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
 * Фасад для сущности B2BTerm
 *
 * @author reson
 */
@IdGen(entityName="B2B_TERM",idFieldName="TERMID")
@BOName("B2BTerm")
public class B2BTermFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DAYCOUNT - Количество дней</LI>
     * <LI>TERMID - ИД записи</LI>
     * <LI>MONTHCOUNT - Количество месяцев</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>YEARCOUNT - Количество лет</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TERMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BTermCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BTermInsert", params);
        result.put("TERMID", params.get("TERMID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DAYCOUNT - Количество дней</LI>
     * <LI>TERMID - ИД записи</LI>
     * <LI>MONTHCOUNT - Количество месяцев</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>YEARCOUNT - Количество лет</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TERMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TERMID"})
    public Map<String,Object> dsB2BTermInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BTermInsert", params);
        result.put("TERMID", params.get("TERMID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DAYCOUNT - Количество дней</LI>
     * <LI>TERMID - ИД записи</LI>
     * <LI>MONTHCOUNT - Количество месяцев</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>YEARCOUNT - Количество лет</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TERMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TERMID"})
    public Map<String,Object> dsB2BTermUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BTermUpdate", params);
        result.put("TERMID", params.get("TERMID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DAYCOUNT - Количество дней</LI>
     * <LI>TERMID - ИД записи</LI>
     * <LI>MONTHCOUNT - Количество месяцев</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>YEARCOUNT - Количество лет</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TERMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"TERMID"})
    public Map<String,Object> dsB2BTermModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BTermUpdate", params);
        result.put("TERMID", params.get("TERMID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>TERMID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"TERMID"})
    public void dsB2BTermDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BTermDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DAYCOUNT - Количество дней</LI>
     * <LI>TERMID - ИД записи</LI>
     * <LI>MONTHCOUNT - Количество месяцев</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>YEARCOUNT - Количество лет</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DAYCOUNT - Количество дней</LI>
     * <LI>TERMID - ИД записи</LI>
     * <LI>MONTHCOUNT - Количество месяцев</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>YEARCOUNT - Количество лет</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BTermBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BTermBrowseListByParam", "dsB2BTermBrowseListByParamCount", params);
        return result;
    }





}
