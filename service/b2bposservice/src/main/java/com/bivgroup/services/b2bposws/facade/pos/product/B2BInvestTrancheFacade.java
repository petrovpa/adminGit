/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BInvestTranche
 *
 * @author reson
 */
@IdGen(entityName="B2B_INVTRANCHE",idFieldName="INVTRANCHEID")
@BOName("B2BInvestTranche")
public class B2BInvestTrancheFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BARRIER - Значение барьера</LI>
     * <LI>CALCDATE - Дата расчета</LI>
     * <LI>CONTRACTSTARTDATE - Дата начала договоров проданных в окне продаж</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>RATE - Ставка</LI>
     * <LI>SALEFINISHDATE - Дата закрытия окна продаж</LI>
     * <LI>SALESTARTDATE - Дата открытия окна продаж</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestTrancheCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestTrancheInsert", params);
        result.put("INVTRANCHEID", params.get("INVTRANCHEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BARRIER - Значение барьера</LI>
     * <LI>CALCDATE - Дата расчета</LI>
     * <LI>CONTRACTSTARTDATE - Дата начала договоров проданных в окне продаж</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>RATE - Ставка</LI>
     * <LI>SALEFINISHDATE - Дата закрытия окна продаж</LI>
     * <LI>SALESTARTDATE - Дата открытия окна продаж</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTRANCHEID"})
    public Map<String,Object> dsB2BInvestTrancheInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestTrancheInsert", params);
        result.put("INVTRANCHEID", params.get("INVTRANCHEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BARRIER - Значение барьера</LI>
     * <LI>CALCDATE - Дата расчета</LI>
     * <LI>CONTRACTSTARTDATE - Дата начала договоров проданных в окне продаж</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>RATE - Ставка</LI>
     * <LI>SALEFINISHDATE - Дата закрытия окна продаж</LI>
     * <LI>SALESTARTDATE - Дата открытия окна продаж</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTRANCHEID"})
    public Map<String,Object> dsB2BInvestTrancheUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestTrancheUpdate", params);
        result.put("INVTRANCHEID", params.get("INVTRANCHEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BARRIER - Значение барьера</LI>
     * <LI>CALCDATE - Дата расчета</LI>
     * <LI>CONTRACTSTARTDATE - Дата начала договоров проданных в окне продаж</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>RATE - Ставка</LI>
     * <LI>SALEFINISHDATE - Дата закрытия окна продаж</LI>
     * <LI>SALESTARTDATE - Дата открытия окна продаж</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTRANCHEID"})
    public Map<String,Object> dsB2BInvestTrancheModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestTrancheUpdate", params);
        result.put("INVTRANCHEID", params.get("INVTRANCHEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTRANCHEID"})
    public void dsB2BInvestTrancheDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BInvestTrancheDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BARRIER - Значение барьера</LI>
     * <LI>CALCDATE - Дата расчета</LI>
     * <LI>CONTRACTSTARTDATE - Дата начала договоров проданных в окне продаж</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>RATE - Ставка</LI>
     * <LI>SALEFINISHDATE - Дата закрытия окна продаж</LI>
     * <LI>SALESTARTDATE - Дата открытия окна продаж</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BARRIER - Значение барьера</LI>
     * <LI>CALCDATE - Дата расчета</LI>
     * <LI>CONTRACTSTARTDATE - Дата начала договоров проданных в окне продаж</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTRANCHEID - ИД транша</LI>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>RATE - Ставка</LI>
     * <LI>SALEFINISHDATE - Дата закрытия окна продаж</LI>
     * <LI>SALESTARTDATE - Дата открытия окна продаж</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestTrancheBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BInvestTrancheBrowseListByParam", "dsB2BInvestTrancheBrowseListByParamCount", params);
        return result;
    }





}
