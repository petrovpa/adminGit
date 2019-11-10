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
 * Фасад для сущности B2BExportDataOrgStruct
 *
 * @author reson
 */
@IdGen(entityName="B2B_EXPORTDATAORGSTRUCT",idFieldName="EXPORTDATAORGSTRUCTID")
@BOName("B2BExportDataOrgStruct")
public class B2BExportDataOrgStructFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>EXPORTDATAID - ИД экспорта</LI>
     * <LI>EXPORTDATAORGSTRUCTID - Первичный ключ</LI>
     * <LI>ORGSTRUCTID - Ид департамента</LI>
     * <LI>ROLEID - Ид роли</LI>
     * <LI>USERID - Пользователь</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>EXPORTDATAORGSTRUCTID - Первичный ключ</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BExportDataOrgStructCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BExportDataOrgStructInsert", params);
        result.put("EXPORTDATAORGSTRUCTID", params.get("EXPORTDATAORGSTRUCTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>EXPORTDATAID - ИД экспорта</LI>
     * <LI>EXPORTDATAORGSTRUCTID - Первичный ключ</LI>
     * <LI>ORGSTRUCTID - Ид департамента</LI>
     * <LI>ROLEID - Ид роли</LI>
     * <LI>USERID - Пользователь</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>EXPORTDATAORGSTRUCTID - Первичный ключ</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"EXPORTDATAID"})
    public Map<String,Object> dsB2BExportDataOrgStructInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BExportDataOrgStructInsert", params);
        result.put("EXPORTDATAORGSTRUCTID", params.get("EXPORTDATAORGSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>EXPORTDATAID - ИД экспорта</LI>
     * <LI>EXPORTDATAORGSTRUCTID - Первичный ключ</LI>
     * <LI>ORGSTRUCTID - Ид департамента</LI>
     * <LI>ROLEID - Ид роли</LI>
     * <LI>USERID - Пользователь</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>EXPORTDATAORGSTRUCTID - Первичный ключ</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"EXPORTDATAID"})
    public Map<String,Object> dsB2BExportDataOrgStructUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BExportDataOrgStructUpdate", params);
        result.put("EXPORTDATAORGSTRUCTID", params.get("EXPORTDATAORGSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>EXPORTDATAID - ИД экспорта</LI>
     * <LI>EXPORTDATAORGSTRUCTID - Первичный ключ</LI>
     * <LI>ORGSTRUCTID - Ид департамента</LI>
     * <LI>ROLEID - Ид роли</LI>
     * <LI>USERID - Пользователь</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>EXPORTDATAORGSTRUCTID - Первичный ключ</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"EXPORTDATAID"})
    public Map<String,Object> dsB2BExportDataOrgStructModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BExportDataOrgStructUpdate", params);
        result.put("EXPORTDATAORGSTRUCTID", params.get("EXPORTDATAORGSTRUCTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>EXPORTDATAORGSTRUCTID - Первичный ключ</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"EXPORTDATAID"})
    public void dsB2BExportDataOrgStructDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BExportDataOrgStructDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>EXPORTDATAID - ИД экспорта</LI>
     * <LI>EXPORTDATAORGSTRUCTID - Первичный ключ</LI>
     * <LI>ORGSTRUCTID - Ид департамента</LI>
     * <LI>ROLEID - Ид роли</LI>
     * <LI>USERID - Пользователь</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>EXPORTDATAID - ИД экспорта</LI>
     * <LI>EXPORTDATAORGSTRUCTID - Первичный ключ</LI>
     * <LI>ORGSTRUCTID - Ид департамента</LI>
     * <LI>ROLEID - Ид роли</LI>
     * <LI>USERID - Пользователь</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BExportDataOrgStructBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BExportDataOrgStructBrowseListByParam", "dsB2BExportDataOrgStructBrowseListByParamCount", params);
        return result;
    }





}
