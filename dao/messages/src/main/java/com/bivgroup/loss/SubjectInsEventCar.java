package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:24 AM unknow unknow 


import com.bivgroup.crm.Document;
import com.bivgroup.crm.Person;

import javax.persistence.*;

/**
 * SubjectInsEventCar Generated Aug 7, 2017 10:42:24 AM unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "SubjectInsEventCar") 
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id") 
@Table(name="CH_SUBJECTINSEVENT_CAR"
)
public class SubjectInsEventCar extends com.bivgroup.loss.SubjectInsEventProperty implements java.io.Serializable {


     private String markStr;
     private String modelStr;
     private Long yearOfIssue;
     private String vin;
     private String bodyNumber;
     private String chassisNumber;
     private String regNumber;
     private Document documentId_EN;
     private Long documentId;

    public SubjectInsEventCar() {
    }

	
    public SubjectInsEventCar(Long id) {
        super(id);        
    }
    public SubjectInsEventCar(Long id, Integer isNoData, Long roleOnInsEvent, ClaimHandlingInsEvent insEventId_EN, Long insEventId, SubjectInsEvent sourceSubjectId_EN, Long sourceSubjectId, OtherInsCompany otherInsCompanyId_EN, Long otherInsCompanyId, Long rowStatus, Person ownerId_EN, Long ownerId, String markStr, String modelStr, Long yearOfIssue, String vin, String bodyNumber, String chassisNumber, String regNumber, Document documentId_EN, Long documentId) {
        super(id, isNoData, roleOnInsEvent, insEventId_EN, insEventId, sourceSubjectId_EN, sourceSubjectId, otherInsCompanyId_EN, otherInsCompanyId, rowStatus, ownerId_EN, ownerId);        
       this.markStr = markStr;
       this.modelStr = modelStr;
       this.yearOfIssue = yearOfIssue;
       this.vin = vin;
       this.bodyNumber = bodyNumber;
       this.chassisNumber = chassisNumber;
       this.regNumber = regNumber;
       this.documentId_EN = documentId_EN;
       this.documentId = documentId;
    }
   

    
    @Column(name="markStr")
    public String getMarkStr() {
        return this.markStr;
    }
    
    public void setMarkStr(String markStr) {
        this.markStr = markStr;
    }

    
    @Column(name="modelStr")
    public String getModelStr() {
        return this.modelStr;
    }
    
    public void setModelStr(String modelStr) {
        this.modelStr = modelStr;
    }

    
    @Column(name="yearOfIssue")
    public Long getYearOfIssue() {
        return this.yearOfIssue;
    }
    
    public void setYearOfIssue(Long yearOfIssue) {
        this.yearOfIssue = yearOfIssue;
    }

    
    @Column(name="vin")
    public String getVin() {
        return this.vin;
    }
    
    public void setVin(String vin) {
        this.vin = vin;
    }

    
    @Column(name="bodyNumber")
    public String getBodyNumber() {
        return this.bodyNumber;
    }
    
    public void setBodyNumber(String bodyNumber) {
        this.bodyNumber = bodyNumber;
    }

    
    @Column(name="chassisNumber")
    public String getChassisNumber() {
        return this.chassisNumber;
    }
    
    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    
    @Column(name="regNumber")
    public String getRegNumber() {
        return this.regNumber;
    }
    
    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
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




}


