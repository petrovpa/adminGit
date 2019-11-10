package com.bivgroup.termination;
// Generated 16.03.2018 18:04:52 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_DuplicateDocument Generated 16.03.2018 18:04:52 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_DuplicateDocument")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_DuplicateDocument extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Date fromDate;
     private String docType;
     private String docReceiptAddressStr;
     private String reasonDescription;
     private String makeDuplicateReason;

    public ReasonChangeForContract_DuplicateDocument() {
    }

	
    public ReasonChangeForContract_DuplicateDocument(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_DuplicateDocument(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Date fromDate, String docType, String docReceiptAddressStr, String reasonDescription, String makeDuplicateReason) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.fromDate = fromDate;
       this.docType = docType;
       this.docReceiptAddressStr = docReceiptAddressStr;
       this.reasonDescription = reasonDescription;
       this.makeDuplicateReason = makeDuplicateReason;
    }
   

    
    @Column(name="attrDate1")
    public Date getFromDate() {
        return this.fromDate;
    }
    
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    
    @Column(name="typeStr")
    public String getDocType() {
        return this.docType;
    }
    
    public void setDocType(String docType) {
        this.docType = docType;
    }

    
    @Column(name="addressStr")
    public String getDocReceiptAddressStr() {
        return this.docReceiptAddressStr;
    }
    
    public void setDocReceiptAddressStr(String docReceiptAddressStr) {
        this.docReceiptAddressStr = docReceiptAddressStr;
    }

    
    @Column(name="note")
    public String getReasonDescription() {
        return this.reasonDescription;
    }
    
    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    
    @Column(name="cause")
    public String getMakeDuplicateReason() {
        return this.makeDuplicateReason;
    }
    
    public void setMakeDuplicateReason(String makeDuplicateReason) {
        this.makeDuplicateReason = makeDuplicateReason;
    }




}


