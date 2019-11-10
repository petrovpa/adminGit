/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

import java.util.Map;

/**
 * Интерфейс фабрики валидаторов
 * @author reson
 */
public interface ConditionValidatorFactory {
    ConditionValidator getConditionValidatorByName(String name) throws ValidationException;
    ConditionValidator getConditionValidatorById(Long id) throws ValidationException;
    ConditionValidator getConditionValidatorByMetadata(Map<String,Object> metadata) throws ValidationException;
}
