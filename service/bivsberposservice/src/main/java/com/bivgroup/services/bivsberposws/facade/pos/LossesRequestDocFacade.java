/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesRequestDoc
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@BinaryFile(objTableName = "LOSS_REQUESTDOC", objTablePKFieldName = "REQUESTDOCID")
@IdGen(entityName="LOSS_REQUESTDOC",idFieldName="REQUESTDOCID")
@BOName("LossesRequestDoc")
public class LossesRequestDocFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - Вид документа</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * <LI>ISDOCCHECKING - Флаг Документ проверен</LI>
     * <LI>ISORIGINALRECEIVED - Флаг Оригинал получен</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesRequestDocCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesRequestDocInsert", params);
        result.put("REQUESTDOCID", params.get("REQUESTDOCID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - Вид документа</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * <LI>ISDOCCHECKING - Флаг Документ проверен</LI>
     * <LI>ISORIGINALRECEIVED - Флаг Оригинал получен</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTDOCID"})
    public Map<String,Object> dsLossesRequestDocInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesRequestDocInsert", params);
        result.put("REQUESTDOCID", params.get("REQUESTDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - Вид документа</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * <LI>ISDOCCHECKING - Флаг Документ проверен</LI>
     * <LI>ISORIGINALRECEIVED - Флаг Оригинал получен</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTDOCID"})
    public Map<String,Object> dsLossesRequestDocUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesRequestDocUpdate", params);
        result.put("REQUESTDOCID", params.get("REQUESTDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - Вид документа</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * <LI>ISDOCCHECKING - Флаг Документ проверен</LI>
     * <LI>ISORIGINALRECEIVED - Флаг Оригинал получен</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTDOCID"})
    public Map<String,Object> dsLossesRequestDocModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesRequestDocUpdate", params);
        result.put("REQUESTDOCID", params.get("REQUESTDOCID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTDOCID"})
    public void dsLossesRequestDocDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesRequestDocDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - Вид документа</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * <LI>ISDOCCHECKING - Флаг Документ проверен</LI>
     * <LI>ISORIGINALRECEIVED - Флаг Оригинал получен</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINDOCID - Вид документа</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTDOCID - ИД прикрепленного документа</LI>
     * <LI>ISDOCCHECKING - Флаг Документ проверен</LI>
     * <LI>ISORIGINALRECEIVED - Флаг Оригинал получен</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesRequestDocBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesRequestDocBrowseListByParam", "dsLossesRequestDocBrowseListByParamCount", params);
        return result;
    }





}
