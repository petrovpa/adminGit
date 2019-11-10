package com.bivgroup.lib.yandexkassa.api.caller;

import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class CheckPaymentRequest extends AuthRequest {
    protected String paymentId;

    public CheckPaymentRequest(String shopId, String secretKey, String paymentId) {
        super(shopId, secretKey);
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    @Override
    public Map<String, Object> make() {
        final Response response = ((ResteasyWebTarget) ClientBuilder.newClient().target("https://payment.yandex.net/api/v3/payments/{payment_id}"))
                .register(new BasicAuthentication(this.shopId, this.secretKey))
                .resolveTemplate("payment_id", this.paymentId)
                .request()
                .get();
        final int responseStatus = response.getStatus();
        final Map<String, Object> responseBody = response.readEntity(new GenericType<HashMap<String, Object>>() {});
        final Map<String, Object> result = new HashMap<>();
        {
            if (responseStatus == 200) {
                result.put("ERRORCODE", "0");
                result.put("ERRORMESSAGE", "");
                result.put("PAYMENTCODE", responseBody.get("id"));
                result.put("PAYMENTSTATUS", responseBody.get("status"));
            } else {
                result.put("ERRORCODE", String.valueOf(responseStatus));
                result.put("ERRORMESSAGE", responseBody.get("description"));
            }
        }
        return result;
    }
}
