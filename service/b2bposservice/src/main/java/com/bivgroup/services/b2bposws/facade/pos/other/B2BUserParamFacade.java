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
 * Фасад для сущности B2BUserParam
 *
 * @author reson
 */
@IdGen(entityName="B2B_USERPARAM",idFieldName="USERPARAMID")
@BOName("B2BUserParam")
public class B2BUserParamFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ENTITYKEY - Идентификатор сохраняемого параметра</LI>
     * <LI>ENTITYTYPE - Тип сущности</LI>
     * <LI>USERACCOUNTID - Ссылка на аккаунт пользователя</LI>
     * <LI>USERPARAMID - Первичный ключ</LI>
     * <LI>VALUE - Значение параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERACCOUNTID - Ссылка на аккаунт пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BUserParamCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BUserParamInsert", params);
        result.put("USERPARAMID", params.get("USERPARAMID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ENTITYKEY - Идентификатор сохраняемого параметра</LI>
     * <LI>ENTITYTYPE - Тип сущности</LI>
     * <LI>USERACCOUNTID - Ссылка на аккаунт пользователя</LI>
     * <LI>USERPARAMID - Первичный ключ</LI>
     * <LI>VALUE - Значение параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERACCOUNTID - Ссылка на аккаунт пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERPARAMID"})
    public Map<String,Object> dsB2BUserParamInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BUserParamInsert", params);
        result.put("USERPARAMID", params.get("USERPARAMID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ENTITYKEY - Идентификатор сохраняемого параметра</LI>
     * <LI>ENTITYTYPE - Тип сущности</LI>
     * <LI>USERACCOUNTID - Ссылка на аккаунт пользователя</LI>
     * <LI>USERPARAMID - Первичный ключ</LI>
     * <LI>VALUE - Значение параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERACCOUNTID - Ссылка на аккаунт пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERPARAMID"})
    public Map<String,Object> dsB2BUserParamUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BUserParamUpdate", params);
        result.put("USERPARAMID", params.get("USERPARAMID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ENTITYKEY - Идентификатор сохраняемого параметра</LI>
     * <LI>ENTITYTYPE - Тип сущности</LI>
     * <LI>USERACCOUNTID - Ссылка на аккаунт пользователя</LI>
     * <LI>USERPARAMID - Первичный ключ</LI>
     * <LI>VALUE - Значение параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERACCOUNTID - Ссылка на аккаунт пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERPARAMID"})
    public Map<String,Object> dsB2BUserParamModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BUserParamUpdate", params);
        result.put("USERPARAMID", params.get("USERPARAMID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>USERACCOUNTID - Ссылка на аккаунт пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERPARAMID"})
    public void dsB2BUserParamDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BUserParamDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ENTITYKEY - Идентификатор сохраняемого параметра</LI>
     * <LI>ENTITYTYPE - Тип сущности</LI>
     * <LI>USERACCOUNTID - Ссылка на аккаунт пользователя</LI>
     * <LI>USERPARAMID - Первичный ключ</LI>
     * <LI>VALUE - Значение параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ENTITYKEY - Идентификатор сохраняемого параметра</LI>
     * <LI>ENTITYTYPE - Тип сущности</LI>
     * <LI>USERACCOUNTID - Ссылка на аккаунт пользователя</LI>
     * <LI>USERPARAMID - Первичный ключ</LI>
     * <LI>VALUE - Значение параметра</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BUserParamBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BUserParamBrowseListByParam", "dsB2BUserParamBrowseListByParamCount", params);
        return result;
    }

}
