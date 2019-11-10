package com.bivgroup.termination;
// Generated 18.10.2017 15:05:21 unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * KindDeclarationDiscriminator Generated 18.10.2017 15:05:21 unknow unknow 
 */
@Entity
@Table(name="HB_KINDDECLARATIONDISCR"
)
public class KindDeclarationDiscriminator  implements java.io.Serializable {


     private Long id;
     private String sysName;
     private String name;
     private Long rowStatus;

    public KindDeclarationDiscriminator() {
    }

	
    public KindDeclarationDiscriminator(Long id) {
        this.id = id;
    }
    public KindDeclarationDiscriminator(Long id, String sysName, String name, Long rowStatus) {
       this.id = id;
       this.sysName = sysName;
       this.name = name;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_KINDDECLARATIONDISCR_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="sysName")
    public String getSysName() {
        return this.sysName;
    }
    
    public void setSysName(String sysName) {
        this.sysName = sysName;
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


