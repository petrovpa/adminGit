package com.bivgroup.core.audit.rest;

import com.bivgroup.core.audit.AuditParameters;
import com.bivgroup.core.audit.ResultOperation;
import com.bivgroup.core.audit.annotation.AuditUserInfo;
import com.bivgroup.core.audit.annotation.AuditInputId;
import com.bivgroup.core.audit.annotation.AuditOutputId;
import com.bivgroup.core.audit.annotation.param.AuditObjParam;
import com.bivgroup.core.audit.annotation.param.AuditParam;
import com.bivgroup.core.audit.annotation.result.AuditObjResult;
import com.bivgroup.core.audit.annotation.result.AuditResult;
import com.bivgroup.core.audit.annotation.result.AuditResultOperationHandler;
import com.bivgroup.sessionutils.SessionController;
import com.bivgroup.utils.ParamGetter;
import com.bivgroup.utils.serviceloader.DefaultServiceLoader;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class AuditCommonHandler extends BaseHandler implements AuditResponseAndRequestHandler {
    private ResourceInfo resourceInfo;

    @Override
    public Map<String, Object> getRequest(HttpRequest request, UriInfo uriInfo, AuditParameters auditParameters) {
        Map<String, Object> result = new HashMap<>();
        Parameter[] parameters = this.resourceInfo.getResourceMethod().getParameters();
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                result.putAll(getRequestMap(request, uriInfo, auditParameters, parameter));
            }
        }
        AuditInputId inputDocumentId = this.resourceInfo.getResourceMethod().getAnnotation(AuditInputId.class);
        if (inputDocumentId != null) {
            String[] idSystemNameAndMap = inputDocumentId.value().split("=");
            Map<String, Object> realDocument = ParamGetter.getMapParamName(result, idSystemNameAndMap[1]);
            auditParameters.setInputDocumentId(ParamGetter.getLongParam(realDocument, idSystemNameAndMap[0]));
        }
        return result;
    }

    private Map<String, Object> getRequestMap(HttpRequest request, UriInfo uriInfo, AuditParameters auditParameters, Parameter parameter) {
        Map<String, Object> result = new HashMap<>();
        String value = getValueParam(request, uriInfo, parameter);
        try {
            if (value != null) {
                AuditParam auditParam = parameter.getAnnotation(AuditParam.class);
                if (auditParam != null) {
                    result.putAll(getMapFormString(value, auditParam.value()));
                }
                AuditObjParam auditObjParam = parameter.getAnnotation(AuditObjParam.class);
                if (auditObjParam != null) {
                    result.putAll(getParameterObjectValue(value, auditObjParam.value()));
                }
                analysisUserInfo(auditParameters, value);
            }
        } catch (IOException e) {
            logger.error(WORK_ERROR_MARKER, "error parse json string value {}", value);
            logger.error(WORK_ERROR_MARKER, "error parse json string", e);
        }
        return result;
    }

    private void analysisUserInfo(AuditParameters auditParameters, String value) throws IOException {
        AuditUserInfo auditUserInfo = getAuditUserInfo(this.resourceInfo);
        if (auditUserInfo != null) {
            String userInfoStr = getUserInfo(value, auditUserInfo.value());
            if (!userInfoStr.isEmpty()) {
                if (!auditUserInfo.sessionController().isEmpty()) {
                    SessionController sessionController = DefaultServiceLoader.loadServiceByName(SessionController.class, auditUserInfo.sessionController());
                    Map<String, Object> sessionParams = sessionController.checkSession(userInfoStr);
                    if (!auditUserInfo.loginParamName().isEmpty()) {
                        auditParameters.setLogin(ParamGetter.getStringParam(sessionParams, auditUserInfo.loginParamName()));
                    }
                    if (!auditUserInfo.userAccountIdParamName().isEmpty()) {
                        auditParameters.setUserAccountId(ParamGetter.getLongParam(sessionParams, auditUserInfo.userAccountIdParamName()));
                    }
                } else {
                    auditParameters.setLogin(userInfoStr);
                }
            }
        }
    }

    private String getValueParam(HttpRequest request, UriInfo uriInfo, Parameter parameter) {
        FormParam formParam = parameter.getAnnotation(FormParam.class);
        String value = null;
        if (formParam != null) {
            value = request.getDecodedFormParameters().getFirst(formParam.value());
        }
        QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
        if (queryParam != null) {
            value = uriInfo.getQueryParameters().getFirst(queryParam.value());
        }
        PathParam pathParam = parameter.getAnnotation(PathParam.class);
        if (pathParam != null) {
            value = uriInfo.getPathParameters().getFirst(pathParam.value());
        }
        HeaderParam headerParam = parameter.getAnnotation(HeaderParam.class);
        if (headerParam != null) {
            value = request.getHttpHeaders().getHeaderString(headerParam.value());
        }
        return value;
    }

    @Override
    public Map<String, Object> getResponse(ContainerResponseContext responseContext, AuditParameters auditParameters) {
        Map<String, Object> entity = convertObjectToMap(responseContext.getEntity());
        Map<String, Object> result = getResultObject(auditParameters, entity);
        AuditResultOperationHandler operationHandler = getAuditResultOperationHandler(this.resourceInfo);
        if (operationHandler != null) {
            AuditAnalysisResult auditAnalysisResult = DefaultServiceLoader.loadServiceByName(AuditAnalysisResult.class, operationHandler.value());
            ResultOperation resultOperation = auditAnalysisResult.analysisOperationResult(responseContext);
            if (resultOperation != null) {
                auditParameters.setResultStatus(resultOperation);
            }
        }
        AuditOutputId outputDocumentId = this.resourceInfo.getResourceMethod().getAnnotation(AuditOutputId.class);
        if (outputDocumentId != null) {
            String[] idSystemNameAndMap = outputDocumentId.value().split("=");
            Map<String, Object> realDocument = ParamGetter.getMapParamName(result, idSystemNameAndMap[1]);
            auditParameters.setInputDocumentId(ParamGetter.getLongParam(realDocument, idSystemNameAndMap[0]));
        }
        return result;
    }

    private Map<String, Object> getResultObject(AuditParameters auditParameters, Map<String, Object> entity) {
        AuditResult auditResult = this.resourceInfo.getResourceMethod().getAnnotatedReturnType().getAnnotation(AuditResult.class);
        Map<String, Object> result = new HashMap<>();
        if (auditResult == null) {
            auditResult = this.resourceInfo.getResourceMethod().getAnnotation(AuditResult.class);
            if (auditResult != null) {
                String[] auditObjResultParses = auditResult.value().split("=");
                Object realDocument = entity;
                if (auditObjResultParses.length > 1) {
                    realDocument = getObjectParamByPath(entity, auditObjResultParses[1]);
                    if (realDocument != null) {
                        auditParameters.setResultStatus(ResultOperation.SUCCESE);
                    }
                }
                result.put(auditObjResultParses[0], realDocument);
            } else {
                result = analysisAuditResultObj(auditParameters, entity);
            }
        }
        return result;
    }

    private Map<String, Object> analysisAuditResultObj(AuditParameters auditParameters, Map<String, Object> entity) {
        Map<String, Object> result = new HashMap<>();
        AuditObjResult auditObjResult = this.resourceInfo.getResourceMethod().getAnnotatedReturnType().getAnnotation(AuditObjResult.class);
        if (auditObjResult != null) {
            auditObjResult = this.resourceInfo.getResourceMethod().getAnnotation(AuditObjResult.class);
            if (auditObjResult != null) {
                String[] auditObjResultParses = auditObjResult.value().split("=");
                Map<String, Object> realDocument = entity;
                if (auditObjResultParses.length > 1) {
                    realDocument = getMapParamByPath(entity, auditObjResultParses[1]);
                }
                result.put(auditObjResultParses[0], realDocument);
            }
        }
        return result;
    }

    @Override
    public void setResourceInfo(ResourceInfo resourceInfo) {
        this.resourceInfo = resourceInfo;
    }
}
