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
 * Фасад для сущности B2BPaymentVariant
 *
 * @author reson
 */
@IdGen(entityName="B2B_PAYVAR",idFieldName="PAYVARID")
@BOName("B2BPaymentVariant")
public class B2BPaymentVariantFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>NAME - Наименование – название периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>SYSNAME - Псевдоним – уникальный номер периодичности оплаты</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BPaymentVariantCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BPaymentVariantInsert", params);
        result.put("PAYVARID", params.get("PAYVARID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>NAME - Наименование – название периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>SYSNAME - Псевдоним – уникальный номер периодичности оплаты</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYVARID"})
    public Map<String,Object> dsB2BPaymentVariantInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BPaymentVariantInsert", params);
        result.put("PAYVARID", params.get("PAYVARID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>NAME - Наименование – название периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>SYSNAME - Псевдоним – уникальный номер периодичности оплаты</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYVARID"})
    public Map<String,Object> dsB2BPaymentVariantUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BPaymentVariantUpdate", params);
        result.put("PAYVARID", params.get("PAYVARID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>NAME - Наименование – название периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>SYSNAME - Псевдоним – уникальный номер периодичности оплаты</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYVARID"})
    public Map<String,Object> dsB2BPaymentVariantModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BPaymentVariantUpdate", params);
        result.put("PAYVARID", params.get("PAYVARID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYVARID"})
    public void dsB2BPaymentVariantDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BPaymentVariantDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>NAME - Наименование – название периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>SYSNAME - Псевдоним – уникальный номер периодичности оплаты</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>NAME - Наименование – название периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>SYSNAME - Псевдоним – уникальный номер периодичности оплаты</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BPaymentVariantBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BPaymentVariantBrowseListByParam", "dsB2BPaymentVariantBrowseListByParamCount", params);
        return result;
    }





}
