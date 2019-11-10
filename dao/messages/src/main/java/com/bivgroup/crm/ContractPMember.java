package com.bivgroup.crm;
// Generated 14.12.2017 18:39:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * ContractPMember Generated 14.12.2017 18:39:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ContractPMember") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="PD_Contract_PMember"
)
public class ContractPMember extends com.bivgroup.crm.PPerson implements java.io.Serializable {


     private Date startDate;
     private Date endDate;
     private Long contractId;
     private KindContractMember typeId_EN;
     private Long typeId;

    public ContractPMember() {
    }

	
    public ContractPMember(Long id) {
        super(id);        
    }
    public ContractPMember(Long id, String innOther, String taxResidentCountry, Long isTaxResidentOther, Long isTaxResidentUsa, String innUsa, Long eId, BankDetails bankDetailsId_EN, Long bankDetailsId, Client clientId_EN, Long clientId, KindStatus stateId_EN, Long stateId, KindCountry countryId_EN, Long countryId, Set<PersonDocument> documents, Set<PersonAddress> addresses, Set<PersonContact> contacts, Set<BankDetails> bankDetailsList, Long rowStatus, String residencePermit, Long isResidencePermit, String countryOfBirth, String snils, Long kinshipId, Integer isMarried, String inn, Integer sex, String placeOfBirth, Date dateOfBirth, Integer isEmptyPatronymic, String patronymic2, String patronymic, String name2, String name, String surname2, String surname, Set<PPersonChild> childs, Date startDate, Date endDate, Long contractId, KindContractMember typeId_EN, Long typeId) {
        super(id, innOther, taxResidentCountry, isTaxResidentOther, isTaxResidentUsa, innUsa, eId, bankDetailsId_EN, bankDetailsId, clientId_EN, clientId, stateId_EN, stateId, countryId_EN, countryId, documents, addresses, contacts, bankDetailsList, rowStatus, residencePermit, isResidencePermit, countryOfBirth, snils, kinshipId, isMarried, inn, sex, placeOfBirth, dateOfBirth, isEmptyPatronymic, patronymic2, patronymic, name2, name, surname2, surname, childs);        
       this.startDate = startDate;
       this.endDate = endDate;
       this.contractId = contractId;
       this.typeId_EN = typeId_EN;
       this.typeId = typeId;
    }
   

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="StartDate")
    public Date getStartDate() {
        return this.startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EndDate")
    public Date getEndDate() {
        return this.endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    
    @Column(name="ContractID")
    public Long getContractId() {
        return this.contractId;
    }
    
    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TypeID")
    public KindContractMember getTypeId_EN() {
        return this.typeId_EN;
    }
    
    public void setTypeId_EN(KindContractMember typeId_EN) {
        this.typeId_EN = typeId_EN;
    }

    
    @Column(name="TypeID", insertable=false, updatable=false)
    public Long getTypeId() {
        return this.typeId;
    }
    
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }




}


