package com.bivgroup.services.b2bposws.facade.pos.pay;

import com.bivgroup.services.b2bposws.facade.pos.declaration.B2BDeclarationBaseFacade;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BLkPayPrint")
public class B2BLkPayPrintFacade extends B2BDeclarationBaseFacade {

    private static final String DEFAULT_CLAIM_PRINT_ERROR_MSG = "Не удалось сформировать заявление на изменение условий страхования!";
    private static final String CONTRACT_ID_PARAMNAME = "contractId";
    private static final String ACCOUNT_PARAM_NAME = "ACCOUNT";
    private static final Map<String, String> currencyMap;

    private final Logger logger = Logger.getLogger(this.getClass());

    static {
        currencyMap = new HashMap<>();
        currencyMap.put("3", "EUR");
        currencyMap.put("1", "RUB");
        currencyMap.put("2", "USD");
        // не поддерживаются.
        currencyMap.put("4", "CHF");
        currencyMap.put("5", "RUB_AND_USD");
    }

    @WsMethod(requiredParams = {CONTRACT_ID_PARAMNAME, CLIENT_PROFILE_ID_PARAMNAME})
    public Map<String, Object> dsB2BPayPrintDataProvider(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        String error = "";

        Map<String, Object> reportData = new HashMap<>();

        Long clientProfileId = getLongParam(params, CLIENT_PROFILE_ID_PARAMNAME);
        Map<String, Object> clientProfile = dctFindById(CLIENT_PROFILE_ENTITY_NAME, clientProfileId);
        Map<String, Object> client = getMapParam(clientProfile, "clientId_EN");

        Map<String, Object> applicant = new HashMap<>();
        Map<String, Object> insurerMap = new HashMap<>();
        if (clientProfileId == null) {
            error = "Не удалось получить данные профиля клиента, сохраняющего заявление на изменение условий страхования!";
        } else {
            applicant = makeContragentFromClient(client);
            // фамилия заявителя
            insurerMap.put("LASTNAME", applicant.get("surname"));
            // имя заявителя
            insurerMap.put("FIRSTNAME", applicant.get("name"));
            // отчество заявителя
            insurerMap.put("MIDDLENAME", applicant.get("patronymic"));
        }

        List<Map<String, Object>> addressList = new ArrayList<>();
        if (error.isEmpty()) {
            addressList = getOrCreateListParam(applicant, "addresses");
            addressList = addressList.stream().filter(new Predicate<Map<String, Object>>() {
                @Override
                public boolean test(Map<String, Object> stringObjectMap) {
                    String itemType = getStringParam(stringObjectMap, "typeId");
                    return "1003".equals(itemType);
                }
            }).collect(Collectors.toList());
        }

        if (error.isEmpty()) {
            if (!addressList.isEmpty()) {
                insurerMap.put("REGADDR", getStringParam(addressList.get(0), "address"));
            }
            reportData.put("INSURERMAP", insurerMap);
        }

        Long contractId = getLongParam(params, CONTRACT_ID_PARAMNAME);
        Map<String, Object> contractParams = new HashMap<>();
        contractParams.put("CONTRID", contractId);
        contractParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> contract = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParam", contractParams, login, password);
        if (!isCallResultOKAndContainsLongValue(contract, "CONTRID", contractId)) {
            error = "Не удалось загрузить сведения договора страхования, указанного в заявлении на изменение условий страхования!";
        }

        Long contractProdVerId = null;
        Long prodProgId = null;
        String premCurrency = "";

        if (error.isEmpty()) {
            contractProdVerId = getLongParamLogged(contract, "PRODVERID");
            prodProgId = getLongParamLogged(contract, "PRODPROGID");
            String contractSeries = getStringParamLogged(contract, "CONTRPOLSER");
            String contractNumber = getStringParamLogged(contract, "CONTRPOLNUM");
            String contractFullNumber = getStringParamLogged(contract, "CONTRNUMBER");
            if (!contractNumber.isEmpty() && contractNumber.equals(contractFullNumber)) {
                // CONTRPOLNUM и CONTRNUMBER содержат одинаковое значение - договор из старой версии интеграции и следует вычислить номер по полной строке
                if (contractNumber.startsWith(contractSeries)) {
                    String contractNumberReal = contractNumber.substring(contractSeries.length()).replaceAll("№", "").trim();
                    logger.debug(String.format(
                            "Real contract number '%s' was resolved from full contract number '%s' considering series '%s'.",
                            contractNumberReal, contractNumber, contractSeries
                    ));
                    contractNumber = contractNumberReal;
                }
            }
            reportData.put("CONTRPOLSER", contractSeries);
            reportData.put("CONTRPOLNUM", contractNumber);
            reportData.put("CONTRNUMBER", contractFullNumber);

            Date documentDate = (Date) parseAnyDate(contract.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
            if (documentDate != null) {
                SimpleDateFormat dateFormatterMonth = new SimpleDateFormat("dd MM yyyy");
                reportData.put("DOCUMENTDATESTR", dateFormatterMonth.format(documentDate));
                Locale russianLocale = new Locale("ru");
                DateFormatSymbols russianDateFormatSymbols = DateFormatSymbols.getInstance(russianLocale);
                russianDateFormatSymbols.setMonths(MONTHS_FOR_STRING_DATE);
                dateFormatterMonth = new SimpleDateFormat("«dd» MMMMM yyyy", russianLocale);
                dateFormatterMonth.setDateFormatSymbols(russianDateFormatSymbols);
                reportData.put("DOCUMENTDATEMONTHLYSTR", dateFormatterMonth.format(documentDate));
            }

            premCurrency = getStringParam(contract, "PREMCURRENCYID");
            String rub = "";
            String kop = "";
            // если пришло с интерфейса, тогда используем его
            Object premValueBean = params.get("payment");
            // если с интерфейса не пришло, тогда берем из договора
            if (premValueBean == null) {
                premValueBean = contract.get("PREMVALUE");
            }
            String premValueStr = String.format("%.2f", getDoubleParam(premValueBean));
            if ("1".equals(premCurrency)) {
                String[] rubAndKop = premValueStr.split("\\.");
                rub = rubAndKop[0];
                kop = rubAndKop[1];
            }
            reportData.put("PERMVALUERUB", rub);
            reportData.put("PERMVALUEKOP", kop);
        }

        //BANKMAP.CHECKACC
        if (error.isEmpty()) {
            Map<String, Object> bankDetailsQuerryParams = new HashMap<>();
            bankDetailsQuerryParams.put(RETURN_AS_HASH_MAP, true);
            bankDetailsQuerryParams.put(CONTRACT_ID_PARAMNAME, contractId);
            Map<String, Object>  bankDetailsResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BGetBankAccountByContrId", bankDetailsQuerryParams, login, password);
            error = getStringParam(bankDetailsResult, ERROR);
            if (error.isEmpty()) {
                Map<String, Object> bankMap = new HashMap<>();
                String bankDetailFind = getStringParam(bankDetailsResult, ACCOUNT_PARAM_NAME);
                bankMap.put("CHECKACC", bankDetailFind);
                reportData.put("BANKMAP", bankMap);
            } else {
                error = "Банковские реквизиты для данного договора отсутсвуют!";
            }
        }

        // формирование результата
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            // формирование результата
            result.put("REPORTDATA", reportData);
        } else {
            result.put(ERROR, error);
        }

        return result;
    }

