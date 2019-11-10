/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("LossesRequestOrgStructCustom")
public class LossesRequestOrgStructCustomFacade extends BaseFacade {

    private static final String BIVSBERPOS_SERVICE_NAME = "bivsberposws";

    @WsMethod(requiredParams = {"REQUESTID"})
    public Map<String, Object> dsLossesRequestOrgStructCreateEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // обязательные документы
        Long requestId = Long.valueOf(params.get("REQUESTID").toString());
        if (params.get("DEPROLELIST") != null) {
            List<Map<String, Object>> depRoleList = (List<Map<String, Object>>) params.get("DEPROLELIST");
            for (Map<String, Object> map : depRoleList) {
                Long orgStructId = Long.valueOf(map.get("depid").toString());
                Map<String, Object> qparam = new HashMap<String, Object>();
                qparam.put("REQUESTID", requestId);
                qparam.put("ORGSTRUCTID", orgStructId);
                qparam.put(RETURN_AS_HASH_MAP, "TRUE");
                Map<String, Object> qRes = this.callService(BIVSBERPOS_SERVICE_NAME, "dsLossesRequestOrgStructBrowseListByParam", qparam, login, password);
                if (qRes != null) {
                    if (qRes.get("REQUESTORGSTRUCTID") == null) {
                        Map<String, Object> createRes = this.callService(BIVSBERPOS_SERVICE_NAME, "dsLossesRequestOrgStructCreate", qparam, login, password);



                        //[bivsberposws]?dsLossesRequestOrgStructCreate
                    }
                }
                qparam.put("OLDORGSTRUCTID", params.get("SESSION_ORGSTRUCTID"));
                qparam.put("OLDUSERID", params.get("USERACCOUNTID"));
                qparam.put("UPDATETEXT", params.get("UPDATETEXT"));
//                qparam.put("UPDATETEXT", "Договор передан в другое подразделение");
                Map<String, Object> histRes = this.callService(BIVSBERPOS_SERVICE_NAME, "dsLossesRequestOrgHistCreate", qparam, login, password);

                if (params.get("CLEARCREATOR") != null) {
                    if ("TRUE".equalsIgnoreCase(params.get("CLEARCREATOR").toString())) {
                        qparam.clear();
                        qparam.put("REQUESTID", requestId);
                        Map<String, Object> clearRes = this.callService(BIVSBERPOS_SERVICE_NAME, "dsLossesRequestNACreateUserReset", qparam, login, password);
                        //[bivsberposws]?dsLossesRequestNACreateUserReset
                    }
                }

            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("FLINK", "SELF");

        return result;
    }
}
