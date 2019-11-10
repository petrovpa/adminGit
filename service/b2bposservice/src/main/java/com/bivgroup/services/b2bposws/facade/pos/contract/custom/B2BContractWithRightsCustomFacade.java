/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.contract.custom;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.aspect.impl.ownerright.OwnerRightView;
import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

/**
 * @author ilich
 */
@ProfileRights({
        @PRight(sysName = "RPAccessPOS_Branch",
                name = "Доступ по подразделению",
//            joinStr = "  inner join B2B_CONTRORGSTRUCT COS on (t.CONTRID = COS.CONTRID) inner join INS_DEPLVL DEPLVL on (COS.ORGSTRUCTID = DEPLVL.OBJECTID) ",
                joinStr = "  inner join (select COS.CONTRID, max(DEPLVL.PARENTID) as PARENTID\n" +
                        "                          from  B2B_CONTRORGSTRUCT COS" +
                        "                   inner join  INS_DEPLVL DEPLVL on (COS.ORGSTRUCTID = DEPLVL.OBJECTID) where (COS.ISBLOCKED != 1 or COS.ISBLOCKED is null) and COS.OBJLEVEL is null and PRIGHTRESTRICTION\n" +
                        "                group by contrid\n" +
                        "              ) COS on (COS.CONTRID = t.CONTRID) ",
                restrictionFieldName = "DEPLVL.PARENTID",
                optimizeExist = "TRUE",
                paramName = "DEPARTMENTID")})
@OwnerRightView(accessByUserRole = true,
        joinStr = " left join B2B_CONTRORGSTRUCT ORGSTR on (T.CONTRID = ORGSTR.CONTRID) ")
@Auth(onlyCreatorAccess = false)
@CustomWhere(customWhereName = "CUSTOMWHERE")
@BOName("B2BContractWithRightsCustom")
public class B2BContractWithRightsCustomFacade extends B2BBaseFacade {

    protected static final String COREB2BPOSWS_SERVICE_NAME = Constants.COREB2BPOSWS;


    private void addCondition(String operarion, StringBuilder conditionString, StringBuilder currentConditionString) {
        if (conditionString.length() != 0) {
            conditionString.append(operarion);
        }
        conditionString.append("(");
        conditionString.append(currentConditionString.toString());
        conditionString.append(")");
    }

