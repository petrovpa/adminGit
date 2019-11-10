/*
* Copyright (c) Diasoft 2004-2013
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.journals;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.facade.RowStatus;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.system.external.ExternalService;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 * Фасад для сущности B2B_Handbook_Owner
 *
 * @author kkulkov
 */
@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("B2BHandbookCustom")
public class B2BHandbookCustomFacade extends B2BBaseFacade {

    /**
     * Имена доп. параметров, которые могут находится в мапе с элементом справочника,
     * в то время, как некоторые методы требуют их рядом с переданным элементом
     */
    private static final String[] ADDITIONAL_HB_ITEM_PARAMS = {"HBDATAVERID", "TABLENAME", "PK_SYSNAME"};

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BGetHandbookList(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = null;
        Map<String, Object> customParams = params;
        // обработка одиночного запроса.
        if (null != params.get("ID")) {
            customParams = new HashMap<String, Object>();
            customParams.put("ID", params.get("ID"));
            customParams.put("USERID", params.get(Constants.SESSIONPARAM_USERACCOUNTID));
            result = this.selectQuery("dsB2BHandbookListBrowseListByParam", "dsB2BHandbookListBrowseListByParamCount", customParams);
        } else {
            parseDates(params, Double.class);

            // Переписываем ограничение по пользователю
            customParams.put("USERID", params.get(Constants.SESSIONPARAM_USERACCOUNTID));
            customParams.put("CP_TODAYDATE", new Date());
            XMLUtil.convertDateToFloat(customParams);
            result = this.selectQuery("dsB2BHandbookListBrowseListByParam", "dsB2BHandbookListBrowseListByParamCount", customParams);

        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BGetHandbookListEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = null;
        Map<String, Object> customParams = new HashMap<String, Object>();
        // обработка одиночного запроса.
        if (null != params.get("ID")) {
            customParams = new HashMap<String, Object>();
            customParams.put("ID", params.get("ID"));
            customParams.put("HBDATAVERID", params.get("HBDATAVERID"));
            customParams.put("USERID", params.get(Constants.SESSIONPARAM_USERACCOUNTID));
            result = this.selectQuery("dsB2BHandbookListBrowseListByParamEx", "dsB2BHandbookListBrowseListByParamExCount", customParams);
        }
        return result;
    }

    @WsMethod(requiredParams = {"HANDBOOKOWNERID"})
    public Map<String, Object> dsB2BGetHandbookData(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> checkPermisionParams = new HashMap<String, Object>();
        checkPermisionParams.put("ID", params.get("HANDBOOKOWNERID"));
        checkPermisionParams.put(Constants.SESSIONPARAM_USERACCOUNTID, params.get(Constants.SESSIONPARAM_USERACCOUNTID));
        Map<String, Object> result = null;
        Map<String, Object> checkPermisionResult = this.callService(Constants.B2BPOSWS, "dsB2BGetHandbookList", checkPermisionParams, login, password);
        if ((checkPermisionResult != null) && (checkPermisionResult.get(RESULT) != null) && (((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).size() > 0)) {
            StringBuilder sb = new StringBuilder();
            String orderBy = "";
            if (params.get("sortModel") != null) {
                ArrayList<Object> sortModel = (ArrayList<Object>) params.get("sortModel");
                if (!sortModel.isEmpty()) {
                    for (Iterator iterator = sortModel.iterator(); iterator.hasNext(); ) {
                        Map<String, String> sModel = (Map<String, String>) iterator.next();
                        if ((sModel.get("colId") != null) && (sModel.get("sort") != null)) {
                            sb.append(sModel.get("colId")).append(" ").append(sModel.get("sort"));
                            sb.append(", ");
                        }
                    }
                    if (sb.length() > 1) {
                        sb.delete(sb.length() - 2, sb.length());
                        orderBy = sb.toString();
                    }
                }
            }

            Map<String, Object> firstHandBook = ((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).get(0);
            // Нашли запись handbook, значит доступ есть.
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("HBDATAVERID", firstHandBook.get("KINDHANDBOOKHBDATAVERSIONID"));
            queryParams.put("PAGE", params.get("PAGE"));
            queryParams.put("ROWSCOUNT", params.get("ROWSCOUNT"));
            queryParams.put("FILTERPARAMS", params.get("FILTERPARAMS"));
            if (!orderBy.isEmpty()) {
                queryParams.put(ORDERBY, orderBy);
            }
            parseDates(params, Double.class);

            result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", queryParams, login, password);

        } else {
            result.put(RESULT, new ArrayList<Map<String, Object>>());
            result.put(TOTALCOUNT, 0L);
        }
        return result;

    }

    @WsMethod(requiredParams = {"HANDBOOKOWNERID"})
    public Map<String, Object> dsB2BGetHandbookDataEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> checkPermisionParams = new HashMap<String, Object>();
        checkPermisionParams.put("ID", params.get("HANDBOOKOWNERID"));
        checkPermisionParams.put("HBDATAVERID", params.get("HBDATAVERID"));
        checkPermisionParams.put(Constants.SESSIONPARAM_USERACCOUNTID, params.get(Constants.SESSIONPARAM_USERACCOUNTID));
        Map<String, Object> result = null;
        // для редактора справочников по калькулятору = возможно указать в оунере - калкверид вместо хбдатаверид.
        Map<String, Object> checkPermisionResult = this.callService(Constants.B2BPOSWS, "dsB2BGetHandbookListEx", checkPermisionParams, login, password);
        if ((checkPermisionResult != null) && (checkPermisionResult.get(RESULT) != null) && (((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).size() > 0)) {
            StringBuilder sb = new StringBuilder();
            String orderBy = "";
            if (params.get("sortModel") != null) {
                ArrayList<Object> sortModel = (ArrayList<Object>) params.get("sortModel");
                if (!sortModel.isEmpty()) {
                    for (Iterator iterator = sortModel.iterator(); iterator.hasNext(); ) {
                        Map<String, String> sModel = (Map<String, String>) iterator.next();
                        if ((sModel.get("colId") != null) && (sModel.get("sort") != null)) {
                            sb.append(sModel.get("colId")).append(" ").append(sModel.get("sort"));
                            sb.append(", ");
                        }
                    }
                    if (sb.length() > 1) {
                        sb.delete(sb.length() - 2, sb.length());
                        orderBy = sb.toString();
                    }
                }
            }
            List<Map<String, Object>> hbList = (List<Map<String, Object>>) checkPermisionResult.get(RESULT);
            if (hbList.get(0).get("HBDATAVERID") != null) {
                if (getStringParam(hbList.get(0).get("HBDATAVERID")).equals(getStringParam(params.get("HBDATAVERID")))) {
                    // Нашли запись handbook, значит доступ есть.
                    Map<String, Object> queryParams = new HashMap<String, Object>();
                    queryParams.put("HBDATAVERID", params.get("HBDATAVERID"));
                    queryParams.put("PAGE", params.get("PAGE"));
                    queryParams.put("ROWSCOUNT", params.get("ROWSCOUNT"));
                    queryParams.put("FILTERPARAMS", params.get("FILTERPARAMS"));
                    if (!orderBy.isEmpty()) {
                        queryParams.put(ORDERBY, orderBy);
                    }
                    parseDates(params, Double.class);

                    result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", queryParams, login, password);

                } else {
                    result.put(RESULT, new ArrayList<Map<String, Object>>());
                    result.put(TOTALCOUNT, 0L);
                }
            } else {
                result.put(RESULT, new ArrayList<Map<String, Object>>());
                result.put(TOTALCOUNT, 0L);
            }

        } else {
            result.put(RESULT, new ArrayList<Map<String, Object>>());
            result.put(TOTALCOUNT, 0L);
        }
        return result;

    }

    @WsMethod()
    public Map<String, Object> dsB2BGetHandbookDataByName(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> checkPermisionParams = new HashMap<String, Object>();
        checkPermisionParams.put("CALCVERID", params.get("CALCVERID"));
        checkPermisionParams.put("HBDESCRNAME", params.get("NAME"));
        Map<String, Object> result = null;
        // для редактора справочников по калькулятору = возможно указать в оунере - калкверид вместо хбдатаверид.
        Map<String, Object> checkPermisionResult = this.callService(Constants.INSTARIFICATORWS, "dsCalculatorHandbookBrowseListByParamEx", checkPermisionParams, login, password);
        if ((checkPermisionResult != null) && (checkPermisionResult.get(RESULT) != null) && (((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).size() > 0)) {
            StringBuilder sb = new StringBuilder();
            String orderBy = "";
            if (params.get("sortModel") != null) {
                ArrayList<Object> sortModel = (ArrayList<Object>) params.get("sortModel");
                if (!sortModel.isEmpty()) {
                    for (Iterator iterator = sortModel.iterator(); iterator.hasNext(); ) {
                        Map<String, String> sModel = (Map<String, String>) iterator.next();
                        if ((sModel.get("colId") != null) && (sModel.get("sort") != null)) {
                            sb.append(sModel.get("colId")).append(" ").append(sModel.get("sort"));
                            sb.append(", ");
                        }
                    }
                    if (sb.length() > 1) {
                        sb.delete(sb.length() - 2, sb.length());
                        orderBy = sb.toString();
                    }
                }
            }
            List<Map<String, Object>> hbList = (List<Map<String, Object>>) checkPermisionResult.get(RESULT);
            if (hbList.get(0).get("HBDATAVERID") != null) {
                // Нашли запись handbook, значит доступ есть.
                Map<String, Object> queryParams = new HashMap<String, Object>();
                queryParams.put("HBDATAVERID", hbList.get(0).get("HBDATAVERID"));
                if (!orderBy.isEmpty()) {
                    queryParams.put(ORDERBY, orderBy);
                }
                parseDates(params, Double.class);

                result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", queryParams, login, password);

            }
        } else {
            // справочник не зареган в калькуляторе. пытаемся получить по имени последнюю версию.
            Map<String, Object> descrParam = new HashMap<String, Object>();
            descrParam.put("NAME", params.get("NAME"));
            descrParam.put("ReturnAsHashMap", "TRUE");
            // для редактора справочников по калькулятору = возможно указать в оунере - калкверид вместо хбдатаверид.
            Map<String, Object> descrRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookDescriptorsBrowseListByParamEx", descrParam, login, password);
            if (descrRes != null) {
                if (descrRes.get("HBDESCRID") != null) {
                    Map<String, Object> dataverParam = new HashMap<String, Object>();
                    dataverParam.put("HBDESCRID", descrRes.get("HBDESCRID"));
                    dataverParam.put("ReturnAsHashMap", "TRUE");
                    // для редактора справочников по калькулятору = возможно указать в оунере - калкверид вместо хбдатаверид.
                    Map<String, Object> dataverRes = this.callService(Constants.INSTARIFICATORWS, "dsHandbookDataVersionBrowseListByParamEx", dataverParam, login, password);
                    if (dataverRes != null) {
                        if (dataverRes.get("HBDATAVERID") != null) {

                            StringBuilder sb = new StringBuilder();
                            String orderBy = "";
                            if (params.get("sortModel") != null) {
                                ArrayList<Object> sortModel = (ArrayList<Object>) params.get("sortModel");
                                if (!sortModel.isEmpty()) {
                                    for (Iterator iterator = sortModel.iterator(); iterator.hasNext(); ) {
                                        Map<String, String> sModel = (Map<String, String>) iterator.next();
                                        if ((sModel.get("colId") != null) && (sModel.get("sort") != null)) {
                                            sb.append(sModel.get("colId")).append(" ").append(sModel.get("sort"));
                                            sb.append(", ");
                                        }
                                    }
                                    if (sb.length() > 1) {
                                        sb.delete(sb.length() - 2, sb.length());
                                        orderBy = sb.toString();
                                    }
                                }
                            }
                            Map<String, Object> queryParams = new HashMap<String, Object>();
                            queryParams.put("HBDATAVERID", dataverRes.get("HBDATAVERID"));
                            if (!orderBy.isEmpty()) {
                                queryParams.put(ORDERBY, orderBy);
                            }
                            parseDates(params, Double.class);
                            result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", queryParams, login, password);
                        }
                    }
                }
            }
        }
        if (result == null) {
            result = new HashMap<String, Object>();
            result.put(RESULT, new ArrayList<Map<String, Object>>());
            result.put(TOTALCOUNT, 0L);
        }
        return result;

    }

    @WsMethod(requiredParams = {"HANDBOOKOWNERID", "HBITEM"})
    public Map<String, Object> dsB2BDeleteHandbookData(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> checkPermisionParams = new HashMap<String, Object>();
        checkPermisionParams.put("ID", params.get("HANDBOOKOWNERID"));
        Map<String, Object> result = null;
        Map<String, Object> checkPermisionResult = this.callService(Constants.B2BPOSWS, "dsB2BGetHandbookList", checkPermisionParams, login, password);
        if ((checkPermisionResult != null) && (checkPermisionResult.get(RESULT) != null) && (((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).size() > 0)) {

            Map<String, Object> firstHandBook = ((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).get(0);
            // Нашли запись handbook, значит доступ есть.
            Map<String, Object> queryParams = new HashMap<String, Object>();
            Map<String, Object> hbItem = (Map<String, Object>) params.get("HBITEM");
            queryParams.put("HBDATAVERID", firstHandBook.get("KINDHANDBOOKHBDATAVERSIONID"));
            queryParams.putAll(hbItem);
            result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordDelete", queryParams, login, password);
        } else {
            result.put(RESULT, new ArrayList<Map<String, Object>>());
            result.put(TOTALCOUNT, 0L);
        }
        return result;

    }

    @WsMethod(requiredParams = {"HANDBOOKOWNERID", "HBITEM"})
    public Map<String, Object> dsB2BDeleteHandbookDataEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> checkPermisionParams = new HashMap<String, Object>();
        checkPermisionParams.put("ID", params.get("HANDBOOKOWNERID"));
        checkPermisionParams.put("HBDATAVERID", params.get("HBDATAVERID"));
        checkPermisionParams.put(Constants.SESSIONPARAM_USERACCOUNTID, params.get(Constants.SESSIONPARAM_USERACCOUNTID));
        Map<String, Object> result = null;
        Map<String, Object> checkPermisionResult = this.callService(Constants.B2BPOSWS, "dsB2BGetHandbookListEx", checkPermisionParams, login, password);
        if ((checkPermisionResult != null) && (checkPermisionResult.get(RESULT) != null) && (((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> hbList = (List<Map<String, Object>>) checkPermisionResult.get(RESULT);
            if (hbList.get(0).get("HBDATAVERID") != null) {
                if (getStringParam(hbList.get(0).get("HBDATAVERID")).equals(getStringParam(params.get("HBDATAVERID")))) {

                    Map<String, Object> firstHandBook = ((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).get(0);
                    // Нашли запись handbook, значит доступ есть.
                    Map<String, Object> queryParams = new HashMap<String, Object>();
                    Map<String, Object> hbItem = (Map<String, Object>) params.get("HBITEM");
                    queryParams.put("HBDATAVERID", params.get("HBDATAVERID"));
                    queryParams.putAll(hbItem);
                    result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordDelete", queryParams, login, password);
                } else {
                    result.put(RESULT, new ArrayList<Map<String, Object>>());
                    result.put(TOTALCOUNT, 0L);
                }
            } else {
                result.put(RESULT, new ArrayList<Map<String, Object>>());
                result.put(TOTALCOUNT, 0L);
            }
        } else {
            result.put(RESULT, new ArrayList<Map<String, Object>>());
            result.put(TOTALCOUNT, 0L);
        }
        return result;

    }

    @WsMethod(requiredParams = {"HANDBOOKOWNERID", "ACTIONTYPE", "HBITEM"})
    public Map<String, Object> dsB2BCreateOrUpdateHandbookData(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> checkPermisionParams = new HashMap<String, Object>();
        checkPermisionParams.put("ID", params.get("HANDBOOKOWNERID"));
        Map<String, Object> result = null;
        Map<String, Object> checkPermisionResult = this.callService(Constants.B2BPOSWS, "dsB2BGetHandbookListEx", checkPermisionParams, login, password);
        if ((checkPermisionResult != null) && (checkPermisionResult.get(RESULT) != null) && (((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).size() > 0)) {

            Map<String, Object> firstHandBook = ((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).get(0);
            // Нашли запись handbook, значит доступ есть.
            Map<String, Object> queryParams = new HashMap<String, Object>();
            Map<String, Object> hbItem = (Map<String, Object>) params.get("HBITEM");
            queryParams.put("HBDATAVERID", firstHandBook.get("KINDHANDBOOKHBDATAVERSIONID"));
            parseDates(hbItem, Double.class);
            XMLUtil.convertDateToFloat(hbItem);
            queryParams.putAll(hbItem);
            if (params.get("ACTIONTYPE").toString().equalsIgnoreCase("CREATE")) {
                result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", queryParams, login, password);
            } else {
                result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", queryParams, login, password);

            }

        } else {
            result.put(RESULT, new ArrayList<Map<String, Object>>());
            result.put(TOTALCOUNT, 0L);
        }
        return result;

    }

    @WsMethod(requiredParams = {"HANDBOOKOWNERID", "ACTIONTYPE", "HBITEM"})
    public Map<String, Object> dsB2BCreateOrUpdateHandbookDataEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> checkPermisionParams = new HashMap<String, Object>();
        checkPermisionParams.put("ID", params.get("HANDBOOKOWNERID"));
        checkPermisionParams.put("HBDATAVERID", params.get("HBDATAVERID"));
        checkPermisionParams.put(Constants.SESSIONPARAM_USERACCOUNTID, params.get(Constants.SESSIONPARAM_USERACCOUNTID));
        Map<String, Object> result = null;
        Map<String, Object> checkPermisionResult = this.callService(Constants.B2BPOSWS, "dsB2BGetHandbookListEx", checkPermisionParams, login, password);
        if ((checkPermisionResult != null) && (checkPermisionResult.get(RESULT) != null) && (((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> hbList = (List<Map<String, Object>>) checkPermisionResult.get(RESULT);
            if (hbList.get(0).get("HBDATAVERID") != null) {
                if (getStringParam(hbList.get(0).get("HBDATAVERID")).equals(getStringParam(params.get("HBDATAVERID")))) {

                    Map<String, Object> firstHandBook = ((List<Map<String, Object>>) checkPermisionResult.get(RESULT)).get(0);
                    // Нашли запись handbook, значит доступ есть.
                    Map<String, Object> queryParams = new HashMap<String, Object>();
                    Map<String, Object> hbItem = (Map<String, Object>) params.get("HBITEM");
                    queryParams.put("HBDATAVERID", params.get("HBDATAVERID"));
                    parseDates(hbItem, Double.class);
                    XMLUtil.convertDateToFloat(hbItem);
                    queryParams.putAll(hbItem);
                    if (params.get("ACTIONTYPE").toString().equalsIgnoreCase("CREATE")) {
                        result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", queryParams, login, password);
                    } else {
                        result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", queryParams, login, password);

                    }
                } else {
                    result.put(RESULT, new ArrayList<Map<String, Object>>());
                    result.put(TOTALCOUNT, 0L);
                }
            } else {
                result.put(RESULT, new ArrayList<Map<String, Object>>());
                result.put(TOTALCOUNT, 0L);
            }

        } else {
            result.put(RESULT, new ArrayList<Map<String, Object>>());
            result.put(TOTALCOUNT, 0L);
        }
        return result;

    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BHandbookBrowseList(Map<String, Object> params) throws Exception {

        Map<String, Object> result = null;
        Map<String, Object> customParams = params;

        parseDates(params, Double.class);

        // Переписываем ограничение по пользователю
        customParams.put("USERID", params.get(Constants.SESSIONPARAM_USERACCOUNTID));
        if (null != params.get("PAGE")) {
            customParams.put("PAGE", params.get("PAGE"));
        }
        if (null != params.get("ROWSCOUNT")) {
            customParams.put("ROWSCOUNT", params.get("ROWSCOUNT"));
        }

        XMLUtil.convertDateToFloat(customParams);
        result = this.selectQuery("dsB2BHandbookBrowseList", "dsB2BHandbookBrowseListCount", customParams);


        return result;
    }

    @WsMethod(requiredParams = {"HBDESCRID"})
    public Map<String, Object> dsB2BInsHBPropDescrBrowseCustomListByParamEx(Map<String, Object> params) throws Exception {
        params.put("ORDERBY", "T1.ORDERNUM, T.HBPROPDESCRID");
        Map<String, Object> result = this.selectQuery("dsB2BInsHBPropDescrBrowseCustomListByParamEx", "dsB2BInsHBPropDescrBrowseCustomListByParamExCount", params);
        return result;
    }

    @WsMethod(requiredParams = {"HBDATAVERID"})
    public Map<String, Object> dsB2BHandbookRecordBrowseListByParam(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("HBDATAVERID", params.get("HBDATAVERID"));
        result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordBrowseListByParam", queryParams, login, password);
        return result;
    }

    @WsMethod(requiredParams = {"ACTIONTYPE", "HBITEM", "HBDATAVERID", "TABLENAME", "PK_SYSNAME"})
    public Map<String, Object> dsB2BAddOrUpdateHandbookDataEx(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BAddOrUpdateHandbookDataEx start");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        Map<String, Object> hbItem = (Map<String, Object>) params.get("HBITEM");
        queryParams.put("HBDATAVERID", params.get("HBDATAVERID"));
        parseDates(hbItem, Double.class);
        XMLUtil.convertDateToFloat(hbItem);
        queryParams.putAll(hbItem);

        String pkColumn = getStringParamLogged(params, "PK_SYSNAME");
        String pkValue = getStringParamLogged(hbItem, pkColumn);

        //AUTO_PK support for Handbooks
        if ((params.get("ACTIONTYPE").toString().equalsIgnoreCase("CREATE")) && (pkValue.isEmpty())) {
//            Map<String, Object> maxIdParams = new HashMap<String, Object>();
//            maxIdParams.put("HBDATAVERID", params.get("HBDATAVERID").toString());
//            List<Map<String, Object>> maxIdList = this.callServiceAndGetListFromResultMap(Constants.B2BPOSWS, "dsB2BHandbookRecordBrowseListByParam", maxIdParams, login, password);
//            Long maxId;
//            if (maxIdList.size() > 0) {
//                CopyUtils.sortByLongFieldName(maxIdList, pkColumn);
//                Map<String, Object> lastElem = maxIdList.get(maxIdList.size() - 1);
//                maxId = getLongParamLogged(lastElem, pkColumn);
//            } else {
//                maxId = 0L;
//            }
//            maxId++;
//            hbItem.put(pkColumn, maxId.toString());

            ExternalService externalService = this.getExternalService();
            Long generatedID = getLongParam(externalService.getNewId(params.get("TABLENAME").toString()));
            hbItem.put(pkColumn, generatedID);
        }

        if (params.get("ACTIONTYPE").toString().equalsIgnoreCase("CREATE")) {
            result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordCreate", queryParams, logger.isDebugEnabled(), login, password);
        } else {
            result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordUpdate", queryParams, logger.isDebugEnabled(), login, password);
        }
        logger.debug("dsB2BAddOrUpdateHandbookDataEx end");
        return result;
    }

    @WsMethod(requiredParams = {"HBITEM", "HBDATAVERID"})
    public Map<String, Object> dsB2BRemoveHandbookDataEx(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BRemoveHandbookDataEx start");
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> result = null;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        Map<String, Object> hbItem = (Map<String, Object>) params.get("HBITEM");
        queryParams.put("HBDATAVERID", params.get("HBDATAVERID"));
        queryParams.putAll(hbItem);
        result = this.callService(Constants.INSTARIFICATORWS, "dsHandbookRecordDelete", queryParams, logger.isDebugEnabled(), login, password);
        logger.debug("dsB2BRemoveHandbookDataEx end");
        return result;
    }

    @WsMethod(requiredParams = {"HBITEM"})
    public Map<String, Object> dsB2BSaveHandbookDataEx(Map<String, Object> params) throws Exception {
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        Map<String, Object> hbItem = getMapParam(params, "HBITEM");
        // доп. параметры, которые могут находится в мапе с элементом справочника, в то время, как методы требуют их рядом с переданным элементом
        for (String hbItemParamName : ADDITIONAL_HB_ITEM_PARAMS) {
            if (params.get(hbItemParamName) == null) {
                params.put(hbItemParamName, hbItem.remove(hbItemParamName));
            }
        }
        params.put("ACTIONTYPE", "ANY"); // ACTIONTYPE является обязательным параметром для dsB2BAddOrUpdateHandbookDataEx, но анализируется только значение "CREATE"
        RowStatus rowStatus = getRowStatusLogged(hbItem);
        // в dsB2BAddOrUpdateHandbookDataEx вместо rowStatus используется ACTIONTYPE, а dsHandbookRecordDelete реализован отдельно
        // поэтому требуется переход от стандартных rowStatus-ов к ACTIONTYPE
        // todo: автору ACTIONTYPE-а привести всё к rowStatus (а возможно и объединить всё в один метод), чтобы исключить промежуточные цепочки из вызовов методов
        String methodName = "";
        switch (rowStatus) {
            case DELETED: {
                methodName = "dsB2BRemoveHandbookDataEx";
                break;
            }
            case INSERTED: {
                // в dsB2BAddOrUpdateHandbookDataEx вместо rowStatus используется ACTIONTYPE = "CREATE" для создания записи
                params.put("ACTIONTYPE", "CREATE");
            }
            case MODIFIED: {
                methodName = "dsB2BAddOrUpdateHandbookDataEx";
                break;
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.putAll(hbItem);
        result.remove(LOGIN);
        if (!methodName.isEmpty()) {
            params.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> processResult = this.callService(Constants.B2BPOSWS, methodName, params, logger.isDebugEnabled(), login, password);
            result.putAll(processResult);
        }
        markAllMapsByKeyValue(result, ROWSTATUS_PARAM_NAME, UNMODIFIED_ID);
        return result;
    }

    @WsMethod(requiredParams = {"SYSNAME"})
    public Map<String, Object> dsB2BLoadHandbookBySysname(Map<String, Object> params) throws Exception {
        Map<String, Object> result = null;
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("NAME", params.get("SYSNAME"));

        result = this.selectQuery("dsB2BLoadHandbookBySysname", queryParams);

        return result;
    }
}
