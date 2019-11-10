package com.bivgroup.services.b2bposws.facade.pos.validation;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.utils.date.DSDateUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.bivgroup.services.b2bposws.facade.pos.validation.DescriptionParameter.*;

/**
 * @author Alex Ivashin
 */
public abstract class ValidationParameterB2BCustom {

    private static final String INCORRECT_DATE_ISSUE = "Дата выдачи паспорта некорректна: ";
    private static final String ERROR_MESSAGE_EXPIRED_DATE_ISSUE = INCORRECT_DATE_ISSUE + "срок действия указанного паспорта истек.";
    private static final String ERROR_MESSAGE_EARLIEST_DATE_ISSUE = INCORRECT_DATE_ISSUE + "паспорт не может быть выдан ранее 14 лет с даты рождения.";
    private static final String CHANGE_ERROR_TEXT = "Уважаемый клиент, данных в вашем профиле недостаточно для корректной работы личного кабинета. " +
            "Для передачи полных и актуальных данных в страховую компанию, просьба подать заявление на изменение персональных данных.";

    private static final int FIRST_AGE_OF_ISSUE = 14;
    private static final int SECOND_AGE_OF_ISSUE = 20;
    private static final int THIRD_AGE_OF_ISSUE = 45;
    private static final int ACCEPTABLE_DELAY_MONTH = 2;

    protected static final String DATE_FORMATTER_PATTERN = "dd.MM.yyyy";
    protected static final String DATE_FORMATTER_NO_DOTS_PATTERN = "ddMMyyyy";
    protected static final String DATE_TIME_FORMATTER_PATTERN = DATE_FORMATTER_PATTERN + " HH:mm";
    protected static final String DATE_TIME_TZ_FORMATTER_PATTERN = DATE_TIME_FORMATTER_PATTERN + " z";
    protected static final String DATE_TIME_FORMATTER_DEFAULT_TZ_STR = " GMT+03:00";
    protected static final String DOC_NUMBER_REG_EXP = "^([0-9]{6})$";
    protected static final String DOC_FOREIGN_NUMBER_REG_EXP = "^[0-9A-Za-z]{1,20}";
    protected static final String PASSPORT_SERIES_REG_EXP = "^([0-9]{4})$";
    protected static final String EMAIL_REG_EXP = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$";

    protected static final String ERROR_STRUCTURE_HEADER = "Отсутствуют следующие структуры";
    protected static final String ERROR_STRUCTURE_REQUIRED_HEADER = "Не заполнены следующие структуры";
    protected static final String ERROR_REQUIRED_HEADER = "Не заполнены следующие обязательные поля:";
    protected static final String ERROR_OTHER_HEADER = "Другие ошибки";
    protected static final String ERROR_CRITICAL_HEADER = "Неверное поведение системы";
    protected static final String ERROR_INCORRECT_HEADER = "Неверное значение параметра:";
    protected static final String INCORRECT_SYMBOL = "присутствуют недопустимые символы";

    protected static final String KEY_NAME_DATE_SUFFIX = "DATE"; // например, SOMEDATE
    protected static final String KEY_NAME_TIME_SUFFIX = "TIME"; // прибавляется к имени ключей с датами, поэтому, например, SOMEDATETIME

    private final Calendar calendar;
    private final SimpleDateFormat dateTimeFormatter;
    private final SimpleDateFormat dateTimeFormatterTZ;
    private final SimpleDateFormat dateFormatter;
    private final SimpleDateFormat dateFormatterNoDots;
    protected boolean isNeedChangeError = false;

    protected Map<String, Object> additionalSettings;
    protected Map<String, List<String>> errors;
    protected Map<String, List<DescriptionParameter>> descriptionParameters;

    protected Logger logger;

    //<editor-fold defaultstate="collapsed" desc="Секция для создания описания параметров, которые используются повсеместно. Во избежание дублирования!">
    protected final DescriptionParameter no, foreignNo, series;

    protected final UniqueVerification verificationDateOfIssuePassport;
    // "Профиль. Валидация даты рождения. Дата рождения должна быть 18+"
    protected final UniqueVerification verificationClientDateOfBirthEighteenPlus;

