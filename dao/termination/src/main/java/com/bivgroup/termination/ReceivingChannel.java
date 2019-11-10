package com.bivgroup.termination;
// Generated 18.10.2017 11:54:02 unknow unknow 


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * ReceivingChannel Generated 18.10.2017 11:54:02 unknow unknow 
 */
@Entity
@Table(name="HD_ReceivingChannel"
)
public class ReceivingChannel  implements java.io.Serializable {


     private Long id;
     private Integer isFillDepartureDate;
     private String sysname;
     private String name;
     private Long eId;
     private Long rowStatus;

    public ReceivingChannel() {
    }

	
    public ReceivingChannel(Long id) {
        this.id = id;
    }
    public ReceivingChannel(Long id, Integer isFillDepartureDate, String sysname, String name, Long eId, Long rowStatus) {
       this.id = id;
       this.isFillDepartureDate = isFillDepartureDate;
       this.sysname = sysname;
       this.name = name;
       this.eId = eId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "HD_RECEIVINGCHANNEL_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="IsFillDepartureDateInt")
    public Integer getIsFillDepartureDate() {
        return this.isFillDepartureDate;
    }
    
    public void setIsFillDepartureDate(Integer isFillDepartureDate) {
        this.isFillDepartureDate = isFillDepartureDate;
    }

    
    @Column(name="Sysname")
    public String getSysname() {
        return this.sysname;
    }
    
    public void setSysname(String sysname) {
        this.sysname = sysname;
    }

    
    @Column(name="Name")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


