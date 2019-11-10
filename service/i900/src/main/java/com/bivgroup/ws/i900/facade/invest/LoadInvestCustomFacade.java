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
@BOName("LoadInvestCustom")
public class LoadInvestCustomFacade extends Mort900BaseFacade {

    private static DatesParser datesParser;

    private final boolean IS_VERBOSE_LOGGING = logger.isDebugEnabled();

    public LoadInvestCustomFacade() {
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
    public Map<String, Object> dsB2BLoadInvestProcess(Map<String, Object> params) throws Exception {

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
        /*        
        GregorianCalendar calcDateGC = new GregorianCalendar();
        calcDateGC.setTime(calcDate);
        calcDateGC.set(Calendar.HOUR_OF_DAY, 0);
        calcDateGC.set(Calendar.MINUTE, 0);
        calcDateGC.set(Calendar.SECOND, 0);
        calcDateGC.set(Calendar.MILLISECOND, 0);
         */
        String contrNum = getStringParam(rowData, "CONTRNUMBER");
        if ((contrNum == null) || (contrNum.isEmpty())) {
            throw new Mort900Exception(
                    "Не указан номер договора",
                    "Conract number is not specified"
            );
        }

        // check data
        Double insAmValue = getDoubleAmountParam(rowData, "INSAMVALUE");
        Double indValue = getDoubleAmountParam(rowData, "INDVALUE");
        Double invValue = getDoubleAmountParam(rowData, "INVVALUE");
        Double baValue = getDoubleAmountParam(rowData, "BAVALUE");
        Double redempValue = getDoubleAmountParam(rowData, "REDEMPVALUE");
        Double didValue = getDoubleAmountParam(rowData, "DIDVALUE");
        Double insamiddValue = getDoubleAmountParam(rowData, "INSAMIDDVALUE");
        Double iddValue = getDoubleAmountParam(rowData, "IDDVALUE");
        Double coefIntValue = getDoubleAmountParam(rowData, "COEFINTVALUE");

        // check exist
        Map<String, Object> findParams = new HashMap<String, Object>();
        findParams.put(RETURN_AS_HASH_MAP, true);
        findParams.put("CALCDATE", calcDate);
        findParams.put("CONTRNUMBER", contrNum);
        Map<String, Object> resultFindData;
        try {
            resultFindData = this.callService(THIS_SERVICE_NAME, "dsB2BInvestBrowseListByParam", findParams, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage(),
                    "Find error: " + ex.getMessage(),
                    ex
            );
        }
        // поиск последней доходности потому как данные могут грузиться параллельно
        Map<String, Object> findInvParams = new HashMap<String, Object>();
        findInvParams.put(RETURN_AS_HASH_MAP, true);
        findInvParams.put("CONTRNUMBER", contrNum);
        Map<String, Object> resultInvestData;
        try {
            resultInvestData = this.callService(THIS_SERVICE_NAME, "dsB2BInvestMaxDateBrowseListByParam", findInvParams, login, password);
        } catch (Exception ex) {
            throw new Mort900Exception(
                    "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage(),
                    "Find error: " + ex.getMessage(),
                    ex
            );
        }

        if (resultInvestData.get("CALCDATE") != null) {
            Date calcMaxDate = getDateParam(resultInvestData.get("CALCDATE"));
            if (calcMaxDate.before(calcDate)) {
                // поиск договора для обновления данных по доходности
                Map<String, Object> findContrParams = new HashMap<String, Object>();
                findContrParams.put(RETURN_AS_HASH_MAP, true);
                findContrParams.put("CONTRNUMBER", contrNum);
                findContrParams.put("PRODSYSNAMELIST", "'SBI_ILI0', 'RB-ILI0'");
                Map<String, Object> resultContrData;
                try {
                    resultContrData = this.callService(THIS_SERVICE_NAME, "dsB2BContractBrowseListByInvestParam", findContrParams, login, password);
                } catch (Exception ex) {
                    throw new Mort900Exception(
                            "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage(),
                            "Find error: " + ex.getMessage(),
                            ex
                    );
                }
                if (resultContrData.get("CONTRID") == null) {
                    throw new Mort900Exception(
                            "Договор не найден: " + contrNum,
                            "Contract not found: " + contrNum
                    );
                }
                Long contrId = getLongParam(resultContrData.get("CONTRID"));
                Map<String, Object> findContrExtParams = new HashMap<String, Object>();
                findContrExtParams.put(RETURN_AS_HASH_MAP, true);
                findContrExtParams.put("CONTRID", contrId);
                // обновление ивестиционных данных по договору
                Map<String, Object> resultContrExtData;
                try {
                    resultContrExtData = this.callService(THIS_SERVICE_NAME, "dsB2BContractExtensionBrowseListByParam", findContrExtParams, login, password);
                } catch (Exception ex) {
                    throw new Mort900Exception(
                            "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage(),
                            "Find error: " + ex.getMessage(),
                            ex
                    );
                }
                if (resultContrExtData.get("CONTREXTID") == null) {
                    throw new Mort900Exception(
                            "Договор (Расширенные атрибуты) не найден: " + contrNum,
                            "Contract (Extended attributes) not found: " + contrNum
                    );
                }
                Long contrExtId = getLongParam(resultContrExtData.get("CONTREXTID"));
                Map<String, Object> updateContrExtParams = new HashMap<String, Object>();
                updateContrExtParams.put(RETURN_AS_HASH_MAP, true);
                updateContrExtParams.put("CONTREXTID", contrExtId);
                updateContrExtParams.put("DOUBLEFIELD36", insAmValue);
                updateContrExtParams.put("DOUBLEFIELD37", indValue);
                Map<String, Object> resultUpdateExtData;
                try {
                    resultUpdateExtData = this.callService(THIS_SERVICE_NAME, "dsB2BContractExtensionModify", updateContrExtParams, login, password);
                } catch (Exception ex) {
                    throw new Mort900Exception(
                            "Возникла ошибка при сохранении данных: " + ex.getLocalizedMessage(),
                            "Save data error: " + ex.getMessage(),
                            ex
                    );
                }
            }
        }
        // запись данных в таблицу доходности
        String methodSave = "dsB2BInvestCreate";
        Map<String, Object> investData = new HashMap<String, Object>();
        if ((resultFindData != null) && (resultFindData.get("INVAMID") != null)) {
            investData.put("INVAMID", resultFindData.get("INVAMID"));
            methodSave = "dsB2BInvestModify";
            typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_UPDATE;
        }
        investData.put("CALCDATE", calcDate);
        investData.put("CONTRNUMBER", contrNum);
        investData.put("INSAMVALUE", insAmValue);
        investData.put("INDVALUE", indValue);
        investData.put("INVVALUE", invValue);
        investData.put("BAVALUE", baValue);
        investData.put("REDEMPVALUE", redempValue);
        investData.put("DIDVALUE", didValue);
        investData.put("INSAMIDDVALUE", insamiddValue);
        investData.put("IDDVALUE", iddValue);
        investData.put("COEFINTVALUE", coefIntValue);

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
    @WsMethod(requiredParams = {"CONTRNUMBER"})
    public Map<String, Object> dsB2BContractBrowseListByInvestParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractBrowseListByInvestParam", "dsB2BContractBrowseListByInvestParamCount", params);
        return result;
    }

    @WsMethod(requiredParams = {"BANKCASHFLOWLIST"})
    public Map<String, Object> dsB2BLoadInvestMassProcess(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        ExternalService externalService = this.getExternalService();

        List<Map<String, Object>> cashFlows = (List<Map<String, Object>>) params.get("BANKCASHFLOWLIST");
        // Список договоров кэш по номеру договора
        Map<String, Object> contractMap = new HashMap();
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
                Double insAmValue = getDoubleAmountParam(rowData, "INSAMVALUE");
                Double indValue = getDoubleAmountParam(rowData, "INDVALUE");
                Double invValue = getDoubleAmountParam(rowData, "INVVALUE");
                Double baValue = getDoubleAmountParam(rowData, "BAVALUE");
                Double redempValue = getDoubleAmountParam(rowData, "REDEMPVALUE");
                Double didValue = getDoubleAmountParam(rowData, "DIDVALUE");
                Double insamiddValue = getDoubleAmountParam(rowData, "INSAMIDDVALUE");
                Double iddValue = getDoubleAmountParam(rowData, "IDDVALUE");
                Double coefIntValue = getDoubleAmountParam(rowData, "COEFINTVALUE");

                // check exist
                Map<String, Object> findParams = new HashMap<String, Object>();
                findParams.put(RETURN_AS_HASH_MAP, true);
                findParams.put("CALCDATE", calcDate);
                findParams.put("CONTRNUMBER", contrNum);
                Map<String, Object> resultFindData;
                try {
                    resultFindData = this.callService(THIS_SERVICE_NAME, "dsB2BInvestBrowseListByParam", findParams, login, password);
                } catch (Exception ex) {
                    cashFlow.put("ErrorTextRus", "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage());
                    cashFlow.put("ErrorText", "Find error: " + ex.getMessage());
                    continue;
                }

                // обработка последней доходности для договора
                processContractInvest(cashFlow, contractMap, contrNum, calcDate, insAmValue, indValue, login, password);

                // запись данных в таблицу доходности
                Map<String, Object> investData = new HashMap<String, Object>();
                if ((resultFindData != null) && (resultFindData.get("INVAMID") != null)) {
                    investData.put("INVAMID", resultFindData.get("INVAMID"));
                    typeModifyString = BANKCASHFLOW_TYPEMODIFYSTRING_UPDATE;
                } else {
                    investData.put("INVAMID", externalService.getNewId("B2B_INVAM"));
                }
                BigDecimal calcDateBD = XMLUtil.convertDateToBigDecimal(calcDate);
                investData.put("DISCRIMINATOR", 1L);
                investData.put("CALCDATE", calcDateBD);
                investData.put("CONTRNUMBER", contrNum);
                investData.put("INSAMVALUE", insAmValue);
                investData.put("INDVALUE", indValue);
                investData.put("INVVALUE", invValue);
                investData.put("BAVALUE", baValue);
                investData.put("REDEMPVALUE", redempValue);
                investData.put("DIDVALUE", didValue);
                investData.put("INSAMIDDVALUE", insamiddValue);
                investData.put("IDDVALUE", iddValue);
                investData.put("COEFINTVALUE", coefIntValue);
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

        // сохранение данных по договору
        if (!contractMap.isEmpty()) {
            // Список для расширенных атрибутов
            List<Map<String, Object>> contractExtList = new ArrayList<Map<String, Object>>();
            for (Map.Entry<String, Object> entry : contractMap.entrySet()) {
                Map<String, Object> rowMap = (Map<String, Object>) entry.getValue();
                contractExtList.add(rowMap);
            }
            if (contractExtList.size() > 0) {
                try {
                    Map<String, Object> massParams = new HashMap<String, Object>();
                    massParams.put("rows", contractExtList);
                    massParams.put("totalCount", contractExtList.size());
                    doMassSaveContractExtInvestQuery(massParams);
                } catch (Exception ex) {
                    throw new Mort900Exception(
                            "Возникла ошибка при сохранении данных: " + ex.getLocalizedMessage(),
                            "Save error: " + ex.getMessage(), ex
                    );
                }
            }
        }
        // сохранение данных по доходности
        if (investList.size() > 0) {
            try {
                Map<String, Object> massParams = new HashMap<String, Object>();
                massParams.put("rows", investList);
                massParams.put("totalCount", investList.size());
                doMassSaveInvestQuery(massParams);
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

    private List<Map<String, Object>> doMassSaveInvestQuery(Map<String, Object> params) throws Exception {

        int[] sqlResult = this.insertQuery("dsB2BInvestMassSave", params);

        return (List<Map<String, Object>>) params.get("rows");
    }

    private List<Map<String, Object>> doMassSaveContractExtInvestQuery(Map<String, Object> params) throws Exception {

        int[] sqlResult = this.insertQuery("dsB2BContractExtInvestMassSave", params);

        return (List<Map<String, Object>>) params.get("rows");
    }

    private void processContractInvest(Map<String, Object> cashFlow, Map<String, Object> contractMap,
            String contrNum, Date calcDate, Double insAmValue, Double indValue,
            String login, String password) {
        Date contractCalcDate = null;
        // поиск договора
        Map<String, Object> contrMap = (Map<String, Object>) contractMap.get(contrNum);
        if (contrMap != null) {
            contractCalcDate = (Date) contrMap.get("CALCDATE");
            // у договора и так уже последняя дата доходности обновлять ничего не надо
            if (calcDate.compareTo(contractCalcDate) <= 0) {
                return;
            } else {
                // обновление доходности договора
                contrMap.put("CALCDATE", calcDate);
                contrMap.put("DOUBLEFIELD36", insAmValue);
                contrMap.put("DOUBLEFIELD37", indValue);
                return;
            }
        }
        // Поиск последней доходности
        Map<String, Object> findInvParams = new HashMap<String, Object>();
        findInvParams.put(RETURN_AS_HASH_MAP, true);
        findInvParams.put("CONTRNUMBER", contrNum);
        Map<String, Object> resultInvestData;
        try {
            resultInvestData = this.callService(THIS_SERVICE_NAME, "dsB2BInvestMaxDateBrowseListByParam", findInvParams, login, password);
        } catch (Exception ex) {
            cashFlow.put("ErrorTextRus", "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage());
            cashFlow.put("ErrorText", "Find error: " + ex.getMessage());
            return;
        }

        Date calcMaxDate = getDateParam(resultInvestData.get("CALCDATE"));
        if (calcMaxDate.before(calcDate)) {
            // поиск договора для обновления данных по доходности
            Map<String, Object> findContrParams = new HashMap<String, Object>();
            findContrParams.put(RETURN_AS_HASH_MAP, true);
            findContrParams.put("CONTRNUMBER", contrNum);
            findContrParams.put("PRODSYSNAMELIST", "'SBI_ILI0', 'RB-ILI0'");
            Map<String, Object> resultContrData;
            try {
                resultContrData = this.callService(THIS_SERVICE_NAME, "dsB2BContractBrowseListByInvestParam", findContrParams, login, password);
            } catch (Exception ex) {
                cashFlow.put("ErrorTextRus", "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage());
                cashFlow.put("ErrorText", "Find error: " + ex.getMessage());
                return;
            }
            if (resultContrData.get("CONTRID") == null) {
                cashFlow.put("ErrorTextRus", "Договор не найден: " + contrNum);
                cashFlow.put("ErrorText", "Contract not found: " + contrNum);
                return;
            }
            Long contrId = getLongParam(resultContrData.get("CONTRID"));
            Map<String, Object> findContrExtParams = new HashMap<String, Object>();
            findContrExtParams.put(RETURN_AS_HASH_MAP, true);
            findContrExtParams.put("CONTRID", contrId);
            // обновление ивестиционных данных по договору
            Map<String, Object> resultContrExtData;
            try {
                resultContrExtData = this.callService(THIS_SERVICE_NAME, "dsB2BContractExtensionBrowseListByParam", findContrExtParams, login, password);
            } catch (Exception ex) {
                cashFlow.put("ErrorTextRus", "Возникла ошибка при поиске данных: " + ex.getLocalizedMessage());
                cashFlow.put("ErrorText", "Find error: " + ex.getMessage());
                return;
            }
            if (resultContrExtData.get("CONTREXTID") == null) {
                cashFlow.put("ErrorTextRus", "Договор (Расширенные атрибуты) не найден: " + contrNum);
                cashFlow.put("ErrorText", "Contract (Extended attributes) not found: " + contrNum);
                return;
            }
            Long contrExtId = getLongParam(resultContrExtData.get("CONTREXTID"));
            Map<String, Object> updateContrExtParams = new HashMap<String, Object>();
            updateContrExtParams.put("CALCDATE", calcDate);
            updateContrExtParams.put("CONTREXTID", contrExtId);
            updateContrExtParams.put("DOUBLEFIELD36", insAmValue);
            updateContrExtParams.put("DOUBLEFIELD37", indValue);
            contractMap.put(contrNum, updateContrExtParams);
        }
    }
}
