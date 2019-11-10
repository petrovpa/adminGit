package com.bivgroup.crm;
// Generated 14.12.2017 18:39:20 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * SalesCampaignOffer Generated 14.12.2017 18:39:20 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="SD_SalesCampaign_Offer"
)
public class SalesCampaignOffer  implements java.io.Serializable {


     private Long id;
     private String email;
     private String tel;
     private Long isClose;
     private Long eId;
     private SalesCampaign salesCampaignId_EN;
     private Long salesCampaignId;
     private ClientProfile clientProfileId_EN;
     private Long clientProfileId;
     private Long rowStatus;

    public SalesCampaignOffer() {
    }

	
    public SalesCampaignOffer(Long id) {
        this.id = id;
    }
    public SalesCampaignOffer(Long id, String email, String tel, Long isClose, Long eId, SalesCampaign salesCampaignId_EN, Long salesCampaignId, ClientProfile clientProfileId_EN, Long clientProfileId, Long rowStatus) {
       this.id = id;
       this.email = email;
       this.tel = tel;
       this.isClose = isClose;
       this.eId = eId;
       this.salesCampaignId_EN = salesCampaignId_EN;
       this.salesCampaignId = salesCampaignId;
       this.clientProfileId_EN = clientProfileId_EN;
       this.clientProfileId = clientProfileId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_SALESCAMPAIGN_OFFER_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="Email")
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    
    @Column(name="Tel")
    public String getTel() {
        return this.tel;
    }
    
    public void setTel(String tel) {
        this.tel = tel;
    }

    
    @Column(name="IsClose")
    public Long getIsClose() {
        return this.isClose;
    }
    
    public void setIsClose(Long isClose) {
        this.isClose = isClose;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="SalesCampaignID")
    public SalesCampaign getSalesCampaignId_EN() {
        return this.salesCampaignId_EN;
    }
    
    public void setSalesCampaignId_EN(SalesCampaign salesCampaignId_EN) {
        this.salesCampaignId_EN = salesCampaignId_EN;
    }

    
    @Column(name="SalesCampaignID", insertable=false, updatable=false)
    public Long getSalesCampaignId() {
        return this.salesCampaignId;
    }
    
    public void setSalesCampaignId(Long salesCampaignId) {
        this.salesCampaignId = salesCampaignId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ClientProfileID")
    public ClientProfile getClientProfileId_EN() {
        return this.clientProfileId_EN;
    }
    
    public void setClientProfileId_EN(ClientProfile clientProfileId_EN) {
        this.clientProfileId_EN = clientProfileId_EN;
    }

    
    @Column(name="ClientProfileID", insertable=false, updatable=false)
    public Long getClientProfileId() {
        return this.clientProfileId;
    }
    
    public void setClientProfileId(Long clientProfileId) {
        this.clientProfileId = clientProfileId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


