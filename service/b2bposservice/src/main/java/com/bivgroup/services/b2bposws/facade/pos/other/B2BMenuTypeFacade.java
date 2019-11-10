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
 * Фасад для сущности B2BMenuType
 *
 * @author reson
 */
@IdGen(entityName="B2B_MENUTYPE",idFieldName="MENUTYPEID")
@BOName("B2BMenuType")
public class B2BMenuTypeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUTYPEID - Ид</LI>
     * <LI>NAME - Наименование типа меню</LI>
     * <LI>SYSNAME - Cистемное наименование типа меню</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUTYPEID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMenuTypeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMenuTypeInsert", params);
        result.put("MENUTYPEID", params.get("MENUTYPEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUTYPEID - Ид</LI>
     * <LI>NAME - Наименование типа меню</LI>
     * <LI>SYSNAME - Cистемное наименование типа меню</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUTYPEID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUTYPEID"})
    public Map<String,Object> dsB2BMenuTypeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMenuTypeInsert", params);
        result.put("MENUTYPEID", params.get("MENUTYPEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUTYPEID - Ид</LI>
     * <LI>NAME - Наименование типа меню</LI>
     * <LI>SYSNAME - Cистемное наименование типа меню</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUTYPEID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUTYPEID"})
    public Map<String,Object> dsB2BMenuTypeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMenuTypeUpdate", params);
        result.put("MENUTYPEID", params.get("MENUTYPEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUTYPEID - Ид</LI>
     * <LI>NAME - Наименование типа меню</LI>
     * <LI>SYSNAME - Cистемное наименование типа меню</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUTYPEID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUTYPEID"})
    public Map<String,Object> dsB2BMenuTypeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMenuTypeUpdate", params);
        result.put("MENUTYPEID", params.get("MENUTYPEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUTYPEID - Ид</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUTYPEID"})
    public void dsB2BMenuTypeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMenuTypeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUTYPEID - Ид</LI>
     * <LI>NAME - Наименование типа меню</LI>
     * <LI>SYSNAME - Cистемное наименование типа меню</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUTYPEID - Ид</LI>
     * <LI>NAME - Наименование типа меню</LI>
     * <LI>SYSNAME - Cистемное наименование типа меню</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMenuTypeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMenuTypeBrowseListByParam", "dsB2BMenuTypeBrowseListByParamCount", params);
        return result;
    }





}
