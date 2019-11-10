package com.bivgroup.services.b2bposws.facade.pos.other;

import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;

/**
 * Фасад для сущности MessageRecipient
 *
 * @author reson
 */
@Discriminator(2)
@IdGen(entityName = "SD_MESSAGE_CORRESPONDENT", idFieldName = "ID")
@BOName("MessageRecipient")
public class MessageRecipientFacade extends BaseFacade {

    /**
     * Создать объект с генерацией id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHANNELID - ИД канала отправки сообщения</LI>
     * <LI>CLIENTPROFILEID - ИД профиля клиента</LI>
     * <LI>DISCRIMINATOR - Тип корреспондента (2- получатель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД получателя</LI>
     * <LI>MESSAGEID - ИД сообщения</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД получателя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMessageRecipientCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsMessageRecipientInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Создать объект без генерации id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHANNELID - ИД канала отправки сообщения</LI>
     * <LI>CLIENTPROFILEID - ИД профиля клиента</LI>
     * <LI>DISCRIMINATOR - Тип корреспондента (2- получатель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД получателя</LI>
     * <LI>MESSAGEID - ИД сообщения</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД получателя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageRecipientInsert(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsMessageRecipientInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHANNELID - ИД канала отправки сообщения</LI>
     * <LI>CLIENTPROFILEID - ИД профиля клиента</LI>
     * <LI>DISCRIMINATOR - Тип корреспондента (2- получатель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД получателя</LI>
     * <LI>MESSAGEID - ИД сообщения</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД получателя</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageRecipientUpdate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsMessageRecipientUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHANNELID - ИД канала отправки сообщения</LI>
     * <LI>CLIENTPROFILEID - ИД профиля клиента</LI>
     * <LI>DISCRIMINATOR - Тип корреспондента (2- получатель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД получателя</LI>
     * <LI>MESSAGEID - ИД сообщения</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД получателя</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageRecipientModify(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsMessageRecipientUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Удалить объект по id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД получателя</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {})
    public void dsMessageRecipientDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsMessageRecipientDelete", params);
    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHANNELID - ИД канала отправки сообщения</LI>
     * <LI>CLIENTPROFILEID - ИД профиля клиента</LI>
     * <LI>DISCRIMINATOR - Тип корреспондента (2- получатель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД получателя</LI>
     * <LI>MESSAGEID - ИД сообщения</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHANNELID - ИД канала отправки сообщения</LI>
     * <LI>CLIENTPROFILEID - ИД профиля клиента</LI>
     * <LI>DISCRIMINATOR - Тип корреспондента (2- получатель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД получателя</LI>
     * <LI>MESSAGEID - ИД сообщения</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMessageRecipientBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsMessageRecipientBrowseListByParam", "dsMessageRecipientBrowseListByParamCount", params);
        return result;
    }

}
