package com.bivgroup.services.b2bposws.facade.pos.custom.sberlife;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.currency.AmountUtils;
import ru.diasoft.utils.currency.CastAmount;

import java.io.Serializable;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * @author pzabaluev
 */
@BOName("CaringParentsCustom")
public class CaringParentsCustomFacade extends B2BLifeBaseFacade {

    public static final boolean DEBUG_MODE = true;

    static class ValidationResult {
        public static final int OK = 0;
        public static final int UW = 1;
        public static final int DECLINE = 2;
        public static final int ILLEGAL = 3;

        final int code;
        final String message;
        String sysname;

        public ValidationResult(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

        boolean isOk() {
            return code == 0;
        }
    }

    /**
     * Индекс массы тела
     *
     * @param heightCm рост
     * @param weightKg вес
     * @return индекс
     */
    static Double imt(Double heightCm, Double weightKg) {
        Double heightM = 0.01 * heightCm.doubleValue(); // рост в метрах
        Double bodyIndex = weightKg.doubleValue() / (heightM * heightM);
        return bodyIndex;
    }

    /**
     * Справочниклимитов по рискам
     *
     * @param login    логин
     * @param password пароль
     * @return справочник
     * @throws Exception
     */
    Map<String, Object> loadRiskLimitHandbook(String login, String password) throws Exception {
        return loadHandbook("B2B.CaringParents.RiskLimit", login, password);
    }

    /**
     * Загрузка справочника
     *
     * @param sysname  имя справочника
     * @param login    логин
     * @param password пароль
     * @return справочник
     * @throws Exception
     */
    Map<String, Object> loadHandbook(String sysname, String login, String password) throws Exception {
        Map findBy = new HashMap() {
            {
                put("HANDBOOKNAME", sysname);
                put("ReturnListOnly", true);
            }
        };
        /*
        Map hb = this.callExternalService("b2bposws", "dsB2BHandbookDataBrowseByHBName", findBy, login.get(), password.get());
        */
        Map<String, Object> hb = callService(B2BPOSWS_SERVICE_NAME, "dsB2BHandbookDataBrowseByHBName", findBy, login, password);
        return hb;
    }

    /**
     * @param riskLimitHb
     * @param sysname
     * @param currencyId
     * @return
     */
    static Double getMinBy(Map<String, Object> riskLimitHb, String sysname, Long currencyId) {
        return getMinBy(riskLimitHb, sysname, currencyId, null);
    }

    /**
     * Минимальное значение риска
     *
     * @param riskLimitHb
     * @param sysname
     * @param currencyId
     * @param payVarId
     * @return
     */
    static Double getMinBy(Map<String, Object> riskLimitHb, String sysname, Long currencyId, Long payVarId) {
        return 100_000.00;
    }

    /**
     * Максимальное значениие риска
     *
     * @param riskLimitHb
     * @param sysname
     * @param currencyId
     * @param payVarId
     * @return
     */
    static Double getMaxBy(Map<String, Object> riskLimitHb, String sysname, Long currencyId, Long payVarId) {
        return 8_000_000.00;
    }

    /**
     * Формат валюты
     *
     * @return
     */
    static NumberFormat moneyFormatter() {

        return NumberFormat.getNumberInstance(new Locale("ru"));
    }

    /**
     * Лимиты риска
     *
     * @param contrExtMap  Map
     * @param insAmSysname String
     * @param currencyId   Long
     * @param payVarId     Long
     * @param login        String
     * @param password     String
     * @return ValidationResult
     * @throws Exception
     */
    ValidationResult validateLimit(Map contrExtMap, String insAmSysname, Long currencyId, Long payVarId, String login, String password) throws Exception {
        Map<String, Object> riskLimitHb = loadRiskLimitHandbook(login, password);
        Double insAmount = getDoubleParam(contrExtMap, insAmSysname);
        if (insAmount.equals(0.0)) {
            return new ValidationResult(0, "ok");
        }
        Double min = getMinBy(riskLimitHb, insAmSysname, currencyId, payVarId);
        if (insAmount < min) {
            return new ValidationResult(2, "нарушены лимиты СС по " + insAmSysname);
        }
        Double max = getMaxBy(riskLimitHb, insAmSysname, currencyId, payVarId);
        if (insAmount > max) {
            return new ValidationResult(1, "нарушены лимиты СС по " + insAmSysname);
        }
        return new ValidationResult(0, "ok");
    }

    ValidationResult validateInsuranceAmounts(Map contrExtMap, Long currencyId, Long payVarId, String login, String password) throws Exception {
        Map<String, Object> riskLimitHb = loadRiskLimitHandbook(login, password);

        Double insAmSurvivor2 = getDoubleParam(contrExtMap, "insAmSurvivor2");
        if (insAmSurvivor2 <= 0) {
            return new ValidationResult(2, "100 тыщ минима");
        }

        validateLimit(contrExtMap, "insAmSurvivor2", currencyId, payVarId, login, password);
        validateLimit(contrExtMap, "insAmMorbidDisease", currencyId, null, login, password);
        validateLimit(contrExtMap, "insAmDisabled", currencyId, null, login, password);
        validateLimit(contrExtMap, "insAmSurgeryAccident", currencyId, null, login, password);
        //validateLimit(contrExtMap, "insAmSurvivor1IntermediatePayout", currencyId, payVarId);

        Double insAmMorbidDisease = getDoubleParam("insAmMorbidDisease");
        if (insAmMorbidDisease > insAmSurvivor2) {
            return new ValidationResult(1, "нарушены лимиты СС по особо опасным заболеваниям");
        }
        Double insAmDisabled = getDoubleParam(contrExtMap, "insAmDisabled");
        if (insAmDisabled > insAmSurvivor2) {
            return new ValidationResult(1, "нарушены лимиты СС по disability");
        }
        Double insAmSurgeryAccident = getDoubleParam(contrExtMap, "insAmSurgeryAccident");
        if (insAmSurgeryAccident > insAmSurvivor2) {
            return new ValidationResult(1, "err, surgery accident ins am is too large");
        }
        Double insAmSurvivor1IntermediatePayout = getDoubleParam(contrExtMap, "insAmSurvivor1IntermediatePayout");
        if (insAmSurvivor1IntermediatePayout != 0) {

            Double min = getMinBy(riskLimitHb, "insAmSurvivor1IntermediatePayout", currencyId, payVarId);

            if (insAmSurvivor1IntermediatePayout < insAmSurvivor2 * min) {
                return new ValidationResult(2, "insAmSurvivor1IntermediatePayout < insAmSurvivor2 * min");
            }

            Double max = getMaxBy(riskLimitHb, "insAmSurvivor1IntermediatePayout", currencyId, payVarId);
            if (insAmSurvivor1IntermediatePayout > insAmSurvivor2 * max) {
                return new ValidationResult(1, "insAmSurvivor1IntermediatePayout > insAmSurvivor2 * max");
            }
        }

        Double totalMax = getMaxBy(riskLimitHb, "insAmSurvivor2", currencyId, payVarId);
        if (insAmSurvivor2 + insAmSurvivor1IntermediatePayout > totalMax) {
            return new ValidationResult(1, "insAmSurvivor1IntermediatePayout > insAmSurvivor2 * max");
        }

        return null;
    }

    /**
     * Получение данных застрахованного
     *
     * @param insuredInsObj Map
     * @param insuredNote   String
     * @param errorText     StringBuffer
     */
    private void checkInsObjAttributesExist(Map<String, Object> insuredInsObj, String insuredNote, StringBuffer errorText) {
        if (insuredInsObj == null) {
            errorText.append("Не найдены показатели для застрахованного ").append(insuredNote).append(". ");
        } else {
            String valueNote = "Для застрахованного " + insuredNote + " не указано значение показателя";
            checkBeanValueExist(insuredInsObj, valueNote, "birthDate", "Дата рождения", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "sex", "Пол", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "growth", "Рост", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "weight", "Вес", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "bloodPressureTop", "Нижнее артериальное давление", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "bloodPressureUpper", "Верхнее артериальное давление", errorText);
            checkBeanValueExist(insuredInsObj, valueNote, "insuredDeclCompliance", "Клиент соответствует декларации", errorText);
        }
    }

    /**
     * Валидация данных договора
     *
     * @param contrMap Map
     * @param login    String
     * @param password String
     * @return ValidationResult
     * @throws Exception
     */
    ValidationResult validateContractSaveParams(Map contrMap, String login, String password) throws Exception {
        List<ValidationResult> validationResults = new ArrayList<>();

        if (contrMap.get("CONTRID") == null) {
            // если нет ИД договора - создается заготовка договора, в которой заведомо отсутствуют обязательные данные
            // (вызов сохранения при выборе продукта на странице создания нового договора, еще до перехода на первую страницу договора)
            // такую заготовку следует считать корректной, иначе невозможно будет её сохранить без ввода доп. флагов
            logger.debug("creating new contract empty template - contract data validation skipped.");
            return new ValidationResult(0, "ok");
        }
        logger.debug("Validate contract save params...");

        StringBuffer errorText = new StringBuffer();
        // todo validate currency by handbook
        boolean isCurrencyIdExists = checkContractValueExist(contrMap, "INSAMCURRENCYID", "ИД валюты страхования", errorText);
        boolean isStartDateExists = checkContractValueExist(contrMap, "STARTDATE", "Дата начала действия договора", errorText);
        // todo validate termid by hb
        boolean isTermIdExists = checkContractValueExist(contrMap, "TERMID", "ИД срока страхования", errorText);
        // todo validate payvarid by hb
        boolean isPayVarIdExists = checkContractValueExist(contrMap, "PAYVARID", "ИД периодичности оплаты", errorText);
        // todo validate prodprogid exists
        boolean isProdProgIdExists = checkContractValueExist(contrMap, "PRODPROGID", "ИД страховой программы", errorText);

        Long currencyId = getLongParam(contrMap, "INSAMCURRENCYID");
        Long payVarId = getLongParam(contrMap, "PAYVARID");

        Map contrExtMap = (Map) contrMap.get("CONTREXTMAP");
        if (null == contrExtMap) {
            return new ValidationResult(2, "Не заполнен атрибут CONTREXTMAP");
        }
        validateInsuranceAmounts(contrExtMap, currencyId, payVarId, login, password);

        // todo validate adult age, child age
        Map<String, Object> insuredAdultInsObj = getInsObjBySysNameFromContract(contrMap, "insuredAdult");
        Map<String, Object> insuredChildInsObj = getInsObjBySysNameFromContract(contrMap, "insuredChild");

        checkInsObjAttributesExist(insuredAdultInsObj, "взрослого", errorText);
        checkInsObjAttributesExist(insuredChildInsObj, "ребенка", errorText);

        // todo check insobj are residents of RU, return UW otherwise
        // todo check
        Long uw = this.underwritingCheck(contrMap, login, password);
        logger.debug("CaringParentsCustomFacade.validateContractSaveParams UW = " + uw);

        return null;
    }


