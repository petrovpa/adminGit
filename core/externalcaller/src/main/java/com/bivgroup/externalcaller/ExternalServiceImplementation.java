package com.bivgroup.externalcaller;

import com.bivgroup.config.Config;
import com.bivgroup.xmlutil.XmlUtil;
import com.bivgroup.xmlutil.exception.SoftServiceErrorException;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ExternalServiceImplementation extends ExternalService {

    private static Logger logger = Logger.getLogger(ExternalServiceImplementation.class);
    private static final String COREWS = "corews";
    private static final String COREWS_CONFIG = "corews";

    public ExternalServiceImplementation() {
    }

    private static Config getConfig() {
        return Config.getConfig(getServiceName());
    }

    private Map<String, Object> callExternalServiceImpl(String serviceName, String methodName, Map<String, Object> params, boolean isAsync, String login, String password) throws Exception {
        XmlUtil util = null;
        if (login != null) {
            util = new XmlUtil(login, password);
        } else {
            util = new XmlUtil();
        }

        String locCoreUrl = Config.getConfig(COREWS).getParam(COREWS, "http://localhost:8080/corews/corews");
        XmlUtil.setLocatorServiceURL(locCoreUrl);
        String serviceNameInBrackets = "[" + serviceName + "]";
        String serviceUrl = util.getServiceURL(serviceNameInBrackets);
        if (serviceUrl == null || serviceUrl.equals("") || serviceUrl.equals(serviceNameInBrackets)) {
            serviceUrl = getConfig().getParam(serviceName, "http://localhost:8080/" + serviceName + "/" + serviceName);
        }

        String serviceParams = util.createXml(params);
        Map<String, Object> result = null;
        try {
            result = util.doURL(serviceUrl, methodName, serviceParams, null);
        } catch (SoftServiceErrorException ex) {
            logger.error("Error calling external service: " + ex.getMessage(), ex);
            logger.error(String.format("External  service call params: url=%s, command=%s, params:%n%s", serviceUrl, methodName, params));
            result = new HashMap<>();
            result.put("Status", "ERROR");
            result.put("Error", ex.toString());
            result.put("FaultMessage", ex.getFaultMessage());
        } catch (Exception ex) {
            logger.error("Error calling external service: " + ex.getMessage(), ex);
            logger.error(String.format("External  service call params: url=%s, command=%s, params:%n%s", serviceUrl, methodName, params));
            result = new HashMap<>();
            result.put("Status", "ERROR");
            result.put("Error", ex.toString());
        }

        return result;
    }

    public Map<String, Object> callExternalService(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        return this.callExternalServiceImpl(serviceName, methodName, params, false, login, password);
    }

    public Map<String, Object> callExternalServiceAsync(String serviceName, String methodName, Map<String, Object> params, String login, String password) throws Exception {
        return this.callExternalServiceImpl(serviceName, methodName, params, true, login, password);
    }
}
