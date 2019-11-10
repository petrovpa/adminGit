package com.bivgroup.rest.admrestws.services;

import com.bivgroup.core.audit.Audit;
import com.bivgroup.core.audit.AuditParameters;
import com.bivgroup.core.audit.Obfuscator;
import com.bivgroup.core.audit.ResultOperation;
import com.bivgroup.core.audit.annotation.AuditBean;
import com.bivgroup.password.PasswordStrengthVerifier;
import com.bivgroup.rest.admrestws.pojo.common.ResponseFactory;
import com.bivgroup.rest.admrestws.pojo.request.base.DefaultRequest;
import com.bivgroup.rest.admrestws.pojo.request.params.*;
import com.bivgroup.rest.admrestws.pojo.request.params.base.DepartmentIdParams;
import com.bivgroup.rest.admrestws.pojo.response.base.BaseResponse;
import com.bivgroup.rest.admrestws.pojo.response.base.DefaultResponse;
import com.bivgroup.rest.admrestws.pojo.response.result.*;
import com.bivgroup.utils.ConvertUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

import static com.bivgroup.login.base.common.Constants.SYSTEM_PARAM_NAME_LOGIN;
import static com.bivgroup.login.base.common.Constants.USER_ACCOUNT_ID_PARAM_NAME;
import static com.bivgroup.password.PasswordStrengthVerifier.*;
import static com.bivgroup.rest.admrestws.pojo.common.ResponseFactory.createClassByMap;
import static com.bivgroup.rest.common.AdmSessionController.SESSION_ID_PARAMNAME;
import static com.bivgroup.rest.common.Constants.*;
import static com.bivgroup.utils.ParamGetter.*;

/**
 * @author npetrov
 */
@Path("/rest/admrestgate")
public class AdminWebServiceGate extends BaseService {
    private static final Logger logger = Logger.getLogger(AdminWebServiceGate.class);
    private static final List<String> SKIPPED_USER_TYPE;

    static {
        SKIPPED_USER_TYPE = Collections.unmodifiableList(Arrays.asList("device", "program"));
    }

    public AdminWebServiceGate() {
        super();
    }

