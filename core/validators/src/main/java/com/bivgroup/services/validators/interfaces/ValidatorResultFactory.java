/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

import java.util.List;

/**
 *
 * @author reson
 */
public interface ValidatorResultFactory {
    ValidatorResult getValidatorResult(String validatorName,List<ValidatorResultListItem> resultList);
}
