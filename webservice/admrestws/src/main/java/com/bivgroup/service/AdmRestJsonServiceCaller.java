package com.bivgroup.service;

import com.bivgroup.config.Config;
import com.bivgroup.config.ConfigUtils;
import com.bivgroup.request.Request;
import com.bivgroup.rest.common.AdmSessionController;
import com.bivgroup.rest.common.RequestValidator;
import com.bivgroup.sessionutils.SessionController;
import com.bivgroup.utils.serviceloader.DefaultServiceLoader;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.bivgroup.rest.common.Constants.*;
import static com.bivgroup.utils.ParamGetter.getBooleanParam;
import static com.bivgroup.utils.ParamGetter.getStringParam;
import static com.fasterxml.jackson.annotation.JsonInclude.Include;

public class AdmRestJsonServiceCaller implements JsonServiceCallerInterface {
    private RequestValidator requestValidator;
    private Long sessionTimeOut;
    private SoapServiceCaller soapServiceCaller;
    private Logger logger;
    private Logger auditLogger;

    public AdmRestJsonServiceCaller() {
        String useServiceName = ConfigUtils.getProjectProperty("ru.diasoft.services.insurance.ServiceName");
        Config config = Config.getConfig(useServiceName);
        this.sessionTimeOut = Long.valueOf(config.getParam("maxSessionSize", "10"));
        this.soapServiceCaller = DefaultServiceLoader.loadServiceAny(SoapServiceCaller.class);
        this.auditLogger = Logger.getLogger(config.getParam("auditLoggerName", "audit-appender"));
        this.requestValidator = new RequestValidator();
        this.logger = Logger.getLogger(this.getClass());
    }

    @Override
    public Map<String, Object> callExternalService(String moduleName, String methodName, Request params, String[] ignorableFieldNames) {
        Map<String, Object> result;
        if (isNeedLogging()) {
            result = this.callExternalServiceLogged(moduleName, methodName, params, ignorableFieldNames);
        } else {
            result = this.callExternalServiceWithoutLogging(moduleName, methodName, params, ignorableFieldNames);
        }
        return result;
    }

    @Override
    public boolean isNeedLogging() {
        return logger.isDebugEnabled();
    }

    private Map<String, Object> callExternalServiceLogged(String moduleName, String methodName,
                                                          Request params, String[] ignorableFieldNames) {
        // протоколирование вызова
        long callTimer = System.nanoTime();
        logger.debug("Call method: " + methodName + " from module: " + moduleName + " with parameters:\n\n" + params.toString() + "\n");
        // вызов действительного метода
        Map<String, Object> callResult = this.callExternalServiceWithoutLogging(moduleName, methodName, params, ignorableFieldNames);
        // протоколирование вызова
        callTimer = System.nanoTime() - callTimer;
        String callResultToStr = callResult == null ? "null" : callResult.toString();
        logger.debug("Method " + methodName + " from module: " + moduleName + " executed in " + callTimer + " ms and returned result:\n\n" + callResultToStr + "\n");
        // возврат результата
        return callResult;
    }

    private Map<String, Object> callExternalServiceWithoutLogging(String moduleName, String methodName, Request params, String[] ignorableFieldNames) {
        SessionController sessionController = new AdmSessionController(this.sessionTimeOut);
        Map<String, Object> sessionCheckResult = sessionController.checkAndCreateSession(params.getSessionId());
        String error = getStringParam(sessionCheckResult, ERROR);

        if (error.isEmpty()) {
            error = requestValidator.validateResponse(params);
        }

        String sessionLogin = "";
        String sessionPass = "";
        if (error.isEmpty()) {
            sessionLogin = getStringParam(sessionCheckResult, SYSTEM_LOGIN);
            sessionPass = getStringParam(sessionCheckResult, SYSTEM_PASS);

            Map<String, Object> checkRightParams = new HashMap<>();
            checkRightParams.put(SYSTEM_LOGIN, sessionLogin);
            checkRightParams.put(RETURN_AS_HASH_MAP, true);
            auditLogger.info("Audit: user with login " + sessionLogin + " try call service for check user rights.");
            Map<String, Object> rightCheckResult = soapServiceCaller.callExternalService(B2BPOSWS, "dsCheckAdminRights",
                    checkRightParams, sessionLogin, sessionPass);
            if (!getStringParam(rightCheckResult, ERROR).isEmpty()) {
                error = "Ошибка вызова сервиса проверки прав";
            }
            if (error.isEmpty() && !getBooleanParam(rightCheckResult, "ACCESS", false)) {
                error = "У пользователя нет прав на вызов данного (" + methodName + ") сервиса";
                auditLogger.info("Audit: user with login " + sessionLogin + " does not have rights to call the service " + methodName);
            }
        }

        String newSession = getStringParam(sessionCheckResult, AdmSessionController.SESSION_ID_PARAMNAME);
        Map<String, Object> callResult = new HashMap<>();
        if (error.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(Include.NON_NULL);
            FilterProvider filters = new SimpleFilterProvider().addFilter("ignorableFilter",
                    SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames));
            mapper.setFilters(filters);
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
            Map<String, Object> admParams = mapper.convertValue(params.getParams(), typeRef);
            admParams.put("DEPRIGHT", sessionCheckResult.get(SESSIONPARAM_DEPARTMENTID));
            admParams.put("SESSIONIDFORCALL", newSession);
            auditLogger.info("Audit: user with login " + sessionLogin + " call service " + methodName + " from module " + moduleName);
            callResult = soapServiceCaller.callExternalService(moduleName, methodName, admParams, sessionLogin, sessionPass);
            error = getStringParam(callResult, "FaultMessage");
        }

        if (!error.isEmpty()) {
            callResult.put("admCallError", error);
            callResult.put("Status", "ERROR");
        } else {
            if (!newSession.isEmpty()) {
                callResult.put("SESSIONID", newSession);
            }
        }

        return callResult;
    }
}
