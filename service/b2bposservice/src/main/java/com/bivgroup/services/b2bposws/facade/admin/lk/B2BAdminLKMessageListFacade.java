package com.bivgroup.services.b2bposws.facade.admin.lk;

import ru.diasoft.services.inscore.aspect.impl.customwhere.CustomWhere;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.utils.XMLUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Фасад для работы с сообщениями СБСЖ ЛК
 */
@BOName("B2BAdminLKMessageList")
@CustomWhere(customWhereName = "CUSTOMWHERE")
public class B2BAdminLKMessageListFacade extends B2BAdminLKBaseFacade {
    /**
     * Функция для получения списка сообщений
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminLkBrowseMessageListByParams(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsAdminLkBrowseMessageListByParams", params);
        return result;
    }

    /**
     * Функция для получения списка чатов
     *
     * @param params список входных параметров
     * @return Возвращает данные в грид с их количеством
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminLkBrowseChatListByParams(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsAdminLkBrowseChatMessageListByParams", params);
        return result;
    }

    /**
     * Дата провайдер для получения списка состояний сообщений
     *
     * @param params
     * @return возвращает маппу доступных состояний
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsAdminLKBrowseMessageStateListByParams(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<>();
        params.put("TYPEID", TYPEID_STATE_MESSAGE);
        result = this.selectQuery("dsAdminLKBrowseStateListByParams", params);

        return result;
    }
}
