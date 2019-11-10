/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.system;

import com.bivgroup.services.b2bposws.system.Constants;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

/**
 *
 * @author averichevsm
 */
public class ReferralSender {

    private static final Logger logger = Logger.getLogger(ReferralSender.class);
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;

    public Map<String, Object> doReferralGet(String login, String password, Map<String, Object> refRes, String method, String transactionId, String contrId) throws IOException, Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        String token = refRes.get("TOKEN").toString();
        //String token = "eb07bb44719a0de2946ff1c4aa53e5bc";
        // 2. по method - выбираем урл из параметров реферала.
        String url = "";
        if ("BEGIN".equalsIgnoreCase(method)) {
            url = refRes.get("URLBEGIN").toString();
//            url = "http://api.leads.su/advertiser/conversion/createUpdate?token=!TOKEN!&goal_id=0&transaction_id=!TRANSACTION_ID!&adv_sub=!ADV_SUB!&status=pending";
        }
        if ("DONE".equalsIgnoreCase(method)) {
            url = refRes.get("URLDONE").toString();
//            url = "http://api.leads.su/advertiser/conversion/createUpdate?token=!TOKEN!&goal_id=0&adv_sub=!ADV_SUB!&status=approved";
        }
        if (url.isEmpty()) {
            // если урл пустой = ругаемся и выходим.
            logger.error("Referral method url is empty");
            result.put("STATUS", "ERROR");
            result.put("MESSAGE", "Referral method url is empty");
            return result;
        }
        // сформируем мапу параметров для замен
        Map<String, Object> urlParams = new HashMap<String, Object>();
        urlParams.put("TOKEN", token);
        urlParams.put("ADV_SUB", contrId);
        urlParams.put("TRANSACTION_ID", transactionId);
        // 3. в урле есть параметры, которые надо проставить.
        for (Map.Entry<String, Object> entry : urlParams.entrySet()) {
            String string = entry.getKey();
            Object object = entry.getValue();
            if (object != null) {
                url = url.replaceAll("\\Q!" + string + "!\\E", object.toString());
            }
        }

        // 4 вызываем get с получившимся УРЛом.
        logger.debug(url);
        HttpClient httpClient = new HttpClient();
        GetMethod httpGet = new GetMethod(url);
        try {
            httpClient.executeMethod(httpGet);
            result.put("REFRES", httpGet.getResponseBodyAsString());
            logger.debug("REFRES = " + httpGet.getResponseBodyAsString());
            result.put("httpStatusLine", httpGet.getStatusLine().toString());
        } finally {
            httpGet.releaseConnection();
        }

        return result;
    }

}
