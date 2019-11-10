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
 * Фасад для сущности B2BProductConfig
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODCONF",idFieldName="PRODCONFID")
@BOName("B2BProductConfig")
public class B2BProductConfigFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CALCVERID - ИД версии калькулятора</LI>
     * <LI>HBDATAVERID - ИД версии справочника расш. атрибутов продукта</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVERID"})
    public Map<String,Object> dsB2BProductConfigCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductConfigInsert", params);
        result.put("PRODCONFID", params.get("PRODCONFID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CALCVERID - ИД версии калькулятора</LI>
     * <LI>HBDATAVERID - ИД версии справочника расш. атрибутов продукта</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODCONFID", "PRODVERID"})
    public Map<String,Object> dsB2BProductConfigInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductConfigInsert", params);
        result.put("PRODCONFID", params.get("PRODCONFID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CALCVERID - ИД версии калькулятора</LI>
     * <LI>HBDATAVERID - ИД версии справочника расш. атрибутов продукта</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODCONFID"})
    public Map<String,Object> dsB2BProductConfigUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductConfigUpdate", params);
        result.put("PRODCONFID", params.get("PRODCONFID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CALCVERID - ИД версии калькулятора</LI>
     * <LI>HBDATAVERID - ИД версии справочника расш. атрибутов продукта</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODCONFID"})
    public Map<String,Object> dsB2BProductConfigModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductConfigUpdate", params);
        result.put("PRODCONFID", params.get("PRODCONFID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODCONFID"})
    public void dsB2BProductConfigDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductConfigDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CALCVERID - ИД версии калькулятора</LI>
     * <LI>HBDATAVERID - ИД версии справочника расш. атрибутов продукта</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CALCVERID - ИД версии калькулятора</LI>
     * <LI>HBDATAVERID - ИД версии справочника расш. атрибутов продукта</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductConfigBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductConfigBrowseListByParam", "dsB2BProductConfigBrowseListByParamCount", params);
        return result;
    }





}
