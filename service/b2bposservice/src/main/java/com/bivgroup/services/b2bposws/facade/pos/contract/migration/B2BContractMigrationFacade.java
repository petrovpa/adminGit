/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.contract.migration;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import static com.bivgroup.services.b2bposws.facade.pos.contract.custom.B2BContractCustomFacade.participantNodes;
import com.bivgroup.services.b2bposws.system.Constants;
import edu.emory.mathcs.backport.java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("B2BContractMigration")
public class B2BContractMigrationFacade extends B2BBaseFacade {

    // список идентифкаторов версий продуктов, поддерживаемых миграцией
    // 1050 - Защита дома Онлайн
    // 1060 - Защита банковской карты Онлайн
    // 1070 - Страхование путешественников Онлайн
    // 1080 - Защита имущества сотрудников сбербанка Онлайн
    // 1090 - Страхование ипотеки Онлайн для клиентов ОАО «Сбербанк России»
    public static final String SUPPORTED_PRODVERID_LIST = "1050, 1060, 1070, 1080, 1090";
    
    // 1080 - Защита имущества сотрудников сбербанка Онлайн
    //public static final String SUPPORTED_PRODVERID_LIST = "1080"; //!только для отладки!

    // старые идентификаторы версий продуктов, используются для вызова универсального метода переноса из "продуктовых" методов
    // (для старых продуктов идентификаторы версии и конфигурации всегда равны: PRODVERID = PRODCONFID)
    public static final Long HIB_PRODVERID = 1050L;
    public static final Long CIB_PRODVERID = 1060L;
    public static final Long VZR_PRODVERID = 1070L;
    public static final Long SIS_PRODVERID = 1080L;
    public static final Long MORTGAGE_PRODVERID = 1090L;
    
    // системные имена типа объекта контракта, применяющиеся в системе состояний
    private static final String oldContractTypeSysName = "INS_CONTRACT";
    private static final String newContractTypeSysName = "B2B_CONTRACT";
    
    // идентификаторы типа объекта контракта, применяющиеся в системе состояний
    // (генерируется при первой необходимости по сведениям из БД и *ContractTypeSysName)
    private static long oldContractTypeID = 0;
    private static long newContractTypeID = 0;

    // соответствие системных имен состояний
    // (новое имя состояния, старое имя состояния)
    private static final int stateNameRelationsNewNameIndex = 0;
    private static final int stateNameRelationsOldNameIndex = 1;
    private static final String[][] stateNameRelations = {
        {"B2B_CONTRACT_DRAFT", "INS_CONTRACT_PREPARE"},
        {"B2B_CONTRACT_PREPRINTING", "INS_CONTRACT_TO_PAYMENT"},
        {"B2B_CONTRACT_SG", "INS_CONTRACT_PAID"},
        {"B2B_CONTRACT_REJECT", "INS_CONTRACT_REJECT"},
        {"B2B_CONTRACT_PAYTIMEOUT", "INS_CONTRACT_PAYTIMEOUT"},
        {"B2B_CONTRACT_UPLOADED_SUCCESFULLY", "INS_CONTRACT_UPLOADED_SUCCESFULLY"},
    };
    
    // соответствия имен старых состояний сведениями о новых состояниях
    // (ключ - системное имя старого состояния, значение - карта со сведениями о соответвующем старому новом состоянии)
    // (генерируются при первой необходимости по сведениям из БД и stateNameRelations)
    private static Map<String, Map<String, Object>> newStatesByOldStateSysName = null;

    // соответствия старых идентификаторов версий продукта сведениями о новых B2B-версиях этих продуктов
    // (ключ - старый идентификатор версии/конфигурации продукта (для старых продуктов идентификаторы версии и конфигурации всегда равны: PRODVERID = PRODCONFID),
    //  значение - карта со сведениями о новой B2B-версии эттого продукта)
    // (дополняются при первой необходимости в ходе создания первого нового договора по конкретному продукту)
    private static Map<Long, Map<String, Object>> newProductsByOldProdVerID = new HashMap<Long, Map<String, Object>>();

    protected void initStateMigrationRelations() throws Exception {

        // идентификаторы типа объекта контракта, применяющиеся в системе состояний
        if (oldContractTypeID == 0) {
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("SYSNAME", oldContractTypeSysName);
            oldContractTypeID = getLongParam(this.selectQueryAndGetOneValueFromFistItem("dsGetTypeIdBySysName", queryParams, "ID"));
            if (oldContractTypeID == 0) {
                oldContractTypeID = 1000L;
            }
        }
        if (newContractTypeID == 0) {
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("SYSNAME", newContractTypeSysName);
            newContractTypeID = getLongParam(this.selectQueryAndGetOneValueFromFistItem("dsGetTypeIdBySysName", queryParams, "ID"));
            if (newContractTypeID == 0) {
                newContractTypeID = 2000L;
            }
        }
        
        // соответствия имен старых состояний сведениями о новых состояниях
        if (newStatesByOldStateSysName == null) {
            newStatesByOldStateSysName = new HashMap<String, Map<String, Object>>();
            HashMap<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("TYPEID", newContractTypeID);
            // todo: выполнить один полный запрос и проанализорвать его результаты для формирования newStatesByOldStateSysName
            for (String[] stateNameRelation : stateNameRelations) {
                String newName = stateNameRelation[stateNameRelationsNewNameIndex];
                String oldName = stateNameRelation[stateNameRelationsOldNameIndex];
                queryParams.put("STATENAME", newName);
                try {
                    Map<String, Object> newState = this.selectQuery("dsStateBrowseListByParams", queryParams);
                    if (newState != null) {
                        newStatesByOldStateSysName.put(oldName, WsUtils.getFirstItemFromResultMap(newState));
                    }
                } catch (Exception ex) {
                    logger.error("Исключение при выполнении запроса dsStateBrowseListByParams(Count):\n", ex);
                }
            }
        }
        
    }
    
