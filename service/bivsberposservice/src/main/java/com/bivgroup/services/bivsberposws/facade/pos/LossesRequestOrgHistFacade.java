/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesRequestOrgHist
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="LOSS_REQUESTORGHIST",idFieldName="REQUESTORGHISTID")
@BOName("LossesRequestOrgHist")
public class LossesRequestOrgHistFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTORGHISTID - ИД истории изменения пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesRequestOrgHistCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesRequestOrgHistInsert", params);
        result.put("REQUESTORGHISTID", params.get("REQUESTORGHISTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTORGHISTID - ИД истории изменения пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTORGHISTID"})
    public Map<String,Object> dsLossesRequestOrgHistInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesRequestOrgHistInsert", params);
        result.put("REQUESTORGHISTID", params.get("REQUESTORGHISTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTORGHISTID - ИД истории изменения пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTORGHISTID"})
    public Map<String,Object> dsLossesRequestOrgHistUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesRequestOrgHistUpdate", params);
        result.put("REQUESTORGHISTID", params.get("REQUESTORGHISTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTORGHISTID - ИД истории изменения пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTORGHISTID"})
    public Map<String,Object> dsLossesRequestOrgHistModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesRequestOrgHistUpdate", params);
        result.put("REQUESTORGHISTID", params.get("REQUESTORGHISTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTORGHISTID - ИД истории изменения пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTORGHISTID"})
    public void dsLossesRequestOrgHistDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesRequestOrgHistDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTORGHISTID - ИД истории изменения пользователя</LI>
     * <LI>NEWORGSTRUCTID - ИД нового подразделения</LI>
     * <LI>NEWUSERID - ИД нового пользователя</LI>
     * <LI>OLDORGSTRUCTID - ИД старого подразделения</LI>
     * <LI>OLDUSERID - ИД старого пользователя</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATETEXT - Текст изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesRequestOrgHistBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesRequestOrgHistBrowseListByParam", "dsLossesRequestOrgHistBrowseListByParamCount", params);
        return result;
    }





}
