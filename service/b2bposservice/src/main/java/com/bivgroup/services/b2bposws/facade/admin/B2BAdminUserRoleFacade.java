package com.bivgroup.services.b2bposws.facade.admin;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Фасад для работы с ролями пользователей
 *
 * @author Alex Ivashin
 */
@BOName("B2BAdminUserRole")
public class B2BAdminUserRoleFacade extends B2BBaseFacade {


    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminGetUserRoleList(Map<String, Object> params)
            throws Exception {
        // возможно, здесь не потребуется, когда преобразование дат 
        // во входных параметрах будет перенесено в BoxPropertyGate
        parseDates(params, Double.class);

        Map<String, Object> customParams = params;

        Map<String, Object> result = null;

        // добавляем ограничение по агентам только если пользователь не "Сотрудник страховой" и не "Робот"
        Object userTypeId = params.get(Constants.SESSIONPARAM_USERTYPEID);
        if ((userTypeId == null) || ((Long.parseLong(userTypeId.toString()) != 1)
                && ((Long.parseLong(userTypeId.toString())) != 4))) {
            customParams.put("CP_DEPARTMENTID", params.get(Constants.SESSIONPARAM_DEPARTMENTID));
        }
        XMLUtil.convertDateToFloat(customParams);
        // Ограничение по орг структуре.
        if (null == params.get("ROOTID")) {
            customParams.put("DEPRIGHT", params.get("SESSION_DEPARTMENTID"));
            if (null != params.get("USE_SESSION_DEPARTMENTID")) {
                if (params.get("USE_SESSION_DEPARTMENTID").equals(true)) {
                    customParams.put("DEPRIGHT", params.get("SESSION_DEPARTMENTID"));
                }
            }
        }

        result = this.selectQuery(
                "dsGetRoleListByUserAndUserAccountId",
                "dsGetRoleListByUserAndUserAccountIdCount",
                customParams
        );
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminRoleUserAdd(Map<String, Object> params)
            throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> roleUserAddResult = this.callService(
                Constants.ADMINWS, "admRoleUserAdd", params, login, password
        );

        Map<String, Object> result = new HashMap<String, Object>();
        if (roleUserAddResult.get("Status").toString().equalsIgnoreCase("ERROR")) {
            result.put("StatusType", "ERROR");
            result.put("Error", "Попытка добавить существующую роль.");
        } else {
            result.put(RESULT, roleUserAddResult);
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminRoleUserRemove(Map<String, Object> params)
            throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> roleUserAddResult = this.callService(
                Constants.ADMINWS, "admRoleUserRemove", params, login, password
        );

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, roleUserAddResult);

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdmRoleListByAccount(Map<String, Object> params)
            throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> roleList = this.callService(
                Constants.ADMINWS, "admRoleListByAccount", params, login, password
        );

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, roleList);

        return result;
    }

}
