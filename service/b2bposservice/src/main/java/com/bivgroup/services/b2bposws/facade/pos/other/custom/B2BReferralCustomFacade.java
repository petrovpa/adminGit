/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.other.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.system.ReferralSender;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.HashMap;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author averichevsm
 */
@BOName("B2BReferralCustom")
public class B2BReferralCustomFacade extends B2BBaseFacade{
    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BReferralBeginMethod(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> contrQParam = new HashMap<String, Object>();
        logger.debug("dsB2BReferralBeginMethod start");
        String guid = null;
        String hash = null;
        String referral = "";
        String transactionId = "";
        Map<String, Object> dataMap = (Map<String, Object>) params.get("DATAMAP");
        if (dataMap != null) {
            if (dataMap.get("hash") != null) {
                hash = dataMap.get("hash").toString();
                guid = base64Decode(hash);
            }
            contrQParam.put("EXTERNALID", guid);
        }
        // Вызов из сервиса
        if (guid == null) {
            if (params.get("CONTRID") != null) {
                contrQParam.put("CONTRID", params.get("CONTRID"));
            } else if (dataMap.get("contrId") != null) {
                contrQParam.put("CONTRID", dataMap.get("contrId"));
            } else {
                throw new Exception("Service need required params DATAMAP for calling from angular or CONTRID for calling from java");
            }
        }
        contrQParam.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> contrRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParamExShort", contrQParam, login, password);
        if (contrRes != null) {
            if (contrRes.get("REFERRAL") != null) {
                referral = getStringParam(contrRes.get("REFERRAL"));
            }
            if (contrRes.get("TRANSACTION_ID") != null) {
                transactionId = getStringParam(contrRes.get("TRANSACTION_ID"));
            }
        }
        Map<String,Object> refParams = new HashMap<String,Object>();
        refParams.put("REFERRAL", referral);
        refParams.put("CONTRID", contrRes.get("CONTRID"));
        refParams.put("TRANSACTION_ID", transactionId);
        refParams.put("METHOD", "BEGIN");
        Map<String,Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BReferralMethod", refParams, login, password);
        logger.debug("dsB2BReferralBeginMethod end");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BReferralMethod(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String referral = params.get("REFERRAL").toString();
        String contrId = params.get("CONTRID").toString();
        String transId = getStringParam(params.get("TRANSACTION_ID"));
        String method = getStringParam(params.get("METHOD"));
        Map<String, Object> refParams = new HashMap<String, Object>();
        refParams.put("LINK", referral);
        refParams.put("ReturnAsHashMap", "TRUE");
        Map<String, Object> refRes = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BReferralBrowseListByParam", refParams, login, password);
        Map<String,Object> result = null;
        ReferralSender rs = new ReferralSender();
        if ("leads.su".equalsIgnoreCase(referral)) {
            result = rs.doReferralGet(login, password, refRes, method, transId, contrId);
        }
        return result;
    }

}
