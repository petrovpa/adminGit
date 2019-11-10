package com.bivgroup.services.b2bposws.facade.pos.product.cdi;

import java.util.Map;

/**
 *
 * @author aklunok
 */
public interface ProductManager {

    public Map<String, Object> getProductBySysName(String sysName);

    public Map<String, Object> getProductByVerId(Long verId);

    public void addProduct(String sysName, Long verId, Map<String, Object> productMap);
}
