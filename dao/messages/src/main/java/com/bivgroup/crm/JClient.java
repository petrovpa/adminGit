package com.bivgroup.crm;
// Generated 14.12.2017 18:39:24 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * JClient Generated 14.12.2017 18:39:24 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "JClient") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="CDM_JClient"
)
public class JClient extends com.bivgroup.crm.Client implements java.io.Serializable {


     private Long verLock;
     private JClient_VER verLastId_EN;
     private Long verLastId;

    public JClient() {
    }

	
    public JClient(Long id) {
        super(id);        
    }
    public JClient(Long id, Long eId, ClientManager clientManagerId_EN, Long clientManagerId, Set<ClientAddress> addresses, Set<ClientDocument> documents, Set<ClientContact> contacts, Set<Person> persons, Set<ClientBankDetails> bankDetails, Long rowStatus, Long verLock, JClient_VER verLastId_EN, Long verLastId) {
        super(id, eId, clientManagerId_EN, clientManagerId, addresses, documents, contacts, persons, bankDetails, rowStatus);        
       this.verLock = verLock;
       this.verLastId_EN = verLastId_EN;
       this.verLastId = verLastId;
    }
   

    
    @Column(name="VerLock")
    public Long getVerLock() {
        return this.verLock;
    }
    
    public void setVerLock(Long verLock) {
        this.verLock = verLock;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="VerLastID")
    public JClient_VER getVerLastId_EN() {
        return this.verLastId_EN;
    }
    
    public void setVerLastId_EN(JClient_VER verLastId_EN) {
        this.verLastId_EN = verLastId_EN;
    }

    
    @Column(name="VerLastID", insertable=false, updatable=false)
    public Long getVerLastId() {
        return this.verLastId;
    }
    
    public void setVerLastId(Long verLastId) {
        this.verLastId = verLastId;
    }




}


