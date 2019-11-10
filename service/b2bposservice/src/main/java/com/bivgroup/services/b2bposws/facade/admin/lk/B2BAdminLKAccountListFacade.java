package com.bivgroup.services.b2bposws.facade.admin.lk;

import com.bivgroup.services.b2bposws.facade.B2BPosServiceSessionController;
import com.bivgroup.sessionutils.SessionController;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;

/**
 * Фасад для работы с договорами СБСЖ ЛК
 */
@BOName("B2BAdminLKAccountList")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class B2BAdminLKAccountListFacade extends B2BAdminLKBaseFacade {

    //    private static final String ENCRYPTION_PASSWORD = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
//    private static final byte[] ENCRYPTION_SALT = {
//            (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
//            (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};
    private static final String SESSION_STR_DIVIDER = "__div__";
    //    private static final StringCryptUtils scu = new StringCryptUtils(ENCRYPTION_PASSWORD, ENCRYPTION_SALT);
    public static final String CONTRACT_ID_PARAMNAME = "contractId";
    private static final List<String> ROLE_LIST = Arrays.asList(
            "adminOperatorLK",
            "adminOperatorKC",
            "adminOperatorUKS"
    );
    private static final String CLIENT_PROFILE_DISCRIMINATOR_WEB = "web";

    /**
     * Функция для получения списка сообщений
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminLkBrowseAccountListByParams(Map<String, Object> params) throws Exception {
        Map<String, Object> result = selectQuery("dsAdminLkBrowseAccountListByParams", params);
        return result;
    }

    /**
     * Метод получения списка статусов для журнала учетных записей
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BAccountListGetStateList(Map<String, Object> params) throws Exception {
        Map<String, Object> queryParams = new HashMap<>();
        Map<String, Object> result = this.selectQuery("dsB2BAccountListGetStateList", queryParams);
        return result;
    }

    private String findSysnameGrandRole(List<Map<String, Object>> roleList) {
        /**
         * по идее юзер будет иметь только одну интересующую нас роль
         */
        for (Map<String, Object> role : roleList) {
            String roleSysname = getStringParam(role, "ROLESYSNAME");
            if (ROLE_LIST.contains(roleSysname)) {
                return roleSysname;
            }
        }
        return "emptyRole";
    }

    /**
     * Метод получения sessionId для lk по id кдиента
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"clientProfileId"})
    public Map<String, Object> dsB2BGetSessionIdUserLkById(Map<String, Object> params) throws Exception {
        logger.info("launch method dsB2BGetSessionIdUserLkById");

        Map<String, Object> result = new HashMap<String, Object>();

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Long clientProfileId = getLongParam(params, "clientProfileId");
        Long userAccountId = getLongParam(params, "SESSION_USERACCOUNTID");

        // ищем имя роли по USERACCOUNTID. если же она не найдена - возвращаем "emptyRole"
        Map<String, Object> roleParams = new HashMap<String, Object>();
        roleParams.put("USERACCOUNTID", userAccountId);
        List<Map<String, Object>> roleList = getListFromResultMap(this.callExternalService(B2BPOSWS_SERVICE_NAME, "dsUserRoleBrowseListByParam", roleParams, login, password));
        String roleSysname = findSysnameGrandRole(roleList);
        logger.info("launch generate session whith role: " + roleSysname);

        // тянем требуемую информацию о профиле
        Map<String, Object> clientProfileCallParams = new HashMap<>();
        clientProfileCallParams.put("clientProfileId", clientProfileId);
        clientProfileCallParams.put("discriminator", CLIENT_PROFILE_DISCRIMINATOR_WEB);
        List<Map<String, Object>> clientProfileWithTokenList = getListFromResultMap(this.selectQuery("dsB2BGetClientProfileWithTokenById", "dsB2BGetClientProfileWithTokenByIdCount", clientProfileCallParams));
        if (!clientProfileWithTokenList.isEmpty()) {
            // все ок - генерируем сессию
            Map<String, Object> clientProfileWithToken = clientProfileWithTokenList.get(0);
            String userPassword = getStringParam(clientProfileWithToken, "userPassword");
            String userLogin = getStringParam(clientProfileWithToken, "userLogin");
            //
            Map<String, Object> sessionParams = new HashMap<>();
            sessionParams.put(B2B2LKSessionController.PA2_BASE_PROFILE_ID_PARAMNAME, clientProfileId);
            sessionParams.put(B2B2LKSessionController.PA2_BASE_USERLOGIN_PARAMNAME, userLogin);
            sessionParams.put(B2B2LKSessionController.PA2_BASE_USERPASS_PARAMNAME, userPassword);
            sessionParams.put(B2B2LKSessionController.SESSION_USERROLE_PARAMNAME, roleSysname);
            SessionController controller = new B2B2LKSessionController();
            String sessionId = controller.createSession(sessionParams);
            result.put("sessionId", sessionId);
            result.put(RET_STATUS, RET_STATUS_OK);
        } else {
            // пасос - логаем что клиент не найден
            result.put(RET_STATUS, RET_STATUS_ERROR);
            String errorText = "not found profile with clientProfileId: " + clientProfileId;
            logger.error(errorText);
            result.put("ErrorText", errorText);
        }
        logger.info("finish method dsB2BGetSessionIdUserLkById");
        return result;
    }

    @WsMethod(requiredParams = {CONTRACT_ID_PARAMNAME, "SESSIONIDFORCALL"})
    public Map<String, Object> dsB2BGetSessionIdUserLkByContractId(Map<String, Object> params) {

        Map<String, Object> result = new HashMap<>();
        Long contractID = getLongParam(params, CONTRACT_ID_PARAMNAME);
        String eventLog = String.format("просмотр контракта [ID = %s]", contractID);
        ;
        String[] groupsToLog = {"SB1"};
        logToAuditForGroups(params, eventLog, groupsToLog);
        String b2bSessionId = (String) params.get("SESSIONIDFORCALL");
        SessionController b2bController = new B2BPosServiceSessionController();
        Map<String, Object> b2bSessionParams = b2bController.checkSession(b2bSessionId);

        if (b2bSessionParams.get(ERROR) != null) return null;

        String userPassword = (String) b2bSessionParams.get(B2BPosServiceSessionController.B2B_USERPASSWORD_PARAMNAME);
        String userLogin = (String) b2bSessionParams.get(B2BPosServiceSessionController.B2B_USERLOGIN_PARAMNAME);

        Map<String, Object> pa2SessionParams = new HashMap<>();
        pa2SessionParams.put(B2B2LKSessionController.PA2_BASE_USERLOGIN_PARAMNAME, userLogin);
        pa2SessionParams.put(B2B2LKSessionController.PA2_BASE_USERPASS_PARAMNAME, userPassword);
        pa2SessionParams.put(B2B2LKSessionController.PA2_BASE_PROFILE_ID_PARAMNAME, contractID);

        SessionController pa2Controller = new B2B2LKSessionController();
        String newSessionId = pa2Controller.createSession(pa2SessionParams);
        result.put("sessionId", newSessionId);

        return result;
    }
}
