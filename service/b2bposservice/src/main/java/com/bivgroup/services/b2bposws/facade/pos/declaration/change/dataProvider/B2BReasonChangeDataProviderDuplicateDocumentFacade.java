package com.bivgroup.services.b2bposws.facade.pos.declaration.change.dataProvider;

import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.util.HashMap;
import java.util.Map;

@BOName("B2BReasonChangeDataProviderDuplicateDocument")
public class B2BReasonChangeDataProviderDuplicateDocumentFacade extends B2BReasonChangeDataProviderCustomFacade {
    private static final String REASON_TYPE_NAME = "DUBLE";
    private static final String DOC_TYPE_PARAM_NAME = "dubleType";
    private static final String DOP_AGR_DATE_PARAM_NAME = "dopAgrDATE";
    private static final String ADDRESS_PARAM_NAME = "dubleAddr";
    private final Logger logger = Logger.getLogger(this.getClass());

    @WsMethod(requiredParams = {REPORT_DATA_PARAM_NAME, REASON_PARAM_NAME})
    public Map<String, Object> dsB2BChangeReasonDataProviderDuplicateDocument(Map<String, Object> params) throws Exception {
        Map<String, Object> reasonMap = getMapParam(params, REASON_PARAM_NAME);
        Map<String, Object> reportData = getMapParam(params, REPORT_DATA_PARAM_NAME);
        Map<String, Object> reasonReportDataMap = getOrCreateMapParam(reportData, REASON_PARAM_NAME);
        addReasonType(reasonReportDataMap, REASON_TYPE_NAME);
        String dubleType = getStringParam(reasonMap, "docType");
        reasonReportDataMap.put(DOC_TYPE_PARAM_NAME, dubleType);
        boolean isCallFromGate = isCallFromGate(params);
        if (dubleType.equals("DOPAGR")) {
            reasonReportDataMap.put(DOP_AGR_DATE_PARAM_NAME, reasonMap.get("fromDate" + (isCallFromGate ? "$date" : "")));
        }
        reasonReportDataMap.put(ADDRESS_PARAM_NAME, getStringParam(reasonMap, "docReceiptAddressStr"));
        String duplicateCause = getStringParam(reasonMap, "makeDuplicateReason");
        switch (duplicateCause) {
            case "NEEDORIGPRINT":
                reasonReportDataMap.put("needOrigPrint", TRUE_STR_VALUE);
                reasonReportDataMap.put("loss", FALSE_STR_VALUE);
                reasonReportDataMap.put("otherReason", FALSE_STR_VALUE);
                break;
            case "LOSS":
                reasonReportDataMap.put("loss", TRUE_STR_VALUE);
                reasonReportDataMap.put("needOrigPrint", FALSE_STR_VALUE);
                reasonReportDataMap.put("otherReason", FALSE_STR_VALUE);
                break;
            case "OTHERREASON":
                reasonReportDataMap.put("loss", FALSE_STR_VALUE);
                reasonReportDataMap.put("needOrigPrint", FALSE_STR_VALUE);
                reasonReportDataMap.put("otherReason", getStringParam(reasonMap, "reasonDescription"));
                break;
        }
        reportData.put("REPLEVEL", REPLEVEL_FOR_SPECIFICATION_TWO);

        Map<String, Object> result = new HashMap<>();
        reportData.put(REASON_PARAM_NAME, reasonReportDataMap);
        result.put(REPORT_DATA_PARAM_NAME, reportData);
        return result;
    }
}
