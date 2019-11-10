package com.bivgroup.crm;
// Generated Dec 14, 2017 1:23:16 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * SMState Generated Dec 14, 2017 1:23:16 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="CORE_SM_STATE"
)
public class SMState  implements java.io.Serializable {


     private Long id;
     private String publicName;
     private String sysName;
     private SMType typeId_EN;
     private Long typeId;
     private Long rowStatus;

    public SMState() {
    }

	
    public SMState(Long id) {
        this.id = id;
    }
    public SMState(Long id, String publicName, String sysName, SMType typeId_EN, Long typeId, Long rowStatus) {
       this.id = id;
       this.publicName = publicName;
       this.sysName = sysName;
       this.typeId_EN = typeId_EN;
       this.typeId = typeId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CORE_SM_STATE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
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

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TYPEID")
    public SMType getTypeId_EN() {
        return this.typeId_EN;
    }
    
    public void setTypeId_EN(SMType typeId_EN) {
        this.typeId_EN = typeId_EN;
    }

    
    @Column(name="TYPEID", insertable=false, updatable=false)
    public Long getTypeId() {
        return this.typeId;
    }
    
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


