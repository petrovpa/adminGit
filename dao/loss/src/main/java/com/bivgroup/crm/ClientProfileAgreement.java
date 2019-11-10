package com.bivgroup.crm;
// Generated Dec 5, 2017 4:10:29 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * ClientProfileAgreement Generated Dec 5, 2017 4:10:29 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="SD_CLIENTPROFILE_AGREEMENT"
)
public class ClientProfileAgreement  implements java.io.Serializable {


     private Long id;
     private Date agreementDate;
     private String agreementNumber;
     private Client clientId_EN;
     private Long clientId;
     private KindAgreementClientProfile typeAgreementId_EN;
     private Long typeAgreementId;
     private KindStatus stateId_EN;
     private Long stateId;
     private Long rowStatus;

    public ClientProfileAgreement() {
    }

	
    public ClientProfileAgreement(Long id) {
        this.id = id;
    }
    public ClientProfileAgreement(Long id, Date agreementDate, String agreementNumber, Client clientId_EN, Long clientId, KindAgreementClientProfile typeAgreementId_EN, Long typeAgreementId, KindStatus stateId_EN, Long stateId, Long rowStatus) {
       this.id = id;
       this.agreementDate = agreementDate;
       this.agreementNumber = agreementNumber;
       this.clientId_EN = clientId_EN;
       this.clientId = clientId;
       this.typeAgreementId_EN = typeAgreementId_EN;
       this.typeAgreementId = typeAgreementId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_CLIENTPROFILE_AGREEMENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="agreementDate")
    public Date getAgreementDate() {
        return this.agreementDate;
    }
    
    public void setAgreementDate(Date agreementDate) {
        this.agreementDate = agreementDate;
    }

    
    @Column(name="agreementNumber")
    public String getAgreementNumber() {
        return this.agreementNumber;
    }
    
    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="CLIENTID")
    public Client getClientId_EN() {
        return this.clientId_EN;
    }
    
    public void setClientId_EN(Client clientId_EN) {
        this.clientId_EN = clientId_EN;
    }

    
    @Column(name="CLIENTID", insertable=false, updatable=false)
    public Long getClientId() {
        return this.clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="typeAgreementId")
    public KindAgreementClientProfile getTypeAgreementId_EN() {
        return this.typeAgreementId_EN;
    }
    
    public void setTypeAgreementId_EN(KindAgreementClientProfile typeAgreementId_EN) {
        this.typeAgreementId_EN = typeAgreementId_EN;
    }

    
    @Column(name="typeAgreementId", insertable=false, updatable=false)
    public Long getTypeAgreementId() {
        return this.typeAgreementId;
    }
    
    public void setTypeAgreementId(Long typeAgreementId) {
        this.typeAgreementId = typeAgreementId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="stateId")
    public KindStatus getStateId_EN() {
        return this.stateId_EN;
    }
    
    public void setStateId_EN(KindStatus stateId_EN) {
        this.stateId_EN = stateId_EN;
    }

    
    @Column(name="stateId", insertable=false, updatable=false)
    public Long getStateId() {
        return this.stateId;
    }
    
    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


