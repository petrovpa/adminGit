/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.sal;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности SAL_Journal
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="SAL_JOURNAL",idFieldName="ID")
@BOName("SAL_Journal")
public class SAL_JournalFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>EVENTID - Классификатор видов событий журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>ISRESOLVED - Признак Обработан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>RESOLVEDUSERID - Обработавший</LI>
     * <LI>SOURCEID - Классификатор источников событий журнала аудита безопасности</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_JournalCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_JournalInsert", params);
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
     * <LI>EVENTID - Классификатор видов событий журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>ISRESOLVED - Признак Обработан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>RESOLVEDUSERID - Обработавший</LI>
     * <LI>SOURCEID - Классификатор источников событий журнала аудита безопасности</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_JournalInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_JournalInsert", params);
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
     * <LI>EVENTID - Классификатор видов событий журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>ISRESOLVED - Признак Обработан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>RESOLVEDUSERID - Обработавший</LI>
     * <LI>SOURCEID - Классификатор источников событий журнала аудита безопасности</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_JournalUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_JournalUpdate", params);
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
     * <LI>EVENTID - Классификатор видов событий журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>ISRESOLVED - Признак Обработан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>RESOLVEDUSERID - Обработавший</LI>
     * <LI>SOURCEID - Классификатор источников событий журнала аудита безопасности</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_JournalModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_JournalUpdate", params);
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
    public void dsSAL_JournalDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsSAL_JournalDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>EVENTID - Классификатор видов событий журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>ISRESOLVED - Признак Обработан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>RESOLVEDUSERID - Обработавший</LI>
     * <LI>SOURCEID - Классификатор источников событий журнала аудита безопасности</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>EVENTID - Классификатор видов событий журнала аудита безопасности</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>ISRESOLVED - Признак Обработан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>RESOLVEDUSERID - Обработавший</LI>
     * <LI>SOURCEID - Классификатор источников событий журнала аудита безопасности</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_JournalBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsSAL_JournalBrowseListByParam", "dsSAL_JournalBrowseListByParamCount", params);
        return result;
    }





}
