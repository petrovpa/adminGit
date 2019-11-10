/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.autonumber.AutoNumber;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности SHTask
 *
 * @author reson
 */
@BinaryFile(objTableName = "SHTASK", objTablePKFieldName = "ID")
@AutoNumber(autoNumberFieldName = "NUM",dataParamName = "CREATEFDATE")
@IdGen(entityName="SHTASK",idFieldName="ID")
@BOName("SHTask")
public class SHTaskFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - null</LI>
     * <LI>CREATEFDATE - null</LI>
     * <LI>DEF_DATE - Дата расторжения</LI>
     * <LI>DEF_FDATE - null</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>DONE_DATE - null</LI>
     * <LI>DONE_FDATE - null</LI>
     * <LI>ENTITYID - заполнять значением 5890</LI>
     * <LI>EXTSYSTEMID - ИД договора</LI>
     * <LI>SHTASKID - null</LI>
     * <LI>INITIATORID - null</LI>
     * <LI>ISGROUPTASK - null</LI>
     * <LI>MUST_DONE_DATE - null</LI>
     * <LI>MUST_DONE_FDATE - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>NUM - null</LI>
     * <LI>OENTITYID - null</LI>
     * <LI>OOBJECTID - null</LI>
     * <LI>PARENTID - null</LI>
     * <LI>REMIND_TIME - null</LI>
     * <LI>REMIND_TIMEFDATE - null</LI>
     * <LI>RESPONSIBLE_EXECUTORID - null</LI>
     * <LI>SHKINDID - целое, ссылка – select SHTASK_KINDID from SHTASK_KIND where BRIEF = ЗаявлениеНаРасторжениеФронт</LI>
     * <LI>SHTASK_STATUSID - Целое 21</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SHTASKID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSHTaskCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSHTaskInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - null</LI>
     * <LI>CREATEFDATE - null</LI>
     * <LI>DEF_DATE - Дата расторжения</LI>
     * <LI>DEF_FDATE - null</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>DONE_DATE - null</LI>
     * <LI>DONE_FDATE - null</LI>
     * <LI>ENTITYID - заполнять значением 5890</LI>
     * <LI>EXTSYSTEMID - ИД договора</LI>
     * <LI>SHTASKID - null</LI>
     * <LI>INITIATORID - null</LI>
     * <LI>ISGROUPTASK - null</LI>
     * <LI>MUST_DONE_DATE - null</LI>
     * <LI>MUST_DONE_FDATE - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>NUM - null</LI>
     * <LI>OENTITYID - null</LI>
     * <LI>OOBJECTID - null</LI>
     * <LI>PARENTID - null</LI>
     * <LI>REMIND_TIME - null</LI>
     * <LI>REMIND_TIMEFDATE - null</LI>
     * <LI>RESPONSIBLE_EXECUTORID - null</LI>
     * <LI>SHKINDID - целое, ссылка – select SHTASK_KINDID from SHTASK_KIND where BRIEF = ЗаявлениеНаРасторжениеФронт</LI>
     * <LI>SHTASK_STATUSID - Целое 21</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SHTASKID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSHTaskInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSHTaskInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - null</LI>
     * <LI>CREATEFDATE - null</LI>
     * <LI>DEF_DATE - Дата расторжения</LI>
     * <LI>DEF_FDATE - null</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>DONE_DATE - null</LI>
     * <LI>DONE_FDATE - null</LI>
     * <LI>ENTITYID - заполнять значением 5890</LI>
     * <LI>EXTSYSTEMID - ИД договора</LI>
     * <LI>SHTASKID - null</LI>
     * <LI>INITIATORID - null</LI>
     * <LI>ISGROUPTASK - null</LI>
     * <LI>MUST_DONE_DATE - null</LI>
     * <LI>MUST_DONE_FDATE - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>NUM - null</LI>
     * <LI>OENTITYID - null</LI>
     * <LI>OOBJECTID - null</LI>
     * <LI>PARENTID - null</LI>
     * <LI>REMIND_TIME - null</LI>
     * <LI>REMIND_TIMEFDATE - null</LI>
     * <LI>RESPONSIBLE_EXECUTORID - null</LI>
     * <LI>SHKINDID - целое, ссылка – select SHTASK_KINDID from SHTASK_KIND where BRIEF = ЗаявлениеНаРасторжениеФронт</LI>
     * <LI>SHTASK_STATUSID - Целое 21</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SHTASKID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSHTaskUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSHTaskUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - null</LI>
     * <LI>CREATEFDATE - null</LI>
     * <LI>DEF_DATE - Дата расторжения</LI>
     * <LI>DEF_FDATE - null</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>DONE_DATE - null</LI>
     * <LI>DONE_FDATE - null</LI>
     * <LI>ENTITYID - заполнять значением 5890</LI>
     * <LI>EXTSYSTEMID - ИД договора</LI>
     * <LI>SHTASKID - null</LI>
     * <LI>INITIATORID - null</LI>
     * <LI>ISGROUPTASK - null</LI>
     * <LI>MUST_DONE_DATE - null</LI>
     * <LI>MUST_DONE_FDATE - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>NUM - null</LI>
     * <LI>OENTITYID - null</LI>
     * <LI>OOBJECTID - null</LI>
     * <LI>PARENTID - null</LI>
     * <LI>REMIND_TIME - null</LI>
     * <LI>REMIND_TIMEFDATE - null</LI>
     * <LI>RESPONSIBLE_EXECUTORID - null</LI>
     * <LI>SHKINDID - целое, ссылка – select SHTASK_KINDID from SHTASK_KIND where BRIEF = ЗаявлениеНаРасторжениеФронт</LI>
     * <LI>SHTASK_STATUSID - Целое 21</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SHTASKID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSHTaskModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSHTaskUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>SHTASKID - null</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public void dsSHTaskDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsSHTaskDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - null</LI>
     * <LI>CREATEFDATE - null</LI>
     * <LI>DEF_DATE - Дата расторжения</LI>
     * <LI>DEF_FDATE - null</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>DONE_DATE - null</LI>
     * <LI>DONE_FDATE - null</LI>
     * <LI>ENTITYID - заполнять значением 5890</LI>
     * <LI>EXTSYSTEMID - ИД договора</LI>
     * <LI>SHTASKID - null</LI>
     * <LI>INITIATORID - null</LI>
     * <LI>ISGROUPTASK - null</LI>
     * <LI>MUST_DONE_DATE - null</LI>
     * <LI>MUST_DONE_FDATE - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>NUM - null</LI>
     * <LI>OENTITYID - null</LI>
     * <LI>OOBJECTID - null</LI>
     * <LI>PARENTID - null</LI>
     * <LI>REMIND_TIME - null</LI>
     * <LI>REMIND_TIMEFDATE - null</LI>
     * <LI>RESPONSIBLE_EXECUTORID - null</LI>
     * <LI>SHKINDID - целое, ссылка – select SHTASK_KINDID from SHTASK_KIND where BRIEF = ЗаявлениеНаРасторжениеФронт</LI>
     * <LI>SHTASK_STATUSID - Целое 21</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - null</LI>
     * <LI>CREATEFDATE - null</LI>
     * <LI>DEF_DATE - Дата расторжения</LI>
     * <LI>DEF_FDATE - null</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>DONE_DATE - null</LI>
     * <LI>DONE_FDATE - null</LI>
     * <LI>ENTITYID - заполнять значением 5890</LI>
     * <LI>EXTSYSTEMID - ИД договора</LI>
     * <LI>SHTASKID - null</LI>
     * <LI>INITIATORID - null</LI>
     * <LI>ISGROUPTASK - null</LI>
     * <LI>MUST_DONE_DATE - null</LI>
     * <LI>MUST_DONE_FDATE - null</LI>
     * <LI>NOTE - null</LI>
     * <LI>NUM - null</LI>
     * <LI>OENTITYID - null</LI>
     * <LI>OOBJECTID - null</LI>
     * <LI>PARENTID - null</LI>
     * <LI>REMIND_TIME - null</LI>
     * <LI>REMIND_TIMEFDATE - null</LI>
     * <LI>RESPONSIBLE_EXECUTORID - null</LI>
     * <LI>SHKINDID - целое, ссылка – select SHTASK_KINDID from SHTASK_KIND where BRIEF = ЗаявлениеНаРасторжениеФронт</LI>
     * <LI>SHTASK_STATUSID - Целое 21</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSHTaskBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsSHTaskBrowseListByParam", "dsSHTaskBrowseListByParamCount", params);
        return result;
    }





}
