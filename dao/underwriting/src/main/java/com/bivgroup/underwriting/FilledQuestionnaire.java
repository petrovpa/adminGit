package com.bivgroup.underwriting;
// Generated Oct 19, 2017 6:35:50 PM unknow unknow 


import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * FilledQuestionnaire Generated Oct 19, 2017 6:35:50 PM unknow unknow
 */
@Entity
@Table(name = "UW_FILLEDQUESTIONNAIRE")
public class FilledQuestionnaire implements java.io.Serializable {

    private Long id;

    private Long contract;
    private Long insured;

    private Long stateId;

    private String declarationNumber;

    private Date fillDate;
    private Date dateOfBirth;
    private QuestionnaireVersion questionnaireVersionId_EN;
    private Long questionnaireVersionId;
    /*private QuestionnaireByProduct questionnaireByProductId_EN;*/
    private Long questionnaireByProductId;
    private Long rowStatus;
    private String status;
    private String sex;
    private String commentary;

    private String firstName;
    private String lastName;
    private String patronymic;
    private Double height;
    private Integer weight;

    private Integer systolic;
    private Integer diastolic;

    private Set<UwResult> uwResults = new HashSet<>();

    @Column(name = "QUESTIONNAIREBYPRODUCTID")
    public Long getQuestionnaireByProductId() {
        return questionnaireByProductId;
    }

    public void setQuestionnaireByProductId(Long questionnaireByProductId) {
        this.questionnaireByProductId = questionnaireByProductId;
    }

    @Column(name = "SYSTOLIC")
    public Integer getSystolic() {
        return systolic;
    }

    public void setSystolic(Integer systolic) {
        this.systolic = systolic;
    }

    @Column(name = "DIASTOLIC")
    public Integer getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(Integer diastolic) {
        this.diastolic = diastolic;
    }

    private Set<Answer> answers = new HashSet<>();

    @Column(name = "DATEOFBIRTH")
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Column(name = "SEX")
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Column(name = "FIRSTNAME")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "LASTNAME")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "PATRONYMIC")
    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Column(name = "HEIGHT")
    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    @Column(name = "WEIGHT")
    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    @Column(name = "COMMENTARY")
    public String getCommentary() {
        return commentary;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    @Column(name = "STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @OneToMany(mappedBy = "filledQuestionnaireId_EN", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    public void addAnswer(Answer answer) {
        answer.setFilledQuestionnaireId_EN(this);
        this.answers.add(answer);
    }

    public FilledQuestionnaire() {
    }

    @Column(name = "DECLARATIONNUMBER", nullable = false)
    public String getDeclarationNumber() {
        return declarationNumber;
    }

    public void setDeclarationNumber(String declarationNumber) {
        this.declarationNumber = declarationNumber;
    }

    public FilledQuestionnaire(Long id) {
        this.id = id;
    }

    public FilledQuestionnaire(Long id, Long contract, Long insured, Date fillDate, QuestionnaireVersion questionnaireVersionId_EN, Long questionnaireVersionId, Long rowStatus) {
        this.id = id;
        this.contract = contract;
        this.insured = insured;
        this.fillDate = fillDate;
        this.questionnaireVersionId_EN = questionnaireVersionId_EN;
        this.questionnaireVersionId = questionnaireVersionId;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "UW_FILLEDQUESTIONNAIRE_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id


    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "CONTRID")
    public Long getContract() {
        return this.contract;
    }

    public void setContract(Long contract) {
        this.contract = contract;
    }


    @Column(name = "INSUREDID")
    public Long getInsured() {
        return this.insured;
    }

    public void setInsured(Long insured) {
        this.insured = insured;
    }


    @Column(name = "FILLDATE")
    public Date getFillDate() {
        return this.fillDate;
    }

    public void setFillDate(Date fillDate) {
        this.fillDate = fillDate;
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

    @Transient
    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "filledQuestionnaireId_EN")
    public Set<UwResult> getUwResults() {
        return uwResults;
    }

    public void setUwResults(Set<UwResult> uwResults) {
        this.uwResults = uwResults;
    }

    @Column(name = "STATEID")
    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }
}


