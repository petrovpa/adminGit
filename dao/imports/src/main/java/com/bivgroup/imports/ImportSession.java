package com.bivgroup.imports;
// Generated 26.04.2018 19:25:18 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * ImportSession Generated 26.04.2018 19:25:18 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="B2B_IMPORTSESSION"
)
public class ImportSession  implements java.io.Serializable {


     private Long id;
     private Date updateDate;
     private Long updateUserId;
     private Date createDate;
     private Long createUserId;
     private Long isLast;
     private SMState stateId_EN;
     private Long stateId;
     private Set<ImportSessionContent> contents = new HashSet<ImportSessionContent>(0);
     private Long rowStatus;

    public ImportSession() {
    }

	
    public ImportSession(Long id) {
        this.id = id;
    }
    public ImportSession(Long id, Date updateDate, Long updateUserId, Date createDate, Long createUserId, Long isLast, SMState stateId_EN, Long stateId, Set<ImportSessionContent> contents, Long rowStatus) {
       this.id = id;
       this.updateDate = updateDate;
       this.updateUserId = updateUserId;
       this.createDate = createDate;
       this.createUserId = createUserId;
       this.isLast = isLast;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.contents = contents;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "B2B_IMPORTSESSION_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updateDate")
    public Date getUpdateDate() {
        return this.updateDate;
    }
    
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    
    @Column(name="updateUserId")
    public Long getUpdateUserId() {
        return this.updateUserId;
    }
    
    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createDate")
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    
    @Column(name="createUserId")
    public Long getCreateUserId() {
        return this.createUserId;
    }
    
    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    
    @Column(name="isLast")
    public Long getIsLast() {
        return this.isLast;
    }
    
    public void setIsLast(Long isLast) {
        this.isLast = isLast;
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

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="importSessionId_EN")
    public Set<ImportSessionContent> getContents() {
        return this.contents;
    }
    
    public void setContents(Set<ImportSessionContent> contents) {
        this.contents = contents;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


