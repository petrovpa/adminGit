package com.bivgroup.crm;
// Generated 21.03.2018 22:24:06 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT


import com.bivgroup.core.aspect.impl.state.AspectStateSM;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * ClientProfileNotification Generated 21.03.2018 22:24:06 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT
 */

@AspectStateSM(attributeName = "stateId_EN",
        startStateName = "CDM_CLIENTPROFILENOTIFICATION_NEW",
        typeSysName = "CDM_CLIENTPROFILENOTIFICATION")
@DiscriminatorValue(value = "ClientProfileNotification")
@DiscriminatorColumn(name = "DISCRIMINATOR")
@Entity
@Table(name="CDM_CLIENT_NOTIFICATION"
)
public class ClientProfileNotification implements java.io.Serializable {


    private Long id;
    private String customParam;
    private String description;
    private Long clientProfileId;
    private Long objectId;
    private String text;
    private SMState stateId_EN;
    private Long stateId;
    private Long rowStatus;

    public ClientProfileNotification() {
    }


    public ClientProfileNotification(Long id) {
        this.id = id;
    }
    public ClientProfileNotification(Long id, String customParam, String description, Long clientProfileId, Long objectId, String text, SMState stateId_EN, Long stateId, Long rowStatus) {
        this.id = id;
        this.customParam = customParam;
        this.description = description;
        this.clientProfileId = clientProfileId;
        this.objectId = objectId;
        this.text = text;
        this.stateId_EN = stateId_EN;
        this.stateId = stateId;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "CDM_CLIENT_NOTIFICATION_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id


    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name="customParam")
    public String getCustomParam() {
        return this.customParam;
    }

    public void setCustomParam(String customParam) {
        this.customParam = customParam;
    }


    @Column(name="description")
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Column(name = "clientProfileId")
    public Long getClientProfileId() {
        return this.clientProfileId;
    }

    public void setClientProfileId(Long clientProfileId) {
        this.clientProfileId = clientProfileId;
    }


    @Column(name = "objectId")
    public Long getObjectId() {
        return this.objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }


    @Column(name = "text")
    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stateId")
    public SMState getStateId_EN() {
        return this.stateId_EN;
    }

    public void setStateId_EN(SMState stateId_EN) {
        this.stateId_EN = stateId_EN;
    }
    

    @Column(name = "stateId", insertable = false, updatable = false)
    public Long getStateId() {
        return this.stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    @Transient

    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


