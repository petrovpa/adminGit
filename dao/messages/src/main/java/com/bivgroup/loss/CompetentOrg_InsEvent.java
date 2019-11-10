package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:22 AM unknow unknow 


import javax.persistence.*;
import java.util.Date;

/**
 * CompetentOrg_InsEvent Generated Aug 7, 2017 10:42:22 AM unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "CompetentOrg_InsEvent") 
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id") 
@Table(name="CH_COMPETENTORG_2"
)
public class CompetentOrg_InsEvent extends CompetentOrg implements java.io.Serializable {


     private ClaimHandlingInsEvent insEventId_EN;
     private Long insEventId;

    public CompetentOrg_InsEvent() {
    }

	
    public CompetentOrg_InsEvent(Long id) {
        super(id);        
    }
    public CompetentOrg_InsEvent(Long id, Date incidentDate, KindCompetentOrg kindCompetentOrgID_EN, Long kindCompetentOrgID, Long rowStatus, ClaimHandlingInsEvent insEventId_EN, Long insEventId) {
        super(id, incidentDate, kindCompetentOrgID_EN, kindCompetentOrgID, rowStatus);        
       this.insEventId_EN = insEventId_EN;
       this.insEventId = insEventId;
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




}


