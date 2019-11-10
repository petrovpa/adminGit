package com.bivgroup.termination;
// Generated Aug 31, 2017 11:57:55 AM unknow unknow 


import java.util.Date;
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
 * ValueReasonChangeForContract Generated Aug 31, 2017 11:57:55 AM unknow unknow 
 */
@Entity
@Table(name="PD_REASONCHANGE_VALUE"
)
public class ValueReasonChangeForContract  implements java.io.Serializable {


     private Long id;
     private Date dateBegin;
     private Date dateEnd;
     private Long valueHandbook;
     private Integer valueBoolean;
     private ReasonChangeForContract reasonId_EN;
     private Long reasonId;
     private Long rowStatus;

    public ValueReasonChangeForContract() {
    }

	
    public ValueReasonChangeForContract(Long id) {
        this.id = id;
    }
    public ValueReasonChangeForContract(Long id, Date dateBegin, Date dateEnd, Long valueHandbook, Integer valueBoolean, ReasonChangeForContract reasonId_EN, Long reasonId, Long rowStatus) {
       this.id = id;
       this.dateBegin = dateBegin;
       this.dateEnd = dateEnd;
       this.valueHandbook = valueHandbook;
       this.valueBoolean = valueBoolean;
       this.reasonId_EN = reasonId_EN;
       this.reasonId = reasonId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "PD_REASONCHANGE_VALUE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="dateBegin")
    public Date getDateBegin() {
        return this.dateBegin;
    }
    
    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    
    @Column(name="dateEnd")
    public Date getDateEnd() {
        return this.dateEnd;
    }
    
    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    
    @Column(name="valueHandbook")
    public Long getValueHandbook() {
        return this.valueHandbook;
    }
    
    public void setValueHandbook(Long valueHandbook) {
        this.valueHandbook = valueHandbook;
    }

    
    @Column(name="valueBoolean")
    public Integer getValueBoolean() {
        return this.valueBoolean;
    }
    
    public void setValueBoolean(Integer valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="reasonId")
    public ReasonChangeForContract getReasonId_EN() {
        return this.reasonId_EN;
    }
    
    public void setReasonId_EN(ReasonChangeForContract reasonId_EN) {
        this.reasonId_EN = reasonId_EN;
    }

    
    @Column(name="reasonId", insertable=false, updatable=false)
    public Long getReasonId() {
        return this.reasonId;
    }
    
    public void setReasonId(Long reasonId) {
        this.reasonId = reasonId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


