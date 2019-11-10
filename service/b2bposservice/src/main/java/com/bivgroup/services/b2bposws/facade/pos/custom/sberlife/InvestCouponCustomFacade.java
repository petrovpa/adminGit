package com.bivgroup.services.b2bposws.facade.pos.custom.sberlife;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bivgroup.services.b2bposws.facade.pos.contract.custom.B2BContractCustomFacade.getLastElementByAtrrValue;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * @author averichevsm
 */
@BOName("InvestCouponCustom")
public class InvestCouponCustomFacade extends B2BLifeBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    /*
     insFee - страховой взнос
     currencyID - ИД валюты договора
     currencySysName - Системное наименование валюты договора
     termYears - срок страхования в годах
     assuranceLevelIncreased - повышенный уровень гарантии
     */
    @WsMethod(requiredParams = {"CALCVERID", "insFee", "currencyID", "currencySysName", "termYears", "assuranceLevelIncreased"})
    public Map<String, Object> dsB2BInvestCouponCalc(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BInvestCouponCalc");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.putAll(params);
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Long insPeriod = (Long) params.get("termYears");
        List<Map<String, Object>> yearList = new ArrayList<Map<String, Object>>();
        for (int i = 1; i <= insPeriod.intValue(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("year", i);
            yearList.add(map);
        }
        calcParams.put("LISTONYEAR", yearList);
        logger.debug("calcParams: " + calcParams);
        Map<String, Object> calcRes = this.callService(Constants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcParams, login, password);
        logger.debug("calcRes: " + calcRes);

        logger.debug("after dsB2BInvestCouponCalc");
        return calcRes;
    }

    private Long getTermInYearsById(Map<String, Object> productMap, Long termId) {
        List<Map<String, Object>> prodTermList = (List<Map<String, Object>>) productMap.get("PRODTERMS");
        if (prodTermList != null) {
            for (Map<String, Object> bean : prodTermList) {
                if (bean.get("TERM") != null) {
                    Map<String, Object> termMap = (Map<String, Object>) bean.get("TERM");
                    if (Long.valueOf(termMap.get("TERMID").toString()).longValue() == termId.longValue()) {
                        return Long.valueOf(termMap.get("YEARCOUNT").toString());
                    }
                }
            }
        }
        return null;
    }

    private String getCurrencySysNameById(Long currencyId, String login, String password) throws Exception {
        Map<String, Object> qparam = new HashMap<String, Object>();
        qparam.put("CurrencyID", currencyId);
        qparam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qRes = this.callService(Constants.REFWS, "getCurrencyByParams", qparam, login, password);
        if ((qRes != null) && (qRes.get("Brief") != null)) {
            return qRes.get("Brief").toString();
        } else {
            return null;
        }
    }

    private void setResCalcRiskMapping(List<Map<String, Object>> contrRiskList, String riskProdStructSysName,
                                       Map<String, Object> calcResMap, String insAmValueParamName, String premValueParamName, boolean checkIsSelected) {
        for (Map<String, Object> bean : contrRiskList) {
            if ((bean.get("PRODRISKSYSNAME").toString().equalsIgnoreCase(riskProdStructSysName))
                    && ((!checkIsSelected) || (checkIsSelected && (bean.get("ISSELECTED") != null) && (Long.valueOf(bean.get("ISSELECTED").toString()).longValue() == 1)))) {
                if (insAmValueParamName != null) {
                    bean.put("INSAMVALUE", calcResMap.get(insAmValueParamName));
                }
                if (premValueParamName != null) {
                    bean.put("PREMVALUE", calcResMap.get(premValueParamName));
                }
                break;
            }
        }
    }

    @WsMethod(requiredParams = {"CONTRMAP", "FULLPRODMAP"})
    public Map<String, Object> dsB2BInvestCouponCalcByContrMap(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BInvestCouponCalcByContrMap");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contractMap = (Map<String, Object>) params.get("CONTRMAP");
        // предварительная проверка на корректность введенных данных
        // (вычисления выполнять не нужно, если переданы заведомо не походящие для создания договора данные)
        boolean isPreCalcCheck = true; // проверка перед вызовом калькулятора (часть атрибутов, например, вычисляемые суммы, не будут проверены)
        boolean isDataValid = this.validateContractSaveParams(contractMap, false, isPreCalcCheck, login, password);
        if (!isDataValid) {
            // данные не корректны - досрочный возврат мапы договора
            logger.debug("Contract is not valid - calculation skipped!");
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("CONTRMAP", contractMap);
            logger.debug("after dsB2BInvestCouponCalcByContrMap");
            return result;
        }
        // данные договора корректны - можно выполнять вычисления
        Map<String, Object> contrExtMap = (Map<String, Object>) contractMap.get("CONTREXTMAP");
        Map<String, Object> productMap = (Map<String, Object>) params.get("FULLPRODMAP");
        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        calcParams.put("CALCVERID", productMap.get("CALCVERID"));
        calcParams.put("insFee", Double.valueOf(contractMap.get("PREMVALUE").toString()));
        calcParams.put("currencyID", contractMap.get("INSAMCURRENCYID"));
        calcParams.put("currencySysName", getCurrencySysNameById(Long.valueOf(contractMap.get("INSAMCURRENCYID").toString()), login, password));
        calcParams.put("termYears", getTermInYearsById(productMap, Long.valueOf(contractMap.get("TERMID").toString())));
        calcParams.put("assuranceLevelIncreased", 0L);

        //adding PRODCONFID - id продукта
        {
            calcParams.put("PRODCONFID", productMap.get("PRODCONFID"));
        }

        //adding PRODTERMID
        {
            HashMap<String, Object> params1 = new HashMap<>();
            params1.put("PRODCONFID", contractMap.get("PRODCONFID"));
            params1.put("TERMID", contractMap.get("TERMID"));
            Map<String, Object> res1 = this.callService(Constants.B2BPOSWS, "dsB2BProductTermBrowseListByParam", params1, login, password);
            List result = (List) res1.get("Result");
            calcParams.put("PRODTERMID", ((Map<String, Object>) result.get(0)).get("PRODTERMID"));
        }
        //adding PRODINSAMCURID
        {
            Map<String, Object> params2 = new HashMap<>();
            params2.put("PRODCONFID",contractMap.get("PRODCONFID"));
            params2.put("CURRENCYID",contractMap.get("INSAMCURRENCYID"));
            Map<String,Object> tmpResMap = this.callService(Constants.B2BPOSWS,"dsB2BProductInsAmCurrencyBrowseListByParam",params2,login,password);
            List result = (List) tmpResMap.get("Result");
            calcParams.put("PRODINSAMCURID", ((Map<String, Object>) result.get(0)).get("PRODINSAMCURID"));
        }
        //adding PRODPAYVARID - период страхования
        {
            Map<String, Object> params2 = new HashMap<>();
            params2.put("PRODVERID",contractMap.get("PRODVERID"));
            params2.put("PAYVARID",contractMap.get("PAYVARID"));
            Map<String,Object> tmpResMap = this.callService(Constants.B2BPOSWS,"dsB2BProductPaymentVariantBrowseListByParam",params2,login,password);
            List result = (List) tmpResMap.get("Result");
            calcParams.put("PRODPAYVARID", ((Map<String, Object>) result.get(0)).get("PRODPAYVARID"));
        }
        //adding PRODINVESTID - инвестиционная стратегия
        {
            Map<String, Object> params2 = new HashMap<>();
            params2.put("PRODCONFID",contractMap.get("PRODCONFID"));
            Map<String,Object> tmpResMap = this.callService(Constants.B2BPOSWS,"dsB2BProductInvestBrowseListByParamEx",params2,login,password);
            List result = (List) tmpResMap.get("Result");
            calcParams.put("PRODINVESTID", ((Map<String, Object>) result.get(0)).get("PRODINVESTID"));
        }

        Map<String, Object> resMap = this.callService(Constants.B2BPOSWS, "dsB2BInvestCouponCalc", calcParams, login, password);
        // обработка результата калькулятора
        if (resMap.get("insSum") != null) {
            List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contractMap.get("INSOBJGROUPLIST");
            List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupList.get(0).get("OBJLIST");
            Map<String, Object> contrObjMap = (Map<String, Object>) objList.get(0).get("CONTROBJMAP");
            List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
            setResCalcRiskMapping(contrRiskList, "survivalDeath", resMap, "insSum",
                    null, false);
            setResCalcRiskMapping(contrRiskList, "accidentDeath", resMap, "insSum",
                    null, false);
            setResCalcRiskMapping(contrRiskList, "RB_ILIK_MAIN_PROGRAM", resMap, "insSum",
                    null, false);
            setResCalcRiskMapping(contrRiskList, "RB_ILIK_DEATH_DUE_ACC", resMap, "insSum",
                    null, false);
            contractMap.put("INSAMVALUE", resMap.get("insSum"));
            // обработка выкупных сумм
            List<Map<String, Object>> yearList = (List<Map<String, Object>>) resMap.get("yearList");
            for (Map<String, Object> bean : yearList) {
                contrExtMap.put("redemptionSumYear" + bean.get("year").toString(), bean.get("redemptionSum"));
            }
        }
        //
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRMAP", contractMap);
        logger.debug("after dsB2BInvestCouponCalcByContrMap");
        return result;
    }

    protected String generateContrSer(Map<String, Object> contract, String serPrefix, String login, String password) throws Exception {
        String result = serPrefix;
        result = result + getFundChar(contract, login, password);
        result = result + getDurationChar(contract, login, password);
        result = result + getCurrencyChar(contract);
        result = result + getDeathNSChar(contract);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestCouponContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BInvestCouponContractPrepareToSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        boolean isDataValid = this.validateContractSaveParams(contract, false, login, password);
        Map<String, Object> result;
        if (isDataValid) {
            result = genAdditionalSaveParams(contract, login, password);
            //geterateContractSerNum(contract, login, password); // перенесено в B2BLifeBaseFacade.genAdditionalSaveParams
            //getFinishDateByStartDateAndTermId(contract, login, password); // перенесено в B2BLifeBaseFacade.genAdditionalSaveParams
            //result = contract;
        } else {
            result = contract;
        }
        logger.debug("after dsB2BInvestCouponContractPrepareToSave");
        return result;
    }

    @Override
    protected void validateContractExtValues(Map<String, Object> contract, Map<String, Object> contractExtValues, boolean isFixContr, StringBuffer errorText) {
        checkContractExtValueExist(contractExtValues, "insuredGender", "Пол застрахованного", errorText);
        checkContractExtValueExist(contractExtValues, getInsuredAdultBirthDateFieldName(), "Дата рождения застрахованного", errorText);
        checkContractExtValueExist(contractExtValues, "insuredDeclCompliance", "Клиент соответствует декларации", errorText);
        checkContractExtValueExist(contractExtValues, FUND_ID_PARAMNAME, "Фонд", errorText);
        //checkContractExtValueExist(contractExtValues, "isAutopilot", "Опция АВТОПИЛОТ подключена", errorText);
        //checkContractExtValueExist(contractExtValues, "isAutopilotTakeProfit", "Автопилот ВВЕРХ (Take profit)", errorText);
        //checkContractExtValueExist(contractExtValues, "isAutopilotStopLoss", "Автопилот ВНИЗ (Stop Loss)", errorText);
        //checkContractExtValueExist(contractExtValues, "autopilotTakeProfitPerc", "Процент по опции Автопилот ВВЕРХ (Take profit)", errorText);
        //checkContractExtValueExist(contractExtValues, "autopilotStopLossPerc", "Процент по опции Автопилот ВНИЗ (Stop Loss)", errorText);
        checkContractExtValueExist(contractExtValues, "assuranceLevel", "Уровень гарантии", errorText);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestCouponContractUnderwritingCheck(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BInvestCouponContractUnderwritingCheck");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        this.underwritingCheck(contract, login, password);
        Map<String, Object> result = contract;
        logger.debug("after dsB2BInvestCouponContractUnderwritingCheck");
        return result;
    }

    /*private String getWarrantyChar(Map<String, Object> contract) {
        if (contract.get("CONTREXTMAP") != null) {
            Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
            if (contrExtMap.get("assuranceLevelIncreased") != null) {
                if ("1".equals(contrExtMap.get("assuranceLevelIncreased").toString())) {
                    // повышенный уровень гарантии
                    return "П";
                } else {
                    // стандартный уровень гарантии
                    return "С";
                }
            }
        }
        return "С"; // по умолчанию стандартный уровень
    }*/
    //
    /*
    private String getFundChar(Map<String, Object> contract) {
        if (contract.get("CONTREXTMAP") != null) {
            Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
            if (contrExtMap.get("fund") != null) {
                //todo после переделки интерфейсов со старых справочников на новую структуру - не забыть допилить тут!
                if ("1".equals(contrExtMap.get("fund").toString())) {
                    // Пакет it
                    return "3";
                } else {
                    // пакет фармы
                    return "1";
                }
            }
        }
        return "3"; // по умолчанию it
    }
    */

    private String getFundChar(Map<String, Object> contract, String login, String password) throws Exception {
        String fundChar = "3"; // по умолчанию it
        Map<String, Object> contractExtMap = getContrExtMap(contract);
        Long fundId = getLongParamLogged(contractExtMap, FUND_ID_PARAMNAME);
        if (fundId != null) {
            Long fundRefTypeId = getLongParamLogged(contractExtMap, FUND_REF_TYPE_ID_PARAMNAME);
            if (FUND_REF_TYPE_ID_PRODUCT_STRATEGY_HANDBOOK.equals(fundRefTypeId)) {
                // Тип ссылки на фонд (fundRefTypeId): 2 - на новый справочник (B2B.SBSJ.RelationStrategyProduct)
                // todo: возможно, следовало бы хранить т.н. fundChar в справочниках стратегий инвестирования по продукту
                Map<String, Object> productStrategy = getProductStrategy(fundId, login, password);
                String productStrategyName = getStringParamLogged(productStrategy, "STRATEGYNAME");
                String productStrategySysName = getStringParamLogged(productStrategy, "STRATEGYSYSNAME");
                if (productStrategyName.toUpperCase().contains("ФАРМА") || productStrategySysName.contains("К00009")) {
                    // пакет фармы
                    fundChar = "1";
                } else {
                    // Пакет it
                    fundChar = "3";
                }
            } else {
                // Тип ссылки на фонд (fundRefTypeId): null/1 - на старый справочник (B2B.InvestNum1.Funds)
                if ("1".equals(fundId.toString())) {
                    // Пакет it
                    fundChar = "3";
                } else {
                    // пакет фармы
                    fundChar = "1";
                }
            }
        }
        return fundChar;
    }

    private String getDurationChar(Map<String, Object> contract, String login, String password) throws Exception {
        if (contract.get("TERMID") != null) {
            Long termId = getLongParam(contract.get("TERMID"));
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("TERMID", termId);
            param.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> res = this.callService(Constants.B2BPOSWS, "dsB2BProductTermBrowseListByParamEx", param, login, password);
            if (res.get("YEARCOUNT") != null) {
                if ("5".equals(res.get("YEARCOUNT").toString())) {
                    return "5";
                }
                if ("7".equals(res.get("YEARCOUNT").toString())) {
                    return "7";
                }
                if ("10".equals(res.get("YEARCOUNT").toString())) {
                    return "0";
                }
            }

        }
        return "5"; // по умолчанию 5 лет
    }

    private String getDeathNSChar(Map<String, Object> contract) {
        //if (contract.containsKey("INSOBJGROUPLIST") != null)
        List<Map<String, Object>> riskList = getAllRisksListFromContract(contract);
        if (riskList == null) {
            return "0";//accidentDeath по умолчанию риск СНС всегда включен            
        } else {
            if (riskList.size() == 0) {
                return "0";//accidentDeath по умолчанию риск СНС всегда включен            
            } else {
                //Map<String, Object> risk = (Map<String, Object>) getLastElementByAtrrValue(riskList, "PRODRISKSYSNAME", "accidentDeath");
                Map<String, Object> risk = (Map<String, Object>) getLastElementByAtrrValue(riskList, "PRODRISKSYSNAME", "RB_ILIK_DEATH_DUE_ACC");
                if (risk != null) {
                    return "0";//accidentDeath по умолчанию риск СНС всегда включен
                } else {
                    return "1";//accidentDeath по умолчанию риск СНС всегда включен
                }
            }
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BInvestCouponGetAvailableStrategyForSale(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BInvestCouponGetAvailableStrategyForSale");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        String productSysName = getStringParamLogged(params, "PRODSYSNAME");
        List<Map<String, Object>> productStrategyList = this.getAvailableProductStrategyForSale(productSysName, login, password);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, productStrategyList);
        logger.debug("after dsB2BInvestCouponGetAvailableStrategyForSale");
        return result;
    }

}
