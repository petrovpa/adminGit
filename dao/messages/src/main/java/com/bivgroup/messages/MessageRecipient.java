package com.bivgroup.messages;
// Generated 14.12.2017 18:39:30 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * MessageRecipient Generated 14.12.2017 18:39:30 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="SD_MESSAGE_RECIPIENT"
)
public class MessageRecipient  implements java.io.Serializable {


     private Long id;
     private Long eId;
     private Message messageId_EN;
     private Long messageId;
     private Long rowStatus;

    public MessageRecipient() {
    }

	
    public MessageRecipient(Long id) {
        this.id = id;
    }
    public MessageRecipient(Long id, Long eId, Message messageId_EN, Long messageId, Long rowStatus) {
       this.id = id;
       this.eId = eId;
       this.messageId_EN = messageId_EN;
       this.messageId = messageId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_MESSAGE_RECIPIENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="eId")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="messageId")
    public Message getMessageId_EN() {
        return this.messageId_EN;
    }
    
    public void setMessageId_EN(Message messageId_EN) {
        this.messageId_EN = messageId_EN;
    }

    
    @Column(name="messageId", insertable=false, updatable=false)
    public Long getMessageId() {
        return this.messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


