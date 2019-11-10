/*
 * To change this template, choose Tools | Templates
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
 * @author averichevsm
 */
@BOName("LossesAddressCustom")
public class LossesAddressCustomFacade extends BaseFacade {

    private static final String BIVSBERPOS_SERVICE_NAME = Constants.BIVSBERPOSWS;
    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;

    @WsMethod(requiredParams = {"OBJID"})
    public Map<String, Object> dsLossesAddressSaveEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        if (params.get("INSADDRESSDATA") != null) {
            List<Map<String, Object>> addrMapList = (List<Map<String, Object>>) params.get("INSADDRESSDATA");
            if (!addrMapList.isEmpty()) {

                Map<String, Object> addrMap = addrMapList.get(0);

        Map<String, Object> qParam = new HashMap<String, Object>();

        qParam.put("ADDRESSMAP", addrMap);
        qParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> modifyParams = this.callExternalService(INSPOSWS_SERVICE_NAME, "dsGenFullAddressMap", qParam, login, password);
                
                modifyParams.put("OBJSYSNAME", params.get("OBJSYSNAME"));
                modifyParams.put("OBJID", params.get("OBJID"));

                if ((addrMap.get("ADDRESSID") == null) || (addrMap.get("ADDRESSID").toString().equalsIgnoreCase("")) || (addrMap.get("ADDRESSID").toString().equalsIgnoreCase("0"))) {
                    if (addrMap.get("ADDRESSID") != null) {
                      modifyParams.remove("ADDRESSID");
                    }
                    result = this.callService(BIVSBERPOS_SERVICE_NAME, "dsLossesAddressCreate", modifyParams, login, password);
                } else {
                    result = this.callService(BIVSBERPOS_SERVICE_NAME, "dsLossesAddressModify", modifyParams, login, password);
                }
            }
        }
        return result;
    }
    

    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesAddressBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesAddressBrowseListByParamEx", "dsLossesAddressBrowseListByParamExCount", params);
        return result;
    }    
    
}
