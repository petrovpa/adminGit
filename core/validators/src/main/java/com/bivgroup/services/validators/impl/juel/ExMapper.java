/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl.juel;

import de.odysseus.el.ExpressionFactoryImpl;
import java.util.Map;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import com.bivgroup.services.validators.interfaces.FormulaCalculator;

/**
 * Объединяющий все элементы вычислений формул в единое целое класс.
 * 
 * @author reson
 */
public class ExMapper implements FormulaCalculator {

    private ExtendedContext localContext = null;
    private BaseVariableMapper localVM = null;
    private ExpressionFactory factory = null;
    private FunctionMapper functionMapper = null;
    
    CVariableMapper variableMapper;

    public ExMapper() {
        localContext = new ExtendedContext();
        factory = ExpressionFactoryImpl.newInstance();

        this.variableMapper = new CVariableMapper();

        //this.variableMapper.getVariables().putAll(variableMap);
        
        this.functionMapper = new ExFunctionMapper();
        variableMapper.setFunctionMapper(this.functionMapper);
        this.setLocalVM(variableMapper);        
    }

    public <T> T calcFormulaByName(String formulaName, Class<T> resultClass) {
       
            String formula = localVM.getFormulaByName(formulaName);
            return this.calcFormula(formula, resultClass);
        
    }

    public <T> T calcFormula(String formula, Class<T> resultClass) {
        localContext.setVariableMapper(localVM);
        localContext.setFunctionMapper(functionMapper);
        ValueExpression expression = factory.createValueExpression(localContext, formula, resultClass);
        T result = (T) expression.getValue(localContext);
        return result;
    }

    /**
     * @return the localVM
     */
    public VariableMapper getLocalVM() {
        return localVM;
    }

    /**
     * @param localVM the localVM to set
     */
    public final void setLocalVM(BaseVariableMapper localVM) {
        this.localVM = localVM;
        this.localVM.setContext(localContext);
    }

    /**
     * @return the functionMapper
     */
    public FunctionMapper getFunctionMapper() {
        return functionMapper;
    }

    /**
     * @param functionMapper the functionMapper to set
     */
    public void setFunctionMapper(FunctionMapper functionMapper) {
        this.functionMapper = functionMapper;
    }

    @Override
    public boolean calcBooleanFormula(String formulaText, Map<String, Object> params) {
        this.variableMapper.getVariables().clear();
        this.variableMapper.getVariables().putAll(params);
        Boolean result = this.calcFormula(formulaText, Boolean.class);
        return result;
    }

    @Override
    public Object calcObjectFormula(String formulaText, Map<String, Object> params) {
        this.variableMapper.getVariables().clear();
        this.variableMapper.getVariables().putAll(params);
        Object result = this.calcFormula(formulaText, Object.class);
        return result;
    }
}
