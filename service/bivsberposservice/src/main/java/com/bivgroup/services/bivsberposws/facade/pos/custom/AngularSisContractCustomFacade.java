/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomFacade.SERVICE_NAME;
import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomFacade.base64Decode;
import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomFacade.base64Encode;
import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularReportCustomFacade.MAX_SECOND;
import static com.bivgroup.services.bivsberposws.facade.pos.custom.AngularReportCustomFacade.MONTH_NAMES;
import com.bivgroup.services.bivsberposws.system.Constants;
import com.bivgroup.services.bivsberposws.system.SmsSender;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.apache.log4j.Logger; // import java.util.logging.Logger
import java.util.regex.Matcher;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang.StringUtils;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;
import ru.diasoft.utils.currency.AmountUtils;

/**
 *
 * @author 1
 */
@BOName("AngularSisContractCustom")
public class AngularSisContractCustomFacade extends AngularContractCustomBaseFacade {

    private static final String LIBREOFFICEREPORTSWS_SERVICE_NAME = Constants.LIBREOFFICEREPORTSWS;
    private static final String SIGNWS_SERVICE_NAME = Constants.SIGNWS;

    public static final String[] RUB_NAMES = {"рубль", "рубля", "рублей", "копейка", "копейки", "копеек", "M"};
    public static final String[] EUR_NAMES = {"евро", "евро", "евро", "евроцент", "евроцента", "евроцентов", "M"};
    public static final String[] USD_NAMES = {"доллар", "доллара", "долларов", "цент", "цента", "центов", "M"};

    private String smsText = "";
    private String smsUser = "";
    private String smsPwd = "";
    private String smsFrom = "";

    private void remapSisContr(Map<String, Object> contrMap, Map<String, Object> master, String login, String password) throws Exception {
        // премия
        contrMap.put("PREMVALUE", master.get("yearPremium"));

        // дата оформления
        Date docDate = (Date) contrMap.get("DOCUMENTDATE");

        // дата начала договора
        GregorianCalendar startDateGC = new GregorianCalendar();
        startDateGC.setTime(docDate); // "Дата начала договора должна быть «Дата оформления» + ...
        startDateGC.add(Calendar.DATE, 1); // ... + 1 день"
        Date startDate = startDateGC.getTime();
        contrMap.put("STARTDATE", startDate);

        // дата окончания договора
        GregorianCalendar finishDateGC = new GregorianCalendar();
        finishDateGC.setTime(startDate); // "соответственно дата окончания должна быть «Дата начала» + ...
        finishDateGC.add(Calendar.YEAR, 1); // ... + 1 страховой год"
        Date finishDate = finishDateGC.getTime();
        contrMap.put("FINISHDATE", finishDate);

        // срок действия договора
        //int duration2 = finishDateGC.getActualMaximum(Calendar.DAY_OF_YEAR); // неверно для даты начала или окончания в январе/феврале високосного года
        //
        // срок действия договора
        long startDateInMillis = startDateGC.getTimeInMillis();
        long finishDateInMillis = finishDateGC.getTimeInMillis();
        // в сутках (24*60*60*1000) милисекунд
        int duration = (int) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000));
        // duration -= 1; // вычитать один день не требуется - уже учтено при отбросе дробной части выше
        contrMap.put("DURATION", duration);

        // валюта
        contrMap.put("PREMCURRENCYID", 1L);
        contrMap.put("INSAMCURRENCYID", 1L);
        contrMap.put("CURRENCYRATE", 1L);

        //contrExt
        master.put("dopPackageList", master.get("protectTypeList"));
        contrMap.put("movDetailAmValSum", master.get("movableSum"));
        contrMap.put("movDetailAmValOst", master.get("movableOst"));

    }

    private void saveContrRiskList(Map<String, Object> risk, Long contrObjId, Long contrId, Long prodRiskId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        // риск может быть выключен
        if (getBooleanParam(risk.get("checked"), Boolean.TRUE)) {
            params.put("CONTROBJID", contrObjId);
            params.put("CONTRID", contrId);
            params.put("CURRENCYID", 1L);
            params.put("INSAMCURRENCYID", 1L);
            params.put("PREMCURRENCYID", 1L);
            params.put("PRODRISKID", prodRiskId);
            params.put("INSAMVALUE", risk.get("INSAMVALUE"));
            params.put("PREMVALUE", risk.get("PREMIUM"));
            this.callService(INSPOSWS_SERVICE_NAME, "dsContractRiskCreate", params, login, password);
        }
    }

    private Map<String, Object> saveInsObj(Map<String, Object> insObj, Map<String, Object> contrMap, Map<String, Object> master,
            Map<String, Object> insObjAddress, List<Map<String, Object>> prodRiskList, Long movableDetailDataVerId, String login, String password) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Long contrId = getLongParam(contrMap.get("CONTRID"));
        Long insObjNodeId = createNode(login, password);
        Map<String, Object> saveParams = new HashMap<String, Object>();

        String typeSysName = getStringParam(insObj.get("typeSysName"));
        // сохранить адрес имущества
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("ADDRESSDATA", insObjAddress);
        params.put("NAME", typeSysName);
        // params.put("NAME", insObj.get(""));

        params.put("CONTRID", contrId);
        params.put("ROWSTATUS", 1L);

        Map<String, Object> cpRes = this.callService(INSPOSWS_SERVICE_NAME, "dsContractPropertySave", params, login, password);
        Long contrPropertyId = getLongParam(cpRes.get("CONTRPROPERTYID"));

        saveParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
        saveParams.put("INSOBJNODEID", insObjNodeId);
        saveParams.put("FACINGTYPE", insObj.get("facingType"));
        saveParams.put("HOUSEHAS", insObj.get("HOUSEHAS"));
        if (insObj.get("NAME") == null) {
            insObj.put("NAME", insObj.get("name"));
        }
        saveParams.put("NAME", insObj.get("NAME"));
        saveParams.put("NOTE", insObj.get("NOTE"));
        saveParams.put("OBJAREA", insObj.get("space"));
        saveParams.put("OBJTYPESYSNAME", typeSysName);
        saveParams.put("PRODYEARSYSNAME", insObj.get("buildYear"));
        saveParams.put("WALMATERIAL", insObj.get("woodInWal"));
        saveParams.put("CONTRPROPERTYID", contrPropertyId);

        String insObjServiceName = getInsObjServiceName(typeSysName);
        Map<String, Object> saveRes = this.callService(BIVSBERPOSWS_SERVICE_NAME, insObjServiceName, saveParams, login, password);
        Long insObjId = getLongParam(saveRes.get("INSOBJID"));
        //очень надолго зависает
        //updateNodeActiveVersion(insObjNodeId, insObjId, login, password);

        // Создать объект страхования
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
        //получаем риск конструктива, по объекту
        // риск конструктива у каждого объекта свой.
