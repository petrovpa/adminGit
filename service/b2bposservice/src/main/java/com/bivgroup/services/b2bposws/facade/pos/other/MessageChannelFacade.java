package com.bivgroup.services.b2bposws.facade.pos.other;

import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;

/**
 * Фасад для сущности MessageChannel
 *
 * @author reson
 */
@IdGen(entityName = "HD_MESSAGECHANNEL", idFieldName = "ID")
@BOName("MessageChannel")
public class MessageChannelFacade extends BaseFacade {

    /**
     * Создать объект с генерацией id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД канала сообщения</LI>
     * <LI>JAVACLASS - Java класс</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД канала сообщения</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMessageChannelCreate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsMessageChannelInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Создать объект без генерации id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД канала сообщения</LI>
     * <LI>JAVACLASS - Java класс</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД канала сообщения</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageChannelInsert(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.insertQuery("dsMessageChannelInsert", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД канала сообщения</LI>
     * <LI>JAVACLASS - Java класс</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД канала сообщения</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageChannelUpdate(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsMessageChannelUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Изменить объект
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД канала сообщения</LI>
     * <LI>JAVACLASS - Java класс</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ID - ИД канала сообщения</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {"ID"})
    public Map<String, Object> dsMessageChannelModify(Map<String, Object> params) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        this.updateQuery("dsMessageChannelUpdate", params);
        result.put("ID", params.get("ID"));
        return result;
    }

    /**
     * Удалить объект по id
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>ID - ИД канала сообщения</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {})
    public void dsMessageChannelDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsMessageChannelDelete", params);
    }

    /**
     * Получить объекты в виде списка по ограничениям
     *
     * @author reson
     * @param params
     * <UL>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД канала сообщения</LI>
     * <LI>JAVACLASS - Java класс</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>EID - ИД сущности</LI>
     * <LI>ID - ИД канала сообщения</LI>
     * <LI>JAVACLASS - Java класс</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @throws java.lang.Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsMessageChannelBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> result = this.selectQuery("dsMessageChannelBrowseListByParam", "dsMessageChannelBrowseListByParamCount", params);
        return result;
    }

}
