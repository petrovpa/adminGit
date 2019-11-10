package com.bivgroup.sessionutils;

import com.bivgroup.stringutils.StringCryptUtils;

import java.util.*;

public class BaseSessionController {

    protected final String SESSION_TYPE;
    protected StringCryptUtils SCU;
    protected String SESSION_DELIMITER;
    public static final int SESSION_PARAM_TIME_ID = 1;
    private static final int SESSION_PARAM_SESSIONTYPE_ID = 0;
    public static final String EMPTY_TIME = "emptyTime";
    public static final String SESSION_HASH_PARAMNAME = "sessionHash";
    public static final String TIMED_OUT_TIMEPARAM = "-1";
    protected Long timeOut;

    public BaseSessionController(String type, String delimiter, long timeOut) {
        this(type, delimiter);
        this.SCU = new StringCryptUtils(timeOut);
        this.SCU.setDelimiter4CheckDecoded(delimiter);
    }

    public BaseSessionController(String type, String delimiter, String encryptionPassword, byte[] salt) {
        this(type, delimiter);
        this.SCU = new StringCryptUtils(encryptionPassword, salt);
        this.SCU.setDelimiter4CheckDecoded(delimiter);
    }

    public BaseSessionController(String type, String delimiter) {
        this.SESSION_TYPE = type;
        this.SESSION_DELIMITER = delimiter;
    }

    // создание новой сессии
    protected String compileSessionId(String[] input) throws SessionUtilException {
        try {
            String timeStr = input[SESSION_PARAM_TIME_ID];
            if (timeStr == null) timeStr = EMPTY_TIME;
            if (!timeStr.isEmpty()) {
                String currentTimeMillis = getNowTimeInMillisStr();
                if (timeStr.equalsIgnoreCase(TIMED_OUT_TIMEPARAM)) {
                    input[SESSION_PARAM_TIME_ID] = TIMED_OUT_TIMEPARAM;
                } else {
                    input[SESSION_PARAM_TIME_ID] = currentTimeMillis;
                }
            }
            return compileSessionWithTime(input);
        } catch (
                SecurityException ex) {
            String error = "Ошибка во время шифрования: \r\n";
            error += "\u0009 данные:" + Arrays.toString(input);
            error += "\u0009 Exception" + ex.toString();
            throw new SessionUtilException(error);
        }
    }

    // создание новой сессии и задание времени жизни
    private String compileSessionWithTime(String[] input) throws SessionUtilException {
        try {
            input[SESSION_PARAM_SESSIONTYPE_ID] = SESSION_TYPE;
            return SCU.encrypt(String.join(SESSION_DELIMITER, input));
        } catch (SecurityException ex) {
            String error = "Ошибка во время шифрования: \r\n";
            error += "\u0009 данные:" + Arrays.toString(input);
            error += "\u0009 Exception" + ex.toString();
            throw new SessionUtilException(error);
        }
    }

    protected String getNowTimeInMillisStr() {
        return String.valueOf(System.currentTimeMillis());
    }

    protected String[] decompileSessionId(String sessionId) throws SessionUtilException {
        try {
            SCU.setDelimiter4CheckDecoded(SESSION_DELIMITER);
            String decompiled = SCU.decrypt(sessionId);
            return decompiled.split(SESSION_DELIMITER);
        } catch (SecurityException ex) {
            String error = "Ошибка во время дешифровки: \r\n";
            error += "\u0009 данные:" + sessionId;
            error += "\u0009 длина строки сессии:" + sessionId.length();
            error += "\u0009 Exception" + ex.toString();
            throw new SessionUtilException(error);
        }
    }

    protected Map<String, Object> error(String s) {
        Map<String, Object> result = new HashMap<>();
        result.put(SessionController.ERROR_PARAMNAME, s);
        return result;
    }

    protected Map<String, Object> error(String sysname, String text) {
        Map<String, Object> result = new HashMap<>();
        result.put(SessionController.ERROR_PARAMNAME, text);
        result.put(SessionController.ERRORSYSANAME_PARAMNAME, sysname);
        return result;
    }

    public static Map<String, Object> getErrorMap(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        if (params.get(SessionController.ERROR_PARAMNAME) != null) {
            result.put(SessionController.ERROR_PARAMNAME, params.get(SessionController.ERROR_PARAMNAME));
        }
        if (params.get(SessionController.ERRORSYSANAME_PARAMNAME) != null) {
            result.put(SessionController.ERRORSYSANAME_PARAMNAME, params.get(SessionController.ERRORSYSANAME_PARAMNAME));
        }
        return result;
    }

    protected boolean paramsAreValid(String... params) {

        for (String param : params) {
            if (param == null || param.isEmpty()) return false;
        }
        return true;
    }

    public static boolean sessionWithError(Map<String, Object> params) {
        return params.get(SessionController.ERROR_PARAMNAME) != null;
    }

    protected String getString(Map<String, Object> params, String field) {
        Object o = params.get(field);
        return o != null ? o.toString() : "";
    }
}
