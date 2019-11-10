/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BFeedBack
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_FEEDBACK",idFieldName="FEEDBACKID")
@BOName("B2BFeedBack")
public class B2BFeedBackFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>COREUSERID - ИД пользователя b2b оставляющего отзыв</LI>
     * <LI>CREATEDATE - Дата создания отзыва</LI>
     * <LI>CREATEUSERID - Ид пользователя создавшего отзыв</LI>
     * <LI>FEEDBACK - Отзыв</LI>
     * <LI>FEEDBACKID - ИД</LI>
     * <LI>PAUSERID - Ид пользователя ЛК</LI>
     * <LI>SESSIONID - Ид сессии</LI>
     * <LI>TYPE - Тип отзыва.</LI>
     * <LI>UPDATEDATE - Дата обновления отзыва</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего отзыв</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>FEEDBACKID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BFeedBackCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BFeedBackInsert", params);
        result.put("FEEDBACKID", params.get("FEEDBACKID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>COREUSERID - ИД пользователя b2b оставляющего отзыв</LI>
     * <LI>CREATEDATE - Дата создания отзыва</LI>
     * <LI>CREATEUSERID - Ид пользователя создавшего отзыв</LI>
     * <LI>FEEDBACK - Отзыв</LI>
     * <LI>FEEDBACKID - ИД</LI>
     * <LI>PAUSERID - Ид пользователя ЛК</LI>
     * <LI>SESSIONID - Ид сессии</LI>
     * <LI>TYPE - Тип отзыва.</LI>
     * <LI>UPDATEDATE - Дата обновления отзыва</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего отзыв</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>FEEDBACKID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"FEEDBACKID"})
    public Map<String,Object> dsB2BFeedBackInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BFeedBackInsert", params);
        result.put("FEEDBACKID", params.get("FEEDBACKID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>COREUSERID - ИД пользователя b2b оставляющего отзыв</LI>
     * <LI>CREATEDATE - Дата создания отзыва</LI>
     * <LI>CREATEUSERID - Ид пользователя создавшего отзыв</LI>
     * <LI>FEEDBACK - Отзыв</LI>
     * <LI>FEEDBACKID - ИД</LI>
     * <LI>PAUSERID - Ид пользователя ЛК</LI>
     * <LI>SESSIONID - Ид сессии</LI>
     * <LI>TYPE - Тип отзыва.</LI>
     * <LI>UPDATEDATE - Дата обновления отзыва</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего отзыв</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>FEEDBACKID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"FEEDBACKID"})
    public Map<String,Object> dsB2BFeedBackUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BFeedBackUpdate", params);
        result.put("FEEDBACKID", params.get("FEEDBACKID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>COREUSERID - ИД пользователя b2b оставляющего отзыв</LI>
     * <LI>CREATEDATE - Дата создания отзыва</LI>
     * <LI>CREATEUSERID - Ид пользователя создавшего отзыв</LI>
     * <LI>FEEDBACK - Отзыв</LI>
     * <LI>FEEDBACKID - ИД</LI>
     * <LI>PAUSERID - Ид пользователя ЛК</LI>
     * <LI>SESSIONID - Ид сессии</LI>
     * <LI>TYPE - Тип отзыва.</LI>
     * <LI>UPDATEDATE - Дата обновления отзыва</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего отзыв</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>FEEDBACKID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"FEEDBACKID"})
    public Map<String,Object> dsB2BFeedBackModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BFeedBackUpdate", params);
        result.put("FEEDBACKID", params.get("FEEDBACKID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>FEEDBACKID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"FEEDBACKID"})
    public void dsB2BFeedBackDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BFeedBackDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>COREUSERID - ИД пользователя b2b оставляющего отзыв</LI>
     * <LI>CREATEDATE - Дата создания отзыва</LI>
     * <LI>CREATEUSERID - Ид пользователя создавшего отзыв</LI>
     * <LI>FEEDBACK - Отзыв</LI>
     * <LI>FEEDBACKID - ИД</LI>
     * <LI>PAUSERID - Ид пользователя ЛК</LI>
     * <LI>SESSIONID - Ид сессии</LI>
     * <LI>TYPE - Тип отзыва.</LI>
     * <LI>UPDATEDATE - Дата обновления отзыва</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего отзыв</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>COREUSERID - ИД пользователя b2b оставляющего отзыв</LI>
     * <LI>CREATEDATE - Дата создания отзыва</LI>
     * <LI>CREATEUSERID - Ид пользователя создавшего отзыв</LI>
     * <LI>FEEDBACK - Отзыв</LI>
     * <LI>FEEDBACKID - ИД</LI>
     * <LI>PAUSERID - Ид пользователя ЛК</LI>
     * <LI>SESSIONID - Ид сессии</LI>
     * <LI>TYPE - Тип отзыва.</LI>
     * <LI>UPDATEDATE - Дата обновления отзыва</LI>
     * <LI>UPDATEUSERID - ИД пользователя обновившего отзыв</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BFeedBackBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BFeedBackBrowseListByParam", "dsB2BFeedBackBrowseListByParamCount", params);
        return result;
    }





}
