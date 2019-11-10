/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.integration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.log4j.Logger; // import java.util.logging.Logger
import javax.xml.datatype.XMLGregorianCalendar;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.sberbankins.schema.online11.PaymentType;
import ru.sberbankins.schema.online11.AddressType;
import ru.sberbankins.schema.online11.AgentType;
import ru.sberbankins.schema.online11.AgentsType;
import ru.sberbankins.schema.online11.CommissType;
import ru.sberbankins.schema.online11.ContentType;
import ru.sberbankins.schema.online11.ContractType;
import ru.sberbankins.schema.online11.DocumentType;
import ru.sberbankins.schema.online11.EntrepreneurType;
import ru.sberbankins.schema.online11.FacePersonType;
import ru.sberbankins.schema.online11.JuridicalType;
import ru.sberbankins.schema.online11.MemberType;
import ru.sberbankins.schema.online11.MembersType;
import ru.sberbankins.schema.online11.ObjectType;
import ru.sberbankins.schema.online11.ObjectsType;
import ru.sberbankins.schema.online11.Package;
import ru.sberbankins.schema.online11.PaymentsType;
import ru.sberbankins.schema.online11.PhysicalType;
import ru.sberbankins.schema.online11.PlanPaymentScheduleType;
import ru.sberbankins.schema.online11.PlanType;
import ru.sberbankins.schema.online11.PropertiesType;
import ru.sberbankins.schema.online11.PropertyType;
import ru.sberbankins.schema.online11.RiskType;
import ru.sberbankins.schema.online11.RisksType;
import ru.sberbankins.schema.online11.TypeType;
import ru.sberbankins.schema.online11.TypesInsObjectsType;

/**
 *
 * @author averichevsm
 */
