package com.bivgroup.crm;
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
 * Generate by core-dictionary-plugin for module Crm
 */
public class Crm extends HierarchyDAO implements ExtendGenericDAO {

    @Override
    public String getModuleName() {
    return "com.bivgroup.crm";
    }

    public Crm(OrmProvider provider) {
        super(provider);
    }

    public Crm(DataSource dataSource){
        super(new OrmProviderImpl(dataSource, "Crm"));
    }
    public Crm(Session session){
        super(session);
    }

    public static ExtendGenericDAO newInstance(OrmProvider provider) throws AspectException {
            Crm tmp = new Crm(provider);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Crm.class});
    }

    public static ExtendGenericDAO newInstance(DataSource dataSource) throws AspectException {
            Crm tmp = new Crm(dataSource);
            return (ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Crm.class});
    }

    public static ExtendGenericDAO newInstance(Session session) throws AspectException {
            Crm tmp = new Crm(session);
            return ( ExtendGenericDAO) AspectProxy.newInstance(tmp, new Class[]{Crm.class});
    }

    public void makeTransByState(String entityClassName, Long objectId, String stateSysName, String typeSysName) throws AspectException, DictionaryException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
