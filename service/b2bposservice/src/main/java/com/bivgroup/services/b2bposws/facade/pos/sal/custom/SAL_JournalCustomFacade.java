/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.sal.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kkulkov
 */
@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("SAL_JournalCustom")
public class SAL_JournalCustomFacade extends B2BBaseFacade {

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsSAL_JournalBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> customParams = params;
        Map<String, Object> filterParams = (Map<String, Object>) params.get("FILTERPARAMS");
        Map<String, Object> result = null;
        parseDates(params, Double.class);

        XMLUtil.convertDateToFloat(customParams);
        result = this.selectQuery("dsSAL_JournalBrowseListByParamEx", "dsSAL_JournalBrowseListByParamExCount", customParams);
        return result;
    }

    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsSAL_JournalProcessItem(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("ID", params.get("ID"));
        queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> queryResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsSAL_JournalBrowseListByParam", queryParams, login, password);
        Map<String, Object> result = new HashMap<String, Object>();
        if ((null == queryResult.get("ISRESOLVED") || ((null != queryResult.get("ISRESOLVED") && ((Long) queryResult.get("ISRESOLVED") < 1L))))) {
            queryParams.clear();
            queryParams.put("ID", params.get("ID"));
            queryParams.put("ISRESOLVED", 1L);

            return this.callService(B2BPOSWS_SERVICE_NAME, "dsSAL_JournalUpdate", queryParams, login, password);
        }
        result.put(RESULT, "error");
        result.put("ERRORINFO", "rsInvalid");
        return result;

    }

    @WsMethod(requiredParams = {"JOURNALID"})
    public Map<String, Object> dsSAL_Journal_FlagCustomBrowseListByParam(Map<String, Object> params) throws Exception {
        return this.selectQuery("dsSAL_Journal_FlagCustomBrowseListByParam", "dsSAL_Journal_FlagCustomBrowseListByParamCount", params);
    }

    @WsMethod(requiredParams = {"JOURNALID"})
    public Map<String, Object> dsSAL_Journal_ContextCustomBrowseListByParam(Map<String, Object> params) throws Exception {
        return this.selectQuery("dsSAL_Journal_ContextCustomBrowseListByParam", "dsSAL_Journal_ContextCustomBrowseListByParamCount", params);
    }


}
