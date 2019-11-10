package com.bivgroup.system;
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
 * Generate by core-dictionary-plugin for module System
 */
public class System extends HierarchyDAO implements ExtendGenericDAO {

    @Override
    public String getModuleName() {
    return "com.bivgroup.system";
    }

    public System(OrmProvider provider) {
        super(provider);
    }

    public System(DataSource dataSource){
        super(new OrmProviderImpl(dataSource, "System"));
    }
    public System(Session session){
        super(session);
    }

    public static ExtendGenericDAO newInstance(OrmProvider provider) throws AspectException {
            System tmp = new System(provider);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{System.class});
    }

    public static ExtendGenericDAO newInstance(DataSource dataSource) throws AspectException {
            System tmp = new System(dataSource);
            return (ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{System.class});
    }

    public static ExtendGenericDAO newInstance(Session session) throws AspectException {
            System tmp = new System(session);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{System.class});
    }

}
