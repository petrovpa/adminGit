package com.bivgroup.services.b2bposws.facade.pos.underwriting;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo.FilledQuestionnaire;
import com.bivgroup.services.b2bposws.system.Constants;
import com.bivgroup.underwriting.AnswerEnumValue;
import com.bivgroup.underwriting.Question;
import com.bivgroup.underwriting.QuestionnaireVersion;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.bivgroup.services.b2bposws.facade.pos.underwriting.Util.toMap;
import static com.bivgroup.services.b2bposws.facade.pos.underwriting.Util.wrapListToMap;
import static com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo.FilledQuestionnaire.Status.NOT_PROCESSED;
import static com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo.FilledQuestionnaire.Status.PROCESSED;
import static com.bivgroup.services.b2bposws.facade.pos.underwriting.pojo.FilledQuestionnaire.toEntity;
import static java.time.ZoneOffset.UTC;

@BOName("QuestionnaireFacade")
public class QuestionnaireFacade extends B2BDictionaryBaseFacade {

    private static final Logger logger = Logger.getLogger(QuestionnaireFacade.class);

    public static final String FilledQuestionnaireId_EN = com.bivgroup.underwriting.FilledQuestionnaire.class.getCanonicalName();
    public static final String QuestionnaireVersionId_EN = QuestionnaireVersion.class.getCanonicalName();
    public static final String QuestionnaireByProductId_EN = com.bivgroup.underwriting.QuestionnaireByProduct.class.getCanonicalName();
    public static final String QuestionId_EN = Question.class.getCanonicalName();
    public static final String AnswerEnumValueId_EN = AnswerEnumValue.class.getCanonicalName();

    public static final Map<String, Object> filterBy(String declarationNumber) {
        Map<String, Object> filterBy = new HashMap<>();
        filterBy.put("declarationNumber", declarationNumber);
        return filterBy;
    }

    @SuppressWarnings("unchecked")
    String findProductNameByQuestionnaireVersionId(Long questionnaireVersionId, String login, String password) throws Exception {
        //DictionaryCaller dc = dictionaryCaller();

        Map findBy = new HashMap();
        findBy.put("questionnaireVersionId", questionnaireVersionId);

        List<Map> questionnaireByProductId_ENs =
                dctFindByExample(QuestionnaireByProductId_EN, findBy);
        if (questionnaireByProductId_ENs.isEmpty()) {
            return null;
        }
        Map questionnaireByProductId_EN = questionnaireByProductId_ENs.get(0);
        Long prodConfId = (Long) questionnaireByProductId_EN.get("prodConfId");

        Map findProductBy = new HashMap();
        findProductBy.put("PRODCONFID", prodConfId);

        Map<String, Object> product = callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByID", findProductBy, login, password);

        return null;
    }

    static List<Map> staticQuestions = new ArrayList<>();
    static {
        Map bloodPressure = new HashMap();
        bloodPressure.put("id", 1L);
        bloodPressure.put("type", "BLOOD_PRESSURE");
        Map dateOfBirth = new HashMap();
        dateOfBirth.put("id", 2L);
        dateOfBirth.put("type", "DATE_OF_BIRTH");
        Map sex = new HashMap();
        sex.put("id", 3L);
        sex.put("type", "SEX");
        Map height = new HashMap();
        height.put("id", 4L);
        height.put("type", "HEIGHT");
        Map weight = new HashMap();
        weight.put("id", 5L);
        weight.put("type", "WEIGHT");

        staticQuestions.add(bloodPressure);
        staticQuestions.add(height);
        staticQuestions.add(weight);
        staticQuestions.add(dateOfBirth);
        staticQuestions.add(sex);
    }

