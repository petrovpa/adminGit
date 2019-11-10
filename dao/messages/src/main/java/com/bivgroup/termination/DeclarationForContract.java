package com.bivgroup.termination;
// Generated 20.11.2017 12:47:07 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.core.annotation.IsArray;
import com.bivgroup.crm.KindStatus;
import com.bivgroup.crm.PPerson;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * DeclarationForContract Generated 20.11.2017 12:47:07 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)       
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="PD_Declaration"
)
public class DeclarationForContract  implements java.io.Serializable {


     private Long id;
     private Date contractDate;
     private String docFolder1C;
     private String externalId;
     private String note;
     private Integer isExistOriginal;
     private Long eId;
     private Date incomingDate;
     private Integer typeRecipient;
     private Date dateOfEntry;
     private Float changePremValue;
     private Date receivingDate;
     private String incomingNumber;
     private Date departureDate;
     private Date supposedDateOfEntry;
     private Integer initiator;
     private Date declarationDate;
     private String declarationNumber;
     private Long contractId;
     private Date dateLastDoc;
     private Long updateUser;
     private Date updateDate;
     private Long createUser;
     private Date createDate;
     private PPerson recipientId_EN;
     private Long recipientId;
     private PPerson representativeId_EN;
     private Long representativeId;
     private PPerson applicantId_EN;
     private Long applicantId;
     private DeclarationForContract parentId_EN;
     private Long parentId;
     private KindStatus stateId_EN;
     private Long stateId;
     private ReceivingChannel receivingChannelId_EN;
     private Long receivingChannelId;
     private Set<UserPost> notes = new HashSet<UserPost>(0);
     private Set<DeclarationForContract> subDeclarations = new HashSet<DeclarationForContract>(0);
     private Long rowStatus;

