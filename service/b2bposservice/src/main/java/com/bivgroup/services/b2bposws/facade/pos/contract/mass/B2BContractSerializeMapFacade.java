package com.bivgroup.services.b2bposws.facade.pos.contract.mass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.external.ExternalService;
import ru.diasoft.utils.XMLUtil;

/**
 *
 * @author aklunok
 */
public class B2BContractSerializeMapFacade {

    private ExternalService externalService;

    private final HashMap<String, Object> massMap = new HashMap();

    public static final String ROWSLIST = "rows";
    public static final String BATCHLIST = "batches";
    public static final String BATCH = "batch";
    public static final String CURBATCH = "curBatch";
    public static final String TOTALCOUNT = "totalCount";
    public static final String MAXCOUNT = "maxCount";
    public static final String METHODNAME = "methodName";
    public static final String TABLENAME = "tableName";
    public static final String CONTR_MAP = "CONTRMAP";
    public static final String CONTREXT_MAP = "CONTREXTMAP";
    public static final String CONTRNODE_MAP = "CONTRNODEMAP";
    public static final String CONTRSECTION_MAP = "CONTRSECTIONMAP";
    public static final String CONTRINSOBJGROUP_MAP = "CONTRINSOBJGROUPMAP";
    public static final String CONTROBJ_MAP = "CONTROBJMAP";
    public static final String CONTRRISK_MAP = "CONTRRISKMAP";
    public static final String CONTRINSOBJ_MAP = "CONTRINSOBJMAP";
    public static final String CONTRPAYMENT_MAP = "CONTRPAYMENTMAP";
    public static final String CONTRMEMBER_MAP = "CONTRMEMBERMAP";
    public static final String CONTRBENEFICIARY_MAP = "CONTRBENEFICIARYMAP";

    public static final Long BATCHSIZE_MAX = 1000L;
    public static final Long BATCHSIZE_MED = 500L;
    public static final Long BATCHSIZE_MIN = 100L;

    private static final String[][] contractKeys = {
        {"STARTDATE", "STARTDATE"},
        {"FINISHDATE", "FINISHDATE"},
        {"STATEID", "STATEID"},
        {"PRODVERID", "PRODVERID"},
        {"INSAMCURRENCYID", "INSAMCURRENCYID"},
        {"DOCUMENTDATE", "DOCUMENTDATE"},
        {"PRODPROGID", "PRODPROGID"},
        {"POLICYID", "EXTERNALID"},
        {"CONTRPOLNUM", "CONTRPOLNUM"},
        {"PREMCURRENCYID", "PREMCURRENCYID"},
        {"PAYVARID", "PAYVARID"},
        {"CONTRPOLSER", "CONTRPOLSER"},
        {"INSAMVALUE", "INSAMVALUE"},
        {"PREMVALUE", "PREMVALUE"},};
    private static final List<String[]> contractKeysList = Arrays.asList(contractKeys);

    private static final String[][] contractSectionKeys = {
        {"STARTDATE", "STARTDATE"},
        {"FINISHDATE", "FINISHDATE"},
        {"INSAMCURRENCYID", "INSAMCURRENCYID"},
        {"PREMCURRENCYID", "PREMCURRENCYID"},
        {"INSAMVALUE", "INSAMVALUE"},
        {"PREMVALUE", "PREMVALUE"},};
    private static final List<String[]> contractSectionKeysList = Arrays.asList(contractSectionKeys);

    private static final String[][] contractInsObjGroupKeys = {
        {"HBDATAVERID", "HBDATAVERID"},
        {"PRODSTRUCTID", "PRODSTRUCTID"},};
    private static final List<String[]> contractInsObjGroupKeysList = Arrays.asList(contractInsObjGroupKeys);

    private static final String[][] contractRiskKeys = {
        {"STARTDATE", "STARTDATE"},
        {"FINISHDATE", "FINISHDATE"},
        {"CURRENCYID", "CURRENCYID"},
        {"CURRENCYID", "INSAMCURRENCYID"},
        {"CURRENCYID", "PREMCURRENCYID"},
        {"INSAMVALUE", "INSAMVALUE"},
        {"PREMVALUE", "PREMVALUE"},
        {"PRODSTRUCTID", "PRODSTRUCTID"},};
    private static final List<String[]> contractRiskKeysList = Arrays.asList(contractRiskKeys);

