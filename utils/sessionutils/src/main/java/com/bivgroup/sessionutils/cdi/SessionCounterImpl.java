package com.bivgroup.sessionutils.cdi;

import com.bivgroup.sessionutils.BroadcastTarget;
import com.bivgroup.sessionutils.UserInfoWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.bivgroup.sessionutils.BaseSessionController.SESSION_HASH_PARAMNAME;

@ApplicationScoped
public class SessionCounterImpl implements SessionCounter {

    private Map<String, Map<String, Long>> aliveCache;
    private Map<String, Long> deadCache;
    private Set<String> adminUsers;
    private String USERNAME_PARAMNAME;
    private String consuURI;
    private Long sessionTimeOut;
    private List<BroadcastTarget> servers;
    private String ADMIN_GROUPNAME_SYSNAME = "-";
    private long maxSessionsCountForAdmin = 10L;
    private long maxSessionsCount = 1L;
    // разрешение создать новую сессию при ошибке обращения к кешам
    private final boolean defaultDesision = true;
    private boolean isBuilt = false;
    private String restPath = "";

    public SessionCounterImpl() {
        init();
    }

    public void init() {
        this.servers = new ArrayList<>();
        if (this.aliveCache == null) {
            this.aliveCache = new HashMap<>();
        }
        if (this.deadCache == null) {
            this.deadCache = new HashMap<>();
        }
        this.adminUsers = new HashSet<>();
    }

    public Map<String, Long> getUserData(String userName) {
        if (!this.aliveCache.containsKey(userName)) {
            this.aliveCache.put(userName, new HashMap<>());
        }
        return this.aliveCache.get(userName);
    }

    public boolean sessionIsActive(String username, String hash) {
        Long time = getUserData(username).get(hash);
        return !sessionIsKilled(hash) && (time == null || time > System.currentTimeMillis());
    }

    public boolean sessionIsActive(Map<String, Object> params) {
        String username = params.get(USERNAME_PARAMNAME) != null ? params.get(USERNAME_PARAMNAME).toString() : "";
        String hash = params.getOrDefault(SESSION_HASH_PARAMNAME, "emptyhash").toString();
        return sessionIsActive(username, hash);
    }

    public SessionCounterImpl updateData(String name, String session, Long timeOut) {
        getUserData(name).put(session, System.currentTimeMillis() + (timeOut * 60 * 1000));
        return this;
    }

    public SessionCounterImpl updateSessionData(Map<String, Object> sessionParams) {
        return updateSessionDataWithMap(sessionParams, this.sessionTimeOut);
    }

    public SessionCounterImpl updateSessionData(Map<String, Object> sessionParams, Long timeout) {
        return updateSessionDataWithMap(sessionParams, timeout);
    }

    public SessionCounterImpl updateSessionDataWithMap(Map<String, Object> sessionParams, Long timeout) {
        String username = (String) sessionParams.get(USERNAME_PARAMNAME);
        if (username == null || username.isEmpty()) {
            return this;
        }
        String hash = (String) sessionParams.get(SESSION_HASH_PARAMNAME);
        // храним время, когда сессия истечет
        updateData(username, hash, timeout);
        return this;
    }

    public boolean sessionIsKilled(String hash) {
        return this.deadCache.containsKey(hash);
    }

    public void killSession(String userName, String hash) {
        Map<String, Long> userData = getUserData(userName);
        userData.remove(hash);
        if (userData.size() == 0) {
            this.aliveCache.remove(userName);
        }
        this.deadCache.put(hash, 0L);
    }

    public void loadServers(List<Map<String, Object>> servers) {
        this.servers.clear();
        for (Map<String, Object> server : servers) {
            this.servers.add(new BroadcastTarget(server));
        }
    }

