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
 * Фасад для сущности B2BReport
 *
 * @author reson
 */
@IdGen(entityName="B2B_REP",idFieldName="REPID")
@BOName("B2BReport")
public class B2BReportFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * <LI>NAME - Наименование отчета</LI>
     * <LI>TEMPLATENAME - Наименование шаблона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"NAME"})
    public Map<String,Object> dsB2BReportCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BReportInsert", params);
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
     * <LI>TEMPLATENAME - Наименование шаблона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REPID", "NAME"})
    public Map<String,Object> dsB2BReportInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BReportInsert", params);
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
     * <LI>TEMPLATENAME - Наименование шаблона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REPID"})
    public Map<String,Object> dsB2BReportUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BReportUpdate", params);
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
     * <LI>TEMPLATENAME - Наименование шаблона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REPID"})
    public Map<String,Object> dsB2BReportModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BReportUpdate", params);
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
    public void dsB2BReportDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BReportDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * <LI>NAME - Наименование отчета</LI>
     * <LI>TEMPLATENAME - Наименование шаблона</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REPID - ИД отчета</LI>
     * <LI>NAME - Наименование отчета</LI>
     * <LI>TEMPLATENAME - Наименование шаблона</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BReportBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BReportBrowseListByParam", "dsB2BReportBrowseListByParamCount", params);
        return result;
    }





}
