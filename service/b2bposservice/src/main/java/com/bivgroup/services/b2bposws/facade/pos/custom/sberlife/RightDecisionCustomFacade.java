package com.bivgroup.services.b2bposws.facade.pos.custom.sberlife;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
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
@BOName("RightDecisionCustom")
public class RightDecisionCustomFacade extends B2BLifeBaseFacade {

    private final Logger logger = Logger.getLogger(RightDecisionCustomFacade.class);

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
    public Map<String, Object> dsRightDecisionContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsRightDecisionContractPrepareToSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }

        logger.debug("after dsRightDecisionContractPrepareToSave");
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
     currencySysName - сиснейм валюты
     saleChannelSysName - канал продаж
     insurerIsInsured - страхователь является застрахованным
     genderInsured - пол застрахованного (0, 1)
     insuredBirthDATE - дата рождения застрахованного
     insurancePeriod - срок страхования
     frequeOfPaymentSysName - периодичность выплат
     expectedProfit - ожидаемая доходность, double, процент доходности 0.05 например
     CalcFromInsuranceFee_insuranceFee - размер страхового взноса (расчет от страхового взноса)
     CalcFromInsuranceSumm_guarantedInsuranceSumm_BaseProgram - размер страховой суммы по основной программе (расчет от страховой суммы)
     RandomDeathKoef - коэффициент по программе случайная смерть, 0 (выключ), 1, 2, 3, целое. Умножает страховую сумму на него
     AllRisksModeSysName - Режим по всем рискам "Максимальные ГСС", "Ручной ввод"
     */
    @WsMethod(requiredParams = {"CALCVERID", "currencySysName", "saleChannelSysName", "insurerIsInsured", "genderInsured", "insuredBirthDATE",
        "insurancePeriod", "frequeOfPaymentSysName", "expectedProfit"})
    public Map<String, Object> dsB2BRightDecisionCalc(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BRightDecisionCalc");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.putAll(params);
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Long insPeriod = (Long) params.get("insurancePeriod");
        List<Map<String, Object>> yearList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i <= insPeriod.intValue(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("year", i);
            map.put("rowNum", String.valueOf(i + 1));
            yearList.add(map);
        }
        calcParams.put("CALCLIST", yearList);
        calcParams.put("ageInsured", calcAge((Date) parseAnyDate(params.get("insuredBirthDATE"), Date.class, "insuredBirthDATE")));
        logger.debug("calcParams: " + calcParams);
        Map<String, Object> calcRes = this.callService(Constants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcParams, login, password);
        logger.debug("calcRes: " + calcRes);

        logger.debug("after dsB2BRightDecisionCalc");
        return calcRes;
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
                    calcParams.put(modeSysNameParamName, "ISMANUAL");
                } else {
                    calcParams.put(sumParamName, 0.0);
                    calcParams.put(modeSysNameParamName, "DISABLE");
                }
                fFind = true;
                break;
            }
        }
        if (!fFind) {
            calcParams.put(sumParamName, 0.0);
            calcParams.put(modeSysNameParamName, "DISABLE");
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

    private String getProgramSysNameById(List<Map<String, Object>> prodProgramList, Long prodProgId) {
        for (Map<String, Object> bean : prodProgramList) {
            if (Long.valueOf(bean.get("PRODPROGID").toString()).longValue() == prodProgId.longValue()) {
                return bean.get("SYSNAME").toString();
            }
        }
        return null;
    }

    @WsMethod(requiredParams = {"CONTRMAP", "FULLPRODMAP"})
    public Map<String, Object> dsB2BRightDecisionCalcByContrMap(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BRightDecisionCalcByContrMap");
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
            logger.debug("after dsB2BRightDecisionCalcByContrMap");
            return result;
        }
        // данные договора корректны - можно выполнять вычисления
        Map<String, Object> contrExtMap = (Map<String, Object>) contractMap.get("CONTREXTMAP");
        Map<String, Object> productMap = (Map<String, Object>) params.get("FULLPRODMAP");
        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        calcParams.put("CALCVERID", productMap.get("CALCVERID"));
        if (contractMap.get("INSAMCURRENCYID") != null) {
            calcParams.put("currencySysName", getCurrencySysNameById(Long.valueOf(contractMap.get("INSAMCURRENCYID").toString()), login, password));
        }
        calcParams.put("saleChannelSysName", getSaleChannelName(productMap));
        calcParams.put("insurerIsInsured", contrExtMap.get("insurerIsInsured"));
        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contractMap.get("INSOBJGROUPLIST");
        List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupList.get(0).get("OBJLIST");
        Map<String, Object> insObjMap = (Map<String, Object>) objList.get(0).get("INSOBJMAP");
        calcParams.put("genderInsured", insObjMap.get("gender"));
        calcParams.put("insuredBirthDATE", insObjMap.get("birthDATE"));
        calcParams.put("insurancePeriod", getTermInYearsById(productMap, Long.valueOf(contractMap.get("TERMID").toString())));
        calcParams.put("frequeOfPaymentSysName", getPaymentVariantSysNameById(productMap, Long.valueOf(contractMap.get("PAYVARID").toString())));
        calcParams.put("expectedProfit", Double.valueOf(contrExtMap.get("expectedReturnPerc").toString()));
        Map<String, Object> prodVerMap = (Map<String, Object>) productMap.get("PRODVER");
        List<Map<String, Object>> prodProgramList = (List<Map<String, Object>>) prodVerMap.get("PRODPROGS");
        String prodProgSysName = getProgramSysNameById(prodProgramList, Long.valueOf(contractMap.get("PRODPROGID").toString()));
        boolean calcFromFee = false;
        if (prodProgSysName.equalsIgnoreCase("RD_BASIC")) {
            calcParams.put("CalcFromInsuranceFee_InsuranceFee", Double.valueOf(contrExtMap.get("premValNoRisk").toString()));
            calcParams.put("CalcFromInsuranceSumm_GuarantedInsuranceSumm_BaseProgram", 5000000.0);
            calcFromFee = true;
        } else {
            calcParams.put("CalcFromInsuranceFee_InsuranceFee", 0.0);
            calcParams.put("CalcFromInsuranceSumm_GuarantedInsuranceSumm_BaseProgram", Double.valueOf(contrExtMap.get("guarInsAmValNoRisk").toString()));
        }
        Double riderDesiredMultValue = Double.valueOf(contrExtMap.get("riderDesiredMultValue").toString());
        calcParams.put("RandomDeathKoef", riderDesiredMultValue);
        calcParams.put("AllRisksModeSysName", "ISMANUAL");
        Map<String, Object> contrObjMap = (Map<String, Object>) objList.get(0).get("CONTROBJMAP");
        if (contrObjMap != null) {
            List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
            //setCalcRiskMapping(calcParams, "diagDangerDiseaseModeSysName", "diagDangerDiseaseManualSumm", contrRiskList, "ARI");
            setCalcRiskMapping(calcParams, "diagDangerDiseaseModeSysName", "diagDangerDiseaseManualSumm", contrRiskList, "FCC_DIAGNOSIS_HIGH_RISK_IA_2_RB-FCC0");
//            setCalcRiskMapping(calcParams, "deathAccidentModeSysName", "deathAccidentManualSumm", contrRiskList, "accidentDeath");
            setCalcRiskMapping(calcParams, "deathAccidentModeSysName", "deathAccidentManualSumm", contrRiskList, "FCC_DEATH_DUE_ACC_2_RB-FCC0");
//            setCalcRiskMapping(calcParams, "deathPublicTransportModeSysName", "deathPublicTransportManualSumm", contrRiskList, "transportDeath");
            setCalcRiskMapping(calcParams, "deathPublicTransportModeSysName", "deathPublicTransportManualSumm", contrRiskList, "FCC_DEATH_DUE_ACC_TRANS_2_RB-FCC0");
//            setCalcRiskMapping(calcParams, "disabilityModeSysName", "disabilityManualSumm", contrRiskList, "accidentDisability");
            setCalcRiskMapping(calcParams, "disabilityModeSysName", "disabilityManualSumm", contrRiskList, "FCC_DISABILITY_DUE_ACC_IA_2_RB-FCC0");
//            setCalcRiskMapping(calcParams, "disabilityModeSysName", "disabilityManualSumm", contrRiskList, "accidentDisabilityIll");
            setCalcRiskMapping(calcParams, "disabilityModeSysName", "disabilityManualSumm", contrRiskList, "FCC_DISABILITY_IA_2_RB-FCC0");
//            setCalcRiskMapping(calcParams, "injuriesAccidentModeSysName", "injuriesAccidentManualSumm", contrRiskList, "accidentInjured");
            setCalcRiskMapping(calcParams, "injuriesAccidentModeSysName", "injuriesAccidentManualSumm", contrRiskList, "FCC_TRAUMA_DUE_ACC_IA_2_RB-FCC0");
//            setCalcRiskMapping(calcParams, "hospitalizationAccidentModeSysName", "hospitalizationAccidentManualSumm", contrRiskList, "accidentHosp");
            setCalcRiskMapping(calcParams, "hospitalizationAccidentModeSysName", "hospitalizationAccidentManualSumm", contrRiskList, "FCC_HOSP_DUE_ACC_IA_2_RB-FCC0");
//            setCalcRiskMapping(calcParams, "surgeryAccidentModeSysName", "surgeryAccidentManualSumm", contrRiskList, "accidentSurgery");
            setCalcRiskMapping(calcParams, "surgeryAccidentModeSysName", "surgeryAccidentManualSumm", contrRiskList, "FCC_SURGICAL_DUE_ACC_IA_2_RB-FCC0");
        }
        Map<String, Object> resMap = this.callService(Constants.B2BPOSWS, "dsB2BRightDecisionCalc", calcParams, login, password);
        // обработка результата калькулятора
        if (resMap.get("TotalInsuranceFee_AllRisks") != null) {
            contractMap.put("PREMVALUE", resMap.get("TotalInsuranceFee_AllRisks"));
            //contractMap.put("INSAMVALUE", resMap.get(""));
            if (calcFromFee) {
                contrExtMap.put("guarInsAmValNoRisk", resMap.get("FromInsuranceFee_GuarantedInsuranceSumm_BaseProgram"));
                contrExtMap.put("expectedInsAmValNoRisk", resMap.get("FromInsuranceFee_GuarantedInsuranceSumm_BaseProgram"));
            } else {
                contrExtMap.put("premValNoRisk", resMap.get("FromInsuranceSumm_InsuranceFee_BaseProgram"));
                contrExtMap.put("expectedInsAmValNoRisk", resMap.get("FromInsuranceSumm_ExpectedInsuranceSumm_BaseProgram"));
            }
            if (riderDesiredMultValue > 0.0001) {
                List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                //setResCalcRiskMapping(contrRiskList, "addIns", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_RandomDeath",
                setResCalcRiskMapping(contrRiskList, "FCC_TERM_INSURANCE_RB-FCC0", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_RandomDeath",
                        "FromInsuranceSumm_InsuranceFee_RandomDeath", true);
            }
            if (contrObjMap != null) {
                List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                for (Map<String, Object> bean : contrRiskList) {
                    //if (bean.get("PRODRISKSYSNAME").toString().equalsIgnoreCase("mixedLifeIns")) {
                    if (bean.get("PRODRISKSYSNAME").toString().equalsIgnoreCase("FCC_MAIN_PROGRAM_2_RB-FCC0")) {
                        bean.put("INSAMVALUE", contrExtMap.get("guarInsAmValNoRisk"));
                        bean.put("PREMVALUE", contrExtMap.get("premValNoRisk"));
                        break;
                    }
                }
                //setResCalcRiskMapping(contrRiskList, "ARI", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_DiagDangerDisease",
                setResCalcRiskMapping(contrRiskList, "FCC_DIAGNOSIS_HIGH_RISK_IA_2_RB-FCC0", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_DiagDangerDisease",
                        "FromInsuranceSumm_InsuranceFee_DiagDangerDisease", true);
//                setResCalcRiskMapping(contrRiskList, "accidentDeath", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_DeathAccident",
                setResCalcRiskMapping(contrRiskList, "FCC_DEATH_DUE_ACC_2_RB-FCC0", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_DeathAccident",
                        "FromInsuranceSumm_InsuranceFee_DeathAccident", true);
                //setResCalcRiskMapping(contrRiskList, "transportDeath", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_DeathPublicTransport",
                setResCalcRiskMapping(contrRiskList, "FCC_DEATH_DUE_ACC_TRANS_2_RB-FCC0", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_DeathPublicTransport",
                        "FromInsuranceSumm_InsuranceFee_DeathPublicTransport", true);
//                setResCalcRiskMapping(contrRiskList, "accidentDisability", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_Disability",
                setResCalcRiskMapping(contrRiskList, "FCC_DISABILITY_DUE_ACC_IA_2_RB-FCC0", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_Disability",
                        "FromInsuranceSumm_InsuranceFee_Disability", true);
//                setResCalcRiskMapping(contrRiskList, "accidentDisabilityIll", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_Disability",
                setResCalcRiskMapping(contrRiskList, "FCC_DISABILITY_IA_2_RB-FCC0", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_Disability",
                        "FromInsuranceSumm_InsuranceFee_Disability", true);
//                setResCalcRiskMapping(contrRiskList, "accidentInjured", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_InjuriesAccident",
                setResCalcRiskMapping(contrRiskList, "FCC_TRAUMA_DUE_ACC_IA_2_RB-FCC0", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_InjuriesAccident",
                        "FromInsuranceSumm_InsuranceFee_InjuriesAccident", true);
//                setResCalcRiskMapping(contrRiskList, "accidentHosp", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_HospitalizationAccident",
                setResCalcRiskMapping(contrRiskList, "FCC_HOSP_DUE_ACC_IA_2_RB-FCC0", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_HospitalizationAccident",
                        "FromInsuranceSumm_InsuranceFee_HospitalizationAccident", true);
//                setResCalcRiskMapping(contrRiskList, "accidentSurgery", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_SurgeryAccident",
                setResCalcRiskMapping(contrRiskList, "FCC_SURGICAL_DUE_ACC_IA_2_RB-FCC0", resMap, "FromInsuranceSumm_GuarantedInsuranceSumm_SurgeryAccident",
                        "FromInsuranceSumm_InsuranceFee_SurgeryAccident", true);
//                setResCalcRiskMapping(contrRiskList, "insPremExemption", resMap, null,
                setResCalcRiskMapping(contrRiskList, "FCC_EXEMPTION_PAYMENT_DISAB_RB-FCC0", resMap, null,
                        "FromInsuranceSumm_InsuranceFee_FreePaymentsOfInsurer", false);
                if (resMap.get("FreePaymentsOfInsurer").toString().equalsIgnoreCase("Включено")) {
                    contrExtMap.put("freePaymentsOfInsurer", 1L);
                } else {
                    contrExtMap.put("freePaymentsOfInsurer", 0L);
                }
                // обработка выкупных сумм
                List<Map<String, Object>> calcList = (List<Map<String, Object>>) resMap.get("CALCLIST");
                for (Map<String, Object> bean : calcList) {
                    if (Long.valueOf(bean.get("year").toString()).longValue() > 0) {
                        contrExtMap.put("redemptionSumYear" + bean.get("year").toString(), bean.get("TotalRedemptionSumm"));
                    }
                }
            }
        }
        //
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRMAP", contractMap);
        logger.debug("after dsB2BRightDecisionCalcByContrMap");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BRightDecisionContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BRightDecisionContractPrepareToSave");
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
        logger.debug("after dsB2BRightDecisionContractPrepareToSave");
        return result;
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

        Map<String, Object> insuredAdultInsObj = getInsObjBySysNameFromContract(contract, "insured");

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
        logger.debug("Additional contract validation for 'Right Decision'...");

        Map<String, Object> insuredInsObj = getInsObjBySysNameFromContract(contract, "insured");

        checkInsuredObjExtAttrs(insuredInsObj, "лица", errorText);

        logger.debug("Additional contract validation for 'Right Decision' finished.");
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
    public Map<String, Object> dsB2BRightDecisionContractUnderwritingCheck(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BRightDecisionContractUnderwritingCheck");
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
        logger.debug("after dsB2BRightDecisionContractUnderwritingCheck");
        return result;
    }

}
