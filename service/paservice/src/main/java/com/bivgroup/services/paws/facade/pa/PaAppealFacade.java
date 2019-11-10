/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.paws.facade.pa;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности PaAppeal
 *
 * @author reson
 */
@IdGen(entityName="PA_APPEAL",idFieldName="APPEALID")
@BOName("PaAppeal")
public class PaAppealFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
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
     * <LI>APPEALID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsPaAppealCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPaAppealInsert", params);
        result.put("APPEALID", params.get("APPEALID"));
        return result;
    }





    /**
     * Создать объект без генерации id
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
     * <LI>APPEALID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"APPEALID"})
    public Map<String,Object> dsPaAppealInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPaAppealInsert", params);
        result.put("APPEALID", params.get("APPEALID"));
        return result;
    }





    /**
     * Изменить объект
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
     * <LI>APPEALID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"APPEALID"})
    public Map<String,Object> dsPaAppealUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPaAppealUpdate", params);
        result.put("APPEALID", params.get("APPEALID"));
        return result;
    }





    /**
     * Изменить объект
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
     * <LI>APPEALID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"APPEALID"})
    public Map<String,Object> dsPaAppealModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPaAppealUpdate", params);
        result.put("APPEALID", params.get("APPEALID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>APPEALID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"APPEALID"})
    public void dsPaAppealDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsPaAppealDelete", params);
    }





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
    public Map<String,Object> dsPaAppealBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsPaAppealBrowseListByParam", "dsPaAppealBrowseListByParamCount", params);
        return result;
    }





}
