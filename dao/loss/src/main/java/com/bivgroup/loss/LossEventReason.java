package com.bivgroup.loss;
// Generated Aug 30, 2017 2:47:05 PM unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * LossEventReason Generated Aug 30, 2017 2:47:05 PM unknow unknow 
 */
@Entity
@Table(name="B2B_INSEVENTREASON"
)
public class LossEventReason  implements java.io.Serializable {


     private Long id;
     private String name;
     private String sysname;
     private String hint;
     private String externalCode;
     private Long rowStatus;

    public LossEventReason() {
    }

	
    public LossEventReason(Long id) {
        this.id = id;
    }
    public LossEventReason(Long id, String name, String sysname, String hint, String externalCode, Long rowStatus) {
       this.id = id;
       this.name = name;
       this.sysname = sysname;
       this.hint = hint;
       this.externalCode = externalCode;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "B2B_INSEVENTREASON_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="sysname")
    public String getSysname() {
        return this.sysname;
    }
    
    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    
    @Column(name="hint")
    public String getHint() {
        return this.hint;
    }
    
    public void setHint(String hint) {
        this.hint = hint;
    }

    
    @Column(name="externalCode")
    public String getExternalCode() {
        return this.externalCode;
    }
    
    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


