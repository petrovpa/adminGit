package com.bivgroup.crm;
// Generated 23.10.2017 14:39:46 unknow unknow 


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * ClientBankDetails Generated 23.10.2017 14:39:46 unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "ClientBankDetails") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="CDM_CLIENT_BANKDETAILS"
)
public class ClientBankDetails extends com.bivgroup.crm.BankDetails implements java.io.Serializable {


     private String alias;
     private Integer isPrimary;
     private Client clientId_EN;
     private Long clientId;
     private KindStatus stateId_EN;
     private Long stateId;

    public ClientBankDetails() {
    }

	
    public ClientBankDetails(Long id) {
        super(id);        
    }
    public ClientBankDetails(Long id, String bankPhone, String bankAddress, String bankSettlementAccount, String cardNumber, String bankAddressPhone, String purposeOfPayment, String account, String bankAccount, String bankINN, String bankBIK, String bankName, Long eId, Person personId_EN, Long personId, Long rowStatus, String alias, Integer isPrimary, Client clientId_EN, Long clientId, KindStatus stateId_EN, Long stateId) {
        super(id, bankPhone, bankAddress, bankSettlementAccount, cardNumber, bankAddressPhone, purposeOfPayment, account, bankAccount, bankINN, bankBIK, bankName, eId, personId_EN, personId, rowStatus);        
       this.alias = alias;
       this.isPrimary = isPrimary;
       this.clientId_EN = clientId_EN;
       this.clientId = clientId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
    }
   

    
    @Column(name="Alias")
    public String getAlias() {
        return this.alias;
    }
    
    public void setAlias(String alias) {
        this.alias = alias;
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


