package com.bivgroup.termination;
// Generated 16.03.2018 18:02:53 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import com.bivgroup.crm.PPerson;

import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_BenChange Generated 16.03.2018 18:02:53 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_BenChange")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_BenChange extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private String riskCode;
     private Long part;
     private Long oldThirdPartyId;
     private Long newThirdPartyId;
     private PPerson personId_EN;
     private Long personId;
     private PPerson insuredId_EN;
     private Long insuredId;

    public ReasonChangeForContract_BenChange() {
    }

	
    public ReasonChangeForContract_BenChange(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_BenChange(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, String riskCode, Long part, Long oldThirdPartyId, Long newThirdPartyId, PPerson personId_EN, Long personId, PPerson insuredId_EN, Long insuredId) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.riskCode = riskCode;
       this.part = part;
       this.oldThirdPartyId = oldThirdPartyId;
       this.newThirdPartyId = newThirdPartyId;
       this.personId_EN = personId_EN;
       this.personId = personId;
       this.insuredId_EN = insuredId_EN;
       this.insuredId = insuredId;
    }
   

    
    @Column(name="riskCode")
    public String getRiskCode() {
        return this.riskCode;
    }
    
    public void setRiskCode(String riskCode) {
        this.riskCode = riskCode;
    }

    
    @Column(name="part")
    public Long getPart() {
        return this.part;
    }
    
    public void setPart(Long part) {
        this.part = part;
    }

    
    @Column(name="oldThirdPartyId")
    public Long getOldThirdPartyId() {
        return this.oldThirdPartyId;
    }
    
    public void setOldThirdPartyId(Long oldThirdPartyId) {
        this.oldThirdPartyId = oldThirdPartyId;
    }

    
    @Column(name="newThirdPartyId")
    public Long getNewThirdPartyId() {
        return this.newThirdPartyId;
    }
    
    public void setNewThirdPartyId(Long newThirdPartyId) {
        this.newThirdPartyId = newThirdPartyId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="personId")
    public PPerson getPersonId_EN() {
        return this.personId_EN;
    }
    
    public void setPersonId_EN(PPerson personId_EN) {
        this.personId_EN = personId_EN;
    }

    
    @Column(name="personId", insertable=false, updatable=false)
    public Long getPersonId() {
        return this.personId;
    }
    
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="personId1")
    public PPerson getInsuredId_EN() {
        return this.insuredId_EN;
    }
    
    public void setInsuredId_EN(PPerson insuredId_EN) {
        this.insuredId_EN = insuredId_EN;
    }

    
    @Column(name="personId1", insertable=false, updatable=false)
    public Long getInsuredId() {
        return this.insuredId;
    }
    
    public void setInsuredId(Long insuredId) {
        this.insuredId = insuredId;
    }




}


