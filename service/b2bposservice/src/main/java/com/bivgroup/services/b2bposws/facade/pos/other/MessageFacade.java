package com.bivgroup.services.b2bposws.facade.pos.other;

import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;

/**
 * Фасад для сущности Message
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@BinaryFile(objTableName = "SD_MESSAGE", objTablePKFieldName = "ID")
@IdGen(entityName = "SD_MESSAGE", idFieldName = "ID")
@BOName("Message")
public class MessageFacade extends BaseFacade {

    /**
     * Создать объект с генерацией id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHNOTIFICATIONID - ИД заяления по страховому событию</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший сообщение</LI>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД сообщения</LI>
     * <LI>ISFAVORITE - Признак Избранный</LI>
     * <LI>ISUNREAD - Признак Прочитан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PDDECLARATIONID - ИД заявления по договору</LI>
     * <LI>SENDERID - ИД отправителя</LI>
     * <LI>TYPEMESSAGE - Тип сообщения</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший сообщение</LI>
     * <LI>TITLEHASH - хэш-код темы сообщения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД сообщения</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMessageCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsMessageInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Создать объект без генерации id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHNOTIFICATIONID - ИД заяления по страховому событию</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший сообщение</LI>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД сообщения</LI>
     * <LI>ISFAVORITE - Признак Избранный</LI>
     * <LI>ISUNREAD - Признак Прочитан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PDDECLARATIONID - ИД заявления по договору</LI>
     * <LI>SENDERID - ИД отправителя</LI>
     * <LI>TYPEMESSAGE - Тип сообщения</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший сообщение</LI>
     * <LI>TITLEHASH - хэш-код темы сообщения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД сообщения</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageInsert(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsMessageInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHNOTIFICATIONID - ИД заяления по страховому событию</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший сообщение</LI>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД сообщения</LI>
     * <LI>ISFAVORITE - Признак Избранный</LI>
     * <LI>ISUNREAD - Признак Прочитан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PDDECLARATIONID - ИД заявления по договору</LI>
     * <LI>SENDERID - ИД отправителя</LI>
     * <LI>TYPEMESSAGE - Тип сообщения</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший сообщение</LI>
     * <LI>TITLEHASH - хэш-код темы сообщения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД сообщения</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageUpdate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsMessageUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHNOTIFICATIONID - ИД заяления по страховому событию</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший сообщение</LI>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД сообщения</LI>
     * <LI>ISFAVORITE - Признак Избранный</LI>
     * <LI>ISUNREAD - Признак Прочитан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PDDECLARATIONID - ИД заявления по договору</LI>
     * <LI>SENDERID - ИД отправителя</LI>
     * <LI>TYPEMESSAGE - Тип сообщения</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший сообщение</LI>
     * <LI>TITLEHASH - хэш-код темы сообщения</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД сообщения</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageModify(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsMessageUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Удалить объект по id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД сообщения</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public void dsMessageDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsMessageDelete", params);
    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>CHNOTIFICATIONID - ИД заяления по страховому событию</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший сообщение</LI>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД сообщения</LI>
     * <LI>ISFAVORITE - Признак Избранный</LI>
     * <LI>ISUNREAD - Признак Прочитан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PDDECLARATIONID - ИД заявления по договору</LI>
     * <LI>SENDERID - ИД отправителя</LI>
     * <LI>TYPEMESSAGE - Тип сообщения</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший сообщение</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHNOTIFICATIONID - ИД заяления по страховому событию</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший сообщение</LI>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД сообщения</LI>
     * <LI>ISFAVORITE - Признак Избранный</LI>
     * <LI>ISUNREAD - Признак Прочитан</LI>
     * <LI>NOTE - Описание</LI>
     * <LI>PDDECLARATIONID - ИД заявления по договору</LI>
     * <LI>SENDERID - ИД отправителя</LI>
     * <LI>TYPEMESSAGE - Тип сообщения</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший сообщение</LI>
     * <LI>TITLEHASH - хэш-код темы сообщения</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMessageBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsMessageBrowseListByParam", "dsMessageBrowseListByParamCount", params);
        return result;
    }

}
