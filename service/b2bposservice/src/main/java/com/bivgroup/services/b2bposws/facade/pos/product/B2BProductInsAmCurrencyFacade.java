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
 * Фасад для сущности B2BProductInsAmCurrency
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODINSAMCUR",idFieldName="PRODINSAMCURID")
@BOName("B2BProductInsAmCurrency")
public class B2BProductInsAmCurrencyFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODINSAMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINSAMCURID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductInsAmCurrencyCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductInsAmCurrencyInsert", params);
        result.put("PRODINSAMCURID", params.get("PRODINSAMCURID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODINSAMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINSAMCURID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINSAMCURID"})
    public Map<String,Object> dsB2BProductInsAmCurrencyInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductInsAmCurrencyInsert", params);
        result.put("PRODINSAMCURID", params.get("PRODINSAMCURID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODINSAMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINSAMCURID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINSAMCURID"})
    public Map<String,Object> dsB2BProductInsAmCurrencyUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductInsAmCurrencyUpdate", params);
        result.put("PRODINSAMCURID", params.get("PRODINSAMCURID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODINSAMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINSAMCURID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINSAMCURID"})
    public Map<String,Object> dsB2BProductInsAmCurrencyModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductInsAmCurrencyUpdate", params);
        result.put("PRODINSAMCURID", params.get("PRODINSAMCURID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODINSAMCURID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINSAMCURID"})
    public void dsB2BProductInsAmCurrencyDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductInsAmCurrencyDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODINSAMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>PRODINSAMCURID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта (устаревшее, теперь прикрепляется к секции договора)</LI>
     * <LI>PRODSTRUCTID - ИД структуры секции договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductInsAmCurrencyBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductInsAmCurrencyBrowseListByParam", "dsB2BProductInsAmCurrencyBrowseListByParamCount", params);
        return result;
    }

    /**
     * Получить список валют для версии продукта
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"PRODVERID"})
    public Map<String,Object> dsB2BProductInsAmCurrencyListByProdVerId(Map<String, Object> params) throws Exception {
        return this.selectQuery("dsB2BProductInsAmCurrencyListByProdVerId", "dsB2BProductInsAmCurrencyListByProdVerIdCount", params);
    }
}
