/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.bivgroup.services.validators.interfaces.OneConditionValidator;
import com.bivgroup.services.validators.interfaces.ValidateLevel;
import com.bivgroup.services.validators.interfaces.ValidationException;
import com.bivgroup.services.validators.interfaces.ValidatorResult;
import com.bivgroup.services.validators.interfaces.ValidatorResultType;
import com.bivgroup.services.validators.interfaces.ValidatorSession;

/**
 *
 * @author reson
 */
public class JuelOneConditionValidatorImpl extends BaseConditionValidatorImpl implements OneConditionValidator {

    private static final String CONDITION_FIELDNAME = "CONDITION";
    private Logger logger = Logger.getLogger(this.getClass());

    public JuelOneConditionValidatorImpl(Map<String, Object> metadata, ValidatorSession session) {
        super(metadata, session);
    }

    @Override
    public String getCondition() {
        return (String) this.getMetadata().get(CONDITION_FIELDNAME);
    }

    @Override
    public ValidatorResult validate(String parentValidatorName, Map<String, Object> params, ValidateLevel maxLevel, ValidatorResultType resultType) throws ValidationException {

        
        logger.log(Level.DEBUG,"JuelOneConditionValidator: Validate method called. Method params:");
        logger.log(Level.DEBUG,"Name");
        logger.log(Level.DEBUG,this.getName());
        logger.log(Level.DEBUG,"parentValidatorName");
        logger.debug(parentValidatorName);
        logger.log(Level.DEBUG,"params");
        logger.debug(params);
        logger.log(Level.DEBUG,"maxLevel");
        logger.debug(maxLevel);
        logger.log(Level.DEBUG,"resultType");
        logger.debug(resultType);
        ValidatorResult result = null;
        if (null != params) {
            if (null != this.getCondition()) {

                logger.log(Level.DEBUG, "CallBooleanFormula condition is:");
                logger.debug(this.getCondition());
                Boolean boolResult = this.getValidatorSession().getFormulaCalculator().calcBooleanFormula(this.getCondition(), params);
                logger.debug("callBooleanFormula result is ");
                logger.debug(boolResult);
                // если проверка выявила ошибку, то формируем результат с ошибкой
                if (boolResult) {
                    result = this.makeSingleResult(parentValidatorName, params, maxLevel, maxLevel, resultType);
                } else {
                    result = this.makeSingleResult(parentValidatorName, params, ValidateLevel.DISABLED, maxLevel, resultType);
                }
            } else {
                String validatorFullName = (parentValidatorName == null) ? this.getName() : parentValidatorName + '.' + this.getName();
                throw new ValidationException(String.format("Validator [%s] : validation condition is null.\n ", validatorFullName));
            }
        }else{
                String validatorFullName = (parentValidatorName == null) ? this.getName() : parentValidatorName + '.' + this.getName();
                throw new ValidationException(String.format("Validator [%s] : validation params is null.\n ", validatorFullName));          
        }
        
        logger.log(Level.DEBUG,"validate method call end. Result is :");
        logger.debug(result);
        return result;
    }
}
