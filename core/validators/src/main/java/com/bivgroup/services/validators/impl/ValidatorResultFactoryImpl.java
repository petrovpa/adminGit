/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.ArrayList;
import java.util.List;
import com.bivgroup.services.validators.interfaces.ValidateLevel;
import com.bivgroup.services.validators.interfaces.ValidatorResult;
import com.bivgroup.services.validators.interfaces.ValidatorResultFactory;
import com.bivgroup.services.validators.interfaces.ValidatorResultListItem;

/**
 *
 * @author reson
 */
public class ValidatorResultFactoryImpl implements ValidatorResultFactory{

    @Override
    public ValidatorResult getValidatorResult(String validatorName, List<ValidatorResultListItem> resultList) {
        if (null == resultList){
            resultList = new ArrayList<ValidatorResultListItem>();
        }
        return new ValidatorResultImpl(validatorName, ValidateLevel.DISABLED, "", resultList);
    }
    
}
