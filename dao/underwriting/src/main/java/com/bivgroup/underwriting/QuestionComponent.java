package com.bivgroup.underwriting;

import com.bivgroup.core.annotation.IsArray;

import javax.persistence.*;

import java.io.Serializable;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "UW_QUESTIONCOMPONENT")
public class QuestionComponent implements Serializable {

    private Long id;

    private String label;

    private Integer order;

    private String type;

    private String mask;

    private Question questionId_EN;

    private Long questionId;

    private AnswerEnumType answerEnumTypeId_EN;

    private Long answerEnumTypeId;

    private Long rowStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ANSWERENUMTYPEID")
    public AnswerEnumType getAnswerEnumTypeId_EN() {
        return answerEnumTypeId_EN;
    }

    public void setAnswerEnumTypeId_EN(AnswerEnumType answerEnumTypeId_EN) {
        this.answerEnumTypeId_EN = answerEnumTypeId_EN;
    }

    @Column(name = "ANSWERENUMTYPEID", insertable = false, updatable = false)
    public Long getAnswerEnumTypeId() {
        return answerEnumTypeId;
    }

    public void setAnswerEnumTypeId(Long answerEnumTypeId) {
        this.answerEnumTypeId = answerEnumTypeId;
    }

    @ManyToOne
    @JoinColumn(name = "QUESTIONID")
    @IsArray
    public Question getQuestionId_EN() {
        return questionId_EN;
    }

    public void setQuestionId_EN(Question questionId_EN) {
        this.questionId_EN = questionId_EN;
    }

    @Column(name = "QUESTIONID", insertable = false, updatable = false)
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    @SequenceGenerator(name = "generator", sequenceName = "UW_ANSWERCOMPONENT_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id
    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "LABEL")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Column(name = "ORDERVALUE")
    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Column(name = "TYPEVALUE")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "MASK")
    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    @Transient
    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }

}
