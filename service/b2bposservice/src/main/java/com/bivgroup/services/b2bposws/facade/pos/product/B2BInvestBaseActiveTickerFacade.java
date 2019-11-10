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
 * Фасад для сущности B2BInvestBaseActiveTicker
 *
 * @author reson
 */
@IdGen(entityName="B2B_INVBATICKER",idFieldName="INVBATICKERID")
@BOName("B2BInvestBaseActiveTicker")
public class B2BInvestBaseActiveTickerFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestBaseActiveTickerCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestBaseActiveTickerInsert", params);
        result.put("INVBATICKERID", params.get("INVBATICKERID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVBATICKERID"})
    public Map<String,Object> dsB2BInvestBaseActiveTickerInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestBaseActiveTickerInsert", params);
        result.put("INVBATICKERID", params.get("INVBATICKERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVBATICKERID"})
    public Map<String,Object> dsB2BInvestBaseActiveTickerUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestBaseActiveTickerUpdate", params);
        result.put("INVBATICKERID", params.get("INVBATICKERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVBATICKERID"})
    public Map<String,Object> dsB2BInvestBaseActiveTickerModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestBaseActiveTickerUpdate", params);
        result.put("INVBATICKERID", params.get("INVBATICKERID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVBATICKERID"})
    public void dsB2BInvestBaseActiveTickerDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BInvestBaseActiveTickerDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVBATICKERID - ИД связи базового актива с тикером</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVTICKERID - ИД тикера</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestBaseActiveTickerBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BInvestBaseActiveTickerBrowseListByParam", "dsB2BInvestBaseActiveTickerBrowseListByParamCount", params);
        return result;
    }





}
