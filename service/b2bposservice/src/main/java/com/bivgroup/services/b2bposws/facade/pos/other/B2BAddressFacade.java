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
 * Фасад для сущности B2BAddress
 *
 * @author reson
 */
@IdGen(entityName="B2B_ADDRESS",idFieldName="ADDRESSID")
@BOName("B2BAddress")
public class B2BAddressFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESSTEXT1 - Адрес в виде строки 1</LI>
     * <LI>ADDRESSTEXT2 - Адрес в виде строки 2</LI>
     * <LI>ADDRESSTEXT3 - Адрес в виде строки 3</LI>
     * <LI>ADDRESSTYPEID - Ид типа адреса</LI>
     * <LI>BUILDING - Строение</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>FLOOR - Этаж</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Корпус</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Признак использования КЛАДР</LI>
     * <LI>VILLAGE - Населенный пункт</LI>
     * <LI>VILLAGEKLADR - Кладр населенного пункта</LI>
     * <LI>VILLAGETYPE - Тип населенного пункта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESSID - ИД адреса</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAddressCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BAddressInsert", params);
        result.put("ADDRESSID", params.get("ADDRESSID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESSTEXT1 - Адрес в виде строки 1</LI>
     * <LI>ADDRESSTEXT2 - Адрес в виде строки 2</LI>
     * <LI>ADDRESSTEXT3 - Адрес в виде строки 3</LI>
     * <LI>ADDRESSTYPEID - Ид типа адреса</LI>
     * <LI>BUILDING - Строение</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>FLOOR - Этаж</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Корпус</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Признак использования КЛАДР</LI>
     * <LI>VILLAGE - Населенный пункт</LI>
     * <LI>VILLAGEKLADR - Кладр населенного пункта</LI>
     * <LI>VILLAGETYPE - Тип населенного пункта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESSID - ИД адреса</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDRESSID"})
    public Map<String,Object> dsB2BAddressInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BAddressInsert", params);
        result.put("ADDRESSID", params.get("ADDRESSID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESSTEXT1 - Адрес в виде строки 1</LI>
     * <LI>ADDRESSTEXT2 - Адрес в виде строки 2</LI>
     * <LI>ADDRESSTEXT3 - Адрес в виде строки 3</LI>
     * <LI>ADDRESSTYPEID - Ид типа адреса</LI>
     * <LI>BUILDING - Строение</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>FLOOR - Этаж</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Корпус</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Признак использования КЛАДР</LI>
     * <LI>VILLAGE - Населенный пункт</LI>
     * <LI>VILLAGEKLADR - Кладр населенного пункта</LI>
     * <LI>VILLAGETYPE - Тип населенного пункта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESSID - ИД адреса</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDRESSID"})
    public Map<String,Object> dsB2BAddressUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BAddressUpdate", params);
        result.put("ADDRESSID", params.get("ADDRESSID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESSTEXT1 - Адрес в виде строки 1</LI>
     * <LI>ADDRESSTEXT2 - Адрес в виде строки 2</LI>
     * <LI>ADDRESSTEXT3 - Адрес в виде строки 3</LI>
     * <LI>ADDRESSTYPEID - Ид типа адреса</LI>
     * <LI>BUILDING - Строение</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>FLOOR - Этаж</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Корпус</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Признак использования КЛАДР</LI>
     * <LI>VILLAGE - Населенный пункт</LI>
     * <LI>VILLAGEKLADR - Кладр населенного пункта</LI>
     * <LI>VILLAGETYPE - Тип населенного пункта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESSID - ИД адреса</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDRESSID"})
    public Map<String,Object> dsB2BAddressModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BAddressUpdate", params);
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
    public void dsB2BAddressDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BAddressDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDRESSTEXT1 - Адрес в виде строки 1</LI>
     * <LI>ADDRESSTEXT2 - Адрес в виде строки 2</LI>
     * <LI>ADDRESSTEXT3 - Адрес в виде строки 3</LI>
     * <LI>ADDRESSTYPEID - Ид типа адреса</LI>
     * <LI>BUILDING - Строение</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>FLOOR - Этаж</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Корпус</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Признак использования КЛАДР</LI>
     * <LI>VILLAGE - Населенный пункт</LI>
     * <LI>VILLAGEKLADR - Кладр населенного пункта</LI>
     * <LI>VILLAGETYPE - Тип населенного пункта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDRESSTEXT1 - Адрес в виде строки 1</LI>
     * <LI>ADDRESSTEXT2 - Адрес в виде строки 2</LI>
     * <LI>ADDRESSTEXT3 - Адрес в виде строки 3</LI>
     * <LI>ADDRESSTYPEID - Ид типа адреса</LI>
     * <LI>BUILDING - Строение</LI>
     * <LI>CITY - Город</LI>
     * <LI>CITYKLADR - Кладр города</LI>
     * <LI>CITYTYPE - Тип города</LI>
     * <LI>DISTRICT - Район</LI>
     * <LI>DISTRICTKLADR - Кладр района</LI>
     * <LI>DISTRICTTYPE - Тип района</LI>
     * <LI>FLAT - Квартира</LI>
     * <LI>FLATTYPE - Тип квартиры</LI>
     * <LI>FLOOR - Этаж</LI>
     * <LI>HOUSE - Дом</LI>
     * <LI>HOUSING - Корпус</LI>
     * <LI>ADDRESSID - ИД адреса</LI>
     * <LI>POSTALCODE - Индекс</LI>
     * <LI>REGION - Регион</LI>
     * <LI>REGIONKLADR - Кладр региона</LI>
     * <LI>REGIONTYPE - Тип региона</LI>
     * <LI>STREET - Улица</LI>
     * <LI>STREETKLADR - Кладр улицы</LI>
     * <LI>STREETTYPE - Тип улицы</LI>
     * <LI>USEKLADR - Признак использования КЛАДР</LI>
     * <LI>VILLAGE - Населенный пункт</LI>
     * <LI>VILLAGEKLADR - Кладр населенного пункта</LI>
     * <LI>VILLAGETYPE - Тип населенного пункта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAddressBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BAddressBrowseListByParam", "dsB2BAddressBrowseListByParamCount", params);
        return result;
    }





}
