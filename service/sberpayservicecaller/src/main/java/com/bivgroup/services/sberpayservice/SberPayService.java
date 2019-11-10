/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.sberpayservice;

import org.apache.log4j.Logger;
import ru.bpc.phoenix.web.api.merchant.soap.MerchantService;
import ru.bpc.phoenix.web.api.merchant.soap.MerchantServiceImplService;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.external.ExternalService;
import ru.paymentgate.engine.webservices.merchant.*;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.diasoft.services.inscore.system.WsConstants.RETURN_AS_HASH_MAP;

//import ru.paymentgate.engine.webservices.merchant.PageViewEnum;

/**
 * @author mvolkov
 */
public class SberPayService {

    public static final String SBER_PAY_SERVICE_LOGIN = "SBERPAYSERVICELOGIN";
    public static final String SBER_PAY_SERVICE_PASSWORD = "SBERPAYSERVICEPASSWORD";
    public static final String SBER_PAY_SERVICE_LOCATION = "SBERPAYSERVICELOCATION";
    public static final String SBER_PAY_SERVICE_CLIENTID = "SBERPAYSERVICECLIENTID";
    public static final String SBER_PAY_SERVICE_SESSIONTIMEOUT = "SBERPAYSERVICESESSIONTIMEOUT";

    public static final String ALT_SBER_PAY_SERVICE_PRODS = "ALTSBERPAYSERVICEPRODS";
    public static final String ALT_SBER_PAY_SERVICE_LOGIN = "ALTSBERPAYSERVICELOGIN";
    public static final String ALT_SBER_PAY_SERVICE_PASSWORD = "ALTSBERPAYSERVICEPASSWORD";
    public static final String ALT_SBER_PAY_SERVICE_LOCATION = "ALTSBERPAYSERVICELOCATION";
    public static final String ALT_SBER_PAY_SERVICE_CLIENTID = "ALTSBERPAYSERVICECLIENTID";
    public static final String ALT_SBER_PAY_SERVICE_SESSIONTIMEOUT = "ALTSBERPAYSERVICESESSIONTIMEOUT";

    public static final String FORMURL_PARAM_NAME = "FORMURL";
    public static final String ORDERID_PARAM_NAME = "ORDERID";
    public static final String ERRORMESSAGE_PARAM_NAME = "ERRORMESSAGE";
    public static final String ERRORCODE_PARAM_NAME = "ERRORCODE";

    // параметры ответа для расширенного запроса статуса
    public static final String ORDERSTATUS_PARAM_NAME = "ORDERSTATUS";
    public static final String ORDERNUMBER_PARAM_NAME = "ORDERNUMBER";
    // это так же может быть код транзакции, который нужен для обмена в 1с.
    public static final String REFERENCENUMBER_PARAM_NAME = "REFERENCENUMBER";
    public static final String DATE_PARAM_NAME = "DATE";
    public static final String ACTIONCODE_PARAM_NAME = "ACTIONCODE";

    private Logger logger = Logger.getLogger(SberPayService.class);

    private String moduleName = null;
    private Boolean useAltService = false;
    /**
     * ИД конфигурации продукта - для получение параметров продукта (из B2B_PRODDEFVAL), отвечающих за настройки работы с мерчантом
     */
    private Long prodConfID = null;

    private String dsLogin = null;
    private String dsPassword = null;

    ExternalService externalService = ExternalService.createInstance();

    public SberPayService(String moduleName, Long prodConfID, String dsLogin, String dsPassword) throws SberPayServiceException {
        if ((moduleName != null) && !moduleName.isEmpty()) {
            this.moduleName = moduleName;
            if (prodConfID != null) {
                this.prodConfID = prodConfID;
                if ((dsLogin == null) || (dsLogin.isEmpty()) || (dsPassword == null) || (dsPassword.isEmpty())) {
                    throw new SberPayServiceException("SberPayService: ds login and/or password is null or empty");
                } else {
                    this.dsLogin = dsLogin;
                    this.dsPassword = dsPassword;
                }
            } else {
                throw new SberPayServiceException("SberPayService: prodConfID is null");
            }
        } else {
            throw new SberPayServiceException("SberPayService: Module name is null or empty");
        }
    }

    public SberPayService(String moduleName, Boolean useAltService) throws SberPayServiceException {
        if ((moduleName != null) && !moduleName.isEmpty()) {
            this.moduleName = moduleName;
            if (useAltService != null) {
                this.useAltService = useAltService;
            } else {
                throw new SberPayServiceException("SberPayService:useAltService is null");
            }
        } else {
            throw new SberPayServiceException("SberPayService:Module name is null or empty");
        }
    }

