package com.bivgroup.services.b2bposws.facade;

import java.util.Map;

/**
 * Интерфейс, описывающий поведение и свойства для договоров, которые относятся с sberlife
 */
public interface ContractLifeValidate {

    // Для сложных общих проверок, храним этот флаг
    String IS_PRE_CALC_CHECK = "IS_PRE_CALC_CHECK";

    // Мапа договора
    String CONTR_MAP = "CONTR_MAP";

    // Мапа продукта
    String PRODUCT_MAP = "PRODUCT_MAP";

    // Мапа продукта
    String PRODDEFVAL_MAP = "PRODDEFVAL_MAP";

    // Мапа расширенных аттрибутов
    String CONTREXT_MAP = "CONTREXT_MAP";

    // Существуют ли расширенные аттрибуты
    String IS_CONTRACT_EXT_VALUES_EXIST = "IS_CONTRACT_EXT_VALUES_EXIST";

    // Вернет null, если проверка не требуется (или невозомжна) или ИД фонда, если проверка требуется и возможна
    String FUND_FOR_INVESTMENT_STRATEGY_CHECK = "FUND_FOR_INVESTMENT_STRATEGY_CHECK";

    // Дата рождения застрахованного
    String INSURED_BIRTH_DATE = "INSURED_BIRTH_DATE";

    // Страховая сумма
    String INSAMVALUE = "INSAMVALUE";

    // Дата рождения застрахованного существует ?
    String IS_INSURED_BIRTH_DATE_EXIST = "IS_INSURED_BIRTH_DATE_EXIST";

    // Мапа инвестиционных стратегий
    String INVEST_STRATEGY_MAP = "INVEST_STRATEGY_MAP";

    // Известна ли стратегия инвестирования ?
    String IS_INVEST_STRATEGY_MAP_EXIST = "IS_INVEST_STRATEGY_MAP_EXIST";

    // Мапа инвестиционных стратегий
    String IS_SUPPORTED_CURRENCY_FOUND = "IS_SUPPORTED_CURRENCY_FOUND";

    // Код поддерживаемой валюты, устанавливается обычнро вместе с IS_SUPPORTED_CURRENCY_FOUND == true
    String INSAMCURRENCY_ALPHA_CODE = "INSAMCURRENCY_ALPHA_CODE";

    // Указана ли валюта
    String IS_CURRENCY_ID_EXISTS = "IS_CURRECNY_ID_EXISTS";

    // Указана ли дата начала действия договора
    String IS_START_DATE_EXISTS = "IS_START_DATE_EXISTS";

    // Указана ли дата начала действия договора
    String CONTRACT_START_DATE = "CONTRACT_START_DATE";

    // Указана ли дата окончания действия договора
    String CONTRACT_FINISH_DATE = "CONTRACT_FINISH_DATE";

    // Указан ли срок страхования
    String IS_TERM_ID_EXISTS = "IS_TERM_ID_EXISTS";

    // Указана ли переодичность оплаты взносов
    String IS_PAY_VAR_ID_EXISTS = "IS_PAY_VAR_ID_EXISTS";

    // Указана ли страховая программа
    String IS_PROD_PROG_ID_EXISTS = "IS_PROD_PROG_ID_EXISTS";

    // Указана ли премия по договору
    String IS_PREM_VALUE_EXISTS = "IS_PREM_VALUE_EXISTS";

    /**
     * Метод валидации даты начала договора
     *
     * <p>
     * Согласно https://rybinsk.bivgroup.com/redmine/issues/23062
     * Дата начала полиса(Contract/PolicyStartDate): не позднее текущей даты (дата вызова сервиса)
     * <p>
     * Текст ошибки: Дата начала договора ранее текущей даты
     *
     * @param contractValidateParams - параметры, которые подготавливаются отдельно
     * @param errorText              - сборщик ошибок валидации
     */
    void validateContractStartDateBeforeCurrentDateFromContractValidateParams(Map<String, Object> contractValidateParams,
                                                                              StringBuffer errorText, String login, String password);

    /**
     * Метод валидации даты окончания полиса за первый год
     * <p>
     * Дата окончания полиса за первый период: 12 месяцев от даты начала договора
     * (например, если дата начала 15.06.2018, то дата окончания может быть только 14.06.2019 ,
     * но НЕ может быть 15.06.2019)
     * <p>
     * Текст ошибки: Дата окончания договора не соответствует периодичности 12 месяцев
     *
     * @param contractValidateParams - параметры, которые подготавливаются отдельно
     * @param errorText              - сборщик ошибок валидации
     */
    void validateContractEndDateForTheFirstPeriodFromContractValidateParams(Map<String, Object> contractValidateParams,
                                                                            StringBuffer errorText, String login, String password);

    /**
     * Метод валидации возраста страхователя
     * <p>
     * Дата рождения(contract/memberlist/member/insurer/birthdate):
     * возраст женщины 55 лет,
     * мужчина 60 лет
     * <p>
     * (т.е. в дату окончания первого периода страхования (Дата окончания полиса за первый период)
     * допускается для женщин – 55 лет и 364/365 (для високосного года) дней, для мужчин – 60 лет и 364/365
     * (для високосного года) дней)
     * <p>
     * Текст ошибки: Клиент не может быть застрахован по возрасту
     *
     * @param contractValidateParams - параметры, которые подготавливаются отдельно
     * @param errorText              - сборщик ошибок валидации
     */
    void validateInsurerBirthDateFromContractValidateParams(Map<String, Object> contractValidateParams,
                                                            StringBuffer errorText, String login, String password);

