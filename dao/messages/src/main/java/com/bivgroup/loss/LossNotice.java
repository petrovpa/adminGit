package com.bivgroup.loss;
// Generated 14.12.2017 18:39:27 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.Client;
import com.bivgroup.crm.ClientProfile;
import com.bivgroup.crm.KindStatus;

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
 * LossNotice Generated 14.12.2017 18:39:27 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="B2B_LOSSNOTICE"
)
public class LossNotice  implements java.io.Serializable {


     private Long lossNoticeId;
     private String docFolder1C;
     private String applicantSurname;
     private String applicantPhone;
     private String applicantMiddleName;
     private Long contrId;
     private String applicantName;
     private String applicantEmail;
     private String address;
     private Date insuredBirthDate;
     private String insuredMiddleName;
     private String insuredSurname;
     private String insuredName;
     private String riskCategory;
     private String clause;
     private Long thirdPartyId;
     private Date regDate;
     private String externalId;
     private Date updateDate;
     private Long updateUserId;
     private Date createDate;
     private Long createUserId;
     private String eventCode;
     private Date eventDate;
     private Client insuredId_EN;
     private Long insuredId;
     private LossDamageCategory damageCatId_EN;
     private Long damageCatId;
     private LossEvent insEventId_EN;
     private Long insEventId;
     private KindStatus stateId_EN;
     private Long stateId;
     private ClientProfile applicantId_EN;
     private Long applicantId;
     private Long rowStatus;

    public LossNotice() {
    }

	
    public LossNotice(Long lossNoticeId) {
        this.lossNoticeId = lossNoticeId;
    }
    public LossNotice(Long lossNoticeId, String docFolder1C, String applicantSurname, String applicantPhone, String applicantMiddleName, Long contrId, String applicantName, String applicantEmail, String address, Date insuredBirthDate, String insuredMiddleName, String insuredSurname, String insuredName, String riskCategory, String clause, Long thirdPartyId, Date regDate, String externalId, Date updateDate, Long updateUserId, Date createDate, Long createUserId, String eventCode, Date eventDate, Client insuredId_EN, Long insuredId, LossDamageCategory damageCatId_EN, Long damageCatId, LossEvent insEventId_EN, Long insEventId, KindStatus stateId_EN, Long stateId, ClientProfile applicantId_EN, Long applicantId, Long rowStatus) {
       this.lossNoticeId = lossNoticeId;
       this.docFolder1C = docFolder1C;
       this.applicantSurname = applicantSurname;
       this.applicantPhone = applicantPhone;
       this.applicantMiddleName = applicantMiddleName;
       this.contrId = contrId;
       this.applicantName = applicantName;
       this.applicantEmail = applicantEmail;
       this.address = address;
       this.insuredBirthDate = insuredBirthDate;
       this.insuredMiddleName = insuredMiddleName;
       this.insuredSurname = insuredSurname;
       this.insuredName = insuredName;
       this.riskCategory = riskCategory;
       this.clause = clause;
       this.thirdPartyId = thirdPartyId;
       this.regDate = regDate;
       this.externalId = externalId;
       this.updateDate = updateDate;
       this.updateUserId = updateUserId;
       this.createDate = createDate;
       this.createUserId = createUserId;
       this.eventCode = eventCode;
       this.eventDate = eventDate;
       this.insuredId_EN = insuredId_EN;
       this.insuredId = insuredId;
       this.damageCatId_EN = damageCatId_EN;
       this.damageCatId = damageCatId;
       this.insEventId_EN = insEventId_EN;
       this.insEventId = insEventId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.applicantId_EN = applicantId_EN;
       this.applicantId = applicantId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "B2B_LOSSNOTICE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="lossNoticeId", nullable=false, insertable=false, updatable=false)
    public Long getLossNoticeId() {
        return this.lossNoticeId;
    }
    
    public void setLossNoticeId(Long lossNoticeId) {
        this.lossNoticeId = lossNoticeId;
    }

    
    @Column(name="docFolder1C")
    public String getDocFolder1C() {
        return this.docFolder1C;
    }
    
    public void setDocFolder1C(String docFolder1C) {
        this.docFolder1C = docFolder1C;
    }

    
    @Column(name="applicantSurname")
    public String getApplicantSurname() {
        return this.applicantSurname;
    }
    
    public void setApplicantSurname(String applicantSurname) {
        this.applicantSurname = applicantSurname;
    }

    
    @Column(name="applicantPhone")
    public String getApplicantPhone() {
        return this.applicantPhone;
    }
    
    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    
    @Column(name="applicantMiddleName")
    public String getApplicantMiddleName() {
        return this.applicantMiddleName;
    }
    
    public void setApplicantMiddleName(String applicantMiddleName) {
        this.applicantMiddleName = applicantMiddleName;
    }

    
    @Column(name="contrId")
    public Long getContrId() {
        return this.contrId;
    }
    
    public void setContrId(Long contrId) {
        this.contrId = contrId;
    }

    
    @Column(name="applicantName")
    public String getApplicantName() {
        return this.applicantName;
    }
    
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    
    @Column(name="applicantEmail")
    public String getApplicantEmail() {
        return this.applicantEmail;
    }
    
    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    
    @Column(name="address")
    public String getAddress() {
        return this.address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="insuredBirthDate")
    public Date getInsuredBirthDate() {
        return this.insuredBirthDate;
    }
    
    public void setInsuredBirthDate(Date insuredBirthDate) {
        this.insuredBirthDate = insuredBirthDate;
    }

    
    @Column(name="insuredMiddleName")
    public String getInsuredMiddleName() {
        return this.insuredMiddleName;
    }
    
    public void setInsuredMiddleName(String insuredMiddleName) {
        this.insuredMiddleName = insuredMiddleName;
    }

    
    @Column(name="insuredSurname")
    public String getInsuredSurname() {
        return this.insuredSurname;
    }
    
    public void setInsuredSurname(String insuredSurname) {
        this.insuredSurname = insuredSurname;
    }

    
    @Column(name="insuredName")
    public String getInsuredName() {
        return this.insuredName;
    }
    
    public void setInsuredName(String insuredName) {
        this.insuredName = insuredName;
    }

    
    @Column(name="riskCategory")
    public String getRiskCategory() {
        return this.riskCategory;
    }
    
    public void setRiskCategory(String riskCategory) {
        this.riskCategory = riskCategory;
    }

    
    @Column(name="clause")
    public String getClause() {
        return this.clause;
    }
    
    public void setClause(String clause) {
        this.clause = clause;
    }

    
    @Column(name="thirdPartyId")
    public Long getThirdPartyId() {
        return this.thirdPartyId;
    }
    
    public void setThirdPartyId(Long thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="regDate")
    public Date getRegDate() {
        return this.regDate;
    }
    
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    
    @Column(name="externalId")
    public String getExternalId() {
        return this.externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updateDate")
    public Date getUpdateDate() {
        return this.updateDate;
    }
    
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    
    @Column(name="updateUserId")
    public Long getUpdateUserId() {
        return this.updateUserId;
    }
    
    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createDate")
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    
    @Column(name="createUserId")
    public Long getCreateUserId() {
        return this.createUserId;
    }
    
    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    
    @Column(name="eventCode")
    public String getEventCode() {
        return this.eventCode;
    }
    
    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="eventDate")
    public Date getEventDate() {
        return this.eventDate;
    }
    
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="insuredId")
    public Client getInsuredId_EN() {
        return this.insuredId_EN;
    }
    
