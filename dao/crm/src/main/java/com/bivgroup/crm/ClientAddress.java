package com.bivgroup.crm;
// Generated Sep 6, 2017 5:42:20 PM unknow unknow 


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * ClientAddress Generated Sep 6, 2017 5:42:20 PM unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "ClientAddress") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="CDM_Client_Address"
)
public class ClientAddress extends com.bivgroup.crm.Address implements java.io.Serializable {


     private Integer isPrimary;
     private Client clientId_EN;
     private Long clientId;
     private KindStatus stateId_EN;
     private Long stateId;

    public ClientAddress() {
    }

	
    public ClientAddress(Long id) {
        super(id);        
    }
    public ClientAddress(Long id, String streetCode, String street, String villageCode, String village, String cityCode, String city, String districtCode, String district, String regionCode, String region, Integer fillMethod, Long eId, String address2, String address, String postcode, String flat, String house, String isEmptyStreet, KindCountry countryId_EN, Long countryId, KindAddress typeId_EN, Long typeId, Long rowStatus, Integer isPrimary, Client clientId_EN, Long clientId, KindStatus stateId_EN, Long stateId) {
        super(id, streetCode, street, villageCode, village, cityCode, city, districtCode, district, regionCode, region, fillMethod, eId, address2, address, postcode, flat, house, isEmptyStreet, countryId_EN, countryId, typeId_EN, typeId, rowStatus);        
       this.isPrimary = isPrimary;
       this.clientId_EN = clientId_EN;
       this.clientId = clientId;
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




}


