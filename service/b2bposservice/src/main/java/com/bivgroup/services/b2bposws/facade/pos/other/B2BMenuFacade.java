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
 * Фасад для сущности B2BMenu
 *
 * @author reson
 */
@IdGen(entityName="B2B_MENU",idFieldName="MENUID")
@BOName("B2BMenu")
public class B2BMenuFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTIONURL - URL</LI>
     * <LI>MENUID - Ид</LI>
     * <LI>MENUTYPEID - Тип меню</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARENTMENUID - Меню заголовок</LI>
     * <LI>PICTUREURL - URL Картинки</LI>
     * <LI>PRODCONFIGID - Id конфигурации</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMenuCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMenuInsert", params);
        result.put("MENUID", params.get("MENUID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTIONURL - URL</LI>
     * <LI>MENUID - Ид</LI>
     * <LI>MENUTYPEID - Тип меню</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARENTMENUID - Меню заголовок</LI>
     * <LI>PICTUREURL - URL Картинки</LI>
     * <LI>PRODCONFIGID - Id конфигурации</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUID"})
    public Map<String,Object> dsB2BMenuInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMenuInsert", params);
        result.put("MENUID", params.get("MENUID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTIONURL - URL</LI>
     * <LI>MENUID - Ид</LI>
     * <LI>MENUTYPEID - Тип меню</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARENTMENUID - Меню заголовок</LI>
     * <LI>PICTUREURL - URL Картинки</LI>
     * <LI>PRODCONFIGID - Id конфигурации</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUID"})
    public Map<String,Object> dsB2BMenuUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMenuUpdate", params);
        result.put("MENUID", params.get("MENUID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTIONURL - URL</LI>
     * <LI>MENUID - Ид</LI>
     * <LI>MENUTYPEID - Тип меню</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARENTMENUID - Меню заголовок</LI>
     * <LI>PICTUREURL - URL Картинки</LI>
     * <LI>PRODCONFIGID - Id конфигурации</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MENUID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUID"})
    public Map<String,Object> dsB2BMenuModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMenuUpdate", params);
        result.put("MENUID", params.get("MENUID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MENUID - Ид</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MENUID"})
    public void dsB2BMenuDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMenuDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTIONURL - URL</LI>
     * <LI>MENUID - Ид</LI>
     * <LI>MENUTYPEID - Тип меню</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARENTMENUID - Меню заголовок</LI>
     * <LI>PICTUREURL - URL Картинки</LI>
     * <LI>PRODCONFIGID - Id конфигурации</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ACTIONURL - URL</LI>
     * <LI>MENUID - Ид</LI>
     * <LI>MENUTYPEID - Тип меню</LI>
     * <LI>NAME - Имя</LI>
     * <LI>PARENTMENUID - Меню заголовок</LI>
     * <LI>PICTUREURL - URL Картинки</LI>
     * <LI>PRODCONFIGID - Id конфигурации</LI>
     * <LI>SYSNAME - Системное имя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMenuBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMenuBrowseListByParam", "dsB2BMenuBrowseListByParamCount", params);
        return result;
    }





}
