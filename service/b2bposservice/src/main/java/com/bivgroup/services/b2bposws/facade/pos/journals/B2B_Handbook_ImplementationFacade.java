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
 * Фасад для сущности B2B_Handbook_Implementation
 *
 * @author reson
 */
@IdGen(entityName="B2B_HANDBOOK_IMPLEMENTATION",idFieldName="ID")
@BOName("B2B_Handbook_Implementation")
public class B2B_Handbook_ImplementationFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название способа</LI>
     * <LI>SYSNAME - содержит служебное название способа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_Handbook_ImplementationCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_Handbook_ImplementationInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название способа</LI>
     * <LI>SYSNAME - содержит служебное название способа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_Handbook_ImplementationInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2B_Handbook_ImplementationInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название способа</LI>
     * <LI>SYSNAME - содержит служебное название способа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_Handbook_ImplementationUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_Handbook_ImplementationUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название способа</LI>
     * <LI>SYSNAME - содержит служебное название способа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String,Object> dsB2B_Handbook_ImplementationModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2B_Handbook_ImplementationUpdate", params);
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
    public void dsB2B_Handbook_ImplementationDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2B_Handbook_ImplementationDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название способа</LI>
     * <LI>SYSNAME - содержит служебное название способа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД</LI>
     * <LI>NAME - содержит название способа</LI>
     * <LI>SYSNAME - содержит служебное название способа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2B_Handbook_ImplementationBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2B_Handbook_ImplementationBrowseListByParam", "dsB2B_Handbook_ImplementationBrowseListByParamCount", params);
        return result;
    }





}
