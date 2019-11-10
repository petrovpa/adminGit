/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс для чтения метаданных,
 * которые необходимы для работы системы проверок.
 * @author reson
 */
public interface MetadataReader {
    
    Map<String,Object> getCheckByName(String name);
    
    Map<String,Object> getCheckById(Long checkId);
    
    /**
     * Возвращает только enabled проверки для заданной сложной проверки
     * @param complexCheckId
     * @return 
     */
    List<Map<String,Object>> getChildChecks(Long complexCheckId);
    
    List<Map<String,Object>> getChildCheckMappings(Long complexCheckId,Long childCheckId);
    
    Map<String,Object> getDataProviderById(Long dataproviderId);
    
    List<Map<String,Object>> getListDataProviderFields(Long dataproviderId);
    
    
}
