package com.bivgroup.services.b2bposws.facade.pos.invest.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@BOName("B2BTickerRateCustom")
public class B2BTickerRateCustomFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BTickerRateBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BTickerRateBrowseListByParamEx", "dsB2BTickerRateBrowseListByParamExCount", params);
        return result;
    }

}
