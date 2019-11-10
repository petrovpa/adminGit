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
 * KindImportSessionDepartmentType Generated 15.04.2018 13:13:19 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="HB_IS_DEPTYPE"
)
public class KindImportSessionDepartmentType  implements java.io.Serializable {


     private Long id;
     private String sysName;
     private String name;
     private String note;
     private Long rowStatus;

    public KindImportSessionDepartmentType() {
    }

	
    public KindImportSessionDepartmentType(Long id) {
        this.id = id;
    }
    public KindImportSessionDepartmentType(Long id, String sysName, String name, String note, Long rowStatus) {
       this.id = id;
       this.sysName = sysName;
       this.name = name;
       this.note = note;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_IS_DEPTYPE_SEQ", allocationSize = 10)
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


