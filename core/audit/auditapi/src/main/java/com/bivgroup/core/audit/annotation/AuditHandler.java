package com.bivgroup.core.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотациия для указания какой обработчик использовать для анализа запроса и ответа
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditHandler {
    /**
     * Имя класса обработчика запроса и ответа
     *
     * @return имя класса обработчика запроса и ответа
     */
    String value();
}
