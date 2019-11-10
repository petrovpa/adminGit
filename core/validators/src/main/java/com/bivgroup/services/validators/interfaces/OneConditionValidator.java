/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

/**
 * Проверяльщик одного условия контроля
 * @author reson
 */
public interface OneConditionValidator extends ConditionValidator{
    String getCondition();
}
