/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.Map;
import com.bivgroup.services.validators.interfaces.ValidatorDataProvider;

/**
 *
 * @author reson
 */
public abstract class BaseDataProvider implements ValidatorDataProvider{
    public static final String NAME_FIELDNAME = "NAME";
    
    private Map<String,Object> metadata;

    public BaseDataProvider(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    
    @Override
    public String getName() {
        return (String)this.getMetadata().get(NAME_FIELDNAME);
    }


    /**
     * @return the metadata
     */
    public Map<String,Object> getMetadata() {
        return metadata;
    }
    
}
