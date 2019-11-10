package com.bivgroup.termination;
import com.bivgroup.common.orm.OrmProviderImpl;
import com.bivgroup.common.orm.interfaces.OrmProvider;
import com.bivgroup.core.dictionary.dao.hierarchy.HierarchyDAO;
import com.bivgroup.core.aspect.proxy.AspectProxy;
import com.bivgroup.core.aspect.exceptions.AspectException;
import com.bivgroup.core.dictionary.dao.hierarchy.ExtendGenericDAO;
import org.hibernate.Session;
import java.util.Map;

import javax.sql.DataSource;

/**
 * Generate by core-dictionary-plugin for module Termination
 */
public class Termination extends HierarchyDAO implements ExtendGenericDAO {

    @Override
    public String getModuleName() {
    return "com.bivgroup.termination";
    }

    public Termination(OrmProvider provider) {
        super(provider);
    }

    public Termination(DataSource dataSource){
        super(new OrmProviderImpl(dataSource, "Termination"));
    }
    public Termination(Session session){
        super(session);
    }

    public static ExtendGenericDAO newInstance(OrmProvider provider) throws AspectException {
            Termination tmp = new Termination(provider);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Termination.class});
    }

    public static ExtendGenericDAO newInstance(DataSource dataSource) throws AspectException {
            Termination tmp = new Termination(dataSource);
            return (ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Termination.class});
    }

    public static ExtendGenericDAO newInstance(Session session) throws AspectException {
            Termination tmp = new Termination(session);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Termination.class});
    }

}