    /**
     * Метод валидации даты окончания полиса
     *
     * <p>
     * Дата окончания страхования (Contract/PolicyEndDate): =
     * «Дата начала страхования» + Срок страхования (лет) < «Дата рождения застрахованного» + 56 лет для женщин/61 лет для мужчин
     * <p>
     * Текст ошибки: Клиент не может быть застрахован по возрасту
     *
     * @param contractValidateParams - параметры, которые подготавливаются отдельно
     * @param errorText              - сборщик ошибок валидации
     */
    void validateContractEndDateFromContractValidateParams(Map<String, Object> contractValidateParams,
                                                           StringBuffer errorText, String login, String password);

    /**
     * Метод валидации даты согласия на обработку персональных данных
     * <p>
     * Дата согласия на обработку персональных данных (contract/memberlist/member/insurer/DateAllowPersData):
     * не позднее текущей даты (дата вызова сервиса)
     * <p>
     * Текст ошибки: Дата согласия на обработку персональных данных ранее текущей даты
     *
     * @param contractValidateParams - параметры, которые подготавливаются отдельно
     * @param errorText              - сборщик ошибок валидации
     */
    void validateDateOfConsentToTheProcessingOfPersonalDataFromContractValidateParams(Map<String, Object> contractValidateParams,
                                                                                      StringBuffer errorText, String login, String password);

    /**
     * Метод валидации даты согласия на обработку персональных данных
     * <p>
     * Дата подписания договора страхования (Contract/PolicySignDate): не позднее текущей даты
     * (дата вызова сервиса)
     * <p>
     * Текст ошибки: Дата подписания договора ранее текущей даты
     *
     * @param contractValidateParams - параметры, которые подготавливаются отдельно
     * @param errorText              - сборщик ошибок валидации
     */
    void validateDateOfSigningTheInsuranceContractFromContractValidateParams(Map<String, Object> contractValidateParams,
                                                                             StringBuffer errorText, String login, String password);


    /**
     * Метод валидации даты выдачи паспорта страхователя
     * <p>
     * Дата выдачи паспорта страхователя: дата не может быть из будущего периода.
     * Дата не может быть ранее, чем через 14 лет после даты рождения страхователя.
     * <p>
     * Текст ошибки: Дата выдачи паспорта некорректна: паспорт не может быть выдан ранее 14 лет с даты рождения
     *
     * @param contractValidateParams - параметры, которые подготавливаются отдельно
     * @param errorText              - сборщик ошибок валидации
     */
    void validateDateOfIssueOfThePassportOfTheInsuredFromContractValidateParams(Map<String, Object> contractValidateParams,
                                                                                StringBuffer errorText, String login, String password);


    /**
     * Метод валидации страховой суммы
     * <p>
     * Страховая сумма: не более 10 млн. руб.
     * <p>
     * Текст ошибки: Максимальный размер страховой суммы не болеее 10 000 000 рублей
     *
     * @param contractValidateParams - параметры, которые подготавливаются отдельно
     * @param errorText              - сборщик ошибок валидации
     * @param insAmValueMaxDefault   - максимальное значение страховой суммы по умолчанию
     */
    void validateInsamvalueFromContractValidateParams(Map<String, Object> contractValidateParams,
                                                      StringBuffer errorText, Long insAmValueMaxDefault, String login, String password);

    /**
     * Метод валидации ставки кредита в %
     * <p>
     * Ставка кредита: от 5% до 20%
     * <p>
     * Текст ошибки: Процентная ставка должна находится в диапазоне от 5% до 20%. Введите корректную процентную ставку
     *
     * @param contractValidateParams - параметры, которые подготавливаются отдельно
     * @param errorText              - сборщик ошибок валидации
     */
    void validateRateOfCreditFromContractValidateParams(Map<String, Object> contractValidateParams,
                                                        StringBuffer errorText, Long minRateDefault, Long maxRateDefault, String login, String password);

    /**
     * Валидация расчитанной премии за первый период с премией
     * <p>
     * Проверка рассчитанной премии за первый период с премией, которая возвращается из сервиса
     * (Contract/Premium) Возвращать ошибку, если значения не совпадают.
     * <p>
     * Текст ошибки: Премия за первый период не соответствует рассчитанной премии
     *
     * @param contractValidateParams - параметры, которые подготавливаются отдельно
     * @param errorText              - сборщик ошибок валидации
     * @param minRateDefault         - минимальный процент срока страхования
     * @param maxRateDefault         - максимальный процент срока страхования
     */
    void validateCalculatedPremiumForTheFirstPeriodWithAPremiumFromContractValidateParams(
            Map<String, Object> contractValidateParams, StringBuffer errorText, Long minRateDefault, Long maxRateDefault, String login, String password);

    /**
     * Валидация срока кредитования
     * <p>
     * Срок кредитования не более 360 месяцев (30 лет)
     * <p>
     * Текст ошибки: Срок кредитования превышен
     *
     * @param contractValidateParams - параметры, которые подготавливаются отдельно
     * @param errorText              - сборщик ошибок валидации
     */
    void validateLoanTermsFromContractValidateParams(Map<String, Object> contractValidateParams,
                                                     StringBuffer errorText, String login, String password);
}
