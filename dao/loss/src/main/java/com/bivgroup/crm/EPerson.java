package com.bivgroup.crm;
// Generated 17.11.2017 17:20:23 unknow unknow 


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

/**
 * EPerson Generated 17.11.2017 17:20:23 unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "EPerson") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="CDM_EPerson"
)
public class EPerson extends com.bivgroup.crm.Person implements java.io.Serializable {


     private String ogrnIP;
     private String inn;
     private Long sex;
     private Date placeOfBirth;
     private Date dateOfBirth;
     private Integer isEmptyPatronymic;
     private String patronymic;
     private String name;
     private String surname;
     private LegalFormsOfBusiness legalFormsOfBusinessId_EN;
     private Long legalFormsOfBusinessId;

    public EPerson() {
    }

	
    public EPerson(Long id) {
        super(id);        
    }
    public EPerson(Long id, String innOther, String taxResidentCountry, Long isTaxResidentOther, Long isTaxResidentUsa, String innUsa, Long eId, BankDetails bankDetailsId_EN, Long bankDetailsId, Client clientId_EN, Long clientId, KindStatus stateId_EN, Long stateId, KindCountry countryId_EN, Long countryId, Set<PersonDocument> documents, Set<PersonAddress> addresses, Set<PersonContact> contacts, Set<BankDetails> bankDetailsList, Long rowStatus, String ogrnIP, String inn, Long sex, Date placeOfBirth, Date dateOfBirth, Integer isEmptyPatronymic, String patronymic, String name, String surname, LegalFormsOfBusiness legalFormsOfBusinessId_EN, Long legalFormsOfBusinessId) {
        super(id, innOther, taxResidentCountry, isTaxResidentOther, isTaxResidentUsa, innUsa, eId, bankDetailsId_EN, bankDetailsId, clientId_EN, clientId, stateId_EN, stateId, countryId_EN, countryId, documents, addresses, contacts, bankDetailsList, rowStatus);        
       this.ogrnIP = ogrnIP;
       this.inn = inn;
       this.sex = sex;
       this.placeOfBirth = placeOfBirth;
       this.dateOfBirth = dateOfBirth;
       this.isEmptyPatronymic = isEmptyPatronymic;
       this.patronymic = patronymic;
       this.name = name;
       this.surname = surname;
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
    public Date getPlaceOfBirth() {
        return this.placeOfBirth;
    }
    
    public void setPlaceOfBirth(Date placeOfBirth) {
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