    public SberPayService(String moduleName) throws SberPayServiceException {
        if ((moduleName != null) && !moduleName.isEmpty()) {
            this.moduleName = moduleName;
        } else {
            throw new SberPayServiceException("SberPayService:Module name is null or empty");
        }
    }

    protected String getStringParam(Object bean) {
        if (bean == null) {
            return "";
        } else if (bean instanceof Double) {
            DecimalFormat df = new DecimalFormat("0");
            df.setMaximumFractionDigits(2);
            return df.format(Double.valueOf(bean.toString()).doubleValue());
        } else {
            return bean.toString();
        }
    }

    protected String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = "";
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }

    // аналог getStringParam, но с протоколировнием полученного значения
    protected String getStringParamLogged(Map<String, Object> map, String keyName) {
        String paramValue = getStringParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    private String getProdParam(String name) {
        String value;
        if (prodConfID != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("PRODCONFID", prodConfID);
            params.put("NAME", name);
            params.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> result = callExternalService("b2bposws", "dsB2BProductDefaultValueBrowseListByParam", params);
            value = getStringParamLogged(result, "VALUE");
        } else {
            value = "";
        }
        return value;
    }

    private String getProdOrConfigParam(String name, String defaultValue) {
        String value = getProdParam(name);
        if (value.isEmpty()) {
            value = Config.getConfig(moduleName).getParam(name, defaultValue);
        }
        return value;
    }

    private String base64Decode(String value) {
        return new String(javax.xml.bind.DatatypeConverter.parseBase64Binary(value));
    }

    private Map<String, Object> callExternalService(String serviceName, String methodName, Map<String, Object> params) {
        Map<String, Object> callResult;
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        logger.debug(String.format("Calling external service [%s] %s with params: ", serviceName, methodName) + params);
        try {
            callResult = externalService.callExternalService(serviceName, methodName, params, dsLogin, dsPassword);
        } catch (Exception ex) {
            logger.error(String.format("Exception during external service call: ", serviceName, methodName, params.toString()), ex);
            callResult = null;
        }
        return callResult;
    }

    private String getSberPayServiceLogin() {
        String loginDefault = "sberbankins-api";
        String login;
        if (prodConfID != null) {
            login = getProdOrConfigParam(SBER_PAY_SERVICE_LOGIN, loginDefault);
        } else {
            if (this.useAltService) {
                login = Config.getConfig(moduleName).getParam(ALT_SBER_PAY_SERVICE_LOGIN, loginDefault);
            } else {
                login = Config.getConfig(moduleName).getParam(SBER_PAY_SERVICE_LOGIN, loginDefault);
            }
        }
        logger.debug("SberPayService: getSberPayServiceLogin returned result = " + login);
        return login;
    }

    private String getSberPayServicePassword() {
        String passwordEncodedDefault = "sberbankins";
        String passwordEncoded;
        if (prodConfID != null) {
            passwordEncoded = getProdOrConfigParam(SBER_PAY_SERVICE_PASSWORD, passwordEncodedDefault);
        } else {
            if (this.useAltService) {
                passwordEncoded = Config.getConfig(moduleName).getParam(ALT_SBER_PAY_SERVICE_PASSWORD, passwordEncodedDefault);
            } else {
                passwordEncoded = Config.getConfig(moduleName).getParam(SBER_PAY_SERVICE_PASSWORD, passwordEncodedDefault);
            }
        }
        String password = base64Decode(passwordEncoded);
        //logger.debug("SberPayService: getSberPayServicePassword returned result = " + password);
        logger.debug("SberPayService: getSberPayServicePassword returned some result, but logging passwords is bad");
        return password;
    }

    private String getSberPayServiceLocation() {
        String locationDefault = "https://3dsec.sberbank.ru/payment/webservices/merchant-ws";
        String location;
        if (prodConfID != null) {
            location = getProdOrConfigParam(SBER_PAY_SERVICE_LOCATION, locationDefault);
        } else {
            if (this.useAltService) {
                location = Config.getConfig(moduleName).getParam(ALT_SBER_PAY_SERVICE_LOCATION, locationDefault);
            } else {
                location = Config.getConfig(moduleName).getParam(SBER_PAY_SERVICE_LOCATION, locationDefault);
            }
        }
        logger.debug("SberPayService: getSberPayServiceLocation returned result = " + location);
        return location;
    }

    private String getSberPayServiceClientId() {
        String clientIDDefault = "sberbankins";
        String clientID;
        if (prodConfID != null) {
            clientID = getProdOrConfigParam(SBER_PAY_SERVICE_CLIENTID, clientIDDefault);
        } else {
            if (this.useAltService) {
                clientID = Config.getConfig(moduleName).getParam(ALT_SBER_PAY_SERVICE_CLIENTID, clientIDDefault);
            } else {
                clientID = Config.getConfig(moduleName).getParam(SBER_PAY_SERVICE_CLIENTID, clientIDDefault);
            }
        }
        logger.debug("SberPayService: getSberPayServiceClientId returned result = " + clientID);
        return clientID;
    }

    private Integer getSberPayServiceSessionTimeout() {
        Integer timeoutDefault = 1200;
        String timeoutDefaultStr = timeoutDefault.toString();
        String timeoutStr;
        if (prodConfID != null) {
            timeoutStr = getProdOrConfigParam(SBER_PAY_SERVICE_SESSIONTIMEOUT, timeoutDefaultStr);
        } else {
            timeoutStr = Config.getConfig(moduleName).getParam(SBER_PAY_SERVICE_SESSIONTIMEOUT, timeoutDefaultStr);
        }
        Integer timeout;
        try {
            timeout = Integer.valueOf(timeoutStr);
        } catch (NumberFormatException ex) {
            timeout = timeoutDefault;
        }
        logger.debug("SberPayService: getSberPayServiceSessionTimeout returned result = " + timeout);
        return timeout;
    }

    protected void processWsSecurity(BindingProvider proxy) {
        //if (this.isWsSecurityEnabled(serviceName)) {
        List<Handler> handlerChain
                = ((BindingProvider) proxy).getBinding().getHandlerChain();
        handlerChain.add(new UserNameTokenHandler(this.getSberPayServiceLogin(), this.getSberPayServicePassword()));
        ((BindingProvider) proxy).getBinding().setHandlerChain(handlerChain);
        //}
    }

    private RegisterOrderResponse callRegisterOrderService(OrderParams params) {
        URL url = this.getClass().getClassLoader().getResource("sberpayservice.wsdl");
        MerchantServiceImplService service = new MerchantServiceImplService(url);
        MerchantService proxy = service.getMerchantServiceImplPort();
        Map<String, Object> ctxt = ((BindingProvider) proxy).getRequestContext();

        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getSberPayServiceLocation());
        this.processWsSecurity((BindingProvider) proxy);
        RegisterOrderResponse result = proxy.registerOrder(params);
        return result;
    }

    private OrderResult callReverseOrderService(ReversalOrderParams params) {
        URL url = this.getClass().getClassLoader().getResource("sberpayservice.wsdl");
        MerchantServiceImplService service = new MerchantServiceImplService(url);
        MerchantService proxy = service.getMerchantServiceImplPort();
        Map<String, Object> ctxt = ((BindingProvider) proxy).getRequestContext();

        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getSberPayServiceLocation());
        this.processWsSecurity((BindingProvider) proxy);
        OrderResult result = proxy.reverseOrder(params);
        return result;
    }

    private GetOrderStatusExtendedResponse callGetOrderStatusExtended(GetOrderStatusExtendedRequest params) {
        URL url = this.getClass().getClassLoader().getResource("sberpayservice.wsdl");
        MerchantServiceImplService service = new MerchantServiceImplService(url);
        MerchantService proxy = service.getMerchantServiceImplPort();
        Map<String, Object> ctxt = ((BindingProvider) proxy).getRequestContext();

        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getSberPayServiceLocation());
        this.processWsSecurity((BindingProvider) proxy);
        GetOrderStatusExtendedResponse result = proxy.getOrderStatusExtended(params);
        return result;
    }

    public Map<String, Object> sberPayServiceRegisterOrder(String merchantOrderNumber,
                                                           String description, Long amount, boolean isMobile,
                                                           String returnSuccessURL, String returnFailURL) {
        return this.sberPayServiceRegisterOrder(merchantOrderNumber, description, amount, isMobile, returnSuccessURL, returnFailURL, "");
    }

    public Map<String, Object> sberPayServiceRegisterOrder(String merchantOrderNumber,
                                                           String description, Long amount, boolean isMobile,
                                                           String returnSuccessURL, String returnFailURL, String email) {

        logger.debug("SberPayServiceRegisterOrder method called");
        logger.debug(String.format("SberPayServiceRegisterOrder params"
                        + " (merchantOrderNumber = [%s], description = [%s], amount = [%d],"
                        + " isMobile = [%b], returnSuccessURL = [%s], returnFailURL = [%s] )",
                merchantOrderNumber, description, amount, isMobile, returnSuccessURL, returnFailURL));

        OrderParams params = new OrderParams();
        params.setSessionTimeoutSecs(this.getSberPayServiceSessionTimeout());
        params.setMerchantOrderNumber(merchantOrderNumber);
        params.setDescription(description);
        params.setAmount(amount);
        params.setClientId(this.getSberPayServiceClientId());
        params.setPageView(isMobile ? "MOBILE" : "DESKTOP");
        params.setReturnUrl(returnSuccessURL);
        params.setFailUrl(returnFailURL);
        if (email != null) {
            if (!email.isEmpty()) {
                List<ServiceParam> servParams = params.getParams();
                ServiceParam sp = new ServiceParam();
                sp.setName("email");
                sp.setValue(email);
                servParams.add(sp);
                //params.setEmail(email);
            }
        }
        RegisterOrderResponse registerOrderResponse = this.callRegisterOrderService(params);
        logger.debug("registerOrder method called");
        logger.debug(String.format("registerOrder result is (formURL = [%s], orderId = [%s]",
                registerOrderResponse.getFormUrl(), registerOrderResponse.getOrderId()));

        Map<String, Object> result = new HashMap<String, Object>();
        result.put(FORMURL_PARAM_NAME, registerOrderResponse.getFormUrl());
        result.put(ORDERID_PARAM_NAME, registerOrderResponse.getOrderId());

        result.put(ERRORCODE_PARAM_NAME, registerOrderResponse.getErrorCode());
        result.put(ERRORMESSAGE_PARAM_NAME, registerOrderResponse.getErrorMessage());

        return result;
    }

    public Map<String, Object> sberPayServiceGetOrderStatusExtended(String merchantOrderNumber) {
        logger.debug("SberPayServiceGetOrderStatusExtended called");
        logger.debug(String.format("SberPayServiceGetOrderStatusExtended params: merchantOrderNumber = [%s]", merchantOrderNumber));
        GetOrderStatusExtendedRequest request = new GetOrderStatusExtendedRequest();
        request.setMerchantOrderNumber(merchantOrderNumber);
        logger.debug("call method getOrderStatusExtended");
        GetOrderStatusExtendedResponse resStatus = this.callGetOrderStatusExtended(request);
        logger.debug("getOrderStatusExtended method called");
        logger.debug(String.format("getOrderStatusExtended result is (orderStatus = [%d], actionCode = [%d],"
                + " referenceNumber(authRefNum) = [%s]", resStatus.getOrderStatus(), resStatus.getActionCode(), resStatus.getAuthRefNum()));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(ORDERSTATUS_PARAM_NAME, resStatus.getOrderStatus());
        result.put(ORDERNUMBER_PARAM_NAME, resStatus.getOrderNumber());
        result.put(ACTIONCODE_PARAM_NAME, resStatus.getActionCode());
        result.put(REFERENCENUMBER_PARAM_NAME, resStatus.getAuthRefNum());
        result.put(DATE_PARAM_NAME, resStatus.getDate());

        String orderId = "";
        for (ServiceParam attr : resStatus.getAttributes()) {
            if ("mdOrder".equals(attr.getName())) {
                orderId = attr.getValue();
                break;
            }
        }

        result.put(ORDERID_PARAM_NAME, orderId);
        return result;
    }

    public Map<String, Object> sberPayServiceReverseOrder(String orderId) {
        logger.debug("sberPayServiceReverseOrder called");
        logger.debug(String.format("sberPayServiceReverseOrder params: orderId = [%s]", orderId));
        ReversalOrderParams request = new ReversalOrderParams();

        request.setOrderId(orderId);
        logger.debug("call method callReverseOrderService");
        OrderResult resStatus = this.callReverseOrderService(request);
        logger.debug("callReverseOrderService method called");
        logger.debug(String.format("callReverseOrderService result is (orderStatus = [%d], actionCode = [%s],",
                resStatus.getErrorCode(), resStatus.getErrorMessage()));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(ERRORCODE_PARAM_NAME, resStatus.getErrorCode());
        result.put(ERRORMESSAGE_PARAM_NAME, resStatus.getErrorMessage());
        return result;
    }
}
