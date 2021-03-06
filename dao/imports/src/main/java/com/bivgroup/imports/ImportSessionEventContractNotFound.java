package com.bivgroup.imports;
// Generated 06.05.2018 15:14:40 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ImportSessionEventContractNotFound Generated 06.05.2018 15:14:40 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionEventContractNotFound")   
@Table(name="B2B_IS_CNT_PROCESS_LOG"
)
public class ImportSessionEventContractNotFound extends com.bivgroup.imports.ImportSessionContentProcessLogEntry implements java.io.Serializable {


     private String searchValue;

    public ImportSessionEventContractNotFound() {
    }

	
    public ImportSessionEventContractNotFound(Long id) {
        super(id);        
    }
    public ImportSessionEventContractNotFound(Long id, String note, Long createUserId, Date createDate, Long updateUserId, Date updateDate, ImportSessionContent importSessionCntId_EN, Long importSessionCntId, Long rowStatus, String searchValue) {
        super(id, note, createUserId, createDate, updateUserId, updateDate, importSessionCntId_EN, importSessionCntId, rowStatus);        
       this.searchValue = searchValue;
    }
   

    
    @Column(name="searchValue")
    public String getSearchValue() {
        return this.searchValue;
    }
    
    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }




}


