package com.bivgroup.underwriting;

import javax.persistence.*;

import java.io.Serializable;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "UW_ANSWERCOMPONENT")
public class AnswerComponent implements Serializable {

    private Long id;

    private Answer answerId_EN;

    private Long answerId;

    private String stringValue;

    private Integer intValue;

    private Double doubleValue;

    private AnswerEnumValue enumValueId_EN;

    private Long enumValueId;

    private String enumMultiValueIds;

    private Long questionComponentId;

    private Long rowStatus;

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

    @ManyToOne
    @JoinColumn(name = "ANSWERID")
    public Answer getAnswerId_EN() {
        return answerId_EN;
    }

    public void setAnswerId_EN(Answer answerId_EN) {
        this.answerId_EN = answerId_EN;
    }

    @Column(name = "ANSWERID", insertable = false, updatable = false)
    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    @Column(name = "STRINGVALUE")
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Column(name = "INTVALUE")
    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    @Column(name = "DOUBLEVALUE")
    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    @ManyToOne
    @JoinColumn(name = "ENUMVALUEID")
    public AnswerEnumValue getEnumValueId_EN() {
        return enumValueId_EN;
    }

    public void setEnumValueId_EN(AnswerEnumValue enumValueId_EN) {
        this.enumValueId_EN = enumValueId_EN;
    }

    @Column(name = "ENUMVALUEID", insertable = false, updatable = false)
    public Long getEnumValueId() {
        return enumValueId;
    }

    public void setEnumValueId(Long enumValueId) {
        this.enumValueId = enumValueId;
    }

    @Column(name = "ENUMMULTIVALUEIDS")
    public String getEnumMultiValueIds() {
        return enumMultiValueIds;
    }

    public void setEnumMultiValueIds(String enumMultiValueIds) {
        this.enumMultiValueIds = enumMultiValueIds;
    }

    @Column(name = "QUESTIONCOMPONENTID", insertable = false, updatable = false)
    public Long getQuestionComponentId() {
        return questionComponentId;
    }

    public void setQuestionComponentId(Long questionComponentId) {
        this.questionComponentId = questionComponentId;
    }

    @Transient
    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }

}
