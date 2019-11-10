/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.paws.facade.pa.custom;

import com.bivgroup.services.paws.facade.PABaseFacade;
import com.bivgroup.services.paws.system.Constants;
import com.bivgroup.services.paws.system.SmsSender;
import com.ibm.icu.util.Calendar;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.StringCryptUtils;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author ilich
 */
@BOName("PASessionCustom")
public class PASessionCustomFacade extends PABaseFacade {

    private static final Long PASTATUS_VERIFYEMAIL_ID = 1L;
    private static final String PASTATUS_VERIFYEMAIL_SYSNAME = "VERIFYEMAIL";
    private static final Long PASTATUS_VERIFYPHONE_ID = 2L;
    private static final String PASTATUS_VERIFYPHONE_SYSNAME = "VERIFYPHONE";
    private static final Long PASTATUS_REGISTERED_ID = 3L;
    private static final String PASTATUS_REGISTERED_SYSNAME = "REGISTERED";
    private static Map<String, Long> paStatusIDBySysName; // формируется в init() - дополнять при добавлении статусов

    final private String EncryptionPassword = "2E0XNZA9YYXJ9M6LF5XLP7GD4WXB9F7FXD8IISTAXBL2T5FQZ2X";
    private static final String SERVICE_NAME = "paws";
    final private byte[] Salt = {
        (byte) 0xa3, (byte) 0x23, (byte) 0x34, (byte) 0x2c,
        (byte) 0xf1, (byte) 0xd5, (byte) 0x31, (byte) 0x19};
    final private String divider = "__div__";
    private String lkURL = "";
    private Long sessionTimeOut = 10L;
    private Long smsCodeValidTime = 10L;
    private String smsText = "";
    private String smsUser = "";
    private String smsPwd = "";
    private String smsFrom = "";
    private Long isDebug = 0L;

    public PASessionCustomFacade() {
        super();
        init();
    }

    private void init() {
        Config config = Config.getConfig(SERVICE_NAME);
        lkURL = config.getParam("LKURL", "http://localhost:8080/bivsberlossws/html/bivlksber/app/index.html");
        sessionTimeOut = Long.valueOf(config.getParam("maxSessionSize", "10"));
        smsCodeValidTime = Long.valueOf(config.getParam("smsCodeValidTime", "10"));
        this.smsText = config.getParam("SMSTEXT", "Уважаемый клиент! Ваш пароль для подтверждения введенных данных: ");
        this.smsUser = config.getParam("SMSUSER", "sberinsur");
        this.smsPwd = config.getParam("SMSPWD", "KD9zVoeR123");
        this.smsFrom = config.getParam("SMSFROM", "SberbankIns");
        try {
            this.isDebug = Long.valueOf(config.getParam("DEBUG", "0"));
        } catch (Exception e) {
            this.isDebug = 0L;
        }

        paStatusIDBySysName = new HashMap<String, Long>();
        paStatusIDBySysName.put(PASTATUS_VERIFYEMAIL_SYSNAME, PASTATUS_VERIFYEMAIL_ID);
        paStatusIDBySysName.put(PASTATUS_VERIFYPHONE_SYSNAME, PASTATUS_VERIFYPHONE_ID);
        paStatusIDBySysName.put(PASTATUS_REGISTERED_SYSNAME, PASTATUS_REGISTERED_ID);

    }

