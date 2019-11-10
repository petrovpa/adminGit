package com.bivgroup.core.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для указания откуда брать информацию о пользователе.
 * Может указываться над классом или над методом.
 * Если методы используют один контроллер сессии, тогда указать
 * лучше над классом, если есть методы в классе, которые
 * расшифровываются другим контроллером или информация о пользователе
 * лежит в по другому пути, тогда можно переопределить аннотацию
 * указав ее над методом.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditUserInfo {
    /**
     * Путь до поля, которое хранит информацию о пользователе.
     * Например: obj.sessionid
     *
     * @return путь до поля, которое хранит информацию о пользователе
     */
    String value();

    /**
     * Наименование класса контроллера сессии для расшифровки sessionId,
     * если имя контроллера не указано, тогда поле взятое из #{@link #value()}
     * сохраняется в аудите в поле login без преобразований.
     *
     * @return имя класса контроллера сессии
     */
    String sessionController() default "";

    /**
     * Наименование параметра, под которым логин лежит в расшифрованой сессии.
     *
     * @return наименование параметра, под которым логин лежит в расшифрованой сессии
     */
    String loginParamName() default "";

    /**
     * Наименование параметра, под которым идентификар пользователя лежит в расшифрованой сессии.
     *
     * @return наименование параметра, под которым логин лежит в расшифрованой сессии
     */
    String userAccountIdParamName() default "";
}
