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
 * Фасад для сущности WSTemplate
 *
 * @author reson
 */
@IdGen(entityName="WS_TEMPLATE",idFieldName="TEMPLATEID")
@BOName("WSTemplate")
public class WSTemplateFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEID - ИД записи</LI>
     * <LI>ISONEPRODUCT - Признак того, что продукт в шаблоне единственный</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODCODEXPATH - Xpath путь для получения кода продукта из XML</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDSCHEME2 - Дополнительная схема валидации входящих данных</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSTemplateCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSTemplateInsert", params);
        result.put("TEMPLATEID", params.get("TEMPLATEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEID - ИД записи</LI>
     * <LI>ISONEPRODUCT - Признак того, что продукт в шаблоне единственный</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODCODEXPATH - Xpath путь для получения кода продукта из XML</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDSCHEME2 - Дополнительная схема валидации входящих данных</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEID"})
    public Map<String,Object> dsWSTemplateInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSTemplateInsert", params);
        result.put("TEMPLATEID", params.get("TEMPLATEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEID - ИД записи</LI>
     * <LI>ISONEPRODUCT - Признак того, что продукт в шаблоне единственный</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODCODEXPATH - Xpath путь для получения кода продукта из XML</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDSCHEME2 - Дополнительная схема валидации входящих данных</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEID"})
    public Map<String,Object> dsWSTemplateUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSTemplateUpdate", params);
        result.put("TEMPLATEID", params.get("TEMPLATEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEID - ИД записи</LI>
     * <LI>ISONEPRODUCT - Признак того, что продукт в шаблоне единственный</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODCODEXPATH - Xpath путь для получения кода продукта из XML</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDSCHEME2 - Дополнительная схема валидации входящих данных</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEID"})
    public Map<String,Object> dsWSTemplateModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSTemplateUpdate", params);
        result.put("TEMPLATEID", params.get("TEMPLATEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>TEMPLATEID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"TEMPLATEID"})
    public void dsWSTemplateDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsWSTemplateDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEID - ИД записи</LI>
     * <LI>ISONEPRODUCT - Признак того, что продукт в шаблоне единственный</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODCODEXPATH - Xpath путь для получения кода продукта из XML</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDSCHEME2 - Дополнительная схема валидации входящих данных</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>DESCRIPTION - Описание</LI>
     * <LI>TEMPLATEID - ИД записи</LI>
     * <LI>ISONEPRODUCT - Признак того, что продукт в шаблоне единственный</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PRODCODEXPATH - Xpath путь для получения кода продукта из XML</LI>
     * <LI>XSDSCHEME - Схема валидации входящих данных</LI>
     * <LI>XSDSCHEME2 - Дополнительная схема валидации входящих данных</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSTemplateBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsWSTemplateBrowseListByParam", "dsWSTemplateBrowseListByParamCount", params);
        return result;
    }





}
