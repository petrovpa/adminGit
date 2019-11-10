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
 * Фасад для сущности B2BCountry
 *
 * @author reson
 */
@IdGen(entityName="B2B_COUNTRY",idFieldName="COUNTRYID")
@BOName("B2BCountry")
public class B2BCountryFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ALPHACODE2 - Альфа код2</LI>
     * <LI>ALPHACODE3 - Альфа код 3</LI>
     * <LI>BRIEFNAME - Сокращенное название</LI>
     * <LI>COUNTRYNAME - Название страны</LI>
     * <LI>DIGITCODE - Код страны</LI>
     * <LI>ENGNAME - Наименование на английском</LI>
     * <LI>FLAG - Путь к изображению флага</LI>
     * <LI>COUNTRYID - ИД страны</LI>
     * <LI>ISNOTUSE - Не используется</LI>
     * <LI>NATIVENAME - Наименование на родном языке</LI>
     * <LI>PHONECODE - Префикс номера телефона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>COUNTRYID - ИД страны</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BCountryCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BCountryInsert", params);
        result.put("COUNTRYID", params.get("COUNTRYID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ALPHACODE2 - Альфа код2</LI>
     * <LI>ALPHACODE3 - Альфа код 3</LI>
     * <LI>BRIEFNAME - Сокращенное название</LI>
     * <LI>COUNTRYNAME - Название страны</LI>
     * <LI>DIGITCODE - Код страны</LI>
     * <LI>ENGNAME - Наименование на английском</LI>
     * <LI>FLAG - Путь к изображению флага</LI>
     * <LI>COUNTRYID - ИД страны</LI>
     * <LI>ISNOTUSE - Не используется</LI>
     * <LI>NATIVENAME - Наименование на родном языке</LI>
     * <LI>PHONECODE - Префикс номера телефона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>COUNTRYID - ИД страны</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"COUNTRYID"})
    public Map<String,Object> dsB2BCountryInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BCountryInsert", params);
        result.put("COUNTRYID", params.get("COUNTRYID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ALPHACODE2 - Альфа код2</LI>
     * <LI>ALPHACODE3 - Альфа код 3</LI>
     * <LI>BRIEFNAME - Сокращенное название</LI>
     * <LI>COUNTRYNAME - Название страны</LI>
     * <LI>DIGITCODE - Код страны</LI>
     * <LI>ENGNAME - Наименование на английском</LI>
     * <LI>FLAG - Путь к изображению флага</LI>
     * <LI>COUNTRYID - ИД страны</LI>
     * <LI>ISNOTUSE - Не используется</LI>
     * <LI>NATIVENAME - Наименование на родном языке</LI>
     * <LI>PHONECODE - Префикс номера телефона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>COUNTRYID - ИД страны</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"COUNTRYID"})
    public Map<String,Object> dsB2BCountryUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BCountryUpdate", params);
        result.put("COUNTRYID", params.get("COUNTRYID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ALPHACODE2 - Альфа код2</LI>
     * <LI>ALPHACODE3 - Альфа код 3</LI>
     * <LI>BRIEFNAME - Сокращенное название</LI>
     * <LI>COUNTRYNAME - Название страны</LI>
     * <LI>DIGITCODE - Код страны</LI>
     * <LI>ENGNAME - Наименование на английском</LI>
     * <LI>FLAG - Путь к изображению флага</LI>
     * <LI>COUNTRYID - ИД страны</LI>
     * <LI>ISNOTUSE - Не используется</LI>
     * <LI>NATIVENAME - Наименование на родном языке</LI>
     * <LI>PHONECODE - Префикс номера телефона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>COUNTRYID - ИД страны</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"COUNTRYID"})
    public Map<String,Object> dsB2BCountryModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BCountryUpdate", params);
        result.put("COUNTRYID", params.get("COUNTRYID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>COUNTRYID - ИД страны</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"COUNTRYID"})
    public void dsB2BCountryDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BCountryDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ALPHACODE2 - Альфа код2</LI>
     * <LI>ALPHACODE3 - Альфа код 3</LI>
     * <LI>BRIEFNAME - Сокращенное название</LI>
     * <LI>COUNTRYNAME - Название страны</LI>
     * <LI>DIGITCODE - Код страны</LI>
     * <LI>ENGNAME - Наименование на английском</LI>
     * <LI>FLAG - Путь к изображению флага</LI>
     * <LI>COUNTRYID - ИД страны</LI>
     * <LI>ISNOTUSE - Не используется</LI>
     * <LI>NATIVENAME - Наименование на родном языке</LI>
     * <LI>PHONECODE - Префикс номера телефона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ALPHACODE2 - Альфа код2</LI>
     * <LI>ALPHACODE3 - Альфа код 3</LI>
     * <LI>BRIEFNAME - Сокращенное название</LI>
     * <LI>COUNTRYNAME - Название страны</LI>
     * <LI>DIGITCODE - Код страны</LI>
     * <LI>ENGNAME - Наименование на английском</LI>
     * <LI>FLAG - Путь к изображению флага</LI>
     * <LI>COUNTRYID - ИД страны</LI>
     * <LI>ISNOTUSE - Не используется</LI>
     * <LI>NATIVENAME - Наименование на родном языке</LI>
     * <LI>PHONECODE - Префикс номера телефона</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BCountryBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BCountryBrowseListByParam", "dsB2BCountryBrowseListByParamCount", params);
        return result;
    }





}
