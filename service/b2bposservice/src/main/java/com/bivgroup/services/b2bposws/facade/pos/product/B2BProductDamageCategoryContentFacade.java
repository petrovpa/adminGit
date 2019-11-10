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
 * Фасад для сущности B2BProductDamageCategoryContent
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODDAMAGECATCNT",idFieldName="PRODDAMAGECATCNTID")
@BOName("B2BProductDamageCategoryContent")
public class B2BProductDamageCategoryContentFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODDAMAGECATCNTID - ИД состава категории ущерба по продукту</LI>
     * <LI>INSEVENTID - ИД вида страхового события</LI>
     * <LI>MINRZUVALUE - Сумма первой РЗУ</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба по убытку</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDAMAGECATCNTID - ИД состава категории ущерба по продукту</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductDamageCategoryContentCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDamageCategoryContentInsert", params);
        result.put("PRODDAMAGECATCNTID", params.get("PRODDAMAGECATCNTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODDAMAGECATCNTID - ИД состава категории ущерба по продукту</LI>
     * <LI>INSEVENTID - ИД вида страхового события</LI>
     * <LI>MINRZUVALUE - Сумма первой РЗУ</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба по убытку</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDAMAGECATCNTID - ИД состава категории ущерба по продукту</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDAMAGECATCNTID"})
    public Map<String,Object> dsB2BProductDamageCategoryContentInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDamageCategoryContentInsert", params);
        result.put("PRODDAMAGECATCNTID", params.get("PRODDAMAGECATCNTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODDAMAGECATCNTID - ИД состава категории ущерба по продукту</LI>
     * <LI>INSEVENTID - ИД вида страхового события</LI>
     * <LI>MINRZUVALUE - Сумма первой РЗУ</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба по убытку</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDAMAGECATCNTID - ИД состава категории ущерба по продукту</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDAMAGECATCNTID"})
    public Map<String,Object> dsB2BProductDamageCategoryContentUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDamageCategoryContentUpdate", params);
        result.put("PRODDAMAGECATCNTID", params.get("PRODDAMAGECATCNTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODDAMAGECATCNTID - ИД состава категории ущерба по продукту</LI>
     * <LI>INSEVENTID - ИД вида страхового события</LI>
     * <LI>MINRZUVALUE - Сумма первой РЗУ</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба по убытку</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDAMAGECATCNTID - ИД состава категории ущерба по продукту</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDAMAGECATCNTID"})
    public Map<String,Object> dsB2BProductDamageCategoryContentModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDamageCategoryContentUpdate", params);
        result.put("PRODDAMAGECATCNTID", params.get("PRODDAMAGECATCNTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODDAMAGECATCNTID - ИД состава категории ущерба по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDAMAGECATCNTID"})
    public void dsB2BProductDamageCategoryContentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductDamageCategoryContentDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODDAMAGECATCNTID - ИД состава категории ущерба по продукту</LI>
     * <LI>INSEVENTID - ИД вида страхового события</LI>
     * <LI>MINRZUVALUE - Сумма первой РЗУ</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба по убытку</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDAMAGECATCNTID - ИД состава категории ущерба по продукту</LI>
     * <LI>INSEVENTID - ИД вида страхового события</LI>
     * <LI>MINRZUVALUE - Сумма первой РЗУ</LI>
     * <LI>PRODDAMAGECATID - ИД категории ущерба по убытку</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductDamageCategoryContentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductDamageCategoryContentBrowseListByParam", "dsB2BProductDamageCategoryContentBrowseListByParamCount", params);
        return result;
    }





}
