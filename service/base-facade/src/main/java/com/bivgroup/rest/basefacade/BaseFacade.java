/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.basefacade;


import com.bivgroup.utils.ParamGetter;
import com.bivgroup.xmlutil.XmlUtil;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * @author mmamaev
 */
public class BaseFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    private final DatesParser datesParser = new DatesParser();

    public BaseFacade() {
        super();
    }

    protected Object parseAnyDate(Object dateValue, Class targetClass, String keyName, Boolean logged) {
        return datesParser.parseAnyDate(dateValue, targetClass, keyName, logged);
    }

   
   
    public static Date getDateParam(Object date) {
        if (date != null) {
            if (date instanceof Double) {
                return XmlUtil.convertDate((Double) date);
            } else if (date instanceof BigDecimal) {
                return XmlUtil.convertDate((BigDecimal) date);
            } else if (date instanceof String) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                try {
                    return sdf.parse(date.toString());
                } catch (ParseException ex) {
                    sdf = new SimpleDateFormat("yyyy.MM.dd");
                    try {
                        return sdf.parse(date.toString());
                    } catch (ParseException ex1) {
                        java.util.logging.Logger.getLogger(BaseFacade.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            return (Date) date;
        } else {
            return null;
        }
    }

    protected void parseDates(Map<String, Object> data, Class targetClass, String dateSuffix, String timeSuffix, boolean addParamWithoutSuffix, boolean analyzeNoSuffixParamAndAddSuffix) {
        datesParser.parseDates(data, targetClass, dateSuffix, timeSuffix, addParamWithoutSuffix, analyzeNoSuffixParamAndAddSuffix);
    }

    public static Long getLongParam(Map<String, Object> map, String keyName) {
        return ParamGetter.getLongParam(map, keyName);
    }

    // формирование из списка специальной мапы (ключ - значение указанного поля, значение - соответсвующий элемент списка)
    // (например, для формирования мап по сис. наименованиям и т.п.)
    protected Map<String, Map<String, Object>> getMapByFieldStringValues(List<Map<String, Object>> list, String fieldName) {
        Map<String, Map<String, Object>> mapByFieldStringValues = new HashMap<String, Map<String, Object>>();
        if ((list != null) && (fieldName != null) && (list.size() > 0) && (!fieldName.isEmpty())) {
            for (Map<String, Object> bean : list) {
                String fieldValue = ParamGetter.getStringParam(bean, fieldName);
                mapByFieldStringValues.put(fieldValue, bean);
            }
        }
        return mapByFieldStringValues;
    }

    // аналог getMapByFieldStringValues, но для типа Long
    // (например, для формирования мап по ИД и т.п.)
    protected Map<Long, Map<String, Object>> getMapByFieldLongValues(List<Map<String, Object>> list, String fieldName) {
        Map<Long, Map<String, Object>> mapByFieldLongValues = new HashMap<Long, Map<String, Object>>();
        if ((list != null) && (fieldName != null) && (list.size() > 0) && (!fieldName.isEmpty())) {
            for (Map<String, Object> bean : list) {
                Long fieldValue = getLongParam(bean, fieldName);
                mapByFieldLongValues.put(fieldValue, bean);
            }
        }
        return mapByFieldLongValues;
    }


    protected void copyParamsIfNotNull(Map<String, Object> targetMap, Map<String, Object> sourceMap, String... paramKeyNames) {
        if ((targetMap != null) && (sourceMap != null)) {
            for (String paramKeyName : paramKeyNames) {
                Object paramValue = sourceMap.get(paramKeyName);
                if (paramValue != null) {
                    targetMap.put(paramKeyName, paramValue);
                }
            }
        }
    }

    protected static boolean isCallResultOKAndContains(Map<String, Object> callResult, String keyName) {
        return (ParamGetter.isCallResultOK(callResult)) && (callResult.get(keyName) != null);
    }

    protected boolean isCallResultOKAndContainsLongValue(Map<String, Object> callResult, String keyName, Long keyValue) {
        boolean result = false;
        if (ParamGetter.isCallResultOK(callResult)) {
            Long longValue = getLongParam(callResult, keyName);
            if ((keyValue != null) && (keyValue.equals(longValue))) {
                result = true;
            }
        }
        return result;
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

    // копия из CopyUtils
    protected static void sortByLongFieldName(List<Map<String, Object>> list, final String fieldName) {
        Collections.sort(list, new Comparator<Map<String, Object>>() {
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                int result = -1;
                Long l1 = (Long) o1.get(fieldName);
                Long l2 = (Long) o2.get(fieldName);
                if (null != l1 && null != l2) {
                    result = l1.compareTo(l2);
                }
                return result;
            }
        });
    }
}
