package com.bivgroup.services.b2bposws.facade.admin.lk;

import com.bivgroup.sessionutils.BaseSessionController;
import com.bivgroup.sessionutils.SessionController;
import com.bivgroup.sessionutils.SessionUtilException;
import org.apache.commons.codec.digest.DigestUtils;
import ru.diasoft.services.inscore.util.StringCryptUtils;

import java.util.HashMap;
import java.util.Map;

public class B2B2LKSessionController extends BaseSessionController implements SessionController {

    public static final String THIS_SESSION_TYPE = "PA2baseSession";
    public static final long DEFAULT_TIMEOUT = 0L;

    public static String PA2_BASE_USERLOGIN_PARAMNAME = "login";
    public static String PA2_BASE_USERPASS_PARAMNAME = "password";
    public static String PA2_BASE_PROFILE_ID_PARAMNAME = "clientProfileId";
    // общие параметры
    public static String SESSION_TIME_PARAMNAME = "timeInMiliseconds";
    public static String SESSION_TYPE_PARAMNAME = "sessionType";
    public static String SESSION_USERROLE_PARAMNAME = "userRole";
    public static String SESSION_IP_ADDRESES_PARAMNAME = "IP";
    // роли
    private String DEFAULT_ROLE = "emptyRole";
    // IP
    private String DEFAULT_IP = "127.0.0.1";

    private String IP_ADDRESES_MAP_PARAMNAME = "IPMAP";
    private String IP_ADDRESES_ROUTE_PARAMNAME = "route";

    private String ERROR_SESSION_TIMEOUT = "Время сессии истекло!";
    private String SESSION_ID_PARAMNAME = "sessionID";

    public static String ERROR = SessionController.ERROR_PARAMNAME;

    private static int IP_ADDRESS_PARAM_ID = 2;

    private static final String SESSION_DIVIDER = "__div__";

    public B2B2LKSessionController(Long sessionTimeOut) {
        super(THIS_SESSION_TYPE, SESSION_DIVIDER, sessionTimeOut*60*1000);
        //super(THIS_SESSION_TYPE, SESSION_DIVIDER, new StringCryptUtils(ENCRYPTION_PASSWORD, ENCRYPTION_SALT));
        this.timeOut = sessionTimeOut;
        init();
    }

    public B2B2LKSessionController() {
        super(THIS_SESSION_TYPE, SESSION_DIVIDER, DEFAULT_TIMEOUT);
        this.timeOut = DEFAULT_TIMEOUT;
        init();
    }

    private void init() {
    }

    @Override
    public String createSession(Map<String, Object> sessionParams) {
        String[] params = new String[8];
        // устанавливаем сессию
        // Проверки
        String profile = sessionParams.get(PA2_BASE_PROFILE_ID_PARAMNAME).toString();
        String userLogin = (String) sessionParams.get(PA2_BASE_USERLOGIN_PARAMNAME);
        String userPassword = (String) sessionParams.get(PA2_BASE_USERPASS_PARAMNAME);
        if (!paramsAreValid(profile, userLogin, userPassword)) {
            return null;
        } else {
            try {
                params[0] = ""; // параметр задается в compileSessionId
                params[1] = String.valueOf(sessionParams.getOrDefault(SESSION_TIME_PARAMNAME, EMPTY_TIME));
                params[2] = (String) sessionParams.getOrDefault(SESSION_IP_ADDRESES_PARAMNAME, DEFAULT_IP);
                params[3] = (String) sessionParams.getOrDefault(SESSION_USERROLE_PARAMNAME, DEFAULT_ROLE);
                //параметры профиля
                params[4] = profile;
                params[5] = userLogin;
                params[6] = userPassword;
                params[7] = userLogin + "_" + DigestUtils.shaHex(getNowTimeInMillisStr());
                return compileSessionId(params);
            } catch (SessionUtilException ex) {
                return null;
            }
        }
    }

    @Override
    public Map<String, Object> check(String sessionId,  boolean doCreateNewSession) {
        Map<String, Object> result = new HashMap<>();
        try {
            String[] params = decompileSessionId(sessionId);
            Long time = Long.parseLong(params[SESSION_PARAM_TIME_ID]);
            result.put(SESSION_TYPE_PARAMNAME, params[0]);
            result.put(SESSION_TIME_PARAMNAME, params[1]);
            result.put(SESSION_IP_ADDRESES_PARAMNAME, params[2]);
            result.put(SESSION_USERROLE_PARAMNAME, params[3]);
            // параметры профиля
            result.put(PA2_BASE_PROFILE_ID_PARAMNAME, params[4]);
            result.put(PA2_BASE_USERLOGIN_PARAMNAME, params[5]);
            result.put(PA2_BASE_USERPASS_PARAMNAME, params[6]);
            result.put(SESSION_HASH_PARAMNAME, params[7]);
            if (doCreateNewSession) {
                String newSessionId = compileSessionId(params);
                result.put(SESSION_ID_PARAMNAME, newSessionId);
            } else {
                result.put(SESSION_ID_PARAMNAME, sessionId);
            }
            // вывод маршрута запроса
            Map<String, Object> ipMap = new HashMap<>();
            String ipAdresses = params[IP_ADDRESS_PARAM_ID];
            ipMap.put(IP_ADDRESES_ROUTE_PARAMNAME, ipAdresses);
            result.put(IP_ADDRESES_MAP_PARAMNAME, ipMap);
            result.put(SESSION_IP_ADDRESES_PARAMNAME, ipAdresses);
            return result;
        } catch (SessionUtilException ex) {
            result.put(ERROR, "Не верный ИД сессии");
            return result;
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
