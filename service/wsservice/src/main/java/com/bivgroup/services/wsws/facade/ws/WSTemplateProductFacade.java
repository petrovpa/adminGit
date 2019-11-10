/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.wsws.facade.ws;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности WSTemplateProduct
 *
 * @author reson
 */
@IdGen(entityName="WS_TEMPLATEPRODUCT",idFieldName="TEMPLATEPRODUCTID")
@BOName("WSTemplateProduct")
public class WSTemplateProductFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEPRODUCTID - ИД записи</LI>
     * <LI>JAXBCLASSNAME - Наименование класса для конвертации</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDTARGETNS - Конечный неймспейс</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEPRODUCTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSTemplateProductCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSTemplateProductInsert", params);
        result.put("TEMPLATEPRODUCTID", params.get("TEMPLATEPRODUCTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEPRODUCTID - ИД записи</LI>
     * <LI>JAXBCLASSNAME - Наименование класса для конвертации</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDTARGETNS - Конечный неймспейс</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEPRODUCTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEPRODUCTID"})
    public Map<String,Object> dsWSTemplateProductInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSTemplateProductInsert", params);
        result.put("TEMPLATEPRODUCTID", params.get("TEMPLATEPRODUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEPRODUCTID - ИД записи</LI>
     * <LI>JAXBCLASSNAME - Наименование класса для конвертации</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDTARGETNS - Конечный неймспейс</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEPRODUCTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEPRODUCTID"})
    public Map<String,Object> dsWSTemplateProductUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSTemplateProductUpdate", params);
        result.put("TEMPLATEPRODUCTID", params.get("TEMPLATEPRODUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEPRODUCTID - ИД записи</LI>
     * <LI>JAXBCLASSNAME - Наименование класса для конвертации</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDTARGETNS - Конечный неймспейс</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEPRODUCTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEPRODUCTID"})
    public Map<String,Object> dsWSTemplateProductModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSTemplateProductUpdate", params);
        result.put("TEMPLATEPRODUCTID", params.get("TEMPLATEPRODUCTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>TEMPLATEPRODUCTID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEPRODUCTID"})
    public void dsWSTemplateProductDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsWSTemplateProductDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEPRODUCTID - ИД записи</LI>
     * <LI>JAXBCLASSNAME - Наименование класса для конвертации</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDTARGETNS - Конечный неймспейс</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEPRODUCTID - ИД записи</LI>
     * <LI>JAXBCLASSNAME - Наименование класса для конвертации</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDTARGETNS - Конечный неймспейс</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSTemplateProductBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsWSTemplateProductBrowseListByParam", "dsWSTemplateProductBrowseListByParamCount", params);
        return result;
    }





}
