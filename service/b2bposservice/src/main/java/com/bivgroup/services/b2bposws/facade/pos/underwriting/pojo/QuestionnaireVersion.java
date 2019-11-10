package com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireVersion {

    Long questionnaireVersionId;
    Set<Question> questions = new HashSet<>();

    /*public static QuestionnaireVersion from(Map<String, Object> entity) {
        QuestionnaireVersion newQuestionnaireVersion = new QuestionnaireVersion();

        List<Map<String, Object>> questions = (List<Map<String, Object>>) entity.get("questions");

        Collection<Question> questions = questions.stream()
                // оставим только корневые вопросы
                .filter(question -> {
                    question.getParentId() == null;
                })
                .map(Question::fromHib)
                .collect(Collectors.toSet());
        newQuestionnaireVersion.setQuestions(questions);
        return newQuestionnaireVersion;
    }*/

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    //<editor-fold desc="get set">
    public Long getQuestionnaireVersionId() {
        return questionnaireVersionId;
    }

    public void setQuestionnaireVersionId(Long questionnaireVersionId) {
        this.questionnaireVersionId = questionnaireVersionId;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }
    //</editor-fold>
}
