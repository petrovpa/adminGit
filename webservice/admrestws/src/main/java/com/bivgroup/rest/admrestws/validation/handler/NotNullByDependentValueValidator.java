package com.bivgroup.rest.admrestws.validation.handler;

import com.bivgroup.rest.admrestws.validation.annotation.NotNullByDependentValue;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

public class NotNullByDependentValueValidator implements ConstraintValidator<NotNullByDependentValue, Object> {
    private String fieldName;
    private String dependentFieldName;
    private String value;
    private Logger logger;

    @Override
    public void initialize(NotNullByDependentValue constraintAnnotation) {
        this.logger = Logger.getLogger(this.getClass());
        this.fieldName = constraintAnnotation.fieldName();
        this.dependentFieldName = constraintAnnotation.dependentFieldName();
        this.value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean result = true;
        if (value != null) {
            try {
                String fieldValue = BeanUtils.getProperty(value, this.fieldName);
                String dependFieldValue = BeanUtils.getProperty(value, this.dependentFieldName);
                if ((dependFieldValue != null) && (dependFieldValue.equals(this.value)) && (fieldValue == null)) {
                    result = false;
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                logger.error("NotNullByDependentValueValidator method isValid", e);
            }
        }
        return result;
    }
}
