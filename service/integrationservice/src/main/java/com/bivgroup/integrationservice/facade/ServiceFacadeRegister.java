package com.bivgroup.integrationservice.facade;

import com.bivgroup.integrationservice.facade.integration.*;
import ru.diasoft.services.inscore.facade.FacadeRegister;

import java.util.HashSet;
import java.util.Set;

public class ServiceFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<?>> getFacadeClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(ContractProcessorFacade.class);
        classes.add(LifeContractIntegrationFacade.class);
        classes.add(LifeContractCutGetterFacade.class);
        classes.add(LifeContractGetterFacade.class);
        classes.add(LifeLKUserRegistratorFacade.class);
        classes.add(LifeClaimCutGetterFacade.class);
        classes.add(LifeClaimGetterFacade.class);
        classes.add(LifeClaimPutterFacade.class);
        classes.add(LifeChangePutterFacade.class);
        classes.add(LifeChangeCutGetterFacade.class);
        classes.add(LifeChangeGetterFacade.class);
        classes.add(LifeClaimFilePutterFacade.class);
        classes.add(LifeDeletedGetterFacade.class);

        return classes;
    }
}
