/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.system;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;

/**
 *
 * @author averichevsm
 */
public class SmsSender {

    protected static final String THIS_SERVICE_NAME = Constants.B2BPOSWS;

    private final Logger logger = Logger.getLogger(this.getClass());

    private HashMap<String, String> smsStatusNames = new HashMap<String, String>();

    private String smsUser = "";
    private String smsPwd = "";
    private String smsFrom = "";

    public SmsSender() {
        init();
    }

    private void init() {
        Config config = Config.getConfig(THIS_SERVICE_NAME);
        //this.smsTextPrefix = config.getParam("SMSTEXT", "Уважаемый клиент! Ваш пароль для подтверждения введенных данных: ");
        this.smsUser = config.getParam("SMSUSER", "sberinsur");
        this.smsPwd = config.getParam("SMSPWD", "KD9zVoeR123");
        this.smsFrom = config.getParam("SMSFROM", "SberbankIns");
    }

    public Map<String, Object> sendSms(String phone, String message) throws IOException {
        Map<String, Object> sendResult = sendSms(this.smsUser, this.smsPwd, this.smsFrom, phone, message);
        return sendResult;
    }

    public Map<String, Object> sendSmsCode(String phone, String smsCode) throws IOException {
        String message = "Уважаемый клиент! Ваш пароль для подтверждения введенных данных: " + smsCode;
        Map<String, Object> sendResult = sendSms(this.smsUser, this.smsPwd, this.smsFrom, phone, message);
        return sendResult;
    }

    private Map<String, Object> sendSms(String login, String password, String seller, String phone, String message) throws IOException {
        Map<String, Object> result = new HashMap<String, Object>();
        HttpClient httpClient = new HttpClient();
        StringBuilder sb = new StringBuilder();
        // sb.append("http://gateway.api.sc/get/?");
        sb.append("user=");
        sb.append(login);
        sb.append("&pwd=");
        sb.append(password);
        sb.append("&sadr=");
        sb.append(seller);
        sb.append("&dadr=");
        sb.append(phone);
        sb.append("&text=");
        sb.append(URLEncoder.encode(message, "UTF-8"));

        /*
        logger.debug(sb.toString());
        GetMethod httpGet = new GetMethod("http://gateway.api.sc/get/?" + sb.toString());
        */
        String sbStr = sb.toString();
        logger.debug(sbStr);
        GetMethod httpGet = new GetMethod("http://gateway.api.sc/get/?" + sbStr);

        try {
            if ((password == null) || (password.isEmpty())) {
                logger.error("пароль или логин смс пусты");
            }
            httpClient.executeMethod(httpGet);
            /*
            result.put("SMSID", httpGet.getResponseBodyAsString());
            logger.debug("SMSID = " + httpGet.getResponseBodyAsString());
            */
            String smsIdStr = httpGet.getResponseBodyAsString();
            result.put("SMSID", smsIdStr);
            if (logger.isDebugEnabled()) {
                logger.debug("SMSID = " + smsIdStr);
            }
            try {
                Long smsId = Long.parseLong(smsIdStr);
                result.put("SMSID", smsId);
            } catch (NumberFormatException ex) {
                // если smsIdStr не является числовым идентификатором успешно отправленной СМС, то он будет содержать текст ошибки
                result.put("Error", smsIdStr);
                logger.error("Unable to get sms id long value from response (probably sms send failed)! Details (result): " + result, ex);
            }
            result.put("httpStatusLine", httpGet.getStatusLine().toString());
        } finally {
            httpGet.releaseConnection();
        }

        return result;
    }

    public Map<String, Object> getSmsStatus(String SmsID) throws IOException {
        Map<String, Object> smsStatus = getSmsStatus(this.smsUser, this.smsPwd, this.smsFrom, SmsID);
        return smsStatus;
    }

    private Map<String, Object> getSmsStatus(String login, String password, String seller, String SmsID) throws IOException {

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("SmsID", SmsID);
        smsStatusNames.put("send", "Отправлено, но не доставлено");
        smsStatusNames.put("deliver", "Доставлено");
        HttpClient httpClient = new HttpClient();
        StringBuilder sb = new StringBuilder();
        // sb.append("http://gateway.api.sc/get/?");
        sb.append("user=");
        sb.append(login);
        sb.append("&pwd=");
        sb.append(password);
        sb.append("&sadr=");
        sb.append(seller);
        sb.append("&smsid=");
        sb.append(SmsID);

        logger.debug("Запрошен статус SMS с идентификатором '" + SmsID + "'...");

        GetMethod httpGet = new GetMethod("http://gateway.api.sc/get/?" + sb.toString());
        try {
            if ((password == null) || (password.isEmpty())) {
                logger.error("пароль или логин смс пусты");
            }
            httpClient.executeMethod(httpGet);
            if (httpGet.getStatusCode() == 200) {
                String body = httpGet.getResponseBodyAsString();
                result.put("SMSRawStatus", body);
                result.put("SMSStatusName", smsStatusNames.get(body));
            } else {
                result.put("HTTPReason", httpGet.getStatusLine().getReasonPhrase());
                logger.debug("Полный запрос: " + sb.toString());
                logger.debug("Полный ответ: " + httpGet);
            }

        } finally {
            httpGet.releaseConnection();
        }
        return result;
    }

}
