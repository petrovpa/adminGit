package com.bivgroup.ws.kladr.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportStage {
    public final static String ITEMDATE = "ITEMDATE";
    public final static String ITEMNAME = "ITEMNAME";
    public final static String ITEMSTATUS = "ITEMSTATUS";
    public final static String ITEMRESULT = "RESULT";
    private StringBuilder detail = new StringBuilder();

    public StringBuilder getDetail() {
        return detail;
    }

    private List<Map<String, Object>> reportProtocol = new ArrayList<Map<String, Object>>();

    public void addStep(String name, String status) {
        Map<String, Object> item = new HashMap<String, Object>();
        item.put(ITEMDATE, new Date());
        item.put(ITEMNAME, name);
        item.put(ITEMSTATUS, status);
        reportProtocol.add(item);
    }

    public void addResult(String detail) {
        this.detail.append(detail).append("\n");
    }

    public List<Map<String, Object>> getProtocol() {
        List<Map<String, Object>> reportProtocolWithResult = new ArrayList<Map<String, Object>>();
        reportProtocolWithResult.addAll(reportProtocol);
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put(ITEMRESULT, this.getDetail().toString());
        reportProtocolWithResult.add(resultMap);
        return reportProtocolWithResult;
    }

    public void clearHistory() {
        reportProtocol.clear();
    }

}
