package com.bivgroup.externalcaller;

import com.bivgroup.config.ConfigUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.Map;

public abstract class ExternalService {
    private static Logger logger = Logger.getLogger(ExternalService.class);

    public ExternalService() {
        logger.debug("ExternalService constructor called...");
    }

    public static ExternalService createInstance() {
        ExternalService externalService = null;
        String implementationClassName = ConfigUtils.getProjectProperty("ru.diasoft.services.insurance.system.external.ExternalService");
        logger.info("ExternalService implementation class name = " + implementationClassName);
        if (implementationClassName != null) {
            try {
                Class<? extends ExternalService> clazz = (Class<? extends ExternalService>) Class.forName(implementationClassName);
                Constructor<? extends ExternalService> constructor = clazz.getConstructor();
                externalService = constructor.newInstance();
            } catch (Exception var4) {
                logger.warn("Unable to create object of the class: " + implementationClassName + ". " + var4.getMessage());
            }
        }

        if (externalService == null) {
            externalService = new ExternalServiceImplementation();
        }

        return externalService;
    }

    protected static String getServiceName() {
        String result = ConfigUtils.getProjectProperty("ru.diasoft.services.inscore.ServiceName");
        return result;
    }

    public abstract Map<String, Object> callExternalService(String var1, String var2, Map<String, Object> var3, String var4, String var5) throws Exception;

    public abstract Map<String, Object> callExternalServiceAsync(String var1, String var2, Map<String, Object> var3, String var4, String var5) throws Exception;

    public Map<String, Object> callExternalService(String serviceName, String methodName, Map<String, Object> params) throws Exception {
        return this.callExternalService(serviceName, methodName, params, (String)null, (String)null);
    }

    public Map<String, Object> callExternalServiceAsync(String serviceName, String methodName, Map<String, Object> params) throws Exception {
        return this.callExternalServiceAsync(serviceName, methodName, params, (String)null, (String)null);
    }
}

