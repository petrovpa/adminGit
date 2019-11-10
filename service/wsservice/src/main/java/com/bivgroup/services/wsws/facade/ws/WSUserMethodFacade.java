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
 * Фасад для сущности WSUserMethod
 *
 * @author reson
 */
@IdGen(entityName="WS_USERMETHOD",idFieldName="USERMETHODID")
@BOName("WSUserMethod")
public class WSUserMethodFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>USERMETHODID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERMETHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSUserMethodCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSUserMethodInsert", params);
        result.put("USERMETHODID", params.get("USERMETHODID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>USERMETHODID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERMETHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERMETHODID"})
    public Map<String,Object> dsWSUserMethodInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSUserMethodInsert", params);
        result.put("USERMETHODID", params.get("USERMETHODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>USERMETHODID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERMETHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERMETHODID"})
    public Map<String,Object> dsWSUserMethodUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSUserMethodUpdate", params);
        result.put("USERMETHODID", params.get("USERMETHODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>USERMETHODID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERMETHODID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERMETHODID"})
    public Map<String,Object> dsWSUserMethodModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSUserMethodUpdate", params);
        result.put("USERMETHODID", params.get("USERMETHODID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>USERMETHODID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERMETHODID"})
    public void dsWSUserMethodDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsWSUserMethodDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>USERMETHODID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERMETHODID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSUserMethodBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsWSUserMethodBrowseListByParam", "dsWSUserMethodBrowseListByParamCount", params);
        return result;
    }





}