    //
    private String getFieldPrefix(String fieldName) {
        return "T";
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListByParamCustomMultiConditionWhereEx(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        StringBuilder sb = new StringBuilder();

        if (params.get("sortModel") != null) {
            ArrayList<Object> sortModel = (ArrayList<Object>) params.get("sortModel");
            if (!sortModel.isEmpty()) {
                for (Iterator iterator = sortModel.iterator(); iterator.hasNext(); ) {
                    Map<String, String> sModel = (Map<String, String>) iterator.next();
                    if ((sModel.get("colId") != null) && (sModel.get("sort") != null)) {
                        sb.append(sModel.get("colId").toString() + " " + sModel.get("sort").toString());
                        sb.append(", ");
                    }
                }
                if (sb.length() > 1) {
                    sb.delete(sb.length() - 2, sb.length());
                    params.put("ORDERBY", sb.toString());
                }
            }
        }

        if (params.get("filterModel") != null) {
            LinkedHashMap<String, Object> filterModel = (LinkedHashMap<String, Object>) params.get("filterModel");
            if (!filterModel.isEmpty()) {
                try {
                    StringBuilder conditionString = new StringBuilder();
                    for (Map.Entry<String, Object> es1 : filterModel.entrySet()) {
                        String fieldName = es1.getKey();
                        String prefix = getFieldPrefix(fieldName);
                        String fullFielName = prefix + "." + fieldName;
                        StringBuilder currentConditionString = new StringBuilder();
                        currentConditionString.append(fullFielName);
                        Object conditionObject = es1.getValue();
                        // В списке перечисление.
                        if (conditionObject instanceof List) {
                            List<Object> conditionList = (List<Object>) conditionObject;
                            // пустой список генерим is null
                            if (conditionList.isEmpty()) {
                                currentConditionString.append(" IS NULL");
                                addCondition("AND", conditionString, currentConditionString);
                            } else {
                                currentConditionString.append(" IN (");
                                for (Object next : conditionList) {
                                    currentConditionString.append("'" + next.toString() + "'");
                                    currentConditionString.append(",");
                                }
                                currentConditionString.append(")");

                                currentConditionString.delete(currentConditionString.length() - 1, currentConditionString.length());
                                addCondition("AND", conditionString, currentConditionString);
                            }
                        } else if (conditionObject instanceof LinkedHashMap) {
                            for (Map.Entry<String, Object> es2 : ((LinkedHashMap<String, Object>) conditionObject).entrySet()) {
                                String cType = es2.getKey();
                                Object value = es2.getValue();
                                // К реализации в полученом списке нет информации о том к типу какого поля применен фильтр
                                // А типы cType ограничения зависят от типа поля определенного на интерфейсе в "js"
                                // {headerName: "ИД", field: "CONTRID", width: 80, hide: false, filter: 'number'},
                                // == > filter: 'number'
                            }
                        }
                    }
                    params.put("MULTICONDITION", conditionString.toString());
                } catch (Exception e) {
                }
            }
        }

        params.put("HIDECHILDCONTR", Boolean.TRUE);
        return this.callService(Constants.B2BPOSWS, "dsB2BContractBrowseListByParamCustomWhereEx", params, login, password);

    }


    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListByParamCustomCoreWhereEx(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        StringBuilder sb = new StringBuilder();

        if (params.get("sortModel") != null) {
            ArrayList<Object> sortModel = (ArrayList<Object>) params.get("sortModel");
            if (!sortModel.isEmpty()) {
                for (Iterator iterator = sortModel.iterator(); iterator.hasNext(); ) {
                    Map<String, String> sModel = (Map<String, String>) iterator.next();
                    if ((sModel.get("colId") != null) && (sModel.get("sort") != null)) {
                        sb.append(sModel.get("colId").toString() + " " + sModel.get("sort").toString());
                        sb.append(", ");
                    }
                }
                if (sb.length() > 1) {
                    sb.delete(sb.length() - 2, sb.length());
                    params.put("ORDERBY", sb.toString());
                }
            }
        }

        if (params.get("filterModel") != null) {
            LinkedHashMap<String, Object> filterModel = (LinkedHashMap<String, Object>) params.get("filterModel");
            if (!filterModel.isEmpty()) {
                try {
                    StringBuilder conditionString = new StringBuilder();
                    for (Map.Entry<String, Object> es1 : filterModel.entrySet()) {
                        String fieldName = es1.getKey();
                        String prefix = getFieldPrefix(fieldName);
                        String fullFielName = prefix + "." + fieldName;
                        StringBuilder currentConditionString = new StringBuilder();
                        currentConditionString.append(fullFielName);
                        Object conditionObject = es1.getValue();
                        // В списке перечисление.
                        if (conditionObject instanceof List) {
                            List<Object> conditionList = (List<Object>) conditionObject;
                            // пустой список генерим is null
                            if (conditionList.isEmpty()) {
                                currentConditionString.append(" IS NULL");
                                addCondition("AND", conditionString, currentConditionString);
                            } else {
                                currentConditionString.append(" IN (");
                                for (Object next : conditionList) {
                                    currentConditionString.append("'" + next.toString() + "'");
                                    currentConditionString.append(",");
                                }
                                currentConditionString.append(")");

                                currentConditionString.delete(currentConditionString.length() - 1, currentConditionString.length());
                                addCondition("AND", conditionString, currentConditionString);
                            }
                        } else if (conditionObject instanceof LinkedHashMap) {
                            for (Map.Entry<String, Object> es2 : ((LinkedHashMap<String, Object>) conditionObject).entrySet()) {
                                String cType = es2.getKey();
                                Object value = es2.getValue();
                                // К реализации в полученом списке нет информации о том к типу какого поля применен фильтр
                                // А типы cType ограничения зависят от типа поля определенного на интерфейсе в "js"
                                // {headerName: "ИД", field: "CONTRID", width: 80, hide: false, filter: 'number'},
                                // == > filter: 'number'
                            }
                        }
                    }
                    params.put("MULTICONDITION", conditionString.toString());
                } catch (Exception e) {
                }
            }
        }

        params.put("HIDECHILDCONTR", Boolean.TRUE);
        return this.callService(Constants.B2BPOSWS, "dsB2BContractBrowseListByParamCustomWhereCoreEx", params, login, password);

    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListByParamCustomWhereEx(Map<String, Object> params) throws Exception {
        // по дефолту скрываем предыдущие версии.
        params.put("HIDEVERSION", 1L);
        Map<String, Object> filterParams = (Map<String, Object>) params.get("FILTERPARAMS");
        if ((filterParams != null) && (filterParams.get("PARAMS") != null)) {
            Map<String, Object> innerParams = (Map<String, Object>) filterParams.get("PARAMS");
            if ((innerParams.get("HIDEVERSION") != null) && (Long.valueOf(innerParams.get("HIDEVERSION").toString()) > 0)) {
                params.remove("HIDEVERSION");
            }
        }
        filterParams.remove("PARAMS");

        Map<String, Object> result = this.selectQuery("dsB2BContractBrowseListByParamCustomWhereEx", null, params);

        if (result.containsKey("TOTALCOUNT")) {
            result.remove("TOTALCOUNT");
        }
        if (result.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) result.get(RESULT);
            Long rowsCount = getLongParam(params.get("ROWSCOUNT"));
            Long page = getLongParam(params.get("PAGE"));
            int count = resList.size();
            if (count < rowsCount) {
                result.put("TOTALCOUNT", page * rowsCount + count);
            }
        }

        return result;


    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BContractBrowseListByParamCustomWhereCoreEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        /*  <CORELOGIN>man</CORELOGIN>
  <COREPASSWORD>356a192b7913b04c54574d18c28d46e6395428ab</COREPASSWORD>*/
        String corelogin = Config.getConfig(B2BPOSWS_SERVICE_NAME).getParam("CORELOGIN", "man");
        String corepassword = Config.getConfig(B2BPOSWS_SERVICE_NAME).getParam("COREPASSWORD", "356a192b7913b04c54574d18c28d46e6395428ab");
        // по дефолту скрываем предыдущие версии.
        params.put("HIDEVERSION", 1L);
        Map<String, Object> filterParams = (Map<String, Object>) params.get("FILTERPARAMS");
        if ((filterParams != null) && (filterParams.get("PARAMS") != null)) {
            Map<String, Object> innerParams = (Map<String, Object>) filterParams.get("PARAMS");
            if ((innerParams.get("HIDEVERSION") != null) && (Long.valueOf(innerParams.get("HIDEVERSION").toString()) > 0)) {
                params.remove("HIDEVERSION");
            }
        }
        filterParams.remove("PARAMS");
        String surname = "";
        String name = "";
        String patronymic = "";
        String dul = "";
        if (params.get("EXTERNALALIAS") != null) {
            Map<String, Object> extAliasMap = (Map<String, Object>) params.get("EXTERNALALIAS");
            if (extAliasMap.get("3") != null) {
                Map<String, Object> equalMap = (Map<String, Object>) extAliasMap.get("3");
                Map<String, Object> thirdPartySearchParams = new HashMap<>();
                if (equalMap.get("INSURERSURNAME") != null) {
                    params.put("INSURERSURNAME", equalMap.get("INSURERSURNAME"));
                    thirdPartySearchParams.put("SURNAME", equalMap.get("INSURERSURNAME"));
                    surname = getStringParam(equalMap.get("INSURERSURNAME"));
                }
                if (equalMap.get("INSURERNAME") != null) {
                    params.put("INSURERNAME", equalMap.get("INSURERNAME"));
                    thirdPartySearchParams.put("NAME", equalMap.get("INSURERNAME"));
                    name = getStringParam(equalMap.get("INSURERNAME"));
                }
                if ((equalMap.get("INSURERMIDDLENAME") != null) && (!getStringParam(equalMap.get("INSURERMIDDLENAME")).isEmpty())) {
                    params.put("INSURERMIDDLENAME", equalMap.get("INSURERMIDDLENAME"));
                    params.put("SEARCHBYFIONDUL", "TRUE");
                    thirdPartySearchParams.put("PATRONYMIC", equalMap.get("INSURERMIDDLENAME"));
                    patronymic = getStringParam(equalMap.get("INSURERMIDDLENAME"));
                } else {
                    params.put("SEARCHBYFINDUL", "TRUE");
                }
                if (equalMap.get("INSURERDOCUMENT") != null) {
                    String docSerNum = getStringParam(equalMap.get("INSURERDOCUMENT"));
                    docSerNum = docSerNum.replaceAll("-", "");
                    String docSer = docSerNum.substring(0, docSerNum.lastIndexOf(" "));
                    String docNum = docSerNum.substring(docSerNum.lastIndexOf(" ") + 1, docSerNum.length());
                    params.put("INSURERDOCSER", docSer);
                    params.put("INSURERDOCNUM", docNum);
                    thirdPartySearchParams.put("SERNUM", docSerNum);
                    dul = docSerNum;
                }
                try {
                    params.put("THIRDPARTYLIST", -1);
                    Map<String, Object> tpResMap = this.callExternalService(COREB2BPOSWS_SERVICE_NAME, "dsB2BPclientVerBrowseByFIOnDocSeriesNumber", thirdPartySearchParams, corelogin, corepassword);
                    if (tpResMap.get(RESULT) != null) {
                        List<Map<String, Object>> tpResult = (List<Map<String, Object>>) tpResMap.get(RESULT);
                        if (!tpResMap.isEmpty()) {
                            String tpStrList = "-1";
                            for (Map<String, Object> tpMap : tpResult) {
                                if (tpMap.get("EXTID") != null) {
                                    tpStrList = tpStrList + "," + getStringParam(tpMap.get("EXTID"));
                                }
                            }
                            params.put("THIRDPARTYLIST", tpStrList);
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Error search insurer from CORE", e);
                }
            }
        }
        Map<String, Object> result = this.selectQuery("dsB2BContractBrowseListByParamCustomWhereCoreEx", null, params);
        if (result.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) result.get(RESULT);
            List<Map<String, Object>> fiodulList = null;
            if (surname.isEmpty() || name.isEmpty() || dul.isEmpty()) {
                String thirdpartyidList = "";
                for (Map<String, Object> resMap : resList) {
                    if (resMap.get("THIRDPARTYID") != null) {
                        if (!getStringParam(resMap.get("THIRDPARTYID")).isEmpty()) {
                            if (thirdpartyidList.isEmpty()) {
                                thirdpartyidList = getStringParam(resMap.get("THIRDPARTYID"));
                            } else {
                                thirdpartyidList = thirdpartyidList + "," + getStringParam(resMap.get("THIRDPARTYID"));
                            }
                        }
                    }
                }
                if (!thirdpartyidList.isEmpty()) {
                    Map<String, Object> fiodulParams = new HashMap<>();
                    fiodulParams.put("THIRDPARTYIDLIST", thirdpartyidList);
                    try {
                        Map<String, Object> fioRes = this.callExternalService(COREB2BPOSWS_SERVICE_NAME, "dsB2BPclientVerBrowseByThirdPartyIdList", fiodulParams, corelogin, corepassword);
                        if (fioRes.get(RESULT) != null) {
                            fiodulList = (List<Map<String, Object>>) fioRes.get(RESULT);
                            CopyUtils.sortByLongFieldName(fiodulList, "EXTID");

                        }
                    } catch (Exception e) {
                        logger.debug("Error search insurer from CORE", e);
                    }
                }

            }
            if (!resList.isEmpty()) {
                String briefName = "";
                if (!surname.isEmpty() && !name.isEmpty() && !dul.isEmpty()) {
                    briefName = genBriefNameByFio(surname, name, patronymic);
                }
                for (Map<String, Object> resMap : resList) {
                    Long curThirdPartyId = getLongParam(resMap.get("THIRDPARTYID"));
                    List<Map<String, Object>> filterFioList = null;
                    if ((resMap.get("INSURERBRIEFNAME") == null) || (getStringParam(resMap.get("INSURERBRIEFNAME")).isEmpty())) {
                        if (briefName.isEmpty()) {
                            filterFioList = CopyUtils.filterSortedListByLongFieldName(fiodulList, "EXTID", curThirdPartyId);
                            if (filterFioList != null && !filterFioList.isEmpty()) {
                                briefName = genBriefNameByFio(getStringParam(filterFioList.get(0).get("SURNAME")),
                                        getStringParam(filterFioList.get(0).get("NAME")),
                                        getStringParam(filterFioList.get(0).get("PATRONYMIC")));
                            }
                        }
                        resMap.put("INSURERBRIEFNAME", briefName);
                    }
                    if ((resMap.get("INSURERDOCUMENT") == null) || (getStringParam(resMap.get("INSURERDOCUMENT")).isEmpty())
                            || (" ".equals(getStringParam(resMap.get("INSURERDOCUMENT"))))) {
                        if (dul.isEmpty()) {
                            if (filterFioList == null) {
                                filterFioList = CopyUtils.filterSortedListByLongFieldName(fiodulList, "EXTID", curThirdPartyId);
                            }
                            if (filterFioList != null && !filterFioList.isEmpty()) {
                                dul = getStringParam(filterFioList.get(0).get("SERIES") + " " + getStringParam(filterFioList.get(0).get("NO")));
                            }
                        }
                        resMap.put("INSURERDOCUMENT", dul);
                    }

                }
            }
        }

        if (result.containsKey("TOTALCOUNT")) {
            result.remove("TOTALCOUNT");
        }
        if (result.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) result.get(RESULT);
            Long rowsCount = getLongParam(params.get("ROWSCOUNT"));
            Long page = getLongParam(params.get("PAGE"));
            int count = resList.size();
            if (count < rowsCount) {
                result.put("TOTALCOUNT", page * rowsCount + count);
            }
        }


        return result;


    }

    String genBriefNameByFio(String surname, String name, String patronymic) {
        String briefName = "";
        if (!surname.isEmpty() && !name.isEmpty()) {
            briefName = surname + " " + name.substring(0, 1) + ".";
        }
        if (!patronymic.isEmpty()) {
            briefName = briefName + " " + patronymic.substring(0, 1) + ".";
        }
        return briefName;
    }

    @WsMethod(requiredParams = {"SURNAME", "NAME", "PATRONYMIC", "SERNUM"})
    public Map<String, Object> dsB2BPclientVerBrowseByFIOnDocSeriesNumber(Map<String, Object> params) throws Exception {
        Map<String, Object> result = null;
        result = this.selectQuery("dsB2BPclientVerBrowseByFIOnDocSeriesNumber", null, params);
        return result;
    }

    @WsMethod(requiredParams = {"THIRDPARTYIDLIST"})
    public Map<String, Object> dsB2BPclientVerBrowseByThirdPartyIdList(Map<String, Object> params) throws Exception {
        Map<String, Object> result = null;
        result = this.selectQuery("dsB2BPclientVerBrowseByFIOnDocSeriesNumber", null, params);
        return result;
    }

}