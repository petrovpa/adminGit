/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BContractObject
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@BinaryFile(objTableName = "B2B_CONTROBJ", objTablePKFieldName = "CONTROBJID")
@IdGen(entityName="B2B_CONTROBJ",idFieldName="CONTROBJID")
@BOName("B2BContractObject")
public class B2BContractObjectFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта</LI>
     * <LI>DURATION - Срок страхования объекта</LI>
     * <LI>FINISHDATE - Конечная дата страхования объекта</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>MAINCONTROBJCALCID - ИД основного расчета</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>STARTDATE - Дата начала страхования объекта</LI>
     * <LI>STATEID - Состояние объекта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String,Object> dsB2BContractObjectCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractObjectInsert", params);
        result.put("CONTROBJID", params.get("CONTROBJID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта</LI>
     * <LI>DURATION - Срок страхования объекта</LI>
     * <LI>FINISHDATE - Конечная дата страхования объекта</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>MAINCONTROBJCALCID - ИД основного расчета</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>STARTDATE - Дата начала страхования объекта</LI>
     * <LI>STATEID - Состояние объекта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID", "CONTROBJID"})
    public Map<String,Object> dsB2BContractObjectInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractObjectInsert", params);
        result.put("CONTROBJID", params.get("CONTROBJID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта</LI>
     * <LI>DURATION - Срок страхования объекта</LI>
     * <LI>FINISHDATE - Конечная дата страхования объекта</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>MAINCONTROBJCALCID - ИД основного расчета</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>STARTDATE - Дата начала страхования объекта</LI>
     * <LI>STATEID - Состояние объекта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTROBJID"})
    public Map<String,Object> dsB2BContractObjectUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractObjectUpdate", params);
        result.put("CONTROBJID", params.get("CONTROBJID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта</LI>
     * <LI>DURATION - Срок страхования объекта</LI>
     * <LI>FINISHDATE - Конечная дата страхования объекта</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>MAINCONTROBJCALCID - ИД основного расчета</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>STARTDATE - Дата начала страхования объекта</LI>
     * <LI>STATEID - Состояние объекта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTROBJID"})
    public Map<String,Object> dsB2BContractObjectModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractObjectUpdate", params);
        result.put("CONTROBJID", params.get("CONTROBJID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTROBJID"})
    public void dsB2BContractObjectDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractObjectDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта</LI>
     * <LI>DURATION - Срок страхования объекта</LI>
     * <LI>FINISHDATE - Конечная дата страхования объекта</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>MAINCONTROBJCALCID - ИД основного расчета</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>STARTDATE - Дата начала страхования объекта</LI>
     * <LI>STATEID - Состояние объекта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CANCELDATE - Дата расторжения</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта</LI>
     * <LI>DURATION - Срок страхования объекта</LI>
     * <LI>FINISHDATE - Конечная дата страхования объекта</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>MAINCONTROBJCALCID - ИД основного расчета</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>STARTDATE - Дата начала страхования объекта</LI>
     * <LI>STATEID - Состояние объекта</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractObjectBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractObjectBrowseListByParam", "dsB2BContractObjectBrowseListByParamCount", params);
        return result;
    }





}
