/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.other.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("B2BCalendarCustom")
public class B2BCalendarCustomFacade extends B2BBaseFacade {
    
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
    public Map<String,Object> dsB2BCalendarBrowseListByParamEx(Map<String, Object> params) throws Exception {
        parseDates(params, Double.class);
        Map<String,Object> result = this.selectQuery("dsB2BCalendarBrowseListByParamEx", "dsB2BCalendarBrowseListByParamExCount", params);
        return result;
    }
}
