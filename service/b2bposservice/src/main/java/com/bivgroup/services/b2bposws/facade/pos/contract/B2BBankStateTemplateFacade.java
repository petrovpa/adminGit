/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BBankStateTemplate
 *
 * @author reson
 */
@IdGen(entityName="B2B_BANKSTATETEMPLATE",idFieldName="BANKSTATETEMPLATEID")
@BOName("B2BBankStateTemplate")
public class B2BBankStateTemplateFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CHECKINGACCOUNT - Расчетный счет</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона</LI>
     * <LI>ISNOTUSE - Признак для отключения</LI>
     * <LI>NAME - Наименоание</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankStateTemplateCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankStateTemplateInsert", params);
        result.put("BANKSTATETEMPLATEID", params.get("BANKSTATETEMPLATEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CHECKINGACCOUNT - Расчетный счет</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона</LI>
     * <LI>ISNOTUSE - Признак для отключения</LI>
     * <LI>NAME - Наименоание</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATEID"})
    public Map<String,Object> dsB2BBankStateTemplateInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankStateTemplateInsert", params);
        result.put("BANKSTATETEMPLATEID", params.get("BANKSTATETEMPLATEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CHECKINGACCOUNT - Расчетный счет</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона</LI>
     * <LI>ISNOTUSE - Признак для отключения</LI>
     * <LI>NAME - Наименоание</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATEID"})
    public Map<String,Object> dsB2BBankStateTemplateUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankStateTemplateUpdate", params);
        result.put("BANKSTATETEMPLATEID", params.get("BANKSTATETEMPLATEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CHECKINGACCOUNT - Расчетный счет</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона</LI>
     * <LI>ISNOTUSE - Признак для отключения</LI>
     * <LI>NAME - Наименоание</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATEID"})
    public Map<String,Object> dsB2BBankStateTemplateModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankStateTemplateUpdate", params);
        result.put("BANKSTATETEMPLATEID", params.get("BANKSTATETEMPLATEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATEID"})
    public void dsB2BBankStateTemplateDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BBankStateTemplateDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CHECKINGACCOUNT - Расчетный счет</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона</LI>
     * <LI>ISNOTUSE - Признак для отключения</LI>
     * <LI>NAME - Наименоание</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHECKINGACCOUNT - Расчетный счет</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона</LI>
     * <LI>ISNOTUSE - Признак для отключения</LI>
     * <LI>NAME - Наименоание</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankStateTemplateBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BBankStateTemplateBrowseListByParam", "dsB2BBankStateTemplateBrowseListByParamCount", params);
        return result;
    }





}
