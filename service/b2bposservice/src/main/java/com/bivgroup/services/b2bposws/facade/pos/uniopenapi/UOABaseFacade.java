package com.bivgroup.services.b2bposws.facade.pos.uniopenapi;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UOABaseFacade extends B2BLifeBaseFacade {

    protected List<Map<String, Object>> getAndPrintWithAttachingReportsByRepLevel(Long contractId, Long prodConfId, String repLevelListStr, String login, String password) throws Exception {
        Map<String, Object> printParams = new HashMap<>();
        printParams.put("CONTRID", contractId);
        printParams.put("PRODCONFID", prodConfId);
        printParams.put("REPLEVELLIST", repLevelListStr);
        Map<String, Object> printResult = callServiceLogged(SIGNB2BPOSWS_SERVICE_NAME, "dsB2BGetAndPrintWithAttachingReportsByRepLevel", printParams, login, password);
        List<Map<String, Object>> printResultList = getListFromResultMap(printResult);
        return printResultList;
    }

    protected Long getContractIdByExternalIdFromParams(Map<String, Object> params, StringBuilder error, String login, String password) throws Exception {
        String externalId = getStringParamLogged(params, "EXTERNALID");
        Long contractId = getContractIdByExternalId(externalId, error, password, login);
        return contractId;
    }

    protected Long getContractIdByExternalId(String externalId, StringBuilder error, String password, String login) throws Exception {
        Long contractId = null;
        if (!externalId.isEmpty()) {
            Map<String, Object> contractParams = new HashMap<>();
            contractParams.put("EXTERNALID", externalId);
            contractParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> contract = callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractContrIdByExternalId", contractParams, login, password);
            contractId = getLongParamLogged(contract, "CONTRID");
        }
        if (contractId == null) {
            // todo: тексты ошибок в константы
            error.append("Не удалось получить сведения по договору! ");
        }
        return contractId;
    }

    protected Map<String, Object> getContractExtraBriefByExternalIdFromParams(Map<String, Object> params, StringBuilder error, String login, String password) throws Exception {
        String externalId = getStringParamLogged(params, "EXTERNALID");
        Map<String, Object> contract = getContractExtraBriefByExternalId(externalId, error, login, password);
        return contract;
    }

    protected Map<String, Object> getContractExtraBriefByExternalId(String externalId, StringBuilder error, String login, String password) throws Exception {
        Map<String, Object> contract = null;
        Long contractId = null;
        if (!externalId.isEmpty()) {
            Map<String, Object> contractParams = new HashMap<>();
            contractParams.put("EXTERNALID", externalId);
            contractParams.put(RETURN_AS_HASH_MAP, true);
            contract = callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractExtraBriefByIds", contractParams, login, password);
            contractId = getLongParamLogged(contract, "CONTRID");
        }
        if (contractId == null) {
            // todo: тексты ошибок в константы
            error.append("Не удалось получить сведения по договору! ");
        }
        return contract;
    }

    /** сис. наименование состояния договора */
    protected String getContractStateSysName(Map<String, Object> contract, StringBuilder error) {
        String stateSysName = "";
        if (error.length() == 0) {
            stateSysName = getStringParamLogged(contract, STATE_SYSNAME_PARAMNAME);
            if (stateSysName.isEmpty()) {
                // todo: тексты ошибок в константы
                error.append("Не удалось получить сведения по договору! ");
            }
        }
        return stateSysName;
    }

    // смена состояния договора
    protected Map<String, Object> doContractMakeTrans(Map<String, Object> contract, String toStateSysName, StringBuilder error, String login, String password) throws Exception {
        Map<String, Object> transResult = null;
        if (error.length() == 0) {
            Map<String, Object> transParams = new HashMap<>();
            Long contractId = getLongParam(contract, "CONTRID");
            transParams.put("CONTRID", contractId);
            String stateSysName = getStringParamLogged(contract, "STATESYSNAME");
            transParams.put("STATESYSNAME", stateSysName);
            transParams.put("TOSTATESYSNAME", toStateSysName);
            transParams.put("CONTRMAP", contract);
            transParams.put(RETURN_AS_HASH_MAP, true);
            String methodName = "dsB2BcontractMakeTrans";
            transResult = callServiceLogged(B2BPOSWS_SERVICE_NAME, methodName, transParams, login, password);
            String resultStateSysName = getStringParamLogged(transResult, "STATESYSNAME");
            if (!toStateSysName.equals(resultStateSysName)) {
                logger.error(String.format(
                        "Calling %s (with params = %s) caused error! Details (call result): %s",
                        methodName, transParams, transResult
                ));
                error.append("Не удалось изменить состояние договора! ");
            }
        }
        return transResult;
    }

    /** последовательная смена состояния договора согласно stateSysNameList */
    protected void doContractStateChanges(Map<String, Object> contract, List<String> stateSysNameList, StringBuilder error, String login, String password) throws Exception {
        if ((stateSysNameList == null) || (stateSysNameList.isEmpty())) {
            logger.debug("No contract state changes needed.");
        } else {
            for (String toStateSysName : stateSysNameList) {
                // последовательная смена состояния согласно targetStateSysName (если требуется)
                Map<String, Object> transResult = doContractMakeTrans(contract, toStateSysName, error, login, password);
                if (error.length() == 0) {
                    contract.putAll(transResult);
                } else {
                    break;
                }
            }
        }
    }

}
