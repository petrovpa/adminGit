/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl.juel;

import java.util.Map;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 *
 * @author reson
 */
public class IteratorVariableMapper extends BaseVariableMapper {

    private CVariableMapper mapper = null;
    private Map<String, Object> bean = null;

    public IteratorVariableMapper(CVariableMapper vMapper) {
        this.mapper = vMapper;
    }

    @Override
    public ValueExpression resolveVariable(String variable) {

        ValueExpression result = null;
        if ((getBean() != null) && (getBean().containsKey(variable))) {
            result = new SimpleValueExpression(getBean().get(variable));
        } else {
            if (mapper != null) {
                result = mapper.resolveVariable(variable);
            }
        }

        return result;
    }

    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        return mapper.setVariable(variable, expression);
    }

    /**
     * @return the bean
     */
    public Map<String, Object> getBean() {
        return bean;
    }

    /**
     * @param bean the bean to set
     */
    public void setBean(Map<String, Object> bean) {
        this.bean = bean;
    }

    @Override
    public String getFormulaByName(String name) {
        return mapper.getFormulaByName(name);
    }

    @Override
    public ELContext getContext() {
        return mapper.getContext();
    }

    @Override
    public void setContext(ELContext context) {
        mapper.setContext(context);
    }
}
