package com.bivgroup.crm;
// Generated 14.12.2017 18:39:24 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * ClientProperty Generated 14.12.2017 18:39:24 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="CDM_Client_Property"
)
public class ClientProperty  implements java.io.Serializable {


     private Long id;
     private Date valueDate;
     private Integer valueBoolean;
     private Float valueFloat;
     private Long valueInt;
     private String valueStr;
     private Long eId;
     private Client clientId_EN;
     private Long clientId;
     private KindStatus stateId_EN;
     private Long stateId;
     private KindClientProperty propertyTypeId_EN;
     private Long propertyTypeId;
     private Long rowStatus;

    public ClientProperty() {
    }

	
    public ClientProperty(Long id) {
        this.id = id;
    }
    public ClientProperty(Long id, Date valueDate, Integer valueBoolean, Float valueFloat, Long valueInt, String valueStr, Long eId, Client clientId_EN, Long clientId, KindStatus stateId_EN, Long stateId, KindClientProperty propertyTypeId_EN, Long propertyTypeId, Long rowStatus) {
       this.id = id;
       this.valueDate = valueDate;
       this.valueBoolean = valueBoolean;
       this.valueFloat = valueFloat;
       this.valueInt = valueInt;
       this.valueStr = valueStr;
       this.eId = eId;
       this.clientId_EN = clientId_EN;
       this.clientId = clientId;
       this.stateId_EN = stateId_EN;
       this.stateId = stateId;
       this.propertyTypeId_EN = propertyTypeId_EN;
       this.propertyTypeId = propertyTypeId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "CDM_CLIENT_PROPERTY_SEQ", allocationSize = 10)
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
    @Column(name="ValueDate")
    public Date getValueDate() {
        return this.valueDate;
    }
    
    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    
    @Column(name="ValueBoolean")
    public Integer getValueBoolean() {
        return this.valueBoolean;
    }
    
    public void setValueBoolean(Integer valueBoolean) {
        this.valueBoolean = valueBoolean;
    }

    
    @Column(name="ValueFloat")
    public Float getValueFloat() {
        return this.valueFloat;
    }
    
    public void setValueFloat(Float valueFloat) {
        this.valueFloat = valueFloat;
    }

    
    @Column(name="ValueInt")
    public Long getValueInt() {
        return this.valueInt;
    }
    
    public void setValueInt(Long valueInt) {
        this.valueInt = valueInt;
    }

    
    @Column(name="ValueStr")
    public String getValueStr() {
        return this.valueStr;
    }
    
    public void setValueStr(String valueStr) {
        this.valueStr = valueStr;
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
    @JoinColumn(name="PropertyTypeID")
    public KindClientProperty getPropertyTypeId_EN() {
        return this.propertyTypeId_EN;
    }
    
    public void setPropertyTypeId_EN(KindClientProperty propertyTypeId_EN) {
        this.propertyTypeId_EN = propertyTypeId_EN;
    }

    
    @Column(name="PropertyTypeID", insertable=false, updatable=false)
    public Long getPropertyTypeId() {
        return this.propertyTypeId;
    }
    
    public void setPropertyTypeId(Long propertyTypeId) {
        this.propertyTypeId = propertyTypeId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


