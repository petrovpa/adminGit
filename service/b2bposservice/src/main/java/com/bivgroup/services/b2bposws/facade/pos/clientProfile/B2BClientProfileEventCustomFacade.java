/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.clientProfile;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.mobilerest.MobileRestError;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * @author mmamaev
 */
public class B2BClientProfileEventCustomFacade extends B2BDictionaryBaseFacade {

    private Logger logger = Logger.getLogger(this.getClass());

    private static Map<String, Map<String, Object>> eventTypeBySysName = null;
    private static Map<String, Long> eventTypeEIDBySysName = null;

    // системные наименования типов событий
    public static final String EVENT_SYSNAME_REGISTRATION_REQUEST = "REGISTRATION_REQUEST";
    //public static final String EVENT_SYSNAME_WEB_SMS_CODE_SEND = "WEB_SMS_CODE_SEND";
    //public static final String EVENT_SYSNAME_WEB_SMS_CODE_CHECK = "WEB_SMS_CODE_CHECK";
    public static final String EVENT_SYSNAME_DEVICE_SMS_CODE_SEND = "DEVICE_SMS_CODE_SEND";
    public static final String EVENT_SYSNAME_DEVICE_SMS_CODE_CHECK = "DEVICE_SMS_CODE_CHECK";
    public static final String EVENT_SYSNAME_DEVICE_TOKEN_GENERATED = "DEVICE_TOKEN_GENERATED";
    public static final String EVENT_SYSNAME_DEVICE_LOGIN_SUCCESS = "DEVICE_LOGIN_SUCCESS";
    //public static final String EVENT_SYSNAME_WEB_LOGIN_SUCCESS = "WEB_LOGIN_SUCCESS";

    // системные наименования типов событий для соглашения на цифровую подпись
    public static final String EVENT_SYSNAME_DGTSGN_SMS_CODE_SEND = "DIGITAL_SIGNATURE_SMS_CODE_SEND";
    public static final String EVENT_SYSNAME_DGTSGN_SMS_CODE_CHECK = "DIGITAL_SIGNATURE_SMS_CODE_CHECK";

    // для подписания с устройства
    public static final String EVENT_SYSNAME_DEVICE_APPL_SIGN = "DEVICE_INSURANCE_APPLICATION_SIGN";

    /** Отправка кода SMS для альтернативной аутентификации */
    public static final String EVENT_SYSNAME_WEB_ALT_AUTH_SMS_CODE_SEND = "WEB_ALT_AUTH_SMS_CODE_SEND";
    /** Подтверждение кода SMS для альтернативной аутентификации */
    public static final String EVENT_SYSNAME_WEB_ALT_AUTH_SMS_CODE_CHECK = "WEB_ALT_AUTH_SMS_CODE_CHECK";

    // "Вид приложения (ModeApp): 0 - Web, 1 - Mobile"
    public static final Long EVENT_MODE_APP_WEB = 0L;
    public static final Long EVENT_MODE_APP_DEVICE = 1L;

    public static final String EVENT_TYPE_SYS_NAME_PARAMNAME = "eventTypeSysName";

    Map<String, String> defaultEventMsgBySysName = null;

    public B2BClientProfileEventCustomFacade() {
        super();
        defaultEventMsgBySysName = new HashMap<String, String>();
        defaultEventMsgBySysName.put(EVENT_SYSNAME_REGISTRATION_REQUEST, "Запрос на регистрацию успешно исполнен.");
        //defaultEventMsgBySysName.put(EVENT_SYSNAME_WEB_SMS_CODE_SEND, "Отправка кода SMS для входа успешно выполнена.");
        //defaultEventMsgBySysName.put(EVENT_SYSNAME_WEB_SMS_CODE_CHECK, "Подтверждение кода SMS для входа произведено успешно.");
        //defaultEventMsgBySysName.put(EVENT_SYSNAME_WEB_LOGIN_SUCCESS, "Вход через Web выполнен успешно.");
        // todo: аналогично для EVENT_SYSNAME_DEVICE_*
        defaultEventMsgBySysName.put(EVENT_SYSNAME_DEVICE_SMS_CODE_SEND, "Отправка кода SMS для регистрации устройства успешно выполнена.");
        defaultEventMsgBySysName.put(EVENT_SYSNAME_DEVICE_SMS_CODE_CHECK, "Подтверждение кода SMS для регистрации устройства произведено успешно.");
        defaultEventMsgBySysName.put(EVENT_SYSNAME_DEVICE_TOKEN_GENERATED, "Формирование токена устройства для входа выполнено успешно.");
        defaultEventMsgBySysName.put(EVENT_SYSNAME_DEVICE_LOGIN_SUCCESS, "Вход с устройства выполнен успешно.");
        // события для соглашения на цифровую подпись
        defaultEventMsgBySysName.put(EVENT_SYSNAME_DGTSGN_SMS_CODE_SEND, "Отправка кода SMS для рамочного соглашения на цифровую подпись успешно выполнена.");
        defaultEventMsgBySysName.put(EVENT_SYSNAME_DGTSGN_SMS_CODE_CHECK, "Подтверждение кода SMS для рамочного соглашения на цифровую подпись произведено успешно.");
        // для подписания с устройства
        defaultEventMsgBySysName.put(EVENT_SYSNAME_DEVICE_APPL_SIGN, "Подписание заявление на страхование с устройства произведено успешно.");
        // альт. аутентификация
        defaultEventMsgBySysName.put(EVENT_SYSNAME_WEB_ALT_AUTH_SMS_CODE_SEND, "Отправка кода SMS для альтернативной аутентификации успешно выполнена.");
        defaultEventMsgBySysName.put(EVENT_SYSNAME_WEB_ALT_AUTH_SMS_CODE_CHECK, "Подтверждение кода SMS для альтернативной аутентификации произведено успешно.");
    }

