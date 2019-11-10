package com.bivgroup.crm;
// Generated Apr 25, 2018 2:13:41 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT


import com.bivgroup.core.aspect.impl.state.AspectStateSM;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * ClientProfile Generated Apr 25, 2018 2:13:41 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT
 */
@Entity
@Table(name = "SD_ClientProfile")
@AspectStateSM(attributeName = "stateId_EN", startStateName = "SD_CLIENTPROFILE_ACTIVE", typeSysName = "SD_CLIENTPROFILE")
public class ClientProfile implements java.io.Serializable {


    private Long id;
    private String prevPpoOnlineType;
    private String ppoOnlineType;
    private Date createDate;
    private String login;
    private String tel;
    private Long eId;
    private Client clientId_EN;
    private Long clientId;
    private SMState stateId_EN;
    private Long stateId;
    private KindAccount typeId_EN;
    private Long typeId;
    private Set<ClientProfileToken> tokens = new HashSet<ClientProfileToken>(0);
    private Set<ClientProfileEvent> events = new HashSet<ClientProfileEvent>(0);
    private Long rowStatus;

    public ClientProfile() {
    }


    public ClientProfile(Long id) {
        this.id = id;
    }

    public ClientProfile(Long id, String prevPpoOnlineType, String ppoOnlineType, Date createDate, String login, String tel, Long eId, Client clientId_EN, Long clientId, SMState stateId_EN, Long stateId, KindAccount typeId_EN, Long typeId, Set<ClientProfileToken> tokens, Set<ClientProfileEvent> events, Long rowStatus) {
        this.id = id;
        this.prevPpoOnlineType = prevPpoOnlineType;
        this.ppoOnlineType = ppoOnlineType;
        this.createDate = createDate;
        this.login = login;
        this.tel = tel;
        this.eId = eId;
        this.clientId_EN = clientId_EN;
        this.clientId = clientId;
        this.stateId_EN = stateId_EN;
        this.stateId = stateId;
        this.typeId_EN = typeId_EN;
        this.typeId = typeId;
        this.tokens = tokens;
        this.events = events;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "SD_CLIENTPROFILE_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id


    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "prevPpoOnlineType")
    public String getPrevPpoOnlineType() {
        return this.prevPpoOnlineType;
    }

    public void setPrevPpoOnlineType(String prevPpoOnlineType) {
        this.prevPpoOnlineType = prevPpoOnlineType;
    }


    @Column(name = "ppoOnlineType")
    public String getPpoOnlineType() {
        return this.ppoOnlineType;
    }

    public void setPpoOnlineType(String ppoOnlineType) {
        this.ppoOnlineType = ppoOnlineType;
    }


    @Column(name = "createDate")
    public Date getCreateDate() {
        return this.createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    @Column(name = "Login")
    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    @Column(name = "Tel")
    public String getTel() {
        return this.tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }


    @Column(name = "EID")
    public Long geteId() {
        return this.eId;
    }

    public void seteId(Long eId) {
        this.eId = eId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ClientID")
    public Client getClientId_EN() {
        return this.clientId_EN;
    }

    public void setClientId_EN(Client clientId_EN) {
        this.clientId_EN = clientId_EN;
    }


    @Column(name = "ClientID", insertable = false, updatable = false)
    public Long getClientId() {
        return this.clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "StateID")
    public SMState getStateId_EN() {
        return this.stateId_EN;
    }

    public void setStateId_EN(SMState stateId_EN) {
        this.stateId_EN = stateId_EN;
    }


    @Column(name = "StateID", insertable = false, updatable = false)
    public Long getStateId() {
        return this.stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TypeID")
    public KindAccount getTypeId_EN() {
        return this.typeId_EN;
    }

    public void setTypeId_EN(KindAccount typeId_EN) {
        this.typeId_EN = typeId_EN;
    }


    @Column(name = "TypeID", insertable = false, updatable = false)
    public Long getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "clientProfileId_EN")
    public Set<ClientProfileToken> getTokens() {
        return this.tokens;
    }

    public void setTokens(Set<ClientProfileToken> tokens) {
        this.tokens = tokens;
    }

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "clientProfileId_EN")
    public Set<ClientProfileEvent> getEvents() {
        return this.events;
    }

    public void setEvents(Set<ClientProfileEvent> events) {
        this.events = events;
    }

    @Transient

    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }


}


