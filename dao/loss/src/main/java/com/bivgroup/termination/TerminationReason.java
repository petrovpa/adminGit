package com.bivgroup.termination;
// Generated Aug 31, 2017 11:57:55 AM unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * TerminationReason Generated Aug 31, 2017 11:57:55 AM unknow unknow 
 */
@Entity
@Table(name="HD_TerminationReason"
)
public class TerminationReason  implements java.io.Serializable {


     private Long id;
     private String name;
     private Long eId;
     private String sysname;
     private Long rowStatus;

    public TerminationReason() {
    }

	
    public TerminationReason(Long id) {
        this.id = id;
    }
    public TerminationReason(Long id, String name, Long eId, String sysname, Long rowStatus) {
       this.id = id;
       this.name = name;
       this.eId = eId;
       this.sysname = sysname;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HD_TERMINATIONREASON_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="Name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

    
    @Column(name="Sysname")
    public String getSysname() {
        return this.sysname;
    }
    
    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


