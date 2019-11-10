/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;


/**
 * Фасад для сущности B2BProductIntegrationRoute
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODINTEGROUTE",idFieldName="PRODINTEGROUTEID")
@BOName("B2BProductIntegrationRoute")
public class B2BProductIntegrationRouteFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODINTEGROUTEID - ИД роутинга интеграции</LI>
     * <LI>PRODUCTNAME - Cистемное наименование продукта ОИС</LI>
     * <LI>PROGRAMNAME - Системное наименование программы ОИС</LI>
     * <LI>ROUTE - Имя таблицы, по системному имени которой будет происходить интеграция</LI>
     * <LI>METHODNAME - метод browse для выборки данных из таблицы указанной в route</LI>
     * <LI>SERVICENAME - имя сервиса, в котором реализован метод</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINTEGROUTEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductIntegrationRouteCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductIntegrationRouteInsert", params);
        result.put("PRODINTEGROUTEID", params.get("PRODINTEGROUTEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODINTEGROUTEID - ИД роутинга интеграции</LI>
     * <LI>PRODUCTNAME - Cистемное наименование продукта ОИС</LI>
     * <LI>PROGRAMNAME - Системное наименование программы ОИС</LI>
     * <LI>ROUTE - Имя таблицы, по системному имени которой будет происходить интеграция</LI>
     * <LI>METHODNAME - метод browse для выборки данных из таблицы указанной в route</LI>
     * <LI>SERVICENAME - имя сервиса, в котором реализован метод</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINTEGROUTEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINTEGROUTEID"})
    public Map<String,Object> dsB2BProductIntegrationRouteInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductIntegrationRouteInsert", params);
        result.put("PRODINTEGROUTEID", params.get("PRODINTEGROUTEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODINTEGROUTEID - ИД роутинга интеграции</LI>
     * <LI>PRODUCTNAME - Cистемное наименование продукта ОИС</LI>
     * <LI>PROGRAMNAME - Системное наименование программы ОИС</LI>
     * <LI>ROUTE - Имя таблицы, по системному имени которой будет происходить интеграция</LI>
     * <LI>METHODNAME - метод browse для выборки данных из таблицы указанной в route</LI>
     * <LI>SERVICENAME - имя сервиса, в котором реализован метод</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINTEGROUTEID - ИД</LI>>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINTEGROUTEID"})
    public Map<String,Object> dsB2BProductIntegrationRouteUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductIntegrationRouteUpdate", params);
        result.put("PRODINTEGROUTEID", params.get("PRODINTEGROUTEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODINTEGROUTEID - ИД роутинга интеграции</LI>
     * <LI>PRODUCTNAME - Cистемное наименование продукта ОИС</LI>
     * <LI>PROGRAMNAME - Системное наименование программы ОИС</LI>
     * <LI>ROUTE - Имя таблицы, по системному имени которой будет происходить интеграция</LI>
     * <LI>METHODNAME - метод browse для выборки данных из таблицы указанной в route</LI>
     * <LI>SERVICENAME - имя сервиса, в котором реализован метод</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINTEGROUTEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINTEGROUTEID"})
    public Map<String,Object> dsB2BProductIntegrationRouteModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductIntegrationRouteUpdate", params);
        result.put("PRODINTEGROUTEID", params.get("PRODINTEGROUTEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODINTEGROUTEID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINTEGROUTEID"})
    public void dsB2BProductIntegrationRouteDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductIntegrationRouteDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODINTEGROUTEID - ИД роутинга интеграции</LI>
     * <LI>PRODUCTNAME - Cистемное наименование продукта ОИС</LI>
     * <LI>PROGRAMNAME - Системное наименование программы ОИС</LI>
     * <LI>ROUTE - Имя таблицы, по системному имени которой будет происходить интеграция</LI>
     * <LI>METHODNAME - метод browse для выборки данных из таблицы указанной в route</LI>
     * <LI>SERVICENAME - имя сервиса, в котором реализован метод</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINTEGROUTEID - ИД роутинга интеграции</LI>
     * <LI>PRODUCTNAME - Cистемное наименование продукта ОИС</LI>
     * <LI>PROGRAMNAME - Системное наименование программы ОИС</LI>
     * <LI>ROUTE - Имя таблицы, по системному имени которой будет происходить интеграция</LI>
     * <LI>METHODNAME - метод browse для выборки данных из таблицы указанной в route</LI>
     * <LI>SERVICENAME - имя сервиса, в котором реализован метод</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductIntegrationRouteBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductIntegrationRouteBrowseListByParam", "dsB2BProductIntegrationRouteBrowseListByParamCount", params);
        return result;
    }





}
