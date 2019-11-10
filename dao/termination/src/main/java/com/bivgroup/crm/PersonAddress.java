package com.bivgroup.crm;
// Generated Sep 6, 2017 5:42:22 PM unknow unknow 


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * PersonAddress Generated Sep 6, 2017 5:42:22 PM unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "PersonAddress") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="CDM_Person_Address"
)
public class PersonAddress extends com.bivgroup.crm.Address implements java.io.Serializable {


     private Integer isPrimary;
     private Person personId_EN;
     private Long personId;
     private KindStatus stateId_EN;
     private Long stateId;

    public PersonAddress() {
    }

	
    public PersonAddress(Long id) {
        super(id);        
    }
    public PersonAddress(Long id, String streetCode, String street, String villageCode, String village, String cityCode, String city, String districtCode, String district, String regionCode, String region, Integer fillMethod, Long eId, String address2, String address, String postcode, String flat, String house, String isEmptyStreet, KindCountry countryId_EN, Long countryId, KindAddress typeId_EN, Long typeId, Long rowStatus, Integer isPrimary, Person personId_EN, Long personId, KindStatus stateId_EN, Long stateId) {
        super(id, streetCode, street, villageCode, village, cityCode, city, districtCode, district, regionCode, region, fillMethod, eId, address2, address, postcode, flat, house, isEmptyStreet, countryId_EN, countryId, typeId_EN, typeId, rowStatus);        
       this.isPrimary = isPrimary;
       this.personId_EN = personId_EN;
       this.personId = personId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
    }
   

    
    @Column(name="IsPrimary")
    public Integer getIsPrimary() {
        return this.isPrimary;
    }
    
    public void setIsPrimary(Integer isPrimary) {
        this.isPrimary = isPrimary;
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


