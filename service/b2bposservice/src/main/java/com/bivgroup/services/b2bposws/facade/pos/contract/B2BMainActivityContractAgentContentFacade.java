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
 * Фасад для сущности B2BMainActivityContractAgentContent
 *
 * @author reson
 */
@IdGen(entityName="B2B_MACAGENTCNT",idFieldName="MACAGENTCNTID")
@BOName("B2BMainActivityContractAgentContent")
public class B2BMainActivityContractAgentContentFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД договора по основной деятельности</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractAgentContentCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractAgentContentInsert", params);
        result.put("MACAGENTCNTID", params.get("MACAGENTCNTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД договора по основной деятельности</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTCNTID"})
    public Map<String,Object> dsB2BMainActivityContractAgentContentInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractAgentContentInsert", params);
        result.put("MACAGENTCNTID", params.get("MACAGENTCNTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД договора по основной деятельности</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTCNTID"})
    public Map<String,Object> dsB2BMainActivityContractAgentContentUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractAgentContentUpdate", params);
        result.put("MACAGENTCNTID", params.get("MACAGENTCNTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД договора по основной деятельности</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTCNTID"})
    public Map<String,Object> dsB2BMainActivityContractAgentContentModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractAgentContentUpdate", params);
        result.put("MACAGENTCNTID", params.get("MACAGENTCNTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTCNTID"})
    public void dsB2BMainActivityContractAgentContentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMainActivityContractAgentContentDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД договора по основной деятельности</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DEFAULTCOMMISSION - Комиссия по-умолчанию</LI>
     * <LI>MACAGENTCNTID - ИД записи</LI>
     * <LI>MAINACTCONTRID - ИД договора по основной деятельности</LI>
     * <LI>MAXCOMMISSION - Максимальная комиссия</LI>
     * <LI>MINCOMMISSION - Минимальная комиссия</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractAgentContentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMainActivityContractAgentContentBrowseListByParam", "dsB2BMainActivityContractAgentContentBrowseListByParamCount", params);
        return result;
    }





}
