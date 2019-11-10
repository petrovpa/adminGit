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
 * LossEvent Generated 14.12.2017 18:39:27 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="B2B_INSEVENT"
)
public class LossEvent  implements java.io.Serializable {


     private Long insEventId;
     private String note;
     private String externalCode;
     private String hint;
     private String sysname;
     private String name;
     private Long rowStatus;

    public LossEvent() {
    }

	
    public LossEvent(Long insEventId) {
        this.insEventId = insEventId;
    }
    public LossEvent(Long insEventId, String note, String externalCode, String hint, String sysname, String name, Long rowStatus) {
       this.insEventId = insEventId;
       this.note = note;
       this.externalCode = externalCode;
       this.hint = hint;
       this.sysname = sysname;
       this.name = name;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "B2B_INSEVENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="insEventId", nullable=false, insertable=false, updatable=false)
    public Long getInsEventId() {
        return this.insEventId;
    }
    
    public void setInsEventId(Long insEventId) {
        this.insEventId = insEventId;
    }

    
    @Column(name="note")
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
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


