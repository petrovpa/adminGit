package com.bivgroup.services.b2bposws.facade;

import com.bivgroup.sessionutils.BaseSessionController;
import com.bivgroup.sessionutils.SessionController;
import com.bivgroup.sessionutils.SessionUtilException;

import java.util.HashMap;
import java.util.Map;

public class B2BFileSessionController extends BaseSessionController implements SessionController {

    public static final String THIS_SESSION_TYPE = "B2BFileSession";
    public static final long DEFAULT_TIMEOUT = 0L;

    public static String FS_TYPE_PARAMNAME = "fsType";
    public static String SOME_ID_PARAMNAME = "someId";
    public static String USER_DOCNAME_PARAMNAME = "userDocName";
    public static String UUID_PARAMNAME = "UUID";
    public static String SESSION_TIME_PARAMNAME = "timeInMiliseconds";
    public static String SESSION_TYPE_PARAMNAME = "sessionType";

    // роли
    private String DEFAULT_ROLE = "emptyRole";
    private String SESSION_USERROLE_PARAMNAME = "userRole";
    // IP
    private String DEFAULT_IP = "127.0.0.1";
    public static String SESSION_IP_ADDRESES_PARAMNAME = "IP";
    private String IP_ADDRESES_MAP_PARAMNAME = "IPMAP";
    private String IP_ADDRESES_ROUTE_PARAMNAME = "route";

    private String ERROR_SESSION_TIMEOUT = "Время сессии истекло!";
    public static String SESSION_ID_PARAMNAME = "sessionID";

    public static String ERROR = SessionController.ERROR_PARAMNAME;

    private static final String SESSION_DIVIDER = "__div__";

    private static int IP_ADDRESS_PARAM_ID = 2;

    B2BFileSessionController(Long sessionTimeOut) {
        super(THIS_SESSION_TYPE, SESSION_DIVIDER, sessionTimeOut*60*1000);
        //super(THIS_SESSION_TYPE, SESSION_DIVIDER, new StringCryptUtils(ENCRYPTION_PASSWORD, ENCRYPTION_SALT));
        this.timeOut = sessionTimeOut;
        init();
    }

    public B2BFileSessionController() {
        super(THIS_SESSION_TYPE, SESSION_DIVIDER, DEFAULT_TIMEOUT);
        this.timeOut = DEFAULT_TIMEOUT;
        init();
    }

    private void init() {
    }

    public String createSession(Map<String, Object> sessionParams) {
        String[] params = new String[8];
        // устанавливаем сессию
        // Проверки
        String fsType = (String) sessionParams.get(FS_TYPE_PARAMNAME);
        String someId = (String) sessionParams.get(SOME_ID_PARAMNAME);
        String userDocname = (String) sessionParams.get(USER_DOCNAME_PARAMNAME);
        String UUID = java.util.UUID.randomUUID().toString();
        if (!paramsAreValid(fsType, someId, userDocname)) {
            return null;
        } else {
            try {
                params[0] = ""; // параметр задается в compileSessionId
                params[1] = String.valueOf(sessionParams.getOrDefault(SESSION_TIME_PARAMNAME, EMPTY_TIME));
                params[2] = (String) sessionParams.getOrDefault(SESSION_IP_ADDRESES_PARAMNAME, DEFAULT_IP);
                params[3] = (String) sessionParams.getOrDefault(SESSION_USERROLE_PARAMNAME, DEFAULT_ROLE);
                //параметры файла
                params[4] = fsType;
                params[5] = someId;
                params[6] = userDocname;
                params[7] = UUID;
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
            result.put(SESSION_USERROLE_PARAMNAME, params[3]);
            // параметры файла
            result.put(FS_TYPE_PARAMNAME, params[4]);
            result.put(SOME_ID_PARAMNAME, params[5]);
            result.put(USER_DOCNAME_PARAMNAME, params[6]);
            result.put(UUID_PARAMNAME, params[7]);
            if (doCreateNewSession) {
                String newSessionId = compileSessionId(params);
                result.put(SESSION_ID_PARAMNAME, newSessionId);
            } else {
                result.put(SESSION_ID_PARAMNAME, sessionId);
            }
            Map<String, Object> ipMap = new HashMap<>();
            String ipAdresses = params[IP_ADDRESS_PARAM_ID];
            ipMap.put(IP_ADDRESES_ROUTE_PARAMNAME, ipAdresses);
            result.put(IP_ADDRESES_MAP_PARAMNAME, ipMap);
            result.put(SESSION_IP_ADDRESES_PARAMNAME, ipAdresses);
            return result;
        } catch (SessionUtilException ex) {
            result.put(ERROR, "Неверный ИД сессии");
            return result;
        }
    }

    public String regenerateSession(String sessionId) {
        try {
            String[] params = decompileSessionId(sessionId);
            params[SESSION_PARAM_TIME_ID] = getNowTimeInMillisStr();
            String newSession = compileSessionId(params);
            return newSession;
        } catch (SessionUtilException ex) {
            return sessionId;
        }
    }

    public static String concatenateSessionParams(String... params) {
        StringBuilder res = new StringBuilder();
        for (String s : params) {
            res.append(SESSION_DIVIDER).append(s);
        }
        return res.toString();
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
