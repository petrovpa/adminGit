/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.sal;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности SAL_KindSource
 *
 * @author reson
 */
@IdGen(entityName="SAL_KINDSOURCE",idFieldName="ID")
@BOName("SAL_KindSource")
public class SAL_KindSourceFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_KindSourceCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_KindSourceInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_KindSourceInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_KindSourceInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_KindSourceUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_KindSourceUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_KindSourceModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_KindSourceUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public void dsSAL_KindSourceDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsSAL_KindSourceDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_KindSourceBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsSAL_KindSourceBrowseListByParam", "dsSAL_KindSourceBrowseListByParamCount", params);
        return result;
    }





}
