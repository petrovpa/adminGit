package com.bivgroup.crm;
// Generated 16.01.2018 8:26:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * ClientContact Generated 16.01.2018 8:26:26 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT
 */
@Entity
@DiscriminatorValue(value = "ClientContact")
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "id")
@Table(name="CDM_Client_Contact"
)
public class ClientContact extends com.bivgroup.crm.Contact implements java.io.Serializable {


    private Integer isPpoPrimary;
    private Integer isPrimary;
    private Client clientId_EN;
    private Long clientId;
    private KindStatus stateId_EN;
    private Long stateId;

    public ClientContact() {
    }


    public ClientContact(Long id) {
        super(id);
    }
    public ClientContact(Long id, String value, Long eId, KindContact typeId_EN, Long typeId, Long rowStatus, Integer isPpoPrimary, Integer isPrimary, Client clientId_EN, Long clientId, KindStatus stateId_EN, Long stateId) {
        super(id, value, eId, typeId_EN, typeId, rowStatus);
        this.isPpoPrimary = isPpoPrimary;
        this.isPrimary = isPrimary;
        this.clientId_EN = clientId_EN;
        this.clientId = clientId;
        this.stateId_EN = stateId_EN;
        this.stateId = stateId;
    }



    @Column(name="isPpoPrimary")
    public Integer getIsPpoPrimary() {
        return this.isPpoPrimary;
    }

    public void setIsPpoPrimary(Integer isPpoPrimary) {
        this.isPpoPrimary = isPpoPrimary;
    }


    @Column(name="IsPrimary")
    public Integer getIsPrimary() {
        return this.isPrimary;
    }

    public void setIsPrimary(Integer isPrimary) {
        this.isPrimary = isPrimary;
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




}


