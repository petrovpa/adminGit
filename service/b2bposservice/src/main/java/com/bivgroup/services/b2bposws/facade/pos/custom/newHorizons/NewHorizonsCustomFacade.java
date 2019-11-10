/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.newHorizons;

import com.bivgroup.services.b2bposws.facade.B2BContractLifeBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.util.CopyUtils;

/**
 * @author kkulkov
 */
@BOName("NewHorizonsCustom")
public class NewHorizonsCustomFacade extends B2BContractLifeBaseFacade {

    private static final String DEF_PROG_NAME = "NH_BASIC";
    private final Logger logger = Logger.getLogger(this.getClass());
    private String ROSBANK_DEPARTMENT_CODE_LIKE = "131";
    private String CONFIDERIFO_DEPARTMENT_CODE_LIKE = "gazprom";
    // Типы выгодоприобретателей
    // 'Застрахованный' (когда CONTREXTMAP.insurerIsInsured == 0) или 'Страхователь (совпадает с ЗЛ)' (когда CONTREXTMAP.insurerIsInsured == 1)
    private static final Long BENEFICIARY_INSURED_TYPEID = 1L;
    // 'По закону'
    private static final Long BENEFICIARY_BY_LAW_TYPEID = 2L;
    // 'Новый'
    private static final Long BENEFICIARY_NORMAL_TYPEID = 3L;
    // 'Страхователь' (когда CONTREXTMAP.insurerIsInsured == 0)
    private static final Long BENEFICIARY_INSURER_TYPEID = 4L;

