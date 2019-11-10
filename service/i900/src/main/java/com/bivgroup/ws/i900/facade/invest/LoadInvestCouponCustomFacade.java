/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.i900.facade.invest;

import com.bivgroup.ws.i900.Mort900Exception;
import com.bivgroup.ws.i900.facade.Mort900BaseFacade;
import com.bivgroup.ws.i900.system.CommonPaymentPurposeProcessor;
import com.bivgroup.ws.i900.system.DatesParser;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.system.external.ExternalService;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author aklunok
 */
@BOName("LoadInvestCouponCustom")
public class LoadInvestCouponCustomFacade extends Mort900BaseFacade {

    private static DatesParser datesParser;

    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

    public LoadInvestCouponCustomFacade() {
        super();
        init();
    }

    private void init() {
        datesParser = new DatesParser();
        // протоколирование операций с датами
        datesParser.setVerboseLogging(IS_VERBOSE_LOGGING);
    }

    private Map<String, Object> commonProcessData(Map<String, Object> processParams) throws Exception, Mort900Exception {
        // Назначение платежа
        List<Map<String, Object>> purposeDetails = (List<Map<String, Object>>) processParams.get("BANKPURPOSEDETAILLIST");
        // список деталей назначения платежа из шаблона обработки банковской выписки
        List<Map<String, Object>> purposeTemplateDetails = (List<Map<String, Object>>) processParams.get("BANKSTATETEMPLATEPURPOSEDETAILLIST");
        // основные данные банковской выписки
        Map<String, Object> bankCashFlow = (Map<String, Object>) processParams.get("BANKCASHFLOW");

        CommonPaymentPurposeProcessor paymentPurposeProcessor = new CommonPaymentPurposeProcessor();
        Map<String, Object> rowData = paymentPurposeProcessor.processRow(purposeDetails, purposeTemplateDetails, bankCashFlow);

        if(processParams.containsKey("BANKCASHFLOWID")) {
                rowData.put("BANKCASHFLOWID", processParams.get("BANKCASHFLOWID"));
        }
        else {
            if(bankCashFlow.containsKey("BANKCASHFLOWID")) {
                rowData.put("BANKCASHFLOWID", bankCashFlow.get("BANKCASHFLOWID"));
            }
        }
        return rowData;
    }

