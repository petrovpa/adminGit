package com.bivgroup.imports;
// Generated 06.05.2018 15:14:38 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ImportSessionEventDepartmentToContractChanged Generated 06.05.2018 15:14:38 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionEventDepartmentToContractChanged")   
@Table(name="B2B_IS_CNT_PROCESS_LOG"
)
public class ImportSessionEventDepartmentToContractChanged extends com.bivgroup.imports.ImportSessionContentProcessLogEntry implements java.io.Serializable {


     private Long departmentId;
     private Long contrOrgStruct2Id;
     private Long contrOrgStructId;
     private Long contractId;
     private Long department2Id;
     private String departmentCode;
     private String departmentName;
     private String department2Code;
     private String department2Name;

    public ImportSessionEventDepartmentToContractChanged() {
    }

	
    public ImportSessionEventDepartmentToContractChanged(Long id) {
        super(id);        
    }
    public ImportSessionEventDepartmentToContractChanged(Long id, String note, Long createUserId, Date createDate, Long updateUserId, Date updateDate, ImportSessionContent importSessionCntId_EN, Long importSessionCntId, Long rowStatus, Long departmentId, Long contrOrgStruct2Id, Long contrOrgStructId, Long contractId, Long department2Id, String departmentCode, String departmentName, String department2Code, String department2Name) {
        super(id, note, createUserId, createDate, updateUserId, updateDate, importSessionCntId_EN, importSessionCntId, rowStatus);        
       this.departmentId = departmentId;
       this.contrOrgStruct2Id = contrOrgStruct2Id;
       this.contrOrgStructId = contrOrgStructId;
       this.contractId = contractId;
       this.department2Id = department2Id;
       this.departmentCode = departmentCode;
       this.departmentName = departmentName;
       this.department2Code = department2Code;
       this.department2Name = department2Name;
    }
   

    
    @Column(name="departmentId")
    public Long getDepartmentId() {
        return this.departmentId;
    }
    
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    
    @Column(name="contrOrgStruct2Id")
    public Long getContrOrgStruct2Id() {
        return this.contrOrgStruct2Id;
    }
    
    public void setContrOrgStruct2Id(Long contrOrgStruct2Id) {
        this.contrOrgStruct2Id = contrOrgStruct2Id;
    }

    
    @Column(name="contrOrgStructId")
    public Long getContrOrgStructId() {
        return this.contrOrgStructId;
    }
    
    public void setContrOrgStructId(Long contrOrgStructId) {
        this.contrOrgStructId = contrOrgStructId;
    }

    
    @Column(name="contractId")
    public Long getContractId() {
        return this.contractId;
    }
    
    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    
    @Column(name="department2Id")
    public Long getDepartment2Id() {
        return this.department2Id;
    }
    
    public void setDepartment2Id(Long department2Id) {
        this.department2Id = department2Id;
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




}


