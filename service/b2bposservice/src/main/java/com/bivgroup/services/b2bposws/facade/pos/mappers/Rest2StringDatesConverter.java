package com.bivgroup.services.b2bposws.facade.pos.mappers;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rest2StringDatesConverter {

    public static final Logger logger = Logger.getLogger(Rest2StringDatesConverter.class);

    // Мастер Трупов: "входной формат дат для рест2 - yyyy-MM-dd"
    // Мастер Трупов: "это стандарт. отдельного пункта в доках на этот счёт нет."
    // Мастер Трупов: "из примеров документации по взр (дата начала и дата конца периода страхования) следует что формат именно такой."
    public static final String REST2_STR_DATE_PATTERN_STR = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$";
    //
    public static final Pattern REST2_STR_DATE_PATTERN = Pattern.compile(REST2_STR_DATE_PATTERN_STR);

    public static boolean checkDateValueByPattern(String value) {
        Boolean isMatches = false;
        if (value != null) {
            Matcher matcher = REST2_STR_DATE_PATTERN.matcher(value);
            isMatches = matcher.matches();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format(
                        "Value '%s' checked by regular expression '%s' with result '%s'.",
                        value, matcher.pattern().toString(), isMatches.toString()
                ));
            }
        }
        return isMatches;
    }

    public static void convert(Map<String, Object> map) {
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if ((key != null) && (key.toUpperCase().contains("DATE"))) {
                    if (value instanceof String) {
                        String valueStr = (String) value;
                        boolean isDateLikeStr = checkDateValueByPattern(valueStr);
                        if (isDateLikeStr) {
                            String yearStr = valueStr.substring(0, 4);
                            String monthStr = valueStr.substring(5, 7);
                            String dayStr = valueStr.substring(8, 10);
                            String convertedDateStr = dayStr + "." + monthStr + "." + yearStr;
                            if (logger.isDebugEnabled()) {
                                logger.debug(String.format(
                                        "Date-like value '%s' (referenced by key '%s') was converted to '%s'.",
                                        value, key, convertedDateStr
                                ));
                            }
                            entry.setValue(convertedDateStr);
                        }
                    } else if (value instanceof Map) {
                        Map<String, Object> subMap = null;
                        try {
                            subMap = (Map<String, Object>) value;
                        } catch (Exception ex) {
                            logger.error(String.format(
                                    "Unsupported map (referenced by key '%s') generics! Details (exception):",
                                    key
                            ), ex);
                        }
                        if (subMap != null) {
                            convert(subMap);
                        }
                    } else if (value instanceof List) {
                        logger.error(String.format(
                                "Found list value (referenced by key '%s') - lists support not implemented yet!",
                                key
                        ));
                    }
                }
            }
        }
    }

}