    public void setInsuredId_EN(Client insuredId_EN) {
        this.insuredId_EN = insuredId_EN;
    }

    
    @Column(name="insuredId", insertable=false, updatable=false)
    public Long getInsuredId() {
        return this.insuredId;
    }
    
    public void setInsuredId(Long insuredId) {
        this.insuredId = insuredId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="damageCatId")
    public LossDamageCategory getDamageCatId_EN() {
        return this.damageCatId_EN;
    }
    
    public void setDamageCatId_EN(LossDamageCategory damageCatId_EN) {
        this.damageCatId_EN = damageCatId_EN;
    }

    
    @Column(name="damageCatId", insertable=false, updatable=false)
    public Long getDamageCatId() {
        return this.damageCatId;
    }
    
    public void setDamageCatId(Long damageCatId) {
        this.damageCatId = damageCatId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="insEventId")
    public LossEvent getInsEventId_EN() {
        return this.insEventId_EN;
    }
    
    public void setInsEventId_EN(LossEvent insEventId_EN) {
        this.insEventId_EN = insEventId_EN;
    }

    
    @Column(name="insEventId", insertable=false, updatable=false)
    public Long getInsEventId() {
        return this.insEventId;
    }
    
    public void setInsEventId(Long insEventId) {
        this.insEventId = insEventId;
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

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="applicantId")
    public ClientProfile getApplicantId_EN() {
        return this.applicantId_EN;
    }
    
    public void setApplicantId_EN(ClientProfile applicantId_EN) {
        this.applicantId_EN = applicantId_EN;
    }

    
    @Column(name="applicantId", insertable=false, updatable=false)
    public Long getApplicantId() {
        return this.applicantId;
    }
    
    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


