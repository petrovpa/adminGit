/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.bivgroup.services.validators.interfaces.ValidateLevel;
import com.bivgroup.services.validators.interfaces.ValidatorResult;
import com.bivgroup.services.validators.interfaces.ValidatorResultListItem;

/**
 *
 * @author reson
 */
public class ValidatorResultImpl  implements ValidatorResult{
    private static final String VALIDATORNAME_FIELDNAME = "VALIDATORNAME";
    private static final String FINALRESULT_FIELDNAME = "FINALRESULT";
    private static final String RESULTLIST_FIELDNAME = "RESULTLIST";
    private static final String ERRORMESSAGE_FIELDNAME = "ERRORMESSAGE";
    
    
    private String validatorName;
    private ValidateLevel finalResult;
    private String errorMessage;
    private List<ValidatorResultListItem> resultList;

    public ValidatorResultImpl(String validatorName, ValidateLevel finalResult, String errorMessage,List<ValidatorResultListItem> resultList) {
        this.validatorName = validatorName;
        this.finalResult = finalResult;
        this.resultList = resultList;
        this.errorMessage = errorMessage;
    }
    
    /**
     * @return the validatorName
     */
    @Override
    public String getValidatorName() {
        return validatorName;
    }

    /**
     * @return the finalResult
     */
    @Override
    public ValidateLevel getFinalResult() {
        return finalResult;
    }

    @Override
    public String getErrorMessage(){
        return errorMessage;
    }
    /**
     * @return the resultList
     */
    @Override
    public List<ValidatorResultListItem> getResultList() {
        return resultList;
    }    

    @Override
    public Map<String, Object> getAsMap() {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String,Object>> resultListMap = new ArrayList<Map<String, Object>>();
        for(ValidatorResultListItem item: this.getResultList()){
            resultListMap.add(item.getAsMap());
        }
        result.put(VALIDATORNAME_FIELDNAME, validatorName);
        result.put(FINALRESULT_FIELDNAME, finalResult.getLevel());
        result.put(ERRORMESSAGE_FIELDNAME, errorMessage);
        result.put(RESULTLIST_FIELDNAME, resultListMap);
        return result;
    }

    /**
     * @param finalResult the finalResult to set
     */
    @Override
    public void setFinalResult(ValidateLevel finalResult) {
        this.finalResult = finalResult;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    
}
