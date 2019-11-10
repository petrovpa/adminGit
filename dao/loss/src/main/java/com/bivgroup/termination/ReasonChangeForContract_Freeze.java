package com.bivgroup.termination;
// Generated 16.03.2018 18:00:21 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_Freeze Generated 16.03.2018 18:00:21 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_Freeze")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_Freeze extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Date attrDate1;
     private Integer attrBoolean1;

    public ReasonChangeForContract_Freeze() {
    }

	
    public ReasonChangeForContract_Freeze(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_Freeze(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Date attrDate1, Integer attrBoolean1) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.attrDate1 = attrDate1;
       this.attrBoolean1 = attrBoolean1;
    }
   

    
    @Column(name="attrDate1")
    public Date getAttrDate1() {
        return this.attrDate1;
    }
    
    public void setAttrDate1(Date attrDate1) {
        this.attrDate1 = attrDate1;
    }

    
    @Column(name="attrBoolean1")
    public Integer getAttrBoolean1() {
        return this.attrBoolean1;
    }
    
    public void setAttrBoolean1(Integer attrBoolean1) {
        this.attrBoolean1 = attrBoolean1;
    }




}


