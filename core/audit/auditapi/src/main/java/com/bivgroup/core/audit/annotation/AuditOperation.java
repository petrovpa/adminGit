package com.bivgroup.core.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация аудирования метода
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditOperation {
    /**
     * Наименование выполняемой операции. Обязательный параметр.
     * По операции в elasticsearch строится индекс, по этому операции должна быть в нижнем регистре.
     *
     * @return наименование выполняемой операции
     */
    String value();

    /**
     * Сообщение, которое требуется сохрнаить для операции.
     *
     * @return сообщение
     */
    String message() default "";

}
