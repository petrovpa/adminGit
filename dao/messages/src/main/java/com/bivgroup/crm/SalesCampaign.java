package com.bivgroup.crm;
// Generated 14.12.2017 18:39:20 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * SalesCampaign Generated 14.12.2017 18:39:20 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="SD_SalesCampaign"
)
public class SalesCampaign  implements java.io.Serializable {


     private Long id;
     private Date dateEnd;
     private Date dateBegin;
     private String note;
     private String name;
     private Long insProductId;
     private Long eId;
     private Set<SalesCampaignOffer> offers = new HashSet<SalesCampaignOffer>(0);
     private Long rowStatus;

    public SalesCampaign() {
    }

	
    public SalesCampaign(Long id) {
        this.id = id;
    }
    public SalesCampaign(Long id, Date dateEnd, Date dateBegin, String note, String name, Long insProductId, Long eId, Set<SalesCampaignOffer> offers, Long rowStatus) {
       this.id = id;
       this.dateEnd = dateEnd;
       this.dateBegin = dateBegin;
       this.note = note;
       this.name = name;
       this.insProductId = insProductId;
       this.eId = eId;
       this.offers = offers;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_SALESCAMPAIGN_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DateEnd")
    public Date getDateEnd() {
        return this.dateEnd;
    }
    
    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="DateBegin")
    public Date getDateBegin() {
        return this.dateBegin;
    }
    
    public void setDateBegin(Date dateBegin) {
        this.dateBegin = dateBegin;
    }

    
    @Column(name="Note")
    public String getNote() {
        return this.note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }

    
    @Column(name="Name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="InsProductID")
    public Long getInsProductId() {
        return this.insProductId;
    }
    
    public void setInsProductId(Long insProductId) {
        this.insProductId = insProductId;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.LAZY, mappedBy="salesCampaignId_EN")
    public Set<SalesCampaignOffer> getOffers() {
        return this.offers;
    }
    
    public void setOffers(Set<SalesCampaignOffer> offers) {
        this.offers = offers;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


