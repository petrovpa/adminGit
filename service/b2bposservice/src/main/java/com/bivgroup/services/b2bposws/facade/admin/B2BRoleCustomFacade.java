/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.admin;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import org.apache.cayenne.query.NamedQuery;
import ru.diasoft.services.common.QueryBuilder;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.*;

/**
 * @author kkulkov
 */
@CustomWhere()
@BOName("B2BDepartmentCustom")
public class B2BRoleCustomFacade extends B2BBaseFacade {

    /**
     * Логин пользователя Администратор Системы
     */
    public static final String DSA = "dsa";
    /**
     * Логин пользователя Администратор Безопасности
     */
    public static final String DSSO = "dsso";
    /**
     * Логин пользователя Установщик система
     */
    public static final String DCA = "dca";
    /**
     * Имя роли Технолог Бизнес-Процессов
     */
    public static final String TBP = "tbp";
    /**
     * Имя роли Администратор Подразделения/группы
     */
    public static final String DSG = "dsg";
    private static final String coreWs = Constants.COREWS;
    private static final String adminWs = Constants.ADMINWS;
    /**
     * Наименование параметра таблиц отношения права
     */
    private static final String RELATION_TABLE = "RELATION_TABLE";
    /**
     * Наименование параметра поля для построения ограничения
     */
    private static final String RESTRICTION_FIELD = "RESTRICTION_FIELD";

    private static String getCorewsURI() {
        return Config.getConfig(coreWs).getParam(coreWs, "http://localhost:8080/corews/corews");
    }

    public int getNewId(String tableName, int batchSize) throws Exception {
        return QueryBuilder.getNewId(getCorewsURI(), tableName, batchSize);
    }

