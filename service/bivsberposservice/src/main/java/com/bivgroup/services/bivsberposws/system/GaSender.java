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
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import ru.diasoft.services.config.Config;

/**
 *
 * @author averichevsm
 */
public class GaSender {

    private static final Logger logger = Logger.getLogger(GaSender.class);
    private HashMap<String, String> smsStatusNames = new HashMap<String, String>();

    public HashMap<String, Object> doGaPost(String login, String password, String cid, String step, String ev, String product) throws IOException {
        /*
         var result = $rootScope.ga.url + "?v=" + $rootScope.ga.v + "&tid=" + $rootScope.ga.tid + "&cid=" + encodeURIComponent($rootScope.ga.cid) +
         "&t=event&ec=" + $rootScope.ga.ec + "&ea=" + step + "&el=" + $rootScope.ga.productName;
         if (ev != null) {
         result = result + "&ev=" + encodeURIComponent((Math.round(1.0 * ev)).toString());
         }
         // чтобы не срабатывало кэширование
         result = result + "&z=" + Math.round(Math.random() * 100000);
         //
         return result;
         */

        HashMap<String, Object> result = new HashMap<String, Object>();
        HttpClient httpClient = new HttpClient();
        StringBuilder sb = new StringBuilder();
        Config config = Config.getConfig(Constants.BIVSBERPOSWS);
        String gaEnable = config.getParam("GAENABLE", "false");
        if ("true".equalsIgnoreCase(gaEnable)) {
            
            String gaUrl = config.getParam("GASERVICELOCATION", "http://www.google-analytics.com/collect");
            String gaTid = config.getParam("GATID", "UA-69053084-1");
            String gaEc = config.getParam("GAEC", "online_policy");
            String gaV = config.getParam("GAV", "1");
            sb.append(gaUrl);
            sb.append("?v=");
            sb.append(gaV);
            sb.append("&tid=");
            sb.append(gaTid);
            sb.append("&cid=");
            sb.append(cid);
            sb.append("&t=event&ec=");
            sb.append(gaEc);
            sb.append("&ea=");
            sb.append(step);
            sb.append("&el=");
            sb.append(product);
            if (ev != null) {
                if (!ev.isEmpty()) {
                    sb.append("&ev=");
                    sb.append(String.valueOf(Math.round(Double.valueOf(ev))));
                }
            }
            sb.append("&z=");
            sb.append(Math.round(Math.random() * 100000));

            logger.debug(sb.toString());
            PostMethod httpPost = new PostMethod(sb.toString());
            try {
                httpClient.executeMethod(httpPost);
                result.put("GARES", httpPost.getResponseBodyAsString());
                logger.debug("GARES = " + httpPost.getResponseBodyAsString());
                result.put("httpStatusLine", httpPost.getStatusLine().toString());
            } finally {
                httpPost.releaseConnection();
            }
        }
        return result;
    }

}