    private void migratePayData(Long contractId, Long newContractId, String login, String password) throws Exception {
        Map<String, Object> readParams = new HashMap<String, Object>();
        readParams.put("CONTRID", contractId);
        Map<String, Object> readRes = this.callService(Constants.INSPOSWS, "dsPaymentBrowseListByParam", readParams, login, password);
        List<Map<String, Object>> payList = WsUtils.getListFromResultMap(readRes);
        if (payList != null) {
            for (Map<String, Object> bean : payList) {
                Map<String, Object> saveParams = new HashMap<String, Object>();
                saveParams.putAll(bean);
                saveParams.put("CONTRID", newContractId);
                saveParams.remove("PAYID");
                this.callService(Constants.B2BPOSWS, "dsB2BPaymentCreate", saveParams, login, password);
            }
        }
    }

    private void migratePayFactData(Long contractNodeId, Long newContractNodeId, String login, String password) throws Exception {
        Map<String, Object> readParams = new HashMap<String, Object>();
        readParams.put("CONTRNODEID", contractNodeId);
        Map<String, Object> readRes = this.callService(Constants.INSPOSWS, "dsPaymentFactBrowseListByParam", readParams, login, password);
        List<Map<String, Object>> payFactList = WsUtils.getListFromResultMap(readRes);
        if (payFactList != null) {
            for (Map<String, Object> bean : payFactList) {
                Map<String, Object> saveParams = new HashMap<String, Object>();
                saveParams.putAll(bean);
                saveParams.put("CONTRNODEID", newContractNodeId);
                saveParams.remove("PAYFACTID");
                this.callService(Constants.B2BPOSWS, "dsB2BPaymentFactCreate", saveParams, login, password);
            }
        }
    }

    private void migrateContractBinFileData(Long contractId, Long newContractId, String login, String password) throws Exception {
        Map<String, Object> readParams = new HashMap<String, Object>();
        readParams.put("OBJID", contractId);
        Map<String, Object> readRes = this.callService(Constants.INSPOSWS, "dsContract_BinaryFile_BinaryFileBrowseListByParam", readParams, login, password);
        List<Map<String, Object>> binFileList = WsUtils.getListFromResultMap(readRes);
        if (binFileList != null) {
            for (Map<String, Object> bean : binFileList) {
                Map<String, Object> saveParams = new HashMap<String, Object>();
                saveParams.putAll(bean);
                saveParams.put("OBJID", newContractId);
                saveParams.remove("BINFILEID");
                saveParams.remove("OBJTABLENAME");
                this.callService(Constants.B2BPOSWS, "dsB2BContract_BinaryFile_createBinaryFileInfo", saveParams, login, password);
            }
        }
    }

    private boolean migrateContractStateData(Long newContractID, Long oldContractID, String oldStateSysName, String login, String password) throws Exception {
        
        logger.debug("Старт переноса сведений о состоянии договора...\n");
        boolean isNoErrors = true;
        
        initStateMigrationRelations();

        Map<String, Object> newContractState = (Map<String, Object>) newStatesByOldStateSysName.get(oldStateSysName);        
        
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("CONTRID", newContractID);
        Object newContractStateID = newContractState.get("STATEID");
        queryParams.put("STATEID", newContractStateID);
        try {
            this.updateQuery("dsB2BContractUpdateStateID", queryParams);
        } catch (Exception ex) {
            isNoErrors = false;
            logger.error("Исключение при выполнении запроса dsB2BContractUpdateStateID:\n", ex);
        }

        queryParams = new HashMap<String, Object>();
        queryParams.putAll(newContractState);
        queryParams.put("NEWCONTRID", newContractID);
        queryParams.put("OLDCONTRID", oldContractID);
        try {
            this.updateQuery("dsB2BObjStateUpdate", queryParams);
        } catch (Exception ex) {
            isNoErrors = false;
            logger.error("Исключение при выполнении запроса dsB2BObjStateUpdate:\n", ex);
        }
        
        queryParams = new HashMap<String, Object>();
        queryParams.put("CONTRID", oldContractID);
        queryParams.put("TYPEID", oldContractTypeID);
        Map<String, Object> oldHistoryQueryResult = this.selectQuery("dsStateHistoryBrowseListByContrAndTypeIDs", queryParams);
        List<Map<String, Object>> oldHistory = WsUtils.getListFromResultMap(oldHistoryQueryResult);
        List<Map<String, Object>> newHistory = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> oldRecord : oldHistory) {
            Map<String, Object> newRecord = new HashMap<String, Object>();
            newRecord.putAll(oldRecord);
            String oldStateName = oldRecord.get("STATENAME").toString();
            newRecord.putAll(newStatesByOldStateSysName.get(oldStateName));
            newRecord.remove("ID");
            newRecord.put("OBJID", newContractID);            
            newHistory.add(newRecord);
        }
        
