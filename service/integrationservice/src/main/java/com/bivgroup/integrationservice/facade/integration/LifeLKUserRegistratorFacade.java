/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.integrationservice.facade.integration;

import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.sberinsur.esb.partner.shema.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import static com.bivgroup.integrationservice.facade.integration.PPOOnlineHelper.*;
import static ru.sberinsur.esb.partner.shema.ASalesServOnlineType.*;

/**
 * @author sambucus
 */
@BOName("LifeLKUserRegistrator")
public class LifeLKUserRegistratorFacade extends LifeContractGetterFacade {

    public static final String PPOONLINE_NOTIFICATION_TYPE_SYSNAME = "PPOONLINE";

    @WsMethod()
    public Map<String, Object> dsLifeShortExternalIdRequest(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<String, Object>();

        try {
            RegistrationUser user = new RegistrationUser();
            user.setBirthDate(getFormattedDate(getDateParam(params.get("BIRTHDATE"))));
            user.setPolicyNumber(getStringParam(params.get("POLICYNUMBER")));
            user.setThirdPartyFullName(getStringParam(params.get("FIO")));
            RegistrationUserAnswer resUser = callLifePartnerRegistrationUser(user);
            String goltXML = this.marshall(user, RegistrationUser.class);
            String contractListRespXML = this.marshall(resUser, RegistrationUserAnswer.class);
            long thirdPartyId = resUser.getThirdPartyId();
            if (thirdPartyId == 0) {
                logger.error(
                        "Unable to get thirdPartyId from callLifePartnerRegistrationUser." +
                                "\n\nRequest: \n\n" + goltXML +
                                "\n\nResponse: \n\n" + contractListRespXML
                );
            }
            result.put("EXTPARTICIPANTID", thirdPartyId);
            result.put("requestStr", goltXML);
            result.put("responseStr", contractListRespXML);
            result.put("STATUS", "DONE");
        } catch (Exception e) {
            logger.error("Partner service GetContractsCut call error", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.toString(); // stack trace as a string
            result.put("responseStr", sw.toString());
            result.put("STATUS", "outERROR");
        }
        return result;

    }

    @WsMethod()
    public Map<String, Object> dsLifeRegisterUser(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            RegistrationUser user = new RegistrationUser();
            user.setBirthDate(getFormattedDate(getDateParam(params.get("BIRTHDATE"))));
            Map<String, Object> mapDoc = (Map<String, Object>) params.get("DOCUMENTMAP");

            DocumentsType dt = new DocumentsType();
            dt.setDocumentFull(getStringParam(mapDoc.get("DESCRIPTION")));
            dt.setDocumentType(DOCUMENTTYPEMAP.get(getStringParam(mapDoc.get("DOCTYPESYSNAME"))));
            //thirdParty.setDocumentCountry(""); // пропускаем
            dt.setDocumentNumber(getStringParam(mapDoc.get("DOCNUMBER")));
            dt.setDocumentSeries(getStringParam(mapDoc.get("DOCSERIES")));
            dt.setDocumentInstitution(getStringParam(mapDoc.get("ISSUEDBY")));
            dt.setDocumentDate(getFormattedDate(getDateParam(mapDoc.get("ISSUEDATE"))));
            //thirdParty.setDocumentCity(); пропускаем
            dt.setDocumentCodeIns(getStringParam(mapDoc.get("ISSUERCODE")));
            user.setDocument(dt);
            user.setPhoneMobile(getStringParam(params.get("PHONE")));
            user.setThirdPartyFullName(getStringParam(params.get("FIO")));
            RegistrationUserAnswer resUser = callLifePartnerRegistrationUser(user);
            String goltXML = this.marshall(user, RegistrationUser.class);
            String contractListRespXML = this.marshall(resUser, RegistrationUserAnswer.class);
            result.put("EXTPARTICIPANTID", resUser.getThirdPartyId());
            result.put("requestStr", goltXML);
            result.put("responseStr", contractListRespXML);
            result.put("STATUS", "DONE");
        } catch (Exception e) {
            logger.error("Partner service GetContractsCut call error", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.toString(); // stack trace as a string
            result.put("responseStr", sw.toString());
            result.put("STATUS", "outERROR");
        }
        return result;

    }

    @WsMethod()
    public Map<String, Object> dsLifeUpdateUserByExternalId(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> client = params;
        Long clientTPId = getLongParam(client.get("externalId"));

        GetObjType got = new GetObjType();
        got.setPolicyId(getLongParam(params.get("POLICYID")));
        ListContractType lct = callLifePartnerGetContracts(got);

        List<ContractType> contractList = lct.getContract();
        for (ContractType contract : contractList) {
            List<ThirdParty> thirdPartiesList = contract.getThirdPartyList().getThirdParty();
            for (ThirdParty thirdParty : thirdPartiesList) {
                Long tpId = thirdParty.getThirdPartyId();
                if (tpId.compareTo(clientTPId) == 0) {

                    Map<String, Object> participantMap = new HashMap<>();

                    processParticipant(participantMap, thirdParty, contract, login, password);

                    Map<String, Object> clientMap = getClientMapByCrmParticipantMap(participantMap, thirdParty.getPhoneMobile());


                    client.put("surname", thirdParty.getLastName());
                    client.put("name", thirdParty.getFirstName());
                    client.put("patronymic", thirdParty.getPatronymic());
                    client.put("dateOfBirth", processDate(thirdParty.getBirthDate()));

                    if (thirdParty.getCitizenship() != null && !thirdParty.getCitizenship().isEmpty()) {
                        client.put("countryId", getCountryIdByAlphaCode(thirdParty.getCitizenship(), login, password));
                    }
                    client.remove("countryId_EN");
                    client.put("sex", 0);
                    if ("M".equals(thirdParty.getGender())) {
                        client.put("sex", 1);
                    }
                    //client.put("sex", GENDERMAP.getKey(thirdParty.getGender()));

                    client.put("contacts", getContactList((List<Map<String, Object>>) client.get("contacts"), thirdParty, login, password));
                    client.put("documents", getDocumentList((List<Map<String, Object>>) client.get("documents"), thirdParty, login, password));
                    client.put("addresses", getAddressList((List<Map<String, Object>>) client.get("addresses"), thirdParty, login, password));
                    client.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED.getId());
                    Map<String, Object> saveParam = new HashMap<>();
                    saveParam.put("CLIENTMAP", client);

                    client = this.callService(THIS_SERVICE_NAME, "dsB2BClientProfileClientSave", saveParam, login, password);
                    break;
                }
            }
        }

        return client;
    }

