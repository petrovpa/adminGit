/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.primaryactivity.facade.pos.contract.custom;

import com.bivgroup.ws.primaryactivity.facade.B2BPrimaryActivityBaseFacade;
import com.bivgroup.ws.primaryactivity.system.Constants;
import com.bivgroup.ws.primaryactivity.system.DatesParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.system.external.ExternalService;

/**
 *
 * @author averichevsm
 */
@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("PrimaryActivityContractAgentReportCustom")
public class B2BPrimaryActivityContractAgentReportCustomFacade extends B2BPrimaryActivityBaseFacade {

    protected static final String[] PAC_AGENT_REPORT_CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES = {
        "T",
        "T2",
        "T4"
    };

    protected static final String[] PAC_AGENT_REPORT_CUSTOM_WHERE_SUPPORTED_FIELDS = {
        "SYSNAME",
        "AGENTID",
        "MAINACTCONTRID",
        "PRODID",
        "REPORTNUMBER"
    };

    protected static final String[] PAC_AGENT_REPORT_HISTORY_CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES = {
        "T",
        "T2"
    };

    protected static final String[] PAC_AGENT_REPORT_HISTORY_CUSTOM_WHERE_SUPPORTED_FIELDS = {
        "OBJID",
        "TYPENAME",
        "ID",
        "STATEID",
        "STATENAME",
        "PUBLICNAME",
        "STARTDATE"
    };

    private final Logger logger = Logger.getLogger(this.getClass());

    // Имя сервиса для вызова методов из b2bposservice
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    // Имя сервиса для вызова методов из primaryactivity
    private static final String THIS_SERVICE_NAME = Constants.B2BPOSWS;
    // Имя сервиса для вызова методов по формированию и печати файлов экспорта
    private static final String SIGN_B2BPOSWS_SERVICE_NAME = Constants.SIGNB2BPOSWS;

    private static final String SESSIONPARAM_USERACCOUNTID = Constants.SESSIONPARAM_USERACCOUNTID;

    private static DatesParser datesParser;

    // флаг подробного протоколирования операций с датами и переопределения параметров
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

    // флаг подробного протоколирования вызовов методов веб-сервисов
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_CALLS_LOGGING = logger.isDebugEnabled();

    // имя поля с PK в таблице отчетов агента
    private static final String AGENT_REPORT_PK_FIELD_NAME = "MACAGENTREPORTID";

    // имя поля с PK в таблице содержимого отчета агента
    private static final String AGENT_REPORT_CONTENT_PK_FIELD_NAME = "MACAGENTREPORTCNTID";

    // имя ключа, указывающиего на комментарий к изменению состояния
    // (комментарий сохраняется в текущее состояние, затем копируется в историю состояний)
    private static final String STATE_NOTE_KEY_NAME = "TOSTATENOTE";

    private static volatile int agentReportsProcessingThreadCount = 0;

    // Состояния отчета агента:
    //     * Новый
    private static final String B2B_MACAGENTREPORT_NEW = "B2B_MACAGENTREPORT_NEW";
    //     * Поставлен в очередь
    private static final String B2B_MACAGENTREPORT_INQUEUE = "B2B_MACAGENTREPORT_INQUEUE";
    //     * Создан
    private static final String B2B_MACAGENTREPORT_CREATED = "B2B_MACAGENTREPORT_CREATED";
    //     * На согласовании Агента
    private static final String B2B_MACAGENTREPORT_IN_AGENT = "B2B_MACAGENTREPORT_IN_AGENT";
    //     * На согласовании СК
    private static final String B2B_MACAGENTREPORT_IN_SK = "B2B_MACAGENTREPORT_IN_SK";
    //     * Акцептован
    private static final String B2B_MACAGENTREPORT_ACCEPTED = "B2B_MACAGENTREPORT_ACCEPTED";

    //<editor-fold defaultstate="collapsed" desc="Переходы между состояниями отчета агента (на данный момент не используются)">
    // Переходы между состояниями отчета агента (на данный момент не используются)
    //private static final String B2B_MACAGENTREPORT_FROM_IN_AGENT_TO_IN_SK = "B2B_MACAGENTREPORT_FROM_IN_AGENT_TO_IN_SK";
    //private static final String B2B_MACAGENTREPORT_FROM_IN_SK_TO_ACCEPTED = "B2B_MACAGENTREPORT_FROM_IN_SK_TO_ACCEPTED";
    //private static final String B2B_MACAGENTREPORT_FROM_IN_SK_TO_IN_AGENT = "B2B_MACAGENTREPORT_FROM_IN_SK_TO_IN_AGENT";
    //private static final String B2B_MACAGENTREPORT_FROM_CREATED_TO_ACCEPTED = "B2B_MACAGENTREPORT_FROM_CREATED_TO_ACCEPTED";
    //private static final String B2B_MACAGENTREPORT_FROM_CREATED_TO_IN_AGENT = "B2B_MACAGENTREPORT_FROM_CREATED_TO_IN_AGENT";
    //private static final String B2B_MACAGENTREPORT_FROM_CREATED_TO_IN_SK = "B2B_MACAGENTREPORT_FROM_CREATED_TO_IN_SK";
    //private static final String B2B_MACAGENTREPORT_FROM_INQUEUE_TO_CREATED = "B2B_MACAGENTREPORT_FROM_INQUEUE_TO_CREATED";
    //</editor-fold>
    // Состояния договора:
    //     * Подписан
    private static final String B2B_CONTRACT_SG = "B2B_CONTRACT_SG";
    //     * Выгружен успешно
    private static final String B2B_CONTRACT_UPLOADED_SUCCESFULLY = "B2B_CONTRACT_UPLOADED_SUCCESFULLY";

    // Состояния договоров, которые могут быть включены в отчет агента
    private static final String B2B_INCLUDED_CONTRACT_STATE_SYSNAMES = "'" + B2B_CONTRACT_SG + "', '" + B2B_CONTRACT_UPLOADED_SUCCESFULLY + "'";
    // Имя ключа параметра для передачи строки с системными именами состояний договоров (которые могут быть включены в отчет агента) в запросы
    private static final String B2B_INCLUDED_CONTRACT_STATE_KEY_NAME = "CONTRACTSTATESYSNAMELIST";

    // максимальное количество строк, сохраняемых в одном запросе
    private static final int MAX_RECORDS_FOR_MASS_CREATE_SINGLE_QUERY = 1000;
    //private static final int MAX_RECORDS_FOR_MASS_CREATE_SINGLE_QUERY = 10; //!только для отладки!

    private static final String MEMBER_TYPE_SYSNAME_MANAGER = "Manager";
    private static final String MEMBER_TYPE_SYSNAME_CURATOR = "Curator";

    private static final String FLAG_SYSNAME_EDIT = "edit"; // Редактирование (изменение основных полей)
    private static final String FLAG_SYSNAME_HISTORY = "history"; // История (согласований)
    private static final String FLAG_SYSNAME_NOTE = "note"; // Примечание (к действию)

    // системное имя действия (должно быть уникальным, используется для идентификации действия)
    private static final String ACTION_SYSNAME_GENERATE = "Generate";
    private static final String ACTION_SYSNAME_TO_SK = "ToSK";
    private static final String ACTION_SYSNAME_TO_AGENT = "ToAgent";
    private static final String ACTION_SYSNAME_ACCEPT = "ToAccepted";
    private static final String ACTION_SYSNAME_EXPORT = "Export";

