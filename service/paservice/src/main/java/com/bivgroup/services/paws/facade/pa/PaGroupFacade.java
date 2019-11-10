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
 * Фасад для сущности PaGroup
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="PA_GROUP",idFieldName="GROUPID")
@BOName("PaGroup")
public class PaGroupFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Пользователь обновивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>GROUPID - Ид группы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsPaGroupCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPaGroupInsert", params);
        result.put("GROUPID", params.get("GROUPID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Пользователь обновивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>GROUPID - Ид группы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"GROUPID"})
    public Map<String,Object> dsPaGroupInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPaGroupInsert", params);
        result.put("GROUPID", params.get("GROUPID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Пользователь обновивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>GROUPID - Ид группы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"GROUPID"})
    public Map<String,Object> dsPaGroupUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPaGroupUpdate", params);
        result.put("GROUPID", params.get("GROUPID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Пользователь обновивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>GROUPID - Ид группы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"GROUPID"})
    public Map<String,Object> dsPaGroupModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPaGroupUpdate", params);
        result.put("GROUPID", params.get("GROUPID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>GROUPID - Ид группы</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"GROUPID"})
    public void dsPaGroupDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsPaGroupDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Пользователь обновивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>GROUPID - Ид группы</LI>
     * <LI>NAME - Имя</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * <LI>UPDATEUSERID - Пользователь обновивший запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsPaGroupBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsPaGroupBrowseListByParam", "dsPaGroupBrowseListByParamCount", params);
        return result;
    }





}
