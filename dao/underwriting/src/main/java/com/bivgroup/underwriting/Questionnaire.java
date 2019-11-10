package com.bivgroup.underwriting;
// Generated Oct 19, 2017 6:35:49 PM unknow unknow 


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * Questionnaire Generated Oct 19, 2017 6:35:49 PM unknow unknow
 */
@Entity
@Table(name = "UW_QUESTIONNAIRE")
public class Questionnaire implements java.io.Serializable {

    private Long id;
    private Long rVersion;
    private Long questionnaireTypeId;

    private QuestionnaireVersion currentQuestionnaireVersionId_EN;

    private Long currentQuestionnaireVersionId;
    private Long rowStatus;

    private String name;

    private QuestionnaireType questionnaireTypeId_EN;

    private Set<QuestionnaireVersion> versions = new HashSet<>();

    public void addVersion(QuestionnaireVersion questionnaireVersion) {
        this.versions.add(questionnaireVersion);
        questionnaireVersion.setQuestionnaireId_EN(this);
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTIONNAIRETYPEID")
    public QuestionnaireType getQuestionnaireTypeId_EN() {
        return questionnaireTypeId_EN;
    }

    public void setQuestionnaireTypeId_EN(QuestionnaireType questionnaireTypeId_EN) {
        this.questionnaireTypeId_EN = questionnaireTypeId_EN;
    }

    @Column(name = "SYSNAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "questionnaireId_EN")
    public Set<QuestionnaireVersion> getVersions() {
        return versions;
    }

    public void setVersions(Set<QuestionnaireVersion> versions) {
        this.versions = versions;
    }

    public Questionnaire() {
    }


    public Questionnaire(Long id) {
        this.id = id;
    }

    public Questionnaire(Long id, Long rVersion, Long questionnaireTypeId, QuestionnaireVersion currentQuestionnaireVersionId_EN, Long currentQuestionnaireVersionId, Long rowStatus) {
        this.id = id;
        this.rVersion = rVersion;
        this.questionnaireTypeId = questionnaireTypeId;
        this.currentQuestionnaireVersionId_EN = currentQuestionnaireVersionId_EN;
        this.currentQuestionnaireVersionId = currentQuestionnaireVersionId;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "UW_QUESTIONNAIRE_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id


    @Column(name = "ID", nullable = false, insertable = false, updatable = false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name = "RVERSION")
    @Version
    public Long getRVersion() {
        return this.rVersion;
    }

    public void setRVersion(Long rVersion) {
        this.rVersion = rVersion;
    }

    @Column(name = "QUESTIONNAIRETYPEID", updatable = false, insertable = false)
    public Long getQuestionnaireTypeId() {
        return this.questionnaireTypeId;
    }

    public void setQuestionnaireTypeId(Long questionnaireTypeId) {
        this.questionnaireTypeId = questionnaireTypeId;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CURRENTVERSIONID")
    public QuestionnaireVersion getCurrentQuestionnaireVersionId_EN() {
        return this.currentQuestionnaireVersionId_EN;
    }

    public void setCurrentQuestionnaireVersionId_EN(QuestionnaireVersion currentQuestionnaireVersionId_EN) {
        this.currentQuestionnaireVersionId_EN = currentQuestionnaireVersionId_EN;
    }


    @Column(name = "CURRENTVERSIONID", insertable = false, updatable = false)
    public Long getCurrentQuestionnaireVersionId() {
        return this.currentQuestionnaireVersionId;
    }

    public void setCurrentQuestionnaireVersionId(Long currentQuestionnaireVersionId) {
        this.currentQuestionnaireVersionId = currentQuestionnaireVersionId;
    }

    @Transient

    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }


}


