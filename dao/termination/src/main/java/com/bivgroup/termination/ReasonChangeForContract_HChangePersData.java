package com.bivgroup.termination;
// Generated 16.03.2018 18:02:44 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_HChangePersData Generated 16.03.2018 18:02:44 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_HChangePersData")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_HChangePersData extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Long thirdPartyId;
     private String placeOfBirth;
     private String snils;
     private String countryOfBirth;
     private String inn;
     private String oldFullNameH;
     private Date birthDate;
     private String middleName;
     private String name;

    public ReasonChangeForContract_HChangePersData() {
    }

	
    public ReasonChangeForContract_HChangePersData(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_HChangePersData(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Long thirdPartyId, String placeOfBirth, String snils, String countryOfBirth, String inn, String oldFullNameH, Date birthDate, String middleName, String name) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.thirdPartyId = thirdPartyId;
       this.placeOfBirth = placeOfBirth;
       this.snils = snils;
       this.countryOfBirth = countryOfBirth;
       this.inn = inn;
       this.oldFullNameH = oldFullNameH;
       this.birthDate = birthDate;
       this.middleName = middleName;
       this.name = name;
    }
   

    
    @Column(name="thirdPartyId")
    public Long getThirdPartyId() {
        return this.thirdPartyId;
    }
    
    public void setThirdPartyId(Long thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    
    @Column(name="placeOfBirth")
    public String getPlaceOfBirth() {
        return this.placeOfBirth;
    }
    
    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    
    @Column(name="snils")
    public String getSnils() {
        return this.snils;
    }
    
    public void setSnils(String snils) {
        this.snils = snils;
    }

    
    @Column(name="countryOfBirth")
    public String getCountryOfBirth() {
        return this.countryOfBirth;
    }
    
    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    
    @Column(name="inn")
    public String getInn() {
        return this.inn;
    }
    
    public void setInn(String inn) {
        this.inn = inn;
    }

    
    @Column(name="stringField")
    public String getOldFullNameH() {
        return this.oldFullNameH;
    }
    
    public void setOldFullNameH(String oldFullNameH) {
        this.oldFullNameH = oldFullNameH;
    }

    
    @Column(name="birthDate")
    public Date getBirthDate() {
        return this.birthDate;
    }
    
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    
    @Column(name="middleName")
    public String getMiddleName() {
        return this.middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }




}


