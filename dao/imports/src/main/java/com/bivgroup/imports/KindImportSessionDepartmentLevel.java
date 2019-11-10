package com.bivgroup.imports;
// Generated 27.04.2018 11:24:25 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * KindImportSessionDepartmentLevel Generated 27.04.2018 11:24:25 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="DEP_DEPTLEVEL"
)
public class KindImportSessionDepartmentLevel  implements java.io.Serializable {


     private Long id;
     private String description;
     private String levelName;
     private String levelSysName;
     private Long isSystem;
     private Long levelWeight;
     private Long rowStatus;

    public KindImportSessionDepartmentLevel() {
    }

	
    public KindImportSessionDepartmentLevel(Long id) {
        this.id = id;
    }
    public KindImportSessionDepartmentLevel(Long id, String description, String levelName, String levelSysName, Long isSystem, Long levelWeight, Long rowStatus) {
       this.id = id;
       this.description = description;
       this.levelName = levelName;
       this.levelSysName = levelSysName;
       this.isSystem = isSystem;
       this.levelWeight = levelWeight;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "DEP_DEPTLEVEL_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="deptLevelId", nullable=false, insertable=false, updatable=false)
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

    
    @Column(name="levelName")
    public String getLevelName() {
        return this.levelName;
    }
    
    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    
    @Column(name="levelSysName")
    public String getLevelSysName() {
        return this.levelSysName;
    }
    
    public void setLevelSysName(String levelSysName) {
        this.levelSysName = levelSysName;
    }

    
    @Column(name="isSystem")
    public Long getIsSystem() {
        return this.isSystem;
    }
    
    public void setIsSystem(Long isSystem) {
        this.isSystem = isSystem;
    }

    
    @Column(name="levelWeight")
    public Long getLevelWeight() {
        return this.levelWeight;
    }
    
    public void setLevelWeight(Long levelWeight) {
        this.levelWeight = levelWeight;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


