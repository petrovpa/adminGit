package com.bivgroup.services.paws.facade;

import com.bivgroup.services.paws.facade.pa.InsFacadeRegister;
import com.bivgroup.services.paws.facade.pa.custom.ContractCustomFacade;
import com.bivgroup.services.paws.facade.pa.custom.PASessionCustomFacade;
import com.bivgroup.services.paws.facade.pa.custom.PaAppealCustomFacade;
import com.bivgroup.services.paws.facade.pa.custom.PaAppealHistCustomFacade;
import java.util.HashSet;
import java.util.Set;
import ru.diasoft.services.inscore.facade.FacadeRegister;

public class ServiceFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();

        classes.add(InsFacadeRegister.class);
        classes.add(HealthFacade.class);
        classes.add(ContractCustomFacade.class);
        classes.add(PASessionCustomFacade.class);
        classes.add(PaAppealCustomFacade.class);
        classes.add(PaAppealHistCustomFacade.class);
        return classes;
    }
}
