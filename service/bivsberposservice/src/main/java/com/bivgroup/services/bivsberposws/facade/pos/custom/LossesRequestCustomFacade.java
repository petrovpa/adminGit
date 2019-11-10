/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.bivsberposws.facade.pos.custom;

import com.bivgroup.services.bivsberposws.system.Constants;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.velocity.runtime.directive.Foreach;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.aspect.impl.ownerright.OwnerRightView;
import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.aspect.impl.version.NodeVersion;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.util.CopyUtils;

/**
 *
 * @author averichevsm
 */
@Auth(onlyCreatorAccess = false)
@ProfileRights({
        @PRight(sysName="RPAccessPOS_Branch",
                name="Доступ по подразделению",
                joinStr="  inner join LOSS_REQUESTORGSTRUCT AOS on (t.REQUESTID = AOS.REQUESTID) inner join INS_DEPLVL DEPLVL on (AOS.ORGSTRUCTID = DEPLVL.OBJECTID) ",
                restrictionFieldName="DEPLVL.PARENTID",
                paramName="DEPARTMENTID")})
@OwnerRightView()
@BOName("LossesRequestCustom")
public class LossesRequestCustomFacade extends BaseFacade {
    
    private static final String BIVSBERPOS_SERVICE_NAME = Constants.BIVSBERPOSWS; 
    private static final String CRMWS_SERVICE_NAME = Constants.CRMWS;
    private static final String ADMINIWS_SERVICE_NAME = Constants.ADMINWS;
    
    private void clearEmptyValue(Map<String, Object> params, String paramName) {
        if ((params.get(paramName) != null) && params.get(paramName).toString().equalsIgnoreCase("")) {
            params.put(paramName, null);
        }
    }
    