    public DeclarationForContract() {
    }

	
    public DeclarationForContract(Long id) {
        this.id = id;
    }
    public DeclarationForContract(Long id, Date contractDate, String docFolder1C, String externalId, String note, Integer isExistOriginal, Long eId, Date incomingDate, Integer typeRecipient, Date dateOfEntry, Float changePremValue, Date receivingDate, String incomingNumber, Date departureDate, Date supposedDateOfEntry, Integer initiator, Date declarationDate, String declarationNumber, Long contractId, Date dateLastDoc, Long updateUser, Date updateDate, Long createUser, Date createDate, PPerson recipientId_EN, Long recipientId, PPerson representativeId_EN, Long representativeId, PPerson applicantId_EN, Long applicantId, DeclarationForContract parentId_EN, Long parentId, KindStatus stateId_EN, Long stateId, ReceivingChannel receivingChannelId_EN, Long receivingChannelId, Set<UserPost> notes, Set<DeclarationForContract> subDeclarations, Long rowStatus) {
       this.id = id;
       this.contractDate = contractDate;
       this.docFolder1C = docFolder1C;
       this.externalId = externalId;
       this.note = note;
       this.isExistOriginal = isExistOriginal;
       this.eId = eId;
       this.incomingDate = incomingDate;
       this.typeRecipient = typeRecipient;
       this.dateOfEntry = dateOfEntry;
       this.changePremValue = changePremValue;
       this.receivingDate = receivingDate;
       this.incomingNumber = incomingNumber;
       this.departureDate = departureDate;
       this.supposedDateOfEntry = supposedDateOfEntry;
       this.initiator = initiator;
       this.declarationDate = declarationDate;
       this.declarationNumber = declarationNumber;
       this.contractId = contractId;
       this.dateLastDoc = dateLastDoc;
       this.updateUser = updateUser;
       this.updateDate = updateDate;
       this.createUser = createUser;
       this.createDate = createDate;
       this.recipientId_EN = recipientId_EN;
       this.recipientId = recipientId;
       this.representativeId_EN = representativeId_EN;
       this.representativeId = representativeId;
       this.applicantId_EN = applicantId_EN;
       this.applicantId = applicantId;
       this.parentId_EN = parentId_EN;
       this.parentId = parentId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.receivingChannelId_EN = receivingChannelId_EN;
       this.receivingChannelId = receivingChannelId;
       this.notes = notes;
       this.subDeclarations = subDeclarations;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "PD_DECLARATION_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="contractDate")
    public Date getContractDate() {
        return this.contractDate;
    }
    
    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    
    @Column(name="docFolder1C")
    public String getDocFolder1C() {
        return this.docFolder1C;
    }
    
    public void setDocFolder1C(String docFolder1C) {
        this.docFolder1C = docFolder1C;
    }

    
    @Column(name="externalId")
    public String getExternalId() {
        return this.externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    
    @Column(name="note")
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }

    
    @Column(name="IsExistOriginal")
    public Integer getIsExistOriginal() {
        return this.isExistOriginal;
    }
    
    public void setIsExistOriginal(Integer isExistOriginal) {
        this.isExistOriginal = isExistOriginal;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="IncomingDate")
    public Date getIncomingDate() {
        return this.incomingDate;
    }
    
    public void setIncomingDate(Date incomingDate) {
        this.incomingDate = incomingDate;
    }

    
    @Column(name="TypeRecipient")
    public Integer getTypeRecipient() {
        return this.typeRecipient;
    }
    
    public void setTypeRecipient(Integer typeRecipient) {
        this.typeRecipient = typeRecipient;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DateOfEntry")
    public Date getDateOfEntry() {
        return this.dateOfEntry;
    }
    
    public void setDateOfEntry(Date dateOfEntry) {
        this.dateOfEntry = dateOfEntry;
    }

    
    @Column(name="ChangePremValue")
    public Float getChangePremValue() {
        return this.changePremValue;
    }
    
    public void setChangePremValue(Float changePremValue) {
        this.changePremValue = changePremValue;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ReceivingDate")
    public Date getReceivingDate() {
        return this.receivingDate;
    }
    
    public void setReceivingDate(Date receivingDate) {
        this.receivingDate = receivingDate;
    }

    
    @Column(name="IncomingNumber")
    public String getIncomingNumber() {
        return this.incomingNumber;
    }
    
    public void setIncomingNumber(String incomingNumber) {
        this.incomingNumber = incomingNumber;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DepartureDate")
    public Date getDepartureDate() {
        return this.departureDate;
    }
    
    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="SupposedDateOfEntry")
    public Date getSupposedDateOfEntry() {
        return this.supposedDateOfEntry;
    }
    
    public void setSupposedDateOfEntry(Date supposedDateOfEntry) {
        this.supposedDateOfEntry = supposedDateOfEntry;
    }

    
    @Column(name="Initiator")
    public Integer getInitiator() {
        return this.initiator;
    }
    
    public void setInitiator(Integer initiator) {
        this.initiator = initiator;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DeclarationDate")
    public Date getDeclarationDate() {
        return this.declarationDate;
    }
    
    public void setDeclarationDate(Date declarationDate) {
        this.declarationDate = declarationDate;
    }

    
    @Column(name="DeclarationNumber")
    public String getDeclarationNumber() {
        return this.declarationNumber;
    }
    
    public void setDeclarationNumber(String declarationNumber) {
        this.declarationNumber = declarationNumber;
    }

    
    @Column(name="ContractID")
    public Long getContractId() {
        return this.contractId;
    }
    
    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DateLastDoc")
    public Date getDateLastDoc() {
        return this.dateLastDoc;
    }
    
    public void setDateLastDoc(Date dateLastDoc) {
        this.dateLastDoc = dateLastDoc;
    }

    
    @Column(name="UpdateUser")
    public Long getUpdateUser() {
        return this.updateUser;
    }
    
    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="UpdateDate")
    public Date getUpdateDate() {
        return this.updateDate;
    }
    
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
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

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="RecipientID")
    public PPerson getRecipientId_EN() {
        return this.recipientId_EN;
    }
    
    public void setRecipientId_EN(PPerson recipientId_EN) {
        this.recipientId_EN = recipientId_EN;
    }

    
    @Column(name="RecipientID", insertable=false, updatable=false)
    public Long getRecipientId() {
        return this.recipientId;
    }
    
    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="RepresentativeID")
    public PPerson getRepresentativeId_EN() {
        return this.representativeId_EN;
    }
    
    public void setRepresentativeId_EN(PPerson representativeId_EN) {
        this.representativeId_EN = representativeId_EN;
    }

    
    @Column(name="RepresentativeID", insertable=false, updatable=false)
    public Long getRepresentativeId() {
        return this.representativeId;
    }
    
    public void setRepresentativeId(Long representativeId) {
        this.representativeId = representativeId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ApplicantID")
    public PPerson getApplicantId_EN() {
        return this.applicantId_EN;
    }
    
    public void setApplicantId_EN(PPerson applicantId_EN) {
        this.applicantId_EN = applicantId_EN;
    }

    
    @Column(name="ApplicantID", insertable=false, updatable=false)
    public Long getApplicantId() {
        return this.applicantId;
    }
    
    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="parentId")
   @IsArray
    public DeclarationForContract getParentId_EN() {
        return this.parentId_EN;
    }
    
    public void setParentId_EN(DeclarationForContract parentId_EN) {
        this.parentId_EN = parentId_EN;
    }

    
    @Column(name="parentId", insertable=false, updatable=false)
    public Long getParentId() {
        return this.parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="StateID")
    public KindStatus getStateId_EN() {
        return this.stateId_EN;
    }
    
    public void setStateId_EN(KindStatus stateId_EN) {
        this.stateId_EN = stateId_EN;
    }

    
    @Column(name="StateID", insertable=false, updatable=false)
    public Long getStateId() {
        return this.stateId;
    }
    
    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ReceivingChannelID")
    public ReceivingChannel getReceivingChannelId_EN() {
        return this.receivingChannelId_EN;
    }
    
    public void setReceivingChannelId_EN(ReceivingChannel receivingChannelId_EN) {
        this.receivingChannelId_EN = receivingChannelId_EN;
    }

    
    @Column(name="ReceivingChannelID", insertable=false, updatable=false)
    public Long getReceivingChannelId() {
        return this.receivingChannelId;
    }
    
    public void setReceivingChannelId(Long receivingChannelId) {
        this.receivingChannelId = receivingChannelId;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="pdDeclarationId_EN")
    public Set<UserPost> getNotes() {
        return this.notes;
    }
    
    public void setNotes(Set<UserPost> notes) {
        this.notes = notes;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="parentId_EN")
    public Set<DeclarationForContract> getSubDeclarations() {
        return this.subDeclarations;
    }
    
    public void setSubDeclarations(Set<DeclarationForContract> subDeclarations) {
        this.subDeclarations = subDeclarations;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


