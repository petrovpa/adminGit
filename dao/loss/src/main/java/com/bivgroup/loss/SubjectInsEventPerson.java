package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:24 AM unknow unknow 


import com.bivgroup.crm.Address;
import com.bivgroup.crm.ContractPMember;
import com.bivgroup.crm.Document;
import com.bivgroup.crm.KindCountry;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * SubjectInsEventPerson Generated Aug 7, 2017 10:42:24 AM unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "SubjectInsEventPerson") 
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id") 
@Table(name="CH_SUBJECTINSEVENT_PERSON"
)
public class SubjectInsEventPerson extends com.bivgroup.loss.SubjectInsEvent implements java.io.Serializable {


     private Long category;
     private String surname;
     private String name;
     private String patronymic;
     private Integer isEmptyPatronymic;
     private Date dateOfBirth;
     private String inn;
     private Long sex;
     private KindCountry countryId_EN;
     private Long countryId;
     private Document documentId_EN;
     private Long documentId;
     private Address addressId_EN;
     private Long addressId;
     private ContractPMember contractMemberId_EN;
     private Long contractMemberId;
     private SubjectInsEventCar transportId_EN;
     private Long transportId;

    public SubjectInsEventPerson() {
    }

	
    public SubjectInsEventPerson(Long id) {
        super(id);        
    }
    public SubjectInsEventPerson(Long id, Integer isNoData, Long roleOnInsEvent, ClaimHandlingInsEvent insEventId_EN, Long insEventId, SubjectInsEvent sourceSubjectId_EN, Long sourceSubjectId, OtherInsCompany otherInsCompanyId_EN, Long otherInsCompanyId, Long rowStatus, Long category, String surname, String name, String patronymic, Integer isEmptyPatronymic, Date dateOfBirth, String inn, Long sex, KindCountry countryId_EN, Long countryId, Document documentId_EN, Long documentId, Address addressId_EN, Long addressId, ContractPMember contractMemberId_EN, Long contractMemberId, SubjectInsEventCar transportId_EN, Long transportId) {
        super(id, isNoData, roleOnInsEvent, insEventId_EN, insEventId, sourceSubjectId_EN, sourceSubjectId, otherInsCompanyId_EN, otherInsCompanyId, rowStatus);        
       this.category = category;
       this.surname = surname;
       this.name = name;
       this.patronymic = patronymic;
       this.isEmptyPatronymic = isEmptyPatronymic;
       this.dateOfBirth = dateOfBirth;
       this.inn = inn;
       this.sex = sex;
       this.countryId_EN = countryId_EN;
       this.countryId = countryId;
       this.documentId_EN = documentId_EN;
       this.documentId = documentId;
       this.addressId_EN = addressId_EN;
       this.addressId = addressId;
       this.contractMemberId_EN = contractMemberId_EN;
       this.contractMemberId = contractMemberId;
       this.transportId_EN = transportId_EN;
       this.transportId = transportId;
    }
   

    
    @Column(name="category")
    public Long getCategory() {
        return this.category;
    }
    
    public void setCategory(Long category) {
        this.category = category;
    }

    
    @Column(name="surname")
    public String getSurname() {
        return this.surname;
    }
    
    public void setSurname(String surname) {
        this.surname = surname;
    }

    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="patronymic")
    public String getPatronymic() {
        return this.patronymic;
    }
    
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    
    @Column(name="isEmptyPatronymic")
    public Integer getIsEmptyPatronymic() {
        return this.isEmptyPatronymic;
    }
    
    public void setIsEmptyPatronymic(Integer isEmptyPatronymic) {
        this.isEmptyPatronymic = isEmptyPatronymic;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dateOfBirth")
    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    
    @Column(name="inn")
    public String getInn() {
        return this.inn;
    }
    
    public void setInn(String inn) {
        this.inn = inn;
    }

    
    @Column(name="sex")
    public Long getSex() {
        return this.sex;
    }
    
    public void setSex(Long sex) {
        this.sex = sex;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="countryId")
    public KindCountry getCountryId_EN() {
        return this.countryId_EN;
    }
    
    public void setCountryId_EN(KindCountry countryId_EN) {
        this.countryId_EN = countryId_EN;
    }

    
    @Column(name="countryId", insertable=false, updatable=false)
    public Long getCountryId() {
        return this.countryId;
    }
    
    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="documentId")
    public Document getDocumentId_EN() {
        return this.documentId_EN;
    }
    
    public void setDocumentId_EN(Document documentId_EN) {
        this.documentId_EN = documentId_EN;
    }

    
    @Column(name="documentId", insertable=false, updatable=false)
    public Long getDocumentId() {
        return this.documentId;
    }
    
    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="addressId")
    public Address getAddressId_EN() {
        return this.addressId_EN;
    }
    
    public void setAddressId_EN(Address addressId_EN) {
        this.addressId_EN = addressId_EN;
    }

    
    @Column(name="addressId", insertable=false, updatable=false)
    public Long getAddressId() {
        return this.addressId;
    }
    
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="contractMemberId")
    public ContractPMember getContractMemberId_EN() {
        return this.contractMemberId_EN;
    }
    
    public void setContractMemberId_EN(ContractPMember contractMemberId_EN) {
        this.contractMemberId_EN = contractMemberId_EN;
    }

    
    @Column(name="contractMemberId", insertable=false, updatable=false)
    public Long getContractMemberId() {
        return this.contractMemberId;
    }
    
    public void setContractMemberId(Long contractMemberId) {
        this.contractMemberId = contractMemberId;
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




}