    @WsMethod(requiredParams = {CONTRACT_ID_PARAMNAME, CLIENT_PROFILE_ID_PARAMNAME})
    public Map<String, Object> dsB2BPayPrintPrint(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        String error = "";
        Map<String, Object> reportData = new HashMap<>();

        Map<String, Object> dataProviderParams = new HashMap<String, Object>(params);
        dataProviderParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> dataProviderResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPayPrintDataProvider", dataProviderParams, login, password);
        error = getStringParamLogged(dataProviderResult, ERROR);
        if (isCallResultOKAndContains(dataProviderResult, "REPORTDATA") && (error.isEmpty())) {
            // данные подготовленные для отчета провайдером
            reportData = getMapParam(dataProviderResult, "REPORTDATA");
        }

        List<Map<String, Object>> docList = new ArrayList<>();
        if (error.isEmpty()) {
            // по умолчанию считается, что произошла ошибка
            error = DEFAULT_CLAIM_PRINT_ERROR_MSG;
            String templateName = getCoreSettingBySysName("paymentTemplate", login, password);
            Map<String, Object> reportParams = new HashMap<>();
            if (!templateName.isEmpty()) {
                reportParams.put("templateName", templateName);
                reportParams.put("REPORTDATA", reportData);
                reportParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> subResult = new HashMap<>();
                Map<String, Object> reportPrintResult = this.callServiceLogged(LIBREOFFICEREPORTSWS_SERVICE_NAME, "dsLibreOfficeReportCreate", reportParams, login, password);
                subResult.put("PRINTRES", reportPrintResult);
                String reportName = "";
                if (isCallResultOKAndContains(reportPrintResult, "REPORTDATA")) {
                    Map<String, Object> printResultReportData = getMapParam(reportPrintResult, "REPORTDATA");
                    reportName = getStringParam(printResultReportData, "reportName");
                    if (!reportName.isEmpty()) {
                        // успех
                        error = "";
                        String fileFormat = getStringParam(printResultReportData, "REPORTFORMATS");
                        printResultReportData.put("FILEPATH", reportName + fileFormat);
                        printResultReportData.put("FILENAME", "PayOrder" + fileFormat);
                        docList.add(printResultReportData);
                        processDocListForUpload(docList, params, login, password);
                    }
                }
            } else {
                error = "Отсутствует шаблон.";
            }
        }

        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            result.put("PAYMENT", docList.get(0));
        } else {
            result.put(ERROR, error);
        }

