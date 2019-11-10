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
 * Фасад для сущности WSUser
 *
 * @author reson
 */
@IdGen(entityName="WS_USER",idFieldName="USERID")
@BOName("WSUser")
public class WSUserFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>AUTHMETHODID - ИД метода авторизации</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>USERID - ИД записи</LI>
     * <LI>LOGIN - Логин пользователя</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PASSWORD - Пароль пользователя</LI>
     * <LI>PWDEXPDATE - Дата истечения срока действия пароля</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSUserCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSUserInsert", params);
        result.put("USERID", params.get("USERID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>AUTHMETHODID - ИД метода авторизации</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>USERID - ИД записи</LI>
     * <LI>LOGIN - Логин пользователя</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PASSWORD - Пароль пользователя</LI>
     * <LI>PWDEXPDATE - Дата истечения срока действия пароля</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERID"})
    public Map<String,Object> dsWSUserInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSUserInsert", params);
        result.put("USERID", params.get("USERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AUTHMETHODID - ИД метода авторизации</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>USERID - ИД записи</LI>
     * <LI>LOGIN - Логин пользователя</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PASSWORD - Пароль пользователя</LI>
     * <LI>PWDEXPDATE - Дата истечения срока действия пароля</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERID"})
    public Map<String,Object> dsWSUserUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSUserUpdate", params);
        result.put("USERID", params.get("USERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AUTHMETHODID - ИД метода авторизации</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>USERID - ИД записи</LI>
     * <LI>LOGIN - Логин пользователя</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PASSWORD - Пароль пользователя</LI>
     * <LI>PWDEXPDATE - Дата истечения срока действия пароля</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERID"})
    public Map<String,Object> dsWSUserModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSUserUpdate", params);
        result.put("USERID", params.get("USERID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>USERID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERID"})
    public void dsWSUserDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsWSUserDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>AUTHMETHODID - ИД метода авторизации</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>USERID - ИД записи</LI>
     * <LI>LOGIN - Логин пользователя</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PASSWORD - Пароль пользователя</LI>
     * <LI>PWDEXPDATE - Дата истечения срока действия пароля</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AUTHMETHODID - ИД метода авторизации</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>USERID - ИД записи</LI>
     * <LI>LOGIN - Логин пользователя</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PASSWORD - Пароль пользователя</LI>
     * <LI>PWDEXPDATE - Дата истечения срока действия пароля</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSUserBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsWSUserBrowseListByParam", "dsWSUserBrowseListByParamCount", params);
        return result;
    }





}
