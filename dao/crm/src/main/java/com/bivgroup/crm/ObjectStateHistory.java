package com.bivgroup.crm;
// Generated 29.05.2018 16:26:14 core-dictionary-maven-plugin Maven Mojo 1.01.07-SNAPSHOT 


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
 * ObjectStateHistory Generated 29.05.2018 16:26:14 core-dictionary-maven-plugin Maven Mojo 1.01.07-SNAPSHOT 
 */
@Entity
@Table(name="INS_OBJSTATEH"
)
public class ObjectStateHistory  implements java.io.Serializable {


     private Long id;
     private String note;
     private String commentary;
     private String userName;
     private Double startDate;
     private String typeName;
     private String stateName;
     private Long objId;
     private SMType typeId_EN;
     private Long typeId;
     private SMState stateId_EN;
     private Long stateId;
     private Long rowStatus;

    public ObjectStateHistory() {
    }

	
    public ObjectStateHistory(Long id) {
        this.id = id;
    }
    public ObjectStateHistory(Long id, String note, String commentary, String userName, Double startDate, String typeName, String stateName, Long objId, SMType typeId_EN, Long typeId, SMState stateId_EN, Long stateId, Long rowStatus) {
       this.id = id;
       this.note = note;
       this.commentary = commentary;
       this.userName = userName;
       this.startDate = startDate;
       this.typeName = typeName;
       this.stateName = stateName;
       this.objId = objId;
       this.typeId_EN = typeId_EN;
       this.typeId = typeId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.rowStatus = rowStatus;
    }
   
 // @SequenceGenerator(name = "generator", sequenceName = "INS_OBJSTATEH_SEQ", allocationSize = 10)
 // @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, updatable=false)
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

    
    @Column(name="commentary")
    public String getCommentary() {
        return this.commentary;
    }
    
    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    
    @Column(name="userName")
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    
    @Column(name="startDate")
    public Double getStartDate() {
        return this.startDate;
    }
    
    public void setStartDate(Double startDate) {
        this.startDate = startDate;
    }

    
    @Column(name="typeName")
    public String getTypeName() {
        return this.typeName;
    }
    
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    
    @Column(name="stateName")
    public String getStateName() {
        return this.stateName;
    }
    
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    
    @Column(name="objId")
    public Long getObjId() {
        return this.objId;
    }
    
    public void setObjId(Long objId) {
        this.objId = objId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="typeId")
    public SMType getTypeId_EN() {
        return this.typeId_EN;
    }
    
    public void setTypeId_EN(SMType typeId_EN) {
        this.typeId_EN = typeId_EN;
    }

    
    @Column(name="typeId", insertable=false, updatable=false)
    public Long getTypeId() {
        return this.typeId;
    }
    
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
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

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


