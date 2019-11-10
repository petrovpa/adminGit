package com.bivgroup.services.b2bposws.facade.pos.sal;
import java.util.HashSet;
import java.util.Set;

import ru.diasoft.services.inscore.facade.FacadeRegister;


public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(SAL_Journal_ContextFacade.class);
        classes.add(SAL_InvalidPassportsNodeFacade.class);
        classes.add(SAL_JournalFacade.class);
        classes.add(SAL_InvalidPassports_TmpFacade.class);
        classes.add(SAL_KindFlagEventFacade.class);
        classes.add(SAL_TerroristsNodeFacade.class);
        classes.add(SAL_Terrorists_LogFacade.class);
        classes.add(SAL_KindContextSourceFacade.class);
        classes.add(SAL_KindEventFacade.class);
        classes.add(SAL_InvalidPassportsFacade.class);
        classes.add(SAL_Terrorists_TmpFacade.class);
        classes.add(SAL_Journal_FlagFacade.class);
        classes.add(SAL_InvalidPassports_LogFacade.class);
        classes.add(SAL_KindSourceFacade.class);
        classes.add(SAL_TerroristsFacade.class);
        return classes;
    }
}
