package com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider;

import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BReasonChangeDataProviderBen")
public class B2BReasonChangeDataProviderBenFacade extends B2BReasonChangeDataProviderPersonCustomFacade {
    private static final String NEWBENEF_PERSON_TYPE = "NEWBENEF"; // новый выгодоприобретатель

    private final Logger logger = Logger.getLogger(this.getClass());

    /**
     * Сервис формирования данных для изменения "Выгодоприобретателя"
     *
     * @param params - идентификатор изменения
     * @return - мапу с ошибкой
     * @throws Exception
     */
    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderBenChange(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BChangeReasonDataProviderFinancialVacation begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        String error = "";
        Map<String, Object> fullContract = null;
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        if (error.isEmpty() && !isNotExistContract) {
            Long externalId = getLongParam(params.get("EXTERNALID"));
            fullContract = getFullContractInfo(externalId, login, password);
            error = getStringParam(fullContract, ERROR);
        }

        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(reasonReportDataMap, "CHANGEBENEF");
        reasonReportDataMap.put("changeBenef", TRUE_STR_VALUE);

        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> benefMap = new HashMap<>();
        if (error.isEmpty()) {
            benefMap = getMapParam(reasonMap, "personId_EN");
            if (benefMap == null) {
                Long thirdPartyId = null;
                thirdPartyId = getLongParam(reasonMap, "newThirdPartyId");
                if (thirdPartyId == null) {
                    thirdPartyId = getLongParam(reasonMap, "oldThirdPartyId");
                }
                // если у нас ничего не указано то создаем пустую мапу
                if (thirdPartyId == null) {
                    benefMap = new HashMap<>();
                    //error = "Не указан идентификатор выгодоприобретателя!";
                }
                if (!isNotExistContract && thirdPartyId != null) {
                    benefMap = getMemberById(fullContract, thirdPartyId,
                            "beneficiary", "выгодоприобретателя");
                    error = getStringParam(benefMap, ERROR);
                }
            }
        }

        boolean isVipSegment = getBooleanParam(reportData, ISVIPSEGMENT_PARAM_NAME, false);
        boolean isNeedAnalyzeInsured = false;
        if (error.isEmpty()) {
            String prodProgSysName = getStringParam(reportData, "PRODPROGSYSNAME");
            Long prodConfId = getLongParam(reportData, "PRODCONFID");
            List<Map<String, Object>> prodDefValList = new ArrayList<>();
            List<Map<String, Object>> prodDefValForInsured = new ArrayList<>();
            if (isVipSegment) {
                if (!prodProgSysName.isEmpty() && prodConfId != null) {
                    Map<String, Object> prodDefValueQuery = new HashMap<>();
                    prodDefValueQuery.put("PRODCONFID", prodConfId);
                    String findFormName = prodProgSysName + "_BENEF_FORM";
                    prodDefValueQuery.put("NAME", findFormName);
                    prodDefValList = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME,
                            "dsB2BProductDefaultValueBrowseListByParam",
                            prodDefValueQuery, login, password);
                    String findIsNeedName = prodProgSysName + "_ANALYZE_INSURED";

                    prodDefValueQuery.put("NAME", findIsNeedName);
                    prodDefValForInsured = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME,
                            "dsB2BProductDefaultValueBrowseListByParam",
                            prodDefValueQuery, login, password);

                    if (!prodDefValForInsured.isEmpty()) {
                        isNeedAnalyzeInsured = getBooleanParam(prodDefValForInsured.get(0), "VALUE", false);
                    }
                }
            }

            String formNumber = "";
            if (!prodDefValList.isEmpty()) {
                formNumber = getStringParam(prodDefValList.get(0), "VALUE");
            } else {
                formNumber = isVipSegment ? "11" : "2";
            }

