/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl.juel;

import java.util.HashMap;
import java.util.Map;
import javax.el.ELContext;
import javax.el.FunctionMapper;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import org.apache.log4j.Logger;

/**
 * Маппер переменных для вычисления формул. Значение переменных ищется следующим
 * образом: 1. Ищется по имени в списке переменных. 2. Ищется по имени в списке
 * формул. Формула вычисляется, значение возвращается.
 *
 * @author reson
 */
public class CVariableMapper extends BaseVariableMapper {

    private Map<String, Object> variables = new HashMap<String, Object>();
    //private Map<String, CalculatorFormula> formulas = new HashMap<String, CalculatorFormula>();
    private Map<String, Object> formulaResultsCache = new HashMap<String, Object>();
    private Map<String, Object> returnParams = new HashMap<String, Object>();
    private FunctionMapper functionMapper = null;
    private ELContext context = null;
    private Logger logger = Logger.getLogger(this.getClass());

    public ELContext getContext() {
        return context;
    }

    public void setContext(ELContext context) {
        this.context = context;
    }

    /**
     * Пытаемся получить значение переменной в следующем порядке: 1. Ищем
     * переменную в списке переменных, если нашли, то возвращаем значение. 2.
     * Ищем формулу в списке формул. Если нашли, то вычисляем
     *
     * @param string
     * @return
     */
    @Override
    public ValueExpression resolveVariable(String string) {
        logger.debug(String.format("Resolve variable [%s]", string));
        Object value = this.getVariables().get(string);
        //if (null == value) {
        if (!this.getVariables().containsKey(string)) {
            logger.debug(String.format("Variable [%s] not found.", string));
            //return null;
            ValueExpression result = new SimpleValueExpression(null);
            return result;
        } else {
            if (null != value) {
                logger.debug(String.format("Variable [%s] found. Variable value is [%s]", string, value.toString()));
            } else {
                logger.debug(String.format("Variable [%s] found. Variable value is NULL", string));
            }

        }
        ValueExpression result = new SimpleValueExpression(value);
        return result;
    }

    @Override
    public ValueExpression setVariable(String string, ValueExpression ve) {
        if (ve != null) {
            this.getVariables().put(string, ve.getValue(this.getContext()));
        } else {
            this.getVariables().remove(string);
        }
        return ve;
    }

    /**
     * @return the variables
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * @param variables the variables to set
     */
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
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

    public String getFormulaByName(String name) {
        return null;
    }

    /**
     * @return the returnParams
     */
    public Map<String, Object> getReturnParams() {
        return returnParams;
    }
}
