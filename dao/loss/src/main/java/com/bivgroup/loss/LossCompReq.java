package com.bivgroup.loss;
// Generated Aug 30, 2017 2:47:07 PM unknow unknow 


import com.bivgroup.crm.Client;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * LossCompReq Generated Aug 30, 2017 2:47:07 PM unknow unknow 
 */
@Entity
@Table(name="B2B_LOSSCOMPREQ"
)
public class LossCompReq  implements java.io.Serializable {


     private Long id;
     private Date requestDate;
     private LossNotice lossNoticeId_EN;
     private Long lossNoticeId;
     private Client insurerId_EN;
     private Long insurerId;
     private Client beneficiaryId_EN;
     private Long beneficiaryId;
     private Long rowStatus;

    public LossCompReq() {
    }

	
    public LossCompReq(Long id) {
        this.id = id;
    }
    public LossCompReq(Long id, Date requestDate, LossNotice lossNoticeId_EN, Long lossNoticeId, Client insurerId_EN, Long insurerId, Client beneficiaryId_EN, Long beneficiaryId, Long rowStatus) {
       this.id = id;
       this.requestDate = requestDate;
       this.lossNoticeId_EN = lossNoticeId_EN;
       this.lossNoticeId = lossNoticeId;
       this.insurerId_EN = insurerId_EN;
       this.insurerId = insurerId;
       this.beneficiaryId_EN = beneficiaryId_EN;
       this.beneficiaryId = beneficiaryId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "B2B_LOSSCOMPREQ_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="requestDate")
    public Date getRequestDate() {
        return this.requestDate;
    }
    
    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="lossNoticeId")
    public LossNotice getLossNoticeId_EN() {
        return this.lossNoticeId_EN;
    }
    
    public void setLossNoticeId_EN(LossNotice lossNoticeId_EN) {
        this.lossNoticeId_EN = lossNoticeId_EN;
    }

    
    @Column(name="lossNoticeId", insertable=false, updatable=false)
    public Long getLossNoticeId() {
        return this.lossNoticeId;
    }
    
    public void setLossNoticeId(Long lossNoticeId) {
        this.lossNoticeId = lossNoticeId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="insurerId")
    public Client getInsurerId_EN() {
        return this.insurerId_EN;
    }
    
    public void setInsurerId_EN(Client insurerId_EN) {
        this.insurerId_EN = insurerId_EN;
    }

    
    @Column(name="insurerId", insertable=false, updatable=false)
    public Long getInsurerId() {
        return this.insurerId;
    }
    
    public void setInsurerId(Long insurerId) {
        this.insurerId = insurerId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="beneficiaryId")
    public Client getBeneficiaryId_EN() {
        return this.beneficiaryId_EN;
    }
    
    public void setBeneficiaryId_EN(Client beneficiaryId_EN) {
        this.beneficiaryId_EN = beneficiaryId_EN;
    }

    
    @Column(name="beneficiaryId", insertable=false, updatable=false)
    public Long getBeneficiaryId() {
        return this.beneficiaryId;
    }
    
    public void setBeneficiaryId(Long beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


