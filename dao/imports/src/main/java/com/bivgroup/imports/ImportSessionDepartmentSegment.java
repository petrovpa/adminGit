package com.bivgroup.imports;
// Generated 15.04.2018 13:13:19 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * ImportSessionDepartmentSegment Generated 15.04.2018 13:13:19 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="B2B_IS_DEP_SEGMENT"
)
public class ImportSessionDepartmentSegment  implements java.io.Serializable {


     private Long id;
     private ImportSessionContentDepartment departmentId_EN;
     private Long departmentId;
     private KindImportSessionDepartmentSegment segmentId_EN;
     private Long segmentId;
     private Long rowStatus;

    public ImportSessionDepartmentSegment() {
    }

	
    public ImportSessionDepartmentSegment(Long id) {
        this.id = id;
    }
    public ImportSessionDepartmentSegment(Long id, ImportSessionContentDepartment departmentId_EN, Long departmentId, KindImportSessionDepartmentSegment segmentId_EN, Long segmentId, Long rowStatus) {
       this.id = id;
       this.departmentId_EN = departmentId_EN;
       this.departmentId = departmentId;
       this.segmentId_EN = segmentId_EN;
       this.segmentId = segmentId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "B2B_IS_DEP_SEGMENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="departmentId")
    public ImportSessionContentDepartment getDepartmentId_EN() {
        return this.departmentId_EN;
    }
    
    public void setDepartmentId_EN(ImportSessionContentDepartment departmentId_EN) {
        this.departmentId_EN = departmentId_EN;
    }

    
    @Column(name="departmentId", insertable=false, updatable=false)
    public Long getDepartmentId() {
        return this.departmentId;
    }
    
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="segmentId")
    public KindImportSessionDepartmentSegment getSegmentId_EN() {
        return this.segmentId_EN;
    }
    
    public void setSegmentId_EN(KindImportSessionDepartmentSegment segmentId_EN) {
        this.segmentId_EN = segmentId_EN;
    }

    
    @Column(name="segmentId", insertable=false, updatable=false)
    public Long getSegmentId() {
        return this.segmentId;
    }
    
    public void setSegmentId(Long segmentId) {
        this.segmentId = segmentId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


