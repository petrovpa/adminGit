package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:23 AM unknow unknow 


import com.bivgroup.crm.Address;
import com.bivgroup.crm.KindStatus;
import com.bivgroup.crm.PPerson;
import com.bivgroup.crm.Person;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * ClaimHandlingNotification Generated Aug 7, 2017 10:42:23 AM unknow unknow 
 */
@Entity
@Table(name="CH_NOTIFICATION"
)
public class ClaimHandlingNotification  implements java.io.Serializable {


     private Long id;
     private Long eId;
     private Date createDate;
     private Long createUser;
     private Date updateDate;
     private Long updateUser;
     private Date dateLastDoc;
     private Long contractId;
     private Date eventDate;
     private String eventNote;
     private String declarationNumber;
     private Date declarationDate;
     private Long typeApplicant;
     private Long typeRecipient;
     private Date departureDate;
     private Long receivingChannelId;
     private String incomingNumber;
     private Date incomingDate;
     private Date receivingDate;
     private Integer isInsuredOtherInsCompany;
     private Integer isComplaintThirdParty;
     private Date travelBeginDate;
     private Date travelEndDate;
     private Date handlingDate;
     private Integer isHandlingService;
     private String causeNoHandling;
     private KindStatus stateId_EN;
     private Long stateId;
     private ClaimHandlingInsEvent insEventId_EN;
     private Long insEventId;
     private ClaimHandlingMessageInsEvent messageInsEventId_EN;
     private Long messageInsEventId;
     private Address eventAddressId_EN;
     private Long eventAddressId;
     private CategoryOfDamageOnInsProduct categoryOfDamageId_EN;
     private Long categoryOfDamageId;
     private Person applicantId_EN;
     private Long applicantId;
     private PPerson representativeId_EN;
     private Long representativeId;
     private Person recipientId_EN;
     private Long recipientId;
     private SubjectInsEventCar transportId_EN;
     private Long transportId;
     private SubjectInsEventPerson victimId_EN;
     private Long victimId;
     private SubjectInsEventPerson driverId_EN;
     private Long driverId;
     private SubjectInsEventPerson perpetratorId_EN;
     private Long perpetratorId;
     private OtherInsCompany otherInsCompanyId_EN;
     private Long otherInsCompanyId;
     private RepairShopCar repairShopId_EN;
     private Long repairShopId;
     private Set<CompetentOrg_Notification> competentOrganizations = new HashSet<CompetentOrg_Notification>(0);
     private Long rowStatus;

