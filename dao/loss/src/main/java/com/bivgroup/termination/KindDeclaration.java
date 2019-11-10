package com.bivgroup.termination;
// Generated 18.10.2017 15:05:21 unknow unknow 


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
import javax.persistence.Transient;

/**
 * KindDeclaration Generated 18.10.2017 15:05:21 unknow unknow 
 */
@Entity
@Table(name="HB_KINDDECLARATION"
)
public class KindDeclaration  implements java.io.Serializable {


     private Long id;
     private String sysname;
     private String name;
     private String cssClass;
     private String locationPath;
     private String infoNoteTemplate;
     private String externalId;
     private KindDeclarationDiscriminator discriminatorId_EN;
     private Long discriminatorId;
     private Set<KindChangeReason> kindReasons = new HashSet<KindChangeReason>(0);
     private Long rowStatus;

    public KindDeclaration() {
    }

	
    public KindDeclaration(Long id) {
        this.id = id;
    }
    public KindDeclaration(Long id, String sysname, String name, String cssClass, String locationPath, String infoNoteTemplate, String externalId, KindDeclarationDiscriminator discriminatorId_EN, Long discriminatorId, Set<KindChangeReason> kindReasons, Long rowStatus) {
       this.id = id;
       this.sysname = sysname;
       this.name = name;
       this.cssClass = cssClass;
       this.locationPath = locationPath;
       this.infoNoteTemplate = infoNoteTemplate;
       this.externalId = externalId;
       this.discriminatorId_EN = discriminatorId_EN;
       this.discriminatorId = discriminatorId;
       this.kindReasons = kindReasons;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_KINDDECLARATION_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="sysname")
    public String getSysname() {
        return this.sysname;
    }
    
    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    
    @Column(name="name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="cssClass")
    public String getCssClass() {
        return this.cssClass;
    }
    
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    
    @Column(name="locationPath")
    public String getLocationPath() {
        return this.locationPath;
    }
    
    public void setLocationPath(String locationPath) {
        this.locationPath = locationPath;
    }

    
    @Column(name="infoNoteTemplate")
    public String getInfoNoteTemplate() {
        return this.infoNoteTemplate;
    }
    
    public void setInfoNoteTemplate(String infoNoteTemplate) {
        this.infoNoteTemplate = infoNoteTemplate;
    }

    
    @Column(name="externalId")
    public String getExternalId() {
        return this.externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="discriminatorId")
    public KindDeclarationDiscriminator getDiscriminatorId_EN() {
        return this.discriminatorId_EN;
    }
    
    public void setDiscriminatorId_EN(KindDeclarationDiscriminator discriminatorId_EN) {
        this.discriminatorId_EN = discriminatorId_EN;
    }

    
    @Column(name="discriminatorId", insertable=false, updatable=false)
    public Long getDiscriminatorId() {
        return this.discriminatorId;
    }
    
    public void setDiscriminatorId(Long discriminatorId) {
        this.discriminatorId = discriminatorId;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="kindDeclarationId_EN")
    public Set<KindChangeReason> getKindReasons() {
        return this.kindReasons;
    }
    
    public void setKindReasons(Set<KindChangeReason> kindReasons) {
        this.kindReasons = kindReasons;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


