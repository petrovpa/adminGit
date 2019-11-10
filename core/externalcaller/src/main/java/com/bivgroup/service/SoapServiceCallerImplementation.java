package com.bivgroup.service;

import com.bivgroup.config.Config;
import com.bivgroup.config.ConfigUtils;
import com.bivgroup.externalcaller.ExternalService;
import com.bivgroup.utils.ParamGetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SoapServiceCallerImplementation extends SoapServiceCaller {
    private final Logger logger;
    private static final String THIS_SERVICE_NAME;

    static {
        THIS_SERVICE_NAME = ConfigUtils.getProjectProperty("ru.diasoft.services.insurance.ServiceName");
    }

    public SoapServiceCallerImplementation() {
        logger = Logger.getLogger(this.getClass());
    }

    public Map<String, Object> callExternalService(String moduleName, String methodName, Map<String, Object> params,
                                                   String login, String password) {
        Map<String, Object> result;
        if (isNeedLogging()) {
            result = this.callExternalServiceLogged(moduleName, methodName, params, login, password);
        } else {
            result = this.callExternalServiceWithoutLogging(moduleName, methodName, params, login, password);
        }
        return result;
    }

    private Map<String, Object> callExternalServiceLogged(String moduleName, String methodName,
                                                          Map<String, Object> params, String login, String password) {
        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        logger.debug("Call method: " + methodName + "  from module: " + moduleName + " with parameters:\n\n" + params.toString() + "\n");
        // вызов действительного метода
        Map<String, Object> callResult = this.callExternalServiceWithoutLogging(moduleName, methodName, params, login, password);
        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        String callResultToStr = callResult == null ? "null" : callResult.toString();
        logger.debug("Method " + methodName + " from module: " + moduleName + " executed in " + callTimer + " ms and returned result:\n\n" + callResultToStr + "\n");
        // возврат результата
        return callResult;
    }

    private Map<String, Object> callExternalServiceWithoutLogging(String moduleName, String methodName, Map<String, Object> params,
                                                                  String login, String password) {
        if (moduleName != null && methodName != null) {
            logger.debug(String.format("Begin callExternalService [%s:%s] on behalf of [%s]...", moduleName, methodName, login));
        } else {
            logger.debug("Begin callExternalService...");
        }
        Map<String, Object> result;
        ExternalService ex = ExternalService.createInstance();
        try {
            result = ex.callExternalService(moduleName, methodName, params, login, password);
            logger.debug("callExternalService result: " + result);
            // анализ результата вызова метода и если имеются ошибки/исключения - сохранение в БД
            analyseResultAndSaveErrorInLog(result, moduleName, methodName, params, login, password);
        } catch (Exception ex1) {
            result = null;
            if (params != null) {
                logger.error(String.format("Error call service [%s:%s] on behalf of [%s] with params [%s]! Details: ",
                        moduleName, methodName, login, params.toString()), ex1);
            } else {
                logger.error(String.format("Error call service [%s:%s] on behalf of [%s] with null params! Details: ",
                        moduleName, methodName, login), ex1);
            }
        }
        logger.debug("End callExternalService.");
        return result;
    }

    // анализ результата вызова метода и если имеются ошибки/исключения - сохранение в БД
    private void analyseResultAndSaveErrorInLog(Map<String, Object> methodCallResult, String moduleName, String methodName,
                                                Map<String, Object> params, String login, String password) {
        // если метод упал - то проверяем и если что сохраняем callstack
        String sessionId = "";
        String contrId = "";
        if (params.get("SESSIONID") != null) {
            //4b2b
            sessionId = params.get("SESSIONID").toString();
        } else if (params.get("sessionToken") != null) {
            String token = params.get("sessionToken").toString();
            sessionId = token;// base64Decode(token);
        } else //4online
            // различные варианты входных данных. мапа договора на сохранение
            if (params.get("CONTRMAP") != null) {
                Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
                if (contrMap.get("sessionToken") != null) {
                    String token = contrMap.get("sessionToken").toString();
                    sessionId = token;//  base64Decode(token);
                }
            } else if (params.get("DATAMAP") != null) {
                Map<String, Object> dataMap = (Map<String, Object>) params.get("DATAMAP");
                if (dataMap.get("contrId") != null) {
                    contrId = dataMap.get("contrId").toString();

                }
            }
        saveErrorInLog(methodCallResult, sessionId, contrId, moduleName, methodName, params, login, password);
    }

    private void saveErrorInLog(Map<String, Object> methodCallResult, String sessionId, String contrId, String serviceName,
                                String methodName, Map<String, Object> params, String login, String password) {
        boolean isError = false;
        if (methodCallResult.get("Status") != null) {
            if ("ERROR".equals(methodCallResult.get("Status").toString())) {
                isError = true;
            }
        }
        if (methodCallResult.get("Status") != null) {
            try {
                if (methodCallResult.get("Status") instanceof Map) {
                    Map<String, Object> res = (Map<String, Object>) methodCallResult.get("Status");
                    if (res.get("Status") != null) {
                        if ("ERROR".equals(res.get("Status").toString())) {
                            isError = true;
                        }
                    }
                }
            } finally {

            }
        }
        if (isError) {
            ObjectMapper mapper = new ObjectMapper();
            String rawres;
            try {
                rawres = mapper.writeValueAsString(methodCallResult);
            } catch (IOException e) {
                logger.error("Unable to serialize method call result object: ", e);
                rawres = String.format("Unable to serialize method call result object: %s", e.toString());
            }

            if (params != null) {
                String rawparams;
                try {
                    rawparams = mapper.writeValueAsString(params);
                } catch (IOException ex) {
                    logger.error("Unable to serialize method call params object: ", ex);
                    rawparams = String.format("Unable to serialize method call params object: %s", ex.toString());
                }
                rawres = "params: " + rawparams + "\\r\\n\\t result:" + rawres;
            }

            Map<String, Object> logParam = new HashMap<>();
            Map<String, Object> objMap = new HashMap<>();
            Map<String, Object> obj = new HashMap<>();

            obj.put("ACTION", "ERROR");
            obj.put("NOTE", "Сохранение текста ошибки");
            if (methodCallResult.get("SESSIONID") != null) {
                obj.put("SESSIONTOKEN", methodCallResult.get("SESSIONID"));
            } else {
                obj.put("SESSIONTOKEN", sessionId);
            }
            obj.put("PARAM1", serviceName);
            obj.put("PARAM2", methodName);
            obj.put("PARAM4", login);
            obj.put("PARAM5", password);
            obj.put("CONTRID", contrId);

            obj.put("RAWDATA", rawres);

            objMap.put("obj", obj);

            logParam.put("OBJMAP", objMap);

            Map<String, Object> res;
            if ("dsAngularUserActionLogCreate".equals(methodName)) {
                String errorText = "Error in method for saving errors to DB itselfs (dsAngularUserActionLogCreate)! Saving this error to DB is skipped to prevent infinitive recursive calling. Error details:\n\n" + logParam + "\n";
                logger.error(errorText);
                res = new HashMap<>();
                res.put("Error", errorText);
            } else {
                res = this.callExternalService("bivsberposws", "dsAngularUserActionLogCreate", logParam, login, password); // dsAngularUserActionLogCreate
            }

            // затираем данные выдающиеся наружу.
            String faultMessage = ParamGetter.getStringParam(methodCallResult, "FaultMessage");
            methodCallResult.clear();
            methodCallResult.putAll(res);
            methodCallResult.put("Status", "ERROR");
            methodCallResult.put("FaultMessage", faultMessage);
        }

    }

    @Override
    public String getLogin() {
        String login;
        Config config = Config.getConfig(THIS_SERVICE_NAME);
        login = config.getParam("DEFAULTLOGIN", "os1");
        return login;
    }

    @Override
    public String getPassword() {
        String password;
        Config config = Config.getConfig(THIS_SERVICE_NAME);
        password = config.getParam("DEFAULTPASSWORD", "356a192b7913b04c54574d18c28d46e6395428ab");
        return password; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isNeedLogging() {
        return logger.isDebugEnabled();
    }
}
