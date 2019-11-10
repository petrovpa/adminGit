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
 * Фасад для сущности B2BProductActivationType
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODACTIVTYPE",idFieldName="PRODACTIVTYPEID")
@BOName("B2BProductActivationType")
public class B2BProductActivationTypeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODACTIVTYPEID - ИД типа активаций</LI>
     * <LI>MASTERURL - URL страницы активации продуктов</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODACTIVTYPEID - ИД типа активаций</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductActivationTypeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductActivationTypeInsert", params);
        result.put("PRODACTIVTYPEID", params.get("PRODACTIVTYPEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODACTIVTYPEID - ИД типа активаций</LI>
     * <LI>MASTERURL - URL страницы активации продуктов</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODACTIVTYPEID - ИД типа активаций</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODACTIVTYPEID"})
    public Map<String,Object> dsB2BProductActivationTypeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductActivationTypeInsert", params);
        result.put("PRODACTIVTYPEID", params.get("PRODACTIVTYPEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODACTIVTYPEID - ИД типа активаций</LI>
     * <LI>MASTERURL - URL страницы активации продуктов</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODACTIVTYPEID - ИД типа активаций</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODACTIVTYPEID"})
    public Map<String,Object> dsB2BProductActivationTypeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductActivationTypeUpdate", params);
        result.put("PRODACTIVTYPEID", params.get("PRODACTIVTYPEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODACTIVTYPEID - ИД типа активаций</LI>
     * <LI>MASTERURL - URL страницы активации продуктов</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODACTIVTYPEID - ИД типа активаций</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODACTIVTYPEID"})
    public Map<String,Object> dsB2BProductActivationTypeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductActivationTypeUpdate", params);
        result.put("PRODACTIVTYPEID", params.get("PRODACTIVTYPEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODACTIVTYPEID - ИД типа активаций</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODACTIVTYPEID"})
    public void dsB2BProductActivationTypeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductActivationTypeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODACTIVTYPEID - ИД типа активаций</LI>
     * <LI>MASTERURL - URL страницы активации продуктов</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODACTIVTYPEID - ИД типа активаций</LI>
     * <LI>MASTERURL - URL страницы активации продуктов</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductActivationTypeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductActivationTypeBrowseListByParam", "dsB2BProductActivationTypeBrowseListByParamCount", params);
        return result;
    }





}
