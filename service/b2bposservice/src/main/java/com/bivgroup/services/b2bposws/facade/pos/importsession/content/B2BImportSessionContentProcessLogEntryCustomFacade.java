package com.bivgroup.services.b2bposws.facade.pos.importsession.content;

import com.bivgroup.services.b2bposws.facade.pos.importsession.common.B2BImportSessionBaseFacade;
import org.apache.commons.jxpath.JXPathContext;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BImportSessionContentProcessLogEntryCustom")
public class B2BImportSessionContentProcessLogEntryCustomFacade  extends B2BImportSessionBaseFacade {

    private static final Pattern PROCESSING_LOG_MESSAGE_PATTERN = Pattern.compile("(%)(.*?)(%)");

    /** Получение протокола обработки содержимого сессии импорта по ИД содержимого сессии импорта  */
    @WsMethod(requiredParams = {"importSessionCntId"})
    public Map<String, Object> dsB2BImportSessionContentProcessLogEntryLoad(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionContentProcessLogEntryLoad...");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        Long importSessionCntId = getLongParamLogged(params, "importSessionCntId");
        Map<String, Object> entryParams = new HashMap<>();
        entryParams.put("importSessionCntId", importSessionCntId);
        List<Map<String, Object>> resultList = dctFindByExample(IMPORT_SESSION_CONTENT_PROCESS_LOG_ENTRY_ENTITY_NAME, entryParams);
        if (resultList != null) {
            for (Map<String, Object> logEntry : resultList) {
                if (logEntry != null) {
                    // добавление вычислимых значений
                    String entityName = getStringParam(logEntry, ENTITY_TYPE_NAME_FIELD_NAME);
                    if (!entityName.isEmpty()) {
                        Map<String, Object> eventType = getEntityRecordByOtherFieldValue(KIND_IMPORT_SESSION_CONTENT_PROCESS_LOG_EVENT_ENTITY_NAME, "entityName", entityName);
                        logEntry.put("name", getStringParam(eventType, "name"));
                        String message;
                        // текст сообщения из события
                        String eventNote = getStringParam(logEntry, "note");
                        if (eventNote.isEmpty()) {
                            // текст сообщения из справочника событий
                            // message = getStringParam(eventType, "messageTemplate");
                            // формирование текста для колонки "Описание" окна "Детализация по записи" в журнале содержимого
                            message = getEventNoteStr(logEntry, eventType, login, password);
                            // todo: специальная особая динамическая генерации сообщения/описания события
                        } else {
                            // текст сообщения из события
                            message = eventNote;
                        }
                        // сообщение по события
                        logEntry.put("message", message);
                    }
                    // удаление избыточных сведений
                    logEntry.remove("importSessionCntId_EN");
                }
            }
        }
        if (isCallFromGate) {
            // сортировка по дате (для интерфейса)
            sortByDateFieldName(resultList, "createDate", true, true);
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(resultList);
        }
        Map<String, Object> result = new HashMap<>();
        result.put(RESULT, resultList);
        logger.debug("dsB2BImportSessionContentProcessLogEntryLoad finished.");
        return result;
    }

