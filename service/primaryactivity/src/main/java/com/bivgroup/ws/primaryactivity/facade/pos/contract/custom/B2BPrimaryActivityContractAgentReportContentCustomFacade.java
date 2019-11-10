/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.primaryactivity.facade.pos.contract.custom;

import com.bivgroup.ws.primaryactivity.facade.B2BPrimaryActivityBaseFacade;
import com.bivgroup.ws.primaryactivity.system.Constants;
import com.bivgroup.ws.primaryactivity.system.DatesParser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("B2BPrimaryActivityContractAgentReportContentCustom")
public class B2BPrimaryActivityContractAgentReportContentCustomFacade extends B2BPrimaryActivityBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    // Имя сервиса для вызова методов из b2bposservice
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    // Имя сервиса для вызова методов из primaryactivity
    private static final String THIS_SERVICE_NAME = Constants.B2BPOSWS;

    private static DatesParser datesParser;

    // флаг подробного протоколирования операций с датами и переопределения параметров
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

    // флаг подробного протоколирования вызовов методов веб-сервисов
    // (после завершения отладки можно отключить)
    private final boolean IS_VERBOSE_CALLS_LOGGING = logger.isDebugEnabled();

    // имя поля с PK в таблице содержимого отчета агента
    private static final String AGENT_REPORT_CONTENT_PK_FIELD_NAME = "MACAGENTREPORTCNTID";

    // имя поля с PK в таблице отчета агента
    private static final String AGENT_REPORT_PK_FIELD_NAME = "MACAGENTREPORTID";

    protected static final String[] PAC_AGENT_REPORT_CONTENT_CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES = {
        "T",
        "T2",
        "T3",
        "T41",
        "T42",
        "T43",
        "T51",
        "T52",
        "T53",
        "T6",
        "T7"
    };

    protected static final String[] PAC_AGENT_REPORT_CONTENT_CUSTOM_WHERE_SUPPORTED_FIELDS = {
        "CONTRID",
        AGENT_REPORT_CONTENT_PK_FIELD_NAME,
        "ISREMOVED",
        AGENT_REPORT_PK_FIELD_NAME,
        "CONTRNUMBER",
        "DOCUMENTDATE",
        "SIGNDATE",
        "STARTDATE",
        "FINISHDATE",
        "PREMVALUE",
        "PARTICIPANTID",
        "UPDATEDATE",
        "INSURERID",
        "CREATEUSERID",
        "USERACCOUNTID",
        "UPDATEUSERID",
        "INSURERID",
        "INSREGIONCODE",
        "CODE"
    };

    public B2BPrimaryActivityContractAgentReportContentCustomFacade() {
        super();
        init();
    }

    private void init() {
        // обработчик дат
        datesParser = new DatesParser();
        // обработчик дат - протоколирование операций с датами
        datesParser.setVerboseLogging(IS_VERBOSE_LOGGING);
    }

    @WsMethod(requiredParams = {AGENT_REPORT_PK_FIELD_NAME})
    public Map<String, Object> dsB2BMainActivityContractAgentReportContentBrowseListByAgentReportID(Map<String, Object> params) throws Exception {
        
        logger.debug("Start agents reports content browse by agent report id...");
        
        Long agentReportID = getLongParamLogged(params, AGENT_REPORT_PK_FIELD_NAME);
        
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put(AGENT_REPORT_PK_FIELD_NAME, agentReportID);
        queryParams.put("ISREMOVED", params.get("ISREMOVED"));
        
        boolean isForExport = getBooleanParam(params, FOR_EXPORT, false);
        String queryName;
        if (isForExport) {
            queryName = "dsB2BMainActivityContractAgentReportContentBrowseListByParamForExport";
        } else {
            queryName = "dsB2BMainActivityContractAgentReportContentBrowseListByParamCustomWhereEx";
        }

        Map<String, Object> result = this.selectQuery(queryName, queryParams);

        logger.debug("Agents reports content browse by agent report id finish.");

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMainActivityContractAgentReportContentBrowseListByParamsEx(Map<String, Object> params) throws Exception {
        
        logger.debug("Start agents reports content browse by parameters...");
        
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.putAll(params);
        
        Map<String, Object> result = this.selectQuery("dsB2BMainActivityContractAgentReportContentBrowseListByParamCustomWhereEx", queryParams);
        
        logger.debug("Agents reports content browse by parameters finish.");

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BMainActivityContractAgentReportContentBrowseListByParamCustomWhereEx(Map<String, Object> params) throws Exception {
        logger.debug("Start agents reports content browse...");

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(params, Double.class);

        String idFieldName = AGENT_REPORT_CONTENT_PK_FIELD_NAME;
        String customWhereQueryName = "dsB2BMainActivityContractAgentReportContentBrowseListByParamCustomWhereEx";
        params.put(CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES_KEY_NAME, PAC_AGENT_REPORT_CONTENT_CUSTOM_WHERE_SUPPORTED_TABLE_ALIASES);
        params.put(CUSTOM_WHERE_SUPPORTED_FIELDS_KEY_NAME, PAC_AGENT_REPORT_CONTENT_CUSTOM_WHERE_SUPPORTED_FIELDS);
        Map<String, Object> result = doCustomWhereQuery(customWhereQueryName, idFieldName, params);

        // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
        datesParser.parseDates(result, String.class);

        logger.debug("Agents reports content browse finish.");

        return result;
    }
    
    // изменение флага 'Исключен' записи содержимого отчета агента для действий 'Исключить/Включить договор' на странице 'Содержимое' angular-интерфейса отчета агента
    @WsMethod(requiredParams = {AGENT_REPORT_CONTENT_PK_FIELD_NAME, "CONTRID", "ISREMOVED"})
    public Map<String, Object> dsB2BMainActivityContractAgentReportContentUpdateRemoved(Map<String, Object> params) throws Exception {

        logger.debug("Changing 'removed' flag (ISREMOVED) in primary activity agent report content...");

        // логин и пароль для вызова методов веб-сервисов
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String resultErrorText = "";
        Map<String, Object> result = null;

        Long agentReportContentID = getLongParamLogged(params, AGENT_REPORT_CONTENT_PK_FIELD_NAME);
        Long isRemoved = getLongParamLogged(params, "ISREMOVED");

        List<Map<String, Object>> duplicatesList = null;
        if (isRemoved == 0) {
            // при включении исключенного ранее договора необходимо проверить не был ли он уже включен в другие отчеты
            Map<String, Object> duplicatesParams = new HashMap<String, Object>();
            Long contractID = getLongParamLogged(params, "CONTRID");
            duplicatesParams.put("CONTRID", contractID);
            duplicatesParams.put("EXCLUDED" + AGENT_REPORT_CONTENT_PK_FIELD_NAME, agentReportContentID);
            duplicatesParams.put("ISREMOVED", 0L);
            duplicatesList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BMainActivityContractAgentReportContentBrowseListByParamsEx", duplicatesParams, IS_VERBOSE_CALLS_LOGGING, login, password);
        } else {
            // при исключении договора дополнительная проверка на треубется
            //duplicatesList = new ArrayList<Map<String, Object>>();
        }

        if ((duplicatesList != null) && (duplicatesList.size() > 0)) {

            // была вполнена дополнительная проверка и установлено, что данный договор уже был включен в другие отчеты
            logger.debug("This contract already included in another agent report(s) with number(s): ");
            StringBuilder duplicatesAgentReportsNumbersSB = new StringBuilder();
            for (Map<String, Object> duplicate : duplicatesList) {
                String duplicateAgentReportNumber = getStringParamLogged(duplicate, "MACAGENTREPORTNUMBER");
                duplicatesAgentReportsNumbersSB.append("№ ").append(duplicateAgentReportNumber).append(", ");
            }
            duplicatesAgentReportsNumbersSB.setLength(duplicatesAgentReportsNumbersSB.length() - 2);
            resultErrorText = String.format("Данный договор уже включен в состав другого отчета агента (%s)!", duplicatesAgentReportsNumbersSB.toString());

        } else {

            // договор допустимо включать/исключать
            Map<String, Object> updateParams = new HashMap<String, Object>();
            updateParams.put(AGENT_REPORT_CONTENT_PK_FIELD_NAME, agentReportContentID);
            updateParams.put("ISREMOVED", isRemoved);
            updateParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> updateResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BMainActivityContractAgentReportContentUpdate", updateParams, IS_VERBOSE_CALLS_LOGGING, login, password);
            logger.debug("Changing 'removed' flag (ISREMOVED) in primary activity agent report content finished with result: " + updateResult);

            // проверка результата обновления флага 'Исключен' записи содержимого отчета агента
            agentReportContentID = getLongParamLogged(updateResult, AGENT_REPORT_CONTENT_PK_FIELD_NAME);
            if (agentReportContentID == null) {
                logger.error("Error while updating primary activity agent report content record! Details: " + updateResult);
                resultErrorText = "Ошибка при обновлении записи содержимого отчета агента!";
            } else {
                // загурзка полных сведений обновленной записи содержимого отчета агента (включая дату обновления и обновившего пользователя)
                Map<String, Object> browseParams = new HashMap<String, Object>();
                browseParams.put(AGENT_REPORT_CONTENT_PK_FIELD_NAME, agentReportContentID);
                browseParams.put(RETURN_AS_HASH_MAP, true);
                result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BMainActivityContractAgentReportContentBrowseListByParamCustomWhereEx", browseParams, IS_VERBOSE_CALLS_LOGGING, login, password);
                logger.debug("Loaded updated primary activity agent report content: " + result);
            }

        }

        if (!resultErrorText.isEmpty()) {
            // если в ходе обновления было подготовлено описание ошибки необходимо сформировать соответствующий результат
            result = new HashMap<String, Object>();
            result.put("Error", resultErrorText);
        }

        return result;
    }

    @WsMethod(requiredParams = {AGENT_REPORT_PK_FIELD_NAME, "ISREMOVED", "rows", "totalCount"})
    public Map<String, Object> dsB2BMainActivityContractAgentReportContentMassCreate(Map<String, Object> params) throws Exception {
        
        int[] insertResult = this.insertQuery("dsB2BMainActivityContractAgentReportContentMassCreate", params);
        //int[] insertResult = {0, 0, 0}; !только для отладки!

        if (logger.isDebugEnabled()) {
            int totalSavedRows = 0;
            for (int i : insertResult) {
                totalSavedRows = totalSavedRows + i;
            }
            logger.debug("Rows created in DB: " + totalSavedRows);
        }

        return params;
    }    
    
}
