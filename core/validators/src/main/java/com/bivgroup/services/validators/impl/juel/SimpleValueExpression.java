package com.bivgroup.services.validators.impl.juel;

import javax.el.ELContext;

/**
 * Реализация EL-выражения, возвращающая объект как константу
 */
public class SimpleValueExpression extends javax.el.ValueExpression {
    private Object value;

    public SimpleValueExpression(Object value){
        this.value = value;
    }

    @Override
    public Class<?> getExpectedType() {
        return value.getClass();
    }

    @Override
    public Class<?> getType(ELContext context) {
        return value.getClass();
    }

    @Override
    public Object getValue(ELContext context) {
        return value;
    }

    @Override
    public boolean isReadOnly(ELContext context) {
        return true;
    }

    @Override
    public void setValue(ELContext context, Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        return value.equals(obj);
    }

    @Override
    public String getExpressionString() {
        return value.toString();
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean isLiteralText() {
        return false;
    }

}