    private static final String[][] contractInsObjKeys = {
        {"HBDATAVERID", "HBDATAVERID"},
        {"PRODSTRUCTID", "PRODSTRUCTID"},};
    private static final List<String[]> contractInsObjKeysList = Arrays.asList(contractInsObjKeys);

    private static final String[][] contractPaymentKeys = {
        {"AMOUNT", "AMOUNT"},
        {"PAYDATE", "PAYDATE"},};
    private static final List<String[]> contractPaymentKeysList = Arrays.asList(contractPaymentKeys);

    private static final String[][] contractMemberKeys = {
        {"TYPESYSNAME", "TYPESYSNAME"},};

    private static final List<String[]> contractMemberKeysList = Arrays.asList(contractMemberKeys);

    private static final String[][] contractBeneficiaryKeys = {
        {"PART", "PART"},};

    private static final List<String[]> contractBeneficiaryKeysList = Arrays.asList(contractBeneficiaryKeys);

    public void init() {
        massMap.clear();
        massMap.put(CONTR_MAP, initMap("dsB2BContractMassCreate", "B2B_CONTR", BATCHSIZE_MAX));
        massMap.put(CONTREXT_MAP, initMap("dsB2BContractExtensionMassCreate", "B2B_CONTREXT", BATCHSIZE_MIN));
        massMap.put(CONTRNODE_MAP, initMap("dsB2BContractNodeMassCreate", "B2B_CONTRNODE", BATCHSIZE_MAX));
        massMap.put(CONTRSECTION_MAP, initMap("dsB2BContractSectionMassCreate", "B2B_CONTRSECTION", BATCHSIZE_MAX));
        massMap.put(CONTRINSOBJGROUP_MAP, initMap("dsB2BInsuranceObjectGroupMassCreate", "B2B_INSOBJGROUP", BATCHSIZE_MIN));
        massMap.put(CONTROBJ_MAP, initMap("dsB2BContractObjectMassCreate", "B2B_CONTROBJ", BATCHSIZE_MED));
        massMap.put(CONTRRISK_MAP, initMap("dsB2BContractRiskMassCreate", "B2B_CONTRRISK", BATCHSIZE_MED));
        massMap.put(CONTRINSOBJ_MAP, initMap("dsB2BInsuranceObjectMassCreate", "B2B_INSOBJ", BATCHSIZE_MIN));
        massMap.put(CONTRPAYMENT_MAP, initMap("dsB2BPaymentMassCreate", "B2B_PAY", BATCHSIZE_MED));
        massMap.put(CONTRMEMBER_MAP, initMap("dsB2BMemberMassCreate", "B2B_MEMBER", BATCHSIZE_MED));
        massMap.put(CONTRBENEFICIARY_MAP, initMap("dsB2BBeneficiaryMassCreate", "B2B_BENEFICIARY", BATCHSIZE_MED));

    }

    private Map<String, Object> initMap(String methodName, String tableName, Long maxCount) {
        Map<String, Object> row = new HashMap();
        List<Map<String, Object>> batchList = new ArrayList<>();
        Map<String, Object> batch = new HashMap();
        List<Map<String, Object>> rowList = new ArrayList<>();
        batch.put(ROWSLIST, rowList);
        batch.put(METHODNAME, methodName);
        batch.put(TABLENAME, tableName);
        batch.put(TOTALCOUNT, 0);
        batchList.add(batch);

        row.put(BATCHLIST, batchList);
        row.put(METHODNAME, methodName);
        row.put(TABLENAME, tableName);
        row.put(TOTALCOUNT, 0);
        row.put(MAXCOUNT, maxCount);
        row.put(CURBATCH, 0);
        return row;
    }

