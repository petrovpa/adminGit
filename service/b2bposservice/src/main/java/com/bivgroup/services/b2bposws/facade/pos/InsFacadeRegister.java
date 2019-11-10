package com.bivgroup.services.b2bposws.facade.pos;

import com.bivgroup.services.b2bposws.facade.pos.other.MessageChannelFacade;
import com.bivgroup.services.b2bposws.facade.pos.other.MessageCorrespondentFacade;
import com.bivgroup.services.b2bposws.facade.pos.other.MessageFacade;
import com.bivgroup.services.b2bposws.facade.pos.other.MessageRecipientFacade;
import java.util.HashSet;
import java.util.Set;

import com.bivgroup.services.b2bposws.facade.pos.subsystem.custom.B2BSubsystemProductParamCustomFacade;
import ru.diasoft.services.inscore.facade.FacadeRegister;

public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(com.bivgroup.services.b2bposws.facade.pos.contract.InsFacadeRegister.class);
        classes.add(com.bivgroup.services.b2bposws.facade.pos.product.InsFacadeRegister.class);
        classes.add(com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.InsFacadeRegister.class);
        classes.add(com.bivgroup.services.b2bposws.facade.pos.other.InsFacadeRegister.class);
        classes.add(com.bivgroup.services.b2bposws.facade.pos.journals.InsFacadeRegister.class);
        classes.add(com.bivgroup.services.b2bposws.facade.pos.sal.InsFacadeRegister.class);
        classes.add(MessageFacade.class);
        classes.add(MessageChannelFacade.class);
        classes.add(MessageCorrespondentFacade.class);
        classes.add(MessageRecipientFacade.class);
        // фасад по работе с подсистемой (B2B_SUBSYSTEM)
        classes.add(B2BSubsystemProductParamCustomFacade.class);
        return classes;
    }
}
