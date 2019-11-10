package com.bivgroup.services.b2bposws.facade.pos.other;
import com.bivgroup.services.b2bposws.facade.pos.other.custom.B2BUserParamCustomFacade;
import java.util.HashSet;
import java.util.Set;

import ru.diasoft.services.inscore.facade.FacadeRegister;


public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(B2BMenuTypeFacade.class);
        classes.add(B2BCalendarFacade.class);
        classes.add(B2BExportDataContentFacade.class);
        classes.add(B2BAddressFacade.class);
        classes.add(B2BMenuFacade.class);
        classes.add(B2BMenuorgstructFacade.class);
        classes.add(B2BExportDataFacade.class);
        classes.add(B2BExportDataTypeFacade.class);
        classes.add(B2BUserParamFacade.class);
        classes.add(B2BReferralFacade.class);
        classes.add(B2BCountryFacade.class);
        classes.add(B2BExportDataTemplateFacade.class);
        classes.add(B2BUserParamCustomFacade.class);
        classes.add(B2BExportDataOrgStructFacade.class);
        classes.add(B2BBinDocTypeFacade.class);
        return classes;
    }
}
