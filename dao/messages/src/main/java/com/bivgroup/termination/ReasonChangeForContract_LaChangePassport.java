package com.bivgroup.termination;
// Generated 16.03.2018 18:04:44 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import com.bivgroup.crm.Document;
import com.bivgroup.crm.KindCountry;
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
 * ReasonChangeForContract_LaChangePassport Generated 16.03.2018 18:04:44 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_LaChangePassport")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_LaChangePassport extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Long thirdPartyId;
     private String innOther;
     private String taxResidentCountry;
     private Long isTaxResidentOther;
     private String residencePermit;
     private Long isResidencePermit;
     private String innUsa;
     private Long isTaxResidentUsa;
     private Document documentId_EN;
     private Long documentId;
     private KindCountry countryId_EN;
     private Long countryId;

    public ReasonChangeForContract_LaChangePassport() {
    }

	
    public ReasonChangeForContract_LaChangePassport(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_LaChangePassport(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Long thirdPartyId, String innOther, String taxResidentCountry, Long isTaxResidentOther, String residencePermit, Long isResidencePermit, String innUsa, Long isTaxResidentUsa, Document documentId_EN, Long documentId, KindCountry countryId_EN, Long countryId) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.thirdPartyId = thirdPartyId;
       this.innOther = innOther;
       this.taxResidentCountry = taxResidentCountry;
       this.isTaxResidentOther = isTaxResidentOther;
       this.residencePermit = residencePermit;
       this.isResidencePermit = isResidencePermit;
       this.innUsa = innUsa;
       this.isTaxResidentUsa = isTaxResidentUsa;
       this.documentId_EN = documentId_EN;
       this.documentId = documentId;
       this.countryId_EN = countryId_EN;
       this.countryId = countryId;
    }
   

    
    @Column(name="thirdPartyId")
    public Long getThirdPartyId() {
        return this.thirdPartyId;
    }
    
    public void setThirdPartyId(Long thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    
    @Column(name="innOther")
    public String getInnOther() {
        return this.innOther;
    }
    
    public void setInnOther(String innOther) {
        this.innOther = innOther;
    }

    
    @Column(name="taxResidentCountry")
    public String getTaxResidentCountry() {
        return this.taxResidentCountry;
    }
    
    public void setTaxResidentCountry(String taxResidentCountry) {
        this.taxResidentCountry = taxResidentCountry;
    }

    
    @Column(name="isTaxResidentOther")
    public Long getIsTaxResidentOther() {
        return this.isTaxResidentOther;
    }
    
    public void setIsTaxResidentOther(Long isTaxResidentOther) {
        this.isTaxResidentOther = isTaxResidentOther;
    }

    
    @Column(name="residencePermit")
    public String getResidencePermit() {
        return this.residencePermit;
    }
    
    public void setResidencePermit(String residencePermit) {
        this.residencePermit = residencePermit;
    }

    
    @Column(name="isResidencePermit")
    public Long getIsResidencePermit() {
        return this.isResidencePermit;
    }
    
    public void setIsResidencePermit(Long isResidencePermit) {
        this.isResidencePermit = isResidencePermit;
    }

    
    @Column(name="innUsa")
    public String getInnUsa() {
        return this.innUsa;
    }
    
    public void setInnUsa(String innUsa) {
        this.innUsa = innUsa;
    }

    
    @Column(name="isTaxResidentUsa")
    public Long getIsTaxResidentUsa() {
        return this.isTaxResidentUsa;
    }
    
    public void setIsTaxResidentUsa(Long isTaxResidentUsa) {
        this.isTaxResidentUsa = isTaxResidentUsa;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="documentId")
    public Document getDocumentId_EN() {
        return this.documentId_EN;
    }
    
    public void setDocumentId_EN(Document documentId_EN) {
        this.documentId_EN = documentId_EN;
    }

    
    @Column(name="documentId", insertable=false, updatable=false)
    public Long getDocumentId() {
        return this.documentId;
    }
    
    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="countryId")
    public KindCountry getCountryId_EN() {
        return this.countryId_EN;
    }
    
    public void setCountryId_EN(KindCountry countryId_EN) {
        this.countryId_EN = countryId_EN;
    }

    
    @Column(name="countryId", insertable=false, updatable=false)
    public Long getCountryId() {
        return this.countryId;
    }
    
    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }




}