    @POST
    @AuditBean
    @Path("/saveAccount")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<SaveAccountResult> saveAccount(DefaultRequest<SaveAccountParams> request,
                                                          @Context HttpHeaders httpHeaders,
                                                          @Context Audit audit) {
        String error = "";
        StringBuilder auditMessageBuilder = new StringBuilder();

        String methodName = "dsAccountCreate";
        String auditOperation = "accountcreate";
        String rusMessage = "создания аккаунта";
        String enMessage = "create";
        String[] ignorableFieldNames = new String[]{"NEWLOGIN", "OBJECTTYPE", "PARTICIPANTID", "USERACCOUNTID", "EMPLOYEEID", "STATUS"};
        boolean isCreate = true;
        SaveAccountParams params = request.getParams();
        if (params.getUserAccountId() != null) {
            isCreate = false;
            methodName = "dsAccountUpdate";
            auditOperation = "accountupdate";
            rusMessage = "обновления данных аккаунта";
            enMessage = "update";
            ignorableFieldNames = new String[]{"PASSWORD", "RETPASSWORD", "USERTYPE"};
            DefaultRequest<UserTypeParams> userTypeParam = new DefaultRequest<>();
            userTypeParam.setSessionId(request.getSessionId());
            userTypeParam.setParams(new UserTypeParams(params.getUserType()));
            DefaultResponse<List<UserTypeResult>> userTypeResponse = this.loadUserTypes(userTypeParam);
            if (userTypeResponse != null && userTypeResponse.getResult() != null && !userTypeResponse.getResult().isEmpty()) {
                params.setObjectType(userTypeResponse.getResult().get(0).getCode());
            }
        } else {
            Result pasVerifierResult = createPasswordVerifier().isPasswordValid(params.getPassword());
            if (!pasVerifierResult.equals(Result.OK)) {
                error = pasVerifierResult.getDescription();
                auditMessageBuilder.append("Невозможно создать пользователя. Причина: ").append(error);
            }
        }
        final AuditParameters auditParameters = new AuditParameters();
        ResultOperation resultOperation;
        auditParameters.setOperation(auditOperation);
        DefaultResponse<SaveAccountResult> response = ResponseFactory.createEmptyGenericResponse();
        if (error.isEmpty()) {
            try {
                Map<String, Object> saveAccountResult = callAdminWsForAuditWithIgnorableField(methodName, request, httpHeaders, auditParameters, ignorableFieldNames);
                if (saveAccountResult != null && RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(saveAccountResult, STATUS))) {
                    if (isCreate) {
                        saveAccountResult = saveAccountResult.entrySet().stream()
                                .filter(item -> !getStringParam(item.getValue()).equalsIgnoreCase("NULL"))
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue
                                ));
                        response = ResponseFactory.createResponseByMap(saveAccountResult, SaveAccountResult.class);
                    } else {
                        SaveAccountResult saveAccountResultObj = new SaveAccountResult();
                        saveAccountResultObj.setEmployeeId(params.getEmployeeId());
                        saveAccountResultObj.setParticipantId(params.getParticipantId());
                        saveAccountResultObj.setUserAccountId(params.getUserAccountId());
                        saveAccountResultObj.setUserId(params.getUserId());
                        response.setResult(saveAccountResultObj);
                    }
                    response.setSessionId(getStringParam(saveAccountResult, SESSION_ID_PARAMNAME));

                    resultOperation = ResultOperation.SUCCESE;
                    auditParameters.setResultStatus(resultOperation);
                    auditMessageBuilder.append("New user created.");
                } else {
                    error = getStringParam(saveAccountResult, ADMIN_CALL_ERROR);
                    if (error.isEmpty()) {
                        error = "Ошибка вызова системного сервиса " + rusMessage + " пользователя.";
                    }
                    auditMessageBuilder.append(String.format("Audit: error call system service for %s user account. Call error service: %s", enMessage, error));
                }
            } catch (Exception ex) {
                auditMessageBuilder.append(String.format("An error occurred in method saveAccount: %s", ex));
            }
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }

        auditParameters.setMessage(auditMessageBuilder.toString());

        Map<String, Object> requestMap = ConvertUtils.convertObjectToMapWithoutNull(request.getParams());
        requestMap.remove("PASSWORD"); //delete password from request
        requestMap.remove("RETPASSWORD"); //delete password from request
        Map<String, Object> responseMap = ConvertUtils.convertObjectToMapWithoutNull(response);

        audit.audit(auditParameters, requestMap, responseMap, (Class<Obfuscator>[]) null, null);

        return response;
    }

    private PasswordStrengthVerifier createPasswordVerifier() {
        try {
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> passwordSetting = soapServiceCaller.callExternalService(ADMINWS, "getPwdAcctSettings", params);
            int minLen = getIntegerParam(passwordSetting, PWD_MIN_LEN);
            if (minLen == 0) {
                minLen = 6;
            }
            int maxLen = getIntegerParam(passwordSetting, PWD_MAX_LEN);
            if (maxLen == 0) {
                maxLen = 40;
            }
            int groupsCount = getIntegerParam(passwordSetting, PWD_GROUPS_COUNT);
            if (groupsCount == 0) {
                groupsCount = 40;
            }
            int idtSymCount = getIntegerParam(passwordSetting, PWD_IDT_SYM_COUNT);
            if (idtSymCount == 0) {
                idtSymCount = 40;
            }
            boolean additionalAllowed = getBooleanParam(passwordSetting, PWD_GROUPS_ADDITIONAL_ALLOWED, false);
            return new PasswordStrengthVerifier(minLen, maxLen, idtSymCount, groupsCount,
                    additionalAllowed ? PasswordStrengthVerifier.SET_ADDITIONAL
                            : PasswordStrengthVerifier.SET_ASCII_ALPHANUM);
        } catch (Exception ex) {
            logger.error("Error while creating PasswordStrengthVerifier: " + ex.getMessage(), ex);
            return new PasswordStrengthVerifier(6, 40, 2, 3, PasswordStrengthVerifier.SET_ADDITIONAL);
        }
    }

    @POST
    @Path("/roleListByAccountId")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<Map<String, Object>>> roleListByAccountId(DefaultRequest<RoleListByAccountIdParams> request) {
        logger.debug(String.format("roleListByAccountId begin with params: %s", request));
        DefaultResponse<List<Map<String, Object>>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> admRoleListByAccountResult = callAdminWs("admRoleListByAccount", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admRoleListByAccountResult, STATUS))) {
                response = ResponseFactory.createResponseListMapByResultMap(admRoleListByAccountResult);
                response.setSessionId(getStringParam(admRoleListByAccountResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(admRoleListByAccountResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса получения ролей пользователя.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method roleListByAccountId: ", ex);
            error = "Ошибка вызова сервиса получения ролей пользователя.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }

        logger.debug(String.format("roleListByAccountId end with result: %s", response));
        return response;
    }

    /**
     * Метод получения информации о пользователе по идентификатору аккаунта
     *
     * @param request параметры запроса в json формате. Подробнее: {@link UserInfoByAccountIdParams}
     * @return параметры ответа в json формате. Подробнее: {@link UserInfoByAccountIdResult}
     */
    @POST
    @Path("/getUserInfoByAccountId")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<UserInfoByAccountIdResult> getUserInfoByAccountId(DefaultRequest<UserInfoByAccountIdParams> request) {
        logger.debug(String.format("getUserInfoByAccountId begin with params: %s", request));
        DefaultResponse<UserInfoByAccountIdResult> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> admCurrentUserInfoResult = callAdminWs("admCurrentUserInfo", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admCurrentUserInfoResult, STATUS))) {
                response = ResponseFactory.createResponseByMap(admCurrentUserInfoResult, UserInfoByAccountIdResult.class);
                response.setSessionId(getStringParam(admCurrentUserInfoResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(admCurrentUserInfoResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса получения информации о пользователе по идентификатору аккаунта.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method getUserInfoByAccountId: ", ex);
            error = "Ошибка вызова сервиса получения информации о пользователе по идентификатору аккаунта.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("getUserInfoByAccountId end with result: %s", response));
        return response;
    }

    /**
     * Метод по добавлению роли пользователю
     *
     * @param request параметры запроса в json формате. Подробнее: {@link RoleUserAddParams}
     * @return параметры ответа в json формате. Подробнее: {@link RoleUserAddResult}
     */
    @POST
    @AuditBean
    @Path("/roleUserAdd")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<RoleUserAddResult> roleUserAdd(DefaultRequest<RoleUserAddParams> request,
                                                          @Context HttpHeaders httpHeaders,
                                                          @Context Audit audit) {
        DefaultResponse<RoleUserAddResult> response = ResponseFactory.createEmptyGenericResponse();

        final AuditParameters auditParameters = new AuditParameters();
        String error = "";
        final ResultOperation resultOperation;
        final StringBuilder auditMessageBuilder = new StringBuilder();
        auditParameters.setOperation("roleuseradd");

        try {
            Map<String, Object> admRoleUserAddResult = callAdminWsForAudit("admRoleUserAdd", request, httpHeaders, auditParameters);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admRoleUserAddResult, STATUS))) {
                response.setSessionId(getStringParam(admRoleUserAddResult, SESSION_ID_PARAMNAME));

                resultOperation = ResultOperation.SUCCESE;
                auditParameters.setResultStatus(resultOperation);
                auditMessageBuilder.append("New role for user added.");

            } else {
                error = getStringParam(admRoleUserAddResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса добавление роли пользователю.";
                }
                auditMessageBuilder.append(String.format("Audit: error call system service for add role to the user account. Call error service: %s", error));
            }
        } catch (Exception ex) {
            auditMessageBuilder.append(String.format("An error occurred in method roleUserAdd: %s", ex));
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }

        auditParameters.setMessage(auditMessageBuilder.toString());

        RoleUserAddParams roleUserAddParams = request.getParams();
        Map<String, Object> requestMap = ConvertUtils.convertObjectToMapWithoutNull(roleUserAddParams);
        requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admUserRoleByID", roleUserAddParams.getRoleId(),
                "ROLEID", "ROLESYSNAME", "ROLENAME"));

        RoleUserAddParams requestParam = request.getParams();
        if (requestParam.getUserAccountId() != null) {
            final Long userAccountId = requestParam.getUserAccountId();
            requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admCurrentUserInfo", userAccountId,
                    USER_ACCOUNT_ID_PARAM_NAME, "EMPLOYEENAME", "USERLOGIN"));
        }

        Map<String, Object> responseMap = ConvertUtils.convertObjectToMapWithoutNull(response);

        audit.audit(auditParameters, requestMap, responseMap, (Class<Obfuscator>[]) null, null);

        return response;
    }

    /**
     * Метод по удалению роли у пользователя.
     *
     * @param request параметры запроса в json формате. Подробнее: {@link RoleUserRemoveParams}
     * @return параметры ответа в json формате. Подробнее: {@link RoleUserRemoveResult}
     */
    @POST
    @AuditBean
    @Path("/roleUserRemove")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<String> roleUserRemove(DefaultRequest<RoleUserRemoveParams> request,
                                                  @Context HttpHeaders httpHeaders,
                                                  @Context Audit audit) {
        DefaultResponse<String> response = ResponseFactory.createEmptyGenericResponse();

        final AuditParameters auditParameters = new AuditParameters();
        String error = "";
        final ResultOperation resultOperation;
        final StringBuilder auditMessageBuilder = new StringBuilder();
        auditParameters.setOperation("roleuserremove");

        try {
            Map<String, Object> admRoleUserRemoveResult = callAdminWsForAudit("admRoleUserRemove", request, httpHeaders, auditParameters);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admRoleUserRemoveResult, STATUS))) {
                response.setResult(getStringParam(admRoleUserRemoveResult, RESULT));
                response.setSessionId(getStringParam(admRoleUserRemoveResult, SESSION_ID_PARAMNAME));

                resultOperation = ResultOperation.SUCCESE;
                auditParameters.setResultStatus(resultOperation);
                auditMessageBuilder.append("Role for user removed.");

            } else {
                error = getStringParam(admRoleUserRemoveResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса удаления роли у пользователя.";
                }
                auditMessageBuilder.append(String.format("Audit: error call system service for delete role at user account. Call error service: %s", error));
            }
        } catch (Exception ex) {
            auditMessageBuilder.append(String.format("An error occurred in method roleUserRemove: %s", ex));
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }

        auditParameters.setMessage(auditMessageBuilder.toString());

        RoleUserRemoveParams roleUserRemoveParams = request.getParams();
        Map<String, Object> requestMap = ConvertUtils.convertObjectToMapWithoutNull(roleUserRemoveParams);
        requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admUserRoleByID", roleUserRemoveParams.getRoleId(),
                "ROLEID", "ROLESYSNAME", "ROLENAME"));

        RoleUserRemoveParams requestParam = request.getParams();
        if (requestParam.getUserAccountId() != null) {
            final Long userAccountId = requestParam.getUserAccountId();
            requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admCurrentUserInfo", userAccountId,
                    USER_ACCOUNT_ID_PARAM_NAME, "EMPLOYEENAME", "USERLOGIN"));
        }

        Map<String, Object> responseMap = ConvertUtils.convertObjectToMapWithoutNull(response);

        audit.audit(auditParameters, requestMap, responseMap, (Class<Obfuscator>[]) null, null);


        return response;
    }

    /**
     * Метод получения списка груп пользователя по идентификатору аккаунта
     *
     * @param request параметры запроса в json формате. Подробнее: {@link GroupListByAccountIdParams}
     * @return параметры ответа в json формате. Подробнее: список из карт вида:
     * <UL>
     * <LI>USERGROUPID - идентификтаор связи группы и пользователя</LI>
     * <LI>PROJECTID - идентификтаор проекта</LI>
     * <LI>GROUPNAME - имя группы</LI>
     * <LI>DESCRIPTION - описание группы группы</LI>
     * <LI>PARENTGROUP - родительская гурппа</LI>
     * </UL>
     */
    @POST
    @Path("/getGroupListByAccountId")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<Map<String, Object>>> getGroupListByAccountId(DefaultRequest<GroupListByAccountIdParams> request) {
        logger.debug(String.format("getGroupListByAccountId begin with params: %s", request));
        DefaultResponse<List<Map<String, Object>>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> admGroupListResult = callAdminWs("admGroupListByAccount", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admGroupListResult, STATUS))) {
                response = ResponseFactory.createResponseListMapByResultMap(admGroupListResult);
                response.setSessionId(getStringParam(admGroupListResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(admGroupListResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса получения списка получения списка групп по идентификатору аккаунта.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method getGroupListByAccountId: ", ex);
            error = "Ошибка вызова сервиса получения списка групп пользователя по идентификатору аккаунта.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("getGroupListByAccountId end with result: %s", response));
        return response;
    }

    /**
     * Метод получения списка пользователей в группе
     *
     * @param request параметры запроса в json формате. Подробнее: {@link ListUsersInGroupParams}
     * @return параметры ответа в json формате. Подробнее: список карт
     */
    @POST
    @Path("/getListUsersInGroup")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<Map<String, Object>>> getListUsersInGroup(DefaultRequest<ListUsersInGroupParams> request) {
        logger.debug(String.format("getListUsersInGroup begin with params: %s", request));
        DefaultResponse<List<Map<String, Object>>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> admGroupUsersResult = callAdminWs("admGroupUsers", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admGroupUsersResult, STATUS))) {
                response.setSessionId(getStringParam(admGroupUsersResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(admGroupUsersResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса получения списка пользователей в группе.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method getListUsersInGroup: ", ex);
            error = "Ошибка вызова сервиса получения списка пользователей в группе.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("getListUsersInGroup end with result: %s", response));
        return response;
    }

    /**
     * Метод по добавлению пользователя в группу.
     *
     * @param request параметры запроса в json формате. Подробнее: {@link GroupUserAddParams}
     * @return параметры ответа в json формате. Подробнее: {@link GroupUserAddResult}
     */
    @POST
    @AuditBean
    @Path("/groupUserAdd")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<GroupUserAddResult> groupUserAdd(DefaultRequest<GroupUserAddParams> request,
                                                            @Context HttpHeaders httpHeaders,
                                                            @Context Audit audit) {
        DefaultResponse<GroupUserAddResult> response = ResponseFactory.createEmptyGenericResponse();

        final AuditParameters auditParameters = new AuditParameters();
        String error = "";
        final ResultOperation resultOperation;
        final StringBuilder auditMessageBuilder = new StringBuilder();
        auditParameters.setOperation("groupuseradd");
        final GroupUserAddParams params = request.getParams();

        try {
            if (error.isEmpty()) {
                Map<String, Object> admGroupUserAddResult = callAdminWsForAudit("admGroupUserAdd", request, httpHeaders, auditParameters);
                if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admGroupUserAddResult, STATUS))) {
                    response.setSessionId(getStringParam(admGroupUserAddResult, SESSION_ID_PARAMNAME));

                    resultOperation = ResultOperation.SUCCESE;
                    auditParameters.setResultStatus(resultOperation);
                    auditMessageBuilder.append("Group for user added.");

                } else {
                    error = getStringParam(admGroupUserAddResult, ADMIN_CALL_ERROR);
                    if (error.isEmpty()) {
                        error = "Ошибка вызова системного сервиса добавления пользователя в группу.";
                    }
                    auditMessageBuilder.append(String.format("Audit: error call system service for add user in group. Call error service: %s", error));
                }
            }
        } catch (Exception ex) {
            auditMessageBuilder.append(String.format("An error occurred in method groupUserAdd: %s", ex));
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }

        auditParameters.setMessage(auditMessageBuilder.toString());

        GroupUserAddParams groupUserAddParams = request.getParams();
        Map<String, Object> requestMap = ConvertUtils.convertObjectToMapWithoutNull(groupUserAddParams);
        requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admgroupbyid", groupUserAddParams.getUserGroupId(),
                "USERGROUPID", "GROUPNAME"));

        Map<String, Object> responseMap = ConvertUtils.convertObjectToMapWithoutNull(response);

        audit.audit(auditParameters, requestMap, responseMap, (Class<Obfuscator>[]) null, null);

        return response;
    }

    /**
     * Метод по удалению пользователя из группы.
     *
     * @param request параметры запроса в json формате. Подробнее: {@link GroupUserRemoveParams}
     * @return параметры ответа в json формате. Подробнее: {@link GroupUserRemoveResult}
     */
    @POST
    @AuditBean
    @Path("/groupUserRemove")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<GroupUserRemoveResult> groupUserRemove(DefaultRequest<GroupUserRemoveParams> request,
                                                                  @Context HttpHeaders httpHeaders,
                                                                  @Context Audit audit) {
        DefaultResponse<GroupUserRemoveResult> response = ResponseFactory.createEmptyGenericResponse();

        final AuditParameters auditParameters = new AuditParameters();
        String error = "";
        final ResultOperation resultOperation;
        final StringBuilder auditMessageBuilder = new StringBuilder();
        auditParameters.setOperation("groupuserremove");
        final GroupUserRemoveParams params = request.getParams();

        try {
            Map<String, Object> admGroupUserRemoveResult = callAdminWsForAudit("admGroupUserRemove", request, httpHeaders, auditParameters);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admGroupUserRemoveResult, STATUS))) {
                response.setSessionId(getStringParam(admGroupUserRemoveResult, SESSION_ID_PARAMNAME));

                resultOperation = ResultOperation.SUCCESE;
                auditParameters.setResultStatus(resultOperation);
                auditMessageBuilder.append("Group for user removed.");

            } else {
                error = getStringParam(admGroupUserRemoveResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса удаления пользователя из группы.";
                }
                auditMessageBuilder.append(String.format("Audit: error call system service for deleted user from group. Call error service: %s", error));
            }
        } catch (Exception ex) {
            auditMessageBuilder.append(String.format("An error occurred in method groupUserRemove: %s", ex));
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }

        auditParameters.setMessage(auditMessageBuilder.toString());

        GroupUserRemoveParams groupUserRemoveParams = request.getParams();
        Map<String, Object> requestMap = ConvertUtils.convertObjectToMapWithoutNull(groupUserRemoveParams);
        requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admgroupbyid", groupUserRemoveParams.getGroupId(),
                "USERGROUPID", "GROUPNAME"));

        Map<String, Object> responseMap = ConvertUtils.convertObjectToMapWithoutNull(response);

        audit.audit(auditParameters, requestMap, responseMap, (Class<Obfuscator>[]) null, null);

        return response;
    }

    /**
     * Метод получения списка доступных ролей по идентификатору аккаунта
     *
     * @param request параметры запроса в json формате. Подробнее: {@link RoleAvailableListByAccountIdParams}
     * @return параметры ответа в json формате. Подробнее: список карт
     */
    @POST
    @Path("/getRoleAvailableListByAccountId")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<Map<String, Object>>> getRoleAvailableListByAccountId(DefaultRequest<RoleAvailableListByAccountIdParams> request) {
        logger.debug(String.format("getRoleAvailableListByAccountId begin with params: %s", request));
        DefaultResponse<List<Map<String, Object>>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> admAccountAvailableRoleListResult = callAdminWs("admAccountAvailableRoleList", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admAccountAvailableRoleListResult, STATUS))) {
                response = ResponseFactory.createResponseListMapByResultMap(admAccountAvailableRoleListResult);
                response.setSessionId(getStringParam(admAccountAvailableRoleListResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(admAccountAvailableRoleListResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса получения списка доступных ролей по идентификатору аккаунта.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method getRoleAvailableListByAccountId: ", ex);
            error = "Ошибка вызова сервиса получения списка доступных ролей по идентификатору аккаунта.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("getRoleAvailableListByAccountId end with result: %s", response));
        return response;
    }


    /**
     * Удаляет все назначенные пользователю права по ключу (USERACCOUNTID)
     *
     * @param request параметры запроса в json формате. Подробнее: {@link UserRemoveAllRightsByUserAccountIdParams}
     * @return параметры ответа в json формате. Подробнее: {@link UserRemoveAllRightsByUserAccountIdResult}
     */
    @POST
    @Path("/userRemoveAllRightsByAccountId")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<UserRemoveAllRightsByUserAccountIdResult> userRemoveAllRightsByAccountId(DefaultRequest<UserRemoveAllRightsByUserAccountIdParams> request) {
        logger.debug(String.format("userRemoveAllRights begin with params: %s", request));
        DefaultResponse<UserRemoveAllRightsByUserAccountIdResult> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> userAllRemoveRightsResult = callAdminWs("dsUserRemoveAllRights", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(userAllRemoveRightsResult, STATUS))) {
                response.setSessionId(getStringParam(userAllRemoveRightsResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(userAllRemoveRightsResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса удаления всех назначенных прав пользователю.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method getRoleAvailableListByAccountId: ", ex);
            error = "Ошибка вызова сервиса удаления всех назначенных прав пользователю.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("getRoleAvailableListByAccountId end with result: %s", response));
        return response;
    }

    /**
     * Сервис изменения настроек пароля.
     * Сервис возвращает либо OK, в случае успешного сохранения настроек,
     * либо ERROR и сообщения об ошибке
     *
     * @param request параметры запроса в json формате. Подробнее: {@link SaveSettingPasswordParams}
     * @return параметры ответа в json формате. Подробнее: {@link BaseResponse}
     */
    @POST
    @AuditBean
    @Path("/saveSettingPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse saveSettingPassword(DefaultRequest<SaveSettingPasswordParams> request, @Context HttpHeaders headers, @Context Audit audit) {

        final AuditParameters auditParameters = new AuditParameters();
        final ResultOperation resultOperation;
        final StringBuilder auditMessageBuilder = new StringBuilder();
        auditParameters.setOperation("savesettingpassword");
        String error = "";

        logger.debug(String.format("saveSettingPassword begin with params: %s", request));

        BaseResponse response = ResponseFactory.createEmptyResponse();

        try {
            Map<String, Object> admPasswordSettingSaveResult = callAdminWs("savePwdAcctSettings", request);
            Map<String, Object> corePassSettingUpdateResult = callCoreWs("updatepasswordsettings", request);
            if (!RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admPasswordSettingSaveResult, STATUS))
                    && !RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(corePassSettingUpdateResult, STATUS))) {
                error = getStringParam(admPasswordSettingSaveResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = getStringParam(corePassSettingUpdateResult, ADMIN_CALL_ERROR);
                    if (error.isEmpty()) {
                        error = "Ошибка вызова системного сервиса изменения настроек пароля.";
                    }
                    auditMessageBuilder.append(error);
                }
            } else {
                final String sessionId = getStringParam(admPasswordSettingSaveResult, SESSION_ID_PARAMNAME);
                response.setSessionId(sessionId);

                final String login = getStringParam(admPasswordSettingSaveResult, SYSTEM_PARAM_NAME_LOGIN);
                auditParameters.setLogin(login);

                resultOperation = ResultOperation.SUCCESE;
                auditParameters.setResultStatus(resultOperation);
                auditMessageBuilder.append("User settings was update.");
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method saveSettingPassword: ", ex);
            error = "Ошибка вызова сервиса изменения настроек пароля.";
            auditMessageBuilder.append(error);
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorResponse(error);
        }

        auditParameters.setMessage(auditMessageBuilder.toString());

        Map<String, Object> requestMap = ConvertUtils.convertObjectToMapWithoutNull(request.getParams());
//        SaveSettingPasswordParams requestParam = request.getParams();
//        if (requestParam..getUserAccountId() != null) {
//            final Long userAccountId = requestParam.getUserAccountId();
//            requestMap.putAll(dereferenceRequest.dereferenceByObjectId(ADMINWS, "admCurrentUserInfo", userAccountId,
//                    USER_ACCOUNT_ID_PARAM_NAME, "EMPLOYEENAME", "USERLOGIN"));
//        }

        Map<String, Object> responseMap = ConvertUtils.convertObjectToMapWithoutNull(response);

        audit.audit(auditParameters, requestMap, responseMap, (Class<Obfuscator>[]) null, null);

        logger.debug(String.format("saveSettingPassword end with result: %s", response));
        return response;
    }

    /**
     * Сервис получения настроек пароля.
     * Сервис принимает только сессию и пустой объект параметров
     *
     * @param request параметры запроса в json формате. Подробнее: {@link DefaultRequest}
     * @return параметры ответа в json формате. Подробнее: {@link ReadPasswordSetting}
     */
    @POST
    @Path("/readSettingPassword")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<ReadPasswordSetting> readSettingPassword(DefaultRequest request) {
        logger.debug(String.format("readSettingPassword begin with params: %s", request));
        DefaultResponse<ReadPasswordSetting> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> admPasswordSettingGetResult = callAdminWs("getPwdAcctSettings", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admPasswordSettingGetResult, STATUS))) {
                response = ResponseFactory.createResponseByMap(admPasswordSettingGetResult, ReadPasswordSetting.class);
                response.setSessionId(getStringParam(admPasswordSettingGetResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(admPasswordSettingGetResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса получения настроек пароля.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method readSettingPassword: ", ex);
            error = "Ошибка вызова сервиса получения настроек пароля.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("readSettingPassword end with result: %s", response));
        return response;
    }

    /**
     * Сервис получения настроек автоимпорта файлов.
     * Сервис принимает только сессию и пустой объект параметров
     *
     * @param request параметры запроса в json формате. Подробнее: {@link DefaultRequest}
     * @return параметры ответа в json формате. Подробнее: {@link ReadImportSettingsResponse}
     */
    @POST
    @Path("/readAutoImportSetting")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<ReadImportSettingsResponse> readAutoImportSetting(DefaultRequest request) {
        logger.debug(String.format("readAutoImportSetting begin with params: %s", request));
        DefaultResponse<ReadImportSettingsResponse> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        String[] sysNames = new String[]{"KMSB_AUTOIMPORT_WORKTIME", "KMSB_COMMON_FOLDER", "KMSB_IMPORTRESULT_EMAIL"};
        try {
            Map<String, Object> settings = new HashMap<>();
            for (String sysName : sysNames) {
                DefaultRequest<Map<String, Object>> settingRequest = new DefaultRequest<>();
                settingRequest.setSessionId(request.getSessionId());
                Map<String, Object> params = new HashMap<>();
                params.put("SETTINGSYSNAME", sysName);
                params.put(RETURN_AS_HASH_MAP, "TRUE");
                settingRequest.setParams(params);
                Map<String, Object> settingResult = callCoreWs("getSysSettingBySysName", settingRequest);
                settings.put(sysName, settingResult.get("SETTINGVALUE"));
            }
            response = ResponseFactory.createResponseByMap(settings, ReadImportSettingsResponse.class);
        } catch (Exception ex) {
            logger.error("An error occurred in method readAutoImportSetting: ", ex);
            error = "Ошибка вызова сервиса получения настроек импорта.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("readAutoImportSetting end with result: %s", response));
        return response;
    }

    /**
     * Сервис изменения настроек автоимпорта файлов.
     * Сервис возвращает либо OK, в случае успешного сохранения настроек,
     * либо ERROR и сообщения об ошибке
     *
     * @param request параметры запроса в json формате. Подробнее: {@link SaveImportSettingsResponse}
     * @return параметры ответа в json формате. Подробнее: {@link BaseResponse}
     */
    @POST
    @AuditBean
    @Path("/saveAutoImportSettings")
    @Produces(MediaType.APPLICATION_JSON)
    public BaseResponse saveAutoImportSettings(DefaultRequest<SaveImportSettingsResponse> request, @Context HttpHeaders headers, @Context Audit audit) {
        final AuditParameters auditParameters = new AuditParameters();
        final ResultOperation resultOperation;
        final StringBuilder auditMessageBuilder = new StringBuilder();
        auditParameters.setOperation("savesettingautoimport");
        String error = "";
        logger.debug(String.format("saveSettingAutoImport begin with params: %s", request));
        BaseResponse response = ResponseFactory.createEmptyResponse();
        try {
            Map<String, Object> admAutoImportSettingSaveResult = callAdminWs("saveimportacctsettings", request);
            if (!RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admAutoImportSettingSaveResult, STATUS))) {
                error = getStringParam(admAutoImportSettingSaveResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса изменения настроек автоимпорта.";
                    auditMessageBuilder.append(error);
                }
            } else {
                final String sessionId = getStringParam(admAutoImportSettingSaveResult, SESSION_ID_PARAMNAME);
                response.setSessionId(sessionId);

                final String login = getStringParam(admAutoImportSettingSaveResult, SYSTEM_PARAM_NAME_LOGIN);
                auditParameters.setLogin(login);
                resultOperation = ResultOperation.SUCCESE;
                auditParameters.setResultStatus(resultOperation);
                auditMessageBuilder.append("User settings was update.");
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method saveAutoImportSettings: ", ex);
            error = "Ошибка вызова сервиса изменения настроек автоимпорта.";
            auditMessageBuilder.append(error);
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorResponse(error);
        }
        auditParameters.setMessage(auditMessageBuilder.toString());
        Map<String, Object> requestMap = ConvertUtils.convertObjectToMapWithoutNull(request.getParams());
        Map<String, Object> responseMap = ConvertUtils.convertObjectToMapWithoutNull(response);
        audit.audit(auditParameters, requestMap, responseMap, (Class<Obfuscator>[]) null, null);
        logger.debug(String.format("saveSettingPassword end with result: %s", response));
        return response;
    }

    /**
     * Сервис получения списка всех ролей отсутствующих у пользователя
     *
     * @param request параметры запроса в json формате. Подробнее: {@link RoleListAbsentFromUserParams}
     * @return параметры ответа в json формате. Подробнее: возвращает список,
     * который содержит в себе записи следующего вида
     * <UL>
     * <LI>ROLEID - идентификатор роли</LI>
     * <LI>ROLENAME - имя роли</LI>,
     * <LI>ROLESYSNAME - описани роли</LI>
     * </UL>
     */
    @POST
    @Path("/roleListAbsentFromUser")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<Map<String, Object>>> roleListAbsentFromUser(DefaultRequest<RoleListAbsentFromUserParams> request) {
        logger.debug(String.format("roleListAbsentFromUser begin with params: %s", request));
        DefaultResponse<List<Map<String, Object>>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> lkpUserRoleResult = callAdminWs("lkpUserRole", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(lkpUserRoleResult, STATUS))) {
                response = ResponseFactory.createResponseListMapByResultMap(lkpUserRoleResult);
                response.setSessionId(getStringParam(lkpUserRoleResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(lkpUserRoleResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса получения списка всех ролей.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method roleListAbsentFromUser: ", ex);
            error = "Ошибка вызова сервиса получения списка всех ролей.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("roleListAbsentFromUser end with result: %s", response));
        return response;
    }

    /**
     * Сервис получения списка в виде дерева всех групп пользователей
     * Сервис принимает только сессию и пустой объект параметров
     *
     * @param request параметры запроса в json формате. Подробнее: {@link DefaultRequest}
     * @return параметры ответа в json формате. Подробнее: возвращает список,
     * который содержит в себе записи следующего вида
     * <UL>
     * <LI>Description - описание
     * <LI>PARENTID - идентификатор родительской записи</LI>,
     * <LI>ID - идентификатор группы</LI>
     * <LI>NODEID - идентификатор узла дерева</LI>
     * <LI>NAME - имя группы</LI>
     * <LI>Name - имя группы</LI>
     * <LI>Children - список дочерних групп</LI>
     * </UL>
     */
    @POST
    @Path("/userGroupsTree")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<Map<String, Object>>> userGroupsTree(DefaultRequest request) {
        logger.debug(String.format("userGroupsTree begin with params: %s", request));
        DefaultResponse<List<Map<String, Object>>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> admUserGroupsResult = callAdminWs("admUserGroups", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admUserGroupsResult, STATUS))) {
                response = ResponseFactory.createResponseListMapByKeyName(admUserGroupsResult, "usergroupsTree");
                response.setSessionId(getStringParam(admUserGroupsResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(admUserGroupsResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса получения списка в виде дерева всех групп.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method userGroupsTree: ", ex);
            error = "Ошибка вызова сервиса получения списка в виде дерева всех групп.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("userGroupsTree end with result: %s", response));
        return response;
    }

    /**
     * Сервис получения списка в виде дерева всех подразделений
     *
     * @param request параметры запроса в json формате. Подробнее: {@link DepartmentIdParams}
     * @return параметры ответа в json формате. Подробнее: возвращает список,
     * который содержит в себе записи следующего вида
     * <UL>
     * <LI>Description - описание
     * <LI>PARENTID - идентификатор родительской записи</LI>,
     * <LI>ID - идентификатор департамента</LI>
     * <LI>NODEID - идентификатор узла дерева</LI>
     * <LI>NAME - имя департамента</LI>
     * <LI>Name - имя департамента</LI>
     * <LI>Children - список дочерних подразделений</LI>
     * </UL>
     */
    @POST
    @Path("/departmentTree")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<Map<String, Object>>> departmentTree(DefaultRequest<DepartmentIdParams> request) {
        logger.debug(String.format("departmentTree begin with params: %s", request));
        DefaultResponse<List<Map<String, Object>>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> depStructureResult = callAdminWs("depStructure", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(depStructureResult, STATUS))) {
                response = ResponseFactory.createResponseListMapByKeyName(depStructureResult, "depTree");
                response.setSessionId(getStringParam(depStructureResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(depStructureResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса получения списка в виде дерева всех департаментов.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method departmentTree: ", ex);
            error = "Ошибка вызова сервиса получения списка в виде дерева всех департаментов.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("departmentTree end with result: %s", response));
        return response;
    }

    /**
     * Функция обновления статуса аккаунта
     *
     * @param request параметры запроса в json формате. Подробнее: {@link AccountUpdateStatusParams}
     * @return - возвращает статус выполнения обновления статуса
     */
    @POST
    @AuditBean
    @Path("/accountUpdateStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<Map<String, Object>>> accountUpdateStatus(DefaultRequest<AccountUpdateStatusParams> request,
                                                                          @Context HttpHeaders httpHeaders,
                                                                          @Context Audit audit) {
        DefaultResponse<List<Map<String, Object>>> response = ResponseFactory.createEmptyGenericResponse();

        final String methodName = "dsAccountUpdateStatus";
        final String auditOperation = "accountupdatestatus";
        final AuditParameters auditParameters = new AuditParameters();
        String error = "";
        ResultOperation resultOperation;
        StringBuilder auditMessageBuilder = new StringBuilder();
        auditParameters.setOperation(auditOperation);

        try {
            Map<String, Object> accountUpdateStatusResult = callAdminWsForAudit(methodName, request, httpHeaders, auditParameters);
            if (!RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(accountUpdateStatusResult, STATUS))) {
                error = getStringParam(accountUpdateStatusResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса обновления статуса аккаунта.";
                }
                auditMessageBuilder.append(String.format("Audit: error call system service for update account status. Call error service: %s", error));
            } else {
                response.setSessionId(getStringParam(accountUpdateStatusResult, SESSION_ID_PARAMNAME));

                resultOperation = ResultOperation.SUCCESE;
                auditParameters.setResultStatus(resultOperation);
                auditMessageBuilder.append("User account was update.");
            }
        } catch (Exception ex) {
            auditMessageBuilder.append(String.format("An error occurred in method accountUpdateStatus: %s", ex));
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }

        auditParameters.setMessage(auditMessageBuilder.toString());

        Map<String, Object> requestMap = ConvertUtils.convertObjectToMapWithoutNull(request.getParams());
        Map<String, Object> responseMap = ConvertUtils.convertObjectToMapWithoutNull(response);

        audit.audit(auditParameters, requestMap, responseMap, (Class<Obfuscator>[]) null, null);

        return response;
    }

    /**
     * Сервис смены пароля аккаунта пользователя
     *
     * @param request параметры запроса в json формате. Подробнее: {@link AccountUpdateStatusParams}
     * @return - возвращает статус выполнения смены пароля
     */
    @POST
    @AuditBean
    @Path("/dsAdminUserChangePass")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<String> adminUserChangePassword(DefaultRequest<AdminUserChangePasswordParams> request,
                                                           @Context HttpHeaders httpHeaders,
                                                           @Context Audit audit) {
        DefaultResponse<String> response = ResponseFactory.createEmptyGenericResponse();

        final AuditParameters auditParameters = new AuditParameters();
        String error = "";
        final ResultOperation resultOperation;
        final StringBuilder auditMessageBuilder = new StringBuilder();
        auditParameters.setOperation("userchangepass");

        try {
            Map<String, Object> accountChangePassResult = callAdminWsForAudit("admAccountChangePass", request, httpHeaders, auditParameters);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(accountChangePassResult, STATUS))) {
                response.setResult(getStringParam(accountChangePassResult, RESULT));
                response.setSessionId(getStringParam(accountChangePassResult, SESSION_ID_PARAMNAME));

                resultOperation = ResultOperation.SUCCESE;
                auditParameters.setResultStatus(resultOperation);
                auditMessageBuilder.append("User password changed.");

            } else {
                error = getStringParam(accountChangePassResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса смены пароля аккаунта.";
                }
                auditMessageBuilder.append(String.format("Audit: error call system service for change account password. Call error service: %s", error));
            }
        } catch (Exception ex) {
            auditMessageBuilder.append(String.format("An error occurred in method adminUserChangePassword: %s", ex));
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }

//        Map<String, Object> currentUserResult = getChangeableUserInfo(request);
//        if (!currentUserResult.isEmpty()) {
//            addUserInfoInAuditMessage(currentUserResult, auditMessageBuilder);
//        }

        auditParameters.setMessage(auditMessageBuilder.toString());

        Map<String, Object> requestMap = ConvertUtils.convertObjectToMapWithoutNull(request.getParams());
        requestMap.remove("NEWPASS"); ////delete password from request

        Map<String, Object> responseMap = ConvertUtils.convertObjectToMapWithoutNull(response);

        audit.audit(auditParameters, requestMap, responseMap, (Class<Obfuscator>[]) null, null);

        return response;
    }

    /**
     * Сервис загрузки роли по идентификатору
     *
     * @param request параметры запроса в json формате. Подробнее: {@link LoadRoleByIdParam}
     * @return параметры ответа в json формате. Подробнее: {@link LoadRoleResult}
     */
    @POST
    @Path("/loadRoleById")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<LoadRoleResult> loadRoleById(DefaultRequest<LoadRoleByIdParam> request) {
        logger.debug(String.format("loadRoleById begin with params: %s", request));
        DefaultResponse<LoadRoleResult> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> loadUserRoleById = callAdminWs("admUserRoleByID", request);
            if (loadUserRoleById != null && loadUserRoleById.get("ROLEID") != null) {
                response = ResponseFactory.createResponseByMap(loadUserRoleById, LoadRoleResult.class);
                response.setSessionId(getStringParam(loadUserRoleById, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(loadUserRoleById, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса загрузки роли по идентификатору.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method loadRoleById: ", ex);
            error = "Ошибка вызова сервиса загрузки роли по идентификатору.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("loadRoleById end with result: %s", response));
        return response;
    }

    /**
     * Сервис обновления или создания роли. Если идентификатор роли отсутствует,
     * тогда считаем что это создание роли
     *
     * @param request параметры запроса в json формате. Подробнее: {@link SaveRoleParams}
     * @return параметры ответа в json формате. Подробнее: {@link SaveRoleResult}
     */
    @POST
    @Path("/saveRole")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<SaveRoleResult> saveRole(DefaultRequest<SaveRoleParams> request) {
        logger.debug(String.format("saveRole begin with params: %s", request));
        DefaultResponse<SaveRoleResult> response = ResponseFactory.createEmptyGenericResponse();
        String error;
        try {
            SaveRoleParams params = request.getParams();
            if (params.getRoleId() == null) {
                error = createRole(request, response);
            } else {
                error = updateRole(request, response);
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method saveRole: ", ex);
            error = "Ошибка вызова сервиса сохранения роли.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("loadRoleById end with result: %s", response));
        return response;
    }

    /**
     * Метод создания роли
     *
     * @param request  параметры запроса в json формате. Подробнее: {@link SaveRoleParams}
     * @param response параметры ответа в json формате. Подробнее: {@link SaveRoleResult}
     * @return ошибка
     */
    private String createRole(DefaultRequest<SaveRoleParams> request, DefaultResponse<SaveRoleResult> response) {
        String error = "";
        SaveRoleParams params = request.getParams();
        Map<String, Object> admUserRoleAdd = callAdminWs("admUserRoleAdd", request);
        if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admUserRoleAdd, STATUS))) {
            Map<String, Object> result = getMapParamName(admUserRoleAdd, RESULT);
            if (result.get("ROLEID") == null) {
                error = String.format("Роль с указанным системным именем (%s) уже существует.", params.getRoleSystemName());
            } else {
                response.setResult(createClassByMap(result, SaveRoleResult.class));
                response.setSessionId(getStringParam(admUserRoleAdd, SESSION_ID_PARAMNAME));
            }
        } else {
            error = getStringParam(admUserRoleAdd, ADMIN_CALL_ERROR);
            if (error.isEmpty()) {
                error = "Ошибка вызова системного сервиса создания роли.";
            }
        }
        return error;
    }

    /**
     * Метод обновления роли
     *
     * @param request  параметры запроса в json формате. Подробнее: {@link SaveRoleParams}
     * @param response параметры ответа в json формате. Подробнее: {@link SaveRoleResult}
     * @return ошибка
     */
    private String updateRole(DefaultRequest<SaveRoleParams> request, DefaultResponse<SaveRoleResult> response) {
        String error = "";
        Map<String, Object> admUserRoleUpdate = callAdminWs("admUserRoleUpdate", request);
        if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(admUserRoleUpdate, STATUS))) {
            response.setResult(new SaveRoleResult(getLongParam(admUserRoleUpdate, RESULT)));
            response.setSessionId(getStringParam(admUserRoleUpdate, SESSION_ID_PARAMNAME));
        } else {
            error = getStringParam(admUserRoleUpdate, ADMIN_CALL_ERROR);
            if (error.isEmpty()) {
                error = "Ошибка вызова системного сервиса обновления роли.";
            }
        }
        return error;
    }

    /**
     * Сервис удаления роли по идентификатору
     *
     * @param request параметры запроса в json формате. Подробнее: {@link DeleteRoleByIdParam}
     * @return - возвращает статус выполнения удаления роли
     */
    @POST
    @Path("/deleteRoleById")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<String> deleteRoleById(DefaultRequest<DeleteRoleByIdParam> request) {
        logger.debug(String.format("deleteRoleById begin with params: %s", request));
        DefaultResponse<String> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> deleteRoleResult = callAdminWs("admUserRoleRemove", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(deleteRoleResult, STATUS))) {
                Map<String, Object> result = getMapParamName(deleteRoleResult, RESULT);
                response.setResult(getStringParam(result, RESULT));
                response.setSessionId(getStringParam(deleteRoleResult, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(deleteRoleResult, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса удаления роли.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method admUserRoleRemove: ", ex);
            error = "Ошибка вызова сервиса удаления роли.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("deleteRoleById end with result: %s", response));
        return response;
    }

    /**
     * Метод загрузки типов пользователей
     *
     * @param request параметры запроса в json формате. Подробнее: {@link UserTypeParams}
     * @return параметры ответа список с json'ами. Формат json подробнее: {@link UserTypeResult}
     */
    @POST
    @Path("/loadUserTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public DefaultResponse<List<UserTypeResult>> loadUserTypes(DefaultRequest<UserTypeParams> request) {
        logger.debug(String.format("loadUserTypes begin with params: %s", request));
        DefaultResponse<List<UserTypeResult>> response = ResponseFactory.createEmptyGenericResponse();
        String error = "";
        try {
            Map<String, Object> refItemGetListByParams = callAdminWs("refItemGetListByParams", request);
            if (RESULT_STATUS_OK.equalsIgnoreCase(getStringParam(refItemGetListByParams, STATUS))) {
                response = ResponseFactory.createResponseListClassByResultMap(refItemGetListByParams, UserTypeResult.class);
                List<UserTypeResult> resultList = response.getResult().stream().filter(it -> !SKIPPED_USER_TYPE.contains(it.getSystemName())).collect(Collectors.toList());
                response.setResult(resultList);
                response.setSessionId(getStringParam(refItemGetListByParams, SESSION_ID_PARAMNAME));
            } else {
                error = getStringParam(refItemGetListByParams, ADMIN_CALL_ERROR);
                if (error.isEmpty()) {
                    error = "Ошибка вызова системного сервиса загрузки списка типов пользователей.";
                }
            }
        } catch (Exception ex) {
            logger.error("An error occurred in method loadUserTypes: ", ex);
            error = "Ошибка вызова сервиса загрузки списка типов пользователей.";
        }

        if (!error.isEmpty()) {
            response = ResponseFactory.createErrorGenericResponse(error);
        }
        logger.debug(String.format("loadUserTypes end with result: %s", response));
        return response;
    }
}
