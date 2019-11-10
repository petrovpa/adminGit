package com.bivgroup.services.b2bposws.facade.pos.custom.sberlife;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.text.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * Кастомный фасад продукта "Защищенный заемщик многолетний"
 *
 * @author averichevsm
 */
@BOName("BorrowerProtectLongTermCustom")
public class BorrowerProtectLongTermCustomFacade extends ProductContractCustomFacade {

    // Кастомные ошибки валидации

    public static final String MISSING_REQUIRED_PARAMETER = "Отсутствует обязательный параметр: ";
    private static final String START_DATE_EARLIER_THAN_CURRENT_DATE_ERROR_TEXT = "Дата начала договора ранее " +
            "текущей даты. ";
    private static final String EXPIRATION_DATE_DOES_NOT_CORRESPOND_TO_THE_PERIODICITY_OF_12_MONTHS_ERROR_TEXT = "Дата" +
            " окончания договора не соответствует периодичности 12 месяцев. ";
    private static final String CLIENT_CAN_NOT_BE_INSURED_BY_AGE_ERROR_TEXT = "Клиент не может быть застрахован по " +
            "возрасту. ";
    private static final String CONSENT_DATE_EARLIER_CURRENT_DATE_ERROR_TEXT = "Дата согласия на обработку персональных " +
            "данных ранее текущей даты. ";
    private static final String SIGN_DATE_EARLIER_CURRENT_DATE_ERROR_TEXT = "Дата подписания договора ранее текущей даты. ";
    private static final String PASSPORT_DATE_INCORECT_BEFORE_14YEARS_ERROR_TEXT = "Дата выдачи паспорта некорректна: " +
            "паспорт не может быть выдан ранее 14 лет с даты рождения. ";
    private static final String MAX_INSAMVALUE_INCORECT_ERROR_TEXT = "Максимальный размер страховой суммы не " +
            "болеее 10 000 000 рублей. ";
    private static final String PREMIUM_FIRST_PERIOD_NOT_MATCH_CALC_PREMIUIM_ERROR_TEXT = "Премия за первый период не " +
            "соответствует рассчитанной премии. ";
    private static final String LOAN_PERIOD_EXCEEDED_ERROR_TEXT = "Срок кредитования превышен. ";
    private static final String INTEREST_RATE_DIAPOSON_WRON_ERROR_TEXT = " Процентная ставка должна находится в диапазоне от %d до d%. Введите корректную процентную ставку . ";

    private final Logger logger = Logger.getLogger(this.getClass());

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

    @WsMethod(requiredParams = {"CALCVERID", "loanIssueDATE", "percentRate", "insAmValue",
            "termInsuranceYear", "termInsuranceMonth", "gender", "insBirthDATE"})
    public Map<String, Object> dsB2BBorrowerProtectLongTermCalc(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BBorrowerProtectLongTermCalc");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> calcParams = new HashMap<String, Object>();
        calcParams.putAll(params);
        calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Date insBirthDate = (Date) parseAnyDate(params.get("insBirthDATE"), Date.class, "insBirthDATE");
        long insAge = calcAge(insBirthDate);
        calcParams.put("age", insAge);
        // подготовка yearList
        Double insAmValue = getDoubleParam(params, "insAmValue");
        Long termInsuranceYear = getLongParam(params, "termInsuranceYear");
        Long termInsuranceMonth = getLongParam(params, "termInsuranceMonth");
        List<Map<String, Object>> yearList = new ArrayList<>();
        GregorianCalendar gcCurStartDate = new GregorianCalendar();
        gcCurStartDate.setTime((Date) parseAnyDate(params.get("loanIssueDATE"), Date.class, "loanIssueDATE"));
        //gcCurStartDate.add(Calendar.DAY_OF_YEAR, 1);
        // расчет ОСЗ (по месяцам)
        Double p = getDoubleParam(params, "percentRate") / 1200.0;
        Long monthsActual = termInsuranceMonth; //Math.min(termInsuranceMonth, termInsuranceYear * 12);
        Double annuitPay = insAmValue * p * (1 + 1 / (Math.pow(1 + p, monthsActual) - 1));
        List<Double> oszMonth = new ArrayList<>();
        // 0й - СК
        oszMonth.add(insAmValue);
        // 1й - СК
        oszMonth.add(insAmValue);
        // рассчитываем остальные
        for (int i = 2; i <= termInsuranceMonth + 1; i++) {
            Double oszI = oszMonth.get(i - 1) - (annuitPay - oszMonth.get(i - 1) * p);
            oszMonth.add(oszI);
        }
        // формирование списка (по годам)
        for (int k = 1; k <= termInsuranceYear; k++) {
            Map<String, Object> yearMap = new HashMap<>();
            yearMap.put("yearNum", k);
            int month =  12 * (k - 1) + 1;
            /*if (month == 1) {
                month = 0;
            }*/
            yearMap.put("insAmValue", roundSum(oszMonth.get(month)));
            yearMap.put("ageOnYear", insAge + k - 1);
            yearMap.put("startDate", gcCurStartDate.getTime());
            gcCurStartDate.add(Calendar.YEAR, 1);
            
            GregorianCalendar gcCurFinishDate = new GregorianCalendar();
            gcCurFinishDate.setTime(gcCurStartDate.getTime());
            gcCurFinishDate.add(Calendar.DAY_OF_YEAR, -1);
            //if (k != termInsuranceYear) {
                yearMap.put("finishDate", gcCurFinishDate.getTime());

            //}

            if (k == termInsuranceYear) {
                yearMap.put("lastYear", true);
            }

            yearList.add(yearMap);
        }
        calcParams.put("yearList", yearList);
        //
        logger.debug("calcParams: " + calcParams);
        Map<String, Object> calcRes = this.callService(Constants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcParams, login, password);
        logger.debug("calcRes: " + calcRes);

        logger.debug("after dsB2BBorrowerProtectLongTermCalc");
        return calcRes;
    }