    @WsMethod(requiredParams = {})
    public Map dsB2BLoadFilledQuestionnaire(Map params) throws Exception {
        logger.debug("dsB2BLoadFilledQuestionnaire start");
        try {
            String login = params.get(WsConstants.LOGIN).toString();
            String password = params.get(WsConstants.PASSWORD).toString();

            Long filledQuestionnaireId = getLongParam(params.get("id"));
            if (null == filledQuestionnaireId) {
                throw new NotFoundException();
            }

            Map filledQuestionnaireId_EN = dctFindById(FilledQuestionnaireId_EN, filledQuestionnaireId);
            Long questionnaireByProductId = (Long) filledQuestionnaireId_EN.get("questionnaireByProductId");
            Map questionnaireByProductId_EN = dctFindById(QuestionnaireByProductId_EN, questionnaireByProductId);

            String productName = "Неизвестный продукт";
            if (questionnaireByProductId != null) {
                Map findProductBy = new HashMap();
                findProductBy.put("PRODCONFID", questionnaireByProductId_EN.get("prodConfId"));
                Map<String, Object> productCall = callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByID", findProductBy, login, password);
                Map<String, Object> product = (Map<String, Object>) productCall.get("Result");
                if (product != null) {
                    productName = (String) product.get("NAME");
                    if (productName.equalsIgnoreCase("ИСЖ")) {
                        productName = "СмартПолис";
                    } else if (productName.equalsIgnoreCase("ИСЖ Купонный")) {
                        productName = "СмартПолис Купонный";
                    }
                }
            }

            Long questionnaireVersionId = getLongParam(filledQuestionnaireId_EN, "questionnaireVersionId");
            Map questionnaireVersionId_EN = dctFindById(QuestionnaireVersionId_EN, questionnaireVersionId);

            Map<Long, Map> questions = flattenQuestions(questionnaireVersionId_EN);
            // bonus static
            flattenQuestions(staticQuestions, questions);

            List<Map> answerId_ENs = (List<Map>) filledQuestionnaireId_EN.get("answers");

            for (Map answerId_EN : answerId_ENs) {
                Long questionId = (Long) answerId_EN.get("questionId");
                Map questionId_EN = questions.get(questionId);
                //Map questionId_EN = dctFindById(QuestionId_EN, questionId);
                answerId_EN.put("questionId_EN", questionId_EN);

                /*List<Map> componentId_ENs = (List<Map>) answerId_EN.get("components");
                for (Map componentId_EN : componentId_ENs) {
                    Long enumValueId = getLongParam(componentId_EN, "enumValueId");
                    if (enumValueId != null) {
                        Map enumValueId_EN = dctFindById(AnswerEnumValueId_EN, enumValueId);
                        componentId_EN.put("enumValueId_EN", enumValueId_EN);
                    }
                }*/
            }

            FilledQuestionnaire filledQuestionnaire = FilledQuestionnaire.from(filledQuestionnaireId_EN);
            filledQuestionnaire.setProductName(productName);

            Map<String, Object> result = toMap(filledQuestionnaire);
            return result;
        } catch (Exception e) {
            logger.error("failed to load filledQuestionnaire", e);
            throw e;
        } finally {
            logger.debug("dsB2BLoadFilledQuestionnaire end");
        }
    }

    Map<Long, Map> flattenQuestions(Map questionnaireVersionId_EN) {
        Map<Long, Map> accumulator = new HashMap<>();
        List<Map> questions = (List<Map>) questionnaireVersionId_EN.get("questions");
        flattenQuestions(questions, accumulator);
        return accumulator;
    }

