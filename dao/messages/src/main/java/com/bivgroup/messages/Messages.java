package com.bivgroup.messages;
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
 * Generate by core-dictionary-plugin for module Messages
 */
public class Messages extends HierarchyDAO implements ExtendGenericDAO {

    @Override
    public String getModuleName() {
    return "com.bivgroup.messages";
    }

    public Messages(OrmProvider provider) {
        super(provider);
    }

    public Messages(DataSource dataSource){
        super(new OrmProviderImpl(dataSource, "Messages"));
    }
    public Messages(Session session){
        super(session);
    }

    public static ExtendGenericDAO newInstance(OrmProvider provider) throws AspectException {
            Messages tmp = new Messages(provider);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Messages.class});
    }

    public static ExtendGenericDAO newInstance(DataSource dataSource) throws AspectException {
            Messages tmp = new Messages(dataSource);
            return (ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Messages.class});
    }

    public static ExtendGenericDAO newInstance(Session session) throws AspectException {
            Messages tmp = new Messages(session);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Messages.class});
    }

}
