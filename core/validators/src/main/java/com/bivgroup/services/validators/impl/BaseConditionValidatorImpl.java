/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.Map;
import org.apache.log4j.Logger;
import com.bivgroup.services.validators.interfaces.ConditionValidator;
import com.bivgroup.services.validators.interfaces.ValidateLevel;
import com.bivgroup.services.validators.interfaces.ValidationException;
import com.bivgroup.services.validators.interfaces.ValidatorResult;
import com.bivgroup.services.validators.interfaces.ValidatorResultListItem;
import com.bivgroup.services.validators.interfaces.ValidatorResultType;
import com.bivgroup.services.validators.interfaces.ValidatorSession;

/**
 *
 * @author reson
 */
public abstract class BaseConditionValidatorImpl implements ConditionValidator {

    public static final String CHECKID_FIELDNAME = "CHECKID";
    public static final String NAME_FIELDNAME = "NAME";
    //public static final String VALIDATELEVEL_FIELDNAME = "LEVEL";
    public static final String ERRORSTR_FIELDNAME = "ERRORMESSAGE";
    public static final String DISCRIMINATOR_FIELDNAME = "DISCRIMINATOR";
    private Map<String, Object> metadata = null;
    private ValidatorSession session = null;
    private Logger logger = Logger.getLogger(this.getClass());
    
    public BaseConditionValidatorImpl(Map<String, Object> metadata, ValidatorSession session) {
        this.metadata = metadata;
        this.session = session;
    }

    public Long getCheckId() {
        return (Long) this.getMetadata().get(CHECKID_FIELDNAME);
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String getName() {
        return (String) this.getMetadata().get(NAME_FIELDNAME);
    }

    @Override
    public String getErrorStr() {
        return (String) this.getMetadata().get(ERRORSTR_FIELDNAME);
    }

    public ValidatorSession getValidatorSession() {
        return this.session;
    }

    private boolean isNeedAddResultListItem(ValidatorResultType resultType, ValidateLevel validationResult){
        logger.debug(String.format("isNeedAddResultListItem called in [%s] Validator", this.getName()));
        logger.debug(String.format("input params: resultType = %s, validationResult = %s", resultType.toString(),validationResult.toString()));
        ValidateLevel minValidateLevel = ValidateLevel.ERROR;
        if (resultType.equals(ValidatorResultType.ERRORS_AND_WARNINGS)|| 
                resultType.equals(ValidatorResultType.ERRORS_AND_WARNINGS_WITH_BEANS)){
            minValidateLevel = ValidateLevel.WARNING;
        }
        if (resultType.equals(ValidatorResultType.ALL_VALIDATION_RESULTS)||
                resultType.equals(ValidatorResultType.ALL_VALIDATION_RESULTS_WITH_BEANS)){
            minValidateLevel = ValidateLevel.DISABLED;
        }
        logger.debug(String.format("calculated minValidateLevel is [%s]", minValidateLevel.toString()));
        return  minValidateLevel.getLevel()<=validationResult.getLevel();
        
    }
    
    private Map<String,Object> getBeanForResult(ValidatorResultType resultType, Map<String,Object> bean){
        Map<String,Object> result = null;
        if (resultType.equals(ValidatorResultType.ALL_VALIDATION_RESULTS_WITH_BEANS)|| 
                resultType.equals(ValidatorResultType.ERRORS_AND_WARNINGS_WITH_BEANS)||
                resultType.equals(ValidatorResultType.ERRORS_WITH_BEANS)||
                resultType.equals(ValidatorResultType.FIRST_ERROR)){
            result = bean;
        }
        return result;
    }
    
    @Override
    public abstract ValidatorResult validate(String parentValidatorName, Map<String, Object> params, ValidateLevel maxLevel, ValidatorResultType resultType) throws ValidationException;

    protected ValidatorResult makeSingleResult(String parentValidatorName, Map<String, Object> bean, ValidateLevel checkResult, ValidateLevel level, ValidatorResultType resultType) {

        String validatorFullName = (parentValidatorName == null) ? this.getName() : parentValidatorName + '.'+this.getName();
        ValidatorResult result = this.getValidatorSession().getValidatorResultFactory().getValidatorResult(this.getName(), null);

        if (this.isNeedAddResultListItem(resultType, checkResult)) {
            
            ValidatorResultListItem resultListItem =
                    this.getValidatorSession().getValidatorResultListItemFactory().getValidatorResultListItem(this.getCheckId(),this.getName(),
                    validatorFullName, this.getErrorStr(), checkResult, level, this.getBeanForResult(resultType, bean));
            result.getResultList().add(resultListItem);
        }
        result.setErrorMessage(this.getErrorStr());
        result.setFinalResult(checkResult);

        return result;
    }
}
