/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("B2BProductPaymentVariantCustom")
public class B2BProductPaymentVariantCustomFacade extends B2BBaseFacade {
    
    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODPAYVARID - ИД варианта оплаты продукта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PAYVARID - ИД варианта оплаты</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPAYVARID - ИД варианта оплаты продукта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PAYVARID - ИД варианта оплаты</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductPaymentVariantBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductPaymentVariantBrowseListByParamEx", "dsB2BProductPaymentVariantBrowseListByParamExCount", params);
        return result;
    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODPAYVARID - ИД варианта оплаты продукта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PAYVARID - ИД варианта оплаты</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPAYVARID - ИД варианта оплаты продукта</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PAYVARID - ИД варианта оплаты</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>PAYVARNAME - периодичность оплаты</LI>
     * <LI>PAYVARSYSNAME - системное имя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductPayVarBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductPayVarBrowseListByParamEx", "dsB2BProductPayVarBrowseListByParamExCount", params);
        return result;
    }
}
