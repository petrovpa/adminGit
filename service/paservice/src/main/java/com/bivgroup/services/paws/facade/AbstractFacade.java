package com.bivgroup.services.paws.facade;

import ru.diasoft.services.inscore.facade.BaseFacade;

public class AbstractFacade extends BaseFacade {

    @Override
    public String getServiceName() {
        return "paws";
    }
}
