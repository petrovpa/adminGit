/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author sambucus
 */
@BOName("B2BProductTermCustom")
public class B2BProductTermCustomFacade extends B2BBaseFacade{
    
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductTermBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductTermBrowseListByParamEx", "dsB2BProductTermBrowseListByParamExCount", params);
        return result;
    }    
}
