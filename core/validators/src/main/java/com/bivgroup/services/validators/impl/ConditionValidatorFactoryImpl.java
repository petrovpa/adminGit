/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.Map;
import com.bivgroup.services.validators.interfaces.ConditionValidator;
import com.bivgroup.services.validators.interfaces.ConditionValidatorFactory;
import com.bivgroup.services.validators.interfaces.ValidationException;
import com.bivgroup.services.validators.interfaces.ValidatorSession;

/**
 *
 * @author reson
 */
public class ConditionValidatorFactoryImpl implements ConditionValidatorFactory{
    private static final String DISCRIMINATOR_FIELDNAME = "DISCRIMINATOR";
    private ValidatorSession session;

    public ConditionValidatorFactoryImpl(ValidatorSession session) {
        this.session = session;
    }
    
    
    
    @Override
    public ConditionValidator getConditionValidatorByName(String name) throws ValidationException {
        Map<String,Object> metadata = session.getMetadataReader().getCheckByName(name);
        if (null == metadata){
            throw new ValidationException(String.format("Can't get Validator metadata by name [%s]", name));
        }
        return this.getConditionValidatorByMetadata(metadata);
    }

    @Override
    public ConditionValidator getConditionValidatorById(Long id) throws ValidationException{
        Map<String,Object> metadata = session.getMetadataReader().getCheckById(id);
        if (null == metadata){
            throw new ValidationException(String.format("Can't get Validator metadata by id [%d]",id));
        }
        return this.getConditionValidatorByMetadata(metadata);
    }

    @Override
    public ConditionValidator getConditionValidatorByMetadata(Map<String, Object> metadata) throws ValidationException{
        Long discriminator = (Long)metadata.get(DISCRIMINATOR_FIELDNAME);
        ConditionValidator result = null;
        switch (discriminator.intValue()){
            case 10:{
                result = new JuelOneConditionValidatorImpl(metadata, session);
                break;
            }
            case 20:{
                result = new MultiConditionValidatorImpl(metadata, session);
                break;
            }
        }
        if (null == result){
            throw new ValidationException(String.format("Unknown validator type: [%d]",discriminator));
        }
        return result;
    }
    
}
