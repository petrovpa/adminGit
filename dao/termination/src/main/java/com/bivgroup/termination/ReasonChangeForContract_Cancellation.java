package com.bivgroup.termination;
// Generated 18.01.2018 15:51:07 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.BankDetails;
import com.bivgroup.crm.KindStatus;
import com.bivgroup.crm.PPerson;

import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_Cancellation Generated 18.01.2018 15:51:07 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_Cancellation")
@Table(name = "PD_REASONCHANGE"
)
public class ReasonChangeForContract_Cancellation extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


    private String cancellationDescription;
     private String cancellationSysname;
    private PPerson insurantId_EN;
    private Long insurantId;
    private BankDetails bankDetailsId_EN;
    private Long bankDetailsId;

    public ReasonChangeForContract_Cancellation() {
    }

    public ReasonChangeForContract_Cancellation(Long id) {
        super(id);
    }
    public ReasonChangeForContract_Cancellation(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, String cancellationDescription, String cancellationSysname, PPerson insurantId_EN, Long insurantId, BankDetails bankDetailsId_EN, Long bankDetailsId) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.cancellationDescription = cancellationDescription;
       this.cancellationSysname = cancellationSysname;
        this.insurantId_EN = insurantId_EN;
        this.insurantId = insurantId;
        this.bankDetailsId_EN = bankDetailsId_EN;
        this.bankDetailsId = bankDetailsId;
    }


    @Column(name = "note")
    public String getCancellationDescription() {
        return this.cancellationDescription;
    }

    public void setCancellationDescription(String cancellationDescription) {
        this.cancellationDescription = cancellationDescription;
    }


    @Column(name = "cause")
    public String getCancellationSysname() {
        return this.cancellationSysname;
    }

    public void setCancellationSysname(String cancellationSysname) {
        this.cancellationSysname = cancellationSysname;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="personId")
    public PPerson getInsurantId_EN() {
        return this.insurantId_EN;
    }

    public void setInsurantId_EN(PPerson insurantId_EN) {
        this.insurantId_EN = insurantId_EN;
    }


    @Column(name="personId", insertable=false, updatable=false)
    public Long getInsurantId() {
        return this.insurantId;
    }

    public void setInsurantId(Long insurantId) {
        this.insurantId = insurantId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bankDetailsId")
    public BankDetails getBankDetailsId_EN() {
        return this.bankDetailsId_EN;
    }

    public void setBankDetailsId_EN(BankDetails bankDetailsId_EN) {
        this.bankDetailsId_EN = bankDetailsId_EN;
    }


    @Column(name = "bankDetailsId", insertable = false, updatable = false)
    public Long getBankDetailsId() {
        return this.bankDetailsId;
    }

    public void setBankDetailsId(Long bankDetailsId) {
        this.bankDetailsId = bankDetailsId;
    }


}