    // префикс имени метода для случаев, когда предполагается вызов методов действий с именем вида ACTION_METHOD_NAME_PREFIX + <системное имя действия>
    private static final String ACTION_METHOD_NAME_PREFIX = "dsB2BPrimaryActivityContractAgentReportDoAction";

    // таблица имен действий и вызываемых методов
    // 0 - системное имя действия (должно быть уникальным, используется для идентификации действия)
    // 1 - имя данного действия (русское наименование действия, для отображения в интерфейсе на кнопках и т.д.)
    // 2 - имя вызываемого для данного метода метода (или null, если предполагается вызов метода с именем вида ACTION_METHOD_NAME_PREFIX + <системное имя действия>)
    private static final String[][] ACTIONS_NAMES_AND_METHODS = {
        {ACTION_SYSNAME_GENERATE, "Сформировать", null},
        {ACTION_SYSNAME_TO_SK, "Передать на согласование", null},
        {ACTION_SYSNAME_TO_AGENT, "Передать на согласование", null},
        {ACTION_SYSNAME_ACCEPT, "Акцептовать", null},
        {ACTION_SYSNAME_EXPORT, "Выгрузить файл", null}
    };

    private static final int ACTIONS_TABLE_INDEX_MEMBER_TYPE = 0;
    private static final int ACTIONS_TABLE_INDEX_REPORT_STATE = 1;
    private static final int ACTIONS_TABLE_INDEX_ACTIONS = 2;
    private static final int ACTIONS_TABLE_INDEX_FLAGS = 3;

