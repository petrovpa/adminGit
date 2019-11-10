package com.bivgroup.system;

import com.bivgroup.core.aspect.AspectEntityManager;
import com.bivgroup.core.dictionary.dao.hierarchy.ExtendGenericDAO;
import com.bivgroup.core.dictionary.dao.jpa.HierarchyDAO;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

public class System2 extends HierarchyDAO implements ExtendGenericDAO {

    @PersistenceContext(unitName = "System")
    private EntityManager l_em;

    @Resource
    private UserTransaction l_ut;

    @Inject
    private AspectEntityManager l_aem;

    public System2() {
    }

    @PostConstruct
    public void init() {
        this.setEm(l_em);
        this.setUt(l_ut);
        this.setAem(l_aem);
        initEntityClasses();
    }

    @PreDestroy
    public void cleanUp() {
    }

    @Override
    public void initEntityClasses() {
        mapClasses.put("KindKMImport", com.bivgroup.system.KindKMImport.class);
    }

}

