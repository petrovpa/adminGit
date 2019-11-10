package com.bivgroup.service;

import com.bivgroup.config.Config;
import com.bivgroup.config.ConfigUtils;
import com.bivgroup.pojo.JsonResult;
import com.bivgroup.pojo.Obj;
import com.bivgroup.rest.common.AdmSessionController;
import com.bivgroup.service.login.common.service.LoginCommonGate;
import com.bivgroup.sessionutils.SessionController;
import com.bivgroup.utils.RequestWorker;
import com.bivgroup.utils.serviceloader.DefaultServiceLoader;
import org.apache.log4j.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static com.bivgroup.rest.common.Constants.*;
import static com.bivgroup.utils.ParamGetter.*;

public class AdmRestUrlencodedServiceCaller implements UrlencodedServiceCallerInterface {
    private static final String IS_CALL_FROM_GATE_PARAMNAME = "ISCALLFROMGATE";

    private SoapServiceCaller soapServiceCaller;
    private Long sessionTimeOut;
    private RequestWorker requestWorker;
    private Logger logger;
    private Logger auditLogger;
    private Config config;

    public AdmRestUrlencodedServiceCaller() {
        String useServiceName = ConfigUtils.getProjectProperty("ru.diasoft.services.insurance.ServiceName");
        this.config = Config.getConfig(useServiceName);
        this.soapServiceCaller = DefaultServiceLoader.loadServiceAny(SoapServiceCaller.class);
        this.sessionTimeOut = getSessionTimeOut();
        this.auditLogger = Logger.getLogger(config.getParam("auditLoggerName", "audit-appender"));
        this.requestWorker = new RequestWorker();
        this.logger = Logger.getLogger(this.getClass());
    }

    @Override
    public Response callExternalService(@FormParam("params") String paramsStr, String moduleName, String methodName,
                                        String... passedParamNames) {
        Response result;
        if (isNeedLogging()) {
            result = this.callExternalServiceLogged(paramsStr, moduleName, methodName);
        } else {
            result = this.callExternalServiceWithoutLogging(paramsStr, moduleName, methodName);
        }
        return result;
    }

    @Override
    public boolean isNeedLogging() {
        return logger.isDebugEnabled();
    }

    private Response callExternalServiceLogged(String paramsStr, String moduleName, String methodName,
                                               String... passedParamNames) {
        // протоколирование вызова
        long callTimer = System.nanoTime();
        logger.debug("Call method: " + methodName + " from module: " + moduleName + " with parameters:\n\n" + paramsStr + "\n");
        // вызов действительного метода
        Response callResult = this.callExternalServiceWithoutLogging(paramsStr, moduleName, methodName, passedParamNames);
        // протоколирование вызова
        callTimer = System.nanoTime() - callTimer;
        String callResultToStr = callResult == null ? "null" : callResult.toString();
        logger.debug("Method " + methodName + " from module: " + moduleName + " executed in " + callTimer + " ms and returned result:\n\n" + callResultToStr + "\n");
        // возврат результата
        return callResult;
    }

    private Response callExternalServiceWithoutLogging(String paramsStr, String serviceName, String methodName,
                                                       String... passedParamNames) {
        Obj paramsObj = requestWorker.deserializeJSON(paramsStr, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (paramsObj != null) {
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            Map<String, Object> obj = getMapParamName(objMap, "obj");
            String sessionid = removeStringParam(obj, "sessionid");
            SessionController sessionController = new AdmSessionController(this.sessionTimeOut);
            Map<String, Object> sessionCheckResult = sessionController.checkAndCreateSession(sessionid);
            new LoginCommonGate().updateSessionData(sessionCheckResult, this.sessionTimeOut);
            String newSessionID = removeStringParam(sessionCheckResult, AdmSessionController.SESSION_ID_PARAMNAME);
            Map<String, Object> callResult = null;
            Map<String, Object> params = new HashMap<>();
            String error = getStringParam(sessionCheckResult, ERROR);
            if ((error.isEmpty()) && (!serviceName.isEmpty()) && (!methodName.isEmpty())) {
                String sessionLogin = getStringParam(sessionCheckResult, SYSTEM_LOGIN);
                String sessionPassword = getStringParam(sessionCheckResult, SYSTEM_PASS);
                Map<String, Object> checkRightParams = new HashMap<>();
                checkRightParams.put(SYSTEM_LOGIN, sessionLogin);
                checkRightParams.put(RETURN_AS_HASH_MAP, true);
                auditLogger.info("Audit: user with login " + sessionLogin + " try call service for check user rights.");
                Map<String, Object> rightCheckResult = soapServiceCaller.callExternalService(B2BPOSWS, "dsCheckAdminRights",
                        checkRightParams, sessionLogin, sessionPassword);
                if (!getStringParam(rightCheckResult, ERROR).isEmpty()) {
                    error = "Ошибка вызова сервиса проверки прав";
                    auditLogger.info("Audit: user with login " + sessionLogin + " does not have rights to call the service " + methodName);
                }
                if (error.isEmpty() && !getBooleanParam(rightCheckResult, "ACCESS", false)) {
                    error = "У пользователя нет прав на вызов данного (" + methodName + ") сервиса";
                }
                if (error.isEmpty()) {
                    if (passedParamNames.length == 0) {
                        params.putAll(obj);
                    } else {
                        for (String passedParamName : passedParamNames) {
                            params.put(passedParamName, obj.get(passedParamName));
                        }
                    }

                    params.putAll(sessionCheckResult);
                    params.put("SESSIONIDFORCALL", newSessionID);
                    params.put(IS_CALL_FROM_GATE_PARAMNAME, true);
                    auditLogger.info("Audit: user with login " + sessionLogin + " call service " + methodName + " from module " + serviceName);
                    callResult = soapServiceCaller.callExternalService(serviceName, methodName, params);
                    String faultMessage = getStringParam(callResult, "FaultMessage");
                    if (!faultMessage.isEmpty()) {
                        callResult.put(ERROR, faultMessage);
                    }
                } else {
                    callResult = new HashMap<>();
                    callResult.put("Status", ERROR);
                    callResult.put(ERROR, error);
                }
            } else {
                callResult = sessionCheckResult;
            }
            if ((callResult == null) || (callResult.isEmpty())) {
                jsonResult.setResultStatus("Error");
            } else {
                if (!newSessionID.isEmpty()) {
                    callResult.put("SESSIONID", newSessionID);
                }

                requestWorker.serializeJSON(callResult, jsonResult);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }

        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    private Long getSessionTimeOut() {
        Map<String, Object> params = new HashMap<>();
        params.put("SETTINGSYSNAME", "SESSION_SIZE");
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        String sessionSizeStr = (String) this.soapServiceCaller.callExternalService(COREWS, "getSysSettingBySysName", params).get("SETTINGVALUE");
        if (sessionSizeStr == null || sessionSizeStr.isEmpty()) {
            return Long.valueOf(config.getParam("maxSessionSize", "10"));
        } else {
            return Long.parseLong(sessionSizeStr);
        }
    }
}