    private static byte[] stringToBytes(String value) {
        byte[] result = null;
        try {
            result = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return result;
    }

    private static String bytesToString(byte[] value) {
        String result = "";
        try {
            result = new String(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        return result;
    }

    //private static String base64DecodeUrlSafe(String input) {
    //    Base64 decoder = new Base64(true);
    //    return bytesToString(decoder.decode(input));
    //}
    private static String base64EncodeUrlSafe(String input) {
        Base64 encoder = new Base64(true);
        String result = bytesToString(encoder.encode(stringToBytes(input)));
        return result.substring(0, result.length() - 2);
    }

    private String makeSessionId(String login, String passSha) {
        GregorianCalendar gcToday = new GregorianCalendar();
        gcToday.setTime(new Date());
        String sessionSt = login + divider + passSha + divider + String.valueOf(gcToday.getTimeInMillis());
        StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        return scu.encrypt(sessionSt);
    }

    private String makeSessionIdRestSbol(String login, String phoneNumber) {
        GregorianCalendar gcToday = new GregorianCalendar();
        gcToday.setTime(new Date());
        String sessionSt = login + divider + phoneNumber + divider + String.valueOf(gcToday.getTimeInMillis());
        StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        return scu.encrypt(sessionSt);
    }

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

    private Map<String, Object> findPAUser(Map<String, Object> params, boolean isOnlyRegistered, String login, String password) throws Exception {
        String phoneNumber = getStringParam(params, "PHONENUMBER");
        String eMail = getStringParam(params, "EMAIL");
        String paStatusSysName = null;
        if (isOnlyRegistered) {
            paStatusSysName = PASTATUS_REGISTERED_SYSNAME;
        }

        return findPAUserByPhoneOrEMail(phoneNumber, eMail, paStatusSysName, login, password);
    }

    //private Map<String, Object> findPAUser(Map<String, Object> params, boolean isOnlyRegistered, String login, String password) throws Exception {
    //    String phoneNumber = null;
    //    if ((params.get("PHONENUMBER") != null) && (!params.get("PHONENUMBER").toString().isEmpty())) {
    //        phoneNumber = params.get("PHONENUMBER").toString();
    //    }
    //    String eMail = null;
    //    if ((params.get("EMAIL") != null) && (!params.get("EMAIL").toString().isEmpty())) {
    //        eMail = params.get("EMAIL").toString();
    //    }
    //    Map<String, Object> qParam = new HashMap<String, Object>();
    //    qParam.put("PHONENUMBER", phoneNumber);
    //    qParam.put("EMAIL", eMail);
    //    if (isOnlyRegistered) {
    //        qParam.put("STATUSSYSNAME", PASTATUS_REGISTERED_SYSNAME);
    //    }
    //    qParam.put(RETURN_AS_HASH_MAP, true);
    //    return this.callService(Constants.PAWS, "dsPaUserBrowseListByParam", qParam, login, password);
    //}
    private Map<String, Object> findPAUserByID(Long userID, String login, String password) throws Exception {
        Map<String, Object> qParam = new HashMap<String, Object>();
        qParam.put("USERID", userID);
        qParam.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> paUserMap = this.callService(Constants.PAWS, "dsPaUserBrowseListByParam", qParam, login, password);
        return paUserMap;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsPaUserBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsPaUserBrowseListByParamEx", "dsPaUserBrowseListByParamExCount", params);
        return result;
    }

    // поиск зарегистрировнного пользователя по номеру телефона
    //private Map<String, Object> findPARegisteredUserByPhone(String phoneNumber, String login, String password) throws Exception {
    //    return findPAUserByPhoneOrEMail(phoneNumber, null, PASTATUS_REGISTERED_SYSNAME, login, password);
    //}
    // поиск зарегистрировнного пользователя по номеру телефона
    //private Map<String, Object> findPARegisteredUserByPhone(String phoneNumber, String login, String password) throws Exception {
    //    
    //    logger.debug(String.format("Searching registered user by phone number [%s]...", phoneNumber));
    //    
    //    // маска телефона для поиска
    //    String phoneNumberCleared = phoneNumber.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\-", "").replaceAll("\\%", "").replaceAll("\\?", "");
    //    String phoneNumberLike;
    //    if ((phoneNumberCleared.length() == 12) && (phoneNumberCleared.startsWith("+7"))) {
    //        // +71112223344 -> %1112223344
    //        phoneNumberLike = "%" + phoneNumberCleared.substring(2);
    //    } else if ((phoneNumberCleared.length() == 11) && (phoneNumberCleared.startsWith("8"))) {
    //        // 81112223344 -> %1112223344
    //        phoneNumberLike = "%" + phoneNumberCleared.substring(1);
    //    } else if (phoneNumberCleared.length() == 10) {
    //        // 1112223344 -> %1112223344
    //        phoneNumberLike = "%" + phoneNumberCleared;
    //    } else {
    //        // ошибочный или тестовый/отладочный номер - в обоих случаях поиск по точному совпадению
    //        phoneNumberLike = phoneNumberCleared;
    //    }
    //    logger.debug(String.format("Phone number SQL-like mask [%s] will be used for search.", phoneNumberLike));
    //    
    //    // параметры поиска и вызов метода
    //    Map<String, Object> paUserParams = new HashMap<String, Object>();
    //    paUserParams.put("STATUSID", PASTATUS_REGISTERED_ID);
    //    paUserParams.put("STATUSSYSNAME", PASTATUS_REGISTERED_SYSNAME);
    //    paUserParams.put("PHONENUMBERLIKE", phoneNumberLike);
    //    paUserParams.put(RETURN_AS_HASH_MAP, true);
    //    Map<String, Object> paUser = this.callService(Constants.PAWS, "dsPaUserBrowseListByParamEx", paUserParams, login, password);
    //    
    //    logger.debug("Searching registered user by phone number finished with result: " + paUser);
    //    return paUser;
    //}
    // поиск зарегистрировнного пользователя по номеру телефона ИЛИ по электронной почте, с учетом статуса (если указан)
    private Map<String, Object> findPAUserByPhoneOrEMail(String phoneNumber, String eMail, String paStatusSysName, String login, String password) throws Exception {

        logger.debug(String.format("Searching user by phone number [%s] or e-mail [%s]...", phoneNumber, eMail));
        Map<String, Object> paUserParams = new HashMap<String, Object>();

        // номер сотового телефона
        if ((phoneNumber != null) && (!phoneNumber.isEmpty())) {
            // маска телефона для поиска
            String phoneNumberCleared = phoneNumber.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\-", "").replaceAll("\\%", "").replaceAll("\\?", "");
            String phoneNumberLike;
            if ((phoneNumberCleared.length() == 12) && (phoneNumberCleared.startsWith("+7"))) {
                // +71112223344 -> %1112223344
                phoneNumberLike = "%" + phoneNumberCleared.substring(2);
            } else if ((phoneNumberCleared.length() == 11) && (phoneNumberCleared.startsWith("8"))) {
                // 81112223344 -> %1112223344
                phoneNumberLike = "%" + phoneNumberCleared.substring(1);
            } else if (phoneNumberCleared.length() == 10) {
                // 1112223344 -> %1112223344
                phoneNumberLike = "%" + phoneNumberCleared;
            } else {
                // ошибочный или тестовый/отладочный номер - в обоих случаях поиск по точному совпадению
                phoneNumberLike = phoneNumberCleared;
            }
            logger.debug(String.format("Phone number SQL-like mask [%s] will be used for search.", phoneNumberLike));
            paUserParams.put("PHONENUMBERLIKE", phoneNumberLike);
        }

        // электронная почта
        if ((eMail != null) && (!eMail.isEmpty())) {
            paUserParams.put("EMAILUPPERCASE", eMail.toUpperCase());
        }

        // статус
        if ((paStatusSysName != null) && (!paStatusSysName.isEmpty())) {
            logger.debug(String.format("Only users with status '%s' will be searched.", paStatusSysName));
            paUserParams.put("STATUSSYSNAME", paStatusSysName);
            paUserParams.put("STATUSID", paStatusIDBySysName.get(paStatusSysName));
        }

        // доп. параметры поиска и вызов метода
        paUserParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> paUser = this.callService(Constants.PAWS, "dsPaUserBrowseListByParamEx", paUserParams, login, password);

        logger.debug("Searching user by phone number or e-mail finished with result: " + paUser);
        return paUser;
    }

    private Map<String, Object> sendAndSaveSMSCode(Long userId, String phoneNumber, String smsCode, String login, String password) throws Exception, IOException {
        // генерация случайного СМС-кода, если отправляемый код не указан в явном виде
        if ((smsCode == null) || (smsCode.isEmpty())) {
            smsCode = generateSMSCode();
        }
        // отправка СМС клиенту
        GregorianCalendar gcToday = new GregorianCalendar();
        gcToday.setTime(new Date());
        SmsSender smsSender = new SmsSender();
        Map<String, Object> sendRes = smsSender.doGet(this.smsUser, this.smsPwd, this.smsFrom, phoneNumber, this.smsText + smsCode);
        // сохранить код, номер телефона и время отправки в базу
        Map<String, Object> updParams = new HashMap<String, Object>();
        updParams.put("USERID", userId);
        updParams.put("PHONENUMBER", phoneNumber);
        updParams.put("SMSCODE", smsCode);
        updParams.put("SMSCODEDATE", gcToday.getTime());
        XMLUtil.convertDateToFloat(updParams);
        this.callService(Constants.PAWS, "dsPaUserUpdate", updParams, login, password);
        return sendRes;
    }

    private Map<String, Object> sendAndSaveNewlyGeneratedSMSCode(Long userId, String phoneNumber, String login, String password) throws Exception, IOException {
        return sendAndSaveSMSCode(userId, phoneNumber, null, login, password);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsPALogin(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> paUserMap = findPAUser(params, true, login, password);
        if ((paUserMap != null) && (paUserMap.get("USERID") != null)) {
            Long userId = getLongParam(paUserMap.get("USERID"));
            String phoneNumber = paUserMap.get("PHONENUMBER").toString();
            // отправка СМС клиенту
            Map<String, Object> sendRes = sendAndSaveNewlyGeneratedSMSCode(userId, phoneNumber, login, password);
            result.put("SENDRES", sendRes);
            result.put("NAME", paUserMap.get("NAME"));
            result.put("SURNAME", paUserMap.get("SURNAME"));
            result.put("EMAIL", paUserMap.get("EMAIL"));
        } else {
            result.put("Error", "По указанным данным не определен аккаунт");
            return result;
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsPARestSbolLogin(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> paUserMap = findPAUser(params, true, login, password);
        if ((paUserMap != null) && (paUserMap.get("USERID") != null)) {
            result.putAll(paUserMap);
            result.put("SESSIONID", makeSessionIdRestSbol(paUserMap.get("USERID").toString(), paUserMap.get("PHONENUMBER").toString()));
        } else {
            result.put("Error", "По указанным данным не определен аккаунт");
            return result;
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsPALoginResendSMSCode(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> paUserMap = findPAUser(params, true, login, password);
        if ((paUserMap != null) && (paUserMap.get("USERID") != null)) {
            Long userId = getLongParam(paUserMap.get("USERID"));
            String phoneNumber = paUserMap.get("PHONENUMBER").toString();
            String smsCode = getStringParam(paUserMap.get("SMSCODE"));
            // отправка СМС клиенту
            Map<String, Object> sendRes = sendAndSaveSMSCode(userId, phoneNumber, smsCode, login, password);
            result.put("SENDRES", sendRes);
            result.put("NAME", paUserMap.get("NAME"));
            result.put("SURNAME", paUserMap.get("SURNAME"));
            result.put("EMAIL", paUserMap.get("EMAIL"));
        } else {
            result.put("Error", "По указанным данным не определен аккаунт");
            return result;
        }
        return result;
    }

    @WsMethod(requiredParams = {"SMSCODE"})
    public Map<String, Object> dsPALoginEnterSMSCode(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> paUserMap = findPAUser(params, true, login, password);
        if ((paUserMap != null) && (paUserMap.get("USERID") != null)) {
            XMLUtil.convertFloatToDate(paUserMap);
            if (paUserMap.get("SMSCODEDATE") != null) {
                Date smsCodeDate = (Date) paUserMap.get("SMSCODEDATE");
                GregorianCalendar gcValidSms = new GregorianCalendar();
                gcValidSms.setTime(smsCodeDate);
                gcValidSms.add(Calendar.MINUTE, smsCodeValidTime.intValue());
                GregorianCalendar gcNow = new GregorianCalendar();
                gcNow.setTime(new Date());
                if (gcNow.getTimeInMillis() <= gcValidSms.getTimeInMillis()) {
                    String smsCode = params.get("SMSCODE").toString();
                    if (smsCode.equals(paUserMap.get("SMSCODE").toString())) {
                        String smsCodeSHA = DigestUtils.shaHex(smsCode);
                        result.put("SESSIONID", makeSessionId(paUserMap.get("USERID").toString(), smsCodeSHA));
                    } else {
                        result.put("Error", "Указан неверный SMS пароль");
                        return result;
                    }
                } else {
                    result.put("Error", "Превышено время ожидания ввода пароля");
                    return result;
                }
            } else {
                result.put("Error", "Не определена дата отсылки СМС");
                return result;
            }
        } else {
            result.put("Error", "По указанным данным не определен аккаунт");
            return result;
        }
        return result;
    }

    private Map<String, Object> trySendVerifyEMail(String eMail, String urlCode, String RegisterInfoMsg, String login, String password) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        logger.debug("RegisterInfoMsg = " + RegisterInfoMsg);
        try {
            if (sendVerifyEMail(eMail, urlCode, login, password)) {
                result.put("RegisterInfo", RegisterInfoMsg);
            } else {
                result.put("Error", "Ошибка отправки эл. почты.");
            }
        } catch (Exception ex) {
            result.put("Error", "Ошибка отправки эл. почты: " + ex.getMessage());
        }
        return result;
    }

    private boolean sendVerifyEMail(String eMail, String urlCode, String login, String password) throws Exception {
        logger.debug("Отправка письма для подтверждения адреса электронной почты...");
        //StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        //String verifyCodedSt = scu.encrypt(userId.toString());
        //String verifyUrlEncoded = lkURL + "#/posReg?vcode=" + base64EncodeUrlSafe(verifyCodedSt);
        String verifyUrlEncoded = lkURL + "#/posReg?vcode=" + urlCode;
        logger.debug("Сформирована ссылка для подтверждения: " + verifyUrlEncoded);
        Map<String, Object> sendParams = new HashMap<String, Object>();
        sendParams.put("SMTPReceipt", eMail);
        if (isAllEmailValid(eMail)) {

            sendParams.put("SMTPSubject", "Личный кабинет. Подтверждение EMail адреса");
            String text
                    = "Добрый день, уважаемый Клиент!\n\n"
                    + "Спасибо, что решили самостоятельно управлять вашими страховыми продуктами!\n"
                    + "Для продолжения создания Личного кабинета, пройдите, пожалуйста, по ссылке: \n"
                    + verifyUrlEncoded + "\n\n"
                    + "Добро пожаловать в мир Сбербанк Страхование!";
            sendParams.put("SMTPMESSAGE", text);
            String htmlText
                    = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
                    + "<html>\n"
                    + "<head>\n"
                    + "	<meta http-equiv=\"content-type\" content=\"text/html; charset=windows-1251\"/>\n"
                    + "	<title></title>\n"
                    + "	<style type=\"text/css\">\n"
                    + "		@page { margin: 0cm }\n"
                    + "		p { margin-bottom: 0.0cm; line-height: 80% }\n"
                    + "		a:link { so-language: en-US }\n"
                    + "	</style>\n"
                    + "</head>\n"
                    + "<body lang=\"ru-RU\" dir=\"ltr\" style=\"background: transparent\">"
                    + "Добрый день, уважаемый Клиент!<br/><br/>"
                    + "Спасибо, что решили самостоятельно управлять вашими страховыми продуктами!<br/>"
                    + "Для продолжения создания Личного кабинета, пройдите, пожалуйста, по ссылке: <br/>"
                    + "<a href=\"" + verifyUrlEncoded + "\" target=\"_blank\">" + verifyUrlEncoded + "</a>"
                    + "<br/><br/>Добро пожаловать в мир Сбербанк Страхование!" + "</body>\n"
                    + "</html>";
            sendParams.put("HTMLTEXT", htmlText);
            Map<String, Object> sendRes;
            try {
                boolean isError = false;
                sendRes = this.callService(Constants.WEBSMSWS, "mailmessage", sendParams, login, password);
                if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                    sendRes = this.callService(Constants.WEBSMSWS, "mailmessage", sendParams, login, password);
                    if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                        sendRes = this.callService(Constants.WEBSMSWS, "mailmessage", sendParams, login, password);
                        if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                            sendRes = this.callService(Constants.WEBSMSWS, "mailmessage", sendParams, login, password);
                            if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                                sendRes = this.callService(Constants.WEBSMSWS, "mailmessage", sendParams, login, password);
                                if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                                    //отправка письма не удалась
                                    logger.debug("При отправке письма для подтверждения адреса электронной почты возникла ошибка. Подробнее: " + sendRes);
                                    isError = true;
                                }
                            }
                        }
                    }
                }
                return !isError;
            } catch (Exception ex) {
                logger.debug("При отправке письма для подтверждения адреса электронной почты возникло исключение: " + ex);
                return false;
            }
        }
        return false;
    }

    /*
    private Map<String, Object> trySendVerifyEMail(String eMail, Long userId, String RegisterInfoMsg, String login, String password) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        logger.debug("RegisterInfoMsg = " + RegisterInfoMsg);
        try {
            if (sendVerifyEMail(eMail, userId, login, password)) {
                result.put("RegisterInfo", RegisterInfoMsg);
            } else {
                result.put("Error", "Ошибка отправки эл. почты.");
            }
        } catch (Exception ex) {
            result.put("Error", "Ошибка отправки эл. почты: " + ex.getMessage());
        }
        return result;
    }
     */
 /*
    private boolean sendVerifyEMail(String eMail, Long userId, String login, String password) throws Exception {
        logger.debug("Отправка письма для подтверждения адреса электронной почты...");
        StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        String verifyCodedSt = scu.encrypt(userId.toString());
        String verifyUrlEncoded = lkURL + "#/posReg?vcode=" + base64EncodeUrlSafe(verifyCodedSt);
        logger.debug("Сформирована ссылка для подтверждения: " + verifyUrlEncoded);
        Map<String, Object> sendParams = new HashMap<String, Object>();
        sendParams.put("SMTPReceipt", eMail);
        sendParams.put("SMTPSubject", "Личный кабинет. Подтверждение EMail адреса");
        String text 
                = "Добрый день, уважаемый Клиент!\n\n"
                + "Спасибо, что решили самостоятельно управлять вашими страховыми продуктами!\n"
                + "Для продолжения создания Личного кабинета, пройдите, пожалуйста, по ссылке: \n"
                + verifyUrlEncoded + "\n\n"
                + "Добро пожаловать в мир Сбербанк Страхование!";
        sendParams.put("SMTPMESSAGE", text);
        String htmlText
                = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n"
                + "<html>\n"
                + "<head>\n"
                + "	<meta http-equiv=\"content-type\" content=\"text/html; charset=windows-1251\"/>\n"
                + "	<title></title>\n"
                + "	<style type=\"text/css\">\n"
                + "		@page { margin: 0cm }\n"
                + "		p { margin-bottom: 0.0cm; line-height: 80% }\n"
                + "		a:link { so-language: en-US }\n"
                + "	</style>\n"
                + "</head>\n"
                + "<body lang=\"ru-RU\" dir=\"ltr\" style=\"background: transparent\">"
                + "Добрый день, уважаемый Клиент!<br/><br/>"
                + "Спасибо, что решили самостоятельно управлять вашими страховыми продуктами!<br/>"
                + "Для продолжения создания Личного кабинета, пройдите, пожалуйста, по ссылке: <br/>"
                + "<a href=\"" + verifyUrlEncoded + "\" target=\"_blank\">" + verifyUrlEncoded + "</a>"
                + "<br/><br/>Добро пожаловать в мир Сбербанк Страхование!" +"</body>\n"
                + "</html>"
                ;
        sendParams.put("HTMLTEXT", htmlText);
        Map<String, Object> sendRes;
        try {
            boolean isError = false;
            sendRes = this.callService(Constants.WEBSMSWS, "mailmessage", sendParams, login, password);
            if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                sendRes = this.callService(Constants.WEBSMSWS, "mailmessage", sendParams, login, password);
                if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                    sendRes = this.callService(Constants.WEBSMSWS, "mailmessage", sendParams, login, password);
                    if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                        sendRes = this.callService(Constants.WEBSMSWS, "mailmessage", sendParams, login, password);
                        if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                            sendRes = this.callService(Constants.WEBSMSWS, "mailmessage", sendParams, login, password);
                            if ((sendRes.get(RESULT) == null) || (!"ok".equalsIgnoreCase(sendRes.get(RESULT).toString()))) {
                                //отправка письма не удалась
                                logger.debug("При отправке письма для подтверждения адреса электронной почты возникла ошибка. Подробнее: " + sendRes);
                                isError = true;
                            }
                        }
                    }
                }
            }
            return !isError;
        } catch (Exception ex) {
            logger.debug("При отправке письма для подтверждения адреса электронной почты возникло исключение: " + ex);
            return false;
        }
    }
     */
    private Map<String, Object> checkOrCreatePAUser(Map<String, Object> paExistedUserMap, Map<String, Object> paRequestedUserMap, Long paUserStatusID, String paUserStatusSysName, boolean isFullInfoRequired, String login, String password) throws Exception {
        if ((paExistedUserMap != null) && (paExistedUserMap.get("USERID") != null)) {
            logger.debug("Found user with id (USERID) = " + paExistedUserMap.get("USERID"));
            paExistedUserMap.put("ISCREATEDNOW", false);
            return paExistedUserMap;
        } else {
            logger.debug("No user found - new user will be created...");
            if (isFullInfoRequired) {
                logger.debug("Performing requested created user info check before user creation...");
                // при создании пользователя ЛК нельзя использовать почту из назначения платежа - согласно клиенту она может быть некорректной
                boolean isNotFullInfoSupplied = (getStringParam(paRequestedUserMap, "SURNAME").isEmpty()
                        || getStringParam(paRequestedUserMap, "NAME").isEmpty()
                        //|| getStringParam(paRequestedUserMap, "EMAIL").isEmpty()
                        || getStringParam(paRequestedUserMap, "PHONENUMBER").isEmpty());
                if (isNotFullInfoSupplied) {
                    logger.debug("User info not fully supplied - creation will be skipped.");
                    paRequestedUserMap.put("CREATIONERROR", "isNotFullInfoSupplied");
                    return paRequestedUserMap;
                }
                logger.debug("Full user info supplied.");
            }
            Map<String, Object> userParams = new HashMap<String, Object>();
            userParams.put("SURNAME", paRequestedUserMap.get("SURNAME"));
            userParams.put("NAME", paRequestedUserMap.get("NAME"));
            userParams.put("EMAIL", paRequestedUserMap.get("EMAIL"));
            userParams.put("PHONENUMBER", paRequestedUserMap.get("PHONENUMBER"));
            userParams.put("STATUSDATE", new Date());
            userParams.put("STATUSID", paUserStatusID);
            userParams.put("STATUSSYSNAME", paUserStatusSysName);
            userParams.put(RETURN_AS_HASH_MAP, true);
            XMLUtil.convertDateToFloat(userParams);
            Map<String, Object> createRes = this.callService(Constants.PAWS, "dsPaUserCreate", userParams, login, password);
            logger.debug("User creation result: " + createRes);
            Long userId = Long.valueOf(createRes.get("USERID").toString());
            userParams.put("USERID", userId);
            userParams.put("ISCREATEDNOW", true);
            return userParams;
        }
    }

    @WsMethod(requiredParams = {"SURNAME", "NAME", "EMAIL"})
    public Map<String, Object> dsPAFindOrCreateUser(Map<String, Object> params) throws Exception {

        logger.debug("Searching (and creating if necessary) user...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> paUserMap = findPAUser(params, false, login, password);
        //if ((paUserMap != null) && (paUserMap.get("USERID") != null)) {
        //    paUserMap.put("ISCREATEDNOW", false);
        //    return paUserMap;
        //} else {
        //    Map<String, Object> userParams = new HashMap<String, Object>();
        //    userParams.put("SURNAME", params.get("SURNAME"));
        //    userParams.put("NAME", params.get("NAME"));
        //    userParams.put("EMAIL", params.get("EMAIL"));
        //    userParams.put("STATUSDATE", new Date());
        //    userParams.put("STATUSID", PASTATUS_VERIFYEMAIL_ID);
        //    userParams.put("STATUSSYSNAME", PASTATUS_VERIFYEMAIL_SYSNAME);
        //    userParams.put(RETURN_AS_HASH_MAP, true);
        //    XMLUtil.convertDateToFloat(userParams);
        //    Map<String, Object> createRes = this.callService(Constants.PAWS, "dsPaUserCreate", userParams, login, password);
        //    Long userId = Long.valueOf(createRes.get("USERID").toString());
        //    userParams.put("USERID", userId);
        //    userParams.put("ISCREATEDNOW", true);
        //    return userParams;
        //}

        Map<String, Object> result = checkOrCreatePAUser(paUserMap, params, PASTATUS_VERIFYEMAIL_ID, PASTATUS_VERIFYEMAIL_SYSNAME, false, login, password);

        logger.debug("Searching (and creating if necessary) registered user finished.\n");
        return result;
    }

    @WsMethod(requiredParams = {"PHONENUMBER", "SURNAME", "NAME"})
    public Map<String, Object> dsPAFindOrCreateRegisteredUser(Map<String, Object> params) throws Exception {

        logger.debug("Searching (and creating if necessary) registered user...");

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        // поиск зарегистрированного пользователя по номеру ИЛИ почте
        String phoneNumber = getStringParam(params, "PHONENUMBER");
        String eMail = getStringParam(params, "EMAIL");
        //Map<String, Object> paRegisteredUserMap = findPARegisteredUserByPhone(phoneNumber, login, password);
        Map<String, Object> paRegisteredUserMap = findPAUserByPhoneOrEMail(phoneNumber, eMail, PASTATUS_REGISTERED_SYSNAME, login, password);

        //params.put("FULLINFOREQUIRED", true);
        params.remove("CREATIONERROR");

        Map<String, Object> result = checkOrCreatePAUser(paRegisteredUserMap, params, PASTATUS_REGISTERED_ID, PASTATUS_REGISTERED_SYSNAME, true, login, password);

        logger.debug("Searching (and creating if necessary) registered user finished.\n");
        return result;

    }

    @WsMethod(requiredParams = {"EMAIL", "STATUSSYSNAME", "ENCODEDUSERID"})
    public Map<String, Object> dsPASendVerifyEMail(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        String statusSysName = getStringParam(params.get("STATUSSYSNAME"));
        String eMail = getStringParam(params.get("EMAIL"));
        String urlCode = getStringParam(params.get("ENCODEDUSERID"));

        Map<String, Object> result;
        if (urlCode.isEmpty()) {
            result = new HashMap<String, Object>();
            result.put("Error", "Ошибка отправки эл. почты.");
        } else if (statusSysName.equals(PASTATUS_REGISTERED_SYSNAME)) {
            result = new HashMap<String, Object>();
            result.put("Error", "По указанному адресу электронной почты уже зарегистрирован аккаунт.");
        } else {
            String RegisterInfoMsg;
            if (statusSysName.equals(PASTATUS_VERIFYEMAIL_SYSNAME)) {
                boolean isCreatedNow = getBooleanParam(params.get("ISCREATEDNOW"), false);
                if (isCreatedNow) {
                    RegisterInfoMsg = String.format("На адрес %s было направлено письмо для подтверждения электронной почты.", eMail);
                } else {
                    RegisterInfoMsg = String.format("На адрес %s было повторно направлено письмо для подтверждения электронной почты.", eMail);
                }
                //return trySendVerifyEMail(eMail, urlCode, RegisterInfoMsg, login, password);
            } else if (statusSysName.equals(PASTATUS_VERIFYPHONE_SYSNAME)) {
                RegisterInfoMsg = "По указанному адресу электронной почты не была окончена процедура регистрации аккаунта. Для завершения регистрации, воспользуйтесь ссылкой из отправленного вам повторно письма.";
            } else {
                RegisterInfoMsg = String.format("На адрес %s было направлено письмо для подтверждения электронной почты.", eMail);
            }
            result = trySendVerifyEMail(eMail, urlCode, RegisterInfoMsg, login, password);

        }
        return result;
    }

    /*
    @WsMethod(requiredParams = {"SURNAME", "NAME", "EMAIL"})
    public Map<String, Object> dsPARegister(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String eMail = params.get("EMAIL").toString();
        Map<String, Object> paUserMap = findPAUser(params, false, login, password);
        if ((paUserMap != null) && (paUserMap.get("USERID") != null)) {
            String statusSysName = paUserMap.get("STATUSSYSNAME").toString();
            if (statusSysName.equals(PASTATUS_VERIFYEMAIL_SYSNAME)) {
                Long userId = Long.valueOf(paUserMap.get("USERID").toString());
                try {
                    if (sendVerifyEMail(eMail, userId, login, password)) {
                        result.put("RegisterInfo", String.format("На адрес %s было повторно направлено письмо для подтверждения электронной почты.", eMail));
                    } else {
                        result.put("Error", "Ошибка отправки эл. почты.");
                        return result;
                    }
                } catch (Exception ex) {
                    result.put("Error", "Ошибка отправки эл. почты: " + ex.getMessage());
                    return result;
                }
            }
            if (statusSysName.equals(PASTATUS_VERIFYPHONE_SYSNAME)) {
                Long userId = Long.valueOf(paUserMap.get("USERID").toString());
                try {
                    if (sendVerifyEMail(eMail, userId, login, password)) {
                        result.put("RegisterInfo", "По указанному адресу электронной почты не была окончена процедура регистрации аккаунта. Для завершения регистрации, воспользуйтесь ссылкой из отправленного вам повторно письма.");
                    } else {
                        result.put("Error", "Ошибка отправки эл. почты.");
                        return result;
                    }
                } catch (Exception ex) {
                    result.put("Error", "Ошибка отправки эл. почты: " + ex.getMessage());
                    return result;
                }
            }
            if (statusSysName.equals(PASTATUS_REGISTERED_SYSNAME)) {
                result.put("Error", "По указанному адресу электронной почты уже зарегистрирован аккаунт.");
            }
        } else {
            Map<String, Object> createParams = new HashMap<String, Object>();
            createParams.put(RETURN_AS_HASH_MAP, "TRUE");
            createParams.put("SURNAME", params.get("SURNAME"));
            createParams.put("NAME", params.get("NAME"));
            createParams.put("EMAIL", eMail);
            createParams.put("STATUSDATE", new Date());
            createParams.put("STATUSID", PASTATUS_VERIFYEMAIL_ID);
            createParams.put("STATUSSYSNAME", PASTATUS_VERIFYEMAIL_SYSNAME);
            XMLUtil.convertDateToFloat(createParams);
            Map<String, Object> createRes = this.callService(Constants.PAWS, "dsPaUserCreate", createParams, login, password);
            Long userId = Long.valueOf(createRes.get("USERID").toString());
            try {
                if (sendVerifyEMail(eMail, userId, login, password)) {
                    result.put("RegisterInfo", String.format("На адрес %s было направлено письмо для подтверждения электронной почты.", eMail));
                } else {
                    result.put("Error", "Ошибка отправки эл. почты.");
                    return result;
                }
            } catch (Exception ex) {
                result.put("Error", "Ошибка отправки эл. почты: " + ex.getMessage());
                return result;
            }
        }
        return result;
    }
     */
    @WsMethod(requiredParams = {"USERID"})
    public Map<String, Object> dsPARegisterVerifyCode(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        //String verifyCode = params.get("VERIFYCODE").toString();
        //String verifyCodeDecoded = base64DecodeUrlSafe(verifyCode);
        //StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        try {
            //String verifyCodeDecrypted = scu.decrypt(verifyCodeDecoded);
            //Long userId = Long.valueOf(verifyCodeDecrypted);
            Long userId = getLongParam(params.get("USERID"));
            Map<String, Object> qRes = findPAUserByID(userId, login, password);
            if ((qRes != null) && (qRes.get("USERID") != null)) {
                String statusSysName = qRes.get("STATUSSYSNAME").toString();
                if (statusSysName.equals(PASTATUS_REGISTERED_SYSNAME)) {
                    // для данной учетной записи уже завершена регистрация - переход к авторизации
                    result.put("NextStep", "Authorize");
                } else {

                    // дополнительная проверка на наличие других учетных записей (с уже завершенной регистрацией) с таким же номером и/или почтой
                    String userPhoneNumber = getStringParam(qRes, "PHONENUMBER");
                    String userEMail = getStringParam(qRes, "EMAIL");
                    Map<String, Object> registeredUser = findPAUserByPhoneOrEMail(userPhoneNumber, userEMail, PASTATUS_REGISTERED_SYSNAME, login, password);
                    if ((registeredUser != null) && (registeredUser.get("USERID") != null)) {
                        // для данной учетной записи еще не завершена регистрация, но уже существует другая учетная запись с совпадающим номером и/или почтой
                        result.put("Error", "Уже зарегистрирован другой пользователь с таким же номером телефона или адресом электронной почты");
                        return result;
                    }

                    if (statusSysName.equals(PASTATUS_VERIFYEMAIL_SYSNAME)) {
                        // для данной учетной записи еще не завершена регистрация - обновление сведений и переход к подтверждению телефона
                        Map<String, Object> updParams = new HashMap<String, Object>();
                        updParams.put("USERID", userId);
                        updParams.put("STATUSDATE", new Date());
                        updParams.put("STATUSID", PASTATUS_VERIFYPHONE_ID);
                        updParams.put("STATUSSYSNAME", PASTATUS_VERIFYPHONE_SYSNAME);
                        XMLUtil.convertDateToFloat(updParams);
                        this.callService(Constants.PAWS, "dsPaUserUpdate", updParams, login, password);
                        result.put("NextStep", "VerifyPhone");
                    }
                    if (statusSysName.equals(PASTATUS_VERIFYPHONE_SYSNAME)) {
                        // для данной учетной записи еще не завершена регистрация - переход к подтверждению телефона
                        result.put("NextStep", "VerifyPhone");
                    }

                }
                result.put("NAME", qRes.get("NAME"));
                result.put("SURNAME", qRes.get("SURNAME"));
                result.put("EMAIL", qRes.get("EMAIL"));
            } else {
                result.put("Error", "Личный кабинет пользователя не найден");
                return result;
            }
        } catch (Exception e) {
            result.put("Error", "Неверный код подтверждения");
            return result;
        }
        return result;
    }

    @WsMethod(requiredParams = {"USERID", "PHONENUMBER", "EMAIL"})
    public Map<String, Object> dsPARegisterVerifyPhoneNumber(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        //String verifyCode = params.get("VERIFYCODE").toString();
        //String verifyCodeDecoded = base64DecodeUrlSafe(verifyCode);
        //StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        try {
            //String verifyCodeDecrypted = scu.decrypt(verifyCodeDecoded);
            //Long userId = Long.valueOf(verifyCodeDecrypted);

            // поиск зарегистрированного пользователя по номеру ИЛИ почте
            String phoneNumber = getStringParam(params, "PHONENUMBER");
            String eMail = getStringParam(params, "EMAIL");
            //Map<String, Object> qRes = findPARegisteredUserByPhone(phoneNumber, login, password);
            Map<String, Object> qRes = findPAUserByPhoneOrEMail(phoneNumber, eMail, PASTATUS_REGISTERED_SYSNAME, login, password);

            if ((qRes != null) && (qRes.get("USERID") != null)) {
                result.put("Error", "По указанному телефону или адресу электронной почты уже зарегистрирован аккаунт.");
                return result;
            } else {
                // отправка СМС клиенту
                Long userId = getLongParam(params.get("USERID"));
                Map<String, Object> sendRes = sendAndSaveNewlyGeneratedSMSCode(userId, phoneNumber, login, password);
                result.put("SENDRES", sendRes);
            }
        } catch (Exception e) {
            result.put("Error", "Неверный код подтверждения");
            return result;
        }
        return result;
    }

    @WsMethod(requiredParams = {"USERID", "PHONENUMBER", "SMSCODE"})
    public Map<String, Object> dsPARegisterEnterSMSCode(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        //String verifyCode = params.get("VERIFYCODE").toString();
        //String verifyCodeDecoded = base64DecodeUrlSafe(verifyCode);
        //StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        try {
            //String verifyCodeDecrypted = scu.decrypt(verifyCodeDecoded);
            //Long userId = Long.valueOf(verifyCodeDecrypted);
            Long userId = getLongParam(params.get("USERID"));
            Map<String, Object> paUserMap = findPAUserByID(userId, login, password);
            if ((paUserMap != null) && (paUserMap.get("USERID") != null)) {
                // дополнительная проверка на наличие других учетных записей (с уже завершенной регистрацией) с таким же номером и/или почтой
                String phoneNumber = getStringParam(paUserMap, "PHONENUMBER");
                String eMail = getStringParam(paUserMap, "EMAIL");
                Map<String, Object> qRes = findPAUserByPhoneOrEMail(phoneNumber, eMail, PASTATUS_REGISTERED_SYSNAME, login, password);
                if ((qRes != null) && (qRes.get("USERID") != null)) {
                    result.put("Error", "По указанному телефону или адресу электронной почты уже зарегистрирован аккаунт.");
                    return result;
                } else {
                    // дополнительная проверка не выявила наличия других учетных записей (с уже завершенной регистрацией) с таким же номером и/или почтой 

                    // дополнительная проверка соответствия введенного номера и номера на который отправлялась СМС
                    String userPhoneNumber = getStringParam(paUserMap.get("PHONENUMBER"));
                    String enteredPhoneNumber = getStringParam(params.get("PHONENUMBER"));
                    if ((userPhoneNumber.isEmpty()) || (!enteredPhoneNumber.equals(userPhoneNumber))) {
                        result.put("Error", "Введенный номер телефона отличается от того, на который был отправлен последний SMS пароль");
                        return result;
                    }

                    // проверка СМС кода
                    XMLUtil.convertFloatToDate(paUserMap);
                    if (paUserMap.get("SMSCODEDATE") != null) {
                        Date smsCodeDate = (Date) paUserMap.get("SMSCODEDATE");
                        GregorianCalendar gcValidSms = new GregorianCalendar();
                        gcValidSms.setTime(smsCodeDate);
                        gcValidSms.add(Calendar.MINUTE, smsCodeValidTime.intValue());
                        GregorianCalendar gcNow = new GregorianCalendar();
                        gcNow.setTime(new Date());
                        if (gcNow.getTimeInMillis() <= gcValidSms.getTimeInMillis()) {
                            String smsCode = params.get("SMSCODE").toString();
                            if (smsCode.equals(paUserMap.get("SMSCODE").toString())) {
                                Map<String, Object> updParams = new HashMap<String, Object>();
                                updParams.put("USERID", userId);
                                updParams.put("STATUSDATE", new Date());
                                updParams.put("STATUSID", PASTATUS_REGISTERED_ID);
                                updParams.put("STATUSSYSNAME", PASTATUS_REGISTERED_SYSNAME);
                                XMLUtil.convertDateToFloat(updParams);
                                this.callService(Constants.PAWS, "dsPaUserUpdate", updParams, login, password);
                                String passSha = DigestUtils.shaHex(smsCode);
                                result.put("SESSIONID", makeSessionId(paUserMap.get("USERID").toString(), passSha));
                            } else {
                                result.put("Error", "Указан неверный SMS пароль");
                                return result;
                            }
                        } else {
                            result.put("Error", "Превышено время ожидания ввода пароля");
                            return result;
                        }
                    } else {
                        result.put("Error", "Не определена дата отсылки СМС");
                        return result;
                    }
                }
            } else {
                result.put("Error", "По указанным данным не определен аккаунт");
                return result;
            }
        } catch (Exception e) {
            result.put("Error", "Неверный код подтверждения");
            return result;
        }
        return result;
    }

