package com.bivgroup.services.b2bposws.facade.pos.contract;
import java.util.HashSet;
import java.util.Set;

import ru.diasoft.services.inscore.facade.FacadeRegister;


public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {
        Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
        classes.add(B2BContrChildFacade.class);
        classes.add(B2BAgentCommissFacade.class);
        classes.add(B2BBankCashFlowFacade.class);
        classes.add(B2BBankPurposeDetailFacade.class);
        classes.add(B2BBankStateDocumentFacade.class);
        classes.add(B2BBankStateFacade.class);
        classes.add(B2BBankStateTemplateConditionFacade.class);
        classes.add(B2BBankStateTemplateFacade.class);
        classes.add(B2BBankStateTemplatePurposeDetailFacade.class);
        classes.add(B2BBeneficiaryFacade.class);
        classes.add(B2BContractAgentFacade.class);
        classes.add(B2BContractDiscountFacade.class);
        classes.add(B2BContractDocumentFacade.class);
        classes.add(B2BContractExtensionFacade.class);
        classes.add(B2BContractFacade.class);
        classes.add(B2BContractLobFacade.class);
        classes.add(B2BContractNodeFacade.class);
        classes.add(B2BContractObjectExtensionFacade.class);
        classes.add(B2BContractObjectFacade.class);
        classes.add(B2BContractOrgHistFacade.class);
        classes.add(B2BContractOrgStructFacade.class);
        classes.add(B2BContractParticipantRoleFacade.class);
        classes.add(B2BContractRiskExtensionFacade.class);
        classes.add(B2BContractRiskFacade.class);
        classes.add(B2BContractSectionExtensionFacade.class);
        classes.add(B2BContractSectionFacade.class);
        classes.add(B2BContractSourceParamFacade.class);
        classes.add(B2BFeedBackFacade.class);
        classes.add(B2BInsuranceObjectFacade.class);
        classes.add(B2BInsuranceObjectGroupFacade.class);
        classes.add(B2BMainActivityContractAgentCommissionFacade.class);
        classes.add(B2BMainActivityContractAgentContentFacade.class);
        classes.add(B2BMainActivityContractAgentFacade.class);
        classes.add(B2BMainActivityContractAgentReportContentFacade.class);
        classes.add(B2BMainActivityContractAgentReportFacade.class);
        classes.add(B2BMainActivityContractAgentSalesChannelFacade.class);
        classes.add(B2BMainActivityContractBaseFacade.class);
        classes.add(B2BMainActivityContractMemberFacade.class);
        classes.add(B2BMainActivityContractMemberTypeFacade.class);
        classes.add(B2BMainActivityContractNodeFacade.class);
        classes.add(B2BMainActivityContractTravelFacade.class);
        classes.add(B2BMemberFacade.class);
        classes.add(B2BPaymentFacade.class);
        classes.add(B2BPaymentFactFacade.class);
        classes.add(B2BRequestQueueFacade.class);
        classes.add(B2BRequestTypeFacade.class);
        classes.add(INSContractDiscountFacade.class);
        classes.add(SHTaskFacade.class);
        classes.add(B2BContractRedemptionFacade.class);
        classes.add(B2BContractSavingsScheduleFacade.class);
        classes.add(B2BContractPayoutScheduleFacade.class);
        return classes;
    }
}
