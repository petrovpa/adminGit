/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.ws.kladr.system;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import ru.diasoft.utils.date.DSDateUtil;

public class DatesParser {

    private final Logger logger = Logger.getLogger(this.getClass());
    
    private static final String KEY_NAME_DATE_SUFFIX = "DATE"; // например, SOMEDATE
    private static final String KEY_NAME_TIME_SUFFIX = "TIME"; // прибавляется к имени ключей с датами, поэтому, например, SOMEDATETIME
    
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private final SimpleDateFormat dateTimeFormatterSlashes = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private final SimpleDateFormat dateFormatterNoDots = new SimpleDateFormat("ddMMyyyy");
    
    private boolean verboseLogging = false;
    
    private void logDateParseEx(String keyName, Object dateValue, String conversion, Exception ex) {
        logger.error("Parsing key's '" + keyName + "' value '" + dateValue + "' (using " + conversion + ") caused error:", ex);
    }

    public Object parseAnyDate(Object dateValue, Class targetClass, String keyName, Boolean logged) {
        return parseAnyDate(dateValue, null, targetClass, keyName, logged);
    }
    
    private String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else {
            return bean.toString();
        }
    }    
    
    /**
     * Аналогично {@link #parseAnyDate(Object, Object, Class, String)}, но с флагом
     * протоколирования
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
            logStr.append("' содержал дату '").append(dateValue);
            logStr.append("' ('").append(dateValue.getClass().getSimpleName()).append("')");
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
    
    private Date strToDate(Object date, String keyName) {
        Date dateValue = null;
        try {
            dateValue = dateFormatter.parse(date.toString());
        } catch (ParseException ex1) {
            try {
                dateValue = dateFormatterNoDots.parse(date.toString());
            } catch (ParseException ex2) {
                logDateParseEx(keyName, date, "format " + dateFormatter.toPattern(), ex1);
                logDateParseEx(keyName, date, "format " + dateFormatterNoDots.toPattern(), ex2);
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
            try {
                dateValue = dateTimeFormatterSlashes.parse(dateTimeStr);
            } catch (ParseException ex2) {
                logDateParseEx(keyName, date, "format " + dateTimeFormatter.toPattern(), ex1);
                logDateParseEx(keyName, date, "format " + dateTimeFormatterSlashes.toPattern(), ex2);
            }
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
                Date dateValue1 = strToDate(betweenStr[0], keyName);
                Date dateValue2 = strToDate(betweenStr[1], keyName);
                if (Double.class.equals(targetClass)) {
                    dateValue = String.valueOf(DSDateUtil.convertDate(dateValue1).doubleValue()) + " AND "
                                + String.valueOf(DSDateUtil.convertDate(dateValue2).doubleValue());
                }
                if (Date.class.equals(targetClass)) {
                    dateValue = dateValue1.toString() + " AND " + dateValue2.toString();
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
            } else {
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
        }
        // возврат значения, вероятнее всего не преобразованного вовсе (например, из-за исключения)
        return dateValue;
    }

    /**
     * Рекурсивно вызывает {@link #parseAnyDate} на всех значениях, имя ключа
     * которых имеет вид "*DATE"
     *
     * @param data обрабатываемая мапа (может содержать списки и вложенные
     * мапы)
     * @param targetClass целевой тип преобразования
     */
    public void parseDates(Map<String, Object> data, Class targetClass) {
        logger.debug("Начато преобразование дат в '" + targetClass.getSimpleName() + "'...");
        parseDates(data, targetClass, "*");
        logger.debug("Преобразование дат в '" + targetClass.getSimpleName() + "' завершено.");
    }

    /**
     * Рекурсивно вызывает {@link #parseAnyDate} на всех значениях, имя ключа
     * которых имеет вид "*DATE"
     *
     * @param data обрабатываемая мапа (может содержать списки и вложенные
     * мапы)
     * @param targetClass целевой тип преобразования
     * @param dataNodePath имя обрабатываемого узла - ключ, номер элемента и
     * т.п. (только для протоколирования)
     */
    private void parseDates(Map<String, Object> data, Class targetClass, String dataNodePath) {        
        boolean isTargetClassString = String.class.equals(targetClass);
        Map<String, Object> additionalTimeEntries = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String keyName = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                String keyFullName = dataNodePath + "." + keyName;
                if (value instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) value;
                    parseDates(map, targetClass, keyFullName);
                } else if (value instanceof List) {
                    ArrayList<Object> list = (ArrayList<Object>) value;
                    for (int i = 0; i < list.size(); i++) {
                        Object rawElement = list.get(i);
                        if (rawElement instanceof Map) {
                            Map<String, Object> element = (Map<String, Object>) rawElement;
                            parseDates(element, targetClass, keyFullName + "[" + i + "]");
                        }
                    }
                } else if (keyName.endsWith(KEY_NAME_DATE_SUFFIX)) {
                    
                    //String timeKeyName = keyName.substring(0, keyName.length() - 4) + KEY_NAME_TIME_SUFFIX;
                    String timeKeyName = keyName + KEY_NAME_TIME_SUFFIX;
                    Object timeValue = data.get(timeKeyName);
                    
                    //Object newValue = parseAnyDate(value, timeValue, targetClass, keyFullName, true); // с протоколированием, для проверки работы с датами
                    //Object newValue = parseAnyDate(value, timeValue , targetClass, keyFullName); // без протоколирования
                    Object newValue = parseAnyDate(value, timeValue , targetClass, keyFullName, isVerboseLogging()); // без протоколирования
                    
                    if ((isTargetClassString) && (newValue instanceof String)) {
                        String[] dateTime = getStringParam(newValue).split(" ");
                        if (dateTime.length == 2) {
                            newValue = dateTime[0];
                            additionalTimeEntries.put(timeKeyName, dateTime[1]);
                        } else {
                            logger.debug("Не удалось выделить время из строкового представляения даты '" + newValue + "', содержащегося в атрибуте '" + keyFullName + "'.");
                        }
                    }
                    
                    entry.setValue(newValue);
                    
                }
            }
        }
        data.putAll(additionalTimeEntries);
    }

    /**
     * @return the isVerboseLoggingEnabled
     */
    public boolean isVerboseLogging() {
        return verboseLogging;
    }

    /**
     * @param verboseLogging the isVerboseLoggingEnabled to set
     */
    public void setVerboseLogging(boolean verboseLogging) {
        this.verboseLogging = verboseLogging;
    }
    
}
