/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.paws.facade.pa.custom;

import com.bivgroup.services.paws.facade.PABaseFacade;
import com.bivgroup.services.paws.system.Constants;
import static com.bivgroup.services.paws.system.Constants.*;
import com.bivgroup.services.paws.system.PAException;
import com.bivgroup.services.paws.system.SmsSender;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.cayenne.access.Transaction;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author kkulkov
 */
@BOName("ContractCustom")
public class ContractCustomFacade extends PABaseFacade {

    private static final String PAWS_SERVICE_NAME = Constants.PAWS;
    private static final String CRMWS_SERVICE_NAME = Constants.CRMWS;
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    private static final String SIGNB2BPOSWS_SERVICE_NAME = Constants.SIGNB2BPOSWS;
    private static final String SYSNAME_HOUSE900 = "00006"; // Защита дома 900

    private String smsText = "";
    private String smsUser = "";
    private String smsPwd = "";
    private String smsFrom = "";

    protected Logger logger = Logger.getLogger(ContractCustomFacade.class);

    private static volatile int attachableContractsProcessingThreadCount = 0;

    // Системные имена статусов для движения денежных средств
    // (должны совпадать с указанными в Mort900CustomFacade из i900)
    private static final String B2B_BANKCASHFLOW_INQUEUE = "B2B_BANKCASHFLOW_INQUEUE"; // Поставлен в очередь

    // шаблон текста СМС, отправляемого при автоматическом прикреплении создаваемых договоров в ЛК 
    // todo: действительный текст СМС
    // (в ФТ от 16.02.2016 указан как "Текст SMS";
    //  в письме от 26.02.2016 - "Ваш полис оформлен и находится в личном кабинете https://online.sberbankins.ru/lk/index.html. Для авторизации укажите Ваш номер телефона.")
    // todo: возможно, чтение шаблона из БД/конфига?
    private static final String CONTRACT_ATTACHMENT_SMS_TEMPLATE = "Ваш полис оформлен и находится в личном кабинете https://online.sberbankins.ru/lk/index.html. Для авторизации укажите Ваш номер телефона.";
    private static final String CONTRACT_ATTACHMENT_SMS_900TMTEMPLATE = "Полис страхования ипотеки оформлен и находится в личном кабинете https://online.sberbankins.ru/lk/index.html. Вход по номеру телефона.";
    private static final String CONTRACT_ATTACHMENT_SMS_HOUSE900TMTEMPLATE = "Полис страхования имущества оформлен и находится в личном кабинете https://online.sberbankins.ru/lk/index.html. Вход по номеру телефона. Оригинал полиса отправлен по адресу застрахованного имущества.";

