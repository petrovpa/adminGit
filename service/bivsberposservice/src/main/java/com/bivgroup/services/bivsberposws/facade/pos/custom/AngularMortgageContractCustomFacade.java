/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomFacade.base64Decode;
import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomFacade.base64Encode;
import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularReportCustomFacade.MAX_SECOND;
import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularReportCustomFacade.MONTH_NAMES;

import com.bivgroup.services.bivsberposws.system.Constants;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;
import ru.diasoft.utils.currency.AmountUtils;
import ru.diasoft.utils.exception.XMLUtilException;

/**
 * @author 1
 */
@BOName("AngularMortgageContractCustom")
public class AngularMortgageContractCustomFacade extends AngularContractCustomBaseFacade {

    private static final String LIBREOFFICEREPORTSWS_SERVICE_NAME = Constants.LIBREOFFICEREPORTSWS;
    private static final String SIGNWS_SERVICE_NAME = Constants.SIGNWS;

    // без изменений из sis, требуются в методах печати договоров
    public static final String[] RUB_NAMES = {"рубль", "рубля", "рублей", "копейка", "копейки", "копеек", "M"};
    public static final String[] EUR_NAMES = {"евро", "евро", "евро", "евроцент", "евроцента", "евроцентов", "M"};
    public static final String[] USD_NAMES = {"доллар", "доллара", "долларов", "цент", "цента", "центов", "M"};

    // имена избыточных родительских объектов, передаваемых в params для dsMortgageContractCreateEx
    private static final String NAME_OBJMAP = "OBJMAP";
    private static final String NAME_OBJ = "obj";

    // атрибуты нумерации договоров
    // пока без изменений из sis, потребуется замена на соответствующие страхованию ипотеки
    //private static final String POLSER = "005EP";
    //private static final String POLNUM = "801";
    // заменены на соответствующие страхованию ипотеки по данным из "Страховой полис.docx"
    private static final String POLSER = "105EP";
    private static final String POLNUM = "105";

    // объект для работы с датами
    private static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private static final NumberFormat moneyFormatter = NumberFormat.getNumberInstance(new Locale("ru"));

    // константы для пересчета премии
    // (пересчет премии - без использования калькулятора, т. к. формула простая)
    private static final int mortgageInsAmValueLimit = 15000000;
    private static final double mortgagePremValueBelowLimitMult = 0.00225;
    private static final double mortgagePremValueAboveLimitMult = 0.00120;

    public AngularMortgageContractCustomFacade() {
        super();
        moneyFormatter.setMinimumFractionDigits(2);
        moneyFormatter.setMaximumFractionDigits(2);
    }

    private void saveContrRiskList(Map<String, Object> risk, Long contrObjId, Long contrId, String login, String password) throws Exception {
        Map<String, Object> riskSaveParams = new HashMap<String, Object>();
        // возможно, риск может быть выключен, но не для страхования ипотеки
        boolean isChecked = getBooleanParam(risk.get("checked"), Boolean.TRUE);
        if (isChecked) {
            riskSaveParams.put("CONTROBJID", contrObjId);
            riskSaveParams.put("CONTRID", contrId);
            riskSaveParams.put("CURRENCYID", 1L);
            riskSaveParams.put("INSAMCURRENCYID", 1L);
            riskSaveParams.put("PREMCURRENCYID", 1L);
            Long prodRiskId = getLongParam(risk.get("PRODRISKID"));
            riskSaveParams.put("PRODRISKID", prodRiskId);
            riskSaveParams.put("INSAMVALUE", risk.get("INSAMVALUE"));
            riskSaveParams.put("PREMVALUE", risk.get("PREMVALUE"));
            this.callService(INSPOSWS_SERVICE_NAME, "dsContractRiskCreate", riskSaveParams, login, password);
        }
    }

    private Map<String, Object> saveInsObj(Map<String, Object> insObj,
                                           Map<String, Object> contrMap,
                                           Map<String, Object> master,
                                           Map<String, Object> insObjAddress,
                                           List<Map<String, Object>> prodRiskList,
                                           String login, String password) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        Long contrId = getLongParam(contrMap.get("CONTRID"));
        Long insObjNodeId = createNode(login, password);

        String typeSysName = getStringParam(insObj.get("typeSysName"));

        // сохранение адреса имущества
        Map<String, Object> addressSaveParams = new HashMap<String, Object>();
        addressSaveParams.put("ReturnAsHashMap", "TRUE");
        addressSaveParams.put("ADDRESSDATA", insObjAddress);
        addressSaveParams.put("NAME", typeSysName);
        addressSaveParams.put("CONTRID", contrId);
        addressSaveParams.put("ROWSTATUS", 1L);
        Map<String, Object> cpRes = this.callService(INSPOSWS_SERVICE_NAME, "dsContractPropertySave", addressSaveParams, login, password);
        Long contrPropertyId = getLongParam(cpRes.get("CONTRPROPERTYID"));

