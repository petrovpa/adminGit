/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom;

import com.bivgroup.services.b2bposws.facade.B2BPosServiceSessionController;
import com.bivgroup.sessionutils.SessionController;
import org.apache.commons.codec.digest.DigestUtils;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.math.BigDecimal;
import java.util.*;

import static com.bivgroup.services.b2bposws.facade.B2BPosServiceSessionController.*;
import static com.bivgroup.sessionutils.BaseSessionController.getErrorMap;

/**
 * @author averichevsm
 */
@BOName("SessionCustom")
public class SessionCustomFacade extends BaseFacade {

    private static final String SERVICE_NAME = "b2bposws";

    private Long sessionTimeOut = 10L;

    private String ERROR = SessionController.ERROR_PARAMNAME;

    public SessionCustomFacade() {
        super();
        init();
    }

    private void init() {
        Config config = Config.getConfig(SERVICE_NAME);
        sessionTimeOut = Long.valueOf(config.getParam("maxSessionSize", "10"));
    }

    protected static Date getDateParam(Object date) {
        if (date != null) {
            if (date instanceof Double) {
                return XMLUtil.convertDate((Double) date);
            } else if (date instanceof BigDecimal) {
                return XMLUtil.convertDate((BigDecimal) date);
            }
            return (Date) date;
        } else {
            return null;
        }
    }

    private String makeSessionId(String login, String passSha, Long userAccountId, Long userTypeId, Long departmentId) {
        SessionController controller = new B2BPosServiceSessionController(this.sessionTimeOut);
        Map<String, Object> sessionParams = new HashMap<>();
        sessionParams.put(B2B_USERLOGIN_PARAMNAME, login);
        sessionParams.put(B2B_USERPASSWORD_PARAMNAME, passSha);
        sessionParams.put(B2B_USERACCOUNTID_PARAMNAME, userAccountId.toString());
        sessionParams.put(B2B_USERTYPEID_PARAMNAME, userTypeId.toString());
        sessionParams.put(B2B_DEPARTMENTID_PARAMNAME, departmentId.toString());
        return controller.createSession(sessionParams);
    }

    @WsMethod(requiredParams = {"username", "passwordSha"})
    public Map<String, Object> dsB2BCheckLogin(Map<String, Object> params) throws Exception {
        return this.selectQuery("dsCheckLogin", null, params);
    }

    @WsMethod(requiredParams = {"login", "password"})
    public Map<String, Object> dsB2BLogin(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get("login").toString();
        String password = params.get("password").toString();

        //на текущий момент в базе хранится sha код пароля
        String passSha = DigestUtils.shaHex(password);
        Map<String, Object> qParam = new HashMap<String, Object>();
        qParam.put("username", XMLUtil.getUserName(login));
        qParam.put("passwordSha", passSha);
        Map<String, Object> qres = this.selectQuery("dsCheckLogin", null, qParam);
        if ((qres != null) && (qres.get(RESULT) != null) && ((List<Map<String, Object>>) qres.get(RESULT)).size() > 0) {
            List<Map<String, Object>> loginList = (List<Map<String, Object>>) qres.get(RESULT);
            Date expDate = getDateParam(loginList.get(0).get("PWDEXPDATE"));
            GregorianCalendar gcToday = new GregorianCalendar();
            gcToday.setTime(new Date());
            if (gcToday.getTime().getTime() < expDate.getTime()) {
                result.put("AUTHMETHOD", loginList.get(0).get("AUTHMETHOD"));
                result.put("FIRSTNAME", loginList.get(0).get("FIRSTNAME"));
                result.put("MIDDLENAME", loginList.get(0).get("MIDDLENAME"));
                result.put("LASTNAME", loginList.get(0).get("LASTNAME"));
                Long userAccountId = Long.valueOf(loginList.get(0).get("USERACCOUNTID").toString());
                Long departmentId = 0L;
                if (loginList.get(0).get("DEPARTMENTID") != null) {
                    departmentId = Long.valueOf(loginList.get(0).get("DEPARTMENTID").toString());
                }
                Long userTypeId = Long.valueOf(loginList.get(0).get("OBJECTTYPE").toString());
                result.put(SESSION_ID_PARAMNAME, makeSessionId(login, passSha, userAccountId, userTypeId, departmentId));
            } else {
                //throw new Exception("Истекла дата действия пароля");
                result.put("Error", "Истекла дата действия пароля");
                return result;
            }
        } else {
            //throw new Exception("Некорректный логин/пароль");
            result.put("Error", "Некорректный логин/пароль");
            return result;
        }
        return result;
    }