    /**
     * Получить список департаментов
     *
     * @return
     * @author kkulkov
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsUserRoleBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> customParams = params;

        XMLUtil.convertDateToFloat(customParams);
        Map<String, Object> queryResult = new HashMap<String, Object>();
        result = this.selectQuery("dsUserRoleBrowseListByParamEx", "dsUserRoleBrowseListByParamExCount", customParams);

        return result;
    }

    @WsMethod(requiredParams = {"ACTIONTYPE"})
    public Map<String, Object> dsUserRoleAction(Map<String, Object> params) throws Exception {
        String at = params.get("ACTIONTYPE").toString();
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();

        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> qRes = new HashMap<String, Object>();
        if (at.equalsIgnoreCase("DELETE")) {
            qRes = this.callService(coreWs, "admRoleDelete", params, login, password);
        }
        if (at.equalsIgnoreCase("UPDATE")) {
            params.remove("ROLESYSNAME");
            qRes = this.callService(coreWs, "admRoleEdit", params, login, password);
        }
        if (at.equalsIgnoreCase("CREATE")) {
            qRes = admroleadd(params);
        }
        if (qRes.get(WsConstants.RET_STATUS).toString().equalsIgnoreCase(WsConstants.RET_STATUS_ERROR)) {
            qRes.put("WSSTATUS", "ERROR");
            qRes.remove(WsConstants.RET_STATUS);
        }
        result.put(RESULT, qRes);
        return result;
    }

    public Map<String, Object> admroleadd(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>(1);
        StringBuilder sb = new StringBuilder();

        if (params.get("PROJECTID") == null || params.get("PROJECTID").equals("")) {
            result.put("Error", "Не указан проект.");
        }
        String roleName = params.get("ROLENAME") == null ? "" : String.valueOf(params.get("ROLENAME"));
        if (roleName.trim().equals("")) {
            result.put("Error", "Не указано наименование роли.");
        }
        if (roleName.equalsIgnoreCase(DSA) || roleName.equalsIgnoreCase(DSSO) || roleName.equalsIgnoreCase(DCA)
                || roleName.equalsIgnoreCase(TBP) || roleName.equalsIgnoreCase(DSG)) {
            result.put("Error", String.format("Невозможно создать роль с наименованием: %s.", roleName));
        }

        // проверка на уникальность
        List list = getDataContext().performQuery(new NamedQuery("admRoleUnique", params));
        if (list.size() > 0) {
            result.put("Error", String.format("Роль с наименованием: %s уже существует.", roleName));

        }
        int id = getNewId("CORE_USERROLE", 1);
        params.put("ID", id);
        NamedQuery query = new NamedQuery("admRoleAdd", params);
        getDataContext().performNonSelectingQuery(query);
        result.put(WsConstants.RET_STATUS, WsConstants.RET_STATUS_OK);
        return result;
    }

    /**
     * Список проектов
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsGetProjectList(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> callRes = this.callService(adminWs, "admProjectListAll", params, login, password);
        if ((null != callRes) && (null != callRes.get(RESULT))) {
            List<Map<String, Object>> mappedList = (List<Map<String, Object>>) callRes.get(RESULT);
            for (Iterator<Map<String, Object>> it = mappedList.iterator(); it.hasNext(); ) {
                Map<String, Object> prj = it.next();
                prj.put("hid", prj.get("PROJECTID"));
                prj.put("name", prj.get("PROJECTNAME"));

            }
        }
        return callRes;
    }

    /**
     * Список типов прав
     *
     * @param params - отсутствуют
     * @return мапа типов
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsRightTypeList(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> rightSimple = new HashMap<String, Object>();
        rightSimple.put("SYSNAME", 1L);
        rightSimple.put("NAME", "Простое");
        list.add(rightSimple);

        Map<String, Object> rightProfile = new HashMap<String, Object>();
        rightProfile.put("SYSNAME", 2L);
        rightProfile.put("NAME", "Профильное");
        list.add(rightProfile);

        result.put(RESULT, list);
        return result;
    }

    /**
     * Дата провайдер получения списка прав у роли
     *
     * @param params <UL>
     *               <LI>OBJECTID - обязательный параметр; идентификатор владельца права</LI>
     *               <LI>RIGHTOWNER - обязательный параметр; тип владельца права</LI>
     *               <LI>FILTERPARAMS
     *               <LI>RIGHTNAME - необязательный параметр; имя права</LI>
     *               <LI>RIGHTTYPE - необязательный параметр; тип права: 1 - простое, 2 - профильное</LI>
     *               </LI>
     *               </UL>
     * @return список прав, каждый элемент модержит
     * <UL>
     * <LI>RIGHTID - идентификатор права</LI>
     * <LI>RIGHTNAME - имя права</LI>
     * <LI>RIGHTSYSNAME - системное имя права</LI>
     * <LI>RIGHTTYPE - тип права: 1 - простое, 2 - профильное</LI>
     * <LI>RIGHTTYPESTR - строковое наименование типа права: простое, профильное</LI>
     * </UL>
     * @throws Exception ошибка выполнения запроса
     */
    @WsMethod(requiredParams = {"OBJECTID", "RIGHTOWNER"})
    public Map<String, Object> dsUserRightBrowseListByParamEx(Map<String, Object> params) throws Exception {
        String rightOwner = getStringParam(params, "RIGHTOWNER");
        switch (rightOwner) {
            case "ROLE":
                params.put(RELATION_TABLE, " INNER JOIN CORE_RIGHTUSERROLE T1 on (T1.RIGHTID = T.RIGHTID) INNER JOIN CORE_USERROLE T2 on (T2.ROLEID = T1.ROLEID) ");
                params.put(RESTRICTION_FIELD, "T2.ROLEID");
                break;
            case "USERGROUP":
                params.put(RELATION_TABLE, " INNER JOIN CORE_RIGHTUSRGROUP T1 on (T1.RIGHTID = T.RIGHTID) INNER JOIN CORE_USERGROUP T2 on (T2.USERGROUPID = T1.USERGROUPID) ");
                params.put(RESTRICTION_FIELD, "T2.USERGROUPID");
                break;
            case "ACCOUNT":
                params.put(RELATION_TABLE, " INNER JOIN CORE_RIGHTACCOUNT T1 on (T1.RIGHTID = T.RIGHTID) INNER JOIN CORE_USERACCOUNT T2 on (T2.USERACCOUNTID = T1.USERACCOUNTID) ");
                params.put(RESTRICTION_FIELD, "T2.USERACCOUNTID");
                break;
            case "DEPARTMENT":
                params.put(RELATION_TABLE, " INNER JOIN CORE_RIGHTDEPT T1 on (T1.RIGHTID = T.RIGHTID) INNER JOIN DEP_DEPARTMENT T2 on (T2.DEPARTMENTID = T1.DEPARTMENTID) ");
                params.put(RESTRICTION_FIELD, "T2.DEPARTMENTID");
                break;
            default:
                Map<String, Object> result = new HashMap<>();
                result.put("Status", ERROR);
                result.put("Error", "Недопустимое значение типа владельца права.");
                return result;
        }
        return this.selectQuery("dsUserRightBrowseListByParamEx",
                "dsUserRightBrowseListByParamExCount", params
        );
    }
}
