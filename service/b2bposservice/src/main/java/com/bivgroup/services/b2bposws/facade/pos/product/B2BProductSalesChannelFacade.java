/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BProductSalesChannel
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODSALESCHAN",idFieldName="PRODSALESCHANID")
@BOName("B2BProductSalesChannel")
public class B2BProductSalesChannelFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODSALESCHANID - ИД канала продаж продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSALESCHANID - ИД канала продаж продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODVERID", "SALECHANNELID"})
    public Map<String,Object> dsB2BProductSalesChannelCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductSalesChannelInsert", params);
        result.put("PRODSALESCHANID", params.get("PRODSALESCHANID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODSALESCHANID - ИД канала продаж продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSALESCHANID - ИД канала продаж продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSALESCHANID", "PRODVERID", "SALECHANNELID"})
    public Map<String,Object> dsB2BProductSalesChannelInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductSalesChannelInsert", params);
        result.put("PRODSALESCHANID", params.get("PRODSALESCHANID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODSALESCHANID - ИД канала продаж продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSALESCHANID - ИД канала продаж продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSALESCHANID"})
    public Map<String,Object> dsB2BProductSalesChannelUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductSalesChannelUpdate", params);
        result.put("PRODSALESCHANID", params.get("PRODSALESCHANID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODSALESCHANID - ИД канала продаж продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSALESCHANID - ИД канала продаж продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSALESCHANID"})
    public Map<String,Object> dsB2BProductSalesChannelModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductSalesChannelUpdate", params);
        result.put("PRODSALESCHANID", params.get("PRODSALESCHANID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODSALESCHANID - ИД канала продаж продукта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSALESCHANID"})
    public void dsB2BProductSalesChannelDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductSalesChannelDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODSALESCHANID - ИД канала продаж продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSALESCHANID - ИД канала продаж продукта</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SALECHANNELID - ИД канала продаж</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductSalesChannelBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductSalesChannelBrowseListByParam", "dsB2BProductSalesChannelBrowseListByParamCount", params);
        return result;
    }





}