        // сохранение страхуемого имущества (со ссылкой на сохраненный ранее адрес)
        Map<String, Object> objectSaveParams = new HashMap<String, Object>();
        objectSaveParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        objectSaveParams.put("INSOBJNODEID", insObjNodeId);
        objectSaveParams.put("NAME", insObj.get("NAME")); // ???
        objectSaveParams.put("OBJTYPESYSNAME", typeSysName);
        objectSaveParams.put("PRODYEARSYSNAME", insObj.get("buildYear"));
        objectSaveParams.put("WALMATERIAL", insObj.get("woodInWal"));
        objectSaveParams.put("CONTRPROPERTYID", contrPropertyId);
        String insObjServiceName = getInsObjServiceName(typeSysName);
        Map<String, Object> saveRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, insObjServiceName, objectSaveParams, login, password);
        Long insObjId = getLongParam(saveRes.get("INSOBJID"));

        // очень надолго зависает
        // updateNodeActiveVersion(insObjNodeId, insObjId, login, password);
        // создание объекта страхования
        Map<String, Object> contrObjParams = new HashMap<String, Object>();
        contrObjParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        contrObjParams.put("CONTRID", contrId);
        contrObjParams.put("INSOBJID", insObjId);
        contrObjParams.put("PREMCURRENCYID", 1L);
        contrObjParams.put("INSAMCURRENCYID", 1L);
        contrObjParams.put("PREMVALUE", insObj.get("PREMVALUE"));
        contrObjParams.put("INSAMVALUE", insObj.get("INSAMVALUE"));
        Map<String, Object> contrObjRes = this.callService(Constants.INSPOSWS, "dsContractObjectCreate", contrObjParams, login, password);
        Long contrObjId = Long.valueOf(contrObjRes.get("CONTROBJID").toString());

        // сохранить риски по объекту
        // (для страхования ипотеки все риски постоянные)
        for (Map<String, Object> risk : prodRiskList) {
            saveContrRiskList(risk, contrObjId, contrId, login, password);
        }

        result.put("CONTRPROPERTYID", contrPropertyId);
        result.put("INSOBJNODEID", insObjNodeId);
        result.put("INSOBJID", insObjId);
        result.put("CONTROBJID", contrObjId);
        return result;
    }

    private String getInsObjServiceName(String typeSysName) {

        // для страхования ипотеки нужны только два варианта - flat и house
        String[][] serviceByInsObjName = {
                {"flat", "dsInsObjFlatCreate"},
                {"house", "dsInsObjHouseCreate"},
                {"sauna", "dsInsObjSaunaCreate"},
                {"other", "dsInsObjOtherCreate"},
                {"movable", "dsInsObjMovableCreate"},
                {"go", "dsInsObjGOCreate"},};

        for (String[] checkedLine : serviceByInsObjName) {
            if (checkedLine[0].equalsIgnoreCase(typeSysName)) {
                return checkedLine[1];
            }
        }

        return "";
    }

    /**
     * @param insurer
     */
    protected Map<String, Object> getRemappedInsurer(Map<String, Object> insurer) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("CITIZENSHIP", insurer.get("citizenship"));
        result.put("LASTNAME", insurer.get("surname"));
        result.put("FIRSTNAME", insurer.get("name"));
        result.put("MIDDLENAME", insurer.get("middlename"));
        //DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        String date = insurer.get("birthDateStr").toString();

        try {
            result.put("BIRTHDATE", dateFormatter.parse(date));
        } catch (ParseException ex) {
            logger.error("Insurer: parse birthdate error", ex);
        }

        // 0 - male, 1 - female
        result.put("SEX", 0);
        if ((insurer.get("gender") != null) && ("female".equalsIgnoreCase(insurer.get("gender").toString()))) {
            result.put("SEX", 1);
        }

        Map<String, Object> сontacts = (Map<String, Object>) insurer.get("contacts");

        result.put("CONTACTPHONEMOBILE", сontacts.get("phone"));

        result.put("PREVCONTACTEMAIL", сontacts.get("email"));
        result.put("CONTACTEMAIL", сontacts.get("email"));

        Map<String, Object> document = (Map<String, Object>) insurer.get("passport");
        result.put("DOCTYPESYSNAME", document.get("typeId"));
        result.put("DOCSERIES", document.get("series"));
        result.put("DOCNUMBER", document.get("number"));
        String passportIssueDate = document.get("issueDateStr").toString();
        try {
            result.put("ISSUEDATE", dateFormatter.parse(passportIssueDate));
        } catch (ParseException ex) {
            logger.error("Insurer: parse passport issue date error", ex);
        }

        result.put("ISSUEDBY", document.get("issuePlace"));
        // insurer.put("ISSUERCODE", pass.get("insPassIssueCode"));
        Map<String, Object> insAddressMap = new HashMap<String, Object>();
        Map<String, Object> rawAddressMap = (Map<String, Object>) insurer.get("address");

        insAddressMap.put("addrSysName", "RegisterAddress");

        Map<String, Object> regionMap = (Map<String, Object>) rawAddressMap.get("region");
        insAddressMap.put("eRegion", regionMap.get("NAME"));
        insAddressMap.put("regionCode", regionMap.get("CODE"));

        Map<String, Object> cityMap = (Map<String, Object>) rawAddressMap.get("city");
        insAddressMap.put("eCity", cityMap.get("NAME"));
        insAddressMap.put("cityCode", cityMap.get("CODE"));

        Map<String, Object> streetMap = (Map<String, Object>) rawAddressMap.get("street");
        insAddressMap.put("eStreet", streetMap.get("NAME"));
        insAddressMap.put("streetCode", streetMap.get("CODE"));
        insAddressMap.put("eIndex", streetMap.get("POSTALCODE"));

        insAddressMap.put("eHouse", rawAddressMap.get("house"));
        insAddressMap.put("eCorpus", rawAddressMap.get("housing"));
        insAddressMap.put("eBuilding", rawAddressMap.get("building"));
        insAddressMap.put("eFlat", rawAddressMap.get("flat"));

        List<Map<String, Object>> addressList = new ArrayList<Map<String, Object>>();
        addressList.add(insAddressMap);
        result.put("INSADDRESSDATA", addressList);

        return result;
    }

    /**
     * метод создает договор страхования ипотеки со всеми необходимыми
     * сущностями.
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {NAME_OBJMAP})
    public Map<String, Object> dsMortgageContractCreateEx(Map<String, Object> params) throws Exception {

        logger.debug("Вызван dsMortgageContractCreateEx с параметрами:\n\n" + params.toString() + "\n");

        Map<String, Object> result = new HashMap<String, Object>();
        if (params.get(NAME_OBJMAP) != null) {
            Map<String, Object> contrMapIn = (Map<String, Object>) params.get(NAME_OBJMAP);
            Map<String, Object> contrMap = (Map<String, Object>) contrMapIn.get(NAME_OBJ);//

            if (!contrMap.isEmpty()) {
                Map<String, String> contrEmptyRequiredFields = new HashMap<String, String>();
                // проверка по переченю обязательных полей для mortgage
                if (checkRequiredFields(contrMapIn, contrEmptyRequiredFields, mortgageRequiredFields)) {
                    // страховая сумма
                    Map<String, Object> master = (Map<String, Object>) contrMap.get("master");
                    Double insAmValue = Double.valueOf(master.get("insuredSum").toString());
                    contrMap.put("INSAMVALUE", insAmValue);
                    // получаем конфигурацию продукта (только ID)
                    Long prodVerId = getLongParam(contrMap.get("prodVerId"));
                    Long prodConfId = getLongParam(contrMap.get("prodConfId"));
                    // ... и пробрасываем в выходные параметры
                    contrMap.put("PRODVERID", prodVerId);
                    contrMap.put("PRODCONFID", prodConfId);
                    // страховая премия
                    String login = params.get(WsConstants.LOGIN).toString();
                    String password = params.get(WsConstants.PASSWORD).toString();

                    BigDecimal promoValue = getPromoValueByCodeAndProdVerId(master.get("promo"), prodVerId.longValue(), login, password);
                    contrMap.put("promoCode", master.get("promo"));
                    contrMap.put("promoValue", promoValue);

                    Double premValue = calcMortgagePremValue(insAmValue);
                    premValue = roundSum(BigDecimal.valueOf(premValue).multiply(promoValue).doubleValue());
                    contrMap.put("PREMVALUE", premValue);
                    master.put("contrPremium", premValue);

                    //contrMap.put("PREMVALUE", premValue);

                    // даты начала и конца действия договора страхования, а так же дата заключения кредитного договора
                    //DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
                    String dateStr;
                    Date startDate = null;
                    Date finishDate = null;
                    Date credDate = null;
                    try {
                        dateStr = ((Map<String, Object>) master.get("startDate")).get("dateStr").toString();
                        startDate = dateFormatter.parse(dateStr);
                    } catch (ParseException ex) {
                        logger.error("contract start date parse error: ", ex);
                    }
                    try {
                        dateStr = ((Map<String, Object>) master.get("finishDate")).get("dateStr").toString();
                        finishDate = dateFormatter.parse(dateStr);
                    } catch (ParseException ex) {
                        logger.error("contract finish date parse error: ", ex);
                    }
                    try {
                        dateStr = ((Map<String, Object>) master.get("contrDate")).get("dateStr").toString();
                        credDate = dateFormatter.parse(dateStr);
                    } catch (ParseException ex) {
                        logger.error("mortgage contract date parse error: ", ex);
                    }
                    contrMap.put("STARTDATE", startDate);
                    contrMap.put("FINISHDATE", finishDate);
                    contrMap.put("credDate", credDate);

                    // срок действия договора
                    GregorianCalendar sdgc = new GregorianCalendar();
                    sdgc.setTime(startDate);
                    long startDateInMillis = sdgc.getTimeInMillis();
                    sdgc.setTime(finishDate);
                    long finishDateInMillis = sdgc.getTimeInMillis();
                    // в сутках (24*60*60*1000) милисекунд
                    int duration = (int) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000));
                    // вычитать один день не требуется - уже учтено при отбросе дробной части выше
                    // duration -= 1;
                    contrMap.put("DURATION", duration);
                    //
                    contrMap.put("REFERRAL", master.get("REFERRAL"));
                    contrMap.put("REFERRALBACK", master.get("REFERRALBACK"));
                    contrMap.put("CONTRSRCPARAMLIST", master.get("CONTRSRCPARAMLIST"));

                    if (isB2BMode(params)) {
                        Map<String, Object> createdContract = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsContractCreateInB2BModeEx", contrMap, login, password);
                        return createdContract;
                        //return null; //!только для отладки!
                    }

                    logger.debug("required fields are valid - start saving");

                    // логин и пароль для вызова других методов веб-сервиса                    


                    // создание узловой записи для договора
                    // (таблица в БД - INS_CONTRNODE)
                    logger.debug("create contract node...");
                    Long contrNodeId = сontrNodeСreate(0L, 0L, login, password);
                    logger.debug("created contract node ID: " + contrNodeId);
                    contrMap.put("CONTRNODEID", contrNodeId);

                    // установка даты документа равной текущей
                    Date docDate = new Date();
                    GregorianCalendar gc = new GregorianCalendar();
                    gc.setTime(docDate);
                    gc.set(Calendar.HOUR_OF_DAY, 0);
                    gc.set(Calendar.MINUTE, 0);
                    gc.set(Calendar.SECOND, 0);
                    gc.set(Calendar.MILLISECOND, 0);
                    docDate = gc.getTime();
                    contrMap.put("DOCUMENTDATE", docDate);
                    contrMap.put("DECLDATE", docDate);

                    //Map<String, Object> rawInsurer = (Map<String, Object>) master.get("insurer");
                    Map<String, Object> pureInsurer = getRemappedInsurer((Map<String, Object>) master.get("insurer"));

                    // создание лица / "персоны" в CRM
                    logger.debug("create person...");
                    // создание физ. лица
                    // (таблицы в БД - CRM_PARTICIPANT, CRM_PERSON, CRM_PERSONDOC, CRM_ADDRESS, CRM_CONTACTPERSON, CRM_CONTACT)
                    Map<String, Object> personMap = participantCreate(pureInsurer, login, password);
                    logger.debug("created person: " + personMap.toString());
                    contrMap.put("PARTICIPANTID", personMap.get("PARTICIPANTID"));
                    contrMap.put("PERSONID", personMap.get("PERSONID"));

                    // получаем продавца по логину/паролю                    
                    // при передаче из angular-интерфеса - будет системный продавец вида "Сайт"
                    Map<String, Object> userInfo = findDepByLogin(login, password);
                    Long sellerId = getSellerId(userInfo, login, password);
                    contrMap.put("SELLERID", sellerId);
                    Long sellerDepartmentID = (Long) userInfo.get("DEPARTMENTID");
                    contrMap.put("ORGSTRUCTID", sellerDepartmentID);
                    contrMap.put("SELFORGSTRUCTID", sellerDepartmentID);

                    // генерация номера договора                    
                    String contrNum = generateContrNum(prodConfId, login, password);
                    contrMap.put("CONTRPOLSER", POLSER);
                    contrMap.put("CONTRPOLNUM", POLNUM + contrNum);
                    contrMap.put("CONTRNUMBER", POLSER + POLNUM + contrNum);
                    contrMap.put("ORGSTRUCT", sellerDepartmentID);

                    // номер кредитного договора
                    contrMap.put("CREDCONTRNUM", master.get("insurerContrNum"));

                    // вынесено из remapMortgageContr
                    // настройки валют - без изменений из sis
                    // необязательны, если валюта - рубль (будут установлены в contrCreate)
                    contrMap.put("PREMCURRENCYID", 1L);
                    contrMap.put("INSAMCURRENCYID", 1L);
                    contrMap.put("CURRENCYRATE", 1L);

                    // создание договора
                    logger.debug("create contract...");
                    Long contrId = contrCreate(contrMap, login, password);
                    contrMap.put("CONTRID", contrId);
                    // установка прав на договор и указание текущей версии договора в contrNode
                    setRightsOnContr(userInfo, contrId, login, password);
                    сontrNodeUpdate(contrNodeId, contrId, login, password);
                    logger.debug("created contract ID: " + contrId);

                    // получение списка рисков по ИД версии продукта
                    Map<String, Object> prodRiskParams = new HashMap<String, Object>();
                    prodRiskParams.put("PRODVERID", prodVerId);
                    Map<String, Object> prodRiskRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductRiskBrowseListByParam", prodRiskParams, login, password);
                    List<Map<String, Object>> prodRiskList = (List<Map<String, Object>>) prodRiskRes.get(RESULT);
                    CopyUtils.sortByStringFieldName(prodRiskList, "SYSNAME");

                    for (Map<String, Object> prodRisk : prodRiskList) {
                        prodRisk.put("PREMVALUE", premValue); //PREMIUM???
                        prodRisk.put("INSAMVALUE", insAmValue);
                    }

                    // объекты страхования и риски
                    Map<String, Object> insObj = (Map<String, Object>) master.get("insObj");
                    // страховые сумма и премия - копировать в объект без изменений
                    insObj.put("INSAMVALUE", insAmValue);
                    insObj.put("PREMVALUE", premValue);
                    // адрес объекта страхования
                    Map<String, Object> insObjAddress = (Map<String, Object>) insObj.get("address");
                    mapInsObjAddress(insObjAddress);

                    // сохранение объекта и рисков
                    saveInsObj(insObj, contrMap, master, insObjAddress, prodRiskList, login, password);

                    // сохранение дополнительных атрибутов договора
                    // (таблицы в БД - INS_CONTREXT, ...)
                    contrExtCreate(contrMap, login, password);

                    // 
                    logger.debug("reading contract ext ID...");
                    readContrExtId(contrMap, login, password);
                    String hash = "";
                    if (contrMap.get("GUID") != null) {
                        String guid = contrMap.get("GUID").toString();
                        hash = base64Encode(guid);
                        logger.debug("contract ext ID: " + guid);
                        logger.debug("generated hash (based on ext ID): " + hash);
                    } else {
                        logger.debug("empty contract ext ID! no hash generated.");
                    }
                    contrMap.put("HASH", hash);

                    // итоговый результат
                    result.put(RESULT, contrMap);

                } else {
                    // не все обязательные поля заполнены - создавать договор нельзя
                    result.put("Status", "requiredFieldError");
                    result.put("EmptyRequiredFields", contrEmptyRequiredFields);
                }
            } else {
                result.put("Status", "emptyInputMap");
            }
        } else {
            result.put("Status", "emptyInputMap");
        }
        logger.debug("Вызов метода dsMortgageContractCreateEx завершен с результатом:\n\n" + result.toString() + "\n");
        return result;
    }

    public static Double calcMortgagePremValue(Double insAmValue) {
        // пересчет премии - без использования калькулятора, т. к. формула простая
        // todo: при необходимости - заменить на вызов калькулятора
        if (insAmValue < mortgageInsAmValueLimit) {
            return roundSum(insAmValue * mortgagePremValueBelowLimitMult);
        } else {
            return roundSum(insAmValue * mortgagePremValueAboveLimitMult);
        }
    }

    /**
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMortgageContractBrowseListByParamEx(Map<String, Object> params) throws Exception {

        logger.debug("Вызван dsMortgageContractBrowseListByParamEx с параметрами:\n\n" + params.toString() + "\n");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        if (isB2BMode(params)) {

            Map<String, Object> contractParams = new HashMap<String, Object>();
            contractParams.putAll(params);
            contractParams.put("PRODCONFID", convertValue(params.get("prodConfId"), getProdConfIDConvertRules(), Direction.TO_LOAD));
            contractParams.put(RETURN_AS_HASH_MAP, true);
            contractParams.put("TARGETDATEFORMAT", "DATE");

            Map<String, Object> browsedContract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", contractParams, login, password);

            logger.debug("browsedContract (dsB2BContrLoad):");
            logger.debug(browsedContract);

            Map<String, Object> contract = prepareB2BParams(browsedContract, Direction.TO_LOAD);

            logger.debug("contract (prepareB2BParams):");
            logger.debug(contract);
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("CONTRMAP", contract);
            return contract;
        }

        //params.put("ReturnAsHashMap", "TRUE");
        logger.debug("Выполнение запросов dsMortgageContractBrowseListByParamEx(Count)...");
        Map<String, Object> queriedContracts = this.selectQuery("dsMortgageContractBrowseListByParamEx",
                "dsSisMortgageBrowseListByParamExCount",
                params);
        logger.debug("Запросы dsMortgageContractBrowseListByParamEx(Count) завершены с результатом:");
        logger.debug(" " + queriedContracts.toString());
        List<Map<String, Object>> contracts = (List<Map<String, Object>>) queriedContracts.get(RESULT);

        Map<String, Object> result = new HashMap<String, Object>();

        for (Map<String, Object> contract : contracts) {
            Long contrId = getLongParam(contract.get("CONTRID"));
            Map<String, Object> contrAttrs = new HashMap<String, Object>();
            contrAttrs.put("CONTRID", contrId);
            logger.debug("Выбран договор с ИД = " + contrId);

            logger.debug("Выполнение запросов dsSisContractObjBrowseListByParamEx(Count)...");
            Map<String, Object> queriedObjects = this.selectQuery("dsSisContractObjBrowseListByParamEx",
                    "dsSisContractObjBrowseListByParamExCount",
                    contrAttrs);
            logger.debug("Запросы dsSisContractObjBrowseListByParamEx(Count) завершены с результатом:");
            logger.debug(" " + queriedObjects.toString());
            List<Map<String, Object>> objects = (List<Map<String, Object>>) queriedObjects.get(RESULT);

            for (Map<String, Object> object : objects) {
                Long objId = getLongParam(object.get("CONTROBJID"));
                Map<String, Object> objAttrs = new HashMap<String, Object>();
                objAttrs.put("CONTROBJID", objId);
                logger.debug("Выбран объект с ИД = " + objId);

                // logger.debug("Выполнение запросов dsContractRiskBrowseListByParam(Count) ...");
                // Map<String, Object> queriedRisks = this.selectQuery("dsContractRiskBrowseListByParam", "dsContractRiskBrowseListByParamCount", objAttrs);
                // logger.debug("Запросы dsContractRiskBrowseListByParam(Count) завершены с результатом:");
                logger.debug("Вызов метода dsContractRiskBrowseListByContrIdJoinProdRisk...");
                Map<String, Object> queriedRisks = this.callService(INSPOSWS_SERVICE_NAME,
                        "dsContractRiskBrowseListByContrIdJoinProdRisk",
                        contrAttrs, login, password);
                logger.debug("Вызов метода dsContractRiskBrowseListByContrIdJoinProdRisk завершен с результатом:");
                logger.debug(" " + queriedRisks.toString());
                List<Map<String, Object>> risks = (List<Map<String, Object>>) queriedRisks.get(RESULT);

                object.put("RISKS", risks);

            }

            contract.put("OBJECTS", objects);

        }

        result.put(RESULT, (List<Map<String, Object>>) contracts);

        logger.debug("Вызов метода dsMortgageContractBrowseListByParamEx завершен с результатом:\n\n" + result.toString() + "\n");

        return result;
    }

    /**
     * метод, выберет договор по хешу. сделан, для получения данных договора на
     * форму..
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMortgageContractBrowseEx(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> contrMapIn;

        if (params.get("CONTRMAP") == null) {
            if (params.get("CONTRID") != null) {
                // если не переданы параметры договора, но передан его ИД
                contrMapIn = new HashMap<String, Object>();
                contrMapIn.put("CONTRID", params.get("CONTRID"));
                params.put("CONTRMAP", contrMapIn);
            } else {
                // если не передано ничего - возврат со статусом emptyInputMap
                result.put("Status", "emptyInputMap");
                return result;
            }
        } else {
            // если переданы параметры договора, используем их
            contrMapIn = (Map<String, Object>) params.get("CONTRMAP");
        }

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> browseParams = new HashMap<String, Object>();
        if (contrMapIn.get("orderNum") != null) {
            String orderGuid = base64Decode(contrMapIn.get("orderNum").toString());
            browseParams.put("EXTERNALID", orderGuid);
        } else {
            // в шлюзе с ангуляром закрыта возможность запросить договор по ИД.
            if (contrMapIn.get("contrId") != null) {
                browseParams.put("CONTRID", contrMapIn.get("contrId"));
            }
            if (contrMapIn.get("CONTRID") != null) {
                browseParams.put("CONTRID", contrMapIn.get("CONTRID"));
            }
        }

        if (isB2BMode(params)) {

            Map<String, Object> contractParams = new HashMap<String, Object>();
            contractParams.putAll(browseParams);
            contractParams.put("PRODCONFID", convertValue(contrMapIn.get("prodConfId"), getProdConfIDConvertRules(), Direction.TO_LOAD));
            contractParams.put(RETURN_AS_HASH_MAP, true);
            contractParams.put("TARGETDATEFORMAT", "DATE");

            Map<String, Object> browsedContract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", contractParams, login, password);

            logger.debug("browsedContract (dsB2BContrLoad):");
            logger.debug(browsedContract);

            Map<String, Object> contract = prepareB2BParams(browsedContract, Direction.TO_LOAD);

            logger.debug("contract (prepareB2BParams):");
            logger.debug(contract);

            result.put("CONTRMAP", contract);
            return result;
        }

//        logger.debug("Вызов метода dsMortgageContractBrowseListByParamEx (без ReturnAsHashMap)...");
//        Map<String, Object> queriedContractsRaw = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsMortgageContractBrowseListByParamEx", browseParams, login, password);
//        logger.debug("Вызов метода dsMortgageContractBrowseListByParamEx (без ReturnAsHashMap) завершен с результатом:");
//        logger.debug(" " + queriedContractsRaw.toString());
        //
        browseParams.put("ReturnAsHashMap", "TRUE");
        browseParams.put(USEB2B_PARAM_NAME, params.get(USEB2B_PARAM_NAME));
        logger.debug("Вызов метода dsMortgageContractBrowseListByParamEx (с ReturnAsHashMap = true)...");
        Map<String, Object> queriedContractAsHashMap = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsMortgageContractBrowseListByParamEx", browseParams, login, password);
        logger.debug("Вызов метода dsMortgageContractBrowseListByParamEx (с ReturnAsHashMap = true) завершен с результатом:");
        logger.debug(" " + queriedContractAsHashMap.toString());
        //List<Map<String, Object>> contracts = (List<Map<String, Object>>) queriedContracts.get(RESULT);
        Map<String, Object> contract = queriedContractAsHashMap;

        result.put(RESULT, contract);

        // возврат результата
        return result;
    }

    private String getUploadFilePath() {

        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();

        return result;
    }

    protected String getMetadataURL(String login, String password, String project) throws Exception {
        Map args = new HashMap();
        args.put("fileType", "REPORTS");
        args.put(PROJECT_PARAM_NAME, project);
        String adminwsURL = Config.getConfig().getParam("adminws", "http://localhost:8080/adminws/adminws");
        String COREWSURL = Config.getConfig().getParam("corews", "http://localhost:8080/corews/corews");
        try {
            XMLUtil xmlutil = new XMLUtil(login, password);
            args.put("PROJECTSYSNAME", project);
            Map resultMap = xmlutil.doURL(adminwsURL, "admProjectBySysname", xmlutil.createXML(args), null);

            List result = (List) resultMap.get("Result");
            if (logger.isDebugEnabled()) {
                logger.debug("projectBySysname result = " + result);
            }
            if ((result != null) && (result.size() == 1)
                    && (((Map) result.get(0)).containsKey("METADATAURL")) && ((((Map) result.get(0)).get("METADATAURL") instanceof String))
                    && (!((Map) result.get(0)).get("METADATAURL").toString().equals(""))) {
                return ((Map) result.get(0)).get("METADATAURL").toString();
            }

            resultMap = xmlutil.doURL(COREWSURL, "getSystemMetadataURL", xmlutil.createXML(args), null);
            if (logger.isDebugEnabled()) {
                logger.debug("getMetadataURL result = " + resultMap);
            }
            if (resultMap.containsKey("MetadataURL")) {
                return resultMap.get("MetadataURL").toString();
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        } catch (XMLUtilException e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
        return null;
    }

    private String getTemplateFullPath(String path, String login, String password) throws Exception {
        String project = "insurance";
        String metadataURL = getMetadataURL(login, password, project);
        String fullPath;
        if ((metadataURL == null) || (metadataURL.equals(""))) {
            metadataURL = Config.getConfig("reportws").getParam("rootPath", "C:/bea/workshop92/METADATA/REPORTS/");

            fullPath = metadataURL + path;
        } else {
            if ((!metadataURL.endsWith("/")) && (!metadataURL.endsWith("\\"))) {
                metadataURL = metadataURL + File.separator;
            }
            if (!path.contains("REPORTS/")) {
                metadataURL = metadataURL + "REPORTS/";
            }
            fullPath = metadataURL + path;
        }
        return fullPath;
    }

    private String getUploadFolder() {
        return Config.getConfig("reportws").getParam("reportOutput", System.getProperty("user.home"));
    }

    private String getReportFullPath(String reportName, String format) {
        String path = getUploadFolder();
        File reportFile = new File(path + File.separator + reportName + format);

        if (!reportFile.exists()) {
            reportFile = new File(path + File.separator + reportName + ".odt");
            if (!reportFile.exists()) {
                reportFile = new File(path + File.separator + reportName + ".ods");
                if (!reportFile.exists()) {
                    reportFile = null;
                }

            }
        }
        String fullPath = "";
        try {
            if (reportFile.getCanonicalPath().startsWith(path)) {
                if (reportFile != null) {
                    fullPath = reportFile.getAbsolutePath();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fullPath;
    }

    /**
     * Fix т.к. система обрезает в датах секунды
     *
     * @param date         - дата
     * @param isFixSeconds - флаг добавления секунд до 59 (важно!!! должно быть
     *                     59, никаких 50 )
     * @return
     * @throws java.lang.Exception
     */
    protected XMLGregorianCalendar dateToXMLGC(Date date, boolean isFixSeconds) throws Exception {
        XMLGregorianCalendar result = null;
        if (date != null) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(date);
            if (isFixSeconds) {
                gc.set(Calendar.SECOND, MAX_SECOND);
            }
            try {
                result = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
            } catch (DatatypeConfigurationException ex) {
                throw new Exception("Error convert Date to XMLGregorianCalendar", ex);
            }

        }
        return result;
    }

    protected XMLGregorianCalendar dateToXMLGC(Date date) throws Exception {
        return dateToXMLGC(date, false);
    }

    protected String getStringByDate(Date date) throws Exception {
        XMLGregorianCalendar xmlgc = dateToXMLGC(date);
        String result = "";
        if (xmlgc != null) {
            String maybeZero = (xmlgc.getDay() < 10) ? "0" : "";
            result = "«" + maybeZero + xmlgc.getDay() + "» " + MONTH_NAMES[xmlgc.getMonth() - 1] + " " + xmlgc.getYear() + "";
        }
        return result;
    }

    private boolean callSignSersvice(String srcPath, String destPath, String location, String reason, String login, String password) throws Exception {
        Map<String, Object> signParam = new HashMap<String, Object>();
        signParam.put("SOURCEFILENAME", srcPath);
        signParam.put("SIGNEDFILENAME", destPath);
        signParam.put("LOCATION", location);
        signParam.put("REASON", reason);
        boolean result;
        try {
            this.callService(SIGNWS_SERVICE_NAME, "dsSignPDF", signParam, login, password);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    protected String getCurrByCodeToNum(String currCode, long amValueInt) {
        String[] CurrNames = RUB_NAMES;
        if (currCode.equalsIgnoreCase("RUB")) {
            CurrNames = RUB_NAMES;
        }
        if (currCode.equalsIgnoreCase("USD")) {
            CurrNames = USD_NAMES;
        }
        if (currCode.equalsIgnoreCase("EUR")) {
            CurrNames = EUR_NAMES;
        }
        String result;

        int rank10 = (int) (amValueInt % 100 / 10);
        int rank = (int) (amValueInt % 10);
        if (rank10 == 1) {
            result = CurrNames[2];
        } else {
            switch (rank) {
                case 1:
                    result = CurrNames[0];
                    break;
                case 2:
                case 3:
                case 4:
                    result = CurrNames[1];
                    break;
                default:
                    result = CurrNames[2];
            }
        }
        return result;
    }

    // todo: заменить на запрос справочника из базы
    private static String getGenderStr(Object genderCode) {
        if (genderCode != null) {
            if ("0".equals(genderCode.toString())) {
                return ("Мужской");
            } else {
                return ("Женский");
            }
        }
        return ("Не указан");
    }

    private void copyMapEntries(Map<String, Object> source, Map<String, Object> destination, String... keys) {
        for (String key : keys) {
            destination.put(key, source.get(key));
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMortgagePrintReport(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        logger.debug("Вызван dsMortgagePrintReport с параметрами:\n\n" + params.toString() + "\n");

        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> contrQRes1 = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsMortgageContractBrowseListByParamEx", params, login, password);
        // this.selectQuery("dsSisContractBrowseListByParamEx", "dsSisContractBrowseListByParamExCount", contrQParam);
        Map<String, Object> contrQRes = contrQRes1;

        //Map<String, Object> reportData = params;
        Map<String, Object> reportData = new HashMap<String, Object>();

        Map<String, Object> qProdRepParam = new HashMap<String, Object>();
        Map<String, Object> qProdRepRes = null;
        boolean isB2BModeFlag = isB2BMode(params);
        if (isB2BModeFlag) {
            qProdRepParam.put("PRODCONFID", 1011);
            qProdRepRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductReportBrowseListByParamEx", qProdRepParam, login, password);
        } else {
            qProdRepParam.put("PRODCONFID", 1090);
            qProdRepRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductReportBrowseListByParamEx", qProdRepParam, login, password);
        }
        //qProdRepParam.put("PRODCONFID", params.get("PRODCONFID"));
//        Map<String, Object> qProdRepRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductReportBrowseListByParamEx", qProdRepParam, login, password);
        //Map<String, Object> printRes = null;
        Map<String, Object> filePathList = new HashMap<String, Object>();
        if (qProdRepRes.get(RESULT) != null) {
            List<Map<String, Object>> listRep = WsUtils.getListFromResultMap(qProdRepRes);
            String repFormat = ".pdf";
            List<Map<String, Object>> repDataList = new ArrayList<Map<String, Object>>();

            // копирование без изменений
            copyMapEntries(contrQRes, reportData,
                    "CONTRPOLNUM", // Номер текущего договора (105ХХХХХХХ)
                    "INSDOCSERIES", // Документ, серия
                    "INSDOCNUMBER", // Документ, номер
                    "INSISSUEDBY", // Документ, кем выдан
                    "INSADDRESSTEXT1", // Адрес регистрации
                    "INSPHONE", // Мобильный телефон
                    "INSEMAIL", // E-mail
                    "CREDCONTRNUM" // Номер кредитного (ипотечного) договора
            );

            // Срок действия полиса
            Date startDate = getDateParam(contrQRes.get("STARTDATE"));
            Date finishDate = getDateParam(contrQRes.get("FINISHDATE"));
            reportData.put("STARTDATESTR", getStringByDate(startDate));
            reportData.put("FINISHDATESTR", getStringByDate(finishDate));

            // дата заключения текущего договора
            Date docDate = getDateParam(contrQRes.get("DOCUMENTDATE"));
            reportData.put("DOCUMENTDATESTR", getStringByDate(docDate));

            // ФИО
            String middleName = getStringParam(contrQRes.get("INSMIDDLENAME"));
            if (!middleName.isEmpty()) {
                middleName = " " + middleName;
            }
            reportData.put("INSURERSTR", contrQRes.get("INSLASTNAME") + " " + contrQRes.get("INSFIRSTNAME") + middleName);

            // Дата рождения
            Date birthDate = getDateParam(contrQRes.get("INSBIRTHDATE"));
            reportData.put("INSBIRTHDATESTR", getStringByDate(birthDate));

            // Пол
            String genderStr = getGenderStr(contrQRes.get("INSGENDER"));
            reportData.put("INSGENDERSTR", genderStr);

            // адрес имущества и его вид
            String chosenName = "Квартира";
            String insObjAddress = "";
            if (isB2BModeFlag) {
                insObjAddress = ((List<Map<String, Object>>) contrQRes.get("INSOBJGROUPLIST")).get(0).get("ADDRESSTEXT1").toString();
                String typesysname = ((List<Map<String, Object>>) contrQRes.get("INSOBJGROUPLIST")).get(0).get("PROPERTYSYSNAME").toString();
                //insObjAddress = contrQRes.get("INSOBJADDRESSTEXT1").toString();
                if ("flat".equalsIgnoreCase(typesysname)) {
                    chosenName = "Квартира";
                } else {
                    chosenName = "Дом";
                }
            } else {
                List<Map<String, Object>> objects = (List<Map<String, Object>>) contrQRes.get("OBJECTS");
                if ((objects != null) && (!objects.isEmpty())) {
                    Map<String, Object> obj = objects.get(0);
                    insObjAddress = obj.get("ADDRESSTEXT1").toString();
                    if ("flat".equalsIgnoreCase(obj.get("OBJTYPESYSNAME").toString())) {
                        chosenName = "Квартира";
                    } else {
                        chosenName = "Дом";
                    }
                }
            }
            reportData.put("INSOBJNAMESTR", chosenName);
            reportData.put("INSOBJADDRESSSTR", insObjAddress);

            // дата выдачи документа
            Date issueDate = getDateParam(contrQRes.get("INSISSUEDATE"));
            reportData.put("INSISSUEDATESTR", getStringByDate(issueDate));

            // дата заключения ипотечного договора
            Date credDate = getDateParam(contrQRes.get("credDate"));
            reportData.put("credDateSTR", getStringByDate(credDate));

            // Страховая сумма и её валюта
            // todo: "810" заменить на код валюты - договора могут создаваться и не в рублях, но пока только в VZR (он же TRAVEL)
            // todo: INSAMVALUECURRNUMCODE не формируется методом по запросу сведений договора
            Double insAmValue = getDoubleParam(contrQRes.get("INSAMVALUE"));
            String insAmCurrNumCode = getStringParam(contrQRes.get("INSAMVALUE" + "CURRNUMCODE"));
            if (insAmCurrNumCode.isEmpty()) {
                insAmCurrNumCode = "810";
            }
            String insAmStr = AmountUtils.amountToString(insAmValue, insAmCurrNumCode);
            String insAmNumStr = moneyFormatter.format(insAmValue);
            // отдельно валюта не требуется, уже учитывается при вызове amountToString
            //reportData.put("INSAMCURRENCYSTR", getCurrByCodeToNum("RUB", insAmValue.longValue())); 
            reportData.put("INSAMVALUESTR", insAmNumStr + " (" + insAmStr + ")");

            // Страховая премия и её валюта            
            Double premValue = getDoubleParam(contrQRes.get("PREMIUM"));
            String premCurrNumCode = getStringParam(contrQRes.get("PREMIUM" + "CURRNUMCODE"));
            if (premCurrNumCode.isEmpty()) {
                premCurrNumCode = "810";
            }
            String premStr = AmountUtils.amountToString(premValue, premCurrNumCode);
            String premNumStr = moneyFormatter.format(premValue);
            // отдельно валюта не требуется, уже учитывается при вызове amountToString
            //reportData.put("PREMCURRENCYSTR", getCurrByCodeToNum("RUB", premValue.longValue())); 
            reportData.put("PREMIUMSTR", premNumStr + " (" + premStr + ")");
            String templateFieldName = "PAGEFLOWNAME";
            String servicename = INSPOSWS_SERVICE_NAME;
            String binfileBrowseMethod = "dsContract_BinaryFile_BinaryFileBrowseListByParam";
            String binfileCreateMethod = "dsContract_BinaryFile_createBinaryFileInfo";
            if (isB2BModeFlag) {
                templateFieldName = "TEMPLATENAME";
                servicename = B2BPOSWS_SERVICE_NAME;
                binfileBrowseMethod = "dsB2BContract_BinaryFile_BinaryFileBrowseListByParam";
                binfileCreateMethod = "dsB2BContract_BinaryFile_createBinaryFileInfo";
            }

            for (Map<String, Object> report : listRep) {
                if (report.get(templateFieldName) != null) {
                    String path = report.get(templateFieldName).toString();
                    // если это odt или ods то это шаблоны, и их сразу в сервис получения данных, 
                    if ((path.indexOf(".odt") > 1) || (path.indexOf(".ods") > 1)) {
                        // в sis для дома и для квартиры разные шаблоны, при страховании ипотеки - одинаковые
                        logger.debug("template print");
                        Map<String, Object> binQueryParams = new HashMap<String, Object>();
                        binQueryParams.put("OBJID", contrQRes.get("CONTRID"));
                        binQueryParams.put("FILETYPENAME", report.get("NAME"));
                        binQueryParams.put(RETURN_AS_HASH_MAP, "TRUE");
                        Map<String, Object> findRes = this.callService(servicename, binfileBrowseMethod, binQueryParams, login, password);
                        if ((findRes.get("BINFILEID") != null) && ((Long) findRes.get("BINFILEID") > 0)) {
                            String fullPath = getTemplateFullPath(path, login, password);
                            fullPath = fullPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
                            logger.debug("binfile exist");
                            logger.debug(fullPath);
                            if (!fullPath.toUpperCase().contains("UPLOAD")) {
                                //файл прикреплен без пути - берем путь из конфига
                                String upFilePath = getUploadFilePath();
                                filePathList.put(findRes.get("FILENAME").toString(), upFilePath + findRes.get("FILEPATH").toString());
                            } else {
                                filePathList.put(findRes.get("FILENAME").toString(), findRes.get("FILEPATH").toString());
                            }
                        } else {
                            logger.debug("binfile not exist");
                            Map<String, Object> printParams = new HashMap<String, Object>();
                            //
                            // проставляем дату вручения сейчас т.к. отправка будет сразу за формированием полиса
                            //result.put("PRINTDATESTR", getStringByDate(new Date()));
                            // 

                            printParams.put("templateName", path);
                            printParams.put("REPORTFORMATS", repFormat);
                            // замена всех nullевых объектов в карте на пустые строки
                            // todo: перенести в dsLibreOfficeReportCreate, если недопустимость null актуальна для всех отчетов
                            setNullsToEmptyStrings(reportData);
                            printParams.put("REPORTDATA", reportData);
                            printParams.put(RETURN_AS_HASH_MAP, "TRUE");

                            logger.debug("Вызван метод dsLibreOfficeReportCreate с параметрами:\n\n" + printParams.toString() + "\n");
                            Map<String, Object> printRes = this.callService(LIBREOFFICEREPORTSWS_SERVICE_NAME, "dsLibreOfficeReportCreate", printParams, login, password);
                            logger.debug("printRes: " + printRes.toString());

                            if (printRes.get("REPORTDATA") != null) {
                                Map<String, Object> printedReportData = (Map<String, Object>) printRes.get("REPORTDATA");
                                printedReportData.put("templateName", "Страховой полис");
                                repDataList.add(printedReportData);
                                //String reportName = "";
                                if (printedReportData.get("reportName") != null) {
                                    String reportName = printedReportData.get("reportName").toString();
                                    String fullPath = getReportFullPath(reportName, repFormat);
                                    fullPath = fullPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
                                    String destPath = fullPath.substring(0, fullPath.length() - 4) + "_signed"
                                            + fullPath.substring(fullPath.length() - 4, fullPath.length());

                                    boolean signRes = callSignSersvice(fullPath, destPath, "Сбербанк страхование", "Покупка полиса", login, password);
                                    logger.debug("signed: " + String.valueOf(signRes));

                                    if (signRes) {
                                        File f = new File(destPath);
                                        if (f.exists()) {
                                            fullPath = destPath;
                                            Map<String, Object> binParams = new HashMap<String, Object>();
                                            binParams.put("OBJID", params.get("CONTRID"));
                                            String realFormat = fullPath.substring(fullPath.length() - 4);

                                            binParams.put("FILENAME", report.get("NAME").toString() + realFormat);
                                            // файлы из uploadPath храним без пути.
                                            binParams.put("FILEPATH", reportName + "_signed.pdf"); //f.getPath());
                                            binParams.put("FILESIZE", f.length());
                                            binParams.put("FILETYPEID", 15);
                                            binParams.put("FILETYPENAME", report.get("NAME"));//"Полис подписанный");
                                            binParams.put("NOTE", "");
                                            logger.debug("binfile Create: " + binParams.toString());
                                            this.callService(servicename, binfileCreateMethod, binParams, login, password);
                                        }
                                    }
                                    if (!fullPath.isEmpty()) {
                                        String realFormat = fullPath.substring(fullPath.length() - 4);
                                        filePathList.put(report.get("NAME").toString() + realFormat, fullPath);
                                    }
                                }
                            }
                        }
                    } else {
                        String fullPath = getTemplateFullPath(path, login, password);
                        logger.debug("template attach " + fullPath);
                        fullPath = fullPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
                        filePathList.put(report.get("NAME").toString() + repFormat, fullPath);
                    }
                }
            }
            filePathList.put("REPORTDATALIST", repDataList);
        }

        Map<String, Object> res = new HashMap<String, Object>();
        res.put(RESULT, filePathList);

        logger.debug("Вызов метода dsMortgagePrintReport завершен с результатом:\n\n" + res.toString() + "\n");

        return res;
    }

    // замена всех nullевых объектов в карте на пустые строки
    // todo: перенести в dsLibreOfficeReportCreate, если недопустимость null актуальна для всех отчетов
    private void setNullsToEmptyStrings(Map<String, Object> data) {
        data.put("NULL", null);
        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value == null) {
                data.put(key, "");
            }
        }
    }

    /**
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod()
    public Map<String, Object> dsCallMortgagePrintAndSendEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        logger.debug("dsCallMortgagePrintAndSendEx start");
        String guid = null;
        String action = "";
        String url = null;
        String hash = null;
        Map<String, Object> contrQParam = new HashMap<String, Object>();
        Map<String, Object> dataMap = (Map<String, Object>) params.get("DATAMAP");
        if (dataMap != null) {
            hash = dataMap.get("hash").toString();
            url = dataMap.get("url").toString();
            if (dataMap.get("action") != null) {
                action = dataMap.get("action").toString();
            }
            guid = base64Decode(hash);
            contrQParam.put("EXTERNALID", guid);
        }
        // Вызов из сервиса
        if ((guid == null) && (params.get("CONTRID") != null)) {
            contrQParam.put("CONTRID", params.get("CONTRID"));
        } else {
            if (guid == null) {
                throw new Exception("Service need required params DATAMAP for calling from angular or CONTRID for calling from java");
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();

        contrQParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> contrQRes1 = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsMortgageContractBrowseListByParamEx", contrQParam, login, password);
        // this.selectQuery("dsSisContractBrowseListByParamEx", "dsSisContractBrowseListByParamExCount", contrQParam);
        Map<String, Object> contrQRes = contrQRes1;
        contrQRes.put(USEB2B_PARAM_NAME, params.get(USEB2B_PARAM_NAME));
        String sessionId = getStringParam(contrQRes.get("sessionId"));
        Map<String, Object> printQParam = new HashMap<String, Object>();
        printQParam.put("CONTRID", contrQRes.get("CONTRID"));
        // 
        logger.debug("CONTRID: " + contrQRes.get("CONTRID").toString());
        if (contrQRes.get("EXTERNALID") != null) {
            hash = base64Encode(contrQRes.get("EXTERNALID").toString());
        } else {
            throw new Exception("Contract attribute EXTERNALID is empty, but required.");
        }

        boolean isB2BModeFlag = isB2BMode(params);
        if ((url == null) || (url.isEmpty())) {
            // загрузка url из конфига продукта.
            //Map<String, Object> productConfigRes = this.callService(INSPRODUCTWSWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", productConfQueryParams, login, password);
            Map<String, Object> productConfigRes = getProductDefaultValueByProdConfId(contrQRes.get("PRODCONFID"), isB2BModeFlag, login, password);
            if ((productConfigRes != null) && (productConfigRes.get("PRODUCTURL") != null)) {
                url = (String) productConfigRes.get("PRODUCTURL");
            } else {
                url = "";
            }
        }

        logger.debug("url " + url);
        Map<String, Object> printQRes = null;
        if ("".equals(action) || "sendEmail".equals(action)) {
            logger.debug("print docs: " + printQParam.toString());
            //printQRes = dsMortgagePrintReport(contrQRes, login, password);
            printQRes = this.callService(SIGNBIVSBERPOSWS_SERVICE_NAME, "dsMortgagePrintReport", contrQRes, login, password);
            //logger.debug("printing result: " + printQRes.toString());
        }
        getIsurerEmail(contrQRes, login, password);
        String email = contrQRes.get("PersonalEmail").toString();
        String phone = contrQRes.get("MobilePhone").toString();
        result.put("action", action);
        logger.debug("action: " + action);
        if ("".equals(action) || "sendSms".equals(action)) {
            if ((phone != null) && (!phone.isEmpty())) {
                logger.debug("sms send: " + phone);
                Map<String, Object> sendRes = sendSms(phone, contrQRes, sessionId, login, password);
                result.put("SMSRES", sendRes);
            }
        }
        if ("".equals(action) || "sendEmail".equals(action)) {
            if ((email != null) && (!email.isEmpty())) {
                String emailText = generateEmailText(url, hash, contrQRes, login, password);
                logger.debug("email send: " + email);
                Map<String, Object> sendRes = sendReportByEmailInCreate(printQRes, contrQRes, emailText, email, sessionId, isB2BModeFlag, login, password);
                result.put("EMAILRES", sendRes);
            }
        }
        logger.debug("dsCallMortgagePrintAndSendEx finish");
        return result;
    }
}
