/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.ownerright.OwnerRightView;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesRequestNode
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@OwnerRightView()
@IdGen(entityName="LOSS_REQUESTNODE",idFieldName="REQUESTNODEID")
@BOName("LossesRequestNode")
public class LossesRequestNodeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * <LI>LASTVERNUMBER - Номер последней версии заявки</LI>
     * <LI>RVERSION - Номер версии для оптимистической блокировки</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesRequestNodeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesRequestNodeInsert", params);
        result.put("REQUESTNODEID", params.get("REQUESTNODEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * <LI>LASTVERNUMBER - Номер последней версии заявки</LI>
     * <LI>RVERSION - Номер версии для оптимистической блокировки</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTNODEID"})
    public Map<String,Object> dsLossesRequestNodeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesRequestNodeInsert", params);
        result.put("REQUESTNODEID", params.get("REQUESTNODEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * <LI>LASTVERNUMBER - Номер последней версии заявки</LI>
     * <LI>RVERSION - Номер версии для оптимистической блокировки</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTNODEID"})
    public Map<String,Object> dsLossesRequestNodeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesRequestNodeUpdate", params);
        result.put("REQUESTNODEID", params.get("REQUESTNODEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * <LI>LASTVERNUMBER - Номер последней версии заявки</LI>
     * <LI>RVERSION - Номер версии для оптимистической блокировки</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTNODEID"})
    public Map<String,Object> dsLossesRequestNodeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesRequestNodeUpdate", params);
        result.put("REQUESTNODEID", params.get("REQUESTNODEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTNODEID"})
    public void dsLossesRequestNodeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesRequestNodeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * <LI>LASTVERNUMBER - Номер последней версии заявки</LI>
     * <LI>RVERSION - Номер версии для оптимистической блокировки</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>REQUESTNODEID - ИД ноды заявки</LI>
     * <LI>LASTVERNUMBER - Номер последней версии заявки</LI>
     * <LI>RVERSION - Номер версии для оптимистической блокировки</LI>
     * <LI>REQUESTID - ИД заявки</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesRequestNodeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesRequestNodeBrowseListByParam", "dsLossesRequestNodeBrowseListByParamCount", params);
        return result;
    }





}
