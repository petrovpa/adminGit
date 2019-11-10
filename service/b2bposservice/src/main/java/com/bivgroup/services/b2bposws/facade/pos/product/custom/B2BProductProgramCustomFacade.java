package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;


/**
 *
 * @author averichevsm
 */
@BOName("B2BProductProgramCustom")
public class B2BProductProgramCustomFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductProgramBrowseListByParam4lk(Map<String, Object> params) throws Exception {
        params.put("LKVISIBLE", 1);
        Map<String,Object> result = this.selectQuery("dsB2BProductProgramBrowseListByParam", "dsB2BProductProgramBrowseListByParamCount", params);
        return result;
    }
}
