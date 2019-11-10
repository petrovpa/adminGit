/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.additionalAgreements.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import static com.bivgroup.services.b2bposws.system.Constants.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author kkulkov
 */
@BOName("B2BAddAgrCustom")
public class B2BAddAgrContractCustomFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String, Object> dsB2BAddAgrApplayContract(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("CONTRID", params.get("CONTRID"));
        queryParams.put(RETURN_AS_HASH_MAP, "TRUE");
        Map<String, Object> contrData = this.callService(B2BPOSWS, "dsB2BContractUniversalLoad", queryParams, login, password);
        Map<String, Object> result = this.callService(B2BPOSWS, "dsB2BAddAgrBrowseListByParam", params, login, password);
        if (result != null) {
            // по продверу ид получить список возможных причин
            Long prodConfId = getLongParam(result.get("PRODCONFIGID"));
            Map<String, Object> prodConfParam = new HashMap<String, Object>();
            prodConfParam.put("PRODCONFID", prodConfId);
            prodConfParam.put("ReturnAsHashMap", "TRUE");
            Map<String, Object> prodConfRes = this.callService(B2BPOSWS, "dsB2BProductConfigBrowseListByParam", prodConfParam, login, password);
            if (prodConfRes != null) {
                Long prodVerId = getLongParam(prodConfRes.get("PRODVERID"));
                params.put("PRODVERID", prodVerId);
                Long addAgrId = getLongParam(params.get("ADDAGRID"));
                queryParams.clear();
                queryParams.put("ADDAGRID", addAgrId);
                Map<String, Object> addAgrCntList = this.callService(B2BPOSWS, "dsB2bAddAgrCntBrowseL", queryParams, login, password);
                if (addAgrCntList != null) {
                    if (addAgrCntList.get(RESULT) != null) {
                        List<Map<String, Object>> cntList = (List<Map<String, Object>>) addAgrCntList.get(RESULT);
                        for (Map<String, Object> cntMap : cntList) {
                            Long hbDataVerId = getLongParam(cntMap.get("HBDATAVERID"));
                            Map<String, Object> extParam = new HashMap<String, Object>();
                            extParam.put("HBDATAVERID", hbDataVerId);
                            extParam.put("ADDAGRID", addAgrId);
                            extParam.put("ReturnAsHashMap", "TRUE");
                            Map<String, Object> res = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", params, login, password);
                            if (res != null) {
                                cntMap.putAll(res);
                            }
                        }
                    }
                }
            }

        }

        return result;
    }
}
