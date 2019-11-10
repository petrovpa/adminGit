/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.sal;


import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности SAL_InvalidPassportsNode
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@State(idFieldName = "ID", startStateName = "SAL_INVALIDPASSPORTSNODE_NEW", typeSysName = "SAL_INVALIDPASSPORTSNODE")
@IdGen(entityName="SAL_INVALIDPASSPORTSNODE",idFieldName="ID")
@BOName("SAL_InvalidPassportsNode")
public class SAL_InvalidPassportsNodeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>STATEID - ИД состояния</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>VERSION - Номер версии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_InvalidPassportsNodeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_InvalidPassportsNodeInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>STATEID - ИД состояния</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>VERSION - Номер версии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_InvalidPassportsNodeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_InvalidPassportsNodeInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>STATEID - ИД состояния</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>VERSION - Номер версии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_InvalidPassportsNodeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_InvalidPassportsNodeUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>STATEID - ИД состояния</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>VERSION - Номер версии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_InvalidPassportsNodeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_InvalidPassportsNodeUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public void dsSAL_InvalidPassportsNodeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsSAL_InvalidPassportsNodeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>STATEID - ИД состояния</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>VERSION - Номер версии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>STATEID - ИД состояния</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>VERSION - Номер версии</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_InvalidPassportsNodeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsSAL_InvalidPassportsNodeBrowseListByParam", "dsSAL_InvalidPassportsNodeBrowseListByParamCount", params);
        return result;
    }





}
