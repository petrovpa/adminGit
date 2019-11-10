/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.validators.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.bivgroup.services.validators.interfaces.ServiceCaller;
import com.bivgroup.services.validators.interfaces.ValidationException;

/**
 *
 * @author reson
 */
public class ServiceDataProvider extends ListDataProvider {

    private static final String SERVICENAME_FIELDNAME = "SERVICENAME";
    private static final String METHODNAME_FIELDNAME = "METHODNAME";
    private static final String KIND_FIELDNAME = "KIND";
    private ServiceCaller serviceCaller;
    private Logger logger = Logger.getLogger(this.getClass());

    public ServiceDataProvider(Map<String, Object> metadata, ServiceCaller serviceCaller) {
        super(metadata);
        this.serviceCaller = serviceCaller;
    }

    private Map<String, Object> getInputParams(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (Map<String, Object> field : this.getFields()) {
            Long kind = (Long) field.get(KIND_FIELDNAME);
            if ((kind != null) && (Long.valueOf(1).equals(kind))) {
                result.put((String) field.get(NAME_FIELDNAME), params.get((String) field.get(NAME_FIELDNAME)));
            }
        }
        logger.debug("Service input params is :");
        logger.debug(result);
        return result;
    }

    public String getServiceName() {
        return (String) this.getMetadata().get(SERVICENAME_FIELDNAME);
    }

    public String getMethodName() {
        return (String) this.getMetadata().get(METHODNAME_FIELDNAME);
    }

    @Override
    public Map<String, Object> getData(Map<String, Object> params) throws ValidationException {
        logger.debug("getData called");
        logger.debug("DataProviderName is :");
        logger.debug(this.getName());
        logger.debug("params is :");
        logger.debug(params);
        logger.debug("Metadata is :");
        logger.debug(this.getMetadata());
        Map<String, Object> result = null;
        try {
            result = this.serviceCaller.callService(getServiceName(), getMethodName(), this.getInputParams(params));
        } catch (Exception ex) {
            throw new ValidationException("Error service call", ex);
        }
        logger.debug("Service call result is: ");
        logger.debug(result);
        Object mainBeanListParam = result.get(getMainBeanListParamName());
        if (mainBeanListParam instanceof Map) {
            List<Map<String, Object>> listParam = new ArrayList<Map<String, Object>>();
            listParam.add((Map<String, Object>) mainBeanListParam);
            result.put(this.getMainBeanListParamName(), listParam);
        } else {
            if (mainBeanListParam instanceof List) {
                result.put(this.getMainBeanListParamName(), mainBeanListParam);
            } else {
                // тут считаем, что вернулось скалярное и его пихаем в список и в мапу
                List<Map<String, Object>> listParam = new ArrayList<Map<String, Object>>();
                Map<String,Object> map = new HashMap<String, Object>();
                map.put(this.getMainBeanListParamName(), mainBeanListParam);
                listParam.add(map);
                result.put(this.getMainBeanListParamName(), listParam);

            }
        }
        logger.debug("Result is :");
        logger.debug(result);
        return result;
    }
}
