package com.bivgroup.termination;
// Generated 16.03.2018 18:00:23 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * ReasonChangeForContract_ChangeFund Generated 16.03.2018 18:00:23 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_ChangeFund")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_ChangeFund extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private KindInvBaseActive fundId_EN;
     private Long fundId;
     private KindInvBaseActive prevFundId_EN;
     private Long prevFundId;

    public ReasonChangeForContract_ChangeFund() {
    }

	
    public ReasonChangeForContract_ChangeFund(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_ChangeFund(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, KindInvBaseActive fundId_EN, Long fundId, KindInvBaseActive prevFundId_EN, Long prevFundId) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.fundId_EN = fundId_EN;
       this.fundId = fundId;
       this.prevFundId_EN = prevFundId_EN;
       this.prevFundId = prevFundId;
    }
   

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="fundId")
    public KindInvBaseActive getFundId_EN() {
        return this.fundId_EN;
    }
    
    public void setFundId_EN(KindInvBaseActive fundId_EN) {
        this.fundId_EN = fundId_EN;
    }

    
    @Column(name="fundId", insertable=false, updatable=false)
    public Long getFundId() {
        return this.fundId;
    }
    
    public void setFundId(Long fundId) {
        this.fundId = fundId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="prevFundId")
    public KindInvBaseActive getPrevFundId_EN() {
        return this.prevFundId_EN;
    }
    
    public void setPrevFundId_EN(KindInvBaseActive prevFundId_EN) {
        this.prevFundId_EN = prevFundId_EN;
    }

    
    @Column(name="prevFundId", insertable=false, updatable=false)
    public Long getPrevFundId() {
        return this.prevFundId;
    }
    
    public void setPrevFundId(Long prevFundId) {
        this.prevFundId = prevFundId;
    }




}


