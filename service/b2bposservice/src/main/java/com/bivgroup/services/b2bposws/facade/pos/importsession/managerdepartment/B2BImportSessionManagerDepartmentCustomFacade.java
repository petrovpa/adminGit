package com.bivgroup.services.b2bposws.facade.pos.importsession.managerdepartment;

import com.bivgroup.services.b2bposws.facade.pos.importsession.common.B2BImportSessionBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BImportSessionManagerDepartmentCustom")
public class B2BImportSessionManagerDepartmentCustomFacade extends B2BImportSessionBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BImportSessionManagerDepartmentCreate(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerDepartmentCreate...");
        // loggerDebugPretty(logger, "dsB2BImportSessionManagerDepartmentCreate params", params);
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> importSessionManagerDepartment = new HashMap<>();
        genSystemAttributesWithoutAspect(importSessionManagerDepartment, params);
        processImportSessionLastFlag(IMPORT_SESSION_MANAGER_DEPARTMENT_ENTITY_NAME, importSessionManagerDepartment, params);
        Map<String, Object> result = dctCrudByHierarchy(IMPORT_SESSION_MANAGER_DEPARTMENT_ENTITY_NAME, importSessionManagerDepartment);
        if (isCallFromGate) {
            // преобразование всех дат в String для возврата в интерфейс после выполнения операций через словарную систему
            parseDatesAfterDictionaryCalls(result);
        }
        // loggerDebugPretty(logger, "dsB2BImportSessionManagerDepartmentCreate result", result);
        logger.debug("dsB2BImportSessionManagerDepartmentCreate finished.");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BImportSessionManagerDepartmentGetBinaryFileInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionManagerDepartmentGetBinaryFileInfo...");
        // loggerDebugPretty(logger, "dsB2BImportSessionManagerDepartmentGetBinaryFileInfo params", params);
        boolean isCallFromGate = isCallFromGate(params);
        Long id = getLongParam(params, "id");
        List<Map<String, Object>> resultList = dctGetBinaryFileInfo(IMPORT_SESSION_MANAGER_DEPARTMENT_ENTITY_NAME, id, isCallFromGate);
        if (isCallFromGate) {
            // преобразование всех путей в ссылки для возврата в интерфейс после выполнения операций через словарную систему
            String login = getStringParam(params, LOGIN);
            String password = getStringParam(params, PASSWORD);
            processDocListForUpload(resultList, params, login, password);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, resultList);
        // loggerDebugPretty(logger, "dsB2BImportSessionManagerDepartmentGetBinaryFileInfo result", result);
        logger.debug("dsB2BImportSessionManagerDepartmentGetBinaryFileInfo finished.");
        return result;
    }

}
