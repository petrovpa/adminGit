/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product.custom;


import com.bivgroup.services.b2bposws.facade.pos.product.*;
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
@BOName("B2BProductSalesChannelCustom")
public class B2BProductSalesChannelCustomFacade extends BaseFacade {
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
    public Map<String,Object> dsB2BProductSalesChannelBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductSalesChannelBrowseListByParamEx", "dsB2BProductSalesChannelBrowseListByParamExCount", params);
        return result;
    }





}