    public SessionCounterImpl downloadServers() {
        if (consuURI == null || consuURI.isEmpty()) {
            return this;
        }
        List<Map<String, Object>> broadcastServers;
        Client client = new ResteasyClientBuilder()
                .establishConnectionTimeout(1, TimeUnit.SECONDS)
                .socketTimeout(1, TimeUnit.SECONDS)
                .build();
        WebTarget target = client.target(consuURI);
        Response response = null;
        String broadcastParamsStr;
        try {
            response = target.request().get();
            broadcastParamsStr = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            broadcastServers = mapper.readValue(broadcastParamsStr, mapper.getTypeFactory().constructCollectionType(
                    List.class, Map.class));
            loadServers(broadcastServers);
            return this;
        } catch (Exception ex) {
            String error = "Error while downloading servers list from " + consuURI;
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return this;
    }

    public SessionCounterImpl clearDeadSessions(String userName) {
        Map<String, Long> userData = getUserData(userName);
        Long now = System.currentTimeMillis();
        for (String key : new HashMap<>(userData).keySet()) {
            Long expireTime = userData.get(key);
            if (expireTime < now) {
                userData.remove(key);
            }
        }
        return this;
    }

    public boolean userCanCreateNewSession(UserInfoWrapper user) {
        downloadServers();
        Client client;
        WebTarget target;
        Response response = null;
        String responseJson;
        Map<String, Long> result;
        // храним все скачанные живые сессии, затем считаем их число в зависимости от роли пользователя
        Map<String, Object> aliveSessions = new HashMap<>();
        //если консул не настроен или не вернул сервисов, то требуется посмотреть
        // число живых сессий хотя бы на самом себе
        if (servers.isEmpty()) {
            aliveSessions.putAll(this.clearDeadSessions(user.getLogin()).getUserData(user.getLogin()));
        }
        for (BroadcastTarget s : servers) {
            String ip = s.getIp();
            String port = s.getPort();
            String postURI = "http://" + ip + ":" + port + "/" + restPath;
            // хардкод УРИ для отладки
            postURI = "http://localhost:8080/" + restPath;
            client = new ResteasyClientBuilder()
                    .establishConnectionTimeout(1, TimeUnit.SECONDS)
                    .socketTimeout(1, TimeUnit.SECONDS)
                    .build();
            target = client.target(postURI);
            try {
                response = target.request().post(Entity.entity(user.getLogin(), "application/json"));
                responseJson = response.readEntity(String.class);
                ObjectMapper mapper = new ObjectMapper();
                result = mapper.readValue(responseJson, new TypeReference<Map<String, String>>() {
                });
                aliveSessions.putAll(result);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (response != null) {
                    response.close();  // Обязательно закрыть
                }
            }
        }
        // если живых сессий не нашлось, то возможно произошли ошибки при опросе АПП, добавим
        // все живые сессии с самого себя
        if (aliveSessions.isEmpty()) {
            aliveSessions.putAll(this.clearDeadSessions(user.getLogin()).getUserData(user.getLogin()));
        }
        try {
            List<String> groups = user.getGroups();
            boolean isAdmin = adminUsers.contains(user.getLogin()) || groups.stream().anyMatch(group -> ADMIN_GROUPNAME_SYSNAME.equalsIgnoreCase(group));
            // админу можно до ADMIN_MAX_SESSIONCOUNT сессий
            if (isAdmin) {
                adminUsers.add(user.getLogin());
                if (aliveSessions.size() < maxSessionsCountForAdmin) {
                    return true;
                } else {
                    user.setParam("sessionLimitError", getErrorMessage(aliveSessions));
                    return false;
                }
            } else {
                if (aliveSessions.size() >= user.getLimit()) {
                    user.setParam("sessionLimitError", getErrorMessage(aliveSessions));
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception ex) {
            return defaultDesision;
        }
    }

    public String getErrorMessage(Map<String, Object> aliveSessions) {
        String error = "";
        Long time = Long.MAX_VALUE;
        for (String key : aliveSessions.keySet()) {
            Long timeout = Long.parseLong(aliveSessions.get(key).toString());
            if (timeout < time) {
                time = timeout;
            }
        }
        time = time - System.currentTimeMillis();
        double minutes = time.doubleValue() / 60000;

        error = "Превышено число активных сессий (" + aliveSessions.size() + "). Пожалуйста, попробуйте войти через " + (int) Math.ceil(minutes) + " мин.";
        return error;
    }


    // ============= ГЕТТЕРЫ / СЕТТЕРЫ

    public SessionCounterImpl setSessionTimeOut(Long sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
        return this;
    }

    public SessionCounterImpl setConsuURI(String consuURI) {
        this.consuURI = consuURI;
        return this;
    }

    public List<BroadcastTarget> getServers() {
        return servers;
    }

    public SessionCounterImpl setUsernameParamname(String USERNAME_PARAMNAME) {
        this.USERNAME_PARAMNAME = USERNAME_PARAMNAME;
        return this;
    }

    public Map<String, Long> getActiveSessions(String username) {
        return getUserData(username);
    }

    public SessionCounterImpl setMaxSessionsCountForAdmin(long maxSessionsCountForAdmin) {
        this.maxSessionsCountForAdmin = maxSessionsCountForAdmin;
        return this;
    }

    public SessionCounterImpl setMaxSessionsCount(long maxSessionsCount) {
        this.maxSessionsCount = maxSessionsCount;
        return this;
    }

    public SessionCounterImpl setAdminGroupSysname(String s) {
        this.ADMIN_GROUPNAME_SYSNAME = s;
        return this;
    }

    public void build() {
        isBuilt = true;
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    public Map<String, Object> getDescription() {
        Map<String, Object> result = new HashMap<>();
        result.put("Имя админской группы", ADMIN_GROUPNAME_SYSNAME);
        result.put("Сессия", sessionTimeOut.toString() + " м.");
        result.put("Кешированных пользователей", this.aliveCache.size());
        result.put("Адрес консула", consuURI);
        result.put("Макс сессий", maxSessionsCount);
        result.put("Макс. сессий админам", maxSessionsCountForAdmin);
        result.put("Известные админы", adminUsers.toString());
        return result;
    }

    public Map<String, Map<String, Long>> getAliveCache() {
        return aliveCache;
    }

    public String getRestPath() {
        return restPath;
    }

    public SessionCounter setRestPath(String restPath) {
        this.restPath = restPath;
        return this;
    }
}
