package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@BOName("B2BInvestBaseActiveCustom")
public class B2BInvestBaseActiveCustomFacade extends BaseFacade {

    @WsMethod(requiredParams = {"PRODVERID"})
    public Map<String, Object> dsB2BProductInvestBaseActiveBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BProductInvestBaseActiveBrowseListByParamEx", "dsB2BProductInvestBaseActiveBrowseListByParamExCount", params);
        return result;
    }

}
