package com.bivgroup.crm;
// Generated 17.11.2017 17:19:49 unknow unknow 


import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * PPerson Generated 17.11.2017 17:19:49 unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "PPerson") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="CDM_PPerson"
)
public class PPerson extends com.bivgroup.crm.Person implements java.io.Serializable {


     private String residencePermit;
     private Long isResidencePermit;
     private String countryOfBirth;
     private String snils;
     private Long kinshipId;
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
     private Set<PPersonChild> childs = new HashSet<PPersonChild>(0);

    public PPerson() {
    }

	
    public PPerson(Long id) {
        super(id);        
    }
    public PPerson(Long id, String innOther, String taxResidentCountry, Long isTaxResidentOther, Long isTaxResidentUsa, String innUsa, Long eId, BankDetails bankDetailsId_EN, Long bankDetailsId, Client clientId_EN, Long clientId, KindStatus stateId_EN, Long stateId, KindCountry countryId_EN, Long countryId, Set<PersonDocument> documents, Set<PersonAddress> addresses, Set<PersonContact> contacts, Set<BankDetails> bankDetailsList, Long rowStatus, String residencePermit, Long isResidencePermit, String countryOfBirth, String snils, Long kinshipId, Integer isMarried, String inn, Integer sex, String placeOfBirth, Date dateOfBirth, Integer isEmptyPatronymic, String patronymic2, String patronymic, String name2, String name, String surname2, String surname, Set<PPersonChild> childs) {
        super(id, innOther, taxResidentCountry, isTaxResidentOther, isTaxResidentUsa, innUsa, eId, bankDetailsId_EN, bankDetailsId, clientId_EN, clientId, stateId_EN, stateId, countryId_EN, countryId, documents, addresses, contacts, bankDetailsList, rowStatus);        
       this.residencePermit = residencePermit;
       this.isResidencePermit = isResidencePermit;
       this.countryOfBirth = countryOfBirth;
       this.snils = snils;
       this.kinshipId = kinshipId;
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
       this.childs = childs;
    }
   

    
    @Column(name="residencePermit")
    public String getResidencePermit() {
        return this.residencePermit;
    }
    
    public void setResidencePermit(String residencePermit) {
        this.residencePermit = residencePermit;
    }

    
    @Column(name="isResidencePermit")
    public Long getIsResidencePermit() {
        return this.isResidencePermit;
    }
    
    public void setIsResidencePermit(Long isResidencePermit) {
        this.isResidencePermit = isResidencePermit;
    }

    
    @Column(name="countryOfBirth")
    public String getCountryOfBirth() {
        return this.countryOfBirth;
    }
    
    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    
    @Column(name="snils")
    public String getSnils() {
        return this.snils;
    }
    
    public void setSnils(String snils) {
        this.snils = snils;
    }

    
    @Column(name="kinshipId")
    public Long getKinshipId() {
        return this.kinshipId;
    }
    
    public void setKinshipId(Long kinshipId) {
        this.kinshipId = kinshipId;
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

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="personId_EN")
    public Set<PPersonChild> getChilds() {
        return this.childs;
    }
    
    public void setChilds(Set<PPersonChild> childs) {
        this.childs = childs;
    }




}


