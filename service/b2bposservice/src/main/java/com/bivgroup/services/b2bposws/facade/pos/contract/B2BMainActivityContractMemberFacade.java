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
 * Фасад для сущности B2BMainActivityContractMember
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_MACMEMBER",idFieldName="MACMEMBERID")
@BOName("B2BMainActivityContractMember")
public class B2BMainActivityContractMemberFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания участника</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего участника</LI>
     * <LI>MACMEMBERID - ИД участника</LI>
     * <LI>MACMEMBERTYPEID - Тип участника</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>UPDATEDATE - Дата изменения участника</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего участника</LI>
     * <LI>USERID - ИД  пользователя участника</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACMEMBERID - ИД участника</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractMemberCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractMemberInsert", params);
        result.put("MACMEMBERID", params.get("MACMEMBERID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания участника</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего участника</LI>
     * <LI>MACMEMBERID - ИД участника</LI>
     * <LI>MACMEMBERTYPEID - Тип участника</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>UPDATEDATE - Дата изменения участника</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего участника</LI>
     * <LI>USERID - ИД  пользователя участника</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACMEMBERID - ИД участника</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACMEMBERID"})
    public Map<String,Object> dsB2BMainActivityContractMemberInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractMemberInsert", params);
        result.put("MACMEMBERID", params.get("MACMEMBERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания участника</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего участника</LI>
     * <LI>MACMEMBERID - ИД участника</LI>
     * <LI>MACMEMBERTYPEID - Тип участника</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>UPDATEDATE - Дата изменения участника</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего участника</LI>
     * <LI>USERID - ИД  пользователя участника</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACMEMBERID - ИД участника</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACMEMBERID"})
    public Map<String,Object> dsB2BMainActivityContractMemberUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractMemberUpdate", params);
        result.put("MACMEMBERID", params.get("MACMEMBERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания участника</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего участника</LI>
     * <LI>MACMEMBERID - ИД участника</LI>
     * <LI>MACMEMBERTYPEID - Тип участника</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>UPDATEDATE - Дата изменения участника</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего участника</LI>
     * <LI>USERID - ИД  пользователя участника</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MACMEMBERID - ИД участника</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACMEMBERID"})
    public Map<String,Object> dsB2BMainActivityContractMemberModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractMemberUpdate", params);
        result.put("MACMEMBERID", params.get("MACMEMBERID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MACMEMBERID - ИД участника</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MACMEMBERID"})
    public void dsB2BMainActivityContractMemberDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMainActivityContractMemberDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания участника</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего участника</LI>
     * <LI>MACMEMBERID - ИД участника</LI>
     * <LI>MACMEMBERTYPEID - Тип участника</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>UPDATEDATE - Дата изменения участника</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего участника</LI>
     * <LI>USERID - ИД  пользователя участника</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания участника</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего участника</LI>
     * <LI>MACMEMBERID - ИД участника</LI>
     * <LI>MACMEMBERTYPEID - Тип участника</LI>
     * <LI>MAINACTCONTRID - ИД агентского договора</LI>
     * <LI>UPDATEDATE - Дата изменения участника</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего участника</LI>
     * <LI>USERID - ИД  пользователя участника</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractMemberBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMainActivityContractMemberBrowseListByParam", "dsB2BMainActivityContractMemberBrowseListByParamCount", params);
        return result;
    }





}
