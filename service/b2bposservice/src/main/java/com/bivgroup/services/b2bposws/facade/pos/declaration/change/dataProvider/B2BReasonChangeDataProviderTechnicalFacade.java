package com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider;

import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BReasonChangeTechnicalDataProvider")
public class B2BReasonChangeDataProviderTechnicalFacade extends B2BReasonChangeDataProviderCustomFacade {
    private static String REASON_SYS_NAME_PARAM_NAME = "REASONSYSNAME";
    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Сервис формирования данных для изменения "Финансовые каникулы"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderFinancialVacation(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderFinancialVacation begin");
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        addViewFormInShowFormMap(reportData, login, password);
        Map<String, Object> contrOptionMap = getOrCreateMapParam(reportData, "CONTROPTIONMAP");
        boolean isCallFromGate = isCallFromGate(params);
        Object financeHolidaysDate = reasonMap.get("startFinHolidayDate" + (isCallFromGate ? "$date" : ""));
        if (financeHolidaysDate == null) {
            contrOptionMap.put("financeHolidaysDATESTR", " ");
        } else {
            contrOptionMap.put("financeHolidaysDATE", financeHolidaysDate);
        }
        contrOptionMap.put("applyDATE", reasonMap.get("changeDate" + (isCallFromGate ? "$date" : "")));
        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);

        Map<String, Object> changeReasonMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(changeReasonMap, "CHANGEOPTION");

