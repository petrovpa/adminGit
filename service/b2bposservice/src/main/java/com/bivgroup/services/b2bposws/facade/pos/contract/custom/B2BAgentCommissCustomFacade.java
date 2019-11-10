/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.contract.custom;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("B2BAgentCommissCustom")
public class B2BAgentCommissCustomFacade extends B2BLifeBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAgentCommissBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BAgentCommissBrowseListByParamEx", "dsB2BAgentCommissBrowseListByParamExCount", params);
        return result;
    }

}