@BOName("ContractIntegration")
public class ContractIntegrationFacade extends BaseIntegrationFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsIntegrationGetContractsData(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Package contractPackage = getContractsPackage(params);

        String registryXML = this.marshall(contractPackage);
        result.put("registry", registryXML);
        return result;
    }

    private Package getContractsPackage(Map<String, Object> params) {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Package p = new Package();
        List<ContractType> contracts = p.getContracts();
        List<Map<String, Object>> contractList = (List<Map<String, Object>>) params.get("CONTRACTLIST");
        for (Map<String, Object> contract : contractList) {
            ContractType contractType = getContractType(contract, login, password);
            contracts.add(contractType);
        }
        return p;
    }

    private ContractType getContractType(Map<String, Object> contract, String login, String password) {
        ContractType contractType = new ContractType();
        //contractType.setAgents(getAgentsType(contract, login, password));
//        contractType.setCalcCourse(getCalcCourse(contract, login, password));
        contractType.setChannel(getStringParam(contract.get("SALECHANNELCODE")));
        contractType.setCurrency(getStringParam(contract.get("INSAMCURRISO")));
        contractType.setCurrencyClearing(getStringParam(contract.get("PREMIUMCURRISO")));
        contractType.setDateBegin(getDate(contract, "STARTDATE"));
        contractType.setDateCancel(getDate(contract, "CANCELDATE"));
        contractType.setDateEnd(getDate(contract, "FINISHDATE"));
        contractType.setDateSigning(getDateSigningType(contract, login, password));

        contractType.setDepartment(getStringParam(contract.get("DEPNAME")));
        contractType.setFullNumber(getStringParam(contract.get("CONTRNUMBER")));
        contractType.setID(getLongParam(contract.get("CONTRID")));
        contractType.setInsProduct(getStringParam(contract.get("SBERPRODCODE")));
        contractType.setInsRegion(getDepRegionCode(contract, login, password));
        contractType.setInsurer(getFacePersonType(getLongParam(contract.get("INSUREDID")), login, password));
        contractType.setMembers(getMembersType(contract, login, password));
        contractType.setMethodNumbering("automatic");
        contractType.setNumber(getStringParam(contract.get("CONTRPOLNUM")));
        contractType.setOffice(getStringParam(contract.get("DEPNAME")));
        contractType.setPayments(getPaymentsType(contract, login, password));
        contractType.setPeriodicityPayment("00001");
        contractType.setPlanPaymentSchedule(getPlanPaymentScheduleType(contract, login, password));
        contractType.setPrem(getBigDecimalParam(contract.get("PREMIUM")));
        contractType.setProperties(getPropertiesType((List<Map<String, Object>>) contract.get("PROPERTYLIST"), login, password));
        contractType.setTypesInsObjects(getTypesInsObjectsType(contract, login, password));

        return contractType;
    }

    private AgentsType getAgentsType(Map<String, Object> contract, String login, String password) {
        AgentsType agentsType = new AgentsType();
        List<AgentType> agentsTypes = agentsType.getAgents();

        //agentsTypes.add(getAgentType(contract, login, password));
        return agentsType;
    }

    private AgentType getAgentType(Map<String, Object> contract, String login, String password) {
        AgentType agentType = new AgentType();

        agentType.setContent(getContentType(contract, login, password));
        agentType.setNumber("");
        return agentType;
    }

    private ContentType getContentType(Map<String, Object> contract, String login, String password) {
        ContentType contentType = new ContentType();
        List<CommissType> commissTypes = contentType.getCommisses();
        commissTypes.add(getCommissType());
        return contentType;
    }

    private CommissType getCommissType() {
        CommissType commissType = new CommissType();
//        commissType.setStructureInsProduct("");
//        commissType.setValue(BigDecimal.ZERO);
        return commissType;
    }

    /*    private CalcCourseType getCalcCourse(Map<String, Object> contract, String login, String password) {
     CalcCourseType calcCourseType = null;
     //
     if (contract.get("INSAMCURRNAME") != null) {
     // если страховая сумма не в рублях. т.е. договор валютный, то выгружаем тип расчета курса
     if (!"RUB".equalsIgnoreCase(getStringParam(contract.get("INSAMCURRNAME")))) {
     calcCourseType = new CalcCourseType();
     // необходимо в свойствах продукта считать тип расчета курса
     // • CBСourseOnDateSigning – по курсу ЦБ на дату подписания
     // • CBСourseOnDatePay – по курсу ЦБ на дату оплаты
     // • FixedCBСourseOnDate – фиксированный по курсу ЦБ на дату
     // • FixedСourse – фиксированный курс
     // пока не реализовано используется только 1 вид.
     calcCourseType.setType("CBСourseOnDateSigning");
     calcCourseType.setDate(getDate(contract, "SIGNDATE"));
     }
     }
     return calcCourseType;
     }*/
    private XMLGregorianCalendar getDateSigningType(Map<String, Object> contract, String login, String password) {
        XMLGregorianCalendar calendar = null;
        if (contract.get("SIGNDATE") != null) {
            calendar = getDate(contract, "SIGNDATE");
        } else {
            try {
                // если дата подписания в договоре не сохранена, пытаемся получить дату оплаты договора. т.к. момент оплаты и считается подписанием клиента
                calendar = getDateFromPayFact(getLongParam(contract.get("CONTRNODEID")), login, password);
            } catch (Exception ex) {
                logger.error("getDateSigningType error", ex);
            }
        }
        return calendar;
    }

    private String getDepRegionCode(Map<String, Object> contract, String login, String password) {
        // узнать как получать регион страхования
        return "77";
    }

    private FacePersonType getFacePersonType(Long participantId, String login, String password) {
        FacePersonType facePersonType = new FacePersonType();
        Map<String, Object> params = new HashMap();
        params.put(RETURN_AS_HASH_MAP, WsConstants.TRUE);
        params.put("PARTICIPANTID", participantId);

        try {
            Map<String, Object> participantRes = this.callService(CRMWS_SERVICE_NAME, "participantGetByIdFull", params, login, password);
            //facePersonType.setDateCreate(getDate(participantRes, "CREATIONDATE"));
            // заполнитель анкеты
            //facePersonType.setEmployee("");
            facePersonType.setState("new");
            if (participantRes.get("PARTICIPANTTYPE") != null) {
                if ("1".equals(getStringParam(participantRes.get("PARTICIPANTTYPE")))) {
                    // физ лицо
                    boolean isIP = false;
                    if (participantRes.get("ISBUSINESSMAN") != null) {
                        if ("1".equals(getStringParam(participantRes.get("ISBUSINESSMAN")))) {
                            // физ лицо - ИП
                            isIP = true;
                        }
                    }
                    if (isIP) {
                        //физ лицо - ИП
                        facePersonType.setEntrepreneur(getEntrepreneurType(participantRes, login, password));
                    } else {
                        //физ лицо
                        facePersonType.setPhysical(getPhysicalType(participantRes, login, password));
                    }
                }
                if ("2".equals(getStringParam(participantRes.get("PARTICIPANTTYPE")))) {
                    // Юр лицо
                    facePersonType.setJuridical(getJuridicalType(participantRes, login, password));
                }
            }
        } catch (Exception ex) {
            logger.error("getFacePersonType", ex);
        }

        return facePersonType;
    }

    private XMLGregorianCalendar getDateFromPayFact(Long contrNodeId, String login, String password) throws Exception {
        XMLGregorianCalendar calendar = null;
        Map<String, Object> planParams = new HashMap<String, Object>();
        planParams.put("CONTRNODEID", contrNodeId);
        Map<String, Object> qPlanRes = this.callService(INSPOSWS_SERVICE_NAME, "dsPaymentFactBrowseListByParamEx", planParams, login, password);
        List<Map<String, Object>> payList = WsUtils.getListFromResultMap(qPlanRes);
        if (!payList.isEmpty()) {
            CopyUtils.sortByDateFieldName(payList, "PAYFACTDATE");
            Map<String, Object> pay = (Map<String, Object>) payList.get(0);
            if (pay.get("PAYFACTDATE") != null) {
                calendar = getDate(pay, "PAYFACTDATE");
            }
        }
        return calendar;
    }

    private DocumentType findDocBySysName(Map<String, Object> participantRes, String docName, String login, String password) {
        List<Map<String, Object>> documents = WsUtils.getListFromObject(participantRes.get("documentList"));
        for (Map<String, Object> document : documents) {
            Object docTypeSysName = document.get("DOCTYPESYSNAME");
            if (docTypeSysName != null) {
                if (docName.equalsIgnoreCase(getStringParam(docTypeSysName))) {
                    return getDocumentType(document, login, password);
                }
            }
        }
        return null;
    }

    private AddressType findAddrBySysName(Map<String, Object> participantRes, String adrName, String login, String password) {
        List<Map<String, Object>> addresses = WsUtils.getListFromObject(participantRes.get("addressList"));
        for (Map<String, Object> address : addresses) {
            Object addressTypeSysName = address.get("ADDRESSTYPESYSNAME");
            if (addressTypeSysName != null) {
                if (adrName.equalsIgnoreCase(getStringParam(addressTypeSysName))) {
                    return getAddressType(address, login, password);
                }
            }
        }
        return null;
    }

    private EntrepreneurType getEntrepreneurType(Map<String, Object> participantRes, String login, String password) {
        EntrepreneurType entrepreneurType = new EntrepreneurType();
        //согласие на обращение в бюро кредитных историй
        entrepreneurType.setAcceptOfAccessToCHB(false);

        // адреса ИП
        //List<Map<String, Object>> addressList = WsUtils.getListFromObject(participantRes.get("addressList"));
        //Map<String, Object> addressReg = null;
        //Map<String, Object> addressFact = null;
        //Map<String, Object> addressPost = null;
        //for (Map<String, Object> addressMap : addressList) {
        //    if (addressMap.get("ADDRESSTYPESYSNAME") != null) {
        //        if ("RegisterAddress".equalsIgnoreCase(getStringParam(addressMap.get("ADDRESSTYPESYSNAME")))) {
        //            addressReg = addressMap;
        //        }
        //        if ("FactAddress".equalsIgnoreCase(getStringParam(addressMap.get("ADDRESSTYPESYSNAME")))) {
        //            addressFact = addressMap;
        //        }
        //        if ("PostAddress".equalsIgnoreCase(getStringParam(addressMap.get("ADDRESSTYPESYSNAME")))) {
        //            addressPost = addressMap;
        //        }
        //    }
        //}        
        //entrepreneurType.setAddress(getAddressType(addressReg, login, password));
        //entrepreneurType.setLegalAddress(getAddressType(addressFact, login, password));
        //entrepreneurType.setPostalAddress(getAddressType(addressPost, login, password).getAddressString());
        //
        // адреса ИП
        entrepreneurType.setAddress(findAddrBySysName(participantRes, "RegisterAddress", login, password));
        entrepreneurType.setLegalAddress(findAddrBySysName(participantRes, "FactAddress", login, password));
        entrepreneurType.setPostalAddress(findAddrBySysName(participantRes, "PostAddress", login, password).getAddressString());

        String country = getCitizenship(participantRes, login, password);
        entrepreneurType.setContry(country);

        // документы ИП
        //List<Map<String, Object>> documentList = WsUtils.getListFromObject(participantRes.get("documentList"));
        //Map<String, Object> docPasport = null;
        //Map<String, Object> docOnResidence = null;        
        //Map<String, Object> migrationCard = null;
        //for (Map<String, Object> documentMap : documentList) {
        //    if (documentMap.get("DOCTYPESYSNAME") != null) {
        //        if ("PassportRF".equalsIgnoreCase(getStringParam(documentMap.get("DOCTYPESYSNAME")))) {
        //            docPasport = documentMap;
        //        }
        //        // разрешение на временное проживание
        //        if ("ResidencePermit".equalsIgnoreCase(getStringParam(documentMap.get("DOCTYPESYSNAME")))) {
        //            docOnResidence = documentMap;
        //        }
        //        if ("MigrationCard".equalsIgnoreCase(getStringParam(documentMap.get("DOCTYPESYSNAME")))) {
        //            migrationCard = documentMap;
        //        }
        //    }
        //}
        //entrepreneurType.setDocument(getDocumentType(docPasport, login, password));        
        //entrepreneurType.setDocumentOnResidence(getDocumentType(docOnResidence, login, password));        
        //entrepreneurType.setMigrationCard(getDocumentType(migrationCard, login, password));        
        //
        // документы ИП
        entrepreneurType.setDocument(findDocBySysName(participantRes, "PassportRF", login, password));
        entrepreneurType.setDocumentOnResidence(findDocBySysName(participantRes, "ResidencePermit", login, password));
        entrepreneurType.setMigrationCard(findDocBySysName(participantRes, "MigrationCard", login, password));

        // документы ИП
        List<Map<String, Object>> documentRegList = WsUtils.getListFromObject(participantRes.get("partRegDocList"));
        Map<String, Object> docRegistration = null;
        for (Map<String, Object> documentRegMap : documentRegList) {
            if (documentRegMap.get("DOCTYPESYSNAME") != null) {
                if ("OGRN".equalsIgnoreCase(getStringParam(documentRegMap.get("DOCTYPESYSNAME")))) {
                    docRegistration = documentRegMap;
                }
            }
        }
        entrepreneurType.setDocumentOfRegistration(getDocumentType(docRegistration, login, password));

        List<Map<String, Object>> contactList = WsUtils.getListFromObject(participantRes.get("contactList"));
        String email = "";
        String mobilePhone = "";
        String homePhone = "";
        String workPhone = "";
        for (Map<String, Object> contact : contactList) {
            if (contact.get("CONTACTTYPESYSNAME") != null) {
                if ("PersonalEmail".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    email = getStringParam(contact.get("VALUE"));
                }
                if ("MobilePhone".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    mobilePhone = getStringParam(contact.get("VALUE"));
                }
                if ("FactAddressPhone".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    homePhone = getStringParam(contact.get("VALUE"));
                }
                if ("WorkAddressPhone".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    workPhone = getStringParam(contact.get("VALUE"));
                }
            }
        }

        entrepreneurType.setEmail(email);
        if (!workPhone.isEmpty()) {
            entrepreneurType.setTel(workPhone);
        } else {
            if (!homePhone.isEmpty()) {
                entrepreneurType.setTel(homePhone);
            } else {
                if (!mobilePhone.isEmpty()) {
                    entrepreneurType.setTel(mobilePhone);
                }
            }
        }

        entrepreneurType.setFormOfIncorporation(getStringParam(participantRes.get("OPF")));
        entrepreneurType.setINN(getStringParam(participantRes.get("INN")));
        entrepreneurType.setName(getStringParam(participantRes.get("FIRSTNAME")));
        entrepreneurType.setSurname(getStringParam(participantRes.get("LASTNAME")));
        entrepreneurType.setPatronymic(getStringParam(participantRes.get("MIDDLENAME")));
        String sex = "male";
        if (participantRes.get("GENDER") != null) {
            if ("1".equals(getStringParam(participantRes.get("GENDER")))) {
                sex = "female";
            }
        }
        entrepreneurType.setSex(sex);
        entrepreneurType.setDateOfBirth(getDate(participantRes, "BIRTHDATE", 0));
        entrepreneurType.setPlaceOfBirth(getStringParam(participantRes.get("BIRTHPLACE")));
        // сейчас хранение нигде не предусмотрено, как сделаем сохранение, так необходимо будет доработать выгрузку
        entrepreneurType.setOGRNIP("");
        return entrepreneurType;
    }

    private String getCitizenship(Map<String, Object> participantRes, String login, String password) {
        Long citizenship = getLongParam(participantRes.get("CITIZENSHIP"));
        String country = "";
        if (citizenship != null) {
            Map<String, Object> qparam = new HashMap<String, Object>();
            qparam.put("COUNTRYID", citizenship);
            qparam.put(RETURN_AS_HASH_MAP, WsConstants.TRUE);
            Map<String, Object> qres = null;
            try {
                qres = this.callService(REFWS_SERVICE_NAME, "findCountryById", qparam, login, password);
            } catch (Exception ex) {
                logger.error("getEntrepreneurType", ex);
            }
            if (qres != null) {
                if (qres.get("ALPHACODE3") != null) {
                    country = getStringParam(qres.get("ALPHACODE3"));
                }
                //if (qres.get("COUNTRYNAME") != null) {
                //    country = getStringParam(qres.get("COUNTRYNAME"));
                //}
            }
        }
        return country;
    }

    private PhysicalType getPhysicalType(Map<String, Object> participantRes, String login, String password) {
        PhysicalType physicalType = new PhysicalType();
        //согласие на обращение в бюро кредитных историй
        physicalType.setAcceptOfAccessToCHB(false);

        // адрес физ. лица
        //List<Map<String, Object>> addressList = WsUtils.getListFromObject(participantRes.get("addressList"));
        //Map<String, Object> addressReg = null;
        //for (Map<String, Object> addressMap : addressList) {
        //    if (addressMap.get("ADDRESSTYPESYSNAME") != null) {
        //        if ("RegisterAddress".equalsIgnoreCase(getStringParam(addressMap.get("ADDRESSTYPESYSNAME")))) {
        //            addressReg = addressMap;
        //        }
        //    }
        //}
        //physicalType.setAddress(getAddressType(addressReg, login, password));
        //
        // адрес физ. лица
        physicalType.setAddress(findAddrBySysName(participantRes, "RegisterAddress", login, password));

        String country = getCitizenship(participantRes, login, password);
        physicalType.setContry("643");

        // документы физ. лица        
        //List<Map<String, Object>> documentList = WsUtils.getListFromObject(participantRes.get("documentList"));
        //Map<String, Object> docPasport = null;
        //Map<String, Object> docOnResidence = null;        
        //Map<String, Object> migrationCard = null;
        //for (Map<String, Object> documentMap : documentList) {
        //    if (documentMap.get("DOCTYPESYSNAME") != null) {
        //        if ("PassportRF".equalsIgnoreCase(getStringParam(documentMap.get("DOCTYPESYSNAME")))) {
        //            docPasport = documentMap;
        //        }
        //        // разрешение на временное проживание
        //        if ("ResidencePermit".equalsIgnoreCase(getStringParam(documentMap.get("DOCTYPESYSNAME")))) {
        //            docOnResidence = documentMap;
        //        }
        //        if ("MigrationCard".equalsIgnoreCase(getStringParam(documentMap.get("DOCTYPESYSNAME")))) {
        //            migrationCard = documentMap;
        //        }
        //    }
        //}
        //physicalType.setDocument(getDocumentType(docPasport, login, password));        
        //physicalType.setDocumentOnResidence(getDocumentType(docOnResidence, login, password));        
        //physicalType.setMigrationCard(getDocumentType(migrationCard, login, password));        
        //
        // документы физ. лица
        DocumentType personDoc = findDocBySysName(participantRes, "PassportRF", login, password);
        if (personDoc == null) {
            personDoc = findDocBySysName(participantRes, "ForeignPassport", login, password);
        }
        physicalType.setDocument(personDoc);
        physicalType.setDocumentOnResidence(findDocBySysName(participantRes, "ResidencePermit", login, password));
        physicalType.setMigrationCard(findDocBySysName(participantRes, "MigrationCard", login, password));

        // сейчас эта инфа не сохраняестя, после реализации возможно хранение в core_extAttr в виде типа дипломатического лица (сейчас есть просто флаг isDiplomate)
        List<Map<String, Object>> extAttrList = WsUtils.getListFromObject(participantRes.get("extAttributeList"));
        String isDiplomate = "";
        String Position = "";
        for (Map<String, Object> extAttr : extAttrList) {
            if ("Position".equalsIgnoreCase(getStringParam(extAttr.get("EXTATT_SYSNAME")))) {
                Position = getStringParam(extAttr.get("EXTATTVAL_VALUE"));
            }
            if ("isDiplomat".equalsIgnoreCase(getStringParam(extAttr.get("EXTATT_SYSNAME")))) {
                isDiplomate = getStringParam(extAttr.get("EXTATTVAL_VALUE"));
            }
        }
        /*  physicalType.setPosition(Position);
         if (isDiplomate.isEmpty()) {
         physicalType.setForeignPublicOfficial("");
         } else {
         physicalType.setForeignPublicOfficial("self");
         }
         */
        List<Map<String, Object>> contactList = WsUtils.getListFromObject(participantRes.get("contactList"));
        String email = "";
        String mobilePhone = "";
        String homePhone = "";
        String workPhone = "";
        for (Map<String, Object> contact : contactList) {
            if (contact.get("CONTACTTYPESYSNAME") != null) {
                if ("PersonalEmail".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    email = getStringParam(contact.get("VALUE"));
                }
                if ("MobilePhone".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    mobilePhone = getStringParam(contact.get("VALUE"));
                }
                if ("FactAddressPhone".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    homePhone = getStringParam(contact.get("VALUE"));
                }
                if ("WorkAddressPhone".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    workPhone = getStringParam(contact.get("VALUE"));
                }
            }
        }

        physicalType.setEmail(email);
        physicalType.setWorkPhone(workPhone);
        physicalType.setHomePhone(homePhone);
        physicalType.setMobilePhone(mobilePhone);

        //physicalType.setINN(getStringParam(participantRes.get("INN")));
        physicalType.setName(getStringParam(participantRes.get("FIRSTNAME")));
        physicalType.setSurname(getStringParam(participantRes.get("LASTNAME")));
        physicalType.setPatronymic(getStringParam(participantRes.get("MIDDLENAME")));
        String sex = "male";
        if (participantRes.get("GENDER") != null) {
            if ("1".equals(getStringParam(participantRes.get("GENDER")))) {
                sex = "female";
            }
        }
        physicalType.setSex(sex);
        physicalType.setDateOfBirth(getDate(participantRes, "BIRTHDATE",0));
        physicalType.setPlaceOfBirth(getStringParam(participantRes.get("BIRTHPLACE")));
        // доверенность. пока сохранение доверенностей не предусмотерно.
        // по идее должны лежать в CRM_LAWRELSHIPDOC
        physicalType.setPowerOfAttorney(null);

        return physicalType;
    }

    private JuridicalType getJuridicalType(Map<String, Object> participantRes, String login, String password) {
        JuridicalType juridicalType = new JuridicalType();
        //согласие на обращение в бюро кредитных историй
        List<Map<String, Object>> addressList = WsUtils.getListFromObject(participantRes.get("addressList"));
        Map<String, Object> addressFact = null;
        Map<String, Object> addressPost = null;
        for (Map<String, Object> addressMap : addressList) {
            if (addressMap.get("ADDRESSTYPESYSNAME") != null) {
                if ("JuridicalAddress".equalsIgnoreCase(getStringParam(addressMap.get("ADDRESSTYPESYSNAME")))) {
                    addressFact = addressMap;
                }
                if ("PostAddress".equalsIgnoreCase(getStringParam(addressMap.get("ADDRESSTYPESYSNAME")))) {
                    addressPost = addressMap;
                }
            }
        }

        juridicalType.setTelegraphAddress(null);
        juridicalType.setLegalAddress(getAddressType(addressFact, login, password));
        juridicalType.setPostalAddress(getAddressType(addressPost, login, password).getAddressString());

        // документы юр. лица
        List<Map<String, Object>> documentRegList = WsUtils.getListFromObject(participantRes.get("partRegDocList"));
        Map<String, Object> docRegistration = null;
        docRegistration = documentRegList.get(0);
        juridicalType.setDocumentOfRegistration(getDocumentType(docRegistration, login, password));

        List<Map<String, Object>> contactList = WsUtils.getListFromObject(participantRes.get("contactList"));
        String email = "";
        String mobilePhone = "";
        String homePhone = "";
        String workPhone = "";
        String fax = "";
        String teletype = "";
        String telex = "";
        for (Map<String, Object> contact : contactList) {
            if (contact.get("CONTACTTYPESYSNAME") != null) {
                if ("PersonalEmail".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    email = getStringParam(contact.get("VALUE"));
                }
                if ("MobilePhone".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    mobilePhone = getStringParam(contact.get("VALUE"));
                }
                if ("FactAddressPhone".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    homePhone = getStringParam(contact.get("VALUE"));
                }
                if ("WorkAddressPhone".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    workPhone = getStringParam(contact.get("VALUE"));
                }
                if ("FaxNumber".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    fax = getStringParam(contact.get("VALUE"));
                }
                if ("Teletype".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    teletype = getStringParam(contact.get("VALUE"));
                }
                if ("Telex".equalsIgnoreCase(getStringParam(contact.get("CONTACTTYPESYSNAME")))) {
                    telex = getStringParam(contact.get("VALUE"));
                }
            }
        }

        juridicalType.setEmail(email);
        if (!workPhone.isEmpty()) {
            juridicalType.setTel(workPhone);
        } else {
            if (!homePhone.isEmpty()) {
                juridicalType.setTel(homePhone);
            } else {
                if (!mobilePhone.isEmpty()) {
                    juridicalType.setTel(mobilePhone);
                }
            }
        }
        juridicalType.setFax(fax);
        juridicalType.setTeletype(teletype);
        juridicalType.setTelex(telex);

        // сейчас эта инфа не сохраняестя, после реализации возможно хранение в core_extAttr в виде типа дипломатического лица (сейчас есть просто флаг isDiplomate)
        List<Map<String, Object>> extAttrList = WsUtils.getListFromObject(participantRes.get("extAttributeList"));
        String name2eng = "";
        String OGRN = "";
        for (Map<String, Object> extAttr : extAttrList) {
            if ("EngName".equalsIgnoreCase(getStringParam(extAttr.get("EXTATT_SYSNAME")))) {
                name2eng = getStringParam(extAttr.get("EXTATTVAL_VALUE"));
            }
            if ("OGRN".equalsIgnoreCase(getStringParam(extAttr.get("EXTATT_SYSNAME")))) {
                OGRN = getStringParam(extAttr.get("EXTATTVAL_VALUE"));
            }
        }

        juridicalType.setFormOfIncorporation(getStringParam(participantRes.get("OPF")));
        juridicalType.setINN(getStringParam(participantRes.get("INN")));
        juridicalType.setName(getStringParam(participantRes.get("FULLNAME")));
        juridicalType.setName2(name2eng);
        juridicalType.setOGRN(OGRN);

        List<Map<String, Object>> codeList = WsUtils.getListFromObject(participantRes.get("partCodeList"));
        String kpp = "";
        for (Map<String, Object> codeMap : codeList) {
            if ("KPPCODE".equalsIgnoreCase(getStringParam(codeMap.get("PARTCODETYPESYSNAME")))) {
                kpp = getStringParam(codeMap.get("CODEVALUE"));
            }

        }

        juridicalType.setKPP(kpp);

        return juridicalType;
    }

    private AddressType getAddressType(Map<String, Object> addressMap, String login, String password) {
        AddressType addressType = new AddressType();
        //addressType.setAddressString(getStringParam(addressMap.get("ADDRESSTEXT2")));
        addressType.setBuilding(getStringParam(addressMap.get("BUILDING")));
        //addressType.setContry(getStringParam(addressMap.get("COUNTRY")));
        addressType.setContry("643");
        addressType.setFlat(getStringParam(addressMap.get("FLAT")));
        addressType.setHouse(getStringParam(addressMap.get("HOUSE")));
        addressType.setHousing(getStringParam(addressMap.get("HOUSING")));
        String regionCode = getStringParam(addressMap.get("REGIONKLADR"));
        String cityCode = getStringParam(addressMap.get("CITYKLADR"));
        String districtCode = getStringParam(addressMap.get("DISTRICTKLADR"));
        String villageCode = getStringParam(addressMap.get("VILLAGEKLADR"));
        String fullCode = villageCode;
        if (fullCode.isEmpty()) {
            fullCode = districtCode;
        }
        if (fullCode.isEmpty()) {
            fullCode = cityCode;
        }
        if (fullCode.isEmpty()) {
            fullCode = regionCode;
        }
        if (fullCode.isEmpty()) {
            return null;
        }
        addressType.setLocalityCode(fullCode);
        if (addressMap.get("STREETKLADR") != null) {
            addressType.setStreet(getStringParam(addressMap.get("STREETTYPE_SHORT")) + " " + getStringParam(addressMap.get("STREET")));
        } else {
            addressType.setStreet(getStringParam(addressMap.get("STREET")));
        }
        //addressType.setStreetCode(getStringParam(addressMap.get("STREETKLADR")));
        addressType.setZipCode(getStringParam(addressMap.get("POSTALCODE")));
        return addressType;
    }

    private DocumentType getDocumentType(Map<String, Object> docMap, String login, String password) {
        DocumentType documentType = new DocumentType();
        if (docMap != null) {
            documentType.setAuthority(getStringParam(docMap.get("ISSUEDBY")));
            documentType.setCode(getStringParam(docMap.get("ISSUERCODE")));
            documentType.setDateOfIssue(getDate(docMap, "ISSUEDATE",0));
            documentType.setInfo(getStringParam(docMap.get("DESCRIPTION")));
            documentType.setKind("21");
            documentType.setNumber(getStringParam(docMap.get("DOCNUMBER")));
            documentType.setSeries(getStringParam(docMap.get("DOCSERIES")));
        }
        return documentType;
    }

    private MembersType getMembersType(Map<String, Object> contract, String login, String password) {
        MembersType membersType = null;
        List<MemberType> memberTypeList = null;//membersType.getMembers();
        // в далеком будущем в новом Б2Б у продукта будет описание типов страховых объектов, и определять есть в нем застрахованные или иные
        // лица можно будет по описанию.
        if ("VZR".equalsIgnoreCase(getStringParam(contract.get("PRODUCTSYSNAME")))) {
            if (contract.get("INSUREDLIST") != null) {
                List<Map<String, Object>> insuredList = WsUtils.getListFromObject(contract.get("INSUREDLIST"));
                for (Map<String, Object> insured : insuredList) {
                    if (membersType == null) {
                        membersType = new MembersType();
                        memberTypeList = membersType.getMembers();
                    }
                    memberTypeList.add(getMemberType(insured, "insured", login, password));
                }
            }
        }
        // если в договоре есть застрахованные, то получить их список и добавить каждого
        return membersType;
    }

    private MemberType getMemberType(Map<String, Object> member, String memberKind, String login, String password) {
        MemberType memberType = new MemberType();
        memberType.setKind(memberKind);
        memberType.setName(getStringParam(member.get("FIRSTNAME")));
        memberType.setSurname(getStringParam(member.get("LASTNAME")));
        memberType.setPatronymic(getStringParam(member.get("MIDDLENAME")));

        String sex = "male";
        if (member.get("GENDER") != null) {
            if ("1".equals(getStringParam(member.get("GENDER")))) {
                sex = "female";
            }
        }
        memberType.setSex(sex);
        memberType.setDateOfBirth(getDate(member, "BIRTHDATE",0));

        // в застрахованном сейчас нет ссылки на crm соответственно нет документа и лица
        //memberType.setDocument(memberKind);
        //Long participantId = null;
        //memberType.setFace(getFacePersonType(participantId, login, password));
        //
        return memberType;
    }

    private PaymentsType getPaymentsType(Map<String, Object> contract, String login, String password) {
        PaymentsType paymentsType = new PaymentsType();
        List<PaymentType> paymentTypeList = paymentsType.getPayments();
        // получить список платежей и добавить каждый
        if (contract.get("PAYMENTLIST") != null) {
            List<Map<String, Object>> paymentList = WsUtils.getListFromObject(contract.get("PAYMENTLIST"));
            for (Map<String, Object> payment : paymentList) {
                payment.put("CURRENCYRATE", contract.get("CURRENCYRATE"));
                paymentTypeList.add(getPaymentType(payment, login, password));
            }
        }
        return paymentsType;
    }


    private PaymentType getPaymentType(Map<String, Object> payment, String login, String password) {
        PaymentType paymentType = new PaymentType();
        paymentType.setAmount(getBigDecimalParam(payment.get("AMVALUE")));
        if ("1".equalsIgnoreCase(getStringParam(payment.get("AMCURRENCYID")))) {
            // платеж в рублях
            paymentType.setAmountRUB(getBigDecimalParam(payment.get("AMVALUE")));
            if (payment.get("CURRENCYRATE") != null) {
                if (getBigDecimalParamNoScale(payment.get("CURRENCYRATE")).compareTo(BigDecimal.ONE) > 0) {
                    paymentType.setAmount(getBigDecimalParam(payment.get("AMVALUE")).divide(getBigDecimalParamNoScale(payment.get("CURRENCYRATE")), 2, RoundingMode.HALF_UP));
                }
            }
        } else {
            // платеж в валюте
            paymentType.setAmount(getBigDecimalParam(payment.get("AMVALUE")));
            if (payment.get("CURRENCYRATE") != null) {
                if (getBigDecimalParamNoScale(payment.get("CURRENCYRATE")).compareTo(BigDecimal.ONE) > 0) {
                    BigDecimal value = getBigDecimalParam(payment.get("AMVALUE")).multiply(getBigDecimalParamNoScale(payment.get("CURRENCYRATE")));
                    value.setScale(2, RoundingMode.HALF_UP);
                    paymentType.setAmountRUB(value);
                }
            }
        }
        //paymentType.setAmount(getBigDecimalParam(payment.get("AMVALUE")));

        paymentType.setDate(getDate(payment, "PAYFACTDATE"));
        paymentType.setIDTransaction(getStringParam(payment.get("PAYFACTNUMBER")));
        // ссылка на справочник "Способ оплаты" еще не реализовано возможно хранить в payfactType и разыменовывать.
        if ("1".equalsIgnoreCase(getStringParam(payment.get("PAYFACTTYPE")))) {
            paymentType.setPaymentMethod("00001");
        } else {
            paymentType.setPaymentMethod("00001");
        }

        return paymentType;
    }

    private PlanPaymentScheduleType getPlanPaymentScheduleType(Map<String, Object> contract, String login, String password) {
        PlanPaymentScheduleType paymentScheduleType = new PlanPaymentScheduleType();
        List<PlanType> planTypeList = paymentScheduleType.getPlen();

        // если в договоре присутствует план платежей - добавить его
        if (contract.get("PAYMENTSCHEDULELIST") != null) {
            List<Map<String, Object>> paymentList = WsUtils.getListFromObject(contract.get("PAYMENTSCHEDULELIST"));
            for (Map<String, Object> payment : paymentList) {
                planTypeList.add(getPlanType(payment, login, password));
            }
        }
        return paymentScheduleType;
    }

    private PlanType getPlanType(Map<String, Object> payment, String login, String password) {
        PlanType planType = new PlanType();
        planType.setAmount(getBigDecimalParam(payment.get("AMOUNT")));
        planType.setDate(getDate(payment, "PAYDATE"));
        return planType;
    }

    private PropertiesType getPropertiesType(List<Map<String, Object>> propList, String login, String password) {
        PropertiesType propertiesType = new PropertiesType();
        if (propList != null) {
            List<PropertyType> propertyTypeList = propertiesType.getProperties();
            for (Map<String, Object> prop : propList) {
                propertyTypeList.add(getPropertyType(getStringParam(prop.get("SYSNAME")), getLongParam(prop.get("ID")), getStringParam(prop.get("TYPESYSNAME")), prop.get("VALUE"), getBooleanParam(prop.get("ISMANUAL")), login, password));
            }
        }
        return propertiesType;
    }

    private PropertyType getPropertyType(String alias, Long id, String typeSysName, Object value, boolean isManual, String login, String password) {
        PropertyType propertyType = new PropertyType();
        propertyType.setID(id);
        propertyType.setAlias(alias);
        //propertyType.setIsManually(isManual);
        if ("String".equalsIgnoreCase(typeSysName)) {
            propertyType.setString(getStringParam(value));
        }
        if ("Integer".equalsIgnoreCase(typeSysName)) {
            propertyType.setInteger(getIntegerParam(value));
        }
        if ("Float".equalsIgnoreCase(typeSysName)) {
            propertyType.setFloat(getBigDecimalParam(value));
        }
        if ("Date".equalsIgnoreCase(typeSysName)) {
            try {
                propertyType.setDate(dateToXMLGC(getDateParam(value)));
            } catch (Exception ex) {
                logger.error("getPropertyType Date " + alias, ex);
            }
        }
        if ("Boolean".equalsIgnoreCase(typeSysName)) {
            propertyType.setBoolean(Boolean.valueOf(getStringParam(value)));
        }
        if ("Link".equalsIgnoreCase(typeSysName)) {
            propertyType.setLink(getStringParam(value));
        }
        return propertyType;
    }

    private TypesInsObjectsType getTypesInsObjectsType(Map<String, Object> contract, String login, String password) {
        TypesInsObjectsType insObjectsType = new TypesInsObjectsType();
        List<TypeType> typeTypes = insObjectsType.getTypes();
        //в продукте договора могут быть поддержаны различные типы страховых объектов в каждом типе могут быть объекты
        typeTypes.add(getTypeType(contract, login, password));
        return insObjectsType;
    }

    private TypeType getTypeType(Map<String, Object> contract, String login, String password) {
        TypeType tt = new TypeType();
        // ид типа объектов

        tt.setBase(getLongParam(contract.get("CONTRID")));
        tt.setDateBegin(getDate(contract, "STARTDATE"));
        tt.setDateEnd(getDate(contract, "FINISHDATE"));
        tt.setObjects(getObjectsType(contract, login, password));
        // пройтись по объектам, посчитать премию
        tt.setPrem(getBigDecimalParam(contract.get("PREMIUM")));
        // передать свойства типа объекта
        tt.setProperties(getPropertiesType(null, login, password));
        // системное наименование вида типа объекта страхования из справочника
        tt.setStructureInsProduct("00000");
        return tt;
    }

    private ObjectsType getObjectsType(Map<String, Object> contract, String login, String password) {
        ObjectsType objectsType = new ObjectsType();
        List<ObjectType> objectTypeList = objectsType.getObjects();
        List<Map<String, Object>> riskList = (List<Map<String, Object>>) contract.get("RISKLIST");
        if (contract.get("CONTROBJLIST") != null) {
            List<Map<String, Object>> contrObjList = WsUtils.getListFromObject(contract.get("CONTROBJLIST"));
            for (Map<String, Object> contrObj : contrObjList) {
                if (contrObj.get("RISKLIST") == null) {
                    contrObj.put("RISKLIST", riskList);
                }
                objectTypeList.add(getObjectType(contract, contrObj, login, password));
            }
        }
        return objectsType;
    }

    private ObjectType getObjectType(Map<String, Object> contract, Map<String, Object> contrObj, String login, String password) {
        ObjectType objectType = new ObjectType();
        objectType.setBase(getLongParam(contract.get("CONTRID")));
        objectType.setDateBegin(getDate(contract, "STARTDATE"));
        objectType.setDateEnd(getDate(contract, "FINISHDATE"));
        objectType.setPrem(getBigDecimalParam(contrObj.get("PREMVALUE")));
        //свойства - возможно расширенные атрибуты
        objectType.setProperties(getPropertiesType(null, login, password));
        objectType.setRisks(getRisksType(contract, contrObj, login, password));
        objectType.setStructureInsProduct(getStringParam(contrObj.get("STRUCTUREINSPROD")));
        return objectType;
    }

    private RisksType getRisksType(Map<String, Object> contract, Map<String, Object> contrObj, String login, String password) {
        RisksType risksType = new RisksType();
        List<RiskType> riskTypeList = risksType.getRisks();
        List<Map<String, Object>> riskList = WsUtils.getListFromObject(contrObj.get("RISKLIST"));
        for (Map<String, Object> risk : riskList) {
            riskTypeList.add(getRiskType(contract, risk, login, password));
        }
        return risksType;
    }

    private RiskType getRiskType(Map<String, Object> contract, Map<String, Object> risk, String login, String password) {
        RiskType riskType = new RiskType();
        riskType.setBase(getLongParam(contract.get("CONTRID")));
        riskType.setDateBegin(getDate(risk, "STARTDATE"));
        riskType.setDateEnd(getDate(risk, "FINISHDATE"));
        // некая франшиза
        riskType.setDeductible(BigDecimal.ZERO);
        riskType.setPrem(getBigDecimalParam(risk.get("PREMVALUE")));
        riskType.setProperties(getPropertiesType(null, login, password));
        riskType.setStructureInsProduct(getStringParam(risk.get("STRUCTUREINSPROD")));
        riskType.setSum(getBigDecimalParam(risk.get("INSAMVALUE")));
        return riskType;
    }
}
