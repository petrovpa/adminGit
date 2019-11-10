package com.bivgroup.core.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Аннотация для получения идентификатора документа, который требуется сохранить
 * в аудит из выходных парамтеров.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditOutputId {
    /**
     * Указывается системное имя идентификатора документа и через равно ('=')
     * в каком "словаре" из выходных параметров требуется взять идентификатор.
     * Пример: CONTRID=outputContract
     *
     * @return системное имя идентификатора документа
     */
    String value() default "";
}
