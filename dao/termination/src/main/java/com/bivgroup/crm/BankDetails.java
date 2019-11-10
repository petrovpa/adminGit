package com.bivgroup.crm;
// Generated 23.10.2017 14:39:45 unknow unknow 


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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * BankDetails Generated 23.10.2017 14:39:45 unknow unknow 
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)       
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="SD_BankDetails"
)
public class BankDetails  implements java.io.Serializable {


     private Long id;
     private String bankPhone;
     private String bankAddress;
     private String bankSettlementAccount;
     private String cardNumber;
     private String bankAddressPhone;
     private String purposeOfPayment;
     private String account;
     private String bankAccount;
     private String bankINN;
     private String bankBIK;
     private String bankName;
     private Long eId;
     private Person personId_EN;
     private Long personId;
     private Long rowStatus;

    public BankDetails() {
    }

	
    public BankDetails(Long id) {
        this.id = id;
    }
    public BankDetails(Long id, String bankPhone, String bankAddress, String bankSettlementAccount, String cardNumber, String bankAddressPhone, String purposeOfPayment, String account, String bankAccount, String bankINN, String bankBIK, String bankName, Long eId, Person personId_EN, Long personId, Long rowStatus) {
       this.id = id;
       this.bankPhone = bankPhone;
       this.bankAddress = bankAddress;
       this.bankSettlementAccount = bankSettlementAccount;
       this.cardNumber = cardNumber;
       this.bankAddressPhone = bankAddressPhone;
       this.purposeOfPayment = purposeOfPayment;
       this.account = account;
       this.bankAccount = bankAccount;
       this.bankINN = bankINN;
       this.bankBIK = bankBIK;
       this.bankName = bankName;
       this.eId = eId;
       this.personId_EN = personId_EN;
       this.personId = personId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_BANKDETAILS_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="bankPhone")
    public String getBankPhone() {
        return this.bankPhone;
    }
    
    public void setBankPhone(String bankPhone) {
        this.bankPhone = bankPhone;
    }

    
    @Column(name="bankAddress")
    public String getBankAddress() {
        return this.bankAddress;
    }
    
    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    
    @Column(name="bankSettlementAccount")
    public String getBankSettlementAccount() {
        return this.bankSettlementAccount;
    }
    
    public void setBankSettlementAccount(String bankSettlementAccount) {
        this.bankSettlementAccount = bankSettlementAccount;
    }

    
    @Column(name="cardNumber")
    public String getCardNumber() {
        return this.cardNumber;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    
    @Column(name="bankAddressPhone")
    public String getBankAddressPhone() {
        return this.bankAddressPhone;
    }
    
    public void setBankAddressPhone(String bankAddressPhone) {
        this.bankAddressPhone = bankAddressPhone;
    }

    
    @Column(name="PurposeOfPayment")
    public String getPurposeOfPayment() {
        return this.purposeOfPayment;
    }
    
    public void setPurposeOfPayment(String purposeOfPayment) {
        this.purposeOfPayment = purposeOfPayment;
    }

    
    @Column(name="Account")
    public String getAccount() {
        return this.account;
    }
    
    public void setAccount(String account) {
        this.account = account;
    }

    
    @Column(name="BankAccount")
    public String getBankAccount() {
        return this.bankAccount;
    }
    
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    
    @Column(name="BankINN")
    public String getBankINN() {
        return this.bankINN;
    }
    
    public void setBankINN(String bankINN) {
        this.bankINN = bankINN;
    }

    
    @Column(name="BankBIK")
    public String getBankBIK() {
        return this.bankBIK;
    }
    
    public void setBankBIK(String bankBIK) {
        this.bankBIK = bankBIK;
    }

    
    @Column(name="BankName")
    public String getBankName() {
        return this.bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="PersonId")
    public Person getPersonId_EN() {
        return this.personId_EN;
    }
    
    public void setPersonId_EN(Person personId_EN) {
        this.personId_EN = personId_EN;
    }

    
    @Column(name="PersonId", insertable=false, updatable=false)
    public Long getPersonId() {
        return this.personId;
    }
    
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


