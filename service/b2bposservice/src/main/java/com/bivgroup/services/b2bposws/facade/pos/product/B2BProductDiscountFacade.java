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
 * Фасад для сущности B2BProductDiscount
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODDISC",idFieldName="PRODDISCID")
@BOName("B2BProductDiscount")
public class B2BProductDiscountFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCKINDID - Вид</LI>
     * <LI>FINISHDATE - Дата окончания действия скидки</LI>
     * <LI>PRODDISCID - ИД</LI>
     * <LI>ISPREMIUM - Премиум аккаунт</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PREMIUMURL - УРЛ премиум аккаунта</LI>
     * <LI>PRODCONFID - Конфигурация продукта</LI>
     * <LI>STARTDATE - Дата начала действия скидки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductDiscountCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDiscountInsert", params);
        result.put("PRODDISCID", params.get("PRODDISCID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCKINDID - Вид</LI>
     * <LI>FINISHDATE - Дата окончания действия скидки</LI>
     * <LI>PRODDISCID - ИД</LI>
     * <LI>ISPREMIUM - Премиум аккаунт</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PREMIUMURL - УРЛ премиум аккаунта</LI>
     * <LI>PRODCONFID - Конфигурация продукта</LI>
     * <LI>STARTDATE - Дата начала действия скидки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCID"})
    public Map<String,Object> dsB2BProductDiscountInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDiscountInsert", params);
        result.put("PRODDISCID", params.get("PRODDISCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCKINDID - Вид</LI>
     * <LI>FINISHDATE - Дата окончания действия скидки</LI>
     * <LI>PRODDISCID - ИД</LI>
     * <LI>ISPREMIUM - Премиум аккаунт</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PREMIUMURL - УРЛ премиум аккаунта</LI>
     * <LI>PRODCONFID - Конфигурация продукта</LI>
     * <LI>STARTDATE - Дата начала действия скидки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCID"})
    public Map<String,Object> dsB2BProductDiscountUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDiscountUpdate", params);
        result.put("PRODDISCID", params.get("PRODDISCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCKINDID - Вид</LI>
     * <LI>FINISHDATE - Дата окончания действия скидки</LI>
     * <LI>PRODDISCID - ИД</LI>
     * <LI>ISPREMIUM - Премиум аккаунт</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PREMIUMURL - УРЛ премиум аккаунта</LI>
     * <LI>PRODCONFID - Конфигурация продукта</LI>
     * <LI>STARTDATE - Дата начала действия скидки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCID"})
    public Map<String,Object> dsB2BProductDiscountModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDiscountUpdate", params);
        result.put("PRODDISCID", params.get("PRODDISCID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODDISCID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCID"})
    public void dsB2BProductDiscountDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductDiscountDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCKINDID - Вид</LI>
     * <LI>FINISHDATE - Дата окончания действия скидки</LI>
     * <LI>PRODDISCID - ИД</LI>
     * <LI>ISPREMIUM - Премиум аккаунт</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PREMIUMURL - УРЛ премиум аккаунта</LI>
     * <LI>PRODCONFID - Конфигурация продукта</LI>
     * <LI>STARTDATE - Дата начала действия скидки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DISCKINDID - Вид</LI>
     * <LI>FINISHDATE - Дата окончания действия скидки</LI>
     * <LI>PRODDISCID - ИД</LI>
     * <LI>ISPREMIUM - Премиум аккаунт</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PREMIUMURL - УРЛ премиум аккаунта</LI>
     * <LI>PRODCONFID - Конфигурация продукта</LI>
     * <LI>STARTDATE - Дата начала действия скидки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductDiscountBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductDiscountBrowseListByParam", "dsB2BProductDiscountBrowseListByParamCount", params);
        return result;
    }





}
