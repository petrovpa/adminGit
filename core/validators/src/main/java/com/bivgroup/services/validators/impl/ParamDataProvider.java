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
public class ParamDataProvider extends BaseDataProvider {
    public static final String MAINPARAM_PARAMNAME = "#MAINPARAM";
    private Logger logger = Logger.getLogger(this.getClass());
    
    public ParamDataProvider(Map<String, Object> metadata) {
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
        List<Map<String,Object>> resultList = new ArrayList<Map<String, Object>>();
        resultList.add(params);
        result.put(this.getMainBeanListParamName(), resultList);
        logger.debug("Result is :");
        logger.debug(result);
        return result;
    }



    @Override
    public String getMainBeanListParamName() {
        return MAINPARAM_PARAMNAME+"_"+this.getName();
    }
}
