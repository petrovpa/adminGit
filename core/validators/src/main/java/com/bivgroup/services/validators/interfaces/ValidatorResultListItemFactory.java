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
public interface ValidatorResultListItemFactory {

    /**
     *
     * @param validatorName
     * @param validatorFullName
     * @param errorMessage
     * @param validationResult
     * @param validateLevel
     * @param validatedParams
     * @return
     */
    ValidatorResultListItem getValidatorResultListItem(Long validatorId,String validatorName, 
            String validatorFullName, String errorMessage, ValidateLevel validationResult,
              ValidateLevel validateLevel, Map<String, Object> validatedParams);
}
