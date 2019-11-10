/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.other;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BExportDataTemplate
 *
 * @author reson
 */
@IdGen(entityName="B2B_EXPORTDATA_TEMPLATE",idFieldName="TEMPLATEID")
@ProfileRights({
        @PRight(sysName="RPAccessBIV_ExportTemplate",
                name="Доступ по подразделению",
                joinStr="  inner join B2B_EXPORTDATATMPLORGSTRUCT AOS on (t.TEMPLATEID = AOS.EXPORTDATATEMPLATEID) inner join INS_DEPLVL DEPLVL on (AOS.ORGSTRUCTID = DEPLVL.OBJECTID) ",
                restrictionFieldName="DEPLVL.PARENTID",
                paramName="DEPARTMENTID")})
@BOName("B2BExportDataTemplate")
public class B2BExportDataTemplateFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ISRECORDDISABLE - null</LI>
     * <LI>SQL - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TEMPLATEID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BExportDataTemplateCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BExportDataTemplateInsert", params);
        result.put("TEMPLATEID", params.get("TEMPLATEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ISRECORDDISABLE - null</LI>
     * <LI>SQL - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TEMPLATEID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEID"})
    public Map<String,Object> dsB2BExportDataTemplateInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BExportDataTemplateInsert", params);
        result.put("TEMPLATEID", params.get("TEMPLATEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ISRECORDDISABLE - null</LI>
     * <LI>SQL - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TEMPLATEID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEID"})
    public Map<String,Object> dsB2BExportDataTemplateUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BExportDataTemplateUpdate", params);
        result.put("TEMPLATEID", params.get("TEMPLATEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ISRECORDDISABLE - null</LI>
     * <LI>SQL - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TEMPLATEID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEID"})
    public Map<String,Object> dsB2BExportDataTemplateModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BExportDataTemplateUpdate", params);
        result.put("TEMPLATEID", params.get("TEMPLATEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>TEMPLATEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEID"})
    public void dsB2BExportDataTemplateDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BExportDataTemplateDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ISRECORDDISABLE - null</LI>
     * <LI>SQL - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TEMPLATEID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ISRECORDDISABLE - null</LI>
     * <LI>SQL - null</LI>
     * <LI>SYSNAME - null</LI>
     * <LI>TEMPLATEID - null</LI>
     * <LI>TYPEID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BExportDataTemplateBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BExportDataTemplateBrowseListByParam", "dsB2BExportDataTemplateBrowseListByParamCount", params);
        return result;
    }





}
