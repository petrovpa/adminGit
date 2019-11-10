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
 * Фасад для сущности ContractRedemption
 *
 * @author aklunok
 */
@Discriminator(10)
@Auth(onlyCreatorAccess = false)
@IdGen(entityName = "B2B_CONTRAMPREM", idFieldName = "CONTRAMPREMID")
@BOName("B2BContractRedemption")
public class B2BContractRedemptionFacade extends BaseFacade {

    /**
     * Создать объект с генерацией id
     *
     * @author aklunok
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
    public Map<String, Object> dsB2BContractRedemptionCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        this.insertQuery("dsB2BContractRedemptionInsert", params);
        result.put("CONTRAMPREMID", params.get("CONTRAMPREMID"));
        return result;
    }

    /**
     * Создать объект без генерации id
     *
     * @author aklunok
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
    public Map<String, Object> dsB2BContractRedemptionInsert(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        this.insertQuery("dsB2BContractRedemptionInsert", params);
        result.put("CONTRAMPREMID", params.get("CONTRAMPREMID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author aklunok
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
    public Map<String, Object> dsB2BContractRedemptionUpdate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        this.updateQuery("dsB2BContractRedemptionUpdate", params);
        result.put("CONTRAMPREMID", params.get("CONTRAMPREMID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author aklunok
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
    public Map<String, Object> dsB2BContractRedemptionModify(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        this.updateQuery("dsB2BContractRedemptionUpdate", params);
        result.put("CONTRAMPREMID", params.get("CONTRAMPREMID"));
        return result;
    }

    /**
     * Удалить объект по id
     *
     * @author aklunok
     * @param params
     * <UL>
     * <LI>CONTRAMPREMID - ИД</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {"CONTRAMPREMID"})
    public void dsB2BContractRedemptionDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractRedemptionDelete", params);
    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author aklunok
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
    public Map<String, Object> dsB2BContractRedemptionBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BContractRedemptionBrowseListByParam", "dsB2BContractRedemptionBrowseListByParamCount", params);
        return result;
    }

}
