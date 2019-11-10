/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.kladr.system;

import static com.bivgroup.ws.kladr.system.Constants.*;
import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.Transaction;
import org.apache.cayenne.query.NamedQuery;
import org.apache.cayenne.query.Query;
import org.apache.log4j.Logger;
import ru.diasoft.services.common.QueryBuilder;
import ru.diasoft.services.common.exception.IDObtainedException;
import ru.diasoft.utils.XMLUtil;
import ru.diasoft.utils.date.DSDateUtil;
import ru.diasoft.utils.exception.XMLUtilException;

/**
 *
 * @author averichevsm
 */
public class KLADRLoader implements Runnable {

    private final Logger logger = Logger.getLogger(this.getClass());

    private final ReportStage reportStage = new ReportStage();
    private final DataContext dc;
    private final String kladrFolderPath;
    private final String login;
    private final String password;
    private final DataSource dataSource;
    private final int MAX_BATCH_SIZE = 1000;
    private volatile int loadCounter = 0;
    private final AtomicBoolean stopFlag = new AtomicBoolean(false);
    private volatile Object operId;
    private volatile int progress = 0;
    
    private static final String[] TEMP_TABLES_DELETE_QUERIES_NAMES = {
        "deleteKladrAltNamesTmp",
        "deleteKladrHouseTmp",
        "deleteKladrObjTmp",
        "deleteKladrSocrBaseTmp",
        "deleteKladrSocrStreetTmp"
    };

    //private static final String KLADRALTNAME_TABLE_NAME = "CORE_KLADRALTNAME";
    //private static final String KLADROBJ_TABLE_NAME = "CORE_KLADROBJ";
    //private static final String KLADRSOCR_TABLE_NAME = "CORE_KLADRSOCR";
    //private static final String KLADRHOUSE_TABLE_NAME = "CORE_KLADRHOUSE";
    //private static final String KLADRSTREET_TABLE_NAME = "CORE_KLADRSTREET";
    private static final String KLADRALTNAME_TABLE_NAME = "CORE_KLADRALTNAMETMP";
    private static final String KLADROBJ_TABLE_NAME = "CORE_KLADROBJTMP";
    private static final String KLADRSOCR_TABLE_NAME = "CORE_KLADRSOCRTMP";
    private static final String KLADRHOUSE_TABLE_NAME = "CORE_KLADRHOUSETMP";
    private static final String KLADRSTREET_TABLE_NAME = "CORE_KLADRSTREETTMP";

    private static final String KLADRALTNAME_INSERT_SQL
                                = "INSERT INTO " + KLADRALTNAME_TABLE_NAME
                                  + "(KLADRALTNAMEID, OLDCODE, NEWCODE, OBJLEVEL)"
                                  + "VALUES(?, ?, ?, ?)";
    private static final String KLADROBJ_INSERT_SQL
                                = "INSERT INTO " + KLADROBJ_TABLE_NAME
                                  + "(KLADROBJID, FULLNAME, SHORTNAME, CODE, POSTALCODE, GNICODE, UNO, OKATO, STATUS)"
                                  + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String KLADROBJ_UPDATE_SQL
                                = "UPDATE " + KLADROBJ_TABLE_NAME + " SET "
                                  + "FULLNAME = ?, SHORTNAME = ?, CODE = ?, POSTALCODE = ?, GNICODE = ?, UNO = ?, CODECUSTOM = ?, STATUS = ?, OKATO = ?"
                                  + " WHERE KLADROBJID = ?";
    private static final String KLADRSOCR_INSERT_SQL
                                = "INSERT INTO " + KLADRSOCR_TABLE_NAME
                                  + "(KLADRSOCRID, OBJLEVEL, SHORTNAME, FULLNAME, TYPECODE)"
                                  + "VALUES(?, ?, ?, ?, ?)";
    private static final String KLADRHOUSE_INSERT_SQL
                                = "INSERT INTO " + KLADRHOUSE_TABLE_NAME
                                  + "(KLADRHOUSEID, HOUSENUMBER, BUILDING, SHORTNAME, CODE, POSTALCODE, GNICODE, UNO, OKATO)"
                                  + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String KLADRHOUSE_UPDATE_SQL
                                = "UPDATE " + KLADRHOUSE_TABLE_NAME + " SET "
                                  + "HOUSENUMBER = ?, SHORTNAME = ?, CODE = ?, POSTALCODE = ?, GNICODE = ?, UNO = ?, CODECUSTOM = ?, OKATO = ?"
                                  + " WHERE KLADRHOUSEID = ?";
    private static final String KLADRSTREET_INSERT_SQL
                                = "INSERT INTO " + KLADRSTREET_TABLE_NAME
                                  + "(KLADRSTREETID, FULLNAME, SHORTNAME, CODE, POSTALCODE, GNICODE, UNO, OKATO)"
                                  + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String KLADRSTREET_UPDATE_SQL
                                = "UPDATE " + KLADRSTREET_TABLE_NAME + " SET "
                                  + "FULLNAME = ?, SHORTNAME = ?, CODE = ?, POSTALCODE = ?, GNICODE = ?, UNO = ?, CODECUSTOM = ?, STATUS = ?, OKATO = ?"
                                  + " WHERE KLADRSTREETID = ?";
    
    private static final String SELECT_KLADR_OBJ_FOR_INSERT_SQL
                                = ""
                                  + "SELECT KLADROBJID, FULLNAME, SHORTNAME, CODE, POSTALCODE, GNICODE, UNO, OKATO, STATUS, "
                                  // Город федерального значения – устанавливается для 1 уровня классификатора (Москва, Санкт-Петербург, Севастополь, Байконур)
                                  + "  0 AS ISFEDERALCITY,"
                                  // Без улицы – устанавливается в случае наличия в справочнике «Дома» позиций с кодом улицы «0000»
                                  + "  ( "
                                  + "  CASE "
                                  + "    WHEN (EXISTS "
                                  + "      (SELECT * "
                                  + "      FROM CORE_KLADRHOUSE h "
                                  + "      WHERE ( h.CODE LIKE CONCAT((SUBSTR(o.CODE,1,11)), '0000____') ) "
                                  + "      )) "
                                  + "    THEN 1 "
                                  + "    ELSE 0 "
                                  + "  END) AS ISNOSTREET, "
                                  // Контекстное наименование – обратное наименование вышестоящих уровней классификатора до уровня 1 (не включительно) с сокращением административной единицы через пробел
                                  + "CASE "
                                  + "    WHEN (o.CODE LIKE '__00000000000') "
                                  + "    THEN NULL "
                                  + "    ELSE CONCAT(CONCAT(CONCAT(o.FULLNAME, CONCAT(' ', o.SHORTNAME)), "
                                  + "      (SELECT CONCAT(', ', CONCAT(FULLNAME, CONCAT(' ', SHORTNAME))) "
                                  + "      FROM CORE_KLADROBJTMP "
                                  + "      WHERE (o.CODE NOT LIKE '________00000') "
                                  + "      AND KLADROBJID = "
                                  + "        (SELECT MAX(oo.KLADROBJID) "
                                  + "        FROM CORE_KLADROBJTMP oo "
                                  + "        WHERE (oo.CODE LIKE '___________00') "
                                  + "        AND (oo.CODE NOT LIKE '_____00000000') "
                                  + "        AND (oo.CODE NOT LIKE '__00000000000') "
                                  + "        AND (oo.CODE = CONCAT((SUBSTR(o.CODE,1,8)), '00000')) "
                                  + "        ) "
                                  + "      )), "
                                  + "      (SELECT CONCAT(', ', CONCAT(FULLNAME, CONCAT(' ', SHORTNAME))) "
                                  + "      FROM CORE_KLADROBJTMP "
                                  + "      WHERE (o.CODE NOT LIKE '_____00000000') "
                                  + "      AND KLADROBJID = "
                                  + "        (SELECT MAX(ooo.KLADROBJID) "
                                  + "        FROM CORE_KLADROBJTMP ooo "
                                  + "        WHERE (ooo.CODE LIKE '_____00000000') "
                                  + "        AND (ooo.CODE NOT LIKE '__00000000000') "
                                  + "        AND (ooo.CODE = CONCAT((SUBSTR(o.CODE,1,5)), '00000000')) "
                                  + "        ) "
                                  + "      )) "
                                  + "  END AS CONTEXTNAME"
                                  + " "
                                  + "FROM CORE_KLADROBJTMP o"
                                  + "";
    
