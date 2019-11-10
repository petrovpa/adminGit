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
@BOName("B2BContractRiskCustom")
public class B2BContractRiskCustomFacade  extends B2BBaseFacade { 
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
    public Map<String,Object> dsB2BContractRiskBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractRiskBrowseListByParamEx", "dsB2BContractRiskBrowseListByParamExCount", params);
        return result;
    }
}