    /**
     * Обновление и сохранение "Даты рассчета" при калькуляции
     *
     * @param contrExtMap Map<String, Object>
     * @param login       String
     * @param password    String
     */
    private String updateCalculationDate(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        // текущая дата калькуляции
        Date nowDate = new Date();
        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
        String calculationDATE = formatter.format(nowDate);

        contrExtMap.put("calculationDATE", parseAnyDate(calculationDATE, Double.class, "calculationDATE"));

        // обновляем
        updateContractValues(contrExtMap, login, password);

        return calculationDATE;
    }

    /**
     * Калькулятор
     *
     * @param params Map<String, Object>
     * @return Map
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> dsB2BCaringParentsCalcByContrMap(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BCaringParentsCalcByContrMap");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> contract = getMapParam(params, "CONTRMAP");
        Map<String, Object> productMap = getMapParam(params, "FULLPRODMAP");

        String error = "";
        ValidationResult validationResult = validateContractSaveParams(contract, login, password);
        if (validationResult == null || validationResult.isOk()) {
            error = calculateByContrMap(contract, productMap, login, password);
        } else {
            // запоминаем ошибку, если данные не валидны
            error = validationResult.message;
        }

        Map<String, Object> result = new HashMap<>();

        // возвращаем ошибку, если она была
        // если ошибки нет, то обновляем дату расчета calculationDATE!
        if (!error.isEmpty()) {
            result.put(ERROR, error);
        } else {
            // получаем мапу доп параметров договора
            Map<String, Object> contrExtMap = getMapParam(contract, "CONTREXTMAP");

            // Обновление и сохранение "Даты рассчета" при калькуляции
            String calculationDATE = updateCalculationDate(contrExtMap, login, password);
            // возвращаем строку
            contrExtMap.put("calculationDATE", calculationDATE);
        }

        // возвращаем данные
        result.put("CONTRMAP", contract);
        logger.debug("after dsB2BCaringParentsCalcByContrMap");
        return result;
    }

    /**
     * Установка даты договора
     *
     * @param contract мапа договора
     * @throws Exception
     */
    private void setContractStartDate(Map<String, Object> contract) throws Exception {
        if (contract.get("STARTDATE") == null) {
            Date nowDate = new Date();
            Format formatter = new SimpleDateFormat("dd.MM.yyyy");
            String startDate = formatter.format(nowDate);

            contract.put("STARTDATE", startDate);
        }
    }