        // при прямой вставке не будут сгенерированы идентификаторы БД
        //try {
        //    this.insertMassQuery("dsStateHistoryMassInsert", newStateHistoryRecords);
        //} catch (Exception ex) {
        //    logger.error("Исключение при выполнении запроса dsStateHistoryMassInsert:\n", ex);
        //}
        
        // для массовых вставок генерация идентификаторов БД по @IdGen в отдельном фасаде не предназначена?
        //Map<String, Object> callParams = new HashMap<String, Object>();
        //callParams.put("ROWS", newStateHistoryRecords);
        //this.callService(Constants.B2BPOSWS, "dsB2BContractStateHistoryMassCreate", callParams, login, password);
        
        for (Map<String, Object> historyRecord : newHistory) {
            Map<String, Object> recordCreateResult = this.callService(Constants.B2BPOSWS, "dsB2BContractStateHistoryRecordCreate", historyRecord, login, password);
            if (!isCallResultOK(recordCreateResult)) {
                isNoErrors = false;
                logger.error("Ошибка при вызове метода dsB2BContractStateHistoryRecordCreate:\n" + recordCreateResult.get("Error"));
            }
        }
        
        if (isNoErrors == true) {
            logger.debug("Перенос сведений о состоянии договора завершен без возникновения ошибок.\n");
        } else {
            logger.debug("В ходе переноса сведений о состоянии возникли ошибки.\n");
        }
        
