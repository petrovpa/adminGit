package com.bivgroup.loss;
// Generated Sep 1, 2017 4:21:35 PM unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * LossEvent Generated Sep 1, 2017 4:21:35 PM unknow unknow 
 */
@Entity
@Table(name="B2B_INSEVENT"
)
public class LossEvent  implements java.io.Serializable {


     private Long insEventId;
     private String name;
     private String sysname;
     private String hint;
     private String externalCode;
     private String note;
     private Long rowStatus;

    public LossEvent() {
    }

	
    public LossEvent(Long insEventId) {
        this.insEventId = insEventId;
    }
    public LossEvent(Long insEventId, String name, String sysname, String hint, String externalCode, String note, Long rowStatus) {
       this.insEventId = insEventId;
       this.name = name;
       this.sysname = sysname;
       this.hint = hint;
       this.externalCode = externalCode;
       this.note = note;
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

    
    @Column(name="hint")
    public String getHint() {
        return this.hint;
    }
    
    public void setHint(String hint) {
        this.hint = hint;
    }

    
    @Column(name="externalCode")
    public String getExternalCode() {
        return this.externalCode;
    }
    
    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    
    @Column(name="note")
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


