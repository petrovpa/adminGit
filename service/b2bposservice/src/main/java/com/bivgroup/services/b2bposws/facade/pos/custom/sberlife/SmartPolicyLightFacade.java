package com.bivgroup.services.b2bposws.facade.pos.custom.sberlife;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
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
 * Продукт "СмартПолис Лайт"
 *
 * @author Ivanov Roman
 **/
@BOName("SmartPolicyLight")
public class SmartPolicyLightFacade extends B2BLifeBaseFacade {

    @Override
    protected void validateContractExtValues(Map<String, Object> contract, Map<String, Object> contractExtValues, boolean isFixContr, StringBuffer errorText) {
        checkContractExtValueExist(contractExtValues, "insuredGender", "Пол застрахованного", errorText);
        checkContractExtValueExist(contractExtValues, getInsuredAdultBirthDateFieldName(), "Дата рождения застрахованного", errorText);
        checkContractExtValueExist(contractExtValues, "insuredDeclCompliance", "Клиент соответствует декларации", errorText);
        checkContractExtValueExist(contractExtValues, FUND_ID_PARAMNAME, "Фонд", errorText);
        checkContractExtValueExist(contractExtValues, "isAutopilot", "Опция АВТОПИЛОТ подключена", errorText);
        checkContractExtValueExist(contractExtValues, "isAutopilotTakeProfit", "Автопилот ВВЕРХ (Take profit)", errorText);
        checkContractExtValueExist(contractExtValues, "isAutopilotStopLoss", "Автопилот ВНИЗ (Stop Loss)", errorText);
        checkContractExtValueExist(contractExtValues, "autopilotTakeProfitPerc", "Процент по опции Автопилот ВВЕРХ (Take profit)", errorText);
        checkContractExtValueExist(contractExtValues, "autopilotStopLossPerc", "Процент по опции Автопилот ВНИЗ (Stop Loss)", errorText);
        checkContractExtValueExist(contractExtValues, "assuranceLevel", "Уровень гарантии", errorText);
    }


    /**
     * Функция маппинга рисков согласно результатам калькулятора
     *
     * @param contrRiskList - список рисков
     * @param riskProdStructSysName - системное имя структуры риска
     * @param calcResMap - мапа, результат расчета калькулятора
     * @param insAmValueParamName
     * @param premValueParamName
     * @param checkIsSelected
     */
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

    /**
     * Функция получения срока страхования по id
     * @param productMap - мапа продукта
     * @param termId - id срока страхования
     * @return
     */
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

    /**
     * Функция получения разыменовки валюты договора по currencyId
     * @param currencyId - ид, валюты
     * @param login
     * @param password
     * @return
     * @throws Exception
     */
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

    /**
     * Метод получения кода канала продаж
     * @param contract - параметры договра
     * @param login - сис. логин
     * @param password сис. пароль
     * @return
     */
    private String getSaleChanelChar(Map<String, Object> contract, String login, String password) throws Exception {
        // Попытамся получить согласно данным в бд
        if ((contract != null) && (contract.get("PRODVERID") != null) && (contract.get("PRODSYSNAME") != null)) {

            Long prodVerId = getLongParam(contract.get("PRODVERID"));
            String prodSysname  = getStringParam(contract.get("PRODSYSNAME"));

            // Создаем параметры для запроса
            Map<String, Object> params = new HashMap<>();
            params.put("PRODVERID", prodVerId);
            params.put("PRODSYSNAME", prodSysname);
            params.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> result = this.callService(Constants.B2BPOSWS, "dsB2BProductSalesChannelBrowseListByParamEx", params, login, password);



        }

        return "B"; // По умолчанию
    }

