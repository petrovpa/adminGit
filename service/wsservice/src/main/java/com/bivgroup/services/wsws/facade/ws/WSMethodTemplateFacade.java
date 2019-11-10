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
 * Фасад для сущности WSMethodTemplate
 *
 * @author reson
 */
@IdGen(entityName="WS_METHODTEMPLATE",idFieldName="METHODTEMPLATEID")
@BOName("WSMethodTemplate")
public class WSMethodTemplateFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>METHODTEMPLATEID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>METHODTEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSMethodTemplateCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSMethodTemplateInsert", params);
        result.put("METHODTEMPLATEID", params.get("METHODTEMPLATEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>METHODTEMPLATEID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>METHODTEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"METHODTEMPLATEID"})
    public Map<String,Object> dsWSMethodTemplateInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsWSMethodTemplateInsert", params);
        result.put("METHODTEMPLATEID", params.get("METHODTEMPLATEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>METHODTEMPLATEID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>METHODTEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"METHODTEMPLATEID"})
    public Map<String,Object> dsWSMethodTemplateUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSMethodTemplateUpdate", params);
        result.put("METHODTEMPLATEID", params.get("METHODTEMPLATEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>METHODTEMPLATEID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>METHODTEMPLATEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"METHODTEMPLATEID"})
    public Map<String,Object> dsWSMethodTemplateModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsWSMethodTemplateUpdate", params);
        result.put("METHODTEMPLATEID", params.get("METHODTEMPLATEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>METHODTEMPLATEID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"METHODTEMPLATEID"})
    public void dsWSMethodTemplateDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsWSMethodTemplateDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>METHODTEMPLATEID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>METHODTEMPLATEID - ИД записи</LI>
     * <LI>METHODID - ИД метода</LI>
     * <LI>TEMPLATEID - ИД шаблона</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsWSMethodTemplateBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsWSMethodTemplateBrowseListByParam", "dsWSMethodTemplateBrowseListByParamCount", params);
        return result;
    }





}
