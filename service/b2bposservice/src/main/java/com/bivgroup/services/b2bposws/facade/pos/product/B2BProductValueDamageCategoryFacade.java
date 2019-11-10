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
 * Фасад для сущности B2BProductValueDamageCategory
 *
 * @author reson
 */
@Discriminator(4)
@IdGen(entityName="B2B_PRODVALUE",idFieldName="PRODVALUEID")
@BOName("B2BProductValueDamageCategory")
public class B2BProductValueDamageCategoryFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductValueDamageCategoryCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductValueDamageCategoryInsert", params);
        result.put("PRODVALUEID", params.get("PRODVALUEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVALUEID"})
    public Map<String,Object> dsB2BProductValueDamageCategoryInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductValueDamageCategoryInsert", params);
        result.put("PRODVALUEID", params.get("PRODVALUEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVALUEID"})
    public Map<String,Object> dsB2BProductValueDamageCategoryUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductValueDamageCategoryUpdate", params);
        result.put("PRODVALUEID", params.get("PRODVALUEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODVALUEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVALUEID"})
    public Map<String,Object> dsB2BProductValueDamageCategoryModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductValueDamageCategoryUpdate", params);
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
    public void dsB2BProductValueDamageCategoryDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductValueDamageCategoryDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>DESCR - Примечание</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FORMULA - Формула</LI>
     * <LI>PRODVALUEID - ИД</LI>
     * <LI>ISHANDBOOK - Признак формирования справочника</LI>
     * <LI>KINDID - ИД вида</LI>
     * <LI>NAME - Наименование показателя</LI>
     * <LI>PRODDAMAGECATID - Ид категории ущерба по убытку</LI>
     * <LI>VALGROUPID - ИД группы</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductValueDamageCategoryBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductValueDamageCategoryBrowseListByParam", "dsB2BProductValueDamageCategoryBrowseListByParamCount", params);
        return result;
    }





}
