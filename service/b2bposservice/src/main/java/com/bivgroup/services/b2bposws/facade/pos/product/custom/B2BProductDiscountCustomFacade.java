/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("B2BProductDiscountCustom")
public class B2BProductDiscountCustomFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductDiscountBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> res = this.selectQuery("dsB2BProductDiscountBrowseListByParamEx", "dsB2BProductDiscountBrowseListByParamExCount", params);
        return res;
    }
    
    @WsMethod(requiredParams = {"DISCOUNTCODE", "PRODCODE"})
    public Map<String, Object> dsB2BGetProductByDiscountCode(Map<String, Object> params) throws Exception {
        String discountCode = getStringParam(params.get("DISCOUNTCODE"));
        String prodCode = getStringParam(params.get("PRODCODE"));
                
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("EXISTS", Boolean.FALSE);
        Map<String, Object> callParams = new HashMap<String, Object>();
        callParams.put("DISCOUNTCODE", discountCode);
        callParams.put("PRODCODE", prodCode);
        Map<String, Object> res = this.selectQuery("dsB2BGetProductByDiscountCode", "dsB2BGetProductByDiscountCodeCount", callParams);
        if (( res != null) && (res.get(RESULT) != null)) {
            List<Map<String, Object>> dList = (List<Map<String, Object>>) res.get(RESULT);
            for (Map<String, Object> discount : dList) {
                String resProdCode = getStringParam(discount.get("PRODSYSNAME"));
                if (resProdCode.equalsIgnoreCase(prodCode)) {
                    result.put("EXISTS", Boolean.FALSE);
                    return result;
                }
            }
        }
        return result;
    }
    
}
