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

public class CreateSberbankSMSPaymentRequest extends IdempotentRequest {
    protected String phone;

    protected String value;

    protected String currency;

    protected String email;

    protected String description;

    protected Boolean useTestCardRequest = false;

    protected Boolean useLowPrice = false;

    public CreateSberbankSMSPaymentRequest(String shopId, String secretKey, String key, String phone, String value, String currency, String email, String description) {
        super(shopId, secretKey, key);
        this.phone = phone;
        this.value = value;
        this.currency = currency;
        this.email = email;
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getUseLowPrice() {
        return useLowPrice;
    }

    public void setUseLowPrice(Boolean useLowPrice) {
        this.useLowPrice = useLowPrice;
    }

    public Boolean getUseTestCardRequest() {
        return useTestCardRequest;
    }

    public void setUseTestCardRequest(Boolean useTestCardRequest) {
        this.useTestCardRequest = useTestCardRequest;
    }

    public String getValue() {
        return value;
    }

    public String getCurrency() {
        return currency;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Object> make() {
        if (this.useTestCardRequest) {
            return new CreateTestCardPaymentRequest(this.shopId, this.secretKey, this.key, this.value, this.description).make();
        }
        if (this.useLowPrice) {
            this.value = "1.0";
        }
        final Map<String, Object> params = new HashMap<>();
        {
            final Map<String, Object> amount = new HashMap<>();
            amount.put("value", this.value);
            amount.put("currency", this.currency);
            params.put("amount", amount);
        }
        params.put("description", this.description);
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
                        amount.put("currency", this.currency);
                        item.put("amount", amount);
                    }
                    item.put("vat_code", 1);
                    items.add(item);
                }
                receipt.put("items", items);
                receipt.put("email", this.email);
            }
            params.put("receipt", receipt);
        }
        {
            final Map<String, Object> payment = new HashMap<>();
            payment.put("type", "sberbank");
            payment.put("phone", this.phone);
            params.put("payment_method_data", payment);
        }
        {
            final Map<String, Object> confirmation = new HashMap<>();
            confirmation.put("type", "external");
            params.put("confirmation", confirmation);
        }
        params.put("capture", true);
        System.out.println(params);
        final Response response = ((ResteasyWebTarget) ClientBuilder.newClient().target("https://payment.yandex.net/api/v3/payments"))
                .register(new BasicAuthentication(this.shopId, this.secretKey))
                .request()
                .header("Idempotence-Key", this.key)
                .post(Entity.entity(params, MediaType.APPLICATION_JSON_TYPE));
        final int responseStatus = response.getStatus();
        final Map<String, Object> responseBody = response.readEntity(new GenericType<HashMap<String, Object>>() {});
        final Map<String, Object> result = new HashMap<>();
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
