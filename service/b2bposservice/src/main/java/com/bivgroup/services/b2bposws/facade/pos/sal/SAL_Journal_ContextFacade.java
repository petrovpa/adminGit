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
 * Фасад для сущности SAL_Journal_Context
 *
 * @author reson
 */
@IdGen(entityName="SAL_JOURNAL_CONTEXT",idFieldName="ID")
@BOName("SAL_Journal_Context")
public class SAL_Journal_ContextFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал</LI>
     * <LI>PROPERTYSOURCEID - Признак</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_Journal_ContextCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_Journal_ContextInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал</LI>
     * <LI>PROPERTYSOURCEID - Признак</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Journal_ContextInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsSAL_Journal_ContextInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал</LI>
     * <LI>PROPERTYSOURCEID - Признак</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Journal_ContextUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_Journal_ContextUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал</LI>
     * <LI>PROPERTYSOURCEID - Признак</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsSAL_Journal_ContextModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsSAL_Journal_ContextUpdate", params);
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
    public void dsSAL_Journal_ContextDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsSAL_Journal_ContextDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал</LI>
     * <LI>PROPERTYSOURCEID - Признак</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД объекта</LI>
     * <LI>JOURNALID - Журнал</LI>
     * <LI>PROPERTYSOURCEID - Признак</LI>
     * <LI>VALUE - Значение</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsSAL_Journal_ContextBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsSAL_Journal_ContextBrowseListByParam", "dsSAL_Journal_ContextBrowseListByParamCount", params);
        return result;
    }





}