    private List<Map<String, Object>> getRows(Map<String, Object> rowMap) {
        // get current batch
        Long curBatch = Long.parseLong(rowMap.get(CURBATCH).toString());
        // get maxcount
        Long maxCount = Long.parseLong(rowMap.get(MAXCOUNT).toString());
        // get rowcount in cur bach
        List<Map<String, Object>> batchList = (List<Map<String, Object>>) rowMap.get(BATCHLIST);
        Map<String, Object> batchMap = batchList.get(curBatch.intValue());
        List<Map<String, Object>> rowList = (List<Map<String, Object>>) batchMap.get(ROWSLIST);
        if (rowList.size() < maxCount) {
            return rowList;
        } else {
            Map<String, Object> batch = new HashMap();
            rowList = new ArrayList<>();
            batch.put(ROWSLIST, rowList);
            batch.put(METHODNAME, batchMap.get(METHODNAME));
            batch.put(TABLENAME, batchMap.get(TABLENAME));
            batch.put(TOTALCOUNT, 0);
            batchList.add(batch);
            curBatch++;
            rowMap.put(CURBATCH, curBatch);
            return rowList;
        }
    }

    public void setExternalService(ExternalService externalService) {
        this.externalService = externalService;
    }

    public void serialize(Map<String, Object> contrMap) throws Exception {
        Long contrNodeId = new Long(externalService.getNewId("B2B_CONTRNODE"));
        Long contrId = serializeContract(contrMap, contrNodeId);
        serializeContractNode(contrMap, contrNodeId, contrId);
        serializeContractExt(contrMap, contrId);
        serializeContractSection(contrMap, contrId);
        // contragent
        serializeContractMember(contrMap, contrId);
        serializeContractBeneficiary(contrMap, contrId);
        serializeContractPayment(contrMap, contrId);
    }

    private void copyKeys(Map<String, Object> srcMap, Map<String, Object> dstMap, List<String[]> pairKeys) {
        int fromIndex = 0;
        int toIndex = 1;
        String srcKey;
        String dstKey;
        for (int i = 0; i < pairKeys.size(); i++) {
            String[] keyRelation = pairKeys.get(i);
            srcKey = keyRelation[fromIndex];
            dstKey = keyRelation[toIndex];
            dstMap.put(dstKey, srcMap.get(srcKey));
        }
    }

    private Long serializeContract(Map<String, Object> contrMap, Long contrNodeId) throws Exception {
        Map<String, Object> rowMap = (Map<String, Object>) massMap.get(CONTR_MAP);
        List<Map<String, Object>> rows = getRows(rowMap);

        Map<String, Object> row = new HashMap();
        Long contrId = new Long(externalService.getNewId(rowMap.get(TABLENAME).toString()));
        row.put("CONTRID", contrId);
        row.put("CONTRNODEID", contrNodeId);
        row.put("VERNUMBER", 1L);
        copyKeys(contrMap, row, contractKeysList);
        XMLUtil.convertDateToFloat(row);
        rows.add(row);

        return contrId;
    }

    private void serializeContractNode(Map<String, Object> contrMap, Long contrNodeId, Long contrId) throws Exception {
        Map<String, Object> rowMap = (Map<String, Object>) massMap.get(CONTRNODE_MAP);
        List<Map<String, Object>> rows = getRows(rowMap);

        Map<String, Object> row = new HashMap();
        row.put("CONTRNODEID", contrNodeId);
        row.put("CONTRID", contrId);
        row.put("LASTVERNUMBER", 1L);
        row.put("RVERSION", 1L);
        XMLUtil.convertDateToFloat(row);
        rows.add(row);
    }

    private void serializeContractExt(Map<String, Object> contrMap, Long contrId) throws Exception {
        Map<String, Object> rowMap = (Map<String, Object>) massMap.get(CONTREXT_MAP);
        List<Map<String, Object>> rows = getRows(rowMap);

        Map<String, Object> row = new HashMap();
        Long contrExtId = new Long(externalService.getNewId(rowMap.get(TABLENAME).toString()));
        row.put("CONTRID", contrId);
        row.put("CONTREXTID", contrExtId);
        rows.add(row);
    }

