package com.bivgroup.termination;
// Generated 18.01.2018 13:39:30 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;

import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChange_PPOOnlineActivation Generated 18.01.2018 13:39:30 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChange_PPOOnlineActivation")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChange_PPOOnlineActivation extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


    private Long policyId;
    private Long thirdPartyId;
     private String ppoStatus;

    public ReasonChange_PPOOnlineActivation() {
    }


    public ReasonChange_PPOOnlineActivation(Long id) {
        super(id);
    }
    public ReasonChange_PPOOnlineActivation(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Long policyId, Long thirdPartyId, String ppoStatus) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.policyId = policyId;
       this.thirdPartyId = thirdPartyId;
       this.ppoStatus = ppoStatus;
    }
   

    
    @Column(name="attrInt1")
    public Long getPolicyId() {
        return this.policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }


    @Column(name="thirdPartyId")
    public Long getThirdPartyId() {
        return this.thirdPartyId;
    }

    public void setThirdPartyId(Long thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }


    @Column(name="ppoStatus")
    public String getPpoStatus() {
        return this.ppoStatus;
    }

    public void setPpoStatus(String ppoStatus) {
        this.ppoStatus = ppoStatus;
    }




}


