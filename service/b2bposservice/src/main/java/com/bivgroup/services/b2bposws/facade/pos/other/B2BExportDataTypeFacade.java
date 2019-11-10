/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.other;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BExportDataType
 *
 * @author reson
 */
@IdGen(entityName="B2B_EXPORTDATA_TYPE",idFieldName="TYPEID")
@BOName("B2BExportDataType")
public class B2BExportDataTypeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>NAME - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TYPEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BExportDataTypeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BExportDataTypeInsert", params);
        result.put("TYPEID", params.get("TYPEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>NAME - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TYPEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TYPEID"})
    public Map<String,Object> dsB2BExportDataTypeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BExportDataTypeInsert", params);
        result.put("TYPEID", params.get("TYPEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>NAME - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TYPEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TYPEID"})
    public Map<String,Object> dsB2BExportDataTypeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BExportDataTypeUpdate", params);
        result.put("TYPEID", params.get("TYPEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>NAME - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TYPEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TYPEID"})
    public Map<String,Object> dsB2BExportDataTypeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BExportDataTypeUpdate", params);
        result.put("TYPEID", params.get("TYPEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"TYPEID"})
    public void dsB2BExportDataTypeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BExportDataTypeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>NAME - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>NAME - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BExportDataTypeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BExportDataTypeBrowseListByParam", "dsB2BExportDataTypeBrowseListByParamCount", params);
        return result;
    }





}