    @WsMethod(requiredParams = {"BANKPURPOSEDETAILLIST", "BANKSTATETEMPLATEPURPOSEDETAILLIST", "BANKCASHFLOW"})
    public Map<String, Object> dsB2BLoadInvestCouponProcess(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        long typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_ADD;
        Map<String, Object> rowData = commonProcessData(params);
        String bankCashFlowID = getStringParam(rowData, "BANKCASHFLOWID");

        Date calcDate = (Date) datesParser.parseAnyDate(rowData.get("CALCDATE"), Date.class, "CALCDATE", true);
        if (calcDate == null) {
            throw new Mort900Exception(
                    "Дата расчета указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)",
                    "Calc date from payment purpose is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)"
            );
        }
        String contrNum = getStringParam(rowData, "CONTRNUMBER");
        if ((contrNum == null) || (contrNum.isEmpty())) {
            throw new Mort900Exception(
                    "Не указан номер договора",
                    "Conract number is not specified"
            );
        }

        // check data
        Double cpPctMem = getDoubleAmountParam(rowData, "CPPCTMEM");
        Double cpContrAmValue = getDoubleAmountParam(rowData, "CPCONTRAMVALUE");
        Double cpRvltAmValue = getDoubleAmountParam(rowData, "CPRVLTAMVALUE");
        Double cpArcdContrAmValue = getDoubleAmountParam(rowData, "CPACRDCONTRAMVALUE");
        Double cpArcdRvltAmValue = getDoubleAmountParam(rowData, "CPACRDRVLTAMVALUE");
        Double premValue = getDoubleAmountParam(rowData, "PREMVALUE");
        Double cpPCTWMem = getDoubleAmountParam(rowData, "CPPCTWMEM");
        Double rateStart = getDoubleAmountParam(rowData, "RATESTART");
        Double rateCalc = getDoubleAmountParam(rowData, "RATECALC");
        Long contrCurrencyId = getLongParam(rowData, "CONTRCURRENCYID");
        Long condInvCurrencyId = getLongParam(rowData, "CONDINVCURRENCYID");
        Long isCondition = getLongParam(rowData, "ISCONDITION");
        Long isToDayCpArcd = getLongParam(rowData, "ISTODAYCPACRD");
        Double barrierValue = getDoubleAmountParam(rowData, "BARRIERVALUE");
        Double multiMemValue = getDoubleAmountParam(rowData, "MULTIMEMVALUE");
        String condNote = getStringParam(rowData, "CONDNOTE");
        String condCode = getStringParam(rowData, "CONDCODE");

        // check exist
        Map<String, Object> findParams = new HashMap<String, Object>();
        findParams.put(RETURN_AS_HASH_MAP, true);
        findParams.put("CALCDATE", calcDate);
        findParams.put("CONTRNUMBER", contrNum);
        Map<String, Object> resultFindData;
        try {
            resultFindData = this.callService(THIS_SERVICE_NAME, "dsB2BInvestCouponBrowseListByParam", findParams, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage(),
                    "Find error: " + ex.getMessage(),
                    ex
            );
        }

        String methodSave = "dsB2BInvestCouponCreate";
        Map<String, Object> investData = new HashMap<String, Object>();
        if ((resultFindData != null) && (resultFindData.get("INVAMID") != null)) {
            investData.put("INVAMID", resultFindData.get("INVAMID"));
            methodSave = "dsB2BInvestCouponModify";
            typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_UPDATE;
        }
        
        investData.put("CALCDATE", calcDate);
        investData.put("CONTRNUMBER", contrNum);
        investData.put("CPPCTMEM", cpPctMem);
        investData.put("CPCONTRAMVALUE", cpContrAmValue);
        investData.put("CPRVLTAMVALUE", cpRvltAmValue);
        investData.put("CPACRDCONTRAMVALUE", cpArcdContrAmValue);
        investData.put("CPACRDRVLTAMVALUE", cpArcdRvltAmValue);
        investData.put("PREMVALUE", premValue);
        investData.put("CPPCTWMEM", cpPCTWMem);
        investData.put("RATESTART", rateStart);
        investData.put("RATECALC", rateCalc);
        investData.put("CONTRCURRENCYID", contrCurrencyId);
        investData.put("CONDINVCURRENCYID", condInvCurrencyId);
        investData.put("ISCONDITION", isCondition);
        investData.put("ISTODAYCPACRD", isToDayCpArcd);
        investData.put("BARRIERVALUE", barrierValue);
        investData.put("MULTIMEMVALUE", multiMemValue);
        investData.put("CONDNOTE", condNote);
        investData.put("CONDCODE", condCode);
        
        // save data
        Map<String, Object> resultData;
        try {
            resultData = this.callService(THIS_SERVICE_NAME, methodSave, investData, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при сохранении данных: " + ex.getLocalizedMessage(),
                    "Save data error: " + ex.getMessage(),
                    ex
            );
        }

        Map<String, Object> paramTypeData = new HashMap<String, Object>();
        paramTypeData.put("BANKCASHFLOWID", bankCashFlowID);
        paramTypeData.put("TYPEMODIFYSTRING", typeModifyString);
        try {
            Map<String, Object> resultSetTypeStr = this.callService(THIS_SERVICE_NAME, "dsB2B2BCashFlowSetTypeModifyString", paramTypeData, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при сохранении типа изменения строки: " + ex.getLocalizedMessage(),
                    "Save data error: " + ex.getMessage(),
                    ex
            );
        }
        return resultData;
    }

