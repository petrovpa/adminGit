package com.bivgroup.services.b2bposws.facade.pos.importsession.department;

import com.bivgroup.services.b2bposws.facade.pos.importsession.common.B2BImportSessionBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BImportSessionDepartmentCustom")
public class B2BImportSessionDepartmentCustomFacade extends B2BImportSessionBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BImportSessionDepartmentCreate(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionDepartmentCreate...");
        // loggerDebugPretty(logger, "dsB2BImportSessionDepartmentCreate params", params);
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> importSessionDepartment = new HashMap<>();
        genSystemAttributesWithoutAspect(importSessionDepartment, params);
        processImportSessionLastFlag(IMPORT_SESSION_DEPARTMENT_ENTITY_NAME, importSessionDepartment, params);
        Map<String, Object> result = dctCrudByHierarchy(IMPORT_SESSION_DEPARTMENT_ENTITY_NAME, importSessionDepartment);
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        // loggerDebugPretty(logger, "dsB2BImportSessionDepartmentCreate result", result);
        logger.debug("dsB2BImportSessionDepartmentCreate finished.");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BImportSessionDepartmentGetBinaryFileInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionDepartmentGetBinaryFileInfo...");
        // loggerDebugPretty(logger, "dsB2BImportSessionDepartmentGetBinaryFileInfo params", params);
        boolean isCallFromGate = isCallFromGate(params);
        Long id = getLongParam(params, "id");
        List<Map<String, Object>> resultList = dctGetBinaryFileInfo(IMPORT_SESSION_DEPARTMENT_ENTITY_NAME, id, isCallFromGate);
        if (isCallFromGate) {
            // преобразование всех путей в ссылки для возврата в интерфейс после выполнения операций через словарную систему
            String login = getStringParam(params, LOGIN);
            String password = getStringParam(params, PASSWORD);
            processDocListForUpload(resultList, params, login, password);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, resultList);
        // loggerDebugPretty(logger, "dsB2BImportSessionDepartmentGetBinaryFileInfo result", result);
        logger.debug("dsB2BImportSessionDepartmentGetBinaryFileInfo finished.");
        return result;
    }

}