    protected Map<String, Object> getProdStructBySysName(List<Map<String, Object>> prodStructs, String sysNameKey, String sysName) {
        if ((prodStructs != null) && (!sysName.isEmpty())) {
            for (Map<String, Object> prodStruct : prodStructs) {
                String prodStructSysName = getStringParam(prodStruct.get(sysNameKey));
                if (sysName.equalsIgnoreCase(prodStructSysName)) {
                    return prodStruct;
                }
            }
        }
        return null;
    }

    @WsMethod(requiredParams = {"CONTRMAP", "FULLPRODMAP"})
    public Map<String, Object> dsB2BBorrowerProtectLongTermCalcByContrMap(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BBorrowerProtectLongTermCalcByContrMap");
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
            logger.debug("after dsB2BBorrowerProtectLongTermCalcByContrMap");
            return result;
        }

        // данные договора корректны - можно выполнять вычисления
        Map<String, Object> contrExtMap = (Map<String, Object>) contractMap.get("CONTREXTMAP");
        Map<String, Object> productMap = (Map<String, Object>) params.get("FULLPRODMAP");

        Object insAmValue = contractMap.get("INSAMVALUE");
        Object calcVerId = productMap.get("CALCVERID");
        Object startDate = contractMap.get("STARTDATE");
        Double percentRate = getDoubleParam(contrExtMap, "percentRate");
        Object termInsuranceYear = contrExtMap.get("termInsuranceYear");
        Object termCreditingMonth = contrExtMap.get("termCreditingMonth");
        Object insuredGender = contrExtMap.get("insuredGender");
        Object insuredBirthDate = contrExtMap.get("insuredBirthDATE");
        String calcServiceName = Constants.B2BPOSWS;
        String calcMethodName = "dsB2BBorrowerProtectLongTermCalc";
        Map<String, Object> resMap;
        Map<String, Object> calcParams = new HashMap<>();
        if (
                insAmValue == null || calcVerId == null || startDate == null || (percentRate < MIN_SIGNIFICANT_RATE) ||
                        termInsuranceYear == null || termCreditingMonth == null || insuredGender == null || insuredBirthDate == null
                ) {
            // недостаточно обязательных параметров для вызова калькулятора
            resMap = new HashMap<>();
        } else {
            calcParams.put("CALCVERID", calcVerId);
            calcParams.put("loanIssueDATE", startDate);
            calcParams.put("percentRate", percentRate);
            calcParams.put("insAmValue", insAmValue);
            calcParams.put("termInsuranceYear", termInsuranceYear);
            calcParams.put("termInsuranceMonth", termCreditingMonth);
            calcParams.put("gender", insuredGender);
            calcParams.put("insBirthDATE", insuredBirthDate);
            calcParams.put(RETURN_AS_HASH_MAP, true);
            resMap = callServiceLogged(calcServiceName, calcMethodName, calcParams, login, password);
        }

