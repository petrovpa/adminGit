package com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {

    Long id;
    QuestionType type;
    String questionText;
    String number;
    Integer order = Integer.MAX_VALUE;
    AnswerEnumValue activationEnumValue;
    String activationFormula;
    Set<QuestionComponent> components;
    Set<Question> subQuestions;

    public enum ComponentAnswerType {
        ENUM,
        ENUMMULTI,
        STRING,
        INTEGER,
        DOUBLE;

        public String value() {
            return name();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QuestionComponent {
        String id;
        String label;
        Integer order;
        ComponentAnswerType type;
        String mask;
        Set<AnswerEnumValue> enumValues;
        boolean isOtherEnabled;

        /*public static QuestionComponent fromHib(com.bivgroup.underwriting.QuestionComponent component) {
            QuestionComponent newQuestionComponent = new QuestionComponent();
            newQuestionComponent.setLabel(component.getLabel());
            if (component.getType() != null) {
                newQuestionComponent.setType(ComponentAnswerType.valueOf(component.getType()));
            }
            newQuestionComponent.setMask(component.getMask());
            newQuestionComponent.setOrder(component.getOrder());

            if (component.getAnswerEnumType() != null) {
                Set<AnswerEnumValue> answerEnumValues = component.getAnswerEnumType()
                        .getValues().stream().map(AnswerEnumValue::fromHib).collect(toSet());
                newQuestionComponent.setEnumValues(answerEnumValues);
            }

            return newQuestionComponent;
        }*/

        //<editor-fold desc="get set">
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public ComponentAnswerType getType() {
            return type;
        }

        public void setType(ComponentAnswerType type) {
            this.type = type;
        }

        public String getMask() {
            return mask;
        }

        public void setMask(String mask) {
            this.mask = mask;
        }

        public Set<AnswerEnumValue> getEnumValues() {
            return enumValues;
        }

        public void setEnumValues(Set<AnswerEnumValue> enumValues) {
            this.enumValues = enumValues;
        }

        public boolean isOtherEnabled() {
            return isOtherEnabled;
        }

        public void setOtherEnabled(boolean otherEnabled) {
            isOtherEnabled = otherEnabled;
        }

        //</editor-fold>
    }

    public static Question fromHibFlat(Map<String, Object> entity) {
        Question newQuestion = new Question();

        Long id = (Long) entity.get("id");
        newQuestion.setId(id);

        newQuestion.setNumber((String) entity.get("num"));
        newQuestion.setQuestionText((String) entity.get("questionText"));
        newQuestion.setOrder((Integer) entity.get("order"));
        String type = (String) entity.get("type");
        if (type != null) {
            newQuestion.setType(QuestionType.valueOf(type));
        }

        /*Set<QuestionComponent> questionComponents =
                question.getComponents().stream().map(QuestionComponent::fromHib).collect(toSet());*/
        //newQuestion.setComponents(questionComponents);

        return newQuestion;
    }

    /*public static Question fromHib(com.bivgroup.underwriting.Question question) {
        Question newQuestion = fromHibFlat(question);
        Set<Question> subQuestions = question.getSubQuestions().stream()
                .map(Question::fromHib)
                //Comparator.comparingInt(Question::getOrder)
                .sorted(Comparator.comparingInt(Question::getOrder))
                .collect(toCollection(LinkedHashSet::new));
        newQuestion.setSubQuestions(subQuestions);
        return newQuestion;
    }*/

    //<editor-fold desc="get set">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public AnswerEnumValue getActivationEnumValue() {
        return activationEnumValue;
    }

    public void setActivationEnumValue(AnswerEnumValue activationEnumValue) {
        this.activationEnumValue = activationEnumValue;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getActivationFormula() {
        return activationFormula;
    }

    public void setActivationFormula(String activationFormula) {
        this.activationFormula = activationFormula;
    }

    public Set<QuestionComponent> getComponents() {
        return components;
    }

    public void setComponents(Set<QuestionComponent> components) {
        this.components = components;
    }

    public Set<Question> getSubQuestions() {
        return subQuestions;
    }

    public void setSubQuestions(Set<Question> subQuestions) {
        this.subQuestions = subQuestions;
    }
    //</editor-fold>
}
