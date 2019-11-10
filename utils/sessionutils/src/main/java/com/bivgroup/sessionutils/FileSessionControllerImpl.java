package com.bivgroup.sessionutils;

import java.util.HashMap;
import java.util.Map;

public class FileSessionControllerImpl extends BaseSessionController implements SessionController {

    public static final String THIS_SESSION_TYPE = "FileSession";
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

    private static final String ENCRYPTION_PASSWORD = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    private static final byte[] ENCRYPTION_SALT = {
            (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
            (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};

    private static final String SESSION_DIVIDER = "__div__";

    private static int IP_ADDRESS_PARAM_ID = 2;

    FileSessionControllerImpl(Long sessionTimeOut) {
        super(THIS_SESSION_TYPE, SESSION_DIVIDER, ENCRYPTION_PASSWORD, ENCRYPTION_SALT);
        this.timeOut = sessionTimeOut;
        init();
    }

    public FileSessionControllerImpl() {
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

    @Override
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
