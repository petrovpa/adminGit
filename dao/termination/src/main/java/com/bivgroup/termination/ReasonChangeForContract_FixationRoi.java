package com.bivgroup.termination;
// Generated 16.03.2018 18:02:52 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 

import com.bivgroup.crm.KindStatus;

import java.util.Date;
import java.util.Set;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_FixationRoi Generated 16.03.2018 18:02:52 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_FixationRoi")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_FixationRoi extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {



    public ReasonChangeForContract_FixationRoi() {
    }

	
    public ReasonChangeForContract_FixationRoi(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_FixationRoi(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
    }
   




}


