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
 * Фасад для сущности B2BProductCalcRateRule
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODCALCRATERULE",idFieldName="PRODCALCRATERULEID")
@BOName("B2BProductCalcRateRule")
public class B2BProductCalcRateRuleFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CALCVARIANTID - Тип расчета</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODCALCRATERULEID - ИД</LI>
     * <LI>PERCENT - Процент</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>RATEDATE - Дата курса</LI>
     * <LI>RATEVALUE - Значение</LI>
     * <LI>RULEDATE - Дата правила</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODCALCRATERULEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductCalcRateRuleCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductCalcRateRuleInsert", params);
        result.put("PRODCALCRATERULEID", params.get("PRODCALCRATERULEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CALCVARIANTID - Тип расчета</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODCALCRATERULEID - ИД</LI>
     * <LI>PERCENT - Процент</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>RATEDATE - Дата курса</LI>
     * <LI>RATEVALUE - Значение</LI>
     * <LI>RULEDATE - Дата правила</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODCALCRATERULEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODCALCRATERULEID"})
    public Map<String,Object> dsB2BProductCalcRateRuleInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductCalcRateRuleInsert", params);
        result.put("PRODCALCRATERULEID", params.get("PRODCALCRATERULEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CALCVARIANTID - Тип расчета</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODCALCRATERULEID - ИД</LI>
     * <LI>PERCENT - Процент</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>RATEDATE - Дата курса</LI>
     * <LI>RATEVALUE - Значение</LI>
     * <LI>RULEDATE - Дата правила</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODCALCRATERULEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODCALCRATERULEID"})
    public Map<String,Object> dsB2BProductCalcRateRuleUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductCalcRateRuleUpdate", params);
        result.put("PRODCALCRATERULEID", params.get("PRODCALCRATERULEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CALCVARIANTID - Тип расчета</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODCALCRATERULEID - ИД</LI>
     * <LI>PERCENT - Процент</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>RATEDATE - Дата курса</LI>
     * <LI>RATEVALUE - Значение</LI>
     * <LI>RULEDATE - Дата правила</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODCALCRATERULEID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODCALCRATERULEID"})
    public Map<String,Object> dsB2BProductCalcRateRuleModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductCalcRateRuleUpdate", params);
        result.put("PRODCALCRATERULEID", params.get("PRODCALCRATERULEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODCALCRATERULEID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODCALCRATERULEID"})
    public void dsB2BProductCalcRateRuleDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductCalcRateRuleDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CALCVARIANTID - Тип расчета</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODCALCRATERULEID - ИД</LI>
     * <LI>PERCENT - Процент</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>RATEDATE - Дата курса</LI>
     * <LI>RATEVALUE - Значение</LI>
     * <LI>RULEDATE - Дата правила</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CALCVARIANTID - Тип расчета</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODCALCRATERULEID - ИД</LI>
     * <LI>PERCENT - Процент</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>RATEDATE - Дата курса</LI>
     * <LI>RATEVALUE - Значение</LI>
     * <LI>RULEDATE - Дата правила</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductCalcRateRuleBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductCalcRateRuleBrowseListByParam", "dsB2BProductCalcRateRuleBrowseListByParamCount", params);
        return result;
    }





}
