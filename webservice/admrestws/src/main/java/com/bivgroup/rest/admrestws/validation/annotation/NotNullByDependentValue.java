package com.bivgroup.rest.admrestws.validation.annotation;

import com.bivgroup.rest.admrestws.validation.handler.NotNullByDependentValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {NotNullByDependentValueValidator.class})
@Documented
public @interface NotNullByDependentValue {
    String message() default "{com.bivgroup.rest.admrestws.validation.annotation.NotNullIfDependentNotNull.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Имя поля, которое не должно быть null, если заполнено зависимое
     *
     * @return имя поля, которое не должно быть null
     */
    String fieldName();

    /**
     * Имя зависимого поля, которое должно быть заполнено,
     * чтобы проверять на обязательность нужное поле.
     *
     * @return имя зависимого поля
     */
    String dependentFieldName();

    /**
     * Значение которое должно быть в зависимом поле
     *
     * @return значение в зависимом поле
     */
    String value();

    /**
     * Определяет несколько аннотаций {@link NotNullIfDependentNotNull} для одного и тогоже элемента.
     *
     * @see javax.validation.constraints.NotNull
     */
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        NotNullIfDependentNotNull[] value();
    }
}
