package com.bivgroup.services.b2bposws.facade.pos.mappers;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.facade.RowStatus;

import java.text.DecimalFormat;
import java.util.*;

/**
 *
 * @author mmamaev
 */
public class KeyToKeyRemapper {

    // синонимы для констант из RowStatus
    protected static final RowStatus UNMODIFIED = RowStatus.UNMODIFIED;
    protected static final RowStatus INSERTED = RowStatus.INSERTED;
    protected static final RowStatus MODIFIED = RowStatus.MODIFIED;
    protected static final RowStatus DELETED = RowStatus.DELETED;
    protected static final int UNMODIFIED_ID = RowStatus.UNMODIFIED.getId();
    protected static final int INSERTED_ID = RowStatus.INSERTED.getId();
    protected static final int MODIFIED_ID = RowStatus.MODIFIED.getId();
    protected static final int DELETED_ID = RowStatus.DELETED.getId();
    protected static final String ROWSTATUS_PARAM_NAME = RowStatus.ROWSTATUS_PARAM_NAME;
    protected static final String ROWSTATUS_LOG_PATTERN = ROWSTATUS_PARAM_NAME + " = %s (%d)";

    protected Logger logger = Logger.getLogger(this.getClass());

    boolean isVerboseLog = logger.isDebugEnabled();

    //private String[][] collapsedMapsArray = null;
    //private String[][] expandedListsArray = null;
    private List<String[]> keysRelations = null;
    private List<String[]> expandedListsList = null;
    private List<String[]> collapsedMapsList = null;

    public KeyToKeyRemapper(List<String[]> keysRelations, List<String[]> expandedListsList, List<String[]> collapsedMapsList) {
        this.keysRelations = keysRelations;
        this.expandedListsList = expandedListsList;
        this.collapsedMapsList = collapsedMapsList;
    }

    private static String getStringParam(Object bean) {
        if (bean == null) {
            return StringUtils.EMPTY;
        } else if (bean instanceof Double) {
            DecimalFormat df = new DecimalFormat("0");
            df.setMaximumFractionDigits(2);
            return df.format(Double.valueOf(bean.toString()).doubleValue());
        } else {
            return bean.toString();
        }
    }

    private static String getStringParam(Map<String, Object> map, String keyName) {
        String stringParam = StringUtils.EMPTY;
        if (map != null) {
            stringParam = getStringParam(map.get(keyName));
        }
        return stringParam;
    }

    // аналог getStringParam, но с протоколировнием полученного значения
    private String getStringParamLogged(Map<String, Object> map, String keyName) {
        String paramValue = getStringParam(map, keyName);
        logger.debug(keyName + " = " + paramValue);
        return paramValue;
    }

