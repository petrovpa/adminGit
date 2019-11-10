package com.bivgroup.rest.admrestws.services;

import com.bivgroup.config.Config;
import com.bivgroup.config.ConfigUtils;
import com.bivgroup.core.audit.AuditIpInfo;
import com.bivgroup.core.audit.AuditParameters;
import com.bivgroup.request.Request;
import com.bivgroup.rest.admrestws.utils.DereferenceRequest;
import com.bivgroup.rest.common.AdmSessionController;
import com.bivgroup.service.JsonServiceCallerInterface;
import com.bivgroup.service.SoapServiceCaller;
import com.bivgroup.sessionutils.SessionController;
import com.bivgroup.utils.serviceloader.DefaultServiceLoader;

import javax.ws.rs.core.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

import static com.bivgroup.rest.common.Constants.*;
import static com.bivgroup.utils.ParamGetter.getLongParam;
import static com.bivgroup.utils.ParamGetter.getStringParam;

public abstract class BaseService {
    private Long sessionTimeOut;
    private JsonServiceCallerInterface jsonServiceCaller;
    static final String[] NO_IGNORABLE_FIELDS = {};
    SoapServiceCaller soapServiceCaller;
    private Config config;
    DereferenceRequest dereferenceRequest;

    BaseService() {
        String useServiceName = ConfigUtils.getProjectProperty("ru.diasoft.services.insurance.ServiceName");
        this.config = Config.getConfig(useServiceName);
        this.jsonServiceCaller = DefaultServiceLoader.loadServiceAny(JsonServiceCallerInterface.class);
        this.soapServiceCaller = DefaultServiceLoader.loadServiceAny(SoapServiceCaller.class);
        this.sessionTimeOut = getSessionTimeOut();
        this.dereferenceRequest = new DereferenceRequest();
    }

    Map<String, Object> callAdminWs(String methodName, Request request) {
        return callAdminWsWithIgnorableField(methodName, request, NO_IGNORABLE_FIELDS);
    }

    Map<String, Object> callCoreWs(String methodName, Request request) {
        return callCoreWsWithIgnorableField(methodName, request, NO_IGNORABLE_FIELDS);
    }


    Map<String, Object> callAdminWsWithIgnorableField(String methodName, Request request, String[] ignorableFieldNames) {
        return callService(ADMINWS, methodName, request, ignorableFieldNames);
    }

    Map<String, Object> callCoreWsWithIgnorableField(String methodName, Request request, String[] ignorableFieldNames) {
        return callService(COREWS, methodName, request, ignorableFieldNames);
    }

    Map<String, Object> callService(String moduleName, String methodName, Request request, String[] ignorableFieldNames) {
        SessionController sessionController = new AdmSessionController(this.sessionTimeOut);
        Map<String, Object> sessionCheckResult = sessionController.checkSession(request.getSessionId());
        String sessionLogin = getStringParam(sessionCheckResult, SYSTEM_LOGIN);
        String sessionAccountId = getStringParam(sessionCheckResult, SESSIONPARAM_USERACCOUNTID);
        Map<String, Object> result = jsonServiceCaller.callExternalService(moduleName, methodName, request, ignorableFieldNames);
        if (result != null) {
            result.put(SYSTEM_LOGIN, sessionLogin);
            result.put(SESSIONPARAM_USERACCOUNTID, sessionAccountId);
        }
        return result;
    }

    Map<String, Object> callAdminWsForAudit(String methodName, Request request, HttpHeaders httpHeaders,
                                            AuditParameters auditParameters) {
        return callAdminWsForAuditWithIgnorableField(methodName, request, httpHeaders, auditParameters, NO_IGNORABLE_FIELDS);
    }

    Map<String, Object> callAdminWsForAuditWithIgnorableField(String methodName, Request request, HttpHeaders httpHeaders,
                                                              AuditParameters auditParameters, String[] ignorableFieldNames) {
        String ipChain = httpHeaders.getHeaderString(IP_CHAIN_HEADER_NAME);
        AuditIpInfo ipInfo = new AuditIpInfo().setIpChainAddresses(ipChain == null || ipChain.isEmpty() ? DEFAULT_IP_CHAIN : ipChain);
        SessionController sessionController = new AdmSessionController(this.sessionTimeOut);
        Map<String, Object> sessionCheckResult = sessionController.checkSession(request.getSessionId());
        String sessionLogin = getStringParam(sessionCheckResult, SYSTEM_LOGIN);
        Long sessionAccountId = getLongParam(sessionCheckResult, SESSIONPARAM_USERACCOUNTID);
        Map<String, Object> result = jsonServiceCaller.callExternalService(ADMINWS, methodName, request, ignorableFieldNames);
        auditParameters.setLogin(sessionLogin);
        auditParameters.setUserAccountId(sessionAccountId);
        auditParameters.setIpInfo(ipInfo);
        return result;
    }

    private Long getSessionTimeOut() {
        Map<String, Object> params = new HashMap<>();
        params.put("SETTINGSYSNAME", "SESSION_SIZE");
        params.put(RETURN_AS_HASH_MAP, "TRUE");
        String sessionSizeStr = (String) this.soapServiceCaller.callExternalService(COREWS, "getSysSettingBySysName", params).get("SETTINGVALUE");
        if (sessionSizeStr == null || sessionSizeStr.isEmpty()) {
            return Long.valueOf(config.getParam("maxSessionSize", "10"));
        } else {
            return Long.parseLong(sessionSizeStr);
        }
    }
}
