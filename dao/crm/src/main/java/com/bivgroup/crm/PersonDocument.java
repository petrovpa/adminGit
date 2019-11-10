package com.bivgroup.crm;
// Generated 31.01.2018 18:17:48 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * PersonDocument Generated 31.01.2018 18:17:48 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "PersonDocument") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="CDM_Person_Document"
)
public class PersonDocument extends com.bivgroup.crm.Document implements java.io.Serializable {


     private Person personId_EN;
     private Long personId;
     private KindStatus stateId_EN;
     private Long stateId;

    public PersonDocument() {
    }

	
    public PersonDocument(Long id) {
        super(id);        
    }
    public PersonDocument(Long id, Date dateStayUntil, Date dateStayFrom, Date dateOfExpiry, String issuerCode, String authority, Date dateOfIssue, String no, String series, Long eId, KindDocument typeId_EN, Long typeId, Long rowStatus, Person personId_EN, Long personId, KindStatus stateId_EN, Long stateId) {
        super(id, dateStayUntil, dateStayFrom, dateOfExpiry, issuerCode, authority, dateOfIssue, no, series, eId, typeId_EN, typeId, rowStatus);        
       this.personId_EN = personId_EN;
       this.personId = personId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
    }
   

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="PersonID")
    public Person getPersonId_EN() {
        return this.personId_EN;
    }
    
    public void setPersonId_EN(Person personId_EN) {
        this.personId_EN = personId_EN;
    }

    
    @Column(name="PersonID", insertable=false, updatable=false)
    public Long getPersonId() {
        return this.personId;
    }
    
    public void setPersonId(Long personId) {
        this.personId = personId;
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




}


