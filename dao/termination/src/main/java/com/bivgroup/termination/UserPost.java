package com.bivgroup.termination;
// Generated Aug 31, 2017 11:57:54 AM unknow unknow 


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
 * UserPost Generated Aug 31, 2017 11:57:54 AM unknow unknow 
 */
@Entity
@Table(name="SD_UserPost"
)
public class UserPost  implements java.io.Serializable {


     private Long id;
     private Long chNotificationId;
     private String note;
     private Long createUser;
     private Date createDate;
     private Long eId;
     private DeclarationForContract pdDeclarationId_EN;
     private Long pdDeclarationId;
     private Long rowStatus;

    public UserPost() {
    }

	
    public UserPost(Long id) {
        this.id = id;
    }
    public UserPost(Long id, Long chNotificationId, String note, Long createUser, Date createDate, Long eId, DeclarationForContract pdDeclarationId_EN, Long pdDeclarationId, Long rowStatus) {
       this.id = id;
       this.chNotificationId = chNotificationId;
       this.note = note;
       this.createUser = createUser;
       this.createDate = createDate;
       this.eId = eId;
       this.pdDeclarationId_EN = pdDeclarationId_EN;
       this.pdDeclarationId = pdDeclarationId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_USERPOST_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="CHNotificationID")
    public Long getChNotificationId() {
        return this.chNotificationId;
    }
    
    public void setChNotificationId(Long chNotificationId) {
        this.chNotificationId = chNotificationId;
    }

    
    @Column(name="Note")
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }

    
    @Column(name="CreateUser")
    public Long getCreateUser() {
        return this.createUser;
    }
    
    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CreateDate")
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="PDDeclarationID")
    public DeclarationForContract getPdDeclarationId_EN() {
        return this.pdDeclarationId_EN;
    }
    
    public void setPdDeclarationId_EN(DeclarationForContract pdDeclarationId_EN) {
        this.pdDeclarationId_EN = pdDeclarationId_EN;
    }

    
    @Column(name="PDDeclarationID", insertable=false, updatable=false)
    public Long getPdDeclarationId() {
        return this.pdDeclarationId;
    }
    
    public void setPdDeclarationId(Long pdDeclarationId) {
        this.pdDeclarationId = pdDeclarationId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


