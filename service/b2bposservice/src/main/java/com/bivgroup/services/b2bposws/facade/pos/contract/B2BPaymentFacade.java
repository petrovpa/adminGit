/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BPayment
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_PAY",idFieldName="PAYID")
@BOName("B2BPayment")
public class B2BPaymentFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - Ссылка на допс</LI>
     * <LI>AMOUNT - Плановая сумма платежа</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PAYID - ИД платежа</LI>
     * <LI>ORDERNUM - Порядковый номер платежа</LI>
     * <LI>PAYDATE - Плановая дата платежа</LI>
     * <LI>STARTDATE - Дата оплаты страховой премии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYID - ИД платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String,Object> dsB2BPaymentCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BPaymentInsert", params);
        result.put("PAYID", params.get("PAYID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - Ссылка на допс</LI>
     * <LI>AMOUNT - Плановая сумма платежа</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PAYID - ИД платежа</LI>
     * <LI>ORDERNUM - Порядковый номер платежа</LI>
     * <LI>PAYDATE - Плановая дата платежа</LI>
     * <LI>STARTDATE - Дата оплаты страховой премии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYID - ИД платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID", "PAYID"})
    public Map<String,Object> dsB2BPaymentInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BPaymentInsert", params);
        result.put("PAYID", params.get("PAYID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - Ссылка на допс</LI>
     * <LI>AMOUNT - Плановая сумма платежа</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PAYID - ИД платежа</LI>
     * <LI>ORDERNUM - Порядковый номер платежа</LI>
     * <LI>PAYDATE - Плановая дата платежа</LI>
     * <LI>STARTDATE - Дата оплаты страховой премии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYID - ИД платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYID"})
    public Map<String,Object> dsB2BPaymentUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BPaymentUpdate", params);
        result.put("PAYID", params.get("PAYID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - Ссылка на допс</LI>
     * <LI>AMOUNT - Плановая сумма платежа</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PAYID - ИД платежа</LI>
     * <LI>ORDERNUM - Порядковый номер платежа</LI>
     * <LI>PAYDATE - Плановая дата платежа</LI>
     * <LI>STARTDATE - Дата оплаты страховой премии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYID - ИД платежа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYID"})
    public Map<String,Object> dsB2BPaymentModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BPaymentUpdate", params);
        result.put("PAYID", params.get("PAYID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PAYID - ИД платежа</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public void dsB2BPaymentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BPaymentDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - Ссылка на допс</LI>
     * <LI>AMOUNT - Плановая сумма платежа</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PAYID - ИД платежа</LI>
     * <LI>ORDERNUM - Порядковый номер платежа</LI>
     * <LI>PAYDATE - Плановая дата платежа</LI>
     * <LI>STARTDATE - Дата оплаты страховой премии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRID - Ссылка на допс</LI>
     * <LI>AMOUNT - Плановая сумма платежа</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>PAYID - ИД платежа</LI>
     * <LI>ORDERNUM - Порядковый номер платежа</LI>
     * <LI>PAYDATE - Плановая дата платежа</LI>
     * <LI>STARTDATE - Дата оплаты страховой премии</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BPaymentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BPaymentBrowseListByParam", "dsB2BPaymentBrowseListByParamCount", params);
        return result;
    }





}
