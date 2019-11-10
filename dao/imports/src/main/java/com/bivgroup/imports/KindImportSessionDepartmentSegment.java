package com.bivgroup.imports;
// Generated 15.04.2018 13:13:19 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * KindImportSessionDepartmentSegment Generated 15.04.2018 13:13:19 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="HB_IS_DEPSEGMENT"
)
public class KindImportSessionDepartmentSegment  implements java.io.Serializable {


     private Long id;
     private String note;
     private String name;
     private String sysName;
     private Long rowStatus;

    public KindImportSessionDepartmentSegment() {
    }

	
    public KindImportSessionDepartmentSegment(Long id) {
        this.id = id;
    }
    public KindImportSessionDepartmentSegment(Long id, String note, String name, String sysName, Long rowStatus) {
       this.id = id;
       this.note = note;
       this.name = name;
       this.sysName = sysName;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_IS_DEPSEGMENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
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


