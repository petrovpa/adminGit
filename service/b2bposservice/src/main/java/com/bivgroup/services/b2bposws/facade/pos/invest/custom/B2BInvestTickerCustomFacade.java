package com.bivgroup.services.b2bposws.facade.pos.invest.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@BOName("B2BInvestTickerCustom")
public class B2BInvestTickerCustomFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestTickerBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BInvestTickerBrowseListByParamEx", "dsB2BInvestTickerBrowseListByParamExCount", params);
        return result;
    }

}
