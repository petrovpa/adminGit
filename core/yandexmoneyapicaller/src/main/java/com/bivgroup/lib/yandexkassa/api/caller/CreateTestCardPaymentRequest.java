package com.bivgroup.lib.yandexkassa.api.caller;

import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTestCardPaymentRequest extends IdempotentRequest {
    private String value;

    private String description;

    public CreateTestCardPaymentRequest(String shopId, String secretKey, String key, String value, String description) {
        super(shopId, secretKey, key);
        this.value = value;
        this.description = description;
    }

    @Override
    public Map<String, Object> make() {
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("capture", true);
        params.put("description", this.description);
        {
            final Map<String, Object> amount = new HashMap<String, Object>();
            amount.put("value", this.value);
            amount.put("currency", "RUB");
            params.put("amount", amount);
        }
        {
            final Map<String, Object> payment = new HashMap<String, Object>();
            payment.put("type", "bank_card");
            {
                final Map<String, Object> card = new HashMap<>();
                card.put("number", "1111111111111026");
                card.put("expiry_year", "2025");
                card.put("expiry_month", "12");
                card.put("CSC", "000");
                payment.put("card", card);
            }
            params.put("payment_method_data", payment);
        }
        {
            final Map<String, Object> receipt = new HashMap<>();
            {
                final List<Map<String, Object>> items = new ArrayList<>();
                {
                    final Map<String, Object> item = new HashMap<>();
                    item.put("description", this.description);
                    item.put("quantity", "1");
                    {
                        final Map<String, Object> amount = new HashMap<>();
                        amount.put("value", this.value);
                        amount.put("currency", "RUB");
                        item.put("amount", amount);
                    }
                    item.put("vat_code", 1);
                    items.add(item);
                }
                receipt.put("items", items);
            }
            receipt.put("email", "example@mail.com");
            params.put("receipt", receipt);
        }
        {
            final Map<String, Object> confirmation = new HashMap<>();
            confirmation.put("type", "redirect");
            confirmation.put("return_url", "example.com");
            params.put("confirmation", confirmation);
        }
        final Response response = ((ResteasyWebTarget) ClientBuilder.newClient().target("https://payment.yandex.net/api/v3/payments"))
                .register(new BasicAuthentication(this.shopId, this.secretKey))
                .request()
                .header("Idempotence-Key", this.key)
                .post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));
        final int responseStatus = response.getStatus();
        final Map<String, Object> responseBody = response.readEntity(new GenericType<HashMap<String, Object>>() {});
        final Map<String, Object> result = new HashMap<String, Object>();
        if (responseStatus == 200) {
            result.put("ERRORCODE", "0");
            result.put("ERRORMESSAGE", "");
            result.put("PAYMENTID", responseBody.get("id"));
            result.put("PAYMENTSTATUS", responseBody.get("status"));
        } else {
            result.put("ERRORCODE", String.valueOf(responseStatus));
            result.put("ERRORMESSAGE", responseBody.get("description"));
        }
        return result;
    }
}
