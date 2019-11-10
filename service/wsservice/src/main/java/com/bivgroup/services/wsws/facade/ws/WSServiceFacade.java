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
 * Фасад для сущности WSService
 *
 * @author reson
 */
@IdGen(entityName="WS_SERVICE",idFieldName="SERVICEID")
@BOName("WSService")
public class WSServiceFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>SERVICEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SERVICEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSServiceCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSServiceInsert", params);
        result.put("SERVICEID", params.get("SERVICEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>SERVICEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SERVICEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"SERVICEID"})
    public Map<String,Object> dsWSServiceInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSServiceInsert", params);
        result.put("SERVICEID", params.get("SERVICEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>SERVICEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SERVICEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"SERVICEID"})
    public Map<String,Object> dsWSServiceUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSServiceUpdate", params);
        result.put("SERVICEID", params.get("SERVICEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>SERVICEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SERVICEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"SERVICEID"})
    public Map<String,Object> dsWSServiceModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSServiceUpdate", params);
        result.put("SERVICEID", params.get("SERVICEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>SERVICEID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"SERVICEID"})
    public void dsWSServiceDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsWSServiceDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>SERVICEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>SERVICEID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSServiceBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsWSServiceBrowseListByParam", "dsWSServiceBrowseListByParamCount", params);
        return result;
    }





}