    private static final String UPDATE_KLADR_OBJ_FEDERAL_CITIES_SQL
                                = "UPDATE CORE_KLADROBJ SET "
                                  // Город федерального значения – устанавливается для 1 уровня классификатора (Москва, Санкт-Петербург, Севастополь, Байконур)
                                  + "ISFEDERALCITY = 1, "
                                  // Контекстное наименование – устанавливается обратное наименование вышестоящих уровней классификатора до уровня 1 (не включительно) с сокращением административной единицы через пробел.
                                  // Данный атрибут определяется только 3 и 4 уровней классификатора и 1 уровня классификатора (Москва, Санкт-Петербург, Севастополь, Байконур)
                                  + "CONTEXTNAME = CONCAT(FULLNAME, CONCAT(' ', SHORTNAME)) "
                                  + "WHERE SHORTNAME = 'г' AND CODE LIKE '__00000000000'";

    private static final String COPY_KLADR_TABLES_SQL
                                = ""
                                  /* */
                                  // CORE_KLADRSOCR
                                  + "DELETE CORE_KLADRSOCR;"
                                  + "INSERT INTO CORE_KLADRSOCR"
                                  + " (KLADRSOCRID, OBJLEVEL, SHORTNAME, FULLNAME, TYPECODE) "
                                  + "SELECT"
                                  + "  KLADRSOCRID, OBJLEVEL, SHORTNAME, FULLNAME, TYPECODE "
                                  + "FROM CORE_KLADRSOCRTMP;"
                                  // CORE_KLADRALTNAME
                                  + "DELETE CORE_KLADRALTNAME;"
                                  + "INSERT INTO CORE_KLADRALTNAME"
                                  + " (KLADRALTNAMEID, OLDCODE, NEWCODE, OBJLEVEL) "
                                  + "SELECT"
                                  + "  KLADRALTNAMEID, OLDCODE, NEWCODE, OBJLEVEL "
                                  + "FROM CORE_KLADRALTNAMETMP;"
                                  /* */
                            // CORE_KLADRHOUSE
                            + "DELETE CORE_KLADRHOUSE;"
                            + "INSERT INTO CORE_KLADRHOUSE"
                            + " (KLADRHOUSEID, HOUSENUMBER, BUILDING, SHORTNAME, CODE, POSTALCODE, GNICODE, UNO, OKATO) "
                            + "SELECT"
                            + "  KLADRHOUSEID, HOUSENUMBER, BUILDING, SHORTNAME, CODE, POSTALCODE, GNICODE, UNO, OKATO "
                            + "FROM CORE_KLADRHOUSETMP;"
                            /* */
                                  // CORE_KLADROBJ
                                  + "DELETE CORE_KLADROBJ;"
                                  + "INSERT INTO CORE_KLADROBJ"
                                  + " (KLADROBJID, FULLNAME, SHORTNAME, CODE, POSTALCODE, GNICODE, UNO, OKATO, STATUS, ISFEDERALCITY, ISNOSTREET, CONTEXTNAME) "
                                  + SELECT_KLADR_OBJ_FOR_INSERT_SQL + ";"
                                  + UPDATE_KLADR_OBJ_FEDERAL_CITIES_SQL + ";"
                                  // CORE_KLADRSTREET
                                  /* */
                                  + "DELETE CORE_KLADRSTREET;"
                                  + "INSERT INTO CORE_KLADRSTREET"
                                  + " (KLADRSTREETID, FULLNAME, SHORTNAME, CODE, POSTALCODE, GNICODE, UNO, OKATO) "
                                  + "SELECT"
                                  + "  KLADRSTREETID, FULLNAME, SHORTNAME, CODE, POSTALCODE, GNICODE, UNO, OKATO "
                                  + "FROM CORE_KLADRSTREETTMP;"
                                  + "";
    
    private final String MASS_OPER_TYPE_SYSNAME = "loadKladr";
    
    private static final long INSERT_LOGGING_INTERVAL_MINUTES = 1;
    private static final long INSERT_LOGGING_INTERVAL_NS = INSERT_LOGGING_INTERVAL_MINUTES * 60 * 1000000000;
                
    public KLADRLoader(DataContext dc, String kladrFolderPath, String login, String password, DataSource dataSource) {
        super();
        this.dc = dc;
        this.kladrFolderPath = kladrFolderPath;
        this.login = login;
        this.password = password;
        this.dataSource = dataSource;
    }

    public int getProgress() {
        return progress;
    }

    public int getLoadCounter() {
        return loadCounter;
    }

    public List<Map<String, Object>> getProtocol() {
        return reportStage.getProtocol();
    }

