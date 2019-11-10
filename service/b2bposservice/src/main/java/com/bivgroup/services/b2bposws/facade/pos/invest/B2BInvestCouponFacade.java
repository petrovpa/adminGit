package com.bivgroup.services.b2bposws.facade.pos.invest;

import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@Discriminator(2)
@IdGen(entityName = "B2B_INVAM", idFieldName = "INVAMID")
@BOName("B2BInvestCoupon")
public class B2BInvestCouponFacade extends BaseFacade {

    /**
     * Создать объект с генерацией id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVAMID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVAMID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestCouponCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestCouponInsert", params);
        result.put("INVAMID", params.get("INVAMID"));
        return result;
    }

    /**
     * Создать объект без генерации id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVAMID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVAMID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVAMID"})
    public Map<String, Object> dsB2BInvestCouponInsert(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestCouponInsert", params);
        result.put("INVAMID", params.get("INVAMID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVAMID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVAMID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVAMID"})
    public Map<String, Object> dsB2BInvestCouponUpdate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestCouponUpdate", params);
        result.put("INVAMID", params.get("INVAMID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVAMID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVAMID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVAMID"})
    public Map<String, Object> dsB2BInvestCouponModify(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestCouponUpdate", params);
        result.put("INVAMID", params.get("INVAMID"));
        return result;
    }

    /**
     * Удалить объект по id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>INVAMID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVAMID"})
    public void dsB2BInvestCouponDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BInvestDelete", params);
    }
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
     * <LI>CPPCTMEM - % Купона (С Множ За Память)</LI>
     * <LI>CPCONTRAMVALUE - Купон В Валюте Договора</LI>
     * <LI>CPRVLTAMVALUE - Купон С Переоценкой, Руб</LI>
     * <LI>CPACRDCONTRAMVALUE - Начисленный Купон В Валюте Договора</LI>
     * <LI>CPACRDRVLTAMVALUE - Начисленный Купон С Переоценкой</LI>
     * <LI>PREMVALUE - Премия</LI>
     * <LI>CPPCTWMEM - % Купона (Без Множ За Память)</LI>
     * <LI>RATESTART - Курс На Дату Начала Действия</LI>
     * <LI>RATECALC - Курс На Дату Расчета</LI>
     * <LI>CONTRCURRENCYID - Валюта Полиса</LI>
     * <LI>CONDINVCURRENCYID - Валюта Условий Инвестирования</LI>
     * <LI>ISCONDITION - Выполнение Условия (0/1)</LI>
     * <LI>ISTODAYCPACRD - Сегодня Начисление Куп?(0/1)</LI>
     * <LI>BARRIERVALUE - Барьер</LI>
     * <LI>MULTIMEMVALUE - Множитель За Эффект Памяти</LI>
     * <LI>CONDNOTE - Описание Условий</LI>
     * <LI>CONDCODE - Код Условий</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestCouponBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BInvestCouponBrowseListByParam", "dsB2BInvestCouponBrowseListByParamCount", params);
        return result;
    }

}
