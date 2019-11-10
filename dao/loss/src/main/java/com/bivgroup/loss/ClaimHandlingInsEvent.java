package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:22 AM unknow unknow 


import com.bivgroup.crm.Address;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * ClaimHandlingInsEvent Generated Aug 7, 2017 10:42:22 AM unknow unknow 
 */
@Entity
@Table(name="CH_INSEVENT"
)
public class ClaimHandlingInsEvent  implements java.io.Serializable {


     private Long id;
     private Long contractId;
     private Date eventDate;
     private String eventNote;
     private Address eventAddressId_EN;
     private Long eventAddressId;
     private Set<CompetentOrg_InsEvent> competentOrganizations = new HashSet<CompetentOrg_InsEvent>(0);
     private Set<SubjectInsEvent> members = new HashSet<SubjectInsEvent>(0);
     private Long rowStatus;

    public ClaimHandlingInsEvent() {
    }

	
    public ClaimHandlingInsEvent(Long id) {
        this.id = id;
    }
    public ClaimHandlingInsEvent(Long id, Long contractId, Date eventDate, String eventNote, Address eventAddressId_EN, Long eventAddressId, Set<CompetentOrg_InsEvent> competentOrganizations, Set<SubjectInsEvent> members, Long rowStatus) {
       this.id = id;
       this.contractId = contractId;
       this.eventDate = eventDate;
       this.eventNote = eventNote;
       this.eventAddressId_EN = eventAddressId_EN;
       this.eventAddressId = eventAddressId;
       this.competentOrganizations = competentOrganizations;
       this.members = members;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CH_INSEVENT_SEQ", allocationSize = 10)
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

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="insEventId_EN")
    public Set<CompetentOrg_InsEvent> getCompetentOrganizations() {
        return this.competentOrganizations;
    }
    
    public void setCompetentOrganizations(Set<CompetentOrg_InsEvent> competentOrganizations) {
        this.competentOrganizations = competentOrganizations;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="insEventId_EN")
    public Set<SubjectInsEvent> getMembers() {
        return this.members;
    }
    
    public void setMembers(Set<SubjectInsEvent> members) {
        this.members = members;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