//        Map<String, Object> construct = getRiskFromObj(insObj, "construct");
//        saveContrRiskList(construct,contrObjId, contrId);
        // риски за исключением конструктива только у основного объекта, поэтому сохраняются отдельно
        // распределения лимита покрытия по объектам нет. поэтому у каждого объекта будет сохранен полный риск.
        List<Map<String, Object>> riskList = (List<Map<String, Object>>) insObj.get("RISKLIST");
        int riskCount = riskList.size();
        for (Map<String, Object> risk : riskList) {
            String sysName = getStringParam(risk.get("SYSNAME"));
            if (!sysName.isEmpty()) {
                List<Map<String, Object>> prodRiskFilterList = CopyUtils.filterSortedListByStringFieldName(prodRiskList, "SYSNAME", sysName);
                Long prodRiskId = null;
                if (!prodRiskFilterList.isEmpty()) {
                    prodRiskId = getLongParam(prodRiskFilterList.get(0).get("PRODRISKID"));
                }
                // если квартира, то объект только 1 и все риски сохраняем на нем
                if ("flat".equalsIgnoreCase(typeSysName)) {
                    saveContrRiskList(risk, contrObjId, contrId, prodRiskId, login, password);
                } else {
                    // для остальных объектов с привязкой к объекту сохраняем только конструктив, остальные риски привязываем только к договору

                    if ("construct".equalsIgnoreCase(sysName)) {
                        saveContrRiskList(risk, contrObjId, contrId, prodRiskId, login, password);
                    } else {
                        saveContrRiskList(risk, null, contrId, prodRiskId, login, password);
                    }
                }
            }
        }

        // сохраняем детализацию движимого имущества, если страховой объект "Дом"
        if ("flat".equalsIgnoreCase(typeSysName)) {
            List<Map<String, Object>> moveList = (List<Map<String, Object>>) master.get("movableList");
            if (moveList != null) {
                if (!moveList.isEmpty()) {
                    for (Map<String, Object> moveObj : moveList) {
                        saveMovableDetail(moveObj, contrId, contrObjId, movableDetailDataVerId, login, password);
                    }
                }
            }
        }
        // сохраняем детализацию движимого имущества, если страховой объект "Дом"
        if ("house".equalsIgnoreCase(typeSysName)) {
            if (riskCount > 1) {
                // у первого дома рисков больше одного
                List<Map<String, Object>> moveList = (List<Map<String, Object>>) master.get("movableList");
                if (moveList != null) {
                    if (!moveList.isEmpty()) {
                        for (Map<String, Object> moveObj : moveList) {
                            Map<String, Object> houseType = (Map<String, Object>) moveObj.get("HOUSE");
                            if (houseType != null) {
                                if (houseType.get("SYSNAME") != null) {
                                    if ("firstHouse".equalsIgnoreCase(houseType.get("SYSNAME").toString())) {
                                        saveMovableDetail(moveObj, contrId, contrObjId, movableDetailDataVerId, login, password);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // у второго дома только 1 риск
                List<Map<String, Object>> moveList = (List<Map<String, Object>>) master.get("movableList");
                if (moveList != null) {
                    if (!moveList.isEmpty()) {
                        for (Map<String, Object> moveObj : moveList) {
                            Map<String, Object> houseType = (Map<String, Object>) moveObj.get("HOUSE");
                            if (houseType != null) {
                                if (houseType.get("SYSNAME") != null) {
                                    if ("secondHouse".equalsIgnoreCase(houseType.get("SYSNAME").toString())) {
                                        saveMovableDetail(moveObj, contrId, contrObjId, movableDetailDataVerId, login, password);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        result.put("CONTRPROPERTYID", contrPropertyId);
        result.put("INSOBJNODEID", insObjNodeId);
        result.put("INSOBJID", insObjId);
        result.put("CONTROBJID", contrObjId);
        return result;
    }

    private void saveMovableDetail(Map<String, Object> movableObj, Long contrId, Long contrObjId, Long movableObjHbDataVerId, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("contrId", contrId);
        params.put("contrObjId", contrObjId);
        params.put("insAmValue", movableObj.get("sum"));
        params.put("name", movableObj.get("name"));
        Map<String, Object> objType = (Map<String, Object>) movableObj.get("TYPE");
        if (objType != null) {
            if (objType.get("SYSNAME") != null) {
                params.put("typeSysName", objType.get("SYSNAME"));
            }
        }
        //params.put("typeSysName", movableObj.get("typeSysName"));
        Map<String, Object> houseType = (Map<String, Object>) movableObj.get("HOUSE");
        if (houseType != null) {
            if (houseType.get("SYSNAME") != null) {
                params.put("houseSysName", houseType.get("SYSNAME"));
            }
        }
        //params.put("houseSysName", movableObj.get("houseSysName"));
        params.put("HBDATAVERID", movableObjHbDataVerId);
        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> result = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordCreate", params, login, password);

    }

    private String getInsObjServiceName(String typeSysName) {
        String result = "";
        if ("flat".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjFlatCreate";
        }
        if ("house".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjHouseCreate";
        }
        if ("sauna".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjSaunaCreate";
        }
        if ("other".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjOtherCreate";
        }
        if ("movable".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjMovableCreate";
        }
        if ("go".equalsIgnoreCase(typeSysName)) {
            result = "dsInsObjGOCreate";
        }
        return result;
    }

    protected void remapInsurer(Map<String, Object> insurer) {
        insurer.put("CITIZENSHIP", insurer.get("citizenship"));
        insurer.put("LASTNAME", insurer.get("surname"));
        insurer.put("FIRSTNAME", insurer.get("name"));
        insurer.put("MIDDLENAME", insurer.get("middlename"));
        DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
        DateFormat df2 = new SimpleDateFormat("ddMMyyyy");
        String date = insurer.get("birthDateStr").toString();

        try {
            insurer.put("BIRTHDATE", df1.parse(date));
        } catch (ParseException ex) {
            //logger.error("parse birthdate error", ex);
            try {
                insurer.put("BIRTHDATE", df2.parse(date));
            } catch (ParseException ex1) {
                logger.error("parse birthdate error", ex1);
            }
        }
        insurer.put("SEX", 0);
        if (insurer.get("gender") != null) {
            if ("female".equalsIgnoreCase(insurer.get("gender").toString())) {
                insurer.put("SEX", 1);
            }
        }

        Map<String, Object> cont = (Map<String, Object>) insurer.get("contacts");

        insurer.put("CONTACTPHONEMOBILE", cont.get("phone"));

        String email = "";
        Map<String, Object> emailMap = (Map<String, Object>) cont.get("email");
        if (emailMap != null) {
            if (emailMap.get("login") != null) {
                if (emailMap.get("domen") != null) {
                    Map<String, Object> domenMap = (Map<String, Object>) emailMap.get("domen");
                    if (domenMap.get("SYSNAME") != null) {
                        email = emailMap.get("login").toString() + domenMap.get("SYSNAME").toString();
                    }
                }
            }
        }

        insurer.put("PREVCONTACTEMAIL", email);
        insurer.put("CONTACTEMAIL", email);

        Map<String, Object> pass = (Map<String, Object>) insurer.get("passport");

        insurer.put("DOCTYPESYSNAME", pass.get("typeId"));
        insurer.put("DOCSERIES", pass.get("series"));
        insurer.put("DOCNUMBER", pass.get("number"));
        String date1 = pass.get("issueDateStr").toString();
        try {
            insurer.put("ISSUEDATE", df1.parse(date1));
        } catch (ParseException ex) {
            try {
                insurer.put("ISSUEDATE", df2.parse(date1));
            } catch (ParseException ex1) {
                logger.error("parse birthdate error", ex1);
            }
        }

        insurer.put("ISSUEDBY", pass.get("issuePlace"));
        // insurer.put("ISSUERCODE", pass.get("insPassIssueCode"));
        Map<String, Object> insAddressMap = new HashMap<String, Object>();
        Map<String, Object> addrMap = (Map<String, Object>) insurer.get("address");

        insAddressMap.put("addrSysName", "RegisterAddress");

        Map<String, Object> regionMap = (Map<String, Object>) addrMap.get("region");
        insAddressMap.put("eRegion", regionMap.get("NAME"));
        insAddressMap.put("regionCode", regionMap.get("CODE"));

        Map<String, Object> cityMap = (Map<String, Object>) addrMap.get("city");
        insAddressMap.put("eCity", cityMap.get("NAME"));
        insAddressMap.put("cityCode", cityMap.get("CODE"));

        Map<String, Object> streetMap = (Map<String, Object>) addrMap.get("street");
        insAddressMap.put("eStreet", streetMap.get("NAME"));
        insAddressMap.put("streetCode", streetMap.get("CODE"));
        insAddressMap.put("eIndex", streetMap.get("POSTALCODE"));

        insAddressMap.put("eHouse", addrMap.get("house"));
        insAddressMap.put("eCorpus", addrMap.get("housing"));
        insAddressMap.put("eBuilding", addrMap.get("building"));
        insAddressMap.put("eFlat", addrMap.get("flat"));
        List<Map<String, Object>> addressList = new ArrayList<Map<String, Object>>();
        addressList.add(insAddressMap);
        insurer.put("INSADDRESSDATA", addressList);
    }

    /**
     * метод, создаст договор ВЗР, со всеми необходимыми сущностями.
     *
     * @param params
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"OBJMAP"})
    public Map<String, Object> dsSisContractCreateEx(Map<String, Object> params) throws Exception {
        logger.debug("dsSisContractCreateEx start");
        Map<String, Object> result = new HashMap<String, Object>();
        if (params.get("OBJMAP") != null) {
            Map<String, Object> contrMapIn = (Map<String, Object>) params.get("OBJMAP");
            Map<String, Object> contrMap = (Map<String, Object>) contrMapIn.get("obj");//

            if (!contrMap.isEmpty()) {
                Map<String, String> contrEmptyRequiredFields = new HashMap<String, String>();
                // пока оставляем перечень обязательных полей от hib т.к. описания отдельного не было, а продукт очень похож
                if (checkRequiredFields(contrMapIn, contrEmptyRequiredFields, sisRequiredFields)) {
                    logger.debug("required field valid. begin save");
                    String login = params.get(WsConstants.LOGIN).toString();
                    String password = params.get(WsConstants.PASSWORD).toString();
                    //получаем конфигурацию продукта
                    Long prodVerId = getLongParam(contrMap.get("prodVerId"));
                    Long prodConfId = getLongParam(contrMap.get("prodConfId"));
                    contrMap.put("PRODCONFID", prodConfId);
                    // Map<String, Object> productConfigRes = this.callService(INSPRODUCTWSWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", productConfQueryParams, login, password);
                    Map<String, Object> productConfigRes = getProductDefaultValueByProdConfId(prodConfId, isB2BMode(params), login, password);

                    Long movableDetailDataVerId = getLongParam(productConfigRes.get("MovableDetailDataVerId"));
                    //создаем contrNode
                    Long contrNodeId = сontrNodeСreate(0L, 0L, login, password);
                    contrMap.put("CONTRNODEID", contrNodeId);
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

                    Map<String, Object> master = (Map<String, Object>) contrMap.get("master");
                    BigDecimal promoValue = getPromoValueByCodeAndProdVerId(master.get("promo"), prodVerId.longValue(), login, password);
                    contrMap.put("promoCode", master.get("promo"));
                    contrMap.put("promoValue", promoValue);

                    Map<String, Object> insurer = (Map<String, Object>) master.get("insurer");
                    remapInsurer(insurer);
                    //создаем лицо
                    //создаем персону в CRM
                    logger.debug("create insurer");
                    Map<String, Object> personMap = participantCreate(insurer, login, password);
                    logger.debug(personMap.toString());
                    contrMap.put("PARTICIPANTID", personMap.get("PARTICIPANTID"));
                    contrMap.put("PERSONID", personMap.get("PERSONID"));
                    //создаем договор
                    Map<String, Object> userInfo = findDepByLogin(login, password);
                    Long sellerId = getSellerId(userInfo, login, password);
                    contrMap.put("SELLERID", sellerId);
                    contrMap.put("ORGSTRUCTID", userInfo.get("DEPARTMENTID"));
                    contrMap.put("SELFORGSTRUCTID", userInfo.get("DEPARTMENTID"));
                    //SELLERID - сделать продавца для сайта? или искать его по логину
                    contrMap.put("PRODVERID", prodVerId);

                    remapSisContr(contrMap, master, login, password);

                    Long contrId = contrCreate(contrMap, login, password);
                    contrMap.put("CONTRID", contrId);
                    //устанавливаем текущую версию договора в contrNode
                    setRightsOnContr(userInfo, contrId, login, password);

                    сontrNodeUpdate(contrNodeId, contrId, login, password);
                    // генерим номер договора
                    String contrNum = generateContrNum(prodConfId, login, password);

                    contrMap.put("CONTRPOLSER", "001EP");
                    contrMap.put("CONTRPOLNUM", "106" + contrNum);
                    contrMap.put("CONTRNUMBER", "001EP106" + contrNum);
                    contrMap.put("ORGSTRUCT", userInfo.get("DEPARTMENTID"));
                    // Пересчет премии.
                    Map<String, Object> calcRes = doCalc(master, login, password);
                    contrMap.put("protectTypeList", master.get("protectTypeList"));
                    Map<String, Object> prodRiskParams = new HashMap<String, Object>();
                    prodRiskParams.put("PRODVERID", prodVerId);
                    Map<String, Object> prodRiskRes = this.callService(INSPRODUCTWS_SERVICE_NAME, "dsProductRiskBrowseListByParam", prodRiskParams, login, password);
                    List<Map<String, Object>> prodRiskList = (List<Map<String, Object>>) prodRiskRes.get(RESULT);
                    CopyUtils.sortByStringFieldName(prodRiskList, "SYSNAME");
                    // сохранить объекты страхования, риски
                    Map<String, Object> insObj = (Map<String, Object>) master.get("insObj");
                    Map<String, Object> insObjAddress = (Map<String, Object>) insObj.get("address");
                    mapInsObjAddress(insObjAddress);

                    boolean calcValid = true;
                    Double prem = 0.0;
                    Double insAmVal = 0.0;
                    if (calcRes != null) {
                        List<Map<String, Object>> objectList = (List<Map<String, Object>>) calcRes.get("objectList");
                        if (objectList != null) {
                            if (!objectList.isEmpty()) {
                                //сохранение объектов
                                for (Map<String, Object> object : objectList) {
                                    Double objPrem = 0.0;
                                    Double insAmValObj = 0.0;
                                    String typeSysName = getStringParam(insObj.get("typeSysName"));
                                    List<Map<String, Object>> rlist = (List<Map<String, Object>>) object.get("RISKLIST");
                                    for (Map<String, Object> risk : rlist) {
                                        if (getBooleanParam(risk.get("checked"), true)) {

                                            BigDecimal jsPrem = getBigDecimalParam(risk.get("prem"));
                                            BigDecimal calcPrem = getBigDecimalParam(risk.get("PREMIUM"));
                                            jsPrem = jsPrem.setScale(2, BigDecimal.ROUND_HALF_UP);
                                            calcPrem = calcPrem.setScale(2, BigDecimal.ROUND_HALF_UP);
                                            Double jsPremD = jsPrem.doubleValue();
                                            Double calcPremD = calcPrem.doubleValue();
                                            risk.put("prem", jsPremD * promoValue.doubleValue());
                                            risk.put("PREMIUM", calcPremD * promoValue.doubleValue());

                                            if (jsPremD.compareTo(calcPremD) != 0) {
                                                calcValid = false;
                                                logger.error("calcError: jsPrem=" + String.valueOf(jsPremD) + " calcPrem="
                                                        + String.valueOf(calcPremD));
                                                logger.error("object= " + object.toString());
                                            }
                                            // сумма по объекту квартиры - содержит все риски
                                            if ("flat".equalsIgnoreCase(typeSysName)) {
                                                objPrem = objPrem + calcPremD;
                                                insAmValObj = insAmValObj + getDoubleParam(risk.get("INSAMVALUE"));
                                            } else {
                                                String sysName = getStringParam(risk.get("SYSNAME"));
                                                //для остальных объектов - только по риску конструктив
                                                if ("construct".equalsIgnoreCase(sysName)) {
                                                    objPrem = objPrem + calcPremD;
                                                    insAmValObj = insAmValObj + getDoubleParam(risk.get("INSAMVALUE"));
                                                } else {
                                                    //тем не менее в общей сумме по договору все риски тоже должны участвовать
                                                    prem = prem + calcPremD;
                                                    insAmVal = insAmVal + getDoubleParam(risk.get("INSAMVALUE"));
                                                }
                                            }
                                        }
                                    }
                                    prem = prem + objPrem;
                                    insAmVal = insAmVal + insAmValObj;
                                    object.put("PREMVALUE", objPrem * promoValue.doubleValue());
                                    object.put("INSAMVALUE", insAmValObj);
                                    //контроль на совпадение расчета с формы и с калькулятора

                                    //сохраняем объект и риски
                                    saveInsObj(object, contrMap, master, insObjAddress, prodRiskList, movableDetailDataVerId, login, password);
                                }
                            } else {
                                logger.error("Калькулятор вернул пустой список объектов");
                            }
                        } else {
                            logger.error("Калькулятор не вернул список объектов");
                        }

                    } else {
                        logger.error("Ошибка калькулятора");
                    }

                    contrMap.put("PREMVALUE", prem * promoValue.doubleValue());
                    contrMap.put("INSAMVALUE", insAmVal);
                    contrMap.put("isCalcValid", calcValid);

                    // сохраняем номер договора
                    сontrUpdate(contrMap, login, password);

                    contrExtCreate(contrMap, login, password);
                    readContrExtId(contrMap, login, password);

                    contrMap.put("HASH", base64Encode(contrMap.get("GUID").toString()));
                    result.put(RESULT, contrMap);
                } else {
                    //не все обязательные поля заполнены. создавать договор нельзя.
                    result.put("Status", "requiredFieldError");
                    result.put("EmptyRequiredFields", contrEmptyRequiredFields);
                }
            } else {
                result.put("Status", "emptyInputMap");
            }
        } else {
            result.put("Status", "emptyInputMap");
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsSisContractBrowseListByParamEx(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        params.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> result = this.selectQuery("dsSisContractBrowseListByParamEx", "dsSisContractBrowseListByParamExCount", params);
        result = WsUtils.getFirstItemFromResultMap(result);
        Long contrId = getLongParam(result.get("CONTRID"));
        Long prodConfId = getLongParam(result.get("PRODCONFID"));

        //Map<String, Object> productConfigRes = this.callService(INSPRODUCTWSWS_SERVICE_NAME, "dsProductDefaultValueByProdConfId", productConfQueryParams, login, password);
        Map<String, Object> productConfigRes = getProductDefaultValueByProdConfId(prodConfId, isB2BMode(params), login, password);

        Long movableDetailDataVerId = getLongParam(productConfigRes.get("MovableDetailDataVerId"));

        Map<String, Object> contrObjParam = new HashMap<String, Object>();
        contrObjParam.put("CONTRID", contrId);
        Map<String, Object> contrObjRes = this.selectQuery("dsSisContractObjBrowseListByParamEx", "dsSisContractObjBrowseListByParamExCount", contrObjParam);
        Map<String, Object> contrRiskRes = this.callService(INSPOSWS_SERVICE_NAME, "dsContractRiskBrowseListByContrIdJoinProdRisk", contrObjParam, login, password);
        //this.selectQuery("dsContractRiskBrowseListByParam", "dsContractRiskBrowseListByParamCount", contrObjParam);
        List<Map<String, Object>> riskList = WsUtils.getListFromResultMap(contrRiskRes);
        if (contrObjRes != null) {
            if (contrObjRes.get(RESULT) != null) {
                List<Map<String, Object>> contrObjList = (List<Map<String, Object>>) contrObjRes.get(RESULT);
                if (!contrObjList.isEmpty()) {
                    for (Map<String, Object> contrObj : contrObjList) {
                        Long contrObjId = getLongParam(contrObj.get("CONTROBJID"));
                        List<Map<String, Object>> contrObjRiskList = new ArrayList<Map<String, Object>>();
                        for (Map<String, Object> risk : riskList) {
                            Long riskContrObjId = getLongParam(risk.get("CONTROBJID"));
                            if (riskContrObjId == null) {
                                // если ContrObj пустой, то риск вставляем в список первого дома. для загрузки на форму
                                String objName = getStringParam(contrObj.get("NOTE"));
                                if (!objName.isEmpty()) {
                                    if ("firstHouse".equalsIgnoreCase(objName)) {
                                        contrObjRiskList.add(risk);
                                    }
                                }

                            } else {
                                if (contrObjId.compareTo(riskContrObjId) == 0) {
                                    contrObjRiskList.add(risk);
                                }
                            }
                        }
                        contrObj.put("RISKLIST", contrObjRiskList);
                    }
                }

                result.put("CONTROBJLIST", contrObjList);
            }
        }
        // считываем детализацию движимого имущества

        Map<String, Object> hbParam = new HashMap<String, Object>();
        hbParam.put("HBDATAVERID", movableDetailDataVerId);
        hbParam.put("contrId", contrId);
        Map<String, Object> hbRes = this.callService(INSTARIFICATORWS_SERVICE_NAME, "dsHandbookRecordBrowseListByParam", hbParam, login, password);
        List<Map<String, Object>> movableList = WsUtils.getListFromResultMap(hbRes);
        result.put("movableList", movableList);
        return result;
    }

    /**
     * метод, выберет договор по хешу. сделан, для получения данных договора на
     * форму..
     *
     * @param params
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsSisContractBrowseEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        if (params.get("CONTRMAP") == null) {
            if (params.get("CONTRID") != null) {
                Map<String, Object> contrMapIn = new HashMap<String, Object>();
                contrMapIn.put("CONTRID", params.get("CONTRID"));
                params.put("CONTRMAP", contrMapIn);
            }
        }
        if (params.get("CONTRMAP") != null) {
            Map<String, Object> contrMapIn = (Map<String, Object>) params.get("CONTRMAP");
            String login = params.get(WsConstants.LOGIN).toString();
            String password = params.get(WsConstants.PASSWORD).toString();
            Long contrId;
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
            browseParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> contrMap = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsSisContractBrowseListByParamEx", browseParams, login, password);
            contrId = getLongParam(contrMap.get("CONTRID"));

            Map<String, Object> master = new HashMap<String, Object>();
            //1. считываем договор
            //2. считываем расширенные параметры договора
            //3. считываем страхователя               
            //4. считываем объекты страхования
            //   4.1 считываем риски по объектам

            if (contrMap.get("CONTRNODEID") != null) {
                Long contrNodeId = getLongParam(contrMap.get("CONTRNODEID"));
                // получение план графика
                Map<String, Object> planParams = new HashMap<String, Object>();
                planParams.put("CONTRNODEID", contrNodeId);
                //planParams.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> qPlanRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentFactBrowseListByParamEx", planParams, login, password);
                contrMap.put("PAYMENTLIST", qPlanRes);
            }
            // получение графика оплаты
            Map<String, Object> factParams = new HashMap<String, Object>();
            factParams.put("CONTRID", contrId);
            Map<String, Object> qFactRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentBrowseListByParam", factParams, login, password);
            contrMap.put("PAYMENTSCHEDULELIST", qFactRes.get(RESULT));
            result.put("CONTRMAP", contrMap);
            return result;
        } else {
            result.put("Status", "emptyInputMap");
        }
        return result;
    }

    private Map<String, Object> doCalc(Map<String, Object> master, String login, String password) throws Exception {
        Map<String, Object> calcMap = new HashMap<String, Object>();
        calcMap.put("ReturnAsHashMap", "TRUE");
        calcMap.put("CALCVERID", 1080);
        List<Map<String, Object>> insuredList = new ArrayList<Map<String, Object>>();

        Map<String, Object> insObj = (Map<String, Object>) master.get("insObj");
        if (insObj.get("protectTypeList") != null) {
            String protectTypeList = insObj.get("protectTypeList").toString();
            if (!protectTypeList.isEmpty()) {
                master.put("protectTypeList", protectTypeList);
                if (protectTypeList.indexOf("noSubLimits") < 0) {
                    calcMap.put("noSubLimits", 0L);
                } else {
                    calcMap.put("noSubLimits", 1L);
                }
                if (protectTypeList.indexOf("dopRisks") < 0) {
                    calcMap.put("dopRisks", 0L);
                } else {
                    calcMap.put("dopRisks", 1L);
                }
            }
        }

        insuredList.add(insObj);
        String typeSysName = getStringParam(insObj.get("typeSysName"));
        if ("flat".equalsIgnoreCase(typeSysName)) {
            insObj.put("NAME", "Квартира");
            insObj.put("NOTE", "firstHouse");
        } else {
            insObj.put("NAME", "Дом");
            insObj.put("NOTE", "firstHouse");
        }
        if ("house".equalsIgnoreCase(typeSysName)) {
            if (getBooleanParam(master.get("secondHouse"), Boolean.FALSE)) {
                // добавляем второй дом, только если он есть
                Map<String, Object> insObj1 = (Map<String, Object>) master.get("insObj1");
                if (insObj1 != null) {
                    insObj1.put("NAME", "Второй дом");
                    insObj1.put("NOTE", "secondHouse");
                    insuredList.add(insObj1);

                }
            }
            List<Map<String, Object>> otherInsObjList = (List<Map<String, Object>>) master.get("otherInsObjList");
            insuredList.addAll(otherInsObjList);
        }
        for (Map<String, Object> insured : insuredList) {
            insured.put("INSAMVALUE", insured.get("sum"));
            insured.put("OBJTYPESYSNAME", getStringParam(insured.get("typeSysName")));
            List<Map<String, Object>> riskList = (List<Map<String, Object>>) insured.get("protectLevelList");
            for (Map<String, Object> risk : riskList) {
                risk.put("INSAMVALUE", risk.get("sum"));
            }
            insured.put("RISKLIST", riskList);
            insured.put("WALMATERIAL", getStringParam(insured.get("woodInWal")));
            insured.put("PRODYEARSYSNAME", getStringParam(insured.get("buildYear")));
            if (getStringParam(insured.get("hasBSFG")).isEmpty()) {
                insured.put("HOUSEHAS", "0");
            } else {
                insured.put("HOUSEHAS", getStringParam(insured.get("hasBSFG")));
            }
            /* if (insured.get("houseHas") != null) {
             if (!insured.get("houseHas").toString().isEmpty()) {
             insured.put("HOUSEHAS", 1L);
             }
             }*/
        }

        calcMap.put("objectList", insuredList);

        Map<String, Object> calcRes = this.callService(WsConstants.INSTARIFICATORWS, "calculateByCalculatorVersionID", calcMap, login, password);
        return calcRes;
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
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return null;
        }
        return null;
    }

    private String getTemplateFullPath(String path, String login, String password) throws Exception {
        String project = "insurance";
        String metadataURL = getMetadataURL(login, password, project);
        String fullPath = "";
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
     * @param date - дата
     * @param isFixSeconds - флаг добавления секунд до 59 (важно!!! должно быть
     * 59, никаких 50 )
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
            if (xmlgc.getDay() < 10) {
                result = "«0" + xmlgc.getDay() + "» " + MONTH_NAMES[xmlgc.getMonth() - 1] + " " + xmlgc.getYear() + "";
            } else {
                result = "«" + xmlgc.getDay() + "» " + MONTH_NAMES[xmlgc.getMonth() - 1] + " " + xmlgc.getYear() + "";
            }
        }
        return result;
    }

    private boolean callSignSersvice(String srcPath, String destPath, String location, String reason, String login, String password) throws Exception {
        Map<String, Object> signParam = new HashMap<String, Object>();
        signParam.put("SOURCEFILENAME", srcPath);
        signParam.put("SIGNEDFILENAME", destPath);
        signParam.put("LOCATION", location);
        signParam.put("REASON", reason);
        boolean result = false;
        try {
            this.callService(SIGNWS_SERVICE_NAME, "dsSignPDF", signParam, login, password);
            result = true;
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    protected String getCurrByCodeToNum(String curCode, long amValueInt) {
        String[] CurrNames = RUB_NAMES;
        if (curCode.equalsIgnoreCase("RUB")) {
            CurrNames = RUB_NAMES;
        }
        if (curCode.equalsIgnoreCase("USD")) {
            CurrNames = USD_NAMES;
        }
        if (curCode.equalsIgnoreCase("EUR")) {
            CurrNames = EUR_NAMES;
        }
        String result = CurrNames[0];

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

    protected Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsSisPrintReport(Map<String, Object> params, String login, String password) throws Exception {
        logger.debug("dsSisPrintReport start");
        Map<String, Object> result = params;
        List<Map<String, Object>> contrObjList = (List<Map<String, Object>>) result.get("CONTROBJLIST");
        String reportNameContent = "flat";
        String insObjAddress = "";
        if (contrObjList != null) {
            if (!contrObjList.isEmpty()) {
                Map<String, Object> obj = contrObjList.get(0);
                insObjAddress = contrObjList.get(0).get("ADDRESSTEXT1").toString();
                if ("flat".equalsIgnoreCase(obj.get("OBJTYPESYSNAME").toString())) {
                    reportNameContent = "flat";
                } else {
                    reportNameContent = "house";
                }
                if (contrObjList.get(0).get("POSTALCODE") != null) {
                    insObjAddress = contrObjList.get(0).get("POSTALCODE").toString() + " " + insObjAddress;
                }
            }
        }
        
        boolean isB2BModeFlag = isB2BMode(params);

        String productServiceName; //       имя сервиса для получения данных продукта (списка документов)
        String filesServiceName; //         имя сервиса для работы с файлами
        String reportBrowseMethodName; //   имя метода для получения списка документов для продукта
        String filesBrowseMethodName; //    имя метода для получения списка файлов контракта
        String filesCreateMethodName; //    имя метода для создания файла к контрактк
        Object prodConfID; //               идентификатор продукта
        String templateFieldName; //        ключ, указывающий на имя шаблона
        if (isB2BModeFlag) {
            productServiceName = B2BPOSWS_SERVICE_NAME;
            filesServiceName = B2BPOSWS_SERVICE_NAME;
            reportBrowseMethodName = "dsB2BProductReportBrowseListByParamEx";
            filesBrowseMethodName = "dsB2BContract_BinaryFile_BinaryFileBrowseListByParam";
            filesCreateMethodName = "dsB2BContract_BinaryFile_createBinaryFileInfo";
            prodConfID = convertValue(result.get("PRODCONFID"), getProdConfIDConvertRules(), Direction.TO_SAVE);
            templateFieldName = "TEMPLATENAME";
        } else {
            productServiceName = INSPRODUCTWS_SERVICE_NAME;
            filesServiceName = INSPOSWS_SERVICE_NAME;
            reportBrowseMethodName = "dsProductReportBrowseListByParamEx";
            filesBrowseMethodName = "dsContract_BinaryFile_BinaryFileBrowseListByParam";
            filesCreateMethodName = "dsContract_BinaryFile_createBinaryFileInfo";
            prodConfID = result.get("PRODCONFID");
            templateFieldName = "PAGEFLOWNAME";
        }        
        
        Map<String, Object> qProdRepParam = new HashMap<String, Object>();
        qProdRepParam.put("PRODCONFID", prodConfID);
        Map<String, Object> qProdRepRes = this.callService(productServiceName, reportBrowseMethodName, qProdRepParam, login, password);
        Map<String, Object> filePathList = new HashMap<String, Object>();
        if (qProdRepRes.get(RESULT) != null) {
            List<Map<String, Object>> listRep = WsUtils.getListFromResultMap(qProdRepRes);
            logger.debug("listRep: " + listRep.toString());
            String repFormat = ".pdf";
            List<Map<String, Object>> repDataList = new ArrayList<Map<String, Object>>();

            Date birthdate = getDateParam(result.get("INSBIRTHDATE"));
            Date issuedate = getDateParam(result.get("INSISSUEDATE"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String middleName = getStringParam(result.get("INSMIDDLENAME"));
            if (!middleName.isEmpty()) {
                middleName = " " + middleName;
            }
            result.put("INSURERSTR", result.get("INSLASTNAME") + " " + result.get("INSFIRSTNAME") + middleName
                    + ", " + sdf.format(birthdate) + ", " + result.get("INSADDRESSTEXT2")
                    + ", " + result.get("INSDOCSERIES") + " " + result.get("INSDOCNUMBER")
                    + ", " + result.get("INSISSUEDBY") + " " + sdf.format(issuedate));

            result.put("INSOBJADDRESSSTR", insObjAddress);
            Date docDate = getDateParam(result.get("DOCUMENTDATE"));
            Date startDate = getDateParam(result.get("STARTDATE"));
            Date finishDate = getDateParam(result.get("FINISHDATE"));
            result.put("DOCUMENTDATESTR", getStringByDate(docDate));
            result.put("STARTDATESTR", getStringByDate(startDate));
            result.put("FINISHDATESTR", getStringByDate(finishDate));

            String protectTypeList = getStringParam(result.get("protectTypeList"));
            String riskCodeStr = "О";
            result.put("isDopRisk", "FALSE");

            if (protectTypeList.toUpperCase().indexOf("dopRisks".toUpperCase()) >= 0) {
                result.put("isDopRisk", "TRUE");
                if (reportNameContent.equalsIgnoreCase("house")) {
                    riskCodeStr = riskCodeStr + " ДРЗЖ ДРМВ ДРТ ДРВЭ";
                } else {
                    riskCodeStr = riskCodeStr + " ДРМВ ДРТ ДРВЭ";
                }
            }

            result.put("isnoSubLimits", "FALSE");

            if (protectTypeList.toUpperCase().indexOf("noSubLimits".toUpperCase()) >= 0) {
                result.put("isnoSubLimits", "TRUE");
            }
            result.put("riskCodeStr", riskCodeStr);
            Double prem = getDoubleParam(result.get("PREMVALUE"));

            String premString = AmountUtils.amountToString(prem, "810");

            result.put("PREMSTR", premString);
            result.put("PREMCURRENCYSTR", getCurrByCodeToNum("RUB", prem.longValue()));

            result.put("conInsAmValue", "");
            result.put("conPremValue", "");
            result.put("moveInsAmValue", "");
            result.put("movePremValue", "");
            result.put("moveInsAmValueOst", "");
            result.put("movePremValueOst", "");
            result.put("moveDetailInsAmValue", "");
            result.put("moveDetailPremValue", "");
            result.put("goInsAmValue", "");
            result.put("goPremValue", "");
            result.put("ifInsAmValue", "");
            result.put("ifPremValue", "");

            for (Map<String, Object> contrObj : contrObjList) {
                boolean isFirstObj = false;
                if (contrObj.get("OBJTYPESYSNAME") != null) {
                    if ("house".equalsIgnoreCase(contrObj.get("OBJTYPESYSNAME").toString())) {
                        if (contrObj.get("NAME") != null) {
                            if ("firstHouse".equalsIgnoreCase(contrObj.get("NOTE").toString())) {
                                isFirstObj = true;
                                contrObj.put("NAMESTR", "Основной дом");
                            } else {
                                contrObj.put("NAMESTR", "Второй дом");
                            }
                        } else {
                            isFirstObj = true;
                            contrObj.put("NAMESTR", "Основной дом");
                        }
                    }
                    if ("sauna".equalsIgnoreCase(contrObj.get("OBJTYPESYSNAME").toString())) {
                        contrObj.put("NAMESTR", contrObj.get("NAME"));
                    }
                    if ("other".equalsIgnoreCase(contrObj.get("OBJTYPESYSNAME").toString())) {
                        contrObj.put("NAMESTR", contrObj.get("NAME"));
                    }
                    if ("flat".equalsIgnoreCase(contrObj.get("OBJTYPESYSNAME").toString())) {
                        contrObj.put("NAMESTR", "Квартира");
                        isFirstObj = true;
                    }
                }

                List<Map<String, Object>> riskList = (List<Map<String, Object>>) contrObj.get("RISKLIST");
                if (riskList != null) {
                    if (!riskList.isEmpty()) {
                        if (riskList.size() > 0) {
                            // первый объект
                            if (isFirstObj) {
                                if (contrObj.get("FACINGTYPE") != null) {
                                    String facingType = contrObj.get("FACINGTYPE").toString();
                                    if ("base".equalsIgnoreCase(facingType)) {
                                        result.put("MOVEOBJDETAILLIMIT", 25);
                                    }
                                    if ("standart".equalsIgnoreCase(facingType)) {
                                        result.put("MOVEOBJDETAILLIMIT", 50);
                                    }
                                    if ("eurostd".equalsIgnoreCase(facingType)) {
                                        result.put("MOVEOBJDETAILLIMIT", 75);
                                    }
                                    if ("exclusive".equalsIgnoreCase(facingType)) {
                                        result.put("MOVEOBJDETAILLIMIT", 100);
                                    }
                                }
                            }
                            for (Map<String, Object> risk : riskList) {
                                if (risk.get("PRODRISKSYSNAME") != null) {
                                    if ("movables".equalsIgnoreCase(risk.get("PRODRISKSYSNAME").toString())) {
                                        Double moveInsAmValue = getDoubleParam(risk.get("INSAMVALUE"));
                                        Double movePremValue = getDoubleParam(risk.get("PREMVALUE"));
                                        Double moveDetailInsAmValue = 0.0;
                                        Double moveDetailPremValue = 0.0;
                                        if (result.get("movableList") != null) {
                                            List<Map<String, Object>> movableList = (List<Map<String, Object>>) result.get("movableList");
                                            if (!movableList.isEmpty()) {
                                                for (Map<String, Object> movableObj : movableList) {
                                                    Double moInsAmValue = getDoubleParam(movableObj.get("insAmValue"));
                                                    moveDetailInsAmValue = moveDetailInsAmValue + moInsAmValue;

                                                    if (movableObj.get("houseSysName") != null) {
                                                        if ("firstHouse".equalsIgnoreCase(movableObj.get("houseSysName").toString())) {
                                                            movableObj.put("HOUSENAMESTR", "Основной дом");
                                                        } else {
                                                            movableObj.put("HOUSENAMESTR", "Второй дом");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        moveDetailPremValue = movePremValue * moveDetailInsAmValue / moveInsAmValue;
                                        BigDecimal mdpv = new BigDecimal(Double.valueOf(moveDetailPremValue).toString());
                                        mdpv = mdpv.setScale(2, RoundingMode.HALF_UP);
                                        moveDetailPremValue = mdpv.doubleValue();

                                        Double moveInsAmValueOst = moveInsAmValue - moveDetailInsAmValue;
                                        Double movePremValueOst = movePremValue - moveDetailPremValue;

                                        result.put("moveInsAmValue", moveInsAmValue);
                                        result.put("movePremValue", movePremValue);
                                        result.put("moveInsAmValueOst", moveInsAmValueOst);
                                        result.put("movePremValueOst", movePremValueOst);
                                        result.put("moveDetailInsAmValue", moveDetailInsAmValue);
                                        result.put("moveDetailPremValue", moveDetailPremValue);

                                    }
                                    if ("go".equalsIgnoreCase(risk.get("PRODRISKSYSNAME").toString())) {
                                        Double goInsAmValue = getDoubleParam(risk.get("INSAMVALUE"));
                                        Double goPremValue = getDoubleParam(risk.get("PREMVALUE"));
                                        result.put("goInsAmValue", goInsAmValue);
                                        result.put("goPremValue", goPremValue);
                                    }
                                    // может быть только у квартиры
                                    if (reportNameContent.equalsIgnoreCase("flat")) {

                                        if ("insideFacing".equalsIgnoreCase(risk.get("PRODRISKSYSNAME").toString())) {
                                            Double ifInsAmValue = getDoubleParam(risk.get("INSAMVALUE"));
                                            Double ifPremValue = getDoubleParam(risk.get("PREMVALUE"));
                                            result.put("ifInsAmValue", ifInsAmValue);
                                            result.put("ifPremValue", ifPremValue);
                                        }
                                        if ("construct".equalsIgnoreCase(risk.get("PRODRISKSYSNAME").toString())) {
                                            Double conInsAmValue = getDoubleParam(risk.get("INSAMVALUE"));
                                            Double conPremValue = getDoubleParam(risk.get("PREMVALUE"));
                                            result.put("conInsAmValue", conInsAmValue);
                                            result.put("conPremValue", conPremValue);
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

            for (Map<String, Object> report : listRep) {
                if (report.get(templateFieldName) != null) {
                    // для дома и для квартиры разные шаблоны.
                    String path = report.get(templateFieldName).toString();
                    if ((path.indexOf(".odt") > 1) || (path.indexOf(".ods") > 1)) {
                        if (path.toUpperCase().indexOf(reportNameContent.toUpperCase()) >= 0) {
                            boolean isNeedPrint = true;
                            if (path.toUpperCase().indexOf("MovableDetail".toUpperCase()) >= 0) {
                                if ((result.get("movableList") == null) || (((List) result.get("movableList")).isEmpty())) {
                                    isNeedPrint = false;
                                }

                            }
                            if (isNeedPrint) {
                                // если это odt или ods то это шаблоны, и их сразу в сервис получения данных,
                                logger.debug("template print");
                                Map<String, Object> binQueryParams = new HashMap<String, Object>();
                                binQueryParams.put("OBJID", params.get("CONTRID"));
                                binQueryParams.put("FILETYPENAME", report.get("NAME"));

                                binQueryParams.put("ReturnAsHashMap", true);
                                Map<String, Object> findRes = this.callService(filesServiceName, filesBrowseMethodName, binQueryParams, login, password);
                                if ((findRes.get("BINFILEID") != null) && ((Long) findRes.get("BINFILEID") > 0)) {
                                    String fullPath = getTemplateFullPath(path, login, password);
                                    fullPath = fullPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
                                    logger.debug("binfile exist");

                                    logger.debug(fullPath);
                                    if (fullPath.toUpperCase().indexOf("UPLOAD") < 0) {
                                        //файл прикреплен без пути.
                                        //берем путь из конфига
                                        String upFilePath = getUploadFilePath();
                                        filePathList.put(findRes.get("FILENAME").toString(), upFilePath + findRes.get("FILEPATH").toString());
                                    } else {
                                        filePathList.put(findRes.get("FILENAME").toString(), findRes.get("FILEPATH").toString());
                                    }
                                } else {
                                    logger.debug("binfile not exist");
                                    Map<String, Object> printParams = new HashMap<String, Object>();
                                // проставляем дату вручения сейчас т.к. отправка будет сразу за формированием полиса
                                    //result.put("PRINTDATESTR", getStringByDate(new Date()));

                                    // contrObjList
                                    printParams.put("REPORTDATA", result);
                                    printParams.put("templateName", path);
                                    printParams.put("REPORTFORMATS", repFormat);
                                    printParams.put("ReturnAsHashMap", "TRUE");

                                    Map<String, Object> printRes = this.callService(LIBREOFFICEREPORTSWS_SERVICE_NAME, "dsLibreOfficeReportCreate", printParams, login, password);
                                    logger.debug("printRes: " + printRes.toString());

                                    if (printRes.get("REPORTDATA") != null) {
                                        Map<String, Object> reportData = (Map<String, Object>) printRes.get("REPORTDATA");
                                        reportData.put("templateName", "Страховой полис");
                                        repDataList.add(reportData);
                                        String reportName = "";
                                        if (reportData.get("reportName") != null) {
                                            reportName = reportData.get("reportName").toString();
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
                                                    this.callService(filesServiceName, filesCreateMethodName, binParams, login, password);
                                                }
                                            }
                                            if (!fullPath.isEmpty()) {
                                                String realFormat = fullPath.substring(fullPath.length() - 4);
                                                filePathList.put(report.get("NAME").toString() + realFormat, fullPath);
                                            }
                                        }
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
        logger.debug("dsSisPrintReport finish");
        Map<String, Object> res = new HashMap<String, Object>();
        res.put(RESULT, filePathList);
        return res;
    }

    @WsMethod()
    public Map<String, Object> dsCallSisPringAndSendEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        logger.debug("dsCallSisPringAndSendEx start");
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
        if (action.isEmpty()) {
            if (params.get("action") != null) {
                action = params.get("action").toString();
            }
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
        Map<String, Object> contrQRes1 = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsSisContractBrowseListByParamEx", contrQParam, login, password);
//this.selectQuery("dsSisContractBrowseListByParamEx", "dsSisContractBrowseListByParamExCount", contrQParam);
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
        if ((url == null) || ((url != null) && (url.isEmpty()))) {
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
            printQRes = dsSisPrintReport(contrQRes, login, password);
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
        logger.debug("dsCallPringAndSendEx finish");

        return result;
    }

}
