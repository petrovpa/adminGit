/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.basefacade;

import com.bivgroup.utils.ParamGetter;
import static com.bivgroup.xmlutil.XmlUtil.convertDate;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author mmamaev
 */
public class DatesParser {

    protected static final String KEY_NAME_DATE_SUFFIX = "DATE"; // например, SOMEDATE
    protected static final String KEY_NAME_TIME_SUFFIX = "TIME"; // прибавляется к имени ключей с датами, поэтому, например, SOMEDATETIME
    protected static final String KEY_NAME_DATE_SUFFIX_NEW = "$date"; // например, SOME$date
    protected static final String KEY_NAME_TIME_SUFFIX_NEW = "time"; // прибавляется к имени ключей с датами, поэтому, например, SOME$datetime

    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat dateFormatterNoDots = new SimpleDateFormat("ddMMyyyy");

    private static final String DATE_TIME_FORMATTER_PATTERN = "dd.MM.yyyy HH:mm";
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(DATE_TIME_FORMATTER_PATTERN);

    private static final String DATE_TIME_TZ_FORMATTER_PATTERN = DATE_TIME_FORMATTER_PATTERN + " z";
    private final SimpleDateFormat dateTimeFormatterTZ = new SimpleDateFormat(DATE_TIME_TZ_FORMATTER_PATTERN);

    private static final String DATE_TIME_FORMATTER_DEFAULT_TZ_STR = " GMT+03:00";

    private String getStringParam(Object newValue) {
        return ParamGetter.getStringParam(newValue);
    }

    private Logger logger = Logger.getLogger(this.getClass());

    private void logDateParseEx(String keyName, Object dateValue, String conversion, Exception ex) {
        logger.error("Parsing key's '" + keyName + "' value '" + dateValue + "' (using " + conversion + ") caused error:", ex);
    }

    public Object parseAnyDate(Object dateValue, Class targetClass, String keyName, Boolean logged) {
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
    public Object parseAnyDate(Object dateValue, Object timeValue, Class targetClass, String keyName, Boolean logged) {
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
    public Object parseAnyDate(Object dateValue, Class targetClass, String keyName) {
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
    public Object parseAnyDate(Object dateValue, Object timeValue, Class targetClass, String keyName) {

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
                        dateValue = String.valueOf(convertDate(dateValue1).doubleValue()) + " AND "
                                + String.valueOf(convertDate(dateValue2).doubleValue());
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
                dateValue = convertDate(dateDouble);
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
            if (Double.class.equals(targetClass)) {
                // попытка преобразовать дату в число для БД
                try {
                    Date dateDate = (Date) dateValue;
                    dateValue = convertDate(dateDate);
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

    public void parseDates(Map<String, Object> data, Class targetClass, String dateSuffix, String timeSuffix,
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
     * @param addParamWithoutSuffix добавлять в мапу с датой параметр без суффикса даты
     * @param analyzeNoSuffixParamAndAddSuffix анализировать дату как java.util.Date без суффикса, и добавлять в мапу с суффиксом
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
                            //additionalEntries.put(timeKeyName, dateTime[1]);
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
