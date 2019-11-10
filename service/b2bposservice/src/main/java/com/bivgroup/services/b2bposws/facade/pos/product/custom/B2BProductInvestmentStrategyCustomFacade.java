package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;

@BOName("B2BProductInvestmentStrategyCustom")
public class B2BProductInvestmentStrategyCustomFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductInvestmentStrategyBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BProductInvestmentStrategyBrowseListByParamEx", params);
        return result;
    }

    /**
     * Получить объекты в виде списка по ограничениям
     * @param params
     * <UL>
     * <LI>$PRODCONFID</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DISCRIMINATOR</LI>
     * <LI>FINISHDATE</LI>
     * <LI>PRODINVESTID</LI>
     * <LI>INVESTSTRATEGYID</LI>
     * <LI>PRODCONFID</LI>
     * <LI>STARTDATE</LI>
     * <LI>NAME</LI>
     * <LI>SYSNAME</LI>
     * <LI>CODE</LI>
     * <LI>CURRENCYID</LI>
     * <LI>TERMID</LI>
     * <LI>MINPAYVALUE</LI>
     * </UL>
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductInvestBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductInvestBrowseListByParamEx", "dsB2BProductInvestBrowseListByParamExCount", params);
        return result;
    }

}
