package com.bivgroup.services.b2bposws.facade.pos.additionalAgreements;
import java.util.HashSet;
import java.util.Set;

import ru.diasoft.services.inscore.facade.FacadeRegister;


public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(B2BAddAgrFacade.class);
        classes.add(B2bAddAgrCntFacade.class);
        classes.add(B2BAddAgrDocFacade.class);
        classes.add(B2BAddAgrCauseFacade.class);
        classes.add(PDDeclarationDocFacade.class);
        return classes;
    }
}