    @WsMethod()
    public Map<String, Object> dsLifeUpdateUserByFullContractInfo(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Boolean isCallFromGate = isCallFromGate(params);

        Map<String, Object> clientProfile = (Map<String, Object>) params.get("clientProfile");
        Long clientProfileId = (Long) clientProfile.get("id");
        Map<String, Object> client = (Map<String, Object>) clientProfile.get("clientId_EN");
        Long clientTPId = getLongParam(client.get("externalId"));
        String previousPpoStatus = clientProfile.getOrDefault("prevPpoOnlineType", ASalesServOnlineType.NONE.value()).toString();

        GetObjType got = new GetObjType();
        got.setPolicyId(getLongParam(params.get("POLICYID")));
        ListContractType lct = callLifePartnerGetContracts(got);

        List<ContractType> contractList = lct.getContract();
        for (ContractType contract : contractList) {

            List<ThirdParty> thirdPartiesList = contract.getThirdPartyList().getThirdParty();
            for (ThirdParty thirdParty : thirdPartiesList) {
                Long tpId = thirdParty.getThirdPartyId();
                if (tpId.compareTo(clientTPId) == 0) {

                    Map<String, Object> participantMap = new HashMap<>();

                    processParticipant(participantMap, thirdParty, contract, login, password);

                    Map<String, Object> clientMap = getClientMapByCrmParticipantMap(participantMap, thirdParty.getPhoneMobile());

                    client.put("surname", thirdParty.getLastName());
                    client.put("name", thirdParty.getFirstName());
                    client.put("patronymic", thirdParty.getPatronymic());
                    client.put("dateOfBirth", processDate(thirdParty.getBirthDate()));

                    if (thirdParty.getCitizenship() != null && !thirdParty.getCitizenship().isEmpty()) {
                        client.put("countryId", getCountryIdByAlphaCode(thirdParty.getCitizenship(), login, password));
                    } else {
                        // Российское гражданство по умолчанию #16546
                        client.put("countryId", "1");
                    }
                    client.remove("countryId_EN");
                    client.put("sex", 0);
                    if ("M".equals(thirdParty.getGender())) {
                        client.put("sex", 1);
                    }
                    //client.put("sex", GENDERMAP.getKey(thirdParty.getGender()));

                    client.put("contacts", getContactList((List<Map<String, Object>>) client.get("contacts"), thirdParty, login, password));
                    client.put("documents", getDocumentList((List<Map<String, Object>>) client.get("documents"), thirdParty, login, password));
                    client.put("addresses", getAddressList((List<Map<String, Object>>) client.get("addresses"), thirdParty, login, password));
                    client.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED.getId());
                    Map<String, Object> saveParam = new HashMap<>();
                    saveParam.put("CLIENTMAP", client);
                    saveParam.put("ISCALLFROMGATE", isCallFromGate ? "true" : "false");
                    Long contractsWithPpo = getLongParam(params, "contractsWithPpo");
                    Boolean draftPpoReasonsAreExists = Boolean.parseBoolean(params.getOrDefault("draftPpoReasonsExists", "true").toString());

                    // если есть хоть один контракт с ППО  и при этом нет не обработанных заявок на смену статуса
                    if (contractsWithPpo != null && contractsWithPpo > 0 && !draftPpoReasonsAreExists) {
                        {
                            String currentPpoStatus;
                            if (thirdParty.getASalesServOnline() != null) {
                                currentPpoStatus = thirdParty.getASalesServOnline().value();
                            } else {
                                currentPpoStatus = ASalesServOnlineType.NONE.value();
                            }
                            // если статус поменялся
                            if (!currentPpoStatus.equalsIgnoreCase(previousPpoStatus)) {
                                // если статус пришел как YES_YEMPLOYEE
                                if (currentPpoStatus.equalsIgnoreCase(YES_EMPLOYEE.value())) {
                                    Map<String, Object> notifParams = new HashMap<>();
                                    // ищем уведомления о подключении ППО для профиля
                                    notifParams.put("clientProfileId", clientProfile.get("id"));
                                    notifParams.put("description", PPOONLINE_NOTIFICATION_TYPE_SYSNAME);
                                    List<Map<String, Object>> notifications;
                                    Map<String, Object> notificationRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BClientProfileNotificationLoadByParams", notifParams, isCallFromGate, login, password);
                                    notifications = (List<Map<String, Object>>) notificationRes.get(RESULT);
                                    if (notifications != null && notifications.size() == 0) {
                                        createNewNotification(clientProfileId, PPOONLINE_NOTIFICATION_TYPE_SYSNAME, currentPpoStatus, clientProfileId, isCallFromGate, login, password);
                                    }
                                } else { // поменялся не в YES_EMPLOYEE
                                    createNewNotification(clientProfileId, PPOONLINE_NOTIFICATION_TYPE_SYSNAME, currentPpoStatus, clientProfileId, isCallFromGate, login, password);
                                }
                            }
                        }

                        if (phonesExists(thirdParty, client)) {
                            ASalesServOnlineType sellType = thirdParty.getASalesServOnline();
                            if (sellType != null) {
                                switch (sellType) {
                                    case YES_EMPLOYEE: {
                                        processPhoneNumbers(thirdParty, clientProfile);
                                        break;
                                    }
                                    case YES_CLIENT_CABINET: {
                                        turnOnPPoOnline(clientProfile, YES_CLIENT_CABINET.value());
                                        break;
                                    }
                                    case YES_FROM_REGISTER: {
                                        turnOnPPoOnline(clientProfile, ASalesServOnlineType.YES_FROM_REGISTER.value());
                                        break;
                                    }
                                }
                            } else { // если нет телефонов
                                turnOffPPoOnline(clientProfile);
                            }
                        } else {
                            turnOffPPoOnline(clientProfile);
                        }
                    } else {
                        logger.debug("PPO status has no update iteration because of some reasons: ");
                        logger.debug("\u0009 Contracts with PPO = " + contractsWithPpo + ", must be more than 0;");
                        logger.debug("\u0009 Contracts with draft declaration about client profile ppo status are exists = " + draftPpoReasonsAreExists + ", must be true;");
                    }

                    client = this.callService(THIS_SERVICE_NAME, "dsB2BClientProfileClientSave", saveParam, login, password);
                    break;
                }
            }
        }

