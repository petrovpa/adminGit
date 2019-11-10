package com.bivgroup.services.b2bposws.facade.pos.contract.mass;

import com.bivgroup.services.b2bposws.facade.B2BLifeBaseFacade;
import static com.bivgroup.services.b2bposws.facade.pos.contract.mass.B2BContractSerializeMapFacade.BATCHLIST;
import static com.bivgroup.services.b2bposws.facade.pos.contract.mass.B2BContractSerializeMapFacade.ROWSLIST;
import static com.bivgroup.services.b2bposws.facade.pos.contract.mass.B2BContractSerializeMapFacade.TOTALCOUNT;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author aklunok
 */
@BOName("B2BContractMass")
public class B2BContractMassFacade extends B2BLifeBaseFacade {

    public static final String METHODNAME = "methodName";
    public static final String TOTALCOUNT = "totalCount";

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractMassTest(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        // get contract
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("POLICYID", 1414588534L);
//        queryParams.put("PRODPROGID", 218002L);
        //queryParams.put(RETURN_AS_HASH_MAP, true);
        // вызов сортировки договоров по типу продуктов
//        Map<String, Object> fullMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationGetContractList", queryParams, login, password);
        Map<String, Object> fullMap = this.callService(B2BPOSWS_SERVICE_NAME, "dsLifeIntegrationGetFileContractList", queryParams, login, password);
        B2BContractSerializeMapFacade cserialize = new B2BContractSerializeMapFacade();
        cserialize.init();
        cserialize.setExternalService(this.getExternalService());
        Map<String, Object> resultFromCall = (Map<String, Object>) fullMap.get(RESULT);
        if (resultFromCall != null) {
            List<Map<String, Object>> resultMapFromCall = (List<Map<String, Object>>) resultFromCall.get("CONTRLIST");
            if ((resultMapFromCall != null) && (resultMapFromCall.size() > 0)) {
                logger.error("serialize start, count = " + resultMapFromCall.size());
                for (Map<String, Object> contract : resultMapFromCall) {
                    // serialize contract
                    cserialize.serialize(contract);
                }
                logger.error("serialize finish");
                // save mass contract
                Map<String, Object> saveParams = new HashMap<>();
                saveParams.put("CONTRMASSMAP", cserialize.getSerializeMap());
                logger.error("contract mass save start");
                dsB2BContractMassCreate(saveParams);
                logger.error("contract mass save finish");
            }
        }
        return null;
    }


    /*
    Метод посделовательно сохраняет списки объектов как есть
     */
    @WsMethod(requiredParams = {"CONTRMASSMAP"})
    public Map<String, Object> dsB2BContractMassCreate(Map<String, Object> params) throws Exception {
//        String login = params.get(WsConstants.LOGIN).toString();
//        String password = params.get(WsConstants.PASSWORD).toString();
        String methodName;
        Long totalCount;
        Map<String, Object> massMap = (Map<String, Object>) params.get("CONTRMASSMAP");
        for (Map.Entry<String, Object> entry : massMap.entrySet()) {
            Map<String, Object> rowMap = (Map<String, Object>) entry.getValue();
            List<Map<String, Object>> batchList = (List<Map<String, Object>>) rowMap.get(BATCHLIST);
            for (Map<String, Object> batch : batchList) {
                totalCount = Long.parseLong(batch.get(TOTALCOUNT).toString());
                if (totalCount > 0) {
                    methodName = batch.get(METHODNAME).toString();
                    logger.error(methodName + "save start, count = " + totalCount);

                    doRowsMassCreate(methodName, batch);
                }
            }
        }
        return null;
    }

    private Map<String, Object> doRowsMassCreate(String queryName, Map<String, Object> params) throws Exception {
        int[] insertResult = this.insertQuery(queryName, params);
        return null;
    }

}
