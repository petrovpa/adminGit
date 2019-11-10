package com.bivgroup.core.audit.rest;

import com.bivgroup.core.audit.*;
import com.bivgroup.core.audit.annotation.AuditHandler;
import com.bivgroup.core.audit.annotation.AuditOperation;
import com.bivgroup.core.audit.annotation.param.AuditObfuscateParam;
import com.bivgroup.core.audit.annotation.result.AuditObfuscateResult;
import com.bivgroup.utils.serviceloader.DefaultServiceLoader;
import org.jboss.resteasy.spi.HttpRequest;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Priority(2)
@Provider
public class AuditInfoFilter implements ContainerResponseFilter {
    private static final String IP_CHAIN_HEADER_NAME = "X-Forwarded-For";
    private static final String DEFAULT_IP_CHAIN = "127.0.0.1";

    @Context
    private ResourceInfo resourceInfo;
    @Context
    private HttpRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        AuditOperation auditing = resourceInfo.getResourceMethod().getAnnotation(AuditOperation.class);
        AuditParameters parameters = new AuditParameters();
        if (auditing != null) {
            parameters.setMessage(auditing.message());
            parameters.setOperation(auditing.value().toLowerCase().replaceAll(" ", ""));
            AuditIpInfo ipInfo = null;
            String ipChain = requestContext.getHeaderString(IP_CHAIN_HEADER_NAME);
            ipInfo = new AuditIpInfo().setIpChainAddresses(ipChain == null || ipChain.isEmpty() ? DEFAULT_IP_CHAIN : ipChain);
            parameters.setIpInfo(ipInfo);
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> document = new HashMap<>();
            AuditHandler auditHandler = getAuditHandler();
            if (auditHandler != null) {
                AuditResponseAndRequestHandler responseAndRequestHandler = DefaultServiceLoader.loadServiceByName(AuditResponseAndRequestHandler.class, auditHandler.value());
                responseAndRequestHandler.setResourceInfo(this.resourceInfo);
                params = responseAndRequestHandler.getRequest(request, requestContext.getUriInfo(), parameters);
                document = responseAndRequestHandler.getResponse(responseContext, parameters);
            }
            Audit audit = new AuditImpl();
            Class<Obfuscator>[] auditObfuscateParamClasses = null;
            AuditObfuscateParam auditObfuscateParam = resourceInfo.getResourceMethod().getAnnotation(AuditObfuscateParam.class);
            if (auditObfuscateParam != null) {
                auditObfuscateParamClasses = auditObfuscateParam.value();
            }
            Class<Obfuscator>[] auditObfuscateResultClasses = null;
            AuditObfuscateResult auditObfuscateResult = getAuditObfuscateResult();
            if (auditObfuscateResult != null) {
                auditObfuscateResultClasses = auditObfuscateResult.value();
            }
            audit.audit(parameters, params, document, auditObfuscateParamClasses, auditObfuscateResultClasses);
        }
    }

    private AuditHandler getAuditHandler() {
        AuditHandler auditHandler = resourceInfo.getResourceMethod().getAnnotation(AuditHandler.class);
        if (auditHandler == null) {
            auditHandler = resourceInfo.getResourceClass().getAnnotation(AuditHandler.class);
        }
        return auditHandler;
    }

    private AuditObfuscateResult getAuditObfuscateResult() {
        AuditObfuscateResult auditObfuscateResult = resourceInfo.getResourceMethod().getAnnotation(AuditObfuscateResult.class);
        if (auditObfuscateResult == null) {
            auditObfuscateResult = resourceInfo.getResourceMethod().getAnnotatedReturnType().getAnnotation(AuditObfuscateResult.class);
        }
        return auditObfuscateResult;
    }

}
