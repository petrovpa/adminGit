package com.bivgroup.crm;
// Generated 14.12.2017 18:39:25 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * EClient_VER Generated 14.12.2017 18:39:25 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "EClient_VER") 
@PrimaryKeyJoinColumn(name = "VerID", referencedColumnName = "id") 
@Table(name="CDM_EClient_VER"
)
public class EClient_VER extends com.bivgroup.crm.EClient implements java.io.Serializable {


     private String ogrnIP;
     private String inn;
     private Long sex;
     private String placeOfBirth;
     private Date dateOfBirth;
     private Integer isEmptyPatronymic;
     private String patronymic;
     private String name;
     private String surname;
     private Date verDate;
     private Long verNumber;
     private KindCountry countryId_EN;
     private Long countryId;
     private LegalFormsOfBusiness legalFormsOfBusinessId_EN;
     private Long legalFormsOfBusinessId;

    public EClient_VER() {
    }

	
    public EClient_VER(Long id) {
        super(id);        
    }
    public EClient_VER(Long id, Long eId, ClientManager clientManagerId_EN, Long clientManagerId, Set<ClientAddress> addresses, Set<ClientDocument> documents, Set<ClientContact> contacts, Set<Person> persons, Set<ClientBankDetails> bankDetails, Long rowStatus, Long verLock, EClient_VER verLastId_EN, Long verLastId, String ogrnIP, String inn, Long sex, String placeOfBirth, Date dateOfBirth, Integer isEmptyPatronymic, String patronymic, String name, String surname, Date verDate, Long verNumber, KindCountry countryId_EN, Long countryId, LegalFormsOfBusiness legalFormsOfBusinessId_EN, Long legalFormsOfBusinessId) {
        super(id, eId, clientManagerId_EN, clientManagerId, addresses, documents, contacts, persons, bankDetails, rowStatus, verLock, verLastId_EN, verLastId);        
       this.ogrnIP = ogrnIP;
       this.inn = inn;
       this.sex = sex;
       this.placeOfBirth = placeOfBirth;
       this.dateOfBirth = dateOfBirth;
       this.isEmptyPatronymic = isEmptyPatronymic;
       this.patronymic = patronymic;
       this.name = name;
       this.surname = surname;
       this.verDate = verDate;
       this.verNumber = verNumber;
       this.countryId_EN = countryId_EN;
       this.countryId = countryId;
       this.legalFormsOfBusinessId_EN = legalFormsOfBusinessId_EN;
       this.legalFormsOfBusinessId = legalFormsOfBusinessId;
    }
   

    
    @Column(name="OGRNIP")
    public String getOgrnIP() {
        return this.ogrnIP;
    }
    
    public void setOgrnIP(String ogrnIP) {
        this.ogrnIP = ogrnIP;
    }

    
    @Column(name="INN")
    public String getInn() {
        return this.inn;
    }
    
    public void setInn(String inn) {
        this.inn = inn;
    }

    
    @Column(name="Sex")
    public Long getSex() {
        return this.sex;
    }
    
    public void setSex(Long sex) {
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

    
    @Column(name="Patronymic")
    public String getPatronymic() {
        return this.patronymic;
    }
    
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    
    @Column(name="Name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
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

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="LegalFormsOfBusinessID")
    public LegalFormsOfBusiness getLegalFormsOfBusinessId_EN() {
        return this.legalFormsOfBusinessId_EN;
    }
    
    public void setLegalFormsOfBusinessId_EN(LegalFormsOfBusiness legalFormsOfBusinessId_EN) {
        this.legalFormsOfBusinessId_EN = legalFormsOfBusinessId_EN;
    }

    
    @Column(name="LegalFormsOfBusinessID", insertable=false, updatable=false)
    public Long getLegalFormsOfBusinessId() {
        return this.legalFormsOfBusinessId;
    }
    
    public void setLegalFormsOfBusinessId(Long legalFormsOfBusinessId) {
        this.legalFormsOfBusinessId = legalFormsOfBusinessId;
    }




}


