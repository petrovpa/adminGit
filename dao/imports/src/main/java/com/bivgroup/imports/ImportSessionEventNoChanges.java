package com.bivgroup.imports;
// Generated 06.05.2018 15:14:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ImportSessionEventNoChanges Generated 06.05.2018 15:14:39 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionEventNoChanges")   
@Table(name="B2B_IS_CNT_PROCESS_LOG"
)
public class ImportSessionEventNoChanges extends com.bivgroup.imports.ImportSessionContentProcessLogEntry implements java.io.Serializable {



    public ImportSessionEventNoChanges() {
    }

	
    public ImportSessionEventNoChanges(Long id) {
        super(id);        
    }
    public ImportSessionEventNoChanges(Long id, String note, Long createUserId, Date createDate, Long updateUserId, Date updateDate, ImportSessionContent importSessionCntId_EN, Long importSessionCntId, Long rowStatus) {
        super(id, note, createUserId, createDate, updateUserId, updateDate, importSessionCntId_EN, importSessionCntId, rowStatus);        
    }
   




}


