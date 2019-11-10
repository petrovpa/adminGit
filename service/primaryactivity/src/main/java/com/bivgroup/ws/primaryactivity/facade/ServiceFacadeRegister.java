package com.bivgroup.ws.primaryactivity.facade;

import com.bivgroup.ws.primaryactivity.facade.pos.contract.custom.B2BPrimaryActivityContractAgentReportContentCustomFacade;
import com.bivgroup.ws.primaryactivity.facade.pos.contract.custom.B2BPrimaryActivityContractAgentReportCustomFacade;
import com.bivgroup.ws.primaryactivity.facade.pos.contract.custom.B2BPrimaryActivityContractCustomFacade;
import com.bivgroup.ws.primaryactivity.facade.pos.custom.export.B2BPrimaryActivityExportCustomFacade;
import com.bivgroup.ws.primaryactivity.facade.pos.product.custom.B2BProductStructureRiskCustomFacade;
import java.util.HashSet;
import java.util.Set;
import ru.diasoft.services.inscore.facade.FacadeRegister;

public class ServiceFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();

        // фасад для договоров по основной деятельности
        classes.add(B2BPrimaryActivityBaseFacade.class);
        classes.add(B2BPrimaryActivityContractCustomFacade.class);
        // фасады по работе с продуктами для договоров по основной деятельности
        classes.add(B2BProductStructureRiskCustomFacade.class);
        // фасады для отчетов агента
        classes.add(B2BPrimaryActivityContractAgentReportCustomFacade.class); // отчет
        classes.add(B2BPrimaryActivityContractAgentReportContentCustomFacade.class); // содержимое отчета
        // фасад для экспорта сведений отчетов агента
        classes.add(B2BPrimaryActivityExportCustomFacade.class); // отчет

        return classes;
    }
}
