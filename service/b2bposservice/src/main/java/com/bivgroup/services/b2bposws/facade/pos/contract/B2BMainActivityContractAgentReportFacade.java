/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.autonumber.AutoNumber;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BMainActivityContractAgentReport
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@AutoNumber(autoNumberFieldName = "REPORTNUMBER",dataParamName = "CREATEDATE")
@State(idFieldName = "MACAGENTREPORTID", startStateName = "B2B_MACAGENTREPORT_NEW", typeSysName = "B2B_MACAGENTREPORT")
@IdGen(entityName="B2B_MACAGENTREPORT",idFieldName="MACAGENTREPORTID")
@BOName("B2BMainActivityContractAgentReport")
public class B2BMainActivityContractAgentReportFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>AGENTID - ИД агента. ид орг структуры агента</LI>
     * <LI>CREATEDATE - Дата создания отчета</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего отчет</LI>
     * <LI>ENDDATE - Дата по</LI>
     * <LI>MACAGENTREPORTID - ИД отчета</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * <LI>STATEID - ИД состояния отчета</LI>
     * <LI>UPDATEDATE - Дата изменения отчета</LI>
     * <LI>UPDATEUSERID - ИД изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTREPORTID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractAgentReportCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractAgentReportInsert", params);
        result.put("MACAGENTREPORTID", params.get("MACAGENTREPORTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>AGENTID - ИД агента. ид орг структуры агента</LI>
     * <LI>CREATEDATE - Дата создания отчета</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего отчет</LI>
     * <LI>ENDDATE - Дата по</LI>
     * <LI>MACAGENTREPORTID - ИД отчета</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * <LI>STATEID - ИД состояния отчета</LI>
     * <LI>UPDATEDATE - Дата изменения отчета</LI>
     * <LI>UPDATEUSERID - ИД изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTREPORTID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTREPORTID"})
    public Map<String,Object> dsB2BMainActivityContractAgentReportInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractAgentReportInsert", params);
        result.put("MACAGENTREPORTID", params.get("MACAGENTREPORTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AGENTID - ИД агента. ид орг структуры агента</LI>
     * <LI>CREATEDATE - Дата создания отчета</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего отчет</LI>
     * <LI>ENDDATE - Дата по</LI>
     * <LI>MACAGENTREPORTID - ИД отчета</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * <LI>STATEID - ИД состояния отчета</LI>
     * <LI>UPDATEDATE - Дата изменения отчета</LI>
     * <LI>UPDATEUSERID - ИД изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTREPORTID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTREPORTID"})
    public Map<String,Object> dsB2BMainActivityContractAgentReportUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractAgentReportUpdate", params);
        result.put("MACAGENTREPORTID", params.get("MACAGENTREPORTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AGENTID - ИД агента. ид орг структуры агента</LI>
     * <LI>CREATEDATE - Дата создания отчета</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего отчет</LI>
     * <LI>ENDDATE - Дата по</LI>
     * <LI>MACAGENTREPORTID - ИД отчета</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * <LI>STATEID - ИД состояния отчета</LI>
     * <LI>UPDATEDATE - Дата изменения отчета</LI>
     * <LI>UPDATEUSERID - ИД изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTREPORTID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTREPORTID"})
    public Map<String,Object> dsB2BMainActivityContractAgentReportModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractAgentReportUpdate", params);
        result.put("MACAGENTREPORTID", params.get("MACAGENTREPORTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MACAGENTREPORTID - ИД отчета</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTREPORTID"})
    public void dsB2BMainActivityContractAgentReportDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMainActivityContractAgentReportDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>AGENTID - ИД агента. ид орг структуры агента</LI>
     * <LI>CREATEDATE - Дата создания отчета</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего отчет</LI>
     * <LI>ENDDATE - Дата по</LI>
     * <LI>MACAGENTREPORTID - ИД отчета</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * <LI>STATEID - ИД состояния отчета</LI>
     * <LI>UPDATEDATE - Дата изменения отчета</LI>
     * <LI>UPDATEUSERID - ИД изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AGENTID - ИД агента. ид орг структуры агента</LI>
     * <LI>CREATEDATE - Дата создания отчета</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего отчет</LI>
     * <LI>ENDDATE - Дата по</LI>
     * <LI>MACAGENTREPORTID - ИД отчета</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODVERID - Версия продукта</LI>
     * <LI>STATEID - ИД состояния отчета</LI>
     * <LI>UPDATEDATE - Дата изменения отчета</LI>
     * <LI>UPDATEUSERID - ИД изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractAgentReportBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMainActivityContractAgentReportBrowseListByParam", "dsB2BMainActivityContractAgentReportBrowseListByParamCount", params);
        return result;
    }





}
