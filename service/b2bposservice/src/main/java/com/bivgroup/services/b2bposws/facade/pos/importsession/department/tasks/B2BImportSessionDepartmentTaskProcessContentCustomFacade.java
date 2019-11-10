package com.bivgroup.services.b2bposws.facade.pos.importsession.department.tasks;

import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryCaller;
import com.bivgroup.services.b2bposws.facade.pos.importsession.common.*;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BImportSessionDepartmentTaskProcessContentCustom")
public class B2BImportSessionDepartmentTaskProcessContentCustomFacade extends B2BImportSessionTaskProcessContentBaseFacade {

    /** Количество потоков обработки (не должно превышать одного потока) */
    private static volatile int threadCount = 0;
    /** Подробные сведения о текущем состоянии импорта */
    private static volatile ImportSessionTaskDetails taskDetails = new ImportSessionTaskDetails();

    private ImportSessionTaskOptions importSessionTaskOptions = new ImportSessionTaskOptions(
            // Имя сущности обрабатываемой сессии импорта
            IMPORT_SESSION_DEPARTMENT_ENTITY_NAME,
            // Системное наименование состояния обрабатываемых сессий импорта
            B2B_IMPORTSESSION_INPROCESSQUEUE,
            // Системное наименование перехода в случае ошибке в ходе обработки содержимого сессии импорта
            B2B_IMPORTSESSION_FROM_INPROCESSQUEUE_TO_ERROR,
            // Системное наименование перехода в случае успешного завершения обработки содержимого сессии импорта
            B2B_IMPORTSESSION_FROM_INPROCESSQUEUE_TO_SUCCESS,
            // Имя сущности содержимого обрабатываемой сессии импорта
            IMPORT_SESSION_CONTENT_DEPARTMENT_ENTITY_NAME
    );

    /** Список системных наименований прав вида "на корневой узел", регистрируемых при создании подразделения */
    protected static final String[] DEPARTMENT_ON_ROOT_RIGHT_SYSNAME_LIST = {
            // 2020 (RPAccess_Menu) Право на доступ к пунктам меню
            "RPAccess_Menu"
            // todo: дополнительные права, если потребуются
    };

    /** Список системных наименований прав вида "на себя", регистрируемых при создании подразделения */
    protected static final String[] DEPARTMENT_ON_SELF_RIGHT_SYSNAME_LIST = {
            // 2013 (RPAccessPOS_Branch) Право работы с информацией модуля. Подразделение
            "RPAccessPOS_Branch"
            // todo: дополнительные права, если потребуются
    };

