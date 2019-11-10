package com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider;

import com.bivgroup.services.b2bposws.facade.pos.declaration.utils.MappingHelper;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bivgroup.services.b2bposws.facade.pos.declaration.change.B2BReasonChangeCustomFacade.FIX_INCOME_FIX_TYPE_AUTO;
import static com.bivgroup.services.b2bposws.facade.pos.declaration.change.B2BReasonChangeCustomFacade.FIX_INCOME_FIX_TYPE_ONCE;
import static com.bivgroup.services.b2bposws.facade.pos.declaration.change.B2BReasonChangeCustomFacade.FIX_INCOME_LIMIT_PCT_FACTOR;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BReasonChangeDataProviderOptions")
public class B2BReasonChangeDataProviderOptionsFacade extends B2BReasonChangeDataProviderCustomFacade {
    private final Logger logger = Logger.getLogger(this.getClass());

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderChangeFund(Map<String, Object> params) throws Exception {
        // новый фонд (REPORTDATA.NEWRISKFUND)
        String newFundSysName = "";
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Long newFundId = getLongParamLogged(reasonMap, "fundId");
        String error = "";
        if (newFundId != null) {
            // имеются данные о смене фонда
            Map<String, Object> fundParams = new HashMap<>();
            fundParams.put("INVBASEACTIVEID", newFundId);
            fundParams.put(RETURN_AS_HASH_MAP, true);
            String methodName = "dsB2BInvestBaseActiveBrowseListByParam";
            Map<String, Object> newFund = callServiceLogged(THIS_SERVICE_NAME, methodName, fundParams, login, password);
            // полные сведения о фонде (на всякий случай)
            reportData.put("NEWRISKFUNDMAP", newFund);
            newFundSysName = getStringParamLogged(newFund, "SYSNAME");
            if (newFundSysName.isEmpty()) {
                logger.error(String.format(
                        "Unable to get new fund sysname by calling %s with params: %s! Details (call result): %s.",
                        methodName, fundParams, newFund
                ));
                error = "Не удалось получить сведения о новом фонде!";
            } else {
                // REPORTDATA.NEWRISKFUND, Андрей К.: "сис. наименование из БД"
                reportData.put("NEWRISKFUND", newFundSysName);
                reportData.put("NEWRISKFUNDNAME", getStringParam(newFund,"NAME"));
            }
        } else {
            reportData.put("NEWRISKFUND", EMPTY_STRING);
            reportData.put("NEWRISKFUNDNAME", EMPTY_STRING);
        }

        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        if (error.isEmpty() && !isNotExistContract) {
            formationServiceMarks(params, reportData);
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderFixIncome(Map<String, Object> params) throws Exception {
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);

        String error = "";
        Long fixTypeId = getLongParamLogged(reasonMap, "fixTypeId");
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        if (fixTypeId != null) {
            Double fixMaxLimit = getDoubleParamLogged(reasonMap, "fixMaxLimit") / FIX_INCOME_LIMIT_PCT_FACTOR;
            Double fixMinLimit = getDoubleParamLogged(reasonMap, "fixMinLimit") / FIX_INCOME_LIMIT_PCT_FACTOR;

            String fixMaxLimitStr = getStringParam(fixMaxLimit);
            String fixMinLimitStr = getStringParam(fixMinLimit);

            if (FIX_INCOME_FIX_TYPE_ONCE.equals(fixTypeId) && (fixMaxLimit < 0.0001)) {
                // Паша П: "Для опции Единовременная фиксация: maxPercent = minPercent = Размер минимально фиксируемого дополнительного инвестиционного дохода"
                fixMaxLimitStr = fixMinLimitStr;
            }
            // Паша П: "optionState = ON, если опция включена, и OFF, если выключена.
            // Для опции Единовременная фиксация: optionSysName = ONETIMEFIX,
            // maxPercent = minPercent = Размер минимально фиксируемого дополнительного инвестиционного дохода
            // Для опции Автопилот: optionSysName = AUTOPILOT
            // maxPercent - верхний лимит для фиксации ДИД. Возможны строковые значения 15, 30 или 50.
            // minPercent - нижний лимит для фиксации ДИД. Возможны строковые значения 15, 30 или 50."
            Map<String, Object> option;
            Map<Long, Map<String, Object>> optionMap = new HashMap<>();
            option = new HashMap<String, Object>();
            option.put("optionSysName", "ONETIMEFIX");
            optionMap.put(FIX_INCOME_FIX_TYPE_ONCE, option);
            option = new HashMap<String, Object>();
            option.put("optionSysName", "AUTOPILOT");
            optionMap.put(FIX_INCOME_FIX_TYPE_AUTO, option);
            List<Map<String, Object>> optionList = new ArrayList<Map<String, Object>>(optionMap.values());
            for (Map<String, Object> optionItem : optionList) {
                optionItem.put("optionState", "OFF");
                optionItem.put("maxPercent", "");
                optionItem.put("minPercent", "");
            }
            option = optionMap.get(fixTypeId);
            if (option == null) {
                logger.error(String.format(
                        "Unable to resolve fix type %d for 'ReasonChangeForContract_FixIncome' change reason! Details (reason map): %s.",
                        fixTypeId, reasonMap
                ));
                error = "Не удалось получить сведения о типе фиксации инвестиционного дохода!";
            } else {
                option.put("optionState", "ON");
                option.put("maxPercent", fixMaxLimitStr);
                option.put("minPercent", fixMinLimitStr);
                // Информация по опциям Единовременная фиксация и Авотпилот (REPORTDATA.OPTIONSLIST)
                reportData.put("OPTIONSLIST", optionList);
            }
        } else {
            reportData.put("OPTIONSLIST", new ArrayList<Map<String, Object>>());
        }

        if (error.isEmpty() && !isNotExistContract) {
            formationServiceMarks(params, reportData);
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderWithdrawIncome(Map<String, Object> params) throws Exception {
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);

        String error = "";

        Map<String, Object> bankDetails = getMapParam(reasonMap, "bankDetailsId_EN");
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        Map<String, Object> didBankMap = MappingHelper.bankDetailsMapping(bankDetails, isNotExistContract);
        if (!didBankMap.isEmpty()) {
            reportData.put("DIDBANKMAP", didBankMap);
        } else {
            logger.error(String.format(
                    "Failed to get information about recipient's details for 'ReasonChangeForContract_WithdrawIncome' change reason! Details (reason map): %s.",
                    reasonMap
            ));
            error = "Не удалось получить сведения о реквизитах получателя!";
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderExtPremPay(Map<String, Object> params) throws Exception {
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        String error = "";
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        if (reasonMap != null && !reasonMap.isEmpty() && !isNotExistContract) {
            Double dopPremVal = getDoubleParamLogged(reasonMap, "dopPremVal");
            reportData.put("ADDINSPREMVALUE", dopPremVal);
            if ("1".equals(getStringParam(reportData.get("PREMCURRENCYID")))) {
                // рубли
                reportData.put("ADDINSPREMRUBVALUE", dopPremVal);
            } else {
                // валюта usd
                reportData.put("ADDINSPREMUSDVALUE", dopPremVal);
            }
        } else {
            if (isNotExistContract) {
                reportData.put("ADDINSPREMVALUE", EMPTY_STRING);
            } else {
                logger.error(String.format(
                        "Failed to get information about additional insurance premium for 'ReasonChangeForContract_WithdrawIncome' change reason! Details (reason map): %s.",
                        reasonMap
                ));
                error = "Не удалось получить сведения о дополнительном страховом взносе!";
            }
        }

        if (error.isEmpty() && !isNotExistContract) {
            formationServiceMarks(params, reportData);
        }

        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Long externalId = getLongParam(params.get("EXTERNALID"));
        Map<String, Object> fullContractMap = getFullContractInfo(externalId, login, password);
        if (error.isEmpty() && !isNotExistContract) {
            error = getStringParam(fullContractMap, ERROR);
        }
        if (error.isEmpty() && !isNotExistContract) {
            Map<String, Object> insuredMap = getOrCreateMapParam(fullContractMap, "INSUREDMAP");
            Map<String, Object> insurerMap = getOrCreateMapParam(fullContractMap, "INSURERMAP");
            if (!getStringParam(insuredMap, "THIRDPARTYID").equals(getStringParam(insurerMap, "THIRDPARTYID"))) {
                reportData.put(INSURED_MAP_PARAMNAME, insuredMap);
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);

        return result;
    }

    private void formationServiceMarks(Map<String, Object> params, Map<String, Object> reportData) throws Exception {
            String login = getStringParam(params, LOGIN);
            String password = getStringParam(params, PASSWORD);
            Map<String, Object> formationClientManagerData = new HashMap<>();
            formationClientManagerData.put(REPORT_DATA_PARAM_NAME, reportData);
            formationClientManagerData.put("docReceiptMap", getMapParam(params, "docReceiptMap"));
            formationClientManagerData.put("clientId", getLongParam(params, "clientId"));
            this.callService(B2BPOSWS_SERVICE_NAME, "formationClientManagerData", formationClientManagerData, login, password);
    }
}
