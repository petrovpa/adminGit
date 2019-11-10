package com.bivgroup.services.b2bposws.facade.pos.other;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.diasoft.services.inscore.facade.RowStatus;

import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import static com.bivgroup.services.b2bposws.system.Constants.B2BPOSWS;

import java.util.Date;

import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.annotations.WsMethod;


/**
 * @author adanilov
 */
public class MessageDictionaryFacade extends B2BDictionaryBaseFacade {

    /**
     * Метод для создания нового сообщения
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsDCTCreateNewMessage(Map<String, Object> params) throws Exception {
        boolean isCallFromGate = isCallFromGate(params);
        Map<String, Object> newMessage = new HashMap<>();
        newMessage.putAll(params);
        Map<String, Object> result = new HashMap<>();
        result = dctCrudByHierarchy(MESSAGE_ENTITY_NAME, newMessage, isCallFromGate);
        return result;
    }

    @WsMethod(requiredParams = {"id"})
    public Map<String, Object> dsDCTMessageLoadById(Map<String, Object> params) throws Exception {
        Long id = getLongParam(params, "id");
        Map<String, Object> result = dctFindById(MESSAGE_ENTITY_NAME, id);
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsDCTMessageCreate(Map<String, Object> params) throws Exception {

        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        boolean isCallFromGate = isCallFromGate(params);

        Map<String, Object> result = new HashMap<String, Object>();

        // тянем userId и по нему находим запись в таблице DEP_EMPLOYEE его и сохраняем.
        Object userId = params.get("SESSION_USERACCOUNTID");
        if (userId != null) {
            Map<String, Object> queryParams = new HashMap<String, Object>();
            queryParams.put("LOGIN", login);
            Map<String, Object> qres = this.callService(WsConstants.ADMINWS, "admAccountFind", queryParams, login, password);
            if (qres != null) {
                List<Map<String, Object>> listParam = getListParam(qres, RESULT);
                Map<String, Object> firtsAccount = listParam.get(0);
                userId = getLongParam(firtsAccount, "OBJECTID");
            }
        }

        // создать senderid
        Map<String, Object> senderParams = new HashMap<String, Object>();
        senderParams.put("userId", userId); // SESSIONIDFORCALL
        senderParams.put("clientProfileId", params.get("CLIENTPROFILEID")); // SESSIONIDFORCALL
        senderParams.put("channelId", 1);
        Long senderId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsDCTCreateNewMessageCorrespondent", senderParams, login, password, "id"));
        if (senderId == null) {
            result.put(ERROR, "message: error create sender");
            return result;
        }

        // получить идентификатор сообщения
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put("SYSTEMBRIEF", "messageIdentifierAutoNum");
        String identifierMsg = null;
        Map res1 = this.callService(COREWS, "dsNumberFindByMask", reqParams, login, password);
        if (res1 != null && res1.containsKey(RESULT)) {
            identifierMsg = (String) res1.get(RESULT);
        }

        // создать сообщение
        Map<String, Object> messageParams = new HashMap<String, Object>();
        messageParams.putAll(params);
        messageParams.put("createDate", new Date());
        messageParams.put("senderId", senderId);
        messageParams.put("typeMessage", 0);
        messageParams.put("identifierMsg", identifierMsg);

        Map<String, Object> newMessage = new HashMap<String, Object>();
        newMessage = this.dsDCTCreateNewMessage(messageParams);
        Long messageId = getLongParam(newMessage, "id");
        // обновляем lastMessageId
        if (messageId != null && newMessage.get("chatId") != null) {
            Long chatId = getLongParam(newMessage, "chatId");
            Map<String, Object> chat =  dctFindById(MESSAGES_CHAT_ENTITY_NAME, chatId);
            chat.put("lastMessageId", messageId);
            chat.put(ROWSTATUS_PARAM_NAME, RowStatus.MODIFIED.getId());
            dctCrudByHierarchy(MESSAGES_CHAT_ENTITY_NAME, chat);
        }

        if (messageId == null) {
            result.put(ERROR, "message: error create message");
            return result;
        }

        // проверка получателя сообщения
        Long recipientClientProfileId = getLongParam(params.get("RECIPIENTCLIENTPROFILEID"));
        if (recipientClientProfileId == null) {
            result.put("id", messageId);
            return result;
        }
        // создать получателя сообщения
        Map<String, Object> recipientParams = new HashMap<String, Object>();
        recipientParams.put("clientProfileId", recipientClientProfileId);
        recipientParams.put("channelId", 1);
        recipientParams.put("messageId", messageId);
        Long recipientId = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsDCTCreateNewMessageCorrespondent", recipientParams, login, password, "id"));
        if (recipientId == null) {
            result.put("Error", "message: error create recipient message");
            return result;
        }
        result.put("id", messageId);
        result.put("pdDeclarationId", params.get("pdDeclarationId")); // для получения ссылки на заявление при отказе в расторжении

        return result;
    }

}
