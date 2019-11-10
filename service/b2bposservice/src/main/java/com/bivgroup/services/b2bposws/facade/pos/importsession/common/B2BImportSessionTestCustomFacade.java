package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

// тестовый фасад
// todo: отключить по завершению разработки
@BOName("B2BImportSessionTestCustom")
public class B2BImportSessionTestCustomFacade extends B2BImportSessionBaseFacade {

    // работа в режиме отладки (разрешен вызов тестового метода и др.)
    // todo: отключить по завершению разработки
    protected boolean isImportInDebugMode = false;
    private static final String DEBUG_MODE_CONFIG_PARAMNAME = "DEBUGMODE";

    public B2BImportSessionTestCustomFacade() {
        super();
        logger.debug("B2BImportSessionTestCustomFacade init...");
        Config config = Config.getConfig(THIS_SERVICE_NAME);
        try {
            // todo: отключить по завершению разработки
            String debugModeParamValue = config.getParam(DEBUG_MODE_CONFIG_PARAMNAME, "0");
            logger.debug(DEBUG_MODE_CONFIG_PARAMNAME + " = " + debugModeParamValue);
            isImportInDebugMode = (debugModeParamValue != null) && ((debugModeParamValue.equals("1")) || (debugModeParamValue.equalsIgnoreCase("TRUE")) || (debugModeParamValue.equalsIgnoreCase("YES")));
        } catch (Exception ex) {
            isImportInDebugMode = false;
        }
        logger.debug("isImportInDebugMode = " + isImportInDebugMode);
    }
    
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BImportSessionTestAnyMethod(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BImportSessionTestAnyMethod...");
        loggerDebugPretty(logger, "dsB2BImportSessionTestAnyMethod params", params);
        Map<String, Object> result;
        if (isImportInDebugMode) {
            String login = getStringParam(params, LOGIN);
            String password = getStringParam(params, PASSWORD);
            String methodName = getStringParamLogged(params, "method");
            String serviceName = getStringParamLogged(params, "service");
            if (serviceName.isEmpty()) {
                serviceName = THIS_SERVICE_NAME;
            }
            Map<String, Object> methodParams = new HashMap<>();
            methodParams.putAll(params);
            methodParams.putAll(getOrCreateMapParamLogged(params, "params"));
            result = this.callService(serviceName, methodName, methodParams, login, password);
        } else {
            result = new HashMap<>();
        }
        loggerDebugPretty(logger, "dsB2BImportSessionTestAnyMethod result", result);
        logger.debug("dsB2BImportSessionTestAnyMethod finished.");
        return result;
    }

}