        return isNoErrors;
        
    }    

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BCommonMigration(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        logger.debug("===================================================================");
        logger.debug("Старт переноса договоров...");
        
        long migrationTimer = (new Date()).getTime();

        // проверка на ключ подробного протоколирования
        boolean isVerboseLog = getBooleanParam(params.get("VERBOSELOG"), false);
        if (isVerboseLog) {
            logger.debug("Включен режим подробного протоколирования...");
        }

        // проверка на ключ агрессивной миграции
        boolean isPredatoryMigration = getBooleanParam(params.get("PREDATORYMIGRATION"), false);
        if (isPredatoryMigration) {
            logger.debug("Включен режим безусловного переноса (уже перенесенные версии переносимых договоров будут удалены)...");
        } else {
            logger.debug("Включен режим условного переноса (уже перенесенные договоры будут пропущены)...");
        }

        long maxFlockSize = getIntegerParam(params.get("MAXFLOCKSIZE"));
        if (maxFlockSize == 0) {
            //maxFlockSize = 9000L; 
            //maxFlockSize = Long.MAX_VALUE; 
            maxFlockSize = 1L;
        }
        Map<String, Object> result = new HashMap<String, Object>();
        
        Map<String, Object> contrParams = new HashMap<String, Object>();
        //contrParams.put("CONTRID", params.get("CONTRID"));
        //contrParams.put("EXTERNALID", params.get("EXTERNALID"));
        contrParams.putAll(params);
        contrParams.put("MAXPROCESSCONTRACT", params.get("MAXPROCESSCONTRACT"));
        contrParams.put("PRODVERLIST", params.get("PRODVERLIST"));
        contrParams.put("SUPPORTEDPRODVERIDLIST", SUPPORTED_PRODVERID_LIST);
        //contrParams.put("PRODVERID", CIB_PRODVERID);
        //Map<String, Object> contrRes = this.callService(Constants.INSPOSWS, "dsContractNABrowseListByParam", contrParams, login, password);
        //List<Map<String, Object>> migratingContracts = WsUtils.getListFromResultMap(contrRes);
        List<Map<String, Object>> migratingContracts = this.selectQueryAndGetListFromResultMap("dsContractIDsForMigrationBrowseListByParam", contrParams);

        // 
        Map<String, Object> product;
        long migratedContractsCount = 0;
        long processedContractsCount = 0;
        long skippedContractsCount = 0;
        long migratedWithErrorsContractsCount = 0;
        long migrateCandidatContractsCount = 0;
        long existingNewContractsCount = 0;
        long deletedNewContractsCount = 0;
        long maxContractCount = 0;
        
        if (migratingContracts != null) {
            migrateCandidatContractsCount = migratingContracts.size();
            if (maxFlockSize < migrateCandidatContractsCount) {
                maxContractCount = maxFlockSize;
            } else {
                maxContractCount = migrateCandidatContractsCount;
            }
        }
        
        if (migrateCandidatContractsCount > 0) {
            
            logger.debug("По указанным параметрам выбрано кандидатов на перенос: " + migrateCandidatContractsCount + ".");
            logger.debug("За один вызов будет перенесено не более: " + maxFlockSize + ".");
            
            // очистка сведений о продуктах при каждом новом запуске миграции
            // (в пределах одного запуска сведения продуктов по прежнему будут запрашиваться единожды для каждого переносимого продукта и запоминаться на всю операцию миграции)
            newProductsByOldProdVerID.clear();
            
            if ((isVerboseLog) && (!((maxFlockSize == 1) || (migrateCandidatContractsCount == 1)))) {
                logger.debug("Режим подробного протоколирования отключен (не предназначен для использования при переносе более одного договора за один вызов метода)...");
                isVerboseLog = false;
            }
            
            for (Map<String, Object> bean : migratingContracts) {

                logger.debug("-------------------------------------------------------------------\n");

                if (migratedContractsCount >= maxFlockSize) {
                    logger.debug("Достигнуто предельное число договоров, переносимых за один вызов метода (" + maxFlockSize + ").\n");
                    break;
                }
 
                processedContractsCount++;
                logger.debug("Начата обработка " + processedContractsCount + "-го кандидата из " + migrateCandidatContractsCount + ".\n");
                
                //Long contractId = Long.valueOf(bean.get("CONTRID").toString());
                long oldContractID = getLongParam(bean.get("CONTRID"));
                //Long contractNodeId = Long.valueOf(bean.get("CONTRNODEID").toString()); // см. ниже

                //String contractExternalID = getStringParam(bean.get("EXTERNALID"));
                //String contractNumber = getStringParam(bean.get("CONTRNUMBER"));
                Object oldContractExternalID = bean.get("EXTERNALID");
                Object oldContractNumber = bean.get("CONTRNUMBER");

                Map<String, Object> getExistingNewContractsParams = new HashMap<String, Object>();
                getExistingNewContractsParams.put("EXTERNALID", oldContractExternalID);
                getExistingNewContractsParams.put("CONTRNUMBER", oldContractNumber);
                List<Map<String, Object>> existingNewContracts = this.callServiceAndGetListFromResultMap(Constants.B2BPOSWS, "dsB2BContractBrowseListByParam", getExistingNewContractsParams, login, password);
                if (existingNewContracts != null) {
                    existingNewContractsCount = existingNewContracts.size();
                } else {
                    existingNewContractsCount = 0;
                }
                
                boolean isContractMigratedWithErrors = false;

                if (existingNewContractsCount > 0) {
                    if (isPredatoryMigration) {
                        if (existingNewContractsCount == 1) {
                            logger.debug("Для переносимого договора c CONTRID = '" + oldContractID + "' уже существует перенесенная версия - она будет удалена, а договор будет перенесен повторно.\n");
                        } else {
                            logger.debug("Для переносимого договора c CONTRID = '" + oldContractID + "' уже существуют перенесенные версии (" + existingNewContractsCount + ") - они будет удалены, а договор будет перенесен повторно.\n");
                        }
                        boolean isDeleteFailed = false;
                        // todo: удаление существующих договоров
                        for (Map<String, Object> existingNewContract : existingNewContracts) {
                            Object existingNewContractID = existingNewContract.get("CONTRID");
                            logger.debug("Удаление перенесенного ранее договора c CONTRID = '" + existingNewContractID + "'...");
                            Map<String, Object> deletingParams = new HashMap<String, Object>();
                            deletingParams.put("CONTRID", existingNewContractID);
                            deletingParams.put(RETURN_AS_HASH_MAP, true);
                            logger.debug("Запрос дополнительных сведений для удаления перенесенного ранее договора...");
                            Map<String, Object> contractForDeleting = null;
                            try {
                                contractForDeleting = this.callService(Constants.B2BPOSWS, "dsB2BContractUniversalLoad", deletingParams, isVerboseLog, login, password);
                            } catch (Exception ex) {
                                isDeleteFailed = true;
                                logger.error("При запросе дополнительных сведений для удаления перенесенного ранее договора возникло исключение: ", ex);
                            }
                            if (isCallResultOK(contractForDeleting)) {
                                try {
                                    logger.debug("Запрос дополнительных сведений для удаления перенесенного ранее договора завершен без возникновения ошибок.");
                                    logger.debug("Удаление дополнительных сведений перенесенного ранее договора...");
                                    
                                    // удаление на уровне договора в dsB2BContractUniversalSave на данный момент не реализовано,
                                    // поэтому чтобы договор был обработан (с удалением всех дочерних сущностей) - помечаем его неизменившимся, а все удаляемые сущности - удаляемыми
                                    // (договор, помеченный удаляемым в текущей версии dsB2BContractUniversalSave не обрабатывается вовсе, пропускается полностью)
                                    markAllMapsByKeyValue(contractForDeleting, ROWSTATUS_PARAM_NAME, DELETED_ID);
                                    contractForDeleting.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                                    // участники помечаются отдельно (в CRM используется другой ключ для статуса модификации)
                                    for (String participantNode : participantNodes) {
                                        String dataKeyName = participantNode + "MAP";
                                        //String idKeyName = participantNode + "ID";
                                        Object participant = contractForDeleting.get(dataKeyName);
                                        if (participant != null) {
                                            markAllMapsByKeyValue(participant, FLAG_PARAM_NAME, FLAG_DEL);
                                        }
                                    }
                                    // перобразование всех дат в числовой формат для корректной работы с БД в dsB2BContractUniversalSave
                                    parseDates(contractForDeleting, Date.class);
                                    
                                    List<Map<String, Object>> contractsForDeleting = new ArrayList<Map<String, Object>>();
                                    contractsForDeleting.add(contractForDeleting);
                                    Map<String, Object> deleteParams = new HashMap<String, Object>();
                                    deleteParams.put("CONTRLIST", contractsForDeleting);
                                    Map<String, Object> delResult = this.callService(Constants.B2BPOSWS, "dsB2BContractUniversalSave", deleteParams, isVerboseLog, login, password);
                                    if (isCallResultOK(delResult)) {
                                        logger.debug("Дополнительные сведения перенесенного ранее договора удалены без возникновения ошибок.");
                                    } else {
                                        isDeleteFailed = true;
                                        logger.error("Ошибка при вызове метода dsB2BContractUniversalSave:\n" + delResult.get("Error"));
                                    }
                                    logger.debug("Удаление основных данных перенесенного ранее договора...");
                                    delResult = this.callService(Constants.B2BPOSWS, "dsB2BContractDelete", deletingParams, isVerboseLog, login, password);
                                    if (isCallResultOK(delResult)) {                                        
                                        logger.debug("Основные сведения перенесенного ранее договора удалены без возникновения ошибок.\n");
                                    } else {
                                        isDeleteFailed = true;
                                        logger.error("Ошибка при вызове метода dsB2BContractDelete:\n" + delResult.get("Error"));
                                    }
                                } catch (Exception ex) {
                                    isDeleteFailed = true;
                                    logger.error("При удалении сведений перенесенного ранее договора возникло исключение: ", ex);
                                }
                            } else {
                                isDeleteFailed = true;
                                logger.error("Ошибка при вызове метода dsB2BContractUniversalLoad:\n" + contractForDeleting.get("Error"));
                            }
                            //break; // !только для отладки!
                        }
                        if (isDeleteFailed) {
                            logger.error("В ходе удаления перенесенной ранее версии договора возникли ошибками - повторный перенос договора выполнен не будет.\n");
                            isContractMigratedWithErrors = true;
                            skippedContractsCount++;
                            continue;
                        } else {
                            logger.debug("Удаление перенесенной ранее версии договора завершено без возникновения ошибок.\n");
                            deletedNewContractsCount++;
                        }
                    } else {
                        logger.debug("Для переносимого договора c CONTRID = '" + oldContractID + "' уже существует как минимум одна перенесенная версия - договор пропущен.\n");
                        skippedContractsCount++;
                        continue;
                    }
                }
                
                // чтение данных старого договора
                long oldProdVerID = getLongParam(bean.get("PRODVERID"));
                Map<String, Object> oldContract = getOldContract(oldContractID, oldProdVerID, login, password);

                // сохранение данных старого договора в новый B2B-договор 
                if (oldContract != null) {
                    
                    String oldStateSysName = getStringParam(oldContract.get("STATESYSNAME"));
                    long oldContractNodeId = getLongParam(oldContract.get("CONTRNODEID"));
                    long oldContractProdVerId = getLongParam(oldContract.get("PRODVERID"));
                    
                    if (isVerboseLog) {
                        result.put("OLDCONTRACT", oldContract);
                    }
                    
                    logger.debug("Данные переносимого договора успешно загружены.\n");
                    try {
                        logger.debug("Сохранение договора в новую БД...\n");
                        Map<String, Object> saveParams = new HashMap<String, Object>();
                        saveParams.putAll(oldContract);
                        // если сведения продукта были запомнены ниже ранее - включить их в параметры вызова dsContractCreateInB2BModeEx, чтоб избежать повторных запросов данных продукта
                        product = newProductsByOldProdVerID.get(oldContractProdVerId);
                        if (product != null) {
                            saveParams.put("PRODCONF", product);
                        }
                        //<editor-fold defaultstate="collapsed" desc="! только для отладки !">
                        //saveParams.put("NOTE", "Проверка переноса договоров (договор перенесен " + (new Date()).toString() + ")"); // ! только для отладки !
                        saveParams.put("VERBOSELOG", params.get("VERBOSELOG")); // ! только для отладки !
                        //</editor-fold>
                        saveParams.put("ISMIGRATION", true); // флаг миграции (если выставлен в true, то в dsContractCreateInB2BModeEx не будет выполнятся безусловное перевычисление сумм, дат и пр. перед сохранением договора)
                        saveParams.put(RETURN_AS_HASH_MAP, true);
                        Map<String, Object> saveRes = this.callService(Constants.BIVSBERPOSWS, "dsContractCreateInB2BModeEx", saveParams, isVerboseLog, login, password);
                        
                        if ((isCallResultOK(saveRes)) && (saveRes.get("CONTRID") != null)) {

                            Long newContractId = getLongParam(saveRes.get("CONTRID"));
                            Long newContractNodeId = getLongParam(saveRes.get("CONTRNODEID"));

                            if (newContractId != null) {

                                // сведения продукта запоминаются, чтоб избежать повторных запросов данных продукта
                                if (product == null) {
                                    //product = (Map<String, Object>) saveRes.get("PRODCONF");
                                    //newProductsByOldProdVerID.put(contractProdVerId, product);
                                    newProductsByOldProdVerID.put(oldContractProdVerId, (Map<String, Object>) saveRes.get("PRODCONF"));
                                }
                                
                                // перенос данных платежей и приложенных файлов
                                migratePayData(oldContractID, newContractId, login, password);
                                if (newContractNodeId != null) {
                                    migratePayFactData(oldContractNodeId, newContractNodeId, login, password);
                                } else {
                                    isContractMigratedWithErrors = true;
                                    logger.debug("Для переносимого договора c CONTRID = '" + oldContractID + "' не удалось определить новый CONTRNODEID перенесенного договора.\n");
                                }
                                migrateContractBinFileData(oldContractID, newContractId, login, password);

                                // перенос сведений о состоянии, истории состояний
                                if (oldStateSysName.isEmpty()) {
                                    isContractMigratedWithErrors = true;
                                    logger.debug("Для переносимого договора c CONTRID = '" + oldContractID + "' не удалось определить имя текущего состояния - состояния перенесенного договора установлены не будет.\n");
                                } else {
                                    migrateContractStateData(newContractId, oldContractID, oldStateSysName, login, password);
                                }

                                if (isVerboseLog) {
                                    result.put("PREPARINGPROCESSLOG", saveRes.get("PREPARINGPROCESSLOG"));
                                    //Map<String, Object> readParams = new HashMap<String, Object>();
                                    //Map<String, Object> contrMap = new HashMap<String, Object>();
                                    //contrMap.put("contrId", newContractId);
                                    ////contrMap.put("prodConfId", newProductsByOldProdVerID.get(oldContractProdVerId).get("PRODCONFID"));
                                    //contrMap.put("prodConfId", oldContractProdVerId);
                                    //readParams.put("CONTRMAP", contrMap);
                                    //readParams.put("USEB2B", "true");
                                    //Map<String, Object> createdContract = (Map<String, Object>) this.callServiceAndGetOneValue(Constants.BIVSBERPOSWS, "dsContractBrowseEx", readParams, login, password, "CONTRMAP");
                                    //result.put("NEWCONTRACT", createdContract);
                                }
                                
                                migratedContractsCount++;
                                logger.debug("Договор c CONTRID = '" + oldContractID + "' перенесен без возникновения ошибок, перенесенный договор в новой БД имеет CONTRID = '" + newContractId + "'.\n");
                                logger.debug("Завершен перенос " + migratedContractsCount + "-го договора из " + maxContractCount + ".\n");


                            } else {
                                isContractMigratedWithErrors = true;
                                logger.debug("Для переносимого договора c CONTRID = '" + oldContractID + "' не удалось определить новый CONTRID перенесенного договора - дополнительные сведения (платежи, состояния, файлы) перенесены не будут.\n");
                            }

                        } else {
                            isContractMigratedWithErrors = true;
                            logger.debug("Переносиммый договор c CONTRID = '" + oldContractID + "' не удалось сохранить в новую БД.\n");
                        }
                    } catch (Exception ex) {
                        isContractMigratedWithErrors = true;
                        logger.debug("При переносе договора с CONTRID = '" + oldContractID + "' возникло исключение: ", ex);
                    }
                    
                    // !!! пока только один договор
                    //break;
                } else {
                    isContractMigratedWithErrors = true;
                    logger.debug("Не удалось получить данные переносимого договора c CONTRID = '" + oldContractID + "'.\n");
                }
                
                if (isContractMigratedWithErrors) {
                    migratedWithErrorsContractsCount++;
                }
            }
        } else {
            logger.debug("По указанным параметрам не найдено ни одного договора для переноса.");
            //logger.debug("-------------------------------------------------------------------\n");
        }
        
        migrationTimer = (new Date()).getTime() - migrationTimer;
        float migrationTimerHours = (float) ((int)(((float) migrationTimer/1000/60/60) * 1000)) / 1000;
        
        logger.debug("-------------------------------------------------------------------");
        logger.debug("Перенос договоров завершен.");
        logger.debug("На перенос затрачено: " + migrationTimer + " мс. (примерно " + migrationTimerHours + " ч.).");
        logger.debug("Кандидатов на перенос: " + migrateCandidatContractsCount + ".");
        logger.debug("Кандидатов проверено: " + processedContractsCount + ".");
        logger.debug("Договоров перенесено: " + migratedContractsCount + ".");
        logger.debug("Договоров пропущено: " + skippedContractsCount + ".");
        logger.debug("Удалено ранее перенесенных договоров: " + deletedNewContractsCount + ".");
        logger.debug("Число договоров, при переносе которых возникали ошибки: " + migratedWithErrorsContractsCount + ".");
        logger.debug("===================================================================\n");
        
        return result;
    }

    // чтение данных старого договора
    protected Map<String, Object> getOldContract(long contractId, long contractProdVerID, String login, String password) {
        logger.debug("Загрузка сведений переносимого договора...\n");
        Map<String, Object> oldContract = null;
        try {
            // подготовка параметров запроса
            Map<String, Object> getParams = new HashMap<String, Object>();
            String methodName = "dsContractBrowseEx";
            String contractDataKeyName = "CONTRMAP";
            Map<String, Object> contrMap = new HashMap<String, Object>();
            getParams.put("CONTRID", contractId);
            contrMap.put("contrId", contractId);
            getParams.put("CONTRMAP", contrMap);
            if ((contractProdVerID == HIB_PRODVERID) || (contractProdVerID == CIB_PRODVERID)) {
                methodName = "dsContractBrowseEx";
                contractDataKeyName = "CONTRMAP";
            } else if (contractProdVerID == MORTGAGE_PRODVERID) {
                methodName = "dsMortgageContractBrowseEx";
                contractDataKeyName = "";
            } else if (contractProdVerID == VZR_PRODVERID) {
                methodName = "dsTravelContractBrowseEx";
                contractDataKeyName = "CONTRMAP"; // todo: проверить
            } else if (contractProdVerID == SIS_PRODVERID) {
                methodName = "dsSisContractBrowseEx";
                contractDataKeyName = "CONTRMAP"; // todo: проверить
            }
            getParams.put("USEB2B", "false");
            
            // запрос сведений старого договора
            if (contractDataKeyName.isEmpty()) {
                getParams.put(RETURN_AS_HASH_MAP, true);
                oldContract = (Map<String, Object>) this.callService(Constants.BIVSBERPOSWS, methodName, getParams, login, password);
            } else {
                oldContract = (Map<String, Object>) this.callServiceAndGetOneValue(Constants.BIVSBERPOSWS, methodName, getParams, login, password, contractDataKeyName);
            }
            
            // удаление идентификаторов участников, иначе по их наличию при создании договора будет выбрано обновление существующего участника вместо создания нового
            List<String> participantNodeNames = new ArrayList<String>(Arrays.asList(participantNodes));
            participantNodeNames.add("INSURED"); // в старых договорах узел страхователя называется по другому
            for (String participantNode : participantNodeNames) {
                String dataKeyName = participantNode + "MAP";
                String idKeyName = participantNode + "ID";
                oldContract.remove(idKeyName);
                Object participantObj = oldContract.get(dataKeyName);
                if ((participantObj != null) && (participantObj instanceof Map)) {
                    Map<String, Object> participant = (Map<String, Object>) participantObj;
                    participant.remove("PARTICIPANTID");
                    participant.remove("PERSONID");
                }
            }
        } catch (Exception ex) {
            logger.debug("При загрузке переносимого договора с CONTRID = '" + contractId + "' возникло исключение: ", ex);
        }
        return oldContract;
    }
    
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BCIBMigration(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        params.put("PRODVERID", CIB_PRODVERID);
        return this.callService(Constants.B2BPOSWS, "dsB2BCommonMigration", params, login, password);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BHIBMigration(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        params.put("PRODVERID", HIB_PRODVERID);
        return this.callService(Constants.B2BPOSWS, "dsB2BCommonMigration", params, login, password);
    }
    
    //@WsMethod(requiredParams = {})
    //public Map<String, Object> dsB2BHIBMigration(Map<String, Object> params) throws Exception {
    //    String login = params.get(WsConstants.LOGIN).toString();
    //    String password = params.get(WsConstants.PASSWORD).toString();
    //    Map<String, Object> contrParams = new HashMap<String, Object>();
    //    contrParams.put("PRODVERID", HIB_PRODVERID);
    //    Map<String, Object> contrRes = this.callService(Constants.INSPOSWS, "dsContractNABrowseListByParam", contrParams, login, password);
    //    if ((contrRes != null) && (contrRes.get(RESULT) != null) && (((List<Map<String, Object>>) contrRes.get(RESULT)).size() > 0)) {
    //        List<Map<String, Object>> contractList = (List<Map<String, Object>>) contrRes.get(RESULT);
    //        for (Map<String, Object> bean : contractList) {
    //            Long contractId = Long.valueOf(bean.get("CONTRID").toString());
    //            Long contractNodeId = Long.valueOf(bean.get("CONTRNODEID").toString());
    //            Map<String, Object> readParams = new HashMap<String, Object>();
    //            readParams.put("CONTRID", contractId);
    //            readParams.put("USEB2B", "false");
    //            Map<String, Object> oldFormatRes = this.callService(Constants.BIVSBERPOSWS, "dsContractBrowseEx", readParams, login, password);
    //            Map<String, Object> oldFormatMap = WsUtils.getFirstItemFromResultMap(oldFormatRes);
    //            if (oldFormatMap != null) {
    //                Map<String, Object> saveParams = new HashMap<String, Object>();
    //                saveParams.put("ReturnAsHashMap", "TRUE");
    //                saveParams.put("CONTRMAP", oldFormatMap);
    //                Map<String, Object> saveRes = this.callService(Constants.B2BPOSWS, "dsContractCreateEx", saveParams, login, password);
    //                if (saveRes != null) {
    //                    Long newContractId = Long.valueOf(saveRes.get("CONTRID").toString());
    //                    Long newContractNodeId = Long.valueOf(saveRes.get("CONTRNODEID").toString());
    //                    migratePayData(contractId, newContractId, login, password);
    //                    migratePayFactData(contractNodeId, newContractNodeId, login, password);
    //                    migrateContractBinFileData(contractId, newContractId, login, password);
    //                }
    //                // !!! пока только один договор
    //                break;
    //            }
    //        }
    //    }
    //    Map<String, Object> result = new HashMap<String, Object>();
    //    return result;
    //}
    
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMortgageMigration(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        params.put("PRODVERID", MORTGAGE_PRODVERID);
        return this.callService(Constants.B2BPOSWS, "dsB2BCommonMigration", params, login, password);
    }

    //@WsMethod(requiredParams = {})
    //public Map<String, Object> dsB2BMortgageMigration(Map<String, Object> params) throws Exception {
    //    String login = params.get(WsConstants.LOGIN).toString();
    //    String password = params.get(WsConstants.PASSWORD).toString();
    //    Map<String, Object> contrParams = new HashMap<String, Object>();
    //    contrParams.put("PRODVERID", VZR_PRODVERID);
    //    Map<String, Object> contrRes = this.callService(Constants.INSPOSWS, "dsContractNABrowseListByParam", contrParams, login, password);
    //    if ((contrRes != null) && (contrRes.get(RESULT) != null) && (((List<Map<String, Object>>) contrRes.get(RESULT)).size() > 0)) {
    //        List<Map<String, Object>> contractList = (List<Map<String, Object>>) contrRes.get(RESULT);
    //        for (Map<String, Object> bean : contractList) {
    //            Long contractId = Long.valueOf(bean.get("CONTRID").toString());
    //            Long contractNodeId = Long.valueOf(bean.get("CONTRNODEID").toString());
    //            Map<String, Object> readParams = new HashMap<String, Object>();
    //            readParams.put("CONTRID", contractId);
    //            readParams.put("USEB2B", "false");
    //            Map<String, Object> oldFormatRes = this.callService(Constants.BIVSBERPOSWS, "dsMortgageContractBrowseEx", readParams, login, password);
    //            Map<String, Object> oldFormatMap = WsUtils.getFirstItemFromResultMap(oldFormatRes);
    //            if (oldFormatMap != null) {
    //                Map<String, Object> saveParams = new HashMap<String, Object>();
    //                saveParams.put("ReturnAsHashMap", "TRUE");
    //                saveParams.put("OBJMAP", oldFormatMap);
    //                Map<String, Object> saveRes = this.callService(Constants.BIVSBERPOSWS, "dsMortgageContractCreateEx", saveParams, login, password);
    //                if (saveRes != null) {
    //                    Long newContractId = Long.valueOf(saveRes.get("CONTRID").toString());
    //                    Long newContractNodeId = Long.valueOf(saveRes.get("CONTRNODEID").toString());
    //                    migratePayData(contractId, newContractId, login, password);
    //                    migratePayFactData(contractNodeId, newContractNodeId, login, password);
    //                }
    //                // !!! пока только один договор
    //                break;
    //            }
    //        }
    //    }
    //    Map<String, Object> result = new HashMap<String, Object>();
    //    return result;
    //}
    
    //@WsMethod(requiredParams = {})
    //public Map<String, Object> dsB2BVZRMigration(Map<String, Object> params) throws Exception {
    //    String login = params.get(WsConstants.LOGIN).toString();
    //    String password = params.get(WsConstants.PASSWORD).toString();
    //    Map<String, Object> contrParams = new HashMap<String, Object>();
    //    contrParams.put("PRODVERID", VZR_PRODVERID);
    //    Map<String, Object> contrRes = this.callService(Constants.INSPOSWS, "dsContractNABrowseListByParam", contrParams, login, password);
    //    if ((contrRes != null) && (contrRes.get(RESULT) != null) && (((List<Map<String, Object>>) contrRes.get(RESULT)).size() > 0)) {
    //        List<Map<String, Object>> contractList = (List<Map<String, Object>>) contrRes.get(RESULT);
    //        for (Map<String, Object> bean : contractList) {
    //            Long contractId = Long.valueOf(bean.get("CONTRID").toString());
    //            Long contractNodeId = Long.valueOf(bean.get("CONTRNODEID").toString());
    //            Map<String, Object> readParams = new HashMap<String, Object>();
    //            readParams.put("CONTRID", contractId);
    //            readParams.put("USEB2B", "false");
    //            Map<String, Object> oldFormatRes = this.callService(Constants.BIVSBERPOSWS, "dsTravelContractBrowseEx", readParams, login, password);
    //            Map<String, Object> oldFormatMap = WsUtils.getFirstItemFromResultMap(oldFormatRes);
    //            if (oldFormatMap != null) {
    //                Map<String, Object> saveParams = new HashMap<String, Object>();
    //                saveParams.put("ReturnAsHashMap", "TRUE");
    //                saveParams.put("CONTRMAP", oldFormatMap);
    //                Map<String, Object> saveRes = this.callService(Constants.BIVSBERPOSWS, "dsContractCreateEx", saveParams, login, password);
    //                if (saveRes != null) {
    //                    Long newContractId = Long.valueOf(saveRes.get("CONTRID").toString());
    //                    Long newContractNodeId = Long.valueOf(saveRes.get("CONTRNODEID").toString());
    //                    migratePayData(contractId, newContractId, login, password);
    //                    migratePayFactData(contractNodeId, newContractNodeId, login, password);
    //                }
    //                // !!! пока только один договор
    //                break;
    //            }
    //        }
    //    }
    //    Map<String, Object> result = new HashMap<String, Object>();
    //    return result;
    //}
    
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BVZRMigration(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        params.put("PRODVERID", VZR_PRODVERID);
        return this.callService(Constants.B2BPOSWS, "dsB2BCommonMigration", params, login, password);
    }    

}
