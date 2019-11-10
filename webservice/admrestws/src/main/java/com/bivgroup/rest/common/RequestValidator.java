package com.bivgroup.rest.common;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.StringJoiner;

public class RequestValidator {
    /**
     * Валидация входных данных
     */
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    public <T> String validateResponse(T checkParams) {
        final Validator validator = this.validatorFactory.getValidator();
        final Set<ConstraintViolation<T>> violations = validator.validate(checkParams);
        final StringBuilder messageBuilder = new StringBuilder();
        if (!violations.isEmpty()) {
            messageBuilder.append("Ошибка вызова метода, несоблюдены требования к входным параметрам: ");
            StringJoiner joiner = new StringJoiner(", \n");
            String error = "";
            for (final ConstraintViolation<T> violation : violations) {
                error = violation.getMessage();
                joiner.add(error);
            }
            messageBuilder.append(joiner.toString());
        }
        return messageBuilder.toString();
    }
}