    private void serializeContractSection(Map<String, Object> contrMap, Long contrId) throws Exception {
        Map<String, Object> rowMap = (Map<String, Object>) massMap.get(CONTRSECTION_MAP);
        List<Map<String, Object>> rows = getRows(rowMap);

        Map<String, Object> row = new HashMap();
        Long contrSecId = new Long(externalService.getNewId(rowMap.get(TABLENAME).toString()));
        row.put("CONTRID", contrId);
        row.put("CONTRSECTIONID", contrSecId);
        copyKeys(contrMap, row, contractSectionKeysList);
        XMLUtil.convertDateToFloat(row);
        rows.add(row);
        serializeContractInsObjGroup(contrMap, contrId, contrSecId);
    }

    private void serializeContractInsObjGroup(Map<String, Object> contrMap, Long contrId, Long contrSecId) throws Exception {
        Map<String, Object> rowMap = (Map<String, Object>) massMap.get(CONTRINSOBJGROUP_MAP);
        List<Map<String, Object>> rows = getRows(rowMap);

        List<Map<String, Object>> insobjgrouplist = (List<Map<String, Object>>) contrMap.get("INSOBJGROUPLIST");
        if ((insobjgrouplist != null) && (insobjgrouplist.size() > 0)) {
            for (Map<String, Object> insobjgroup : insobjgrouplist) {
                Long contrInsObjGroupId = new Long(externalService.getNewId(rowMap.get(TABLENAME).toString()));
                Map<String, Object> row = new HashMap();
                row.put("INSOBJGROUPID", contrInsObjGroupId);
                row.put("CONTRSECTIONID", contrSecId);
                copyKeys(insobjgroup, row, contractInsObjGroupKeysList);
                XMLUtil.convertDateToFloat(row);
                rows.add(row);
                List<Map<String, Object>> insobjlist = (List<Map<String, Object>>) insobjgroup.get("OBJLIST");
                if ((insobjlist != null) && (insobjlist.size() > 0)) {
                    for (Map<String, Object> insobj : insobjlist) {
                        Long contrObjId = null;
                        // serialize controbjmap
                        Map<String, Object> contrObjMap = (Map<String, Object>) insobj.get("CONTROBJMAP");
                        if (contrObjMap != null) {
                            contrObjId = serializeContractObj(contrObjMap, contrId);
                        }
                        Map<String, Object> insObjMap = (Map<String, Object>) insobj.get("INSOBJMAP");
                        if (insObjMap != null) {
                            serializeContractInsObj(insObjMap, contrInsObjGroupId, contrObjId);
                        }
                    }
                }
            }
        }
    }

    private Long serializeContractObj(Map<String, Object> contrObjMap, Long contrId) throws Exception {
        Map<String, Object> rowMap = (Map<String, Object>) massMap.get(CONTROBJ_MAP);
        List<Map<String, Object>> rows = getRows(rowMap);

        Map<String, Object> row = new HashMap();
        Long contrObjId = new Long(externalService.getNewId(rowMap.get(TABLENAME).toString()));
        row.put("CONTRID", contrId);
        row.put("CONTROBJID", contrObjId);
        rows.add(row);
        serializeContractRisk(contrObjMap, contrObjId);
        return contrObjId;
    }

    private void serializeContractRisk(Map<String, Object> contrObjMap, Long contrObjId) throws Exception {
        Map<String, Object> rowMap = (Map<String, Object>) massMap.get(CONTRRISK_MAP);
        List<Map<String, Object>> rows = getRows(rowMap);

        List<Map<String, Object>> riskList = (List<Map<String, Object>>) contrObjMap.get("CONTRRISKLIST");
        if ((riskList != null) && (riskList.size() > 0)) {
            for (Map<String, Object> risk : riskList) {
                Long riskId = new Long(externalService.getNewId(rowMap.get(TABLENAME).toString()));
                Map<String, Object> row = new HashMap();
                row.put("CONTRRISKID", riskId);
                row.put("CONTROBJID", contrObjId);
                copyKeys(risk, row, contractRiskKeysList);
                XMLUtil.convertDateToFloat(row);
                rows.add(row);
            }
        }
    }

