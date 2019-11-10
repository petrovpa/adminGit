package com.bivgroup.imports;
// Generated 08.05.2018 18:22:55 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * KindImportSessionDepartmentFileExceptionRule Generated 08.05.2018 18:22:55 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "KindImportSessionDepartmentFileExceptionRule")   
@Table(name="HB_IS_FILEEXRULE"
)
public class KindImportSessionDepartmentFileExceptionRule extends com.bivgroup.imports.KindImportSessionFileExceptionRule implements java.io.Serializable {



    public KindImportSessionDepartmentFileExceptionRule() {
    }

	
    public KindImportSessionDepartmentFileExceptionRule(Long id) {
        super(id);        
    }
    public KindImportSessionDepartmentFileExceptionRule(Long id, String valueReal, String valueFile, Long rowStatus) {
        super(id, valueReal, valueFile, rowStatus);        
    }
   




}


