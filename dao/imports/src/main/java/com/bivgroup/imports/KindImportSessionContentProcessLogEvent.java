package com.bivgroup.imports;
// Generated 21.04.2018 14:22:42 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * KindImportSessionContentProcessLogEvent Generated 21.04.2018 14:22:42 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="HB_IS_PROCESS_LOG_EVENT"
)
public class KindImportSessionContentProcessLogEvent  implements java.io.Serializable {


     private Long id;
     private Long entityId;
     private String sysName;
     private String name;
     private String note;
     private String messageTemplate;
     private String entityName;
     private Long rowStatus;

    public KindImportSessionContentProcessLogEvent() {
    }

	
    public KindImportSessionContentProcessLogEvent(Long id) {
        this.id = id;
    }
    public KindImportSessionContentProcessLogEvent(Long id, Long entityId, String sysName, String name, String note, String messageTemplate, String entityName, Long rowStatus) {
       this.id = id;
       this.entityId = entityId;
       this.sysName = sysName;
       this.name = name;
       this.note = note;
       this.messageTemplate = messageTemplate;
       this.entityName = entityName;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_IS_PROCESS_LOG_EVENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="entityId")
    public Long getEntityId() {
        return this.entityId;
    }
    
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    
    @Column(name="sysName")
    public String getSysName() {
        return this.sysName;
    }
    
    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="note")
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }

    
    @Column(name="messageTemplate")
    public String getMessageTemplate() {
        return this.messageTemplate;
    }
    
    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    
    @Column(name="entityName")
    public String getEntityName() {
        return this.entityName;
    }
    
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


