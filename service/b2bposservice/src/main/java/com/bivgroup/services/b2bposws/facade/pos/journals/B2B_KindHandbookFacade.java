/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.journals;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2B_KindHandbook
 *
 * @author reson
 */
@IdGen(entityName="B2B_KINDHANDBOOK",idFieldName="ID")
@BOName("B2B_KindHandbook")
public class B2B_KindHandbookFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CATEGORY - содержит классификацию справочника по категории</LI>
     * <LI>HBDATAVERSIONID - Версия данных справочника</LI>
     * <LI>ID - ИД</LI>
     * <LI>IMPLEMENTATIONID - ссылка на объект учета «Способ реализации справочника»</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования списка объектов</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_KindHandbookCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_KindHandbookInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CATEGORY - содержит классификацию справочника по категории</LI>
     * <LI>HBDATAVERSIONID - Версия данных справочника</LI>
     * <LI>ID - ИД</LI>
     * <LI>IMPLEMENTATIONID - ссылка на объект учета «Способ реализации справочника»</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования списка объектов</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_KindHandbookInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_KindHandbookInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CATEGORY - содержит классификацию справочника по категории</LI>
     * <LI>HBDATAVERSIONID - Версия данных справочника</LI>
     * <LI>ID - ИД</LI>
     * <LI>IMPLEMENTATIONID - ссылка на объект учета «Способ реализации справочника»</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования списка объектов</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_KindHandbookUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_KindHandbookUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CATEGORY - содержит классификацию справочника по категории</LI>
     * <LI>HBDATAVERSIONID - Версия данных справочника</LI>
     * <LI>ID - ИД</LI>
     * <LI>IMPLEMENTATIONID - ссылка на объект учета «Способ реализации справочника»</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования списка объектов</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_KindHandbookModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_KindHandbookUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public void dsB2B_KindHandbookDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2B_KindHandbookDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CATEGORY - содержит классификацию справочника по категории</LI>
     * <LI>HBDATAVERSIONID - Версия данных справочника</LI>
     * <LI>ID - ИД</LI>
     * <LI>IMPLEMENTATIONID - ссылка на объект учета «Способ реализации справочника»</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования списка объектов</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CATEGORY - содержит классификацию справочника по категории</LI>
     * <LI>HBDATAVERSIONID - Версия данных справочника</LI>
     * <LI>ID - ИД</LI>
     * <LI>IMPLEMENTATIONID - ссылка на объект учета «Способ реализации справочника»</LI>
     * <LI>NAME - содержит название параметра</LI>
     * <LI>SQLDATA - содержит SQL запрос для формирования списка объектов</LI>
     * <LI>SYSNAME - содержит системное имя параметра</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_KindHandbookBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2B_KindHandbookBrowseListByParam", "dsB2B_KindHandbookBrowseListByParamCount", params);
        return result;
    }





}
