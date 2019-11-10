package com.bivgroup.services.b2bposws.facade.pos.loss;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BLossNoticeCustom")
public class B2BLossNoticeCustomFacade extends B2BDictionaryBaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());
    private static final String LOSS_NOTICE_MAP_PARAMNAME = "LOSSNOTICE" + "MAP";
    static final String LOSS_NOTICE_ID_PARAMNAME = "lossNoticeId";
    private static final String LOSS_NOTICE_EXTERNAL_ID_PARAMNAME = "externalId";
    private static final String LOSS_NOTICE_APPLICANT_EMAIL_PARAMNAME = "applicantEmail";

    // todo: добавить поддержу передачи clientExternalId из гейта ЛК2 и учета этого тут (не загружать клиента и пр. если clientExternalId уже передан из гейта)
    private String getClientExternalIdByClientProfileId(Long clientProfileId) throws Exception {
        Map<String, Object> clientProfile = dctFindById(CLIENT_PROFILE_ENTITY_NAME, clientProfileId);
        Map<String, Object> client = getMapParam(clientProfile, "clientId_EN");
        String clientExternalId = getStringParamLogged(client, "externalId");
        return clientExternalId;
    }

    private Map<String, Object> getLossNoticeApplicantParamsByClientProfileId(Long clientProfileId) throws Exception {
        HashMap<String, Object> lossNoticeApplicantParams = new HashMap<String, Object>();
        Map<String, Object> clientProfile = dctFindById(CLIENT_PROFILE_ENTITY_NAME, clientProfileId);
        Map<String, Object> client = getMapParam(clientProfile, "clientId_EN");
        // externalId
        String clientExternalId = getStringParamLogged(client, "externalId");
        lossNoticeApplicantParams.put("thirdPartyId", clientExternalId);
        // главный адрес электронной почты
        String eMail = "";
        List<Map<String, Object>> contactList = getOrCreateListParam(client, "contacts");
        for (Map<String, Object> contact : contactList) {
            Long isPrimary = getLongParam(contact, "isPrimary");
            if (BOOLEAN_FLAG_LONG_VALUE_TRUE.equals(isPrimary)) {
                Map<String, Object> contactType = getMapParam(contact, "typeId_EN");
                String contactTypeSysName = getStringParam(contactType, "sysname");
                if ("PersonalEmail".equals(contactTypeSysName)) {
                    eMail = getStringParam(contact, "value");
                    break;
                }
            }
        }
        lossNoticeApplicantParams.put("applicantEmail", eMail);
        return lossNoticeApplicantParams;
    }

    @WsMethod(requiredParams = {LOSS_NOTICE_MAP_PARAMNAME})
    public Map<String, Object> dsB2BLossNoticeSave(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BLossNoticeSave begin");
        boolean isCallFromGate = isCallFromGate(params);
        String errorDefault = "Не удалось сохранить уведомление о страховом событии!";
        String error = "";
        Map<String, Object> lossNotice = getOrCreateMapParam(params, LOSS_NOTICE_MAP_PARAMNAME);
        // Ид профиля клиента (если указан - то в качестве заявителя будет установлен клиент, который сохраняет уведомление)
        Long clientProfileId = getLongParamLogged(params, "clientProfileId");
        // почта заявителя
        String applicantEmail = getStringParamLogged(lossNotice, "applicantEmail");
        if (clientProfileId != null) {
            // режим пользователя ЛК (заявитель = клиент, зарегистрированный в ЛК)
            lossNotice.putIfAbsent("applicantId", clientProfileId);
            Map<String, Object> lossNoticeApplicantParams = null;
            if (lossNotice.get("thirdPartyId") == null) {
                lossNoticeApplicantParams = getLossNoticeApplicantParamsByClientProfileId(clientProfileId);
                // связь по clientExternalId - thirdPartyId
                //String clientExternalId = getClientExternalIdByClientProfileId(clientProfileId);
                String clientExternalId = getStringParamLogged(lossNoticeApplicantParams, "thirdPartyId");
                if (clientExternalId.isEmpty()) {
                    logger.error(String.format(
                            "Unable to get clientExternalId (for client profile with id = %d) for saving loss notice with link to client profile! Details (save call params): %s.",
                            clientProfileId, params
                    ));
                    error = errorDefault;
                } else {
                    lossNotice.put("thirdPartyId", clientExternalId);
                }
            }
            if ((error.isEmpty()) && (applicantEmail.isEmpty())) {
                // почта не передана с интерфейса в явном виде - следует определить из параметров ИД альт. сессии
                if (lossNoticeApplicantParams == null) {
                    lossNoticeApplicantParams = getLossNoticeApplicantParamsByClientProfileId(clientProfileId);
                }
                String eMailClientProfile = getStringParamLogged(lossNoticeApplicantParams, "applicantEmail");
                if (eMailClientProfile.isEmpty()) {
                    logger.error(String.format(
                            "Unable to get client's e-mail (for client profile with id = %d) for saving as applicant e-mail address in loss notice! Details (save call params): %s.",
                            clientProfileId, params
                    ));
                    error = errorDefault;
                } else {
                    lossNotice.put("applicantEmail", eMailClientProfile);
                }
            }
            // todo: мб еще и applicantName
        } else {
            // режим кабинета представителя (заявитель = представитель)
            if (applicantEmail.isEmpty()) {
                // почта не передана с интерфейса в явном виде - следует определить из параметров ИД альт. сессии
                String eMailAltAuth = getStringParamLogged(params, ALT_AUTH_EMAIL_PARAMNAME);
                if (!eMailAltAuth.isEmpty()) {
                    lossNotice.put("applicantEmail", eMailAltAuth);
                }
            }
            // todo: мб еще и applicantName
        }
        // пока не известно, следует ли вместо автогенерации полей вида CREATE* / UPDATE* использовать другой механизм
        /*
        Long lossNoticeId = getLongParamLogged(lossNotice, LOSS_NOTICE_ID_PARAMNAME);
        Date nowDate = new Date();
        if (lossNoticeId == null) {
            lossNotice.put("createDate", nowDate);
        }
        lossNotice.put("updateDate", nowDate);
        */
        // пока не известно, следует ли вместо автогенерации полей вида CREATE* / UPDATE* использовать другой механизм
        updateEntitySystemDates(lossNotice, LOSS_NOTICE_ID_PARAMNAME);
        //
        lossNotice.remove("stateId_EN");
        Map<String, Object> result;
        if (error.isEmpty()) {
            result = dctCrudByHierarchy(LOSS_NOTICE_ENTITY_NAME, lossNotice, isCallFromGate);
            markAllMapsByKeyValue(result, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        } else {
            result = new HashMap<String, Object>();
            result.put(ERROR, error);
        }
        logger.debug("dsB2BLossNoticeSave end");
        return result;
    }

    @WsMethod(requiredParams = {"stateId"})
    public Map<String, Object> dsB2BLossNoticeLoadByStateId(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BLossNoticeLoadByStateId begin");
        boolean isCallFromGate = isCallFromGate(params);
        String error = "Не удалось получить список уведомлений о страховом событии!";
        // обязательно учитываемый методом входной параметр
        Long stateId = getLongParamLogged(params, "stateId");
        List<Map<String, Object>> lossNoticeList = null;
        if (stateId != null) {
            Map<String, Object> lossNoticeParams = new HashMap<String, Object>();
            lossNoticeParams.put("stateId", stateId);
            lossNoticeList = dctFindByExample(LOSS_NOTICE_ENTITY_NAME, lossNoticeParams, isCallFromGate);
            markAllMapsByKeyValue(lossNoticeList, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
            error = "";
        } else {
            logger.error("Unable to get loss notice list - stateId from params is null! Found method params: " + params);
        }
        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        if (error.isEmpty()) {
            result.put(RESULT, lossNoticeList);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BLossNoticeLoadByStateId end");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BLossNoticeLoad(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BLossNoticeLoad begin");
        boolean isCallFromGate = isCallFromGate(params);
        Long lossNoticeId = getLongParamLogged(params, LOSS_NOTICE_ID_PARAMNAME);
        List<Map<String, Object>> lossNoticeList;
        if (lossNoticeId != null) {
            Map<String, Object> lossNotice = dctFindById(LOSS_NOTICE_ENTITY_NAME, lossNoticeId, isCallFromGate);
            lossNoticeList = new ArrayList<Map<String, Object>>();
            lossNoticeList.add(lossNotice);
        } else {
            lossNoticeList = dctFindByExample(LOSS_NOTICE_ENTITY_NAME, params, isCallFromGate);
        }
        markAllMapsByKeyValue(lossNoticeList, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(RESULT, lossNoticeList);
        logger.debug("dsB2BLossNoticeLoad end");
        return result;
    }

    @WsMethod(requiredParams = {"clientProfileId"})
    public Map<String, Object> dsB2BLossNoticeLoadByClientProfileId(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BLossNoticeLoad begin");
        boolean isCallFromGate = isCallFromGate(params);
        String error = "Не удалось получить список уведомлений о страховом событии!";
        // обязательно учитываемый методом входной параметр
        Long clientProfileId = getLongParamLogged(params, "clientProfileId");
        List<Map<String, Object>> lossNoticeList = null;
        if (clientProfileId != null) {
            String clientExternalId = getClientExternalIdByClientProfileId(clientProfileId);
            if (!clientExternalId.isEmpty()) {
                Map<String, Object> lossNoticeParams = new HashMap<String, Object>();
                lossNoticeParams.put("thirdPartyId", clientExternalId);
                lossNoticeParams.put(LOSS_NOTICE_ID_PARAMNAME, params.get(LOSS_NOTICE_ID_PARAMNAME)); // ИД конкретного уведомления
                lossNoticeList = dctFindByExample(LOSS_NOTICE_ENTITY_NAME, lossNoticeParams, isCallFromGate);
                markAllMapsByKeyValue(lossNoticeList, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
                error = "";
            } else {
                logger.error(String.format("Unable to get loss notice list - externalId for client with specified clientProfileId (%d) is empty!", clientProfileId));
            }
        } else {
            logger.error("Unable to get loss notice list - clientProfileId from params is null! Found method params: " + params);
        }

        //фильтрация не удаленных объектов
        List<Map<String,Object>> lossNoticeListFiltered = lossNoticeList.stream().filter(new Predicate<Map<String, Object>>() {
            @Override
            public boolean test(Map<String, Object> lossNotice) {
                try {
                    Map<String,Object> state = (Map<String, Object>) lossNotice.get("stateId_EN");
                    String stateSysname = (String) state.get("sysname");
                    return !stateSysname.equalsIgnoreCase("B2B_LOSSNOTICE_DELETED");
                } catch (NullPointerException ex) {
                    return true;
                }
            }
        }).collect(Collectors.toList());

        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        if (error.isEmpty()) {
            result.put(RESULT, lossNoticeListFiltered);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BLossNoticeLoad end");
        return result;
    }

    // для проверки статуса без авторизации.
    private Map<String, Object> getLossNoticeBriefWithClaims(Long lossNoticeId, String applicantEmail, Map<String,Object> params, Map<String, Object> subResult, boolean isCallFromGate, String login, String password) throws Exception {
        String errorDefault = "Данные не найдены. Проверьте правильность указанных данных."; // сообщение согласно ФТ
        String error = "";
        Map<String, Object> lossNoticeParams = new HashMap<String, Object>();
        lossNoticeParams.put(LOSS_NOTICE_ID_PARAMNAME, lossNoticeId);
        lossNoticeParams.put(LOSS_NOTICE_APPLICANT_EMAIL_PARAMNAME, applicantEmail);
        List<Map<String, Object>> lossNoticeList = dctFindByExample(LOSS_NOTICE_ENTITY_NAME, lossNoticeParams, isCallFromGate);
        Long claimId = null;
        if (lossNoticeList == null) {
            error = "Ошибка при поиске соответсвтующего уведомления о событии для проверки статуса рассмотрения заявленного страхового случая!";
        } else if (lossNoticeList.isEmpty()) {
            error = errorDefault; // сообщение согласно ФТ
        } else if (lossNoticeList.size() > 1) {
            error = "По указанным сведениям найдено более одно уведомления о событии!";
        }
        Map<String,Object> result = new HashMap<>();
        result.put(RESULT, lossNoticeList);
        if (!error.isEmpty()) {
            result.put(ERROR, error);
        }
        return result;
    }

    //для фулл инфо
    private Map<String, Object> getLossNoticeBriefWithClaims(Long lossNoticeId, Map<String,Object> params, Map<String, Object> subResult, boolean isCallFromGate, String login, String password) throws Exception {
        Map<String, Object> lossNoticeParams = new HashMap<String, Object>();
        lossNoticeParams.put(LOSS_NOTICE_ID_PARAMNAME, lossNoticeId);
        Map<String, Object> lossNoticeBriefWithClaims = getLossNoticeBriefWithClaims(lossNoticeParams, params, subResult, isCallFromGate, login, password);
        return lossNoticeBriefWithClaims;
    }

    private Map<String, Object> getLossNoticeBriefWithClaims(Map<String, Object> lossNoticeParams, Map<String,Object> params, Map<String, Object> subResult, boolean isCallFromGate, String login, String password) throws Exception {
        Map<String, Object> lossNoticeBriefWithClaims = new HashMap<String, Object>();
        String errorDefault = "Данные не найдены. Проверьте правильность указанных данных."; // сообщение согласно ФТ
        String error;

        List<Map<String, Object>> lossNoticeList = dctFindByExample(LOSS_NOTICE_ENTITY_NAME, lossNoticeParams, isCallFromGate);
        Long claimId = null;
        if (lossNoticeList == null) {
            error = "Ошибка при поиске соответсвтующего уведомления о событии для проверки статуса рассмотрения заявленного страхового случая!";
        } else if (lossNoticeList.isEmpty()) {
            error = errorDefault; // сообщение согласно ФТ
        } else if (lossNoticeList.size() > 1) {
            error = "По указанным сведениям найдено более одно уведомления о событии!";
        } else {
            error = errorDefault; // сообщение согласно ФТ
            Map<String, Object> lossNotice = lossNoticeList.get(0);
            subResult.put("lossNoticeList", lossNoticeList);
            logger.debug("lossNotice: " + lossNotice);
            if (lossNotice != null) {
                copyParamsIfNotNull(lossNoticeBriefWithClaims, lossNotice,
                        LOSS_NOTICE_ID_PARAMNAME,
                        "eventDate$date",
                        "insEventId", "insEventId_EN",
                        "damageCatId", "damageCatId_EN",
                        "insuredSurname",
                        "insuredName",
                        "insuredMiddleName",
                        "insuredBirthDate$date",
                        "createDate$date",
                        "updateDate$date"
                );
            }
            claimId = getLongParamLogged(lossNotice, "externalId");
            if (claimId != null) {
                error = "";
            }
        }

        List<Map<String, Object>> claimList = null;
        if (error.isEmpty()) {
            Map<String, Object> getClaimParams = new HashMap<String, Object>();
            getClaimParams.put("CLAIMID", claimId);
            getClaimParams.put("urlPath", params.get("urlPath"));
            getClaimParams.put("SESSIONIDFORCALL", params.get("SESSIONIDFORCALL"));

            getClaimParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> claimListResult = callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationGetClaimList", getClaimParams, login, password);
            claimList = getListParam(claimListResult, "CLAIMFULLLIST");
            List<Map<String, Object>> claimDocList = getListParam(claimListResult, "CLAIMDOCLIST");

            if (claimDocList != null) {
                lossNoticeBriefWithClaims.put("claimDoc", claimDocList);
            }
            subResult.put("CLAIMFULLLIST", claimList);
            if ((claimList == null) || (claimList.isEmpty())) {
                error = "Не удалось получить сведения о заявлениях по найденному уведомлению о событии!";
            }
        }

        if (error.isEmpty()) {
            ArrayList<Map<String, Object>> claimBriefInfoList = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> claim : claimList) {
                //<editor-fold desc="договор из интеграции" defaultstate="collapsed">
                /*
                Long policyId = getLongParamLogged(claim, "policyId");
                if (policyId != null) {
                    // done: заменить на более адекватный и менее ресурсоемкий вариант, после того как клиент ответит на вопросы
                    Map<String, Object> getContractParams = new HashMap<String, Object>();
                    getContractParams.put("POLICYID", policyId);
                    getContractParams.put(RETURN_AS_HASH_MAP, true);
                    Map<String, Object> contractResult = callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationGetContractList", getContractParams, login, password);
                    Map<String, Object> contract = getMapParam(contractResult, "CONTRMAP");
                    claim.put("policy", contract);
                }
                */
                //</editor-fold>
                Map<String, Object> claimBrief = new HashMap<String, Object>();
                copyParamsIfNotNull(claimBrief, claim,
                        "policyNumber", // номер договора
                        "claimNumb", // номер заявления
                        //"productRisk", // done: заменить на наименование риска после ответов клиента, сейчас доступно только сис. наименование
                        "statusList" // todo: заменить на наименование статуса после ответов клиента, сейчас доступен список статусов с одинаковыми датами
                );
                Map<String, Object> claimBen = getMapParam(claim, "ben");
                Map<String, Object> claimBenBrief = new HashMap<String, Object>();
                copyParamsIfNotNull(claimBenBrief, claimBen,
                        "lastName", // фамилия
                        "firstName", // имя
                        "patronymic", // отчество
                        "fullName" // фамилия имя отчество
                );
                claimBrief.put("ben", claimBenBrief);

                // Определение наименовани риска по его коду из интеграции (productRisk -> productRiskName)
                String productRiskCode = getStringParamLogged(claim, "productRisk");
                String productRiskName = getEntityRecordFieldStringValueByOtherFieldValue(KIND_INTEGRATION_RISK_ENTITY_NAME, "name", "code", productRiskCode);
                claimBrief.put("productRisk", productRiskCode);
                claimBrief.put("productRiskName", productRiskName);
                if (productRiskName.isEmpty()) {
                    logger.error(String.format(
                            "Unable to get risk name from classifier '%s' for integration risk code = '%s'!",
                            KIND_INTEGRATION_RISK_ENTITY_NAME, productRiskCode
                    ));
                    error = "Не для всех рисков удалось определить наименования по коду (возможно, данные соответствующего справочника устарели)!";
                }

                clearAllMapFromKeysAndEtc(claimBrief, true, false, "$type$", "ROWSTATUS");
                claimBriefInfoList.add(claimBrief);
            }
            lossNoticeBriefWithClaims.put("claims", claimBriefInfoList);
            subResult.put("claimBriefInfoList", claimBriefInfoList);
        }

        if (!error.isEmpty()) {
            lossNoticeBriefWithClaims.put(ERROR, error);
        }
        return lossNoticeBriefWithClaims;
    }

    // ФТ: "Проверить статус рассмотрения заявленного страхового    случая" (для кабинета представителя)
    @WsMethod(requiredParams = {LOSS_NOTICE_ID_PARAMNAME, LOSS_NOTICE_APPLICANT_EMAIL_PARAMNAME})
    public Map<String, Object> dsB2BLossNoticeCheckState(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BLossNoticeCheckState begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        String error = "";

        String matchesLossNoticeId = getStringParam(params,LOSS_NOTICE_ID_PARAMNAME);
        // Если в номере заявки, не только цифры, то ошибка #14888
        if (!matchesLossNoticeId.matches("\\d+") && error.isEmpty()) {
            error = "Данные не найдены. Проверьте правильность указанных данных."; // сообщение согласно ФТ;
        }

        Map<String, Object> lossNoticeBrief = null;
        Long lossNoticeId = null;
        String applicantEmail = null;
        if (error.isEmpty()) {
            lossNoticeId = getLongParamLogged(params, LOSS_NOTICE_ID_PARAMNAME);
            applicantEmail = getStringParamLogged(params, LOSS_NOTICE_APPLICANT_EMAIL_PARAMNAME);
            Map<String, Object> subResult = new HashMap<>();
            // ФТ: "Пользователь вводит свой электронный адрес и номер заявки (присвоенный при регистрации заявления о страховом событии)"
            if ((lossNoticeId == null) || (applicantEmail.isEmpty())) {
                error = "Указано недостаточно сведений для проверки статус рассмотрения заявленного страхового случая!";
            } else {
                error = "";
            }

            if (error.isEmpty()) {
                lossNoticeBrief = getLossNoticeBriefWithClaims(lossNoticeId, applicantEmail, params, subResult, isCallFromGate, login, password);
                error = getStringParamLogged(lossNoticeBrief, ERROR);
            }
        }

        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        if (error.isEmpty()) {
            //result.put(DEBUG_SUB_RESULT_PARAMNAME, subResult); // !только для отладки!
            // todo: уточнить требующиеся выходные параметры у группы разработки интерфейсов
            result.putAll(lossNoticeBrief);
            result.put("ALT_AUTH_EMAIL", applicantEmail);
            result.put("ALT_AUTH_PHONENUMBER", "noPhoneLossNoticeCheckNoAuth");
            result.put("ALT_AUTH_SMSCODE","noSMS");
            result.put("ALT_AUTH_CONTRNUMBER", lossNoticeId);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BLossNoticeCheckState end");
        return result;
    }

    // Сервис загрузки заявления (вся информация по заявлению)
    @WsMethod(requiredParams = {LOSS_NOTICE_ID_PARAMNAME})
    public Map<String, Object> dsB2BLossNoticeLoadFullInfo(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BLossNoticeLoadFullInfo begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        String error = "";
        Map<String, Object> subResult = new HashMap<String, Object>();
        Long lossNoticeId = getLongParamLogged(params, LOSS_NOTICE_ID_PARAMNAME);
        if (lossNoticeId == null) {
            error = "Указано недостаточно сведений для получения полных данных заявленного страхового случая!";
        } else {
            error = "";
        }

        Map<String, Object> lossNoticeBrief = null;
        if (error.isEmpty()) {
            lossNoticeBrief = getLossNoticeBriefWithClaims(lossNoticeId, params, subResult, isCallFromGate, login, password);
            error = getStringParamLogged(lossNoticeBrief, ERROR);
        }

        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        if (error.isEmpty()) {
            //result.put(DEBUG_SUB_RESULT_PARAMNAME, subResult); // !только для отладки!
            // todo: уточнить требующиеся выходные параметры у группы разработки интерфейсов
            result.putAll(lossNoticeBrief);
        } else {
            result.put(ERROR, error);
        }
        logger.debug("dsB2BLossNoticeLoadFullInfo end");
        return result;
    }

}
