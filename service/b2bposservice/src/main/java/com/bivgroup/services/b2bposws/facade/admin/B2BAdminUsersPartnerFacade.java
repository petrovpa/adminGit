/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.admin;

import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author Ivanovra
 *
 * Facade work with UsersPartner interface
 *
 */
@BOName("B2BUsersPartner")
public class B2BAdminUsersPartnerFacade extends BaseFacade {

    /**
     *
     * @param params Options that come with the customer
     * @return the result of the query that returns a list of units as the root
     * element (defined in parameter) to its extreme (last)
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsGetAdminDepartmentListFromRoot(Map<String, Object> params)
            throws Exception {
        Map<String, Object> result = this.selectQuery(
                "dsGetAdminDepartmentListFromRoot",
                "dsGetAdminDepartmentListFromRootCount",
                params);
        return result;
    }

}
