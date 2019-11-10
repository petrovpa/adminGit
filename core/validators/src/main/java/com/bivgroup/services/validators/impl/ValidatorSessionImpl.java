/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import org.apache.cayenne.access.DataContext;
import com.bivgroup.services.validators.impl.juel.ExMapper;
import com.bivgroup.services.validators.impl.metadata.SQLMetadataReaderImpl;
import com.bivgroup.services.validators.interfaces.ConditionValidatorFactory;
import com.bivgroup.services.validators.interfaces.FormulaCalculator;
import com.bivgroup.services.validators.interfaces.MetadataReader;
import com.bivgroup.services.validators.interfaces.ServiceCaller;
import com.bivgroup.services.validators.interfaces.ValidatorDataProviderFactory;
import com.bivgroup.services.validators.interfaces.ValidatorParamMapper;
import com.bivgroup.services.validators.interfaces.ValidatorResultFactory;
import com.bivgroup.services.validators.interfaces.ValidatorResultListItemFactory;
import com.bivgroup.services.validators.interfaces.ValidatorSession;

/**
 *
 * @author reson
 */
public class ValidatorSessionImpl implements  ValidatorSession {

    private DataContext context;
    private ServiceCaller caller;
    private ValidatorDataProviderFactory validatorDataProviderFactory;
    private MetadataReader metadataReader;
    private ConditionValidatorFactory conditionValidatorFactory;
    private ValidatorParamMapper mapper;
    private ValidatorResultFactory validatorResultFactory;
    private ValidatorResultListItemFactory validatorResultListItemFactory;
    private FormulaCalculator calculator;
    
    
    
    public ValidatorSessionImpl(DataContext context, ServiceCaller caller) {
        this.context = context;
        this.caller = caller;
    }
    
    
    
    @Override
    public ValidatorDataProviderFactory getValidatorDataProviderFactory() {
        if (null == validatorDataProviderFactory){
            validatorDataProviderFactory = new ValidatorDataProviderFactoryImpl(this);
        }
        return validatorDataProviderFactory;
    }

    @Override
    public MetadataReader getMetadataReader() {
        if (null == metadataReader){
            metadataReader = new SQLMetadataReaderImpl(context);
        }
        return metadataReader;
    }

    @Override
    public ConditionValidatorFactory getConditionValidatorFactory() {
        if (null == conditionValidatorFactory){
            conditionValidatorFactory = new ConditionValidatorFactoryImpl(this);
        }
        return conditionValidatorFactory;
    }

    @Override
    public ValidatorParamMapper getMapper() {
        if (null == mapper){
            mapper = new JuelValidatorParamMappingImpl(this);
        }
        return mapper;
    }

    @Override
    public ValidatorResultFactory getValidatorResultFactory() {
        if (null == validatorResultFactory){
            validatorResultFactory = new ValidatorResultFactoryImpl();
        }
        return validatorResultFactory;
    }

    @Override
    public ValidatorResultListItemFactory getValidatorResultListItemFactory() {
        if (null == validatorResultListItemFactory){
            validatorResultListItemFactory = new ValidatorResultListItemFactoryImpl();
        }
        return validatorResultListItemFactory;
    }

    @Override
    public ServiceCaller getServiceCaller() {
        return caller;
    }

    @Override
    public FormulaCalculator getFormulaCalculator() {
        if (null == calculator){
            calculator = new ExMapper();
        }
        return calculator;
    }
    
}
