package com.bivgroup.integrationservice.admin;

import ru.diasoft.services.inscore.admin.AdmInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IntegrationAdmInfo extends AdmInfo {
    @Override
    public List<Map<String, Object>> getExtendableObjects() {
        List<Map<String, Object>> result = new ArrayList<>();

        Map<String, Object> item = new HashMap<>();
        item.put("SYSNAME", "SAMPLE");
        item.put("PUBLICNAME", "SAMPLE");
        result.add(item);

        return result;
    }

    @Override
    public List<Map<String, Object>> getRigths() {
        List<Map<String, Object>> result = new ArrayList<>();

        Map<String, Object> item = new HashMap<>();
        item.put("RIGHTSYSNAME", "SAMPLE");
        item.put("RIGHTNAME", "SAMPLE");
        result.add(item);

        return result;
    }
}
