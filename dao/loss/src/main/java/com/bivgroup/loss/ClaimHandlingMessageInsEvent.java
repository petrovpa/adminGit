package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:22 AM unknow unknow 


import com.bivgroup.crm.Address;
import com.bivgroup.crm.KindStatus;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * ClaimHandlingMessageInsEvent Generated Aug 7, 2017 10:42:22 AM unknow unknow 
 */
@Entity
@Table(name="CH_MESSAGEINSEVENT"
)
public class ClaimHandlingMessageInsEvent  implements java.io.Serializable {


     private Long id;
     private Long contractId;
     private Date eventDate;
     private String eventNote;
     private KindStatus stateId_EN;
     private Long stateId;
     private ClaimHandlingInsEvent insEventId_EN;
     private Long insEventId;
     private Address eventAddressId_EN;
     private Long eventAddressId;
     private Long rowStatus;

    public ClaimHandlingMessageInsEvent() {
    }

	
    public ClaimHandlingMessageInsEvent(Long id) {
        this.id = id;
    }
    public ClaimHandlingMessageInsEvent(Long id, Long contractId, Date eventDate, String eventNote, KindStatus stateId_EN, Long stateId, ClaimHandlingInsEvent insEventId_EN, Long insEventId, Address eventAddressId_EN, Long eventAddressId, Long rowStatus) {
       this.id = id;
       this.contractId = contractId;
       this.eventDate = eventDate;
       this.eventNote = eventNote;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.insEventId_EN = insEventId_EN;
       this.insEventId = insEventId;
       this.eventAddressId_EN = eventAddressId_EN;
       this.eventAddressId = eventAddressId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CH_MESSAGEINSEVENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="contractId")
    public Long getContractId() {
        return this.contractId;
    }
    
    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="eventDate")
    public Date getEventDate() {
        return this.eventDate;
    }
    
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    
    @Column(name="eventNote")
    public String getEventNote() {
        return this.eventNote;
    }
    
    public void setEventNote(String eventNote) {
        this.eventNote = eventNote;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="stateId")
    public KindStatus getStateId_EN() {
        return this.stateId_EN;
    }
    
    public void setStateId_EN(KindStatus stateId_EN) {
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
    @JoinColumn(name="insEventId")
    public ClaimHandlingInsEvent getInsEventId_EN() {
        return this.insEventId_EN;
    }
    
    public void setInsEventId_EN(ClaimHandlingInsEvent insEventId_EN) {
        this.insEventId_EN = insEventId_EN;
    }

    
    @Column(name="insEventId", insertable=false, updatable=false)
    public Long getInsEventId() {
        return this.insEventId;
    }
    
    public void setInsEventId(Long insEventId) {
        this.insEventId = insEventId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="eventAddressId")
    public Address getEventAddressId_EN() {
        return this.eventAddressId_EN;
    }
    
    public void setEventAddressId_EN(Address eventAddressId_EN) {
        this.eventAddressId_EN = eventAddressId_EN;
    }

    
    @Column(name="eventAddressId", insertable=false, updatable=false)
    public Long getEventAddressId() {
        return this.eventAddressId;
    }
    
    public void setEventAddressId(Long eventAddressId) {
        this.eventAddressId = eventAddressId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


