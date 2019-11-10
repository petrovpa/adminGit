package com.bivgroup.crm;
// Generated Dec 14, 2017 1:17:20 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * SMTransition Generated Dec 14, 2017 1:17:20 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="CORE_SM_TRANS"
)
public class SMTransition  implements java.io.Serializable {


     private Long id;
     private String publicName;
     private String sysName;
     private SMType typeId_EN;
     private Long typeId;
     private SMState toStateId_EN;
     private Long toStateId;
     private SMState fromStateId_EN;
     private Long fromStateId;
     private Long rowStatus;

    public SMTransition() {
    }

	
    public SMTransition(Long id) {
        this.id = id;
    }
    public SMTransition(Long id, String publicName, String sysName, SMType typeId_EN, Long typeId, SMState toStateId_EN, Long toStateId, SMState fromStateId_EN, Long fromStateId, Long rowStatus) {
       this.id = id;
       this.publicName = publicName;
       this.sysName = sysName;
       this.typeId_EN = typeId_EN;
       this.typeId = typeId;
       this.toStateId_EN = toStateId_EN;
       this.toStateId = toStateId;
       this.fromStateId_EN = fromStateId_EN;
       this.fromStateId = fromStateId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CORE_SM_TRANS_SEQ", allocationSize = 10)
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

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TOSTATEID")
    public SMState getToStateId_EN() {
        return this.toStateId_EN;
    }
    
    public void setToStateId_EN(SMState toStateId_EN) {
        this.toStateId_EN = toStateId_EN;
    }

    
    @Column(name="TOSTATEID", insertable=false, updatable=false)
    public Long getToStateId() {
        return this.toStateId;
    }
    
    public void setToStateId(Long toStateId) {
        this.toStateId = toStateId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="FROMSTATEID")
    public SMState getFromStateId_EN() {
        return this.fromStateId_EN;
    }
    
    public void setFromStateId_EN(SMState fromStateId_EN) {
        this.fromStateId_EN = fromStateId_EN;
    }

    
    @Column(name="FROMSTATEID", insertable=false, updatable=false)
    public Long getFromStateId() {
        return this.fromStateId;
    }
    
    public void setFromStateId(Long fromStateId) {
        this.fromStateId = fromStateId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


