package com.bivgroup.services.b2bposws.facade.pos.mappers;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;

import java.util.List;
import java.util.Map;

/**
 * Фабрика для создания объектов маппинга (универсальная)
 */
public class JXPathContextFactory extends AbstractFactory {

    private Map<String, Class> objectClasses;

    public JXPathContextFactory(Map<String, Class> objectClasses) {
        super();
        this.objectClasses = objectClasses;
    }

    public boolean createObject(JXPathContext context, Pointer pointer,
                                Object parent, String name, int index){
        if (objectClasses.get(name) != null) {
            if (parent instanceof Map) {
                try {
                    ((Map)parent).put(name, objectClasses.get(name).newInstance());
                    return true;
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                if (parent instanceof List) {
                    try {
                        ((List)parent).add(objectClasses.get(name).newInstance());
                        return true;
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}