    protected void validateContractExtBeforeCalc(Map<String, Object> contract, Map<String, Object> contractExtValues, boolean isFixContr, boolean isPreCalcCheck, StringBuffer errorText, String login, String password) throws Exception {
        // получение продукта по данным договора
        Map<String, Object> product = getFullProductDataFromContract(contract, login, password);
        Map<String, Object> prodDefValsMap = getProdDefValsMapFromProduct(product);
        Date insuredBirthDate2 = getInsured2AdultBirthDate(contract);
        Date insuredBirthDate = getInsuredAdultBirthDate(contract);
        boolean isInsuredBirthDate2Exist = (insuredBirthDate2 != null);
        if (contract.get("CONTREXTMAP") != null) {
            Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
            if ((contrExtMap.get("programId") != null) && (Long.valueOf(contrExtMap.get("programId").toString()) == 2L)) {
                boolean isStartDateExists = checkContractValueExist(contract, "STARTDATE", "Дата начала действия договора", errorText);
                checkContractValueExist(contract, "PREMVALUE", "Страховой взнос", errorText);

                // проверки на 'Допустимый возраст на начало срока страхования от/до Х полных лет включительно на дату начала срока страхования'
                if (isStartDateExists && isInsuredBirthDate2Exist) {
                    Date startDate = (Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE", true);
                    // проверка на 'Допустимый возраст на начало срока страхования до Х полных лет включительно на дату начала срока страхования'
                    // (в том числе с учетом пола застрахованного, если в PRODDEFVAL-ах продукта имеется соответствующий параметр)
                    Long insuredAgeOnContrStartMax = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "REJECT_INSUREDAGE_ONCONTRACTSTART_MAX");
                    String ageMsgEnding = "";

                    if (insuredAgeOnContrStartMax == null) {
                        // если не найден обычный унисекс-параметр, то выполняется поиск параметра, зависящего от пола
                        String insuredAdultGenderSysName = getInsuredAdultGenderProdDefValSysName(contract);
                        if (insuredAdultGenderSysName.isEmpty()) {
                            errorText.append("Не удалось определить системное наименование для указанного пола застрахованного - возможно, соответствующий атрибут содержит некорректно значение. ");
                        } else {
                            String insuredAgeOnContrStartMaxProdDefValName = String.format("REJECT_%s_INSUREDAGE_ONCONTRACTSTART_MAX", insuredAdultGenderSysName);
                            insuredAgeOnContrStartMax = getLongProdDefValueFromDefValuesMap(prodDefValsMap, insuredAgeOnContrStartMaxProdDefValName);
                            ageMsgEnding = " для застрахованного данного пола";
                        }
                    }

                    if (insuredAgeOnContrStartMax != null) {
                        GregorianCalendar insuredBirthDatePlusMaxStartAgeGC = new GregorianCalendar();
                        insuredBirthDatePlusMaxStartAgeGC.setTime(insuredBirthDate2);
                        insuredBirthDatePlusMaxStartAgeGC.add(Calendar.YEAR, insuredAgeOnContrStartMax.intValue());
                        // проверка на возраст должна пропускать человека по ограниченный возраст включительно. вплоть до его др.
                        insuredBirthDatePlusMaxStartAgeGC.add(Calendar.YEAR, 1);
                        insuredBirthDatePlusMaxStartAgeGC.add(Calendar.DATE, -1);
                        if (startDate.after(insuredBirthDatePlusMaxStartAgeGC.getTime())) {
                            errorText.append(String.format("Возраст второго застрахованного лица на начало срока страхования более допустимого%s. ", ageMsgEnding));
                        }
                    }
                    if ((contrExtMap.get("programId") != null) && (Long.valueOf(contrExtMap.get("programId").toString()) == 1L)) {
                        if (checkContractExtValueExist(contractExtValues, "inheritedPeriod", "Период наследуемых выплат", errorText)) {
                            Long inheritedPeriod = getLongParam(contrExtMap, "inheritedPeriod");
                            Date docDate = (Date) parseAnyDate(contract.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
                            insuredAgeOnContrStartMax = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "INCREASEDRENTPERIODMAXAGE");
                            checkAge(contract, inheritedPeriod, insuredAgeOnContrStartMax, insuredBirthDate, docDate, "Период наследуемых выплат", "Cумма возраста застрахованного, периода до начала выплат (лет) и периода наследуемых выплат больше ", errorText);
                            if (isInsuredBirthDate2Exist) {
                                checkAge(contract, inheritedPeriod, insuredAgeOnContrStartMax, insuredBirthDate2, docDate, "Период наследуемых выплат", "Cумма возраста застрахованного, периода до начала выплат (лет) и периода наследуемых выплат больше ", errorText);
                            }
                        }
                    }

                    // проверка на 'Допустимый возраст на начало срока страхования от Х полных лет включительно на дату начала срока страхования'
                    Long insuredAgeOnContrStartMin = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "REJECT_INSUREDAGE_ONCONTRACTSTART_MIN");
                    if (insuredAgeOnContrStartMin != null) {
                        GregorianCalendar insuredBirthDatePlusMinStartAgeGC = new GregorianCalendar();
                        insuredBirthDatePlusMinStartAgeGC.setTime(insuredBirthDate2);
                        insuredBirthDatePlusMinStartAgeGC.add(Calendar.YEAR, insuredAgeOnContrStartMin.intValue());
                        if (startDate.before(insuredBirthDatePlusMinStartAgeGC.getTime())) {
                            errorText.append("Возраст второго застрахованного лица на начало срока страхования менее допустимого. ");
                        }
                    }
                }
            }
            checkContractExtValueExist(contractExtValues, "programId", "Тип программы", errorText);
            checkContractExtValueExist(contractExtValues, "productId", "Тип продукта", errorText);
            if (!isPreCalcCheck) {
                checkContractExtValueExist(contractExtValues, "rentBeginDATE", "Период выплат ренты с", errorText);
                checkContractExtValueExist(contractExtValues, "rentEndDATE", "Дата окончания платежного периода", errorText);
                checkContractExtValueExist(contractExtValues, "needDocDATE", "Для 1-й выплаты предоставить документы до", errorText);
                checkContractExtValueExist(contractExtValues, "rentFirstDATE", "Дата 1-й выплаты ренты (без учета праздничных дней)", errorText);
            }
            ArrayList<Map<String, Object>> allRisksList = getAllRisksListFromContract(contract);
            Map<String, Map<String, Object>> risks = getRisksMapBySysNameFromRiskList(allRisksList);
            Long paramId = getLongParam(contrExtMap.get("programId"));
            if (paramId != null) {
                if (paramId == 2L) {
                    // формирование списка актуальных (не удаляемых) рисков из мапы договора
                    if (null != risks.get("deathStatePayments")) {
                        Map<String, Object> deathStatePayments = risks.get("deathStatePayments");
                        if (null != deathStatePayments) {
                            if (null != deathStatePayments.get("CONTRRISKEXTMAP")) {
                                Map<String, Object> riskExtValues = (Map<String, Object>) deathStatePayments.get("CONTRRISKEXTMAP");
                                if (null == riskExtValues.get("valueRentAfterDeath")) {
                                    checkContractExtValueExist(contractExtValues, "valueRentAfterDeath", "Соотношение ренты после ухода одного из ЗЛ из жизни", errorText);
                                }
                            } else {
                                checkContractExtValueExist(contractExtValues, "valueRentAfterDeath", "Соотношение ренты после ухода одного из ЗЛ из жизни", errorText);
                            }
                        } else {
                            checkContractExtValueExist(contractExtValues, "valueRentAfterDeath", "Соотношение ренты после ухода одного из ЗЛ из жизни", errorText);
                        }
                    } else {
                        checkContractExtValueExist(contractExtValues, "valueRentAfterDeath", "Соотношение ренты после ухода одного из ЗЛ из жизни", errorText);
                    }
                }
                if (paramId == 1L) {
                    if (risks.get("deathGuaranteedPayments") != null) {
                        Long inheridPeriod = getLongParam(contrExtMap.get("inheritedPeriod"));
                        if (inheridPeriod != null) {
                            if (inheridPeriod == 0) {
                                errorText.append("Значение поля 'Период наследуемых выплат' должно быть больше 0. ");
                            }
                        } else {
                            checkContractExtValueExist(contractExtValues, "inheritedPeriod", "Период наследуемых выплат", errorText);
                        }
                    }
                }
            }
            if (checkContractExtValueExist(contractExtValues, "waitingPeriod", "Период ожидания до начала выплат (лет)", errorText)) {
                Long waitingPeriod = getLongParam(contrExtMap, "waitingPeriod");
                if (!((waitingPeriod >= 0) && (waitingPeriod <= 30))) {
                    errorText
                            .append("Период ожидания до начала выплат (лет)")
                            .append(" '")
                            .append("Допустимо вводить значения от 0 до 30")
                            .append("'. ");
                } else {
                    Date docDate = (Date) parseAnyDate(contract.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
                    Long insuredAgeOnContrStartMax = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "WAITINGPERIODMAXAGE");
                    if (null != insuredBirthDate) {
                        checkAge(contract, waitingPeriod, insuredAgeOnContrStartMax, insuredBirthDate, docDate, "Период ожидания до начала выплат (лет)", "Cумма возраста застрахованного на момент оформления договора и периода до начала выплат более ", errorText);
                    }
                    if (isInsuredBirthDate2Exist) {
                        checkAge(contract, waitingPeriod, insuredAgeOnContrStartMax, insuredBirthDate2, docDate, "Период ожидания до начала выплат (лет)", "Cумма возраста застрахованного на момент оформления договора и периода до начала выплат более ", errorText);
                    }
                }
            }
            if (!isPreCalcCheck) {
                checkContractExtValueExist(contrExtMap, "insuredAge1", "Возраст застрахованного на начало выплат (лет)", errorText);
            }
            if (null != contrExtMap.get("inheritedPeriod")) {
                if ((contrExtMap.get("programId") != null) && (Long.valueOf(contrExtMap.get("programId").toString()) == 2L)) {
                    checkContractExtValueExist(contrExtMap, "inheritedPaymentendDATE", "Дата окончания периода наследуемых выплат", errorText);
                }
            }
            checkContractExtValueExist(contrExtMap, "payPeriod", "Периодичность выплат", errorText);
            if ((contrExtMap.get("productId") != null) && (Long.valueOf(contrExtMap.get("productId").toString()) == 1L)) {
                if (checkContractExtValueExist(contrExtMap, "countPayYear", "Период выплат (лет)", errorText)) {
                    Long countPayYear = getLongParam(contrExtMap, "countPayYear");
                    if (!((countPayYear >= 5) && (countPayYear <= 30))) {
                        errorText
                                .append("Период выплат (лет)")
                                .append(" '")
                                .append("Допустимо вводить значения от 5 до 30")
                                .append("'. ");
                    }
                }
            }
            if (!isPreCalcCheck) {

                if ((contrExtMap.get("programId") != null) && (Long.valueOf(contrExtMap.get("programId").toString()) == 2L)) {
                    checkContractExtValueExist(contrExtMap, "insuredAge2", "Возраст второго застрахованного на начало выплат (лет)", errorText);
                }
            }
            /*            if ((contrExtMap.get("programId") != null) && (Long.valueOf(contrExtMap.get("programId").toString()) == 1L)
                    && (contrExtMap.get("productId") != null) && (Long.valueOf(contrExtMap.get("productId").toString()) == 2L)) {
                checkContractExtValueExist(contrExtMap, "insuredAge2", "Возраст второго застрахованного на начало выплат (лет)", errorText);
            }*/
            if ((contrExtMap.get("programId") != null) && (Long.valueOf(contrExtMap.get("programId").toString()) == 1L)) {
                if (checkContractExtValueExist(contrExtMap, "inheritedPeriod", "Период наследуемых выплат", errorText)) {
                    Long inheritedPeriod = getLongParam(contrExtMap, "inheritedPeriod");
                    Date docDate = (Date) parseAnyDate(contract.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
                    Long insuredAgeOnContrStartMax = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "INCREASEDRENTPERIODMAXAGE");
                    if (null != insuredBirthDate) {
                        checkAge(contract, inheritedPeriod, insuredAgeOnContrStartMax, insuredBirthDate, docDate, "Период наследуемых выплат", "Cумма возраста застрахованного, периода до начала выплат (лет) и периода наследуемых выплат больше ", errorText);
                    }
                    if (isInsuredBirthDate2Exist) {
                        checkAge(contract, inheritedPeriod, insuredAgeOnContrStartMax, insuredBirthDate2, docDate, "Период наследуемых выплат", "Cумма возраста застрахованного, периода до начала выплат (лет) и периода наследуемых выплат больше ", errorText);
                    }
                }
            }

            if ((contrExtMap.get("programId") != null) && (Long.valueOf(contrExtMap.get("programId").toString()) == 1L)
                    && (contrExtMap.get("productId") != null) && (Long.valueOf(contrExtMap.get("productId").toString()) == 2L)) {
                if (checkContractExtValueExist(contrExtMap, "ratioIncreasedRentPeriod", "Период распределения повышенной ренты", errorText)) {
                    Long ratioIncreasedRentPeriod = getLongParam(contrExtMap, "ratioIncreasedRentPeriod");
                    Long waitingPeriod = getLongParam(contrExtMap, "waitingPeriod");
                    Date docDate = (Date) parseAnyDate(contract.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
                    Long insuredAgeOnContrStartMax = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "INCREASEDRENTPERIODMAXAGE");
                    if (null != insuredBirthDate) {
                        checkAge(contract, waitingPeriod + ratioIncreasedRentPeriod, insuredAgeOnContrStartMax, insuredBirthDate, docDate, "Период распределения повышенной ренты", "Cумма возраста застрахованного, периода до начала выплат (лет) и периода распределения повешенной ренты больше ", errorText);
                    }
                    if (isInsuredBirthDate2Exist) {
                        checkAge(contract, waitingPeriod + ratioIncreasedRentPeriod, insuredAgeOnContrStartMax, insuredBirthDate2, docDate, "Период распределения повышенной ренты", "Cумма возраста застрахованного, периода до начала выплат (лет) и периода распределения повешенной ренты больше ", errorText);
                    }
                }
            }
        }
    }

    protected boolean validateContractParams(Map<String, Object> contract, boolean isFixContr, boolean isPreCalcCheck, String login, String password) throws Exception {
        StringBuffer errorText = new StringBuffer();
        checkContractValueExist(contract, "DOCUMENTDATE", "Дата оформления договора", errorText);
        Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (contractExtValues == null) {
            errorText.append("Не указаны расширенные атрибуты договора. ");
        } else {
            validateContractExtBeforeCalc(contract, contractExtValues, isFixContr, isPreCalcCheck, errorText, login, password);
            boolean isDataValid = (errorText.length() == 0);
            if (!isDataValid) {
                errorText.append("Сведения договора не сохранены.");
                contract.put("Status", "Error");
                contract.put("Error", errorText.toString());
            }
            isPreCalcCheck = isPreCalcCheck && isDataValid;
        }
        return isPreCalcCheck && super.validateContractSaveParams(contract, isFixContr, isPreCalcCheck, login, password);
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
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BNewHorizonsCalc(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BRightDecisionCalc");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }

        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        calcParams.put("CALCVERID", params.get("CALCVERID"));

        Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (contractExtValues != null) {
            long firstInsAge = calcAge((Date) parseAnyDate(contractExtValues.get("insuredBirthDATE"), Date.class, "insuredBirthDATE"));
            long secondInsAge = calcAge((Date) parseAnyDate(contractExtValues.get("insuredBirth2DATE"), Date.class, "insuredBirth2DATE"));
            calcParams.put("age", firstInsAge);
            calcParams.put("secondAge", secondInsAge);
            calcParams.put("programType", contractExtValues.get("programId"));
            calcParams.put("insProgType", contractExtValues.get("productId"));
            calcParams.put("insFee", contract.get("PREMVALUE"));
            calcParams.put("currency", contract.get("INSAMCURRENCYID"));
            calcParams.put("gender", contractExtValues.get("insuredGender"));
            calcParams.put("secondGender", contractExtValues.get("insuredGender2"));

            Long waitPeriod = getLongParam(contractExtValues.get("waitingPeriod"));
            calcParams.put("waitPeriod", waitPeriod);
            Object countPayYearBean = contractExtValues.get("countPayYear");
            calcParams.put("payoutPeriodYear", countPayYearBean == null ? 0 : countPayYearBean);
            calcParams.put("payInheritedPeriod", contractExtValues.get("inheritedPeriod"));
            calcParams.put("waitInheritePayPeriod", contractExtValues.get("beforStartPaymentPeriod"));
            calcParams.put("isPayFeeEnd", contractExtValues.get("periodEndValue"));
            //calcParams.put("periodicityPay", contractExtValues.get("payPeriod"));
            calcParams.put("periodicityPay", params.get("paymentVariantSysName"));
            // !!
            calcParams.put("isCalcByGuarRent", 0L);
            // !!
            calcParams.put("guaranteedRent", contractExtValues.get("beforStartPaymentPeriod"));
            // !!
            calcParams.put("isRegularDeposit", 0L);
            calcParams.put("isRegularFee", 0L);
            Date sd = clearDateTime((Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE"));
            calcParams.put("dateBeginContr", sd);
            calcParams.put("dateEndPayRent", clearDateTime((Date) parseAnyDate(contractExtValues.get("rentEndDATE"), Date.class, "rentEndDATE")));
            Object increasedRentLevelBean = contractExtValues.get("increasedRentLevel");
            Double increasedRentLevel = increasedRentLevelBean != null ? (Double.valueOf(increasedRentLevelBean.toString()) / 100) : 0.5;
            calcParams.put("rentRationAfterIncreasedPeriod", increasedRentLevel);
            calcParams.put("rentRation", 0.0);

            calcParams.put("guarFundDate", getGuarFundDate(sd, waitPeriod.intValue()));

            // формирование списка актуальных (не удаляемых) рисков из мапы договора
            ArrayList<Map<String, Object>> allRisksList = getAllRisksListFromContract(contract);
            Map<String, Map<String, Object>> risks = getRisksMapBySysNameFromRiskList(allRisksList);
            if (null != risks.get("deathStatePayments")) {
                Map<String, Object> deathStatePayments = risks.get("deathStatePayments");
                if (null != deathStatePayments) {
                    if (null != deathStatePayments.get("CONTRRISKEXTMAP")) {
                        Map<String, Object> riskExtValues = (Map<String, Object>) deathStatePayments.get("CONTRRISKEXTMAP");
                        if (null != riskExtValues.get("valueRentAfterDeath")) {
                            Double rentRation = Double.valueOf(riskExtValues.get("valueRentAfterDeath").toString()) / 100;
                            calcParams.put("rentRation", rentRation);
                        }
                    }
                }
            }
            Object periodIncDistrRentBean = contractExtValues.get("ratioIncreasedRentPeriod");
            calcParams.put("periodIncDistrRent", periodIncDistrRentBean == null ? 1 : periodIncDistrRentBean);
            calcParams.put("guarRentStrat", 0L);
            calcParams.put("baseActive", 0L);
            calcParams.put("profitBaseActive", 0L);
            if (null != contract.get("CONTREXTMAP")) {
                Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
                int year = 1900;
                int month = 1;
                int day = 1;
                String paymentVariantSysName = "ANNUALLY";
                Long mStep = 12L;
                if (null != params.get("paymentVariantSysName")) {
                    paymentVariantSysName = getStringParam(params.get("paymentVariantSysName"));
                    if (paymentVariantSysName.equalsIgnoreCase("QUARTERLY")) {
                        mStep = 3L;
                    }
                    if (paymentVariantSysName.equalsIgnoreCase("MONTHLY")) {
                        mStep = 1L;
                    }
                    if (paymentVariantSysName.equalsIgnoreCase("ANNUALLY")) {
                        mStep = 12L;
                    }
                }
                GregorianCalendar documentDateSD = new GregorianCalendar();
                documentDateSD.setTime(sd);
                documentDateSD.set(GregorianCalendar.HOUR, 0);
                documentDateSD.set(GregorianCalendar.MINUTE, 0);
                documentDateSD.set(GregorianCalendar.SECOND, 0);
                documentDateSD.set(GregorianCalendar.MILLISECOND, 0);
                year = documentDateSD.get(GregorianCalendar.YEAR);
                month = documentDateSD.get(GregorianCalendar.MONTH);
                day = documentDateSD.get(GregorianCalendar.DAY_OF_MONTH);

                Map<String, Object> calcMap = new HashMap<>();
                calcMap.put("rowNum", 0);
                calcMap.put("ageConst", 18);
                calcMap.put("constDate", sd);

                ArrayList<Map<String, Object>> calcList = new ArrayList<Map<String, Object>>();
                calcList.add(calcMap);
                //  Первоначальные значения от 0 до 984.
                for (int i = 1; i < 985; i++) {
                    calcMap = new HashMap<>();
                    month += mStep;
                    if (12 < month) {
                        month = month % 12;
                        year += 1;
                    }
                    if (101 < i + 18) {
                        calcMap.put("ageConst", 0);
                    } else {
                        calcMap.put("ageConst", i + 18);
                    }
                    GregorianCalendar constDate = new GregorianCalendar();
                    constDate.set(year, month, day, 0, 0, 0);
                    calcMap.put("constDate", constDate.getTime());
                    calcMap.put("rowNum", i);
                    calcList.add(calcMap);
                }
                calcParams.put("CALCLIST", calcList);

                GregorianCalendar startDate = new GregorianCalendar();
                GregorianCalendar documentDate = new GregorianCalendar();

                startDate.setTime(sd);
                startDate.set(GregorianCalendar.HOUR, 0);
                startDate.set(GregorianCalendar.MINUTE, 0);
                startDate.set(GregorianCalendar.SECOND, 0);
                startDate.set(GregorianCalendar.MILLISECOND, 0);
                sd = clearDateTime((Date) parseAnyDate(contract.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE"));
                documentDate.setTime(sd);

                ArrayList<Map<String, Object>> calcParamProgram = new ArrayList<Map<String, Object>>();
                //  Первоначальные значения от 1 до 70.
                calcMap = new HashMap<>();
                calcMap.put("yaerByInsContr", 1L);
                calcMap.put("periodByInsContrFrom", documentDate.getTime());
                startDate.add(GregorianCalendar.YEAR, 1);
                startDate.add(GregorianCalendar.DAY_OF_YEAR, -1);
                calcMap.put("periodByInsContrByInc", startDate.getTime());
                startDate.add(GregorianCalendar.DAY_OF_YEAR, 1);
                calcMap.put("periodByInsContrBy", startDate.getTime());
                calcParamProgram.add(calcMap);

                for (int i = 2; i < 71; i++) {
                    calcMap = new HashMap<>();
                    calcMap.put("yaerByInsContr", i);
                    calcMap.put("periodByInsContrFrom", startDate.getTime());
                    startDate.add(GregorianCalendar.YEAR, 1);
                    startDate.add(GregorianCalendar.DAY_OF_YEAR, -1);
                    calcMap.put("periodByInsContrByInc", startDate.getTime());
                    startDate.add(GregorianCalendar.DAY_OF_YEAR, 1);
                    calcMap.put("periodByInsContrBy", startDate.getTime());
                    calcParamProgram.add(calcMap);
                }
                calcParams.put("CALCPARAMPROGRAM", calcParamProgram);

            }
            logger.debug("after dsB2BRightDecisionCalc");
            addDataForCacheHandbook(calcParams, firstInsAge, secondInsAge);
        }
        Map<String, Object> calcRes = this.callService(Constants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcParams, login, password);
        logger.debug("calcParams: " + calcParams);
        logger.debug("calcRes: " + calcRes);
        return calcRes;
    }

    private Date clearDateTime(Date date) {
        if (date == null) {
            return null;
        }
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);
        gregorianCalendar.set(GregorianCalendar.HOUR, 0);
        gregorianCalendar.set(GregorianCalendar.MINUTE, 0);
        gregorianCalendar.set(GregorianCalendar.SECOND, 0);
        gregorianCalendar.set(GregorianCalendar.MILLISECOND, 0);
        return gregorianCalendar.getTime();
    }

    /**
     * Получить дату для поиска стоимости гарантированого фонда по дате начала
     * договора
     *
     * @param startDate - дата начала договора
     * @param waitPeriod - период ожидания выплат
     * @return
     */
    private Date getGuarFundDate(Date startDate, int waitPeriod) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(startDate);
        gregorianCalendar.add(GregorianCalendar.MONTH, (12 * waitPeriod));
        gregorianCalendar.add(GregorianCalendar.DAY_OF_YEAR, -1);
        return gregorianCalendar.getTime();
    }

    private void addDataForCacheHandbook(Map<String, Object> calcParams, long firstInsAge, long secondInsAge) {
        List<Map<String, Object>> yearList = new ArrayList<Map<String, Object>>();
        Map<String, Object> addMap;
        for (long i = 1; i <= 40; i++) {
            addMap = new HashMap<String, Object>();
            addMap.put("year", i);
            yearList.add(addMap);
        }
        calcParams.put("yearList", yearList);

        List<Map<String, Object>> ageList1 = new ArrayList<Map<String, Object>>();
        for (long i = firstInsAge; i <= 122; i++) {
            addMap = new HashMap<String, Object>();
            addMap.put("age", i);
            ageList1.add(addMap);
        }
        calcParams.put("ageList1", ageList1);

        List<Map<String, Object>> ageList2 = new ArrayList<Map<String, Object>>();
        for (long i = 20; i <= 95; i += 5) {
            addMap = new HashMap<String, Object>();
            addMap.put("age", i);
            ageList2.add(addMap);
        }
        calcParams.put("ageList2", ageList2);

        List<Map<String, Object>> ageList3 = new ArrayList<Map<String, Object>>();
        for (long i = secondInsAge; i <= 122; i++) {
            addMap = new HashMap<String, Object>();
            addMap.put("age", i);
            ageList3.add(addMap);
        }
        calcParams.put("ageList3", ageList3);

        calcParams.put("currencyMap", new HashMap<String, Object>());
        calcParams.put("mortalityTpt", new HashMap<String, Object>());
        calcParams.put("tariffMap", new HashMap<String, Object>());
        calcParams.put("stockMap", new HashMap<String, Object>());
        calcParams.put("depositMap", new HashMap<String, Object>());
        calcParams.put("mortalityTptSecond", new HashMap<String, Object>());
    }

    private String getPaymentVariantSysNameById(Map<String, Object> productMap, Long paymentVariantId, String login, String password) throws Exception {
        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("PAYVARID", paymentVariantId);
        List<Map<String, Object>> prodPayVarList = loadHandbookData(hbParams, "B2B.SBSJ.NewHorizons.PayVar", login, password);
        if (prodPayVarList != null) {
            for (Map<String, Object> bean : prodPayVarList) {
                Map<String, Object> payVarMap = bean;
                if (Long.valueOf(payVarMap.get("PAYVARID").toString()).longValue() == paymentVariantId.longValue()) {
                    return payVarMap.get("SYSNAME").toString();
                }
            }
        }
        return null;
    }

    @WsMethod(requiredParams = {"CONTRMAP", "FULLPRODMAP"})
    public Map<String, Object> dsB2BNewHorizonsCalcByContrMap(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BNewHorizonsCalcByContrMap");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contractMap = (Map<String, Object>) params.get("CONTRMAP");
        Long contractId = getLongParam(contractMap.get("CONTRID"));
        // предварительная проверка на корректность введенных данных
        // (вычисления выполнять не нужно, если переданы заведомо не походящие для создания договора данные)
        boolean isPreCalcCheck = true; // проверка перед вызовом калькулятора (часть атрибутов, например, вычисляемые суммы, не будут проверены)
        // данные договора корректны - можно выполнять вычисления
        Map<String, Object> contrExtMap = (Map<String, Object>) contractMap.get("CONTREXTMAP");
        Map<String, Object> productMap = (Map<String, Object>) params.get("FULLPRODMAP");
        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        calcParams.put("CALCVERID", productMap.get("CALCVERID"));
        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contractMap.get("INSOBJGROUPLIST");
        List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupList.get(0).get("OBJLIST");
        calcParams.put("termIns", getTermInYearsById(productMap, Long.valueOf(contractMap.get("TERMID").toString())));
        calcParams.put("CONTRMAP", contractMap);
        calcParams.put("paymentVariantSysName", getPaymentVariantSysNameById(productMap, getLongParamLogged(contrExtMap, "payPeriod"), login, password));
        boolean calcFromFee = false;
        Map<String, Object> hbParams = new HashMap<String, Object>();
        List<Map<String, Object>> trancheList = loadHandbookData(hbParams, "B2B.NewHorizons.TrancheSchedule", login, password);
        Date date = new Date();

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        if ((trancheList != null) && (trancheList.size() > 1)) {
            for (Map<String, Object> tranche : trancheList) {
                Date beginDate = new Date(0);
                if (null != tranche.get("beginDATE")) {
                    beginDate = (Date) tranche.get("beginDATE");
                }
                // 73051 - 01.01.2100
                Date endDate = new Date(73051);
                if (null != tranche.get("endDATE")) {
                    endDate = (Date) tranche.get("endDATE");
                }
                if ((date.after(beginDate)) && (date.before(endDate))) {
                    Date trancheDate = (Date) tranche.get("trancheDATE");
                    
//                    contractMap.put("STARTDATE", trancheDate);
                    contractMap.put("STARTDATE", df.format(trancheDate));
                }
            }
        }
        if (null != contractMap.get("CONTREXTMAP")) {
            if (null != contrExtMap.get("waitingPeriod")) {
                Long waitingPeriod = getLongParam(contrExtMap.get("waitingPeriod"));
                Date sd = (Date) parseAnyDate(contractMap.get("STARTDATE"), Date.class, "STARTDATE");
                GregorianCalendar documentDateSD = new GregorianCalendar();
                documentDateSD.setTime(sd);
                documentDateSD.add(GregorianCalendar.YEAR, waitingPeriod.intValue());
                contractMap.put("FINISHDATE", df.format(documentDateSD.getTime()));
                contrExtMap.put("rentEndDATE", df.format(documentDateSD.getTime()));
                GregorianCalendar sdgc = new GregorianCalendar();
                sdgc.setTime(sd);
                sdgc.add(Calendar.MONTH, (Long.valueOf((12 * waitingPeriod) + (waitingPeriod == 0 ? 1 : 0))).intValue());
                contrExtMap.put("rentBeginDATE", df.format(sdgc.getTime()));
                long yearRentStartDate = sdgc.get(GregorianCalendar.YEAR);
                int monthRentStartDate = sdgc.get(GregorianCalendar.MONTH);
                int dayRentStartDate = sdgc.get(GregorianCalendar.DAY_OF_MONTH) - 1;

                documentDateSD.setTime(sdgc.getTime());
                if (null != contrExtMap.get("inheritedPeriod")) {
                    Long inheritedPeriod = getLongParam(contrExtMap.get("inheritedPeriod"));
                    if (null == contrExtMap.get("countPayYear")) {
                        contrExtMap.put("countPayYear", 0L);
                    }
                    documentDateSD.add(Calendar.DAY_OF_YEAR, -1);
                    documentDateSD.add(Calendar.YEAR, inheritedPeriod.intValue());
                    contrExtMap.put("inheritedPaymentendDATE", df.format(documentDateSD.getTime()));
                }

                sdgc.setTime(sd);
                String paymentVariantSysName = (String) calcParams.get("paymentVariantSysName");
                sdgc.add(Calendar.MONTH, (Long.valueOf(((12 * waitingPeriod) + (waitingPeriod == 0 ? 1 : 0) + (paymentVariantSysName.equalsIgnoreCase("ANNUALLY") ? 12 : (paymentVariantSysName.equalsIgnoreCase("QUARTERLY") ? 3 : 1))))).intValue());
                contrExtMap.put("billingEndDATE", df.format(sdgc.getTime()));
                sdgc.add(GregorianCalendar.MONTH, 1);
                sdgc.set(GregorianCalendar.DAY_OF_MONTH, 1);
                sdgc.add(GregorianCalendar.DAY_OF_YEAR, -1);
                contrExtMap.put("needDocDATE", df.format(sdgc.getTime()));
                int year = sdgc.get(Calendar.YEAR);
                int month = sdgc.get(Calendar.MONTH) + 1;
                int day = 1;

                GregorianCalendar tdgc = new GregorianCalendar((month == 12 ? 1 : 0) + year, (month == 12 ? 1 : month), day);
                int counter = 0;
                while (counter < 14) {
                    tdgc.add(GregorianCalendar.DAY_OF_YEAR, 1);
                    if (!((tdgc.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY) || ((tdgc.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY)))) {
                        counter++;
                    }
                }
                contrExtMap.put("rentFirstDATE", df.format(tdgc.getTime()));
                long firstInsAge = calcAge((Date) parseAnyDate(contrExtMap.get("insuredBirthDATE"), Date.class, "insuredBirthDATE"));
                tdgc = new GregorianCalendar(Long.valueOf(yearRentStartDate + (getLongParam(contrExtMap.get("productId")) == 2 ? (122 - firstInsAge - waitingPeriod) : getLongParam(contrExtMap.get("countPayYear")))).intValue(), monthRentStartDate, dayRentStartDate);
                contrExtMap.put("rentEndDATE", df.format(tdgc.getTime()));
            }

        }
        boolean isDataValid = validateContractParams(contractMap, false, true, login, password);
        if (!isDataValid) {
            // данные не корректны - досрочный возврат мапы договора
            logger.debug("Contract is not valid - calculation skipped!");
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("CONTRMAP", contractMap);
            logger.debug("after dsB2BNewHorizonsCalcByContrMap");
            return result;
        }
        Map<String, Object> resMap = this.callService(Constants.B2BPOSWS, "dsB2BNewHorizonsCalc", calcParams, login, password);
        // обработка результата калькулятора
        // Обработка выкупных сумм
        if ((resMap.get("CALCPARAMPROGRAM") != null)) {
            List<Map<String, Object>> redemptionSumList = (List<Map<String, Object>>) resMap.get("CALCPARAMPROGRAM");
            List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>();
            if (redemptionSumList.size() > 0) {
                for (Iterator<Map<String, Object>> iterator = redemptionSumList.iterator(); iterator.hasNext();) {
                    Map<String, Object> item = iterator.next();
                    Map<String, Object> newItem = new HashMap<String, Object>();
                    newItem.put("PAYVALUE", item.get("payGurantedFundForParamProgram"));
                    newItem.put("RISKVALUE", item.get("payRiskFundForParamProgram"));
                    newItem.put("RENTVALUE", item.get("fixedRentForParamProgram"));
                    newItem.put("REDEMPVALUE", item.get("redemtionSum"));
                    newItem.put("PAYNUM", item.get("yaerByInsContr"));
                    newItem.put("STARTDATE", df.format(item.get("periodByInsContrFrom")));
                    newItem.put("FINISHDATE", df.format(item.get("periodByInsContrByInc")));
                    newItem.put("PAYDATE", df.format(item.get("periodByInsContrBy")));
                    newItem.put("CONTRID", contractId);
                    mappedList.add(newItem);
                }
            }
            contractMap.put(SAVINGSSCHEDULE_SUM_LIST_PARAMNAME, mappedList);

        }
        contrExtMap.put("insuredAge1", resMap.get("insAgeOnBeginPay"));
        contrExtMap.put("insuredAge2", resMap.get("insSecondAgeOnBeginPay"));
        contrExtMap.put("yieldContribution", resMap.get("profitabilityOnFee"));
        // 
        contrExtMap.put("minGuarantedRent", resMap.get("minGuarantedRent"));
        contrExtMap.put("rentCalc", resMap.get("rentCalc"));
        contrExtMap.put("profByDeposMed", resMap.get("profByDeposMed"));
        contrExtMap.put("calcInsFee", resMap.get("calcInsFee"));
        //
        contrExtMap.put("fixedRentPercent", resMap.get("fixRentAtFee"));
        contrExtMap.put("fixedRentAPPercent", resMap.get("fixedRentAfter"));
        contrExtMap.put("fixedRentAPValue", resMap.get("fixRentAfterAtCurProg"));
        // Страховой тариф от страховой суммы по страховому риску «Смерть в Накопительный период» на день окончания Накопительного периода
        contrExtMap.put("insTariffValue", resMap.get("insTariffValue"));

        contrExtMap.put("fixedRentValue", resMap.get("fixRentAtCurProg"));
        contrExtMap.put("fixedRentFDPercent", resMap.get("fixedRentAfter"));
        contrExtMap.put("fixedRentFDValue", resMap.get("fixRentAfterAtCurProg"));

        // Обработка выкупных сумм
        if ((resMap.get("CALCLIST") != null)) {
            List<Map<String, Object>> redemptionSumList = (List<Map<String, Object>>) resMap.get("CALCLIST");
            List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>();
            if (redemptionSumList.size() > 0) {
                for (Iterator<Map<String, Object>> iterator = redemptionSumList.iterator(); iterator.hasNext();) {
                    Map<String, Object> item = iterator.next();
                    Map<String, Object> newItem = new HashMap<String, Object>();
                    Double accumRent = getDoubleParam(item, "accumRent");
                    if ((null != accumRent) && (accumRent > 0.0)) {
                        newItem.put("AGE", item.get("ageConst"));
                        newItem.put("PAYDATE", df.format(item.get("constDate")));
                        newItem.put("PAYNUM", item.get("rowNum"));
                        newItem.put("RENTACCUMULATION", item.get("accumRent"));
                        newItem.put("RENTVALUE", item.get("payByRent"));
                        newItem.put("PAYVALUE", item.get("accumDeposit"));
                        newItem.put("RISKVALUE", item.get("payByDiposit"));
                        newItem.put("CONTRID", contractId);
                        mappedList.add(newItem);
                    }
                }
            }
            contractMap.put(REDEMPTION_SUM_LIST_PARAMNAME, mappedList);
        }
        contractMap.put(SAVINGSSCHEDULE_SUM_LIST_UPDATE_PARAMNAME, true);
        contractMap.put(REDEMPTION_SUM_LIST_UPDATE_PARAMNAME, true);

        Map<String, Object> result = new HashMap<String, Object>();
        parseDates(contractMap, String.class);
        result.put("CONTRMAP", contractMap);
        logger.debug("after dsB2BNewHorizonsCalcByContrMap");
        return result;
    }

    private String findProdDefValByName(List<Map<String, Object>> prodDefValList, String name) {
        for (Map<String, Object> bean : prodDefValList) {
            if ((bean.get("NAME") != null) && (bean.get("NAME").toString().equalsIgnoreCase(name))) {
                if (bean.get("VALUE") != null) {
                    return bean.get("VALUE").toString();
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private Long getTermInYearsById(Map<String, Object> productMap, Long termId) {
        List<Map<String, Object>> prodTermList = (List<Map<String, Object>>) productMap.get("PRODTERMS");
        if (prodTermList != null) {
            for (Map<String, Object> bean : prodTermList) {
                if (bean.get("TERM") != null) {
                    Map<String, Object> termMap = (Map<String, Object>) bean.get("TERM");
                    if (Long.parseLong(termMap.get("TERMID").toString()) == termId) {
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

    @Override
    protected String generateContrSer(Map<String, Object> contract, String serPrefix, String login, String password) throws Exception {
        String result = getDeptString(contract, login, password);
        result = result + getWaitingPeriodChar(contract);
        result = result + getCurrencyChar(contract);
        result = result + getProgramTypeChar(contract);
        return result;
    }


    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BNewHorizonsContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BNewHorizonsContractPrepareToSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        boolean isDataValid = validateContractSaveParams(contract, false, false, login, password);
        if (null != contract.get("INSURERMAP")) {
            Map<String, Object> insurer = (Map<String, Object>) contract.get("INSURERMAP");
            if (null != contract.get("Error")) {
                StringBuilder errorText = new StringBuilder();
                errorText.append(contract.get("Error"));
                contract.put("Error", errorText.toString());
            }
        }
        Map<String, Object> result;
        if (isDataValid) {
            result = genAdditionalSaveParams(contract, login, password);
        } else {
            result = contract;
        }
        logger.debug("after dsB2BNewHorizonsContractPrepareToSave");
        if ((result.get("Error") != null) && result.get("Error").toString().isEmpty()) {
            result.remove("Error");
        }
        return result;
    }

    // получает дату рождения основного застрахованного по договору (взрослого)
    // переопределить метод в конкретных фасадах, если дату рождения основного застрахованного по договору (взрослого) храниться не в расширенных атрибутах договора
    protected Date getInsured2AdultBirthDate(Map<String, Object> contract) {
        Date insuredBirth2Date = null;
        Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (contractExtValues != null) {
            String insuredBirthDateExtValueFieldName = "insuredBirth2DATE";
            Object insured2BirthDateObj = contractExtValues.get(insuredBirthDateExtValueFieldName);

            if (insured2BirthDateObj != null) {
                insuredBirth2Date = (Date) parseAnyDate(insured2BirthDateObj, Date.class, insuredBirthDateExtValueFieldName, true);
            }
        }
        return insuredBirth2Date;
    }

    protected void checkAge(Map<String, Object> contract, Long ageAddCount, Long ageCount, Date birthDate, Date checkDate, String errorField, String errorMessage, StringBuffer errorText) {
        GregorianCalendar insuredBirthDatePlusMaxStartAgeGC = new GregorianCalendar();
        insuredBirthDatePlusMaxStartAgeGC.setTime(birthDate);
        insuredBirthDatePlusMaxStartAgeGC.add(Calendar.YEAR, ageAddCount.intValue());
        insuredBirthDatePlusMaxStartAgeGC.add(Calendar.YEAR, ageCount.intValue());

        // проверка на возраст должна пропускать человека по ограниченный возраст включительно. вплоть до его др.
        insuredBirthDatePlusMaxStartAgeGC.add(Calendar.YEAR, 1);
        insuredBirthDatePlusMaxStartAgeGC.add(Calendar.DATE, -1);
        if (checkDate.after(insuredBirthDatePlusMaxStartAgeGC.getTime())) {
            errorText
                    .append(errorField)
                    .append(" '")
                    .append(errorMessage)
                    .append(ageCount.toString())
                    .append(" лет.")
                    .append("'. ");
        }
    }

    protected void checkContractExtValues(Map<String, Object> contract, Map<String, Object> contractExtValues, boolean isFixContr, StringBuffer errorText) {
        //checkContractExtValueExist(contractExtValues, "insuredGender1", "Пол застрахованного № 2", errorText);
        //checkContractExtValueExist(contractExtValues, "insuredBirth1DATE", "Дата рождения застрахованного № 2", errorText);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BNewHorizonsContractUnderwritingCheck(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BNewHorizonsContractUnderwritingCheck");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        contract.put("UW", UW_DO_NOT_NEEDED);
        Map<String, Object> result = contract;
        logger.debug("after dsB2BNewHorizonsContractUnderwritingCheck");
        return result;
    }

    private String getWaitingPeriodChar(Map<String, Object> contract) {
        if (contract.get("CONTREXTMAP") != null) {
            Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
            if (contrExtMap.get("waitingPeriod") != null) {
                if (Long.valueOf(contrExtMap.get("waitingPeriod").toString()) > 0) {
                    // Пакет it
                    return "Б";
                } else {
                    // пакет фармы
                    return "";
                }
            }
        }
        return ""; // по умолчанию it
    }

    private String getDeptString(Map<String, Object> contract, String login, String password) throws Exception {
        // получение информации о сотруднике
        Long createUserId = getLongParam(contract.get("CREATEUSERID"));
        Map<String, Object> emplParams = new HashMap<String, Object>();
        emplParams.put(RETURN_AS_HASH_MAP, "TRUE");
        emplParams.put("useraccountid", createUserId);
        Map<String, Object> emplRes = this.selectQuery("dsUserAccountGetInfoById", null, emplParams);
        Map<String, Object> createUserMap = null;
        if (emplRes != null) {
            List<Map<String, Object>> emplResList = (List<Map<String, Object>>) emplRes.get(RESULT);
            if ((emplResList != null) && emplResList.size() > 0) {
                createUserMap = emplResList.get(0);
            }
        }
        Long childDepartmentID = getLongParamLogged(createUserMap, "DEPARTMENTID");
        if (childDepartmentID != null) {
            Map<String, Object> partnerParams = new HashMap<String, Object>();
            partnerParams.put(PARTNERS_DEPARTMENT_CODE_LIKE_KEY_NAME, ROSBANK_DEPARTMENT_CODE_LIKE);
            partnerParams.put("USERDEPARTMENTID", childDepartmentID);
            partnerParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> deptInfo = this.callService(Constants.B2BPOSWS, "dsB2BPartnersDepartmentsListBrowseListByParamEx", partnerParams, true, login, password);
            if (isCallResultOK(deptInfo)) {
                List<Map<String, Object>> deptInfoList = (List<Map<String, Object>>) deptInfo.get(RESULT);
                if ((deptInfoList != null) && deptInfoList.size() > 0) {
                    return "ПП004ПДР";
                }
            }
            partnerParams.put(PARTNERS_DEPARTMENT_CODE_LIKE_KEY_NAME, CONFIDERIFO_DEPARTMENT_CODE_LIKE);
            partnerParams.put("USERDEPARTMENTID", childDepartmentID);
            partnerParams.put(RETURN_AS_HASH_MAP, true);
            deptInfo = this.callService(Constants.B2BPOSWS, "dsB2BPartnersDepartmentsListBrowseListByParamEx", partnerParams, true, login, password);
            if (isCallResultOK(deptInfo)) {
                List<Map<String, Object>> deptInfoList = (List<Map<String, Object>>) deptInfo.get(RESULT);
                if ((deptInfoList != null) && deptInfoList.size() > 0) {
                    return "ПП019ПДР";
                }
            }
        }
        return "ПП018ПДР";
    }

    // не используется в текущем фасаде, удалить позднее
    /*
    private String getFundChar(Map<String, Object> contract) {
        if (contract.get("CONTREXTMAP") != null) {
            Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
            if (contrExtMap.get(FUND_ID_PARAMNAME) != null) {
                // Стратегический инвестор.
                if ("3".equals(contrExtMap.get(FUND_ID_PARAMNAME).toString())) {
                    return "1";
                } else {
                    return "0";
                }
            }
        }
        return "0"; // по умолчанию it
    }
    */

    private String getProgramTypeChar(Map<String, Object> contract) {
        if (contract.get("CONTREXTMAP") != null) {
            Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
            if (contrExtMap.get("programId") != null) {
                // Стратегический инвестор.
                if ("3".equals(contrExtMap.get("programId").toString())) {
                    return "1";
                } else {
                    return "0";
                }
            }
        }
        return "0"; // по умолчанию it
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

    @Override
    protected Map<String, Object> genProductAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {
        logger.debug("Additional contract secondary product depended attributes generation for NewHorizons...");
        // полная мапа продукта - получение из мапы договора или загрузка из БД (если в мапе договора есть системное наименовние продукта)
        Map<String, Object> product = getFullProductMapFromContractMapOrLoadFromDB(contract, login, password);
        // установка ИД программы по умолчанию
        // для случаев, когда в мапе договора не заполнена ссылка на программу и продукт имеет одну единственную программу - будет выбрана программа из продукта
        checkAndUpdateContractProgram(contract, login, password);
        // инициализация значений расширенных атрибутов по умолчанию
        Map<String, Object> contrExtMap = getOrCreateContrExtMap(contract);
        // по умолчанию основная программа включена
        setGeneratedParamIfNull(contrExtMap, "insurerIsInsured", 1L, true, isVerboseLogging);
        // по умолчанию пол застрахованного №1 "Мужской"
        setGeneratedParamIfNull(contrExtMap, "insuredGender", 0L, true, isVerboseLogging);
        // по умолчанию пол застрахованного №2 "Мужской"
        setGeneratedParamIfNull(contrExtMap, "insuredGender2", 0L, true, isVerboseLogging);
        // по умолчанию "Срок от начала выплат до начала периода наследуемых выплат" - 0
        setGeneratedParamIfNull(contrExtMap, "beforStartPaymentPeriod", 0L, true, isVerboseLogging);

//        initDefaultInsurerMap(contract);
//        initDefaultInsuredMap(contract);
        // формирование или обновление (в зависимости от переданного параметра, по умолчанию - обновление без создания недостающих элементов)
        // структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор
        boolean isMissingStructsCreated = getBooleanParam(contract.get("ISMISSINGSTRUCTSCREATED"), false); // флаг создания недостающих элементов структуры страхового продукта договора
        logger.debug("Is missing contract insurance product structure will be created (ISMISSINGSTRUCTSCREATED): " + isMissingStructsCreated);
        updateContractInsuranceProductStructure(contract, product, false, getStringParam(contrExtMap.get("insuranceProgram")), isMissingStructsCreated, login, password);

        logger.debug("Additional contract secondary product depended attributes generation not implemented for this product.");
        processExtParam(contract,contrExtMap,login,password);
        // доработки по задаче 10899
        if (contract.get("INSOBJGROUPLIST") != null) {
            List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
            if (insObjGroupList.isEmpty()) {
                return contract;
            }
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            for (Map<String, Object> insObjGroup : insObjGroupList) {
                if (insObjGroup.get("OBJLIST") != null) {
                    List<Map<String, Object>> insObjList = (List<Map<String, Object>>) insObjGroup.get("OBJLIST");
                    if (insObjList.isEmpty()) {
                        return contract;
                    }
                    for (Map<String, Object> insObj : insObjList) {
                        if (insObj.get("CONTROBJMAP") != null) {
                            Map<String, Object> contrObj = (Map<String, Object>) insObj.get("CONTROBJMAP");
                            if (contrObj.get("CONTRRISKLIST") != null) {
                                List<Map<String, Object>> riskList = (List<Map<String, Object>>) contrObj.get("CONTRRISKLIST");
                                if (riskList.isEmpty()) {
                                    return contract;
                                }
                                for (Map<String, Object> risk : riskList) {

                                    if ("deathStateAccumulations".equalsIgnoreCase(getStringParam(risk.get("PRODRISKSYSNAME")))) {
                                        // смерть в период накоплений
                                        // CC - стоимость гарантийного фонда , мы предлагаем указывать  первую сумму
                                        // из графика гарантийного фонда.
                                        // дата начала - дата начала действия договора
                                        // дата окончания - дата начала выплат - 1 день
                                        risk.put("STARTDATE", contract.get("STARTDATE"));
                                        // TODO: походу неправильно считается фром1дата. т.к. совпадает с датой
                                        // оформления договора, а должна с датой начала выплат (т.к. дожитие
                                        // начинает действовать с момента окончания периода накоплений)
                                        Date riskFinishDate = getDateParam(contract.get("FINISHDATE"));
                                        GregorianCalendar gc = new GregorianCalendar();
                                        gc.setTime(riskFinishDate);
                                        gc.set(Calendar.HOUR, 23);
                                        gc.set(Calendar.MINUTE, 59);
                                        gc.set(Calendar.SECOND, 59);
                                        risk.put("FINISHDATE", df.format(gc.getTime()));
                                        //Сумма премии определяется в разбивке по покрытиям и затем суммируется. Если
                                        //неизвестна разбивка премии по покрытиям - ее нужно указать для одного
                                        //покрытия (сделав равной общей премии по полису) и обнулить для всех
                                        //остальных..
                                        risk.put("PREMVALUE", contract.get("PREMVALUE"));
                                        //Смерть ЗЛ по ЛП в течение периода накоплений (deathStateAccumulations)
                                        //СС  =  стоимость гарантийного фонда , мы предлагаем указывать  первую сумму
                                        //из графика гарантийного фонда.
                                        risk.put("INSAMVALUE", contrExtMap.get("fixedRentValue"));

                                    }
                                    if ("survival".equalsIgnoreCase(getStringParam(risk.get("PRODRISKSYSNAME")))) {
                                        // дожитие
                                        risk.put("STARTDATE", contrExtMap.get("rentBeginDATE"));
                                        // TODO: походу неправильно считается фром1дата. т.к. совпадает с датой
                                        // оформления договора, а должна с датой начала выплат (т.к. дожитие
                                        // начинает действовать с момента окончания периода накоплений)
                                        risk.put("FINISHDATE", contrExtMap.get("rentEndDATE"));
                                        risk.remove("PREMVALUE");
                                        risk.put("INSAMVALUE", contrExtMap.get("fixedRentValue"));
                                    }
                                    if ("deathStatePayments".equalsIgnoreCase(getStringParam(risk.get("PRODRISKSYSNAME")))) {
                                        // Смерть одного из ЗЛ по ЛП на этапе выплат
                                        // судя по всему риск попадается только в программе "Семейный фонд", а она в текущий момент исключена из продукта и не страхуется.
                                        risk.put("STARTDATE", contrExtMap.get("rentBeginDATE"));
                                        // TODO: походу неправильно считается фром1дата. т.к. совпадает с датой
                                        // оформления договора, а должна с датой начала выплат (т.к. дожитие
                                        // начинает действовать с момента окончания периода накоплений)
                                        risk.put("FINISHDATE", contrExtMap.get("rentEndDATE"));
                                        risk.remove("PREMVALUE");
                                        risk.put("INSAMVALUE", contrExtMap.get("fixedRentValue"));

                                    }
                                    if ("deathGuaranteedPayments".equalsIgnoreCase(getStringParam(risk.get("PRODRISKSYSNAME")))) {
                                        //смерть в гарантированный период.
                                        risk.put("STARTDATE", contrExtMap.get("rentBeginDATE"));

                                        risk.put("FINISHDATE", contrExtMap.get("inheritedPaymentendDATE"));
                                        //Для покрытия смерть в гарантированном периоде указываются те же суммы что и
                                        //в покрытии дожитие.
                                        risk.remove("PREMVALUE");
                                        risk.put("INSAMVALUE", contrExtMap.get("fixedRentValue"));
                                    }

                                }

                            }
                        }
                    }

                }
            }
        }

        logger.debug("Additional contract secondary product depended attributes generation for NewHorizons finished.");
        return contract;
    }

    private String getPaymentVariantNameById(Long paymentVariantId, String login, String password) throws Exception {
        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("PAYVARID", paymentVariantId);
        List<Map<String, Object>> prodPayVarList = loadHandbookData(hbParams, "B2B.SBSJ.NewHorizons.PayVar", login, password);
        if (prodPayVarList != null) {
            for (Map<String, Object> bean : prodPayVarList) {
                Map<String, Object> payVarMap = bean;
                if (paymentVariantId != null) {
                    if (getLongParam(payVarMap.get("PAYVARID")) != null) {
                        if (getLongParam(payVarMap.get("PAYVARID")).longValue() == paymentVariantId.longValue()) {
                            return getStringParam(payVarMap.get("NAME"));
                        }
                    }
                }
            }
        }
        return null;
    }

    private void processExtParam(Map<String, Object> loadRes, Map<String, Object> contrExtMap, String login, String password)throws Exception {
        // B2B. "Новые горизонты". Тип продукта
        Long productId = getLongParamLogged(contrExtMap, "productId");
        if (productId != null) {
            // используется справочник
            Map<String, Object> hbParams = new HashMap<String, Object>();
            hbParams.put("hid", productId);
            List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.NewHorizons.TypeProduct", login, password);
            if ((filteredList != null) && (filteredList.size() == 1)) {
                Map<String, Object> selectedItem = filteredList.get(0);
                contrExtMap.put("productSelectedItem", selectedItem);
                contrExtMap.put("productStr", getStringParamLogged(selectedItem, "name"));
            }
            Date FromDate1 = null;
            Date ToDate1 = null;
            Date FromDate2 = null;
            Date ToDate2 = null;
            // Рента на срок.
            if (productId == 1L) {
                Date rentBeginDATE = getDateParam(contrExtMap.get("rentBeginDATE"));
                Date rentEndDATE = getDateParam(contrExtMap.get("rentEndDATE"));
                Long periodEndValue = 0L;
                if (null != contrExtMap.get("periodEndValue")) {
                    periodEndValue = getDoubleParam(contrExtMap.get("periodEndValue")).longValue();
                }
                Date startDate = (Date) parseAnyDate(loadRes.get("STARTDATE"), Date.class, "STARTDATE");
                Map<String, Object> productMap = (Map<String, Object>) loadRes.get("PRODUCTMAP");
                String paymentVariantSysName = getPaymentVariantSysNameById(productMap, getLongParamLogged(contrExtMap, "payPeriod"), login, password);
                contrExtMap.put("PAYMENTVARSYSNAME",paymentVariantSysName);
                if (periodEndValue == 1L) {
                    FromDate1 = (Date) parseAnyDate(loadRes.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
                    GregorianCalendar sdgc = new GregorianCalendar();
                    sdgc.setTime(rentEndDATE);
                    sdgc.add(GregorianCalendar.MONTH, -1 * (Long.valueOf((paymentVariantSysName.equalsIgnoreCase("ANNUALLY") ? 12 : ((paymentVariantSysName.equalsIgnoreCase("QUARTERLY")) ? 3 : 1)))).intValue());
                    ToDate1 = sdgc.getTime();
                    sdgc.add(GregorianCalendar.DAY_OF_YEAR, 1);
                    FromDate2 = sdgc.getTime();
                    ToDate2 = rentEndDATE;
                } else {
                    FromDate1 = startDate;
                    ToDate1 = rentEndDATE;
                }
            } else {
                Long waitingPeriod = getLongParam(contrExtMap, "waitingPeriod");
                if ((waitingPeriod == null) || ((waitingPeriod != null) && (waitingPeriod >= 0))) {
                    // Период ожидания до начала выплат (лет)
                    // Период распределения повышенной ренты
                    Long ratioIncreasedRentPeriod = getLongParam(contrExtMap, "ratioIncreasedRentPeriod");
                    if (ratioIncreasedRentPeriod == null) {
                        ratioIncreasedRentPeriod = 0L;
                    }
                    Date rentEndDATE = getDateParam(contrExtMap.get("rentEndDATE"));
                    GregorianCalendar gcRentEndDATE = new GregorianCalendar();
                    gcRentEndDATE.setTime(rentEndDATE);
                    Date startDate = (Date) parseAnyDate(loadRes.get("STARTDATE"), Date.class, "STARTDATE");
                    FromDate1 = (Date) parseAnyDate(loadRes.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
                    GregorianCalendar sdgc = new GregorianCalendar();
                    sdgc.setTime(startDate);
                    sdgc.add(GregorianCalendar.YEAR, Long.valueOf(waitingPeriod + ratioIncreasedRentPeriod).intValue());
                    sdgc.set(GregorianCalendar.MONTH, gcRentEndDATE.get(GregorianCalendar.MONTH));
                    sdgc.set(GregorianCalendar.DAY_OF_MONTH, gcRentEndDATE.get(GregorianCalendar.DAY_OF_MONTH));
                    ToDate1 = sdgc.getTime();
                    sdgc.add(GregorianCalendar.DAY_OF_YEAR, 1);
                    FromDate2 = sdgc.getTime();
                    ToDate2 = rentEndDATE;
                }
            }
            contrExtMap.put("From1DATE", FromDate1);
            contrExtMap.put("To1DATE", ToDate1);
            if (null != FromDate2) {
                contrExtMap.put("From2DATE", FromDate2);
            } else {
                contrExtMap.put("From2DATE", "-");
            }
            if (null != ToDate2) {
                contrExtMap.put("To2DATE", ToDate2);
            } else {
                contrExtMap.put("To2DATE", "-");
            }
        }

    }

    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> dsB2BNewHorizontProcessExtParam(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contrMap = (Map<String, Object>) params.get("CONTRMAP");
        Map<String, Object> contrExtMap = (Map<String, Object>) contrMap.get("CONTREXTMAP");
        processExtParam(contrMap, contrExtMap, login, password);
        return contrExtMap;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BNewHorizontPrintDocDataProvider(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> loadRes = null;
        if (params.get("CONTRID") != null) {
            Long contractID = getLongParamLogged(params, "CONTRID");
            logger.debug("Export: begin prepare data for contrid = " + contractID);

            Map<String, Object> loadParam = new HashMap<String, Object>();
            loadParam.put("CONTRID", contractID);
            loadParam.put("SKIPDATESRECALC", true);
            loadParam.put(RETURN_AS_HASH_MAP, true);
            String dataProviderMethodName = "dsB2BSberLifePrintDocDataProvider";
            try {
                loadRes = this.callServiceTimeLogged(Constants.B2BPOSWS, dataProviderMethodName, loadParam, login, password);
            } catch (Exception ex) {
                loadRes = new HashMap<String, Object>();
                loadRes.put("CONTRID", contractID);
                loadRes.put("ERROR", ex.getMessage());
                logger.error(String.format("Exception during getting contract data by %s for contract with CONTRID = %d! Details: ", dataProviderMethodName, contractID), ex);
            }

            Map<String, Object> contrExtMap = (Map<String, Object>) loadRes.get("CONTREXTMAP");
            // B2B. "Новые горизонты". Тип программы
            Long programId = getLongParamLogged(contrExtMap, "programId");
            if (programId != null) {
                // используется справочник
                Map<String, Object> hbParams = new HashMap<String, Object>();
                hbParams.put("hid", programId);
                List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.NewHorizons.TypeProgram", login, password);
                if ((filteredList != null) && (filteredList.size() == 1)) {
                    Map<String, Object> selectedItem = filteredList.get(0);
                    contrExtMap.put("programSelectedItem", selectedItem);
                    contrExtMap.put("programStr", getStringParamLogged(selectedItem, "name"));
                }
            }
            Map<String, Object> hbParams = new HashMap<String, Object>();
            List<Map<String, Object>> trancheList = loadHandbookData(hbParams, "B2B.NewHorizons.TrancheSchedule", login, password);
            Date date = new Date();

            DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

            if ((trancheList != null) && (trancheList.size() > 1)) {
                for (Map<String, Object> tranche : trancheList) {
                    Date beginDate = new Date(0);
                    if (null != tranche.get("beginDATE")) {
                        beginDate = (Date) tranche.get("beginDATE");
                    }
                    // 73051 - 01.01.2100
                    Date endDate = new Date(73051);
                    if (null != tranche.get("endDATE")) {
                        endDate = (Date) tranche.get("endDATE");
                    }
                    if ((date.after(beginDate)) && (date.before(endDate))) {
                        Date trancheDate = (Date) tranche.get("endDATE");
                        loadRes.put("PAYMENTENDDATE", trancheDate);
                    }
                }
            }
            // B2B. "Новые горизонты". Тип продукта
            processExtParam(loadRes, contrExtMap, login, password);

//            Long productId = getLongParamLogged(contrExtMap, "productId");
//            if (productId != null) {
//                // используется справочник
//                hbParams = new HashMap<String, Object>();
//                hbParams.put("hid", productId);
//                List<Map<String, Object>> filteredList = loadHandbookData(hbParams, "B2B.NewHorizons.TypeProduct", login, password);
//                if ((filteredList != null) && (filteredList.size() == 1)) {
//                    Map<String, Object> selectedItem = filteredList.get(0);
//                    contrExtMap.put("productSelectedItem", selectedItem);
//                    contrExtMap.put("productStr", getStringParamLogged(selectedItem, "name"));
//                }
//                Date FromDate1 = null;
//                Date ToDate1 = null;
//                Date FromDate2 = null;
//                Date ToDate2 = null;
//                // Рента на срок.
//                if (productId == 1L) {
//                    Date rentBeginDATE = getDateParam(contrExtMap.get("rentBeginDATE"));
//                    Date rentEndDATE = getDateParam(contrExtMap.get("rentEndDATE"));
//                    Long periodEndValue = 0L;
//                    if (null != contrExtMap.get("periodEndValue")) {
//                        periodEndValue = getDoubleParam(contrExtMap.get("periodEndValue")).longValue();
//                    }
//                    Date startDate = (Date) parseAnyDate(loadRes.get("STARTDATE"), Date.class, "STARTDATE");
//                    Map<String, Object> productMap = (Map<String, Object>) loadRes.get("PRODUCTMAP");
//                    String paymentVariantSysName = getPaymentVariantSysNameById(productMap, getLongParamLogged(contrExtMap, "payPeriod"), login, password);
//                    if (periodEndValue == 1L) {
//                        FromDate1 = (Date) parseAnyDate(loadRes.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
//                        GregorianCalendar sdgc = new GregorianCalendar();
//                        sdgc.setTime(rentEndDATE);
//                        sdgc.add(GregorianCalendar.MONTH, -1 * (Long.valueOf((paymentVariantSysName.equalsIgnoreCase("ANNUALLY") ? 12 : ((paymentVariantSysName.equalsIgnoreCase("QUARTERLY")) ? 3 : 1)))).intValue());
//                        ToDate1 = sdgc.getTime();
//                        sdgc.add(GregorianCalendar.DAY_OF_YEAR, 1);
//                        FromDate2 = sdgc.getTime();
//                        ToDate2 = rentEndDATE;
//                    } else {
//                        FromDate1 = startDate;
//                        ToDate1 = rentEndDATE;
//                    }
//                } else {
//                    Long waitingPeriod = getLongParam(contrExtMap, "waitingPeriod");
//                    if ((waitingPeriod == null) || ((waitingPeriod != null) && (waitingPeriod >= 0))) {
//                        // Период ожидания до начала выплат (лет)
//                        // Период распределения повышенной ренты
//                        Long ratioIncreasedRentPeriod = getLongParam(contrExtMap, "ratioIncreasedRentPeriod");
//                        if (ratioIncreasedRentPeriod == null) {
//                            ratioIncreasedRentPeriod = 0L;
//                        }
//                        Date rentEndDATE = getDateParam(contrExtMap.get("rentEndDATE"));
//                        GregorianCalendar gcRentEndDATE = new GregorianCalendar();
//                        gcRentEndDATE.setTime(rentEndDATE);
//                        Date startDate = (Date) parseAnyDate(loadRes.get("STARTDATE"), Date.class, "STARTDATE");
//                        FromDate1 = (Date) parseAnyDate(loadRes.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
//                        GregorianCalendar sdgc = new GregorianCalendar();
//                        sdgc.setTime(startDate);
//                        sdgc.add(GregorianCalendar.YEAR, Long.valueOf(waitingPeriod + ratioIncreasedRentPeriod).intValue());
//                        sdgc.set(GregorianCalendar.MONTH, gcRentEndDATE.get(GregorianCalendar.MONTH));
//                        sdgc.set(GregorianCalendar.DAY_OF_MONTH, gcRentEndDATE.get(GregorianCalendar.DAY_OF_MONTH));
//                        ToDate1 = sdgc.getTime();
//                        sdgc.add(GregorianCalendar.DAY_OF_YEAR, 1);
//                        FromDate2 = sdgc.getTime();
//                        ToDate2 = rentEndDATE;
//                    }
//                }
//                contrExtMap.put("From1DATE", FromDate1);
//                contrExtMap.put("To1DATE", ToDate1);
//                if (null != FromDate2) {
//                    contrExtMap.put("From2DATE", FromDate2);
//                } else {
//                    contrExtMap.put("From2DATE", "-");
//                }
//                if (null != ToDate2) {
//                    contrExtMap.put("To2DATE", ToDate2);
//                } else {
//                    contrExtMap.put("To2DATE", "-");
//                }
//            }
            genDateStrs(contrExtMap, "*");

            Long waitingPeriod = getLongParamLogged(contrExtMap, "waitingPeriod");
            if ((waitingPeriod == null) || ((waitingPeriod != null) && (waitingPeriod == 0))) {
                contrExtMap.put("FundedPeriodStr", "1 месяц");
            } else {
                Long wpMod = waitingPeriod % 10;
                if ((0 == wpMod) || (4 < wpMod)) {
                    contrExtMap.put("FundedPeriodStr", waitingPeriod + " лет");
                } else if ((1 == wpMod)) {
                    contrExtMap.put("FundedPeriodStr", waitingPeriod + " год");
                } else {
                    contrExtMap.put("FundedPeriodStr", waitingPeriod + " года");
                }
                Map<String, Object> prodConfMap = (Map<String, Object>) loadRes.get("PRODUCTMAP");

                Date sd = (Date) parseAnyDate(contrExtMap.get("rentBeginDATE"), Date.class, "rentBeginDATE");
                if (null != sd) {
                    GregorianCalendar documentDateSD = new GregorianCalendar();
                    documentDateSD.setTime(sd);
                    Object inheritedPeriodBean = contrExtMap.get("countPayYear");
                    Long inheritedPeriod = inheritedPeriodBean == null ? 0L : 0L;//getLongParam(inheritedPeriodBean);
                    documentDateSD.add(Calendar.YEAR, inheritedPeriod.intValue());
                    df = new SimpleDateFormat("dd.MM.yyyy");
                    contrExtMap.put("inheritedPaymentbeginDATE", df.format(documentDateSD.getTime()));
                }
            }
            Object payPeriodBean = contrExtMap.get("payPeriod");
            if (payPeriodBean != null) {
                contrExtMap.put("payPeriodStr", getPaymentVariantNameById(getLongParam(payPeriodBean), login, password));
            } else {
                contrExtMap.put("payPeriodStr", "Ежегодно");
            }
            logger.debug("Export: end prepare data for  contrid = " + contractID);

            // Сортировка гарантийного фонда по номеру платежа
            List<Map<String, Object>> savingScheduleList = (List<Map<String, Object>>) loadRes.get(SAVINGSSCHEDULE_SUM_LIST_PARAMNAME);
            CopyUtils.sortByLongFieldName(savingScheduleList, "PAYNUM");
            loadRes.put("GFLIST", savingScheduleList);
            
            //loadRes.put("GFLIST", loadRes.get(SAVINGSSCHEDULE_SUM_LIST_PARAMNAME));
            loadRes.put("RENTLIST", loadRes.get(REDEMPTION_SUM_LIST_PARAMNAME));
            loadRes.remove(REDEMPTION_SUM_LIST_PARAMNAME);
            loadRes.remove(SAVINGSSCHEDULE_SUM_LIST_PARAMNAME);
        }

        return loadRes;
    }


}