    private void serializeContractInsObj(Map<String, Object> insObjMap, Long contrInsObjGroupId, Long contrObjId) throws Exception {
        Map<String, Object> rowMap = (Map<String, Object>) massMap.get(CONTRINSOBJ_MAP);
        List<Map<String, Object>> rows = getRows(rowMap);

        Map<String, Object> row = new HashMap();
        Long insObjId = new Long(externalService.getNewId(rowMap.get(TABLENAME).toString()));
        row.put("INSOBJGROUPID", contrInsObjGroupId);
        row.put("CONTROBJID", contrObjId);
        row.put("INSOBJID", insObjId);
        copyKeys(insObjMap, row, contractInsObjKeysList);
        XMLUtil.convertDateToFloat(row);
        rows.add(row);
    }

    private void serializeContractMember(Map<String, Object> contrMap, Long contrId) throws Exception {
        Map<String, Object> rowMap = (Map<String, Object>) massMap.get(CONTRMEMBER_MAP);
        List<Map<String, Object>> rows = getRows(rowMap);

        List<Map<String, Object>> members = (List<Map<String, Object>>) contrMap.get("MEMBERLIST");
        if ((members != null) && (members.size() > 0)) {
            for (Map<String, Object> member : members) {
                Long memberId = new Long(externalService.getNewId(rowMap.get(TABLENAME).toString()));
                Map<String, Object> row = new HashMap();
                row.put("MEMBERID", memberId);
                row.put("CONTRID", contrId);
                copyKeys(member, row, contractMemberKeysList);
                //XMLUtil.convertDateToFloat(row);
                rows.add(row);
            }
        }
    }

    private void serializeContractBeneficiary(Map<String, Object> contrMap, Long contrId) throws Exception {
        Map<String, Object> rowMap = (Map<String, Object>) massMap.get(CONTRBENEFICIARY_MAP);
        List<Map<String, Object>> rows = getRows(rowMap);

        List<Map<String, Object>> benefs = (List<Map<String, Object>>) contrMap.get("BENEFICIARYLIST");
        if ((benefs != null) && (benefs.size() > 0)) {
            for (Map<String, Object> benef : benefs) {
                Long benefId = new Long(externalService.getNewId(rowMap.get(TABLENAME).toString()));
                Map<String, Object> row = new HashMap();
                row.put("BENEFICIARYID", benefId);
                row.put("CONTRID", contrId);
                copyKeys(benef, row, contractBeneficiaryKeysList);
                //XMLUtil.convertDateToFloat(row);
                rows.add(row);
            }
        }
    }

    private void serializeContractPayment(Map<String, Object> contrMap, Long contrId) throws Exception {
        Map<String, Object> rowMap = (Map<String, Object>) massMap.get(CONTRPAYMENT_MAP);
        List<Map<String, Object>> rows = getRows(rowMap);

        List<Map<String, Object>> payments = (List<Map<String, Object>>) contrMap.get("PAYMENTSCHEDULELIST");
        if ((payments != null) && (payments.size() > 0)) {
            for (Map<String, Object> payment : payments) {
                Long payId = new Long(externalService.getNewId(rowMap.get(TABLENAME).toString()));
                Map<String, Object> row = new HashMap();
                row.put("PAYID", payId);
                row.put("CONTRID", contrId);
                copyKeys(payment, row, contractPaymentKeysList);
                XMLUtil.convertDateToFloat(row);
                rows.add(row);
            }
        }
    }

    public Map<String, Object> getSerializeMap() {
        // set totalcount map
        for (Map.Entry<String, Object> entry : massMap.entrySet()) {
            Map<String, Object> rowMap = (Map<String, Object>) entry.getValue();
            List<Map<String, Object>> batchList = (List<Map<String, Object>>) rowMap.get(BATCHLIST);
            for (Map<String, Object> batch : batchList) {
                List<Map<String, Object>> rows = (List<Map<String, Object>>) batch.get(ROWSLIST);
                batch.put(TOTALCOUNT, rows.size());
            }
        }
        return massMap;
    }

}
