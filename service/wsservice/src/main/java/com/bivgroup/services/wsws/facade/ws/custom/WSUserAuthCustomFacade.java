package com.bivgroup.services.wsws.facade.ws.custom;

import com.bivgroup.services.wsws.system.AuthErrorConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * Фасад для авторизации пользователя
 *
 * @author ilich
 */
@BOName("WSUserAuthCustom")
public class WSUserAuthCustomFacade extends BaseFacade {
    
    @WsMethod(requiredParams = {"LOGIN", "AUTHMETHODSYSNAME", "SERVICENAME", "METHODNAME"})
    public Map<String, Object> dsWSUserCheckAuth(Map<String, Object> params) throws Exception {
        Map<String, Object> checkParams = new HashMap<String, Object>();
        checkParams.put("LOGIN", params.get("LOGIN"));
        checkParams.put("AUTHMETHODSYSNAME", params.get("AUTHMETHODSYSNAME"));
        checkParams.put("METHODNAME", params.get("METHODNAME"));
        checkParams.put("SERVICENAME", params.get("SERVICENAME"));
        if (params.get("TEMPLATECODE") != null) {
            checkParams.put("TEMPLATECODE", params.get("TEMPLATECODE"));
        }
        Map<String, Object> authRes = this.selectQuery("dsWSUserCheckAuth", null, checkParams);
        Long authError = AuthErrorConstants.AUTHERROR_INVALID_LOGIN;
        if ((authRes != null) && (authRes.get(RESULT) != null) && (((List<Map<String, Object>>)authRes.get(RESULT)).size() > 0)) {
            authRes = ((List<Map<String, Object>>)authRes.get(RESULT)).get(0);
            if (authRes.get("LOGIN") == null) {
                authError = AuthErrorConstants.AUTHERROR_INVALID_LOGIN;
            } else if (authRes.get("AUTHMETHODSYSNAME") == null) {
                authError = AuthErrorConstants.AUTHERROR_AUTHMETHOD_DENIED;
            } else if ((authRes.get("METHODNAME") == null) || (authRes.get("SERVICENAME") == null)) {
                authError = AuthErrorConstants.AUTHERROR_METHOD_DENIED;
            } else if ((params.get("TEMPLATECODE") != null) && (authRes.get("TEMPLATECODE") == null)) {
                authError = AuthErrorConstants.AUTHERROR_INVALID_TEMPLATECODE;
            } else {
                authError = AuthErrorConstants.AUTHERROR_NONE;
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("AUTHERROR", authError);
        result.put("AUTHMAP", authRes);
        return result;
    }

}
