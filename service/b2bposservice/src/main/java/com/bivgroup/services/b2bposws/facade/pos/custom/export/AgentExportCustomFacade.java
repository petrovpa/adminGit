/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.export;

import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author sambucus
 */
@BOName("AgentExportCustom")
public class AgentExportCustomFacade extends ExportCustomFacade {

    private static final String B2BPOSWS = Constants.B2BPOSWS;

    @WsMethod()
    public Map<String, Object> dsB2BBrowseData4AgentReportAndCalcComiss(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Double sumComiss = 0.0;
        Double sumPrem = 0.0;
        Double sumnds = 0.0;
        Double sumComisNoNDS = 0.0;
        if (params.get("CONTRLIST") != null) {
            List<Map<String, Object>> contrList = (List<Map<String, Object>>) params.get("CONTRLIST");
            for (Map<String, Object> contrMap : contrList) {
                calcAgentPrem(contrMap, login, password);
                Long insAmCurrency = getLongParam(contrMap.get("INSAMCURRENCYID"));
                Double premValue = getDoubleParam(contrMap.get("PREMVALUE"));
                Double agentPrem = getDoubleParam(contrMap.get("AGENTPREM"));
                if (Long.compare(1L, insAmCurrency.longValue()) == 0) {
                    // договор рублевый. приводить не требуется
                } else {
                    Double exchangeRate = getDoubleParam(contrMap.get("CURRENCYRATE"));
                    premValue = roundSum(premValue * exchangeRate);
                    agentPrem = roundSum(agentPrem * exchangeRate);
                    contrMap.put("PREMVALUE", premValue);
                    contrMap.put("AGENTPREM", agentPrem);
                }
                sumComiss = agentPrem + sumComiss;
                sumPrem = sumPrem + premValue;
                contrMap.put("COMMISSIONNDSVALUE",agentPrem);
                Double agentNDS = roundSum((agentPrem*18)/118);
                sumnds = sumnds + agentNDS;
                Double agentPremNoNDS = agentPrem - agentNDS;
                sumComisNoNDS = sumComisNoNDS + agentPremNoNDS;
                contrMap.put("NDSVALUE",agentNDS);
                contrMap.put("COMMISSIONVALUE",agentPremNoNDS);
                
            }
        }
        Map<String,Object> totals = new HashMap<>();
        
                totals.put("TOTALPREMVALUE",sumPrem);
                totals.put("TOTALCOMMISSIONNDSVALUE",sumComiss);
           //     Double sumagentNDS = roundSum((sumComiss*18)/100);
                totals.put("TOTALNDSVALUE",sumnds);
           //     Double sumagentPremNoNDS = sumComiss - sumagentNDS;
                totals.put("TOTALCOMMISSIONVALUE",sumComisNoNDS);
        params.put("TOTALS", totals);

        return null;
    }

