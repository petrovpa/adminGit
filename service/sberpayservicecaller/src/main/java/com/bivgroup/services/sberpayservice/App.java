package com.bivgroup.services.sberpayservice;

import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import ru.bpc.phoenix.web.api.merchant.soap.MerchantService;
import ru.bpc.phoenix.web.api.merchant.soap.MerchantServiceImplService;
import ru.paymentgate.engine.webservices.merchant.OrderParams;
//import ru.paymentgate.engine.webservices.merchant.PageViewEnum;
import ru.paymentgate.engine.webservices.merchant.RegisterOrderResponse;

/**
 * Hello world!
 *
 */
public class App {

    protected void processWsSecurity(String serviceName, BindingProvider proxy) {
        //if (this.isWsSecurityEnabled(serviceName)) {
        List<Handler> handlerChain
                = ((BindingProvider) proxy).getBinding().getHandlerChain();
        handlerChain.add(new UserNameTokenHandler("sberbankins-api", "sberbankins"));
        ((BindingProvider) proxy).getBinding().setHandlerChain(handlerChain);
        //}
    }

    private RegisterOrderResponse callRegisterOrderService(OrderParams params) {
        URL url = this.getClass().getClassLoader().getResource("sberpayservice.wsdl");
        MerchantServiceImplService service = new MerchantServiceImplService(url);
        MerchantService proxy = service.getMerchantServiceImplPort();
        Map<String, Object> ctxt = ((BindingProvider) proxy).getRequestContext();
        // Enable HTTP chunking mode, otherwise HttpURLConnection buffers
        //ctxt.put(JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8192);
        ctxt.put(BindingProvider.USERNAME_PROPERTY, "sberbankins-api");
        ctxt.put(BindingProvider.PASSWORD_PROPERTY, "sberbankins");
        ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, /*this.getServiceLocation("VTBSService")*/ "https://3dsec.sberbank.ru/payment/webservices/merchant-ws");
        this.processWsSecurity("", (BindingProvider) proxy);
        RegisterOrderResponse result = proxy.registerOrder(params);
        return result;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");
        OrderParams params = new OrderParams();
        params.setSessionTimeoutSecs(30);
        params.setMerchantOrderNumber("1");
        params.setDescription("Покупка страхового продукта \"Страхование имущества\" ");
        params.setAmount(325000L);
        params.setClientId("sberbankins");
        params.setPageView("DESKTOP");
        params.setReturnUrl("http://rybinsk.bivgroup.com:17070/webclient/html/index.html#/paymentSucces");
        params.setFailUrl("http://rybinsk.bivgroup.com:17070/webclient/html/index.html#/paymentFail");

        App app = new App();
        RegisterOrderResponse result = app.callRegisterOrderService(params);
        /*
         try { // Call Web Service Operation
         com.rsa.dkbm.ws.KbmToServiceService service = new com.rsa.dkbm.ws.KbmToServiceService();
         com.rsa.dkbm.ws.KbmToService port = service.getKbmToServicePort();
         // TODO initialize WS operation arguments here
         com.rsa.dkbm.ws.Attachment attachment = new com.rsa.dkbm.ws.Attachment();
         // TODO process result here
         com.rsa.dkbm.ws.Attachment result = port.getKbmTo(attachment);
         System.out.println("Result = "+result);
         } catch (Exception ex) {
         // TODO handle custom exceptions here
         }
         */

        System.out.println(result.getFormUrl());
    }
}
