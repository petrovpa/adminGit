package com.bivgroup.services.b2bposws.facade.pos.custom.vzr;

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
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsConstants;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.services.inscore.system.WsUtils;

/**
 *
 * @author averichevsm
 */
@BOName("VZRCustom")
public class VZRCustomFacade extends ProductContractCustomFacade {

    private final Logger logger = Logger.getLogger(VZRCustomFacade.class);

    private static final String RISKLIMITHBDATAVERID_PARAMNAME = "RISKLIMITHBDATAVERID";

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    private static final String THIS_SERVICE_NAME = B2BPOSWS_SERVICE_NAME;
    private static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;
    private static final String REFWS_SERVICE_NAME = Constants.REFWS;
    private static final String COREWS_SERVICE_NAME = Constants.COREWS;
    private static final String WEBSMSWS_SERVICE_NAME = Constants.WEBSMSWS;

    // Свойства полиса, используемые в расширенных атрибутах договора
    // Признак годового полиса (CONTREXTMAP.annualPolicy): 0 - не годовой; 1 - годовой
    private static final long POLICY_IS_NOT_ANNUAL = 0L; // полис не годовой
    private static final long POLICY_IS_ANNUAL = 1L; // годовой полис
    // Вариант годового полиса (CONTREXTMAP.annualPolicyType): 0 - 90 дней; 1 - 365 дней
    private static final long ANNUAL_POLICY_TYPE_90_DAYS = 0L; // годовой полис на 90 дней
    private static final long ANNUAL_POLICY_TYPE_1_YEAR = 1L; // годовой полис на полный год

    // Виды полиса, используемые калькулятором
    private static final long TRAVEL_KIND_NOT_ANNUAL = 0L; // полис не годовой
    private static final long TRAVEL_KIND_1_YEAR = 1L; // годовой полис на полный год
    private static final long TRAVEL_KIND_90_DAYS = 2L; // годовой полис на 90 дней

