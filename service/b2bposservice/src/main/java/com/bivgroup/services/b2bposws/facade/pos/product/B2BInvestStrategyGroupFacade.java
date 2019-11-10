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
 * Фасад для сущности B2BInvestStrategyGroup
 *
 * @author reson
 */
@IdGen(entityName="B2B_INVSTRATGROUP",idFieldName="INVSTRATGROUPID")
@BOName("B2BInvestStrategyGroup")
public class B2BInvestStrategyGroupFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - код</LI>
     * <LI>INVSTRATGROUPID - ид группы инвестиционных стратегий</LI>
     * <LI>NAME - наименование</LI>
     * <LI>NOTE - примечание</LI>
     * <LI>SYSNAME - системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVSTRATGROUPID - ид группы инвестиционных стратегий</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestStrategyGroupCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestStrategyGroupInsert", params);
        result.put("INVSTRATGROUPID", params.get("INVSTRATGROUPID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - код</LI>
     * <LI>INVSTRATGROUPID - ид группы инвестиционных стратегий</LI>
     * <LI>NAME - наименование</LI>
     * <LI>NOTE - примечание</LI>
     * <LI>SYSNAME - системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVSTRATGROUPID - ид группы инвестиционных стратегий</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVSTRATGROUPID"})
    public Map<String,Object> dsB2BInvestStrategyGroupInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestStrategyGroupInsert", params);
        result.put("INVSTRATGROUPID", params.get("INVSTRATGROUPID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - код</LI>
     * <LI>INVSTRATGROUPID - ид группы инвестиционных стратегий</LI>
     * <LI>NAME - наименование</LI>
     * <LI>NOTE - примечание</LI>
     * <LI>SYSNAME - системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVSTRATGROUPID - ид группы инвестиционных стратегий</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVSTRATGROUPID"})
    public Map<String,Object> dsB2BInvestStrategyGroupUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestStrategyGroupUpdate", params);
        result.put("INVSTRATGROUPID", params.get("INVSTRATGROUPID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - код</LI>
     * <LI>INVSTRATGROUPID - ид группы инвестиционных стратегий</LI>
     * <LI>NAME - наименование</LI>
     * <LI>NOTE - примечание</LI>
     * <LI>SYSNAME - системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVSTRATGROUPID - ид группы инвестиционных стратегий</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVSTRATGROUPID"})
    public Map<String,Object> dsB2BInvestStrategyGroupModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestStrategyGroupUpdate", params);
        result.put("INVSTRATGROUPID", params.get("INVSTRATGROUPID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVSTRATGROUPID - ид группы инвестиционных стратегий</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVSTRATGROUPID"})
    public void dsB2BInvestStrategyGroupDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BInvestStrategyGroupDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - код</LI>
     * <LI>INVSTRATGROUPID - ид группы инвестиционных стратегий</LI>
     * <LI>NAME - наименование</LI>
     * <LI>NOTE - примечание</LI>
     * <LI>SYSNAME - системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CODE - код</LI>
     * <LI>INVSTRATGROUPID - ид группы инвестиционных стратегий</LI>
     * <LI>NAME - наименование</LI>
     * <LI>NOTE - примечание</LI>
     * <LI>SYSNAME - системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestStrategyGroupBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BInvestStrategyGroupBrowseListByParam", "dsB2BInvestStrategyGroupBrowseListByParamCount", params);
        return result;
    }





}