    @WsMethod(requiredParams = {"login", "password"})
    public Map<String, Object> dsB2BLoginEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        String login = params.get("login").toString();
        String password = params.get("password").toString();

        //на текущий момент в базе хранится sha код пароля
        //String passSha = DigestUtils.shaHex(password);
        Map<String, Object> qParam = new HashMap<>();
        qParam.put("username", XMLUtil.getUserName(login));
        qParam.put("passwordSha", password);
        Map<String, Object> qres = this.selectQuery("dsCheckLogin", null, qParam);
        if ((qres != null) && (qres.get(RESULT) != null) && ((List<Map<String, Object>>) qres.get(RESULT)).size() > 0) {
            List<Map<String, Object>> loginList = (List<Map<String, Object>>) qres.get(RESULT);
            Date expDate = getDateParam(loginList.get(0).get("PWDEXPDATE"));
            GregorianCalendar gcToday = new GregorianCalendar();
            gcToday.setTime(new Date());
            if (gcToday.getTime().getTime() < expDate.getTime()) {
                result.put("AUTHMETHOD", loginList.get(0).get("AUTHMETHOD"));
                result.put("FIRSTNAME", loginList.get(0).get("FIRSTNAME"));
                result.put("MIDDLENAME", loginList.get(0).get("MIDDLENAME"));
                result.put("LASTNAME", loginList.get(0).get("LASTNAME"));
                Long userAccountId = Long.valueOf(loginList.get(0).get("USERACCOUNTID").toString());
                Long departmentId = 0L;
                if (loginList.get(0).get("DEPARTMENTID") != null) {
                    departmentId = Long.valueOf(loginList.get(0).get("DEPARTMENTID").toString());
                }
                Long userTypeId = Long.valueOf(loginList.get(0).get("OBJECTTYPE").toString());
                result.put(SESSION_ID_PARAMNAME, makeSessionId(login, password, userAccountId, userTypeId, departmentId));
            } else {
                //throw new Exception("Истекла дата действия пароля");
                result.put("Error", "Истекла дата действия пароля");
                return result;
            }
        } else {
            //throw new Exception("Некорректный логин/пароль");
            result.put("Error", "Некорректный логин/пароль");
            return result;
        }
        return result;
    }

    @WsMethod(requiredParams = {"SESSIONID"})
    public Map<String, Object> dsB2BCallService(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String sessionIdCoded = params.get(SESSION_ID_PARAMNAME).toString();

        String serviceName = "";
        String methodName = "";
        if ((params.get("SERVICENAME") != null) && (params.get("METHODNAME") != null)) {
            serviceName = params.get("SERVICENAME").toString();
            methodName = params.get("METHODNAME").toString();
        }
        //
        String sessionId = null;
        Map<String, Object> sessionParams;
        B2BPosServiceSessionController controller = new B2BPosServiceSessionController(this.sessionTimeOut);
        if (!sessionIdCoded.isEmpty()) {
            sessionParams = controller.checkSession(sessionId);
            sessionId = (String) sessionParams.get(SESSION_ID_PARAMNAME);
            if (sessionParams.get(ERROR) != null) {
                return getErrorMap(sessionParams);
            }
            String login = (String) sessionParams.get(B2B_USERLOGIN_PARAMNAME);
            String password = (String) sessionParams.get(B2B_USERPASSWORD_PARAMNAME);
            if (!serviceName.isEmpty() && !methodName.isEmpty()) {
                params.putAll(sessionParams);
                result = this.callService(serviceName, methodName, params, login, password);
            }
            result.put(B2BPosServiceSessionController.SESSION_ID_PARAMNAME, controller.regenerateSession(sessionId));
        } else {
            result.put("Error", "Пользователь не представился");
        }
        return result;
    }

}
