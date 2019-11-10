/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.HashMap;
import java.util.Map;
import com.bivgroup.services.validators.interfaces.ValidateLevel;
import com.bivgroup.services.validators.interfaces.ValidatorResultListItem;

/**
 *
 * @author reson
 */
public  class ValidatorResultListItemImpl  implements ValidatorResultListItem{
    public static final String VALIDATORID_FIELDNAME = "VALIDATORID";
    public static final String VALIDATORNAME_FIELDNAME = "VALIDATORNAME";
    public static final String VALIDATORFULLNAME_FIELDNAME = "VALIDATORFULLNAME";
    public static final String VALIDATELEVEL_FIELDNAME = "VALIDATELEVEL";
    public static final String VALIDATEDPARAMS_FIELDNAME = "VALIDATEDPARAMS";
    public static final String VALIDATORERRORMESSAGE_FIELDNAME = "VALIDATORERRORMESSAGE";
    public static final String VALIDATIONRESULT_FIELDNAME = "VALIDATIONRESULT";
    
    private Map<String,Object> map = new HashMap<String, Object>();

    public ValidatorResultListItemImpl(Long validatorId,String validatorName, String validatorFullName, String errorMessage, ValidateLevel validationResult, ValidateLevel validateLevel, Map<String, Object> validatedParams) {
        map.put(VALIDATORNAME_FIELDNAME, validatorName);
        map.put(VALIDATORFULLNAME_FIELDNAME, validatorFullName);
        map.put(VALIDATORERRORMESSAGE_FIELDNAME, errorMessage);
        map.put(VALIDATIONRESULT_FIELDNAME, validationResult.getLevel());
        map.put(VALIDATELEVEL_FIELDNAME, validateLevel.getLevel());
        map.put(VALIDATEDPARAMS_FIELDNAME, validatedParams);
        map.put(VALIDATORID_FIELDNAME, validatorId);
    }
    
    
    
    @Override
    public String getValidatorName() {
        return (String)map.get(VALIDATORNAME_FIELDNAME);
    }

    @Override
    public String getValidatorFullName() {
        return (String)map.get(VALIDATORFULLNAME_FIELDNAME);
    }

    @Override
    public String getValidatorErrorMessage() {
        return (String)map.get(VALIDATORERRORMESSAGE_FIELDNAME);
    }

    @Override
    public ValidateLevel getValidationResult() {
        return ValidateLevel.getValidateLevelByLevel((Long)map.get(VALIDATIONRESULT_FIELDNAME));
    }

    @Override
    public ValidateLevel getValidateLevel() {
        return ValidateLevel.getValidateLevelByLevel((Long)map.get(VALIDATELEVEL_FIELDNAME));
    }

    @Override
    public Map<String, Object> getValidatedParams() {
        return (Map<String, Object>)map.get(VALIDATEDPARAMS_FIELDNAME);
    }

    @Override
    public Map<String, Object> getAsMap() {
        return map;
    }

    @Override
    public Long getValidatorId() {
        return (Long)map.get(VALIDATORID_FIELDNAME);
    }
    
}
