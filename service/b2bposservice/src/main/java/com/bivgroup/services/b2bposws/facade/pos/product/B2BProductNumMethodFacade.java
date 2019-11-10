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
 * Фасад для сущности B2BProductNumMethod
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODNUMMETHOD",idFieldName="PRODNUMMETHODID")
@BOName("B2BProductNumMethod")
public class B2BProductNumMethodFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODNUMMETHODID - ИД</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODNUMMETHODID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductNumMethodCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductNumMethodInsert", params);
        result.put("PRODNUMMETHODID", params.get("PRODNUMMETHODID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODNUMMETHODID - ИД</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODNUMMETHODID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODNUMMETHODID"})
    public Map<String,Object> dsB2BProductNumMethodInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductNumMethodInsert", params);
        result.put("PRODNUMMETHODID", params.get("PRODNUMMETHODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODNUMMETHODID - ИД</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODNUMMETHODID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODNUMMETHODID"})
    public Map<String,Object> dsB2BProductNumMethodUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductNumMethodUpdate", params);
        result.put("PRODNUMMETHODID", params.get("PRODNUMMETHODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODNUMMETHODID - ИД</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODNUMMETHODID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODNUMMETHODID"})
    public Map<String,Object> dsB2BProductNumMethodModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductNumMethodUpdate", params);
        result.put("PRODNUMMETHODID", params.get("PRODNUMMETHODID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODNUMMETHODID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODNUMMETHODID"})
    public void dsB2BProductNumMethodDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductNumMethodDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODNUMMETHODID - ИД</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODNUMMETHODID - ИД</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductNumMethodBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductNumMethodBrowseListByParam", "dsB2BProductNumMethodBrowseListByParamCount", params);
        return result;
    }





}
