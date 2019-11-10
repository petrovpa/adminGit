package com.bivgroup.services.b2bposws.facade;

import com.bivgroup.sessionutils.BaseSessionController;
import com.bivgroup.sessionutils.SessionController;
import com.bivgroup.sessionutils.SessionUtilException;
import org.apache.commons.codec.digest.DigestUtils;
import ru.diasoft.services.config.Config;

import java.util.HashMap;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

public class B2BPosServiceSessionController extends BaseSessionController implements SessionController {

    private static final String THIS_SESSION_TYPE = "B2BBaseSession";
    public static final long DEFAULT_TIMEOUT_MINUTEN = 10L;

    public static String SESSION_TYPE_PARAMNAME = "sessionType";
    public static String SESSION_TIME_PARAMNAME = "timeInMiliseconds";
    // сессия Б2Б
    public static String B2B_USERACCOUNTID_PARAMNAME = "SESSION_USERACCOUNTID";
    public static String B2B_USERTYPEID_PARAMNAME = "SESSION_USERTYPEID";
    public static String B2B_DEPARTMENTID_PARAMNAME = "SESSION_DEPARTMENTID";
    public static String B2B_USERLOGIN_PARAMNAME = LOGIN;
    public static String B2B_USERPASSWORD_PARAMNAME = PASSWORD;
    public static String B2B_USERGROUPS_PARAMNAME = "SESSION_USERGROUPS";

    // IP
    private String DEFAULT_IP = "127.0.0.1";
    private String SESSION_IP_ADDRESES_PARAMNAME = "IP";
    private String IP_ADDRESES_MAP_PARAMNAME = "IPMAP";
    private String IP_ADDRESES_ROUTE_PARAMNAME = "route";

    private String ERROR_SESSION_TIMEOUT = "Время сессии истекло!";
    public static String SESSION_ID_PARAMNAME = "SESSIONID";

    private static String ERROR = SessionController.ERROR_PARAMNAME;

    private static final String SESSION_DIVIDER = "__div__";

    public B2BPosServiceSessionController(Long sessionTimeOut) {
        super(THIS_SESSION_TYPE, SESSION_DIVIDER, sessionTimeOut * 60 * 1000);
        this.timeOut = sessionTimeOut;
        init();
    }

    public B2BPosServiceSessionController() {
        super(THIS_SESSION_TYPE, SESSION_DIVIDER);
        Config conf = Config.getConfig("bivsberlossws");
        Long timeOutFromConfig = Long.parseLong(conf.getParam("maxSessionSize", String.valueOf(DEFAULT_TIMEOUT_MINUTEN)));
        this.timeOut = timeOutFromConfig;
        init();
    }

    private void init() {
    }

    public String createSession(Map<String, Object> sessionParams) {
        String[] params = new String[10];
        // устанавливаем сессию
        // Проверки
        String userAccount = (String) sessionParams.get(B2B_USERACCOUNTID_PARAMNAME);
        String userType = (String) sessionParams.get(B2B_USERTYPEID_PARAMNAME);
        String department = (String) sessionParams.get(B2B_DEPARTMENTID_PARAMNAME);
        String userLogin = (String) sessionParams.get(B2B_USERLOGIN_PARAMNAME);
        String userPass = (String) sessionParams.get(B2B_USERPASSWORD_PARAMNAME);
        String groups = (String) sessionParams.getOrDefault(B2B_USERGROUPS_PARAMNAME, "-");
        if (groups.isEmpty()) groups = "-";
        if (!paramsAreValid(userLogin, userPass, userAccount, userType, department)) {
            return null;
        } else {
            try {
                params[0] = ""; // параметр задается в compileSessionId
                params[1] = String.valueOf(sessionParams.getOrDefault(SESSION_TIME_PARAMNAME, EMPTY_TIME));
                //параметры профиля
                params[2] = (String) sessionParams.getOrDefault(SESSION_IP_ADDRESES_PARAMNAME, DEFAULT_IP);
                params[3] = userAccount;
                params[4] = userType;
                params[5] = department;
                params[6] = userLogin;
                params[7] = userPass;
                params[8] = groups;
                params[9] = userLogin + "_" + DigestUtils.shaHex(getNowTimeInMillisStr());
                return compileSessionId(params);
            } catch (SessionUtilException ex) {
                return null;
            }
        }
    }

    public Map<String, Object> check(String sessionId, boolean doCreateNewSession) {
        Map<String, Object> result = new HashMap<>();
        try {
            String[] params = decompileSessionId(sessionId);
            result.put(SESSION_TYPE_PARAMNAME, params[0]);
            result.put(SESSION_TIME_PARAMNAME, params[1]);
            result.put(SESSION_IP_ADDRESES_PARAMNAME, params[2]);
            // параметры профиля
            result.put(B2B_USERACCOUNTID_PARAMNAME, params[3]);
            result.put(B2B_USERTYPEID_PARAMNAME, params[4]);
            result.put(B2B_DEPARTMENTID_PARAMNAME, params[5]);
            result.put(B2B_USERLOGIN_PARAMNAME, params[6]);
            result.put(B2B_USERPASSWORD_PARAMNAME, params[7]);
            result.put(B2B_USERGROUPS_PARAMNAME, params[8]);
            result.put(SESSION_HASH_PARAMNAME, params[9]);
            if (doCreateNewSession) {
                String newSessionId = compileSessionId(params);
                result.put(SESSION_ID_PARAMNAME, newSessionId);
            } else {
                result.put(SESSION_ID_PARAMNAME, sessionId);
            }
            // вывод маршрута запроса
            Map<String, Object> ipMap = new HashMap<>();
            String ipAdresses = params[2];
            ipMap.put(IP_ADDRESES_ROUTE_PARAMNAME, ipAdresses);
            result.put(IP_ADDRESES_MAP_PARAMNAME, ipMap);
            result.put(SESSION_IP_ADDRESES_PARAMNAME, ipAdresses);
            return result;
        } catch (SessionUtilException | ArrayIndexOutOfBoundsException e) {
            return error(ERROR_TIMEOUT_SYSNAME, ERROR_TIMEOUT_TEXT);
        }
    }

    public String regenerateSession(String sessionId) {
        try {
            String[] params = decompileSessionId(sessionId);
            return compileSessionId(params);
        } catch (SessionUtilException ex) {
            return sessionId;
        }
    }

    public void setTimeout(Long timeout) {
        this.timeOut = timeout;
    }

    public Map<String, Object> checkSession(String sessionId) {
        return check(sessionId, false);
    }

    public Map<String, Object> checkAndCreateSession(String sessionId) {
        return check(sessionId, true);
    }
}
