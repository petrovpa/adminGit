package com.bivgroup.lib.yandexkassa.api.caller;

public abstract class AuthRequest extends AbstractRequest {
    protected String shopId;

    protected String secretKey;

    public AuthRequest(String shopId, String secretKey) {
        this.shopId = shopId;
        this.secretKey = secretKey;
    }

    public String getShopId() {
        return shopId;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
