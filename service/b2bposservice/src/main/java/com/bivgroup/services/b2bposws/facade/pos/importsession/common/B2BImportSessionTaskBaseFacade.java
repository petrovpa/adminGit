package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

import com.bivgroup.services.b2bposws.facade.pos.dictionary.DictionaryCaller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class B2BImportSessionTaskBaseFacade extends B2BImportSessionBaseFacade {

    /** "Строки с пустым значением кода головного подразделения не подлежать импорту, кроме строки с собственным кодом подразделения = 099 (ПАО Сбербанк)" */
    protected static final String PROCESSING_ROOT_DEPT_CODE = "099";

    /** ФТ v1.0: "УЗ, входящие в закрываемое подразделение: Деактивируются; Помещаются в системное подразделение;" */
    protected static final String PURGATORY_DEPT_CODE = "purgatory";

    /** Некоторые методы из adminws возвращают ИД обработанной/созданной записи в ключе AUDITEVENTOBJECTID */
    protected static final String AUDIT_EVENT_OBJECT_ID_PARAMNAME = "AUDITEVENTOBJECTID";

    /** ФТ: "E-mail: *@*.*" */
    protected static final String REGEXP_MANAGER_FILE_VALID_EMAIL = "^(.+)\\@(.+)\\.(.+)$";
    /** ФТ v1.0: "Обязательна проверка на наличие «@sberbank.ru» в почте. Если почта не содержит «@sberbank.ru», считать поле пустым." */
    protected static final String REGEXP_MANAGER_ACCOUNT_VALID_EMAIL = "^(.+)\\@sberbank\\.ru$";
    protected static final String MANAGER_EMAIL_KEYNAME = "managerEMail";

    protected ImportSessionException newImportSessionException(String messageHumanized, String message) {
        ImportSessionException importSessionException = newImportSessionException(messageHumanized, message, null);
        return importSessionException;
    }

    protected ImportSessionException newImportSessionException(String messageHumanized, String message, Throwable cause) {
        ImportSessionException importSessionException;
        if (cause == null) {
            importSessionException = new ImportSessionException(messageHumanized, message);
        } else {
            importSessionException = new ImportSessionException(messageHumanized, message, cause);
        }
        logger.error(importSessionException);
        return importSessionException;
    }

    protected abstract ImportSessionTaskOptions getImportSessionTaskOptions();

    protected Long getImportSessionId(Map<String, Object> importSession) throws ImportSessionException {
        Long importSessionId = getLongParamLogged(importSession, "id");
        if (importSessionId == null) {
            throw newImportSessionException(
                    "Ошибка при получении данных сессии импорта!",
                    "No id was found in importSession map parameter!"
            );
        }
        return importSessionId;
    }

    /*
    protected ImportSessionClassifier getImportSessionClassifier(String clsName) throws ImportSessionException {
        ImportSessionClassifier departmentTypeCls;
        try {
            departmentTypeCls = new ImportSessionClassifier(clsName);
        } catch (Exception ex) {
            throw newImportSessionException(
                    "Ошибка при получении сведений классификаторов, необходимых для корректного импорта данных!",
                    "Unable to create ImportSessionClassifier for all required classifiers!",
                    ex
            );
        }
        return departmentTypeCls;
    }
    */

    protected void changeImportSessionState(Long importSessionId, String importSessionTransSysName, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        try {
            String importSessionEntityName = getImportSessionTaskOptions().getImportSessionEntityName();
            makeTransition(importSessionEntityName, importSessionId, importSessionTransSysName, dictionaryCaller);
        } catch (Exception ex) {
            throw newImportSessionException(
                    "Ошибка при изменении состояния сессии импорта!",
                    "Unable to change import session state by dctMakeTransitionWithoutTransactionControl!",
                    ex
            );
        }
    }

    protected void changeImportSessionContentState(Long importSessionCntId, String importSessionContentTransSysName, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        try {
            String importSessionContentEntityName = getImportSessionTaskOptions().getImportSessionContentEntityName();
            makeTransition(importSessionContentEntityName, importSessionCntId, importSessionContentTransSysName, dictionaryCaller);
        } catch (Exception ex) {
            throw newImportSessionException(
                    "Ошибка при изменении состояния содержимого сессии импорта!",
                    "Unable to change import session content state by dctMakeTransition / dctMakeTransitionWithoutTransactionControl!",
                    ex
            );
        }
    }

    protected void makeTransition(String entityName, Long entityId, String transitionSysName) throws Exception {
        // обычная смена состояния
        DictionaryCaller dictionaryCaller = null;
        makeTransition(entityName, entityId, transitionSysName, dictionaryCaller);
    }

    protected void makeTransition(String entityName, Long entityId, String transitionSysName, DictionaryCaller dictionaryCaller) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(
                    "Changing state for '%s' with id = %d by transition '%s'.",
                    entityName, entityId, transitionSysName
            ));
        }
        // long startMs = System.currentTimeMillis();
        if (dictionaryCaller == null) {
            // обычная смена состояния
            // todo: скопировать актуальные версии методов по переводу состояния из СБС
            dctMakeTransition(entityName, entityId, transitionSysName);
        } else {
            // смена состояния с внешним управлением транзакцией
            // todo: скопировать актуальные версии методов по переводу состояния из СБС
            dctMakeTransitionWithoutTransactionControl(entityName, entityId, transitionSysName, dictionaryCaller);
        }
        // logger.error(String.format("[NOT ERROR] dctMakeTransition/dctMakeTransitionWithoutTransactionControl takes %d ms.", System.currentTimeMillis() - startMs));
        // todo: скопировать с СБС вариант с возвратом данных о новом состоянии и добавить его возврат
    }

    protected void changeImportSessionStateToSuccess(Long importSessionId, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        String importSessionSuccessTransSysName = getImportSessionTaskOptions().getImportSessionSuccessTransSysName();
        changeImportSessionState(importSessionId, importSessionSuccessTransSysName, dictionaryCaller);
    }

    protected void changeImportSessionStateToError(Long importSessionId, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        String importSessionErrorTransSysName = getImportSessionTaskOptions().getImportSessionErrorTransSysName();
        changeImportSessionState(importSessionId, importSessionErrorTransSysName, dictionaryCaller);
    }

    protected void changeImportSessionContentStateToInProcessQueue(Map<String, Object> importSessionContent, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        Long importSessionCntId = getLongParam(importSessionContent, "id");
        changeImportSessionContentStateToInProcessQueue(importSessionCntId, dictionaryCaller);
    }

    protected void changeImportSessionContentStateToInProcessQueue(Long importSessionCntId, DictionaryCaller dictionaryCaller) throws ImportSessionException {
        String importSessionContentTransSysName = B2B_IMPORTSESSION_CNT_from_INVALID_to_INPROCESSQUEUE;
        changeImportSessionContentState(importSessionCntId, importSessionContentTransSysName, dictionaryCaller);
    }

    protected Map<String, Object> analyzeGlobalExceptionAndChangeImportSessionState(ImportSessionException ex, Map<String, Object> importSession) {
        String loggedMsg = String.format(
                "[analyzeGlobalExceptionAndChangeImportSessionState] doImportSessionProcess caused exception with messages '%s' and '%s': ",
                ex.getLocalizedMessage(), ex.getMessageHumanized()
        );
        logger.error(loggedMsg, ex);
        // todo: если потребуетя, то реализовать сохранение текста ошибки (согласно текущему ФТ не нужно)
        // смена состояния сессии импорта (на "ошибочное")
        Long importSessionId = getLongParamLogged(importSession, "id");
        if (importSessionId != null) {
            try {
                // todo: скопировать актуальные версии методов по переводу состояния из СБС
                String importSessionEntityName = getImportSessionTaskOptions().getImportSessionEntityName();
                String transSysName = getImportSessionTaskOptions().getImportSessionErrorTransSysName();
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format(
                            "Changing state for '%s' with id = %d by transition '%s'.",
                            importSessionEntityName, importSessionId, transSysName
                    ));
                }
                makeTransition(importSessionEntityName, importSessionId, transSysName);
            } catch (Exception transEx) {
                logger.error(
                        "[analyzeGlobalExceptionAndChangeImportSessionState] Failed to changing import session state! Details (exception): ", ex
                );
            }
        } else {
            logger.error(
                    "[analyzeGlobalExceptionAndChangeImportSessionState] Unable to found import session id - changing import session state cant be performed! Details (import session): "
                            + importSession
            );
        }
        HashMap<String, Object> processResult = new HashMap<>();
        processResult.put(ERROR, ex.getMessageHumanized());
        return processResult;
    }

    protected DictionaryCaller initDictionaryCallerAndBeginTransaction() throws ImportSessionException {
        DictionaryCaller dictionaryCaller;
        try {
            String importSessionContentEntityName = getImportSessionTaskOptions().getImportSessionContentEntityName();
            dictionaryCaller = getDictionaryCallerByEntityNameAndBeginTransaction(importSessionContentEntityName);
        } catch (Exception ex) {
            throw newImportSessionException(
                    "Ошибка при инициализации базы данных!",
                    "Unable to create dictionary caller and/or begin transaction!",
                    ex
            );
        }
        return dictionaryCaller;
    }

    protected void analyzeProcessingExceptionAndRollback(DictionaryCaller dictionaryCaller, Exception processingException) throws ImportSessionException {
        ImportSessionException importSessionException;
        if (processingException instanceof ImportSessionException) {
            importSessionException = (ImportSessionException) processingException;
        } else {
            importSessionException = newImportSessionException(
                    "Ошибка при обработке данных из файла сессии импорта (подробные сведения сохранены в серверный протокол)!",
                    "Method doImportSessionProcess - unknown exception on processing import session file entry!",
                    processingException
            );
        }
        if (dictionaryCaller != null) {
            try {
                dictionaryCaller.rollback();
            } catch (Exception ex) {
                String message = "Unable to rollback DB transaction!";
                logger.error(message, ex);
                importSessionException.addMessages(
                        "Отмену внесенных в БД изменений выполнить не удалось!",
                        message
                );
            }
        }
        throw importSessionException;
    }

    protected void commit(DictionaryCaller dictionaryCaller, ImportSessionTaskDetails taskDetails) throws ImportSessionException {
        try {
            logger.debug("Committing changes to DB...");
            taskDetails.commit.markStart();
            tryLogTaskDetails(taskDetails);
            if (dictionaryCaller == null) {
                logger.debug("No external transaction was found - no commit required.");
            } else {
                dictionaryCaller.commit();
            }
            taskDetails.commit.markFinish();
            taskDetails.commit.markAllItemsProcessed();
            forceLogTaskDetails(taskDetails);
            logger.debug("Committing changes to DB finished.");
        } catch (Exception ex) {
            throw newImportSessionException(
                    "Ошибка при сохранении сведений в базу данных на этапе импорта из файла!",
                    "Unable to commit transaction with saving records to database by dctCrudByHierarchyWithoutTransactionControl and dictionaryCaller.commit!",
                    ex
            );
        }
    }

    protected abstract Map<String, Object> doImportSessionProcess(Map<String, Object> importSession, Map<String, Object> params, ImportSessionTaskDetails taskDetails) throws ImportSessionException;

    protected Map<String, Object> doImportSessionProcessAndCatchEx(Map<String, Object> importSession, Map<String, Object> params, ImportSessionTaskDetails taskDetails) {
        Map<String, Object> processResult = null;
        try {
            processResult = doImportSessionProcess(importSession, params, taskDetails);
        } catch (ImportSessionException ex) {
            processResult = analyzeGlobalExceptionAndChangeImportSessionState(ex, importSession);
        } finally {
            // отметка об окончании
            taskDetails.markFinish();
            // протоколирование и получение мапы со сведениями о задании
            Map<String, Object> taskDetailsMap = forceLogTaskDetails(taskDetails);
            // формирование результата, включение в него мапы со сведениями о задании
            if (processResult == null) {
                processResult = new HashMap<>();
            }
            processResult.put("taskDetails", taskDetailsMap);
        }
        return processResult;
    }

    protected Map<String, Object> doImportSessionListProcess(Map<String, Object> params, ImportSessionTaskDetails taskDetails) {
        logger.debug("doImportSessionListProcess start...");
        loggerDebugPretty(logger, "doImportSessionListProcess params", params);
        // boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> result = new HashMap<>();
        String importSessionProcessedStateSysName = getImportSessionTaskOptions().getImportSessionProcessedStateSysName();
        Long stateId = null;
        try {
            stateId = getEntityRecordIdBySysName(SM_STATE_ENTITY_NAME, importSessionProcessedStateSysName);
        } catch (Exception ex) {
            logger.error("Unable to get state id by getEntityRecordIdBySysName! Details (exception): ", ex);
        }
        if (stateId == null) {
            result = makeErrorResult(result, "Не удалось получить из БД сведения о состояних для формирования ограничения обрабатываемых сессий импорта");
        } else {
            Map<String, Object> importSessionParams = new HashMap<>();
            importSessionParams.put("stateId", stateId);
            String importSessionEntityName = getImportSessionTaskOptions().getImportSessionEntityName();
            List<Map<String, Object>> importSessionList = null;
            try {
                importSessionList = dctFindByExample(importSessionEntityName, importSessionParams);
            } catch (Exception ex) {
                logger.error("Unable to get import session list by dctFindByExample! Details (exception): ", ex);
            }
            // согласно ФТ файлы импортироваться должны по одному (и, вероятно, раз в день),
            // соответственно следует обрабатывать одну сессию импорта за один вызов регламентного задания
            if (importSessionList == null) {
                result = makeErrorResult(result, "Не удалось получить из БД сведения о состояних для формирования ограничения обрабатываемых сессий импорта");
            } else {
                if ((importSessionList.size() > 0)) {
                    sortByDateFieldName(importSessionList, "createDate", true, true);
                    Map<String, Object> importSession = importSessionList.get(0);
                    result = doImportSessionProcessAndCatchEx(importSession, params, taskDetails);
                }
            }
        }
        loggerDebugPretty(logger, "doImportSessionListProcess result", result);
        logger.debug("doImportSessionListProcess finish.");
        return result;
    }

    private void logTaskDetailsMap(Map<String, Object> taskDetailsMap) {
        if (taskDetailsMap != null) {
            loggerErrorPretty(logger, "[NOT ERROR] [TASKDETAILS] Task details", taskDetailsMap);
        }
    }

    protected Map<String, Object> tryLogTaskDetails(ImportSessionTaskDetails taskDetails) {
        Map<String, Object> taskDetailsMap = taskDetails.checkForLogging(logger);
        logTaskDetailsMap(taskDetailsMap);
        return taskDetailsMap;
    }

    protected Map<String, Object> forceLogTaskDetails(ImportSessionTaskDetails taskDetails) {
        Map<String, Object> taskDetailsMap = taskDetails.toMap();
        logTaskDetailsMap(taskDetailsMap);
        return taskDetailsMap;
    }

}
