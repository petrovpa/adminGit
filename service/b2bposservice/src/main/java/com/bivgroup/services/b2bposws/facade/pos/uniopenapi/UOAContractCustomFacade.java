package com.bivgroup.services.b2bposws.facade.pos.uniopenapi;

import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bivgroup.services.b2bposws.facade.pos.uniopenapi.UOAConstants.*;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("UOAContractCustom")
public class UOAContractCustomFacade extends UOABaseFacade {

    private static final Map<String, List<String>> CONTRACT_TRANS_STATES_BY_TARGET_STATE;

    // "словарь" для мужского поля и женского для isMarried = true
    private static final Map<Long, Map<String, Object>> maritalStatusForOpenApiNotMarried;
    // "словарь" для мужского поля и женского для isMarried = false
    private static final Map<Long, Map<String, Object>> maritalStatusForOpenApiMarried;

    static {
        maritalStatusForOpenApiMarried = new HashMap<>();
        Map<String, Object> maleMarriedStatus = new HashMap<>();
        // женат
        maleMarriedStatus.put("EXTATT_SYSNAME", "MaritalStatus");
        maleMarriedStatus.put("EXTATTVAL_VALUE", "MARITAL02");
        maritalStatusForOpenApiMarried.put(0L, maleMarriedStatus);
        Map<String, Object> femaleMarriedStatus = new HashMap<>();
        // замужем
        femaleMarriedStatus.put("EXTATT_SYSNAME", "MaritalStatus");
        femaleMarriedStatus.put("EXTATTVAL_VALUE", "MARITAL04");
        maritalStatusForOpenApiMarried.put(1L, femaleMarriedStatus);

        maritalStatusForOpenApiNotMarried = new HashMap<>();
        maleMarriedStatus = new HashMap<>();
        // холост
        maleMarriedStatus.put("EXTATT_SYSNAME", "MaritalStatus");
        maleMarriedStatus.put("EXTATTVAL_VALUE", "MARITAL01");
        maritalStatusForOpenApiNotMarried.put(0L, maleMarriedStatus);
        femaleMarriedStatus = new HashMap<>();
        // не замужем
        femaleMarriedStatus.put("EXTATT_SYSNAME", "MaritalStatus");
        femaleMarriedStatus.put("EXTATTVAL_VALUE", "MARITAL03");
        maritalStatusForOpenApiNotMarried.put(1L, femaleMarriedStatus);

        //
        CONTRACT_TRANS_STATES_BY_TARGET_STATE = new HashMap<>();
        CONTRACT_TRANS_STATES_BY_TARGET_STATE.put(B2B_CONTRACT_PREPRINTING, Arrays.asList(
                B2B_CONTRACT_PREPRINTING
        ));
        CONTRACT_TRANS_STATES_BY_TARGET_STATE.put(B2B_CONTRACT_PREPARE, Arrays.asList(
                B2B_CONTRACT_PREPRINTING,
                B2B_CONTRACT_PREPARE
        ));
        CONTRACT_TRANS_STATES_BY_TARGET_STATE.put(B2B_CONTRACT_SG, Arrays.asList(
                B2B_CONTRACT_PREPRINTING,
                B2B_CONTRACT_PREPARE,
                B2B_CONTRACT_SG
        ));
    }

    /**
     * Метод добавления к документу системного наименогования по граждаству застрахованного
     * Добавлем системное наименование только если documentList не null, не пуст и количество элементов 1
     *
     * @param contract "словарь" договора
     */
    protected void addDocumentTypeSysNameByCitizenshipIfNullOrEmpty(Map<String, Object> contract) {
        Map<String, Object> insurerMap = getMapParam(contract, "INSURERMAP");
        List<Map<String, Object>> documentList = getListParam(insurerMap, "documentList");
        if (documentList != null && !documentList.isEmpty() && documentList.size() == 1) {
            Map<String, Object> document = documentList.get(0);
            String documentSysName = getStringParam(document, "DOCTYPESYSNAME");
            if (documentSysName.isEmpty()) {
                documentSysName = getLongParam(insurerMap, "CITIZENSHIP") == 1 ? "PassportRF" : "ForeignPassport";
                document.put("DOCTYPESYSNAME", documentSysName);
            }
        }
    }