    @Override
    protected ImportSessionTaskOptions getImportSessionTaskOptions() {
        return importSessionTaskOptions;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionDepartmentTaskProcessContentGetDetails(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionDepartmentTaskProcessContentGetDetails start...");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = taskDetails.toMap();
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        logger.debug("dsB2BImportSessionDepartmentTaskProcessContentGetDetails finished.");
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionDepartmentTaskProcessContent(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionDepartmentTaskProcessContent start...");
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
        logger.debug("dsB2BImportSessionDepartmentTaskProcessContent finished.");
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
        // инициализация узлового подразделения, достаточно проверенного факта его наличия
        // (поиск и установка родителя для всех обычных элементов будет осуществлятся по коду и доп. запросами к БД)
        Map<String, Object> processingRootDepartment = getOrCreateProcessingRootDepartment(importSessionClassifierPack, login, password);
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
            Map<String, Object> department;
            // Long departmentId;
            Map<String, Object> parentDepartment;
            int unprocessedIdsCount;
            do {
                unprocessedIdsCount = contentIdSet.size();
                logger.debug("unprocessedIdsCount = " + unprocessedIdsCount);
                // основной цикл обработки
                Iterator<Long> contentIdSetIterator = contentIdSet.iterator();
                while (contentIdSetIterator.hasNext()) {
                    //
                    boolean isProcessed = false;
                    boolean isError = false;
                    // ИД содержимого
                    Long contentId = contentIdSetIterator.next();
                    logger.debug(String.format("id = %d", contentId));
                    // полные данные по содержимому
                    String importSessionContentEntityName = getImportSessionTaskOptions().getImportSessionContentEntityName();
                    Map<String, Object> content = dctFindById(importSessionContentEntityName, contentId);
                    loggerDebugPretty(logger, importSessionContentEntityName, content);
                    // ИД содержимого сессии импорта
                    Long importSessionCntId = getLongParam(content, "id");
                    // свойства события, протоколируемого в ходе обработки
                    eventType = "";
                    department = null;
                    String parentCode = getStringParam(content, "parentCode");
                    parentDepartment = null;
                    if (parentCode.isEmpty()) {
                        // пустой код родителя
                        eventType = IMPORT_SESSION_EVENT_NO_CHANGES_ENTITY_NAME;
                        isProcessed = true;
                    } else {
                        String code = getStringParam(content, "code");
                        List<Map<String, Object>> existedList = getDepartmentListByCode(code, login, password);
                        if (existedList.size() > 0) {
                            // найдено существующее подразделение
                            eventType = IMPORT_SESSION_EVENT_NO_CHANGES_ENTITY_NAME;
                            Map<String, Object> existedDepartment = existedList.get(0);
                            // departmentId = getLongParam(existedDepartment, DEPARTMENT_ID_PARAMNAME);
                            department = existedDepartment;
                            isProcessed = true;
                        } else {
                            // поиск родителя по коду
                            List<Map<String, Object>> parentList = getDepartmentListByCode(parentCode, login, password);
                            if ((parentList != null) && (parentList.size() == 1)) {
                                // найден единственный кандидат в родители
                                parentDepartment = parentList.get(0);
                                Long parentDepartmentId = getLongParam(parentDepartment, DEPARTMENT_ID_PARAMNAME);
                                if (parentDepartmentId != null) {
                                    try {
                                        department = createDepartment(content, parentDepartmentId, processingRootDepartment, importSessionClassifierPack, login, password);
                                    } catch (ImportSessionException ex) {
                                        String departmentCode = getStringParamLogged(content, "code");
                                        String error = String.format(
                                                "Unable to create department with code '%s' and parent department code '%s'. Details (exception): ",
                                                departmentCode, parentCode
                                        );
                                        logger.error(error, ex);
                                    }
                                    Long departmentId = getLongParamLogged(department, DEPARTMENT_ID_PARAMNAME);
                                    if (departmentId == null) {
                                        // не удалось создать подразделение
                                        // todo: другой тип ошибки, когда будет обновлено ФТ
                                        isError = true;
                                        eventType = IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME;
                                    } else {
                                        eventType = IMPORT_SESSION_EVENT_DEPARTMENT_CREATED_ENTITY_NAME;
                                    }
                                    isProcessed = true;
                                }
                            }
                        }
                    }

                    // если запись обработана и установлен тип события для протоколирования
                    if (isProcessed && !eventType.isEmpty()) {
                        // создание события
                        Map<String, Object> savedEvent = createDepartmentProcessingEvent(
                                eventType, importSessionId, importSessionCntId,
                                department, null, null, null, params
                        );
                        // создание события-"близнеца" (второго события для случаев неразрывных операций)
                        if (IMPORT_SESSION_EVENT_DEPARTMENT_CREATED_ENTITY_NAME.equals(eventType)) {
                            // для события создания подразделение событие-"близнец" это создание связи между подразделениями
                            Map<String, Object> savedSecondaryEvent = createDepartmentProcessingEvent(
                                    IMPORT_SESSION_EVENT_DEPARTMENT_TO_DEPARTMENT_CREATED_ENTITY_NAME, importSessionId, importSessionCntId,
                                    department, parentDepartment, null, null, params
                            );
                        }
                        // удаление из итератора
                        contentIdSetIterator.remove();
                        // изменения состояния обработанной записи
                        String transSysName = isError ? "B2B_IMPORTSESSION_CNT_from_INPROCESSQUEUE_to_ERROR" : "B2B_IMPORTSESSION_CNT_from_INPROCESSQUEUE_to_SUCCESS";
                        makeTransition(
                                importSessionContentEntityName, contentId, transSysName, dictionaryCaller
                        );
                        // ошибка обработки одной записи влияет на состояние всей сессии
                        if (isError) {
                            isErrors = true;
                        }
                    }

                    // увеличение счетчика обработанных записей
                    taskDetails.incRecordsProcessed();
                    taskDetails.reading.incItemsProcessed();
                    taskDetails.commit.incItemsTotal();
                    tryLogTaskDetails(taskDetails);
                }
                logger.debug("unprocessedIdsCount = " + unprocessedIdsCount);
                logger.debug("contentIdSet.size() = " + contentIdSet.size());
                tryLogTaskDetails(taskDetails);
            } while (unprocessedIdsCount != contentIdSet.size());

            // список ИД обрабатываемых записей перестал сокращатся -
            // остались только записи для которых не удалось найти родителей:
            // их следует обработать отдельно
            if (contentIdSet.size() > 0) {
                String importSessionContentEntityName = getImportSessionTaskOptions().getImportSessionContentEntityName();
                Map<String, Object> content;
                Long importSessionCntId;
                String parentCode;
                for (Long contentId : contentIdSet) {
                    // полные данные по содержимому
                    content = dctFindById(importSessionContentEntityName, contentId);
                    // ИД содержимого
                    importSessionCntId = getLongParam(content, "id");
                    // Код родителя, который не был найден
                    parentCode = getStringParam(content, "parentCode");
                    // создание события
                    Map<String, Object> savedEvent = createDepartmentProcessingEvent(
                            IMPORT_SESSION_EVENT_DEPARTMENT_NOT_FOUND_ENTITY_NAME, importSessionId, importSessionCntId,
                            null, null, parentCode, null, params
                    );
                    // изменения состояния обработанной записи
                    String toErrorTransSysName = "B2B_IMPORTSESSION_CNT_from_INPROCESSQUEUE_to_ERROR";
                    makeTransition(
                            importSessionContentEntityName, contentId, toErrorTransSysName, dictionaryCaller
                    );
                }
                // отсутствие родителей для создания записей влияет на состояние всей сессии
                isErrors = true;
            }
            // все подразделения партнера, которые имеются в БД, но отсутствуют в файле данной сессии импорта следует удалить
            deleteAllAbsentInImportSessionDepartments(importSessionId, importSessionClassifierPack, taskDetails, params, login, password, dictionaryCaller);
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

    /** все подразделения партнера, которые имеются в БД, но отсутствуют в файле данной сессии импорта следует удалить */
    private void deleteAllAbsentInImportSessionDepartments(Long importSessionId, ImportSessionClassifierPack importSessionClassifierPack, ImportSessionTaskDetails taskDetails, Map<String, Object> params, String login, String password, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        taskDetails.blocking.clear();
        taskDetails.blocking.markStart();
        Map<String, Object> departmentParams = new HashMap<>();
        departmentParams.put("PARENTDEPTCODE", PROCESSING_ROOT_DEPT_CODE);
        departmentParams.put("NOTINIMPORTSESSIONWITHID", importSessionId);
        // следует исключать из удаляемых единожды создаваемый корневой узел (напримре, ПАО Сбербанк)
        departmentParams.put("DEPTCODENOTEQUAL", PROCESSING_ROOT_DEPT_CODE);
        // следует исключать из удаляемых единожды создаваемый системный узел (для отключенных пользователей)
        departmentParams.put("DEPTCODALSONOTEQUAL", PURGATORY_DEPT_CODE);
        List<Map<String, Object>> departmentList;
        Exception departmentListEx = null;
        try {
            departmentList = callServiceAndGetListFromResultMapLogged(
                    THIS_SERVICE_NAME, "dsB2BDepartmentBrowseListByParamEx", departmentParams, login, password
            );
        } catch (Exception ex) {
            departmentList = null;
            departmentListEx = ex;
        }
        if (departmentList == null) {
            taskDetails.blocking.markFinish();
            throw newImportSessionException(
                    "Не удалось выполнить поиск подразделений, отсутствующих в импортированном файле!",
                    "Unable to browse departments not in imported session for deleting by dsB2BDepartmentBrowseListByParamEx!",
                    departmentListEx
            );
        } else if (!departmentList.isEmpty()) {
            //
            taskDetails.blocking.setItemsTotal((long) departmentList.size());
            // создание системной записи в таблице содержимого импорта
            Map<String, Object> blockedSystemRecord = createBlockedSystemRecord(importSessionId, params, dictionaryCaller);
            Long blockedSystemRecordId = getLongParam(blockedSystemRecord, "id");
            // формирование набора ИД удаляемых подразделений
            logger.debug(String.format("dsB2BDepartmentBrowseListByParamEx returned %d records.", departmentList.size()));
            Set<Long> departmentIdSet = getLongValuesSetFromList(departmentList, DEPARTMENT_ID_PARAMNAME);
            logger.debug(String.format("departmentIdSet contains %s entries.", departmentIdSet.size()));
            // формирование мапы для быстрого доступа к элементам
            Map<Long, Map<String, Object>> departmentMapById = getMapByFieldLongValues(departmentList, DEPARTMENT_ID_PARAMNAME);
            //
            int nonDeletedIdsCount = 0;
            Map<Long, ImportSessionException> deleteExMap = new HashMap<>();
            do {
                nonDeletedIdsCount = departmentIdSet.size();
                logger.debug("nonDeletedIdsCount = " + nonDeletedIdsCount);
                // основной цикл обработки
                Iterator<Long> departmentIdSetIterator = departmentIdSet.iterator();
                while (departmentIdSetIterator.hasNext()) {
                    //
                    Long departmentId = departmentIdSetIterator.next();
                    ImportSessionException deleteEx = null;
                    try {
                        deleteDepartment(
                                departmentId, importSessionId, blockedSystemRecordId, importSessionClassifierPack,
                                params, login, password, dictionaryCaller
                        );
                    } catch (ImportSessionException ex) {
                        // следует запомнить последнее исключение при попытке удаления конкретной записи
                        deleteEx = ex;
                    }
                    if (deleteEx == null) {
                        // запись удалена успешно - следует исключить из набора
                        departmentIdSetIterator.remove();
                        deleteExMap.remove(departmentId);
                        Map<String, Object> department = departmentMapById.get(departmentId);
                        // создание события об удалении подразделения
                        Map<String, Object> savedEvent = createDepartmentProcessingEvent(
                                IMPORT_SESSION_EVENT_DEPARTMENT_BLOCKED_ENTITY_NAME, importSessionId, blockedSystemRecordId,
                                department, null, null, null, params
                        );
                        taskDetails.blocking.incItemsProcessed();
                    } else {
                        // следует запомнить последнее исключение при попытке удаления конкретной записи
                        deleteExMap.put(departmentId, deleteEx);
                    }
                    tryLogTaskDetails(taskDetails);
                }
                logger.debug("nonDeletedIdsCount = " + nonDeletedIdsCount);
                logger.debug("departmentIdSet.size() = " + departmentIdSet.size());
            } while (nonDeletedIdsCount != departmentIdSet.size());
            // список ИД удаляемых записей перестал сокращатся -
            // остались только записи, которые не удалось удалить:
            // исключения при их удалении следует запротоколировать и выбросить любое из них в качестве глобального
            if (!deleteExMap.isEmpty()) {
                ImportSessionException ex = null;
                for (Map.Entry<Long, ImportSessionException> deleteEx : deleteExMap.entrySet()) {
                    ImportSessionException value = deleteEx.getValue();
                    // value должно всегда содержать исключение, но лишние проверки на null никогда не лишние
                    if (value != null) {
                        ex = value;
                    }
                    logger.error(String.format("Deleting department with id = %d caused exception:", deleteEx.getKey()), ex);
                }
                // ex должно всегда содержать исключение, но лишние проверки на null никогда не лишние
                if (ex != null) {
                    throw ex;
                }
            }
        } else {
            taskDetails.blocking.markFinish();
        }
        taskDetails.blocking.markFinish();
    }

    /*
    protected Long getImportSessionContentStateId(String importSessionContentStateSysName) throws ImportSessionException {
        // получение ИД в явном виде для более быстрого запроса данных для обработки
        // String importSessionContentProcessedStateSysName = getImportSessionTaskOptions().getImportSessionContentProcessedStateSysName();
        Long stateId = null;
        Exception stateIdEx = null;
        try {
            stateId = getEntityRecordIdBySysName(SM_STATE_ENTITY_NAME, importSessionContentStateSysName);
        } catch (Exception ex) {
            stateIdEx = ex;
        }
        if (stateId == null) {
            throw newImportSessionException(
                    "Не удалось получить из БД сведения о состояних для создании системных записей содержимого сессии импорта!",
                    "Unable to get import session content content system state id by getEntityRecordIdBySysName!",
                    stateIdEx
            );
        }
        return stateId;
    }
    */

    private Map<String, Object> createDepartmentProcessingEvent(
            String eventType, Long importSessionId, Long importSessionCntId,
            Map<String, Object> department, Map<String, Object> department2, String searchValue, String note,
            Map<String, Object> params
    ) throws ImportSessionException {
        // параметры события
        Map<String, Object> event = new HashMap<>();
        // основное подразделение события
        event = addDepartmentInfoToEvent(event, department);
        // второе подразделение, участвующее в событии (совместно с основным)
        event = addDepartment2InfoToEvent(event, department2);
        // сообщение
        if (note != null) {
            event.put("note", note);
        }
        // искомое значение (например, для событий не удавшегося поиска и пр.)
        if (searchValue != null) {
            event.put("searchValue", searchValue);
        }
        Map<String, Object> savedEvent = createProcessingEvent(eventType, importSessionId, importSessionCntId, event, params);
        return savedEvent;
    }

    private Map<String, Object> createDepartment(
            Map<String, Object> importSessionDepartment, Long parentDepartmentId,
            Map<String, Object> processingRootDepartment, ImportSessionClassifierPack importSessionClassifierPack,
            String login, String password
    ) throws ImportSessionException {
        // параметры создания
        Map<String, Object> departmentParams = new HashMap<>();
        departmentParams.put(RETURN_AS_HASH_MAP, true);
        // родитель подразделения
        departmentParams.put("PARENTDEPARTMENT", parentDepartmentId);
        // тип подразделения
        Long depTypeId = getLongParamLogged(importSessionDepartment, "typeId");
        departmentParams.put("DEPTYPEID", depTypeId);
        // код подразделения
        String departmentCode = getStringParamLogged(importSessionDepartment, "code");
        departmentParams.put(DEPARTMENT_CODE_PARAMNAME, departmentCode);
        // имена
        String departmentName = getStringParamLogged(importSessionDepartment, "name");
        departmentParams.put(DEPARTMENT_FULL_NAME_PARAMNAME, departmentName);
        departmentParams.put("DEPTSHORTNAME", departmentName);
        // уровень подразделения
        // при создании через depStructureAdd есть проверка на DEPTLEVEL через LEVELWEIGHT
        // (может вернуть ошибку вида 'Unable to add department structure element because selected level weight is lower or equals to level weight of parent element!')
        Long deptLevelId = selectDepartmentLevel(departmentCode, parentDepartmentId, importSessionClassifierPack, login, password);
        departmentParams.put("DEPTLEVEL", deptLevelId);
        // адрес подразделения
        Map<String, Object> departmentAddress = new HashMap<>();
        String depRegion = getStringParamLogged(importSessionDepartment, "region");
        departmentAddress.put("REGIONSTR", depRegion);
        String depLocality = getStringParamLogged(importSessionDepartment, "locality");
        departmentAddress.put("LOCALITYSTR", depLocality);
        departmentParams.put("DEPADDRESS", departmentAddress);
        // todo: сегменты
        // создание подразделения
        Map<String, Object> department = null;
        Exception departmentEx = null;
        try {
            department = callServiceLogged(ADMINWS_SERVICE_NAME, "depStructureAdd", departmentParams, login, password);
        } catch (Exception ex) {
            departmentEx = ex;
        }
        // ИД созданного подразделения из depStructureAdd возвращается в ключе AUDITEVENTOBJECTID
        Long departmentId = getLongParam(department, /*"DEPARTMENTID"*/ AUDIT_EVENT_OBJECT_ID_PARAMNAME);
        if ((department == null) || (departmentId == null)) {
            logger.error("Unable to create new department by calling depStructureAdd from adminws service. Details (call result): " + department);
            throw newImportSessionException(
                    "Не удалось создать подразделение!",
                    "Unable to create department!",
                    departmentEx
            );
        } else {
            // следует добавить в результат ИД созданного подразделения
            department.put(DEPARTMENT_ID_PARAMNAME, departmentId);
            // следует добавить в результат код созданного подразделения
            department.put(DEPARTMENT_CODE_PARAMNAME, departmentCode);
            // следует добавить в результат название созданного подразделения
            department.put(DEPARTMENT_FULL_NAME_PARAMNAME, departmentName);
            // назначение прав
            List<Map<String, Object>> rightList = new ArrayList<>();
            // права вида "на корневой узел"
            for (String rightSysName : DEPARTMENT_ON_ROOT_RIGHT_SYSNAME_LIST) {
                Map<String, Object> right = addRightToDepartment(departmentId, rightSysName, processingRootDepartment, login, password);
                // следует добавить в список результатов мапу созданного права
                rightList.add(right);
            }
            // права вида "на себя"
            for (String rightSysName : DEPARTMENT_ON_SELF_RIGHT_SYSNAME_LIST) {
                Map<String, Object> right = addRightToDepartment(departmentId, rightSysName, department, login, password);
                // следует добавить в список результатов мапу созданного права
                rightList.add(right);
            }
            // следует добавить в результат список созданных прав
            department.put("RIGHTLIST", rightList);
        }
        return department;
    }

    // назначение профильного права
    private Map<String, Object> addRightToDepartment(Long departmentId, String rightSysName, Map<String, Object> rightFilterDepartment, String login, String password) throws ImportSessionException {
        // параметры права
        Map<String, Object> rightParams = new HashMap<>();
        // сис. наименование права
        rightParams.put("RIGHTSYSNAME", rightSysName);
        // продукт (пакет)
        rightParams.put("PACKAGEID", 1001L); // 1001 insposws todo: заменить на поиск по сис. наименованию
        // основные атрибуты
        rightParams.put("EXCEPTIONMODE", 1L);
        rightParams.put("OBJECTID", departmentId);
        rightParams.put("RIGHTOWNER", "DEPARTMENT");
        rightParams.put("EXTINTEGRATION", 1L);
        rightParams.put("ANYVALUE", false);
        rightParams.put("RIGHTTYPE", "profileRights");
        rightParams.put("ISEXCEPTION", 0L);
        // фильтры (параметры) права
        HashMap<String, Object> rightFilter = new HashMap<>();
        rightFilter.put("SYSNAME", "DEPARTMENTID");
        rightFilter.put("OPERATION", "==");
        Long rightFilterDepartmentId = getLongParam(rightFilterDepartment, DEPARTMENT_ID_PARAMNAME);
        rightFilter.put("KEYS", rightFilterDepartmentId);
        String rightFilterDepartmentName = getStringParam(rightFilterDepartment, DEPARTMENT_FULL_NAME_PARAMNAME);
        rightFilter.put("VALUES", rightFilterDepartmentName);
        //
        List<Map<String, Object>> rightFilterList = new ArrayList<>();
        rightFilterList.add(rightFilter);
        rightParams.put("FILTERS", rightFilterList);
        //
        rightParams.put(RETURN_AS_HASH_MAP, true);
        // admRightAdd
        String serviceName = ADMINWS_SERVICE_NAME;
        String methodName = "admRightAdd";
        Map<String, Object> right = null;
        Exception rightEx = null;
        try {
            right = callServiceLogged(serviceName, methodName, rightParams, login, password);
            // результат вызова admRightAdd имеет вид типа {Status=OK, AUDITEVENTOBJECTID=36223, Result=36223}
        } catch (Exception ex) {
            rightEx = ex;
        }
        // ИД созданного права из admRightAdd возвращается в ключе AUDITEVENTOBJECTID
        Long rightId = getLongParam(right, AUDIT_EVENT_OBJECT_ID_PARAMNAME);
        if (isCallResultNotOK(right) || (rightId == null)) {
            String error = String.format(
                    "Unable to add right with system name '%s' to department with id = %d by calling %s from %s service (with params: %s). Details (call result): %s",
                    rightSysName, departmentId, methodName, serviceName, rightParams, right
            );
            logger.error(error);
            throw newImportSessionException(
                    "Не удалось назначить право на подразделение!",
                    error, rightEx
            );
        } else {
            right.putAll(rightParams);
            right.put("RIGHTID", rightId);
        }
        return right;
    }

    // уровень подразделения
    // при создании через depStructureAdd есть проверка на DEPTLEVEL через LEVELWEIGHT
    // (может вернуть ошибку вида 'Unable to add department structure element because selected level weight is lower or equals to level weight of parent element!')
    private Long selectDepartmentLevel(
            String departmentCode, Long parentId,
            ImportSessionClassifierPack importSessionClassifierPack, String login, String password
    ) throws ImportSessionException {
        // код подразделения
        String levelSysName;
        int codeLen = departmentCode.length();
        if (codeLen > 7) {
            // ФТ: "9,10,11,12-ти значный код – признак уровня ВСП;"
            // Внутреннее структурное подразделение (уровень 1/2/3)
            // для ВСП точный уровень можно определить только по родителю
            Map<String, Object> department = getDepartmentById(parentId, login, password);
            String parentLevelSysName = getStringParam(department, "DEPTLEVELSYSNAME");
            if (parentLevelSysName.startsWith(DEPTLEVEL_DEPARTMENT_SYSNAME_BASE)) {
                // согласно сис. наименованию родителя, он является ВСП
                // следует определить его уровень и для его подчиненного выбрать на один ниже по иерархии
                String parentLevelNumStr = parentLevelSysName.substring(DEPTLEVEL_DEPARTMENT_SYSNAME_BASE_LENGTH);
                Long parentLevelNum;
                Exception parentLevelNumEx = null;
                try {
                    parentLevelNum = getLongParam(parentLevelNumStr);
                } catch (NumberFormatException ex) {
                    parentLevelNum = null;
                    parentLevelNumEx = ex;
                }
                if (parentLevelNum == null) {
                    logger.error(String.format(
                            "Unable to parse parent department level system name! Details (parent department level system name): %s",
                            parentLevelSysName
                    ));
                    throw newImportSessionException(
                            "Не удалось получить или проанализировать сведения об уровне родительского подразделения!",
                            "Unable to get or parse parent department level system name!",
                            parentLevelNumEx
                    );
                } else {
                    Long levelNum = parentLevelNum + 1;
                    levelSysName = DEPTLEVEL_DEPARTMENT_SYSNAME_BASE + levelNum.toString();
                }
            } else {
                // согласно сис. наименованию родителя, он сам не является ВСП
                // для его подчиненного следует выбрать верхний уровня ВСП
                levelSysName = DEPTLEVEL_DEPARTMENT_LEVEL_1_SYSNAME;
            }

        } else if (codeLen > 3) {
            // ФТ: "7-ми значный код – признак уровня ГОСБ;"
            // Головное отделение
            levelSysName = DEPTLEVEL_HEAD_BRANCH_SYSNAME;
        } else {
            // ФТ: "3-х значный код – признак уровня ТБ;"
            // Территориальный банк
            levelSysName = DEPTLEVEL_TERRITORIAL_BANK_SYSNAME;
        }
        Long deptLevelId = importSessionClassifierPack
                .get(KIND_IMPORT_SESSION_DEPARTMENT_LEVEL_ENTITY_NAME)
                .getRecordFieldLongValueByFieldStringValue("levelSysName", levelSysName, "id");
        if (deptLevelId == null) {
            logger.error(String.format(
                    "Unable to get department level id by system name! Details (department level system name): %s",
                    levelSysName
            ));
            throw newImportSessionException(
                    "Не удалось получить или проанализировать сведения об уровне подразделения!",
                    "Unable to get or parse department level system name!"
            );
        }
        return deptLevelId;
    }

    private Map<String, Object> deleteDepartment(
            Long departmentId, Long importSessionId, Long blockedSystemRecordId, ImportSessionClassifierPack importSessionClassifierPack,
            Map<String, Object> params, String login, String password, DictionaryCaller dictionaryCaller
    ) throws ImportSessionException {
        // проверка наличия "неотключенных" пользователей в подразделении и их "отключение"
        disableAllDepartmentManagers(departmentId, importSessionId, blockedSystemRecordId, importSessionClassifierPack, params, login, password, dictionaryCaller);
        // параметры удаления
        Map<String, Object> departmentParams = new HashMap<String, Object>();
        departmentParams.put(RETURN_AS_HASH_MAP, true);
        // ИД подразделения
        departmentParams.put(DEPARTMENT_ID_PARAMNAME, departmentId);
        // удаление
        Map<String, Object> result = null;
        Exception departmentEx = null;
        try {
            result = callServiceLogged(ADMINWS_SERVICE_NAME, "depStructureRemove", departmentParams, login, password);
        } catch (Exception ex) {
            departmentEx = ex;
        }
        // проверка результата
        if ((result == null) || (!RET_STATUS_OK.equals(getStringParamLogged(result, RET_STATUS))) || (!BOOLEAN_FLAG_LONG_VALUE_FALSE.equals(getLongParam(result, "ReturnCode")))) {
            logger.error("Unable to create new department by calling depStructureAdd from adminws service. Details (call result): " + result);
            throw newImportSessionException(
                    "Не удалось удалить подразделение!",
                    "Unable to delete department by depStructureRemove from adminws!",
                    departmentEx
            );
        }
        return result;
    }

    /** Проверка наличия "неотключенных" пользователей в подразделении и их "отключение" */
    private void disableAllDepartmentManagers(Long departmentId, Long importSessionId, Long blockedSystemRecordId, ImportSessionClassifierPack importSessionClassifierPack, Map<String, Object> params, String login, String password, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        // параметры менеджеров, которых следует заблокировать
        Map<String, Object> managerParams = new HashMap<>();
        managerParams.put("PARENTDEPTCODE", PROCESSING_ROOT_DEPT_CODE);
        managerParams.put(DEPARTMENT_ID_PARAMNAME, departmentId);
        // поскольку в disableManager > disableEmployee используется установка STATUS = DELETED через depEmployeeUpdate,
        // то и при запросе блокируемых следует использовать иное ограничение (на <> DELETED)
        // managerParams.put("ANYSTATUSEQUALS", "ACTIVE");
        managerParams.put("ANYSTATUSNOTEQUALS", "DELETED");
        List<Map<String, Object>> managerList;
        Exception managerListEx = null;
        try {
            managerList = callServiceAndGetListFromResultMapLogged(
                    THIS_SERVICE_NAME, "dsB2BEmployeeBrowseListByParamEx", managerParams, login, password
            );
        } catch (Exception ex) {
            managerList = null;
            managerListEx = ex;
        }
        if (managerList == null) {
            throw newImportSessionException(
                    "Не удалось выполнить поиск менеджеров, приписанных к удаляемому подразделению!",
                    "Unable to browse managers in deleted department by dsB2BEmployeeBrowseListByParamEx!",
                    managerListEx
            );
        } else if (!managerList.isEmpty()) {
            // найдены "неотключенные" пользователи в подразделении - их следует "отключить" перед удалением подразделения
            disableManagers(
                    managerList, importSessionId, blockedSystemRecordId,
                    importSessionClassifierPack, null,
                    params, login, password, dictionaryCaller
            );
        }
    }

}
