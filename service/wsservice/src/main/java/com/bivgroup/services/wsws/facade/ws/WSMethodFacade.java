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
 * Фасад для сущности WSMethod
 *
 * @author reson
 */
@IdGen(entityName="WS_METHOD",idFieldName="METHODID")
@BOName("WSMethod")
public class WSMethodFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>METHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SERVICEID - ИД сервиса</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>METHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSMethodCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSMethodInsert", params);
        result.put("METHODID", params.get("METHODID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>METHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SERVICEID - ИД сервиса</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>METHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"METHODID"})
    public Map<String,Object> dsWSMethodInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSMethodInsert", params);
        result.put("METHODID", params.get("METHODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>METHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SERVICEID - ИД сервиса</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>METHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"METHODID"})
    public Map<String,Object> dsWSMethodUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSMethodUpdate", params);
        result.put("METHODID", params.get("METHODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>METHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SERVICEID - ИД сервиса</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>METHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"METHODID"})
    public Map<String,Object> dsWSMethodModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSMethodUpdate", params);
        result.put("METHODID", params.get("METHODID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>METHODID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"METHODID"})
    public void dsWSMethodDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsWSMethodDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>METHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SERVICEID - ИД сервиса</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>METHODID - ИД записи</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SERVICEID - ИД сервиса</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSMethodBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsWSMethodBrowseListByParam", "dsWSMethodBrowseListByParamCount", params);
        return result;
    }





}
