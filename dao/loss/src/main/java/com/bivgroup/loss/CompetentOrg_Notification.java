package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:23 AM unknow unknow 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * CompetentOrg_Notification Generated Aug 7, 2017 10:42:23 AM unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "CompetentOrg_Notification") 
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id") 
@Table(name="CH_COMPETENTORG_1"
)
public class CompetentOrg_Notification extends com.bivgroup.loss.CompetentOrg implements java.io.Serializable {


     private ClaimHandlingNotification notificationId_EN;
     private Long notificationId;

    public CompetentOrg_Notification() {
    }

	
    public CompetentOrg_Notification(Long id) {
        super(id);        
    }
    public CompetentOrg_Notification(Long id, Date incidentDate, KindCompetentOrg kindCompetentOrgID_EN, Long kindCompetentOrgID, Long rowStatus, ClaimHandlingNotification notificationId_EN, Long notificationId) {
        super(id, incidentDate, kindCompetentOrgID_EN, kindCompetentOrgID, rowStatus);        
       this.notificationId_EN = notificationId_EN;
       this.notificationId = notificationId;
    }
   

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="notificationId")
    public ClaimHandlingNotification getNotificationId_EN() {
        return this.notificationId_EN;
    }
    
    public void setNotificationId_EN(ClaimHandlingNotification notificationId_EN) {
        this.notificationId_EN = notificationId_EN;
    }

    
    @Column(name="notificationId", insertable=false, updatable=false)
    public Long getNotificationId() {
        return this.notificationId;
    }
    
    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }




}