    //<editor-fold defaultstate="collapsed" desc="основная таблица действий (она же по ФТ - "Описание процесса отчета агента")">
    // основная таблица действий (она же по ФТ - "Описание процесса отчета агента")
    // 0 - системное имя участника договора по основной деятельности
    // 1 - системное состояния отчета агента
    // 2 - перечень доступных действий
    // 3 - перечень доступности общего функционала
    private static final String[][] ACTIONS_TABLE = {
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_MANAGER, B2B_MACAGENTREPORT_NEW,
            // доступные основные действия (в одну строку с разделителем - "|")
            ACTION_SYSNAME_GENERATE,
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            FLAG_SYSNAME_EDIT + "|" + FLAG_SYSNAME_NOTE
        },
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_CURATOR, B2B_MACAGENTREPORT_NEW,
            // доступные основные действия (в одну строку с разделителем - "|")
            ACTION_SYSNAME_GENERATE,
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            FLAG_SYSNAME_EDIT + "|" + FLAG_SYSNAME_NOTE
        },
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_MANAGER, B2B_MACAGENTREPORT_INQUEUE,
            // доступные основные действия (в одну строку с разделителем - "|")
            "",
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            ""
        },
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_CURATOR, B2B_MACAGENTREPORT_INQUEUE,
            // доступные основные действия (в одну строку с разделителем - "|")
            "",
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            ""
        },
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_MANAGER, B2B_MACAGENTREPORT_CREATED,
            // доступные основные действия (в одну строку с разделителем - "|")
            ACTION_SYSNAME_TO_SK + "|" + ACTION_SYSNAME_EXPORT,
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            FLAG_SYSNAME_HISTORY + "|" + FLAG_SYSNAME_NOTE
        },
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_CURATOR, B2B_MACAGENTREPORT_CREATED,
            // доступные основные действия (в одну строку с разделителем - "|")
            ACTION_SYSNAME_TO_AGENT + "|" + ACTION_SYSNAME_ACCEPT + "|" + ACTION_SYSNAME_EXPORT,
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            FLAG_SYSNAME_HISTORY + "|" + FLAG_SYSNAME_NOTE
        },
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_MANAGER, B2B_MACAGENTREPORT_IN_SK,
            // доступные основные действия (в одну строку с разделителем - "|")
            ACTION_SYSNAME_EXPORT,
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            FLAG_SYSNAME_HISTORY
        },
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_CURATOR, B2B_MACAGENTREPORT_IN_SK,
            // доступные основные действия (в одну строку с разделителем - "|")
            ACTION_SYSNAME_TO_AGENT + "|" + ACTION_SYSNAME_ACCEPT + "|" + ACTION_SYSNAME_EXPORT,
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            FLAG_SYSNAME_HISTORY + "|" + FLAG_SYSNAME_NOTE
        },
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_MANAGER, B2B_MACAGENTREPORT_IN_AGENT,
            // доступные основные действия (в одну строку с разделителем - "|")
            ACTION_SYSNAME_TO_SK + "|" + ACTION_SYSNAME_EXPORT,
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            FLAG_SYSNAME_HISTORY + "|" + FLAG_SYSNAME_NOTE
        },
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_CURATOR, B2B_MACAGENTREPORT_IN_AGENT,
            // доступные основные действия (в одну строку с разделителем - "|")
            ACTION_SYSNAME_EXPORT,
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            FLAG_SYSNAME_HISTORY
        },
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_MANAGER, B2B_MACAGENTREPORT_ACCEPTED,
            // доступные основные действия (в одну строку с разделителем - "|")
            ACTION_SYSNAME_EXPORT,
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            FLAG_SYSNAME_HISTORY
        },
        {
            // Системное имя участника, системное имя состояния отчета
            MEMBER_TYPE_SYSNAME_CURATOR, B2B_MACAGENTREPORT_ACCEPTED,
            // доступные основные действия (в одну строку с разделителем - "|")
            ACTION_SYSNAME_EXPORT,
            // флаги доступности общего функционала (в одну строку с разделителем - "|")
            FLAG_SYSNAME_HISTORY
        }
    };
    //</editor-fold>

    private static Map<String, String> actionMethods;

    private static Map<String, Map<String, Map<String, String>>> actionsNamesByUserAndState;

    private static Map<String, Map<String, Map<String, Boolean>>> actionsFlagsByUserAndState;

    public B2BPrimaryActivityContractAgentReportCustomFacade() {
        super();
        init();
    }

    private void init() {
        // обработчик дат
        datesParser = new DatesParser();
        // обработчик дат - протоколирование операций с датами
        datesParser.setVerboseLogging(IS_VERBOSE_LOGGING);
        //
        initActions();
    }

    private void initActions() {

        Map<String, String> actionNames = new HashMap<String, String>();
        actionMethods = new HashMap<String, String>();
        for (String[] actionNameAndMethod : ACTIONS_NAMES_AND_METHODS) {
            String actionSysName = actionNameAndMethod[0];
            String actionName = actionNameAndMethod[1];
            String actionMethod = actionNameAndMethod[2];
            if (actionMethod == null) {
                actionMethod = ACTION_METHOD_NAME_PREFIX + actionSysName;
            }
            actionNames.put(actionSysName, actionName);
            actionMethods.put(actionSysName, actionMethod);
        }
        logger.debug("actionNames: " + actionNames);
        logger.debug("actionMethods: " + actionMethods);

        actionsFlagsByUserAndState = new HashMap<String, Map<String, Map<String, Boolean>>>();

        for (String[] actionRow : ACTIONS_TABLE) {

            String memberSysName = actionRow[ACTIONS_TABLE_INDEX_MEMBER_TYPE];
            Map<String, Map<String, Boolean>> statesMap = actionsFlagsByUserAndState.get(memberSysName);
            if (statesMap == null) {
                statesMap = new HashMap<String, Map<String, Boolean>>();
                actionsFlagsByUserAndState.put(memberSysName, statesMap);
            }

            String reportStateSysName = actionRow[ACTIONS_TABLE_INDEX_REPORT_STATE];
            Map<String, Boolean> actionsMap = statesMap.get(reportStateSysName);
            if (actionsMap == null) {
                actionsMap = new HashMap<String, Boolean>();
                statesMap.put(reportStateSysName, actionsMap);
            }

            String[] flags = actionRow[ACTIONS_TABLE_INDEX_FLAGS].split("\\|");
            for (String flagName : flags) {
                if ((flagName != null) && (!flagName.isEmpty())) {
                    actionsMap.put(flagName, true);
                }
            }

        }

        logger.debug(actionsFlagsByUserAndState);

        actionsNamesByUserAndState = new HashMap<String, Map<String, Map<String, String>>>();

        for (String[] actionRow : ACTIONS_TABLE) {

            String memberSysName = actionRow[ACTIONS_TABLE_INDEX_MEMBER_TYPE];
            Map<String, Map<String, String>> statesMap = actionsNamesByUserAndState.get(memberSysName);
            if (statesMap == null) {
                statesMap = new HashMap<String, Map<String, String>>();
                actionsNamesByUserAndState.put(memberSysName, statesMap);
            }

            String reportStateSysName = actionRow[ACTIONS_TABLE_INDEX_REPORT_STATE];
            Map<String, String> actionsMap = statesMap.get(reportStateSysName);
            if (actionsMap == null) {
                actionsMap = new HashMap<String, String>();
                statesMap.put(reportStateSysName, actionsMap);
            }

            String[] actions = actionRow[ACTIONS_TABLE_INDEX_ACTIONS].split("\\|");
            for (String actionSysName : actions) {
                if ((actionSysName != null) && (!actionSysName.isEmpty())) {
                    String actionName = actionNames.get(actionSysName);
                    if ((actionName != null) && (!actionName.isEmpty())) {
                        actionsMap.put(actionSysName, actionName);
                    } else {
                        logger.error(String.format(
                                "For action with system name '%s' used for member with system name '%s' on reports with state '%s' can not be found a button title - action initialization will be skipped!",
                                actionSysName, memberSysName, reportStateSysName
                        ));
                    }
                }
            }

        }

        logger.debug(actionsNamesByUserAndState);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportBrowseListByParamEx(Map<String, Object> params) throws Exception {

        logger.debug("Start agents reports browse by parameters...");

        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.putAll(params);

        Map<String, Object> result = this.selectQuery("dsB2BPrimaryActivityContractAgentReportBrowseListByParamCustomWhereEx", queryParams);

        logger.debug("Agents reports browse by parameters finish.");

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportBrowseListByParamCustomWhereEx(Map<String, Object> params) throws Exception {
        logger.debug("Start agents reports  browse...");

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(params, Double.class);

        String idFieldName = AGENT_REPORT_PK_FIELD_NAME;
        String customWhereQueryName = "dsB2BPrimaryActivityContractAgentReportBrowseListByParamCustomWhereEx";
        params.put(CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES_KEY_NAME, PAC_AGENT_REPORT_CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES);
        params.put(CUSTOM_WHERE_SUPPORTED_FIELDS_KEY_NAME, PAC_AGENT_REPORT_CUSTOM_WHERE_SUPPORTED_FIELDS);
        Map<String, Object> result = doCustomWhereQuery(customWhereQueryName, idFieldName, params);

        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(result, String.class);

        Long agentReportID = getLongParam(params, idFieldName);
        if (agentReportID != null) {
            List<Map<String, Object>> resultList = WsUtils.getListFromResultMap(result);
            if (resultList.size() == 1) {
                // единственный загруженный отчета агента
                Map<String, Object> resultItem = resultList.get(0);
                // логин и пароль для вызова методов веб-сервисов
                String login = params.get(WsConstants.LOGIN).toString();
                String password = params.get(WsConstants.PASSWORD).toString();
                // дополнение отчета агента историей состояний
                updateAgentReportDataWithStateHistory(resultItem, agentReportID, login, password);
            }
        }

        logger.debug("Agents reports browse finish.");

        //initActions(); //!только для отладки!
        return result;
    }

    private void updateAgentReportDataWithStateHistory(Map<String, Object> resultItem, Long agentReportID, String login, String password) throws Exception {
        List<Map<String, Object>> stateHistoryList = getAgentReportStateHistory(agentReportID, login, password);
        resultItem.put("STATEHISTORY", stateHistoryList);
    }

    private void updateAgentReportDataWithStateHistory(Map<String, Object> resultItem, String login, String password) throws Exception {
        Long agentReportID = getLongParam(resultItem, AGENT_REPORT_PK_FIELD_NAME);
        updateAgentReportDataWithStateHistory(resultItem, agentReportID, login, password);
    }

    private List<Map<String, Object>> getAgentReportStateHistory(Long agentReportID, String login, String password) throws Exception {
        logger.debug("Browsing state history for agent report...");
        List<Map<String, Object>> stateHistoryList;
        if (agentReportID != null) {
            Map<String, Object> historyParams = new HashMap<String, Object>();
            historyParams.put("OBJID", agentReportID);
            historyParams.put("TYPESYSNAME", "B2B_MACAGENTREPORT");
            historyParams.put("INCLUDECURRENTSTATE", true); // достаточно любой не null величины для срабатывания флага в cayenne-запросе
            //Map<String, Object> stateHistory = this.callService(THIS_SERVICE_NAME, "dsB2BStateHistoryBrowseListByParamCustomWhereEx", historyParams, IS_VERBOSE_CALLS_LOGGING, login, password);
            //Map<String, Object> stateHistory = this.callService(THIS_SERVICE_NAME, "dsB2BStateHistoryBrowseListByParamEx", historyParams, IS_VERBOSE_CALLS_LOGGING, login, password);
            //List<Map<String, Object>> stateHistoryList = WsUtils.getListFromResultMap(stateHistory);
            stateHistoryList = this.callServiceAndGetListFromResultMap(THIS_SERVICE_NAME, "dsB2BStateHistoryBrowseListByParamEx", historyParams, IS_VERBOSE_CALLS_LOGGING, login, password);
        } else {
            logger.error("No agent report id found in parameters - empty state history will be returned!");
            stateHistoryList = new ArrayList<Map<String, Object>>();
        }
        logger.debug("Browsing state history for agent report finished with result: " + stateHistoryList);
        return stateHistoryList;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMainActivityContractAgentReportSave(Map<String, Object> params) throws Exception {
        logger.debug("Saving primary activity contract agent report...");

        // логин и пароль для вызова методов веб-сервисов
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // идентификатор отчета агента
        Long agentReportID = getLongParamLogged(params, AGENT_REPORT_PK_FIELD_NAME);

        Map<String, Object> reportParams = new HashMap<String, Object>();
        String methodName;
        if (agentReportID != null) {
            // если передан идентификатор - требуется выполнить обновление существующего отчета агента
            logger.debug(String.format("Primary activity contract agent report id (%s = %d) found in parameters - existing primary activity contract agent report will be updated.", AGENT_REPORT_PK_FIELD_NAME, agentReportID));
            methodName = "dsB2BMainActivityContractAgentReportUpdate";
            // обновляемые параметры существующего отчета агента
            reportParams.putAll(params);
            reportParams.remove(WsConstants.LOGIN);
            reportParams.remove(WsConstants.PASSWORD);
        } else {
            // если не передан идентификатор - требуется выполнить создание заготовки нового отчета агента
            logger.debug(String.format("No primary activity contract agent report id (%s) found in parameters - new primary activity contract agent report will be created.", AGENT_REPORT_PK_FIELD_NAME));
            methodName = "dsB2BMainActivityContractAgentReportCreate";
            // дополнительные параметры при создании нового отчета агента
            copyEntriesByKeys(reportParams, params, "AGENTID", "MAINACTCONTRID", "PRODVERID", "PRODID", "ENDDATE");
            reportParams.put("AUTONUMBERSYSNAME", "B2BMACAgentReportAutoNum");
        }

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(reportParams, Double.class);

        reportParams.put(RETURN_AS_HASH_MAP, true);
        logger.debug(String.format("Method '%s' will be used to save primary activity contract agent report...", methodName));
        Map<String, Object> saveResult = this.callService(B2BPOSWS_SERVICE_NAME, methodName, reportParams, IS_VERBOSE_CALLS_LOGGING, login, password);
        agentReportID = getLongParamLogged(saveResult, AGENT_REPORT_PK_FIELD_NAME);
        Map<String, Object> result;
        if (agentReportID == null) {
            String errorText = "Error occured during primary activity contract agent report saving! Details: " + saveResult;
            logger.error(errorText);
            result = new HashMap<String, Object>();
            result.put("Error", errorText);
        } else {
            logger.debug("Saved primary activity contract agent report with id (MACAGENTREPORTID) = " + agentReportID);
            Map<String, Object> loadParams = new HashMap<String, Object>();
            loadParams.put(AGENT_REPORT_PK_FIELD_NAME, agentReportID);
            loadParams.put(RETURN_AS_HASH_MAP, true);
            logger.debug("Loading updated primary activity contract agent report data...");
            Map<String, Object> loadedReport = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPrimaryActivityContractAgentReportBrowseListByParamCustomWhereEx", loadParams, IS_VERBOSE_CALLS_LOGGING, login, password);
            result = loadedReport;
        }

        // здесь не требуется, преобразование дат в выходных параметрах уже выполнено в dsB2BPrimaryActivityContractAgentReportBrowseListByParamCustomWhereEx
        //datesParser.parseDates(loadedReport, String.class);
        logger.debug("Saving primary activity contract finished.");

        return result;

    }

    // сохранение списка идентификаторов договоров в таблицу содержимого отчета агента
    private List<Map<String, Object>> massCreateAgentReportContent(List<Map<String, Object>> idList, Long agentReportID, String login, String password) throws Exception {

        // генерация идентификаторов записей для массового создания
        ExternalService externalService = this.getExternalService();
        for (Map<String, Object> idRecord : idList) {
            Long generatedContendID = getLongParam(externalService.getNewId("B2B_MACAGENTREPORTCNT"));
            idRecord.put(AGENT_REPORT_CONTENT_PK_FIELD_NAME, generatedContendID);
            logger.debug(String.format("%s (generated) = %d", AGENT_REPORT_CONTENT_PK_FIELD_NAME, generatedContendID));
            getLongParamLogged(idRecord, "CONTRID"); // для протокола
        }

        // формирование параметров для сохранения списка идентификаторов договоров в таблицу отчета агента
        Map<String, Object> massCreateParams = new HashMap<String, Object>();
        massCreateParams.put("rows", idList);
        massCreateParams.put("totalCount", idList.size());
        massCreateParams.put(AGENT_REPORT_PK_FIELD_NAME, agentReportID);
        massCreateParams.put("ISREMOVED", 0L);
        massCreateParams.put(RETURN_AS_HASH_MAP, true);

        // выполнение запроса        
        Map<String, Object> massCreateCallResult = this.callService(THIS_SERVICE_NAME, "dsB2BMainActivityContractAgentReportContentMassCreate", massCreateParams, login, password);
        List<Map<String, Object>> massCreateResult = null;
        if (massCreateCallResult != null) {
            massCreateResult = (List<Map<String, Object>>) massCreateCallResult.get("rows");
        }
        if (massCreateResult == null) {
            massCreateResult = new ArrayList<Map<String, Object>>();
        }
        logger.debug("massCreateResult = " + massCreateResult);

        return massCreateResult;
    }

    private Map<String, Object> agentReportMakeTrans(Map<String, Object> agentReport, String toStateSysName, String login, String password) throws Exception {
        String idFieldName = AGENT_REPORT_PK_FIELD_NAME;
        String methodNamePrefix = "dsB2BMainActivityContractAgentReport";
        String typeSysName = "B2B_MACAGENTREPORT";
        String toStateNote = getStringParamLogged(agentReport, STATE_NOTE_KEY_NAME);
        return recordMakeTrans(agentReport, toStateSysName, toStateNote, idFieldName, methodNamePrefix, typeSysName, login, password);
    }

    @WsMethod(requiredParams = {"AGENTID", "MAINACTCONTRID", PARTNERS_DEPARTMENT_CODE_LIKE_KEY_NAME, B2B_INCLUDED_CONTRACT_STATE_KEY_NAME})
    public Map<String, Object> dsB2BMainActivityContractAgentReportIncludedContractsBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BMainActivityContractAgentReportIncludedContractsBrowseListByParamEx", params);
        return result;
    }

    @WsMethod()
    public Map<String, Object> dsB2BMainActContrBrowseByContr(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BMainActContrBrowseByContr", params);
        return result;
    }

    @WsMethod(requiredParams = {AGENT_REPORT_PK_FIELD_NAME, "AGENTID", "MAINACTCONTRID", "ENDDATE"})
    public Map<String, Object> dsB2BMainActivityContractAgentReportsProcessSingleRecord(Map<String, Object> agentReport) throws Exception {

        Map<String, Object> result;
        try {
            result = doAgentReportsProcessSingleRecord(agentReport);
        } catch (Exception ex) {
            logger.error(String.format(
                    "During processing agent's report with %s = %d exception was thrown: %s",
                    AGENT_REPORT_PK_FIELD_NAME, getLongParam(agentReport, AGENT_REPORT_PK_FIELD_NAME), ex.getMessage()
            ), ex);
            throw ex; // для отката транзакции по всем выполненным в ходе вызова dsB2BMainActivityContractAgentReportsProcessSingleRecord операциям с БД
        }

        return result;
    }

    private Map<String, Object> doAgentReportsProcessSingleRecord(Map<String, Object> params) throws Exception {
        // обработка конкретной записи
        logger.debug("Agent's report single record processing...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        // идентификатор обрабатываемой записи
        Long agentReportID = getLongParam(params, AGENT_REPORT_PK_FIELD_NAME);
        logger.debug(String.format("Start processing agent's report record with id (%s) = %d...", AGENT_REPORT_PK_FIELD_NAME, agentReportID));
        // идентификатор агента
        Long agentID = getLongParam(params, "AGENTID");
        logger.debug(String.format("This report's agent's id (AGENTID) = %d.", agentID));
        // идентификатор договора по основной деятельности
        Long mainActivityContractID = getLongParam(params, "MAINACTCONTRID");
        logger.debug(String.format("This report's main activity contract id (MAINACTCONTRID) = %d.", mainActivityContractID));
        // идентификатор версии продукта (для договора по основной деятельности)
        Long productVersionID = getLongParam(params, "PRODVERID");
        logger.debug(String.format("This report's main activity contract product version id (PRODVERID) = %d.", productVersionID));

        // выполнение запроса (с параметрами из обрабатываемой записи) для получения списка идентификаторов договоров
        Map<String, Object> browseParams = new HashMap<String, Object>();
        browseParams.put("AGENTID", agentID); // DEPARTMENTID организации-агента
        browseParams.put("MAINACTCONTRID", mainActivityContractID);
        browseParams.put("PRODVERID", productVersionID);
        browseParams.put("ENDDATE", params.get("ENDDATE"));
        // системные имена передаются в запрос в виде параметров - чтобы не указывать их в самих SQL-запросах в явном виде
        browseParams.put(PARTNERS_DEPARTMENT_CODE_LIKE_KEY_NAME, PARTNERS_DEPARTMENT_CODE_LIKE);
        browseParams.put(B2B_INCLUDED_CONTRACT_STATE_KEY_NAME, B2B_INCLUDED_CONTRACT_STATE_SYSNAMES);
        logger.debug("Included contracts browse parameters = " + browseParams);
        Map<String, Object> browseResult = this.callService(THIS_SERVICE_NAME, "dsB2BMainActivityContractAgentReportIncludedContractsBrowseListByParamEx", browseParams, login, password);
        Long idCount = null;
        if (browseResult != null) {
            idCount = getLongParamLogged(browseResult, TOTALCOUNT);
        }
        logger.debug("Contracts ids count returned by browse (idCount) = " + idCount);
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        if ((idCount != null) && (idCount > 0)) {
            // определение списка идентификаторов договоров, если запрос вернул его
            List<Map<String, Object>> idList = (ArrayList<Map<String, Object>>) WsUtils.getListFromResultMap(browseResult);
            logger.debug("Contracts ids list returned by browse (idList) = " + idList);

            // максимальное количество строк, сохраняемых в одном запросе
            logger.debug("Max records count for one insert query (MAX_RECORDS_FOR_MASS_CREATE_SINGLE_QUERY) = " + MAX_RECORDS_FOR_MASS_CREATE_SINGLE_QUERY);

            // определение количества фрагментов для сохранения в отдельных запросах
            int subListCount = (idCount.intValue() / MAX_RECORDS_FOR_MASS_CREATE_SINGLE_QUERY) + 1;
            logger.debug(String.format("Contracts ids list will be saved by %d querys (subListCount)", subListCount));

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
                List<Map<String, Object>> subResultList = massCreateAgentReportContent(idSubList, agentReportID, login, password);
                // дополнение списка результата результатом текущего запроса
                resultList.addAll(subResultList);

            }

        } else {
            logger.debug("No contracts ids to save - saving skipped.");
        }

        // дополнение списка результата списком сохраненных записей содержимого отчета агента
        result.put("AGENTREPORTCONTENTLIST", resultList);

        // перевод статуса в 'Создан'        
        Map<String, Object> transResult = agentReportMakeTrans(params, B2B_MACAGENTREPORT_CREATED, login, password);
        result.putAll(transResult);

        logger.debug("Agent's report single record processing finished.");

        return result;
    }

    private Map<String, Object> doAgentReportsProcess(Map<String, Object> params) throws Exception {
        logger.debug("");
        logger.debug("Start agent's reports processing...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> agentReportsParams = new HashMap<String, Object>();
        agentReportsParams.putAll(params);
        agentReportsParams.put("STATESYSNAME", B2B_MACAGENTREPORT_INQUEUE);

        List<Map<String, Object>> agentReportsList;
        try {
            agentReportsList = WsUtils.getListFromResultMap(this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BMainActivityContractAgentReportBrowseListByParam", agentReportsParams, login, password));
        } catch (Exception ex) {
            agentReportsList = new ArrayList<Map<String, Object>>();
            result.put("ErrorEx", ex.getMessage());
            logger.error(String.format("During browsing agent's reports for processing by parameters %s exception was thrown: %s. ", agentReportsParams.toString(), ex.getMessage()), ex);
        }

        int agentReportsTotalCount = agentReportsList.size();
        if (agentReportsTotalCount <= 0) {
            logger.debug("No agent's reports found for processing.");
        } else {
            logger.debug(String.format("Found %d agent's reports for processing.", agentReportsTotalCount));
            int agentReportsCount = 0;

            for (Map<String, Object> agentReport : agentReportsList) {

                agentReportsCount++;
                logger.debug(String.format("Start processing agent's report %d (from total of %d reports)...", agentReportsCount, agentReportsTotalCount));

                Map<String, Object> processParams = new HashMap<String, Object>();
                processParams.putAll(agentReport);
                processParams.put(RETURN_AS_HASH_MAP, true);
                try {
                    Map<String, Object> processResult = this.callExternalService(B2BPOSWS_SERVICE_NAME, "dsB2BMainActivityContractAgentReportsProcessSingleRecord", processParams, login, password);
                    agentReport.putAll(processResult);
                } catch (Exception ex) {
                    agentReport.put("ErrorEx", ex.getMessage());
                    logger.error(String.format(
                            "During calling processing agent's report with %s = %d exception was thrown: %s",
                            AGENT_REPORT_PK_FIELD_NAME, getLongParam(agentReport, AGENT_REPORT_PK_FIELD_NAME), ex.getMessage()
                    ), ex);
                }
                logger.debug(String.format("Processing agent's report %d (from total of %d reports) finished.", agentReportsCount, agentReportsTotalCount));

            }
        }

        result.put("EXPORTDATALIST", agentReportsList);
        logger.debug("Agent's report processing finished.");
        logger.debug("");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMainActivityContractAgentReportsProcess(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        if (agentReportsProcessingThreadCount == 0) {
            logger.debug("No doAgentReportsProcess running threads found - starting new...");
            agentReportsProcessingThreadCount = 1;
            try {
                result = doAgentReportsProcess(params);
            } finally {
                agentReportsProcessingThreadCount = 0;
                logger.debug("Thread doAgentReportsProcess finished.");
            }
        } else {
            logger.debug("Thread doAgentReportsProcess already running, no new threads started.");
        }
        return result;

    }

    // запрос состояния конкретного отчета агента и данных об участнике связанного с отчетом договора по основной деятельности (по пользователю)
    @WsMethod(requiredParams = {AGENT_REPORT_PK_FIELD_NAME, "USERID"})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportMemberBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BPrimaryActivityContractAgentReportMemberBrowseListByParamEx", params);
        return result;
    }

    // запрос данных об участнике договора по основной деятельности (по пользователю)
    @WsMethod(requiredParams = {"MAINACTCONTRID", "USERID"})
    public Map<String, Object> dsB2BPrimaryActivityContractMemberBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BPrimaryActivityContractMemberBrowseListByParamEx", params);
        return result;
    }

    // запрос допустимых действий для предполагаемого отчета агента по текущему пользователю и известному агентскому договору
    @WsMethod(requiredParams = {"MAINACTCONTRID"})
    public Map<String, Object> dsB2BPrimaryActivityContractGetAvailableActions(Map<String, Object> params) throws Exception {
        logger.debug("Getting available actions for agent report by user and report state...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        //Map<String, Object> result = new HashMap<String, Object>();

        //Long agentReportID = getLongParamLogged(params, AGENT_REPORT_PK_FIELD_NAME);
        Long primaryActivityContractID = getLongParamLogged(params, "MAINACTCONTRID");
        //result.put(AGENT_REPORT_PK_FIELD_NAME, agentReportID);

        Long userAccountID = getLongParamLogged(params, SESSIONPARAM_USERACCOUNTID);

        List<Map<String, Object>> reportAndMemberList = getPrimaryActivityContractMemberList(primaryActivityContractID, userAccountID, login, password);
        String agentReportStateSysName = getStringParamLogged(params, "STATESYSNAME");
        if (agentReportStateSysName.isEmpty()) {
            agentReportStateSysName = B2B_MACAGENTREPORT_NEW;
            logger.debug("STATESYSNAME from parameters is null - '" + agentReportStateSysName + "' will be used.");
        }
        for (Map<String, Object> reportAndMember : reportAndMemberList) {
            reportAndMember.put("STATESYSNAME", agentReportStateSysName);
        }

        Map<String, Object> result = createActionsAndFlags(reportAndMemberList);
        result.put("MAINACTCONTRID", primaryActivityContractID);

        logger.debug("Getting available actions for agent report by user and report state finished with result: " + result);

        return result;
    }

    // запрос допустимых действий для конкретного отчета агента по текущему пользователю
    @WsMethod(requiredParams = {AGENT_REPORT_PK_FIELD_NAME})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportGetAvailableActions(Map<String, Object> params) throws Exception {

        logger.debug("Getting available actions for agent report by user and report state...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        //Map<String, Object> result = new HashMap<String, Object>();

        Long agentReportID = getLongParamLogged(params, AGENT_REPORT_PK_FIELD_NAME);
        Long userAccountID = getLongParamLogged(params, SESSIONPARAM_USERACCOUNTID);

        List<Map<String, Object>> reportAndMemberList = getReportAndMemberList(agentReportID, userAccountID, login, password);

        Map<String, Object> result = createActionsAndFlags(reportAndMemberList);
        result.put(AGENT_REPORT_PK_FIELD_NAME, agentReportID);

        logger.debug("Getting available actions for agent report by user and report state finished with result: " + result);

        return result;

    }

    private Map<String, Object> createActionsAndFlags(List<Map<String, Object>> reportAndMemberList) {

        Map<String, Object> result = new HashMap<String, Object>();

        String memberTypeSysName = null;
        String agentReportStateSysName = null;
        // доступные действия - список из мап (в мапах ключ - наименование свойства действия, значение - свойство действия)
        ArrayList<Map<String, Object>> resultActionList = new ArrayList<Map<String, Object>>();
        // флаги - мапа (ключ - имя флага, значение - состояние флага)
        Map<String, Boolean> resultFlagMap = new HashMap<String, Boolean>();

        for (Map<String, Object> reportAndMember : reportAndMemberList) {
            memberTypeSysName = getStringParamLogged(reportAndMember, "MACMEMBERTYPESYSNAME");
            agentReportStateSysName = getStringParamLogged(reportAndMember, "STATESYSNAME");

            // доступные действия - формирование
            Map<String, Map<String, String>> actionsByStateMap = actionsNamesByUserAndState.get(memberTypeSysName);
            if (actionsByStateMap != null) {
                Map<String, String> actionMap = actionsByStateMap.get(agentReportStateSysName);
                if (actionMap != null) {
                    for (Map.Entry<String, String> action : actionMap.entrySet()) {
                        String actionSysName = action.getKey();
                        String actionName = action.getValue();
                        Map<String, Object> actionInfo = new HashMap<String, Object>();
                        actionInfo.put("SYSNAME", actionSysName);
                        actionInfo.put("NAME", actionName);
                        //resultActionList.add(0, actionInfo);
                        resultActionList.add(actionInfo);
                    }
                }
            }

            // флаги - формирование            
            Map<String, Map<String, Boolean>> flagsByStateMap = actionsFlagsByUserAndState.get(memberTypeSysName);
            if (actionsByStateMap != null) {
                Map<String, Boolean> flagMap = flagsByStateMap.get(agentReportStateSysName);
                if (flagMap != null) {
                    for (Map.Entry<String, Boolean> action : flagMap.entrySet()) {
                        String flagSysName = action.getKey();
                        Boolean flagValue = action.getValue();
                        resultFlagMap.put(flagSysName, flagValue);
                    }
                }
            }

        }

        if (reportAndMemberList.size() == 1) {
            // если пользователь входит как участник в договора по основной деятельности единожды - дополнительно возвращаются тип участника и состояние отчета
            result.put("MACMEMBERTYPESYSNAME", memberTypeSysName);
            result.put("STATESYSNAME", agentReportStateSysName);
        }

        // формирование результата
        result.put("ACTIONLIST", resultActionList); // доступные действия
        result.put("FLAGMAP", resultFlagMap); // флаги

        return result;

    }

    @WsMethod(requiredParams = {"ACTIONSYSNAME"})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportDoAction(Map<String, Object> params) throws Exception {

        logger.debug("Trying to perform action for agent report...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String resultErrorText = "";
        Map<String, Object> result = new HashMap<String, Object>();

        Long userAccountID = getLongParamLogged(params, SESSIONPARAM_USERACCOUNTID);
        Long agentReportID = getLongParamLogged(params, AGENT_REPORT_PK_FIELD_NAME);

        List<Map<String, Object>> reportAndMemberList = new ArrayList<Map<String, Object>>();
        if (agentReportID != null) {
            reportAndMemberList = getReportAndMemberList(agentReportID, userAccountID, login, password);
        } else {
            Long primaryActivityContractID = getLongParamLogged(params, "MAINACTCONTRID");
            if (primaryActivityContractID != null) {
                reportAndMemberList = getPrimaryActivityContractMemberList(primaryActivityContractID, userAccountID, login, password);
                String agentReportStateSysName = getStringParamLogged(params, "STATESYSNAME");
                if (agentReportStateSysName.isEmpty()) {
                    agentReportStateSysName = B2B_MACAGENTREPORT_NEW;
                }
                for (Map<String, Object> reportAndMember : reportAndMemberList) {
                    reportAndMember.put("STATESYSNAME", agentReportStateSysName);
                }
            } else {
                logger.error(String.format("Required parameters (%s or MAINACTCONTRID) for performing action not found.)", AGENT_REPORT_PK_FIELD_NAME));
                resultErrorText = "Передано недостаточно параметров!";
            }
        }

        if (resultErrorText.isEmpty()) {

            String actionSysName = getStringParamLogged(params, "ACTIONSYSNAME");
            String actionMethodName = "";
            for (Map<String, Object> reportAndMember : reportAndMemberList) {
                String memberTypeSysName = getStringParamLogged(reportAndMember, "MACMEMBERTYPESYSNAME");
                String agentReportStateSysName = getStringParamLogged(reportAndMember, "STATESYSNAME");
                Map<String, Map<String, String>> actionsNamesByState = actionsNamesByUserAndState.get(memberTypeSysName);
                if (actionsNamesByState != null) {
                    Map<String, String> actionsNames = actionsNamesByState.get(agentReportStateSysName);
                    if (actionsNames != null) {
                        if (actionsNames.containsKey(actionSysName)) {
                            actionMethodName = actionMethods.get(actionSysName);
                            logger.debug(String.format("Requested action found for current user member type '%s' on current agent report with state '%s'.", memberTypeSysName, agentReportStateSysName));
                            break;
                        }
                    }
                }
            }

            if (!actionMethodName.isEmpty()) {

                //reportParams.put(RETURN_AS_HASH_MAP, true);
                logger.debug(String.format("To perform action with system name '%s' will be called method '%s'...", actionSysName, actionMethodName));
                Map<String, Object> actionParams = new HashMap<String, Object>();
                actionParams.putAll(params);
                actionParams.remove(WsConstants.LOGIN);
                actionParams.remove(WsConstants.PASSWORD);
                actionParams.remove("ACTIONSYSNAME");
                actionParams.put(RETURN_AS_HASH_MAP, true);

                Map<String, Object> actionResult = this.callService(THIS_SERVICE_NAME, actionMethodName, actionParams, IS_VERBOSE_CALLS_LOGGING, login, password);
                agentReportID = getLongParamLogged(actionResult, AGENT_REPORT_PK_FIELD_NAME);

                if (agentReportID == null) {
                    String errorText = "Error occured during performing call of action method! Details: " + actionResult;
                    logger.error(errorText);
                    result = new HashMap<String, Object>();
                    resultErrorText = getStringParamLogged(actionResult, "Error");
                    if (resultErrorText.isEmpty()) {
                        resultErrorText = "Неизвестная ошибка при выполнении метода, соответствующего выбранному действию над отчетом агента!";
                    }
                } else {
                    logger.debug("Action successfully performed on primary activity contract agent report with id (MACAGENTREPORTID) = " + agentReportID);
                    Map<String, Object> reloadParams = new HashMap<String, Object>();
                    reloadParams.put(AGENT_REPORT_PK_FIELD_NAME, agentReportID);
                    reloadParams.put(RETURN_AS_HASH_MAP, true);
                    logger.debug("Loading updated primary activity contract agent report data...");
                    Map<String, Object> reloadedReport = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPrimaryActivityContractAgentReportBrowseListByParamCustomWhereEx", reloadParams, IS_VERBOSE_CALLS_LOGGING, login, password);
                    result = reloadedReport;
                    result.put("LASTACTIONRESULT", actionResult);
                }

            } else {
                logger.debug("Requested action not found in supported for current user on current agent report.");
                resultErrorText = "У вас нет прав для выполнения этого действия над данным отчетом агента в его текущем состоянии!";
            }

        }

        if (!resultErrorText.isEmpty()) {
            // если в ходе выполнения было подготовлено описание ошибки, то необходимо сформировать соответствующий результат
            result = new HashMap<String, Object>();
            result.put("Error", resultErrorText);
        }

        return result;

    }

    private List<Map<String, Object>> getReportAndMemberList(Long agentReportID, Long userAccountID, String login, String password) throws Exception {

        logger.debug("Getting members info for agent report by user...");

        // формирование параметров
        Map<String, Object> reportAndMemberParams = new HashMap<String, Object>();
        reportAndMemberParams.put(AGENT_REPORT_PK_FIELD_NAME, agentReportID);
        reportAndMemberParams.put("USERID", userAccountID);

        // вызов метода по запросу списка
        Map<String, Object> reportAndMemberCallResult = this.callService(THIS_SERVICE_NAME, "dsB2BPrimaryActivityContractAgentReportMemberBrowseListByParamEx", reportAndMemberParams, IS_VERBOSE_CALLS_LOGGING, login, password);

        // анализ результата
        List<Map<String, Object>> reportAndMemberList;
        if (isCallResultOK(reportAndMemberCallResult)) {
            reportAndMemberList = WsUtils.getListFromResultMap(reportAndMemberCallResult);
            logger.debug(String.format("Found %d members info sets for this agent report by user.", reportAndMemberList.size()));
        } else {
            logger.error("Error while getting agent report state and user/member creds! Details: " + reportAndMemberCallResult);
            reportAndMemberList = new ArrayList<Map<String, Object>>();
        }

        logger.debug("Getting members info for agent report by user finished.");

        return reportAndMemberList;

    }

    private List<Map<String, Object>> getPrimaryActivityContractMemberList(Long primaryActivityContractID, Long userAccountID, String login, String password) throws Exception {

        logger.debug("Getting members info for primary activity contract by user...");

        // формирование параметров
        Map<String, Object> reportAndMemberParams = new HashMap<String, Object>();
        reportAndMemberParams.put("MAINACTCONTRID", primaryActivityContractID);
        reportAndMemberParams.put("USERID", userAccountID);

        // вызов метода по запросу списка
        Map<String, Object> reportAndMemberCallResult = this.callService(THIS_SERVICE_NAME, "dsB2BPrimaryActivityContractMemberBrowseListByParamEx", reportAndMemberParams, IS_VERBOSE_CALLS_LOGGING, login, password);

        // анализ результата
        List<Map<String, Object>> reportAndMemberList;
        if (isCallResultOK(reportAndMemberCallResult)) {
            reportAndMemberList = WsUtils.getListFromResultMap(reportAndMemberCallResult);
            logger.debug(String.format("Found %d members info sets for this primary activity contract by user.", reportAndMemberList.size()));
        } else {
            logger.error("Error while getting primary activity contract user/member creds! Details: " + reportAndMemberCallResult);
            reportAndMemberList = new ArrayList<Map<String, Object>>();
        }

        logger.debug("Getting members info for primary activity contract by user finished.");

        return reportAndMemberList;

    }

    private Map<String, Object> copyEntriesByKeys(Map<String, Object> targetMap, Map<String, Object> sourceMap, String... keyNames) {
        if ((targetMap != null) && (sourceMap != null)) {
            for (String keyName : keyNames) {
                targetMap.put(keyName, sourceMap.get(keyName));
            }
        }
        return targetMap;
    }

    private Map<String, Object> newMapFromExisting(Map<String, Object> sourceMap, String... keyNames) {
        Map<String, Object> newMap = new HashMap<String, Object>();
        copyEntriesByKeys(newMap, sourceMap, keyNames);
        return newMap;
    }

    @WsMethod(requiredParams = {"AGENTID", "MAINACTCONTRID", "PRODVERID", "STATESYSNAME"})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportDoActionGenerate(Map<String, Object> params) throws Exception {

        logger.debug(String.format("Performing agent report action '%s'...", ACTION_SYSNAME_GENERATE));

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> duplicatesParams = newMapFromExisting(params, "AGENTID", "MAINACTCONTRID", "PRODVERID");
        duplicatesParams.put("EXCLUDEDSTATESYSNAME", B2B_MACAGENTREPORT_ACCEPTED);
        duplicatesParams.put("EXCLUDED" + AGENT_REPORT_PK_FIELD_NAME, getLongParamLogged(params, AGENT_REPORT_PK_FIELD_NAME));

        List<Map<String, Object>> duplicatesList = this.callServiceAndGetListFromResultMap(THIS_SERVICE_NAME, "dsB2BPrimaryActivityContractAgentReportBrowseListByParamEx", duplicatesParams, IS_VERBOSE_CALLS_LOGGING, login, password);

        String resultErrorText = "";
        if ((duplicatesList != null) && (duplicatesList.size() > 0)) {

            // была вполнена дополнительная проверка и установлено, что существуют
            // "другие отчеты агента с теми же критериями (Агент + Агентский договор + Страховой продукт) в статусе отличном от «Акцептован»"
            logger.debug("Already exists identical (by agent, contract and product) non accepted agent report(s) with number(s): ");
            // формирование списка номеров существующих аналогичных отчетов для включения в сообщение
            StringBuilder duplicatesAgentReportsNumbersSB = new StringBuilder();
            for (Map<String, Object> duplicate : duplicatesList) {
                String duplicateAgentReportNumber = getStringParamLogged(duplicate, "REPORTNUMBER");
                duplicatesAgentReportsNumbersSB.append("№ ").append(duplicateAgentReportNumber).append(", ");
            }
            duplicatesAgentReportsNumbersSB.setLength(duplicatesAgentReportsNumbersSB.length() - 2);

            // первый из существующих аналогичных отчетов для полчения информации в сообщение
            // (агент, агентский договор и страховой продукт во всех найденых аналогичных отчетах как бы аналогичны)
            Map<String, Object> firstDuplicate = duplicatesList.get(0);
            // формирование сообщения вида "По агенту «XXXXXXXXX» договор № XXXXXXXXX страховой продукт «XXXXXXXXXX» не завершено согласование предыдущего отчет агента № XXXXXXXX."
            resultErrorText = String.format(
                    "По агенту «%s» договор № %s страховой продукт «%s» не завершено согласование %s %s.",
                    getStringParam(firstDuplicate, "AGENTNAME"),
                    getStringParam(firstDuplicate, "MAINACTCONTRNUMBER"),
                    getStringParam(firstDuplicate, "PRODNAME"),
                    duplicatesList.size() == 1 ? "предыдущего отчета агента" : "предыдущих отчетов агента",
                    duplicatesAgentReportsNumbersSB.toString()
            );
        }

        if (resultErrorText.isEmpty()) {

            logger.debug("Already exists identical (by agent, contract and product) non accepted agent reports not found.");
            Map<String, Object> createParams = newMapFromExisting(params, "AGENTID", "MAINACTCONTRID", "PRODVERID", "PRODID", "ENDDATE");
            createParams.put(RETURN_AS_HASH_MAP, true);

            // создание отчета в исходном статусе - 'Новый'
            Map<String, Object> createResult = this.callService(THIS_SERVICE_NAME, "dsB2BMainActivityContractAgentReportSave", createParams, IS_VERBOSE_CALLS_LOGGING, login, password);

            // перевод отчета в статус 'Поставлен в очередь'
            copyEntriesByKeys(createResult, params, "TOSTATENOTE"); // комментарий, введенный в интерфейсе, должен быть связан с конечным статусом - 'Поставлен в очередь'
            Map<String, Object> transResult = agentReportActionChangeState(createResult, B2B_MACAGENTREPORT_INQUEUE, login, password);

            if (isCallResultOK(transResult) && (transResult.get("STATESYSNAME") != null) && (transResult.get("STATEID") != null)) {
                // формирование итого результата (создание + перевод в 'Поставлен в очередь')
                result = createResult;
                if (result != null) {
                    result.putAll(transResult);
                } else {
                    throw new Exception("Invoked the method dsB2BMainActivityContractAgentReportSave the return null");
                }
            } else {
                // если перевод отчета в статус 'Поставлен в очередь' не удался - требуется выполнить откат всей транзакции целиком
                // (при выбрасывании исключения это произойдет автоматически)
                logger.error("State transition error! Details: " + transResult);
                throw new Exception("State transition error!");
            }

        }

        if (!resultErrorText.isEmpty()) {
            // если в ходе обновления было подготовлено описание ошибки необходимо сформировать соответствующий результат
            result = new HashMap<String, Object>();
            result.put("Error", resultErrorText);
        }

        logger.debug(String.format("Performing agent report action '%s' finished with result: ", ACTION_SYSNAME_GENERATE) + result);

        return result;
    }

    @WsMethod(requiredParams = {AGENT_REPORT_PK_FIELD_NAME, "STATESYSNAME"})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportDoActionToSK(Map<String, Object> params) throws Exception {
        Map<String, Object> result = agentReportActionChangeState(params, B2B_MACAGENTREPORT_IN_SK);
        return result;
    }

    @WsMethod(requiredParams = {AGENT_REPORT_PK_FIELD_NAME, "STATESYSNAME"})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportDoActionToAgent(Map<String, Object> params) throws Exception {
        Map<String, Object> result = agentReportActionChangeState(params, B2B_MACAGENTREPORT_IN_AGENT);
        return result;
    }

    @WsMethod(requiredParams = {AGENT_REPORT_PK_FIELD_NAME, "STATESYSNAME"})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportDoActionToAccepted(Map<String, Object> params) throws Exception {
        Map<String, Object> result = agentReportActionChangeState(params, B2B_MACAGENTREPORT_ACCEPTED);
        return result;
    }

    @WsMethod(requiredParams = {AGENT_REPORT_PK_FIELD_NAME})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportDoActionExport(Map<String, Object> params) throws Exception {
        // логин и пароль для вызова методов веб-сервисов
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // вызов формирования файла экспорта
        Map<String, Object> result = this.callService(SIGN_B2BPOSWS_SERVICE_NAME, "dsB2BPrimaryActivityContractAgentReportCreateXLSReport", params, IS_VERBOSE_CALLS_LOGGING, login, password);
        // возврат результата
        return result;
    }

    private Map<String, Object> agentReportActionChangeState(Map<String, Object> params, String toStateSysName) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        return agentReportActionChangeState(params, toStateSysName, login, password);
    }

    private Map<String, Object> agentReportActionChangeState(Map<String, Object> params, String toStateSysName, String login, String password) throws Exception {
        logger.debug("Changing state of agent report...");
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> transResult = agentReportMakeTrans(params, toStateSysName, login, password);
        if (isCallResultOK(transResult)) {
            result.putAll(transResult);
            result.put(AGENT_REPORT_PK_FIELD_NAME, params.get(AGENT_REPORT_PK_FIELD_NAME));
        } else {
            logger.error("Error during changing state of agent report! Details: " + transResult);
            result.put("Error", "Ошибка при смене состояния отчета агента!");
        }
        logger.debug("Changing state of agent report finished with result: " + result);
        return result;
    }

    // Запрос истории состояний указанного объекта определенного типа
    @WsMethod(requiredParams = {"OBJID", "TYPESYSNAME"})
    public Map<String, Object> dsB2BStateHistoryBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BStateHistoryBrowseListByParamCustomWhereEx", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BStateHistoryBrowseListByParamCustomWhereEx(Map<String, Object> params) throws Exception {
        logger.debug("Start agents report state history browse...");

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(params, Double.class);

        String idFieldName = "ID";
        String customWhereQueryName = "dsB2BStateHistoryBrowseListByParamCustomWhereEx";
        params.put(CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES_KEY_NAME, PAC_AGENT_REPORT_HISTORY_CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES);
        params.put(CUSTOM_WHERE_SUPPORTED_FIELDS_KEY_NAME, PAC_AGENT_REPORT_HISTORY_CUSTOM_WHERE_SUPPORTED_FIELDS);
        Map<String, Object> result = doCustomWhereQuery(customWhereQueryName, idFieldName, params);

        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(result, String.class);

        logger.debug("Agents report state history browse finish.");

        initActions();

        return result;
    }

    // Запрос истории состояний отчета агента по договорам основной деятельности для angular-грида на странице 'Основные' интерфейса отчета агента
    @WsMethod(requiredParams = {AGENT_REPORT_PK_FIELD_NAME})
    public Map<String, Object> dsB2BPrimaryActivityContractAgentReportStateHistoryBrowseListByParamEx(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова методов веб-сервисов
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        //datesParser.parseDates(params, Double.class);
        Map<String, Object> agentReport = new HashMap<String, Object>();
        Long agentReportID = getLongParamLogged(params, AGENT_REPORT_PK_FIELD_NAME);
        agentReport.put(AGENT_REPORT_PK_FIELD_NAME, agentReportID);

        updateAgentReportDataWithStateHistory(agentReport, login, password);

        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        //datesParser.parseDates(agentReport, String.class);
        return agentReport;
    }

}