    void flattenQuestions(List<Map> questions, Map<Long, Map> accumulator) {
        if (questions == null) {
            return;
        }
        for (Map questionId_EN : questions) {
            Long id = (Long) questionId_EN.get("id");
            accumulator.put(id, questionId_EN);
            List<Map> subQuestions = (List<Map>) questionId_EN.get("subQuestions");
            flattenQuestions(subQuestions, accumulator);
        }
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BUpdateFilledQuestionnaire(Map<String, Object> params) throws Exception {
        String filledQuestionnaireJson = (String) params.get("filledQuestionnaire");
        FilledQuestionnaire filledQuestionnaire = Util.deserializeJsonSimple(filledQuestionnaireJson, FilledQuestionnaire.class);
        if (filledQuestionnaire == null) {
            throw new IOException("Failed to parse filledQuestionnaire JSON");
        }
        Map<String, Object> filledQuestionnaireId_EN = toEntity(filledQuestionnaire);

        //DictionaryCaller dc = dictionaryCaller();
        try {
            //dc.getDAO().getUt().begin();
            Map result = dctCrudByHierarchy(FilledQuestionnaireId_EN, filledQuestionnaireId_EN);
            //dc.getDAO().getUt().commit();
            return result;
        } catch (Exception e) {
            //dc.getDAO().getUt().rollback();
            throw e;
        }
    }

    public static final String orderBy(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        if (params.get("sortModel") != null) {
            ArrayList<Object> sortModel = (ArrayList<Object>) params.get("sortModel");
            if (!sortModel.isEmpty()) {
                for (Iterator iterator = sortModel.iterator(); iterator.hasNext(); ) {
                    Map<String, String> sModel = (Map<String, String>) iterator.next();
                    if ((sModel.get("colId") != null) && (sModel.get("sort") != null)) {
                        sb.append(sModel.get("colId").toString() + " " + sModel.get("sort").toString());
                        sb.append(", ");
                    }
                }
                if (sb.length() > 1) {
                    sb.delete(sb.length() - 2, sb.length());
                    params.put("ORDERBY", sb.toString());
                }
            }
        }
        return sb.toString();
    }

    static void journalFilterBy(Map<String, Object> params) {
        Map<String, Object> filterParams = (Map<String, Object>) params.get("FILTERPARAMS");
        for (Object o1 : filterParams.values()) {
            Map<String, Object> map1 = (Map<String, Object>) o1;
            for (Object o2 : map1.values()) {
                Map<String, Object> kvMap = (Map<String, Object>) o2;
                kvMap.forEach((key, value) -> {
                    String valueStr = (String) value;
                    params.put(key, value);
                    if (key.equals("STATEID")) {
                        List<String> states = new ArrayList<>();
                        if (valueStr.contains("1")) {
                            states.add("NOT_PROCESSED");
                        }
                        if (valueStr.contains("2")) {
                            states.add("PROCESSED");
                        }
                        params.put(key, states);
                    }
                    if (key.equals("FILLDATE")) {
                        String[] split = valueStr.split(" AND ");
                        LocalDateTime from = LocalDateTime.parse(split[0], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z"));
                        LocalDateTime to = LocalDateTime.parse(split[1], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z"));
                        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        params.put("fromDate", Date.from(from.toInstant(UTC)));
                        params.put("toDate", Date.from(to.toInstant(UTC)));
                    }
                });
            }
        }
    }

    /**
     * журнал
     *
     * @return
     */
    @WsMethod(requiredParams = {})
    public Map dsB2BFilledQuestionnaireList(Map params) throws Exception {
        // todo входные параметры фильтрования: дата заполнения анкеты
        // todo ФИО, название страхового продукта
        // todo номер заявления, статус
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        orderBy(params);

        Map<String, Object> filterParams = (Map<String, Object>) params.get("FILTERPARAMS");
        Map<String, Object> special = (Map<String, Object>) filterParams.remove("SPECIAL");
        if (special != null) {
            Map<String, Object> contains = (Map<String, Object>) special.get("12");
            if (contains != null) {
                String fio = (String) contains.get("FIO");
                params.put("FIO", fio);
            }
            Map<String, Object> between = (Map<String, Object>) special.get("9");
            if (between != null) {
                String fillDate = (String) between.get("FILLDATE");
                if (fillDate != null) {
                    String[] split = fillDate.split(" AND ");
                    LocalDateTime from = LocalDateTime.parse(split[0], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z"));
                    LocalDateTime to = LocalDateTime.parse(split[1], DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z"));
                    params.put("fromDate", Date.from(from.toInstant(UTC)));
                    params.put("toDate", Date.from(to.toInstant(UTC)));
                }
            }
            Map<String, Object> greaterThan = (Map<String, Object>) special.get("6");
            if (greaterThan != null) {
                String fillDate = (String) greaterThan.get("FILLDATE");
                if (fillDate != null) {
                    LocalDateTime from = LocalDateTime.parse(fillDate, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z"));
                    params.put("fromDate", Date.from(from.toInstant(UTC)));
                }
            }
            Map<String, Object> greaterOrEqualThan = (Map<String, Object>) special.get("6");
            if (greaterOrEqualThan != null) {
                String fillDate = (String) greaterOrEqualThan.get("FILLDATE");
                if (fillDate != null) {
                    LocalDateTime from = LocalDateTime.parse(fillDate, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z"));
                    params.put("fromDate", Date.from(from.toInstant(UTC)));
                }
            }
            Map<String, Object> lessThan = (Map<String, Object>) special.get("5");
            if (lessThan != null) {
                String fillDate = (String) lessThan.get("FILLDATE");
                if (fillDate != null) {
                    LocalDateTime to = LocalDateTime.parse(fillDate, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z"));
                    params.put("toDate", Date.from(to.toInstant(UTC)));
                }
            }
            Map<String, Object> lessOrEqualThan = (Map<String, Object>) special.get("5");
            if (lessOrEqualThan != null) {
                String fillDate = (String) lessOrEqualThan.get("FILLDATE");
                if (fillDate != null) {
                    LocalDateTime to = LocalDateTime.parse(fillDate, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm z"));
                    params.put("toDate", Date.from(to.toInstant(UTC)));
                }
            }
        }

        Map<String, Object> whereRes = this.callService(Constants.INSTARIFICATORWS, "dsWhereRistrictionGenerate", filterParams, login, password);
        if (whereRes != null) {
            if (whereRes.get(RESULT) != null) {
                whereRes = (Map<String, Object>) whereRes.get(RESULT);
            }
            String customWhere = getStringParam(whereRes.get("customWhere"));
            Map<String, Object> customWhereParams = (Map<String, Object>) whereRes.get("customWhereParams");
            if (customWhereParams != null) {
                params.putAll(customWhereParams);
            }
            String fixedCustomWhere = customWhere.replace("UNDEFINED.", "").replace("undefined.", "");
            params.put("CUSTOMWHERE", fixedCustomWhere);
            logger.debug("Generated custom 'WHERE' restriction: " + fixedCustomWhere);
        }

        //journalFilterBy(params);
        //params.put("DECLARATIONNUMBER", "%112%"/*"33445-11223"*/);
        //params.put("FIO", "%Виталий%");

        Map list = this.selectQuery("dsB2BFilledQuestionnaireList", params);

        List<Map> rows = (List<Map>) list.get("Result");
        for (Map row : rows) {
            String statusString = (String) row.get("STATUS");
            if (statusString != null) {
                FilledQuestionnaire.Status status = FilledQuestionnaire.Status.valueOf(statusString);
                row.put("STATUS", status.text());
            }
            String dateOfBirthString = (String) row.get("DATEOFBIRTH");
            if (dateOfBirthString != null) {
                long years = ChronoUnit.YEARS.between(LocalDate.parse(dateOfBirthString), LocalDate.now());
                row.put("AGE", years);

                String dateOfBirthFormatted = LocalDate.parse(dateOfBirthString).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                row.put("DATEOFBIRTH", dateOfBirthFormatted);
            }
            String sexString = (String) row.get("SEX");
            if (sexString != null) {
                row.put("SEX", sexString.equals("MALE") ? "М" : "Ж");
            }
            String fillDate = (String) row.get("FILLDATE");
            if (fillDate != null) {
                LocalDateTime localDateTime = LocalDateTime.parse(fillDate.replace(" ", "T"));
                String FILLDATESTR = localDateTime.plusHours(3L).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                row.put("FILLDATE", FILLDATESTR);
            }
            //String productName = getStringParam(row, "PRODUCTNAME");
            /*if ("ИСЖ".equalsIgnoreCase(productName)) {
                row.put("PRODUCTNAME", "СмартПолис");
            } else if ("ИСЖ Купонный".equalsIgnoreCase(productName)) {
                row.put("PRODUCTNAME", "СмартПолис Купонный");
            }*/
        }

        return list;
    }

    @WsMethod(requiredParams = {})
    public Map dsB2BLoadListOfFilledQuestionnaireStatuses(Map params) {
        List<Map> states = new ArrayList<>();
        states.add(new HashMap() {
            {
                put("STATEID", NOT_PROCESSED.getId());
                put("STATENAME", "'" + NOT_PROCESSED.name() + "'");
                put("PUBLICNAME", NOT_PROCESSED.text());
            }
        });
        states.add(new HashMap() {
            {
                put("STATEID", PROCESSED.getId());
                put("STATENAME", "'" + PROCESSED.name() + "'");
                put("PUBLICNAME", PROCESSED.text());
            }
        });
        Map<String, Object> result = wrapListToMap(states);
        return result;
    }

    @WsMethod
    public Map dsB2BQuestionnaireProducts(Map params) throws Exception {
        Map result = this.selectQuery("dsB2BQuestionnaireProducts", params);
        return result;
    }

    @WsMethod
    public Map dsB2BQuestionnaireListFilterResetDefault(Map params) throws Exception {
        String login = getStringParam(params, WsConstants.LOGIN);
        String password = getStringParam(params, WsConstants.PASSWORD);

        Map findBy = new HashMap();
        findBy.put("ENTITYKEY", "QUESTIONNAIRELIST-1.1");
        findBy.put("USERACCOUNTID", 49009L);

        Long userAccountId = getLongParam(params, "SESSION_USERACCOUNTID");
        if (userAccountId == null || userAccountId == 0) {
            logger.error("userAccountId is undefined");
            // error happened actually
            return new HashMap();
        }

        findBy.put("USERACCOUNTID", getLongParam(params, "SESSION_USERACCOUNTID"));
        findBy.put("ENTITYTYPE", "journal");
        Map userParamList = this.callService("b2bposws", "dsB2BUserParamBrowseListByParam", findBy, login, password);
        List<Map> resultList = (List<Map>) userParamList.get("Result");
        Map settings = null;
        for (Map userParam : resultList) {
            String jsonSettings = getStringParam(userParam, "VALUE");
            settings = Util.toMap(jsonSettings);

            Map Tsettings = (Map) settings.putIfAbsent("T", new HashMap());
            Map STATEID = (Map) Tsettings.putIfAbsent("STATEID", new HashMap());
            STATEID.put("TYPESYSNAME", "handbook");
            STATEID.put("isEnable", true);
            STATEID.put("tableAlias", "T");
            STATEID.put("type", "in");
            STATEID.put("value", "9000");
        }

        String value = Util.serializeJsonSimple(settings);

        Map setUserParamsCallParams = new HashMap();
        setUserParamsCallParams.put("SESSION_USERACCOUNTID", userAccountId);

        Map qlist = new HashMap();
        qlist.put("QUESTIONNAIRELIST-1.1", value);

        Map journal = new HashMap();
        journal.put("journal", qlist);

        setUserParamsCallParams.put("USERPARAM", journal);

        Map setUserParams = this.callService("b2bposws", "dsB2B_SetUserParams", setUserParamsCallParams, login, password);
        return setUserParams;
    }

}
