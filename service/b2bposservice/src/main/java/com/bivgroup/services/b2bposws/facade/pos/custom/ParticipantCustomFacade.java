/*
 * Copyright (c) Diasoft 2004-2014
 */
package com.bivgroup.services.b2bposws.facade.pos.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.TranslitStringConverter;
import com.bivgroup.services.b2bposws.system.Constants;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author ilich
 */
@BOName("ParticipantCustom")
public class ParticipantCustomFacade extends B2BBaseFacade {

    // синонимы констант имен веб-сервисов
    private static final String CRMWS_SERVICE_NAME = Constants.CRMWS;
    private static final String INSPOSWS_SERVICE_NAME = Constants.INSPOSWS;
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    // данные для проверки передаваемых при создании участника списков
    // (имя списка, обязательный параметр 1, обязательный параметр 2, ...)
    private static final String[][] participantListsValidationInfo = {
        //{"...List", "...TYPESYSNAME"}, //
        //{"addressList", "ADDRESSTYPESYSNAME", "REGIONKLADR"}, // адреса
        {"addressList", "ADDRESSTYPESYSNAME"/*, "REGIONKLADR"*/}, // адреса
        {"contactList", "CONTACTTYPESYSNAME", "VALUE"}, // контактные данные
        {"documentList", "DOCTYPESYSNAME", "DOCNUMBER"}, // персональные документы
        {"partRegDocList", "REGDOCTYPESYSNAME", "DOCNUMBER"}, // персональные документы
        {"partPositionList", "POSITION"}, // должности
    };

    //<editor-fold defaultstate="collapsed" desc="скопировано без изменений из ?.ParticipantCustomFacade">
    private boolean needToProcess(Map<String, Object> params, String processName) throws Exception {
        if (!params.containsKey(processName)) {
            return true;
        } else if (params.get(processName).toString().equalsIgnoreCase("TRUE")) {
            return true;
        } else {
            return false;
        }
    }

    private Map<String, Object> createPerson(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.put("ReturnAsHashMap", "TRUE");
        createParams.put("FIRSTNAME", params.get("NAME").toString() + "_");
        createParams.put("LASTNAME", params.get("SURNAME"));
        createParams.put("MIDDLENAME", params.get("MIDDLENAME"));
        createParams.put("BIRTHDATE", params.get("BIRTHDATE"));
        createParams.put("GENDER", params.get("SEX"));
        createParams.put("BIRTHPLACE", params.get("BIRTHPLACE"));
        createParams.put("INN", params.get("INN"));
        createParams.put("ISBUSINESSMAN", params.get("ISBUSINESSMAN"));
        createParams.put("CITIZENSHIP", params.get("CITIZENSHIP"));
        createParams.put("PARTICIPANTTYPE", 1);
        createParams.put("ISCLIENT", 0);
        createParams.put("STATE", "Обслуживается");
        String surName = params.get("SURNAME").toString();
        String name = params.get("NAME").toString();
        if ((params.get("MIDDLENAME") != null) && (params.get("MIDDLENAME").toString().compareTo("") != 0)) {
            String middleName = params.get("MIDDLENAME").toString();
            createParams.put("BRIEFNAME", String.format("%s %c. %c.", surName, name.charAt(0), middleName.charAt(0)));
        } else {
            createParams.put("BRIEFNAME", String.format("%s %c.", surName, name.charAt(0)));
        }
        params.put("BRIEFNAME", createParams.get("BRIEFNAME"));
        createParams.put("CREATIONDATE", (new GregorianCalendar()).getTime());
        Map<String, Object> result = this.callService(CRMWS_SERVICE_NAME, "personCreate", createParams, login, password);
        createParams.put("FIRSTNAME", params.get("NAME"));
        createParams.put("PARTICIPANTID", result.get("PARTICIPANTID"));
        this.callService(CRMWS_SERVICE_NAME, "personModify", createParams, login, password);
        result.put("FIRSTNAME", params.get("NAME"));
        return result;

    }

    private void getPersonModifyData(Map<String, Object> result, Map<String, Object> params, String login, String password) throws Exception {
        result.put("FLAG", "UPD");
        result.put("PARTICIPANTID", params.get("PARTICIPANTID"));
        result.put("FIRSTNAME", params.get("NAME"));
        result.put("LASTNAME", params.get("SURNAME"));
        result.put("MIDDLENAME", params.get("MIDDLENAME"));
        result.put("BIRTHDATE", params.get("BIRTHDATE"));
        result.put("GENDER", params.get("SEX"));
        result.put("BIRTHPLACE", params.get("BIRTHPLACE"));
        result.put("INN", params.get("INN"));
        result.put("ISBUSINESSMAN", params.get("ISBUSINESSMAN"));
        result.put("CITIZENSHIP", params.get("CITIZENSHIP"));
        String surName = params.get("SURNAME").toString();
        String name = params.get("NAME").toString();
        if ((params.get("MIDDLENAME") != null) && (params.get("MIDDLENAME").toString().compareTo("") != 0)) {
            String middleName = params.get("MIDDLENAME").toString();
            result.put("BRIEFNAME", String.format("%s %c. %c.", surName, name.charAt(0), middleName.charAt(0)));
        } else {
            result.put("BRIEFNAME", String.format("%s %c.", surName, name.charAt(0)));
        }
        params.put("BRIEFNAME", result.get("BRIEFNAME"));
    }

    private Map<String, Object> createPersonAltName(Map<String, Object> params, Long participantId, String login, String password) throws Exception {
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.put("PARTICIPANTID", participantId);
        createParams.put("ALTNAMETYPESYSNAME", "EnglishName");
        createParams.put("ALTNAME", params.get("ALTNAME"));
        return this.callService(CRMWS_SERVICE_NAME, "altNameCreate", createParams, login, password);
    }

    private void modifyPersonAltName(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> modifyParams = new HashMap<String, Object>();
        modifyParams.put("PARTICIPANTID", params.get("PARTICIPANTID"));
        modifyParams.put("ALTNAMETYPESYSNAME", "EnglishName");
        modifyParams.put("ALTNAME", params.get("ALTNAME"));
        if (params.get("ALTNAMEID") != null) {
            modifyParams.put("ALTNAMEID", params.get("ALTNAMEID"));
            this.callService(CRMWS_SERVICE_NAME, "altNameModify", modifyParams, login, password);
        } else {
            this.callService(CRMWS_SERVICE_NAME, "altNameCreate", modifyParams, login, password);
        }
    }

    private Map<String, Object> createCompanyAltName(Map<String, Object> params, Long participantId, String login, String password) throws Exception {
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.put("PARTICIPANTID", participantId);
        createParams.put("ALTNAMETYPESYSNAME", "EnglishName");
        createParams.put("ALTNAME", params.get("ORGALTNAME"));
        return this.callService(CRMWS_SERVICE_NAME, "altNameCreate", createParams, login, password);
    }

    private void modifyCompanyAltName(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> modifyParams = new HashMap<String, Object>();
        modifyParams.put("PARTICIPANTID", params.get("PARTICIPANTID"));
        modifyParams.put("ALTNAMETYPESYSNAME", "EnglishName");
        modifyParams.put("ALTNAME", params.get("ORGALTNAME"));
        if (params.get("ALTNAMEID") != null) {
            modifyParams.put("ALTNAMEID", params.get("ALTNAMEID"));
            this.callService(CRMWS_SERVICE_NAME, "altNameModify", modifyParams, login, password);
        } else {
            this.callService(CRMWS_SERVICE_NAME, "altNameCreate", modifyParams, login, password);
        }
    }

