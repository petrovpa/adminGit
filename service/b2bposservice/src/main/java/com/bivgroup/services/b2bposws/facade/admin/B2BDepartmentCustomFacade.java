/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.admin;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import ru.diasoft.services.common.QueryBuilder;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.DefaultedHashMap;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

/**
 * @author kkulkov
 */
@BOName("B2BDepartmentCustom")

public class B2BDepartmentCustomFacade extends B2BBaseFacade {

    public static final String COREWS = "corews";
    public static final String EVENT_PARAMS = "eventParams";
    private static final String adminWS = Constants.ADMINWS;
    private static final String B2BPOSWS = Constants.B2BPOSWS;
    private static final List<Map<String, Object>> timeZones;
    private static final String DEFAULT_COREWS_URL = "http://localhost:8080/corews/corews";

    static {
        long multi = 1000 * 60 * 60;
        List<Map<String, Object>> tlist = new ArrayList<Map<String, Object>>();
        tlist.add(getTimeMap("UTC+12 Камчатка, Чукотка", 12 * multi));
        tlist.add(getTimeMap("GMT+10 Магадан, Верхоянск, Сахалин, Владивосток", 10 * multi));
        tlist.add(getTimeMap("UTC+9 Якутск", 9 * multi));
        tlist.add(getTimeMap("UTC+8 Иркутск", 8 * multi));
        tlist.add(getTimeMap("UTC+7 Красноярск", 7 * multi));
        tlist.add(getTimeMap("UTC+6 Омск", 6 * multi));
        tlist.add(getTimeMap("UTC+5 Екатеринбург", 5 * multi));
        tlist.add(getTimeMap("UTC+4 Московское время, Объединённые Арабские Эмираты, Оман, Азербайджан, Армения, Грузия", 4 * multi));
        tlist.add(getTimeMap("UTC+1 Париж  \"Среднеевропейское (Центральноевропейское) время\" (CET - Central Europe Time Zone)", 1 * multi));
        tlist.add(getTimeMap("UTC+0 Лондон \"Гринвичское время\" / \"Западноевропейское время\" ", 0 * multi));
        tlist.add(getTimeMap("UTC-2 \"Среднеатлантическое время\"", -2 * multi));
        tlist.add(getTimeMap("UTC-3 Аргентина, Буэнос-Айрес", -3 * multi));
        tlist.add(getTimeMap("UTC-4 Канада,  \"Атлантическое время\"", -4 * multi));
        tlist.add(getTimeMap("UTC-5 С Ш А, Нью-Йорк.  \"Восточное время\" (EST - US Eastern Savings Time Zone)", -5 * multi));
        tlist.add(getTimeMap("UTC-6 Чикаго (Chicago). \"Центральное время\" (CST - US Central Time)", -6 * multi));
        tlist.add(getTimeMap("UTC-7 Денвер (Denver), \"Горное время\" (MST - US Mountain Time)", -7 * multi));
        tlist.add(getTimeMap("UTC-8 США, Лос-Анджелес, Сан-Франциско. \"Тихоокеанское время\" (PST - Pacific Savings Time)", -8 * multi));
        timeZones = Collections.unmodifiableList(tlist);
    }

    private static Map<String, Object> getTimeMap(String ZONENAME, long ZONEID) {
        Map<String, Object> aMap = new HashMap<String, Object>();
        aMap.put("ZONEID", ZONEID);
        aMap.put("ZONENAME", ZONENAME);
        return Collections.unmodifiableMap(aMap);
    }

    public int getNewId(String tableName, int batchSize) throws Exception {
        return QueryBuilder.getNewId(Config.getConfig(adminWS).getParam(COREWS, DEFAULT_COREWS_URL), tableName, batchSize);
    }

