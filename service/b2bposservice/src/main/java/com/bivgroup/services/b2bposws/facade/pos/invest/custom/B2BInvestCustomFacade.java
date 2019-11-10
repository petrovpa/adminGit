package com.bivgroup.services.b2bposws.facade.pos.invest.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Map;

/**
 *
 * @author aklunok
 */
@Discriminator(1)
@BOName("B2BInvestCustom")
public class B2BInvestCustomFacade extends B2BBaseFacade {

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVAMID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVAMID - ИД записи</LI>
     * <LI>DISCRIMINATOR - Дискриминатор сущности</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRNUMBER - Номер договора</LI>
     * <LI>CALCDATE - Дата расчета</LI>
     * <LI>INSAMVALUE - Страховая сумма</LI>
     * <LI>INDVALUE - Гарантия</LI>
     * <LI>INVVALUE - Инвестиционный доход</LI>
     * <LI>BAVALUE - Базовый актив</LI>
     * <LI>REDEMPVALUE - Выкупная сумма</LI>
     * <LI>DIDVALUE - ДИД (% от взноса)</LI>
     * <LI>INSAMIDDVALUE - Страховая сумма + ИДД</LI>
     * <LI>IDDVALUE - ИДД</LI>
     * <LI>COEFINTVALUE - Коэффициент участия</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRNUMBER"})
    public Map<String, Object> dsB2BInvestBrowseListByParamEx(Map<String, Object> params) throws Exception {
        parseDates(params, Double.class);
        Map<String, Object> result = this.selectQuery("dsB2BInvestBrowseListByParamEx", "dsB2BInvestBrowseListByParamExCount", params);
        return result;
    }

    @WsMethod(requiredParams = {"CONTRNUMBER"})
    public Map<String, Object> dsB2BInvestMaxDateBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BInvestMaxDateBrowseListByParam", "dsB2BInvestMaxDateBrowseListByParamCount", params);
        return result;
    }
    
}
