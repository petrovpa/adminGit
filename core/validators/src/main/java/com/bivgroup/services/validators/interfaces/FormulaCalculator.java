/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

import java.util.Map;

/**
 *
 * @author reson
 */
public interface FormulaCalculator {
    boolean calcBooleanFormula(String formulaText, Map<String,Object> params);
    Object calcObjectFormula(String formulaText,Map<String,Object> params);
}
