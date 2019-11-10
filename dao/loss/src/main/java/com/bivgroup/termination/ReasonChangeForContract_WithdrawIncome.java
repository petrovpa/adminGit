package com.bivgroup.termination;
// Generated Oct 10, 2017 3:26:07 PM unknow unknow 


import com.bivgroup.crm.KindStatus;
import com.bivgroup.crm.BankDetails;
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
 * ReasonChangeForContract_WithdrawIncome Generated Oct 10, 2017 3:26:07 PM unknow unknow 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_WithdrawIncome")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_WithdrawIncome extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private BankDetails bankDetailsId_EN;
     private Long bankDetailsId;

    public ReasonChangeForContract_WithdrawIncome() {
    }

	
    public ReasonChangeForContract_WithdrawIncome(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_WithdrawIncome(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, BankDetails bankDetailsId_EN, Long bankDetailsId) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);
        this.bankDetailsId_EN = bankDetailsId_EN;
        this.bankDetailsId = bankDetailsId;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="bankDetailsId")
    public BankDetails getBankDetailsId_EN() {
        return this.bankDetailsId_EN;
    }
    
    public void setBankDetailsId_EN(BankDetails bankDetailsId_EN) {
        this.bankDetailsId_EN = bankDetailsId_EN;
    }

    
    @Column(name="bankDetailsId", insertable=false, updatable=false)
    public Long getBankDetailsId() {
        return this.bankDetailsId;
    }
    
    public void setBankDetailsId(Long bankDetailsId) {
        this.bankDetailsId = bankDetailsId;
    }




}