    private void createPersonDocument(Map<String, Object> params, Long personId, String login, String password) throws Exception {
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.put("PERSONID", personId);
        createParams.put("ISMAIN", 1);
        createParams.put("ISSUSPECT", 0);
        createParams.put("ISINVALID", 0);
        createParams.put("DOCTYPESYSNAME", params.get("DOCTYPESYSNAME"));
        createParams.put("DOCSERIES", params.get("DOCSERIES"));
        createParams.put("DOCNUMBER", params.get("DOCNUMBER"));
        createParams.put("ISSUEDATE", params.get("ISSUEDATE"));
        createParams.put("ISSUEDBY", params.get("ISSUEDBY"));
        createParams.put("ISSUERCODE", params.get("ISSUERCODE"));
        createParams.put("DESCRIPTION", params.get("DOCDESCRIPTION"));
        XMLUtil.convertFloatToDate(createParams);
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "personDocCreate", createParams, login, password);
    }

    private void getPersonModifyDocumentData(Map<String, Object> result, Map<String, Object> params, String login, String password) throws Exception {
        String oldDocTypeSysName = null;
        if (params.containsKey("OLDDOCTYPESYSNAME")) {
            oldDocTypeSysName = params.get("OLDDOCTYPESYSNAME").toString();
        }
        //String docTypeSysName = params.get("DOCTYPESYSNAME").toString();
        if ((oldDocTypeSysName != null) && (oldDocTypeSysName.compareTo("") != 0)) {
            result.put("FLAG", "UPD");
            result.put("PERSONDOCID", params.get("DOCID"));
        } else {
            result.put("FLAG", "ADD");
        }
        result.put("ISMAIN", 1);
        result.put("ISSUSPECT", 0);
        result.put("ISINVALID", 0);
        result.put("DOCTYPESYSNAME", params.get("DOCTYPESYSNAME"));
        result.put("DOCSERIES", params.get("DOCSERIES"));
        result.put("DOCNUMBER", params.get("DOCNUMBER"));
        result.put("ISSUEDATE", params.get("ISSUEDATE"));
        result.put("ISSUEDBY", params.get("ISSUEDBY"));
        result.put("ISSUERCODE", params.get("ISSUERCODE"));
        result.put("DESCRIPTION", params.get("DOCDESCRIPTION"));
    }

    private void createPersonDL(Map<String, Object> params, Long personId, String login, String password) throws Exception {
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.put("PERSONID", personId);
        createParams.put("DOCTYPESYSNAME", "DrivingLicence");
        createParams.put("DOCSERIES", params.get("DLSERIES"));
        createParams.put("DOCNUMBER", params.get("DLNUMBER"));
        createParams.put("ISSUEDATE", params.get("DLDATE"));
        XMLUtil.convertFloatToDate(createParams);
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "personDocCreate", createParams, login, password);
    }

    private void getPersonModifyDLData(Map<String, Object> result, Map<String, Object> params, String login, String password) throws Exception {
        if (params.get("DLID") != null) {
            result.put("FLAG", "UPD");
            result.put("PERSONDOCID", params.get("DLID"));
        } else {
            result.put("FLAG", "ADD");
        }
        result.put("DOCTYPESYSNAME", "DrivingLicence");
        result.put("DOCSERIES", params.get("DLSERIES"));
        result.put("DOCNUMBER", params.get("DLNUMBER"));
        result.put("ISSUEDATE", params.get("DLDATE"));
    }

    private void createDriver(Map<String, Object> params, Long personId, Long participantId, String login, String password) throws Exception {
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.put("BIRTHDATE", params.get("BIRTHDATE"));
        createParams.put("DOCDATE", params.get("DLDATE"));
        createParams.put("DOCSERIES", params.get("DLSERIES"));
        createParams.put("DOCNUMBER", params.get("DLNUMBER"));
        if ((params.get("DLEXPDATEFLAG") != null) && (Long.valueOf(params.get("DLEXPDATEFLAG").toString()).longValue() == 1)) {
            if (params.get("DLEXPDATE") != null) {
                createParams.put("EXPSTARTDATE", params.get("DLEXPDATE"));
            }
        } else if ((params.get("DLEXP") != null) && (!params.get("DLEXP").toString().isEmpty())) {
            Integer year = Integer.valueOf(params.get("DLEXP").toString());
            GregorianCalendar newDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
            createParams.put("EXPSTARTDATE", XMLUtil.convertDateToBigDecimal(newDate.getTime()));
        }
        if (params.get("MIDDLENAME") != null) {
            createParams.put("FULLNAME", String.format("%s %s %s",
                    params.get("SURNAME").toString(), params.get("NAME").toString(), params.get("MIDDLENAME").toString()));
        } else {
            createParams.put("FULLNAME", String.format("%s %s",
                    params.get("SURNAME").toString(), params.get("NAME").toString()));
        }
        if (params.get("SEX").toString().compareTo("0") == 0) {
            createParams.put("GENDER", "М");
        } else {
            createParams.put("GENDER", "Ж");
        }
        createParams.put("PERSONID", personId);
        createParams.put("PARTICIPID", participantId);
        createParams.put("NAME", params.get("NAME"));
        createParams.put("SURNAME", params.get("SURNAME"));
        createParams.put("MIDDLENAME", params.get("MIDDLENAME"));
        createParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsDriverInsertWithDriverNode", createParams, login, password);
        if (qres != null) {
            params.put("DRIVERNODEID_", qres.get("DRIVERNODEID"));
            params.put("DRIVERID_", qres.get("DRIVERID"));
        }
    }

    private boolean isDiffParamsString(Object param1, Object param2) {
        if ((param1 == null) || (param2 == null)) {
            return true;
        }
        if (param1.toString().compareTo(param2.toString()) != 0) {
            return true;
        }
        return false;
    }

    private boolean isDiffParamsDouble(Object param1, Object param2) {
        if ((param1 == null) || (param2 == null)) {
            return true;
        }
        if (Math.abs(Double.valueOf(param1.toString()).doubleValue() - Double.valueOf(param2.toString()).doubleValue()) > 0.0001) {
            return true;
        }
        return false;
    }

    private boolean isDiffParamsDate(Object param1, Object param2) {
        if ((param1 == null) || (param2 == null)) {
            return true;
        }
        Date date1 = (Date) param1;
        Date date2 = (Date) param2;
        if (date1.compareTo(date2) == 0) {
            return true;
        }
        return false;
    }

    private boolean isDiffParamsYears(Object param1, Object param2) {
        if ((param1 == null) || (param2 == null)) {
            return true;
        }
        Date date1 = (Date) param1;
        GregorianCalendar gcDate1 = new GregorianCalendar();
        gcDate1.setTime(date1);
        int year1 = gcDate1.get(Calendar.YEAR);
        Date date2 = (Date) param2;
        GregorianCalendar gcDate2 = new GregorianCalendar();
        gcDate2.setTime(date2);
        int year2 = gcDate2.get(Calendar.YEAR);
        if (year1 != year2) {
            return true;
        }
        return false;
    }

    private void updateDriver(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> readParams = new HashMap<String, Object>();
        //readParams.put("ReturnAsHashMap", "TRUE");
        readParams.put("DRIVERNODEID", params.get("DRIVERNODEID"));
        readParams.put("DRIVERID", params.get("DRIVERID"));
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsDriverBrowseListByParam", readParams, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            qres = ((List<Map<String, Object>>) qres.get(RESULT)).get(0);
            Map<String, Object> updateParams = new HashMap<String, Object>();
            updateParams.putAll(qres);
            updateParams.remove("VERNUMBER");
            if ((params.get("DLEXPDATEFLAG") != null) && (Long.valueOf(params.get("DLEXPDATEFLAG").toString()).longValue() == 1)) {
                if (params.get("DLEXPDATE") != null) {
                    updateParams.put("EXPSTARTDATE", params.get("DLEXPDATE"));
                }
            } else if ((params.get("DLEXP") != null) && (!params.get("DLEXP").toString().isEmpty())) {
                Integer year = Integer.valueOf(params.get("DLEXP").toString());
                GregorianCalendar newDate = new GregorianCalendar(year, 0, 1, 0, 0, 0);
                updateParams.put("EXPSTARTDATE", newDate.getTime());
            }
            updateParams.put("DOCDATE", params.get("DLDATE"));
            updateParams.put("DOCSERIES", params.get("DLSERIES"));
            updateParams.put("DOCNUMBER", params.get("DLNUMBER"));
            XMLUtil.convertFloatToDate(updateParams);
            // проверка на необходимость создания новой версии водителя
            boolean fNeedUpdate = false;
            fNeedUpdate |= (isDiffParamsString(qres.get("DOCSERIES"), updateParams.get("DLSERIES")) == true);
            fNeedUpdate |= (isDiffParamsString(qres.get("DOCNUMBER"), updateParams.get("DLNUMBER")) == true);
            fNeedUpdate |= (isDiffParamsDate(qres.get("DOCDATE"), updateParams.get("DLDATE")) == true);
            fNeedUpdate |= (isDiffParamsYears(qres.get("EXPSTARTDATE"), updateParams.get("EXPSTARTDATE")) == true);
            //
            if (fNeedUpdate) {
                updateParams.put("ReturnAsHashMap", "TRUE");
                Map<String, Object> updRes = this.callService(INSPOSWS_SERVICE_NAME, "dsDriverUpdateWithDriverNode", updateParams, login, password);
                params.put("DRIVERIDNEW", updRes.get("DRIVERID"));
            }
        }
    }

    private void updateContrDrivers(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> updateParams = new HashMap<String, Object>();
        updateParams.put("CONTRID", params.get("CONTRID"));
        updateParams.put("DRIVERID", params.get("DRIVERID"));
        updateParams.put("DRIVERIDNEW", params.get("DRIVERIDNEW"));
        this.callService(INSPOSWS_SERVICE_NAME, "dsContractDriverUpdateEx", updateParams, login, password);
    }

    private void createPersonContact(Long contactPersonId, String contactSysName, String contactValue, String contactValueExt, String login, String password) throws Exception {
        if ((contactValue != null) && (!contactValue.isEmpty())) {
            Map<String, Object> createParams = new HashMap<String, Object>();
            createParams.put("CONTACTPERSONID", contactPersonId);
            createParams.put("CONTACTTYPESYSNAME", contactSysName);
            createParams.put("VALUE", contactValue);
            createParams.put("VALUEEXT", contactValueExt);
            createParams.put("PRIORITY", 0);
            this.callService(CRMWS_SERVICE_NAME, "contactCreate", createParams, login, password);
        }
    }

    private boolean getPersonModifyContactData(Map<String, Object> result, Object contactId, Long contactPersonId, String contactSysName, String contactValue, String contactValueExt, String login, String password) throws Exception {
        boolean isEmptyValue = (contactValue == null) || (contactValue.isEmpty());
        if (((contactId == null) || (contactId.toString().equalsIgnoreCase("0"))) && (isEmptyValue)) {
            return false;
        }
        if ((contactId != null) && (Long.valueOf(contactId.toString()).intValue() > 0)) {
            if (!isEmptyValue) {
                result.put("FLAG", "UPD");
            } else {
                result.put("FLAG", "DEL");
            }
            result.put("CONTACTID", contactId);
        } else {
            result.put("FLAG", "ADD");
        }
        result.put("CONTACTPERSONID", contactPersonId);
        result.put("CONTACTTYPESYSNAME", contactSysName);
        result.put("VALUE", contactValue);
        result.put("VALUEEXT", contactValueExt);
        result.put("PRIORITY", 0);
        return true;
    }

    private String generateFullAddress(Map<String, Object> params, Map<String, Object> map) {
        // TODO: при вызове этой функции для ПОЧТОВОГО (и возможно фактического) адреса - берутся поля от обычного адреса. Необходимо исправить

        StringBuilder res = new StringBuilder();
        if (params.get("POSTALCODE") != null) {
            res.append(params.get("POSTALCODE").toString());
        }
        res.append(",");
        res.append("РОССИЯ");
        res.append(",");
        if (map.get("REGIONFULLNAME") != null) {
            res.append(map.get("REGIONFULLNAME").toString());
        }
        res.append(",");
        if (map.get("ZONEFULLNAME") != null) {
            res.append(map.get("ZONEFULLNAME").toString());
        }
        res.append(",");
        if (map.get("CITYFULLNAME") != null) {
            res.append(map.get("CITYFULLNAME").toString());
            res.append(",");
        }
        if (map.get("PLACEFULLNAME") != null) {
            res.append(map.get("PLACEFULLNAME").toString());
            res.append(",");
        }
        if (params.get("STREET") != null) {
            res.append(params.get("STREET").toString());
        }
        res.append(",");
        if (params.get("HOUSE") != null) {
            res.append(params.get("HOUSE").toString());
        }
        res.append(",");
        if (params.get("CORPUS") != null) {
            res.append(params.get("CORPUS").toString());
        }
        res.append(",");
        if (params.get("BUILDING") != null) {
            res.append(params.get("BUILDING").toString());
        }
        res.append(",");
        if (params.get("FLAT") != null) {
            res.append(params.get("FLAT").toString());
        }

        return res.toString();
    }

    private String generateFullPostAddress(Map<String, Object> params, Map<String, Object> map) {
        StringBuilder res = new StringBuilder();
        if (params.get("POSTPOSTALCODE") != null) {
            res.append(params.get("POSTPOSTALCODE").toString());
        }
        res.append(",");
        res.append("РОССИЯ");
        res.append(",");
        if (map.get("REGIONFULLNAME") != null) {
            res.append(map.get("REGIONFULLNAME").toString());
        }
        res.append(",");
        if (map.get("ZONEFULLNAME") != null) {
            res.append(map.get("ZONEFULLNAME").toString());
        }
        res.append(",");
        if (map.get("CITYFULLNAME") != null) {
            res.append(map.get("CITYFULLNAME").toString());
            res.append(",");
        }
        if (map.get("PLACEFULLNAME") != null) {
            res.append(map.get("PLACEFULLNAME").toString());
            res.append(",");
        }
        if (params.get("POSTSTREET") != null) {
            res.append(params.get("POSTSTREET").toString());
        }
        res.append(",");
        if (params.get("POSTHOUSE") != null) {
            res.append(params.get("POSTHOUSE").toString());
        }
        res.append(",");
        if (params.get("POSTCORPUS") != null) {
            res.append(params.get("POSTCORPUS").toString());
        }
        res.append(",");
        if (params.get("POSTBUILDING") != null) {
            res.append(params.get("POSTBUILDING").toString());
        }
        res.append(",");
        if (params.get("POSTFLAT") != null) {
            res.append(params.get("POSTFLAT").toString());
        }

        return res.toString();
    }

    private String generateFullFactAddress(Map<String, Object> params, Map<String, Object> map) {
        StringBuilder res = new StringBuilder();
        if (params.get("POSTPOSTALCODE") != null) {
            res.append(params.get("POSTPOSTALCODE").toString());
        }
        res.append(",");
        res.append("РОССИЯ");
        res.append(",");
        if (map.get("REGIONFULLNAME") != null) {
            res.append(map.get("REGIONFULLNAME").toString());
        }
        res.append(",");
        if (map.get("ZONEFULLNAME") != null) {
            res.append(map.get("ZONEFULLNAME").toString());
        }
        res.append(",");
        if (map.get("CITYFULLNAME") != null) {
            res.append(map.get("CITYFULLNAME").toString());
            res.append(",");
        }
        if (map.get("PLACEFULLNAME") != null) {
            res.append(map.get("PLACEFULLNAME").toString());
            res.append(",");
        }
        if (params.get("FACTSTREET") != null) {
            res.append(params.get("FACTSTREET").toString());
        }
        res.append(",");
        if (params.get("FACTHOUSE") != null) {
            res.append(params.get("FACTHOUSE").toString());
        }
        res.append(",");
        if (params.get("FACTCORPUS") != null) {
            res.append(params.get("FACTCORPUS").toString());
        }
        res.append(",");
        if (params.get("FACTBUILDING") != null) {
            res.append(params.get("FACTBUILDING").toString());
        }
        res.append(",");
        if (params.get("FACTFLAT") != null) {
            res.append(params.get("FACTFLAT").toString());
        }

        return res.toString();
    }

    private static String getStreetShortType(Long streetType, String streetName, String streetTypeName) {
        switch (streetType.intValue()) {
            case 529:
                return "ул. " + streetName;
            case 514:
                return "пер " + streetName;
            case 518:
                return streetName + " проезд";
            case 531:
                return "ш. " + streetName;
            case 528:
                return "туп. " + streetName;
            case 522:
                return "проулок " + streetName;
            case 519:
                return "пр-кт " + streetName;
            case 516:
                return "пл. " + streetName;
            case 511:
                return "наб. " + streetName;
            case 502:
                return "б-р " + streetName;
            case 501:
                return streetName + " аллея";

            case 507:
                return "кв-л " + streetName;
            case 508:
                return streetName + " км";
            case 510:
                return streetName + " линия";
            case 512:
                return streetName + " остров";
            case 513:
                return streetName + " парк";
            case 517:
                return streetName + " пл-ка";
            case 521:
                return "проселок " + streetName;
            case 524:
                return streetName + " сквер";
            case 533:
                return "аул " + streetName;
            case 562:
                return "канал " + streetName;
            case 563:
                return "гск " + streetName;
            case 570:
                return streetName + " мост";

            default:
                if (streetTypeName.compareTo("") != 0) {
                    return streetTypeName + ". " + streetName;
                } else {
                    return streetName;
                }

        }
    }

    private static String getRegionShortType(Long regionType, String regionName, String regionTypeName) {
        switch (regionType.intValue()) {
            case 101:
                return regionName + " АО";
            case 102:
                return regionName + " Аобл.";
            case 103:
                return "г. " + regionName;
            case 104:
                return regionName + " край";
            case 105:
                return regionName + " обл.";
            case 106:
                return "Респ. " + regionName;
            case 107:
                return regionName + " округ";
            default:
                return regionTypeName + " " + regionName;
        }
    }

    private static String getDistrictShortType(Long districtType, String districtName, String districtTypeName) {
        switch (districtType.intValue()) {
            case 201:
                return districtName + " р-н";
            case 202:
                return "у. " + districtName;
            case 203:
                return "тер. " + districtName;
            case 204:
                return "кожуун " + districtName;
            case 205:
                return districtName + " АО";
            default:
                return districtTypeName + " " + districtName;
        }
    }

    private static String getCityShortType(Long cityType, String cityName, String cityTypeName) {
        switch (cityType.intValue()) {
            case 301:
                return "г. " + cityName;
            case 302:
                return "пгт. " + cityName;
            case 303:
                return "рп " + cityName;
            case 304:
                return "кп. " + cityName;
            case 305:
                return "дп. " + cityName;
            case 306:
                return "с/с " + cityName;
            case 307:
                return "с/а " + cityName;
            case 308:
                return "с/мо " + cityName;
            case 309:
                return "с/о " + cityName;
            case 310:
                return cityName + " волость";
            case 312:
                return "тер. " + cityName;
            case 314:
                return "с/п " + cityName;
            default:
                return cityTypeName + " " + cityName;
        }
    }

    private static String getVillageShortType(Long villageType, String villageName, String villageTypeName) {
        switch (villageType.intValue()) {
            case 401:
                return "аал " + villageName;
            case 402:
                return "аул " + villageName;
            case 403:
                return villageName + " волость";
            case 426:
                return villageName + " промзона";
            case 433:
                return "ст-ца " + villageName;
            case 436:
                return "городок " + villageName;
            case 439:
                return "кв-л " + villageName;
            case 440:
                return "арбан " + villageName;
            default:
                return villageTypeName + ". " + villageName;
        }
    }

    private String generateShortAddress(Map<String, Object> params, Map<String, Object> map) {
        // TODO: при вызове этой функции для ПОЧТОВОГО (и возможно фактического) адреса - берутся поля от обычного адреса. Необходимо исправить

        StringBuilder res = new StringBuilder();

        if (params.get("POSTALCODE") != null) {
            res.append(params.get("POSTALCODE").toString());
        }

        if ((params.get("COUNTRY") != null) && (!(params.get("COUNTRY").toString()).equalsIgnoreCase("РОССИЯ"))) {
            res.append(", ");
            res.append(params.get("COUNTRY").toString());
        }/* else {
         res.append(", ");
         res.append("РОССИЯ");
         }*/

        if (map.get("REGIONNAME") != null) {
            res.append(", ");
            res.append(getRegionShortType(Long.valueOf(map.get("REGIONTYPECODE").toString()), map.get("REGIONNAME").toString(), map.get("REGIONTYPE").toString()));
        }

        if ((map.get("ZONECODE") != null) && (map.get("ZONENAME") != null)) {
            res.append(", ");
            res.append(getDistrictShortType(Long.valueOf(map.get("ZONETYPECODE").toString()), map.get("ZONENAME").toString(), map.get("ZONETYPE").toString()));
        }

        if ((map.get("CITYCODE") != null) && (map.get("CITYNAME") != null)) {
            if ((map.get("REGIONCODE") != null) && (map.get("REGIONCODE").toString().compareTo(map.get("CITYCODE").toString()) != 0)) {
                res.append(", ");
                res.append(getCityShortType(Long.valueOf(map.get("CITYTYPECODE").toString()), map.get("CITYNAME").toString(), map.get("CITYTYPE").toString()));
            }
        }

        if ((map.get("PLACECODE") != null) && (map.get("PLACENAME") != null)) {
            if (((map.get("CITYCODE") != null) && (map.get("CITYCODE").toString().compareTo(map.get("PLACECODE").toString()) != 0)) || (map.get("CITYCODE") == null)) {
                res.append(", ");
                res.append(getVillageShortType(Long.valueOf(map.get("PLACETYPECODE").toString()), map.get("PLACENAME").toString(), map.get("PLACETYPE").toString()));
            }
        }

        if ((params.get("STREET") != null) && (params.get("STREET").toString().compareTo("") != 0)) {
            res.append(", ");
//            if (params.get("STREETKLADR") != null) {
            if (params.get("STREETTYPE") != null) {
                if (params.get("STREETTYPENAME") != null) {
                    res.append(getStreetShortType(Long.valueOf(params.get("STREETTYPE").toString()), params.get("STREET").toString(), params.get("STREETTYPENAME").toString()));
                } else {
                    res.append(getStreetShortType(Long.valueOf(params.get("STREETTYPE").toString()), params.get("STREET").toString(), ""));

                }
//                }
            } else {
                res.append(params.get("STREET").toString());
            }
        }

        if ((params.get("HOUSE") != null) && (params.get("HOUSE").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("д. ");
            res.append(params.get("HOUSE").toString());
        }

        if ((params.get("CORPUS") != null) && (params.get("CORPUS").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("корп. ");
            res.append(params.get("CORPUS").toString());
        }

        if ((params.get("BUILDING") != null) && (params.get("BUILDING").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("стр. ");
            res.append(params.get("BUILDING").toString());
        }

        if ((params.get("FLAT") != null) && (params.get("FLAT").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("кв. ");
            res.append(params.get("FLAT").toString());
        }
        if ((res.length() > 0) && (res.charAt(0) == ',')) {
            res.deleteCharAt(0);
        }

        return res.toString();
    }

    private String generateShortPostAddress(Map<String, Object> params, Map<String, Object> map) {
        StringBuilder res = new StringBuilder();

        if (params.get("POSTPOSTALCODE") != null) {
            res.append(params.get("POSTPOSTALCODE").toString());
        }

        if ((params.get("POSTCOUNTRY") != null) && (!(params.get("POSTCOUNTRY").toString()).equalsIgnoreCase("РОССИЯ"))) {
            res.append(", ");
            res.append(params.get("POSTCOUNTRY").toString());
        }/* else {
         res.append(", ");
         res.append("РОССИЯ");
         }*/

        if (map.get("REGIONNAME") != null) {
            res.append(", ");
            res.append(getRegionShortType(Long.valueOf(map.get("REGIONTYPECODE").toString()), map.get("REGIONNAME").toString(), map.get("REGIONTYPE").toString()));
        }

        if ((map.get("ZONECODE") != null) && (map.get("ZONENAME") != null)) {
            res.append(", ");
            res.append(getDistrictShortType(Long.valueOf(map.get("ZONETYPECODE").toString()), map.get("ZONENAME").toString(), map.get("ZONETYPE").toString()));
        }

        if ((map.get("CITYCODE") != null) && (map.get("CITYNAME") != null)) {
            if ((map.get("REGIONCODE") != null) && (map.get("REGIONCODE").toString().compareTo(map.get("CITYCODE").toString()) != 0)) {
                res.append(", ");
                res.append(getCityShortType(Long.valueOf(map.get("CITYTYPECODE").toString()), map.get("CITYNAME").toString(), map.get("CITYTYPE").toString()));
            }
        }

        if ((map.get("PLACECODE") != null) && (map.get("PLACENAME") != null)) {
            if (((map.get("CITYCODE") != null) && (map.get("CITYCODE").toString().compareTo(map.get("PLACECODE").toString()) != 0)) || (map.get("CITYCODE") == null)) {
                res.append(", ");
                res.append(getVillageShortType(Long.valueOf(map.get("PLACETYPECODE").toString()), map.get("PLACENAME").toString(), map.get("PLACETYPE").toString()));
            }
        }

        if ((params.get("POSTSTREET") != null) && (params.get("POSTSTREET").toString().compareTo("") != 0)) {
            res.append(", ");
//            if (params.get("STREETKLADR") != null) {
            if (params.get("POSTSTREETTYPE") != null) {
                if (params.get("STREETTYPENAME") != null) {
                    res.append(getStreetShortType(Long.valueOf(params.get("POSTSTREETTYPE").toString()), params.get("POSTSTREET").toString(), params.get("STREETTYPENAME").toString()));
                } else {
                    res.append(getStreetShortType(Long.valueOf(params.get("POSTSTREETTYPE").toString()), params.get("POSTSTREET").toString(), ""));

                }
//                }
            } else {
                res.append(params.get("POSTSTREET").toString());
            }
        }

        if ((params.get("POSTHOUSE") != null) && (params.get("POSTHOUSE").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("д. ");
            res.append(params.get("POSTHOUSE").toString());
        }

        if ((params.get("POSTCORPUS") != null) && (params.get("POSTCORPUS").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("корп. ");
            res.append(params.get("POSTCORPUS").toString());
        }

        if ((params.get("POSTBUILDING") != null) && (params.get("POSTBUILDING").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("стр. ");
            res.append(params.get("POSTBUILDING").toString());
        }

        if ((params.get("POSTFLAT") != null) && (params.get("POSTFLAT").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("кв. ");
            res.append(params.get("POSTFLAT").toString());
        }
        if ((res.length() > 0) && (res.charAt(0) == ',')) {
            res.deleteCharAt(0);
        }

        return res.toString();
    }

    private String generateShortFactAddress(Map<String, Object> params, Map<String, Object> map) {
        StringBuilder res = new StringBuilder();

        if (params.get("POSTPOSTALCODE") != null) {
            res.append(params.get("POSTPOSTALCODE").toString());
        }

        if ((params.get("POSTCOUNTRY") != null) && (!(params.get("POSTCOUNTRY").toString()).equalsIgnoreCase("РОССИЯ"))) {
            res.append(", ");
            res.append(params.get("POSTCOUNTRY").toString());
        }/* else {
         res.append(", ");
         res.append("РОССИЯ");
         }*/

        if (map.get("REGIONNAME") != null) {
            res.append(", ");
            res.append(getRegionShortType(Long.valueOf(map.get("REGIONTYPECODE").toString()), map.get("REGIONNAME").toString(), map.get("REGIONTYPE").toString()));
        }

        if ((map.get("ZONECODE") != null) && (map.get("ZONENAME") != null)) {
            res.append(", ");
            res.append(getDistrictShortType(Long.valueOf(map.get("ZONETYPECODE").toString()), map.get("ZONENAME").toString(), map.get("ZONETYPE").toString()));
        }

        if ((map.get("CITYCODE") != null) && (map.get("CITYNAME") != null)) {
            if ((map.get("REGIONCODE") != null) && (map.get("REGIONCODE").toString().compareTo(map.get("CITYCODE").toString()) != 0)) {
                res.append(", ");
                res.append(getCityShortType(Long.valueOf(map.get("CITYTYPECODE").toString()), map.get("CITYNAME").toString(), map.get("CITYTYPE").toString()));
            }
        }

        if ((map.get("PLACECODE") != null) && (map.get("PLACENAME") != null)) {
            if (((map.get("CITYCODE") != null) && (map.get("CITYCODE").toString().compareTo(map.get("PLACECODE").toString()) != 0)) || (map.get("CITYCODE") == null)) {
                res.append(", ");
                res.append(getVillageShortType(Long.valueOf(map.get("PLACETYPECODE").toString()), map.get("PLACENAME").toString(), map.get("PLACETYPE").toString()));
            }
        }

        if ((params.get("FACTSTREET") != null) && (params.get("FACTSTREET").toString().compareTo("") != 0)) {
            res.append(", ");
//            if (params.get("STREETKLADR") != null) {
            if (params.get("FACTSTREETTYPE") != null) {
                if (params.get("STREETTYPENAME") != null) {
                    res.append(getStreetShortType(Long.valueOf(params.get("FACTSTREETTYPE").toString()), params.get("FACTSTREET").toString(), params.get("STREETTYPENAME").toString()));
                } else {
                    res.append(getStreetShortType(Long.valueOf(params.get("FACTSTREETTYPE").toString()), params.get("FACTSTREET").toString(), ""));

                }
//                }
            } else {
                res.append(params.get("FACTSTREET").toString());
            }
        }

        if ((params.get("FACTHOUSE") != null) && (params.get("FACTHOUSE").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("д. ");
            res.append(params.get("FACTHOUSE").toString());
        }

        if ((params.get("FACTCORPUS") != null) && (params.get("FACTCORPUS").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("корп. ");
            res.append(params.get("FACTCORPUS").toString());
        }

        if ((params.get("FACTBUILDING") != null) && (params.get("FACTBUILDING").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("стр. ");
            res.append(params.get("FACTBUILDING").toString());
        }

        if ((params.get("FACTFLAT") != null) && (params.get("FACTFLAT").toString().compareTo("") != 0)) {
            res.append(", ");
            res.append("кв. ");
            res.append(params.get("FACTFLAT").toString());
        }
        if ((res.length() > 0) && (res.charAt(0) == ',')) {
            res.deleteCharAt(0);
        }

        return res.toString();
    }

    private void createParticipantAddress(Map<String, Object> params, Long participantId, String login, String password) throws Exception {
        // разыменовка места
        Map<String, Object> kladrParams = new HashMap<String, Object>();
        Map<String, Object> qres = null;
        if ((params.get("ADDRESSCODE") != null) && (!"".equals(params.get("ADDRESSCODE").toString()))) {
            kladrParams.put("CODE", params.get("ADDRESSCODE"));
            qres = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrBrowseListByParam", kladrParams, login, password);
        }

        // разыменовка улицы
        Map<String, Object> streetMap = null;
        Map<String, Object> streetParams = new HashMap<String, Object>();
        if ((params.get("STREETCODE") != null) && (!"".equals(params.get("STREETCODE").toString()))) {
            streetParams.put("KLADRCODE", params.get("STREETCODE"));
            Map<String, Object> qStreet = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrStreetBrowseListByParam", streetParams, login, password);
            if ((qStreet != null) && (qStreet.get(RESULT) != null) && (((List) qStreet.get(RESULT)).size() > 0)) {
                streetMap = (Map<String, Object>) ((List) qStreet.get(RESULT)).get(0);
            }
        }
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            Map<String, Object> map = (Map<String, Object>) ((List) qres.get(RESULT)).get(0);
            Map<String, Object> createParams = new HashMap<String, Object>();
            createParams.put("PARTICIPANTID", participantId);
            if (Long.valueOf(params.get("INSUREDTYPE").toString()).compareTo(Long.valueOf(0)) == 0) {
                createParams.put("ADDRESSTYPESYSNAME", "RegisterAddress");
            } else {
                createParams.put("ADDRESSTYPESYSNAME", "JuridicalAddress");
            }
//            createParams.put("COUNTRY", "РОССИЯ");
            if (params.get("COUNTRY") != null) {
                createParams.put("COUNTRY", params.get("COUNTRY"));
            } else {
                createParams.put("COUNTRY", "РОССИЯ");
            }
            createParams.put("REGION", map.get("REGIONNAME"));
            createParams.put("REGIONKLADR", map.get("REGIONCODE"));
            createParams.put("REGIONTYPE", map.get("REGIONTYPECODE"));
            createParams.put("DISTRICT", map.get("ZONENAME"));
            createParams.put("DISTRICTKLADR", map.get("ZONECODE"));
            createParams.put("DISTRICTTYPE", map.get("ZONETYPECODE"));
            createParams.put("CITY", map.get("CITYNAME"));
            createParams.put("CITYKLADR", map.get("CITYCODE"));
            createParams.put("CITYTYPE", map.get("CITYTYPECODE"));
            createParams.put("VILLAGE", map.get("PLACENAME"));
            createParams.put("VILLAGEKLADR", map.get("PLACECODE"));
            createParams.put("VILLAGETYPE", map.get("PLACETYPECODE"));
            createParams.put("POSTALCODE", params.get("POSTALCODE"));
            createParams.put("HOUSE", params.get("HOUSE"));
            createParams.put("HOUSING", params.get("BUILDING"));
            createParams.put("BUILDING", params.get("CORPUS"));
            createParams.put("FLAT", params.get("FLAT"));
            createParams.put("PRIORITY", 0);
            if (streetMap != null) {
                createParams.put("STREETKLADR", streetMap.get("CODE"));
                createParams.put("STREET", streetMap.get("NAME"));
                createParams.put("STREETTYPE", streetMap.get("TYPECODE"));
                params.put("STREET", streetMap.get("NAME"));
                params.put("STREETTYPE", streetMap.get("TYPECODE"));

                params.put("STREETTYPENAME", streetMap.get("TYPE"));
                params.put("STREETKLADR", streetMap.get("CODE"));
            } else {
                createParams.put("STREET", params.get("STREET"));
                createParams.put("STREETTYPE", params.get("STREETTYPE"));
            }

            createParams.put("ADDRESSTEXT1", generateFullAddress(params, map));
            if (params.get("ADDRESSTEXT2") == null) {
                params.put("ADDRESSTEXT2", "");
            }
            if (params.get("COUNTRY") != null) {
                if (params.get("COUNTRY").toString().equalsIgnoreCase("РОССИЯ")) {
                    createParams.put("ADDRESSTEXT2", generateShortAddress(params, map));
                } else {
                    createParams.put("ADDRESSTEXT2", params.get("ADDRESSTEXT2"));
                }
            } else {
                createParams.put("ADDRESSTEXT2", generateShortAddress(params, map));
            }
            if (createParams.get("ADDRESSTEXT2") != null) {
                createParams.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(createParams.get("ADDRESSTEXT2").toString()));
            } else {
                createParams.put("ADDRESSTEXT3", "");
            }
            createParams.put("USEKLADR", params.get("USEKLADR"));
            this.callService(CRMWS_SERVICE_NAME, "addressCreate", createParams, login, password);
        } else {
            Map<String, Object> createParams = new HashMap<String, Object>();
            createParams.put("PARTICIPANTID", participantId);
            createParams.put("PRIORITY", 0);
            if (Long.valueOf(params.get("INSUREDTYPE").toString()).compareTo(Long.valueOf(0)) == 0) {
                createParams.put("ADDRESSTYPESYSNAME", "RegisterAddress");
            } else {
                createParams.put("ADDRESSTYPESYSNAME", "JuridicalAddress");
            }
//            createParams.put("COUNTRY", "РОССИЯ");
            if (params.get("COUNTRY") != null) {
                createParams.put("COUNTRY", params.get("COUNTRY"));
                createParams.put("ADDRESSTEXT2", params.get("ADDRESSTEXT2"));
                if (createParams.get("ADDRESSTEXT2") != null) {
                    createParams.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(createParams.get("ADDRESSTEXT2").toString()));
                } else {
                    createParams.put("ADDRESSTEXT3", "");
                }
            }
            this.callService(CRMWS_SERVICE_NAME, "addressCreate", createParams, login, password);
        }
    }

    private void modifyParticipantAddressData(Map<String, Object> params, Long participantId, String login, String password) throws Exception {
        // разыменовка места
        Map<String, Object> kladrParams = new HashMap<String, Object>();
        Map<String, Object> modifyParams = new HashMap<String, Object>();
        kladrParams.put("CODE", params.get("ADDRESSCODE"));
        Map<String, Object> qres = null;
        if (params.get("ADDRESSCODE") != null) {
            qres = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrBrowseListByParam", kladrParams, login, password);
        }
        // разыменовка улицы
        Map<String, Object> streetMap = null;
        Map<String, Object> streetParams = new HashMap<String, Object>();
        if ((params.get("STREETCODE") != null) && (!"".equals(params.get("STREETCODE").toString()))) {
            streetParams.put("KLADRCODE", params.get("STREETCODE"));
            Map<String, Object> qStreet = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrStreetBrowseListByParam", streetParams, login, password);
            if ((qStreet != null) && (qStreet.get(RESULT) != null) && (((List) qStreet.get(RESULT)).size() > 0)) {
                streetMap = (Map<String, Object>) ((List) qStreet.get(RESULT)).get(0);
            }
        }

        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            Map<String, Object> map = (Map<String, Object>) ((List) qres.get(RESULT)).get(0);
            modifyParams.put("PARTICIPANTID", participantId);
            if (Long.valueOf(params.get("INSUREDTYPE").toString()).compareTo(Long.valueOf(0)) == 0) {
                modifyParams.put("ADDRESSTYPESYSNAME", "RegisterAddress");
            } else {
                modifyParams.put("ADDRESSTYPESYSNAME", "JuridicalAddress");
            }
            if (params.get("COUNTRY") != null) {
                modifyParams.put("COUNTRY", params.get("COUNTRY"));
            } else {
                modifyParams.put("COUNTRY", "РОССИЯ");
            }
            modifyParams.put("REGION", map.get("REGIONNAME"));
            modifyParams.put("REGIONKLADR", map.get("REGIONCODE"));
            modifyParams.put("REGIONTYPE", map.get("REGIONTYPECODE"));
            if (null != map.get("ZONENAME")) {
                modifyParams.put("DISTRICT", map.get("ZONENAME"));
            } else {
                modifyParams.put("DISTRICT", "");
            }
            modifyParams.put("DISTRICTKLADR", map.get("ZONECODE"));
            modifyParams.put("DISTRICTTYPE", map.get("ZONETYPECODE"));
            if (null != map.get("CITYNAME")) {
                modifyParams.put("CITY", map.get("CITYNAME"));
            } else {
                modifyParams.put("CITY", "");
            }
            if (null != map.get("CITYCODE")) {
                modifyParams.put("CITYKLADR", map.get("CITYCODE"));
            } else {
                modifyParams.put("CITYKLADR", "");
            }
            modifyParams.put("CITYTYPE", map.get("CITYTYPECODE"));
            if (null != map.get("PLACENAME")) {
                modifyParams.put("VILLAGE", map.get("PLACENAME"));
            } else {
                modifyParams.put("VILLAGE", "");
            }
            if (null != map.get("PLACECODE")) {
                modifyParams.put("VILLAGEKLADR", map.get("PLACECODE"));
            } else {
                modifyParams.put("VILLAGEKLADR", "");
            }
            modifyParams.put("VILLAGETYPE", map.get("PLACETYPECODE"));
            modifyParams.put("POSTALCODE", params.get("POSTALCODE"));
            modifyParams.put("HOUSE", params.get("HOUSE"));
            modifyParams.put("HOUSING", params.get("BUILDING"));
            modifyParams.put("BUILDING", params.get("CORPUS"));
            modifyParams.put("FLAT", params.get("FLAT"));
            modifyParams.put("PRIORITY", 0);
            if (streetMap != null) {
                modifyParams.put("STREETKLADR", streetMap.get("CODE"));
                modifyParams.put("STREET", streetMap.get("NAME"));
                modifyParams.put("STREETTYPE", streetMap.get("TYPECODE"));
                params.put("STREET", streetMap.get("NAME"));
                params.put("STREETTYPE", streetMap.get("TYPECODE"));

                params.put("STREETTYPENAME", streetMap.get("TYPE"));
                params.put("STREETKLADR", streetMap.get("CODE"));
            } else {
                modifyParams.put("STREET", params.get("STREET"));
                modifyParams.put("STREETTYPE", params.get("STREETTYPE"));
            }
            modifyParams.put("ADDRESSTEXT1", generateFullAddress(params, map));
            if (params.get("ADDRESSTEXT2") == null) {
                params.put("ADDRESSTEXT2", "");
            }
            if (params.get("COUNTRY") != null) {
                if (params.get("COUNTRY").toString().equalsIgnoreCase("РОССИЯ")) {
                    modifyParams.put("ADDRESSTEXT2", generateShortAddress(params, map));
                } else {
                    modifyParams.put("ADDRESSTEXT2", params.get("ADDRESSTEXT2"));
                }
            } else {
                modifyParams.put("ADDRESSTEXT2", generateShortAddress(params, map));
            }
            if (modifyParams.get("ADDRESSTEXT2") != null) {
                modifyParams.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(modifyParams.get("ADDRESSTEXT2").toString()));
            } else {
                modifyParams.put("ADDRESSTEXT3", "");
            }
            modifyParams.put("USEKLADR", params.get("USEKLADR"));
            if ((params.get("ADDRESSID") != null) && (Long.valueOf(params.get("ADDRESSID").toString()).longValue() > 0)) {
                modifyParams.put("ADDRESSID", params.get("ADDRESSID"));
                this.callService(CRMWS_SERVICE_NAME, "addressModify", modifyParams, login, password);
            } else {
                this.callService(CRMWS_SERVICE_NAME, "addressCreate", modifyParams, login, password);
            }
        } else {
            modifyParams.put("PARTICIPANTID", participantId);
            modifyParams.put("PRIORITY", 0);
            if (Long.valueOf(params.get("INSUREDTYPE").toString()).compareTo(Long.valueOf(0)) == 0) {
                modifyParams.put("ADDRESSTYPESYSNAME", "RegisterAddress");
            } else {
                modifyParams.put("ADDRESSTYPESYSNAME", "JuridicalAddress");
            }
            if (params.get("COUNTRY") != null) {
                modifyParams.put("COUNTRY", params.get("COUNTRY"));
            } else {
                modifyParams.put("COUNTRY", "РОССИЯ");
            }
            modifyParams.put("ADDRESSTEXT2", params.get("ADDRESSTEXT2"));
            if (modifyParams.get("ADDRESSTEXT2") != null) {
                modifyParams.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(modifyParams.get("ADDRESSTEXT2").toString()));
            } else {
                modifyParams.put("ADDRESSTEXT3", "");
            }
            if ((params.get("ADDRESSID") != null) && (Long.valueOf(params.get("ADDRESSID").toString()).longValue() > 0)) {
                modifyParams.put("ADDRESSID", params.get("ADDRESSID"));
                this.callService(CRMWS_SERVICE_NAME, "addressModify", modifyParams, login, password);
            } else {
                this.callService(CRMWS_SERVICE_NAME, "addressCreate", modifyParams, login, password);
            }

        }
    }

    private void createParticipantAddressPost(Map<String, Object> params, Long participantId, String login, String password) throws Exception {
        // разыменовка места
        Map<String, Object> kladrParams = new HashMap<String, Object>();
        Map<String, Object> qres = null;
        if ((params.get("ADDRESSPOSTCODE") != null) && (!"".equals(params.get("ADDRESSPOSTCODE").toString()))) {
            kladrParams.put("CODE", params.get("ADDRESSPOSTCODE"));
            qres = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrBrowseListByParam", kladrParams, login, password);
        }
        // разыменовка улицы
        Map<String, Object> streetMap = null;
        Map<String, Object> streetParams = new HashMap<String, Object>();
        if ((params.get("STREETPOSTCODE") != null) && (!"".equals(params.get("STREETPOSTCODE").toString()))) {
            streetParams.put("KLADRCODE", params.get("STREETPOSTCODE"));
            Map<String, Object> qStreet = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrStreetBrowseListByParam", streetParams, login, password);
            if ((qStreet != null) && (qStreet.get(RESULT) != null) && (((List) qStreet.get(RESULT)).size() > 0)) {
                streetMap = (Map<String, Object>) ((List) qStreet.get(RESULT)).get(0);
            }
        }

        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            Map<String, Object> map = (Map<String, Object>) ((List) qres.get(RESULT)).get(0);
            Map<String, Object> createParams = new HashMap<String, Object>();
            createParams.put("PARTICIPANTID", participantId);
            createParams.put("ADDRESSTYPESYSNAME", "PostAddress");
            //createParams.put("COUNTRY", "РОССИЯ");
            if (params.get("COUNTRY") != null) {
                createParams.put("COUNTRY", params.get("POSTCOUNTRY"));
            } else {
                createParams.put("COUNTRY", "РОССИЯ");
            }

            createParams.put("REGION", map.get("REGIONNAME"));
            createParams.put("REGIONKLADR", map.get("REGIONCODE"));
            createParams.put("REGIONTYPE", map.get("REGIONTYPECODE"));
            createParams.put("DISTRICT", map.get("ZONENAME"));
            createParams.put("DISTRICTKLADR", map.get("ZONECODE"));
            createParams.put("DISTRICTTYPE", map.get("ZONETYPECODE"));
            createParams.put("CITY", map.get("CITYNAME"));
            createParams.put("CITYKLADR", map.get("CITYCODE"));
            createParams.put("CITYTYPE", map.get("CITYTYPECODE"));
            createParams.put("VILLAGE", map.get("PLACENAME"));
            createParams.put("VILLAGEKLADR", map.get("PLACECODE"));
            createParams.put("VILLAGETYPE", map.get("PLACETYPECODE"));
            createParams.put("POSTALCODE", params.get("POSTPOSTALCODE"));
            createParams.put("HOUSE", params.get("POSTHOUSE"));
            createParams.put("HOUSING", params.get("POSTBUILDING"));
            createParams.put("BUILDING", params.get("POSTCORPUS"));
            createParams.put("FLAT", params.get("POSTFLAT"));
            createParams.put("PRIORITY", 0);
            if (streetMap != null) {
                createParams.put("STREETKLADR", streetMap.get("CODE"));
                createParams.put("STREET", streetMap.get("NAME"));
                createParams.put("STREETTYPE", streetMap.get("TYPECODE"));
                params.put("POSTSTREET", streetMap.get("NAME"));
                params.put("POSTSTREETTYPE", streetMap.get("TYPECODE"));

                params.put("STREETTYPENAME", streetMap.get("TYPE"));
                params.put("POSTSTREETTYPENAME", streetMap.get("TYPE"));
                params.put("STREETKLADR", streetMap.get("CODE"));
                params.put("POSTSTREETKLADR", streetMap.get("CODE"));
            } else {
                createParams.put("STREET", params.get("POSTSTREET"));
                createParams.put("STREETTYPE", params.get("POSTSTREETTYPE"));
            }
            createParams.put("ADDRESSTEXT1", generateFullPostAddress(params, map));
            if (params.get("POSTADDRESSTEXT2") == null) {
                params.put("POSTADDRESSTEXT2", "");
            }
            if (params.get("POSTCOUNTRY") != null) {
                if (params.get("POSTCOUNTRY").toString().equalsIgnoreCase("РОССИЯ")) {
                    createParams.put("ADDRESSTEXT2", generateShortPostAddress(params, map));
                } else {
                    createParams.put("ADDRESSTEXT2", params.get("POSTADDRESSTEXT2"));
                }
            } else {
                createParams.put("ADDRESSTEXT2", generateShortPostAddress(params, map));
            }
            if (createParams.get("ADDRESSTEXT2") != null) {
                createParams.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(createParams.get("ADDRESSTEXT2").toString()));
            } else {
                createParams.put("ADDRESSTEXT3", "");
            }
            // createParams.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(createParams.get("ADDRESSTEXT2").toString()));
            createParams.put("USEKLADR", params.get("USEKLADR"));
            this.callService(CRMWS_SERVICE_NAME, "addressCreate", createParams, login, password);
        } else {
            Map<String, Object> createParams = new HashMap<String, Object>();
            createParams.put("PARTICIPANTID", participantId);
            createParams.put("PRIORITY", 0);
            /*
             if (Long.valueOf(params.get("INSUREDTYPE").toString()).compareTo(Long.valueOf(0)) == 0) {
             createParams.put("ADDRESSTYPESYSNAME", "RegisterAddress");
             } else {
             createParams.put("ADDRESSTYPESYSNAME", "JuridicalAddress");
             }
             */
            createParams.put("ADDRESSTYPESYSNAME", "PostAddress");

            if (params.get("POSTCOUNTRY") != null) {
                createParams.put("COUNTRY", params.get("POSTCOUNTRY"));
                createParams.put("ADDRESSTEXT2", params.get("POSTADDRESSTEXT2"));
                if (createParams.get("ADDRESSTEXT2") != null) {
                    createParams.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(createParams.get("ADDRESSTEXT2").toString()));
                } else {
                    createParams.put("ADDRESSTEXT3", "");
                }
            }
            this.callService(CRMWS_SERVICE_NAME, "addressCreate", createParams, login, password);
        }
    }

    private void modifyParticipantAddressPostData(Map<String, Object> params, Long participantId, String login, String password) throws Exception {
        // разыменовка места
        Map<String, Object> kladrParams = new HashMap<String, Object>();
        Map<String, Object> modifyParams = new HashMap<String, Object>();
        kladrParams.put("CODE", params.get("ADDRESSPOSTCODE"));
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrBrowseListByParam", kladrParams, login, password);
        // разыменовка улицы
        Map<String, Object> streetMap = null;
        Map<String, Object> streetParams = new HashMap<String, Object>();
        if ((params.get("STREETPOSTCODE") != null) && (!"".equals(params.get("STREETPOSTCODE").toString()))) {
            streetParams.put("KLADRCODE", params.get("STREETPOSTCODE"));
            Map<String, Object> qStreet = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrStreetBrowseListByParam", streetParams, login, password);
            if ((qStreet != null) && (qStreet.get(RESULT) != null) && (((List) qStreet.get(RESULT)).size() > 0)) {
                streetMap = (Map<String, Object>) ((List) qStreet.get(RESULT)).get(0);
            }
        }

        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            Map<String, Object> map = (Map<String, Object>) ((List) qres.get(RESULT)).get(0);
            modifyParams.put("PARTICIPANTID", participantId);
            modifyParams.put("ADDRESSTYPESYSNAME", "PostAddress");
            if (params.get("POSTCOUNTRY") != null) {
                modifyParams.put("COUNTRY", params.get("POSTCOUNTRY"));
            } else {
                modifyParams.put("COUNTRY", "РОССИЯ");
            }
            modifyParams.put("REGION", map.get("REGIONNAME"));
            modifyParams.put("REGIONKLADR", map.get("REGIONCODE"));
            modifyParams.put("REGIONTYPE", map.get("REGIONTYPECODE"));
            modifyParams.put("DISTRICT", map.get("ZONENAME"));
            modifyParams.put("DISTRICTKLADR", map.get("ZONECODE"));
            modifyParams.put("DISTRICTTYPE", map.get("ZONETYPECODE"));
            modifyParams.put("CITY", map.get("CITYNAME"));
            modifyParams.put("CITYKLADR", map.get("CITYCODE"));
            modifyParams.put("CITYTYPE", map.get("CITYTYPECODE"));
            modifyParams.put("VILLAGE", map.get("PLACENAME"));
            modifyParams.put("VILLAGEKLADR", map.get("PLACECODE"));
            modifyParams.put("VILLAGETYPE", map.get("PLACETYPECODE"));
            modifyParams.put("POSTALCODE", params.get("POSTPOSTALCODE"));
            modifyParams.put("HOUSE", params.get("POSTHOUSE"));
            modifyParams.put("HOUSING", params.get("POSTBUILDING"));
            modifyParams.put("BUILDING", params.get("POSTCORPUS"));
            modifyParams.put("FLAT", params.get("POSTFLAT"));
            modifyParams.put("PRIORITY", 0);
            if (streetMap != null) {
                modifyParams.put("STREETKLADR", streetMap.get("CODE"));
                modifyParams.put("STREET", streetMap.get("NAME"));
                modifyParams.put("STREETTYPE", streetMap.get("TYPECODE"));
                params.put("STREET", streetMap.get("NAME"));
                params.put("STREETTYPE", streetMap.get("TYPECODE"));

                params.put("STREETTYPENAME", streetMap.get("TYPE"));
                params.put("STREETKLADR", streetMap.get("CODE"));
            } else {
                modifyParams.put("STREET", params.get("POSTSTREET"));
                modifyParams.put("STREETTYPE", params.get("POSTSTREETTYPE"));
                modifyParams.put("STREETKLADR", null);
            }
            modifyParams.put("ADDRESSTEXT1", generateFullPostAddress(params, map));
            if (params.get("POSTADDRESSTEXT2") == null) {
                params.put("POSTADDRESSTEXT2", "");
            }
            if (params.get("POSTCOUNTRY") != null) {
                if (params.get("POSTCOUNTRY").toString().equalsIgnoreCase("РОССИЯ")) {
                    modifyParams.put("ADDRESSTEXT2", generateShortPostAddress(params, map));
                } else {
                    modifyParams.put("ADDRESSTEXT2", params.get("POSTADDRESSTEXT2"));
                }
            } else {
                modifyParams.put("ADDRESSTEXT2", generateShortPostAddress(params, map));
            }
            if (modifyParams.get("ADDRESSTEXT2") != null) {
                modifyParams.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(modifyParams.get("ADDRESSTEXT2").toString()));
            } else {
                modifyParams.put("ADDRESSTEXT3", "");
            }
            modifyParams.put("USEKLADR", params.get("USEKLADR"));
            if ((params.get("ADDRESSPOSTID") != null) && (Long.valueOf(params.get("ADDRESSPOSTID").toString()).longValue() > 0)) {
                modifyParams.put("ADDRESSID", params.get("ADDRESSPOSTID"));
                this.callService(CRMWS_SERVICE_NAME, "addressModify", modifyParams, login, password);
            } else {
                this.callService(CRMWS_SERVICE_NAME, "addressCreate", modifyParams, login, password);
            }
        } else {
            // адрес не по КЛАДР, страна не РОССИЯ
            modifyParams.put("PARTICIPANTID", participantId);
            modifyParams.put("PRIORITY", 0);
            /*
             if (Long.valueOf(params.get("INSUREDTYPE").toString()).compareTo(Long.valueOf(0)) == 0) {
             modifyParams.put("ADDRESSTYPESYSNAME", "RegisterAddress");
             } else {
             modifyParams.put("ADDRESSTYPESYSNAME", "JuridicalAddress");
             }
             */
            modifyParams.put("ADDRESSTYPESYSNAME", "PostAddress");

            if (params.get("COUNTRY") != null) {
                modifyParams.put("COUNTRY", params.get("POSTCOUNTRY"));
            } else {
                modifyParams.put("COUNTRY", "РОССИЯ");
            }
            modifyParams.put("ADDRESSTEXT2", params.get("POSTADDRESSTEXT2"));
            if (modifyParams.get("ADDRESSTEXT2") != null) {
                modifyParams.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(modifyParams.get("ADDRESSTEXT2").toString()));
            } else {
                modifyParams.put("ADDRESSTEXT3", "");
            }
            if ((params.get("ADDRESSPOSTID") != null) && (Long.valueOf(params.get("ADDRESSPOSTID").toString()).longValue() > 0)) {
                modifyParams.put("ADDRESSID", params.get("ADDRESSPOSTID"));
                this.callService(CRMWS_SERVICE_NAME, "addressModify", modifyParams, login, password);
            } else {
                this.callService(CRMWS_SERVICE_NAME, "addressCreate", modifyParams, login, password);
            }

        }
    }

    private void createParticipantAddressFact(Map<String, Object> params, Long participantId, String login, String password) throws Exception {
        if (params.get("ADDRESSFACTCODE") != null) {
            // разыменовка места
            Map<String, Object> qres = null;
            Map<String, Object> kladrParams = new HashMap<String, Object>();
            if ((params.get("ADDRESSFACTCODE") != null) && (!"".equals(params.get("ADDRESSFACTCODE").toString()))) {
                kladrParams.put("CODE", params.get("ADDRESSFACTCODE"));
                qres = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrBrowseListByParam", kladrParams, login, password);
            }
            // разыменовка улицы
            Map<String, Object> streetMap = null;
            Map<String, Object> streetParams = new HashMap<String, Object>();
            if ((params.get("STREETFACTCODE") != null) && (!"".equals(params.get("STREETFACTCODE").toString()))) {
                streetParams.put("KLADRCODE", params.get("STREETFACTCODE"));
                Map<String, Object> qStreet = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrStreetBrowseListByParam", streetParams, login, password);
                if ((qStreet != null) && (qStreet.get(RESULT) != null) && (((List) qStreet.get(RESULT)).size() > 0)) {
                    streetMap = (Map<String, Object>) ((List) qStreet.get(RESULT)).get(0);
                }
            }
            if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
                Map<String, Object> map = (Map<String, Object>) ((List) qres.get(RESULT)).get(0);
                Map<String, Object> createParams = new HashMap<String, Object>();
                createParams.put("PARTICIPANTID", participantId);
                createParams.put("ADDRESSTYPESYSNAME", "FactAddress");
                //createParams.put("COUNTRY", "РОССИЯ");
                if (params.get("COUNTRY") != null) {
                    createParams.put("COUNTRY", params.get("FACTCOUNTRY"));
                } else {
                    createParams.put("COUNTRY", "РОССИЯ");
                }

                createParams.put("REGION", map.get("REGIONNAME"));
                createParams.put("REGIONKLADR", map.get("REGIONCODE"));
                createParams.put("REGIONTYPE", map.get("REGIONTYPECODE"));
                createParams.put("DISTRICT", map.get("ZONENAME"));
                createParams.put("DISTRICTKLADR", map.get("ZONECODE"));
                createParams.put("DISTRICTTYPE", map.get("ZONETYPECODE"));
                createParams.put("CITY", map.get("CITYNAME"));
                createParams.put("CITYKLADR", map.get("CITYCODE"));
                createParams.put("CITYTYPE", map.get("CITYTYPECODE"));
                createParams.put("VILLAGE", map.get("PLACENAME"));
                createParams.put("VILLAGEKLADR", map.get("PLACECODE"));
                createParams.put("VILLAGETYPE", map.get("PLACETYPECODE"));
                createParams.put("POSTALCODE", params.get("FACTPOSTALCODE"));
                createParams.put("HOUSE", params.get("FACTHOUSE"));
                createParams.put("HOUSING", params.get("FACTBUILDING"));
                createParams.put("BUILDING", params.get("FACTCORPUS"));
                createParams.put("FLAT", params.get("FACTFLAT"));
                createParams.put("PRIORITY", 0);
                if (streetMap != null) {
                    createParams.put("STREETKLADR", streetMap.get("CODE"));
                    createParams.put("STREET", streetMap.get("NAME"));
                    createParams.put("STREETTYPE", streetMap.get("TYPECODE"));
                    params.put("FACTSTREET", streetMap.get("NAME"));
                    params.put("FACTSTREETTYPE", streetMap.get("TYPECODE"));

//                    params.put("FACTSTREETTYPENAME", streetMap.get("TYPE"));
                    params.put("STREETTYPENAME", streetMap.get("TYPE"));
//                    params.put("FACTSTREETKLADR", streetMap.get("CODE"));
                    params.put("STREETKLADR", streetMap.get("CODE"));
                } else {
                    createParams.put("STREET", params.get("FACTSTREET"));
                    createParams.put("STREETTYPE", params.get("FACTSTREETTYPE"));
                }
                // для генерации строки
                params.putAll(createParams);
                params.put("BUILDING", params.get("FACTBUILDING"));
                params.put("CORPUS", params.get("FACTCORPUS"));

                createParams.put("ADDRESSTEXT1", generateFullAddress(params, map));
                if (params.get("FACTADDRESSTEXT2") == null) {
                    params.put("FACTADDRESSTEXT2", "");
                }
                if (params.get("FACTCOUNTRY") != null) {
                    if (params.get("FACTCOUNTRY").toString().equalsIgnoreCase("РОССИЯ")) {
                        createParams.put("ADDRESSTEXT2", generateShortAddress(params, map));
                    } else {
                        createParams.put("ADDRESSTEXT2", params.get("FACTADDRESSTEXT2"));
                    }
                } else {
                    createParams.put("ADDRESSTEXT2", generateShortAddress(params, map));
                }
                if (createParams.get("ADDRESSTEXT2") != null) {
                    createParams.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(createParams.get("ADDRESSTEXT2").toString()));
                } else {
                    createParams.put("ADDRESSTEXT3", "");
                }
                createParams.put("USEKLADR", params.get("USEKLADR"));
                this.callService(CRMWS_SERVICE_NAME, "addressCreate", createParams, login, password);
            }
        }
    }

    private void modifyParticipantAddressFactData(Map<String, Object> params, Long participantId, String login, String password) throws Exception {
        if (params.get("ADDRESSFACTCODE") != null) {
            // разыменовка места
            Map<String, Object> kladrParams = new HashMap<String, Object>();
            Map<String, Object> modifyParams = new HashMap<String, Object>();
            kladrParams.put("CODE", params.get("ADDRESSFACTCODE"));
            Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrBrowseListByParam", kladrParams, login, password);

            // разыменовка улицы
            Map<String, Object> streetMap = null;
            Map<String, Object> streetParams = new HashMap<String, Object>();
            if ((params.get("STREETFACTCODE") != null) && (!"".equals(params.get("STREETFACTCODE").toString()))) {
                streetParams.put("KLADRCODE", params.get("STREETFACTCODE"));
                Map<String, Object> qStreet = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrStreetBrowseListByParam", streetParams, login, password);
                if ((qStreet != null) && (qStreet.get(RESULT) != null) && (((List) qStreet.get(RESULT)).size() > 0)) {
                    streetMap = (Map<String, Object>) ((List) qStreet.get(RESULT)).get(0);
                }
            }

            if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
                Map<String, Object> map = (Map<String, Object>) ((List) qres.get(RESULT)).get(0);
                modifyParams.put("PARTICIPANTID", participantId);
                modifyParams.put("ADDRESSTYPESYSNAME", "FactAddress");
                if (params.get("FACTCOUNTRY") != null) {
                    modifyParams.put("COUNTRY", params.get("FACTCOUNTRY"));
                } else {
                    modifyParams.put("COUNTRY", "РОССИЯ");
                }
                modifyParams.put("REGION", map.get("REGIONNAME"));
                modifyParams.put("REGIONKLADR", map.get("REGIONCODE"));
                modifyParams.put("REGIONTYPE", map.get("REGIONTYPECODE"));
                if (null != map.get("ZONENAME")) {
                    modifyParams.put("DISTRICT", map.get("ZONENAME"));
                } else {
                    modifyParams.put("DISTRICT", "");
                }
                modifyParams.put("DISTRICTKLADR", map.get("ZONECODE"));
                modifyParams.put("DISTRICTTYPE", map.get("ZONETYPECODE"));
                if (null != map.get("CITYNAME")) {
                    modifyParams.put("CITY", map.get("CITYNAME"));
                } else {
                    modifyParams.put("CITY", "");
                }
                if (null != map.get("CITYCODE")) {
                    modifyParams.put("CITYKLADR", map.get("CITYCODE"));
                } else {
                    modifyParams.put("CITYKLADR", "");
                }
                modifyParams.put("CITYTYPE", map.get("CITYTYPECODE"));
                if (null != map.get("PLACENAME")) {
                    modifyParams.put("VILLAGE", map.get("PLACENAME"));
                } else {
                    modifyParams.put("VILLAGE", "");
                }
                if (null != map.get("PLACECODE")) {
                    modifyParams.put("VILLAGEKLADR", map.get("PLACECODE"));
                } else {
                    modifyParams.put("VILLAGEKLADR", "");
                }
                modifyParams.put("VILLAGETYPE", map.get("PLACETYPECODE"));
                modifyParams.put("POSTALCODE", params.get("FACTPOSTALCODE"));
                modifyParams.put("HOUSE", params.get("FACTHOUSE"));
                modifyParams.put("HOUSING", params.get("FACTBUILDING"));
                modifyParams.put("BUILDING", params.get("FACTCORPUS"));

                modifyParams.put("FLAT", params.get("FACTFLAT"));
                modifyParams.put("PRIORITY", 0);
                if (streetMap != null) {
                    modifyParams.put("STREETKLADR", streetMap.get("CODE"));
                    modifyParams.put("STREET", streetMap.get("NAME"));
                    modifyParams.put("STREETTYPE", streetMap.get("TYPECODE"));
                    params.put("STREET", streetMap.get("NAME"));
                    params.put("STREETTYPE", streetMap.get("TYPECODE"));

                    params.put("STREETTYPENAME", streetMap.get("TYPE"));
                    params.put("STREETKLADR", streetMap.get("CODE"));
                } else {
                    modifyParams.put("STREET", params.get("FACTSTREET"));
                    modifyParams.put("STREETTYPE", params.get("FACTSTREETTYPE"));
                }

                //для генерации строки адреса
                params.putAll(modifyParams);
                params.put("BUILDING", params.get("FACTBUILDING"));
                params.put("CORPUS", params.get("FACTCORPUS"));

                modifyParams.put("ADDRESSTEXT1", generateFullAddress(params, map));
                if (params.get("FACTADDRESSTEXT2") == null) {
                    params.put("FACTADDRESSTEXT2", "");
                }
                if (params.get("FACTCOUNTRY") != null) {
                    if (params.get("FACTCOUNTRY").toString().equalsIgnoreCase("РОССИЯ")) {
                        modifyParams.put("ADDRESSTEXT2", generateShortAddress(params, map));
                    } else {
                        modifyParams.put("ADDRESSTEXT2", params.get("FACTADDRESSTEXT2"));
                    }
                } else {
                    modifyParams.put("ADDRESSTEXT2", generateShortAddress(params, map));
                }
                if (modifyParams.get("ADDRESSTEXT2") != null) {
                    modifyParams.put("ADDRESSTEXT3", TranslitStringConverter.toTranslit(modifyParams.get("ADDRESSTEXT2").toString()));
                } else {
                    modifyParams.put("ADDRESSTEXT3", "");
                }
                modifyParams.put("USEKLADR", params.get("USEKLADR"));
                if ((params.get("ADDRESSFACTID") != null) && (Long.valueOf(params.get("ADDRESSFACTID").toString()).longValue() > 0)) {
                    modifyParams.put("ADDRESSID", params.get("ADDRESSFACTID"));
                    this.callService(CRMWS_SERVICE_NAME, "addressModify", modifyParams, login, password);
                } else {
                    this.callService(CRMWS_SERVICE_NAME, "addressCreate", modifyParams, login, password);
                }
            }
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsPersonCreateEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // создание физического лица
        result = createPerson(params, login, password);
        Long personId = Long.valueOf(result.get("PERSONID").toString());
        Long participantId = Long.valueOf(result.get("PARTICIPANTID").toString());
        params.put("PARTICIPANTID", participantId);
        String briefName = params.get("BRIEFNAME").toString();
        // альтернативное имя
        if (params.get("ALTNAME") != null) {
            createPersonAltName(params, participantId, login, password);
        }
        // документ, удостоверяющий личность
        if (needToProcess(params, "PROCESSMAINDOC")) {
            createPersonDocument(params, personId, login, password);
        }
        //
        if (needToProcess(params, "PROCESSDL")) {
            if ((params.get("DLSERIES") != null) && (params.get("DLNUMBER") != null)
                    && (!params.get("DLSERIES").toString().isEmpty())
                    && (!params.get("DLNUMBER").toString().isEmpty())) {
                createPersonDL(params, personId, login, password);
                createDriver(params, personId, participantId, login, password);
            }
        }
        // адрес
        // метод сохранения адресов лица, переданных с перв
        params.put("CREATENEWADDRESS", "TRUE");
        if (!processAddresses(params)) {
            if (needToProcess(params, "PROCESSADDRESS")) {
                createParticipantAddress(params, participantId, login, password);
            }
            if (needToProcess(params, "PROCESSADDRESSFACT")) {
                createParticipantAddressFact(params, participantId, login, password);
            }
        }
        // контакты
        if (needToProcess(params, "PROCESSCONTACTS")) {
            Map<String, Object> contactPersonParams = new HashMap<String, Object>();
            contactPersonParams.put("ReturnAsHashMap", "TRUE");
            contactPersonParams.put("PARTICIPANTID", participantId);
            contactPersonParams.put("NAME", briefName);
            contactPersonParams.put("ROLENAME", "Participant");
            Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "contactPersonCreate", contactPersonParams, login, password);
            if (qres != null) {
                Long contactPersonId = Long.valueOf(qres.get("CONTACTPERSONID").toString());
                if (params.get("CONTACTPHONEMOBILE") != null) {
                    createPersonContact(contactPersonId, "MobilePhone", (String) params.get("CONTACTPHONEMOBILE"), (String) params.get("CONTACTPHONEMOBILEEXT"), login, password);
                }
                // старая реализация, не руками не трогать последующие два if'а,
                // в таком варианте сохраняется когда на интерфейсе используется один телефон "домашний/рабочий"
                if (params.get("CONTACTPHONEHOME") != null) {
                    createPersonContact(contactPersonId, "WorkAddressPhone", (String) params.get("CONTACTPHONEHOME"), (String) params.get("CONTACTPHONEHOMEEXT"), login, password);
                }
                if (params.get("CONTACTPHONEWORK") != null) {
                    createPersonContact(contactPersonId, "FactAddressPhone", (String) params.get("CONTACTPHONEWORK"), (String) params.get("CONTACTPHONEWORKEXT"), login, password);
                }
                // новая реализация с правильным маппингом для раздельного хранения домашнего и рабочего телефонов
                if (params.get("CONTACTPHONEHOME2") != null) {
                    createPersonContact(contactPersonId, "FactAddressPhone", (String) params.get("CONTACTPHONEHOME2"), (String) params.get("CONTACTPHONEHOME2EXT"), login, password);
                }
                if (params.get("CONTACTPHONEWORK2") != null) {
                    createPersonContact(contactPersonId, "WorkAddressPhone", (String) params.get("CONTACTPHONEWORK2"), (String) params.get("CONTACTPHONEWORK2EXT"), login, password);
                }
                //
                if (params.get("CONTACTEMAIL") != null) {
                    createPersonContact(contactPersonId, "PersonalEmail", (String) params.get("CONTACTEMAIL"), null, login, password);
                }
            }
        }
        result.put("DRIVERNODEID", params.get("DRIVERNODEID_"));
        result.put("DRIVERID", params.get("DRIVERID_"));

        return result;
    }

    private Map<String, Object> createCompany(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.put("ReturnAsHashMap", "TRUE");
        createParams.put("PARTICIPANTTYPE", 2);
        createParams.put("ISCLIENT", 0);
        createParams.put("STATE", "Обслуживается");
        createParams.put("CREATIONDATE", (new GregorianCalendar()).getTime());
        createParams.put("OPF", params.get("ORGOPF"));
        createParams.put("SHORTNAME", params.get("ORGSHORTNAME"));
        // начало костыля для создания компаний дубликатов (для костыля "Версионные партиципанты")
        createParams.put("FULLNAME", params.get("ORGFULLNAME") + "_");
        createParams.put("INN", params.get("ORGINN"));
        createParams.put("BRIEFNAME", params.get("ORGSHORTNAME"));
        createParams.put("partCodeList", params.get("partCodeList"));
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "companyCreate", createParams, login, password);
        // продолжение костыля для создания компаний дубликатов
        createParams.remove("partCodeList");
        createParams.put("FULLNAME", params.get("ORGFULLNAME"));
        createParams.put("PARTICIPANTID", qres.get("PARTICIPANTID"));
        this.callService(CRMWS_SERVICE_NAME, "companyModify", createParams, login, password);
        createParams.put("partCodeList", params.get("partCodeList"));
        return qres;
    }

    private Map<String, Object> createBank(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.put("ReturnAsHashMap", "TRUE");
        createParams.put("PARTICIPANTTYPE", 3);
        createParams.put("ISCLIENT", 0);
        createParams.put("STATE", "Обслуживается");
        createParams.put("CREATIONDATE", (new GregorianCalendar()).getTime());
        createParams.put("OPF", params.get("ORGOPF"));
        createParams.put("SHORTNAME", params.get("ORGSHORTNAME"));
        // начало костыля для создания компаний дубликатов (для костыля "Версионные партиципанты")
        createParams.put("FULLNAME", params.get("ORGFULLNAME") + "_");
        createParams.put("INN", params.get("ORGINN"));
        createParams.put("BRIEFNAME", params.get("ORGSHORTNAME"));
        createParams.put("partCodeList", params.get("partCodeList"));
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "financialInstCreate", createParams, login, password);
        // продолжение костыля для создания компаний дубликатов
        createParams.remove("partCodeList");
        createParams.put("FULLNAME", params.get("ORGFULLNAME"));
        createParams.put("PARTICIPANTID", qres.get("PARTICIPANTID"));
        this.callService(CRMWS_SERVICE_NAME, "financialInstModify", createParams, login, password);
        createParams.put("partCodeList", params.get("partCodeList"));
        return qres;
    }

    private void getCompanyModifyData(Map<String, Object> result, Map<String, Object> params, String login, String password) throws Exception {
        result.put("FLAG", "UPD");
        result.put("PARTICIPANTID", params.get("PARTICIPANTID"));
        result.put("OPF", params.get("ORGOPF"));
        result.put("SHORTNAME", params.get("ORGSHORTNAME"));
        result.put("FULLNAME", params.get("ORGFULLNAME"));
        result.put("INN", params.get("ORGINN"));
        result.put("BRIEFNAME", params.get("ORGSHORTNAME"));
    }

    private Map<String, Object> createRegDocument(Map<String, Object> params, Long participantId, String login, String password) throws Exception {
        Map<String, Object> createParams = new HashMap<String, Object>();
        createParams.put("PARTICIPANTID", participantId);
        createParams.put("REGDOCTYPESYSNAME", "OGRN");
        createParams.put("DESCRIPTION", params.get("ORGOGRN"));
        createParams.put("REGISTRDATE", params.get("ORGREGISTERDATE"));
        createParams.put("DOCSERIES", params.get("ORGREGSERIES"));
        createParams.put("DOCNUMBER", params.get("ORGREGNUMBER"));
        createParams.put("ISSUEDATE", params.get("ORGISSUEDATE"));
        createParams.put("REGISTRPLACE", params.get("ORGREGPLACE"));
        createParams.put("ISSUEDBY", params.get("ORGREGISSUEDBY"));
        XMLUtil.convertFloatToDate(createParams);
        return this.callService(CRMWS_SERVICE_NAME, "partRegDocCreate", createParams, login, password);
    }

    private void getModifyRegDocumentData(Map<String, Object> result, Map<String, Object> params, Long participantId, String login, String password) throws Exception {
        if ((params.get("ORGREGDOCID") != null) && (Long.valueOf(params.get("ORGREGDOCID").toString()).intValue() > 0)) {
            if ((params.get("ORGREGNUMBER") != null) && (!params.get("ORGREGNUMBER").toString().isEmpty())) {
                result.put("FLAG", "UPD");
            } else {
                result.put("FLAG", "DEL");
            }
            result.put("PARTREGDOCID", params.get("ORGREGDOCID"));
        } else if ((params.get("ORGREGNUMBER") != null) && (!params.get("ORGREGNUMBER").toString().isEmpty())) {
            result.put("FLAG", "ADD");
        } else {
            result.put("FLAG", "NONE");
        }
        result.put("PARTICIPANTID", participantId);
        result.put("REGDOCTYPESYSNAME", "OGRN");
        result.put("DESCRIPTION", params.get("ORGOGRN"));
        result.put("REGISTRDATE", params.get("ORGREGISTERDATE"));
        result.put("DOCSERIES", params.get("ORGREGSERIES"));
        result.put("DOCNUMBER", params.get("ORGREGNUMBER"));
        result.put("ISSUEDATE", params.get("ORGISSUEDATE"));
        result.put("REGISTRPLACE", params.get("ORGREGPLACE"));
        result.put("ISSUEDBY", params.get("ORGREGISSUEDBY"));
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsCompanyCreateEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // создание юридического лица
        Long insuredType = Long.valueOf(params.get("INSUREDTYPE").toString());
        if (insuredType.compareTo(Long.valueOf(3)) == 0) {
            result = createBank(params, login, password);
            Long companyId = Long.valueOf(result.get("FININSTID").toString());
        } else {
            result = createCompany(params, login, password);
            Long companyId = Long.valueOf(result.get("COMPANYID").toString());
        }

        Long participantId = Long.valueOf(result.get("PARTICIPANTID").toString());
        params.put("PARTICIPANTID", participantId);
        // создание альтернативного имени
        if (params.get("ORGALTNAME") != null) {
            createCompanyAltName(params, participantId, login, password);
        }
        // регистрационный документ
        if (needToProcess(params, "PROCESSREGDOC")) {
            createRegDocument(params, participantId, login, password);
        }
        //
        params.put("CREATENEWADDRESS", "TRUE");
        if (!processAddresses(params)) {
            if (needToProcess(params, "PROCESSADDRESS")) {
                createParticipantAddress(params, participantId, login, password);
            }
            if (needToProcess(params, "PROCESSADDRESSPOST")) {
                createParticipantAddressPost(params, participantId, login, password);
            }
        }
        if (params.get("partCodeList") != null) {
            createPartCodeList(params, participantId, login, password);
            //modifyParams.put("partCodeList",params.get("partCodeList"));
        }
        // контакты
        if (needToProcess(params, "PROCESSCONTACTS")) {
            Map<String, Object> contactPersonParams = new HashMap<String, Object>();
            contactPersonParams.put("ReturnAsHashMap", "TRUE");
            contactPersonParams.put("PARTICIPANTID", participantId);
            contactPersonParams.put("NAME", params.get("ORGSHORTNAME"));
            contactPersonParams.put("ROLENAME", "Participant");
            Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "contactPersonCreate", contactPersonParams, login, password);
            if (qres != null) {
                Long contactPersonId = Long.valueOf(qres.get("CONTACTPERSONID").toString());
                if (params.containsKey("CONTACTPHONEMOBILE")) {
                    createPersonContact(contactPersonId, "MobilePhone", (String) params.get("CONTACTPHONEMOBILE"), (String) params.get("CONTACTPHONEMOBILEEXT"), login, password);
                }/* else {
                 createPersonContact(contactPersonId, "MobilePhone", " ", login, password);
                 }*/

                if (params.containsKey("CONTACTPHONEHOME")) {
                    createPersonContact(contactPersonId, "WorkAddressPhone", (String) params.get("CONTACTPHONEHOME"), (String) params.get("CONTACTPHONEHOMEEXT"), login, password);
                }/* else {
                 createPersonContact(contactPersonId, "WorkAddressPhone", " ", login, password);
                 }*/

                if (params.containsKey("CONTACTEMAIL")) {
                    createPersonContact(contactPersonId, "PersonalEmail", (String) params.get("CONTACTEMAIL"), null, login, password);
                }
                /*else {
                 createPersonContact(contactPersonId, "PersonalEmail", "", login, password);
                 }*/

            }
        }
        return result;
    }

    @WsMethod(requiredParams = {"INSUREDTYPE"})
    public Map<String, Object> dsParticipantCustomCreate(Map<String, Object> params) throws Exception {
        Long insuredType = Long.valueOf(params.get("INSUREDTYPE").toString());
        if (insuredType.compareTo(Long.valueOf(0)) == 0) {
            return this.dsPersonCreateEx(params);
        } else if (insuredType.compareTo(Long.valueOf(3)) == 0) {
            return this.dsCompanyCreateEx(params);
        } else {
            return this.dsCompanyCreateEx(params);
        }
    }

    private void getPersonModifyContactsData(Map<String, Object> modifyParams, Map<String, Object> params, Long participantId, String login, String password) throws Exception {
        Long contactPersonId = null;
        if ((params.get("CONTACTPERSONID") != null) && (Long.valueOf(params.get("CONTACTPERSONID").toString()).intValue() > 0)) {
            contactPersonId = Long.valueOf(params.get("CONTACTPERSONID").toString());
        } else {
            Map<String, Object> contactPersonParams = new HashMap<String, Object>();
            contactPersonParams.put("ReturnAsHashMap", "TRUE");
            contactPersonParams.put("PARTICIPANTID", participantId);
            if (params.get("ORGSHORTNAME") != null) {
                contactPersonParams.put("NAME", params.get("ORGSHORTNAME"));
            } else if (params.get("BRIEFNAME") != null) {
                contactPersonParams.put("NAME", params.get("BRIEFNAME"));
            } else {
                contactPersonParams.put("NAME", params.get("SURNAME"));
            }
            contactPersonParams.put("ROLENAME", "Participant");
            Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "contactPersonCreate", contactPersonParams, login, password);
            if (qres != null) {
                logger.debug("debug contactPersonCreate");
                logger.debug(qres);
                if (qres.get("CONTACTPERSONID") != null) {
                    contactPersonId = Long.valueOf(qres.get("CONTACTPERSONID").toString());
                }
            }
        }
        if (contactPersonId != null) {
            List<Map<String, Object>> contactList = new ArrayList();
            if (params.get("CONTACTPHONEMOBILE") != null) {
                Map<String, Object> mobilePhoneMap = new HashMap<String, Object>();
                if (getPersonModifyContactData(mobilePhoneMap, params.get("CONTACTPHONEMOBILEID"), contactPersonId, "MobilePhone", (String) params.get("CONTACTPHONEMOBILE"), (String) params.get("CONTACTPHONEMOBILEEXT"), login, password)) {
                    contactList.add(mobilePhoneMap);
                }
            }
            Map<String, Object> workPhoneMap = new HashMap<String, Object>();
            Map<String, Object> factPhoneMap = new HashMap<String, Object>();
            // старая реализация, не руками не трогать последующие два if'а,
            // в таком варианте сохраняется когда на интерфейсе используется один телефон "домашний/рабочий"
            if (params.get("CONTACTPHONEHOME") != null) {
                if (getPersonModifyContactData(workPhoneMap, params.get("CONTACTPHONEHOMEID"), contactPersonId, "WorkAddressPhone", (String) params.get("CONTACTPHONEHOME"), (String) params.get("CONTACTPHONEHOMEEXT"), login, password)) {
                    contactList.add(workPhoneMap);
                }
            }
            if (params.get("CONTACTPHONEWORK") != null) {
                if (getPersonModifyContactData(factPhoneMap, params.get("CONTACTPHONEWORKID"), contactPersonId, "FactAddressPhone", (String) params.get("CONTACTPHONEWORK"), (String) params.get("CONTACTPHONEWORKEXT"), login, password)) {
                    contactList.add(factPhoneMap);
                }
            }
            // новая реализация с правильным маппингом для раздельного хранения домашнего и рабочего телефонов
            if (params.get("CONTACTPHONEHOME2") != null) {
                if (getPersonModifyContactData(workPhoneMap, params.get("CONTACTPHONEHOMEID"), contactPersonId, "FactAddressPhone", (String) params.get("CONTACTPHONEHOME2"), (String) params.get("CONTACTPHONEHOME2EXT"), login, password)) {
                    contactList.add(workPhoneMap);
                }
            }
            if (params.get("CONTACTPHONEWORK2") != null) {
                if (getPersonModifyContactData(factPhoneMap, params.get("CONTACTPHONEWORKID"), contactPersonId, "WorkAddressPhone", (String) params.get("CONTACTPHONEWORK2"), (String) params.get("CONTACTPHONEWORK2EXT"), login, password)) {
                    contactList.add(factPhoneMap);
                }
            }

            Map<String, Object> eMailMap = new HashMap<String, Object>();
            if (params.get("CONTACTEMAIL") != null) {
                if (getPersonModifyContactData(eMailMap, params.get("CONTACTEMAILID"), contactPersonId, "PersonalEmail", params.get("CONTACTEMAIL").toString(), null, login, password)) {
                    contactList.add(eMailMap);
                }
            }
            modifyParams.put("contactList", contactList);
        }
    }

    /**
     * Обновление данных физ. лица
     *
     * @author ilich
     * @param params
     * <UL>
     * <LI>PARTICIPANTID - ИД участника</LI>
     * <LI>UPDATECONTRDRIVER - флаг обновления ИД водителя в таблице
     * INS_CONTRDRIVER, если изменились данные ВУ</LI>
     * <UL>
     */
    @WsMethod(requiredParams = {"PARTICIPANTID"})
    public Map<String, Object> dsPersonModifyEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Long participantId = Long.valueOf(params.get("PARTICIPANTID").toString());
        loadPersonId(params, login, password);
        Long personId = Long.valueOf(params.get("PERSONID").toString());
        Map<String, Object> modifyParams = new HashMap<String, Object>();
        getPersonModifyData(modifyParams, params, login, password);
        if (params.get("ALTNAME") != null) {
            modifyPersonAltName(params, login, password);
        }
        // основной документ и вод. удостоверение
        List<Map<String, Object>> personDocs = new ArrayList();
        if (needToProcess(params, "PROCESSMAINDOC")) {
            Map<String, Object> mainDoc = new HashMap<String, Object>();
            getPersonModifyDocumentData(mainDoc, params, login, password);
            personDocs.add(mainDoc);
        }
        if (needToProcess(params, "PROCESSDL")) {
            Map<String, Object> dlDoc = new HashMap<String, Object>();
            getPersonModifyDLData(dlDoc, params, login, password);
            personDocs.add(dlDoc);
        }
        modifyParams.put("documentList", personDocs);
        // адрес
        if (!processAddresses(params)) {

            if (needToProcess(params, "PROCESSADDRESS")) {
                modifyParticipantAddressData(params, participantId, login, password);
            }
            if (needToProcess(params, "PROCESSADDRESSFACT")) {
                modifyParticipantAddressFactData(params, participantId, login, password);
            }
        }
        // контакты
        if (needToProcess(params, "PROCESSCONTACTS")) {
            getPersonModifyContactsData(modifyParams, params, participantId, login, password);
        }
        //
        if (needToProcess(params, "PROCESSDL")) {
            if (params.get("DRIVERID") != null) {
                updateDriver(params, login, password);
                if (params.get("DRIVERIDNEW") != null) {
                    if ((params.get("UPDATECONTRDRIVER") != null)
                            && (Boolean.valueOf(params.get("UPDATECONTRDRIVER").toString()).booleanValue() == true)) {
                        updateContrDrivers(params, login, password);
                    }
                }
            } else {
                createDriver(params, personId, participantId, login, password);
            }
        }

        Map<String, Object> result = this.callService(CRMWS_SERVICE_NAME, "personModify", modifyParams, login, password);
        if (result != null) {
            result.put("DRIVERNODEID", params.get("DRIVERNODEID_"));
            result.put("DRIVERID", params.get("DRIVERID_"));
        }
        if ((modifyParams.get("documentList") != null)) {
            personDocs = (List<Map<String, Object>>) modifyParams.get("documentList");
            for (Iterator<Map<String, Object>> it = personDocs.iterator(); it.hasNext();) {
                Map<String, Object> map = it.next();
                if ((map.get("PERSONDOCID") != null) && (map.get("ISSUEDATE") == null)) {
                    Map<String, Object> clearParams = new HashMap<String, Object>();
                    clearParams.put("PERSONDOCID", map.get("PERSONDOCID"));
                    // Значение не важно.
                    clearParams.put("ISSUEDATE", 1L);
                    Map<String, Object> clRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPersonClearAddressNullFields", clearParams, login, password);

                }
            }

        }
        return result;
    }

    /**
     * Очистка полей в таблице CRM_PERSONDOC, на данный момент реализована
     * очистка ISSUEDATE если нужно что то еще нужно править запрос
     * dsPersonClearAddressNullFields
     *
     * @author kkulkov
     * @param params
     * <UL>
     * <LI>PERSONDOCID - ИД Документа</LI>
     * <LI>ISSUEDATE - флаг обновления что поле нужно занулить</LI>
     * <UL>
     */
    @WsMethod(requiredParams = {"PERSONDOCID"})
    public Map<String, Object> dsPersonClearAddressNullFields(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPersonClearAddressNullFields", params);
        result.put("PERSONDOCID", params.get("PERSONDOCID"));
        return result;
    }

    @WsMethod(requiredParams = {"PARTICIPANTID"})
    public Map<String, Object> dsCompanyModifyEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Long participantId = Long.valueOf(params.get("PARTICIPANTID").toString());
        Map<String, Object> modifyParams = new HashMap<String, Object>();
        getCompanyModifyData(modifyParams, params, login, password);
        if (params.get("ORGALTNAME") != null) {
            modifyCompanyAltName(params, login, password);
        }
        // рег. документ
        if (needToProcess(params, "PROCESSREGDOC")) {
            List<Map<String, Object>> regDocs = new ArrayList();
            Map<String, Object> regDocMap = new HashMap<String, Object>();
            getModifyRegDocumentData(regDocMap, params, participantId, login, password);
            if (!regDocMap.get("FLAG").toString().equalsIgnoreCase("NONE")) {
                regDocs.add(regDocMap);
                modifyParams.put("partRegDocList", regDocs);
            }
        }
        // адрес
        if (!processAddresses(params)) {

            if (needToProcess(params, "PROCESSADDRESS")) {
                modifyParticipantAddressData(params, participantId, login, password);
            }
            if (needToProcess(params, "PROCESSADDRESSPOST")) {
                modifyParticipantAddressPostData(params, participantId, login, password);
            }
        }
        // контакты
        if (needToProcess(params, "PROCESSCONTACTS")) {
            getPersonModifyContactsData(modifyParams, params, participantId, login, password);
        }
        Long insuredType = Long.valueOf(params.get("INSUREDTYPE").toString());
        if (params.get("partCodeList") != null) {
            modifyParams.put("partCodeList", params.get("partCodeList"));
        }
        Map<String, Object> result = null;
        if (insuredType.compareTo(Long.valueOf(3)) == 0) {
            result = this.callService(CRMWS_SERVICE_NAME, "financialInstModify", modifyParams, login, password);//createBank(params, login, password);
        } else {
            result = this.callService(CRMWS_SERVICE_NAME, "companyModify", modifyParams, login, password);
        }

        return result;
    }

    /**
     * Обновление данных участника
     *
     * @author ilich
     * @param params
     * <UL>
     * <LI>PARTICIPANTID - ИД участника</LI>
     * <LI>INSUREDTYPE - тип участника (0 - физ. лицо, 1 - юр. лицо)</LI>
     * <LI>UPDATECONTRDRIVER (для физ. лица) - флаг обновления ИД водителя в
     * таблице INS_CONTRDRIVER, если изменились данные ВУ</LI>
     * <UL>
     */
    @WsMethod(requiredParams = {"PARTICIPANTID", "INSUREDTYPE"})
    public Map<String, Object> dsParticipantCustomModify(Map<String, Object> params) throws Exception {
        Long insuredType = Long.valueOf(params.get("INSUREDTYPE").toString());
        if (insuredType.compareTo(Long.valueOf(0)) == 0) {
            return this.dsPersonModifyEx(params);
        } else {
            return this.dsCompanyModifyEx(params);
        }
    }

    private void loadPersonId(Map<String, Object> result, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("PARTICIPANTID", result.get("PARTICIPANTID"));
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "personGetListByParams", params, login, password);
        if (qres != null) {
            result.put("PERSONID", qres.get("PERSONID"));
        }
    }

    private void loadPersonFullName(Map<String, Object> result, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("PARTICIPANTID", result.get("PARTICIPANTID"));
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "personGetById", params, login, password);
        if (qres != null) {
            if (qres.get("MIDDLENAME") != null) {
                result.put("FULLNAME", String.format("%s %s %s",
                        qres.get("LASTNAME").toString(), qres.get("FIRSTNAME").toString(), qres.get("MIDDLENAME").toString()));
            } else {
                result.put("FULLNAME", String.format("%s %s",
                        qres.get("LASTNAME").toString(), qres.get("FIRSTNAME").toString()));
            }
            result.put("CREATIONDATE", qres.get("CREATIONDATE"));
        }
    }

    private void loadCompanyFullName(Map<String, Object> result, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("PARTICIPANTID", result.get("PARTICIPANTID"));
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "companyGetById", params, login, password);
        if (qres != null) {
            result.put("COMPANYID", qres.get("COMPANYID"));
            result.put("FULLNAME", qres.get("FULLNAME"));
            result.put("DOCUMENTNUMBER", qres.get("SHORTNAME"));
            result.put("INN", qres.get("INN"));
            result.put("CREATIONDATE", qres.get("CREATIONDATE"));
        }
    }

    private void loadPersonDL(Map<String, Object> result, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("PERSONID", result.get("PERSONID"));
        params.put("DOCTYPESYSNAME", "DrivingLicence");
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "personDocGetByPersonIdAndDocType", params, login, password);
        if ((qres.get("DOCSERIES") != null) && (qres.get("DOCNUMBER") != null)) {
            result.put("DOCUMENTNUMBER", String.format("%s %s", qres.get("DOCSERIES").toString(), qres.get("DOCNUMBER").toString()));
        }
    }

    private void loadPersonPassport(Map<String, Object> result, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("PERSONID", result.get("PERSONID"));
        params.put("DOCTYPESYSNAME", "PassportRF");
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "personDocGetByPersonIdAndDocType", params, login, password);
        if ((qres.get("DOCSERIES") != null) && (qres.get("DOCNUMBER") != null)) {
            result.put("PASSPORT", String.format("%s %s", qres.get("DOCSERIES").toString(), qres.get("DOCNUMBER").toString()));
        }
    }

    private void loadPersonForPassport(Map<String, Object> result, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("PERSONID", result.get("PERSONID"));
        params.put("DOCTYPESYSNAME", "ForeignPassportRF");
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "personDocGetByPersonIdAndDocType", params, login, password);
        if ((qres.get("DOCSERIES") != null) && (qres.get("DOCNUMBER") != null)) {
            result.put("DOCUMENTNUMBER", String.format("%s %s", qres.get("DOCSERIES").toString(), qres.get("DOCNUMBER").toString()));
        }
    }

    private void loadParticipantAddress(Map<String, Object> result, String addressTypeSysName, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("PARTICIPANTID", result.get("PARTICIPANTID"));
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "addressGetListByParticipantId", params, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> addressList = (List<Map<String, Object>>) qres.get(RESULT);
            Long minPriority = new Long(Long.MAX_VALUE);
            Map<String, Object> minMap = null;
            for (Map<String, Object> bean : addressList) {
                if (bean.get("ADDRESSTYPESYSNAME").toString().compareTo(addressTypeSysName) == 0) {
                    Long priority = Long.valueOf(0);
                    if (bean.get("PRIORITY") != null) {
                        priority = Long.valueOf(bean.get("PRIORITY").toString());
                    }
                    if (priority.compareTo(minPriority) < 0) {
                        minPriority = Long.valueOf(priority.toString());
                        minMap = bean;
                    }
                }
            }
            if (minMap != null) {
                result.put("ADDRESS", minMap.get("ADDRESSTEXT2"));
            }
        }
    }

    private void loadCompanyRegistration(Map<String, Object> result, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("PARTICIPANTID", result.get("PARTICIPANTID"));
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "partRegDocGetListByParticipantId", params, login, password);
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> regList = (List<Map<String, Object>>) qres.get(RESULT);
            for (Map<String, Object> bean : regList) {
                if ((bean.get("REGDOCTYPESYSNAME") != null) && (bean.get("REGDOCTYPESYSNAME").toString().compareTo("OGRN") == 0)) {
                    Date issuedDate = (Date) bean.get("ISSUEDATE");
                    String d = "";
                    if (issuedDate != null) {
                        Format formatter = new SimpleDateFormat("dd.MM.yyyy");
                        d = formatter.format(issuedDate);
                    }
                    String inn = "";
                    if (result.get("INN") != null) {
                        inn = result.get("INN").toString();
                    }
                    String docSeries = "";
                    if (bean.get("DOCSERIES") != null) {
                        docSeries = bean.get("DOCSERIES").toString();
                    }
                    String docNumber = "";
                    if (bean.get("DOCNUMBER") != null) {
                        docNumber = bean.get("DOCNUMBER").toString();
                    }
                    String regPlace = "";
                    if (bean.get("REGISTRPLACE") != null) {
                        regPlace = ", " + bean.get("REGISTRPLACE").toString();
                    }
                    result.put("PASSPORT", String.format("%s; %s %s от %s%s", inn, docSeries,
                            docNumber, d, regPlace));
                }
            }
        }
    }

    @WsMethod(requiredParams = {"PARTICIPANTIDLIST"})
    public Map<String, Object> dsParticipantLoadAddData(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Map<String, Object>> participantList = (List<Map<String, Object>>) params.get("PARTICIPANTIDLIST");
        String product = "";
        if (params.get("PRODUCT") != null) {
            product = params.get("PRODUCT").toString();
        }
        for (Map<String, Object> bean : participantList) {
            loadPersonId(bean, login, password);
            if (bean.get("PERSONID") != null) {
                loadPersonFullName(bean, login, password);
                loadPersonPassport(bean, login, password);
                if (product.equalsIgnoreCase("")) {
                    loadPersonDL(bean, login, password);
                } else if (product.equalsIgnoreCase("travel")) {
                    loadPersonForPassport(bean, login, password);
                }
                loadParticipantAddress(bean, "RegisterAddress", login, password);
                bean.put("PERSONTYPE", 0);
            } else {
                loadCompanyFullName(bean, login, password);
                if (bean.get("COMPANYID") != null) {

                    loadParticipantAddress(bean, "JuridicalAddress", login, password);
                    loadCompanyRegistration(bean, login, password);
                    bean.put("PERSONTYPE", 1);
                } else {
                    // ну значит наверняка банк. читаем оттудова.
                    loadBankFullName(bean, login, password);
                    loadParticipantAddress(bean, "JuridicalAddress", login, password);
                    loadCompanyRegistration(bean, login, password);
                }
            }
            XMLUtil.convertFloatToDate(bean);
        }
        Collections.sort(participantList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> cs1, Map<String, Object> cs2) {
                Date date1 = (Date) cs1.get("CREATIONDATE");
                Date date2 = (Date) cs2.get("CREATIONDATE");
                if (date1.getTime() < date2.getTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        while (participantList.size() > 3) {
            participantList.remove(participantList.size() - 1);
        }
        result.put(RESULT, participantList);
        return result;
    }

    @WsMethod(requiredParams = {"ADDRESSCODE"})
    public Map<String, Object> dsAddressGetKladrCodes(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // разыменовка места
        Map<String, Object> kladrParams = new HashMap<String, Object>();
        kladrParams.put("CODE", params.get("ADDRESSCODE"));
        Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrBrowseListByParam", kladrParams, login, password);
        // разыменовка улицы
        Map<String, Object> streetMap = null;
        Map<String, Object> streetParams = new HashMap<String, Object>();
        if ((params.get("STREETCODE") != null) && (!"".equals(params.get("STREETCODE").toString()))) {
            streetParams.put("KLADRCODE", params.get("STREETCODE"));
            Map<String, Object> qStreet = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrStreetBrowseListByParam", streetParams, login, password);
            if ((qStreet != null) && (qStreet.get(RESULT) != null) && (((List) qStreet.get(RESULT)).size() > 0)) {
                streetMap = (Map<String, Object>) ((List) qStreet.get(RESULT)).get(0);
            }
        } else if ((params.get("STREETKLADR") != null) && (!"".equals(params.get("STREETKLADR").toString()))) {
            streetParams.put("KLADRCODE", params.get("STREETKLADR"));
            Map<String, Object> qStreet = this.callService(INSPOSWS_SERVICE_NAME, "dsKladrStreetBrowseListByParam", streetParams, login, password);
            if ((qStreet != null) && (qStreet.get(RESULT) != null) && (((List) qStreet.get(RESULT)).size() > 0)) {
                streetMap = (Map<String, Object>) ((List) qStreet.get(RESULT)).get(0);
            }
        }
        if ((qres != null) && (qres.get(RESULT) != null) && (((List) qres.get(RESULT)).size() > 0)) {
            Map<String, Object> map = (Map<String, Object>) ((List) qres.get(RESULT)).get(0);
            result.put("REGION", map.get("REGIONNAME"));
            result.put("REGIONKLADR", map.get("REGIONCODE"));
            result.put("REGIONTYPE", map.get("REGIONTYPECODE"));
            result.put("REGIONTYPENAME", map.get("REGIONTYPE"));
            result.put("REGIONFULLNAME", map.get("REGIONFULLNAME"));
            result.put("DISTRICT", map.get("ZONENAME"));
            result.put("DISTRICTKLADR", map.get("ZONECODE"));
            result.put("DISTRICTTYPE", map.get("ZONETYPECODE"));
            result.put("DISTRICTTYPENAME", map.get("ZONETYPE"));
            result.put("DISTRICTFULLNAME", map.get("ZONEFULLNAME"));
            result.put("CITY", map.get("CITYNAME"));
            result.put("CITYKLADR", map.get("CITYCODE"));
            result.put("CITYTYPE", map.get("CITYTYPECODE"));
            result.put("CITYTYPENAME", map.get("CITYTYPE"));
            result.put("CITYFULLNAME", map.get("CITYFULLNAME"));
            result.put("VILLAGE", map.get("PLACENAME"));
            result.put("VILLAGEKLADR", map.get("PLACECODE"));
            result.put("VILLAGETYPE", map.get("PLACETYPECODE"));
            result.put("VILLAGETYPENAME", map.get("PLACETYPE"));
            result.put("VILLAGEFULLNAME", map.get("PLACEFULLNAME"));
            if (streetMap != null) {
                result.put("STREETKLADR", streetMap.get("CODE"));
                result.put("STREET", streetMap.get("NAME"));
                result.put("STREETTYPE", streetMap.get("TYPECODE"));
                result.put("STREETTYPENAME", streetMap.get("TYPE"));
            } else {
                result.put("STREET", params.get("STREET"));
                result.put("STREETTYPE", params.get("STREETTYPE"));
                result.put("STREETKLADR", null);
            }
        }
        return result;
    }

    private Map<String, Object> insertParticipantAndCreateNode(Long participantId, int verNumber, String login, String password) throws Exception {
        // создаем нод (уже со ссылкой на лицо, т.к. его идшник нам известен)
        Map<String, Object> crParams = new HashMap<String, Object>();
        crParams.put("ReturnAsHashMap", "TRUE");
        crParams.put("PARTICIPID", participantId);
        crParams.put("RVERSION", 0);
        crParams.put("LASTVERNUMBER", verNumber);
        Map<String, Object> qres1 = this.callService(INSPOSWS_SERVICE_NAME, "dsParticipantNodeCreate", crParams, login, password);
        if (qres1 != null) {
            // добавляем лицо в верс. таблицу
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("PARTICIPID", participantId);
            params.put("PARTICIPNODEID", qres1.get("PARTICIPNODEID"));
            params.put("VERNUMBER", verNumber);
            Map<String, Object> qres2 = this.callService(INSPOSWS_SERVICE_NAME, "dsParticipantInsert", params, login, password);
        }
        return qres1;
    }

    private void insertParticipantAndRefreshNode(Long participantId, Long participantNode, String login, String password) throws Exception {
        // добавляем лицо в верс. таблицу
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("PARTICIPID", participantId);
        params.put("PARTICIPNODEID", participantNode);
        params.put("VERNUMBER", 0);
        Map<String, Object> qres1 = this.callService(INSPOSWS_SERVICE_NAME, "dsParticipantInsert", params, login, password);
        // обновляем активную версию в ноде
        Map<String, Object> updParams = new HashMap<String, Object>();
        updParams.put("PARTICIPID", participantId);
        updParams.put("PARTICIPNODEID", participantNode);
        Map<String, Object> qres2 = this.callService(INSPOSWS_SERVICE_NAME, "dsParticipantNodeUpdate", updParams, login, password);
    }

    @WsMethod(requiredParams = {"PARTICIPANTID"})
    public Map<String, Object> dsParticipantSaveToNode(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // если исходное лицо указано
        if ((params.get("SRCPARTICIPANTID") != null) && (!params.get("SRCPARTICIPANTID").toString().isEmpty()) && (Long.valueOf(params.get("SRCPARTICIPANTID").toString())).longValue() > 0) {
            // ищем нод у исходного лица
            Map<String, Object> findParams = new HashMap<String, Object>();
            findParams.put("ReturnAsHashMap", "TRUE");
            findParams.put("PARTICIPID", params.get("SRCPARTICIPANTID"));
            Map<String, Object> qres = this.callService(INSPOSWS_SERVICE_NAME, "dsParticipantBrowseListByParam", findParams, login, password);
            // если нашли нод
            if ((qres != null) && (qres.get("PARTICIPNODEID") != null)) {
                // создаем новую запись в верс. таблице и устанавливаем активную версию в ноде
                insertParticipantAndRefreshNode(Long.valueOf(params.get("PARTICIPANTID").toString()), Long.valueOf(qres.get("PARTICIPNODEID").toString()), login, password);
            } else {
                Map<String, Object> qres1 = insertParticipantAndCreateNode(Long.valueOf(params.get("PARTICIPANTID").toString()), 1, login, password);
                // если ноды не было, но было указано исходное лицо, то это копирование, и при копировании в версионную таблицу надо сохранить и исходное лицо тоже.

                Map<String, Object> qOldParams = new HashMap<String, Object>();
                qOldParams.put("PARTICIPID", params.get("SRCPARTICIPANTID"));
                qOldParams.put("PARTICIPNODEID", qres1.get("PARTICIPNODEID"));
                qOldParams.put("VERNUMBER", 0);
                this.callService(INSPOSWS_SERVICE_NAME, "dsParticipantInsert", qOldParams, login, password);
            }
        } else {
            insertParticipantAndCreateNode(Long.valueOf(params.get("PARTICIPANTID").toString()), 0, login, password);
        }
        return result;
    }

    private void createPartCodeList(Map<String, Object> params, Long participantId, String login, String password) {
        //modifyParams.put("partCodeList",params.get("PARTCODELIST"));
    }

    private void loadBankFullName(Map<String, Object> result, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("ReturnAsHashMap", "TRUE");
        params.put("PARTICIPANTID", result.get("PARTICIPANTID"));
        Map<String, Object> qres = this.callService(CRMWS_SERVICE_NAME, "financialinstGetById", params, login, password);
        if (qres != null) {
            result.put("FININSTID", qres.get("FININSTID"));
            result.put("FULLNAME", qres.get("FULLNAME"));
            result.put("DOCUMENTNUMBER", qres.get("SHORTNAME"));
            result.put("INN", qres.get("INN"));
            result.put("CREATIONDATE", qres.get("CREATIONDATE"));
        }
    }

    private boolean processAddresses(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        boolean result = false;
        if (params.get("ADDRESSDATA") != null) {
            List<Map<String, Object>> addressList = (List<Map<String, Object>>) params.get("ADDRESSDATA");
            //Map<String, Object> addressMaps = CopyUtils.filterHashMapByStr(params, "ADDRESSMAP");
            //Set<Entry<String, Object>> addrEntrySet = addressMaps.entrySet();
            for (Map<String, Object> address : addressList) {
                result = processAddress(address, params);
            }

        }
        return result;
    }

    private boolean processAddress(Map<String, Object> map, Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // разыменовка места
        Map<String, Object> qParam = new HashMap<String, Object>();

        qParam.put("ADDRESSMAP", map);
        qParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> modifyParams = this.callExternalService(INSPOSWS_SERVICE_NAME, "dsGenFullAddressMap", qParam, login, password);
        modifyParams.put("PARTICIPANTID", params.get("PARTICIPANTID"));
        if (((params.get("CREATENEWADDRESS") == null) || (!params.get("CREATENEWADDRESS").toString().equals("TRUE")))
                && ((modifyParams.get("ADDRESSID") != null) && (Long.valueOf(modifyParams.get("ADDRESSID").toString()).longValue() > 0))) {
            modifyParams.put("ADDRESSID", modifyParams.get("ADDRESSID"));
            Map<String, Object> qRes = this.callService(CRMWS_SERVICE_NAME, "addressModify", modifyParams, login, password);
            logger.debug(qRes.toString());
        } else {
            Map<String, Object> qRes = this.callService(CRMWS_SERVICE_NAME, "addressCreate", modifyParams, login, password);
            logger.debug(qRes.toString());
        }

        return true;
    }

// //<editor-fold defaultstate="collapsed" desc="перенесено в B2BBaseFacade и переименовано">
//    private Map<String, Object> participantGetByID(Object participantID, String login, String password) throws Exception {
//        Map<String, Object> result = null;
//        if (participantID != null) {
//            Map<String, Object> participantParams = new HashMap<String, Object>();
//            participantParams.put("PARTICIPANTID", participantID);
//            participantParams.put(RETURN_AS_HASH_MAP, true);
//            Map<String, Object> participant = this.callService(CRMWS_SERVICE_NAME, "participantGetByIdFull", participantParams, login, password);
//        // костыль для загрузки учредителей для юр лица. т.к. црм поддерживает автозагрузку только учредилелей финансовых организаций.
//            // а сохраняет норм.
//            if (participant != null) {
//                if (participant.get("PARTICIPANTTYPE") != null) {
//                    if ("2".equals(participant.get("PARTICIPANTTYPE").toString())) {
//                        Map<String, Object> affParams = new HashMap<String, Object>();
//                        affParams.put("PARTICIPANTID", participantID);
//                        Map<String, Object> affParamsRes = this.callService(Constants.CRMWS, "affiliateGetListByMainParticipantId", params, login, password);
//                        if (affParamsRes != null) {
//                            if (affParamsRes.get(RESULT) != null) {
//                                participant.put("affiliateIfnsList", affParamsRes.get(RESULT));
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (participant != null) {
//                result = (Map<String, Object>) participant;
//            }
//        }
//        return result;
//    }
//</editor-fold>
    /**
     * Функция создания нового или обновления существующего участника.
     * Конкретная операция выбирается по наличию идентификатора участника в
     * переданных данных.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BParticipantSave(Map<String, Object> params) throws Exception {

        logger.debug("Обработка данных участника...\n");

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> result = null;

        // идентификатор участника
        Object participantID = params.get("PARTICIPANTID");

        // для создания-удаления расширенных атрибутов
        // надо зачистить из перечня все атрибуты с пустым value
        if (params.get("extAttributeList") != null) {
            params.remove("extAttributeList");
        }
        if (params.get("extAttributeList2") != null) {
            List<Map<String, Object>> extAttrList = (List<Map<String, Object>>) params.get("extAttributeList2");
            List<Map<String, Object>> extAttrListNew = new ArrayList<Map<String, Object>>();

            for (Map<String, Object> extAttr : extAttrList) {
                if (extAttr.get("EXTATTVAL_VALUE") != null) {
                    extAttrListNew.add(extAttr);
                }
            }
            params.put("extAttributeList2", extAttrListNew);
        }

        // флаг удаления участника
        Object rowStatus = params.get(ROWSTATUS_PARAM_NAME);
        Object crmFlag = params.get(FLAG_PARAM_NAME);
        boolean isParticipantMarkedForDeleting
                = ((rowStatus != null) && (Integer.parseInt(rowStatus.toString()) == DELETED_ID))
                || ((crmFlag != null) && (FLAG_DEL.equalsIgnoreCase(crmFlag.toString())));

        if (isParticipantMarkedForDeleting) {
            // удаление участника
            //logger.debug("Удаление участника...\n");
            //if (participantID != null) {
            //    //result = this.callServiceLogged(CRMWS_SERVICE_NAME, "participantDelete", params, login, password); // todo: управляемое протоколирование
            //    result = this.callService(CRMWS_SERVICE_NAME, "participantDelete", params, login, password);
            //    if (isCallResultOK(result)) {
            //        logger.debug("Удаление участника с идентификатором '" + participantID + "' завершено без возникновения ошибок.\n");
            //    } else {
            //        logger.error("В ходе удаления сведений участника с идентификатором '" + participantID + "' возникла ошибка:\n" + result.get("Error"));
            //    }
            //} else {
            //    logger.debug("Удаление участника не было выполнено - в переданных сведениях отсутствует идентификатор удаляемого участника.\n");
            //}
            //return result;
            logger.debug("Удаление участников отключено.\n");
            return params;
        }

        // проверка переданных списков на наличие некорректных элементов
        // (такие элементы будут убраны из списков, за исключением элементов помеченных удаляемыми)
        logger.debug("Анализ переданных списков атрибутов участника на наличие некорректных элементов...\n");
        for (String[] validatingListAttr : participantListsValidationInfo) {
            String listName = validatingListAttr[0];
            String[] attrNames = Arrays.copyOfRange(validatingListAttr, 1, validatingListAttr.length);
            Object listObj = params.get(listName);
            if ((listObj != null) && (listObj instanceof List)) {
                List<Object> rawList = (List<Object>) listObj;
                ArrayList<Map<String, Object>> pureList = new ArrayList<Map<String, Object>>();
                for (Object rowObj : rawList) {
                    boolean isRowValid = false; // до проверки на null элемент списка считается некорректным
                    if (rowObj != null) {
                        Map<String, Object> row = (Map<String, Object>) rowObj;
                        isRowValid = true; // существующий элемент списка являющийся картой до обнаружения нехватки ключевого атрибута считается корректным
                        for (String attrName : attrNames) {
                            Object valueObj = row.get(attrName);
                            boolean isKeyValueMissing = (valueObj == null) || (valueObj.toString().isEmpty());
                            if (isKeyValueMissing) {
                                Object statusFlag = row.get(FLAG_PARAM_NAME);
                                boolean isMarkedForDelete = ((statusFlag != null) && (FLAG_DEL.equalsIgnoreCase(statusFlag.toString())));
                                if (!isMarkedForDelete) {
                                    isRowValid = false; // первый же отсутствующий ключевой атрибут делает элемент некорректным
                                    logger.debug("Из списка '" + listName + "' будет удален указанный элемент: " + rowObj.toString());
                                    logger.debug("Причина удаления данного элемента из списка '" + listName + "': обязательный атрибут '" + attrName + "' создаваемого или изменяемого элемента отсутствует или пуст.\n");
                                    break;
                                }
                            }
                        }
                    }
                    // корректный элемент переноситься в "чистый" список
                    if (isRowValid) {
                        pureList.add((Map<String, Object>) rowObj);
                    }
                }
                // замена списка
                params.put(listName, pureList);
            }
        }

        // полные строковые адреса для каждого элемента из списка адресов
        // todo: условное обновление (для случаев обновления сведений участников без изменения адресов)
        Object addressListObj = params.get("addressList");
        if ((addressListObj != null) && (addressListObj instanceof List)) {
            logger.debug("Подготовка полных строковых представлений для адресов...\n");
            ArrayList<Map<String, Object>> addressList = (ArrayList<Map<String, Object>>) addressListObj;
            for (Map<String, Object> address : addressList) {
                // получение сведений из КЛАДР
                Map<String, Object> genFullAddressMapParams = new HashMap<String, Object>();
                if ((address.get("USEKLADR") != null) && ("1".equals(address.get("USEKLADR").toString()))) {
                    //адрес не нуждается в генерации строки - она указана вручную.
                    continue;
                }
                genFullAddressMapParams.putAll(address);
                genFullAddressMapParams.put("PARTICIPANTTYPE", params.get("PARTICIPANTTYPE")); // передача типа участника в генерацию адресов для выбора между 'кв. ' и 'оф. '
                genFullAddressMapParams.put(RETURN_AS_HASH_MAP, true);
                //Map<String, Object> fullAddressMap = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsGenFullTextAddress", genFullAddressMapParams, login, password); // todo: управляемое протоколирование

                Map<String, Object> fullAddressMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsGenFullTextAddress", genFullAddressMapParams, login, password);
                if (fullAddressMap != null) {
                    address.putAll(fullAddressMap);
                }
            }
        }

        // todo: перенести сюда общие для dsB2BParticipantCreate и dsB2BParticipantModify подготовительные операции над данными
        // преобразование из возможных форматов в числа для сохранения в БД
        parseDates(params, Double.class);

        if ((participantID == null) || (participantID.toString().isEmpty())) {
            logger.debug("Создание нового участника...\n");
            //result = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BParticipantCreate", params, login, password); // todo: управляемое протоколирование
            result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BParticipantCreate", params, login, password);
        } else {
            logger.debug("Обновление участника с идентификатором '" + participantID.toString() + "'...\n");
            //result = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BParticipantModify", params, login, password); // todo: управляемое протоколирование
            result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BParticipantModify", params, login, password);
        }

        if (result != null) {
            // преобразование дат из возможных форматов в строки для передачи в Angular-интерфейс
            // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
            parseDates(params, String.class);
            logger.debug("Сохранение данных участника с идентификатором '" + ((Map<String, Object>) result.get(RESULT)).get("PARTICIPANTID") + "' завершено.\n");
        }

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BParticipantLoad(Map<String, Object> params) throws Exception {
        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        Map<String, Object> result = loadParticipant(params.get("PARTICIPANTID"), login, password);
        if (result != null) {
            // преобразование дат из возможных форматов в строки для передачи в Angular-интерфейс
            // возможно, здесь не потребуется, когда преобразование дат в выходных параметрах будет перенесено в BoxPropertyGate
            parseDates(result, String.class);
        }

        return result;
    }

    /**
     * Функция создания нового участника. Предназначена для вызова из
     * dsB2BParticipantSave, использовать напрямую не рекомендуется.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"PARTICIPANTTYPE"})
    public Map<String, Object> dsB2BParticipantCreate(Map<String, Object> params) throws Exception {

        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        if (params.get("STATE") == null) {
            params.put("STATE", "Обслуживается");
        }
        if (params.get("ISCLIENT") == null) {
            params.put("ISCLIENT", "1");
        }
        if (params.get("BRIEFNAME") == null) {
            StringBuilder briefName = new StringBuilder();
            String lastName = getStringParam(params.get("LASTNAME")); // Фамилия
            briefName.append(lastName);
            String firstName = getStringParam(params.get("FIRSTNAME")); // Имя
            if (!firstName.isEmpty()) {
                briefName.append(" ").append(firstName.charAt(0)).append("."); // И.
                String middleName = getStringParam(params.get("MIDDLENAME")); // Отчество
                if (!middleName.isEmpty()) {
                    briefName.append(" ").append(middleName.charAt(0)).append("."); // О.
                }
            }
            if (briefName.length() > 0) {
                params.put("BRIEFNAME", briefName.toString());
            } else {
                params.put("BRIEFNAME", " ");
            }
        }
        if (params.get("LASTNAME") == null) {
            params.put("LASTNAME", " ");
        }
        if (params.get("FIRSTNAME") == null) {
            params.put("FIRSTNAME", " ");
        }
        if (params.get("MIDDLENAME") == null) {
            params.put("MIDDLENAME", " ");
        }
        if (params.get("BRIEFNAME") == null) {
            params.put("BRIEFNAME", params.get("SHORTNAME"));
        } else if (params.get("SHORTNAME") == null) {
            params.put("SHORTNAME", params.get("BRIEFNAME"));
        }
        if (params.get("FULLNAME") == null) {
            params.put("FULLNAME", params.get("BRIEFNAME"));
        }

        // если не передана дата создания участника - то значит сейчас (в формате числа для БД, т. к. все остальные даты уже преобразованы в dsB2BParticipantSave)
        Date nowDate = new Date();
        if (params.get("CREATIONDATE") == null) {
            //params.put("CREATIONDATE", parseAnyDate(nowDate, Double.class, "CREATIONDATE", true)); // с протоколированием, для проверки работы с датами
            params.put("CREATIONDATE", parseAnyDate(nowDate, Double.class, "CREATIONDATE")); // без протоколирования
        }

        // для гарантированного создания новой организации через CRM необходимо чтобы комбинация её ИНН+ПолноеИмя+... отличалась от имеющихся в базе
        // для гарантированного создания нового физического лица через CRM необходимо чтобы комбинация её Фамилия+Имя+... отличалась от имеющихся в базе
        // для создания индивидуального предпринимателя подобного не требуется
        // todo: другой вариант создания организации через CRM, без необходимости выполнения создания+запроса+обновления+запроса
        String participantType = getStringParamLogged(params, "PARTICIPANTTYPE");

        String isBusinessman = "0";
        if (params.get("ISBUSINESSMAN") != null) {
            isBusinessman = params.get("ISBUSINESSMAN").toString();
        }
        String realName = null;
        String trickNameKey = null;
        boolean isJuridical = "2".equals(participantType);
        boolean isPhisical = (!isJuridical) && ("0".equals(isBusinessman));
        if (isJuridical) {
            trickNameKey = "FULLNAME";
        } else if (isPhisical) {
            trickNameKey = "LASTNAME";
        }
        boolean isNameTrickNeeded = trickNameKey != null;

        if (isNameTrickNeeded) {
            realName = getStringParamLogged(params, trickNameKey);

            // создаваемой организации генерируется уникальное полное имя (действительное + текущая дата и время)
            // создаваемому физлицу генерируется уникальная фамилия (действительная + текущая дата и время)
            String uniqeName = realName + nowDate.toString();
            params.put(trickNameKey, uniqeName);

            if (logger.isDebugEnabled()) {
                if (isJuridical) {
                    logger.debug("Участник будет создан с полным наименованием '" + uniqeName + "'.\n");
                } else {
                    logger.debug("Участник будет создан с фамилией '" + uniqeName + "'.\n");
                }
            }

        }

        //фикс, если есть пустой список элементов грохать его. ато црм упадет
        List<Map<String, Object>> altNameList = (List<Map<String, Object>>) params.get("altNameList");
        if (altNameList != null) {
            if (altNameList.isEmpty()) {
                params.remove("altNameList");
            } else if (altNameList.get(0) != null) {
                if (altNameList.get(0).get("ALTNAME") != null) {
                    if (altNameList.get(0).get("ALTNAME").toString().isEmpty()) {
                        params.remove("altNameList");
                    }
                } else {
                    params.remove("altNameList");
                }
            } else {
                params.remove("altNameList");
            }
        }

        // вызов создания участника через CRM
        Object participantID = this.callServiceAndGetOneValue(CRMWS_SERVICE_NAME, "participantCreate", params, login, password, "PARTICIPANTID");
        Map<String, Object> result = loadParticipant(participantID, login, password);
        if (result != null) {

            if (logger.isDebugEnabled()) {
                if (isJuridical) {
                    logger.debug("Cоздан участник с идентификатором " + result.get("PARTICIPANTID") + " и полным наименованием '" + result.get(trickNameKey) + "'.\n");
                } else {
                    logger.debug("Cоздан участник с идентификатором " + result.get("PARTICIPANTID") + " и фамилией '" + result.get(trickNameKey) + "'.\n");
                }
            }

            // созданной организации/физлицу возвращается действительное неуникальное наименование/фамилия
            // todo: другой вариант создания организации через CRM, без необходимости выполнения создания+запроса+обновления+запроса
            if (isNameTrickNeeded) {
                Map<String, Object> updateParams = new HashMap<String, Object>();
                updateParams.putAll(result);
                updateParams.put(trickNameKey, realName);

                if (logger.isDebugEnabled()) {
                    if (isJuridical) {
                        logger.debug("Участнику с идентификатором " + result.get("PARTICIPANTID") + " будет возвращено действительное полное наименование '" + realName + "'.\n");
                    } else {
                        logger.debug("Участнику с идентификатором " + result.get("PARTICIPANTID") + " будет возвращена действительная фамилия '" + realName + "'.\n");
                    }
                }

                // чтобы гарантированно избежать повторного создания сущностей все имеющиеся помечаются как измененные
                // todo: возможно, достаточно отсутствия флага FLAG (в CRM для неизменившихся записей явного значения флага нет)
                markAllMapsByKeyValue(result, FLAG_PARAM_NAME, FLAG_UPD);

                //еще один гвоздь в гроб ЦРМ. при создании лица не создаются сущности altNameList .
                // зато при создании создаются обновляются норм.
                // поэтому щас пытаемся еще раз добавить его.
                /* if (params.get("altNameList") != null) {
                 List<Map<String, Object>> altNameList = (List<Map<String, Object>>) params.get("altNameList");
                 altNameList.get(0).put("PARTICIPANTID", participantID);
                 }*/
                // updateParams.put("altNameList", params.get("altNameList"));
                updateParams.put(RETURN_AS_HASH_MAP, true);
                //result = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BParticipantModify", updateParams, login, password);
                result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BParticipantModify", updateParams, login, password);
                if (((result != null)) && (logger.isDebugEnabled())) {
                    if (isJuridical) {
                        logger.debug("Участнику с идентификатором " + result.get("PARTICIPANTID") + " возвращено действительное полное наименование '" + result.get(trickNameKey) + "'.\n");
                    } else {
                        logger.debug("Участнику с идентификатором " + result.get("PARTICIPANTID") + " возвращена действительная фамилия '" + result.get(trickNameKey) + "'.\n");
                    }
                }

            }
        }

        return result;
    }

    /**
     * Функция обновления существующего участника. Предназначена для вызова из
     * dsB2BParticipantSave, использовать напрямую не рекомендуется.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"PARTICIPANTID"})
    public Map<String, Object> dsB2BParticipantModify(Map<String, Object> params) throws Exception {
        // логин и пароль для вызова других методов веб-сервиса
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        // дата обновления участника - безусловно сейчас (в формате числа для БД, т. к. все остальные даты уже преобразованы в dsB2BParticipantSave)
        Date nowDate = new Date();
        //params.put("MODIFYDATE", parseAnyDate(nowDate, Double.class, "MODIFYDATE", true)); // с протоколированием, для проверки работы с датами
        params.put("MODIFYDATE", parseAnyDate(nowDate, Double.class, "MODIFYDATE")); // без протоколирования

// если это юр лицо - надо пройтись по списку учредителей и грохнуть всех у которых флаг DEL
        if ("2".equals(getStringParam(params.get("PARTICIPANTTYPE")))) {
            if (params.get("affiliateIfnsList") != null) {
                List<Map<String, Object>> affilList = (List<Map<String, Object>>) params.get("affiliateIfnsList");
                for (Map<String, Object> affil : affilList) {
                    if (affil.get("FLAG") != null) {
                        if ("DEL".equalsIgnoreCase(getStringParam(affil.get("FLAG")))) {
                            // удалить связь
                            this.callService(CRMWS_SERVICE_NAME, "affiliateDelete", affil, login, password);
                            affil.put("FLAG", "UNMOD");
                        }
                    } else {
                        affil.put("FLAG", "UNMOD");
                    }
                }
            }
        }
        // todo: заново генерировать вычисляемые параметры - BRIEFNAME, SHORTNAME и т.п. (могли поменяться исходные сведения)
        StringBuilder briefName = new StringBuilder();
        String lastName = getStringParam(params.get("LASTNAME")); // Фамилия
        briefName.append(lastName);
        String firstName = getStringParam(params.get("FIRSTNAME")); // Имя
        if (!firstName.isEmpty()) {
            briefName.append(" ").append(firstName.charAt(0)).append("."); // И.
            String middleName = getStringParam(params.get("MIDDLENAME")); // Отчество
            if (!middleName.isEmpty()) {
                briefName.append(" ").append(middleName.charAt(0)).append("."); // О.
            }
        }
        if (briefName.length() > 0) {
            params.put("BRIEFNAME", briefName.toString());
        }
        if (params.get("BRIEFNAME") == null) {
            params.put("BRIEFNAME", params.get("SHORTNAME"));
        } else if (params.get("SHORTNAME") == null) {
            params.put("SHORTNAME", params.get("BRIEFNAME"));
        }

        //
        // обновление данных участника
        this.callService(CRMWS_SERVICE_NAME, "participantModify", params, login, password);
        Object participantID = params.get("PARTICIPANTID");
        Map<String, Object> result = loadParticipant(participantID, login, password);

        return result;
    }

}
