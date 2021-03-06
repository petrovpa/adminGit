package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:22 AM unknow unknow 


import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * KindCompetentOrg Generated Aug 7, 2017 10:42:22 AM unknow unknow 
 */
@Entity
@Table(name="HB_KINDCOMPETENTORG"
)
public class KindCompetentOrg  implements java.io.Serializable {


     private Long id;
     private String name;
     private String sysname;
     private Long rowStatus;

    public KindCompetentOrg() {
    }

	
    public KindCompetentOrg(Long id) {
        this.id = id;
    }
    public KindCompetentOrg(Long id, String name, String sysname, Long rowStatus) {
       this.id = id;
       this.name = name;
       this.sysname = sysname;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_KINDCOMPETENTORG_SEQ", allocationSize = 10)
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

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


