/*
* Copyright (c) Diasoft 2004-2013
 */
package com.bivgroup.services.b2bposws.facade.admin;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Фасад для сущности Admin
 *
 * @author reson
 */
@BOName("B2BAdmin")
public class B2BAdminFacade extends BaseFacade {

    private final String admRoleList = "'dca','dsso','dsa', 'tbp', 'dsg'";
    private static final String LOGIN_PARAM_NAME = "LOGIN";
    @Resource
    private WebServiceContext wsContext;


    @WsMethod(requiredParams = {})
    public Map<String, Object> dsCheckAdminRights(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("ACCESS", Boolean.FALSE);

        Map<String, Object> qParams = new HashMap<String, Object>();
        qParams.put("ROLELIST", admRoleList);
        qParams.put("TODATE", new XMLUtil().convertDate(new Date()));
        qParams.put(LOGIN_PARAM_NAME, params.get(WsConstants.LOGIN));
        Map<String, Object> qResult = this.selectQuery("dsCheckAdminRights", "dsCheckAdminRightsCount", qParams);
        if ((null != qResult) && (null != qResult.get(TOTALCOUNT)) && (Long.valueOf(qResult.get(TOTALCOUNT).toString()) > 0)) {
            result.put("ACCESS", Boolean.TRUE);
        }
        return result;
    }

    @WsMethod(requiredParams = {LOGIN_PARAM_NAME})
    public Map<String, Object> dsCheckAdminRightsByLogin(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("ACCESS", Boolean.FALSE);

        Map<String, Object> qParams = new HashMap<>();
        qParams.put("ROLELIST", admRoleList);
        qParams.put("TODATE", new XMLUtil().convertDate(new Date()));
        qParams.put(LOGIN_PARAM_NAME,  params.get(LOGIN_PARAM_NAME));
        Map<String, Object> qResult = this.selectQuery("dsCheckAdminRights", "dsCheckAdminRightsCount", qParams);
        if ((null != qResult) && (null != qResult.get(TOTALCOUNT)) && (Long.valueOf(qResult.get(TOTALCOUNT).toString()) > 0)) {
            result.put("ACCESS", Boolean.TRUE);
        }
        return result;
    }

}
