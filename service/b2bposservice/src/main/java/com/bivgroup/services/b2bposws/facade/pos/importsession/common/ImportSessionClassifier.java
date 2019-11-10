package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;

import java.util.*;

public class ImportSessionClassifier extends B2BDictionaryBaseFacade {

    protected static final String LIST_ITEM_QUOTE = "'";
    protected static final String LIST_DIVIDER = ", ";
    protected static final Integer LIST_DIVIDER_LENGTH = LIST_DIVIDER.length();

    private String name;
    private List<Map<String, Object>> dataList;
    private Map<String, Map<String, Object>> dataMaps = new HashMap<>();
    private Map<String, Map<String, Long>> valueLongMaps = new HashMap<>();
    private Map<String, Map<String, String>> valueStringMaps = new HashMap<>();


    public ImportSessionClassifier(String name) throws Exception {
        this.name = name;
        this.dataList = dctFindByExample(name, null);
    }

    // todo: вынести в отдельный класс/хелпер или т.п.
    protected static String concatStrings(Collection<String> stringCollection, Set<String> allowedStringSet) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String stringItem : stringCollection) {
            if ((allowedStringSet == null) || (allowedStringSet.contains(stringItem))) {
                stringBuilder.append(LIST_ITEM_QUOTE);
                stringBuilder.append(stringItem);
                stringBuilder.append(LIST_ITEM_QUOTE);
                stringBuilder.append(LIST_DIVIDER);
            }
        }
        if (stringBuilder.length() > LIST_DIVIDER_LENGTH) {
            stringBuilder.setLength(stringBuilder.length() - LIST_DIVIDER_LENGTH);
        }
        String stringStr = stringBuilder.toString();
        return stringStr;
    }

    public Map<String, Object> getRecordByFieldStringValue(String fieldName, String fieldValue) {
        Map<String, Object> dataMap = this.dataMaps.get(fieldName);
        if (dataMap == null) {
            dataMap = new HashMap<>();
            for (Map<String, Object> record : this.dataList) {
                String recordFieldValue = getStringParam(record, fieldName);
                dataMap.put(recordFieldValue, record);
            }
            this.dataMaps.put(fieldName, dataMap);
        }
        return dataMap;
    }

    public Long getRecordFieldLongValueByFieldStringValue(String fieldName, String fieldValue, String wantedFieldName) {
        Map<String, Long> valueLongMap = this.valueLongMaps.get(wantedFieldName);
        if (valueLongMap == null) {
            valueLongMap = new HashMap<>();
            for (Map<String, Object> record : this.dataList) {
                String recordFieldValue1 = getStringParam(record, fieldName);
                Long recordFieldValue2 = getLongParam(record, wantedFieldName);
                // основной вариант (будет выбран при точном совпадении)
                valueLongMap.put(recordFieldValue1, recordFieldValue2);
                // дополнительный вариант в верхнем регистре
                valueLongMap.put(recordFieldValue1.toUpperCase(), recordFieldValue2);
            }
            this.valueLongMaps.put(wantedFieldName, valueLongMap);
        }
        Long wantedValue = valueLongMap.get(fieldValue);
        return wantedValue;
    }

    public String getRecordFieldStringValueByFieldStringValue(String fieldName, String fieldValue, String wantedFieldName) {
        Map<String, String> valueStringMap = this.valueStringMaps.get(wantedFieldName);
        if (valueStringMap == null) {
            valueStringMap = new HashMap<>();
            for (Map<String, Object> record : this.dataList) {
                String recordFieldValue1 = getStringParam(record, fieldName);
                String recordFieldValue2 = getStringParam(record, wantedFieldName);
                // основной вариант (будет выбран при точном совпадении)
                valueStringMap.put(recordFieldValue1, recordFieldValue2);
                // дополнительный вариант в верхнем регистре
                valueStringMap.put(recordFieldValue1.toUpperCase(), recordFieldValue2);
            }
            this.valueStringMaps.put(wantedFieldName, valueStringMap);
        }
        String wantedValue = valueStringMap.get(fieldValue);
        return wantedValue;
    }

    public String makeStringValuesListStr(String fieldName) {
        Set<String> valuesSet = new HashSet<>();
        for (Map<String, Object> record : this.dataList) {
            String value = getStringParam(record, fieldName);
            valuesSet.add(value);
        }
        String valuesListStr = concatStrings(valuesSet, null);
        return valuesListStr;
    }

    public List<Map<String, Object>> getDataList() {
        return dataList;
    }
}
