package com.bivgroup.termination;
// Generated 16.03.2018 18:04:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
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
 * ReasonChangeForContract_FixIncome Generated 16.03.2018 18:04:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_FixIncome")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_FixIncome extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Float fixMinLimit;
     private Float fixMaxLimit;
     private KindInvIncomeFixType fixTypeId_EN;
     private Long fixTypeId;

    public ReasonChangeForContract_FixIncome() {
    }

	
    public ReasonChangeForContract_FixIncome(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_FixIncome(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Float fixMinLimit, Float fixMaxLimit, KindInvIncomeFixType fixTypeId_EN, Long fixTypeId) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.fixMinLimit = fixMinLimit;
       this.fixMaxLimit = fixMaxLimit;
       this.fixTypeId_EN = fixTypeId_EN;
       this.fixTypeId = fixTypeId;
    }
   

    
    @Column(name="fixMinLimit")
    public Float getFixMinLimit() {
        return this.fixMinLimit;
    }
    
    public void setFixMinLimit(Float fixMinLimit) {
        this.fixMinLimit = fixMinLimit;
    }

    
    @Column(name="fixMaxLimit")
    public Float getFixMaxLimit() {
        return this.fixMaxLimit;
    }
    
    public void setFixMaxLimit(Float fixMaxLimit) {
        this.fixMaxLimit = fixMaxLimit;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="fixTypeId")
    public KindInvIncomeFixType getFixTypeId_EN() {
        return this.fixTypeId_EN;
    }
    
    public void setFixTypeId_EN(KindInvIncomeFixType fixTypeId_EN) {
        this.fixTypeId_EN = fixTypeId_EN;
    }

    
    @Column(name="fixTypeId", insertable=false, updatable=false)
    public Long getFixTypeId() {
        return this.fixTypeId;
    }
    
    public void setFixTypeId(Long fixTypeId) {
        this.fixTypeId = fixTypeId;
    }




}


