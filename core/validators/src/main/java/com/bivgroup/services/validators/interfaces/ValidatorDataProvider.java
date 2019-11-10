/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Провайдер данных, формирующий данные для проверки OneConditionChecker
 * @author reson
 */
public interface ValidatorDataProvider {
    /**
     * Имя провайдера
     * @return 
     */
    String getName();
    
    /**
     * Выдает бин для проверки чекером из параметров, которые выбраны сервисом
     * выборки данных
     * @param params
     * @return 
     */
    Map<String,Object> getData(Map<String,Object> params) throws ValidationException;
    
    String getMainBeanListParamName();
    
}
