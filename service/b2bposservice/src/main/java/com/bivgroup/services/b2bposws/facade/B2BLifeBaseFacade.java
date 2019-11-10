/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade;

import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.utils.XMLUtil;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author averichevsm
 */
@BOName("B2BLifeBase")
public abstract class B2BLifeBaseFacade extends B2BDictionaryBaseFacade implements ContractLifeValidate {
    protected static final String SIGNB2BPOSWS_SERVICE_NAME = Constants.SIGNB2BPOSWS;
    // protected static final String SIGNB2BPOSWS_SERVICE_NAME = B2BPOSWS_SERVICE_NAME; // !только для отладки!
    protected static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;

    // требуется ли андеррайтинг
    // 0L - нет, андеррайтинг не требуется
    // 1L - да, андеррайтинг требуется
    // 2L - недостаточно сведений, чтоб определить однозначно (например, не известна валюта и, как следствие, не проверить лимиты и пр.)
    public static final Long UW_DO_NOT_NEEDED = 0L;
    public static final Long UW_NEEDED = 1L;
    public static final Long UW_UNKNOWN = 2L;
    protected static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    /**
     * Имя расширенного атрибута договора, хранящего ссылку на фонд (fund)
     */
    protected static final String FUND_ID_PARAMNAME = "fund";
    /**
     * Имя расширенного атрибута договора, хранящего тип ссылки на фонд
     * (fundRefTypeId)
     */
    protected static final String FUND_REF_TYPE_ID_PARAMNAME = "fundRefTypeId";
    /**
     * Тип ссылки на фонд (fundRefTypeId): null/1 - на старый справочник
     * (B2B.InvestNum1.Funds)
     */
    protected static final Long FUND_REF_TYPE_ID_FUND_HANDBOOK = 1L;
    /**
     * Тип ссылки на фонд (fundRefTypeId): 2 - на новый справочник
     * (B2B.SBSJ.RelationStrategyProduct)
     */
    protected static final Long FUND_REF_TYPE_ID_PRODUCT_STRATEGY_HANDBOOK = 2L;
    // флаг подробного протоколирования
    protected boolean isVerboseLogging = logger.isDebugEnabled();

    /** Требуется ли принудительная валидация договора (в том числе при первом сохранении) */
    protected static final String IS_FORCED_VALIDATION_PARAMNAME = "IS_FORCED_VALIDATION";

    protected boolean checkIsValueValidByRegExp(Object value, String regExp, boolean allowNull) {
        boolean result;
        if (value == null) {
            result = allowNull;
        } else {
            Pattern pattern = Pattern.compile(regExp);
            String checkedString = getStringParam(value);
            Matcher matcher = pattern.matcher(checkedString);
            result = matcher.matches();
            //logger.debug("Проверка значения '" + checkedString + "' по регулярному выражению '" + regExp + "' завершена с результатом '" + result + "'.");
        }
        return result;
    }

    protected boolean checkIsValueInvalidByRegExp(Object value, String regExp, boolean allowNull) {
        return !checkIsValueValidByRegExp(value, regExp, allowNull);
    }

    protected void setOverridedParam(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        if (isLogged) {
            Object oldValue = paramParent.get(paramName);
            logParamOverriding(paramName, newValue, oldValue);
        }
        paramParent.put(paramName, newValue);
    }

    protected void setGeneratedParam(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        paramParent.put(paramName, newValue);
        if (isLogged) {
            logParamGeneration(paramName, newValue);
        }
    }

    protected void setGeneratedParamIfNull(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        if (paramParent.get(paramName) == null) {
            setGeneratedParam(paramParent, paramName, newValue, isLogged);
        }
    }

    private void logParamGeneration(String paramName, Object newValue) {
        StringBuilder logStr = new StringBuilder();
        logStr.append("Значение параметра '");
        logStr.append(paramName);
        logStr.append("' не найдено во входных данных. Для указанного параметра сгенерировано новое значение: ");
        logStr.append(newValue);
        if (newValue != null) {
            logStr.append(" (");
            logStr.append(newValue.getClass().getSimpleName());
            logStr.append(").");
        } else {
            logStr.append(".");
        }
        logger.debug(logStr.toString());
    }

    private void logParamOverriding(String paramName, Object newValue, Object oldValue) {
        StringBuilder logStr = new StringBuilder();
        logStr.append("Значение параметра '");
        logStr.append(paramName);
        logStr.append("' из входных данных (");
        logStr.append(oldValue);
        logStr.append(") проигонорировано. Для указанного параметра сгенерировано новое значение: ");
        logStr.append(newValue);
        if (newValue != null) {
            logStr.append(" (");
            logStr.append(newValue.getClass().getSimpleName());
            logStr.append(")");
        }
        logStr.append(".");
        logger.debug(logStr.toString());
    }

    // возвращает INSOBJMAP по INSOBJSYSNAME из CONTRMAP.INSOBJGROUPLIST
    protected Map<String, Object> getInsObjBySysNameFromContract(Map<String, Object> contract, String sysNameValue) {
        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        if (insObjGroupList != null) {
            for (Map<String, Object> insObjGroup : insObjGroupList) {
                List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroup.get("OBJLIST");
                if (objList != null) {
                    for (Map<String, Object> obj : objList) {
                        Map<String, Object> insObjMap = (Map<String, Object>) obj.get("INSOBJMAP");
                        if (insObjMap != null) {
                            String insObjSysName = getStringParamLogged(insObjMap, "INSOBJSYSNAME");
                            if (sysNameValue.equals(insObjSysName)) {
                                return insObjMap;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    protected void validateInsurerDocuments(ArrayList<Map<String, Object>> documentList, Long citizenship, StringBuffer errorText) {

        String docType;
        String docSeriesRegExp;
        String docNumberRegExp;
        if (citizenship.intValue() == 0) {
            docType = "PassportRF";
            docSeriesRegExp = "\\d{4}";
            docNumberRegExp = "\\d{6}";
        } else {
            docType = "ForeignPassport";
            docSeriesRegExp = "^[0-9A-Za-z]{1,10}"; // аналогично angular-интерфейсу
            docNumberRegExp = "^[0-9A-Za-z]{1,20}"; // аналогично angular-интерфейсу
        }

        Map<String, Object> passport = null;
        for (Map<String, Object> document : documentList) {
            String docTypeSysName = getStringParam(document.get("DOCTYPESYSNAME"));
            if (docType.equals(docTypeSysName)) {
                passport = document;
                break;
            }
        }

        if (passport == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) список документов страхователя (documentList) не содержит записи о паспортных данных. ");
        } else {

            if (passport.get("ISSUEDBY") == null) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных не содержит данных о том, кем выдан паспорт (ISSUEDBY). ");
            }
            if (passport.get("ISSUEDATE") == null) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных не содержит даты выдачи паспорта (ISSUEDATE). ");
            }

            String docSeries = getStringParam(passport.get("DOCSERIES"));
            if (docSeries.isEmpty()) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных не содержит серию паспорта. ");
            } else if (checkIsValueInvalidByRegExp(docSeries, docSeriesRegExp, false)) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных содержит некорректное значение серии паспорта. ");
            }

            String docNumber = getStringParam(passport.get("DOCNUMBER"));
            if (docNumber.isEmpty()) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных не содержит номер паспорта. ");
            } else if (checkIsValueInvalidByRegExp(docNumber, docNumberRegExp, false)) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных содержит некорректное значение номера паспорта. ");
            }
        }
    }

