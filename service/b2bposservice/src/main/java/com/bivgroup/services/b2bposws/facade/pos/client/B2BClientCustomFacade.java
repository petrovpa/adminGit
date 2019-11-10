package com.bivgroup.services.b2bposws.facade.pos.client;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author andreyboo
 */
@BOName("B2BClientCustom")
public class B2BClientCustomFacade extends B2BDictionaryBaseFacade {
    
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BPclientVerBrowseByFIOnDocSeriesNumberAndBirthDate(Map<String, Object> params) 
            throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BPclientVerBrowseByFIOnDocSeriesNumberAndBirthDate", "dsB2BPclientVerBrowseByFIOnDocSeriesNumberAndBirthDateCount", params);
        return result;
    }
    
}
