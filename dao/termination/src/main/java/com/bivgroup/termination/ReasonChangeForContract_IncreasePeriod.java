package com.bivgroup.termination;
// Generated 16.03.2018 18:02:51 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_IncreasePeriod Generated 16.03.2018 18:02:51 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_IncreasePeriod")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_IncreasePeriod extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Date newFinishDate;
     private Date oldFinishDate;
     private Long oldTermId;
     private Long newTermId;

    public ReasonChangeForContract_IncreasePeriod() {
    }

	
    public ReasonChangeForContract_IncreasePeriod(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_IncreasePeriod(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Date newFinishDate, Date oldFinishDate, Long oldTermId, Long newTermId) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.newFinishDate = newFinishDate;
       this.oldFinishDate = oldFinishDate;
       this.oldTermId = oldTermId;
       this.newTermId = newTermId;
    }
   

    
    @Column(name="newFinishDate")
    public Date getNewFinishDate() {
        return this.newFinishDate;
    }
    
    public void setNewFinishDate(Date newFinishDate) {
        this.newFinishDate = newFinishDate;
    }

    
    @Column(name="oldFinishDate")
    public Date getOldFinishDate() {
        return this.oldFinishDate;
    }
    
    public void setOldFinishDate(Date oldFinishDate) {
        this.oldFinishDate = oldFinishDate;
    }

    
    @Column(name="oldTermId")
    public Long getOldTermId() {
        return this.oldTermId;
    }
    
    public void setOldTermId(Long oldTermId) {
        this.oldTermId = oldTermId;
    }

    
    @Column(name="NewTermId")
    public Long getNewTermId() {
        return this.newTermId;
    }
    
    public void setNewTermId(Long newTermId) {
        this.newTermId = newTermId;
    }




}


