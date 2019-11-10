/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.product.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.facade.pos.product.cdi.CacheManager;
import com.bivgroup.services.b2bposws.facade.pos.product.cdi.CacheManagerFactory;
import com.bivgroup.services.b2bposws.facade.pos.product.cdi.CacheManagerImpl;
import com.bivgroup.services.b2bposws.facade.pos.product.cdi.ProductManager;
import com.bivgroup.services.b2bposws.system.Constants;
import com.fasterxml.jackson.databind.ObjectWriter;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

/**
 * @author ilich
 */
@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("B2BProductVersionCustom")
public class B2BProductVersionCustomFacade extends B2BBaseFacade {

    private static final String[] avaliableTableAliases = {
            "T",
            "T2"
    };
    private static final String[] avaliableFields = {
            "EXPLFINISHDATE",
            "EXPLSTARTDATE",
            "PRODVERID",
            "LOGOTYPE",
            "NAME",
            "NOTE",
            "IMGPATH",
            "JSPATH",
            "PRODCODE",
            "PRODID",
            "ASSURERID",
            "ASSURERID",
            "EXTERNALID",
            "INSTERMKINDID",
            "ISHIDDEN",
            "MODELID",
            "NAME",
            "NOTE",
            "PRODKINDID",
            "STRUCTROOTID",
            "SYSNAME",
            "PRODNAME"
    };
    private ProductManager pm = null;

    private ProductManager getPM() {
        if (pm != null) {
            return pm;
        }
        CacheManagerFactory cmf = new CacheManagerFactory();
        CacheManager cm = cmf.getCacheManager(CacheManagerImpl.class);
        pm = cm.getProductManager();
        return pm;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductVersionBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result;
        // check prodverid
        Long prodVerId = getLongParam(params.get("PRODVERID"));
        if (prodVerId != null) {
            result = getPM().getProductByVerId(prodVerId);
            if (result != null) {
                return result;
            }
        }
        // check prodsysname
        String prodSysName = getStringParam(params.get("PRODSYSNAME"));
        if (prodSysName != null && !prodSysName.isEmpty()) {
            result = getPM().getProductBySysName(prodSysName);
            if (result != null) {
                return result;
            }
        }
        Map<String, Object> qParams = new HashMap<>();
        qParams.putAll(params);
        qParams.put("CP_TODAYDATE", new Date());
        Object userTypeId = params.get(Constants.SESSIONPARAM_USERTYPEID);
        if ((userTypeId == null) || ((Long.valueOf(userTypeId.toString()).longValue() != 1)
                && ((Long.valueOf(userTypeId.toString())).longValue() != 4))) {
            qParams.put("CP_DEPARTMENTID", params.get(Constants.SESSIONPARAM_DEPARTMENTID));
        }
        XMLUtil.convertDateToFloat(qParams);
        qParams.put("HIDECHILDCONTR", Boolean.TRUE);

        // для фильтра "Страховой продукт" на журнале договоров
        if (qParams.get("ISHIDDEN") == null) {
            qParams.put("ISHIDDEN", 0);
        }

        result = this.selectQuery("dsB2BProductVersionBrowseListByParamEx", "dsB2BProductVersionBrowseListByParamExCount", qParams);
        if ((null != result) && ((null != result.get(RESULT)) && ((List) result.get(RESULT)).size() > 0)) {
            List productList = (List) result.get(RESULT);
            Map<String, Object> product = (Map<String, Object>) productList.get(0);
            prodVerId = getLongParam(product.get("PRODVERID"));
            prodSysName = getStringParam(product.get("PRODSYSNAME"));
            getPM().addProduct(prodSysName, prodVerId, result);
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductVersionBrowseListForSaleReportByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> qParams = new HashMap<String, Object>();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        qParams.putAll(params);
        qParams.put("ISSALEREPORT", 1);
        return this.callService(Constants.B2BPOSWS, "dsB2BProductVersionBrowseListByParamEx", qParams, login, password);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductVersionBrowseListByParamCustomMultiConditionWhereEx(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        StringBuilder sb = new StringBuilder();

        if (params.get("sortModel") != null) {
            ArrayList<Object> sortModel = (ArrayList<Object>) params.get("sortModel");
            if (!sortModel.isEmpty()) {
                for (Iterator iterator = sortModel.iterator(); iterator.hasNext(); ) {
                    Map<String, String> sModel = (Map<String, String>) iterator.next();
                    if ((sModel.get("field") != null) && (sModel.get("sort") != null)) {
                        sb.append(sModel.get("field").toString() + " " + sModel.get("sort").toString());
                        sb.append(", ");
                    }
                }
                if (sb.length() > 1) {
                    sb.delete(sb.length() - 2, sb.length());
                    params.put("ORDERBY", sb.toString());
                }
            }
        }
        return this.callService(Constants.B2BPOSWS, "dsB2BProductVersionBrowseListByParamCustomWhereEx", params, login, password);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductVersionBrowseListByParamCustomWhereEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BProductVersionBrowseListByParamCustomWhereEx", "dsB2BProductVersionBrowseListByParamCustomWhereExCount", params);
        return result;
    }

    //Обертка обычно закачки продуктов параметром для left join канала продаж КМ СБ1
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductVersionBrowseListForKMSB1ByParamEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        params.put("SALECHANNELID", 50000L);
        params.put("ISSKIPNEGATIVPRODSALESCHANEL", true);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BProductVersionBrowseListByParamEx", params, login, password);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BProductVersionBrowseListForKMSB1ByParam(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> configParams = new HashMap<>();
        configParams.put("LKVISIBLE", 1);
        Map<String, Object> result = this.callServiceLogged(B2BPOSWS_SERVICE_NAME,
                "dsB2BProductVersionBrowseListByParam", configParams, login, password);
        return result;
    }
}