    @WsMethod(requiredParams = {"CONTRNUMBER", "STARTDATE"})
    public Map<String, Object> dsFindContractForAttach(Map<String, Object> params) throws Exception {

        logger.debug("Поиск договора для прикрепления в ЛК...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("CONTRNUMBER", params.get("CONTRNUMBER"));
        queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
        GregorianCalendar cal = new GregorianCalendar();

        // возможно, здесь не потребуется, когда преобразование дат во входных параметрах будет перенесено в BoxPropertyGate
        SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd.MM.yyyy");
        cal.setTime(format.parse(params.get("STARTDATE").toString()));
        //Date startDate = getDateParam(params.get("STARTDATE"));
        //cal.setTime(startDate);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        queryParams.put("DOCUMENTSTARTDATE", new Date(cal.getTimeInMillis()));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        queryParams.put("DOCUMENTFINISHDATE", new Date(cal.getTimeInMillis()));
        Map<String, Object> contract = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParamEx", queryParams, login, password);
        Map<String, Object> result = new HashMap<String, Object>();
        String errorStr = "";
        Long errorCode = 0L;
        Object contrID = contract.get("CONTRID");
        //result.put("ERRORCODE", 0L);
        if (contrID != null) {
            queryParams.clear();
            queryParams.put("CONTRID", contrID);
            Map<String, Object> resParams = this.callService(PAWS_SERVICE_NAME, "dsPaContractBrowseListByParam", queryParams, login, password);
            if (!((resParams != null) && ((resParams.get(RESULT) != null) && ((List) resParams.get(RESULT)).isEmpty()))) {
                //result.clear();
                //result.put("ERROR", "Договор уже включен в состав текущего или другого аккаунта.");
                //result.put("ERRORCODE", 2L);
                errorStr = "Договор уже включен в состав текущего или другого аккаунта.";
                errorCode = 2L;
            } else {
                result.put("CONTRID", contrID);

                // проверка наличия отчества у страхователя - для определения обязательности поля в angular-интерфейсе
                boolean isMiddleNameRequired = contract.get("INSURERMIDDLENAME") != null; // отчество страхователя возвращает dsB2BContractBrowseListByParamEx
                result.put("ISMIDDLENAMEREQUIRED", isMiddleNameRequired);
                if (logger.isDebugEnabled()) {
                    logger.debug("Insurer middle name will " + (isMiddleNameRequired ? "be required." : "not be required."));
                }

                Object insurerID = contract.get("INSURERID");
                if (insurerID != null) {
                    logger.debug("Определен идентификатор страхователя по найденному договору: '" + insurerID + "'.");
                    result.put("INSURERID", insurerID);
                    Map<String, Object> docListParams = new HashMap<String, Object>();
                    docListParams.put("PARTICIPANTID", insurerID);
                    List<Map<String, Object>> personDocList = WsUtils.getListFromResultMap(this.callExternalService(CRMWS_SERVICE_NAME, "personDocGetListByParticipantId", docListParams, login, password));
                    if ((personDocList != null) && (!personDocList.isEmpty())) {
                        Object firstDocTypeSysName = (personDocList.get(0)).get("DOCTYPESYSNAME");
                        result.put("DOCTYPESYSNAME", firstDocTypeSysName);
                        logger.debug("Определено системное имя документа, удостоверяющего личность страхователя: '" + firstDocTypeSysName + "'.");
                    } else {
                        errorStr = "Для найденного договора не удалось определить документ, удостоверяющий личность страхователя.";
                        errorCode = 4L;
                    }
                } else {
                    errorStr = "Для найденного договора не удалось определить страхователя.";
                    errorCode = 3L;

                }
            }
        } else {
            //result.clear();
            //result.put("ERROR", "Договор не найден.");
            //result.put("ERRORCODE", 1L);
            errorStr = "Договор не найден.";
            errorCode = 1L;
        }

        if (errorCode.intValue() != 0) {
            result.clear();
            result.put("ERROR", errorStr);
            logger.debug("Поиск договора для прикрепления завершен с ошибкой: " + errorCode + " - " + errorStr);
        } else {
            logger.debug("Поиск договора для прикрепления успешно завершен. Идентификатор найденного договора: '" + contrID + "'.");
        }
        result.put("ERRORCODE", errorCode);

        return result;
    }

    private Boolean errorExist = Boolean.FALSE;
    private String errorText = "";

    private Map<String, Object> checkDataMap(Map<String, Object> checkMap, Map<String, Object> errorMap, Map<String, Object> contrMap) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Map.Entry<String, Object> kv : checkMap.entrySet()) {
            String key = kv.getKey();
            logger.debug("key-" + key);
            Object val = kv.getValue();
            if (val instanceof List) {
                logger.debug("val-list-" + val.toString());
                List<Map<String, Object>> callErrorList = null;
                if (errorMap != null) {
                    callErrorList = (List<Map<String, Object>>) errorMap.get(key);
                }
                result.put(key, checkDataList((List<Map<String, Object>>) val, callErrorList, (List<Map<String, Object>>) contrMap.get(key)));
            } else if (val instanceof Map) {
                logger.debug("val-map-" + val.toString());
                Map<String, Object> callErrorMap = null;
                if (errorMap != null) {
                    callErrorMap = (Map<String, Object>) errorMap.get(key);
                }
                result.put(key, checkDataMap((Map<String, Object>) val, callErrorMap, (Map<String, Object>) contrMap.get(key)));
            } else if (null != val) {
                if (contrMap.get(key) instanceof Double) {
                    logger.debug("val-double-" + val.toString());
                    logger.debug("compare-to-" + contrMap.get(key).toString());
                    Boolean res;
                    try {
                        res = Double.valueOf(val.toString()).equals(contrMap.get(key));
                    } catch (Exception e) {
                        res = false;
                    }
                    logger.debug("res-" + res.toString());
                    if (!res) {
                        if ((errorMap != null) && (errorMap.get(key) != null)) {
                            if (errorText.isEmpty()) {
                                errorText = errorMap.get(key).toString();
                            } else {
                                errorText += "<br/>" + errorMap.get(key).toString();
                            }
                        }
                        errorMap.get(key);
                    }
                    errorExist = errorExist || !res;
                    result.put(key, res);
                } else if (contrMap.get(key) instanceof Long) {
                    logger.debug("val-long-" + val.toString());
                    logger.debug("compare-to-" + contrMap.get(key).toString());
                    Boolean res;
                    try {
                        res = Long.valueOf(val.toString()).equals(contrMap.get(key));
                    } catch (Exception e) {
                        res = false;
                    }
                    logger.debug("res-" + res.toString());
                    errorExist = errorExist || !res;
                    result.put(key, res);
                } else {
                    logger.debug("val-other-" + val.toString());
                    logger.debug("compare-to-" + contrMap.get(key).toString());

                    Boolean res = val.equals(contrMap.get(key));
                    logger.debug("res-" + res.toString());
                    if (!res) {
                        if ((errorMap != null) && (errorMap.get(key) != null)) {
                            if (errorText.isEmpty()) {
                                errorText = errorMap.get(key).toString();
                            } else {
                                errorText += "<br/>" + errorMap.get(key).toString();
                            }
                        }
                        errorMap.get(key);
                    }
                    errorExist = errorExist || !res;
                    result.put(key, res);
                }
            } else if (null == contrMap.get(key)) {
                result.put(key, 0L);
                errorExist = errorExist || false;
            } else {
                result.put(key, 1L);
                if ((errorMap != null) && (errorMap.get(key) != null)) {
                    if (errorText.isEmpty()) {
                        errorText = errorMap.get(key).toString();
                    } else {
                        errorText += "<br/>" + errorMap.get(key).toString();
                    }
                }
                errorMap.get(key);
                errorExist = errorExist || true;
            }
        }
        return result;
    }

    private List<Map<String, Object>> checkDataList(List<Map<String, Object>> checkList, List<Map<String, Object>> errorList, List<Map<String, Object>> contrList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Iterator<Map<String, Object>> errorMapIt = null;
        if (errorList != null) {
            errorMapIt = errorList.iterator();
        }
        for (Map<String, Object> keymap : checkList) {
            String valueField = "";
            String keyFieldName = "";
            for (Map.Entry<String, Object> entrySet : keymap.entrySet()) {
                String key = entrySet.getKey();

                if (key.contains("SYSNAME")) {
                    keyFieldName = key;
                }
                if (key.contains("VALUE")) {
                    valueField = key;
                }
            }
            if (!keyFieldName.isEmpty()) {
                String sysname = keymap.get(keyFieldName).toString();
                Object value = keymap.get(valueField);
                Map<String, Object> errorMap = null;
                if (errorMapIt != null) {
                    errorMap = errorMapIt.next();
                }
                for (Map<String, Object> contrMap : contrList) {
                    String cSysname = contrMap.get(keyFieldName).toString();
                    if (sysname.equalsIgnoreCase(cSysname)) {
                        Object cValue = contrMap.get(valueField);
                        Map<String, Object> check = new HashMap<String, Object>();
                        for (Map.Entry<String, Object> entrySet : keymap.entrySet()) {
                            String key = entrySet.getKey();
                            logger.debug("key-" + key);

                            if (!(key.equals(keyFieldName))) {
                                Object val = entrySet.getValue();
                                if (null != val) {
                                    if (contrMap.get(key) instanceof Double) {
                                        logger.debug("val-double-" + val.toString());
                                        logger.debug("compare-to-" + contrMap.get(key).toString());
                                        Boolean res;
                                        try {
                                            res = Double.valueOf(val.toString()).equals(contrMap.get(key));
                                        } catch (Exception e) {
                                            res = false;
                                        }
                                        logger.debug("res-" + res.toString());
                                        if (!res) {
                                            if ((errorMap != null) && (errorMap.get(key) != null)) {
                                                if (errorText.isEmpty()) {
                                                    errorText = errorMap.get(key).toString();
                                                } else {
                                                    errorText += "<br/>" + errorMap.get(key).toString();
                                                }
                                            }
                                            errorMap.get(key);
                                        }
                                        errorExist = errorExist || !res;
                                        check.put(key, res);
                                    } else if (contrMap.get(key) instanceof Long) {
                                        logger.debug("val-long-" + val.toString());
                                        logger.debug("compare-to-" + contrMap.get(key).toString());
                                        Boolean res;
                                        try {
                                            res = Long.valueOf(val.toString()).equals(contrMap.get(key));
                                        } catch (Exception e) {
                                            res = false;
                                        }
                                        logger.debug("res-" + res.toString());
                                        if (!res) {
                                            if ((errorMap != null) && (errorMap.get(key) != null)) {
                                                if (errorText.isEmpty()) {
                                                    errorText = errorMap.get(key).toString();
                                                } else {
                                                    errorText += "<br/>" + errorMap.get(key).toString();
                                                }
                                            }
                                            errorMap.get(key);
                                        }
                                        errorExist = errorExist || !res;
                                        check.put(key, res);
                                    } else {
                                        logger.debug("val-other-" + val.toString());
                                        logger.debug("compare-to-" + contrMap.get(key).toString());
                                        Boolean res = val.equals(contrMap.get(key));
                                        errorExist = errorExist || !res;
                                        logger.debug("res-" + res.toString());
                                        if (!res) {
                                            if ((errorMap != null) && (errorMap.get(key) != null)) {
                                                if (errorText.isEmpty()) {
                                                    errorText = errorMap.get(key).toString();
                                                } else {
                                                    errorText += "<br/>" + errorMap.get(key).toString();
                                                }
                                            }
                                            errorMap.get(key);
                                        }
                                        check.put(key, res);
                                    }
                                }
                            }
                        }
                        result.add(check);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public ContractCustomFacade() {
        super();
        init();
    }

    private void init() {
        Config config = Config.getConfig(PAWS_SERVICE_NAME);
        this.smsText = config.getParam("SMSTEXT", "Уважаемый клиент! Ваш пароль для подтверждения введенных данных: ");
        this.smsUser = config.getParam("SMSUSER", "sberinsur");
        this.smsPwd = config.getParam("SMSPWD", "KD9zVoeR123");
        this.smsFrom = config.getParam("SMSFROM", "SberbankIns");
        try {
            this.isDebug = Long.valueOf(config.getParam("DEBUG", "0"));
        } catch (Exception e) {
            this.isDebug = 0L;
        }
    }

    @WsMethod(requiredParams = {"CHECKMAP"}) //, 
    public Map<String, Object> dsAttachContract(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        errorExist = Boolean.FALSE;
        errorText = "";
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("CONTRID", params.get("CONTRID"));
        queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> contrData = this.callService(B2BPOSWS, "dsB2BContractUniversalLoad", queryParams, login, password);
        Map<String, Object> errorList = new HashMap<String, Object>();
        Map<String, Object> result = new HashMap<String, Object>();

        if (params.get("CHECKMAP") != null) {
            Map<String, Object> checkmap = (Map<String, Object>) params.get("CHECKMAP");
            Map<String, Object> errormap = (Map<String, Object>) params.get("ERRORMAP");
            logger.debug("before checkDataMap");
            logger.debug("checkmap: " + checkmap.toString());
            logger.debug("errormap: " + errormap.toString());
            logger.debug("contrData: " + contrData.toString());

            errorList = checkDataMap(checkmap, errormap, contrData);
            logger.debug("after checkDataMap");
            logger.debug("errorList: " + errorList.toString());
        }
        if (errorExist) {
            result.clear();
            result.put("ERRORLIST", errorList);
            result.put("ERRORCODE", 1L);
            result.put("ERRORTEXT", errorText);
            return result;
        }
        return result;
    }

    @WsMethod(requiredParams = {}) //, 
    public Map<String, Object> dsPAContractBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("PAUSERID", params.get("CURRENT_USERID"));
        queryParams.put("CONTRID", params.get("CONTRID"));
        Map<String, Object> result = this.selectQuery("dsPAContractBrowseListByParamEx", "dsPAContractBrowseListByParamExCount", queryParams);
        return result;
    }

    @WsMethod(requiredParams = {"CONTRID", "CURRENT_USERID"}) //, 
    public Map<String, Object> dsPAContractValidAndAttachContract(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        // идентификаторы прикрепляемого договора и учетной записи пользователя ЛК
        Long contractID = getLongParam(params, "CONTRID");
        Long paUserID = getLongParam(params, "CURRENT_USERID");
        logger.debug(String.format("Attaching contract with id (CONTRID) = [%d] to PA account with id (PAUSERID) = [%d]...", contractID, paUserID));

        // прикрепление договора к учетной записи пользователя ЛК
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("CONTRID", contractID);
        queryParams.put("NAME", params.get("NAME")); // ?
        queryParams.put("PAUSERID", paUserID);
        queryParams.put("RELIABILITYLEVEL", 1L);
        result.putAll(queryParams);
        queryParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> createResult = this.callService(PAWS_SERVICE_NAME, "dsPaContractCreate", queryParams, login, password);
        logger.debug("Attaching contract result: " + createResult);

        // анализ результата прикрепления
        if (createResult != null) {
            Long paObjectID = getLongParam(createResult, "PAOBJECTID");
            result.put("PAOBJECTID", paObjectID); // дополнение результата идентификатором созданной связи
            if (paObjectID != null) {
                // договор успешно прикреплен - проверка была ли запрошена отправка информационного СМС-сообщения
                // (запрашивать отправку СМС здесь можно только если прикрепление окончательное и не предполагается откат транзакции)
                Map<String, Object> sendResult = trySendInfoSMSAboutContractAttachment(params);
                result.put("SMSSENDRESULT", sendResult); // дополнение результата сведениями об отправке СМС
            }
        }
        logger.debug("Attaching contract to PA account finished.");
        return result;
    }

    // проверка была ли запрошена отправка информационного СМС-сообщения о прикреплении договора в ЛК и выполнение отправки
    private Map<String, Object> trySendInfoSMSAboutContractAttachment(Map<String, Object> infoSMS) throws IOException {
        logger.debug("Checking if sending informational SMS about contract attachment now is requested...");
        Map<String, Object> result = new HashMap<String, Object>();
        String infoSMSPhoneNumber = getStringParam(infoSMS, "SMSPHONENUMBER");
        String infoSMSText = getStringParam(infoSMS, "SMSTEXT");
        // проверка была ли запрошена отправка информационного СМС-сообщения
        if ((!infoSMSPhoneNumber.isEmpty()) && (!infoSMSText.isEmpty())) {
            // если были переданы текст и номер - отправка СМС
            logger.debug("Informational SMS about contract attachment IS requested.");
            result = sendInfoSMSAboutContractAttachment(infoSMSPhoneNumber, infoSMSText);
        } else {
            logger.debug("Sending informational SMS about contract attachment now IS NOT requested, sending skipped.");
        }
        return result;
    }

    // отправка информационного СМС-сообщения о прикреплении договора в ЛК
    private Map<String, Object> sendInfoSMSAboutContractAttachment(String infoSMSPhoneNumber, String infoSMSText) throws IOException {
        logger.debug("Sending informational SMS about contract attachment...");
        logger.debug("Phone number: " + infoSMSPhoneNumber);
        logger.debug("SMS text: " + infoSMSText);
        SmsSender smsSender = new SmsSender();
        Map<String, Object> sendResult = smsSender.doGet(this.smsUser, this.smsPwd, this.smsFrom, infoSMSPhoneNumber, infoSMSText);
        logger.debug("Sending informational SMS about contract attachment finished with result: " + sendResult);
        return sendResult;
    }

    private Long isDebug = 0L;

    private String generateSMSCode() {
        Random r = new Random();
        int value = r.nextInt(1000000);
        String result = String.valueOf(value);
        while (result.length() < 6) {
            result = "0" + result;
        }
        if (isDebug > 0) {
            return "1";
        } else {
            return result;
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsPAAttachContractSendSMSCode(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("USERID", params.get("CURRENT_USERID"));
        queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> paUserMap = this.callService(Constants.PAWS, "dsPaUserBrowseListByParam", queryParams, login, password);
        if ((paUserMap != null) && (paUserMap.get("USERID") != null)) {
            GregorianCalendar gcToday = new GregorianCalendar();
            gcToday.setTime(new Date());
            String smsCode;
            if (paUserMap.get("SMSCODE") != null) {
                smsCode = paUserMap.get("SMSCODE").toString();
            } else {
                smsCode = generateSMSCode();
            }
            // отправка СМС клиенту
            SmsSender smsSender = new SmsSender();
            if (params.get("PHONENUMBER") == null) {
                if (paUserMap.get("PHONENUMBER") != null) {
                    params.put("PHONENUMBER", paUserMap.get("PHONENUMBER"));
                }
            }
            if (params.get("PHONENUMBER") != null) {
                Map<String, Object> sendRes = smsSender.doGet(this.smsUser, this.smsPwd, this.smsFrom, params.get("PHONENUMBER").toString(),
                        this.smsText + smsCode);
                // сохранить код и время отправки в базу
                Map<String, Object> updParams = new HashMap<String, Object>();
                updParams.put("USERID", paUserMap.get("USERID"));
                updParams.put("SMSCODE", smsCode);
                updParams.put("SMSCODEDATE", gcToday.getTime());
                XMLUtil.convertDateToFloat(updParams);
                this.callService(Constants.PAWS, "dsPaUserUpdate", updParams, login, password);
                result.put("SENDRES", sendRes);
                result.put("NAME", paUserMap.get("NAME"));
                result.put("SURNAME", paUserMap.get("SURNAME"));
                result.put("EMAIL", paUserMap.get("EMAIL"));
            } else {
                result.put("Error", "Не указан номер телефона");
                return result;
            }
        } else {
            result.put("Error", "По указанным данным не определен аккаунт");
            return result;
        }
        return result;
    }

    @WsMethod(requiredParams = {"CONTRID", "INSURERID", "PHONENUMBER", "EMAIL"})
    public Map<String, Object> dsPAAttachContractSendSMSCodeToInsurer(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Object insurerID = params.get("INSURERID");

        Map<String, Object> contactListParams = new HashMap<String, Object>();
        contactListParams.put("PARTICIPANTID", insurerID);
        List<Map<String, Object>> contactList = WsUtils.getListFromResultMap(this.callExternalService(CRMWS_SERVICE_NAME, "contactGetListByParticipantId", contactListParams, login, password));

        String errorStr = null;
        Long errorCode = 0L;

        if ((contactList != null) && (!contactList.isEmpty())) {
            Object personalEmail = null;
            Object mobilePhone = null;
            for (Map<String, Object> contact : contactList) {
                Object contactTypeSysName = contact.get("CONTACTTYPESYSNAME");
                if (contactTypeSysName != null) {
                    String contactTypeSysNameStr = contactTypeSysName.toString();
                    if ((personalEmail == null) && ("PersonalEmail".equalsIgnoreCase(contactTypeSysNameStr))) {
                        personalEmail = contact.get("VALUE");
                    } else if ((mobilePhone == null) && ("MobilePhone".equalsIgnoreCase(contactTypeSysNameStr))) {
                        mobilePhone = contact.get("VALUE");
                    }
                }
            }
            if ((personalEmail != null) && (mobilePhone != null)) {
                if (((personalEmail.toString()).equalsIgnoreCase(params.get("EMAIL").toString()))
                        && ((mobilePhone.toString()).equalsIgnoreCase(params.get("PHONENUMBER").toString()))) {

                    // генерация СМС-кода
                    String smsCode = generateSMSCode();
                    // отправка СМС клиенту
                    SmsSender smsSender = new SmsSender();
                    Map<String, Object> sendRes = smsSender.doGet(this.smsUser, this.smsPwd, this.smsFrom, params.get("PHONENUMBER").toString(), this.smsText + smsCode);
                    logger.debug("sendRes from smsSender.doGet: " + sendRes);
                    // определение текущей даты
                    GregorianCalendar gcToday = new GregorianCalendar();
                    gcToday.setTime(new Date());

                    // сохранение кода и времени отправки в базу
                    Map<String, Object> updParams = new HashMap<String, Object>();
                    updParams.put("CONTRID", params.get("CONTRID"));
                    updParams.put("SMSCODE", smsCode);
                    //updParams.put("SMSCODEDATE", XMLUtil.convertDateToBigDecimal(gcToday.getTime()));
                    Map<String, Object> updateRes = this.callService(Constants.B2BPOSWS, "dsB2BContrUpdate", updParams, login, password);
                    logger.debug("updateRes from dsB2BContrUpdate: " + updateRes);

                    result.put("SENDRES", sendRes);
                    //result.put("NAME", paUserMap.get("NAME"));
                    //result.put("SURNAME", paUserMap.get("SURNAME"));
                    //result.put("EMAIL", paUserMap.get("EMAIL"));

                } else {
                    errorStr = "Введенные контактные данные не сопадают с контактными данными страхователя для найденного договора.";
                    errorCode = 3L;
                }

            } else {
                errorStr = "Для найденного договора не удалось определить номер сотового телефона и/или адрес электронной почты страхователя.";
                errorCode = 2L;
            }

        } else {
            errorStr = "Для найденного договора не удалось определить контактные данные страхователя.";
            errorCode = 1L;
        }

        if (errorCode.intValue() != 0) {
            result.clear();
            result.put("ERROR", errorStr);
            logger.debug("Проверка контактных данных страхователя и отправка СМС-кода завершены с ошибкой: " + errorCode + " - " + errorStr);
        } else {
            logger.debug("Проверка контактных данных страхователя и отправка СМС-кода успешно завершены.");
        }
        result.put("ERRORCODE", errorCode);

        return result;
    }

    @WsMethod(requiredParams = {"SMSCODE"})
    public Map<String, Object> dsAttachContractEnterSMSCode(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("USERID", params.get("CURRENT_USERID"));
        queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> paUserMap = this.callService(Constants.PAWS, "dsPaUserBrowseListByParam", queryParams, login, password);
        if ((paUserMap != null) && (paUserMap.get("USERID") != null)) {
            XMLUtil.convertFloatToDate(paUserMap);
            if (paUserMap.get("SMSCODEDATE") != null) {
                String smsCode = params.get("SMSCODE").toString();
                if (smsCode.equals(paUserMap.get("SMSCODE").toString())) {
                    queryParams.clear();
                    queryParams.put("CONTRID", params.get("CONTRID"));
                    queryParams.put("CURRENT_USERID", params.get("CURRENT_USERID"));
                    this.callService(Constants.PAWS, "dsPAContractValidAndAttachContract", queryParams, login, password);
                    result.put("validsmscode", 1L);
                } else {
                    result.put("Error", "Указан неверный SMS пароль");
                    result.put("validsmscode", 0L);

                    return result;
                }
            }
        }
        return result;
    }

    @WsMethod(requiredParams = {"CONTRID", "SMSCODE", "CURRENT_USERID"})
    public Map<String, Object> dsAttachContractEnterInsurerSMSCode(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        String smsCodeEnteredByUser = params.get("SMSCODE").toString();
        Object contractID = params.get("CONTRID");

        // получение СМС-кода из контракта в БД
        Map<String, Object> contractParams = new HashMap<String, Object>();
        contractParams.put("CONTRID", contractID);
        contractParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> contract = this.callService(B2BPOSWS, "dsB2BContractBrowseListByParam", contractParams, login, password);
        Object smsCodeFromDB = contract.get("SMSCODE");

        // проверка СМС-кода
        String errorStr = null;
        Long errorCode = 0L;

        //boolean isSmsValid = false;
        if (smsCodeFromDB != null) {
            boolean isSmsNotValid = !smsCodeEnteredByUser.equalsIgnoreCase(smsCodeFromDB.toString());
            if (isSmsNotValid) {
                //errorStr = "Введенный пользователем СМС-код не совпадает с указанным в БД.";
                errorStr = "Указан неверный SMS пароль.";
                errorCode = 1L;
            }
        } else {
            errorStr = "Не удалось получить верное значение СМС-кода из БД.";
            errorCode = 2L;
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("ERRORCODE", errorCode);
        if (errorCode.intValue() != 0) {
            result.put("validsmscode", 0L); // validsmscode должен содержать 1 если коды совпали и 0 во всех остальных случаях
            result.put("ERROR", errorStr);
            logger.debug("Проверка СМС-кода завершена с ошибкой: " + errorCode + " - " + errorStr);
        } else {
            result.put("validsmscode", 1L); // validsmscode должен содержать 1 если коды совпали и 0 во всех остальных случаях
            logger.debug("Проверка СМС-кода успешно завершена.");
            Map<String, Object> attachParams = new HashMap<String, Object>();
            attachParams.put("CONTRID", contractID);
            attachParams.put("CURRENT_USERID", params.get("CURRENT_USERID"));
            Map<String, Object> attachResult = this.callService(Constants.PAWS, "dsPAContractValidAndAttachContract", attachParams, login, password);
            logger.debug("attachResult from dsPAContractValidAndAttachContract:" + attachResult);
        }

        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsPAAttachableContractsProcess(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        if (attachableContractsProcessingThreadCount == 0) {
            attachableContractsProcessingThreadCount = 1;
            try {
                logger.debug("doAttachableContractsProcess start");
                result = doAttachableContractsProcess(params);
            } finally {
                attachableContractsProcessingThreadCount = 0;
                logger.debug("doAttachableContractsProcess finish\n");
            }
        } else {
            logger.debug("doAttachableContractsProcess already running");
        }
        return result;
    }

    private Map<String, Object> doAttachableContractsProcess(Map<String, Object> params) throws Exception {
        logger.debug("");
        logger.debug("Start attachable contracts processing...");

        String login = removeLogin(params);
        String password = removePassword(params);
        Map<String, Object> result = new HashMap<String, Object>();

        Map<String, Object> attachableContractsParams = new HashMap<String, Object>();
        //attachableContractsParams.putAll(params);
        //attachableContractsParams.remove(RETURN_AS_HASH_MAP);
        attachableContractsParams.put("BANKCASHFLOWSTATESYSNAME", B2B_BANKCASHFLOW_INQUEUE);
        attachableContractsParams.put("PAUSERIDISNULL", true);

        List<Map<String, Object>> attachableContracts = this.callServiceAndGetListFromResultMap(THIS_SERVICE_NAME, "dsPAAttachableContractBrowseListByParamExForProcessing", attachableContractsParams, login, password);

        int totalCount = attachableContracts.size();
        int current = 1;
        logger.debug(String.format("Found %d attachable contracts for processing.", totalCount));

        for (Map<String, Object> attachableContract : attachableContracts) {

            logger.debug("");
            logger.debug(String.format("Preparing for processing %d attachable contract (from total of %d found attachable contracts)...", current, totalCount));

            Map<String, Object> processParams = new HashMap<String, Object>();
            processParams.putAll(attachableContract);
            processParams.put(RETURN_AS_HASH_MAP, true);
            try {
                Map<String, Object> processResult = this.callExternalService(THIS_SERVICE_NAME, "dsPAAttachableContractsProcessSingleRecord", processParams, login, password);

                if ((processResult != null) && (processResult.get("EXCEPTION") == null) && (processResult.get("EXCEPTIONTEXT") == null)) {

                    // договор создан, выполнено связывание с ЛК - отправка информационного СМС-сообщения о прикреплении договора в ЛК
                    try {
                        Map<String, Object> sendResult = trySendInfoSMSAboutContractAttachment(processResult);
                        processResult.put("SMSSENDRES", sendResult); // дополнение результата обработки сведениями об отправке СМС
                    } catch (Exception ex) {
                        logger.warn("Catched exception on sending informational SMS about contract attachment (this exception will be ignored): " + ex.getLocalizedMessage());
                        processResult.put("SMSSENDRES", ex.getLocalizedMessage()); // дополнение результата обработки сведениями об исключении при отправке СМС
                    }

                    // договор создан, выполнено связывание с ЛК
                    // здесь необходимо сформировать печатный документ и отправить его в банк
                    logger.debug("Printing and sending contract's documents...");
                    try {
                        Map<String, Object> printParams = new HashMap<String, Object>();
                        printParams.put("CONTRID", attachableContract.get("CONTRID"));
                        printParams.put("CONTRNUMBER", attachableContract.get("CONTRNUMBER"));
                        printParams.put("PRODCONFID", attachableContract.get("PRODCONFID"));
                        printParams.put("ONLYPRINT", "TRUE");
                        printParams.put("SENDCOPY", "TRUE");
                        printParams.put(RETURN_AS_HASH_MAP, true);
                        Map<String, Object> printRes = this.callService(SIGNB2BPOSWS_SERVICE_NAME, "dsB2BPrintAndSendAllDocument", printParams, login, password);
                        processResult.put("PRINTRES", printRes); // дополнение результата обработки сведениями об отправке документов
                        logger.debug("Printing and sending contract's documents finished with result: " + printRes);
                    } catch (Exception ex) {
                        logger.warn("Catched exception on printing and sending contract's documents (this exception will be ignored): " + ex.getLocalizedMessage());
                        processResult.put("PRINTRES", ex.getLocalizedMessage()); // дополнение результата обработки сведениями об исключении при отправке документов
                    }
                }

                attachableContract.putAll(processResult);

            } catch (Exception ex) {
                String exLocalizedMessage = ex.getLocalizedMessage();
                logger.warn("Catched exception: " + exLocalizedMessage);
                attachableContract.put("EXCEPTION", exLocalizedMessage);

                // получение текста ошибки для сохранения в БД
                String errorMessage = getRussianMessageFromException(ex);
                logger.debug("Catched exception info for saving in DB: " + errorMessage);
                attachableContract.put("EXCEPTIONTEXT", errorMessage);

                // перевод статуса в обработанный с ошибкой
                Map<String, Object> transResult = tryTransBankCashFlowToErrorByExternalCall(params, errorMessage, login, password);
                attachableContract.putAll(transResult);
            }

            current += 1;

        }

        result.put("CONTRLIST", attachableContracts);
        logger.debug("Attachable contracts processing finished.");
        logger.debug("");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsPAAttachableContractBrowseListByParamExForProcessing(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsPAAttachableContractBrowseListByParamExForProcessing", params);
        return result;
    }

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsPAAttachableContractsProcessSingleRecord(Map<String, Object> params) throws Exception {

        logger.debug("Start bank cash flow single record processing...");

        String login = removeLogin(params);
        String password = removePassword(params);
        Map<String, Object> result = new HashMap<String, Object>();

        try {

            result = doAttachableContractsProcessSingleRecord(params, login, password);

        } catch (Exception ex) {

            String exLocalizedMessage = ex.getLocalizedMessage();
            logger.warn("Catched exception: " + exLocalizedMessage);
            result.put("EXCEPTION", exLocalizedMessage);

            // получение текста ошибки для сохранения в БД
            String errorMessage = getRussianMessageFromException(ex);
            logger.debug("Catched exception info for saving in DB: " + errorMessage);

            // откат текущей транзакции и создание взамен новой
            String transactionError = rollbackCurrentAndBeginNewTransaction();

            // если были проблемы при работе с транзакциями - дополнение описания ошибки для сохранения в БД упоминанием о транзакциях
            if (transactionError != null) {
                errorMessage = String.format("Ошибка при откате изменений: %s. Причина выполнения отката - %s.", transactionError, errorMessage);
                logger.debug("Catched exception info plus transaction error description for saving in DB: " + errorMessage);
            }
            result.put("EXCEPTIONTEXT", errorMessage);

            // перевод статуса в обработанный с ошибкой
            Map<String, Object> transResult = tryTransBankCashFlowToErrorByExternalCall(params, errorMessage, login, password);
            result.putAll(transResult);

        }

        logger.debug("Bank cash flow single record processing finished.");

        return result;
    }

    private Map<String, Object> tryTransBankCashFlowToErrorByExternalCall(Map<String, Object> params, String errorMessage, String login, String password) {
        logger.error("Trying to change bank cash flow record status and update record with error info...");
        Map<String, Object> transParams = new HashMap<String, Object>();
        transParams.putAll(params);
        transParams.put("STATESYSNAME", transParams.remove("BANKCASHFLOWSTATESYSNAME"));
        transParams.put("ERRORTEXT", errorMessage);
        transParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> transResult;
        String transError;
        try {
            // используется callExternalService для выполнения обновления записи о движении денежных средств в отдельной транзакции
            transResult = this.callExternalService(B2BPOSWS_SERVICE_NAME, "dsB2BBankCashFlowMakeTransToError", transParams, login, password);
            transError = getStringParam(transResult, "Error");
            if (!transError.isEmpty()) {
                logger.error("Error while saving processing error info to DB:\n" + transError);
            } else {
                transResult.put("BANKCASHFLOWSTATESYSNAME", transResult.remove("STATESYSNAME"));
                logger.error("Changed bank cash flow record status and update record with error info successfully with result: " + transResult);
            }
        } catch (Exception transEx) {
            transError = "Unknown exception while saving processing error info to DB";
            logger.error(transError, transEx);
            transResult = new HashMap<String, Object>();
            transResult.put("Error", transError + ": " + transEx.getLocalizedMessage());
        }
        return transResult;
    }

    // откат текущей транзакции и создание взамен новой
    // (скопирован из Mort900CustomFacade в i900)
    // todo: перенести в Base-фасад или тп    
    private String rollbackCurrentAndBeginNewTransaction() {
        String transactionError = null;
        try {
            logger.debug("Getting current thread transaction...");
            Transaction threadTransaction = Transaction.getThreadTransaction();
            if (threadTransaction != null) {
                logger.debug("Rollback current transaction...");
                threadTransaction.rollback();
                Transaction.bindThreadTransaction(null);
                logger.debug("Rollback current transaction finished.");
                logger.debug("Starting new transaction (to replace rollbacked one)...");
                Transaction.bindThreadTransaction(Transaction.internalTransaction(null));
                threadTransaction = Transaction.getThreadTransaction();
                //threadTransaction = null; // !только для отладки!
                if (threadTransaction != null) {
                    threadTransaction.setStatus(Transaction.STATUS_NO_TRANSACTION);
                    threadTransaction.begin();
                    logger.debug("Starting new transaction (to replace rollbacked one) finished.");
                } else {
                    logger.error("Starting new transaction (to replace rollbacked one) failed!");
                    transactionError = "не удалось создать новую транзакцию";
                }
            } else {
                logger.error("Getting current transaction failed!");
                transactionError = "не удалось получить текущую транзакцию";
            }
        } catch (Exception rollbackEx) {
            transactionError = rollbackEx.getLocalizedMessage();
        }
        return transactionError;
    }

    // получение текста ошибки для сохранения в БД
    private String getRussianMessageFromException(Exception ex) {
        String errorMessage;
        if (ex instanceof PAException) {
            errorMessage = ((PAException) ex).getRussianMessage();
        } else {
            Throwable cause = ex.getCause();
            if (cause != null) {
                if (cause instanceof PAException) {
                    errorMessage = ((PAException) cause).getRussianMessage();
                } else {
                    errorMessage = cause.getLocalizedMessage();
                }
            } else {
                errorMessage = ex.getLocalizedMessage();
            }
        }
        return errorMessage;
    }

    // прикрепление договора в ЛК (с автоматической регистрацией пользователя при необходимости)
    private Map<String, Object> doAttachableContractsProcessSingleRecord(Map<String, Object> params, String login, String password) throws Exception {

        logger.debug("Attaching created contract to PA user account...");

        // определение идентификатора и номера прикрепляемого договора
        Long contractID = getLongParam(params, "CONTRID");
        logger.debug("Created contract id (CONTRID) = " + contractID);
        String contractNumber = getStringParam(params, "CONTRNUMBER");
        logger.debug("Created contract number (CONTRNUMBER) = " + contractNumber);
        if (contractID == null) {
            // идентификатор прикрепляемого договора не найден
            throw new PAException(
                    "Не удалось определить ИД созданного договора (вероятно, в ходе созания договора возникла неизвестная ошибка)",
                    "Can't found contract id (probably, this caused by unknown error during contract creation)"
            );
        }

        // определение сведений о страхователе из сохраненного черновика договора
        //String phoneNumber = "";
        //String eMail = "";
        //String name = "";
        //String surname = "";
        //Map<String, Object> insurer = (Map<String, Object>) contract.get("INSURERMAP");
        //if (insurer != null) {
        //    // определение номера мобильного телефона и адреса почты из сохраненного черновика договора
        //    List<Map<String, Object>> insurerContacts = (List<Map<String, Object>>) insurer.get("contactList");
        //    if (insurerContacts != null) {
        //        for (Map<String, Object> insurerContact : insurerContacts) {
        //            String contactSysName = getStringParam(insurerContact, "CONTACTTYPESYSNAME");
        //            if ("MobilePhone".equals(contactSysName)) {
        //                phoneNumber = getStringParam(insurerContact, "VALUE");
        //                logger.debug("Insurer's mobile phone number (required for contract attachment to PA user account) from created contract: " + phoneNumber);
        //            } else if ("PersonalEmail".equals(contactSysName)) {
        //                eMail = getStringParam(insurerContact, "VALUE");
        //                logger.debug("Insurer's e-mail (required for PA user creation) from created contract: " + eMail);
        //            }
        //        }
        //    }
        //    // определение имени и фамилии из сохраненного черновика договора
        //    name = getStringParam(insurer, "FIRSTNAME");
        //    logger.debug("Insurer's first name (required for PA user creation) from created contract: " + name);
        //    surname = getStringParam(insurer, "LASTNAME");
        //    logger.debug("Insurer's last name (required for PA user creation) from created contract: " + surname);
        //}
        // определение сведений о страхователе из переданных параметров
        String phoneNumber = getStringParam(params, "PHONENUMBER");
        String eMail = getStringParam(params, "EMAIL");
        if ((null != eMail) && (eMail.equalsIgnoreCase("N"))) {
            eMail = "";
        }

        String name = getStringParam(params, "NAME");
        String surname = getStringParam(params, "SURNAME");
        if (logger.isDebugEnabled()) {
            logger.debug("Insurer's mobile phone number (required for contract attachment to PA user account) from created contract: " + phoneNumber);
            // при создании и/или поиске пользователя ЛК нельзя использовать почту из назначения платежа - согласно клиенту она может быть некорректной
            logger.debug("Insurer's e-mail (may be invalid, will not be used in PA user search or creation) from created contract: " + eMail);
            logger.debug("Insurer's first name (required for PA user creation) from created contract: " + name);
            logger.debug("Insurer's last name (required for PA user creation) from created contract: " + surname);
        }

        if (phoneNumber.isEmpty()) {
            // номера мобильного телефона в сохраненном договоре не найден
            throw new PAException(
                    "В созданном договоре не удалось найти номер мобильного телефона страхователя (требующийся для прикрепления договора в Личном кабинете)",
                    "Insurer's mobile phone number (required for contract attachment to PA user account) not found in created contract"
            );
        }

        // поиск или создание зарегистрированного пользователя
        logger.debug("Searching (or creating if necessary) PA user...");
        Map<String, Object> userParams = new HashMap<String, Object>();
        userParams.put("PHONENUMBER", phoneNumber);
        userParams.put("SURNAME", surname);
        userParams.put("NAME", name);
        // при создании и/или поиске пользователя ЛК нельзя использовать почту из назначения платежа - согласно клиенту она может быть некорректной
        //userParams.put("EMAIL", eMail);
        logger.debug("PA user info: ");
        if (logger.isDebugEnabled()) {
            for (Map.Entry<String, Object> userParam : userParams.entrySet()) {
                String key = userParam.getKey();
                Object value = userParam.getValue();
                logger.debug("   " + key + " = " + value);
            }
        }
        userParams.put(RETURN_AS_HASH_MAP, true);
        // для поддержки отката транзакций вызов метода должен выполнятся внутри сервиса
        Map<String, Object> paUser = this.callService(/*PAWS_SERVICE_NAME*/THIS_SERVICE_NAME, "dsPAFindOrCreateRegisteredUser", userParams, login, password);

        if (paUser == null) {
            // не удалось найти/создать пользователя
            throw new PAException(
                    // при создании и/или поиске пользователя ЛК нельзя использовать почту из назначения платежа - согласно клиенту она может быть некорректной
                    //String.format("Не удалось найти (или создать) пользователя Личного кабинета по полученным из созданного черновика договора сведениям (телефон: %s, фамилия: %s, имя: %s, почта: %s)",
                    //              phoneNumber, surname, name, eMail),
                    //String.format("Can't find or create PA user by info from created contract draft (phone: %s, surname: %s, name: %s, e-mail: %s)",
                    //              phoneNumber, surname, name, eMail)
                    String.format("Не удалось найти (или создать) пользователя Личного кабинета по полученным из созданного черновика договора сведениям (телефон: %s, фамилия: %s, имя: %s)",
                            phoneNumber, surname, name),
                    String.format("Can't find or create PA user by info from created contract draft (phone: %s, surname: %s, name: %s)",
                            phoneNumber, surname, name)
            );
        } else if (paUser.get("CREATIONERROR") != null) {
            // не удалось найти пользователя, а для создания нового не хватило обязательных атрибутов
            throw new PAException(
                    // при создании и/или поиске пользователя ЛК нельзя использовать почту из назначения платежа - согласно клиенту она может быть некорректной
                    //String.format("Невозможно создать нового пользователя Личного кабинета (телефон: %s, фамилия: %s, имя: %s, почта: %s) для прикрепления созданного договора - предоставлены не все данные пользователя",
                    //              phoneNumber, surname, name, eMail),
                    //String.format("Can't create new PA user (phone: %s, surname: %s, name: %s, e-mail: %s) for created contract attachment - not all requiried user info supplied",
                    //              phoneNumber, surname, name, eMail)
                    String.format("Невозможно создать нового пользователя Личного кабинета (телефон: %s, фамилия: %s, имя: %s) для прикрепления созданного договора - предоставлены не все данные пользователя",
                            phoneNumber, surname, name),
                    String.format("Can't create new PA user (phone: %s, surname: %s, name: %s) for created contract attachment - not all requiried user info supplied",
                            phoneNumber, surname, name)
            );
        } else if (paUser.get("USERID") == null) {
            // не удалось найти/создать пользователя
            throw new PAException(
                    // при создании и/или поиске пользователя ЛК нельзя использовать почту из назначения платежа - согласно клиенту она может быть некорректной
                    //String.format("Не удалось найти (или создать) пользователя Личного кабинета по полученным из созданного черновика договора сведениям (телефон: %s, фамилия: %s, имя: %s, почта: %s)",
                    //              phoneNumber, surname, name, eMail),
                    //String.format("Can't find or create PA user by info from created contract draft (phone: %s, surname: %s, name: %s, e-mail: %s)",
                    //              phoneNumber, surname, name, eMail)
                    String.format("Не удалось найти (или создать) пользователя Личного кабинета по полученным из созданного черновика договора сведениям (телефон: %s, фамилия: %s, имя: %s)",
                            phoneNumber, surname, name),
                    String.format("Can't find or create PA user by info from created contract draft (phone: %s, surname: %s, name: %s)",
                            phoneNumber, surname, name)
            );
        }

        Long paUserID = getLongParam(paUser, "USERID");
        if (logger.isDebugEnabled()) {
            boolean isCreatedNow = getBooleanParam(paUser, "ISCREATEDNOW", false);
            logger.debug(String.format("%s PA user with id (USERID) = %d.", (isCreatedNow ? "Created new" : "Found existing"), paUserID));
        }

        // проверка прикрепляемого договора - не прикреплен ли уже (маловероятно, но проверить)
        logger.debug("Checking if attached contract is already attached...");
        Map<String, Object> paContractParams = new HashMap<String, Object>();
        paContractParams.put("CONTRID", contractID);
        List<Map<String, Object>> attachedContracts = WsUtils.getListFromResultMap(this.callService(PAWS_SERVICE_NAME, "dsPaContractBrowseListByParam", paContractParams, login, password));
        logger.debug("Already attached contracts links list: " + attachedContracts);
        if ((attachedContracts != null) && (!attachedContracts.isEmpty())) {
            // прикрепляемый договора уже прикреплен
            throw new PAException(
                    String.format("Договор c номером '%s' и ИД '%d' уже включен в состав текущего или другого аккаунта",
                            contractNumber, contractID),
                    String.format("Contract with number '%s' and id '%d' already attached to this or another account",
                            contractNumber, contractID)
            );
        }
        logger.debug("Created contract is not already attached.");

        // прикрепление договора
        logger.debug("Attaching contract...");
        Map<String, Object> attachParams = new HashMap<String, Object>();
        attachParams.put("CONTRID", contractID);
        attachParams.put("CURRENT_USERID", paUserID);
        // отправлять информационное СМС-сообщение одновременно с прикреплением договора нельзя - всё еще возможен откат транзакции
        //String infoSMSText = CONTRACT_ATTACHMENT_SMS_TEMPLATE; // todo: формирование текста СМС (возможно, включение в него сведений о созданном договоре?)
        //attachParams.put("SMSTEXT", infoSMSText);
        //attachParams.put("SMSPHONENUMBER", phoneNumber);
        attachParams.put(RETURN_AS_HASH_MAP, true);
        // для поддержки отката транзакций вызов метода должен выполнятся внутри сервиса
        Map<String, Object> attachResult = this.callService(/*PAWS_SERVICE_NAME*/THIS_SERVICE_NAME, "dsPAContractValidAndAttachContract", attachParams, login, password);
        logger.debug("Contract attached with result: " + attachResult);
        if ((attachResult == null) || (attachResult.get("PAOBJECTID") == null)) {
            // не удалось прикрепить договор
            throw new PAException(
                    // при создании пользователя ЛК нельзя использовать почту из назначения платежа - согласно клиенту она может быть некорректной
                    //String.format("Не удалось прикрепить договор c номером '%s' и ИД '%d' к выбранной учетной записи пользователя ЛК (телефон: %s, фамилия: %s, имя: %s, почта: %s, ИД учетной записи: %d)",
                    //              contractNumber, contractID, phoneNumber, surname, name, eMail, paUserID),
                    //String.format("Can't attach contract with number '%s' and id '%d' to selected PA user account (phone: %s, surname: %s, name: %s, e-mail: %s, account id: %d)",
                    //              contractNumber, contractID, phoneNumber, surname, name, eMail, paUserID)
                    String.format("Не удалось прикрепить договор c номером '%s' и ИД '%d' к выбранной учетной записи пользователя ЛК (телефон: %s, фамилия: %s, имя: %s, ИД учетной записи: %d)",
                            contractNumber, contractID, phoneNumber, surname, name, paUserID),
                    String.format("Can't attach contract with number '%s' and id '%d' to selected PA user account (phone: %s, surname: %s, name: %s, account id: %d)",
                            contractNumber, contractID, phoneNumber, surname, name, paUserID)
            );
        }
        logger.debug("Attaching created contract to PA user account finished.");

        //<editor-fold defaultstate="collapsed" desc="!только для отладки!">
        // !только для отладки!
        //logger.debug("CONTRID = " + contractID);
        //logger.debug("PAUSERID = " + paUserID);
        //logger.debug("PAOBJECTID = " + attachResult.get("PAOBJECTID"));
        //if (surname.equals("666")) {
        //    throw new PAException("Тестовое исключение", "Test exception");
        //}
        //</editor-fold>
        // подготовка информационного СМС-сообщения о прикреплении договора в ЛК
        //String infoSMSText = CONTRACT_ATTACHMENT_SMS_TEMPLATE; // todo: формирование текста СМС (возможно, включение в него сведений о созданном договоре?)
        String infoSMSText;
        
        if (params.get("PRODUCTMAP") == null) {
            Map<String, Object> prodParam = new HashMap<String, Object>();
            prodParam.put("PRODCONFID", params.get("PRODCONFID"));
            prodParam.put("HIERARCHY", true);
            prodParam.put(RETURN_AS_HASH_MAP, "TRUE");
            Map<String, Object> prodMap = this.callService(Constants.B2BPOSWS, "dsProductBrowseByParams", prodParam, login, password);
            if (prodMap.get(RESULT) != null) {
                prodMap = (Map<String, Object>) prodMap.get(RESULT);
            }
            params.put("PRODUCTMAP", prodMap);
        }
        Map<String, Object> productMap = (Map<String, Object>) params.get("PRODUCTMAP");
        List<Map<String, Object>> prodDefValList = (List<Map<String, Object>>) productMap.get("PRODDEFVALS");

        if ((prodDefValList != null) && (prodDefValList.size() > 0)) {
            Map<String, Object> prdInfo = (Map<String, Object>) getLastElementByAtrrValue(prodDefValList, "NAME", "INFOSMSTEXT");
            if (prdInfo != null) {
                infoSMSText = getStringParam(prdInfo.get("VALUE"));
            } else {
                infoSMSText = CONTRACT_ATTACHMENT_SMS_900TMTEMPLATE; // todo: формирование текста СМС (возможно, включение в него сведений о созданном договоре?)
            }
        } else {

            String prdName = getStringParam(params, "PRODSYSNAME");
            if ((null != prdName) && (prdName.equalsIgnoreCase(SYSNAME_HOUSE900))) {
                infoSMSText = CONTRACT_ATTACHMENT_SMS_HOUSE900TMTEMPLATE;
            } else {
                infoSMSText = CONTRACT_ATTACHMENT_SMS_900TMTEMPLATE; // todo: формирование текста СМС (возможно, включение в него сведений о созданном договоре?)
            }
        }
        // смена статуса записи о движении денежных средств на «Обработан»
        logger.debug("Starting state transition to 'B2B_BANKCASHFLOW_PROCESSED' for current bank cash flow record...");
        Map<String, Object> transParams = new HashMap<String, Object>();
        transParams.put("BANKCASHFLOWID", params.get("BANKCASHFLOWID"));
        transParams.put("STATESYSNAME", params.get("BANKCASHFLOWSTATESYSNAME"));
        transParams.put(RETURN_AS_HASH_MAP, true);
        // данный вызов не поддерживает отката транзакций (т.к. вызов метода в b2bposws), но является завершающим обработку, поэтому будет выполнятся только если все предыдущие операции успешно завершены
        Map<String, Object> transResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBankCashFlowMakeTransToProcessed", transParams, login, password);
        if (transResult == null) {
            // не удалось изменить статус записи о движении денежных средств на «Обработан» (по неизвестной причине)
            throw new PAException(
                    "Не удалось изменить статус текущей записи о движении денежных средств на 'Обработан'",
                    "State transition to 'B2B_BANKCASHFLOW_PROCESSED' for current bank cash flow record failed"
            );
        } else if ((transResult.get("STATEID") == null) || (transResult.get("STATESYSNAME") == null) || (transResult.get("Error") != null)) {
            // не удалось изменить статус записи о движении денежных средств на «Обработан» (и, возможно, доступен текст ошибки)                
            String transError = getStringParam(transResult, "Error");
            if (!transError.isEmpty()) {
                transError = ": " + transError;
            }
            throw new PAException(
                    "Ошибка при смене статуса текущей записи о движении денежных средств на 'Обработан'" + transError,
                    "Error on state transition to 'B2B_BANKCASHFLOW_PROCESSED' for current bank cash flow record" + transError
            );
        } else {
            logger.debug("State transition to 'B2B_BANKCASHFLOW_PROCESSED' for current bank cash flow record finished with result: " + transResult);
        }

        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("PAUSER", paUser);
        result.put("PACONTRACT", attachResult);
        // возврат данных для выполнения отправки СМС после завершения транзакции
        result.put("SMSTEXT", infoSMSText);
        result.put("SMSPHONENUMBER", phoneNumber);
        // возврат сведений о смене статуса записи о движении денежных средств
        result.put("BANKCASHFLOWSTATEID", transResult.get("STATEID"));
        result.put("BANKCASHFLOWSTATESYSNAME", transResult.get("STATESYSNAME"));

        return result;

    }

}
