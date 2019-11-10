package com.bivgroup.crm;
// Generated 14.12.2017 18:39:16 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * Client Generated 14.12.2017 18:39:16 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)       
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="CDM_Client"
)
public class Client  implements java.io.Serializable {


     private Long id;
     private Long eId;
     private ClientManager clientManagerId_EN;
     private Long clientManagerId;
     private Set<ClientAddress> addresses = new HashSet<ClientAddress>(0);
     private Set<ClientDocument> documents = new HashSet<ClientDocument>(0);
     private Set<ClientContact> contacts = new HashSet<ClientContact>(0);
     private Set<Person> persons = new HashSet<Person>(0);
     private Set<ClientBankDetails> bankDetails = new HashSet<ClientBankDetails>(0);
     private Long rowStatus;

    public Client() {
    }

	
    public Client(Long id) {
        this.id = id;
    }
    public Client(Long id, Long eId, ClientManager clientManagerId_EN, Long clientManagerId, Set<ClientAddress> addresses, Set<ClientDocument> documents, Set<ClientContact> contacts, Set<Person> persons, Set<ClientBankDetails> bankDetails, Long rowStatus) {
       this.id = id;
       this.eId = eId;
       this.clientManagerId_EN = clientManagerId_EN;
       this.clientManagerId = clientManagerId;
       this.addresses = addresses;
       this.documents = documents;
       this.contacts = contacts;
       this.persons = persons;
       this.bankDetails = bankDetails;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CDM_CLIENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="clientManagerId")
    public ClientManager getClientManagerId_EN() {
        return this.clientManagerId_EN;
    }
    
    public void setClientManagerId_EN(ClientManager clientManagerId_EN) {
        this.clientManagerId_EN = clientManagerId_EN;
    }

    
    @Column(name="clientManagerId", insertable=false, updatable=false)
    public Long getClientManagerId() {
        return this.clientManagerId;
    }
    
    public void setClientManagerId(Long clientManagerId) {
        this.clientManagerId = clientManagerId;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="clientId_EN")
    public Set<ClientAddress> getAddresses() {
        return this.addresses;
    }
    
    public void setAddresses(Set<ClientAddress> addresses) {
        this.addresses = addresses;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="clientId_EN")
    public Set<ClientDocument> getDocuments() {
        return this.documents;
    }
    
    public void setDocuments(Set<ClientDocument> documents) {
        this.documents = documents;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="clientId_EN")
    public Set<ClientContact> getContacts() {
        return this.contacts;
    }
    
    public void setContacts(Set<ClientContact> contacts) {
        this.contacts = contacts;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="clientId_EN")
    public Set<Person> getPersons() {
        return this.persons;
    }
    
    public void setPersons(Set<Person> persons) {
        this.persons = persons;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="clientId_EN")
    public Set<ClientBankDetails> getBankDetails() {
        return this.bankDetails;
    }
    
    public void setBankDetails(Set<ClientBankDetails> bankDetails) {
        this.bankDetails = bankDetails;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


