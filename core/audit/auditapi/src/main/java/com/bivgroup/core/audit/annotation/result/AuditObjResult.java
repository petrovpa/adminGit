package com.bivgroup.core.audit.annotation.result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для получения объекта, которы требуется сохранить
 * в аудит из выходных документа. Объект будет преобразов в "словарь"
 */
@Target({ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditObjResult {

    /**
     * Через равно указывается имя (слева от равно) под которым требуется сохранить
     * и путь до выходного документа (справа от равно), которые требуется сохранить.
     * Пример: "outputDocument=resultJson.CONTRMAP"
     *
     * @return имя и путь документа
     */
    String value();
}
