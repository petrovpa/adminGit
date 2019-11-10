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
@Discriminator(3)
@IdGen(entityName = "B2B_INVAM", idFieldName = "INVAMID")
@BOName("B2BInvestDID")
public class B2BInvestDIDFacade extends BaseFacade {

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
    public Map<String, Object> dsB2BInvestDIDCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestDIDInsert", params);
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
    public Map<String, Object> dsB2BInvestDIDInsert(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestDIDInsert", params);
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
    public Map<String, Object> dsB2BInvestDIDUpdate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestDIDUpdate", params);
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
    public Map<String, Object> dsB2BInvestDIDModify(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestDIDUpdate", params);
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
    public void dsB2BInvestDIDDelete(Map<String, Object> params) throws Exception {
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
     * <LI>PROGNAME - Программа страхования (маркетинговое наименование продукта)</LI>
     * <LI>INSUREDNAME - ФИО Страхователя</LI>
     * <LI>CONTRCURRENCYID - Валюта договора (USD / RUR)</LI>
     * <LI>CONTRSTARTDATE - Дата начала действия договора</LI>
     * <LI>TERMYEARCOUNT - Срок страхования (количество лет)</LI>
     * <LI>PAYVAR - Периодичность оплаты взносов: 0 - единовременно; 1 - раз в год; 2 - раз в полгода; 4 - ежеквартально</LI>
     * <LI>INSAMVALUE - Страховая сумма по Основной программе</LI>
     * <LI>PREMVALUE - Страховая премия по Основной программе</LI>
     * <LI>PREMTOTALVALUE - Общая премия</LI>
     * <LI>DIDCONTRVALUE - ДИД на 31.12.2015 в валюте договора</LI>
     * <LI>DIDPAYMENTVALUE - ДИД на 31.12.2015 в рублях</LI>
     * <LI>INDVALUE - Гарантированная страховая сумма  (стрховая сумма по основной программе с учетом дохода)</LI>
     * <LI>DIDVALUE - ДИД в % от внесенных взносов</LI>
     * <LI>DIDYEAR - Год ДИД</LI>
     * <LI>RATEVALUE - Ставка фактической нормы доходности, %</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestDIDBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BInvestDIDBrowseListByParam", "dsB2BInvestDIDBrowseListByParamCount", params);
        return result;
    }

}
