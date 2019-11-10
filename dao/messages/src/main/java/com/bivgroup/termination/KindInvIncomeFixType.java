package com.bivgroup.termination;
// Generated 05.09.2017 17:36:44 unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * KindInvIncomeFixType Generated 05.09.2017 17:36:44 unknow unknow 
 */
@Entity
@Table(name="HB_KINDINVINCOMEFIXTYPE"
)
public class KindInvIncomeFixType  implements java.io.Serializable {


     private Long id;
     private String description;
     private String note;
     private String name;
     private String externalId;
     private String sysName;
     private Long rowStatus;

    public KindInvIncomeFixType() {
    }

	
    public KindInvIncomeFixType(Long id) {
        this.id = id;
    }
    public KindInvIncomeFixType(Long id, String description, String note, String name, String externalId, String sysName, Long rowStatus) {
       this.id = id;
       this.description = description;
       this.note = note;
       this.name = name;
       this.externalId = externalId;
       this.sysName = sysName;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_KINDINVINCOMEFIXTYPE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="description")
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    
    @Column(name="note")
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }

    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="externalId")
    public String getExternalId() {
        return this.externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    
    @Column(name="sysName")
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


