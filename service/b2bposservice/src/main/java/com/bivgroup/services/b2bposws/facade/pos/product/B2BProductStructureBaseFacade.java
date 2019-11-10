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
 * Фасад для сущности B2BProductStructureBase
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODSTRUCT",idFieldName="PRODSTRUCTID")
@BOName("B2BProductStructureBase")
public class B2BProductStructureBaseFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDCALENDARTYPE - Правило определения даты начала - тип календаля</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductStructureBaseCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductStructureBaseInsert", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDCALENDARTYPE - Правило определения даты начала - тип календаля</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureBaseInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductStructureBaseInsert", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDCALENDARTYPE - Правило определения даты начала - тип календаля</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureBaseUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductStructureBaseUpdate", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDCALENDARTYPE - Правило определения даты начала - тип календаля</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureBaseModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductStructureBaseUpdate", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public void dsB2BProductStructureBaseDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductStructureBaseDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDCALENDARTYPE - Правило определения даты начала - тип календаля</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDCALENDARTYPE - Правило определения даты начала - тип календаля</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductStructureBaseBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductStructureBaseBrowseListByParam", "dsB2BProductStructureBaseBrowseListByParamCount", params);
        return result;
    }





}
