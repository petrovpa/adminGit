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
 * Фасад для сущности B2BProductPremiumCurrency
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODPREMCUR",idFieldName="PRODPREMCURID")
@BOName("B2BProductPremiumCurrency")
public class B2BProductPremiumCurrencyFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODPREMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPREMCURID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductPremiumCurrencyCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductPremiumCurrencyInsert", params);
        result.put("PRODPREMCURID", params.get("PRODPREMCURID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODPREMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPREMCURID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPREMCURID"})
    public Map<String,Object> dsB2BProductPremiumCurrencyInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductPremiumCurrencyInsert", params);
        result.put("PRODPREMCURID", params.get("PRODPREMCURID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODPREMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPREMCURID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPREMCURID"})
    public Map<String,Object> dsB2BProductPremiumCurrencyUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductPremiumCurrencyUpdate", params);
        result.put("PRODPREMCURID", params.get("PRODPREMCURID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODPREMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODPREMCURID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPREMCURID"})
    public Map<String,Object> dsB2BProductPremiumCurrencyModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductPremiumCurrencyUpdate", params);
        result.put("PRODPREMCURID", params.get("PRODPREMCURID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODPREMCURID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODPREMCURID"})
    public void dsB2BProductPremiumCurrencyDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductPremiumCurrencyDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODPREMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODPREMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductPremiumCurrencyBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductPremiumCurrencyBrowseListByParam", "dsB2BProductPremiumCurrencyBrowseListByParamCount", params);
        return result;
    }





}
