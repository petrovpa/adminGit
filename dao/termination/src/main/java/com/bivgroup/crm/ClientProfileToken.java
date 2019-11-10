package com.bivgroup.crm;
// Generated 28.05.2018 12:28:36 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * ClientProfileToken Generated 28.05.2018 12:28:36 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="SD_ClientProfile_Token"
)
public class ClientProfileToken  implements java.io.Serializable {


     private Long id;
     private String discriminator;
     private Integer isNotUsed;
     private String idDevice;
     private String hash;
     private Date createDate;
     private Date dateOfExpiry;
     private Long eId;
     private ClientProfile clientProfileId_EN;
     private Long clientProfileId;
     private Long rowStatus;

    public ClientProfileToken() {
    }

	
    public ClientProfileToken(Long id) {
        this.id = id;
    }
    public ClientProfileToken(Long id, String discriminator, Integer isNotUsed, String idDevice, String hash, Date createDate, Date dateOfExpiry, Long eId, ClientProfile clientProfileId_EN, Long clientProfileId, Long rowStatus) {
       this.id = id;
       this.discriminator = discriminator;
       this.isNotUsed = isNotUsed;
       this.idDevice = idDevice;
       this.hash = hash;
       this.createDate = createDate;
       this.dateOfExpiry = dateOfExpiry;
       this.eId = eId;
       this.clientProfileId_EN = clientProfileId_EN;
       this.clientProfileId = clientProfileId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_CLIENTPROFILE_TOKEN_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="discriminator")
    public String getDiscriminator() {
        return this.discriminator;
    }
    
    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    
    @Column(name="IsNotUsed")
    public Integer getIsNotUsed() {
        return this.isNotUsed;
    }
    
    public void setIsNotUsed(Integer isNotUsed) {
        this.isNotUsed = isNotUsed;
    }

    
    @Column(name="IDDevice")
    public String getIdDevice() {
        return this.idDevice;
    }
    
    public void setIdDevice(String idDevice) {
        this.idDevice = idDevice;
    }

    
    @Column(name="Hash")
    public String getHash() {
        return this.hash;
    }
    
    public void setHash(String hash) {
        this.hash = hash;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CreateDate")
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DateOfExpiry")
    public Date getDateOfExpiry() {
        return this.dateOfExpiry;
    }
    
    public void setDateOfExpiry(Date dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ClientProfileID")
    public ClientProfile getClientProfileId_EN() {
        return this.clientProfileId_EN;
    }
    
    public void setClientProfileId_EN(ClientProfile clientProfileId_EN) {
        this.clientProfileId_EN = clientProfileId_EN;
    }

    
    @Column(name="ClientProfileID", insertable=false, updatable=false)
    public Long getClientProfileId() {
        return this.clientProfileId;
    }
    
    public void setClientProfileId(Long clientProfileId) {
        this.clientProfileId = clientProfileId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


