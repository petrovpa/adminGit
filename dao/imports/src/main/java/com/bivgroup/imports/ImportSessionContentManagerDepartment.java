package com.bivgroup.imports;
// Generated 20.04.2018 19:10:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.core.aspect.impl.state.AspectStateSM;
import com.bivgroup.crm.SMState;

import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * ImportSessionContentManagerDepartment Generated 20.04.2018 19:10:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ImportSessionContentManagerDepartment") 
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id") 
@Table(name="B2B_IS_CNT_MANAGER_DEPARTMENT"
)
@AspectStateSM(attributeName = "stateId_EN", startStateName = "B2B_IMPORTSESSION_CNT_INVALID", typeSysName = "B2B_IMPORTSESSION_CNT")
public class ImportSessionContentManagerDepartment extends com.bivgroup.imports.ImportSessionContent implements java.io.Serializable {


     private String territorialBankExternalId;
     private String territorialBankName;
     private String departmentName;
     private String departmentExternalId;
     private Date departmentCloseDate;
     private Date departmentOpenDate;
     private String departmentCity;
     private String headBranchCode;
     private String territorialBankCode;
     private String departmentCode;
     private Date managerDismissalDate;
     private Date managerHireDate;
     private String managerFullName;
     private String managerEMail;
     private Long managerPersonnelNumber;
     private String externalId;
     private KindImportSessionManagerRole managerRoleId_EN;
     private Long managerRoleId;
     private KindImportSessionManagerPosition managerPositionId_EN;
     private Long managerPositionId;

    public ImportSessionContentManagerDepartment() {
    }

	
    public ImportSessionContentManagerDepartment(Long id) {
        super(id);        
    }
    public ImportSessionContentManagerDepartment(Long id, Long createUserId, Date createDate, Long updateUserId, Date updateDate, SMState stateId_EN, Long stateId, ImportSession importSessionId_EN, Long importSessionId, Set<ImportSessionContentProcessLogEntry> events, Long rowStatus, String territorialBankExternalId, String territorialBankName, String departmentName, String departmentExternalId, Date departmentCloseDate, Date departmentOpenDate, String departmentCity, String headBranchCode, String territorialBankCode, String departmentCode, Date managerDismissalDate, Date managerHireDate, String managerFullName, String managerEMail, Long managerPersonnelNumber, String externalId, KindImportSessionManagerRole managerRoleId_EN, Long managerRoleId, KindImportSessionManagerPosition managerPositionId_EN, Long managerPositionId) {
        super(id, createUserId, createDate, updateUserId, updateDate, stateId_EN, stateId, importSessionId_EN, importSessionId, events, rowStatus);        
       this.territorialBankExternalId = territorialBankExternalId;
       this.territorialBankName = territorialBankName;
       this.departmentName = departmentName;
       this.departmentExternalId = departmentExternalId;
       this.departmentCloseDate = departmentCloseDate;
       this.departmentOpenDate = departmentOpenDate;
       this.departmentCity = departmentCity;
       this.headBranchCode = headBranchCode;
       this.territorialBankCode = territorialBankCode;
       this.departmentCode = departmentCode;
       this.managerDismissalDate = managerDismissalDate;
       this.managerHireDate = managerHireDate;
       this.managerFullName = managerFullName;
       this.managerEMail = managerEMail;
       this.managerPersonnelNumber = managerPersonnelNumber;
       this.externalId = externalId;
       this.managerRoleId_EN = managerRoleId_EN;
       this.managerRoleId = managerRoleId;
       this.managerPositionId_EN = managerPositionId_EN;
       this.managerPositionId = managerPositionId;
    }
   

    
    @Column(name="territorialBankExternalId")
    public String getTerritorialBankExternalId() {
        return this.territorialBankExternalId;
    }
    
    public void setTerritorialBankExternalId(String territorialBankExternalId) {
        this.territorialBankExternalId = territorialBankExternalId;
    }

    
    @Column(name="territorialBankName")
    public String getTerritorialBankName() {
        return this.territorialBankName;
    }
    
