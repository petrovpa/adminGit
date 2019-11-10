package com.bivgroup.crm;
// Generated 14.12.2017 18:39:15 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
import javax.persistence.Transient;

/**
 * Person Generated 14.12.2017 18:39:15 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)       
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="CDM_Person"
)
public class Person  implements java.io.Serializable {


     private Long id;
     private String innOther;
     private String taxResidentCountry;
     private Long isTaxResidentOther;
     private Long isTaxResidentUsa;
     private String innUsa;
     private Long eId;
     private BankDetails bankDetailsId_EN;
     private Long bankDetailsId;
     private Client clientId_EN;
     private Long clientId;
     private KindStatus stateId_EN;
     private Long stateId;
     private KindCountry countryId_EN;
     private Long countryId;
     private Set<PersonDocument> documents = new HashSet<PersonDocument>(0);
     private Set<PersonAddress> addresses = new HashSet<PersonAddress>(0);
     private Set<PersonContact> contacts = new HashSet<PersonContact>(0);
     private Set<BankDetails> bankDetailsList = new HashSet<BankDetails>(0);
     private Long rowStatus;

    public Person() {
    }

	
    public Person(Long id) {
        this.id = id;
    }
    public Person(Long id, String innOther, String taxResidentCountry, Long isTaxResidentOther, Long isTaxResidentUsa, String innUsa, Long eId, BankDetails bankDetailsId_EN, Long bankDetailsId, Client clientId_EN, Long clientId, KindStatus stateId_EN, Long stateId, KindCountry countryId_EN, Long countryId, Set<PersonDocument> documents, Set<PersonAddress> addresses, Set<PersonContact> contacts, Set<BankDetails> bankDetailsList, Long rowStatus) {
       this.id = id;
       this.innOther = innOther;
       this.taxResidentCountry = taxResidentCountry;
       this.isTaxResidentOther = isTaxResidentOther;
       this.isTaxResidentUsa = isTaxResidentUsa;
       this.innUsa = innUsa;
       this.eId = eId;
       this.bankDetailsId_EN = bankDetailsId_EN;
       this.bankDetailsId = bankDetailsId;
       this.clientId_EN = clientId_EN;
       this.clientId = clientId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.countryId_EN = countryId_EN;
       this.countryId = countryId;
       this.documents = documents;
       this.addresses = addresses;
       this.contacts = contacts;
       this.bankDetailsList = bankDetailsList;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CDM_PERSON_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="innOther")
    public String getInnOther() {
        return this.innOther;
    }
    
    public void setInnOther(String innOther) {
        this.innOther = innOther;
    }

    
    @Column(name="taxResidentCountry")
    public String getTaxResidentCountry() {
        return this.taxResidentCountry;
    }
    
    public void setTaxResidentCountry(String taxResidentCountry) {
        this.taxResidentCountry = taxResidentCountry;
    }

    
    @Column(name="isTaxResidentOther")
    public Long getIsTaxResidentOther() {
        return this.isTaxResidentOther;
    }
    
    public void setIsTaxResidentOther(Long isTaxResidentOther) {
        this.isTaxResidentOther = isTaxResidentOther;
    }

    
    @Column(name="isTaxResidentUsa")
    public Long getIsTaxResidentUsa() {
        return this.isTaxResidentUsa;
    }
    
    public void setIsTaxResidentUsa(Long isTaxResidentUsa) {
        this.isTaxResidentUsa = isTaxResidentUsa;
    }

    
    @Column(name="innUsa")
    public String getInnUsa() {
        return this.innUsa;
    }
    
    public void setInnUsa(String innUsa) {
        this.innUsa = innUsa;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="BankDetailsID")
    public BankDetails getBankDetailsId_EN() {
        return this.bankDetailsId_EN;
    }
    
    public void setBankDetailsId_EN(BankDetails bankDetailsId_EN) {
        this.bankDetailsId_EN = bankDetailsId_EN;
    }

    
    @Column(name="BankDetailsID", insertable=false, updatable=false)
    public Long getBankDetailsId() {
        return this.bankDetailsId;
    }
    
    public void setBankDetailsId(Long bankDetailsId) {
        this.bankDetailsId = bankDetailsId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ClientID")
    public Client getClientId_EN() {
        return this.clientId_EN;
    }
    
    public void setClientId_EN(Client clientId_EN) {
        this.clientId_EN = clientId_EN;
    }

    
    @Column(name="ClientID", insertable=false, updatable=false)
    public Long getClientId() {
        return this.clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
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

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="personId_EN")
    public Set<PersonDocument> getDocuments() {
        return this.documents;
    }
    
    public void setDocuments(Set<PersonDocument> documents) {
        this.documents = documents;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="personId_EN")
    public Set<PersonAddress> getAddresses() {
        return this.addresses;
    }
    
    public void setAddresses(Set<PersonAddress> addresses) {
        this.addresses = addresses;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="personId_EN")
    public Set<PersonContact> getContacts() {
        return this.contacts;
    }
    
    public void setContacts(Set<PersonContact> contacts) {
        this.contacts = contacts;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="personId_EN")
    public Set<BankDetails> getBankDetailsList() {
        return this.bankDetailsList;
    }
    
    public void setBankDetailsList(Set<BankDetails> bankDetailsList) {
        this.bankDetailsList = bankDetailsList;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


