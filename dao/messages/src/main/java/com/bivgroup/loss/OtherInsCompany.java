package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:22 AM unknow unknow 


import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * OtherInsCompany Generated Aug 7, 2017 10:42:22 AM unknow unknow 
 */
@Entity
@Table(name="CH_OTHERINSCOMPANY"
)
public class OtherInsCompany  implements java.io.Serializable {


     private Long id;
     private String fullNumber;
     private String insSum;
     private KindInsCompany kindInsCompanyId_EN;
     private Long kindInsCompanyId;
     private Long rowStatus;

    public OtherInsCompany() {
    }

	
    public OtherInsCompany(Long id) {
        this.id = id;
    }
    public OtherInsCompany(Long id, String fullNumber, String insSum, KindInsCompany kindInsCompanyId_EN, Long kindInsCompanyId, Long rowStatus) {
       this.id = id;
       this.fullNumber = fullNumber;
       this.insSum = insSum;
       this.kindInsCompanyId_EN = kindInsCompanyId_EN;
       this.kindInsCompanyId = kindInsCompanyId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CH_OTHERINSCOMPANY_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="fullNumber")
    public String getFullNumber() {
        return this.fullNumber;
    }
    
    public void setFullNumber(String fullNumber) {
        this.fullNumber = fullNumber;
    }

    
    @Column(name="insSum")
    public String getInsSum() {
        return this.insSum;
    }
    
    public void setInsSum(String insSum) {
        this.insSum = insSum;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="kindInsCompanyId")
    public KindInsCompany getKindInsCompanyId_EN() {
        return this.kindInsCompanyId_EN;
    }
    
    public void setKindInsCompanyId_EN(KindInsCompany kindInsCompanyId_EN) {
        this.kindInsCompanyId_EN = kindInsCompanyId_EN;
    }

    
    @Column(name="kindInsCompanyId", insertable=false, updatable=false)
    public Long getKindInsCompanyId() {
        return this.kindInsCompanyId;
    }
    
    public void setKindInsCompanyId(Long kindInsCompanyId) {
        this.kindInsCompanyId = kindInsCompanyId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


