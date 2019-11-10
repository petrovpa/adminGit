/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.km;

import com.bivgroup.services.b2bposws.facade.B2BBaseFacade;

import java.util.Map;

import ru.diasoft.services.inscore.aspect.impl.orgstruct.OrgStruct;
import ru.diasoft.services.inscore.aspect.impl.ortstructchecker.OrgStructChecker;
import ru.diasoft.services.inscore.aspect.impl.ownerright.OwnerRightView;
import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * @author andreyboo
 */
@OrgStructChecker(tableName = "SD_CHATORGSTRUCT", orgStructPKFieldName = "CHATORGSTRUCTID", objTablePKFieldName = "ID",
        value = @PRight(sysName = "RPAccessPOS_Branch", name = "Доступ по подразделению", restrictionFieldName = "ORGSTRTABLE.ORGSTRUCTID", paramName = "DEPARTMENTID",
                joinStr = " inner join SD_CHATORGSTRUCT ORGSTRTABLE on ((T.ID = ORGSTRTABLE.ID) and (ORGSTRTABLE.ISBLOCKED != 1 or ORGSTRTABLE.ISBLOCKED is null))")
)
@BOName("B2BChatList")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class ChatWithRightsFacade extends B2BBaseFacade {

    public static final String FIO_NODE_NAME = "$AUTHOR";
    public static final String CORE_USERACCOUNT = "coreUserAccount";
    public static final String LK_APPLICANT = "applicant";

    /**
     * Функция для получения списка чатов
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BKmBrowseChatListByParams(Map<String, Object> params) throws Exception {

        String filterError = "";
        String userCode = getUserCodeFromIncomingParams(params);

        String[] userParams = userCode.split("\\-");
        if (userParams.length != 2) {
            filterError = "Код неверный";
        }

        //обрабатываем тип пользователя
        if (filterError.isEmpty()) {
            String userType = userParams[0];
            Long userId = Long.parseLong(userParams[1]);
            switch (userType) {
                case CORE_USERACCOUNT: {
                    params.put("USERID", userId);
                    break;
                }
                case LK_APPLICANT: {
                    params.put("APPLICANTID", userId);
                    break;
                }
            }
        }

        // если пришло только ограничение по ФИО, то CUSTOMWHERE придет пустым и испортит все
        String customWhere = (String) params.get("CUSTOMWHERE");
        if (customWhere.isEmpty()) {
            params.put("CUSTOMWHERE", "1=1");
        }

        Map<String, Object> result = this.selectQuery("dsKmBrowseChatListByParams", params);

        if (result != null){

        }

        return result;
    }

    @WsMethod
    public Map<String, Object> dsKMSBBrowseChatAuthorsListByParams(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsKMSBBrowseChatAuthorsListByParams", null, params);
        return result;
    }

    private String getUserCodeFromIncomingParams(Map<String, Object> params) {

        try {
            Map<String, Object> filterParams = (Map<String, Object>) params.get("FILTERPARAMS");
            Map<String, Object> creatorParams = (Map<String, Object>) filterParams.get(FIO_NODE_NAME);
            Map<String, Object> user = null;
            for (String strict : creatorParams.keySet()) {
                user = (Map<String, Object>) creatorParams.get(strict);
            }
            if (user == null) return "";
            String value = (String) user.get("CODES");
            return value;

        } catch (Exception ex) {
            logger.error("Error while computing chat creator: " + ex.toString());
            return "";
        }
    }
}
