package com.bivgroup.services.b2bposws.facade.pos.invest.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@BOName("B2BInvestDIDDynamicReportDataProvider")
public class B2BInvestDIDDynamicReportDataProvider extends B2BBaseFacade {

    private static final String DEFAULT_DATA_PROVIDER_ERROR_MSG = "Не удалось подготовить данные для формирования отчета динамика ДИД!";
    private static final String DIDDINAMIC_MAP_PARAMNAME = "DIDDINAMICMAP";
    private static final String DIDLIST_PARAMNAME = "DIDLIST";

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BInvestDIDDynamicReportDataProvider(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BInvestDIDDynamicReportDataProvider begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        String errorDefault = DEFAULT_DATA_PROVIDER_ERROR_MSG;
        String error = "";
        // мапа основного результата работы провайдера - REPORTDATA
        Map<String, Object> result = new HashMap<>();
        // ИД договора
        Long contrId = getLongParamLogged(params, "CONTRID");
        String contractFullNumber = null;
        Map<String, Object> contractParams = new HashMap<>();
        contractParams.put("CONTRID", contrId);
        contractParams.put(RETURN_AS_HASH_MAP, true);
//        Map<String, Object> contract = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParam", contractParams, login, password);
        Map<String, Object> contract = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractUniversalLoad", contractParams, login, password);
        if (!isCallResultOKAndContainsLongValue(contract, "CONTRID", contrId)) {
            error = "Не удалось загрузить сведения договора страхования, указанного в заявлении на изменение условий страхования!";
        } else {
            // данные договора в reportData
            String contractSeries = getStringParamLogged(contract, "CONTRPOLSER");
            String contractNumber = getStringParamLogged(contract, "CONTRPOLNUM");
            contractFullNumber = getStringParamLogged(contract, "CONTRNUMBER");
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
            result.put("CONTRPOLSER", contractSeries);
            result.put("CONTRPOLNUM", contractNumber);
            result.put("CONTRNUMBER", contractFullNumber);
            result.put("DOCUMENTDATE", contract.get("DOCUMENTDATE"));
            result.put("PREMCURRENCYID", contract.get("PREMCURRENCYID"));
            result.put("INSAMCURRENCYID", contract.get("INSAMCURRENCYID"));
            result.put("STARTDATE", contract.get("STARTDATE"));
            result.put("CONTRID", contrId);
            Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
            if (contrExtMap != null) {
                result.put("BASEACTIVE", contrExtMap.get("BASEACTIVE"));
            }

            Map<String, Object> productMainInfo = (Map<String, Object>) contract.get("PRODUCTMAP");
            if (productMainInfo != null) {
                Long prodConfID = getLongParamLogged(productMainInfo, "PRODCONFID");
                Map<String, Object> productParams = new HashMap<>();
                productParams.put("PRODCONFID", prodConfID);
                productParams.put("LOADALLDATA", 1L);
                productParams.put("HIERARCHY", false);
                productParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> productFullMap = this.callServiceTimeLogged(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);
                if (productFullMap != null) {
                    result.put("PREMCURRENCYSTR", getCurrencyNameById(productFullMap, getLongParam(contract.get("PREMCURRENCYID"))));
                    result.put("INSAMCURRENCYSTR", getCurrencyNameById(productFullMap, getLongParam(contract.get("INSAMCURRENCYID"))));
                    if (result.get("PREMCURRENCYSTR") == null || getStringParam(result.get("PREMCURRENCYSTR")).isEmpty()) {
                        Map<String,Object> curMap = getCurrencyById(getLongParam(contract.get("PREMCURRENCYID")), login, password);
                        if (curMap != null) {
                            if (curMap.get("Name") != null) {
                                result.put("PREMCURRENCYSTR", curMap.get("Name"));
                            }
                        }
                    }
                    if (result.get("INSAMCURRENCYSTR") == null || getStringParam(result.get("INSAMCURRENCYSTR")).isEmpty()) {
                        if (getLongParam(contract.get("PREMCURRENCYID")).compareTo(getLongParam(contract.get("INSAMCURRENCYID"))) != 0) {
                            Map<String,Object> curMap = getCurrencyById(getLongParam(contract.get("INSAMCURRENCYID")), login, password);
                            if (curMap != null) {
                                if (curMap.get("Name") != null) {
                                    result.put("INSAMCURRENCYSTR", curMap.get("Name"));
                                }
                            }
                        } else {
                            result.put("INSAMCURRENCYSTR", result.get("PREMCURRENCYSTR"));
                        }

                    }

                }
            }

        }

        if (error.isEmpty()) {
            // Загрузка ДИД
            Map<String, Object> didMap = new HashMap<>();
            result.put(DIDDINAMIC_MAP_PARAMNAME, didMap);
            Map<String, Object> investParams = new HashMap<>();
            investParams.put("CONTRNUMBER", contractFullNumber);
            investParams.put("ORDERBYCALCDATE", true);
            Map<String, Object> investMap = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BInvestBrowseListByParamEx", investParams, login, password);
            if (investMap != null) {
                List<Map<String, Object>> investList = (List<Map<String, Object>>) investMap.get(RESULT);
                if (!investList.isEmpty()) {
                    didMap.put(DIDLIST_PARAMNAME, investList);
                    // last elem
                    Map<String, Object> itemMap = investList.get(0);
                    if (itemMap != null) {
                        didMap.put("FIRSTDATE", itemMap.get("CALCDATE"));
                    }
                    // first elem
                    itemMap = investList.get(investList.size() - 1);
                    if (itemMap != null) {
                        didMap.put("LASTDATE", itemMap.get("CALCDATE"));
                    }
                }
            }
        }

        if (error.isEmpty()) {
            Date todayDate = new Date();
            result.put("TODAYDATE", todayDate);
            // генерация строковых представлений для всех дат
            genDateStrs(result);
        }

        // формирование результата
        if (!error.isEmpty()) {
            result.put(ERROR, error);
        }
        //loggerDebugPretty(logger, "dsB2BInvestDIDDynamicReportDataProvider result", result);
        logger.debug("dsB2BInvestDIDDynamicReportDataProvider end");
        return result;
    }

    private String getCurrencyNameById(Map<String, Object> productMap, Long currencyID) {
        if ((productMap != null) && (currencyID != null)) {
            List<Map<String, Object>> prodInsAmCursList = (List<Map<String, Object>>) productMap.get("PRODINSAMCURS");
            if (prodInsAmCursList != null) {
                for (Map<String, Object> bean : prodInsAmCursList) {
                    Map<String, Object> currencyMap = (Map<String, Object>) bean.get("CURRENCY");
                    Long prodInfoCurrencyID = getLongParam(currencyMap, "CURRENCYID");
                    if (currencyID != null) {
                        if (prodInfoCurrencyID != null) {
                            if (prodInfoCurrencyID.equals(currencyID)) {
                                return getStringParam(currencyMap.get("CURRENCYNAME"));
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
