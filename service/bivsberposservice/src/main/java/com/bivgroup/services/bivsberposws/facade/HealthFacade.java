/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade;

import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("Health")
public class HealthFacade  extends BaseFacade  {
    @WsMethod(requiredParams = {})
    public Map<String,Object> healthCheck(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        return result;
    }
    
}