    public ClaimHandlingNotification() {
    }

	
    public ClaimHandlingNotification(Long id) {
        this.id = id;
    }
    public ClaimHandlingNotification(Long id, Long eId, Date createDate, Long createUser, Date updateDate, Long updateUser, Date dateLastDoc, Long contractId, Date eventDate, String eventNote, String declarationNumber, Date declarationDate, Long typeApplicant, Long typeRecipient, Date departureDate, Long receivingChannelId, String incomingNumber, Date incomingDate, Date receivingDate, Integer isInsuredOtherInsCompany, Integer isComplaintThirdParty, Date travelBeginDate, Date travelEndDate, Date handlingDate, Integer isHandlingService, String causeNoHandling, KindStatus stateId_EN, Long stateId, ClaimHandlingInsEvent insEventId_EN, Long insEventId, ClaimHandlingMessageInsEvent messageInsEventId_EN, Long messageInsEventId, Address eventAddressId_EN, Long eventAddressId, CategoryOfDamageOnInsProduct categoryOfDamageId_EN, Long categoryOfDamageId, Person applicantId_EN, Long applicantId, PPerson representativeId_EN, Long representativeId, Person recipientId_EN, Long recipientId, SubjectInsEventCar transportId_EN, Long transportId, SubjectInsEventPerson victimId_EN, Long victimId, SubjectInsEventPerson driverId_EN, Long driverId, SubjectInsEventPerson perpetratorId_EN, Long perpetratorId, OtherInsCompany otherInsCompanyId_EN, Long otherInsCompanyId, RepairShopCar repairShopId_EN, Long repairShopId, Set<CompetentOrg_Notification> competentOrganizations, Long rowStatus) {
       this.id = id;
       this.eId = eId;
       this.createDate = createDate;
       this.createUser = createUser;
       this.updateDate = updateDate;
       this.updateUser = updateUser;
       this.dateLastDoc = dateLastDoc;
       this.contractId = contractId;
       this.eventDate = eventDate;
       this.eventNote = eventNote;
       this.declarationNumber = declarationNumber;
       this.declarationDate = declarationDate;
       this.typeApplicant = typeApplicant;
       this.typeRecipient = typeRecipient;
       this.departureDate = departureDate;
       this.receivingChannelId = receivingChannelId;
       this.incomingNumber = incomingNumber;
       this.incomingDate = incomingDate;
       this.receivingDate = receivingDate;
       this.isInsuredOtherInsCompany = isInsuredOtherInsCompany;
       this.isComplaintThirdParty = isComplaintThirdParty;
       this.travelBeginDate = travelBeginDate;
       this.travelEndDate = travelEndDate;
       this.handlingDate = handlingDate;
       this.isHandlingService = isHandlingService;
       this.causeNoHandling = causeNoHandling;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.insEventId_EN = insEventId_EN;
       this.insEventId = insEventId;
       this.messageInsEventId_EN = messageInsEventId_EN;
       this.messageInsEventId = messageInsEventId;
       this.eventAddressId_EN = eventAddressId_EN;
       this.eventAddressId = eventAddressId;
       this.categoryOfDamageId_EN = categoryOfDamageId_EN;
       this.categoryOfDamageId = categoryOfDamageId;
       this.applicantId_EN = applicantId_EN;
       this.applicantId = applicantId;
       this.representativeId_EN = representativeId_EN;
       this.representativeId = representativeId;
       this.recipientId_EN = recipientId_EN;
       this.recipientId = recipientId;
       this.transportId_EN = transportId_EN;
       this.transportId = transportId;
       this.victimId_EN = victimId_EN;
       this.victimId = victimId;
       this.driverId_EN = driverId_EN;
       this.driverId = driverId;
       this.perpetratorId_EN = perpetratorId_EN;
       this.perpetratorId = perpetratorId;
       this.otherInsCompanyId_EN = otherInsCompanyId_EN;
       this.otherInsCompanyId = otherInsCompanyId;
       this.repairShopId_EN = repairShopId_EN;
       this.repairShopId = repairShopId;
       this.competentOrganizations = competentOrganizations;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CH_NOTIFICATION_SEQ", allocationSize = 10)
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createDate")
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    
    @Column(name="createUser")
    public Long getCreateUser() {
        return this.createUser;
    }
    
    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updateDate")
    public Date getUpdateDate() {
        return this.updateDate;
    }
    
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    
    @Column(name="updateUser")
    public Long getUpdateUser() {
        return this.updateUser;
    }
    
    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dateLastDoc")
    public Date getDateLastDoc() {
        return this.dateLastDoc;
    }
    
    public void setDateLastDoc(Date dateLastDoc) {
        this.dateLastDoc = dateLastDoc;
    }

    
    @Column(name="contractId")
    public Long getContractId() {
        return this.contractId;
    }
    
    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="eventDate")
    public Date getEventDate() {
        return this.eventDate;
    }
    
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    
    @Column(name="eventNote")
    public String getEventNote() {
        return this.eventNote;
    }
    
    public void setEventNote(String eventNote) {
        this.eventNote = eventNote;
    }

    
    @Column(name="declarationNumber")
    public String getDeclarationNumber() {
        return this.declarationNumber;
    }
    
    public void setDeclarationNumber(String declarationNumber) {
        this.declarationNumber = declarationNumber;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="declarationDate")
    public Date getDeclarationDate() {
        return this.declarationDate;
    }
    
    public void setDeclarationDate(Date declarationDate) {
        this.declarationDate = declarationDate;
    }

    
    @Column(name="typeApplicant")
    public Long getTypeApplicant() {
        return this.typeApplicant;
    }
    
    public void setTypeApplicant(Long typeApplicant) {
        this.typeApplicant = typeApplicant;
    }

    
    @Column(name="typeRecipient")
    public Long getTypeRecipient() {
        return this.typeRecipient;
    }
    
    public void setTypeRecipient(Long typeRecipient) {
        this.typeRecipient = typeRecipient;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="departureDate")
    public Date getDepartureDate() {
        return this.departureDate;
    }
    
    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    
    @Column(name="receivingChannelId")
    public Long getReceivingChannelId() {
        return this.receivingChannelId;
    }
    