    /**
     * Калькулятор: мапинг данных и вызов калькуляции
     *
     * @param contract   мапа договора
     * @param productMap мапа продукта
     * @param login      логин
     * @param password   пароль
     * @return результат калькуляции
     */
    private String calculateByContrMap(Map<String, Object> contract, Map<String, Object> productMap, String login, String password) throws Exception {
        // получаем мапу доп параметров договора
        Map<String, Object> contrExtMap = getMapParam(contract, "CONTREXTMAP");

        Map<String, Object> insuredChild = new HashMap<>();
        List<Map<String, Object>> childContrRiskList = new ArrayList<>();
        Map<String, Object> insuredAdult = new HashMap<>();
        List<Map<String, Object>> adultContrRiskList = new ArrayList<>();
        List<Map<String, Object>> insObjGroupList = getListParam(contract, "INSOBJGROUPLIST");
        Map<String, Object> contrObjMap;
        for (Map<String, Object> insObjGroupListItem : insObjGroupList) {
            List<Map<String, Object>> objList = getListParam(insObjGroupListItem, "OBJLIST");
            for (Map<String, Object> objListItem : objList) {
                Map<String, Object> insObjMap = getMapParam(objListItem, "INSOBJMAP");
                if ("insuredChild".equals(getStringParam(insObjMap, "INSOBJSYSNAME"))) {
                    insuredChild = insObjMap;
                    contrObjMap = getMapParam(objListItem, "CONTROBJMAP");
                    childContrRiskList = getListParam(contrObjMap, "CONTRRISKLIST");
                }
                if ("insuredAdult".equals(getStringParam(insObjMap, "INSOBJSYSNAME"))) {
                    insuredAdult = insObjMap;
                    contrObjMap = getMapParam(objListItem, "CONTROBJMAP");
                    adultContrRiskList = getListParam(contrObjMap, "CONTRRISKLIST");
                }
            }
        }

        // Получаем мапу продукта

        Map<String, Object> calculationMap = new HashMap<>();
        calculationMap.put(RETURN_AS_HASH_MAP, "TRUE");
        calculationMap.put("CALCVERID", productMap.get("CALCVERID"));

        // получаем страховые суммы рисков ребенка
        // получаем страховую сумму по риску "Дожитие до окончания срока страховани с
        // возвратом взносов в случае смерти ребенка (ОСНОВНАЯ программа)"
        setCalcRiskMapping(calculationMap, "insSum", childContrRiskList, "childSurvives2");
        // получаем страховую сумму по риску "Диагностирование особо опасных заболеваний"
        setCalcRiskMapping(calculationMap, "CISum", childContrRiskList, "childDiagnosedMorbidDisease");
        // получаем страховую сумму по риску "Хирургическое вмешательство в результате НС"
        setCalcRiskMapping(calculationMap, "surgerySum", childContrRiskList, "childSurgeryAccident");
        // получаем страховую сумму по риску "Инвалидность по любой причине, Травмы"
        setCalcRiskMapping(calculationMap, "childDisabSum", childContrRiskList, "childDisabledLpOrInjuredNs");
        // получаем страховую сумму по риску "Дожитие с промежуточной выплатой"
        boolean childSurvived1Find = setCalcRiskMapping(calculationMap, "endowmentSum",
                childContrRiskList, "childSurvives1");
        // если страховая сумма "Дожите с промежуточной выплатой" найдена (т.е. "включена"), тогда пытаемся получить:
        // Срок страхования по риску Дожитие 1 с промежуточной выплатой (период до даты промежуточной выплаты)
        if (childSurvived1Find) {
            Map<String, Object> hbParams = new HashMap<String, Object>();
            // получаем данный параметр из дополнительных атрибутов и отфильтровываем по нему справочник
            hbParams.put("HID", getLongParam(contrExtMap, "termSurvivor1IntermediatePayout"));
            List<Map<String, Object>> filteredList = loadHandbookData(hbParams,
                    "B2B.CaringParents.Survivor1Term", login, password);
            Map<String, Object> termInfo = new HashMap<>();
            // если все корректно и мы нашли нужно значение, тогда берем TERMID из него
            // и пытаемся получить YEARCOUNT из таблицы B2B_TERM
            if (filteredList != null && filteredList.size() > 0) {
                Long termId = getLongParam(filteredList.get(0), "TERMID");
                termInfo = getTermDataByTermId(termId, login, password);
            }
            calculationMap.put("endowmentTerm",
                    getLongParamLogged(termInfo, "YEARCOUNT"));
        }

        // получаем страховые суммы рисков взрослого
        // получаем страховую сумму по риску "Смерть в результате несчастного случая"
        setCalcRiskMapping(calculationMap, "PASum", adultContrRiskList, "adultDeathAccident");

        // расчитываем возраст застрахованного ребенка по его дате рождения и помещаем в расчетные параметры
        calcAgeInsuredAndSetInCalcParam(insuredChild, calculationMap, "ageChild");

        // расчитываем возраст застрахованного взрослого по дате рождения  и помещаем в расчетные параметры
        calcAgeInsuredAndSetInCalcParam(insuredAdult, calculationMap, "ageAdult");

        // получаем пол застрахованного ребенка
        calculationMap.put("genderChild", getLongParam(insuredChild, "sex"));
        // получаем пол застрахованного взрослого
        calculationMap.put("genderAdult", getLongParam(insuredAdult, "sex"));

        // получаем системное имя Валюта страхования
        if (contract.get("INSAMCURRENCYID") != null) {
            calculationMap.put("currency", getCurrencySysNameById(
                    Long.valueOf(contract.get("INSAMCURRENCYID").toString()), login, password));
        }

        // получаем Вариант выплаты в случае Дожития до окончания срока из договора
        // и по payvarid фильтруем таблицу B2B_PAYVAR
        Map<String, Object> payVarQuery = new HashMap<>();
        payVarQuery.put("PAYVARID", getLongParam(contrExtMap, "rentPayoutVar"));
        payVarQuery.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> payVarResult = this.callServiceLogged(B2BPOSWS_SERVICE_NAME,
                "dsB2BPaymentVariantBrowseListByParam", payVarQuery, login, password);
        // и получаем системное имя периодичности
        String payVarSysName = getStringParam(payVarResult, "SYSNAME");
        // Вариант выплаты в случае Дожития до окончания срока
        calculationMap.put("freqPaym", payVarSysName);

        Map<String, Object> fullProductMap = (Map<String, Object>) contract.get("FULLPRODMAP");

        // Периодичность уплаты взносов из полной мапы договора
        if (fullProductMap != null) calculationMap.put("freqPrem", getPaymentVariantSysNameById(fullProductMap,
                Long.valueOf(contract.get("PAYVARID").toString())));
        else   calculationMap.put("freqPrem", getPaymentVariantSysNameById(productMap,
                Long.valueOf(contract.get("PAYVARID").toString())));

        // Срок страхования (лет) из полной мапы договора
        Long insTerm = 0L;
        if (fullProductMap != null) {
            insTerm = getTermInYearsById(fullProductMap, getLongParam(contract.get("TERMID")));
        } else {
            insTerm = getTermInYearsById(productMap, getLongParam(contract.get("TERMID")));
        }
        calculationMap.put("insTerm", insTerm);

        // "Застрахованный взрослый - сотрудник группы Сбербанк?" так как нет на интерфейсе считам что нет, т.е. 0
        calculationMap.put("isDiscount", 0);

        // Срок выплаты ренты
        Long rentPayoutTerm = getLongParam(contrExtMap, "rentPayoutTerm");
        Long paymTerm = 0L;
        if (rentPayoutTerm != null) {
            Map<String, Object> hbParams = new HashMap<String, Object>();
            // получаем данный параметр из дополнительных атрибутов и отфильтровываем по нему справочник
            hbParams.put("HID", rentPayoutTerm);
            List<Map<String, Object>> filteredList = loadHandbookData(hbParams,
                    "B2B.CaringParents.RentPayoutTerm", login, password);
            Map<String, Object> termInfo = new HashMap<>();
            // если все корректно и мы нашли нужно значение, тогда берем TERMID из него
            // и пытаемся получить YEARCOUNT из таблицы B2B_TERM
            if (filteredList != null && filteredList.size() > 0) {
                Long termId = getLongParam(filteredList.get(0), "TERMID");
                termInfo = getTermDataByTermId(termId, login, password);
            }
            calculationMap.put("paymTerm",
                    getLongParamLogged(termInfo, "YEARCOUNT"));
            paymTerm = getLongParamLogged(termInfo, "YEARCOUNT");
        }

        // TODO: канал продаж, пока константа
        calculationMap.put("sales", "Партнерский канал");

        List<Map<String, Object>> list = new ArrayList<>();
        // формируем вспомогательный список для расчетов от 0 до 34 согласно калькулятору
        for (long i = 0; i <= 34; i++) {
            Map<String, Object> item = new HashMap<>();
            // номер записи в списке
            item.put("rowNum", i);
            // год действия договора
            item.put("K", i);
            list.add(item);
        }
        // вспомогательный список для расчетов
        calculationMap.put("list", list);

        // формируем вспомогательный список для рассчета таблицы вероятностей (смерть + инвалидность (на 1000))
        List<Map<String, Object>> deathList = new ArrayList<>();
        for (long i = 0; i <= 100; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("rowNum", i);
            item.put("age", i);
            deathList.add(item);
        }
        calculationMap.put("deathList", deathList);

        List<Map<String, Object>> insList = new ArrayList<>();
        for (long i = 0; i < (insTerm) ; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("rowNum", i);
            insList.add(item);
        }
        calculationMap.put("insList", insList);

        List<Map<String, Object>> redemptionList = new ArrayList<>();
        for (long i = 0; i < (insTerm+paymTerm); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("rowNum", i);
            redemptionList.add(item);
        }
        calculationMap.put("redemptionList", redemptionList);

        List<Map<String, Object>> tempList = new ArrayList<>();
        for (long i = 0; i <= (insTerm*12); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("rowNum", i);
            tempList.add(item);
        }
        calculationMap.put("tempList", tempList);

        //добавление процентов/промилей андеррайтера
        {

            for (Map<String,Object> childContrRiskListItem : childContrRiskList)
            {
                Map <String,Object> contrRiskExtMap = getMapParam(childContrRiskListItem,"CONTRRISKEXTMAP");
//                if ("childSurvives2".equals(getStringParam(childContrRiskListItem,"PRODRISKSYSNAME"))) {
//                    if (contrRiskExtMap.get("ratePercent") != null)
//                        calculationMap.put("proc1", (getDoubleParam(contrRiskExtMap,"ratePercent") / 100));
//                    else
//                        calculationMap.put("proc1", 0);
//                    if (contrRiskExtMap.get("ratePerMill") != null)
//                        calculationMap.put("prom1", (getDoubleParam(contrRiskExtMap,"ratePerMill")));
//                    else
//                        calculationMap.put("prom1", 0);
//
//                }
//                if ("childSurvives1".equals(getStringParam(childContrRiskListItem,"PRODRISKSYSNAME"))) {
//                    if (contrRiskExtMap.get("ratePercent") != null)
//                        calculationMap.put("proc8", (getDoubleParam(contrRiskExtMap,"ratePercent") / 100));
//                    else
//                        calculationMap.put("proc8", 0);
//                    if (contrRiskExtMap.get("ratePerMill") != null)
//                        calculationMap.put("prom8", (getDoubleParam(contrRiskExtMap,"ratePerMill")));
//                    else
//                        calculationMap.put("prom8", 0);
//
//                }
                if ("childDiagnosedMorbidDisease".equals(getStringParam(childContrRiskListItem,"PRODRISKSYSNAME"))) {
                    if (contrRiskExtMap.get("ratePercent") != null)
                        calculationMap.put("proc2", (getDoubleParam(contrRiskExtMap,"ratePercent") / 100));
                    else
                        calculationMap.put("proc2", 0);
                    if (contrRiskExtMap.get("ratePerMill") != null)
                        calculationMap.put("prom2", (getDoubleParam(contrRiskExtMap,"ratePerMill")));
                    else
                        calculationMap.put("prom2", 0);

                }
                if ("childSurgeryAccident".equals(getStringParam(childContrRiskListItem,"PRODRISKSYSNAME"))) {
                    if (contrRiskExtMap.get("ratePercent") != null)
                        calculationMap.put("proc4", (getDoubleParam(contrRiskExtMap,"ratePercent") / 100));
                    else
                        calculationMap.put("proc4", 0);
                    if (contrRiskExtMap.get("ratePerMill") != null)
                        calculationMap.put("prom4", (getDoubleParam(contrRiskExtMap,"ratePerMill")));
                    else
                        calculationMap.put("prom4", 0);

                }
                if ("childDisabledLpOrInjuredNs".equals(getStringParam(childContrRiskListItem,"PRODRISKSYSNAME"))) {
                    if (contrRiskExtMap.get("ratePercent") != null)
                        calculationMap.put("proc3", (getDoubleParam(contrRiskExtMap,"ratePercent") / 100));
                    else
                        calculationMap.put("proc3", 0);
                    if (contrRiskExtMap.get("ratePerMill") != null)
                        calculationMap.put("prom3", (getDoubleParam(contrRiskExtMap,"ratePerMill")));
                    else
                        calculationMap.put("prom3", 0);

                }
            }

            for (Map<String,Object> adultContrRiskListItem : adultContrRiskList) {
                Map <String,Object> contrRiskExtMap = getMapParam(adultContrRiskListItem,"CONTRRISKEXTMAP");
                if ("adultDeathAccident".equals(getStringParam(adultContrRiskListItem,"PRODRISKSYSNAME"))) {
                    if (contrRiskExtMap.get("ratePercent") != null)
                        calculationMap.put("proc6", (getDoubleParam(contrRiskExtMap,"ratePercent") / 100));
                    else
                        calculationMap.put("proc6", 0);
                    if (contrRiskExtMap.get("ratePerMill") != null)
                        calculationMap.put("prom6", (getDoubleParam(contrRiskExtMap,"ratePerMill")));
                    else
                        calculationMap.put("prom6", 0);

                }
                if ("exemptionPremiumsDeathOrDisability".equals(getStringParam(adultContrRiskListItem,"PRODRISKSYSNAME"))) {
                    if (contrRiskExtMap.get("ratePercent") != null)
                        calculationMap.put("proc7", (getDoubleParam(contrRiskExtMap,"ratePercent") / 100));
                    else
                        calculationMap.put("proc7", 0);
                    if (contrRiskExtMap.get("ratePerMill") != null)
                        calculationMap.put("prom7", (getDoubleParam(contrRiskExtMap,"ratePerMill")));
                    else
                        calculationMap.put("prom7", 0);

                }
                if ("exemptionPremiumsDeath".equals(getStringParam(adultContrRiskListItem,"PRODRISKSYSNAME"))) {
                    if (contrRiskExtMap.get("ratePercent") != null)
                        calculationMap.put("proc1", (getDoubleParam(contrRiskExtMap,"ratePercent") / 100));
                    else
                        calculationMap.put("proc1", 0);
                    if (contrRiskExtMap.get("ratePerMill") != null)
                        calculationMap.put("prom1", (getDoubleParam(contrRiskExtMap,"ratePerMill")));
                    else
                        calculationMap.put("prom1", 0);

                }
            }

            //промили для спорта пока не предусмотрены по ФТ, следовательн по-дефолту 0
            if (getDoubleParam(contrExtMap,"childSportMaxRate") != null)
                calculationMap.put("proc5", getDoubleParam(contrExtMap,"childSportMaxRate")/100);
            else
                calculationMap.put("proc5", 0);
            calculationMap.put("prom5", 0);
        }

//         вызываем расчет
        Map<String, Object> calculationResult = this.callService(Constants.INSTARIFICATORWS, "calculateByCalculatorVersionID",
                calculationMap, login, password);

//         обрабатываем результаты расчета
//         если расчет не выполнился то передаем ошибку: "Ошибка выполнения расчета."
        String error = processingCalculationResults(contract, calculationResult, contrExtMap,
                childContrRiskList, adultContrRiskList);


        // FIXME: для откладки <!--
//        contract.put("PREMVALUE", calculationResult.get("premValue"));
//        contract.put("PRODPROGID", getLongParam(productMap, "PRODCONFID"));
//        contrExtMap.put("personalIncomeTaxDeductionValue", calculationResult.get("personalIncomeTaxDeductionValue"));
        // Дожитие до окончания срока страховани с возвратом взносов в случае смерти ребенка (ОСНОВНАЯ программа)
//        Map<String, Object> calculationResultTmp = new HashMap<>();
//        calculationResultTmp.put("insuranceFeeMaster", 94379);
//        calculationResultTmp.put("survivorRentMaster", 200000);
//        calculationResultTmp.put("insuranceBenefitMaster", 393298);
//        calculationResultTmp.put("insuranceFeeChild0", 8268);
//        calculationResultTmp.put("insuranceFeeChild2", 13297);
//        calculationResultTmp.put("insuranceFeeChild1", 2480);
//        calculationResultTmp.put("insuranceFeeAdditional", 115985);
//        calculationResultTmp.put("insuranceBenefitAdditional", 541435);
//        calculationResultTmp.put("insuranceFeeAdult", 1240);
//        calculationResultTmp.put("insuranceExemptionFeeAdult", 12298);
//        calculationResultTmp.put("insuranceExemptionFeeAdult", 12298);

//        setResCalcRiskMapping(childContrRiskList, "childSurvives2",
//                calculationResult, "insuranceFeeMaster");
//        // Ожидаемая страховая выплата по риску "Дожитие до окончания срока страховани с возвратом взносов в случае смерти ребенка (ОСНОВНАЯ программа)"
//        setRiskExtMapping(childContrRiskList, "childSurvives2", "insuranceBenefit",
//                calculationResult, "insuranceBenefitMaster");
//        // Рента дожития по риску "Дожитие до окончания срока страховани с возвратом взносов в случае смерти ребенка (ОСНОВНАЯ программа)"
//        setRiskExtMapping(childContrRiskList, "childSurvives2", "survivorRent",
//                calculationResult, "survivorRentMaster");
//
//        // Диагностирование особо опасных заболеваний
//        setResCalcRiskMapping(childContrRiskList, "childDiagnosedMorbidDisease", calculationResult,
//                "insuranceFeeChild0");
//        // Хирургическое вмешательство
//        setResCalcRiskMapping(childContrRiskList, "childSurgeryAccident", calculationResult,
//                "insuranceFeeChild2");
//        // Инвалидность по любой причине, Травмы
//        setResCalcRiskMapping(childContrRiskList, "childDisabledLpOrInjuredNs", calculationResult,
//                "insuranceFeeChild1");
//
//
//        // Дожитие с промежуточной выплатой
//        setResCalcRiskMapping(childContrRiskList, "childSurvives1", calculationResult,
//                "insuranceFeeAdditional");
//        // Ожидаемая страховая выплата по риску "Дожитие с промежуточной выплатой"
//        setRiskExtMapping(childContrRiskList, "childSurvives1", "insuranceBenefit",
//                calculationResult, "insuranceBenefitAdditional");
//
//        // мапим взрослые риски
//        // Смерть в результате несчастного случая
//        setResCalcRiskMapping(adultContrRiskList, "adultDeathAccident", calculationResult,
//                "insuranceFeeAdult");
//        // ОУСВ смерть + инвалидность
//        setResCalcRiskMapping(adultContrRiskList, "exemptionPremiumsDeathOrDisability", calculationResult,
//                "insuranceExemptionFeeAdult");
//        // ОУСВ смерть
//        setResCalcRiskMapping(adultContrRiskList, "exemptionPremiumsDeath", calculationResult,
//                "insuranceExemptionFeeAdult");

        // дата договора
        setContractStartDate(contract);

        return error;
        // FIXME: для откладки -->
    }

