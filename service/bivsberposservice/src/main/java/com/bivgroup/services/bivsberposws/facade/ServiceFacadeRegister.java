package com.bivgroup.services.bivsberposws.facade;

import java.util.HashSet;
import java.util.Set;
import ru.diasoft.services.inscore.facade.FacadeRegister;
import com.bivgroup.services.bivsberposws.facade.pos.InsFacadeRegister;
import com.bivgroup.services.bivsberposws.facade.pos.custom.AngularCalculatorCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.AngularClientActionLogFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.AngularContractCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.AngularSisContractCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.AngularHandbookCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.AngularMortgageContractCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.AngularReportCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.BorderoCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.EMailSMSSenderCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.FlexteraContractCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.InsClientActionLogCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.InsParticipantBinFileCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.InsPromocodesCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.InsSharesCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.LossesAddressCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.LossesProductBinaryDocumentCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.LossesProductReportCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.LossesRequestCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.LossesRequestDocCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.LossesRequestOrgStructCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.PaymentCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.StringEncriptorCustomFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.integration.CIBContractIntegrationFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.integration.ContractIntegrationFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.integration.HIBContractIntegrationFacade;
import com.bivgroup.services.bivsberposws.facade.pos.custom.integration.TravelContractIntegrationFacade;

public class ServiceFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(InsFacadeRegister.class);
        classes.add(HealthFacade.class);
        classes.add(LossesRequestCustomFacade.class);
        classes.add(LossesRequestDocCustomFacade.class);
        classes.add(LossesProductBinaryDocumentCustomFacade.class);
        classes.add(LossesRequestCustomFacade.class);
        classes.add(LossesProductReportCustomFacade.class);
        classes.add(LossesRequestOrgStructCustomFacade.class);
        classes.add(LossesAddressCustomFacade.class);
        classes.add(AngularSisContractCustomFacade.class);
        classes.add(AngularContractCustomFacade.class);
        classes.add(AngularReportCustomFacade.class);
        classes.add(AngularCalculatorCustomFacade.class);
        classes.add(AngularHandbookCustomFacade.class);
        classes.add(BorderoCustomFacade.class);
        classes.add(AngularClientActionLogFacade.class);
        classes.add(HIBContractIntegrationFacade.class);
        classes.add(CIBContractIntegrationFacade.class);
        classes.add(TravelContractIntegrationFacade.class);
        classes.add(ContractIntegrationFacade.class);
        classes.add(InsClientActionLogCustomFacade.class);
        classes.add(StringEncriptorCustomFacade.class);
        classes.add(PaymentCustomFacade.class);
        classes.add(InsParticipantBinFileCustomFacade.class);
        classes.add(EMailSMSSenderCustomFacade.class);
        classes.add(AngularMortgageContractCustomFacade.class);
        classes.add(InsSharesCustomFacade.class);
        classes.add(InsPromocodesCustomFacade.class);
        classes.add(FlexteraContractCustomFacade.class);

        return classes;
    }
}