    /**
     * Метод добавления застрахованому "Семейного положения" по полу
     * и isMarried приходящему со стороних ресурсовю. "Семейного положения"
     * добавлем только если в extAttributeList2 еще нет объекта с системным
     * наименованием "MaritalStatus" и "пол" не равен null
     * не пуст и количество элементов 1
     *
     * @param contract "словарь" договора
     */
    private void updateMaritalStatusFromInsurer(Map<String, Object> contract) {
        Map<String, Object> insurerMap = getMapParam(contract, "INSURERMAP");
        List<Map<String, Object>> extAttributeList2 = getOrCreateListParam(insurerMap, "extAttributeList2");
        Long gender = getLongParam(insurerMap, "GENDER");
        Map<String, Object> maritalStatus = extAttributeList2.stream().filter(it -> getStringParam("EXTATT_SYSNAME")
                .equalsIgnoreCase("MaritalStatus")).findAny().orElse(null);
        if (maritalStatus == null && gender != null) {
            Map<Long, Map<String, Object>> maritalStatusConstant = getBooleanParam(insurerMap, "ISMARRIED", false) ?
                    maritalStatusForOpenApiMarried : maritalStatusForOpenApiNotMarried;
            extAttributeList2.add(maritalStatusConstant.get(gender));
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContrSaveUniOpenAPI(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BContrSaveUniOpenAPI start...");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        // текст ошибки (будет пуст, если нет ошибок)
        StringBuilder error = new StringBuilder();
        boolean isCallFromGate = isCallFromGate(params);
        boolean isCallFromUniOpenAPI = (!isCallFromGate) && isCallFromUniOpenAPI(params);
        if (!isCallFromUniOpenAPI) {
            error.append("Вызов метода выполнен не из веб-сервиса OpenAPI - в сохранении договора отказано! ");
        }
        // определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения
        updateSessionParamsIfNullByCallingUserCreds(params, error, login, password);
        // т.к dozer не может обращаться к предыдущему бину, то кое-что приходится мапить здесь
        doAdditionalContractMapping(params, error);
        // сохранение договора
        Map<String, Object> contract = doContractSave(params, error, login, password);
        // получение сис. наименования продукта из результатов сохранения
        String productSysName = getProductSysNameFromContract(contract, error);
        // опциональные операции по сменам состояния для выполнения после создания договора из веб-сервиса (UniOpenAPI)
        doContractStateChangesIfNeeded(contract, productSysName, error, password, login);
        // опциональные операции по формированию полисов и других ПФ для выполнения после создания договора из веб-сервиса (UniOpenAPI)
        doContractReportsPrintAndAttachIfNeeded(contract, error, login, password);
        // опциональные операции по формированию и отправке писем, содержащих ссылки на соответствующий вариант полиса и пр. документы
        doReportsAndEtcLinksSendingIfNeeded(contract, error, login, password);
        // формирование ответа для возврата через веб-сервиса (UniOpenAPI)
        Map<String, Object> result = makeContractSaveResult(contract, error);
        logger.debug("dsB2BContrSaveUniOpenAPI finished.");
        return result;
    }

    private void doReportsAndEtcLinksSendingIfNeeded(Map<String, Object> contract, StringBuilder error, String login, String password) {
        logger.debug("doReportsAndEtcLinksSendingIfNeeded start...");
        if (error.length() == 0) {
            // todo: вызовы соответствующих методов, анализ результатов, формирание тесктов ошибок и тд и тп
            loggerErrorPretty(logger, "doReportsAndEtcLinksSendingIfNeeded not implemented! Details (contract)", contract);
        }
        logger.debug("doReportsAndEtcLinksSendingIfNeeded finished.");
    }

    /**
     * определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения
     */
    private void updateSessionParamsIfNullByCallingUserCreds(Map<String, Object> params, StringBuilder error, String login, String password) throws Exception {
        if (error.length() == 0) {
            // определение атрибутов пользователя для назначения прав по сведениям о пользователе, от имени которого вызван метод сохранения
            logger.debug("Продукт из веб-сервиса (UniOpenAPI): для назначения прав на договор будут использоваться сведения, определенные по данным пользователя, от имени которого вызван метод сохранения.\n");
            updateSessionParamsIfNullByCallingUserCreds(params, login, password);
        }
    }

    /**
     * т.к dozer не может обращаться к предыдущему бину, то кое-что приходится мапить здесь
     */
    private void doAdditionalContractMapping(Map<String, Object> params, StringBuilder error) {
        if (error.length() == 0) {
            Map<String, Object> contrMap = getMapParam(params, "CONTRMAP");
            if (contrMap == null) {
                contrMap = params;
            }
            // т.к dozer не может обращаться к предыдущему бину, то кое-что приходится мапить здесь
            addDocumentTypeSysNameByCitizenshipIfNullOrEmpty(contrMap);
            updateMaritalStatusFromInsurer(contrMap);
            addInfoByInsuredInContrExtMap(contrMap);
            checkAddressMembers(contrMap);
        }
    }

    /**
     * Проверка адресов участников договора. Добавление копии адреса регистрации
     * с типом фактический адресс. Так надо
     *
     * @param contrMap "словарь" договора
     */
    private void checkAddressMembers(Map<String, Object> contrMap) {
        Map<String, Object> insured = getMapParam(contrMap, "INSUREDMAP");
        Map<String, Object> insurer = getMapParam(contrMap, "INSURERMAP");
        addFactAddress(insurer);
        addFactAddress(insured);
        List<Map<String, Object>> beneficiaryList = getListParam(contrMap, "BENEFICIARYLIST");
        if (beneficiaryList != null) {
            for (Map<String, Object> beneficiary : beneficiaryList) {
                addFactAddress(beneficiary);
            }
        }
    }

    /**
     * Метод добавление копии адреса регистрации в "словарь" участник
     * фактичекого адреса, если в списке адресов находится только адрес регистрации
     *
     * @param participant "карта" участника
     */
    private void addFactAddress(Map<String, Object> participant) {
        List<Map<String, Object>> addressList = getListParam(participant, "addressList");
        if (addressList != null && !addressList.isEmpty()) {
            boolean isExistFactAddress = addressList.stream()
                    .anyMatch(it -> getStringParam(it, "ADDRESSTYPESYSNAME").equals("FactAddress"));
            if (!isExistFactAddress) {
                Map<String, Object> address = addressList.stream()
                        .filter(it -> getStringParam(it, "ADDRESSTYPESYSNAME").equals("RegisterAddress"))
                        .findAny()
                        .orElse(null);
                if (address != null) {
                    Map<String, Object> factAddress = new HashMap<>(address);
                    factAddress.put("ADDRESSTYPESYSNAME", "FactAddress");
                    addressList.add(factAddress);
                }
            }
        }
    }

    /**
     * Добавление дополнительной информации о застрахованном в contrExtMap
     * такой как: insuredGender - пол застрахованного
     * insuredBirthDATE - дата рождения застрахованного
     * insurerIsInsured - страхователь является застрахованным
     * insuredDeclCompliance - Клиент соответствует декларации
     *
     * @param contrMap "словарь" договора
     */
    private void addInfoByInsuredInContrExtMap(Map<String, Object> contrMap) {
        Map<String, Object> insured = getMapParam(contrMap, "INSUREDMAP");
        Map<String, Object> participant = insured != null && !insured.isEmpty() ? insured : getMapParam(contrMap, "INSURERMAP");
        Map<String, Object> contrExtMap = getOrCreateMapParam(contrMap, "CONTREXTMAP");
        if (participant != null) {
            /*
            contrExtMap.put("insuredGender", getStringParam(participant, "GENDER").equals("F") ? 1L : 0L);
            */
            Long insuredGender = getLongParam(participant, "GENDER");
            setOverridedParam(contrExtMap, "insuredGender", insuredGender, true);
            contrExtMap.put("insuredBirthDATE", participant.get("BIRTHDATE"));
            contrExtMap.put("insurerIsInsured", insured != null ? 0L : 1L);
        }
        Long insuredDeclCompliance = getLongParam(contrExtMap, "insuredDeclCompliance");
        if (insuredDeclCompliance == null) {
            contrExtMap.put("insuredDeclCompliance", 1L);
        }
    }

    /**
     * получение сис. наименования продукта из результатов сохранения
     */
    private String getProductSysNameFromContract(Map<String, Object> contract, StringBuilder error) {
        String productSysName = "";
        if (error.length() == 0) {
            productSysName = getStringParamLogged(contract, PRODUCT_SYSNAME_PARAMNAME);
            if (productSysName.isEmpty()) {
                loggerErrorPretty(logger, String.format(
                        "Unable to get product system name (by checking key '%s') from contract save result",
                        PRODUCT_SYSNAME_PARAMNAME
                ), contract);
                error.append("Не удалось определить продукт по завершению сохранения договора! ");
            }
        }
        return productSysName;
    }

    /**
     * сохранение договора
     */
    private Map<String, Object> doContractSave(Map<String, Object> params, StringBuilder error, String login, String password) throws Exception {
        Map<String, Object> contract = null;
        if (error.length() == 0) {
            // следует запомнить премию по договору (если была передана во входных данных)
            Double premValueFromParams = roundSum(getDoubleParam(params, "PREMVALUE"));
            // параметры создания договора
            Map<String, Object> saveParams = new HashMap<>();
            saveParams.putAll(params);
            // требуется принудительная валидация договора (в том числе при первом сохранении)
            saveParams.put(IS_FORCED_VALIDATION_PARAMNAME, true);
            // Map<String, Object> contract = doB2BContrSave(saveParams, login, password);
            // используется callExternalService для того, чтобы обеспечить закрытие транзакции после создания договора
            // т.к. ниже вызывается печать на сервере цифровой подписи, которому не будут доступны данные по договору если транзакция не будет закрыта
            saveParams.put(RETURN_AS_HASH_MAP, true);
            String contractSaveServiceName = B2BPOSWS_SERVICE_NAME;
            String contractSaveMethodName = "dsB2BContrSave";
            contract = callExternalServiceLogged(contractSaveServiceName, contractSaveMethodName, saveParams, login, password);
            String saveError = getStringParamLogged(contract, ERROR);
            if (saveError.isEmpty() && !isCallResultOKAndContains(contract, "CONTRID")) {
                loggerErrorServiceCall(contractSaveServiceName, contractSaveMethodName, saveParams, contract);
                saveError = "Не удалось сохранить договор! ";
            }
            if (saveError.isEmpty()) {
                // проверка вычисленной (и сохраненной в БД премии) на соответствие переданной во входных параметрах
                // (https://rybinsk.bivgroup.com/redmine/issues/23062 - п. 10)
                if (premValueFromParams > MIN_SIGNIFICANT_SUM) {
                    /*
                    Double premValue = roundSum(getDoubleParam(contract, "PREMVALUE"));
                    */
                    // использовать обычную премию по договору допустимо не во всех случаях,
                    // т.к. по многолетним договорам в веб-сервис будет передаваться премия только за первый период
                    // т.е. сравнивать необходимо с суммой, выбираемой по алгоритму, аналогичному применямому при определении оплаты
                    Map<String, Object> getPaymentParams = new HashMap<>();
                    copyParamsIfNotNull(getPaymentParams, contract, "PREMVALUE", CONTRID_PARAMNAME, PRODCONFID_PARAMNAME);
                    getPaymentParams.put(RETURN_AS_HASH_MAP, true);
                    Map<String, Object> getPaymentResult = null;
                    try {
                        getPaymentResult = callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BPaymentGetPaymentForProduct", getPaymentParams, login, password);
                    } catch (Exception ex) {
                        logger.error("Unable to get payment for product! See previously logged errors for details!", ex);
                    }
                    saveError = getStringParamLogged(getPaymentResult, ERROR);
                    if (saveError.isEmpty()) {
                        Double premValue = roundSum(getDoubleParam(getPaymentResult, "PAYMENT"));
                        if (premValue < MIN_SIGNIFICANT_SUM) {
                            logger.error("Unable to get payment for product! See previously logged errors for details!");
                            saveError = "Не удалось определить сумму, которую следует использовать при подтверждении корректности расчета!";
                        } else if (Math.abs(premValue - premValueFromParams) > MIN_SIGNIFICANT_SUM) {
                            // текст ошибки согласно задаче
                            saveError = "Премия за первый период не соответствует рассчитанной премии! ";
                            // протоколирование ошибки
                            loggerErrorPretty(logger, String.format(
                                    "Premium check failed! Premium from params is %s, but payment value (selected from calculated and saved sums) is %s! Details (params)",
                                    premValueFromParams, premValue
                            ), params);
                        }
                    } else {
                        saveError = "Не удалось определить сумму, которую следует использовать при подтверждении корректности расчета! " + saveError;
                    }
                }
            }
            error.append(saveError);
        }
        return contract;
    }

    /**
     * опциональные операции по сменам состояния для выполнения после создания договора из веб-сервиса (UniOpenAPI)
     */
    private void doContractStateChangesIfNeeded(Map<String, Object> contract, String productSysName, StringBuilder error, String password, String login) throws Exception {
        String targetStateSysName = null;
        if (error.length() == 0) {
            targetStateSysName = getParamFromSubsystemProductHandbookOrProductDefaultValuesOrCoreSetting(
                    contract, CONTRACT_CREATE_STATE_PARAMNAME, B2B_CONTRACT_DRAFT, error, login, password
            );
        }
        if (error.length() == 0) {
            if ((targetStateSysName == null) || (targetStateSysName.isEmpty())) {
                error.append("Не удалось определить необходимость выполнения операций по смене состояния договора! ");
            } else {
                // последовательная смена состояния согласно targetStateSysName (если требуется)
                List<String> stateSysNameList = CONTRACT_TRANS_STATES_BY_TARGET_STATE.get(targetStateSysName);
                // последовательная смена состояния договора согласно stateSysNameList
                doContractStateChanges(contract, stateSysNameList, error, login, password);
            }
        }
    }

    /**
     * опциональные операции по формированию полисов и других ПФ для выполнения после создания договора из веб-сервиса (UniOpenAPI)
     */
    private void doContractReportsPrintAndAttachIfNeeded(Map<String, Object> contract, StringBuilder error, String login, String password) throws Exception {
        if (error.length() == 0) {
            // данные договора
            Long contractId = getLongParamLogged(contract, "CONTRID");
            Long prodConfId = getLongParamLogged(contract, "PRODCONFID");
            // ПФ по договору следует выбирать в зависимости от конечного состояния (учитывая переходы)
            String stateSysName = getStringParamLogged(contract, STATE_SYSNAME_PARAMNAME);
            String repLevelList;
            if (B2B_CONTRACT_SG.equals(stateSysName)) {
                // если договор подписан - чистовик полиса
                repLevelList = UNIOPENAPI_REPLEVEL_POLICY_SIGNED;
            } else {
                // если договор еще не подписан (в любом другом предшествующем подписанию состоянии) - заявление и черновик полиса
                repLevelList = UNIOPENAPI_REPLEVEL_POLICY_DRAFT + ", " + UNIOPENAPI_REPLEVEL_APPLICATION;
            }
            // получение и печать ПФ по значениям REPLEVEL с прикреплением к договору
            // todo: обработка ошибок печати
            List<Map<String, Object>> printResultList = getAndPrintWithAttachingReportsByRepLevel(contractId, prodConfId, repLevelList, login, password);
        }
    }

    /**
     * формирование ответа для возврата через веб-сервиса (UniOpenAPI)
     */
    private Map<String, Object> makeContractSaveResult(Map<String, Object> contract, StringBuilder error) {
        Map<String, Object> result = new HashMap<>();
        if (error.length() == 0) {
            HashMap<String, Object> contractResultType = new HashMap<>();
            // todo: рассмотреть возможность отказа от хеш-код в пользу GUID из B2B_CONTR.EXTERNALID
            // contractResultType.put(UNIOPENAPI_RESULT_CLASS_CONTRACT_CONTRACT_ID_PARAMNAME, contract.get("CONTRID"));
            contractResultType.put(UNIOPENAPI_RESULT_CLASS_CONTRACT_CONTRACT_ID_PARAMNAME, contract.get("EXTERNALID"));
            // дополнительно возвращаем номер договора
            // todo: изменить когда/если аналитики сообщат требуется ли всегда или только по определенным продуктам
            contractResultType.put(UNIOPENAPI_RESULT_CLASS_CONTRACT_CONTRACT_NUMBER_PARAMNAME, contract.get("CONTRNUMBER"));
            result.put(UNIOPENAPI_RESULT_CLASS_CONTRACT, contractResultType);
        } else {
            result.put(ERROR, error.toString());
            result.put(REASON, contract.get(REASON));
        }
        return result;
    }

    /**
     * получение сис. наименования продукта по ИД договора
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BContractProductSysNameByContrId(Map<String, Object> params) throws Exception {
        Map<String, Object> result = selectQuery("dsB2BContractProductSysNameByContrId", params);
        return result;
    }

    /**
     * получение ИД договора по EXTERNALID
     */
    @WsMethod(requiredParams = {"EXTERNALID"})
    public Map<String, Object> dsB2BContractContrIdByExternalId(Map<String, Object> params) throws Exception {
        Map<String, Object> result = selectQuery("dsB2BContractContrIdByExternalId", params);
        return result;
    }

    /**
     * получение единичных данных договора по EXTERNALID
     */
    @WsMethod(requiredParams = {"EXTERNALID"})
    public Map<String, Object> dsB2BContractExtraBriefByIds(Map<String, Object> params) throws Exception {
        Map<String, Object> result = selectQuery("dsB2BContractExtraBriefByIds", params);
        return result;
    }

}