        return result;
    }

    @WsMethod(requiredParams = {CONTRACT_ID_PARAMNAME})
    public Map<String, Object> dsB2BGetBankAccountByContrId(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Long contractId = getLongParam(params, CONTRACT_ID_PARAMNAME);

        Map<String, Object> contractParams = new HashMap<>();
        contractParams.put("CONTRID", contractId);
        contractParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> contract = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParam", contractParams, login, password);

        Long contractProdVerId = getLongParamLogged(contract, "PRODVERID");
        Long prodProgId = getLongParamLogged(contract, "PRODPROGID");
        String premCurrency = getStringParam(contract, "PREMCURRENCYID");
        Map<String, Object> configParams = new HashMap<String, Object>();

        configParams.put("PRODVERID", contractProdVerId);
        Long prodConfId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME,
                "dsB2BProductConfigBrowseListByParam", configParams,
                login, password, "PRODCONFID")
        );

        String currencyStr = currencyMap.get(premCurrency);
        String findStr = currencyStr + "_BANK_DETAILS_ID";
        Map<String, Object> progrmParam = new HashMap<>();
        progrmParam.put("PRODPROGID", prodProgId);
        String prodProgSysName = getStringParam(
                this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME,
                        "dsB2BProductProgramBrowseListByParam",
                        progrmParam, login, password, "SYSNAME")
        );
        String findProdStr = findStr + "_" + prodProgSysName;

        Map<String, Object> prodDefValuQuerry = new HashMap<>();
        prodDefValuQuerry.put("PRODCONFID", prodConfId);
        List<Map<String, Object>> ropDefValList = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME,
                "dsB2BProductDefaultValueBrowseListByParam",
                prodDefValuQuerry, login, password);

        Long bankDetailsId = null;
        String prodDefValueName;
        for (Map<String, Object> prodDefVal : ropDefValList) {
            prodDefValueName = getStringParam(prodDefVal, "NAME");
            if (prodDefValueName.equals(findProdStr)) {
                bankDetailsId = getLongParam(prodDefVal, "VALUE");
                break;
            }
            if (prodDefValueName.equals(findStr)) {
                bankDetailsId = getLongParam(prodDefVal, "VALUE");
            }
        }

        String error = "";
        String accaountStr = "";
        if (bankDetailsId != null) {
            Map<String, Object> bankDetailFind = dctFindById(BANK_DETAILS_ENTITY_NAME, bankDetailsId);
            if (bankDetailFind != null) {
                accaountStr = getStringParam(bankDetailFind.get(ACCOUNT_PARAM_NAME.toLowerCase()));
            }
        } else {
            error = "Банковские реквизиты для данного договора отсутсвуют!";
        }

        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            result.put(ACCOUNT_PARAM_NAME, accaountStr);
        } else {
            result.put(ERROR, error);
        }
        return result;
    }

}
