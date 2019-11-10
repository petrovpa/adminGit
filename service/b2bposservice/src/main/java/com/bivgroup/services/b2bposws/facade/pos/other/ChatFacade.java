/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.other;

import com.bivgroup.messages.Message;
import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bivgroup.services.b2bposws.facade.B2BPosServiceSessionController;
import com.bivgroup.sessionutils.SessionController;
import ru.diasoft.services.inscore.aspect.impl.orgstruct.OrgStruct;
import ru.diasoft.services.inscore.facade.RowStatus;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;
import static com.bivgroup.services.b2bposws.system.Constants.B2BPOSWS;

import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * @author npetrov
 */
@OrgStruct(tableName = "SD_CHATORGSTRUCT", orgStructPKFieldName = "CHATORGSTRUCTID", objTablePKFieldName = "ID")
@BOName("B2BChat")
public class ChatFacade extends B2BDictionaryBaseFacade {

	private static String SORT_BY_LAST_MESSAGE_DATE_PARAM="sortByLastMessageDate";

    public static final Map<String, String> CHAT_TRANSITION_SYSNAME_MAP = new HashMap<String, String>() {
        {
            put("FROM_ANSWERED_TO_NEW", "SD_CHAT_from_ANSWERED_to_NEW");
            put("FROM_NEW_TO_VIEWED", "SD_CHAT_from_NEW_to_VIEWED");
            put("FROM_ANSWERED_TO_CLOSED", "SD_CHAT_from_ANSWERED_to_CLOSED");
            put("FROM_VIEWED_TO_NEW", "SD_CHAT_from_VIEWED_to_NEW");
            put("FROM_VIEWED_TO_ANSWERED", "SD_CHAT_from_VIEWED_to_ANSWERED");
            put("FROM_VIEWED_TO_CLOSED", "SD_CHAT_from_VIEWED_to_CLOSED");
        }
    };
    
    @WsMethod(requiredParams = {"id"})
    public Map<String, Object> dsB2BChatLoadById(Map<String, Object> params) throws Exception {
        Long id = getLongParam(params, "id");
        Map<String, Object> result = dctFindById(MESSAGES_CHAT_ENTITY_NAME, id);
        return result;
    }

