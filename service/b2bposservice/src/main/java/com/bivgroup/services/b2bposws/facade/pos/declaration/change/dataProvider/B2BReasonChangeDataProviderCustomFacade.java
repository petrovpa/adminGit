package com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider;

import com.bivgroup.services.b2bposws.facade.pos.declaration.B2BDeclarationBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.declaration.utils.MappingHelper;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.system.dispatcher.ServiceInvocationException;

import java.util.*;

import static java.util.stream.Collectors.joining;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

public class B2BReasonChangeDataProviderCustomFacade extends B2BDeclarationBaseFacade {
    // тексты ошибок
    private static final String DEFAULT_DATA_PROVIDER_ERROR_MSG = "Не удалось подготовить данные для формирования заявления на изменение условий страхования: ";

    private static final String EXTATTRLIST_MAP_PARAMNAME = "extAttributeList2";
    private static final String INSURER_MAP_PARAMNAME = "INSURERMAP";
    private static final String APPLICANT_MAP_PARAMNAME = "APPLICANTMAP";
    private static final String CREATEUSER_MAP_PARAMNAME = "CREATEUSERMAP";
    private static final String TRANCHE_MAP_PARAMNAME = "TRANCHELIST";
    private static final String VIP_POSRTFIX_NAME = "_ISVIP";

    protected static final String INSURED_MAP_PARAMNAME = "INSUREDMAP";
    protected static final String REPORT_DATA_PARAM_NAME = "REPORTDATA";
    protected static final String REASON_PARAM_NAME = "REASONMAP";
    protected static final String TRUE_STR_VALUE = "TRUE";
    protected static final String FALSE_STR_VALUE = "FALSE";
    protected static final String EMPTY_STRING = "";
    protected static final String REPLEVEL_FOR_SPECIFICATION_TWO = "1000000";

    public static final String DECLARATION_OF_CHANGE_ID_PARAMNAME = "id";

    private final Logger logger = Logger.getLogger(this.getClass());

