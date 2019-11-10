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
 * Фасад для сущности B2BContractOrgHist
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_CONTRORGHIST",idFieldName="CONTRORGHISTID")
@BOName("B2BContractOrgHist")
public class B2BContractOrgHistFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>CONTRORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRORGHISTID - ИД истории изменения пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractOrgHistCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractOrgHistInsert", params);
        result.put("CONTRORGHISTID", params.get("CONTRORGHISTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>CONTRORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRORGHISTID - ИД истории изменения пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRORGHISTID"})
    public Map<String,Object> dsB2BContractOrgHistInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractOrgHistInsert", params);
        result.put("CONTRORGHISTID", params.get("CONTRORGHISTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>CONTRORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRORGHISTID - ИД истории изменения пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRORGHISTID"})
    public Map<String,Object> dsB2BContractOrgHistUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractOrgHistUpdate", params);
        result.put("CONTRORGHISTID", params.get("CONTRORGHISTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>CONTRORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRORGHISTID - ИД истории изменения пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRORGHISTID"})
    public Map<String,Object> dsB2BContractOrgHistModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractOrgHistUpdate", params);
        result.put("CONTRORGHISTID", params.get("CONTRORGHISTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRORGHISTID - ИД истории изменения пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRORGHISTID"})
    public void dsB2BContractOrgHistDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractOrgHistDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>CONTRORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>CONTRORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractOrgHistBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractOrgHistBrowseListByParam", "dsB2BContractOrgHistBrowseListByParamCount", params);
        return result;
    }





}
