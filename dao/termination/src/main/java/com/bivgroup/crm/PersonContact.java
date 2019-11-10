package com.bivgroup.crm;
// Generated Sep 6, 2017 5:42:23 PM unknow unknow 


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * PersonContact Generated Sep 6, 2017 5:42:23 PM unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "PersonContact") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="CDM_Person_Contact"
)
public class PersonContact extends com.bivgroup.crm.Contact implements java.io.Serializable {


     private Person personId_EN;
     private Long personId;
     private KindStatus stateId_EN;
     private Long stateId;

    public PersonContact() {
    }

	
    public PersonContact(Long id) {
        super(id);        
    }
    public PersonContact(Long id, String value, Long eId, KindContact typeId_EN, Long typeId, Long rowStatus, Person personId_EN, Long personId, KindStatus stateId_EN, Long stateId) {
        super(id, value, eId, typeId_EN, typeId, rowStatus);        
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


