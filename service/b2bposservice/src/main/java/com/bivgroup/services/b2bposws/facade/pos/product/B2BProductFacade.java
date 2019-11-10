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
 * Фасад для сущности B2BProduct
 *
 * @author reson
 */
@IdGen(entityName="B2B_PROD",idFieldName="PRODID")
@BOName("B2BProduct")
public class B2BProductFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ASSURERID - ИД страховщика</LI>
     * <LI>EXTERNALCODE - Внешний код продукта</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>INSTERMKINDID - Вид</LI>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>ISMULTICONTR - Признак мультивалютного договора</LI>
     * <LI>MODELID - Модель учета</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>STRUCTROOTID - Структура</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"NAME"})
    public Map<String,Object> dsB2BProductCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductInsert", params);
        result.put("PRODID", params.get("PRODID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ASSURERID - ИД страховщика</LI>
     * <LI>EXTERNALCODE - Внешний код продукта</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>INSTERMKINDID - Вид</LI>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>ISMULTICONTR - Признак мультивалютного договора</LI>
     * <LI>MODELID - Модель учета</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>STRUCTROOTID - Структура</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODID", "NAME"})
    public Map<String,Object> dsB2BProductInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductInsert", params);
        result.put("PRODID", params.get("PRODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ASSURERID - ИД страховщика</LI>
     * <LI>EXTERNALCODE - Внешний код продукта</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>INSTERMKINDID - Вид</LI>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>ISMULTICONTR - Признак мультивалютного договора</LI>
     * <LI>MODELID - Модель учета</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>STRUCTROOTID - Структура</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODID"})
    public Map<String,Object> dsB2BProductUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductUpdate", params);
        result.put("PRODID", params.get("PRODID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ASSURERID - ИД страховщика</LI>
     * <LI>EXTERNALCODE - Внешний код продукта</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>INSTERMKINDID - Вид</LI>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>ISMULTICONTR - Признак мультивалютного договора</LI>
     * <LI>MODELID - Модель учета</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>STRUCTROOTID - Структура</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODID"})
    public Map<String,Object> dsB2BProductModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductUpdate", params);
        result.put("PRODID", params.get("PRODID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODID - ИД продукта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODID"})
    public void dsB2BProductDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ASSURERID - ИД страховщика</LI>
     * <LI>EXTERNALCODE - Внешний код продукта</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>INSTERMKINDID - Вид</LI>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>ISMULTICONTR - Признак мультивалютного договора</LI>
     * <LI>MODELID - Модель учета</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>STRUCTROOTID - Структура</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ASSURERID - ИД страховщика</LI>
     * <LI>EXTERNALCODE - Внешний код продукта</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>INSTERMKINDID - Вид</LI>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>ISMULTICONTR - Признак мультивалютного договора</LI>
     * <LI>MODELID - Модель учета</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>STRUCTROOTID - Структура</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductBrowseListByParam", "dsB2BProductBrowseListByParamCount", params);
        return result;
    }





}
