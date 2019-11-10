package com.bivgroup.crm;
// Generated Nov 20, 2017 11:02:43 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * BinaryFile Generated Nov 20, 2017 11:02:43 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="INS_BINFILE"
)
public class BinaryFile  implements java.io.Serializable {


     private Long id;
     private String fsId;
     private String note;
     private Date createDate;
     private Long createUserId;
     private String objTableName;
     private Long objId;
     private String fileTypeName;
     private Long fileTypeId;
     private String fileSize;
     private String filePath;
     private String fileName;
     private Long rowStatus;

    public BinaryFile() {
    }

	
    public BinaryFile(Long id) {
        this.id = id;
    }
    public BinaryFile(Long id, String fsId, String note, Date createDate, Long createUserId, String objTableName, Long objId, String fileTypeName, Long fileTypeId, String fileSize, String filePath, String fileName, Long rowStatus) {
       this.id = id;
       this.fsId = fsId;
       this.note = note;
       this.createDate = createDate;
       this.createUserId = createUserId;
       this.objTableName = objTableName;
       this.objId = objId;
       this.fileTypeName = fileTypeName;
       this.fileTypeId = fileTypeId;
       this.fileSize = fileSize;
       this.filePath = filePath;
       this.fileName = fileName;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "INS_BINFILE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="BINFILEID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="FSID")
    public String getFsId() {
        return this.fsId;
    }
    
    public void setFsId(String fsId) {
        this.fsId = fsId;
    }

    
    @Column(name="NOTE")
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CREATEHDATE")
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    
    @Column(name="CREATEUSERID")
    public Long getCreateUserId() {
        return this.createUserId;
    }
    
    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    
    @Column(name="OBJTABLENAME")
    public String getObjTableName() {
        return this.objTableName;
    }
    
    public void setObjTableName(String objTableName) {
        this.objTableName = objTableName;
    }

    
    @Column(name="OBJID")
    public Long getObjId() {
        return this.objId;
    }
    
    public void setObjId(Long objId) {
        this.objId = objId;
    }

    
    @Column(name="FILETYPENAME")
    public String getFileTypeName() {
        return this.fileTypeName;
    }
    
    public void setFileTypeName(String fileTypeName) {
        this.fileTypeName = fileTypeName;
    }

    
    @Column(name="FILETYPEID")
    public Long getFileTypeId() {
        return this.fileTypeId;
    }
    
    public void setFileTypeId(Long fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    
    @Column(name="FILESIZE")
    public String getFileSize() {
        return this.fileSize;
    }
    
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    
    @Column(name="FILEPATH")
    public String getFilePath() {
        return this.filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    
    @Column(name="FILENAME")
    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


