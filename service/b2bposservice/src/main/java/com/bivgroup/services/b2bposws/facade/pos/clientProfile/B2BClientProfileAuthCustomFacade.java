package com.bivgroup.services.b2bposws.facade.pos.clientProfile;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;

import static com.bivgroup.services.b2bposws.facade.pos.clientProfile.B2BClientProfileEventCustomFacade.*;

import com.bivgroup.services.b2bposws.system.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;

/**
 * @author mmamaev
 */
@BOName("B2BClientProfileAuthCustom")
public class B2BClientProfileAuthCustomFacade extends B2BDictionaryBaseFacade {

    private Logger logger = Logger.getLogger(this.getClass());

    private static final String THIS_SERVICE_NAME = Constants.B2BPOSWS;
    //private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    protected static Map<String, Map<String, Object>> eventTypeBySysName = new HashMap<String, Map<String, Object>>();

    public B2BClientProfileAuthCustomFacade() {
        super();
        init();
    }

    private void init() {
        logger.debug("B2BClientProfileAuthCustomFacade init...");
        logger.debug("B2BClientProfileAuthCustomFacade init finished.");
    }

    private Map<String, Object> checkIsDigitalSignatureConfirmedByInfoFromClientEvents(Long clientProfileID, String login, String password) throws Exception {
        String sendEventTypeSysName = EVENT_SYSNAME_DGTSGN_SMS_CODE_SEND;
        String checkEventTypeSysName = EVENT_SYSNAME_DGTSGN_SMS_CODE_CHECK;
        Map<String, Object> result = checkCustomEventByInfoFromClientEvents(clientProfileID, sendEventTypeSysName, checkEventTypeSysName, login, password);
        return result;
    }

    // проверка наличия успешного подтверждения по СМС для указанных типов событий (отправка/подтверждение)
    // требуется, например, для проверки наличия соглашения клиента на цифровую подпись
    protected Map<String, Object> checkCustomEventByInfoFromClientEvents(Long clientProfileID, String sendEventTypeSysName, String checkEventTypeSysName, String login, String password) throws Exception {
        Map<String, Object> eventAdditionalParams = new HashMap<String, Object>();
        Map<String, Object> result = checkCustomEventByInfoFromClientEvents(clientProfileID, sendEventTypeSysName, checkEventTypeSysName, eventAdditionalParams, login, password);
        return result;
    }

    protected Map<String, Object> getEventTypeBySysName(String eventTypeSysName, String login, String password) throws Exception {
        Map<String, Object> eventType = eventTypeBySysName.get(eventTypeSysName);
        if (eventType == null) {
            Map<String, Object> eventTypeParams = new HashMap<String, Object>();
            eventTypeParams.put("eventTypeSysName", eventTypeSysName);
            eventTypeParams.put(RETURN_AS_HASH_MAP, true);
            eventType = this.callService(THIS_SERVICE_NAME, "dsB2BClientProfileEventTypeGetBySysName", eventTypeParams, login, password);
            eventTypeBySysName.put(eventTypeSysName, eventType);
        }
        return eventType;
    }

    protected Long getEventTypeEIDBySysName(String eventTypeSysName, String login, String password) throws Exception {
        Map<String, Object> eventType = getEventTypeBySysName(eventTypeSysName, login, password);
        Long eventTypeEID = getLongParam(eventType, "eId");
        return eventTypeEID;
    }

