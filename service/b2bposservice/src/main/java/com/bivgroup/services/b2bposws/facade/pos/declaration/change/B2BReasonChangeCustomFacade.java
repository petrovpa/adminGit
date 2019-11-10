package com.bivgroup.services.b2bposws.facade.pos.declaration.change;

import com.bivgroup.services.b2bposws.facade.pos.declaration.B2BDeclarationBaseFacade;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BReasonChangeCustom")
public class B2BReasonChangeCustomFacade extends B2BDeclarationBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());
    private static final String KIND_CHANGE_REASON_ID_PARAM_NAME = "kindChangeReasonId";

    //private static final String REASON_CHANGE_MAP_PARAMNAME = "REASONMAP";

    // Лёха И.: "... будет fixType 1 - Автопилот, а 2 - Единовременная фиксация ..."
    /** Тип фиксации: 1 - Автопилот */
    public static final Long FIX_INCOME_FIX_TYPE_AUTO = 1L;
    /** Тип фиксации: 2 - Единовременная фиксация */
    public static final Long FIX_INCOME_FIX_TYPE_ONCE = 2L;

    public static final Double FIX_INCOME_LIMIT_PCT_FACTOR = 0.01;

    /** ФТ: "Если выбран тип "Единовременная фиксация", то <...> ограничение на минимальное значение - 10%." */
    private static final Double FIX_INCOME_FIX_TYPE_ONCE_UP_LIMIT_MIN_PCT = FIX_INCOME_LIMIT_PCT_FACTOR * 10.0;

    private static final Long FIX_INCOME_LIMIT_15 = 15L;
    private static final Long FIX_INCOME_LIMIT_30 = 30L;
    private static final Long FIX_INCOME_LIMIT_50 = 50L;

    private static final Set<Long> FIX_INCOME_LIMIT_SET;
    private static final String FIX_INCOME_LIMIT_SET_STR;

    private static final Map<Long, Double> EXT_PREM_PAY_MIN_SUM_BY_CURRENCY;

    static {
        FIX_INCOME_LIMIT_SET = new HashSet<Long>();
        FIX_INCOME_LIMIT_SET.add(FIX_INCOME_LIMIT_50);
        FIX_INCOME_LIMIT_SET.add(FIX_INCOME_LIMIT_30);
        FIX_INCOME_LIMIT_SET.add(FIX_INCOME_LIMIT_15);

        StringBuilder sb = new StringBuilder();
        for (Long limit : FIX_INCOME_LIMIT_SET) {
            sb.append(limit).append(" %, ");
        }
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        FIX_INCOME_LIMIT_SET_STR = sb.toString();

        // ФТ: "Система должна проверять, что введенная сумма больше или равно 50 000 (если договор в рублях), 1500 (если договор в долларах)."
        EXT_PREM_PAY_MIN_SUM_BY_CURRENCY = new HashMap<Long, Double>();
        EXT_PREM_PAY_MIN_SUM_BY_CURRENCY.put(CURRENCY_ID_RUB, 50000.0);
        EXT_PREM_PAY_MIN_SUM_BY_CURRENCY.put(CURRENCY_ID_USD, 1500.0);

    }

    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateChangeFund(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateChangeFund begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        List<String> errorList = new ArrayList<String>();

        Long kindChangeReasonId = getLongParamLogged(params, KIND_CHANGE_REASON_ID_PARAM_NAME);
        Map<String, Object> kindChangeReason = dctFindById(KIND_CHANGE_REASON_ENTITY_NAME, kindChangeReasonId);
        String kindChangeReasonName = getStringParamLogged(kindChangeReason, "name");
        String valueOwnerNameGenitive = String.format("в параметрах опции '%s'", kindChangeReasonName);

        checkNotEmpty(errorList, params, "changeDate" + (isCallFromGate ? "$date" : ""), "Не указана дата внесения изменений", valueOwnerNameGenitive);
        checkNotEmpty(errorList, params, "prevFundId", "Не указан старый фонд", valueOwnerNameGenitive);
        checkNotEmpty(errorList, params, "fundId", "Не указан новый фонд", valueOwnerNameGenitive);

        Map<String, Object> result = makeResultMapFromParamsAndError(params, errorList);
        logger.debug("dsB2BReasonChangeValidateChangeFund end");
        return result;
    }

    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateFixIncome(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateFixIncome begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        List<String> errorList = new ArrayList<String>();

        Long kindChangeReasonId = getLongParamLogged(params, KIND_CHANGE_REASON_ID_PARAM_NAME);
        Map<String, Object> kindChangeReason = dctFindById(KIND_CHANGE_REASON_ENTITY_NAME, kindChangeReasonId);
        String kindChangeReasonName = getStringParamLogged(kindChangeReason, "name");
        String valueOwnerNameGenitive = String.format("в параметрах опции '%s'", kindChangeReasonName);

        checkNotEmpty(errorList, params, "changeDate" + (isCallFromGate ? "$date" : ""), "Не указана дата внесения изменений", valueOwnerNameGenitive);
        checkNotEmpty(errorList, params, "fixTypeId", "Не указан тип фиксации", valueOwnerNameGenitive);

        if (errorList.isEmpty()) {
            Long fixTypeId = getLongParamLogged(params, "fixTypeId");
            boolean fixMaxLimitIsNull = (params.get("fixMaxLimit") == null);
            boolean fixMinLimitIsNull = (params.get("fixMinLimit") == null);
            Double fixMaxLimit = getDoubleParamLogged(params, "fixMaxLimit");
            Double fixMinLimit = getDoubleParamLogged(params, "fixMinLimit");
            Long fixMaxLimitRnd = ((Double) (fixMaxLimit / FIX_INCOME_LIMIT_PCT_FACTOR)).longValue();
            Long fixMinLimitRnd = ((Double) (fixMinLimit / FIX_INCOME_LIMIT_PCT_FACTOR)).longValue();
            if (FIX_INCOME_FIX_TYPE_AUTO.equals(fixTypeId)) {
                // Тип фиксации: 1 - Автопилот
                if (fixMaxLimitIsNull && fixMinLimitIsNull) {
                    errorList.add(String.format(
                            "Не указано ни значения верхнего лимита ни значения нижнего лимита фиксации %s - требуется указать как минимум одно из значений",
                            valueOwnerNameGenitive
                    ));
                } else {
                    boolean isCorrectMaxLimitValue = FIX_INCOME_LIMIT_SET.contains(fixMaxLimitRnd);
                    boolean isCorrectMinLimitValue = FIX_INCOME_LIMIT_SET.contains(fixMinLimitRnd);
                    if (!isCorrectMaxLimitValue && !isCorrectMinLimitValue) {
                        errorList.add(String.format(
                                "Указаны некорректные значения верхнего и нижнего лимитов фиксации %s (хотя бы одно значение должно быть из списка - %s)",
                                valueOwnerNameGenitive, FIX_INCOME_LIMIT_SET_STR
                        ));
                    }
                }
            } else if (FIX_INCOME_FIX_TYPE_ONCE.equals(fixTypeId)) {
                // Тип фиксации: 2 - Единовременная фиксация
                if (fixMaxLimitIsNull) {
                    errorList.add(String.format(
                            "Не указано значение верхнего лимита фиксации %s",
                            valueOwnerNameGenitive
                    ));
                } else if (fixMaxLimit < FIX_INCOME_FIX_TYPE_ONCE_UP_LIMIT_MIN_PCT) {
                    errorList.add(String.format(
                            "Указано некорректное значение верхнего лимита фиксации %s (допустимое значение - не менее %s %%)",
                            valueOwnerNameGenitive, FIX_INCOME_FIX_TYPE_ONCE_UP_LIMIT_MIN_PCT
                    ));
                }
            } else {
                // неподдерживаемый тип фиксации
                errorList.add(String.format(
                        "Указан некорректный тип фиксации %s (допустимые значения - %d или %d)",
                        valueOwnerNameGenitive, FIX_INCOME_FIX_TYPE_AUTO, FIX_INCOME_FIX_TYPE_ONCE
                ));
            }
        }

        Map<String, Object> result = makeResultMapFromParamsAndError(params, errorList);
        logger.debug("dsB2BReasonChangeValidateFixIncome end");
        return result;
    }

    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateExtPremPay(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateExtPremPay begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        List<String> errorList = new ArrayList<String>();

        Long kindChangeReasonId = getLongParamLogged(params, KIND_CHANGE_REASON_ID_PARAM_NAME);
        Map<String, Object> kindChangeReason = dctFindById(KIND_CHANGE_REASON_ENTITY_NAME, kindChangeReasonId);
        String kindChangeReasonName = getStringParamLogged(kindChangeReason, "name");
        String valueOwnerNameGenitive = String.format("в параметрах опции '%s'", kindChangeReasonName);

        checkNotEmpty(errorList, params, "dopPremVal", "Не указан размер дополнительного страхового взноса", valueOwnerNameGenitive);

        // ФТ: "Система должна проверять, что введенная сумма больше или равно 50 000 (если договор в рублях), 1500 (если договор в долларах)."
        Long premCurrencyId = getLongParamLogged(params, "PREMCURRENCYID");
        if (premCurrencyId == null) {
            errorList.add("Не удалось определить валюту по указанному в заявлении договору!");
        } else {
            Double minSum = EXT_PREM_PAY_MIN_SUM_BY_CURRENCY.get("premCurrencyId");
            if (minSum != null) {
                Double dopPremVal = getDoubleParamLogged(params, "dopPremVal");
                if (dopPremVal < minSum) {
                    errorList.add("Размер дополнительного страхового взноса (с учетом валюты по указанному в заявлении договору) менее установленного предела");
                }
            }
        }

        Map<String, Object> result = makeResultMapFromParamsAndError(params, errorList);
        logger.debug("dsB2BReasonChangeValidateExtPremPay end");
        return result;
    }

    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateWithdrawIncome(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateWithdrawIncome begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        List<String> errorList = new ArrayList<String>();

        Long kindChangeReasonId = getLongParamLogged(params, KIND_CHANGE_REASON_ID_PARAM_NAME);
        Map<String, Object> kindChangeReason = dctFindById(KIND_CHANGE_REASON_ENTITY_NAME, kindChangeReasonId);
        String kindChangeReasonName = getStringParamLogged(kindChangeReason, "name");
        String valueOwnerNameGenitive = String.format("в параметрах опции '%s'", kindChangeReasonName);

        checkNotEmpty(errorList, params, "changeDate" + (isCallFromGate ? "$date" : ""), "Не указана дата внесения изменений", valueOwnerNameGenitive);

        Map<String, Object> bankDetails = getMapParam(params, "bankDetailsId_EN");
        if ((bankDetails == null) || (bankDetails.isEmpty())) {
            errorList.add("Не указаны банковские реквизиты получателя " + valueOwnerNameGenitive);
        }

        if (errorList.isEmpty()) {
            //
            valueOwnerNameGenitive = "в банковских реквизитах получателя " + valueOwnerNameGenitive;
            // Наименование банка и его отделения
            checkNotEmpty(errorList, bankDetails, "bankName", "Не указано наименование банка и его отделения", valueOwnerNameGenitive);
            // Адрес и телефон банка
            //checkNotEmpty(errorList, bankDetails, "bankAddressPhone","Не указан адрес и телефон банка", valueOwnerNameGenitive);
            // Телефон банка
            checkNumberIfNotEmpty(errorList, bankDetails, "bankPhone",
                    "о телефоне банка", valueOwnerNameGenitive, "10"
            );
            // БИК банка
            checkNotEmptyAndNumber(errorList, bankDetails, "bankBIK",
                    valueOwnerNameGenitive, "Не указан БИК банка",
                    "о БИК банка", "9"
            );
            /*
            // ИНН банка
            checkNotEmptyAndNumber(errorList, bankDetails, "bankINN",
                    valueOwnerNameGenitive, "Не указан ИНН банка",
                    "о ИНН банка", "10"
            );
            // Расчетный счет банка
            checkNotEmptyAndNumber(errorList, bankDetails, "bankSettlementAccount",
                    valueOwnerNameGenitive, "Не указан расчетный счет",
                    "о расчетном счете", "20"
            );
            // Корреспондентский счет банка
            checkNotEmptyAndNumber(errorList, bankDetails, "bankAccount",
                    valueOwnerNameGenitive, "Не указан корреспондентский счет",
                    "о корреспондентском счете", "20"
            );
            */
            if (getStringParamLogged(bankDetails, "cardNumber").isEmpty()) {
                // Лицевой (расчетный) счет получателя
                checkNotEmptyAndNumber(errorList, bankDetails, "account",
                        valueOwnerNameGenitive, "Не указан лицевой (расчетный) счет",
                        "о лицевом (расчетном) счете", "20"
                );
            }
            // Номер пластиковой карты получателя
            checkNumberIfNotEmpty(errorList, bankDetails, "cardNumber",
                    "о номере пластиковой карты", valueOwnerNameGenitive, "16"
            );
        }

        Map<String, Object> result = makeResultMapFromParamsAndError(params, errorList);
        logger.debug("dsB2BReasonChangeValidateWithdrawIncome end");
        return result;
    }

    /**
     * Сервис валидации изменения фамилии страхователя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateHChangeSurname(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateHChangeSurname begin");
       /* Long kindChangeReasonId = getLongParamLogged(params, KIND_CHANGE_REASON_ID_PARAM_NAME);
        Map<String, Object> kindChangeReason = dctFindById(KIND_CHANGE_REASON_ENTITY_NAME, kindChangeReasonId);
        String kindChangeReasonName = getStringParamLogged(kindChangeReason, "surname");
        String valueOwnerNameGenitive = String.format("в параметрах опции '%s'", kindChangeReasonName);
        List<String> errorList = new ArrayList<>();
        checkNotEmpty(errorList, params, "surname", "Не указана фамилия страхователя", valueOwnerNameGenitive);*/
        logger.debug("dsB2BReasonChangeValidateHChangeSurname end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения конт.информации выгодоприобретателя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateBChangeContInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateBChangeContInfo begin");

        logger.debug("dsB2BReasonChangeValidateBChangeContInfo end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения перс.данных страхователя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateHChangePersData(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateHChangePersData begin");

        logger.debug("dsB2BReasonChangeValidateHChangePersData end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения перс.данных застрахованного
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateLaChangePersData(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateLaChangePersData begin");

        logger.debug("dsB2BReasonChangeValidateLaChangePersData end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения фамилии застрахованного
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateLaChangeSurname(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateLaChangePersData begin");

        logger.debug("dsB2BReasonChangeValidateLaChangePersData end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения адреса страхователя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateHChangeAddress(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateHChangeAddress begin");
        logger.debug("dsB2BReasonChangeValidateHChangeAddress end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения фамилии выгодоприобретателя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateBChangeSurname(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateBChangeSurname begin");

        logger.debug("dsB2BReasonChangeValidateBChangeSurname end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения перс.данных выгодоприобретателя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateBChangePersData(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateBChangePersData begin");
        logger.debug("dsB2BReasonChangeValidateBChangePersData end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения паспортных данных страхователя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateHChangePassport(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateHChangePassport begin");
        logger.debug("dsB2BReasonChangeValidateHChangePassport end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения паспортных данных застрахованного
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateLaChangePassport(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateLaChangePassport begin");
        logger.debug("dsB2BReasonChangeValidateLaChangePassport end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения паспортных данных выгодоприобретателя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateBChangePassport(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateBChangePassport begin");
        logger.debug("dsB2BReasonChangeValidateBChangePassport end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения адреса застрахованного
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateLaChangeAddress(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateLaChangeAddress begin");
        logger.debug("dsB2BReasonChangeValidateLaChangeAddress end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения адреса выгодоприобретателя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateBChangeAddress(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateBChangeAddress begin");
        logger.debug("dsB2BReasonChangeValidateBChangeAddress end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения конт.информации страхователя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateHChangeContInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateHChangeContInfo begin");
        logger.debug("dsB2BReasonChangeValidateHChangeContInfo end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения застрахованого
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateLaChange(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateLaChange begin");
        logger.debug("dsB2BReasonChangeValidateLaChange end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения выгодоприобретателя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateBenChange(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateBenChange begin");
        logger.debug("dsB2BReasonChangeValidateBenChange end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения страхователя
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateHolderChange(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateHolderChange begin");
        logger.debug("dsB2BReasonChangeValidateHolderChange end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Финансовые каникулы"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateFinancialVacation(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateFinancialVacation begin");
        logger.debug("dsB2BReasonChangeValidateFinancialVacation end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Выход из финансовых каникул"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateExitFinancialVacation(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateExitFinancialVacation begin");
        logger.debug("dsB2BReasonChangeValidateExitFinancialVacation end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Исключение программ"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateExcludePrograms(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateExcludePrograms begin");
        logger.debug("dsB2BReasonChangeValidateExcludePrograms end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Включение программ"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateIncludePrograms(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateIncreaseInsSum begin");
        logger.debug("dsB2BReasonChangeValidateIncreaseInsSum end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Увеличение взноса / СС"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateIncreaseInsSum(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateIncreaseInsSum begin");
        logger.debug("dsB2BReasonChangeValidateIncreaseInsSum end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Уменьшение взноса / СС"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateDecreaseInsSum(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateIncreaseInsSum begin");
        logger.debug("dsB2BReasonChangeValidateIncreaseInsSum end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Сокращение срока страхования"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateDecreasePeriod(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateIncreaseInsSum begin");
        logger.debug("dsB2BReasonChangeValidateIncreaseInsSum end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Увеличение срока страхования"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateIncreasePeriod(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateIncreaseInsSum begin");
        logger.debug("dsB2BReasonChangeValidateIncreaseInsSum end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения конт.информации застрахованного
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateLaChangeContInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateLaChangeContInfo begin");
        logger.debug("dsB2BReasonChangeValidateLaChangeContInfo end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Перевод в оплаченный"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateTransferToPaid(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateTransferToPaid begin");
        logger.debug("dsB2BReasonChangeValidateTransferToPaid end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Периодичности оплаты"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateInstalments(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateInstalments begin");
        logger.debug("dsB2BReasonChangeValidateInstalments end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Заявление на изготовление дубликата документа"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateDuplicateDocument(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateDuplicateDocument begin");
        logger.debug("dsB2BReasonChangeValidateDuplicateDocument end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    /**
     * Сервис валидации изменения "Расторжение"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateCancellation(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateCancellation begin");
        logger.debug("dsB2BReasonChangeValidateCancellation end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }
    /**
     * Сервис валидации изменения "Анулирован"
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {KIND_CHANGE_REASON_ID_PARAM_NAME})
    public Map<String, Object> dsB2BReasonChangeValidateAnnulment(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BReasonChangeValidateAnnulment begin");
        logger.debug("dsB2BReasonChangeValidateAnnulment end");
        return makeResultMapFromParamsAndError(params, new ArrayList<>());
    }

    private Map<String, Object> makeResultMapFromParamsAndError(Map<String, Object> params, List<String> errorList) {
        String error = getErrorStringFromList(errorList);
        Map<String, Object> result = new HashMap<String, Object>();
        if (error.isEmpty()) {
            result.putAll(params);
            result.remove(LOGIN);
            result.remove(PASSWORD);
        } else {
            logger.debug("Validation error: " + error);
            result.put(ERROR, error);
        }
        return result;
    }

}

