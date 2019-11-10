package com.bivgroup.login.base;

import com.bivgroup.config.Config;
import com.bivgroup.config.ConfigUtils;
import com.bivgroup.core.audit.Audit;
import com.bivgroup.core.audit.AuditIpInfo;
import com.bivgroup.core.audit.ResultOperation;
import com.bivgroup.ldap.authorization.LdapUserService;
import com.bivgroup.ldap.exception.ServiceException;
import com.bivgroup.ldap.pojo.ActiveDirectoryAuthUserInfo;
import com.bivgroup.ldap.pojo.ActiveDirectoryUserInfo;
import com.bivgroup.login.base.pojo.AuthUserInfo;
import com.bivgroup.login.base.utils.StaticDataHelper;
import com.bivgroup.password.PasswordStrengthVerifier;
import com.bivgroup.pojo.JsonResult;
import com.bivgroup.pojo.Obj;
import com.bivgroup.service.SoapServiceCaller;
import com.bivgroup.sessionutils.BaseSessionController;
import com.bivgroup.sessionutils.BroadcastTarget;
import com.bivgroup.sessionutils.SessionController;
import com.bivgroup.sessionutils.cdi.CounterCache;
import com.bivgroup.sessionutils.cdi.CounterCacheFactory;
import com.bivgroup.sessionutils.cdi.CounterCacheImpl;
import com.bivgroup.sessionutils.cdi.SessionCounter;
import com.bivgroup.utils.RequestWorker;
import com.bivgroup.utils.serviceloader.DefaultServiceLoader;
import com.bivgroup.xmlutil.XmlUtil;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.bivgroup.login.base.common.Constants.*;
import static com.bivgroup.password.PasswordStrengthVerifier.*;
import static com.bivgroup.sessionutils.BaseSessionController.SESSION_HASH_PARAMNAME;
import static com.bivgroup.sessionutils.FileSessionControllerImpl.SESSION_TIME_PARAMNAME;
import static com.bivgroup.utils.ParamGetter.*;

public abstract class LoginBase {
    private static final String AUDIT_LOGGERNAME = "AUDIT";
    private static final String CHANGE_PASS_FLAG_NAME = "isNeedChangePwd";
    private static final String TRUE_STR_VALUE = "true";
    private static final String LINK_FIND_PARAM_NAME = "linkFind";

    private RequestWorker requestWorker;
    private Logger logger;
    /**
     * Флаг сообщающий нужно ли проверять сложность введенного пароля
     */
    private boolean isNeedCheckCorrectPassword;
    /**
     * Флаг сообщающий требуется ли проверять доступность проекта, в котором авторизовываемся, для пользователя
     */
    private boolean isNeedCheckProject;
    /**
     * Флаг сообщающий требуется ли авторизация через active directory
     */
    private boolean isAuthorizationAcrossActiveDirectory;
    /**
     * Флаг сообщающий требуется ли искать связь с AD по логину в таблице CORE_USERACCOUNT
     */
    private boolean isNeedFindLinkWithAdByCoreLogin;
    private Logger auditLogger;
    private Long sessionTimeOut = 10L;
    private Long adminSessionTimeOut = 10L;
    private SessionCounter sc = null;
    private boolean doCheckSessionCount;

    protected SoapServiceCaller soapServiceCaller;
    protected Config config;

    protected LoginBase() {
        String useServiceName = ConfigUtils.getProjectProperty("ru.diasoft.services.insurance.ServiceName", "login-service");
        this.config = Config.getConfig(useServiceName);
        this.soapServiceCaller = DefaultServiceLoader.loadServiceAny(SoapServiceCaller.class);
        this.requestWorker = new RequestWorker();
        this.isNeedCheckCorrectPassword = Boolean.valueOf(this.config.getParam("isNeedCheckCorrectPassword", TRUE_STR_VALUE));
        this.isNeedCheckProject = Boolean.valueOf(this.config.getParam("isNeedCheckProject", TRUE_STR_VALUE));
        this.isAuthorizationAcrossActiveDirectory = Boolean.valueOf(this.config.getParam("isAuthorizationAcrossActiveDirectory", TRUE_STR_VALUE));
        this.doCheckSessionCount = Boolean.valueOf(this.config.getParam("doCheckSessionCount", TRUE_STR_VALUE));
        this.isNeedFindLinkWithAdByCoreLogin = Boolean.valueOf(this.config.getParam("isNeedFindLinkWithAdByCoreLogin", "false"));
        this.auditLogger = Logger.getLogger(AUDIT_LOGGERNAME);
        this.logger = Logger.getLogger(this.getClass());
        if (sc == null) {
            CounterCacheFactory ccf = new CounterCacheFactory();
            CounterCache cm = ccf.getCacheManager(CounterCacheImpl.class);
            sc = cm.getSessionCounter();
        }
        this.sessionTimeOut = getSessionTimeOut();
        if (!sc.isBuilt()) {
            String consulUri = config.getParam("CONSULAPPLISTSOURCE", "");
            String adminGroupSysname = config.getParam("ADMINGROUPSYSNAME", "-");
            long maxSessionCountForAdmin = Long.parseLong(config.getParam("ADMINGROUPMAXSESSIONCNT", "5"));
            long maxSessionCount = Long.parseLong(config.getParam("NOTADMINGROUPMAXSESSIONCNT", "5"));
            sc
                    .setConsuURI(consulUri)
                    .setUsernameParamname(SYSTEM_PARAM_NAME_LOGIN)
                    .setAdminGroupSysname(adminGroupSysname)
                    .setMaxSessionsCountForAdmin(maxSessionCountForAdmin)
                    .setMaxSessionsCount(maxSessionCount)
                    .setRestPath("/admrestws/rest/login/getActiveSessionsOfUser")
                    .build();
        }
        sc.setSessionTimeOut(this.sessionTimeOut);
    }

