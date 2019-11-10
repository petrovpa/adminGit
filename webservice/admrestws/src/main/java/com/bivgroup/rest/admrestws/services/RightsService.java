package com.bivgroup.rest.admrestws.services;

import com.bivgroup.core.audit.Audit;
import com.bivgroup.core.audit.AuditParameters;
import com.bivgroup.core.audit.Obfuscator;
import com.bivgroup.core.audit.ResultOperation;
import com.bivgroup.core.audit.annotation.AuditBean;
import com.bivgroup.request.Request;
import com.bivgroup.rest.admrestws.pojo.common.ResponseFactory;
import com.bivgroup.rest.admrestws.pojo.request.base.DefaultRequest;
import com.bivgroup.rest.admrestws.pojo.request.params.LoadFilterMetadataForRightParam;
import com.bivgroup.rest.admrestws.pojo.request.params.rights.*;
import com.bivgroup.rest.admrestws.pojo.response.base.DefaultResponse;
import com.bivgroup.rest.admrestws.pojo.response.result.LoadAvailableRightsResult;
import com.bivgroup.rest.admrestws.pojo.response.result.LoadRightFiltersForObjectResult;
import com.bivgroup.rest.admrestws.pojo.response.result.LoadRoleResult;
import com.bivgroup.rest.admrestws.pojo.response.result.MetadataProfileRight;
import com.bivgroup.utils.ConvertUtils;
import com.bivgroup.utils.ParamGetter;
import org.apache.log4j.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bivgroup.login.base.common.Constants.USER_ACCOUNT_ID_PARAM_NAME;
import static com.bivgroup.rest.common.AdmSessionController.SESSION_ID_PARAMNAME;
import static com.bivgroup.rest.common.Constants.*;
import static com.bivgroup.utils.ParamGetter.*;

@Path("/rest/rights")
public class RightsService extends BaseService {

    private static final Logger logger = Logger.getLogger(RightsService.class);

    public RightsService() {
        super();
    }

