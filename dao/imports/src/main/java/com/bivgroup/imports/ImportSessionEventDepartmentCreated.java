package com.bivgroup.imports;
// Generated 06.05.2018 15:14:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ImportSessionEventDepartmentCreated Generated 06.05.2018 15:14:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionEventDepartmentCreated")   
@Table(name="B2B_IS_CNT_PROCESS_LOG"
)
public class ImportSessionEventDepartmentCreated extends com.bivgroup.imports.ImportSessionContentProcessLogEntry implements java.io.Serializable {


     private Long departmentId;
     private String departmentName;
     private String departmentCode;

    public ImportSessionEventDepartmentCreated() {
    }

	
    public ImportSessionEventDepartmentCreated(Long id) {
        super(id);        
    }
    public ImportSessionEventDepartmentCreated(Long id, String note, Long createUserId, Date createDate, Long updateUserId, Date updateDate, ImportSessionContent importSessionCntId_EN, Long importSessionCntId, Long rowStatus, Long departmentId, String departmentName, String departmentCode) {
        super(id, note, createUserId, createDate, updateUserId, updateDate, importSessionCntId_EN, importSessionCntId, rowStatus);        
       this.departmentId = departmentId;
       this.departmentName = departmentName;
       this.departmentCode = departmentCode;
    }
   

    
    @Column(name="departmentId")
    public Long getDepartmentId() {
        return this.departmentId;
    }
    
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    
    @Column(name="departmentName")
    public String getDepartmentName() {
        return this.departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    
    @Column(name="departmentCode")
    public String getDepartmentCode() {
        return this.departmentCode;
    }
    
    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }




}


