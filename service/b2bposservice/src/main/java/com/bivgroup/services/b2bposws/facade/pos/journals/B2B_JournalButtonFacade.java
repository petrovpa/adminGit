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
 * Фасад для сущности B2B_JournalButton
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_JOURNALBUTTON",idFieldName="ID")
@BOName("B2B_JournalButton")
public class B2B_JournalButtonFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTION - действие</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>GROUPNAME - содержит название группировки кнопок</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>NAME - содержит название кнопки</LI>
     * <LI>NOTE - содержит текст подробного описания кнопки</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер кнопки</LI>
     * <LI>TYPEBUTTON - тип кнопки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLPICTURE - содержит URL на иконку кнопки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_JournalButtonCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_JournalButtonInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTION - действие</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>GROUPNAME - содержит название группировки кнопок</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>NAME - содержит название кнопки</LI>
     * <LI>NOTE - содержит текст подробного описания кнопки</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер кнопки</LI>
     * <LI>TYPEBUTTON - тип кнопки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLPICTURE - содержит URL на иконку кнопки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_JournalButtonInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_JournalButtonInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTION - действие</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>GROUPNAME - содержит название группировки кнопок</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>NAME - содержит название кнопки</LI>
     * <LI>NOTE - содержит текст подробного описания кнопки</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер кнопки</LI>
     * <LI>TYPEBUTTON - тип кнопки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLPICTURE - содержит URL на иконку кнопки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_JournalButtonUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_JournalButtonUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTION - действие</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>GROUPNAME - содержит название группировки кнопок</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>NAME - содержит название кнопки</LI>
     * <LI>NOTE - содержит текст подробного описания кнопки</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер кнопки</LI>
     * <LI>TYPEBUTTON - тип кнопки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLPICTURE - содержит URL на иконку кнопки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_JournalButtonModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_JournalButtonUpdate", params);
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
    public void dsB2B_JournalButtonDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2B_JournalButtonDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTION - действие</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>GROUPNAME - содержит название группировки кнопок</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>NAME - содержит название кнопки</LI>
     * <LI>NOTE - содержит текст подробного описания кнопки</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер кнопки</LI>
     * <LI>TYPEBUTTON - тип кнопки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLPICTURE - содержит URL на иконку кнопки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ACTION - действие</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>GROUPNAME - содержит название группировки кнопок</LI>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - ссылка на объект учета «Журнал»</LI>
     * <LI>NAME - содержит название кнопки</LI>
     * <LI>NOTE - содержит текст подробного описания кнопки</LI>
     * <LI>SEQUENCE - содержит сквозной порядковый номер кнопки</LI>
     * <LI>TYPEBUTTON - тип кнопки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * <LI>URLPICTURE - содержит URL на иконку кнопки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_JournalButtonBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2B_JournalButtonBrowseListByParam", "dsB2B_JournalButtonBrowseListByParamCount", params);
        return result;
    }





}
