package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:22 AM unknow unknow 


import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * CompetentOrg Generated Aug 7, 2017 10:42:22 AM unknow unknow 
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)       
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="CH_COMPETENTORG"
)
public class CompetentOrg  implements java.io.Serializable {


     private Long id;
     private Date incidentDate;
     private KindCompetentOrg kindCompetentOrgID_EN;
     private Long kindCompetentOrgID;
     private Long rowStatus;

    public CompetentOrg() {
    }

	
    public CompetentOrg(Long id) {
        this.id = id;
    }
    public CompetentOrg(Long id, Date incidentDate, KindCompetentOrg kindCompetentOrgID_EN, Long kindCompetentOrgID, Long rowStatus) {
       this.id = id;
       this.incidentDate = incidentDate;
       this.kindCompetentOrgID_EN = kindCompetentOrgID_EN;
       this.kindCompetentOrgID = kindCompetentOrgID;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CH_COMPETENTORG_SEQ", allocationSize = 10)
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
    @Column(name="incidentDate")
    public Date getIncidentDate() {
        return this.incidentDate;
    }
    
    public void setIncidentDate(Date incidentDate) {
        this.incidentDate = incidentDate;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="kindCompetentOrgID")
    public KindCompetentOrg getKindCompetentOrgID_EN() {
        return this.kindCompetentOrgID_EN;
    }
    
    public void setKindCompetentOrgID_EN(KindCompetentOrg kindCompetentOrgID_EN) {
        this.kindCompetentOrgID_EN = kindCompetentOrgID_EN;
    }

    
    @Column(name="kindCompetentOrgID", insertable=false, updatable=false)
    public Long getKindCompetentOrgID() {
        return this.kindCompetentOrgID;
    }
    
    public void setKindCompetentOrgID(Long kindCompetentOrgID) {
        this.kindCompetentOrgID = kindCompetentOrgID;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


