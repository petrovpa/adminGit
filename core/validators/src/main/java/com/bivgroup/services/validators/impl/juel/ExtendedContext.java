/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl.juel;

import de.odysseus.el.util.SimpleContext;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

/**
 * Класс - контекст, позволяющий переопределить 
 * некоторые механизмы вычисления формул в библиотеке JUEL.
 * @author reson
 */
public class ExtendedContext extends SimpleContext{
    private VariableMapper variableMapper = null;
    private FunctionMapper functionMapper = null;
    
    
    
    @Override
    public VariableMapper getVariableMapper() {
        return this.variableMapper;
    }

    /**
     * @param variableMapper the variableMapper to set
     */
    public void setVariableMapper(VariableMapper variableMapper) {
        this.variableMapper = variableMapper;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return this.functionMapper;
    }
    
    public void setFunctionMapper(FunctionMapper mapper){
        this.functionMapper = mapper;
    }
    
}