    /**
     * Метод по генерации серии договора согласно правилу, описаному в задаче #20553
     *
     * Код продукта указывается на Страховом полисе и имеет следующий 6-ти символьный формат,
     * где каждый символ характеризует один из параметров продукта и определяется в ИТ-системе
     * Банка в соответствии с заданным набором значений (буквы русские):
     *
     * 1. В, Е - продукт (В - СмартПолис (5 и 7 лет), Е - СмартПолис Лайт (3 года));
     * 2. В – канал (Сбербанк Первый (VIP));
     * 3. С, П – уровень гарантии (С - стандартная (100%), П - повышенная (115%));
     * 4. Р, Д - валюта договора (Р – Рубли, Д – доллары США);
     * 5. 3, 5, 7 - срок договора (3 – 3 года, 5 – 5 лет, 7 – 7 лет);
     * 6. 0, 1 - дополнительные риски (0 – включен риск СНС, 1 – исключен риск СНС).
     * Риск СНС на интерфейсе не указывается и включен по-умолчанию.
     *
     * @param contract
     * @param serPrefix
     * @param login
     * @param password
     * @return
     * @throws Exception
     */
    @Override
    protected String generateContrSer(Map<String, Object> contract, String serPrefix, String login, String password) throws Exception {
        String result = serPrefix;
        result = result + getSaleChanelChar(contract, login, password);
        result = result + getWarrantyChar(contract);
        result = result + getCurrencyChar(contract);
        result = result + getDurationChar(contract, login, password);
        result = result + getDeathNSChar(contract);
        if (contract == null) {
            contract = new HashMap<>();
        }
        contract.put("CONTRPOLSER", result);
        return result;
    }

    private String getWarrantyChar(Map<String, Object> contract) {
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
    }

    private String getDurationChar(Map<String, Object> contract, String login, String password) throws Exception {
        if (contract.get("TERMID") != null) {
            Long termId = getLongParam(contract.get("TERMID"));
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("TERMID", termId);
            param.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> res = this.callService(Constants.B2BPOSWS, "dsB2BProductTermBrowseListByParamEx", param, login, password);
            if (res.get("YEARCOUNT") != null) {
                if ("3".equals(res.get("YEARCOUNT").toString())) {
                    return "3";
                }
            }
        }
        return "3"; // по умолчанию 3 года
    }

    private String getDeathNSChar(Map<String, Object> contract) {
//        //if (contract.containsKey("INSOBJGROUPLIST") != null)
//        List<Map<String, Object>> riskList = getAllRisksListFromContract(contract);
//        if (riskList == null) {
//            return "0";//accidentDeath по умолчанию риск СНС всегда включен
//        } else {
//            if (riskList.size() == 0) {
//                return "0";//accidentDeath по умолчанию риск СНС всегда включен
//            } else {
//                //Map<String, Object> risk = (Map<String, Object>) getLastElementByAtrrValue(riskList, "PRODRISKSYSNAME", "accidentDeath");
//                Map<String, Object> risk = (Map<String, Object>) getLastElementByAtrrValue(riskList, "PRODRISKSYSNAME", "ILI0_DEATH_DUE_ACC");
//                if (risk != null) {
//                    return "0";//accidentDeath по умолчанию риск СНС всегда включен
//                } else {
//                    return "1";//accidentDeath по умолчанию риск СНС всегда включен
//                }
//            }
//        }
        // Для этих продуктов на момент разработки безусловно передается 0
        return "0";
    }


    /*
     insFee - страховой взнос
     currencyID - ИД валюты договора
     currencySysName - Системное наименование валюты договора
     termYears - срок страхования в годах
     assuranceLevelIncreased - повышенный уровень гарантии
     */
    @WsMethod(requiredParams = {"CALCVERID", "insFee", "currencyID", "currencySysName", "termYears", "assuranceLevelIncreased"})
    public Map<String, Object> dsB2BSmartPolicyLightCalc(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BSmartPolicyLightCalc");
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

        logger.debug("after dsB2BSmartPolicyLightCalc");
        return calcRes;
    }



