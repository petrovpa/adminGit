package com.bivgroup.core.audit.annotation.param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для получения объекта, которы требуется сохранить
 * в аудит из входных парамтеров. Объект будет преобразов в "словарь"
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditObjParam {
    /**
     * Через равно указывается имя (слева от равно) под которым требуется сохранить
     * и путь до входного документа (справа от равно), которые требуется сохранить.
     * Пример: "inputDocument=obj.CONTRMAP"
     *
     * @return имя и путь документа
     */
    String value();
}
