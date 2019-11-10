/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.other;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BCalendar
 *
 * @author reson
 */
@IdGen(entityName="B2B_CALENDAR",idFieldName="CALENDARID")
@BOName("B2BCalendar")
public class B2BCalendarFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CALDATE - Дата дня календаря</LI>
     * <LI>CALENDARID - ИД записи</LI>
     * <LI>ISDAYOFF - Признак выходного дня</LI>
     * <LI>ISHOLIDAY - Признак праздничного дня</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CALENDARID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BCalendarCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BCalendarInsert", params);
        result.put("CALENDARID", params.get("CALENDARID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CALDATE - Дата дня календаря</LI>
     * <LI>CALENDARID - ИД записи</LI>
     * <LI>ISDAYOFF - Признак выходного дня</LI>
     * <LI>ISHOLIDAY - Признак праздничного дня</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CALENDARID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CALENDARID"})
    public Map<String,Object> dsB2BCalendarInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BCalendarInsert", params);
        result.put("CALENDARID", params.get("CALENDARID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CALDATE - Дата дня календаря</LI>
     * <LI>CALENDARID - ИД записи</LI>
     * <LI>ISDAYOFF - Признак выходного дня</LI>
     * <LI>ISHOLIDAY - Признак праздничного дня</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CALENDARID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CALENDARID"})
    public Map<String,Object> dsB2BCalendarUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BCalendarUpdate", params);
        result.put("CALENDARID", params.get("CALENDARID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CALDATE - Дата дня календаря</LI>
     * <LI>CALENDARID - ИД записи</LI>
     * <LI>ISDAYOFF - Признак выходного дня</LI>
     * <LI>ISHOLIDAY - Признак праздничного дня</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CALENDARID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CALENDARID"})
    public Map<String,Object> dsB2BCalendarModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BCalendarUpdate", params);
        result.put("CALENDARID", params.get("CALENDARID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CALENDARID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CALENDARID"})
    public void dsB2BCalendarDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BCalendarDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CALDATE - Дата дня календаря</LI>
     * <LI>CALENDARID - ИД записи</LI>
     * <LI>ISDAYOFF - Признак выходного дня</LI>
     * <LI>ISHOLIDAY - Признак праздничного дня</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CALDATE - Дата дня календаря</LI>
     * <LI>CALENDARID - ИД записи</LI>
     * <LI>ISDAYOFF - Признак выходного дня</LI>
     * <LI>ISHOLIDAY - Признак праздничного дня</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BCalendarBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BCalendarBrowseListByParam", "dsB2BCalendarBrowseListByParamCount", params);
        return result;
    }





}
