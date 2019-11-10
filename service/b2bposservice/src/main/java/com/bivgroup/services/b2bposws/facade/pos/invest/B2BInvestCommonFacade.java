package com.bivgroup.services.b2bposws.facade.pos.invest;

import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@IdGen(entityName="B2B_INVAM", idFieldName="INVAMID")
@BOName("B2BInvestCommon")
public class B2BInvestCommonFacade extends BaseFacade {

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author aklunok
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
     * <LI>INSAMVALUE - Страховая сумма</LI>
     * <LI>INDVALUE - Гарантия</LI>
     * <LI>INVVALUE - Инвестиционный доход</LI>
     * <LI>BAVALUE - Базовый актив</LI>
     * <LI>REDEMPVALUE - Выкупная сумма</LI>
     * <LI>DIDVALUE - ДИД (% от взноса)</LI>
     * <LI>INSAMIDDVALUE - Страховая сумма + ИДД</LI>
     * <LI>IDDVALUE - ИДД</LI>
     * <LI>COEFINTVALUE - Коэффициент участия</LI>
     * <LI>PROGNAME - Программа страхования (маркетинговое наименование продукта)</LI>
     * <LI>INSUREDNAME - ФИО Страхователя</LI>
     * <LI>CONTRSTARTDATE - Дата начала действия договора</LI>
     * <LI>TERMYEARCOUNT - Срок страхования (количество лет)</LI>
     * <LI>PAYVAR - Периодичность оплаты взносов: 0 - единовременно; 1 - раз в год; 2 - раз в полгода; 4 - ежеквартально</LI>
     * <LI>PREMTOTALVALUE - Общая премия</LI>
     * <LI>DIDCONTRVALUE - ДИД на 31.12.2015 в валюте договора</LI>
     * <LI>DIDPAYMENTVALUE - ДИД на 31.12.2015 в рублях</LI>
     * <LI>DIDYEAR - Год ДИД</LI>
     * <LI>RATEVALUE - Ставка фактической нормы доходности, %</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRNUMBER"})
    public Map<String, Object> dsB2BInvestCommonBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BInvestCommonBrowseListByParam", "dsB2BInvestCommonBrowseListByParamCount", params);
        return result;
    }
         /**
     * Изменить объект
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKCASHFLOWID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKCASHFLOWID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKCASHFLOWID"})
    public Map<String, Object> dsB2B2BCashFlowSetTypeModifyString(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B2BCashFlowSetTypeModifyString", params);
        result.put("BANKCASHFLOWID", params.get("BANKCASHFLOWID"));
        return result;
    }
    
}
