package com.bivgroup.termination;
// Generated 16.03.2018 18:04:43 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.Address;
import com.bivgroup.crm.KindStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_BChangeAddress Generated 16.03.2018 18:04:43 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_BChangeAddress")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_BChangeAddress extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Long thirdPartyId;
     private Address addressId_EN;
     private Long addressId;

    public ReasonChangeForContract_BChangeAddress() {
    }

	
    public ReasonChangeForContract_BChangeAddress(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_BChangeAddress(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Long thirdPartyId, Address addressId_EN, Long addressId) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.thirdPartyId = thirdPartyId;
       this.addressId_EN = addressId_EN;
       this.addressId = addressId;
    }
   

    
    @Column(name="thirdPartyId")
    public Long getThirdPartyId() {
        return this.thirdPartyId;
    }
    
    public void setThirdPartyId(Long thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="addressId")
    public Address getAddressId_EN() {
        return this.addressId_EN;
    }
    
    public void setAddressId_EN(Address addressId_EN) {
        this.addressId_EN = addressId_EN;
    }

    
    @Column(name="addressId", insertable=false, updatable=false)
    public Long getAddressId() {
        return this.addressId;
    }
    
    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }




}


