/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import com.bivgroup.services.bivsberposws.system.Constants;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("AngularClientActionLog")
public class AngularClientActionLogFacade extends AngularContractCustomBaseFacade {

    private static final String BIVSBERPOSWS_SERVICE_NAME = Constants.BIVSBERPOSWS;

    public String convertQuotes(String str) {
        return str.replaceAll("'", "''");
    }

    @WsMethod(requiredParams = {"OBJMAP"})
    public Map<String, Object> dsAngularUserActionLogCreate(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = new HashMap<String, Object>();

        // проверить наличие обязательных параметров.
        Map<String, Object> objMap = (Map<String, Object>) params.get("OBJMAP");
        if (objMap.get("obj") != null) {
            Map<String, Object> obj = (Map<String, Object>) objMap.get("obj");
            if (obj.get("ACTION") != null) {
                Map<String, Object> qParam = new HashMap<String, Object>();
                qParam.put("ACTION", obj.get("ACTION"));
                qParam.put("NOTE", obj.get("NOTE"));
                if (obj.get("SESSIONTOKEN") != null) {
                    String token = obj.get("SESSIONTOKEN").toString();
                    qParam.put("SESSIONID", base64Decode(token));
                } else {
                    UUID token = generateSessionToken();
                    result.put("SESSIONTOKEN", base64Encode(token.toString()));
                    qParam.put("SESSIONID", token.toString());
                }
                qParam.put("VALUE", obj.get("VALUE"));
                qParam.put("CONTRID", obj.get("CONTRID"));
                qParam.put("PARAM1", obj.get("PARAM1"));
                qParam.put("PARAM2", obj.get("PARAM2"));
                qParam.put("PARAM3", obj.get("PARAM3"));
                qParam.put("PARAM4", obj.get("PARAM4"));
                qParam.put("PARAM5", obj.get("PARAM5"));
                if (obj.get("RAWDATA") != null) {
                    String rawres = convertQuotes(obj.get("RAWDATA").toString());
/*                    if (rawres.length() > 4000) {
                        rawres = rawres.substring(0, 3990);
                    }*/
                    qParam.put("RAWDATA", rawres);
                } else {
                    qParam.put("RAWDATA", "");
                }
                Map<String, Object> res = this.callService(BIVSBERPOSWS_SERVICE_NAME, "dsInsClientActionLogCreate", qParam, login, password);
                return result;
            }
        }
        return result;
    }

    // уже есть в AngularContractCustomBaseFacade
    /*
    public static String base64Decode(String input) {
        Base64 decoder = new Base64(true);
        return bytesToString(decoder.decode(input));
    }

    public static String base64Encode(String input) {
        Base64 encoder = new Base64(true);
        String result = bytesToString(encoder.encode(stringToBytes(input)));
        return result.substring(0, result.length() - 2);
    }
     */
    private UUID generateSessionToken() {
        UUID token = UUID.randomUUID();
        return token;
    }

}
