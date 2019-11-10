package com.bivgroup.loss;
// Generated Aug 30, 2017 2:47:08 PM unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * KindIntegrationRisk Generated Aug 30, 2017 2:47:08 PM unknow unknow 
 */
@Entity
@Table(name="HB_KINDINTEGRATIONRISK"
)
public class KindIntegrationRisk  implements java.io.Serializable {


     private Long id;
     private String code;
     private String name;
     private String sysName;
     private Long rowStatus;

    public KindIntegrationRisk() {
    }

	
    public KindIntegrationRisk(Long id) {
        this.id = id;
    }
    public KindIntegrationRisk(Long id, String code, String name, String sysName, Long rowStatus) {
       this.id = id;
       this.code = code;
       this.name = name;
       this.sysName = sysName;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_KINDINTEGRATIONRISK_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="code")
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="sysName")
    public String getSysName() {
        return this.sysName;
    }
    
    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


