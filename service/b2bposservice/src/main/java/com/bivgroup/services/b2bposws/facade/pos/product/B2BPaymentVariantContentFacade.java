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
 * Фасад для сущности B2BPaymentVariantContent
 *
 * @author reson
 */
@IdGen(entityName="B2B_PAYVARCNT",idFieldName="PAYVARCNTID")
@BOName("B2BPaymentVariantContent")
public class B2BPaymentVariantContentFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DATESHIFT - Сдвиг – сдвиг даты для элемента (в месяцах)</LI>
     * <LI>NUM - Порядковый норме – номер элемента</LI>
     * <LI>PAYVARCNTID - Идентификатор элемента состава периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>PREMSHARE - Доля – доля премии элемента</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYVARCNTID - Идентификатор элемента состава периодичности оплаты</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BPaymentVariantContentCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BPaymentVariantContentInsert", params);
        result.put("PAYVARCNTID", params.get("PAYVARCNTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DATESHIFT - Сдвиг – сдвиг даты для элемента (в месяцах)</LI>
     * <LI>NUM - Порядковый норме – номер элемента</LI>
     * <LI>PAYVARCNTID - Идентификатор элемента состава периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>PREMSHARE - Доля – доля премии элемента</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYVARCNTID - Идентификатор элемента состава периодичности оплаты</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYVARCNTID"})
    public Map<String,Object> dsB2BPaymentVariantContentInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BPaymentVariantContentInsert", params);
        result.put("PAYVARCNTID", params.get("PAYVARCNTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DATESHIFT - Сдвиг – сдвиг даты для элемента (в месяцах)</LI>
     * <LI>NUM - Порядковый норме – номер элемента</LI>
     * <LI>PAYVARCNTID - Идентификатор элемента состава периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>PREMSHARE - Доля – доля премии элемента</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYVARCNTID - Идентификатор элемента состава периодичности оплаты</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYVARCNTID"})
    public Map<String,Object> dsB2BPaymentVariantContentUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BPaymentVariantContentUpdate", params);
        result.put("PAYVARCNTID", params.get("PAYVARCNTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DATESHIFT - Сдвиг – сдвиг даты для элемента (в месяцах)</LI>
     * <LI>NUM - Порядковый норме – номер элемента</LI>
     * <LI>PAYVARCNTID - Идентификатор элемента состава периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>PREMSHARE - Доля – доля премии элемента</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYVARCNTID - Идентификатор элемента состава периодичности оплаты</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYVARCNTID"})
    public Map<String,Object> dsB2BPaymentVariantContentModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BPaymentVariantContentUpdate", params);
        result.put("PAYVARCNTID", params.get("PAYVARCNTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PAYVARCNTID - Идентификатор элемента состава периодичности оплаты</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYVARCNTID"})
    public void dsB2BPaymentVariantContentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BPaymentVariantContentDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DATESHIFT - Сдвиг – сдвиг даты для элемента (в месяцах)</LI>
     * <LI>NUM - Порядковый норме – номер элемента</LI>
     * <LI>PAYVARCNTID - Идентификатор элемента состава периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>PREMSHARE - Доля – доля премии элемента</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DATESHIFT - Сдвиг – сдвиг даты для элемента (в месяцах)</LI>
     * <LI>NUM - Порядковый норме – номер элемента</LI>
     * <LI>PAYVARCNTID - Идентификатор элемента состава периодичности оплаты</LI>
     * <LI>PAYVARID - Идентификатор периодичности оплаты</LI>
     * <LI>PREMSHARE - Доля – доля премии элемента</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BPaymentVariantContentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BPaymentVariantContentBrowseListByParam", "dsB2BPaymentVariantContentBrowseListByParamCount", params);
        return result;
    }





}
