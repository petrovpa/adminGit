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
 * Фасад для сущности B2BProductAdditionalChangeType
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODADDCHT",idFieldName="PRODADDCHTID")
@BOName("B2BProductAdditionalChangeType")
public class B2BProductAdditionalChangeTypeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>COPYINSOBJ - Флаг копирования объекта страхования</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODADDCHTID - ИД типа изменения по договору</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAGEFLOWNAME - Наименование бизнес процесса</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TEMPLATE - Шаблон содержимого доп. соглашения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODADDCHTID - ИД типа изменения по договору</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"NAME", "PAGEFLOWNAME", "PRODCONFID"})
    public Map<String,Object> dsB2BProductAdditionalChangeTypeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductAdditionalChangeTypeInsert", params);
        result.put("PRODADDCHTID", params.get("PRODADDCHTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>COPYINSOBJ - Флаг копирования объекта страхования</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODADDCHTID - ИД типа изменения по договору</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAGEFLOWNAME - Наименование бизнес процесса</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TEMPLATE - Шаблон содержимого доп. соглашения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODADDCHTID - ИД типа изменения по договору</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODADDCHTID", "NAME", "PAGEFLOWNAME", "PRODCONFID"})
    public Map<String,Object> dsB2BProductAdditionalChangeTypeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductAdditionalChangeTypeInsert", params);
        result.put("PRODADDCHTID", params.get("PRODADDCHTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>COPYINSOBJ - Флаг копирования объекта страхования</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODADDCHTID - ИД типа изменения по договору</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAGEFLOWNAME - Наименование бизнес процесса</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TEMPLATE - Шаблон содержимого доп. соглашения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODADDCHTID - ИД типа изменения по договору</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODADDCHTID"})
    public Map<String,Object> dsB2BProductAdditionalChangeTypeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductAdditionalChangeTypeUpdate", params);
        result.put("PRODADDCHTID", params.get("PRODADDCHTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>COPYINSOBJ - Флаг копирования объекта страхования</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODADDCHTID - ИД типа изменения по договору</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAGEFLOWNAME - Наименование бизнес процесса</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TEMPLATE - Шаблон содержимого доп. соглашения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODADDCHTID - ИД типа изменения по договору</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODADDCHTID"})
    public Map<String,Object> dsB2BProductAdditionalChangeTypeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductAdditionalChangeTypeUpdate", params);
        result.put("PRODADDCHTID", params.get("PRODADDCHTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODADDCHTID - ИД типа изменения по договору</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODADDCHTID"})
    public void dsB2BProductAdditionalChangeTypeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductAdditionalChangeTypeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>COPYINSOBJ - Флаг копирования объекта страхования</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODADDCHTID - ИД типа изменения по договору</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAGEFLOWNAME - Наименование бизнес процесса</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TEMPLATE - Шаблон содержимого доп. соглашения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>COPYINSOBJ - Флаг копирования объекта страхования</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PRODADDCHTID - ИД типа изменения по договору</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAGEFLOWNAME - Наименование бизнес процесса</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TEMPLATE - Шаблон содержимого доп. соглашения</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductAdditionalChangeTypeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductAdditionalChangeTypeBrowseListByParam", "dsB2BProductAdditionalChangeTypeBrowseListByParamCount", params);
        return result;
    }





}
