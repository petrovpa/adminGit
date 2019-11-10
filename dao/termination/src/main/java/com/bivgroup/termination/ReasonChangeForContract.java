package com.bivgroup.termination;
// Generated 16.03.2018 18:02:35 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
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
import javax.persistence.Transient;

/**
 * ReasonChangeForContract Generated 16.03.2018 18:02:35 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract  implements java.io.Serializable {


     private Long id;
     private String reasonComment;
     private Date changeDate;
     private String externalTypeId;
     private String docFolder1C;
     private String externalId;
     private KindChangeReason kindChangeReasonId_EN;
     private Long kindChangeReasonId;
     private DeclarationOfChangeForContract declarationId_EN;
     private Long declarationId;
     private KindStatus stateId_EN;
     private Long stateId;
     private KindDeclaration kindDeclarationId_EN;
     private Long kindDeclarationId;
     private Set<ValueReasonChangeForContract> values = new HashSet<ValueReasonChangeForContract>(0);
     private Long rowStatus;

    public ReasonChangeForContract() {
    }

	
    public ReasonChangeForContract(Long id) {
        this.id = id;
    }
    public ReasonChangeForContract(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus) {
       this.id = id;
       this.reasonComment = reasonComment;
       this.changeDate = changeDate;
       this.externalTypeId = externalTypeId;
       this.docFolder1C = docFolder1C;
       this.externalId = externalId;
       this.kindChangeReasonId_EN = kindChangeReasonId_EN;
       this.kindChangeReasonId = kindChangeReasonId;
       this.declarationId_EN = declarationId_EN;
       this.declarationId = declarationId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.kindDeclarationId_EN = kindDeclarationId_EN;
       this.kindDeclarationId = kindDeclarationId;
       this.values = values;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "PD_REASONCHANGE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="reasonComment")
    public String getReasonComment() {
        return this.reasonComment;
    }
    
    public void setReasonComment(String reasonComment) {
        this.reasonComment = reasonComment;
    }

    
    @Column(name="changeDate")
    public Date getChangeDate() {
        return this.changeDate;
    }
    
    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    
    @Column(name="externalTypeId")
    public String getExternalTypeId() {
        return this.externalTypeId;
    }
    
    public void setExternalTypeId(String externalTypeId) {
        this.externalTypeId = externalTypeId;
    }

    
    @Column(name="docFolder1C")
    public String getDocFolder1C() {
        return this.docFolder1C;
    }
    
    public void setDocFolder1C(String docFolder1C) {
        this.docFolder1C = docFolder1C;
    }

    
    @Column(name="externalId")
    public String getExternalId() {
        return this.externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="kindChangeReasonId")
    public KindChangeReason getKindChangeReasonId_EN() {
        return this.kindChangeReasonId_EN;
    }
    
    public void setKindChangeReasonId_EN(KindChangeReason kindChangeReasonId_EN) {
        this.kindChangeReasonId_EN = kindChangeReasonId_EN;
    }

    
    @Column(name="kindChangeReasonId", insertable=false, updatable=false)
    public Long getKindChangeReasonId() {
        return this.kindChangeReasonId;
    }
    
    public void setKindChangeReasonId(Long kindChangeReasonId) {
        this.kindChangeReasonId = kindChangeReasonId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="declarationId")
    public DeclarationOfChangeForContract getDeclarationId_EN() {
        return this.declarationId_EN;
    }
    
    public void setDeclarationId_EN(DeclarationOfChangeForContract declarationId_EN) {
        this.declarationId_EN = declarationId_EN;
    }

    
    @Column(name="declarationId", insertable=false, updatable=false)
    public Long getDeclarationId() {
        return this.declarationId;
    }
    
    public void setDeclarationId(Long declarationId) {
        this.declarationId = declarationId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="StateID")
    public KindStatus getStateId_EN() {
        return this.stateId_EN;
    }
    
    public void setStateId_EN(KindStatus stateId_EN) {
        this.stateId_EN = stateId_EN;
    }

    
    @Column(name="StateID", insertable=false, updatable=false)
    public Long getStateId() {
        return this.stateId;
    }
    
    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="kindDeclarationId")
    public KindDeclaration getKindDeclarationId_EN() {
        return this.kindDeclarationId_EN;
    }
    
    public void setKindDeclarationId_EN(KindDeclaration kindDeclarationId_EN) {
        this.kindDeclarationId_EN = kindDeclarationId_EN;
    }

    
    @Column(name="kindDeclarationId", insertable=false, updatable=false)
    public Long getKindDeclarationId() {
        return this.kindDeclarationId;
    }
    
    public void setKindDeclarationId(Long kindDeclarationId) {
        this.kindDeclarationId = kindDeclarationId;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="reasonId_EN")
    public Set<ValueReasonChangeForContract> getValues() {
        return this.values;
    }
    
    public void setValues(Set<ValueReasonChangeForContract> values) {
        this.values = values;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


