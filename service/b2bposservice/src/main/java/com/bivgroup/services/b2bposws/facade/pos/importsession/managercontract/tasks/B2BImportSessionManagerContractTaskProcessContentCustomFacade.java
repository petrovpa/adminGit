package com.bivgroup.services.b2bposws.facade.pos.importsession.managercontract.tasks;

import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryCaller;
import com.bivgroup.services.b2bposws.facade.pos.importsession.common.*;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;

import static com.bivgroup.services.b2bposws.facade.pos.importsession.common.ProcessingState.NOT_PROCESSED;
import static com.bivgroup.services.b2bposws.facade.pos.importsession.common.ProcessingState.PROCESSED_SUCCESSFULLY;
import static com.bivgroup.services.b2bposws.facade.pos.importsession.common.ProcessingState.PROCESSED_WITH_ERROR;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BImportSessionManagerContractTaskProcessContentCustom")
public class B2BImportSessionManagerContractTaskProcessContentCustomFacade extends B2BImportSessionTaskProcessContentBaseFacade {

    protected static final String CONTRACT_ORG_STRUCT_ID_PARAMNAME = "CONTRORGSTRUCTID";
    /** Заблокировать все существующие права на договор */
    protected static final String CONTRACT_RIGHTS_BLOCK_METHOD_NAME = "dsB2BContractCustom_OrgStruct_blockOrgStructInfo";
    /** Создать права на договор */
    protected static final String CONTRACT_RIGHTS_CREATE_METHOD_NAME = "dsB2BContractCustom_OrgStruct_createOrgStructInfo";

    /** события создания связей */
    protected static final List<String> CONTRACT_RIGHTS_PROCESSING_CREATION_EVENTS = Arrays.asList(
            // ФТ v1.0: "При создании связи КМ-договор, должна быть создана связь Группа-договор"
            // ФТ v1.0: "создание связи группа-договор выполняется однократно"
            // ФТ v1.0: "На первом этапе всех договоров определена группа СБ1"
            IMPORT_SESSION_EVENT_GROUP_TO_CONTRACT_CREATED_ENTITY_NAME,
            // ФТ v1.0: "4. Проверяется наличие связи договора с другим КМ в БД Фронта"
            // ФТ v1.0: "4.1. Если связи договора с другим КМ нет:"
            // ФТ v1.0: "Запись в протокол (код 041)."
            // событие создания связи менеджер-договор
            IMPORT_SESSION_EVENT_MANAGER_TO_CONTRACT_CREATED_ENTITY_NAME,
            // ФТ v1.0: "5.2. Проверяется наличие связи договора с другим ВСП в БД Фронта"
            // ФТ v1.0: "5.2.1. Если связи договора с другим ВСП нет:"
            // ФТ v1.0: "Запись в протокол (код 043)."
            // событие создания связи подразделение-договор
            IMPORT_SESSION_EVENT_DEPARTMENT_TO_CONTRACT_CREATED_ENTITY_NAME
    );

    /** ФТ v1.0: "На первом этапе всех договоров определена группа СБ1" */
    protected static final String CONTRACT_DEFAULT_GROUP_SYSNAME = DEFAULT_GROUP_SYSNAME;

    /** Количество потоков обработки (не должно превышать одного потока) */
    private static volatile int threadCount = 0;
    /** Подробные сведения о текущем состоянии импорта */
    private static volatile ImportSessionTaskDetails taskDetails = new ImportSessionTaskDetails();

    private ImportSessionTaskOptions importSessionTaskOptions = new ImportSessionTaskOptions(
            // Имя сущности обрабатываемой сессии импорта
            IMPORT_SESSION_MANAGER_CONTRACT_ENTITY_NAME,
            // Системное наименование состояния обрабатываемых сессий импорта
            B2B_IMPORTSESSION_INPROCESSQUEUE,
            // Системное наименование перехода в случае ошибке в ходе обработки содержимого сессии импорта
            B2B_IMPORTSESSION_FROM_INPROCESSQUEUE_TO_ERROR,
            // Системное наименование перехода в случае успешного завершения обработки содержимого сессии импорта
            B2B_IMPORTSESSION_FROM_INPROCESSQUEUE_TO_SUCCESS,
            // Имя сущности содержимого обрабатываемой сессии импорта
            IMPORT_SESSION_CONTENT_MANAGER_CONTRACT_ENTITY_NAME
    );

