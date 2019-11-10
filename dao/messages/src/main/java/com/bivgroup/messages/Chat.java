package com.bivgroup.messages;
// Generated Apr 10, 2018 12:58:16 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.core.aspect.impl.state.AspectStateSM;
import com.bivgroup.crm.Client;
import com.bivgroup.crm.SMState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Chat Generated Apr 10, 2018 12:58:16 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="SD_CHAT"
)
@AspectStateSM(attributeName = "stateId_EN", startStateName = "SD_CHAT_NEW", typeSysName = "SD_CHAT")
public class Chat  implements java.io.Serializable {


     private Long id;
     private Long userId;
     private Long contractId;
     private String title;
     private Client applicantId_EN;
     private Long applicantId;
     private Message lastMessageId_EN;
     private Long lastMessageId;
     private SMState stateId_EN;
     private Long stateId;
     private ChatTopicType chatTopicTypeId_EN;
     private Long chatTopicTypeId;
     private Long rowStatus;

    public Chat() {
    }

	
    public Chat(Long id) {
        this.id = id;
    }
    public Chat(Long id, Long userId, Long contractId, String title, Client applicantId_EN, Long applicantId, Message lastMessageId_EN, Long lastMessageId, SMState stateId_EN, Long stateId, ChatTopicType chatTopicTypeId_EN, Long chatTopicTypeId, Long rowStatus) {
       this.id = id;
       this.userId = userId;
       this.contractId = contractId;
       this.title = title;
       this.applicantId_EN = applicantId_EN;
       this.applicantId = applicantId;
       this.lastMessageId_EN = lastMessageId_EN;
       this.lastMessageId = lastMessageId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.chatTopicTypeId_EN = chatTopicTypeId_EN;
       this.chatTopicTypeId = chatTopicTypeId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_CHAT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="userId")
    public Long getUserId() {
        return this.userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    
    @Column(name="contractId")
    public Long getContractId() {
        return this.contractId;
    }
    
    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    
    @Column(name="title")
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="applicantId")
    public Client getApplicantId_EN() {
        return this.applicantId_EN;
    }
    
    public void setApplicantId_EN(Client applicantId_EN) {
        this.applicantId_EN = applicantId_EN;
    }

    
    @Column(name="applicantId", insertable=false, updatable=false)
    public Long getApplicantId() {
        return this.applicantId;
    }
    
    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="LASTMESSAGEID")
    public Message getLastMessageId_EN() {
        return this.lastMessageId_EN;
    }
    
    public void setLastMessageId_EN(Message lastMessageId_EN) {
        this.lastMessageId_EN = lastMessageId_EN;
    }

    
    @Column(name="LASTMESSAGEID", insertable=false, updatable=false)
    public Long getLastMessageId() {
        return this.lastMessageId;
    }
    
    public void setLastMessageId(Long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="stateId")
    public SMState getStateId_EN() {
        return this.stateId_EN;
    }
    
    public void setStateId_EN(SMState stateId_EN) {
        this.stateId_EN = stateId_EN;
    }

    
    @Column(name="stateId", insertable=false, updatable=false)
    public Long getStateId() {
        return this.stateId;
    }
    
    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="chatTopicTypeId")
    public ChatTopicType getChatTopicTypeId_EN() {
        return this.chatTopicTypeId_EN;
    }
    
    public void setChatTopicTypeId_EN(ChatTopicType chatTopicTypeId_EN) {
        this.chatTopicTypeId_EN = chatTopicTypeId_EN;
    }

    
    @Column(name="chatTopicTypeId", insertable=false, updatable=false)
    public Long getChatTopicTypeId() {
        return this.chatTopicTypeId;
    }
    
    public void setChatTopicTypeId(Long chatTopicTypeId) {
        this.chatTopicTypeId = chatTopicTypeId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


