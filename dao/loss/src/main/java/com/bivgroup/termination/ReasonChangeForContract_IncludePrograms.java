package com.bivgroup.termination;
// Generated 16.03.2018 18:00:33 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import com.bivgroup.crm.Person;

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
 * ReasonChangeForContract_IncludePrograms Generated 16.03.2018 18:00:33 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_IncludePrograms")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_IncludePrograms extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Double insAmValue;
     private String riskSysName;
     private Person insuredId_EN;
     private Long insuredId;
     private Long thirdPartyId;


    public ReasonChangeForContract_IncludePrograms() {
    }

	
    public ReasonChangeForContract_IncludePrograms(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_IncludePrograms(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Double insAmValue, String riskSysName, Person insuredId_EN, Long insuredId) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.insAmValue = insAmValue;
       this.riskSysName = riskSysName;
       this.insuredId_EN = insuredId_EN;
       this.insuredId = insuredId;
    }
   

    
    @Column(name="insAmValue")
    public Double getInsAmValue() {
        return this.insAmValue;
    }
    
    public void setInsAmValue(Double insAmValue) {
        this.insAmValue = insAmValue;
    }

    
    @Column(name="riskSysName")
    public String getRiskSysName() {
        return this.riskSysName;
    }
    
    public void setRiskSysName(String riskSysName) {
        this.riskSysName = riskSysName;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="insuredId")
    public Person getInsuredId_EN() {
        return this.insuredId_EN;
    }
    
    public void setInsuredId_EN(Person insuredId_EN) {
        this.insuredId_EN = insuredId_EN;
    }

    
    @Column(name="insuredId", insertable=false, updatable=false)
    public Long getInsuredId() {
        return this.insuredId;
    }
    
    public void setInsuredId(Long insuredId) {
        this.insuredId = insuredId;
    }

    @Column(name = "THIRDPARTYID")
    public Long getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(Long thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

}


