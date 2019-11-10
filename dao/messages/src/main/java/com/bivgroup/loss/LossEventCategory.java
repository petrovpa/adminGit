package com.bivgroup.loss;
// Generated 14.12.2017 18:39:27 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * LossEventCategory Generated 14.12.2017 18:39:27 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="B2B_INSEVENTCAT"
)
public class LossEventCategory  implements java.io.Serializable {


     private Long id;
     private LossEventReason insEventReasonId_EN;
     private Long insEventReasonId;
     private LossDamageCategory damageCatId_EN;
     private Long damageCatId;
     private LossEvent insEventId_EN;
     private Long insEventId;
     private Long rowStatus;

    public LossEventCategory() {
    }

	
    public LossEventCategory(Long id) {
        this.id = id;
    }
    public LossEventCategory(Long id, LossEventReason insEventReasonId_EN, Long insEventReasonId, LossDamageCategory damageCatId_EN, Long damageCatId, LossEvent insEventId_EN, Long insEventId, Long rowStatus) {
       this.id = id;
       this.insEventReasonId_EN = insEventReasonId_EN;
       this.insEventReasonId = insEventReasonId;
       this.damageCatId_EN = damageCatId_EN;
       this.damageCatId = damageCatId;
       this.insEventId_EN = insEventId_EN;
       this.insEventId = insEventId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "B2B_INSEVENTCAT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="insEventReasonId")
    public LossEventReason getInsEventReasonId_EN() {
        return this.insEventReasonId_EN;
    }
    
    public void setInsEventReasonId_EN(LossEventReason insEventReasonId_EN) {
        this.insEventReasonId_EN = insEventReasonId_EN;
    }

    
    @Column(name="insEventReasonId", insertable=false, updatable=false)
    public Long getInsEventReasonId() {
        return this.insEventReasonId;
    }
    
    public void setInsEventReasonId(Long insEventReasonId) {
        this.insEventReasonId = insEventReasonId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="damageCatId")
    public LossDamageCategory getDamageCatId_EN() {
        return this.damageCatId_EN;
    }
    
    public void setDamageCatId_EN(LossDamageCategory damageCatId_EN) {
        this.damageCatId_EN = damageCatId_EN;
    }

    
    @Column(name="damageCatId", insertable=false, updatable=false)
    public Long getDamageCatId() {
        return this.damageCatId;
    }
    
    public void setDamageCatId(Long damageCatId) {
        this.damageCatId = damageCatId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="insEventId")
    public LossEvent getInsEventId_EN() {
        return this.insEventId_EN;
    }
    
    public void setInsEventId_EN(LossEvent insEventId_EN) {
        this.insEventId_EN = insEventId_EN;
    }

    
    @Column(name="insEventId", insertable=false, updatable=false)
    public Long getInsEventId() {
        return this.insEventId;
    }
    
    public void setInsEventId(Long insEventId) {
        this.insEventId = insEventId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


