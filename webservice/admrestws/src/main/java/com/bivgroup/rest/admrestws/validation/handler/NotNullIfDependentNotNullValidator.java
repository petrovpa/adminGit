package com.bivgroup.rest.admrestws.validation.handler;

import com.bivgroup.rest.admrestws.validation.annotation.NotNullIfDependentNotNull;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

public class NotNullIfDependentNotNullValidator implements ConstraintValidator<NotNullIfDependentNotNull, Object> {
    private String fieldName;
    private String dependentFieldName;
    private Logger logger;

    @Override
    public void initialize(NotNullIfDependentNotNull constraintAnnotation) {
        this.logger = Logger.getLogger(this.getClass());
        this.fieldName = constraintAnnotation.fieldName();
        this.dependentFieldName = constraintAnnotation.dependentFieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean result = false;
        if (value != null) {
            try {
                String fieldValue = BeanUtils.getProperty(value, this.fieldName);
                String dependFieldValue = BeanUtils.getProperty(value, this.dependentFieldName);
                if (dependFieldValue == null) {
                    result = true;
                } else {
                    if (fieldValue != null) {
                        result = true;
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error("NotNullIfDependentNotNullValidator method isValid", e);
            }
        }
        return result;
    }
}
