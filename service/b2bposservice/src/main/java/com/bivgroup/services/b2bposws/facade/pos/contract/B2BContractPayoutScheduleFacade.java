package com.bivgroup.services.b2bposws.facade.pos.contract;

import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * Фасад для сущности PayoutSchedule
 *
 * @author kkulkov
 */
@Discriminator(30)
@Auth(onlyCreatorAccess = false)
@IdGen(entityName = "B2B_CONTRAMPREM", idFieldName = "CONTRAMPREMID")
@BOName("B2BContractPayoutSchedule")
public class B2BContractPayoutScheduleFacade extends BaseFacade {

    /**
     * Создать объект с генерацией id
     *
     * @author kkulkov
     * @param params
     * <UL>
     * <LI>CONTRID - ссылка на договор</LI>
     * <LI>FINISHDATE - дата окончания периода</LI>
     * <LI>REDEMPVALUE - выкупная сумма</LI>
     * <LI>PAYVALUE - сумма платежа</LI>
     * <LI>PAYNUM - нумерация строки, целое</LI>
     * <LI>PREMVALUE - страховая премия</LI>
     * <LI>STARTDATE - дата начала периода</LI>
     * <LI>PAYDATE - дата платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRAMPREMID - ИД</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BContractPayoutScheduleCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        this.insertQuery("dsB2BContractPayoutScheduleInsert", params);
        result.put("CONTRAMPREMID", params.get("CONTRAMPREMID"));
        return result;
    }

    /**
     * Создать объект без генерации id
     *
     * @author kkulkov
     * @param params
     * <UL>
     * <LI>CONTRID - ссылка на договор</LI>
     * <LI>FINISHDATE - дата окончания периода</LI>
     * <LI>REDEMPVALUE - выкупная сумма</LI>
     * <LI>PAYVALUE - сумма платежа</LI>
     * <LI>PAYNUM - нумерация строки, целое</LI>
     * <LI>PREMVALUE - страховая премия</LI>
     * <LI>STARTDATE - дата начала периода</LI>
     * <LI>PAYDATE - дата платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRAMPREMID - ИД</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {"CONTRAMPREMID"})
    public Map<String, Object> dsB2BContractPayoutScheduleInsert(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        this.insertQuery("dsB2BContractPayoutScheduleInsert", params);
        result.put("CONTRAMPREMID", params.get("CONTRAMPREMID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author kkulkov
     * @param params
     * <UL>
     * <LI>CONTRID - ссылка на договор</LI>
     * <LI>FINISHDATE - дата окончания периода</LI>
     * <LI>REDEMPVALUE - выкупная сумма</LI>
     * <LI>PAYVALUE - сумма платежа</LI>
     * <LI>PAYNUM - нумерация строки, целое</LI>
     * <LI>PREMVALUE - страховая премия</LI>
     * <LI>STARTDATE - дата начала периода</LI>
     * <LI>PAYDATE - дата платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRAMPREMID - ИД</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {"CONTRAMPREMID"})
    public Map<String, Object> dsB2BContractPayoutScheduleUpdate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        this.updateQuery("dsB2BContractPayoutScheduleUpdate", params);
        result.put("CONTRAMPREMID", params.get("CONTRAMPREMID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author kkulkov
     * @param params
     * <UL>
     * <LI>CONTRID - ссылка на договор</LI>
     * <LI>FINISHDATE - дата окончания периода</LI>
     * <LI>REDEMPVALUE - выкупная сумма</LI>
     * <LI>PAYVALUE - сумма платежа</LI>
     * <LI>PAYNUM - нумерация строки, целое</LI>
     * <LI>PREMVALUE - страховая премия</LI>
     * <LI>STARTDATE - дата начала периода</LI>
     * <LI>PAYDATE - дата платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRAMPREMID - ИД</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {"CONTRAMPREMID"})
    public Map<String, Object> dsB2BContractPayoutScheduleModify(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        this.updateQuery("dsB2BContractPayoutScheduleUpdate", params);
        result.put("CONTRAMPREMID", params.get("CONTRAMPREMID"));
        return result;
    }

    /**
     * Удалить объект по id
     *
     * @author kkulkov
     * @param params
     * <UL>
     * <LI>CONTRAMPREMID - ИД</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {"CONTRAMPREMID"})
    public void dsB2BContractPayoutScheduleDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractPayoutScheduleDelete", params);
    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author kkulkov
     * @param params
     * <UL>
     * <LI>CONTRID - ссылка на договор</LI>
     * <LI>FINISHDATE - дата окончания периода</LI>
     * <LI>REDEMPVALUE - выкупная сумма</LI>
     * <LI>PAYVALUE - сумма платежа</LI>
     * <LI>PAYNUM - нумерация строки, целое</LI>
     * <LI>PREMVALUE - страховая премия</LI>
     * <LI>STARTDATE - дата начала периода</LI>
     * <LI>PAYDATE - дата платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ссылка на договор</LI>
     * <LI>FINISHDATE - дата окончания периода</LI>
     * <LI>REDEMPVALUE - выкупная сумма</LI>
     * <LI>PAYVALUE - сумма платежа</LI>
     * <LI>PAYNUM - нумерация строки, целое</LI>
     * <LI>STARTDATE - дата начала периода</LI>
     * <LI>PAYDATE - дата платежа</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractPayoutScheduleBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BContractPayoutScheduleBrowseListByParam", "dsB2BContractPayoutScheduleBrowseListByParamCount", params);
        return result;
    }

}
