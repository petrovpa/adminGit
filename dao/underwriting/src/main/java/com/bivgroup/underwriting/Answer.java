package com.bivgroup.underwriting;
// Generated Oct 19, 2017 6:35:50 PM unknow unknow 

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * Answer Generated Oct 19, 2017 6:35:50 PM unknow unknow
 */
@Entity
@Table(name = "UW_ANSWER")
public class Answer implements java.io.Serializable {

    private Long id;
    private Set<AnswerComponent> components = new HashSet<>();

    //private Question questionId_EN;
    private Long questionId;

    private FilledQuestionnaire filledQuestionnaireId_EN;
    private Long filledQuestionnaireId;

    private Long rowStatus;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "answerId_EN", cascade = CascadeType.ALL)
    public Set<AnswerComponent> getComponents() {
        return components;
    }

    public void setComponents(Set<AnswerComponent> components) {
        this.components = components;
    }

    public void addComponent(AnswerComponent component) {
        component.setAnswerId_EN(this);
        this.components.add(component);
    }

    public Answer() {
    }

    public Answer(Long id) {
        this.id = id;
    }

    @SequenceGenerator(name = "generator", sequenceName = "UW_ANSWER_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id
    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    /*@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTIONID")
    public Question getQuestionId_EN() {
        return this.questionId_EN;
    }

    public void setQuestionId_EN(Question questionId_EN) {
        this.questionId_EN = questionId_EN;
    }*/


    @Column(name = "QUESTIONID")
    public Long getQuestionId() {
        return this.questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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

}


