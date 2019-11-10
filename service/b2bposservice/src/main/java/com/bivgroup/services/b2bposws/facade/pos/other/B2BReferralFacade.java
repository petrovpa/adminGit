/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.other;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BReferral
 *
 * @author reson
 */
@IdGen(entityName="B2B_REFERRAL",idFieldName="REFERRALID")
@BOName("B2BReferral")
public class B2BReferralFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>REFERRALID - ИД реферала</LI>
     * <LI>LINK - линк реферала сохраняется в договоре в REFERRAL</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TOKEN - Токен</LI>
     * <LI>URLBEGIN - Линк запроса о начале офомрления договора</LI>
     * <LI>URLDONE - Линк запроса о оплате и заключении договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REFERRALID - ИД реферала</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BReferralCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BReferralInsert", params);
        result.put("REFERRALID", params.get("REFERRALID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>REFERRALID - ИД реферала</LI>
     * <LI>LINK - линк реферала сохраняется в договоре в REFERRAL</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TOKEN - Токен</LI>
     * <LI>URLBEGIN - Линк запроса о начале офомрления договора</LI>
     * <LI>URLDONE - Линк запроса о оплате и заключении договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REFERRALID - ИД реферала</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REFERRALID"})
    public Map<String,Object> dsB2BReferralInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BReferralInsert", params);
        result.put("REFERRALID", params.get("REFERRALID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>REFERRALID - ИД реферала</LI>
     * <LI>LINK - линк реферала сохраняется в договоре в REFERRAL</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TOKEN - Токен</LI>
     * <LI>URLBEGIN - Линк запроса о начале офомрления договора</LI>
     * <LI>URLDONE - Линк запроса о оплате и заключении договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REFERRALID - ИД реферала</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REFERRALID"})
    public Map<String,Object> dsB2BReferralUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BReferralUpdate", params);
        result.put("REFERRALID", params.get("REFERRALID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>REFERRALID - ИД реферала</LI>
     * <LI>LINK - линк реферала сохраняется в договоре в REFERRAL</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TOKEN - Токен</LI>
     * <LI>URLBEGIN - Линк запроса о начале офомрления договора</LI>
     * <LI>URLDONE - Линк запроса о оплате и заключении договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REFERRALID - ИД реферала</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REFERRALID"})
    public Map<String,Object> dsB2BReferralModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BReferralUpdate", params);
        result.put("REFERRALID", params.get("REFERRALID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>REFERRALID - ИД реферала</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"REFERRALID"})
    public void dsB2BReferralDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BReferralDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>REFERRALID - ИД реферала</LI>
     * <LI>LINK - линк реферала сохраняется в договоре в REFERRAL</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TOKEN - Токен</LI>
     * <LI>URLBEGIN - Линк запроса о начале офомрления договора</LI>
     * <LI>URLDONE - Линк запроса о оплате и заключении договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REFERRALID - ИД реферала</LI>
     * <LI>LINK - линк реферала сохраняется в договоре в REFERRAL</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TOKEN - Токен</LI>
     * <LI>URLBEGIN - Линк запроса о начале офомрления договора</LI>
     * <LI>URLDONE - Линк запроса о оплате и заключении договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BReferralBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BReferralBrowseListByParam", "dsB2BReferralBrowseListByParamCount", params);
        return result;
    }





}
