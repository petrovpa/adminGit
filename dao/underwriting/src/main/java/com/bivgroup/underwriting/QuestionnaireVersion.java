package com.bivgroup.underwriting;
// Generated Oct 19, 2017 6:35:49 PM unknow unknow 


import com.bivgroup.core.annotation.IsArray;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * QuestionnaireVersion Generated Oct 19, 2017 6:35:49 PM unknow unknow
 */
@Entity
@Table(name = "UW_QUESTIONNAIREVERSION")
public class QuestionnaireVersion implements java.io.Serializable {

    private Long id;
    private Boolean isEnabled;
    private String version;
    private Date endDate;
    private Date startDate;
    private Questionnaire questionnaireId_EN;
    private Long questionnaireId;
    private Long rowStatus;
    private Set<Question> questions = new HashSet<>();

    /*private Set<QuestionnaireByProduct> questionnaireByProductLinks = new HashSet<>();

    @OneToMany(mappedBy = "questionnaireVersionId_EN", fetch = FetchType.EAGER)
    public Set<QuestionnaireByProduct> getQuestionnaireByProductLinks() {
        return questionnaireByProductLinks;
    }

    public void setQuestionnaireByProductLinks(Set<QuestionnaireByProduct> questionnaireByProductLinks) {
        this.questionnaireByProductLinks = questionnaireByProductLinks;
    }*/

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "questionnaireVersionId_EN", fetch = FetchType.EAGER)
    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public QuestionnaireVersion() {
    }

    public void addQuestion(Question question) {
        question.setQuestionnaireVersionId_EN(this);
        this.questions.add(question);
    }

    public QuestionnaireVersion(Long id) {
        this.id = id;
    }

    @SequenceGenerator(name = "generator", sequenceName = "UW_QUESTIONNAIREVERSION_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id
    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "ISENABLED")
    public Boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }


    @Column(name = "VERSION")
    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ENDDATE")
    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "STARTDATE")
    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTIONNAIREID")
    @IsArray
    public Questionnaire getQuestionnaireId_EN() {
        return this.questionnaireId_EN;
    }

    public void setQuestionnaireId_EN(Questionnaire questionnaireId_EN) {
        this.questionnaireId_EN = questionnaireId_EN;
    }


    @Column(name = "QUESTIONNAIREID", insertable = false, updatable = false)
    public Long getQuestionnaireId() {
        return this.questionnaireId;
    }

    public void setQuestionnaireId(Long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    @Transient

    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }


}


