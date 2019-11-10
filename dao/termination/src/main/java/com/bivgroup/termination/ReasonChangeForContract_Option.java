package com.bivgroup.termination;
// Generated 16.03.2018 18:02:40 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_Option Generated 16.03.2018 18:02:40 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_Option")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_Option extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Long attrInt1;

    public ReasonChangeForContract_Option() {
    }

	
    public ReasonChangeForContract_Option(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_Option(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Long attrInt1) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.attrInt1 = attrInt1;
    }
   

    
    @Column(name="attrInt1")
    public Long getAttrInt1() {
        return this.attrInt1;
    }
    
    public void setAttrInt1(Long attrInt1) {
        this.attrInt1 = attrInt1;
    }




}


