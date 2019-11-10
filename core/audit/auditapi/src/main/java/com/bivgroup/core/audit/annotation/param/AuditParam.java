package com.bivgroup.core.audit.annotation.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для получения объекта, которы требуется сохранить
 * в аудит из входных парамтеров, объект будет сохранен так как оне есть
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditParam {

    /**
     * Через равно указывается имя (слева от равно) под которым требуется сохранить
     * и путь до входного параметра (справа от равно), которые требуется сохранить.
     * Пример: "inputContractNumber=obj.CONTRMAP.CONTRNUMBER"
     *
     * @return имя и путь документа
     */
    String value();
}
