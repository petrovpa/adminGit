/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Выполняет маппинг параметров из Validator более высокого уровня
 * к следующему уровню проверки
 * @author reson
 */
public interface ValidatorParamMapper {
    
    /**
     * Маппит params по правилам, заданным в mapping 
     * @param mapping - правила маппинга
     * @param params - параметры-источник маппинга
     * @return - результирующие смапленные параметры
     */
    Map<String,Object> map(List<Map<String,Object>> mapping, Map<String,Object> params);
}
