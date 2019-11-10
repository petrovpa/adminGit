package com.bivgroup.services.b2bposws.facade.pos.invest.custom;

import java.util.Map;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@Discriminator(3)
@BOName("B2BInvestDIDCustom")
public class B2BInvestDIDCustomFacade extends BaseFacade {

    @WsMethod(requiredParams = {"CONTRNUMBER"})
    public Map<String, Object> dsB2BInvestDIDMaxDateBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BInvestDIDMaxDateBrowseListByParam", "dsB2BInvestDIDMaxDateBrowseListByParamCount", params);
        return result;
    }

}
