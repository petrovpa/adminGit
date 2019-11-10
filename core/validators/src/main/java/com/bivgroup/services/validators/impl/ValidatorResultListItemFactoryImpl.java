/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.Map;
import com.bivgroup.services.validators.interfaces.ValidateLevel;
import com.bivgroup.services.validators.interfaces.ValidatorResultListItem;
import com.bivgroup.services.validators.interfaces.ValidatorResultListItemFactory;

/**
 *
 * @author reson
 */
public class ValidatorResultListItemFactoryImpl implements ValidatorResultListItemFactory{

    @Override
    public ValidatorResultListItem getValidatorResultListItem(Long validatorId,String validatorName, String validatorFullName, String errorMessage, ValidateLevel validationResult, ValidateLevel validateLevel, Map<String, Object> validatedParams) {
        return new ValidatorResultListItemImpl(validatorId,validatorName, validatorFullName, errorMessage, validationResult, validateLevel, validatedParams);
    }
    
}
