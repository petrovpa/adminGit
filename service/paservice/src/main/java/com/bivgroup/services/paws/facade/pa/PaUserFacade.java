/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.paws.facade.pa;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности PaUser
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="PA_USER",idFieldName="USERID")
@BOName("PaUser")
public class PaUserFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EMAIL - Емайл</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PHONENUMBER - Номер телефона</LI>
     * <LI>SMSCODE - Код смс</LI>
     * <LI>SMSCODEDATE - Дата отправки смс кода</LI>
     * <LI>STATUSDATE - Дата перевода статуса</LI>
     * <LI>STATUSID - ИД статуса</LI>
     * <LI>STATUSSYSNAME - Системное наименование статуса</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * <LI>USERID - Ид пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERID - Ид пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsPaUserCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPaUserInsert", params);
        result.put("USERID", params.get("USERID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EMAIL - Емайл</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PHONENUMBER - Номер телефона</LI>
     * <LI>SMSCODE - Код смс</LI>
     * <LI>SMSCODEDATE - Дата отправки смс кода</LI>
     * <LI>STATUSDATE - Дата перевода статуса</LI>
     * <LI>STATUSID - ИД статуса</LI>
     * <LI>STATUSSYSNAME - Системное наименование статуса</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * <LI>USERID - Ид пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERID - Ид пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERID"})
    public Map<String,Object> dsPaUserInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPaUserInsert", params);
        result.put("USERID", params.get("USERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EMAIL - Емайл</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PHONENUMBER - Номер телефона</LI>
     * <LI>SMSCODE - Код смс</LI>
     * <LI>SMSCODEDATE - Дата отправки смс кода</LI>
     * <LI>STATUSDATE - Дата перевода статуса</LI>
     * <LI>STATUSID - ИД статуса</LI>
     * <LI>STATUSSYSNAME - Системное наименование статуса</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * <LI>USERID - Ид пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERID - Ид пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERID"})
    public Map<String,Object> dsPaUserUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPaUserUpdate", params);
        result.put("USERID", params.get("USERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EMAIL - Емайл</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PHONENUMBER - Номер телефона</LI>
     * <LI>SMSCODE - Код смс</LI>
     * <LI>SMSCODEDATE - Дата отправки смс кода</LI>
     * <LI>STATUSDATE - Дата перевода статуса</LI>
     * <LI>STATUSID - ИД статуса</LI>
     * <LI>STATUSSYSNAME - Системное наименование статуса</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * <LI>USERID - Ид пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERID - Ид пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERID"})
    public Map<String,Object> dsPaUserModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPaUserUpdate", params);
        result.put("USERID", params.get("USERID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>USERID - Ид пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERID"})
    public void dsPaUserDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsPaUserDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EMAIL - Емайл</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PHONENUMBER - Номер телефона</LI>
     * <LI>SMSCODE - Код смс</LI>
     * <LI>SMSCODEDATE - Дата отправки смс кода</LI>
     * <LI>STATUSDATE - Дата перевода статуса</LI>
     * <LI>STATUSID - ИД статуса</LI>
     * <LI>STATUSSYSNAME - Системное наименование статуса</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * <LI>USERID - Ид пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>EMAIL - Емайл</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PHONENUMBER - Номер телефона</LI>
     * <LI>SMSCODE - Код смс</LI>
     * <LI>SMSCODEDATE - Дата отправки смс кода</LI>
     * <LI>STATUSDATE - Дата перевода статуса</LI>
     * <LI>STATUSID - ИД статуса</LI>
     * <LI>STATUSSYSNAME - Системное наименование статуса</LI>
     * <LI>SURNAME - Фамилия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший</LI>
     * <LI>USERID - Ид пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsPaUserBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsPaUserBrowseListByParam", "dsPaUserBrowseListByParamCount", params);
        return result;
    }





}
