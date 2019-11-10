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
 * @author aklunok
 */
@BOName("LoadInvestDIDCustom")
public class LoadInvestDIDCustomFacade extends Mort900BaseFacade {

    private static DatesParser datesParser;

    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

    public LoadInvestDIDCustomFacade() {
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

        if (processParams.containsKey("BANKCASHFLOWID")) {
            rowData.put("BANKCASHFLOWID", processParams.get("BANKCASHFLOWID"));
        } else {
            if (bankCashFlow.containsKey("BANKCASHFLOWID")) {
                rowData.put("BANKCASHFLOWID", bankCashFlow.get("BANKCASHFLOWID"));
            }
        }
        return rowData;
    }

    @WsMethod(requiredParams = {"BANKPURPOSEDETAILLIST", "BANKSTATETEMPLATEPURPOSEDETAILLIST", "BANKCASHFLOW"})
    public Map<String, Object> dsB2BLoadInvestDIDProcess(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        long typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_ADD;
        Map<String, Object> rowData = commonProcessData(params);

        String bankCashFlowID = getStringParam(rowData, "BANKCASHFLOWID");
        String contrNum = getStringParam(rowData, "CONTRNUMBER");
        if ((contrNum == null) || (contrNum.isEmpty())) {
            throw new Mort900Exception(
                    "Не указан номер договора",
                    "Conract number is not specified"
            );
        }

        Long didYear = getLongParam(rowData, "DIDYEAR");
        if (didYear == null) {
            throw new Mort900Exception(
                    "Не указан год ДИД",
                    "Year DID is not specified"
            );
        }

        // check data
        String sContrStartDate = getStringParam(rowData, "CONTRSTARTDATE");
        Date contrStartDate = null;
        if ((sContrStartDate != null) && (!sContrStartDate.isEmpty())) {
            contrStartDate = (Date) datesParser.parseAnyDate(sContrStartDate, Date.class, "CONTRSTARTDATE", true);
            if (contrStartDate == null) {
                throw new Mort900Exception(
                        "Дата начала действия договора указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)",
                        "Contract start date is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)"
                );
            }
        }

        String progName = getStringParam(rowData, "PROGNAME");
        String insuredName = getStringParam(rowData, "INSUREDNAME");
        // Long contrCurrencyId = getLongParam(rowData, "CONTRCURRENCYID");
        Long termYearCount = getLongParam(rowData, "TERMYEARCOUNT");
        Long payVar = getLongParam(rowData, "PAYVAR");
        Double insAmValue = getDoubleAmountParam(rowData, "INSAMVALUE");
        Double premValue = getDoubleAmountParam(rowData, "PREMVALUE");
        Double premTotalValue = getDoubleAmountParam(rowData, "PREMTOTALVALUE");
        Double didContrValue = getDoubleAmountParam(rowData, "DIDCONTRVALUE");
        Double didPaymentValue = getDoubleAmountParam(rowData, "DIDPAYMENTVALUE");
        Double indValue = getDoubleAmountParam(rowData, "INDVALUE");
        Double didValue = getDoubleAmountParam(rowData, "DIDVALUE");
        Double rateValue = getDoubleAmountParam(rowData, "RATEVALUE");

        // check exist
        Map<String, Object> findParams = new HashMap<String, Object>();
        findParams.put(RETURN_AS_HASH_MAP, true);
        findParams.put("CONTRNUMBER", contrNum);
        findParams.put("DIDYEAR", didYear);
        Map<String, Object> resultFindData;
        try {
            resultFindData = this.callService(THIS_SERVICE_NAME, "dsB2BInvestDIDBrowseListByParam", findParams, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage(),
                    "Find error: " + ex.getMessage(),
                    ex
            );
        }

        String methodSave = "dsB2BInvestDIDCreate";
        Map<String, Object> investData = new HashMap<String, Object>();
        if ((resultFindData != null) && (resultFindData.get("INVAMID") != null)) {
            investData.put("INVAMID", resultFindData.get("INVAMID"));
            methodSave = "dsB2BInvestDIDModify";
            typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_UPDATE;
        }

        investData.put("CONTRNUMBER", contrNum);
        investData.put("PROGNAME", progName);
        investData.put("INSUREDNAME", insuredName);
        investData.put("CONTRSTARTDATE", contrStartDate);
        //investData.put("CONTRCURRENCYID", contrCurrencyId);
        investData.put("TERMYEARCOUNT", termYearCount);
        investData.put("PAYVAR", payVar);
        investData.put("INSAMVALUE", insAmValue);
        investData.put("PREMVALUE", premValue);
        investData.put("PREMTOTALVALUE", premTotalValue);
        investData.put("DIDCONTRVALUE", didContrValue);
        investData.put("DIDPAYMENTVALUE", didPaymentValue);
        investData.put("INDVALUE", indValue);
        investData.put("DIDVALUE", didValue);
        investData.put("DIDYEAR", didYear);
        investData.put("RATEVALUE", rateValue);

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
    public Map<String, Object> dsB2BLoadInvestDIDMassProcess(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        ExternalService externalService = this.getExternalService();
        Long createDateBD = XMLUtil.convertDateToBigDecimal(new Date()).longValue();

        List<Map<String, Object>> cashFlows = (List<Map<String, Object>>) params.get("BANKCASHFLOWLIST");
        // Список для доходности
        List<Map<String, Object>> investList = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> cashFlow : cashFlows) {
            try {
                long typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_ADD;

                Map<String, Object> rowData = commonProcessData(cashFlow);

                String bankCashFlowID = getStringParam(rowData, "BANKCASHFLOWID");

                String contrNum = getStringParam(rowData, "CONTRNUMBER");
                if ((contrNum == null) || (contrNum.isEmpty())) {
                    cashFlow.put("ErrorTextRus", "Не указан номер договора");
                    cashFlow.put("ErrorText", "Conract number is not specified");
                    continue;
                }

                Long didYear = getLongParam(rowData, "DIDYEAR");
                if (didYear == null) {
                    cashFlow.put("ErrorTextRus", "Не указан год ДИД");
                    cashFlow.put("ErrorText", "Year DID is not specified");
                    continue;
                }

                // check data
                String sContrStartDate = getStringParam(rowData, "CONTRSTARTDATE");
                Date contrStartDate = null;
                if ((sContrStartDate != null) && (!sContrStartDate.isEmpty())) {
                    contrStartDate = (Date) datesParser.parseAnyDate(sContrStartDate, Date.class, "CONTRSTARTDATE", true);
                    if (contrStartDate == null) {
                        cashFlow.put("ErrorTextRus", "Дата начала действия договора указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)");
                        cashFlow.put("ErrorText", "Contract start date is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)");
                        continue;
                    }
                }

                String progName = getStringParam(rowData, "PROGNAME");
                String insuredName = getStringParam(rowData, "INSUREDNAME");
                // Long contrCurrencyId = getLongParam(rowData, "CONTRCURRENCYID");
                Long termYearCount = getLongParam(rowData, "TERMYEARCOUNT");
                Long payVar = getLongParam(rowData, "PAYVAR");
                Double insAmValue = getDoubleAmountParam(rowData, "INSAMVALUE");
                Double premValue = getDoubleAmountParam(rowData, "PREMVALUE");
                Double premTotalValue = getDoubleAmountParam(rowData, "PREMTOTALVALUE");
                Double didContrValue = getDoubleAmountParam(rowData, "DIDCONTRVALUE");
                Double didPaymentValue = getDoubleAmountParam(rowData, "DIDPAYMENTVALUE");
                Double indValue = getDoubleAmountParam(rowData, "INDVALUE");
                Double didValue = getDoubleAmountParam(rowData, "DIDVALUE");
                Double rateValue = getDoubleAmountParam(rowData, "RATEVALUE");

                // check exist
                Map<String, Object> findParams = new HashMap<String, Object>();
                findParams.put(RETURN_AS_HASH_MAP, true);
                findParams.put("CONTRNUMBER", contrNum);
                findParams.put("DIDYEAR", didYear);
                Map<String, Object> resultFindData;
                try {
                    resultFindData = this.callService(THIS_SERVICE_NAME, "dsB2BInvestDIDBrowseListByParam", findParams, login, password);
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
                BigDecimal contrStartDateBD = null;
                if (contrStartDate != null) {
                    contrStartDateBD = XMLUtil.convertDateToBigDecimal(contrStartDate);
                }
                investData.put("DISCRIMINATOR", 3L);
                investData.put("CREATEDATE", createDateBD);
                investData.put("CONTRNUMBER", contrNum);
                investData.put("PROGNAME", progName);
                investData.put("INSUREDNAME", insuredName);
                investData.put("CONTRSTARTDATE", contrStartDateBD);
                //investData.put("CONTRCURRENCYID", contrCurrencyId);
                investData.put("TERMYEARCOUNT", termYearCount);
                investData.put("PAYVAR", payVar);
                investData.put("INSAMVALUE", insAmValue);
                investData.put("PREMVALUE", premValue);
                investData.put("PREMTOTALVALUE", premTotalValue);
                investData.put("DIDCONTRVALUE", didContrValue);
                investData.put("DIDPAYMENTVALUE", didPaymentValue);
                investData.put("INDVALUE", indValue);
                investData.put("DIDVALUE", didValue);
                investData.put("DIDYEAR", didYear);
                investData.put("RATEVALUE", rateValue);
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
                doMassSaveInvestDIDQuery(massParams);
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

    private List<Map<String, Object>> doMassSaveInvestDIDQuery(Map<String, Object> params) throws Exception {

        int[] sqlResult = this.insertQuery("dsB2BInvestDIDMassSave", params);

        return (List<Map<String, Object>>) params.get("rows");
    }

}