    {

        no = new DescriptionParameter("no", "Номер", new UniqueVerification() {
            @Override
            public boolean check(Object parameter, String errorPrefix) {
                return isValidateDocumentNumber(parameter, errorPrefix, "В номере ", DOC_NUMBER_REG_EXP);
            }
        });
        foreignNo = new DescriptionParameter(no).setVerification(new UniqueVerification() {
            @Override
            public boolean check(Object parameter, String errorPrefix) {
                return isValidateDocumentNumber(parameter, errorPrefix, "В номере ", DOC_FOREIGN_NUMBER_REG_EXP);
            }
        });
        series = new DescriptionParameter("series", "Серия", new UniqueVerification() {
            @Override
            public boolean check(Object parameter, String errorPrefix) {
                return isValidateDocumentNumber(parameter, errorPrefix, "В серии ", PASSPORT_SERIES_REG_EXP);
            }
        });

        verificationDateOfIssuePassport = new UniqueVerification() {
            @Override
            public boolean check(Object parameter, String errorPrefix) {
                boolean result = true;
                if (isNullParameter(parameter)) {
                    result = false;
                }

                if (result) {
                    errorPrefix = isNullParameter(errorPrefix) ? "" : errorPrefix;
                    Date minDate = getDateByParameter(1900, 0, 1);
                    Date maxDate = clearTimeToDate(new Date());
                    Date checkDate = (Date) parseAnyDate(parameter, Date.class, "Дата выдачи");
                    if (checkDate.before(minDate) || checkDate.after(maxDate)) {
                        setOtherError(errorPrefix + "Неверное значение поля дата выдачи паспорта");
                        result = false;
                    }
                }
                return result;
            }
        };

        // "Профиль. Валидация даты рождения. Дата рождения должна быть 18+"
        verificationClientDateOfBirthEighteenPlus = new UniqueVerification() {
            @Override
            public boolean check(Object parameter, String errorPrefix) {
                boolean result = true;
                if (isNullParameter(parameter)) {
                    result = false;
                }

                if (result) {
                    errorPrefix = isNullParameter(errorPrefix) ? "" : errorPrefix;
                    Date currentDate = clearTimeToDate(new Date());
                    Date minDate = changeDateToYears(currentDate, -18);
                    Date checkDate = (Date) parseAnyDate(parameter, Date.class, "Дата рождения клиента");
                    if (checkDate.after(minDate)) {
                        setIncorrectError(errorPrefix + "Дата рождения клиента. Возраст клиента должен быть от 18 лет.");
                        result = false;
                    }
                }
                return result;
            }
        };
    }
    //</editor-fold>