    private static final String REG_EXP_LATIN = "^[A-Za-z-]+";

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
    public Map<String, Object> dsB2BVZRHandbooksBrowseEx(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BVZRHandbooksBrowseEx");
        //Map<String, Object> hbMapIn = (Map<String, Object>) params.get("HBMAP");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> product = null;
        //List<Map<String, Object>> riskLimitsList = null;
        Object riskLimitsList = null;
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
                            riskLimitsList = makeErrorResult("Не удалось определить идентификатор калькулятора (CALCVERID) по переданному системному имени продукта (PRODSYSNAME).");
                        } else {
                            riskLimitsList = getB2BVZRRiskLimitsFullList(calcVerID, login, password);
                        }
                    }
                }
            }
        }

        result.put("PRODMAP", product);
        result.put("riskLimitsList", riskLimitsList);

        logger.debug("after dsB2BVZRHandbooksBrowseEx");
        return result;
    }

    private void loadRiskLimits(Map<String, Object> contrMap, String login, String password) throws Exception {
        Map<String, Object> getProdConfIDParams = new HashMap<String, Object>();
        getProdConfIDParams.put("PRODVERID", contrMap.get("PRODVERID"));
        Long prodConfId = (Long) this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductConfigBrowseListByParam", getProdConfIDParams, login, password, "PRODCONFID");
        Map<String, Object> defParams = new HashMap<String, Object>();
        defParams.put(RETURN_AS_HASH_MAP, "TRUE");
        defParams.put("PRODCONFID", prodConfId);
        defParams.put("NAME", RISKLIMITHBDATAVERID_PARAMNAME);
        Long hbDataVerId = getLongParam(this.callServiceAndGetOneValue(Constants.B2BPOSWS, "dsB2BProductDefaultValueBrowseListByParam", defParams, login, password, "VALUE"));
        if (hbDataVerId != null) {
            Map<String, Object> contrExtMap = (Map<String, Object>) contrMap.get("CONTREXTMAP");
            if ((contrExtMap != null) && (contrExtMap.get("insuranceTerritory") != null) && (contrExtMap.get("annualPolicy") != null)
                    && (contrExtMap.get("insuranceProgram") != null)) {
                Long insuranceTerritory = Long.valueOf(contrExtMap.get("insuranceTerritory").toString());
                String insuranceTerritoryS;
                switch (insuranceTerritory.intValue()) {
                    case 0:
                        insuranceTerritoryS = "NoUSARF";
                        break;
                    case 1:
                        insuranceTerritoryS = "NoRF";
                        break;
                    case 2:
                        insuranceTerritoryS = "RFSNG";
                        break;
                    case 3:
                        insuranceTerritoryS = "NoUSA";
                        break;
                    default:
                        insuranceTerritoryS = "unknown";
                }
                Long annualPolicy = Long.valueOf(contrExtMap.get("annualPolicy").toString());
                Long prodProgId = Long.valueOf(contrMap.get("PRODPROGID").toString());
                Map<String, Object> progParams = new HashMap<String, Object>();
                progParams.put(RETURN_AS_HASH_MAP, "TRUE");
                progParams.put("PRODPROGID", prodProgId);
                Map<String, Object> progRes = this.callService(Constants.B2BPOSWS, "dsB2BProductProgramBrowseListByParam", progParams, login, password);
                Double progInsAmValue = Double.valueOf(progRes.get("INSAMVALUE").toString());
                String insuranceProgramS = "unknown";
                if (Math.abs(progInsAmValue.doubleValue() - 15000.0) < 0.0001) {
                    insuranceProgramS = "VZR_RFCLASSIC";
                }
                if (Math.abs(progInsAmValue.doubleValue() - 30000.0) < 0.0001) {
                    insuranceProgramS = "VZR_BASIC";
                }
                if (Math.abs(progInsAmValue.doubleValue() - 60000.0) < 0.0001) {
                    insuranceProgramS = "VZR_CLASSIC";
                }
                if (Math.abs(progInsAmValue.doubleValue() - 120000.0) < 0.0001) {
                    insuranceProgramS = "VZR_PREMIUM";
                }
                Map<String, Object> hbParams = new HashMap<String, Object>();
                hbParams.put("HBDATAVERID", hbDataVerId);
                hbParams.put("territorySysName", insuranceTerritoryS);
                hbParams.put("travelKind", annualPolicy);
                hbParams.put("programSysName", insuranceProgramS);
                Map<String, Object> hbRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", hbParams, login, password);
                if ((hbRes != null) && (hbRes.get(RESULT) != null) && (((List<Map<String, Object>>) hbRes.get(RESULT)).size() > 0)) {
                    List<Map<String, Object>> limitList = (List<Map<String, Object>>) hbRes.get(RESULT);
                    if (contrMap.get("INSOBJGROUPLIST") != null) {
                        List<Map<String, Object>> insObjGroupList = (List<Map<String, Object>>) contrMap.get("INSOBJGROUPLIST");
                        for (Map<String, Object> insObjGroupBean : insObjGroupList) {
                            if (insObjGroupBean.get("OBJLIST") != null) {
                                List<Map<String, Object>> objList = (List<Map<String, Object>>) insObjGroupBean.get("OBJLIST");
                                for (Map<String, Object> objBean : objList) {
                                    if (objBean.get("CONTROBJMAP") != null) {
                                        Map<String, Object> contrObjMap = (Map<String, Object>) objBean.get("CONTROBJMAP");
                                        if (contrObjMap.get("CONTRRISKLIST") != null) {
                                            List<Map<String, Object>> contrRiskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                                            for (Map<String, Object> riskBean : contrRiskList) {
                                                String riskSysName = riskBean.get("PRODRISKSYSNAME").toString();
                                                List<Map<String, Object>> limitData = new ArrayList<Map<String, Object>>();
                                                for (Map<String, Object> lBean : limitList) {
                                                    if (lBean.get("riskSysName").toString().equalsIgnoreCase(riskSysName)) {
                                                        limitData.add(lBean);
                                                    }
                                                }
                                                riskBean.put("LIMITLIST", limitData);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
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
    public Map<String, Object> dsB2BVZRPrintDocDataProvider(Map<String, Object> params) throws Exception {

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        // загрузка данных договора базовой версией поставщика
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBasePrintDocDataProvider", params, login, password);

        // todo: генерация строковых представлений для сумм
        result.put(RETURN_AS_HASH_MAP, true);
        result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractTextSums", result, login, password);

        // загрузка спиков лимитов по рискам
        loadRiskLimits(result, login, password);
        // todo: генерация дополнительных значений (после предоставления шаблонов печатного документа)

        //logger.debug("dsB2BVZRPrintDocDataProvider result:\n\n" + result + "\n");
        result.put("PRODCONFID", params.get("PRODCONFID"));
        return result;
    }

    private boolean validateSaveParams(Map<String, Object> contract, boolean isFixContr, String login, String password) throws Exception {
        StringBuffer errorText = new StringBuffer();

        if (contract.get("STARTDATE") == null) {
            errorText.append("Не указана дата начала поездки (STARTDATE). ");
        }

        if (!isFixContr && (contract.get("SELECTEDRISKLIST") == null)) {
            boolean isMigration = getBooleanParam(contract.get("ISMIGRATION"), false);
            if (isMigration) {
                contract.put("SELECTEDRISKLIST", ((Map<String, Object>) contract.get("CALCPARAMS")).get("RISKSYSNAMES"));
            } else {
                errorText.append("Не указан перечень выбранных рисков (SELECTEDRISKLIST). ");
            }
        }


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
            boolean isAllAgesCountsExist = validateVZRContractExtValues(contract, contractExtValues, isFixContr, errorText);
            validateMembersList(contract, errorText, isAllAgesCountsExist, !isFixContr);
        }
        validateCurrencyRate(contract, errorText, login, password);

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

    private void validateCurrencyRate(Map<String, Object> contract, StringBuffer errorText, String login, String password) throws Exception {
        // валюта страховой суммы по договору - передается с интерфейса (аналогично старому варинату онлайн-продукта)
        Long insAmCurrencyID = getLongParam(contract.get("INSAMCURRENCYID"));
        if (insAmCurrencyID == null) {
            errorText.append("Не указана валюта страховых сумм (INSAMCURRENCYID). ");
        } else {
            Double currencyRateFromInterface = getDoubleParam(contract.get("CURRENCYRATE"));
            if (currencyRateFromInterface == 0.0) {
                errorText.append("Не указан курс валюты, использованный при вычислении отображаемых в интерфейсе сумм (CURRENCYRATE). ");
            } else {
                logger.debug("Сравнение курсов валют...");
                /*Date dateRate = new Date();
                boolean isMigration = getBooleanParam(contract.get("ISMIGRATION"), false);
                if (isMigration) {
                    dateRate = getDateParam(contract.get("DOCUMENTDATE"));
                }*/
                Date dateRate = (Date) parseAnyDate(contract.get("DOCUMENTDATE"), Date.class, "DOCUMENTDATE");
                //
                Double currencyRate = roundCurrencyRate(getExchangeRateByCurrencyID(insAmCurrencyID, dateRate, login, password));
                String currencyCompareResult;
                if (!currencyRateFromInterface.equals(currencyRate)) {
                    currencyCompareResult = "Курс валюты, использованный при вычислении отображаемых в интерфейсе сумм (CURRENCYRATE) не совпадает с текущим курсом (" + currencyRateFromInterface + " <> " + currencyRate + "). ";
                    errorText.append(currencyCompareResult);
                } else {
                    currencyCompareResult = "Курс валюты, использованный при вычислении отображаемых в интерфейсе сумм (CURRENCYRATE) совпадает с текущим курсом (" + currencyRateFromInterface + " = " + currencyRate + "). ";
                }
                logger.debug(currencyCompareResult);
            }
        }
    }

    private boolean validateVZRContractExtValues(Map<String, Object> contract, Map<String, Object> contractExtValues, boolean isFixContr, StringBuffer errorText) {
        Long annualPolicy = getLongParam(contractExtValues.get("annualPolicy"));
        if (annualPolicy == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указан тип полиса (annualPolicy). ");
        } else if (annualPolicy.intValue() == POLICY_IS_ANNUAL) {
            Long annualPolicyType = getLongParam(contractExtValues.get("annualPolicyType"));
            if (annualPolicyType == null) {
                errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указан вариант годового полиса (annualPolicyType). ");
            }
        } else if (contract.get("DURATION") == null) {
            errorText.append("Не указан срок действия полиса (DURATION). ");
        } else if ((contract.get("STARTDATE") != null) && (contract.get("FINISHDATE") != null)) {
            if (!isFixContr) {
                GregorianCalendar startDateGC = new GregorianCalendar();
                Object startDate = contract.get("STARTDATE");
                startDateGC.setTime((Date) parseAnyDate(startDate, Date.class, "STARTDATE"));

                GregorianCalendar finishDateGC = new GregorianCalendar();
                GregorianCalendar finishDateGC1 = new GregorianCalendar();
                if (annualPolicy == POLICY_IS_ANNUAL) {
                    // Вариант годового полиса (0 - 90 дней; 1 - 365 дней)
                    int annualPolicyType = getIntegerParam(contractExtValues.get("annualPolicyType"));
                    // для обоих вариантов годового полиса - безусловное перевычислине даты оконочания
                    int days = 0;
                    int years = 0;
                    if (annualPolicyType == ANNUAL_POLICY_TYPE_90_DAYS) {
                        days = 90;
                    } else {
                        years = 1;
                    }
                    finishDateGC.setTime(startDateGC.getTime());
                    finishDateGC.add(Calendar.YEAR, years);
                    finishDateGC.add(Calendar.DATE, days);
                    // даже если 90 дней - полис все равно годовой.
                    finishDateGC1.setTime(startDateGC.getTime());
                    finishDateGC1.add(Calendar.YEAR, 1);
                    finishDateGC1.add(Calendar.DATE, 0);
                } else {
                    // для негодового полиса дата окончания действия всегда передается с интерфейса
                    Object finishDate = contract.get("FINISHDATE");
                    finishDateGC.setTime((Date) parseAnyDate(finishDate, Date.class, "FINISHDATE"));
                    finishDateGC1 = finishDateGC;
                }
                long startDateInMillis = startDateGC.getTimeInMillis();
                long finishDateInMillis = finishDateGC.getTimeInMillis();
                long calcDuration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000)); // в сутках (24*60*60*1000) милисекунд
                long duration = getLongParam(contract.get("DURATION"));
                if (annualPolicy.longValue() != POLICY_IS_ANNUAL) {
                    calcDuration += 1;
                }
                if ((duration < 1) || (duration > calcDuration)) {
                    errorText.append("Неверное количество дней поездки. ");
                }
            }
        }
        if (contractExtValues.get("insuranceProgram") == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указана программа страхования (insuranceProgram). ");
        }
        if (contractExtValues.get("insuranceTerritory") == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указан регион действия полиса (insuranceTerritory). ");
        }
        if (contractExtValues.get("optionSport") == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указано наличие опции спорт (optionSport). ");
        }
        boolean isAllAgesCountsExist = true;
        if (contractExtValues.get("babes") == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указано количество застрахованных младенцев 0 - 2 года (babes). ");
            isAllAgesCountsExist = false;
        }
        if (contractExtValues.get("adultsAndChildren3_60") == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указано количество застрахованных взрослых и/или детей 3 - 60 лет (adultsAndChildren3_60). ");
            isAllAgesCountsExist = false;
        }
        if (contractExtValues.get("old61_70") == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указано количество застрахованных старшего возраста 61 - 70 лет (old61_70). ");
            isAllAgesCountsExist = false;
        }
        return isAllAgesCountsExist;
    }

    private void validateMembersList(Map<String, Object> contract, StringBuffer errorText, boolean isAllAgesCountsExist, boolean isNeedRecreateDocDate) {
        ArrayList<Map<String, Object>> membersList = (ArrayList<Map<String, Object>>) contract.get("MEMBERLIST");
        if ((membersList == null) || (membersList.isEmpty())) {
            errorText.append("Не указан или пуст перечень застрахованных (MEMBERLIST). ");
        } else if (isAllAgesCountsExist) {
            Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
            Integer babesCountFromContract = getIntegerParam(contractExtValues.get("babes"));
            Integer adultsAndChildrenCountFromContract = getIntegerParam(contractExtValues.get("adultsAndChildren3_60"));
            Integer oldCountFromContract = getIntegerParam(contractExtValues.get("old61_70"));
            Integer totalInsuredCount = babesCountFromContract + adultsAndChildrenCountFromContract + oldCountFromContract;
            if (membersList.size() != totalInsuredCount) {
                errorText.append("Количество застрахованных в перечене (MEMBERLIST) не совпадает с итоговым количеством застрахованных (babes + adultsAndChildren3_60 + old61_70) из расширенных атрибутов договора (CONTREXTMAP). ");
            } else {
                // инициализация даты документа
                GregorianCalendar documentDateGC = getOrGenerateDocumentDate(contract, isNeedRecreateDocDate);
                // дата документа минус 2 года
                // поправка - первый диапазон - от 0 до 2х лет включительно
                GregorianCalendar documentDateMinus2YearsGC = new GregorianCalendar();
                documentDateMinus2YearsGC.setTime(documentDateGC.getTime());
                documentDateMinus2YearsGC.add(Calendar.YEAR, -3);
                // дата документа минус 60 лет
                GregorianCalendar documentDateMinus60YearsGC = new GregorianCalendar();
                documentDateMinus60YearsGC.setTime(documentDateGC.getTime());
                documentDateMinus60YearsGC.add(Calendar.YEAR, -61);
                // дата документа минус 70 лет
                GregorianCalendar documentDateMinus70YearsGC = new GregorianCalendar();
                documentDateMinus70YearsGC.setTime(documentDateGC.getTime());
                documentDateMinus70YearsGC.add(Calendar.YEAR, -70);
                // объект под дату рождения конкретного застрахованного
                GregorianCalendar insurerBirthdateGC = new GregorianCalendar();
                // количества застрахованных по возрастам
                int babesCount = 0;
                int adultsAndChildrenCount = 0;
                int oldCount = 0;
                // флаг ошибочных (неуказанных и пр) дат рождения - если такие есть, то сверка количества застрахованных по возрастам нецелесообразна
                boolean isBirthdatesMissingOrOutOfRange = false;
                int insuranceTerritory = getLongParam(contractExtValues.get("insuranceTerritory")).intValue();
                for (int i = 0; i < membersList.size(); i++) {
                    Map<String, Object> member = membersList.get(i);
                    // проверка имени
                    Object memberName = member.get("NAME_ENG");
                    if (memberName == null) {
                        errorText.append("У ").append(i).append("-го застрахованного из списка (MEMBERLIST) не указано имя (NAME_ENG). ");
                    }
                    if (insuranceTerritory != 2) {
                        // кириллические символы в имени застрахованного недопустимы для любой территории страхования, кроме России (RFSNG, insuranceTerritory = 2)
                        if (checkIsValueInvalidByRegExp(memberName, REG_EXP_LATIN, false)) {
                            errorText.append("У ").append(i).append("-го застрахованного из списка (MEMBERLIST) имя (NAME_ENG) должно содержать только латиницу. ");
                        }
                    }
                    // проверка фамилии
                    Object memberSurname = member.get("SURNAME_ENG");
                    if (memberSurname == null) {
                        errorText.append("У ").append(i).append("-го застрахованного из списка (MEMBERLIST) не указана фамилия (SURNAME_ENG). ");
                    }
                    if (insuranceTerritory != 2) {
                        // кириллические символы в фамилии застрахованного недопустимы для любой территории страхования, кроме России (RFSNG, insuranceTerritory = 2)
                        if (checkIsValueInvalidByRegExp(memberSurname, REG_EXP_LATIN, false)) {
                            errorText.append("У ").append(i).append("-го застрахованного из списка (MEMBERLIST) фамилия (SURNAME_ENG) должна содержать только латиницу. ");
                        }
                    }
                    // проверка даты рождения
                    Object insurerBirthdate = member.get("BIRTHDATE");
                    if (insurerBirthdate == null) {
                        errorText.append("У ").append(i).append("-го застрахованного из списка (MEMBERLIST) не указана дата рождения (BIRTHDATE). ");
                        isBirthdatesMissingOrOutOfRange = true;
                    }
                    // если есть ошибки в датах рождения, то сверка количества застрахованных по возрастам нецелесообразна
                    if (!isBirthdatesMissingOrOutOfRange) {
                        // дата рождения застрахованного
                        Date insurerBirthdateDate = (Date) parseAnyDate(insurerBirthdate, Date.class, "BIRTHDATE");
                        insurerBirthdateGC.setTime(insurerBirthdateDate);
                        // сравнение с пограничными датами в прошлом
                        if (insurerBirthdateGC.after(documentDateMinus2YearsGC)) {
                            babesCount++;
                        } else if (insurerBirthdateGC.after(documentDateMinus60YearsGC)) {
                            adultsAndChildrenCount++;
                        } else if (insurerBirthdateGC.after(documentDateMinus70YearsGC)) {
                            oldCount++;
                        } else {
                            errorText.append("У ").append(i).append("-го застрахованного из списка (MEMBERLIST) возраст выходит за пределы допустимого. ");
                            isBirthdatesMissingOrOutOfRange = true;
                        }
                    }
                }
                // если есть ошибки в датах рождения, то сверка количества застрахованных по возрастам нецелесообразна
                if (!isBirthdatesMissingOrOutOfRange) {
                    if ((babesCount != babesCountFromContract) || (adultsAndChildrenCount != adultsAndChildrenCountFromContract) || (oldCount != oldCountFromContract)) {
                        errorText.append("Даты рождения застрахованных в перечене (MEMBERLIST) не соответсвует количеству застрахованных по возрастам (babes/adultsAndChildren3_60/old61_70) из расширенных атрибутов договора (CONTREXTMAP). ");
                    }
                }
            }
        }
    }

    @WsMethod(requiredParams = {"PRODSYSNAME", "CONTREXTMAP", "SELECTEDRISKLIST"})
    public Map<String, Object> dsB2BVZRCalculatePremValues(Map<String, Object> params) throws Exception {

        logger.debug("Определение страховых премий...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        //Map<String, Object> contract = (Map<String, Object>) params.get("CONTRMAP");
        Map<String, Object> contract = params;

        Map<String, Object> result;

        String productSysName = getStringParam(contract.get("PRODSYSNAME"));
        if (productSysName.isEmpty()) {
            result = makeErrorResult("Не указано системное имя продукта (PRODSYSNAME) - калькулятор не вызван.");
        } else {
            Map<String, Object> productVersionInfo = null;
            productVersionInfo = getProductVersionInfoIfNullBySingleParam(productVersionInfo, "PRODSYSNAME", productSysName, login, password);
            Long productVersionID = getLongParam(productVersionInfo.get("PRODVERID"));
            if (productVersionID == null) {
                result = makeErrorResult("Не удалось определить версию продукта (PRODVERID) по переданному системному имени продукта (PRODSYSNAME) - калькулятор не вызван.");
            } else {
                Map<String, Object> configParams = new HashMap<String, Object>();
                configParams.put("PRODVERID", productVersionID);
                Long calcVerID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "CALCVERID"));
                if (calcVerID == null) {
                    result = makeErrorResult("Не удалось определить идентификатор калькулятора продукта (CALCVERID) по переданному системному имени продукта (PRODSYSNAME) - калькулятор не вызван.");
                } else {
                    Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
                    if (contractExtValues == null) {
                        result = makeErrorResult("Не переданы расширенные атрибуты договора (CONTREXTMAP) - калькулятор не вызван.");
                    } else {
                        String programCode = getStringParam(contractExtValues.get("insuranceProgram"));
                        Map<String, Object> programParams = new HashMap<String, Object>();
                        programParams.put("PRODVERID", productVersionID);
                        programParams.put("PROGCODE", programCode);
                        String programSysName = getStringParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductProgramBrowseListByParam", programParams, login, password, "SYSNAME"));
                        if (programSysName.isEmpty()) {
                            result = makeErrorResult("Не удалось определить системное имя программы по коду программы (CONTREXTMAP.insuranceProgram) - калькулятор не вызван.");
                        } else {
                            Long annualPolicy = getLongParam(contractExtValues.get("annualPolicy"));
                            if (annualPolicy == null) {
                                result = makeErrorResult("В расширенных атрибутах договора не указан тип полиса (CONTREXTMAP.annualPolicy) - калькулятор не вызван.");
                            } else {
                                // основной результат калькулятора
                                Map<String, Object> calcResMain = vzrCalculatePremValues(contract, calcVerID, programSysName, login, password);
                                result = new HashMap<String, Object>();

                                // дополнительный коэффициент для расчета "спортивных" сумм в angular-интерфейсе
                                // (более не требуется, вместо него используется PREMIUMSPORTREVER в рисках)
                                //result.put("SPORTTARIFFENABLED", calcResMain.get("SPORTTARIFFENABLED"));
                                // главный список рисков с суммами
                                Object riskListMain = getRiskListOrErrorFromVZRCalcResult(calcResMain);

                                // валюта страховой суммы по договору - передается с интерфейса (аналогично старому варинату онлайн-продукта)
                                Long insAmCurrencyID = getLongParam(contract.get("INSAMCURRENCYID"));
                                Double currencyRate = roundCurrencyRate(getExchangeRateByCurrencyID(insAmCurrencyID, new Date(), login, password));
                                result.put("CURRENCYRATE", currencyRate);

                                // перевод сумм из валюты договора в рубли
                                multRisksSumsByCurrencyRate(riskListMain, currencyRate);

                                // перевод "спортивных" сумм (требуются для angular-интерфейса) из валюты договора в рубли
                                String sumKeyName = "PREMIUMSPORTREVER";
                                multRisksSumsByCurrencyRate(riskListMain, currencyRate, sumKeyName);

                                result.put("RISKLIST", riskListMain);
                                // дополнительный вызов калькулятора - расчет выгодного годового предложения, если выбранный полис - не годовой
                                if (annualPolicy.intValue() == POLICY_IS_NOT_ANNUAL) {
                                    contractExtValues.put("annualPolicy", POLICY_IS_ANNUAL);
                                    contractExtValues.put("annualPolicyType", ANNUAL_POLICY_TYPE_1_YEAR);
                                    Map<String, Object> calcResYearOffer = vzrCalculatePremValues(contract, calcVerID, programSysName, login, password);
                                    contractExtValues.put("annualPolicy", POLICY_IS_NOT_ANNUAL);
                                    //contractExtValues.remove("annualPolicyType");
                                    Object riskListYearOffer = getRiskListOrErrorFromVZRCalcResult(calcResYearOffer);
                                    multRisksSumsByCurrencyRate(riskListYearOffer, currencyRate);
                                    result.put("yearRISKLIST", riskListYearOffer);
                                }
                            }
                        }
                    }
                }
            }
        }

        String errorText = getStringParam(result.get("Error"));
        if (errorText.isEmpty()) {
            logger.debug("Определение страховых премий успешно завершено.");
        } else {
            logger.debug("Определение страховых премий завершено с ошибкой: " + errorText);
        }

        return result;
    }

    private void multRisksSumsByCurrencyRate(Object riskListMain, Double currencyRate) {
        // по-умолчанию на суммы указывает ключ с именем PREMIUM
        String sumKeyName = "PREMIUM";
        multRisksSumsByCurrencyRate(riskListMain, currencyRate, sumKeyName);
    }

    private void multRisksSumsByCurrencyRate(Object riskListMain, Double currencyRate, String sumKeyName) {
        logger.debug("Сonversion of insurance premiums on the risks in rubles...");
        logger.debug("Exchange currency rate: " + currencyRate);
        logger.debug("Sums key name: " + sumKeyName);
        String sumOrigKeyName = sumKeyName + "ORIG";
        logger.debug("Original sums key name: " + sumKeyName);
        if (riskListMain instanceof List) {
            ArrayList<Map<String, Object>> riskList = (ArrayList<Map<String, Object>>) riskListMain;
            for (Map<String, Object> risk : riskList) {
                String riskSysName = getStringParam(risk.get("PRODRISKSYSNAME"));
                logger.debug("Risk system name (PRODRISKSYSNAME) = " + riskSysName);
                Double riskSumOriginal = getDoubleParam(risk.get(sumKeyName));
                logger.debug("    Premium in the currency of the contract = " + riskSumOriginal);
                risk.put(sumOrigKeyName, riskSumOriginal);
                Double riskSum = roundSum(riskSumOriginal * currencyRate);
                logger.debug("    Premium in rubles = " + riskSum);
                risk.put(sumKeyName, riskSum);
            }
        }
        logger.debug("Сonversion of insurance premiums on the risks in rubles finished.");
    }

    private Object getRiskListOrErrorFromVZRCalcResult(Map<String, Object> calcResult) {
        Object result;
        if (calcResult == null) {
            result = null;
        } else if (isCallResultOK(calcResult)) {
            List<Map<String, Object>> insuredCalculatedList = (List<Map<String, Object>>) calcResult.get("insuredList");
            if ((insuredCalculatedList == null) || (insuredCalculatedList.size() != 1)) {
                result = makeErrorResult("В результате, возвращенном калькулятором, список застрахованных (insuredList) отсутствует или содержит не один элемент.");
            } else {
                Map<String, Object> insured = insuredCalculatedList.get(0);
                if (insured == null) {
                    result = makeErrorResult("В первом элементе списока застрахованных (insuredList), возвращенном калькулятором, отсутствуют какие-либо сведения.");
                } else {
                    result = (List<Map<String, Object>>) insured.get("riskList");
                }
            }
        } else {
            result = makeErrorResult("При вызове калькулятора возникла ошибка: " + calcResult);
        }
        return result;
    }

    private Map<String, Object> vzrCalculatePremValues(Map<String, Object> contract, Long calcVerID, String programSysName, String login, String password) throws Exception {

        Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");

        Map<String, Object> calcParams = new HashMap<String, Object>();

        calcParams.put("babesCount", getIntegerParam(contractExtValues.get("babes")));
        calcParams.put("adultsAndChildren3_60Count", getIntegerParam(contractExtValues.get("adultsAndChildren3_60")));
        calcParams.put("old61_70Count", getIntegerParam(contractExtValues.get("old61_70")));

        calcParams.put("CALCVERID", calcVerID);

        int duration = getIntegerParam(contractExtValues.get("dayCount"));
        if (duration == 0) {
            duration = getIntegerParam(contract.get("DURATION"));
        }
        calcParams.put("daysCount", duration);

        Long travelKind;
        if (getIntegerParam(contractExtValues.get("annualPolicy")) == POLICY_IS_NOT_ANNUAL) {
            travelKind = TRAVEL_KIND_NOT_ANNUAL;
        } else if (getIntegerParam(contractExtValues.get("annualPolicyType")) == ANNUAL_POLICY_TYPE_1_YEAR) {
            travelKind = TRAVEL_KIND_1_YEAR;
        } else {
            travelKind = TRAVEL_KIND_90_DAYS;
        }
        calcParams.put("travelKind", travelKind);

        calcParams.put("isSportEnabled", contractExtValues.get("optionSport"));

        String territorySysName;
        switch (getLongParam(contractExtValues.get("insuranceTerritory")).intValue()) {
            case 0:
                territorySysName = "NoUSARF";
                break;
            case 1:
                territorySysName = "NoRF";
                break;
            case 2:
                territorySysName = "RFSNG";
                break;
            case 3:
                territorySysName = "NoUSA";
                break;
            default:
                territorySysName = "NoUSARF";
        }
        calcParams.put("territorySysName", territorySysName);

        calcParams.put("programSysName", programSysName);

        // не используется в калькуляторе
        //calcParams.put("CURRENCYID", contract.get("INSAMCURRENCYID")); 
        List<Map<String, Object>> riskList = (List<Map<String, Object>>) contract.get("SELECTEDRISKLIST"); //prepareRiskList(riskSysNames);
        List<Map<String, Object>> insuredList = new ArrayList();
        Map<String, Object> insuredMap = new HashMap<String, Object>();
        insuredMap.put("riskList", riskList);
        insuredList.add(insuredMap);
        calcParams.put("insuredList", insuredList);

        contract.put("CALCPARAMS", calcParams);

        calcParams.put(RETURN_AS_HASH_MAP, true);

        Map<String, Object> calcRes = this.callService(WsConstants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcParams, login, password);

        return calcRes;
    }

    //<editor-fold defaultstate="collapsed" desc="скопировано из AngularContractCustomFacade без изменений (для работы с курсами валют)">
    // перенесено в ProductContractCustomFacade
    //private String getUploadFilePath() {
    //    String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
    //    // проверим, что пути есть и каталоги существуют
    //    File dirFile = new File(result);
    //    dirFile.mkdirs();
    //    return result;
    //}
    /*private void saveEmailInFile(Map<String, Object> sendParams, String login, String password) {
        // получаем значение флага сохранения из конфига bivsberposws
        Config config = Config.getConfig(THIS_SERVICE_NAME);
        String saveEmailInFile = config.getParam("SAVEEMAILINFILE", "FALSE");
        if ("true".equalsIgnoreCase(saveEmailInFile)) {
            // если флаг взведен, делаем файл по пути uploadpath 
            // получаем путь к uploadpath
            String upPath = getUploadFilePath();
            // сохраняем в данный файл текст письма
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy_HH.mm.ss");
            String email = getStringParam(sendParams.get("SMTPReceipt"));
            String htmlText = getStringParam(sendParams.get("HTMLTEXT"));
            if (htmlText.isEmpty()) {
                htmlText = getStringParam(sendParams.get("SMTPMESSAGE"));
            }
            email.replace("@", "_");

            BufferedOutputStream bufferedOutput = null;
            FileOutputStream fileOutputStream = null;
            try {
                String fileName = sdf.format(now) + "_" + email + ".html";
                fileOutputStream = new FileOutputStream(upPath + fileName);//".txt"
                bufferedOutput = new BufferedOutputStream(fileOutputStream);
                //Start writing to the output stream
                byte[] ba = null;
                String codePage = "";
                if (codePage.equals("")) {
                    ba = htmlText.getBytes();
                } else {
                    ba = htmlText.getBytes(codePage);
                }
                bufferedOutput.write(ba);
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            } finally {
                //Close the BufferedOutputStream
                try {
                    if (bufferedOutput != null) {
                        bufferedOutput.flush();
                        bufferedOutput.close();
                    }
                } catch (IOException ex) {
                }
            }
        }
    }*/

    /*private void sendEmailCurrencyFail(String email, Date date, boolean usdExist, boolean euroExist, String login, String password) {
        Map<String, Object> sendParams = new HashMap<String, Object>();
        sendParams.put("SMTPSubject", "Ошибка сервиса обновления курсов валют");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String dateStr = sdf.format(date);
        String currNames = "";
        if (!usdExist) {
            currNames = "USD ";
        }
        if (!euroExist) {
            currNames = currNames + "EUR ";
        }
        sendParams.put("SMTPMESSAGE", "На дату " + dateStr + " отсутствует курс валют: " + currNames + ". Проверьте работу сервиса получения курсов валют от центробанка.");
        sendParams.put("SMTPReceipt", email);
        if (isAllEmailValid(email)) {
        
        logger.debug("sendParams = " + sendParams.toString());
        Map<String, Object> sendRes = null;

        try {
            saveEmailInFile(sendParams, login, password);

            sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
            if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                    sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                    if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                        sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                        if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                            sendRes = this.callService(WEBSMSWS_SERVICE_NAME, "mailmessage", sendParams, login, password);
                        }
                    }
                }
            }
            logger.debug("mailSendSuccess");
        } catch (Exception e) {
            saveEmailInFile(sendParams, login, password);
            logger.debug("mailSendException: ", e);
        }
        //return sendRes;        
        }
    }*/

 /*   private Double getExchangeRateByCurrencyID(Long currencyID, Date date, String login, String password) throws Exception {
        Double result = 1.0;

        Map<String, Object> curParams = new HashMap<String, Object>();
        curParams.put("CurrencyID", currencyID);
        Map<String, Object> curRes = this.callService(REFWS_SERVICE_NAME, "getCurrencyByParams", curParams, login, password);
        List<Map<String, Object>> curList = WsUtils.getListFromResultMap(curRes);
        if (curList != null) {
            if (!curList.isEmpty()) {
                String curCode = getStringParam(curList.get(0).get("Brief"));
                Map<String, Object> exParams = new HashMap<String, Object>();
                exParams.put("natCurCode", curCode);
                exParams.put("QuotedCurrencyID", 1);
                GregorianCalendar gcd = new GregorianCalendar();
                if (date != null) {
                    gcd.setTime(date);
                    if (gcd.get(Calendar.HOUR_OF_DAY) < 4) {
                        gcd.add(Calendar.HOUR_OF_DAY, 4);
                        date = gcd.getTime();
                    }
                } else {
                    date = new Date();
                    gcd.setTime(date);
                }                
                exParams.put("Date", date);
                Map<String, Object> exRes = this.callService(REFWS_SERVICE_NAME, "getCurrencyPairForCrossCourseByParams", exParams, login, password);
                List<Map<String, Object>> exList = WsUtils.getListFromResultMap(exRes);
                if (exList != null) {
                    if (!exList.isEmpty()) {
                        if (exList.get(0).get("COURSEVALUE") != null) {
                            Date cd = getDateParam(exList.get(0).get("COURSEDATE"));
                            GregorianCalendar gc = new GregorianCalendar();
                            gc.setTime(cd);
                            gc.set(Calendar.HOUR_OF_DAY, 0);
                            gc.set(Calendar.MINUTE, 0);
                            gc.set(Calendar.SECOND, 0);
                            gc.set(Calendar.MILLISECOND, 0);
                            GregorianCalendar cgc = new GregorianCalendar();
                            cgc.setTime(date);
                            cgc.set(Calendar.HOUR_OF_DAY, 0);
                            cgc.set(Calendar.MINUTE, 0);
                            cgc.set(Calendar.SECOND, 0);
                            cgc.set(Calendar.MILLISECOND, 0);
                            result = getDoubleParam(exList.get(0).get("COURSEVALUE"));
                            if (gc.getTime().compareTo(cgc.getTime()) != 0) {

                                //даты не равны отправляем сообщение об ошибке
                                Map<String, Object> sysParam = new HashMap<String, Object>();
                                sysParam.put("SETTINGSYSNAME", "NOTIFICATIONEMAILS");
                                sysParam.put("ReturnAsHashMap", "TRUE");
                                Map<String, Object> sysRes = this.callService(COREWS_SERVICE_NAME, "getSysSettingBySysName", sysParam, login, password);
                                if (sysRes.get("SETTINGVALUE") != null) {
                                    String emails = sysRes.get("SETTINGVALUE").toString();
                                    String[] emailList = emails.split(",");
                                    boolean usdExist = true;
                                    boolean euroExist = true;
                                    if ("EUR".equals(curCode)) {
                                        euroExist = false;
                                    }
                                    if ("USD".equals(curCode)) {
                                        usdExist = false;
                                    }
                                    for (String email : emailList) {
                                        //4. отправляем на указанные адреса уведомления о отсутсвии курса валюты на заданную дату.
                                        sendEmailCurrencyFail(email, date, usdExist, euroExist, login, password);
                                    }
                                }
                            }

                        }

                    }
                }
            }
        }
        return result;
    }*/
    //</editor-fold>

    // генерация дополнительных вычисляемых сумм для продукта 'Страхование путешественников Онлайн'
    private void genAdditionalTravelSumSaveParams(Map<String, Object> contract, Map<String, Object> product, String programSysName, boolean isVerboseLog, String login, String password) throws Exception {

        // валюта премии по договору - всегда передается с интерфейса (в отличие от старого варианта онлайн-продукта все суммы в B2B хранятся в валюте договора)
        // setOverridedParam(contract, "PREMCURRENCYID", 1, isVerboseLog);
        //Double currencyRate = roundCurrencyRate(getExchangeRateByCurrencyID(insAmCurrencyID, new Date(), login, password));
        Double currencyRate = getDoubleParam(contract.get("CURRENCYRATE"));
        setOverridedParam(contract, "CURRENCYRATE", currencyRate, isVerboseLog);

        Long calcVerID = getLongParam(product.get("CALCVERID"));
        logger.debug("Версия калькулятора (CALCVERID): " + calcVerID + ".");

        Map<String, Double> premListAsMapBySysName;

        premListAsMapBySysName = getPremListAsMapBySysName(contract, calcVerID, programSysName, login, password);

        // Определение страховых сумм по системным именам рисков
        Map<String, Double> limitListAsMapBySysName = getLimitListAsMapBySysName(contract, calcVerID, programSysName, login, password);

        //Double currencyRate = getDoubleParam(contract.get("CURRENCYRATE"));
        updateContractInsuranceProductStructureSums(contract, premListAsMapBySysName, limitListAsMapBySysName, currencyRate, isVerboseLog);

    }

    private Map<String, Double> getPremListAsMapBySysName(Map<String, Object> contract, Long calcVerID, String programSysName, String login, String password) throws Exception {
        logger.debug("Определение премий по системным именам рисков...");
        Map<String, Object> calcResult = vzrCalculatePremValues(contract, calcVerID, programSysName, login, password);
        Object riskListObj = getRiskListOrErrorFromVZRCalcResult(calcResult);
        Map<String, Double> premListAsMapBySysName = new HashMap<String, Double>();
        if (riskListObj instanceof List) {
            List<Map<String, Object>> riskList = (List<Map<String, Object>>) riskListObj;
            for (Map<String, Object> risk : riskList) {
                String riskSysName = getStringParam(risk.get("PRODRISKSYSNAME"));
                Double totalSum = getDoubleParam(risk.get("PREMIUM"));
                Double roundedTotalSum = roundSum(totalSum);
                logger.debug("    " + riskSysName + " = " + roundedTotalSum);
                premListAsMapBySysName.put(riskSysName, roundedTotalSum);
            }
        }
        logger.debug("Определение премий по системным именам рисков завершено.");
        return premListAsMapBySysName;
    }

    private Map<String, Double> getLimitListAsMapBySysName(Map<String, Object> contract, Long calcVerID, String programSysName, String login, String password) throws Exception {
        logger.debug("Определение страховых сумм по системным именам рисков...");
        Map<String, Object> calcParams = (Map<String, Object>) contract.get("CALCPARAMS");
        Map<String, Object> qRes = getB2BVZRRiskLimits(calcVerID, calcParams, programSysName, login, password);
        Map<String, Double> limitListAsMapBySysName = new HashMap<String, Double>();
        if (isCallResultOK(qRes)) {
            List<Map<String, Object>> limitList = WsUtils.getListFromResultMap(qRes);
            if (!limitList.isEmpty()) {
                for (Map<String, Object> limit : limitList) {
                    Double sum = getDoubleParam(limit.get("limit"));
                    String riskSysName = getStringParam(limit.get("elRiskSysName"));
                    if (riskSysName.isEmpty()) {
                        riskSysName = getStringParam(limit.get("riskSysName"));
                    }
                    logger.debug("    " + riskSysName + " = " + sum);
                    limitListAsMapBySysName.put(riskSysName, sum);
                }
            }
        }
        limitListAsMapBySysName.put("VZRmedical", limitListAsMapBySysName.get("VZRmedExpenses"));
        logger.debug("    VZRmedical = VZRmedExpenses = " + limitListAsMapBySysName.get("VZRmedExpenses"));
        logger.debug("Определение страховых сумм по системным именам рисков завершено.");
        return limitListAsMapBySysName;
    }

    private void updateContractInsuranceProductStructureSums(Map<String, Object> contract, Map<String, Double> premListAsMapBySysName, Map<String, Double> limitListAsMapBySysName, Double currencyRate, boolean isVerboseLog) {

        logger.debug("Установка страховых сумм и премий в структуре страхового объекта договора...");

        Double totalContractLimit = 0D;
        Double totalContractPremium = 0D;
        Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
        Integer totalInsuredCount = getIntegerParam(contractExtValues.get("babes")) + getIntegerParam(contractExtValues.get("adultsAndChildren3_60")) + getIntegerParam(contractExtValues.get("old61_70"));
        //Double totalContractPayPremium = 0D;

        ArrayList<Map<String, Object>> insObjGroupList = (ArrayList<Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        //Map<String, Map<String, Object>> insObjGroupListAsMapBySysName = (Map<String, Map<String, Object>>) contract.get("INSOBJGROUPLIST");
        for (Map<String, Object> insObjGroup : insObjGroupList) {
            ArrayList<Map<String, Object>> objList = (ArrayList<Map<String, Object>>) insObjGroup.get("OBJLIST");
            //Map<String, Map<String, Object>> objListAsMapBySysName = (Map<String, Map<String, Object>>) insObjGroup.get("OBJLIST");
            ArrayList<Map<String, Object>> objListWithSums = new ArrayList<Map<String, Object>>();
            //Map<String, Map<String, Object>> objListAsMapBySysNameWithSums = new HashMap<String, Map<String, Object>>();
            for (Map<String, Object> obj : objList) {
                Map<String, Object> contrObjMap = (Map<String, Object>) obj.get("CONTROBJMAP");
                Map<String, Object> insObjMap = (Map<String, Object>) obj.get("INSOBJMAP");

                String insObjSysName = getStringParam(insObjMap.get("INSOBJSYSNAME"));
                if (insObjSysName.isEmpty()) {
                    insObjSysName = getStringParam(insObjMap.get("SYSNAME"));
                }
                logger.debug("insObjSysName = " + insObjSysName + "...");

                Double objPayPremium = 0D;
                Double objPremium = 0D;
                Double objLimit = 0D;
                ArrayList<Map<String, Object>> contrRiskList = (ArrayList<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                //Map<String, Map<String, Object>> contrRiskListAsMapBySysName = (Map<String, Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
                ArrayList<Map<String, Object>> contrRiskListWithSums = new ArrayList<Map<String, Object>>();
                //Map<String, Map<String, Object>> contrRiskListAsMapBySysNameWithSums = new HashMap<String, Map<String, Object>>();
                for (Map<String, Object> contrRisk : contrRiskList) {
                    String riskSysName = getStringParam(contrRisk.get("PRODRISKSYSNAME"));
                    if (riskSysName.isEmpty()) {
                        riskSysName = getStringParam(contrRisk.get("SYSNAME"));
                    }
                    logger.debug("riskSysName = " + riskSysName + "...");

                    Double roundedPremium = getDoubleParam(premListAsMapBySysName.get(riskSysName));

                    if (roundedPremium > 0) {

                        totalContractPremium += roundedPremium;
                        contrRisk.put("PREMVALUE", roundedPremium);
                        objPremium += roundedPremium;
                        Double payPremium = roundedPremium * currencyRate;
                        Double roundedPayPremium = roundSum(payPremium);
                        //totalContractPayPremium += roundedPayPremium;
                        contrRisk.put("PAYPREMVALUE", roundedPayPremium);
                        objPayPremium += roundedPayPremium;

                        // "Страховая сумма для каждого рискам рассчитывается по формуле:
                        //  СС * ( NМладенцев + NВзрослых + NПожилых)"
                        Double limit = 0.0;
                        if (limitListAsMapBySysName != null) {
                            limit = getDoubleParam(limitListAsMapBySysName.get(riskSysName)) * totalInsuredCount;
                        }

                        if (totalContractLimit < limit) {
                            totalContractLimit = limit;
                        }
                        if (objLimit < limit) {
                            objLimit = limit;
                        }
                        contrRisk.put("INSAMVALUE", limit);

                        logger.debug("              Страховая сумма / лимит (INSAMVALUE) = " + limit + ";");
                        logger.debug("              Страховая премия в валюте договора (PREMVALUE) = " + roundedPremium + ";");
                        logger.debug("              Страховая премия в рублях (PAYPREMVALUE) = " + roundedPayPremium + ";");

                        //contrRiskListAsMapBySysNameWithSums.put(riskSysName, contrRisk);
                        contrRiskListWithSums.add(contrRisk);

                    } else {
                        logger.debug("              Страховая премия в валюте договора (PREMVALUE) = 0;");
                        logger.debug("              Риск исключен.");
                    }
                }

                //contrObjMap.put("CONTRRISKLIST", contrRiskListAsMapBySysNameWithSums);
                contrObjMap.put("CONTRRISKLIST", contrRiskListWithSums);

                if (objPremium > 0) {
                    Double roundedObjPremium = roundSum(objPremium);
                    contrObjMap.put("PREMVALUE", roundedObjPremium);
                    Double roundedObjPayPremium = roundSum(objPayPremium);
                    contrObjMap.put("PAYPREMVALUE", roundedObjPayPremium);
                    Double roundedObjLimit = roundSum(objLimit);
                    contrObjMap.put("INSAMVALUE", roundedObjLimit);
                    //objListAsMapBySysNameWithSums.put(insObjSysName, obj);
                    objListWithSums.add(obj);
                }

            }

            //insObjGroup.put("OBJLIST", objListAsMapBySysNameWithSums);
            insObjGroup.put("OBJLIST", objListWithSums);

        }

        Double roundedTotalContractLimit = roundSum(totalContractLimit);
        Double roundedTotalContractPremium = roundSum(totalContractPremium);
        //Double roundedTotalContractPayPremium = ((new BigDecimal(totalContractPayPremium.toString())).setScale(2, RoundingMode.HALF_UP)).doubleValue();

        setOverridedParam(contract, "INSAMVALUE", roundedTotalContractLimit, isVerboseLog);
        setOverridedParam(contract, "PREMVALUE", roundedTotalContractPremium, isVerboseLog);
        //setOverridedParam(contract, "PAYPREMVALUE", roundedTotalContractPayPremium, isVerboseLog);
        //setOverridedParam(contract, "PREMVALUE", roundedTotalContractPayPremium, isVerboseLog);

        logger.debug("Установка страховых сумм и премий в структуре страхового объекта договора завершена.");

    }

    private Object getB2BVZRRiskLimitsFullList(Long calcVerID, String login, String password) throws Exception {
        Map<String, Object> callResult = getB2BVZRRiskLimits(calcVerID, new HashMap<String, Object>(), null, login, password);
        if (isCallResultOK(callResult)) {
            return WsUtils.getListFromResultMap(callResult);
        } else {
            return callResult;
        }
    }

    private Map<String, Object> getB2BVZRRiskLimits(Long calcVerID, Map<String, Object> calcParams, String programSysName, String login, String password) throws Exception {
        Map<String, Object> сalculatorHBParams = new HashMap<String, Object>();
        сalculatorHBParams.put("CALCVERID", calcVerID);
        сalculatorHBParams.put("NAME", "Ins.Vzr.Risk.Limits");
        Map<String, Object> hbRecordsParams = new HashMap<String, Object>();
        hbRecordsParams.put("territorySysName", calcParams.get("territorySysName"));
        hbRecordsParams.put("travelKind", calcParams.get("travelKind"));
        hbRecordsParams.put("programSysName", programSysName);
        сalculatorHBParams.put("PARAMS", hbRecordsParams);
        //logger.debug("qParam: " + сalculatorHBParams);
        Map<String, Object> qRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsGetCalculatorHandbookData", сalculatorHBParams, login, password);
        //logger.debug("qRes (dsGetCalculatorHandbookData): " + qRes);
        //contract.put("DEBUGLIMITSHBRES", qRes);
        return qRes;
    }

    protected Map<String, Object> genAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {
        boolean isParamsChangingLogged = logger.isDebugEnabled();
        // идентификатор версии продукта всегда передается в явном виде с интерфейса
        Long prodVerID = getLongParam(contract.get("PRODVERID"));
        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            setGeneratedParam(contract, "PRODCONFID", prodConfID, isParamsChangingLogged);
        }
        // даты начала действия всегда передается с интерфейса
        GregorianCalendar startDateGC = new GregorianCalendar();
        Object startDate = contract.get("STARTDATE");
        startDateGC.setTime((Date) parseAnyDate(startDate, Date.class, "STARTDATE"));

        // расширенные атрибуты договора
        Object contractExt = contract.get("CONTREXTMAP");
        Map<String, Object> contractExtValues;
        if (contractExt != null) {
            contractExtValues = (Map<String, Object>) contractExt;
        } else {
            contractExtValues = new HashMap<String, Object>();
            contract.put("CONTREXTMAP", contractExtValues);
        }

        // признак годового полиса (0 - обычный, 1 - годовой)
        int annualPolicy = getIntegerParam(contractExtValues.get("annualPolicy"));

        GregorianCalendar finishDateGC = new GregorianCalendar();
        GregorianCalendar finishDateGC1 = new GregorianCalendar();

        if (annualPolicy == POLICY_IS_ANNUAL) {
            // Вариант годового полиса (0 - 90 дней; 1 - 365 дней)
            int annualPolicyType = getIntegerParam(contractExtValues.get("annualPolicyType"));
            // для обоих вариантов годового полиса - безусловное перевычислине даты оконочания
            int days = 0;
            int years = 0;
            if (annualPolicyType == ANNUAL_POLICY_TYPE_90_DAYS) {
                days = 90;
                //     days = 89;
            } else {
                years = 1;
                //      days = -1;
            }
            finishDateGC.setTime(startDateGC.getTime());
            finishDateGC.add(Calendar.YEAR, years);
            finishDateGC.add(Calendar.DATE, days);
            // даже если 90 дней - полис все равно годовой.
            finishDateGC1.setTime(startDateGC.getTime());
            finishDateGC1.add(Calendar.YEAR, 1);
            finishDateGC1.add(Calendar.DATE, 0);
            setOverridedParam(contract, "FINISHDATE", finishDateGC1.getTime(), isParamsChangingLogged);
        } else {
            // для негодового полиса дата окончания действия всегда передается с интерфейса
            Object finishDate = contract.get("FINISHDATE");
            finishDateGC.setTime((Date) parseAnyDate(finishDate, Date.class, "FINISHDATE"));
            finishDateGC1 = finishDateGC;
        }

        long duration = 0;
        if (annualPolicy == POLICY_IS_ANNUAL) {
            // при годовом полисе, вычисляем срок действия автоматически
            long startDateInMillis = startDateGC.getTimeInMillis();
            long finishDateInMillis = finishDateGC.getTimeInMillis();
            duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000)); // в сутках (24*60*60*1000) милисекунд
        } else if (contract.get("DURATION") != null) {
            // срок действия берем с формы, он может быть меньше чем разницы между датами поездки
            duration = getLongParam(contract.get("DURATION"));
        }
        setOverridedParam(contract, "DURATION", duration, isParamsChangingLogged);
        setOverridedParam(contractExtValues, "dayCount", duration, isParamsChangingLogged);

        // +15 дней к дате окончания (без увеличения фактического срока действия) для некоторых insuranceTerritory - аналогично старому варинату онлайн-продукта
        Long insuranceTerritory = getLongParam(contractExtValues.get("insuranceTerritory"));
        if ((insuranceTerritory.intValue() == 0) || (insuranceTerritory.intValue() == 1)) {

            logger.debug("Согласно визовым требованиям Шенгенской зоны, действие полиса будет продлено на 15 дней после окончания поездки...");

            // дата окончания, увеличенная на 15 дней для оформления визы
            GregorianCalendar finishDateWithShengenGC = new GregorianCalendar();
            finishDateWithShengenGC.setTime(finishDateGC1.getTime());
            finishDateWithShengenGC.add(Calendar.DATE, 15);

            // дата окончания, увеличенная на год относительно даты начала
            GregorianCalendar finishDateFullYearGC = new GregorianCalendar();
            finishDateFullYearGC.setTime(startDateGC.getTime());
            finishDateFullYearGC.add(Calendar.YEAR, 1);
            finishDateFullYearGC.add(Calendar.DATE, -1);

            // дата окончания даже с учетом времени на оформление визы не должна превышать дату начала более чем на год - аналогично старому варинату онлайн-продукта
            if (finishDateWithShengenGC.getTimeInMillis() > finishDateFullYearGC.getTimeInMillis()) {
                finishDateWithShengenGC.setTime(finishDateFullYearGC.getTime());
            }
            setOverridedParam(contract, "FINISHDATE", finishDateWithShengenGC.getTime(), isParamsChangingLogged);
        }

        // список типов объектов - выбор (если уже существует в договоре) или создание нового
        Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        ArrayList<Map<String, Object>> insObjGroupList;
        if (insObjGroupListFromContract != null) {
            insObjGroupList = (ArrayList<Map<String, Object>>) insObjGroupListFromContract;
        } else {
            insObjGroupList = new ArrayList<Map<String, Object>>();
            //insObjGroupList.add(new HashMap<String, Object>());
            contract.put("INSOBJGROUPLIST", insObjGroupList);
        }

        // получение сведений о продукте (по идентификатору конфигурации продукта)
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODCONFID", prodConfID);
        productParams.put("HIERARCHY", false);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);

        // определение идентификатора справочника расширенных атрибутов договора на основании сведений о продукте
        Object contrExtMapHBDataVerID = product.get("HBDATAVERID");
        contractExtValues.put("HBDATAVERID", contrExtMapHBDataVerID);

        // определение кода программы
        String programCode = getStringParam(contractExtValues.get("insuranceProgram"));
        //if (programCode.isEmpty()) {
        //    programCode = DEFAULT_AND_ONLY_PROGRAM_CODE_GAP;
        //    contractExtValues.put("insuranceProgram", programCode);
        //}

        // определение идентификатора программы по её коду на основании сведений о продукте
        Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
        ArrayList<Map<String, Object>> prodProgs = (ArrayList<Map<String, Object>>) prodVer.get("PRODPROGS");
        Map<String, Object> program = (Map<String, Object>) getLastElementByAtrrValue(prodProgs, "PROGCODE", programCode);
        Long programID = getLongParam(program.get("PRODPROGID"));
        setOverridedParam(contract, "PRODPROGID", programID, isParamsChangingLogged); //contract.put("PRODPROGID", programID);
        String programSysName = getStringParam(program.get("SYSNAME"));

        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор
        updateContractInsuranceProductStructure(contract, product, false, programCode, login, password);

        // генерация сумм
        genAdditionalTravelSumSaveParams(contract, product, programSysName, isParamsChangingLogged, login, password);

        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        Object currentRowStatus = contract.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
            contract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
        }

        //с интерфейса может придти измененный список застрахованных. возможно какие-либо из застрахованных были удалены.
        // 1. если договор на обновление (существует CONTRID)
        if (contract.get("CONTRID") != null) {
            // 2. считываем мемберов договора
            List<Map<String, Object>> memberListInDB = getMemberList(getLongParam(contract.get("CONTRID")), login, password);
            List<Map<String, Object>> memberListInput = (List<Map<String, Object>>) contract.get("MEMBERLIST");
            // 3. проходим по списку мемберов из базы, и для каждого пытаемся найти по ид соответствие во входной мапе.
            for (Map<String, Object> dbMember : memberListInDB) {
                Long memberId = getLongParam(dbMember.get("MEMBERID"));
                boolean isMemberInInput = false;
                for (Map<String, Object> inputMember : memberListInput) {
                    if (inputMember.get("MEMBERID") != null) {
                        Long inputMemberId = getLongParam(inputMember.get("MEMBERID"));
                        if (inputMemberId.equals(memberId)) {
                            isMemberInInput = true;
                        }
                    }
                }
                // 4. если не находим - добавляем этого мембера во входной список с rowstatus = 3 (удаление)
                if (!isMemberInInput) {
                    dbMember.put("ROWSTATUS", 3);
                    memberListInput.add(dbMember);
                }
                // 5. если находим - ничего не делаем, запись по умолчанию апдейтнется.
            }
        }

        return contract;
    }

    private GregorianCalendar getOrGenerateDocumentDate(Map<String, Object> contract) {
        return getOrGenerateDocumentDate(contract, true);
    }    
    // инициализация даты документа
    private GregorianCalendar getOrGenerateDocumentDate(Map<String, Object> contract, boolean needRecreate) {
        GregorianCalendar documentDateGC = new GregorianCalendar();
        Object docDate = contract.get("DOCUMENTDATE");
        if ((docDate == null) || needRecreate) {
        documentDateGC.setTime(new Date());
        documentDateGC.set(Calendar.HOUR_OF_DAY, 0);
        documentDateGC.set(Calendar.MINUTE, 0);
        documentDateGC.set(Calendar.SECOND, 0);
        documentDateGC.set(Calendar.MILLISECOND, 0);
        setGeneratedParam(contract, "DOCUMENTDATE", documentDateGC.getTime(), logger.isDebugEnabled());
        } else {
            logger.debug("DOCDATE-" + docDate);
            documentDateGC.setTime((Date) parseAnyDate(docDate, Date.class, "DOCUMENTDATE"));
        }
        return documentDateGC;
    }

    private void genAdditionalSaveParamsFixContr(Map<String, Object> contract, String login, String password) throws Exception {
        boolean isParamsChangingLogged = logger.isDebugEnabled();
        // идентификатор версии продукта всегда передается в явном виде с интерфейса
        Long prodVerID = getLongParam(contract.get("PRODVERID"));
        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            setGeneratedParam(contract, "PRODCONFID", prodConfID, isParamsChangingLogged);
        }
        GregorianCalendar startDateGC = new GregorianCalendar();
        Object startDate = contract.get("STARTDATE");
        startDateGC.setTime((Date) parseAnyDate(startDate, Date.class, "STARTDATE"));

        // расширенные атрибуты договора
        Object contractExt = contract.get("CONTREXTMAP");
        Map<String, Object> contractExtValues;
        if (contractExt != null) {
            contractExtValues = (Map<String, Object>) contractExt;
        } else {
            contractExtValues = new HashMap<String, Object>();
            contract.put("CONTREXTMAP", contractExtValues);
        }
        // признак годового полиса (0 - обычный, 1 - годовой)
        int annualPolicy = getIntegerParam(contractExtValues.get("annualPolicy"));

        // срок не перевычисляем, т.к. он может быть меньше разницы дат
        /*// безусловное перевычисление срока действия
        long startDateInMillis = startDateGC.getTimeInMillis();
        long finishDateInMillis = finishDateGC.getTimeInMillis();
        long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000)); // в сутках (24*60*60*1000) милисекунд
        // дюратион везде уехал на 1 вперед. соответственно данный плюс лишний.
        if (annualPolicy != POLICY_IS_ANNUAL) {
            duration += 1;
        }
        setOverridedParam(contract, "DURATION", duration, isParamsChangingLogged);
        setOverridedParam(contractExtValues, "dayCount", duration, isParamsChangingLogged);*/
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
    public Map<String, Object> dsB2BVZRContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BVZRContractPrepareToSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        boolean isDataValid = validateSaveParams(contract, false, login, password);
        Map<String, Object> result;
        if (isDataValid) {
            genAdditionalSaveParams(contract, login, password);
            result = contract;
        } else {
            result = contract;
        }
        logger.debug("after dsB2BVZRContractPrepareToSave");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BVZRContractPrepareToSaveFixContr(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BVZRContractPrepareToSaveFixContr");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        boolean isDataValid = validateSaveParams(contract, true, login, password);
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
            } else if ((null != params.get("isCorrector")) && ((Boolean) params.get("isCorrector"))) {
                contract.remove("DURATION");
                contract.remove("FINISHDATETIME");
                contract.remove("FINISHDATE");
                contract.remove("STARTDATETIME");
                contract.remove("STARTDATE");
                contract.remove("MEMBERLIST");
                contract.remove("CRMDOCLIST");
                contract.remove("INSURERID");
                contract.remove("INSURERMAP");
                result = contract;
            } else {
                // Если договор выгружен в 1С и у пользователя нет прав корректора запрещем что либо сохранять.
                result = new HashMap< String, Object>();
            }
        }
        logger.debug("after dsB2BVZRContractPrepareToSaveFixContr");
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
    public Map<String, Object> dsB2BVZRContrLoad(Map<String, Object> params) throws Exception {

        logger.debug("before dsB2BVZRContrLoad");

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> loadResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", params, login, password);

        logger.debug("after dsB2BVZRContrLoad");

        return loadResult;

    }

    @WsMethod(requiredParams = {"CONTRMAP"})
    public Map<String, Object> dsB2BVZRContractApplyDiscount(Map<String, Object> params) throws Exception {
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
        updateContractInsuranceProductStructureSums(contract, premListAsMapBySysName, null, currencyRate, Boolean.FALSE);
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

    private List<Map<String, Object>> getMemberList(Long contrId, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("CONTRID", contrId);
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BMemberBrowseListByParam", param, login, password);
        return (List<Map<String, Object>>) res.get(RESULT);
    }
}
