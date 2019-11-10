package com.bivgroup.services.b2bposws.facade.pos.custom.sberlife;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * @author averichevsm
 */
@BOName("CapitalCustom")
public class CapitalCustomFacade extends B2BLifeBaseFacade {

    private final Logger logger = Logger.getLogger(CapitalCustomFacade.class);
    private static final String SDLAG_FOR_UPDATE_STARTDATE = "CDLAG_TO_SKIP_SATURDAY";

    /**
     * Метод для сохранения договора по продукту
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsCapitalContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsCapitalContractPrepareToSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }

        logger.debug("after dsCapitalContractPrepareToSave");
        return contract;
    }

    public static Integer calcYears(Date from, Date to) {
        Integer result = 0;
        if ((from != null) && (to != null)) {
            GregorianCalendar fromG = new GregorianCalendar();
            GregorianCalendar toG = new GregorianCalendar();
            fromG.setTime(from);
            toG.setTime(to);
            result = WsUtils.calcYears(fromG, toG);
        }
        return result;
    }

    private long calcAge(Date birthDate) {
        return calcYears(birthDate, new Date()).longValue();
    }

    /*
     termIns - срок страхования
     currencySysName - валюта договора
     chanelSysName - канал продаж
     minTerm - минимальный срок страхования
     gender - пол (0, 1)
     insBirthDATE - дата рождения застрахованного
     insFee - страховой взнос
     */
    @WsMethod(requiredParams = {"CALCVERID", "termIns", "currencySysName", "chanelSysName", "minTerm",
            "gender", "insBirthDATE", "insFee"})
    public Map<String, Object> dsB2BCapitalCalc(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BCapitalCalc");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.putAll(params);
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Long insPeriod = (Long) params.get("termIns");
        List<Map<String, Object>> yearList = new ArrayList<Map<String, Object>>();
        for (int i = 1; i <= insPeriod.intValue(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("year", i);
            map.put("rowNum", String.valueOf(i));
            yearList.add(map);
        }
        calcParams.put("LISTONYEAR", yearList);
        Date insBirthDate = (Date) parseAnyDate(params.get("insBirthDATE"), Date.class, "insBirthDATE");
        long insAge = calcAge(insBirthDate);
        calcParams.put("kAge", insAge);
        List<Map<String, Object>> calcList = new ArrayList<Map<String, Object>>();
        Map<String, Object> calcFirstMap = new HashMap<String, Object>();
        calcFirstMap.put("year", 0L);
        calcFirstMap.put("month", 0L);
        calcFirstMap.put("rowNum", 0L);
        calcList.add(calcFirstMap);
        long curAge = insAge;
        int rowNum = 1;
        for (int i = 1; i <= insPeriod; i++) {
            for (int j = 1; j <= 12; j++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("year", i);
                map.put("month", j);
                map.put("age", curAge);
                map.put("rowNum", rowNum);
                calcList.add(map);
                rowNum++;
            }
            curAge++;
        }
        calcParams.put("CALCLIST", calcList);
        logger.debug("calcParams: " + calcParams);
        Map<String, Object> calcRes = this.callService(Constants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcParams, login, password);
        logger.debug("calcRes: " + calcRes);

        logger.debug("after dsB2BCapitalCalc");
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

    /*
     Получение канала продаж (возвращаем первый, т.к. он один для данного продукта)
     */
    private String getSaleChannelName(Map<String, Object> productMap) {
        Map<String, Object> prodVerMap = (Map<String, Object>) productMap.get("PRODVER");
        List<Map<String, Object>> prodSaleChannelList = (List<Map<String, Object>>) prodVerMap.get("PRODSALESCHANS");
        if (prodSaleChannelList != null) {
            for (Map<String, Object> bean : prodSaleChannelList) {
                return bean.get("SALECHANNELNAME").toString();
            }
        }
        return null;
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
    public Map<String, Object> dsB2BCapitalCalcByContrMap(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BCapitalCalcByContrMap");
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
            logger.debug("after dsB2BCapitalCalcByContrMap");
            return result;
        }
        // данные договора корректны - можно выполнять вычисления
        Map<String, Object> contrExtMap = (Map<String, Object>) contractMap.get("CONTREXTMAP");
        Map<String, Object> productMap = (Map<String, Object>) params.get("FULLPRODMAP");
        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        calcParams.put("CALCVERID", productMap.get("CALCVERID"));
        calcParams.put("termIns", getTermInYearsById(productMap, Long.valueOf(contractMap.get("TERMID").toString())));
        if (contractMap.get("INSAMCURRENCYID") != null) {
            calcParams.put("currencySysName", getCurrencySysNameById(Long.valueOf(contractMap.get("INSAMCURRENCYID").toString()), login, password));
        }
        calcParams.put("chanelSysName", getSaleChannelName(productMap));
        calcParams.put("minTerm", calcParams.get("termIns"));
        calcParams.put("gender", contrExtMap.get("insuredGender"));
        calcParams.put("insBirthDATE", contrExtMap.get("insuredBirthDATE"));
        calcParams.put("insFee", Double.valueOf(contractMap.get("PREMVALUE").toString()));

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

        Map<String, Object> resMap = this.callService(Constants.B2BPOSWS, "dsB2BCapitalCalc", calcParams, login, password);
        // обработка результата калькулятора
        if (resMap.get("yearList") != null) {
            List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contractMap.get("INSOBJGROUPLIST");
            List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupList.get(0).get("OBJLIST");
            Map<String, Object> contrObjMap = (Map<String, Object>) objList.get(0).get("CONTROBJMAP");
            List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
            setResCalcRiskMapping(contrRiskList, "survivalDeath", resMap, "SurvivalEndInsPeriod",
                    null, false);
            setResCalcRiskMapping(contrRiskList, "accidentDeath", resMap, "AccidentalDeath",
                    null, false);
            List<Map<String, Object>> yearList = (List<Map<String, Object>>) resMap.get("yearList");
            for (int i = 0; i < 3; i++) {
                contrExtMap.put("deathGSS" + String.valueOf(i + 1) + "Year", yearList.get(i).get("guaranteedSumByRiskDeath"));
                contrExtMap.put("redemptionSumYear" + String.valueOf(i + 1), yearList.get(i).get("guaranteedSurrenderSum"));
            }
            contrExtMap.put("marketRateOfReturn", resMap.get("GuarYieldAnn"));
        }
        //
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRMAP", contractMap);
        logger.debug("after dsB2BCapitalCalcByContrMap");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BCapitalContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BCapitalContractPrepareToSave");
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
            //getFinishDateByStartDateAndTermId(contract, login, password); // перенесено в B2BLifeBaseFacade.genAdditionalSaveParams
            //geterateContractSerNum(contract, login, password); // перенесено в B2BLifeBaseFacade.genAdditionalSaveParams
            //result = contract;
        } else {
            result = contract;
        }
        logger.debug("after dsB2BCapitalContractPrepareToSave");
        return result;
    }

    @WsMethod(requiredParams = {"CONTRMAP"})
    public void updateContractStartDate(Map<String, Object> params) throws Exception {
        Map<String, Object> contract = (Map<String, Object>) params.get("CONTRMAP");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Long lag = getSdLagToSkipSaturday(contract, login, password);
        if (contract != null && !contract.isEmpty() && lag != null) {
            Date date = (Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE");
            GregorianCalendar gcDate = new GregorianCalendar();
            gcDate.setTime(date);
            resetTime(gcDate);
            date = gcDate.getTime();
            Map<String, Object> callParams = new HashMap<String, Object>();
            callParams.put("STARTCALDATE", parseAnyDate(gcDate.getTime(), Double.class, "STARTDATE"));
            GregorianCalendar gcDatePlus = new GregorianCalendar();
            gcDatePlus.setTime(date);
            // прибавляем 60 дней, должно хватить (лаг не должен быть больше 60 дней)
            gcDatePlus.add(Calendar.DAY_OF_YEAR, 60);
            callParams.put("FINISHCALDATE", parseAnyDate(gcDatePlus.getTime(), Double.class, "STARTDATE"));
            callParams.put("ISHOLIDAY", 1L);
            callParams.put("ORDERBY", "T.CALDATE");
            List<Map<String, Object>> resList = this.callServiceAndGetListFromResultMap(Constants.B2BPOSWS,
                    "dsB2BCalendarBrowseListByParamEx", callParams, login, password);
            if (resList == null || resList.isEmpty()) {
                gcDate.add(Calendar.DAY_OF_YEAR, lag.intValue());
            } else {
                int idx = 0;
                while (lag > 0) {
                    gcDate.add(Calendar.DAY_OF_YEAR, 1);
                    Date dCalDate = (Date) parseAnyDate(resList.get(idx).get("CALDATE"), Date.class, "CALDATE");
                    GregorianCalendar gcCalcDate = new GregorianCalendar();
                    gcCalcDate.setTime(dCalDate);
                    resetTime(gcCalcDate);
                    // если текущий день - праздник, пропускаем его, при этом лаг не уменьшается
                    if (gcDate.getTimeInMillis() >= gcCalcDate.getTimeInMillis()) {
                        idx++;
                    } else {
                        lag -= 1;
                    }
                }
            }
            if (gcDate.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) {
                gcDate.add(Calendar.DAY_OF_YEAR, 1);
            }
            contract.put("STARTDATE", gcDate.getTime());
        }
    }

    private void resetTime(GregorianCalendar gc) {
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
    }

    private Long getSdLagToSkipSaturday(Map<String, Object> contract, String login, String password) throws Exception {
        Map<String, Object> product = getFullProductDataFromContract(contract, login, password);
        Map<String, Object> prodDefVal = getProdDefValsMapFromProduct(product);
        return getLongProdDefValueFromDefValuesMap(prodDefVal, SDLAG_FOR_UPDATE_STARTDATE);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BCapitalContractUnderwritingCheck(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BCapitalContractUnderwritingCheck");
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
        logger.debug("after dsB2BCapitalContractUnderwritingCheck");
        return result;
    }

}
