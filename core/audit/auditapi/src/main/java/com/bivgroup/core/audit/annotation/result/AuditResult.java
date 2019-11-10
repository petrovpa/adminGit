package com.bivgroup.core.audit.annotation.result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для получения объекта, которы требуется сохранить
 * в аудит из выходных парамтеров, объект будет сохранен так как он есть
 */
@Target({ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditResult {

    /**
     * Через равно указывается имя (слева от равно) под которым требуется сохранить
     * и путь до выходного параметра (справа от равно), которые требуется сохранить.
     * Пример: "outputContractNumber=resultJson.CONTRMAP.CONTRNUMBER"
     *
     * @return имя и путь документа
     */
    String value();
}
