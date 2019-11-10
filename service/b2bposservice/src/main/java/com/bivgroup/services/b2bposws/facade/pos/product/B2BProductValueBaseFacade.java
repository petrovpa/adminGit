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
 * Фасад для сущности B2BProductValueBase
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODVALUE",idFieldName="PRODVALUEID")
@BOName("B2BProductValueBase")
public class B2BProductValueBaseFacade extends BaseFacade {



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
    public Map<String,Object> dsB2BProductValueBaseCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductValueBaseInsert", params);
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
    public Map<String,Object> dsB2BProductValueBaseInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductValueBaseInsert", params);
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
    public Map<String,Object> dsB2BProductValueBaseUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductValueBaseUpdate", params);
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
    public Map<String,Object> dsB2BProductValueBaseModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductValueBaseUpdate", params);
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
    public void dsB2BProductValueBaseDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductValueBaseDelete", params);
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
    public Map<String,Object> dsB2BProductValueBaseBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductValueBaseBrowseListByParam", "dsB2BProductValueBaseBrowseListByParamCount", params);
        return result;
    }





}
