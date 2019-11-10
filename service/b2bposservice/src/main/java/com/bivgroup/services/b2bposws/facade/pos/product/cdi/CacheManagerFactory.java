package com.bivgroup.services.b2bposws.facade.pos.product.cdi;

import javax.enterprise.inject.spi.Unmanaged;

/**
 *
 * @author aklunok
 */
public class CacheManagerFactory {

    public CacheManager getCacheManager(Class clazz) {
        Unmanaged unmanaged = new Unmanaged(clazz);
        Unmanaged.UnmanagedInstance inst = unmanaged.newInstance();
        return (CacheManager) inst.produce().inject().postConstruct().get();
    }
}
