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
 * Фасад для сущности B2BInsuranceEvent
 *
 * @author reson
 */
@IdGen(entityName="B2B_INSEVENT",idFieldName="INSEVENTID")
@BOName("B2BInsuranceEvent")
public class B2BInsuranceEventFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>INSEVENTID - ИД события</LI>
     * <LI>INSELEMRISKID - ИД элементарного риска</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSEVENTID - ИД события</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInsuranceEventCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInsuranceEventInsert", params);
        result.put("INSEVENTID", params.get("INSEVENTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>INSEVENTID - ИД события</LI>
     * <LI>INSELEMRISKID - ИД элементарного риска</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSEVENTID - ИД события</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSEVENTID"})
    public Map<String,Object> dsB2BInsuranceEventInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInsuranceEventInsert", params);
        result.put("INSEVENTID", params.get("INSEVENTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>INSEVENTID - ИД события</LI>
     * <LI>INSELEMRISKID - ИД элементарного риска</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSEVENTID - ИД события</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSEVENTID"})
    public Map<String,Object> dsB2BInsuranceEventUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInsuranceEventUpdate", params);
        result.put("INSEVENTID", params.get("INSEVENTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>INSEVENTID - ИД события</LI>
     * <LI>INSELEMRISKID - ИД элементарного риска</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSEVENTID - ИД события</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSEVENTID"})
    public Map<String,Object> dsB2BInsuranceEventModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInsuranceEventUpdate", params);
        result.put("INSEVENTID", params.get("INSEVENTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INSEVENTID - ИД события</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSEVENTID"})
    public void dsB2BInsuranceEventDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BInsuranceEventDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>INSEVENTID - ИД события</LI>
     * <LI>INSELEMRISKID - ИД элементарного риска</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>HINT - Подсказка</LI>
     * <LI>INSEVENTID - ИД события</LI>
     * <LI>INSELEMRISKID - ИД элементарного риска</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInsuranceEventBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BInsuranceEventBrowseListByParam", "dsB2BInsuranceEventBrowseListByParamCount", params);
        return result;
    }





}
