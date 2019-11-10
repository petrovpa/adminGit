package com.bivgroup.termination;
// Generated 16.03.2018 18:00:24 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_ExtPremPay Generated 16.03.2018 18:00:24 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_ExtPremPay")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_ExtPremPay extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Float dopPremVal;

    public ReasonChangeForContract_ExtPremPay() {
    }

	
    public ReasonChangeForContract_ExtPremPay(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_ExtPremPay(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Float dopPremVal) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.dopPremVal = dopPremVal;
    }
   

    
    @Column(name="dopPremVal")
    public Float getDopPremVal() {
        return this.dopPremVal;
    }
    
    public void setDopPremVal(Float dopPremVal) {
        this.dopPremVal = dopPremVal;
    }




}


