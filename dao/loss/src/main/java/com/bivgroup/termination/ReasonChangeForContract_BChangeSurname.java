package com.bivgroup.termination;
// Generated 16.03.2018 18:00:35 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_BChangeSurname Generated 16.03.2018 18:00:35 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_BChangeSurname")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_BChangeSurname extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Long thirdPartyId;
     private String surName;

    public ReasonChangeForContract_BChangeSurname() {
    }

	
    public ReasonChangeForContract_BChangeSurname(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_BChangeSurname(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Long thirdPartyId, String surName) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.thirdPartyId = thirdPartyId;
       this.surName = surName;
    }
   

    
    @Column(name="thirdPartyId")
    public Long getThirdPartyId() {
        return this.thirdPartyId;
    }
    
    public void setThirdPartyId(Long thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    
    @Column(name="surName")
    public String getSurName() {
        return this.surName;
    }
    
    public void setSurName(String surName) {
        this.surName = surName;
    }




}