    private void getDepartmentUsers(Map<String, Object> department) throws Exception {
        Map<String, Object> queryResult = new HashMap<String, Object>();
        ArrayList<Map<String, Object>> header;
        if (null != department.get("children")) {
            header = (ArrayList<Map<String, Object>>) department.get("children");
        } else {
            header = new ArrayList<Map<String, Object>>();
        }
        Map<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("ITEMTYPE", "USERS");
        headerMap.put("ITEMNAME", "Пользователи подразделения");
        headerMap.put("ITEMKEY", "USERS_" + department.get("DEPARTMENTID").toString());
        header.add(headerMap);
        department.put("children", header);
        Map<String, Object> qParams = new HashMap<String, Object>();
        qParams.put("DEPARTMENTID", department.get("DEPARTMENTID"));
        queryResult = this.selectQuery("dsGetUserByDepartment", "dsGetUserByDepartmentCount", department);

        if ((queryResult != null) && (queryResult.get(RESULT) != null) && (((List<Map<String, Object>>) queryResult.get(RESULT)).size() > 0)) {
            headerMap.put("children", queryResult.get(RESULT));
            headerMap.put("hasChildren", Boolean.TRUE);
            department.put("hasChildren", Boolean.TRUE);
        } else {
            headerMap.put("children", new ArrayList<Map<String, Object>>());

        }

    }

    private void getDepartmentRights(Map<String, Object> department) throws Exception {
        Map<String, Object> queryResult = new HashMap<String, Object>();
        ArrayList<Map<String, Object>> header;
        if (null != department.get("children")) {
            header = (ArrayList<Map<String, Object>>) department.get("children");
        } else {
            header = new ArrayList<Map<String, Object>>();
        }
        Map<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("ITEMTYPE", "DEP_RIGHTS");
        headerMap.put("ITEMKEY", "CDRS_" + department.get("DEPARTMENTID").toString());
        headerMap.put("ITEMNAME", "Права подразделения");
        header.add(headerMap);
        department.put("children", header);

        queryResult = this.selectQuery("dsDepartmentProfileRightsList", "dsDepartmentProfileRightsListCount", department);
        if ((queryResult != null) && (queryResult.get(RESULT) != null) && (((List<Map<String, Object>>) queryResult.get(RESULT)).size() > 0)) {
            headerMap.put("children", queryResult.get(RESULT));
            headerMap.put("hasChildren", Boolean.TRUE);
            department.put("hasChildren", Boolean.TRUE);
        } else {
            headerMap.put("children", new ArrayList<Map<String, Object>>());

        }

    }

    private Map<Long, Object> mapDepartmentList(List<Map<String, Object>> paramList) {
        Map<Long, Object> departmentMap = new HashMap<Long, Object>();
        for (int i = 0; i < paramList.size(); i++) {
            Map<String, Object> departmnet = paramList.get(i);
            departmentMap.put((Long) departmnet.get("DEPARTMENTID"), departmnet);
        }
        for (Map.Entry<Long, Object> departmentEntry : departmentMap.entrySet()) {
            Long key = departmentEntry.getKey();
            Map<String, Object> department = (Map<String, Object>) departmentEntry.getValue();
            if (null != department.get("PARENTDEPARTMENT")) {
                if (null != departmentMap.get((Long) department.get("PARENTDEPARTMENT"))) {
                    department.put("FINDPARENT", Boolean.TRUE);

                } else {
                    department.put("FINDPARENT", Boolean.FALSE);
                }
            } else {
                department.put("FINDPARENT", Boolean.FALSE);
            }

        }
        return departmentMap;
    }

