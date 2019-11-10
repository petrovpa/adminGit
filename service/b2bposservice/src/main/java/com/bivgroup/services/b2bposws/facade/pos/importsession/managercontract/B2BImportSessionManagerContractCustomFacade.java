package com.bivgroup.services.b2bposws.facade.pos.importsession.managercontract;

import com.bivgroup.services.b2bposws.facade.pos.importsession.common.B2BImportSessionBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BImportSessionManagerContractCustom")
public class B2BImportSessionManagerContractCustomFacade extends B2BImportSessionBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BImportSessionManagerContractCreate(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerContractCreate...");
        // loggerDebugPretty(logger, "dsB2BImportSessionManagerContractCreate params", params);
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> importSessionManagerContract = new HashMap<>();
        genSystemAttributesWithoutAspect(importSessionManagerContract, params);
        processImportSessionLastFlag(IMPORT_SESSION_MANAGER_CONTRACT_ENTITY_NAME, importSessionManagerContract, params);
        Map<String, Object> result = dctCrudByHierarchy(IMPORT_SESSION_MANAGER_CONTRACT_ENTITY_NAME, importSessionManagerContract);
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        // loggerDebugPretty(logger, "dsB2BImportSessionManagerContractCreate result", result);
        logger.debug("dsB2BImportSessionManagerContractCreate finished.");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BImportSessionManagerContractGetBinaryFileInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerContractGetBinaryFileInfo...");
        // loggerDebugPretty(logger, "dsB2BImportSessionManagerContractGetBinaryFileInfo params", params);
        boolean isCallFromGate = isCallFromGate(params);
        Long id = getLongParam(params, "id");
        List<Map<String, Object>> resultList = dctGetBinaryFileInfo(IMPORT_SESSION_MANAGER_CONTRACT_ENTITY_NAME, id, isCallFromGate);
        if (isCallFromGate) {
            // преобразование всех путей в ссылки для возврата в интерфейс после выполнения операций через словарную систему
            String login = getStringParam(params, LOGIN);
            String password = getStringParam(params, PASSWORD);
            processDocListForUpload(resultList, params, login, password);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, resultList);
        // loggerDebugPretty(logger, "dsB2BImportSessionManagerContractGetBinaryFileInfo result", result);
        logger.debug("dsB2BImportSessionManagerContractGetBinaryFileInfo finished.");
        return result;
    }

}