    private PasswordStrengthVerifier createPasswordVerifier() {
        try {
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> passwordSetting = soapServiceCaller.callExternalService(ADMINWS, "getPwdAcctSettings", params);
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

    protected Response dsLoginBase(String paramsStr, HttpHeaders httpHeaders, Audit audit) {
        return dsB2BLoginBase(paramsStr, httpHeaders, audit);
    }

    protected Response dsB2BLoginBase(String paramsStr, HttpHeaders httpHeaders, Audit audit) {
        Obj paramsObj = this.requestWorker.deserializeJSON(paramsStr, Obj.class);
        JsonResult jsonResult = new JsonResult();
        ResultOperation resultOperation = ResultOperation.FALTURE;
        String auditLogin = "";
        Long userAccountId = null;
        StringBuilder auditMessageBuilder = new StringBuilder();
        if (paramsObj != null) {
            Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
            Map<String, Object> obj = getMapParamName(objMap, "obj");
            String login = getStringParam(obj, "login");
            auditLogin = login;
            String password = getStringParam(obj, PASS_PARAM_NAME);

            String missingParamName = "";
            if (login.isEmpty()) {
                missingParamName = "login";
            } else if (password.isEmpty()) {
                missingParamName = PASS_PARAM_NAME;
            }

            if (missingParamName.isEmpty()) {
                String projectName = getStringParam(httpHeaders.getRequestHeaders().getFirst("projectName"));
                Map<String, Object> loginResult = doB2BLogin(login, password, projectName, auditMessageBuilder);
                if (loginResult.isEmpty()) {
                    jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
                } else {
                    this.requestWorker.serializeJSON(loginResult, jsonResult);
                    Map<String, Object> innerLoginResult = getMapParamName(loginResult, RESULT);
                    if (getStringParam(innerLoginResult, ERROR).isEmpty()) {
                        resultOperation = ResultOperation.SUCCESE;
                    }
                    auditLogin = getStringParam(innerLoginResult, UPPERCASE_LOGIN_PARAM_NAME);
                    userAccountId = getLongParam(innerLoginResult, USER_ACCOUNT_ID_PARAM_NAME);
                }
            } else {
                String methodName = "dsB2BLogin";
                // дополнительная обработка ответа со статусом 'EMPTYLOGINPASS' (генерация описания ошибки, сохранение в БД)
                processResultStatusEmptyLoginPass(jsonResult, methodName, B2BPOSWS, missingParamName, obj);
            }
        } else {
            jsonResult.setResultJson(JsonResult.RESULT_ERROR);
        }
        String ipChain = httpHeaders.getHeaderString(IP_CHAIN_HEADER_NAME);
        AuditIpInfo ipInfo = new AuditIpInfo().setIpChainAddresses(ipChain == null || ipChain.isEmpty() ? DEFAULT_IP_CHAIN : ipChain);
        audit.audit("authorization", resultOperation, auditLogin, userAccountId, ipInfo, auditMessageBuilder.toString());
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }


    /**
     * Метод входа.
     * Пытается войти через AD или B2B. Так создает сессию и анализирует сложность пароля и др.
     *
     * @param login               логин, с помощью которого пытаются войти
     * @param password            пароль, с помощью которого пытаются войти
     * @param projectName         имя проекта в который пытаются войти
     * @param auditMessageBuilder билдер для создания сообщения аудита
     * @return "словарь" результата входа
     */
    public Map<String, Object> doB2BLogin(String login, String password, String projectName, StringBuilder auditMessageBuilder) {
        auditMessageBuilder.append("Попытка входа пользователя с логином: ").append(login).append('.');
        String username = XmlUtil.getUserName(login);
        StringBuilder log = new StringBuilder();
        log.append("Попытка входа пользователя '").append(username).append("', ");
        Map<String, Object> authorizationInfo = selectAuthorizationUser(username, password, auditMessageBuilder);
        Map<String, Object> subResult = new HashMap<>();
        Long userAccountId = getLongParam(authorizationInfo, USER_ACCOUNT_ID_PARAM_NAME);
        String errorText = getStringParam(authorizationInfo, UPPERCASE_ERROR);
        boolean isPwdExpired = getBooleanParam(authorizationInfo, "FORCEPWDCHANGE", false);
        if ((errorText.isEmpty() || isPwdExpired) && (userAccountId != null)) {
            AuthUserInfo authUserInfo = new AuthUserInfo(login, password, projectName, userAccountId, isPwdExpired);
            log.append("пользователь найден, ID = ").append(userAccountId).append(", ");
            subResult.clear();
            // в authorizationInfo добавляется поле USER_SESSION_COUNT_PARAMNAME
            // так как поле ISCONCURRENT можно было добавить только в тот вызов adminws
            errorText = loadUserInfoByAccount(authUserInfo, log, authorizationInfo, subResult);
            boolean canCreateNewSession = !doCheckSessionCount || sc.userCanCreateNewSession(new ProfileDataWrapper(authorizationInfo));
            if (errorText.isEmpty() && !canCreateNewSession) {
                subResult.clear();
                errorText = getStringParam(authorizationInfo, "sessionLimitError");
            }
        }
        if (errorText != null && !errorText.isEmpty()) {
            log.append(errorText);
            // если в под результате флаг CHANGE_PASS_FLAG_NAME равен true, то сессию генерить не надо
            // иначе невозможно будет вызвать смену пароля
            if (getBooleanParam(subResult, CHANGE_PASS_FLAG_NAME, false)) {
                subResult.put(SESSION_ID_LOWERCASE_PARAM_NAME, makeSessionId(login, null, null,
                        null, null, null, null));
            }
            auditMessageBuilder.append("\nОшибка входа пользователя с логином ").append(login)
                    .append(".\nОшибка: ").append(errorText).append('.');
            log.append(errorText);
            subResult.put(ERROR, errorText);
        } else {
            String newSession = getStringParam(subResult, SESSION_ID_PARAM_NAME);
            SessionController controller = DefaultServiceLoader.loadServiceAny(SessionController.class);
            controller.setTimeout(this.sessionTimeOut);
            Map<String, Object> decompiledSession = controller.checkSession(newSession);
            sc.updateSessionData(decompiledSession);
        }
        Map<String, Object> result = new HashMap<>();
        result.put(RESULT, subResult);
        loginToAudit(authorizationInfo, log);
        return result;
    }

    private void loginToAudit(Map<String, Object> qres, StringBuilder message) {
        Set<String> userGroups = StaticDataHelper.getUserGroups(qres);
        if (userGroups.contains("SB1")) {
            auditLogger.error(message);
        }
    }

    /**
     * Метод выбора авторизации.
     * Через active directory с помощью логина в формета login@domen и просто login.
     * И если нет связи с active directory, то стандартная авторизациия через б2б
     *
     * @param username            логин для входа
     * @param password            пароль для входа
     * @param auditMessageBuilder билдер для создания сообщения аудита
     * @return "словарь" информации об авторизации.
     */
    private Map<String, Object> selectAuthorizationUser(String username, String password, StringBuilder auditMessageBuilder) {
        Map<String, Object> result = new HashMap<>();
        boolean isLinkAddFind = false;
        if (isAuthorizationAcrossActiveDirectory) {
            auditMessageBuilder.append("\nПоиск связи с AD.");
            result = findLinkWithAdByCoreLoginAndAuthorization(username, password, auditMessageBuilder);
            isLinkAddFind = getBooleanParam(result, LINK_FIND_PARAM_NAME, false);
            // удаляем флаг из результата, чтобы не мешал в дальнейшем
            result.remove(LINK_FIND_PARAM_NAME);
            // результат авторизации через AD пусть, но связь с пользователем AD по б2б логину найдена
            // т.е. логины б2б и AD могут быть равны, нужно попытатся поискать связь с AD по другим полям
            if (result.isEmpty() || isLinkAddFind) {
                result = authorizationAcrossActiveDirectoryLogin(username, password, auditMessageBuilder);
            }
        }
        if (result.isEmpty()) {
            // если до этого мы не смогли автризоваться через AD, но связь нашли, то запрещаем
            // пользователю авторизовываться через б2б, он должен авторизовываться только через AD
            if (!isLinkAddFind) {
                result = b2bAuthorization(username, password, auditMessageBuilder);
            } else {
                result.put(UPPERCASE_ERROR, "Найдена связь с AD. Необходимо выполнить вход через учетную запись Active Directory.");
            }
        }
        if (result == null) {
            result = new HashMap<>();
        }
        return result;
    }

    /**
     * Метод поиска связи пользователя core c AD по логину.
     * Ищем пользователя в таблице CORE_USERACCOUNT по полю LOGIN,
     * если такой пользователь найден и в поле ADUSERPRINCIPALNAME
     * есть значение, значит авторизуемся через AD.
     *
     * @param username            имя пользователя, предположительно логин из core_useraccount
     * @param password            пароль пользователя
     * @param auditMessageBuilder сбор строки для айдита
     * @return результат авторизации черз AD, либо пустой "словарь", если связь с AD не найдена
     */
    private Map<String, Object> findLinkWithAdByCoreLoginAndAuthorization(String username, String password, StringBuilder auditMessageBuilder) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> searchUserAccountParams = new HashMap<>();
        searchUserAccountParams.put(RETURN_AS_HASH_MAP, true);
        searchUserAccountParams.put(UPPERCASE_LOGIN_PARAM_NAME, username);
        Map<String, Object> searchUserResult = this.soapServiceCaller.callExternalService(B2BPOSWS,
                SEARCH_LINK_AD_METHOD_NAME, searchUserAccountParams);
        Long userAccountId = getLongParam(searchUserResult, USER_ACCOUNT_ID_PARAM_NAME);
        String activeDirectoryUserPrincipalName = getStringParam(searchUserResult, ACTIVE_DIRECTORY_PRINCIPAL_NAME_PARAM_NAME);
        // связь б2б пользователя с active directory найдена
        if (!activeDirectoryUserPrincipalName.isEmpty()) {
            result.put(LINK_FIND_PARAM_NAME, true);
            // согласно задаче #34899 требуется убрать возможность авторизации через AD если связь найдена по логину в таблице CORE_USERACCOUNT
            if (this.isNeedFindLinkWithAdByCoreLogin) {
                // пытаемся авторизоваться в active directory с помощью userPrincipalName
                result = activeDirectoryAuthorizationByPrincipalName(activeDirectoryUserPrincipalName, password,
                        userAccountId, auditMessageBuilder);
            }
        }
        return result;
    }

    /**
     * Метод стандартной авторизации через б2б
     *
     * @param username            логин пользователя б2б
     * @param password            пароль пользователя б2б
     * @param auditMessageBuilder билдер для создания сообщения аудита
     * @return "словарь" информации об авторизации.
     */
    private Map<String, Object> b2bAuthorization(String username, String password, StringBuilder auditMessageBuilder) {
        Map<String, Object> result;
        auditMessageBuilder.append("\nДля данного пользователя не найдена связь с AD. Попытка входа через B2B.");
        Map<String, Object> checkLoginParams = new HashMap<>();
        checkLoginParams.put("username", username);
        checkLoginParams.put(PASS_PARAM_NAME, password);
        result = this.soapServiceCaller.callExternalService(COREWS, "getlogin", checkLoginParams, username, password);
        if (result != null && result.containsKey("ROLES")) {
            result.put(ROLE_LIST_PARAM_NAME, result.remove("ROLES"));
            auditMessageBuilder.append("\nПользователь вошел через B2B.");
        }
        return result;
    }

    /**
     * Метод авторизации в active directory.
     * Метод вначале ищет пользователя в таблице CORE_USERACCOUNT по ADUSERPRINCIPALNAME,
     * если найти пользователя не удалось, то ищем по ADUSERLOGIN.
     * Если пользователь со связью с помощью CORE_USERACCOUNT млм ADUSERPRINCIPALNAME
     * найден, то авторизуем его через active directory
     *
     * @param username            логин для входа через active directory
     * @param password            пароль для входа через active directory
     * @param auditMessageBuilder билдер для создания сообщения аудита
     * @return "словарь" информации об авторизации.
     */
    private Map<String, Object> authorizationAcrossActiveDirectoryLogin(String username, String password, StringBuilder auditMessageBuilder) {
        Long userAccountId = null;
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> searchUserAccountParams = new HashMap<>();
        searchUserAccountParams.put(RETURN_AS_HASH_MAP, true);
        searchUserAccountParams.put(ACTIVE_DIRECTORY_PRINCIPAL_NAME_PARAM_NAME, username);
        Map<String, Object> searchUserResult = this.soapServiceCaller.callExternalService(B2BPOSWS,
                SEARCH_LINK_AD_METHOD_NAME, searchUserAccountParams);
        userAccountId = getLongParam(searchUserResult, USER_ACCOUNT_ID_PARAM_NAME);
        if (userAccountId != null) {
            String activeDirectoryUserPrincipalName = getStringParam(searchUserResult, ACTIVE_DIRECTORY_PRINCIPAL_NAME_PARAM_NAME);
            result = activeDirectoryAuthorizationByPrincipalName(activeDirectoryUserPrincipalName, password, userAccountId, auditMessageBuilder);
        } else {
            searchUserAccountParams = new HashMap<>();
            searchUserAccountParams.put(RETURN_AS_HASH_MAP, true);
            searchUserAccountParams.put("ADUSERLOGIN", username);
            searchUserResult = this.soapServiceCaller.callExternalService(B2BPOSWS,
                    SEARCH_LINK_AD_METHOD_NAME, searchUserAccountParams);
            userAccountId = getLongParam(searchUserResult, USER_ACCOUNT_ID_PARAM_NAME);
            if (userAccountId != null) {
                String activeDirectoryUserPrincipalName = getStringParam(searchUserResult, ACTIVE_DIRECTORY_PRINCIPAL_NAME_PARAM_NAME);
                result = activeDirectoryAuthorizationByPrincipalName(activeDirectoryUserPrincipalName, password, userAccountId, auditMessageBuilder);
            }
        }
        return result;
    }

    /**
     * Авторизация в active directory с помощью userPrincipalName
     *
     * @param userPrincipalName   логин пользователя для авторизации в active directory
     * @param password            пароль пользователя для авторизации в active directory
     * @param userAccountId       идентификатор аккаунта
     * @param auditMessageBuilder билдер для создания сообщения аудита
     * @return "словарь" информации об авторизации.
     */
    private Map<String, Object> activeDirectoryAuthorizationByPrincipalName(String userPrincipalName, String password,
                                                                            Long userAccountId, StringBuilder auditMessageBuilder) {
        Map<String, Object> result;
        auditMessageBuilder.append("\nСвязь с AD найдена. Попытка входа через AD.");
        ActiveDirectoryAuthUserInfo authUserInfo = new ActiveDirectoryAuthUserInfo();
        authUserInfo.setUserPrincipalName(userPrincipalName);
        authUserInfo.setPassword(password);
        result = activeDirectoryAuthorization(userAccountId, authUserInfo, auditMessageBuilder);
        return result;
    }

    /**
     * Авторизация пользователя через active directory
     *
     * @param userAccountId       идентификатор связанного пользователя б2б системы
     * @param authUserInfo        данные для авторизации пользователя active directory
     * @param auditMessageBuilder билдер для создания сообщения аудита
     * @return "словарь" с информацией об авторизации пользователя через active directory
     */
    private Map<String, Object> activeDirectoryAuthorization(Long userAccountId, ActiveDirectoryAuthUserInfo authUserInfo,
                                                             StringBuilder auditMessageBuilder) {
        Map<String, Object> result = new HashMap<>();
        try {
            LdapUserService ldapUserService = new LdapUserService();
            ActiveDirectoryUserInfo userInfo = ldapUserService.authorizationActiveDirectoryUser(authUserInfo);
            if (userInfo == null) {
                result.put(UPPERCASE_ERROR, "Пользователь не найден в active directory!");
            } else {
                String login = userInfo.getUserPrincipalName();
                if (userInfo.isBlocked()) {
                    result.put(UPPERCASE_ERROR, "Пользователь AD заблокирован. Обратитесь к администратору системы!");
                } else {
                    if (!userInfo.isAccessUserIsAvailable()) {
                        result.put(UPPERCASE_ERROR, "Пользователь AD не входит в допустимую группу для входа.");
                    } else {
                        if (userInfo.isInForbiddenGroup()) {
                            result.put(UPPERCASE_ERROR, "Пользователь AD входит в запрещенную для входа группу: " + userInfo.getForbiddenGroupName() + '.');
                        } else {
                            auditMessageBuilder.append(String.format("%nПользователь %s вошел через AD.", login));
                            result.put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
                            result.put(ROLE_LIST_PARAM_NAME, loadRoleListByUserAccount(userAccountId));
                            result.put("GROUPS", loadGroupListByUserAccount(userAccountId));
                            result.put("ISACTIVEDIRECTORYAUTHORIZATION", true);
                        }
                    }
                }
            }
        } catch (ServiceException e) {
            logger.error("Authorization across active directory by principal name %s failed", e);
            String message = e.getMessage();
            result.put(UPPERCASE_ERROR, message);
        }
        return result;
    }

    /**
     * Метод загрузки списка ролей авторизованного пользователя.
     * Требуется для пользователей, которые авторизуеются не
     * через стандартные методы b2b. Например через active directory
     *
     * @param userAccountId идентификатор аккаунта пользователя b2b системы, для которого нужно загрузить роли
     * @return список ролей пользователя
     */
    private List<Map<String, Object>> loadRoleListByUserAccount(Long userAccountId) {
        Map<String, Object> roleParams = new HashMap<>();
        roleParams.put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
        Map<String, Object> roleRes = this.soapServiceCaller.callExternalService(B2BPOSWS, "dsUserRoleBrowseListByParam", roleParams);
        return getListFromResultMap(roleRes);
    }

    private List<Map<String, Object>> loadGroupListByUserAccount(Long userAccountId) {
        Map<String, Object> roleParams = new HashMap<>();
        roleParams.put(USER_ACCOUNT_ID_PARAM_NAME, userAccountId);
        Map<String, Object> roleRes = this.soapServiceCaller.callExternalService(ADMINWS, "admAccountGroupList", roleParams);
        return getListFromResultMap(roleRes);
    }

    /**
     * Метод загрузки информации о пользователе по идентификатору аккаунта
     *
     * @param authUserInfo информации об пользователя для авторизации
     * @param log          для конкатенации ошибок и вывод их всех в лог аудита
     * @param subResult    "словарь" в который требуется положить параметры для возвращения на интерфейс
     * @return возможную ошибку загрузки информации о подьзователя
     */
    private String loadUserInfoByAccount(AuthUserInfo authUserInfo, StringBuilder log, Map<String, Object> authorizationInfo,
                                         Map<String, Object> subResult) {
        Map<String, Object> userInfoParam = new HashMap<>();
        userInfoParam.put(RETURN_AS_HASH_MAP, true);
        userInfoParam.put(USER_ACCOUNT_ID_PARAM_NAME, authUserInfo.getUserAccountId());
        Map<String, Object> userInfoResult = this.soapServiceCaller.callExternalService(ADMINWS, "admCurrentUserInfo", userInfoParam);
        String errorText = "";
        if (userInfoResult != null && "OK".equalsIgnoreCase(getStringParam(userInfoResult, STATUS))) {
            subResult.put("AUTHMETHOD", userInfoResult.get("AUTHMETHOD"));
            subResult.put("FIRSTNAME", userInfoResult.get("FIRSTNAME"));
            subResult.put("MIDDLENAME", userInfoResult.get("MIDDLENAME"));
            subResult.put("LASTNAME", userInfoResult.get("LASTNAME"));
            subResult.put(USER_ACCOUNT_ID_PARAM_NAME, authorizationInfo.get(USER_ACCOUNT_ID_PARAM_NAME));
            String userLogin = getStringParam(userInfoResult, "USERLOGIN");
            subResult.put(UPPERCASE_LOGIN_PARAM_NAME, userLogin);
            authUserInfo.setB2bLogin(userLogin);
            authUserInfo.setHash(getStringParam(userInfoResult, "HASH"));
            authUserInfo.setDepartmentId(getLongParamWithDefaultValue(userInfoResult, "USERDEPTID", 0L));
            authUserInfo.setUserTypeId(getLongParam(userInfoResult, "OBJECTTYPE"));
            errorText = analysisUserInfoAndGenerateSession(authUserInfo, log, authorizationInfo, subResult);
            // добавляем в исходные данные параметр ограничиваюший число активных сессий пользователя
            if (userInfoResult.get(USER_SESSION_COUNT_PARAMNAME) != null) {
                authorizationInfo.put(USER_SESSION_COUNT_PARAMNAME, userInfoResult.get(USER_SESSION_COUNT_PARAMNAME));
            }
        } else {
            errorText = "Невозможно получить информации о пользователе";
        }
        return errorText;
    }

    /**
     * Анализ загруженной информации о пользователя. Например:
     * проверка истечении срока действия пароля,
     * проверка входа по проекту
     * проверка соответсия пароля требованиям безопасности т.д
     *
     * @param authUserInfo      информации об пользователя для авторизации
     * @param log               для конкатенации ошибок и вывод их всех в лог аудита
     * @param authorizationInfo авторизационная информация о пользователе
     * @param subResult         "словарь" в который требуется положить параметры для возвращения на интерфейс
     * @return ошибку, если пользователь не соответсвует требованиям
     */
    private String analysisUserInfoAndGenerateSession(AuthUserInfo authUserInfo, StringBuilder log, Map<String, Object> authorizationInfo,
                                                      Map<String, Object> subResult) {
        String errorText = "";
        Set<String> groupsSet = StaticDataHelper.getUserGroups(authorizationInfo);
        String userGroupsList = String.join(",", groupsSet);
        List<Map<String, Object>> roleList = getListParamName(authorizationInfo, ROLE_LIST_PARAM_NAME);
        // если настройка isNeedCheckProject в конфиге равна false, тогда и не надо проверять роли
        // иначе требуется проверить доступен ли проект пользователю
        String login = authUserInfo.getB2bLogin();
        errorText = checkAuthorization(authUserInfo);
        if (errorText.isEmpty()) {
            boolean necessaryProject = !isNeedCheckProject || checkProject(roleList, authUserInfo.getProjectName());
            if (necessaryProject) {
                String hash = authUserInfo.getHash();
                String password = authUserInfo.getPassword();
                Long userAccountId = authUserInfo.getUserAccountId();
                Long userTypeId = authUserInfo.getUserTypeId();
                Long departmentId = authUserInfo.getDepartmentId();
                // если мы дошли до сюда, но флга "требуется сменить пароль" взведен, значит дата действия пароля истекла
                if (authUserInfo.isNeedChangePwd()) {
                    errorText = "Истекла дата действия пароля";
                    log.append(errorText).append(",");
                    subResult.put(CHANGE_PASS_FLAG_NAME, authUserInfo.isNeedChangePwd());
                    subResult.put(SESSION_ID_PARAM_NAME, makeTimeoutSessionId(login, hash, userAccountId, userTypeId, departmentId, userGroupsList));
                } else {
                    boolean isNeedVerifierPassword = isNeedCheckCorrectPassword && !getBooleanParam(authorizationInfo, "ISACTIVEDIRECTORYAUTHORIZATION", false);
                    Result verifierResult = createPasswordVerifier().isPasswordValid(password);
                    if (isNeedVerifierPassword && !verifierResult.equals(Result.OK)) {
                        errorText = "Пароль не соответствует правилам безопасности";
                        log.append(errorText).append(",");
                        subResult.put(CHANGE_PASS_FLAG_NAME, true);
                        subResult.put(SESSION_ID_PARAM_NAME, makeTimeoutSessionId(login, hash, userAccountId, userTypeId, departmentId, userGroupsList));
                    } else {
                        subResult.put(SESSION_ID_PARAM_NAME, makeSessionId(login, hash, userAccountId, userTypeId, departmentId, null, userGroupsList));
                        subResult.put(ROLE_LIST_PARAM_NAME, roleList);
                        log.append("успешно.");
                    }
                }
            } else {
                errorText = "Данный проект недоступен для пользователя: " + login;
                log.append(errorText);
            }
        }
        return errorText;
    }

    /**
     * Метод проверки авторизации пользователя
     *
     * @param authUserInfo данные об авторизации пользователя
     * @return строка ошибки, если что-то не так
     */
    protected abstract String checkAuthorization(AuthUserInfo authUserInfo);

    /**
     * Проверяем доступность пользователю проекта.
     * Если наименование проекта пусто и у пользователя отсутсвуют роли с системным именем "kmsbOneProject",
     * тогда пускаем пользователя, т.к. он кмсб'шный и заходит в правильное место.
     * Если же имя проекта, пришедшее в хедерсах "kmsb1Project" и роль с системным именем "kmsbOneProject"
     * у пользователя присутсвует, тогда пускаем пользователя.
     * Во всех остальных случаях не пускаем
     *
     * @param roleList    список ролей
     * @param projectName название проекта, которое должно придти в хедерсах от nginx
     * @return доступен ли проект пользователю или нет
     */
    private boolean checkProject(List<Map<String, Object>> roleList, String projectName) {
        boolean result = false;
        boolean isExistKmsbOneRole = false;
        if (roleList != null) {
            isExistKmsbOneRole = roleList.parallelStream()
                    .anyMatch(item -> getStringParam(item, "ROLESYSNAME").equals("kmsbOneProject"));
        }
        if (projectName.isEmpty() && !isExistKmsbOneRole) {
            result = true;
        } else {
            if (projectName.equals("kmsb1Project") && isExistKmsbOneRole) {
                result = true;
            }
        }
        return result;
    }

    // дополнительная обработка ответа со статусом 'EMPTYLOGINPASS' (генерация описания ошибки, сохранение в БД)
    private void processResultStatusEmptyLoginPass(JsonResult jsonResult, String methodName, String serviceName,
                                                   String missingParamName, Map<String, Object> callParams) {

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
        fakeCallResult.put(RESULT, fakeCallSubResult);
        String login = soapServiceCaller.getLogin();
        String password = soapServiceCaller.getPassword();

        // вызов универсального анализа результата с сохранением ошибки в БД
        this.analyseResultAndSaveErrorInLog(fakeCallResult, serviceName, methodName, callParams, login, password);

        // cформированный результат повторно обновляем описанием ошибки (в analyseResultAndSaveErrorInLog после
        // сохранения ошибки в БД результат очищается но не дополняется значимыми данными)
        fakeCallResult.put(ERROR, fakeCallSubResult);

        // подготовка ответа для возврата из гейта (не будет виден пользователю, но будет доступен в протоколе и в отладке js)
        requestWorker.serializeJSON(fakeCallResult, jsonResult);
        jsonResult.setResultStatus(JsonResult.RESULT_empty_LoginPass);

    }

    // анализ результата вызова метода и если имеются ошибки/исключения - сохранение в БД
    private void analyseResultAndSaveErrorInLog(Map<String, Object> result, String moduleName, String methodName,
                                                Map<String, Object> params, String login, String password) {
        // если метод упал - то проверяем и если что сохраняем callstack
        String sessionId = "";
        String contrId = "";
        if (params != null) {
            if (params.get(SESSION_ID_PARAM_NAME) != null) {
                //4b2b
                sessionId = getStringParam(params, SESSION_ID_PARAM_NAME);
            } else if (params.get(SESSION_TOKEN_PARAM_NAME) != null) {
                sessionId = getStringParam(params, SESSION_TOKEN_PARAM_NAME);
            } else {
                //4online
                // различные варианты входных данных. мапа договора на сохранение
                if (params.get("CONTRMAP") != null) {
                    Map<String, Object> contrMap = getMapParamName(params, "CONTRMAP");
                    if (contrMap.get(SESSION_TOKEN_PARAM_NAME) != null) {
                        sessionId = getStringParam(contrMap, SESSION_TOKEN_PARAM_NAME);
                    }
                } else if (params.get("DATAMAP") != null) {
                    Map<String, Object> dataMap = getMapParamName(params, "DATAMAP");
                    if (dataMap.get("contrId") != null) {
                        contrId = dataMap.get("contrId").toString();
                    }
                }
            }
        }
        this.saveErrorInLog(result, sessionId, contrId, moduleName, methodName, params, login, password);
    }

    private void saveErrorInLog(Map<String, Object> methodCallResult, String sessionId, String contrId, String serviceName,
                                String methodName, Map<String, Object> params, String login, String password) {
        boolean isError = false;
        if ((methodCallResult.get(STATUS)) != null && (UPPERCASE_ERROR.equals(methodCallResult.get(STATUS).toString()))) {
            isError = true;
        }
        if (methodCallResult.get(ERROR) instanceof Map) {
            Map<String, Object> res = getMapParamName(methodCallResult, ERROR);
            if ((res.get(STATUS) != null) && (UPPERCASE_ERROR.equals(res.get(STATUS).toString()))) {
                isError = true;
            }
        }
        if (isError) {
            JsonResult result = new JsonResult();
            requestWorker.serializeJSON(methodCallResult, result);
            String rawres = result.getResultJson();

            Map<String, Object> logParam = new HashMap<>();
            Map<String, Object> objMap = new HashMap<>();
            Map<String, Object> obj = new HashMap<>();

            obj.put("ACTION", UPPERCASE_ERROR);
            obj.put("NOTE", "Сохранение текста ошибки");
            if (methodCallResult.get(SESSION_ID_PARAM_NAME) != null) {
                obj.put("SESSIONTOKEN", methodCallResult.get(SESSION_ID_PARAM_NAME));
            } else {
                obj.put("SESSIONTOKEN", sessionId);
            }
            obj.put("PARAM1", serviceName);
            obj.put("PARAM2", methodName);
            obj.put("PARAM4", login);
            obj.put("PARAM5", password);
            obj.put("CONTRID", contrId);
            JsonResult resParamLog = new JsonResult();
            if (params != null) {
                requestWorker.serializeJSON(params, resParamLog);
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
                res = soapServiceCaller.callExternalService(BIVSBERPOSWS, "dsAngularUserActionLogCreate", logParam);
            }

            JsonResult resLog = new JsonResult();
            requestWorker.serializeJSON(res, resLog);
            result.setResultJson(resLog.getResultJson());
            result.setResultStatus(JsonResult.RESULT_ERROR);

            // протоколирование ошибки в стандартном протоколе
            logger.error(String.format(
                    "Error call service [%s:%s] on behalf of [%s] with params [%s]! Details (call result): %s.",
                    serviceName, methodName, login, params == null ? "null" : params.toString(), methodCallResult
            ));

            // затираем данные выдающиеся наружу.
            methodCallResult.clear();
            methodCallResult.putAll(res);
            methodCallResult.put(STATUS, UPPERCASE_ERROR);
        }

    }

    private String makeSessionId(String login, String passwordSHA, Long userAccountId, Long userTypeId, Long
            departmentId, String time, String groupsList) {
        SessionController controller = DefaultServiceLoader.loadServiceAny(SessionController.class);
        Map<String, Object> sessionParams = new HashMap<>();
        sessionParams.put(SYSTEM_PARAM_NAME_LOGIN, login);
        sessionParams.put(SYSTEM_PARAM_NAME_PASS, passwordSHA);
        sessionParams.put(B2B_USERACCOUNTID_PARAMNAME, userAccountId != null ? userAccountId.toString() : "");
        sessionParams.put(B2B_USERTYPEID_PARAMNAME, userTypeId != null ? userTypeId.toString() : "");
        sessionParams.put(B2B_DEPARTMENTID_PARAMNAME, departmentId != null ? departmentId.toString() : "");
        sessionParams.put(SESSION_TIME_PARAMNAME, time == null ? BaseSessionController.EMPTY_TIME : time);
        sessionParams.put(B2B_USERGROUPS_PARAMNAME, groupsList);
        return controller.createSession(sessionParams);
    }

    private String makeTimeoutSessionId(String login, String passwordSHA, Long userAccountId, Long userTypeId, Long
            departmentId, String groupsList) {
        return makeSessionId(login, passwordSHA, userAccountId, userTypeId, departmentId, "1", groupsList);
    }

    /**
     * Метод разлогирования пользователя
     *
     * @param paramStr
     * @param headers
     * @return
     * @author Ilya Belugin
     */
    protected Response dsB2BLogoutBase(String paramStr, HttpHeaders headers, Audit audit) {
        //for audit
        String auditLogin = "";
        Long userAccountId = null;
        StringBuilder auditMessageBuilder = new StringBuilder();
        String auditOperation = "logout";

        String ipChain = headers.getHeaderString(IP_CHAIN_HEADER_NAME);
        AuditIpInfo ipInfo = new AuditIpInfo().setIpChainAddresses(ipChain == null || ipChain.isEmpty() ? DEFAULT_IP_CHAIN : ipChain);
        //

        Obj paramsObj = this.requestWorker.deserializeJSON(paramStr, Obj.class);
        JsonResult jsonResult = new JsonResult();
        if (paramsObj == null) {
            jsonResult.setResultStatus(JsonResult.RESULT_ERROR);
            Map<String, Object> result = new HashMap<>();
            String message = "Не хватает входных параметров";
            result.put("Error", message);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("Result", result);
            this.requestWorker.serializeJSON(resultMap, jsonResult);

            auditMessageBuilder.append(message);
            auditLogOut(audit, auditOperation, ResultOperation.FALTURE, auditLogin, userAccountId, ipInfo, auditMessageBuilder.toString());

            return Response.status(Response.Status.OK).entity(jsonResult).build();
        }

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> objMap = paramsObj.copyObjFromEntityToMap();
        Map<String, Object> requestParams = getMapParamName(objMap, "obj");
        String error = "";
        String sessionHash = "";
        String login = "";
        SessionController controller = DefaultServiceLoader.loadServiceAny(SessionController.class);
        String sessionId = getStringParam(requestParams, SESSION_ID_LOWERCASE_PARAM_NAME);
        Map<String, Object> callParams;
        callParams = controller.checkSession(sessionId);
        boolean doBroadcast = false;
        if (callParams.get(ERROR) != null) {
            result.put(RESULT, RET_STATUS_OK);
            this.requestWorker.serializeJSON(result, jsonResult);

            auditLogOut(audit, auditOperation, ResultOperation.FALTURE, auditLogin, userAccountId, ipInfo, auditMessageBuilder.toString());

            return Response.status(Response.Status.OK).entity(jsonResult).build();
        } else {
            sessionHash = callParams.get(SESSION_HASH_PARAMNAME).toString();
            login = getStringParam(callParams, SYSTEM_PARAM_NAME_LOGIN);
            auditLogin = login;
            userAccountId = getLongParam(callParams, USER_ACCOUNT_ID_PARAM_NAME);
        }

        boolean sessionInLoggedOut = sc.sessionIsKilled(sessionHash);
        if (sessionInLoggedOut) {

            String message = "Сессия более недействительна.";
            auditMessageBuilder.append(message);
            auditLogOut(audit, auditOperation, ResultOperation.SUCCESE, auditLogin, userAccountId, ipInfo, auditMessageBuilder.toString());

            result.put(RESULT, message);
            logger.debug(sessionHash + " is logged out");
            this.requestWorker.serializeJSON(result, jsonResult);
            sc.killSession(login, sessionHash);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        }
        // и удаляем сессию из списка активных
        sc.killSession(login, sessionHash);

        // проверка источника вызова - гейт или браузер
        doBroadcast = requestParams.get("doBroadcast") == null;
        if (!doBroadcast) {
            logger.debug("[BROADCAST LOG]: Incoming request without broadcast directive found.");

            auditMessageBuilder.append("Пользовательский сеанс завершен");
            auditLogOut(audit, auditOperation, ResultOperation.SUCCESE, auditLogin, userAccountId, ipInfo, auditMessageBuilder.toString());

            result.put(RESULT, RET_STATUS_OK);
            this.requestWorker.serializeJSON(result, jsonResult);
            return Response.status(Response.Status.OK).entity(jsonResult).build();
        } else {
            // для отладки, можно удалить
            logger.debug("[BROADCAST LOG]: Incoming request with broadcast directive found.");
        }

        List<BroadcastTarget> broadcastServers = sc.downloadServers().getServers();

        if (error.isEmpty() && doBroadcast && broadcastServers != null && !broadcastServers.isEmpty()) {
            // делаем клон запроса, важна только действующая сессия
            Client client;
            Response response = null;
            // воспроизводим запрос
            for (BroadcastTarget server : broadcastServers) {
                String postURI = "http://" + server.getIp() + ":" + server.getPort();
                postURI = "http://localhost:8080";
                postURI = postURI + "/admrestws/rest/login/dsB2BLogOut";
                requestParams.put("doBroadcast", false);
                this.requestWorker.serializeJSON(objMap, jsonResult);
                try {
                    client = new ResteasyClientBuilder()
                            .establishConnectionTimeout(1, TimeUnit.SECONDS)
                            .socketTimeout(1, TimeUnit.SECONDS)
                            .build();
                    WebTarget target = client.target(postURI);
                    Form form = new Form();
                    form.param("params", jsonResult.getResultJson());
                    Entity<Form> data = Entity.form(form);
                    response = target.request(MediaType.APPLICATION_FORM_URLENCODED)
                            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            .header("Accept", MediaType.APPLICATION_JSON)
                            .post(data);
                    String resString = response.readEntity(String.class);
                } catch (Exception ex) {
                    logger.error("dsB2BLogOut", ex);
                } finally {
                    if (response != null) {
                        response.close();  // Обязательно закрыть
                    }
                }
            }
        }

        auditMessageBuilder.append("Пользовательский сеанс завершен");
        auditLogOut(audit, auditOperation, ResultOperation.SUCCESE, auditLogin, userAccountId, ipInfo, auditMessageBuilder.toString());

        result.put(RESULT, RET_STATUS_OK);
        this.requestWorker.serializeJSON(result, jsonResult);
        return Response.status(Response.Status.OK).entity(jsonResult).build();
    }

    protected Map<String, Long> getActiveSessionsOfUserBase(String userName) {
        return sc.clearDeadSessions(userName).getActiveSessions(userName);
    }

    public SessionCounter updateSessionData(Map<String, Object> data, Long timeout) {
        return sc.updateSessionData(data, timeout);
    }

    private Long getSessionTimeOut() {
        Map<String, Object> params = new HashMap<>();
        params.put("SETTINGSYSNAME", "SESSION_SIZE");
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        Long sessionSize = getLongParam(this.soapServiceCaller.callExternalService(COREWS, "getSysSettingBySysName", params), "SETTINGVALUE");
        if (sessionSize == null) {
            return getLongParam(config.getParam("maxSessionSize", "10"));
        } else {
            return sessionSize;
        }
    }

    private void auditLogOut(final Audit audit, final String auditOperation, final ResultOperation resultOperation, final String auditLogin, final Long userAccountId, final AuditIpInfo ipInfo, final String auditMessageBuilder) {
        audit.audit(auditOperation, resultOperation, auditLogin, userAccountId, ipInfo, auditMessageBuilder);
    }
}
