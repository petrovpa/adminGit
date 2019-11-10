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
 * Фасад для сущности B2BProductDefaultValue
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODDEFVAL",idFieldName="PRODDEFVALID")
@BOName("B2BProductDefaultValue")
public class B2BProductDefaultValueFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFTYPE - Тип</LI>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"DEFTYPE", "NAME", "PRODCONFID", "VALUE"})
    public Map<String,Object> dsB2BProductDefaultValueCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDefaultValueInsert", params);
        result.put("PRODDEFVALID", params.get("PRODDEFVALID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFTYPE - Тип</LI>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"DEFTYPE", "PRODDEFVALID", "NAME", "PRODCONFID", "VALUE"})
    public Map<String,Object> dsB2BProductDefaultValueInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductDefaultValueInsert", params);
        result.put("PRODDEFVALID", params.get("PRODDEFVALID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFTYPE - Тип</LI>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDEFVALID"})
    public Map<String,Object> dsB2BProductDefaultValueUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDefaultValueUpdate", params);
        result.put("PRODDEFVALID", params.get("PRODDEFVALID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFTYPE - Тип</LI>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDEFVALID"})
    public Map<String,Object> dsB2BProductDefaultValueModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductDefaultValueUpdate", params);
        result.put("PRODDEFVALID", params.get("PRODDEFVALID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODDEFVALID"})
    public void dsB2BProductDefaultValueDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductDefaultValueDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFTYPE - Тип</LI>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DEFTYPE - Тип</LI>
     * <LI>PRODDEFVALID - ИД значения по-умолчанию</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductDefaultValueBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductDefaultValueBrowseListByParam", "dsB2BProductDefaultValueBrowseListByParamCount", params);
        return result;
    }





}
