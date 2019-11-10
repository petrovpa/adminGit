/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesChats
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="LOSS_CHATS",idFieldName="CHATSID")
@BOName("LossesChats")
public class LossesChatsFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CHATSID - ИД записи</LI>
     * <LI>CHATNUM - Номер чата</LI>
     * <LI>CREATEDATE - Дата создания записи чата</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>MESSAGE - Сообщение чата</LI>
     * <LI>MESSAGEID - Номер сообщения чата</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>UPDATEDATE - Дата обновления записи чата</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего запись чата</LI>
     * <LI>USERNAME - ФИО пользователя</LI>
     * <LI>USERROLEID - ИД роли пользователя</LI>
     * <LI>USERROLESYSNAME - Системное имя роли пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHATSID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesChatsCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesChatsInsert", params);
        result.put("CHATSID", params.get("CHATSID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CHATSID - ИД записи</LI>
     * <LI>CHATNUM - Номер чата</LI>
     * <LI>CREATEDATE - Дата создания записи чата</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>MESSAGE - Сообщение чата</LI>
     * <LI>MESSAGEID - Номер сообщения чата</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>UPDATEDATE - Дата обновления записи чата</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего запись чата</LI>
     * <LI>USERNAME - ФИО пользователя</LI>
     * <LI>USERROLEID - ИД роли пользователя</LI>
     * <LI>USERROLESYSNAME - Системное имя роли пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHATSID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CHATSID"})
    public Map<String,Object> dsLossesChatsInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesChatsInsert", params);
        result.put("CHATSID", params.get("CHATSID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CHATSID - ИД записи</LI>
     * <LI>CHATNUM - Номер чата</LI>
     * <LI>CREATEDATE - Дата создания записи чата</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>MESSAGE - Сообщение чата</LI>
     * <LI>MESSAGEID - Номер сообщения чата</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>UPDATEDATE - Дата обновления записи чата</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего запись чата</LI>
     * <LI>USERNAME - ФИО пользователя</LI>
     * <LI>USERROLEID - ИД роли пользователя</LI>
     * <LI>USERROLESYSNAME - Системное имя роли пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHATSID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CHATSID"})
    public Map<String,Object> dsLossesChatsUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesChatsUpdate", params);
        result.put("CHATSID", params.get("CHATSID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CHATSID - ИД записи</LI>
     * <LI>CHATNUM - Номер чата</LI>
     * <LI>CREATEDATE - Дата создания записи чата</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>MESSAGE - Сообщение чата</LI>
     * <LI>MESSAGEID - Номер сообщения чата</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>UPDATEDATE - Дата обновления записи чата</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего запись чата</LI>
     * <LI>USERNAME - ФИО пользователя</LI>
     * <LI>USERROLEID - ИД роли пользователя</LI>
     * <LI>USERROLESYSNAME - Системное имя роли пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHATSID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CHATSID"})
    public Map<String,Object> dsLossesChatsModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesChatsUpdate", params);
        result.put("CHATSID", params.get("CHATSID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CHATSID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CHATSID"})
    public void dsLossesChatsDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesChatsDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CHATSID - ИД записи</LI>
     * <LI>CHATNUM - Номер чата</LI>
     * <LI>CREATEDATE - Дата создания записи чата</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>MESSAGE - Сообщение чата</LI>
     * <LI>MESSAGEID - Номер сообщения чата</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>UPDATEDATE - Дата обновления записи чата</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего запись чата</LI>
     * <LI>USERNAME - ФИО пользователя</LI>
     * <LI>USERROLEID - ИД роли пользователя</LI>
     * <LI>USERROLESYSNAME - Системное имя роли пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHATSID - ИД записи</LI>
     * <LI>CHATNUM - Номер чата</LI>
     * <LI>CREATEDATE - Дата создания записи чата</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>MESSAGE - Сообщение чата</LI>
     * <LI>MESSAGEID - Номер сообщения чата</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>UPDATEDATE - Дата обновления записи чата</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего запись чата</LI>
     * <LI>USERNAME - ФИО пользователя</LI>
     * <LI>USERROLEID - ИД роли пользователя</LI>
     * <LI>USERROLESYSNAME - Системное имя роли пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesChatsBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesChatsBrowseListByParam", "dsLossesChatsBrowseListByParamCount", params);
        return result;
    }





}
