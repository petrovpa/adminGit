package com.bivgroup.termination;
// Generated 24.10.2017 10:52:59 unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * KindInvBaseActive Generated 24.10.2017 10:52:59 unknow unknow 
 */
@Entity
@Table(name="B2B_INVBASEACTIVE"
)
public class KindInvBaseActive  implements java.io.Serializable {


     private Long invBaseActiveId;
     private String name;
     private String sysName;
     private String code;
     private Long rowStatus;

    public KindInvBaseActive() {
    }

	
    public KindInvBaseActive(Long invBaseActiveId) {
        this.invBaseActiveId = invBaseActiveId;
    }
    public KindInvBaseActive(Long invBaseActiveId, String name, String sysName, String code, Long rowStatus) {
       this.invBaseActiveId = invBaseActiveId;
       this.name = name;
       this.sysName = sysName;
       this.code = code;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "B2B_INVBASEACTIVE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="invBaseActiveId", nullable=false, insertable=false, updatable=false)
    public Long getInvBaseActiveId() {
        return this.invBaseActiveId;
    }
    
    public void setInvBaseActiveId(Long invBaseActiveId) {
        this.invBaseActiveId = invBaseActiveId;
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

    
    @Column(name="code")
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


