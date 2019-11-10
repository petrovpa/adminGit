/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BRequestQueue
 *
 * @author reson
 */
@IdGen(entityName="B2B_REQUESTQUEUE",idFieldName="REQUESTQUEUEID")
@BOName("B2BRequestQueue")
public class B2BRequestQueueFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTQUEUEID - ИД записи</LI>
     * <LI>PARENTREQUESTID - ИД запроса родителя</LI>
     * <LI>PROCESSDATE - Дата обработки</LI>
     * <LI>RVERSION - Для блокировки</LI>
     * <LI>REQUESTDATE - Дата запроса</LI>
     * <LI>REQUESTSTATEID - Состояние</LI>
     * <LI>REQUESTTYPEID - Тип запроса</LI>
     * <LI>USERID - ИД пользователя, выполнявшего запрос</LI>
     * <LI>XMLREQUEST - Содержимое запроса</LI>
     * <LI>XMLRESPONSE - Содержимое ответа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTQUEUEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BRequestQueueCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BRequestQueueInsert", params);
        result.put("REQUESTQUEUEID", params.get("REQUESTQUEUEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTQUEUEID - ИД записи</LI>
     * <LI>PARENTREQUESTID - ИД запроса родителя</LI>
     * <LI>PROCESSDATE - Дата обработки</LI>
     * <LI>RVERSION - Для блокировки</LI>
     * <LI>REQUESTDATE - Дата запроса</LI>
     * <LI>REQUESTSTATEID - Состояние</LI>
     * <LI>REQUESTTYPEID - Тип запроса</LI>
     * <LI>USERID - ИД пользователя, выполнявшего запрос</LI>
     * <LI>XMLREQUEST - Содержимое запроса</LI>
     * <LI>XMLRESPONSE - Содержимое ответа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTQUEUEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTQUEUEID"})
    public Map<String,Object> dsB2BRequestQueueInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BRequestQueueInsert", params);
        result.put("REQUESTQUEUEID", params.get("REQUESTQUEUEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTQUEUEID - ИД записи</LI>
     * <LI>PARENTREQUESTID - ИД запроса родителя</LI>
     * <LI>PROCESSDATE - Дата обработки</LI>
     * <LI>RVERSION - Для блокировки</LI>
     * <LI>REQUESTDATE - Дата запроса</LI>
     * <LI>REQUESTSTATEID - Состояние</LI>
     * <LI>REQUESTTYPEID - Тип запроса</LI>
     * <LI>USERID - ИД пользователя, выполнявшего запрос</LI>
     * <LI>XMLREQUEST - Содержимое запроса</LI>
     * <LI>XMLRESPONSE - Содержимое ответа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTQUEUEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTQUEUEID"})
    public Map<String,Object> dsB2BRequestQueueUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BRequestQueueUpdate", params);
        result.put("REQUESTQUEUEID", params.get("REQUESTQUEUEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTQUEUEID - ИД записи</LI>
     * <LI>PARENTREQUESTID - ИД запроса родителя</LI>
     * <LI>PROCESSDATE - Дата обработки</LI>
     * <LI>RVERSION - Для блокировки</LI>
     * <LI>REQUESTDATE - Дата запроса</LI>
     * <LI>REQUESTSTATEID - Состояние</LI>
     * <LI>REQUESTTYPEID - Тип запроса</LI>
     * <LI>USERID - ИД пользователя, выполнявшего запрос</LI>
     * <LI>XMLREQUEST - Содержимое запроса</LI>
     * <LI>XMLRESPONSE - Содержимое ответа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTQUEUEID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTQUEUEID"})
    public Map<String,Object> dsB2BRequestQueueModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BRequestQueueUpdate", params);
        result.put("REQUESTQUEUEID", params.get("REQUESTQUEUEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTQUEUEID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTQUEUEID"})
    public void dsB2BRequestQueueDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BRequestQueueDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTQUEUEID - ИД записи</LI>
     * <LI>PARENTREQUESTID - ИД запроса родителя</LI>
     * <LI>PROCESSDATE - Дата обработки</LI>
     * <LI>RVERSION - Для блокировки</LI>
     * <LI>REQUESTDATE - Дата запроса</LI>
     * <LI>REQUESTSTATEID - Состояние</LI>
     * <LI>REQUESTTYPEID - Тип запроса</LI>
     * <LI>USERID - ИД пользователя, выполнявшего запрос</LI>
     * <LI>XMLREQUEST - Содержимое запроса</LI>
     * <LI>XMLRESPONSE - Содержимое ответа</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTQUEUEID - ИД записи</LI>
     * <LI>PARENTREQUESTID - ИД запроса родителя</LI>
     * <LI>PROCESSDATE - Дата обработки</LI>
     * <LI>RVERSION - Для блокировки</LI>
     * <LI>REQUESTDATE - Дата запроса</LI>
     * <LI>REQUESTSTATEID - Состояние</LI>
     * <LI>REQUESTTYPEID - Тип запроса</LI>
     * <LI>USERID - ИД пользователя, выполнявшего запрос</LI>
     * <LI>XMLREQUEST - Содержимое запроса</LI>
     * <LI>XMLRESPONSE - Содержимое ответа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BRequestQueueBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BRequestQueueBrowseListByParam", "dsB2BRequestQueueBrowseListByParamCount", params);
        return result;
    }





}
