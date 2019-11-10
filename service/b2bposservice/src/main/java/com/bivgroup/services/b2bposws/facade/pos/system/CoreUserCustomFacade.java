package com.bivgroup.services.b2bposws.facade.pos.system;

import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("CoreUserCustom")
public class CoreUserCustomFacade extends BaseFacade {

    @WsMethod(requiredParams = {"USERID"})
    public Map<String, Object> dsCoreUserUpdate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsCoreUserUpdate", params);
        result.put("USERID", params.get("USERID"));
        return result;
    }
}
