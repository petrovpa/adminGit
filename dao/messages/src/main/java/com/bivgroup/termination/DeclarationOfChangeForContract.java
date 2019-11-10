package com.bivgroup.termination;
// Generated 20.11.2017 12:47:09 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import com.bivgroup.crm.PPerson;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * DeclarationOfChangeForContract Generated 20.11.2017 12:47:09 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "DeclarationOfChangeForContract") 
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id") 
@Table(name="PD_DECLARATIONOFCHANGE"
)
public class DeclarationOfChangeForContract extends com.bivgroup.termination.DeclarationForContract implements java.io.Serializable {


     private Long sectionId;
     private Set<ReasonChangeForContract> reasons = new HashSet<ReasonChangeForContract>(0);

    public DeclarationOfChangeForContract() {
    }

	
    public DeclarationOfChangeForContract(Long id) {
        super(id);        
    }
    public DeclarationOfChangeForContract(Long id, Date contractDate, String docFolder1C, String externalId, String note, Integer isExistOriginal, Long eId, Date incomingDate, Integer typeRecipient, Date dateOfEntry, Float changePremValue, Date receivingDate, String incomingNumber, Date departureDate, Date supposedDateOfEntry, Integer initiator, Date declarationDate, String declarationNumber, Long contractId, Date dateLastDoc, Long updateUser, Date updateDate, Long createUser, Date createDate, PPerson recipientId_EN, Long recipientId, PPerson representativeId_EN, Long representativeId, PPerson applicantId_EN, Long applicantId, DeclarationForContract parentId_EN, Long parentId, KindStatus stateId_EN, Long stateId, ReceivingChannel receivingChannelId_EN, Long receivingChannelId, Set<UserPost> notes, Set<DeclarationForContract> subDeclarations, Long rowStatus, Long sectionId, Set<ReasonChangeForContract> reasons) {
        super(id, contractDate, docFolder1C, externalId, note, isExistOriginal, eId, incomingDate, typeRecipient, dateOfEntry, changePremValue, receivingDate, incomingNumber, departureDate, supposedDateOfEntry, initiator, declarationDate, declarationNumber, contractId, dateLastDoc, updateUser, updateDate, createUser, createDate, recipientId_EN, recipientId, representativeId_EN, representativeId, applicantId_EN, applicantId, parentId_EN, parentId, stateId_EN, stateId, receivingChannelId_EN, receivingChannelId, notes, subDeclarations, rowStatus);        
       this.sectionId = sectionId;
       this.reasons = reasons;
    }
   

    
    @Column(name="sectionId")
    public Long getSectionId() {
        return this.sectionId;
    }
    
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="declarationId_EN")
    public Set<ReasonChangeForContract> getReasons() {
        return this.reasons;
    }
    
    public void setReasons(Set<ReasonChangeForContract> reasons) {
        this.reasons = reasons;
    }




}