    protected Double getDoubleParam(Object bean) {
        if (bean != null && bean.toString().trim().length() > 0) {
            return Double.valueOf(bean.toString());
        } else {
            return null;
        }
    }    
    
    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BENEFICIARID - ИД выгодопреобретателя</LI>
     * <LI>BIC - БИК получателя</LI>
     * <LI>BKNUM - Номер банковской карты получателя</LI>
     * <LI>CALCAMOUNT - Рассчетная сумма выплаты</LI>
     * <LI>CANCELCOMMENT - Комментарий к отказу в возврате</LI>
     * <LI>CANCELREASON - Причина отказа возврата</LI>
     * <LI>CANCELREASONID - ИД причины отказа возврата</LI>
     * <LI>CAUSES - Обстоятельства и причины события</LI>
     * <LI>CONFIRMDOCS - Документы подтверждающие событие</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CREATEUSERROLE - роль пользователя создавшего заявку</LI>
     * <LI>CULPRIT - Виновник</LI>
     * <LI>CURACCOUNT - Расчетный счет получателя</LI>
     * <LI>DECISIONCSKO - Решение ЦСКО</LI>
     * <LI>DECISIONSK - Решение СК</LI>
     * <LI>DECLARERID - ИД заявителя</LI>
     * <LI>EVENTDATE - Дата события</LI>
     * <LI>FACEACCOUNT - Лицевой счет получателя</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>INN - ИНН получателя</LI>
     * <LI>INSOBJADDRESS - Адрес объекта страхования или события</LI>
     * <LI>INSOBJNAME - Наименование объекта страхования</LI>
     * <LI>INSOBJTYPE - Тип объекта страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>ISOTHERINS - Признак страхования в другой страховой</LI>
     * <LI>ISTHIRDPART - Признак предъявления претензий третьим лицам</LI>
     * <LI>KORACCOUNT - Корр. счет</LI>
     * <LI>KPP - КПП</LI>
     * <LI>LOSSES - Причиненные убытки</LI>
     * <LI>LOSSESAMOUNT - Предположительная сумма убытков</LI>
     * <LI>NAMEOTHERINS - Наименование другой страховой компании</LI>
     * <LI>REQUESTDATE - Дата заявки на возврат</LI>
     * <LI>REQUESTNODEID - ИД ноды</LI>
     * <LI>REQUESTNUM - Номер заявки</LI>
     * <LI>STATEID - Состояние заявки</LI>
     * <LI>TOTALAMOUNT - Итоговая сумма убытка</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>USERCOMMENT - Комментарии пользователя</LI>
     * <LI>USERCOMMENT2 - Комментарии сотрудников СГИ</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WHENWHOCLAIM - Кому и когда заявлено о событии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BENEFICIARID - ИД выгодопреобретателя</LI>
     * <LI>BIC - БИК получателя</LI>
     * <LI>BKNUM - Номер банковской карты получателя</LI>
     * <LI>CALCAMOUNT - Рассчетная сумма выплаты</LI>
     * <LI>CANCELCOMMENT - Комментарий к отказу в возврате</LI>
     * <LI>CANCELREASON - Причина отказа возврата</LI>
     * <LI>CANCELREASONID - ИД причины отказа возврата</LI>
     * <LI>CAUSES - Обстоятельства и причины события</LI>
     * <LI>CONFIRMDOCS - Документы подтверждающие событие</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CREATEUSERROLE - роль пользователя создавшего заявку</LI>
     * <LI>CULPRIT - Виновник</LI>
     * <LI>CURACCOUNT - Расчетный счет получателя</LI>
     * <LI>DECISIONCSKO - Решение ЦСКО</LI>
     * <LI>DECISIONSK - Решение СК</LI>
     * <LI>DECLARERID - ИД заявителя</LI>
     * <LI>EVENTDATE - Дата события</LI>
     * <LI>FACEACCOUNT - Лицевой счет получателя</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>INN - ИНН получателя</LI>
     * <LI>INSOBJADDRESS - Адрес объекта страхования или события</LI>
     * <LI>INSOBJNAME - Наименование объекта страхования</LI>
     * <LI>INSOBJTYPE - Тип объекта страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>ISOTHERINS - Признак страхования в другой страховой</LI>
     * <LI>ISTHIRDPART - Признак предъявления претензий третьим лицам</LI>
     * <LI>KORACCOUNT - Корр. счет</LI>
     * <LI>KPP - КПП</LI>
     * <LI>LOSSES - Причиненные убытки</LI>
     * <LI>LOSSESAMOUNT - Предположительная сумма убытков</LI>
     * <LI>NAMEOTHERINS - Наименование другой страховой компании</LI>
     * <LI>REQUESTDATE - Дата заявки на возврат</LI>
     * <LI>REQUESTNODEID - ИД ноды</LI>
     * <LI>REQUESTNUM - Номер заявки</LI>
     * <LI>STATEID - Состояние заявки</LI>
     * <LI>TOTALAMOUNT - Итоговая сумма убытка</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>USERCOMMENT - Комментарии пользователя</LI>
     * <LI>USERCOMMENT2 - Комментарии сотрудников СГИ</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WHENWHOCLAIM - Кому и когда заявлено о событии</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesRequestBrowseListByParamEx(Map<String, Object> params) throws Exception {
        clearEmptyValue(params, "CONTRNUM");
        clearEmptyValue(params, "NAME");
        clearEmptyValue(params, "MIDDLENAME");
        clearEmptyValue(params, "SURNAME");
        if (params.get("ORDERBY") == null) {
            params.put("ORDERBY", "REQUESTNUM DESC");            
        }
        if (params.get("REQUESTDATE") != null) {
            Double reqDate = getDoubleParam(params.get("REQUESTDATE"));
            
            params.put("STARTREQUESTDATE", reqDate);
            params.put("FINISHREQUESTDATE", reqDate + 1.0);
            params.remove("REQUESTDATE");
        }
        Map<String,Object> result = this.selectQuery("dsLossesRequestBrowseListByParamEx", "dsLossesRequestBrowseListByParamExCount", params);
        return result;
    }    
    
