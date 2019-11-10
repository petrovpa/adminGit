package com.bivgroup.services.b2bposws.facade.pos.uniopenapi;

import ru.diasoft.services.inscore.facade.FacadeRegister;

import java.util.HashSet;
import java.util.Set;

public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(UOAContractCustomFacade.class);
        classes.add(UOAContractDocumentCustomFacade.class);
        classes.add(UOAPaymentCustomFacade.class);
        classes.add(OpenApiDataProviderFacade.class);
        return classes;
    }
}

