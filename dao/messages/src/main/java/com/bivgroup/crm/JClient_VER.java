package com.bivgroup.crm;
// Generated 14.12.2017 18:39:24 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * JClient_VER Generated 14.12.2017 18:39:24 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "JClient_VER") 
@PrimaryKeyJoinColumn(name = "VerID", referencedColumnName = "id") 
@Table(name="CDM_JClient_VER"
)
public class JClient_VER extends com.bivgroup.crm.JClient implements java.io.Serializable {


     private String ogrn;
     private String kpp;
     private String inn;
     private String name2;
     private String name;
     private Date verDate;
     private Long verNumber;
     private PPerson firstPersonId_EN;
     private Long firstPersonId;
     private KindCountry countryId_EN;
     private Long countryId;
     private LegalFormsOfBusiness legalFormsOfBusinessId_EN;
     private Long legalFormsOfBusinessId;

    public JClient_VER() {
    }

	
    public JClient_VER(Long id) {
        super(id);        
    }
    public JClient_VER(Long id, Long eId, ClientManager clientManagerId_EN, Long clientManagerId, Set<ClientAddress> addresses, Set<ClientDocument> documents, Set<ClientContact> contacts, Set<Person> persons, Set<ClientBankDetails> bankDetails, Long rowStatus, Long verLock, JClient_VER verLastId_EN, Long verLastId, String ogrn, String kpp, String inn, String name2, String name, Date verDate, Long verNumber, PPerson firstPersonId_EN, Long firstPersonId, KindCountry countryId_EN, Long countryId, LegalFormsOfBusiness legalFormsOfBusinessId_EN, Long legalFormsOfBusinessId) {
        super(id, eId, clientManagerId_EN, clientManagerId, addresses, documents, contacts, persons, bankDetails, rowStatus, verLock, verLastId_EN, verLastId);        
       this.ogrn = ogrn;
       this.kpp = kpp;
       this.inn = inn;
       this.name2 = name2;
       this.name = name;
       this.verDate = verDate;
       this.verNumber = verNumber;
       this.firstPersonId_EN = firstPersonId_EN;
       this.firstPersonId = firstPersonId;
       this.countryId_EN = countryId_EN;
       this.countryId = countryId;
       this.legalFormsOfBusinessId_EN = legalFormsOfBusinessId_EN;
       this.legalFormsOfBusinessId = legalFormsOfBusinessId;
    }
   

    
    @Column(name="OGRN")
    public String getOgrn() {
        return this.ogrn;
    }
    
    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    
    @Column(name="KPP")
    public String getKpp() {
        return this.kpp;
    }
    
    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    
    @Column(name="INN")
    public String getInn() {
        return this.inn;
    }
    
    public void setInn(String inn) {
        this.inn = inn;
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
    @JoinColumn(name="FirstPersonID")
    public PPerson getFirstPersonId_EN() {
        return this.firstPersonId_EN;
    }
    
    public void setFirstPersonId_EN(PPerson firstPersonId_EN) {
        this.firstPersonId_EN = firstPersonId_EN;
    }

    
    @Column(name="FirstPersonID", insertable=false, updatable=false)
    public Long getFirstPersonId() {
        return this.firstPersonId;
    }
    
    public void setFirstPersonId(Long firstPersonId) {
        this.firstPersonId = firstPersonId;
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


