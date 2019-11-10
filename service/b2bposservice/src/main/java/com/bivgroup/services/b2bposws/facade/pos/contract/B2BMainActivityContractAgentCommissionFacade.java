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
 * Фасад для сущности B2BMainActivityContractAgentCommission
 *
 * @author reson
 */
@IdGen(entityName="B2B_MACAGENTCOMMIS",idFieldName="MACAGENTCOMMISID")
@BOName("B2BMainActivityContractAgentCommission")
public class B2BMainActivityContractAgentCommissionFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCOMMISID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODSTRUCTID - ИД структуры риска</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTCOMMISID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractAgentCommissionCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractAgentCommissionInsert", params);
        result.put("MACAGENTCOMMISID", params.get("MACAGENTCOMMISID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCOMMISID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODSTRUCTID - ИД структуры риска</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTCOMMISID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTCOMMISID"})
    public Map<String,Object> dsB2BMainActivityContractAgentCommissionInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractAgentCommissionInsert", params);
        result.put("MACAGENTCOMMISID", params.get("MACAGENTCOMMISID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCOMMISID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODSTRUCTID - ИД структуры риска</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTCOMMISID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTCOMMISID"})
    public Map<String,Object> dsB2BMainActivityContractAgentCommissionUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractAgentCommissionUpdate", params);
        result.put("MACAGENTCOMMISID", params.get("MACAGENTCOMMISID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCOMMISID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODSTRUCTID - ИД структуры риска</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTCOMMISID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTCOMMISID"})
    public Map<String,Object> dsB2BMainActivityContractAgentCommissionModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractAgentCommissionUpdate", params);
        result.put("MACAGENTCOMMISID", params.get("MACAGENTCOMMISID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MACAGENTCOMMISID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTCOMMISID"})
    public void dsB2BMainActivityContractAgentCommissionDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMainActivityContractAgentCommissionDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCOMMISID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODSTRUCTID - ИД структуры риска</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCOMMISID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODSTRUCTID - ИД структуры риска</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractAgentCommissionBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMainActivityContractAgentCommissionBrowseListByParam", "dsB2BMainActivityContractAgentCommissionBrowseListByParamCount", params);
        return result;
    }





}
