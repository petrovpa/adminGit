package com.bivgroup.services.wsws.facade;

import java.util.HashSet;
import java.util.Set;
import ru.diasoft.services.inscore.facade.FacadeRegister;
import com.bivgroup.services.wsws.facade.ws.InsFacadeRegister;
import com.bivgroup.services.wsws.facade.ws.custom.WSTemplateCustomFacade;
import com.bivgroup.services.wsws.facade.ws.custom.WSUserAuthCustomFacade;

public class ServiceFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(InsFacadeRegister.class);
        // custom facades
        classes.add(WSUserAuthCustomFacade.class);
        classes.add(WSTemplateCustomFacade.class);
        classes.add(HealthFacade.class);
        return classes;
    }
}
