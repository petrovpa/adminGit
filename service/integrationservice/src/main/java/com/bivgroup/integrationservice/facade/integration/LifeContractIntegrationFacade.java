package com.bivgroup.integrationservice.facade.integration;

import com.bivgroup.integrationservice.facade.IntegrationBaseFacade;
import com.bivgroup.integrationservice.system.MapWrapper;
import com.bivgroup.integrationservice.system.MapWrapperImpl;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.sberinsur.esb.partner.shema.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.XMLGregorianCalendar;

import static ru.sberinsur.esb.partner.shema.ThirdPartyType.LEGAL_PERSON;
import static ru.sberinsur.esb.partner.shema.ThirdPartyType.PERSON;

@BOName("LifeContractIntegration")
public class LifeContractIntegrationFacade extends IntegrationBaseFacade {


    @WsMethod(requiredParams = {"CONTRIDLIST"})
    public Map<String, Object> dsLifeIntegrationGetContractsData(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Object> contractsIds = (List<Object>) params.get("CONTRIDLIST");
        // здесь в contractIds может быть только 1 id договора. иначе, не получится сохранять на каждый договор свой запрос ответ, и связь запроса ответа с договором
        // 
        if (contractsIds.isEmpty()) {
            return null;
        }
        Long contrId = getLongParam(contractsIds.get(0));
        ListContractImportType contractList = isEmptyCollection(contractsIds) ? new ListContractImportType() : getContractListByIds(contractsIds, login, password);
        Map<String, Object> result = new HashMap<String, Object>();
        result.putAll(params);
        try {

            AnswerImportListType resContrList = callLifePartnerPutContract(contractList);
            if (!params.containsKey("SESSIONIDFORCALL")) {
                result.put("request", contractList);
                result.put("response", resContrList);
            }

            resContrList.getAnswerImport();
            List<AnswerImportType> resList = resContrList.getAnswerImport();
            for (AnswerImportType item : resList) {
            String contractListXML = this.marshall(contractList, ListContractType.class);
            String contractListRespXML = this.marshall(resContrList, AnswerImportListType.class);
            result.put("requestStr", contractListXML);
            result.put("responseStr", contractListRespXML);
                if (StatusIntType.SUCCESS.equals(item.getStatus())) {
                    // makeTrans to 8502 state
                    // штатно в этом  методе должен быть только 1 ответ по 1 договору. 
                    Map<String, Object> lossNoticeSaveParams = new HashMap<String, Object>();
                    lossNoticeSaveParams.put("CONTRID", contrId);
                    lossNoticeSaveParams.put("EXTERNALID", item.getPolicyId());
                    lossNoticeSaveParams.put(RETURN_AS_HASH_MAP, true);
                    Map<String, Object> lossNoticeSaved = this.callServiceLogged(B2BPOSWS, "dsB2BContractUpdate", lossNoticeSaveParams, login, password);
            result.put("STATUS", "DONE");
                    break;
                } else {
                    result.put("STATUS", "ERROR");
                }
            }
        } catch (Exception e) {
            logger.error("Partner service call error", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.toString(); // stack trace as a string
            String contractListXML = this.marshall(contractList, ListContractType.class);
            result.put("requestStr", contractListXML);
            result.put("responseStr", sw.toString());
            result.put("STATUS", "outERROR");
        }
        return result;
    }

    private ListContractImportType getContractListByIds(List<Object> contractsIds, String login, String password) throws Exception {
        int size = contractsIds.size();
        logger.debug("count export contracts: " + size);

        ListContractImportType contractList = new ListContractImportType();
        List<ContractImportType> contracts = contractList.getContract();

        Map<String, Object> contractResult = null;
        int count = 0;
        for (Object contractId : contractsIds) {
            logger.debug("begin export: " + (++count) + " of " + size + " contracts");
            Map<String, Object> params = new HashMap<>();
            params.put(RETURN_AS_HASH_MAP, "TRUE");
            params.put("CONTRID", contractId);
            contractResult = this.callExternalService(B2BPOSWS, "dsB2BContractBrowseByContrIdForIntegration", params, login, password);
            if (!isEmptyMap(contractResult) && (contractResult.get("CONTRID") != null)) {
                ContractImportType contract = getContract(new MapWrapperImpl(contractResult), login, password);
                contracts.add(contract);
            } else {
                logger.error("error loading contract");
            }
        }
        logger.debug("end export contract. Contract export: " + count + " of " + size + " contracts");
        return contractList;
    }


    private ContractImportType getContract(MapWrapper contractMap, String login, String password) throws Exception {
        ContractImportType contract = new ContractImportType();

        contract.setRqUID("0");// TODO ???
        contract.setRqTM("0"); //TODO ???
        String prodSysName = contractMap.getString("PRODSYSNAME");
        // для продуктов активации - проставляем соответствующий тип.
        //if ("BOX_SEAT_BELT".equals(prodSysName) || "BOX_RIGHT_CHOICE".equals(prodSysName)) {
        if ("SBELT_RTBOX".equals(prodSysName) || "RIGHT_CHOICE_RTBOX".equals(prodSysName)) {
            contract.setActivation("ACTIVATION");
        } else {
            contract.setActivation("NEW_POLICY");
        }
        contract.setExternalId(contractMap.getString("EXTERNALID"));
        contract.setProductName(contractMap.getString("PRODUCTNAME"));
        contract.setPolicyProgram(contractMap.getString("PRODUCTNAME"));
        if ("SMART_POLICY".equals(prodSysName) || "SMART_POLICY_LIGHT".equals(prodSysName)) {
            contract.setProductName("SBI_ILI0");
            contract.setPolicyProgram(prodSysName);
        }
        contract.setPolicySeries(contractMap.getString("CONTRPOLSER"));
        //contract.setPolicyProgram(contractMap.getString("PROGNAME"));
        //согласно письму от Терентьев Святослав Евгеньевич 12.01.17 тег PolicyProgram заполнять так же как ProductName
        //contract.setPolicyNumber(contractMap.getString("CONTRNUMBER"));
        contract.setPolicyNumber(contractMap.getString("CONTRPOLSER") + " № " + contractMap.getString("CONTRPOLNUM"));
        // задача 5708
        // 1. Для премиальных продуктов к нам из АСБС приходит и мы храним серию 
        //и номер полиса в склеенном виде (например номер полиса «ИВСР00 № 000000404» + серия «ИВСР00»).
        //Для облегчения работы сотрудников коллцентра предлагаю придерживаться такого же формата при выпуске полисов из фронт-офиса.
        //contract.setPolicyNumber(contractMap.getString("CONTRPOLNUM"));
        contract.setGroupPolicy("0");
        contract.setCurrency(contractMap.getString("INSAMCURRENCYSTR"));
        contract.setThirdPartyList(getThirdPartyList(contractMap.getList("MEMBERLIST"), login, password));
        try {
            contract.setPolicyDocDate(getFormattedDate(contractMap.getDate("SIGNDATE"))); // TODO ??
            contract.setPolicyStartDate(getFormattedDate(contractMap.getDate("STARTDATE")));
            contract.setPolicyEndDate(getFormattedDate(contractMap.getDate("FINISHDATE")));
        } catch (Exception ex) {
            Logger.getLogger(LifeContractIntegrationFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        contract.setPaymentPeriodicity(getPeriodicity(contractMap.getString("PAYVARSYSNAME")));
        setCoverage(contract, contractMap);

        contract.setBroker(getBroker(contractMap));
        setPayment(contract, contractMap);
        if (contractMap.get("CONTREXTMAP") != null) {
            MapWrapper contrExtMap = contractMap.getMapWrapper("CONTREXTMAP");
            if (contrExtMap.get("assuranceLevelSelectedItem") != null) {
                MapWrapper assuranceLevelMap = contrExtMap.getMapWrapper("assuranceLevelSelectedItem");
                if (assuranceLevelMap.get("percentProportion") != null) {
                    contract.setWarrantyLevel(assuranceLevelMap.getBigDecimal("percentProportion"));
                }
            }
            if (prodSysName.equals("B2B_NEW_HORIZONS")) {
                processNewHorizonsParams(contract, contractMap, contrExtMap, login, password);

            }
        }
        //contract.setPayment(getPaymentList(contractMap.getList("PAYMENTLIST")));
        //contract.setManagerList(getManagerList(contractMap));
        contract.setManager(getManager(contractMap));
        contract.setPolicyStatus(contractMap.getString("STATENAME"));
        //contract.setParameterList(); TODO Пропускаем

        return contract;
    }

    private void processNewHorizonsParams(ContractImportType contract, MapWrapper contr, MapWrapper contrExt,
                                          String login, String password) throws Exception {
        Map<String, Object> procParams = new HashMap<>();
        procParams.put("CONTRMAP", contr.get());
        Map<String, Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BNewHorizontProcessExtParam", procParams, login, password);
        Map<String, Object> contrExtMapRaw = (Map<String, Object>) res.get(RESULT);
        contract.setPolicyEndDate(getFormattedDate(getDateParam(contrExtMapRaw.get("To1DATE"))));
        if ((contrExtMapRaw.get("To2DATE")) != null && (!contrExtMapRaw.get("To2DATE").toString().equals("-"))) {
            contract.setPolicyEndDate(getFormattedDate(getDateParam(contrExtMapRaw.get("To2DATE"))));
        }

        contract.setLifeOfTermDSP(getFormattedDate(getDateParam(contrExtMapRaw.get("From1DATE"))));
        if ((contrExtMapRaw.get("From2DATE")) != null && (!contrExtMapRaw.get("From2DATE").toString().equals("-"))) {
            contract.setLifeOfTermDSP2(getFormattedDate(getDateParam(contrExtMapRaw.get("From2DATE"))));
            Double rentVal = getDoubleParam(contrExtMapRaw.get("fixedRentValue"));
            Double rentFDVal = getDoubleParam(contrExtMapRaw.get("fixedRentFDValue"));
            contract.setSurvPayRatio(BigDecimal.ONE);
            if ((rentFDVal != null) && (rentVal != null) && (rentVal != 0)) {
                if (rentFDVal.compareTo(rentVal) < 0) {
                    Double ratio = rentFDVal / rentVal;
                    if (ratio != null) {
                        contract.setSurvPayRatio(BigDecimal.valueOf(ratio.doubleValue()));
                    }
                } else {
                    Double ratio = rentVal / rentFDVal;
                    if (ratio != null) {
//                        contract.setLifeOfTermDSP(getFormattedDate(getDateParam(contrExtMapRaw.get("From2DATE"))));
//                        contract.setLifeOfTermDSP2(getFormattedDate(getDateParam(contrExtMapRaw.get("From1DATE"))));

                        contract.setSurvPayRatio(BigDecimal.valueOf(ratio.doubleValue()));
                    }
                }
            }
            contract.setLifeOfTermPP(PERIODICITYMAP.get(getStringParam(contrExtMapRaw.get("PAYMENTVARSYSNAME"))));
        }

        contract.setGarantPeriodUnit("YEAR");
        Long countPayYear = getLongParam(contrExtMapRaw.get("countPayYear"));
        if (countPayYear != null) {
            BigInteger countBI = BigInteger.valueOf(countPayYear.longValue());
            if (countBI != null) {
                contract.setGarantPeriodTerm(countBI);
            }
        }

        List<Coverage> coverList = contract.getCoverageList().getCoverage();
        Map<String,Coverage> coverMap = new HashMap<>();
        ListCoverageType listCoverTypeNew = new ListCoverageType();
        List<Coverage> coverListNew = listCoverTypeNew.getCoverage();
        for (Coverage cover: coverList) {
            coverMap.put(cover.getCoverageName(), cover);
            if ("deathStateAccumulations".equalsIgnoreCase(cover.getCoverageName())) {
                Coverage covNew = copyCoverage(cover);
                // попробовать отъехать от отправки им кодов рисков ОИС, т.к. в таком случае придется и для других продуктов отправлять эти коды.
                // пусть определяют по датам какой из 2х покрытий с одинаковым именем - повышенный, какой нет.
//                covNew.setCoverageName("DEATH_CLAUSE");
                coverListNew.add(covNew);
    }

            if ("survival".equalsIgnoreCase(cover.getCoverageName())) {
                Coverage covNew = copyCoverage(cover);
                // попробовать отъехать от отправки им кодов рисков ОИС, т.к. в таком случае придется и для других продуктов отправлять эти коды.
                // пусть определяют по датам какой из 2х покрытий с одинаковым именем - повышенный, какой нет.
//                covNew.setCoverageName("LIFE_OF_TERM");
                if ((contrExtMapRaw.get("From2DATE")) != null && (!contrExtMapRaw.get("From2DATE").toString().equals("-"))) {
                    Coverage covNew2 = copyCoverage(cover);
                    // попробовать отъехать от отправки им кодов рисков ОИС, т.к. в таком случае придется и для других продуктов отправлять эти коды.
                    // пусть определяют по датам какой из 2х покрытий с одинаковым именем - повышенный, какой нет.
//                    covNew2.setCoverageName("LIFE_OF_TERM2");
                    List<CoverageDet> covDetList = covNew2.getCoverageDetList().getCoverageDet();
                    for (CoverageDet covDet: covDetList) {
                        covDet.setAmountAssured(getBigDecimalParam(contrExtMapRaw.get("fixedRentFDValue")));
                        covDet.setAmountPrem(BigDecimal.ZERO);
                    }
                    covDetList = covNew.getCoverageDetList().getCoverageDet();
                    for (CoverageDet covDet: covDetList) {
                        covDet.setAmountPrem(BigDecimal.ZERO);
                    }

                    covNew.setPaymentEndDate(dateToXMLGC(getDateParam(contrExtMapRaw.get("To1DATE"))));
                    covNew2.setPaymentStartDate(dateToXMLGC(getDateParam(contrExtMapRaw.get("From2DATE"))));
                    coverListNew.add(covNew2);
                }
                coverListNew.add(covNew);

            }

            if ("deathGuaranteedPayments".equalsIgnoreCase(cover.getCoverageName())) {
                Coverage covNew = copyCoverage(cover);
                // попробовать отъехать от отправки им кодов рисков ОИС, т.к. в таком случае придется и для других продуктов отправлять эти коды.
                // пусть определяют по датам какой из 2х покрытий с одинаковым именем - повышенный, какой нет.
//                covNew.setCoverageName("DEATH_CLAUSE3");
                if ((contrExtMapRaw.get("From2DATE")) != null && (!contrExtMapRaw.get("From2DATE").toString().equals("-"))) {
                    Coverage covNew2 = copyCoverage(cover);
                    // попробовать отъехать от отправки им кодов рисков ОИС, т.к. в таком случае придется и для других продуктов отправлять эти коды.
                    // пусть определяют по датам какой из 2х покрытий с одинаковым именем - повышенный, какой нет.
//                    covNew2.setCoverageName("DEATH_CLAUSE4");
                    List<CoverageDet> covDetList = covNew2.getCoverageDetList().getCoverageDet();
                    for (CoverageDet covDet: covDetList) {
                        covDet.setAmountAssured(getBigDecimalParam(contrExtMapRaw.get("fixedRentFDValue")));
                        covDet.setAmountPrem(BigDecimal.ZERO);
                    }
                    covDetList = covNew.getCoverageDetList().getCoverageDet();
                    for (CoverageDet covDet: covDetList) {
                        covDet.setAmountPrem(BigDecimal.ZERO);
                    }


                    covNew.setPaymentEndDate(dateToXMLGC(getDateParam(contrExtMapRaw.get("To1DATE"))));
                    covNew2.setPaymentStartDate(dateToXMLGC(getDateParam(contrExtMapRaw.get("From2DATE"))));

                    coverListNew.add(covNew2);
                }
                coverListNew.add(covNew);
            }

            if ("deathStatePayments".equalsIgnoreCase(cover.getCoverageName())) {
                Coverage covNew = copyCoverage(cover);
                // Смерть одного из ЗЛ по ЛП на этапе выплат
                // судя по всему риск попадается только в программе "Семейный фонд", а она в текущий момент исключена из продукта и не страхуется.
                // кода покрытия из ОИС по нему тоже нет.
                coverListNew.add(covNew);
            }



        }
        contract.setCoverageList(listCoverTypeNew);
    }

    private Coverage copyCoverage(Coverage cover) {
        Coverage result = new Coverage();
        result.setCoverageName(cover.getCoverageName());
        result.setStartDate(cover.getStartDate());
        result.setEndDate(cover.getEndDate());
        result.setPaymentStartDate(cover.getPaymentStartDate());
        result.setPaymentEndDate(cover.getPaymentEndDate());
        result.setPeriodicity(cover.getPeriodicity());
        result.setCoverageDetList(copyCoverageDet(cover.getCoverageDetList()));
        //result.setLifeAssureds(copyLifeAssureds(cover.getLifeAssureds()));
        //result.setExtraPremList(copyExtraPremList(cover.getExtraPremList()));


        return result;
    }

    private ListCoverageDetType copyCoverageDet(ListCoverageDetType coverageDetList) {
        ListCoverageDetType result = new ListCoverageDetType();
        List<CoverageDet> rescovDetList = result.getCoverageDet();
        List<CoverageDet> covDetList = coverageDetList.getCoverageDet();
        for (CoverageDet covDet: covDetList) {
            CoverageDet resCovDet = new CoverageDet();
            resCovDet.setAmountPrem(covDet.getAmountPrem());
            resCovDet.setAmountAssured(covDet.getAmountAssured());
            resCovDet.setCurrency(covDet.getCurrency());
            rescovDetList.add(resCovDet);
        }
        return result;
    }
    private ListPaymentType getPaymentList(List<Map<String, Object>> paymentlist) {
        ListPaymentType listPayment = new ListPaymentType();
        List<PaymentType> payments = listPayment.getPayment();

        for (Map<String, Object> payMap : paymentlist) {
            if (payMap != null) {
                payments.add(getPayment(new MapWrapperImpl(payMap)));
            }
        }
        return listPayment;
    }

    private PaymentType getPayment(MapWrapper payMap) {
        PaymentType payment = new PaymentType();

        //payment.setPaymentType(); TODO ???
        payment.setAmmount(payMap.getBigDecimal("AMVALUE"));
        payment.setCurrency(CurrencyType.fromValue(payMap.getString("AMCURRENCYSTR")));
        try {
            payment.setPaymentDate(getFormattedDate(payMap.getDate("PAYFACTDATE")));
        } catch (Exception ex) {
            Logger.getLogger(LifeContractIntegrationFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        return payment;
    }

    private PeriodicityType getPeriodicity(String payvarsysname) {
        return PERIODICITYMAP.get(payvarsysname);
        /*switch (payvarsysname) {
            case "QUARTERLY":
                return Periodicity.QUA;
            case "ANNUALLY":
                return Periodicity.ANN;
            case "SEMIANNUALLY":
                return Periodicity.SEM;
        }
        return Periodicity.ONE;*/
    }

    private ListThirdPartyImportType getThirdPartyList(List<Map<String, Object>> memberlist, String login, String password) throws Exception {
        ListThirdPartyImportType listThirdParty = new ListThirdPartyImportType();
        List<ThirdPartyImport> thirdParties = listThirdParty.getThirdParty();

        for (Map<String, Object> member : memberlist) {
            MapWrapper memberWrapper = new MapWrapperImpl(member);
            if (memberWrapper.has("PARTICIPANTMAP")) {
                if (!memberWrapper.getString("TYPESYSNAME").isEmpty()) {
                    String role = memberWrapper.getString("TYPESYSNAME");
                    boolean isValidRole = false;
                    if ("beneficiary".equals(role)) {
                        isValidRole = true;
                    }
                    if ("insured".equals(role)) {
                        isValidRole = true;
                    }
                    if ("insurer".equals(role)) {
                        isValidRole = true;
                    }
                    if (isValidRole) {
                        thirdParties.add(getThirdParty(memberWrapper.getMapWrapper("PARTICIPANTMAP"), role, login, password));
                    }
                }
            }
        }
        return listThirdParty;
    }

    private ThirdPartyImport getThirdParty(MapWrapper member, String role, String login, String password) throws Exception {
        ThirdPartyImport thirdParty = new ThirdPartyImport();

        thirdParty.setThirdPartyId(member.getLong("PARTICIPANTID").longValue());
        boolean isFiz = Objects.equals(1, member.get("PARTICIPANTTYPE"));
        boolean isIP = Objects.equals(1, member.get("ISBUSINESSMAN"));

        thirdParty.setThirdPartyType(isFiz && !isIP ? PERSON : LEGAL_PERSON);
        thirdParty.setFullName(getFullName(member, isFiz));
        thirdParty.setRole(getRole(role));

        Dictionary contact = new Dictionary(member.getListWrapper("contactList"), "CONTACTTYPESYSNAME", "VALUE");
        thirdParty.setPhoneMobile(contact.getValue("MobilePhone"));
        thirdParty.setPhoneWorking(contact.getValue("WorkAddressPhone"));
        thirdParty.setPhoneHome(contact.getValue("FactAddressPhone"));
        thirdParty.setEmail(contact.getValue("PersonalEmail"));

        thirdParty.setBirthPlace(member.getString("BIRTHPLACE"));

        Dictionary extAttr = new Dictionary(member.getListWrapper("extAttributeList2"), "EXTATT_SYSNAME", "EXTATTVAL_VALUE");
        thirdParty.setMaritalStatus(getMaritialStatus(extAttr.getValue("MaritalStatus")));
        thirdParty.setOccupation(getOccupation(extAttr.getValue("education"), login, password));
        thirdParty.setResident(extAttr.getValue("residencePermitForeignCountry"));
        thirdParty.setTaxResident(extAttr.getValue("taxResidentOtherCountry"));

        thirdParty.setGender(getGender(member));

//        QName lastNameQName = new QName("http://www.example.org/schema", "LastName");
//        JAXBElement<String> LastName = new JAXBElement<String>(lastNameQName, String.class, member.getString("LASTNAME"));
        thirdParty.setLastName(member.getString("LASTNAME"));
        thirdParty.setFirstName(member.getString("FIRSTNAME"));
        thirdParty.setPatronymic(member.getString("MIDDLENAME"));
//        thirdParty.setCitizenship(Objects.equals(0, member.get("CITIZENSHIP")) ? "Российская федерация" : "Иностранный гражданин");
        // по словам максима в поле Страна необходимо выгрузать цифровой код iso страны.
        // по доработке - заменяющей выбор гражданства между "Россия" и "Иностранное гос-во" на выбор из выпадающего списка стран, в гражданство выгружать 
        // цифровой код iso выбранной страны.
        // если договор старый, и еще не обновлен на новый компонент - подкостылим вывод правильных кодов.
        thirdParty.setCitizenship(getCitizenship(member, login, password));

        thirdParty.setBirthDate(getFormattedDate(member.getDate("BIRTHDATE")));
        //thirdParty.setLegalFormText(); TODO пока пропускаем
        if (isIP) {
            Dictionary ogrn = new Dictionary(member.getListWrapper("partRegDocList"), "REGDOCTYPESYSNAME", "DESCRIPTION");
            thirdParty.setOgrn(ogrn.getValue("OGRN"));
        }
        MapWrapper passport = getPassport(member.getList("documentList"),
                "PassportRF", "ForeignPassport", "BornCertificate", "MigrationCard");
        if (passport != null) {
            thirdParty.setDocumntsList(getDocumentsList(member.getList("documentList")));
            /* thirdParty.setDocumentFull(passport.getString("DESCRIPTION"));
            thirdParty.setDocumentType(getDocType(passport.getString("DOCTYPESYSNAME")));
            //thirdParty.setDocumentCountry(""); // пропускаем
            thirdParty.setDocumentNumber(passport.getString("DOCNUMBER"));
            thirdParty.setDocumentSeries(passport.getString("DOCSERIES"));
            thirdParty.setDocumentInstitution(passport.getString("ISSUEDBY"));
            thirdParty.setDocumentData(getFormattedDate(passport.getDate("ISSUEDATE")));
            //thirdParty.setDocumentCity(); пропускаем
            thirdParty.setDocumentCodeIns(passport.getString("ISSUERCODE"));*/
        }

        thirdParty.setListAddress(getListAddress(member.getList("addressList")));
        thirdParty.setTin(member.getString("INN"));

        return thirdParty;
    }

    private String getOccupation(String education, String login, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("hid", education);
        Map<String, Object> professionHBParams = new HashMap<>();
        professionHBParams.put("HANDBOOKNAME", "B2B.Life.Profession");
        professionHBParams.put("HANDBOOKDATAPARAMS", params);
        professionHBParams.put(RETURN_AS_HASH_MAP, true);

        try {
            Map<String, Object> professionRes = this.callService(B2BPOSWS, "dsB2BHandbookDataBrowseByHBName", professionHBParams, login, password);
            if (professionRes != null && "OK".equals(professionRes.get("Status"))) {
                List<Map<String, Object>> professions = (List<Map<String, Object>>) professionRes.get("B2B.Life.Profession");
                if (professions.size() != 0) {
                    return (String) professions.get(0).get("name");
                }
            }
        } catch (Exception e) {
            Logger.getLogger(LifeContractIntegrationFacade.class.getName()).log(Level.SEVERE, null, e);
        }
        return "";
    }

    private String getMaritialStatus(String maritalStatus) {
        return MARITIALMAP.get(maritalStatus);
        /*switch (maritalStatus) {
            case "MARITAL01":
                return "Холост";
            case "MARITAL02":
                return "Женат";
            case "MARITAL03":
                return "Не замужем";
            case "MARITAL04":
                return "Замужем";
        }
        return "";*/
    }

    private ListAddressType getListAddress(List<Map<String, Object>> addressList) {
        ListAddressType listAddress = new ListAddressType();
        List<Address> addresses = listAddress.getAddress();

        for (Map<String, Object> addrMap : addressList) {
            addresses.add(getAddress(new MapWrapperImpl(addrMap)));
        }

        return listAddress;
    }

    private Address getAddress(MapWrapper addrMap) {
        Address address = new Address();
        address.setAddressFull(addrMap.getString("ADDRESSTEXT1"));
        //address.setCountry("Россия");
        address.setCountry("643");
        // по словам максима в поле Страна необходимо выгрузать цифровой код iso страны.


        address.setAddressType(getAddressType(addrMap.getString("ADDRESSTYPESYSNAME")));
        address.setAddressTxt(addrMap.getString("ADDRESSTEXT1"));
        address.setCladrCode(addrMap.getString("STREETKLADR"));
        address.setPostCode(addrMap.getString("POSTALCODE"));
        address.setArea(addrMap.getString("REGION"));
        address.setDistrict(addrMap.getString("DISTRICT"));
        address.setTown(addrMap.getString("CITY"));
        address.setStreet(addrMap.getString("STREET"));
        address.setStreetNr(addrMap.getString("HOUSE"));
        address.setStreetBuild(addrMap.getString("BUILD"));
        address.setStreetFlat(addrMap.getString("FLAT"));
        return address;
    }

    private DocumentType getDocType(String doctypesysname) {
        return DOCUMENTTYPEMAP.get(doctypesysname);
        /*switch (doctypesysname) {
            case "PassportRF":
                return DocumentType.PASSPORT;
            case "BornCertificate":
                return DocumentType.BIRTH_CERTIFICATE;
            case "ForeignPassport":
                return DocumentType.CONF_RIGHTS_RF;
            case "MigrationCard":
                return DocumentType.MIGRATION_CARD;
        }
        return null;*/
    }

    private AddressType getAddressType(String addressTypeSysName) {
        return ADDRESSTYPEMAP.get(addressTypeSysName);
        /*switch (addressTypeSysName) {
            case "RegisterAddress":
                return AddressType.REGISTRATION;
            case "FactAddress":
                return AddressType.ACTUAL;
                // у нас в системе отсутсвтует адрес с типом - адрес рождения.
            //case "ForeignPassport":
              //  return AddressType.BORN;
        }
        return null;*/
    }

    private MapWrapper getPassport(List<Map<String, Object>> documentList, String... passportTypePriority) {
        for (String passType : passportTypePriority) {
            for (Map<String, Object> doc : documentList) {
                if (passType.equals(doc.get("DOCTYPESYSNAME"))) {
                    return new MapWrapperImpl(doc);
                }
            }
        }
        return null;
    }

    private RolesType getRole(String role) {
        return ROLEMAP.get(role);
        /*switch (role) {
            case "beneficiary":
                return Roles.BEN;
            case "insured":
                return Roles.LIFE_ASSURED;
            case "insurer":
                return Roles.HOLDER;
        }
        // GUARDIAN - опекун
        return Roles.BEN;*/
    }

    private String getFullName(MapWrapper member, boolean isFiz) {
        if (isFiz) {
            return member.getString("LASTNAME") + " "
                    + member.getString("FIRSTNAME") + " "
                    + member.getString("MIDDLENAME")
                    ;
        }
        return member.getString("FULLNAME");
    }

    private void setCoverage(ContractImportType contract, MapWrapper contractMap) throws Exception {
        ListInvestCoverageType investCovers = new ListInvestCoverageType();
        ListCoverageType covers = new ListCoverageType();
        List<InvestCoverageType> investCoverList = investCovers.getInvestCoverage();
        List<Coverage> coverList = covers.getCoverage();
        InvestCoverageType investCover = new InvestCoverageType();

        String prodSysName = contractMap.getString("PRODSYSNAME");

        //if ("B2B_INVEST_NUM1".equalsIgnoreCase(prodSysName)
        if ("LIGHTHOUSE".equalsIgnoreCase(prodSysName)
//                || "B2B_INVEST_COUPON".equalsIgnoreCase(prodSysName)) {
                || "SMART_POLICY_RB_ILIK".equalsIgnoreCase(prodSysName)
                || "SMART_POLICY".equalsIgnoreCase(prodSysName) || "SMART_POLICY_LIGHT".equalsIgnoreCase(prodSysName)
                ) {
            //инвестиционное покрытие.
            if (contractMap.get("CONTREXTMAP") != null) {
                MapWrapper contrExtMap = contractMap.getMapWrapper("CONTREXTMAP");

                investCover.setAmountBonus(BigDecimal.ZERO);
                BigDecimal insAmValue = contractMap.getBigDecimal("INSAMVALUE");
                BigDecimal exchangeRate = contractMap.getBigDecimal("CURRENCYRATE");
                investCover.setAmountCur1(insAmValue);

                investCover.setAmountCur2(insAmValue.multiply(exchangeRate));
                investCover.setBaseActive(contrExtMap.getString("fundStr"));
                investCover.setFix("FIXED_PERIOD");

                // #10321: 1. тег Contract/InvestCoverageList/InvestCoverage/InvestDate - не заполнять никогда
                //investCover.setInvestDate(getFormattedDate(contractMap.getDate("STARTDATE")));

                investCover.setStopLoss(contrExtMap.getBigDecimal("autopilotStopLossPercValue"));
                investCover.setTakeProfit(contrExtMap.getBigDecimal("autopilotTakeProfitPercValue"));
                investCover.setTypeOperation("BASE_ACTIVE");
            }
            investCoverList.add(investCover);
            contract.setInvestCoverage(investCovers);
        }

        //if ("B2B_INVEST_COUPON".equalsIgnoreCase(prodSysName)) {
        if ("SMART_POLICY_RB_ILIK".equalsIgnoreCase(prodSysName)) {
            // #10321: 2. Для продукта Маяк-купонный
            // #10321: 2.1. Не заполнять тег <InvestCoverege><BaseActive>Недвижимость<BaseActive> - он не имеет отношение к продукту
            investCover.setBaseActive(null);
            // #10321: 2.2. Заполнять тег <InvestCoverege><InvestConditions >код корзины, которую купил клиент</InvestConditions >
            MapWrapper baseActiveMap = contractMap.getMapWrapper("BASEACTIVEMAP");
            investCover.setInvestConditions(baseActiveMap.getString("CODE"));
            // #10321: 2.3. Заполнять тег <InvestCoverege><CustomerCoupon>0.113</CustomerCoupon> - размер купона клиента, берется динамически из конструктора фондов, сейчас динамически выводится в декларации
            investCover.setCustomerCoupon(baseActiveMap.getString("COUPONSIZE"));
        }

        if (contractMap.get("INSOBJGROUPLIST") != null) {
            List<MapWrapper> insObjGroupList = contractMap.getListWrapper("INSOBJGROUPLIST");
            for (MapWrapper insObjGroupMap : insObjGroupList) {
                if (insObjGroupMap.get("OBJLIST") != null) {
                    List<MapWrapper> objList = insObjGroupMap.getListWrapper("OBJLIST");
                    for (MapWrapper objMap : objList) {
                        if (objMap.get("CONTROBJMAP") != null) {
                            MapWrapper contrObjMap = objMap.getMapWrapper("CONTROBJMAP");
                            if (contrObjMap.get("CONTRRISKLIST") != null) {
                                List<MapWrapper> riskList = contrObjMap.getListWrapper("CONTRRISKLIST");
                                for (MapWrapper riskMap : riskList) {

                                    Coverage cover = new Coverage();
                                    cover.setCoverageName(riskMap.getString("PRODRISKSYSNAME"));
                                    try {
                                        cover.setPaymentStartDate(getFormattedDate(riskMap.getDate("STARTDATE")));
                                        cover.setPaymentEndDate(getFormattedDate(riskMap.getDate("FINISHDATE")));
                                    } catch (Exception ex) {
                                        Logger.getLogger(LifeContractIntegrationFacade.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    cover.setPeriodicity(getPeriodicity(contractMap.getString("PAYVARSYSNAME")));
                                    cover.setCoverageDetList(getCoverageDetList(contractMap, riskMap));
                                    coverList.add(cover);

                                }
                            }
                        }
                    }
                }
            }
        }

        contract.setCoverageList(covers);
    }

    private ListCoverageDetType getCoverageDetList(MapWrapper contractMap, MapWrapper riskMap) {
        ListCoverageDetType coverDets = new ListCoverageDetType();
        List<CoverageDet> coverDetList = coverDets.getCoverageDet();
        CoverageDet coverDet = new CoverageDet();
        coverDet.setAmountAssured(riskMap.getBigDecimal("INSAMVALUE"));
        String prodSysName = contractMap.getString("PRODSYSNAME");

        //if ("B2B_INVEST_NUM1".equalsIgnoreCase(prodSysName)
        if ("SMART_POLICY".equalsIgnoreCase(prodSysName) || "SMART_POLICY_LIGHT".equalsIgnoreCase(prodSysName)) {
            coverDet.setAmountPrem(contractMap.getBigDecimal("PREMVALUE"));//?? в риске
        } else {


            if (riskMap.get("PREMVALUE") != null) {
                coverDet.setAmountPrem(riskMap.getBigDecimal("PREMVALUE"));//?? в риске
            } else {
                coverDet.setAmountPrem(riskMap.getBigDecimal("INSAMVALUE"));//?? в риске
            }
        }
        String curStr = riskMap.getString("INSAMCURRENCYSTR", contractMap.getString("INSAMCURRENCYSTR"));
        coverDet.setCurrency(CurrencyType.fromValue(curStr));
        coverDetList.add(coverDet);
        return coverDets;
    }

    private Broker getBroker(MapWrapper contractMap) {
        Broker brocker = new Broker();
        brocker.setBankRegion(contractMap.getString("REGIONBANK"));
        brocker.setBroker(contractMap.getString("VSP"));
        brocker.setBrokerParent(contractMap.getString("OSB"));
        brocker.setBrokerRoot(contractMap.getString("TERRITORYBANK"));
        return brocker;
    }

    /*  private ListManager getManagerList(MapWrapper contractMap) {
        ListManager lm = new ListManager();
        lm.setManager(getManager(contractMap));
        return lm;
    }*/
    private ManagerType getManager(MapWrapper contractMap) {
        ManagerType man = new ManagerType();
        man.setManageName(contractMap.getString("MANAGERLASTNAME") + " " + contractMap.getString("MANAGERFIRSTNAME")
                + " " + contractMap.getString("MANAGERMIDDLENAME"));
        try {
            man.setManagerDate(getFormattedDate(contractMap.getDate("CREATEDATE")));
        } catch (Exception ex) {
            Logger.getLogger(LifeContractIntegrationFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        man.setManagerID(contractMap.getLong("MANAGERID").longValue());
        man.setManagerNumber(contractMap.getString("MANAGERCODE"));

        return man;
    }

    private void setPayment(ContractImportType contract, MapWrapper contractMap) {

        List<Map<String, Object>> paymentlist = contractMap.getList("PAYMENTLIST");
        ListPaymentType listPayment = new ListPaymentType();
        List<PaymentType> payments = listPayment.getPayment();
        boolean hasPayments = false;
        for (Map<String, Object> payMap : paymentlist) {
            if (payMap != null) {
                payments.add(getPayment(new MapWrapperImpl(payMap)));
                hasPayments = true;
            }
        }
        if (hasPayments) {
            contract.setPayment(listPayment);
        }

    }

    private DocumentsListImportType getDocumentsList(List<Map<String, Object>> docList) throws Exception {
        DocumentsListImportType dlt = new DocumentsListImportType();
        List<DocumentsImportType> dtl = dlt.getDocument();
        for (Map<String, Object> map : docList) {
            MapWrapper mapDoc = new MapWrapperImpl(map);
            if ("PassportRF".equals(mapDoc.getString("DOCTYPESYSNAME"))
                    || "ForeignPassport".equals(mapDoc.getString("DOCTYPESYSNAME"))
                    || "BornCertificate".equals(mapDoc.getString("DOCTYPESYSNAME"))
                    || "MigrationCard".equals(mapDoc.getString("DOCTYPESYSNAME"))) {
                DocumentsImportType dt = new DocumentsImportType();
                dt.setDocumentFull(mapDoc.getString("DESCRIPTION"));
                dt.setDocumentType(getDocType(mapDoc.getString("DOCTYPESYSNAME")));
                //thirdParty.setDocumentCountry(""); // пропускаем
                dt.setDocumentNumber(mapDoc.getString("DOCNUMBER"));
                dt.setDocumentSeries(mapDoc.getString("DOCSERIES"));
                dt.setDocumentInstitution(mapDoc.getString("ISSUEDBY"));
                dt.setDocumentData(getFormattedDate(mapDoc.getDate("ISSUEDATE")));
                //thirdParty.setDocumentCity(); пропускаем
                dt.setDocumentCodeIns(mapDoc.getString("ISSUERCODE"));
                dtl.add(dt);
            }
        }
        return dlt;
    }

    private String getGender(MapWrapper member) {
        return GENDERMAP.get(member.getString("GENDER"));
        /*if (member.get("GENDER") != null) {
            if ("1".equals(member.getString("GENDER"))) {
                return "F";
            }
            if ("0".equals(member.getString("GENDER"))) {
                return "M";
            }
        }
        return "unknow";*/
    }

    private String getCitizenship(MapWrapper member, String login, String password) throws Exception {
        if (member.get("CITIZENSHIP") != null) {
            String citizenship = member.getString("CITIZENSHIP");
            if ("0".equals(citizenship)) {
                //Российская федерация
                return "643";
            }
            if ("1000".equals(citizenship)) {
                //Иностранный гражданин
                return "000";
            }
            return getCountryDigitCodeById(citizenship, login, password);
        }
        return "";
    }


    class Dictionary {

        Map<String, String> dataMap = new HashMap<>();

        Dictionary(List<MapWrapper> dataList, String key, String value) {
            for (MapWrapper map : dataList) {
                dataMap.put(map.getString(key), map.getString(value));
            }
        }

        String getValue(String name) {
            return Optional.ofNullable(dataMap.get(name)).orElse("");
        }

    }

}
