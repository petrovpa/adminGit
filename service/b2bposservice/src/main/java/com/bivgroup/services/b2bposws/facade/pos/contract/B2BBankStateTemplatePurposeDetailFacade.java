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
 * Фасад для сущности B2BBankStateTemplatePurposeDetail
 *
 * @author reson
 */
@IdGen(entityName="B2B_BANKSTATETEMPLATEPDET",idFieldName="BANKSTATETEMPLATEPDETID")
@BOName("B2BBankStateTemplatePurposeDetail")
public class B2BBankStateTemplatePurposeDetailFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>CONSTANTVALUESTR - Константа – значение по умолчанию (строковое) блока назначения платежа, если заполнено не может быть изменено вручную</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>BANKSTATETEMPLATEPDETID - ИД шаблона детали назначения платежа</LI>
     * <LI>NAME - Наименование детали назначения платежа</LI>
     * <LI>NOTE - Примечание к шаблону детали назначения платежа</LI>
     * <LI>NUM - Порядковый номер – номер позиции в строке назначения платежа</LI>
     * <LI>SYSNAME - Системное наименование детали назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATEPDETID - ИД шаблона детали назначения платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATEID"})
    public Map<String,Object> dsB2BBankStateTemplatePurposeDetailCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankStateTemplatePurposeDetailInsert", params);
        result.put("BANKSTATETEMPLATEPDETID", params.get("BANKSTATETEMPLATEPDETID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>CONSTANTVALUESTR - Константа – значение по умолчанию (строковое) блока назначения платежа, если заполнено не может быть изменено вручную</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>BANKSTATETEMPLATEPDETID - ИД шаблона детали назначения платежа</LI>
     * <LI>NAME - Наименование детали назначения платежа</LI>
     * <LI>NOTE - Примечание к шаблону детали назначения платежа</LI>
     * <LI>NUM - Порядковый номер – номер позиции в строке назначения платежа</LI>
     * <LI>SYSNAME - Системное наименование детали назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATEPDETID - ИД шаблона детали назначения платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATEID", "BANKSTATETEMPLATEPDETID"})
    public Map<String,Object> dsB2BBankStateTemplatePurposeDetailInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankStateTemplatePurposeDetailInsert", params);
        result.put("BANKSTATETEMPLATEPDETID", params.get("BANKSTATETEMPLATEPDETID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>CONSTANTVALUESTR - Константа – значение по умолчанию (строковое) блока назначения платежа, если заполнено не может быть изменено вручную</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>BANKSTATETEMPLATEPDETID - ИД шаблона детали назначения платежа</LI>
     * <LI>NAME - Наименование детали назначения платежа</LI>
     * <LI>NOTE - Примечание к шаблону детали назначения платежа</LI>
     * <LI>NUM - Порядковый номер – номер позиции в строке назначения платежа</LI>
     * <LI>SYSNAME - Системное наименование детали назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATEPDETID - ИД шаблона детали назначения платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATEPDETID"})
    public Map<String,Object> dsB2BBankStateTemplatePurposeDetailUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankStateTemplatePurposeDetailUpdate", params);
        result.put("BANKSTATETEMPLATEPDETID", params.get("BANKSTATETEMPLATEPDETID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>CONSTANTVALUESTR - Константа – значение по умолчанию (строковое) блока назначения платежа, если заполнено не может быть изменено вручную</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>BANKSTATETEMPLATEPDETID - ИД шаблона детали назначения платежа</LI>
     * <LI>NAME - Наименование детали назначения платежа</LI>
     * <LI>NOTE - Примечание к шаблону детали назначения платежа</LI>
     * <LI>NUM - Порядковый номер – номер позиции в строке назначения платежа</LI>
     * <LI>SYSNAME - Системное наименование детали назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATEPDETID - ИД шаблона детали назначения платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATEPDETID"})
    public Map<String,Object> dsB2BBankStateTemplatePurposeDetailModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankStateTemplatePurposeDetailUpdate", params);
        result.put("BANKSTATETEMPLATEPDETID", params.get("BANKSTATETEMPLATEPDETID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEPDETID - ИД шаблона детали назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATETEMPLATEPDETID"})
    public void dsB2BBankStateTemplatePurposeDetailDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BBankStateTemplatePurposeDetailDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>CONSTANTVALUESTR - Константа – значение по умолчанию (строковое) блока назначения платежа, если заполнено не может быть изменено вручную</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>BANKSTATETEMPLATEPDETID - ИД шаблона детали назначения платежа</LI>
     * <LI>NAME - Наименование детали назначения платежа</LI>
     * <LI>NOTE - Примечание к шаблону детали назначения платежа</LI>
     * <LI>NUM - Порядковый номер – номер позиции в строке назначения платежа</LI>
     * <LI>SYSNAME - Системное наименование детали назначения платежа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки</LI>
     * <LI>CONSTANTVALUESTR - Константа – значение по умолчанию (строковое) блока назначения платежа, если заполнено не может быть изменено вручную</LI>
     * <LI>DATATYPEID - ИД типа данных</LI>
     * <LI>DATATYPESTR - Строка типа данных</LI>
     * <LI>BANKSTATETEMPLATEPDETID - ИД шаблона детали назначения платежа</LI>
     * <LI>NAME - Наименование детали назначения платежа</LI>
     * <LI>NOTE - Примечание к шаблону детали назначения платежа</LI>
     * <LI>NUM - Порядковый номер – номер позиции в строке назначения платежа</LI>
     * <LI>SYSNAME - Системное наименование детали назначения платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankStateTemplatePurposeDetailBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BBankStateTemplatePurposeDetailBrowseListByParam", "dsB2BBankStateTemplatePurposeDetailBrowseListByParamCount", params);
        return result;
    }





}
