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
 * ReasonChangeForContract_HolderChange Generated 16.03.2018 18:02:53 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_HolderChange")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_HolderChange extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Long thirdPartyId;
     private PPerson insurantId_EN;
     private Long insurantId;
     private PPerson oldInsuredId_EN;
     private Long oldInsuredId;

    public ReasonChangeForContract_HolderChange() {
    }

	
    public ReasonChangeForContract_HolderChange(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_HolderChange(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Long thirdPartyId, PPerson insurantId_EN, Long insurantId, PPerson oldInsuredId_EN, Long oldInsuredId) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.thirdPartyId = thirdPartyId;
       this.insurantId_EN = insurantId_EN;
       this.insurantId = insurantId;
       this.oldInsuredId_EN = oldInsuredId_EN;
       this.oldInsuredId = oldInsuredId;
    }
   

    
    @Column(name="thirdPartyId")
    public Long getThirdPartyId() {
        return this.thirdPartyId;
    }
    
    public void setThirdPartyId(Long thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="personId")
    public PPerson getInsurantId_EN() {
        return this.insurantId_EN;
    }
    
    public void setInsurantId_EN(PPerson insurantId_EN) {
        this.insurantId_EN = insurantId_EN;
    }

    
    @Column(name="PersonId", insertable=false, updatable=false)
    public Long getInsurantId() {
        return this.insurantId;
    }
    
    public void setInsurantId(Long insurantId) {
        this.insurantId = insurantId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="personId1")
    public PPerson getOldInsuredId_EN() {
        return this.oldInsuredId_EN;
    }
    
    public void setOldInsuredId_EN(PPerson oldInsuredId_EN) {
        this.oldInsuredId_EN = oldInsuredId_EN;
    }

    
    @Column(name="personId1", insertable=false, updatable=false)
    public Long getOldInsuredId() {
        return this.oldInsuredId;
    }
    
    public void setOldInsuredId(Long oldInsuredId) {
        this.oldInsuredId = oldInsuredId;
    }




}


