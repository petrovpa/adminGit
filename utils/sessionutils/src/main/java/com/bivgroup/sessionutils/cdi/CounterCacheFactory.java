package com.bivgroup.sessionutils.cdi;

import javax.enterprise.inject.spi.Unmanaged;

public class CounterCacheFactory {

    public CounterCache getCacheManager(Class clazz) {
        Unmanaged unmanaged = new Unmanaged(clazz);
        Unmanaged.UnmanagedInstance inst = unmanaged.newInstance();
        return (CounterCache) inst.produce().inject().postConstruct().get();
    }
}