    /**
     * Метод обработки результатов расчета калькулятора
     *
     * @param contract           договор, в который мапим результаты
     * @param calculationResult  результаты расчета калькулятора
     * @param contrExtMap        дополнительные данные по договору
     * @param childContrRiskList список рисков ребенка
     * @param adultContrRiskList список рисков взрослового
     * @return ошибка
     */
    private String processingCalculationResults(Map<String, Object> contract, Map<String, Object> calculationResult,
                                                Map<String, Object> contrExtMap,
                                                List<Map<String, Object>> childContrRiskList,
                                                List<Map<String, Object>> adultContrRiskList) {
        String error = "";
        Double premValue = getDoubleParam(calculationResult, "premValue");
        if (premValue != null && premValue > 0.0001) {
            // TODO: Доделать мапинг выходных значений калькуялтора, когда будут известны их наименвоания
            contract.put("PREMVALUE", premValue);
            // мапим детские риски
            // Дожитие до окончания срока страховани с возвратом взносов в случае смерти ребенка (ОСНОВНАЯ программа)
            setResCalcRiskMapping(childContrRiskList, "childSurvives2", calculationResult,
                    "insuranceFeeMaster");
            // Ожидаемая страховая выплата по риску "Дожитие до окончания срока страховани с возвратом взносов в случае смерти ребенка (ОСНОВНАЯ программа)"
            setRiskExtMapping(childContrRiskList, "childSurvives2", "insuranceBenefit",
                    calculationResult, "insuranceBenefitMaster");
            // Рента дожития по риску "Дожитие до окончания срока страховани с возвратом взносов в случае смерти ребенка (ОСНОВНАЯ программа)"
            setRiskExtMapping(childContrRiskList, "childSurvives2", "survivorRent",
                    calculationResult, "survivorRentMaster");
            // Диагностирование особо опасных заболеваний
            setResCalcRiskMapping(childContrRiskList, "childDiagnosedMorbidDisease", calculationResult,
                    "insuranceFeeChild0");
            // Хирургическое вмешательство
            setResCalcRiskMapping(childContrRiskList, "childSurgeryAccident", calculationResult,
                    "insuranceFeeChild2");
            // Инвалидность по любой причине, Травмы
            setResCalcRiskMapping(childContrRiskList, "childDisabledLpOrInjuredNs", calculationResult,
                    "insuranceFeeChild1");
            // Дожитие с промежуточной выплатой
            setResCalcRiskMapping(childContrRiskList, "childSurvives1", calculationResult,
                    "insuranceFeeAdditional");
            // Ожидаемая страховая выплата по риску "Дожитие с промежуточной выплатой"
            setRiskExtMapping(childContrRiskList, "childSurvives1", "insuranceBenefit",
                    calculationResult, "insuranceBenefitAdditional");

            // мапим взрослые риски
            // Смерть в результате несчастного случая
            setResCalcRiskMapping(adultContrRiskList, "adultDeathAccident", calculationResult,
                    "insuranceFeeAdult");
            // ОУСВ смерть + инвалидность
            setResCalcRiskMapping(adultContrRiskList, "exemptionPremiumsDeathOrDisability", calculationResult,
                    "insuranceExemptionFeeAdult");
            // ОУСВ смерть
            setResCalcRiskMapping(adultContrRiskList, "exemptionPremiumsDeath", calculationResult,
                    "insuranceExemptionFeeAdult");

            contrExtMap.put("personalIncomeTaxDeductionValue", getDoubleParam(calculationResult, "personalIncomeTaxDeductionValue"));

            // обработка взносов и тарифов
            List<Map<String, Object>> calcList = getListParam(calculationResult, "insList");
            if (calcList != null) {
                for (Map<String, Object> bean : calcList) {
                    Long year = getLongParam(bean,"rowNum");
                    contrExtMap.put("insSumYear" + year.toString(), bean.get("insValue"));
                    contrExtMap.put("insTariffYear" + year.toString(), bean.get("insTariff"));
                }
            }

            // обработка выкупных сумм
            calcList = getListParam(calculationResult, "redemptionList");
            if (calcList != null) {
                for (Map<String, Object> bean : calcList) {
                    Long year = getLongParam(bean,"rowNum");
                    contrExtMap.put("redemptionSumYear" + year.toString(), bean.get("redemptionSum"));
                    contrExtMap.put("redemptionPremDSumYear" + year.toString(), bean.get("premDSum"));
                }
            }
        } else {
            error = "Ошибка выполнения расчета.";
        }

        return error;
    }

    /**
     * Метод для расчета возраста застрахованного и добавления его
     * под наименование (paramName) в мапу параметров расчета
     *
     * @param insured        застрахованный
     * @param calculationMap мапа параметров расчета
     * @param paramName      наименование параметры под которым нужно положить возраст в мапу параметров расчета
     */
    private void calcAgeInsuredAndSetInCalcParam(Map<String, Object> insured, Map<String, Object> calculationMap, String paramName) {
        Date insuredBirthDate = getDateParam(insured.get("birthDATE"));
        if (insuredBirthDate != null) {
            Long insuredAge = calcAge(insuredBirthDate);
            calculationMap.put(paramName, insuredAge);
        }
    }

    /**
     * Метод добавление страховых сумм рисков в мапу параметров расчета
     * Находим риск в списке (по системному наименованию риска (riskProdStructSysName))
     * берем из найденного риска INSAMVALUE и кладем в ману параметров расчета под именем sumParamName
     *
     * @param calcParams            мапа параметров расчета калькулятора
     * @param sumParamName          имя параметра под которым надо положить в мапу паратров расчета калькулятора
     * @param contrRiskList         список рисков
     * @param riskProdStructSysName системное наименование риска
     * @return
     */
    private boolean setCalcRiskMapping(Map<String, Object> calcParams, String sumParamName,
                                       List<Map<String, Object>> contrRiskList, String riskProdStructSysName) {
        boolean fFind = false;
        for (Map<String, Object> bean : contrRiskList) {
            if (getStringParam(bean, "PRODRISKSYSNAME").equalsIgnoreCase(riskProdStructSysName)) {
                if ((bean.get("INSAMVALUE") != null) && (getDoubleParam(bean, "INSAMVALUE") > 0.0001)
                        && (getLongParam(bean,"ROWSTATUS") != 3)) {
//                        && (bean.get("isSelected") != null) && (getLongParam(bean, "isSelected") == 1)) {
                    calcParams.put(sumParamName, bean.get("INSAMVALUE"));
                } else {
                    calcParams.put(sumParamName, 0.0);
                }
                fFind = true;
                break;
            }
        }
        if (!fFind) {
            calcParams.put(sumParamName, 0.0);
        }
        return fFind;
    }

    /**
     * Метод добавление PREMVALUE в риск
     * В списке рисков находим требуемый риск по riskProdStructSysName
     * И из результатов расчета по premValueParamName берем значение
     * и кладем его в PREMVALUE риска
     *
     * @param contrRiskList         список рисков
     * @param riskProdStructSysName системное имя риска
     * @param calcResMap            результаты расчета калькулятора
     * @param premValueParamName    наименование параметра в мапе результатов расчета калькулятора
     */
    private void setResCalcRiskMapping(List<Map<String, Object>> contrRiskList, String riskProdStructSysName,
                                       Map<String, Object> calcResMap, String premValueParamName) {
        for (Map<String, Object> bean : contrRiskList) {
            if ((getStringParam(bean, "PRODRISKSYSNAME").equalsIgnoreCase(riskProdStructSysName))
                    && ((bean.get("isSelected") != null) && (getLongParam(bean, "isSelected") == 1))) {
                if (premValueParamName != null) {
                    bean.put("PREMVALUE", calcResMap.get(premValueParamName));
                }
                break;
            }
        }
    }

