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
 * Фасад для сущности B2BProductStructureSection
 *
 * @author reson
 */
@Discriminator(6)
@IdGen(entityName="B2B_PRODSTRUCT",idFieldName="PRODSTRUCTID")
@BOName("B2BProductStructureSection")
public class B2BProductStructureSectionFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов секции</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductStructureSectionCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductStructureSectionInsert", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов секции</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureSectionInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductStructureSectionInsert", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов секции</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureSectionUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductStructureSectionUpdate", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов секции</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureSectionModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductStructureSectionUpdate", params);
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
    public void dsB2BProductStructureSectionDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductStructureSectionDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов секции</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>EXTERNALCODE - Внешний код структуры</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов секции</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>REPEATED - Повторяется</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductStructureSectionBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductStructureSectionBrowseListByParam", "dsB2BProductStructureSectionBrowseListByParamCount", params);
        return result;
    }





}
