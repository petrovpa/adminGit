/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

/**
 *
 * @author reson
 */
public interface ValidatorSession {
    ValidatorDataProviderFactory getValidatorDataProviderFactory();
    MetadataReader getMetadataReader();
    ConditionValidatorFactory getConditionValidatorFactory();
    ValidatorParamMapper getMapper();
    ValidatorResultFactory getValidatorResultFactory();
    ValidatorResultListItemFactory getValidatorResultListItemFactory();
    ServiceCaller getServiceCaller();
    FormulaCalculator getFormulaCalculator();
}