    /**
     * Метод добавление параметра в дополнительную мапу риска из результатов расчета
     * Добавляем по наименованию (contrExtParamName) в мапу (CONTRRISKEXTMAP)
     * значение параметра берем из результатов калькулятора по имени (calcParamName)
     * Добавляем только если данный риск выбран
     *
     * @param contrRiskList         список рисков
     * @param riskProdStructSysName системное имя риска
     * @param contrExtParamName     наименование параметра в CONTRRISKEXTMAP
     * @param calcResMap            результаты расчета калькулятора
     * @param calcParamName         наименование параметра в мапе результатов расчета калькулятора
     */
    private void setRiskExtMapping(List<Map<String, Object>> contrRiskList, String riskProdStructSysName, String contrExtParamName,
                                   Map<String, Object> calcResMap, String calcParamName) {
        Map<String, Object> contrRiskExtMap;
        for (Map<String, Object> bean : contrRiskList) {
            if ((getStringParam(bean, "PRODRISKSYSNAME").equalsIgnoreCase(riskProdStructSysName))
                    && ((bean.get("isSelected") != null) && (getLongParam(bean, "isSelected") == 1))) {
                contrRiskExtMap = getMapParam(bean, "CONTRRISKEXTMAP");
                if (contrRiskExtMap != null) {
                    contrRiskExtMap.put(contrExtParamName, calcResMap.get(calcParamName));
                }
                break;
            }
        }
    }

    /**
     * Получение сведений о сроке страхования
     *
     * @param termId   - идентификатор срока
     * @param login    - логин
     * @param password - пароль
     * @return
     * @throws Exception
     */
    private Map<String, Object> getTermDataByTermId(Long termId, String login, String password) throws Exception {
        Map<String, Object> termParams = new HashMap<>();
        termParams.put("TERMID", termId);
        termParams.put(RETURN_AS_HASH_MAP, true);
        return this.callServiceTimeLogged(B2BPOSWS_SERVICE_NAME, "dsB2BTermBrowseListByParam",
                termParams, login, password);
    }

    /**
     * Метод получения срока страхования из мапы продукта по идентификатору срока
     *
     * @param productMap полная мапа продукта
     * @param termId     идентификатор срока
     * @return
     */
    private Long getTermInYearsById(Map<String, Object> productMap, Long termId) {
        List<Map<String, Object>> prodTermList = getListParam(productMap, "PRODTERMS");
        if (prodTermList != null) {
            for (Map<String, Object> bean : prodTermList) {
                if (bean.get("TERM") != null) {
                    Map<String, Object> termMap = getMapParam(bean, "TERM");
                    Long beanTermId = getLongParam(termMap, "TERMID");
                    if (beanTermId != null && beanTermId.equals(termId)) {
                        return getLongParam(termMap, "YEARCOUNT");
                    }
                }
            }
        }
        return null;
    }

    /**
     * Метод расчета позраст по дате
     *
     * @param birthDate дата
     * @return возраст
     */
    private long calcAge(Date birthDate) {
        return calcYears(birthDate, new Date()).longValue();
    }

    /**
     * Метод расчета количества лет между датами от (from) и до (to)
     *
     * @param from дата от
     * @param to   дата до
     * @return количество лет
     */
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

    /**
     * Метод получения системного имени валюты по идентификатору валюты
     *
     * @param currencyId идентификатор валюты
     * @param login      логин
     * @param password   пароль
     * @return системное имя валюты
     * @throws Exception
     */
    private String getCurrencySysNameById(Long currencyId, String login, String password) throws Exception {
        Map<String, Object> qparam = new HashMap<String, Object>();
        qparam.put("CurrencyID", currencyId);
        qparam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qRes = this.callService(Constants.REFWS, "getCurrencyByParams",
                qparam, login, password);
        if ((qRes != null) && (qRes.get("Brief") != null)) {
            return qRes.get("Brief").toString();
        } else {
            return null;
        }
    }

    /**
     * Метод получения системного имени периодичности из мапы продукта по идентификатору
     *
     * @param productMap       полная мапа договора
     * @param paymentVariantId идентификатор периодичности
     * @return системное имени периодичности
     */
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


    /**
     * Проверка андеррайтера
     *
     * @param params Map
     * @return Map
     * @throws Exception
     */
    @WsMethod
    public Map<String, Object> dsB2BCaringParentsUnderwritingCheck(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BCaringParentsUnderwritingCheck");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        this.underwritingCheck(contract, login, password);
        Map<String, Object> result = contract;
        logger.debug("after dsB2BCaringParentsUnderwritingCheck");
        return result;
    }

    /**
     * Метод для сохранения договора по продукту
     *
     * @param params Map
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BCaringParentsPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BCaringParentsPrepareToSave");

        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        // приходят данные в разном формате
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }

        ValidationResult validationResult = validateContractSaveParams(contract, login, password);
        Map<String, Object> result;

        if (DEBUG_MODE || validationResult.isOk()) {
            result = genAdditionalSaveParams(contract, login, password);

            // запускаем калькулятор для обновления данных
            Boolean isNeedRecalculate = getBooleanParam(result, "isNeedRecalculate", false);
            if (isNeedRecalculate) {
                Map<String, Object> productMap = getMapParam(result, "PRODMAP");
                if (productMap == null) {
                    productMap = getMapParam(result, "FULLPRODMAP");
                }

                calculateByContrMap(result, productMap, login, password);

                result.put("isNeedRecalculate", false);
            }

        } else {
            result = contract;
        }

        logger.debug("after dsB2BCaringParentsPrepareToSave");
        return result;
    }

    /**
     * @param contract
     * @param login
     * @param password
     * @return
     * @throws Exception
     */
    @Override
    protected Map<String, Object> genProductAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {
        logger.debug("CaringParentsCustomFacade.genProductAdditionalSaveParams start...");
        Map<String, Object> callParams = new HashMap<String, Object>();
        callParams.put("CONTRMAP", contract);
        callParams.put("ISMISSINGSTRUCTSCREATED", false);
        callParams.put(RETURN_AS_HASH_MAP, true);
        contract = callService(B2BPOSWS_SERVICE_NAME, "dsB2BUpdateContractInsuranceProductStructure", callParams, login, password);
        logger.debug("CaringParentsCustomFacade.genProductAdditionalSaveParams finished.");
        return contract;
    }

