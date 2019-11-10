package com.bivgroup.sessionutils;

import java.util.Map;

public interface

SessionController {

    String ERROR_PARAMNAME = "Error";
    String ERRORSYSANAME_PARAMNAME = "ErrorSysname";
    String ERROR_INVALID_SESSION = "Не верный тип сессии";

    String ERROR_TIMEOUT_TEXT = "Время сессии истекло!";
    String ERROR_TIMEOUT_SYSNAME = "SESSION_TIMEOUT";

    String createSession(Map<String, Object> sessionParams);

    void setTimeout(Long timeout);

    Map<String, Object> checkSession(String sessionId);

    Map<String, Object> checkAndCreateSession(String sessionId);

    Map<String, Object> check(String sessionId, boolean doCreateNewSession);

}