    public void setTerritorialBankName(String territorialBankName) {
        this.territorialBankName = territorialBankName;
    }

    
    @Column(name="departmentName")
    public String getDepartmentName() {
        return this.departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    
    @Column(name="departmentExternalId")
    public String getDepartmentExternalId() {
        return this.departmentExternalId;
    }
    
    public void setDepartmentExternalId(String departmentExternalId) {
        this.departmentExternalId = departmentExternalId;
    }

    
    @Column(name="departmentCloseDate")
    public Date getDepartmentCloseDate() {
        return this.departmentCloseDate;
    }
    
    public void setDepartmentCloseDate(Date departmentCloseDate) {
        this.departmentCloseDate = departmentCloseDate;
    }

    
    @Column(name="departmentOpenDate")
    public Date getDepartmentOpenDate() {
        return this.departmentOpenDate;
    }
    
    public void setDepartmentOpenDate(Date departmentOpenDate) {
        this.departmentOpenDate = departmentOpenDate;
    }

    
    @Column(name="departmentCity")
    public String getDepartmentCity() {
        return this.departmentCity;
    }
    
    public void setDepartmentCity(String departmentCity) {
        this.departmentCity = departmentCity;
    }

    
    @Column(name="headBranchCode")
    public String getHeadBranchCode() {
        return this.headBranchCode;
    }
    
    public void setHeadBranchCode(String headBranchCode) {
        this.headBranchCode = headBranchCode;
    }

    
    @Column(name="territorialBankCode")
    public String getTerritorialBankCode() {
        return this.territorialBankCode;
    }
    
    public void setTerritorialBankCode(String territorialBankCode) {
        this.territorialBankCode = territorialBankCode;
    }

    
    @Column(name="departmentCode")
    public String getDepartmentCode() {
        return this.departmentCode;
    }
    
    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    
    @Column(name="managerDismissalDate")
    public Date getManagerDismissalDate() {
        return this.managerDismissalDate;
    }
    
    public void setManagerDismissalDate(Date managerDismissalDate) {
        this.managerDismissalDate = managerDismissalDate;
    }

    
    @Column(name="managerHireDate")
    public Date getManagerHireDate() {
        return this.managerHireDate;
    }
    
    public void setManagerHireDate(Date managerHireDate) {
        this.managerHireDate = managerHireDate;
    }

    
    @Column(name="managerFullName")
    public String getManagerFullName() {
        return this.managerFullName;
    }
    
    public void setManagerFullName(String managerFullName) {
        this.managerFullName = managerFullName;
    }

    
    @Column(name="managerEMail")
    public String getManagerEMail() {
        return this.managerEMail;
    }
    
    public void setManagerEMail(String managerEMail) {
        this.managerEMail = managerEMail;
    }

    
    @Column(name="managerPersonnelNumber")
    public Long getManagerPersonnelNumber() {
        return this.managerPersonnelNumber;
    }
    
    public void setManagerPersonnelNumber(Long managerPersonnelNumber) {
        this.managerPersonnelNumber = managerPersonnelNumber;
    }

    
    @Column(name="externalId")
    public String getExternalId() {
        return this.externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="managerRoleId")
    public KindImportSessionManagerRole getManagerRoleId_EN() {
        return this.managerRoleId_EN;
    }
    
    public void setManagerRoleId_EN(KindImportSessionManagerRole managerRoleId_EN) {
        this.managerRoleId_EN = managerRoleId_EN;
    }

    
    @Column(name="managerRoleId", insertable=false, updatable=false)
    public Long getManagerRoleId() {
        return this.managerRoleId;
    }
    
    public void setManagerRoleId(Long managerRoleId) {
        this.managerRoleId = managerRoleId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="managerPositionId")
    public KindImportSessionManagerPosition getManagerPositionId_EN() {
        return this.managerPositionId_EN;
    }
    
    public void setManagerPositionId_EN(KindImportSessionManagerPosition managerPositionId_EN) {
        this.managerPositionId_EN = managerPositionId_EN;
    }

    
    @Column(name="managerPositionId", insertable=false, updatable=false)
    public Long getManagerPositionId() {
        return this.managerPositionId;
    }
    
    public void setManagerPositionId(Long managerPositionId) {
        this.managerPositionId = managerPositionId;
    }




}


