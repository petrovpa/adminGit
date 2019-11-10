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
 * Фасад для сущности B2BContractAgent
 *
 * @author reson
 */
@IdGen(entityName="B2B_CONTRAGENT",idFieldName="CONTRAGENTID")
@BOName("B2BContractAgent")
public class B2BContractAgentFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>AGENTCONTRID - Агентский договор</LI>
     * <LI>COMISSION - Процент комиссии</LI>
     * <LI>CONTRAGENTID - ИД</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRAGENTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractAgentCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractAgentInsert", params);
        result.put("CONTRAGENTID", params.get("CONTRAGENTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>AGENTCONTRID - Агентский договор</LI>
     * <LI>COMISSION - Процент комиссии</LI>
     * <LI>CONTRAGENTID - ИД</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRAGENTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRAGENTID"})
    public Map<String,Object> dsB2BContractAgentInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractAgentInsert", params);
        result.put("CONTRAGENTID", params.get("CONTRAGENTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AGENTCONTRID - Агентский договор</LI>
     * <LI>COMISSION - Процент комиссии</LI>
     * <LI>CONTRAGENTID - ИД</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRAGENTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRAGENTID"})
    public Map<String,Object> dsB2BContractAgentUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractAgentUpdate", params);
        result.put("CONTRAGENTID", params.get("CONTRAGENTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AGENTCONTRID - Агентский договор</LI>
     * <LI>COMISSION - Процент комиссии</LI>
     * <LI>CONTRAGENTID - ИД</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRAGENTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRAGENTID"})
    public Map<String,Object> dsB2BContractAgentModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractAgentUpdate", params);
        result.put("CONTRAGENTID", params.get("CONTRAGENTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRAGENTID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRAGENTID"})
    public void dsB2BContractAgentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractAgentDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>AGENTCONTRID - Агентский договор</LI>
     * <LI>COMISSION - Процент комиссии</LI>
     * <LI>CONTRAGENTID - ИД</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AGENTCONTRID - Агентский договор</LI>
     * <LI>COMISSION - Процент комиссии</LI>
     * <LI>CONTRAGENTID - ИД</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractAgentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractAgentBrowseListByParam", "dsB2BContractAgentBrowseListByParamCount", params);
        return result;
    }





}
