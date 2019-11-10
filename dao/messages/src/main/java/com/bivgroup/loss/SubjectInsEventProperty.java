package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:24 AM unknow unknow 


import com.bivgroup.crm.Person;

import javax.persistence.*;

/**
 * SubjectInsEventProperty Generated Aug 7, 2017 10:42:24 AM unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "SubjectInsEventProperty") 
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id") 
@Table(name="CH_SUBJECTINSEVENT_PROP"
)
public class SubjectInsEventProperty extends SubjectInsEvent implements java.io.Serializable {


     private Person ownerId_EN;
     private Long ownerId;

    public SubjectInsEventProperty() {
    }

	
    public SubjectInsEventProperty(Long id) {
        super(id);        
    }
    public SubjectInsEventProperty(Long id, Integer isNoData, Long roleOnInsEvent, ClaimHandlingInsEvent insEventId_EN, Long insEventId, SubjectInsEvent sourceSubjectId_EN, Long sourceSubjectId, OtherInsCompany otherInsCompanyId_EN, Long otherInsCompanyId, Long rowStatus, Person ownerId_EN, Long ownerId) {
        super(id, isNoData, roleOnInsEvent, insEventId_EN, insEventId, sourceSubjectId_EN, sourceSubjectId, otherInsCompanyId_EN, otherInsCompanyId, rowStatus);        
       this.ownerId_EN = ownerId_EN;
       this.ownerId = ownerId;
    }
   

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ownerId")
    public Person getOwnerId_EN() {
        return this.ownerId_EN;
    }
    
    public void setOwnerId_EN(Person ownerId_EN) {
        this.ownerId_EN = ownerId_EN;
    }

    
    @Column(name="ownerId", insertable=false, updatable=false)
    public Long getOwnerId() {
        return this.ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }




}