    @WsMethod(requiredParams = {"EXPORTDATAID", "TEMPLATEID"})
    public Map<String, Object> dsB2BExportDataCreateAgentReport(Map<String, Object> params) throws Exception {

        logger.debug("Export data report creating...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        // идентификатор обрабатываемой записи
        Long exportDataID = getLongParam(params.get("EXPORTDATAID"));
        logger.debug("Export data record id (EXPORTDATAID) = " + exportDataID);

        // получение шаблона обрабатываемой записи
        Long templateID = getLongParam(params.get("TEMPLATEID"));
        logger.debug("Template id for this export data (TEMPLATEID) = " + templateID);
        Map<String, Object> template = getExportDataTemplateByID(templateID, login, password);

        // получение текста запроса из шаблона обрабатываемой записи
        String dataQueryText = getStringParam(template.get("DATASQL"));
        String dataMethod = "";
        logger.debug("Data query text for this template (DATASQL) = \n\n" + dataQueryText + "\n");
        if (dataQueryText.isEmpty()) {
            // получение текста запроса из шаблона обрабатываемой записи
            dataQueryText = getStringParam(template.get("DATASQLCLOB"));
            logger.debug("Data query text for this template (DATASQL) = \n\n" + dataQueryText + "\n");
            if (dataQueryText.isEmpty()) {
                throw new Exception(String.format("No data query text (DATASQL) found in template with id (TEMPLATEID) = %d for export data record with id (EXPORTDATAID) = %d.", templateID, exportDataID));
            }
        }
        dataMethod = getStringParam(template.get("DATAMETHOD"));
        logger.debug("Data method for this template (DATAMETHOD) = \n\n" + dataQueryText + "\n");
        if (dataMethod.isEmpty()) {
            throw new Exception(String.format("No data method (DATAMETHOD) found in template with id (TEMPLATEID) = %d for export data record with id (EXPORTDATAID) = %d.", templateID, exportDataID));
        }

        // строковый список идентификаторов объектов
        String objectIDsListStr = getObjectIDsListStrByExportDataID(exportDataID, login, password);

        // формирование списка со сведениями объектов
        List<Map<String, Object>> dataList = null;
        List<Map<String, Object>> contrList = null;
        if (objectIDsListStr.isEmpty()) {
            // если список идентификаторов пуст - то и список запрошенных данных также будет пуст
            contrList = new ArrayList<Map<String, Object>>();
        } else {
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("OBJECTIDLIST", objectIDsListStr);
            queryParams.put("EXPORTDATAID", exportDataID);
            if (!dataQueryText.isEmpty()) {
                // параметры для постраничных запросов, фомируются angular-гридом
                // запрос списка со сведениями объектов
                Map<String, Object> queryRes = doQuery("B2BExportData", dataQueryText, queryParams);
                contrList = WsUtils.getListFromResultMap(queryRes);
                
            }
            if (!dataMethod.isEmpty()) {
                params.put("CONTRLIST", contrList);
                Map<String, Object> queryRes = this.callService(B2BPOSWS, dataMethod, params, login, password);
                
                dataList = WsUtils.getListFromResultMap(queryRes);
            }
        }

        // подготовка параметров для генерации отчета
        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put("CONTRLIST", contrList);
        reportData.put("TOTALS", params.get("TOTALS"));
        reportData.put("DATALIST", dataList);
        reportData.put("TODAYDATE", new Date());
        Map<String, Object> exportDataMap = getExportData(exportDataID, login, password);
        reportData.put("EXPORTDATAMAP", exportDataMap);
        reportData.put("TEMPLATEMAP", template);        
        parseDates(reportData, String.class);
        genDateStrs(reportData, "*");
        genSumStrs(reportData, "*");
        String reportFormat = ".docx";

        /*
        String templateName = getStringParam(template.get("REPTEMPLATENAME"));

        // генерация отчета
        String reportName = genExportReport(reportData, reportFormat, templateName, login, password);
         */

        // генерация отчета
        Map<String, Object> reportNameResult = genExportReport(reportData, reportFormat, template, exportDataID, login, password);
        if (reportNameResult.get("Status").toString() == "ERROR") {
           return reportNameResult;
        }
        String reportName = reportNameResult.get("reportNameWithExt").toString();
        // шифрование имен файлов отчета для возврата в angular-интерфейс
        String encryptedFileNamesStr = getEncryptedFileNamesStr(reportName);
        if (!encryptedFileNamesStr.isEmpty()) {
            result.put("ENCRIPTEDFILENAME", encryptedFileNamesStr);
        }

        // сгенерированный отчет выдать пользователю в интерфейсе
        logger.debug("Export data report creating finish.");
        return result;

    }
    
    private Map<String, Object> getExportData(Long exportDataID, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("ReturnAsHashMap", "TRUE");
        param.put("EXPORTDATAID", exportDataID);
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BExportDataBrowseListByParam", param, login, password);
        Long createUserId = getLongParam(res.get("CREATEUSERID"));
        Map<String, Object> emplParams = new HashMap<>();
        emplParams.put("useraccountid", createUserId);
        emplParams.put(WsConstants.LOGIN, login);
        emplParams.put(WsConstants.PASSWORD, password);
        Map<String, Object> emplRes = this.selectQuery("dsUserAccountGetInfoById", null, emplParams);
        if (emplRes.get(RESULT) != null) {
            List<Map<String, Object>> emplResList = (List<Map<String, Object>>) emplRes.get(RESULT);
            if (!emplResList.isEmpty()) {
                emplRes = emplResList.get(0);
            }
        }
        Map<String, Object> agentParams = new HashMap<>();
        agentParams.put("ORGSTRUCTID", emplRes.get("DEPARTMENTID"));
        agentParams.put("ReturnAsHashMap", "TRUE");
        
        Map<String, Object> agentMainActContrRes = this.callService(B2BPOSWS, 
                "dsB2BMainActivityContractFindByOrgStructId", agentParams, login, password);
        // TODO: если не нашли, для теста - брать первый попавшийся. (т.к. он сейчас единственный.)
        if (agentMainActContrRes.get("CONTRNUMBER") == null) {
            //текущий пользователь не находится в ветке партнера с заключенным агентским договором.
            //выбираем агентский договор для теста стандартный
            agentParams.put("ORGSTRUCTID", 29010);
            agentParams.put("MAINACTCONTRID", 2000);
            agentParams.put("ReturnAsHashMap", "TRUE");
            
            agentMainActContrRes = this.callService(B2BPOSWS, 
                "dsB2BMainActivityContractAgentBrowseListByParam", agentParams, login, password);
            
        }
        // TODO: Брать ограничения по продукту для выгрузки из агентского договора. 
        // (сейчас договор 1 и для выгрузки ограничения по продукту заданы в скрипте SQL шаблона выгрузки)                        
        emplRes.remove("PASSWORD");
        res.put("MAINACTCONTRINFO", agentMainActContrRes);
        res.put("CREATEUSERINFO", emplRes);

        Map<String, Object> checkLoginParams = new HashMap<String, Object>();
        checkLoginParams.put("username", XMLUtil.getUserName(login));
        checkLoginParams.put("passwordSha", password);
        checkLoginParams.put(WsConstants.LOGIN, login);
        checkLoginParams.put(WsConstants.PASSWORD, password);
        Map<String, Object> checkLoginResult = WsUtils.getFirstItemFromResultMap(this.selectQuery("dsCheckLogin", checkLoginParams));
        if (checkLoginResult != null) {
            Long exportuserid = getLongParam(checkLoginResult.get("USERACCOUNTID"));
            Map<String, Object> emplParams1 = new HashMap<>();
            emplParams1.put("useraccountid", exportuserid);
            emplParams1.put(WsConstants.LOGIN, login);
            emplParams1.put(WsConstants.PASSWORD, password);
            Map<String, Object> emplRes1 = this.selectQuery("dsUserAccountGetInfoById", null, emplParams1);
            if (emplRes1.get(RESULT) != null) {
                List<Map<String, Object>> emplResList = (List<Map<String, Object>>) emplRes1.get(RESULT);
                if (!emplResList.isEmpty()) {
                    emplRes1 = emplResList.get(0);
                }
            }

            emplRes1.remove("PASSWORD");
            res.put("EXPORTUSERINFO", emplRes1);

        }

        parseDates(res, String.class);
        return res;
    }    
}
