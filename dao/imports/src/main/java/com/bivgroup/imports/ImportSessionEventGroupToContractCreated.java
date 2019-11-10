package com.bivgroup.imports;
// Generated 06.05.2018 15:14:37 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ImportSessionEventGroupToContractCreated Generated 06.05.2018 15:14:37 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionEventGroupToContractCreated")   
@Table(name="B2B_IS_CNT_PROCESS_LOG"
)
public class ImportSessionEventGroupToContractCreated extends com.bivgroup.imports.ImportSessionContentProcessLogEntry implements java.io.Serializable {


     private Long groupId;
     private Long contrOrgStruct2Id;
     private Long contrOrgStructId;
     private Long contractId;

    public ImportSessionEventGroupToContractCreated() {
    }

	
    public ImportSessionEventGroupToContractCreated(Long id) {
        super(id);        
    }
    public ImportSessionEventGroupToContractCreated(Long id, String note, Long createUserId, Date createDate, Long updateUserId, Date updateDate, ImportSessionContent importSessionCntId_EN, Long importSessionCntId, Long rowStatus, Long groupId, Long contrOrgStruct2Id, Long contrOrgStructId, Long contractId) {
        super(id, note, createUserId, createDate, updateUserId, updateDate, importSessionCntId_EN, importSessionCntId, rowStatus);        
       this.groupId = groupId;
       this.contrOrgStruct2Id = contrOrgStruct2Id;
       this.contrOrgStructId = contrOrgStructId;
       this.contractId = contractId;
    }
   

    
    @Column(name="groupId")
    public Long getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
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




}


