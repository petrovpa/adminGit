package com.bivgroup.lib.yandexkassa.api.caller;

public abstract class IdempotentRequest extends AuthRequest {
    protected String key;

    public IdempotentRequest(String shopId, String secretKey, String key) {
        super(shopId, secretKey);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
