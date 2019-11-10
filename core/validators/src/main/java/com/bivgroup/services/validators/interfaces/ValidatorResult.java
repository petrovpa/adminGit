/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Результат проверки
 * @author reson
 */
public interface ValidatorResult {
    /**
     * Возвращает имя валидатора
     * @return 
     */
    String getValidatorName();
    
    /**
     * Возвращает объединенный результат проверки. True - проверка закончилась с ошибкой.
     * @return - объединенный результат проверки. True - проверка закончилась с ошибкой.
     */    
    ValidateLevel getFinalResult();
    void setFinalResult(ValidateLevel result);
    
    String getErrorMessage();
    void setErrorMessage(String value);
    /**
     * Возвращает список результатов проверок, которые привели к такому результату.
     * Список не является иерархическим. Иерархичность проверок можно получить,
     * анализируя свойство FullName
     * @return - список результатов проверок, которые привели к такому результату.
     */
    List<ValidatorResultListItem> getResultList();
    /**
     * Возвращает данный объект в виде Map, включая список результатов,
     * который должен быть преобразован в список мап.
     * @return 
     */
    Map<String,Object> getAsMap();
}
