package com.bivgroup.loss;
import com.bivgroup.common.orm.OrmProviderImpl;
import com.bivgroup.common.orm.interfaces.OrmProvider;
import com.bivgroup.core.dictionary.dao.hierarchy.HierarchyDAO;
import com.bivgroup.core.aspect.proxy.AspectProxy;
import com.bivgroup.core.aspect.exceptions.AspectException;
import com.bivgroup.core.dictionary.dao.hierarchy.ExtendGenericDAO;
import com.bivgroup.core.dictionary.exceptions.DictionaryException;
import org.hibernate.Session;

import javax.sql.DataSource;

/**
 * Generate by core-dictionary-plugin for module Loss
 */
public class Loss extends HierarchyDAO implements ExtendGenericDAO {

    @Override
    public String getModuleName() {
    return "com.bivgroup.loss";
    }

    public Loss(OrmProvider provider) {
        super(provider);
    }

    public Loss(DataSource dataSource){
        super(new OrmProviderImpl(dataSource, "Loss"));
    }
    public Loss(Session session){
        super(session);
    }

    public static ExtendGenericDAO newInstance(OrmProvider provider) throws AspectException {
            Loss tmp = new Loss(provider);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Loss.class});
    }

    public static ExtendGenericDAO newInstance(DataSource dataSource) throws AspectException {
            Loss tmp = new Loss(dataSource);
            return (ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Loss.class});
    }

    public static ExtendGenericDAO newInstance(Session session) throws AspectException {
            Loss tmp = new Loss(session);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Loss.class});
    }

    
    public void makeTransByState(String entityClassName, Long objectId, String stateSysName, String typeSysName) throws AspectException, DictionaryException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
