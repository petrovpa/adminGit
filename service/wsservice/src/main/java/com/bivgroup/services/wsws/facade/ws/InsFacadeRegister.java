package com.bivgroup.services.wsws.facade.ws;
import java.util.HashSet;
import java.util.Set;

import ru.diasoft.services.inscore.facade.FacadeRegister;


public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(WSUserFacade.class);
        classes.add(WSTemplateFacade.class);
        classes.add(WSMethodTemplateFacade.class);
        classes.add(WSUserTemplateFacade.class);
        classes.add(WSServiceFacade.class);
        classes.add(WSUserMethodFacade.class);
        classes.add(WSTemplateProductFacade.class);
        classes.add(WSMethodFacade.class);
        classes.add(WSAuthMethodFacade.class);
        return classes;
    }
}
