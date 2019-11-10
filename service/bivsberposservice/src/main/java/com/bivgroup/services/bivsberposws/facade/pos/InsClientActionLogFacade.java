/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности InsClientActionLog
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="INS_CLIENTACTLOG",idFieldName="CLIENTACTLOGID")
@BOName("InsClientActionLog")
public class InsClientActionLogFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTION - совершенное действие</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * <LI>NOTE - Описание действия</LI>
     * <LI>PARAM1 - null</LI>
     * <LI>PARAM2 - null</LI>
     * <LI>PARAM3 - null</LI>
     * <LI>SESSIONID - ид сессии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsInsClientActionLogCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsClientActionLogInsert", params);
        result.put("CLIENTACTLOGID", params.get("CLIENTACTLOGID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTION - совершенное действие</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * <LI>NOTE - Описание действия</LI>
     * <LI>PARAM1 - null</LI>
     * <LI>PARAM2 - null</LI>
     * <LI>PARAM3 - null</LI>
     * <LI>SESSIONID - ид сессии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CLIENTACTLOGID"})
    public Map<String,Object> dsInsClientActionLogInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsClientActionLogInsert", params);
        result.put("CLIENTACTLOGID", params.get("CLIENTACTLOGID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTION - совершенное действие</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * <LI>NOTE - Описание действия</LI>
     * <LI>PARAM1 - null</LI>
     * <LI>PARAM2 - null</LI>
     * <LI>PARAM3 - null</LI>
     * <LI>SESSIONID - ид сессии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CLIENTACTLOGID"})
    public Map<String,Object> dsInsClientActionLogUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsClientActionLogUpdate", params);
        result.put("CLIENTACTLOGID", params.get("CLIENTACTLOGID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTION - совершенное действие</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * <LI>NOTE - Описание действия</LI>
     * <LI>PARAM1 - null</LI>
     * <LI>PARAM2 - null</LI>
     * <LI>PARAM3 - null</LI>
     * <LI>SESSIONID - ид сессии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CLIENTACTLOGID"})
    public Map<String,Object> dsInsClientActionLogModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsClientActionLogUpdate", params);
        result.put("CLIENTACTLOGID", params.get("CLIENTACTLOGID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CLIENTACTLOGID"})
    public void dsInsClientActionLogDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsInsClientActionLogDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTION - совершенное действие</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * <LI>NOTE - Описание действия</LI>
     * <LI>PARAM1 - null</LI>
     * <LI>PARAM2 - null</LI>
     * <LI>PARAM3 - null</LI>
     * <LI>SESSIONID - ид сессии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ACTION - совершенное действие</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CLIENTACTLOGID - ИД записи лога</LI>
     * <LI>NOTE - Описание действия</LI>
     * <LI>PARAM1 - null</LI>
     * <LI>PARAM2 - null</LI>
     * <LI>PARAM3 - null</LI>
     * <LI>SESSIONID - ид сессии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsInsClientActionLogBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsInsClientActionLogBrowseListByParam", "dsInsClientActionLogBrowseListByParamCount", params);
        return result;
    }





}
