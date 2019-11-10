package com.bivgroup.services.paws.facade.pa;
import java.util.HashSet;
import java.util.Set;

import ru.diasoft.services.inscore.facade.FacadeRegister;


public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(PaAppealFacade.class);
        classes.add(PaContractFacade.class);
        classes.add(PaGroupFacade.class);
        classes.add(PaAppealHistFacade.class);
        classes.add(PaUserFacade.class);
        return classes;
    }
}