    /**
     * Метод для создания нового чата
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BCreateNewChat(Map<String, Object> params) throws Exception {
        boolean isCallFromGate = isCallFromGate(params);
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> userParams = new HashMap<>();
        userParams.put("LOGIN", login);
        Long employeeId = getLongParam(this.selectQueryAndGetOneValueFromFistItem("dsGetEmployeeIdByParam", userParams, "EMPLOYEEID"));
        Map<String, Object> newChat = new HashMap<>();
        newChat.putAll(params);
        newChat.put("userId", employeeId);
        //SessionController b2bController = new B2BPosServiceSessionController();
        //Map<String, Object> b2bSessionParams = b2bController.checkSession(b2bSessionId);
        Map<String, Object> result = new HashMap<>();
        result = dctCrudByHierarchy(MESSAGES_CHAT_ENTITY_NAME, newChat, isCallFromGate);

        Map<String, Object> orgStructParam = new HashMap<>();
        Long chatId = getLongParam(result, "id");
        orgStructParam.put("ID", chatId);
        Map<String,Object> res = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BChat_OrgStruct_createOrgStructInfo", orgStructParam, login, password);
        return result;
    }
        
    /**
     * Метод обновления последнего сообщения чата
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"chatId", "lastMessageId"})
    public Map<String, Object> dsB2BUpdateChatLastMessage(Map<String, Object> params) throws Exception {
        Long messageId = getLongParam(params, "lastMessageId");
        Long chatId = getLongParam(params, "chatId");
        // мапа, которая пойдет в запрос
        Map<String, Object> currentChat = dctFindById(MESSAGES_CHAT_ENTITY_NAME, chatId);
        Map<String, Object> result = new HashMap<>();
        if (!currentChat.isEmpty()) {
            currentChat.put("lastMessageId", messageId);
            currentChat.remove("applicantId_EN");
            currentChat.remove("lastMessageId_EN");
            currentChat.remove("stateId_EN");
            markAsModified(currentChat);
            if (messageId != null) {
                result = dctCrudByHierarchy(MESSAGES_CHAT_ENTITY_NAME, currentChat);
            }
        }
        return result;
    }
    
    /**
     * Получения списка сообщений с прикрепленными документами по теме
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CHATID"})
    public Map<String, Object> dsMessageLoadWithDocumentsByChatId(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        List<Map<String, Object>> chatMessages = this.selectQueryAndGetListFromResultMap("dsMessageBrowseListByParamEx", params);
        if ((null != chatMessages) && (!chatMessages.isEmpty())) {
            for (Map<String, Object> message : chatMessages) {
                if (message != null) {
                    params.put("ID", message.get("ID"));
                    List<Map<String, Object>> documents = this.callServiceAndGetListFromResultMap(B2BPOSWS, "dsMessageDocumentBrowseListByParam", params, login, password);
                    message.put("documents", documents);
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("MESSAGELIST", chatMessages);
        return result;
    }

    /**
     * Hibernate метод получения списка сообщений с прикрепленными документами по теме
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"chatId"})
    public Map<String, Object> dsDCTMessageWithDocumentsBrowseByChatId(Map<String, Object> params) throws Exception {
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        boolean sortByLastMessageDate=getBooleanParam(params, SORT_BY_LAST_MESSAGE_DATE_PARAM, false);
        List<Map<String, Object>> messages = dctFindByExample(MESSAGE_ENTITY_NAME, params, false);
        if ((null != messages) && (!messages.isEmpty())) {
            for (Map<String, Object> message : messages) {
                if (message != null) {
                    Map<String, Object> attachListQueryParams = new HashMap<>();
                    attachListQueryParams.put("ID", message.get("id"));
                    List<Map<String, Object>> documents = this.callServiceAndGetListFromResultMap(B2BPOSWS, "dsMessageDocumentBrowseListByParam", attachListQueryParams, login, password);
                    processDocListForUpload(documents, params, login, password);
                    message.put("documents", documents);
                }
            }
            if(sortByLastMessageDate) {
            	sortByDateFieldName(messages,"createDate",false,true);
            }
            parseDatesAfterDictionaryCalls(messages);
        }
        Map<String, Object> result = new HashMap<>();
        result.put(RESULT, messages);
        return result;
    }

    private void takeCreateDateToChat(List<Map<String, Object>> chats){
    	for (Map<String, Object> entry:chats) {
    		Map<String, Object> o1Message=(Map<String, Object>)entry.get("lastMessageId_EN");
    		if (o1Message!=null) {
    			Date createDate=(Date)o1Message.get("createDate");
    			entry.put("createDate",createDate);
    		}
    	}
    }
    
    /**
     * Получения списка чатов
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"applicantId"})
    public Map<String, Object> dsChatLoadByClientId(Map<String, Object> params) throws Exception {
        boolean sortByLastMessageDate=getBooleanParam(params, SORT_BY_LAST_MESSAGE_DATE_PARAM, false);
        List<Map<String, Object>> chats = dctFindByExample(MESSAGES_CHAT_ENTITY_NAME, params, false);
        if(sortByLastMessageDate) {
        	takeCreateDateToChat(chats);
        	sortByDateFieldName(chats,"createDate",false,true);
        }
        parseDatesAfterDictionaryCalls(chats);
        Map<String, Object> result = new HashMap<>();
        result.put(RESULT, chats);
        return result;
    }
    
    /**
     * Дата провайдер для получения списка типов обращений 
     *
     * @param params
     * @return возвращает маппу доступных типов
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BChatGetTopicType(Map<String, Object> params) throws Exception {

        Map<String, Object> result = new HashMap<>();
        result = this.selectQuery("dsChatGetTopicType", params);
        return result;
    }
    
    @WsMethod(requiredParams = {"id"})
    public void dsChatMakeTransitionFromAnsweredToNew(Map<String, Object> params) throws Exception {
        Long entityId = getLongParam(params, "id");
        String entityName = MESSAGES_CHAT_ENTITY_NAME;
        String transitionSysName = CHAT_TRANSITION_SYSNAME_MAP.get("FROM_ANSWERED_TO_NEW");
        dctMakeTransition(entityName, entityId, transitionSysName);
    }

    @WsMethod(requiredParams = {"id"})
    public void dsChatMakeTransitionFromNewToViewed(Map<String, Object> params) throws Exception {
        Long entityId = getLongParam(params, "id");
        String entityName = MESSAGES_CHAT_ENTITY_NAME;
        String transitionSysName = CHAT_TRANSITION_SYSNAME_MAP.get("FROM_NEW_TO_VIEWED");
        dctMakeTransition(entityName, entityId, transitionSysName);
    }

    @WsMethod(requiredParams = {"id"})
    public void dsChatMakeTransitionFromAnsweredToClosed(Map<String, Object> params) throws Exception {
        Long entityId = getLongParam(params, "id");
        String entityName = MESSAGES_CHAT_ENTITY_NAME;
        String transitionSysName = CHAT_TRANSITION_SYSNAME_MAP.get("FROM_ANSWERED_TO_CLOSED");
        dctMakeTransition(entityName, entityId, transitionSysName);
    }

    @WsMethod(requiredParams = {"id"})
    public void dsChatMakeTransitionFromViewedToNew(Map<String, Object> params) throws Exception {
        Long entityId = getLongParam(params, "id");
        String entityName = MESSAGES_CHAT_ENTITY_NAME;
        String transitionSysName = CHAT_TRANSITION_SYSNAME_MAP.get("FROM_VIEWED_TO_NEW");
        dctMakeTransition(entityName, entityId, transitionSysName);
    }

    @WsMethod(requiredParams = {"id"})
    public void dsChatMakeTransitionFromViewedToAnswered(Map<String, Object> params) throws Exception {
        Long entityId = getLongParam(params, "id");
        String entityName = MESSAGES_CHAT_ENTITY_NAME;
        String transitionSysName = CHAT_TRANSITION_SYSNAME_MAP.get("FROM_VIEWED_TO_ANSWERED");
        dctMakeTransition(entityName, entityId, transitionSysName);
    }

    @WsMethod(requiredParams = {"id"})
    public void dsChatMakeTransitionFromViewedToClosed(Map<String, Object> params) throws Exception {
        Long entityId = getLongParam(params, "id");
        String entityName = MESSAGES_CHAT_ENTITY_NAME;
        String transitionSysName = CHAT_TRANSITION_SYSNAME_MAP.get("FROM_VIEWED_TO_CLOSED");
        dctMakeTransition(entityName, entityId, transitionSysName);
    }

}
