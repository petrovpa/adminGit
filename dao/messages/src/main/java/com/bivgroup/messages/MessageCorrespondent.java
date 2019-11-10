package com.bivgroup.messages;
// Generated Jan 31, 2018 3:51:03 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.ClientProfile;

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
 * MessageCorrespondent Generated Jan 31, 2018 3:51:03 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="SD_MESSAGE_CORRESPONDENT"
)
public class MessageCorrespondent  implements java.io.Serializable {


     private Long id;
     private String phone;
     private String email;
     private Long discriminator;
     private Long eId;
     private Employee userId_EN;
     private Long userId;
     private ClientProfile clientProfileId_EN;
     private Long clientProfileId;
     private Message messageId_EN;
     private Long messageId;
     private MessageChannel channelId_EN;
     private Long channelId;
     private Long rowStatus;

    public MessageCorrespondent() {
    }

	
    public MessageCorrespondent(Long id) {
        this.id = id;
    }
    public MessageCorrespondent(Long id, String phone, String email, Long discriminator, Long eId, Employee userId_EN, Long userId, ClientProfile clientProfileId_EN, Long clientProfileId, Message messageId_EN, Long messageId, MessageChannel channelId_EN, Long channelId, Long rowStatus) {
       this.id = id;
       this.phone = phone;
       this.email = email;
       this.discriminator = discriminator;
       this.eId = eId;
       this.userId_EN = userId_EN;
       this.userId = userId;
       this.clientProfileId_EN = clientProfileId_EN;
       this.clientProfileId = clientProfileId;
       this.messageId_EN = messageId_EN;
       this.messageId = messageId;
       this.channelId_EN = channelId_EN;
       this.channelId = channelId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_MESSAGE_CORRESPONDENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="tel")
    public String getPhone() {
        return this.phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    
    @Column(name="email")
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    
    @Column(name="discriminator")
    public Long getDiscriminator() {
        return this.discriminator;
    }
    
    public void setDiscriminator(Long discriminator) {
        this.discriminator = discriminator;
    }

    
    @Column(name="eId")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="userId")
    public Employee getUserId_EN() {
        return this.userId_EN;
    }
    
    public void setUserId_EN(Employee userId_EN) {
        this.userId_EN = userId_EN;
    }

    
    @Column(name="userId", insertable=false, updatable=false)
    public Long getUserId() {
        return this.userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="clientProfileId")
    public ClientProfile getClientProfileId_EN() {
        return this.clientProfileId_EN;
    }
    
    public void setClientProfileId_EN(ClientProfile clientProfileId_EN) {
        this.clientProfileId_EN = clientProfileId_EN;
    }

    
    @Column(name="clientProfileId", insertable=false, updatable=false)
    public Long getClientProfileId() {
        return this.clientProfileId;
    }
    
    public void setClientProfileId(Long clientProfileId) {
        this.clientProfileId = clientProfileId;
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

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="channelId")
    public MessageChannel getChannelId_EN() {
        return this.channelId_EN;
    }
    
    public void setChannelId_EN(MessageChannel channelId_EN) {
        this.channelId_EN = channelId_EN;
    }

    
    @Column(name="channelId", insertable=false, updatable=false)
    public Long getChannelId() {
        return this.channelId;
    }
    
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


