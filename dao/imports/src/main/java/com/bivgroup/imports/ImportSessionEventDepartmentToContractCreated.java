package com.bivgroup.imports;
// Generated 06.05.2018 15:14:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ImportSessionEventDepartmentToContractCreated Generated 06.05.2018 15:14:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionEventDepartmentToContractCreated")   
@Table(name="B2B_IS_CNT_PROCESS_LOG"
)
public class ImportSessionEventDepartmentToContractCreated extends com.bivgroup.imports.ImportSessionContentProcessLogEntry implements java.io.Serializable {


     private String departmentCode;
     private String departmentName;
     private Long contrOrgStructId;
     private Long contractId;
     private Long departmentId;
     private Long contrOrgStruct2Id;

    public ImportSessionEventDepartmentToContractCreated() {
    }

	
    public ImportSessionEventDepartmentToContractCreated(Long id) {
        super(id);        
    }
    public ImportSessionEventDepartmentToContractCreated(Long id, String note, Long createUserId, Date createDate, Long updateUserId, Date updateDate, ImportSessionContent importSessionCntId_EN, Long importSessionCntId, Long rowStatus, String departmentCode, String departmentName, Long contrOrgStructId, Long contractId, Long departmentId, Long contrOrgStruct2Id) {
        super(id, note, createUserId, createDate, updateUserId, updateDate, importSessionCntId_EN, importSessionCntId, rowStatus);        
       this.departmentCode = departmentCode;
       this.departmentName = departmentName;
       this.contrOrgStructId = contrOrgStructId;
       this.contractId = contractId;
       this.departmentId = departmentId;
       this.contrOrgStruct2Id = contrOrgStruct2Id;
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




}


