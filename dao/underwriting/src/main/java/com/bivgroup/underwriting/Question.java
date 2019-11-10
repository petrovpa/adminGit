package com.bivgroup.underwriting;
// Generated Oct 19, 2017 6:35:49 PM unknow unknow 

import com.bivgroup.core.annotation.IsArray;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * Question Generated Oct 19, 2017 6:35:49 PM unknow unknow
 */
@Entity
@Table(name = "UW_QUESTION")
public class Question implements java.io.Serializable {

    private Long id;
    private String questionText;

    private String activationFormula;
    private Long activationEnumValueId;
    private AnswerEnumValue activationEnumValueId_EN;

    private String activationType;
    private Integer order = Integer.MAX_VALUE;

    private String num;

    private QuestionnaireVersion questionnaireVersionId_EN;
    private Long questionnaireVersionId;
    private Question parentId_EN;
    private Long parentId;

    /*private Set<Answer> answers = new HashSet<>();*/

    private Set<Question> subQuestions = new HashSet<Question>(0);
    private Long rowStatus;

    private Set<QuestionComponent> components = new HashSet<>();

    private String type;

    public void addComponent(QuestionComponent component) {
        component.setQuestionId_EN(this);
        this.components.add(component);
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ACTIVATIONENUMVALUE")
    public AnswerEnumValue getActivationEnumValueId_EN() {
        return activationEnumValueId_EN;
    }

    public void setActivationEnumValueId_EN(AnswerEnumValue activationEnumValueId_EN) {
        this.activationEnumValueId_EN = activationEnumValueId_EN;
    }

    public Question() {
    }

    @Column(name = "TYPEVALUE")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /*@OneToMany(mappedBy = "questionId_EN")
    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }*/

    @OneToMany(mappedBy = "questionId_EN", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<QuestionComponent> getComponents() {
        return components;
    }

    public void setComponents(Set<QuestionComponent> components) {
        this.components = components;
    }

    public Question(Long id) {
        this.id = id;
    }

    public void addSubQuestion(Question subQuestion) {
        subQuestion.setParentId_EN(this);
        this.subQuestions.add(subQuestion);
    }

    @SequenceGenerator(name = "generator", sequenceName = "UW_QUESTION_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id
    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "QUESTIONTEXT")
    public String getQuestionText() {
        return this.questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    @Column(name = "ACTIVATIONFORMULA")
    public String getActivationFormula() {
        return this.activationFormula;
    }

    public void setActivationFormula(String activationFormula) {
        this.activationFormula = activationFormula;
    }


    @Column(name = "ACTIVATIONENUMVALUE", insertable = false, updatable = false)
    public Long getActivationEnumValueId() {
        return this.activationEnumValueId;
    }

    public void setActivationEnumValueId(Long activationEnumValueId) {
        this.activationEnumValueId = activationEnumValueId;
    }

    @Column(name = "ACTIVATIONTYPE")
    public String getActivationType() {
        return this.activationType;
    }

    public void setActivationType(String activationType) {
        this.activationType = activationType;
    }


    @Column(name = "ORDERVALUE")
    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Column(name = "NUM")
    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTIONNAIREVERSIONID")
    public QuestionnaireVersion getQuestionnaireVersionId_EN() {
        return this.questionnaireVersionId_EN;
    }

    public void setQuestionnaireVersionId_EN(QuestionnaireVersion questionnaireVersionId_EN) {
        this.questionnaireVersionId_EN = questionnaireVersionId_EN;
    }

    @Column(name = "QUESTIONNAIREVERSIONID", insertable = false, updatable = false)
    public Long getQuestionnaireVersionId() {
        return this.questionnaireVersionId;
    }

    public void setQuestionnaireVersionId(Long questionnaireVersionId) {
        this.questionnaireVersionId = questionnaireVersionId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENTID")
    @IsArray
    public Question getParentId_EN() {
        return this.parentId_EN;
    }

    public void setParentId_EN(Question parentId_EN) {
        this.parentId_EN = parentId_EN;
    }

    @Column(name = "PARENTID", insertable = false, updatable = false)
    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "parentId_EN")
    public Set<Question> getSubQuestions() {
        return this.subQuestions;
    }

    public void setSubQuestions(Set<Question> subQuestions) {
        this.subQuestions = subQuestions;
    }

    @Transient
    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }

}


