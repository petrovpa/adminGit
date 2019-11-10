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
public interface ConditionValidator {
    
    /**
     * Имя валидатора
     * @return 
     */
    String getName();
    


    
    /**
     * Сообщение об ошибке, которое выдается при ошибке валидации
     * @return 
     */
    String getErrorStr();    
    
    /**
     * Возвращает метаданные валидатора.
     * Если у валидатора в метаданных присутствует детализация,
     * то она не возвращается и ее надо выбирать самостоятельно.
     * @return 
     */
    Map<String,Object> getMetadata();

    /**
     * Проверить данные по параметрам
     * @param parentValidatorName - имя родительской проверки или нулл
     * @param params - параметры для проверки
     * @param maxLevel - максимальный уровень, с которой должна работать проверка.
     * @param resultType - уровень подробности возвращаемого результата.
     * @return - результат
     * @throws ValidationException 
     */
    ValidatorResult validate(String parentValidatorName, Map<String,Object> params, ValidateLevel maxLevel,ValidatorResultType resultType) throws ValidationException;
    
    
}