    private Map<String, Object> mapDepartmentTree(Map<String, Object> department, List<Map<String, Object>> paramList, boolean loadUsers, boolean loadRights) throws Exception {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if ((null == department.get("ISPROCESSED")) || ((null != department.get("ISPROCESSED")) && (!(Boolean) department.get("ISPROCESSED")))) {
            department.put("ISPROCESSED", Boolean.TRUE);
            Long cDepartment = (Long) department.get("DEPARTMENTID");
            List<Map<String, Object>> subItems = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < paramList.size(); i++) {
                Map<String, Object> processDepartmnet = paramList.get(i);
                Long parentDepartment = null;
                if (null != processDepartmnet.get("PARENTDEPARTMENT")) {
                    parentDepartment = (Long) processDepartmnet.get("PARENTDEPARTMENT");
                }
                if ((null != parentDepartment) && (0 == cDepartment.compareTo(parentDepartment))) {
                    mapDepartmentTree(processDepartmnet, paramList, loadUsers, loadRights);
                    subItems.add(processDepartmnet);
                }
            }

            if (!subItems.isEmpty()) {
                if (loadRights || loadRights) {
                    ArrayList<Map<String, Object>> header;
                    if (null != department.get("children")) {
                        header = (ArrayList<Map<String, Object>>) department.get("children");
                    } else {
                        header = new ArrayList<Map<String, Object>>();
                    }
                    Map<String, Object> headerMap = new HashMap<String, Object>();
                    headerMap.put("ITEMTYPE", "DEPARTMNETS");
                    headerMap.put("ITEMKEY", "DEPS_" + department.get("DEPARTMENTID").toString());
                    headerMap.put("ITEMNAME", "Подчиненные подразделения");
                    header.add(headerMap);
                    department.put("children", header);
                    department.put("hasChildren", Boolean.TRUE);
                    headerMap.put("children", subItems);
                } else {

                    department.put("children", subItems);
                    department.put("hasChildren", Boolean.TRUE);
                }
            } else {
                department.put("hasChildren", Boolean.FALSE);
            }
            department.put("canHaveChildren", Boolean.TRUE);
            if (loadUsers) {
                getDepartmentUsers(department);
            }
            if (loadRights) {
                getDepartmentRights(department);
            }
            //dsGetUserByDepartment

            result.add(department);
        }
        return department;
    }

    private List<Map<String, Object>> buildDepartmentTree(int itemIndex, int level, List<Map<String, Object>> paramList, boolean loadUsers, boolean loadRights) throws Exception {
        List<Map<String, Object>> result = new java.util.ArrayList<Map<String, Object>>();
        for (int i = itemIndex; i < paramList.size(); i++) {
            Map<String, Object> departmnet = paramList.get(i);
            int depLevel = Long.valueOf(departmnet.get("LEVEL").toString()).intValue();
            if (depLevel == level) {
                List<Map<String, Object>> subItems = buildDepartmentTree(i + 1, level + 1, paramList, loadUsers, loadRights);
                if (!subItems.isEmpty()) {
                    if (loadRights || loadRights) {
                        ArrayList<Map<String, Object>> header;
                        if (null != departmnet.get("children")) {
                            header = (ArrayList<Map<String, Object>>) departmnet.get("children");
                        } else {
                            header = new ArrayList<Map<String, Object>>();
                        }
                        Map<String, Object> headerMap = new HashMap<String, Object>();
                        headerMap.put("ITEMTYPE", "DEPARTMNETS");
                        headerMap.put("ITEMNAME", "Подчиненные подразделения");
                        header.add(headerMap);
                        departmnet.put("children", header);
                        departmnet.put("hasChildren", Boolean.TRUE);
                        headerMap.put("children", subItems);
                    } else {

                        departmnet.put("children", subItems);
                        departmnet.put("hasChildren", Boolean.TRUE);
                    }
                } else {
                    departmnet.put("hasChildren", Boolean.FALSE);
                }
                departmnet.put("canHaveChildren", Boolean.TRUE);
                if (loadUsers) {
                    getDepartmentUsers(departmnet);
                }
                if (loadRights) {
                    getDepartmentRights(departmnet);
                }
                //dsGetUserByDepartment
                result.add(departmnet);
            } else if (depLevel < level) {
                return result;
            }
        }
        return result;
    }

    /**
     * Получить список департаментов
     *
     * @return
     * @author kkulkov
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminGetDepartmentList(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> avaliableTableAliases = new HashMap<String, Object>();
        Map<String, Object> avaliableFields = new HashMap<String, Object>();
        Map<String, Object> customParams = params;

        boolean loadUsers = true;
        boolean loadRights = true;

        ///////////////////////////todo: для проверки, потом удалить ///////////////////////////////////////// 
        if ((params.get("USE_SESSION_DEPARTMENTID") != null)
                && (params.get("USE_SESSION_DEPARTMENTID").equals(true))) {
            loadRights = false;
            loadUsers = false;
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (null != params.get("LOADUSERS")) {
            loadUsers = Boolean.parseBoolean(params.get("LOADUSERS").toString());
        }
        if (null != params.get("LOADURIGHTS")) {
            loadRights = Boolean.parseBoolean(params.get("LOADURIGHTS").toString());

        }

        if (null == params.get("ROOTID")) {
            customParams.put("ROOTID", params.get("SESSION_DEPARTMENTID"));
            if (null != params.get("USE_SESSION_DEPARTMENTID")) {
                if (params.get("USE_SESSION_DEPARTMENTID").equals(true)) {
                    customParams.put("ROOTID", params.get("SESSION_DEPARTMENTID"));
                }
            }
        }

        if (null != params.get("JOURNALPARAMS")) {
            List<Map<String, Object>> journalList = (List<Map<String, Object>>) params.get("JOURNALPARAMS");
            for (Map<String, Object> jParam : journalList) {
                avaliableFields.put(jParam.get("NAMESPACE").toString(), Boolean.TRUE);
                avaliableTableAliases.put(jParam.get("TABLEALIAS").toString(), Boolean.TRUE);
            }
        }

        XMLUtil.convertDateToFloat(customParams);
        Map<String, Object> queryResult = new HashMap<String, Object>();
        queryResult = this.selectQuery("dsAdminGetDepartmentList", "dsAdminGetDepartmentListCount", customParams);

        if ((queryResult != null) && (queryResult.get(RESULT) != null) && (((List<Map<String, Object>>) queryResult.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> paramList = (List<Map<String, Object>>) queryResult.get(RESULT);
            List<Map<String, Object>> departmentTree = new ArrayList<Map<String, Object>>();
            mapDepartmentList(paramList);
            for (int i = 0; i < paramList.size(); i++) {
                Map<String, Object> departmnet = paramList.get(i);
                if ((null != departmnet.get("FINDPARENT")) && (!(Boolean) departmnet.get("FINDPARENT"))) {
                    mapDepartmentTree(departmnet, paramList, loadUsers, loadRights);
                    departmentTree.add(departmnet);
                }
            }
            result.put(RESULT, departmentTree);
        } else {
            result.put("PARAMS", new ArrayList<Map<String, Object>>());
        }
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsGetDepartmentList(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsGetDepartmentList", "dsGetDepartmentCount", params);
        return result;
    }

    private Integer getLevelWeight(Object depLevelId) throws Exception {
        Map params = new HashMap<String, Object>();
        params.put("DEPTLEVELID", depLevelId);
        List<Map<String, Object>> result = this.selectQueryAndGetListFromResultMap("depLevelByID", params);
        if (result.size() > 0) {
            Map level = result.get(0);
            return ((Number) level.get("LEVELWEIGHT")).intValue();
        }
        return null;
    }

    @WsMethod(requiredParams = {"ACTIONTYPE"})
    public Map<String, Object> dsDepartmentAction(Map<String, Object> params) throws Exception {
        String at = params.get("ACTIONTYPE").toString();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> qRes = new HashMap<String, Object>();
        if (at.equalsIgnoreCase("DELETE")) {
            qRes = this.callService(adminWS, "depStructureRemove", params, login, password);
        }
        if (at.equalsIgnoreCase("UPDATE")) {
            /*проверим уровни*/
            Integer newLevel = getLevelWeight(params.get("DEPTLEVEL"));
            if (newLevel == null) {
                result.put("Error", String.format("Не указан уровень департамента."));
            }
            params.put("LEVELWEIGHT", newLevel);
            DefaultedHashMap<String, Object> parentParams = new DefaultedHashMap<String, Object>();
            parentParams.put("DEPARTMENTID", params.get("PARENTDEPARTMENT"));
            parentParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
            List<Map<String, Object>> qList = this.selectQueryAndGetListFromResultMap("dsDepartmentBrowseListByParamEx", parentParams);
            if ((null != qList) && (!qList.isEmpty())) {
                Map<String, Object> parent = qList.get(0);
                Integer parentLevel = ((Number) parent.get("LEVELWEIGHT")).intValue();
                if ((parentLevel >= newLevel) && (parentLevel < 1000)) {
                    result.put("Error", String.format("Уровень департамента должен быть ниже уровня главного департамента[%s].", newLevel));
                }
            }
            Map<String, Object> searchParams = new HashMap<String, Object>();
            if (params.containsKey("DEPTCODE") && params.get("DEPTCODE") != null) {
                searchParams.put("DEPTCODE", params.get("DEPTCODE"));
                searchParams.put("EXLUDEDEPARTAMENTID", params.get("DEPARTMENTID"));

                List<Map<String, Object>> searchResult = this.selectQueryAndGetListFromResultMap("dsDepartmentBrowseListByParamEx", searchParams);
                if (searchResult != null && searchResult.size() > 0) {
                    result.put("Error", String.format("Департамент с кодом [%s] уже существует.", params.get("DEPTCODE").toString()));
                }
            }
            if (null == result.get("Error")) {
                // Чистим поля.
                //this.updateQuery("dsClearDepartmentFields", params);
                qRes = this.callService(adminWS, "depStructureUpdate", params, login, password);
            }
        }
        if (at.equalsIgnoreCase("CREATE")) {
            /*проверим уровни*/
            Integer newLevel = getLevelWeight(params.get("DEPTLEVEL"));
            if (newLevel == null) {
                result.put("Error", String.format("Не указан уровень департамента."));
            }
            params.put("LEVELWEIGHT", newLevel);
            DefaultedHashMap<String, Object> parentParams = new DefaultedHashMap<String, Object>();
            parentParams.put("DEPARTMENTID", params.get("PARENTDEPARTMENT"));
            parentParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
            List<Map<String, Object>> qList = this.selectQueryAndGetListFromResultMap("dsDepartmentBrowseListByParamEx", parentParams);
            if ((null != qList) && (!qList.isEmpty())) {
                Map<String, Object> parent = qList.get(0);
                Integer parentLevel = ((Number) parent.get("LEVELWEIGHT")).intValue();
                if ((parentLevel >= newLevel) && (parentLevel < 1000)) {
                    result.put("Error", String.format("Уровень департамента должен быть ниже уровня главного департамента[%s].", newLevel));
                }
            }
            Map<String, Object> searchParams = new HashMap<String, Object>();
            if (params.containsKey("DEPTCODE") && params.get("DEPTCODE") != null) {
                searchParams.put("DEPTCODE", params.get("DEPTCODE"));
                List<Map<String, Object>> searchResult = this.selectQueryAndGetListFromResultMap("dsDepartmentBrowseListByParamEx", searchParams);
                if (searchResult != null && searchResult.size() > 0) {
                    result.put("Error", String.format("Департамент с кодом [%s] уже существует.", params.get("DEPTCODE").toString()));
                }
            }
            if (null == result.get("Error")) {
                qRes = this.callService(adminWS, "depStructureAdd", params, login, password);
            }
        }
        if (null != result.get("Error")) {
            result.put("WSSTATUS", "ERROR");
            result.remove(WsConstants.RET_STATUS);
        }
        result.put(RESULT, qRes);
        return result;

    }

    @WsMethod(requiredParams = {"ACTIONTYPE"})
    public Map<String, Object> dsDepartmentPartnerAction(Map<String, Object> params) throws Exception {
        String at = params.get("ACTIONTYPE").toString();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> qRes = new HashMap<String, Object>();
        if (at.equalsIgnoreCase("DELETE")) {
            qRes = this.callService(adminWS, "depStructureRemove", params, login, password);
            if (qRes.get(WsConstants.RET_STATUS).equals(WsConstants.RET_STATUS_ERROR)) {
                qRes.clear();
                qRes.put("ErrorText", "Не возможно удалить департамент.");
                qRes.put("WSSTATUS", "ERROR");
                qRes.put(WsConstants.RET_STATUS, WsConstants.RET_STATUS_OK);
            } else {
                // Обновляем запись в интеграции
                Map<String, Object> integDep = new HashMap<>();
                integDep.put("DEPARTMENTID", params.get("DEPARTMENTID"));
//                НА СБС пока нет интеграции
//                this.callService(B2BPOSWS, "dsExSysSaleStructIntegDelete", integDep, login, password);
            }
        }
        if (at.equalsIgnoreCase("UPDATE")) {
            params.put("DEPUSELINK", 0L);

            /*проверим уровни*/
            Integer newLevel = getLevelWeight(params.get("DEPTLEVEL"));
            if (newLevel == null) {
                result.put("ErrorText", String.format("Не указан уровень департамента."));
            }
            params.put("LEVELWEIGHT", newLevel);
            DefaultedHashMap<String, Object> parentParams = new DefaultedHashMap<String, Object>();
            parentParams.put("DEPARTMENTID", params.get("PARENTDEPARTMENT"));
            parentParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
            List<Map<String, Object>> qList = this.selectQueryAndGetListFromResultMap("dsDepartmentBrowseListByParamEx", parentParams);
            if ((null != qList) && (!qList.isEmpty())) {
                Map<String, Object> parent = qList.get(0);
                Integer parentLevel = ((Number) parent.get("LEVELWEIGHT")).intValue();
                params.put("DEPTZDIFF", parent.get("DEPTZDIFF"));
                params.put("DEPLINKID", parent.get("DEPLINKID"));
                params.put("DEPTZNAME", parent.get("DEPTZNAME"));
                params.put("DEPTZCHANGE", 0L);

                if ((parentLevel >= newLevel) || (parentLevel < 1000)) {
                    result.put("ErrorText", String.format("Уровень департамента должен быть ниже уровня главного департамента[%s].", newLevel));
                }
            }
            Map<String, Object> searchParams = new HashMap<String, Object>();
            if (params.containsKey("DEPTCODE") && params.get("DEPTCODE") != null) {
                searchParams.put("DEPTCODE", params.get("DEPTCODE"));
                searchParams.put("EXLUDEDEPARTAMENTID", params.get("DEPARTMENTID"));

                List<Map<String, Object>> searchResult = this.selectQueryAndGetListFromResultMap("dsDepartmentBrowseListByParamEx", searchParams);
                if (searchResult != null && searchResult.size() > 0) {
                    result.put("ErrorText", String.format("Департамент с кодом [%s] уже существует.", params.get("DEPTCODE").toString()));
                }
            }
            if (null == result.get("ErrorText")) {
                // Чистим поля.
                //this.updateQuery("dsClearDepartmentFields", params);
                qRes = this.callService(adminWS, "depStructureUpdate", params, login, password);
                // Обновляем запись в интеграции
                Map<String, Object> integDep = new HashMap<>();
                integDep.put("DEPARTMENTID", params.get("DEPARTMENTID"));
                integDep.put("DEPTCODE", params.get("DEPTCODE"));
                integDep.put("DEPTFULLNAME", params.get("DEPTFULLNAME"));
                integDep.put("DEPTSHORTNAME", params.get("DEPTSHORTNAME"));
                integDep.put("DEPTLEVEL", params.get("DEPTLEVEL"));
                integDep.put("PARENTDEPARTMENT", params.get("PARENTDEPARTMENT"));
//                на СБС пока нет интеграции                
//                this.callService(B2BPOSWS, "dsExSysSaleStructIntegUpdate", integDep, login, password);
            }
        }
        if (at.equalsIgnoreCase("CREATE")) {
            params.put("DEPUSELINK", 0L);
            /*проверим уровни*/
            Integer newLevel = getLevelWeight(params.get("DEPTLEVEL"));
            if (newLevel == null) {
                result.put("ErrorText", String.format("Не указан уровень департамента."));
            }
            params.put("LEVELWEIGHT", newLevel);
            DefaultedHashMap<String, Object> parentParams = new DefaultedHashMap<String, Object>();
            parentParams.put("DEPARTMENTID", params.get("PARENTDEPARTMENT"));
            parentParams.put(WsConstants.RETURN_AS_HASH_MAP, "TRUE");
            List<Map<String, Object>> qList = this.selectQueryAndGetListFromResultMap("dsDepartmentBrowseListByParamEx", parentParams);
            if ((null != qList) && (!qList.isEmpty())) {
                Map<String, Object> parent = qList.get(0);
                Integer parentLevel = ((Number) parent.get("LEVELWEIGHT")).intValue();
                params.put("DEPTZDIFF", parent.get("DEPTZDIFF"));
                params.put("DEPLINKID", parent.get("DEPLINKID"));
                params.put("DEPTZNAME", parent.get("DEPTZNAME"));
                params.put("DEPTZCHANGE", 0L);
                if ((parentLevel >= newLevel) && (parentLevel < 1000)) {
                    result.put("ErrorText", String.format("Уровень департамента должен быть ниже уровня главного департамента[%s].", newLevel));
                }
            }
            Map<String, Object> searchParams = new HashMap<String, Object>();
            if (params.containsKey("DEPTCODE") && params.get("DEPTCODE") != null) {
                searchParams.put("DEPTCODE", params.get("DEPTCODE"));
                List<Map<String, Object>> searchResult = this.selectQueryAndGetListFromResultMap("dsDepartmentBrowseListByParamEx", searchParams);
                if (searchResult != null && searchResult.size() > 0) {
                    result.put("ErrorText", String.format("Департамент с кодом [%s] уже существует.", params.get("DEPTCODE").toString()));
                }
            }
            if (null == result.get("ErrorText")) {
                qRes = this.callService(adminWS, "depStructureAdd", params, login, password);
                // Копируем доступ к меню.
                try {
                    Long dId = Long.parseLong(qRes.get(RESULT).toString());
                    Map<String, Object> qParams = new HashMap<String, Object>();
                    qParams.put("ORGSTRUCTID", params.get("PARENTDEPARTMENT"));
                    qList = this.callServiceAndGetListFromResultMap(B2BPOSWS, "dsB2BMenuorgstructBrowseListByParam", qParams, login, password);
                    for (Iterator<Map<String, Object>> it = qList.iterator(); it.hasNext(); ) {
                        Map<String, Object> struct = it.next();
                        struct.remove("MENUORGSTRUCTID");
                        struct.put("ORGSTRUCTID", dId);
                        this.callService(B2BPOSWS, "dsB2BMenuorgstructCreate", struct, login, password);
                    }
                    qParams.clear();
                    qParams.put("DEPARTMENTID", dId);
                    qList = this.callServiceAndGetListFromResultMap(B2BPOSWS, "dsAdminGetAvailableRightsList", qParams, login, password);
                    for (Map<String, Object> r : qList) {
                        if (r.get("RIGHTSYSNAME").toString().equalsIgnoreCase("RPAccessPOS_Branch")) {
                            Map<String, Object> right = new HashMap<>();
                            right.put("RIGHTID", r.get("hid"));
                            right.put("ANYVALUE", false);
                            right.put("RIGHTTYPE", "profileRights");
                            right.put("ISEXCEPTION", 0);
                            right.put("EXCEPTIONMODE", 1);
                            right.put("FILTERS", false);
                            right.put("RIGHTOWNER", "DEPARTMENT");
                            right.put("EXTINTEGRATION", 1L);
                            right.put("OBJECTID", dId);
                            List<Map<String, Object>> filters = new ArrayList<>();
                            Map<String, Object> filter = new HashMap<>();
                            filter.put("SYSNAME", "DEPARTMENTID");
                            filter.put("OPERATION", "==");
                            filter.put("ACCESSMODE", 1);
                            filter.put("ANYVALUE", 0);
                            filter.put("DEPARTMENTID", dId);
                            filter.put("VALUES", params.get("DEPTFULLNAME"));
                            filter.put("KEYS", dId);
                            filters.add(filter);
                            right.put("FILTERS", filters);
                            Map<String, Object> qRightRes = this.callService(Constants.ADMINWS, "admRightAdd", right, login, password);
                            if (qRightRes != null) {
                                qRightRes.put(at, PAGE);
                            }
                        }
                    }
                    // Добавляем запись в интеграцию
                    Map<String, Object> integDep = new HashMap<>();
                    integDep.put("DEPARTMENTID", dId);
                    integDep.put("DEPTCODE", params.get("DEPTCODE"));
                    integDep.put("DEPTFULLNAME", params.get("DEPTFULLNAME"));
                    integDep.put("DEPTSHORTNAME", params.get("DEPTSHORTNAME"));
                    integDep.put("DEPTLEVEL", params.get("DEPTLEVEL"));
                    integDep.put("PARENTDEPARTMENT", params.get("PARENTDEPARTMENT"));
//                    на СБС пока нет интеграции                    
//                    this.callService(B2BPOSWS, "dsExSysSaleStructIntegCreate", integDep, login, password);
                } catch (Exception e) {
                }
            }

        }
        if (null != result.get("ErrorText")) {
            result.put("WSSTATUS", "ERROR");
            result.remove(WsConstants.RET_STATUS);
            qRes.put("ErrorText", result.get("ErrorText"));
            qRes.put("WSSTATUS", "ERROR");
        }
        result.put(RESULT, qRes);
        return result;

    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsGetHandbookForEditCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> qParams = new HashMap<String, Object>();
        qParams.put("EXLUDEDEPARTAMENTID", params.get("DEPARTMENTID"));
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> rMap = new HashMap<String, Object>();
        // Ограничение по орг структуре.
        if (null == params.get("ROOTID")) {
            qParams.put("DEPRIGHT", params.get("SESSION_DEPARTMENTID"));
            if (null != params.get("USE_SESSION_DEPARTMENTID")) {
                if (params.get("USE_SESSION_DEPARTMENTID").equals(true)) {
                    qParams.put("DEPRIGHT", params.get("SESSION_DEPARTMENTID"));
                }
            }
        }

        Map<String, Object> queryResult = this.selectQuery("dsDepartmentBrowseListByParamEx", "dsDepartmentBrowseListByParamExCount", qParams);
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        if ((queryResult != null) && (queryResult.get(RESULT) != null) && (((List<Map<String, Object>>) queryResult.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> paramList = (List<Map<String, Object>>) queryResult.get(RESULT);
            rMap.put("DEPARTMENTS", paramList);
        } else {
            rMap.put("DEPARTMENTS", new ArrayList<Map<String, Object>>());
        }
        qParams.clear();
        qParams.put("DEPARTMENTID", params.get("DEPARTMENTID"));
        if (params.get("DEPARTMENTID") != null) {
            queryResult = this.selectQuery("dsGetUserByDepartment", "dsGetUserByDepartmentCount", qParams);
            if ((queryResult != null) && (queryResult.get(RESULT) != null) && (((List<Map<String, Object>>) queryResult.get(RESULT)).size() > 0)) {
                List<Map<String, Object>> paramList = (List<Map<String, Object>>) queryResult.get(RESULT);
                rMap.put("MANAGERS", paramList);
            } else {
                rMap.put("MANAGERS", new ArrayList<Map<String, Object>>());
            }
        } else {
            rMap.put("MANAGERS", new ArrayList<Map<String, Object>>());
        }
        qParams.clear();
        qParams.put("DEPARTMENTID", params.get("DEPARTMENTID"));
        queryResult = this.selectQuery("dsDepartmentLevelBrowseListByParam", "dsDepartmentLevelBrowseListByParamCount", qParams);
        if ((queryResult != null) && (queryResult.get(RESULT) != null) && (((List<Map<String, Object>>) queryResult.get(RESULT)).size() > 0)) {
            List<Map<String, Object>> paramList = (List<Map<String, Object>>) queryResult.get(RESULT);
            rMap.put("LEVELS", paramList);
        } else {
            rMap.put("LEVELS", new ArrayList<Map<String, Object>>());
        }
        rMap.put("TIMEZONES", timeZones);
        result.put(RESULT, rMap);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BDepartmentBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsB2BDepartmentBrowseListByParamEx", params);
        return result;
    }

}
