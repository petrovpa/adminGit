/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import com.bivgroup.services.bivsberposws.system.Constants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averishevsm
 */
@BOName("InsSharesCustom")
public class InsSharesCustomFacade extends BaseFacade {

    private static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;

    private void clearEmptyValue(Map<String, Object> params, String paramName) {
        if ((params.get(paramName) != null) && params.get(paramName).toString().equalsIgnoreCase("")) {
            params.put(paramName, null);
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsInsSharesBrowseListByParamEx(Map<String, Object> params) throws Exception {
        clearEmptyValue(params, "PRODID");
        clearEmptyValue(params, "PRODVERID");
        if (params.get("PRODID") == null) {
            if (params.get("PRODVERID") != null) {
                params.remove("PRODVERID");
            }
        }
        clearEmptyValue(params, "WORKDATE");
        clearEmptyValue(params, "NAME");
        clearEmptyValue(params, "SYSNAME");
        clearEmptyValue(params, "TYPESYSNAME");
        Map<String, Object> result = this.selectQuery("dsInsSharesBrowseListByParamEx", "dsInsSharesBrowseListByParamExCount", params);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsInsSharesDeleteEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsPromocodesDeleteEx", params, login, password);
        this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsSharesDelete", params, login, password);
        return null;
    }

}
