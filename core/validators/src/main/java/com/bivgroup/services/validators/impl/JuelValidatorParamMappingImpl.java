/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.bivgroup.services.validators.interfaces.ValidatorParamMapper;
import com.bivgroup.services.validators.interfaces.ValidatorSession;

/**
 *
 * @author reson
 */
public class JuelValidatorParamMappingImpl implements ValidatorParamMapper{

    private static final String SOURCE_NAME_FIELDNAME = "PARENTPARAM";
    private static final String SOURCE_FORMULA_FIELDNAME = "FORMULA";
    private static final String DEST_NAME_FIELDNAME = "CHILDPARAM";
    
    private ValidatorSession session;
    private Logger logger = Logger.getLogger(this.getClass());    

    public JuelValidatorParamMappingImpl(ValidatorSession session) {
        this.session = session;
    }
    
    
    
    
    private Object calcFormula(Map<String, Object> params, String formula){
        return session.getFormulaCalculator().calcObjectFormula(formula, params);
    }
    
    @Override
    public Map<String, Object> map(List<Map<String, Object>> mapping, Map<String, Object> params) {
        Map<String,Object> result = new HashMap<String, Object>();
        logger.log(Level.DEBUG, "Mapper called");
        logger.log(Level.DEBUG,"Mapping is :");
        logger.debug(mapping);
        logger.log(Level.DEBUG, "Params is :");
        logger.debug(params);
        for(Map<String,Object> oneMap:mapping){
            String sourceName = (String)oneMap.get(SOURCE_NAME_FIELDNAME);
            String sourceFormula = (String)oneMap.get(SOURCE_FORMULA_FIELDNAME);
            String destName = (String)oneMap.get(DEST_NAME_FIELDNAME);
            Object resObj = null;
            // если имя параметра источника пустое, то значит значение мы получим из формулы

            if ((null == sourceName) || (sourceName.isEmpty())){
                logger.debug("Source name is null or empty string. Call calcFormula :");
                logger.debug(String.format("sourceFormula is: [%s]",sourceFormula));
                logger.debug(sourceFormula);                
                resObj = calcFormula(params, sourceFormula);
                logger.debug("CalcFormula result is :");
                logger.debug(resObj);
            } else {
                logger.debug("source name is not null. Map from source name");
                logger.debug(String.format("Source name is : [%s]",sourceName));
                resObj = params.get(sourceName);
                logger.debug("Map result is : ");
                logger.debug(resObj);                
            }
            result.put(destName, resObj);
        }
        
        logger.debug("Final mapping result is :");
        logger.debug(result);
                
        return result;
    }
    
}
