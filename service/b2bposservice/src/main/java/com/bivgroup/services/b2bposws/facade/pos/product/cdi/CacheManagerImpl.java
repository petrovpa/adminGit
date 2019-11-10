package com.bivgroup.services.b2bposws.facade.pos.product.cdi;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author aklunok
 */
public class CacheManagerImpl implements CacheManager {

    @Inject
    private ProductManager pm;

//    private ProductManager pm;
    
    @Override
    public ProductManager getProductManager() {
        return pm;
    }
    
    public CacheManagerImpl() {
    }

    @PostConstruct
    public void init() {
//        this.setProductManager(l_pm);
    }
 
//    public void setProductManager(ProductManager pm) {
//        this.pm = pm;
//    }
    
}
