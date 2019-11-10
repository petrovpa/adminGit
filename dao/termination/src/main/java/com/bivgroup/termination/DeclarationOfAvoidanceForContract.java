package com.bivgroup.termination;
// Generated 20.11.2017 11:45:56 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * DeclarationOfAvoidanceForContract Generated 20.11.2017 11:45:56 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "DeclarationOfAvoidanceForContract") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="PD_DeclarationOfAvoid"
)
public class DeclarationOfAvoidanceForContract extends com.bivgroup.termination.DeclarationForContract implements java.io.Serializable {


     private TerminationReason terminationReasonId_EN;
     private Long terminationReasonId;

    public DeclarationOfAvoidanceForContract() {
    }

	
    public DeclarationOfAvoidanceForContract(Long id) {
        super(id);        
    }
    public DeclarationOfAvoidanceForContract(Long id, Date contractDate, String docFolder1C, String externalId, String note, Integer isExistOriginal, Long eId, Date incomingDate, Integer typeRecipient, Date dateOfEntry, Float changePremValue, Date receivingDate, String incomingNumber, Date departureDate, Date supposedDateOfEntry, Integer initiator, Date declarationDate, String declarationNumber, Long contractId, Date dateLastDoc, Long updateUser, Date updateDate, Long createUser, Date createDate, PPerson recipientId_EN, Long recipientId, PPerson representativeId_EN, Long representativeId, PPerson applicantId_EN, Long applicantId, DeclarationForContract parentId_EN, Long parentId, KindStatus stateId_EN, Long stateId, ReceivingChannel receivingChannelId_EN, Long receivingChannelId, Set<UserPost> notes, Set<DeclarationForContract> subDeclarations, Long rowStatus, TerminationReason terminationReasonId_EN, Long terminationReasonId) {
        super(id, contractDate, docFolder1C, externalId, note, isExistOriginal, eId, incomingDate, typeRecipient, dateOfEntry, changePremValue, receivingDate, incomingNumber, departureDate, supposedDateOfEntry, initiator, declarationDate, declarationNumber, contractId, dateLastDoc, updateUser, updateDate, createUser, createDate, recipientId_EN, recipientId, representativeId_EN, representativeId, applicantId_EN, applicantId, parentId_EN, parentId, stateId_EN, stateId, receivingChannelId_EN, receivingChannelId, notes, subDeclarations, rowStatus);        
       this.terminationReasonId_EN = terminationReasonId_EN;
       this.terminationReasonId = terminationReasonId;
    }
   

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TerminationReasonID")
    public TerminationReason getTerminationReasonId_EN() {
        return this.terminationReasonId_EN;
    }
    
    public void setTerminationReasonId_EN(TerminationReason terminationReasonId_EN) {
        this.terminationReasonId_EN = terminationReasonId_EN;
    }

    
    @Column(name="TerminationReasonID", insertable=false, updatable=false)
    public Long getTerminationReasonId() {
        return this.terminationReasonId;
    }
    
    public void setTerminationReasonId(Long terminationReasonId) {
        this.terminationReasonId = terminationReasonId;
    }




}


