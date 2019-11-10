package com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo;

import com.bivgroup.core.dictionary.dao.jpa.RowStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Answer {

    Long id;
    Set<AnswerComponent> components = new HashSet<>();
    Long questionId;
    Question question;
    Integer rowStatus;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnswerComponent {
        Long id;

        Long questionComponentId;

        String stringValue;
        Integer intValue;
        Double doubleValue;
        String enumValue;
        Long enumValueId;
        Set<String> enumMultiValues;
        Integer rowStatus;

        public static AnswerComponent fromHib(Map<String, Object> entity) {
            AnswerComponent newAnswerComponent = new AnswerComponent();
            newAnswerComponent.setId((Long) entity.get("id"));
            newAnswerComponent.setQuestionComponentId((Long) entity.get("questionComponentId"));
            newAnswerComponent.setDoubleValue((Double) entity.get("doubleValue"));
            newAnswerComponent.setIntValue((Integer) entity.get("intValue"));
            newAnswerComponent.setStringValue((String) entity.get("stringValue"));
            Map<String, Object> enumValueId_EN = (Map<String, Object>) entity.get("enumValueId_EN");
            if (enumValueId_EN != null) {
                newAnswerComponent.setEnumValue((String) enumValueId_EN.get("text"));
                newAnswerComponent.setEnumValueId((Long) enumValueId_EN.get("id"));
            }
            return newAnswerComponent;
        }

        //<editor-fold desc="get set">
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEnumValue() {
            return enumValue;
        }

        public void setEnumValue(String enumValueId) {
            this.enumValue = enumValueId;
        }

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public Integer getIntValue() {
            return intValue;
        }

        public void setIntValue(Integer intValue) {
            this.intValue = intValue;
        }

        public Double getDoubleValue() {
            return doubleValue;
        }

        public void setDoubleValue(Double doubleValue) {
            this.doubleValue = doubleValue;
        }

        public Long getQuestionComponentId() {
            return questionComponentId;
        }

        public void setQuestionComponentId(Long questionComponentId) {
            this.questionComponentId = questionComponentId;
        }

        public Set<String> getEnumMultiValues() {
            return enumMultiValues;
        }

        public void setEnumMultiValues(Set<String> enumMultiValues) {
            this.enumMultiValues = enumMultiValues;
        }

        public Long getEnumValueId() {
            return enumValueId;
        }

        public void setEnumValueId(Long enumValueId) {
            this.enumValueId = enumValueId;
        }

        public Integer getRowStatus() {
            return rowStatus;
        }

        public void setRowStatus(Integer rowStatus) {
            this.rowStatus = rowStatus;
        }

        //</editor-fold>
    }

    public static Answer from(Map<String, Object> entity) {
        Answer newAnswer = new Answer();
        newAnswer.setId((Long) entity.get("id"));
        newAnswer.setQuestionId((Long) entity.get("questionId"));
        Map questionId_EN = (Map) entity.get("questionId_EN");
        if (questionId_EN != null) {
            newAnswer.setQuestion(Question.fromHibFlat(questionId_EN));
        }

        List<Map> list = (List<Map>) entity.get("components");

        Set<AnswerComponent> answerComponents = new HashSet<>();
        for (Map objectMap : list) {
            AnswerComponent answerComponent = AnswerComponent.fromHib(objectMap);
            answerComponents.add(answerComponent);
        }

        newAnswer.setComponents(answerComponents);
        return newAnswer;
    }

    public static Map<String, Object> toEntity(Answer answer) {
        Map<String, Object> answerId_EN = new HashMap<>();
        answerId_EN.put("id", answer.getId());
        Long questionId = answer.getQuestion().getId();
        if (questionId != null) {
            answerId_EN.put("questionId", questionId);
        }

        QuestionType type = answer.getQuestion().getType();
        if (type == null) {
            type = QuestionType.REGULAR;
        }
        if (type != QuestionType.REGULAR) {
            answerId_EN.put("questionId", type.getDatabaseId());
        }

        List<Map<String, Object>> components = new ArrayList<>();
        if (answer.getComponents() != null) {
            for (Answer.AnswerComponent answerComponent : answer.getComponents()) {
                Map<String, Object> answerComponentId_EN = toEntity(answerComponent);
                answerComponentId_EN.put("rowStatus", RowStatus.UNMODIFIED.getId());
                Integer rowStatus = answerComponent.getRowStatus();
                if (rowStatus == null) {
                    answerId_EN.put("rowStatus", RowStatus.UNMODIFIED.getId());
                } else {
                    answerId_EN.put("rowStatus", rowStatus);
                }
                components.add(answerComponentId_EN);
            }
        }
        answerId_EN.put("components", components);

        Integer rowStatus = answer.getRowStatus();
        if (rowStatus == null) {
            answerId_EN.put("rowStatus", RowStatus.UNMODIFIED.getId());
        } else {
            answerId_EN.put("rowStatus", rowStatus);
        }
        return answerId_EN;
    }

    static Map<String, Object> toEntity(Answer.AnswerComponent component) {
        Map<String, Object> map = new HashMap<>();
        Long questionComponentId = component.getQuestionComponentId();
        map.put("id", component.getId());
        map.put("questionComponentId", questionComponentId);
        map.put("stringValue", component.getStringValue());
        map.put("doubleValue", component.getDoubleValue());
        map.put("intValue", component.getIntValue());

        Long enumValueId = component.getEnumValueId();
        map.put("enumValueId", enumValueId);

        map.put("rowStatus", component.getRowStatus());
        return map;
    }

    public void addComponent(AnswerComponent component) {
        this.components.add(component);
    }

    //<editor-fold desc="get set">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<AnswerComponent> getComponents() {
        return components;
    }

    public void setComponents(Set<AnswerComponent> components) {
        this.components = components;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Integer getRowStatus() {
        return rowStatus;
    }

    public void setRowStatus(Integer rowStatus) {
        this.rowStatus = rowStatus;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    //</editor-fold>
}
