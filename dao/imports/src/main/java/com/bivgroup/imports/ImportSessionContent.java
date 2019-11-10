package com.bivgroup.imports;
// Generated 21.04.2018 13:02:18 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.SMState;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * ImportSessionContent Generated 21.04.2018 13:02:18 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)       
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="B2B_IMPORTSESSION_CNT"
)
public class ImportSessionContent  implements java.io.Serializable {


     private Long id;
     private Long createUserId;
     private Date createDate;
     private Long updateUserId;
     private Date updateDate;
     private SMState stateId_EN;
     private Long stateId;
     private ImportSession importSessionId_EN;
     private Long importSessionId;
     private Set<ImportSessionContentProcessLogEntry> events = new HashSet<ImportSessionContentProcessLogEntry>(0);
     private Long rowStatus;

    public ImportSessionContent() {
    }

	
    public ImportSessionContent(Long id) {
        this.id = id;
    }
    public ImportSessionContent(Long id, Long createUserId, Date createDate, Long updateUserId, Date updateDate, SMState stateId_EN, Long stateId, ImportSession importSessionId_EN, Long importSessionId, Set<ImportSessionContentProcessLogEntry> events, Long rowStatus) {
       this.id = id;
       this.createUserId = createUserId;
       this.createDate = createDate;
       this.updateUserId = updateUserId;
       this.updateDate = updateDate;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.importSessionId_EN = importSessionId_EN;
       this.importSessionId = importSessionId;
       this.events = events;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "B2B_IMPORTSESSION_CNT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    @JoinColumn(name="stateId")
    public SMState getStateId_EN() {
        return this.stateId_EN;
    }
    
    public void setStateId_EN(SMState stateId_EN) {
        this.stateId_EN = stateId_EN;
    }

    
    @Column(name="stateId", insertable=false, updatable=false)
    public Long getStateId() {
        return this.stateId;
    }
    
    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="importSessionId")
    public ImportSession getImportSessionId_EN() {
        return this.importSessionId_EN;
    }
    
    public void setImportSessionId_EN(ImportSession importSessionId_EN) {
        this.importSessionId_EN = importSessionId_EN;
    }

    
    @Column(name="importSessionId", insertable=false, updatable=false)
    public Long getImportSessionId() {
        return this.importSessionId;
    }
    
    public void setImportSessionId(Long importSessionId) {
        this.importSessionId = importSessionId;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="importSessionCntId_EN")
    public Set<ImportSessionContentProcessLogEntry> getEvents() {
        return this.events;
    }
    
    public void setEvents(Set<ImportSessionContentProcessLogEntry> events) {
        this.events = events;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