    @WsMethod(requiredParams = {"BANKCASHFLOWLIST"})
    public Map<String, Object> dsB2BLoadInvestCouponMassProcess(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        ExternalService externalService = this.getExternalService();

        List<Map<String, Object>> cashFlows = (List<Map<String, Object>>) params.get("BANKCASHFLOWLIST");
        // Список для доходности
        List<Map<String, Object>> investList = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> cashFlow : cashFlows) {
            try {
                long typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_ADD;
        
                Map<String, Object> rowData = commonProcessData(cashFlow);

                String bankCashFlowID = getStringParam(rowData, "BANKCASHFLOWID");
                
                Date calcDate = (Date) datesParser.parseAnyDate(rowData.get("CALCDATE"), Date.class, "CALCDATE", true);
                if (calcDate == null) {
                    cashFlow.put("ErrorTextRus", "Дата расчета указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)");
                    cashFlow.put("ErrorText", "Calc date from payment purpose is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)");
                    continue;
                }

                String contrNum = getStringParam(rowData, "CONTRNUMBER");
                if ((contrNum == null) || (contrNum.isEmpty())) {
                    cashFlow.put("ErrorTextRus", "Не указан номер договора");
                    cashFlow.put("ErrorText", "Conract number is not specified");
                    continue;
                }

                // check data
                Double cpPctMem = getDoubleAmountParam(rowData, "CPPCTMEM");
                Double cpContrAmValue = getDoubleAmountParam(rowData, "CPCONTRAMVALUE");
                Double cpRvltAmValue = getDoubleAmountParam(rowData, "CPRVLTAMVALUE");
                Double cpArcdContrAmValue = getDoubleAmountParam(rowData, "CPACRDCONTRAMVALUE");
                Double cpArcdRvltAmValue = getDoubleAmountParam(rowData, "CPACRDRVLTAMVALUE");
                Double premValue = getDoubleAmountParam(rowData, "PREMVALUE");
                Double cpPCTWMem = getDoubleAmountParam(rowData, "CPPCTWMEM");
                Double rateStart = getDoubleAmountParam(rowData, "RATESTART");
                Double rateCalc = getDoubleAmountParam(rowData, "RATECALC");
                Long contrCurrencyId = getLongParam(rowData, "CONTRCURRENCYID");
                Long condInvCurrencyId = getLongParam(rowData, "CONDINVCURRENCYID");
                Long isCondition = getLongParam(rowData, "ISCONDITION");
                Long isToDayCpArcd = getLongParam(rowData, "ISTODAYCPACRD");
                Double barrierValue = getDoubleAmountParam(rowData, "BARRIERVALUE");
                Double multiMemValue = getDoubleAmountParam(rowData, "MULTIMEMVALUE");
                String condNote = getStringParam(rowData, "CONDNOTE");
                String condCode = getStringParam(rowData, "CONDCODE");

                // check exist
                Map<String, Object> findParams = new HashMap<String, Object>();
                findParams.put(RETURN_AS_HASH_MAP, true);
                findParams.put("CALCDATE", calcDate);
                findParams.put("CONTRNUMBER", contrNum);
                Map<String, Object> resultFindData;
                try {
                    resultFindData = this.callService(THIS_SERVICE_NAME, "dsB2BInvestCouponBrowseListByParam", findParams, login, password);
                } catch (Exception ex) {
                    cashFlow.put("ErrorTextRus", "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage());
                    cashFlow.put("ErrorText", "Find error: " + ex.getMessage());
                    continue;
                }

                // запись данных в таблицу доходности
                Map<String, Object> investData = new HashMap<String, Object>();
                if ((resultFindData != null) && (resultFindData.get("INVAMID") != null)) {
                    investData.put("INVAMID", resultFindData.get("INVAMID"));
                    typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_UPDATE;
                } else {
                    investData.put("INVAMID", externalService.getNewId("B2B_INVAM"));
                }
                BigDecimal calcDateBD = XMLUtil.convertDateToBigDecimal(calcDate);
                investData.put("DISCRIMINATOR", 2L);
                investData.put("CALCDATE", calcDateBD);
                investData.put("CONTRNUMBER", contrNum);
                investData.put("CPPCTMEM", cpPctMem);
                investData.put("CPCONTRAMVALUE", cpContrAmValue);
                investData.put("CPRVLTAMVALUE", cpRvltAmValue);
                investData.put("CPACRDCONTRAMVALUE", cpArcdContrAmValue);
                investData.put("CPACRDRVLTAMVALUE", cpArcdRvltAmValue);
                investData.put("PREMVALUE", premValue);
                investData.put("CPPCTWMEM", cpPCTWMem);
                investData.put("RATESTART", rateStart);
                investData.put("RATECALC", rateCalc);
                investData.put("CONTRCURRENCYID", contrCurrencyId);
                investData.put("CONDINVCURRENCYID", condInvCurrencyId);
                investData.put("ISCONDITION", isCondition);
                investData.put("ISTODAYCPACRD", isToDayCpArcd);
                investData.put("BARRIERVALUE", barrierValue);
                investData.put("MULTIMEMVALUE", multiMemValue);
                investData.put("CONDNOTE", condNote);
                investData.put("CONDCODE", condCode);
                investList.add(investData);
                
                Map<String, Object> paramTypeData = new HashMap<String, Object>();
                paramTypeData.put("BANKCASHFLOWID", bankCashFlowID);
                paramTypeData.put("TYPEMODIFYSTRING", typeModifyString);
                try {
                    Map<String, Object> resultSetTypeStr = this.callService(THIS_SERVICE_NAME, "dsB2B2BCashFlowSetTypeModifyString", paramTypeData, login, password);
                } catch (Exception ex) {
                    throw new Mort900Exception(
                            "Возникла ошибка при сохранении типа изменения строки: " + ex.getLocalizedMessage(),
                            "Save data error: " + ex.getMessage(),
                            ex
                    );
                }
            } catch (Exception ex) {
                cashFlow.put("ErrorTextRus", "Возникла ошибка: " + ex.getLocalizedMessage());
                cashFlow.put("ErrorText", "Find error: " + ex.getMessage());
            }
        }

        // сохранение данных по доходности
        if (investList.size() > 0) {
            try {
                Map<String, Object> massParams = new HashMap<String, Object>();
                massParams.put("rows", investList);
                massParams.put("totalCount", investList.size());
                doMassSaveInvestCouponQuery(massParams);
            } catch (Exception ex) {
                throw new Mort900Exception(
                        "Возникла ошибка при сохранении данных: " + ex.getLocalizedMessage(),
                        "Save error: " + ex.getMessage(), ex
                );
            }
        }

        Map<String, Object> resultData = new HashMap<String, Object>();
        resultData.put("BANKCASHFLOWLIST", cashFlows);
        return resultData;
    }

    private List<Map<String, Object>> doMassSaveInvestCouponQuery(Map<String, Object> params) throws Exception {

        int[] sqlResult = this.insertQuery("dsB2BInvestCouponMassSave", params);

        return (List<Map<String, Object>>) params.get("rows");
    }
    
}
