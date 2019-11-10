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
 * Фасад для сущности B2BContractOrgStruct
 *
 * @author reson
 */
@IdGen(entityName="B2B_CONTRORGSTRUCT",idFieldName="CONTRORGSTRUCTID")
@BOName("B2BContractOrgStruct")
public class B2BContractOrgStructFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRORGSTRUCTID - ИД связи договора с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRORGSTRUCTID - ИД связи договора с орг. структурой</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractOrgStructCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractOrgStructInsert", params);
        result.put("CONTRORGSTRUCTID", params.get("CONTRORGSTRUCTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRORGSTRUCTID - ИД связи договора с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRORGSTRUCTID - ИД связи договора с орг. структурой</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRORGSTRUCTID"})
    public Map<String,Object> dsB2BContractOrgStructInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractOrgStructInsert", params);
        result.put("CONTRORGSTRUCTID", params.get("CONTRORGSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRORGSTRUCTID - ИД связи договора с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRORGSTRUCTID - ИД связи договора с орг. структурой</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRORGSTRUCTID"})
    public Map<String,Object> dsB2BContractOrgStructUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractOrgStructUpdate", params);
        result.put("CONTRORGSTRUCTID", params.get("CONTRORGSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRORGSTRUCTID - ИД связи договора с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRORGSTRUCTID - ИД связи договора с орг. структурой</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRORGSTRUCTID"})
    public Map<String,Object> dsB2BContractOrgStructModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractOrgStructUpdate", params);
        result.put("CONTRORGSTRUCTID", params.get("CONTRORGSTRUCTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRORGSTRUCTID - ИД связи договора с орг. структурой</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRORGSTRUCTID"})
    public void dsB2BContractOrgStructDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractOrgStructDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRORGSTRUCTID - ИД связи договора с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTRORGSTRUCTID - ИД связи договора с орг. структурой</LI>
     * <LI>ORGSTRUCTID - ИД орг. структуры</LI>
     * <LI>ROLEID - Ссылка на роль</LI>
     * <LI>USERID - Ссылка на пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractOrgStructBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractOrgStructBrowseListByParam", "dsB2BContractOrgStructBrowseListByParamCount", params);
        return result;
    }





}
