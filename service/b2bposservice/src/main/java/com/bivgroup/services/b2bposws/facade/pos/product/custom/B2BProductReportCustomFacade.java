/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 *
 * @author ilich
 */
@BOName("B2BProductReportCustom")
public class B2BProductReportCustomFacade extends B2BBaseFacade {

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductReportBrowseListByParamEx(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BProductReportBrowseListByParamEx start...");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> reportResult = callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductReportBrowseListByParamExQuery", params, login, password);
        List<Map<String, Object>> reportList = getListFromResultMap(reportResult);
        if ((reportList != null) && (!reportList.isEmpty())) {
            Long prodConfId = getLongParamLogged(params, "PRODCONFID");
            if (prodConfId != null) {
                Map<String, Object> prodDefValParams = new HashMap<String, Object>();
                prodDefValParams.put("NAME", "REPORT_BROWSE_POSTPROCESSING_METHOD_NAME");
                prodDefValParams.put("PRODCONFID", prodConfId);
                prodDefValParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> prodDefVal = callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductDefaultValueBrowseListByNameNote", prodDefValParams, login, password);
                String methodName = getStringParamLogged(prodDefVal, "VALUE");
                if (!methodName.isEmpty()) {
                    Map<String, Object> methodParams = new HashMap<String, Object>();
                    methodParams.putAll(params);
                    methodParams.put("REPORTLIST", reportList);
                    Map<String, Object> methodResult = callService(B2BPOSWS_SERVICE_NAME, methodName, methodParams, login, password);
                    reportList = getListFromResultMap(methodResult);
                    reportResult.put(RESULT, reportList);
                }
            }
        }
        logger.debug("dsB2BProductReportBrowseListByParamEx finished.");
        return reportResult;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductReportBrowseListByParamExQuery(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BProductReportBrowseListByParamEx", "dsB2BProductReportBrowseListByParamExCount", params);
        return result;
    }

}
