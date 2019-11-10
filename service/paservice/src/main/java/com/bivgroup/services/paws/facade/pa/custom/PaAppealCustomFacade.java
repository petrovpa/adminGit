/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.paws.facade.pa.custom;

import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("PaAppealCustom")
public class PaAppealCustomFacade extends BaseFacade {

    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>APPEALDATE - Дата обращения</LI>
     * <LI>APPEALNUMBER - Номер обращения</LI>
     * <LI>APPEALOBJECTID - Объект обращения</LI>
     * <LI>CATEGORYID - Категория обращения</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>APPEALID - ИД записи</LI>
     * <LI>INITIATOREMAIL - Адрес электронной почты обратившегося</LI>
     * <LI>INITIATORID - ИД инициатора (либо Пользователь B2B, либо Пользователь личного кабинета)</LI>
     * <LI>INITIATORNAME - Фамилия обратившегося</LI>
     * <LI>INITIATORPHONE - Номер телефона обратившегося</LI>
     * <LI>INITIATORSURNAME - Фамилия обратившегося</LI>
     * <LI>INITIATORTYPEID - Тип инициатора (1 - Пользователь B2B, 2 - Пользователь личного кабинета, 3 - Отправитель с сайта)</LI>
     * <LI>SOURCEID - ИД источника</LI>
     * <LI>STATUSNAME - Статус</LI>
     * <LI>STATUSSYSNAME - Системное наименование статуса</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>APPEALDATE - Дата обращения</LI>
     * <LI>APPEALNUMBER - Номер обращения</LI>
     * <LI>APPEALOBJECTID - Объект обращения</LI>
     * <LI>CATEGORYID - Категория обращения</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>APPEALID - ИД записи</LI>
     * <LI>INITIATOREMAIL - Адрес электронной почты обратившегося</LI>
     * <LI>INITIATORID - ИД инициатора (либо Пользователь B2B, либо Пользователь личного кабинета)</LI>
     * <LI>INITIATORNAME - Фамилия обратившегося</LI>
     * <LI>INITIATORPHONE - Номер телефона обратившегося</LI>
     * <LI>INITIATORSURNAME - Фамилия обратившегося</LI>
     * <LI>INITIATORTYPEID - Тип инициатора (1 - Пользователь B2B, 2 - Пользователь личного кабинета, 3 - Отправитель с сайта)</LI>
     * <LI>SOURCEID - ИД источника</LI>
     * <LI>STATUSNAME - Статус</LI>
     * <LI>STATUSSYSNAME - Системное наименование статуса</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsPaAppealBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> p = new HashMap<String, Object>();
        p.putAll(params);
        p.put("INITIATORID", params.get("CURRENT_USERID"));
        Map<String,Object> result = this.selectQuery("dsPaAppealBrowseListByParamEx", "dsPaAppealBrowseListByParamExCount", p);
        return result;
    }
    
}
