package com.bivgroup.termination;
// Generated 16.03.2018 18:00:25 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import com.bivgroup.crm.KindStatus;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ReasonChangeForContract_FinancialVacation Generated 16.03.2018 18:00:25 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@DiscriminatorValue(value = "ReasonChangeForContract_FinancialVacation")   
@Table(name="PD_REASONCHANGE"
)
public class ReasonChangeForContract_FinancialVacation extends com.bivgroup.termination.ReasonChangeForContract implements java.io.Serializable {


     private Date endFinHolidayDate;
     private Date startFinHolidayDate;

    public ReasonChangeForContract_FinancialVacation() {
    }

	
    public ReasonChangeForContract_FinancialVacation(Long id) {
        super(id);        
    }
    public ReasonChangeForContract_FinancialVacation(Long id, String reasonComment, Date changeDate, String externalTypeId, String docFolder1C, String externalId, KindChangeReason kindChangeReasonId_EN, Long kindChangeReasonId, DeclarationOfChangeForContract declarationId_EN, Long declarationId, KindStatus stateId_EN, Long stateId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Set<ValueReasonChangeForContract> values, Long rowStatus, Date endFinHolidayDate, Date startFinHolidayDate) {
        super(id, reasonComment, changeDate, externalTypeId, docFolder1C, externalId, kindChangeReasonId_EN, kindChangeReasonId, declarationId_EN, declarationId, stateId_EN, stateId, kindDeclarationId_EN, kindDeclarationId, values, rowStatus);        
       this.endFinHolidayDate = endFinHolidayDate;
       this.startFinHolidayDate = startFinHolidayDate;
    }
   

    
    @Column(name="endFinHolidayDate")
    public Date getEndFinHolidayDate() {
        return this.endFinHolidayDate;
    }
    
    public void setEndFinHolidayDate(Date endFinHolidayDate) {
        this.endFinHolidayDate = endFinHolidayDate;
    }

    
    @Column(name="startFinHolidayDate")
    public Date getStartFinHolidayDate() {
        return this.startFinHolidayDate;
    }
    
    public void setStartFinHolidayDate(Date startFinHolidayDate) {
        this.startFinHolidayDate = startFinHolidayDate;
    }




}


