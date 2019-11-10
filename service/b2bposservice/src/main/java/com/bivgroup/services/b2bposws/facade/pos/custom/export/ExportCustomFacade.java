/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.export;

import com.bivgroup.services.b2bposws.facade.B2BFileSessionController;
import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;

import static com.bivgroup.services.b2bposws.facade.B2BFileSessionController.FS_TYPE_PARAMNAME;
import static com.bivgroup.services.b2bposws.facade.B2BFileSessionController.SOME_ID_PARAMNAME;
import static com.bivgroup.services.b2bposws.facade.B2BFileSessionController.USER_DOCNAME_PARAMNAME;
import static com.bivgroup.services.b2bposws.facade.pos.custom.export.CSVExportCustomFacade.SERVICE_NAME;
import static com.bivgroup.services.b2bposws.system.files.BinFileType.EXPORT_DATA_FILE;

import com.bivgroup.services.b2bposws.system.Constants;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.bivgroup.services.b2bposws.system.files.FileWriter;
import com.bivgroup.sessionutils.SessionController;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.query.Query;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.system.external.ExternalService;
import ru.diasoft.services.inscore.util.ConfigUtils;
import ru.diasoft.services.inscore.util.StringCryptUtils;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author averichevsm
 */
@BOName("ExportCustom")
public class ExportCustomFacade extends ProductContractCustomFacade implements FileWriter {

    protected final Logger logger = Logger.getLogger(this.getClass());

    private static final String B2BPOSWS = Constants.B2BPOSWS;
    private static final String SIGNB2BPOSWS = Constants.SIGNB2BPOSWS;
    // private static final String SIGNB2BPOSWS = Constants.B2BPOSWS; // !только для отладки!

    // Новый
    private static final String B2B_EXPORTDATA_NEW = "B2B_EXPORTDATA_NEW";
    // Поставлен в очередь обработки
    private static final String B2B_EXPORTDATA_QUEUE = "B2B_EXPORTDATA_QUEUE";
    // Обработан
    private static final String B2B_EXPORTDATA_PROCESSED = "B2B_EXPORTDATA_PROCESSED";

    private static volatile int exportDataProcessingThreadCount = 0;