    // проверка наличия успешного подтверждения по СМС для указанных типов событий (отправка/подтверждение)
    // требуется, например, для проверки наличия соглашения клиента на цифровую подпись
    protected Map<String, Object> checkCustomEventByInfoFromClientEvents(Long clientProfileID, String sendEventTypeSysName, String checkEventTypeSysName, Map<String, Object> eventAdditionalParams, String login, String password) throws Exception {
        logger.debug("checkCustomEventByInfoFromClientEvents...");
        Map<String, Object> result = new HashMap<String, Object>();
        // по умолчанию - считается, что ошибка
        String checkConfirmEventErrorStr = "возникла ошибка при проверке cуществования событий!";
        // события найденного клиента
        // todo: исправить на запрос по криетриям, когда словарная система сможет их поддерживать
        Long eventTypeID;
        // общие параметры событий
        HashMap<String, Object> eventsParams = new HashMap<String, Object>();
        eventsParams.put("clientProfileId", clientProfileID);
        eventsParams.put("modeApp", EVENT_MODE_APP_WEB);
        eventsParams.put("paramBoolean10", 0);
        eventsParams.putAll(eventAdditionalParams); // доп. параметры событий
        // события отправки
        eventTypeID = getEventTypeEIDBySysName(sendEventTypeSysName, login, password);
        eventsParams.put("eventTypeId", eventTypeID);
        List<Map<String, Object>> sendEventsList = dctFindByExample(CLIENT_PROFILE_EVENT_ENTITY_NAME, eventsParams);
        // события проверки (только успешные)
        eventTypeID = getEventTypeEIDBySysName(checkEventTypeSysName, login, password);
        eventsParams.put("eventTypeId", eventTypeID);
        eventsParams.put(ERROR_CODE_EVENT_FIELDNAME, "0"); // только успешные события
        List<Map<String, Object>> checkEventsList = dctFindByExample(CLIENT_PROFILE_EVENT_ENTITY_NAME, eventsParams);

        //List<Map<String, Object>> eventsList = (ArrayList<Map<String, Object>>) clientProfile.get("Events");
        List<Map<String, Object>> eventsList = new ArrayList<Map<String, Object>>();
        if (sendEventsList != null) {
            eventsList.addAll(sendEventsList);
        }
        if (checkEventsList != null) {
            eventsList.addAll(checkEventsList);
        }

        if (eventsList.isEmpty()) {
            // вообще не найдено событий (маловероятно)
            checkConfirmEventErrorStr = "не найдено сведений об отправке СМС!";
        } else {
            // сортировка событий по ID
            // todo: убрать когда будут поддерживаться нормальные даты со временем
            CopyUtils.sortByLongFieldName(eventsList, "id");
            // сортировка событий по дате
            CopyUtils.sortByDateFieldName(eventsList, "eventDate");
            // поиск последнего события отправки СМС
            Long smsSendEventTypeEID = getEventTypeEIDBySysName(sendEventTypeSysName, login, password);
            Map<String, Object> smsSendEvent = null;
            Integer smsSendEventIndex = null;
            for (int i = eventsList.size() - 1; i >= 0; i--) {
                Map<String, Object> event = eventsList.get(i);
                Long eventTypeEID = getLongParam(event, "eventTypeId");
                if (smsSendEventTypeEID.equals(eventTypeEID)) {
                    smsSendEvent = event;
                    smsSendEventIndex = i;
                    break;
                }
            }
            if ((smsSendEvent == null) || (smsSendEventIndex == null)) {
                // не найдено событий по отправке (маловероятно)
                //checkSmsCodeErrorStr = "не найдено сведений об отправке СМС! Пожалуйста, повторите регистрацию.";
                checkConfirmEventErrorStr = "не найдено сведений об отправке СМС!";
            } else {
                // найдено событие по отправке - требуется выполнить ряд проверок
                //
                // проверка "количество событий <checkEventTypeSysName> зарегистрированных после найденного события <sendEventTypeSysName>"
                List<Map<String, Object>> eventsAfterSms = eventsList.subList(smsSendEventIndex, eventsList.size());
                Long smsCheckEventTypeEID = getEventTypeEIDBySysName(checkEventTypeSysName, login, password);

                checkConfirmEventErrorStr = "не найдено событий успешного подтверждения по СМС!";
                for (Map<String, Object> eventAfterSms : eventsAfterSms) {
                    Long eventTypeEID = getLongParam(eventAfterSms, "eventTypeId");
                    if (smsCheckEventTypeEID.equals(eventTypeEID)) {
                        String errorCode = getStringParam(eventAfterSms, ERROR_CODE_EVENT_FIELDNAME);
                        if ("0".equals(errorCode)) {
                            // найдено успешное событие подтверждения по СМС
                            checkConfirmEventErrorStr = "";
                            Map<String, Object> resultEvent = new HashMap<String, Object>();
                            resultEvent.putAll(eventAfterSms);
                            resultEvent.remove("clientProfileId_EN");
                            result.put("CLIENTPROFILEEVENT", resultEvent);
                        }
                    }
                }
            }
        }

        if (!checkConfirmEventErrorStr.isEmpty()) {
            logger.debug("checkCustomEventByInfoFromClientEvents failed. Details: " + checkConfirmEventErrorStr);
            checkConfirmEventErrorStr = "Для данного типа событий проверка наличия подтверждения не пройдена: " + checkConfirmEventErrorStr;
        }

        logger.debug("checkCustomEventByInfoFromClientEvents finished. Result: " + checkConfirmEventErrorStr);
        // текст ошибки или пустая строка (если все проверки выполнились успешно)
        result.put(ERROR, checkConfirmEventErrorStr);
        return result;
    }

