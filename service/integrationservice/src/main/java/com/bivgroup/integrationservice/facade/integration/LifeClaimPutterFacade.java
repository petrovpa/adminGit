package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;

import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.sberinsur.esb.partner.shema.*;

import java.util.*;

import ru.diasoft.services.inscore.system.WsUtils;

@BOName("LifeClaimPutter")
public class LifeClaimPutterFacade extends IntegrationBaseFacade {
    public static final int B2B_LOSSNOTICE_SENDING = 8501;
    public static final int B2B_LOSSNOTICE_SENDED = 8502;
    private static final String LOSS_NOTICE_MAP_PARAMNAME = "LOSSNOTICE" + "MAP";
    private static final String LOSS_NOTICE_ID_PARAMNAME = "lossNoticeId";
    private static final String LOSS_NOTICE_STATEID_PARAMNAME = "stateId";
    private static final String LOSS_NOTICE_DOCFOLDER1C_PARAMNAME = "docFolder1C";
    private static final String LOSS_NOTICE_EXTERNAL_ID_PARAMNAME = "externalId";


    private void processClaim(Map<String, Object> lossNoticeMap, Map<String, Object> params, String login, String password) throws Exception {
        ClaimImportType cit = new ClaimImportType();
        // todo: сохранить уведомление о событии в журнал уведомлений.


        try {
            mappingClaim(lossNoticeMap, params, cit, login, password);
            Map<String, Object> requestqueueMap = (Map<String, Object>) lossNoticeMap.remove("REQUESTQUEUEMAP");
            AnswerImportListType resContrList = callLifePartnerPutClaim(cit);
            List<AnswerImportType> ccList = resContrList.getAnswerImport();
            String request = this.marshall(cit, ClaimImportType.class);
            String response = this.marshall(resContrList, AnswerImportListType.class);
            boolean hasError = false;
            for (AnswerImportType item : ccList) {
                if (item.getStatus() == StatusIntType.SUCCESS) {
                    //makeTrans to 8502 state
                    Map<String, Object> lossNoticeSaveParams = new HashMap<String, Object>();
                    lossNoticeMap.put(LOSS_NOTICE_STATEID_PARAMNAME, B2B_LOSSNOTICE_SENDED);
                    lossNoticeMap.put(LOSS_NOTICE_EXTERNAL_ID_PARAMNAME, item.getClaimId());
                    lossNoticeMap.put(LOSS_NOTICE_DOCFOLDER1C_PARAMNAME, item.getDocFolder1C());
                    lossNoticeMap.remove("stateId_EN");
                    lossNoticeMap.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);

                    lossNoticeSaveParams.put(LOSS_NOTICE_MAP_PARAMNAME, lossNoticeMap);
                    lossNoticeSaveParams.put(RETURN_AS_HASH_MAP, true);
                    Map<String, Object> lossNoticeSaved = this.callServiceLogged(B2BPOSWS, "dsB2BLossNoticeSave", lossNoticeSaveParams, login, password);
                } else {
                    hasError = true;
                }

            }

            int statusInQueue = 1000;
            if (hasError) {
                statusInQueue = 404;
            }
            if (requestqueueMap == null) {
                b2bRequestQueueCreate(request, response, PUTCUTCLAIMINFO, statusInQueue, getLongParam(lossNoticeMap.get(LOSS_NOTICE_ID_PARAMNAME)), login, password);
            } else {
                b2bRequestQueueUpdate(requestqueueMap, request, response, statusInQueue, login, password);
            }
        } catch (Exception ex) {
            String request = this.marshall(cit, ClaimImportType.class);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            b2bRequestQueueCreate(request, sw.toString(), PUTCUTCLAIMINFO, 404, login, password);
        }
    }

    @WsMethod()
    public Map<String, Object> dsLifeIntegrationPutClaim(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        try {

            List<Map<String, Object>> lossNoticeList = getLossNoticeList(params, login, password);
            Long packSize = getLongParam(params.get("PACKSIZE"));
            int ps = packSize.intValue();

            int count = 0;

            for (Map<String, Object> lossNoticeMap : lossNoticeList) {
                if (lossNoticeMap.get("REQUESTQUEUEMAP") == null) {
                    Map<String, Object> requestMap = new HashMap<>();
                    requestMap.put("ReturnAsHashMap", "TRUE");
                    requestMap.put("OBJID", lossNoticeMap.get(LOSS_NOTICE_ID_PARAMNAME));
                    requestMap.put("REQUESTTYPEID", PUTCUTCLAIMINFO);
                    Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BRequestQueueBrowseListByParam", requestMap, login, password);
                    if (res != null) {
                        if (res.get("REQUESTSTATEID") != null) {
                            if ("404".equalsIgnoreCase(getStringParam(res.get("REQUESTSTATEID")))) {
                                lossNoticeMap.put("REQUESTQUEUEMAP", res);
                                continue;
                            }else {
                                 if ("1000".equalsIgnoreCase(getStringParam(res.get("REQUESTSTATEID")))) {
                                    lossNoticeMap.put("REQUESTQUEUEMAP", res);
                                    continue;
                                }
                            }
                        }
                    }
                }
                if (count >= ps) {
                    break;
                }
                processClaim(lossNoticeMap, params, login, password);
                count++;

            }
            for (Map<String, Object> lossNoticeMap : lossNoticeList) {
                if (lossNoticeMap.get("REQUESTQUEUEMAP") != null) {
                    Map<String, Object> res = (Map<String, Object>) lossNoticeMap.get("REQUESTQUEUEMAP");
                    if ("404".equalsIgnoreCase(getStringParam(res.get("REQUESTSTATEID")))) {
                        if (count >= ps) {
                            break;
                        }
                        processClaim(lossNoticeMap, params, login, password);
                        count++;
                    }
                }
            }

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


    private List<Map<String, Object>> getLossNoticeList(Map<String, Object> params, String login, String password) throws Exception {
        Map<String, Object> searchParam = new HashMap<>();
        searchParam.put(LOSS_NOTICE_STATEID_PARAMNAME, B2B_LOSSNOTICE_SENDING);
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BLossNoticeLoadByStateId", searchParam, login, password);
        if (res.get(RESULT) != null) {
            return (List<Map<String, Object>>) res.get(RESULT);
        }
        return null;
    }

    private Map<String, Object> getPayVarIdBySysName(String sysName, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("SYSNAME", sysName);
        params.put("ReturnAsHashMap", sysName);
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BPaymentVariantBrowseListByParam", params, login, password);
        return res;
    }

    private void prodStructProcess(Map<String, Object> contrMap, Map<String, Object> productMap, ContractType contr, String login, String password) {
        List<Map<String, Object>> insObjGroupList = new ArrayList<>();
        if (productMap.get("PRODVER") != null) {
            Map<String, Object> prodverMap = (Map<String, Object>) productMap.get("PRODVER");
            if (prodverMap.get("PRODSTRUCTS") != null) {
                List<Map<String, Object>> prodStrList = (List<Map<String, Object>>) prodverMap.get("PRODSTRUCTS");
                for (Map<String, Object> prodStrMap : prodStrList) {
                    if (prodStrMap != null) {
                        if (prodStrMap.get("DISCRIMINATOR") != null) {
                            if ("2".equals(prodStrMap.get("DISCRIMINATOR").toString())) {
                                // формируем ТОСЫ.
                                Map<String, Object> tosMap = new HashMap<>();
                                tosMap.putAll(prodStrMap);
                                List<Map<String, Object>> insObjList = new ArrayList<>();
                                if (prodStrMap.get("CHILDS") != null) {
                                    List<Map<String, Object>> prodObjList = (List<Map<String, Object>>) prodStrMap.get("CHILDS");
                                    for (Map<String, Object> prodObjMap : prodObjList) {
                                        Map<String, Object> osMap = new HashMap<>();
                                        Map<String, Object> insObjMap = new HashMap<>();
                                        Map<String, Object> contrObjMap = new HashMap<>();
                                        insObjMap.putAll(prodObjMap);

                                        List<Map<String, Object>> riskList = new ArrayList<>();
                                        if (prodObjMap.get("CHILDS") != null) {
                                            List<Map<String, Object>> prodRiskList = (List<Map<String, Object>>) prodObjMap.get("CHILDS");
                                            for (Map<String, Object> prodRiskMap : prodRiskList) {
                                                Map<String, Object> riskMap = new HashMap<>();
                                                riskMap.putAll(prodRiskMap);
                                                String riskSysName = getStringParam(riskMap.get("SYSNAME"));
                                                if (!riskSysName.isEmpty()) {
                                                    ListCoverageType lc = contr.getCoverageList();
                                                    if (lc != null) {

                                                        List<Coverage> cList = lc.getCoverage();
                                                        if (cList != null) {
                                                            for (Coverage coverage : cList) {
                                                                if (riskSysName.equalsIgnoreCase(coverage.getCoverageName())) {
                                                                    riskMap.put("STARTDATE", processDate(coverage.getPaymentStartDate()));
                                                                    riskMap.put("FINISHDATE", processDate(coverage.getPaymentEndDate()));
                                                                    if (coverage.getCoverageDetList() != null) {
                                                                        if (coverage.getCoverageDetList().getCoverageDet() != null) {
                                                                            if (!coverage.getCoverageDetList().getCoverageDet().isEmpty()) {
                                                                                if (coverage.getCoverageDetList().getCoverageDet().get(0).getCurrency() != null) {
                                                                                    riskMap.put("CURRENCYID", CURRENCYMAP.getKey(coverage.getCoverageDetList().getCoverageDet().get(0).getCurrency().value()));
                                                                                }
                                                                                if (coverage.getCoverageDetList().getCoverageDet().get(0).getAmountPrem() != null) {
                                                                                    riskMap.put("PREMVALUE", coverage.getCoverageDetList().getCoverageDet().get(0).getAmountPrem().doubleValue());
                                                                                }
                                                                                if (coverage.getCoverageDetList().getCoverageDet().get(0).getAmountPrem() != null) {
                                                                                    riskMap.put("INSAMVALUE", coverage.getCoverageDetList().getCoverageDet().get(0).getAmountAssured().doubleValue());
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                riskList.add(riskMap);
                                            }

                                        }
                                        contrObjMap.put("CONTRRISKLIST", riskList);
                                        osMap.put("INSOBJMAP", insObjMap);
                                        osMap.put("CONTROBJMAP", contrObjMap);

                                        insObjList.add(osMap);
                                    }
                                }
                                tosMap.put("OBJLIST", insObjList);
                                insObjGroupList.add(tosMap);
                            }
                        }
                    }
                }
            }
        }

        contrMap.put("INSOBJGROUPLIST", insObjGroupList);

    }

    private void payScheduleProcess(Map<String, Object> contrMap, ContractType contr, String login, String password) {
        ListPaymentSchedulerType lps = contr.getPaymentSchedulerList();
        if (lps != null) {

            /// вероятно ошибка схемы. судя по именам классов - тут должен быть список.
            List<PaymentSchedulerType> psList = lps.getPaymentScheduler();
            if (psList != null) {
                List<Map<String, Object>> payList = new ArrayList<>();
                for (PaymentSchedulerType paymentSheduler : psList) {
                    //          PaymentScheduler paymentSheduler = lps.getPaymentScheduler();
                    Map<String, Object> payMap = new HashMap<>();
                    if (paymentSheduler.getSchedulerSum() != null) {
                        payMap.put("AMOUNT", paymentSheduler.getSchedulerSum().doubleValue());
                    }
                    if (paymentSheduler.getSchedulerDate() != null) {
                        payMap.put("PAYDATE", processDate(paymentSheduler.getSchedulerDate()));
                    }
                    payList.add(payMap);
                }
                contrMap.put("PAYMENTSCHEDULELIST", payList);
            }
        }

    }

    private void payFactProcess(Map<String, Object> contrMap, ContractType contr, String login, String password) {
        ListPaymentType lp = contr.getPayment();
        if (lp != null) {

            /// вероятно ошибка схемы. судя по именам классов - тут должен быть список.
            List<PaymentType> pList = lp.getPayment();
            if (pList != null) {
                List<Map<String, Object>> payList = new ArrayList<>();
                for (PaymentType payment : pList) {
                    Map<String, Object> payMap = new HashMap<>();
                    if (payment.getAmmount() != null) {
                        payMap.put("AMVALUE", payment.getAmmount().doubleValue());
                    }
                    if (payment.getAmmount() != null) {
                        payMap.put("AMCURRENCYID", CURRENCYMAP.getKey(payment.getCurrency().value()));
                    }
                    if (payment.getPaymentDate() != null) {
                        payMap.put("PAYFACTDATE", processDate(payment.getPaymentDate()));
                    }
                    if (payment.getPaymentType() != null) {
                        payMap.put("NOTE", payment.getPaymentType().toString());
                    }
                    payList.add(payMap);
                }
                contrMap.put("PAYMENTLIST", payList);
            }
        }
    }

    private void memberProcess(Map<String, Object> contrMap, ContractType contr, String login, String password) {
        ListThirdPartyType ltp = contr.getThirdPartyList();
        if (ltp != null) {
            List<ThirdParty> tpList = ltp.getThirdParty();
            if (tpList != null) {
                List<Map<String, Object>> memberList = new ArrayList<>();
                List<Map<String, Object>> beneficiaryList = new ArrayList<>();
                Map<String, Object> insurerMap = new HashMap<>();
                Map<String, Object> insuredMap = new HashMap<>();
                for (ThirdParty thirdParty : tpList) {
                    if (thirdParty != null) {
                        Map<String, Object> memberMap = new HashMap<>();
                        memberMap.put("TYPESYSNAME", ROLEMAP.getKey(thirdParty.getRole()));
                        Map<String, Object> participantMap = new HashMap<>();

                        processParticipant(participantMap, thirdParty, contr, login, password);

                        memberMap.put("PARTICIPANTMAP", participantMap);
                        if ("beneficiary".equalsIgnoreCase(ROLEMAP.getKey(thirdParty.getRole()))) {
                            Map<String, Object> benMap = new HashMap<>();
                            benMap.put("PART", thirdParty.getSplit());
                            benMap.put("INSCOVERID", thirdParty.getRiskCode());
                            benMap.put("RISKCODE", thirdParty.getRiskCode());
                            //INSCOVERID
                            //     PART   
                            //        TYPEID
                            benMap.put("PARTICIPANTMAP", participantMap);
                            beneficiaryList.add(benMap);
                        }
                        if ("insured".equalsIgnoreCase(ROLEMAP.getKey(thirdParty.getRole()))) {
                            insuredMap.putAll(participantMap);
                        }
                        if ("insurer".equalsIgnoreCase(ROLEMAP.getKey(thirdParty.getRole()))) {
                            insurerMap.putAll(participantMap);
                        }
                        memberList.add(memberMap);
                    }
                }
                contrMap.put("MEMBERLIST", memberList);
                contrMap.put("BENEFICIARYLIST", beneficiaryList);
                contrMap.put("INSURERMAP", insurerMap);
                contrMap.put("INSUREDMAP", insuredMap);

            }
        }
    }

    private void processParticipant(Map<String, Object> participantMap, ThirdParty thirdParty, ContractType contr, String login, String password) {

        participantMap.put("FIRSTNAME", thirdParty.getFirstName());
        participantMap.put("LASTNAME", thirdParty.getLastName());
        participantMap.put("MIDDLENAME", thirdParty.getPatronymic());
        participantMap.put("BRIEFNAME", thirdParty.getFullName());
        participantMap.put("GENDER", GENDERMAP.getKey(thirdParty.getGender()));

        participantMap.put("BIRTHDATE", processDate(thirdParty.getBirthDate()));
        participantMap.put("BIRTHPLACE", thirdParty.getBirthPlace());

        participantMap.put("documentList", processDocument(thirdParty.getDocumentsList()));
        participantMap.put("contactList", processContact(thirdParty));
        participantMap.put("addressList", processAddress(thirdParty.getListAddress()));

        participantMap.put("PARTICIPANTTYPE", THIRDPARTYTYPEMAP.getKey(thirdParty.getThirdPartyType()));

        //todo промапить поля в extAttributeList2
        participantMap.put("MaritalStatus", MARITIALMAP.getKey(thirdParty.getMaritalStatus()));
        participantMap.put("Position", thirdParty.getPosition());
        participantMap.put("EmployerName", thirdParty.getEmployerName());
        participantMap.put("education", thirdParty.getOccupation());
        participantMap.put("activityBusinessKind", thirdParty.getFrameReference());
        participantMap.put("OPF", LEGALFORMTYPEMAP.getKey(thirdParty.getLegalFormText()));
        participantMap.put("CITIZENSHIP", thirdParty.getResident());
        participantMap.put("taxResident", thirdParty.getTaxResident());
        participantMap.put("INN", thirdParty.getTin());
        participantMap.put("OGRN", thirdParty.getOgrn());

    }

    private List<Map<String, Object>> processDocument(DocumentsListType documntsList) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (documntsList != null) {
            List<DocumentsType> dtList = documntsList.getDocument();
            if (dtList != null) {
                for (DocumentsType documentType : dtList) {
                    if (documentType != null) {
                        Map<String, Object> docMap = new HashMap<>();
                        docMap.put("DOCTYPESYSNAME", DOCUMENTTYPEMAP.getKey(documentType.getDocumentType()));
                        docMap.put("DOCSERIES", documentType.getDocumentSeries());
                        docMap.put("DOCNUMBER", documentType.getDocumentNumber());
                        docMap.put("ISSUEDATE", processDate(documentType.getDocumentDate()));
                        docMap.put("ISSUEDBY", documentType.getDocumentInstitution());
                        docMap.put("ISSUERCODE", documentType.getDocumentCodeIns());
                        docMap.put("DESCRIPTION", documentType.getDocumentFull());
                        result.add(docMap);
                    }
                }

            }
        }
        return result;
    }

    private List<Map<String, Object>> processContact(ThirdParty thirdParty) {
        List<Map<String, Object>> result = new ArrayList<>();
        result.add(getContactMap(thirdParty.getEmail(), "PersonalEmail"));
        result.add(getContactMap(thirdParty.getPhoneHome(), "FactAddressPhone"));
        result.add(getContactMap(thirdParty.getPhoneMobile(), "MobilePhone"));
        result.add(getContactMap(thirdParty.getPhoneWorking(), "WorkAddressPhone"));
        return result;
    }

    private List<Map<String, Object>> processAddress(ListAddressType listAddress) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (listAddress != null) {
            List<Address> aList = listAddress.getAddress();
            if (aList != null) {
                for (Address address : aList) {
                    if (address != null) {
                        Map<String, Object> addressMap = new HashMap<>();
                        addressMap.put("ADDRESSTEXT2", address.getAddressFull());
                        addressMap.put("ADDRESSTEXT1", address.getAddressTxt());
                        addressMap.put("ADDRESSTYPESYSNAME", ADDRESSTYPEMAP.getKey(address.getAddressType()));
                        addressMap.put("STREETKLADR", address.getCladrCode());
                        addressMap.put("KLADR", address.getCladrCode());
                        addressMap.put("COUNTRY", address.getCountry());
                        addressMap.put("REGION", address.getArea());
                        addressMap.put("DISTRICT", address.getDistrict());
                        addressMap.put("CITY", address.getTown());
                        addressMap.put("STREET", address.getStreet());
                        addressMap.put("HOUSE", address.getStreetNr());
                        addressMap.put("BUILD", address.getStreetBuild());
                        addressMap.put("FLAT", address.getStreetFlat());
                        addressMap.put("POSTALCODE", address.getPostCode());
                        result.add(addressMap);
                    }
                }
            }
        }
        return result;
    }

    private Map<String, Object> getContactMap(String value, String contactTypeSysName) {
        Map<String, Object> result = new HashMap<>();
        result.put("CONTACTTYPESYSNAME", contactTypeSysName);
        result.put("CONTACTTYPESYSNAME", value);
        return result;
    }

    public static Integer calcYears(Date from, Date to) {
        Integer result = 0;
        if ((from != null) && (to != null)) {
            GregorianCalendar fromG = new GregorianCalendar();
            GregorianCalendar toG = new GregorianCalendar();
            fromG.setTime(from);
            toG.setTime(to);
            result = WsUtils.calcYears(fromG, toG);
        }
        return result;
    }

    private Map<String, Object> getProdverByProdProgSysName(String policyProgram, String login, String password) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("EXTERNALID", policyProgram);
        param.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BProductProgramBrowseListByParam", param, login, password);
        if (res.get("PRODPROGID") != null) {
            return res;
        }
        logger.error("Unknown programm " + policyProgram);
        return null;

    }

    private void mappingClaim(Map<String, Object> lossNoticeMap, Map<String, Object> params, ClaimImportType cit, String login, String password) throws Exception {
        cit.setAddress(getStringParam(lossNoticeMap.get("address")));
        // если в наличии applicantId - загрузить данные с учетки заявителя.
        cit.setApplicantEmail(getStringParam(lossNoticeMap.get("applicantEmail")));
        if (lossNoticeMap.get("applicantName") == null) {
            Map<String, Object> applicantMap = (Map<String, Object>) lossNoticeMap.get("applicantId_EN");

            if (applicantMap != null) {
                Map<String, Object> clientMap = (Map<String, Object>) applicantMap.get("clientId_EN");
                cit.setApplicantPhone(getStringParam(applicantMap.get("tel")));
                if (clientMap != null) {
                    cit.setApplicantName(getStringParam(clientMap.get("surname")) + " " + getStringParam(clientMap.get("name")) + " " + getStringParam(clientMap.get("patronymic")));
                } else {
                    cit.setApplicantName("null");
                }
            }
        } else {
            cit.setApplicantName(getStringParam(lossNoticeMap.get("applicantName")));
            cit.setApplicantPhone(getStringParam(lossNoticeMap.get("applicantPhone")));
        }
        try {
            cit.setLaBirthDate(getFormattedDate(getDateParam(lossNoticeMap.get("insuredBirthDate"))));
        } catch (Exception e) {
            logger.error("Error printing insuredBirthDate", e);
            e.printStackTrace();
        }
        cit.setLaFirstName(getStringParam(lossNoticeMap.get("insuredName")));
        cit.setLaLastName(getStringParam(lossNoticeMap.get("insuredSurname")));
        cit.setLaPatromic(getStringParam(lossNoticeMap.get("insuredMiddleName")));
        cit.setExternalId(getStringParam(lossNoticeMap.get("lossNoticeId")));
        if (lossNoticeMap.get("eventCode") == null) {
            Map<String, Object> insEventMap = (Map<String, Object>) lossNoticeMap.get("insEventId_EN");
            cit.setEventCode(EventCodeType.fromValue(getStringParam(insEventMap.get("sysname"))));
        } else {
            cit.setEventCode(EventCodeType.fromValue(getStringParam(lossNoticeMap.get("eventCode"))));
        }
        try {
            cit.setEventDate(getFormattedDate(getDateParam(lossNoticeMap.get("eventDate"))));
        } catch (Exception e) {
            logger.error("Error printing eventDate", e);
            e.printStackTrace();
        }
        cit.setPolicyList(getPolicyList(lossNoticeMap, login, password));
        if (lossNoticeMap.get("reasonCode") == null) {
            Map<String, Object> damageCatMap = (Map<String, Object>) lossNoticeMap.get("damageCatId_EN");
            cit.setReasonCode(ReasonType.fromValue(getStringParam(damageCatMap.get("sysname"))));
        } else {
            cit.setReasonCode(ReasonType.fromValue(getStringParam(lossNoticeMap.get("reasonCode"))));
        }
        if (cit.getReasonCode() == null) {
            cit.setReasonCode(ReasonType.NULL);
        }

        try {
            // пока считаем датой регистрации - дату создания черновика.
            cit.setRegistryDate(getFormattedDate(getDateParam(lossNoticeMap.get("createDate"))));
            //cit.setRegistryDate(getFormattedDate(getDateParam(lossNoticeMap.get("regDate"))));
        } catch (Exception e) {
            logger.error("Error printing regDate", e);
            e.printStackTrace();
        }
    }

  /*  private ClaimImportType.Documents getDocuments(Map<String, Object> params) {
        ClaimImportType.Documents docs = new ClaimImportType.Documents();
        List<ClaimImportType.Documents.Document> docList = docs.getDocument();
        ClaimImportType.Documents.Document doc = new ClaimImportType.Documents.Document();
        doc.setDocumentDate("2017-07-19T00:00:00.000+03:00");
        doc.setDocumentName("testDoc");
        byte[] file = null;
        doc.setFile(file);
        docList.add(doc);
        
        return docs;
    }*/

    private PolicyListType getPolicyList(Map<String, Object> params, String login, String password) throws Exception {
        PolicyListType plt = new PolicyListType();
        if (params.get("contrId") != null) {
            Map<String, Object> searchParams = new HashMap<>();
            searchParams.put("CONTRID", params.get("contrId"));
            searchParams.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> searchContrResult = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParam", searchParams, login, password);
            List<PolicyListType.Policy> pList = plt.getPolicy();
            PolicyListType.Policy policy = new PolicyListType.Policy();
            policy.setPolicyID(BigInteger.valueOf(getLongParam(searchContrResult.get("EXTERNALID")).longValue()));
            policy.setPolicyNumber(getStringParam(searchContrResult.get("CONTRNUMBER")));
            policy.setPolicySeries(getStringParam(searchContrResult.get("CONTRPOLSER")));
            policy.setProgramCode(getSysNameByProdver(getStringParam(searchContrResult.get("PRODVERID")), login,password).toString());

            pList.add(policy);
        }
        return plt;
    }

}
