/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.wsws.facade.ws;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности WSAuthMethod
 *
 * @author reson
 */
@IdGen(entityName="WS_AUTHMETHOD",idFieldName="AUTHMETHODID")
@BOName("WSAuthMethod")
public class WSAuthMethodFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>AUTHMETHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AUTHMETHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSAuthMethodCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSAuthMethodInsert", params);
        result.put("AUTHMETHODID", params.get("AUTHMETHODID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>AUTHMETHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AUTHMETHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"AUTHMETHODID"})
    public Map<String,Object> dsWSAuthMethodInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSAuthMethodInsert", params);
        result.put("AUTHMETHODID", params.get("AUTHMETHODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AUTHMETHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AUTHMETHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"AUTHMETHODID"})
    public Map<String,Object> dsWSAuthMethodUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSAuthMethodUpdate", params);
        result.put("AUTHMETHODID", params.get("AUTHMETHODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AUTHMETHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AUTHMETHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"AUTHMETHODID"})
    public Map<String,Object> dsWSAuthMethodModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSAuthMethodUpdate", params);
        result.put("AUTHMETHODID", params.get("AUTHMETHODID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>AUTHMETHODID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"AUTHMETHODID"})
    public void dsWSAuthMethodDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsWSAuthMethodDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>AUTHMETHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AUTHMETHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSAuthMethodBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsWSAuthMethodBrowseListByParam", "dsWSAuthMethodBrowseListByParamCount", params);
        return result;
    }





}
