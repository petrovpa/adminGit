package com.bivgroup.services.b2bposws.facade.pos.mappers.keyrelations;

import java.util.*;

public interface BaseKeyRelations {

    static Map<String, Class> createClassMap(Object[]... classRuleList) {
        Map<String, Class> classMap = new HashMap<String, Class>();
        for (Object[] classRule : classRuleList) {
            if ((classRule != null) && (classRule.length == 2) && (classRule[0] instanceof String) && (classRule[1] instanceof Class)) {
                classMap.put((String) classRule[0], (Class) classRule[1]);
            }
        }
        return classMap;
    }

    static List<String[]> createKeyRelationList(String[][] keyRelationArray) {
        List<String[]> keyRelationList = new ArrayList<String[]>();
        Collections.addAll(keyRelationList, keyRelationArray);
        return keyRelationList;
    }

    static List<String[]> createKeyRelationList(List<String[]>... keyRelationListArray) {
        List<String[]> totalKeyRelationList = new ArrayList<String[]>();
        for (List<String[]> keyRelationList : keyRelationListArray) {
            totalKeyRelationList.addAll(keyRelationList);
        }
        return totalKeyRelationList;
    }

}