        Long externalId = getLongParam(params.get("EXTERNALID"));
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String error = "";
        boolean isShapingDataForAdditionalForm = getBooleanParam(reportData, "isShapingDataForAdditionalForm", false);
        if (!isNotExistContract && !isShapingDataForAdditionalForm) {
            error = shapingDataForAdditionalForm(reportData, externalId, login, password);
        }
        // формирование результата
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            // формирование результата
            result.put("REPORTDATA", reportData);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BChangeReasonDataProviderFinancialVacation end");
        return result;
    }

    /**
     * Сервис формирования данных для изменения "Выход из финансовых каникул"
     * Пока что без реализации, т.к. выход из ФК мы не поддерживаем
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderExitFinancialVacation(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderExitFinancialVacation begin");
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        //Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        addViewFormInShowFormMap(reportData, login, password);
        // Map<String, Object> contrOptionMap = getOrCreateMapParam(reportData, "CONTROPTIONMAP");
        // boolean isCallFromGate = isCallFromGate(params);

        logger.debug("dsB2BChangeReasonDataProviderExitFinancialVacation end");
        return params;
    }

    /**
     * Сервис формирования данных для изменения "Перевод в оплаченный"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderTransferToPaid(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderTransferToPaid begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);

        addViewFormInShowFormMap(reportData, login, password);

        boolean isCallFromGate = isCallFromGate(params);
        Date changeDate = getDateParam(reasonMap.get("terminationOfPaymentsDate" + (isCallFromGate ? "$date" : "")));
        Map<String, Object> contrOptionMap = getOrCreateMapParam(reportData, "CONTROPTIONMAP");
        if (changeDate == null) {
            contrOptionMap.put("paymentTerminationDATESTR", " ");
        } else {
            contrOptionMap.put("paymentTerminationDATE", changeDate);
        }
        contrOptionMap.put("applyDATE", reasonMap.get("changeDate" + (isCallFromGate ? "$date" : "")));

        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);

        Map<String, Object> changeReasonMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(changeReasonMap, "CHANGEOPTION");
        logger.debug("dsB2BChangeReasonDataProviderTransferToPaid end");
        return params;
    }

    /**
     * Сервис формирования данных для изменения "Периодичности оплаты"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderInstalments(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderInstalments begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);

        addViewFormInShowFormMap(reportData, login, password);

        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);

        Map<String, Object> changeReasonMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(changeReasonMap, "CHANGEOPTION");

        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> contrOptionMap = getOrCreateMapParam(reportData, "CONTROPTIONMAP");
        contrOptionMap.put("changeType", "PAYVAR");
        contrOptionMap.put("applyDATE", reasonMap.get("changeDate" + (isCallFromGate ? "$date" : "")));

        Long newPayVarId = getLongParam(reasonMap, "newPayVarId");
        if (newPayVarId != null) {
            Map<String, Object> payVarQuery = new HashMap<>();
            payVarQuery.put("PAYVARID", newPayVarId);
            payVarQuery.put(RETURN_AS_HASH_MAP, TRUE_STR_VALUE);
            Map<String, Object> payVarResult = this.callServiceLogged(B2BPOSWS_SERVICE_NAME,
                    "dsB2BPaymentVariantBrowseListByParam", payVarQuery, login, password);
            String payVarSysName = getStringParam(payVarResult, "SYSNAME");
            contrOptionMap.put("newPayVar", payVarSysName);
        }

        Long externalId = getLongParam(params.get("EXTERNALID"));
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String error = "";
        boolean isShapingDataForAdditionalForm = getBooleanParam(reportData, "isShapingDataForAdditionalForm", false);
        if (!isNotExistContract && !isShapingDataForAdditionalForm) {
            error = shapingDataForAdditionalForm(reportData, externalId, login, password);
        }
        // формирование результата
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            // формирование результата
            result.put("REPORTDATA", reportData);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BChangeReasonDataProviderInstalments end");
        return result;
    }

    /**
     * Сервис формирования данных для изменения "Исключение программ"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderExcludePrograms(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderExcludePrograms begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);

        addViewFormInShowFormMap(reportData, login, password);

        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);

        Map<String, Object> changeReasonMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(changeReasonMap, "CHANGEOPTION");

        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> contrOptionMap = getOrCreateMapParam(reportData, "CONTROPTIONMAP");
        contrOptionMap.put("applyDATE", reasonMap.get("changeDate" + (isCallFromGate ? "$date" : "")));

		boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        generationDelRiskList(reasonMap, contrOptionMap, isNotExistContract, login, password);

        Long externalId = getLongParam(params.get("EXTERNALID"));
        String error = "";
        boolean isShapingDataForAdditionalForm = getBooleanParam(reportData, "isShapingDataForAdditionalForm", false);
        if (!isNotExistContract && !isShapingDataForAdditionalForm) {
            error = shapingDataForAdditionalForm(reportData, externalId, login, password);
        }
        // формирование результата
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            // формирование результата
            result.put("REPORTDATA", reportData);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BChangeReasonDataProviderExcludePrograms end");
        return result;
    }

    /**
     * Сервис формирования данных для изменения "Включение программ"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderIncludePrograms(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderIncludePrograms begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);

        addViewFormInShowFormMap(reportData, login, password);

        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);

        Map<String, Object> changeReasonMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(changeReasonMap, "CHANGEOPTION");

        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> contrOptionMap = getOrCreateMapParam(reportData, "CONTROPTIONMAP");
        contrOptionMap.put("applyDATE", reasonMap.get("changeDate" + (isCallFromGate ? "$date" : "")));

        generationAddRiskList(reasonMap, contrOptionMap, login, password);

        Long externalId = getLongParam(params.get("EXTERNALID"));
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String error = "";
        boolean isShapingDataForAdditionalForm = getBooleanParam(reportData, "isShapingDataForAdditionalForm", false);
        if (!isNotExistContract && !isShapingDataForAdditionalForm) {
            error = shapingDataForAdditionalForm(reportData, externalId, login, password);
        }
        // формирование результата
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            // формирование результата
            result.put("REPORTDATA", reportData);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BChangeReasonDataProviderIncludePrograms end");
        return result;
    }

    /**
     * Сервис формирования данных для изменения "Увеличение взноса / СС"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderIncreaseInsSum(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderIncreaseInsSum begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);

        addViewFormInShowFormMap(reportData, login, password);

        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);

        Map<String, Object> changeReasonMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(changeReasonMap, "CHANGEOPTION");

        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> contrOptionMap = getOrCreateMapParam(reportData, "CONTROPTIONMAP");
        contrOptionMap.put("applyDATE", reasonMap.get("changeDate" + (isCallFromGate ? "$date" : "")));

        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        generationChangeRiskList(reasonMap, contrOptionMap, isNotExistContract, login, password);

        Long externalId = getLongParam(params.get("EXTERNALID"));
        String error = "";
        boolean isShapingDataForAdditionalForm = getBooleanParam(reportData, "isShapingDataForAdditionalForm", false);
        if (!isNotExistContract && !isShapingDataForAdditionalForm) {
            error = shapingDataForAdditionalForm(reportData, externalId, login, password);
        }
        // формирование результата
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            // формирование результата
            result.put("REPORTDATA", reportData);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BChangeReasonDataProviderIncreaseInsSum end");
        return result;
    }

    /**
     * Сервис формирования данных для изменения "Уменьшение взноса / СС"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderDecreaseInsSum(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderDecreaseInsSum begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);

        addViewFormInShowFormMap(reportData, login, password);

        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);

        Map<String, Object> changeReasonMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(changeReasonMap, "CHANGEOPTION");

        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> contrOptionMap = getOrCreateMapParam(reportData, "CONTROPTIONMAP");
        contrOptionMap.put("applyDATE", reasonMap.get("changeDate" + (isCallFromGate ? "$date" : "")));

        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        generationChangeRiskList(reasonMap, contrOptionMap, isNotExistContract, login, password);

        Long externalId = getLongParam(params.get("EXTERNALID"));
        String error = "";
        boolean isShapingDataForAdditionalForm = getBooleanParam(reportData, "isShapingDataForAdditionalForm", false);
        if (!isNotExistContract && !isShapingDataForAdditionalForm) {
            error = shapingDataForAdditionalForm(reportData, externalId, login, password);
        }
        // формирование результата
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            // формирование результата
            result.put("REPORTDATA", reportData);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BChangeReasonDataProviderDecreaseInsSum end");
        return result;
    }

    private void generationAddRiskList(Map<String, Object> reasonMap, Map<String, Object> contrOptionMap,
                                       String login, String password) throws Exception {
        List<Map<String, Object>> addRiskList = getOrCreateListParam(contrOptionMap, "addRiskList");
        processRiskAndAddToRiskList(addRiskList, reasonMap, contrOptionMap, login, password);
    }

    private void generationDelRiskList(Map<String, Object> reasonMap, Map<String, Object> contrOptionMap,
                                       boolean isNotExistContract, String login, String password) throws Exception {
        List<Map<String, Object>> delRiskList = getOrCreateListParam(contrOptionMap, "delRiskList");
        String riskSysname = getStringParam(reasonMap, "riskSysName");
        if (riskSysname.contains("DISABILITY") || isNotExistContract) {
            contrOptionMap.put("delDisabilityRisk", TRUE_STR_VALUE);
        }
        processRiskAndAddToRiskList(delRiskList, reasonMap, contrOptionMap, login, password);
    }

    private void generationChangeRiskList(Map<String, Object> reasonMap, Map<String, Object> contrOptionMap,
                                          boolean isNotExistContract, String login, String password) throws Exception {
        List<Map<String, Object>> changeRiskList = getOrCreateListParam(contrOptionMap, "changeRiskList");
        processRiskAndAddToRiskList(changeRiskList, reasonMap, contrOptionMap, login, password);
        if (changeRiskList.isEmpty() && isNotExistContract) {
            Map<String, Object> riskInfo = new HashMap<>();
            riskInfo.put("progRiskName", " ");
            riskInfo.put("newInsAmVALUESTR", " ");
            riskInfo.put("newPremVALUESTR", " ");
            changeRiskList.add(riskInfo);
        }
    }

    private void processRiskAndAddToRiskList(List<Map<String, Object>> riskList, Map<String, Object> reasonMap,
                                             Map<String, Object> contrOptionMap, String login, String password) throws Exception {
        Map<String, Object> prodStructQuery = new HashMap<>();
        String riskSysname = getStringParam(reasonMap, "riskSysName");
        prodStructQuery.put("SYSNAME", riskSysname);
        List<Map<String, Object>> prodStructResult = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME,
                "dsB2BProductStructureBaseBrowseListByParam", prodStructQuery, login, password);
        if (!prodStructResult.isEmpty()) {
            Map<String, Object> risk = new HashMap<>();
            risk.put("progRiskName", getStringParam(prodStructResult.get(0), "NAME"));
            String insAmValue = getStringParam(reasonMap, "insAmValue");
            if (!insAmValue.isEmpty()) {
                insAmValue = customFormat(Double.parseDouble(insAmValue));
            } else {
                insAmValue = getStringParam(reasonMap, "newInsAmValue");
                if (!insAmValue.isEmpty()) {
                    insAmValue = customFormat(Double.parseDouble(insAmValue));
                    if (riskSysname.contains("SELF_DEATH_CLAUSE")) {
                        contrOptionMap.put("newDeathInsAmVALUESTR", insAmValue);
                    }
                    contrOptionMap.put("newAllInsAmVALUESTR", insAmValue);
                }
            }
            risk.put("newInsAmVALUESTR", insAmValue);

            String premValue = getStringParam(reasonMap, "newPremValue");
            if (!premValue.isEmpty()) {
                premValue = customFormat(Double.parseDouble(premValue));
                if (riskSysname.contains("SELF_DEATH_CLAUSE")) {
                    contrOptionMap.put("newDeathPremVALUESTR", premValue);
                }
                contrOptionMap.put("newAllPremVALUESTR", premValue);
            }
            risk.put("newPremVALUESTR", premValue);

            riskList.add(risk);
        }
    }

    private String customFormat(double value) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator(',');
        dfs.setGroupingSeparator(' ');
        DecimalFormat myFormatter = new DecimalFormat("###,###.00", dfs);
        return myFormatter.format(value);
    }

    /**
     * Сервис формирования данных для изменения "Сокращение срока страхования"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderDecreasePeriod(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderDecreasePeriod begin");
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        boolean isCallFromGate = isCallFromGate(params);
        addViewFormInShowFormMap(reportData, login, password);
        Long newTermId = getLongParam(reasonMap, "newTermId");
        Map<String, Object> termInfo = getTermDataByTermId(newTermId, login, password);
        String duration = getStringParam(termInfo, "YEARCOUNT");
        Map<String, Object> contrOptionMap = getOrCreateMapParam(reportData, "CONTROPTIONMAP");
        contrOptionMap.put("changeType", "DURATION");
        contrOptionMap.put("newDuration", duration);
        contrOptionMap.put("applyDATE", reasonMap.get("changeDate" + (isCallFromGate ? "$date" : "")));

        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);

        Map<String, Object> changeReasonMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(changeReasonMap, "CHANGEOPTION");

        Long externalId = getLongParam(params.get("EXTERNALID"));
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String error = "";
        boolean isShapingDataForAdditionalForm = getBooleanParam(reportData, "isShapingDataForAdditionalForm", false);
        if (!isNotExistContract && !isShapingDataForAdditionalForm) {
            error = shapingDataForAdditionalForm(reportData, externalId, login, password);
        }
        // формирование результата
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            // формирование результата
            result.put("REPORTDATA", reportData);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BChangeReasonDataProviderDecreasePeriod end");
        return result;
    }

    /**
     * Сервис формирования данных для изменения "Увеличение срока страхования"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderIncreasePeriod(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderIncreasePeriod begin");
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        boolean isCallFromGate = isCallFromGate(params);
        addViewFormInShowFormMap(reportData, login, password);
        Long newTermId = getLongParam(reasonMap, "newTermId");
        Map<String, Object> termInfo = getTermDataByTermId(newTermId, login, password);
        String duration = getStringParam(termInfo, "YEARCOUNT");
        Map<String, Object> contrOptionMap = getOrCreateMapParam(reportData, "CONTROPTIONMAP");
        contrOptionMap.put("changeType", "DURATION");
        contrOptionMap.put("newDuration", duration);
        contrOptionMap.put("applyDATE", reasonMap.get("changeDate" + (isCallFromGate ? "$date" : "")));

        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);

        Map<String, Object> changeReasonMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(changeReasonMap, "CHANGEOPTION");

        Long externalId = getLongParam(params.get("EXTERNALID"));
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        String error = "";
        boolean isShapingDataForAdditionalForm = getBooleanParam(reportData, "isShapingDataForAdditionalForm", false);
        if (!isNotExistContract && !isShapingDataForAdditionalForm) {
            error = shapingDataForAdditionalForm(reportData, externalId, login, password);
        }
        // формирование результата
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            // формирование результата
            result.put("REPORTDATA", reportData);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BChangeReasonDataProviderIncreasePeriod end");
        return result;
    }

    /**
     * Метод формирования данных для доп анкеты
     *
     * @param reportData
     */
    private String shapingDataForAdditionalForm(Map<String, Object> reportData, Long externalId, String login, String password) throws Exception {
        boolean isNeedPrintAdditionalForm = isNeedPrintAdditionalForm(reportData, login, password);
        String error = "";
        if (!isNeedPrintAdditionalForm) {
            return error;
        }

        Map<String, Object> fullContract = null;

        if (error.isEmpty()) {
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }

        List<Map<String, Object>> childInsuredList = new ArrayList<>();
        List<Map<String, Object>> adultsInsuredList = new ArrayList<>();
        if (error.isEmpty()) {
            List<Map<String, Object>> allInsuredList = getInsuredList(fullContract);
            for (Map<String, Object> item : allInsuredList) {
                Map<String, Object> participant = getOrCreateMapParam(item, "PARTICIPANTMAP");
                Date birthDate = getDateParam(participant.get("BIRTHDATE"));
                participant.put("BIRTHDATE", getDateParam(birthDate));
                if (birthDate != null) {
                    Integer years = calculateAge(birthDate);
                    participant.put("YEARS", years);
                    if (years < 18) {
                        childInsuredList.add(participant);
                    } else {
                        adultsInsuredList.add(participant);
                    }
                } else {
                    error = "У застрахованного отсутствует поле дата рождения";
                }
            }
        }

        if (error.isEmpty()) {
            if (!childInsuredList.isEmpty()) {
                Map<String, Object> childInsuredMap = insuredMap(childInsuredList.get(0));
                reportData.put("CHILDINSUREDMAP", childInsuredMap);
                reportData.put("IS_NEED_ADDITIONAL_CHILD_FORM", TRUE_STR_VALUE);
            }
            if (!adultsInsuredList.isEmpty()) {
                List<Map<String, Object>> insureList = getOrCreateListParam(reportData, INSURED_MAP_PARAMNAME + "LIST");
                for (Map<String, Object> item : adultsInsuredList) {
                    insureList.add(insuredMap(item));
                }
                reportData.put("IS_NEED_ADDITIONAL_FORM", TRUE_STR_VALUE);
            }
            reportData.put("isShapingDataForAdditionalForm", true);
        }

        return error;
    }

    private Map<String, Object> insuredMap(Map<String, Object> insured) {
        Map<String, Object> map = new HashMap<>();
        map.put("LASTNAME", insured.get("LASTNAME"));
        map.put("FIRSTNAME", insured.get("FIRSTNAME"));
        map.put("MIDDLENAME", insured.get("MIDDLENAME"));
        map.put("BRIEFNAME", generateBriefName(insured));
        map.put("BIRTHDATE", insured.get("BIRTHDATE"));
        map.put("YEARS", insured.get("YEARS"));
        return map;
    }

    /**
     * Требуется ли печатать доп анкету
     *
     * @param reportData - данные для печати
     * @param login      - логин
     * @param password   - пароль
     * @return true - если анкету требуется печатать и false - если не требуется
     * @throws Exception
     */
    private boolean isNeedPrintAdditionalForm(Map<String, Object> reportData, String login, String password) throws Exception {
        String prodProgramSysName = getStringParam(reportData, "PRODPROGSYSNAME");
        String reasonSysName = getStringParam(reportData, REASON_SYS_NAME_PARAM_NAME);
        String findParamName = prodProgramSysName + "_" + reasonSysName;

        Long prodConfId = getLongParam(reportData, "PRODCONFID");

        Map<String, Object> prodDefValQuerry = new HashMap<>();
        prodDefValQuerry.put("PRODCONFID", prodConfId);
        prodDefValQuerry.put("NAME", findParamName);

        List<Map<String, Object>> prodDefValList = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME,
                "dsB2BProductDefaultValueBrowseListByParam",
                prodDefValQuerry, login, password);
        boolean result = false;
        if (!prodDefValList.isEmpty()) {
            result = getBooleanParam(prodDefValList.get(0), "VALUE", false);
        }

        return result;
    }

    /**
     * Получить список застрахованных из полной информации по договоре
     * В полной информации по договору берем "MEMBERLIST" и его фильтруем по
     * "TYPESYSNAME" со значением "insured", для получения всех застрахованных
     *
     * @param fullContract - полная информация по договору
     * @return
     */
    private List<Map<String, Object>> getInsuredList(Map<String, Object> fullContract) {
        List<Map<String, Object>> memberList = getOrCreateListParam(fullContract, "MEMBERLIST");
        memberList = memberList.stream().filter(new Predicate<Map<String, Object>>() {
            @Override
            public boolean test(Map<String, Object> stringObjectMap) {
                Map<String, Object> participantMap = getMapParam(stringObjectMap, "PARTICIPANTMAP");
                return "insured".equals(stringObjectMap.get("TYPESYSNAME"));
            }
        }).collect(Collectors.toList());
        return memberList;
    }

    private String generateBriefName(Map<String, Object> personMap) {
        StringBuilder briefName = new StringBuilder();
        String lastName = getStringParam(personMap, "LASTNAME");
        if (!lastName.isEmpty()) {
            briefName.append(lastName);
        }

        String firstName = getStringParam(personMap, "FIRSTNAME");
        if (!firstName.isEmpty()) {
            briefName.append(" ").append(firstName.charAt(0)).append(".");
        }

        String middleName = getStringParam(personMap, "MIDDLENAME");
        if (!middleName.isEmpty()) {
            briefName.append(" ").append(middleName.charAt(0)).append(".");
        }

        return briefName.toString();
    }

    /**
     * Метод для рассчета возраста по дате рождения
     *
     * @param birthday дата рождения
     * @return возраст
     */
    protected Integer calculateAge(Date birthday) {
        // дата рождения
        Calendar dob = GregorianCalendar.getInstance();
        dob.setTime(birthday);
        // включая день рождения
        dob.add(Calendar.DAY_OF_MONTH, -1);

        // сегоднящний день
        Calendar today = GregorianCalendar.getInstance();

        Integer age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    /**
     * Получение сведений о сроке страхования
     *
     * @param termId   - идентификатор срока
     * @param login    - логин
     * @param password - пароль
     * @return
     * @throws Exception
     */
    private Map<String, Object> getTermDataByTermId(Long termId, String login, String password) throws Exception {
        Map<String, Object> termParams = new HashMap<String, Object>();
        termParams.put("TERMID", termId);
        termParams.put(RETURN_AS_HASH_MAP, TRUE_STR_VALUE);
        Map<String, Object> result = new HashMap<>();
        if (termId != null) {
            result.putAll(this.callServiceTimeLogged(
                    B2BPOSWS_SERVICE_NAME, "dsB2BTermBrowseListByParam", termParams, login, password
            ));
        }
        return result;
    }

    private void addViewFormInShowFormMap(Map<String, Object> reportData, String login, String password) throws Exception {
        String formNumber = selectNumberPrintForm(reportData, login, password);
        Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
        showFormMap.put("FORM" + formNumber, TRUE_STR_VALUE);
    }

    private String selectNumberPrintForm(Map<String, Object> reportData, String login, String password) throws Exception {
        String prodProgramSysName = getStringParam(reportData, "PRODPROGSYSNAME");
        Long prodConfId = getLongParam(reportData, "PRODCONFID");
        Map<String, Object> prodDefValQuerry = new HashMap<>();
        prodDefValQuerry.put("PRODCONFID", prodConfId);
        String findFormName = prodProgramSysName + "_TECH_" + "FORM";
        prodDefValQuerry.put("NAME", findFormName);
        List<Map<String, Object>> prodDefValList = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME,
                "dsB2BProductDefaultValueBrowseListByParam",
                prodDefValQuerry, login, password);
        String formNumber = "";
        if (!prodDefValList.isEmpty()) {
            formNumber = getStringParam(prodDefValList.get(0), "VALUE");
        }
        if (formNumber.isEmpty()) {
            formNumber = "2";
        }
        return formNumber;
    }

}
