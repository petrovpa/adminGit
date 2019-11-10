/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BRequestType
 *
 * @author reson
 */
@IdGen(entityName="B2B_REQUESTTYPE",idFieldName="REQUESTTYPEID")
@BOName("B2BRequestType")
public class B2BRequestTypeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTTYPEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTTYPEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BRequestTypeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BRequestTypeInsert", params);
        result.put("REQUESTTYPEID", params.get("REQUESTTYPEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTTYPEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTTYPEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTTYPEID"})
    public Map<String,Object> dsB2BRequestTypeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BRequestTypeInsert", params);
        result.put("REQUESTTYPEID", params.get("REQUESTTYPEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTTYPEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTTYPEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTTYPEID"})
    public Map<String,Object> dsB2BRequestTypeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BRequestTypeUpdate", params);
        result.put("REQUESTTYPEID", params.get("REQUESTTYPEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTTYPEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTTYPEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTTYPEID"})
    public Map<String,Object> dsB2BRequestTypeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BRequestTypeUpdate", params);
        result.put("REQUESTTYPEID", params.get("REQUESTTYPEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTTYPEID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTTYPEID"})
    public void dsB2BRequestTypeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BRequestTypeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTTYPEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTTYPEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BRequestTypeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BRequestTypeBrowseListByParam", "dsB2BRequestTypeBrowseListByParamCount", params);
        return result;
    }





}
