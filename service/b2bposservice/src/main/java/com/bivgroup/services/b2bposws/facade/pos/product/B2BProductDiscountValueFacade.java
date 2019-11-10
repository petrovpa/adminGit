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
 * Фасад для сущности B2BProductDiscountValue
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODDISCVAL",idFieldName="PRODDISCVALID")
@BOName("B2BProductDiscountValue")
public class B2BProductDiscountValueFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCOUNTVALUE - Скидка</LI>
     * <LI>PRODDISCVALID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCVALID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductDiscountValueCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDiscountValueInsert", params);
        result.put("PRODDISCVALID", params.get("PRODDISCVALID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCOUNTVALUE - Скидка</LI>
     * <LI>PRODDISCVALID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCVALID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCVALID"})
    public Map<String,Object> dsB2BProductDiscountValueInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDiscountValueInsert", params);
        result.put("PRODDISCVALID", params.get("PRODDISCVALID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCOUNTVALUE - Скидка</LI>
     * <LI>PRODDISCVALID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCVALID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCVALID"})
    public Map<String,Object> dsB2BProductDiscountValueUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDiscountValueUpdate", params);
        result.put("PRODDISCVALID", params.get("PRODDISCVALID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCOUNTVALUE - Скидка</LI>
     * <LI>PRODDISCVALID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDISCVALID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCVALID"})
    public Map<String,Object> dsB2BProductDiscountValueModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDiscountValueUpdate", params);
        result.put("PRODDISCVALID", params.get("PRODDISCVALID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODDISCVALID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDISCVALID"})
    public void dsB2BProductDiscountValueDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductDiscountValueDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCOUNTVALUE - Скидка</LI>
     * <LI>PRODDISCVALID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DISCOUNTVALUE - Скидка</LI>
     * <LI>PRODDISCVALID - ИД</LI>
     * <LI>PRODDISCID - ИД скидки</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductDiscountValueBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductDiscountValueBrowseListByParam", "dsB2BProductDiscountValueBrowseListByParamCount", params);
        return result;
    }





}
