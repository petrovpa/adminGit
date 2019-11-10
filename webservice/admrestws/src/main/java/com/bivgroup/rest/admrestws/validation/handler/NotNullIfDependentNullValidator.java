package com.bivgroup.rest.admrestws.validation.handler;

import com.bivgroup.rest.admrestws.validation.annotation.NotNullIfDependentNull;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

public class NotNullIfDependentNullValidator implements ConstraintValidator<NotNullIfDependentNull, Object> {
    private String fieldName;
    private String dependentFieldName;
    private Logger logger;

    @Override
    public void initialize(NotNullIfDependentNull constraintAnnotation) {
        this.logger = Logger.getLogger(this.getClass());
        this.fieldName = constraintAnnotation.fieldName();
        this.dependentFieldName = constraintAnnotation.dependentFieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean result = true;
        if (value != null) {
            try {
                String fieldValue = BeanUtils.getProperty(value, this.fieldName);
                String dependFieldValue = BeanUtils.getProperty(value, this.dependentFieldName);
                if (dependFieldValue == null && fieldValue == null) {
                    result = false;
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error("NotNullIfDependentNotNullValidator method isValid", e);
            }
        }
        return result;
    }
}