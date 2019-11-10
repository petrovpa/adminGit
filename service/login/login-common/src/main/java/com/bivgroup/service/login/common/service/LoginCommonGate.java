package com.bivgroup.service.login.common.service;

import com.bivgroup.core.audit.Audit;
import com.bivgroup.core.audit.annotation.AuditBean;
import com.bivgroup.login.base.LoginBase;
import com.bivgroup.login.base.pojo.AuthUserInfo;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static com.bivgroup.login.base.common.Constants.*;
import static com.bivgroup.utils.ParamGetter.getBooleanParam;
import static com.bivgroup.utils.ParamGetter.getStringParam;

@Path("/rest/login")
public class LoginCommonGate extends LoginBase {
    private boolean isNeedCheckAdminRight;

    public LoginCommonGate() {
        super();
        this.isNeedCheckAdminRight = Boolean.valueOf(this.config.getParam("isNeedCheckAdminRight", "true"));
    }

    @POST
    @Path("/dsLogin")
    @AuditBean
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsLogin(@FormParam(FORM_PARAM_NAME) String paramsStr, @Context HttpHeaders httpHeaders, @Context Audit audit) {
        return this.dsLoginBase(paramsStr, httpHeaders, audit);
    }

    @POST
    @AuditBean
    @Path("/dsB2BLogin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLogin(@FormParam(FORM_PARAM_NAME) String paramsStr, @Context HttpHeaders httpHeaders, @Context Audit audit) {
        return this.dsB2BLoginBase(paramsStr, httpHeaders, audit);
    }

    @POST
    @Path("/dsB2BLogOut")
    @AuditBean
    @Produces(MediaType.APPLICATION_JSON)
    public Response dsB2BLogout(@FormParam("params") String paramStr, @Context HttpHeaders headers, @Context Audit audit) {
        return this.dsB2BLogoutBase(paramStr, headers, audit);
    }

    @POST
    @Path("/getActiveSessionsOfUser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Long> getActiveSessionsOfUser(String userName) {
        return this.getActiveSessionsOfUserBase(userName);
    }

    @Override
    protected String checkAuthorization(AuthUserInfo authUserInfo) {
        String error = "";
        if (isNeedCheckAdminRight) {
            Map<String, Object> checkRightParams = new HashMap<>();
            checkRightParams.put("LOGIN", authUserInfo.getB2bLogin());
            checkRightParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> rightCheckResult = soapServiceCaller.callExternalService(B2BPOSWS, "dsCheckAdminRightsByLogin",
                    checkRightParams);
            if (!getStringParam(rightCheckResult, ERROR).isEmpty()) {
                error = "Ошибка вызова сервиса проверки администраторских прав.";
            }
            if (error.isEmpty() && getBooleanParam(rightCheckResult, "ACCESS", false)) {
                error = "Пользователь является администратораом. Вход недоступен.";
            }
        }
        return error;
    }
}
