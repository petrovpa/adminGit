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
 * Фасад для сущности B2BProductStructureRisk
 *
 * @author reson
 */
@Discriminator(4)
@IdGen(entityName="B2B_PRODSTRUCT",idFieldName="PRODSTRUCTID")
@BOName("B2BProductStructureRisk")
public class B2BProductStructureRiskFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
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
    public Map<String,Object> dsB2BProductStructureRiskCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductStructureRiskInsert", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
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
    public Map<String,Object> dsB2BProductStructureRiskInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductStructureRiskInsert", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
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
    public Map<String,Object> dsB2BProductStructureRiskUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductStructureRiskUpdate", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
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
    public Map<String,Object> dsB2BProductStructureRiskModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductStructureRiskUpdate", params);
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
    public void dsB2BProductStructureRiskDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductStructureRiskDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDCALENDARTYPE - Правило определения даты начала - тип календаля</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>HBDATAVERID - ИД версии данных справочника расш. атрибутов объекта структуры</LI>
     * <LI>HINT - Подсказка</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>INSCOVERID - Страховое покрытие</LI>
     * <LI>INSKINDID - Вид страхования</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDCALENDARTYPE - Правило определения даты начала - тип календаля</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * <LI>SYSNAME - Псевдоним</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductStructureRiskBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductStructureRiskBrowseListByParam", "dsB2BProductStructureRiskBrowseListByParamCount", params);
        return result;
    }





}
