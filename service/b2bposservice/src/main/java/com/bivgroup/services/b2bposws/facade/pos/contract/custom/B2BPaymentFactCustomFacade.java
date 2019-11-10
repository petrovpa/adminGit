/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.contract.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author sambucus
 */
@BOName("B2BPaymentFactCustom")
public class B2BPaymentFactCustomFacade  extends B2BBaseFacade { 
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
    public Map<String,Object> dsB2BPaymentFactBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BPaymentFactBrowseListByParamEx", "dsB2BPaymentFactBrowseListByParamExCount", params);
        return result;
    }
}
