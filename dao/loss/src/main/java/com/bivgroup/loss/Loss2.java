package com.bivgroup.loss;

import com.bivgroup.core.aspect.AspectEntityManager;
import com.bivgroup.core.dictionary.dao.hierarchy.ExtendGenericDAO;
import com.bivgroup.core.dictionary.dao.jpa.HierarchyDAO;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

/**
 *
 * @author aklunok
 */
public class Loss2 extends HierarchyDAO implements ExtendGenericDAO {

    @PersistenceContext(unitName = "Loss")
    private EntityManager l_em;

    @Resource
    private UserTransaction l_ut;

    @Inject
    private AspectEntityManager l_aem;

    public Loss2() {
    }

    @PostConstruct
    public void init() {
        this.setEm(l_em);
        this.setUt(l_ut);
        this.setAem(l_aem);
        initEntityClasses();
    }

    @PreDestroy
    public void cleanUp() {
    }

    @Override
    public void initEntityClasses() {
        // crm
        mapClasses.put("LegalFormsOfBusiness", com.bivgroup.crm.LegalFormsOfBusiness.class);
        mapClasses.put("ClientProperty", com.bivgroup.crm.ClientProperty.class);
        mapClasses.put("ShareContractIns", com.bivgroup.crm.ShareContractIns.class);
        mapClasses.put("Address", com.bivgroup.crm.Address.class);
        mapClasses.put("ClientProfile", com.bivgroup.crm.ClientProfile.class);
        mapClasses.put("EClient_VER", com.bivgroup.crm.EClient_VER.class);
        mapClasses.put("ClientProfileEvent", com.bivgroup.crm.ClientProfileEvent.class);
        mapClasses.put("ClientAddress", com.bivgroup.crm.ClientAddress.class);
        mapClasses.put("SalesCampaignOffer", com.bivgroup.crm.SalesCampaignOffer.class);
        mapClasses.put("Document", com.bivgroup.crm.Document.class);
        mapClasses.put("KindCountry", com.bivgroup.crm.KindCountry.class);
        mapClasses.put("PersonContact", com.bivgroup.crm.PersonContact.class);
        mapClasses.put("PClient", com.bivgroup.crm.PClient.class);
        mapClasses.put("JClient_VER", com.bivgroup.crm.JClient_VER.class);
        mapClasses.put("ClientProfileToken", com.bivgroup.crm.ClientProfileToken.class);
        mapClasses.put("ClientContact", com.bivgroup.crm.ClientContact.class);
        mapClasses.put("ClientDocument", com.bivgroup.crm.ClientDocument.class);
        mapClasses.put("KindEventClientProfile", com.bivgroup.crm.KindEventClientProfile.class);
        mapClasses.put("Client", com.bivgroup.crm.Client.class);
        mapClasses.put("SalesCampaign", com.bivgroup.crm.SalesCampaign.class);
        mapClasses.put("KindContact", com.bivgroup.crm.KindContact.class);
        mapClasses.put("PersonAddress", com.bivgroup.crm.PersonAddress.class);
        mapClasses.put("BankDetails", com.bivgroup.crm.BankDetails.class);
        mapClasses.put("ContractDriver", com.bivgroup.crm.ContractDriver.class);
        mapClasses.put("PPersonChild", com.bivgroup.crm.PPersonChild.class);
        mapClasses.put("JPerson", com.bivgroup.crm.JPerson.class);
        mapClasses.put("EPerson", com.bivgroup.crm.EPerson.class);
        mapClasses.put("KindAddress", com.bivgroup.crm.KindAddress.class);
        mapClasses.put("KindContractMember", com.bivgroup.crm.KindContractMember.class);
        mapClasses.put("Contact", com.bivgroup.crm.Contact.class);
        mapClasses.put("JClient", com.bivgroup.crm.JClient.class);
        mapClasses.put("EClient", com.bivgroup.crm.EClient.class);
        mapClasses.put("KindDocument", com.bivgroup.crm.KindDocument.class);
        mapClasses.put("PClient_VER", com.bivgroup.crm.PClient_VER.class);
        mapClasses.put("KindShareRole", com.bivgroup.crm.KindShareRole.class);
        mapClasses.put("KindClientProperty", com.bivgroup.crm.KindClientProperty.class);
        mapClasses.put("Person", com.bivgroup.crm.Person.class);
        mapClasses.put("KindStatus", com.bivgroup.crm.KindStatus.class);
        mapClasses.put("PPerson", com.bivgroup.crm.PPerson.class);
        mapClasses.put("PersonDocument", com.bivgroup.crm.PersonDocument.class);
        mapClasses.put("ContractPMember", com.bivgroup.crm.ContractPMember.class);
        mapClasses.put("ClientBankDetails", com.bivgroup.crm.ClientBankDetails.class);
        mapClasses.put("ClientProfileAgreement", com.bivgroup.crm.ClientProfileAgreement.class);
        mapClasses.put("KindAgreementClientProfile", com.bivgroup.crm.KindAgreementClientProfile.class);
        mapClasses.put("ClientManager", com.bivgroup.crm.ClientManager.class);
        mapClasses.put("KindAccount", com.bivgroup.crm.KindAccount.class);
        mapClasses.put("SMState", com.bivgroup.crm.SMState.class);
        mapClasses.put("SMTransition", com.bivgroup.crm.SMTransition.class);
        mapClasses.put("SMType", com.bivgroup.crm.SMType.class);
        mapClasses.put("ClientProfileNotification", com.bivgroup.crm.ClientProfileNotification.class);
        // termination
        mapClasses.put("ReasonChangeForContract", com.bivgroup.termination.ReasonChangeForContract.class);
        mapClasses.put("ReasonChangeForContract_Freeze", com.bivgroup.termination.ReasonChangeForContract_Freeze.class);
        mapClasses.put("ReasonChangeForContract_Option", com.bivgroup.termination.ReasonChangeForContract_Option.class);
        mapClasses.put("ReasonChangeForContract_ChangeFund", com.bivgroup.termination.ReasonChangeForContract_ChangeFund.class);
        mapClasses.put("ReasonChangeForContract_ExtPremPay", com.bivgroup.termination.ReasonChangeForContract_ExtPremPay.class);
        mapClasses.put("ReasonChangeForContract_FixIncome", com.bivgroup.termination.ReasonChangeForContract_FixIncome.class);
        mapClasses.put("ReasonChangeForContract_WithdrawIncome", com.bivgroup.termination.ReasonChangeForContract_WithdrawIncome.class);
        mapClasses.put("ReasonChangeForContract_Indexing", com.bivgroup.termination.ReasonChangeForContract_Indexing.class);
        mapClasses.put("ReasonChangeForContract_BChangePassport", com.bivgroup.termination.ReasonChangeForContract_BChangePassport.class);
        mapClasses.put("ReasonChangeForContract_LaChangeSurname", com.bivgroup.termination.ReasonChangeForContract_LaChangeSurname.class);
        mapClasses.put("ReasonChangeForContract_OnRegistration", com.bivgroup.termination.ReasonChangeForContract_OnRegistration.class);
        mapClasses.put("ReasonChangeForContract_Cancellation", com.bivgroup.termination.ReasonChangeForContract_Cancellation.class);
        mapClasses.put("ReasonChangeForContract_FinancialVacation", com.bivgroup.termination.ReasonChangeForContract_FinancialVacation.class);
        mapClasses.put("ReasonChangeForContract_LaChangePassport", com.bivgroup.termination.ReasonChangeForContract_LaChangePassport.class);
        mapClasses.put("ReasonChangeForContract_BenChange", com.bivgroup.termination.ReasonChangeForContract_BenChange.class);
        mapClasses.put("ReasonChangeForContract_HolderChange", com.bivgroup.termination.ReasonChangeForContract_HolderChange.class);
        mapClasses.put("ReasonChangeForContract_Annulment", com.bivgroup.termination.ReasonChangeForContract_Annulment.class);
        mapClasses.put("ReasonChangeForContract_HChangeSurname", com.bivgroup.termination.ReasonChangeForContract_HChangeSurname.class);
        mapClasses.put("ReasonChangeForContract_DecreaseInsSum", com.bivgroup.termination.ReasonChangeForContract_DecreaseInsSum.class);
        mapClasses.put("ReasonChangeForContract_Activation", com.bivgroup.termination.ReasonChangeForContract_Activation.class);
        mapClasses.put("ReasonChangeForContract_Options", com.bivgroup.termination.ReasonChangeForContract_Options.class);
        mapClasses.put("ReasonChangeForContract_BChangePersData", com.bivgroup.termination.ReasonChangeForContract_BChangePersData.class);
        mapClasses.put("ReasonChangeForContract_ExtraRoi", com.bivgroup.termination.ReasonChangeForContract_ExtraRoi.class);
        mapClasses.put("ReasonChangeForContract_LaChangeAddress", com.bivgroup.termination.ReasonChangeForContract_LaChangeAddress.class);
        mapClasses.put("ReasonChangeForContract_Claim", com.bivgroup.termination.ReasonChangeForContract_Claim.class);
        mapClasses.put("ReasonChangeForContract_Project", com.bivgroup.termination.ReasonChangeForContract_Project.class);
        mapClasses.put("ReasonChangeForContract_TransferToPaid", com.bivgroup.termination.ReasonChangeForContract_TransferToPaid.class);
        mapClasses.put("ReasonChangeForContract_ExitFinancialVacation", com.bivgroup.termination.ReasonChangeForContract_ExitFinancialVacation.class);
        mapClasses.put("ReasonChangeForContract_ChangeAsset", com.bivgroup.termination.ReasonChangeForContract_ChangeAsset.class);
        mapClasses.put("ReasonChangeForContract_Instalments", com.bivgroup.termination.ReasonChangeForContract_Instalments.class);
        mapClasses.put("ReasonChangeForContract_ClaimOpen", com.bivgroup.termination.ReasonChangeForContract_ClaimOpen.class);
        mapClasses.put("ReasonChangeForContract_ChangeBorker", com.bivgroup.termination.ReasonChangeForContract_ChangeBorker.class);
        mapClasses.put("ReasonChangeForContract_ExcludePrograms", com.bivgroup.termination.ReasonChangeForContract_ExcludePrograms.class);
        mapClasses.put("ReasonChangeForContract_ExtraPremium", com.bivgroup.termination.ReasonChangeForContract_ExtraPremium.class);
        mapClasses.put("ReasonChangeForContract_HChangePersData", com.bivgroup.termination.ReasonChangeForContract_HChangePersData.class);
        mapClasses.put("ReasonChangeForContract_Recalculation", com.bivgroup.termination.ReasonChangeForContract_Recalculation.class);
        mapClasses.put("ReasonChangeForContract_Underwriting", com.bivgroup.termination.ReasonChangeForContract_Underwriting.class);
        mapClasses.put("ReasonChangeForContract_IncludePrograms", com.bivgroup.termination.ReasonChangeForContract_IncludePrograms.class);
        mapClasses.put("ReasonChangeForContract_HChangeContInfo", com.bivgroup.termination.ReasonChangeForContract_HChangeContInfo.class);
        mapClasses.put("ReasonChangeForContract_LaChangeContInfo", com.bivgroup.termination.ReasonChangeForContract_LaChangeContInfo.class);
        mapClasses.put("ReasonChangeForContract_BChangeContInfo", com.bivgroup.termination.ReasonChangeForContract_BChangeContInfo.class);
        mapClasses.put("ReasonChangeForContract_Refusal", com.bivgroup.termination.ReasonChangeForContract_Refusal.class);
        mapClasses.put("ReasonChangeForContract_IncreaseInsSum", com.bivgroup.termination.ReasonChangeForContract_IncreaseInsSum.class);
        mapClasses.put("ReasonChangeForContract_LaChangePersData", com.bivgroup.termination.ReasonChangeForContract_LaChangePersData.class);
        mapClasses.put("ReasonChangeForContract_HChangePassport", com.bivgroup.termination.ReasonChangeForContract_HChangePassport.class);
        mapClasses.put("ReasonChangeForContract_BChangeAddress", com.bivgroup.termination.ReasonChangeForContract_BChangeAddress.class);
        mapClasses.put("ReasonChangeForContract_IncreasePeriod", com.bivgroup.termination.ReasonChangeForContract_IncreasePeriod.class);
        mapClasses.put("ReasonChangeForContract_BChangeSurname", com.bivgroup.termination.ReasonChangeForContract_BChangeSurname.class);
        mapClasses.put("ReasonChangeForContract_LaChange", com.bivgroup.termination.ReasonChangeForContract_LaChange.class);
        mapClasses.put("ReasonChangeForContract_DecreasePeriod", com.bivgroup.termination.ReasonChangeForContract_DecreasePeriod.class);
        mapClasses.put("ReasonChangeForContract_ChangeCreditData", com.bivgroup.termination.ReasonChangeForContract_ChangeCreditData.class);
        mapClasses.put("ReasonChangeForContract_Active", com.bivgroup.termination.ReasonChangeForContract_Active.class);
        mapClasses.put("ReasonChangeForContract_FixationRoi", com.bivgroup.termination.ReasonChangeForContract_FixationRoi.class);
        mapClasses.put("ReasonChangeForContract_HChangeAddress", com.bivgroup.termination.ReasonChangeForContract_HChangeAddress.class);
        mapClasses.put("ReasonChangeForContract_DuplicateDocument", com.bivgroup.termination.ReasonChangeForContract_DuplicateDocument.class);
        mapClasses.put("ReasonChange_PPOOnlineActivation", com.bivgroup.termination.ReasonChange_PPOOnlineActivation.class);
        mapClasses.put("TerminationReason", com.bivgroup.termination.TerminationReason.class);
        mapClasses.put("ReceivingChannel", com.bivgroup.termination.ReceivingChannel.class);
        mapClasses.put("DeclarationForContract", com.bivgroup.termination.DeclarationForContract.class);
        mapClasses.put("KindChangeReason", com.bivgroup.termination.KindChangeReason.class);
        mapClasses.put("DeclarationOfChangeForContract", com.bivgroup.termination.DeclarationOfChangeForContract.class);
        mapClasses.put("UserPost", com.bivgroup.termination.UserPost.class);
        mapClasses.put("ValueReasonChangeForContract", com.bivgroup.termination.ValueReasonChangeForContract.class);
        mapClasses.put("DeclarationOfAvoidanceForContract", com.bivgroup.termination.DeclarationOfAvoidanceForContract.class);
        mapClasses.put("ClientBankDetails", com.bivgroup.crm.ClientBankDetails.class);
        mapClasses.put("ClientProfileAgreement", com.bivgroup.crm.ClientProfileAgreement.class);
        mapClasses.put("KindAgreementClientProfile", com.bivgroup.crm.KindAgreementClientProfile.class);
        mapClasses.put("KindInvBaseActive", com.bivgroup.termination.KindInvBaseActive.class);
        mapClasses.put("ProdKindChangeReason", com.bivgroup.termination.ProdKindChangeReason.class);
        mapClasses.put("KindInvIncomeFixType", com.bivgroup.termination.KindInvIncomeFixType.class);
        mapClasses.put("KindDeclaration", com.bivgroup.termination.KindDeclaration.class);
        mapClasses.put("KindDeclarationDiscriminator", com.bivgroup.termination.KindDeclarationDiscriminator.class);
        mapClasses.put("ProdKindDeclaration", com.bivgroup.termination.ProdKindDeclaration.class);
        // loss
        mapClasses.put("LossCompReq", com.bivgroup.loss.LossCompReq.class);
        mapClasses.put("LossDamageCategory", com.bivgroup.loss.LossDamageCategory.class);
        mapClasses.put("LossEvent", com.bivgroup.loss.LossEvent.class);
        mapClasses.put("LossEventCategory", com.bivgroup.loss.LossEventCategory.class);
        mapClasses.put("LossEventReason", com.bivgroup.loss.LossEventReason.class);
        mapClasses.put("LossNotice", com.bivgroup.loss.LossNotice.class);
        mapClasses.put("KindIntegrationRisk", com.bivgroup.loss.KindIntegrationRisk.class);
    }

}
