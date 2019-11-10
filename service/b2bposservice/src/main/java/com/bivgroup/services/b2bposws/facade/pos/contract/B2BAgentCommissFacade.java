/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BAgentCommiss
 *
 * @author reson
 */
@IdGen(entityName="B2B_AGENTCOMMISS",idFieldName="AGENTCOMMISSID")
@BOName("B2BAgentCommiss")
public class B2BAgentCommissFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ид договора страхования</LI>
     * <LI>CONTRSECTIONID - Ид секции договора страхования</LI>
     * <LI>AGENTCOMMISSID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PARENTACID - ИД заголовка комиссии</LI>
     * <LI>PREMVALUE - Сумма премии агента</LI>
     * <LI>PROCESSDATE - Дата расчета</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>TARIFF - Тариф</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AGENTCOMMISSID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAgentCommissCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BAgentCommissInsert", params);
        result.put("AGENTCOMMISSID", params.get("AGENTCOMMISSID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ид договора страхования</LI>
     * <LI>CONTRSECTIONID - Ид секции договора страхования</LI>
     * <LI>AGENTCOMMISSID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PARENTACID - ИД заголовка комиссии</LI>
     * <LI>PREMVALUE - Сумма премии агента</LI>
     * <LI>PROCESSDATE - Дата расчета</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>TARIFF - Тариф</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AGENTCOMMISSID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"AGENTCOMMISSID"})
    public Map<String,Object> dsB2BAgentCommissInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BAgentCommissInsert", params);
        result.put("AGENTCOMMISSID", params.get("AGENTCOMMISSID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ид договора страхования</LI>
     * <LI>CONTRSECTIONID - Ид секции договора страхования</LI>
     * <LI>AGENTCOMMISSID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PARENTACID - ИД заголовка комиссии</LI>
     * <LI>PREMVALUE - Сумма премии агента</LI>
     * <LI>PROCESSDATE - Дата расчета</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>TARIFF - Тариф</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AGENTCOMMISSID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"AGENTCOMMISSID"})
    public Map<String,Object> dsB2BAgentCommissUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BAgentCommissUpdate", params);
        result.put("AGENTCOMMISSID", params.get("AGENTCOMMISSID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ид договора страхования</LI>
     * <LI>CONTRSECTIONID - Ид секции договора страхования</LI>
     * <LI>AGENTCOMMISSID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PARENTACID - ИД заголовка комиссии</LI>
     * <LI>PREMVALUE - Сумма премии агента</LI>
     * <LI>PROCESSDATE - Дата расчета</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>TARIFF - Тариф</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AGENTCOMMISSID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"AGENTCOMMISSID"})
    public Map<String,Object> dsB2BAgentCommissModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BAgentCommissUpdate", params);
        result.put("AGENTCOMMISSID", params.get("AGENTCOMMISSID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>AGENTCOMMISSID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"AGENTCOMMISSID"})
    public void dsB2BAgentCommissDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BAgentCommissDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ид договора страхования</LI>
     * <LI>CONTRSECTIONID - Ид секции договора страхования</LI>
     * <LI>AGENTCOMMISSID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PARENTACID - ИД заголовка комиссии</LI>
     * <LI>PREMVALUE - Сумма премии агента</LI>
     * <LI>PROCESSDATE - Дата расчета</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>TARIFF - Тариф</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - Ид договора страхования</LI>
     * <LI>CONTRSECTIONID - Ид секции договора страхования</LI>
     * <LI>AGENTCOMMISSID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PARENTACID - ИД заголовка комиссии</LI>
     * <LI>PREMVALUE - Сумма премии агента</LI>
     * <LI>PROCESSDATE - Дата расчета</LI>
     * <LI>PRODSTRUCTID - ИД структуры продукта</LI>
     * <LI>TARIFF - Тариф</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAgentCommissBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BAgentCommissBrowseListByParam", "dsB2BAgentCommissBrowseListByParamCount", params);
        return result;
    }





}