    /**
     * Изменить объект (необходимо для корректного сохранения комментариев с
     * формы)
     *
     * @author ilich
     * @param params
     * <UL>
     * <LI>BANKACCOUNTID - ИД расчетного счета банка получателя</LI>
     * <LI>CALCAMOUNT - Рассчетная сумма возврата</LI>
     * <LI>CANCELCOMMENT - Комментарий к отказу в возврате</LI>
     * <LI>CANCELREASON - Причина отказа возврата</LI>
     * <LI>CANCELREASONID - ИД причины отказа возврата</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CREATEUSERROLE - роль пользователя создавшего заявку</LI>
     * <LI>DECISIONACTUARY - Решение актуария</LI>
     * <LI>DECISIONCOMMERCIAL - Решение сотрудника коммерческого отдела</LI>
     * <LI>DECISIONLAWYER - Решение адвоката</LI>
     * <LI>DECISIONTORETURN - Решение о возврате</LI>
     * <LI>EARLYREPAYMENTDATE - Дата досрочного погашения долга</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>INTEGRLOGID - ИД лога выгрузки, в который попала зявка при
     * выгрузке</LI>
     * <LI>ISEARLYREPAYMENT - Флаг раннего погашения долга</LI>
     * <LI>ISNEEDCLAIM - Флаг подтверждения навязаной страховки</LI>
     * <LI>ISNEEDDOPS - Флаг необходимости печати допса (взводится после
     * добавления допса в таблицу печатных документов после расчета)</LI>
     * <LI>ISNEEDLAWSUIT - Флаг подтверждения претензий к качеству
     * обслуживания</LI>
     * <LI>LOSSESAMOUNT - Сумма зарегистрированных убытков</LI>
     * <LI>OWNERID - ИД ответственного сотрудника</LI>
     * <LI>RECIPIENTMIDDLENAME - Отчество получателя</LI>
     * <LI>RECIPIENTNAME - Имя получателя</LI>
     * <LI>RECIPIENTSURNAME - Фамилия получателя</LI>
     * <LI>REFUNDREASONID - ИД причины возврата</LI>
     * <LI>REFUNDSIZETYPEID - Объем возврата</LI>
     * <LI>REFUNDTYPEID - Тип возврата</LI>
     * <LI>REJECTCOMMENT - Комментарий причины отказа</LI>
     * <LI>REJECTREASONID - Причина отказа</LI>
     * <LI>REQUESTDATE - Дата заявки на возврат</LI>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * <LI>REQUESTNUM - Номер заявки</LI>
     * <LI>STATEID - Состояние заявки</LI>
     * <LI>TOTALAMOUNT - Итоговая сумма возврата</LI>
     * <LI>UIPIMONTH - Количество неиспользованных месяцев страхования</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>USERCOMMENT - Комментарии пользователя</LI>
     * <LI>USERCOMMENT2 - Комментарии сотрудников СГИ</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTID"})
    public Map<String, Object> dsLossesRequestUpdateEx(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        if (params.get("USERCOMMENT") != null) {
            params.put("USERCOMMENT", params.get("USERCOMMENT").toString().replaceAll("aENTERz", "\n"));
        }
        if (params.get("USERCOMMENT2") != null) {
            params.put("USERCOMMENT2", params.get("USERCOMMENT2").toString().replaceAll("aENTERz", "\n"));
        }
        return this.callService(BIVSBERPOS_SERVICE_NAME, "dsLossesRequestUpdate", params, login, password);
    }  
    
    @WsMethod(requiredParams = {"REQUESTID"})
    public Map<String, Object> dsLossesRequestNACreateUserReset(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesRequestNACreateUserReset", params);
        result.put("REQUESTID", params.get("REQUESTID"));
        return result;

    }    
    
    private Long findRoleIdBySysName(String roleSysName, String login, String password) throws Exception {
        Long result = null;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("SEARCHTEXT", roleSysName);
        queryParams.put("SEARCHCOLUMN", "ROLESYSNAME");
        Map<String, Object> qres = this.callService(ADMINIWS_SERVICE_NAME, "admUserRole", queryParams, login, password);
        if (qres.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) qres.get(RESULT);
            if (resList.get(0) != null) {
                if (resList.get(0).get("ROLEID") != null) {
                    result = Long.valueOf(resList.get(0).get("ROLEID").toString());
                }
            }
        }
        return result;
    }

    private List<Map<String, Object>> findUsersByRole(Long roleId, String login, String password) throws Exception {
        List<Map<String, Object>> result = null;
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("ROLEID", roleId);
        Map<String, Object> qres = this.callService(ADMINIWS_SERVICE_NAME, "dsGetUsersByRole", queryParams, login, password);
        if (qres.get(RESULT) != null) {
            result = (List<Map<String, Object>>) qres.get(RESULT);
        }
        return result;
    }

    private String findEmailByUserId(Long userId, String login, String password) throws Exception {
        String result = "";
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("USERID", userId);
        Map<String, Object> qres = this.callService(ADMINIWS_SERVICE_NAME, "admAccountFind", queryParams, login, password);
        if (qres.get(RESULT) != null) {
            List<Map<String, Object>> resList = (List<Map<String, Object>>) qres.get(RESULT);
            if (resList.get(0) != null) {
                if ((resList.get(0).get("STATUS") != null)&&(resList.get(0).get("STATUS").toString().equalsIgnoreCase("ACTIVE"))) {
                    if (resList.get(0).get("EMAIL") != null) {
                        result = resList.get(0).get("EMAIL").toString();
                    }
                }
            }
        }
        return result;
    }

    
    @WsMethod(requiredParams = {"ROLESYSNAME"})
    public Map<String, Object> dsGetUsersByRoleSysName(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String roleName = params.get("ROLESYSNAME").toString();
        Long roleId = findRoleIdBySysName(roleName, login, password);
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("ROLE", roleId);//dsGetUsersByRole
        Map<String, Object> qres = this.callService(ADMINIWS_SERVICE_NAME, "admAccountFind", queryParams, login, password);
        return qres;
    }    
    
    @WsMethod(requiredParams = {"REQUESTID"})
    public Map<String, Object> dsGetRoleListByRequestId(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Long requestId = Long.valueOf(params.get("REQUESTID").toString());
        if (params.get("depByRoleList") == null) {
            //todo: можно потом начитать справочник здесь.
           return null;             
        }
        List<Map<String, Object>> depByRoleList = (List<Map<String, Object>>) params.get("depByRoleList");
        CopyUtils.sortByLongFieldName(depByRoleList, "depid");
              
        Map<String, Object> qparams = new HashMap<String, Object>();
        qparams.put("REQUESTID", requestId);
        Map<String, Object> qres = this.callService(BIVSBERPOS_SERVICE_NAME, "dsLossesRequestOrgStructBrowseListByParam", qparams, login, password);
        String roleListStr = "";                
        if (qres != null) {
            if (qres.get(RESULT) != null) {
                List<Map<String, Object>> orgList = (List<Map<String, Object>>) qres.get(RESULT);
                for (Map<String, Object> orgMap : orgList) {
                    Long depId = Long.valueOf(orgMap.get("ORGSTRUCTID").toString());
                    List<Map<String, Object>> filterDepList = CopyUtils.filterSortedListByLongFieldName(depByRoleList, "depid", depId);
                    for (Map<String, Object> depMap : filterDepList) {
                        roleListStr = roleListStr + ";" + depMap.get("roleSysName");
                    }
                }
            }
        }
        if (!roleListStr.isEmpty()) {
            // удаляем первую ";"
            roleListStr = roleListStr.substring(1);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("ROLELIST", roleListStr);        
        return result;
    }
    
    
    @WsMethod(requiredParams = {"ROLELIST"})
    public Map<String, Object> dsGetUsersDataByRoleList(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String roleStrList = params.get("ROLELIST").toString();
        String[] roleList = roleStrList.split(";");
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Object> emailCach = new HashMap<String, Object>();
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        result.put(RESULT, resList);
        String emailStrList = "";
        for (String roleName : roleList) {
            Long roleId = findRoleIdBySysName(roleName, login, password);
            List<Map<String, Object>> userList = findUsersByRole(roleId, login, password);
            if (userList != null) {
                for (Map<String, Object> userMap : userList) {
                    if (userMap.get("USERID") != null) {

                        Long userId = Long.valueOf(userMap.get("USERID").toString());
                        String eMail = findEmailByUserId(userId, login, password);
                        if ((eMail != null) && (!eMail.equalsIgnoreCase(""))) {
                            if (emailCach.get(eMail) == null) {
                                emailCach.put(eMail, userId);
                                if (emailStrList.equalsIgnoreCase("")) {
                                    emailStrList = eMail;
                                } else {
                                    emailStrList = emailStrList + "," + eMail;
                                }
                                Map<String, Object> user = new HashMap<String, Object>();
                                user.put("USERID", userId);
                                user.put("EMAIL", eMail);
                                user.put("FIO", userMap.get("FIO"));
                                resList.add(user);
                            }
                        }
                    }
                }
            }

        }
        Map<String, Object> user = new HashMap<String, Object>();
        user.put("EMAILSTRLIST", emailStrList);
        resList.add(user);
        return result;
    }    
}