    /** формирование текста для колонки "Описание" окна "Детализация по записи" в журнале содержимого */
    private String getEventNoteStr(Map<String, Object> logEntry, Map<String, Object> eventType, String login, String password) {
        logger.debug("[getEventNoteStr] Generating info note texts start...");
        String messageTemplate = getStringParam(eventType, "messageTemplate");
        String message = messageTemplate;
        if (!messageTemplate.isEmpty()) {
            Matcher matcher = PROCESSING_LOG_MESSAGE_PATTERN.matcher(messageTemplate);
            Long contractId = null;
            Long managerId = null;
            Long manager2Id = null;
            Long groupId = null;
            Set<String> keySet = new HashSet<>();
            while (matcher.find()) {
                String key = matcher.group(2);
                if ((managerId == null) && (key.contains("managerId_EN"))) {
                    // требуются доп. данные по ссылке на менеджера
                    managerId = getLongParam(logEntry, "managerId");
                }
                if ((manager2Id == null) && (key.contains("manager2Id_EN"))) {
                    // требуются доп. данные по ссылке на менеджера
                    manager2Id = getLongParam(logEntry, "manager2Id");
                }
                if ((contractId == null) && (key.contains("contractId_EN"))) {
                    // требуются доп. данные по ссылке на договор
                    contractId = getLongParam(logEntry, "contractId");
                }
                if ((groupId == null) && (key.contains("groupId_EN"))) {
                    // требуются доп. данные по ссылке на группу
                    groupId = getLongParam(logEntry, "groupId");
                }
                keySet.add(key);
            }
            loggerDebugPretty(logger, "[getEventNoteStr] keySet", keySet);
            if (!keySet.isEmpty()) {
                if (managerId != null) {
                    // требуются доп. данные по ссылке на менеджера
                    Map<String, Object> manager = getManagerByEmployeeId(managerId, login, password);
                    logEntry.putIfAbsent("managerId_EN", manager);
                }
                if (manager2Id != null) {
                    // требуются доп. данные по ссылке на второго менеджера
                    Map<String, Object> manager2 = getManagerByEmployeeId(manager2Id, login, password);
                    logEntry.putIfAbsent("manager2Id_EN", manager2);
                }
                if (contractId != null) {
                    // требуются доп. данные по ссылке на второго менеджера
                    Map<String, Object> contract = getContractBriefInfoById(contractId, login, password);
                    logEntry.putIfAbsent("contractId_EN", contract);
                }
                if (groupId != null) {
                    // требуются доп. данные по ссылке на второго менеджера
                    Map<String, Object> group = getGroupById(groupId, login, password);
                    logEntry.putIfAbsent("groupId_EN", group);
                }
                JXPathContext context = JXPathContext.newContext(logEntry);
                context.setLenient(true);
                for (String key : keySet) {
                    String keyValue = null;
                    try {
                        keyValue = getStringParam(context.getValue(key));
                    } catch (Exception ex) {
                        logger.error(String.format(
                                "[getEventNoteStr] Unable to resolve x-path '%s' in map '%s'. Details (exception):",
                                key, logEntry
                        ), ex);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format(
                                "[getEventNoteStr] key = %s; value = %s",
                                key, keyValue
                        ));
                    }
                    if (keyValue == null) {
                        keyValue = "";
                    }
                    message = message.replaceAll("%" + key + "%", keyValue);
                }
            }
            // loggerDebugPretty(logger, "message = ", message);
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("[getEventNoteStr] message = %s", message));
            }
        }
        logger.debug("Generating info note texts finished.");
        return message;
    }

    protected Map<String, Object> getManagerByEmployeeId(Long employeeId, String login, String password) {
        Map<String, Object> manager = null;
        Exception managerListEx = null;
        if (employeeId != null) {
            Map<String, Object> managerParams = new HashMap<>();
            managerParams.put(EMPLOYEE_ID_PARAMNAME, employeeId);
            managerParams.put(RETURN_AS_HASH_MAP, true);
            try {
                manager = callServiceLogged(
                        THIS_SERVICE_NAME, "dsB2BEmployeeBrowseListByParamEx", managerParams, login, password
                );
            } catch (Exception ex) {
                managerListEx = ex;
            }
        }
        if (isCallResultNotOK(manager)) {
            logger.error(String.format(
                    "Unable to browse manager with employee id = %d by dsB2BEmployeeBrowseListByParamEx! Details (call result): %s",
                    employeeId, manager
            ), managerListEx);
            manager = null;
        }
        return manager;
    }

    protected Map<String, Object> getContractBriefInfoById(Long contractId, String login, String password) {
        Map<String, Object> contract = null;
        Exception contractEx = null;
        if (contractId != null) {
            Map<String, Object> contractParams = new HashMap<>();
            contractParams.put(CONTRACT_ID_PARAMNAME, contractId);
            contractParams.put(RETURN_AS_HASH_MAP, true);
            try {
                contract = callServiceLogged(
                        THIS_SERVICE_NAME, "dsB2BImportSessionContractBrowseListByIdBrief", contractParams, login, password
                );
            } catch (Exception ex) {
                contractEx = ex;
            }
        }
        if (isCallResultNotOK(contract)) {
            logger.error(String.format(
                    "Unable to browse contract with contract id = %d by dsB2BImportSessionContractBrowseListByIdBrief! Details (call result): %s",
                    contractId, contract
            ), contractEx);
            contract = null;
        }
        return contract;
    }

    protected Map<String, Object> getGroupById(Long groupId, String login, String password) {
        Map<String, Object> group = null;
        Exception groupEx = null;
        if (groupId != null) {
            Map<String, Object> groupParams = new HashMap<>();
            groupParams.put("USERGROUPID", groupId);
            groupParams.put(RETURN_AS_HASH_MAP, true);
            try {
                group = callServiceLogged(
                        THIS_SERVICE_NAME, "dsB2BUserGroupBrowseListByParamEx", groupParams, login, password
                );
            } catch (Exception ex) {
                groupEx = ex;
            }
        }
        if (isCallResultNotOK(group)) {
            logger.error(String.format(
                    "Unable to browse group with group id = %d by dsB2BUserGroupBrowseListByParamEx! Details (call result): %s",
                    groupId, group
            ), groupEx);
            group = null;
        }
        return group;
    }

    /** Получение кратких данных договора по ИД договора (для динамического формирирования сообщения события обработки содержимого сессии импорта) */
    // todo: переместить в отдельный фасад (по работе с договорами при импорте и пр.)
    @WsMethod(requiredParams = {CONTRACT_ID_PARAMNAME})
    public Map<String, Object> dsB2BImportSessionContractBrowseListByIdBrief(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BImportSessionContractBrowseListByIdBrief", params);
        return result;
    }

}
