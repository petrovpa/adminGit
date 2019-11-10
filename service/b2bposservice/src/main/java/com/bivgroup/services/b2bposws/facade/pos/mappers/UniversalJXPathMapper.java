package com.bivgroup.services.b2bposws.facade.pos.mappers;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Универсальный маппер с использованием библиотеки JXPath
 */
public class UniversalJXPathMapper {

    protected final Logger logger = Logger.getLogger(this.getClass());

    public static final String SPECIAL_VALUE_PREFIX = "$";

    public static final String OPERATION_ADD_TO_LIST = SPECIAL_VALUE_PREFIX + "ADDTOLIST";

    public static final String CONSTANT_BOOLEAN_TRUE = SPECIAL_VALUE_PREFIX + "TRUE";
    public static final String CONSTANT_BOOLEAN_FALSE = SPECIAL_VALUE_PREFIX + "FALSE";

    public static final Map<String, Object> CONSTANT_VALUES_MAP;

    static {
        CONSTANT_VALUES_MAP = new HashMap<String, Object>();
        CONSTANT_VALUES_MAP.put(CONSTANT_BOOLEAN_TRUE, true);
        CONSTANT_VALUES_MAP.put(CONSTANT_BOOLEAN_FALSE, false);
    }

    /*
        Универсальный маппинг, используя пути XPath:
        sourceMap - исходная мапа
        sourceMap - мапа для результата маппинга
        keyRelations - ключи для маппинга (например, {"objTypeSysName", "CONTREXTMAP/insObject", "house > 0; flat > 1", "0"})
        objectClasses - мапа с классами создаваемых объектов (например, чтобы маппер создал CONTREXTMAP, ему необходимо знать,
        какого класса ее нужно создавать, в данном случае будет java.util.HashMap)
     */
    public void doMapping(Map<String, Object> sourceMap, Map<String, Object> targetMap,
                          List<String[]> keyRelations, Map<String, Class> objectClasses) throws Exception {
        internalMapping(sourceMap, targetMap, keyRelations, objectClasses, false);
    }

    public void doReverseMapping(Map<String, Object> sourceMap, Map<String, Object> targetMap,
                                 List<String[]> keyRelations, Map<String, Class> objectClasses) throws Exception {
        internalMapping(sourceMap, targetMap, keyRelations, objectClasses, true);
    }

    private String findConvertedValue(String convertString, Object srcValue, int keyIndex1, int keyIndex2) {
        if (srcValue != null) {
            String[] convertArr = convertString.split(";");
            for (String convertBean : convertArr) {
                String[] rule = convertBean.split(">");
                if (rule[keyIndex1].trim().equals(srcValue.toString())) {
                    return rule[keyIndex2].trim();
                }
            }
        }
        return null;
    }

    private Object createPathAndSetValueEx(JXPathContext targetContext, String targetKey, Object value) {
        Object valueForSet = value;
        if (value instanceof String) {
            String valueStr = (String) value;
            boolean isConstant = valueStr.startsWith("$");
            if (isConstant && CONSTANT_VALUES_MAP.containsKey(valueStr)) {
                valueForSet = CONSTANT_VALUES_MAP.get(valueStr);
            }
        }
        targetContext.createPathAndSetValue(targetKey, valueForSet);
        return valueForSet;
    }

    private void internalMapping(Map<String, Object> sourceMap, Map<String, Object> targetMap,
                                 List<String[]> keyRelations, Map<String, Class> objectClasses, boolean isReverse) throws Exception {
        try {
            JXPathContext srcContext = JXPathContext.newContext(sourceMap);
            JXPathContext targetContext = JXPathContext.newContext(targetMap);
            targetContext.setFactory(new JXPathContextFactory(objectClasses));
            int keyIndex1 = !isReverse ? 0 : 1;
            int keyIndex2 = !isReverse ? 1 : 0;
            for (String[] bean : keyRelations) {
                if (bean.length >= 2) {
                    String srcKey = bean[keyIndex1];
                    String targetKey = bean[keyIndex2];
                    if (targetKey.startsWith("$")) {
                        // при маппинге в текущем направлении следует пропускать записи,
                        // которые предназначены для функционала по созданию элементов списков и пр. в противоположном направлении
                        continue;
                    }
                    Object srcValue = null;
                    boolean isFormula = false;
                    if ((srcKey != null) && (!srcKey.isEmpty())) {
                        isFormula = srcKey.toString().startsWith(SPECIAL_VALUE_PREFIX);
                        if (!isFormula) {
                            try {
                                srcValue = srcContext.getValue(srcKey);
                            } catch (Exception ex) {
                            }
                        }
                    }
                    if (!isFormula) {
                        // если количество элементов в данной строчке маппинга - 4, тогда в 3-ем находится строчка конвертации
                        // значений из исходного элемента в конечный, а в 4-ом значение по-умолчанию (если ни одно значение
                        // из 3-его элемента не подставилось)
                        if (bean.length == 4) {
                            String value = findConvertedValue(bean[2], srcValue, keyIndex1, keyIndex2);
                            if (value == null) {
                                value = bean[3];
                            }
                            createPathAndSetValueEx(targetContext, targetKey, value);
                        } else if ((targetKey != null) && (!targetKey.isEmpty())) {
                            if ((srcKey != null) && (!srcKey.isEmpty())) {
                                if (srcValue != null) {
                                    // целевая мапа получает значение, только если оно было найдено в исходной мапе
                                    // (необходимо для возможности работы маппера в режиме выборочного обновления уже существующего объекта)
                                    // todo: если затирание null-ом - feature, то следует ввести флаг для отключения данного поведения
                                    createPathAndSetValueEx(targetContext, targetKey, srcValue);
                                }
                            } else {
                                targetContext.createPath(targetKey);
                            }
                        }
                    } else {
                        if (!isReverse) {
                            // обрабатываем возможные функции
                            if (srcKey.toString().equalsIgnoreCase(OPERATION_ADD_TO_LIST)) {
                                functionAddToList(targetContext.getValue(targetKey), bean[2], bean[3]);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("UniversalJXPathMapper: mapping error ", e);
            throw e;
        }
    }

    /*
        Добавить мапу с ключом в лист (если такой мапы еще нет)
     */
    private void functionAddToList(Object listObj, String keyName, String keyValue) {
        if ((listObj != null) && (listObj instanceof List)) {
            if ((keyName != null) && (!keyName.isEmpty())) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) listObj;
                for (Map<String, Object> bean : list) {
                    if ((bean.get(keyName) != null) && (bean.get(keyName).toString().equals(keyValue))) {
                        return;
                    }
                }
                Map<String, Object> listBean = new HashMap<String, Object>();
                listBean.put(keyName, keyValue);
                list.add(listBean);
            }
        }
    }
}
