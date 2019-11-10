/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

import java.util.Map;

/**
 *
 * @author reson
 */
public interface ServiceCaller {
    Map<String, Object> callService(String serviceName, String methodName,
            Map<String, Object> params) throws Exception;    
}
