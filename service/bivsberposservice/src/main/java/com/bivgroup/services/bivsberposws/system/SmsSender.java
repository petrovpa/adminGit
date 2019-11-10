/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.system;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

/**
 *
 * @author averichevsm
 */
public class SmsSender {

    private static final Logger logger = Logger.getLogger(SmsSender.class);
    private HashMap<String, String> smsStatusNames = new HashMap<String, String>();

    public HashMap<String, Object> doGet(String login, String password, String seller, String phone, String message) throws IOException {
        HashMap<String, Object> result = new HashMap<String, Object>();
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

        logger.debug(sb.toString());
        GetMethod httpGet = new GetMethod("http://gateway.api.sc/get/?" + sb.toString());
        try {
            if ((password == null) || (password.isEmpty())) {
                logger.error("пароль или логин смс пусты");
            }
            httpClient.executeMethod(httpGet);
            result.put("SMSID", httpGet.getResponseBodyAsString());
            logger.debug("SMSID = " + httpGet.getResponseBodyAsString());
            result.put("httpStatusLine", httpGet.getStatusLine().toString());
        } finally {
            httpGet.releaseConnection();
        }

        return result;
    }

    public HashMap<String, Object> getSmsStatus(String login, String password, String seller, String SmsID) throws IOException {

        HashMap<String, Object> result = new HashMap<String, Object>();
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
