package com.bivgroup.termination;
// Generated 05.09.2017 16:56:32 unknow unknow 


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
 * ProdKindChangeReason Generated 05.09.2017 16:56:32 unknow unknow 
 */
@Entity
@Table(name="HB_PRODKINDCHANGEREASON"
)
public class ProdKindChangeReason  implements java.io.Serializable {


     private Long id;
     private Long prodVerId;
     private KindChangeReason kindChangeReasonId_EN;
     private Long kindChangeReasonId;
     private Long rowStatus;

    public ProdKindChangeReason() {
    }

	
    public ProdKindChangeReason(Long id) {
        this.id = id;
    }
    public ProdKindChangeReason(Long id, Long prodVerId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, Long rowStatus) {
       this.id = id;
       this.prodVerId = prodVerId;
       this.kindChangeReasonId_EN = kindChangeReasonId_EN;
       this.kindChangeReasonId = kindChangeReasonId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_PRODKINDCHANGEREASON_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="prodVerId")
    public Long getProdVerId() {
        return this.prodVerId;
    }
    
    public void setProdVerId(Long prodVerId) {
        this.prodVerId = prodVerId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="kindChangeReasonId")
    public KindChangeReason getKindChangeReasonId_EN() {
        return this.kindChangeReasonId_EN;
    }
    
    public void setKindChangeReasonId_EN(KindChangeReason kindChangeReasonId_EN) {
        this.kindChangeReasonId_EN = kindChangeReasonId_EN;
    }

    
    @Column(name="kindChangeReasonId", insertable=false, updatable=false)
    public Long getKindChangeReasonId() {
        return this.kindChangeReasonId;
    }
    
    public void setKindChangeReasonId(Long kindChangeReasonId) {
        this.kindChangeReasonId = kindChangeReasonId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