    // todo пока не используется в СБСЖ
    // «Проверка рамочного соглашения на цифровую подпись»
    /*@WsMethod(requiredParams = {"clientProfileId"})
    public Map<String, Object> dsB2BWebIsDigitalSignatureConfirmed(Map<String, Object> params) throws Exception {

        logger.debug("dsB2BWebIsDigitalSignatureConfirmed...");

        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        //boolean isCallFromGate = isCallFromGate(params);

        Long clientProfileID = getLongParamLogged(params, "clientProfileId");

        String errorNote = "При проверке рамочного соглашения на цифровую подпись возникла ошибка!";
        Map<String, Object> result = new HashMap<String, Object>();

        if (clientProfileID == null) {
            logger.error("Missed required parameters for digital signature confirmation existance check! Details (found parameters): " + params);
            errorNote = "Недостаточно входных параметров для проверки рамочного соглашения на цифровую подпись!";
        } else {
            // все параметры клиента известны
            //
            // проверка наличия наличия успешного подтверждения соглашение на цифровую подпись
            *//*
            Map<String, Object> checkConfirmEventResult = checkIsDigitalSignatureConfirmedByInfoFromClientEvents(clientProfileID, login, password);
             *//*
            // ЛК 2.1: "необходимо анализировать наличие соответствующего типа «Пользовательское соглашение» в статусе «Действует»"
            Map<String, Object> checkConfirmEventResult = checkIsDigitalSignatureConfirmedByClientProfileAgreement(clientProfileID, login, password);
            String checkConfirmEventErrorStr = getStringParamLogged(checkConfirmEventResult, ERROR);
            if (checkConfirmEventErrorStr.isEmpty()) {
                // успешное подтверждение соглашение на цифровую подпись найдено
                errorNote = "";
                result.put("CLIENTPROFILEAGREEMENT", checkConfirmEventResult.get("CLIENTPROFILEAGREEMENT"));
            } else {
                errorNote = checkConfirmEventErrorStr;
            }
        }

        boolean isDigitalSignatureConfirmed = errorNote.isEmpty();
        result.put("ISDIGITALSIGNATURECONFIRMED", isDigitalSignatureConfirmed);

        if (isDigitalSignatureConfirmed) {
            // успех
            logger.debug("dsB2BWebIsDigitalSignatureConfirmed success.");
        } else {
            result.put("REASON", errorNote);
            logger.debug("dsB2BWebIsDigitalSignatureConfirmed failed with error note = " + errorNote);
        }
        logger.debug("dsB2BWebIsDigitalSignatureConfirmed final result: " + result);
        logger.debug("dsB2BWebIsDigitalSignatureConfirmed end.");
        return result;
    }*/

    // «Проверка рамочного соглашения на цифровую подпись»
    @WsMethod(requiredParams = {"clientId"})
    public Map<String, Object> dsB2BWebIsDigitalSignatureConfirmedByClientID(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BWebIsDigitalSignatureConfirmedByClientID...");

        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);

        Long clientID = getLongParamLogged(params, "clientId");
        boolean isDigitalSignatureConfirmed = false;
        Map<String, Object> digitalSignatureConfirmRes = null;

        String errorNote = "При проверке рамочного соглашения на цифровую подпись возникла ошибка!";
        Map<String, Object> result = new HashMap<String, Object>();