    protected void addReasonType(Map<String, Object> reasonMap, String typeSysname) {
        String type = getStringParam(reasonMap, "type");
        if (type.isEmpty()) {
            reasonMap.put("type", typeSysname);
        } else {
            if (!type.contains(typeSysname)) {
                type += ',' + typeSysname;
                reasonMap.put("type", type);
            }
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BDeclarationOfChangeReportDataProvider(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BDeclarationOfChangeReportDataProvider begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        String errorDefault = DEFAULT_DATA_PROVIDER_ERROR_MSG;
        String error = "";
        // мапа основного результата работы провайдера - REPORTDATA
        Map<String, Object> reportData = new HashMap<>();
        // Страхователь
        Map<String, Object> applicantMap = getOrCreateMapParam(params, INSURER_MAP_PARAMNAME);
        Map<String, Object> insuredMap = getMapParam(params, INSURED_MAP_PARAMNAME);
        if (insuredMap != null) {
            reportData.put(INSURED_MAP_PARAMNAME, insuredMap);
        } else {
            reportData.put(INSURED_MAP_PARAMNAME, applicantMap);
        }
        reportData.put(INSURER_MAP_PARAMNAME, applicantMap);
        reportData.put(APPLICANT_MAP_PARAMNAME, applicantMap);
        // ВСП
        Map<String, Object> createUserMap = getOrCreateMapParam(params, CREATEUSER_MAP_PARAMNAME);
        reportData.put(CREATEUSER_MAP_PARAMNAME, createUserMap);
        // ИД заявления
        Long declarationId = getLongParamLogged(params, DECLARATION_OF_CHANGE_ID_PARAMNAME);
        // заявление
        Map<String, Object> declaration = dctFindById(DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME, declarationId, isCallFromGate);
        Map<String, Object> interfaceDeclarationMap = getMapParam(params, DECLARATION_MAP_PARAMNAME);
        if ((declaration == null) || (declaration.isEmpty())) {
            declaration = interfaceDeclarationMap;
        }
        if (!declaration.isEmpty()) {
            reportData.putAll(declaration);
        } else {
            error = "Не удалось загрузить сведения заявления на изменение условий страхования!";
        }
        // информация об дате окончания
        if (error.isEmpty()) {
            if (reportData.get("supposedDateOfEntry") != null) {
                reportData.put("ACCEPTDATE", reportData.get("supposedDateOfEntry"));
            }
        }
        Long contractId = null;
        Long externalId = null;
        boolean isVipSegment = false;
        boolean isNotExistContract = getBooleanParam(params, "isNotExistContract", false);
        if (error.isEmpty()) {
            contractId = getLongParamLogged(declaration, "contractId");
            Map<String, Object> contract;
            if (contractId != null) {
                Map<String, Object> contractParams = new HashMap<>();
                contractParams.put("CONTRID", contractId);
                contractParams.put(RETURN_AS_HASH_MAP, true);
                contract = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParam", contractParams, login, password);
                if (!isCallResultOKAndContainsLongValue(contract, "CONTRID", contractId)) {
                    error = "Не удалось загрузить сведения договора страхования, указанного в заявлении на изменение условий страхования!";
                }
            } else {
                contract = getOrCreateMapParamLogged(declaration, "contrId_EN");
            }
            // данные договора в reportData
            String contractSeries = getStringParamLogged(contract, "CONTRPOLSER");
            String contractNumber = getStringParamLogged(contract, "CONTRPOLNUM");
            String contractFullNumber = getStringParamLogged(contract, "CONTRNUMBER");
            // если у нас нет номера договора (CONTRPOLNUM) в договоре, то присваиваем
            // (CONTRNUMBER) т.к это параметр приходит с интерфейса
            if (contractNumber.isEmpty()) {
                contractNumber = contractFullNumber;
            }
            Long prodProgId = getLongParamLogged(contract, "PRODPROGID");
            String prodProgSysName = "";
            if (prodProgId != null) {
                Map<String, Object> progrmParam = new HashMap<>();
                progrmParam.put("PRODPROGID", prodProgId);
                prodProgSysName = getStringParam(
                        this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME,
                                "dsB2BProductProgramBrowseListByParam",
                                progrmParam, login, password, "SYSNAME")
                );
            } else {
                prodProgSysName = getStringParam(contract, "PRODPROGSYSNAME");
            }
            reportData.put("PRODPROGSYSNAME", prodProgSysName);
            if (contractSeries.isEmpty() || contractNumber.isEmpty() && !contractFullNumber.isEmpty()) {
                String splitChar = " ";
                if (contractFullNumber.contains("№")) {
                    splitChar = "№";
                } else {
                    if (contractFullNumber.contains("/")) {
                        splitChar = "/";
                    } else {
                        if (contractFullNumber.contains("_")) {
                            splitChar = "_";
                        } else {
                            if (contractFullNumber.contains("-")) {
                                splitChar = "-";
                            }
                        }
                    }
                }
                String[] contractFullNumberSplit = contractFullNumber.split(splitChar);
                if (contractFullNumberSplit.length == 2) {
                    contractSeries = contractFullNumberSplit[0].trim();
                    contractNumber = contractFullNumberSplit[1].trim();
                }
            }
            if (!contractNumber.isEmpty() && contractNumber.equals(contractFullNumber)) {
                // CONTRPOLNUM и CONTRNUMBER содержат одинаковое значение - договор из старой версии интеграции и следует вычислить номер по полной строке
                if (contractNumber.startsWith(contractSeries)) {
                    String contractNumberReal = contractNumber.substring(contractSeries.length()).replaceAll("№", "").trim();
                    logger.debug(String.format(
                            "Real contract number '%s' was resolved from full contract number '%s' considering series '%s'.",
                            contractNumberReal, contractNumber, contractSeries
                    ));
                    contractNumber = contractNumberReal;
                }
            }

            reportData.put("CONTRPOLSER", contractSeries);
            reportData.put("CONTRPOLNUM", contractNumber);
            reportData.put("CONTRNUMBER", contractFullNumber);
            reportData.put("CONTRNUMBERANSER", contractSeries + " № " + contractNumber);
            Object documentDate = contract.get("DOCUMENTDATE");
            if (documentDate == null) {
                // дата оформления в договоре не найдена - следует использовать переданную с интерфейса при создании заявления
                documentDate = declaration.get("contractDate");
            }
            reportData.put("DOCUMENTDATE", documentDate);
            reportData.put("PREMCURRENCYID", contract.get("PREMCURRENCYID"));
            reportData.put("STARTDATE", contract.get("STARTDATE"));
            // todo: доп. данные договора, требующиеся в reportData для ПФ

            externalId = getLongParam(contract.get("EXTERNALID"));

            Long contractProdVerId = getLongParamLogged(contract, "PRODVERID");
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", contractProdVerId);
            Long prodConfId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME,
                    "dsB2BProductConfigBrowseListByParam", configParams,
                    login, password, "PRODCONFID")
            );
            configParams.put(RETURN_AS_HASH_MAP, true);

            Map<String, Object> productShortInfo = this.callServiceLogged(B2BPOSWS_SERVICE_NAME,
                    "dsB2BProductVersionBrowseListByParamEx", configParams, login, password);
            reportData.put("PRODSYSNAME", getStringParam(productShortInfo, "PRODSYSNAME"));
            reportData.put("PRODNAME", getStringParam(productShortInfo, "PRODNAME"));
            reportData.put("PRODCONFID", prodConfId);
            String findIsVipSegment = prodProgSysName + VIP_POSRTFIX_NAME;
            Map<String, Object> prodDefValuQuerry = new HashMap<>();
            prodDefValuQuerry.put("PRODCONFID", prodConfId);
            prodDefValuQuerry.put("NAME", findIsVipSegment);
            List<Map<String, Object>> ropDefValList = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME,
                    "dsB2BProductDefaultValueBrowseListByParam",
                    prodDefValuQuerry, login, password);
            if (!ropDefValList.isEmpty()) {
                isVipSegment = getBooleanParam(ropDefValList.get(0), "VALUE", false);
                reportData.put("ISVIPSEGMENT", isVipSegment);
            }
        }

        Long clientId = null;
        if (error.isEmpty()) {
            // Загрузка информации о страхователе
            String applicantKeyName = "applicantId_EN";
            Map<String, Object> applicantId_EN = getOrCreateMapParam(declaration, applicantKeyName);
            loggerDebugPretty(logger, DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME + ".applicantId_EN", applicantId_EN);
            updateReportPersonMapByClientMap(applicantMap, applicantId_EN, isNotExistContract, login, password);
            clientId = getLongParam(applicantId_EN, "clientId");
            if (clientId != null) {
                boolean isPpoOnline = isPpoOnline(clientId);
                List<Map<String, Object>> reasonChangeForContractId_ENs = getOrCreateListParam(declaration, "reasons");
                boolean kindChangeReasonRequiresElectronicStamp = reasonChangeForContractId_ENs.stream().anyMatch(reasonChangeForContractId_EN -> {
                    Map<String, Object> kindChangeReasonId_EN = getOrCreateMapParam(reasonChangeForContractId_EN, "kindChangeReasonId_EN");
                    String kindChangeReasonSysname = getStringParam(kindChangeReasonId_EN, "sysname");
                    // whitelisting
                    return kindChangeReasonSysname.matches(
                            "ReasonChangeForContract_Instalments|" +
                                    "ReasonChangeForContract_IncreaseInsSum|ReasonChangeForContract_DecreaseInsSum|" +
                                    "ReasonChangeForContract_IncludePrograms|ReasonChangeForContract_ExcludePrograms|" +
                                    "ReasonChangeForContract_IncreasePeriod|ReasonChangeForContract_DecreasePeriod|" +
                                    "ReasonChangeForContract_TransferToPaid|" +
                                    "ReasonChangeForContract_FinancialVacation|ReasonChangeForContract_ExitFinancialVacation|" +
                                    "ReasonChangeForContract_ExtPremPay|" +
                                    "ReasonChangeForContract_ChangeFund|" +
                                    "ReasonChangeForContract_FixIncome|" +
                                    "ReasonChangeForContract_WithdrawIncome");
                });
                String prodProgSysname = getStringParam(reportData, "PRODPROGSYSNAME");

                // #16697 "Список продуктов, по которым *не* должно включаться ППО-онлайн:"
                // blacklisting
                boolean programAllowsElectronicTimestamp = !prodProgSysname.matches(
                        // 1 Новые горизонты;
                        "NH_BASIC|" +
                                // 2 Капитал;
                                "ACCELERATION_RB-FL|" +
                                // 3 Маяк;
                                "LIGHTHOUSE|" +
                                // 4 Ремень безопасности;
                                "SBELT_RTBOX|SB_BASIC|" +
                                // 5 Верный выбор;
                                "RIGHT_CHOICE_RTBOX|RC_BASIC|" +
                                // 6 Маяк купонный;
                                "SMART_POLICY_RB_ILIK|" +
                                // 7 Заботливые родители
                                "FUTURE_TICKET|" +
                                // 8 Верное решение
                                "FAMALYASSETS_RB-FCC0|" +
                                // 9 Первый шаг
                                "FIRSTCAPITAL_RB-FCC0"
                );

                if (isPpoOnline && kindChangeReasonRequiresElectronicStamp && programAllowsElectronicTimestamp) {
                    Map<String, Object> stamp = new HashMap<>();
                    stamp.put("surname", applicantId_EN.get("surname"));
                    stamp.put("name", applicantId_EN.get("name"));
                    stamp.put("middlename", applicantId_EN.get("patronymic"));

                    reportData.put("STAMP", stamp);
                    reportData.put("enableUserStamp", true);
                }
            }
        }

        if (error.isEmpty()) {
            // Загрузка траншей
            Map<String, Object> trParams = new HashMap<>();
            GregorianCalendar gcDate = new GregorianCalendar();
            gcDate.setTime(new Date());
            gcDate.set(Calendar.DAY_OF_YEAR, 1);
            gcDate.set(Calendar.HOUR_OF_DAY, 0);
            gcDate.set(Calendar.MINUTE, 0);
            gcDate.set(Calendar.SECOND, 0);
            gcDate.set(Calendar.MILLISECOND, 0);
            trParams.put("TRANCHESTARTDATE", gcDate.getTime());
            gcDate.set(Calendar.MONTH, Calendar.DECEMBER);
            gcDate.set(Calendar.DAY_OF_MONTH, 31);
            trParams.put("TRANCHEFINISHDATE", gcDate.getTime());
            trParams.put("ORDERBY", "T.SALESTARTDATE ASC");
            Map<String, Object> trancheMap = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BInvestTrancheBrowseListByParamToDataProv", trParams, login, password);
            if (trancheMap != null) {
                reportData.put(TRANCHE_MAP_PARAMNAME, trancheMap.get(RESULT));
            }
        }

        List<Map<String, Object>> reasonList = new ArrayList<>();
        if (error.isEmpty()) {
            reasonList = getListParam(declaration, "reasons");
            reportData.put("reasons", reasonList);
            Map<String, Map<String, Object>> reasonMap = getMapByFieldStringValues(reasonList, "kindChangeReasonId_EN", "sysname");
            if (reasonMap.isEmpty()) {
                logger.error(String.format(
                        "Unable to get change reason from declaration of change map! Details (declaration map): %s.",
                        declaration
                ));
                error = "Не удалось определить список изменений по данному заявлению!";
            }
        }

        if (error.isEmpty()) {
            Map<String, Object> showFormMap = getOrCreateMapParam(reportData, "SHOWFORMMAP");
            showFormMap.put("FORM1IND", FALSE_STR_VALUE);
            showFormMap.put("DECL", FALSE_STR_VALUE);
            showFormMap.put("FORM1JUR", FALSE_STR_VALUE);
            showFormMap.put("FORM2", FALSE_STR_VALUE);
            showFormMap.put("FORM3", FALSE_STR_VALUE);
            showFormMap.put("FORM4", FALSE_STR_VALUE);
            showFormMap.put("FORM5", FALSE_STR_VALUE);
            showFormMap.put("FORM6", FALSE_STR_VALUE);
            showFormMap.put("FORM7", FALSE_STR_VALUE);
            showFormMap.put("FORM8", FALSE_STR_VALUE);
            showFormMap.put("FORM9", FALSE_STR_VALUE);
            showFormMap.put("FORM10", FALSE_STR_VALUE);
            showFormMap.put("FORM11", FALSE_STR_VALUE);
            showFormMap.put("FORM12", FALSE_STR_VALUE);
            showFormMap.put("FORM13", FALSE_STR_VALUE);
            showFormMap.put("FORM14", FALSE_STR_VALUE);
        }
        List<String> repLevelList = new ArrayList<>();
        if (error.isEmpty()) {
            Map<String, Object> kindChangeReason, kindDeclaration, changeReasonDataProviderQueryParams;
            String kindChangeReasonName, kindChangeReasonSysName, binDocTypeSysname;
            for (Map<String, Object> reason : reasonList) {
                kindDeclaration = getMapParam(reason, "kindDeclarationId_EN");
                binDocTypeSysname = getStringParam(kindDeclaration, "BINDOCTYPESYSNAME");
                if (binDocTypeSysname.isEmpty()) {
                    reportData.put("BINDOCTYPESYSNAME", getStringParam(kindDeclaration, "sysname") + "_Pf");
                }
                kindChangeReason = getMapParam(reason, "kindChangeReasonId_EN");
                kindChangeReasonSysName = getStringParamLogged(kindChangeReason, "sysname");
                kindChangeReasonName = getStringParamLogged(kindChangeReason, "name");
                kindChangeReasonSysName = kindChangeReasonSysName.replace("ReasonChangeForContract_", "");
                reportData.put("REASONSYSNAME", kindChangeReasonSysName);
                String changeReasonDataProviderMethodName = String.format(
                        "dsB2BChangeReasonDataProvider%s", kindChangeReasonSysName);
                Map<String, Object> dataProviderResult = null;
                try {
                    changeReasonDataProviderQueryParams = new HashMap<>();
                    changeReasonDataProviderQueryParams.put("EXTERNALID", externalId);
                    changeReasonDataProviderQueryParams.put("REASONMAP", reason);
                    changeReasonDataProviderQueryParams.put("REPORTDATA", reportData);
                    changeReasonDataProviderQueryParams.put("docReceiptMap", getMapParam(interfaceDeclarationMap, "docReceiptMap"));
                    changeReasonDataProviderQueryParams.put("clientId", clientId);
                    changeReasonDataProviderQueryParams.put("isNotExistContract", isNotExistContract);
                    changeReasonDataProviderQueryParams.put(IS_CALL_FROM_GATE_PARAMNAME, isCallFromGate);
                    changeReasonDataProviderQueryParams.put(RETURN_AS_HASH_MAP, TRUE_STR_VALUE);
                    dataProviderResult = callServiceLogged(
                            THIS_SERVICE_NAME, changeReasonDataProviderMethodName,
                            changeReasonDataProviderQueryParams, login, password
                    );
                    dataProviderResult = getMapParam(dataProviderResult, "REPORTDATA");
                    error = getStringParam(dataProviderResult, ERROR);
                    if (error.isEmpty()) {
                        String repLevel = getStringParam(reportData, "REPLEVEL");
                        if (!repLevelList.contains(repLevel)) {
                            repLevelList.add(repLevel);
                        }
                        reportData.putAll(dataProviderResult);
                    } else {
                        break;
                    }
                } catch (ServiceInvocationException ex) {
                    // формирование данных для отчета - обязательный метод, но может быть не объявлен разработчиком для новых опций
                    // (в этом случае следует отобразить и запротоколировать отдельную ошибку)
                    String exMsg = ex.getMessage();
                    String exNote;
                    if ((exMsg != null) && (exMsg.contains("Unable to locate service implementation"))) {
                        dataProviderResult = new HashMap<>();
                        dataProviderResult.put(RET_STATUS, RET_STATUS_OK);
                        error = "Для опции " + kindChangeReasonName + " невозможно сформировать данные для " +
                                "печатной формы - метод по формированию данных для печатной формы данного вида ('"
                                + changeReasonDataProviderMethodName + "') не реализован!";
                        exNote = "Method " + changeReasonDataProviderMethodName
                                + " required for forming reports data change reason (with entity name "
                                + kindChangeReasonSysName + ") not found! Details: ";
                        logger.error(exNote, ex);
                    } else {
                        exNote = "Calling method " + changeReasonDataProviderMethodName
                                + " for forming reports data  change reason (with entity name "
                                + kindChangeReasonSysName + ") exception! Details: ";
                        logger.error(exNote, ex);
                        throw ex;
                    }
                }
            }
            reportData.put("REPLEVEL",
                    repLevelList.stream()
                            .filter(s -> s != null && !s.isEmpty())
                            .collect(joining(","))
            );
        }

        Map<String, Object> personMap = getMapParam(reportData, "PERSONMAP");
        if (error.isEmpty() && isVipSegment && personMap != null) {
            String newLastName = getStringParam(personMap, "LASTNAME");
            String newFirstName = getStringParam(personMap, "FIRSTNAME");
            String newMiddleName = getStringParam(personMap, "MIDDLENAME");
            if (newLastName.isEmpty()) {
                String oldLastName = getStringParam(personMap, "OLDLASTNAME");
                newLastName = oldLastName;
                personMap.put("LASTNAME", oldLastName);
            }
            if (newFirstName.isEmpty()) {
                String oldFirstName = getStringParam(personMap, "OLDFIRSTNAME");
                newFirstName = oldFirstName;
                personMap.put("FIRSTNAME", oldFirstName);
            }
            if (newMiddleName.isEmpty()) {
                String oldMiddleName = getStringParam(personMap, "OLDMIDDLENAME");
                newMiddleName = oldMiddleName;
                personMap.put("MIDDLENAME", oldMiddleName);
            }

            StringBuilder briefName = new StringBuilder();
            if (!newLastName.isEmpty()) {
                briefName.append(newLastName);
            }

            if (!newFirstName.isEmpty()) {
                briefName.append(" ").append(newFirstName.charAt(0)).append(".");
            }

            if (!newMiddleName.isEmpty()) {
                briefName.append(" ").append(newMiddleName.charAt(0)).append(".");
            }

            personMap.put("BRIEFNAME", briefName.toString());

            getOrCreateListParam(personMap, "addressList");
            getOrCreateListParam(personMap, "documentList");
            getOrCreateListParam(personMap, "contactList");
        }

        if (error.isEmpty()) {
            Date todayDate = new Date();
            reportData.put("TODAYDATE", todayDate);
            // генерация строковых представлений для всех дат
            genDateStrs(reportData);
        }

        // формирование результата
        Map<String, Object> result = new HashMap<>();
        if (error.isEmpty()) {
            // формирование результата
            result.put("REPORTDATA", reportData);
        } else {
            errorDefault += error;
            result.put(ERROR, errorDefault);
        }
        loggerDebugPretty(logger, "dsB2BDeclarationOfChangeReportDataProvider result", result);
        logger.debug("dsB2BDeclarationOfChangeReportDataProvider end");
        return result;
    }

    boolean isPpoOnline(Long clientId) throws Exception {
        Map<String, Object> findBy = new HashMap<>();
        findBy.put("clientId", clientId);
        List<Map<String, Object>> profiles = dctFindByExample(CLIENT_PROFILE_ENTITY_NAME, findBy);
        Map<String, Object> profile0 = profiles.get(0);
        String ppoOnlineType = (String) profile0.get("ppoOnlineType");
        return ppoOnlineType != null && ppoOnlineType.matches("YES_CLIENT_CABINET|YES_EMPLOYEE|YES_FROM_REGISTER");
    }

    protected void updateReportPersonMapByClientMap(Map<String, Object> reportPerson, Map<String, Object> client,
                                                    boolean isNotExistContract, String login, String password) throws Exception {
        if ((reportPerson == null) || (client == null)) {
            return;
        }
        // фамилия заявителя
        reportPerson.put("LASTNAME", getStringParam(client, "surname"));
        // имя заявителя
        reportPerson.put("FIRSTNAME", getStringParam(client, "name"));
        // отчество заявителя
        reportPerson.put("MIDDLENAME", getStringParam(client, "patronymic"));
        // фамилия и. о. заявителя
        Map<String, Object> namesMap = getClientNamesMap(client);
        reportPerson.put("BRIEFNAME", namesMap.get("fullNameAbbr"));
        // дата рождения заявителя
        reportPerson.put("BIRTHDATE", getDateDctValue(client, "dateOfBirth"));
        //

        // Контактные данные заявителя (мобильный, дополнительный, email); аналогично той структуре, которая лежит в PARTICIPANTMAP
        // Используемые системные наименования: Email = PersonalEmail, Мобильный = MobilePhone, Дополнительный = FactAddressPhone"
        List<Map<String, Object>> clientContactList = getOrCreateListParam(client, "contacts");
        List<Map<String, Object>> contactList = resolveClientContactList(clientContactList);
        if (contactList.isEmpty()) {
            contactList.add(new HashMap<>());
        }
        reportPerson.put("contactList", contactList);
        // Адреса заявителя; аналогично той структуре, которая лежит в PARTICIPANTMAP
        List<Map<String, Object>> clientAddressList = getOrCreateListParam(client, "addresses");
        List<Map<String, Object>> addressList = resolveClientAddressList(clientAddressList);
        if (addressList.isEmpty()) {
            addressList.add(new HashMap<>());
        }
        reportPerson.put("addressList", addressList);
        // гражданство заявителя
        Map<String, Object> country = getMapParam(client, "countryId_EN");
        if (country == null && !isNotExistContract) {
            Long countryId = getLongParamLogged(client, "countryId");
            country = getCountryInfo(countryId);
        }
        String citizenship = mappingSitizenship(reportPerson, country);

        // реквизиты документа заявителя (в т.ч. миграционная карта и разрешение на пребывание); аналогично той структуре, которая лежит в PARTICIPANTMAP
        List<Map<String, Object>> clientDocumentList = getOrCreateListParam(client, "documents");
        List<Map<String, Object>> documentList = resolveClientDocumentList(clientDocumentList, citizenship);
        if (documentList.isEmpty()) {
            documentList.add(new HashMap<>());
        }
        reportPerson.put("documentList", documentList);

        //stub extattr
        List<Map<String, Object>> extAttrList = getOrCreateListParam(client, EXTATTRLIST_MAP_PARAMNAME);
        reportPerson.put(EXTATTRLIST_MAP_PARAMNAME, extAttrList);
        // СНИЛС персоны
        reportPerson.put("SNILS", getStringParam(client, "snils"));

        mappingResidentInfo(reportPerson, client, isNotExistContract);

        Long kinshipId = getLongParam(client, "kinshipId");
        if (kinshipId != null) {
            reportPerson.put("RELATIONSHIP", getKinshipName(kinshipId, login, password));
        }
        // Реквизиты документа, подтверждающего полномочия (для Представителей) - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("REPRESENTDOCINFO", "");
        // Тип заявителя (лично, представитель, иное) - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("TYPESTR", "");
    }

    protected void mappingResidentInfo(Map<String, Object> reportPerson, Map<String, Object> client, boolean isNotExistContract) {
        MappingHelper.requisitesMapping(reportPerson, client, isNotExistContract);
    }

    protected String mappingSitizenship(Map<String, Object> reportPerson, Map<String, Object> country) {
        if (country == null) {
            country = new HashMap<>();
        }
        String citizenshipStr = getStringParamLogged(country, "countryName");
        reportPerson.put("CITIZENSHIPSTR", citizenshipStr);
        // по умолчанию будем считать гражданство РФ
        String citizenship = EMPTY_STRING;
        String countryCode = EMPTY_STRING;
        if (!country.isEmpty()) {
            citizenship = "0";
            countryCode = "RUS";
            String alphaCode3 = getStringParamLogged(country, "alphaCode3"); // RUS - Российская Федерация и т.д.
            if (!alphaCode3.isEmpty()) {
                countryCode = alphaCode3;
            }
            // если после запроса по countryId alphaCode3 не пусто
            // и не равно русскому, тогда присваиваем 1
            if (!countryCode.isEmpty() && !"RUS".equalsIgnoreCase(countryCode)) {
                citizenship = "1";
            }
        }
        reportPerson.put("CITIZENSHIP", citizenship);
        reportPerson.put("COUNTRYCODE", countryCode);
        return citizenship;
    }

    protected Map<String, Object> getCountryInfo(Long countryId) {
        Map<String, Object> result = new HashMap<>();
        if (countryId == null) {
            countryId = 1L; // todo: в константы (а мб такая коснтанта уже есть, нужно найти)
        }
        try {
            result = dctFindById(KIND_COUNTRY_ENTITY_NAME, countryId);
        } catch (Exception ex) {
            logger.error(String.format(
                    "Unable to get country info by dctFindById from '%s' with id = %d - exception was thrown:",
                    KIND_COUNTRY_ENTITY_NAME, countryId
            ), ex);
        }
        return result;
    }

    private String getKinshipName(Long kinshipId, String login, String password) throws Exception {
        Map<String, Object> kinshipQuerryParams = new HashMap<>();
        kinshipQuerryParams.put("HANDBOOKNAME", "B2B.Life.Kinship");
        kinshipQuerryParams.put(RETURN_LIST_ONLY, true);
        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("hid", kinshipId);
        kinshipQuerryParams.put("HANDBOOKDATAPARAMS", hbParams);
        List<Map<String, Object>> kinshipList = this.callServiceAndGetListFromResultMap(Constants.B2BPOSWS,
                "dsB2BHandbookDataBrowseByHBName", kinshipQuerryParams, login, password);
        String result = "";
        if (!kinshipList.isEmpty()) {
            result = getStringParam(kinshipList.get(0), "name");
        }
        return result;
    }

    private Object getDateDctValue(Map<String, Object> sourceMap, String dateKeyName) {
        Object dateObj = null;
        if (sourceMap != null) {
            dateObj = sourceMap.get(dateKeyName);
            if (dateObj == null) {
                dateObj = sourceMap.get(dateKeyName + "$date");
            }
        }
        return dateObj;
    }

    protected List<Map<String, Object>> resolveClientDocumentList(List<Map<String, Object>> documentList, String citizenship) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> item;
        // системное наименование типа требуемого документа
        String docTypeSysName = citizenship.equals("1") ? "ForeignPassport" : "PassportRF";
        // системное наименование типа противоположного документа
        String oppositeDocTypeSysName = citizenship.equals("1") ? "PassportRF" : "ForeignPassport";
        if (documentList != null && !documentList.isEmpty()) {
            Map<String, Map<String, Object>> clientItemByType = formationMapMapByList(documentList);
            String itemTypeSysName, itemTypeName;
            Map<String, Object> clientItem, itemType;
            boolean isDocTypeExist = false; // флаг, сообщающий о том найденны
            Map<String, Object> copyDoc = new HashMap<>(); // мапа для копирования противоположного документа
            for (Map.Entry<String, Map<String, Object>> clientItemEntry : clientItemByType.entrySet()) {
                itemTypeSysName = clientItemEntry.getKey();
                if (itemTypeSysName.equals(docTypeSysName)) {
                    isDocTypeExist = true;
                }
                clientItem = clientItemEntry.getValue();
                item = new HashMap<>(clientItem);
                itemType = getMapParam(clientItem, "typeId_EN");
                itemTypeName = getStringParam(itemType, "name");
                // документ
                item.put("DOCTYPESYSNAME", itemTypeSysName);
                item.put("DOCTYPENAME", itemTypeName);
                item.put("DOCNUMBER", clientItem.get("no"));
                item.put("DOCSERIES", clientItem.get("series"));
                item.put("ISSUEDATE", getDateDctValue(clientItem, "dateOfIssue"));
                item.put("ISSUEDBY", clientItem.get("authority"));
                item.put("ISSUERCODE", clientItem.get("issuerCode"));
                item.put("VALIDFROMDATE", clientItem.get("dateStayFrom")); // 'Срок пребывания с'
                item.put("VALIDTODATE", clientItem.get("dateStayUntil")); // 'Срок пребывания по'
                if (!isDocTypeExist && itemTypeSysName.equals(oppositeDocTypeSysName)) {
                    copyDoc.putAll(item);
                }
                resultList.add(item);
            }
            // если требуемый согласно гржданству документ отсутствует, тогда
            // копируем имеющийся и подменяем ему DOCTYPESYSNAME
            // для того чтобы вывелось в формуле
            if (!isDocTypeExist) {
                copyDoc.put("DOCTYPESYSNAME", docTypeSysName);
                resultList.add(copyDoc);
            }
        } else {
            // если список документов отсутствует то создадим один пустой документ по гражданству
            // только если гражданство существует
            if (!citizenship.isEmpty()) {
                item = new HashMap<>();
                String docTypeName = citizenship.equals("1") ? "Паспорт иностранного гражданина"
                        : "Паспорт гражданина РФ";
                item.put("DOCTYPESYSNAME", docTypeSysName);
                item.put("DOCTYPENAME", docTypeName);
                resultList.add(item);
            }
        }
        return resultList;
    }

    protected List<Map<String, Object>> resolveClientAddressList(List<Map<String, Object>> addressList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (addressList != null) {
            Map<String, Map<String, Object>> clientItemByType = formationMapMapByList(addressList);
            String itemTypeSysName, itemTypeName;
            Map<String, Object> clientItem, item, itemType;
            for (Map.Entry<String, Map<String, Object>> clientItemEntry : clientItemByType.entrySet()) {
                itemTypeSysName = clientItemEntry.getKey();
                clientItem = clientItemEntry.getValue();
                item = new HashMap<>(clientItem);
                itemType = getMapParam(clientItem, "typeId_EN");
                itemTypeName = getStringParam(itemType, "name");
                // адрес
                item.put("ADDRESSTYPESYSNAME", itemTypeSysName);
                item.put("ADDRESSTYPENAME", itemTypeName);
                item.put("CITY", clientItem.get("city"));
                item.put("REGION", clientItem.get("region"));
                item.put("STREET", clientItem.get("street"));
                item.put("HOUSE", clientItem.get("house"));
                item.put("FLAT", clientItem.get("flat"));

                // исправляем случай с двумя индексами в адресе
                String POSTALCODE = getStringParam(clientItem, "postcode");
                String ADDRESSTEXT1 = getStringParam(clientItem, "address");
            //    if (!POSTALCODE.isEmpty() && ADDRESSTEXT1.startsWith(POSTALCODE)) {
            //        ADDRESSTEXT1 = ADDRESSTEXT1.replaceFirst(POSTALCODE + ",", "");
            //    }

                item.put("ADDRESSTEXT1", ADDRESSTEXT1);
                item.put("ADDRESSTEXT2", clientItem.get("address"));
                item.put("ADDRESSTEXT3", clientItem.get("address2"));

                item.put("POSTALCODE", clientItem.get("postcode"));
                resultList.add(item);
            }
        }
        return resultList;
    }

    protected List<Map<String, Object>> resolveClientContactList(List<Map<String, Object>> contactList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (contactList != null) {
            Map<String, Map<String, Object>> clientItemByType = formationMapMapByList(contactList);
            String itemTypeSysName, itemTypeName;
            Map<String, Object> clientItem, item, itemType;
            for (Map.Entry<String, Map<String, Object>> clientItemEntry : clientItemByType.entrySet()) {
                itemTypeSysName = clientItemEntry.getKey();
                clientItem = clientItemEntry.getValue();
                item = new HashMap<>(clientItem);
                itemType = getMapParam(clientItem, "typeId_EN");
                itemTypeName = getStringParam(itemType, "name");
                // контакт
                item.put("CONTACTTYPESYSNAME", itemTypeSysName);
                item.put("CONTACTTYPENAME", itemTypeName);
                item.put("VALUE", clientItem.get("value"));
                resultList.add(item);
            }
        }
        return resultList;
    }

    private Map<String, Map<String, Object>> formationMapMapByList(List<Map<String, Object>> list) {
        Map<String, Map<String, Object>> clientItemByType = new HashMap<>();
        for (Map<String, Object> clientItem : list) {
            Map<String, Object> itemType = getMapParam(clientItem, "typeId_EN");
            String itemTypeSysName = getStringParam(itemType, "sysname");
            if (!itemTypeSysName.isEmpty()) {
                Long isPrimary = getLongParam(clientItem, "isPrimary");
                if ((BOOLEAN_FLAG_LONG_VALUE_TRUE.equals(isPrimary)) || (clientItemByType.get(itemTypeSysName) == null)) {
                    // если по данному типу не было других элементов или если проверямый элемент имеет признак isPrimary - выбор элемента как используемого для данного типа
                    clientItemByType.put(itemTypeSysName, clientItem);
                }
            }
        }
        return clientItemByType;
    }

    protected Map<String, Object> getFullContractInfo(Long externalId, String login, String password) throws Exception {
        Map<String, Object> fullContract;
        Map<String, Object> fullContractQueryParams = new HashMap<>();
        fullContractQueryParams.put("POLICYID", externalId);
        fullContractQueryParams.put(RETURN_AS_HASH_MAP, TRUE_STR_VALUE);
        fullContract = callExternalServiceLogged(THIS_SERVICE_NAME, "dsLifeIntegrationGetContractList", fullContractQueryParams, login, password);
        String error = "";
        if (isCallResultOK(fullContract)) {
            fullContract = getMapParam(fullContract, CONTRACT_MAP_PARAMNAME);
            if (!externalId.equals(getLongParam(fullContract, "POLICYID"))) {
                error = "Не удалось получить полную информацию по договору!";
            }
        } else {
            error = "Не удалось получить полную информацию по договору!";
        }
        fullContract.put(ERROR, error);
        return fullContract;
    }
}
