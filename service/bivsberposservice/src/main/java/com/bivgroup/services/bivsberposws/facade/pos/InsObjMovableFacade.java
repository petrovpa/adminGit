/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.version.NodeVersion;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности InsObjMovable
 *
 * @author reson
 */
@Discriminator(1004)
@Auth(onlyCreatorAccess = false)
@NodeVersion(nodeTableName="INS_INSOBJNODE",nodeTableIdFieldName="INSOBJNODEID",versionNumberParamName="VERNUMBER",nodeLastVersionNumberFieldName="LASTVERNUMBER",nodeRVersionFieldName="RVERSION")
@IdGen(entityName="INS_INSOBJ",idFieldName="INSOBJID")
@BOName("InsObjMovable")
public class InsObjMovableFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRPROPERTYID - ИД описания имущества</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - ФИО</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSOBJID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSOBJNODEID"})
    public Map<String,Object> dsInsObjMovableCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsObjMovableInsert", params);
        result.put("INSOBJID", params.get("INSOBJID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRPROPERTYID - ИД описания имущества</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - ФИО</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSOBJID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSOBJID", "INSOBJNODEID"})
    public Map<String,Object> dsInsObjMovableInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsObjMovableInsert", params);
        result.put("INSOBJID", params.get("INSOBJID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRPROPERTYID - ИД описания имущества</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - ФИО</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSOBJID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSOBJID"})
    public Map<String,Object> dsInsObjMovableUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsObjMovableUpdate", params);
        result.put("INSOBJID", params.get("INSOBJID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRPROPERTYID - ИД описания имущества</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - ФИО</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSOBJID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSOBJID"})
    public Map<String,Object> dsInsObjMovableModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsObjMovableUpdate", params);
        result.put("INSOBJID", params.get("INSOBJID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INSOBJID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSOBJID"})
    public void dsInsObjMovableDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsInsObjMovableDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRPROPERTYID - ИД описания имущества</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - ФИО</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRPROPERTYID - ИД описания имущества</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - ФИО</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsInsObjMovableBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsInsObjMovableBrowseListByParam", "dsInsObjMovableBrowseListByParamCount", params);
        return result;
    }





}
