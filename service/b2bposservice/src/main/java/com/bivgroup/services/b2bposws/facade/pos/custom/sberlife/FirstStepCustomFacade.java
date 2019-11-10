package com.bivgroup.services.b2bposws.facade.pos.custom.sberlife;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("FirstStepCustom")
public class FirstStepCustomFacade extends B2BLifeBaseFacade {

    private final Logger logger = Logger.getLogger(FirstStepCustomFacade.class);

    /**
     * Метод для сохранения договора по продукту
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsFirstStepContractPrepareToSave(Map<String, Object> params) throws Exception {
        // dsFirstStepContractPrepareToSave оставлен для совместимости в ходе разработки, действительный метод - dsB2BFirstStepContractPrepareToSave
        // todo: текущий метод нужно будет удалить после завершения разработки по валидации и *PrepareToSave
        return dsB2BFirstStepContractPrepareToSave(params);
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
     typeProdSysName - тип продукта
     salesChannelSysName - канал продаж
     genderInsurrer - пол застрахованного
     genderInsured - пол страхователя
     insurrerBirthDATE - дата рождения застрахованного
     issuredBirthDATE - дата рождения страхователя
     termIns - срок страхования
     paymentVariantSysName - периодичность уплаты взносов
     currencySysName - валюта страхования
     persen - ожидаемая доходность
     insFee - страховой взнос
     gurSum - гарантированная страховая сумма
     gurSumProgInsuredExemPayModeSystem - ON или OFF освобождение от уплаты взносов
     modeSysName - {"MAX_GSS", "HAND_GSS"}
     */
    @WsMethod(requiredParams = {"CALCVERID", "typeProdSysName", "salesChannelSysName", "genderInsurrer", "genderInsured",
        "insurrerBirthDATE", "issuredBirthDATE", "termIns", "paymentVariantSysName", "currencySysName", "persen",
        "insFee", "gurSum", "gurSumProgInsuredExemPayModeSystem", "modeSysName"})
    public Map<String, Object> dsB2BFirstStepCalc(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BFirstStepCalc");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.putAll(params);
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Long insPeriod = (Long) params.get("termIns");
        List<Map<String, Object>> yearList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i <= insPeriod.intValue(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("year", i);
            map.put("rowNum", String.valueOf(i + 1));
            yearList.add(map);
        }
        calcParams.put("CALCLIST", yearList);
        Date adultBirthDate = (Date) parseAnyDate(params.get("issuredBirthDATE"), Date.class, "issuredBirthDATE");
        long adultAge = calcAge(adultBirthDate);
        calcParams.put("ageIssured", adultAge);
        Date childBirthDate = (Date) parseAnyDate(params.get("insurrerBirthDATE"), Date.class, "insurrerBirthDATE");
        long childAge = calcAge(childBirthDate);
        calcParams.put("ageInsurrer", childAge);
        logger.debug("calcParams: " + calcParams);
        Map<String, Object> calcRes = this.callService(Constants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcParams, login, password);
        logger.debug("calcRes: " + calcRes);

        logger.debug("after dsB2BFirstStepCalc");
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

    private String getPaymentVariantSysNameById(Map<String, Object> productMap, Long paymentVariantId) {
        Map<String, Object> prodVerMap = (Map<String, Object>) productMap.get("PRODVER");
        List<Map<String, Object>> prodPayVarList = (List<Map<String, Object>>) prodVerMap.get("PRODPAYVARS");
        if (prodPayVarList != null) {
            for (Map<String, Object> bean : prodPayVarList) {
                Map<String, Object> payVarMap = (Map<String, Object>) bean.get("PAYVAR");
                if (Long.valueOf(payVarMap.get("PAYVARID").toString()).longValue() == paymentVariantId.longValue()) {
                    return payVarMap.get("SYSNAME").toString();
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

    private void setCalcRiskMapping(Map<String, Object> calcParams, String modeSysNameParamName, String sumParamName, List<Map<String, Object>> contrRiskList,
            String riskProdStructSysName) {
        boolean fFind = false;
        for (Map<String, Object> bean : contrRiskList) {
            if (bean.get("PRODRISKSYSNAME").toString().equalsIgnoreCase(riskProdStructSysName)) {
                if ((bean.get("INSAMVALUE") != null) && (Double.valueOf(bean.get("INSAMVALUE").toString()).doubleValue() > 0.0001)
                        && (bean.get("ISSELECTED") != null) && (Long.valueOf(bean.get("ISSELECTED").toString()).longValue() == 1)) {
                    calcParams.put(sumParamName, bean.get("INSAMVALUE"));
                    calcParams.put(modeSysNameParamName, "HAND_GSS");
                } else {
                    calcParams.put(sumParamName, 0.0);
                    calcParams.put(modeSysNameParamName, "AVOID_RISK");
                }
                fFind = true;
                break;
            }
        }
        if (!fFind) {
            calcParams.put(sumParamName, 0.0);
            calcParams.put(modeSysNameParamName, "AVOID_RISK");
        }
    }

    private String getProgramSysNameById(List<Map<String, Object>> prodProgramList, Long prodProgId) {
        for (Map<String, Object> bean : prodProgramList) {
            if (Long.valueOf(bean.get("PRODPROGID").toString()).longValue() == prodProgId.longValue()) {
                return bean.get("SYSNAME").toString();
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
    public Map<String, Object> dsB2BFirstStepCalcByContrMap(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BFirstStepCalcByContrMap");
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
            logger.debug("after dsB2BFirstStepCalcByContrMap");
            return result;
        }
        // данные договора корректны - можно выполнять вычисления
        Map<String, Object> contrExtMap = (Map<String, Object>) contractMap.get("CONTREXTMAP");
        Map<String, Object> productMap = (Map<String, Object>) params.get("FULLPRODMAP");
        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        calcParams.put("CALCVERID", productMap.get("CALCVERID"));
        calcParams.put("typeProdSysName", "FIRST_STEP");
        calcParams.put("salesChannelSysName", getSaleChannelName(productMap));
        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contractMap.get("INSOBJGROUPLIST");
        List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupList.get(0).get("OBJLIST");
        for (Map<String, Object> bean : objList) {
            Map<String, Object> insObjMap = (Map<String, Object>) bean.get("INSOBJMAP");
            if ((insObjMap != null) && (insObjMap.get("INSOBJSYSNAME") != null)) {
                Map<String, Object> contrObjMap = (Map<String, Object>) bean.get("CONTROBJMAP");
                List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                if (insObjMap.get("INSOBJSYSNAME").toString().equalsIgnoreCase("insuredAdult")) {
                    calcParams.put("genderInsured", insObjMap.get("gender"));
                    calcParams.put("issuredBirthDATE", insObjMap.get("birthDATE"));
//                    setCalcRiskMapping(calcParams, "accidentalDeathAdultModeSystem", "accidentalDeathAdultModeSystemValue", contrRiskList, "adultAccidentDeath");
                    setCalcRiskMapping(calcParams, "accidentalDeathAdultModeSystem", "accidentalDeathAdultModeSystemValue", contrRiskList, "FCC_DEATH_DUE_ACC_2_RB-FCC0");
//                    setCalcRiskMapping(calcParams, "accidentHospitalizationAdultModeSystem", "accidentHospitalizationAdultModeSystemValue", contrRiskList, "adultAccidentHosp");
                    setCalcRiskMapping(calcParams, "accidentHospitalizationAdultModeSystem", "accidentHospitalizationAdultModeSystemValue", contrRiskList, "FCC_HOSP_DUE_ACC_IA_2_RB-FCC0");
//                    setCalcRiskMapping(calcParams, "accidentsInjuriesAdultModeSystem", "accidentsInjuriesAdultModeSystemValue", contrRiskList, "adultAccidentInjured");
                    setCalcRiskMapping(calcParams, "accidentsInjuriesAdultModeSystem", "accidentsInjuriesAdultModeSystemValue", contrRiskList, "FCC_TRAUMA_DUE_ACC_IA_2_RB-FCC0");
//                    setCalcRiskMapping(calcParams, "accidentSurgeryAdultModeSystem", "accidentSurgeryAdultModeSystemValue", contrRiskList, "adultAccidentSurgery");
                    setCalcRiskMapping(calcParams, "accidentSurgeryAdultModeSystem", "accidentSurgeryAdultModeSystemValue", contrRiskList, "FCC_SURGICAL_DUE_ACC_IA_2_RB-FCC0");
//                    setCalcRiskMapping(calcParams, "deathOnPublicTransportAdultModeSystem", "deathOnPublicTransportAdultModeSystemValue", contrRiskList, "adultTransportDeath");
                    setCalcRiskMapping(calcParams, "deathOnPublicTransportAdultModeSystem", "deathOnPublicTransportAdultModeSystemValue", contrRiskList, "FCC_DEATH_DUE_ACC_TRANS_2_RB-FCC0");
//                    setCalcRiskMapping(calcParams, "diagnosingDangerousDiseasesAdultModeSystem", "diagnosingDangerousDiseasesAdultModeSystemValue", contrRiskList, "adultARI");
                    setCalcRiskMapping(calcParams, "diagnosingDangerousDiseasesAdultModeSystem", "diagnosingDangerousDiseasesAdultModeSystemValue", contrRiskList, "FCC_DIAGNOSIS_HIGH_RISK_IA_2_RB-FCC0");
//                    setCalcRiskMapping(calcParams, "disabilityThreeGroupsAdultModeSystem", "disabilityThreeGroupsAdultModeSystemValue", contrRiskList, "adultAccidentDisability");
                    setCalcRiskMapping(calcParams, "disabilityThreeGroupsAdultModeSystem", "disabilityThreeGroupsAdultModeSystemValue", contrRiskList, "FCC_DISABILITY_DUE_ACC_IA_2_RB-FCC0");
//                    setCalcRiskMapping(calcParams, "disabilityThreeGroupsAdultModeSystem", "disabilityThreeGroupsAdultModeSystemValue", contrRiskList, "adultAccidentDisabilityIll");
                    setCalcRiskMapping(calcParams, "disabilityThreeGroupsAdultModeSystem", "disabilityThreeGroupsAdultModeSystemValue", contrRiskList, "FCC_DISABILITY_IA_2_RB-FCC0");
                } else if (insObjMap.get("INSOBJSYSNAME").toString().equalsIgnoreCase("insuredChild")) {
                    calcParams.put("genderInsurrer", insObjMap.get("gender"));
                    calcParams.put("insurrerBirthDATE", insObjMap.get("birthDATE"));
//                    setCalcRiskMapping(calcParams, "diagnosingDangerousDiseasesChildrenModeSystem", "diagnosingDangerousDiseasesChildrenModeSystemValue", contrRiskList, "childARI");
                    setCalcRiskMapping(calcParams, "diagnosingDangerousDiseasesChildrenModeSystem", "diagnosingDangerousDiseasesChildrenModeSystemValue", contrRiskList, "FCC_DIAGNOSIS_HIGH_RISK_IC_2_RB-FCC0");
//                    setCalcRiskMapping(calcParams, "disabilityAndInjuryChildrenModeSystem", "disabilityAndInjuryChildrenModeSystemValue", contrRiskList, "childAccidentDisabilityInjury");
                    setCalcRiskMapping(calcParams, "disabilityAndInjuryChildrenModeSystem", "disabilityAndInjuryChildrenModeSystemValue", contrRiskList, "FCC_DISABILITY_DUE_ACC_IC_2_RB-FCC0");
                }
            }
        }
        calcParams.put("termIns", getTermInYearsById(productMap, Long.valueOf(contractMap.get("TERMID").toString())));
        calcParams.put("paymentVariantSysName", getPaymentVariantSysNameById(productMap, Long.valueOf(contractMap.get("PAYVARID").toString())));
        if (contractMap.get("INSAMCURRENCYID") != null) {
            calcParams.put("currencySysName", getCurrencySysNameById(Long.valueOf(contractMap.get("INSAMCURRENCYID").toString()), login, password));
        }
        calcParams.put("persen", Double.valueOf(contrExtMap.get("expectedReturnPerc").toString()));
        Map<String, Object> prodVerMap = (Map<String, Object>) productMap.get("PRODVER");
        List<Map<String, Object>> prodProgramList = (List<Map<String, Object>>) prodVerMap.get("PRODPROGS");
        String prodProgSysName = getProgramSysNameById(prodProgramList, Long.valueOf(contractMap.get("PRODPROGID").toString()));
        boolean calcFromFee = false;
        if (prodProgSysName.equalsIgnoreCase("FS_BASIC")) {
            calcParams.put("insFee", Double.valueOf(contrExtMap.get("premValNoRisk").toString()));
            calcParams.put("gurSum", 5000000.0);
            calcFromFee = true;
        } else {
            calcParams.put("insFee", 0.0);
            calcParams.put("gurSum", Double.valueOf(contrExtMap.get("guarInsAmValNoRisk").toString()));
        }
        calcParams.put("gurSumProgInsuredExemPayModeSystem", "ON");
        calcParams.put("modeSysName", "HAND_GSS");
        Map<String, Object> resMap = this.callService(Constants.B2BPOSWS, "dsB2BFirstStepCalc", calcParams, login, password);
        // обработка результата калькулятора
        if (resMap.get("TotalInsurancePremium") != null) {
            contractMap.put("PREMVALUE", resMap.get("TotalInsurancePremium"));
            //contractMap.put("INSAMVALUE", resMap.get(""));
            if (calcFromFee) {
                contrExtMap.put("guarInsAmValNoRisk", resMap.get("GurSumProgSurvival"));
                contrExtMap.put("expectedInsAmValNoRisk", resMap.get("ExpectedAmountOfInsProgSurvivalFirst"));
            } else {
                contrExtMap.put("premValNoRisk", resMap.get("InsurranseFeeProgSurvival"));
                contrExtMap.put("expectedInsAmValNoRisk", resMap.get("ExpectedAmountOfInsProgSurvivalSecond"));
            }
            for (Map<String, Object> bean : objList) {
                Map<String, Object> insObjMap = (Map<String, Object>) bean.get("INSOBJMAP");
                if ((insObjMap != null) && (insObjMap.get("INSOBJSYSNAME") != null)) {
                    Map<String, Object> contrObjMap = (Map<String, Object>) bean.get("CONTROBJMAP");
                    List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                    if (insObjMap.get("INSOBJSYSNAME").toString().equalsIgnoreCase("insuredAdult")) {
                        //setResCalcRiskMapping(contrRiskList, "adultDisab12Death", resMap, null,
                        setResCalcRiskMapping(contrRiskList, "FCC_EXEMPTION_PAYMENT_RB-FCC0", resMap, null,
                                "InsurranseFeeProgInsuredExemptionOfPayment", false);
                        //setResCalcRiskMapping(contrRiskList, "adultDeath", resMap, null,
                        setResCalcRiskMapping(contrRiskList, "FCC_EXEMPTION_PAYMENT_DEATH_RB-FCC0", resMap, null,
                                "InsurranseFeeProgInsuredExemptionOfPayment", false);
                        if ((resMap.get("InsurranseFeeProgInsuredExemptionOfPayment") != null)
                                && (Double.valueOf(resMap.get("InsurranseFeeProgInsuredExemptionOfPayment").toString()).doubleValue() > 0.0001)) {
                            contrExtMap.put("freePaymentsOfInsurer", 1L);
                        } else {
                            contrExtMap.put("freePaymentsOfInsurer", 0L);
                        }
//                        setResCalcRiskMapping(contrRiskList, "adultAccidentDeath", resMap, "GurSumProgAccidentalDeathAdult",
                        setResCalcRiskMapping(contrRiskList, "FCC_DEATH_DUE_ACC_2_RB-FCC0", resMap, "GurSumProgAccidentalDeathAdult",
                                "InsurranseFeeProgAccidentalDeathAdult", true);
//                        setResCalcRiskMapping(contrRiskList, "adultAccidentHosp", resMap, "GurSumProgAccidentHospitalizationAdult",
                        setResCalcRiskMapping(contrRiskList, "FCC_HOSP_DUE_ACC_IA_2_RB-FCC0", resMap, "GurSumProgAccidentHospitalizationAdult",
                                "InsurranseFeeProgAccidentHospitalization", true);
//                        setResCalcRiskMapping(contrRiskList, "adultAccidentInjured", resMap, "GurSumProgAccidentsInjuriesAdult",
                        setResCalcRiskMapping(contrRiskList, "FCC_TRAUMA_DUE_ACC_IA_2_RB-FCC0", resMap, "GurSumProgAccidentsInjuriesAdult",
                                "InsurranseFeeProgAccidentsInjuries", true);
//                        setResCalcRiskMapping(contrRiskList, "adultAccidentSurgery", resMap, "GurSumProgAccidentSurgeryAdult",
                        setResCalcRiskMapping(contrRiskList, "FCC_SURGICAL_DUE_ACC_IA_2_RB-FCC0", resMap, "GurSumProgAccidentSurgeryAdult",
                                "InsurranseFeeProgAccidentSurgery", true);
//                        setResCalcRiskMapping(contrRiskList, "adultTransportDeath", resMap, "GurSumProgDeathOnPublicTransportAdult",
                        setResCalcRiskMapping(contrRiskList, "FCC_DEATH_DUE_ACC_TRANS_2_RB-FCC0", resMap, "GurSumProgDeathOnPublicTransportAdult",
                                "InsurranseFeeProgDeathOnPublicTransport", true);
//                        setResCalcRiskMapping(contrRiskList, "adultARI", resMap, "GurSumProgDiagnosingDangerousDiseasesAdult",
                        setResCalcRiskMapping(contrRiskList, "FCC_DIAGNOSIS_HIGH_RISK_IA_2_RB-FCC0", resMap, "GurSumProgDiagnosingDangerousDiseasesAdult",
                                "InsurranseFeeProgDiagnosingDangerousDiseasesAdult", true);
//                        setResCalcRiskMapping(contrRiskList, "adultAccidentDisability", resMap, "GurSumProgDisabilityThreeGroupsAdult",
                        setResCalcRiskMapping(contrRiskList, "FCC_DISABILITY_DUE_ACC_IA_2_RB-FCC0", resMap, "GurSumProgDisabilityThreeGroupsAdult",
                                "InsurranseFeeProgDisabilityThreeGroupsAdult", true);
//                        setResCalcRiskMapping(contrRiskList, "adultAccidentDisabilityIll", resMap, "GurSumProgDisabilityThreeGroupsAdult",
                        setResCalcRiskMapping(contrRiskList, "FCC_DISABILITY_IA_2_RB-FCC0", resMap, "GurSumProgDisabilityThreeGroupsAdult",
                                "InsurranseFeeProgDisabilityThreeGroupsAdult", true);
                    } else if (insObjMap.get("INSOBJSYSNAME").toString().equalsIgnoreCase("insuredChild")) {
                        for (Map<String, Object> riskBean : contrRiskList) {
                            //if (riskBean.get("PRODRISKSYSNAME").toString().equalsIgnoreCase("childMain")) {
                            if (riskBean.get("PRODRISKSYSNAME").toString().equalsIgnoreCase("FCC_MAIN_PROGRAM_2_RB-FCC0")) {
                                riskBean.put("INSAMVALUE", contrExtMap.get("guarInsAmValNoRisk"));
                                riskBean.put("PREMVALUE", contrExtMap.get("premValNoRisk"));
                            }
                        }
//                        setResCalcRiskMapping(contrRiskList, "childARI", resMap, "GurSumProgDiagnosingDangerousDiseasesChildren",
                        setResCalcRiskMapping(contrRiskList, "FCC_DIAGNOSIS_HIGH_RISK_IC_2_RB-FCC0", resMap, "GurSumProgDiagnosingDangerousDiseasesChildren",
                                "InsurranseFeeProgDiagnosingDangerousDiseases", true);
//                        setResCalcRiskMapping(contrRiskList, "childAccidentDisabilityInjury", resMap, "GurSumProgDisabilityAndInjuryChildren",
                        setResCalcRiskMapping(contrRiskList, "FCC_DISABILITY_DUE_ACC_IC_2_RB-FCC0", resMap, "GurSumProgDisabilityAndInjuryChildren",
                                "InsurranseFeeProgDisabilityAndInjuryChildren", true);
                    }
                }
            }
            // обработка выкупных сумм
            List<Map<String, Object>> calcList = (List<Map<String, Object>>) resMap.get("CALCLIST");
            for (Map<String, Object> bean : calcList) {
                if (Long.valueOf(bean.get("year").toString()).longValue() > 0) {
                    contrExtMap.put("redemptionSumYear" + bean.get("year").toString(), bean.get("RedemptionSum"));
                }
            }
        }
        //
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRMAP", contractMap);
        logger.debug("after dsB2BFirstStepCalcByContrMap");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BFirstStepContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BFirstStepContractPrepareToSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }

        // todo: включить валидацию по завершению разработки и тестирования
        boolean isDataValid = this.validateContractSaveParams(contract, false, login, password);
//        boolean isDataValid = true;
        Map<String, Object> result;
        if (isDataValid) {
            result = genAdditionalSaveParams(contract, login, password);
            //geterateContractSerNum(contract, login, password); // перенесено в B2BLifeBaseFacade.genAdditionalSaveParams
            //getFinishDateByStartDateAndTermId(contract, login, password); // перенесено в B2BLifeBaseFacade.genAdditionalSaveParams
            //result = contract;
        } else {
            result = contract;
        }
        logger.debug("after dsB2BFirstStepContractPrepareToSave");
        return result;
    }

    @Override
    protected void validateContractExtValues(Map<String, Object> contract, Map<String, Object> contractExtValues, boolean isFixContr, StringBuffer errorText) {
        checkContractExtValueExist(contractExtValues, "expectedReturn", "Ожидаемая доходность", errorText);
    }

    // возвращает имя ключа, указывающего на дату рождения основного застрахованного по договору (взрослого)
    // мапу в которой данный ключ нужно искать задается методом getInsuredAdultBirthDate
    @Override
    protected String getInsuredAdultBirthDateFieldName() {
        return "birthDATE";
    }

    // получает дату рождения основного застрахованного по договору (взрослого)
    @Override
    protected Date getInsuredAdultBirthDate(Map<String, Object> contract) {
        Date insuredBirthDate = null;

        Map<String, Object> insuredAdultInsObj = getInsObjBySysNameFromContract(contract, "insuredAdult");

        if (insuredAdultInsObj != null) {
            String insuredBirthDateFieldName = getInsuredAdultBirthDateFieldName();
            Object insuredBirthDateObj = insuredAdultInsObj.get(insuredBirthDateFieldName);
            if (insuredBirthDateObj != null) {
                insuredBirthDate = (Date) parseAnyDate(insuredBirthDateObj, Date.class, insuredBirthDateFieldName, true);
            }
        }

        return insuredBirthDate;
    }

    // дополнительная проверка договора (особая для текущего продукта, в дополнение к стандартной)
    @Override
    protected void validateAdditionalContractSaveParams(Map<String, Object> contract, StringBuffer errorText, String login, String password) throws Exception {
        logger.debug("Additional contract validation for 'First step'...");

        Map<String, Object> insuredAdultInsObj = getInsObjBySysNameFromContract(contract, "insuredAdult");
        Map<String, Object> insuredChildInsObj = getInsObjBySysNameFromContract(contract, "insuredChild");

        checkInsuredObjExtAttrs(insuredAdultInsObj, "взрослого", errorText);
        checkInsuredObjExtAttrs(insuredChildInsObj, "ребенка", errorText);

        if (insuredChildInsObj != null) {

            Date startDate = (Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE", true);

            Date insuredChildBirthDate = null;
            String insuredBirthDateFieldName = getInsuredAdultBirthDateFieldName();
            Object insuredBirthDateObj = insuredChildInsObj.get(insuredBirthDateFieldName);
            if (insuredBirthDateObj != null) {
                insuredChildBirthDate = (Date) parseAnyDate(insuredBirthDateObj, Date.class, insuredBirthDateFieldName, true);
            }

            // проверки 'Застрахованный ребенок (ЗР), возраст на дату начала срока страхования от Х до Y полных лет, но не более Z полных лет на дату окончания срока страхования;'
            if ((startDate != null) && (insuredChildBirthDate != null)) {

                Map<String, Object> product = getFullProductDataFromContract(contract, login, password);
                // формирование мапы PRODDEFVAL-ов из продукта
                Map<String, Object> prodDefValsMap = getProdDefValsMapFromProduct(product);

                // проверка на 'Застрахованный ребенок (ЗР), возраст на дату начала срока страхования до Y полных лет'
                Long сhildAgeOnContrStartMax = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "REJECT_CHILDAGE_ONCONTRACTSTART_MAX");
                if (сhildAgeOnContrStartMax != null) {
                    GregorianCalendar childBirthDatePlusMaxStartAgeGC = new GregorianCalendar();
                    childBirthDatePlusMaxStartAgeGC.setTime(insuredChildBirthDate);
                    childBirthDatePlusMaxStartAgeGC.add(Calendar.YEAR, сhildAgeOnContrStartMax.intValue());
                    if (startDate.after(childBirthDatePlusMaxStartAgeGC.getTime())) {
                        errorText.append("Возраст застрахованного ребенка на начало срока страхования более допустимого. ");
                    }
                }

                // проверка на 'Застрахованный ребенок (ЗР), возраст на дату начала срока страхования до Y полных лет'
                Long childAgeOnContrStartMin = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "REJECT_CHILDAGE_ONCONTRACTSTART_MIN");
                if (childAgeOnContrStartMin != null) {
                    GregorianCalendar childBirthDatePlusMinStartAgeGC = new GregorianCalendar();
                    childBirthDatePlusMinStartAgeGC.setTime(insuredChildBirthDate);
                    childBirthDatePlusMinStartAgeGC.add(Calendar.YEAR, childAgeOnContrStartMin.intValue());
                    if (startDate.before(childBirthDatePlusMinStartAgeGC.getTime())) {
                        errorText.append("Возраст застрахованного ребенка на начало срока страхования менее допустимого. ");
                    }
                }

                // проверка 'Возраст застрахованного лица на дату окончания договора не должен превышать Х полных лет'
                Long childAgeOnContrFinishMax = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "REJECT_CHILDAGE_ONCONTRACTFINISH_MAX");
                if (childAgeOnContrFinishMax != null) {

                    Long termID = getLongParamLogged(contract, "TERMID");

                    if (termID != null) {

                        Map<String, Object> termInfo = getTermDataByTermID(termID, login, password);
                        getStringParamLogged(termInfo, "NAME"); // для протокола
                        Long termYearCount = getLongParamLogged(termInfo, "YEARCOUNT");
                        Long termMonthCount = getLongParamLogged(termInfo, "MONTHCOUNT");
                        Long termDayCount = getLongParamLogged(termInfo, "DAYCOUNT");

                        GregorianCalendar childBirthDatePlusMaxFinishAgeGC = new GregorianCalendar();
                        childBirthDatePlusMaxFinishAgeGC.setTime(insuredChildBirthDate);
                        childBirthDatePlusMaxFinishAgeGC.add(Calendar.YEAR, childAgeOnContrFinishMax.intValue());

                        GregorianCalendar finishDateGC = new GregorianCalendar();
                        finishDateGC.setTime(startDate);
                        if (termYearCount != null) {
                            finishDateGC.add(Calendar.YEAR, termYearCount.intValue());
                        }
                        if (termMonthCount != null) {
                            finishDateGC.add(Calendar.MONTH, termMonthCount.intValue());
                        }
                        if (termDayCount != null) {
                            finishDateGC.add(Calendar.DAY_OF_YEAR, termDayCount.intValue());
                        }

                        if (finishDateGC.after(childBirthDatePlusMaxFinishAgeGC)) {
                            errorText.append("Возраст застрахованного ребенка на конец срока страхования более допустимого. ");
                        }
                    }
                }
            }

        }

        logger.debug("Additional contract validation for 'First step' finished.");
    }

    private void checkInsuredObjExtAttrs(Map<String, Object> insuredInsObj, String insuredNote, StringBuffer errorText) {
        if (insuredInsObj == null) {
            errorText.append("Не найдены показатели для застрахованного ").append(insuredNote).append(". ");
        } else {
            String valueNote = "Для застрахованного " + insuredNote + " не указано значение показателя";
            checkBeanValueExist(insuredInsObj, valueNote, "birthDATE", "Дата рождения", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "gender", "Пол", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "height", "Рост", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "weight", "Вес", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "bloodPressureLower", "Нижнее артериальное давление", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "bloodPressureTop", "Верхнее артериальное давление", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "insuredDeclCompliance", "Клиент соответствует декларации", errorText);
        }
    }

    // проверка показателей здоровья для всех застрахованных по договору
    // переопределен стандартный метод, т.к. требуется проверить несколько застрахованных (с системными именами объекта страхования 'insuredAdult' и 'insuredChild')
    @Override
    protected Long checkAllInsuredHealthForUW(Map<String, Object> contract) {
        // проверка показателей здоровья взрослого (по системному имени объекта страхования)
        Long UW = checkInsuredHealthForUW(contract, "insuredAdult");
        if (UW.equals(UW_DO_NOT_NEEDED)) {
            // проверка показателей здоровья ребенка (по системному имени объекта страхования)
            UW = checkInsuredHealthForUW(contract, "insuredChild");
        }
        return UW;
    }

    // получение списка всех застрахованных, для простых однотипных проверок
    // метод переопределен для текущего продукта, т.к. требуется получение нескольких застрахованных (INSUREDMAP, INSURERMAP)
    @Override
    protected List<Map<String, Object>> getAllInsuredList(Map<String, Object> contract) {
        List<Map<String, Object>> allInsuredList = new ArrayList<Map<String, Object>>();

        // застрахованный ребенок
        Map<String, Object> insured = (Map<String, Object>) contract.get("INSUREDMAP");
        allInsuredList.add(insured);
        // застрахованный взрослый, он же страхователь
        Map<String, Object> insurer = (Map<String, Object>) contract.get("INSURERMAP");
        allInsuredList.add(insurer);

        return allInsuredList;
    }

    // проверка наличия или премии или страховой суммы по договору (одна из сумм обязательна)
    // метод переопределен для текущего продукта, т.к. проверка отличется от стандартной
    @Override
    protected boolean checkContractPremValue(Map<String, Object> contract, boolean isPreCalcCheck, StringBuffer errorText) {
        if (isPreCalcCheck) {
            boolean isPremValueExists = checkBeanValueExist(contract, "PREMVALUE");
            if (!isPremValueExists) {
                logger.debug("No premium value (PREMVALUE) was found in contract data - checking insurance amount value (CONTREXTMAP.guarInsAmValNoRisk) or premium value (CONTREXTMAP.premValNoRisk)...");
                boolean isInsAmValExists = false;
                Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
                if (contrExtMap != null) {
                    isPremValueExists = checkBeanValueExist(contrExtMap, "premValNoRisk");
                    isInsAmValExists = checkBeanValueExist(contrExtMap, "guarInsAmValNoRisk");
                }
                if (!isPremValueExists && !isInsAmValExists) {
                    logger.debug("No premium value (CONTREXTMAP.premValNoRisk) and no insurance amount value (CONTREXTMAP.guarInsAmValNoRisk) was found in contract extended attributes data either - this contract is invalid.");
                    errorText.append("Не указаны ни размер страхового взноса ни величина гарантированной страховой суммы. ");
                }
            }
            return isPremValueExists;
        } else {
            return checkContractValueExist(contract, "PREMVALUE", "Страховой взнос", errorText);
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BFirstStepContractUnderwritingCheck(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BFirstStepContractUnderwritingCheck");
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
        logger.debug("after dsB2BFirstStepContractUnderwritingCheck");
        return result;
    }

}