        if (clientID == null) {
            logger.error("Missed required parameters for digital signature confirmation existance check! Details (found parameters): " + params);
            errorNote = "Недостаточно входных параметров для проверки рамочного соглашения на цифровую подпись!";
            //isDigitalSignatureConfirmed = errorNote.isEmpty();
        } else {

            Map<String, Object> clientParams = new HashMap<String, Object>();
            clientParams.put("clientId", clientID);
            List<Map<String, Object>> clientProfileList = dctFindByExample(CLIENT_PROFILE_ENTITY_NAME, clientParams, isCallFromGate);

            Long clientProfileID = null;
            Map<String, Object> clientProfile = null;
            if (clientProfileList != null && clientProfileList.size() == 1) {
                clientProfile = clientProfileList.get(0);
            }
            clientProfileID = getLongParamLogged(clientProfile, "id");
            if (clientProfileID == null) {
                logger.error(String.format("Unable to find single client profile by client id (ClientID) = %d! Details (found client profile list): ", clientID) + clientProfileList);
                errorNote = "Не удалось получить профиль клиента по указанному идентфикатору клиента для проверки рамочного соглашения на цифровую подпись!";
            } else {
                Map<String, Object> clientProfileParams = new HashMap<String, Object>();
                clientProfileParams.put("clientProfileId", clientProfileID);
                clientProfileParams.put(RETURN_AS_HASH_MAP, true);
                digitalSignatureConfirmRes = this.callServiceLogged(THIS_SERVICE_NAME, "dsB2BWebIsDigitalSignatureConfirmed", clientProfileParams, login, password);
                isDigitalSignatureConfirmed = getBooleanParamLogged(digitalSignatureConfirmRes, "ISDIGITALSIGNATURECONFIRMED", Boolean.FALSE);
                errorNote = getStringParamLogged(digitalSignatureConfirmRes, "REASON");
            }
        }

        result.put("ISDIGITALSIGNATURECONFIRMED", isDigitalSignatureConfirmed);
        if (isDigitalSignatureConfirmed) {
            // успех
            if (digitalSignatureConfirmRes != null) {
                result.put("CLIENTPROFILEAGREEMENT", digitalSignatureConfirmRes.get("CLIENTPROFILEAGREEMENT"));
            }
            logger.debug("dsB2BWebIsDigitalSignatureConfirmedByClientID success.");
        } else {
            if (errorNote.isEmpty()) {
                errorNote = "При проверке рамочного соглашения на цифровую подпись возникла ошибка!";
            }
            result.put("REASON", errorNote);
            logger.debug("dsB2BWebIsDigitalSignatureConfirmedByClientID failed with error note = " + errorNote);
        }

        logger.debug("dsB2BWebIsDigitalSignatureConfirmedByClientID final result: " + result);

        logger.debug("dsB2BWebIsDigitalSignatureConfirmedByClientID end.");

        return result;
    }

    Map<String, Object> findClientProfile(Long id) throws Exception {
        return dctFindById(PCLIENT_VER_ENTITY_NAME, id);
    }

    // ЛК 2.1: "необходимо анализировать наличие соответствующего типа «Пользовательское соглашение» в статусе «Действует»"
    private Map<String, Object> checkIsDigitalSignatureConfirmedByClientProfileAgreement(Long clientProfileId, String login, String password) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String errorNote = "Произошла ошибка в процессе анализа наличия соответствующего типа «Пользовательское соглашение» в статусе «Действует»!";

        // "сохранение соглашений и проверку на их наличие и актуальность следует выполнять с учетом изменения связи соглашений"
        Map<String, Object> clientProfileId_EN = findClientProfile(clientProfileId);
        Long clientId = (Long) clientProfileId_EN.get("clientId");

        Map<String, Object> agreementParams = new HashMap<String, Object>();
        agreementParams.put("clientId", clientId);
        Long kindStatusId = getStatusIdBySysName("B2B_CLIENTPROFILE_AGREEMENT_ACTIVE");
        agreementParams.put("stateId", kindStatusId); // todo: заменить на нормальную поддержку новой системы состояний через аспекты словарной системы, hibernate и пр., когда она будет реализована
        Long typeAgreementId = getAgreementKindIdBySysName("AGREEMENT_DIGITAL_SIGNATURE");
        agreementParams.put("typeAgreementId", typeAgreementId);
        List<Map<String, Object>> existedAgreementList = dctFindByExample(CLIENT_AGREEMENT_ENTITY_NAME, agreementParams);
        if (existedAgreementList == null) {
            logger.error("Error during current digtal signatures search!");
            errorNote = "Не удалось определить наличие соответствующего типа «Пользовательское соглашение» в статусе «Действует»!";
        } else if (existedAgreementList.size() > 0) {
            Map<String, Object> agreement = existedAgreementList.get(0);
            // "сохранение соглашений и проверку на их наличие и актуальность следует выполнять с учетом изменения связи соглашений"
            agreement.remove("clientId_EN");
            result.put("CLIENTPROFILEAGREEMENT", agreement);
            errorNote = "";
        } else {
            errorNote = "Отсутствуют «Пользовательское соглашение» соответствующего типа в статусе «Действует»!";
        }
        result.put(ERROR, errorNote);
        return result;
    }

}
