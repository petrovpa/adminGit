/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.journals;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2B_Journal
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_JOURNAL",idFieldName="ID")
@BOName("B2B_Journal")
public class B2B_JournalFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATAPROVIDERID - Ссылка на дата провайдер</LI>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название журнала</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования табличной части журнала</LI>
     * <LI>SYSNAME - содержит системное имя журнала</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_JournalCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_JournalInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATAPROVIDERID - Ссылка на дата провайдер</LI>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название журнала</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования табличной части журнала</LI>
     * <LI>SYSNAME - содержит системное имя журнала</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_JournalInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_JournalInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATAPROVIDERID - Ссылка на дата провайдер</LI>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название журнала</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования табличной части журнала</LI>
     * <LI>SYSNAME - содержит системное имя журнала</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_JournalUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_JournalUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATAPROVIDERID - Ссылка на дата провайдер</LI>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название журнала</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования табличной части журнала</LI>
     * <LI>SYSNAME - содержит системное имя журнала</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_JournalModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_JournalUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public void dsB2B_JournalDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2B_JournalDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATAPROVIDERID - Ссылка на дата провайдер</LI>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название журнала</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования табличной части журнала</LI>
     * <LI>SYSNAME - содержит системное имя журнала</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>DATAPROVIDERID - Ссылка на дата провайдер</LI>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название журнала</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования табличной части журнала</LI>
     * <LI>SYSNAME - содержит системное имя журнала</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_JournalBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2B_JournalBrowseListByParam", "dsB2B_JournalBrowseListByParamCount", params);
        return result;
    }





}
