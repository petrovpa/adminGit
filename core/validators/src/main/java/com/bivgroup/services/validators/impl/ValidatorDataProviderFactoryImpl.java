/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.List;
import java.util.Map;
import com.bivgroup.services.validators.interfaces.ServiceCaller;
import com.bivgroup.services.validators.interfaces.ValidationException;
import com.bivgroup.services.validators.interfaces.ValidatorDataProvider;
import com.bivgroup.services.validators.interfaces.ValidatorDataProviderFactory;
import com.bivgroup.services.validators.interfaces.ValidatorSession;

/**
 *
 * @author reson
 */
public class ValidatorDataProviderFactoryImpl implements ValidatorDataProviderFactory{
    private static final String DISCRIMINATOR_FIELDNAME = "DISCRIMINATOR";


    private ValidatorSession session;

    public ValidatorDataProviderFactoryImpl(ValidatorSession session) {
        this.session = session;
    }
    

    @Override
    public ValidatorDataProvider getValidatorDataProviderById(Long id)throws ValidationException {
        Map<String,Object> metadata = session.getMetadataReader().getDataProviderById(id);
        if (null == metadata){
            throw new ValidationException(String.format("Can't get ValidatorDataProvider metadata by id: [%s]",id));
        }
        return this.getValidatorDataProviderByMetadata(metadata);
    }

    @Override
    public ValidatorDataProvider getValidatorDataProviderByMetadata(Map<String, Object> metadata) throws ValidationException{
        Long discriminator = (Long)metadata.get(DISCRIMINATOR_FIELDNAME);
        ValidatorDataProvider result = null;
        switch (discriminator.intValue()) {
            case 10:{
                result = new ParamDataProvider(metadata);break;
            }
            case 15:{
                result = new ParamListDataProvider(metadata);break;
            }
            case 20:{
                result = new ServiceDataProvider(metadata,session.getServiceCaller());break;
            }
        }
        if (null == result){
            throw new ValidationException(String.format("Unknown ValidatorDataProvider type: [%d]", discriminator));
        }
        return result;
    }
    
}
