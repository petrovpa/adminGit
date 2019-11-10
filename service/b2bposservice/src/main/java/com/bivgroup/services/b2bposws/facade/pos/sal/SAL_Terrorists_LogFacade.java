/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.sal;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности SAL_Terrorists_Log
 *
 * @author reson
 */
@State(idFieldName = "ID", startStateName = "SAL_TERRORISTS_LOG_NEW", typeSysName = "SAL_TERRORISTS_LOG")
@IdGen(entityName="SAL_TERRORISTS_LOG",idFieldName="ID")
@BOName("SAL_Terrorists_Log")
public class SAL_Terrorists_LogFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NODEID - ИД ноды</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_Terrorists_LogCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_Terrorists_LogInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NODEID - ИД ноды</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Terrorists_LogInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_Terrorists_LogInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NODEID - ИД ноды</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Terrorists_LogUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_Terrorists_LogUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NODEID - ИД ноды</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Terrorists_LogModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_Terrorists_LogUpdate", params);
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
    public void dsSAL_Terrorists_LogDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsSAL_Terrorists_LogDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NODEID - ИД ноды</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NODEID - ИД ноды</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>UPDATEDATE - Дата обновления</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_Terrorists_LogBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsSAL_Terrorists_LogBrowseListByParam", "dsSAL_Terrorists_LogBrowseListByParamCount", params);
        return result;
    }





}
