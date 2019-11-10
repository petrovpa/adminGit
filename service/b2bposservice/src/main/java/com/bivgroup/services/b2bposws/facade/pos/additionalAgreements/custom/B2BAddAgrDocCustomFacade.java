/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.custom;

import com.bivgroup.services.b2bposws.facade.pos.contract.custom.*;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("B2BAddAgrDocCustom")
public class B2BAddAgrDocCustomFacade extends BaseFacade {
    
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAddAgrDocBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BAddAgrDocBrowseListByParamEx", "dsB2BAddAgrDocBrowseListByParamExCount", params);
        return result;
    }
    
    @WsMethod(requiredParams = {"ADDAGRDOCID", "BINFILEID"})
    public void dsB2BAddAgrDocDeleteEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> delMap = new HashMap<String, Object>();
        delMap.put("BINFILEID", params.get("BINFILEID"));
        this.callService(Constants.B2BPOSWS, "dsB2BAddAgrDoc_BinaryFile_deleteBinaryFileInfo", delMap, login, password);
        delMap.clear();
        delMap.put("ADDAGRDOCID", params.get("ADDAGRDOCID"));
        this.callService(Constants.B2BPOSWS, "dsB2BAddAgrDocDelete", delMap, login, password);
    }
    
}
