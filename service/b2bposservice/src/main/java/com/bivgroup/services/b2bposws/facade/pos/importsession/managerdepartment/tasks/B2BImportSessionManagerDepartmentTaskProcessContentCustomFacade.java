package com.bivgroup.services.b2bposws.facade.pos.importsession.managerdepartment.tasks;

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

@BOName("B2BImportSessionManagerDepartmentTaskProcessContentCustom")
public class B2BImportSessionManagerDepartmentTaskProcessContentCustomFacade extends B2BImportSessionTaskProcessContentBaseFacade {

    /** Стас/Саня: "kmsb1Project - сис. наименование роли для отдельной ссылки на страницу логина" */
    protected static final String MANAGER_LOGIN_URL_FIXED_ROLE = "kmsbOneProject";
    /** Ваня (16.05.2018): "также нужно добавлять роль корректор для всех создаваемых пользователей" */
    protected static final String MANAGER_CORRECTOR_ROLE = "b2bCorrector";

    protected static final String[] MANAGER_FIXED_ROLES = {
            // "InsPOSSeller" // Саня: "Не требуется, полностью настроены все три специальные роли"
            MANAGER_CORRECTOR_ROLE, // Ваня (16.05.2018): "также нужно добавлять роль корректор для всех создаваемых пользователей"
            MANAGER_LOGIN_URL_FIXED_ROLE // Стас/Саня: "kmsb1Project - сис. наименование роли для отдельной ссылки на страницу логина"
    };
    protected static final List<String> MANAGER_FIXED_ROLE_LIST = Arrays.asList(MANAGER_FIXED_ROLES);

    /** ФТ v1.0: "На первом этапе всех договоров определена группа СБ1" */
    protected static final String MANAGER_DEFAULT_GROUP_SYSNAME = DEFAULT_GROUP_SYSNAME;
    protected static final String[] MANAGER_FIXED_GROUPS = {
            MANAGER_DEFAULT_GROUP_SYSNAME // ФТ v1.0: "На первом этапе всех договоров определена группа СБ1"
    };
    protected static final List<String> MANAGER_FIXED_GROUPS_LIST = Arrays.asList(MANAGER_FIXED_GROUPS);

    /** DEP_EMPLOYEE.EMAIL */
    public static final String EMPLOYEE_EMAIL_PARAMNAME = "EMAIL";

    /** Количество потоков обработки (не должно превышать одного потока) */
    private static volatile int threadCount = 0;
    /** Подробные сведения о текущем состоянии импорта */
    private static volatile ImportSessionTaskDetails taskDetails = new ImportSessionTaskDetails();

    private ImportSessionTaskOptions importSessionTaskOptions = new ImportSessionTaskOptions(
            // Имя сущности обрабатываемой сессии импорта
            IMPORT_SESSION_MANAGER_DEPARTMENT_ENTITY_NAME,
            // Системное наименование состояния обрабатываемых сессий импорта
            B2B_IMPORTSESSION_INPROCESSQUEUE,
            // Системное наименование перехода в случае ошибке в ходе обработки содержимого сессии импорта
            B2B_IMPORTSESSION_FROM_INPROCESSQUEUE_TO_ERROR,
            // Системное наименование перехода в случае успешного завершения обработки содержимого сессии импорта
            B2B_IMPORTSESSION_FROM_INPROCESSQUEUE_TO_SUCCESS,
            // Имя сущности содержимого обрабатываемой сессии импорта
            IMPORT_SESSION_CONTENT_MANAGER_DEPARTMENT_ENTITY_NAME
    );

