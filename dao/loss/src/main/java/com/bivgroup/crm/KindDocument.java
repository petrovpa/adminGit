package com.bivgroup.crm;
// Generated Sep 6, 2017 5:42:21 PM unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * KindDocument Generated Sep 6, 2017 5:42:21 PM unknow unknow 
 */
@Entity
@Table(name="HB_KindDocument"
)
public class KindDocument  implements java.io.Serializable {


     private Long id;
     private String sysname;
     private String name;
     private Long eId;
     private Long rowStatus;

    public KindDocument() {
    }

	
    public KindDocument(Long id) {
        this.id = id;
    }
    public KindDocument(Long id, String sysname, String name, Long eId, Long rowStatus) {
       this.id = id;
       this.sysname = sysname;
       this.name = name;
       this.eId = eId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_KINDDOCUMENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="Sysname")
    public String getSysname() {
        return this.sysname;
    }
    
    public void setSysname(String sysname) {
        this.sysname = sysname;
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

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