    protected void validateInsurerInfo(Map<String, Object> insurer, StringBuffer errorText) {
        if (insurer.get("FIRSTNAME") == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) не указано имя страхователя (FIRSTNAME). ");
        }
        if (insurer.get("LASTNAME") == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) не указана фамилия страхователя (LASTNAME). ");
        }
        if (insurer.get("BIRTHDATE") == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) не указана дата рождения страхователя (BIRTHDATE). ");
        }
        if (insurer.get("GENDER") == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) не указан пол страхователя (GENDER). ");
        }
        Long citizenship = getLongParam(insurer.get("CITIZENSHIP"));
        if (citizenship == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) не указано гражданство страхователя (CITIZENSHIP). ");
        } else {
            ArrayList<Map<String, Object>> documentList = (ArrayList<Map<String, Object>>) insurer.get("documentList");
            if ((documentList == null) || (documentList.isEmpty())) {
                errorText.append("В сведениях о страхователе (INSURERMAP) список документов страхователя (documentList) отсутствует или пуст. ");
            } else {
                validateInsurerDocuments(documentList, citizenship, errorText);
            }

            ArrayList<Map<String, Object>> contactList = (ArrayList<Map<String, Object>>) insurer.get("contactList");
            if ((contactList == null) || (contactList.isEmpty())) {
                errorText.append("В сведениях о страхователе (INSURERMAP) список контактных данных страхователя (contactList) отсутствует или пуст. ");
            } else {
                validateInsurerContacts(contactList, errorText);
            }
        }
    }

    protected void validateInsurerContacts(ArrayList<Map<String, Object>> contactList, StringBuffer errorText) {

        String personalEmail = "";
        String mobilePhone = "";

        for (Map<String, Object> contact : contactList) {
            String contactTypeSysName = getStringParam(contact.get("CONTACTTYPESYSNAME"));
            if ((personalEmail.isEmpty()) && ("PersonalEmail".equals(contactTypeSysName))) {
                personalEmail = getStringParam(contact.get("VALUE"));
            } else if ((mobilePhone.isEmpty()) && ("MobilePhone".equals(contactTypeSysName))) {
                mobilePhone = getStringParam(contact.get("VALUE"));
            }
        }

        if (personalEmail.isEmpty()) {
            errorText.append("В сведениях о страхователе (INSURERMAP) список контактных данных страхователя (contactList) не содержит записи c адресом электронной почты. ");
        }

        if (mobilePhone.isEmpty()) {
            errorText.append("В сведениях о страхователе (INSURERMAP) список контактных данных страхователя (contactList) не содержит записи с номером мобильного телефона. ");
        }

    }

    protected boolean checkBeanValueExist(Map<String, Object> bean, String valueSysName) {
        return checkBeanValueExist(bean, null, valueSysName, null, null);
    }

    protected boolean checkBeanValueExist(Map<String, Object> bean, String beanNote, String valueSysName, String valueName, StringBuffer errorText) {
        logger.debug("Checking for " + valueSysName + "...");

        if (bean.get(valueSysName) == null) {
            if (errorText != null) {
                // строка с причинами отказа в сохранении дополняется только если передана
                // (для возможности проверок без внесения в список причин отказа)
                errorText
                        .append(beanNote)
                        .append(" '")
                        .append(valueName)
                        //.append("' (")
                        //.append(valueSysName)
                        //.append("). ");
                        .append("'. ");
            }
            logger.debug(valueSysName + " is not found!");
            return false;
        }
        logger.debug(valueSysName + " is found");
        return true;
    }

    protected boolean checkContractExtValueExist(Map<String, Object> contractExtValues, String valueSysName, String valueName, StringBuffer errorText) {
        return checkBeanValueExist(contractExtValues, "В расширенных атрибутах договора не указано значение показателя", valueSysName, valueName, errorText);
    }

    protected boolean checkContractValueExist(Map<String, Object> contract, String valueSysName, String valueName, StringBuffer errorText) {
        return checkBeanValueExist(contract, "В договоре не указано значение атрибута", valueSysName, valueName, errorText);
    }

    // проверка наличия обязательных расширенных атрибутов договора
    // переопределить метод в конкретных фасадах, если требуется подобная проверка
    protected void validateContractExtValues(Map<String, Object> contract, Map<String, Object> contractExtValues, boolean isFixContr, StringBuffer errorText) {
        logger.debug("Contract extended values validation not implemented for this product.");
    }

    /**
     * Метод валидации расширенных аттрибутов на существование
     *
     * @param contractValidateParams - мапа подготовленных параметров на основании договора, который пришел
     * @param errorText              - строка-контейнер всех ошибоу валидации
     */
    private void validateContractExtValues(Map<String, Object> contractValidateParams, StringBuffer errorText) {
        logger.debug("Contract extended values validation not implemented for this product.");
        if (errorText == null) {
            errorText = new StringBuffer();
        }

        if (!getBooleanParam(contractValidateParams.get(IS_CONTRACT_EXT_VALUES_EXIST), false)) {
            errorText.append("Не указаны расширенные атрибуты договора. ");
        }
    }

    protected Long getLongProdDefValueFromDefValuesMap(Map<String, Object> defValuesMap, String defValueName, Long defaultValue) {
        Long defValValue = getLongProdDefValueFromDefValuesMap(defValuesMap, defValueName);
        if (defValValue == null) {
            defValValue = defaultValue;
        }
        return defValValue;
    }

    protected Long getLongProdDefValueFromDefValuesMap(Map<String, Object> defValuesMap, String defValueName) {
        Map<String, Object> defValMap = (Map<String, Object>) defValuesMap.get(defValueName);
        logger.debug("NAME = " + defValueName);
        Long defValValue = getLongParamLogged(defValMap, "VALUE");
        return defValValue;
    }

    protected Double getDoubleProdDefValueFromDefValuesMap(Map<String, Object> defValuesMap, String defValueName) {
        Map<String, Object> defValMap = (Map<String, Object>) defValuesMap.get(defValueName);
        logger.debug("NAME = " + defValueName);
        Double defValValue = getDoubleParamLogged(defValMap, "VALUE");
        return defValValue;
    }

    protected String getStringProdDefValueFromDefValuesMap(Map<String, Object> defValuesMap, String defValueName, String defaultValue) {
        String defValValue = getStringProdDefValueFromDefValuesMap(defValuesMap, defValueName);
        if ((defValValue == null) || (defValValue.isEmpty())) {
            defValValue = defaultValue;
        }
        return defValValue;
    }

    protected String getStringProdDefValueFromDefValuesMap(Map<String, Object> defValuesMap, String defValueName) {
        Map<String, Object> defValMap = (Map<String, Object>) defValuesMap.get(defValueName);
        logger.debug("NAME = " + defValueName);
        String defValValue = getStringParamLogged(defValMap, "VALUE");
        return defValValue;
    }

    // дополнительная проверка договора (особая для конкретного продукта)
    // переопределить метод в конкретных фасадах, если требуется подобная проверка
    @Deprecated
    protected void validateAdditionalContractSaveParams(Map<String, Object> contract, StringBuffer errorText, String login, String password) throws Exception {
        logger.debug("Additional contract validation not implemented for this product.");
    }

    // дополнительная проверка договора с собранными параметрами (особая для конкретного продукта
    // исключающая повторного запроса информаций о продуктах и прочих)
    // переопределить метод в конкретных фасадах, если требуется подобная проверка
    protected void validateAdditionalContractSaveParams(Map<String, Object> contract, Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) throws Exception {
        logger.debug("Additional contract validation not implemented for this product with contractValidateParams.");
        if (contractValidateParams == null) {
            validateAdditionalContractSaveParams(contract, errorText, login, password);
        }
    }

    // проверка наличия премии по договору
    // переопределить метод в конкретных фасадах, если не требуется подобная проверка или она отличется от стандартной
    protected boolean checkContractPremValue(Map<String, Object> contract, boolean isPreCalcCheck, StringBuffer errorText) {
        boolean isPremValueExists = checkContractValueExist(contract, "PREMVALUE", "Страховой взнос", errorText);
        return isPremValueExists;
    }

    // проверка наличия премии по договору
    // переопределить метод в конкретных фасадах, если не требуется подобная проверка или она отличется от стандартной
    protected boolean checkContractPremValue(Map<String, Object> contract, StringBuffer errorText) {
        boolean isPreCalcCheck = false; // по умолчанию считается что проверка выполняется не перед расчетом
        return checkContractPremValue(contract, isPreCalcCheck, errorText);
    }

    protected boolean validateContractSaveParams(Map<String, Object> contract, boolean isFixContr, String login, String password) throws Exception {
        boolean isPreCalcCheck = false; // по умолчанию - штатная проверка, не перед вызовом калькулятора
        return validateContractSaveParams(contract, isFixContr, isPreCalcCheck, login, password);
    }

    /**
     * Метод получения стратегии инвестирования согласно данным договора.
     *
     * @param contract - мапа договора
     * @param login    - логин
     * @param password - пароль
     * @return если для продуктового (нашего) договора нет такой стратегии, то возвращаем NULL,
     * а также возвращаем NULL, когда нашего договора тоже нет (т.е. он тоже NULL)
     */
    private Map<String, Object> getInvestmentStrategyByContract(Map<String, Object> contract, String login, String password) throws Exception {

        logger.debug("Start getInvestmentStrategyByContractInfo with contract = " + contract);
        Map<String, Object> investStrategy = new HashMap<>();

        if (contract == null) {
            return null;
        }

        // следует определить, требуется ли (и возомжна ли) эта проверка для данного продукта
        Long fundForInvestmentStrategyCheck = getFundForInvestmentStrategyCheck(contract, login, password);
        if (fundForInvestmentStrategyCheck != null) {
            // согласно настройкам продукта проверка стратегии инвестирования требуется
            String productSysName = getStringParamLogged(contract, "PRODSYSNAME");
            // получение срока действия договора (#11298)
            Long termId = getLongParamLogged(contract, "TERMID");
            if (productSysName.isEmpty()) {
                logger.debug("Not found sysname of product for select found for contract = " + contract);
            } else if (termId == null) {
                logger.debug("Not found term of product for select found for contract = " + contract);
            } else {
                // выбор даты для проверок сроков действия
                Date checkDate = null;
                Object signDateObj = contract.get("SIGNDATE");
                logger.debug("SIGNDATE = " + signDateObj);
                if (signDateObj != null) {
                    checkDate = (Date) parseAnyDate(signDateObj, Date.class, "SIGNDATE", logger.isDebugEnabled());
                }
                if (checkDate == null) {
                    checkDate = new Date();
                    logger.debug("Sign date is not specified! Using current system date: " + checkDate);
                }

                List<Map<String, Object>> productStrategyList = getAvailableProductStrategyForSale(fundForInvestmentStrategyCheck, productSysName, checkDate, termId, login, password);
                if ((productStrategyList != null) && (productStrategyList.size() == 1)) {
                    investStrategy = productStrategyList.get(0);

                    Long prodInvestId = getLongParamLogged(investStrategy, "PRODINVESTID");
                    if (fundForInvestmentStrategyCheck.equals(prodInvestId)) {
                        logger.debug("Investment strategy successfully checked. Confirmed strategy info: " + investStrategy);
                        return investStrategy;
                    }

                    // Используется для логирования !!!
                    productStrategyList = getAvailableProductStrategyForSale(productSysName, checkDate, login, password);
                    Long contractId = getLongParam(contract, "CONTRID");
                    logger.error(String.format(
                            "Selected for contract (with id = %d) with product system name = '%s' investment strategy (fund = %d) failed availability check on date = %s! Available strategies is: %s.",
                            contractId, productSysName, fundForInvestmentStrategyCheck, checkDate, productStrategyList
                    ));
                }
            }
        }

        logger.debug("getInvestmentStrategyByContractInfo finished.");

        return investStrategy;
    }

    /**
     * Кастомный метод проверки даты начала договора
     *
     * @param contract
     * @param errorText
     * @return
     */
    protected boolean checkContractStartDate(Map<String, Object> contract, StringBuffer errorText) {
        boolean isPremValueExists = checkContractValueExist(contract, "PREMVALUE", "Страховой взнос", errorText);
        return isPremValueExists;
    }

    /**
     * Всевозможные проверки данных контракта
     *
     * @param contract
     * @param isFixContr
     * @param isPreCalcCheck - проверка перед вызовом калькулятора ?
     * @param login
     * @param password
     * @return
     * @throws Exception
     */
    protected boolean validateContractSaveParams(Map<String, Object> contract, boolean isFixContr,
                                                 boolean isPreCalcCheck, String login, String password) {

        /*
        // вычисление обязательности валидации перенесено в метод isValidationRequired
        // выполнен ли вызов сохранения из интерфейса
        boolean isCallFromGate = isCallFromGate(contract);
        // если передан соответствующий флаг и вызов не из интерфейса, то валидация обязательна
        boolean isForcedValidation = (!isCallFromGate) && getBooleanParamLogged(contract, IS_FORCED_VALIDATION_PARAMNAME, false);
        Long contractId = getLongParamLogged(contract, "CONTRID");
        if ((contractId == null) && (!isForcedValidation)) {
        */
        boolean isValidationRequired = isValidationRequired(contract);
        if (!isValidationRequired) {
            // если нет ИД договора - создается заготовка договора, в которой заведомо отсутствуют обязательные данные
            // (вызов сохранения при выборе продукта на странице создания нового договора, еще до перехода на первую страницу договора)
            // такую заготовку следует считать корректной, иначе невозможно будет её сохранить без ввода доп. флагов
            // исключение - если передан соответствующий флаг и вызов не из интерфейса (в этом случае валидация обязательна)
            logger.debug("Creating new contract empty template - contract data validation skipped.");
            return true;
        }

        logger.debug("Validate contract save params...");
        logger.debug("Main insured (insured adult) birth date contract extended (CONTREXTMAP) value field name: " + getInsuredAdultBirthDateFieldName());

        // Собираем ошибки сюда
        StringBuffer errorText = new StringBuffer();
        // Общие параметры для проверок
        // (параметры необходимы для последующих проверок - validateContractBeforeCalc или validateContractBeforeSave)
        Map<String, Object> contractValidateParams = prepareCommonValidateParams(contract, errorText, isPreCalcCheck, login, password);

        boolean isValidateCommon = false;

        try {
            // Общая проверка (перед расчетом или сохранением)
            isValidateCommon = validateContractCommon(contract, contractValidateParams, errorText, isFixContr, login, password);

            if (isPreCalcCheck) {
                // Подготовим параметры валидации, которые проверяются только перед расчетом в каждом продукте
                prepareBeforeCalcValidateParams(contract, contractValidateParams, errorText);
                validateContractBeforeCalc(contract, contractValidateParams, errorText, isFixContr, login, password);
        } else {
                prepareBeforeSaveValidateParams(contract, contractValidateParams, errorText);
                validateContractBeforeSave(contract, contractValidateParams, errorText, isFixContr, login, password);
        }

            // Требуется ли анддерайтинг
            underwritingCheck(contract, login, password);

            // дополнительная проверка договора (особая для конкретного продукта)
            // метод будет переопределен в конкретных фасадах, если потребуется подобная проверка
            // (появилась другая версия такой функции эта оставлена для поддержки продуктов,
            //  которые используют старую версию этой функции)
            validateAdditionalContractSaveParams(contract, errorText, login, password);
            validateAdditionalContractSaveParams(contract, contractValidateParams, errorText, login, password);
        } catch (Exception e) {
            logger.error(String.format("Validation threw an exception %s", e));
        }

        boolean isDataValid = (isValidateCommon && errorText.length() == 0);
        if (!isDataValid) {
            errorText.append("Сведения договора не сохранены.");
            contract.put("Status", "Error");
            contract.put("Error", errorText.toString());
        }
        logger.debug("Validate contract save params finished.");

        return isDataValid;
    }

    /**
     * Подготовка параметров, которые относятся к договору ключи параметров
     * хранятся в ${@link ContractLifeValidate}
     *
     * @param contract       - мапа договора
     * @param errorText
     * @param isPreCalcCheck - флаг, который указывает, в какой момент
     *                       выполняется подгротовка параметров (нужна для усложненной логики)
     * @param login          - логин
     * @param password       - пароль
     * @return если договора нет, то возвращаем null, в противном случае
     * вернем маппу (она может быть и просто пустой)
     */
    protected Map<String, Object> prepareCommonValidateParams(Map<String, Object> contract, StringBuffer errorText,
                                                              boolean isPreCalcCheck, String login, String password) {

        if (contract == null) {
            return null;
        }

        Map<String, Object> contractValidateParams = new HashMap<>();
        try {

            contractValidateParams.put(IS_PRE_CALC_CHECK, isPreCalcCheck);

            contractValidateParams.put(CONTR_MAP, contract);

            // информация о продукте
            contractValidateParams.put(PRODUCT_MAP, getFullProductDataFromContract(contract, login, password));

        // формирование мапы PRODDEFVAL-ов из продукта
            contractValidateParams.put(PRODDEFVAL_MAP,
                    getProdDefValsMapFromProduct((Map<String, Object>) contractValidateParams.get(PRODUCT_MAP)));

            // получим расширенные аттрибуты договора
            contractValidateParams.put(CONTREXT_MAP, getContrExtMap(contract));

            // Установим флаг существования расширенных аттрибутов
            contractValidateParams.put(IS_CONTRACT_EXT_VALUES_EXIST,
                    !getStringParam(contractValidateParams.get(CONTREXT_MAP)).isEmpty());

            // Получим стратегию инвестирования
            contractValidateParams.put(INVEST_STRATEGY_MAP, getInvestmentStrategyByContract(contract, login, password));

            // Стратегия инвестирования известна ?
            contractValidateParams.put(IS_INVEST_STRATEGY_MAP_EXIST,
                    !getStringParam(contractValidateParams.get(INVEST_STRATEGY_MAP)).isEmpty());

            // Вернет null если проверка не требуется (или невозомжна), или ИД фонда, если проверка требуется и возможна
            contractValidateParams.put(FUND_FOR_INVESTMENT_STRATEGY_CHECK,
                    getFundForInvestmentStrategyCheck(contract, login, password));

            // Дата рождения застрахованного
            contractValidateParams.put(INSURED_BIRTH_DATE, getInsuredAdultBirthDate(contract));

            // Страховая сумма
            contractValidateParams.put(INSAMVALUE, contract.get("INSAMVALUE"));

            // Дата рождения застрахованного существует?
            contractValidateParams.put(IS_INSURED_BIRTH_DATE_EXIST,
                    !getStringParam(contractValidateParams.get(INSURED_BIRTH_DATE)).isEmpty());

            // Флаги для дальнейших проверок
            contractValidateParams.put(IS_CURRENCY_ID_EXISTS,
                    checkContractValueExist(contract, "INSAMCURRENCYID", "ИД валюты страхования", errorText));

            contractValidateParams.put(IS_START_DATE_EXISTS,
                    checkContractValueExist(contract, "STARTDATE", "Дата начала действия договора", errorText));

            if (getBooleanParam(contractValidateParams.get(IS_START_DATE_EXISTS), false)) {

                final Date startDate = getDateParam(contract.get("STARTDATE"));

                contractValidateParams.put(CONTRACT_START_DATE, startDate);

                // TODO ПРОВЕРКА ВТОРОГО ПУНКТА НЕ ЦЕЛЕСООБРАЗНА, Т.К. МЫ ДОЛЖНЫ БУДЕМ ПРОВЕРИТЬ ПАРАМЕТР, КОТОРЫЙ МЫ САМИ СЧИТАЕМ 23062 WTF?
//
////                // подготовим нормальную дату окончания договора, которая зависит от даты начала (т.к. дата)
////                // окончания считается непосредственно только перед сохранением... Это означает, что перед расче-
////                // том она может быть неверной или отсутствовать совсем
//                getFinishDateByTermYearAndTermMonth(contract, login, password);
////
//                final Date finishDate = getDateParam(contract.get("FINISHDATE"));
////
//                // Если удалось посчитать, то установим
//                if (finishDate != null) {
//                    contract.put(CONTRACT_FINISH_DATE, finishDate);
//                }
            }

            contractValidateParams.put(IS_TERM_ID_EXISTS,
                    checkContractValueExist(contract, "TERMID", "ИД срока страхования", errorText));
            contractValidateParams.put(IS_PAY_VAR_ID_EXISTS,
                    checkContractValueExist(contract, "PAYVARID", "ИД периодичности оплаты", errorText));
            contractValidateParams.put(IS_PROD_PROG_ID_EXISTS,
                    checkContractValueExist(contract, "PRODPROGID", "ИД страховой программы", errorText));

            // Получение и подготовка сведений о выбранной валюте (тут тоже обновляется contractValidateParams)
            // Если проблему с установкой параметров, касаемо валют, то вам сюда
            receivingInformationAboutTheSelectedCurrency(contract, contractValidateParams, login, password);

        } catch (Exception e) {
            logger.error("PrepareCommonValidateParams throw exception after prepare " +
                    "contractValidateParams ", e);
        }

        return contractValidateParams;
    }

    /**
     * Метод подготовки сведений о выбраной валюте для выбранного продукта
     *
     * @param contract               - мапа договора
     * @param contractValidateParams - параметры валидации договора
     * @param login                  - логин
     * @param password               - пароль
     * @throws Exception
     */
    private void receivingInformationAboutTheSelectedCurrency(Map<String, Object> contract, Map<String, Object> contractValidateParams, String login, String password) throws Exception {

        if (contract == null || contractValidateParams == null ||
                getStringParam(contractValidateParams.get(PRODUCT_MAP)).isEmpty()) {

            throw new Exception(String.format("It is not possible to obtain information about the currency, because there are " +
                    "unknown parameters contract is %s and contractValidateParams is ", contract, contractValidateParams));
        }

        // Работаем с маппой продукта
        final Map<String, Object> product = getMapParam(contractValidateParams, PRODUCT_MAP);

        // получение сведений о выбранной валюте
        boolean isSupportedCurrencyFound = false;
        String currencyAlphaCode = "";
        if (getBooleanParam(contractValidateParams.get(IS_CURRENCY_ID_EXISTS), false)) {
            Long insAmCurrencyID = getLongParamLogged(contract, "INSAMCURRENCYID");
            // проверка на допустимую для данного продукта валюту
            boolean isCurrencyAvailableForProduct = false;
            List<Map<String, Object>> prodInsAmCursList = (List<Map<String, Object>>) product.get("PRODINSAMCURS");
            logger.debug("PRODINSAMCURS: " + prodInsAmCursList);
            if (prodInsAmCursList != null) {
                for (Map<String, Object> bean : prodInsAmCursList) {
                    Map<String, Object> currencyMap = (Map<String, Object>) bean.get("CURRENCY");
                    Long prodInfoCurrencyID = getLongParam(currencyMap, "CURRENCYID");
                    if (prodInfoCurrencyID != null) {
                        if (prodInfoCurrencyID.equals(insAmCurrencyID)) {
                            isCurrencyAvailableForProduct = true;
                        }
                    }
                }
            } else {
                logger.error(String.format("Could not determine the list of currencies allowed for this product = " + product));
            }
            if (isCurrencyAvailableForProduct) {

                // получение кода валюты
                Map<String, Object> currencyParams = new HashMap<String, Object>();
                currencyParams.put("CURRENCYID", insAmCurrencyID);
                currencyParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> currencyInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductRefCurrencyBrowseListByParam", currencyParams, login, password);
                getStringParamLogged(currencyInfo, "CURRENCYNAME"); // для протокола
                currencyAlphaCode = getStringParamLogged(currencyInfo, "ALPHACODE");

                // проверка на поддерживаемый код валюты
                if (currencyAlphaCode.equals("RUB") || currencyAlphaCode.equals("USD")) {
                    isSupportedCurrencyFound = true;
                    contract.put("INSAMCURRENCYALPHACODE", currencyAlphaCode);

                    // Для дальнейшего использования запомним нужную нам валюту
                    contractValidateParams.put(INSAMCURRENCY_ALPHA_CODE, currencyAlphaCode);
                } else {
                    throw new Exception("A currency is specified for which it is not possible to determine additional " +
                            "information or this currency is not supported.");

                }
            } else {
                throw new Exception("The specified currency is invalid for this product.");
            }
        }

        contractValidateParams.put(IS_SUPPORTED_CURRENCY_FOUND, isSupportedCurrencyFound);
        }

    @Override
    public void validateContractStartDateBeforeCurrentDateFromContractValidateParams(
            Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        logger.debug("validateContractStartDateBeforeCurrentDateFromContractValidateParams not implemented for this product.");
    }

    @Override
    public void validateContractEndDateForTheFirstPeriodFromContractValidateParams(
            Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        logger.debug("validateContractEndDateForTheFirstPeriodFromContractValidateParams not implemented for this product.");
    }

    @Override
    public void validateInsurerBirthDateFromContractValidateParams(
            Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        logger.debug("validateInsurerBirthDateFromContractValidateParams not implemented for this product.");
    }

    @Override
    public void validateContractEndDateFromContractValidateParams(
            Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        logger.debug("validateContractEndDateFromContractValidateParams not implemented for this product.");
                }

    @Override
    public void validateDateOfConsentToTheProcessingOfPersonalDataFromContractValidateParams(
            Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        logger.debug("validateDateOfConsentToTheProcessingOfPersonalDataFromContractValidateParams not implemented for this product.");
            }

    @Override
    public void validateDateOfSigningTheInsuranceContractFromContractValidateParams(
            Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        logger.debug("validateDateOfSigningTheInsuranceContractFromContractValidateParams not implemented for this product.");
                }

    @Override
    public void validateDateOfIssueOfThePassportOfTheInsuredFromContractValidateParams(
            Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        logger.debug("validateDateOfIssueOfThePassportOfTheInsuredFromContractValidateParams not implemented for this product.");
            }

    @Override
    public void validateInsamvalueFromContractValidateParams(
            Map<String, Object> contractValidateParams, StringBuffer errorText, Long insAmValueMaxDefault, String login, String password) {
        logger.debug("validateInsamvalueFromContractValidateParams not implemented for this product.");
                }

    @Override
    public void validateRateOfCreditFromContractValidateParams(
            Map<String, Object> contractValidateParams, StringBuffer errorText, Long minRateDefault, Long maxRateDefault, String login, String password) {
        logger.debug("validateRateOfCreditFromContractValidateParams not implemented for this product.");
            }

    @Override
    public void validateCalculatedPremiumForTheFirstPeriodWithAPremiumFromContractValidateParams(
            Map<String, Object> contractValidateParams, StringBuffer errorText, Long minRateDefault, Long maxRateDefault, String login, String password) {
        logger.debug("validateCalculatedPremiumForTheFirstPeriodWithAPremiumFromContractValidateParams not implemented for this product.");
                    }

    @Override
    public void validateLoanTermsFromContractValidateParams(Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) {
        logger.debug("validateLoanTermsFromContractValidateParams not implemented for this product.");
                    }

    /**
     * Функция валидации данных, которые необходимо выполнить перед расчетом
     *
     * @param contractValidateParams - общие параметры договора
     * @param contract               - мапа договора
     * @param errorText              - контейнер, который собирает в себя все невалидные поля (сообщения в случае не валидности)
     * @param isFixContr             - допускать исправление ?
     * @param login                  - логин
     * @param password               - пароль
     * @return
     */
    protected void validateContractBeforeCalc(Map<String, Object> contract,
                                              Map<String, Object> contractValidateParams, StringBuffer errorText,
                                              boolean isFixContr, String login, String password) {
        logger.debug("validateContractBeforeCalc not implemented for this product.");
        // Метод валидации даты начала договора
        validateContractStartDateBeforeCurrentDateFromContractValidateParams(contractValidateParams, errorText, login, password);
        // Метод валидации даты окончания полиса за первый год
        validateContractEndDateForTheFirstPeriodFromContractValidateParams(contractValidateParams, errorText, login, password);
        // Метод валидации возраста страхователя
        validateInsurerBirthDateFromContractValidateParams(contractValidateParams, errorText, login, password);
        // Метод валидации даты окончания полиса
        validateContractEndDateFromContractValidateParams(contractValidateParams, errorText, login, password);
        // Метод валидации даты согласия на обработку персональных данных
        validateDateOfConsentToTheProcessingOfPersonalDataFromContractValidateParams(contractValidateParams, errorText, login, password);
        // Метод валидации даты согласия на обработку персональных данных
        validateDateOfSigningTheInsuranceContractFromContractValidateParams(contractValidateParams, errorText, login, password);
        // Метод валидации даты выдачи паспорта страхователя
        validateDateOfIssueOfThePassportOfTheInsuredFromContractValidateParams(contractValidateParams, errorText, login, password);
        // Метод валидации страховой суммы
        validateInsamvalueFromContractValidateParams(contractValidateParams, errorText, null, login, password);
        // Метод валидации ставки кредита в %
        validateRateOfCreditFromContractValidateParams(contractValidateParams, errorText, null, null, login, password);
        // Валидация расчитанной премии за первый период с премией
        validateCalculatedPremiumForTheFirstPeriodWithAPremiumFromContractValidateParams(contractValidateParams,
                errorText, null, null, login, password);
        // Валидация срока кредитования
        validateLoanTermsFromContractValidateParams(contractValidateParams, errorText, login, password);
                }

    /**
     * Функция валидации данных, которые необходимо выполнить перед сохранением договора
     *
     * @param contract               - мапа договора
     * @param contractValidateParams - общие параметры договора
     * @param errorText              - контейнер, который собирает в себя все невалидные поля (сообщения в случае не валидности)
     * @param isFixContr             - допускать исправление ?
     * @param login                  - логин
     * @param password               - пароль
     */
    protected void validateContractBeforeSave(Map<String, Object> contract, Map<String, Object> contractValidateParams, StringBuffer errorText, boolean isFixContr, String login, String password) {

//        // Провалидируем расширенные аттрибуты (на существование )
//        validateContractExtValues(contractValidateParams, errorText);

        // проверка 'Минимальная страховая сумма по основной и любой дополнительной программе не может быть менее Х рублей/долларов'
        // данная проверка не выполняется перед вызовом расчета, поскольку проверяемые суммы только еще будут вычислены (или же перевычислены) по завершению текущей проверки
        if (getBooleanParam(contractValidateParams.get(IS_SUPPORTED_CURRENCY_FOUND), false)) {

            final String currencyAlphaCode = getStringParam(contractValidateParams.get(INSAMCURRENCY_ALPHA_CODE));

            String anyInsAmValueMinDefValName = "REJECT_ANY_INSAMVALUE_" + currencyAlphaCode + "_MIN";
            Double anyInsAmValueMin = getDoubleProdDefValueFromDefValuesMap(getMapParam(contractValidateParams, PRODDEFVAL_MAP), anyInsAmValueMinDefValName);

            if (anyInsAmValueMin > 0) {

                ArrayList<Map<String, Object>> allRisksList = getAllRisksListFromContract(contract);

                if (allRisksList.size() > 0) {
                    boolean isLowInsAmVAlueRisksExists = false;
                    for (Map<String, Object> risk : allRisksList) {
                        getStringParamLogged(risk, "PRODRISKSYSNAME");
                        Double riskInsAmValue = getDoubleParamLogged(risk, "INSAMVALUE");
                        if (riskInsAmValue < anyInsAmValueMin) {
                            isLowInsAmVAlueRisksExists = true;
                        }
                    }
                    if (isLowInsAmVAlueRisksExists) {
                        // todo: если нужен перечень названий рисков - требуется дополнительно получить их из сведенй продукта по системным наименованиям рисков (PRODRISKSYSNAME)
                        errorText.append("По основным и/или дополнительным программам имеются страховые суммы менее установленного для данного продукта минимума. ");
                    }
                }

            }
        }
    }

    /**
     * Метод подготовки параметров, которые необходимо выпонять перед расчетом
     *
     * @param contract               - мапа договора
     * @param contractValidateParams - мапа параметров, куда следует складывать таккие параметры
     * @param errorText              - сборщик ошибок
     */
    private void prepareBeforeCalcValidateParams(Map<String, Object> contract, Map<String, Object> contractValidateParams, StringBuffer errorText) {
        if (contract == null) {
            return;
        }

        if (contractValidateParams == null) {
            contractValidateParams = new HashMap<>();
        }

        final boolean isBeforeCalc = true;
        contractValidateParams.put(IS_PREM_VALUE_EXISTS, checkContractPremValue(contract, isBeforeCalc, errorText));
    }

    /**
     * Метод подготовки параметров, которые необходимо выпонять перед сохранением
     *
     * @param contract               - мапа договора
     * @param contractValidateParams - мапа параметров, куда следует складывать таккие параметры
     * @param errorText              - сборщик ошибок
     */
    private void prepareBeforeSaveValidateParams(Map<String, Object> contract, Map<String, Object> contractValidateParams, StringBuffer errorText) {
        logger.debug("prepareBeforeSaveValidateParams not implemented for this product.");
    }

    /**
     * Функция валидации размера взноса в зависимости от периодичности оплаты и валюты
     * <p>
     * "Минимальный размер взноса в зависимости от периодичности оплаты и валюты"
     * <p>
     * данная проверка выполняется перед вызовом расчета условно - только если выбрана программа,
     * при которой взнос введен пользователем и является исходным параметром для расчета
     * в остальных случаях данная проверка выполняется безусловно (внимание!!! сложная проверка,
     * может содержать вложенную валидацию)
     *
     * @param contract               - мапа договора
     * @param contractValidateParams - параметры валидации
     * @param errorText              - сборщих ошибок валидации
     * @param login                  - логин
     * @param password               - пароль
     * @throws Exception
     */
    private void validateMinimumFeeDependingOnTheFrequencyOfPaymentAndCurrency(Map<String, Object> contract, Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) throws Exception {

        if (getBooleanParam(contractValidateParams.get(IS_SUPPORTED_CURRENCY_FOUND), false)
                && getBooleanParam(contractValidateParams.get(IS_PAY_VAR_ID_EXISTS), false)) {

            // является ли размер взноса для выбранной программы вводимым пользователем и, соответственно, исходным параметром для расчета
            Boolean isContrPremValueEnteredByUser = null;

            if (getBooleanParam(contractValidateParams.get(IS_PROD_PROG_ID_EXISTS), false)
                    && getBooleanParam(contractValidateParams.get(IS_PRE_CALC_CHECK), false)) {

                // если проверка выполняется перед вызовом расчета - требуется определить выбрана ли программа, при которой взнос введен пользователем и является исходным параметром для расчета
                Map<String, Object> programParams = new HashMap<String, Object>();
                programParams.put("PRODPROGID", getLongParamLogged(contract, "PRODPROGID"));
                programParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> programInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductProgramBrowseListByParam", programParams, login, password);
                getStringParamLogged(programInfo, "NAME"); // для протокола
                String programNote = getStringParamLogged(programInfo, "NOTE");
                String programSysName = getStringParamLogged(programInfo, "SYSNAME");
                if (programSysName.isEmpty() && programNote.isEmpty()) {
                    errorText.append("Указана страховая программа, для которой невозможно определить является ли размер страхового взноса исходным параметром для расчета. ");
                } else if (programSysName.contains("_BASIC") || programNote.contains("исходя из размера страхового взноса")) {
                    // размер взноса является для выбранной программы вводимым пользователем и, соответственно, исходным параметром для расчета
                    isContrPremValueEnteredByUser = true;
                } else if (programSysName.contains("_ADDITIONAL") || programNote.contains("исходя из размера страховых сумм")) {
                    // размер взноса является для выбранной программы рассчетным и, соответственно, результатом расчета
                    isContrPremValueEnteredByUser = false;
                } else {
                    errorText.append("Указана программа, для которой невозможно определить является ли размер страхового взноса исходным параметром для расчета. ");
                }
            }


            // проверка выполняется перед вызовом расчета условно - только если выбрана программа, при которой взнос введен пользователем и является исходным параметром для расчета
            // во всех остальных случаях данная проверка выполняется безусловно
            if ((!getBooleanParam(contractValidateParams.get(IS_PRE_CALC_CHECK), false))
                    || ((isContrPremValueEnteredByUser != null) && (isContrPremValueEnteredByUser))) {

                final String currencyAlphaCode = getStringParam(contractValidateParams.get(INSAMCURRENCY_ALPHA_CODE));
                final Map<String, Object> prodDefValsMap = (Map<String, Object>) contractValidateParams.get(PRODDEFVAL_MAP);

                Long payVarID = getLongParamLogged(contract, "PAYVARID");
                Map<String, Object> payVarData = getPayVarDataByPayVarID(payVarID, login, password);
                String payVarSysName = getStringParamLogged(payVarData, "SYSNAME");
                if ((!payVarSysName.isEmpty()) && (!currencyAlphaCode.isEmpty())) {
                    String premValueMinDefValName = String.format("REJECT_PREMVALUE_%s_%s_MIN", payVarSysName, currencyAlphaCode);
                    Double premValueMin = getDoubleProdDefValueFromDefValuesMap(prodDefValsMap, premValueMinDefValName);
                    Double contrPremValue;

                    final Map<String, Object> contractExtValues = (Map<String, Object>) contractValidateParams.get(CONTREXT_MAP);

                    boolean isPremValNoRiskExists = checkBeanValueExist(contractExtValues, "premValNoRisk");
                    String premName;
                    if (getBooleanParam(contractValidateParams.get(IS_PRE_CALC_CHECK), false)
                            && isPremValNoRiskExists) {
                        // если проверка выполняется перед вызовом расчета, то проверять нужно введенную пользователем исходную сумму (подробнее - см. http://rybinsk.bivgroup.com:9090/issues/3180#note-3)
                        contrPremValue = getDoubleParamLogged(contractExtValues, "premValNoRisk");
                        premName = "Введенная сумма взноса по договору";
                    } else {
                        // во всех остальных случаях - проверяем итоговую сумму взноса по договору (включая суммы по рискам)
                        contrPremValue = getDoubleParamLogged(contract, "PREMVALUE");
                        premName = "Текущая итоговая сумма взноса по договору (включая суммы по рискам)";
                    }
                    if (contrPremValue < premValueMin) {
                        errorText.append(String.format("%s ниже минимального размера взноса, установленного для данного продукта в зависимости от периодичности оплаты и валюты. ", premName));
                    }
                }
            }

        }
    }

    /**
     * Метод общей проверки договора (которая выполняется как до расчета калькудятора, так и перед сохранением)
     *
     * @param contract               - мапа договора
     * @param contractValidateParams - общие параметры договора
     * @param errorText              - сообщение невалидных полей
     * @param isFixContr             - допускать исправление ?
     * @param login                  - логин
     * @param password               - пароль
     */
    private boolean validateContractCommon(Map<String, Object> contract, Map<String, Object> contractValidateParams,
                                           StringBuffer errorText, boolean isFixContr, String login, String password) throws Exception {

        if (contractValidateParams == null) {
            contractValidateParams = new HashMap<>();
        }

        // Оставлена поддержка старой формы валидации (конкретных параметров)
        if (getBooleanParam(contractValidateParams.get(IS_CONTRACT_EXT_VALUES_EXIST), false)) {
            // Оставлена старая поддержка валидации расширенных аттрибутов
            validateContractExtValues(contract, (Map<String, Object>) contractValidateParams.get(CONTREXT_MAP),
                    isFixContr, errorText);
            //проверка выбранной стратегии инвестирования (старый функционал)
            validateInvestmentStrategy(contract, errorText, login, password);

            if (getBooleanParam(contractValidateParams.get(IS_INVEST_STRATEGY_MAP_EXIST), false)) {
                validateInvestmentStrategyByContractValidateParams(contract, contractValidateParams, errorText, login, password);
            }
        }

        // проверка на 'Минимальный размер взноса'
        if (getBooleanParam(contractValidateParams.get(IS_SUPPORTED_CURRENCY_FOUND), false)
                && getBooleanParam(contractValidateParams.get(IS_PREM_VALUE_EXISTS), false)) {
            // проверка на 'Минимальный размер взноса' (с учетом наличия проверенной и корректной стратегии инвестирования)
            validateMinimumPaymentValue(contract,
                    (Map<String, Object>) contractValidateParams.get(INVEST_STRATEGY_MAP), errorText, login, password);
        }

        // проверки на 'Допустимый возраст на начало срока страхования от/до Х полных лет включительно на дату начала срока страхования'
        validateAdmissibleAgeAtTheBeginningOfTheInsurancePeriod(contract, contractValidateParams, errorText,
                login, password);


        // проверка "Минимальный размер взноса в зависимости от периодичности оплаты и валюты"
        // данная проверка выполняется перед вызовом расчета условно - только если выбрана программа,
        // при которой взнос введен пользователем и является исходным параметром для расчета
        // в остальных случаях данная проверка выполняется безусловно (внимание!!! сложная проверка,
        // может содержать вложенную валидацию)
        validateMinimumFeeDependingOnTheFrequencyOfPaymentAndCurrency(contract, contractValidateParams,
                errorText, login, password);


        // Валидна, если нет никаких ошибок
        return errorText.length() == 0;
    }


    /**
     * Метод проверки на 'Допустимый возраст на начало срока страхования
     * от/до Х полных лет включительно на дату начала срока страхования'
     *
     * @param contract               - мапа договора
     * @param contractValidateParams - мапа подготовленных параметров
     * @param errorText              - сборщик сообщений невалидных аттрибутов
     * @param login                  - логин
     * @param password               - пароль
     */
    protected void validateAdmissibleAgeAtTheBeginningOfTheInsurancePeriod(Map<String, Object> contract, Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) throws Exception {

        if (getBooleanParam(contractValidateParams.get(IS_START_DATE_EXISTS), false)
                && getBooleanParam(contractValidateParams.get(IS_INSURED_BIRTH_DATE_EXIST), false)) {

            final Date startDate = (Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE", true);
            final Map<String, Object> prodDefValsMap = (Map<String, Object>) contractValidateParams.get(PRODDEFVAL_MAP);

            if (!getBooleanParam(contractValidateParams.get(IS_INSURED_BIRTH_DATE_EXIST), false)) {
                errorText.append("Не указан возраст застрахованного. ");
                return;
            }

            Date insuredBirthDate = getDateParam(contractValidateParams.get(INSURED_BIRTH_DATE));

            // проверка на 'Допустимый возраст на начало срока страхования до Х полных лет включительно на дату начала срока страхования'
            // (в том числе с учетом пола застрахованного, если в PRODDEFVAL-ах продукта имеется соответствующий параметр)
            Long insuredAgeOnContrStartMax = getLongProdDefValueFromDefValuesMap(getMapParam(contractValidateParams, PRODDEFVAL_MAP), "REJECT_INSUREDAGE_ONCONTRACTSTART_MAX");
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
                insuredBirthDatePlusMaxStartAgeGC.setTime(insuredBirthDate);
                insuredBirthDatePlusMaxStartAgeGC.add(Calendar.YEAR, insuredAgeOnContrStartMax.intValue());
                // проверка на возраст должна пропускать человека по ограниченный возраст включительно. вплоть до его др.
                insuredBirthDatePlusMaxStartAgeGC.add(Calendar.YEAR, 1);
                insuredBirthDatePlusMaxStartAgeGC.add(Calendar.DATE, -1);
                if (startDate.after(insuredBirthDatePlusMaxStartAgeGC.getTime())) {
                    errorText.append(String.format("Возраст застрахованного лица на начало срока страхования более допустимого%s. ", ageMsgEnding));
                }
            }

            Long insuredAgeOnContrStartMin = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "REJECT_INSUREDAGE_ONCONTRACTSTART_MIN");
            if (insuredAgeOnContrStartMin != null) {
                GregorianCalendar insuredBirthDatePlusMinStartAgeGC = new GregorianCalendar();
                insuredBirthDatePlusMinStartAgeGC.setTime(insuredBirthDate);
                insuredBirthDatePlusMinStartAgeGC.add(Calendar.YEAR, insuredAgeOnContrStartMin.intValue());
                if (startDate.before(insuredBirthDatePlusMinStartAgeGC.getTime())) {
                    errorText.append("Возраст застрахованного лица на начало срока страхования менее допустимого. ");
                }
            }

            if (getBooleanParam(contractValidateParams.get(IS_TERM_ID_EXISTS), false)) {

                // проверка 'Возраст застрахованного лица на дату окончания договора не должен превышать Х полных лет'
                Long insuredAgeOnContrFinishMax = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "REJECT_INSUREDAGE_ONCONTRACTFINISH_MAX");
                if (insuredAgeOnContrFinishMax != null) {

                    Long termID = getLongParamLogged(contract, "TERMID");
                    Map<String, Object> termInfo = getTermDataByTermID(termID, login, password);
                    getStringParamLogged(termInfo, "NAME"); // для протокола
                    Long termYearCount = getLongParamLogged(termInfo, "YEARCOUNT");
                    Long termMonthCount = getLongParamLogged(termInfo, "MONTHCOUNT");
                    Long termDayCount = getLongParamLogged(termInfo, "DAYCOUNT");

                    GregorianCalendar insuredBirthDatePlusMaxFinishAgeGC = new GregorianCalendar();
                    insuredBirthDatePlusMaxFinishAgeGC.setTime(insuredBirthDate);
                    insuredBirthDatePlusMaxFinishAgeGC.add(Calendar.YEAR, insuredAgeOnContrFinishMax.intValue());
                    // проверка на возраст должна пропускать человека по ограниченный возраст включительно. вплоть до его др.
                    insuredBirthDatePlusMaxFinishAgeGC.add(Calendar.YEAR, 1);
                    insuredBirthDatePlusMaxFinishAgeGC.add(Calendar.DATE, -1);

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

                    if (finishDateGC.after(insuredBirthDatePlusMaxFinishAgeGC)) {
                        errorText.append("Возраст застрахованного лица на конец срока страхования более допустимого. ");
                    }
                }
            }
        }
    }

    // формирование мапы рисков из списка (ключ - системное наименование, значение - мапа риска)
    protected Map<String, Map<String, Object>> getRisksMapBySysNameFromRiskList(List<Map<String, Object>> riskList) {
        Map<String, Map<String, Object>> risksMapBySysName = new HashMap<String, Map<String, Object>>();
        if (riskList != null) {
            for (Map<String, Object> risk : riskList) {
                String prodRiskSysName = getStringParamLogged(risk, "PRODRISKSYSNAME");
                risksMapBySysName.put(prodRiskSysName, risk);
            }
        }
        return risksMapBySysName;
    }

    protected void getFinishDateByStartDateAndTermId(Map<String, Object> contract, String login, String password) throws Exception {
        Long termID = getLongParamLogged(contract, "TERMID");
        Map<String, Object> termInfo = getTermDataByTermID(termID, login, password);
        getStringParamLogged(termInfo, "NAME"); // для протокола
        Long termYearCount = getLongParamLogged(termInfo, "YEARCOUNT");
        Long termMonthCount = getLongParamLogged(termInfo, "MONTHCOUNT");
        Long termDayCount = getLongParamLogged(termInfo, "DAYCOUNT");
        if (contract.get("STARTDATE") != null) {
            Date startDate = (Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE", true);

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
            contract.put("FINISHDATE", finishDateGC.getTime());
        }
    }

    protected void getFinishDateByTermYearAndTermMonth(Map<String, Object> contractValidateParams, Long termYear,
                                                       Long termMount) {
        final Map<String, Object> contract = (Map<String, Object>) contractValidateParams.get(CONTR_MAP);

        if (contract.get("STARTDATE") != null) {

            Date startDate = (Date) parseAnyDate(contract.get("STARTDATE"), Date.class, "STARTDATE", true);

            GregorianCalendar finishDateGC = new GregorianCalendar();
            finishDateGC.setTime(startDate);

            // TODO Нужно было максимально быстро реализовать данный функционал (в свободное время отрефакторить)
            // Если известны оба параметра, то сдвигаем на любое смещение
            if (termMount != null && termYear != null) {
                finishDateGC.add(Calendar.YEAR, termYear.intValue());
                contractValidateParams.put(CONTRACT_FINISH_DATE, finishDateGC.getTime());
                return;
            }

            if (termMount != null) {
                finishDateGC.add(Calendar.MONTH, termMount.intValue());
                contractValidateParams.put(CONTRACT_FINISH_DATE, finishDateGC.getTime());
                return;
            }

            if (termYear != null) {
                finishDateGC.add(Calendar.YEAR, termYear.intValue());
                contractValidateParams.put(CONTRACT_FINISH_DATE, finishDateGC.getTime());
                return;
            }
        }
    }

    // формирование списка актуальных (не удаляемых) рисков из мапы договора
    protected ArrayList<Map<String, Object>> getAllRisksListFromContract(Map<String, Object> contract) {
        logger.debug("Getting actual (non deleted) risks list from contract map...");
        ArrayList<Map<String, Object>> allRisksList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> insobjGroupList = (List<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        if (insobjGroupList != null) {
            for (Map<String, Object> insObjGroup : insobjGroupList) {
                List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroup.get("OBJLIST");
                if (objList != null) {
                    for (Map<String, Object> obj : objList) {
                        Map<String, Object> contrObjMap = (Map<String, Object>) obj.get("CONTROBJMAP");
                        if (contrObjMap != null) {
                            List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                            if (contrRiskList != null) {
                                for (Map<String, Object> risk : contrRiskList) {
                                    String prodRiskSysName = getStringParamLogged(risk, "PRODRISKSYSNAME");
                                    Long riskRowStatus = getLongParamLogged(risk, "ROWSTATUS");
                                    // пропускаем проверку рисков, отмеченных как удаляемые
                                    boolean isRiskMarkedAsDeleted = ((riskRowStatus != null) && (riskRowStatus.intValue() == DELETED_ID));
                                    // дополнительно требуется пропустить особый риск - 'Освобождение Страхователя от уплаты страховых взносов (ОУСВ)' (insPremExemption) - поскольку он никогда не имеет страховой суммы
                                    boolean isRiskSkipped = "insPremExemption".equals(prodRiskSysName) || "FCC_EXEMPTION_PAYMENT_DISAB_RB-FCC0".equals(prodRiskSysName);
                                    if (!isRiskMarkedAsDeleted && !isRiskSkipped) {
                                        // если риск не удаляемый и не пропускается - добавляем в результирующий список
                                        allRisksList.add(risk);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.debug("Getting actual (non deleted) risks list from contract map finished.");
        return allRisksList;
    }

    protected Map<String, Object> getTermDataByTermID(Long termID, String login, String password) throws Exception {
        // получение сведений о сроке страхования
        Map<String, Object> termParams = new HashMap<String, Object>();
        termParams.put("TERMID", termID);
        termParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> termInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BTermBrowseListByParam", termParams, login, password);
        return termInfo;
    }

    protected Map<String, Object> getPayVarDataByPayVarID(Long payVarID, String login, String password) throws Exception {
        // получение сведений о периодичности оплаты
        Map<String, Object> payVarParams = new HashMap<String, Object>();
        payVarParams.put("PAYVARID", payVarID);
        payVarParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> payVarInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentVariantBrowseListByParam", payVarParams, login, password);
        return payVarInfo;
    }

    // формирование мапы PRODDEFVAL-ов из продукта
    protected Map<String, Object> getProdDefValsMapFromProduct(Map<String, Object> product) {
        // получение мапы PRODDEFVAL-ов из продукта (если уже была сформирована ранее)
        Map<String, Object> prodDefValsMap = (Map<String, Object>) product.get("PRODDEFVALSMAP");
        if ((prodDefValsMap == null) || (prodDefValsMap.isEmpty())) {
            // получение списка PRODDEFVAL-ов из продукта
            ArrayList<Map<String, Object>> prodDefValsList = (ArrayList<Map<String, Object>>) product.get("PRODDEFVALS");
            // формирование мапы PRODDEFVAL-ов из продукта
            prodDefValsMap = new HashMap<String, Object>();
            for (Map<String, Object> prodDefVal : prodDefValsList) {
                String defValName = getStringParamLogged(prodDefVal, "NAME");
                prodDefValsMap.put(defValName, prodDefVal);
            }
            if (prodDefValsMap.size() > 0) {
                product.put("PRODDEFVALSMAP", prodDefValsMap);
            }
        }
        return prodDefValsMap;
    }

    // получение продукта по данным договора
    protected Map<String, Object> getFullProductDataFromContract(Map<String, Object> contract, String login, String password) throws Exception {
        // получение продукта
        Map<String, Object> product = (Map<String, Object>) contract.get("FULLPRODMAP");
        if ((product == null) || (product.isEmpty())) {
            Map<String, Object> productParams = new HashMap<String, Object>();
            productParams.put("PRODSYSNAME", getStringParamLogged(contract, "PRODSYSNAME"));
            productParams.put("LOADALLDATA", 1L); // для проверки нужен список валют договора, поэтому теперь требуется полная загрузка данных продукта
            product = (Map<String, Object>) this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductBrowseBySysName", productParams, login, password, "PRODMAP");
            contract.put("FULLPRODMAP", product);
        }
        return product;
    }

    // получает дату рождения основного застрахованного по договору (взрослого)
    // переопределить метод в конкретных фасадах, если дату рождения основного застрахованного по договору (взрослого) храниться не в расширенных атрибутах договора
    protected Date getInsuredAdultBirthDate(Map<String, Object> contract) {
        Date insuredBirthDate = null;
        Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (contractExtValues != null) {
            String insuredBirthDateExtValueFieldName = getInsuredAdultBirthDateFieldName();
            Object insuredBirthDateObj = contractExtValues.get(insuredBirthDateExtValueFieldName);
            if (insuredBirthDateObj != null) {
                insuredBirthDate = (Date) parseAnyDate(insuredBirthDateObj, Date.class, insuredBirthDateExtValueFieldName, true);
            }
        }
        return insuredBirthDate;
    }

    // возвращает имя ключа, указывающего на дату рождения основного застрахованного по договору (взрослого)
    // мапу в которой данный ключ нужно искать задается методом getInsuredAdultBirthDate
    // переопределить метод в конкретных фасадах, если имя ключа отличается от insuredBirthDATE
    protected String getInsuredAdultBirthDateFieldName() {
        return "insuredBirthDATE";
    }

    // получает системное наименование для пола, используемое для полозависимых параметров продукта в PRODDEFVAL-ах
    // переопределить метод в конкретных фасадах, если требуются иные системные наименования
    protected String getInsuredAdultGenderProdDefValSysName(Map<String, Object> contract) {
        String insuredAdultGenderSysName = "";
        Long insuredAdultGender = getInsuredAdultGender(contract);
        if (insuredAdultGender != null) {
            if (insuredAdultGender.equals(0L)) {
                insuredAdultGenderSysName = "MALE";
            } else if (insuredAdultGender.equals(1L)) {
                insuredAdultGenderSysName = "FEMALE";
            }
        }
        return insuredAdultGenderSysName;
    }

    // получает пол основного застрахованного по договору (взрослого)
    // переопределить метод в конкретных фасадах, если пол основного застрахованного по договору (взрослого) храниться не в расширенных атрибутах договора
    protected Long getInsuredAdultGender(Map<String, Object> contract) {
        Long insuredGender = null;
        Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (contractExtValues != null) {
            String insuredGenderExtValueFieldName = getInsuredAdultGenderFieldName();
            Object insuredGenderObj = contractExtValues.get(insuredGenderExtValueFieldName);
            if (insuredGenderObj != null) {
                insuredGender = getLongParamLogged(contractExtValues, insuredGenderExtValueFieldName);
            }
        }
        return insuredGender;
    }

    // возвращает имя ключа, указывающего на пол основного застрахованного по договору (взрослого)
    // мапу в которой данный ключ нужно искать задается методом getInsuredAdultGender
    // переопределить метод в конкретных фасадах, если имя ключа отличается от insuredGender
    protected String getInsuredAdultGenderFieldName() {
        return "insuredGender";
    }

    /**
     * Проверка андеррайтера
     *
     * @param contract
     * @param login
     * @param password
     * @return
     * @throws Exception
     */
    protected Long underwritingCheck(Map<String, Object> contract, String login, String password) throws Exception {

        logger.debug("Check contract underwriting...");

        // требуется ли андеррайтинг
        // 0L - нет, андеррайтинг не требуется
        // 1L - да, андеррайтинг требуется
        // 2L - недостаточно сведений, чтоб определить однозначно (например, не известна валюта и, как следствие, не проверить лимиты и пр.)
        Long UW = UW_DO_NOT_NEEDED;

        Map<String, Object> product = getFullProductDataFromContract(contract, login, password);
        // формирование мапы PRODDEFVAL-ов из продукта
        Map<String, Object> prodDefValsMap = getProdDefValsMapFromProduct(product);
        Long skipCheck = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "UW_SKIP_CHECK");
        if ((null != skipCheck) && (skipCheck > 0)) {
            logger.debug("UW = " + UW);
            contract.put("UW", UW);
            logger.debug("skip check contract underwriting.");
            logger.debug("Check contract underwriting finished.");
            return UW;
        }

        // получение сведений о выбранной валюте
        String currencyAlphaCode = getStringParamLogged(contract, "INSAMCURRENCYALPHACODE");
        if (currencyAlphaCode.isEmpty()) {
            Long insAmCurrencyID = getLongParamLogged(contract, "INSAMCURRENCYID");
            Map<String, Object> currencyParams = new HashMap<String, Object>();
            currencyParams.put("CURRENCYID", insAmCurrencyID);
            currencyParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> currencyInfo = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductRefCurrencyBrowseListByParam", currencyParams, login, password);
            getStringParamLogged(currencyInfo, "CURRENCYNAME"); // для протокола
            currencyAlphaCode = getStringParamLogged(currencyInfo, "ALPHACODE");
        }
        if (currencyAlphaCode.equals("RUB") || currencyAlphaCode.equals("USD")) {
            contract.put("INSAMCURRENCYALPHACODE", currencyAlphaCode);
        } else {
            // не указана валюта
            logger.error(String.format("No currency data was found for this contract - underwriting check is failed!"));
            UW = UW_UNKNOWN;
        }

        if ((UW.equals(UW_DO_NOT_NEEDED)) && (!currencyAlphaCode.isEmpty())) {
            // формирование списка актуальных (не удаляемых) рисков из мапы договора
            ArrayList<Map<String, Object>> allRisksList = getAllRisksListFromContract(contract);

            // получение лимитов из справочника
            String limitsHBName = getStringProdDefValueFromDefValuesMap(prodDefValsMap, "UW_LIMITS_HB_NAME");
            List<Map<String, Object>> limitsList = null;
            if (!limitsHBName.isEmpty()) {
                Map<String, Object> limitsHBParams = new HashMap<String, Object>();
                limitsHBParams.put("HANDBOOKNAME", limitsHBName);
                limitsHBParams.put(RETURN_LIST_ONLY, true);
                limitsList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BHandbookDataBrowseByHBName", limitsHBParams, login, password);

                // проверка 'Страховая сумма по Основной (Обязательной) и/или Дополнительной программе превышает максимальный установленный лимит'
                if ((limitsList != null) && (limitsList.size() > 0)) {

                    // формирование мапы рисков из списка (ключ - системное наименование, значение - мапа риска)
                    Map<String, Map<String, Object>> allRisksMapBySysName = getRisksMapBySysNameFromRiskList(allRisksList);

                    String insAmValueLimitHBFieldName = "insAmLimit" + currencyAlphaCode;

                    for (Map<String, Object> limit : limitsList) {
                        String riskSysName = getStringParamLogged(limit, "riskSysName");
                        Double insAmValueLimit = getDoubleParamLogged(limit, insAmValueLimitHBFieldName);
                        Map<String, Object> riskFromContract = allRisksMapBySysName.get(riskSysName);
                        if (riskFromContract != null) {
                            Double insAmValue = getDoubleParamLogged(riskFromContract, "INSAMVALUE");
                            if (insAmValue > insAmValueLimit) {
                                UW = UW_NEEDED;
                            }
                        }
                    }

                } else {
                    // указано имя справочника, но данных по лимитам не удалось получить
                    logger.error(String.format("No limits data was found in handbook by name '%s' - underwriting check is failed!", limitsHBName));
                    UW = UW_UNKNOWN;
                }

            } else {

                // проверка 'Размер страховой суммы по риску Х более Y денег'
                for (Map<String, Object> risk : allRisksList) {
                    String riskSysName = getStringParamLogged(risk, "PRODRISKSYSNAME");
                    String limitProdDefValName = "UW_" + riskSysName + "_INSAMVALUE_" + currencyAlphaCode + "_MAX";
                    Double limitProdDefVal = getDoubleProdDefValueFromDefValuesMap(prodDefValsMap, limitProdDefValName);
                    if (limitProdDefVal > 0) {
                        Double insAmValue = getDoubleParamLogged(risk, "INSAMVALUE");
                        if (insAmValue > limitProdDefVal) {
                            UW = UW_NEEDED;
                        }
                    }
                }

            }

        }

        Long skipAgesCheck = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "UW_SKIP_AGES_CHECK", 0L);
        if (UW.equals(UW_DO_NOT_NEEDED) && BOOLEAN_FLAG_LONG_VALUE_FALSE.equals(skipAgesCheck)) {
            // проверка 'Возраст Застрахованного лица 71-75 полных лет на дату начала срока страхования'
            Object startDateObj = contract.get("STARTDATE");
            logger.debug("STARTDATE = " + startDateObj);
            if (startDateObj != null) {
                Date startDate = (Date) parseAnyDate(startDateObj, Date.class, "STARTDATE", true);
                Date insuredBirthDate = getInsuredAdultBirthDate(contract);
                if (insuredBirthDate != null) {
                    // дата рождения + 71
                    GregorianCalendar insuredBirthDatePlusMinStartAgeGC = new GregorianCalendar();
                    insuredBirthDatePlusMinStartAgeGC.setTime(insuredBirthDate);
                    insuredBirthDatePlusMinStartAgeGC.add(Calendar.YEAR, 71);
                    // дата рождения + 75
                    GregorianCalendar insuredBirthDatePlusMaxStartAgeGC = new GregorianCalendar();
                    insuredBirthDatePlusMaxStartAgeGC.setTime(insuredBirthDate);
                    insuredBirthDatePlusMaxStartAgeGC.add(Calendar.YEAR, 75);
                    if ((startDate.after(insuredBirthDatePlusMinStartAgeGC.getTime())) && (startDate.before(insuredBirthDatePlusMaxStartAgeGC.getTime()))) {
                        UW = UW_NEEDED;
                    }
                } else {
                    // не найдена дата рождения застрахованного
                    logger.error(String.format("No insured birth date was found in this contract - underwriting check is failed!"));
                    UW = UW_UNKNOWN;
                }
            } else {
                // не найдена дата начала действия договора
                logger.error(String.format("No contract start date was found in this contract - underwriting check is failed!"));
                UW = UW_UNKNOWN;
            }
        }

        if (UW.equals(UW_DO_NOT_NEEDED)) {
            // проверка 'Индекс массы тела и/или показатели артериального давления Застрахованных лиц не соответствуют лимитам'
            Long bodyIndexCheck = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "UW_BODY_INDEX_CHECK");
            if ((bodyIndexCheck != null) && (bodyIndexCheck.intValue() == 1)) {
                // проверка показателей здоровья для всех застрахованных по договору + проверка 'Наличие положительных ответов на вопросы Декларации застрахованного лица' для всех застрахованных по договору
                UW = checkAllInsuredHealthForUW(contract);
            } else {
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
            }
        }

        if (UW.equals(UW_DO_NOT_NEEDED)) {
            // проверка 'Профессия застрахованного (не ребенка, если двое застрахованных) характеризуются высокой степенью риска (3 или 4 класс риска)'
            UW = checkWorkingInsuredProfession(contract, login, password);
        }

        if (UW.equals(UW_DO_NOT_NEEDED)) {
            // проверка 'Страхователь не является резидентом Российской Федерации'
            UW = checkAllInsuredResidentFlag(contract);
        }

        if (UW.equals(UW_DO_NOT_NEEDED)) {
            // проверка 'Застрахованное лицо не является гражданином Российской Федерации'
            Long bodyIndexCheck = getLongProdDefValueFromDefValuesMap(prodDefValsMap, "UW_BODY_INDEX_CHECK");
            if ((bodyIndexCheck != null) && (bodyIndexCheck.intValue() == 1)) {
                UW = checkAllInsuredCitizenshipFlag(contract);
            }
        }

        if (UW.equals(UW_DO_NOT_NEEDED)) {
            // дополнительная проверка договора на необходимость андеррайтинга (особая для конкретного продукта, в дополнение к стандартной)
            UW = additionalUnderwritingCheck(contract, login, password);
        }

        logger.debug("UW = " + UW);
        contract.put("UW", UW);

        logger.debug("Check contract underwriting finished.");

        return UW;

    }

    // получение застрахованного, профессия которого влияет на андеррайтинг
    // переопределить метод в конкретных фасадах, если требуется получение застрахованного не из INSUREDMAP
    protected Map<String, Object> getWorkingInsured(Map<String, Object> contract) {
        Map<String, Object> workingInsured = (Map<String, Object>) contract.get("INSUREDMAP");
        return workingInsured;
    }

    // получение списка всех застрахованных, для простых однотипных проверок
    // переопределить метод в конкретных фасадах, если требуется получение нескольких застрахованных, в том числе и не из INSUREDMAP
    protected List<Map<String, Object>> getAllInsuredList(Map<String, Object> contract) {
        List<Map<String, Object>> allInsuredList = new ArrayList<Map<String, Object>>();

        Map<String, Object> insured = (Map<String, Object>) contract.get("INSUREDMAP");
        allInsuredList.add(insured);

        return allInsuredList;
    }

    // проверка застрахованного, профессия которого влияет на андеррайтинг
    // переопределить метод в конкретных фасадах, если требуется получение застрахованного не из INSUREDMAP
    protected Long checkWorkingInsuredProfession(Map<String, Object> contract, String login, String password) throws Exception {

        // если выполнен вызов, значит UW в значении UW_DO_NOT_NEEDED
        Long UW = UW_DO_NOT_NEEDED;

        Map<String, Object> workingInsured = getWorkingInsured(contract);

        if (workingInsured != null) {

            Long professionHid = getLongParticipantExtAttrBySysName(workingInsured, "education");

            if (professionHid != null) {

                Map<String, Object> professionHBRecordParams = new HashMap<String, Object>();
                professionHBRecordParams.put("hid", professionHid);
                Map<String, Object> professionHBParams = new HashMap<String, Object>();
                professionHBParams.put("HANDBOOKNAME", "B2B.Life.Profession");
                professionHBParams.put("HANDBOOKDATAPARAMS", professionHBRecordParams);
                professionHBParams.put(RETURN_LIST_ONLY, true);
                //professionHBParams.put(RETURN_AS_HASH_MAP, true);

                List<Map<String, Object>> professionList = (List<Map<String, Object>>) this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BHandbookDataBrowseByHBName", professionHBParams, login, password);
// Кирьянов А.С.
// 04.05.2017 теперь андеррайтинг определяется по профессии "Другое"
                if ((professionList != null) && (professionList.size() == 1)) {
                    //logger.debug("riskClass = " + riskClass);
                    if (getStringParamLogged(professionList.get(0), "name").equalsIgnoreCase("другое")) {
                        UW = UW_NEEDED;
                    }
                }
// 04.05.2017 ранее андеррайтинг определялся по классу опасности профессии                
//                Long riskClass = null;
//                if ((professionList != null) && (professionList.size() == 1)) {
//                    //logger.debug("riskClass = " + riskClass);
//                    riskClass = getLongParamLogged(professionList.get(0), "riskClass");
//                }
//
//                if (riskClass != null) {
//                    if (riskClass > 2) {
//                        // высокая степень риска (3 или 4 класс риска)
//                        UW = UW_NEEDED;
//                    }
//                } else {
//                    // не найден класс риска для профессии
//                    logger.error(String.format("Insured participant profession risk class not found in hanbook with name 'B2B.Life.Profession' by hid (%d) - underwriting check is failed!", professionHid));
//                    UW = UW_UNKNOWN;
//                }
            } else {
                // не найдена мапа застрахованного
                logger.error(String.format("Insured participant profession not found in this contract - underwriting check is failed!"));
                UW = UW_UNKNOWN;
            }

        } else {
            // не найдена мапа застрахованного
            logger.error(String.format("Insured participant data not found in this contract - underwriting check is failed!"));
            UW = UW_UNKNOWN;
        }

        return UW;

    }

    protected Long getLongParticipantExtAttrBySysName(Map<String, Object> participant, String extAttrSysNameValue) {
        Long participantLongExtAttr = null;
        List<Map<String, Object>> extAttributeList2 = (List<Map<String, Object>>) participant.get("extAttributeList2");
        if (extAttributeList2 != null) {
            for (Map<String, Object> extAttr : extAttributeList2) {
                String extAttrSysName = getStringParam(extAttr, "EXTATT_SYSNAME");
                if (extAttrSysName.equals(extAttrSysNameValue)) {
                    logger.debug("EXTATT_SYSNAME = " + extAttrSysNameValue);
                    participantLongExtAttr = getLongParamLogged(extAttr, "EXTATTVAL_VALUE");
                }
            }
        }
        return participantLongExtAttr;
    }

    protected String getStringParticipantExtAttrBySysName(Map<String, Object> participant, String extAttrSysNameValue) {
        String participantStringExtAttr = null;
        List<Map<String, Object>> extAttributeList2 = (List<Map<String, Object>>) participant.get("extAttributeList2");
        if (extAttributeList2 != null) {
            for (Map<String, Object> extAttr : extAttributeList2) {
                String extAttrSysName = getStringParam(extAttr, "EXTATT_SYSNAME");
                if (extAttrSysName.equals(extAttrSysNameValue)) {
                    logger.debug("EXTATT_SYSNAME = " + extAttrSysNameValue);
                    participantStringExtAttr = getStringParamLogged(extAttr, "EXTATTVAL_VALUE");
                }
            }
        }
        return participantStringExtAttr;
    }

    protected Boolean getBooleanParticipantExtAttrBySysName(Map<String, Object> participant, String extAttrSysNameValue) {
        Boolean participantBooleanExtAttr = false;
        String participantStringExtAttr = getStringParticipantExtAttrBySysName(participant, extAttrSysNameValue);

        if (("TRUE".equalsIgnoreCase(participantStringExtAttr)) || ("1".equalsIgnoreCase(participantStringExtAttr)) || ("YES".equalsIgnoreCase(participantStringExtAttr))) {
            participantBooleanExtAttr = true;
        }

        logger.debug("EXTATTVAL_VALUE (boolean) = " + participantBooleanExtAttr);

        return participantBooleanExtAttr;
    }

    // проверка показателей здоровья для всех застрахованных по договору
    // переопределить метод в конкретных фасадах, если требуется проверить несколько застрахованных и/или системному имени объекта страхования отличается от 'insured'
    protected Long checkAllInsuredHealthForUW(Map<String, Object> contract) {
        // проверка показателей здоровья и пр для конкретного застрахованного (по системному имени объекта страхования)
        Long UW = checkInsuredHealthForUW(contract, "insured");
        return UW;
    }

    // проверка показателей здоровья и пр для конкретного застрахованного (по системному имени объекта страхования)
    // переопределить метод в конкретных фасадах, если требуется проверить сведения о здоровье, сохраненные не в объекте страхования
    protected Long checkInsuredHealthForUW(Map<String, Object> contract, String insuredKeyName) {

        // если выполнен вызов, значит UW в значении UW_DO_NOT_NEEDED
        Long UW = UW_DO_NOT_NEEDED;

        Map<String, Object> insuredInsObj = getInsObjBySysNameFromContract(contract, insuredKeyName);
        if (insuredInsObj != null) {

            Long bloodPressureTop = getLongParamLogged(insuredInsObj, "bloodPressureTop");
            Long bloodPressureLower = getLongParamLogged(insuredInsObj, "bloodPressureLower");
            Long heightSm = getLongParamLogged(insuredInsObj, "height"); // рост в метрах
            Long weightKg = getLongParamLogged(insuredInsObj, "weight"); // вес в килограммах

            if ((bloodPressureLower != null) && (bloodPressureTop != null) && (heightSm != null) && (weightKg != null)) {

                if ((bloodPressureTop < 73) || (bloodPressureTop > 142)) {
                    // верхнее давление за пределами нормы
                    UW = UW_NEEDED;
                    return UW;
                } else if ((bloodPressureLower < 56) || (bloodPressureLower > 92)) {
                    // нижнее давление за пределами нормы
                    UW = UW_NEEDED;
                    return UW;
                } else {
                    Double heightM = 0.01 * heightSm.doubleValue(); // рост в метрах
                    logger.debug("Insured height (meters) = " + heightM);
                    Double bodyIndex = weightKg.doubleValue() / (heightM * heightM);
                    logger.debug("Insured body index (calculated) = " + bodyIndex);
                    if ((bodyIndex < 17) || (bodyIndex > 30)) {
                        // индекс массы тела  за пределами нормы
                        UW = UW_NEEDED;
                        return UW;
                    }
                }

            } else {
                // не найден как минимум один из обязательных показателей здоровья
                logger.error(String.format("Not all of required insured health parameters was found in insured object of this contract - underwriting check is failed!"));
                UW = UW_UNKNOWN;
            }

            // проверка 'Наличие положительных ответов на вопросы Декларации застрахованного лица'
            Long insuredDeclCompliance = getLongParamLogged(insuredInsObj, "insuredDeclCompliance");
            if ((insuredDeclCompliance != null) && (insuredDeclCompliance.intValue() == 0)) {
                // галка 'Клиент соответствует декларации застрахованного' снята
                UW = UW_NEEDED;
                return UW;
            }

        } else {
            // не найден страховой объект - застрахованный
            logger.error(String.format("No insured object was found in this contract - underwriting check is failed!"));
            UW = UW_UNKNOWN;
        }

        return UW;
    }

    // проверка 'Наличие положительных ответов на вопросы Декларации застрахованного лица' без проверки других показателей здоровья
    // (для конкретного застрахованного - по системному имени объекта страхования)
    // переопределить метод в конкретных фасадах, если требуется проверить сведения о здоровье, сохраненные не в объекте страхования
    protected Long checkInsuredDeclComplianceUW(Map<String, Object> contract, String insuredKeyName) {

        // если выполнен вызов, значит UW в значении UW_DO_NOT_NEEDED
        Long UW = UW_DO_NOT_NEEDED;

        Map<String, Object> insuredInsObj = getInsObjBySysNameFromContract(contract, insuredKeyName);
        if (insuredInsObj != null) {

            // проверка 'Наличие положительных ответов на вопросы Декларации застрахованного лица'
            Long insuredDeclCompliance = getLongParamLogged(insuredInsObj, "insuredDeclCompliance");
            if ((insuredDeclCompliance != null) && (insuredDeclCompliance.intValue() == 0)) {
                // галка 'Клиент соответствует декларации застрахованного' снята
                UW = UW_NEEDED;
                return UW;
            }

        } else {
            // не найден страховой объект - застрахованный
            logger.error(String.format("No insured object was found in this contract - underwriting check is failed!"));
            UW = UW_UNKNOWN;
        }

        return UW;
    }

    protected Long checkAllInsuredResidentFlag(Map<String, Object> contract) {

        // если выполнен вызов, значит UW в значении UW_DO_NOT_NEEDED
        Long UW = UW_DO_NOT_NEEDED;

        List<Map<String, Object>> allInsuredList = getAllInsuredList(contract);

        for (Map<String, Object> insured : allInsuredList) {
            if (insured != null) {
                boolean unResident = getBooleanParticipantExtAttrBySysName(insured, "unResident");
                if (unResident) {
                    UW = UW_NEEDED;
                    return UW;
                }
            } else {
                // не найден один из застрахованных
                logger.error(String.format("Not all insured data was found in this contract - underwriting check is failed!"));
                UW = UW_UNKNOWN;
                return UW;
            }
        }

        return UW;

    }

    protected Long checkAllInsuredCitizenshipFlag(Map<String, Object> contract) {

        // если выполнен вызов, значит UW в значении UW_DO_NOT_NEEDED
        Long UW = UW_DO_NOT_NEEDED;

        List<Map<String, Object>> allInsuredList = getAllInsuredList(contract);

        for (Map<String, Object> insured : allInsuredList) {
            if (insured != null) {
                Long citizenship = getLongParamLogged(insured, "CITIZENSHIP");
                if (citizenship != null) {
                    // 0 - для CRM, 1 - для случаев, когда CITIZENSHIP хранит ссылку на справочник стран
                    int сitizenshipInt = citizenship.intValue();
                    if ((сitizenshipInt != 0) && (сitizenshipInt != 1)) {
                        UW = UW_NEEDED;
                        return UW;
                    }
                } else {
                    // не найден CITIZENSHIP в мапе застрахованных
                    logger.error(String.format("Not all insured citizenship was found in this contract - underwriting check is failed!"));
                    UW = UW_UNKNOWN;
                    return UW;
                }
            } else {
                // не найден один из застрахованных
                logger.error(String.format("Not all insured data was found in this contract - underwriting check is failed!"));
                UW = UW_UNKNOWN;
                return UW;
            }
        }

        return UW;
    }

    // дополнительная генерация вторичных свойств договора (особая для конкретного продукта)
    // переопределить метод в конкретных фасадах продуктов, если требуется подобная генерация
    protected Map<String, Object> genProductAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {
        logger.debug("Additional contract secondary product depended attributes generation not implemented for this product.");
        return contract;
    }

    /**
     * Получение продукта
     *
     * @param prodConfID Long
     * @param login      String
     * @param password   String
     * @return Map
     */
    protected Map<String, Object> getProductByProdConfID(Long prodConfID, String login, String password) throws Exception {
        // получение сведений о продукте (по идентификатору конфигурации продукта)

        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODCONFID", prodConfID);
        productParams.put("HIERARCHY", false);
        productParams.put(RETURN_AS_HASH_MAP, true);

        return this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);
    }

    // копия updateContractValues из B2BContractCustomFacade
    protected void updateContractValues(Map<String, Object> contrExtMap, String login, String password) throws Exception {
        logger.debug("updateContractValues begin");
        Map<String, Object> params = new HashMap<String, Object>();
        if (contrExtMap != null) {
            params.putAll(contrExtMap);
            Object contrExtID = this.callServiceAndGetOneValue(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", params, login, password, "CONTREXTID");
            if (contrExtID != null) {
                contrExtMap.put(ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
            }
        }
        logger.debug("updateContractValues end");
    }

    // стандартная (общая для всех продуктов) генерация вторичных свойств договора
    protected Map<String, Object> genAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {

        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        Map<String, Object> product = null;
        if (prodConfID == null) {
            if (contract.get("PRODUCTMAP") != null) {
                product = (Map<String, Object>) contract.get("PRODUCTMAP");
                if (product.get("PRODCONFID") != null) {
                    prodConfID = getLongParam(product.get("PRODCONFID"));
                }
            }
        }
        // данные по продукту могут быть и в полной мапе продукта FULLPRODMAP
        // они подходят для использования в качестве обычной мапы продукта
        if (product == null) {
            product = getMapParam(contract, "FULLPRODMAP");
        }
        // стандартная (общая для всех продуктов) генерация вторичных свойств договора
        boolean isDraftGenerate = true;

        if ((contract != null) && (contract.get(IS_GEN_SER_DRAFT_PARAM_NAME) != null)) {
            isDraftGenerate = getBooleanParam(contract.get(IS_GEN_SER_DRAFT_PARAM_NAME), true);
        }

        if (isDraftGenerate) {
            generateContractSerNumDraft(contract, login, password);
        } else {

            // ниже - фрагмента кода скопирован в BorrowerProtectLongTermCustomFacade#dsB2BBorrowerProtectLongTermContractGenerateNumber
            // todo: все изменения вносить синхронно в оба метода (или же выделить в общий базовый метод и вызывать)
            // todo: исправить, когда/если будет найдено более адекватное решение по проблеме перевода состояния договора

            /**
             *  Переходе из черновика безусловно регенерируем серию договора
             *  (т.к. серия может формироваться в зависимости от введенных данных),
             *  пример метод generateContrSer в {@link com.bivgroup.services.b2bposws.facade.pos.custom.sberlife.SmartPolicyFacade}
             *  А вот номер договора формируем только один раз при переходе из черновика в предпечать (повторно генерировать номер не нужно)
             */

            String autoNumSysName = "";
            String seriesPrefix = "";

            if (contract != null) {

                Long  prodConfId = getLongParam(contract.get("PRODCONFID"));

                if (prodConfId != null) {
                    Map<String, Object> prodDefValMap = getProductDefaultValueByProdConfId(prodConfId, login, password);
                    autoNumSysName = getStringParam(prodDefValMap.get("CONTRAUTONUMBERSYSNAME"));
                    seriesPrefix = getStringParam(prodDefValMap.get("CONTRSERIESPREFIX"));
                    // получение альтернативной серии договора (если предусмотрена для данного способа создания договора)
                    seriesPrefix = getAltSeriesPrefixIfNeeded(contract, contract, prodDefValMap, seriesPrefix, login, password);

                }
            }


            if (contract.get("CONTRPOLNUM") == null) {
                contract.put("CONTRPOLNUM", generateContrNum(autoNumSysName, login, password));
            }

            generateContrSer(contract, seriesPrefix, login, password);

            // выше - фрагмента кода скопирован в BorrowerProtectLongTermCustomFacade#dsB2BBorrowerProtectLongTermContractGenerateNumber
            // todo: все изменения вносить синхронно в оба метода (или же выделить в общий базовый метод и вызывать)
            // todo: исправить, когда/если будет найдено более адекватное решение по проблеме перевода состояния договора

        }

        getFinishDateByStartDateAndTermId(contract, login, password);

        Date dateRate = (Date) parseAnyDate(contract.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
        Long insAmCurrencyID = getLongParam(contract.get("INSAMCURRENCYID"));
        Double currencyRate = roundCurrencyRate(getExchangeRateByCurrencyID(insAmCurrencyID, dateRate, login, password));
        contract.put("CURRENCYRATE", currencyRate);

        // инициализация значений расширенных атрибутов
        Map<String, Object> contrExtMap = getOrCreateContrExtMap(contract);

        // получение сведений о продукте (по идентификатору конфигурации продукта)
        if (product == null && prodConfID != null) {
            product = getProductByProdConfID(prodConfID, login, password);
        }
        // определение идентификатора справочника расширенных атрибутов договора на основании сведений о продукте
        Object contrExtMapHBDataVerID = product.get("HBDATAVERID");
        contrExtMap.put("HBDATAVERID", contrExtMapHBDataVerID);

        // дополнительная генерация вторичных свойств договора (особая для конкретного продукта)
        // переопределить метод genProductAdditionalSaveParams в конкретных фасадах продуктов, если требуется подобная генерация
        genProductAdditionalSaveParams(contract, login, password);

        return contract;
    }

    // полная мапа продукта - получение из мапы договора или загрузка из БД (если в мапе договора есть системное наименовние продукта)
    protected Map<String, Object> getFullProductMapFromContractMapOrLoadFromDB(Map<String, Object> contract, String login, String password) throws Exception {
        Map<String, Object> productMap = (Map<String, Object>) contract.get("FULLPRODMAP");
        if (productMap == null) {
            String prodSysName = getStringParamLogged(contract, "PRODSYSNAME");
            if (!prodSysName.isEmpty()) {
                // получение сведений о продукте (по системному имени продукта)
                Map<String, Object> productParams = new HashMap<>();
                productParams.put("PRODSYSNAME", prodSysName);
                productParams.put("LOADALLDATA", 1L);
                productParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> productRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductBrowseBySysName", productParams, login, password);
                productMap = (Map<String, Object>) productRes.get("PRODMAP");
                contract.put("FULLPRODMAP", productMap);
            }
        }
        return productMap;
    }

    // обычная мапа продукта - получение из мапы договора или загрузка из БД (если в мапе договора есть системное наименовние продукта)
    protected Map<String, Object> getProductMapFromContractMapOrLoadFromDB(Map<String, Object> contract, String login, String password) throws Exception {
        /*
        Map<String, Object> productMap = null;
        String[] productMapKeyNames = new String[]{
                "FULLPRODMAP", // полная мапа продукта также подойдет в качестве обычной мапы
                "PRODMAP", "PRODUCTMAP", "PRODCONF"};
        for (String productMapKeyName : productMapKeyNames) {
            productMap = (Map<String, Object>) contract.get(productMapKeyName);
            if (productMap != null) {
                break;
            }
        }
        */
        Map<String, Object> productMap = getAnyProductMapFromContract(contract);
        if (productMap == null) {
            String prodSysName = getStringParamLogged(contract, "PRODSYSNAME");
            if (!prodSysName.isEmpty()) {
                // получение сведений о продукте (по системному имени продукта)
                Map<String, Object> productParams = new HashMap<String, Object>();
                productParams.put("PRODSYSNAME", prodSysName);
                productParams.put("LOADALLDATA", 0L);
                productParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> productRes = this.callService(Constants.B2BPOSWS, "dsB2BProductBrowseBySysName", productParams, login, password);
                productMap = (Map<String, Object>) productRes.get("PRODMAP");
                contract.put("PRODMAP", productMap);
            }
        }
        return productMap;
    }

    // для случаев, когда в мапе договора не заполнена ссылка на программу и продукт имеет одну единственную программу - будет выбрана программа из продукта
    // todo: возможно, добавить в общие для всех продуктов подготовительные операции?
    protected void checkAndUpdateContractProgram(Map<String, Object> contract, String login, String password) throws Exception {
        if (contract.get("PRODPROGID") == null) {
            logger.debug("No selected program id (PRODPROGID) was fount in contract - checking product for programs...");
            // полная мапа продукта - получение из мапы договора или загрузка из БД (если в мапе договора есть системное наименовние продукта)
            Map<String, Object> productMap = getFullProductMapFromContractMapOrLoadFromDB(contract, login, password);
            if (productMap != null) {
                Map<String, Object> prodVer = (Map<String, Object>) productMap.get("PRODVER");
                if (prodVer != null) {
                    List<Map<String, Object>> prodProgList = (List<Map<String, Object>>) prodVer.get("PRODPROGS");
                    if (prodProgList != null) {
                        if (prodProgList.isEmpty()) {
                            logger.error("Unable to find any programs in product data! Contract will be saved without setting the program!");
                        } else if (prodProgList.size() == 1) {
                            // у продукта лишь одна программа - устанавливется безусловно
                            logger.debug("Product info contains only one single program - will be used for setting selected program id (PRODPROGID) in contract.");
                            Map<String, Object> prodProg = prodProgList.get(0);
                            Long prodProgID = getLongParamLogged(prodProg, "PRODPROGID");
                            setGeneratedParam(contract, "PRODPROGID", prodProgID, logger.isDebugEnabled());
                        } else {
                            logger.error("More then one program was found in product data and selecting from multiple programs not implemented in current method! Contract will be saved without setting the program!");
                        }
                    }
                }
            }
        }
    }

    protected Map<String, Object> getOrCreateMapByKey(Map<String, Object> contract, String key) {
        Map<String, Object> resultMap = (Map<String, Object>) contract.get(key);
        if (resultMap == null) {
            resultMap = new HashMap<String, Object>();
            contract.put(key, resultMap);
        }
        return resultMap;
    }

    protected Map<String, Object> getOrCreateInsurerMap(Map<String, Object> contract) {
        return getOrCreateMapByKey(contract, "INSURERMAP");
    }

    protected Map<String, Object> getOrCreateInsuredMap(Map<String, Object> contract) {
        return getOrCreateMapByKey(contract, "INSUREDMAP");
    }

    protected Map<String, Object> getOrCreateContrExtMap(Map<String, Object> contract) {
        return getOrCreateMapByKey(contract, "CONTREXTMAP");
    }

    protected Map<String, Object> getContrExtMap(Map<String, Object> contract) {
        return getMapParam(contract, "CONTREXTMAP");
    }

    protected void initDefaultInsPhysicMapByKey(Map<String, Object> contract, String key) {
        Map<String, Object> resultMap = getOrCreateMapByKey(contract, key);
        // по умолчанию пол страхователя "Мужской"
        setGeneratedParamIfNull(resultMap, "GENDER", 0L, true, isVerboseLogging);
        // по умолчанию гражданство "РФ"
        setGeneratedParamIfNull(resultMap, "CITIZENSHIP", 0L, true, isVerboseLogging);
        setGeneratedParamIfNull(resultMap, "PARTICIPANTTYPE", 1L, true, isVerboseLogging);
        setGeneratedParamIfNull(resultMap, "ISBUSINESSMAN", 0L, true, isVerboseLogging);
//        setGeneratedParamIfNull(resultMap, "LASTNAME", " ", true, isVerboseLogging);
//        setGeneratedParamIfNull(resultMap, "FIRSTNAME", " ", true, isVerboseLogging);
//        setGeneratedParamIfNull(resultMap, "MIDDLENAME", " ", true, isVerboseLogging);
    }

    protected void initDefaultInsurerMap(Map<String, Object> contract) {
        initDefaultInsPhysicMapByKey(contract, "INSURERMAP");
    }

    protected void initDefaultInsuredMap(Map<String, Object> contract) {
        initDefaultInsPhysicMapByKey(contract, "INSUREDMAP");
    }

    protected void setGeneratedParam(Map<String, Object> paramParent, String paramName, Object newValue, boolean isMarkAsModified, boolean isLogged) {
        paramParent.put(paramName, newValue);
        if (isMarkAsModified) {
            markAsModified(paramParent);
        }
        if (isLogged) {
            logParamGeneration(paramName, newValue);
        }
    }

    protected void setGeneratedParamIfNull(Map<String, Object> paramParent, String paramName, Object newValue, boolean isMarkAsModified, boolean isLogged) {
        if (paramParent.get(paramName) == null) {
            setGeneratedParam(paramParent, paramName, newValue, isMarkAsModified, isLogged);
        }
    }

    protected List<Map<String, Object>> getAvailableProductStrategyForSale(String productSysName, String login, String password) throws Exception {
        Date checkDate = new Date();
        return getAvailableProductStrategyForSale(productSysName, checkDate, login, password);
    }

    protected List<Map<String, Object>> getAvailableProductStrategyForSale(String productSysName, Date checkDate, String login, String password) throws Exception {
        Long prodInvestId = null; // без ограничения по конкретному ИД
        Long termId = null; // без ограничения по сроку действия договора
        List<Map<String, Object>> productStrategyList = getAvailableProductStrategyForSale(prodInvestId, productSysName, checkDate, termId, login, password);
        return productStrategyList;
    }

    protected List<Map<String, Object>> getAvailableProductStrategyForSale(Long prodInvestId, String productSysName, Date checkDate, Long termId, String login, String password) throws Exception {
        Map<String, Object> productStrategyParams = new HashMap<String, Object>();
        productStrategyParams.put("STRATEGYISFORSALE", 1L);
        productStrategyParams.put("CHECKDATE", checkDate);
        productStrategyParams.put("CHECKSTRATEGYDATE", checkDate);
        productStrategyParams.put("PRODSYSNAME", productSysName);
        if (prodInvestId != null) {
            // с ограничением по конкретному ИД (например, для проверки выбранной в договоре стратегии по ограничениям)
            productStrategyParams.put("PRODINVESTID", prodInvestId);
        }
        if (termId != null) {
            // с нестрогим ограничением по сроку действия (например, для проверки выбранной в договоре стратегии по ограничениям согласно #11298)
            // (будут исключены только отличающиеся по сроку, с неуказанным сроком - будут включены в результат)
            productStrategyParams.put("STRATEGYTERMIDNULLOREQUAL", termId);
        }
        String methodName = "dsB2BProductInvestmentStrategyBrowseListByParamEx";
        List<Map<String, Object>> productStrategyList = this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, methodName, productStrategyParams, login, password);
        return productStrategyList;
    }

    protected Map<String, Object> getProductStrategy(Long prodInvestId, String login, String password) throws Exception {
        Map<String, Object> productStrategy = null;
        if (prodInvestId != null) {
            Map<String, Object> productStrategyParams = new HashMap<String, Object>();
            // по конкретному ИД (например, для разыменовки и т.п.)
            productStrategyParams.put("PRODINVESTID", prodInvestId);
            productStrategyParams.put(RETURN_AS_HASH_MAP, true);
            String methodName = "dsB2BProductInvestmentStrategyBrowseListByParamEx";
            productStrategy = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, methodName, productStrategyParams, login, password);
        }
        return productStrategy;
    }

    // проверка на 'Минимальный размер взноса' (с учетом наличия проверенной и корректной стратегии инвестирования)
    protected void validateMinimumPaymentValue(Map<String, Object> contract, Map<String, Object> validInvestmentStrategy, StringBuffer errorText, String login, String password) throws Exception {
        // проверка на 'Минимальный размер взноса'
        Double rejectPaymentValueMin = 0.0;
        String selectedError = "Текущая премия по договору ниже минимально допустимого размера взноса";
        // параметр 'Минимальный размер взноса' из стратегии инвестирования приоритетнее указанной в продукте (#9946)
        if (validInvestmentStrategy != null) {
            // имеется проверенная стратегия инвестирования
            // текст ошибки в случае провала данной проверки
            selectedError = "Текущая премия по договору ниже минимального размера взноса, установленного для выбранной стратегии инвестировния";
            rejectPaymentValueMin = getDoubleParamLogged(validInvestmentStrategy, "STRATEGYMINPAYVALUE");
        }
        // параметр 'Минимальный размер взноса' из продукта
        if (rejectPaymentValueMin < MIN_SIGNIFICANT_SUM) {
            // текст ошибки в случае провала данной проверки
            selectedError = "Текущая премия по договору ниже минимального размера взноса, установленного для данного продукта";
            // код валюты
            String currencyAlphaCode = getStringParamLogged(contract, "INSAMCURRENCYALPHACODE");
            String rejectPaymentValueMinProdDefValName = "REJECT_PREMVALUE_" + currencyAlphaCode + "_MIN";
            rejectPaymentValueMin = getDoubleProdDefValueFromContract(contract, rejectPaymentValueMinProdDefValName, login, password);
        }
        if (rejectPaymentValueMin >= MIN_SIGNIFICANT_SUM) {
            Double contrPremValue = getDoubleParamLogged(contract, "PREMVALUE");
            if (contrPremValue < rejectPaymentValueMin) {
                errorText.append(selectedError).append(". ");
            }
        }
    }

    /**
     * Обновленная версия валидации метода validateInvestmentStrategy
     *
     * @param contract               - мапа договора
     * @param contractValidateParams - подготовленные параметры для валидации
     * @param errorText              - сборщик ошибок валидации
     * @param login                  - логин
     * @param password               - пароль
     */
    private void validateInvestmentStrategyByContractValidateParams(Map<String, Object> contract, Map<String, Object> contractValidateParams, StringBuffer errorText, String login, String password) throws Exception {
        logger.debug("validateInvestmentStrategyByContractValidateParams...");


        String error = "";

        // согласно настройкам продукта проверка стратегии инвестирования требуется
        String productSysName = getStringParamLogged(contract, "PRODSYSNAME");
        // получение срока действия договора (#11298)
        Long termId = getLongParamLogged(contract, "TERMID");
        if (productSysName.isEmpty()) {
            error = "Не удалось определить системное наименование продукта, необходимое для проверки выбранного фонда";
        } else if (termId == null) {
            error = "Не удалось определить срок страхования по договору, необходимый для проверки выбранного фонда";
        } else {
            // выбор даты для проверок сроков действия
            Date checkDate = null;
            Object signDateObj = contract.get("SIGNDATE");
            logger.debug("SIGNDATE = " + signDateObj);
            if (signDateObj != null) {
                checkDate = (Date) parseAnyDate(signDateObj, Date.class, "SIGNDATE", logger.isDebugEnabled());
            }
            if (checkDate == null) {
                checkDate = new Date();
                logger.debug("Sign date is not specified! Using current system date: " + checkDate);
            }

            Long fundForInvestmentStrategyCheck = getLongParam(contractValidateParams.get(FUND_FOR_INVESTMENT_STRATEGY_CHECK));
            List<Map<String, Object>> productStrategyList =
                    getAvailableProductStrategyForSale(fundForInvestmentStrategyCheck, productSysName,
                            checkDate, termId, login, password);

            if ((productStrategyList != null) && (productStrategyList.size() == 1)) {
                Map<String, Object> productStrategy = productStrategyList.get(0);
                Long prodInvestId = getLongParamLogged(productStrategy, "PRODINVESTID");
                if (!fundForInvestmentStrategyCheck.equals(prodInvestId)) {
                    error = "Выбранный фонд не подходит по сроку действия или не предназначен для выбора при заключении договора";

                    productStrategyList = getAvailableProductStrategyForSale(productSysName, checkDate, login, password);
                    Long contractId = getLongParam(contract, "CONTRID");
                    logger.error(String.format(
                            "Selected for contract (with id = %d) with product system name = '%s' investment strategy (fund = %d) failed availability check on date = %s! Available strategies is: %s.",
                            contractId, productSysName, fundForInvestmentStrategyCheck, checkDate, productStrategyList
                    ));
                }
            }
        }

        if (!error.isEmpty()) {
            errorText.append(error)
                    .append(". ");
        }

        logger.debug("validateInvestmentStrategyByContractValidateParams finished.");
    }

    // проверка выбранной стратегии инвестирования
    @Deprecated
    protected Map<String, Object> validateInvestmentStrategy(Map<String, Object> contract, StringBuffer errorText, String login, String password) throws Exception {
        logger.debug("validateInvestmentStrategy...");
        Map<String, Object> productStrategy = null;
        String error = "";
        // следует определить, требуется ли (и возомжна ли) эта проверка для данного продукта
        Long fundForInvestmentStrategyCheck = getFundForInvestmentStrategyCheck(contract, login, password);
        if (fundForInvestmentStrategyCheck != null) {
            // согласно настройкам продукта проверка стратегии инвестирования требуется
            String productSysName = getStringParamLogged(contract, "PRODSYSNAME");
            // получение срока действия договора (#11298)
            Long termId = getLongParamLogged(contract, "TERMID");
            if (productSysName.isEmpty()) {
                error = "Не удалось определить системное наименование продукта, необходимое для проверки выбранного фонда";
            } else if (termId == null) {
                error = "Не удалось определить срок страхования по договору, необходимый для проверки выбранного фонда";
            } else {
                // выбор даты для проверок сроков действия
                Date checkDate = null;
                Object signDateObj = contract.get("SIGNDATE");
                logger.debug("SIGNDATE = " + signDateObj);
                if (signDateObj != null) {
                    checkDate = (Date) parseAnyDate(signDateObj, Date.class, "SIGNDATE", logger.isDebugEnabled());
                }
                if (checkDate == null) {
                    checkDate = new Date();
                    logger.debug("Sign date is not specified! Using current system date: " + checkDate);
                }
                // ошибка по умолчанию
                error = "Выбранный фонд не подходит по сроку действия или не предназначен для выбора при заключении договора";
                List<Map<String, Object>> productStrategyList = getAvailableProductStrategyForSale(fundForInvestmentStrategyCheck, productSysName, checkDate, termId, login, password);
                if ((productStrategyList != null) && (productStrategyList.size() == 1)) {
                    productStrategy = productStrategyList.get(0);
                    Long prodInvestId = getLongParamLogged(productStrategy, "PRODINVESTID");
                    if (fundForInvestmentStrategyCheck.equals(prodInvestId)) {
                        // успех
                        logger.debug("Investment strategy successfully checked. Confirmed strategy info: " + productStrategy);
                        error = "";
                    }
                }
                if (!error.isEmpty()) {
                    productStrategyList = getAvailableProductStrategyForSale(productSysName, checkDate, login, password);
                    Long contractId = getLongParam(contract, "CONTRID");
                    logger.error(String.format(
                            "Selected for contract (with id = %d) with product system name = '%s' investment strategy (fund = %d) failed availability check on date = %s! Available strategies is: %s.",
                            contractId, productSysName, fundForInvestmentStrategyCheck, checkDate, productStrategyList
                    ));
                }
            }
            if (!error.isEmpty()) {
                logger.error(String.format(
                        "Method validateInvestmentStrategy resulted in error with message '%s'.",
                        error
                ));
                errorText.append(error).append(". ");
            }
        }
        if (!error.isEmpty()) {
            productStrategy = null;
        }
        logger.debug("validateInvestmentStrategy finished.");
        return productStrategy;
    }

    // следует определить, требуется ли (и возомжна ли) проверка на 'Минимальный размер взноса' из стратегии инвестирования для данного продукта
    // метод getFundForInvestmentStrategyCheck вернет null, если проверка не требуется (или невозомжна) или ИД фонда, если проверка требуется и возможна
    private Long getFundForInvestmentStrategyCheck(Map<String, Object> contract, String login, String password) throws Exception {
        Long fundForInvestmentStrategyCheck = null;
        Map<String, Object> contractExtValues = getMapParam(contract, "CONTREXTMAP");
        if (contractExtValues != null) {
            // имеются расширенные атрибуты договора
            Long fundRefTypeId = getLongParamLogged(contractExtValues, FUND_REF_TYPE_ID_PARAMNAME);
            if (FUND_REF_TYPE_ID_PRODUCT_STRATEGY_HANDBOOK.equals(fundRefTypeId)) {
                // Тип ссылки на фонд (fundRefTypeId): 2 - на новый справочник (B2B.SBSJ.RelationStrategyProduct)
                // (для других типов ссылки данная проверка невозможна)
                Long fund = getLongParamLogged(contractExtValues, FUND_ID_PARAMNAME);
                if (fund != null) {
                    // указана стратегия инвестирования (отсутствие же обязательного расширенного атрибута будет проверено в другом месте - см. validateContractExtValues)
                    // следует определить, включена ли проверка в B2B_PRODDEFVAL для данного продукта (REJECT_INVESTMENT_STRATEGY_CHECK)
                    Long investmentStrategyCheck = getLongProdDefValueFromContract(contract, "REJECT_INVESTMENT_STRATEGY_CHECK", login, password);
                    if (BOOLEAN_FLAG_LONG_VALUE_TRUE.equals(investmentStrategyCheck)) {
                        fundForInvestmentStrategyCheck = fund;
                    };
                }
            }
        }
        return fundForInvestmentStrategyCheck;
    }

    private Long getLongProdDefValueFromContract(Map<String, Object> contract, String defValueName, String login, String password) throws Exception {
        Map<String, Object> product = getFullProductDataFromContract(contract, login, password);
        // todo: возможно, заменить чтение целого продукта (getFullProductDataFromContract) на запрос только B2B_PRODDEFVAL (для случаев вызовов не из сохранения)
        Map<String, Object> prodDefValuesMap = getProdDefValsMapFromProduct(product);
        Long prodDefValue = getLongProdDefValueFromDefValuesMap(prodDefValuesMap, defValueName);
        return prodDefValue;
    }

    private Double getDoubleProdDefValueFromContract(Map<String, Object> contract, String defValueName, String login, String password) throws Exception {
        Map<String, Object> product = getFullProductDataFromContract(contract, login, password);
        // todo: возможно, заменить чтение целого продукта (getFullProductDataFromContract) на запрос только B2B_PRODDEFVAL (для случаев вызовов не из сохранения)
        Map<String, Object> prodDefValuesMap = getProdDefValsMapFromProduct(product);
        Double prodDefValue = getDoubleProdDefValueFromDefValuesMap(prodDefValuesMap, defValueName);
        return prodDefValue;
    }

    protected Map<String, Object> processProductInvestmentStrategy(Long prodInvestId, String login, String password) throws Exception {
        Map<String, Object> productInvestmentStrategy = getProductStrategy(prodInvestId, login, password);
        Map<String, Object> investStrategyMap = new HashMap<String, Object>();
        if (productInvestmentStrategy != null) {
            // из investStrategyMap в методе resolveInvestmentStrategy используются только INVBASEACTIVEID, COUPONSIZE, CURRENCYID
            investStrategyMap.put("INVBASEACTIVEID", productInvestmentStrategy.get("STRATEGYINVBASEACTIVEID"));
            investStrategyMap.put("COUPONSIZE", productInvestmentStrategy.get("STRATEGYCOUPONSIZE"));
            investStrategyMap.put("CURRENCYID", productInvestmentStrategy.get("STRATEGYCURRENCYID"));
            // todo: возможно, следует реализовать цикл вида investStrategyMap.put("*", productInvestmentStrategy.get("STRATEGY*"))
        }
        // BASEACTIVEMAP(NAME,CODE,SYSNAME,TICKERLIST,COUPONS
        Map<String, Object> baseActiveMap = resolveInvestmentStrategy(investStrategyMap, login, password);
        return baseActiveMap;
    }

    private Map<String, Object> resolveInvestmentStrategy(Map<String, Object> investStrategyMap, String login, String password) throws Exception {
        Map<String, Object> baseActiveMap = null;
        Long baseActiveId = getLongParamLogged(investStrategyMap, "INVBASEACTIVEID");
        if (baseActiveId != null) {
            // BASEACTIVEMAP(NAME,CODE,SYSNAME,TICKERLIST,COUPONS
            baseActiveMap = getBaseActiveMap(baseActiveId, login, password);
            baseActiveMap.put("COUPONSIZE", investStrategyMap.get("COUPONSIZE"));
            baseActiveMap.put("CURRENCYID", investStrategyMap.get("CURRENCYID"));
            List<Map<String, Object>> tickerList = getTickerList(baseActiveId, login, password);
            if (tickerList != null) {
                int tickerCount = tickerList.size();
                baseActiveMap.put("TICKERLIST", tickerList);
                // todo сформировать количество тикеров в нужном падеже.
                baseActiveMap.put("TICKERCOUNTSTR", getIntStringGenetive(tickerCount));
                baseActiveMap.put("TICKERCOUNT", tickerCount);
            }
        }
        return baseActiveMap;
    }

    private List<Map<String, Object>> getTickerList(Long baseActiveId, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("INVBASEACTIVEID", baseActiveId);
        return this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BInvestBaseActiveTickerBrowseListByParamEx", param, login, password);

    }

    private Map<String, Object> getBaseActiveMap(Long baseActiveId, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("INVBASEACTIVEID", baseActiveId);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> baseActiveMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BInvestBaseActiveBrowseListByParam", param, login, password);
        return baseActiveMap;
    }

    protected List<Map<String, Object>> getProdInvestStrategyList(Long prodConfID, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("PRODCONFID", prodConfID);
        return this.callServiceAndGetListFromResultMap(B2BPOSWS_SERVICE_NAME, "dsB2BProductInvestBrowseListByParam", param, login, password);
    }


    protected Map<String, Object> processInvestStrategy(List<Map<String, Object>> isList, Long fundHid, Long currencyId, String login, String password) throws Exception {
        // BASEACTIVEMAP(NAME,CODE,SYSNAME,TICKERLIST,COUPONS
        Map<String, Object> investStrategyMap = getInvestStrategyMap(fundHid, currencyId, login, password);
        Map<String, Object> baseActiveMap = resolveInvestmentStrategy(investStrategyMap, login, password);
        return baseActiveMap;
    }

    private Map<String, Object> getInvestStrategyMap(Long fundHid, Long currencyId, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<>();
        //TODO: как временное решение, пока фонды на редакторе продукта не фильтруются по валюте
        // в NOTE храним hid из справочника. на выходе - получим список по валюте.
        // и выбираем по валюте договора.
        //param.put("INVESTSTRATEGYID", fundHid);
        param.put("NOTE", fundHid);
        param.put("CURRENCYID", currencyId);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> investStrategyMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BInvestStrategyBrowseListByParam", param, login, password);
        return investStrategyMap;
    }

    // дополнительная проверка договора на необходимость андеррайтинга (особая для конкретного продукта, в дополнение к стандартной)
    // переопределить метод в конкретных фасадах, если требуется подобная проверка
    protected Long additionalUnderwritingCheck(Map<String, Object> contract, String login, String password) throws Exception {
        // если выполнен вызов, значит UW в значении UW_DO_NOT_NEEDED
        Long UW = UW_DO_NOT_NEEDED;
        logger.debug("Additional contract underwriting checks not implemented for this product.");
        return UW;
    }

    protected void uwLogReason(Long uw, Map<String, Object> contract, String uwReason) {
        // todo: заменить на loggerDebugPretty или отключить совсем протоколирование решения об андеррайтинге после завершения разработки и проверок
        Long contractId = getLongParamLogged(contract, "CONTRID");
        String contractNumber = getStringParamLogged(contract, "CONTRNUMBER");
        String entityName = String.format("договор c ИД = %d и номером = '%s'", contractId, contractNumber);
        uwLogReason(uw, contract, entityName, uwReason);
        contract.put("UWREASON", uwReason);
    }

    private void uwLogReason(Long uw, Map<String, Object> entity, String entityName, String uwReason) {
        String uwName = UW_NEEDED.equals(uw) ? "Требуется андеррайтинг (%d - UW_NEEDED)." :
                (UW_UNKNOWN.equals(uw) ? "Не возможно определить необходимость андеррайтинга (%d - UW_UNKNOWN)." : "Андеррайтинг не требуется (%d - UW_DO_NOT_NEEDED).");
        String uwFullName = String.format(uwName, uw);
        String uwReasonFullMsg = String.format("%s Причина решения: '%s'. Подробнее (%s)", uwFullName, uwReason, entityName);
        // todo: заменить на loggerDebugPretty или отключить совсем протоколирование решения об андеррайтинге после завершения разработки и проверок
        loggerErrorPretty(logger, uwReasonFullMsg, entity);
    }

    /** определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения */
    protected void updateSessionParamsIfNullByCallingUserCreds(Map<String, Object> contract, String login, String password) throws Exception {
        if ((contract.get(Constants.SESSIONPARAM_USERACCOUNTID) == null) && (contract.get(Constants.SESSIONPARAM_DEPARTMENTID) == null)) {
            Map<String, Object> checkLoginParams = new HashMap<String, Object>();
            checkLoginParams.put("username", XMLUtil.getUserName(login));
            checkLoginParams.put("passwordSha", password);
            Map<String, Object> checkLoginResult = WsUtils.getFirstItemFromResultMap(this.selectQuery("dsCheckLogin", checkLoginParams));
            if (checkLoginResult != null) {
                contract.put(Constants.SESSIONPARAM_USERACCOUNTID, checkLoginResult.get("USERACCOUNTID"));
                contract.put(Constants.SESSIONPARAM_DEPARTMENTID, checkLoginResult.get("DEPARTMENTID"));
            }
        }
    }

    // перенесено в UOABaseFacade
    /*
    // смена состояния договора
    protected Map<String, Object> doContractMakeTrans(Map<String, Object> contract, String toStateSysName, StringBuilder error, String login, String password) throws Exception {
        Map<String, Object> transParams = new HashMap<>();
        Long contractId = getLongParam(contract, "CONTRID");
        transParams.put("CONTRID", contractId);
        String stateSysName = getStringParamLogged(contract, "STATESYSNAME");
        transParams.put("STATESYSNAME", stateSysName);
        transParams.put("TOSTATESYSNAME", toStateSysName);
        transParams.put("CONTRMAP", contract);
        transParams.put(RETURN_AS_HASH_MAP, true);
        String methodName = "dsB2BcontractMakeTrans";
        Map<String, Object> transResult = callServiceLogged(B2BPOSWS_SERVICE_NAME, methodName, transParams, login, password);
        String resultStateSysName = getStringParamLogged(transResult, "STATESYSNAME");
        if (!toStateSysName.equals(resultStateSysName)) {
            logger.error(String.format(
                    "Calling %s (with params = %s) caused error! Details (call result): %s",
                    methodName, transParams, transResult
            ));
            error.append("Не удалось изменить состояние договора! ");
        }
        return transResult;
    }
    */

    /**
     * Если нет ИД договора - создается заготовка договора, в которой заведомо отсутствуют обязательные данные
     * (вызов сохранения при выборе продукта на странице создания нового договора, еще до перехода на первую страницу договора).
     * Такую заготовку следует считать корректной, иначе невозможно будет её сохранить без ввода дополнительных флагов и сведений.
     * Исключение - если передан соответствующий флаг и вызов не из интерфейса (в этом случае валидация обязательна).
     */
    protected boolean isValidationRequired(Map<String, Object> params) {
        logger.debug("isValidationRequired start...");
        Long contractId = getLongParamLogged(params, "CONTRID");
        boolean isValidationRequired = (contractId != null);
        if (!isValidationRequired) {
            isValidationRequired = isValidationForced(params);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("isValidationRequired finished with result = " + isValidationRequired + ".");
        }
        return isValidationRequired;
    }

    private boolean isValidationForced(Map<String, Object> params) {
        logger.debug("isValidationForced start...");
        // выполнен ли вызов сохранения из интерфейса
        boolean isCallFromGate = isCallFromGate(params);
        // если передан соответствующий флаг и вызов не из интерфейса, то валидация принудительно обязательная
        boolean isForcedValidation = (!isCallFromGate) && getBooleanParamLogged(params, IS_FORCED_VALIDATION_PARAMNAME, false);
        if (logger.isDebugEnabled()) {
            logger.debug("isValidationForced finished with result = " + isForcedValidation + ".");
        }
        return isForcedValidation;
    }

}
