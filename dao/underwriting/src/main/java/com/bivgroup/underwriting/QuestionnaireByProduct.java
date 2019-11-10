package com.bivgroup.underwriting;
// Generated Oct 19, 2017 6:35:50 PM unknow unknow 


import javax.persistence.*;
import java.util.Date;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * QuestionnaireByProduct Generated Oct 19, 2017 6:35:50 PM unknow unknow
 */
@Entity
@Table(name = "UW_QUESTIONNAIREBYPRODUCTS")
public class QuestionnaireByProduct implements java.io.Serializable {

    private Long id;

    private Date startDate;

    private Date endDate;

    private Long prodConfId;

    private Long calcVerId;

    private Long rowStatus;

    /*private QuestionnaireVersion questionnaireVersionId_EN;*/
    private Long questionnaireVersionId;

    @Column(name = "STARTDATE")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "ENDDATE")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "PRODCONFID")
    public Long getProdConfId() {
        return prodConfId;
    }

    public void setProdConfId(Long prodConfId) {
        this.prodConfId = prodConfId;
    }

    @Column(name = "CALCVERID")
    public Long getCalcVerId() {
        return calcVerId;
    }

    public void setCalcVerId(Long calcVerId) {
        this.calcVerId = calcVerId;
    }

    /*@ManyToOne
    @JoinColumn(name = "QUESTIONNAIREVERSIONID")
    public QuestionnaireVersion getQuestionnaireVersionId_EN() {
        return questionnaireVersionId_EN;
    }

    public void setQuestionnaireVersionId_EN(QuestionnaireVersion questionnaireVersionId_EN) {
        this.questionnaireVersionId_EN = questionnaireVersionId_EN;
    }*/

    @Column(name = "QUESTIONNAIREVERSIONID", insertable = false, updatable = false)
    public Long getQuestionnaireVersionId() {
        return questionnaireVersionId;
    }

    public void setQuestionnaireVersionId(Long questionnaireVersionId) {
        this.questionnaireVersionId = questionnaireVersionId;
    }

    public QuestionnaireByProduct() {
    }

    public QuestionnaireByProduct(Long id) {
        this.id = id;
    }

    public QuestionnaireByProduct(Long id, Long rowStatus) {
        this.id = id;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "UW_QUESTIONNAIREBYPRODUCTS_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id
    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }


}


