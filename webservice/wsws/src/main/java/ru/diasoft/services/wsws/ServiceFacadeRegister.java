package ru.diasoft.services.wsws;

import java.util.HashSet;
import java.util.Set;

import ru.diasoft.services.inscore.aspect.impl.auth.AuthAspect;
import ru.diasoft.services.inscore.aspect.impl.autonumber.AutoNumberAspect;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFileAspect;
import ru.diasoft.services.inscore.aspect.impl.discriminator.DiscriminatorAspect;
import ru.diasoft.services.inscore.aspect.impl.guididgen.GUIDIdGenAspect;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGenAspect;
import ru.diasoft.services.inscore.aspect.impl.join.JoinAspect;
import ru.diasoft.services.inscore.aspect.impl.ownerright.OwnerRightViewAspect;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRightsAspect;
import ru.diasoft.services.inscore.aspect.impl.state.StateAspect;
import ru.diasoft.services.inscore.aspect.impl.version.NodeVersionAspect;
import ru.diasoft.services.inscore.facade.FacadeRegister;

public class ServiceFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(AuthAspect.class);
        classes.add(StateAspect.class);
        classes.add(IdGenAspect.class);
        classes.add(AutoNumberAspect.class);
        classes.add(DiscriminatorAspect.class);
        classes.add(JoinAspect.class);
        classes.add(BinaryFileAspect.class);
        classes.add(NodeVersionAspect.class);
        classes.add(ProfileRightsAspect.class);
        classes.add(OwnerRightViewAspect.class);
        classes.add(GUIDIdGenAspect.class);
        classes.add(com.bivgroup.services.wsws.facade.ServiceFacadeRegister.class);
        return classes;
    }
}
