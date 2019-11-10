package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;
import ru.diasoft.rsa.beanmaputils.BeanToMapMapper;
import ru.diasoft.rsa.beanmaputils.MapException;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.sberinsur.esb.partner.shema.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

@BOName("LifeClaimGetter")
public class LifeClaimGetterFacade extends IntegrationBaseFacade {

    @WsMethod()
    public Map<String, Object> dsLifeIntegrationGetClaimList(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        String pathPrefix = "";
        if ((params.get("urlPath") != null) && (params.get("SESSIONIDFORCALL") != null)) {
            pathPrefix = params.get("urlPath").toString() + "?sid=" + params.get("SESSIONIDFORCALL").toString() + "&fn=";
        }

        try {
            GetObjType got = new GetObjType();
            long claimId = 979763782l;//979763782
            if (params.get("CLAIMID") != null) {
                logger.info("LK get Full contr info: " + params.get("CLAIMID").toString());
                claimId = Long.valueOf(params.get("CLAIMID").toString()).longValue();
            }
            got.setClaimId(claimId);

            ClaimType resContrList = callLifePartnerGetClaims(got);
            List<ClaimPolicyType> ccList = resContrList.getClaimPolicy();
            List<Map<String, Object>> claimList = new ArrayList<>();

            List<Map<String, Object>> docListHaveUrlDoc = new ArrayList<>();
            List<Map<String, Object>> docListNotHaveUrlDoc = new ArrayList<>();

            for (ClaimPolicyType claim : ccList) {
                List<DocumentObjType> docList = claim.getClaimDocumentList().getClaimDocument();
                for (DocumentObjType docMap : docList) {
                    // шифруем url
                    DocumentObjType processedDoc = process1CDoc(docMap, pathPrefix, login, password);

                    Map<String, Object> claimDocMap = BeanToMapMapper.mapBeansToMap(processedDoc);
                    // Сортируем на две маппы (c URL и без)
                    if (!getStringParam(docMap.getDocUrl()).isEmpty()) {
                        // Маппим параметры
                        addNewNameByOldNameWithMap(claimDocMap, "name", "FILENAME");
                        addNewNameByOldNameWithMap(claimDocMap, "docUrl", "DOWNLOADPATH");
                        addDocItemToList(claimDocMap, docListHaveUrlDoc);
                    } else {
                        addDocItemToList(claimDocMap, docListNotHaveUrlDoc);
                    }
                }
                Map<String, Object> claimMap = BeanToMapMapper.mapBeansToMap(claim);
                //Map<String, Object> claimMap = mapClaim(claim, login, password);
                claimList.add(claimMap);
            }

            // Получаем объединенный список
            List<Map<String, Object>> claimDocList = unionClaimDocumentWithoutRepeat(docListHaveUrlDoc, docListNotHaveUrlDoc);

            if (claimDocList != null) {
                result.put("CLAIMDOCLIST", claimDocList);
            }

            result.put("CLAIMFULLLIST", claimList);
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

    private void addNewNameByOldNameWithMap(Map<String, Object> map, String oldName, String newName) {
        if (((oldName != null) && (!oldName.isEmpty())) && ((newName != null) && (!newName.isEmpty()))) {
            map.put(newName, map.get(oldName));
        }
    }

    /**
     * Возвращает объединенный лист без повторений docListHaveUrlDoc и docListNotHaveUrlDoc
     *
     * @param docListHaveUrlDoc
     * @param docListNotHaveUrlDoc
     */
    private List<Map<String, Object>> unionClaimDocumentWithoutRepeat(List<Map<String, Object>> docListHaveUrlDoc, List<Map<String, Object>> docListNotHaveUrlDoc) {

        List<Map<String, Object>> resultList = docListHaveUrlDoc;

        for (Map docItem : docListNotHaveUrlDoc) {
            if (!resultList.contains(docItem)) {
                resultList.add(docItem);
            }
        }

        return resultList;
    }

    private void addDocItemToList(Map<String, Object> document, List<Map<String, Object>> documentList) throws MapException {
        //Добавляем только если такого нет
        if (!documentList.contains(document)) {
            Map<String, Object> documentContainer = new HashMap<>();
            //Добавляем имя
            if (!getStringParam(document.get("name")).isEmpty()) {
                documentContainer.put("DOCNAME", document.get("name"));
            }

            // Отсекаем все документы, у которых все свойства == null
            if (document.values().stream()
                    .filter((value) -> (value != null)).count() != 0) {

                documentContainer.put("DOCUMENT", document);
                documentList.add(documentContainer);

            }

        }
    }


    private Map<String, Object> mapClaim(ClaimPolicyType claim, String login, String password) throws Exception {
        logger.info("startMapContractFullInfo");
        Map<String, Object> claimMap = new HashMap<String, Object>();

        //Дата заявления
        claimMap.put("REQUESTDATE", processDate(claim.getApplicationDate()));
        //Выгодоприобретатель
        Map<String, Object> benMap = new HashMap<>();
        processParticipant(benMap, claim.getBen(), login, password);
        claimMap.put("BENEFICIARYMAP", benMap);
        claimMap.put("RECIPIENTMAP", benMap);
        //Перечень документов
        parocessClaimDocs(claimMap, claim.getClaimDocumentList());

        //Идентификатор заявления на убыток
        claimMap.put("CLAIMID", claim.getClaimID());
        //Номер убытка
        claimMap.put("CLAIMNUMBER", claim.getClaimNumb());
        //Идентификатор убытка
        claimMap.put("CLAIMPOLICYID", claim.getClaimPolicyID());
        //Причина
        claimMap.put("REASONCODE", claim.getReasonCode());
        //Дата события, имеющего признак страхового события
        claimMap.put("EVENTDATE", processDate(claim.getEventDate()));
        //Перечень метаданных
        claim.getListParameter();
        //Идентификатор полиса
        claimMap.put("POLICYID", claim.getPolicyId());
        //Номер полиса
        claimMap.put("CONTRNUMBER", claim.getPolicyNumber());
        //Код риска по продукту
        claimMap.put("PRODRISKEXTERNALID", claim.getProductRisk());

        //Имя программы
        claimMap.put("PRODPROGEXTERNALID", claim.getProgramCode());
        //Категория риска
        //claimMap.put("RISKCATEGORY", claim.getRiskCategory());
        //Перечень статусов
        processStatusList(claimMap, claim.getStatusList());

        return claimMap;
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

                        processParticipant(participantMap, thirdParty, login, password);

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

    private void processParticipant(Map<String, Object> participantMap, ThirdParty thirdParty, String login, String password) {

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
        result.put("VALUE", value);
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
        Map<String, Object> res = this.callService(B2BPOSWS, "dsB2BProductProgramBrowseListByParam4lk", param, login, password);
        if (res.get("PRODPROGID") != null) {
            return res;
        }
        logger.error("Unknown programm " + policyProgram);
        return null;

    }

    private void parocessClaimDocs(Map<String, Object> claimMap, ClaimPolicyType.ClaimDocumentList cdl) {

      /*  List<ClaimPolicyType.ClaimDocumentList.ClaimDocument> dList = cdl.getClaimDocument();
        List<Map<String, Object>> claimDocList = new ArrayList<Map<String, Object>>();
        for (ClaimPolicyType.ClaimDocumentList.ClaimDocument claimDocument : dList) {
            Map<String, Object> docMap = new HashMap<String, Object>();
            //Дата документа
            docMap.put("DOCDATE", processDate(claimDocument.getDate()));
            //Расширение документа
            docMap.put("DOCEXT", claimDocument.getDocExtension());
            //Код документа в 1C документооборот
            docMap.put("DOCURL", claimDocument.getDocUrl());
            //Ошибка
            docMap.put("MISTAKE", claimDocument.getMistake());
            //Наименование документа
            docMap.put("NAME", claimDocument.getName());
            claimDocList.add(docMap);
        }
        claimMap.put("DOCLIST", claimDocList);*/
    }

    private void processStatusList(Map<String, Object> claimMap, ClaimPolicyType.StatusList sl) {

        List<Status> dList = sl.getStatus();
        List<Map<String, Object>> claimStatusList = new ArrayList<Map<String, Object>>();
        for (Status status : dList) {
            Map<String, Object> statusMap = new HashMap<String, Object>();
            //Дата документа
            statusMap.put("STATUSDATE", processDateTime(status.getDate()));
            //Расширение документа
            statusMap.put("STATUS", status.getStatus());
            claimStatusList.add(statusMap);
        }
        claimMap.put("STATUSLIST", claimStatusList);
    }

}
