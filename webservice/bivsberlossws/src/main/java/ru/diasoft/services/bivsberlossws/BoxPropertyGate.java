package ru.diasoft.services.bivsberlossws;

import com.bivgroup.chainproxy.ChainProxyRunner;
import com.bivgroup.chainproxy.executertype.TargetParam;
import com.bivgroup.chainproxy.targetparam.BaseTargetParam;
import com.bivgroup.core.audit.Audit;
import com.bivgroup.core.audit.annotation.AuditBean;
import com.bivgroup.password.PasswordStrengthVerifier;
import com.bivgroup.pojo.JsonResult;
import com.bivgroup.pojo.Obj;
import com.bivgroup.seaweedfs.client.*;
import com.bivgroup.service.login.common.service.LoginCommonGate;
import com.bivgroup.sessionutils.SessionController;
import com.bivgroup.sessionutils.SessionUtilException;
import com.bivgroup.sessionutils.cdi.CounterCache;
import com.bivgroup.sessionutils.cdi.CounterCacheFactory;
import com.bivgroup.sessionutils.cdi.CounterCacheImpl;
import com.bivgroup.sessionutils.cdi.SessionCounter;
import com.bivgroup.utils.ParamGetter;
import com.bivgroup.utils.RequestWorker;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.SingleBody;
import org.apache.log4j.Logger;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.external.ExternalService;
import ru.diasoft.services.utils.Constants;
import ru.diasoft.utils.XMLUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

import static com.bivgroup.chainproxy.enums.TargetType.EXECUTOR_OIS_ERROR_CALLRESULT_TYPE;
import static com.bivgroup.chainproxy.enums.TargetType.EXECUTOR_OIS_RESULTPARAMM_CANT_BE_EMPTY_TYPE;
import static com.bivgroup.password.PasswordStrengthVerifier.*;
import static com.bivgroup.sessionutils.BaseSessionController.getErrorMap;
import static com.bivgroup.utils.ParamGetter.*;
import static ru.diasoft.services.bivsberlossws.B2BSessionController.B2B_DEPARTMENTID_PARAMNAME;
import static ru.diasoft.services.bivsberlossws.B2BSessionController.B2B_USERGROUPS_PARAMNAME;
import static ru.diasoft.services.inscore.system.WsConstants.*;
import static ru.diasoft.services.utils.Constants.*;

@Path("/rest/boxproperty-gate")
public class BoxPropertyGate {
    private static final String[] URLFEEDBACKPARAMNAMES = {"CREATEDATE", "CONTRID", "FEEDBACKTYPE"};
    public static final String SESSION_INACTIVE_ERROR = "Сессия более недействительна";

    private Long sessionTimeOut = 10L;
    // url timeout in minuten
    private Long urlTimeOut = 2880L;
    private RequestWorker requestWorker;

    private static final String SERVER_UPLOAD_LOCATION_FOLDER = "C://Diasoft/UPLOAD";

    private Logger logger = Logger.getLogger(BoxPropertyGate.class);
    private static final String AUDIT_LOGGERNAME = "AUDIT";
    private Logger auditLogger = Logger.getLogger(AUDIT_LOGGERNAME);

    // системные имена продуктов
    private static final String SYSNAME_VZR = "00018"; // по ФТ от 06.10.2015
    private static final String SYSNAME_GAP = "00034"; // по ФТ от 17.09.2015
    private static final String SYSNAME_MULTI = "00035"; // по ФТ от 17.09.2015
    private static final String SYSNAME_SIS = "00039"; // Защита всегда рядом: Дом / Страхование имущества сотрудников СБ
    private static final String SYSNAME_ANTIMITE = "B2B_ANTIMITE"; // Защита от клеща Онлайн
    private static final String SYSNAME_BUSINESS_STAB = "B2B_BUSINESS_STAB";

    // флаг подробного протоколирования вызовов через callExternalService
    // (после завершения отладки передачи параметров рекомендуется отключить)
    private boolean isVerboseLogging = logger.isDebugEnabled();

    // имя параметра управляющего необходимостью прикрепления загружаемых файлов к сущностям в БД
    // (пока используется только в /b2bfileuploadex)
    private static final String UPLOAD_SKIP_DB_ATTACH_PARAM_NAME = "SKIPATTACH";
    // имя параметра, управляющего необходимостью создания Doc сущности для /b2bfileuploadex (когда саму Doc сущность не нужно создавать,
    // но необходимо создать BinaryInfo)
    private static final String UPLOAD_SKIP_CREATE_DBDOC = "SKIPDBDOC";
    // имя параметра, содержащего подкаталог (опционально) для сохранения загружаемых файлов
    // (пока используется только в /b2bfileuploadex)
    private static final String UPLOAD_SUBFOLDER_PARAM_NAME = "SUBFOLDER";

    // имя параметра с признаком выполнения вызова из гейта
    private static final String IS_CALL_FROM_GATE_PARAMNAME = "ISCALLFROMGATE";
    // имя параметра идентификатора сессии
    private static final String SESSION_ID_PARAMNAME = B2BSessionController.SESSION_ID_PARAMNAME;
    private final PasswordStrengthVerifier verifier;
    private static final Map<String, List<String>> ROLE_AVAILABLE_METHODS;
    private static final String ADMIN_OPERATOR_KC_USER_ROLE = "adminOperatorKC";
    private static final String ADMIN_OPERATOR_UKS_USER_ROLE = "adminOperatorUKS";
    private static final String ADMIN_OPERATOR_LK_USER_ROLE = "adminOperatorLK";
    private SessionCounter sc = null;

    static {
        ROLE_AVAILABLE_METHODS = new HashMap<>();

        // Оператор КЦ
        ROLE_AVAILABLE_METHODS.put(ADMIN_OPERATOR_KC_USER_ROLE, Arrays.asList(
                "dsB2BClientProfileClientSave"
        ));
        // Сотрудник УКС
        ROLE_AVAILABLE_METHODS.put(ADMIN_OPERATOR_UKS_USER_ROLE, Arrays.asList(
                "dsB2BClientProfileClientSave"
        ));
        // Администратор
//        ROLE_AVAILABLE_METHODS.put(ADMIN_OPERATOR_LK_USER_ROLE, Arrays.asList(
//
//        ));
    }

    public BoxPropertyGate() {
        super();
        init();
        this.verifier = createPasswordVerifier();
    }

    private PasswordStrengthVerifier createPasswordVerifier() {
        try {
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> passwordSetting = this.callExternalService(ADMINWS, "getPwdAcctSettings", params);
            int minLen = getIntegerParam(passwordSetting, PWD_MIN_LEN);
            if (minLen == 0) {
                minLen = 6;
            }
            int maxLen = getIntegerParam(passwordSetting, PWD_MAX_LEN);
            if (maxLen == 0) {
                maxLen = 40;
            }
            int groupsCount = getIntegerParam(passwordSetting, PWD_GROUPS_COUNT);
            if (groupsCount == 0) {
                groupsCount = 40;
            }
            int idtSymCount = getIntegerParam(passwordSetting, PWD_IDT_SYM_COUNT);
            if (idtSymCount == 0) {
                idtSymCount = 40;
            }
            boolean additionalAllowed = getBooleanParam(passwordSetting, PWD_GROUPS_ADDITIONAL_ALLOWED, false);
            return new PasswordStrengthVerifier(minLen, maxLen, idtSymCount, groupsCount,
                    additionalAllowed ? PasswordStrengthVerifier.SET_ADDITIONAL
                            : PasswordStrengthVerifier.SET_ASCII_ALPHANUM);
        } catch (Exception ex) {
            logger.error("Error while creating PasswordStrengthVerifier: " + ex.getMessage(), ex);
            return new PasswordStrengthVerifier(6, 40, 2, 3, PasswordStrengthVerifier.SET_ADDITIONAL);
        }
    }

    private void init() {
        Config config = Config.getConfig(SERVICE_NAME);
        sessionTimeOut = getSessionTimeOut();
        urlTimeOut = Long.valueOf(config.getParam("urlTimeOut", "2880"));
        this.requestWorker = new RequestWorker(this.getClass());
        if (sc == null) {
            CounterCacheFactory ccf = new CounterCacheFactory();
            CounterCache cm = ccf.getCacheManager(CounterCacheImpl.class);
            sc = cm.getSessionCounter();
        }
        if (!sc.isBuilt()) {
            String consulUri = config.getParam("CONSULAPPLISTSOURCE", "");
            String adminGroupSysname = config.getParam("ADMINGROUPSYSNAME", "");
            long maxSessionCountForAdmin = Long.parseLong(config.getParam("ADMINGROUPMAXSESSIONCNT", "5"));
            long maxSessionCount = Long.parseLong(config.getParam("NOTADMINGROUPMAXSESSIONCNT", "5"));
            sc
                    .setConsuURI(consulUri)
                    .setUsernameParamname(SYSTEM_PARAM_NAME_LOGIN)
                    .setSessionTimeOut(sessionTimeOut)
                    .setAdminGroupSysname(adminGroupSysname)
                    .setMaxSessionsCountForAdmin(maxSessionCountForAdmin)
                    .setMaxSessionsCount(maxSessionCount)
                    .build();
        }
    }

    /**
     * Получение таймаута сессии из БД или конфига
     *
     * @return
     */
    protected Long getSessionTimeOut() {
        String sessionSizeStr = (String) this.callExternalService(WsConstants.COREWS,
                "getSysSettingBySysName", new HashMap<String, Object>() {{
                    put("SETTINGSYSNAME", "SESSION_SIZE");
                    put(RETURN_AS_HASH_MAP, "TRUE");
                }}).get("SETTINGVALUE");
        if (sessionSizeStr.isEmpty()) {
            return Long.valueOf(Config.getConfig(SERVICE_NAME).getParam("maxSessionSize", "10"));
        } else {
            return Long.parseLong(sessionSizeStr);
        }
    }

