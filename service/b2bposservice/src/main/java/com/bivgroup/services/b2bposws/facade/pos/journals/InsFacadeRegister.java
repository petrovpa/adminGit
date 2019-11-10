package com.bivgroup.services.b2bposws.facade.pos.journals;
import java.util.HashSet;
import java.util.Set;

import ru.diasoft.services.inscore.facade.FacadeRegister;


public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(B2B_JournalButtonFacade.class);
        classes.add(B2B_Handbook_ImplementationFacade.class);
        classes.add(B2B_JournalFacade.class);
        classes.add(B2B_JournalParamFacade.class);
        classes.add(B2B_Handbook_OwnerFacade.class);
        classes.add(B2B_KindHandbookFacade.class);
        classes.add(B2B_JournalParamDataTypeFacade.class);
        return classes;
    }
}
