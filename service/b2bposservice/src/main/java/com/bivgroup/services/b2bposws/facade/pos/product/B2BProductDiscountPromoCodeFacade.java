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
 * Фасад для сущности B2BProductDiscountPromoCode
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODDISCPROMO",idFieldName="PRODDISCPROMOID")
@BOName("B2BProductDiscountPromoCode")
public class B2BProductDiscountPromoCodeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Промокод</LI>
     * <LI>PRODDISCPROMOID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PROMOCOUNT - Кол-во промокодов</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCPROMOID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductDiscountPromoCodeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDiscountPromoCodeInsert", params);
        result.put("PRODDISCPROMOID", params.get("PRODDISCPROMOID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Промокод</LI>
     * <LI>PRODDISCPROMOID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PROMOCOUNT - Кол-во промокодов</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCPROMOID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCPROMOID"})
    public Map<String,Object> dsB2BProductDiscountPromoCodeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDiscountPromoCodeInsert", params);
        result.put("PRODDISCPROMOID", params.get("PRODDISCPROMOID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Промокод</LI>
     * <LI>PRODDISCPROMOID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PROMOCOUNT - Кол-во промокодов</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCPROMOID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCPROMOID"})
    public Map<String,Object> dsB2BProductDiscountPromoCodeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDiscountPromoCodeUpdate", params);
        result.put("PRODDISCPROMOID", params.get("PRODDISCPROMOID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Промокод</LI>
     * <LI>PRODDISCPROMOID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PROMOCOUNT - Кол-во промокодов</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCPROMOID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCPROMOID"})
    public Map<String,Object> dsB2BProductDiscountPromoCodeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDiscountPromoCodeUpdate", params);
        result.put("PRODDISCPROMOID", params.get("PRODDISCPROMOID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODDISCPROMOID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCPROMOID"})
    public void dsB2BProductDiscountPromoCodeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductDiscountPromoCodeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Промокод</LI>
     * <LI>PRODDISCPROMOID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PROMOCOUNT - Кол-во промокодов</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CODE - Промокод</LI>
     * <LI>PRODDISCPROMOID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PROMOCOUNT - Кол-во промокодов</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductDiscountPromoCodeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductDiscountPromoCodeBrowseListByParam", "dsB2BProductDiscountPromoCodeBrowseListByParamCount", params);
        return result;
    }





}