        // обработка результата калькулятора
        if (resMap.get("yearList") != null) {
            Map<String, Object> prodVerMap = (Map<String, Object>) productMap.get("PRODVER");
            List<Map<String, Object>> prodStructs = (List<Map<String, Object>>) prodVerMap.get("PRODSTRUCTS");
            //Map<String, Object> deathDisabilityMap = this.getProdStructBySysName(prodStructs, "SYSNAME", "deathDisability");
            Map<String, Object> deathDisabilityMap = this.getProdStructBySysName(prodStructs, "SYSNAME", "MAINPROGRAM_CLPBM");
            if (deathDisabilityMap != null) {
                // генерация рисков (старые просто удаляем, формируем новые)
                List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contractMap.get("INSOBJGROUPLIST");

                if ((insObjGroupList == null) || (insObjGroupList.isEmpty())) {
                    // если по какой-то причине отсутствует дерево страховой структуры продукта договора,
                    // то следует его сгенерировать на основании данных из продука,
                    // поскольку оно необходимо для обработки результатов калькулятора
                    updateContractInsuranceProductStructure(contractMap, productMap, false, "", true, login, password);
                    insObjGroupList = getListParam(contractMap, "INSOBJGROUPLIST");
                }

                List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupList.get(0).get("OBJLIST");
                Map<String, Object> contrObjMap = (Map<String, Object>) objList.get(0).get("CONTROBJMAP");
                List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                for (Iterator<Map<String, Object>> iterator = contrRiskList.iterator(); iterator.hasNext(); ) {
                    Map<String, Object> riskBean = iterator.next();
                    if (riskBean.get("CONTRRISKID") != null) {
                        riskBean.put(ROWSTATUS_PARAM_NAME, RowStatus.DELETED.getId());
                    } else {
                        iterator.remove();
                    }
                }

                Double totalPremium = 0.0;
                Double premiumFirstYear = null;
                List<Map<String, Object>> yearList = (List<Map<String, Object>>) resMap.get("yearList");
                for (Map<String, Object> yearBean : yearList) {
                    Map<String, Object> riskMap = new HashMap<>();
                    riskMap.put(ROWSTATUS_PARAM_NAME, RowStatus.INSERTED.getId());
                    if (contrObjMap.get("CONTROBJID") != null) {
                        riskMap.put("CONTROBJID", contrObjMap.get("CONTROBJID"));
                    }
                    riskMap.put("PRODSTRUCTID", deathDisabilityMap.get("PRODSTRUCTID"));
                    riskMap.put("PRODRISKSYSNAME", deathDisabilityMap.get("SYSNAME"));
                    Double riskPremium = getDoubleParam(yearBean, "premium");
                    riskMap.put("PREMVALUE", riskPremium);
                    riskMap.put("INSAMVALUE", yearBean.get("insAmValue"));
                    riskMap.put("TARIFFVALUE", yearBean.get("tariff"));

                    totalPremium += riskPremium;

                    // Страховая премия за первый год
                    if (premiumFirstYear == null) {
                        premiumFirstYear = riskPremium;
                    }

                    /*Date startDate = (Date) parseAnyDate(yearBean.get("startDate"), Date.class, "startDate");
                    GregorianCalendar calendarStartDate = new GregorianCalendar();
                    calendarStartDate.setTime(startDate);
                    calendarStartDate.add(Calendar.DAY_OF_YEAR, -1);*/

                    riskMap.put("STARTDATE", parseAnyDate(yearBean.get("startDate"), Double.class, "startDate"));
                    if (yearBean.get("lastYear") == null) {
                        riskMap.put("FINISHDATE", parseAnyDate(yearBean.get("finishDate"), Double.class, "finishDate"));
                    } else {
                        Double yearFinishDate = (Double) parseAnyDate(yearBean.get("finishDate"), Double.class, "finishDate");
                        Double contractFinishDate = (Double) parseAnyDate(contractMap.get("FINISHDATE"), Double.class, "FINISHDATE");

                        riskMap.put("FINISHDATE", Math.min(yearFinishDate, contractFinishDate) /*contractMap.get("FINISHDATE")*/);
                    }
                    riskMap.put("INSAMCURRENCYID", contrObjMap.get("INSAMCURRENCYID"));
                    riskMap.put("PREMCURRENCYID", contrObjMap.get("PREMCURRENCYID"));
                    riskMap.put("CURRENCYID", contrObjMap.get("CURRENCYID"));
                    contrRiskList.add(riskMap);
                }

                totalPremium = roundSum(totalPremium);
                contrObjMap.put("PREMVALUE", totalPremium);

                premiumFirstYear = roundSum(premiumFirstYear);
                contrExtMap.put("totalPremValueFirstYear", premiumFirstYear);

                if (contrObjMap.get("CONTROBJID") != null) {
                    contrObjMap.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED.getId());
                }
                contractMap.put("PREMVALUE", totalPremium);
            }
        }

        // определение необходимости проверки на корректность выполненного расчета
        boolean isValidationRequired = isValidationRequired(contractMap);
        if (isValidationRequired) {
            // упрощенная доп. проверка на корректность выполненного расчета
            // премия по договору должна быть значимая
            Double contractPremiumValue = roundSum(getDoubleParamLogged(contractMap, PREMVALUE_FIELDNAME));
            if (contractPremiumValue < MIN_SIGNIFICANT_SUM) {
                loggerErrorServiceCall(calcServiceName, calcMethodName, calcParams, resMap);
                contractMap.put(ERROR, "Не удалось выполнить расчёт для указанных входных данных!");
            }
        } else {
            logger.debug("Creating new contract empty template - contract sum calculation result validation skipped.");
        }

        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        contractMap.put("CONTREXTMAP", contrExtMap);
        result.put("CONTRMAP", contractMap);
        logger.debug("after dsB2BBorrowerProtectLongTermCalcByContrMap");
        return result;
    }

    // проверка наличия премии по договору
    // метод переопределен в данном фасаде, поскольку перед расчетом не требуется подобная проверка
    @Override
    protected boolean checkContractPremValue(Map<String, Object> contract, boolean isPreCalcCheck, StringBuffer errorText) {
        boolean isPremValueExists;
        if (isPreCalcCheck) {
            isPremValueExists = checkBeanValueExist(contract, "PREMVALUE");
        } else {
            isPremValueExists = checkContractValueExist(contract, "PREMVALUE", "Страховой взнос", errorText);
        }
        return isPremValueExists;
    }

    // проверка наличия обязательных расширенных атрибутов договора
    // метод переопределен в текущем фасаде, поскольку для данного продукта требуется подобная проверка
    @Override
    protected void validateContractExtValues(Map<String, Object> contract, Map<String, Object> contractExtValues, boolean isFixContr, StringBuffer errorText) {
        checkContractExtValueExist(contractExtValues, "insuredGender", "Пол застрахованного", errorText);
        checkContractExtValueExist(contractExtValues, "insuredBirthDATE", "Дата рождения застрахованного", errorText);
        checkContractExtValueExist(contractExtValues, "insuredDeclCompliance", "Клиент соответствует декларации", errorText);
        checkContractExtValueExist(contractExtValues, "insurerIsInsured", "Страхователь является застрахованным", errorText);
    }

    @Override
    protected String generateContrSer(Map<String, Object> contract, String serPrefix, String login, String password) throws Exception {
        // фиксированная серия для данного продукта
        return serPrefix;
    }

    @Override
    protected void generateContractSerNumDraft(Map<String, Object> contract, String login, String password) throws Exception {
        if (contract.get("CONTRNUMBER") != null) {
            return;
        }
        if (contract.get("PRODCONFID") == null) {
            return;
        }
        Long prodConfId = getLongParam(contract.get("PRODCONFID"));
        Map<String, Object> prodDefValMap = getProductDefaultValueByProdConfId(prodConfId, login, password);
        String seriesPrefix = getStringParam(prodDefValMap.get("CONTRSERIESPREFIX"));
        // получение альтернативной серии договора (если предусмотрена для данного способа создания договора)
        seriesPrefix = getAltSeriesPrefixIfNeeded(contract, contract, prodDefValMap, seriesPrefix, login, password);
        String contrSer = generateContrSer(contract, seriesPrefix, login, password);
        contract.put("CONTRPOLSER", contrSer);

        geterateContractSerNum(contract, login, password);
    }

    /**
     * копия фрагмента кода из B2BLifeBaseFacade#genAdditionalSaveParams
     * для возможности использовать отдельно от dsB2BBorrowerProtectLongTermContractPrepareToSave)
     * todo: исправить, когда/если будет найдено более адекватное решение по проблеме перевода состояния договора
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBorrowerProtectLongTermContractGenerateNumber(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> contract = params;
        // ниже - копия фрагмента кода из B2BLifeBaseFacade#genAdditionalSaveParams
        // для возможности использовать отдельно от dsB2BBorrowerProtectLongTermContractPrepareToSave)
        // todo: исправить, когда/если будет найдено более адекватное решение по проблеме перевода состояния договора
        /**
         *  Переходе из черновика безусловно регенерируем серию договора
         *  (т.к. серия может формироваться в зависимости от введенных данных),
         *  пример метод generateContrSer в {@link com.bivgroup.services.b2bposws.facade.pos.custom.sberlife.SmartPolicyFacade}
         *  А вот номер договора формируем только один раз при переходе из черновика в предпечать (повторно генерировать номер не нужно)
         */
        Long  prodConfId = getLongParam(contract, "PRODCONFID");
        if (prodConfId != null) {
            Map<String, Object> prodDefValMap = getProductDefaultValueByProdConfId(prodConfId, login, password);
            String autoNumSysName = getStringParam(prodDefValMap, "CONTRAUTONUMBERSYSNAME");
            String seriesPrefix = getStringParam(prodDefValMap, "CONTRSERIESPREFIX");
            // получение альтернативной серии договора (если предусмотрена для данного способа создания договора)
            seriesPrefix = getAltSeriesPrefixIfNeeded(contract, params, prodDefValMap, seriesPrefix, login, password);
            if (contract.get("CONTRPOLNUM") == null) {
                contract.put("CONTRPOLNUM", generateContrNum(autoNumSysName, login, password));
            }
            generateContrSer(contract, seriesPrefix, login, password);
        }
        // выше - копия фрагмента кода из B2BLifeBaseFacade#genAdditionalSaveParams
        // для возможности использовать отдельно от dsB2BBorrowerProtectLongTermContractPrepareToSave)
        // todo: исправить, когда/если будет найдено более адекватное решение по проблеме перевода состояния договора
        return contract;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBorrowerProtectLongTermContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BBorrowerProtectLongTermContractPrepareToSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract = params;

        // получение полной мапы продукта
        // требуется в случае, если передано сис. наименование продукта
        Map<String, Object> fullProductMap = getFullProductMapFromContractMapOrLoadFromDB(contract, login, password);

        // для genAdditionalSaveParams.generateContractSerNumDraft параметр contract.PRODCONFID является обязательным
        // при вызове сохранения с интерфейса данный ИД помещается в мапу договора, вероятно, непосредственно на интерфейсе
        // (по неизвестным причинам, todo: уточнить у авторов generateContractSerNumDraft/b2b-интерфейса почему)
        // при вызове сохранения не из интерфейса (а, например, из веб-сервиса по OpenAPI) будет передано только сис. наименование версии продукта
        // для таких случаев PRODCONFID в мапу договора можно поместить из мапы продукта, которая получается в getFullProductMapFromContractMapOrLoadFromDB выше
        Long prodConfId = getLongParamLogged(fullProductMap, "PRODCONFID");
        if (prodConfId != null) {
            contract.putIfAbsent("PRODCONFID", prodConfId);
        }

        boolean isDataValid = this.validateContractSaveParams(contract, false, login, password);
        Map<String, Object> result;
        if (isDataValid) {
            result = genAdditionalSaveParams(contract, login, password);
            // todo: мб пренести 'вызов перерасчета сумм' в конец метода genProductAdditionalSaveParams (уточнить у авторов почему пересчет идет отдельно, после genAdditionalSaveParams и пр.)
            if (result.get("FULLPRODMAP") != null) {
                // вызов перерасчета сумм
                Map<String, Object> calcParams = new HashMap<String, Object>();
                calcParams.put(RETURN_AS_HASH_MAP, "TRUE");
                calcParams.put("CONTRMAP", result);
                calcParams.put("FULLPRODMAP", result.get("FULLPRODMAP"));
                Map<String, Object> calcRes = this.callService(Constants.B2BPOSWS, "dsB2BBorrowerProtectLongTermCalcByContrMap", calcParams, login, password);
                if (calcRes.get("CONTRMAP") != null) {
                    result = (Map<String, Object>) calcRes.get("CONTRMAP");
                }
                if (result.get("INSUREDMAP") == null && result.get("INSURERMAP") != null) {
                    result.put("INSUREDMAP", new HashMap<>((Map<String, Object>) result.get("INSURERMAP")));
                } else if (result.get("INSUREDMAP") != null && result.get("INSURERMAP") != null) {
                    Map insuredMap = (Map) result.get("INSUREDMAP");
                    Map insurerMap = (Map) result.get("INSURERMAP");
                    insuredMap.putAll(insurerMap);
                    //insuredMap.put("PARTICIPANTID", result.get("INSUREDID"));
                }
            }
        } else {
            result = contract;
        }
        logger.debug("after dsB2BBorrowerProtectLongTermContractPrepareToSave");
        return result;
    }

    /**
     * дополнительная генерация вторичных свойств договора (особая для конкретного продукта)
     * метод переопределен в фасаде данного продукта, поскольку требуется подобная генерация
     */
    @Override
    protected Map<String, Object> genProductAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {
        // инициализация атрибутов по умолчанию для страхователя и застрахованного
        for (String personMapKeyName : Arrays.asList("INSURERMAP", "INSUREDMAP")) {
            if (contract.containsKey(personMapKeyName)) {
                initDefaultInsPhysicMapByKey(contract, personMapKeyName);
            }
        }
        // проверка наличия PRODCONFID, PRODVERID и пр.
        // todo: возможно, вынести в общие методы по сохранению продукта (уточнив у сервисников по СБСЖ как эти ИД определяются в работающий продуктах)
        Long prodConfId = getLongParam(contract, "PRODCONFID");
        if (prodConfId == null) {
            Map<String, Object> product = getProductMapFromContractMapOrLoadFromDB(contract, login, password);
            prodConfId = getLongParam(product, "PRODCONFID");
            setGeneratedParamIfNull(contract, "PRODCONFID", prodConfId, true, true);
        }
        Long prodVerId = getLongParam(contract, "PRODVERID");
        if (prodVerId == null) {
            Map<String, Object> product = getProductMapFromContractMapOrLoadFromDB(contract, login, password);
            prodVerId = getLongParam(product, "PRODVERID");
            setGeneratedParamIfNull(contract, "PRODVERID", prodVerId, true, true);
        }
        // валюты по умолчнию
        setGeneratedParamIfNull(contract, "PREMCURRENCYID", CURRENCY_ID_RUB, true, true);
        setGeneratedParamIfNull(contract, "INSAMCURRENCYID", CURRENCY_ID_RUB, true, true);
        // проверка наличия дерева страховой структуры продукта договора
        List<Map<String, Object>> insObjGroupList = getListParam(contract, "INSOBJGROUPLIST");
        if ((insObjGroupList == null) || (insObjGroupList.isEmpty())) {
            // отсутствует дерево страховой структуры продукта договора, следует сформировать его на основании данных из продука
            Map<String, Object> product = getProductMapFromContractMapOrLoadFromDB(contract, login, password);
            updateContractInsuranceProductStructure(contract, product, false, "", true, login, password);
        }
        return contract;
    }

    // согласно письму от клиента от 30.09.2016:
    // "при невозможности подписать анкету происходит направление на андеррайтинг,
    // на текущий момент процесс андеррайтинга не финализирован,
    // вернемся с конечной информацией по данному вопросу в течении следующей недели"
    //
    // TODO: после получения от клиента окончательных критериев по андеррайтингу - не реализовывать это всё в этом фасаде, а по возможности использовать B2BLifeBaseFacade.underwritingCheck (универсальная, параметризованная реализация)
    @Override
    protected Long underwritingCheck(Map<String, Object> contract, String login, String password) throws Exception {

        logger.debug("Check contract underwriting...");

        // требуется ли андеррайтинг
        // 0L - нет, андеррайтинг не требуется
        // 1L - да, андеррайтинг требуется
        // 2L - недостаточно сведений, чтоб определить однозначно (например, не известна валюта и, как следствие, не проверить лимиты и пр.)
        Long UW = UW_DO_NOT_NEEDED;

        // проверка 'Наличие положительных ответов на вопросы Декларации застрахованного лица'
        Long insuredDeclCompliance = null;
        Map<String, Object> contrExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (contrExtMap != null) {
            insuredDeclCompliance = getLongParamLogged(contrExtMap, "insuredDeclCompliance");
        }
        if (insuredDeclCompliance == null) {
            // не найдена галка 'Клиент соответствует декларации застрахованного'
            logger.error(String.format("No client declaration check was found in this contract - underwriting check is failed!"));
            UW = UW_UNKNOWN;
        } else if (insuredDeclCompliance.intValue() == 0) {
            // галка 'Клиент соответствует декларации застрахованного' снята
            UW = UW_NEEDED;
        }
        logger.debug("UW = " + UW);
        contract.put("UW", UW);
        logger.debug("Check contract underwriting finished.");
        return UW;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BBorrowerProtectLongTermContractUnderwritingCheck(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BBorrowerProtectLongTermContractUnderwritingCheck");
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
        logger.debug("after dsB2BBorrowerProtectLongTermContractUnderwritingCheck");
        return result;
    }

    static String formatTariff(Double insTariffValue) {
        if (insTariffValue == null) {
            return "данные отсутствуют";
        }
        return String.format("%.3f", insTariffValue * 100);
    }

    static DecimalFormat currencyFormatter() {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);
        return formatter;
    }

    static Date stringToDate(String dateStr, String format) {
        if (dateStr.isEmpty()) {
            return null;
        }
        // todo: уточнить у автора зачем в провайдере данных для ПФ конвертация из строк в Date
        LocalDate localDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return date;
    }

    @WsMethod
    public Map dsB2BBorrowerProtectLongTermPrintDocDataProvider(Map params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        /*
        Map docDataCall = this.callExternalService("b2bposws", "dsB2BSberLifePrintDocDataProvider", params, login, password);
        */
        Map<String, Object> docDataCall = callService(B2BPOSWS_SERVICE_NAME, "dsB2BSberLifePrintDocDataProvider", params, login, password);
        Map reportData = (Map) docDataCall.computeIfAbsent("Result", o -> new HashMap());

        //reportData.putIfAbsent("PREMVALUESTR", String.format("%.2f", reportData.get("PREMVALUE")).replace('.', ','));
        reportData.remove("PREMVALUESTR");

        if (docDataCall.get("Status").equals("OK")) {
            // todo replace with normal risk list
            List<Map> riskList = (List<Map>) reportData.get("riskList"); /*new ArrayList<>();*/

            // отсортируем список рисков по дате начала
            riskList.sort(Comparator.comparing(map -> {
                String startDateStr = getStringParam(map, "STARTDATE");
                LocalDate localDate = LocalDate.parse(startDateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                return localDate;
            }));

            List<Map> PERIODINFOLIST = new ArrayList<>();

            for (Map risk : riskList) {
                Map infoMap = new HashMap();

                infoMap.put("PERIODSTARTDATESTR", risk.get("STARTDATESTR"));
                infoMap.put("PERIODFINISHDATESTR", risk.get("FINISHDATESTR"));
                infoMap.put("CHGINSAMVALUE", risk.get("INSAMVALUE"));

                infoMap.put("CHGINSAMVALUESTR", String.format("%.2f", (Double) infoMap.get("CHGINSAMVALUE")));

                infoMap.put("PAYBEFOREDATESTR", risk.get("STARTDATESTR"));
                infoMap.put("INSTARIFVALUESTR", formatTariff(getDoubleParam(risk, "TARIFFVALUE")));
                infoMap.put("CHGPREMVALUE", risk.get("PREMVALUE"));

                reportData.putIfAbsent("PREMVALUESTR", String.format("%.2f", risk.get("PREMVALUE")).replace('.', ','));

                //genSumStr(premChg, "CHGPREMVALUE");
                String CHGPREMVALUESTR = String.format("%.2f", (Double) infoMap.get("CHGPREMVALUE"));// currencyFormatter().format(premChg.get("CHGPREMVALUE"));
                infoMap.put("CHGPREMVALUESTR", CHGPREMVALUESTR);

                PERIODINFOLIST.add(infoMap);
            }
            reportData.put("PERIODINFOLIST", PERIODINFOLIST);
        }

        Map contrExtMap = (Map) reportData.get("CONTREXTMAP");
        if (contrExtMap != null) {
        reportData.put("CREDCONTRNUM", contrExtMap.get("creditContractNumber"));
        reportData.put("CREDCONTRDATE", contrExtMap.get("creditContractDATE"));
        reportData.put("CREDCONTRDATESTR", parseAnyDate(contrExtMap.get("creditContractDATE"), String.class, "CREDCONTRDATE"));
        }

        Date startDate = stringToDate(getStringParam(reportData, "STARTDATE"), "dd.MM.yyyy");
        reportData.put("STARTDATE", startDate);

        Date finishDate = stringToDate(getStringParam(reportData, "FINISHDATE"), "dd.MM.yyyy");
        reportData.put("FINISHDATE", finishDate);

        return docDataCall;

    }

    protected void getFinishDateByStartDateAndTermId(Map<String, Object> contract, String login, String password) throws Exception {
        if (contract.get("STARTDATE") != null) {
            Date startDate = (Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE", true);

            GregorianCalendar finishDateGC = new GregorianCalendar();
            finishDateGC.setTime(startDate);

            // Перед расчетом расширенных аттрибутов еще не будет, поэтому возмем кастомную реализацию
            Map contrExtMap = (Map) contract.get("CONTREXTMAP");
            if (contrExtMap == null) {
                super.getFinishDateByStartDateAndTermId(contract, login, password);
            } else {
                Long years = getLongParam(contrExtMap, "termInsuranceYear");
                if (years == null) {
                    Long months = getLongParam(contrExtMap, "termCreditingMonth");
                    if (months == null) {
                        // по умолчанию - один год (но сюда попадать без обязательных параметров не должна позволять серверная валидация)
                        // todo: уточнить у авторов продукта
                        months = 12L;
                    }
                    years = (long) Math.floor(((double) months) / 12);
                    setGeneratedParam(contrExtMap, "termInsuranceYear", years, true, true);
                }
                finishDateGC.add(Calendar.YEAR, years.intValue());
                finishDateGC.add(Calendar.DAY_OF_YEAR, -1);
                contract.put("FINISHDATE", finishDateGC.getTime());
            }
        }
    }

    @Override
    protected Map<String, Object> prepareCommonValidateParams(Map<String, Object> contract, StringBuffer errorText, boolean isPreCalcCheck, String login, String password) {
        // Подготовим общие параметры
        Map<String, Object> commonValidateParams = super.prepareCommonValidateParams(contract, errorText, isPreCalcCheck, login, password);

        // Подготовим параметры, которые относятся только к нашему продукту
        commonValidateParams.put("loanIssueDATE", commonValidateParams.get(CONTRACT_START_DATE));

        final Map<String, Object> contrExtMap = getMapParam(commonValidateParams, CONTREXT_MAP);

        commonValidateParams.put("percentRate", contrExtMap.get("percentRate"));
        commonValidateParams.put("insAmValue", Double.valueOf(commonValidateParams.get(INSAMVALUE).toString()));


        final Long termInsuranceYear = getLongParam(contrExtMap.get("termInsuranceYear"));
        final Long termCreditingMonth = getLongParam(contrExtMap.get("termCreditingMonth"));

        commonValidateParams.put("termInsuranceYear", termInsuranceYear);
        commonValidateParams.put("termInsuranceMonth", termCreditingMonth);
        commonValidateParams.put("gender", contrExtMap.get("insuredGender"));
        commonValidateParams.put("insBirthDATE", contrExtMap.get("insuredBirthDATE"));

        getFinishDateByTermYearAndTermMonth(commonValidateParams, termInsuranceYear, termCreditingMonth);

        return commonValidateParams;
    }
    // Блок имплементациии валидации интерфейса ContractLifeValidate
    // согласно https://rybinsk.bivgroup.com/redmine/issues/23062

    @Override
    public void validateContractStartDateBeforeCurrentDateFromContractValidateParams(Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        String errorMissingParam = MISSING_REQUIRED_PARAMETER + "Дата начала действия договора. ";

        if ((contractValidateParams == null) || (getDateParam(contractValidateParams.get(CONTRACT_START_DATE)) == null)) {
            errorText.append(errorMissingParam);
            return;
        }

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

        // Текущая дата
        Calendar gCurrentDate = new GregorianCalendar();
        gCurrentDate.setTime(new Date());

        // Дата начала действия договора
        Calendar gContractStartDate = new GregorianCalendar();
        gContractStartDate.setTime(getDateParam(contractValidateParams.get(CONTRACT_START_DATE)));

        final String currentDateStr;
        try {
            final Date currentDate = simpleDateFormat.parse(simpleDateFormat.format(gCurrentDate.getTime()));
            final Date contractStartDate = simpleDateFormat.parse(simpleDateFormat.format(gContractStartDate.getTime()));

            // Если дата начала ранее, чем тек. дата, то ошибка
            if (contractStartDate.before(currentDate)) {
                errorText.append(START_DATE_EARLIER_THAN_CURRENT_DATE_ERROR_TEXT);
            }

        } catch (ParseException e) {
            logger.error(String.format("validateContractStartDateBeforeCurrentDateFromContractValidateParams throw " +
                    "exception parse = %s", e));

            errorText.append(errorMissingParam);
        }
    }

    @Override
    public void validateContractEndDateForTheFirstPeriodFromContractValidateParams(Map<String, Object> contractValidateParams,
                                                                                   StringBuffer errorText, String login, String password) {
        // TODO проверка с датой упущена до уточнения с заказчиками (плохо проверять дату, которую мы сами вычисляем)
        //        String errorMissingParam = MISSING_REQUIRED_PARAMETER + "Дата начала действия договора. ";
//
//        final Date contractStartDate = getDateParam(contractValidateParams.get(CONTRACT_START_DATE));
//        final Date contractFinishDate = getDateParam(contractValidateParams.get(CONTRACT_FINISH_DATE));
//
//        if (contractFinishDate == null) {
//            errorText.append(errorMissingParam);
//            return;
//        }
//
//        if (contractStartDate == null) {
//            errorText.append("Не удалось проверить дату окончания договора, т.к. отсутствует дата начала договора. ");
//            return;
//        }
//
//        Calendar gContractFinishDate = new GregorianCalendar();
//        gContractFinishDate.setTime(contractFinishDate);
//
//        Calendar gContractFinishDateAssert = new GregorianCalendar();
//        gContractFinishDateAssert.setTime(contractStartDate);
//
//        // Добавим один год минус один день - это должна быть такая дата окончания на первый период
//        gContractFinishDateAssert.add(Calendar.YEAR, 1);
//        gContractFinishDateAssert.add(Calendar.DAY_OF_MONTH, -1);
//
//        // Если наша не равна проверяемой, то ошибка
//        if (!gContractFinishDate.equals(gContractFinishDateAssert)) {
//            errorText.append(EXPIRATION_DATE_DOES_NOT_CORRESPOND_TO_THE_PERIODICITY_OF_12_MONTHS_ERROR_TEXT);
//        }
    }

    @Override
    public void validateDateOfConsentToTheProcessingOfPersonalDataFromContractValidateParams(Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        String errorMissingParam = MISSING_REQUIRED_PARAMETER + ": Дата начала действия договора. ";
    }

    @Override
    public void validateDateOfSigningTheInsuranceContractFromContractValidateParams(Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        String errorMissingParam = MISSING_REQUIRED_PARAMETER + ": Дата начала действия договора. ";
    }

    @Override
    public void validateDateOfIssueOfThePassportOfTheInsuredFromContractValidateParams(Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        String errorMissingParam = MISSING_REQUIRED_PARAMETER + ": Дата начала действия договора. ";
    }

    @Override
    public void validateInsamvalueFromContractValidateParams(Map<String, Object> contractValidateParams, StringBuffer errorText, Long insAmValueMaxDefault, String login, String password) {
        String errorMissingParam = MISSING_REQUIRED_PARAMETER + "Страховая сумма (остаток по кредиту). ";


        final Long insAmValue = getLongParam(contractValidateParams.get(INSAMVALUE));
        if (insAmValue == null) {
            errorText.append(errorMissingParam);
            return;
        }

        final long insAmValueMin = 0;
        final long insAmValueMax = 10000000;

        if ((insAmValue < insAmValueMin) || (insAmValue >insAmValueMax)) {
            errorText.append(MAX_INSAMVALUE_INCORECT_ERROR_TEXT);
        }
    }

    @Override
    public void validateRateOfCreditFromContractValidateParams(Map<String, Object> contractValidateParams, StringBuffer errorText, Long minRateDefault, Long maxRateDefault, String login, String password) {
        String errorMissingParam = MISSING_REQUIRED_PARAMETER + "Ставка кредита. ";
        // Входные параметры minRateDefault и maxRateDefault нас не интересуют

        final Long percentRate = getLongParam(contractValidateParams.get("percentRate"));

        if (percentRate == null) {
            errorText.append(errorMissingParam);
            return;
        }

        final long percentRateMin = 5;
        final double percentRateMax = 20;

        // Если то что ввели вне диапозона
        if ((percentRate < percentRateMin) || (percentRateMax < percentRate)) {
            errorText.append(String.format(INTEREST_RATE_DIAPOSON_WRON_ERROR_TEXT, percentRateMin, percentRateMax));
        }
    }


    @Override
    public void validateLoanTermsFromContractValidateParams(Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        String errorMissingParam = MISSING_REQUIRED_PARAMETER + "Срок страхования. ";

        final Long termInsuranceYear = getLongParam(contractValidateParams.get("termInsuranceYear"));
        final Long termInsuranceMonth = getLongParam(contractValidateParams.get("termInsuranceMonth"));

        if (termInsuranceYear == null || termInsuranceMonth == null) {
            errorText.append(errorMissingParam);
            return;
        }

        final double monthInYear = 12.0;

        // Проверим, мб параметры просто подменили (округление в большую сторону)
        if ((Math.ceil(termInsuranceMonth / monthInYear)) != termInsuranceYear) {
            errorText.append(errorMissingParam);
            return;
        }

        // максимальный срок страхования
        final long admissibleMonths = 360;
        final long admissibleYears = 30;

        // Иначе проверим на вхождение числа в диапозон, не вошли - ошибка
        if (((termInsuranceMonth > admissibleMonths) || (termInsuranceMonth < 0))
                && ((termInsuranceYear > admissibleYears) || (termInsuranceYear < 0))) {
            errorText.append(LOAN_PERIOD_EXCEEDED_ERROR_TEXT);
        }
    }


    //    public static void main(String[] args) {
//
//        DecimalFormat formatter = new DecimalFormat("#,###,###");
//        System.out.println(formatter.format(32.4443));
//
//    }

}