    @Override
    public void run() {
        
        logger.info("KLADR loading thread main method started...");
        
        // Код результата выполнения загрузки КЛАДР для записи в истории загрузки КЛАДР (таблица в БД - CORE_KLADRINFO)
        int operationResult = 1;
        // ИД текущей операции загрузки в таблице истории длительных операций (таблица в БД - CORE_MASSOPER)
        operId = null;
        
        try {
            logger.info("KLADR loading initialization is started...");
            
            // очистка локальной оперативную истории загрузки
            reportStage.clearHistory();
            // обнуление счетчика загруженных записей
            loadCounter = 0;
            // обнуление счетчика прогресса загрузки (текущий процент выполнения загрузки - 0%)
            progress = 0;
            
            // обновление записи в истории загрузки КЛАДР (таблица в БД - CORE_KLADRINFO)
            Transaction.bindThreadTransaction(null);
            getCurrentTx().begin();
            addToKladrLoadingHistory(dc, 0, "START");
            getCurrentTx().commit();
            
            logger.info("KLADR loading initialization is finished...");
            
            logger.info("KLADR deleting is started...");
            // обновление записи в истории длительных операций (таблица в БД - CORE_KLADRINFO)
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("OPERNAME", kladrFolderPath);
            params.put("MASSOPERTYPE", MASS_OPER_TYPE_SYSNAME);
            XMLUtil util = new XMLUtil(login, password);
            Map<String, Object> result = util.callService("[corews]", "dsstartmassoper", params);
            logger.debug("dsstartmassoper: " + result);
            
            // ИД текущей операции загрузки в таблице истории длительных операций (таблица в БД - CORE_MASSOPER)
            operId = result.get("MASSOPERID");
            
            // добавление записи в локальную оперативную историю загрузки
            reportStage.addStep("Очистка базы", "Выполняется");
            
            Transaction.bindThreadTransaction(null);
            getCurrentTx().begin();
            
            // обновление записи в истории загрузки КЛАДР (таблица в БД - CORE_KLADRINFO)
            addToKladrLoadingHistory(dc, 0, "PROCESSING");
            dc.commitChanges();
            
            getCurrentTx().commit();
            Transaction.bindThreadTransaction(null);
            
            if (stopFlag.get()) {
                logger.info("KLADR loading process canceled.");
                return;
            }
            
            clearTables(dc);
            
            if (stopFlag.get()) {
                logger.info("KLADR loading process canceled.");
                return;
            }
            
            // добавление записи в локальную оперативную историю загрузки
            logger.info("KLADR deleting is finished...");
            reportStage.addStep("Очистка базы", "Завершено");
            
            // установка счетчика прогресса загрузки (текущий процент выполнения загрузки - 10%)
            progress = 10;
            // добавление записи в локальную оперативную историю загрузки
            logger.info("KLADR loading is started...");
            reportStage.addStep("Загрузка основных объектов", "Выполняется");
            
            loadAltnamesFile(kladrFolderPath);
            
            if (stopFlag.get()) {
                logger.info("KLADR loading process canceled.");
                return;
            }
            
            // установка счетчика прогресса загрузки (текущий процент выполнения загрузки - 30%)
            progress = 30;
            
            loadKladrFile(dc, kladrFolderPath);
            
            if (stopFlag.get()) {
                logger.info("KLADR loading process canceled.");
                return;
            }
            
            // установка счетчика прогресса загрузки (текущий процент выполнения загрузки - 40%)
            progress = 40;
            // добавление записи в локальную оперативную историю загрузки
            reportStage.addStep("Загрузка основных объектов", "Завершено");
            
            loadFlatFile(dc, kladrFolderPath); // не реализовано
            
            if (stopFlag.get()) {
                logger.info("KLADR loading process canceled.");
                return;
            }
            
            // установка счетчика прогресса загрузки (текущий процент выполнения загрузки - 50%)
            progress = 50;
            // добавление записи в локальную оперативную историю загрузки
            reportStage.addStep("Загрузка справочников домов", "Выполняется");
            
            loadHouseFile(dc, kladrFolderPath);
            
            if (stopFlag.get()) {
                logger.info("KLADR loading process canceled.");
                return;
            }
            
            // установка счетчика прогресса загрузки (текущий процент выполнения загрузки - 70%)
            progress = 70;
            // добавление записи в локальную оперативную историю загрузки
            reportStage.addStep("Загрузка справочников домов", "Завершено");
            reportStage.addStep("Загрузка справочников сокращений", "Выполняется");
            
            loadSocrBaseFile(dc, kladrFolderPath);
            
            if (stopFlag.get()) {
                logger.info("KLADR loading process canceled.");
                return;
            }
            
            // установка счетчика прогресса загрузки (текущий процент выполнения загрузки - 80%)
            progress = 80;
            // добавление записи в локальную оперативную историю загрузки
            reportStage.addStep("Загрузка справочников сокращений", "Завершено");
            reportStage.addStep("Загрузка справочников улиц", "Выполняется");
            
            loadStreetFile(dc, kladrFolderPath);
            
            if (stopFlag.get()) {
                logger.info("KLADR loading process canceled.");
                return;
            }
            
            // установка счетчика прогресса загрузки (текущий процент выполнения загрузки - 90%)
            progress = 90;
            
            copyAllDataToRealTables(dc);
            
            if (stopFlag.get()) {
                logger.info("KLADR loading process canceled.");
                return;
            }

            // установка счетчика прогресса загрузки (текущий процент выполнения загрузки - 90%)
            progress = 100;
            
            // добавление записи в локальную оперативную историю загрузки
            logger.info("KLADR loading is finished...");
            reportStage.addStep("Загрузка КЛАДР", "Завершена");
            reportStage.addResult("Загружено " + loadCounter + " записей");
            
            // Код результата выполнения загрузки КЛАДР для записи в истории загрузки КЛАДР (таблица в БД - CORE_KLADRINFO)
            operationResult = 0;
            
            // обновление записи в истории длительных операций (таблица в БД - CORE_KLADRINFO)
            result.put("STATUS", "OK");
            result.put("DETAIL", "Загружено " + loadCounter + " записей");
            result.put("MASSOPERID", operId);
            util.callServiceAsync("[corews]", "dsfinishmassoper", result);
            
        } catch (Exception e) {
            // добавление записи в локальную оперативную историю загрузки
            //reportStage.addResult(e.getLocalizedMessage());
            logException(e);
            
            // параметры для обновления записи в истории длительных операций (таблица в БД - CORE_KLADRINFO)
            HashMap<String, Object> errResult = new HashMap<String, Object>();
            errResult.put("STATUS", "OK");
            errResult.put("DETAIL", "Загружено " + loadCounter + " записей");
            errResult.put("MASSOPERID", operId);
            try {
                // откат транзакции
                dc.rollbackChanges();
                Transaction threadTransaction = Transaction.getThreadTransaction();
                if (threadTransaction != null) {
                    threadTransaction.setRollbackOnly();
                }
                
                // обновление записи в истории длительных операций (таблица в БД - CORE_KLADRINFO)
                XMLUtil util = new XMLUtil(login, password, true, true);
                util.callService("[corews]", "dsfinishmassoper", errResult);
                
            } catch (Exception e1) {
                logger.error("Error while rolling back transaction!", e1);
            }
            if (e instanceof FileNotFoundException) {
                logger.error("KLADR loading error... File not found. (" + e.getMessage() + ")", e);
            } else {
                logger.error("KLADR loading error... (" + e.getMessage() + ")", e);
            }
        } finally {
            try {
                if (Transaction.getThreadTransaction() != null &&
                        Transaction.getThreadTransaction().getStatus() == Transaction.STATUS_MARKED_ROLLEDBACK) {
                    logger.info("In rollback");
                    dc.rollbackChanges();
                    Transaction.getThreadTransaction().rollback();
                }
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
            Transaction.bindThreadTransaction(null);
            try {
                getCurrentTx().setStatus(Transaction.STATUS_NO_TRANSACTION);
                getCurrentTx().begin();
                
                // обновление записи в истории загрузки КЛАДР (таблица в БД - CORE_KLADRINFO)
                addToKladrLoadingHistory(dc, operationResult, "DONE");
                
                dc.commitChanges();
                getCurrentTx().commit();
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
            }
        }
        logger.info("KLADR loading thread main method finished.");
    }

    private Transaction getCurrentTx() throws Exception {
        if (Transaction.getThreadTransaction() == null) {
            Transaction.bindThreadTransaction(Transaction.internalTransaction(null));
        }
        return Transaction.getThreadTransaction();
    }
    
    private void close(Connection connection) throws SQLException {
        if (!connection.getAutoCommit()) {
            connection.rollback();
        }
        connection.close();
    }
    
    // обновление записи в истории загрузки КЛАДР (таблица в БД - CORE_KLADRINFO)
    private void addToKladrLoadingHistory(DataContext dc, int operationResult, String status) throws IDObtainedException {
        
        logger.debug("KLADR loading history updating...");
        
        // если загрузка завершена - удаление из CORE_KLADRINFO всех записей о текущей обработке (по условию STATUS = 'PROCESSING')
        Map<String, Object> queryParams = new HashMap<String, Object>();
        if ("DONE".equals(status)) {
            dc.performNonSelectingQuery("fixKladrInfo", queryParams);
        }
        
        // добавление в CORE_KLADRINFO записи о загрузке КЛАДР
        queryParams = new HashMap<String, Object>();
        // генерация нового ИД
        long infoId = QueryBuilder.getNewIdLong("CORE_KLADRINFO", 1);
        // атрибуты записи
        queryParams.put("KLADRINFOID", infoId);
        queryParams.put("LOADDATE", DSDateUtil.convertDate(new Date()));
        queryParams.put("LOADERROR", operationResult);
        queryParams.put("STATUS", status);
        queryParams.put("RECORDS", loadCounter);
        logger.debug("To KLADR loading history will be added info: " + queryParams);
        dc.performNonSelectingQuery("insertKladrInfo", queryParams);
        
        logger.debug("KLADR loading history updating finished.");
        
    }

    private void clearTables(DataContext dc) throws Exception {
        for (String deleteQueryName : TEMP_TABLES_DELETE_QUERIES_NAMES) {           
            
            long operationStartTime = System.nanoTime();
            logger.debug(String.format("  Performing deleting query '%s'...", deleteQueryName));
            
            Transaction.bindThreadTransaction(null);
            getCurrentTx().begin();
            
            int[] deleteQueryResult = dc.performNonSelectingQuery(deleteQueryName);
            
            dc.commitChanges();
            getCurrentTx().commit();
            Transaction.bindThreadTransaction(null);
            
            reportDeletingTimeDelta(String.format("  Query '%s' performed", deleteQueryName), operationStartTime, deleteQueryResult);
            
            if (stopFlag.get()) {
                return;
            }
            
        }
        
    }

    //private void reportTime(String stage, long nsTime, long counter) {
    //    double dt = nsTime / 1000000;
    //    if (dt > 0) {
    //        double ips = 1000.0 * counter / dt;
    //        logger.info(stage + ": Inserted " + counter + " records at " + ips + " inserts per second");
    //    } else {
    //        logger.warn(stage + ": completed too fast.");
    //    }
    //}

    private long reportDeletingTimeDelta(String stageName, long operationStartTime, int[] deleteQueryResult) {
        long totalRowsCount = 0;
        for (int rowCnt : deleteQueryResult) {
            totalRowsCount += rowCnt;
        }
        reportDeletingTimeDelta(stageName, operationStartTime, totalRowsCount);
        return totalRowsCount;
    }
    
    private void reportDeletingTimeDelta(String stageName, long operationStartTime, long rowsCount) {
        reportTimeDelta(stageName, "deleted", operationStartTime, rowsCount);
    }

    private void reportInsertingTimeDelta(String stageName, long operationStartTime, long rowsCount) {
        reportTimeDelta(stageName, "inserted", operationStartTime, rowsCount);
    }
    
    private void reportSelectingTimeDelta(String stageName, long operationStartTime, long rowsCount) {
        reportTimeDelta(stageName, "selected", operationStartTime, rowsCount);
    }
    
    private void reportTimeDelta(String stageName, String operationName, long operationStartTime, long rowsCount) {
        long nowTime = System.nanoTime();
        long operationTime = nowTime - operationStartTime;
        double operationTimeInSeconds = ((double) operationTime) / 1000000000.0;
        if (operationTimeInSeconds > 0) {
            if (rowsCount > 0) {
                double recordsPerSecond = ((rowsCount * 1000000000.0) / ((double) operationTime));
                logger.debug(String.format("%s: %d records %s in %d nanoseconds (approximately %.5f seconds) at %.5f records per second rate.", stageName, rowsCount, operationName, operationTime, operationTimeInSeconds, recordsPerSecond));
            } else {
                logger.debug(String.format("%s: %d records %s in %d nanoseconds (approximately %.5f seconds).", stageName, rowsCount, operationName, operationTime, operationTimeInSeconds));
            }
        } else {
            logger.debug(String.format("%s: %d records %s almost instantly.", stageName, rowsCount, operationName));
        }        
    }

    private InputStream getFileInputStream(String folder, String fileName) throws Exception {
        String fullFileName = folder + fileName;
        logger.debug(String.format("Opening file '%s'...", fullFileName));
        File file = new File(fullFileName);
        InputStream stream = new FileInputStream(file);
        logger.debug("File opened, input stream ready.");
        return stream;
    }
    
    private DBFReader getDBFReader(InputStream stream) throws DBFException {
        logger.debug("Getting DBFReader from opened file input stream...");
        DBFReader reader = new DBFReader(stream);
        reader.setCharactersetName("Cp866");
        logger.debug("DBFReader ready.");
        return reader;
    }
    
    private int getDBFReaderRecordCount(DBFReader reader) {
        int recordCount = reader.getRecordCount();
        logger.debug("DBFReader reports total records count = " + recordCount);
        return recordCount;
    }

    private void loadAltnamesFile(String folder) throws Exception {
        logger.info("AltNames loading is started...");
        
        //String fullFileName = folder + ALTNAMES_FILE;
        //File file = new File(fullFileName);
        //InputStream stream = new FileInputStream(file);
        InputStream stream = getFileInputStream(folder, ALTNAMES_FILE);

        
        long insertCounter = 0;
        try {
            DBFReader reader = getDBFReader(stream);
            int totalRecordsCount = getDBFReaderRecordCount(reader);

            //Map<String, Object> queryParams = new HashMap<String, Object>();
            long id = 1;
            long counter = 0;
            Object[] selectedRow;
            Connection connection = dataSource.getConnection();
            try {
                connection.setAutoCommit(false);

                PreparedStatement ps = connection.prepareStatement(KLADRALTNAME_INSERT_SQL);
                logger.info("AltNames data inserting started...");
                long insertingStartTime = System.nanoTime();
                long lastLoggingTime = insertingStartTime;
                while ((selectedRow = reader.nextRecord()) != null) {
                    ps.setLong(1, id++);
                    ps.setString(2, selectedRow[0] != null ? selectedRow[0].toString().trim() : "");
                    ps.setString(3, selectedRow[1] != null ? selectedRow[1].toString().trim() : "");
                    ps.setString(4, selectedRow[2] != null ? selectedRow[2].toString().trim() : "");
                    ps.addBatch();

                    counter++;
                    loadCounter++;
                    insertCounter++;
                    if (counter >= MAX_BATCH_SIZE) {
                        counter = 0;
                        ps.executeBatch();
                        ps.clearBatch();
                        connection.commit();
                        
                        lastLoggingTime = reportInsertedRowsCount(lastLoggingTime, insertCounter, totalRecordsCount);
                        
                    }
                    if (stopFlag.get()) {
                        break;
                    }
                }
                ps.executeBatch();
                connection.commit();
                reportInsertedRowsCount(insertCounter, totalRecordsCount);
                logger.info("AltNames data inserting finished.");
                
                //reportTime("AltNames", System.nanoTime() - t0, insertCounter);
                reportInsertingTimeDelta("AltNames", insertingStartTime, insertCounter);
                
                ps.close();
            } finally {
                close(connection);
            }
        } finally {
            stream.close();
        }

        
        logger.info("AltNames loading is finished...");
    }

    private void loadKladrFile(DataContext dc, String folder) throws Exception {
        logger.info("KladrObjects loading is started...");
        
        //String fullFileName = folder + KLADR_FILE;
        //File file = new File(fullFileName);
        //InputStream stream = new FileInputStream(file);
        InputStream stream = getFileInputStream(folder, KLADR_FILE);

        try {
            DBFReader reader = getDBFReader(stream);
            int totalRecordsCount = getDBFReaderRecordCount(reader);

            Connection connection = dataSource.getConnection();
            try {
                connection.setAutoCommit(false);

                PreparedStatement ps = connection.prepareStatement(KLADROBJ_INSERT_SQL);

                long id = 1;
                long counter = 0;

                Map userCustomObjects = getCustomObjects(dc);
                Set<Long> customIndexes = new HashSet<Long>();
                for (Object key : userCustomObjects.keySet()) {
                    Map obj = (Map) userCustomObjects.get(key);
                    customIndexes.add(Long.parseLong(obj.get("KLADROBJID").toString()));
                }
                // Collect updates in list and apply after insert phase
                List<Map> updateList = new ArrayList<Map>();

                long insertCounter = 0;
                Object[] selectedRow;
                logger.info("KladrObjects data inserting started...");
                long insertingStartTime = System.nanoTime();
                long lastLoggingTime = insertingStartTime;
                while ((selectedRow = reader.nextRecord()) != null) {
                    if (customIndexes.contains(id)) {
                        id++;
                        continue;
                    }
                    String fullName = selectedRow[0] != null ? selectedRow[0].toString().trim() : "";
                    Map obj = (Map) userCustomObjects.get(fullName);
                    if (obj != null) {
                        if (obj.get("CODE").equals(selectedRow[2])) {
                            continue; //уже обновлена
                        }
                        // Update map in-place
                        //queryParams.put("KLADROBJID", obj.get("KLADROBJID"));
                        //obj.put("FULLNAME", selectedRow[0] != null ? selectedRow[0].toString().trim() : "");
                        obj.put("SHORTNAME", selectedRow[1] != null ? selectedRow[1].toString().trim() : "");
                        obj.put("CODE", selectedRow[2] != null ? selectedRow[2].toString().trim() : "");
                        obj.put("POSTALCODE", selectedRow[3] != null ? selectedRow[3].toString().trim() : "");
                        obj.put("GNICODE", selectedRow[4] != null ? selectedRow[4].toString().trim() : "");
                        obj.put("UNO", selectedRow[5] != null ? selectedRow[5].toString().trim() : "");
                        obj.put("OKATO", selectedRow[6] != null ? selectedRow[6].toString().trim() : "");
                        obj.put("STATUS", selectedRow[7] != null ? selectedRow[7].toString().trim() : "");

                        updateList.add(obj);
                    } else {
                        String shortName = selectedRow[1] != null ? selectedRow[1].toString().trim() : "";
                        String code = selectedRow[2] != null ? selectedRow[2].toString().trim() : "";
                        String postalCode = selectedRow[3] != null ? selectedRow[3].toString().trim() : "";
                        String gniCode = selectedRow[4] != null ? selectedRow[4].toString().trim() : "";
                        String uno = selectedRow[5] != null ? selectedRow[5].toString().trim() : "";
                        String okato = selectedRow[6] != null ? selectedRow[6].toString().trim() : "";
                        String status = selectedRow[7] != null ? selectedRow[7].toString().trim() : "";

                        ps.setLong(1, id++);
                        ps.setString(2, fullName);
                        ps.setString(3, shortName);
                        ps.setString(4, code);
                        ps.setString(5, postalCode);
                        ps.setString(6, gniCode);
                        ps.setString(7, uno);
                        ps.setString(8, okato);
                        ps.setString(9, status);
                        ps.addBatch();
                        insertCounter++;
                    }
                    counter++;
                    loadCounter++;
                    if (counter >= MAX_BATCH_SIZE) {
                        ps.executeBatch();
                        ps.clearBatch();
                        connection.commit();
                        counter = 0;
                        
                        lastLoggingTime = reportInsertedRowsCount(lastLoggingTime, insertCounter, totalRecordsCount);
                        
                    }
                    if (stopFlag.get()) {
                        break;
                    }
                }
                ps.executeBatch();
                connection.commit();
                reportInsertedRowsCount(insertCounter, totalRecordsCount);
                logger.info("KladrObjects data inserting finished.");
                
                //reportTime("KladrObjects", System.nanoTime() - t0, insertCounter);
                reportInsertingTimeDelta("KladrObjects", insertingStartTime, insertCounter);
                
                ps.close();

                //long t1 = System.nanoTime();

                // Update rows
                if (!updateList.isEmpty()) {
                    ps = connection.prepareStatement(KLADROBJ_UPDATE_SQL);
                    counter = 0;
                    for (Map row : updateList) {
                        ps.setObject(1, row.get("FULLNAME"));
                        ps.setObject(2, row.get("SHORTNAME"));
                        ps.setObject(3, row.get("CODE"));

                        ps.setObject(4, row.get("POSTALCODE"));
                        ps.setObject(5, row.get("GNICODE"));
                        ps.setObject(6, row.get("UNO"));

                        ps.setObject(7, row.get("CODECUSTOM"));
                        ps.setObject(8, row.get("STATUS"));
                        ps.setObject(9, row.get("OKATO"));

                        ps.setObject(10, row.get("KLADROBJID"));
                        ps.addBatch();
                        counter++;
                        if (counter >= MAX_BATCH_SIZE) {
                            counter = 0;
                            ps.executeBatch();
                            ps.clearBatch();
                            connection.commit();
                        }
                        if (stopFlag.get()) {
                            break;
                        }
                    }
                    ps.executeBatch();
                    connection.commit();
                    ps.close();
                }
            } finally {
                close(connection);
            }
        } finally {
            stream.close();
        }
        logger.info("KladrObjects loading is finished...");
    }

    private void loadFlatFile(DataContext dc, String folder) throws Exception {
        // do nothing
    }

    private void loadSocrBaseFile(DataContext dc, String folder) throws Exception {
        logger.info("SocrBase loading is started...");
        
        //String fullFileName = folder + SOCRBASE_FILE;
        //File file = new File(fullFileName);
        //InputStream stream = new FileInputStream(file);
        InputStream stream = getFileInputStream(folder, SOCRBASE_FILE);
        
        try {
            DBFReader reader = getDBFReader(stream);
            int totalRecordsCount = getDBFReaderRecordCount(reader);

            Connection connection = dataSource.getConnection();
            try {
                connection.setAutoCommit(false);

                PreparedStatement ps = connection.prepareStatement(KLADRSOCR_INSERT_SQL);

                long id = 1;
                long counter = 0;
                Object[] selectedRow;
                logger.info("SocrBase data inserting started...");
                long insertingStartTime = System.nanoTime();
                long lastLoggingTime = insertingStartTime;
                while ((selectedRow = reader.nextRecord()) != null) {
                    String level = selectedRow[0] != null ? selectedRow[0].toString().trim() : "";
                    String shortName = selectedRow[1] != null ? selectedRow[1].toString().trim() : "";
                    String fullName = selectedRow[2] != null ? selectedRow[2].toString().trim() : "";
                    String typeCode = selectedRow[3] != null ? selectedRow[3].toString().trim() : "";

                    ps.setLong(1, id++);
                    ps.setString(2, level);
                    ps.setString(3, shortName);
                    ps.setString(4, fullName);
                    ps.setString(5, typeCode);
                    ps.addBatch();

                    counter++;
                    loadCounter++;
                    if (counter % MAX_BATCH_SIZE == 0) {
                        ps.executeBatch();
                        ps.clearBatch();
                        connection.commit();

                        lastLoggingTime = reportInsertedRowsCount(lastLoggingTime, counter, totalRecordsCount);
                        
                    }
                    if (stopFlag.get()) {
                        break;
                    }
                }
                ps.executeBatch();
                connection.commit();
                reportInsertedRowsCount(counter, totalRecordsCount);
                logger.info("SocrBase data inserting finished.");
                
                //reportTime("SocrBase", System.nanoTime() - t0, counter);
                reportInsertingTimeDelta("SocrBase", insertingStartTime, counter);
                
                ps.close();

            } finally {
                close(connection);
            }
        } finally {
            stream.close();
        }
        logger.info("SocrBase loading is finished...");
    }

    private void loadHouseFile(DataContext dc, String folder) throws Exception {
        logger.info("Houses loading is started...");
        
        //String fullFileName = folder + HOUSE_FILE;
        //File file = new File(fullFileName);
        //InputStream stream = new FileInputStream(file);
        InputStream stream = getFileInputStream(folder, HOUSE_FILE);
        
        try {
            DBFReader reader = getDBFReader(stream);
            int totalRecordsCount = getDBFReaderRecordCount(reader);

            Connection connection = dataSource.getConnection();
            try {
                connection.setAutoCommit(false);

                PreparedStatement ps = connection.prepareStatement(KLADRHOUSE_INSERT_SQL);

                Map<String, Object> queryParams = new HashMap<String, Object>();
                long id = 1;
                long counter = 0;
                long insertCounter = 0;
                Object[] selectedRow;
                getCurrentTx().setStatus(Transaction.STATUS_NO_TRANSACTION);
                getCurrentTx().begin();
                Map userCustomObjects = getCustomHouses(dc);
                Set<Long> customIndexes = new HashSet<Long>();
                for (Object key : userCustomObjects.keySet()) {
                    Map obj = (Map) userCustomObjects.get(key);
                    customIndexes.add(Long.parseLong(obj.get("KLADRHOUSEID").toString()));
                }
                List<Map> updateList = new ArrayList<Map>();
                logger.info("Houses data inserting started...");
                long insertingStartTime = System.nanoTime();
                long lastLoggingTime = insertingStartTime;
                while ((selectedRow = reader.nextRecord()) != null) {
                    if (customIndexes.contains(id)) {
                        id++;
                        continue;
                    }
                    String houseNumber = selectedRow[0] != null ? selectedRow[0].toString().trim() : "";
                    Map obj = (Map) userCustomObjects.get(houseNumber);
                    if (obj != null) {
                        if (obj.get("CODE").equals(selectedRow[2])) {
                            continue; //уже обновлена
                        }
                        //queryParams.put("KLADRHOUSEID", obj.get("KLADRHOUSEID"));
                        obj.put("HOUSENUMBER", selectedRow[0] != null ? selectedRow[0].toString().trim() : "");
                        obj.put("BUILDING", selectedRow[1] != null ? selectedRow[1].toString().trim() : "");
                        obj.put("SHORTNAME", selectedRow[2] != null ? selectedRow[2].toString().trim() : "");
                        obj.put("CODE", selectedRow[3] != null ? selectedRow[3].toString().trim() : "");
                        obj.put("POSTALCODE", selectedRow[4] != null ? selectedRow[4].toString().trim() : "");
                        obj.put("GNICODE", selectedRow[5] != null ? selectedRow[5].toString().trim() : "");
                        obj.put("UNO", selectedRow[6] != null ? selectedRow[6].toString().trim() : "");
                        obj.put("OKATO", selectedRow[7] != null ? selectedRow[7].toString().trim() : "");

                        updateList.add(obj);
                        //dc.performNonSelectingQuery("updateKladrHouse", queryParams);
                    } else {
                        String building = selectedRow[1] != null ? selectedRow[1].toString().trim() : "";
                        String shortName = selectedRow[2] != null ? selectedRow[2].toString().trim() : "";
                        String code = selectedRow[3] != null ? selectedRow[3].toString().trim() : "";
                        String postalCode = selectedRow[4] != null ? selectedRow[4].toString().trim() : "";
                        String gniCode = selectedRow[5] != null ? selectedRow[5].toString().trim() : "";
                        String uno = selectedRow[6] != null ? selectedRow[6].toString().trim() : "";
                        String okato = selectedRow[7] != null ? selectedRow[7].toString().trim() : "";

                        ps.setLong(1, id++);
                        ps.setString(2, houseNumber);
                        ps.setString(3, building);
                        ps.setString(4, shortName);
                        ps.setString(5, code);
                        ps.setString(6, postalCode);
                        ps.setString(7, gniCode);
                        ps.setString(8, uno);
                        ps.setString(9, okato);
                        ps.addBatch();
                        insertCounter++;
                    }
                    counter++;
                    loadCounter++;
                    if (counter >= MAX_BATCH_SIZE) {
                        counter = 0;
                        ps.executeBatch();
                        ps.clearBatch();
                        connection.commit();
                        
                        lastLoggingTime = reportInsertedRowsCount(lastLoggingTime, insertCounter, totalRecordsCount);
                        
                    }
                    if (stopFlag.get()) {
                        break;
                    }
                }
                ps.executeBatch();
                connection.commit();
                reportInsertedRowsCount(insertCounter, totalRecordsCount);
                logger.info("Houses data inserting finished.");
                
                //reportTime("Houses", System.nanoTime() - t0, insertCounter);
                reportInsertingTimeDelta("Houses", insertingStartTime, insertCounter);

                ps.close();

                // Update records
                if (!updateList.isEmpty()) {
                    ps = connection.prepareStatement(KLADRHOUSE_UPDATE_SQL);
                    counter = 0;
                    for (Map row : updateList) {
                        ps.setObject(1, row.get("HOUSENUMBER"));
                        ps.setObject(2, row.get("SHORTNAME"));
                        ps.setObject(3, row.get("CODE"));

                        ps.setObject(4, row.get("POSTALCODE"));
                        ps.setObject(5, row.get("GNICODE"));
                        ps.setObject(6, row.get("UNO"));

                        ps.setObject(7, row.get("CODECUSTOM"));
                        ps.setObject(8, row.get("OKATO"));

                        ps.setObject(9, row.get("KLADRHOUSEID"));
                        ps.addBatch();

                        counter++;
                        if (counter >= MAX_BATCH_SIZE) {
                            counter = 0;
                            ps.executeBatch();
                            ps.clearBatch();
                            connection.commit();
                        }
                        if (stopFlag.get()) {
                            break;
                        }
                    }
                    ps.executeBatch();
                    connection.commit();
                    ps.close();
                }
            } finally {
                close(connection);
            }
        } finally {
            stream.close();
        }

        logger.info("Houses loading is finished...");
    }

    private void reportInsertedRowsCount(long counter, int totalRecordsCount) {
        logger.debug(String.format("Inserted %d rows from total of %d records in the DBF file.", counter, totalRecordsCount));
    }    
    
    private long reportInsertedRowsCount(long lastLoggingTime, long insertCounter, int totalRecordsCount) {
        long nowTime = System.nanoTime();
        if ((nowTime - lastLoggingTime) > INSERT_LOGGING_INTERVAL_NS) {
            reportInsertedRowsCount(insertCounter, totalRecordsCount);
            return nowTime;
        }
        return lastLoggingTime;
    }

    private void loadStreetFile(DataContext dc, String folder) throws Exception {
        logger.info("Streets loading is started...");
        
        //String fullFileName = folder + STREET_FILE;
        //File file = new File(fullFileName);
        //InputStream stream = new FileInputStream(file);
        InputStream stream = getFileInputStream(folder, STREET_FILE);
        
        try {
            DBFReader reader = getDBFReader(stream);
            int totalRecordsCount = getDBFReaderRecordCount(reader);

            Connection connection = dataSource.getConnection();
            try {
                connection.setAutoCommit(false);

                PreparedStatement ps = connection.prepareStatement(KLADRSTREET_INSERT_SQL);

                //Map<String, Object> queryParams = new HashMap<String, Object>();
                long id = 1;
                long counter = 0;
                long insertCounter = 0;
                Map userCustomObjects = getCustomStreets(dc);
                Set<Long> customIndexes = new HashSet<Long>();
                for (Object key : userCustomObjects.keySet()) {
                    Map obj = (Map) userCustomObjects.get(key);
                    customIndexes.add(Long.valueOf(obj.get("KLADRSTREETID").toString()));
                }
                List<Map> updateList = new ArrayList<Map>();
                Object[] selectedRow;
                logger.info("Streets data inserting started...");
                long insertingStartTime = System.nanoTime();
                long lastLoggingTime = insertingStartTime;
                while ((selectedRow = reader.nextRecord()) != null) {
                    if (customIndexes.contains(id)) {
                        id++;
                        continue;
                    }

                    String fullName = selectedRow[0] != null ? selectedRow[0].toString().trim() : "";
                    Map obj = (Map) userCustomObjects.get(fullName);
                    if (obj != null) {
                        if (obj.get("CODE").equals(selectedRow[2])) {
                            continue; //уже обновлена
                        }
                        //queryParams.put("KLADRSTREETID", obj.get("KLADRSTREETID"));
                        //queryParams.put("FULLNAME", selectedRow[0] != null ? selectedRow[0].toString().trim() : "");
                        obj.put("SHORTNAME", selectedRow[1] != null ? selectedRow[1].toString().trim() : "");
                        obj.put("CODE", selectedRow[2] != null ? selectedRow[2].toString().trim() : "");
                        obj.put("POSTALCODE", selectedRow[3] != null ? selectedRow[3].toString().trim() : "");
                        obj.put("GNICODE", selectedRow[4] != null ? selectedRow[4].toString().trim() : "");
                        obj.put("UNO", selectedRow[5] != null ? selectedRow[5].toString().trim() : "");
                        obj.put("OKATO", selectedRow[6] != null ? selectedRow[6].toString().trim() : "");

                        updateList.add(obj);
                    } else {
                        String shortName = selectedRow[1] != null ? selectedRow[1].toString().trim() : "";
                        String code = selectedRow[2] != null ? selectedRow[2].toString().trim() : "";
                        String postalCode = selectedRow[3] != null ? selectedRow[3].toString().trim() : "";
                        String gniCode = selectedRow[4] != null ? selectedRow[4].toString().trim() : "";
                        String uno = selectedRow[5] != null ? selectedRow[5].toString().trim() : "";
                        String okato = selectedRow[6] != null ? selectedRow[6].toString().trim() : "";

                        ps.setLong(1, id++);
                        ps.setString(2, fullName);
                        ps.setString(3, shortName);
                        ps.setString(4, code);
                        ps.setString(5, postalCode);
                        ps.setString(6, gniCode);
                        ps.setString(7, uno);
                        ps.setString(8, okato);
                        ps.addBatch();
                        insertCounter++;
                    }
                    counter++;
                    loadCounter++;
                    if (insertCounter % MAX_BATCH_SIZE == 0) {
                        ps.executeBatch();
                        ps.clearBatch();
                        connection.commit();
                        
                        lastLoggingTime = reportInsertedRowsCount(lastLoggingTime, insertCounter, totalRecordsCount);
                        
                    }
                    if (stopFlag.get()) {
                        break;
                    }
                }
                ps.executeBatch();
                connection.commit();
                reportInsertedRowsCount(insertCounter, totalRecordsCount);
                logger.info("Streets data inserting finished.");
                
                ps.close();
                
                //("Streets", System.nanoTime() - t0, insertCounter);
                reportInsertingTimeDelta("Streets", insertingStartTime, insertCounter);

                if (!updateList.isEmpty()) {
                    ps = connection.prepareStatement(KLADRSTREET_UPDATE_SQL);
                    counter = 0;
                    for (Map row : updateList) {
                        ps.setObject(1, row.get("FULLNAME"));
                        ps.setObject(2, row.get("SHORTNAME"));
                        ps.setObject(3, row.get("CODE"));

                        ps.setObject(4, row.get("POSTALCODE"));
                        ps.setObject(5, row.get("GNICODE"));
                        ps.setObject(6, row.get("UNO"));

                        ps.setObject(7, row.get("CODECUSTOM"));
                        ps.setObject(8, row.get("STATUS"));
                        ps.setObject(9, row.get("OKATO"));

                        ps.setObject(10, row.get(""));
                        ps.addBatch();

                        counter++;
                        if (counter % MAX_BATCH_SIZE == 0) {
                            ps.executeBatch();
                            ps.clearBatch();
                            connection.commit();
                        }
                        if (stopFlag.get()) {
                            break;
                        }
                    }
                    ps.executeBatch();
                    connection.commit();
                    ps.close();
                }
            } finally {
                close(connection);
            }
        } finally {
            stream.close();
        }
        logger.info("Streets loading is finished...");
    }

    //private void updateKladrLoadingHistory() throws Exception {
    //    Map<String, Object> queryParams = new HashMap<String, Object>();
    //    queryParams.put("LOADDATE", XMLUtil.convertDate(new Date()));
    //    queryParams.put("RECORDS", loadCounter);
    //    dc.performNonSelectingQuery("updateKladrInfo", queryParams);
    //}

    private Map<String, Object> getCustomObjects(DataContext dc) {
        //return getCustom(dc, "getCustomObjects");
        return getCustom(dc, "getCustomObjectsTmp");
    }

    private Map getCustomStreets(DataContext dc) {
        //return getCustom(dc, "getCustomStreets");
        return getCustom(dc, "getCustomStreetsTmp");
    }

    private Map getCustomHouses(DataContext dc) {
        //return getCustom(dc, "getCustomHouses");
        return getCustom(dc, "getCustomHousesTmp");
    }

    private Map<String, Object> getCustom(DataContext dc, String queryName) {
        logger.debug(String.format("Performing selecting query '%s'...", queryName));
        long operationStartTime = System.nanoTime();
        Query query = new NamedQuery(queryName);
        Map<String, Object> customObjects = new HashMap<String, Object>();
        try {
            List<Map> result = dc.performQuery(query);
            reportSelectingTimeDelta(String.format("Query '%s' performed", queryName), operationStartTime, result.size());
            if (result.size() > 0) {
                for (Map res : result) {
                    customObjects.put(res.get("FULLNAME").toString(), res);
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        logger.debug(String.format("Query '%s' result analized.", queryName));
        return customObjects;
    }

    public void cancel() throws XMLUtilException {
        
        stopFlag.set(true);

        logger.info("KLADR: Canceling KLADR loading...");

        Map<String, Object> sResult = new HashMap<String, Object>();
        sResult.put("STATUS", "OK");
        sResult.put("DETAIL", "Загружено " + loadCounter + " записей");
        sResult.put("MASSOPERID", operId);
        XMLUtil util = new XMLUtil(login, password, true, true);
        util.callService("[corews]", "dsfinishmassoper", sResult);
        
    }

    private void logException(Exception ex) {
        reportStage.addResult(ex.getLocalizedMessage());
        logger.error(ex);
    }

    private void copyAllDataToRealTables(DataContext dc) throws SQLException {
        logger.info("Copying all loaded KLADR data to real tables started...");
        Connection connection = dataSource.getConnection();
        try {
            connection.setAutoCommit(false);
            Statement ps = connection.createStatement();

            long totalRowsCount = 0;
            long copyStartTime = System.nanoTime();
            String[] batches = COPY_KLADR_TABLES_SQL.split(";");
            for (int i = 0; i < batches.length; i++) {
                logger.debug("Preparing SQL statement batch...");
                String batch = batches[i];
                logger.debug(String.format("Batch %d (from %d): %s", i + 1, batches.length, batch));
                ps.clearBatch();
                ps.addBatch(batch);
                logger.debug("Preparing SQL statement batch finished.");
                logger.debug("Executing batched SQL statement...");
                long batchStartTime = System.nanoTime();
                int[] batchRes = ps.executeBatch();
                int batchRowsCount = batchRes[0];
                logger.debug("Query '" + batch + "' was executed with result (affected rows count) = " + batchRowsCount + ".");
                reportTimeDelta("Statement executed", "affected", batchStartTime, batchRowsCount);
                totalRowsCount += batchRowsCount;
            }
            reportTimeDelta("Coped KLADR data to real tables", "deleted/inserted", copyStartTime, totalRowsCount);
            
            //logger.debug("Preparing SQL statement batches...");
            //String[] batches = COPY_KLADR_TABLES_SQL.split(";");
            //for (int i = 0; i < batches.length; i++) {
            //    String batch = batches[i];
            //    logger.debug("Batch " + i + ": " + batch);
            //    ps.addBatch(batch);
            //}
            //logger.debug("Preparing SQL statement batches finished.");
            //
            //logger.debug("Executing batched SQL statements...");
            //long copyStartTime = System.nanoTime();
            //int[] batchRes = ps.executeBatch();
            //
            //long totalRowsCount = 0;
            //for (int i = 0; i < batchRes.length; i++) {
            //    logger.debug("Query '" + batches[i] + "' was executed with result (affected rows count) = " + batchRes[i] + ".");
            //    totalRowsCount += batchRes[i];
            //}
            //
            //reportTimeDelta("Coped KLADR data to real tables", "deleted/inserted", copyStartTime, totalRowsCount);

            logger.debug("Commiting to DB...");
            connection.commit();
            ps.close();
            logger.debug("Successfully commited to DB.");
            logger.info("All loaded KLADR data successfully coped to real tables.");
        } finally {
            close(connection);
        }
        logger.info("Copying all loaded KLADR data to real tables finished.");
                
    }
    
}