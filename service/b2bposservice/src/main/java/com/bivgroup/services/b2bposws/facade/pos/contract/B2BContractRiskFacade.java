/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.guididgen.GUIDIdGen;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BContractRisk
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@GUIDIdGen(idFieldName="EXTERNALID")
@IdGen(entityName="B2B_CONTRRISK",idFieldName="CONTRRISKID")
@BOName("B2BContractRisk")
public class B2BContractRiskFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата отклонения риска</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта риска</LI>
     * <LI>DURATION - Срок страхования от риска</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FINISHDATE - Конечная дата страхования от риска</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>ISMANUAL - Тариф установлен андеррайтером</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODRISKID - ИД риска продукта</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Начальная дата страхования от риска</LI>
     * <LI>TARIFFVALUE - Тариф</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractRiskCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractRiskInsert", params);
        result.put("CONTRRISKID", params.get("CONTRRISKID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата отклонения риска</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта риска</LI>
     * <LI>DURATION - Срок страхования от риска</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FINISHDATE - Конечная дата страхования от риска</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>ISMANUAL - Тариф установлен андеррайтером</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODRISKID - ИД риска продукта</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Начальная дата страхования от риска</LI>
     * <LI>TARIFFVALUE - Тариф</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRRISKID"})
    public Map<String,Object> dsB2BContractRiskInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractRiskInsert", params);
        result.put("CONTRRISKID", params.get("CONTRRISKID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата отклонения риска</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта риска</LI>
     * <LI>DURATION - Срок страхования от риска</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FINISHDATE - Конечная дата страхования от риска</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>ISMANUAL - Тариф установлен андеррайтером</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODRISKID - ИД риска продукта</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Начальная дата страхования от риска</LI>
     * <LI>TARIFFVALUE - Тариф</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRRISKID"})
    public Map<String,Object> dsB2BContractRiskUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractRiskUpdate", params);
        result.put("CONTRRISKID", params.get("CONTRRISKID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата отклонения риска</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта риска</LI>
     * <LI>DURATION - Срок страхования от риска</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FINISHDATE - Конечная дата страхования от риска</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>ISMANUAL - Тариф установлен андеррайтером</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODRISKID - ИД риска продукта</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Начальная дата страхования от риска</LI>
     * <LI>TARIFFVALUE - Тариф</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRRISKID"})
    public Map<String,Object> dsB2BContractRiskModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractRiskUpdate", params);
        result.put("CONTRRISKID", params.get("CONTRRISKID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRRISKID"})
    public void dsB2BContractRiskDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractRiskDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата отклонения риска</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта риска</LI>
     * <LI>DURATION - Срок страхования от риска</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FINISHDATE - Конечная дата страхования от риска</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>ISMANUAL - Тариф установлен андеррайтером</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODRISKID - ИД риска продукта</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Начальная дата страхования от риска</LI>
     * <LI>TARIFFVALUE - Тариф</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CANCELDATE - Дата отклонения риска</LI>
     * <LI>CONTROBJID - ИД объекта договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - Валюта риска</LI>
     * <LI>DURATION - Срок страхования от риска</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FINISHDATE - Конечная дата страхования от риска</LI>
     * <LI>CONTRRISKID - ИД риска договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>ISMANUAL - Тариф установлен андеррайтером</LI>
     * <LI>PAYPREMVALUE - Размер страховой премии в валюте расчетов</LI>
     * <LI>PREMCURRENCYID - Валюта страховой премии</LI>
     * <LI>PREMVALUE - Размер страховой премии</LI>
     * <LI>PRODRISKID - ИД риска продукта</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>STARTDATE - Начальная дата страхования от риска</LI>
     * <LI>TARIFFVALUE - Тариф</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractRiskBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractRiskBrowseListByParam", "dsB2BContractRiskBrowseListByParamCount", params);
        return result;
    }





}
