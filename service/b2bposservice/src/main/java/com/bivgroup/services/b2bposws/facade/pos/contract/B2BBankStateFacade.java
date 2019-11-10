/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.autonumber.AutoNumber;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BBankState
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_BANKSTATE",idFieldName="BANKSTATEMENTID")
@AutoNumber(autoNumberFieldName = "NUM",dataParamName = "CREATEDATE")
@State(idFieldName = "BANKSTATEMENTID", startStateName = "B2B_BANKSTATE_INLOADQUEUE", typeSysName = "B2B_BANKSTATE")
@BOName("B2BBankState")
public class B2BBankStateFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>DOCDATE - Дата выписки</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * <LI>INPUTBEGINDATE - Входящая дата начала</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTFINISHDATE - Входящая дата окончания</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * <LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankStateCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankStateInsert", params);
        result.put("BANKSTATEMENTID", params.get("BANKSTATEMENTID"));
        return result;
    }

 /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>COUNTERRORSTRING - количество строк ошибками</LI>
     * <LI>COUNTUPDATESTRING - количество обновленных строк</LI>
     * <LI>COUNTADDSTRING - количество добавленных строк</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATEMENTID"})
    public Map<String,Object> dsB2BBankStateUpdateCountString(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankStateUpdateCountString", params);
        result.put("BANKSTATEMENTID", params.get("BANKSTATEMENTID"));
        return result;
    }



    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>DOCDATE - Дата выписки</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * <LI>INPUTBEGINDATE - Входящая дата начала</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTFINISHDATE - Входящая дата окончания</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATEMENTID"})
    public Map<String,Object> dsB2BBankStateInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankStateInsert", params);
        result.put("BANKSTATEMENTID", params.get("BANKSTATEMENTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>DOCDATE - Дата выписки</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * <LI>INPUTBEGINDATE - Входящая дата начала</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTFINISHDATE - Входящая дата окончания</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATEMENTID"})
    public Map<String,Object> dsB2BBankStateUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankStateUpdate", params);
        result.put("BANKSTATEMENTID", params.get("BANKSTATEMENTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>DOCDATE - Дата выписки</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * <LI>INPUTBEGINDATE - Входящая дата начала</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTFINISHDATE - Входящая дата окончания</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATEMENTID"})
    public Map<String,Object> dsB2BBankStateModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankStateUpdate", params);
        result.put("BANKSTATEMENTID", params.get("BANKSTATEMENTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATEMENTID"})
    public void dsB2BBankStateDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BBankStateDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>DOCDATE - Дата выписки</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * <LI>INPUTBEGINDATE - Входящая дата начала</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTFINISHDATE - Входящая дата окончания</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>DOCDATE - Дата выписки</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKSTATEMENTID - ИД Банковской выписки</LI>
     * <LI>INPUTBEGINDATE - Входящая дата начала</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTFINISHDATE - Входящая дата окончания</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankStateBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BBankStateBrowseListByParam", "dsB2BBankStateBrowseListByParamCount", params);
        return result;
    }





}
