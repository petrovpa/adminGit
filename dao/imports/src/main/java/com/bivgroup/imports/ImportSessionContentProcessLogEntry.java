package com.bivgroup.imports;
// Generated 21.04.2018 13:02:18 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * ImportSessionContentProcessLogEntry Generated 21.04.2018 13:02:18 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="B2B_IS_CNT_PROCESS_LOG"
)
public class ImportSessionContentProcessLogEntry  implements java.io.Serializable {


     private Long id;
     private String note;
     private Long createUserId;
     private Date createDate;
     private Long updateUserId;
     private Date updateDate;
     private ImportSessionContent importSessionCntId_EN;
     private Long importSessionCntId;
     private Long rowStatus;

    public ImportSessionContentProcessLogEntry() {
    }

	
    public ImportSessionContentProcessLogEntry(Long id) {
        this.id = id;
    }
    public ImportSessionContentProcessLogEntry(Long id, String note, Long createUserId, Date createDate, Long updateUserId, Date updateDate, ImportSessionContent importSessionCntId_EN, Long importSessionCntId, Long rowStatus) {
       this.id = id;
       this.note = note;
       this.createUserId = createUserId;
       this.createDate = createDate;
       this.updateUserId = updateUserId;
       this.updateDate = updateDate;
       this.importSessionCntId_EN = importSessionCntId_EN;
       this.importSessionCntId = importSessionCntId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "B2B_IS_CNT_PROCESS_LOG_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="note")
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }

    
    @Column(name="createUserId")
    public Long getCreateUserId() {
        return this.createUserId;
    }
    
    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createDate")
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    
    @Column(name="updateUserId")
    public Long getUpdateUserId() {
        return this.updateUserId;
    }
    
    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updateDate")
    public Date getUpdateDate() {
        return this.updateDate;
    }
    
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="importSessionCntId")
    public ImportSessionContent getImportSessionCntId_EN() {
        return this.importSessionCntId_EN;
    }
    
    public void setImportSessionCntId_EN(ImportSessionContent importSessionCntId_EN) {
        this.importSessionCntId_EN = importSessionCntId_EN;
    }

    
    @Column(name="importSessionCntId", insertable=false, updatable=false)
    public Long getImportSessionCntId() {
        return this.importSessionCntId;
    }
    
    public void setImportSessionCntId(Long importSessionCntId) {
        this.importSessionCntId = importSessionCntId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