        return clientProfile;
    }

    private List<Map<String, Object>> getAddressList(List<Map<String, Object>> addresses, ThirdParty thirdParty, String login, String password) {
        List<Address> addressList = (thirdParty.getListAddress() == null) ? new ArrayList<>() : thirdParty.getListAddress().getAddress();
        if (addresses == null) {
            addresses = new ArrayList<>();
        }

        for (Address address : addressList) {
            Long docTypeId = getAddressTypeIDBySysName(ADDRESSTYPEMAP.getKey(address.getAddressType()), login, password);
            if ((docTypeId.compareTo(1010L) == 0) || (docTypeId.compareTo(-1L) == 0)) {
                // адрес рождения не поддерживается.
                continue;
            }
            Map<String, Object> addressMap = getOrCreateMap(addresses, docTypeId);
            addressMap.put("isPrimary", "1");

            addressMap.put("address", getNotEmptyStr(address.getAddressFull()));
            addressMap.put("address2", getNotEmptyStr(address.getAddressTxt()));
            addressMap.put("region", getNotEmptyStr(address.getDistrict()));
            addressMap.put("city", getNotEmptyStr(address.getTown()));
            addressMap.put("street", getNotEmptyStr(address.getStreet()));
            addressMap.put("house", getNotEmptyStr(address.getStreetNr()));
            addressMap.put("flat", getNotEmptyStr(address.getStreetFlat()));
            addressMap.put("postcode", getNotEmptyStr(address.getPostCode()));

            if (addressMap.get("id") != null) {
                addressMap.remove("regionCode");
                addressMap.remove("cityCode");
                addressMap.remove("streetCode");
                addressMap.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED.getId());
            } else {
                addresses.add(addressMap);
            }

        }

        return addresses;
    }

    private Object getNotEmptyStr(String str) {
        return getNotEmptyStr(str, "-");
    }

    private Object getNotEmptyStr(String str, String defStr) {
        if (str == null || str.isEmpty()) {
            return defStr;
        }
        return str;
    }

    private List<Map<String, Object>> getDocumentList(List<Map<String, Object>> documents, ThirdParty thirdParty, String login, String password) {
        if (documents == null) {
            documents = new ArrayList<>();
        }
        if (thirdParty != null && thirdParty.getDocumentsList() != null && thirdParty.getDocumentsList().getDocument() != null) {
            List<DocumentsType> documentList = thirdParty.getDocumentsList().getDocument();
            for (DocumentsType doc : documentList) {

                Long docTypeId = getDocTypeIDBySysName(DOCUMENTTYPEMAP.getKey(doc.getDocumentType()), login, password);
                if (docTypeId.compareTo(-1L) == 0) {
                    // адрес рождения не поддерживается.
                    continue;
                }
                Map<String, Object> docMap = getOrCreateMap(documents, docTypeId);

// по умолчанию обновление не меняет статус основной документ.
                docMap.put("isPrimary", "1");
                String issuedBy = doc.getDocumentInstitution();
                docMap.put("authority", getNotEmptyStr(doc.getDocumentInstitution()));
                docMap.put("issuerCode", getNotEmptyStr(doc.getDocumentCodeIns(), "000000"));
                docMap.put("no", doc.getDocumentNumber());
                docMap.put("series", doc.getDocumentSeries());
                docMap.put("dateOfIssue", processDate(doc.getDocumentDate()));
                if (docMap.get("id") != null) {
                    docMap.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED.getId());
                } else {
                    documents.add(docMap);
                }
            }
        }
        return documents;
    }

    private Map<String, Object> getOrCreateMap(List<Map<String, Object>> objTypedList, Long objTypeId) {
        if (objTypedList != null) {
            for (Map<String, Object> objMap : objTypedList) {
                if (objMap.get("typeId") != null) {
                    if (objTypeId.compareTo(getLongParam(objMap.get("typeId"))) == 0) {
                        objMap.remove("typeId_EN");
                        return objMap;
                    }
                }
            }
        }
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("typeId", objTypeId);
        res.put("isPrimary", "1");
        return res;
    }

    //TODO: переделать на получение ид из бд. hb_kinddocument
    private Long getDocTypeIDBySysName(String key, String login, String password) {
        if (key == null) {
            return -1L;
        }
        switch (key) {
            case "PassportRF":
                return 1001L;
            case "ForeignPassport":
                return 1004L;
            case "BornCertificate":
                return 1005L;
            case "ResidencePermit":
                return 1012L;
            case "DrivingLicence":
                return 1016L;
            case "MigrationCard":
                return 1018L;
            case "VehiclePassport":
                return 2001L;
            case "VehicleRegistrationCertificate":
                return 2002L;
        }
        return 1001L;
    }

    //TODO: переделать на получение ид из бд. hb_kindaddress
    private Long getAddressTypeIDBySysName(String key, String login, String password) {
        if (key == null) {
            return -1L;
        }
        switch (key) {
            case "RegisterAddress":
                return 1003L;
            case "FactAddress":
                return 1005L;
            case "BankAddress":
                return 1006L;
            case "BornAddress":
                return 1010L;
        }
        return 1003L;
    }

    public static void main(String[] args) {
        System.out.println(normalizePhoneNumber("+7 (910) 964 40-26"));
    }

    /**
     * нормализует телефонный номер до 10 цифр
     *
     * @param phoneNumber
     * @return телефонный номер в формате "9001002030" без 8 без +7
     */
    static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        try {
            String onlyDigits = phoneNumber.replaceAll("[^0-9]+", "");
            return onlyDigits.substring(onlyDigits.length() - 10, onlyDigits.length());
        } catch (IndexOutOfBoundsException e) {
            //logger.error("failed to normalize phoneNumber " + phoneNumber);
            return null;
        }
    }

    private List<Map<String, Object>> getContactList(List<Map<String, Object>> contacts, ThirdParty thirdParty, String login, String password) {
        boolean mobilePhoneAlreadyExists = false;
        boolean workPhoneAlreadyExists = false;
        boolean factPhoneAlreadyExists = false;
        boolean emailAlreadyExists = false;
        if (contacts == null) {
            contacts = new ArrayList<>();
        }

        for (Map<String, Object> curContact : contacts) {
            Map<String, Object> typeId_En = (Map<String, Object>) curContact.get("typeId_EN");
            Map<String, Object> contact = new HashMap<>();
            contact.putAll(curContact);
            if ("MobilePhone".equalsIgnoreCase(getStringParam(typeId_En.get("sysname")))) {
                boolean isNewPhoneNumber =
                        !Objects.equals(
                                normalizePhoneNumber(thirdParty.getPhoneMobile()),
                                getStringParam(contact.get("value"))
                        );

                if (!isNewPhoneNumber) {
                    //thirdParty.getPhoneMobile() != null && !thirdParty.getPhoneMobile().isEmpty() &&
                    //thirdParty.getPhoneMobile().equals(getStringParam(contact.get("value")))
                    mobilePhoneAlreadyExists = true;

                }
//                contact.put("value", thirdParty.getPhoneMobile());
//                contact.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED);
            }
            if ("PersonalEmail".equalsIgnoreCase(getStringParam(typeId_En.get("sysname")))) {
                if (thirdParty.getEmail() != null && !thirdParty.getEmail().isEmpty() &&
                        thirdParty.getEmail().equals(getStringParam(contact.get("value")))) {
//                    contact.put("value", thirdParty.getEmail());
//                    contact.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED);
                    emailAlreadyExists = true;
                }
            }
            if ("FactAddressPhone".equalsIgnoreCase(getStringParam(typeId_En.get("sysname")))) {
                boolean isNewPhoneNumber =
                        !Objects.equals(
                                normalizePhoneNumber(thirdParty.getPhoneHome()),
                                getStringParam(contact.get("value"))
                        );

                if (!isNewPhoneNumber) {
//                    contact.put("value", thirdParty.getPhoneHome());
//                    contact.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED);
                    factPhoneAlreadyExists = true;
                }
            }
            if ("WorkAddressPhone".equalsIgnoreCase(getStringParam(typeId_En.get("sysname")))) {
                boolean isNewPhoneNumber =
                        !Objects.equals(
                                normalizePhoneNumber(thirdParty.getPhoneWorking()),
                                getStringParam(contact.get("value"))
                        );

                if (!isNewPhoneNumber) {
//                    contact.put("value", thirdParty.getPhoneWorking());
//                    contact.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED);
                    workPhoneAlreadyExists = true;
                }
            }
        }
        //TODO: сделать получение typeid по системным именам типов контактов hb_kindcontact.
        // согласно обсуждению с Ольгой Игнатовой от 22 ноября 2017
        // сбсж в ППО предполагают, что пользователь будет указывать примри контак, для получения писем и кодов
        // новые контакты вставлять не меняя существующие, указанные при регистрации примари контакты не трогать.
        if (!mobilePhoneAlreadyExists) {
            // нормализуем номер телефона до 10 цифр и убираем лишние символы
            // это будет работать на форме восстановления пароля т.к. там +7 нарисовано в фт
            String normalizedPhoneNumber = normalizePhoneNumber(thirdParty.getPhoneMobile());
            if (normalizedPhoneNumber == null) {
                logger.warn("failed to normalize phone number " + thirdParty.getPhoneMobile());
            }
            if (normalizedPhoneNumber != null) {

                unprimaryContact("MobilePhone", contacts);

                Map<String, Object> contactMap = new HashMap<>();
                contactMap.put("typeId", 1005);
                contactMap.put("isPrimary", 1);
                contactMap.put("value", normalizedPhoneNumber);
                contacts.add(contactMap);
            }
        }
        if (!emailAlreadyExists) {
            if (thirdParty.getEmail() != null && !thirdParty.getEmail().isEmpty()) {
                unprimaryContact("PersonalEmail", contacts);

                Map<String, Object> contactMap = new HashMap<>();
                contactMap.put("typeId", 1006);
                contactMap.put("isPrimary", 1);
                contactMap.put("value", thirdParty.getEmail());
                contacts.add(contactMap);
            }
        }
        if (!factPhoneAlreadyExists) {
            String normalizedPhoneNumber = normalizePhoneNumber(thirdParty.getPhoneHome());
            if (normalizedPhoneNumber == null) {
                logger.warn("failed to normalize phone number " + thirdParty.getPhoneHome());
            }
            if (normalizedPhoneNumber != null) {
                Map<String, Object> contactMap = new HashMap<>();
                contactMap.put("typeId", 1003);
                contactMap.put("value", normalizedPhoneNumber);
                contacts.add(contactMap);
            }
        }
        if (!workPhoneAlreadyExists) {
            String normalizedPhoneNumber = normalizePhoneNumber(thirdParty.getPhoneWorking());
            if (normalizedPhoneNumber == null) {
                logger.warn("failed to normalize phone number " + thirdParty.getPhoneWorking());
            }
            if (normalizedPhoneNumber != null) {
                Map<String, Object> contactMap = new HashMap<>();
                contactMap.put("typeId", 1004);
                contactMap.put("value", normalizedPhoneNumber);
                contacts.add(contactMap);
            }
        }
        return contacts;
    }

    private void unprimaryContact(String typeContact, List<Map<String, Object>> contacts) {
        for (Map<String, Object> curContact : contacts) {
            Map<String, Object> typeId_En = (Map<String, Object>) curContact.get("typeId_EN");
            if (typeId_En != null) {
                Map<String, Object> contact = new HashMap<>();
                contact.putAll(curContact);
                if (typeContact.equalsIgnoreCase(getStringParam(typeId_En.get("sysname")))) {
                    curContact.put("isPrimary", 0);
                    curContact.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED.getId());
                }
            }
        }
    }

    private Map<String, Object> createNewNotification(Long cpi, String description, String text, Long object, Boolean icfg, String login, String password) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("clientProfileId", cpi);
        params.put("objectId", object);
        params.put("text", text);
        params.put("description", description);
        Map<String, Object> res;
        Map<String, Object> notification = new HashMap<>();
        notification.put("NOTIFICATIONMAP", params);
        try {
            res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BClientProfileNotificationCreate", notification, icfg, login, password);
            Map<String, Object> n = (Map<String, Object>) res.get(RESULT);
            return n;
        } catch (Exception ex) {
            return null;
        }
    }
}