    @Override
    protected ImportSessionTaskOptions getImportSessionTaskOptions() {
        return importSessionTaskOptions;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionManagerContractTaskProcessContentGetDetails(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerContractTaskProcessContentGetDetails start...");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = taskDetails.toMap();
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        logger.debug("dsB2BImportSessionManagerContractTaskProcessContentGetDetails finished.");
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionManagerContractTaskProcessContent(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerContractTaskProcessContent start...");
        Map<String, Object> result = new HashMap<String, Object>();
        if (threadCount == 0) {
            threadCount = 1;
            taskDetails.clear();
            try {
                result = doImportSessionListProcess(params, taskDetails);
            } finally {
                threadCount = 0;
                taskDetails.markFinish();
            }
        } else {
            logger.debug("doImportSessionListProcess already run!");
        }
        logger.debug("dsB2BImportSessionManagerContractTaskProcessContent finished.");
        return result;
    }

    protected Map<String, Object> doImportSessionProcess(Map<String, Object> importSession, Map<String, Object> params, ImportSessionTaskDetails taskDetails) throws ImportSessionException {

        logger.debug("doImportSessionProcess...");
        loggerDebugPretty(logger, "doImportSessionProcess importSession", importSession);
        // todo: логин и пароль для дс-вызовов в другое место
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        // формирование результат
        HashMap<String, Object> processResult = new HashMap<>();
        String importSessionEntityName = getImportSessionTaskOptions().getImportSessionEntityName();
        processResult.put(importSessionEntityName, importSession);
        // ИД сессии импорта
        Long importSessionId = getImportSessionId(importSession);
        // создание рез. копий данных таблиц, которые могут быть необратимо изменены в ходе импорта
        doImportAffectedTablesDataLogs(importSessionId);
        // инициализация мап справочников (для быстрого доступа к ИД для установки ссылок)
        ImportSessionClassifierPack importSessionClassifierPack = new ImportSessionClassifierPack();
        // ФТ v1.0: "При создании связи КМ-договор, должна быть создана связь Группа-договор"
        // ФТ v1.0: "создание связи группа-договор выполняется однократно"
        // ФТ v1.0: "На первом этапе всех договоров определена группа СБ1"
        Long contractGroupId = getGroupId(CONTRACT_DEFAULT_GROUP_SYSNAME, login, password);
        // получение ИД записей, подлежащих обработке
        Set<Long> contentIdSet = getImportSessionContentForProcessedIdSet(importSessionId, login, password);
        // счетчики записей
        long dataRowsCount = contentIdSet.size();
        taskDetails.setRecordsTotal(dataRowsCount);
        taskDetails.reading.setItemsTotal(dataRowsCount);
        // создание транзакции словарной системы
        // в данный момент не используется, поскольку часть изменений в БД невозможно отменить (выполняются в других war-файлах, например, в adminws)
        // todo: вернуть использование одной транзакции, когда будет возможность вызова rollback и для изменений, выполненных через adminws
        // DictionaryCaller dictionaryCaller = initDictionaryCallerAndBeginTransaction();
        DictionaryCaller dictionaryCaller = null;
        // начало отсчета чтения
        taskDetails.reading.clear();
        taskDetails.reading.markStart();
        tryLogTaskDetails(taskDetails);
        // флаг наличия ошибок в ходе обработки
        boolean isErrors = false;
        //
        try {
            // повторно используемые переменные
            String eventType;
            String eventSearchValue;
            Long contractId;
            Map<String, Object> manager;
            String managerLogin;
            // состояние обработки
            ProcessingState state;
            // список событий, созданных в ходе обработки записи
            List<Map<String, Object>> savedEventList = new ArrayList<>();
            int unprocessedIdsCount;
            do {
                unprocessedIdsCount = contentIdSet.size();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("unprocessedIdsCount = %d", unprocessedIdsCount));
                }
                // основной цикл обработки
                Iterator<Long> contentIdSetIterator = contentIdSet.iterator();
                while (contentIdSetIterator.hasNext()) {
                    // состояние обработки
                    state = NOT_PROCESSED;
                    // ИД содержимого
                    Long contentId = contentIdSetIterator.next();
                    // logger.debug(String.format("id = %d", contentId));
                    // полные данные по содержимому
                    String importSessionContentEntityName = getImportSessionTaskOptions().getImportSessionContentEntityName();
                    Map<String, Object> content = dctFindById(importSessionContentEntityName, contentId);
                    loggerDebugPretty(logger, importSessionContentEntityName, content);
                    // ИД содержимого сессии импорта
                    Long importSessionCntId = getLongParam(content, "id");
                    // свойства события, протоколируемого в ходе обработки
                    eventType = "";
                    eventSearchValue = null;
                    contractId = null;
                    manager = null;
                    managerLogin = "";
                    // очистка списка событий, созданных в ходе обработки записи
                    savedEventList.clear();

                    String contractNumber = getStringParamLogged(content, "contractNumber");
                    // поиск договора по номеру
                    Map<String, Object> contractParams = new HashMap<>();
                    contractParams.put(CONTRACT_NUMBER_PARAMNAME, contractNumber);
                    List<Map<String, Object>> contractList;
                    // Exception contrEx;
                    try {
                        contractList = callServiceAndGetListFromResultMapLogged(
                                THIS_SERVICE_NAME, "dsB2BContractIdBrowseListByNumber", contractParams, login, password
                        );
                    } catch (Exception ex) {
                        logger.error("Unable to find contract by number by dsB2BContractIdBrowseListByNumber! Details (exception): ", ex);
                        contractList = null;
                        // contrEx = ex;
                    }
                    if ((contractList != null) && (contractList.size() == 1)) {
                        // список договоров содержит один договор - искомый
                        Map<String, Object> contract = contractList.get(0);
                        contractId = getLongParamLogged(contract, CONTRACT_ID_PARAMNAME);
                    }
                    if (contractId == null) {
                        // не найден договор
                        eventType = IMPORT_SESSION_EVENT_CONTRACT_NOT_FOUND_ENTITY_NAME;
                        eventSearchValue = contractNumber;
                        // isProcessed = true;
                        // isError = true;
                        state = PROCESSED_WITH_ERROR;
                    }

                    if (state.isNotProcessed) {
                        // договор найден - поиск менеджера по табельному
                        List<Map<String, Object>> managerList;
                        Long managerPersonnelNumber = getLongParamLogged(content, "managerPersonnelNumber");
                        try {
                            managerList = getManagerListByPersonnelNumberAndStatus(managerPersonnelNumber, STATUS_ACTIVE, login, password);
                        } catch (ImportSessionException ex) {
                            logger.error("Unable to find department by code by getDepartmentListByCode! Details (exception): ", ex);
                            managerList = null;
                        }
                        if ((managerList != null) && (managerList.size() == 1)) {
                            // найден менеджер
                            manager = managerList.get(0);
                            // логин найденного менеджера
                            managerLogin = getStringParamLogged(manager, "LOGIN");
                        }
                        if (managerLogin.isEmpty()) {
                            // не найден менеджер (или получены неполные данные по менеджеру)
                            eventType = IMPORT_SESSION_EVENT_MANAGER_NOT_FOUND_ENTITY_NAME;
                            eventSearchValue = getStringParam(managerPersonnelNumber);
                            state = PROCESSED_WITH_ERROR;
                        }
                    }
                    if (state.isNotProcessed) {
                        // перед переназначением прав следует запомнить текущий набор прав (для регистрации событий)
                        List<Map<String, Object>> oldRightList = getContractRightList(contractId, login, password);
                        // договор и менеджер найдены - проверка подразделения не треубется,
                        // поскольку поиск менеджера осуществляется только в существующих подразделениях и только в ветке партнера
                        // todo: возможно, выполнять переназначение прав условно (трудоемко - потребуется сравнивать деревья прав)
                        // параметры блокировки прав
                        Map<String, Object> rightsBlockParams = new HashMap<>();
                        rightsBlockParams.put(CONTRACT_ID_PARAMNAME, contractId);
                        // параметры создания прав
                        Map<String, Object> rightsCreateParams = new HashMap<>();
                        rightsCreateParams.put(CONTRACT_ID_PARAMNAME, contractId);
                        rightsCreateParams.put("LOGIN", managerLogin);
                        rightsCreateParams.put("GROUPID", contractGroupId);
                        //
                        Map<String, Object> rightsBlockResult = null;
                        Map<String, Object> rightsCreateResult = null;
                        Exception rightsEx = null;
                        try {
                            rightsBlockResult = callServiceLogged(
                                    THIS_SERVICE_NAME, CONTRACT_RIGHTS_BLOCK_METHOD_NAME, rightsBlockParams, login, password
                            );
                            rightsCreateResult = callServiceLogged(
                                    THIS_SERVICE_NAME, CONTRACT_RIGHTS_CREATE_METHOD_NAME, rightsCreateParams, login, password
                            );
                        } catch (Exception ex) {
                            rightsEx = ex;
                        }
                        if (isCallResultNotOK(rightsBlockResult) || isCallResultNotOK(rightsCreateResult)) {
                            if (rightsEx == null) {
                                // исключения не было, но ошибки присутствуют - следует протоколировать результаты
                                Map<String, Object> callResults = new HashMap<>();
                                callResults.put(CONTRACT_RIGHTS_BLOCK_METHOD_NAME, rightsBlockResult);
                                callResults.put(CONTRACT_RIGHTS_CREATE_METHOD_NAME, rightsCreateResult);
                                String errorMsg = "Unable to perform contract rights operation! Details (call results)";
                                loggerErrorPretty(logger, errorMsg, callResults);
                                logger.error(errorMsg + callResults);
                            }
                            ImportSessionException importSessionException = newImportSessionException(
                                    "Не удалось выполнить операцию с правами на договор!",
                                    "Unable to perform contract rights operation!",
                                    rightsEx
                            );
                            throw importSessionException;
                        } else {
                            state = PROCESSED_SUCCESSFULLY;
                            // после переназначения прав следует получить текущий набор прав (для регистрации событий)
                            List<Map<String, Object>> newRightList = getContractRightList(contractId, login, password);
                            // формирование событий на основе сравнения наборов прав до и после переназначения прав
                            List<Map<String, Object>> contractRightsProcessingEventList = generateContractRightsProcessingEvents(
                                    importSessionId, importSessionCntId,
                                    contractId, contractGroupId, oldRightList, newRightList,
                                    params, login, password
                            );
                            // дополнение списка событий, созданных в ходе обработки записи
                            savedEventList.addAll(contractRightsProcessingEventList);
                            // сброс типа основного события, создание основного события ниже не требуется
                            eventType = "";
                        }
                    }

                    if (state.isProcessed) {
                        // если запись обработана
                        if (!eventType.isEmpty()) {
                            // если запись обработана И установлен тип основного события для протоколирования - создание события
                            Map<String, Object> savedEvent = createManagerContractProcessingEvent(
                                    eventType, importSessionId, importSessionCntId,
                                    contractId, null, null, manager, null, null, null, null, eventSearchValue,
                                    params, login, password
                            );
                            // дополнение списка событий, созданных в ходе обработки записи
                            savedEventList.add(savedEvent);
                        }
                        // удаление из итератора
                        contentIdSetIterator.remove();
                        // изменения состояния обработанной записи
                        String transSysName = state.isError ? "B2B_IMPORTSESSION_CNT_from_INPROCESSQUEUE_to_ERROR" : "B2B_IMPORTSESSION_CNT_from_INPROCESSQUEUE_to_SUCCESS";
                        makeTransition(
                                importSessionContentEntityName, contentId, transSysName, dictionaryCaller
                        );
                        // ошибка обработки одной записи влияет на состояние всей сессии
                        if (state.isError) {
                            isErrors = true;
                        }
                    }
                    // увеличение счетчика обработанных записей
                    taskDetails.incRecordsProcessed();
                    taskDetails.reading.incItemsProcessed();
                    taskDetails.commit.incItemsTotal();
                    tryLogTaskDetails(taskDetails);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("unprocessedIdsCount = %d", unprocessedIdsCount));
                    logger.debug(String.format("contentIdSet.size() = %d", contentIdSet.size()));
                }
                tryLogTaskDetails(taskDetails);
            } while (unprocessedIdsCount != contentIdSet.size());

            // список ИД обрабатываемых записей перестал сокращатся -
            // остались только записи для которых не удалось выполнить ни одной операции:
            // их следует обработать отдельно
            if (contentIdSet.size() > 0) {
                // на данный момент все записи должны быть обработаны в основном цикле
                // если имеются необработанные - это ошибка
                loggerErrorPretty(logger, "Unprocessed ids (after main processing cycles) was found", contentIdSet);
                throw newImportSessionException(
                        "Ряд записей содержимого сессии импорта не удалось обработать!",
                        "Unprocessed ids (after main processing cycles) was found!"
                );
                // todo: сабж, при необходимости
            }
            // от наличия содержимого с ошибками зависит условная смена состояния сессии импорта
            isErrors = checkImportSessionContentErrorsAndChangeImportSessionState(importSessionId, isErrors, login, password, dictionaryCaller);
        } catch (Exception processingException) {
            // анализ исключения и выброс на уровень выше
            analyzeProcessingExceptionAndRollback(dictionaryCaller, processingException);
        } finally {
            // todo: освобождение ресурсов (если требуется)
            // окончание отсчета основного цикла
            taskDetails.reading.markFinish();
            forceLogTaskDetails(taskDetails);
        }
        // коммит (закрытие транзакции)
        try {
            commit(dictionaryCaller, taskDetails);
        } finally {
            // todo: освобождение ресурсов (если требуется)
        }
        // результат
        loggerDebugPretty(logger, "doImportSessionProcess processResult", processResult);
        logger.debug("doImportSessionProcess finished.");
        return processResult;
    }

    private ArrayList<Map<String, Object>> generateContractRightsProcessingEvents(
            Long importSessionId, Long importSessionCntId, Long contractId, Long groupId,
            List<Map<String, Object>> oldRightList, List<Map<String, Object>> newRightList,
            Map<String, Object> params, String login, String password
    ) throws ImportSessionException {
        // список сформированных событий
        ArrayList<Map<String, Object>> savedEventList = new ArrayList<>();
        // формирование событий на основе сравнения наборов прав до и после переназначения прав
        if (oldRightList.isEmpty()) {
            // сведений о предыдущих правах не найдено - следует регистрировать события создания прав
            for (Map<String, Object> newRight : newRightList) {
                // события создания связей
                for (String eventType : CONTRACT_RIGHTS_PROCESSING_CREATION_EVENTS) {
                    Map<String, Object> savedEvent = generateContractRightsProcessingEvent(
                            eventType, importSessionId, importSessionCntId,
                            contractId, null, newRight, null,
                            params, login, password
                    );
                    savedEventList.add(savedEvent);
                }
            }
        } else if (!newRightList.isEmpty()) {
            // сведения о предыдущих правах найдены - следует регистрировать события изменения прав
            for (Map<String, Object> oldRight : oldRightList) {
                Long oldRightUserAccountId = getLongParamLogged(oldRight, USER_ACCOUNT_ID_PARAMNAME);
                Long oldRightDepartmentId = getLongParamLogged(oldRight, DEPARTMENT_ID_PARAMNAME);
                for (Map<String, Object> newRight : newRightList) {
                    Long newRightUserAccountId = getLongParamLogged(newRight, USER_ACCOUNT_ID_PARAMNAME);
                    if ((oldRightUserAccountId != null) && (newRightUserAccountId != null) && (!newRightUserAccountId.equals(oldRightUserAccountId))) {
                        // ФТ v1.0: "4. Проверяется наличие связи договора с другим КМ в БД Фронта"
                        // ФТ v1.0: "если связь есть - переопределение связи КМ-договор:"
                        // ФТ v1.0: "Запись в протокол (код 042)." - "Переопределена связь КМ-договор"
                        String eventType = IMPORT_SESSION_EVENT_MANAGER_TO_CONTRACT_CHANGED_ENTITY_NAME;
                        // событие изменения связи менеджер-договор
                        Map<String, Object> savedEvent = generateContractRightsProcessingEvent(
                                eventType, importSessionId, importSessionCntId,
                                contractId, null, newRight, oldRight,
                                params, login, password
                        );
                        savedEventList.add(savedEvent);
                    }
                    Long newRightDepartmentId = getLongParamLogged(newRight, DEPARTMENT_ID_PARAMNAME);
                    if ((oldRightDepartmentId != null) && (newRightDepartmentId != null) && (!newRightDepartmentId.equals(oldRightDepartmentId))) {
                        // ФТ v1.0: "5.2. Проверяется наличие связи договора с другим ВСП в БД Фронта"
                        // ФТ v1.0: "если связь есть - переопределение связи"
                        // ФТ v1.0: "Запись в протокол (код 046)." - "Переопределена связь ВСП-договор"
                        String eventType = IMPORT_SESSION_EVENT_DEPARTMENT_TO_CONTRACT_CHANGED_ENTITY_NAME;
                        // событие изменения связи подразделение-договор
                        Map<String, Object> savedEvent = generateContractRightsProcessingEvent(
                                eventType, importSessionId, importSessionCntId,
                                contractId, null, newRight, oldRight,
                                params, login, password
                        );
                        savedEventList.add(savedEvent);
                    }
                }
            }
            // если хотя бы по одной записи из старого набора прав не было группы,
            // то следует зарегистрировать соответствующее событие
            for (Map<String, Object> oldRight : oldRightList) {
                Long oldRightGroupId = getLongParamLogged(oldRight, "GROUPID");
                if (oldRightGroupId == null) {
                    // хотя бы по одной записи из старого набора прав не было группы
                    // следует зарегистрировать соответствующее событие
                    Map<String, Object> savedEvent = generateContractRightsProcessingEvent(
                            IMPORT_SESSION_EVENT_GROUP_TO_CONTRACT_CREATED_ENTITY_NAME, importSessionId, importSessionCntId,
                            contractId, groupId, null, null,
                            params, login, password
                    );
                    savedEventList.add(savedEvent);
                    break;
                }
            }
            // определние необходимости регистарции события об обработке без изменений
            if (savedEventList.isEmpty()) {
                // набор прав до и после переназначения не пуст, но не было зарегистрированного ни одного события по изменению
                // следовательно права по пользователям/подразделениям не изменились и требуется зарегистрировать соответствующее событие
                Map<String, Object> savedEvent = createProcessingEvent(
                        IMPORT_SESSION_EVENT_NO_CHANGES_ENTITY_NAME, importSessionId, importSessionCntId, null, params
                );
                savedEventList.add(savedEvent);
            }
        }
        return savedEventList;
    }

    private Map<String, Object> generateContractRightsProcessingEvent(
            String eventType, Long importSessionId, Long importSessionCntId,
            Long contractId, Long groupId, Map<String, Object> newRight, Map<String, Object> oldRight,
            Map<String, Object> params, String login, String password
    ) throws ImportSessionException {
        Map<String, Object> eventManager = null;
        Map<String, Object> eventDepartment = null;
        Long contrOrgStructId = null;
        if (newRight != null) {
            eventDepartment = new HashMap<>();
            eventDepartment.put(DEPARTMENT_ID_PARAMNAME, getLongParam(newRight, DEPARTMENT_ID_PARAMNAME));
            eventManager = new HashMap<>();
            eventManager.put(EMPLOYEE_ID_PARAMNAME, getLongParam(newRight, EMPLOYEE_ID_PARAMNAME));
            contrOrgStructId = getLongParam(newRight, CONTRACT_ORG_STRUCT_ID_PARAMNAME);
            if (groupId == null) {
                groupId = getLongParam(newRight, "GROUPID");
            }
        }
        Map<String, Object> eventDepartment2 = null;
        Map<String, Object> eventManager2 = null;
        Long contrOrgStruct2Id = null;
        if (oldRight != null) {
            eventDepartment2 = new HashMap<>();
            eventDepartment2.put(DEPARTMENT_ID_PARAMNAME, getLongParam(oldRight, DEPARTMENT_ID_PARAMNAME));
            eventManager2 = new HashMap<>();
            eventManager2.put(EMPLOYEE_ID_PARAMNAME, getLongParam(oldRight, EMPLOYEE_ID_PARAMNAME));
            contrOrgStruct2Id = getLongParam(oldRight, CONTRACT_ORG_STRUCT_ID_PARAMNAME);
        }
        Map<String, Object> savedEvent = createManagerContractProcessingEvent(
                eventType, importSessionId, importSessionCntId,
                contractId, contrOrgStructId, contrOrgStruct2Id, eventManager, eventManager2, eventDepartment, eventDepartment2, groupId, null,
                params, login, password
        );
        return savedEvent;
    }

    private Map<String, Object> createManagerContractProcessingEvent(
            String eventType, Long importSessionId, Long importSessionCntId,
            Long eventContractId,
            Long contrOrgStructId, Long contrOrgStruct2Id, Map<String, Object> manager, Map<String, Object> manager2,
            Map<String, Object> department, Map<String, Object> department2,
            Long groupId, String searchValue,
            Map<String, Object> params, String login, String password) throws ImportSessionException {
        // параметры события
        Map<String, Object> event = new HashMap<>();
        if (eventContractId != null) {
            event.put("contractId", eventContractId);
        }

        Long departmentId = getLongParam(department, DEPARTMENT_ID_PARAMNAME);
        // основной менеджер события
        if (manager != null) {
            Long managerId = getLongParamLogged(manager, EMPLOYEE_ID_PARAMNAME);
            if (managerId != null) {
                event.put("managerId", managerId);
            }
            if (departmentId == null) {
                // не указан ИД основного подразделения события - следует считать, что это подразделение менеджера
                departmentId = getLongParamLogged(manager, DEPARTMENT_ID_PARAMNAME);
            }
        }

        Long department2Id = getLongParam(department2, DEPARTMENT_ID_PARAMNAME);
        // дополнительный менеджер события
        if (manager2 != null) {
            Long manager2Id = getLongParamLogged(manager2, EMPLOYEE_ID_PARAMNAME);
            if (manager2Id != null) {
                event.put("manager2Id", manager2Id);
            }
            if (department2Id == null) {
                // не указан ИД дополнительного подразделения события - следует считать, что это подразделение второго менеджера
                department2Id = getLongParamLogged(manager2, DEPARTMENT_ID_PARAMNAME);
            }
        }

        // основное подразделение события
        String departmentCode = getStringParam(department, DEPARTMENT_CODE_PARAMNAME);
        String departmentName = getStringParam(department, DEPARTMENT_FULL_NAME_PARAMNAME);
        if ((departmentId != null) && (departmentCode.isEmpty() || departmentName.isEmpty())) {
            // имеется ИД подразделения, но не все данные по нему известны - следует запросить из БД
            department = getDepartmentById(departmentId, login, password);
        }
        // основное подразделение события - дополнение события данными
        event = addDepartmentInfoToEvent(event, department);

        // дополнительное подразделение события
        String department2Code = getStringParam(department2, DEPARTMENT_CODE_PARAMNAME);
        String department2Name = getStringParam(department2, DEPARTMENT_FULL_NAME_PARAMNAME);
        if ((department2Id != null) && (department2Code.isEmpty() || department2Name.isEmpty())) {
            // имеется ИД подразделения, но не все данные по нему известны - следует запросить из БД
            department2 = getDepartmentById(department2Id, login, password);
        }
        // дополнительное подразделение события - дополнение события данными
        event = addDepartment2InfoToEvent(event, department2);

        // contrOrgStructId
        if (contrOrgStructId != null) {
            event.put("contrOrgStructId", contrOrgStructId);
        }
        // contrOrgStruct2Id
        if (contrOrgStruct2Id != null) {
            event.put("contrOrgStruct2Id", contrOrgStruct2Id);
        }

        // groupId
        if (groupId != null) {
            event.put("groupId", groupId);
        }

        if (searchValue != null) {
            event.put("searchValue", searchValue);
        }
        Map<String, Object> savedEvent = createProcessingEvent(eventType, importSessionId, importSessionCntId, event, params);
        return savedEvent;
    }

    protected List<Map<String, Object>> getContractRightList(Long contractId, String login, String password) throws ImportSessionException {
        List<Map<String, Object>> contractRightList = null;
        Exception contractRightListEx = null;
        String serviceName = THIS_SERVICE_NAME;
        String methodName = "dsB2BContractOrgStructBrowseListByParamExForEvents";
        if (contractId != null) {
            Map<String, Object> contractRightParams = new HashMap<>();
            contractRightParams.put(CONTRACT_ID_PARAMNAME, contractId);
            contractRightParams.put("ISBLOCKEDNOTEQUAL", 1L);
            contractRightParams.put("USERACCOUNTIDISNOTNULL", true);
            try {
                contractRightList = callServiceAndGetListFromResultMapLogged(
                        serviceName, methodName, contractRightParams, login, password
                );
            } catch (Exception ex) {
                contractRightList = null;
                contractRightListEx = ex;
            }
        }
        if (contractRightList == null) {
            String error = String.format(
                    "Unable to browse contract rights for contract with id (%s) = %d by calling %s from %s! See previous logged errors for details.",
                    CONTRACT_ID_PARAMNAME, contractId, methodName, serviceName
            );
            throw newImportSessionException(
                    "Не удалось получить текущий набор прав по договору!",
                    error, contractRightListEx
            );
        }
        return contractRightList;
    }

    /** Получение ИД группы по системному наименованию */
    /*
    private Long getGroupId(String groupSysName, String login, String password) throws ImportSessionException {
        Long groupId = null;
        Exception groupListEx = null;
        String serviceName = THIS_SERVICE_NAME;
        String methodName = "dsB2BUserGroupBrowseListByParamEx";
        if ((groupSysName != null) && (!groupSysName.isEmpty())) {
            List<Map<String, Object>> groupList;
            Map<String, Object> groupParams = new HashMap<>();
            groupParams.put("SYSNAME", groupSysName);
            try {
                groupList = callServiceAndGetListFromResultMapLogged(
                        serviceName, methodName, groupParams, login, password
                );
            } catch (Exception ex) {
                groupList = null;
                groupListEx = ex;
            }
            if ((groupList != null) && (groupList.size() == 1)) {
                Map<String, Object> group = groupList.get(0);
                groupId = getLongParam(group, "USERGROUPID");
            }
        }
        if (groupId == null) {
            String error = String.format(
                    "Unable to browse core user group with system name = '%s' by calling %s from %s! See previous logged errors for details.",
                    groupSysName, methodName, serviceName
            );
            throw newImportSessionException(
                    "Не удалось получить текущий набор прав по договору!",
                    error, groupListEx
            );
        }
        return groupId;
    }
    */

    /** Получение ИД договора по номеру (для импорта) */
    // todo: переместить в отдельный фасад (по работе с договорами при импорте и пр.)
    @WsMethod(requiredParams = {CONTRACT_NUMBER_PARAMNAME})
    public Map<String, Object> dsB2BContractIdBrowseListByNumber(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BContractIdBrowseListByNumber", params);
        return result;
    }

    /** Получение текущего набора прав на договор по ИД договора (для формирования событий обработки) */
    // todo: переместить в отдельный фасад (по работе с договорами при импорте и пр.)
    @WsMethod(requiredParams = {CONTRACT_ID_PARAMNAME})
    public Map<String, Object> dsB2BContractOrgStructBrowseListByParamExForEvents(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BContractOrgStructBrowseListByParamExForEvents", params);
        return result;
    }

    /** Получение списка групп пользователей */
    // todo: переместить в отдельный фасад (по работе с группами при импорте и пр.)
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BUserGroupBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BUserGroupBrowseListByParamEx", params);
        return result;
    }

}
