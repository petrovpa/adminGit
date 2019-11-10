/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

import java.util.Map;

/**
 * Интерфейс фабрики провайдеров данных
 * @author reson
 */
public interface ValidatorDataProviderFactory {
    ValidatorDataProvider getValidatorDataProviderById(Long id) throws ValidationException;
    ValidatorDataProvider getValidatorDataProviderByMetadata(Map<String,Object> metadata)throws ValidationException;
}
