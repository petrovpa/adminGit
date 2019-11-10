/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.bivgroup.services.validators.interfaces.ValidationException;

/**
 *
 * @author reson
 */
public class ParamListDataProvider extends ListDataProvider {
        private Logger logger = Logger.getLogger(this.getClass());


    public ParamListDataProvider(Map<String, Object> metadata) {
        super(metadata);
    }

    @Override
    public Map<String, Object> getData(Map<String, Object> params) throws ValidationException {
        logger.debug("getData called");
        logger.debug("DataProviderName is :");
        logger.debug(this.getName());
        logger.debug("params is :");
        logger.debug(params);  
        logger.debug("Metadata is :");
        logger.debug(this.getMetadata());
        
        Map<String,Object> result = new HashMap<String, Object>();
        result.putAll(params);
        Object mainBeanListParam = params.get(getMainBeanListParamName());
        if (!(mainBeanListParam instanceof List)){
            List<Map<String,Object>> listParam = new ArrayList<Map<String, Object>>();
            listParam.add((Map<String,Object>)mainBeanListParam);
            result.put(this.getMainBeanListParamName(), listParam);            
        }
        logger.debug("Result is :");
        logger.debug(result);        
        return result;
    }


}
