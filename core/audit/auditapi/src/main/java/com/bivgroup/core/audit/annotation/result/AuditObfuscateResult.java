package com.bivgroup.core.audit.annotation.result;

import com.bivgroup.core.audit.Obfuscator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditObfuscateResult {
    Class<Obfuscator>[] value();
}
