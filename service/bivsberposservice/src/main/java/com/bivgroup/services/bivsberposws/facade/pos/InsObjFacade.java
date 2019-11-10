/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.version.NodeVersion;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности InsObj
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@NodeVersion(nodeTableName="INS_INSOBJNODE",nodeTableIdFieldName="INSOBJNODEID",versionNumberParamName="VERNUMBER",nodeLastVersionNumberFieldName="LASTVERNUMBER",nodeRVersionFieldName="RVERSION")
@IdGen(entityName="INS_INSOBJ",idFieldName="INSOBJID")
@BOName("InsObj")
public class InsObjFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FACINGTYPE - Системное наименование типа отделки</LI>
     * <LI>HOUSEHAS - В доме есть</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - Наименование объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJAREA - Площадь объекта</LI>
     * <LI>OBJTYPESYSNAME - Системное наименование вида объекта</LI>
     * <LI>PRODYEARSYSNAME - Системное наименование периода постройки</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WALMATERIAL - Материал стен</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSOBJID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSOBJNODEID"})
    public Map<String,Object> dsInsObjCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsObjInsert", params);
        result.put("INSOBJID", params.get("INSOBJID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FACINGTYPE - Системное наименование типа отделки</LI>
     * <LI>HOUSEHAS - В доме есть</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - Наименование объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJAREA - Площадь объекта</LI>
     * <LI>OBJTYPESYSNAME - Системное наименование вида объекта</LI>
     * <LI>PRODYEARSYSNAME - Системное наименование периода постройки</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WALMATERIAL - Материал стен</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSOBJID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSOBJID", "INSOBJNODEID"})
    public Map<String,Object> dsInsObjInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsObjInsert", params);
        result.put("INSOBJID", params.get("INSOBJID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FACINGTYPE - Системное наименование типа отделки</LI>
     * <LI>HOUSEHAS - В доме есть</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - Наименование объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJAREA - Площадь объекта</LI>
     * <LI>OBJTYPESYSNAME - Системное наименование вида объекта</LI>
     * <LI>PRODYEARSYSNAME - Системное наименование периода постройки</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WALMATERIAL - Материал стен</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSOBJID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSOBJID"})
    public Map<String,Object> dsInsObjUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsObjUpdate", params);
        result.put("INSOBJID", params.get("INSOBJID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FACINGTYPE - Системное наименование типа отделки</LI>
     * <LI>HOUSEHAS - В доме есть</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - Наименование объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJAREA - Площадь объекта</LI>
     * <LI>OBJTYPESYSNAME - Системное наименование вида объекта</LI>
     * <LI>PRODYEARSYSNAME - Системное наименование периода постройки</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WALMATERIAL - Материал стен</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSOBJID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSOBJID"})
    public Map<String,Object> dsInsObjModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsObjUpdate", params);
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
    public void dsInsObjDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsInsObjDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FACINGTYPE - Системное наименование типа отделки</LI>
     * <LI>HOUSEHAS - В доме есть</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - Наименование объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJAREA - Площадь объекта</LI>
     * <LI>OBJTYPESYSNAME - Системное наименование вида объекта</LI>
     * <LI>PRODYEARSYSNAME - Системное наименование периода постройки</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WALMATERIAL - Материал стен</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего объект сущности</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FACINGTYPE - Системное наименование типа отделки</LI>
     * <LI>HOUSEHAS - В доме есть</LI>
     * <LI>INSOBJID - ИД записи</LI>
     * <LI>INSOBJNODEID - ИД нода</LI>
     * <LI>NAME - Наименование объекта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>OBJAREA - Площадь объекта</LI>
     * <LI>OBJTYPESYSNAME - Системное наименование вида объекта</LI>
     * <LI>PRODYEARSYSNAME - Системное наименование периода постройки</LI>
     * <LI>UPDATEDATE - Дата редактирования</LI>
     * <LI>UPDATEUSERID - ИД редактировавшего пользователя</LI>
     * <LI>USERCOMMENT - Комментарий</LI>
     * <LI>USERCOMMENT2 - Дополнительный комментарий</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WALMATERIAL - Материал стен</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsInsObjBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsInsObjBrowseListByParam", "dsInsObjBrowseListByParamCount", params);
        return result;
    }





}
