package com.bivgroup.imports;
// Generated 26.04.2018 19:25:20 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.core.aspect.impl.binaryfile.AspectBinaryFile;
import com.bivgroup.core.aspect.impl.state.AspectStateSM;
import com.bivgroup.crm.SMState;

import java.util.Date;
import java.util.Set;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ImportSessionManagerDepartment Generated 26.04.2018 19:25:20 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionManagerDepartment")   
@Table(name="B2B_IMPORTSESSION"
)
@AspectBinaryFile(entityName="ImportSessionManagerDepartment")
@AspectStateSM(attributeName = "stateId_EN", startStateName = "B2B_IMPORTSESSION_INLOADQUEUE", typeSysName = "B2B_IMPORTSESSION")
public class ImportSessionManagerDepartment extends com.bivgroup.imports.ImportSession implements java.io.Serializable {



    public ImportSessionManagerDepartment() {
    }

	
    public ImportSessionManagerDepartment(Long id) {
        super(id);        
    }
    public ImportSessionManagerDepartment(Long id, Date updateDate, Long updateUserId, Date createDate, Long createUserId, Long isLast, SMState stateId_EN, Long stateId, Set<ImportSessionContent> contents, Long rowStatus) {
        super(id, updateDate, updateUserId, createDate, createUserId, isLast, stateId_EN, stateId, contents, rowStatus);        
    }
   




}


