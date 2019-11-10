package com.bivgroup.imports;
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
 * Generate by core-dictionary-plugin for module Imports
 */
public class Imports extends HierarchyDAO implements ExtendGenericDAO {

    @Override
    public String getModuleName() {
    return "com.bivgroup.imports";
    }

    public Imports(OrmProvider provider) {
        super(provider);
    }

    public Imports(DataSource dataSource){
        super(new OrmProviderImpl(dataSource, "Imports"));
    }
    public Imports(Session session){
        super(session);
    }

    public static ExtendGenericDAO newInstance(OrmProvider provider) throws AspectException {
            Imports tmp = new Imports(provider);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Imports.class});
    }

    public static ExtendGenericDAO newInstance(DataSource dataSource) throws AspectException {
            Imports tmp = new Imports(dataSource);
            return (ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Imports.class});
    }

    public static ExtendGenericDAO newInstance(Session session) throws AspectException {
            Imports tmp = new Imports(session);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Imports.class});
    }

}
