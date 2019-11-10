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
 * Фасад для сущности B2BProductPaymentVariant
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODPAYVAR",idFieldName="PRODPAYVARID")
@BOName("B2BProductPaymentVariant")
public class B2BProductPaymentVariantFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
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
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYVARID", "PRODVERID"})
    public Map<String,Object> dsB2BProductPaymentVariantCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductPaymentVariantInsert", params);
        result.put("PRODPAYVARID", params.get("PRODPAYVARID"));
        return result;
    }





    /**
     * Создать объект без генерации id
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
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPAYVARID", "PAYVARID", "PRODVERID"})
    public Map<String,Object> dsB2BProductPaymentVariantInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductPaymentVariantInsert", params);
        result.put("PRODPAYVARID", params.get("PRODPAYVARID"));
        return result;
    }





    /**
     * Изменить объект
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
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPAYVARID"})
    public Map<String,Object> dsB2BProductPaymentVariantUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductPaymentVariantUpdate", params);
        result.put("PRODPAYVARID", params.get("PRODPAYVARID"));
        return result;
    }





    /**
     * Изменить объект
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
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPAYVARID"})
    public Map<String,Object> dsB2BProductPaymentVariantModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductPaymentVariantUpdate", params);
        result.put("PRODPAYVARID", params.get("PRODPAYVARID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODPAYVARID - ИД варианта оплаты продукта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPAYVARID"})
    public void dsB2BProductPaymentVariantDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductPaymentVariantDelete", params);
    }





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
    public Map<String,Object> dsB2BProductPaymentVariantBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductPaymentVariantBrowseListByParam", "dsB2BProductPaymentVariantBrowseListByParamCount", params);
        return result;
    }

    @WsMethod(requiredParams = {"PRODVERID"})
    public Map<String,Object> dsB2BProductPayVarListByProdVerId(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductPayVarListByProdVerId", "dsB2BProductPayVarListByProdVerIdCount", params);
        return result;
    }

}
