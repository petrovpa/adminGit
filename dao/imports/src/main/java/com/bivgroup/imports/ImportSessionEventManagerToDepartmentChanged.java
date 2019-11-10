package com.bivgroup.imports;
// Generated 06.05.2018 15:14:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ImportSessionEventManagerToDepartmentChanged Generated 06.05.2018 15:14:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionEventManagerToDepartmentChanged")   
@Table(name="B2B_IS_CNT_PROCESS_LOG"
)
public class ImportSessionEventManagerToDepartmentChanged extends com.bivgroup.imports.ImportSessionContentProcessLogEntry implements java.io.Serializable {


     private String departmentCode;
     private String departmentName;
     private Long department2Id;
     private String department2Code;
     private String department2Name;
     private Long departmentId;
     private Long managerId;

    public ImportSessionEventManagerToDepartmentChanged() {
    }

	
    public ImportSessionEventManagerToDepartmentChanged(Long id) {
        super(id);        
    }
    public ImportSessionEventManagerToDepartmentChanged(Long id, String note, Long createUserId, Date createDate, Long updateUserId, Date updateDate, ImportSessionContent importSessionCntId_EN, Long importSessionCntId, Long rowStatus, String departmentCode, String departmentName, Long department2Id, String department2Code, String department2Name, Long departmentId, Long managerId) {
        super(id, note, createUserId, createDate, updateUserId, updateDate, importSessionCntId_EN, importSessionCntId, rowStatus);        
       this.departmentCode = departmentCode;
       this.departmentName = departmentName;
       this.department2Id = department2Id;
       this.department2Code = department2Code;
       this.department2Name = department2Name;
       this.departmentId = departmentId;
       this.managerId = managerId;
    }
   

    
    @Column(name="departmentCode")
    public String getDepartmentCode() {
        return this.departmentCode;
    }
    
    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    
    @Column(name="departmentName")
    public String getDepartmentName() {
        return this.departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    
    @Column(name="department2Id")
    public Long getDepartment2Id() {
        return this.department2Id;
    }
    
    public void setDepartment2Id(Long department2Id) {
        this.department2Id = department2Id;
    }

    
    @Column(name="department2Code")
    public String getDepartment2Code() {
        return this.department2Code;
    }
    
    public void setDepartment2Code(String department2Code) {
        this.department2Code = department2Code;
    }

    
    @Column(name="department2Name")
    public String getDepartment2Name() {
        return this.department2Name;
    }
    
    public void setDepartment2Name(String department2Name) {
        this.department2Name = department2Name;
    }

    
    @Column(name="departmentId")
    public Long getDepartmentId() {
        return this.departmentId;
    }
    
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    
    @Column(name="managerId")
    public Long getManagerId() {
        return this.managerId;
    }
    
    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }




}


