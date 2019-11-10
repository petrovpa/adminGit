package com.bivgroup.loss;
// Generated 14.12.2017 18:39:27 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * LossEventReason Generated 14.12.2017 18:39:27 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="B2B_INSEVENTREASON"
)
public class LossEventReason  implements java.io.Serializable {


     private Long id;
     private String externalCode;
     private String hint;
     private String sysname;
     private String name;
     private Long rowStatus;

    public LossEventReason() {
    }

	
    public LossEventReason(Long id) {
        this.id = id;
    }
    public LossEventReason(Long id, String externalCode, String hint, String sysname, String name, Long rowStatus) {
       this.id = id;
       this.externalCode = externalCode;
       this.hint = hint;
       this.sysname = sysname;
       this.name = name;
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

    
    @Column(name="externalCode")
    public String getExternalCode() {
        return this.externalCode;
    }
    
    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    
    @Column(name="hint")
    public String getHint() {
        return this.hint;
    }
    
    public void setHint(String hint) {
        this.hint = hint;
    }

    
    @Column(name="sysname")
    public String getSysname() {
        return this.sysname;
    }
    
    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


