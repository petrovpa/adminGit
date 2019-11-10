/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.other.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author kkulkov
 */
@BOName("B2BUserParamCustom")
public class B2BUserParamCustomFacade extends B2BBaseFacade {
    public static final String SESSIONPARAM_USERACCOUNTID = "SESSION_USERACCOUNTID";

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2B_GetUserParams(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (null != params.get(SESSIONPARAM_USERACCOUNTID)) {
            Long uacId = getLongParam(params, SESSIONPARAM_USERACCOUNTID);
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("USERACCOUNTID", uacId);
            queryParams.put("ENTITYKEY", params.get("ENTITYKEY"));
            queryParams.put("ENTITYTYPE", params.get("ENTITYTYPE"));
            queryParams.put(ORDERBY, "T.ENTITYTYPE, T.ENTITYKEY asc");
            String login = params.get(WsConstants.LOGIN).toString();
            String password = params.get(WsConstants.PASSWORD).toString();
            Map<String, Object> callRes = this.callService(Constants.B2BPOSWS,
                    "dsB2BUserParamBrowseListByParam", queryParams, login, password);
            if ((null != callRes) && (null != callRes.get(RESULT)
                    && (callRes.get(RESULT) instanceof List)
                    && (!((List) callRes.get(RESULT)).isEmpty()))) {
                List<Map<String, Object>> upList = (List<Map<String, Object>>) callRes.get(RESULT);
                Map<String, Object> upTree = buildUserParamTree(upList);
                result.putAll(upTree);

            } else {
                result.put("userParam", new ArrayList<Map<String, Object>>());
                result.put(TOTALCOUNT, 0);
            }
        }
        return result;
    }

    private Map<String, Object> buildUserParamTree(List<Map<String, Object>> upList) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Map<String, Object> item : upList) {
            String eType = item.get("ENTITYTYPE").toString();
            String eKey = item.get("ENTITYKEY").toString();
            Map<String, Object> cType = (Map<String, Object>) result.get(eType);
            if (null == cType) {
                cType = new HashMap<String, Object>();
                result.put(eType, cType);
            }
            cType.put(eKey, item.get("VALUE"));
        }
        return result;
    }

    @WsMethod(requiredParams = {"USERPARAM"})
    public Map<String, Object> dsB2B_SetUserParams(Map<String, Object> params) throws Exception {
        Map<String, Object> userParam = (Map<String, Object>) params.get("USERPARAM");
        Long uacId = getLongParam(params, SESSIONPARAM_USERACCOUNTID);
        Map<String, Object> queryParams = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : userParam.entrySet()) {
            String eType = entry.getKey();
            Map<String, Object> cType = (Map<String, Object>) entry.getValue();
            if (null != cType) {
                for (Map.Entry<String, Object> entryKey : cType.entrySet()) {
                    String eKey = entryKey.getKey();
                    Object value = entryKey.getValue();
                    queryParams.clear();
                    queryParams.put("USERACCOUNTID", uacId);
                    queryParams.put("ENTITYKEY", eKey);
                    queryParams.put("ENTITYTYPE", eType);
                    String login = params.get(WsConstants.LOGIN).toString();
                    String password = params.get(WsConstants.PASSWORD).toString();
                    Map<String, Object> callRes = this.callService(Constants.B2BPOSWS,
                            "dsB2BUserParamBrowseListByParam", queryParams, login, password);
                    if ((null != callRes) && (null != callRes.get(RESULT)
                            && (callRes.get(RESULT) instanceof List)
                            && (!((List) callRes.get(RESULT)).isEmpty()))) {
                        List<Map<String, Object>> upList = (List<Map<String, Object>>) callRes.get(RESULT);
                        for (int i = 0; i < upList.size(); i++) {
                            Map<String, Object> up = upList.get(i);
                            if (i == 0) {
                                up.put("VALUE", value);
                                this.callService(Constants.B2BPOSWS, "dsB2BUserParamUpdate", up, login, password);
                            } else {
                                this.callService(Constants.B2BPOSWS, "dsB2BUserParamDelete", up, login, password);
                            }
                        }

                    } else {
                        queryParams.clear();
                        queryParams.put("USERACCOUNTID", uacId);
                        queryParams.put("ENTITYKEY", eKey);
                        queryParams.put("ENTITYTYPE", eType);
                        queryParams.put("VALUE", value);
                        this.callService(Constants.B2BPOSWS, "dsB2BUserParamCreate", queryParams, login, password);
                    }

                }
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Status", "OK");
        return result;
    }

    @WsMethod(requiredParams = {"USERPARAM"})
    public Map<String, Object> dsB2B_DeleteUserParams(Map<String, Object> params) throws Exception {
        Map<String, Object> userParam = (Map<String, Object>) params.get("USERPARAM");
        Long uacId = getLongParam(params, SESSIONPARAM_USERACCOUNTID); // fix journal
        Map<String, Object> queryParams = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : userParam.entrySet()) {
            String eType = entry.getKey();
            Map<String, Object> cType = (Map<String, Object>) entry.getValue();
            if (null != cType) {
                for (Map.Entry<String, Object> entryKey : cType.entrySet()) {
                    String eKey = entryKey.getKey();
                    Object value = entryKey.getValue();
                    queryParams.clear();
                    queryParams.put("USERACCOUNTID", uacId);
                    queryParams.put("ENTITYKEY", eKey);
                    queryParams.put("ENTITYTYPE", eType);
                    String login = params.get(WsConstants.LOGIN).toString();
                    String password = params.get(WsConstants.PASSWORD).toString();
                    Map<String, Object> callRes = this.callService(Constants.B2BPOSWS,
                            "dsB2BUserParamBrowseListByParam", queryParams, login, password);
                    if ((null != callRes) && (null != callRes.get(RESULT)
                            && (callRes.get(RESULT) instanceof List)
                            && (!((List) callRes.get(RESULT)).isEmpty()))) {
                        List<Map<String, Object>> upList = (List<Map<String, Object>>) callRes.get(RESULT);
                        for (int i = 0; i < upList.size(); i++) {
                            Map<String, Object> up = upList.get(i);
                            this.callService(Constants.B2BPOSWS, "dsB2BUserParamDelete", up, login, password);
                        }
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Status", "OK");
        return result;
    }

}
