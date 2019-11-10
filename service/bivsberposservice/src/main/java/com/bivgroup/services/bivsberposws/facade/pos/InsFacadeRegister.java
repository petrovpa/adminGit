package com.bivgroup.services.bivsberposws.facade.pos;
import java.util.HashSet;
import java.util.Set;

import ru.diasoft.services.inscore.facade.FacadeRegister;


public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(LossesReportFacade.class);
        classes.add(LossesBinaryDocumentFacade.class);
        classes.add(LossesProductReportFacade.class);
        classes.add(LossesBinfileFacade.class);
        classes.add(LossesRequestOrgStructFacade.class);
        classes.add(LossesContrFacade.class);
        classes.add(LossesPersonFacade.class);
        classes.add(LossesRequestOrgHistFacade.class);
        classes.add(LossesProductBinaryDocumentFacade.class);
        classes.add(LossesRequestFacade.class);
        classes.add(LossesRequestNodeFacade.class);
        classes.add(LossesRequestDocFacade.class);
        classes.add(LossesChatsFacade.class);
        classes.add(LossesProductFacade.class);
        classes.add(LossesAddressFacade.class);
        classes.add(InsClientActionLogFacade.class);
        classes.add(InsObjFacade.class);
        classes.add(InsObjFlatFacade.class);
        classes.add(InsObjGOFacade.class);
        classes.add(InsObjHouseFacade.class);
        classes.add(InsObjMovableFacade.class);
        classes.add(InsObjOtherFacade.class);
        classes.add(InsObjSaunaFacade.class);
        classes.add(InsParticipantBinFileFacade.class);
        classes.add(InsSharesFacade.class);
        classes.add(InsPromocodesFacade.class);
        
        return classes;
    }
}
