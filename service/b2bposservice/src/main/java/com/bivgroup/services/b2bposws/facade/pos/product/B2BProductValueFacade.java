/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BProductValue
 *
 * @author reson
 */
@Discriminator(1)
@IdGen(entityName="B2B_PRODVALUE",idFieldName="PRODVALUEID")
@BOName("B2BProductValue")
public class B2BProductValueFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид типа причины по ДОПСУ</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductValueCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductValueInsert", params);
        result.put("PRODVALUEID", params.get("PRODVALUEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид типа причины по ДОПСУ</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVALUEID"})
    public Map<String,Object> dsB2BProductValueInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductValueInsert", params);
        result.put("PRODVALUEID", params.get("PRODVALUEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид типа причины по ДОПСУ</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVALUEID"})
    public Map<String,Object> dsB2BProductValueUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductValueUpdate", params);
        result.put("PRODVALUEID", params.get("PRODVALUEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид типа причины по ДОПСУ</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVALUEID"})
    public Map<String,Object> dsB2BProductValueModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductValueUpdate", params);
        result.put("PRODVALUEID", params.get("PRODVALUEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODVALUEID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVALUEID"})
    public void dsB2BProductValueDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductValueDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид типа причины по ДОПСУ</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид типа причины по ДОПСУ</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductValueBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductValueBrowseListByParam", "dsB2BProductValueBrowseListByParamCount", params);
        return result;
    }





}
