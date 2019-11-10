package com.bivgroup.imports;
// Generated 20.04.2018 19:10:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.core.aspect.impl.state.AspectStateSM;
import com.bivgroup.crm.SMState;

import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * ImportSessionContentManagerContract Generated 20.04.2018 19:10:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionContentManagerContract") 
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id") 
@Table(name="B2B_IS_CNT_MANAGER_CONTRACT"
)
@AspectStateSM(attributeName = "stateId_EN", startStateName = "B2B_IMPORTSESSION_CNT_INVALID", typeSysName = "B2B_IMPORTSESSION_CNT")
public class ImportSessionContentManagerContract extends com.bivgroup.imports.ImportSessionContent implements java.io.Serializable {


     private Long managerPersonnelNumber;
     private String managerCode;
     private String contractNumber;

    public ImportSessionContentManagerContract() {
    }

	
    public ImportSessionContentManagerContract(Long id) {
        super(id);        
    }
    public ImportSessionContentManagerContract(Long id, Long createUserId, Date createDate, Long updateUserId, Date updateDate, SMState stateId_EN, Long stateId, ImportSession importSessionId_EN, Long importSessionId, Set<ImportSessionContentProcessLogEntry> events, Long rowStatus, Long managerPersonnelNumber, String managerCode, String contractNumber) {
        super(id, createUserId, createDate, updateUserId, updateDate, stateId_EN, stateId, importSessionId_EN, importSessionId, events, rowStatus);        
       this.managerPersonnelNumber = managerPersonnelNumber;
       this.managerCode = managerCode;
       this.contractNumber = contractNumber;
    }
   

    
    @Column(name="managerPersonnelNumber")
    public Long getManagerPersonnelNumber() {
        return this.managerPersonnelNumber;
    }
    
    public void setManagerPersonnelNumber(Long managerPersonnelNumber) {
        this.managerPersonnelNumber = managerPersonnelNumber;
    }

    
    @Column(name="managerCode")
    public String getManagerCode() {
        return this.managerCode;
    }
    
    public void setManagerCode(String managerCode) {
        this.managerCode = managerCode;
    }

    
    @Column(name="contractNumber")
    public String getContractNumber() {
        return this.contractNumber;
    }
    
    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }




}


