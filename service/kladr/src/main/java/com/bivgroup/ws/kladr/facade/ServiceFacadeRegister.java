package com.bivgroup.ws.kladr.facade;

import com.bivgroup.ws.kladr.facade.pos.loader.custom.B2BKLADRLoaderCustomFacade;
import java.util.HashSet;
import java.util.Set;
import ru.diasoft.services.inscore.facade.FacadeRegister;

public class ServiceFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();

        // фасад по работе с импортом КЛАДР
        classes.add(B2BKLADRLoaderCustomFacade.class);

        return classes;
    }
}