            String formName = "FORM" + formNumber;
            Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
            showFormMap.put(formName, TRUE_STR_VALUE);
            showFormMap.put("FORM1IND", isVipSegment ? TRUE_STR_VALUE : FALSE_STR_VALUE);
            showFormMap.put("DECL", TRUE_STR_VALUE);
        }

        if (error.isEmpty() && isVipSegment && isNeedAnalyzeInsured && !isNotExistContract) {
            List<Map<String, Object>>  memberList = getOrCreateListParam(fullContract, "MEMBERLIST");
            memberList = filterMemberList(memberList, null, "insured");
            Map<String, Object> applicantMap = getMemberById(fullContract, null, "insurer", "страхователь");
            Map<String, Object> participantMap;
            for(Map<String, Object> member : memberList) {
                participantMap = getOrCreateMapParam(member, "PARTICIPANTMAP");
                if (!getStringParam(participantMap, "THIRDPARTYID")
                        .equals(getStringParam(applicantMap, "THIRDPARTYID"))) {
                    getOrCreateMapParam(reportData, "CURINSUREDMAP").putAll(participantMap);
                    break;
                }
            }
        }

        if (error.isEmpty()) {
            if (isVipSegment) {
                vipChangeBenef(reportData, benefMap, reasonMap, isNotExistContract, login, password);
            } else {
                massChangeBenef(reportData, benefMap, reasonMap, isNotExistContract, login, password);
            }
        }
        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);
        reportData.put("ISNEEDADDCONTRNUMBER", true);

        Map<String, Object> result = new HashMap<>();
        if (!error.isEmpty()) {
            reportData.put(ERROR, error);
        }
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        logger.debug("dsB2BChangeReasonDataProviderFinancialVacation end");
        return result;
    }

    private void vipChangeBenef(Map<String, Object> reportData, Map<String, Object> personMap,
                                Map<String, Object> reasonMap, boolean isNotExistContract, String login, String password)
            throws Exception {
        List<Map<String, Object>> newBenefList = getOrCreateListParam(reportData, "NEWBENEFLIST");
        Map<String, Object> benefPersonMap = getOrCreateMapParam(reportData, "PERSONMAP");
        if (personMap == null || personMap.isEmpty() || isNotExistContract) {
            benefPersonMap.put("personType", NEWBENEF_PERSON_TYPE);
            mappingResidentInfo(benefPersonMap, new HashMap<>(), isNotExistContract);
            return;
        }
        if (newBenefList.isEmpty()) {
            newBenefList.add(createNewBenef(personMap, reasonMap));
        } else {
            List<Map<String, Object>> filterBenefList = new ArrayList<>();
            if (personMap.get("documentList") != null) {
                filterBenefList = newBenefList.stream().filter(new Predicate<Map<String, Object>>() {
                    @Override
                    public boolean test(Map<String, Object> stringObjectMap) {
                        return getStringParam(stringObjectMap, LASTNAME_PARAM_NAME).equals(getStringParam(personMap, LASTNAME_PARAM_NAME))
                                && getStringParam(stringObjectMap, "FIRSTNAME").equals(getStringParam(personMap, "FIRSTNAME"))
                                && getStringParam(stringObjectMap, "MIDDLENAME").equals(getStringParam(personMap, "MIDDLENAME"));
                    }
                }).collect(Collectors.toList());
            } else {
                filterBenefList = newBenefList.stream().filter(new Predicate<Map<String, Object>>() {
                    @Override
                    public boolean test(Map<String, Object> stringObjectMap) {
                        return getStringParam(stringObjectMap, LASTNAME_PARAM_NAME).equals(getStringParam(personMap, "surname"))
                                && getStringParam(stringObjectMap, "FIRSTNAME").equals(getStringParam(personMap, "name"))
                                && getStringParam(stringObjectMap, "MIDDLENAME").equals(getStringParam(personMap, "patronymic"));
                    }
                }).collect(Collectors.toList());
            }
            if (!filterBenefList.isEmpty()) {
                Map<String, Object> curBenefMap = filterBenefList.get(0);
                List<Map<String, Object>> riskList = getListParam(curBenefMap, "RISKLIST");
                riskList.add(createRiskItem(reasonMap));
            } else {
                newBenefList.add(createNewBenef(personMap, reasonMap));
            }
        }

        if (personMap.get("documentList") == null) {
            benefPersonMap.put("id", personMap.get("id"));
            benefPersonMap.put("personType", NEWBENEF_PERSON_TYPE);
            updateReportPersonMapByClientMap(benefPersonMap, personMap, isNotExistContract, login, password);
        } else {
            if (benefPersonMap.isEmpty() || benefPersonMap.size() == 1) {
                benefPersonMap.putAll(personMap);
                Long countryId = getLongParamLogged(personMap, "COUNTRYID");
                Map<String, Object> country = getCountryInfo(countryId);
                String sitizenship = mappingSitizenship(benefPersonMap, country);
                List<Map<String, Object>> documentList = getOrCreateListParam(personMap, "documentList");
                documentList = checkPassportAndCopyOther(documentList, sitizenship);
                benefPersonMap.put("documentList", documentList);
                benefPersonMap.put("personType", NEWBENEF_PERSON_TYPE);
            }
        }
    }

    private List<Map<String, Object>> checkPassportAndCopyOther(List<Map<String, Object>> documentList, String citizenship) {
        List<Map<String, Object>> resultList = new ArrayList<>(documentList);
        Map<String, Object> item;
        // системное наименование типа требуемого документа
        String docTypeSysName = citizenship.equals("1") ? "ForeignPassport" : "PassportRF";
        // системное наименование типа противоположного документа
        String oppositeDocTypeSysName = citizenship.equals("1") ? "PassportRF" : "ForeignPassport";
        String docTypeName = citizenship.equals("1") ? "Паспорт иностранного гражданина"
                : "Паспорт гражданина РФ";
        String oppositeDocTypeName = citizenship.equals("1") ? "Паспорт гражданина РФ"
                : "Паспорт иностранного гражданина";
        List<Map<String, Object>> filterDocumentList = documentList.stream()
                .filter(new Predicate<Map<String, Object>>() {
                    @Override
                    public boolean test(Map<String, Object> stringObjectMap) {
                        return docTypeSysName.equals(getStringParam(stringObjectMap, "DOCTYPESYSNAME"));
                    }
                }).map(new Function<Map<String, Object>, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> apply(Map<String, Object> stringObjectMap) {
                        if (getStringParam(stringObjectMap, "DOCTYPENAME").isEmpty()) {
                            stringObjectMap.put("DOCTYPENAME", docTypeName);
                        }
                        return stringObjectMap;
                    }
                })
                .collect(Collectors.toList());

        if (filterDocumentList.isEmpty()) {
            Optional<Map<String, Object>> findDocument = documentList.stream()
                    .filter(new Predicate<Map<String, Object>>() {
                        @Override
                        public boolean test(Map<String, Object> stringObjectMap) {
                            return oppositeDocTypeSysName.equals(getStringParam(stringObjectMap, "DOCTYPESYSNAME"));
                        }
                    }).findAny();
            if (findDocument.isPresent()) {
                Map<String, Object> copyDoc = findDocument.get();
                if (getStringParam(copyDoc, "DOCTYPENAME").isEmpty()) {
                    copyDoc.put("DOCTYPENAME", oppositeDocTypeName);
                }
                copyDoc.put("DOCTYPESYSNAME", docTypeSysName);
                resultList.add(copyDoc);
            } else {
                // если список документов отсутствует то создадим один пустой документ по гражданству
                // только если гражданство существует
                if (!citizenship.isEmpty()) {
                    item = new HashMap<>();
                    item.put("DOCTYPESYSNAME", docTypeSysName);
                    item.put("DOCTYPENAME", docTypeName);
                    resultList.add(item);
                }
            }
        }
        return resultList;
    }


    private Map<String, Object> createNewBenef(Map<String, Object> personMap, Map<String, Object> reasonMap) {
        Map<String, Object> curBenefMap = new HashMap<>();
        if (personMap.get("documentList") != null) {
            curBenefMap.put(LASTNAME_PARAM_NAME, personMap.get(LASTNAME_PARAM_NAME));
            curBenefMap.put("FIRSTNAME", personMap.get("FIRSTNAME"));
            curBenefMap.put("MIDDLENAME", personMap.get("MIDDLENAME"));
        } else {
            curBenefMap.put(LASTNAME_PARAM_NAME, personMap.get("surname"));
            curBenefMap.put("FIRSTNAME", personMap.get("name"));
            curBenefMap.put("MIDDLENAME", personMap.get("patronymic"));
        }
        List<Map<String, Object>> riskList = new ArrayList<>();
        riskList.add(createRiskItem(reasonMap));
        curBenefMap.put("RISKLIST", riskList);
        return curBenefMap;
    }

    private Map<String, Object> createRiskItem(Map<String, Object> reasonMap) {
        Map<String, Object> risk = new HashMap<>();
        String riskCode = getStringParam(reasonMap, "riskCode");
        risk.put("riskSysName", generateRiskSysName(riskCode));
        risk.put("part", getStringParam(reasonMap, "part"));
        return risk;
    }

    private String generateRiskSysName(String riskCode) {
        String result = "";
        //смерть нс SYSNAME like '%ACC%' and SYSNAME like '%DEATH%'
        if (riskCode.contains("ACC") && riskCode.contains("DEATH")) {
            result = "DEATHNS";
        }
        //смерть SYSNAME like '%DEATH%' and SYSNAME not like '%ACC%' and SYSNAME not like '%ALL%' and SYSNAME not like '%CRASH%' and SYSNAME not like '%TRANS%'
        if (riskCode.contains("DEATH") && !riskCode.contains("ACC") && !riskCode.contains("ALL")
                && !riskCode.contains("CRASH") && !riskCode.contains("TRANS")) {
            result = "DEATH";
        }
        //Смерть Застрахованного лица по любой причине SYSNAME like '%ALL%' and SYSNAME like '%DEATH%
        if (riskCode.contains("DEATH") && riskCode.contains("ALL")) {
            result = "DEATHLP";
        }
        //Смерть Застрахованного лица во время поездки на транспорте общего пользования SYSNAME like '%DEATH%' and SYSNAME like '%CRASH%' or SYSNAME like '%TRANS%'
        if (riskCode.contains("DEATH") && (riskCode.contains("CRASH") || riskCode.contains("TRANS"))) {
            result = "DEATHTRANS";
        }
        //дожитие SYSNAME like '%LIFE%' and sysname not like '%TERM%'
        if (riskCode.contains("LIFE") && !riskCode.contains("TERM")) {
            result = "SURVIVE";
        }
        //Дополнительное страхование жизни на срок sysname like '%TERM%' and sysname not like '%LIFE%'
        if (!riskCode.contains("LIFE") && riskCode.contains("TERM")) {
            result = "ADDINS";
        }
        //смерть с выплатой к сроку sysname like '%DEATH%' and sysname like '%CLAUSE%'
        if (riskCode.contains("DEATH") && riskCode.contains("CLAUSE")) {
            result = "DEATHPAYMENT";
        }
        //Смерть в Гарантированный период sysname like '%DEATH_CLAUSE3%' and sysname like '%DEATH_CLAUSE4%'
        if (riskCode.contains("DEATH_CLAUSE3") && riskCode.contains("DEATH_CLAUSE4")) {
            result = "DEATHGUARANTPERIOD";
        }
        //Смерть в Накопительный период sysname like '%DEATH_ALL_CAUSES'
        if (riskCode.endsWith("DEATH_ALL_CAUSES")) {
            result = "DEATHACCPERIOD";
        }
        return result;
    }

    private void massChangeBenef(Map<String, Object> reportData, Map<String, Object> personMap, Map<String, Object> reasonMap,
                                 boolean isNotExistContract, String login, String password) throws Exception {
        Map<String, Object> changeBenefDataMap = getOrCreateMapParam(reportData, "CHANGEBENEFDATAMAP");
        changeBenefDataMap.put("CHGBENEFMULTI", TRUE_STR_VALUE);
        List<Map<String, Object>> documentList = getOrCreateListParam(changeBenefDataMap, "documentList");
        getOrCreateListParam(changeBenefDataMap, "contactList");
        getOrCreateListParam(changeBenefDataMap, "addressList");

        if (isNotExistContract) {
            mappingResidentInfo(changeBenefDataMap, new HashMap<>(), isNotExistContract);
            return;
        }

        if (changeBenefDataMap.isEmpty() || !getBooleanParam(changeBenefDataMap, "isBenefCreate", false)) {
            if (personMap.get("documentList") != null) {
                addRiskType(changeBenefDataMap, reasonMap);
                changeBenefDataMap.putAll(personMap);
                Long countryId = getLongParamLogged(personMap, "COUNTRYID");
                Map<String, Object> country = getCountryInfo(countryId);
                String sitizenship = mappingSitizenship(changeBenefDataMap, country);
                documentList = getOrCreateListParam(personMap, "documentList");
                documentList = checkPassportAndCopyOther(documentList, sitizenship);
                changeBenefDataMap.put("documentList", documentList);
            } else {
                addRiskType(changeBenefDataMap, reasonMap);
                updateReportPersonMapByClientMap(changeBenefDataMap, personMap, isNotExistContract, login, password);
            }
            changeBenefDataMap.put("isBenefCreate", true);
        } else {
            String briefName = getStringParam(changeBenefDataMap, "BRIEFNAME");
            // если пришел тот же выгодоприобретатель что уже был значит добавляем новый риск, иначе
            // ничего не делаем, т.к. для массового сегмента может быть только один выгодоприобретатель
            if (briefName.equals(generateBriefName(personMap))
                    || getStringParam(personMap, "BRIEFNAME").equals(briefName)) {
                addRiskType(changeBenefDataMap, reasonMap);
            }
        }
    }

    private String generateBriefName(Map<String, Object> personMap) {
        StringBuilder briefName = new StringBuilder();
        String lastName = getStringParam(personMap, "surname");
        if (!lastName.isEmpty()) {
            briefName.append(lastName);
        }

        String firstName = getStringParam(personMap, "name");
        if (!firstName.isEmpty()) {
            briefName.append(" ").append(firstName.charAt(0)).append(".");
        }

        String middleName = getStringParam(personMap, "patronymic");
        if (!middleName.isEmpty()) {
            briefName.append(" ").append(middleName.charAt(0)).append(".");
        }

        return briefName.toString();
    }

    private void addRiskType(Map<String, Object> changeBenefDataMap, Map<String, Object> reasonMap) {
        String riskCode = getStringParam(reasonMap, "riskCode");
        if (riskCode.contains("DISABILITY")) {
            // DISABILITYRISK - инвалидность
            changeBenefDataMap.put("DISABILITYRISK", TRUE_STR_VALUE);
        }
        if (riskCode.contains("DEATH")) {
            // DEATHRISK - смерть
            changeBenefDataMap.put("DEATHRISK", TRUE_STR_VALUE);
        }
        if (riskCode.contains("LIFE")) {
            // SURVIVALRISK - дожитие
            changeBenefDataMap.put("SURVIVALRISK", TRUE_STR_VALUE);
        }
    }
}
