package com.bivgroup.services.b2bposws.facade.pos.sal.custom;

import com.bivgroup.services.b2bposws.system.Constants;
import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("SAL_CheckCustom")
public class SAL_CheckCustomFacade extends BaseFacade {

    @WsMethod(requiredParams = {"NAME", "LASTNAME", "BIRTHDATE", "DOCSERIES", "DOCNUMBER", "CID", "REFERENCE"})
    public Map<String, Object> dsSAL_DoCheck(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> passportParams = new HashMap<String, Object>();
        passportParams.put(RETURN_AS_HASH_MAP, "TRUE");
        passportParams.put("DOCSERIES", params.get("DOCSERIES"));
        passportParams.put("DOCNUMBER", params.get("DOCNUMBER"));
        passportParams.put("CID", params.get("CID"));
        passportParams.put("REFERENCE", params.get("REFERENCE"));
        Map<String, Object> passportRes = this.callService(Constants.B2BPOSWS, "dsSAL_InvalidPassportsDoCheck", passportParams, login, password);
        Map<String, Object> result = new HashMap<String, Object>();
        if (passportRes != null) {
            if ((passportRes.get("CHECKRES") != null) && (passportRes.get("CHECKRES").toString().equalsIgnoreCase("OK"))) {
                Map<String, Object> terroristParams = new HashMap<String, Object>();
                terroristParams.put(RETURN_AS_HASH_MAP, "TRUE");
                terroristParams.put("NAME", params.get("NAME"));
                terroristParams.put("LASTNAME", params.get("LASTNAME"));
                terroristParams.put("MIDDLENAME", params.get("MIDDLENAME"));
                terroristParams.put("BIRTHDATE", params.get("BIRTHDATE"));
                terroristParams.put("DOCSERIES", params.get("DOCSERIES"));
                terroristParams.put("DOCNUMBER", params.get("DOCNUMBER"));
                terroristParams.put("CID", params.get("CID"));
                terroristParams.put("REFERENCE", params.get("REFERENCE"));
                Map<String, Object> terroristRes = this.callService(Constants.B2BPOSWS, "dsSAL_TerroristsDoCheck", terroristParams, login, password);
                if (terroristRes != null) {
                    result.put("CHECKRES", terroristRes.get("CHECKRES"));
                } else {
                    result.put("CHECKRES", "Ошибка при проверке");
                }
            } else {
                result.put("CHECKRES", passportRes.get("CHECKRES"));
            }
        } else {
            result.put("CHECKRES", "Ошибка при проверке");
        }
        return result;
    }
}
