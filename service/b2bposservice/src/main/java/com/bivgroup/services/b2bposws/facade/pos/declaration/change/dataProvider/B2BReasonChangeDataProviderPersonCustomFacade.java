package com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class B2BReasonChangeDataProviderPersonCustomFacade extends B2BReasonChangeDataProviderCustomFacade {
    protected static final String LASTNAME_PARAM_NAME = "LASTNAME";
    protected static final String ISVIPSEGMENT_PARAM_NAME = "ISVIPSEGMENT"; // формируем данные для ВИП сегмента

    protected Map<String, Object> getMemberById(Map<String, Object> fullContract, Long thirdPartyId, String memberSysname, String memberRusName) {
        List<Map<String, Object>>  memberList = getOrCreateListParam(fullContract, "MEMBERLIST");
        memberList = filterMemberList(memberList, thirdPartyId, memberSysname);
        String error = "";
        if (memberList == null || memberList.isEmpty()) {
            error = "Не удалось найти " + memberRusName + " с указаным идентификатором";
        }
        Map<String, Object> participantMap = new HashMap<>();
        if (error.isEmpty()) {
            participantMap = getMapParam(memberList.get(0), "PARTICIPANTMAP");
        } else {
            participantMap.put(ERROR, error);
        }
        return participantMap;
    }

    protected List<Map<String, Object>> filterMemberList(List<Map<String, Object>> memberList, Long finalThirdPartyId, String memberSysname) {
        return memberList.stream().filter(new Predicate<Map<String, Object>>() {
            @Override
            public boolean test(Map<String, Object> stringObjectMap) {
                Map<String, Object> participantMap = getMapParam(stringObjectMap, "PARTICIPANTMAP");
                boolean condition = memberSysname.equals(stringObjectMap.get("TYPESYSNAME"));
                if (finalThirdPartyId != null) {
                    condition &= finalThirdPartyId.equals(participantMap.get("THIRDPARTYID"));
                }
                return condition;
            }
        }).collect(Collectors.toList());
    }
}
