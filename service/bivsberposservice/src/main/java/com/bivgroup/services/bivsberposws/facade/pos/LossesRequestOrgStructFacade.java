/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesRequestOrgStruct
 *
 * @author reson
 */
@IdGen(entityName="LOSS_REQUESTORGSTRUCT",idFieldName="REQUESTORGSTRUCTID")
@BOName("LossesRequestOrgStruct")
public class LossesRequestOrgStructFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTORGSTRUCTID - ИД связи заявки с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTORGSTRUCTID - ИД связи заявки с орг. структурой</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesRequestOrgStructCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesRequestOrgStructInsert", params);
        result.put("REQUESTORGSTRUCTID", params.get("REQUESTORGSTRUCTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTORGSTRUCTID - ИД связи заявки с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTORGSTRUCTID - ИД связи заявки с орг. структурой</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTORGSTRUCTID"})
    public Map<String,Object> dsLossesRequestOrgStructInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesRequestOrgStructInsert", params);
        result.put("REQUESTORGSTRUCTID", params.get("REQUESTORGSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTORGSTRUCTID - ИД связи заявки с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTORGSTRUCTID - ИД связи заявки с орг. структурой</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTORGSTRUCTID"})
    public Map<String,Object> dsLossesRequestOrgStructUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesRequestOrgStructUpdate", params);
        result.put("REQUESTORGSTRUCTID", params.get("REQUESTORGSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTORGSTRUCTID - ИД связи заявки с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTORGSTRUCTID - ИД связи заявки с орг. структурой</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTORGSTRUCTID"})
    public Map<String,Object> dsLossesRequestOrgStructModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesRequestOrgStructUpdate", params);
        result.put("REQUESTORGSTRUCTID", params.get("REQUESTORGSTRUCTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTORGSTRUCTID - ИД связи заявки с орг. структурой</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTORGSTRUCTID"})
    public void dsLossesRequestOrgStructDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesRequestOrgStructDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTORGSTRUCTID - ИД связи заявки с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTORGSTRUCTID - ИД связи заявки с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesRequestOrgStructBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesRequestOrgStructBrowseListByParam", "dsLossesRequestOrgStructBrowseListByParamCount", params);
        return result;
    }





}
