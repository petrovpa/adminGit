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
 * Фасад для сущности B2BMainActivityContractMemberType
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_MACMEMBERTYPE",idFieldName="MACMEMBERTYPEID")
@BOName("B2BMainActivityContractMemberType")
public class B2BMainActivityContractMemberTypeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>MACMEMBERTYPEID - ИД типа участника</LI>
     * <LI>NAME - Наименование типа участника</LI>
     * <LI>SYSNAME - Системное наименование типа участника</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACMEMBERTYPEID - ИД типа участника</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractMemberTypeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractMemberTypeInsert", params);
        result.put("MACMEMBERTYPEID", params.get("MACMEMBERTYPEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>MACMEMBERTYPEID - ИД типа участника</LI>
     * <LI>NAME - Наименование типа участника</LI>
     * <LI>SYSNAME - Системное наименование типа участника</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACMEMBERTYPEID - ИД типа участника</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACMEMBERTYPEID"})
    public Map<String,Object> dsB2BMainActivityContractMemberTypeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractMemberTypeInsert", params);
        result.put("MACMEMBERTYPEID", params.get("MACMEMBERTYPEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>MACMEMBERTYPEID - ИД типа участника</LI>
     * <LI>NAME - Наименование типа участника</LI>
     * <LI>SYSNAME - Системное наименование типа участника</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACMEMBERTYPEID - ИД типа участника</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACMEMBERTYPEID"})
    public Map<String,Object> dsB2BMainActivityContractMemberTypeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractMemberTypeUpdate", params);
        result.put("MACMEMBERTYPEID", params.get("MACMEMBERTYPEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>MACMEMBERTYPEID - ИД типа участника</LI>
     * <LI>NAME - Наименование типа участника</LI>
     * <LI>SYSNAME - Системное наименование типа участника</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACMEMBERTYPEID - ИД типа участника</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACMEMBERTYPEID"})
    public Map<String,Object> dsB2BMainActivityContractMemberTypeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractMemberTypeUpdate", params);
        result.put("MACMEMBERTYPEID", params.get("MACMEMBERTYPEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MACMEMBERTYPEID - ИД типа участника</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACMEMBERTYPEID"})
    public void dsB2BMainActivityContractMemberTypeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMainActivityContractMemberTypeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>MACMEMBERTYPEID - ИД типа участника</LI>
     * <LI>NAME - Наименование типа участника</LI>
     * <LI>SYSNAME - Системное наименование типа участника</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>MACMEMBERTYPEID - ИД типа участника</LI>
     * <LI>NAME - Наименование типа участника</LI>
     * <LI>SYSNAME - Системное наименование типа участника</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractMemberTypeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMainActivityContractMemberTypeBrowseListByParam", "dsB2BMainActivityContractMemberTypeBrowseListByParamCount", params);
        return result;
    }





}
