package com.bivgroup.services.b2bposws.facade.admin;

import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * Фасад для работы с ролями
 *
 * @author Alex Ivashin
 */
@BOName("B2BRole")
public class B2BRoleFacade extends BaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsGetRoleList(Map<String, Object> params)
            throws Exception {
        Map<String, Object> result = this.selectQuery(
                "dsGetRoleList", "dsGetRoleListCount", params
        );
        return result;
    }
}
