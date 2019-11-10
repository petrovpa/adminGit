/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl.juel;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 *
 * @author reson
 */
public abstract class BaseVariableMapper extends VariableMapper{

    public abstract String getFormulaByName(String name);
    
    public abstract ELContext getContext();

    public abstract void setContext(ELContext context);
    
    @Override
    public ValueExpression resolveVariable(String variable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