    /**
     * Основной метод для сохранения черновика договора "СмартПолис"
     * @param params - параметры для сохранения
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BSmartPolicyLightContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BSmartPolicyLightContractPrepareToSave");
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
        } else {
            result = contract;
        }
        logger.debug("after dsB2BSmartPolicyLightContractPrepareToSave");
        return result;
    }

    /**
     * Подоготовка списка фондов для продважи договора "СмартПолис"
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BSmartPolicyLightCustomGetAvailableStrategyForSale(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BSmartPolicyLightCustomGetAvailableStrategyForSale");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        String productSysName = getStringParamLogged(params, "PRODSYSNAME");
        List<Map<String, Object>> productStrategyList = this.getAvailableProductStrategyForSale(productSysName, login, password);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, productStrategyList);
        logger.debug("after dsB2BSmartPolicyLightCustomGetAvailableStrategyForSale");
        return result;
    }

    /**
     * Функция вызова расчета калькулятора согласно переданным данным с интерфейса
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRMAP", "FULLPRODMAP"})
    public Map<String, Object> dsB2BSmartPolicyLightCalcByContrMap(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BSmartPolicyLightCalcByContrMap");
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
            logger.debug("after dsB2BSmartPolicyLightCalcByContrMap");
            return result;
        }
        // данные договора корректны - можно выполнять вычисления
        genAdditionalSaveParams(contractMap, login, password);
        Map<String, Object> contrExtMap = (Map<String, Object>) contractMap.get("CONTREXTMAP");
        Map<String, Object> productMap = (Map<String, Object>) params.get("FULLPRODMAP");
        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        calcParams.put("CALCVERID", productMap.get("CALCVERID"));
        calcParams.put("insFee", Double.valueOf(contractMap.get("PREMVALUE").toString()));
        calcParams.put("currencyID", contractMap.get("INSAMCURRENCYID"));
        calcParams.put("currencySysName", getCurrencySysNameById(Long.valueOf(contractMap.get("INSAMCURRENCYID").toString()), login, password));
        calcParams.put("termYears", getTermInYearsById(productMap, Long.valueOf(contractMap.get("TERMID").toString())));
        calcParams.put("assuranceLevelIncreased", contrExtMap.get("assuranceLevelIncreased"));
        calcParams.put("isNewRS", 0L);      //поменяно на использование старого(или теперь уже нового) справочника
        calcParams.put("series", contractMap.get("CONTRPOLSER"));

        // согласно задаче #20553 риск СНС всегда включен а ссылка аанала продаж
        calcParams.put("CHANNELSALEID", 1);
        calcParams.put("ISENABLESNS", 0);

        //adding PRODCONFID - id продукта
        {
            calcParams.put("PRODCONFID", productMap.get("PRODCONFID"));
        }


        //adding PRODTERMID - срок страхования
        {
            HashMap<String, Object> params1 = new HashMap<>();
            params1.put("PRODCONFID", contractMap.get("PRODCONFID"));
            params1.put("TERMID", contractMap.get("TERMID"));
            Map<String, Object> res1 = this.callService(Constants.B2BPOSWS, "dsB2BProductTermBrowseListByParam", params1, login, password);
            List result = (List) res1.get("Result");
            calcParams.put("PRODTERMID", ((Map<String, Object>) result.get(0)).get("PRODTERMID"));
        }
        //adding PRODINSAMCURID - валюта
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

        Map<String, Object> resMap = this.callService(Constants.B2BPOSWS, "dsB2BSmartPolicyLightCalc", calcParams, login, password);
        // обработка результата калькулятора
        if (resMap.get("insSum") != null) {
            List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contractMap.get("INSOBJGROUPLIST");
            List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupList.get(0).get("OBJLIST");
            Map<String, Object> contrObjMap = (Map<String, Object>) objList.get(0).get("CONTROBJMAP");
            List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
            setResCalcRiskMapping(contrRiskList, "LIGHT_MAIN_PROGRAM", resMap, "insSum",
                    null, false);
            setResCalcRiskMapping(contrRiskList, "LIGHT_DEATH_DUE_ACC", resMap, "insSum",
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
        logger.debug("after dsB2BSmartPolicyLightCalcByContrMap");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BSmartPolicyLightContractUnderwritingCheck(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BSmartPolicyLightContractUnderwritingCheck");
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
        logger.debug("after dsB2BSmartPolicyLightContractUnderwritingCheck");
        return result;
    }


}
