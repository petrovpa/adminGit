/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.other;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BMenuorgstruct
 *
 * @author reson
 */
@IdGen(entityName="B2B_MENUORGSTRUCT",idFieldName="MENUORGSTRUCTID")
@BOName("B2BMenuorgstruct")
public class B2BMenuorgstructFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUID - null</LI>
     * <LI>MENUORGSTRUCTID - null</LI>
     * <LI>ORGSTRUCTID - null</LI>
     * <LI>ROLEID - null</LI>
     * <LI>USERID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUORGSTRUCTID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMenuorgstructCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMenuorgstructInsert", params);
        result.put("MENUORGSTRUCTID", params.get("MENUORGSTRUCTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUID - null</LI>
     * <LI>MENUORGSTRUCTID - null</LI>
     * <LI>ORGSTRUCTID - null</LI>
     * <LI>ROLEID - null</LI>
     * <LI>USERID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUORGSTRUCTID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUORGSTRUCTID"})
    public Map<String,Object> dsB2BMenuorgstructInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMenuorgstructInsert", params);
        result.put("MENUORGSTRUCTID", params.get("MENUORGSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUID - null</LI>
     * <LI>MENUORGSTRUCTID - null</LI>
     * <LI>ORGSTRUCTID - null</LI>
     * <LI>ROLEID - null</LI>
     * <LI>USERID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUORGSTRUCTID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUORGSTRUCTID"})
    public Map<String,Object> dsB2BMenuorgstructUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMenuorgstructUpdate", params);
        result.put("MENUORGSTRUCTID", params.get("MENUORGSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUID - null</LI>
     * <LI>MENUORGSTRUCTID - null</LI>
     * <LI>ORGSTRUCTID - null</LI>
     * <LI>ROLEID - null</LI>
     * <LI>USERID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUORGSTRUCTID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUORGSTRUCTID"})
    public Map<String,Object> dsB2BMenuorgstructModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMenuorgstructUpdate", params);
        result.put("MENUORGSTRUCTID", params.get("MENUORGSTRUCTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUORGSTRUCTID - null</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUORGSTRUCTID"})
    public void dsB2BMenuorgstructDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMenuorgstructDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUID - null</LI>
     * <LI>MENUORGSTRUCTID - null</LI>
     * <LI>ORGSTRUCTID - null</LI>
     * <LI>ROLEID - null</LI>
     * <LI>USERID - null</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUID - null</LI>
     * <LI>MENUORGSTRUCTID - null</LI>
     * <LI>ORGSTRUCTID - null</LI>
     * <LI>ROLEID - null</LI>
     * <LI>USERID - null</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMenuorgstructBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMenuorgstructBrowseListByParam", "dsB2BMenuorgstructBrowseListByParamCount", params);
        return result;
    }





}
