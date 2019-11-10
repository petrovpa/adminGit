package com.bivgroup.crm;
// Generated Sep 6, 2017 5:42:17 PM unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * ClientManager Generated Sep 6, 2017 5:42:17 PM unknow unknow 
 */
@Entity
@Table(name="CDM_Client_Manager"
)
public class ClientManager  implements java.io.Serializable {


     private Long id;
     private String bankCode;
     private String tel;
     private String patronymic;
     private String name;
     private String surname;
     private Address addressBankId_EN;
     private Long addressBankId;
     private Long rowStatus;

    public ClientManager() {
    }

	
    public ClientManager(Long id) {
        this.id = id;
    }
    public ClientManager(Long id, String bankCode, String tel, String patronymic, String name, String surname, Address addressBankId_EN, Long addressBankId, Long rowStatus) {
       this.id = id;
       this.bankCode = bankCode;
       this.tel = tel;
       this.patronymic = patronymic;
       this.name = name;
       this.surname = surname;
       this.addressBankId_EN = addressBankId_EN;
       this.addressBankId = addressBankId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CDM_CLIENT_MANAGER_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="BankCode")
    public String getBankCode() {
        return this.bankCode;
    }
    
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    
    @Column(name="Tel")
    public String getTel() {
        return this.tel;
    }
    
    public void setTel(String tel) {
        this.tel = tel;
    }

    
    @Column(name="Patronymic")
    public String getPatronymic() {
        return this.patronymic;
    }
    
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    
    @Column(name="Name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="Surname")
    public String getSurname() {
        return this.surname;
    }
    
    public void setSurname(String surname) {
        this.surname = surname;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="addressBankId")
    public Address getAddressBankId_EN() {
        return this.addressBankId_EN;
    }
    
    public void setAddressBankId_EN(Address addressBankId_EN) {
        this.addressBankId_EN = addressBankId_EN;
    }

    
    @Column(name="addressBankId", insertable=false, updatable=false)
    public Long getAddressBankId() {
        return this.addressBankId;
    }
    
    public void setAddressBankId(Long addressBankId) {
        this.addressBankId = addressBankId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


