package com.bivgroup.services.b2bposws.facade.pos.product.cdi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author aklunok
 */
@ApplicationScoped
public class ProductManagerImpl implements ProductManager {

    private final ConcurrentHashMap<Long, Object> cacheP = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> cachePSysName = new ConcurrentHashMap<>();

    @Override
    public void addProduct(String sysName, Long verId, Map<String, Object> productMap) {
        if (cachePSysName.get(sysName) != null) {
            return;
        }
        cacheP.put(verId, productMap);
        cachePSysName.put(sysName, verId);
    }

    @Override
    public Map<String, Object> getProductBySysName(String sysName) {
        Long verId = cachePSysName.get(sysName);
        if (verId == null) {
            return null;
        }
        Map<String, Object> productMap = (Map<String, Object>) cacheP.get(verId);
        return productMap;
    }

    @Override
    public Map<String, Object> getProductByVerId(Long verId) {
        Map<String, Object> productMap = (Map<String, Object>) cacheP.get(verId);
        return productMap;
    }

}
