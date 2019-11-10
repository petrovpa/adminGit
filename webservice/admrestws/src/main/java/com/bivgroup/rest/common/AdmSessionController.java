package com.bivgroup.rest.common;

import com.bivgroup.config.Config;
import com.bivgroup.config.ConfigUtils;
import com.bivgroup.sessionutils.BaseSessionController;
import com.bivgroup.sessionutils.SessionController;
import com.bivgroup.sessionutils.SessionUtilException;
import com.bivgroup.utils.ParamGetter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.bivgroup.rest.common.Constants.*;

public class AdmSessionController extends BaseSessionController implements SessionController {

    private static final String THIS_SESSION_TYPE = "B2BBaseSession";
    private static final long DEFAULT_TIMEOUT;

    private static final String SESSION_TIME_PARAMNAME = "timeInMiliseconds";
    // сессия Б2Б
    // IP
    private static final String DEFAULT_IP = "127.0.0.1";
    private static final String SESSION_IP_ADDRESES_PARAMNAME = "IP";
    private static final String IP_ADDRESES_MAP_PARAMNAME = "IPMAP";
    private static final String IP_ADDRESES_ROUTE_PARAMNAME = "route";
    private static final String SESSION_DIVIDER = "__div__";
    public static final String SESSION_ID_PARAMNAME = "SESSIONID";

    static {
        String useServiceName = ConfigUtils.getProjectProperty("ru.diasoft.services.insurance.ServiceName");
        Config config = Config.getConfig(useServiceName);
        DEFAULT_TIMEOUT = ParamGetter.getLongParam(config.getParam("maxSessionSize", "10"));
    }

    public AdmSessionController(Long sessionTimeOut) {
        super(THIS_SESSION_TYPE, SESSION_DIVIDER, (sessionTimeOut * 60 * 1000));
        this.timeOut = sessionTimeOut;
        init();
    }

    public AdmSessionController() {
        super(THIS_SESSION_TYPE, SESSION_DIVIDER, (DEFAULT_TIMEOUT * 60 * 1000));
        this.timeOut = DEFAULT_TIMEOUT;
        init();
    }

    private void init() {
    }

    public String createSession(Map<String, Object> sessionParams) {
        String[] params = new String[10];
        // устанавливаем сессию
        // Проверки
        String userAccount = (String) sessionParams.get(SESSIONPARAM_USERACCOUNTID);
        String userType = (String) sessionParams.get(SESSIONPARAM_USERTYPEID);
        String department = (String) sessionParams.get(SESSIONPARAM_DEPARTMENTID);
        String userLogin = (String) sessionParams.get(SYSTEM_LOGIN);
        String userPass = (String) sessionParams.get(SYSTEM_PASS);
        String groups = (String) sessionParams.getOrDefault(USERGROUPS_PARAMNAME, "-");
        if (groups == null || groups.isEmpty()) {
            groups = "-";
        }
        if (!paramsAreValid(userAccount, userType, department, userLogin, userPass)) {
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
                params[9] = UUID.randomUUID().toString();
                return compileSessionId(params);
            } catch (SessionUtilException ex) {
                return null;
            }
        }
    }

    // doCheckTimeout теперь не проверяется (false) так как расшифровальщик выкидывает ошибку расшифровки
    public Map<String, Object> check(String sessionId, boolean doCreateNewSession) {
        Map<String, Object> result = new HashMap<>();
        try {
            String[] params = decompileSessionId(sessionId);
            result.put(SESSION_TYPE_PARAMNAME, params[0]);
            result.put(SESSION_TIME_PARAMNAME, params[1]);
            result.put(SESSION_IP_ADDRESES_PARAMNAME, params[2]);
            // параметры профиля
            result.put(SESSIONPARAM_USERACCOUNTID, params[3]);
            result.put(SESSIONPARAM_USERTYPEID, params[4]);
            result.put(SESSIONPARAM_DEPARTMENTID, params[5]);
            result.put(SYSTEM_LOGIN, params[6]);
            result.put(SYSTEM_PASS, params[7]);
            result.put(USERGROUPS_PARAMNAME, params[8]);
            result.put(SESSION_HASH_PARAMNAME, params[9]);
            //вывод маршрута запроса
            Map<String, Object> ipMap = new HashMap<>();
            String ipAdresses = params[2];
            ipMap.put(IP_ADDRESES_ROUTE_PARAMNAME, ipAdresses);
            result.put(IP_ADDRESES_MAP_PARAMNAME, ipMap);
            result.put(SESSION_IP_ADDRESES_PARAMNAME, ipAdresses);
            String newSessionId = compileSessionId(params);
            if (doCreateNewSession) {
                result.put(SESSION_ID_PARAMNAME, newSessionId);
            } else {
                result.put(SESSION_ID_PARAMNAME, sessionId);
            }
            return result;
        } catch (SessionUtilException | ArrayIndexOutOfBoundsException e) {
            return error(ERROR_TIMEOUT_SYSNAME, ERROR_TIMEOUT_TEXT);
        }
    }

    public Map<String, Object> checkSession(String sessionId) {
        return check(sessionId, false);
    }

    public Map<String, Object> checkAndCreateSession(String sessionId) {

        return check(sessionId, true);
    }

    public void setTimeout(Long timeOut) {
        this.timeOut = timeOut;
    }
}
