/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import com.bivgroup.services.validators.interfaces.ConditionValidator;
import com.bivgroup.services.validators.interfaces.MultiConditionValidator;
import com.bivgroup.services.validators.interfaces.ValidateLevel;
import com.bivgroup.services.validators.interfaces.ValidationException;
import com.bivgroup.services.validators.interfaces.ValidatorDataProvider;
import com.bivgroup.services.validators.interfaces.ValidatorParamMapper;
import com.bivgroup.services.validators.interfaces.ValidatorResult;
import com.bivgroup.services.validators.interfaces.ValidatorResultListItem;
import com.bivgroup.services.validators.interfaces.ValidatorResultType;
import com.bivgroup.services.validators.interfaces.ValidatorSession;

/**
 *
 * @author reson
 */
public class MultiConditionValidatorImpl extends BaseConditionValidatorImpl implements MultiConditionValidator {

    public static final String DATAPROVIDERID_FIELDNAME = "DATAPROVID";
    public static final String BEAN_PARAMNAME = "BEAN";
    public static final String CHECKLINKID_FIELDNAME = "CHECKLINKID";
    
    private Logger logger = Logger.getLogger(this.getClass());

    public MultiConditionValidatorImpl(Map<String, Object> metadata, ValidatorSession session) {
        super(metadata, session);
    }

    private List<Map<String, Object>> getChildChecks() {
        return this.getValidatorSession().getMetadataReader().getChildChecks(this.getCheckId());
    }

    private ValidatorDataProvider getDataProvider() throws ValidationException {
        Long dataProviderId = (Long) this.getMetadata().get(DATAPROVIDERID_FIELDNAME);
        Map<String, Object> providerMetadata = this.getValidatorSession().getMetadataReader().getDataProviderById(dataProviderId);
        ValidatorDataProvider provider = this.getValidatorSession().getValidatorDataProviderFactory().getValidatorDataProviderByMetadata(providerMetadata);
        return provider;
    }

    private Map<Long, Object> getchildMappingMap() {
        List<Map<String, Object>> childMapping = this.getValidatorSession().getMetadataReader().getChildCheckMappings(this.getCheckId(), null);
        Map<Long, Object> childMappingMap = new HashMap<Long, Object>();
        for (Map<String, Object> map : childMapping) {
            Long id = (Long) map.get(CHECKLINKID_FIELDNAME);
            List<Map<String, Object>> mappingParamList = (List<Map<String, Object>>) childMappingMap.get(id);
            if (null == mappingParamList) {
                mappingParamList = new ArrayList<Map<String, Object>>();
            }
            mappingParamList.add(map);
            childMappingMap.put(id, mappingParamList);
        }
        return childMappingMap;
    }

    @Override
    public ValidatorResult validate(String parentValidatorName, Map<String, Object> params, ValidateLevel maxLevel, ValidatorResultType resultType) throws ValidationException {

        logger.log(Level.DEBUG,"MultiConditionValidator: Validate method called. Method params:");
        logger.log(Level.DEBUG,"Name");
        logger.debug(this.getName());
        logger.log(Level.DEBUG,"parentValidatorName");
        logger.debug(parentValidatorName);
        logger.log(Level.DEBUG,"params");
        logger.debug(params);
        logger.log(Level.DEBUG,"maxLevel");
        logger.debug(maxLevel);
        logger.log(Level.DEBUG,"resultType");
        logger.debug(resultType);
        List<ValidatorResultListItem> resultList = new ArrayList<ValidatorResultListItem>();
        boolean stopValidation = false;
        ValidateLevel finalResult = ValidateLevel.DISABLED;

        List<Map<String, Object>> childChecks = this.getChildChecks();
        if ((childChecks != null) && (childChecks.size() > 0)) {
            // получаем данные для проверки
            ValidatorDataProvider provider = this.getDataProvider();

            Map<String, Object> checkedData = new HashMap<String, Object>();
            checkedData.putAll(params);
            checkedData.putAll(provider.getData(params));
            List<Map<String, Object>> mainBeansList = (List<Map<String, Object>>) checkedData.get(provider.getMainBeanListParamName());

            // получаем маппинг
            Map<Long, Object> childMappingMap = this.getchildMappingMap();
            logger.log(Level.DEBUG,"ChildMapping");
            logger.debug(childMappingMap);

            // Сюда попадают только enabled проверки, обеспечивается провайдером
            // метаданных. 
            // Для каждой дочерней проверки делаем: 
            // 1. Если есть маппинг, то выполняем маппинг параметров. 
            // 2. Проверяем данные валидатором. 
            // 3. Формируем результат.
            logger.log(Level.DEBUG,"childChecks");
            logger.debug(childChecks);
            ValidatorParamMapper mapper = this.getValidatorSession().getMapper();
            for (Map<String, Object> check : childChecks) {
                if (stopValidation) {
                    break;
                }
                logger.log(Level.DEBUG,"check");
                logger.debug(check);
                ConditionValidator validator = this.getValidatorSession().getConditionValidatorFactory().getConditionValidatorByMetadata(check);

                Long checkLinkId = (Long) check.get(CHECKLINKID_FIELDNAME);
                List<Map<String, Object>> validatorMapping = (List<Map<String, Object>>) childMappingMap.get(checkLinkId);
                logger.log(Level.DEBUG,"mainBeanList");
                logger.debug(mainBeansList);
                
                for (Map<String, Object> bean : mainBeansList) {
                    if (stopValidation) {
                        break;
                    }
                    logger.log(Level.DEBUG,"bean");
                    logger.debug(bean);
                    checkedData.put(BEAN_PARAMNAME, bean);
                    Map<String, Object> mappedParams = null;
                    if (null == validatorMapping) {
                        mappedParams = bean;
                    } else {
                        mappedParams = mapper.map(validatorMapping, checkedData);
                    }
                    logger.log(Level.DEBUG,"mappedParams");
                    logger.debug(mappedParams);
                    String validatorFullName = (parentValidatorName == null) ? this.getName() : parentValidatorName + '.' + this.getName();
                    ValidatorResult validatorResult = validator.validate(validatorFullName, mappedParams, maxLevel, resultType);
                    logger.log(Level.DEBUG,"validatorResult");
                    logger.debug(validatorResult);
                    if (validatorResult.getFinalResult().getLevel() > finalResult.getLevel()) {
                        finalResult = validatorResult.getFinalResult();
                    }
                    logger.log(Level.DEBUG,"finalResult");
                    logger.debug(finalResult.toString());
                    stopValidation = stopValidation || (resultType.equals(ValidatorResultType.FIRST_ERROR) && finalResult.equals(ValidateLevel.ERROR));
                    resultList.addAll(validatorResult.getResultList());
                }

            }
        }

        logger.debug("Before call makeSingleResult");
        logger.debug("FinalResult");
        logger.debug(finalResult.toString());
        logger.debug("resultType");
        logger.debug(resultType.toString());
        ValidatorResult result = this.makeSingleResult(parentValidatorName, params, finalResult, maxLevel, resultType);
        result.getResultList().addAll(resultList);

        logger.log(Level.DEBUG,"validate method call end. Result is :");
        logger.debug(result);
        return result;
    }
}
