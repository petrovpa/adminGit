package com.bivgroup.services.b2bposws.facade.admin;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

/**
 * Фасад для сущности ProfileRight
 *
 * @author reson
 */
@BOName("B2BProfileRight")

public class B2BProfileRightFacade extends B2BBaseFacade {

    /**
     * Получить список профильных прав
     *
     * @return
     * @author reson
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminGetProfileRightsList(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        ArrayList<Object> resultrows = new ArrayList<Object>();

        Map<String, Object> customParams = params;
        if (!getStringParam(customParams.get("DEPARTMENTID")).isEmpty()) {
            result = this.selectQuery("dsAdminGetDepProfileRightsList", "dsAdminGetDepProfileRightsListCount", customParams);
        }

        if (!getStringParam(customParams.get("USERACCOUNTID")).isEmpty()) {
            result = this.selectQuery("dsAdminGetUserProfileRightsList", "dsAdminGetUserProfileRightsListCount", customParams);
        }

        resultrows = (ArrayList<Object>) result.get(RESULT);

        for (Iterator iterator = resultrows.iterator(); iterator.hasNext(); ) {
            Map<String, Object> params_parameters = new HashMap<String, Object>();

            Map<String, String> resultrow = (Map<String, String>) iterator.next();
            Map<String, Object> paramRes = null;
            params_parameters.put("RIGHTID", resultrow.get("RIGHTID"));

            if (!getStringParam(customParams.get("DEPARTMENTID")).isEmpty()) {
                params_parameters.put("DEPARTMENTID", resultrow.get("OBJECTID"));
                paramRes = this.selectQuery("dsAdminDepParameterListOnRight", "dsAdminDepParameterListOnRightCount", params_parameters);
            }

            if (!getStringParam(customParams.get("USERACCOUNTID")).isEmpty()) {
                params_parameters.put("USERACCOUNTID", resultrow.get("OBJECTID"));
                paramRes = this.selectQuery("dsAdminUserParameterListOnRight", "dsAdminUserParameterListOnRightCount", params_parameters);
            }

            if ((null != paramRes) && (null != paramRes.get(RESULT)) && (!((List<Map<String, Object>>) paramRes.get(RESULT)).isEmpty())) {
                List<Map<String, Object>> parameters = (List<Map<String, Object>>) paramRes.get(RESULT);
                String pattern = "PARAMETER";
                for (int i = 0; i < parameters.size(); i++) {
                    Map<String, Object> param = parameters.get(i);
                    String num = String.valueOf(i + 1);
                    if (num.length() < 2) {
                        num = "0" + num;
                    }
                    if (!(getStringParam(param.get("OPERATION")).isEmpty() || getStringParam(param.get("VALUESTR")).isEmpty())) {
                        resultrow.put(pattern + num, getStringParam(param.get("PARAMSYSNAME")) + " " + getStringParam(param.get("OPERATION")) + " " + getStringParam(param.get("VALUESTR")));
                    }
                }

            }
        }
        return result;
    }

    /**
     * Получить список доступных прав для назначения
     *
     * @return
     * @author reson
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminGetAvailableRightsList(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> customParams = params;
        customParams.put("CURRENTDATE", new Date());
        XMLUtil.convertDateToFloat(customParams);

        if (!getStringParam(customParams.get("DEPARTMENTID")).isEmpty()) {
            result = this.selectQuery("dsAdminGetAvailableDepRightsList", "dsAdminGetAvailableDepRightsListCount", customParams);
        }
        if (!getStringParam(customParams.get("USERACCOUNTID")).isEmpty()) {
            result = this.selectQuery("dsAdminGetAvailableUserRightsList", "dsAdminGetAvailableUserRightsListCount", customParams);
        }

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminParameterListOnRight(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (!getStringParam(params.get("DEPARTMENTID")).isEmpty()) {
            result = this.selectQuery("dsAdminDepParameterListOnRight", "dsAdminDepParameterListOnRightCount", params);
        }
        if (!getStringParam(params.get("USERACCOUNTID")).isEmpty()) {
            result = this.selectQuery("dsAdminUserParameterListOnRight", "dsAdminUserParameterListOnRightCount", params);
        }
        return result;
    }

    /**
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminCreateProfileRight(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();

        parseDates(params, Double.class);

        result = this.callService(Constants.ADMINWS, "admRightAdd", params, login, password);
        return result;
    }

    /**
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminUpdateProfileRight(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();

        parseDates(params, Double.class);

        result = this.callService(Constants.ADMINWS, "admobjrightupdate", params, login, password);
        return result;
    }

    /**
     * Удаляем право (по двум внешним ключам RIGHTID и DEPARTMENTID).
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminDeleteProfileRight(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> checkResult = new HashMap<String, Object>();
        if (params.get("RIGHTID") == null) {
            checkResult.put("StatusType", "ERROR");
            checkResult.put("Error", "Не заполнен обязательный параметр ID.");
            result.put(RESULT, checkResult);
        } else {
            result = this.callService(Constants.ADMINWS, "admRightRemove", params, login, password);
        }

        return result;

    }

}
