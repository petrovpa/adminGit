package com.bivgroup.core.audit.annotation.param;

import com.bivgroup.core.audit.Obfuscator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditObfuscateParam {
    Class<Obfuscator>[] value();
}
