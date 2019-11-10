package com.bivgroup.integrationservice.facade;

import ru.diasoft.services.inscore.facade.BaseFacade;

public class AbstractFacade extends BaseFacade {
    @Override
    public String getServiceName() {
        return "b2bposws";
    }
}
