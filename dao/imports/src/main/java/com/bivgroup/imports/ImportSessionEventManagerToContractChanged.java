package com.bivgroup.imports;
// Generated 06.05.2018 15:14:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ImportSessionEventManagerToContractChanged Generated 06.05.2018 15:14:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionEventManagerToContractChanged")   
@Table(name="B2B_IS_CNT_PROCESS_LOG"
)
public class ImportSessionEventManagerToContractChanged extends com.bivgroup.imports.ImportSessionContentProcessLogEntry implements java.io.Serializable {


     private Long contrOrgStruct2Id;
     private Long contrOrgStructId;
     private Long contractId;
     private Long manager2Id;
     private Long managerId;

    public ImportSessionEventManagerToContractChanged() {
    }

	
    public ImportSessionEventManagerToContractChanged(Long id) {
        super(id);        
    }
    public ImportSessionEventManagerToContractChanged(Long id, String note, Long createUserId, Date createDate, Long updateUserId, Date updateDate, ImportSessionContent importSessionCntId_EN, Long importSessionCntId, Long rowStatus, Long contrOrgStruct2Id, Long contrOrgStructId, Long contractId, Long manager2Id, Long managerId) {
        super(id, note, createUserId, createDate, updateUserId, updateDate, importSessionCntId_EN, importSessionCntId, rowStatus);        
       this.contrOrgStruct2Id = contrOrgStruct2Id;
       this.contrOrgStructId = contrOrgStructId;
       this.contractId = contractId;
       this.manager2Id = manager2Id;
       this.managerId = managerId;
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

    
    @Column(name="manager2Id")
    public Long getManager2Id() {
        return this.manager2Id;
    }
    
    public void setManager2Id(Long manager2Id) {
        this.manager2Id = manager2Id;
    }

    
    @Column(name="managerId")
    public Long getManagerId() {
        return this.managerId;
    }
    
    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }




}


