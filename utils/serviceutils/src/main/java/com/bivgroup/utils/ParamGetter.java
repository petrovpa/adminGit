package com.bivgroup.utils;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.*;

public final class ParamGetter {
    private static final String RET_STATUS = "Status";
    private static final String RET_STATUS_OK = "OK";
    private static final String EMPTY_STRING = "";
    private static final String EQUALLY_STRING_FOR_LOGGING = " = ";

    private static Logger logger = Logger.getLogger(ParamGetter.class);

    /**
     * Приватный конструктор утильного класса, т.к. не должно
     * быть возможности создать экземпляр утильного класса
     */
    private ParamGetter() {

    }

    public static String getStringParam(Object bean) {
        if (bean == null) {
            return EMPTY_STRING;
        } else if (bean instanceof Double) {
            DecimalFormat df = new DecimalFormat("0");
            df.setMaximumFractionDigits(2);
            return df.format(Double.parseDouble(bean.toString()));
        } else {
            return bean.toString();
        }
    }

    public static String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = EMPTY_STRING;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }

    public static String removeStringParam(Map<String, Object> map, String keyName) {
        String stringParam = EMPTY_STRING;
        if (map != null) {
            stringParam = getStringParam(map.remove(keyName));
        }
        return stringParam;
    }

    // аналог getStringParam, но с протоколировнием полученного значения
    public static String getStringParamLogged(Map<String, Object> map, String keyName) {
        String paramValue = getStringParam(map, keyName);
        logger.debug(keyName + EQUALLY_STRING_FOR_LOGGING + paramValue);
        return paramValue;
    }

    // аналог getStringParam, но с расширенным протоколировнием полученного значения
    public static String getStringParamLogged(Map<String, Object> map, String keyName, String commentName) {
        String paramValue = getStringParam(map, keyName);
        if (logger.isDebugEnabled()) {
            logger.debug(keyName + '(' + commentName + ')' + EQUALLY_STRING_FOR_LOGGING + paramValue);
        }
        return paramValue;
    }

    public static boolean getBooleanParam(Object bean, Boolean defVal) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Boolean.parseBoolean(bean.toString());
        } else {
            return defVal;
        }
    }

    public static boolean getBooleanParam(Map<String, Object> map, String keyName, Boolean defVal) {
        boolean booleanParam = defVal;
        if (map != null) {
            booleanParam = getBooleanParam(map.get(keyName), defVal);
        }
        return booleanParam;
    }

    public static boolean getBooleanParamLogged(Map<String, Object> map, String keyName, Boolean defVal) {
        boolean paramValue = getBooleanParam(map, keyName, defVal);
        logger.debug(keyName + EQUALLY_STRING_FOR_LOGGING + paramValue);
        return paramValue;
    }

    public static Long getLongParamWithDefaultValue(Map<String, Object> map, String keyName, Long defaultValue) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParamWithDefaultValue(map.get(keyName), defaultValue);
        }
        return longParam;
    }

    public static Long getLongParamWithDefaultValue(Object bean, Long defaultValue) {
        Long result = getLongParam(bean);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    public static Long getLongParam(Object bean) {
        Long result = null;
        if (bean != null) {
            String beanStr = bean.toString();
            result = getLongParam(beanStr);
        }
        return result;
    }

    private static Long getLongParam(String param) {
        Long result = null;
        try {
            if (!param.isEmpty()) {
                param = param.trim();
                if (!"undefined".equalsIgnoreCase(param)) {
                    result = Long.valueOf(param);
                }
            }
        } catch (Exception ex) {
            logger.error(String.format("Convert str: %s end is error: %s", param, ex.getMessage()), ex);
        }
        return result;
    }

    public static Long getLongParam(Map<String, Object> map, String keyName) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParam(map.get(keyName));
        }
        return longParam;
    }

    public static Long removeLongParam(Map<String, Object> map, String keyName) {
        Long longParam = null;
        if (map != null) {
            longParam = getLongParam(map.remove(keyName));
        }
        return longParam;
    }

    // аналог getLongParam, но с протоколировнием полученного значения
    public static Long getLongParamLogged(Map<String, Object> map, String keyName) {
        Long paramValue = getLongParam(map, keyName);
        logger.debug(keyName + EQUALLY_STRING_FOR_LOGGING + paramValue);
        return paramValue;
    }

    // аналог getLongParam, но с расширенным протоколировнием полученного значения
    public static Long getLongParamLogged(Map<String, Object> map, String keyName, String commentName) {
        Long paramValue = getLongParam(map, keyName);
        if (logger.isDebugEnabled()) {
            logger.debug(keyName + '(' + commentName + ')' + EQUALLY_STRING_FOR_LOGGING + paramValue);
        }
        return paramValue;
    }

    public static Integer getIntegerParam(Map<String, Object> map, String keyName) {
        Integer integerValue = null;
        if (map != null) {
            integerValue = getIntegerParam(map.get(keyName));
        }
        return integerValue;
    }

    public static Integer getIntegerParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Integer.valueOf(bean.toString());
        } else {
            return 0;
        }
    }

    public static Double getDoubleParam(Object bean) {
        if (bean != null) {
            return Double.valueOf(bean.toString());
        } else {
            return 0.0;
        }
    }

    public static Double getDoubleParam(Map<String, Object> map, String keyName) {
        Double doubleParam = 0.0;
        if (map != null) {
            doubleParam = getDoubleParam(map.get(keyName));
        }
        return doubleParam;
    }

    // аналог getDoubleParam, но с протоколировнием полученного значения
    public static Double getDoubleParamLogged(Map<String, Object> map, String keyName) {
        Double paramValue = getDoubleParam(map, keyName);
        logger.debug(keyName + EQUALLY_STRING_FOR_LOGGING + paramValue);
        return paramValue;
    }

    public static Map<String, Object> getFirstItemFromResultMap(Map<String, Object> data) {
        return getFirstItemFromList(getListFromResultMap(data));
    }

    public static Map<String, Object> getFirstItemFromList(List<Map<String, Object>> list) {
        return list != null && !list.isEmpty() ? list.get(0) : new HashMap<>();
    }

    public static List<Map<String, Object>> getListFromResultMap(Map<String, Object> resultMap) {
        return getListParamName(resultMap, "Result");
    }

    public static Map<String, Object> getMapParamNameCreateIfNull(Map<String, Object> map, String keyName) {
        Map<String, Object> result = getMapParamName(map, keyName);
        if (result == null) {
            result = createMapParamName(map, keyName);
        }
        return result;
    }

    public static Map<String, Object> createMapParamName(Map<String, Object> map, String keyName) {
        Map<String, Object> result = new HashMap<>();
        map.put(keyName, result);
        return result;
    }

    public static Map<String, Object> getMapParamName(Map<String, Object> map, String keyName) {
        Map<String, Object> result = null;
        if (map != null && map.containsKey(keyName)) {
            Object bean = map.get(keyName);
            if (bean instanceof Map) {
                result = (Map<String, Object>) bean;
            }
        }
        return result;
    }

    public static List<Map<String, Object>> getListParamName(Map<String, Object> resultMap, String keyName) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (resultMap != null && resultMap.containsKey(keyName)) {
            Object bean = resultMap.get(keyName);
            if (bean instanceof List) {
                result = (List<Map<String, Object>>) bean;
            }
        }
        return result;
    }

    public static Date getDateParam(Object date) {
        if (date != null) {
            if (date instanceof Double) {
                return new Date((long) ((((double) date) - new Double("25569.0")) * 8.64E7D));
            } else if (date instanceof BigDecimal) {
                BigDecimal time = (BigDecimal) date;
                return new Date(time.subtract(new BigDecimal("25569.0")).multiply(new BigDecimal(86400000)).round(new MathContext(15)).longValue());
            }
            return (Date) date;
        } else {
            return null;
        }
    }

    public static boolean isCallResultNotOK(Map<String, Object> callResult) {
        return !isCallResultOK(callResult);
    }

    public static boolean isCallResultOK(Map<String, Object> callResult) {
        return (callResult != null) && (callResult.containsKey(RET_STATUS)) && (RET_STATUS_OK.equalsIgnoreCase(getStringParam(callResult, RET_STATUS)));
    }

    public static Object getObjectParam(Map<String, Object> map, String keyName) {
        Object result = null;
        if (map != null && !map.isEmpty()) {
            result = map.get(keyName);
        }
        return result;
    }
}
