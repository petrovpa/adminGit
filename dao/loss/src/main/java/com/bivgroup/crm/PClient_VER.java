package com.bivgroup.crm;
// Generated 17.01.2018 10:32:38 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import java.util.Set;
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
 * PClient_VER Generated 17.01.2018 10:32:38 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "PClient_VER") 
@PrimaryKeyJoinColumn(name = "VerID", referencedColumnName = "id") 
@Table(name="CDM_PClient_VER"
)
public class PClient_VER extends com.bivgroup.crm.PClient implements java.io.Serializable {


     private String fullName;
     private String externalHashCode;
     private String pprbId;
     private String externalId;
     private Integer isMarried;
     private String inn;
     private Integer sex;
     private String placeOfBirth;
     private Date dateOfBirth;
     private Integer isEmptyPatronymic;
     private String patronymic2;
     private String patronymic;
     private String name2;
     private String name;
     private String surname2;
     private String surname;
     private Date verDate;
     private Long verNumber;
     private KindCountry countryId_EN;
     private Long countryId;

    public PClient_VER() {
    }

	
    public PClient_VER(Long id) {
        super(id);        
    }
    public PClient_VER(Long id, Long eId, ClientManager clientManagerId_EN, Long clientManagerId, Set<ClientAddress> addresses, Set<ClientDocument> documents, Set<ClientContact> contacts, Set<Person> persons, Set<ClientBankDetails> bankDetails, Long rowStatus, Long verLock, PClient_VER verLastId_EN, Long verLastId, String fullName, String externalHashCode, String pprbId, String externalId, Integer isMarried, String inn, Integer sex, String placeOfBirth, Date dateOfBirth, Integer isEmptyPatronymic, String patronymic2, String patronymic, String name2, String name, String surname2, String surname, Date verDate, Long verNumber, KindCountry countryId_EN, Long countryId) {
        super(id, eId, clientManagerId_EN, clientManagerId, addresses, documents, contacts, persons, bankDetails, rowStatus, verLock, verLastId_EN, verLastId);        
       this.fullName = fullName;
       this.externalHashCode = externalHashCode;
       this.pprbId = pprbId;
       this.externalId = externalId;
       this.isMarried = isMarried;
       this.inn = inn;
       this.sex = sex;
       this.placeOfBirth = placeOfBirth;
       this.dateOfBirth = dateOfBirth;
       this.isEmptyPatronymic = isEmptyPatronymic;
       this.patronymic2 = patronymic2;
       this.patronymic = patronymic;
       this.name2 = name2;
       this.name = name;
       this.surname2 = surname2;
       this.surname = surname;
       this.verDate = verDate;
       this.verNumber = verNumber;
       this.countryId_EN = countryId_EN;
       this.countryId = countryId;
    }
   

    
    @Column(name="Fullname")
    public String getFullName() {
        return this.fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    
    @Column(name="externalHashCode")
    public String getExternalHashCode() {
        return this.externalHashCode;
    }
    
    public void setExternalHashCode(String externalHashCode) {
        this.externalHashCode = externalHashCode;
    }

    
    @Column(name="pprbId")
    public String getPprbId() {
        return this.pprbId;
    }
    
    public void setPprbId(String pprbId) {
        this.pprbId = pprbId;
    }

    
    @Column(name="ExternalId")
    public String getExternalId() {
        return this.externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    
    @Column(name="IsMarried")
    public Integer getIsMarried() {
        return this.isMarried;
    }
    
    public void setIsMarried(Integer isMarried) {
        this.isMarried = isMarried;
    }

    
    @Column(name="INN")
    public String getInn() {
        return this.inn;
    }
    
    public void setInn(String inn) {
        this.inn = inn;
    }

    
    @Column(name="Sex")
    public Integer getSex() {
        return this.sex;
    }
    
    public void setSex(Integer sex) {
        this.sex = sex;
    }

    
    @Column(name="PlaceOfBirth")
    public String getPlaceOfBirth() {
        return this.placeOfBirth;
    }
    
    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    
    @Column(name="DateOfBirth")
    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }
    
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    
    @Column(name="IsEmptyPatronymic")
    public Integer getIsEmptyPatronymic() {
        return this.isEmptyPatronymic;
    }
    
    public void setIsEmptyPatronymic(Integer isEmptyPatronymic) {
        this.isEmptyPatronymic = isEmptyPatronymic;
    }

    
    @Column(name="Patronymic2")
    public String getPatronymic2() {
        return this.patronymic2;
    }
    
    public void setPatronymic2(String patronymic2) {
        this.patronymic2 = patronymic2;
    }

    
    @Column(name="Patronymic")
    public String getPatronymic() {
        return this.patronymic;
    }
    
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    
    @Column(name="Name2")
    public String getName2() {
        return this.name2;
    }
    
    public void setName2(String name2) {
        this.name2 = name2;
    }

    
    @Column(name="Name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="Surname2")
    public String getSurname2() {
        return this.surname2;
    }
    
    public void setSurname2(String surname2) {
        this.surname2 = surname2;
    }

    
    @Column(name="Surname")
    public String getSurname() {
        return this.surname;
    }
    
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="VerDate")
    public Date getVerDate() {
        return this.verDate;
    }
    
    public void setVerDate(Date verDate) {
        this.verDate = verDate;
    }

    
    @Column(name="VerNumber")
    public Long getVerNumber() {
        return this.verNumber;
    }
    
    public void setVerNumber(Long verNumber) {
        this.verNumber = verNumber;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="CountryID")
    public KindCountry getCountryId_EN() {
        return this.countryId_EN;
    }
    
    public void setCountryId_EN(KindCountry countryId_EN) {
        this.countryId_EN = countryId_EN;
    }

    
    @Column(name="CountryID", insertable=false, updatable=false)
    public Long getCountryId() {
        return this.countryId;
    }
    
    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }




}