    protected ValidationParameterB2BCustom(Map<String, Object> additionalSettings) {
        this.logger = Logger.getLogger(ValidationParameterB2BCustom.class);
        this.calendar = new GregorianCalendar();
        this.dateTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMATTER_PATTERN);
        this.dateTimeFormatterTZ = new SimpleDateFormat(DATE_TIME_TZ_FORMATTER_PATTERN);
        this.dateFormatter = new SimpleDateFormat(DATE_FORMATTER_PATTERN);
        this.dateFormatterNoDots = new SimpleDateFormat(DATE_FORMATTER_NO_DOTS_PATTERN);
        this.errors = new HashMap<String, List<String>>();
        this.additionalSettings = additionalSettings;
        descriptionParameters = new HashMap<String, List<DescriptionParameter>>();
    }

    public abstract boolean validationParametersBeforeSaving();

    protected boolean isValidateDocumentNumber(Object parameter, String errorPrefix, String prefix, String regExp) {
        boolean result = true;
        if (isNullParameter(parameter)) {
            result = false;
        }

        errorPrefix = isNullParameter(errorPrefix) ? "" : errorPrefix;
        if (result && isValueInvalidByRegExp(parameter, regExp)) {
            setOtherError(errorPrefix + prefix + INCORRECT_SYMBOL);
            result = false;
        }
        return result;
    }

    protected boolean isHaveErrors(Map<String, Object> map) {
        boolean isDataValid = errors.isEmpty();
        if (!isDataValid) {
            map.put("Status", "Error");
            StringBuilder sb = new StringBuilder();
            String errorParamName = "Error";
            if (!this.isNeedChangeError) {
                //errors.forEach((key, value) -> sb.append(key).append(' ').append(value.stream().collect(Collectors.joining(", "))).append('\n'));
                errors.forEach(new BiConsumer<String, List<String>>() {
                    @Override
                    public void accept(String key, List<String> value) {
                        StringJoiner joiner = new StringJoiner(", ");
                        for (String s : value) {
                            joiner.add(s);
                        }
                        sb.append(key).append(' ').append(joiner.toString()).append('\n');
                    }
                });
            } else {
                sb.append(CHANGE_ERROR_TEXT);
                errorParamName = "changeError";
            }
            map.put(errorParamName, sb.toString());
        }

        return isDataValid;
    }

    public void setError(String errorType, String errorMessage) {
        List<String> errorMessages;
        if (errors.containsKey(errorType)) {
            errorMessages = errors.get(errorType);
            errorMessages.add(errorMessage);
        } else {
            errorMessages = new ArrayList<String>();
            errorMessages.add(errorMessage);
            errors.put(errorType, errorMessages);
        }
    }

    public void setRequiredError(String parameterName) {
        setError(ERROR_REQUIRED_HEADER, parameterName);
    }

    public void setOtherError(String parameterName) {
        setError(ERROR_OTHER_HEADER, parameterName);
    }

    public void setCriticalError(String parameterName) {
        setError(ERROR_CRITICAL_HEADER, parameterName);
    }

    public void setIncorrectError(String parameterName) {
        setError(ERROR_INCORRECT_HEADER, parameterName);
    }

    public boolean isNullAndNoDeletedRowStatus(Object rowStatus) {
        return isNullParameter(rowStatus) || !isDeletedRowStatus(rowStatus);
    }

    protected boolean isNotNullAndDeletedRowStatus(Map<String, Object> map) {
        Object rowStatus = map.get(RowStatus.ROWSTATUS_PARAM_NAME);
        return !isNullParameter(rowStatus) && isDeletedRowStatus(rowStatus);
    }

    protected boolean isDeletedRowStatus(Object parametr) {
        Integer rowStatus = (Integer) parametr;
        return RowStatus.getRowStatusById(rowStatus).equals(RowStatus.DELETED);
    }

    protected Map<String, Object> getMapOfSearchMapByPrefixAndSysName(Map<String, Object> searchMap, String prefix, String sysName) {
        Map<String, Object> result = Collections.EMPTY_MAP;

        List<Map<String, Object>> objects = (List<Map<String, Object>>) searchMap.get(prefix + "LIST");
        if (objects != null) {
            for (Map<String, Object> object : objects) {
                Object searchSysName = object.get(prefix + "SYSNAME");
                if (object.get(prefix + "SYSNAME") != null
                        && searchSysName.equals(searchSysName.toString())) {
                    result = object;
                    break;
                }
            }
        }
        return result;
    }

    protected Map<String, Object> getObjectOfListBySysnameAndValueSysname(List<Map<String, Object>> list, String sysname, String value) {
        Map<String, Object> result = Collections.EMPTY_MAP;

        Object valueSysname;
        for (Map<String, Object> item : list) {
            valueSysname = item.get(sysname);
            if (valueSysname != null && value.equals(valueSysname)) {
                result = item;
                break;
            }
        }
        return result;
    }

    protected Map<String, Object> getMapOfListBySysname(List<Map<String, Object>> list, String sysName) {
        Map<String, Object> result = Collections.EMPTY_MAP;
        for (Map<String, Object> insObj : list) {
            Map<String, Object> insObjMap = (Map<String, Object>) insObj.get(sysName);
            result = insObjMap;
        }
        return result;
    }

    protected void checkRequiredParameter(String checkedListName, Map<String, Object> checkedMap) {
        checkRequiredParameterAddPrefix(checkedListName, null, checkedMap);
    }

    protected void checkRequiredParameterAddPrefix(String checkedListName, String prefixError, Map<String, Object> checkedMap) {
        checkRequiredParameterDependingMapAddPrefix(checkedListName, prefixError, checkedMap, null);
    }

    protected void checkRequiredParameterDependingMapAddPrefix(String checkedListName, String prefixError,
                                                               Map<String, Object> checkedMap, Map<String, Object> dependingMap) {
        if (descriptionParameters.isEmpty()) {
            setCriticalError("отсутствует лист описания параметров.");
            return;
        }
        List<DescriptionParameter> descriptionParameterList = this.descriptionParameters.get(checkedListName);

        StringBuilder textError;
        String prefix = prefixError == null ? "" : prefixError;
        Object checkedParameter, determinesParameter;
        String parameterName;
        boolean isRequired;
        boolean isNullDependingMap = isNullCollection(dependingMap);

        if (isNotNullAndDeletedRowStatus(checkedMap)) {
            return;
        }

        boolean isValidParam = true;
        boolean isValidParamVerification = true;
        boolean isNullValue = false;
        for (DescriptionParameter descriptionParameter : descriptionParameterList) {
            checkedParameter = checkedMap.get(descriptionParameter.getSysName());
            parameterName = descriptionParameter.getParameterName();
            textError = new StringBuilder(prefix).append(parameterName);
            if (descriptionParameter.isMissingDependentParameter()) {
                isNullValue = isNullParameter(checkedParameter);
                if (isNullValue) {
                    setRequiredError(textError.toString());
                    isValidParam = false;
                } else {
                    isValidParam = checkIncorrectParameter(descriptionParameter, checkedParameter, textError);
                }
            } else {
                determinesParameter = isNullDependingMap ? checkedMap.get(descriptionParameter.getSysNameDetermines())
                        : dependingMap.get(descriptionParameter.getSysNameDetermines());
                isRequired = descriptionParameter.isRequiredDependingOfValueDeterminesParameter(determinesParameter);
                isNullValue = isNullParameter(checkedParameter);
                if (isNullValue && isRequired) {
                    setRequiredError(textError.toString());
                    isValidParam = false;
                } else {
                    isValidParam = checkIncorrectParameter(descriptionParameter, checkedParameter, textError);
                }
            }
            // если значение нет, то проверять не нужно
            if (isNullValue) {
                isValidParamVerification = descriptionParameter.applyUniqueVerification(checkedParameter, prefixError);
            }

            if ((!isValidParam || !isValidParamVerification) && descriptionParameter.needChangePersonalData()) {
                this.isNeedChangeError = true;
                break;
            }
        }
    }

    private boolean checkIncorrectParameter(DescriptionParameter rp, Object checkParam, StringBuilder textError) {
        boolean result = true;
        if (rp.isLongValueParam() && rp.isIncorrectValue((Long.parseLong(checkParam.toString())))) {
            setIncorrectError(textError.toString());
            result = false;
        }
        return result;
    }

    protected boolean isNullParameter(Object parameter) {
        return parameter == null || parameter.toString().trim().length() == 0;
    }

    protected boolean isNullCollection(Object collection) {
        boolean result = collection == null;
        if (!result) {
            if (collection instanceof Collection) {
                return ((Collection) collection).isEmpty();
            }
            if (collection instanceof Map) {
                return ((Map) collection).isEmpty();
            }
        }
        return result;
    }

    protected boolean isAllElementListWithStatusDeleted(List<Map<String, Object>> list) {
        int length = list.size();
        int count = 0;
        for (Map<String, Object> item : list) {
            if (isNotNullAndDeletedRowStatus(item)) {
                count++;
            }
        }

        return count == length;
    }

    protected boolean getBooleanByIntegerValue(Object parameter) {
        return !isNullParameter(parameter) && Integer.parseInt(parameter.toString()) == 1;
    }

    protected boolean getBooleanParameter(Object bean) {
        return !isNullParameter(bean) && Boolean.parseBoolean(bean.toString());
    }

    protected String checkDateOfIssue(Object dateOfIssueObj, Object dateOfBirthObj) {
        if (isNullParameter(dateOfBirthObj) || isNullParameter(dateOfIssueObj)) {
            return "";
        }

        Date dateOfIssue = (Date) parseAnyDate(dateOfIssueObj, Date.class, "dateOfIssue");
        Date dateOfBirth = (Date) parseAnyDate(dateOfBirthObj, Date.class, "dateOfIssue");

        Date settlementDateOfBirth = changeDateToYears(dateOfBirth, FIRST_AGE_OF_ISSUE);
        if (dateOfIssue.before(settlementDateOfBirth)) {
            return ERROR_MESSAGE_EARLIEST_DATE_ISSUE;
        }

        Date leftRangeDate = dateOfBirth;
        leftRangeDate = changeDateToYears(leftRangeDate, SECOND_AGE_OF_ISSUE);
        leftRangeDate = changeDateToMonths(leftRangeDate, ACCEPTABLE_DELAY_MONTH);

        Date rightRangeDate = dateOfBirth;
        rightRangeDate = changeDateToYears(rightRangeDate, THIRD_AGE_OF_ISSUE);
        rightRangeDate = changeDateToMonths(rightRangeDate, ACCEPTABLE_DELAY_MONTH);
        Date rightRageOneDay = addOneDayToDate(rightRangeDate);

        Date currentDate = clearTimeToDate(new Date());
        Date twentyDateOfBirth = changeDateToYears(dateOfBirth, SECOND_AGE_OF_ISSUE);
        Date fortyDateOfBirth = changeDateToYears(dateOfBirth, THIRD_AGE_OF_ISSUE);
        if (((leftRangeDate.before(currentDate) && rightRageOneDay.after(currentDate)) && twentyDateOfBirth.after(dateOfIssue))
                || rightRangeDate.before(currentDate) && fortyDateOfBirth.after(dateOfIssue)) {
            return ERROR_MESSAGE_EXPIRED_DATE_ISSUE;
        }

        return "";
    }

    protected int getYearByDate(Date date) {
        int result = -1;
        if (date != null) {
            calendar.setTime(date);
            result = calendar.get(Calendar.YEAR);
        }
        return result;
    }

    protected Date changeDateToDays(Date date, int days) {
        Date result = date;
        if (result != null) {
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, days);
            result = calendar.getTime();
        }
        return result;
    }

    protected Date addOneDayToDate(Date date) {
        return changeDateToDays(date, 1);
    }

    protected Date subtractOneDayToDate(Date date) {
        return changeDateToDays(date, -1);
    }

    protected Date changeDateToMonths(Date date, int months) {
        Date result = date;
        if (result != null) {
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, months);
            result = calendar.getTime();
        }
        return result;
    }

    protected Date addOneMonthToDate(Date date) {
        return changeDateToMonths(date, 1);
    }

    protected Date changeDateToYears(Date date, int years) {
        Date result = date;
        if (result != null) {
            calendar.setTime(date);
            calendar.add(Calendar.YEAR, years);
            result = calendar.getTime();
        }
        return result;
    }

    protected Date clearTimeToDate(Date date) {
        Date result = date;
        if (result != null) {
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            result = calendar.getTime();
        }
        return result;
    }

    protected Date getDateByParameter(int year, int month, int date) {
        calendar.set(year, month, date, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public void setDescriptionParameters(Map<String, List<DescriptionParameter>> descriptionParameters) {
        this.descriptionParameters = descriptionParameters;
    }

    protected boolean isValueInvalidByRegExp(Object value, String regExp) {
        return !isValueValidByRegExp(value, regExp);
    }

    protected boolean isValueValidByRegExp(Object value, String regExp) {
        if (value == null) {
            return false;
        }

        Pattern pattern = Pattern.compile(regExp);
        String checkedString = getStringParameter(value);
        Matcher matcher = pattern.matcher(checkedString);
        return matcher.matches();
    }

    protected String getStringParameter(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        }
        if (bean instanceof Double) {
            DecimalFormat df = new DecimalFormat("0");
            df.setMaximumFractionDigits(2);
            return df.format(Double.valueOf(bean.toString()).doubleValue());
        }
        return bean.toString();
    }

    /**
     * Преобразует дату из/в любой из перечисленных типов - String, Date,
     * Double. Для преобразований, использующих тип String, будет происходить
     * потеря точности (до минуты).
     *
     * @param dateValue   объект, содержащий дату в одном из перечисленных выше
     *                    типов
     * @param targetClass целевой тип преобразования (любой из перечисленных
     *                    выше)
     * @param keyName     наименование ключа, указывающего на обрабатываемую дату
     *                    (только для протоколирования)
     * @return преобразованную в указанный тип дату
     */
    protected Object parseAnyDate(Object dateValue, Class targetClass, String keyName) {
        return parseAnyDate(dateValue, null, targetClass, keyName);
    }

    /**
     * Преобразует дату из/в любой из перечисленных типов - String, Date,
     * Double. Для преобразований, использующих тип String, будет происходить
     * потеря точности (до минуты).
     *
     * @param dateValue   объект, содержащий дату в одном из перечисленных выше
     *                    типов
     * @param timeValue   объект, содержащий время (только для String)
     * @param targetClass целевой тип преобразования (любой из перечисленных
     *                    выше)
     * @param keyName     наименование ключа, указывающего на обрабатываемую дату
     *                    (только для протоколирования)
     * @return преобразованную в указанный тип дату
     */
    protected Object parseAnyDate(Object dateValue, Object timeValue, Class targetClass, String keyName) {

        // String -> Date
        if (dateValue instanceof String) {
            if (String.class.equals(targetClass)) {
                return dateValue.toString();
            }
            // попытка преобразовать строку в дату
            // если в строке есть "and"  - то дата - ограничение для between
            String curVal = dateValue.toString();
            curVal = curVal.toUpperCase();
            if (curVal.contains(" AND ")) {
                // получаем даты из ограничения between и собираем обратно
                String[] betweenStr = curVal.split(" AND ");
                Date dateValue1 = strToDateForBetween(betweenStr[0], keyName);
                Date dateValue2 = strToDateForBetween(betweenStr[1], keyName);
                if ((dateValue1 != null) && (dateValue2 != null)) {
                    if (Double.class.equals(targetClass)) {
                        dateValue = String.valueOf(DSDateUtil.convertDate(dateValue1).doubleValue()) + " AND "
                                + String.valueOf(DSDateUtil.convertDate(dateValue2).doubleValue());
                    } else if (Date.class.equals(targetClass)) {
                        dateValue = dateValue1.toString() + " AND " + dateValue2.toString();
                    }
                }
            } else {
                dateValue = strToDateTime(dateValue, timeValue, keyName);
            }
        }
        // Double -> Date
        if (dateValue instanceof BigDecimal) {
            dateValue = ((BigDecimal) dateValue).doubleValue();
        }

        if (dateValue instanceof Double) {
            if (Double.class.equals(targetClass)) {
                return (Double) dateValue;
            }
            // попытка преобразовать число из БД в дату
            try {
                Double dateDouble = (Double) dateValue;
                dateValue = DSDateUtil.convertDate(dateDouble);
            } catch (Exception ex) {
                logDateParseEx(keyName, dateValue, "Double to Date conversion", ex);
            }
        }
        // Date -> String || Double
        if (dateValue instanceof Date) {
            if (Date.class.equals(targetClass)) {
                return (Date) dateValue;
            }
            // Date -> String
            if (String.class.equals(targetClass)) {
                // попытка преобразовать дату в строку
                try {
                    Date dateDate = (Date) dateValue;
                    dateValue = dateTimeFormatter.format(dateDate);
                } catch (Exception ex) {
                    logDateParseEx(keyName, dateValue, "Date to String conversion", ex);
                }
                // немедленный возврат значения, поскольку была проверка на целевой тип
                return dateValue;
            }
            // Date -> Double
            if (Double.class.equals(targetClass)) {
                // попытка преобразовать дату в число для БД
                try {
                    Date dateDate = (Date) dateValue;
                    dateValue = DSDateUtil.convertDate(dateDate).doubleValue();
                } catch (Exception ex) {
                    logDateParseEx(keyName, dateValue, "Date to Double conversion", ex);
                }
                // немедленный возврат значения, поскольку была проверка на целевой тип
                return dateValue;
            }
        }
        // возврат значения, вероятнее всего не преобразованного вовсе (например, из-за исключения)
        return dateValue;
    }

    private Date strToDateForBetween(String dateStr, String keyName) {
        Date dateValue = null;
        try {
            // дата вида "dd.MM.yyyy HH:mm" или "dd.MM.yyyy HH:mm z"
            if (dateStr.length() == DATE_TIME_FORMATTER_PATTERN.length()) {
                // дату вида "dd.MM.yyyy HH:mm" необходимо привести к "dd.MM.yyyy HH:mm z" (считая, что часовая зона по умолчанию это DATE_TIME_FORMATTER_DEFAULT_TZ_STR)
                dateStr = dateStr + DATE_TIME_FORMATTER_DEFAULT_TZ_STR;
            }
            // дата вида "dd.MM.yyyy HH:mm z"
            dateValue = dateTimeFormatterTZ.parse(dateStr);
        } catch (ParseException ex1) {
            try {
                // дата вида "dd.MM.yyyy"
                dateValue = dateFormatter.parse(dateStr);
            } catch (ParseException ex2) {
                try {
                    // дата вида "ddMMyyyy"
                    dateValue = dateFormatterNoDots.parse(dateStr);
                } catch (ParseException ex3) {
                    logDateParseEx(keyName, dateStr, "format " + dateFormatter.toPattern(), ex1);
                    logDateParseEx(keyName, dateStr, "format " + dateFormatterNoDots.toPattern(), ex2);
                    logDateParseEx(keyName, dateStr, "format " + dateTimeFormatterTZ.toPattern(), ex3);
                }
            }
        }
        return dateValue;
    }

    private Date strToDateTime(Object date, Object time, String keyName) {
        Date dateValue = null;
        String timeStr;
        if (time == null) {
            timeStr = "00:00";
        } else {
            timeStr = time.toString();
        }
        String dateTimeStr = date.toString() + " " + timeStr;
        try {
            dateValue = dateTimeFormatter.parse(dateTimeStr);
        } catch (ParseException ex1) {
            logDateParseEx(keyName, date, "format " + dateTimeFormatter.toPattern(), ex1);
        }
        return dateValue;
    }

    private void logDateParseEx(String keyName, Object dateValue, String conversion, Exception ex) {
        logger.error("Parsing key's '" + keyName + "' value '" + dateValue + "' (using " + conversion + ") caused error:", ex);
    }

    protected void parseDatesToDate(Map<String, Object> data) {
        parseDates(data, Date.class);
    }

    protected void parseDatesToString(Map<String, Object> data) {
        parseDates(data, String.class);
    }

    protected void parseDatesToDate(List<Map<String, Object>> data) {
        parseDates(data, Date.class);
    }

    protected void parseDatesToString(List<Map<String, Object>> data) {
        parseDates(data, String.class);
    }

    /**
     * Рекурсивно вызывает {@link #parseAnyDate} на всех значениях, имя ключа
     * которых имеет вид "*DATE"
     *
     * @param data        обрабатываемая карта (может содержать списки и вложенные
     *                    карты)
     * @param targetClass целевой тип преобразования
     */
    protected void parseDates(Map<String, Object> data, Class targetClass) {
        parseDates(data, targetClass, "*", KEY_NAME_DATE_SUFFIX, KEY_NAME_TIME_SUFFIX, false, false);
    }

    protected void parseDates(List<Map<String, Object>> dataList, Class targetClass) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("#", dataList);
        boolean addParamWithoutSuffix = false;
        boolean analyzeNoSuffixParamAndAddSuffix = false;
        parseDates(data, targetClass, "*", KEY_NAME_DATE_SUFFIX, KEY_NAME_TIME_SUFFIX, addParamWithoutSuffix, analyzeNoSuffixParamAndAddSuffix);
    }

    protected void parseDates(Map<String, Object> data, Class targetClass, String dateSuffix, String timeSuffix,
                              boolean addParamWithoutSuffix, boolean analyzeNoSuffixParamAndAddSuffix) {
        parseDates(data, targetClass, "*", dateSuffix, timeSuffix, addParamWithoutSuffix, analyzeNoSuffixParamAndAddSuffix);
    }

    /**
     * Рекурсивно вызывает {@link #parseAnyDate} на всех значениях, имя ключа
     * которых имеет вид "*DATE"
     *
     * @param data                             обрабатываемая карта (может содержать списки и вложенные
     *                                         карты)
     * @param targetClass                      целевой тип преобразования
     * @param dataNodePath                     имя обрабатываемого узла - ключ, номер элемента и
     *                                         т.п. (только для протоколирования)
     * @param dateSuffix                       суффикс для анализа даты
     * @param timeSuffix                       суффикс для анализа времени
     * @param addParamWithoutSuffix            добавлять в мапу с датой параметр без
     *                                         суффикса даты
     * @param analyzeNoSuffixParamAndAddSuffix анализировать дату как
     *                                         java.util.Date без суффикса, и добавлять в мапу с суффиксом
     */
    protected void parseDates(Map<String, Object> data, Class targetClass, String dataNodePath,
                              String dateSuffix, String timeSuffix, boolean addParamWithoutSuffix, boolean analyzeNoSuffixParamAndAddSuffix) {
        boolean isTargetClassString = String.class.equals(targetClass);
        Map<String, Object> additionalEntries = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String keyName = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String keyFullName = dataNodePath + "." + keyName;
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    parseDates(map, targetClass, keyFullName, dateSuffix, timeSuffix, addParamWithoutSuffix, analyzeNoSuffixParamAndAddSuffix);
                } else if (value instanceof List) {
                    List<Object> list = (List<Object>) value;
                    for (int i = 0; i < list.size(); i++) {
                        Object rawElement = list.get(i);
                        if (rawElement instanceof Map) {
                            Map<String, Object> element = (Map<String, Object>) rawElement;
                            parseDates(element, targetClass, keyFullName + "[" + i + "]", dateSuffix, timeSuffix, addParamWithoutSuffix, analyzeNoSuffixParamAndAddSuffix);
                        }
                    }
                } else if (keyName.endsWith(dateSuffix)) {
                    String timeKeyName = keyName + timeSuffix;
                    Object timeValue = data.get(timeKeyName);
                    Object newValue = parseAnyDate(value, timeValue, targetClass, keyFullName); // без протоколирования

                    if ((isTargetClassString) && (newValue instanceof String)) {
                        String[] dateTime = getStringParameter(newValue).split(" ");
                        if (dateTime.length == 2) {
                            newValue = dateTime[0];
                            additionalEntries.put(timeKeyName, dateTime[1]);
                        } else {
                            logger.debug("Не удалось выделить время из строкового представления даты '" + newValue + "', содержащегося в атрибуте '" + keyFullName + "'.");
                        }
                    }
                    entry.setValue(newValue);
                    if (addParamWithoutSuffix) {
                        additionalEntries.put(entry.getKey().substring(0, entry.getKey().length() - dateSuffix.length()),
                                entry.getValue());
                    }
                } else if ((value instanceof Date) && (analyzeNoSuffixParamAndAddSuffix)) {
                    Object newValue = parseAnyDate(value, targetClass, keyFullName);
                    if ((isTargetClassString) && (newValue instanceof String)) {
                        String[] dateTime = getStringParameter(newValue).split(" ");
                        if (dateTime.length == 2) {
                            newValue = dateTime[0];
                            additionalEntries.put(entry.getKey() + dateSuffix + timeSuffix, dateTime[1]);
                        } else {
                            logger.debug("Не удалось выделить время из строкового представления даты '" + newValue + "', содержащегося в атрибуте '" + keyFullName + "'.");
                        }
                    }
                    entry.setValue(newValue);
                    additionalEntries.put(entry.getKey() + dateSuffix, entry.getValue());
                    //additionalEntries.put(entry.getKey() + dateSuffix + timeSuffix, entry.getValue());
                }
            }
        }
        data.putAll(additionalEntries);
    }

}
