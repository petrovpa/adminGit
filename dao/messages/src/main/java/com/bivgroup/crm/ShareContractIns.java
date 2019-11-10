package com.bivgroup.crm;
// Generated 14.12.2017 18:39:23 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


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
 * ShareContractIns Generated 14.12.2017 18:39:23 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="SD_Share_Contract"
)
public class ShareContractIns  implements java.io.Serializable {


     private Long id;
     private Long contractId;
     private String tel;
     private Long eId;
     private Client clientId_EN;
     private Long clientId;
     private KindStatus stateId_EN;
     private Long stateId;
     private KindShareRole shareRoleId_EN;
     private Long shareRoleId;
     private Long rowStatus;

    public ShareContractIns() {
    }

	
    public ShareContractIns(Long id) {
        this.id = id;
    }
    public ShareContractIns(Long id, Long contractId, String tel, Long eId, Client clientId_EN, Long clientId, KindStatus stateId_EN, Long stateId, KindShareRole shareRoleId_EN, Long shareRoleId, Long rowStatus) {
       this.id = id;
       this.contractId = contractId;
       this.tel = tel;
       this.eId = eId;
       this.clientId_EN = clientId_EN;
       this.clientId = clientId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.shareRoleId_EN = shareRoleId_EN;
       this.shareRoleId = shareRoleId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_SHARE_CONTRACT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="ContractID")
    public Long getContractId() {
        return this.contractId;
    }
    
    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    
    @Column(name="Tel")
    public String getTel() {
        return this.tel;
    }
    
    public void setTel(String tel) {
        this.tel = tel;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ClientID")
    public Client getClientId_EN() {
        return this.clientId_EN;
    }
    
    public void setClientId_EN(Client clientId_EN) {
        this.clientId_EN = clientId_EN;
    }

    
    @Column(name="ClientID", insertable=false, updatable=false)
    public Long getClientId() {
        return this.clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
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
    @JoinColumn(name="ShareRoleID")
    public KindShareRole getShareRoleId_EN() {
        return this.shareRoleId_EN;
    }
    
    public void setShareRoleId_EN(KindShareRole shareRoleId_EN) {
        this.shareRoleId_EN = shareRoleId_EN;
    }

    
    @Column(name="ShareRoleID", insertable=false, updatable=false)
    public Long getShareRoleId() {
        return this.shareRoleId;
    }
    
    public void setShareRoleId(Long shareRoleId) {
        this.shareRoleId = shareRoleId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


