package com.bivgroup.termination;
// Generated 16.03.2018 18:00:36 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_DecreaseInsSum Generated 16.03.2018 18:00:36 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_DecreaseInsSum")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_DecreaseInsSum extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Double oldInsPremValue;
     private Double oldInsSumValue;
     private Long oldPayVarId;
     private String riskSysName;
     private Double newInsAmValue;
     private Double newPremValue;

    public ReasonChangeForContract_DecreaseInsSum() {
    }

	
    public ReasonChangeForContract_DecreaseInsSum(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_DecreaseInsSum(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Double oldInsPremValue, Double oldInsSumValue, Long oldPayVarId, String riskSysName, Double newInsAmValue, Double newPremValue) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.oldInsPremValue = oldInsPremValue;
       this.oldInsSumValue = oldInsSumValue;
       this.oldPayVarId = oldPayVarId;
       this.riskSysName = riskSysName;
       this.newInsAmValue = newInsAmValue;
       this.newPremValue = newPremValue;
    }
   

    
    @Column(name="insPremValue")
    public Double getOldInsPremValue() {
        return this.oldInsPremValue;
    }
    
    public void setOldInsPremValue(Double oldInsPremValue) {
        this.oldInsPremValue = oldInsPremValue;
    }

    
    @Column(name="insAmValue")
    public Double getOldInsSumValue() {
        return this.oldInsSumValue;
    }
    
    public void setOldInsSumValue(Double oldInsSumValue) {
        this.oldInsSumValue = oldInsSumValue;
    }

    
    @Column(name="oldPayVarId")
    public Long getOldPayVarId() {
        return this.oldPayVarId;
    }
    
    public void setOldPayVarId(Long oldPayVarId) {
        this.oldPayVarId = oldPayVarId;
    }

    
    @Column(name="riskSysName")
    public String getRiskSysName() {
        return this.riskSysName;
    }
    
    public void setRiskSysName(String riskSysName) {
        this.riskSysName = riskSysName;
    }

    
    @Column(name="newInsAmValue")
    public Double getNewInsAmValue() {
        return this.newInsAmValue;
    }
    
    public void setNewInsAmValue(Double newInsAmValue) {
        this.newInsAmValue = newInsAmValue;
    }

    
    @Column(name="newPremValue")
    public Double getNewPremValue() {
        return this.newPremValue;
    }
    
    public void setNewPremValue(Double newPremValue) {
        this.newPremValue = newPremValue;
    }




}


