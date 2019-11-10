package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryCaller;

import java.util.*;

public abstract class B2BImportSessionTaskProcessContentBaseFacade extends B2BImportSessionTaskBaseFacade {

    /** "Строки с пустым значением кода головного подразделения не подлежать импорту, кроме строки с собственным кодом подразделения = 099 (ПАО Сбербанк)" */
    protected static final String PROCESSING_ROOT_DEPT_NAME = "ПАО Сбербанк";
    protected static final String PROCESSING_ROOT_DEPT_TYPE_NAME = PROCESSING_ROOT_DEPT_NAME;

    /** ФТ v1.0: "УЗ, входящие в закрываемое подразделение: Деактивируются; Помещаются в системное подразделение;" */
    protected static final String PURGATORY_DEPT_NAME = "< Отключенные пользователи >";

    //
    protected static final String DEPTLEVEL_PARTNER_SYSNAME = "partner";
    protected static final String DEPTLEVEL_TERRITORIAL_BANK_SYSNAME = "territorialBank";
    protected static final String DEPTLEVEL_HEAD_BRANCH_SYSNAME = "headBranch";
    protected static final String DEPTLEVEL_DEPARTMENT_SYSNAME_BASE = "departmentLevel";
    protected static final String DEPTLEVEL_DEPARTMENT_LEVEL_1_SYSNAME = DEPTLEVEL_DEPARTMENT_SYSNAME_BASE + "1";
    protected static final int DEPTLEVEL_DEPARTMENT_SYSNAME_BASE_LENGTH = DEPTLEVEL_DEPARTMENT_SYSNAME_BASE.length();

    /** DEPARTMENTID */
    protected static final String DEPARTMENT_ID_PARAMNAME = "DEPARTMENTID";
    /** DEPTCODE */
    protected static final String DEPARTMENT_CODE_PARAMNAME = "DEPTCODE";
    /** DEPTFULLNAME */
    protected static final String DEPARTMENT_FULL_NAME_PARAMNAME = "DEPTFULLNAME";

    /** STATUS_ACTIVE = "ACTIVE" */
    protected static final String STATUS_ACTIVE = "ACTIVE";
    /** STATUS_DELETED = "DELETED" */
    protected static final String STATUS_DELETED = "DELETED";
    /** STATUS_PARAMNAME = "STATUS" */
    protected static final String STATUS_PARAMNAME = "STATUS";

    /** EMPLOYEE_STATUS_ACTIVE = "ACTIVE" */
    protected static final String EMPLOYEE_STATUS_ACTIVE = STATUS_ACTIVE;
    /** EMPLOYEE_STATUS_ACTIVE = "DELETED" */
    protected static final String EMPLOYEE_STATUS_DELETED = STATUS_DELETED;
    /** EMPLOYEE_STATUS_PARAMNAME = "STATUS" */
    protected static final String EMPLOYEE_STATUS_PARAMNAME = STATUS_PARAMNAME;

    /** PARENTDEPTCODE */
    public static final String PARENT_DEPARTMENT_CODE_PARAMNAME = "PARENTDEPTCODE";
    protected static final String USER_ACCOUNT_ID_PARAMNAME = "USERACCOUNTID";
    protected static final String USER_ID_PARAMNAME = "USERID";
    protected static final String USER_ACCOUNT_LOGIN_PARAMNAME = "LOGIN";
    protected static final String USER_ACCOUNT_CODE_PARAMNAME = "CODE";

    /** группа по умолчанию - СБ1" */
    protected static final String DEFAULT_GROUP_SYSNAME = "SB1";

    /** Список имен запросов (c sql-инструкциями вида insert from select)
     *  для создания резервных копий данных таблиц,
     *  которые могут быть необратимо изменены в ходе импорта */
    private static final String[] LOG_CREATE_QUERY_NAMES = new String[]{
            "dsB2BImportSessionLogCreateForCoreRFValue",
            "dsB2BImportSessionLogCreateForCoreRightDept",
            "dsB2BImportSessionLogCreateForCoreRightFilter",
            "dsB2BImportSessionLogCreateForDepDepartment",
            "dsB2BImportSessionLogCreateForDepDepParent"
    };

    /** Список системных наименований состояний содержимого сессии импорта, которые указывают на ошибки в ходе обработки */
    private static final List<String> IMPORT_SESSION_CONTENT_ERRONEOUS_STATE_SYSNAME_LIST = Arrays.asList(
            "B2B_IMPORTSESSION_CNT_ERROR",
            "B2B_IMPORTSESSION_CNT_INVALID"
    );