    private void initEventTypeMaps() throws Exception {
        logger.debug("Analysing event types classifer data...");
        // обращение к словарной системе для получения данных классификатора
        String clsName = KIND_EVENT_CLIENT_PROFILE_ENTITY_NAME;
        Map<String, Object> clsDataParams = new HashMap<String, Object>();
        boolean isCallFromGate = false;
        List<Map<String, Object>> hbDataList = dctFindByExample(clsName, clsDataParams, isCallFromGate);
        eventTypeBySysName = getMapByFieldStringValues(hbDataList, "sysname");
        eventTypeEIDBySysName = new HashMap<String, Long>();
        for (Map.Entry<String, Map<String, Object>> eventType : eventTypeBySysName.entrySet()) {
            String sysName = eventType.getKey();
            Map<String, Object> event = eventType.getValue();
            Long typeEID = getLongParam(event, "eId");
            eventTypeEIDBySysName.put(sysName, typeEID);
            logger.debug(String.format("Event with system name (Sysname) of '%s' got id (EID) = %d.", sysName, typeEID));
        }
        logger.debug("Analysing event types classifer data finshed.");
    }

    private Map<String, Object> getEventTypeBySysName(String eventTypeSysName) throws Exception {
        logger.debug(String.format("Getting event type map by event system name (Sysname) = %s...", eventTypeSysName));
        if (eventTypeBySysName == null) {
            initEventTypeMaps();
        }
        Map<String, Object> eventType = eventTypeBySysName.get(eventTypeSysName);
        logger.debug(String.format("Getting event type map finished - event with system name (Sysname) of '%s' = ", eventTypeSysName) + eventType);
        return eventType;
    }

    private Long getEventTypeIDBySysName(String eventTypeSysName) throws Exception {
        logger.debug(String.format("Getting event type id (EID) by event system name (Sysname) = %s...", eventTypeSysName));
        if (eventTypeBySysName == null) {
            initEventTypeMaps();
        }
        Long eventTypeEID = eventTypeEIDBySysName.get(eventTypeSysName);
        logger.debug(String.format("Getting event type id (EID) finished - event with system name (Sysname) of '%s' got id (EID) = %d.", eventTypeSysName, eventTypeEID));
        return eventTypeEID;
    }

    @WsMethod(requiredParams = {EVENT_TYPE_SYS_NAME_PARAMNAME})
    public Map<String, Object> dsB2BClientProfileEventCreate(Map<String, Object> params) throws Exception {
        boolean isCallFromGate = isCallFromGate(params);
        String eventTypeSysName = getStringParamLogged(params, EVENT_TYPE_SYS_NAME_PARAMNAME);
        Long eventTypeEID = getEventTypeIDBySysName(eventTypeSysName);
        if (eventTypeEID != null) {
            params.put("eventTypeId", eventTypeEID);
            if (params.get("eventDate") == null) {
                params.put("eventDate", new Date());
            }
            String errorCode = getStringParamLogged(params, MobileRestError.ERROR_CODE_EVENT_FIELDNAME);
            if (errorCode.isEmpty()) {
                errorCode = MobileRestError.SYSTEM_SUCCESS.getCodeStr();
                params.put(MobileRestError.ERROR_CODE_EVENT_FIELDNAME, errorCode);
            }
            // текст ошибки - с v2 согласно ФТ
            if (params.get(MobileRestError.ERROR_TEXT_EVENT_FIELDNAME) == null) {
                // v2: теперь коды и текст ошибок регламентированы и указаны в ФТ - нужно использовать их
                //params.put(MobileRestError.ERROR_TEXT_EVENT_FIELDNAME, defaultEventMsgBySysName.get(eventTypeSysName));
                params.put(MobileRestError.ERROR_TEXT_EVENT_FIELDNAME, MobileRestError.findByCode(errorCode).getMsg());
            }
            // примечание об ошибке
            if (params.get(MobileRestError.ERROR_NOTE_EVENT_FIELDNAME) == null) {
                String errorNote = getStringParamLogged(params, MobileRestError.ERROR_NOTE_PARAMNAME);
                if (errorNote.isEmpty()) {
                    errorNote = defaultEventMsgBySysName.get(eventTypeSysName);
                }
                params.put(MobileRestError.ERROR_NOTE_EVENT_FIELDNAME, errorNote);
            }
            // todo: попросить bash-а исправить, ИД типа должно хватать для сохранения ссылки на классификатор
            //setLinkParamWTF(params, "eventTypeId", eventTypeEID);
            params.put("eventTypeId", eventTypeEID);
            Map<String, Object> сlientProfileEvent = dctOnlySave(CLIENT_PROFILE_EVENT_ENTITY_NAME, params, isCallFromGate);
            return сlientProfileEvent;
        } else {
            throw new Exception("No KindEventClientProfile records can be found by specified system name: " + eventTypeSysName);
        }
    }

    @WsMethod(requiredParams = {EVENT_TYPE_SYS_NAME_PARAMNAME})
    public Map<String, Object> dsB2BClientProfileEventTypeGetBySysName(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BClientProfileEventTypeGetBySysName start...");
        String eventTypeSysName = getStringParamLogged(params, EVENT_TYPE_SYS_NAME_PARAMNAME);
        Map<String, Object> eventType = getEventTypeBySysName(eventTypeSysName);
        logger.debug("dsB2BClientProfileEventTypeGetBySysName finished.");
        return eventType;
    }

}