    public void setReceivingChannelId(Long receivingChannelId) {
        this.receivingChannelId = receivingChannelId;
    }

    
    @Column(name="incomingNumber")
    public String getIncomingNumber() {
        return this.incomingNumber;
    }
    
    public void setIncomingNumber(String incomingNumber) {
        this.incomingNumber = incomingNumber;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="incomingDate")
    public Date getIncomingDate() {
        return this.incomingDate;
    }
    
    public void setIncomingDate(Date incomingDate) {
        this.incomingDate = incomingDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="receivingDate")
    public Date getReceivingDate() {
        return this.receivingDate;
    }
    
    public void setReceivingDate(Date receivingDate) {
        this.receivingDate = receivingDate;
    }

    
    @Column(name="isInsuredOtherInsCompany")
    public Integer getIsInsuredOtherInsCompany() {
        return this.isInsuredOtherInsCompany;
    }
    
    public void setIsInsuredOtherInsCompany(Integer isInsuredOtherInsCompany) {
        this.isInsuredOtherInsCompany = isInsuredOtherInsCompany;
    }

    
    @Column(name="isComplaintThirdParty")
    public Integer getIsComplaintThirdParty() {
        return this.isComplaintThirdParty;
    }
    
    public void setIsComplaintThirdParty(Integer isComplaintThirdParty) {
        this.isComplaintThirdParty = isComplaintThirdParty;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="travelBeginDate")
    public Date getTravelBeginDate() {
        return this.travelBeginDate;
    }
    
    public void setTravelBeginDate(Date travelBeginDate) {
        this.travelBeginDate = travelBeginDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="travelEndDate")
    public Date getTravelEndDate() {
        return this.travelEndDate;
    }
    
    public void setTravelEndDate(Date travelEndDate) {
        this.travelEndDate = travelEndDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="handlingDate")
    public Date getHandlingDate() {
        return this.handlingDate;
    }
    
    public void setHandlingDate(Date handlingDate) {
        this.handlingDate = handlingDate;
    }

    
    @Column(name="isHandlingService")
    public Integer getIsHandlingService() {
        return this.isHandlingService;
    }
    
    public void setIsHandlingService(Integer isHandlingService) {
        this.isHandlingService = isHandlingService;
    }

    
    @Column(name="causeNoHandling")
    public String getCauseNoHandling() {
        return this.causeNoHandling;
    }
    
