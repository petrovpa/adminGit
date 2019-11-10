package com.bivgroup.services.b2bposws.facade.pos.other;

import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;

/**
 * Фасад для сущности MessageCorrespondent
 *
 * @author reson
 */
@Discriminator(1)
@IdGen(entityName = "SD_MESSAGE_CORRESPONDENT", idFieldName = "ID")
@BOName("MessageCorrespondent")
public class MessageCorrespondentFacade extends BaseFacade {

    /**
     * Создать объект с генерацией id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHANNELID - ИД канала отправки сообщения</LI>
     * <LI>CLIENTPROFILEID - ИД профиля клиента</LI>
     * <LI>DISCRIMINATOR - Тип корреспондента (1 - отправитель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД корреспондента</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД корреспондента</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMessageCorrespondentCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsMessageCorrespondentInsert", params);
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
     * <LI>DISCRIMINATOR - Тип корреспондента (1 - отправитель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД корреспондента</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД корреспондента</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageCorrespondentInsert(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsMessageCorrespondentInsert", params);
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
     * <LI>DISCRIMINATOR - Тип корреспондента (1 - отправитель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД корреспондента</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД корреспондента</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageCorrespondentUpdate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsMessageCorrespondentUpdate", params);
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
     * <LI>DISCRIMINATOR - Тип корреспондента (1 - отправитель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД корреспондента</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД корреспондента</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageCorrespondentModify(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsMessageCorrespondentUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Удалить объект по id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД корреспондента</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public void dsMessageCorrespondentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsMessageCorrespondentDelete", params);
    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHANNELID - ИД канала отправки сообщения</LI>
     * <LI>CLIENTPROFILEID - ИД профиля клиента</LI>
     * <LI>DISCRIMINATOR - Тип корреспондента (1 - отправитель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД корреспондента</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHANNELID - ИД канала отправки сообщения</LI>
     * <LI>CLIENTPROFILEID - ИД профиля клиента</LI>
     * <LI>DISCRIMINATOR - Тип корреспондента (1 - отправитель)</LI>
     * <LI>EMAIL - Эл. почта</LI>
     * <LI>ID - ИД корреспондента</LI>
     * <LI>TEL - Телефон</LI>
     * <LI>USERID - ИД пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMessageCorrespondentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsMessageCorrespondentBrowseListByParam", "dsMessageСorrespondentBrowseListByParamCount", params);
        return result;
    }

}
