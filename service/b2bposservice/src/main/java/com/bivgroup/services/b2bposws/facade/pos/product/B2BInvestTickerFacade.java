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
 * Фасад для сущности B2BInvestTicker
 *
 * @author reson
 */
@IdGen(entityName="B2B_INVTICKER",idFieldName="INVTICKERID")
@BOName("B2BInvestTicker")
public class B2BInvestTickerFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BRIEFNAME - Сокращенное наименование</LI>
     * <LI>CODE - Код тикера</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * <LI>NAME - Наименование тикера</LI>
     * <LI>STOCKEXCHNAME - Биржа</LI>
     * <LI>SYSNAME - Системное наименование тикера</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestTickerCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestTickerInsert", params);
        result.put("INVTICKERID", params.get("INVTICKERID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BRIEFNAME - Сокращенное наименование</LI>
     * <LI>CODE - Код тикера</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * <LI>NAME - Наименование тикера</LI>
     * <LI>STOCKEXCHNAME - Биржа</LI>
     * <LI>SYSNAME - Системное наименование тикера</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTICKERID"})
    public Map<String,Object> dsB2BInvestTickerInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestTickerInsert", params);
        result.put("INVTICKERID", params.get("INVTICKERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BRIEFNAME - Сокращенное наименование</LI>
     * <LI>CODE - Код тикера</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * <LI>NAME - Наименование тикера</LI>
     * <LI>STOCKEXCHNAME - Биржа</LI>
     * <LI>SYSNAME - Системное наименование тикера</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTICKERID"})
    public Map<String,Object> dsB2BInvestTickerUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestTickerUpdate", params);
        result.put("INVTICKERID", params.get("INVTICKERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BRIEFNAME - Сокращенное наименование</LI>
     * <LI>CODE - Код тикера</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * <LI>NAME - Наименование тикера</LI>
     * <LI>STOCKEXCHNAME - Биржа</LI>
     * <LI>SYSNAME - Системное наименование тикера</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTICKERID"})
    public Map<String,Object> dsB2BInvestTickerModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestTickerUpdate", params);
        result.put("INVTICKERID", params.get("INVTICKERID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTICKERID"})
    public void dsB2BInvestTickerDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BInvestTickerDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BRIEFNAME - Сокращенное наименование</LI>
     * <LI>CODE - Код тикера</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * <LI>NAME - Наименование тикера</LI>
     * <LI>STOCKEXCHNAME - Биржа</LI>
     * <LI>SYSNAME - Системное наименование тикера</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BRIEFNAME - Сокращенное наименование</LI>
     * <LI>CODE - Код тикера</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * <LI>NAME - Наименование тикера</LI>
     * <LI>STOCKEXCHNAME - Биржа</LI>
     * <LI>SYSNAME - Системное наименование тикера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestTickerBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BInvestTickerBrowseListByParam", "dsB2BInvestTickerBrowseListByParamCount", params);
        return result;
    }





}
