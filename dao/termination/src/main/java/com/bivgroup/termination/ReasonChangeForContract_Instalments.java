package com.bivgroup.termination;
// Generated 16.03.2018 18:02:52 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_Instalments Generated 16.03.2018 18:02:52 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_Instalments")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_Instalments extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Long newPayVarId;
     private Long oldPayVarId;
     private Double sumPay;

    public ReasonChangeForContract_Instalments() {
    }

	
    public ReasonChangeForContract_Instalments(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_Instalments(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Long newPayVarId, Long oldPayVarId, Double sumPay) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.newPayVarId = newPayVarId;
       this.oldPayVarId = oldPayVarId;
       this.sumPay = sumPay;
    }
   

    
    @Column(name="NewPayVarId")
    public Long getNewPayVarId() {
        return this.newPayVarId;
    }
    
    public void setNewPayVarId(Long newPayVarId) {
        this.newPayVarId = newPayVarId;
    }

    
    @Column(name="oldPayVarId")
    public Long getOldPayVarId() {
        return this.oldPayVarId;
    }
    
    public void setOldPayVarId(Long oldPayVarId) {
        this.oldPayVarId = oldPayVarId;
    }

    
    @Column(name="insAmValue")
    public Double getSumPay() {
        return this.sumPay;
    }
    
    public void setSumPay(Double sumPay) {
        this.sumPay = sumPay;
    }




}


