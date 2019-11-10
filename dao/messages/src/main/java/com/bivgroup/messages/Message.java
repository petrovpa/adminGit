package com.bivgroup.messages;
// Generated 30.01.2018 11:02:45 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 

import com.bivgroup.loss.LossNotice;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Message Generated 30.01.2018 11:02:45 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="SD_MESSAGE"
)
public class Message  implements java.io.Serializable {


     private Long id;
     private Long titleHash;
     private String identifierMsg;
     private String title;
     private Long updateUserId;
     private Date updateDate;
     private Long createUserId;
     private Date createDate;
     private Long typeMessage;
     private String note;
     private Integer isUnread;
     private Integer isFavorite;
     private Long pdDeclarationId;
     private Long chNotificationId;
     private Long eId;
     private LossNotice lossNoticeId_EN;
     private Long lossNoticeId;
     private Chat chatId_EN;
     private Long chatId;
     private MessageCorrespondent senderId_EN;
     private Long senderId;
     private Long rowStatus;

    public Message() {
    }

	
    public Message(Long id) {
        this.id = id;
    }
    public Message(Long id, Long titleHash, String identifierMsg, String title, Long updateUserId, Date updateDate, Long createUserId, Date createDate, Long typeMessage, String note, Integer isUnread, Integer isFavorite, Long pdDeclarationId, Long chNotificationId, Long eId, LossNotice lossNoticeId_EN, Long lossNoticeId, Chat chatId_EN, Long chatId, MessageCorrespondent senderId_EN, Long senderId, Long rowStatus) {
       this.id = id;
       this.titleHash = titleHash;
       this.identifierMsg = identifierMsg;
       this.title = title;
       this.updateUserId = updateUserId;
       this.updateDate = updateDate;
       this.createUserId = createUserId;
       this.createDate = createDate;
       this.typeMessage = typeMessage;
       this.note = note;
       this.isUnread = isUnread;
       this.isFavorite = isFavorite;
       this.pdDeclarationId = pdDeclarationId;
       this.chNotificationId = chNotificationId;
       this.eId = eId;
       this.lossNoticeId_EN = lossNoticeId_EN;
       this.lossNoticeId = lossNoticeId;
       this.chatId_EN = chatId_EN;
       this.chatId = chatId;
       this.senderId_EN = senderId_EN;
       this.senderId = senderId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_MESSAGE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="titleHash")
    public Long getTitleHash() {
        return this.titleHash;
    }
    
    public void setTitleHash(Long titleHash) {
        this.titleHash = titleHash;
    }

    
    @Column(name="identifierMsg")
    public String getIdentifierMsg() {
        return this.identifierMsg;
    }
    
    public void setIdentifierMsg(String identifierMsg) {
        this.identifierMsg = identifierMsg;
    }

    
    @Column(name="title")
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    
    @Column(name="updateUserId")
    public Long getUpdateUserId() {
        return this.updateUserId;
    }
    
    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updateDate")
    public Date getUpdateDate() {
        return this.updateDate;
    }
    
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    
    @Column(name="createUserId")
    public Long getCreateUserId() {
        return this.createUserId;
    }
    
    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createDate")
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    
    @Column(name="typeMessage")
    public Long getTypeMessage() {
        return this.typeMessage;
    }
    
    public void setTypeMessage(Long typeMessage) {
        this.typeMessage = typeMessage;
    }

    
    @Column(name="note")
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }

    
    @Column(name="isUnread")
    public Integer getIsUnread() {
        return this.isUnread;
    }
    
    public void setIsUnread(Integer isUnread) {
        this.isUnread = isUnread;
    }

    
    @Column(name="isFavorite")
    public Integer getIsFavorite() {
        return this.isFavorite;
    }
    
    public void setIsFavorite(Integer isFavorite) {
        this.isFavorite = isFavorite;
    }

    
    @Column(name="pdDeclarationId")
    public Long getPdDeclarationId() {
        return this.pdDeclarationId;
    }
    
    public void setPdDeclarationId(Long pdDeclarationId) {
        this.pdDeclarationId = pdDeclarationId;
    }

    
    @Column(name="chNotificationId")
    public Long getChNotificationId() {
        return this.chNotificationId;
    }
    
    public void setChNotificationId(Long chNotificationId) {
        this.chNotificationId = chNotificationId;
    }

    
    @Column(name="eid")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="lossNoticeId")
    public LossNotice getLossNoticeId_EN() {
        return this.lossNoticeId_EN;
    }
    
    public void setLossNoticeId_EN(LossNotice lossNoticeId_EN) {
        this.lossNoticeId_EN = lossNoticeId_EN;
    }

    
    @Column(name="lossNoticeId", insertable=false, updatable=false)
    public Long getLossNoticeId() {
        return this.lossNoticeId;
    }
    
    public void setLossNoticeId(Long lossNoticeId) {
        this.lossNoticeId = lossNoticeId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="chatId")
    public Chat getChatId_EN() {
        return this.chatId_EN;
    }
    
    public void setChatId_EN(Chat chatId_EN) {
        this.chatId_EN = chatId_EN;
    }

    
    @Column(name="chatId", insertable=false, updatable=false)
    public Long getChatId() {
        return this.chatId;
    }
    
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="senderId")
    public MessageCorrespondent getSenderId_EN() {
        return this.senderId_EN;
    }
    
    public void setSenderId_EN(MessageCorrespondent senderId_EN) {
        this.senderId_EN = senderId_EN;
    }

    
    @Column(name="senderId", insertable=false, updatable=false)
    public Long getSenderId() {
        return this.senderId;
    }
    
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


