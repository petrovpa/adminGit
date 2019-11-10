/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.wsws.facade.ws;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности WSUserTemplate
 *
 * @author reson
 */
@IdGen(entityName="WS_USERTEMPLATE",idFieldName="USERTEMPLATEID")
@BOName("WSUserTemplate")
public class WSUserTemplateFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>USERTEMPLATEID - ИД записи</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERTEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSUserTemplateCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSUserTemplateInsert", params);
        result.put("USERTEMPLATEID", params.get("USERTEMPLATEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>USERTEMPLATEID - ИД записи</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERTEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERTEMPLATEID"})
    public Map<String,Object> dsWSUserTemplateInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSUserTemplateInsert", params);
        result.put("USERTEMPLATEID", params.get("USERTEMPLATEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>USERTEMPLATEID - ИД записи</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERTEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERTEMPLATEID"})
    public Map<String,Object> dsWSUserTemplateUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSUserTemplateUpdate", params);
        result.put("USERTEMPLATEID", params.get("USERTEMPLATEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>USERTEMPLATEID - ИД записи</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERTEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERTEMPLATEID"})
    public Map<String,Object> dsWSUserTemplateModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSUserTemplateUpdate", params);
        result.put("USERTEMPLATEID", params.get("USERTEMPLATEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>USERTEMPLATEID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"USERTEMPLATEID"})
    public void dsWSUserTemplateDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsWSUserTemplateDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>USERTEMPLATEID - ИД записи</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>USERTEMPLATEID - ИД записи</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSUserTemplateBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsWSUserTemplateBrowseListByParam", "dsWSUserTemplateBrowseListByParamCount", params);
        return result;
    }





}
