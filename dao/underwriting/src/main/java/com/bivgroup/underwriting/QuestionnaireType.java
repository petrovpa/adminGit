package com.bivgroup.underwriting;
// Generated Oct 19, 2017 6:35:50 PM unknow unknow 


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.GenerationType.SEQUENCE;

/**
 * QuestionnaireType Generated Oct 19, 2017 6:35:50 PM unknow unknow
 */
@Entity
@Table(name = "UW_QUESTIONNAIRETYPE")
public class QuestionnaireType implements java.io.Serializable {

    private Long id;
    private String name;
    private Long rowStatus;

    private Set<Questionnaire> questionnaires = new HashSet<>();

    @OneToMany(mappedBy = "questionnaireTypeId_EN", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    public Set<Questionnaire> getQuestionnaires() {
        return questionnaires;
    }

    public void setQuestionnaires(Set<Questionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QuestionnaireType() {
    }

    public QuestionnaireType(Long id) {
        this.id = id;
    }

    public QuestionnaireType(Long id, Long rowStatus) {
        this.id = id;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "UW_QUESTIONNAIRETYPE_SEQ", allocationSize = 10)
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


