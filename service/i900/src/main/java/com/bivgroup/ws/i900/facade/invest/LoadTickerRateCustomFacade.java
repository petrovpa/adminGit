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
@BOName("LoadTickerRateCustom")
public class LoadTickerRateCustomFacade extends Mort900BaseFacade {

    private static DatesParser datesParser;

    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

    public LoadTickerRateCustomFacade() {
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
    public Map<String, Object> dsB2BLoadTickerRateProcess(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        long typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_ADD;
        Map<String, Object> rowData = commonProcessData(params);

        String bankCashFlowID = getStringParam(rowData, "BANKCASHFLOWID");
        String tickerCode = getStringParam(rowData, "TICKERCODE");
        if ((tickerCode == null) || (tickerCode.isEmpty())) {
            throw new Mort900Exception(
                    "Не указан код базового актива",
                    "Code base active is not specified"
            );
        }

        Date trDate = (Date) datesParser.parseAnyDate(rowData.get("TRDATE"), Date.class, "TRDATE", true);
        if (trDate == null) {
            throw new Mort900Exception(
                    "Дата указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)",
                    "Date from payment purpose is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)"
            );
        }

        // check data
        Double rateValue = getDoubleAmountParam(rowData, "RATEVALUE");
        String tickerName = getStringParam(rowData, "TICKERNAME");

        // check exist
        Map<String, Object> findParams = new HashMap<String, Object>();
        findParams.put(RETURN_AS_HASH_MAP, true);
        findParams.put("TICKERCODE", tickerCode);
        findParams.put("TRDATE", trDate);
        Map<String, Object> resultFindData;
        try {
            resultFindData = this.callService(THIS_SERVICE_NAME, "dsB2BTickerRateBrowseListByParam", findParams, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage(),
                    "Find error: " + ex.getMessage(),
                    ex
            );
        }

        String methodSave = "dsB2BTickerRateCreate";
        Map<String, Object> investData = new HashMap<String, Object>();
        if ((resultFindData != null) && (resultFindData.get("INVTICKERRATEID") != null)) {
            investData.put("INVTICKERRATEID", resultFindData.get("INVTICKERRATEID"));
            methodSave = "dsB2BTickerRateModify";
            typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_UPDATE;
        }

        investData.put("TICKERCODE", tickerCode);
        investData.put("TRDATE", trDate);
        investData.put("RATEVALUE", rateValue);
        investData.put("TICKERCODE", tickerCode);
        investData.put("TICKERNAME", tickerName);

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
    public Map<String, Object> dsB2BLoadTickerRateMassProcess(Map<String, Object> params) throws Exception {
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
                
                String tickerCode = getStringParam(rowData, "TICKERCODE");
                if ((tickerCode == null) || (tickerCode.isEmpty())) {
                    cashFlow.put("ErrorTextRus", "Не указан код базового актива");
                    cashFlow.put("ErrorText", "Code base active is not specified");
                    continue;
                }

                Date trDate = (Date) datesParser.parseAnyDate(rowData.get("TRDATE"), Date.class, "TRDATE", true);
                if (trDate == null) {
                    cashFlow.put("ErrorTextRus", "Дата указана в неподдерживаемом формате (требуется в виде ДД.ММ.ГГГГ или ГГГГ/ММ/ДД)");
                    cashFlow.put("ErrorText", "Date is in unsupported date format (requiried dd.MM.yyyy or yyyy/MM/dd)");
                    continue;
                }

                // check data
                Double rateValue = getDoubleAmountParam(rowData, "RATEVALUE");
                String tickerName = getStringParam(rowData, "TICKERNAME");

                // check exist
                Map<String, Object> findParams = new HashMap<String, Object>();
                findParams.put(RETURN_AS_HASH_MAP, true);
                findParams.put("TICKERCODE", tickerCode);
                findParams.put("TRDATE", trDate);
                Map<String, Object> resultFindData;
                try {
                    resultFindData = this.callService(THIS_SERVICE_NAME, "dsB2BTickerRateBrowseListByParam", findParams, login, password);
                } catch (Exception ex) {
                    cashFlow.put("ErrorTextRus", "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage());
                    cashFlow.put("ErrorText", "Find error: " + ex.getMessage());
                    continue;
                }

                // запись данных в таблицу доходности
                Map<String, Object> investData = new HashMap<String, Object>();
                if ((resultFindData != null) && (resultFindData.get("INVAMID") != null)) {
                    investData.put("INVTICKERRATEID", resultFindData.get("INVAMID"));
                    typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_UPDATE;
                } else {
                    investData.put("INVTICKERRATEID", externalService.getNewId("B2B_INVTICKERRATE"));
                }
                BigDecimal trDateBD = XMLUtil.convertDateToBigDecimal(trDate);
                investData.put("TICKERCODE", tickerCode);
                investData.put("TRDATE", trDateBD);
                investData.put("RATEVALUE", rateValue);
                investData.put("TICKERCODE", tickerCode);
                investData.put("TICKERNAME", tickerName);
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
                doMassSaveTickerRateQuery(massParams);
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

    private List<Map<String, Object>> doMassSaveTickerRateQuery(Map<String, Object> params) throws Exception {

        int[] sqlResult = this.insertQuery("dsB2BTickerRateMassSave", params);

        return (List<Map<String, Object>>) params.get("rows");
    }
    
}
