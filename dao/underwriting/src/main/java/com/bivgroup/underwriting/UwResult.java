package com.bivgroup.underwriting;
// Generated Oct 19, 2017 6:35:50 PM unknow unknow 


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * UwResult Generated Oct 19, 2017 6:35:50 PM unknow unknow
 */
@Entity
@Table(name = "UW_UWRESULT")
public class UwResult implements java.io.Serializable {

    private Long id;
    private String result;
    private FilledQuestionnaire filledQuestionnaireId_EN;
    private Long filledQuestionnaireId;
    private Long rowStatus;
    Set<UwRiskDetail> details = new HashSet<>();

    public UwResult() {
    }

    public UwResult(Long id) {
        this.id = id;
    }

    public UwResult(Long id, String result, FilledQuestionnaire filledQuestionnaireId_EN, Long filledQuestionnaireId, Long rowStatus) {
        this.id = id;
        this.result = result;
        this.filledQuestionnaireId_EN = filledQuestionnaireId_EN;
        this.filledQuestionnaireId = filledQuestionnaireId;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "UW_UWRESULT_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id


    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "RESULT")
    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FILLEDQUESTIONNAIREID")
    public FilledQuestionnaire getFilledQuestionnaireId_EN() {
        return this.filledQuestionnaireId_EN;
    }

    public void setFilledQuestionnaireId_EN(FilledQuestionnaire filledQuestionnaireId_EN) {
        this.filledQuestionnaireId_EN = filledQuestionnaireId_EN;
    }


    @Column(name = "FILLEDQUESTIONNAIREID", insertable = false, updatable = false)
    public Long getFilledQuestionnaireId() {
        return this.filledQuestionnaireId;
    }

    public void setFilledQuestionnaireId(Long filledQuestionnaireId) {
        this.filledQuestionnaireId = filledQuestionnaireId;
    }

    @Transient

    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }

    @OneToMany(mappedBy = "uwResultId_EN", fetch = FetchType.EAGER)
    public Set<UwRiskDetail> getDetails() {
        return details;
    }

    public void setDetails(Set<UwRiskDetail> details) {
        this.details = details;
    }
}


