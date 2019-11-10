/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom;

import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("UserRoleCustom")
public class UserRoleCustomFacade extends BaseFacade {
    
    @WsMethod(requiredParams = {"USERACCOUNTID"})
    public Map<String, Object> dsUserRoleBrowseListByParam(Map<String, Object> params) throws Exception {
        // exec query
        Map<String, Object> result = this.selectQuery("dsUserRoleBrowseListByParam", "", params);
        return result;
    }
    
}