    @Override
    protected ImportSessionTaskOptions getImportSessionTaskOptions() {
        return importSessionTaskOptions;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionManagerDepartmentTaskProcessContentGetDetails(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerDepartmentTaskProcessContentGetDetails start...");
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = taskDetails.toMap();
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        logger.debug("dsB2BImportSessionManagerDepartmentTaskProcessContentGetDetails finished.");
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsB2BImportSessionManagerDepartmentTaskProcessContent(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerDepartmentTaskProcessContent start...");
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
        logger.debug("dsB2BImportSessionManagerDepartmentTaskProcessContent finished.");
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
            Object eventSearchValue;
            Map<String, Object> manager;
            String note;
            ImportSessionException contentProcessingEx;
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
                String importSessionContentEntityName = getImportSessionTaskOptions().getImportSessionContentEntityName();
                // непредвиденное исключение, возникшее при обработке содержимого
                contentProcessingEx = null;
                // основной цикл обработки
                Iterator<Long> contentIdSetIterator = contentIdSet.iterator();
                while (contentIdSetIterator.hasNext()) {
                    // состояние обработки
                    state = NOT_PROCESSED;
                    // ИД содержимого
                    Long contentId = null;
                    // ИД содержимого сессии импорта
                    Long importSessionCntId = null;
                    // свойства события, протоколируемого в ходе обработки
                    eventType = "";
                    manager = null;
                    eventSearchValue = null;
                    note = null;
                    // очистка списка событий, созданных в ходе обработки записи
                    savedEventList.clear();
                    // полные данные по содержимому
                    Map<String, Object> content = null;
                    try {
                        //logger.debug(String.format("id = %d", contentId));
                        // ИД содержимого
                        contentId = contentIdSetIterator.next();
                        // полные данные по содержимому
                        content = dctFindById(importSessionContentEntityName, contentId);
                        loggerDebugPretty(logger, importSessionContentEntityName, content);
                        // ИД содержимого сессии импорта
                        importSessionCntId = getLongParam(content, "id");
                        // табельный номер
                        Long managerPersonnelNumber = getLongParamLogged(content, "managerPersonnelNumber");
                        // проверка наличия дубликатов по табельному
                        List<Map<String, Object>> duplicateManagerList = getDuplicateManagerList(importSessionContentEntityName, managerPersonnelNumber, importSessionId);
                        if ((duplicateManagerList == null) || (duplicateManagerList.size() > 1)) {
                            // ошибка при поиске дубликатов или найдены дубликаты
                            loggerErrorPretty(logger, String.format(
                                    "For personnel number '%d' was not found one single entry! Details (found entries)",
                                    managerPersonnelNumber
                            ), duplicateManagerList);
                            // todo: другой тип ошибки, когда будет обновлено ФТ
                            eventType = IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME;
                            eventSearchValue = managerPersonnelNumber;
                            state = PROCESSED_WITH_ERROR;
                        }

                        if (state.isNotProcessed) {
                            // дубликатов по табельному не найдено
                            // проверка даты увольнения
                            Date managerDismissalDate = (Date) content.get("managerDismissalDate");
                            if ((managerDismissalDate != null) && (managerDismissalDate.before(MAX_SIGNIFICANT_DATE))) {
                                // сотрудник уволен - следует заблокировать учетную запись
                                loggerErrorPretty(logger, String.format(
                                        "For import session content with id = %d valid dismissal date was found! Details (content data)",
                                        contentId
                                ), content);
                                // todo: сотрудник уволен - следует заблокировать учетную запись (по новой версии ФТ мб не требуется)
                                // todo: другой тип ошибки, когда будет обновлено ФТ
                                // eventType = IMPORT_SESSION_EVENT_MANAGER_BLOCKED_ENTITY_NAME;
                                eventType = IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME;
                                eventSearchValue = managerPersonnelNumber;
                                state = PROCESSED_WITH_ERROR;
                            }
                        }

                        if (state.isNotProcessed) {
                            // проверка наличия действующего сотрудника в БД (по табельному)
                            List<Map<String, Object>> managerList = getManagerListByPersonnelNumberAndStatus(
                                    managerPersonnelNumber, EMPLOYEE_STATUS_ACTIVE, login, password
                            );
                            if (managerList.size() == 1) {
                                // существующий сотрудник найден
                                eventType = IMPORT_SESSION_EVENT_NO_CHANGES_ENTITY_NAME;
                                manager = managerList.get(0);
                                state = PROCESSED_SUCCESSFULLY;
                            } else if (managerList.isEmpty()) {
                                // существующего действующего сотрудника не найдено
                                // следует выполнить проверку наличия "отключенных" сотрудников (также по табельному)
                                List<Map<String, Object>> disabledManagerList = getManagerListByPersonnelNumberAndStatus(
                                        managerPersonnelNumber, EMPLOYEE_STATUS_DELETED, login, password
                                );
                                if (disabledManagerList.size() == 1) {
                                    // найден "отключенный" сотрудник - следует его активировать и продолжить использование
                                    manager = disabledManagerList.get(0);
                                    Long employeeId = getLongParam(manager, EMPLOYEE_ID_PARAMNAME);
                                    Long userAccountId = getLongParam(manager, USER_ACCOUNT_ID_PARAMNAME);
                                    Long userId = getLongParam(manager, USER_ID_PARAMNAME);
                                    if ((employeeId != null) && (userAccountId != null) && (userId != null)) {
                                        // разблокировка "отключенных" сотрудника, учетной записи и пользователя
                                        unDisableManager(login, password, managerPersonnelNumber, employeeId, userAccountId, userId);
                                        // todo: другой тип ошибки, когда/если будет обновлено ФТ
                                        eventType = IMPORT_SESSION_EVENT_MANAGER_CREATED_ENTITY_NAME;
                                        state = PROCESSED_SUCCESSFULLY;
                                    } else {
                                        String error = String.format(
                                                "Not all required for manager with code = '%s' un-disabling parameters was found! Details (manager data)",
                                                managerPersonnelNumber
                                        );
                                        loggerErrorPretty(logger, error, manager);
                                        eventType = IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME;
                                        eventSearchValue = managerPersonnelNumber;
                                        state = PROCESSED_WITH_ERROR;
                                    }
                                } else if (disabledManagerList.isEmpty()) {
                                    // ни действующего ни "отключенного" существующего сотрудника не найдено
                                    // следует проверить наличие такого логина во всей системе в целом
                                    boolean isAccountLoginExist = checkIsAccountLoginExist(managerPersonnelNumber, login, password);
                                    if (isAccountLoginExist) {
                                        // найдены учетные записи с логином, совпадающим с тем,
                                        // который будет использован при создании менеджера - следует фиксировать ошибку
                                        String error = String.format(
                                                "Account with login '%d' already exists outside of current partner departments tree! Details (manager data)",
                                                managerPersonnelNumber
                                        );
                                        loggerErrorPretty(logger, error, manager);
                                        eventType = IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME;
                                        eventSearchValue = managerPersonnelNumber;
                                        state = PROCESSED_WITH_ERROR;
                                        note = String.format(
                                                "Учётная запись с логином '%d' зарегистрирована для другого партнёра.",
                                                managerPersonnelNumber
                                        );
                                    } else {
                                        // ни действующего ни "отключенного" существующего сотрудника ни повторяющегося логина не найдено - следует создать менеджера
                                        // получение подразделения для нового сотрудника
                                        String departmentCode = getStringParam(content, "departmentCode");
                                        List<Map<String, Object>> departmentList = getDepartmentListByCode(departmentCode, login, password);
                                        if (departmentList.size() == 1) {
                                            // найденное подразделение
                                            Map<String, Object> department = departmentList.get(0);
                                            Long departmentId = getLongParam(department, DEPARTMENT_ID_PARAMNAME);
                                            // существующего сотрудника не найдено - следует создать
                                            manager = createManager(content, departmentId, login, password);
                                            eventType = IMPORT_SESSION_EVENT_MANAGER_CREATED_ENTITY_NAME;
                                            state = PROCESSED_SUCCESSFULLY;
                                        } else {
                                            // не удалось найти подразделение для нового сотрудника
                                            loggerErrorPretty(logger, String.format(
                                                    "Method getDepartmentListByCode reported no (or multiple) departments with code = '%s'. Details (result list)",
                                                    departmentCode
                                            ), departmentList);
                                            eventType = IMPORT_SESSION_EVENT_DEPARTMENT_NOT_FOUND_ENTITY_NAME;
                                            eventSearchValue = departmentCode;
                                            state = PROCESSED_WITH_ERROR;
                                        }
                                    }
                                } else {
                                    // найдено более одного "отключенного" менеджера
                                    loggerErrorPretty(logger, String.format(
                                            "Method getManagerListByPersonnelNumberAndStatus reported multiple managers with personnel number = '%s' and status = '%s'. Details (result list)",
                                            managerPersonnelNumber, EMPLOYEE_STATUS_DELETED
                                    ), disabledManagerList);
                                    // todo: другой тип ошибки, когда/если будет обновлено ФТ
                                    eventType = IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME;
                                    eventSearchValue = managerPersonnelNumber;
                                    state = PROCESSED_WITH_ERROR;
                                }
                            } else {
                                // найдено более одного менеджера
                                loggerErrorPretty(logger, String.format(
                                        "Method getManagerListByPersonnelNumberAndStatus reported multiple managers with personnel number = '%s' and status = '%s'. Details (result list)",
                                        managerPersonnelNumber, EMPLOYEE_STATUS_ACTIVE
                                ), managerList);
                                // todo: другой тип ошибки, когда/если будет обновлено ФТ
                                eventType = IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME;
                                eventSearchValue = managerPersonnelNumber;
                                state = PROCESSED_WITH_ERROR;
                            }
                        }
                        if (manager != null) {
                            // Пользователь либо найден либо создан|разблокирован - следует проверить и обновить его подразделение
                            boolean isManagerDepartmentCheckOrUpdateError = checkManagerDepartmentAndUpdateIfChanged(
                                    importSessionId, importSessionCntId, manager, content, savedEventList, params, login, password
                            );
                            if (isManagerDepartmentCheckOrUpdateError && (!state.isError)) {
                                // ошибки возникали в ходе проверки и/или обновления - следует отразить в state
                                state = PROCESSED_WITH_ERROR;
                            }
                            // если были зарегистрированы события И текущий тип события указывает на отсутствие изменений, то следует сбросить этот тип события
                            if ((!savedEventList.isEmpty()) && (IMPORT_SESSION_EVENT_NO_CHANGES_ENTITY_NAME.equals(eventType))) {
                                eventType = "";
                            }
                            // пользователь либо найден либо создан|разблокирован - следует обработать его роли
                            try {
                                processExistedManagerRoles(manager, content, login, password);
                            } catch (ImportSessionException ex) {
                                // ошибка при работе с ролями - следует регистрировать отдельное событие
                                logger.error("processExistedManagerRoles ex", ex);
                                eventType = IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME;
                                note = ex.getMessageHumanized();
                                if (state.isProcessed) {
                                    state = PROCESSED_WITH_ERROR;
                                }
                            }
                            // пользователь либо найден либо создан|разблокирован - следует обработать его группы
                            try {
                                processExistedManagerGroups(manager, content, login, password);
                            } catch (ImportSessionException ex) {
                                // ошибка при работе с ролями - следует регистрировать отдельное событие
                                logger.error("processExistedManagerGroups ex", ex);
                                eventType = IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME;
                                note = ex.getMessageHumanized();
                                if (state.isProcessed) {
                                    state = PROCESSED_WITH_ERROR;
                                }
                            }
                            // пользователь либо найден либо создан|разблокирован - следует проверить и обновить адрес электронной почты
                            // ФТ v1.0: "Если УЗ найдена в БД Фронта, выполнять проверку на изменение e-mail. Если e-mail изменился, то перезаписывать новым значением."
                            // ФТ v1.0: "При обновлении e-mail выполнять проверку на наличие «@sberbank.ru»: если e-mail не содержит «@sberbank.ru», то оставлять прежний e-mail."
                            checkManagerEMailAndUpdateIfChanged(manager, content, login, password);
                        }
                    } catch (ImportSessionException ex) {
                        contentProcessingEx = ex;
                    } catch (Exception ex) {
                        contentProcessingEx = newImportSessionException(
                                "Неизвестная ошибка при обработке данных из файла сессии импорта (подробные сведения сохранены в серверный протокол)!",
                                "Method doImportSessionProcess - unknown exception on processing import session file entry!",
                                ex
                        );
                    }

                    if (contentProcessingEx != null) {
                        String errorLogMsg = String.format(
                                "Processing import session content (with id = %d) caused exception with messages '%s' and '%s'! Details (content data): %s.",
                                contentId, contentProcessingEx.getLocalizedMessage(), contentProcessingEx.getMessageHumanized(), content
                        );
                        logger.error(errorLogMsg, contentProcessingEx);
                        eventType = IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME;
                        eventSearchValue = contentId;
                        state = PROCESSED_WITH_ERROR;
                        note = contentProcessingEx.getMessageHumanized();
                    }

                    if (state.isProcessed) {
                        // если запись обработана
                        if (!eventType.isEmpty()) {
                            // если запись обработана И установлен тип основного события для протоколирования - создание события
                            Map<String, Object> savedEvent = createManagerDepartmentProcessingEvent(
                                    eventType, importSessionId, importSessionCntId, manager, null, eventSearchValue, note,
                                    params, login, password
                            );
                            // дополнение списка событий, созданных в ходе обработки записи
                            savedEventList.add(savedEvent);
                        }
                        // удаление из итератора
                        contentIdSetIterator.remove();
                        // изменения состояния обработанной записи
                        String transSysName = (state.isError) ? "B2B_IMPORTSESSION_CNT_from_INPROCESSQUEUE_to_ERROR" : "B2B_IMPORTSESSION_CNT_from_INPROCESSQUEUE_to_SUCCESS";
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
            // список ИД обрабатываемых записей перестал сокращатся - окончание отсчета основного цикла
            taskDetails.reading.markFinish();
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
            // всех менеджеров, которые имеются в БД, но отсутствуют в файле данной сессии импорта следует заблокировать
            disableAllAbsentInImportSessionManagers(importSessionId, importSessionClassifierPack, taskDetails, params, login, password, dictionaryCaller);
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

    /** Пользователь либо найден либо создан|разблокирован - следует проверить и обновить его подразделение */
    private boolean checkManagerDepartmentAndUpdateIfChanged(Long importSessionId, Long importSessionCntId, Map<String, Object> manager, Map<String, Object> content, List<Map<String, Object>> savedEventList, Map<String, Object> params, String login, String password) throws ImportSessionException {
        // возникали ли ошибки в ходе проверки и/или обновления
        boolean isError = false;
        // следует проверить коды подразделений
        String departmentCode = getStringParam(content, "departmentCode");
        String managerDepartmentCode = getStringParam(manager, DEPARTMENT_CODE_PARAMNAME);
        if (managerDepartmentCode.isEmpty()) {
            // данные менеджера могут содержать только ИД подразделения (без кода)
            // в этом случае следует получить код подразделения из БД по ИД
            String departmentIdParamName = DEPARTMENT_ID_PARAMNAME;
            Long departmentId = getLongParam(manager, departmentIdParamName);
            Map<String, Object> department;
            try {
                department = getDepartmentById(departmentId, login, password);
            } catch (ImportSessionException ex) {
                // при определении исходного подразделение может быть не найдено (физически удалено из БД)
                // это маловероятная ситуация, но не критическая и не должна приводить к выбросу исключения
                department = null;
                logger.error(String.format(
                        "Unable to get department data for department with id (%s) = %d by dsB2BDepartmentBrowseListByParamEx. See previous logged errors for details.",
                        departmentIdParamName, departmentId
                ));
            }
            // код подразделения, полученный из БД по ИД подразделения
            managerDepartmentCode = getStringParam(department, DEPARTMENT_CODE_PARAMNAME);
        }
        if (departmentCode.equals(managerDepartmentCode)) {
            if (logger.isDebugEnabled()) {
                // доп. протоколирование
                logger.debug(String.format(
                        "For manager with id (%s) = %d department (with code '%s') is not changed.",
                        EMPLOYEE_ID_PARAMNAME, getLongParam(manager, EMPLOYEE_ID_PARAMNAME), departmentCode
                ));
            }
        } else {
            // коды подразделений отличаются - следует установить актуальный
            if (logger.isDebugEnabled()) {
                // доп. протоколирование
                logger.debug(String.format(
                        "For manager with id (%s) = %d required department change (from department with code '%s' to department with code '%s').",
                        EMPLOYEE_ID_PARAMNAME, getLongParam(manager, EMPLOYEE_ID_PARAMNAME), managerDepartmentCode, departmentCode
                ));
            }
            // коды подразделений отличаются - следует установить актуальный
            List<Map<String, Object>> departmentList = getDepartmentListByCode(departmentCode, login, password);
            // найденное подразделение
            Map<String, Object> department = checkDepartmentListAndChooseSingle(departmentList, login, password);
            // ИД найденного подразделения
            Long departmentId = getLongParam(department, DEPARTMENT_ID_PARAMNAME);
            if (departmentId == null) {
                // выбрать подразделение не удалось - создание дополнительного события
                String eventSearchValue = departmentCode;
                Map<String, Object> savedEvent = createManagerDepartmentProcessingEvent(
                        IMPORT_SESSION_EVENT_DEPARTMENT_NOT_FOUND_ENTITY_NAME, importSessionId, importSessionCntId,
                        manager, null, eventSearchValue, null,
                        params, login, password
                );
                // дополнение списка событий, созданных в ходе обработки записи
                savedEventList.add(savedEvent);
                // взведение флага ошибок
                isError = true;
            } else {
                // следует запомнить свойства текущего подразделения (для последующего фиксирования в событии)
                Long department2Id = getLongParam(manager, DEPARTMENT_ID_PARAMNAME);
                // обновление подразделения для существующего сотрудника
                manager = updateManagerDepartment(manager, departmentId, login, password);
                // создание дополнительного события
                Map<String, Object> savedEvent = createManagerDepartmentProcessingEvent(
                        IMPORT_SESSION_EVENT_MANAGER_TO_DEPARTMENT_CHANGED_ENTITY_NAME, importSessionId, importSessionCntId,
                        manager, department2Id, null, null,
                        params, login, password
                );
                // дополнение списка событий, созданных в ходе обработки записи
                savedEventList.add(savedEvent);
            }
        }
        return isError;
    }

    private List<Map<String, Object>> getDuplicateManagerList(String importSessionContentEntityName, Long managerPersonnelNumber, Long importSessionId) throws Exception {
        List<Map<String, Object>> duplicateManagerList = null;
        if (managerPersonnelNumber != null) {
            Map<String, Object> duplicateParams = new HashMap<>();
            duplicateParams.put("importSessionId", importSessionId);
            duplicateParams.put("managerPersonnelNumber", managerPersonnelNumber);
            duplicateManagerList = dctFindByExample(importSessionContentEntityName, duplicateParams);
        }
        return duplicateManagerList;
    }

    /**
     * ФТ v1.0: "Если УЗ найдена в БД Фронта, выполнять проверку на изменение e-mail. Если e-mail изменился, то перезаписывать новым значением."
     * ФТ v1.0: "При обновлении e-mail выполнять проверку на наличие «@sberbank.ru»: если e-mail не содержит «@sberbank.ru», то оставлять прежний e-mail."
     */
    private Map<String, Object> checkManagerEMailAndUpdateIfChanged(Map<String, Object> manager, Map<String, Object> content, String login, String password) throws ImportSessionException {
        String validEMailNew = getValidStringParam(content, MANAGER_EMAIL_KEYNAME, REGEXP_MANAGER_ACCOUNT_VALID_EMAIL);
        Long employeeId = getLongParam(manager, EMPLOYEE_ID_PARAMNAME);
        if (!validEMailNew.isEmpty()) {
            /*
            if (employeeId == null) {
                // todo: возможно, throw newImportSessionException или т.п.
                logger.error(String.format(
                        "Method checkManagerEMailAndUpdateIfChanged used on manager without id! Details (manager map): %s",
                        manager
                ));
            }
            */
            String validEMailCurrent = getStringParam(manager, EMPLOYEE_EMAIL_PARAMNAME);
            if (!validEMailNew.equalsIgnoreCase(validEMailCurrent)) {
                // параметры
                Map<String, Object> employeeParams = new HashMap<>();
                employeeParams.put(EMPLOYEE_ID_PARAMNAME, employeeId);
                employeeParams.put(EMPLOYEE_EMAIL_PARAMNAME, validEMailNew);
                // имя сервиса и метода
                String serviceName = THIS_SERVICE_NAME;
                String methodName = "dsB2BDepEmployeeExpressUpdate";
                //
                Map<String, Object> employeeUpdateResult;
                Exception updateEx = null;
                try {
                    employeeUpdateResult = callServiceLogged(serviceName, methodName, employeeParams, login, password);
                } catch (Exception ex) {
                    employeeUpdateResult = null;
                    updateEx = ex;
                }
                // проверка результата
                if (isCallResultNotOK(employeeUpdateResult)) {
                    String error = String.format("Unable to update manager email by %s from %s!", methodName, serviceName);
                    logger.error(error + " Details (call result): " + employeeUpdateResult);
                    throw newImportSessionException(
                            "Не удалось удалить выполнить обновление адреса электронной почты менеджера!",
                            error, updateEx
                    );
                } else {
                    // метод выполнен успешно - следует обновить данные менеджера
                    manager.put(EMPLOYEE_EMAIL_PARAMNAME, validEMailNew);
                }
            }
        }
        return manager;
    }

    private Map<String, Object> createManager(Map<String, Object> importSessionManager, Long departmentId, String login, String password) throws ImportSessionException {
        // параметры создания
        Map<String, Object> managerParams = new HashMap<String, Object>();
        managerParams.put(RETURN_AS_HASH_MAP, true);
        // ФТ: "Табельный номер КМ (ключевой атрибут для идентификации КМ);"
        Long managerPersonnelNumber = getLongParam(importSessionManager, "managerPersonnelNumber");
        managerParams.put("CODE", managerPersonnelNumber);
        // ФТ: "Логин для входа во Фронт (будет равен табельному номеру);"
        managerParams.put("LOGIN", managerPersonnelNumber);
        // ФИО - разбивка по пробелу согласно ФТ
        String managerFullName = getStringParam(importSessionManager, "managerFullName");
        String[] managerNames = managerFullName.split("\\s");
        managerParams.put("LASTNAME", (managerNames.length > 0) ? managerNames[0] : "");
        managerParams.put("FIRSTNAME", (managerNames.length > 1) ? managerNames[1] : "");
        managerParams.put("MIDDLENAME", (managerNames.length > 2) ? managerNames[2] : "");
        // ФТ: "Адрес e-mail;"
        // ФТ v1.0: "Обязательна проверка на наличие «@sberbank.ru» в почте. Если почта не содержит «@sberbank.ru», считать поле пустым."
        String managerEMail = getValidStringParam(importSessionManager, "managerEMail", REGEXP_MANAGER_ACCOUNT_VALID_EMAIL);
        if (!managerEMail.isEmpty()) {
            managerParams.put(EMPLOYEE_EMAIL_PARAMNAME, managerEMail);
        }
        // подразделение
        managerParams.put(DEPARTMENT_ID_PARAMNAME, departmentId);
        // Стас: "OBJECTTYPE = 2 обязательно для сотрудников партнеров"
        managerParams.put("OBJECTTYPE", 2);
        // копия из dsB2BAdminCreateUser, вероятно обязательные параметры
        managerParams.put("BLOCKIFINACTIVE", 0);
        managerParams.put("ISCONCURRENT", 1);
        managerParams.put("PHONE1", "");
        // в связи с изменениями в авторизации (подробнее см. #19635)
        // следует обязательно указывать проект по умолчанию для создаваемой учетной записи
        managerParams.put("DEFAULT_PROJECT_SYSNAME", "insurance");
        // todo: проверить поддержку должности в adminws
        Map<String, Object> managerPosition = getMapParam(importSessionManager, "managerPositionId_EN");
        String managerPositionName = getStringParam(managerPosition, "name");
        managerParams.put("POSITION", managerPositionName);
        // todo: назначение роли
        Map<String, Object> managerRole = getMapParam(importSessionManager, "managerRoleId_EN");
        String managerRoleSysName = getStringParam(managerRole, "sysName");
        // todo: проверить работу параметров ROLES и GROUPS
        /*
        managerParams.put("ROLES", );
        managerParams.put("GROUPS", );
        */
        // создание
        Map<String, Object> manager = null;
        Exception managerEx = null;
        try {
            manager = callServiceLogged(ADMINWS_SERVICE_NAME, "dsAccountCreate", managerParams, login, password);
        } catch (Exception ex) {
            managerEx = ex;
        }
        // ИД созданной учетной записи из dsAccountCreate возвращается в ключе EMPLOYEEID
        if ((manager == null) || (getLongParamLogged(manager, EMPLOYEE_ID_PARAMNAME) == null)) {
            logger.error("Unable to create new manager by calling dsAccountCreate from adminws service. Details (call result): " + manager);
            throw newImportSessionException(
                    "Не удалось создать менеджера!",
                    "Unable to create manager by calling dsAccountCreate!",
                    managerEx
            );
        } else {
            // manager.put(EMPLOYEE_ID_PARAMNAME, getLongParam(manager, EMPLOYEE_ID_PARAMNAME));
            manager.putIfAbsent(DEPARTMENT_ID_PARAMNAME, departmentId);
            loggerDebugPretty(logger, "Created manager", manager);
        }
        return manager;
    }

    private Map<String, Object> createManagerDepartmentProcessingEvent(
            String eventType, Long importSessionId, Long importSessionCntId,
            Map<String, Object> manager, Long department2Id, Object searchValue, String note,
            Map<String, Object> params, String login, String password
    ) throws ImportSessionException {
        // параметры события
        Map<String, Object> event = new HashMap<>();
        if (manager != null) {
            Long managerId = getLongParamLogged(manager, EMPLOYEE_ID_PARAMNAME);
            if (managerId != null) {
                event.put("managerId", managerId);
            }
            Long managerDepartmentId = getLongParamLogged(manager, DEPARTMENT_ID_PARAMNAME);
            Map<String, Object> department = getDepartmentById(managerDepartmentId, login, password);
            // основное подразделение события
            event = addDepartmentInfoToEvent(event, department);
        }
        // второе подразделение, участвующее в событии (совместно с основным)
        if (department2Id != null) {
            Map<String, Object> department2 = getDepartmentById(department2Id, login, password);
            // второе подразделение, участвующее в событии (совместно с основным)
            event = addDepartment2InfoToEvent(event, department2);
        }
        if (note != null) {
            event.put("note", note);
        }
        if (searchValue != null) {
            event.put("searchValue", getStringParam(searchValue));
        }
        Map<String, Object> savedEvent = createProcessingEvent(eventType, importSessionId, importSessionCntId, event, params);
        return savedEvent;
    }

    /*
    private Map<String, Object> createManagerDepartmentProcessingEvent(
            String eventType, Long eventManagerId, Long eventDepartmentId, Long importSessionId, Long importSessionCntId, Map<String, Object> params
    ) throws ImportSessionException {
        // сообщение, по умолчанию пусто
        String note = null;
        // параметры события
        Map<String, Object> savedEvent = createManagerDepartmentProcessingEvent(
                eventType, eventManagerId, eventDepartmentId, importSessionId, importSessionCntId, note, params
        );
        return savedEvent;
    }
    */

    // пользователь либо найден либо создан - следует обработать его роли
    private void processExistedManagerRoles(Map<String, Object> manager, Map<String, Object> content, String login, String password) throws ImportSessionException {
        List<Map<String, Object>> managerRoleList = getManagerRoleList(manager, login, password);
        Map<String, Object> managerRole = getMapParam(content, "managerRoleId_EN");
        String managerRoleSysName = getStringParam(managerRole, "sysName");
        Set<String> reqRoleSet = new HashSet<>();
        reqRoleSet.add(managerRoleSysName);
        reqRoleSet.addAll(MANAGER_FIXED_ROLE_LIST);
        // удаление лишних ролей
        for (Map<String, Object> role : managerRoleList) {
            String roleSysName = getStringParam(role, "ROLESYSNAME");
            if (!roleSysName.isEmpty()) {
                if (reqRoleSet.contains(roleSysName)) {
                    // роль имеется - следует исключить её из набора требующихся
                    reqRoleSet.remove(roleSysName);
                } else {
                    // удаление роли, отсутствующей в списке требующихся
                    deleteManagerRole(manager, roleSysName, login, password);
                }
            }
        }
        // содание недостающих ролей
        for (String roleSysName : reqRoleSet) {
            try {
                createManagerRole(manager, roleSysName, login, password);
            } catch (ImportSessionException ex) {
                if (MANAGER_LOGIN_URL_FIXED_ROLE.equals(roleSysName)) {
                    // Стас: "Сама роль будет добавлена в БД и настроена позднее, на данный момент следует подавлять исключение"
                    // todo: не подавлять исключение, когда роль будет добавлена в БД и настроена
                    logger.error(String.format("" +
                                    "Suppressed throwing exception for adding role with system name '%s' " +
                                    "(TODO: remove suppression after registering role in DB)! Details (suppressed exception):",
                            roleSysName
                    ), ex);
                } else {
                    throw ex;
                }
            }
        }
    }

    // пользователь либо найден либо создан - следует обработать его группы
    private void processExistedManagerGroups(Map<String, Object> manager, Map<String, Object> content, String login, String password) throws ImportSessionException {
        List<Map<String, Object>> managerGroupList = getManagerGroupList(manager, login, password);
        Set<String> reqGroupSet = new HashSet<>();
        reqGroupSet.addAll(MANAGER_FIXED_GROUPS_LIST);
        // удаление из лишних групп
        for (Map<String, Object> group : managerGroupList) {
            String groupSysName = getStringParam(group, "SYSNAME");
            if (!groupSysName.isEmpty()) {
                if (reqGroupSet.contains(groupSysName)) {
                    // группа имеется - следует исключить её из набора требующихся
                    reqGroupSet.remove(groupSysName);
                } else {
                    // удаление группы, отсутствующей в списке требующихся
                    deleteManagerFromGroup(manager, groupSysName, login, password);
                }
            }
        }
        // добавление в недостающие группы
        for (String groupSysName : reqGroupSet) {
            addManagerToGroup(manager, groupSysName, login, password);
        }
    }

    /*
    protected Set<String> getManagerRoleSysNamesSet(Map<String, Object> manager, String login, String password) throws ImportSessionException {
        Set<String> managerRoleSysNamesSet = null;
        List<Map<String, Object>> managerRoleList = getManagerRoleList(manager, login, password);
        if (managerRoleList != null) {
            managerRoleSysNamesSet = new HashSet<>();
            for (Map<String, Object> managerRole : managerRoleList) {
                String roleSysName = getStringParam(managerRole, "SYSNAME");
                if (!roleSysName.isEmpty()) {
                    managerRoleSysNamesSet.add(roleSysName);
                }
            }
        }
        if (managerRoleSysNamesSet == null) {
            throw newImportSessionException(
                    "Не удалось определить имеющеся роли менеджера!",
                    "Unable to get manager role list by admAccountRoleList!"
            );
        }
        return managerRoleSysNamesSet;
    }
    */

    protected List<Map<String, Object>> getManagerRoleList(Map<String, Object> manager, String login, String password) throws ImportSessionException {
        Long userAccountId = getUserAccountId(manager);
        List<Map<String, Object>> roleList = null;
        Exception roleListException = null;
        Map<String, Object> managerParams = new HashMap<>();
        managerParams.put(USER_ACCOUNT_ID_PARAMNAME, userAccountId);
        try {
            roleList = callServiceAndGetListFromResultMapLogged(
                    ADMINWS_SERVICE_NAME, "admAccountRoleList", managerParams, login, password
            );
        } catch (Exception ex) {
            roleListException = ex;
        }
        if (roleList == null) {
            throw newImportSessionException(
                    "Не удалось определить имеющеся роли менеджера!",
                    "Unable to get manager role list by admAccountRoleList!",
                    roleListException
            );
        }
        return roleList;
    }

    protected void createManagerRole(Map<String, Object> manager, String roleSysName, String login, String password) throws ImportSessionException {
        String methodName = "admRoleUserAdd";
        processManagerRole(manager, roleSysName, methodName, login, password);
    }

    protected void deleteManagerRole(Map<String, Object> manager, String roleSysName, String login, String password) throws ImportSessionException {
        String methodName = "admRoleUserRemove";
        processManagerRole(manager, roleSysName, methodName, login, password);
    }

    protected void processManagerRole(Map<String, Object> manager, String roleSysName, String methodName, String login, String password) throws ImportSessionException {
        Long userAccountId = getUserAccountId(manager);
        List<Map<String, Object>> roleList = getRoleList(roleSysName, login, password);
        for (Map<String, Object> role : roleList) {
            Long roleId = getLongParam(role, "ROLEID");
            Map<String, Object> roleParams = new HashMap<>();
            roleParams.put(USER_ACCOUNT_ID_PARAMNAME, userAccountId);
            roleParams.put("ROLEID", roleId);
            Map<String, Object> processResult;
            Exception processEx = null;
            try {
                processResult = callServiceLogged(
                        ADMINWS_SERVICE_NAME, methodName, roleParams, login, password
                );
            } catch (Exception ex) {
                processResult = null;
                processEx = ex;
            }
            if (isCallResultNotOK(processResult)) {
                String error = String.format("Unable to process manager role by %s from adminws!", methodName);
                logger.error(error + " Details (call result): " + processResult);
                throw newImportSessionException(
                        "Не удалось удалить выполнить операцию с ролями менеджера!",
                        error, processEx
                );
            }
        }
    }

    protected List<Map<String, Object>> getManagerGroupList(Map<String, Object> manager, String login, String password) throws ImportSessionException {
        Long userAccountId = getUserAccountId(manager);
        List<Map<String, Object>> groupList = null;
        Exception groupListException = null;
        Map<String, Object> managerParams = new HashMap<>();
        managerParams.put(USER_ACCOUNT_ID_PARAMNAME, userAccountId);
        try {
            groupList = callServiceAndGetListFromResultMapLogged(
                    ADMINWS_SERVICE_NAME, "admAccountGroupList", managerParams, login, password
            );
        } catch (Exception ex) {
            groupListException = ex;
        }
        if (groupList == null) {
            throw newImportSessionException(
                    "Не удалось определить имеющеся группы менеджера!",
                    "Unable to get manager group list by admAccountGroupList!",
                    groupListException
            );
        }
        return groupList;
    }

    protected void addManagerToGroup(Map<String, Object> manager, String groupSysName, String login, String password) throws ImportSessionException {
        String methodName = "admGroupUserAdd";
        processManagerGroup(manager, groupSysName, methodName, login, password);
    }

    protected void deleteManagerFromGroup(Map<String, Object> manager, String groupSysName, String login, String password) throws ImportSessionException {
        String methodName = "admGroupUserRemove";
        processManagerGroup(manager, groupSysName, methodName, login, password);
    }

    protected void processManagerGroup(Map<String, Object> manager, String groupSysName, String methodName, String login, String password) throws ImportSessionException {
        Long userId = getUserId(manager);
        Long groupId = getGroupId(groupSysName, login, password);
        if ((userId != null) && (groupId != null)) {
            Map<String, Object> roleParams = new HashMap<>();
            // roleParams.put(USER_ACCOUNT_ID_PARAMNAME, userAccountId);
            roleParams.put("USERID", userId);
            roleParams.put("USERGROUPID", groupId);
            Map<String, Object> processResult;
            Exception processEx = null;
            try {
                processResult = callServiceLogged(
                        ADMINWS_SERVICE_NAME, methodName, roleParams, login, password
                );
            } catch (Exception ex) {
                processResult = null;
                processEx = ex;
            }
            if (isCallResultNotOK(processResult)) {
                String error = String.format("Unable to process manager group by %s from adminws!", methodName);
                logger.error(error + " Details (call result): " + processResult);
                throw newImportSessionException(
                        "Не удалось удалить выполнить операцию с группами менеджера!",
                        error, processEx
                );
            }
        }
    }

    private Long getUserAccountId(Map<String, Object> manager) throws ImportSessionException {
        Long userAccountId = getLongParamLogged(manager, USER_ACCOUNT_ID_PARAMNAME);
        if (userAccountId == null) {
            String error = "Unable to get user account id from manager data!";
            loggerErrorPretty(logger, error + " Details (manager map)", manager);
            throw newImportSessionException(
                    "Не удалось получить данные менеджера для определения имеющихся ролей!",
                    error
            );
        }
        return userAccountId;
    }

    private Long getUserId(Map<String, Object> manager) throws ImportSessionException {
        Long userId = getLongParamLogged(manager, USER_ID_PARAMNAME);
        if (userId == null) {
            String error = "Unable to get user id from manager data!";
            loggerErrorPretty(logger, error + " Details (manager map)", manager);
            throw newImportSessionException(
                    "Не удалось получить сведения о пользователе для менеджера!",
                    error
            );
        }
        return userId;
    }

    private List<Map<String, Object>> getRoleList(String roleSysName, String login, String password) throws ImportSessionException {
        Map<String, Object> roleParams = new HashMap<>();
        roleParams.put("SEARCHCOLUMN", "ROLESYSNAME");
        roleParams.put("SEARCHTEXT", roleSysName);
        Exception roleEx = null;
        List<Map<String, Object>> rawRoleList;
        try {
            rawRoleList = callServiceAndGetListFromResultMapLogged(
                    ADMINWS_SERVICE_NAME, "admUserRole", roleParams, login, password
            );
        } catch (Exception ex) {
            rawRoleList = null;
            roleEx = ex;
        }
        if (rawRoleList == null) {
            throw newImportSessionException(
                    "Не удалось получить сведения о роли по её системному наименованию!",
                    "Unable to get role data by system name by admUserRole!",
                    roleEx
            );
        }
        List<Map<String, Object>> roleList = new ArrayList<>();
        for (Map<String, Object> rawRole : rawRoleList) {
            String sysName = getStringParam(rawRole, "ROLESYSNAME");
            if (roleSysName.equals(sysName)) {
                roleList.add(rawRole);
            }
        }
        return roleList;
    }

    /** всех менеджеров, которые имеются в БД, но отсутствуют в файле данной сессии импорта следует заблокировать */
    // todo: можно упростить избыточную логику, т.к. это копия deleteAllAbsentInImportSessionDepartments
    private void disableAllAbsentInImportSessionManagers(Long importSessionId, ImportSessionClassifierPack importSessionClassifierPack, ImportSessionTaskDetails taskDetails, Map<String, Object> params, String login, String password, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        // параметры менеджеров, которых следует заблокировать
        Map<String, Object> managerParams = new HashMap<>();
        managerParams.put("PARENTDEPTCODE", PROCESSING_ROOT_DEPT_CODE);
        managerParams.put("NOTINIMPORTSESSIONWITHID", importSessionId);

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
                    "Не удалось выполнить поиск менеджеров, отсутствующих в импортированном файле!",
                    "Unable to browse managers not in imported session for deleting by dsB2BEmployeeBrowseListByParamEx!",
                    managerListEx
            );
        } else if (!managerList.isEmpty()) {
            // следует заблокировать всех менеджеров из полученного списка
            disableManagers(
                    managerList, importSessionId, null,
                    importSessionClassifierPack, taskDetails,
                    params, login, password, dictionaryCaller
            );
        }
    }

    /** разблокировка "отключенных" сотрудника, учетной записи и пользователя */
    private void unDisableManager(String login, String password, Long managerPersonnelNumber, Long employeeId, Long userAccountId, Long userId) throws ImportSessionException {
        Map<String, Object> unDisableEmployeeResult = unDisableEmployee(employeeId, login, password);
        Map<String, Object> unDisableUserAccountResult = unDisableUserAccount(userAccountId, managerPersonnelNumber, login, password);
        Map<String, Object> unDisableUserResult = unDisableUser(userId, login, password);
    }

    private Map<String, Object> unDisableEmployee(Long employeeId, String login, String password) throws ImportSessionException {
        Map<String, Object> updateResult = null;
        Exception updateEx = null;
        String serviceName = THIS_SERVICE_NAME;
        String methodName = "dsB2BDepEmployeeStatusUpdate";
        if (employeeId != null) {
            Map<String, Object> statusUpdateParams = new HashMap<>();
            statusUpdateParams.put(EMPLOYEE_ID_PARAMNAME, employeeId);
            statusUpdateParams.put(STATUS_PARAMNAME, STATUS_ACTIVE);
            statusUpdateParams.put(RETURN_AS_HASH_MAP, true);
            // обновление
            try {
                updateResult = callServiceLogged(serviceName, methodName, statusUpdateParams, login, password);
            } catch (Exception ex) {
                updateEx = ex;
            }
        }
        // проверка результата
        if (!isCallResultOKAndContainsLongValue(updateResult, EMPLOYEE_ID_PARAMNAME, employeeId)) {
            String error = String.format(
                    "Unable to un-disable employee with id (%s) = %d by calling %s from %s service. Details (call result): %s",
                    EMPLOYEE_ID_PARAMNAME, employeeId, methodName, serviceName, updateResult
            );
            // logger.error(error);
            throw newImportSessionException(
                    "Не удалось разблокировать менеджера!",
                    error, updateEx
            );
        }
        return updateResult;
    }

    private Map<String, Object> unDisableUserAccount(Long userAccountId, Long managerPersonnelNumber, String login, String password) throws ImportSessionException {
        Map<String, Object> updateResult = null;
        Exception updateEx = null;
        String serviceName = THIS_SERVICE_NAME;
        String methodName = "dsB2BCoreUserAccountStatusUpdate";
        if (userAccountId != null) {
            Map<String, Object> statusUpdateParams = new HashMap<>();
            statusUpdateParams.put(USER_ACCOUNT_ID_PARAMNAME, userAccountId);
            // вместе с разблокировкой следует вернуть назад нормальный логин пользователя
            statusUpdateParams.put(USER_ACCOUNT_LOGIN_PARAMNAME, managerPersonnelNumber);
            statusUpdateParams.put(STATUS_PARAMNAME, STATUS_ACTIVE);
            statusUpdateParams.put(RETURN_AS_HASH_MAP, true);
            // обновление
            try {
                updateResult = callServiceLogged(serviceName, methodName, statusUpdateParams, login, password);
            } catch (Exception ex) {
                updateEx = ex;
            }
        }
        // проверка результата
        if (!isCallResultOKAndContainsLongValue(updateResult, USER_ACCOUNT_ID_PARAMNAME, userAccountId)) {
            String error = String.format(
                    "Unable to un-disable user account with id (%s) = %d by calling %s from %s service. Details (call result): %s",
                    USER_ACCOUNT_ID_PARAMNAME, userAccountId, methodName, serviceName, updateResult
            );
            // logger.error(error);
            throw newImportSessionException(
                    "Не удалось разблокировать менеджера!",
                    error, updateEx
            );
        }
        return updateResult;
    }

    private Map<String, Object> unDisableUser(Long userId, String login, String password) throws ImportSessionException {
        Map<String, Object> updateResult = null;
        Exception updateEx = null;
        String serviceName = THIS_SERVICE_NAME;
        String methodName = "dsB2BCoreUserStatusUpdate";
        if (userId != null) {
            Map<String, Object> statusUpdateParams = new HashMap<>();
            statusUpdateParams.put(USER_ID_PARAMNAME, userId);
            statusUpdateParams.put(STATUS_PARAMNAME, STATUS_ACTIVE);
            statusUpdateParams.put(RETURN_AS_HASH_MAP, true);
            // обновление
            try {
                updateResult = callServiceLogged(serviceName, methodName, statusUpdateParams, login, password);
            } catch (Exception ex) {
                updateEx = ex;
            }
        }
        // проверка результата
        if (!isCallResultOKAndContainsLongValue(updateResult, USER_ID_PARAMNAME, userId)) {
            String error = String.format(
                    "Unable to un-disable user with id (%s) = %d by calling %s from %s service. Details (call result): %s",
                    USER_ID_PARAMNAME, userId, methodName, serviceName, updateResult
            );
            // logger.error(error);
            throw newImportSessionException(
                    "Не удалось разблокировать менеджера!",
                    error, updateEx
            );
        }
        return updateResult;
    }

    private Map<String, Object> updateManagerDepartment(Map<String, Object> manager, Long departmentId, String login, String password) throws ImportSessionException {
        Map<String, Object> updateResult = null;
        Exception updateEx = null;
        String serviceName = THIS_SERVICE_NAME;
        String methodName = "dsB2BDepEmployeeDepartmentUpdate";
        Long employeeId = getLongParam(manager, EMPLOYEE_ID_PARAMNAME);
        if ((employeeId != null) && (departmentId != null)) {
            Map<String, Object> statusUpdateParams = new HashMap<>();
            statusUpdateParams.put(EMPLOYEE_ID_PARAMNAME, employeeId);
            statusUpdateParams.put(DEPARTMENT_ID_PARAMNAME, departmentId);
            statusUpdateParams.put(RETURN_AS_HASH_MAP, true);
            // обновление
            try {
                updateResult = callServiceLogged(serviceName, methodName, statusUpdateParams, login, password);
            } catch (Exception ex) {
                updateEx = ex;
            }
        }
        // проверка результата
        if (!isCallResultOKAndContainsLongValue(updateResult, EMPLOYEE_ID_PARAMNAME, employeeId)) {
            String error = String.format(
                    "Unable to update department id (%s) to %d for employee with id (%s) = %d by calling %s from %s service. Details (call result): %s",
                    DEPARTMENT_ID_PARAMNAME, departmentId, EMPLOYEE_ID_PARAMNAME, employeeId, methodName, serviceName, updateResult
            );
            // logger.error(error);
            throw newImportSessionException(
                    "Не удалось изменить связь менеджера с подразделением!",
                    error, updateEx
            );
        } else {
            // следует обновить значение ИД подразделения в сведениях о менеджере
            manager.put(DEPARTMENT_ID_PARAMNAME, departmentId);
        }
        return manager;
    }

    // todo: переместить в отдельный фасад (по работе с сотрудниками и пр.)
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BEmployeeBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = selectQuery("dsB2BEmployeeBrowseListByParamEx", params);
        return result;
    }

    /**
     * При создании пользователя в adminws выполняется проверка только по CORE_USERACCOUNT.LOGIN без учета статусов и пр.,
     * поэтому следует после всех блокировок также сменить логин на уникальный и очень маловероятный
     */
    // todo: переместить в отдельный фасад (по работе с сотрудниками и пр.)
    @WsMethod(requiredParams = {USER_ACCOUNT_ID_PARAMNAME, USER_ACCOUNT_LOGIN_PARAMNAME})
    public Map<String, Object> dsB2BCoreUserAccountLoginUpdate(Map<String, Object> params) throws Exception {
        this.updateQuery("dsB2BCoreUserAccountLoginUpdate", params);
        Map<String, Object> result = new HashMap<>();
        result.put(USER_ACCOUNT_ID_PARAMNAME, params.get(USER_ACCOUNT_ID_PARAMNAME));
        return result;
    }

    /** Ресурсосберегающий вариант обновления второстепенных атрибутов (например, адреса электронной) почты сотрудника */
    // todo: переместить в отдельный фасад (по работе с сотрудниками и пр.)
    @WsMethod(requiredParams = {EMPLOYEE_ID_PARAMNAME, EMPLOYEE_EMAIL_PARAMNAME})
    public Map<String, Object> dsB2BDepEmployeeExpressUpdate(Map<String, Object> params) throws Exception {
        this.updateQuery("dsB2BDepEmployeeExpressUpdate", params);
        Map<String, Object> result = new HashMap<>();
        result.put(EMPLOYEE_ID_PARAMNAME, params.get(EMPLOYEE_ID_PARAMNAME));
        return result;
    }

    /** Разблокировка "отключенного" ранее сотрудника */
    // todo: переместить в отдельный фасад (по работе с сотрудниками и пр.)
    @WsMethod(requiredParams = {EMPLOYEE_ID_PARAMNAME, STATUS_PARAMNAME})
    public Map<String, Object> dsB2BDepEmployeeStatusUpdate(Map<String, Object> params) throws Exception {
        this.updateQuery("dsB2BDepEmployeeStatusUpdate", params);
        Map<String, Object> result = new HashMap<>();
        result.put(EMPLOYEE_ID_PARAMNAME, params.get(EMPLOYEE_ID_PARAMNAME));
        return result;
    }

    /** Разблокировка "отключенной" ранее учетной записи (с опциональным обновлением логина) */
    // todo: переместить в отдельный фасад (по работе с сотрудниками и пр.)
    @WsMethod(requiredParams = {USER_ACCOUNT_ID_PARAMNAME, STATUS_PARAMNAME})
    public Map<String, Object> dsB2BCoreUserAccountStatusUpdate(Map<String, Object> params) throws Exception {
        this.updateQuery("dsB2BCoreUserAccountStatusUpdate", params);
        Map<String, Object> result = new HashMap<>();
        result.put(USER_ACCOUNT_ID_PARAMNAME, params.get(USER_ACCOUNT_ID_PARAMNAME));
        return result;
    }

    /** Разблокировка "отключенного" ранее пользователя */
    // todo: переместить в отдельный фасад (по работе с сотрудниками и пр.)
    @WsMethod(requiredParams = {USER_ID_PARAMNAME, STATUS_PARAMNAME})
    public Map<String, Object> dsB2BCoreUserStatusUpdate(Map<String, Object> params) throws Exception {
        this.updateQuery("dsB2BCoreUserStatusUpdate", params);
        Map<String, Object> result = new HashMap<>();
        result.put(USER_ID_PARAMNAME, params.get(USER_ID_PARAMNAME));
        return result;
    }

    /** Установка нового подразделения для конкретного сотрудника */
    // todo: переместить в отдельный фасад (по работе с сотрудниками и пр.)
    @WsMethod(requiredParams = {EMPLOYEE_ID_PARAMNAME, DEPARTMENT_ID_PARAMNAME})
    public Map<String, Object> dsB2BDepEmployeeDepartmentUpdate(Map<String, Object> params) throws Exception {
        this.updateQuery("dsB2BDepEmployeeDepartmentUpdate", params);
        Map<String, Object> result = new HashMap<>();
        result.put(EMPLOYEE_ID_PARAMNAME, params.get(EMPLOYEE_ID_PARAMNAME));
        return result;
    }

}
