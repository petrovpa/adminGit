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
 * @author averichevsm
 */
@BOName("B2BProductBinaryDocumentCustom")
public class B2BProductBinaryDocumentCustomFacade extends B2BBaseFacade {
 
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductBinaryDocumentBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductBinaryDocumentBrowseListByParamEx", "dsB2BProductBinaryDocumentBrowseListByParamExCount", params);
        return result;
    }    
    
}
