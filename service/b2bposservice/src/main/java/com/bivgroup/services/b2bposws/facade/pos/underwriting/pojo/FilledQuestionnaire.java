package com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo;

import com.bivgroup.beanmapemutils.MapException;
import com.bivgroup.core.dictionary.dao.jpa.RowStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.bivgroup.services.b2bposws.facade.pos.underwriting.Util.format;
import static com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo.UwRiskDetail.uwRiskDetail;
import static java.util.Arrays.asList;
import static java.util.Arrays.fill;
import static java.util.Comparator.comparingInt;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilledQuestionnaire {

    private static final Logger logger = Logger.getLogger(FilledQuestionnaire.class);

    Long id;
    Long questionnaireByProductId;
    String fillingDateDisplay;
    Date fillDate;

    String dateOfBirth;
    String productName;
    String comment;

    String firstName;
    String lastName;
    String patronymic;

    Sex sex;

    QuestionnaireVersion questionnaireVersion;
    Long questionnaireVersionId;
    Status status;
    Long stateId;
    String declarationNumber;

    Integer upperPressure;
    Integer lowerPressure;

    Integer height;
    Integer weight;

    List<Answer> answers = new ArrayList<>();
    Set<Answer> uwAnswers = new HashSet<>();

    List<UwResult> uwResults = new ArrayList<>();

    public enum Status {
        NOT_PROCESSED(9000L) {
            @Override
            public String text() {
                return "Не обработана";
            }
        },
        PROCESSED(9001L) {
            @Override
            public String text() {
                return "Обработана";
            }
        };

        Long id;

        Status(Long id) {
            this.id = id;
        }

        public static final Status byId(Long id) {
            for (Status status : Status.values()) {
                if (id.equals(status.id)) {
                    return status;
                }
            }
            return null;
        }

        public Long getId() {
            return id;
        }

        public String value() {
            return name();
        }

        public abstract String text();
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public enum Sex {
        MALE,
        FEMALE;

        public String value() {
            return name();
        }
    }

    public static final List<Map<String, Object>> sortAnswers(List<Map<String, Object>> allAnswers) {
        // проставим LEVEL и ORDER у каждого ответа для сортировки
        allAnswers.forEach(answer -> {
            Map<String, Object> questionId_EN = (Map<String, Object>) answer.get("questionId_EN");
            if (questionId_EN != null) {
                answer.put("__order", questionId_EN.get("order"));
            }
        });

        allAnswers.forEach(answer -> {
            Map<String, Object> questionId_EN = (Map<String, Object>) answer.get("questionId_EN");
            answer.put("__order", questionId_EN.get("order"));
            int level = 0;
            while (questionId_EN != null) {
                level += 1;
                questionId_EN = (Map<String, Object>) questionId_EN.get("parentId_EN");
            }
            answer.put("__level", level);
        });

        return allAnswers.stream().sorted((o1, o2) -> {
            long order1 = (long) o1.get("__order");
            long order2 = (long) o2.get("__order");
            return (int) (order2 - order1);
        }).collect(Collectors.toList());
    }

    public static FilledQuestionnaire from(Map<String, Object> entity) {
        FilledQuestionnaire filledQuestionnaire = new FilledQuestionnaire();
        filledQuestionnaire.setId((Long) entity.get("id"));
        filledQuestionnaire.setQuestionnaireByProductId((Long) entity.get("questionnaireByProductId"));

        Long questionnaireVersionId = (Long) entity.get("questionnaireVersionId");
        filledQuestionnaire.setQuestionnaireVersionId(questionnaireVersionId);

        List<Map<String, Object>> answers_EN = (List<Map<String, Object>>) entity.get("answers");
        List<Answer> answers = new ArrayList<>();
        for (Map<String, Object> answerId_EN : answers_EN) {
            Answer from = Answer.from(answerId_EN);
            answers.add(from);
        }
        for (Answer answer : answers) {
            QuestionType type = answer.getQuestion().getType();
            Set<Answer.AnswerComponent> answerComponents = answer.getComponents();
            if (type == QuestionType.WEIGHT) {
                Integer weight = answerComponents.iterator().next().getIntValue();
                filledQuestionnaire.setWeight(weight);
            } else if (type == QuestionType.HEIGHT) {
                Integer height = answerComponents.iterator().next().getIntValue();
                filledQuestionnaire.setHeight(height);
            } else if (type == QuestionType.DATE_OF_BIRTH) {
                String dateOfBirthDb = answerComponents.iterator().next().getStringValue();
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirthDb);
                    String dateOfBirthUi = format().format(date);
                    filledQuestionnaire.setDateOfBirth(dateOfBirthUi);
                } catch (ParseException e) {
                    logger.error("", e);
                }
            } else if (type == QuestionType.BLOOD_PRESSURE) {
                try {
                    Iterator<Answer.AnswerComponent> it = answerComponents.stream().sorted(
                            comparingInt(component -> component.getIntValue())
                    ).iterator();
                    Integer lowerPresssure = it.next().getIntValue();
                    filledQuestionnaire.setLowerPressure(lowerPresssure);
                    Integer upperPressure = it.next().getIntValue();
                    filledQuestionnaire.setUpperPressure(upperPressure);
                } catch (Exception e) {
                    logger.error("", e);
                }
            } else if (type == QuestionType.SEX) {
                String sex = answerComponents.iterator().next().getStringValue();
                filledQuestionnaire.setSex(Sex.valueOf(sex));
            }
        }
        answers.sort(comparingInt((Answer answer) -> {
            if (answer.getQuestion() == null) {
                throw new IllegalStateException("question not found/not fetched");
            }
            if (answer.getQuestion().getOrder() == null) {
                answer.getQuestion().setOrder(Integer.MAX_VALUE);
            }
            return answer.getQuestion().getOrder();
        }));
        filledQuestionnaire.setAnswers(answers);
        filledQuestionnaire.setDeclarationNumber((String) entity.get("declarationNumber"));

        Date fillDate = (Date) entity.get("fillDate");
        if (fillDate != null) {
            String fillingDateDisplay = format().format(fillDate);
            filledQuestionnaire.setFillingDateDisplay(fillingDateDisplay);

            filledQuestionnaire.setFillDate(fillDate);
        }

        Date dateOfBirth = (Date) entity.get("dateOfBirth");
        if (dateOfBirth != null) {
            String dateOfBirthString = format().format(dateOfBirth);
            filledQuestionnaire.setDateOfBirth(dateOfBirthString);
        }

        String status = (String) entity.get("status");
        if (status != null) {
            filledQuestionnaire.setStatus(FilledQuestionnaire.Status.valueOf(status));
        }
        Long stateId = (Long) entity.get("stateId");
        if (stateId != null) {
            filledQuestionnaire.setStatus(Status.byId(stateId));
        }

        filledQuestionnaire.setComment((String) entity.get("commentary"));

        filledQuestionnaire.setFirstName((String) entity.get("firstName"));
        filledQuestionnaire.setLastName((String) entity.get("lastName"));
        filledQuestionnaire.setPatronymic((String) entity.get("patronymic"));

        List<Map<String, Object>> uwResults = (List<Map<String, Object>>) entity.get("uwResults");
        Map<String, Object> uwResultId_EN = null;
        if (!uwResults.isEmpty()) {
            uwResultId_EN = uwResults.get(0);
        }
        if (uwResultId_EN != null) {
            List<Map<String, Object>> riskDetails = (List<Map<String, Object>>) uwResultId_EN.get("details");
            List<UwRiskDetail> details = new ArrayList<>();
            // сортировка по ИД
            riskDetails.sort(Comparator.comparingLong(riskDetail -> (Long) riskDetail.get("id")));
            //int i = 0;
            for (Map<String, Object> riskDetail : riskDetails) {
                UwRiskDetail uwRiskDetail = uwRiskDetail(riskDetail);
                details.add(uwRiskDetail);
                /*if (i == 0) {
                    uwRiskDetail.setRiskTitle("Смерть ЛП");
                } else {
                    uwRiskDetail.setRiskTitle("Смерть НС");
                }
                i ++;*/
            }

            UwResult uwResult = new UwResult();
            uwResult.setId((Long) uwResultId_EN.get("id"));
            uwResult.setUwRiskDetails(details);

            filledQuestionnaire.setUwResults(new ArrayList<>(asList(uwResult)));
        }

        return filledQuestionnaire;
    }

    public static final Map<String, Object> toEntity(FilledQuestionnaire filledQuestionnaire) throws ParseException {
        Map<String, Object> filledQuestionnaireId_EN = new HashMap<>();

        filledQuestionnaireId_EN.put("id", filledQuestionnaire.getId());
        filledQuestionnaireId_EN.put("firstName", filledQuestionnaire.getFirstName());
        filledQuestionnaireId_EN.put("lastName", filledQuestionnaire.getLastName());
        filledQuestionnaireId_EN.put("patronymic", filledQuestionnaire.getPatronymic());
        filledQuestionnaireId_EN.put("commentary", filledQuestionnaire.getComment());
        filledQuestionnaireId_EN.put("status", filledQuestionnaire.getStatus().name());
        filledQuestionnaireId_EN.put("stateId", filledQuestionnaire.getStatus().getId());
        // fixme
        filledQuestionnaireId_EN.put("questionnaireVersionId", filledQuestionnaire.getQuestionnaireVersionId());
        filledQuestionnaireId_EN.put("declarationNumber", filledQuestionnaire.getDeclarationNumber());
        /*format().parse(filledQuestionnaire.getFillingDate()*/
        filledQuestionnaireId_EN.put("fillDate", filledQuestionnaire.getFillDate());
        filledQuestionnaireId_EN.put("questionnaireByProductId", filledQuestionnaire.getQuestionnaireByProductId());

        List<Map<String, Object>> answers = new ArrayList<>();
        for (Answer answer : filledQuestionnaire.getAnswers()) {
            QuestionType questionType = answer.getQuestion().getType();
            if (questionType != null && questionType != QuestionType.REGULAR) {
                if (questionType == QuestionType.SEX) {
                    answer.getComponents().iterator().next().setStringValue(filledQuestionnaire.getSex().name());
                } else if (questionType == QuestionType.DATE_OF_BIRTH) {
                    LocalDate uiDate = LocalDate.parse(
                            filledQuestionnaire.getDateOfBirth(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    String databaseFormat = uiDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    answer.getComponents().iterator().next().setStringValue(databaseFormat);
                }
                answer.setRowStatus(RowStatus.MODIFIED.getId());
            }
            Map<String, Object> stringObjectMap = Answer.toEntity(answer);
            answers.add(stringObjectMap);
        }

        filledQuestionnaireId_EN.put("answers", answers);

        List<Map<String, Object>> uwResults = new ArrayList<>();
        for (UwResult uwResult : filledQuestionnaire.getUwResults()) {
            Map<String, Object> objectMap = uwResult.toEntity();
            uwResults.add(objectMap);
        }
        filledQuestionnaireId_EN.put("uwResults", uwResults);

        filledQuestionnaireId_EN.put("rowStatus", RowStatus.MODIFIED);

        return filledQuestionnaireId_EN;
    }

    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    /**
     * @param height в cм (180)
     * @param weight в кг (80)
     * @return
     */
    static Integer calculateImt(Integer height, Integer weight) {
        if (height == null || height == 0) {
            return null;
        }
        if (weight == null) {
            return null;
        }
        Double doubleHeight = Math.ceil(weight / Math.pow(height / (double) 100, 2));
        return doubleHeight.intValue();
    }

    public Integer getImt() {
        return calculateImt(height, weight);
    }

    //<editor-fold desc="get set">

    public Long getQuestionnaireByProductId() {
        return questionnaireByProductId;
    }

    public void setQuestionnaireByProductId(Long questionnaireByProductId) {
        this.questionnaireByProductId = questionnaireByProductId;
    }

    public Date getFillDate() {
        return fillDate;
    }

    public void setFillDate(Date fillDate) {
        this.fillDate = fillDate;
    }

    public Integer getUpperPressure() {
        return upperPressure;
    }

    public void setUpperPressure(Integer upperPressure) {
        this.upperPressure = upperPressure;
    }

    public Integer getLowerPressure() {
        return lowerPressure;
    }

    public void setLowerPressure(Integer lowerPressure) {
        this.lowerPressure = lowerPressure;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuestionnaireVersion getQuestionnaireVersion() {
        return questionnaireVersion;
    }

    public void setQuestionnaireVersion(QuestionnaireVersion questionnaireVersion) {
        this.questionnaireVersion = questionnaireVersion;
    }

    public String getFillingDateDisplay() {
        return fillingDateDisplay;
    }

    public void setFillingDateDisplay(String fillingDateDisplay) {
        this.fillingDateDisplay = fillingDateDisplay;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDeclarationNumber() {
        return declarationNumber;
    }

    public void setDeclarationNumber(String declarationNumber) {
        this.declarationNumber = declarationNumber;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<UwResult> getUwResults() {
        return uwResults;
    }

    public void setUwResults(List<UwResult> uwResults) {
        this.uwResults = uwResults;
    }

    public Set<Answer> getUwAnswers() {
        return uwAnswers;
    }

    public void setUwAnswers(Set<Answer> uwAnswers) {
        this.uwAnswers = uwAnswers;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Long getQuestionnaireVersionId() {
        return questionnaireVersionId;
    }

    public void setQuestionnaireVersionId(Long questionnaireVersionId) {
        this.questionnaireVersionId = questionnaireVersionId;
    }

    //</editor-fold>

    public static void main(String[] args) throws MapException {
        System.out.println(calculateImt(170, 100));
    }

}