    @WsMethod(requiredParams = {"USERID", "PHONENUMBER", "EMAIL"})
    public Map<String, Object> dsPARegisterResendSMSCode(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        //String verifyCode = params.get("VERIFYCODE").toString();
        //String verifyCodeDecoded = base64DecodeUrlSafe(verifyCode);
        //StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        try {
            //String verifyCodeDecrypted = scu.decrypt(verifyCodeDecoded);
            //Long userId = Long.valueOf(verifyCodeDecrypted);

            // поиск зарегистрированного пользователя по номеру ИЛИ почте
            String phoneNumber = getStringParam(params, "PHONENUMBER");
            String eMail = getStringParam(params, "EMAIL");
            //Map<String, Object> qRes = findPARegisteredUserByPhone(phoneNumber, login, password);
            Map<String, Object> qRes = findPAUserByPhoneOrEMail(phoneNumber, eMail, PASTATUS_REGISTERED_SYSNAME, login, password);

            if ((qRes != null) && (qRes.get("USERID") != null)) {
                result.put("Error", "По указанному телефону или адресу электронной почты уже зарегистрирован аккаунт.");
                return result;
            } else {
                Long userId = getLongParam(params.get("USERID"));
                qRes = findPAUserByID(userId, login, password);
                String smsCode = getStringParam(qRes.get("SMSCODE"));
                Map<String, Object> sendRes = sendAndSaveSMSCode(userId, phoneNumber, smsCode, login, password);
                result.put("SENDRES", sendRes);
            }
        } catch (Exception e) {
            result.put("Error", "Неверный код подтверждения");
            return result;
        }
        return result;
    }

    @WsMethod(requiredParams = {"USERID", "SMSCODE"})
    public Map<String, Object> dsPACheckSmsCode(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Long userID = getLongParam(params.get("USERID"));
        String smsCode = getStringParam(params.get("SMSCODE"));

        result.put("ISSMSCODEVALID", checkSmsCode(userID, smsCode, login, password));

        return result;
    }

    private boolean checkSmsCode(Long userId, String smsCode, String login, String password) {
        try {
            Map<String, Object> paUserMap = findPAUserByID(userId, login, password);
            if ((paUserMap != null) && (paUserMap.get("USERID") != null)) {
                XMLUtil.convertFloatToDate(paUserMap);
                if (paUserMap.get("SMSCODEDATE") != null) {
                    Date smsCodeDate = (Date) paUserMap.get("SMSCODEDATE");
                    GregorianCalendar gcValidSms = new GregorianCalendar();
                    gcValidSms.setTime(smsCodeDate);
                    gcValidSms.add(Calendar.MINUTE, smsCodeValidTime.intValue());
                    GregorianCalendar gcNow = new GregorianCalendar();
                    gcNow.setTime(new Date());
                    if (gcNow.getTimeInMillis() <= gcValidSms.getTimeInMillis()) {
                        if (smsCode.equals(paUserMap.get("SMSCODE").toString())) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }

        return false;
    }

    @WsMethod(requiredParams = {"SESSIONID"})
    public Map<String, Object> dsPACallService(Map<String, Object> params) throws Exception {
        String srvLogin = params.get(WsConstants.LOGIN).toString();
        String srvPassword = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();
        String sessionIdCoded = params.get("SESSIONID").toString();
        boolean isSMSCheckNeeded = getBooleanParam(params.get("ISSMSCHECKNEEDED"), false);
        String serviceName = "";
        String methodName = "";
        if ((params.get("SERVICENAME") != null) && (params.get("METHODNAME") != null)) {
            serviceName = params.get("SERVICENAME").toString();
            methodName = params.get("METHODNAME").toString();
        }
        //
        StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
        if (!sessionIdCoded.isEmpty()) {
            String sessionId;
            try {
                sessionId = scu.decrypt(sessionIdCoded);
            } catch (Exception e) {
                //throw new Exception("Неверный ИД сессии");
                result.put("Error", "Неверный ИД сессии");
                return result;
            }
            String[] s = sessionId.split(divider);
            String userIDStr = s[0];
            String password = s[1];
            Long timeInMillis = Long.valueOf(s[2]);
            Long timeOut = this.sessionTimeOut; // 10 минут таймаут
            GregorianCalendar gcSessionValid = new GregorianCalendar();
            gcSessionValid.setTimeInMillis(timeInMillis);
            gcSessionValid.add(java.util.Calendar.MINUTE, timeOut.intValue());
            GregorianCalendar gcNowDate = new GregorianCalendar();
            gcNowDate.setTime(new Date());
            if (gcSessionValid.getTimeInMillis() < gcNowDate.getTimeInMillis()) {
                // new Exception("Время сессии истекло");.
                result.put("Error", "Время сессии истекло");
                return result;
            }
            Long userID = Long.valueOf(userIDStr);
            params.put("CURRENT_USERID", userID);
            // если метод вызван без указания serviceName и methodName. то вызова не будет,
            // получится просто проверка сессии.
            if (!serviceName.isEmpty() && !methodName.isEmpty()) {
                if (isSMSCheckNeeded) {
                    String smsCode = getStringParam(params.get("SMSCODE"));
                    if (!checkSmsCode(userID, smsCode, srvLogin, srvPassword)) {
                        result.put("Error", "Неверный код СМС");
                        return result;
                    }
                }
                result = this.callService(serviceName, methodName, params, srvLogin, srvPassword);
            }
            result.put("SESSIONID", makeSessionId(userIDStr, password));
        } else {
            result.put("Error", "Пользователь не представился");
        }
        return result;
    }

    //@WsMethod(requiredParams = {"SESSIONID"})
    //public Map<String, Object> dsPACallService(Map<String, Object> params) throws Exception {
    //    String srvLogin = params.get(WsConstants.LOGIN).toString();
    //    String srvPassword = params.get(WsConstants.PASSWORD).toString();
    //    Map<String, Object> result = new HashMap<String, Object>();
    //    String sessionIdCoded = params.get("SESSIONID").toString();
    //    String serviceName = "";
    //    String methodName = "";
    //    if ((params.get("SERVICENAME") != null) && (params.get("METHODNAME") != null)) {
    //        serviceName = params.get("SERVICENAME").toString();
    //        methodName = params.get("METHODNAME").toString();
    //    }
    //    //
    //    StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
    //    String sessionId = null;
    //    if (!sessionIdCoded.isEmpty()) {
    //        try {
    //            sessionId = scu.decrypt(sessionIdCoded);
    //        } catch (Exception e) {
    //            //throw new Exception("Неверный ИД сессии");
    //            result.put("Error", "Неверный ИД сессии");
    //            return result;
    //        }
    //        String[] s = sessionId.split(divider);
    //        String userId = s[0];
    //        String password = s[1];
    //        Long timeInMillis = Long.valueOf(s[2]);
    //        Long timeOut = this.sessionTimeOut; // 10 минут таймаут
    //        GregorianCalendar gcSessionValid = new GregorianCalendar();
    //        gcSessionValid.setTimeInMillis(timeInMillis);
    //        gcSessionValid.add(java.util.Calendar.MINUTE, timeOut.intValue());
    //        GregorianCalendar gcNowDate = new GregorianCalendar();
    //        gcNowDate.setTime(new Date());
    //        if (gcSessionValid.getTimeInMillis() < gcNowDate.getTimeInMillis()) {
    //            // new Exception("Время сессии истекло");.
    //            result.put("Error", "Время сессии истекло");
    //            return result;
    //        }
    //        params.put("CURRENT_USERID", Long.valueOf(userId));
    //        // если метод вызван без указания serviceName и methodName. то вызова не будет,
    //        // получится просто проверка сессии.
    //        if (!serviceName.isEmpty() && !methodName.isEmpty()) {
    //            result = this.callService(serviceName, methodName, params, srvLogin, srvPassword);
    //        }
    //        result.put("SESSIONID", makeSessionId(userId, password));
    //    } else {
    //        result.put("Error", "Пользователь не представился");
    //    }
    //    return result;
    //}
    //@WsMethod(requiredParams = {"SESSIONID"})
    //public Map<String, Object> dsPACallServiceWithCheckSmsCode(Map<String, Object> params) throws Exception {
    //    String srvLogin = params.get(WsConstants.LOGIN).toString();
    //    String srvPassword = params.get(WsConstants.PASSWORD).toString();
    //    Map<String, Object> result = new HashMap<String, Object>();
    //    String sessionIdCoded = params.get("SESSIONID").toString();
    //    String serviceName = "";
    //    String methodName = "";
    //    if ((params.get("SERVICENAME") != null) && (params.get("METHODNAME") != null)) {
    //        serviceName = params.get("SERVICENAME").toString();
    //        methodName = params.get("METHODNAME").toString();
    //    }
    //    //
    //    StringCryptUtils scu = new StringCryptUtils(EncryptionPassword, Salt);
    //    String sessionId = null;
    //    if (!sessionIdCoded.isEmpty()) {
    //        try {
    //            sessionId = scu.decrypt(sessionIdCoded);
    //        } catch (Exception e) {
    //            //throw new Exception("Неверный ИД сессии");
    //            result.put("Error", "Неверный ИД сессии");
    //            return result;
    //        }
    //        String[] s = sessionId.split(divider);
    //        String userId = s[0];
    //        String password = s[1];
    //        Long timeInMillis = Long.valueOf(s[2]);
    //        Long timeOut = this.sessionTimeOut; // 10 минут таймаут
    //        GregorianCalendar gcSessionValid = new GregorianCalendar();
    //        gcSessionValid.setTimeInMillis(timeInMillis);
    //        gcSessionValid.add(java.util.Calendar.MINUTE, timeOut.intValue());
    //        GregorianCalendar gcNowDate = new GregorianCalendar();
    //        gcNowDate.setTime(new Date());
    //        if (gcSessionValid.getTimeInMillis() < gcNowDate.getTimeInMillis()) {
    //            // new Exception("Время сессии истекло");.
    //            result.put("Error", "Время сессии истекло");
    //            return result;
    //        }
    //        params.put("CURRENT_USERID", Long.valueOf(userId));
    //        // если метод вызван без указания serviceName и methodName. то вызова не будет,
    //        // получится просто проверка сессии.
    //        if (!serviceName.isEmpty() && !methodName.isEmpty()) {
    //            String smsCode = params.get("SMSCODE").toString();
    //            if (checkSmsCode(userId, smsCode, srvLogin, srvPassword)) {
    //                result = this.callService(serviceName, methodName, params, srvLogin, srvPassword);
    //            } else {
    //                result.put("Error", "Не верный код СМС");
    //            }
    //        }
    //        result.put("SESSIONID", makeSessionId(userId, password));
    //    } else {
    //        result.put("Error", "Пользователь не представился");
    //    }
    //    return result;
    //}
    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    public boolean isAllEmailValid(String allEMail) {
        boolean result = true;
        String newEmailList = "";
        if (allEMail == null) {
            return false;
        }
        if (allEMail.isEmpty()) {
            return false;
        }
        String[] emailList = allEMail.split(",");
        if (emailList.length == 0) {
            return false;
        }
        for (String email : emailList) {
            if (isValidEmailAddress(email)) {
                if (newEmailList.isEmpty()) {
                    newEmailList = email;
                } else {
                    newEmailList = newEmailList + "," + email;
                }
            }
        }
        if (newEmailList.isEmpty()) {
            return false;
        } else {
            allEMail = newEmailList;
            return true;
        }
    }

}
