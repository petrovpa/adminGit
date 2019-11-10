package com.bivgroup.termination;
// Generated 29.09.2017 13:15:24 unknow unknow 


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
 * KindChangeReason Generated 29.09.2017 13:15:24 unknow unknow 
 */
@Entity
@Table(name="HB_KINDCHANGEREASON"
)
public class KindChangeReason  implements java.io.Serializable {


     private Long id;
     private String externalId;
     private String infoNoteTemplate;
     private Long isOption;
     private String sysname;
     private String name;
     private Long insProductId;
     private KindDeclaration kindDeclarationId_EN;
     private Long kindDeclarationId;
     private Long rowStatus;

    public KindChangeReason() {
    }

	
    public KindChangeReason(Long id) {
        this.id = id;
    }
    public KindChangeReason(Long id, String externalId, String infoNoteTemplate, Long isOption, String sysname, String name, Long insProductId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Long rowStatus) {
       this.id = id;
       this.externalId = externalId;
       this.infoNoteTemplate = infoNoteTemplate;
       this.isOption = isOption;
       this.sysname = sysname;
       this.name = name;
       this.insProductId = insProductId;
       this.kindDeclarationId_EN = kindDeclarationId_EN;
       this.kindDeclarationId = kindDeclarationId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HB_KINDCHANGEREASON_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="externalId")
    public String getExternalId() {
        return this.externalId;
    }
    
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    
    @Column(name="infoNoteTemplate")
    public String getInfoNoteTemplate() {
        return this.infoNoteTemplate;
    }
    
    public void setInfoNoteTemplate(String infoNoteTemplate) {
        this.infoNoteTemplate = infoNoteTemplate;
    }

    
    @Column(name="isOption")
    public Long getIsOption() {
        return this.isOption;
    }
    
    public void setIsOption(Long isOption) {
        this.isOption = isOption;
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

    
    @Column(name="insProductId")
    public Long getInsProductId() {
        return this.insProductId;
    }
    
    public void setInsProductId(Long insProductId) {
        this.insProductId = insProductId;
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

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