    protected Set<Long> getImportSessionContentForProcessedIdSet(Long importSessionId, String login, String password) throws ImportSessionException {

        Long stateId = getImportSessionProcessedStateId();

        Map<String, Object> contentParams = new HashMap<String, Object>();
        contentParams.put("IMPORTSESSIONID", importSessionId);
        contentParams.put("STATEID", stateId);

        Exception contentListEx = null;
        List<Map<String, Object>> contentList;
        String methodName = "dsB2BImportSessionContentBrowseListByParamForProcessing";
        try {
            contentList = callServiceAndGetListFromResultMapLogged(
                    THIS_SERVICE_NAME, methodName, contentParams, login, password
            );
        } catch (Exception ex) {
            contentList = null;
            contentListEx = ex;
        }
        Set<Long> contentIdSet = null;
        if (contentList != null) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("%s returned %d records.", methodName, contentList.size()));
            }
            contentIdSet = getLongValuesSetFromList(contentList, "ID");
            /*
            contentIdSet = new HashSet<>();
            for (Map<String, Object> content : contentList) {
                Long contentId = getLongParam(content, "ID");
                if (contentId != null) {
                    contentIdSet.add(contentId);
                }
            }
            */
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("contentIdSet contains %s entries.", contentIdSet.size()));
            }
        }
        if (contentIdSet == null) {
            // не удалось получить данные
            throw newImportSessionException(
                    "Не удалось получить из БД данные по обрабатываемым сведениям!",
                    String.format(
                            "Unable to get content id list for processing form DB by %s for import session with id = %d!",
                            methodName, importSessionId
                    ),
                    contentListEx
            );
        } else if (contentIdSet.isEmpty()) {
            // нет записей для обработки
            String message = String.format(
                    "Content id list for processing form DB by %s is empty for import session with id = %d!",
                    methodName, importSessionId
            );
            // Ваня К.: "пустые (не содержащие ни одной строки) файлы считать корректными"
            /*
            throw newImportSessionException(
                    "Отсутствуют записи для обработки по данной сессии импорта!",
                    message,
                    contentListEx
            );
            */
            logger.warn(message);
        }
        return contentIdSet;
    }

    protected Long getImportSessionStateId(String importSessionContentStateSysName) throws ImportSessionException {
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
                    "Не удалось получить из БД сведения о состояних для формирования ограничения обрабатываемых записей содержимого сессии импорта!",
                    "Unable to get processed content state id by getEntityRecordIdBySysName!",
                    stateIdEx
            );
        }
        return stateId;
    }

    private Long getImportSessionProcessedStateId() throws ImportSessionException {
        // получение ИД в явном виде для более быстрого запроса данных для обработки
        // String importSessionContentProcessedStateSysName = getImportSessionTaskOptions().getImportSessionContentProcessedStateSysName();
        String importSessionContentProcessedStateSysName = "B2B_IMPORTSESSION_CNT_INPROCESSQUEUE";
        Long stateId = getImportSessionStateId(importSessionContentProcessedStateSysName);
        return stateId;
    }

    protected boolean isExistsImportSessionContentWithError(boolean isErrors, Long importSessionId, String importSessionContentStateSysName, String login, String password) throws ImportSessionException {
        if (!isErrors) {
            // если ошибок в ходе обработки не возникало, то следует дополнительно проверить наличие содержимого с ошибками импорта | исходных данных
            Map<String, Object> existsParams = new HashMap<>();
            existsParams.put("IMPORTSESSIONID", importSessionId);
            existsParams.put("STATEID", getImportSessionStateId(importSessionContentStateSysName));
            existsParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> existsResult = null;
            try {
                existsResult = callServiceLogged(
                        THIS_SERVICE_NAME, "dsB2BImportSessionContentExistsByParam", existsParams, login, password
                );
            } catch (Exception ex) {
                throw newImportSessionException(
                        "Не удалось определить наличие ошибок импорта / ошибок исходных данных на предыдущих этапах обработки!",
                        "Unable to dsB2BImportSessionContentExistsByParam and etc!",
                        ex
                );
            }
            Long exists = getLongParam(existsResult, "ISEXISTS");
            isErrors = !BOOLEAN_FLAG_LONG_VALUE_FALSE.equals(exists);
        }
        return isErrors;
    }

    /** создание рез. копий данных таблиц, которые могут быть необратимо изменены в ходе импорта */
    protected Map<String, Object> doImportAffectedTablesDataLogs(Long importSessionId) throws ImportSessionException {
        loggerDebugPretty(logger, "doImportAffectedTablesDataLogs importSessionId", importSessionId);
        Map<String, Object> result = new HashMap<String, Object>();
        Long insertedRowsTotalCount = 0L;
        for (String queryName : LOG_CREATE_QUERY_NAMES) {
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("IMPORTSESSIONID", importSessionId);
            Long insertedRowsCount = null;
            try {
                insertedRowsCount = doInsertQuery(queryName, queryParams);
            } catch (Exception ex) {
                throw newImportSessionException(
                        "Не удалось зафиксировать в базе данных текущее состояние сведений, необратимо изменяемых в ходе импорта!",
                        "Unable to copy important data from import affected tables into backup log tables!",
                        ex
                );
            }
            result.put(queryName, insertedRowsCount);
            insertedRowsTotalCount += insertedRowsCount;
        }
        result.put("TOTAL", insertedRowsTotalCount);
        loggerDebugPretty(logger, "doImportAffectedTablesDataLogs result", result);
        return result;
    }

    private Long doInsertQuery(String queryName, Map<String, Object> queryParams) throws Exception {
        Long insertedRowsTotalCount = 0L;
        if (logger.isDebugEnabled()) {
            loggerDebugPretty(logger, String.format("Executing query '%s' with params", queryName), queryParams);
        }
        int[] insertResult = insertQuery(queryName, queryParams);
        if (logger.isDebugEnabled()) {
            loggerDebugPretty(logger, String.format("Query '%s' executed and reported result", queryName), insertResult);
        }
        if (insertResult != null) {
            for (int i : insertResult) {
                insertedRowsTotalCount = insertedRowsTotalCount + i;
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Query '%s' inserted %d row(s) in db.", queryName, insertedRowsTotalCount));
            }
        }
        return  insertedRowsTotalCount;
    }

    protected Map<String, Object> createProcessingEvent(String eventType, Long importSessionId, Long importSessionCntId, Map<String, Object> event, Map<String, Object> params) throws ImportSessionException {
        if (event == null) {
            event = new HashMap<>();
        }
        event.put("importSessionId", importSessionId);
        event.put("importSessionCntId", importSessionCntId);
        genSystemAttributesWithoutAspect(event, params);
        // создание события
        Map<String, Object> savedEvent = null;
        try {
            savedEvent = dctCrudByHierarchy(eventType, event);
        } catch (Exception ex) {
            throw newImportSessionException(
                    "Не удалось создать запись в протоколе обработки!",
                    "Unable to save processing event to db!",
                    ex
            );
        }
        loggerDebugPretty(logger, "savedEvent", savedEvent);
        return savedEvent;
    }

    protected List<Map<String, Object>> getDepartmentListByCode(String deptCode, String login, String password) throws ImportSessionException {
        // если не указано иное, то поиск подразделений по коду всегда внутри корневого узла партнера
        String parentDeptCode = PROCESSING_ROOT_DEPT_CODE;
        List<Map<String, Object>> departmentList = getDepartmentListByCodes(deptCode, parentDeptCode, login, password);
        return departmentList;
    }

    protected Map<String, Object> getDepartmentById(Long departmentId, String login, String password) throws ImportSessionException {
        String departmentIdParamName = DEPARTMENT_ID_PARAMNAME;
        Map<String, Object> departmentParams = new HashMap<>();
        departmentParams.put(departmentIdParamName, departmentId);
        List<Map<String, Object>> departmentList = getDepartmentListByParams(login, password, departmentParams);
        Map<String, Object> department = null;
        if ((departmentList == null) || (departmentList.size() > 1)) {
            logger.error(String.format(
                    "Unable to get department data for department with id (%s) = %d by dsB2BDepartmentBrowseListByParamEx. Details (list from result): %s",
                    departmentIdParamName, departmentId, departmentList
            ));
            throw newImportSessionException(
                    "Не удалось получить сведения о подразделении из БД!",
                    "Unable to get department data by id by dsB2BDepartmentBrowseListByParamEx!"
            );
        } else if (departmentList.size() == 1) {
            department = departmentList.get(0);
        } else {
            department = new HashMap<>();
        }
        return department;
    }

    protected List<Map<String, Object>> getDepartmentListByCodes(String deptCode, String parentDeptCode, String login, String password) throws ImportSessionException {
        Map<String, Object> departmentParams = new HashMap<>();
        departmentParams.put("DEPTCODE", deptCode);
        departmentParams.put(PARENT_DEPARTMENT_CODE_PARAMNAME, parentDeptCode);
        List<Map<String, Object>> departmentList = getDepartmentListByParams(login, password, departmentParams);
        return departmentList;
    }

    private List<Map<String, Object>> getDepartmentListByParams(String login, String password, Map<String, Object> departmentParams) throws ImportSessionException {
        List<Map<String, Object>> departmentList = null;
        Exception departmentListEx = null;
        if (departmentParams != null) {
            // если не указано иное, то поиск подразделений по коду всегда внутри корневого узла партнера
            departmentParams.putIfAbsent(PARENT_DEPARTMENT_CODE_PARAMNAME, PROCESSING_ROOT_DEPT_CODE);
            try {
                departmentList = callServiceAndGetListFromResultMapLogged(
                        THIS_SERVICE_NAME, "dsB2BDepartmentBrowseListByParamEx", departmentParams, login, password
                );
            } catch (Exception ex) {
                departmentList = null;
                departmentListEx = ex;
            }
        }
        if (departmentList == null) {
            throw newImportSessionException(
                    "Не удалось выполнить поиск подразделения!",
                    "Unable to browse department by dsB2BDepartmentBrowseListByParamEx!",
                    departmentListEx
            );
        }
        return departmentList;
    }

    protected Map<String, Object> checkDepartmentListAndChooseSingle(List<Map<String,Object>> departmentList, String login, String password) {
        Map<String, Object> department = null;
        if (departmentList == null) {
            logger.error("Method checkDepartmentListAndChooseSingle receive null departments list! See previous logged errors for details.");
        } else if (departmentList.size() == 1) {
            // найденное подразделение
            department = departmentList.get(0);
        } else if (departmentList.isEmpty()) {
            // подразделения не найдено
            logger.error("Method checkDepartmentListAndChooseSingle receive empty departments list! See previous logged errors for details.");
        } else {
            // тоже самое что и } else if (departmentList.size() > 1) {
            // найдено несколько подразделений
            loggerErrorPretty(logger,
                    "Method checkDepartmentListAndChooseSingle receive multiple departments! Details (departments list)",
                    departmentList
            );
        }
        if (department == null) {
            // выбрать подразделение не удалось - создание дополнительного события
            /*
            Map<String, Object> savedEvent = createManagerDepartmentProcessingEvent(
                    eventType, importSessionId, importSessionCntId, manager, eventSearchValue, null,
                    params, login, password
            );
            */
        }
        return department;
    }

    protected boolean checkImportSessionContentErrorsAndChangeImportSessionState(Long importSessionId, boolean isErrors, String login, String password, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        // от наличия содержимого с ошибками зависит условная смена состояния сессии импорта (ниже)
        // если ошибок в ходе обработки не возникало, то следует дополнительно проверить наличие содержимого с ошибками импорта | исходных данных
        for (String importSessionContentErroneousStateSysName : IMPORT_SESSION_CONTENT_ERRONEOUS_STATE_SYSNAME_LIST) {
            isErrors = isExistsImportSessionContentWithError(isErrors, importSessionId, importSessionContentErroneousStateSysName, login, password);
        }
        // условная смена состояния сессии импорта - зависит от наличия содержимого с ошибками
        String importSessionTransSysName = isErrors ?
                getImportSessionTaskOptions().getImportSessionErrorTransSysName() :
                getImportSessionTaskOptions().getImportSessionSuccessTransSysName();
        changeImportSessionState(importSessionId, importSessionTransSysName, dictionaryCaller);
        return isErrors;
    }

    /*
    protected List<Map<String, Object>> getManagerListByPersonnelNumber(Long managerPersonnelNumber, String login, String password) throws ImportSessionException {
        List<Map<String, Object>> managerList = getManagerListByPersonnelNumberAndStatus(managerPersonnelNumber, EMPLOYEE_STATUS_ACTIVE, login, password);
        return managerList;
    }
    */

    protected List<Map<String, Object>> getManagerListByPersonnelNumberAndStatus(Long managerPersonnelNumber, String employeeStatus, String login, String password) throws ImportSessionException {
        List<Map<String, Object>> managerList = null;
        Exception managerListEx = null;
        if (managerPersonnelNumber != null) {
            Map<String, Object> managerParams = new HashMap<>();
            managerParams.put("CODE", managerPersonnelNumber);
            managerParams.put("STATUS", employeeStatus);
            managerParams.put(PARENT_DEPARTMENT_CODE_PARAMNAME, PROCESSING_ROOT_DEPT_CODE);
            managerParams.put("ORDERBYUSERACCOUNTCREATIONDATEDESC", true);
            try {
                managerList = callServiceAndGetListFromResultMapLogged(
                        THIS_SERVICE_NAME, "dsB2BEmployeeBrowseListByParamEx", managerParams, login, password
                );
            } catch (Exception ex) {
                managerList = null;
                managerListEx = ex;
            }
        }
        if (managerList == null) {
            throw newImportSessionException(
                    "Не удалось выполнить поиск менеджера по коду!",
                    "Unable to browse manager by dsB2BEmployeeBrowseListByParamEx!",
                    managerListEx
            );
        }
        return managerList;
    }

    /** Проверка наличия учетной записи с указанным логином */
    protected boolean checkIsAccountLoginExist(Long userAccountLogin, String login, String password) throws ImportSessionException {
        List<Map<String, Object>> accountList = null;
        Exception managerListEx = null;
        // dsUserAccountCreationDateByLogin - самый легковесный из готовых запросов с ограничением по логину
        // todo: если потребуются дополнительные сведения о пользователях, найденных по логину - реализовать соответствующий новый метод
        String methodName = "dsUserAccountCreationDateByLogin";
        if (userAccountLogin != null) {
            Map<String, Object> accountParams = new HashMap<>();
            accountParams.put("LOGIN", userAccountLogin);
            try {
                accountList = callServiceAndGetListFromResultMapLogged(
                        B2BPOSWS_SERVICE_NAME, methodName, accountParams, login, password
                );
            } catch (Exception ex) {
                accountList = null;
                managerListEx = ex;
            }
        }
        if (accountList == null) {
            throw newImportSessionException(
                    "Не удалось выполнить проверку наличия учетной записи с указанным логином!",
                    String.format("Unable to browse manager by calling %s (with params = %s)! Details - in previously logged errors.", methodName),
                    managerListEx
            );
        }
        boolean isAccountLoginExist = !accountList.isEmpty();
        return isAccountLoginExist;
    }

    protected Map<String, Object> createBlockedSystemRecord(Long importSessionId, Map<String, Object> params, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        HashMap<String, Object> importSessionContent = new HashMap<String, Object>();
        // системные свойства
        genSystemAttributesWithoutAspect(importSessionContent, params);
        // ИД родителя для связи
        importSessionContent.put("importSessionId", importSessionId);
        // ИД специального состояния для данной системной записи
        // importSessionContent.put("stateId", getImportSessionContentStateId("B2B_IMPORTSESSION_CNT_SYSBLOCK"));
        // сохранение
        Map<String, Object> importSessionContentSaved = saveImportSessionContent(importSessionContent, dictionaryCaller);
        Long importSessionContentId = getLongParam(importSessionContentSaved, "id");
        // изменение состояния созданной записи
        // todo: скопировать с СБС вариант с возвратом данных о новом состоянии и обновлять результат сохранения (importSessionContentSaved)
        changeImportSessionContentState(importSessionContentId, B2B_IMPORTSESSION_CNT_from_INVALID_to_SYSBLOCK, dictionaryCaller);
        // возврат результата
        return importSessionContentSaved;
    }

    protected Map<String, Object> saveImportSessionContent(Map<String, Object> importSessionContent, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        Map<String, Object> importSessionContentSaved;
        String importSessionContentEntityName = getImportSessionTaskOptions().getImportSessionContentEntityName();
        // следуетт временно извлечь список с ошибками исходных данных (не должен обрабатываться словарной системой)
        /*
        Object errorList = null;
        if (importSessionContent != null) {
            errorList = importSessionContent.remove(CONTENT_INVALID_DATA_ERROR_LIST_PARAMNAME);
        }
        */
        // сохранение с try / catch
        try {
            // создание записи содержимого
            loggerDebugPretty(logger, importSessionContentEntityName, importSessionContent);
            if (dictionaryCaller == null) {
                importSessionContentSaved = dctCrudByHierarchy(importSessionContentEntityName, importSessionContent);
            } else {
                importSessionContentSaved = dctCrudByHierarchyWithoutTransactionControl(importSessionContentEntityName, importSessionContent, dictionaryCaller);
            }
            // todo: доп. проверки результата сохранения (например, наличие ИД и пр.)
        } catch (Exception ex) {
            importSessionContentSaved = null;
            throw newImportSessionException(
                    "Ошибка при создании системных записей содержимого сессии импорта!",
                    "Unable to save import session content system record to database by dctCrudByHierarchy / dctCrudByHierarchyWithoutTransactionControl!",
                    ex
            );
        }
        // следует дополнить результат сохранения временно извлеченным списком с ошибками исходных данных
        /*
        if ((importSessionContentSaved != null) && (errorList != null)) {
            importSessionContentSaved.put(CONTENT_INVALID_DATA_ERROR_LIST_PARAMNAME, errorList);
        }
        */
        return importSessionContentSaved;
    }

    /** "Отключение" всех менеджеров из указанного списка */
    protected void disableManagers(
            List<Map<String, Object>> managerList, Long importSessionId, Long blockedSystemRecordId,
            ImportSessionClassifierPack importSessionClassifierPack, ImportSessionTaskDetails taskDetails,
            Map<String, Object> params, String login, String password, DictionaryCaller dictionaryCaller
    ) throws ImportSessionException {
        //
        if (taskDetails != null) {
            taskDetails.blocking.clear();
            taskDetails.blocking.markStart();
            taskDetails.blocking.setItemsTotal((long) managerList.size());
        }
        //
        if (blockedSystemRecordId == null) {
            // создание системной записи в таблице содержимого импорта (если она не была создана ранее)
            Map<String, Object> blockedSystemRecord = createBlockedSystemRecord(importSessionId, params, dictionaryCaller);
            blockedSystemRecordId = getLongParam(blockedSystemRecord, "id");
        }
        // формирование набора ИД удаляемых менеджеров
        Set<Long> managerIdSet = getLongValuesSetFromList(managerList, EMPLOYEE_ID_PARAMNAME);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("dsB2BEmployeeBrowseListByParamEx returned %d records.", managerList.size()));
            logger.debug(String.format("managerIdSet contains %s entries.", managerIdSet.size()));
        }
        // формирование мапы для быстрого доступа к элементам
        Map<Long, Map<String, Object>> managerMapById = getMapByFieldLongValues(managerList, EMPLOYEE_ID_PARAMNAME);
        // ФТ v1.0: "УЗ, входящие в закрываемое подразделение: Деактивируются; Помещаются в системное подразделение;"
        Map<String, Object> purgatoryDepartment = getOrCreatePurgatoryDepartment(importSessionClassifierPack, login, password);
        Long purgatoryDepartmentId = getLongParamLogged(purgatoryDepartment, DEPARTMENT_ID_PARAMNAME);
        //
        int nonDisabledIdsCount = 0;
        Map<Long, ImportSessionException> disableExMap = new HashMap<>();
        do {
            nonDisabledIdsCount = managerIdSet.size();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("nonDisabledIdsCount = %d", nonDisabledIdsCount));
            }
            // основной цикл обработки
            Iterator<Long> managerIdSetIterator = managerIdSet.iterator();
            while (managerIdSetIterator.hasNext()) {
                //
                Long managerId = managerIdSetIterator.next();
                Map<String, Object> manager = managerMapById.get(managerId);
                ImportSessionException disableEx = null;
                try {
                    disableManager(manager, purgatoryDepartmentId, login, password);
                } catch (ImportSessionException ex) {
                    // следует запомнить последнее исключение при попытке блокировки конкретной записи
                    disableEx = ex;
                }
                if (disableEx == null) {
                    // запись заблокирована успешно - следует исключить из набора
                    managerIdSetIterator.remove();
                    disableExMap.remove(managerId);
                    // создание события о блокировке менеджера
                    Map<String, Object> event = new HashMap<>();
                    event.put("managerId", managerId);
                    try {
                        Map<String, Object> savedEvent = createProcessingEvent(
                                IMPORT_SESSION_EVENT_MANAGER_BLOCKED_ENTITY_NAME, importSessionId, blockedSystemRecordId,
                                event, params
                        );
                    } catch (ImportSessionException ex) {
                        // следует запомнить последнее исключение при попытке блокировки конкретной записи
                        disableEx = ex;
                    }
                }
                if (disableEx != null) {
                    // следует запомнить последнее исключение при попытке блокировки конкретной записи
                    disableExMap.put(managerId, disableEx);
                } else {
                    if (taskDetails != null) {
                        taskDetails.blocking.incItemsProcessed();
                    }
                }
                tryLogTaskDetails(taskDetails);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("nonDisabledIdsCount = %d", nonDisabledIdsCount));
                logger.debug(String.format("managerIdSet.size() = %d", managerIdSet.size()));
            }
        } while (nonDisabledIdsCount != managerIdSet.size());
        // список ИД блокируемых записей перестал сокращатся -
        if (taskDetails != null) {
            taskDetails.blocking.markFinish();
        }
        // список ИД блокируемых записей перестал сокращатся -
        // остались только записи, которые не удалось заблокировать:
        // исключения при их блокировке следует запротоколировать и выбросить любое из них в качестве глобального
        if (!disableExMap.isEmpty()) {
            ImportSessionException ex = null;
            for (Map.Entry<Long, ImportSessionException> disableEx : disableExMap.entrySet()) {
                ImportSessionException value = disableEx.getValue();
                // value должно всегда содержать исключение, но лишние проверки на null никогда не лишние
                if (value != null) {
                    ex = value;
                }
                logger.error(String.format("Disabling manager with id = %d caused exception:", disableEx.getKey()), ex);
            }
            // ex должно всегда содержать исключение, но лишние проверки на null никогда не лишние
            if (ex != null) {
                throw ex;
            }
        }
    }

    private void disableManager(Map<String, Object> manager, Long purgatoryDepartmentId, String login, String password) throws ImportSessionException {
        // блокировка аккаунта
        disableAccount(manager, login, password);
        // блокировка пользователя
        disableUser(manager, login, password);
        // блокировка сотрудника
        disableEmployee(manager, purgatoryDepartmentId, login, password);
        // при создании пользователя в adminws выполняется проверка только по CORE_USERACCOUNT.LOGIN без учета статусов и пр
        // поэтому следует после всех блокировок также сменить логин на уникальный и очень маловероятный
        changeAccountLogin(manager, login, password);
    }

    private Map<String, Object> disableAccount(Map<String, Object> manager, String login, String password) throws ImportSessionException {
        // параметры блокировки аккаунта
        Map<String, Object> accountParams = new HashMap<String, Object>();
        accountParams.put(RETURN_AS_HASH_MAP, true);
        // ИД пользователя
        Long userId = getLongParam(manager, USER_ID_PARAMNAME);
        accountParams.put("USERID", userId);
        // ИД аккаунта
        Long userAccountId = getLongParam(manager, USER_ACCOUNT_ID_PARAMNAME);
        accountParams.put("USERACCOUNTID", userAccountId);
        // статус
        accountParams.put("STATUS", "ARCHIVE"); // todo: в константы
        // отключение
        Map<String, Object> result;
        Exception accountEx = null;
        try {
            // установка для аккаунта STATUS = ARCHIVE
            result = callServiceLogged(ADMINWS_SERVICE_NAME, "dsAccountUpdateStatus", accountParams, login, password);
        } catch (Exception ex) {
            result = null;
            accountEx = ex;
        }
        // проверка результата
        if (isCallResultNotOK(result)) {
            String error = "Unable to archive existing account by calling dsAccountUpdateStatus from adminws service!";
            logger.error(error + " Details (call result): " + result);
            throw newImportSessionException(
                    "Не удалось заблокировать менеджера!",
                    error, accountEx
            );
        }
        return result;
    }

    private Map<String, Object> disableUser(Map<String, Object> manager, String login, String password) throws ImportSessionException {
        // параметры блокировки сотрудника
        Map<String, Object> userParams = new HashMap<String, Object>();
        userParams.put(RETURN_AS_HASH_MAP, true);
        // ИД сотрудника
        Long userId = getLongParam(manager, USER_ID_PARAMNAME);
        userParams.put(USER_ID_PARAMNAME, userId);
        // отключение
        Map<String, Object> result;
        Exception userEx = null;
        try {
            // установка для пользователя STATUS = BLOCKED,
            // (методов для пользователю статуса ARCHIVE в adminws не найдено)
            result = callServiceLogged(ADMINWS_SERVICE_NAME, "dsUserBlock", userParams, login, password);
        } catch (Exception ex) {
            result = null;
            userEx = ex;
        }
        // проверка результата
        // dsUserBlock в случае успеха возвращает результат вида { Result = TRUE }
        if ((result == null) || (!"TRUE".equals(getStringParam(result, RESULT)))) {
            String error = "Unable to block existing manager by calling dsUserBlock from adminws service!";
            logger.error(error + " Details (call result): " + result);
            throw newImportSessionException(
                    "Не удалось заблокировать менеджера!",
                    error, userEx
            );
        }
        return result;
    }

    // блокировка сотрудника
    private Map<String, Object> disableEmployee(Map<String, Object> manager, Long purgatoryDepartmentId, String login, String password) throws ImportSessionException {
        // параметры блокировки сотрудника
        Map<String, Object> employeeParams = new HashMap<String, Object>();
        employeeParams.put(RETURN_AS_HASH_MAP, true);
        // ИД сотрудника
        Long employeeId = getLongParam(manager, EMPLOYEE_ID_PARAMNAME);
        employeeParams.put(EMPLOYEE_ID_PARAMNAME, employeeId);
        // установка для сотрудника STATUS = ARCHIVE (через depEmployeeRemove) не позволит в дальнейшем удалить его подразделение
        // поэтому следует использовать для сотрудника установку STATUS = DELETED (это возможно только через depEmployeeUpdate)
        employeeParams.put("STATUS", "DELETED");
        /*
        // для depEmployeeUpdate указание подразделения является обязательным, хотя казалось бы ИД работника должно его однозначно идентифицировать
        // ИД подразделения сотрудника
        Long employeeDepartmentId = getLongParam(manager, DEPARTMENT_ID_PARAMNAME);
        employeeParams.put(DEPARTMENT_ID_PARAMNAME, employeeDepartmentId);
        */
        // ФТ v1.0: "УЗ, входящие в закрываемое подразделение: Деактивируются; Помещаются в системное подразделение;" */
        employeeParams.put(DEPARTMENT_ID_PARAMNAME, purgatoryDepartmentId);
        // установка для сотрудника STATUS = ARCHIVE (через depEmployeeRemove) не позволит в дальнейшем удалить его подразделение
        // поэтому следует использовать для сотрудника установку STATUS = DELETED (это возможно только через depEmployeeUpdate)
        String serviceName = ADMINWS_SERVICE_NAME;
        String methodName = "depEmployeeUpdate";
        // отключение
        Map<String, Object> result;
        Exception employeeEx = null;
        try {
            result = callServiceLogged(serviceName, methodName, employeeParams, login, password);
        } catch (Exception ex) {
            result = null;
            employeeEx = ex;
        }
        // проверка результата
        // ИД заблокированного работника подразделения из depEmployeeUpdate возвращается в ключе AUDITEVENTOBJECTID
        if (isCallResultNotOK(result) || (getLongParam(result, /*"EMPLOYEE_ID_PARAMNAME*/ AUDIT_EVENT_OBJECT_ID_PARAMNAME) == null)) {
            Long employeeDepartmentId = getLongParam(manager, DEPARTMENT_ID_PARAMNAME);
            String error = String.format(
                    "Unable to delete existing employee with id = %d (from department with id = %d) by calling %s from %s service (with params = %s)!",
                    employeeId, employeeDepartmentId, methodName, serviceName, employeeParams
            );
            logger.error(error + " Details (call result): " + result);
            throw newImportSessionException(
                    "Не удалось заблокировать менеджера!",
                    error, employeeEx
            );
        }
        return result;
    }

    /**
     * При создании пользователя в adminws выполняется проверка только по CORE_USERACCOUNT.LOGIN без учета статусов и пр.,
     * поэтому следует после всех блокировок также сменить логин на уникальный и очень маловероятный
     */
    private Map<String, Object> changeAccountLogin(Map<String, Object> manager, String login, String password) throws ImportSessionException {
        Map<String, Object> loginUpdateParams = new HashMap<>();
        loginUpdateParams.put(RETURN_AS_HASH_MAP, true);
        // ИД аккаунта
        Long userAccountId = getLongParam(manager, USER_ACCOUNT_ID_PARAMNAME);
        loginUpdateParams.put(USER_ACCOUNT_ID_PARAMNAME, userAccountId);
        // логин
        // String userAccountLogin = getStringParam(manager, USER_ACCOUNT_LOGIN_PARAMNAME);
        String code = getStringParam(manager, USER_ACCOUNT_CODE_PARAMNAME);
        String userAccountLogin = code + "_DELETED_" + System.currentTimeMillis();
        loginUpdateParams.put(USER_ACCOUNT_LOGIN_PARAMNAME, userAccountLogin);
        // отключение
        Map<String, Object> result;
        Exception accountEx = null;
        try {
            // установка для аккаунта STATUS = ARCHIVE
            result = callServiceLogged(THIS_SERVICE_NAME, "dsB2BCoreUserAccountLoginUpdate", loginUpdateParams, login, password);
        } catch (Exception ex) {
            result = null;
            accountEx = ex;
        }
        // проверка результата
        if (isCallResultNotOK(result)) {
            String error = "Unable to change login for blocked existing account by calling dsB2BCoreUserAccountLoginUpdate from b2bposws service!";
            logger.error(error + " Details (call result): " + result);
            throw newImportSessionException(
                    "Не удалось заблокировать менеджера!",
                    error, accountEx
            );
        }
        return result;
    }

    /** Основное подразделение события */
    protected Map<String, Object> addDepartmentInfoToEvent(Map<String, Object> event, Map<String, Object> department) {
        if (event == null) {
            event = new HashMap<>();
        }
        // основное подразделение события
        if ((department != null)) {
            Long departmentId = getLongParamLogged(department, DEPARTMENT_ID_PARAMNAME);
            if (departmentId != null) {
                event.put("departmentId", departmentId);
            }
            String departmentCode = getStringParamLogged(department, DEPARTMENT_CODE_PARAMNAME);
            if (!departmentCode.isEmpty()) {
                event.put("departmentCode", departmentCode);
            }
            String departmentName = getStringParamLogged(department, DEPARTMENT_FULL_NAME_PARAMNAME);
            if (!departmentName.isEmpty()) {
                event.put("departmentName", departmentName);
            }
        }
        return event;
    }

    /** Второе подразделение, участвующее в событии (совместно с основным) */
    protected Map<String, Object> addDepartment2InfoToEvent(Map<String, Object> event, Map<String, Object> department2) {
        if (event == null) {
            event = new HashMap<>();
        }
        // второе подразделение, участвующее в событии (совместно с основным)
        if (department2 != null) {
            Long department2Id = getLongParamLogged(department2, DEPARTMENT_ID_PARAMNAME);
            if (department2Id != null) {
                event.put("department2Id", department2Id);
            }
            String department2Code = getStringParamLogged(department2, DEPARTMENT_CODE_PARAMNAME);
            if (!department2Code.isEmpty()) {
                event.put("department2Code", department2Code);
            }
            String department2Name = getStringParamLogged(department2, DEPARTMENT_FULL_NAME_PARAMNAME);
            if (!department2Name.isEmpty()) {
                event.put("department2Name", department2Name);
            }
        }
        return event;
    }

    /*
    // блокировка сотрудника
    private Map<String, Object> disableEmployee(Map<String, Object> manager, String login, String password) throws ImportSessionException {
        // параметры блокировки сотрудника
        Map<String, Object> employeeParams = new HashMap<String, Object>();
        employeeParams.put(RETURN_AS_HASH_MAP, true);
        // ИД сотрудника
        Long employeeId = getLongParam(manager, EMPLOYEE_ID_PARAMNAME);
        employeeParams.put(EMPLOYEE_ID_PARAMNAME, employeeId);
        // отключение
        Map<String, Object> result;
        Exception employeeEx = null;
        try {
            // установка для работника STATUS = ARCHIVE
            result = callServiceLogged(ADMINWS_SERVICE_NAME, "depEmployeeRemove", employeeParams, login, password);
        } catch (Exception ex) {
            result = null;
            employeeEx = ex;
        }
        // проверка результата
        if (isCallResultNotOK(result)) {
            String error = "Unable to archive existing manager by calling depEmployeeRemove from adminws service!";
            logger.error(error + " Details (call result): " + result);
            throw newImportSessionException(
                    "Не удалось заблокировать менеджера!",
                    error, employeeEx
            );
        }
        return result;
    }
    */

    protected Map<String, Object> getOrCreateProcessingRootDepartment(ImportSessionClassifierPack importSessionClassifierPack, String login, String password) throws ImportSessionException {
        logger.debug("getOrCreateProcessingRootDepartment start...");
        Map<String, Object> processingRootDepartment = null;
        List<Map<String, Object>> processingRootDepartmentList = null;
        Exception processingRootDepartmentEx = null;
        try {
            processingRootDepartmentList = getDepartmentListByCodes(PROCESSING_ROOT_DEPT_CODE, PROCESSING_ROOT_DEPT_CODE, login, password);
        } catch (Exception ex) {
            processingRootDepartmentEx = ex;
        }
        if (processingRootDepartmentList != null) {
            // запрос завершился успешно
            if (processingRootDepartmentList.size() == 0) {
                // запрос завершился успешно, но головной узел не был найден
                // следует его (головной узел) создать
                Map<String, Object> createdProcessingRootDepartment = createProcessingRootDepartment(importSessionClassifierPack, login, password);
                // после создания следует перечитать данные о корневом подразделении (они требуется для назначения прав)
                Long processingRootDepartmentId = getLongParam(createdProcessingRootDepartment, DEPARTMENT_ID_PARAMNAME);
                processingRootDepartment = getDepartmentById(processingRootDepartmentId, login, password);
            } else if (processingRootDepartmentList.size() == 1) {
                // запрос завершился успешно, головной узел найден
                processingRootDepartment = processingRootDepartmentList.get(0);
            }
        }
        if (processingRootDepartment == null) {
            throw newImportSessionException(
                    "Не удалось определить или создать ключевое головное подразделение!",
                    "Unable to find (or create) current root node in partners department branch!",
                    processingRootDepartmentEx
            );
        }
        loggerDebugPretty(logger, "processingRootDepartment", processingRootDepartment);
        logger.debug("getOrCreateProcessingRootDepartment finished.");
        return processingRootDepartment;
    }

    private Map<String, Object> createProcessingRootDepartment(ImportSessionClassifierPack importSessionClassifierPack, String login, String password) throws ImportSessionException {
        Long partnersRootDepartmentId = getPartnersRootDepartmentId(login, password);
        Map<String, Object> departmentParams = new HashMap<String, Object>();
        departmentParams.put(DEPARTMENT_CODE_PARAMNAME, PROCESSING_ROOT_DEPT_CODE);
        departmentParams.put(DEPARTMENT_FULL_NAME_PARAMNAME, PROCESSING_ROOT_DEPT_NAME);
        departmentParams.put("DEPTSHORTNAME", PROCESSING_ROOT_DEPT_NAME);
        departmentParams.put("PARENTDEPARTMENT", partnersRootDepartmentId);
        // тип подразделения
        Long depTypeId = importSessionClassifierPack
                .get(KIND_IMPORT_SESSION_DEPARTMENT_TYPE_ENTITY_NAME)
                .getRecordFieldLongValueByFieldStringValue("name", PROCESSING_ROOT_DEPT_TYPE_NAME, "id");
        departmentParams.put("DEPTYPEID", depTypeId);
        // уровень подразделения
        Long deptLevelId = importSessionClassifierPack
                .get(KIND_IMPORT_SESSION_DEPARTMENT_LEVEL_ENTITY_NAME)
                .getRecordFieldLongValueByFieldStringValue("levelSysName", DEPTLEVEL_PARTNER_SYSNAME, "id");
        departmentParams.put("DEPTLEVEL", deptLevelId);
        // адрес подразделения
        Map<String, Object> departmentAddress = new HashMap<>();
        departmentAddress.put("REGIONSTR", "г.Москва"); // todo: в константы
        departmentAddress.put("LOCALITYSTR", "г.Москва"); // todo: в константы
        departmentParams.put("DEPADDRESS", departmentAddress);
        //
        departmentParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> processingRootDepartment = null;
        Exception processingRootDepartmentEx = null;
        try {
            processingRootDepartment = callServiceLogged(ADMINWS_SERVICE_NAME, "depStructureAdd", departmentParams, login, password);
        } catch (Exception ex) {
            processingRootDepartmentEx = ex;
        }
        // ИД созданного подразделения из depStructureAdd возвращается в ключе AUDITEVENTOBJECTID
        Long processingRootDepartmentId = getLongParam(processingRootDepartment, /*"DEPARTMENTID"*/ AUDIT_EVENT_OBJECT_ID_PARAMNAME);
        if ((processingRootDepartment == null) || (processingRootDepartmentId == null)) {
            logger.error("depStructureAdd result(processingRootDepartment): " + processingRootDepartment);
            throw newImportSessionException(
                    "Не удалось создать ключевое головное подразделение!",
                    "Unable to create current root node in partners department branch!",
                    processingRootDepartmentEx
            );
        } else {
            processingRootDepartment.put(DEPARTMENT_ID_PARAMNAME, processingRootDepartmentId);
        }
        return processingRootDepartment;
    }

    private Long getPartnersRootDepartmentId(String login, String password) throws ImportSessionException {
        logger.debug("getPartnersRootDepartmentId start...");
        Long partnersRootDepartmentId = null;
        List<Map<String, Object>> partnersRootDepartmentList = null;
        Exception departmentEx = null;
        try {
            String deptCode = PARTNERS_DEPARTMENT_CODE_LIKE;
            String parentDeptCode = PARTNERS_DEPARTMENT_CODE_LIKE;
            partnersRootDepartmentList = getDepartmentListByCodes(deptCode, parentDeptCode, login, password);
        } catch (Exception ex) {
            departmentEx = ex;
        }
        if ((partnersRootDepartmentList != null) && (partnersRootDepartmentList.size() == 1)) {
            Map<String, Object> partnersRootDepartment = partnersRootDepartmentList.get(0);
            partnersRootDepartmentId = getLongParamLogged(partnersRootDepartment, DEPARTMENT_ID_PARAMNAME);
        }
        if (partnersRootDepartmentId == null) {
            throw newImportSessionException(
                    "Не удалось определить ключевое подразделение, отвечающее за партнерсую сеть!",
                    "Unable to find root partners department entry!",
                    departmentEx
            );
        }
        logger.debug("getPartnersRootDepartmentId finished.");
        return partnersRootDepartmentId;
    }

    private Map<String, Object> getOrCreatePurgatoryDepartment(ImportSessionClassifierPack importSessionClassifierPack, String login, String password) throws ImportSessionException {
        logger.debug("getOrCreatePurgatoryDepartment start...");
        Map<String, Object> purgatoryDepartment = null;
        List<Map<String, Object>> purgatoryDepartmentList = null;
        Exception purgatoryDepartmentEx = null;
        try {
            purgatoryDepartmentList = getDepartmentListByCodes(PURGATORY_DEPT_CODE, PROCESSING_ROOT_DEPT_CODE, login, password);
        } catch (Exception ex) {
            purgatoryDepartmentEx = ex;
        }
        if (purgatoryDepartmentList != null) {
            // запрос завершился успешно
            if (purgatoryDepartmentList.size() == 0) {
                // запрос завершился успешно, но системное подразделение для заблокированных пользователей не было найдено
                // следует его (системное подразделение для заблокированных пользователей) создать
                Map<String, Object> createdPurgatoryDepartment = createPurgatoryDepartment(importSessionClassifierPack, login, password);
                // после создания следует перечитать данные о корневом подразделении (они могут потребоваться для назначения прав)
                Long purgatoryDepartmentId = getLongParam(createdPurgatoryDepartment, DEPARTMENT_ID_PARAMNAME);
                purgatoryDepartment = getDepartmentById(purgatoryDepartmentId, login, password);
            } else if (purgatoryDepartmentList.size() == 1) {
                // запрос завершился успешно, системное подразделение для заблокированных пользователей найдено
                purgatoryDepartment = purgatoryDepartmentList.get(0);
            }
        }
        if (purgatoryDepartment == null) {
            throw newImportSessionException(
                    "Не удалось определить или создать ключевое системное подразделение!",
                    "Unable to find (or create) purgatory node in partners department branch!",
                    purgatoryDepartmentEx
            );
        }
        loggerDebugPretty(logger, "purgatoryDepartment", purgatoryDepartment);
        logger.debug("getOrCreatePurgatoryDepartment finished.");
        return purgatoryDepartment;
    }

    private Map<String, Object> createPurgatoryDepartment(ImportSessionClassifierPack importSessionClassifierPack, String login, String password) throws ImportSessionException {
        // определение (или создание) корневой записи
        Map<String, Object> processingRootDepartment = getOrCreateProcessingRootDepartment(importSessionClassifierPack, login, password);
        Long processingRootDepartmentId = getLongParamLogged(processingRootDepartment, DEPARTMENT_ID_PARAMNAME);
        // параметры создаваемого подразделения
        Map<String, Object> departmentParams = new HashMap<String, Object>();
        departmentParams.put(DEPARTMENT_CODE_PARAMNAME, PURGATORY_DEPT_CODE);
        departmentParams.put(DEPARTMENT_FULL_NAME_PARAMNAME, PURGATORY_DEPT_NAME);
        departmentParams.put("DEPTSHORTNAME", PURGATORY_DEPT_NAME);
        departmentParams.put("PARENTDEPARTMENT", processingRootDepartmentId);
        // тип подразделения
        /*
        Long depTypeId = importSessionClassifierPack
                .get(KIND_IMPORT_SESSION_DEPARTMENT_TYPE_ENTITY_NAME)
                .getRecordFieldLongValueByFieldStringValue("name", ???, "id");
        departmentParams.put("DEPTYPEID", depTypeId);
        */
        // уровень подразделения
        Long deptLevelId = importSessionClassifierPack
                .get(KIND_IMPORT_SESSION_DEPARTMENT_LEVEL_ENTITY_NAME)
                .getRecordFieldLongValueByFieldStringValue("levelSysName", DEPTLEVEL_TERRITORIAL_BANK_SYSNAME, "id");
        departmentParams.put("DEPTLEVEL", deptLevelId);
        // адрес подразделения
        /*
        Map<String, Object> departmentAddress = new HashMap<>();
        departmentAddress.put("REGIONSTR", "г.Москва"); // todo: в константы
        departmentAddress.put("LOCALITYSTR", "г.Москва"); // todo: в константы
        departmentParams.put("DEPADDRESS", departmentAddress);
        */
        //
        departmentParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> purgatoryDepartment = null;
        Exception purgatoryDepartmentEx = null;
        try {
            purgatoryDepartment = callServiceLogged(ADMINWS_SERVICE_NAME, "depStructureAdd", departmentParams, login, password);
        } catch (Exception ex) {
            purgatoryDepartmentEx = ex;
        }
        // ИД созданного подразделения из depStructureAdd возвращается в ключе AUDITEVENTOBJECTID
        Long purgatoryDepartmentId = getLongParam(purgatoryDepartment, /*"DEPARTMENTID"*/ AUDIT_EVENT_OBJECT_ID_PARAMNAME);
        if ((purgatoryDepartment == null) || (purgatoryDepartmentId == null)) {
            logger.error("depStructureAdd result (purgatoryDepartment): " + purgatoryDepartment);
            throw newImportSessionException(
                    "Не удалось создать ключевое системное подразделение!",
                    "Unable to create purgatory node in partners department branch!",
                    purgatoryDepartmentEx
            );
        } else {
            purgatoryDepartment.put(DEPARTMENT_ID_PARAMNAME, purgatoryDepartmentId);
        }
        return purgatoryDepartment;
    }

    /** Получение ИД группы по системному наименованию */
    protected Long getGroupId(String groupSysName, String login, String password) throws ImportSessionException {
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
                    "Не удалось получить сведения о группе!",
                    error, groupListEx
            );
        }
        return groupId;
    }

}
