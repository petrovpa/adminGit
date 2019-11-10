package com.bivgroup.crm;
// Generated 17.11.2017 17:19:50 unknow unknow 


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
 * JPerson Generated 17.11.2017 17:19:50 unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "JPerson") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="CDM_JPerson"
)
public class JPerson extends com.bivgroup.crm.Person implements java.io.Serializable {


     private String ogrn;
     private String kpp;
     private String inn;
     private String name2;
     private String name;
     private PPerson firstPersonId_EN;
     private Long firstPersonId;
     private LegalFormsOfBusiness legalFormsOfBusinessId_EN;
     private Long legalFormsOfBusinessId;

    public JPerson() {
    }

	
    public JPerson(Long id) {
        super(id);        
    }
    public JPerson(Long id, String innOther, String taxResidentCountry, Long isTaxResidentOther, Long isTaxResidentUsa, String innUsa, Long eId, BankDetails bankDetailsId_EN, Long bankDetailsId, Client clientId_EN, Long clientId, KindStatus stateId_EN, Long stateId, KindCountry countryId_EN, Long countryId, Set<PersonDocument> documents, Set<PersonAddress> addresses, Set<PersonContact> contacts, Set<BankDetails> bankDetailsList, Long rowStatus, String ogrn, String kpp, String inn, String name2, String name, PPerson firstPersonId_EN, Long firstPersonId, LegalFormsOfBusiness legalFormsOfBusinessId_EN, Long legalFormsOfBusinessId) {
        super(id, innOther, taxResidentCountry, isTaxResidentOther, isTaxResidentUsa, innUsa, eId, bankDetailsId_EN, bankDetailsId, clientId_EN, clientId, stateId_EN, stateId, countryId_EN, countryId, documents, addresses, contacts, bankDetailsList, rowStatus);        
       this.ogrn = ogrn;
       this.kpp = kpp;
       this.inn = inn;
       this.name2 = name2;
       this.name = name;
       this.firstPersonId_EN = firstPersonId_EN;
       this.firstPersonId = firstPersonId;
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


