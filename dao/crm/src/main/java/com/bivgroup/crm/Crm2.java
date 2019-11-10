package com.bivgroup.crm;

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
public class Crm2 extends HierarchyDAO implements ExtendGenericDAO {

    @PersistenceContext(unitName = "Crm")
    private EntityManager l_em;

    @Resource
    private UserTransaction l_ut;

    @Inject
    private AspectEntityManager l_aem;

    public Crm2() {
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
        mapClasses.put("BinaryFile", com.bivgroup.crm.BinaryFile.class);
        mapClasses.put("SMType", com.bivgroup.crm.SMType.class);
        mapClasses.put("ClientProfileNotification", com.bivgroup.crm.ClientProfileNotification.class);
    }

}