    protected String getLogin() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("DEFAULTLOGIN", "os1");
        return login;
    }

    protected String getUploadPath() {
        String path;
        Config config = Config.getConfig(SERVICE_NAME);
        path = config.getParam("userFilePath", SERVER_UPLOAD_LOCATION_FOLDER);
        return path;
    }

    protected String getPassword() {
        String password;
        Config config = Config.getConfig(SERVICE_NAME);
        password = config.getParam("DEFAULTPASSWORD", "356a192b7913b04c54574d18c28d46e6395428ab");
        return password; //To change body of generated methods, choose Tools | Templates.
    }

    protected String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }

    protected String getSeaweedFSUrl() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("SEAWEEDFSURL", "");
        return login;
    }

    private void saveErrorInLog(Map<String, Object> methodCallResult, String sessionId, String contrId, String serviceName, String methodName, Map<String, Object> params, String login, String password) {
        boolean isError = false;
        if (methodCallResult.get(STATUS) != null) {
            if (UPPERCASE_ERROR.equals(methodCallResult.get(STATUS).toString())) {
                isError = true;
            }
        }
        if (methodCallResult.get(Constants.RESULT) != null) {
            try {
                if (methodCallResult.get(Constants.RESULT) instanceof Map) {
                    Map<String, Object> res = getMapParamName(methodCallResult, Constants.RESULT);
                    if (res.get(STATUS) != null) {
                        if (UPPERCASE_ERROR.equals(res.get(STATUS).toString())) {
                            isError = true;
                        }
                    }
                }
            } finally {

            }
        }
        if (isError) {
            JsonResult result = new JsonResult();
            this.requestWorker.serializeJSON(methodCallResult, result);
            String rawres = result.getResultJson();

            Map<String, Object> logParam = new HashMap<>();
            Map<String, Object> objMap = new HashMap<>();
            Map<String, Object> obj = new HashMap<>();

            obj.put("ACTION", UPPERCASE_ERROR);
            obj.put("NOTE", "Сохранение текста ошибки");
            if (methodCallResult.get(SESSION_ID_PARAMNAME) != null) {
                obj.put("SESSIONTOKEN", methodCallResult.get(SESSION_ID_PARAMNAME));
            } else {
                obj.put("SESSIONTOKEN", sessionId);
            }
            // obj.put("VALUE", obj);
            obj.put("PARAM1", serviceName);
            obj.put("PARAM2", methodName);
            obj.put("PARAM4", login);
            obj.put("PARAM5", password);
            obj.put("CONTRID", contrId);
            // obj.put("PARAM3", obj);
            JsonResult resParamLog = new JsonResult();
            if (params != null) {
                this.requestWorker.serializeJSON(params, resParamLog);
                rawres = "params: " + resParamLog.getResultJson() + "\\r\\n\\t result:" + rawres;
            }

            obj.put("RAWDATA", rawres);

            objMap.put("obj", obj);

            logParam.put("OBJMAP", objMap);

            Map<String, Object> res;
            if ("dsAngularUserActionLogCreate".equals(methodName)) {
                String errorText = "Error in method for saving errors to DB itselfs (dsAngularUserActionLogCreate)! Saving this error to DB is skipped to prevent infinitive recursive calling. Error details:\n\n" + logParam + "\n";
                logger.error(errorText);
                res = new HashMap<>();
                res.put(ERROR, errorText);
            } else {
                res = this.callExternalService(BIVSBERPOSWS, "dsAngularUserActionLogCreate", logParam); // dsAngularUserActionLogCreate
            }

            JsonResult resLog = new JsonResult();
            this.requestWorker.serializeJSON(res, resLog);
            result.setResultJson(resLog.getResultJson());
            result.setResultStatus(JsonResult.RESULT_ERROR);

            // протоколирование ошибки в стандартном протоколе
            logger.error(String.format(
                    "Error call service [%s:%s] on behalf of [%s] with params [%s]! Details (call result): %s.",
                    serviceName, methodName, login, params, methodCallResult
            ));

            // затираем данные выдающиеся наружу.
            methodCallResult.clear();
            methodCallResult.putAll(res);
            methodCallResult.put(STATUS, UPPERCASE_ERROR);
        }

    }

    private boolean fileSessionIsValid(String sessionId) {
        SessionController controller = new B2BFIleSessionController(this.sessionTimeOut);
        Map<String, Object> sessionParams = controller.checkSession(sessionId);
        return !B2BFIleSessionController.sessionWithError(sessionParams);
    }

    private Response dsAdminCallService(@FormParam(FORM_PARAM_NAME) String paramsStr, String serviceName, String methodName, String... passedParamNames) {
        Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (paramsObj != null) {
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            Map<String, Object> obj = getMapParamName(objMap, "obj");
            String sessionid = getStringParam(obj.remove(SESSION_ID_LOWERCASE_PARAM_NAME));
            Map<String, Object> sessionParams = checkB2BSession(sessionid);
            String newSessionID = getStringParam(sessionParams.remove(SESSION_ID_PARAMNAME));
            Map<String, Object> сallResult;
            Map<String, Object> params = new HashMap<>();
            if ((sessionParams.get(ERROR) == null) && (!serviceName.isEmpty()) && (!methodName.isEmpty())) {
                if (passedParamNames.length == 0) {
                    params.putAll(obj);
                } else {
                    for (String passedParamName : passedParamNames) {
                        params.put(passedParamName, obj.get(passedParamName));
                    }
                }
                params.putAll(sessionParams);
                params.put("SESSIONIDFORCALL", newSessionID);

                Map<String, Object> qRightParams = new HashMap<>(sessionParams);
                Map<String, Object> qRightResult = this.callExternalService(B2BPOSWS, "dsCheckAdminRights", qRightParams, isVerboseLogging);
                if ((null != qRightResult) && (null != qRightResult.get(Constants.RESULT))) {
                    Map<String, Object> rightMap = getMapParamName(qRightResult, Constants.RESULT);
                    if ((Boolean.valueOf(rightMap.get("ACCESS").toString()))) {
                        params.put("DEPRIGHT", params.get(B2B_DEPARTMENTID_PARAMNAME));
                        сallResult = this.callExternalService(serviceName, methodName, params, isVerboseLogging);
                    } else {
                        сallResult = sessionParams;
                        сallResult.put("ERRORMESSAGE", "Access denied");
                    }

                } else {
                    сallResult = sessionParams;
                    сallResult.put("ERRORMESSAGE", "Access denied");
                }
            } else {
                сallResult = sessionParams;
            }
            if ((сallResult == null) || (сallResult.isEmpty())) {
                jsonResult.setResultStatus(ERROR);
            } else {
                if (!newSessionID.isEmpty()) {
                    сallResult.put(SESSION_ID_PARAMNAME, newSessionID);
                }

                this.requestWorker.serializeJSON(сallResult, jsonResult);

            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    private Map<String, Object> callExternalService(String moduleName, String methodName, Map<String, Object> params) {
        String login = getStringParam(params.remove(LOGIN));
        String password = getStringParam(params.remove(PASSWORD));
        if ((login.isEmpty()) || (password.isEmpty())) {
            login = getLogin();
            password = getPassword();
        }
        return callExternalService(moduleName, methodName, params, login, password);
    }

    private Map<String, Object> callExternalService(String moduleName, String methodName, Map<String, Object> params, String login, String password) {
        Map<String, Object> result = null;

        if (moduleName != null && methodName != null) {
            logger.debug(String.format("Begin callExternalService [%s:%s] on behalf of [%s]...", moduleName, methodName, login));
        }

        logger.debug("Begin callExternalService...");

        ExternalService ex = ExternalService.createInstance();
        try {
            result = ex.callExternalService(moduleName, methodName, params, login, password);
            logger.debug("callExternalService result: " + result);
        } catch (Exception ex1) {
            result = null;
            if (params != null) {
                logger.error(String.format("Error call service [%s:%s] on behalf of [%s] with params [%s]! Details: ", moduleName, methodName, login, params), ex1);
            } else {
                logger.error(String.format("Error call service [%s:%s] on behalf of [%s] with null params! Details: ", moduleName, methodName, login), ex1);
            }
        } finally {
            // анализ результата вызова метода и если имеются ошибки/исключения - сохранение в БД
            analyseResultAndSaveErrorInLog(result, moduleName, methodName, params, login, password);
        }
        logger.debug("End callExternalService.");
        return result;
    }

    // анализ результата вызова метода и если имеются ошибки/исключения - сохранение в БД
    private void analyseResultAndSaveErrorInLog(Map<String, Object> methodCallResult, String moduleName, String methodName, Map<String, Object> params, String login, String password) {
        // Обработка типа ошибки (в наших сервисах, или при вызове интеграции)
        try {
            TargetParam targetParam = prepareCustomTargetParam();

            ChainProxyRunner.createInstance().execute(B2BPOSWS, methodName, methodCallResult,
                    Arrays.asList(
                            EXECUTOR_OIS_ERROR_CALLRESULT_TYPE,
                            EXECUTOR_OIS_RESULTPARAMM_CANT_BE_EMPTY_TYPE
                    ), targetParam);

        } catch (Exception e) {
            logger.error(String.format("Error call service [%s:%s] on behalf of [%s] with params [%s]! Details: ", moduleName, methodName, login, params), e);
        } finally {
            String sessionId = "";
            String contrId = "";
            if (params.get(SESSION_ID_LOWERCASE_PARAM_NAME) != null) {
                //4b2b
                sessionId = params.get(SESSION_ID_LOWERCASE_PARAM_NAME).toString();
            } else if (params.get(SESSION_TOKEN_PARAM_NAME) != null) {
                String token = params.get(SESSION_TOKEN_PARAM_NAME).toString();
                sessionId = token;// base64Decode(token);
            } else {
                //4online
                // различные варианты входных данных. мапа договора на сохранение
                if (params.get("CONTRMAP") != null) {
                    Map<String, Object> contrMap = getMapParamName(params, "CONTRMAP");
                    if (contrMap.get(SESSION_TOKEN_PARAM_NAME) != null) {
                        String token = contrMap.get(SESSION_TOKEN_PARAM_NAME).toString();
                        sessionId = token;//  base64Decode(token);
                    }
                } else if (params.get("DATAMAP") != null) {
                    Map<String, Object> dataMap = getMapParamName(params, "DATAMAP");
                    if (dataMap.get("contrId") != null) {
                        contrId = dataMap.get("contrId").toString();

                    }
                }
                saveErrorInLog(methodCallResult, sessionId, contrId, moduleName, methodName, params, login, password);
            }
        }
    }

    /**
     * Функция подготовки параметров для исполнителей
     *
     * @return возвращает список параметров по типам
     */
    private TargetParam prepareCustomTargetParam() {
        BaseTargetParam targetParam = new BaseTargetParam();
        targetParam.addParamNameByTargetType(EXECUTOR_OIS_RESULTPARAMM_CANT_BE_EMPTY_TYPE, "CONTRMAP");
        return targetParam;
    }

    private Map<String, Object> callExternalService(String serviceName, String
            methodName, Map<String, Object> params, boolean isCallLogged) {
        if (isCallLogged) {
            return this.callExternalServiceLogged(serviceName, methodName, params);
        } else {
            return this.callExternalService(serviceName, methodName, params);
        }
    }

    private Map<String, Object> callExternalServiceLogged(String serviceName, String
            methodName, Map<String, Object> params) {
        // протоколирование вызова
        long callTimer = System.currentTimeMillis();
        logger.debug(String.format("Вызван метод [%s] с параметрами:%n%n [%s] %n", methodName, params));
        // вызов действительного метода
        Map<String, Object> callResult = this.callExternalService(serviceName, methodName, params);
        // протоколирование вызова
        callTimer = System.currentTimeMillis() - callTimer;
        logger.debug(String.format("Метод [%s] выполнился за [%s] мс. и вернул результат:%n%n [%s] %n", methodName, callTimer, callResult));
        // возврат результата
        return callResult;
    }

    private void removeKeysFromAllMaps(Object obj, String[] keyNames) {
        if (obj == null) {
            return;
        }
        if (obj instanceof List) {
            List<Object> list = (List<Object>) obj;
            for (Object bean : list) {
                removeKeysFromAllMaps(bean, keyNames);
            }
        }
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            for (String name : keyNames) {
                if (map.containsKey(name)) {
                    map.remove(name);
                }
            }
            for (String key : map.keySet()) {
                removeKeysFromAllMaps(map.get(key), keyNames);
            }
        }
    }

    private void clearKeysForOnlineMethods(Map<String, Object> dataMap) {
        String[] keys = {"ROWSTATUS", "INSURERID", "INSURERREPID", "MEMBERID"/*, "CONTRID"*/};
        removeKeysFromAllMaps(dataMap, keys);
    }

    @POST
    //@Path("/users/{id}")
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(/*@PathParam("id")*/@FormParam("id") Integer id) {
        User user = new User();
        user.setId(id);
        user.setFirstName("Lokesh");
        user.setLastName("Gupta");
        return Response.status(200).entity(user).build();
    }

    @POST
    @Path("/dsCountryBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCountryBrowseListByParam() {
        Map<String, Object> params = new HashMap<>();
        params.put("ISNOTUSE", "0");
        params.put("ORDERBY", "COUNTRYID");
        Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BCountryBrowseListByParam", params);
        JsonResult jsonResult = new JsonResult();

        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
        } else {
            jsonResult.setResultStatus(ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsKladrRegionBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsKladrRegionBrowseListByParam() {
        Map<String, Object> params = new HashMap<>();
        params.put("SETMOSCOWFIRST", "TRUE");

        Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsKladrRegionBrowseListByParam", params);
        JsonResult jsonResult = new JsonResult();

        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
        } else {
            jsonResult.setResultStatus(ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsKladrBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsKladrBrowseListByParam(@FormParam("name") String name, @FormParam("regionName") String
            regionName,
                                             @FormParam("regionCode") String regionCode, @FormParam("zoneName") String zoneName,
                                             @FormParam("cityName") String cityName, @FormParam("placeName") String
                                                     placeName, @FormParam("code") String code,
                                             @FormParam("kladrObjId") Long kladrObjId, @FormParam("postalCode") String postalCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("NAME", name);
        params.put("REGIONCODE", regionCode);
        params.put("REGIONNAME", regionName);
        params.put("ZONENAME", zoneName);
        params.put("CITYNAME", cityName);
        params.put("PLACENAME", placeName);
        params.put("CODE", code);
        params.put("KLADROBJID", kladrObjId);
        params.put("POSTALCODE", postalCode);
        params.put("SETMOSCOWFIRST", "TRUE");
        Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsKladrBrowseListByParam", params);
        JsonResult jsonResult = new JsonResult();
        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
            this.requestWorker.serializeJSON(methodCallResult, jsonResult);

        } else {
            jsonResult.setResultStatus(ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsKladrStreetBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsKladrStreetBrowseListByParam(@FormParam("name") String name, @FormParam("kladrCode") String
            kladrCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("NAME", name);
        params.put("KLADRCODE", kladrCode);
        Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsKladrStreetBrowseListByParam", params);
        JsonResult jsonResult = new JsonResult();

        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
        } else {
            jsonResult.setResultStatus(ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsGenFullAddressMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsGenFullAddressMap(@FormParam("addressMap") String addressParam) {
        Address address = this.requestWorker.deserializeJSON(addressParam, Address.class
        );
        JsonResult jsonResult = new JsonResult();
        if (address != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("ADDRESSMAP", address.copyAddressFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BGenFullAddressMap", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BKladrBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BKladrBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsKladrBrowseListByParam");
    }

    @POST
    @Path("/dsB2BGenFullAddressMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BGenFullAddressMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BGenFullAddressMap");
    }

    @POST
    @Path("/dsContractCreateEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsContractCreateEx(@FormParam("contrMap") String contractParam) {
        Contract contract = this.requestWorker.deserializeJSON(contractParam, Contract.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("CONTRMAP", contract.copyContractFromEntityToMap());
            clearKeysForOnlineMethods(params);
            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsContractCreateEx", params);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsTravelContractCreateEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsTravelContractCreateEx(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("OBJMAP", contract.copyObjFromEntityToMap());
            clearKeysForOnlineMethods(params);
            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsTravelContractCreateEx", params);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsSisContractCreateEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsSisContractCreateEx(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("OBJMAP", contract.copyObjFromEntityToMap());
            clearKeysForOnlineMethods(params);
            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsSisContractCreateEx", params);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsMortgageContractCreateEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsMortgageContractCreateEx(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("OBJMAP", contract.copyObjFromEntityToMap());
            clearKeysForOnlineMethods(params);
            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsMortgageContractCreateEx", params);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsContractApplyDiscountInB2BMode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsContractApplyDiscountInB2BMode(@FormParam(FORM_PARAM_NAME) String p) {
        Obj objP = this.requestWorker.deserializeJSON(p, Obj.class
        );
        JsonResult jsonResult = new JsonResult();
        if (objP != null) {
            Map<String, Object> params = new HashMap<>(getMapParamName(objP.copyObjFromEntityToMap(), "obj"));
            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsContractApplyDiscountInB2BMode", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsAngularUserActionLogCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAngularUserActionLogCreate(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("OBJMAP", contract.copyObjFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsAngularUserActionLogCreate", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsAngularCalculatePremium")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAngularCalculatePremium(@FormParam("calcMap") String calcParam) {
        Calculate contract = this.requestWorker.deserializeJSON(calcParam, Calculate.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("CALCMAP", contract.copyCalculateFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsAngularCalculatePremium", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsContractBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsContractBrowseEx(@FormParam("contrMap") String contractParam) {
        Contract contract = this.requestWorker.deserializeJSON(contractParam, Contract.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("CONTRMAP", contract.copyContractFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsContractBrowseEx", params);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(com.bivgroup.pojo.JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsTravelContractBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsTravelContractBrowseEx(@FormParam("contrMap") String contractParam) {
        Contract contract = this.requestWorker.deserializeJSON(contractParam, Contract.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("CONTRMAP", contract.copyContractFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsTravelContractBrowseEx", params);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsSisPromoBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsSisPromoBrowseEx(@FormParam("promoMap") String promoMap) {
        Promo promo = this.requestWorker.deserializeJSON(promoMap, Promo.class
        );
        JsonResult jsonResult = new JsonResult();
        if (promo != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("PROMOMAP", promo.copyPromoFromEntityToMap());
            params.put("PRODVERID", 1080);
            params.put("PRODID", 1080);
            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsPromoBrowseEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsCibPromoBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCibPromoBrowseEx(@FormParam("promoMap") String promoMap) {
        Promo promo = this.requestWorker.deserializeJSON(promoMap, Promo.class
        );
        JsonResult jsonResult = new JsonResult();
        if (promo != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("PROMOMAP", promo.copyPromoFromEntityToMap());
            params.put("PRODVERID", 1060);
            params.put("PRODID", 1060);
            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsPromoBrowseEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsHibPromoBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsHibPromoBrowseEx(@FormParam("promoMap") String promoMap) {
        Promo promo = this.requestWorker.deserializeJSON(promoMap, Promo.class
        );
        JsonResult jsonResult = new JsonResult();
        if (promo != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("PROMOMAP", promo.copyPromoFromEntityToMap());
            params.put("PRODVERID", 1050);
            params.put("PRODID", 1050);
            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsPromoBrowseEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsVzrPromoBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsVzrPromoBrowseEx(@FormParam("promoMap") String promoMap) {
        Promo promo = this.requestWorker.deserializeJSON(promoMap, Promo.class
        );
        JsonResult jsonResult = new JsonResult();
        if (promo != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("PROMOMAP", promo.copyPromoFromEntityToMap());
            params.put("PRODVERID", 1070);
            params.put("PRODID", 1070);
            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsPromoBrowseEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsMortPromoBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsMortPromoBrowseEx(@FormParam("promoMap") String promoMap) {
        Promo promo = this.requestWorker.deserializeJSON(promoMap, Promo.class
        );
        JsonResult jsonResult = new JsonResult();
        if (promo != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("PROMOMAP", promo.copyPromoFromEntityToMap());
            params.put("PRODVERID", 1090);
            params.put("PRODID", 1090);
            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsPromoBrowseEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsSisContractBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsSisContractBrowseEx(@FormParam("contrMap") String contractParam) {
        Contract contract = this.requestWorker.deserializeJSON(contractParam, Contract.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("CONTRMAP", contract.copyContractFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsSisContractBrowseEx", params);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsMortgageContractBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsMortgageContractBrowseEx(@FormParam("contrMap") String contractParam) {
        Contract contract = this.requestWorker.deserializeJSON(contractParam, Contract.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("CONTRMAP", contract.copyContractFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsMortgageContractBrowseEx", params);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsHandBookBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsHandBookBrowseEx(@FormParam("hbMap") String hbParam) {
        Handbook handbook = this.requestWorker.deserializeJSON(hbParam, Handbook.class
        );
        JsonResult jsonResult = new JsonResult();
        if (handbook != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("HBMAP", handbook.copyHandbookFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsHandbooksBrowseEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsCallTerminalPayment")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCallTerminalPayment(@FormParam(FORM_PARAM_NAME) final String paramsStr) {
        return dsB2BCallService(paramsStr, BIVSBERPOSWS, "dsCallTerminalPayment");
    }

    @POST
    @Path("/dsCallCheckPaymentStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCallCheckPaymentStatus(@FormParam(FORM_PARAM_NAME) final String paramsStr) {
        return dsB2BCallService(paramsStr, BIVSBERPOSWS, "dsCallCheckPaymentStatus");
    }

    @POST
    @Path("/dsCallSberOnlySMSPayment")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCallSberOnlySMSPayment(@FormParam(FORM_PARAM_NAME) final String paramsStr) {
        return dsB2BCallService(paramsStr, BIVSBERPOSWS, "dsCallSberOnlySMSPaymentService");
    }

    @POST
    @Path("/dsCallPaymentEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCallPaymentEx(@FormParam("paymentMap") String paymentParam) {
        Payment payment = this.requestWorker.deserializeJSON(paymentParam, Payment.class
        );
        JsonResult jsonResult = new JsonResult();
        if (payment != null) {

            Map<String, Object> params = new HashMap<>();
            Map<String, Object> paybean = payment.copyPaymentFromEntityToMap();
            params.put("PAYMENTMAP", paybean);

            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsCallPaymentService", params);
            if (methodCallResult == null) {
                throw new NullPointerException("method methodCallResult return null");
            }
            Map<String, Object> smsRes = ParamGetter.getMapParamName(methodCallResult, Constants.RESULT);
            if ((smsRes != null) && (getBooleanParam(smsRes.get("SMSVALID"), false))) {
                Map<String, Object> transParam = new HashMap<>();
                transParam.put("DATAMAP", paybean);

                Map<String, Object> methodCallResult1 = this.callExternalService(BIVSBERPOSWS, "dsContractToPaymentState", transParam);
                // тест ошибки перевода состояния
                if ((methodCallResult1 != null) && !methodCallResult1.isEmpty()) {
                    this.requestWorker.serializeJSON(methodCallResult, jsonResult);
                    try {
                        Map<String, Object> cMap = getMapParamName(getMapParamName(methodCallResult1, Constants.RESULT), "CONTRMAP");
                        if (cMap.get("PRODVERID").toString().equals("2100")) {
                            if ("B2B_CONTRACT_DRAFT".equalsIgnoreCase(cMap.get("STATESYSNAME").toString())
                                    || "B2B_CONTRACT_PREPRINTING".equalsIgnoreCase(cMap.get("STATESYSNAME").toString())) {
                                Map<String, Object> payFactParams = new HashMap<>();
                                payFactParams.put("PAYMENTMAP", cMap);
                                Map<String, Object> methodCallResult2 = this.callExternalService(BIVSBERPOSWS, "dsCallFictitiousPaymentService", payFactParams);
                                if ((methodCallResult2 != null) && !methodCallResult2.isEmpty()) {
                                    this.requestWorker.serializeJSON(methodCallResult, jsonResult);
                                } else {
                                    jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
                                }
                            } else {
                                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
                            }
                        }
                    } catch (Exception e) {
                    }
                } else {
                    jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
                }
            } else {
                jsonResult.setResultJson("SmsError");
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsCallPringAndSendEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCallPringAndSendEx(@FormParam("paymentMap") String paymentParam) {
        Payment payment = this.requestWorker.deserializeJSON(paymentParam, Payment.class
        );
        JsonResult jsonResult = new JsonResult();
        if (payment != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("DATAMAP", payment.copyPaymentFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(SIGNBIVSBERPOSWS, "dsCallPringAndSendEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsCallSendSms")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCallSendSms(@FormParam("paymentMap") String paymentParam) {
        Payment payment = this.requestWorker.deserializeJSON(paymentParam, Payment.class
        );
        JsonResult jsonResult = new JsonResult();
        if (payment != null) {
            payment.setAction("sendSms");
            Map<String, Object> params = new HashMap<>();
            params.put("DATAMAP", payment.copyPaymentFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(SIGNBIVSBERPOSWS, "dsCallPringAndSendEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsCallReferralBeginMethod")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCallReferralBeginMethod(@FormParam("paymentMap") String paymentParam) {
        Payment payment = this.requestWorker.deserializeJSON(paymentParam, Payment.class
        );
        JsonResult jsonResult = new JsonResult();
        if (payment != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("DATAMAP", payment.copyPaymentFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BReferralBeginMethod", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsCallSisPringAndSendEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCallSisPringAndSendEx(@FormParam("paymentMap") String paymentParam) {
        Payment payment = this.requestWorker.deserializeJSON(paymentParam, Payment.class
        );
        JsonResult jsonResult = new JsonResult();
        if (payment != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("DATAMAP", payment.copyPaymentFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(SIGNBIVSBERPOSWS, "dsCallSisPringAndSendEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsCallMortgagePrintAndSendEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCallMortgagePrintAndSendEx(@FormParam("paymentMap") String paymentParam) {
        Payment payment = this.requestWorker.deserializeJSON(paymentParam, Payment.class
        );
        JsonResult jsonResult = new JsonResult();
        if (payment != null) {
            payment.setAction("sendSms");

            Map<String, Object> params = new HashMap<>();
            params.put("DATAMAP", payment.copyPaymentFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsCallMortgagePrintAndSendEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsContractReject")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsContractReject(@FormParam("dataMap") String paymentParam) {
        Payment payment = this.requestWorker.deserializeJSON(paymentParam, Payment.class
        );
        JsonResult jsonResult = new JsonResult();
        if (payment != null) {

            Map<String, Object> params = new HashMap<>();
            params.put("DATAMAP", payment.copyPaymentFromEntityToMap());

            Map<String, Object> methodCallResult = this.callExternalService(BIVSBERPOSWS, "dsContractReject", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    private String getFormParameter(Map<String, List<InputPart>> formParts, String paramName) throws
            IOException {
        String formParameter = "";
        List<InputPart> inputPartList = formParts.get(paramName);
        if (inputPartList != null) {
            InputPart inputPart = inputPartList.get(0);
            if (inputPart != null) {
                String inputPartBodyStr = inputPart.getBodyAsString();
                if (inputPartBodyStr != null) {
                    formParameter = java.net.URLDecoder.decode(inputPartBodyStr, "UTF-8");
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Getted form part parameter: '%s' = '%s'.", paramName, formParameter));
        }
        return formParameter;
    }

    @POST
    @Path("/b2bfileupload")
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response B2BUploadFile(MultipartFormDataInput input) throws IOException, Exception {
        Map<String, List<InputPart>> formParts = input.getFormDataMap();
        String SID = getFormParameter(formParts, "SID");
        if (!SID.isEmpty()) {
            SessionController controller = new B2BFIleSessionController(this.sessionTimeOut);
            Map<String, Object> sessionParams = controller.checkSession(SID);
            if (B2BFIleSessionController.sessionWithError(sessionParams)) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
            Long ObjId;
            Long prodbindocId;
            String docName = "";
            StringBuilder output = new StringBuilder();
            JsonResult jsonResult = new JsonResult();
            // dsB2BContractDocument
            // прикрепление документа к договору
            Long contrId = getLongParam(getFormParameter(formParts, "CONTRID"));
            Long participantId = getLongParam(getFormParameter(formParts, "PARTICIPANTID"));
            prodbindocId = getLongParam(getFormParameter(formParts, "PRODBINDOCID"));
            docName = getFormParameter(formParts, "DOCNAME");
            Map<String, Object> docParams = new HashMap<>();
            docParams.put(RETURN_AS_HASH_MAP, "TRUE");
            docParams.put("CONTRID", contrId);
            docParams.put("PARTICIPANTID", participantId);
            docParams.put("PRODBINDOCID", prodbindocId);
            Map<String, Object> docRes = this.callExternalService(B2BPOSWS, "dsB2BContractDocumentCreate", docParams);
            if ((docRes == null) || (docRes.get("CONTRDOCID") == null)) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            ObjId = (Long) docRes.get("CONTRDOCID");
            List<InputPart> inPart = formParts.get("file");

            for (InputPart inputPart : inPart) {
                try {
                    String fileName = readHeaderByte(inputPart);
                    InputStream istream = inputPart.getBody(InputStream.class, null);
                    String path = getUploadPath();
                    output.append(B2BContractSaveFile(istream, path, fileName, ObjId, docName, prodbindocId));
                } catch (IOException e) {
                    logger.error("Error in method B2BUploadFile: ", e);
                    jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
                }
            }

            this.requestWorker.serializeJSON(output.toString(), jsonResult);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    /**
     * загрузка файла на сервер и связывание с указанной сущностью
     * (для PA вызывается, например, со страницы подтверждения заявки об убытке при прикреплении документов)
     * (для B2B вызывается, например, со страницы ???)
     *
     * @param input
     * @return
     * @throws IOException
     * @throws Exception
     */
    @POST
    @Path("/b2bfileuploadex")
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response B2BUploadFileEx(MultipartFormDataInput input) throws IOException, Exception {
        logger.debug("Uploading file (by B2BUploadFileEx method)...");
        Map<String, List<InputPart>> formParts = input.getFormDataMap();
        String SID = getFormParameter(formParts, "SID");
        StringBuilder log = new StringBuilder("загрузка файла ");

        logger.debug("Проверка сессии при прикреплении файла...");
        if (!SID.isEmpty()) {
            logger.debug("Идентификатор сессии (SID) = " + SID);
            Long ObjDocId = null;
            Long prodbindocId = null;
            String docName = "";
            String facadeName = "";
            String serviceName = "";
            String hibernateEntity = "";

            StringBuilder outputStr = new StringBuilder();
            List<Map<String, Object>> outputList = new ArrayList<>();

            JsonResult jsonResult = new JsonResult();

            // dsB2BContractDocument
            // прикрепление документа к чему либо
            // префикс метода калера. для вызова проверки сессии в зависимости от системы.
            String systemName = getStringParam(getFormParameter(formParts, "SYSTEMNAME"));
            if (systemName.isEmpty()) {
                systemName = "B2B";
            }
            logger.debug("Имя системы (SYSTEMNAME) = " + systemName);

            Map<String, Object> sessionRes = new HashMap<>();
            if ("PA".equalsIgnoreCase(systemName)) {
                sessionRes.put(ERROR, "Тип сессии не существует!");
                logger.error("BoxPropertyGate#B2BUploadFileEx Session type '" + systemName + "' is deprecated. formParts = " + formParts);
            } else if ("B2B".equalsIgnoreCase(systemName)) {
                sessionRes = checkB2BSession(SID);
            }
            logger.debug("Результат проверки идентификатора сессии = " + sessionRes);

            if (sessionRes != null) {
                if (sessionRes.get(ERROR) == null) {
                    logger.debug("Проверка сессии для прикрепления файла завершена успешно - сессия корректна.");

                    boolean isAttachSkipped = getBooleanParam(getFormParameter(formParts, UPLOAD_SKIP_DB_ATTACH_PARAM_NAME), false);
                    logger.debug("Skip attaching uploaded file to DB entities (" + UPLOAD_SKIP_DB_ATTACH_PARAM_NAME + "): " + isAttachSkipped);
                    boolean isDocCreateSkipped = getBooleanParam(getFormParameter(formParts, UPLOAD_SKIP_CREATE_DBDOC), false);

                    String path = getUploadPath();
                    if (!path.endsWith(File.separator)) {
                        path += File.separator;
                    }
                    logger.debug("Path for saving files uploaded by users on server: " + path);

                    if (isAttachSkipped) {

                        //
                        String subFolder = getStringParam(getFormParameter(formParts, UPLOAD_SUBFOLDER_PARAM_NAME));
                        if (!subFolder.isEmpty()) {
                            logger.debug("Sub folder for saving uploaded file (" + UPLOAD_SUBFOLDER_PARAM_NAME + "): " + subFolder);
                            if (!subFolder.endsWith(File.separator)) {
                                subFolder += File.separator;
                            }
                            path += subFolder;
                        }

                    } else {
                        docName = getFormParameter(formParts, "DOCNAME");
                        prodbindocId = getLongParam(getFormParameter(formParts, "PRODBINDOCID"));
                        // имя фасада, для вызова методов создания, удаления документов
                        facadeName = getStringParam(getFormParameter(formParts, "FACADENAME"));
                        // имя сервиса содержащего фасад сущности документов.
                        serviceName = getStringParam(getFormParameter(formParts, "SERVICENAME"));
                        // имя хибернейт сущности если прикрепление происходит к хибернейт сущности
                        hibernateEntity = getStringParam(getFormParameter(formParts, DCT_ENTITY_PARAM_NAME));

                        if (!isDocCreateSkipped) {
                            // имя поля с ид объекта, к которому будет привязан док
                            String idFieldName = getStringParam(getFormParameter(formParts, "IDFIELDNAME"));
                            // примари кей записи с документом
                            String idDocFieldName = getStringParam(getFormParameter(formParts, "IDDOCFIELDNAME"));
                            // ид объекта, к которому будет привязан док
                            Long objId = getLongParam(getFormParameter(formParts, idFieldName));
                            Map<String, Object> docParams = new HashMap<>();
                            docParams.put(RETURN_AS_HASH_MAP, "TRUE");
                            docParams.put(idFieldName, objId);
                            docParams.put("PRODBINDOCID", prodbindocId);
                            Map<String, Object> docRes = this.callExternalService(serviceName, "ds" + facadeName + "Create", docParams);
                            if ((docRes == null) || (docRes.get(idDocFieldName) == null)) {
                                return Response.status(Response.Status.BAD_REQUEST).build();
                            }
                            ObjDocId = getLongParam(docRes.get(idDocFieldName).toString());
                        } else {
                            ObjDocId = getLongParam(getFormParameter(formParts, "OBJID"));
                        }
                    }

                    List<InputPart> inPart = formParts.get("file");
                    for (InputPart inputPart : inPart) {
                        try {
                            String fileName = readHeaderByte(inputPart);
                            log.append("'").append(fileName).append("', ");
                            InputStream istream = inputPart.getBody(InputStream.class, null);

                            if (isAttachSkipped) {

                                Map<String, Object> fileMap = new HashMap<>();
                                logger.debug("User file name: " + fileName);
                                fileMap.put("USERFILENAME", fileName);
                                try {
                                    Map<String, Object> savedFileMap = B2BSaveFileEx(istream, path, fileName);
                                    fileMap.putAll(savedFileMap);
                                    log.append(" сохранен как '").append(fileMap.get("SAVEDFILEFULLNAME")).append("', ");
                                    fileMap.remove("SAVEDFILE");
                                } catch (Exception ex) {
                                    // todo: в исходном B2BSaveFileEx отсутствует обработка ошибок и возврат точных сообщений об ошибках - возможно, необходимо реализовать их обработку и пр.
                                    fileMap.put(UPPERCASE_ERROR, ex.getLocalizedMessage());
                                    logger.error("Exception during saving file uploaded by user: ", ex);
                                    log.append("ошибка загрузки");
                                }

                                outputList.add(fileMap);

                            } else {
                                outputStr.append(B2BSaveFileEx(istream, path, fileName, ObjDocId, serviceName, "ds" + facadeName, docName, prodbindocId, hibernateEntity));
                            }
                        } catch (IOException e) {
                            logger.error("Error in method B2BUploadFileEx: ", e);
                            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
                            log.append("ошибка загрузки");
                        }
                    }

                    if (isAttachSkipped) {
                        this.requestWorker.serializeJSON(outputList, jsonResult);
                    } else {
                        this.requestWorker.serializeJSON(outputStr.toString(), jsonResult);
                    }
                    logger.debug("Uploading file (by B2BUploadFileEx method) finished.");
                    log.append("успешно");
                    fileUploadToAudit(sessionRes, log, new String[]{"SB1"});
                    return Response.status(Response.Status.OK).entity(jsonResult).build();
                } else {
                    logger.debug("Проверка сессии для прикрепления файла завершена с ошибкой - сессия некорректна, файл прикреплен не будет.");
                    return Response.status(Response.Status.UNAUTHORIZED).build();
                }
            } else {
                logger.debug("Проверка сессии для прикрепления файла завершена с ошибкой - сессия некорректна, файл прикреплен не будет.");
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        }
        logger.debug("Не указан идентификатор сессии для проверки перед прикреплением файла - файл прикреплен не будет.");
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    // загрузка на сервер .xml файла с продуктом или версией продукта для последующего импорта
    // (для B2B вызывается, например, со страницы поиска продуктов (кнопки "Импортировать", "Импортировать с мержем"))
    @POST
    @Path("/dsB2BUploadProdFile")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes("multipart/form-data")
    public Response dsB2BUploadProdFile(MultipartFormDataInput input) throws IOException, Exception {
        Map<String, List<InputPart>> formParts = input.getFormDataMap();
        String SID = getFormParameter(formParts, "SID");
        if (SID.isEmpty()) {
            Boolean sessionIsValid = fileSessionIsValid(SID);
            if (!sessionIsValid) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }

        Long isMerge = getLongParam(getFormParameter(formParts, "ISMERGE"));
        JsonResult jsonResult = new JsonResult();

        List<InputPart> inPart = formParts.get("file");
        StringBuilder output = new StringBuilder();
        for (InputPart inputPart : inPart) {
            try {
                InputStream istream = inputPart.getBody(InputStream.class, null);
                String path = getUploadPath();
                output.append(B2BProductSaveFile(istream, path, isMerge));
            } catch (IOException e) {
                logger.error("Error in method dsB2BUploadProdFile", e);
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        }
        this.requestWorker.serializeJSON(output.toString(), jsonResult);
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    private String B2BProductSaveFile(InputStream uploadedInputStream, String serverLocation, Long isMerge) {
        return B2BSaveFile(uploadedInputStream, serverLocation, B2BPOSWS, "dsB2BProductImportFromXMLFile", isMerge);
    }

    private String B2BSaveFile(InputStream uploadedInputStream, String serverLocation, String serviceName, String
            methodName, Long isMerge) {
        String uploadFileName;
        String diskFileName = UUID.randomUUID().toString();
        // вот такая жопка - serverLocation получают так: String serverLocation = getUploadPath();
        // в getUploadPath этот самый path берется или из конфига где у него уже есть separator на конце
        if (serverLocation.endsWith("/") || serverLocation.endsWith("\\")) {
            uploadFileName = serverLocation + diskFileName;
        } else {
            uploadFileName = serverLocation + File.separator + diskFileName;
        }
        OutputStream outputStream = null;
        try {
            int read = 0;
            byte[] bytes = new byte[1024];
            File uploadFile = new File(uploadFileName);
            if (uploadFile.getCanonicalPath().startsWith(serverLocation)) {
                outputStream = new FileOutputStream(uploadFile);
                while ((read = uploadedInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                outputStream.flush();
                outputStream.close();
                Map<String, Object> params = new HashMap<>();
                params.put("XMLFILENAME", uploadFileName);
                params.put("ISMERGE", isMerge);
                this.callExternalService(serviceName, methodName, params);
                return "File saved to server location : " + uploadFileName + "\n";
            }
        } catch (IOException e) {
            logger.error("B2BSaveFile exception ", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
                logger.error(String.format("Error close file %s", uploadFileName));
                logger.error(ex);
            }
        }
        return "ERROR LOAD: " + uploadFileName;
    }

    private String B2BSaveFileEx(InputStream uploadedInputStream, String serverLocation, String fileName, Long
            objId,
                                 String serviceName, String methodPrefix, String docName, Long prodBinDocId, String hibernateEntity) {
        try {
            int fileSize = 0;
            String filePath = null;
            if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
                String masterUrlString = getSeaweedFSUrl();
                URL masterURL = new URL(masterUrlString);
                WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                Assignation a = client.assign(new AssignParams("b2battach", ReplicationStrategy.TwiceOnRack));
                fileSize = client.write(a.weedFSFile, a.location, uploadedInputStream, fileName);
                if (fileSize == 0) {
                    throw new Exception("Unable to write file to SeaweedFS");
                }
                filePath = a.weedFSFile.fid;
            } else {
                int read = 0;
                byte[] bytes = new byte[1024];
                String diskFileName = UUID.randomUUID().toString();
                File uploadFile = new File(serverLocation + File.separator + diskFileName);
                if (uploadFile.getCanonicalPath().startsWith(serverLocation)) {
                    try (OutputStream outputStream = new FileOutputStream(uploadFile)) {
                        while ((read = uploadedInputStream.read(bytes)) != -1) {
                            outputStream.write(bytes, 0, read);
                        }
                    }
                    filePath = diskFileName;
                    fileSize = (int) uploadFile.length();
                } else {
                    throw new IOException("Error load file to serverLocation");
                }
            }
            // параметры прикрепления (общие для Cayenne и Hibernate)
            Map<String, Object> params = new HashMap<>();
            params.put("OBJID", objId);
            params.put("FILENAME", fileName);
            params.put("FILEPATH", filePath);
            params.put("FILESIZE", fileSize);
            params.put("FILETYPEID", prodBinDocId);
            params.put(DCT_FILE_TYPE_NAME_PARAMNAME, docName);
            if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
                params.put("FSID", filePath);
            }
            params.put(RETURN_AS_HASH_MAP, true);
            String methodName;
            if (hibernateEntity.isEmpty()) {
                // если прикрепление происходит к Cayenne объекту
                methodName = methodPrefix + "_BinaryFile_createBinaryFileInfo";
            } else {
                // если прикрепление происходит к Hibernate объекту
                params.put(DCT_ENTITY_PARAM_NAME, hibernateEntity);
                serviceName = B2BPOSWS;
                methodName = "dsB2BDictionaryCreateBinaryFileInfo";
            }
            Map<String, Object> attachResult = this.callExternalService(serviceName, methodName, params, logger.isDebugEnabled());
            return "File saved to server location : " + filePath + "\n";
        } catch (IOException e) {
            logger.error("B2BSaveFileEx exception ", e);
        } catch (Exception e) {
            logger.error("B2BSaveFileEx exception ", e);
        }
        return "ERROR LOAD: " + serverLocation + File.separator + fileName;
    }

    // сохранение файла из потока в указанную папку на сервере без связывания с какой-либо сущностью в БД
    private Map<String, Object> B2BSaveFileEx(InputStream uploadedInputStream, String serverLocation, String
            fileName) throws Exception {
        // имя файла на сервере (на сервере файл сохраняется под уникальным именем, получаемым прибавлением uuid)
        String diskFileName = UUID.randomUUID().toString() + "_" + fileName;
        logger.debug("Server file name: " + diskFileName);
        // полное имя файла (включая путь) на сервере
        String fileFullName = serverLocation + File.separator + diskFileName;

        File savedFile = writeToFile(fileFullName, uploadedInputStream);

        Map<String, Object> fileMap = new HashMap<>();

        String savedFileFullName = savedFile.getAbsolutePath();
        logger.debug("Saved file full name: " + savedFileFullName);
        Long savedFileSizeInBytes = savedFile.length();
        logger.debug("Saved file size (bytes): " + savedFileSizeInBytes);
        fileMap.put("SAVEDFILE", savedFile); // todo: возможно, не потребуется
        fileMap.put("SAVEDFILENAME", diskFileName);
        fileMap.put("SAVEDFILEFULLNAME", savedFileFullName);
        fileMap.put("SAVEDFILESIZE", savedFileSizeInBytes);

        return fileMap;
    }

    private String B2BContractSaveFile(InputStream uploadedInputStream, String serverLocation, String
            fileName, Long ObjId, String docName, Long prodbindocId) {
        return B2BSaveFileEx(uploadedInputStream, serverLocation, fileName, ObjId, B2BPOSWS, "dsB2BContractDocument", docName, prodbindocId, "");
    }

    private String B2BParticipantSaveFile(InputStream uploadedInputStream, String serverLocation, String
            fileName, Long ObjId, String docName, Long prodbindocId) {
        return B2BSaveFileEx(uploadedInputStream, serverLocation, fileName, ObjId, BIVSBERPOSWS, "dsInsParticipantBinFile", docName, prodbindocId, "");
    }

    private static String bytesToString(byte[] value) {
        String result = "";
        try {
            result = new String(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return result;
    }

    private static byte[] stringToBytes(String value) {
        byte[] result = null;
        try {
            result = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return result;
    }

    private static String base64Decode(String input) {
        org.apache.commons.codec.binary.Base64 decoder = new org.apache.commons.codec.binary.Base64();
        return bytesToString(decoder.decode(input.getBytes()));
    }

    private static String base64Encode(String input) {
        org.apache.commons.codec.binary.Base64 encoder = new org.apache.commons.codec.binary.Base64();
        String result = bytesToString(encoder.encode(stringToBytes(input)));
        return result.substring(0, result.length() - 2);
    }

    private static String base64DecodeUrlSafe(String input) {
        Base64 decoder = new Base64(true);
        return bytesToString(decoder.decode(input));
    }

    private static String base64EncodeUrlSafe(String input) {
        Base64 encoder = new Base64(true);
        String result = bytesToString(encoder.encode(stringToBytes(input)));
        return result.substring(0, result.length() - 2);
    }

    private String readHeaderByte(InputPart inputPart) throws Exception {
        String headerStr = inputPart.getHeaders().getFirst("Content-Disposition");
        String[] headerArr = headerStr.split("; ");
        String result = "";
        if (headerArr[2].indexOf("filename") >= 0) {
            String fileName = headerArr[2].replaceAll("\"", "");
            String[] fileNameArr = fileName.split("filename=");
            result = fileNameArr[1];
            result = base64Decode(result);
        }
        return result;
    }

    private byte[] readByteArray(InputPart inputPart) throws Exception {
        Field f = inputPart.getClass().getDeclaredField("bodyPart");
        f.setAccessible(true);
        BodyPart bodyPart = (BodyPart) f.get(inputPart);
        SingleBody body = (SingleBody) bodyPart.getBody();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        body.writeTo(os);
        byte[] fileBytes = os.toByteArray();
        return fileBytes;
    }

    // Parse Content-Disposition header to get the original file name
    private String parseFileName(MultivaluedMap<String, String> headers) {
        String[] contentDispositionHeader = headers.getFirst("Content-Disposition").split(";");
        for (String name : contentDispositionHeader) {
            if ((name.trim().startsWith("filename"))) {
                String[] tmp = name.split("=");
                String fileName = tmp[1].trim().replaceAll("\"", "");
                return fileName;
            }
        }
        return "randomName";
    }

    // сохранение файла на сервер для онлайн продуктов B2B
    private void B2BOnlineSaveFile(InputStream uploadedInputStream,
                                   String serverLocation, String fileName) {
        try {
            File file = new File(serverLocation);
            if (file.getCanonicalPath().startsWith(getUploadPath())) {
                String fid = null;
                String[] fileNameArr = fileName.split("_", 3);
                String hash = fileNameArr[0];
                String typeSysName = fileNameArr[1];
                String originalName = fileNameArr[2];
                int seaweedWrittenSize = 0;
                if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
                    String masterUrlString = getSeaweedFSUrl();
                    URL masterURL = new URL(masterUrlString);
                    WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                    Assignation a = client.assign(new AssignParams("b2battach", ReplicationStrategy.TwiceOnRack));
                    seaweedWrittenSize = client.write(a.weedFSFile, a.location, uploadedInputStream, originalName);
                    if (seaweedWrittenSize == 0) {
                        throw new Exception("Unable to write file to SeaweedFS");
                    }
                    fid = a.weedFSFile.fid;
                } else {
                    int read = 0;
                    byte[] bytes = new byte[1024];
                    try (OutputStream outpuStream = new FileOutputStream(file)) {
                        while ((read = uploadedInputStream.read(bytes)) != -1) {
                            outpuStream.write(bytes, 0, read);
                        }
                    }
                }
                //
                // по сиснейму типу документа определяем ИД типа и наименование
                Map<String, Object> crmDocParams = new HashMap<>();
                crmDocParams.put(RETURN_AS_HASH_MAP, "TRUE");
                crmDocParams.put("SYSNAME", typeSysName);
                Map<String, Object> crmDocRes = this.callExternalService(Constants.CRMWS, "personDocTypeGetByIdOrSysname", crmDocParams);
                // считываем договор из базы
                Map<String, Object> contrParams = new HashMap<>();
                contrParams.put(RETURN_AS_HASH_MAP, "TRUE");
                contrParams.put("EXTERNALID", base64Decode(hash));
                Map<String, Object> contrRes = this.callExternalService(B2BPOSWS, "dsB2BContractBrowseListByParamExShort", contrParams);
                if (contrRes != null && contrRes.get("CONTRID") != null) {
                    Map<String, Object> docParams = new HashMap<>();
                    docParams.put(RETURN_AS_HASH_MAP, "TRUE");
                    docParams.put("CONTRID", contrRes.get("CONTRID"));
                    docParams.put("PARTICIPANTID", contrRes.get("INSURERID"));
                    // данного ид нет, т.к. прикрепляем crm документ
                    Map<String, Object> docRes = this.callExternalService(B2BPOSWS, "dsB2BContractDocumentCreate", docParams);
                    if ((docRes != null) && (docRes.get("CONTRDOCID") != null)) {
                        // прикрепляем файл к таблице с документами
                        Map<String, Object> params = new HashMap<>();
                        params.put("OBJID", docRes.get("CONTRDOCID"));
                        params.put("FILENAME", fileName);
                        if (getUseSeaweedFS().equalsIgnoreCase("TRUE")) {
                            params.put("FILEPATH", fid);
                            params.put("FILESIZE", seaweedWrittenSize);
                            params.put("FSID", fid);
                        } else {
                            params.put("FILEPATH", fileName/*serverLocation*/);
                            params.put("FILESIZE", file.length());
                        }
                        if (crmDocRes == null) {
                            throw new Exception("method dsB2BContractBrowseListByParamExShort return null");
                        } else {
                            params.put("FILETYPEID", crmDocRes.get("PERSONDOCTYPEID"));
                        }
                        params.put(DCT_FILE_TYPE_NAME_PARAMNAME, crmDocRes.get("NAME"));
                        params.put(RETURN_AS_HASH_MAP, "TRUE");
                        this.callExternalService(B2BPOSWS, "dsB2BContractDocument_BinaryFile_createBinaryFileInfo", params);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error B2BOnlineSaveFile: ", e);
        }
    }

    // сохранение на сервере файла из потока uploadedInputStream под полным именем fileFullName
    private File writeToFile(String fileFullName, InputStream uploadedInputStream) throws
            FileNotFoundException, IOException {
        logger.debug("Writing file to disk...");
        File uploadFile = new File(fileFullName);
        if (uploadFile.getCanonicalPath().startsWith(getUploadPath())) {
            try (OutputStream outputStream = new FileOutputStream(uploadFile)) {
                byte[] bytes = new byte[1024];
                int read;
                while ((read = uploadedInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            }
            logger.debug("Writing file to disk finished.");
        }
        return uploadFile;
    }

    private Map<String, Object> doB2BLogin(String login, String password, String projectName) {
        LoginCommonGate loginService = new LoginCommonGate();
        StringBuilder auditMessageBuilder = new StringBuilder();
        return loginService.doB2BLogin(login, password, projectName, auditMessageBuilder);
    }

    private void passwordCreateRecoverToAudit(HashSet groups, StringBuilder log) {
        if (groups.contains("SB1")) {
            auditLogger.info(log.toString());
        }
    }

    private void fileUploadToAudit(Map<String, Object> params, StringBuilder message, String[] groupNames) {

        String login = (String) params.get(LOGIN);
        String groupsListStr = (String) params.get(B2B_USERGROUPS_PARAMNAME);
        Long userAccountId = Long.parseLong(params.get(SESSIONPARAM_USERACCOUNTID).toString());
        String prefix = String.format("Пользователь ['%s', %s]: ", login, userAccountId);
        //если не передали группы (пустая строка или нулл) то пишем в лог безусловно
        if (groupNames != null && groupNames.length == 1 && groupNames[0].isEmpty()) {
            auditLogger.info(prefix + message);
            return;
        }
        HashSet<String> groupSet = new HashSet<>(Arrays.asList(groupsListStr.split(",")));
        Object o = Arrays.stream(groupNames).filter(groupSet::contains).findFirst().orElse(null);
        if (o == null) return;
        auditLogger.error(prefix + message);
    }

    @POST
    @Path("/dsB2BLoginWithTempPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLoginWithTempPassword(@FormParam(FORM_PARAM_NAME) String paramsStr, @Context HttpHeaders
            httpHeaders) {
        try {
            JsonResult jsonResult = new JsonResult();
            Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            Map<String, Object> obj = getMapParamName(objMap, "obj");

            String login = (String) obj.get("login");
            String tempPassword = (String) obj.get("tempPassword");
            String newPassword = (String) obj.get("newPassword");

            if (!newPassword.equals((String) obj.get("repeatNewPassword"))) {
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Введенные постоянные пароли не совпадают.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }

            Map<String, Object> userAccountSelectResult = callExternalService(B2BPOSWS,
                    "dsAdminGetUsersList", new HashMap<String, Object>() {{
                        put("CUSTOMWHERE", String.format(" T.LOGIN = \'%s\' ", login));
                    }});

            if (!userAccountSelectResult.get("TOTALCOUNT").equals(1)) {
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Невозможно получить информации о пользователе.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }

            Map<String, Object> userAccount = ParamGetter.getFirstItemFromResultMap(userAccountSelectResult);

            Map<String, Object> userTempPasswordSelectResult = callExternalService(B2BPOSWS,
                    "dsUserTempPasswordBrowseListByParam", new HashMap<String, Object>() {{
                        put(USER_ACCOUNT_ID_PARAM_NAME, userAccount.get(USER_ACCOUNT_ID_PARAM_NAME));
                    }});

            if (!userTempPasswordSelectResult.get("TOTALCOUNT").equals(1)) {
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Необходимо получить временный пароль.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }

            Map<String, Object> userTempPassword = ParamGetter.getFirstItemFromResultMap(userTempPasswordSelectResult);

            if (!DigestUtils.sha512Hex(tempPassword).equals(userTempPassword.get("TEMPPASSWORD"))) {
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Введенный временный пароль неверный.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }

            if (((Date) userTempPassword.get("PWDEXPDATE")).before(GregorianCalendar.getInstance().getTime())) {
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Действие временного пароля истекло");
                    }});
                }}, jsonResult);
            }

            Result passwordValidationResult = verifier.isPasswordValid(newPassword);

            if (!passwordValidationResult.equals(Result.OK)) {
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Пароль не соответствует правилам безопасности");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }

            if (!callExternalService(B2BPOSWS, "dsB2BAdminUserChangePasswordAccountWithoutOldPass",
                    new HashMap<String, Object>() {{
                        put("ACCID", userAccount.get(USER_ACCOUNT_ID_PARAM_NAME));
                        put("NEWPASS", newPassword);
                    }}).get(STATUS).equals("OK")) {
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Ошибка смены пароля.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            String projectName = getStringParam(httpHeaders.getRequestHeaders().getFirst("projectName"));
            Map<String, Object> loginResult = doB2BLogin(login, newPassword, projectName);
            if ((loginResult == null) || (loginResult.isEmpty())) {
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Ошибка смены пароля.");
                    }});
                }}, jsonResult);
            } else {
                this.requestWorker.serializeJSON(loginResult, jsonResult);
            }
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        } catch (Throwable throwable) {
            JsonResult jsonResult = new JsonResult();
            this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                put(Constants.RESULT, new HashMap<String, Object>() {{
                    put(ERROR, "Ошибка смены пароля.");
                }});
            }}, jsonResult);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        }
    }

    @POST
    @AuditBean
    @Path("/dsB2BLogin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLogin(@FormParam("params") String paramsStr, @Context HttpHeaders httpHeaders, @Context Audit audit) {
        LoginCommonGate loginService = new LoginCommonGate();
        return loginService.dsB2BLogin(paramsStr, httpHeaders, audit);
    }

    @POST
    @AuditBean
    @Path("/dsB2BLogOut")
    @Produces("application/json")
    public Response dsB2BLogOut(@FormParam(FORM_PARAM_NAME) String paramStr, @Context HttpHeaders headers, @Context Audit audit) {
        return new LoginCommonGate().dsB2BLogout(paramStr, headers, audit);
    }

    @POST
    @Path("/getActiveSessionsOfUser")
    @Produces("application/json")
    @Consumes("application/json")
    public Map<String, Long> getActiveSessionsOfUser(String userName) {
        return new LoginCommonGate().getActiveSessionsOfUser(userName);
    }

    // дополнительная обработка ответа со статусом 'EMPTYLOGINPASS' (генерация описания ошибки, сохранение в БД)
    private void processResultStatusEmptyLoginPass(JsonResult jsonResult, String methodName, String
            serviceName, String missingParamName, Map<String, Object> callParams) {

        // генерация описания ошибки
        missingParamName = missingParamName.replaceAll("\\|\\|", "] or [");
        String errorText = String.format("Insufficient parameters given for calling method [%s] from service [%s] - at least one ([%s]) of required parameters is missing, call execution was skipped! Call parameters: ", methodName, serviceName, missingParamName);
        if (callParams != null) {
            errorText = errorText + callParams.toString();
        } else {
            errorText = errorText + "not found";
        }
        logger.error(errorText);

        // формирование описания результата вызова для возможности использования универсального сохранения ошибки в БД
        HashMap<String, Object> fakeCallResult = new HashMap<>();
        HashMap<String, Object> fakeCallSubResult = new HashMap<>();
        fakeCallSubResult.put(STATUS, UPPERCASE_ERROR);
        fakeCallSubResult.put(ERROR, errorText);
        fakeCallResult.put(Constants.RESULT, fakeCallSubResult);
        String login = getLogin();
        String password = getPassword();

        // вызов универсального анализа результата с сохранением ошибки в БД
        this.analyseResultAndSaveErrorInLog(fakeCallResult, serviceName, methodName, callParams, login, password);

        // cформированный результат повторно обновляем описанием ошибки (в analyseResultAndSaveErrorInLog после сохранения ошибки в БД результат очищается но не дополняется значимыми данными)
        fakeCallResult.put(Constants.RESULT, fakeCallSubResult);

        // подготовка ответа для возврата из гейта (не будет виден пользователю, но будет доступен в протоколе и в отладке js)
        this.requestWorker.serializeJSON(fakeCallResult, jsonResult);
        jsonResult.setResultStatus(JsonResult.RESULT_empty_LoginPass);

    }

    @POST
    @AuditBean
    @Path("/dsLogin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsLogin(@FormParam("params") String paramsStr, @Context HttpHeaders httpHeaders, @Context Audit audit) {
        sessionTimeOut = getSessionTimeOut();
        return dsB2BLogin(paramsStr, httpHeaders, audit);
    }

    @POST
    @Path("/dsB2BMultiContractSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMultiContractSave(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> contrMap = getMapParamName(contract.copyObjFromEntityToMap(), "obj");
            contrMap.put("PRODSYSNAME", SYSNAME_MULTI); // принудительно устанавливается системное имя продукта для использования в dsB2BContrSave
            params.put("CONTRMAP", contrMap);
            clearKeysForOnlineMethods(params);
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BContrSave", params);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BMultiContractLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMultiContractLoad(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> contrMap = getMapParamName(contract.copyObjFromEntityToMap(), "obj");
            params.put("HASH", contrMap.get("HASH")); // для angular-интерфейсов загрузка только по хешу
            params.put("PRODSYSNAME", SYSNAME_MULTI); // принудительно устанавливается системное имя продукта для дальнейшей сверки в dsB2BContrLoad
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BMultiContrLoad", params);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BBusinessStabContractSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBusinessStabContractSave(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> contrMap = getMapParamName(contract.copyObjFromEntityToMap(), "obj");
            contrMap.put("PRODSYSNAME", SYSNAME_BUSINESS_STAB); // принудительно устанавливается системное имя продукта для использования в dsB2BContrSave
            params.put("CONTRMAP", contrMap);
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BContrSave", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BBusinessStabContractLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBusinessStabContractLoad(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> contrMap = getMapParamName(contract.copyObjFromEntityToMap(), "obj");
            params.put("HASH", contrMap.get("HASH")); // для angular-интерфейсов загрузка только по хешу
            params.put("PRODSYSNAME", SYSNAME_BUSINESS_STAB); // принудительно устанавливается системное имя продукта для дальнейшей сверки в dsB2BContrLoad
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BBusinessStabContractLoad", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BBusinessStabHandbooksBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBusinessStabHandbooksBrowseEx(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = getMapParamName(contract.copyObjFromEntityToMap(), "obj");
            params.put("PRODSYSNAME", SYSNAME_BUSINESS_STAB); // принудительно устанавливается системное имя продукта для использования в dsB2BVZRCalculatePremValues
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BBusinessStabHandbooksBrowseEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BVZRHandbooksBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BVZRHandbooksBrowseEx(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = getMapParamName(contract.copyObjFromEntityToMap(), "obj");
            params.put("PRODSYSNAME", SYSNAME_VZR); // принудительно устанавливается системное имя продукта для использования в dsB2BVZRCalculatePremValues
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BVZRHandbooksBrowseEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BVZRCalculatePremValues")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BVZRCalculatePremValues(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = getMapParamName(contract.copyObjFromEntityToMap(), "obj");
            params.put("PRODSYSNAME", SYSNAME_VZR); // принудительно устанавливается системное имя продукта для использования в dsB2BVZRCalculatePremValues
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BVZRCalculatePremValues", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BVZRContractApplyDiscount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BVZRContractApplyDiscount(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class
        );
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> contrMap = getMapParamName(contract.copyObjFromEntityToMap(), "obj");
            contrMap.put("PRODSYSNAME", SYSNAME_VZR);
            params.put("CONTRMAP", contrMap);
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BVZRContractApplyDiscount", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BGetProductByDiscountCode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BGetProductByDiscountCode(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BGetProductByDiscountCode");
    }

    // сохранение b2b-договора для онлайн-продуктов
    private Response callB2BContractSaveForOnlineProduct(String contractParam, String b2bProductSysName) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> contrMap = getMapParamName(contract.copyObjFromEntityToMap(), "obj");
            contrMap.put("PRODSYSNAME", b2bProductSysName); // принудительно устанавливается системное имя продукта для использования в dsB2BContrSave
            Map<String, Object> params = new HashMap<>();
            params.put("CONTRMAP", contrMap);
            clearKeysForOnlineMethods(params);
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BContrSave", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    // загрузка b2b-договора для онлайн-продуктов
    private Response callB2BContractLoadForOnlineProduct(String contractParam, String b2bProductSysName) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> contrMap = getMapParamName(contract.copyObjFromEntityToMap(), "obj");
            Map<String, Object> params = new HashMap<>();
            params.put("HASH", contrMap.get("HASH")); // для angular-интерфейсов онлайн-продуктов загрузка только по хешу
            params.put("PRODSYSNAME", b2bProductSysName); // принудительно устанавливается системное имя продукта для использования в dsB2BContrLoad
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BContrLoad", params);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BVZRContractSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BVZRContractSave(@FormParam("objMap") String contractParam) {
        String b2bProductSysName = SYSNAME_VZR;
        Response response = callB2BContractSaveForOnlineProduct(contractParam, b2bProductSysName);
        return response;
    }

    @POST
    @Path("/dsB2BVZRContractLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BVZRContractLoad(@FormParam("objMap") String contractParam) {
        String b2bProductSysName = SYSNAME_VZR;
        Response response = callB2BContractLoadForOnlineProduct(contractParam, b2bProductSysName);
        return response;
    }

    @POST
    @Path("/dsB2BSISHandbooksBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSISHandbooksBrowseEx(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = contract.copyObjFromEntityToMap();
            params.put("PRODSYSNAME", SYSNAME_SIS); // принудительно устанавливается системное имя продукта для использования в dsB2BSISHandbooksBrowseEx
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BSISHandbooksBrowseEx", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BAntiMiteContractSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAntiMiteContractSave(@FormParam("objMap") String contractParam) {
        String b2bProductSysName = SYSNAME_ANTIMITE;
        Response response = callB2BContractSaveForOnlineProduct(contractParam, b2bProductSysName);
        return response;
    }

    @POST
    @Path("/dsB2BAntiMiteContractLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAntiMiteContractLoad(@FormParam("objMap") String contractParam) {
        String b2bProductSysName = SYSNAME_ANTIMITE;
        Response response = callB2BContractLoadForOnlineProduct(contractParam, b2bProductSysName);
        return response;
    }

    @POST
    @Path("/dsB2BSISContractSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSISContractSave(@FormParam("objMap") String contractParam) {
        String b2bProductSysName = SYSNAME_SIS;
        Response response = callB2BContractSaveForOnlineProduct(contractParam, b2bProductSysName);
        return response;
    }

    @POST
    @Path("/dsB2BSISContractLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSISContractLoad(@FormParam("objMap") String contractParam) {
        String b2bProductSysName = SYSNAME_SIS;
        Response response = callB2BContractLoadForOnlineProduct(contractParam, b2bProductSysName);
        return response;
    }

    private Map<String, Object> parseInputParams(String paramsStr) {
        // 1. десериализовать входную строку
        Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        if (paramsObj != null) {
            // 2. получаем мапу параметров
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            Map<String, Object> obj = getMapParamName(objMap, "obj");
            // 3. возвращаем результат - мапу параметров.
            return obj;
        }
        return null;
    }

    @POST
    @Path("/dsFeedBackSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsFeedBackSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        JsonResult jsonResult = new JsonResult();

        // 1 разбор входных параметров
        Map<String, Object> params = parseInputParams(paramsStr);
        // 1 расшифровать параметр url`a,
        String urlEncr = getStringParam(params, "URLHASH");
        if (params == null) {
            params = new HashMap<>();
        }
        String urlHash = getStringParam(urlEncr);
        if (!urlHash.isEmpty()) {
            Map<String, Object> urlParams = decodeInputParam(urlHash, URLFEEDBACKPARAMNAMES);
            // 2 получить дату генерации url, проверить таймаут
            //   Long urlCreateDateTimeInMillis = getLongParam(getStringParam(urlParams.get("CREATEDATE")));
            //    if (urlCreateDateTimeInMillis != null) {
            //        if (checkUrlTimeout(urlCreateDateTimeInMillis.longValue())) {
            // url еще действителен
            // 3 получить ид договора, преобразовать его в целое
            Long contrid = getLongParam(getStringParam(urlParams.get("CONTRID")));
            // 5 получить тип отзыва, преобразовать его в строку
            String feedbackType = getStringParam(urlParams.get("FEEDBACKTYPE"));
            // 4 получить текст отзыва, преобразовать его в строку
            String feedback = getStringParam(params, "FEEDBACK");
            // 6 вызвать метод сохранеиня отзыва
            if (!feedbackType.isEmpty()) {
                Map<String, Object> feedbackParams = new HashMap<>();
                feedbackParams.put("CONTRID", contrid);
                feedbackParams.put("TYPE", feedbackType);
                feedbackParams.put("FEEDBACK", feedback);
                Map<String, Object> feedbackRes = callExternalService(B2BPOSWS, "dsB2BFeedBackCreate", feedbackParams);
                params.put("FEEDBACKTYPE", feedbackType);
                // возвращаем результат
                this.requestWorker.serializeJSON(params, jsonResult);
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
        }
        // возвращаем результат
        this.requestWorker.serializeJSON(params, jsonResult);
        jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsFSMigrationUploadFilesToFS")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsFSMigrationUploadFilesToFS(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsFSMigrationUploadFilesToFS");
    }

    private Map<String, Object> decodeInputParam(String urlHashCoded, String[] urlNamesList) {
        Map<String, Object> result = new HashMap<>();
        B2BSessionController controller = new B2BSessionController(this.sessionTimeOut);
        if (urlHashCoded.isEmpty()) {
            result.put("Info", "Нет параметров в строке ссылки");
            return result;
        } else {
            String[] s;
            try {
                s = controller.decompileString(base64Decode(urlHashCoded));
            } catch (SessionUtilException ex) {
                result.put("Info", "");
                return result;
            }
            for (int j = 0; j < s.length; j++) {
                result.put(urlNamesList[j], s[j]);
            }
        }
        return result;
    }

    private boolean checkUrlTimeout(long urlCreateDateTimeInMillis) {
        Long timeOut = this.urlTimeOut; // таймаут - 2880 минут (2 дня) или значение из конфига
        GregorianCalendar gcSessionValid = new GregorianCalendar();
        gcSessionValid.setTimeInMillis(urlCreateDateTimeInMillis);
        gcSessionValid.add(Calendar.MINUTE, timeOut.intValue());
        GregorianCalendar gcNowDate = new GregorianCalendar();
        gcNowDate.setTime(new Date());
        return gcSessionValid.getTimeInMillis() >= gcNowDate.getTimeInMillis();
    }

    private Map<String, Object> checkB2BSession(String sessionId) {
        Map<String, Object> result = new HashMap<>();
        if (sessionId == null || sessionId.isEmpty()) {
            result.put(ERROR, "Пользователь не представился");
            return result;
        }
        SessionController controller = new B2BSessionController(this.sessionTimeOut);
        result = controller.checkAndCreateSession(sessionId);
        if (result.get(ERROR) != null) {
            return getErrorMap(result);
        } else {
            result.put(SESSION_ID_PARAMNAME, result.get(B2BSessionController.SESSION_ID_PARAMNAME));
            return result;
        }
    }

    private Map<String, Object> checkB2BexpiredSession(String sessionId) {
        Map<String, Object> result = new HashMap<>();
        if (sessionId.isEmpty()) {
            result.put(ERROR, "Пользователь не представился");
            return result;
        } else {
            B2BSessionController controller = new B2BSessionController();
            result = controller.checkSession(sessionId);
            // в результате содержится либо ошибка либо поля со значениями
            return result;
        }
    }

    private boolean checkAvailableMethod(Map<String, Object> params, String methodName) {
        String login = getStringParam(params.get(SYSTEM_PARAM_NAME_LOGIN));
        String password = getStringParam(params.get(SYSTEM_PARAM_NAME_PASS));
        Long userAccountId = getLongParam(params, SESSIONPARAM_USERACCOUNTID);
        Map<String, Object> roleParams = new HashMap<>();
        roleParams.put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
        Map<String, Object> roleRes = this.callExternalService(B2BPOSWS, "dsUserRoleBrowseListByParam", roleParams, login, password);
        List<Map<String, Object>> roleList = ParamGetter.getListFromResultMap(roleRes);

        boolean notFound = true;
        for (int i = 0; i < roleList.size() && notFound; i++) {
            Map<String, Object> user = roleList.get(i);
            String role = getStringParam(user.get("ROLESYSNAME"));
            List<String> blockMethod = ROLE_AVAILABLE_METHODS.get(role);
            if (blockMethod != null) {
                notFound = !blockMethod.contains(methodName);
            }
        }
        return notFound;
    }

    private Response dsB2BCallService(@FormParam(FORM_PARAM_NAME) String paramsStr, String serviceName, String
            methodName, String... passedParamNames) {
        Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (paramsObj != null) {
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            Map<String, Object> obj = getMapParamName(objMap, "obj");
            String sessionId = getStringParam(obj.remove(SESSION_ID_LOWERCASE_PARAM_NAME));
            Map<String, Object> sessionParams = checkB2BSession(sessionId);
            if (!sc.sessionIsActive(sessionParams)) {
                Map<String, Object> result = new HashMap<>();
                String errorSysname = (String) sessionParams.get("ErrorSysname");
                if ((errorSysname != null) && (!errorSysname.equals(""))) {
                    result.put("ErrorSysname", errorSysname);
                }
                result.put(ERROR, SESSION_INACTIVE_ERROR);
                this.requestWorker.serializeJSON(result, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            // обновляем таймаут, так как его можно менять налету
            sc.setSessionTimeOut(this.sessionTimeOut).updateSessionData(sessionParams);
            String newSessionID = getStringParam(sessionParams.remove(SESSION_ID_PARAMNAME));
            Map<String, Object> callResult;
            Map<String, Object> params = new HashMap<>();
            if ((sessionParams.get(ERROR) == null) && (!serviceName.isEmpty()) && (!methodName.isEmpty())) {
                if (passedParamNames.length == 0) {
                    params.putAll(obj);
                } else {
                    for (String passedParamName : passedParamNames) {
                        params.put(passedParamName, obj.get(passedParamName));
                    }
                }
                params.putAll(sessionParams);
                params.put("SESSIONIDFORCALL", newSessionID);
                params.put(IS_CALL_FROM_GATE_PARAMNAME, true);
                if (checkAvailableMethod(params, methodName)) {
                    callResult = this.callExternalService(serviceName, methodName, params, isVerboseLogging);
                } else {
                    callResult = new HashMap<>();
                    callResult.put(ERROR, "Операция не позволена");
                }
            } else {
                callResult = sessionParams;
            }
            if ((callResult == null) || (callResult.isEmpty())) {
                jsonResult.setResultStatus(ERROR);
            } else {
                if (!newSessionID.isEmpty()) {
                    callResult.put(SESSION_ID_PARAMNAME, newSessionID);
                }

                this.requestWorker.serializeJSON(callResult, jsonResult);

            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    private Response dsB2BExpiredCallService(@FormParam(FORM_PARAM_NAME) String paramsStr, String serviceName, String
            methodName, String... passedParamNames) {
        Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (paramsObj != null) {
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            Map<String, Object> obj = getMapParamName(objMap, "obj");
            String sessionid = getStringParam(obj.remove("timeoutsessionid"));
            Map<String, Object> sessionParams = checkB2BexpiredSession(sessionid);
            String newSessionID = getStringParam(sessionParams.remove(SESSION_ID_PARAMNAME));
            Map<String, Object> callResult;
            Map<String, Object> params = new HashMap<>();
            if ((sessionParams.get(ERROR) == null) && (!serviceName.isEmpty()) && (!methodName.isEmpty())) {
                if (passedParamNames.length == 0) {
                    params.putAll(obj);
                } else {
                    for (String passedParamName : passedParamNames) {
                        params.put(passedParamName, obj.get(passedParamName));
                    }
                }
                params.putAll(sessionParams);
                params.put("SESSIONIDFORCALL", newSessionID);
                callResult = this.callExternalService(serviceName, methodName, params, isVerboseLogging);
            } else {
                callResult = sessionParams;
            }
            if ((callResult == null) || (callResult.isEmpty())) {
                jsonResult.setResultStatus(ERROR);
            } else {
                if (!newSessionID.isEmpty()) {
                    callResult.put(SESSION_ID_PARAMNAME, newSessionID);
                }

                this.requestWorker.serializeJSON(callResult, jsonResult);

            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        //if (isVerboseLogging) {
        //    logger.debug("jsonResult.getResultStatus() = " + jsonResult.getResultStatus());
        //    logger.debug("jsonResult.getResultJson() = " + jsonResult.getResultJson() + "\n");
        //}
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsKladrRegionBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsKladrRegionBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsKladrRegionBrowseListByParamWithMoscowAlwaysFirst");
    }

    @POST
    @Path("/dsB2BHandBookBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BHandBookBrowseEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BHandbooksBrowseEx", "prodVerId", "prodConfId");
    }

    @POST
    @Path("/dsB2BContrSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContrSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContrSave");
    }

    @POST
    @Path("/dsB2BContrSaveFixContr")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContrSaveFixContr(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContrSaveFixContr");
    }

    @POST
    @Path("/dsB2BContrLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContrLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContrLoad", "CONTRID", "PRODCONFID", "LOADCONTRSECTION", "HASH");
    }

    @POST
    @Path("/dsB2BInsComContractPrepareToSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInsComContractPrepareToSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInsComContractPrepareToSave");
    }

    @POST
    @Path("/dsB2BContrBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContrBrowseEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContractBrowseListByParamCustomMultiConditionWhereEx");
    }

    // получение списка со сведениями о доступных состояниях (в том числе с ограничениями по типу объекта)
    @POST
    @Path("/dsB2BStateBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BStateBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BStateBrowseListByParamEx");
    }

    // B2B сервисы для вызова из js. доступ к универсальному вызову закрыт.
    @POST
    @Path("/dsB2BMenuBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMenuBrowseEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsMenuBrowseListByParam");
    }

    @POST
    @Path("/dsB2BContractSetSignDate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContractSetSignDate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContractSetSignDate");
    }

    @POST
    @Path("/dsB2BContractUniversalLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContractUniversalLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContractUniversalLoad");
    }

    @POST
    @Path("/dsB2BPrintDocuments")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrintDocuments(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPrintDocuments");
    }

    @POST
    @Path("/dsB2BPrintAndSendAllDocument")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrintAndSendAllDocument(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BPrintAndSendAllDocument");
    }

    @POST
    @Path("/dsB2BSendNotificationEMail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSendNotificationEMail(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        //return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSendNotificationEMail"); // !только для отладки!
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BSendNotificationEMail");
    }

    @POST
    @Path("/dsB2BOldPrintAndSendAllDocument")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BOldPrintAndSendAllDocument(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNBIVSBERPOSWS, "dsCallPringAndSendEx");
    }

    @POST
    @Path("/getB2BProductVersionList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getB2BProductVersionList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductVersionBrowseListByParamCustomMultiConditionWhereEx");
    }

    @POST
    @Path("/getB2BProductList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getB2BProductList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductVersionBrowseListByParamEx");
    }

    @POST
    @Path("/getB2BProductListForSaleReport")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getB2BProductListForSaleReport(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductVersionBrowseListForSaleReportByParamEx");
    }

    @POST
    @Path("/getB2BProductUploadPath")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getB2BProductUploadPath(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductGetExportUploadPath");
    }

    @POST
    @Path("/B2BLoadHandBookByProduct")
    @Produces(MediaType.APPLICATION_JSON)
    public Response B2BLoadHandBookByProduct(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BLoadHandBookByProduct");
    }

    @POST
    @Path("/prepareReprintB2Bdocument")
    @Produces(MediaType.APPLICATION_JSON)
    public Response prepareReprintB2Bdocument(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BPrepareRePrintDocuments");
    }

    @POST
    @Path("/printB2Bdocument")
    @Produces(MediaType.APPLICATION_JSON)
    public Response printB2Bdocument(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BPrintDocuments");
    }

    @POST
    @Path("/changeB2BContractState")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeB2BContractState(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContract_State_MakeTrans");
    }

    @POST
    @Path("/changeB2BContractStateEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeB2BContractStateEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BcontractMakeTrans");
    }

    @POST
    @Path("/changeB2BContractStateUWCheckEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeB2BContractStateUWCheckEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BcontractMakeTransUWCheck");
    }

    @POST
    @Path("/sendB2BdocPack")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendB2BdocPack(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsSendDocumentsPackage");
    }

    @POST
    @Path("/getB2BdocPack")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getB2BdocPack(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsGetDocumentsPackage");
    }

    @POST
    @Path("/dsB2BProductBinaryDocumentBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductBinaryDocumentBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductBinaryDocumentBrowseListByParam");
    }

    @POST
    @Path("/dsB2BProductBinaryDocumentBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductBinaryDocumentBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductBinaryDocumentBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BContractDocumentBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContractDocumentBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContractDocumentBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BExportDataDocumentBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BExportDataDocumentBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BExportDataDocumentBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BContractDocumentDeleteEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContractDocumentDeleteEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContractDocumentDeleteEx");
    }

    @POST
    @Path("/dsB2BProductReportBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductReportBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductReportBrowseListByParam");
    }

    @POST
    @Path("/dsB2BProductReportBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductReportBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductReportBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BProductDefaultValueBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductDefaultValueBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductDefaultValueBrowseListByParam");
    }

    @POST
    @Path("/dsB2BParticipantLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BParticipantLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BParticipantLoad");
    }

    @POST
    @Path("/dsB2BParticipantSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BParticipantSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BParticipantSave");
    }

    @POST
    @Path("/dsB2BPaymentFactBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPaymentFactBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPaymentFactBrowseListByParam");
    }

    @POST
    @Path("/dsB2BClientActionLogBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BClientActionLogBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, BIVSBERPOSWS, "dsInsClientActionLogBrowseListByParamEx");
    }

    // загрузка списка банковских выписок для продукта Ипотека 900
    @POST
    @Path("/dsB2BMort900BrowseListByParamCustomeWhereEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMort900BrowseListByParamCustomeWhereEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMort900BrowseListByParamCustomeWhereEx");
    }

    @POST
    @Path("/dsB2BBankStateBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankStateBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankStateBrowseListByParam");
    }

    @POST
    @Path("/dsB2BBankCashFlowCountStateString")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankCashFlowCountStateString(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankCashFlowCountStateString");
    }

    @POST
    @Path("/dsB2BBankCashFlowCountTypeModifyString")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankCashFlowCountTypeModifyString(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankCashFlowCountTypeModifyString");
    }

    @POST
    @Path("/dsB2B2BCashFlowSetTypeModifyString")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2B2BCashFlowSetTypeModifyString(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2B2BCashFlowSetTypeModifyString");
    }

    @POST
    @Path("/dsB2BBankStateUpdateCountString")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankStateUpdateCountString(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankStateUpdateCountString");
    }

    @POST
    @Path("/dsB2BBankStateTemplateBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankStateTemplateBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankStateTemplateBrowseListByParam");
    }

    @POST
    @Path("/dsB2BBankStateCreateEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankStateCreateEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankStateCreateEx");
    }

    @POST
    @Path("/dsB2BBankStateDocumentBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankStateDocumentBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankStateDocumentBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BBankCashFlowBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankCashFlowBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankCashFlowBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BBankPurposeDetailBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankPurposeDetailBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankPurposeDetailBrowseListByParam");
    }

    @POST
    @Path("/dsB2BpcfPurposeDetailBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BpcfPurposeDetailBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BpcfPurposeDetailBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BBankStateTemplatePurposeDetailBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankStateTemplatePurposeDetailBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankStateTemplatePurposeDetailBrowseListByParam");
    }

    @POST
    @Path("/dsB2BBankPurposeDetailSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankPurposeDetailSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankPurposeDetailSave");
    }

    // Обновление банковской выписки (при изменении шаблона в окне редактирования детализации назначения платежа)
    @POST
    @Path("/dsB2BBankCashFlowUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankCashFlowUpdate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankCashFlowUpdate");
    }

    // Проверка детализации назначения платежа (для angular-интерфейса)
    @POST
    @Path("/dsB2BMort900CheckPaymentPurpose")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMort900CheckPaymentPurpose(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMort900CheckPaymentPurpose");
    }

    @POST
    @Path("/dsB2BBankCashFlowMakeTrans")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankCashFlowMakeTrans(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankCashFlowMakeTrans");
    }

    @POST
    @Path("/dsB2BBankStatementMakeTrans")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBankStatementMakeTrans(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBankStatementMakeTrans");
    }

    // создание нового объекта выгрузки
    @POST
    @Path("/dsB2BExportDataCreateEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BExportDataCreate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BExportDataCreateEx");
    }

    // получение сведений объекта выгрузки по его идентификатору
    @POST
    @Path("/dsB2BExportDataBrowseByID")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BExportDataBrowseByID(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BExportDataBrowseByID");
    }

    // получение сведений списка объектов (или объекта) выгрузки для angular-грида
    @POST
    @Path("/dsB2BExportDataBrowseListByParamCustomeWhereEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BExportDataBrowseListByParamCustomeWhereEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BExportDataBrowseListByParamCustomeWhereEx");
    }

    // получение содержимого объекта вызгузки для angular-грида
    @POST
    @Path("/dsB2BExportDataContentDataBrowseListByParamsEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BExportDataContentDataBrowseListByParamsEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BExportDataContentDataBrowseListByParamsEx");
    }

    // получение сведений шаблона (или списка шаблонов) для объекта выгрузки
    @POST
    @Path("/dsB2BExportDataTemplateBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BExportDataTemplateBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BExportDataTemplateBrowseListByParam");
    }

    // перевод состояния объекта выгрузки
    @POST
    @Path("/dsB2BExportDataMakeTrans")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BExportDataMakeTrans(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BExportDataMakeTrans");
    }

    // формирование файла со сведениями экспорта
    @POST
    @Path("/dsB2BExportDataCreateReport")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BExportDataCreateReport(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, "fileb2bposws", "dsB2BExportDataCreateReport");
        //return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BExportDataCreateReport"); //!только для отладки!
    }

    @POST
    @Path("/dsB2BProductUniversalLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductUniversalLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductUniversalLoad");
    }

    @POST
    @Path("/dsB2BProductUniversalSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductUniversalSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductUniversalSave");
    }

    @POST
    @Path("/dsB2BProductExportToXML")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductExportToXML(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductExportToXML");
    }

    @POST
    @Path("/dsB2BProductExportToXMLFile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductExportToXMLFile(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductExportToXMLFile");
    }

    @POST
    @Path("/dsB2BProductImportFromXML")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductImportFromXML(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductImportFromXML");
    }

    @POST
    @Path("/dsB2BProductImportFromXMLFile")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductImportFromXMLFile(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductImportFromXMLFile");
    }

    // промежуточный метод-посредник для вызова методов редактора продуктов
    // используется в doProductEditorCall (app.js) angular-интерфейса b2b
    // (применим только для методов с действительными именами вида 'dsB2BProduct*')
    @POST
    @Path("/dsB2BProductEditorMethodCall")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductEditorMethodCall(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductEditorMethodCall");
    }

    @POST
    @Path("/dsB2BReportBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BReportBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BReportBrowseListByParam");
    }

    @POST
    @Path("/dsB2BReportCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BReportCreate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BReportCreate");
    }

    @POST
    @Path("/dsB2BReportUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BReportUpdate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BReportUpdate");
    }

    @POST
    @Path("/dsB2BReportDelete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BReportDelete(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BReportDelete");
    }

    @POST
    @Path("/dsB2BTermBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BTermBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BTermBrowseListByParam");
    }

    @POST
    @Path("/dsB2BTermCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BTermCreate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BTermCreate");
    }

    @POST
    @Path("/dsB2BTermUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BTermUpdate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BTermUpdate");
    }

    @POST
    @Path("/dsB2BTermDelete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BTermDelete(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BTermDelete");
    }

    // запрос списка договоров по основной деятельности для angular-грида
    @POST
    @Path("/dsB2BPrimaryActivityContractBrowseListByParamCustomWhereEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrimaryActivityContractBrowseListByParamCustomWhereEx(@FormParam(FORM_PARAM_NAME) String
                                                                                       paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPrimaryActivityContractBrowseListByParamCustomWhereEx");
    }

    // запрос списка доступных конрагентов для выбора при создании договоров по основной деятельности в angular-интерфейсе
    @POST
    @Path("/dsB2BPrimaryActivityGetContragentsList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrimaryActivityGetContragentsList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPrimaryActivityGetContragentsList");
    }

    // запрос доступных конрагентов для выбора при создании/редактировании отчетов агентов в angular-интерфейсе
    // (доп. ограничение - будут возвращены только те контрагенты, для которых существуют агентские договора И текущий пользователь является участником этих договоров)
    @POST
    @Path("/dsB2BPrimaryActivityGetContragentsListByUserCreds")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrimaryActivityGetContragentsListByUserCreds(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPrimaryActivityGetContragentsListByUserCreds");
    }

    // запрос доступных агентских договоров для выбора при создании/редактировании отчетов агентов в angular-интерфейсе
    @POST
    @Path("/dsB2BMainActivityContractBaseBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMainActivityContractBaseBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMainActivityContractBaseBrowseListByParam");
    }

    // запрос списка рисков по продукту для выбора при работе с агентским договором в angular-интерфейсе
    @POST
    @Path("/dsB2BProductStructureRiskBrowseListByProdVerID")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductStructureRiskBrowseListByProdVerID(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductStructureRiskBrowseListByProdVerID");
    }

    // запрос дерева страховой структуры по продукту для выбора при работе с агентским договором в angular-интерфейсе
    @POST
    @Path("/dsB2BProductStructureBaseBrowseListByProductSysName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductStructureBaseBrowseListByProductSysName(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductStructureBaseBrowseListByProductSysName");
    }

    // запрос валют страховой суммы по продукту для выбора при работе с агентским договором в angular-интерфейсе
    @POST
    @Path("/dsB2BProductInsAmCurrencyBrowseListByProductSysName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductInsAmCurrencyBrowseListByProductSysName(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductInsAmCurrencyBrowseListByProductSysName");
    }

    // запрос валют премии по продукту для выбора при работе с агентским договором в angular-интерфейсе
    @POST
    @Path("/dsB2BProductPremiumCurrencyBrowseListByProductSysName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductPremiumCurrencyBrowseListByProductSysName(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductPremiumCurrencyBrowseListByProductSysName");
    }

    // запрос списка каналов продаж по продукту для выбора при работе с агентским договором в angular-интерфейсе
    @POST
    @Path("/dsB2BProductSalesChannelBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductSalesChannelBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductSalesChannelBrowseListByParamEx");
    }

    // вызов универсального сохранения договора по основной деятельности
    @POST
    @Path("/dsB2BPrimaryActivityContractSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrimaryActivityContractSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPrimaryActivityContractSave");
    }

    // вызов универсальной загрузки договора по основной деятельности
    @POST
    @Path("/dsB2BPrimaryActivityContractLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrimaryActivityContractLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPrimaryActivityContractLoad");
    }

    // запрос списка отчетов агента по договорам основной деятельности для angular-грида
    @POST
    @Path("/dsB2BPrimaryActivityContractAgentReportBrowseListByParamCustomWhereEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrimaryActivityContractAgentReportBrowseListByParamCustomWhereEx
    (@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPrimaryActivityContractAgentReportBrowseListByParamCustomWhereEx");
    }

    // запрос списка продуктов, указанных в конкретном договоре по основной деятельности для выбора при создании/редактировании отчетов агентов в angular-интерфейсе
    @POST
    @Path("/dsB2BMainActivityContractProductBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMainActivityContractProductBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMainActivityContractProductBrowseListByParamEx");
    }

    // сохранение (создание нового по кнопки 'Создать новый' в angular-интерфейсе или обновление существующего) отчета агента и загрузка его сведений для возврата на интерфейс
    @POST
    @Path("/dsB2BMainActivityContractAgentReportSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMainActivityContractAgentReportSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMainActivityContractAgentReportSave");
    }

    // запрос списка содержимого отчета агента по договорам основной деятельности для angular-грида на странице 'Содержимое' интерфейса отчета агента
    @POST
    @Path("/dsB2BMainActivityContractAgentReportContentBrowseListByParamCustomWhereEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMainActivityContractAgentReportContentBrowseListByParamCustomWhereEx
    (@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMainActivityContractAgentReportContentBrowseListByParamCustomWhereEx");
    }

    // изменение флага 'Исключен' записи содержимого отчета агента для действий 'Исключить/Включить договор' на странице 'Содержимое' angular-интерфейса отчета агента
    @POST
    @Path("/dsB2BMainActivityContractAgentReportContentUpdateRemoved")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMainActivityContractAgentReportContentUpdateRemoved(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMainActivityContractAgentReportContentUpdateRemoved");
    }

    // запуск регламентного задания по автоматическому формированию отчетов агентов
    @POST
    @Path("/dsB2BMainActivityContractAgentReportsProcess")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMainActivityContractAgentReportsProcess(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMainActivityContractAgentReportsProcess");
    }

    // запрос допустимых действий для конкретного отчета агента по текущему пользователю
    @POST
    @Path("/dsB2BPrimaryActivityContractAgentReportGetAvailableActions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrimaryActivityContractAgentReportGetAvailableActions(@FormParam(FORM_PARAM_NAME) String
                                                                                       paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPrimaryActivityContractAgentReportGetAvailableActions");
    }

    // запрос допустимых действий для предполагаемого отчета агента по текущему пользователю и известному агентскому договору
    @POST
    @Path("/dsB2BPrimaryActivityContractGetAvailableActions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrimaryActivityContractGetAvailableActions(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPrimaryActivityContractGetAvailableActions");
    }

    // запрос на выполнение конкретного действия для указанного отчета агента от имени текущего пользователя
    @POST
    @Path("/dsB2BPrimaryActivityContractAgentReportDoAction")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrimaryActivityContractAgentReportDoAction(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPrimaryActivityContractAgentReportDoAction");
    }

    // запрос истории состояний отчета агента по договорам основной деятельности для angular-грида на странице 'Основные' интерфейса отчета агента
    @POST
    @Path("/dsB2BPrimaryActivityContractAgentReportStateHistoryBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrimaryActivityContractAgentReportStateHistoryBrowseListByParamEx
    (@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPrimaryActivityContractAgentReportStateHistoryBrowseListByParamEx");
    }

    // запуск импорта сведений о террористах
    @POST
    @Path("/dsB2BTerroristsImportStart")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BTerroristsImportStart(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BTerroristsImportStart");
    }

    // запрос состояния операции импорта сведений о террористах
    @POST
    @Path("/dsB2BTerroristsImportState")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BTerroristsImportState(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BTerroristsImportState");
    }

    // запуск импорта сведений о недействительных паспортах
    @POST
    @Path("/dsB2BPassportsImportStart")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPassportsImportStart(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BPassportsImportStart");
    }

    // запрос состояния операции импорта сведений о недействительных паспортах
    @POST
    @Path("/dsB2BPassportsImportState")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPassportsImportState(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BPassportsImportState");
    }

    // получение сведений справочника по имени справочника (HANDBOOKNAME)
    // необязательные параметры:
    // HANDBOOKDATAVERSIONNAME - имя версии справочника (если не указано, будет использоваться 'Версия 1')
    // HANDBOOKDATAPARAMS - мапа с параметрами для ограничения запрашиваемых из справочника записей (имена параметров должны соответствовать именам свойств справочника, которые описаны в HandbookDescriptor)
    // ReturnListOnly - если true, то возвращает только список с записями справочника; иначе - возвращает мапу (ключ - имя справочника, значение - список с записями справочника)
    @POST
    @Path("/dsB2BHandbookDataBrowseByHBName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BHandbookDataBrowseByHBName(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BHandbookDataBrowseByHBName");
    }

    // загрузка данных продукта по системному имени (PRODSYSNAME)
    @POST
    @Path("/dsB2BProductBrowseBySysName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductBrowseBySysName(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductBrowseBySysName");
    }

    @POST
    @Path("/dsB2BRightDecisionCalc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BRightDecisionCalc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BRightDecisionCalc");
    }

    @POST
    @Path("/dsB2BRightDecisionCalcByContrMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BRightDecisionCalcByContrMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BRightDecisionCalcByContrMap");
    }

    @POST
    @Path("/dsB2BFirstStepCalc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BFirstStepCalc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BFirstStepCalc");
    }

    @POST
    @Path("/dsB2BFirstStepCalcByContrMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BFirstStepCalcByContrMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BFirstStepCalcByContrMap");
    }

    @POST
    @Path("/dsB2BCapitalCalc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BCapitalCalc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BCapitalCalc");
    }

    @POST
    @Path("/dsB2BCapitalCalcByContrMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BCapitalCalcByContrMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BCapitalCalcByContrMap");
    }

    @POST
    @Path("/dsB2BInvestNum1Calc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestNum1Calc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestNum1Calc");
    }

    @POST
    @Path("/dsB2BInvestNum1CalcByContrMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestNum1CalcByContrMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestNum1CalcByContrMap");
    }

    @POST
    @Path("/dsB2BSmartPolicyCalc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSmartPolicyCalc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSmartPolicyCalc");
    }

    @POST
    @Path("/dsB2BSmartPolicyContractUnderwritingCheck")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSmartPolicyContractUnderwritingCheck(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSmartPolicyContractUnderwritingCheck");
    }

    @POST
    @Path("/dsB2BSmartPolicyLightCalc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSmartPolicyLightCalc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSmartPolicyLightCalc");
    }

    @POST
    @Path("/dsB2BInvestNum1GetAvailableStrategyForSale")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestNum1GetAvailableStrategyForSale(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestNum1GetAvailableStrategyForSale");
    }

    @POST
    @Path("/dsB2BInvestCouponCalcByContrMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestCouponCalcByContrMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestCouponCalcByContrMap");
    }

    @POST
    @Path("/dsB2BNewHorizonsCalcByContrMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BNewHorizonsCalcByContrMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BNewHorizonsCalcByContrMap");
    }

    @POST
    @Path("/dsB2BInvestCouponCalc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestCouponCalc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestCouponCalc");
    }

    @POST
    @Path("/dsB2BInvestCouponGetAvailableStrategyForSale")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestCouponGetAvailableStrategyForSale(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestCouponGetAvailableStrategyForSale");
    }

    @POST
    @Path("/dsB2BBorrowerProtectCalc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBorrowerProtectCalc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBorrowerProtectCalc");
    }

    @POST
    @Path("/dsB2BBorrowerProtectCalcByContrMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBorrowerProtectCalcByContrMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBorrowerProtectCalcByContrMap");
    }

    @POST
    @Path("/dsB2BBorrowerProtectLongTermCalcByContrMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBorrowerProtectLongTermCalcByContrMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBorrowerProtectLongTermCalcByContrMap");
    }

    @POST
    @Path("/dsB2BSmartPolicyCalcByContrMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSmartPolicyCalcByContrMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSmartPolicyCalcByContrMap");
    }

    @POST
    @Path("/dsB2BSmartPolicyLightCalcByContrMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSmartPolicyLightCalcByContrMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSmartPolicyLightCalcByContrMap");
    }

    @POST
    @Path("/dsB2BSmartPolicyCustomGetAvailableStrategyForSale")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSmartPolicyCustomGetAvailableStrategyForSale(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSmartPolicyCustomGetAvailableStrategyForSale");
    }

    @POST
    @Path("/dsB2BSmartPolicyLightCustomGetAvailableStrategyForSale")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSmartPolicyLightCustomGetAvailableStrategyForSale(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSmartPolicyLightCustomGetAvailableStrategyForSale");
    }

    // метод для отладочного тестирования провайдера данных отчетов из angular-интерфейса
    // todo: убрать возможость вызова с интерфейсов по завершению тестирования провайдера данных и разработки отчетов
    @POST
    @Path("/dsB2BSberLifePrintDocDataProvider")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSberLifePrintDocDataProvider(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSberLifePrintDocDataProvider");
    }

    // метод для отладочного вызова обработчика выгрузок локально
    // todo: убрать возможость вызова с интерфейсов по завершению разработки выгрузки
    @POST
    @Path("/dsB2BExportDataProcess")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BExportDataProcess(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BExportDataProcess");
    }

    // метод для кнопки 'Завершить прикрепление доп. документов' на странице печати, направит уведомления андеррайтеру и пр
    @POST
    @Path("/dsB2BContractUWNotifyAdditionalDocsAttached")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContractUWNotifyAdditionalDocsAttached(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BContractUWNotifyAdditionalDocsAttached");
        //return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContractUWNotifyAdditionalDocsAttached"); //!только для отладки!
    }

    // построение структуры страхового продукта для договора
    @POST
    @Path("/dsB2BUpdateContractInsuranceProductStructure")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BUpdateContractInsuranceProductStructure(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BUpdateContractInsuranceProductStructure");
    }

    // проверка на недействительный паспорт и террориста
    @POST
    @Path("/dsSAL_DoCheck")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsSAL_DoCheck(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = new HashMap<>(getMapParamName(contract.copyObjFromEntityToMap(), "obj"));
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsSAL_DoCheck", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    // построение структуры договора и применение промокода к ней
    @POST
    @Path("/dsB2BPrepareParamsAndApplyDiscount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BPrepareParamsAndApplyDiscount(@FormParam("objMap") String contractParam) {
        Obj contract = this.requestWorker.deserializeJSON(contractParam, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (contract != null) {
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> contrMap = getMapParamName(contract.copyObjFromEntityToMap(), "obj");
            params.put("CONTRMAP", contrMap);
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BPrepareParamsAndApplyDiscount", params);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    // B2B сервисы для вызова из js. доступ к универсальному вызову закрыт.
    private boolean checkSmsCode(Long userId, String smsCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("USERID", userId);
        params.put("SMSCODE", smsCode);
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> checkResult = this.callExternalService(PAWS, "dsPACheckSmsCode", params, isVerboseLogging);
        boolean result;
        if (checkResult != null) {
            result = getBooleanParam(checkResult.get("ISSMSCODEVALID"), false);
        } else {
            result = false;
        }
        return result;
    }

    /*
    private Response dsPACallService(String paramsStr, String serviceName, String methodName) {
        // по-умолчанию проверка СМС-кода при вызове PA-методов не требуется
        return dsPACallService(paramsStr, serviceName, methodName, false);
    }

    */
    /*
    private String makeSessionId(String userIDStr, String smsCodeSHA) {
        GregorianCalendar gcToday = new GregorianCalendar();
        gcToday.setTime(new Date());
        String sessionSt = userIDStr + divider + smsCodeSHA + divider + String.valueOf(gcToday.getTimeInMillis());
        StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        return scu.encrypt(sessionSt);
    }
    */

    // проверка PA-сессии без проверки СМС-кода
    /*
    private Map<String, Object> checkPASession(String sessionIDCoded) {
        return checkPASession(sessionIDCoded, null);
    }

    // проверка PA-сессии (если smsCode не null, то совместно с проверкой СМС-кода)
    // метод сломан, не используется, спросить Белугина
    private Map<String, Object> checkPASession(String sessionIDCoded, String smsCode) {
        Map<String, Object> result = new HashMap<>();
        StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        SessionController controller = new B2BSessionController();
        Map<String, Object> sessionParams = controller.checkSession(sessionIDCoded);
        if (B2BSessionController.sessionWithError(sessionParams)) {
            // либо ошибка либо нормально расшифрованная мапа
            return sessionParams;
            }
        String userIdStr = (String) sessionParams.get(THIS_SESSION_TYPE);
        String smsCodeSHA = (String) sessionParams.get(THIS_SESSION_TYPE);

        Long userID = Long.valueOf(userIdStr);
                result.put("CURRENT_USERID", userID);
                if (smsCode != null) {
                    if (!checkSmsCode(userID, smsCode)) {
                result.put(ERROR, "Неверный код СМС");
                    }
                }
        result.put(SESSION_ID_PARAMNAME, makeSessionId(userIdStr, smsCodeSHA));
        return result;

    }

    */
    /*
    private Response dsPACallService(String paramsStr, String serviceName, String methodName, boolean isSMSCheckNeeded) {
        Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class
        );
        JsonResult jsonResult = new JsonResult();
        if (paramsObj != null) {
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
            String sessionid = getStringParam(obj.get(SESSION_ID_LOWERCASE_PARAM_NAME));
            Map<String, Object> params = new HashMap<String, Object>();
            params.putAll(obj);

            String smsCode;
            if (isSMSCheckNeeded) {
                // значение СМС-кода для проверки в checkPASession или пустая строка
                smsCode = getStringParam(params.get("SMSCODE"));
            } else {
                // null (в checkPASession будет использоваться как признак пропуска проверки СМС-кода)
                smsCode = null;
            }

            Map<String, Object> sessionParams = checkPASession(sessionid, smsCode);
            String newSessionID = getStringParam(sessionParams.remove(SESSION_ID_PARAMNAME));

            Map<String, Object> сallResult;
            params.put("SESSIONIDFORCALL", newSessionID);
            if ((sessionParams.get(ERROR) == null) && (!serviceName.isEmpty()) && (!methodName.isEmpty())) {
                //Map<String, Object> methodCallResult = this.callExternalService("paws", "dsPACallService", params, isVerboseLogging);
                params.putAll(sessionParams);
                сallResult = this.callExternalService(serviceName, methodName, params, isVerboseLogging);
            } else {
                сallResult = sessionParams;
            }

            if ((сallResult == null) || (сallResult.isEmpty())) {
                jsonResult.setResultStatus(ERROR);
            } else {
                if (!newSessionID.isEmpty()) {
                    сallResult.put(SESSION_ID_PARAMNAME, newSessionID);
                }
                this.requestWorker.serializeJSON(сallResult, jsonResult);

            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        //if (isVerboseLogging) {
        //    logger.debug("jsonResult.getResultStatus() = " + jsonResult.getResultStatus());
        //    logger.debug("jsonResult.getResultJson() = " + jsonResult.getResultJson() + "\n");
        //}
        return Response.status(Response.Status.OK).entity(jsonResult).build();

    }
    */

    //private Response dsPACallService(@FormParam(FORM_PARAM_NAME) String paramsStr, String serviceName, String methodName) {
    //    Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
    //    JsonResult jsonResult = new JsonResult();
    //    if (paramsObj != null) {
    //        Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
    //        Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
    //        String sessionid = getStringParam(obj.get(SESSION_ID_LOWERCASE_PARAM_NAME));
    //        Map<String, Object> params = new HashMap<String, Object>();
    //        params.putAll(obj);
    //        params.put(SESSION_ID_PARAMNAME, sessionid);
    //        params.put("SERVICENAME", serviceName);
    //        params.put("METHODNAME", methodName);
    //        Map<String, Object> methodCallResult = this.callExternalService("paws", "dsPACallService", params, isVerboseLogging);
    //        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
    //            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
    //        } else {
    //            jsonResult.setResultStatus(ERROR);
    //        }
    //    } else {
    //        jsonResult.setResultJson(JsonResult.RESULT_ERROR);
    //    }
    //    return Response.status(Response.Status.OK).entity(jsonResult).build();
    //}
    //private Response dsPACallServiceWithCheckSmsCode(@FormParam(FORM_PARAM_NAME) String paramsStr, String serviceName, String methodName) {
    //    Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
    //    JsonResult jsonResult = new JsonResult();
    //    if (paramsObj != null) {
    //        Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
    //        Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
    //        String sessionid = getStringParam(obj.get(SESSION_ID_LOWERCASE_PARAM_NAME));
    //        Map<String, Object> params = new HashMap<String, Object>();
    //        params.putAll(obj);
    //        params.put(SESSION_ID_PARAMNAME, sessionid);
    //        params.put("SERVICENAME", serviceName);
    //        params.put("METHODNAME", methodName);
    //        Map<String, Object> methodCallResult = this.callExternalService("paws", "dsPACallServiceWithCheckSmsCode", params, isVerboseLogging);
    //        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
    //            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
    //        } else {
    //            jsonResult.setResultStatus(ERROR);
    //        }
    //    } else {
    //        jsonResult.setResultJson(JsonResult.RESULT_ERROR);
    //    }
    //    return Response.status(Response.Status.OK).entity(jsonResult).build();
    //}
    /*
    private Response dsPACallRegisterOrLoginService(String paramsStr, String methodName, String... requiredParamsNames) {
        Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class
        );
        JsonResult jsonResult = new JsonResult();
        if (paramsObj != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
            boolean isParamsMissing = false;
            String missingParamName = "";
            boolean isBadVerifyCode = false;

            if (requiredParamsNames.length == 0) {
                params.putAll(obj);
            } else {
                for (String paramName : requiredParamsNames) {
                    String[] paramAltNames = paramName.split("\\|\\|"); // "FIRSTPARAM||SECONDPARAM"
                    String passedParamName = paramAltNames[0];

                    String paramValue = "";
                    for (String paramAltName : paramAltNames) {
                        paramValue = getStringParam(obj.get(paramAltName));
                        if (!paramValue.isEmpty()) {
                            passedParamName = paramAltName;
                            break;
                        }
                    }

                    //String paramValue = getStringParam(obj.get(paramName));
                    if (paramValue.isEmpty()) {
                        isParamsMissing = true;
                        missingParamName = paramName;
                        break;
                    }

                    if ("VERIFYCODE".equals(passedParamName)) {
                        try {
                            String verifyCode = getStringParam(paramValue);
                            String verifyCodeDecoded = base64DecodeUrlSafe(verifyCode);
                            StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
                            String verifyCodeDecrypted = scu.decrypt(verifyCodeDecoded);
                            Long userId = Long.valueOf(verifyCodeDecrypted);
                            params.put("USERID", userId);
                        } catch (Exception e) {
                            isBadVerifyCode = true;
                        }
                    } else {
                        params.put(passedParamName, paramValue);
                    }

                }
            }

            if (isParamsMissing) {
                String serviceName = PAWS;
                processResultStatusEmptyLoginPass(jsonResult, methodName, serviceName, missingParamName, obj);
            } else {
                Map<String, Object> methodCallResult = null;
                if (isBadVerifyCode) {
                    HashMap<String, Object> errorMap = new HashMap<String, Object>();
                    errorMap.put(ERROR, "Неверный код подтверждения");
                    methodCallResult = new HashMap<String, Object>();
                    methodCallResult.put(Constants.RESULT, errorMap);
                } else if (methodName.contains(">")) {
                    String[] methodNames = methodName.split("\\>"); // "FIRSTMETHODNAME>SECONDMETHODNAME"
                    for (String chainMethodName : methodNames) {
                        String userID = getStringParam(params.get("USERID"));
                        if (!userID.isEmpty()) {
                            try {
                                StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
                                String userIDEncrypted = scu.encrypt(userID);
                                String userIDEncoded = base64EncodeUrlSafe(userIDEncrypted);
                                params.put("ENCODEDUSERID", userIDEncoded);
                            } catch (Exception ex) {
                                logger.error("При кодировании параметра USERID возникло исключние: ", ex);
                            }
                        }
                        methodCallResult = this.callExternalService(PAWS, chainMethodName, params, isVerboseLogging);
                        params = (Map<String, Object>) methodCallResult.get(Constants.RESULT);
                    }
                } else {
                    methodCallResult = this.callExternalService(PAWS, methodName, params, isVerboseLogging);
                }
                if ((methodCallResult == null) || (methodCallResult.isEmpty())) {
                    jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
                } else {
                    this.requestWorker.serializeJSON(methodCallResult, jsonResult);
                }
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    */
    /*
    // PA сервисы для вызова из js. доступ к универсальному вызову закрыт.
    // сохранение одного заявления, для вызова из angular-интерфейса ЛК
    @POST
    @Path("/dsB2BLossCompReqSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLossCompReqSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BLossCompReqSave");
    }

    // загрузка одного заявления по идентификатору, для вызова из angular-интерфейса ЛК
    @POST
    @Path("/dsB2BLossCompReqLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLossCompReqLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BLossCompReqLoad");
    }

    // загрузка основных сведений заявлений по параметрам, для формирования списка в разделе "Страховые события" angular-интерфейса ЛК
    @POST
    @Path("/dsB2BLossCompReqBrowseListByParamExBrief")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLossCompReqBrowseListByParamExBrief(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BLossCompReqBrowseListByParamExBrief");
    }

    */
    // изменение продукта для журнала продуктов
    @POST
    @Path("/dsB2BProductUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductUpdate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductUpdate");
    }

    // изменение версии продукта для журнала продуктов
    @POST
    @Path("/dsB2BProductVersionUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductVersionUpdate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductVersionUpdate");
    }

    // сохранение списка значений продукта
    @POST
    @Path("/dsB2BProductDefaultValuesSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductDefaultValuesSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductDefaultValuesSave");
    }

    /*
    @POST
    @Path("/dsPALogin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPALogin(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        //Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        //JsonResult jsonResult = new JsonResult();
        //if (paramsObj != null) {
        //    Map<String, Object> params = new HashMap<String, Object>();
        //    Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
        //    Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
        //    String phoneNumber = getStringParam(obj.get("PHONENUMBER"));
        //    String eMail = getStringParam(obj.get("EMAIL"));
        //    if (!(phoneNumber.isEmpty() && eMail.isEmpty())) {
        //        params.put("PHONENUMBER", phoneNumber);
        //        params.put("EMAIL", eMail);
        //        Map<String, Object> methodCallResult = this.callExternalService("paws", "dsPALogin", params);
        //        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
        //            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
        //        } else {
        //            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
        //        }
        //    } else {
        //        jsonResult.setResultStatus(JsonResult.RESULT_empty_LoginPass);
        //    }
        //} else {
        //    jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        //}
        //return Response.status(Response.Status.OK).entity(jsonResult).build();
        return dsPACallRegisterOrLoginService(paramsStr, "dsPALogin", "PHONENUMBER||EMAIL");
    }

    @POST
    @Path("/dsPALoginResendSMSCode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPALoginResendSMSCode(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        //Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        //JsonResult jsonResult = new JsonResult();
        //if (paramsObj != null) {
        //    Map<String, Object> params = new HashMap<String, Object>();
        //    Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
        //    Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
        //    String phoneNumber = getStringParam(obj.get("PHONENUMBER"));
        //    String eMail = getStringParam(obj.get("EMAIL"));
        //    if (!(phoneNumber.isEmpty() && eMail.isEmpty())) {
        //        params.put("PHONENUMBER", phoneNumber);
        //        params.put("EMAIL", eMail);
        //        Map<String, Object> methodCallResult = this.callExternalService("paws", "dsPALoginResendSMSCode", params);
        //        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
        //            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
        //        } else {
        //            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
        //        }
        //    } else {
        //        jsonResult.setResultStatus(JsonResult.RESULT_empty_LoginPass);
        //    }
        //} else {
        //    jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        //}
        //return Response.status(Response.Status.OK).entity(jsonResult).build();
        return dsPACallRegisterOrLoginService(paramsStr, "dsPALoginResendSMSCode", "PHONENUMBER||EMAIL");
    }

    @POST
    @Path("/dsPALoginEnterSMSCode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPALoginEnterSMSCode(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        //Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        //JsonResult jsonResult = new JsonResult();
        //if (paramsObj != null) {
        //    Map<String, Object> params = new HashMap<String, Object>();
        //    Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
        //    Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
        //    String phoneNumber = getStringParam(obj.get("PHONENUMBER"));
        //    String eMail = getStringParam(obj.get("EMAIL"));
        //    String smsCode = getStringParam(obj.get("SMSCODE"));
        //    if (!(phoneNumber.isEmpty() && eMail.isEmpty())) {
        //        params.put("PHONENUMBER", phoneNumber);
        //        params.put("EMAIL", eMail);
        //        params.put("SMSCODE", smsCode);
        //        Map<String, Object> methodCallResult = this.callExternalService("paws", "dsPALoginEnterSMSCode", params);
        //        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
        //            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
        //        } else {
        //            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
        //        }
        //    } else {
        //        jsonResult.setResultStatus(JsonResult.RESULT_empty_LoginPass);
        //    }
        //} else {
        //    jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        //}
        //return Response.status(Response.Status.OK).entity(jsonResult).build();
        return dsPACallRegisterOrLoginService(paramsStr, "dsPALoginEnterSMSCode", "PHONENUMBER||EMAIL", "SMSCODE");
    }

    @POST
    @Path("/dsPARegister")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPARegister(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        //Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        //JsonResult jsonResult = new JsonResult();
        //if (paramsObj != null) {
        //    Map<String, Object> params = new HashMap<String, Object>();
        //    Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
        //    Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
        //    String surName = getStringParam(obj.get("SURNAME"));
        //    String name = getStringParam(obj.get("NAME"));
        //    String eMail = getStringParam(obj.get("EMAIL"));
        //    if (!surName.isEmpty() && !name.isEmpty() && !eMail.isEmpty()) {
        //        params.put("SURNAME", surName);
        //        params.put("NAME", name);
        //        params.put("EMAIL", eMail);
        //        Map<String, Object> methodCallResult = this.callExternalService("paws", "dsPARegister", params);
        //        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
        //            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
        //        } else {
        //            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
        //        }
        //    } else {
        //        jsonResult.setResultStatus(JsonResult.RESULT_empty_LoginPass);
        //    }
        //} else {
        //    jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        //}
        //return Response.status(Response.Status.OK).entity(jsonResult).build();
        //return dsPACallRegisterOrLoginService(paramsStr, "dsPARegister", "SURNAME", "NAME", "EMAIL");
        return dsPACallRegisterOrLoginService(paramsStr, "dsPAFindOrCreateUser>dsPASendVerifyEMail", "SURNAME", "NAME", "EMAIL");
    }

    @POST
    @Path("/dsPARegisterVerifyCode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPARegisterVerifyCode(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        //Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        //JsonResult jsonResult = new JsonResult();
        //if (paramsObj != null) {
        //    Map<String, Object> params = new HashMap<String, Object>();
        //    Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
        //    Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
        //    String verifyCode = getStringParam(obj.get("VERIFYCODE"));
        //    if (!verifyCode.isEmpty()) {
        //        params.put("VERIFYCODE", verifyCode);
        //        Map<String, Object> methodCallResult = this.callExternalService("paws", "dsPARegisterVerifyCode", params);
        //        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
        //            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
        //        } else {
        //            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
        //        }
        //    } else {
        //        jsonResult.setResultStatus(JsonResult.RESULT_empty_LoginPass);
        //    }
        //} else {
        //    jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        //}
        //return Response.status(Response.Status.OK).entity(jsonResult).build();
        return dsPACallRegisterOrLoginService(paramsStr, "dsPARegisterVerifyCode", "VERIFYCODE");
    }

    @POST
    @Path("/dsPARegisterVerifyPhoneNumber")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPARegisterVerifyPhoneNumber(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        //Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        //JsonResult jsonResult = new JsonResult();
        //if (paramsObj != null) {
        //    Map<String, Object> params = new HashMap<String, Object>();
        //    Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
        //    Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
        //    String verifyCode = getStringParam(obj.get("VERIFYCODE"));
        //    String phoneNumber = getStringParam(obj.get("PHONENUMBER"));
        //    if (!verifyCode.isEmpty() && !phoneNumber.isEmpty()) {
        //        params.put("VERIFYCODE", verifyCode);
        //        params.put("PHONENUMBER", phoneNumber);
        //        Map<String, Object> methodCallResult = this.callExternalService("paws", "dsPARegisterVerifyPhoneNumber", params);
        //        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
        //            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
        //        } else {
        //            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
        //        }
        //    } else {
        //        jsonResult.setResultStatus(JsonResult.RESULT_empty_LoginPass);
        //    }
        //} else {
        //    jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        //}
        //return Response.status(Response.Status.OK).entity(jsonResult).build();
        return dsPACallRegisterOrLoginService(paramsStr, "dsPARegisterVerifyPhoneNumber", "VERIFYCODE", "PHONENUMBER", "EMAIL");
    }

    @POST
    @Path("/dsPARegisterEnterSMSCode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPARegisterEnterSMSCode(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        //Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        //JsonResult jsonResult = new JsonResult();
        //if (paramsObj != null) {
        //    Map<String, Object> params = new HashMap<String, Object>();
        //    Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
        //    Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
        //    String verifyCode = getStringParam(obj.get("VERIFYCODE"));
        //    String smsCode = getStringParam(obj.get("SMSCODE"));
        //    if (!verifyCode.isEmpty() && !smsCode.isEmpty()) {
        //        params.put("VERIFYCODE", verifyCode);
        //        params.put("SMSCODE", smsCode);
        //        Map<String, Object> methodCallResult = this.callExternalService("paws", "dsPARegisterEnterSMSCode", params);
        //        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
        //            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
        //        } else {
        //            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
        //        }
        //    } else {
        //        jsonResult.setResultStatus(JsonResult.RESULT_empty_LoginPass);
        //    }
        //} else {
        //    jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        //}
        //return Response.status(Response.Status.OK).entity(jsonResult).build();
        return dsPACallRegisterOrLoginService(paramsStr, "dsPARegisterEnterSMSCode", "VERIFYCODE", "PHONENUMBER", "SMSCODE");
    }

    @POST
    @Path("/dsPARegisterResendSMSCode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPARegisterResendSMSCode(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        //Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        //JsonResult jsonResult = new JsonResult();
        //if (paramsObj != null) {
        //    Map<String, Object> params = new HashMap<String, Object>();
        //    Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
        //    Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
        //    String verifyCode = getStringParam(obj.get("VERIFYCODE"));
        //    String phoneNumber = getStringParam(obj.get("PHONENUMBER"));
        //    if (!verifyCode.isEmpty() && !phoneNumber.isEmpty()) {
        //        params.put("VERIFYCODE", verifyCode);
        //        params.put("PHONENUMBER", phoneNumber);
        //        Map<String, Object> methodCallResult = this.callExternalService("paws", "dsPARegisterResendSMSCode", params);
        //        if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
        //            this.requestWorker.serializeJSON(methodCallResult, jsonResult);
        //        } else {
        //            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
        //        }
        //    } else {
        //        jsonResult.setResultStatus(JsonResult.RESULT_empty_LoginPass);
        //    }
        //} else {
        //    jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        //}
        //return Response.status(Response.Status.OK).entity(jsonResult).build();
        return dsPACallRegisterOrLoginService(paramsStr, "dsPARegisterResendSMSCode", "VERIFYCODE", "PHONENUMBER", "EMAIL");
    }
    */

    @POST
    @Path("/getLossCompReqList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLossCompReqList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BLossCompReqBrowseListByParamCustomeWhereEx");
    }

    /*
    // PA сервисы для вызова из js. доступ к универсальному вызову закрыт.
    @POST
    @Path("/getPaContractForAttach")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaContractForAttach(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsFindContractForAttach");
    }

    @POST
    @Path("/sendPaContractSMS")
    @Produces(MediaType.APPLICATION_JSON)
    // отправка смс на введенный номер, смс сохранится в пользователя ЛК
    // если номер телефона не введен - смс уйдет на телефон пользователя ЛК
    public Response sendPaContractSMS(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsPAAttachContractSendSMSCode");
    }

    @POST
    @Path("/sendPaContractSMSToInsurer")
    @Produces(MediaType.APPLICATION_JSON)
    // сверка контактов страхователя и отправка смс на номер страхователя, смс сохранится в договор
    public Response sendPaContractSMSToInsurer(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsPAAttachContractSendSMSCodeToInsurer");
    }

    @POST
    @Path("/attachPaContract")
    @Produces(MediaType.APPLICATION_JSON)
    public Response attachPaContract(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsAttachContract");
    }

    @POST
    @Path("/checkPaContractSmsCode")
    @Produces(MediaType.APPLICATION_JSON)
    // сверка введенного смс с сохраненным в пользователя ЛК, связывание договора при совпадении
    public Response checkPaContractSmsCode(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsAttachContractEnterSMSCode");
    }

    @POST
    @Path("/checkPaContractInsurerSmsCode")
    @Produces(MediaType.APPLICATION_JSON)
    // сверка введенного смс с сохраненным в договор, связывание договора при совпадении
    public Response checkPaContractInsurerSmsCode(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsAttachContractEnterInsurerSMSCode");
    }

    @POST
    @Path("/getPaAddAgrDoc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaAddAgrDoc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BAddAgrDocBrowseListByParamEx");
    }

    @POST
    @Path("/delPaAddAgrDoc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delPaAddAgrDoc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BAddAgrDocDeleteEx");
    }

    @POST
    @Path("/getPaLossCompReqDoc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaLossCompReqDoc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BLossCompReqDocBrowseListByParamEx");
    }

    @POST
    @Path("/delPaLossCompReqDoc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delPaLossCompReqDoc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BLossCompReqDocDeleteEx");
    }

    @POST
    @Path("/getPaAddAgrCause")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaAddAgrCause(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BAddAgrCauseBrowseListByParam");
    }

    @POST
    @Path("/loadPAAddAgr")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadPAAddAgr(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BAddAgrLoad");
    }

    @POST
    @Path("/savePAAddAgr")
    @Produces(MediaType.APPLICATION_JSON)
    public Response savePAAddAgr(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BAddAgrSave");
    }

    @POST
    @Path("/getPAContract")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPAContract(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsPAContractBrowseListByParamEx");
    }

    @POST
    @Path("/getPAProdBinDoc")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPAProdBinDoc(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BProductBinaryDocumentBrowseListByParam");
    }

    @POST
    @Path("/loadPAB2BContr")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadPAB2BContr(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BContrLoad");
    }

    */
    @POST
    @Path("/dsB2BMultiContrLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMultiContrLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMultiContrLoad", "CONTRID", "PRODCONFID", "LOADCONTRSECTION", "HASH");
    }

    /*
    @POST
    @Path("/getPAAddAgr")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPAAddAgr(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BAddAgrBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BAddAgrCalcContractDopSum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAddAgrCalcContractDopSum(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BAddAgrCalcContractDopSum");
    }

    @POST
    @Path("/getPaProdStruct")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaProdStruct(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsProductBrowseByParams");
    }

    */
    @POST
    @Path("/getB2BProdStruct")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getB2BProdStruct(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductStructTreeLoad");
    }

    /*
    @POST
    @Path("/dsPaHandBookBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPaHandBookBrowseEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BHandbooksBrowseEx");
    }

    @POST
    @Path("/dsPaProductBinaryDocumentBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPaProductBinaryDocumentBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BProductBinaryDocumentBrowseListByParam");
    }

    @POST
    @Path("/dsPaContractDocumentDeleteEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPaContractDocumentDeleteEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsPaContractDocumentDeleteEx");
    }

    @POST
    @Path("/dsPAParticipantSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPAParticipantSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsPAParticipantSave");
    }

    @POST
    @Path("/dsPAParticipantLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPAParticipantLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsPAParticipantLoad");
    }

    @POST
    @Path("/dsPAContractDocumentBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPAContractDocumentBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BContractDocumentBrowseListByParamEx");
    }

    @POST
    @Path("/dsPAgetCountryList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPAgetCountryList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BCountryBrowseListByParam");
        //return dsPACallService(paramsStr, "refws", "getcountrylist");
    }

    */
    @POST
    @Path("/dsB2BgetCountryList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BgetCountryList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BCountryBrowseListByParam");
        //return dsPACallService(paramsStr, "refws", "getcountrylist");
    }

    @POST
    @Path("/dsB2BgetCurrentUserInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BgetCurrentUserInfo(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BGetCurrentUserData");
    }

    @POST
    @Path("/dsB2BUserUpdateFIO")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BUserUpdateFIO(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BUserChangeFIO");
    }

    @POST
    @Path("/dsB2BUserChangePass")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BUserChangePass(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BAccountChangePass");
    }

    @POST
    @Path("/dsAdminUserChangePass")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminUserChangePass(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, "adminws", "admaccountchangepass");
    }

    @POST
    @Path("/dsB2BchangeExpiredPass")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BchangeExpiredPass(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BExpiredCallService(paramsStr, B2BPOSWS, "dsB2BAccountChangePass");
    }

    /*
    @POST
    @Path("/changePALossCompReqState")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePALossCompReqState(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        //return dsPACallServiceWithCheckSmsCode(paramsStr, B2BPOSWS, "dsB2BLossCompReq_State_MakeTrans");
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BLossCompReq_State_MakeTrans", true);
    }

    // перевод заявки из состояния "Черновик" в состояние "Удалено" (без подтверждения СМС-кодом)
    @POST
    @Path("/dsB2BLossCompReqChangeStateToDeleted")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLossCompReqChangeStateToDeleted(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, B2BPOSWS, "dsB2BLossCompReqChangeStateToDeleted");
    }

    @POST
    @Path("/dsPaAppealBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPaAppealBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsPaAppealBrowseListByParamEx");
    }

    @POST
    @Path("/dsPaAppealHistBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPaAppealHistBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsPaAppealHistBrowseListByParamEx");
    }

    @POST
    @Path("/dsPaAppealHistMarkMessagesAsReaded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPaAppealHistMarkMessagesAsReaded(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsPaAppealHistMarkMessagesAsReaded");
    }

    @POST
    @Path("/dsPaAppealHistMarkMessagesAsReadedByIdsList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPaAppealHistMarkMessagesAsReadedByIdsList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsPaAppealHistMarkMessagesAsReadedByIdsList");
    }

    @POST
    @Path("/dsPaAppealHistBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPaAppealHistBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsPaAppealHistBrowseListByParam");
    }

    @POST
    @Path("/dsPaAppealHistCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsPaAppealHistCreate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsPACallService(paramsStr, "paws", "dsPaAppealHistCreate");
    }
    */
    // PA сервисы для вызова из js. доступ к универсальному вызову закрыт.

    //тестовый метод для вызовы из Android проиложения
    @POST
    @Path("/dsAndroidTest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAndroidTest(@FormParam("objMap") String param) {
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Path("/dsB2B_JournalCustomBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2B_JournalCustomBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2B_JournalCustomBrowseListByParam");
    }

    @POST
    @Path("/dsB2B_JournalBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2B_JournalBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2B_JournalBrowseListByParam");
    }

    @POST
    @Path("/dsB2B_JournalDataBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2B_JournalDataBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2B_JournalDataBrowseListByParam");
    }

    @POST
    @Path("/dsB2B_JournalDataBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2B_JournalDataBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2B_JournalDataBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2B_GetUserParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2B_GetUserParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2B_GetUserParams");
    }

    @POST
    @Path("/dsB2B_SetUserParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2B_SetUserParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2B_SetUserParams");
    }

    @POST
    @Path("/dsB2B_DeleteUserParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2B_DeleteUserParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2B_DeleteUserParams");
    }

    @POST
    @Path("/dsSAL_JournalProcessItem")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsSAL_JournalProcessItem(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsSAL_JournalProcessItem");
    }

    @POST
    @Path("/dsB2BCreateOrUpdateHandbookData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BCreateOrUpdateHandbookData(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BCreateOrUpdateHandbookData");
    }

    @POST
    @Path("/dsB2BCreateOrUpdateHandbookDataEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BCreateOrUpdateHandbookDataEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BCreateOrUpdateHandbookDataEx");
    }

    @POST
    @Path("/dsB2BGetHandbookDataByName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BGetHandbookDataByName(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BGetHandbookDataByName");
    }

    @POST
    @Path("/dsB2BDeleteHandbookDataEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDeleteHandbookDataEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BDeleteHandbookDataEx");
    }

    @POST
    @Path("/dsB2BGetHandbookList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BGetHandbookList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BGetHandbookList");
    }

    @POST
    @Path("/dsSAL_Journal_FlagCustomBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsSAL_Journal_FlagCustomBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsSAL_Journal_FlagCustomBrowseListByParam");
    }

    @POST
    @Path("/dsSAL_Journal_ContextCustomBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsSAL_Journal_ContextCustomBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsSAL_Journal_ContextCustomBrowseListByParam");
    }

    @POST
    @Path("/dsB2BMainActivityContractBaseAttachDocBrowse")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMainActivityContractBaseAttachDocBrowse(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMainActivityContractBaseAttachDocBrowse");
    }

    @POST
    @Path("/dsB2BMainActivityContractBaseAttachDocDelete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMainActivityContractBaseAttachDocDelete(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMainActivityContractBaseAttachDocDelete");
    }

    @POST
    @Path("/dsB2BMainActivityContractFindByOrgStructIdSessionParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMainActivityContractFindByOrgStructIdSessionParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMainActivityContractFindByOrgStructIdSessionParams");
    }

    @POST
    @Path("/dsActivationCheck")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsActivationCheck(@FormParam(FORM_PARAM_NAME) String contractParam) {
        Activate activ = this.requestWorker.deserializeJSON(contractParam, Activate.class);
        JsonResult jsonResult = new JsonResult();
        if (activ != null) {
            Map<String, Object> activateMap = activ.copyActivateFromEntityToMap();
            clearKeysForOnlineMethods(activateMap);
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BActivationCheck", activateMap);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    @POST
    @Path("/dsB2BActivationCheck")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BActivationCheck(@FormParam(FORM_PARAM_NAME) String contractParam) {
        return dsB2BCallService(contractParam, B2BPOSWS, "dsB2BActivationCheck");
    }

    @POST
    @Path("/dsActivatePolicy")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsActivatePolicy(@FormParam(FORM_PARAM_NAME) String contractParam) {
        Activate activ = this.requestWorker.deserializeJSON(contractParam, Activate.class);
        JsonResult jsonResult = new JsonResult();
        if (activ != null) {
            Map<String, Object> activateMap = activ.copyActivateFromEntityToMap();
            clearKeysForOnlineMethods(activateMap);
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BActivatePolicy", activateMap);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
//        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BActivatePolicy");
    }

    @POST
    @Path("/dsB2BActivatePolicy")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BActivatePolicy(@FormParam(FORM_PARAM_NAME) String contractParam) {
        return dsB2BCallService(contractParam, B2BPOSWS, "dsB2BActivatePolicy");
    }

    @POST
    @Path("/dsActivateResendNotify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsActivateResendNotify(@FormParam(FORM_PARAM_NAME) String contractParam) {
        Activate activ = this.requestWorker.deserializeJSON(contractParam, Activate.class);
        JsonResult jsonResult = new JsonResult();
        if (activ != null) {

            Map<String, Object> activateMap = activ.copyActivateFromEntityToMap();
            clearKeysForOnlineMethods(activateMap);
            Map<String, Object> methodCallResult = this.callExternalService(B2BPOSWS, "dsB2BActivateResendNotify", activateMap);
            clearKeysForOnlineMethods(methodCallResult);
            if ((methodCallResult != null) && !methodCallResult.isEmpty()) {
                this.requestWorker.serializeJSON(methodCallResult, jsonResult);
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            }

        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        return Response.status(Response.Status.OK).entity(jsonResult).build();
//        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BActivatePolicy");
    }

    @POST
    @Path("/dsB2BActivateResendNotify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BActivateResendNotify(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BActivateResendNotify");
    }

    @POST
    @Path("/dsB2BActivationCheckByContr")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BActivationCheckByContr(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BActivationGetCodeByContr");
    }

    @POST
    @Path("/dsB2BSendUWNotificationEMail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSendUWNotificationEMail(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BSendUWNotificationEMail");
    }

    @POST
    @Path("/dsB2BSendSellerNotificationEMail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSendSellerNotificationEMail(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BSendSellerNotificationEMail");
    }

    // вычисление сдвига по дате (с учетом LAG)
    // например для "Период охлаждения" "5 рабочих дней с даты заключения", "<дата заключения + 5 рабочих дней>"
    @POST
    @Path("/dsB2BContractCalcLaggedDate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContractCalcLaggedDate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContractCalcLaggedDate");
    }

    @POST
    @Path("/dsB2BContractCancelApplicationSend")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContractCancelApplicationSend(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsSHTaskCreateEx");
    }

    @POST
    @Path("/dsB2BCalendarBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BCalendarBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BCalendarBrowseListByParamEx");
    }

    @POST
    @Path("/getCurrancyRateByDate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrancyRateByDate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "getCurrancyRateByDate");
    }

    @POST
    @Path("/dsB2BSHtaskLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSHtaskLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSHtaskLoad");
    }

    @POST
    @Path("/dsB2BSHtaskLoadAttachFiles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSHtaskLoadAttachFiles(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSHtaskLoadAttachFiles");
    }

    @POST
    @Path("/dsB2BDoActivLiveContrMigrate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDoActivLiveContrMigrate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BDoActivLiveContrMigrate");
    }

    @POST
    @Path("/dsLifeIntegrationSendContractsData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsLifeIntegrationSendContractsData(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsLifeIntegrationGetContractsData");
    }

    @POST
    @Path("/dsLifeAgentCalculatorBrowseList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsLifeAgentCalculatorBrowseList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BAgentCalculatorBrowseList");
    }

    @POST
    @Path("/dsLifeGetHandbookDescription")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsLifeGetHandbookDescription(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BHandbookDescriptionBrowseListByParam");
    }

    @POST
    @Path("/dsB2BProductStructureBrowseAsTreeGridList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductStructureBrowseAsTreeGridList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductStructureBrowseAsTreeGridList");
    }

    @POST
    @Path("/dsB2BProductValueBaseBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductValueBaseBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductValueBaseBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BProductValueBaseCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductValueBaseCreate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductValueBaseCreate");
    }

    @POST
    @Path("/dsB2BProductValueBaseUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductValueBaseUpdate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductValueBaseUpdate");
    }

    @POST
    @Path("/dsB2BProductValueBaseDelete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductValueBaseDelete(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductValueBaseDelete");
    }

    @POST
    @Path("/dsAdminGetProfileRightsList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminGetProfileRightsList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsAdminCallService(paramsStr, B2BPOSWS, "dsAdminGetProfileRightsList");
    }

    @POST
    @Path("/dsAdminGetMenuRightsList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminGetMenuRightsList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsAdminCallService(paramsStr, B2BPOSWS, "dsAdminGetMenuRightsList");
    }

    @POST
    @Path("/dsB2BAdminCreateMenuRight")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminCreateMenuRight(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsB2BAdminCreateMenuRight");
    }

    @POST
    @Path("/dsB2BAdminDeleteMenuRight")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminDeleteMenuRight(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsB2BAdminDeleteMenuRight");
    }

    @POST
    @Path("/dsGetDepartmentList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsGetDepartmentList(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsGetDepartmentList");
    }

    @POST
    @Path("/dsGetMenuTypeList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsGetMenuTypeList(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsGetMenuTypeList");
    }

    @POST
    @Path("/dsAdminGetAvailableRightsList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminGetAvailableRightsList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsAdminCallService(paramsStr, B2BPOSWS, "dsAdminGetAvailableRightsList");
    }

    @POST
    @Path("/dsAdminParameterListOnRight")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminParameterListOnRight(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminParameterListOnRight");
    }

    @POST
    @Path("/dsAdminCreateProfileRight")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminCreateProfileRight(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminCreateProfileRight");
    }

    @POST
    @Path("/dsAdminUpdateProfileRight")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminUpdateProfileRight(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminUpdateProfileRight");
    }

    @POST
    @Path("/dsAdminDeleteProfileRight")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminDeleteProfileRight(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminDeleteProfileRight");
    }

    @POST
    @Path("/dsGetProjectList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsGetProjectList(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsGetProjectList");
    }

    @POST
    @Path("/dsGetHandbookForEditCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsGetHandbookForEditCreate(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsGetHandbookForEditCreate");
    }

    @POST
    @Path("/dsB2BAdminUpdateStatusUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminDeleteUser(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsB2BAdminUpdateStatusUser");
    }

    @POST
    @Path("/dsB2BGetCancellationTypeByContrId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BGetCancellationTypeByContrId(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsB2BCallService(paramStr, B2BPOSWS, "dsB2BGetCancellationTypeByContrId");
    }

    @POST
    @Path("/dsB2BAdminUserChangePasswordAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminUserChangePasswordAccount(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsB2BAdminUserChangePasswordAccount");
    }

    @POST
    @Path("/dsB2BAdminCreateUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminCreateUser(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsB2BAdminCreateUser");
    }

    @POST
    @Path("/dsB2BAdminMenuCreateOrUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminMenuCreateOrUpdate(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsB2BAdminMenuCreateOrUpdate");
    }

    @POST
    @Path("/dsB2BAdminMenuDelete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminMenuDelete(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsB2BAdminMenuDelete");
    }

    @POST
    @Path("/dsUserRoleAction")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsUserRoleAction(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsUserRoleAction");
    }

    @POST
    @Path("/dsDepartmentPartnerAction")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsDepartmentPartnerAction(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsDepartmentPartnerAction");
    }

    @POST
    @Path("/dsB2BAdminPartnerUserAction")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminPartnerUserAction(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsB2BAdminPartnerUserAction");
    }

    @POST
    @Path("/dsB2BAdminCommonResetPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminCommonResetPassword(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsB2BAdminCommonResetPassword");
    }

    @POST
    @Path("/dsDepartmentAction")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsDepartmentAction(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsDepartmentAction");
    }

    @POST
    @Path("/dsGetRoleList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsGetRoleList(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsGetRoleList");
    }

    @POST
    @Path("/dsAdminRoleUserAdd")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminRoleUserAdd(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsAdminRoleUserAdd");
    }

    @POST
    @Path("/dsAdminRoleUserRemove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminRoleUserRemove(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsAdminRoleUserRemove");
    }

    @POST
    @Path("/dsAdmRoleListByAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdmRoleListByAccount(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsAdmRoleListByAccount");
    }

    @POST
    @Path("/dsGetRoleListByUserAccountId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsGetRoleListByUserAccountId(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsGetRoleListByUserAccountId");
    }

    @POST
    @Path("/dsAdminGetUserRoleList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminGetUserRoleList(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsAdminGetUserRoleList");
    }


    @POST
    @Path("/dsAdminGetMenuList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminGetMenuList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminGetMenuList");
    }

    @POST
    @Path("/dsGetParentMenuList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsGetParentMenuList(@FormParam(FORM_PARAM_NAME) String paramStr) {
        return dsAdminCallService(paramStr, B2BPOSWS, "dsGetParentMenuList");
    }

    @POST
    @Path("/dsB2BUserAccountImport")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BUserAccountImport(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BUserAccountImport");
    }

    @POST
    @Path("/dsB2BCountryBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BCountryBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BCountryBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BMort900BankStatementsProcess")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMort900BankStatementsProcess(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMort900BankStatementsProcess");
    }

    @POST
    @Path("/dsB2BMort900BankCashFlowsPrepare")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMort900BankCashFlowsPrepare(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMort900BankCashFlowsPrepare");
    }

    @POST
    @Path("/dsB2BMort900BankCashFlowsProcess")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMort900BankCashFlowsProcess(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMort900BankCashFlowsProcess");
    }

    @POST
    @Path("/dsB2BMort900BankStatementsFinalize")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMort900BankStatementsFinalize(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMort900BankStatementsFinalize");
    }

    @POST
    @Path("/dsB2BGetPartnerFromContract")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BGetPartnerFromContract(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BGetPartnerFromContract");
    }

    @POST
    @Path("/dsB2BInvestGraphDataByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestGraphDataByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestGraphDataByParam");
    }

    @POST
    @Path("/dsB2BInvestMaxDateBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestMaxDateBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestMaxDateBrowseListByParam");
    }

    @POST
    @Path("/dsB2BInvestDIDMaxDateBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestDIDMaxDateBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestDIDMaxDateBrowseListByParam");
    }

    @POST
    @Path("/dsB2BInvestCouponBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestCouponBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestCouponBrowseListByParamEx");
    }

    // !только для отладки!
    // на момент написания  комментария не используются на интерфейсе - запретить вызов из гейта по окончании разработки
    @POST
    @Path("/dsB2BLossNoticeSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLossNoticeSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BLossNoticeSave");
    }

    // !только для отладки!
    // на момент написания  комментария не используются на интерфейсе - запретить вызов из гейта по окончании разработки
    @POST
    @Path("/dsB2BLossNoticeLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLossNoticeLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BLossNoticeLoad");
    }

    @POST
    @Path("/dsB2BInvestCouponGraphDataByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestCouponGraphDataByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestCouponGraphDataByParam");
    }

    @POST
    @Path("/dsB2BProductInvestBaseActiveBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductInvestBaseActiveBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductInvestBaseActiveBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BInvestTrancheNearDate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestTrancheNearDate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestTrancheNearDate");
    }

    @POST
    @Path("/dsB2BDeclarationOfChangeDataProvider")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDeclarationOfChangeDataProvider(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BDeclarationOfChangeDataProvider");
    }

    // получение списка всех справочников
    @POST
    @Path("/dsB2BHandbookBrowseList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BHandbookBrowseList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BHandbookBrowseList");
    }

    @POST
    @Path("/dsB2BInsHBPropDescrBrowseCustomListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInsHBPropDescrBrowseCustomListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInsHBPropDescrBrowseCustomListByParamEx");
    }

    @POST
    @Path("/dsB2BAddOrUpdateHandbookDataEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAddOrUpdateHandbookDataEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BAddOrUpdateHandbookDataEx");
    }

    @POST
    @Path("/dsB2BRemoveHandbookDataEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BRemoveHandbookDataEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BRemoveHandbookDataEx");
    }

    @POST
    @Path("/dsB2BBaseActiveSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BBaseActiveSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BBaseActiveSave");
    }

    @POST
    @Path("/dsB2BHandbookRecordBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BHandbookRecordBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BHandbookRecordBrowseListByParam");
    }

    @POST
    @Path("/dsB2BLoadHandbookBySysname")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLoadHandbookBySysname(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BLoadHandbookBySysname");
    }

    @POST
    @Path("/dsB2BInvestFastLoadData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestFastLoadData(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestFastLoadData");
    }

    // загрузка прикрепленных документов к заявлению
    @POST
    @Path("/dsB2BDeclarationDocAttachedFileList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDeclarationDocAttachedFileList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsPDDeclarationDocCustomBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BInvestFileLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestFileLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestFileLoad");
    }

    @POST
    @Path("/dsB2BInvestCSVData")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BInvestCSVData(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BInvestCSVData");
    }

    // Администрирование СБСЖ ЛК

    @POST
    @Path("/dsB2BAdminLKCreateDIDReportByParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminLKCreateDIDReportByParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BAdminLKCreateDIDReportByParams");
    }

    @POST
    @Path("/dsB2BAdminLKAllDocCustomBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminLKAllDocCustomBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BAdminLKAllDocCustomBrowseListByParamEx");
    }

    @POST
    @Path("/dsAdminLkBrowseContractListByParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminLkBrowseContractListByParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminLkBrowseContractListByParams");
    }

    @POST
    @Path("dsAdminLkBrowseInsEventListByParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminLkBrowseInsEventListByParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminLkBrowseInsEventListByParams");
    }

    @POST
    @Path("dsAdminLkBrowseInsEventMessageListByParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminLkBrowseInsEventMessageListByParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminLkBrowseInsEventMessageListByParams");
    }

    @POST
    @Path("/dsAdminLKBrowseProductListByParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminLKBrowseProductListByParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminLKBrowseProductListByParams");
    }

    @POST
    @Path("/dsAdminLKBrowseContractStateListByParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminLKBrowseContractStateListByParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminLKBrowseContractStateListByParams");
    }

    @POST
    @Path("dsAdminLkBrowseMessageListByParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminLkBrowseMessageListByParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminLkBrowseMessageListByParams");
    }

    @POST
    @Path("dsAdminLkBrowseChatListByParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminLkBrowseChatListByParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminLkBrowseChatListByParams");
    }

    @POST
    @Path("dsAdminLkBrowseAccountListByParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsAdminLkBrowseAccountListByParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsAdminLkBrowseAccountListByParams");
    }

    @POST
    @Path("/dsB2BAdminLKBrowseContractFullInfoByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminLKBrowseContractFullInfoByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BAdminLKBrowseContractFullInfoByParam");
    }

    // загрузка списка всех сформированных заявлений, прикрепленных к уведомлению о событии
    @POST
    @Path("/dsB2BLossPaymentClaimBrowseListByLossNoticeId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLossPaymentClaimBrowseListByLossNoticeId(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BLossPaymentClaimBrowseListByLossNoticeId");
    }


    @POST
    @Path("/dsB2BContractBrowseFullMapEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContractBrowseFullMapEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsLifeIntegrationGetContractList");
    }

    @POST
    @Path("/dsB2BDeclarationOfChangeForContractCustomBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDeclarationOfChangeForContractCustomBrowseListByParam(@FormParam(FORM_PARAM_NAME) String
                                                                                       paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BDeclarationOfChangeForContractCustomBrowseListByParam");
    }

    @POST
    @Path("/dsB2BDeclarationOfChangeLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDeclarationOfChangeLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BDeclarationOfChangeLoad");
    }

    @POST
    @Path("/dsB2BDeclarationOfChangeForContractGetOperations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDeclarationOfChangeForContractGetOperations(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BDeclarationOfChangeForContractGetOperations");
    }

    @POST
    @Path("/dsB2BGetContractById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BGetContractById(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BContrLoad");
    }

    @POST
    @Path("/dsB2BGetPayVarDataByPayVarID")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPayVarDataByPayVarID(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BPaymentVariantBrowseListByParam");
    }

    @POST
    @Path("/dsB2BProductTermBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductTermBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductTermBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BProductProgramBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductProgramBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductProgramBrowseListByParam");
    }

    @POST
    @Path("/dsB2BAdminLKGetAttachmentsForDeclarationOfChange")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminLKGetAttachmentsForDeclarationOfChange(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BAdminLKGetAttachmentsForDeclarationOfChange");
    }

    @POST
    @Path("/dsB2BMessageSetIsRead")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BMessageSetIsRead(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BMessageSetIsRead");
    }

    @POST
    @Path("/dsB2BLoadFilledQuestionnaire")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLoadFilledQuestionnaire(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BLoadFilledQuestionnaire");
    }

    @POST
    @Path("/dsB2BUpdateFilledQuestionnaire")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BUpdateFilledQuestionnaire(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BUpdateFilledQuestionnaire");
    }

    @POST
    @Path("/dsB2BLoadFilledQuestionnaireStatuses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLoadFilledQuestionnaireStatuses(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        JsonResult jsonResult = new JsonResult();
        jsonResult.setResultJson("[{  }]");
        jsonResult.setResultStatus("OK");

        return Response.ok(jsonResult).build();
    }

    @POST
    @Path("/dsMessageBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsMessageBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsMessageBrowseListByParamEx");
    }

    @POST
    @Path("/dsMessageDocumentBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsMessageDocumentBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsMessageDocumentBrowseListByParam");
    }

    @POST
    @Path("/dsMessageUserCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsMessageUserCreate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsMessageUserCreate");
    }

    @POST
    @Path("/dsDCTMessageCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsDCTMessageCreate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsDCTMessageCreate");
    }

    @POST
    @Path("/dsChatMakeTransitionFromAnsweredToNew")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsChatMakeTransitionFromAnsweredToNew(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsChatMakeTransitionFromAnsweredToNew");
    }

    @POST
    @Path("/dsChatMakeTransitionFromNewToViewed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsChatMakeTransitionFromNewToViewed(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsChatMakeTransitionFromNewToViewed");
    }

    @POST
    @Path("/dsChatMakeTransitionFromAnsweredToClosed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsChatMakeTransitionFromAnsweredToClosed(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsChatMakeTransitionFromAnsweredToClosed");
    }

    @POST
    @Path("/dsChatMakeTransitionFromViewedToNew")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsChatMakeTransitionFromViewedToNew(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsChatMakeTransitionFromViewedToNew");
    }

    @POST
    @Path("/dsChatMakeTransitionFromViewedToAnswered")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsChatMakeTransitionFromViewedToAnswered(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsChatMakeTransitionFromViewedToAnswered");
    }

    @POST
    @Path("/dsChatMakeTransitionFromViewedToClosed")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsChatMakeTransitionFromViewedToClosed(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsChatMakeTransitionFromViewedToClosed");
    }


    /**
     * Получение профиля и аккаунта клиента.
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsB2BClientProfileAccountLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BClientProfileAccountLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BClientProfileAccountLoad");
    }

    /**
     * Получение профиля клиента.
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsB2BClientProfileClientLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BClientProfileClientLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BClientProfileClientLoad");
    }

    /**
     * Сохранение профиля клиента.
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsB2BClientProfileClientSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BClientProfileClientSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BClientProfileClientSave");
    }

    @POST
    @Path("/dsB2BContractMassTest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BContractMassTest(@FormParam(FORM_PARAM_NAME) String params) {
        return dsB2BCallService(params, B2BPOSWS, "dsB2BContractMassTest");
    }

    /**
     * Перевод состояний статуса клиента.
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsClientprofileMakeTransitionFromBlockedToActive")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsClientprofileMakeTransitionFromBlockedToActive(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsClientprofileMakeTransitionFromBlockedToActive");
    }

    @POST
    @Path("/dsClientprofileMakeTransitionFromActiveToBlocked")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsClientprofileMakeTransitionFromActiveToBlocked(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsClientprofileMakeTransitionFromActiveToBlocked");
    }

    /**
     * Получение состояний статуса клиента.
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsB2BAccountListGetStateList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAccountListGetStateList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BAccountListGetStateList");
    }

    /**
     * Получение типов обращений.
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsB2BChatGetTopicType")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BChatGetTopicType(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BChatGetTopicType");
    }

    /**
     * Загрузка классифкаторов
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsB2BDictionaryClassifierLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDictionaryClassifierLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BDictionaryClassifierLoad");
    }

    /**
     * Загрузка дополнительных свойств
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsB2BClientPropertiesLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BClientPropertiesLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BClientPropertiesLoad");
    }

    @POST
    @Path("/dsB2BCaringParentsCalcByContrMap")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BCaringParentsCalcByContrMap(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BCaringParentsCalcByContrMap");
    }

    // загрузка списка всех сформированных заявлений, прикрепленных к уведомлению о событии
    @POST
    @Path("/dsB2BAdminLKGetAllAttachedFileWithDeclaration")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BAdminLKGetAllAttachedFileWithDeclaration(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BAdminLKGetAllAttachedFileWithDeclaration");
    }

    @POST
    @Path("/dsB2BChatLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BChatLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BChatLoad");
    }

    @POST
    @Path("/dsB2BChatSave")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BChatSave(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BChatSave");
    }

    // Вызов проверки возможности расторжения договора
    @POST
    @Path("/dsB2BCheckIsPossibleAnnulmentOrCancellation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BCheckIsPossibleAnnulmentOrCancellation(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BCheckIsPossibleAnnulmentOrCancellation");
    }

    @POST
    @Path("/dsB2BChatLoadById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BChatLoadById(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BChatLoadById");
    }

    @POST
    @Path("/dsB2BCreateNewChat")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BCreateNewChat(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BCreateNewChat");
    }

    @POST
    @Path("/dsB2BUpdateChatLastMessage")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BUpdateChatLastMessage(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BUpdateChatLastMessage");
    }

    @POST
    @Path("/dsB2BGetSessionIdUserLkById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BGetSessionIdUserLkById(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BGetSessionIdUserLkById");
    }

    @POST
    @Path("/dsB2BGetSessionIdUserLkByContractId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BGetSessionIdUserLkByContractId(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BGetSessionIdUserLkByContractId");
    }

    @POST
    @Path("/dsB2BKindCountryLoadById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BKindCountryLoadById(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BKindCountryLoadById");
    }

    @POST
    @Path("/dsB2BDictionaryClassifierDataLoadByName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDictionaryClassifierDataLoadByName(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BDictionaryClassifierDataLoadByName");
    }

    @POST
    @Path("/dsMessageLoadWithDocumentsByChatId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsMessageLoadWithDocumentsByChatId(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsDCTMessageWithDocumentsBrowseByChatId");
    }

    @POST
    @Path("/dsB2BDeclarationOfChangePrint")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDeclarationOfChangePrint(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BDeclarationOfChangePrint");
    }

    @POST
    @Path("/dsB2BDeclarationOfChangePrintWithoutDecalaration")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDeclarationOfChangePrintWithoutDecalaration(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, SIGNB2BPOSWS, "dsB2BDeclarationOfChangePrintWithoutDecalaration");
    }

    @POST
    @Path("/dsB2BRedemptionAmountListByParamBrowseEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BRedemptionAmountListByParamBrowseEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BRedemptionAmountListByParamBrowseEx");
    }

    // справочник валют продукта
    @POST
    @Path("/dsB2BProductInsAmCurrencyBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductInsAmCurrencyBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductInsAmCurrencyBrowseListByParamEx");
    }

    // справочник инвестиционной стратегии продукта
    @POST
    @Path("/dsB2BProductInvestBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductInvestBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductInvestBrowseListByParamEx");
    }

    // справочник периодичности продукта
    @POST
    @Path("/dsB2BProductPayVarBrowseListByParamEx")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductPayVarBrowseListByParamEx(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductPayVarBrowseListByParamEx");
    }

    @POST
    @Path("/dsB2BProductRedemptionAmountCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductRedemptionAmountCreate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductRedemptionAmountCreate");
    }

    @POST
    @Path("/dsB2BProductRedemptionAmountDelete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductRedemptionAmountDelete(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductRedemptionAmountDelete");
    }

    @POST
    @Path("/dsB2BProductRedemptionAmountModify")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductRedemptionAmountModify(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductRedemptionAmountModify");
    }

    @POST
    @Path("/dsB2BProductRedemptionAmountBrowseListByParam")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductRedemptionAmountBrowseListByParam(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductRedemptionAmountBrowseListByParam");
    }

    @POST
    @Path("/dsB2BProductRedemptionAmountMassCreateOrUpdate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductRedemptionAmountMassCreateOrUpdate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductRedemptionAmountMassCreateOrUpdate");
    }

    @POST
    @Path("/dsB2BProductProgramBrowseListByParam4lk")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BProductProgramBrowseListByParam4lk(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BProductProgramBrowseListByParam4lk");
    }

    /**
     * Получение статусов заявлений на изменения
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsB2BChangeListGetStateList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BChangeListGetStateList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BChangeListGetStateList");
    }

    @POST
    @Path("/dsB2BDeclarationOfChangeSaveB2BPPO")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BDeclarationOfChangeSaveB2BPPO(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BDeclarationOfChangeSaveB2BPPO");
    }

    /**
     * Получение списка страховых продуктов в журнале изменений
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsB2BChangeListGetInsProductList")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BChangeListGetInsProductList(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BChangeListGetInsProductList");
    }

    /**
     * Отправка почтового уведомления
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsB2BSendFeedbackOnEmail")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSendFeedbackOnEmail(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSendFeedbackOnEmail");
    }

    @POST
    @Path("/dsB2BSendFeedbackOnEmailKMSB")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BSendFeedbackOnEmailKMSB(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BSendFeedbackOnEmailKMSB");
    }

    @POST
    @Path("/dsKMSBBrowseChatAuthorsListByParams")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsKMSBBrowseChatAuthorsListByParams(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsKMSBBrowseChatAuthorsListByParams");
    }

    @POST
    @Path("/dsCheckAdminRights")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCheckAdminRights(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsCheckAdminRights");
    }

    @POST
    @Path("/dsB2BCheckLogin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BCheckLogin(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BCheckLogin");
    }

    /**
     * Получение списка "Тип импорта"
     *
     * @param paramsStr
     * @return - список типов импорта
     */
    @POST
    @Path("/dsGetTypeImportClientManagerContract")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsGetTypeImportClientManagerContract(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsGetTypeImportClientManagerContract");
    }

    /**
     * Создание новой сессии импорта оргструктуры
     */
    @POST
    @Path("/dsB2BImportSessionDepartmentCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BImportSessionDepartmentCreate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BImportSessionDepartmentCreate");
    }


    /**
     * Получение списка файлов, прикрепленных к сессии импорта оргструктуры
     */
    @POST
    @Path("/dsB2BImportSessionDepartmentGetBinaryFileInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BImportSessionDepartmentGetBinaryFileInfo(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BImportSessionDepartmentGetBinaryFileInfo");
    }

    /**
     * Создание новой сессии импорта км договор
     */
    @POST
    @Path("/dsB2BImportSessionManagerContractCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BImportSessionManagerContractCreate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BImportSessionManagerContractCreate");
    }

    /**
     * Получение списка файлов, прикрепленных к сессии импорта км договор
     */
    @POST
    @Path("/dsB2BImportSessionManagerContractGetBinaryFileInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BImportSessionManagerContractGetBinaryFileInfo(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BImportSessionManagerContractGetBinaryFileInfo");
    }

    /**
     * Создание новой сессии импорта км всп
     */
    @POST
    @Path("/dsB2BImportSessionManagerDepartmentCreate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BImportSessionManagerDepartmentCreate(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BImportSessionManagerDepartmentCreate");
    }

    /**
     * Получение списка файлов, прикрепленных к сессии импорта км всп
     */
    @POST
    @Path("/dsB2BImportSessionManagerDepartmentGetBinaryFileInfo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BImportSessionManagerDepartmentGetBinaryFileInfo(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BImportSessionManagerDepartmentGetBinaryFileInfo");
    }

    /**
     * Получение протокола обработки содержимого сессии импорта по ИД содержимого сессии импорта
     */
    @POST
    @Path("/dsB2BImportSessionContentProcessLogEntryLoad")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BImportSessionContentProcessLogEntryLoad(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BImportSessionContentProcessLogEntryLoad");
    }

    @POST
    @Path("/dsCreateRecoveryPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsCreateRecoveryPassword(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        try {
            // парсинг входных параметров
            Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
            JsonResult jsonResult = new JsonResult();
            if (paramsObj == null) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при получении временного пароля.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            Map<String, Object> obj = getMapParamName(objMap, "obj");
            if (obj == null) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при получении временного пароля.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            String enteredLogin = (String) obj.get("login"); // введенный логин
            String enteredEmail = (String) obj.get("email"); // введенная почта
            String phoneNumber = (String) obj.get("phoneNumber"); // номер телефона
            StringBuilder log = new StringBuilder();
            log.append("Пользователь представился как '").append(enteredLogin).append("', ");

            // проверка логина
            if (enteredLogin == null || enteredLogin.isEmpty() || enteredEmail == null || enteredEmail.isEmpty()) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при получении временного пароля.");
                    }});
                }}, jsonResult);
                log.append("введенных данных недостаточно для идентификации пользователя");
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Map<String, Object> userAccountSelectResult = this.callExternalService(B2BPOSWS,
                    "dsAdminGetUsersList", new HashMap<String, Object>() {{
                        put("CUSTOMWHERE", String.format(" T.LOGIN = \'%s\' ", enteredLogin));
                    }});
            Integer totalCount = (Integer) userAccountSelectResult.get("TOTALCOUNT");
            if (totalCount == null || !totalCount.equals(1)) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Введен неверный логин.");
                    }});
                }}, jsonResult);
                log.append("введен неверный логин");
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Map<String, Object> userAccount;
            try {
                userAccount = ParamGetter.getFirstItemFromResultMap(userAccountSelectResult);
            } catch (NullPointerException e) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Введен неверный логин.");
                    }});
                }}, jsonResult);
                log.append("введен неверный логин");
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            if (userAccount == null) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Введен неверный логин.");
                    }});
                }}, jsonResult);
                log.append("введен неверный логин");
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            BigDecimal userAccountId = (BigDecimal) userAccount.get(USER_ACCOUNT_ID_PARAM_NAME); // ид пользователя
            String userCurrentEmail = (String) userAccount.get("EMAIL"); // текущая почта пользователя
            BigDecimal employeeId = (BigDecimal) userAccount.get("EMPLOYEEID");
            if (userAccountId == null || employeeId == null || userCurrentEmail == null || userCurrentEmail.isEmpty()) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Введен неверный логин.");
                    }});
                }}, jsonResult);
                log.append("введен неверный логин");
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }

            log.append("пользователь идентифицирован [ID = ").append(userAccountId).append("], ");
            Map<String, Object> groupParams = new HashMap() {{
                put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
            }};
            Map<String, Object> userGroupsRes = this.callExternalService(ADMINWS, "admAccountGroupList", groupParams);
            HashSet userGroupsList = new HashSet();
            List<Map<String, Object>> userGroupList = ParamGetter.getListFromResultMap(userGroupsRes);
            if (userGroupList != null && !userGroupList.isEmpty()) {
                userGroupList.stream().forEach(groupMap -> userGroupsList.add(groupMap.get("SYSNAME").toString()));
            }
            // генерация временного пароля
            String tempPassword = verifier.generatePassword(); // TODO: 05.05.18 check it
            String tempPasswordShaHex = DigestUtils.sha512Hex(tempPassword);

            // генерация срока действия пароля
            log.append("сгенерирован новый пароль, ");
            Calendar calendarForExpDate = GregorianCalendar.getInstance();
            calendarForExpDate.add(Calendar.MINUTE, 15);
            Date passwordExpDate = calendarForExpDate.getTime();
            log.append("истекает ").append(calendarForExpDate.getTime().toString()).append(", ");

            // получание максимального количества ввода неверной почты
            String tryCountFromSettingsString = (String) this.callExternalService(WsConstants.COREWS,
                    "getSysSettingBySysName", new HashMap<String, Object>() {{
                        put("SETTINGSYSNAME", "userRecoverPasswordMaxTimes");
                        put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
                    }}).get("SETTINGVALUE");
            if (tryCountFromSettingsString == null) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при получении временного пароля.");
                    }});
                }}, jsonResult);
                log.append("произошла ошибка при получении временного пароля.");
                passwordCreateRecoverToAudit(userGroupsList, log);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Long tryCountFromSettings = Long.parseLong(tryCountFromSettingsString); // максимальное количество ввода неверной почты

            // работа с временными паролями
            Map<String, Object> userTempPasswordSelectResult = this.callExternalService(B2BPOSWS,
                    "dsUserTempPasswordBrowseListByParam", new HashMap<String, Object>() {{
                        put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
                    }});
            Integer totalCountTemp = (Integer) userTempPasswordSelectResult.get("TOTALCOUNT");
            if (totalCountTemp == null) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при получении временного пароля.");
                    }});
                }}, jsonResult);
                log.append("Произошла ошибка при получении временного пароля.");
                passwordCreateRecoverToAudit(userGroupsList, log);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            if (totalCountTemp == 0) { // если записи в таблице нет - создать
                // создание записи во временной таблице
                Map<String, Object> userTempPasswordCreateResult = (Map<String, Object>) this.callExternalService(B2BPOSWS,
                        "dsUserTempPasswordCreate", new HashMap<String, Object>() {{
                            put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
                            put("TRYCOUNT", 0);
                            put("LASTENTEREDEMAIL", enteredEmail);
                            put("TEMPPASSWORD", tempPasswordShaHex);
                            put("PWDEXPDATE", XMLUtil.convertDate(passwordExpDate));
                        }}).get(Constants.RESULT);
                if (userTempPasswordCreateResult == null) {
                    jsonResult.setResultStatus(JsonResult.RESULT_OK);
                    this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                        put(Constants.RESULT, new HashMap<String, Object>() {{
                            put(ERROR, "Произошла ошибка при получении временного пароля.");
                        }});
                    }}, jsonResult);
                    log.append("Произошла ошибка при получении временного пароля.");
                    passwordCreateRecoverToAudit(userGroupsList, log);
                    return Response.status(Response.Status.OK).entity(jsonResult).build();
                }
                Long userTempPasswordId = (Long) userTempPasswordCreateResult.get("USERTEMPPASSWORDID");
                if (userTempPasswordId == null) {
                    jsonResult.setResultStatus(JsonResult.RESULT_OK);
                    this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                        put(Constants.RESULT, new HashMap<String, Object>() {{
                            put(ERROR, "Произошла ошибка при получении временного пароля.");
                        }});
                    }}, jsonResult);
                    log.append("произошла ошибка при получении временного пароля.");
                    passwordCreateRecoverToAudit(userGroupsList, log);
                    return Response.status(Response.Status.OK).entity(jsonResult).build();
                }

                // проверка почты при первом обращении
                if (!userCurrentEmail.equals(enteredEmail)) { // если почта не совпала - инкрементнуть количество попыток и вывести сообщение
                    Map<String, Object> userTempPasswordUpdateResult = (Map<String, Object>) this.callExternalService(B2BPOSWS,
                            "dsUserTempPasswordUpdate", new HashMap<String, Object>() {{
                                put("USERTEMPPASSWORDID", userTempPasswordId);
                                put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
                                put("TRYCOUNT", 1);
                                put("LASTENTEREDEMAIL", enteredEmail);
                                put("TEMPPASSWORD", tempPasswordShaHex);
                                put("PWDEXPDATE", XMLUtil.convertDate(passwordExpDate));
                            }}).get(Constants.RESULT);
                    if (userTempPasswordUpdateResult == null || userTempPasswordUpdateResult.get("USERTEMPPASSWORDID") == null) {
                        jsonResult.setResultStatus(JsonResult.RESULT_OK);
                        this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                            put(Constants.RESULT, new HashMap<String, Object>() {{
                                put(ERROR, "Произошла ошибка при получении временного пароля.");
                            }});
                        }}, jsonResult);
                        log.append("произошла ошибка при получении временного пароля.");
                        passwordCreateRecoverToAudit(userGroupsList, log);
                        return Response.status(Response.Status.OK).entity(jsonResult).build();
                    }
                    jsonResult.setResultStatus(JsonResult.RESULT_OK);
                    this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                        put(Constants.RESULT, new HashMap<String, Object>() {{
                            put(ERROR, String.format("Введен неверный адрес электронной почты. Осталось попыток: %d",
                                    tryCountFromSettings - 1));
                        }});
                    }}, jsonResult);
                    log.append(String.format("Введен неверный адрес электронной почты. Осталось попыток: %d",
                            tryCountFromSettings - 1));
                    passwordCreateRecoverToAudit(userGroupsList, log);
                    return Response.status(Response.Status.OK).entity(jsonResult).build();
                } else { // если почта таки совпала
                    // просрочить пароль пользователя
                    Calendar calendarForAccountPasswordExpDate = GregorianCalendar.getInstance();
                    calendarForAccountPasswordExpDate.add(Calendar.DATE, -1);
                    Date newDate = calendarForAccountPasswordExpDate.getTime();
                    Map<String, Object> accountPasswordUpdateResult = this.callExternalService(B2BPOSWS,
                            "dsB2BAdminAccountChangePasswordExpDate", new HashMap<String, Object>() {{
                                put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
                                put("PWDEXPDATE", XMLUtil.convertDate(newDate));
                            }});
                    if (!accountPasswordUpdateResult.get(STATUS).toString().equalsIgnoreCase("OK")) {
                        jsonResult.setResultStatus(JsonResult.RESULT_OK);
                        this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                            put(Constants.RESULT, new HashMap<String, Object>() {{
                                put(ERROR, "Произошла ошибка при получении временного пароля.");
                            }});
                        }}, jsonResult);
                        log.append("произошла ошибка при получении временного пароля.");
                        passwordCreateRecoverToAudit(userGroupsList, log);
                        return Response.status(Response.Status.OK).entity(jsonResult).build();
                    }

                    // выслать на почту письмо с паролем
                    Map<String, Object> sendEmailWithPasswordResult = null;
                    for (int i = 0; i < 5; i++) {
                        if (sendEmailWithPasswordResult == null || (sendEmailWithPasswordResult.get(Constants.RESULT) == null) ||
                                (!sendEmailWithPasswordResult.get(Constants.RESULT).toString().equalsIgnoreCase("ok"))) {
                            sendEmailWithPasswordResult = this.callExternalService(
                                    "signwebsmsws", "mailmessage", new HashMap<String, Object>() {{
                                        put("SMTPSubject", "Временный пароль для входа");
                                        put("SMTPReceipt", enteredEmail);
                                        put("SMTPMESSAGE", String.format("Временный пароль для входа: %s", tempPassword));
                                    }});
                        } else {
                            break;
                        }
                    }
                    if (sendEmailWithPasswordResult == null || (sendEmailWithPasswordResult.get(Constants.RESULT) == null) ||
                            (!"ok".equalsIgnoreCase(sendEmailWithPasswordResult.get(Constants.RESULT).toString()))) { // если отправить почту таки не удалось
                        jsonResult.setResultStatus(JsonResult.RESULT_OK);
                        this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                            put(Constants.RESULT, new HashMap<String, Object>() {{
                                put(ERROR, "Произошла ошибка при получении временного пароля.");
                            }});
                        }}, jsonResult);
                        log.append("произошла ошибка при получении временного пароля.");
                        passwordCreateRecoverToAudit(userGroupsList, log);
                        return Response.status(Response.Status.OK).entity(jsonResult).build();
                    }

                    // обнулить количесто попыток
                    Map<String, Object> userTempPasswordUpdateResult = (Map<String, Object>) this.callExternalService(B2BPOSWS,
                            "dsUserTempPasswordUpdate", new HashMap<String, Object>() {{
                                put("USERTEMPPASSWORDID", userTempPasswordId);
                                put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
                                put("TRYCOUNT", 0);
                                put("LASTENTEREDEMAIL", enteredEmail);
                                put("TEMPPASSWORD", tempPasswordShaHex);
                                put("PWDEXPDATE", XMLUtil.convertDate(passwordExpDate));
                            }}).get(Constants.RESULT);
                    if (userTempPasswordUpdateResult == null || userTempPasswordUpdateResult.get("USERTEMPPASSWORDID") == null) {
                        jsonResult.setResultStatus(JsonResult.RESULT_OK);
                        this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                            put(Constants.RESULT, new HashMap<String, Object>() {{
                                put(ERROR, "Произошла ошибка при получении временного пароля.");
                            }});
                        }}, jsonResult);
                        log.append("произошла ошибка при получении временного пароля.");
                        passwordCreateRecoverToAudit(userGroupsList, log);
                        return Response.status(Response.Status.OK).entity(jsonResult).build();
                    }

                    if (phoneNumber != null) {
                        Map<String, Object> accountUpdatePhoneNumberResult = (Map<String, Object>) this.callExternalService(B2BPOSWS, "dsB2BUserChangePhoneNumber", new HashMap<String, Object>() {{
                            put("EMPLOYEEID", employeeId);
                            put("PHONE1", phoneNumber);
                        }}).get(Constants.RESULT);
                        if (accountUpdatePhoneNumberResult == null || accountUpdatePhoneNumberResult.get("EMPLOYEEID") == null) {
                            jsonResult.setResultStatus(JsonResult.RESULT_OK);
                            this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                                put(Constants.RESULT, new HashMap<String, Object>() {{
                                    put(ERROR, "Произошла ошибка при получении временного пароля.");
                                }});
                            }}, jsonResult);
                            log.append("произошла ошибка при получении временного пароля.");
                            passwordCreateRecoverToAudit(userGroupsList, log);
                            return Response.status(Response.Status.OK).entity(jsonResult).build();
                        }
                    }

                    jsonResult.setResultStatus(JsonResult.RESULT_OK);
                    this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                        put(Constants.RESULT, new HashMap<String, Object>() {{
                            put("Message", String.format("На указанную почту %s выслан временный пароль для входа.", enteredEmail));
                        }});
                    }}, jsonResult);
                    log.append(String.format("На указанную почту [%s] выслан временный пароль для входа.", enteredEmail));
                    passwordCreateRecoverToAudit(userGroupsList, log);
                    return Response.status(Response.Status.OK).entity(jsonResult).build();
                }
            } else { // если с данного логина уже были попытки получить временный пароль
                Map<String, Object> userTempPassword; // данные о временном пароле
                try {
                    userTempPassword = ParamGetter.getFirstItemFromResultMap(userTempPasswordSelectResult);
                } catch (NullPointerException e) {
                    jsonResult.setResultStatus(JsonResult.RESULT_OK);
                    this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                        put(Constants.RESULT, new HashMap<String, Object>() {{
                            put(ERROR, "Произошла ошибка при получении временного пароля.");
                        }});
                    }}, jsonResult);
                    log.append("произошла ошибка при получении временного пароля.");
                    passwordCreateRecoverToAudit(userGroupsList, log);
                    return Response.status(Response.Status.OK).entity(jsonResult).build();
                }
                Long tryCount = (Long) userTempPassword.get("TRYCOUNT");
                Long userTempPasswordId = (Long) userTempPassword.get("USERTEMPPASSWORDID");
                if (tryCount == null || userTempPasswordId == null) {
                    jsonResult.setResultStatus(JsonResult.RESULT_OK);
                    this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                        put(Constants.RESULT, new HashMap<String, Object>() {{
                            put(ERROR, "Произошла ошибка при получении временного пароля.");
                        }});
                    }}, jsonResult);
                    log.append("произошла ошибка при получении временного пароля.");
                    passwordCreateRecoverToAudit(userGroupsList, log);
                    return Response.status(Response.Status.OK).entity(jsonResult).build();
                }

                // если количество попыток уже превышено - выдаем ошибку
                if (tryCount >= tryCountFromSettings) {
                    jsonResult.setResultStatus(JsonResult.RESULT_OK);
                    this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                        put(Constants.RESULT, new HashMap<String, Object>() {{
                            put(ERROR, "Превышено максимально количество попыток получения временного пароля.");
                        }});
                    }}, jsonResult);
                    log.append("превышено максимально количество попыток получения временного пароля.");
                    passwordCreateRecoverToAudit(userGroupsList, log);
                    return Response.status(Response.Status.OK).entity(jsonResult).build();
                }

                // проверка почты
                if (!userCurrentEmail.equals(enteredEmail)) { // если почты снова не совпадают по инкрементнуть кол - во попыток и вывести сообщение
                    Map<String, Object> userTempPasswordUpdateResult = (Map<String, Object>) this.callExternalService(B2BPOSWS,
                            "dsUserTempPasswordUpdate", new HashMap<String, Object>() {{
                                put("USERTEMPPASSWORDID", userTempPasswordId);
                                put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
                                put("TRYCOUNT", tryCount + 1);
                                put("LASTENTEREDEMAIL", enteredEmail);
                                put("TEMPPASSWORD", tempPasswordShaHex);
                                put("PWDEXPDATE", XMLUtil.convertDate(passwordExpDate));
                            }}).get(Constants.RESULT);
                    if (userTempPasswordUpdateResult == null || userTempPasswordUpdateResult.get("USERTEMPPASSWORDID") == null) {
                        jsonResult.setResultStatus(JsonResult.RESULT_OK);
                        this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                            put(Constants.RESULT, new HashMap<String, Object>() {{
                                put(ERROR, "Произошла ошибка при получении временного пароля.");
                            }});
                        }}, jsonResult);
                        log.append("произошла ошибка при получении временного пароля.");
                        passwordCreateRecoverToAudit(userGroupsList, log);
                        return Response.status(Response.Status.OK).entity(jsonResult).build();
                    }
                    jsonResult.setResultStatus(JsonResult.RESULT_OK);
                    this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                        put(Constants.RESULT, new HashMap<String, Object>() {{
                            put(ERROR, String.format("Введен неверный адрес электронной почты. Осталось попыток: %d",
                                    tryCountFromSettings - (tryCount + 1)));
                        }});
                    }}, jsonResult);
                    log.append(String.format("Введен неверный адрес электронной почты. Осталось попыток: %d",
                            tryCountFromSettings - (tryCount + 1)));
                    passwordCreateRecoverToAudit(userGroupsList, log);
                    return Response.status(Response.Status.OK).entity(jsonResult).build();
                } else { // если почта совпала то обнулить количество попыток и отправить пароль на почту
                    // обнуление счетчика
                    Map<String, Object> userTempPasswordUpdateResult = (Map<String, Object>) this.callExternalService(B2BPOSWS,
                            "dsUserTempPasswordUpdate", new HashMap<String, Object>() {{
                                put("USERTEMPPASSWORDID", userTempPasswordId);
                                put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
                                put("TRYCOUNT", 0);
                                put("LASTENTEREDEMAIL", enteredEmail);
                                put("TEMPPASSWORD", tempPasswordShaHex);
                                put("PWDEXPDATE", XMLUtil.convertDate(passwordExpDate));
                            }}).get(Constants.RESULT);
                    if (userTempPasswordUpdateResult == null || userTempPasswordUpdateResult.get("USERTEMPPASSWORDID") == null) {
                        jsonResult.setResultStatus(JsonResult.RESULT_OK);
                        this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                            put(Constants.RESULT, new HashMap<String, Object>() {{
                                put(ERROR, "Произошла ошибка при получении временного пароля.");
                            }});
                        }}, jsonResult);
                        log.append("произошла ошибка при получении временного пароля.");
                        passwordCreateRecoverToAudit(userGroupsList, log);
                        return Response.status(Response.Status.OK).entity(jsonResult).build();
                    }

                    // отправка пароля на почту
                    Map<String, Object> sendEmailWithPasswordResult = null;
                    for (int i = 0; i < 5; i++) {
                        if (sendEmailWithPasswordResult == null || (sendEmailWithPasswordResult.get(Constants.RESULT) == null) ||
                                (!sendEmailWithPasswordResult.get(Constants.RESULT).toString().equalsIgnoreCase("ok"))) {
                            sendEmailWithPasswordResult = this.callExternalService(
                                    "signwebsmsws", "mailmessage", new HashMap<String, Object>() {{
                                        put("SMTPSubject", "Временный пароль для входа");
                                        put("SMTPReceipt", enteredEmail);
                                        put("SMTPMESSAGE", String.format("Временный пароль для входа: %s", tempPassword));
                                    }});
                        } else {
                            break;
                        }
                    }
                    if (sendEmailWithPasswordResult == null || (sendEmailWithPasswordResult.get(Constants.RESULT) == null) ||
                            (!"ok".equalsIgnoreCase(sendEmailWithPasswordResult.get(Constants.RESULT).toString()))) { // если отправить почту таки не удалось
                        jsonResult.setResultStatus(JsonResult.RESULT_OK);
                        this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                            put(Constants.RESULT, new HashMap<String, Object>() {{
                                put(ERROR, "Произошла ошибка при получении временного пароля.");
                            }});
                        }}, jsonResult);
                        log.append("произошла ошибка при получении временного пароля.");
                        passwordCreateRecoverToAudit(userGroupsList, log);
                        return Response.status(Response.Status.OK).entity(jsonResult).build();
                    }

                    if (phoneNumber != null) {
                        Map<String, Object> accountUpdatePhoneNumberResult = (Map<String, Object>) this.callExternalService(B2BPOSWS, "dsB2BUserChangePhoneNumber", new HashMap<String, Object>() {{
                            put("EMPLOYEEID", employeeId);
                            put("PHONE1", phoneNumber);
                        }}).get(Constants.RESULT);
                        if (accountUpdatePhoneNumberResult == null || accountUpdatePhoneNumberResult.get("EMPLOYEEID") == null) {
                            jsonResult.setResultStatus(JsonResult.RESULT_OK);
                            this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                                put(Constants.RESULT, new HashMap<String, Object>() {{
                                    put(ERROR, "Произошла ошибка при получении временного пароля.");
                                }});
                            }}, jsonResult);
                            log.append("произошла ошибка при получении временного пароля.");
                            passwordCreateRecoverToAudit(userGroupsList, log);
                            return Response.status(Response.Status.OK).entity(jsonResult).build();
                        }
                    }

                    jsonResult.setResultStatus(JsonResult.RESULT_OK);
                    this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                        put(Constants.RESULT, new HashMap<String, Object>() {{
                            put("Message", String.format("На указанную почту %s выслан временный пароль для входа.", enteredEmail));
                        }});
                    }}, jsonResult);
                    log.append(String.format("На указанную почту [%s] выслан временный пароль для входа.", enteredEmail));
                    passwordCreateRecoverToAudit(userGroupsList, log);
                    return Response.status(Response.Status.OK).entity(jsonResult).build();
                }
            }

        } catch (Throwable throwable) {
            JsonResult jsonResult = new JsonResult();
            jsonResult.setResultStatus(JsonResult.RESULT_OK);
            this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                put(Constants.RESULT, new HashMap<String, Object>() {{
                    put(ERROR, "Произошла ошибка при получении временного пароля.");
                }});
            }}, jsonResult);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        }
    }

    @POST
    @Path("/dsB2BUserChangePasswordAccountWithTempPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BUserChangePasswordAccountWithTempPassword(@FormParam(FORM_PARAM_NAME) String paramStr) {
        try {
            Obj paramsObj = this.requestWorker.deserializeJSON(paramStr, Obj.class);
            JsonResult jsonResult = new JsonResult();
            if (paramsObj == null) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при смене пароля.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            Map<String, Object> obj = objMap.get("obj") == null ? null : getMapParamName(objMap, "obj");
            if (obj == null) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при смене пароля.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            String enteredLogin = (String) obj.get("login"); // введенный логин
            String enteredNewPassword = (String) obj.get("newPassword"); // новый пароль
            if (enteredLogin == null || enteredNewPassword == null
                    || enteredLogin.isEmpty() || enteredNewPassword.isEmpty()) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при смене пароля.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Map<String, Object> userAccountSelectResult = this.callExternalService(B2BPOSWS,
                    "dsAdminGetUsersList", new HashMap<String, Object>() {{
                        put("CUSTOMWHERE", String.format(" T.LOGIN = \'%s\' ", enteredLogin));
                    }});
            Integer userAccountSelectResultTotalCount = (Integer) userAccountSelectResult.get("TOTALCOUNT");
            if (userAccountSelectResultTotalCount == null || !userAccountSelectResultTotalCount.equals(1)) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Введен неверный логин.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Map<String, Object> userAccount;
            try {
                userAccount = ParamGetter.getFirstItemFromResultMap(userAccountSelectResult);
            } catch (NullPointerException e) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Введен неверный логин.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            if (userAccount == null) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Введен неверный логин.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            BigDecimal userAccountId = (BigDecimal) userAccount.get(USER_ACCOUNT_ID_PARAM_NAME); // ид пользователя
            Map<String, Object> userTempPasswordSelectResult = this.callExternalService(B2BPOSWS,
                    "dsUserTempPasswordBrowseListByParam", new HashMap<String, Object>() {{
                        put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
                    }});
            Integer userTempPasswordSelectResultTotalCount = (Integer) userTempPasswordSelectResult.get("TOTALCOUNT");
            if (userTempPasswordSelectResultTotalCount == null) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при смене пароля.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            if (userTempPasswordSelectResultTotalCount == 0) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Необходимо получить временный пароль.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Map<String, Object> userTempPassword; // данные о временном пароле
            try {
                userTempPassword = ((List<Map<String, Object>>) userTempPasswordSelectResult.get(Constants.RESULT)).get(0);
            } catch (NullPointerException e) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при смене пароля.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Result passwordValid = this.verifier.isPasswordValid(enteredNewPassword);
            if (!passwordValid.equals(Result.OK)) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, passwordValid.getDescription());
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Calendar calendar = GregorianCalendar.getInstance();
            if (((Date) userTempPassword.get("PWDEXPDATE")).before(calendar.getTime())) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Срок действия временного пароля истек.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Map<String, Object> userAccountUpdatePasswordResult =
                    this.callExternalService(B2BPOSWS, "dsB2BAdminUserChangePasswordAccountWithoutOldPass",
                            new HashMap<String, Object>() {{
                                put("ACCID", userAccountId);
                                put("NEWPASS", enteredNewPassword);
                            }});
            if (!userAccountUpdatePasswordResult.get(STATUS).equals("OK")) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при смене пароля.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            Map<String, Object> deleteUserTempPasswordResult = this.callExternalService(B2BPOSWS,
                    "dsUserTempPasswordDelete", new HashMap<String, Object>() {{
                        put("USERTEMPPASSWORDID", userTempPassword.get("USERTEMPPASSWORDID"));
                    }});
            if (!deleteUserTempPasswordResult.get(STATUS).equals("OK")) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Произошла ошибка при смене пароля.");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
            jsonResult.setResultStatus(JsonResult.RESULT_OK);
            this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                put(Constants.RESULT, new HashMap<String, Object>() {{
                    put("Message", "Пароль успешно сменен.");
                }});
            }}, jsonResult);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        } catch (Throwable throwable) {
            JsonResult jsonResult = new JsonResult();
            jsonResult.setResultStatus(JsonResult.RESULT_OK);
            this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                put(Constants.RESULT, new HashMap<String, Object>() {{
                    put(ERROR, "Произошла ошибка при смене пароля.");
                }});
            }}, jsonResult);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        }
    }

    @POST
    @Path("/dsB2BCheckPasswordForTempProperty")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BCheckPasswordForTempProperty(@FormParam(FORM_PARAM_NAME) String paramsStr) {

        Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        JsonResult jsonResult = new JsonResult();
        boolean passwordIsTemp = false;
        boolean passwordDateExpired = false;

        if (paramsObj == null) {
            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            this.requestWorker.serializeJSON(new HashMap<String, Object>() {
                {
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Не хватает входных параметров");
                    }});
                }
            }, jsonResult);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        }

        Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
        Map<String, Object> obj = getMapParamName(objMap, "obj");
        String login = getStringParam(obj.get("login"));
        String password = getStringParam(obj.get(PASS_PARAM_NAME));

        String missingParamName = "";
        if (login.isEmpty()) {
            missingParamName = "login";
        } else if (password.isEmpty()) {
            missingParamName = PASS_PARAM_NAME;
        }

        if (!missingParamName.isEmpty()) {
            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            this.requestWorker.serializeJSON(new HashMap<String, Object>() {
                {
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put(ERROR, "Не хватает входных параметров");
                    }});
                }
            }, jsonResult);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        }

        //получаем список пользователей по логину из БД
        Map<String, Object> userAccountSelectResult = this.callExternalService(B2BPOSWS,
                "dsAdminGetUsersList", new HashMap<String, Object>() {{
                    put("CUSTOMWHERE", String.format(" T.LOGIN = \'%s\' ", login));
                }});
        Integer totalCount = (Integer) userAccountSelectResult.get("TOTALCOUNT");
        if (totalCount == null || !totalCount.equals(1)) {
            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            this.requestWorker.serializeJSON(userAccountSelectResult, jsonResult);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        }
        // получае аккаунт пользователя для дальнейшей выборки по нему временных паролей
        Map<String, Object> userAccount;
        try {
            userAccount = ((List<Map<String, Object>>) userAccountSelectResult.get(Constants.RESULT)).get(0);
        } catch (NullPointerException e) {
            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            this.requestWorker.serializeJSON(userAccountSelectResult, jsonResult);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        }
        Map<String, Object> userTempPasswordSelectResult = this.callExternalService(B2BPOSWS,
                "dsUserTempPasswordBrowseListByParam", new HashMap<String, Object>() {{
                    put(USER_ACCOUNT_ID_PARAM_NAME, userAccount.get(USER_ACCOUNT_ID_PARAM_NAME));
                }});
        Integer totalCountTemp = (Integer) userTempPasswordSelectResult.get("TOTALCOUNT");

        if (totalCountTemp != null && totalCountTemp == 1) {
            Long tryCountTemp = (Long) ((List<Map<String, Object>>) userTempPasswordSelectResult.get(Constants.RESULT)).get(0).get("TRYCOUNT");
            Date passwordExpDate = (Date) ((List<Map<String, Object>>) userTempPasswordSelectResult.get(Constants.RESULT)).get(0).get("PWDEXPDATE");
            String tempPasswordHex = (String) ((List<Map<String, Object>>) userTempPasswordSelectResult.get(Constants.RESULT)).get(0).get("TEMPPASSWORD");
            String enteredTempPasswordHex = DigestUtils.sha512Hex(password);
            if (tryCountTemp != null && tryCountTemp == 0
                    && totalCount.equals(1) && tempPasswordHex.equals(enteredTempPasswordHex)) {
                passwordIsTemp = true;
                if (passwordExpDate.before(new Date())) {
                    passwordDateExpired = true;
                }
            }
        }

        if (passwordIsTemp) {
            if (!passwordDateExpired) {
                jsonResult.setResultStatus(JsonResult.RESULT_OK);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                    put(Constants.RESULT, new HashMap<String, Object>() {{
                        put("ISTEMPPASSWORD", "1");
                    }});
                }}, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            } else {
                jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
                this.requestWorker.serializeJSON(new HashMap<String, Object>() {
                    {
                        put(Constants.RESULT, new HashMap<String, Object>() {{
                            put(ERROR, "Время действия временного пароля истекло.");
                        }});
                    }
                }, jsonResult);
                return Response.status(Response.Status.OK).entity(jsonResult).build();
            }
        } else {
            jsonResult.setResultStatus(JsonResult.RESULT_OK);
            this.requestWorker.serializeJSON(new HashMap<String, Object>() {{
                put(Constants.RESULT, new HashMap<String, Object>() {{
                    put("ISTEMPPASSWORD", "0");
                }});
            }}, jsonResult);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        }
    }

    /**
     * Метод получения ФИО клиентского менеджера по табельному номеру
     *
     * @param paramsStr
     * @return
     */
    @POST
    @Path("/dsB2BGetClientManagerFIO")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BGetClientManagerFIO(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2BGetClientManagerFIO");
    }

    @POST
    @Path("/dsB2B_JournalDataBrowseListByParamWrapper")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2B_JournalDataBrowseListByParamWrapper(@FormParam(FORM_PARAM_NAME) String paramsStr) {
        return dsB2BCallService(paramsStr, B2BPOSWS, "dsB2B_JournalDataBrowseListByParamWrapper");
    }

}
