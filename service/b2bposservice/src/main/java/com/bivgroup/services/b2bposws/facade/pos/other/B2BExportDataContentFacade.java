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
 * Фасад для сущности B2BExportDataContent
 *
 * @author reson
 */
@IdGen(entityName="B2B_EXPORTDATA_CONTENT",idFieldName="CONTENTID")
@BOName("B2BExportDataContent")
public class B2BExportDataContentFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTENTID - null</LI>
     * <LI>EXPORTDATAID - null</LI>
     * <LI>OBJECTID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTENTID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BExportDataContentCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BExportDataContentInsert", params);
        result.put("CONTENTID", params.get("CONTENTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTENTID - null</LI>
     * <LI>EXPORTDATAID - null</LI>
     * <LI>OBJECTID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTENTID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTENTID"})
    public Map<String,Object> dsB2BExportDataContentInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BExportDataContentInsert", params);
        result.put("CONTENTID", params.get("CONTENTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTENTID - null</LI>
     * <LI>EXPORTDATAID - null</LI>
     * <LI>OBJECTID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTENTID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTENTID"})
    public Map<String,Object> dsB2BExportDataContentUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BExportDataContentUpdate", params);
        result.put("CONTENTID", params.get("CONTENTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTENTID - null</LI>
     * <LI>EXPORTDATAID - null</LI>
     * <LI>OBJECTID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTENTID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTENTID"})
    public Map<String,Object> dsB2BExportDataContentModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BExportDataContentUpdate", params);
        result.put("CONTENTID", params.get("CONTENTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTENTID - null</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTENTID"})
    public void dsB2BExportDataContentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BExportDataContentDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTENTID - null</LI>
     * <LI>EXPORTDATAID - null</LI>
     * <LI>OBJECTID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTENTID - null</LI>
     * <LI>EXPORTDATAID - null</LI>
     * <LI>OBJECTID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BExportDataContentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BExportDataContentBrowseListByParam", "dsB2BExportDataContentBrowseListByParamCount", params);
        return result;
    }





}
