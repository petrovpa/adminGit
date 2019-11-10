package com.bivgroup.crm;
// Generated 14.12.2017 18:39:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * ContractDriver Generated 14.12.2017 18:39:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ContractDriver") 
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id") 
@Table(name="PD_Contract_Driver"
)
public class ContractDriver extends com.bivgroup.crm.ContractPMember implements java.io.Serializable {


     private Date dateOfExp;

    public ContractDriver() {
    }

	
    public ContractDriver(Long id) {
        super(id);        
    }
    public ContractDriver(Long id, String innOther, String taxResidentCountry, Long isTaxResidentOther, Long isTaxResidentUsa, String innUsa, Long eId, BankDetails bankDetailsId_EN, Long bankDetailsId, Client clientId_EN, Long clientId, KindStatus stateId_EN, Long stateId, KindCountry countryId_EN, Long countryId, Set<PersonDocument> documents, Set<PersonAddress> addresses, Set<PersonContact> contacts, Set<BankDetails> bankDetailsList, Long rowStatus, String residencePermit, Long isResidencePermit, String countryOfBirth, String snils, Long kinshipId, Integer isMarried, String inn, Integer sex, String placeOfBirth, Date dateOfBirth, Integer isEmptyPatronymic, String patronymic2, String patronymic, String name2, String name, String surname2, String surname, Set<PPersonChild> childs, Date startDate, Date endDate, Long contractId, KindContractMember typeId_EN, Long typeId, Date dateOfExp) {
        super(id, innOther, taxResidentCountry, isTaxResidentOther, isTaxResidentUsa, innUsa, eId, bankDetailsId_EN, bankDetailsId, clientId_EN, clientId, stateId_EN, stateId, countryId_EN, countryId, documents, addresses, contacts, bankDetailsList, rowStatus, residencePermit, isResidencePermit, countryOfBirth, snils, kinshipId, isMarried, inn, sex, placeOfBirth, dateOfBirth, isEmptyPatronymic, patronymic2, patronymic, name2, name, surname2, surname, childs, startDate, endDate, contractId, typeId_EN, typeId);        
       this.dateOfExp = dateOfExp;
    }
   

    
    @Column(name="DateOfExp")
    public Date getDateOfExp() {
        return this.dateOfExp;
    }
    
    public void setDateOfExp(Date dateOfExp) {
        this.dateOfExp = dateOfExp;
    }




}


