/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BProductPossibleValue
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODPOSSVALUE",idFieldName="PRODPOSSVALUEID")
@BOName("B2BProductPossibleValue")
public class B2BProductPossibleValueFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * <LI>ISDEFAULT - Признак значение по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVALUEID - ИД показателя</LI>
     * <LI>VALUE2DOUBLE - Дробное значение</LI>
     * <LI>VALUE2LONG - Целое значение</LI>
     * <LI>VALUE2STRING - Строковое значение</LI>
     * <LI>VALUEDOUBLE - Дробное значение</LI>
     * <LI>VALUELONG - Целое значение</LI>
     * <LI>VALUESTRING - Строковое значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductPossibleValueCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductPossibleValueInsert", params);
        result.put("PRODPOSSVALUEID", params.get("PRODPOSSVALUEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * <LI>ISDEFAULT - Признак значение по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVALUEID - ИД показателя</LI>
     * <LI>VALUE2DOUBLE - Дробное значение</LI>
     * <LI>VALUE2LONG - Целое значение</LI>
     * <LI>VALUE2STRING - Строковое значение</LI>
     * <LI>VALUEDOUBLE - Дробное значение</LI>
     * <LI>VALUELONG - Целое значение</LI>
     * <LI>VALUESTRING - Строковое значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPOSSVALUEID"})
    public Map<String,Object> dsB2BProductPossibleValueInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductPossibleValueInsert", params);
        result.put("PRODPOSSVALUEID", params.get("PRODPOSSVALUEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * <LI>ISDEFAULT - Признак значение по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVALUEID - ИД показателя</LI>
     * <LI>VALUE2DOUBLE - Дробное значение</LI>
     * <LI>VALUE2LONG - Целое значение</LI>
     * <LI>VALUE2STRING - Строковое значение</LI>
     * <LI>VALUEDOUBLE - Дробное значение</LI>
     * <LI>VALUELONG - Целое значение</LI>
     * <LI>VALUESTRING - Строковое значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPOSSVALUEID"})
    public Map<String,Object> dsB2BProductPossibleValueUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductPossibleValueUpdate", params);
        result.put("PRODPOSSVALUEID", params.get("PRODPOSSVALUEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * <LI>ISDEFAULT - Признак значение по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVALUEID - ИД показателя</LI>
     * <LI>VALUE2DOUBLE - Дробное значение</LI>
     * <LI>VALUE2LONG - Целое значение</LI>
     * <LI>VALUE2STRING - Строковое значение</LI>
     * <LI>VALUEDOUBLE - Дробное значение</LI>
     * <LI>VALUELONG - Целое значение</LI>
     * <LI>VALUESTRING - Строковое значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPOSSVALUEID"})
    public Map<String,Object> dsB2BProductPossibleValueModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductPossibleValueUpdate", params);
        result.put("PRODPOSSVALUEID", params.get("PRODPOSSVALUEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPOSSVALUEID"})
    public void dsB2BProductPossibleValueDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductPossibleValueDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * <LI>ISDEFAULT - Признак значение по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVALUEID - ИД показателя</LI>
     * <LI>VALUE2DOUBLE - Дробное значение</LI>
     * <LI>VALUE2LONG - Целое значение</LI>
     * <LI>VALUE2STRING - Строковое значение</LI>
     * <LI>VALUEDOUBLE - Дробное значение</LI>
     * <LI>VALUELONG - Целое значение</LI>
     * <LI>VALUESTRING - Строковое значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODPOSSVALUEID - ИД</LI>
     * <LI>ISDEFAULT - Признак значение по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVALUEID - ИД показателя</LI>
     * <LI>VALUE2DOUBLE - Дробное значение</LI>
     * <LI>VALUE2LONG - Целое значение</LI>
     * <LI>VALUE2STRING - Строковое значение</LI>
     * <LI>VALUEDOUBLE - Дробное значение</LI>
     * <LI>VALUELONG - Целое значение</LI>
     * <LI>VALUESTRING - Строковое значение</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductPossibleValueBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductPossibleValueBrowseListByParam", "dsB2BProductPossibleValueBrowseListByParamCount", params);
        return result;
    }





}
