/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

import java.util.Map;

/**
 * Элемент списка результатов проверки.
 * @author reson
 */
public interface ValidatorResultListItem {
    
    Long getValidatorId();
    String getValidatorName();
    String getValidatorFullName();
    String getValidatorErrorMessage();
    /**
     * Возвращает результат проверки. True - проверка закончилась с ошибкой.
     * @return - результат проверки. True - проверка закончилась с ошибкой.
     */
    ValidateLevel getValidationResult();
    ValidateLevel getValidateLevel();
    /**
     * Параметры, которые пришли на вход проверки
     * @return - Параметры, которые пришли на вход проверки
     */
    Map<String,Object> getValidatedParams(); 
    Map<String,Object> getAsMap();
}
