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
 * @author deathstalker
 */
@Discriminator(1)
@IdGen(entityName="B2B_INVAM", idFieldName="INVAMID")
@BOName("B2BInvest")
public class B2BInvestFacade extends BaseFacade {

    /**
     * Создать объект с генерацией id
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
    public Map<String,Object> dsB2BInvestCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestInsert", params);
        result.put("INVAMID", params.get("INVAMID"));
        return result;
    }

    /**
     * Создать объект без генерации id
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
    public Map<String,Object> dsB2BInvestInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestInsert", params);
        result.put("INVAMID", params.get("INVAMID"));
        return result;
    }

    /**
     * Изменить объект
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
    public Map<String,Object> dsB2BInvestUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestUpdate", params);
        result.put("INVAMID", params.get("INVAMID"));
        return result;
    }

    /**
     * Изменить объект
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
    public Map<String,Object> dsB2BInvestModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestUpdate", params);
        result.put("INVAMID", params.get("INVAMID"));
        return result;
    }

   /**
     * Удалить объект по id
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
    public void dsB2BInvestDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BInvestDelete", params);
    }

    /**
     * Получить объекты в виде списка по ограничениям
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
    public Map<String,Object> dsB2BInvestBrowseListByParam(Map<String, Object> params) throws Exception {
        String contrNumber = (String) params.get("CONTRNUMBER");
        if (contrNumber == null || contrNumber.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Map<String,Object> result = this.selectQuery("dsB2BInvestBrowseListByParam", "dsB2BInvestBrowseListByParamCount", params);
        return result;
    }
    
}
