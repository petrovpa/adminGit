package com.bivgroup.core.audit.annotation.result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотациия для указания какой обработчик использовать для получения результата операции
 */
@Target({ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditResultOperationHandler {
    /**
     * Имя класса обработчика ответа для получения информации о результате операции
     *
     * @return имя класса обработчика ответа для получения информации о результате операции
     */
    String value();
}