    public void setCauseNoHandling(String causeNoHandling) {
        this.causeNoHandling = causeNoHandling;
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
    @JoinColumn(name="insEventId")
    public ClaimHandlingInsEvent getInsEventId_EN() {
        return this.insEventId_EN;
    }
    
    public void setInsEventId_EN(ClaimHandlingInsEvent insEventId_EN) {
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
    @JoinColumn(name="messageInsEventId")
    public ClaimHandlingMessageInsEvent getMessageInsEventId_EN() {
        return this.messageInsEventId_EN;
    }
    
    public void setMessageInsEventId_EN(ClaimHandlingMessageInsEvent messageInsEventId_EN) {
        this.messageInsEventId_EN = messageInsEventId_EN;
    }

    
    @Column(name="messageInsEventId", insertable=false, updatable=false)
    public Long getMessageInsEventId() {
        return this.messageInsEventId;
    }
    
    public void setMessageInsEventId(Long messageInsEventId) {
        this.messageInsEventId = messageInsEventId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="eventAddressId")
    public Address getEventAddressId_EN() {
        return this.eventAddressId_EN;
    }
    
    public void setEventAddressId_EN(Address eventAddressId_EN) {
        this.eventAddressId_EN = eventAddressId_EN;
    }

    
    @Column(name="eventAddressId", insertable=false, updatable=false)
    public Long getEventAddressId() {
        return this.eventAddressId;
    }
    
    public void setEventAddressId(Long eventAddressId) {
        this.eventAddressId = eventAddressId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="categoryOfDamageId")
    public CategoryOfDamageOnInsProduct getCategoryOfDamageId_EN() {
        return this.categoryOfDamageId_EN;
    }
    
    public void setCategoryOfDamageId_EN(CategoryOfDamageOnInsProduct categoryOfDamageId_EN) {
        this.categoryOfDamageId_EN = categoryOfDamageId_EN;
    }

    
    @Column(name="categoryOfDamageId", insertable=false, updatable=false)
    public Long getCategoryOfDamageId() {
        return this.categoryOfDamageId;
    }
    
    public void setCategoryOfDamageId(Long categoryOfDamageId) {
        this.categoryOfDamageId = categoryOfDamageId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="applicantId")
    public Person getApplicantId_EN() {
        return this.applicantId_EN;
    }
    
    public void setApplicantId_EN(Person applicantId_EN) {
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
    @JoinColumn(name="representativeId")
    public PPerson getRepresentativeId_EN() {
        return this.representativeId_EN;
    }
    
    public void setRepresentativeId_EN(PPerson representativeId_EN) {
        this.representativeId_EN = representativeId_EN;
    }

    
    @Column(name="representativeId", insertable=false, updatable=false)
    public Long getRepresentativeId() {
        return this.representativeId;
    }
    
    public void setRepresentativeId(Long representativeId) {
        this.representativeId = representativeId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="recipientId")
    public Person getRecipientId_EN() {
        return this.recipientId_EN;
    }
    
    public void setRecipientId_EN(Person recipientId_EN) {
        this.recipientId_EN = recipientId_EN;
    }

    
    @Column(name="recipientId", insertable=false, updatable=false)
    public Long getRecipientId() {
        return this.recipientId;
    }
    
    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="transportId")
    public SubjectInsEventCar getTransportId_EN() {
        return this.transportId_EN;
    }
    
    public void setTransportId_EN(SubjectInsEventCar transportId_EN) {
        this.transportId_EN = transportId_EN;
    }

    
    @Column(name="transportId", insertable=false, updatable=false)
    public Long getTransportId() {
        return this.transportId;
    }
    
    public void setTransportId(Long transportId) {
        this.transportId = transportId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="victimId")
    public SubjectInsEventPerson getVictimId_EN() {
        return this.victimId_EN;
    }
    
    public void setVictimId_EN(SubjectInsEventPerson victimId_EN) {
        this.victimId_EN = victimId_EN;
    }

    
    @Column(name="victimId", insertable=false, updatable=false)
    public Long getVictimId() {
        return this.victimId;
    }
    
    public void setVictimId(Long victimId) {
        this.victimId = victimId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="driverId")
    public SubjectInsEventPerson getDriverId_EN() {
        return this.driverId_EN;
    }
    
    public void setDriverId_EN(SubjectInsEventPerson driverId_EN) {
        this.driverId_EN = driverId_EN;
    }

    
    @Column(name="driverId", insertable=false, updatable=false)
    public Long getDriverId() {
        return this.driverId;
    }
    
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="perpetratorId")
    public SubjectInsEventPerson getPerpetratorId_EN() {
        return this.perpetratorId_EN;
    }
    
    public void setPerpetratorId_EN(SubjectInsEventPerson perpetratorId_EN) {
        this.perpetratorId_EN = perpetratorId_EN;
    }

    
    @Column(name="perpetratorId", insertable=false, updatable=false)
    public Long getPerpetratorId() {
        return this.perpetratorId;
    }
    
    public void setPerpetratorId(Long perpetratorId) {
        this.perpetratorId = perpetratorId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="otherInsCompanyId")
    public OtherInsCompany getOtherInsCompanyId_EN() {
        return this.otherInsCompanyId_EN;
    }
    
    public void setOtherInsCompanyId_EN(OtherInsCompany otherInsCompanyId_EN) {
        this.otherInsCompanyId_EN = otherInsCompanyId_EN;
    }

    
    @Column(name="otherInsCompanyId", insertable=false, updatable=false)
    public Long getOtherInsCompanyId() {
        return this.otherInsCompanyId;
    }
    
    public void setOtherInsCompanyId(Long otherInsCompanyId) {
        this.otherInsCompanyId = otherInsCompanyId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="repairShopId")
    public RepairShopCar getRepairShopId_EN() {
        return this.repairShopId_EN;
    }
    
    public void setRepairShopId_EN(RepairShopCar repairShopId_EN) {
        this.repairShopId_EN = repairShopId_EN;
    }

    
    @Column(name="repairShopId", insertable=false, updatable=false)
    public Long getRepairShopId() {
        return this.repairShopId;
    }
    
    public void setRepairShopId(Long repairShopId) {
        this.repairShopId = repairShopId;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="notificationId_EN")
    public Set<CompetentOrg_Notification> getCompetentOrganizations() {
        return this.competentOrganizations;
    }
    
    public void setCompetentOrganizations(Set<CompetentOrg_Notification> competentOrganizations) {
        this.competentOrganizations = competentOrganizations;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


