/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesAddress
 *
 * @author reson
 */
@IdGen(entityName="LOSS_ADDRESS",idFieldName="ADDRESSID")
@BOName("LossesAddress")
public class LossesAddressFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESSSYSNAME - Системное наименование адреса</LI>
     * <LI>ADDRESSTEXT1 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT2 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT3 - Адрес строкой</LI>
     * <LI>BUILDING - Корпус</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>COUNTRY - Страна</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Строение</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>ISINVALID - Адрес не актуален</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJSYSNAME - Системное наименование объекта</LI>
     * <LI>PERMANENTLOCATIONFLAG - Флаг временного размещения</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>PRIORITY - Приоритет</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>REGISTRDATE - Дата регистрации</LI>
     * <LI>REGISTRTODATE - Дата окончания регистрации</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Флаг адрес по КЛАДР</LI>
     * <LI>VILLAGE - Деревня</LI>
     * <LI>VILLAGEKLADR - Кладр деревни</LI>
     * <LI>VILLAGETYPE - Тип деревни</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESSID - ИД адреса</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesAddressCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesAddressInsert", params);
        result.put("ADDRESSID", params.get("ADDRESSID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESSSYSNAME - Системное наименование адреса</LI>
     * <LI>ADDRESSTEXT1 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT2 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT3 - Адрес строкой</LI>
     * <LI>BUILDING - Корпус</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>COUNTRY - Страна</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Строение</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>ISINVALID - Адрес не актуален</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJSYSNAME - Системное наименование объекта</LI>
     * <LI>PERMANENTLOCATIONFLAG - Флаг временного размещения</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>PRIORITY - Приоритет</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>REGISTRDATE - Дата регистрации</LI>
     * <LI>REGISTRTODATE - Дата окончания регистрации</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Флаг адрес по КЛАДР</LI>
     * <LI>VILLAGE - Деревня</LI>
     * <LI>VILLAGEKLADR - Кладр деревни</LI>
     * <LI>VILLAGETYPE - Тип деревни</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESSID - ИД адреса</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDRESSID"})
    public Map<String,Object> dsLossesAddressInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesAddressInsert", params);
        result.put("ADDRESSID", params.get("ADDRESSID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESSSYSNAME - Системное наименование адреса</LI>
     * <LI>ADDRESSTEXT1 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT2 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT3 - Адрес строкой</LI>
     * <LI>BUILDING - Корпус</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>COUNTRY - Страна</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Строение</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>ISINVALID - Адрес не актуален</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJSYSNAME - Системное наименование объекта</LI>
     * <LI>PERMANENTLOCATIONFLAG - Флаг временного размещения</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>PRIORITY - Приоритет</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>REGISTRDATE - Дата регистрации</LI>
     * <LI>REGISTRTODATE - Дата окончания регистрации</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Флаг адрес по КЛАДР</LI>
     * <LI>VILLAGE - Деревня</LI>
     * <LI>VILLAGEKLADR - Кладр деревни</LI>
     * <LI>VILLAGETYPE - Тип деревни</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESSID - ИД адреса</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDRESSID"})
    public Map<String,Object> dsLossesAddressUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesAddressUpdate", params);
        result.put("ADDRESSID", params.get("ADDRESSID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESSSYSNAME - Системное наименование адреса</LI>
     * <LI>ADDRESSTEXT1 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT2 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT3 - Адрес строкой</LI>
     * <LI>BUILDING - Корпус</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>COUNTRY - Страна</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Строение</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>ISINVALID - Адрес не актуален</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJSYSNAME - Системное наименование объекта</LI>
     * <LI>PERMANENTLOCATIONFLAG - Флаг временного размещения</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>PRIORITY - Приоритет</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>REGISTRDATE - Дата регистрации</LI>
     * <LI>REGISTRTODATE - Дата окончания регистрации</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Флаг адрес по КЛАДР</LI>
     * <LI>VILLAGE - Деревня</LI>
     * <LI>VILLAGEKLADR - Кладр деревни</LI>
     * <LI>VILLAGETYPE - Тип деревни</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESSID - ИД адреса</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDRESSID"})
    public Map<String,Object> dsLossesAddressModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesAddressUpdate", params);
        result.put("ADDRESSID", params.get("ADDRESSID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESSID - ИД адреса</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDRESSID"})
    public void dsLossesAddressDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesAddressDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESSSYSNAME - Системное наименование адреса</LI>
     * <LI>ADDRESSTEXT1 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT2 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT3 - Адрес строкой</LI>
     * <LI>BUILDING - Корпус</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>COUNTRY - Страна</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Строение</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>ISINVALID - Адрес не актуален</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJSYSNAME - Системное наименование объекта</LI>
     * <LI>PERMANENTLOCATIONFLAG - Флаг временного размещения</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>PRIORITY - Приоритет</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>REGISTRDATE - Дата регистрации</LI>
     * <LI>REGISTRTODATE - Дата окончания регистрации</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Флаг адрес по КЛАДР</LI>
     * <LI>VILLAGE - Деревня</LI>
     * <LI>VILLAGEKLADR - Кладр деревни</LI>
     * <LI>VILLAGETYPE - Тип деревни</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESSSYSNAME - Системное наименование адреса</LI>
     * <LI>ADDRESSTEXT1 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT2 - Адрес строкой</LI>
     * <LI>ADDRESSTEXT3 - Адрес строкой</LI>
     * <LI>BUILDING - Корпус</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>COUNTRY - Страна</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Строение</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>ISINVALID - Адрес не актуален</LI>
     * <LI>OBJID - ИД объекта</LI>
     * <LI>OBJSYSNAME - Системное наименование объекта</LI>
     * <LI>PERMANENTLOCATIONFLAG - Флаг временного размещения</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>PRIORITY - Приоритет</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>REGISTRDATE - Дата регистрации</LI>
     * <LI>REGISTRTODATE - Дата окончания регистрации</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Флаг адрес по КЛАДР</LI>
     * <LI>VILLAGE - Деревня</LI>
     * <LI>VILLAGEKLADR - Кладр деревни</LI>
     * <LI>VILLAGETYPE - Тип деревни</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesAddressBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesAddressBrowseListByParam", "dsLossesAddressBrowseListByParamCount", params);
        return result;
    }





}
