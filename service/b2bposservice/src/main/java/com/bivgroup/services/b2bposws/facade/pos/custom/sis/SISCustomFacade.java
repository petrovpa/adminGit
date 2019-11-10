/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.sis;

import static com.bivgroup.services.b2bposws.facade.pos.contract.custom.B2BContractCustomFacade.getLastElementByAtrrValue;
import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import org.apache.log4j.Logger;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 *
 * @author averichevsm
 */
@BOName("SISCustom")
public class SISCustomFacade extends ProductContractCustomFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    private static final String THIS_SERVICE_NAME = B2BPOSWS_SERVICE_NAME;
    private static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;

    private static final String DEFAULT_AND_ONLY_PROGRAM_SYSTEM_NAME_SIS = "SIS_BASIC";

    // имена ключей для списка ценных вещей и сумм по ценным вещам
    private static final String VALUABLES_BASE_KEY_NAME = "valuables";
    private static final String LISTED_FLAG_KEY_NAME = VALUABLES_BASE_KEY_NAME + "Listed";
    private static final String LIST_JSON_STRING_KEY_NAME = VALUABLES_BASE_KEY_NAME + "Str";
    private static final String LIST_KEY_NAME = VALUABLES_BASE_KEY_NAME + "List";
    // страховая сумма по перечисленным в списке ценным вещам
    private static final String DETAILED_INSURANCE_SUM_KEY_NAME = VALUABLES_BASE_KEY_NAME + "DetailedInsuranceSum";
    // страховая премия по перечисленным в списке ценным вещам
    private static final String DETAILED_PREMIUM_SUM_KEY_NAME = VALUABLES_BASE_KEY_NAME + "DetailedPremiumSum";
    // полная страховая сумма
    private static final String TOTAL_INSURANCE_SUM_KEY_NAME = VALUABLES_BASE_KEY_NAME + "TotalInsuranceSum";
    // полная страховая премия
    private static final String TOTAL_PREMIUM_SUM_KEY_NAME = VALUABLES_BASE_KEY_NAME + "TotalPremiumSum";
    // страховая сумма по остальным (не перечисленным в списке) ценным вещам
    private static final String UNUSED_INSURANCE_SUM_KEY_NAME = VALUABLES_BASE_KEY_NAME + "UnusedInsuranceSum";
    // страховая премия по остальным (не перечисленным в списке) ценным вещам
    private static final String UNUSED_PREMIUM_SUM_KEY_NAME = VALUABLES_BASE_KEY_NAME + "UnusedPremiumSum";

    //private static final String REG_EXP_LATIN = "^[A-Za-z]+";
    // имена справочников калькулятора, сведения которых необходимы в angular-интерфейсе
    private static final String[] CALCULATOR_HANDBOOKS_NAMES = {
        "Ins.Sis.LimitTable",
        "Ins.Sis.TariffTable"
    };

    /**
     * Метод для загрузки справочников по продукту (может потребоваться для
     * angular-интерфейса).
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"PRODSYSNAME"})
    public Map<String, Object> dsB2BSISHandbooksBrowseEx(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BSISHandbooksBrowseEx");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> product = null;
        String productSysName = getStringParam(params.get("PRODSYSNAME"));
        if (productSysName.isEmpty()) {
            product = makeErrorResult("Не указано системное имя продукта (PRODSYSNAME).");
        } else {
            Map<String, Object> productVersionInfo = null;
            productVersionInfo = getProductVersionInfoIfNullBySingleParam(productVersionInfo, "PRODSYSNAME", productSysName, login, password);
            Long productVersionID = getLongParam(productVersionInfo.get("PRODVERID"));
            if (productVersionID == null) {
                product = makeErrorResult("Не удалось определить версию продукта (PRODVERID) по переданному системному имени продукта (PRODSYSNAME).");
            } else {
                Map<String, Object> configParams = new HashMap<String, Object>();
                configParams.put("PRODVERID", productVersionID);
                Long prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
                if (prodConfID == null) {
                    product = makeErrorResult("Не удалось определить идентификатор продукта (PRODCONFID) по переданному системному имени продукта (PRODSYSNAME).");
                } else {
                    Map<String, Object> productParams = new HashMap<String, Object>();
                    productParams.put("PRODCONFID", prodConfID);
                    productParams.put(RETURN_AS_HASH_MAP, true);

                    product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);

                    if (product == null) {
                        product = makeErrorResult("Не удалось получить сведения продукта по переданному системному имени продукта (PRODSYSNAME).");
                    } else {
                        Long calcVerID = getLongParam(product.get("CALCVERID"));

                        if (calcVerID == null) {
                            Map<String, Object> handbookErrorMap = makeErrorResult("Не удалось определить идентификатор калькулятора (CALCVERID) по переданному системному имени продукта (PRODSYSNAME).");
                            for (String calculatorHandbookName : CALCULATOR_HANDBOOKS_NAMES) {
                                result.put(calculatorHandbookName, handbookErrorMap);
                            }
                        } else {
                            // получение сведений необходимых справочников
                            for (String calculatorHandbookName : CALCULATOR_HANDBOOKS_NAMES) {
                                Map<String, Object> handbookDataResult = getCalculatorHandbookData(calcVerID, calculatorHandbookName, login, password);
                                Object handbookData;
                                if (handbookDataResult != null) {
                                    handbookData = handbookDataResult.get(RESULT);
                                } else {
                                    handbookData = makeErrorResult(String.format("Не удалось получить сведения справочника с именем (PRODSYSNAME) = %s, возможно данный справочник не связан с калькулятором продукта.", calculatorHandbookName));
                                }
                                result.put(calculatorHandbookName, handbookData);
                            }
                        }
                    }
                }
            }
        }

        //if (product.get("Error") != null) {
        //    // если сведения о продукте не требуются в интерфейсе - добавление в результат только описания ошибки (если она возникла)
        //    result.put("PRODMAP", product);
        //}
        //
        // сведения о продукте теперь требуются в интерфейсе - безусловное добавление в результат
        result.put("PRODMAP", product);

        logger.debug("after dsB2BSISHandbooksBrowseEx");
        return result;
    }

    private Map<String, Object> getCalculatorHandbookData(Long calcVerID, String calcHandbookName, String login, String password) throws Exception {
        Map<String, Object> сalculatorHBParams = new HashMap<String, Object>();
        сalculatorHBParams.put("CALCVERID", calcVerID);
        сalculatorHBParams.put("NAME", calcHandbookName);
        Map<String, Object> handbookDataResult = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", сalculatorHBParams, true, login, password);
        return handbookDataResult;
    }

    private Map<String, Object> getValuablesParamsMap(Map<String, Object> contract) {
        logger.debug("Searching for valuables insurance object...");
        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        if (insObjGroupList != null) {
            for (Map<String, Object> insObjGroup : insObjGroupList) {
                if (insObjGroup != null) {
                    List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroup.get("OBJLIST");
                    if (objList != null) {
                        for (Map<String, Object> obj : objList) {
                            if (obj != null) {
                                Map<String, Object> insObjMap = (Map<String, Object>) obj.get("INSOBJMAP");
                                if (insObjMap != null) {
                                    String insObjSysName = getStringParam(insObjMap.get("INSOBJSYSNAME"));
                                    if (("flatMovableProperty".equals(insObjSysName)) || ("movablePropertyObject".equals(insObjSysName))) {
                                        logger.debug("Searching finished - found valuables insurance object: " + insObjMap);
                                        Map<String, Object> contrObjMap = (Map<String, Object>) obj.get("CONTROBJMAP");
                                        if (contrObjMap != null) {
                                            insObjMap.put(TOTAL_INSURANCE_SUM_KEY_NAME, contrObjMap.get("INSAMVALUE"));
                                            insObjMap.put(TOTAL_PREMIUM_SUM_KEY_NAME, contrObjMap.get("PREMVALUE"));
                                        }
                                        //Long valuablesListed = getLongParam(insObjMap.get("valuablesListed"));
                                        //if ((valuablesListed != null) && (valuablesListed == 1L)) {
                                        //    logger.debug("Insurance object contain detailed valuables list.");
                                        //} else {
                                        //    logger.debug("Insurance object do not contain detailed valuables list.");
                                        //}
                                        return insObjMap;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        logger.debug("Searching finished - no valuables insurance object found.");
        return null;
    }

    private Map<String, Object> resolveValuablesStringedList(Map<String, Object> valuablesParamsMap) {

        if (valuablesParamsMap != null) {
            Long valuablesListed = getLongParam(valuablesParamsMap.get(LISTED_FLAG_KEY_NAME));
            if ((valuablesListed != null) && (valuablesListed == 1L)) {

                logger.debug("Insurance object contain detailed valuables list.");

                String fullListStr = getStringParam(valuablesParamsMap.get(LIST_JSON_STRING_KEY_NAME));
                //logger.debug("");
                logger.debug("Full list json for structure object: \n\n" + fullListStr + "\n\n");
                //logger.debug("");

                ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Double listSum = 0.0;

                if (fullListStr.length() > 4) {
                    // отрезка [{" и }]
                    fullListStr = fullListStr.substring(3, fullListStr.length() - 2);

                    String[] fullListArr = fullListStr.split(Pattern.quote("},{\""));

                    String strLimiter = "\"";
                    for (String itemStr : fullListArr) {
                        String[] itemPropertiesArr = itemStr.split(Pattern.quote(",\""));
                        Map<String, Object> item = new HashMap<String, Object>();
                        for (String itemPropery : itemPropertiesArr) {
                            if (itemPropery.endsWith(strLimiter)) {
                                itemPropery = itemPropery.substring(0, itemPropery.length() - 1);
                            }
                            String[] itemArr = itemPropery.split(Pattern.quote("\":"));
                            String itemKeyName = itemArr[0];
                            String itemValue = itemArr[1];
                            if (itemValue.startsWith(strLimiter)) {
                                itemValue = itemValue.substring(1, itemValue.length());
                            }

                            logger.debug("");
                            logger.debug("Item key name = " + itemKeyName);
                            logger.debug("Item value = " + itemValue);

                            item.put(itemKeyName, itemValue);

                        }
                        list.add(item);

                        Double cost = getDoubleParam(item.get("insAmValue"));
                        Double subSum = cost;
                        logger.debug("");
                        logger.debug(String.format("Item sum (insAmValue) = %.2f", subSum, cost));
                        listSum = listSum + subSum;

                    }

                }

                logger.debug("");

                logger.debug("Resulted list key name: " + LIST_KEY_NAME);
                valuablesParamsMap.put(LIST_KEY_NAME, list);

                logger.debug(String.format("List total sum (%s) = %.2f", DETAILED_INSURANCE_SUM_KEY_NAME, listSum));
                valuablesParamsMap.put(DETAILED_INSURANCE_SUM_KEY_NAME, listSum);
                logger.debug("");
            } else {
                logger.debug("Insurance object do not contain detailed valuables list.");
                logger.debug("");
            }
        }

        return valuablesParamsMap;

    }

    // вычисление дополнительных сумм (не хранящися в явном виде в b2b-договоре, но требующихся в полисе) по ценным вещам
    private Map<String, Object> calcAdditionalValuablesSum(Map<String, Object> valuablesParamsMap) {

        if (valuablesParamsMap != null) {

            // полная страховая сумма
            Double totalInsuranceSum = getDoubleParam(valuablesParamsMap.get(TOTAL_INSURANCE_SUM_KEY_NAME));
            logger.debug(TOTAL_INSURANCE_SUM_KEY_NAME + " = " + totalInsuranceSum);
            // полная страховая премия
            Double totalPremiumSum = getDoubleParam(valuablesParamsMap.get(TOTAL_PREMIUM_SUM_KEY_NAME));
            logger.debug(TOTAL_PREMIUM_SUM_KEY_NAME + " = " + totalPremiumSum);

            // страховая сумма по перечисленным в списке ценным вещам
            Double detailedInsuranceSum = getDoubleParam(valuablesParamsMap.get(DETAILED_INSURANCE_SUM_KEY_NAME));
            if (detailedInsuranceSum == null) {
                detailedInsuranceSum = 0.0;
            }
            logger.debug(DETAILED_INSURANCE_SUM_KEY_NAME + " = " + detailedInsuranceSum);

            // страховая сумма по остальным (не перечисленным в списке) ценным вещам
            Double unusedInsuranceSum = totalInsuranceSum - detailedInsuranceSum;
            valuablesParamsMap.put(UNUSED_INSURANCE_SUM_KEY_NAME, unusedInsuranceSum);
            logger.debug(UNUSED_INSURANCE_SUM_KEY_NAME + " = " + unusedInsuranceSum);

            // страховая премия по перечисленным в списке ценным вещам
            Double detailedPremiumSum = roundSum(totalPremiumSum * detailedInsuranceSum / totalInsuranceSum);
            valuablesParamsMap.put(DETAILED_PREMIUM_SUM_KEY_NAME, detailedPremiumSum);
            logger.debug(DETAILED_PREMIUM_SUM_KEY_NAME + " = " + detailedPremiumSum);

            // страховая премия по остальным (не перечисленным в списке) ценным вещам
            Double unusedPremiumSum = roundSum(totalPremiumSum - detailedPremiumSum);
            valuablesParamsMap.put(UNUSED_PREMIUM_SUM_KEY_NAME, unusedPremiumSum);
            logger.debug(UNUSED_PREMIUM_SUM_KEY_NAME + " = " + unusedPremiumSum);

        }

        return valuablesParamsMap;
    }

    /**
     * Метод для подготовки данных для отчета по продукту.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID"})
    public Map<String, Object> dsB2BSISPrintDocDataProvider(Map<String, Object> params) throws Exception {

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        // загрузка данных договора базовой версией поставщика
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBasePrintDocDataProvider", params, login, password);

        // преобразование строкового параметра (содержащего json) в список ценных вещей
        Map<String, Object> valuablesParamsMap = getValuablesParamsMap(result);
        resolveValuablesStringedList(valuablesParamsMap);
        // вычисление дополнительных сумм (не хранящися в явном виде в b2b-договоре, но требующихся в полисе) по ценным вещам
        calcAdditionalValuablesSum(valuablesParamsMap);

        // todo: генерация строковых представлений для сумм
        result.put(RETURN_AS_HASH_MAP, true);
        result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractTextSums", result, login, password);

        // todo: генерация дополнительных значений 
        //logger.debug("dsB2BSISPrintDocDataProvider result:\n\n" + result + "\n");
        result.put("PRODCONFID", params.get("PRODCONFID"));
        return result;
    }

    private boolean validateSaveParams(Map<String, Object> contract, String login, String password) throws Exception {

        StringBuffer errorText = new StringBuffer();

        //if (contract.get("STARTDATE") == null) {
        //    errorText.append("Не указана дата начала поездки (STARTDATE). ");
        //}
        //if (contract.get("SELECTEDRISKLIST") == null) {
        //    boolean isMigration = getBooleanParam(contract.get("ISMIGRATION"), false);
        //    if (isMigration) {
        //        contract.put("SELECTEDRISKLIST", ((Map<String, Object>) contract.get("CALCPARAMS")).get("RISKSYSNAMES"));
        //    } else {
        //        errorText.append("Не указан перечень выбранных рисков (SELECTEDRISKLIST). ");
        //    }
        //}
        // для текущего продукта не требуется - все суммы всегда в рублях
        //validateCurrencyRate(contract, errorText, login, password);
        Map<String, Object> insurer = (Map<String, Object>) contract.get("INSURERMAP");
        if (insurer == null) {
            errorText.append("Не указаны сведения о страхователе (INSURERMAP). ");
        } else {
            validateInsurerInfo(insurer, errorText);
        }

        Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (contractExtValues == null) {
            errorText.append("Не указаны расширенные атрибуты договора (CONTREXTMAP). ");
        } else {
            validateContractExtValues(contract, contractExtValues, errorText);
        }

        boolean isDataValid = errorText.length() == 0;
        if (!isDataValid) {
            errorText.append("Сведения договора не сохранены.");
            contract.put("Status", "Error");
            contract.put("Error", errorText.toString());
        }
        return isDataValid;
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

    private void validateContractExtValues(Map<String, Object> contract, Map<String, Object> contractExtValues, StringBuffer errorText) {
        if (contractExtValues.get("insObject") == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указан(о) ... (insObject). ");
        }
        if (contractExtValues.get("paymentWithoutSublimits") == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указан(о) ... (paymentWithoutSublimits). ");
        }
        if (contractExtValues.get("addRisks") == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указан(о) ... (addRisks). ");
        }
        if (contractExtValues.get("propertyAddressSt") == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указан(о) ... (propertyAddressSt). ");
        }
    }

    // генерация дополнительных вычисляемых сумм для продукта 'Защита всегда рядом: Дом'
    private void genAdditionalSISSumSaveParams(Map<String, Object> contract, Map<String, Object> product, String programSysName, boolean isVerboseLog, String login, String password) throws Exception {

        // валюта премии по договору - всегда рубли
        setOverridedParam(contract, "PREMCURRENCYID", 1, isVerboseLog);
        // страховая сумма тоже в рублях
        setOverridedParam(contract, "INSAMCURRENCYID", 1, isVerboseLog);
        Double currencyRate = 1.0D;
        setOverridedParam(contract, "CURRENCYRATE", currencyRate, isVerboseLog);

        Long calcVerID = getLongParam(product.get("CALCVERID"));
        logger.debug("Версия калькулятора (CALCVERID): " + calcVerID + ".");

        Map<String, Object> calcParams = prepareCalculatorParams(contract, calcVerID);

        Map<String, Object> calcRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "calculateByCalculatorVersionID", calcParams, login, password);
        logger.debug(makeIndentedTextLogBlock("calcRes", calcRes));

        updateContractInsuranceProductStructureSums(contract, calcRes);

    }

    // todo: возможно, перенести в Base-фасад
    private String getStringParamByFirstFoundName(Map<String, Object> map, String... paramNames) {
        String paramValue = "";
        if (map != null) {
            for (String paramName : paramNames) {
                paramValue = getStringParam(map.get(paramName));
                if (!paramValue.isEmpty()) {
                    return paramValue;
                }
            }
        }
        return paramValue;
    }

    private Map<String, Object> prepareCalculatorParams(Map<String, Object> contract, Long calcVerID) throws Exception {
        logger.debug("Preparing calculator parameters...");
        Map<String, Object> preparedCalcParams = new HashMap<String, Object>();

        // расширенные атрибуты договора
        Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
        logger.debug(makeIndentedTextLogBlock("contract.CONTREXTMAP", contractExtValues));

        preparedCalcParams.put("paymentWithoutSublimits", contractExtValues.get("paymentWithoutSublimits"));
        preparedCalcParams.put("addRisks", contractExtValues.get("addRisks"));

        List<Map<String, Object>> calcObjectList = new ArrayList<Map<String, Object>>();
        ArrayList<Map<String, Object>> insObjGroupList = (ArrayList<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        logger.debug(makeIndentedTextLogBlock("contract.INSOBJGROUPLIST", insObjGroupList));

        // требуется найти и запомнить группу со сведениями по основному дому, если такая имеется
        // будет использована в качестве источника параметров для вычислений сумм по движимому имуществу и ГО
        // (на величину премий по движимому имуществу и ГО, согласно ФТ, влияют свойства основного дома,
        //  хотя движимое имущество и ГО и представлены в b2b-структуре в виде двух отдельных независимых групп для вариантов со страхованием дома)
        Map<String, Object> mainHouseInsObjGroup = new HashMap<String, Object>();
        for (Map<String, Object> insObjGroup : insObjGroupList) {
            String insObjGroupSysName = getStringParamByFirstFoundName(insObjGroup, "INSOBJGROUPSYSNAME", "SYSNAME");
            if ("mainHouse".equals(insObjGroupSysName)) {
                mainHouseInsObjGroup = insObjGroup;
                break;
            }
        }

        // полный обход дерева структуры страхового продукта договора
        for (int i = 0; i < insObjGroupList.size(); i++) {

            Map<String, Object> insObjGroup = insObjGroupList.get(i);
            String insObjGroupSysName = getStringParamByFirstFoundName(insObjGroup, "INSOBJGROUPSYSNAME", "SYSNAME");
            logger.debug("Insurance group system name (INSOBJGROUPSYSNAME/SYSNAME): " + insObjGroupSysName);
            logger.debug("Insurance group index in groups list (B2BGROUPINDEX): " + i);

            // группа, используемая в качестве источника параметров для расчета премии
            Map<String, Object> insObjGroupWithMainCalcParams;
            if (("go".equals(insObjGroupSysName)) || ("movableProperty".equals(insObjGroupSysName))) {
                // для движимого имущества и ГО необходимо использовать другую отдельную группу - со сведениями по основному дому
                // (на величину премий по движимому имуществу и ГО, согласно ФТ, влияют свойства основного дома,
                //  хотя движимое имущество и ГО и представлены в b2b-структуре в виде двух отдельных независимых групп для вариантов со страхованием дома)
                insObjGroupWithMainCalcParams = mainHouseInsObjGroup;
                logger.debug(String.format(
                        "For main calculator parameters another insurance group will be used - with system name (INSOBJGROUPSYSNAME/SYSNAME) = '%s'.",
                        getStringParamByFirstFoundName(insObjGroupWithMainCalcParams, "INSOBJGROUPSYSNAME", "SYSNAME")
                ));
            } else {
                // для остальных случаев необходимо использовать ту же группу, в которой вычисляются суммы
                insObjGroupWithMainCalcParams = insObjGroup;
            }

            // В доме есть баня/сауна/камин/газ
            Long banyaSaunaFireplaceGas = getLongParam(insObjGroupWithMainCalcParams.get("banyaSaunaFireplaceGas"));
            logger.debug("banyaSaunaFireplaceGas: " + banyaSaunaFireplaceGas);
            // Год постройки
            Long productionYear = getLongParam(insObjGroupWithMainCalcParams.get("productionYear"));
            logger.debug("productionYear: " + productionYear);
            // Дом деревянный (только для домов)
            Long woodenHouse = getLongParam(insObjGroupWithMainCalcParams.get("woodenHouse"));
            logger.debug("woodenHouse: " + woodenHouse);
            // Есть дерево в перекрытиях (только для квартиры)
            Long woodInCeiling = getLongParam(insObjGroupWithMainCalcParams.get("woodInCeiling"));
            logger.debug("woodInCeiling: " + woodInCeiling);

            ArrayList<Map<String, Object>> objList = (ArrayList<Map<String, Object>>) insObjGroup.get("OBJLIST");
            for (int j = 0; j < objList.size(); j++) {
                Map<String, Object> obj = objList.get(j);
                Map<String, Object> contrObjMap = (Map<String, Object>) obj.get("CONTROBJMAP");
                Map<String, Object> insObjMap = (Map<String, Object>) obj.get("INSOBJMAP");

                String insObjSysName = getStringParamByFirstFoundName(insObjMap, "INSOBJSYSNAME", "SYSNAME");
                logger.debug("Insurance object system name (INSOBJSYSNAME/SYSNAME): " + insObjSysName);
                logger.debug("Insurance object index in objects list (B2BOBJECTINDEX): " + j);

                Map<String, Object> calcObject = new HashMap<String, Object>();
                //calcObject.put("OBJTYPESYSNAME", calcObjSysName);
                calcObject.put("INSOBJSYSNAME", insObjSysName);
                calcObject.put("banyaSaunaFireplaceGas", banyaSaunaFireplaceGas);
                calcObject.put("productionYear", productionYear);
                calcObject.put("woodenHouse", woodenHouse);
                calcObject.put("woodInCeiling", woodInCeiling);
                calcObject.put("INSAMVALUE", contrObjMap.get("INSAMVALUE"));
                calcObject.put("B2BGROUPINDEX", i);
                calcObject.put("B2BOBJECTINDEX", j);
                logger.debug(makeIndentedTextLogBlock("calcObject", calcObject));

                calcObjectList.add(calcObject);

            }
        }

        preparedCalcParams.put("OBJLIST", calcObjectList);
        preparedCalcParams.put("CALCVERID", calcVerID);
        preparedCalcParams.put(RETURN_AS_HASH_MAP, true);

        logger.debug(makeIndentedTextLogBlock("prepared CALCPARAMS", preparedCalcParams));
        logger.debug("Preparing calculator parameters finished.\n");

        return preparedCalcParams;

    }

    private Map<String, Object> updateContractInsuranceProductStructureSums(Map<String, Object> contract, Map<String, Object> calcB2BRes) throws Exception {

        logger.debug("Updating contract insurance product structure sums (by calculator call result data)...");

        Double contractInsAmValue = 0.0D;
        Double contractPremValue = 0.0D;
        ArrayList<Map<String, Object>> insObjGroupList = (ArrayList<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        List<Map<String, Object>> calcObjectList = (List<Map<String, Object>>) calcB2BRes.get("OBJLIST");
        for (Map<String, Object> object : calcObjectList) {

            Double calcRiskInsAmValue = roundSum(getDoubleParam(object.get("INSAMVALUE")));
            Double calcRiskPremium = roundSum(getDoubleParam(object.get("PREMIUM")));

            boolean isSumsUpdated = false;
            Long b2bGroupIndex = getLongParam(object.get("B2BGROUPINDEX"));
            logger.debug("Insurance group index in groups list (B2BGROUPINDEX): " + b2bGroupIndex);
            if ((b2bGroupIndex != null) && (b2bGroupIndex.intValue() < insObjGroupList.size())) {
                Map<String, Object> b2bTargetGroup = insObjGroupList.get(b2bGroupIndex.intValue());
                if (b2bTargetGroup != null) {
                    ArrayList<Map<String, Object>> objList = (ArrayList<Map<String, Object>>) b2bTargetGroup.get("OBJLIST");
                    if (objList != null) {
                        Long b2bObjectIndex = getLongParam(object.get("B2BOBJECTINDEX"));
                        logger.debug("Insurance object index in objects list (B2BOBJECTINDEX): " + b2bObjectIndex);
                        if ((b2bObjectIndex != null) && (b2bObjectIndex.intValue() < objList.size())) {
                            Map<String, Object> b2bTargetObject = objList.get(b2bObjectIndex.intValue());
                            if (b2bTargetObject != null) {
                                Map<String, Object> b2bTargetInsObjMap = (Map<String, Object>) b2bTargetObject.get("INSOBJMAP");
                                Map<String, Object> b2bTargetContrObjMap = (Map<String, Object>) b2bTargetObject.get("CONTROBJMAP");
                                if (b2bTargetContrObjMap != null) {
                                    logger.debug(String.format(
                                            "Updating sums in found b2b-object with system name (INSOBJSYSNAME/SYSNAME) = '%s'...",
                                            getStringParamByFirstFoundName(b2bTargetInsObjMap, "INSOBJSYSNAME", "SYSNAME")
                                    ));
                                    setOverridedParam(b2bTargetContrObjMap, "INSAMVALUE", calcRiskInsAmValue, true);
                                    setOverridedParam(b2bTargetContrObjMap, "PREMVALUE", calcRiskPremium, true);
                                    ArrayList<Map<String, Object>> b2bTargetRiskList = (ArrayList<Map<String, Object>>) b2bTargetContrObjMap.get("CONTRRISKLIST");
                                    // для текущего продукта всегда один риск у любого из объектов
                                    if ((b2bTargetRiskList != null) && (b2bTargetRiskList.size() == 1)) {
                                        Map<String, Object> b2bTargetRisk = b2bTargetRiskList.get(0);
                                        if (b2bTargetRisk != null) {
                                            logger.debug(String.format(
                                                    "Updating sums in found b2b-risk with system name (PRODRISKSYSNAME/SYSNAME) = '%s'...",
                                                    getStringParamByFirstFoundName(b2bTargetRisk, "PRODRISKSYSNAME", "SYSNAME")
                                            ));
                                            setOverridedParam(b2bTargetRisk, "INSAMVALUE", calcRiskInsAmValue, true);
                                            setOverridedParam(b2bTargetRisk, "PREMVALUE", calcRiskPremium, true);
                                            isSumsUpdated = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!isSumsUpdated) {
                logger.error("No b2b-object or b2b-risk found for this indexes, updating sum failed - some sums data will be used from interface!");
            }

            contractInsAmValue += calcRiskInsAmValue;
            contractPremValue += calcRiskPremium;
        }

        logger.debug("Updating main total sums in contract...");
        contractInsAmValue = roundSum(contractInsAmValue);
        logger.debug("Total contract insurance sum value: " + contractInsAmValue);
        contractPremValue = roundSum(contractPremValue);
        logger.debug("Total contract premium sum value: " + contractPremValue);
        setOverridedParam(contract, "INSAMVALUE", contractInsAmValue, true);
        setOverridedParam(contract, "PREMVALUE", contractPremValue, true);
        logger.debug("Updating main total sums in contract finished.");

        logger.debug("Updating contract insurance product structure sums (by calculator call result data) finished.\n");

        return contract;

    }

    // todo: возможно, перенести в Base-фасад
    private String makeIndentedTextLogBlock(Object object) {
        return makeIndentedTextLogBlock(object, null, "", new StringBuilder()).toString();
    }

    // todo: возможно, перенести в Base-фасад
    private String makeIndentedTextLogBlock(String objectName, Object object) {
        return makeIndentedTextLogBlock(object, null, objectName, new StringBuilder()).toString();
    }

    // todo: возможно, перенести в Base-фасад
    private StringBuilder makeIndentedTextLogBlock(Object object, String indent, String objectName, StringBuilder sb) {
        if (objectName == null) {
            objectName = "";
        }
        if (indent != null) {
            indent = indent + "\t";
            sb.append("\n");
        } else {
            indent = "";
        }
        sb.append(indent).append(objectName).append(" [");
        if (object == null) {
            sb.append("?]: null");
        } else {
            sb.append(object.getClass().getName()).append("]: ");
            if (object instanceof Map) {
                Map<String, Object> objectAsMap = (Map<String, Object>) object;
                for (Map.Entry<String, Object> entry : objectAsMap.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    makeIndentedTextLogBlock(value, indent, key, sb);
                }
                sb.append("\n");
            } else if (object instanceof List) {
                List<Object> objectAsList = (List<Object>) object;
                for (int i = 0; i < objectAsList.size(); i++) {
                    Object item = objectAsList.get(i);
                    makeIndentedTextLogBlock(item, indent, Integer.toString(i), sb);
                }
                sb.append("\n");
            } else {
                sb.append(object.toString());
            }
        }
        return sb;
    }

    protected Map<String, Object> genAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {

        boolean isParamsChangingLogged = logger.isDebugEnabled();

        // идентификатор версии продукта всегда передается в явном виде из b2bContrSave
        Long prodVerID = getLongParam(contract.get("PRODVERID"));

        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            setGeneratedParam(contract, "PRODCONFID", prodConfID, isParamsChangingLogged);
        }

        // инициализация даты документа
        GregorianCalendar documentDateGC = getOrGenerateDocumentDate(contract);

        // дата начала действия полиса
        GregorianCalendar startDateGC = new GregorianCalendar();
        startDateGC.setTime(documentDateGC.getTime());
        startDateGC.add(Calendar.DATE, 1);
        setOverridedParam(contract, "STARTDATE", startDateGC.getTime(), isParamsChangingLogged);

        // todo: исправить для SIS
        // дата окончания действия полиса
        GregorianCalendar finishDateGC = new GregorianCalendar();
        finishDateGC.setTime(startDateGC.getTime());
        finishDateGC.add(Calendar.YEAR, 1);
        finishDateGC.add(Calendar.DATE, 0);
        setOverridedParam(contract, "FINISHDATE", finishDateGC.getTime(), isParamsChangingLogged);

        // безусловное перевычисление срока действия
        long startDateInMillis = startDateGC.getTimeInMillis();
        long finishDateInMillis = finishDateGC.getTimeInMillis();
        long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000)); // в сутках (24*60*60*1000) милисекунд
        // duration -= 1; // вычитать один день не требуется - уже учтено при отбросе дробной части выше
        setOverridedParam(contract, "DURATION", duration, isParamsChangingLogged);

        // список типов объектов - выбор (если уже существует в договоре) или создание нового
        Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        ArrayList<Map<String, Object>> insObjGroupList;
        if (insObjGroupListFromContract == null) {
            insObjGroupList = new ArrayList<Map<String, Object>>();
            contract.put("INSOBJGROUPLIST", insObjGroupList);
        } else {
            insObjGroupList = (ArrayList<Map<String, Object>>) insObjGroupListFromContract;
        }

        // получение сведений о продукте (по идентификатору конфигурации продукта)
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODCONFID", prodConfID);
        productParams.put("HIERARCHY", false);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);

        // расширенные атрибуты договора
        Object contractExt = contract.get("CONTREXTMAP");
        Map<String, Object> contractExtValues;
        if (contractExt != null) {
            contractExtValues = (Map<String, Object>) contractExt;
        } else {
            contractExtValues = new HashMap<String, Object>();
            contract.put("CONTREXTMAP", contractExtValues);
        }

        // определение идентификатора справочника расширенных атрибутов договора на основании сведений о продукте
        Object contrExtMapHBDataVerID = product.get("HBDATAVERID");
        contractExtValues.put("HBDATAVERID", contrExtMapHBDataVerID);

        // установка системного имени программы
        String programSysName = DEFAULT_AND_ONLY_PROGRAM_SYSTEM_NAME_SIS;
        setOverridedParam(contract, "PRODPROGSYSNAME", programSysName, isParamsChangingLogged);

        // определение идентификатора и кода программы по её системному имени на основании сведений о продукте
        Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
        ArrayList<Map<String, Object>> prodProgs = (ArrayList<Map<String, Object>>) prodVer.get("PRODPROGS");
        Map<String, Object> program = (Map<String, Object>) getLastElementByAtrrValue(prodProgs, "SYSNAME", programSysName);
        Long programID = getLongParam(program.get("PRODPROGID"));
        setOverridedParam(contract, "PRODPROGID", programID, isParamsChangingLogged);
        String programCode = getStringParam(program.get("PROGCODE"));
        setOverridedParam(contract, "PRODPROGCODE", programCode, isParamsChangingLogged);

        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор
        boolean isMissingStructsCreated = false;
        updateContractInsuranceProductStructure(contract, product, false, programCode, isMissingStructsCreated, login, password);

        // генерация сумм
        genAdditionalSISSumSaveParams(contract, product, programSysName, isParamsChangingLogged, login, password);

        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        Object currentRowStatus = contract.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
            contract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
        }

        return contract;
    }

    private void genAdditionalSaveParamsFixContr(Map<String, Object> contract, String login, String password) throws Exception {
        boolean isParamsChangingLogged = logger.isDebugEnabled();
        // идентификатор версии продукта всегда передается в явном виде из b2bContrSave
        Long prodVerID = getLongParam(contract.get("PRODVERID"));
        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            setGeneratedParam(contract, "PRODCONFID", prodConfID, isParamsChangingLogged);
        }
        GregorianCalendar documentDateGC = new GregorianCalendar();
        Object documentDate = contract.get("DOCUMENTDATE");
        documentDateGC.setTime((Date) parseAnyDate(documentDate, Date.class, "DOCUMENTDATE"));
        GregorianCalendar startDateGC = new GregorianCalendar();
        Object startDate = contract.get("STARTDATE");
        startDateGC.setTime((Date) parseAnyDate(startDate, Date.class, "STARTDATE"));
        GregorianCalendar finishDateGC = new GregorianCalendar();
        Object finishDate = contract.get("FINISHDATE");
        finishDateGC.setTime((Date) parseAnyDate(finishDate, Date.class, "FINISHDATE"));

        // безусловное перевычисление срока действия
        long startDateInMillis = startDateGC.getTimeInMillis();
        long finishDateInMillis = finishDateGC.getTimeInMillis();
        long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000)); // в сутках (24*60*60*1000) милисекунд
        // duration -= 1; // вычитать один день не требуется - уже учтено при отбросе дробной части выше
        setOverridedParam(contract, "DURATION", duration, isParamsChangingLogged);

        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        Object currentRowStatus = contract.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
            contract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
        }
    }

    // инициализация даты документа
    private GregorianCalendar getOrGenerateDocumentDate(Map<String, Object> contract) {
        GregorianCalendar documentDateGC = new GregorianCalendar();
        Object docDate = contract.get("DOCUMENTDATE");
        if (docDate == null) {
            documentDateGC.setTime(new Date());
            documentDateGC.set(Calendar.HOUR_OF_DAY, 0);
            documentDateGC.set(Calendar.MINUTE, 0);
            documentDateGC.set(Calendar.SECOND, 0);
            documentDateGC.set(Calendar.MILLISECOND, 0);
            setGeneratedParam(contract, "DOCUMENTDATE", documentDateGC.getTime(), logger.isDebugEnabled());
        } else {
            //logger.debug("DOCDATE-" + docDate);
            documentDateGC.setTime((Date) parseAnyDate(docDate, Date.class, "DOCUMENTDATE"));
        }
        return documentDateGC;
    }

    /**
     * Метод для сохранения договора по продукту.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BSISContractPrepareToSave(Map<String, Object> params) throws Exception {

        logger.debug("before dsB2BSISContractPrepareToSave");

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }

        boolean isDataValid = validateSaveParams(contract, login, password);
        //boolean isDataValid = true; //!только для отладки!

        Map<String, Object> result;
        if (isDataValid) {
            genAdditionalSaveParams(contract, login, password);
            result = contract;
        } else {
            result = contract;
        }

        logger.debug("after dsB2BSISContractPrepareToSave");

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BSISContractPrepareToSaveFixContr(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BSISContractPrepareToSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        boolean isDataValid = validateSaveParams(contract, login, password);
        //boolean isDataValid = true; //!только для отладки!
        Map<String, Object> result;
        if (isDataValid) {
            genAdditionalSaveParamsFixContr(contract, login, password);
            result = contract;
        } else {
            result = contract;
        }
        if ((null != params.get("is1CExported")) && ((Boolean) params.get("is1CExported"))) {
            if ((null != params.get("b2bCorrector1C")) && ((Boolean) params.get("b2bCorrector1C"))) {
                result = contract;
            } else             
            if ((null != params.get("isCorrector")) && ((Boolean) params.get("isCorrector"))) {
                contract.remove("DURATION");
                contract.remove("FINISHDATETIME");
                contract.remove("FINISHDATE");
                contract.remove("STARTDATETIME");
                contract.remove("STARTDATE");
                contract.remove("CRMDOCLIST");
                contract.remove("INSURERID");
                contract.remove("INSURERMAP");
                result = contract;
            } else {
                // Если договор выгружен в 1С и у пользователя нет прав корректора запрещем что либо сохранять.
                logger.debug("after dsB2BSISContractPrepareToSave");
                result = new HashMap < String, Object > ();
            }
        } 
        logger.debug("after dsB2BSISContractPrepareToSave");
        return result;
    }

    /**
     * Метод для загрузки договора по продукту.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BSISContrLoad(Map<String, Object> params) throws Exception {

        logger.debug("before dsB2BSISContrLoad");

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> loadResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", params, login, password);

        logger.debug("after dsB2BSISContrLoad");

        return loadResult;

    }

    @WsMethod(requiredParams = {"CONTRMAP"})
    // todo: копия из VZR, для SIS нужно перепроверить (возможно не требуется вообще) и исправить
    public Map<String, Object> dsB2BSISContractApplyDiscount(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract = (Map<String, Object>) params.get("CONTRMAP");
        boolean isParamsChangingLogged = logger.isDebugEnabled();

        Map<String, Object> productVersionInfo = null;
        Long productVersionID = getLongParam(contract.get("PRODVERID"));
        String productSysName = getStringParam(contract.get("PRODSYSNAME"));
        if (productVersionID == null) {

            if (productSysName.isEmpty()) {
                return makeErrorResult("Не указаны ни версия (PRODVERID) ни системное имя (PRODSYSNAME) продукта - договор не создан.");
            } else {
                logger.debug("Не указана версия продукта (PRODVERID) - будет определена по переданному системному имени продукта (PRODSYSNAME).\n");
                productVersionInfo = getProductVersionInfoIfNullBySingleParam(productVersionInfo, "PRODSYSNAME", productSysName, login, password);
                productVersionID = getLongParam(productVersionInfo.get("PRODVERID"));
                if (productVersionID == null) {
                    return makeErrorResult("Не удалось определить версию продукта (PRODVERID) по переданному системному имени продукта (PRODSYSNAME) - договор не создан.");
                }
            }

        } else {

            if (!productSysName.isEmpty()) {
                logger.debug("И версия (PRODVERID) и системное имя (PRODSYSNAME) продукта указаны в явном виде - будет выполнена проверка их соответствия по сведениям из БД.\n");
            } else {
                logger.debug("Не указано системное имя продукта (PRODSYSNAME) - будет определено по переданной версии продукта (PRODVERID).\n");
            }

            productVersionInfo = getProductVersionInfoIfNullBySingleParam(productVersionInfo, "PRODVERID", productVersionID, login, password);
            String productSysNameByProdVerID = getStringParam(productVersionInfo.get("PRODSYSNAME"));

            if (productSysName.isEmpty()) {
                if (productSysNameByProdVerID.isEmpty()) {
                    //return makeErrorResult("Не удалось определить системное имя продукта (PRODSYSNAME) по версии продукта (PRODVERID) - возможно, договор будет создан с ошибками.");
                    logger.debug("Не удалось определить системное имя продукта (PRODSYSNAME) по версии продукта (PRODVERID) - возможно, договор будет создан с ошибками.\n");
                } else {
                    productSysName = productSysNameByProdVerID;
                }
            } else if (!productSysName.equalsIgnoreCase(productSysNameByProdVerID)) {
                return makeErrorResult("Указанная версия продукта (PRODVERID) не соответствует переданному системному имени (PRODSYSNAME) - договор не создан.");
            }

        }

        // идентификатор версии продукта всегда передается в явном виде с интерфейса
        contract.put("PRODVERID", productVersionID);
        Long prodVerID = getLongParam(contract.get("PRODVERID"));
        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            setGeneratedParam(contract, "PRODCONFID", prodConfID, isParamsChangingLogged);
        }
        // получение сведений о продукте (по идентификатору конфигурации продукта)
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODCONFID", prodConfID);
        productParams.put("HIERARCHY", false);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);
        Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
        String programCode = getStringParam(contractExtValues.get("insuranceProgram"));
        // определение идентификатора программы по её коду на основании сведений о продукте
        Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
        ArrayList<Map<String, Object>> prodProgs = (ArrayList<Map<String, Object>>) prodVer.get("PRODPROGS");
        Map<String, Object> program = (Map<String, Object>) getLastElementByAtrrValue(prodProgs, "PROGCODE", programCode);
        Long programID = getLongParam(program.get("PRODPROGID"));
        setOverridedParam(contract, "PRODPROGID", programID, isParamsChangingLogged); //contract.put("PRODPROGID", programID);
        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор
        updateContractInsuranceProductStructure(contract, product, false, programCode, login, password);
        // Определение страховых сумм по системным именам рисков
        Double currencyRate = getDoubleParam(contract.get("CURRENCYRATE"));
        Long calcVerID = getLongParam(product.get("CALCVERID"));
        List<Map<String, Object>> riskList = (List<Map<String, Object>>) contract.get("SELECTEDRISKLIST");
        Map<String, Double> premListAsMapBySysName = new HashMap<String, Double>();
        for (Map<String, Object> risk : riskList) {
            String riskSysName = getStringParam(risk.get("PRODRISKSYSNAME"));
            Double totalSum = getDoubleParam(risk.get("PREMIUM"));
            Double roundedTotalSum = roundSum(totalSum);
            premListAsMapBySysName.put(riskSysName, roundedTotalSum);
        }
        // todo: изменить?
        //updateContractInsuranceProductStructureSums(contract, premListAsMapBySysName, null, currencyRate, Boolean.FALSE);
        // применение промокода
        Map<String, Object> discParams = new HashMap<String, Object>();
        discParams.put("CONTRMAP", contract);
        discParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> discRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BApplyDiscountToContractMap", discParams, login, password);
        //
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CONTRMAP", discRes.get("CONTRMAP"));
        return result;
    }
}