    /**
     * Генерация строковых представлений для всех сумм
     *
     * @param data
     * @param dataNodePath
     * @param logger
     */
    static void genSumStrs(Map<String, Object> data, String dataNodePath, Logger logger) {
        Map<String, Object> parsedMap = new HashMap<String, Object>();
        parsedMap.putAll(data);
        for (Map.Entry<String, Object> entry : parsedMap.entrySet()) {
            String keyName = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String dataValuePath = dataNodePath + "." + keyName;
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    genSumStrs(map, dataValuePath, logger);
                } else if (value instanceof List) {
                    ArrayList<Map<String, Object>> list = (ArrayList<Map<String, Object>>) value;
                    for (int i = 0; i < list.size(); i++) {
                        Map<String, Object> element = list.get(i);
                        genSumStrs(element, dataValuePath + "[" + i + "]", logger);
                    }
                } else if(keyName.equals("contributionTarifVALUE")) {
                    final Double percentValue = Double.valueOf(value.toString());
                    logger.debug(dataValuePath + " = " + percentValue);
                    final String finalPercentPrintValue = value.toString();
                    data.put(keyName + "STR2", finalPercentPrintValue);
                    data.put(keyName + "STR", finalPercentPrintValue);
                    data.put(keyName + "STRBILL", finalPercentPrintValue);
                    logger.debug(dataNodePath + "." + keyName + "STRBILL = " + finalPercentPrintValue);
                }
                else if (keyName.endsWith("VALUE")) {
                    // Страховая сумма и её валюта
                    try {
                        Double insAmValue = Double.valueOf(value.toString());
                        logger.debug(dataValuePath + " = " + insAmValue);
                        String currencyKeyName = keyName.replace("VALUE", "") + "CURRENCYID";
                        String insAmCurrNumCode = getStringParam(data.get(currencyKeyName));
                        if ("1".equalsIgnoreCase(insAmCurrNumCode)) {
                            insAmCurrNumCode = "810"; // рубли
                        }
                        if ("2".equalsIgnoreCase(insAmCurrNumCode)) {
                            insAmCurrNumCode = "840"; // доллары
                        } else if ("3".equalsIgnoreCase(insAmCurrNumCode)) {
                            insAmCurrNumCode = "978"; // евро
                        } else {
                            insAmCurrNumCode = "810"; // по-умолчанию: рубли
                        }
                        // отдельно валюта не требуется, уже учитывается при вызове amountToString
                        //reportData.put("INSAMCURRENCYSTR", getCurrByCodeToNum("RUB", insAmValue.longValue()));
                        // "Тринадцать тысяч четыреста тридцать восемь рублей 24 копейки"
                        String insAmStr = AmountUtils.amountToString(insAmValue, insAmCurrNumCode);

                        // "Тринадцать тысяч четыреста тридцать восемь рублей 24 копейки" > " (Тринадцать тысяч четыреста тридцать восемь) рублей 24 копейки"
                        String insAmStrBill = " (" + insAmStr.replace(" рубл", ") рубл");

                        // отбрасываем нулевые копейки
                        String insAmStrSumInSkobki = insAmStrBill.replace(" 00 копеек", "").replace(" 00 евроцентов", "").replace(" 00 центов", "");

                        // отбрасываем нулевые копейки
                        insAmStr = insAmStr.replace(" 00 копеек", "").replace(" 00 евроцентов", "").replace(" 00 центов", "");
                        String insAmNumStr = moneyFormatter().format(insAmValue);
                        String sumValueStr = insAmNumStr + " (" + insAmStr + ")";

                        data.put(keyName + "STR2", insAmNumStr + insAmStrSumInSkobki);
                        data.put(keyName + "STR", insAmNumStr);
                        logger.debug(dataNodePath + "." + keyName + "STR = " + sumValueStr);

                        // 13438.24 > "13 438"
                        String insAmNumStrBill = moneyFormatter().format(insAmValue.intValue());
                        // "13 438 (Тринадцать тысяч четыреста тридцать восемь) рублей 24 копейки"
                        String sumValueStrBill = insAmNumStrBill + insAmStrBill;
                        data.put(keyName + "STRBILL", sumValueStrBill);
                        logger.debug(dataNodePath + "." + keyName + "STRBILL = " + sumValueStrBill);

                    } catch (NumberFormatException ex) {
                        logger.debug(dataValuePath + " - не сумма.");
                    } catch (IllegalArgumentException ex) {
                        logger.debug(dataValuePath + " не удалось преобразовать в строковое представление суммы.");
                    }
                }
            }
        }
    }

    /**
     * Датапровайдер для ПФ
     *
     * @param params Map
     * @return Map
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map dsB2BCaringParentsPrintDocDataProvider(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        Long contrId = getLongParam(params, "CONTRID");
        if (contrId == null) {
            throw new IllegalArgumentException();
        }

        // ???
        Map docDataRes = callService(B2BPOSWS_SERVICE_NAME, "dsB2BSberLifePrintDocDataProvider", params, login, password);

        Map docData = (Map) docDataRes.get(RESULT);
        Map contrExtMap = (Map) docData.get("CONTREXTMAP");

        // вызываем калькулятор
        Map<String, Object> productMap = getMapParam(docData, "PRODUCTMAP");
        if (productMap == null) {
            productMap = getMapParam(docData, "FULLPRODMAP");
        }
        calculateByContrMap(docData, productMap, login, password);

        List<Map> prodDefVals = (List<Map>) productMap.get("PRODDEFVALS");
        
        /*Map BANKDETAILMAP = new HashMap();
        BANKDETAILMAP.put("bankAccount", "bankAccount");
        BANKDETAILMAP.put("bankBIK", "bankBIK");
        BANKDETAILMAP.put("bankINN", "bankINN");
        BANKDETAILMAP.put("bankSettlementAccount", "bankSettlementAccount");
        BANKDETAILMAP.put("bankName", "bankName");
        BANKDETAILMAP.put("bankAddressPhone", "bankAddressPhone");
        BANKDETAILMAP.put("bankAddress", "Bankaddress");
        BANKDETAILMAP.put("account", "account");
        BANKDETAILMAP.put("cardNumber", "cardNumber");
        docData.put("BANKDETAILMAP", BANKDETAILMAP);*/

        // мапы деклараций - инициализация
        // DECLFULLMAP - Данные Декларации Полная ЗВ ЗР_от_18
        HashMap<String, Object> declFullMap = new HashMap<String, Object>();
        docData.put("DECLFULLMAP", declFullMap);
        // DECLADULT_NSMAP - Данные Декларации ЗВ_НС
        HashMap<String, Object> declAdultNSMap = new HashMap<String, Object>();
        docData.put("DECLADULT_NSMAP", declAdultNSMap);
        // DECLCHILDMAP - Данные Декларации ЗР
        HashMap<String, Object> declChildMap = new HashMap<String, Object>();
        docData.put("DECLCHILDMAP", declChildMap);

        List<Map> insObjGroupList = (List<Map>) docData.get("INSOBJGROUPLIST");
        Map insObjGroup = insObjGroupList.get(0);
        List<Map> objList = (List<Map>) insObjGroup.get("OBJLIST");
        for (Map obj : objList) {
            Map insObjMap = (Map) obj.get("INSOBJMAP");
            insObjMap.putIfAbsent("height", insObjMap.get("growth"));
            insObjMap.putIfAbsent("bloodPressureLower", insObjMap.get("arterialPressureDown"));
            insObjMap.putIfAbsent("bloodPressureTop", insObjMap.get("arterialPressureTop"));

            Double heightCm = getDoubleParam(insObjMap.get("height"));
            Double weightKg = getDoubleParam(insObjMap.get("weight"));
            Double imt = imt(heightCm, weightKg);
            String imtString = String.format("%.2f", imt);

            insObjMap.putIfAbsent("indexBW", imtString);

            Map contrObjMap = (Map) obj.get("CONTROBJMAP");

            List<Map> contrRiskList = (List<Map>) contrObjMap.get("CONTRRISKLIST");
            for (Map risk : contrRiskList) {
                String riskSysname = getStringParam(risk, "PRODRISKSYSNAME");
                if (riskSysname.equalsIgnoreCase("childSurvives1")) {
                    contrExtMap.putIfAbsent("interPay", "TRUE");
                    Long yearsSurvivor1 = getLongParam(contrExtMap, "termSurvivor1IntermediatePayout");
                    LocalDateTime startDate = LocalDateTime.ofInstant(getDateParam(risk.get("STARTDATE")).toInstant(), ZoneOffset.UTC);
                    LocalDateTime interPayDate = startDate.plusYears(yearsSurvivor1).minusDays(1);
                    contrExtMap.putIfAbsent("interPayDATE", Date.from(interPayDate.toInstant(ZoneOffset.UTC)));
                    contrExtMap.putIfAbsent("interPayYears", getStringParam(contrExtMap, "termSurvivor1IntermediatePayout"));
                }
            }
        }
        contrExtMap.putIfAbsent("interPay", "FALSE");

        // разыменовка Профессий и Сфер деятельности для обоих застрахованных
        List<Map<String, Object>> allInsuredList = getAllInsuredList(docData);
        for (Map<String, Object> insured : allInsuredList) {
            List<Map<String, Object>> extAttributeList2 = getListParam(insured, "extAttributeList2");
            Map<String, Map<String, Object>> extAttributeList2AsMap = getMapByFieldStringValues(extAttributeList2, "EXTATT_SYSNAME");
            // education
            Map<String, Object> education = extAttributeList2AsMap.get("education");
            loggerDebugPretty(logger, "education", education);
            if (education != null) {
                String hbName = "B2B.Life.Profession";
                Long hid = getLongParamLogged(education, "EXTATTVAL_VALUE");
                education.put("EXTATTVAL_VALUE_HID", hid);
                Map<String, Object> hbRecord = getHBRecordByHid(hbName, hid, login, password);
                String name = getStringParamLogged(hbRecord, "name");
                education.put("EXTATTVAL_VALUE", name);
            }
            // activityBusinessKind
            Map<String, Object> activityBusinessKind = extAttributeList2AsMap.get("activityBusinessKind");
            loggerDebugPretty(logger, "activityBusinessKind", activityBusinessKind);
            if (activityBusinessKind != null) {
                String hbName = "B2B.Life.KindOfActivity";
                Long hid = getLongParamLogged(activityBusinessKind, "EXTATTVAL_VALUE");
                activityBusinessKind.put("EXTATTVAL_VALUE_HID", hid);
                Map<String, Object> hbRecord = getHBRecordByHid(hbName, hid, login, password);
                String name = getStringParamLogged(hbRecord, "name");
                activityBusinessKind.put("EXTATTVAL_VALUE", name);
            }
        }

        // текущая дата для определения возрастов
        Date nowDate = null;
        Object signDateObj = docData.get("SIGNDATE");
        if (signDateObj != null) {
            // имеется дата подписания - следует считать её текущей при вычислении возрастов
            nowDate = (Date) parseAnyDate(signDateObj, Date.class, "SIGNDATE", logger.isDebugEnabled());
        }
        if (nowDate == null) {
            // договор еще не подписан - следует использовать текущую дату при определении возрастов
            nowDate = new Date();
        }

        // застрахованный ребенок
        Map<String, Object> insuredChild = getMapParam(docData, "INSUREDMAP");
        // дата рождения застрахованного ребенка
        Date birthDate = (Date) parseAnyDate(insuredChild.get("BIRTHDATE"), Date.class, "BIRTHDATE", logger.isDebugEnabled());
        if (birthDate != null) {
            // возраст ребенка
            int insuredChildAge = getAge(nowDate, birthDate);
            logger.debug("insuredChildAge = " + insuredChildAge);
            // возраст ребенка - флаги для ПФ
            Boolean under14 = (insuredChildAge < 14);
            // "Флаг что моложе 14 лет (REPORTDATA.INSUREDMAP.UNDER14) - Строка. Если значение "TRUE" - моложе, "FALSE" - нет"
            insuredChild.put("UNDER14", under14.toString().toUpperCase());
            getStringParamLogged(insuredChild, "UNDER14"); // для протоколирования
            Boolean from1to17 = (insuredChildAge >= 1) && (insuredChildAge <= 17);
            // "Флаг что от 1 до 17 лет включительно (REPORTDATA.INSUREDMAP.FROM1TO17) - Строка. Если значение "TRUE" - да, "FALSE" - нет"
            insuredChild.put("FROM1TO17", from1to17.toString().toUpperCase());
            getStringParamLogged(insuredChild, "FROM1TO17"); // для протоколирования
        }

        contrExtMap.putIfAbsent("interPay", "FALSE");

        contrExtMap.putIfAbsent("accSumWithoutDidVALUE", 30000L);
        contrExtMap.putIfAbsent("accSumWithoutDidVALUESTR", "30000 (заглушка)");

        // Расчетный номер приложения
        // Игнатова: 15.01.2018 - делать пустые
        declFullMap.put("APPLNUM", " "); // DECLFULLMAP - Данные Декларации Полная ЗВ ЗР_от_18
        declFullMap.put("APPLNUMBOTH", " "); // DECLFULLMAP - Данные Декларации Полная ЗВ ЗР_от_18
        declAdultNSMap.put("APPLNUM", " "); // DECLADULT_NSMAP - Данные Декларации ЗВ_НС
        declChildMap.put("APPLNUM", " "); // DECLCHILDMAP - Данные Декларации ЗР

        // Опция спорт и виды спорта
        Long childIsSport = (Long) contrExtMap.getOrDefault("childIsSport", 0);
        if (childIsSport != 0) {
            // Загружаем справочник
            Map<Long, Object> hbSportKind = getHBRecordMap("B2B.CaringParents.KindOfSport", login, password);

            List<String> sportKindList = new ArrayList<String>();

            // смотрим все виды спорта
            for (int i = 1; i <= 5; i++) {
                Long childSportKindHid = (Long) contrExtMap.getOrDefault("childSportKind" + Integer.toString(i), 0);

                // выбраны данные?
                if (childSportKindHid != 0) {
                    String childSportKindName = "";

                    // выбран пункт "Иное"?
                    if (childSportKindHid == 256) {
                        childSportKindName = (String) contrExtMap.getOrDefault("childSportKindManual", "");

                    } else {
                        Map<String, Object> hbSportItem = (Map<String, Object>) hbSportKind.getOrDefault(childSportKindHid, "");
                        childSportKindName = (String) hbSportItem.getOrDefault("asBsName", "");
                    }

                    if (!childSportKindName.isEmpty()) {
                        // добавляем в список
                        sportKindList.add(childSportKindName);
                    }
                }

            }

            declChildMap.put("QW13", "Да");
            declChildMap.put("SPORTSTR", String.join(", ", sportKindList));
        }
        contrExtMap.putIfAbsent("sport", childIsSport == 0 ? "FALSE" : "TRUE");

        // периодичность оплаты
        docData.put("PAYVARSTR", getStringParam(docData, "PAYVARSTR").equals("Единовременно") ? "ONETIME" : "ANNUALLY");
        contrExtMap.putIfAbsent("survivalPayVarStr", getLongParam(docData, "PAYVARID").equals(103) ? "ONETIME" : "ANNUALLY");

        // Гарантированный период выплат, лет
        // contrExtMap.putIfAbsent("guarantPeriodYears", String.valueOf(getDoubleParam(contrExtMap, "rentPayoutTerm")));

        // вариант выплаты
        Long rentPayoutTerm = getLongParamLogged(contrExtMap, "rentPayoutTerm");
        String rentPayoutTermStr = rentPayoutTerm != null ? rentPayoutTerm.toString() : "";
        contrExtMap.putIfAbsent("guarantPeriodYears", rentPayoutTermStr);

        contrExtMap.putIfAbsent("CURRENCYRATEVALUESTR", String.format("%.2f", getDoubleParam(contrExtMap, "CURRENCYRATE")));
        docData.putIfAbsent("CURRENCYRATEVALUESTR", String.format("%.2f", getDoubleParam(docData, "CURRENCYRATE")));
        docData.putIfAbsent("RUBPREMVALUE", getDoubleParam(docData, "PREMVALUE") * getDoubleParam(docData, "CURRENCYRATE"));

        contrExtMap.put("insurerIsInsured", "1");
        contrExtMap.put("insurerIsInsuredSTR", "Да");

        Long termYearsCount = getLongParam(docData, "TERMYEARCOUNT");

        // Выкупные суммы
        List<Map> redemptSumList = new ArrayList<>();
        final Long totalYearsCount = termYearsCount + (rentPayoutTerm == null ? 0L : rentPayoutTerm);
        for (int i = 1; i <= totalYearsCount; ++i) {
            Map<String, java.io.Serializable> redemptSum = new HashMap<>();
            redemptSum.put("redemptStartDATESTR", getStringParam(contrExtMap, "redemptionSumYear" + String.valueOf(i) + "DateFrom"));
            redemptSum.put("redemptFinishDATESTR",  getStringParam(contrExtMap, "redemptionSumYear" + String.valueOf(i) + "DateTo"));
            //redemptSum.put("redemptStartDATE", new Date());
            //redemptSum.put("redemptEndDATE",  new Date());
            redemptSum.put("redemptDeathLPAmVALUE", (Serializable) contrExtMap.get("redemptionPremDSumYear" + String.valueOf(i-1)));
            redemptSum.put("redemptSumVALUE", (Serializable) contrExtMap.get("redemptionSumYear" + String.valueOf(i-1)));
            redemptSum.put("redemptSumCURRENCYID", getStringParam(docData,"PREMCURRENCYID"));
            redemptSum.put("redemptDeathLPAmCURRENCYID", getStringParam(docData,"PREMCURRENCYID"));

            redemptSumList.add(redemptSum);
        }
        docData.putIfAbsent("REDEMPTSUMLIST", redemptSumList);

        // График взносов
        List<Map> contriButtonList = new ArrayList<>();
        for (int i = 1; i <= termYearsCount; ++i) {
            Map<String, java.io.Serializable> contriButton = new HashMap<>();
            contriButton.put("contributionStartDATE", new Date());
            contriButton.put("contributionFinishDATE", new Date());
            contrExtMap.putIfAbsent("firstPayDATE", getDateParam(contriButton.get("contributionStartDATE")));
            contriButton.put("contributionSumVALUE", (Serializable) contrExtMap.get("insSumYear" + String.valueOf(i-1)));
            contriButton.put("contributionTarifVALUE", (Serializable) contrExtMap.get("insTariffYear" + String.valueOf(i-1)));
            contriButton.put("contributionSumCURRENCYID", getStringParam(docData,"PREMCURRENCYID"));
            contriButton.put("contributionTarifCURRENCYID", getStringParam(docData,"PREMCURRENCYID"));

            contriButtonList.add(contriButton);
        }
        contrExtMap.put("insTariffVALUE", (Serializable) contrExtMap.get("insTariffYear0"));

        docData.putIfAbsent("CONTRIBUTIONLIST", contriButtonList);

        genDateStrs(docData, "*");

        genSumStrs(docData, "*", logger);

        return docData;
    }

    /**
     * получение списка всех застрахованных, для простых однотипных проверок
     * метод переопределен для текущего продукта, т.к. требуется получение нескольких застрахованных (INSUREDMAP, INSURERMAP)
     *
     * @param contract договор
     * @return List
     */
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

    /**
     * проверка показателей здоровья для всех застрахованных по договору
     * переопределен стандартный метод, т.к. требуется:
     * 1. проверить несколько застрахованных (с системными именами объекта страхования 'insuredAdult' и 'insuredChild')
     * 2. проверка 'Имеется положительный ответ на вопрос Декларации застрахованного лица' без проверки других показателей здоровья
     *
     * @param contract Map
     * @return Long
     */
    @Override
    protected Long checkAllInsuredHealthForUW(Map<String, Object> contract) {
        // проверка 'Имеется положительный ответ на вопрос Декларации застрахованного лица' для взрослого (по системному имени объекта страхования)
        Long UW = checkInsuredDeclComplianceUW(contract, "insuredAdult");
        if (UW.equals(UW_DO_NOT_NEEDED)) {
            // проверка 'Имеется положительный ответ на вопрос Декларации застрахованного лица' для ребенка (по системному имени объекта страхования)
            UW = checkInsuredDeclComplianceUW(contract, "insuredChild");
        }
        return UW;
    }

    /**
     * получение застрахованного, профессия которого влияет на андеррайтинг
     * метод переопределен для текущего продукта, т.к. требуется получение застрахованного не из INSUREDMAP (ребенок), а из INSURERMAP (взрослый)
     *
     * @param contract Map
     * @return Map
     */
    protected Map<String, Object> getWorkingInsured(Map<String, Object> contract) {
        Map<String, Object> workingInsured = (Map<String, Object>) contract.get("INSURERMAP");
        return workingInsured;
    }

    /**
     * дополнительная проверка договора на необходимость андеррайтинга (особая для текущего продукта, в дополнение к стандартной)
     * метод переопределен для текущего продукта, т.к. требуются специальные проверки
     *
     * @param contract Map
     * @param login    String
     * @param password String
     * @return Long
     * @throws Exception
     */
    @Override
    protected Long additionalUnderwritingCheck(Map<String, Object> contract, String login, String password) throws Exception {

        logger.debug("CaringParentsCustomFacade.additionalUnderwritingCheck...");

        // если выполнен вызов, значит UW в значении UW_DO_NOT_NEEDED
        Long UW = UW_DO_NOT_NEEDED;

        // ФТ v3: "Если установлен признак по опции 'Спорт' и у выбранного вида спорта в справочнике в графе 'Решение' стоит значение 777"
        Map<String, Object> contractExtMap = getContrExtMap(contract);
        Long childIsSport = getLongParamLogged(contractExtMap, "childIsSport");
        if (childIsSport == null) {
            // не известно значение опции спорт
            logger.error(String.format("No child sport option value was found in this contract - underwriting check is failed!"));
            UW = UW_UNKNOWN;
        } else if (BOOLEAN_FLAG_LONG_VALUE_TRUE.equals(childIsSport)) {
            // установлен признак по опции 'Спорт', следует проверить вид спорта
            Map<String, Object> product = getFullProductMapFromContractMapOrLoadFromDB(contract, login, password);
            Map<String, Object> defValuesMap = getProdDefValsMapFromProduct(product);
            String childSportHandbookName = getStringProdDefValueFromDefValuesMap(defValuesMap, "UW_CHILD_SPORT_KIND_HANDBOOKNAME", "B2B.CaringParents.KindOfSport");
            Long childSportKindCount = getLongProdDefValueFromDefValuesMap(defValuesMap, "UW_CHILD_SPORT_KIND_COUNT", 5L);
            for (int i = 1; i <= childSportKindCount; i++) {
                String childSportKindKeyName = "childSportKind" + Integer.toString(i);
                Long childSportKindHid = getLongParamLogged(contractExtMap, childSportKindKeyName);
                if (childSportKindHid != null && childSportKindHid != 0) {
                    Map<String, Object> sportHBDataParams = new HashMap<String, Object>();
                    sportHBDataParams.put("hid", childSportKindHid);
                    Map<String, Object> sportHBParams = new HashMap<String, Object>();
                    sportHBParams.put("HANDBOOKNAME", childSportHandbookName);
                    sportHBParams.put("HANDBOOKDATAPARAMS", sportHBDataParams);
                    sportHBParams.put(RETURN_LIST_ONLY, true);
                    List<Map<String, Object>> sportKindList = callServiceAndGetListFromResultMap(
                            B2BPOSWS_SERVICE_NAME, "dsB2BHandbookDataBrowseByHBName", sportHBParams, login, password
                    );
                    Map<String, Object> sportKind = null;

                    if (sportKindList != null && sportKindList.size() == 1) {
                        sportKind = sportKindList.get(0);
                        contractExtMap.put(childSportKindKeyName + "Map", sportKind);
                        loggerDebugPretty(logger, "sportKind", sportKind);
                    }

                    if (sportKind == null) {
                        // не удалось получить данные о спорте из справочника по указанному hid
                        logger.error(String.format("Not all child sport kinds was found by hids, specified in this contract - underwriting check is failed!"));
                        UW = UW_UNKNOWN;
                        break;
                    } else {
                        Double sportKindRate = getDoubleParamLogged(sportKind, "rate");
                        if (Math.abs(sportKindRate - 777) <= MIN_SIGNIFICANT_RATE) {
                            // "у выбранного вида спорта в справочнике в графе 'Решение' стоит значение 777"
                            UW = UW_NEEDED;
                            uwLogReason(UW, contract,
                                    "Установлен признак по опции 'Спорт' и у выбранного вида спорта в справочнике в графе 'Решение' стоит значение 777"
                            );
                            break;
                        }
                    }
                }
            }
        }

        // ФТ v3: "Дожитие с промежуточной выплатой"
        // ФТ v3: "Страховая сумма по программе превышает безандеррайтинговый лимит 50% от страховой суммы по риску"
        // ФТ v3: "Дожитие до окончания срока страхования с возвратом взносов в случае смерти ребенка"
        if (UW_DO_NOT_NEEDED.equals(UW)) {
            // риски
            ArrayList<Map<String, Object>> riskList = getAllRisksListFromContract(contract);
            Map<String, Map<String, Object>> riskMap = getRisksMapBySysNameFromRiskList(riskList);
            // ФТ v3: "Дожитие с промежуточной выплатой"
            Map<String, Object> childSurvivesWithInterPayRisk = riskMap.get("childSurvives1");
            if (childSurvivesWithInterPayRisk != null) {
                RowStatus rowStatus = getRowStatusLogged(childSurvivesWithInterPayRisk);
                if (!DELETED.equals(rowStatus)) {
                    // риск имеется и не является удаляемым - следует выполнить проверку суммы согласно фт
                    //
                    // ФТ v3: "Дожитие до окончания срока страхования с возвратом взносов в случае смерти ребенка"
                    Map<String, Object> childSurvivesWithDeathReturnRisk = riskMap.get("childSurvives2");
                    if (childSurvivesWithDeathReturnRisk == null) {
                        // не удалось получить данные о спорте из справочника по указанному hid
                        logger.error(String.format("No childSurvives2 risk was found in this contract - underwriting check is failed!"));
                        UW = UW_UNKNOWN;
                        uwLogReason(UW, contract,
                                "Не удалось найти риск 'Дожитие до окончания срока страхования с возвратом взносов в случае смерти ребенка' (childSurvives2)"
                        );
                    } else {
                        Double childSurvivesWithInterPayRiskInsAmValue = getDoubleParamLogged(childSurvivesWithInterPayRisk, "INSAMVALUE");
                        Double childSurvivesWithDeathReturnRiskInsAmValue = getDoubleParamLogged(childSurvivesWithDeathReturnRisk, "INSAMVALUE");
                        if (childSurvivesWithInterPayRiskInsAmValue > (0.5 * childSurvivesWithDeathReturnRiskInsAmValue)) {
                            UW = UW_NEEDED;
                            uwLogReason(UW, contract,
                                    "Страховая сумма по программе 'Дожитие с промежуточной выплатой' (childSurvives1) " +
                                            "превышает безандеррайтинговый лимит 50% от страховой суммы по риску " +
                                            "'Дожитие до окончания срока страхования с возвратом взносов в случае смерти ребенка' (childSurvives2)"
                            );
                        }
                    }
                }
            }

        }

        logger.debug("CaringParentsCustomFacade.additionalUnderwritingCheck finished.");

        return UW;
    }

    /**
     * Получение данных справочника
     *
     * @param hbName   String
     * @param hid      Long
     * @param login    String
     * @param password String
     * @return Map
     * @throws Exception
     */
    private Map<String, Object> getHBRecordByHid(String hbName, Long hid, String login, String password) throws Exception {
        Map<String, Object> hbRecord = null;
        if (hid != null) {
            Map<String, Object> hbDataParams = new HashMap<String, Object>();
            hbDataParams.put("hid", hid);
            Map<String, Object> hbParams = new HashMap<String, Object>();
            hbParams.put("HANDBOOKNAME", hbName);
            hbParams.put("HANDBOOKDATAPARAMS", hbDataParams);
            hbParams.put(RETURN_LIST_ONLY, true);
            List<Map<String, Object>> hbDataList = callServiceAndGetListFromResultMap(
                    B2BPOSWS_SERVICE_NAME, "dsB2BHandbookDataBrowseByHBName", hbParams, login, password
            );
            if (hbDataList != null && hbDataList.size() == 1) {
                hbRecord = hbDataList.get(0);
                loggerDebugPretty(logger, "hbRecord", hbRecord);
            }
        }
        return hbRecord;
    }

    /**
     * Обработка ПФ по определенным условиям
     *
     * @param params Map
     * @return Map
     * @throws Exception
     */
    @WsMethod(requiredParams = {"REPORTLIST"})
    public Map<String, Object> dsB2BCaringParentsReportBrowsePostprocessing(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BCaringParentsReportBrowsePostprocessing start...");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);

        List<Map<String, Object>> reportList = getListParam(params, "REPORTLIST");
        Map<String, Object> contract = getMapParam(params, "CONTRMAP");
        if (contract == null) {
            Long contractId = getLongParamLogged(params, "CONTRID");
            if (contractId != null) {
                Map<String, Object> contractParams = new HashMap<String, Object>();
                contractParams.put("CONTRID", contractId);
                contractParams.put(RETURN_AS_HASH_MAP, true);
                contract = callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractUniversalLoad", contractParams, login, password);
            }
        }

        if (contract != null) {
            Map<String, Object> product = getMapParam(contract, "PRODUCTMAP");
            Map<String, Object> productVersion = getMapParam(product, "PRODVER");
            List<Map<String, Object>> prodStructList = getListParam(productVersion, "PRODSTRUCTS");
            if ((prodStructList != null) && (!prodStructList.isEmpty())) {
                List<Map<String, Object>> prodRiskList = new ArrayList<Map<String, Object>>();
                Long discriminatorRisk = DISCRIMINATOR_RISK;
                for (Map<String, Object> prodStruct : prodStructList) {
                    if (discriminatorRisk.equals(getLongParam(prodStruct, "DISCRIMINATOR"))) {
                        prodRiskList.add(prodStruct);
                    }
                }
                loggerDebugPretty(logger, "prodRiskList", prodRiskList);
                ArrayList<Map<String, Object>> riskList = getAllRisksListFromContract(contract);
                Map<String, Map<String, Object>> riskMap = getMapByFieldStringValues(riskList, "PRODRISKSYSNAME");
                loggerDebugPretty(logger, "riskMap", riskMap);

                List<Map<String, Object>> reportFiteredList = new ArrayList<Map<String, Object>>();
                reportFiteredList.addAll(reportList);

                // фильтрация доков в зависимости от выбранных рисков
                for (Map<String, Object> report : reportList) {
                    loggerDebugPretty(logger, "Checked report", report);
                    String reportTemplateName = getStringParamLogged(report, "TEMPLATENAME");
                    for (Map<String, Object> prodRisk : prodRiskList) {
                        String prodRiskSysName = getStringParamLogged(prodRisk, "SYSNAME");
                        if (reportTemplateName.contains(prodRiskSysName)) {
                            Map<String, Object> risk = riskMap.get(prodRiskSysName);
                            if (risk == null) {
                                reportFiteredList.remove(report);
                            }
                            break;
                        }
                    }
                }

                reportList = reportFiteredList;

                Map<String, Object> finalContract = contract;
                reportList = reportList.stream()
                        .filter((Map<String, Object> report) -> new CaringParentsCustomFilters.reportsFilter(finalContract).test(report)).collect(Collectors.toList());

            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put(RESULT, reportList);

        logger.debug("dsB2BCaringParentsReportBrowsePostprocessing finished.");
        return result;

    }

    /**
     * Если пользователь пытается перевести статус заявления,
     * у которого дата расчета ≠ текущая системная дата,
     * система выводит сообщение о необходимости произвести перерасчет
     *
     * @param methodParams Map<String, Object>
     * @return Map
     * @throws Exception
     */
    @WsMethod()
    public Map<String, Object> dsB2BCaringParentsBeforeStatusChange(Map<String, Object> methodParams) throws Exception {
        logger.debug("dsB2BCaringParentsBeforeStatusChange start...");

        Map<String, Object> result = new HashMap<String, Object>();

        // получаем данные
        Map<String, Object> params = getMapParam(methodParams, "params");
        Map<String, Object> contract = getMapParam(methodParams, "contract");

        if (contract != null) {
            String state = getStringParam(params.get("STATESYSNAME"));

            // При переводе статуса договора, система должна проверять значение поля "Дата расчета".
            // Если пользователь пытается перевести статус заявления, у которого дата расчета ≠ текущая системная дата,
            // система выводит сообщение о необходимости произвести перерасчет.
            // При переводе в статус "Отменен" проверка не должна производится.

            if (!"B2B_CONTRACT_DRAFT".equals(state) || !"B2B_CONTRACT_CANCEL".equals(state)) {
                // инициализация значений расширенных атрибутов
                Map<String, Object> contrExtMap = getOrCreateContrExtMap(contract);

                Date calculationDATE = (Date) parseAnyDate(contrExtMap.get("calculationDATE"), Date.class, "calculationDATE");
                Date nowDATE = new Date();

                // не в тот же день?
                if (!compareDates(calculationDATE, nowDATE)) {
                    result.put("Error", "Вы должны произвести перерасчет");
                }
            }
        }

        logger.debug("dsB2BCaringParentsBeforeStatusChange finished.");
        return result;
    }

}