    protected Integer getIntegerParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Integer.valueOf(bean.toString());
        } else {
            return 0;
        }
    }

    // помечает сущность как изменившуюся (условно - только если требуется обновить значение в БД)
    protected Boolean markAsModified(Map<String, Object> targetStruct) {
        Boolean isMarkedAsModified = false;
        Object currentRowStatus = targetStruct.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
            targetStruct.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
            isMarkedAsModified = true;
        }
        return isMarkedAsModified;
    }

    private String[] getKeysChainWithLastKeyUpperCase(String[] keys) {
        String[] result = keys;
        int lastKeyIndex = result.length - 1;
        result[lastKeyIndex] = result[lastKeyIndex].toUpperCase();
        return result;
    }

    private String[] getKeysChainWithAllKeysFirstLetterLowerCase(String[] keys) {
        String[] result = keys;
        for (int keyIndex = 0; keyIndex < result.length; keyIndex++) {
            String lastKeyFirstLetter = result[keyIndex].substring(0, 1);
            String lastKeyOtherLetters = result[keyIndex].substring(1);
            result[keyIndex] = lastKeyFirstLetter.toLowerCase() + lastKeyOtherLetters;
        }
        return result;
    }

    private String getKeysChainWithLastKeyUpperCase(String keysChain) {
        int lastDotIndex = keysChain.lastIndexOf(".");
        String result;
        if (lastDotIndex < 0) {
            result = keysChain.toUpperCase();
        } else {
            String mainPart = keysChain.substring(0, lastDotIndex); // '*' без '.someKeyName'
            String upperPart = keysChain.substring(lastDotIndex).toUpperCase(); // '.SOMEKEYNAME'
            result = mainPart + upperPart; // '*.SOMEKEYNAME'
        }
        return result;
    }

    private Object chainedPut(Map<String, Object> map, String keysChain, Object value) {
        String[] keys = keysChain.split("\\.");
        String[] getKeys;
        String putKey;
        getKeys = Arrays.copyOfRange(keys, 0, keys.length - 1);
        putKey = keys[keys.length - 1];
        Object updatedMap = chainedGet(map, getKeys);
        if ((updatedMap != null) && (updatedMap instanceof Map) && (((Map) updatedMap).get(putKey) == null)) {
            Map<String, Object> updatedMapAsMap = (Map<String, Object>) updatedMap;
            if (value instanceof Map) {
                Map<String, Object> puttedMap = new HashMap<String, Object>();
                puttedMap.putAll((Map) value);
                updatedMapAsMap.put(putKey, puttedMap);
            } else {
                updatedMapAsMap.put(putKey, value);
                if (!(value instanceof List)) {
                    // установленное в мапе значение - не список и не мапа: требуется пометить обновляемую мапу соответствующим ROWSTATUS
                    markAsModified(updatedMapAsMap);
                }
            }
            return value;
        }
        return null;
    }

    private Object chainedCreativePut(Map<String, Object> map, String[] keys, Object value) {
        String[] getKeys = Arrays.copyOfRange(keys, 0, keys.length - 1);
        String putKey = keys[keys.length - 1];
        Object updatedMap = chainedCreativeGet(map, getKeys);
        if ((updatedMap != null) && (updatedMap instanceof Map) /*&& (((Map) updatedMap).get(putKey) == null)*/) {
            Map<String, Object> updatedMapAsMap = (Map<String, Object>) updatedMap;
            if (value instanceof Map) {
                Map<String, Object> puttedMap = new HashMap<String, Object>();
                puttedMap.putAll((Map) value);
                updatedMapAsMap.put(putKey, puttedMap);
            } else {
                updatedMapAsMap.put(putKey, value);
                if (!(value instanceof List)) {
                    // установленное в мапе значение - не список и не мапа: требуется пометить обновляемую мапу соответствующим ROWSTATUS
                    markAsModified(updatedMapAsMap);
                }
            }
            return value;
        }
        return null;
    }

    private Object chainedCreativeGet(Map<String, Object> map, String[] keys) {
        Object element = map;
        for (String key : keys) {
            if (element instanceof Map) {
                Map elementAsMap = (Map) element;
                Object nextElement = elementAsMap.get(key);
                if (nextElement == null) {
                    Map<String, Object> createdMap = new HashMap<String, Object>();
                    elementAsMap.put(key, createdMap);
                    element = createdMap;
                    //return null;
                } else if (nextElement instanceof List) {
                    element = null;
                    if (((List) nextElement).size() > 0) {
                        element = ((List) nextElement).get(0);
                    }
                    if (element == null) {
                        return null;
                    }
                } else {
                    element = nextElement;
                }
            } else {
                // todo: управлять доп. протоколированием через константу
                //logger.debug("Промежуточный ключ '" + keys[i - 1] + "' (в чепочке ключей '" + Arrays.toString(keys) + "') не указывает на карту, установить значение окончательного элемента невозможно.");
                return null;
            }
        }
        return element;
    }

    private Object chainedCreativePut(Map<String, Object> map, String keysChain, Object value) {
        String[] keys = keysChain.split("\\.");
        return chainedCreativePut(map, keys, value);
    }

    private Object chainedCreativePut(Map<String, Object> map, String keysChain, Object value, boolean isCreative) {
        if (isCreative) {
            String[] keys = keysChain.split("\\.");
            return chainedCreativePut(map, keys, value);
        } else {
            return chainedPut(map, keysChain, value);
        }
    }

    // аналог chainedGet, но
    // 1 - с проверкой двух вариантов регистра первых символво всех ключей - как был передан и первые символы малые (*.SomeParentKeyName.SomeChildKeyName и *.someParentKeyName.someChildKeyName)
    // 2 - с проверкой двух вариантов регистра конечного ключа - как был передан и все заглавные (*.someKeyName и *.SOMEKEYNAME)
    private Object chainedGetIgnoreCase(Map<String, Object> map, String keysChain) {
        if (keysChain.isEmpty()) {
            return null;
        }
        String[] keys = keysChain.split("\\.");
        Object result = chainedGet(map, keys);
        if (result == null) {
            String[] keysChainWithAllKeysFirstLetterLowerCase = getKeysChainWithAllKeysFirstLetterLowerCase(keys);
            result = chainedGet(map, keysChainWithAllKeysFirstLetterLowerCase);
        }
        if (result == null) {
            String[] keysChainWithLastKeyUpperCase = getKeysChainWithLastKeyUpperCase(keys);
            result = chainedGet(map, keysChainWithLastKeyUpperCase);
        }
        return result;
    }

    private Object chainedGet(Map<String, Object> map, String keysChain) {
        if (keysChain.isEmpty()) {
            return null;
        }
        String[] keys = keysChain.split("\\.");
        return chainedGet(map, keys);
    }

    private Object chainedGet(Map<String, Object> map, String[] keys) {
        if (keys.length == 0) {
            return null;
        }
        Object element = map;
        for (String key : keys) {
            if (element instanceof Map) {
                Object nextElement = ((Map) element).get(key);
                if (nextElement == null) {
                    return null;
                } else if (nextElement instanceof List) {
                    element = null;
                    if (((List) nextElement).size() > 0) {
                        element = ((List) nextElement).get(0);
                    }
                    if (element == null) {
                        return null;
                    }
                } else {
                    element = nextElement;
                }
            } else {
                // todo: управлять доп. протоколированием через константу
                //logger.debug("Промежуточный ключ '" + keys[i-1] + "' (в чепочке ключей '" + Arrays.toString(keys) + "') не указывает на карту, определить значение окончательного элемента невозможно.");
                return null;
            }
        }
        return element;
    }

    // у полученной карты "разворачивает" указанный список в карты вида 'имяСписка_системноеИмяЭлементаСписка'
    private void expandListToMapBySysName(Map<String, Object> source, String listChainKeys, String sysNameKey) {
        //Object listObj = source.get(listKey);
        String[] keys = listChainKeys.split("\\.");
        String listKey = keys[keys.length - 1];
        Object parentMapObj;
        if (keys.length == 1) {
            parentMapObj = source;
        } else {
            String[] parentMapKeys = Arrays.copyOfRange(keys, 0, keys.length - 1);
            parentMapObj = chainedGet(source, parentMapKeys);
        }
        Object listObj = null;
        Map<String, Object> parentMap = null;
        if (parentMapObj != null) {
            parentMap = (Map<String, Object>) parentMapObj;
            listObj = parentMap.get(listKey);
        }
        if (listObj == null) {
            return;
        }
        List<Map<String, Object>> list = (List<Map<String, Object>>) listObj;
        List<String> sysNames = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            Object newSysName = list.get(i).get(sysNameKey);
            if (newSysName == null) {
                newSysName = i;
            }
            if (newSysName != null) {
                for (int j = 0; j < sysNames.size(); j++) {
                    if (sysNames.get(j).equalsIgnoreCase(newSysName.toString())) {
                        newSysName = null;
                        break;
                    }
                }
                if (newSysName != null) {
                    sysNames.add(newSysName.toString());
                }
            }
        }
        Map<String, Object> expandMap = null;
        if (!sysNames.isEmpty()) {
            expandMap = new HashMap<String, Object>();
            parentMap.put(listKey, expandMap);
        }
        for (String expandedSysName : sysNames) {
            Object expandedElement = getLastElementByAtrrValue(list, sysNameKey, expandedSysName);
            expandMap.put(expandedSysName, expandedElement);
        }
    }

    // у полученной карты "cворачивает" указанную карту вида 'имяСписка.системноеИмяЭлементаСписка' в элемент списка
    private void collapseMapToListBySysName(Map<String, Object> source, String mapKeysChain, String sysNameKey) {
        String[] oldMapKeys = mapKeysChain.split("\\.");
        Object oldMapObj = chainedGet(source, oldMapKeys);
        if (oldMapObj == null) {
            return;
        }
        List<Map<String, Object>> newListTmp = new ArrayList<Map<String, Object>>();
        Object newListObj = chainedCreativePut(source, oldMapKeys, newListTmp);
        Map<String, Object> oldMap = ((Map<String, Object>) oldMapObj);
        List<Map<String, Object>> newList = (List<Map<String, Object>>) newListObj;
        for (Map.Entry entry : oldMap.entrySet()) {
            Map<String, Object> subMap = (Map<String, Object>) entry.getValue();
            String sysNameValue = entry.getKey().toString();
            String sysNameValueDirect = getStringParam(subMap, sysNameKey);
            if ((sysNameValueDirect.isEmpty()) || (sysNameValue.equalsIgnoreCase(sysNameValueDirect))) {
                subMap.put(sysNameKey, sysNameValue);
                newList.add(subMap);
            }
        }
    }

    private Object convertValue(Object convertedValueObj, String convertRulesStr) {
        if (convertedValueObj == null) {
            return null;
        }
        int fromIndex = 0;
        int toIndex = 1;
        String convertedValueStr = convertedValueObj.toString();
        String[] convertRules = convertRulesStr.split("; ");
        String[][] convertTable = new String[convertRules.length][2];
        for (int i = 0; i < convertRules.length; i++) {
            convertTable[i] = convertRules[i].split(" > ");
        }
        for (String[] aConvertTable : convertTable) {
            if (convertedValueStr.equalsIgnoreCase(aConvertTable[fromIndex])) {
                return aConvertTable[toIndex];
            }
        }
        return convertedValueObj;
    }

    // получение из списка последнего элемента у которого в attrName храниться значение attrValue
    private Object getLastElementByAtrrValue(List<Map<String, Object>> list, String attrName, String attrValue) {
        for (int i = list.size() - 1; i >= 0; i--) {
            Map element = list.get(i);
            Object elementAttrValue = element.get(attrName);
            if ((elementAttrValue != null) && (attrValue.equalsIgnoreCase(elementAttrValue.toString()))) {
                return element;
            }
        }
        try {
            int attrValueInt = Integer.parseInt(attrValue);
            return list.get(attrValueInt);
        } catch (NumberFormatException e) {
        }
        return null;
    }

    public Map<String, Object> remap(Map<String, Object> sourceMap) {
        Map<String, Object> targetMap = new HashMap<String, Object>();
        targetMap = remap(sourceMap, targetMap);
        return targetMap;
    }

    public Map<String, Object> remap(Map<String, Object> sourceMap, Map<String, Object> targetMap) {
        //Map<String, Object> targetMap = new HashMap<String, Object>();
        int fromIndex = 0;
        int toIndex = 1;
        // развертывание списков в карты вида 'имяСписка_системноеИмяЭлементаСписка'
        //String[][] expanded = expandedListsArray;
        //for (int e = 0; e < expanded.length; e++) {
        //    String listName = expanded[e][0];
        //    String sysAttrName = expanded[e][1];
        //    expandListToMapBySysName(sourceMap, listName, sysAttrName);
        //}
        // развертывание списков в карты вида 'имяСписка_системноеИмяЭлементаСписка'
        for (String[] expandedListDescr : expandedListsList) {
            String listName = expandedListDescr[0];
            String sysAttrName = expandedListDescr[1];
            expandListToMapBySysName(sourceMap, listName, sysAttrName);
        }
        // развертывание списков в мапы вида 'имяСписка_системноеИмяЭлементаСписка' (для случаев наличия данных мап в targetMap)
        for (int i = collapsedMapsList.size() - 1 ; i >= 0 ; i--) {
            String[] expandedListDescr = collapsedMapsList.get(i);
            String listName = expandedListDescr[0];
            String sysAttrName = expandedListDescr[1];
            expandListToMapBySysName(targetMap, listName, sysAttrName);
        }
        // копирование сведений в новую структуру
        for (String[] keyRelation : keysRelations) {
            String newKey = keyRelation[fromIndex];
            String oldKey = keyRelation[toIndex];
            Boolean isCreativePut = true;
            String convertRulesStr = null;
            if ((keyRelation.length > 3) && (!keyRelation[3].isEmpty())) {
                convertRulesStr = keyRelation[3];
            }
            Object rawValue = chainedGetIgnoreCase(sourceMap, oldKey);
            //if (rawValue == null) {
            //    oldKey = getKeysChainWithLastKeyUpperCase(oldKey);
            //    rawValue = chainedGet(rawParams, oldKey);
            //}
            Object value = null;
            if (rawValue instanceof List) {
                List listValue = (List) rawValue;
                value = listValue.subList(listValue.size() - 1, listValue.size());
            } else {
                value = rawValue;
            }
            if (value != null) {
                if (convertRulesStr != null) {
                    value = convertValue(value, convertRulesStr);
                }
                if (isVerboseLog) {
                    logger.debug("");
                    logger.debug("Source key: " + oldKey);
                    logger.debug("Target key: " + newKey);
                }
                chainedCreativePut(targetMap, newKey, value, isCreativePut);
                if (isVerboseLog) {
                    logger.debug("Setted value: " + value + ((convertRulesStr == null) ? "" : " (converted from '" + rawValue + "' by using rule '" + convertRulesStr + "')"));
                    //chainedCreativePut(rawParamsCopyForLog, oldKey, "'ЗНАЧЕНИЕ ПЕРЕНЕСЕНО'");
                }
            }
        }
        // установка значний по-умолчанию
        for (String[] contractKeyRelation : keysRelations) {
            String newKey = contractKeyRelation[fromIndex];
            Object newKeyValue = chainedGet(targetMap, newKey);
            Boolean isCreativePut = true;
            String defaultValue = "";
            if ((contractKeyRelation.length > 2) && (!contractKeyRelation[2].isEmpty())) {
                defaultValue = contractKeyRelation[2];
            }
            if ((newKeyValue == null) && (!defaultValue.isEmpty())) {
                if (isVerboseLog) {
                    logger.debug("");
                    logger.debug("Target key: " + newKey);
                    logger.debug("Setted default value: " + defaultValue);
                }
                chainedCreativePut(targetMap, newKey, defaultValue, isCreativePut);
            }
        }
        // свертывание карт вида 'имяСписка.системноеИмяЭлементаСписка' в списки
        //String[][] collapsed = collapsedMapsArray;
        //for (int e = 0; e < collapsed.length; e++) {
        //    String listName = collapsed[e][0];
        //    String sysAttrName = collapsed[e][1];
        //    collapseMapToListBySysName(targetMap, listName, sysAttrName);
        //}
        for (String[] collapsedMapDescr : collapsedMapsList) {
            String listName = collapsedMapDescr[0];
            String sysAttrName = collapsedMapDescr[1];
            collapseMapToListBySysName(targetMap, listName, sysAttrName);
        }
        // свертывание карт вида 'имяСписка.системноеИмяЭлементаСписка' в списки для возрата исходной мапы в начальное состояние (требуется например для продукта)
        //collapsed = expandedListsArray;
        //for (int e = 0; e < collapsed.length; e++) {
        //    String listName = collapsed[e][0];
        //    String sysAttrName = collapsed[e][1];
        //    collapseMapToListBySysName(sourceMap, listName, sysAttrName);
        //}
        for (String[] expandedListDescr : expandedListsList) {
            String listName = expandedListDescr[0];
            String sysAttrName = expandedListDescr[1];
            collapseMapToListBySysName(sourceMap, listName, sysAttrName);
        }
        // возврат результата
        return targetMap;
    }

}
