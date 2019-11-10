package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:22 AM unknow unknow 


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
 * SubjectInsEvent Generated Aug 7, 2017 10:42:22 AM unknow unknow 
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)       
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="CH_SUBJECTINSEVENT"
)
public class SubjectInsEvent  implements java.io.Serializable {


     private Long id;
     private Integer isNoData;
     private Long roleOnInsEvent;
     private ClaimHandlingInsEvent insEventId_EN;
     private Long insEventId;
     private SubjectInsEvent sourceSubjectId_EN;
     private Long sourceSubjectId;
     private OtherInsCompany otherInsCompanyId_EN;
     private Long otherInsCompanyId;
     private Long rowStatus;

    public SubjectInsEvent() {
    }

	
    public SubjectInsEvent(Long id) {
        this.id = id;
    }
    public SubjectInsEvent(Long id, Integer isNoData, Long roleOnInsEvent, ClaimHandlingInsEvent insEventId_EN, Long insEventId, SubjectInsEvent sourceSubjectId_EN, Long sourceSubjectId, OtherInsCompany otherInsCompanyId_EN, Long otherInsCompanyId, Long rowStatus) {
       this.id = id;
       this.isNoData = isNoData;
       this.roleOnInsEvent = roleOnInsEvent;
       this.insEventId_EN = insEventId_EN;
       this.insEventId = insEventId;
       this.sourceSubjectId_EN = sourceSubjectId_EN;
       this.sourceSubjectId = sourceSubjectId;
       this.otherInsCompanyId_EN = otherInsCompanyId_EN;
       this.otherInsCompanyId = otherInsCompanyId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CH_SUBJECTINSEVENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="isNoData")
    public Integer getIsNoData() {
        return this.isNoData;
    }
    
    public void setIsNoData(Integer isNoData) {
        this.isNoData = isNoData;
    }

    
    @Column(name="roleOnInsEvent")
    public Long getRoleOnInsEvent() {
        return this.roleOnInsEvent;
    }
    
    public void setRoleOnInsEvent(Long roleOnInsEvent) {
        this.roleOnInsEvent = roleOnInsEvent;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="insEventId")
    public ClaimHandlingInsEvent getInsEventId_EN() {
        return this.insEventId_EN;
    }
    
    public void setInsEventId_EN(ClaimHandlingInsEvent insEventId_EN) {
        this.insEventId_EN = insEventId_EN;
    }

    
    @Column(name="insEventId", insertable=false, updatable=false)
    public Long getInsEventId() {
        return this.insEventId;
    }
    
    public void setInsEventId(Long insEventId) {
        this.insEventId = insEventId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="sourceSubjectId")
    public SubjectInsEvent getSourceSubjectId_EN() {
        return this.sourceSubjectId_EN;
    }
    
    public void setSourceSubjectId_EN(SubjectInsEvent sourceSubjectId_EN) {
        this.sourceSubjectId_EN = sourceSubjectId_EN;
    }

    
    @Column(name="sourceSubjectId", insertable=false, updatable=false)
    public Long getSourceSubjectId() {
        return this.sourceSubjectId;
    }
    
    public void setSourceSubjectId(Long sourceSubjectId) {
        this.sourceSubjectId = sourceSubjectId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="otherInsCompanyId")
    public OtherInsCompany getOtherInsCompanyId_EN() {
        return this.otherInsCompanyId_EN;
    }
    
    public void setOtherInsCompanyId_EN(OtherInsCompany otherInsCompanyId_EN) {
        this.otherInsCompanyId_EN = otherInsCompanyId_EN;
    }

    
    @Column(name="otherInsCompanyId", insertable=false, updatable=false)
    public Long getOtherInsCompanyId() {
        return this.otherInsCompanyId;
    }
    
    public void setOtherInsCompanyId(Long otherInsCompanyId) {
        this.otherInsCompanyId = otherInsCompanyId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


