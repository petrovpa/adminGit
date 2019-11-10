package com.bivgroup.login.base.utils;

import com.bivgroup.utils.ParamGetter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class StaticDataHelper {

    private StaticDataHelper() {
    }

    public static Set<String> getUserGroups(Map<String, Object> userInfo) {
        return getPropertiesSet(userInfo, "GROUPS", "SYSNAME");
    }

    public static Set<String> getPropertiesSet(Map<String, Object> obj, String nodeName, String sysnameName) {
        Set<String> result = new HashSet<>();
        List<Map<String, Object>> roles = ParamGetter.getListParamName(obj, nodeName);
        if (roles == null || roles.isEmpty()) return result;
        for (Map<String, Object> role : roles) {
            String sysname = ParamGetter.getStringParam(role, sysnameName);
            if (!sysname.isEmpty()) {
                result.add(sysname);
            }
        }
        return result;
    }
}
