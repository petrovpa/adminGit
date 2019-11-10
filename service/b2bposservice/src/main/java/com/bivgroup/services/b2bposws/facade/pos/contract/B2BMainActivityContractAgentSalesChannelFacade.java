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
 * Фасад для сущности B2BMainActivityContractAgentSalesChannel
 *
 * @author reson
 */
@IdGen(entityName="B2B_MACAGENTSCHAN",idFieldName="MACAGENTSCHANID")
@BOName("B2BMainActivityContractAgentSalesChannel")
public class B2BMainActivityContractAgentSalesChannelFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>MACAGENTSCHANID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTSCHANID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractAgentSalesChannelCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractAgentSalesChannelInsert", params);
        result.put("MACAGENTSCHANID", params.get("MACAGENTSCHANID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>MACAGENTSCHANID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTSCHANID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTSCHANID"})
    public Map<String,Object> dsB2BMainActivityContractAgentSalesChannelInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractAgentSalesChannelInsert", params);
        result.put("MACAGENTSCHANID", params.get("MACAGENTSCHANID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>MACAGENTSCHANID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTSCHANID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTSCHANID"})
    public Map<String,Object> dsB2BMainActivityContractAgentSalesChannelUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractAgentSalesChannelUpdate", params);
        result.put("MACAGENTSCHANID", params.get("MACAGENTSCHANID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>MACAGENTSCHANID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTSCHANID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTSCHANID"})
    public Map<String,Object> dsB2BMainActivityContractAgentSalesChannelModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractAgentSalesChannelUpdate", params);
        result.put("MACAGENTSCHANID", params.get("MACAGENTSCHANID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MACAGENTSCHANID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTSCHANID"})
    public void dsB2BMainActivityContractAgentSalesChannelDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMainActivityContractAgentSalesChannelDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>MACAGENTSCHANID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTSCHANID - ИД записи</LI>
     * <LI>MACAGENTCNTID - ИД содержимого агентского договора</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractAgentSalesChannelBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMainActivityContractAgentSalesChannelBrowseListByParam", "dsB2BMainActivityContractAgentSalesChannelBrowseListByParamCount", params);
        return result;
    }





}
