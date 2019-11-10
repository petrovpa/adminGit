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
 * Фасад для сущности B2BBankStateTemplateCondition
 *
 * @author reson
 */
@IdGen(entityName="B2B_BANKSTATETEMPLATECOND",idFieldName="BANKSTATETEMPLATECONDID")
@BOName("B2BBankStateTemplateCondition")
public class B2BBankStateTemplateConditionFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>BANKSTATETEMPLATECONDID - ИД условия шаблона обработки</LI>
     * <LI>NUM - Порядковый номер</LI>
     * <LI>VALUE - Значение блока назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATECONDID - ИД условия шаблона обработки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankStateTemplateConditionCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankStateTemplateConditionInsert", params);
        result.put("BANKSTATETEMPLATECONDID", params.get("BANKSTATETEMPLATECONDID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>BANKSTATETEMPLATECONDID - ИД условия шаблона обработки</LI>
     * <LI>NUM - Порядковый номер</LI>
     * <LI>VALUE - Значение блока назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATECONDID - ИД условия шаблона обработки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATECONDID"})
    public Map<String,Object> dsB2BBankStateTemplateConditionInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankStateTemplateConditionInsert", params);
        result.put("BANKSTATETEMPLATECONDID", params.get("BANKSTATETEMPLATECONDID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>BANKSTATETEMPLATECONDID - ИД условия шаблона обработки</LI>
     * <LI>NUM - Порядковый номер</LI>
     * <LI>VALUE - Значение блока назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATECONDID - ИД условия шаблона обработки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATECONDID"})
    public Map<String,Object> dsB2BBankStateTemplateConditionUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankStateTemplateConditionUpdate", params);
        result.put("BANKSTATETEMPLATECONDID", params.get("BANKSTATETEMPLATECONDID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>BANKSTATETEMPLATECONDID - ИД условия шаблона обработки</LI>
     * <LI>NUM - Порядковый номер</LI>
     * <LI>VALUE - Значение блока назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATECONDID - ИД условия шаблона обработки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATECONDID"})
    public Map<String,Object> dsB2BBankStateTemplateConditionModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankStateTemplateConditionUpdate", params);
        result.put("BANKSTATETEMPLATECONDID", params.get("BANKSTATETEMPLATECONDID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATECONDID - ИД условия шаблона обработки</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATECONDID"})
    public void dsB2BBankStateTemplateConditionDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BBankStateTemplateConditionDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>BANKSTATETEMPLATECONDID - ИД условия шаблона обработки</LI>
     * <LI>NUM - Порядковый номер</LI>
     * <LI>VALUE - Значение блока назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>BANKSTATETEMPLATECONDID - ИД условия шаблона обработки</LI>
     * <LI>NUM - Порядковый номер</LI>
     * <LI>VALUE - Значение блока назначения платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankStateTemplateConditionBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BBankStateTemplateConditionBrowseListByParam", "dsB2BBankStateTemplateConditionBrowseListByParamCount", params);
        return result;
    }





}
