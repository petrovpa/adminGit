/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesReport
 *
 * @author reson
 */
@IdGen(entityName="LOSS_REP",idFieldName="REPID")
@BOName("LossesReport")
public class LossesReportFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * <LI>NAME - Наименование отчета</LI>
     * <LI>PAGEFLOWNAME - Наименование pageflow</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"NAME"})
    public Map<String,Object> dsLossesReportCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesReportInsert", params);
        result.put("REPID", params.get("REPID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * <LI>NAME - Наименование отчета</LI>
     * <LI>PAGEFLOWNAME - Наименование pageflow</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REPID", "NAME"})
    public Map<String,Object> dsLossesReportInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesReportInsert", params);
        result.put("REPID", params.get("REPID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * <LI>NAME - Наименование отчета</LI>
     * <LI>PAGEFLOWNAME - Наименование pageflow</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REPID"})
    public Map<String,Object> dsLossesReportUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesReportUpdate", params);
        result.put("REPID", params.get("REPID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * <LI>NAME - Наименование отчета</LI>
     * <LI>PAGEFLOWNAME - Наименование pageflow</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REPID"})
    public Map<String,Object> dsLossesReportModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesReportUpdate", params);
        result.put("REPID", params.get("REPID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"REPID"})
    public void dsLossesReportDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesReportDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * <LI>NAME - Наименование отчета</LI>
     * <LI>PAGEFLOWNAME - Наименование pageflow</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * <LI>NAME - Наименование отчета</LI>
     * <LI>PAGEFLOWNAME - Наименование pageflow</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesReportBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesReportBrowseListByParam", "dsLossesReportBrowseListByParamCount", params);
        return result;
    }





}