    private static final String DEFAULT_EXPORT_REPORT_TEMPLATE_NAME = "EXPORT/EXPORT01/export.ods";

    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    final private byte[] Salt = {
        (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
        (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19
    };

    // максимальное количество строк, сохраняемых в одном запросе
    private static final int MAX_RECORDS_FOR_MASS_CREATE_SINGLE_QUERY = 1000;
    //private static final int MAX_RECORDS_FOR_MASS_CREATE_SINGLE_QUERY = 10; //!только для отладки!

    protected SimpleDateFormat EXPORT_DATA_FILENAME_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd-hh.mm.ss");

    private Map<String, String> listsSysNameFieldsByListNames;
    protected Map<String, String> columnNamesByKeyNames;
    private Set<String> alwaysSkippedKeyNames;

    /*
    // здесь более не требуется - будет доступно за счет B2BBaseFacade implements SeaweedsGetters
    protected String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }

    // здесь более не требуется - будет доступно за счет B2BBaseFacade implements SeaweedsGetters
    protected String getSeaweedFSUrl() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("SEAWEEDFSURL", "");
        return login;
    }
    */

    private void addSubMapLevel(Map<String, String> mainMap, String parentKey, String parentValue, Map<String, String> subMap) {
        String valuePrefix = parentValue.isEmpty() ? "" : parentValue + " - ";
        for (Map.Entry<String, String> entry : subMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mainMap.put(parentKey + key, valuePrefix + value);
        }
    }

    private Map<String, Object> makeReportKeyMap(String field, String headerName) {
        Map<String, Object> result = new HashMap<>();
        columnNamesByKeyNames.put(field, headerName);
        result.put("headerName", headerName);
        result.put("field", field);
        return result;
    }

    protected void makeBeneficaryInvest(String beneficaryPrefixList, String beneficaryPrefix, List<Map<String, Object>> outList) {

        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".FIO", "ФИО Выгодоприобретателя " + beneficaryPrefix));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".PASSPORT", "Паспорт Выгодоприобретателя " + beneficaryPrefix));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".PART", "Доля Выгодоприобретателя " + beneficaryPrefix));
    }

    protected void makeBeneficaryInvest1(String beneficaryPrefixList, String beneficaryPrefix, List<Map<String, Object>> outList) {

        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".BIRTHDATE", "Дата рождения выгодоприобретателя " + beneficaryPrefix));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".GENDERSTR", "Пол выгодоприобретателя " + beneficaryPrefix));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".RISK", "Страховой риск " + beneficaryPrefix));
    }

    protected void makeBeneficary(String beneficaryPrefixList, String beneficaryPrefix, List<Map<String, Object>> outList) {

        outList.add(makeReportKeyMap(".BENEFICIARYLIST" + beneficaryPrefixList + ".RISK", "Выгодоприобретатель " + beneficaryPrefix + ": Риск"));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".FIO", "Выгодоприобретатель " + beneficaryPrefix + ": ФИО"));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".TYPESTR", "Выгодоприобретатель " + beneficaryPrefix + ": Тип документа удостоверяющего личность выгодоприобретателя"));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".PASSPORT", "Выгодоприобретатель " + beneficaryPrefix + ": Паспортные данные"));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".PART", "Выгодоприобретатель " + beneficaryPrefix + ": Доля (процент)"));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".BIRTHDATE", "Выгодоприобретатель " + beneficaryPrefix + ": Дата рождения"));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + "", "Выгодоприобретатель " + beneficaryPrefix + ": Родство"));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".CITIZENSHIPSTR", "Выгодоприобретатель " + beneficaryPrefix + ": Гражданство"));
        outList.add(makeReportKeyMap(".BENEFICIARYLIST." + beneficaryPrefixList + ".INN", "Выгодоприобретатель " + beneficaryPrefix + ": ИНН (при наличии)"));
    }

    private Map<String, Object> exportOrgStructCreate(Long exportDataId, String login, String password) throws Exception {
        // Грузим оргструктуру пользователя.
        Map<String, Object> checkLoginParams = new HashMap<String, Object>();
        checkLoginParams.put("username", XMLUtil.getUserName(login));
        checkLoginParams.put("passwordSha", password);
        Map<String, Object> checkLoginResult = WsUtils.getFirstItemFromResultMap(this.selectQuery("dsCheckLogin", checkLoginParams));
        if (null != checkLoginResult) {
            Map<String, Object> structParams = new HashMap<String, Object>();
            structParams.put(RETURN_AS_HASH_MAP, true);
            structParams.put("EXPORTDATAID", exportDataId);
            structParams.put("ORGSTRUCTID", checkLoginResult.get("DEPARTMENTID"));
            Map<String, Object> structRes = this.callService(Constants.B2BPOSWS, "dsB2BExportDataOrgStructCreate", structParams, login, password);

            return structRes;
        } else {
            return new HashMap<String, Object>();
        }
    }

    @WsMethod(requiredParams = {"TEMPLATEID", "FINISHDATE"})
    public Map<String, Object> dsB2BExportDataCreateEx(Map<String, Object> params) throws Exception {

        logger.debug("Creating export data record...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        // создание объекта экспорта на основании параметров вызова метода
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.putAll(params);

        // преобразование дат в числовые представления для выполнения запросов
        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(createParams, Double.class);

        Long exportDataID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS, "dsB2BExportDataCreate", createParams, login, password, "EXPORTDATAID"));
        if (exportDataID != null) {
            // создан объекта экспорта с идентификатором
            logger.debug("Created export data record with id (EXPORTDATAID) = " + exportDataID);
            // добавляем права на выгрузку.
            exportOrgStructCreate(exportDataID, login, password);

            // перевод объекта экспорта в статус "Поставлен в очередь обработки", поскольку теперь сохранение вызывается по нажатию на кнопку "Сформировать"
            Map<String, Object> exportData = new HashMap<String, Object>();
            exportData.put("EXPORTDATAID", exportDataID);
            exportData.put("STATESYSNAME", B2B_EXPORTDATA_NEW);
            Map<String, Object> transRes = exportDataMakeTrans(exportData, B2B_EXPORTDATA_QUEUE, login, password);

            // запрос полных сведений созданного объекта (чтобы не делать отдельного запроса с интерфейса сразу после создания)
            Map<String, Object> browseParams = new HashMap<String, Object>();
            browseParams.put("EXPORTDATAID", exportDataID);
            browseParams.put(RETURN_AS_HASH_MAP, true);
            exportData = this.callService(B2BPOSWS, "dsB2BExportDataBrowseListByParam", browseParams, login, password);
            logger.debug("Browsed export data record = " + exportData);

            // преобразование дат в строковые представления для angular-интерфейса
            // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
            Map<String, Object> callParams = new HashMap<String, Object>();
            callParams.put("EXPORTDATAID", exportDataID);
            callParams.put("STATESYSNAME", B2B_EXPORTDATA_QUEUE);
            callParams.put("STARTDATE", exportData.get("STARTDATE"));
            callParams.put("FINISHDATE", exportData.get("FINISHDATE"));
            callParams.put("AGENTID", exportData.get("AGENTID"));
            callParams.put("TEMPLATEID", exportData.get("TEMPLATEID"));
            callParams.put(RETURN_AS_HASH_MAP, true);
            this.callService(B2BPOSWS, "dsB2BExportDataProcessSingleRecord", callParams, login, password);
            browseParams.clear();
            browseParams.put("EXPORTDATAID", exportDataID);
            browseParams.put(RETURN_AS_HASH_MAP, true);
            // запрос полных сведений созданного объекта (чтобы не делать отдельного запроса с интерфейса сразу после создания)
            exportData = this.callService(B2BPOSWS, "dsB2BExportDataBrowseListByParam", browseParams, login, password);
            logger.debug("Browsed export data record = " + exportData);
            // формирование результата - запрошенные сведения объекта экспорта после смены статуса
            result.putAll(exportData);
            parseDates(result, String.class);
        } else {

            // объекта экспорта не создан
            result.put("Error", "No export data record created!");
            logger.error("No export data record created!");

        }
        // возврат результата
        logger.debug("Creating export data record finished.");
        return result;

    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BExportDataProcess(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        if (exportDataProcessingThreadCount == 0) {
            logger.debug("No doExportDataProcess threads running found - starting new...");
            exportDataProcessingThreadCount = 1;
            try {
                result = doExportDataProcess(params);
            } finally {
                exportDataProcessingThreadCount = 0;
                logger.debug("doExportDataProcess thread finished.");
            }
        } else {
            logger.debug("doExportDataProcess already running, no new threads started.");
        }
        return result;

    }

    private Map<String, Object> doExportDataProcess(Map<String, Object> params) throws Exception {
        logger.debug("");
        logger.debug("Start export data processing...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> exportDataParams = new HashMap<String, Object>();
        exportDataParams.putAll(params);
        exportDataParams.put("STATESYSNAME", B2B_EXPORTDATA_QUEUE);

        List<Map<String, Object>> exportDataList = WsUtils.getListFromResultMap(this.callService(B2BPOSWS, "dsB2BExportDataBrowseListByParam", exportDataParams, login, password));

        int exportDataTotalCount = exportDataList.size();
        if (exportDataTotalCount <= 0) {
            logger.debug("No export data records found for processing.");
        } else {
            logger.debug(String.format("Found %d export data records for processing.", exportDataTotalCount));
            int exportDataCount = 0;

            for (Map<String, Object> exportDataRecord : exportDataList) {

                exportDataCount++;
                logger.debug(String.format("Start processing record %d (from total of %d records)...", exportDataCount, exportDataTotalCount));

                Map<String, Object> processParams = new HashMap<String, Object>();
                processParams.putAll(exportDataRecord);
                processParams.put(RETURN_AS_HASH_MAP, true);
                try {
                    Map<String, Object> processResult = this.callService(B2BPOSWS, "dsB2BExportDataProcessSingleRecord", processParams, login, password);
                    exportDataRecord.putAll(processResult);
                } catch (Exception ex) {
                    exportDataRecord.put("ErrorEx", ex);
                    logger.error(String.format("During processing export data record with EXPORTDATAID = %d exception has been thrown: %s", getLongParam(exportDataRecord.get("EXPORTDATAID")), ex.toString()));

                    // получение текста ошибки и сохранение его в БД - не предусмотрено ФТ
                    //String errorText;
                    //Throwable cause = ex.getCause();
                    //if (cause instanceof Mort900Exception) {
                    //    errorText = ((Mort900Exception) cause).getRussianMessage();
                    //} else {
                    //    errorText = cause.getMessage();
                    //}
                    // перевод статуса в обработанный с ошибкой - не предусмотрено ФТ
                    //Map<String, Object> transResult = bankCashFlowMakeTrans(exportDataRecord, B2B_BANKCASHFLOW_ERROR, errorText, login, password);
                    //exportDataRecord.putAll(transResult);
                }
                logger.debug(String.format("Processing record %d (from total of %d records) finished.", exportDataCount, exportDataTotalCount));

            }
        }

        result.put("EXPORTDATALIST", exportDataList);
        logger.debug("Export data processing finished.");
        logger.debug("");
        return result;
    }

    @WsMethod(requiredParams = {"EXPORTDATAID", "TEMPLATEID"})
    public Map<String, Object> dsB2BExportDataCreateReport(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        //1. получаем тип темплейта по templateId
        //2. из типа получаем имя метода - формирователя файла.
        //3. если метода нет, или записи типа нет - используем старый метод по умолчанию.
        Map<String, Object> templateParam = new HashMap<String, Object>();
        templateParam.put("TEMPLATEID", params.get("TEMPLATEID"));
        templateParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> templateRes = this.callService(B2BPOSWS, "dsB2BExportDataTemplateBrowseListByParam", templateParam, login, password);
        if (templateRes != null) {
            if (templateRes.get("TYPEID") != null) {
                Long typeId = getLongParam(templateRes.get("TYPEID"));
                Map<String, Object> typeParam = new HashMap<String, Object>();
                typeParam.put("TYPEID", typeId);
                typeParam.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> typeRes = this.callService(B2BPOSWS, "dsB2BExportDataTypeBrowseListByParam", typeParam, login, password);
                if (typeRes != null) {
                    if (typeRes.get("METHODNAME") != null) {
                        return this.callService(SIGNB2BPOSWS, typeRes.get("METHODNAME").toString(), params, login, password);
                    }
                }
            }
        }

        return this.callService(B2BPOSWS, "dsB2BExportDataCreateXLSReport", params, login, password);
    }

    protected String genSumFormattedStr(Map<String, Object> bean, String fieldName) {
        if (bean.get(fieldName) != null) {
            if (!getStringParam(bean.get(fieldName)).isEmpty()) {
                Double amount = getDoubleParam(bean.get(fieldName));
                NumberFormat numberFormatter;
                String amountOut;
                Locale currentLocale = new Locale("ru");

                numberFormatter = NumberFormat.getNumberInstance(currentLocale);
                amountOut = numberFormatter.format(amount);
                bean.put(fieldName, amountOut);
                return amountOut;
            }
        }
        return null;
    }

    @WsMethod(requiredParams = {"EXPORTDATAID", "TEMPLATEID"})
    public Map<String, Object> dsB2BExportDataCreateXLSReport(Map<String, Object> params) throws Exception {

        logger.debug("Export data report creating...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        // идентификатор обрабатываемой записи
        Long exportDataID = getLongParam(params.get("EXPORTDATAID"));
        logger.debug("Export data record id (EXPORTDATAID) = " + exportDataID);

        // получение шаблона обрабатываемой записи
        Long templateID = getLongParam(params.get("TEMPLATEID"));
        logger.debug("Template id for this export data (TEMPLATEID) = " + templateID);
        Map<String, Object> template = getExportDataTemplateByID(templateID, login, password);

        // получение текста запроса из шаблона обрабатываемой записи
        String dataQueryText = getStringParam(template.get("DATASQL"));
        logger.debug("Data query text for this template (DATASQL) = \n\n" + dataQueryText + "\n");
        if (dataQueryText.isEmpty()) {
            throw new Exception(String.format("No data query text (DATASQL) found in template with id (TEMPLATEID) = %d for export data record with id (EXPORTDATAID) = %d.", templateID, exportDataID));
        }

        // OBJECTIDLIST больше не используется (и не должен быть использован) ни в одном шаблоне выгрузки
        /*
        // строковый список идентификаторов объектов
        String objectIDsListStr = getObjectIDsListStrByExportDataID(exportDataID, login, password);

        // формирование списка со сведениями объектов
        List<Map<String, Object>> dataList;
        if (objectIDsListStr.isEmpty()) {
            // если список идентификаторов пуст - то и список запрошенных данных также будет пуст
            dataList = new ArrayList<Map<String, Object>>();
        } else {
            // подготовка параметров для запрос списка со сведениями объектов
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("OBJECTIDLIST", objectIDsListStr);
            queryParams.put("EXPORTDATAID", exportDataID);

            // запрос списка со сведениями объектов
            Map<String, Object> queryRes = doQuery("B2BExportData", dataQueryText, queryParams);
            dataList = WsUtils.getListFromResultMap(queryRes);
        }
        */

        // формирование списка со сведениями объектов
        List<Map<String, Object>> dataList;
        if (exportDataID == null) {
            // если идентификатор выгрузки пуст - то и список запрошенных данных также должен быть пуст
            dataList = new ArrayList<Map<String, Object>>();
        } else {
            // подготовка параметров для запрос списка со сведениями объектов
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("EXPORTDATAID", exportDataID);
            // запрос списка со сведениями объектов
            Map<String, Object> queryRes = doQuery("B2BExportData", dataQueryText, queryParams);
            dataList = WsUtils.getListFromResultMap(queryRes);
        }

        // список из мап, содержащих описание столбцов отчета
        ArrayList<Map<String, Object>> columnNamesMapList = getColumnMapListFormExportDataTemplate(template);

        // получение списка из мап, содержащих описание столбцов отчета с датами
        // (для последующего использования при конвертации дат - чтобы не проверять имена ключей у каждого элемента списка со сведениями объектов)
        ArrayList<Map<String, Object>> datesColumnNamesMapList = getDatesColumnNamesMapList(columnNamesMapList);

        // список с именами столбцов/ключей с нестроковым содержимым
        // (для последующего использования при конвертации дат - чтобы не проверять тип содержимого у каждого ключа каждого элемента списка со сведениями объектов)
        // (строится на основании типов в первой из строк, поскольку для всех остальных строк ключи и типы такие же)
        List<String> nonStringKeys = new ArrayList<String>();
        if (dataList.size() > 0) {
            Map<String, Object> firstDataRecord = dataList.get(0);
            for (Map.Entry<String, Object> entry : firstDataRecord.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if ((!(value instanceof String)) && (!key.endsWith("DATE"))) {
                    nonStringKeys.add(key);
                }
            }
        }
        logger.debug("Non string data columns list (nonStringKeys) = " + nonStringKeys);

        // дополнительна обработка содержимого списка со сведениями объектов (конвертация значений в строковые представления)
        for (Map<String, Object> dataRecord : dataList) {
            if (dataRecord != null) {
                // конвертация значений в строковые представления для дат, с переименованием ключей ("Название_колонкиDATE" > "Название_колонки")
                for (Map<String, Object> dateColumnNameMap : datesColumnNamesMapList) {
                    String field = getStringParam(dateColumnNameMap.get("field"));
                    String newFieldName = getStringParam(dateColumnNameMap.get("newField"));
                    Object rawDateObj = dataRecord.remove(field);
                    String dateStr = "";

                    if (rawDateObj != null) {
                        Object parsedDateObj = parseAnyDate(rawDateObj, String.class, field, true);
                        if (parsedDateObj != null) {
                            dateStr = (String) parsedDateObj;
                            dateStr = dateStr.split(" ")[0];
                        }
                    } else {
                        // пропуск конвертации, если дата - null
                        logger.warn("Parsing key's '" + field + "' value to string date is skipped - value is absent or null.");
                    }
                    // строковое представление для даты безусловно помещаем в запись, даже если это пустая строка (иначе могут 'съезжать' колонки в xls-отчете)
                    dataRecord.put(newFieldName, dateStr);
                }
                // конвертация значений в строковые представления для нестрокового содержимого, без переименования ключей
                for (String nonStringKey : nonStringKeys) {
                    dataRecord.put(nonStringKey, getStringParam(dataRecord.get(nonStringKey)));
                }
            }
        }
        // logger.debug("dataList = " + dataList); // спам

        // подготовка параметров для генерации отчета
        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put("COLUMNLIST", columnNamesMapList);
        reportData.put("DATALIST", dataList);
        String reportFormat = ".xls";

        // генерация отчета
        /*
        String reportName = genExportReport(reportData, reportFormat, login, password);
        */
        if (template != null) {
            template.remove("REPTEMPLATENAME"); // ранее тут вызвалась генерация отчета с REPTEMPLATENAME = null, следует сохранить эту же логику
        }
        Map<String, Object> reportNameResult = genExportReport(reportData, reportFormat, template, exportDataID, login, password);
        if (reportNameResult.get("Status").toString() == "ERROR") {
           logger.debug("Error genExportReport:"+ reportNameResult.get("Error").toString());
           return reportNameResult;
        }
        String reportName = reportNameResult.get("reportNameWithExt").toString();
        // шифрование имен файлов отчета для возврата в angular-интерфейс
        String encryptedFileNamesStr = getEncryptedFileNamesStr(reportName);
        if (!encryptedFileNamesStr.isEmpty()) {
            result.put("ENCRIPTEDFILENAME", encryptedFileNamesStr);
        }

        // сгенерированный отчет выдать пользователю в интерфейсе
        logger.debug("Export data report creating finish.");
        return result;

    }
    // получение списка из мап, содержащих описание столбцов отчета с датами

    protected ArrayList<Map<String, Object>> getDatesColumnNamesMapList(ArrayList<Map<String, Object>> columnNamesMapList) {
        ArrayList<Map<String, Object>> datesColumnNamesMapList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> columnNamesMap : columnNamesMapList) {
            String field = getStringParam(columnNamesMap.get("field"));
            if (field.endsWith("DATE")) {
                String newField = field.replace("DATE", "");
                Map<String, Object> datesColumnNamesMap = new HashMap<String, Object>();
                datesColumnNamesMap.putAll(columnNamesMap);
                datesColumnNamesMap.put("newField", newField);
                columnNamesMap.put("field", newField);
                datesColumnNamesMapList.add(datesColumnNamesMap);
            }
        }
        logger.debug("Custom date columns maps list = " + datesColumnNamesMapList);
        return datesColumnNamesMapList;
    }

    protected String getEncryptedFileNamesStr(String reportName, String userFileName, boolean isExternalFS) throws SecurityException {
        logger.debug("Encrypting file names for angular interface...");
        String encryptedFileNamesStr = "";
        if (!reportName.isEmpty()) {
            logger.debug("Report name for encrypting (reportName): " + reportName);
            String docPath = reportName;
            String docName = reportName;
            if (!isExternalFS) {
                int separatorIndex = docPath.indexOf("\\");
                if (separatorIndex >= 0) {
                    docName = docPath.substring(separatorIndex + 1);
                }
                separatorIndex = docPath.indexOf("/");
                if (separatorIndex >= 0) {
                    docName = docPath.substring(separatorIndex + 1);
                }
            }
            String userDocName = userFileName;
            String fileNamesStr;
            SessionController controller = new B2BFileSessionController();
            Map<String, Object> sessionParams = new HashMap<>();
            if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
                sessionParams.put(FS_TYPE_PARAMNAME, Constants.FS_EXTERNAL);
                sessionParams.put(SOME_ID_PARAMNAME, docName);
                sessionParams.put(USER_DOCNAME_PARAMNAME, userDocName);
                //fileNamesStr = Constants.FS_EXTERNAL + "@" + docName + "@" + userDocName;
            } else {
                sessionParams.put(FS_TYPE_PARAMNAME, Constants.FS_HARDDRIVE);
                sessionParams.put(SOME_ID_PARAMNAME, docName);
                sessionParams.put(USER_DOCNAME_PARAMNAME, userDocName);
                //fileNamesStr = Constants.FS_HARDDRIVE + "@" + docName + "@" + userDocName;
            }
            //logger.debug("File names string for encrypting (fileNamesStr) = " + fileNamesStr);

            //StringCryptUtils crypter = new StringCryptUtils(EncryptionPassword, Salt);
            //encryptedFileNamesStr = crypter.encrypt(fileNamesStr);
            encryptedFileNamesStr = controller.createSession(sessionParams);
            logger.debug("Encrypted file names string (encryptedFileNamesStr) = " + encryptedFileNamesStr);

        }
        logger.debug("Encrypting file names for angular interface finished.");
        return encryptedFileNamesStr;
    }

    protected String getEncryptedFileNamesStr(String reportName) throws SecurityException {
        logger.debug("Encrypting file names for angular interface...");
        String encryptedFileNamesStr = "";
        if (!reportName.isEmpty()) {
            logger.debug("Report name for encrypting (reportName): " + reportName);
            String docPath = reportName;
            String docName = reportName;
            int separatorIndex = docPath.indexOf("\\");
            if (separatorIndex >= 0) {
                docName = docPath.substring(separatorIndex + 1);
            }
            separatorIndex = docPath.indexOf("/");
            if (separatorIndex >= 0) {
                docName = docPath.substring(separatorIndex + 1);
            }
            String userDocName = reportName;
            SessionController controller = new B2BFileSessionController();
            Map<String, Object> sessionParams = new HashMap<>();
            sessionParams.put(FS_TYPE_PARAMNAME, Constants.FS_HARDDRIVE);
            sessionParams.put(SOME_ID_PARAMNAME, docName);
            sessionParams.put(USER_DOCNAME_PARAMNAME, userDocName);

            String fileNamesStr = Constants.FS_HARDDRIVE + "@" + docName + "@" + userDocName;
            logger.debug("File names string for encrypting (fileNamesStr) = " + fileNamesStr);
//            StringCryptUtils crypter = new StringCryptUtils(EncryptionPassword, Salt);
//            encryptedFileNamesStr = crypter.encrypt(fileNamesStr);
            encryptedFileNamesStr = controller.createSession(sessionParams);

            logger.debug("Encrypted file names string (encryptedFileNamesStr) = " + encryptedFileNamesStr);

        }
        logger.debug("Encrypting file names for angular interface finished.");
        return encryptedFileNamesStr;
    }

    protected String genNoSQLExportReport(Map<String, Object> reportData, String reportFormat, String templateName, String login, String password) throws Exception {

        // получение из CORE_SETTINGS относительного пути (включая имя файла) до шаблона универсального отчета для B2B-выгрузок
        if ((templateName == null) || (templateName.isEmpty())) {
            templateName = getTemplateNoSQLNameFromCoreSettingsOrDefault(login, password);
        }
        // вызов генерации отчета
        Map<String, Object> libreReportParams = new HashMap<String, Object>();
        libreReportParams.put("REPORTDATA", reportData);
        libreReportParams.put("REPORTFORMATS", reportFormat);
        libreReportParams.put("templateName", templateName);
        libreReportParams.put(RETURN_AS_HASH_MAP, true);
        //logger.debug("libreReportParams = " + libreReportParams); // отключено, слишком большая запись в протокол
        //Map<String, Object> reportResult = null;
        Map<String, Object> reportResult = this.callServiceTimeLogged(Constants.LIBREOFFICEREPORTSWS, "dsLibreOfficeReportCreate", libreReportParams, login, password);
        logger.debug("reportResult = " + reportResult);

        // получение имени сгенерированного отчета из результатов вызова генерации
        String reportName = "";
        if (reportResult != null) {
            Map<String, Object> reportResultData = (Map<String, Object>) reportResult.get("REPORTDATA");
            if (reportResultData != null) {
                reportName = getStringParam(reportResultData.get("reportName"));
                if (!reportName.isEmpty()) {
                    reportName = reportName + reportFormat;
                }
            }
        }
        logger.debug("reportName = " + reportName);

        return reportName;
    }

    protected Map<String, Object> genExportReport(Map<String, Object> reportData, String reportFormat, Map<String, Object> template, Long exportDataId, String login, String password) throws Exception {
        String templateName = getStringParamLogged(template, "REPTEMPLATENAME");
        if (templateName.isEmpty()) {
            // получение из CORE_SETTINGS относительного пути (включая имя файла) до шаблона универсального отчета для B2B-выгрузок
            templateName = getTemplateNameFromCoreSettingsOrDefault(login, password);
        }
        // вызов генерации отчета
        Map<String, Object> libreReportParams = new HashMap<String, Object>();
        Map<String, Object> reportNameWithExtResult = new HashMap<String, Object>();
        libreReportParams.put("REPORTDATA", reportData);
        libreReportParams.put("REPORTFORMATS", reportFormat);
        libreReportParams.put("templateName", templateName);
        libreReportParams.put(RETURN_AS_HASH_MAP, true);
        
        //logger.debug("libreReportParams = " + libreReportParams); // отключено, слишком большая запись в протокол
        Map<String, Object> reportResult = this.callService(Constants.LIBREOFFICEREPORTSWS, "dsLibreOfficeReportCreate", libreReportParams, login, password);
        reportNameWithExtResult.put("Status",reportResult.get("Status").toString());
        if(reportResult.get("Status").toString() == "ERROR") {
            reportNameWithExtResult.put("Error",reportResult.get("Error").toString());
            return reportNameWithExtResult;
        }
        logger.debug("reportResult = " + reportResult);

        // получение имени сгенерированного отчета из результатов вызова генерации
        String reportNameWithExt = "";
        String reportName = "";
        if (reportResult != null) {
            Map<String, Object> reportResultData = getMapParam(reportResult, "REPORTDATA");
            if (reportResultData != null) {
                reportName = getStringParamLogged(reportResultData, "reportName");
                if (!reportName.isEmpty()) {
                    reportNameWithExt = reportName + reportFormat;
                }
            }
        }
        logger.debug("reportNameWithExt = " + reportNameWithExt);

        // удаление старых (при необходимости) и прикрепление нового сформированного файла с данными выгрузки к самой записи выгрузки
        // (для возможности скачивания в интерфейсе, например, при длительном формировании, таймаутах и пр.)
        if (!reportName.isEmpty()) {
            // проверка наличия уже прикрепленных ранее файлов такого же типа
            Map<String, Object> binFileBrowseParams = new HashMap<String, Object>();
            binFileBrowseParams.put("FILETYPEID", EXPORT_DATA_FILE.getFileTypeId());
            binFileBrowseParams.put("OBJID", exportDataId);
            String browseMethodName = "dsB2BExportData_BinaryFile_BinaryFileBrowseListByParam";
            List<Map<String, Object>> existedBinFileList = this.callServiceAndGetListFromResultMap(B2BPOSWS, browseMethodName, binFileBrowseParams, login, password);
            logger.debug("existedBinFileList: " + existedBinFileList);
            if ((existedBinFileList != null) && (!existedBinFileList.isEmpty())) {
                // удаление прикрепленных ранее файлов такого же типа
                for (Map<String, Object> exisitingBinFile : existedBinFileList) {
                    String deleteMethodName = "dsB2BExportData_BinaryFile_deleteBinaryFileInfo";
                    Map<String, Object> deleteBinFileParams = new HashMap<String, Object>();
                    deleteBinFileParams.put("BINFILEID", exisitingBinFile.get("BINFILEID"));
                    try {
                        Map<String, Object> binFileInfoDeleteResult = this.callServiceLogged(B2BPOSWS, deleteMethodName, deleteBinFileParams, login, password);
                        exisitingBinFile.put("binFileInfoDeleteResult", binFileInfoDeleteResult);
                    } catch (Exception ex) {
                        logger.error(String.format(
                                "Unable to delete file by calling '%s' with params: %s! Details (exception):",
                                deleteMethodName, deleteBinFileParams
                        ), ex);
                    }
                }
                logger.debug("existedBinFileList (after deleting): " + existedBinFileList);
            }
            // Сохранение уже существующего файла в SeaweedFS (если требуется); получение подробной информации о файле (всегда)
            Map<String, Object> binFileParams = trySaveReportToSeaweeds(reportName, reportFormat);
            logger.debug("binFileParams from trySaveReportToSeaweeds: " + binFileParams); // спам
            // формирование имени файла для пользователя
            String templateCaption = getStringParamLogged(template, "CAPTION");
            String userFileName = getExportDataFilenameShiftedDateStr() + " " + templateCaption + reportFormat;
            // параметры прикрепления файла
            binFileParams.putAll(EXPORT_DATA_FILE.getFileTypeMap());
            binFileParams.put("OBJID", exportDataId);
            binFileParams.put("FILENAME", userFileName);
            binFileParams.put(RETURN_AS_HASH_MAP, true);
            //
            String methodName = "dsB2BExportData_BinaryFile_createBinaryFileInfo";
            Map<String, Object> binFileInfoCreateResult = this.callServiceLogged(B2BPOSWS, methodName, binFileParams, login, password);
            Long binFileId = null;
            if (isCallResultOK(binFileInfoCreateResult)) {
                // успех
                binFileId = getLongParamLogged(binFileInfoCreateResult, "BINFILEID");
            }
            if (binFileId == null) {
                logger.error(String.format(
                        "Failed attaching file to export data record by calling %s with params %s! Details (call result): %s.",
                        methodName, binFileParams, binFileInfoCreateResult
                ));
            }
        }
        reportNameWithExtResult.put("reportNameWithExt",reportNameWithExt);
        return reportNameWithExtResult;
    }

    /*
    protected String genExportReport(Map<String, Object> reportData, String reportFormat, String login, String password) throws Exception {
        return genExportReport(reportData, reportFormat, null, login, password);
    }
    */

    // получение из CORE_SETTINGS относительного пути (включая имя файла) до шаблона универсального отчета для B2B-выгрузок
    private String getTemplateNoSQLNameFromCoreSettingsOrDefault(String login, String password) throws Exception {
        logger.debug("Getting export report template file name from CORE_SETTING...");
        String settingSysName = "b2bExportNoSQLReportTemplateFilename";
        return getTemplateNameFromCoreSettingsOrDefaultEx(settingSysName, login, password);
    }

    // получение из CORE_SETTINGS относительного пути (включая имя файла) до шаблона универсального отчета для B2B-выгрузок
    private String getTemplateNameFromCoreSettingsOrDefault(String login, String password) throws Exception {
        logger.debug("Getting export report template file name from CORE_SETTING...");
        String settingSysName = "b2bExportReportTemplateFilename";
        return getTemplateNameFromCoreSettingsOrDefaultEx(settingSysName, login, password);
    }

    // получение из CORE_SETTINGS относительного пути (включая имя файла) до шаблона универсального отчета для B2B-выгрузок
    private String getTemplateNameFromCoreSettingsOrDefaultEx(String settingSysName, String login, String password) throws Exception {
        logger.debug("Getting export report template file name from CORE_SETTING...");
        String templateName = getCoreSettingBySysName(settingSysName, login, password);
        if (templateName.isEmpty()) {
            templateName = DEFAULT_EXPORT_REPORT_TEMPLATE_NAME;
            logger.debug("Export report template file name not found in CORE_SETTING, default file name will be used: " + templateName);
        } else {
            logger.debug("Export report template file name found in CORE_SETTING and will be used: " + templateName);
        }
        return templateName;
    }

    // перенесено в B2BBaseFacade
    /*
    // получение из CORE_SETTINGS значения конкретного параметра по его системному имени
    private String getCoreSettingBySysName(String settingSysName, String login, String password) throws Exception {
        logger.debug(String.format("Getting core setting by system name [%s]...", settingSysName));
        Map<String, Object> coreSettingParams = new HashMap<String, Object>();
        coreSettingParams.put("SETTINGSYSNAME", settingSysName);
        coreSettingParams.put(RETURN_AS_HASH_MAP, "TRUE"); // getSysSettingBySysName работает только со строковыми значениями RETURN_AS_HASH_MAP
        String coreSettingValue = "";
        Map<String, Object> coreSetting = this.callService(COREWS, "getSysSettingBySysName", coreSettingParams, login, password);
        if (coreSetting != null) {
            coreSettingValue = getStringParam(coreSetting.get("SETTINGVALUE"));
            if (coreSettingValue.isEmpty()) {
                logger.debug(String.format("Core setting with system name [%s] does not exist or contain no value.", settingSysName));
            } else {
                logger.debug(String.format("Core setting with system name [%s] contain value [%s].", settingSysName, coreSettingValue));
            }
        } else {
            logger.debug("Method getSysSettingBySysName return no result.");
        }
        return coreSettingValue;
    }
    */

    private Map<String, Object> loadDepartment(Long departamentId, String login, String password) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("DEPARTMENTID", departamentId);
        Map<String, Object> qres = this.callService(ADMINWS_SERVICE_NAME, "admDepartment", queryParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            qres = ((List<Map<String, Object>>) qres.get(RESULT)).get(0);
            return qres;
        }
        return null;
    }

    @WsMethod(requiredParams = {"EXPORTDATAID", "STATESYSNAME", "TEMPLATEID"})
    public Map<String, Object> dsB2BExportDataProcessSingleRecord(Map<String, Object> params) throws Exception {

        // обработка конкретной записи
        logger.debug("Export data single record processing...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        // идентификатор обрабатываемой записи
        Long exportDataID = getLongParam(params.get("EXPORTDATAID"));
        logger.debug("Processed export data record id (EXPORTDATAID) = " + exportDataID);

        // получение текста запроса из шаблона обрабатываемой записи        
        Long templateID = getLongParam(params.get("TEMPLATEID"));
        logger.debug("Template id for this export data (TEMPLATEID) = " + templateID);
        Map<String, Object> template = getExportDataTemplateByID(templateID, login, password);

        String idQueryText = getStringParam(template.get("SQL"));
        logger.debug("Query text for this template (SQL) = \n\n" + idQueryText + "\n");
        if (idQueryText.isEmpty()) {
            throw new Exception(String.format("No query text found in template with id (TEMPLATEID) = %d for export data record with id (EXPORTDATAID) = %d.", templateID, exportDataID));
        }
        Long typeID = getLongParam(template.get("TYPEID"));
        if (typeID == null) {
            throw new Exception(String.format("No type id (TYPEID) found in template with id (TEMPLATEID) = %d for export data record with id (EXPORTDATAID) = %d.", templateID, exportDataID));
        }
        Map<String, Object> checkLoginParams = new HashMap<String, Object>();
        checkLoginParams.put("username", XMLUtil.getUserName(login));
        checkLoginParams.put("passwordSha", password);
        Map<String, Object> checkLoginResult = WsUtils.getFirstItemFromResultMap(this.selectQuery("dsCheckLogin", checkLoginParams));
        if (checkLoginResult != null) {
            params.put(Constants.SESSIONPARAM_USERACCOUNTID, checkLoginResult.get("USERACCOUNTID"));
            params.put(Constants.SESSIONPARAM_DEPARTMENTID, checkLoginResult.get("DEPARTMENTID"));
        }

        // выполнение запроса (с параметрами из обрабатываемой записи) для получения списка идентификаторов объектов
        Map<String, Object> queryParams = new HashMap<String, Object>();
        //queryParams.putAll(params);
        //queryParams.remove("OBJECTIDLIST");
        queryParams.put("DOCUMENTFROMDATE", params.get("STARTDATE"));
        queryParams.put("DOCUMENTTODATE", params.get("FINISHDATE"));
        queryParams.put("AGENTID", params.get("AGENTID"));
        queryParams.put("PRight_RPAccessPOS_Branch", params.get(Constants.SESSIONPARAM_DEPARTMENTID));
        queryParams.put("TEMPLATEID", templateID);
        // системные имена продуктов передаются в запрос в виде параметров - чтобы не указывать их в самих SQL-запросах в явном виде
        queryParams.put("PRODSYSNAMEMORTGAGE900", SYSNAME_MORTGAGE900);
        // todo: дополнять передаваемые системные имена продуктов по мере их добавления в запросы в шаблоны
        logger.debug("Query parameters = " + queryParams);
        Map<String, Object> queryResult = doQuery("B2BExportData", idQueryText, queryParams);
        Long idCount = null;
        if (queryResult != null) {
            idCount = getLongParam(queryResult.get(TOTALCOUNT));
        }
        logger.debug("Objects ids count returned from query (idCount) = " + idCount);

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        if ((idCount != null) && (idCount > 0)) {
            // определение списка идентификаторов объектов, если запрос вернул его
            List<Map<String, Object>> idList = (ArrayList<Map<String, Object>>) WsUtils.getListFromResultMap(queryResult);
            logger.debug("Objects ids list returned from query (idList) = " + idList);

            // максимальное количество строк, сохраняемых в одном запросе
            logger.debug("Max records count for one insert query (MASS_CREATE_MAX_RECORDS_BY_QUERY) = " + MAX_RECORDS_FOR_MASS_CREATE_SINGLE_QUERY);

            // определение количества фрагментов для сохранения в отдельных запросах
            int subListCount = (idCount.intValue() / MAX_RECORDS_FOR_MASS_CREATE_SINGLE_QUERY) + 1;
            logger.debug(String.format("Objects ids list will be saved by %d querys (subListCount)", subListCount));

            // формирование и сохранение каждого фрагмента в отдельном запросе
            for (int subListNum = 0; subListNum < subListCount; subListNum++) {

                // получение фрагмента списка для сохранения
                logger.debug(String.format("Preparing query № %d (from total of %d querys)...", subListNum + 1, subListCount));
                int fromIndexInclusive = subListNum * MAX_RECORDS_FOR_MASS_CREATE_SINGLE_QUERY;
                logger.debug(String.format("Will be saved records from %d (inclusive).", fromIndexInclusive));
                int toIndexExclusive = (subListNum + 1) * MAX_RECORDS_FOR_MASS_CREATE_SINGLE_QUERY;
                if (toIndexExclusive > idCount.intValue()) {
                    toIndexExclusive = idCount.intValue();
                }
                logger.debug(String.format("Will be saved records to %d (exclusive).", toIndexExclusive));
                List<Map<String, Object>> idSubList = idList.subList(fromIndexInclusive, toIndexExclusive);

                // генерация идентификаторов записей для массового создания и выполнение запроса
                List<Map<String, Object>> subResultList = massCreateExportDataContent(idSubList, exportDataID, typeID);
                // дополнение списка результата результатом текущего запроса
                resultList.addAll(subResultList);

            }

        } else {
            logger.debug("No objects ids to save - saving skipped.");
        }

        // дополнение списка результата списком сохраненных записей о содержимом выгрузки
        result.put("EXPORTDATACONTENTLIST", resultList);

        // перевод статуса в успешно обработанный
        // todo: включить после реализации сохранения списка идентификаторов объектов в таблицу содержимого выгрузки
        Map<String, Object> transResult = exportDataMakeTrans(params, B2B_EXPORTDATA_PROCESSED, login, password);
        result.putAll(transResult);

        logger.debug("Export data single record processing finished.");

        return result;
    }

    private List<Map<String, Object>> doMassCreateExportDataContentQuery(Map<String, Object> params) throws Exception {

        int[] insertResult = this.insertQuery("dsB2BExportDataContentMassCreate", params);

        if (logger.isDebugEnabled()) {
            int totalSavedRows = 0;
            for (int i : insertResult) {
                totalSavedRows = totalSavedRows + i;
            }
            logger.debug("Rows created in DB: " + totalSavedRows);
        }

        return (List<Map<String, Object>>) params.get("rows");
    }

    // сохранение списка идентификаторов объектов в таблицу содержимого выгрузки
    private List<Map<String, Object>> massCreateExportDataContent(List<Map<String, Object>> idList, Long exportDataID, Long typeID) throws Exception {

        // генерация идентификаторов записей для массового создания
        ExternalService externalService = this.getExternalService();
        for (Map<String, Object> idRecord : idList) {
            Long generatedContendID = getLongParam(externalService.getNewId("B2B_EXPORTDATA_CONTENT"));
            idRecord.put("CONTENTID", generatedContendID);
            logger.debug("CONTENDID (generated) = " + generatedContendID);
            logger.debug("OBJECTID = " + getLongParam(idRecord.get("OBJECTID")));
        }

        // формирование параметров для сохранения списка идентификаторов объектов в таблицу содержимого выгрузки
        Map<String, Object> massCreateParams = new HashMap<String, Object>();
        massCreateParams.put("rows", idList);
        massCreateParams.put("totalCount", idList.size());
        massCreateParams.put("EXPORTDATAID", exportDataID);
        massCreateParams.put("TYPEID", typeID);

        // выполнение запроса
        List<Map<String, Object>> massCreateResult = doMassCreateExportDataContentQuery(massCreateParams);
        logger.debug("massCreateResult = " + massCreateResult);

        return massCreateResult;
    }

    protected Map<String, Object> getExportDataTemplateByID(Long templateID, String login, String password) throws Exception {
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("TEMPLATEID", templateID);
        templateParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> template = this.callService(B2BPOSWS, "dsB2BExportDataTemplateBrowseListByParamEx", templateParams, login, password);
        return template;
    }

    private Map<String, Object> exportDataMakeTrans(Map<String, Object> exportData, String toStateSysName, String login, String password) throws Exception {
        String idFieldName = "EXPORTDATAID";
        String methodNamePrefix = "dsB2BExportData";
        String typeSysName = "B2B_EXPORTDATA";
        return recordMakeTrans(exportData, toStateSysName, idFieldName, methodNamePrefix, typeSysName, login, password);
    }

    @WsMethod(requiredParams = {"EXPORTDATAID", "STATESYSNAME", "TOSTATESYSNAME"})
    public Map<String, Object> dsB2BExportDataMakeTrans(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String toStateSysName = getStringParam(params.get("TOSTATESYSNAME"));
        return exportDataMakeTrans(params, toStateSysName, login, password);
    }

    protected Map<String, Object> doQuery(String objEntityName, String dataQueryText, Map<String, Object> params) throws Exception {
        logger.debug("doQuery...");
        // создание запроса
        SQLTemplate query = createSQLTemplate(objEntityName, dataQueryText, params);
        // получение контекста и выполнение запроса
        DataContext context = this.getDataContext();
        List<Map<String, Object>> list = context.performQuery((Query) query);
        int totalCount = (list != null) ? list.size() : 0;
        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, list);
        result.put(TOTALCOUNT, totalCount);
        logger.debug(String.format("doQuery finished and returned %d records.", totalCount));
        return result;
    }

    // постраничный select для динамического angular-грида
    private Map<String, Object> doPagedQuery(String objEntityName, String dataQueryText, String idQueryText, Map<String, Object> params) throws Exception {

        // получение текста запроса подсчета количества строк (для постраничного select-а для динамического angular-грида) из текста запроса идентификаторов
        String countQueryText = getCountQueryTextFromIDQueryText(idQueryText);
        // получение объекта запроса количества строк (для постраничного select-а для динамического angular-грида) из текста запроса
        SQLTemplate countQuery = createSQLTemplate(objEntityName, countQueryText, params);

        // получение объекта запроса данных из текста запроса
        SQLTemplate selectQuery = createSQLTemplate(objEntityName, dataQueryText, params);

        // получение контекста и выполнение запроса
        DataContext context = this.getDataContext();
        Map<String, Object> result = this.performSelectQuery(context, (Query) countQuery, (Query) selectQuery, params);

        return result;
    }

    // получение текста запроса подсчета количества строк (для постраничного select-а для динамического angular-грида) из текста запроса идентификаторов
    private String getCountQueryTextFromIDQueryText(String idQueryText) {
        // фрагмент вида "#result('*', 'java.*', 'OBJECTID')" преобразуется в "#result('COUNT(*)', 'java.*', 'CNT')"
        String countQueryText = idQueryText.replace("#result('", "#result('COUNT(");
        countQueryText = countQueryText.replace("', 'java.", ")', 'java.");
        countQueryText = countQueryText.replace("', 'OBJECTID')", "', 'CNT')");
        return countQueryText;
    }

    // создание объекта запроса данных из текста запроса
    private SQLTemplate createSQLTemplate(String objEntityName, String queryText, Map<String, Object> queryParams) {
        SQLTemplate sqlTemplate = new SQLTemplate(objEntityName, queryText);
        sqlTemplate.setParameters(queryParams);
        sqlTemplate.setFetchingDataRows(true);
        sqlTemplate.setCachePolicy(SQLTemplate.NO_CACHE);
        return sqlTemplate;
    }

    // получение списка из мап, описывающих столбцы с данными
    // (для применения как в динамическом angular-гриде, так и при генерации отчетов)
    protected ArrayList<Map<String, Object>> getColumnMapListFormExportDataTemplate(Map<String, Object> template) {
        ArrayList<String> columnNamesList = getColumnNamesListFormExportDataTemplate(template);
        ArrayList<Map<String, Object>> columnMapList = new ArrayList<Map<String, Object>>();
        for (String columnName : columnNamesList) {
            if (isColumnNamesInComment(columnName)) {
                Map<String, Object> columnMap = new HashMap<String, Object>();
                String[] columnArr = columnName.split("-=:@:=-");
                columnMap.put("field", columnArr[0]);
                columnMap.put("headerName", columnArr[1]);
                if (columnArr[0].endsWith("DATE")) {
                    columnMap.put("cellRenderer", "renderDate"); // в js будет использоваться в виде ... cellRenderer: $rootScope["renderDate"] ... для использования функции $rootScope.renderDate
                }
                columnMapList.add(columnMap);
            } else {
                Map<String, Object> columnMap = new HashMap<String, Object>();
                columnMap.put("field", columnName);
                String headerName = columnName.replaceAll("_", " ");
                if (columnName.endsWith("DATE")) {
                    headerName = headerName.replaceAll("DATE", "");
                    columnMap.put("cellRenderer", "renderDate"); // в js будет использоваться в виде ... cellRenderer: $rootScope["renderDate"] ... для использования функции $rootScope.renderDate
                }
                columnMap.put("headerName", headerName.replace("\"", ""));
                columnMapList.add(columnMap);
            }

        }
        logger.debug("Custom columns maps list, generated from template data query text (TEMPLATECOLUMNLIST) = " + columnMapList);
        return columnMapList;
    }

    private ArrayList<String> getColumnNamesListFormExportDataTemplate(Map<String, Object> template) {
        String selectDataQueryText = getStringParam(template.get("DATASQL"));
        if (isColumnNamesInComment(selectDataQueryText)) {
            return getColumnNamesListFormDataQueryComment(selectDataQueryText);
        } else {
            return getColumnNamesListFormDataQueryText(selectDataQueryText);
        }
    }

    private ArrayList<String> getColumnNamesListFormDataQueryText(String selectDataQueryText) {
        logger.debug("Analysing query text:\n\n" + selectDataQueryText + "\n");

        int startIndex = 0;
        int startIndex1 = 0;
        int endIndex = 0;
        boolean isRepeat = true;
        String startStr = "#result('";
        String startStr1 = "#result(\"";

        ArrayList<String> result = new ArrayList<String>();
        do {
            logger.debug("Searching cayenne result key word...");
            startIndex = selectDataQueryText.indexOf(startStr, endIndex);
            startIndex1 = selectDataQueryText.indexOf(startStr1, endIndex);
            String splitter = "', '";
            String splitter1 = "\", '";
            boolean isFirstSplit1 = false;
            if ((startIndex1 < startIndex) || ((startIndex == -1) && (startIndex1 > 0))) {
                if (startIndex1 > 0) {
                    startIndex = startIndex1;
                    isFirstSplit1 = true;
                }
            }
            //logger.debug("startIndex = " + startIndex);
            if (startIndex > 0) {
                logger.debug("Cayenne result key word found at position: " + startIndex);
                endIndex = selectDataQueryText.indexOf("\n", startIndex);
                logger.debug(String.format("Cayenne result fragment ends at position: %d", endIndex));
                String line = selectDataQueryText.substring(startIndex + startStr.length(), endIndex - 2);
                line = line.trim();
                logger.debug("line = " + line);
                if (line.endsWith(")")) {
                    line = line.substring(0, line.length() - 1);
                }
                if (line.endsWith("'")) {
                    line = line.substring(0, line.length() - 1);
                }
                if (line.endsWith("'") || line.endsWith("\"")) {
                    line = line.substring(0, line.length() - 1);
                }
                logger.debug("line = " + line);
                //logger.debug("fragment = " + line);
                if (isFirstSplit1) {
                    String[] lineValues = line.split("\", '");
                    String[] lineValues1 = lineValues[1].split("', '");
                    logger.debug("Cayenne result field = " + lineValues[0]);
                    logger.debug("Cayenne result type = " + lineValues1[0]);
                    logger.debug("Cayenne result name = " + lineValues1[1]);
                    if (lineValues1[1].startsWith("'")) {
                        lineValues1[1] = lineValues1[1] + "'";
                    }
                    if (lineValues1[1].startsWith("\"")) {
                        lineValues1[1] = lineValues1[1] + "\"";
                    }
                    result.add(lineValues1[1]);
                    logger.debug("lineValues1 = " + lineValues1[1]);
                } else {
                    String[] lineValues = line.split("', '");
                    logger.debug("Cayenne result field = " + lineValues[0]);
                    logger.debug("Cayenne result type = " + lineValues[1]);
                    logger.debug("Cayenne result name = " + lineValues[2]);
                    if (lineValues[2].startsWith("'")) {
                        lineValues[2] = lineValues[2] + "'";
                    }
                    if (lineValues[2].startsWith("\"")) {
                        lineValues[2] = lineValues[2] + "\"";
                    }
                    result.add(lineValues[2]);
                    logger.debug("lineValues = " + lineValues[2]);
                }
            } else {
                logger.debug("No more cayenne result key word found.");
                isRepeat = false;
            }
        } while (isRepeat);

        logger.debug("Column names list generated from query text: " + result);

        return result;
    }

    @WsMethod(requiredParams = {"EXPORTDATAID", "TEMPLATEID"})
    public Map<String, Object> dsB2BExportDataContentDataBrowseListByParamsEx(Map<String, Object> params) throws Exception {

        logger.debug("Export data content objects browse...");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // идентификатор обрабатываемой записи
        Long exportDataID = getLongParam(params.get("EXPORTDATAID"));
        logger.debug("Analysed export data record id (EXPORTDATAID) = " + exportDataID);

        // получение текста запроса из шаблона обрабатываемой записи
        Long templateID = getLongParam(params.get("TEMPLATEID"));
        logger.debug("Template id for this export data (TEMPLATEID) = " + templateID);
        Map<String, Object> template = getExportDataTemplateByID(templateID, login, password);

        // текст запроса из шаблона обрабатываемой записи для получения данных
        String dataQueryText = getStringParam(template.get("DATASQL"));
        String dataMethod = "";
        logger.debug("Data query text for this template (DATASQL) = \n\n" + dataQueryText + "\n");
        if (dataQueryText.isEmpty()) {
            //доработка. если отсутствует datasql пытаемся найти DATAMETHOD.
            dataMethod = getStringParam(template.get("DATAMETHOD"));
            logger.debug("Data method for this template (DATAMETHOD) = \n\n" + dataQueryText + "\n");
            if (dataMethod.isEmpty()) {
                throw new Exception(String.format("No data query text (DATASQL) and data method (DATAMETHOD) found in template with id (TEMPLATEID) = %d for export data record with id (EXPORTDATAID) = %d.", templateID, exportDataID));
            }
        }

        // текст запроса из шаблона обрабатываемой записи для получения идентификаторов 
        String idsQueryText = getStringParam(template.get("SQL"));
        logger.debug("IDs query text for this template (SQL) = \n\n" + idsQueryText + "\n");
        if (idsQueryText.isEmpty()) {
            throw new Exception(String.format("No data query text (SQL) found in template with id (TEMPLATEID) = %d for export data record with id (EXPORTDATAID) = %d.", templateID, exportDataID));
        }

        // строковый список идентификаторов объектов
        String objectIDsListStr = getObjectIDsListStrByExportDataID(exportDataID, login, password);

        // подготовка параметров для запрос списка со сведениями объектов
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("OBJECTIDLIST", objectIDsListStr);
        queryParams.put("EXPORTDATAID", exportDataID);
        Map<String, Object> dataList = null;
        if (!dataQueryText.isEmpty()) {
            // параметры для постраничных запросов, фомируются angular-гридом
            queryParams.put("PAGE", params.get("PAGE"));
            queryParams.put("ROWSCOUNT", params.get("ROWSCOUNT"));
            queryParams.put("CP_TODAYDATE", new Date());

            // запрос списка со сведениями объектов
            dataList = doPagedQuery("B2BExportData", dataQueryText, idsQueryText, queryParams);
        }
        if (!dataMethod.isEmpty()) {
            dataList = this.callService(B2BPOSWS, dataMethod, params, login, password);
        }
        logger.debug("dataList = " + dataList);
        logger.debug("Export data content objects browse finished.");

        return dataList;

    }

    protected String getObjectIDsListStrByExportDataID(Long exportDataID, String login, String password) throws Exception {

        logger.debug("Getting objects ids list for export data with id (EXPORTDATAID) = " + exportDataID);
        Map<String, Object> contentParams = new HashMap<String, Object>();
        contentParams.put("EXPORTDATAID", exportDataID);
        Map<String, Object> content = this.callService(B2BPOSWS, "dsB2BExportDataContentBrowseListByParam", contentParams, login, password);
        List<Map<String, Object>> objectIDsList = WsUtils.getListFromResultMap(content);
        StringBuilder objectIDsListStr = new StringBuilder();
        String idSeparator = ", ";
        for (Map<String, Object> objectRecord : objectIDsList) {
            String objectIDStr = getStringParam(objectRecord.get("OBJECTID"));
            if (!objectIDStr.isEmpty()) {
                objectIDsListStr.append(objectIDStr).append(idSeparator);
            }
        }
        objectIDsListStr.setLength(objectIDsListStr.length() - idSeparator.length());
        logger.debug("Objects ids list = " + objectIDsListStr);

        return objectIDsListStr.toString();
    }

    @WsMethod(requiredParams = {"EXPORTDATAID"})
    public Map<String, Object> dsB2BExportDataBrowseByID(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // преобразование дат в числовые представления для выполнения запросов
        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(params, Double.class
        );

        // идентификатор обрабатываемой записи
        Long exportDataID = getLongParam(params.get("EXPORTDATAID"));
        logger.debug("Export data record id (EXPORTDATAID) = " + exportDataID);

        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = this.callService(B2BPOSWS, "dsB2BExportDataBrowseListByParam", params, login, password);

        // получение текста запроса из шаблона обрабатываемой записи
        Long templateID = getLongParam(result.get("TEMPLATEID"));
        logger.debug("Template id for this export data (TEMPLATEID) = " + templateID);
        Map<String, Object> template = getExportDataTemplateByID(templateID, login, password);

        ArrayList<Map<String, Object>> columnMapList = getColumnMapListFormExportDataTemplate(template);

        result.put("TEMPLATECOLUMNLIST", columnMapList);

        // преобразование дат в строковые представления для angular-интерфейса
        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        parseDates(result, String.class
        );

        return result;

    }

    private boolean isColumnNamesInComment(String selectDataQueryText) {
        return selectDataQueryText.indexOf("-=:@:=-") >= 0;
    }

    private ArrayList<String> getColumnNamesListFormDataQueryComment(String selectDataQueryText) {
        logger.debug("Analysing query comments:\n\n" + selectDataQueryText + "\n");

        int startIndex = 0;
        int finishIndex = 0;
        int endIndex = 0;
        boolean isRepeat = true;
        String startStr = "$#@";
        String finishStr = "@#$";

        ArrayList<String> result = new ArrayList<String>();
        do {
            logger.debug("Searching cayenne start key word...");
            startIndex = selectDataQueryText.indexOf(startStr, endIndex);
            finishIndex = selectDataQueryText.indexOf(finishStr, startIndex);
            //logger.debug("startIndex = " + startIndex);
            if (startIndex > 0) {
                logger.debug("Cayenne start key word found at position: " + startIndex);
                logger.debug("Cayenne finish key word found at position: " + finishIndex);
                endIndex = finishIndex + 5;
                logger.debug(String.format("Cayenne result fragment ends at position: %d", endIndex));
                String line = selectDataQueryText.substring(startIndex + startStr.length(), finishIndex);
                line = line.trim();
                logger.debug("line = " + line);
                //logger.debug("fragment = " + line);
                result.add(line);
            } else {
                logger.debug("No more cayenne start key word found.");
                isRepeat = false;
            }
        } while (isRepeat);

        logger.debug("Column names list generated from query text: " + result);

        return result;

    }

    protected List<Map<String, Object>> getContrSectionList(Map<String, Object> loadRes, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("CONTRID", loadRes.get("CONTRID"));
        Map<String, Object> contrSectRes = this.callServiceTimeLogged(B2BPOSWS, "dsB2BContractSectionBrowseListByParam", param, login, password);
        if (contrSectRes != null) {
            if (contrSectRes.get(RESULT) != null) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) contrSectRes.get(RESULT);
                return result;
            }
        }
        return null;
    }

    protected List<Map<String, Object>> loadHandbookData(Map<String, Object> params, String hbName, String login, String password) throws Exception {
        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("HANDBOOKNAME", hbName);
        hbParams.put("HANDBOOKDATAPARAMS", params);
        hbParams.put(RETURN_LIST_ONLY, true);
        Map<String, Object> resultMap = this.callServiceTimeLogged(B2BPOSWS, "dsB2BHandbookDataBrowseByHBName", hbParams, login, password);
        List<Map<String, Object>> resultList = WsUtils.getListFromResultMap(resultMap);
        return resultList;
    }

    protected void calcAgentPrem(Map<String, Object> loadRes, String login, String password) throws Exception {
        List<Map<String, Object>> contrSectionList = getContrSectionList(loadRes, login, password);
//            List<Map<String,Object>> agentTarifList = getAgentTariffList(loadRes, login, password);
        Map<String, Object> agentPramRes = getAgentPremByContrId(loadRes, login, password);
        Double sum = 0.0;
        Double tarif = 0.0;
        boolean isAgentPremCalculated = false;
        if (agentPramRes.get("AGENTCOMMISSID") != null) {
            if (agentPramRes.get("PREMVALUE") != null) {
                sum = getDoubleParam(agentPramRes.get("PREMVALUE"));
                if (sum > 0.01) {
                    Double contrPrem = getDoubleParam(loadRes.get("PREMVALUE"));
                    if (agentPramRes.get("TARIFF") != null) {
                        tarif = getDoubleParam(agentPramRes.get("TARIFF"));
                    } else {
                        tarif = sum / contrPrem;
                    }
                    isAgentPremCalculated = true;
                }
            }
        }
        if (!isAgentPremCalculated) {
            //дернуть расчет, сохранить результат, 
            Map<String, Object> calcParam = new HashMap<String, Object>();
            calcParam.put("CONTRID", loadRes.get("CONTRID"));
            Map<String, Object> calcRes = this.callService(Constants.B2BPOSWS, "dsB2BcallCalcAgentCommissPrem", calcParam, login, password);
            sum = getDoubleParam(calcRes.get("Result"));
            if (sum > 0.01) {
                Double contrPrem = getDoubleParam(loadRes.get("PREMVALUE"));
                if (calcRes.get("tariff") != null) {
                    tarif = getDoubleParam(calcRes.get("tariff"));
                } else {
                    tarif = sum / contrPrem;
                }
                isAgentPremCalculated = true;
            }
        }
//        if (!contrSectionList.isEmpty()) {
//            for (Map<String, Object> contrSection : contrSectionList) {
//                Double val = 0.0;
//                if (contrSection.get("PRODSTRUCTID") != null) {
//                    Long yearCount = getLongParam(loadRes.get("TERMYEARCOUNT"));
//                    if (yearCount == null) {
//                        yearCount = 0L;
//                    }
//                    int yearCountInt = yearCount.intValue();
//                    if ("50000".equals(getStringParam(contrSection.get("PRODSTRUCTID")))) {
//                        Map<String, Object> hbParams = new HashMap<String, Object>();
//                        hbParams.put("PAYVARID", loadRes.get("PAYVARID"));
//                        hbParams.put("TERMID", loadRes.get("TERMID"));
//                        List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.RightDec.AgentTarifParams", login, password);
//                        if (!filteredList.isEmpty()) {
//                            tarif = getDoubleParam(filteredList.get(0).get("COMMISSION"));
//                        }
//
//                        /* // единовременно
//                        if ("103".equals(getStringParam(loadRes.get("PAYVARID")))) {
//                        //Верное решение
//                            if (yearCountInt <= 9) {
//                                // 5 years
//                                tarif = 0.04;
//                            }
//                            if ((yearCountInt >= 10) && ((yearCountInt <= 19))) {
//                                // 5 years
//                                tarif = 0.06;
//                            }
//                            if ((yearCountInt >= 20) && ((yearCountInt <= 30))) {
//                                // 5 years
//                                tarif = 0.08;
//                            }                        
//                        } else {
//                            // в рассрочку
//                            if (yearCountInt <= 9) {
//                                // 5 years
//                                tarif = 0.4;
//                            }
//                            if ((yearCountInt >= 10) && ((yearCountInt <= 19))) {
//                                // 5 years
//                                tarif = 0.6;
//                            }
//                            if ((yearCountInt >= 20) && ((yearCountInt <= 30))) {
//                                // 5 years
//                                tarif = 0.7;
//                            }                                                    
//                        }*/
//                        val = getDoubleParam(contrSection.get("PREMVALUE")) * tarif;
//
//                    }
//                    if ("51000".equals(getStringParam(contrSection.get("PRODSTRUCTID")))) {
//                        Map<String, Object> hbParams = new HashMap<String, Object>();
//                        hbParams.put("PAYVARID", loadRes.get("PAYVARID"));
//                        hbParams.put("TERMID", loadRes.get("TERMID"));
//                        List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.Fstep.AgentTarifParams", login, password);
//                        if (!filteredList.isEmpty()) {
//                            tarif = getDoubleParam(filteredList.get(0).get("COMMISSION"));
//                        }
//                        /*// единовременно
//                        if ("103".equals(getStringParam(loadRes.get("PAYVARID")))) {
//                        //Первый шаг
//                            if (yearCountInt <= 9) {
//                                // 5 years
//                                tarif = 0.04;
//                            }
//                            if ((yearCountInt >= 10) && ((yearCountInt <= 19))) {
//                                // 5 years
//                                tarif = 0.06;
//                            }
//                            if ((yearCountInt >= 20) && ((yearCountInt <= 30))) {
//                                // 5 years
//                                tarif = 0.08;
//                            }                        
//                        } else {
//                            // в рассрочку
//                            if (yearCountInt <= 9) {
//                                // 5 years
//                                tarif = 0.4;
//                            }
//                            if ((yearCountInt >= 10) && ((yearCountInt <= 19))) {
//                                // 5 years
//                                tarif = 0.6;
//                            }
//                            if ((yearCountInt >= 20) && ((yearCountInt <= 30))) {
//                                // 5 years
//                                tarif = 0.7;
//                            }                                                    
//                        }*/
//                        val = getDoubleParam(contrSection.get("PREMVALUE")) * tarif;
//                    }
//                    if ("52000".equals(getStringParam(contrSection.get("PRODSTRUCTID")))) {
//                        //капитал
//                        Map<String, Object> hbParams = new HashMap<String, Object>();
//                        hbParams.put("CURRENCYID", loadRes.get("INSAMCURRENCYID"));
//                        hbParams.put("TERMID", loadRes.get("TERMID"));
//                        List<Map<String, Object>> filteredList = loadHandbookData(null, "B2B.Capital.AgentTarifParams", login, password);
//                        if (!filteredList.isEmpty()) {
//                            tarif = getDoubleParam(filteredList.get(0).get("COMMISSION"));
//                        }
//                        //tarif = 0.01;
//                        val = getDoubleParam(contrSection.get("PREMVALUE")) * tarif;
//                    }
//                    if ("53000".equals(getStringParam(contrSection.get("PRODSTRUCTID")))) {
//                        //Маяк
//                        Map<String, Object> hbParams = new HashMap<String, Object>();
//                        hbParams.put("CURRENCYID", loadRes.get("INSAMCURRENCYID"));
//                        hbParams.put("TERMID", loadRes.get("TERMID"));
//                        List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.Invest.AgentTarifParams", login, password);
//                        if (!filteredList.isEmpty()) {
//                            tarif = getDoubleParam(filteredList.get(0).get("COMMISSION"));
//                        }
//
//                        /* if ("1".equals(getStringParam(loadRes.get("INSAMCURRENCYID")))) {
//                            tarif = 0.10;
//                        } else {
//                            // валюта
//                            // рубли
//                            if ("1000".equals(getStringParam(loadRes.get("TERMID")))) {
//                                // 5 years
//                                tarif = 0.05;
//                            }
//                            if ("1002".equals(getStringParam(loadRes.get("TERMID")))) {
//                                // 7 years
//                                tarif = 0.07;
//                            }
//                            if ("1005".equals(getStringParam(loadRes.get("TERMID")))) {
//                                // 10 years
//                                tarif = 0.1;
//                            }
//                        }*/
//                        //маяк
//                        val = getDoubleParam(contrSection.get("PREMVALUE")) * tarif;
//                    }
//                }
//                sum = sum + val;
//            }
//        }
        loadRes.put("AGENTTARIF", tarif);
        Double agentNDS = roundSum((sum * 18) / 118);
        Double agentnoNDS = sum - agentNDS;
        loadRes.put("AGENTPREM", sum);
        loadRes.put("AGENTPREMNDS", agentNDS);
        loadRes.put("AGENTPREMNONDS", agentnoNDS);
    }

    protected String getFundStr(Map<String, Object> dataItem, String login, String password) throws Exception {
        Long fundHid = getLongParamLogged(dataItem, ".CONTREXTMAP.fund");
        String result = "";
        if (fundHid != null) {
            // используется справочник
            Map<String, Object> hbParams = new HashMap<String, Object>();
            hbParams.put("hid", fundHid);
            List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.InvestNum1.Funds", login, password);
            if ((filteredList != null) && (filteredList.size() == 1)) {
                Map<String, Object> selectedItem = filteredList.get(0);
                result = getStringParamLogged(selectedItem, "name");
            } else {
                // если по какой то причине справочник будет недоступен или тп - оставлена реализованная ранее разыменовка по константам, но с протоколированием ошибки
                logger.error("Can not get info from handbook 'B2B.InvestNum1.Funds' for CONTREXTMAP.fund resolving! Hardcoded values will be used instead!");
                String fundStr;
                // вести синхронно с $rootScope.baseActiveList из commonIns.js до создания справочника "B2B.InvestNum1.BaseActive"
                switch (fundHid.intValue()) {
                    case 1:
                        fundStr = "Глобальный фонд облигаций";
                        break;
                    case 2:
                        fundStr = "Недвижимость";
                        break;
                    case 3:
                        fundStr = "Новые технологии";
                        break;
                    case 4:
                        fundStr = "Золото";
                        break;
                    case 5:
                        fundStr = "Глобальный нефтяной сектор";
                        break;
                    default:
                        fundStr = "Неизвестный фонд";
                        break;
                }
                result = fundStr;
            }
        }
        return result;
    }

    private List<Map<String, Object>> getAgentTariffList(Map<String, Object> loadRes, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("CONTRID", loadRes.get("CONTRID"));
        // 1. получить агентский договор по текущему договору.
        // из содержимого агентского договора выбрать запись содержимого, с продуктом соответствующим текущему договору.
        // в этой записи calcverid калькулятора расчета комиссии агента.
        // по ид калькулятора получить данные по комиссии, (hbdataverid справочника с коеффициентами)
        // фильтронуть содержимое справочника, по ограничениям текущего договора, получив 1 запись - взять из нее тариф.
        Map<String, Object> contrSectRes = this.callServiceTimeLogged(B2BPOSWS, "dsB2BContractSectionBrowseListByParam", param, login, password);
        if (contrSectRes != null) {
            if (contrSectRes.get(RESULT) != null) {
                List<Map<String, Object>> result = (List<Map<String, Object>>) contrSectRes.get(RESULT);
                return result;
            }
        }
        return null;
    }

    private Map<String, Object> getAgentPremByContrId(Map<String, Object> loadRes, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("CONTRID", loadRes.get("CONTRID"));
        param.put("PARENTACIDISNULL", "TRUE");
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> contrSectRes = this.callServiceTimeLogged(B2BPOSWS, "dsB2BAgentCommissBrowseListByParamEx", param, login, password);
        return contrSectRes;
    }

    protected Long getConfigParam(String paramName) throws Exception {
        String serviceName = ConfigUtils.getProjectProperty("ru.diasoft.services.insurance.ServiceName");
        Config config = Config.getConfig(serviceName);
        Long result = 0L;
        String res = "";
        try {
            res = config.getParam(paramName, "");
        } catch (Exception ex) {
            throw new Exception(String.format("Ошибка получения смещения времени из конфига"), ex);
        }
        try {
            result = Long.valueOf(res);
        } catch (Exception ex) {
            result = 0L;
        }
        return result;
    }

    protected String getExportDataFilenameShiftedDateStr() throws Exception {
        Long timeShift = getConfigParam("BORDEROTIMESHIFT");
        Date now = new Date();
        Date shiftNow = new Date(now.getTime() + (timeShift * 60 * 60 * 1000));
        return EXPORT_DATA_FILENAME_DATE_FORMAT.format(shiftNow);
    }

    protected String getExportDataFilenameDateStr() throws Exception {
        Date now = new Date();
        return EXPORT_DATA_FILENAME_DATE_FORMAT.format(now);
    }

}
