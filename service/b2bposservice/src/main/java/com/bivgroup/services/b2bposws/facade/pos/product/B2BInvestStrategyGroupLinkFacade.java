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
 * Фасад для сущности B2BInvestStrategyGroupLink
 *
 * @author reson
 */
@IdGen(entityName="B2B_INVSTRATGROUPLINK",idFieldName="INVSTRATGROUPLINKID")
@BOName("B2BInvestStrategyGroupLink")
public class B2BInvestStrategyGroupLinkFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVSTRATGROUPLINKID - ИД связи</LI>
     * <LI>INVSTRATGROUPID - ИД группы стратегий</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVSTRATGROUPLINKID - ИД связи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestStrategyGroupLinkCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestStrategyGroupLinkInsert", params);
        result.put("INVSTRATGROUPLINKID", params.get("INVSTRATGROUPLINKID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVSTRATGROUPLINKID - ИД связи</LI>
     * <LI>INVSTRATGROUPID - ИД группы стратегий</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVSTRATGROUPLINKID - ИД связи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVSTRATGROUPLINKID"})
    public Map<String,Object> dsB2BInvestStrategyGroupLinkInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestStrategyGroupLinkInsert", params);
        result.put("INVSTRATGROUPLINKID", params.get("INVSTRATGROUPLINKID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>INVSTRATGROUPLINKID - ИД связи</LI>
     * <LI>INVSTRATGROUPID - ИД группы стратегий</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVSTRATGROUPLINKID - ИД связи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVSTRATGROUPLINKID"})
    public Map<String,Object> dsB2BInvestStrategyGroupLinkUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestStrategyGroupLinkUpdate", params);
        result.put("INVSTRATGROUPLINKID", params.get("INVSTRATGROUPLINKID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>INVSTRATGROUPLINKID - ИД связи</LI>
     * <LI>INVSTRATGROUPID - ИД группы стратегий</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVSTRATGROUPLINKID - ИД связи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVSTRATGROUPLINKID"})
    public Map<String,Object> dsB2BInvestStrategyGroupLinkModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestStrategyGroupLinkUpdate", params);
        result.put("INVSTRATGROUPLINKID", params.get("INVSTRATGROUPLINKID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVSTRATGROUPLINKID - ИД связи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVSTRATGROUPLINKID"})
    public void dsB2BInvestStrategyGroupLinkDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BInvestStrategyGroupLinkDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>INVSTRATGROUPLINKID - ИД связи</LI>
     * <LI>INVSTRATGROUPID - ИД группы стратегий</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVSTRATGROUPLINKID - ИД связи</LI>
     * <LI>INVSTRATGROUPID - ИД группы стратегий</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestStrategyGroupLinkBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BInvestStrategyGroupLinkBrowseListByParam", "dsB2BInvestStrategyGroupLinkBrowseListByParamCount", params);
        return result;
    }





}
