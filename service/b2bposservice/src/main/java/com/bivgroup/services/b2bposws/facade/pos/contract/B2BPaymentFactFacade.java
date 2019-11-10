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
 * Фасад для сущности B2BPaymentFact
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_PAYFACT",idFieldName="PAYFACTID")
@BOName("B2BPaymentFact")
public class B2BPaymentFactFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>AMCURRENCYID - ИД валюты</LI>
     * <LI>AMVALUE - Сумма</LI>
     * <LI>AMVALUERUB - Сумма в рублях</LI>
     * <LI>CONTRNODEID - ИД договора (все версии)</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>PAYFACTID - ИД платежного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAYFACTDATE - Дата</LI>
     * <LI>PAYFACTNUMBER - Номер</LI>
     * <LI>PAYFACTTYPE - Тип</LI>
     * <LI>SERIES - Серия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYFACTID - ИД платежного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRNODEID"})
    public Map<String,Object> dsB2BPaymentFactCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BPaymentFactInsert", params);
        result.put("PAYFACTID", params.get("PAYFACTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>AMCURRENCYID - ИД валюты</LI>
     * <LI>AMVALUE - Сумма</LI>
     * <LI>AMVALUERUB - Сумма в рублях</LI>
     * <LI>CONTRNODEID - ИД договора (все версии)</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>PAYFACTID - ИД платежного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAYFACTDATE - Дата</LI>
     * <LI>PAYFACTNUMBER - Номер</LI>
     * <LI>PAYFACTTYPE - Тип</LI>
     * <LI>SERIES - Серия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYFACTID - ИД платежного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRNODEID", "PAYFACTID"})
    public Map<String,Object> dsB2BPaymentFactInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BPaymentFactInsert", params);
        result.put("PAYFACTID", params.get("PAYFACTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AMCURRENCYID - ИД валюты</LI>
     * <LI>AMVALUE - Сумма</LI>
     * <LI>AMVALUERUB - Сумма в рублях</LI>
     * <LI>CONTRNODEID - ИД договора (все версии)</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>PAYFACTID - ИД платежного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAYFACTDATE - Дата</LI>
     * <LI>PAYFACTNUMBER - Номер</LI>
     * <LI>PAYFACTTYPE - Тип</LI>
     * <LI>SERIES - Серия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYFACTID - ИД платежного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYFACTID"})
    public Map<String,Object> dsB2BPaymentFactUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BPaymentFactUpdate", params);
        result.put("PAYFACTID", params.get("PAYFACTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AMCURRENCYID - ИД валюты</LI>
     * <LI>AMVALUE - Сумма</LI>
     * <LI>AMVALUERUB - Сумма в рублях</LI>
     * <LI>CONTRNODEID - ИД договора (все версии)</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>PAYFACTID - ИД платежного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAYFACTDATE - Дата</LI>
     * <LI>PAYFACTNUMBER - Номер</LI>
     * <LI>PAYFACTTYPE - Тип</LI>
     * <LI>SERIES - Серия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAYFACTID - ИД платежного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYFACTID"})
    public Map<String,Object> dsB2BPaymentFactModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BPaymentFactUpdate", params);
        result.put("PAYFACTID", params.get("PAYFACTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PAYFACTID - ИД платежного документа</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAYFACTID"})
    public void dsB2BPaymentFactDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BPaymentFactDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>AMCURRENCYID - ИД валюты</LI>
     * <LI>AMVALUE - Сумма</LI>
     * <LI>AMVALUERUB - Сумма в рублях</LI>
     * <LI>CONTRNODEID - ИД договора (все версии)</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>PAYFACTID - ИД платежного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAYFACTDATE - Дата</LI>
     * <LI>PAYFACTNUMBER - Номер</LI>
     * <LI>PAYFACTTYPE - Тип</LI>
     * <LI>SERIES - Серия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AMCURRENCYID - ИД валюты</LI>
     * <LI>AMVALUE - Сумма</LI>
     * <LI>AMVALUERUB - Сумма в рублях</LI>
     * <LI>CONTRNODEID - ИД договора (все версии)</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>PAYFACTID - ИД платежного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PAYFACTDATE - Дата</LI>
     * <LI>PAYFACTNUMBER - Номер</LI>
     * <LI>PAYFACTTYPE - Тип</LI>
     * <LI>SERIES - Серия</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД ипользователя, изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BPaymentFactBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BPaymentFactBrowseListByParam", "dsB2BPaymentFactBrowseListByParamCount", params);
        return result;
    }





}
