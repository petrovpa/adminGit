/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.i900.facade;

import com.bivgroup.ws.i900.system.Constants;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;
import ru.diasoft.utils.date.DSDateUtil;

/**
 *
 * @author averichevsm
 */
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class Mort900BaseFacade extends BaseFacade {

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat dateFormatterNoDots = new SimpleDateFormat("ddMMyyyy");

    private static final String DATE_TIME_FORMATTER_PATTERN = "dd.MM.yyyy HH:mm:ss";
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMATTER_PATTERN);

    private static final String DATE_TIME_TZ_FORMATTER_PATTERN = DATE_TIME_FORMATTER_PATTERN + " z";
    private final SimpleDateFormat dateTimeFormatterTZ = new SimpleDateFormat(DATE_TIME_TZ_FORMATTER_PATTERN);

    private static final String DATE_TIME_FORMATTER_DEFAULT_TZ_STR = " GMT+03:00";

    protected static final String KEY_NAME_DATE_SUFFIX = "DATE"; // например, SOMEDATE
    protected static final String KEY_NAME_TIME_SUFFIX = "TIME"; // прибавляется к имени ключей с датами, поэтому, например, SOMEDATETIME
    protected static final String KEY_NAME_DATE_SUFFIX_NEW = "$date"; // например, SOME$date
    protected static final String KEY_NAME_TIME_SUFFIX_NEW = "time"; // прибавляется к имени ключей с датами, поэтому, например, SOME$datetime
    
    protected static final Long BANKCASHFLOW_TYPEMODIFYSTRING_ADD = 0L;
    protected static final Long BANKCASHFLOW_TYPEMODIFYSTRING_UPDATE = 1L;
    protected static final Long BANKCASHFLOW_STATE_ERROR = 4016L;
    
    // Имя сервиса для вызова методов из i900
    protected static final String THIS_SERVICE_NAME = Constants.B2BPOSWS;
    // Имя сервиса для вызова методов из i900 по работе с файлами
    protected static final String THIS_FILE_SERVICE_NAME = Constants.FILEB2BPOSWS;
    //protected static final String THIS_FILE_SERVICE_NAME = THIS_SERVICE_NAME; // !только для отладки!
    // Имя сервиса для вызова методов из b2bposservice
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    protected static final String COREWS_SERVICE_NAME = Constants.COREWS;

    // имя ключа в мапе договора, указывающего на мапу с продуктом
    protected String PRODUCTMAP_KEYNAME = "PRODCONF";
    // имя ключа в мапе договора, указывающего на список плановых платежей
    protected String PAYMENTPLAN_KEYNAME = "PAYMENTPLAN";
    // имя ключа в мапе договора, указывающего на список фактических платежей
    protected String PAYMENTFACT_KEYNAME = "PAYMENTFACT";
    // todo: коммент
    protected String PAYMENTPLAN_SELECTEDRECORD_KEYNAME = "PAYMENTPLANSELECTEDRECORD";
    // todo: коммент
    protected String PAYMENTPLAN_RECORD_KEYNAME = "PAYMENTPLANRECORD";
    // todo: коммент
    protected String PAYMENTFACT_RECORD_KEYNAME = "PAYMENTFACTRECORD";
    
    protected Object parseAnyDate(Object dateValue, Class targetClass, String keyName, Boolean logged) {
        return parseAnyDate(dateValue, null, targetClass, keyName, logged);
    }

    /**
     * Аналогично {@link #parseAnyDate(Object, Object, Class, String)}, но с
     * флагом протоколирования
     *
     * @param dateValue объект, содержащий дату в одном из перечисленных выше
     * типов
     * @param timeValue объект, содержащий время (только для String)
     * @param targetClass целевой тип преобразования (любой из перечисленных
     * выше)
     * @param keyName наименование ключа, указывающего на обрабатываемую дату
     * (только для протоколирования)
     * @param logged флаг протоколирования
     *
     * @return преобразованную в указанный тип дату
     */
    protected Object parseAnyDate(Object dateValue, Object timeValue, Class targetClass, String keyName, Boolean logged) {
        if ((logged) && (logger.isDebugEnabled())) {
            Object result = parseAnyDate(dateValue, timeValue, targetClass, keyName);
            StringBuffer logStr = new StringBuffer();
            logStr.append("Атрибут '").append(keyName);
            logStr.append("' содержал дату '").append(dateValue).append("'");
            if (dateValue != null) {
                logStr.append(" ('").append(dateValue.getClass().getSimpleName()).append("')");
            }
            if (timeValue != null) {
                logStr.append(", а атрибут '").append(keyName).append(KEY_NAME_TIME_SUFFIX);
                logStr.append("' - время '").append(timeValue);
                logStr.append("' ('").append(timeValue.getClass().getSimpleName()).append("')");
            }
            logStr.append(", преобразование в '").append(targetClass.getSimpleName()).append("' завершено с результатом: '").append(result).append("'.");
            logger.debug(logStr);
            return result;
        } else {
            return parseAnyDate(dateValue, timeValue, targetClass, keyName);
        }
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
                    if (dateStr.indexOf(".") > 0) {
                        try {
                            Double dateStrDouble = Double.valueOf(dateStr);
                            Object tryParseDate = parseAnyDate(dateStrDouble, Date.class, "betweenDate");
                            if (tryParseDate != null) {
                                dateValue = (Date) tryParseDate;
                            } else {
                                dateValue = dateFormatterNoDots.parse(dateStr);
                            }

                        } catch (Exception e) {
                            dateValue = dateFormatterNoDots.parse(dateStr);
                        }
                    }
                } catch (ParseException ex3) {
                    logDateParseEx(keyName, dateStr, "format " + dateFormatter.toPattern(), ex1);
                    logDateParseEx(keyName, dateStr, "format " + dateFormatterNoDots.toPattern(), ex2);
                    logDateParseEx(keyName, dateStr, "format " + dateTimeFormatterTZ.toPattern(), ex3);
                }
            }
        }
        return dateValue;
    }

    private void logDateParseEx(String keyName, Object dateValue, String conversion, Exception ex) {
        logger.error("Parsing key's '" + keyName + "' value '" + dateValue + "' (using " + conversion + ") caused error:", ex);
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

    /**
     * Преобразует дату из/в любой из перечисленных типов - String, Date,
     * Double. Для преобразований, использующих тип String, будет происходить
     * потеря точности (до минуты).
     *
     * @param dateValue объект, содержащий дату в одном из перечисленных выше
     * типов
     * @param targetClass целевой тип преобразования (любой из перечисленных
     * выше)
     * @param keyName наименование ключа, указывающего на обрабатываемую дату
     * (только для протоколирования)
     *
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
     * @param dateValue объект, содержащий дату в одном из перечисленных выше
     * типов
     * @param timeValue объект, содержащий время (только для String)
     * @param targetClass целевой тип преобразования (любой из перечисленных
     * выше)
     * @param keyName наименование ключа, указывающего на обрабатываемую дату
     * (только для протоколирования)
     *
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
            } else // Date -> Double
            {
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
        }
        // возврат значения, вероятнее всего не преобразованного вовсе (например, из-за исключения)
        return dateValue;
    }

    /**
     * Рекурсивно вызывает {@link #parseAnyDate} на всех значениях, имя ключа
     * которых имеет вид "*DATE"
     *
     * @param data обрабатываемая карта (может содержать списки и вложенные
     * карты)
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
     * @param data обрабатываемая карта (может содержать списки и вложенные
     * карты)
     * @param targetClass целевой тип преобразования
     * @param dataNodePath имя обрабатываемого узла - ключ, номер элемента и
     * т.п. (только для протоколирования)
     * @param dateSuffix суффикс для анализа даты
     * @param timeSuffix суффикс для анализа времени
     * @param addParamWithoutSuffix добавлять в мапу с датой параметр без
     * суффикса даты
     * @param analyzeNoSuffixParamAndAddSuffix анализировать дату как
     * java.util.Date без суффикса, и добавлять в мапу с суффиксом
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
                        String[] dateTime = getStringParam(newValue).split(" ");
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
                        String[] dateTime = getStringParam(newValue).split(" ");
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

    /*protected void parseDateFromMap(Map<String, Object> contrMap) {
        Map<String, Object> addValues = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : contrMap.entrySet()) {
            if (entry.getKey().endsWith(KEY_NAME_DATE_SUFFIX) || entry.getKey().endsWith(KEY_NAME_DATE_SUFFIX_NEW)) {
                // попытка парсить дату
                DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                DateFormat df2 = new SimpleDateFormat("ddMMyyyy");
                if (entry.getValue() != null) {
                    String date = entry.getValue().toString();
                    boolean fParsed = false;
                    try {
                        entry.setValue(df1.parse(date));
                        fParsed = true;
                    } catch (ParseException ex) {
                        try {
                            entry.setValue(df2.parse(date));
                            fParsed = true;
                        } catch (ParseException ex1) {
                            logger.error("parse " + entry.getKey() + " error", ex1);
                        }
                    }
                    if (fParsed && entry.getKey().endsWith(KEY_NAME_DATE_SUFFIX_NEW)) {
                        addValues.put(entry.getKey().substring(0, entry.getKey().length() - KEY_NAME_DATE_SUFFIX_NEW.length()),
                                entry.getValue());
                    }
                }
            } else {
                Object entryValue = entry.getValue();
                if (entryValue != null) {
                    if (entryValue instanceof Map) {
                        // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                        parseDateFromMap((Map<String, Object>) entryValue);
                    }
                    if (entryValue instanceof List) {
                        // например застрахованные
                        List<Object> entryAsList = (List) entryValue;
                        for (Object entryMap : entryAsList) {
                            // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                            parseDateFromMap((Map<String, Object>) entryMap);
                        }
                    }
                }
            }
        }
        contrMap.putAll(addValues);
    }*/
    protected void formatDateFromMap(Map<String, Object> contrMap) {
        Map<String, Object> addValues = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : contrMap.entrySet()) {
            if (entry.getKey().endsWith(KEY_NAME_DATE_SUFFIX) || entry.getKey().endsWith(KEY_NAME_DATE_SUFFIX_NEW)) {
                // попытка парсить дату
                DateFormat df1 = new SimpleDateFormat("dd.MM.yyyy");
                if (entry.getValue() != null) {
                    try {
                        Date date = (Date) entry.getValue();
                        entry.setValue(df1.format(date));
                        if (entry.getKey().endsWith(KEY_NAME_DATE_SUFFIX_NEW)) {
                            addValues.put(entry.getKey().substring(0, entry.getKey().length() - KEY_NAME_DATE_SUFFIX_NEW.length()),
                                    entry.getValue());
                        }
                    } catch (Exception ex1) {
                        logger.error("parse " + entry.getKey() + " error", ex1);
                    }
                }
            } else {
                Object entryValue = entry.getValue();
                if (entryValue != null) {
                    if (entryValue instanceof Map) {
                        // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                        formatDateFromMap((Map<String, Object>) entryValue);
                    }
                    if (entryValue instanceof List) {
                        // например застрахованные
                        List<Object> entryAsList = (List) entryValue;
                        for (Object entryMap : entryAsList) {
                            // если начали искать соответствие иерархии, то поиск в подмапах прекращаем
                            formatDateFromMap((Map<String, Object>) entryMap);
                        }
                    }
                }
            }
        }
        contrMap.putAll(addValues);
    }

    //<editor-fold defaultstate="collapsed" desc="из ProductContractCustomFacade / B2BCustomFacade">
    protected Long getLongParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Long.valueOf(bean.toString());
        } else {
            return null;
        }
    }

    protected String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else {
            return bean.toString();
        }
    }

    protected Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }

    protected Double getDoubleParam(Map<String, Object> map, String keyName) {
        Double doubleParam = 0.0;
        if (map != null) {
            doubleParam = getDoubleParam(map.get(keyName));
        }
        return doubleParam;
    }

    protected Double getDoubleAmountParam(Object bean) {
        String sValue = bean.toString();
        if ((sValue != null) && (!sValue.trim().isEmpty())) {
            return getDoubleParam(sValue.trim().replace(" ", "").replace(",", "."));
        } else {
            return null;
        }
    }
    protected Double getDoubleAmountParam(Map<String, Object> map, String keyName) {
        String sValue = getStringParam(map, keyName);
        if ((sValue != null) && (!sValue.trim().isEmpty())) {
            return getDoubleParam(sValue.trim().replace(" ", "").replace(",", "."));
        } else {
            return null;
        }
    }

    protected Date getDateParam(Object date) {
        if (date != null) {
            if (date instanceof Double) {
                return XMLUtil.convertDate((Double) date);
            } else if (date instanceof BigDecimal) {
                return XMLUtil.convertDate((BigDecimal) date);
            } else if (date instanceof String) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                Date res = null;
                try {
                    res = sdf.parse(date.toString());
                } catch (ParseException e) {
                }
                return res;
            }
            return (Date) date;
        } else {
            return null;
        }
    }

    protected boolean getBooleanParam(Object bean, Boolean defVal) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Boolean.valueOf(bean.toString()).booleanValue();
        } else {
            return defVal;
        }
    }

    /**
     * Вызывает обычную версию selectQuery, передавая в качестве имени второго
     * "количественного" запроса имя фактического запроса прибавив к нему
     * "Count".
     *
     * @param queryName имя фактического запроса
     * @param selectParams параметры выполнения запроса
     *
     * @return результат, аналогичный получаемому при вызове обычной версии
     * selectQuery
     *
     * @throws Exception
     */
    protected Map<String, Object> selectQuery(String queryName, Map<String, Object> selectParams) throws Exception {
        return this.selectQuery(queryName, queryName + "Count", selectParams);
    }

    protected void setOverridedParam(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        if (isLogged) {
            Object oldValue = paramParent.get(paramName);
            logParamOverriding(paramName, newValue, oldValue);
        }
        paramParent.put(paramName, newValue);
    }

    protected void setGeneratedParam(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        paramParent.put(paramName, newValue);
        if (isLogged) {
            logParamGeneration(paramName, newValue);
        }
    }

    protected void setGeneratedParamIfNull(Map<String, Object> paramParent, String paramName, Object newValue, boolean isLogged) {
        if (paramParent.get(paramName) == null) {
            setGeneratedParam(paramParent, paramName, newValue, isLogged);
        }
    }

    protected void logParamGeneration(String paramName, Object newValue) {
        StringBuilder logStr = new StringBuilder();
        logStr.append("Значение параметра '");
        logStr.append(paramName);
        logStr.append("' не найдено во входных данных. Для указанного параметра сгенерировано новое значение: ");
        logStr.append(newValue);
        if (newValue != null) {
            logStr.append(" (");
            logStr.append(newValue.getClass().getSimpleName());
            logStr.append(").");
        } else {
            logStr.append(".");
        }
        logger.debug(logStr.toString());
    }

    protected void logParamOverriding(String paramName, Object newValue, Object oldValue) {
        StringBuilder logStr = new StringBuilder();
        logStr.append("Значение параметра '");
        logStr.append(paramName);
        logStr.append("' из входных данных (");
        logStr.append(oldValue);
        logStr.append(") проигонорировано. Для указанного параметра сгенерировано новое значение: ");
        logStr.append(newValue);
        if (newValue != null) {
            logStr.append(" (");
            logStr.append(newValue.getClass().getSimpleName());
            logStr.append(")");
        }
        logStr.append(".");
        logger.debug(logStr.toString());
    }

    protected Map<String, Object> recordMakeTrans(Map<String, Object> record, String toStateSysName, String idFieldName, String methodNamePrefix, String typeSysName, String login, String password) throws Exception {
        logger.debug("State transition started...");
        Map<String, Object> result = null;
        logger.debug("    Record type: " + typeSysName);
        Long recordID = getLongParam(record.get(idFieldName));
        logger.debug("    Record ID: " + recordID);
        String fromStateSysName = getStringParam(record.get("STATESYSNAME"));
        logger.debug("    Initial state: " + fromStateSysName);
        logger.debug("    Destination state: " + toStateSysName);
        if ((!fromStateSysName.isEmpty()) && (recordID != null)) {
            Map<String, Object> getTransParams = new HashMap<String, Object>();
            getTransParams.put("TYPESYSNAME", typeSysName);
            getTransParams.put("FROMSTATESYSNAME", fromStateSysName);
            getTransParams.put("TOSTATESYSNAME", toStateSysName);
            getTransParams.put("JOIN_TO_SMTYPE", true);
            getTransParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> qTransRes = this.callService(Constants.INSPOSWS, "dsTransitionsBrowseByParamEx", getTransParams, login, password);
            if (qTransRes.get("SYSNAME") != null) {
                String transSysName = getStringParam(qTransRes.get("SYSNAME"));
                if (!transSysName.isEmpty()) {
                    logger.debug("    Selected transition: " + transSysName);
                    Map<String, Object> makeTransParams = new HashMap<String, Object>();
                    makeTransParams.put(idFieldName, recordID);
                    makeTransParams.put("DOCUMENTID", recordID);
                    makeTransParams.put("TYPESYSNAME", typeSysName);
                    makeTransParams.put("TRANSITIONSYSNAME", transSysName);
                    makeTransParams.put(RETURN_AS_HASH_MAP, true);
                    // для поддержки отката транзакций вызов метода должен выполнятся внутри сервиса
                    result = this.callService(/*Constants.B2BPOSWS*/THIS_SERVICE_NAME, methodNamePrefix + "_State_MakeTrans", makeTransParams, login, password);
                } else {
                    logger.debug("     No transition selected by this transition params.");
                }
            }
        }
        logger.debug("State transition finished with result: " + result);
        return result;
    }

    protected Object callServiceAndGetOneValue(String serviceName, String methodName, Map<String, Object> params, String login, String password, String keyName) throws Exception {
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> rawResult = this.callService(serviceName, methodName, params, login, password);
        if (rawResult != null) {
            Object oneValue = rawResult.get(keyName);
            if (oneValue != null) {
                return oneValue;
            }
        }
        // todo: протоколирование неудачи при получении значения по переданному ключу
        return null;
    }

    protected String getUserUploadFilePath() {
        String result = Config.getConfig("webclient").getParam("userFilePath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();
        return result;
    }

    protected static boolean isCallResultOK(Map<String, Object> callResult) {
        return (callResult != null) && (callResult.get("Status") != null) && ("OK".equalsIgnoreCase(callResult.get("Status").toString()));
    }

    protected static double roundSum(Double sum) {
        return ((new BigDecimal(sum.toString())).setScale(2, RoundingMode.HALF_UP)).doubleValue();
    }
    //</editor-fold>    

    //<editor-fold defaultstate="collapsed" desc="созданы в Mort900BaseFacade">
    protected Long getLongParam(Map<String, Object> map, String keyName) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParam(map.get(keyName));
        }
        return longParam;
    }

    protected String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }

    protected Integer getIntegerParam(Object bean, Integer defaultValue) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Integer.valueOf(bean.toString());
        } else {
            return defaultValue;
        }
    }

    protected Integer getIntegerParam(Object bean) {
        return getIntegerParam(bean, 0);
    }

    protected Integer getIntegerParam(Map<String, Object> map, String keyName, Integer defaultValue) {
        Integer integerParam = defaultValue;
        if (map != null) {
            integerParam = getIntegerParam(map.get(keyName), defaultValue);
        }
        return integerParam;
    }

    protected Integer getIntegerParam(Map<String, Object> map, String keyName) {
        return getIntegerParam(map, keyName, 0);
    }

    protected boolean getBooleanParam(Map<String, Object> map, String keyName, Boolean defaultValue) {
        Boolean booleanParam = defaultValue;
        if (map != null) {
            booleanParam = getBooleanParam(map.get(keyName), defaultValue);
        }
        return booleanParam;
    }

    protected Date getDateParam(Map<String, Object> map, String keyName) {
        Date dateParam = null;
        if (map != null) {
            dateParam = getDateParam(map.get(keyName));
        }
        return dateParam;
    }

    // аналог getLongParam, но с протоколировнием полученного значения
    protected Long getLongParamLogged(Map<String, Object> map, String keyName) {
        Long paramValue = getLongParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    // аналог getStringParam, но с протоколировнием полученного значения
    protected String getStringParamLogged(Map<String, Object> map, String keyName) {
        String paramValue = getStringParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    // аналог getDoubleParam, но с протоколировнием полученного значения
    protected Double getDoubleParamLogged(Map<String, Object> map, String keyName) {
        Double paramValue = getDoubleParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    // аналог getDoubleParam с протоколировнием полученного значения, но с доп. описанием значения
    protected Double getDoubleParamLogged(Map<String, Object> map, String keyName, String keyNote) {
        Double paramValue = getDoubleParam(map, keyName);
        logger.debug(String.format("%s (%s) = ", keyNote, keyName) + paramValue);
        return paramValue;
    }

    // аналог getDateParam, но с протоколировнием полученного значения
    protected Date getDateParamLogged(Map<String, Object> map, String keyName) {
        Date paramValue = getDateParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="общие методы для использования в dsB2B*TMContractPrepareToSave и др.">
    // расширенные атрибуты договора - получение из договора или создание пустой мапы (если не найдены)
    protected Map<String, Object> getOrCreateContractExtMap(Map<String, Object> contract) {
        Object contractExt = contract.get("CONTREXTMAP");
        Map<String, Object> contractExtMap;
        if (contractExt != null) {
            contractExtMap = (Map<String, Object>) contractExt;
        } else {
            contractExtMap = new HashMap<String, Object>();
            contract.put("CONTREXTMAP", contractExtMap);
        }
        return contractExtMap;
    }

    // получение сведений о продукте - из мапы договора (если уже загружены) или загрузка из БД
    protected Map<String, Object> getOrLoadProduct(Map<String, Object> contract, String login, String password) throws Exception {
        Object productObj = contract.get(PRODUCTMAP_KEYNAME);
        Map<String, Object> product = null;
        if (productObj != null) {
            // получение сведений о продукте из мапы договора
            product = (Map<String, Object>) productObj;
        } else {
            // получение сведений о продукте (по идентификатору конфигурации продукта) из БД
            Long prodConfID = getLongParam(contract, "PRODCONFID");
            if (prodConfID == null) {
                // определение идентификатора конфига продукта
                Long prodVerID = getLongParam(contract, "PRODVERID");
                if (prodVerID == null) {
                    // определение идентификатора версии продукта по системному имени
                    Map<String, Object> getVerConfIDParams = new HashMap<String, Object>();
                    getVerConfIDParams.put("PRODSYSNAME", getStringParam(contract, "PRODSYSNAME"));
                    prodVerID = (Long) this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductVersionBrowseListByParamEx", getVerConfIDParams, login, password, "PRODVERID");
                }
                Map<String, Object> getProdConfIDParams = new HashMap<String, Object>();
                getProdConfIDParams.put("PRODVERID", prodVerID);
                prodConfID = (Long) this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", getProdConfIDParams, login, password, "PRODCONFID");
            }
            if (prodConfID != null) {
                Map<String, Object> productParams = new HashMap<String, Object>();
                productParams.put("PRODCONFID", prodConfID);
                productParams.put("HIERARCHY", false);
                productParams.put("LOADALLDATA", 1L);
                productParams.put(RETURN_AS_HASH_MAP, true);
                product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);
                contract.put(PRODUCTMAP_KEYNAME, product);
            }
        }
        return product;
    }

    // безусловное обновление в мапе договора сведений о программе и страховой суммы по значению премии
    protected Map<String, Object> updateSumsAndProgramByPaymentValue(Map<String, Object> contract, boolean isVerboseLogging, String login, String password) throws Exception {

        logger.debug("Updating insurance amount value, premium value and program info by bank cash flow payment value...");

        // расширенные атрибуты договора
        Map<String, Object> contractExtMap = getOrCreateContractExtMap(contract);

        // получение сведений о продукте
        Map<String, Object> product = getOrLoadProduct(contract, login, password);

        // определение программы и страховой суммы по значению премии
        Double paymentValue = getDoubleParamLogged(contract, "BANKCASHFLOWAMVALUE", "Bank cash flow payment value");
        Map<String, Object> prodVerMap = (Map<String, Object>) product.get("PRODVER");
        if (prodVerMap != null) {
            List<Map<String, Object>> productProgramsList = (List<Map<String, Object>>) prodVerMap.get("PRODPROGS");
            if (productProgramsList != null) {
                logger.debug(String.format("Found %d programs for this product.", productProgramsList.size()));
                for (Map<String, Object> program : productProgramsList) {
                    Double prodProgPremValue = getDoubleParam(program, "PREMVALUE");
                    logger.debug(String.format("Checking program with premium value %f...", prodProgPremValue));
                    Double absDelta = Math.abs(paymentValue - prodProgPremValue);
                    if (absDelta < 0.01) {
                        logger.debug("Required program found: " + program);
                        // идентификатор программы - для договора (B2B_CONTR.PRODPROGID)
                        Long prodProgId = getLongParam(program, "PRODPROGID");
                        setOverridedParam(contract, "PRODPROGID", prodProgId, isVerboseLogging);
                        // код программы - для расширенных атрибутов договора (B2B_CONTR.CONTREXTMAP.insuranceProgram)
                        String prodProgCode = getStringParam(program, "PROGCODE");
                        setOverridedParam(contractExtMap, "insuranceProgram", prodProgCode, isVerboseLogging);
                        // страховая сумма - для договора (B2B_CONTR.INSAMVALUE)
                        Double insAmValue = roundSum(getDoubleParam(program, "INSAMVALUE"));
                        setOverridedParam(contract, "INSAMVALUE", insAmValue, isVerboseLogging);
                        logger.debug("Updating insurance amount value, premium value and program info by bank cash flow payment value finished successfully.");

                        // http://rybinsk.bivgroup.com:9090/issues/2676#note-9
                        // "... по новому условию - Страховая премия соответствует «Сумма» из движения средств * 8"
                        logger.debug("Updating premium value...");
                        logger.debug("Real contract full premium value is x8 (premium value from bank cash flow processor is really sum of one payment).");
                        Double realPremValue = roundSum(paymentValue * 8);
                        setOverridedParam(contract, "PREMVALUE", realPremValue, isVerboseLogging);
                        logger.debug("Updating premium value finished successfully.");

                        return contract;
                    }
                }
                logger.debug(String.format("Found %d programs for this product.", productProgramsList.size()));
            }
        }

        contract.put("Error", String.format("не удалось определить программу и/или величину страховой суммы по переданному значению платежа (%.2f)", paymentValue));
        logger.error("Updating insurance amount value, premium value and program info by bank cash flow payment value failed!");
        return contract;
    }

    // получение мапы плановых платежей по данным из продукта (ключ - порядковый номер планового платежа (NUM), значение - сведения о плановом платеже)
    protected Map<Long, Map<String, Object>> getPayPlanMapFromProduct(Map<String, Object> product) {
        Map<Long, Map<String, Object>> payPlanMap = null;
        Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
        if (prodVer != null) {
            List<Map<String, Object>> prodPayVarList = (List<Map<String, Object>>) prodVer.get("PRODPAYVARS");
            if ((prodPayVarList != null) && (prodPayVarList.size() == 1)) {
                Map<String, Object> prodPayVar = prodPayVarList.get(0);
                if (prodPayVar != null) {
                    Map<String, Object> payVar = (Map<String, Object>) prodPayVar.get("PAYVAR");
                    if (payVar != null) {
                        List<Map<String, Object>> payVarCnt = (List<Map<String, Object>>) payVar.get("PAYVARCNTS");
                        if ((payVarCnt != null) && (payVarCnt.size() > 1)) {
                            CopyUtils.sortByLongFieldName(payVarCnt, "NUM");
                            payPlanMap = new HashMap<Long, Map<String, Object>>();
                            for (Map<String, Object> planPayment : payVarCnt) {
                                Long num = getLongParam(planPayment, "NUM");
                                payPlanMap.put(num, planPayment);
                            }
                        }
                    }
                }
            }
        }
        return payPlanMap;
    }

    // формирование структуры секция/тип/объект/риск с длительностью соответствующей одному (первому) оплаченному периоду
    protected Map<String, Object> createContractFirstSection(Map<String, Object> contract, String login, String password) throws Exception {
        Map<Long, Map<String, Object>> payPlanMap = getPayPlanMapFromContract(contract, login, password);
        // проверка полученной мапы плановых платежей
        if (payPlanMap == null) {
            // не удалось получить корректный график плановых платежей из данных о продукте
            String errorText = "Не удалось получить график плановых платежей из данных о продукте. ";
            errorText = errorText + "Сведения договора не сохранены.";
            contract.put("Status", "Error");
            contract.put("Error", errorText);
        } else {
            // получина корректная мапа плановых платежей - будет выполнена подмена в договоре действительных дат датами из первого периода плана платежей (для формирования структуры секция/тип/объект/риск с длительностью соответствующей одному оплаченному периоду)
            // сохранение действительных дат по договору и суммы премии в отдельную временную мапу
            Map<String, Object> contractRealDates = new HashMap<String, Object>();
            String[] realDatesKeys = {"STARTDATE", "FINISHDATE", "DURATION", "PREMVALUE"};
            for (String realDatesKey : realDatesKeys) {
                contractRealDates.put(realDatesKey, contract.get(realDatesKey));
            }
            // дата начала действия первой секции совпадает с датой начала действия договора
            Date contractStartDate = getDateParam(contract.get("STARTDATE"));
            Date sectionStartDate = contractStartDate;
            // дата окончания действия первой секции соответствует дате второго планового платежа
            // вычисление даты второго планового платежа - по сдвигу в месяцах из плана платежей
            Map<String, Object> secondPlanPayment = payPlanMap.get(2L);
            Long secondPlanPaymentDateShiftMonths = getLongParam(secondPlanPayment, "DATESHIFT");
            GregorianCalendar sectionFinishDateGC = new GregorianCalendar();
            sectionFinishDateGC.setTime(contractStartDate);
            sectionFinishDateGC.add(Calendar.MONTH, secondPlanPaymentDateShiftMonths.intValue());
            sectionFinishDateGC.add(Calendar.DATE, -1);
            sectionFinishDateGC.set(Calendar.HOUR_OF_DAY, 23);
            sectionFinishDateGC.set(Calendar.MINUTE, 59);
            sectionFinishDateGC.set(Calendar.SECOND, 59);
            sectionFinishDateGC.set(Calendar.MILLISECOND, 0);
            Date sectionFinishDate = sectionFinishDateGC.getTime();
            // подмена в договоре действительных дат датами из первого периода плана платежей (для формирования структуры секция/тип/объект/риск с длительностью соответствующей одному оплаченному периоду)
            contract.put("STARTDATE", sectionStartDate);
            contract.put("FINISHDATE", sectionFinishDate);
            // подмена в договоре действительной премии суммой платежа (для формирования структуры секция/тип/объект/риск с правильной суммой)
            contract.put("PREMVALUE", roundSum(getDoubleParam(contract, "BANKCASHFLOWAMVALUE")));
            // формирование структуры секция/тип/объект/риск по сведениям из продукта для включения в сохраняемый договор
            Map<String, Object> updateContrInsProdStructParams = new HashMap<String, Object>();
            updateContrInsProdStructParams.put("CONTRMAP", contract);
            updateContrInsProdStructParams.put("ISMISSINGSTRUCTSCREATED", true);
            updateContrInsProdStructParams.put("ISCREATESECTIONS", true);
            updateContrInsProdStructParams.put(RETURN_AS_HASH_MAP, true);
            contract = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BUpdateContractInsuranceProductStructure", updateContrInsProdStructParams, login, password);
            // восстановление действительных дат по договору и суммы премии из отдельной временной мапы
            contract.putAll(contractRealDates);
        }
        return contract;
    }

    //</editor-fold>
    // получение мапы плановых платежей по данным из продукта, включенного в мапу договора (ключ - порядковый номер планового платежа (NUM), значение - сведения о плановом платеже)
    // если в мапе договора отсутствуют сведения о продукте - они будут загружены из БД
    protected Map<Long, Map<String, Object>> getPayPlanMapFromContract(Map<String, Object> contract, String login, String password) throws Exception {
        // получение сведений о продукте
        Map<String, Object> product = getOrLoadProduct(contract, login, password);
        // получение мапы плановых платежей по данным из продукта (ключ - порядковый номер планового платежа (NUM), значение - сведения о плановом платеже)
        Map<Long, Map<String, Object>> payPlanMap = getPayPlanMapFromProduct(product);
        return payPlanMap;
    }
    
    protected String getCoreSettingBySysName(String settingSysName, String login, String password) throws Exception {
        logger.debug(String.format("Getting core setting by system name [%s]...", settingSysName));
        Map<String, Object> coreSettingParams = new HashMap<String, Object>();
        coreSettingParams.put("SETTINGSYSNAME", settingSysName);
        coreSettingParams.put(RETURN_AS_HASH_MAP, "TRUE"); // getSysSettingBySysName работает только со строковыми значениями RETURN_AS_HASH_MAP
        String coreSettingValue = "";
        Map<String, Object> coreSetting = this.callService(COREWS_SERVICE_NAME, "getSysSettingBySysName", coreSettingParams, login, password);
        if (coreSetting != null) {
            coreSettingValue = getStringParam(coreSetting.get("SETTINGVALUE"));
            if (coreSettingValue.isEmpty()) {
                logger.debug(String.format("Core setting with system name [%s] does not exist or contain no value.", settingSysName));
            } else {
                logger.debug(String.format("Core setting with system name [%s] contain value [%s].", settingSysName, coreSettingValue));
            }
        } else {
            logger.debug("Method getSysSettingBySysName return no result.");
        }
        return coreSettingValue;
    }
    
}
