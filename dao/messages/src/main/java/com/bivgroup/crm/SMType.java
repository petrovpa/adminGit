package com.bivgroup.crm;
// Generated 14.12.2017 18:39:21 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * SMType Generated 14.12.2017 18:39:21 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="CORE_SM_TYPE"
)
public class SMType  implements java.io.Serializable {


     private Long id;
     private Long packageId;
     private String publicName;
     private String sysName;
     private Long rowStatus;

    public SMType() {
    }

	
    public SMType(Long id) {
        this.id = id;
    }
    public SMType(Long id, Long packageId, String publicName, String sysName, Long rowStatus) {
       this.id = id;
       this.packageId = packageId;
       this.publicName = publicName;
       this.sysName = sysName;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CORE_SM_TYPE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="PACKAGEID")
    public Long getPackageId() {
        return this.packageId;
    }
    
    public void setPackageId(Long packageId) {
        this.packageId = packageId;
    }

    
    @Column(name="PUBLICNAME")
    public String getPublicName() {
        return this.publicName;
    }
    
    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    
    @Column(name="SYSNAME")
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


