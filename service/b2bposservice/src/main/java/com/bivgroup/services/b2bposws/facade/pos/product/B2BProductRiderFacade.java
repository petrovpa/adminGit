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
 * Фасад для сущности B2BProductRider
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODRIDER",idFieldName="PRODRIDERID")
@BOName("B2BProductRider")
public class B2BProductRiderFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODRIDERID - ИД райдера</LI>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODRIDERID - ИД райдера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODID"})
    public Map<String,Object> dsB2BProductRiderCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductRiderInsert", params);
        result.put("PRODRIDERID", params.get("PRODRIDERID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODRIDERID - ИД райдера</LI>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODRIDERID - ИД райдера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODRIDERID", "PRODID"})
    public Map<String,Object> dsB2BProductRiderInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductRiderInsert", params);
        result.put("PRODRIDERID", params.get("PRODRIDERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODRIDERID - ИД райдера</LI>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODRIDERID - ИД райдера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODRIDERID"})
    public Map<String,Object> dsB2BProductRiderUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductRiderUpdate", params);
        result.put("PRODRIDERID", params.get("PRODRIDERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODRIDERID - ИД райдера</LI>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODRIDERID - ИД райдера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODRIDERID"})
    public Map<String,Object> dsB2BProductRiderModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductRiderUpdate", params);
        result.put("PRODRIDERID", params.get("PRODRIDERID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODRIDERID - ИД райдера</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODRIDERID"})
    public void dsB2BProductRiderDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductRiderDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODRIDERID - ИД райдера</LI>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODRIDERID - ИД райдера</LI>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductRiderBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductRiderBrowseListByParam", "dsB2BProductRiderBrowseListByParamCount", params);
        return result;
    }





}
