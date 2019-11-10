/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.admin.lk;

import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.Map;

/**
 * @author petrovpa
 */
@BOName("B2BAdminLKInsEventMessage")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class B2BAdminLKInsEventMessageFacade extends B2BAdminLKBaseFacade {

    /**
     * Функция для получения списка договоров
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminLkBrowseInsEventMessageListByParams(Map<String, Object> params) throws Exception {
        Map<String, Object> result = selectQuery("dsAdminLkBrowseInsEventMsgListByParams", params);
        return result;
    }
}