    /**
     * Метод получения списка прав объекта по идентификатору
     *
     * @param request параметры запроса в json формате. Подробнее: {@link LoadRightsParams}
     * @return параметры ответа в json формате. Подробнее: список карт
     */
    @POST
    @Path("/loadObjectRights")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<Map<String, Object>>> loadObjectRights(DefaultRequest<LoadRightsParams> request) {
        logger.debug(String.format("loadObjectRights begin with params: %s", request));
        DefaultResponse<List<Map<String, Object>>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> admRightListResult = callAdminWs("admRightList", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admRightListResult, STATUS))) {
                response = ResponseFactory.createResponseListMapByResultMap(admRightListResult);
                response.setSessionId(getStringParam(admRightListResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(admRightListResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса получения списка прав по идентификатору объекта.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method loadObjectRights: ", ex);
            error = "Ошибка вызова сервиса получения списка прав пользователя по идентификатору объекта.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("loadObjectRights end with result: %s", response));
        return response;
    }

    /**
     * Метод получения списка доступных прав для сущности
     *
     * @param request параметры запроса в json формате. Подробнее: {@link LoadAvailableRightsParams}
     * @return параметры ответа в json формате. Подробнее: список карт
     */
    @POST
    @Path("/loadAvailableObjectRights")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<LoadAvailableRightsResult>> loadAvailableObjectRights(DefaultRequest<LoadAvailableRightsParams> request) {
        logger.debug(String.format("loadAvailableObjectRights begin with params: %s", request));
        DefaultResponse<List<LoadAvailableRightsResult>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> admRightAvailableResult = callAdminWs("admRightAvalible", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admRightAvailableResult, STATUS))) {
                if ("profileRights".equals(request.getParams().getRightType())) {
                    List<Map<String, Object>> resultList = getListFromResultMap(admRightAvailableResult);
                    resultList.forEach(item -> {
                        Map<String, Object> innerParams = new HashMap<>();
                        innerParams.put("RIGHTID", item.get("RIGHTID"));
                        innerParams.put("SEARCHCOLUMN", "SYSNAME");
                        innerParams.put("SEARCHTEXT", "");
                        item.put("METADATA", ParamGetter.getListFromResultMap(
                                this.soapServiceCaller.callExternalService(ADMINWS, "admRightFilterMetadataList", innerParams)
                        ));
                    });
                }
                response = ResponseFactory.createResponseListClassByResultMap(admRightAvailableResult, LoadAvailableRightsResult.class);
                response.setSessionId(getStringParam(admRightAvailableResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(admRightAvailableResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса получения списка доступных прав объекта.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method loadAvailableObjectRights: ", ex);
            error = "Ошибка вызова сервиса получения списка доступных прав объекта.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("loadAvailableObjectRights end with result: %s", response));
        return response;
    }

    /**
     * Сервис добавления любово права к ROLE|USERGROUP|ACCOUNT|DEPARTMENT
     *
     * @param request параметры запроса в json формате. Подробнее: {@link AddAnyRightsParams}
     * @return - возвращает статус выполнения операции
     */
    @POST
    @AuditBean
    @Path("/addAnyRights")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<Map<String, Object>> addAnyRights(DefaultRequest<AddAnyRightsParams> request,
                                                             @Context HttpHeaders httpHeaders,
                                                             @Context Audit audit) {

        logger.debug(String.format("addAnyRights begin with params: %s", request));

        DefaultResponse<Map<String, Object>> response = ResponseFactory.createEmptyGenericResponse();

        final AuditParameters auditParameters = new AuditParameters();
        String error = "";
        final ResultOperation resultOperation;
        final StringBuilder auditMessageBuilder = new StringBuilder();
        auditParameters.setOperation("addanyrights");
        final AddAnyRightsParams params = request.getParams();

        try {
            String[] ignorableFieldNames = NO_IGNORABLE_FIELDS;
            String rightType = params.getRightType();
            Request realRequest = new DefaultRequest();
            realRequest.setParams(params);
            realRequest.setSessionId(request.getSessionId());



            if ("rights".equals(rightType)) {
                ignorableFieldNames = new String[]{"FILTERVALUES", "RIGHTFILTERID", "FILTERSYSNAME", "EXTINTEGRATION"};
            } else {
                if (params.getRightFilterId() != null) {
                    Map<String, Object> filtersRemove = new HashMap<>();
                    filtersRemove.put("RIGHTFILTERID", params.getRightFilterId());
                    Map<String, Object> filterRemoveResult = soapServiceCaller.callExternalService(ADMINWS, "admrightfiltervalueremoveall", filtersRemove);
                    if (RESULT_STATUS_OK.equals(getStringParam(filterRemoveResult, STATUS))) {
                        filterRemoveResult = soapServiceCaller.callExternalService(ADMINWS, "admrightfilterremove", filtersRemove);
                        if (!RESULT_STATUS_OK.equals(getStringParam(filterRemoveResult, STATUS))) {
                            error = "Ошибка удаления фильтра.";
                            auditMessageBuilder.append(error);
                        }
                    } else {
                        error = "Ошибка удаление значений фильтра.";
                        auditMessageBuilder.append(error);
                    }
                }
                realRequest.setParams(new AddProfileRightToObject(params));
            }
            if (error.isEmpty()) {
                DefaultRequest<RemoveObjectRightsParam> removeObjectRightsRequest = new DefaultRequest<>();
                RemoveObjectRightsParam removeObjectRightsParams = new RemoveObjectRightsParam();
                removeObjectRightsParams.setObjectId(params.getObjectId());
                removeObjectRightsParams.setRightId(params.getRightId());
                removeObjectRightsParams.setRightOwner(params.getRightOwner());
                removeObjectRightsRequest.setParams(removeObjectRightsParams);
                removeObjectRightsRequest.setSessionId(request.getSessionId());
                DefaultResponse<String> removeObjectRightResponse = removeObjectRights(removeObjectRightsRequest, httpHeaders, audit);
                if (removeObjectRightResponse.getResult().equals("TRUE")) {
                    Map<String, Object> admRightResult = callAdminWsWithIgnorableField("admRightAdd", realRequest, ignorableFieldNames);
                    if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admRightResult, STATUS))) {
                        response.setResult(admRightResult);
                        response.setSessionId(getStringParam(admRightResult, SESSION_ID_PARAMNAME));

                        resultOperation = ResultOperation.SUCCESE;
                        auditParameters.setResultStatus(resultOperation);
                        auditMessageBuilder.append("rights was change.");

                        String login = getStringParam(admRightResult, SYSTEM_LOGIN);
                        auditParameters.setLogin(login);
                    } else {
                        error = getStringParam(admRightResult, ADMIN_CALL_ERROR);
                        if (error.isEmpty()) {
                            error = "Ошибка вызова системного сервиса добавления права.";
                            auditMessageBuilder.append(error);
                        }
                    }
                } else {
                    error = "Ошибка удаления права объекта.";
                    auditMessageBuilder.append(error);
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method addAnyRights: ", ex);
            error = "Ошибка вызова сервиса добавления прав.";
            auditMessageBuilder.append(error);
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }

        auditParameters.setMessage(auditMessageBuilder.toString());

        Map<String, Object> requestMap = ConvertUtils.convertObjectToMapWithoutNull(params);
        requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admmodulerightbyid", params.getRightId(),
                "RIGHTID", "RIGHTSYSNAME", "RIGHTNAME"));

        if ("ROLE".equals(params.getRightOwner())) {
            final Long roleId = params.getObjectId();
            requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admUserRoleByID", roleId,
                    "ROLEID", "ROLESYSNAME", "ROLENAME", "DESCRIPTION"));
        }

        if ("ACCOUNT".equals(params.getRightOwner())) {
            final Long userAccountId = params.getObjectId();
            requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admCurrentUserInfo", userAccountId,
                    USER_ACCOUNT_ID_PARAM_NAME, "EMPLOYEENAME", "USERLOGIN"));
        }

        Map<String, Object> responseMap = ConvertUtils.convertObjectToMapWithoutNull(response);

        audit.audit(auditParameters, requestMap, responseMap, (Class<Obfuscator>[]) null, null);

        logger.debug(String.format("addAnyRights end with result: %s", response));
        return response;
    }

    /**
     * Сервис удаление права у объекта
     *
     * @param request параметры запроса в json формате. Подробнее: {@link RemoveObjectRightsParam}
     * @return - возвращает статус выполнения удаления роли
     */
    @POST
    @AuditBean
    @Path("/removeObjectRights")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<String> removeObjectRights(DefaultRequest<RemoveObjectRightsParam> request,
                                                      @Context HttpHeaders httpHeaders,
                                                      @Context Audit audit) {

        logger.debug(String.format("removeObjectRights begin with params: %s", request));

        DefaultResponse<String> response = ResponseFactory.createEmptyGenericResponse();

        final AuditParameters auditParameters = new AuditParameters();
        String error = "";
        final ResultOperation resultOperation;
        final StringBuilder auditMessageBuilder = new StringBuilder();
        auditParameters.setOperation("removeobjectrights");
        final RemoveObjectRightsParam params = request.getParams();

        try {
            Map<String, Object> admRightRemoveResult = callAdminWs("admRightRemove", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admRightRemoveResult, STATUS))) {
                response = ResponseFactory.createResponseByResultMap(admRightRemoveResult, String.class);
                response.setSessionId(getStringParam(admRightRemoveResult, SESSION_ID_PARAMNAME));

                resultOperation = ResultOperation.SUCCESE;
                auditParameters.setResultStatus(resultOperation);
                auditMessageBuilder.append("rights was remove.");

                String login = getStringParam(admRightRemoveResult, SYSTEM_LOGIN);
                auditParameters.setLogin(login);

            } else {
                error = getStringParam(admRightRemoveResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса удаления права объекта.";
                    auditMessageBuilder.append(error);
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method removeObjectRights: ", ex);
            error = "Ошибка вызова сервиса удалениея права объекта.";
            auditMessageBuilder.append(error);
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }

        auditParameters.setMessage(auditMessageBuilder.toString());

        Map<String, Object> requestMap = ConvertUtils.convertObjectToMapWithoutNull(params);
        requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admmodulerightbyid", params.getRightId(),
                "RIGHTID", "RIGHTSYSNAME", "RIGHTNAME"));

        if ("ROLE".equals(params.getRightOwner())) {
            final Long roleId = params.getObjectId();
            requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admUserRoleByID", roleId,
                    "ROLEID", "ROLESYSNAME", "ROLENAME", "DESCRIPTION"));
        }

        if ("ACCOUNT".equals(params.getRightOwner())) {
            final Long userAccountId = params.getObjectId();
            requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admCurrentUserInfo", userAccountId,
                    USER_ACCOUNT_ID_PARAM_NAME, "EMPLOYEENAME", "USERLOGIN"));
        }

        Map<String, Object> responseMap = ConvertUtils.convertObjectToMapWithoutNull(response);

        audit.audit(auditParameters, requestMap, responseMap, (Class<Obfuscator>[]) null, null);

        logger.debug(String.format("removeObjectRights end with result: %s", response));
        return response;
    }


    /**
     * Метод получения списка фильтров права для объекта
     *
     * @param request параметры запроса в json формате. Подробнее: {@link LoadRightFiltersForObject}
     * @return параметры ответа в json формате. Подробнее: каждый элемент списка {@link LoadRightFiltersForObjectResult}
     */
    @POST
    @Path("/loadRightFiltersForObject")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<LoadRightFiltersForObjectResult>> loadRightFiltersForObject(DefaultRequest<LoadRightFiltersForObject> request) {
        logger.debug(String.format("loadRightFiltersForObject begin with params: %s", request));
        DefaultResponse<List<LoadRightFiltersForObjectResult>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> dsRightFilterListResult = callAdminWs("admrightfilterlist", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(dsRightFilterListResult, STATUS))) {
                response = ResponseFactory.createResponseListClassByResultMap(dsRightFilterListResult, LoadRightFiltersForObjectResult.class);
                response.setSessionId(getStringParam(dsRightFilterListResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(dsRightFilterListResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса загрузки фильтров конекртеного права и объекта.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method loadRightFiltersForObject: ", ex);
            error = "Ошибка вызова сервиса загрузки фильтров конекртеного права и объекта.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("loadRightFiltersForObject end with result: %s", response));
        return response;
    }

    /**
     * Метод получения списка матаданныз права
     *
     * @param request параметры запроса в json формате. Подробнее: {@link LoadFilterMetadataForRightParam}
     * @return параметры ответа в json формате. Подробнее: каждый элемент списка {@link MetadataProfileRight}
     */
    @POST
    @Path("/loadFilterMetadataForRight")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<MetadataProfileRight>> loadFilterMetadataForRight(DefaultRequest<LoadFilterMetadataForRightParam> request) {
        logger.debug(String.format("loadFilterMetadataForRight begin with params: %s", request));
        DefaultResponse<List<MetadataProfileRight>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> admRightFilterMetadataListResult = callAdminWs("admRightFilterMetadataList", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admRightFilterMetadataListResult, STATUS))) {
                response = ResponseFactory.createResponseListClassByResultMap(admRightFilterMetadataListResult, MetadataProfileRight.class);
                response.setSessionId(getStringParam(admRightFilterMetadataListResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(admRightFilterMetadataListResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса загрузки метданных роли.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method loadFilterMetadataForRight: ", ex);
            error = "Ошибка вызова сервиса загрузки метаданных роли.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("loadFilterMetadataForRight end with result: %s", response));
        return response;
    }
}
