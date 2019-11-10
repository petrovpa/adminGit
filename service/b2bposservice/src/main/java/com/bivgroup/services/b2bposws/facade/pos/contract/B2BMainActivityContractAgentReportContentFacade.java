/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BMainActivityContractAgentReportContent
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_MACAGENTREPORTCNT",idFieldName="MACAGENTREPORTCNTID")
@BOName("B2BMainActivityContractAgentReportContent")
public class B2BMainActivityContractAgentReportContentFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - Ид пользователя, создавшего запись</LI>
     * <LI>MACAGENTREPORTCNTID - ИД содержимого отчета</LI>
     * <LI>ISREMOVED - Исключен</LI>
     * <LI>MACAGENTREPORTID - ИД отчета агента</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTREPORTCNTID - ИД содержимого отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractAgentReportContentCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractAgentReportContentInsert", params);
        result.put("MACAGENTREPORTCNTID", params.get("MACAGENTREPORTCNTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - Ид пользователя, создавшего запись</LI>
     * <LI>MACAGENTREPORTCNTID - ИД содержимого отчета</LI>
     * <LI>ISREMOVED - Исключен</LI>
     * <LI>MACAGENTREPORTID - ИД отчета агента</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTREPORTCNTID - ИД содержимого отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTREPORTCNTID"})
    public Map<String,Object> dsB2BMainActivityContractAgentReportContentInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractAgentReportContentInsert", params);
        result.put("MACAGENTREPORTCNTID", params.get("MACAGENTREPORTCNTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - Ид пользователя, создавшего запись</LI>
     * <LI>MACAGENTREPORTCNTID - ИД содержимого отчета</LI>
     * <LI>ISREMOVED - Исключен</LI>
     * <LI>MACAGENTREPORTID - ИД отчета агента</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTREPORTCNTID - ИД содержимого отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTREPORTCNTID"})
    public Map<String,Object> dsB2BMainActivityContractAgentReportContentUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractAgentReportContentUpdate", params);
        result.put("MACAGENTREPORTCNTID", params.get("MACAGENTREPORTCNTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - Ид пользователя, создавшего запись</LI>
     * <LI>MACAGENTREPORTCNTID - ИД содержимого отчета</LI>
     * <LI>ISREMOVED - Исключен</LI>
     * <LI>MACAGENTREPORTID - ИД отчета агента</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACAGENTREPORTCNTID - ИД содержимого отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTREPORTCNTID"})
    public Map<String,Object> dsB2BMainActivityContractAgentReportContentModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractAgentReportContentUpdate", params);
        result.put("MACAGENTREPORTCNTID", params.get("MACAGENTREPORTCNTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MACAGENTREPORTCNTID - ИД содержимого отчета</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACAGENTREPORTCNTID"})
    public void dsB2BMainActivityContractAgentReportContentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMainActivityContractAgentReportContentDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - Ид пользователя, создавшего запись</LI>
     * <LI>MACAGENTREPORTCNTID - ИД содержимого отчета</LI>
     * <LI>ISREMOVED - Исключен</LI>
     * <LI>MACAGENTREPORTID - ИД отчета агента</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - Ид пользователя, создавшего запись</LI>
     * <LI>MACAGENTREPORTCNTID - ИД содержимого отчета</LI>
     * <LI>ISREMOVED - Исключен</LI>
     * <LI>MACAGENTREPORTID - ИД отчета агента</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractAgentReportContentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMainActivityContractAgentReportContentBrowseListByParam", "dsB2BMainActivityContractAgentReportContentBrowseListByParamCount", params);
        return result;
    }





}
