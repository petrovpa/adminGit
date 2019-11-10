package com.bivgroup.sessionutils.cdi;

import com.bivgroup.sessionutils.BroadcastTarget;
import com.bivgroup.sessionutils.UserInfoWrapper;

import java.util.List;
import java.util.Map;

public interface SessionCounter {

    void init();

    Map<String, Long> getUserData(String userName);

    boolean sessionIsActive(String username, String hash);

    boolean sessionIsActive(Map<String, Object> params);

    SessionCounter updateData(String name, String session, Long timeOut);

    SessionCounter updateSessionData(Map<String, Object> sessionParams);

    SessionCounter updateSessionData(Map<String, Object> sessionParams, Long timeout);

    SessionCounter updateSessionDataWithMap(Map<String, Object> sessionParams, Long timeout);

    boolean sessionIsKilled(String hash);

    void killSession(String userName, String hash);

    void loadServers(List<Map<String, Object>> servers);

    SessionCounter downloadServers();

    SessionCounter clearDeadSessions(String userName);

    boolean userCanCreateNewSession(UserInfoWrapper user);

    String getErrorMessage(Map<String, Object> aliveSessions);


    // ============= ГЕТТЕРЫ / СЕТТЕРЫ

    SessionCounter setSessionTimeOut(Long sessionTimeOut);

    SessionCounter setConsuURI(String consuURI);

    List<BroadcastTarget> getServers();

    SessionCounter setUsernameParamname(String USERNAME_PARAMNAME);

    Map<String, Long> getActiveSessions(String username);

    SessionCounter setMaxSessionsCountForAdmin(long maxSessionsCountForAdmin);

    SessionCounter setMaxSessionsCount(long maxSessionsCount);

    SessionCounter setAdminGroupSysname(String s);

    SessionCounter setRestPath(String s);

    String getRestPath();

    void build();

    boolean isBuilt();

    Map<String, Object> getDescription();

    Map<String, Map<String, Long>> getAliveCache();


}
