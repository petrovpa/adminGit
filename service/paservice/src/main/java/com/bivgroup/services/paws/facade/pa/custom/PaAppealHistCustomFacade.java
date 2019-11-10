/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.paws.facade.pa.custom;

import com.bivgroup.services.paws.system.Constants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 *
 * @author ilich
 */
@BOName("PaAppealHistCustom")
public class PaAppealHistCustomFacade extends BaseFacade {

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>APPEALID - ИД обращения</LI>
     * <LI>APPEALHISTID - ИД записи</LI>
     * <LI>ISFAVORITE - Является избранным</LI>
     * <LI>ISUNREADED - Является не прочтенным</LI>
     * <LI>MESSAGEDATE - Дата отправки сообщения</LI>
     * <LI>MESSAGETEXT - Текст сообщения</LI>
     * <LI>USERACCOUNTID - Пользователь B2B, отправитель (НЕ инициатор)</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>APPEALID - ИД обращения</LI>
     * <LI>APPEALHISTID - ИД записи</LI>
     * <LI>ISFAVORITE - Является избранным</LI>
     * <LI>ISUNREADED - Является не прочтенным</LI>
     * <LI>MESSAGEDATE - Дата отправки сообщения</LI>
     * <LI>MESSAGETEXT - Текст сообщения</LI>
     * <LI>USERACCOUNTID - Пользователь B2B, отправитель (НЕ инициатор)</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsPaAppealHistBrowseListByParamEx(Map<String, Object> params) throws Exception {
        Map<String, Object> p = new HashMap<String, Object>();
        p.putAll(params);
        p.put("INITIATORID", params.get("CURRENT_USERID"));
        Map<String, Object> result = this.selectQuery("dsPaAppealHistBrowseListByParamEx", "dsPaAppealHistBrowseListByParamExCount", p);
        return result;
    }

    @WsMethod(requiredParams = {"APPEALID"})
    public Map<String, Object> dsPaAppealHistMarkMessagesAsReaded(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        Map<String, Object> appealHistParams = new HashMap<String, Object>();
        appealHistParams.put("APPEALID", params.get("APPEALID"));
        Map<String, Object> appealHistRes = this.callService(Constants.PAWS, "dsPaAppealHistBrowseListByParam", appealHistParams, login, password);
        List<Map<String, Object>> appealHistList = WsUtils.getListFromResultMap(appealHistRes);
        if (appealHistList != null) {
            for (Map<String, Object> bean : appealHistList) {
                if ((bean.get("ISUNREADED") != null) && (Long.valueOf(bean.get("ISUNREADED").toString())).longValue() == 1) {
                    Map<String, Object> appealHistUpdParams = new HashMap<String, Object>();
                    appealHistUpdParams.put("APPEALHISTID", bean.get("APPEALHISTID"));
                    appealHistUpdParams.put("ISUNREADED", 0L);
                    this.callService(Constants.PAWS, "dsPaAppealHistUpdate", appealHistUpdParams, login, password);
                }
            }
        }
        return new HashMap<String, Object>();
    }

    @WsMethod(requiredParams = {"APPEALHISTIDLIST"})
    public Map<String, Object> dsPaAppealHistMarkMessagesAsReadedByIdsList(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        List<Map<String, Object>> appealHistList = (List<Map<String, Object>>) params.get("APPEALHISTIDLIST");
        for (Map<String, Object> bean : appealHistList) {
            if ((bean.get("ISUNREADED") != null) && (Long.valueOf(bean.get("ISUNREADED").toString())).longValue() == 1) {
                Map<String, Object> appealHistUpdParams = new HashMap<String, Object>();
                appealHistUpdParams.put("APPEALHISTID", bean.get("APPEALHISTID"));
                appealHistUpdParams.put("ISUNREADED", 0L);
                this.callService(Constants.PAWS, "dsPaAppealHistUpdate", appealHistUpdParams, login, password);
            }
        }
        return new HashMap<String, Object>();
    }

}
