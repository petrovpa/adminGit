package com.bivgroup.imports;
// Generated 21.04.2018 13:02:19 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.core.aspect.impl.state.AspectStateSM;
import com.bivgroup.crm.SMState;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * ImportSessionContentDepartment Generated 21.04.2018 13:02:19 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionContentDepartment") 
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id") 
@Table(name="B2B_IS_CNT_DEPARTMENT"
)
@AspectStateSM(attributeName = "stateId_EN", startStateName = "B2B_IMPORTSESSION_CNT_INVALID", typeSysName = "B2B_IMPORTSESSION_CNT")
public class ImportSessionContentDepartment extends com.bivgroup.imports.ImportSessionContent implements java.io.Serializable {


     private String region;
     private String locality;
     private String name;
     private String parentCode;
     private String code;
     private KindImportSessionDepartmentType typeId_EN;
     private Long typeId;
     private Set<ImportSessionDepartmentSegment> segments = new HashSet<ImportSessionDepartmentSegment>(0);

    public ImportSessionContentDepartment() {
    }

	
    public ImportSessionContentDepartment(Long id) {
        super(id);        
    }
    public ImportSessionContentDepartment(Long id, Long createUserId, Date createDate, Long updateUserId, Date updateDate, SMState stateId_EN, Long stateId, ImportSession importSessionId_EN, Long importSessionId, Set<ImportSessionContentProcessLogEntry> events, Long rowStatus, String region, String locality, String name, String parentCode, String code, KindImportSessionDepartmentType typeId_EN, Long typeId, Set<ImportSessionDepartmentSegment> segments) {
        super(id, createUserId, createDate, updateUserId, updateDate, stateId_EN, stateId, importSessionId_EN, importSessionId, events, rowStatus);        
       this.region = region;
       this.locality = locality;
       this.name = name;
       this.parentCode = parentCode;
       this.code = code;
       this.typeId_EN = typeId_EN;
       this.typeId = typeId;
       this.segments = segments;
    }
   

    
    @Column(name="region")
    public String getRegion() {
        return this.region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }

    
    @Column(name="locality")
    public String getLocality() {
        return this.locality;
    }
    
    public void setLocality(String locality) {
        this.locality = locality;
    }

    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="parentCode")
    public String getParentCode() {
        return this.parentCode;
    }
    
    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    
    @Column(name="code")
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="typeId")
    public KindImportSessionDepartmentType getTypeId_EN() {
        return this.typeId_EN;
    }
    
    public void setTypeId_EN(KindImportSessionDepartmentType typeId_EN) {
        this.typeId_EN = typeId_EN;
    }

    
    @Column(name="typeId", insertable=false, updatable=false)
    public Long getTypeId() {
        return this.typeId;
    }
    
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="departmentId_EN")
    public Set<ImportSessionDepartmentSegment> getSegments() {
        return this.segments;
    }
    
    public void setSegments(Set<ImportSessionDepartmentSegment> segments) {
        this.segments = segments;
    }




}


