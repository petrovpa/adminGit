package com.bivgroup.services.wsws.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.admin.AdmInfo;

/**
 *
 * @author ilich
 */
public class WSAdmInfo extends AdmInfo {

    @Override
    public List<Map<String, Object>> getExtendableObjects() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> item = new HashMap<String, Object>();
        item.put("SYSNAME", "SAMPLE");
        item.put("PUBLICNAME", "SAMPLE");
        result.add(item);

        return result;
    }

    @Override
    public List<Map<String, Object>> getRigths() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

        Map<String, Object> item = new HashMap<String, Object>();
        item.put("RIGHTSYSNAME", "SAMPLE");
        item.put("RIGHTNAME", "SAMPLE");
        result.add(item);

        return result;
    }
}
