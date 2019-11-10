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
 * Фасад для сущности B2BMainActivityContractNode
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_MAINACTCONTRNODE",idFieldName="MAINACTCONTRNODEID")
@BOName("B2BMainActivityContractNode")
public class B2BMainActivityContractNodeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания договора по основной деятельности</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор по основной деятельности</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора по основной деятельности со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>MAINACTCONTRID - ИД текущей версии договора по основной деятельности</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора по основной деятельности</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор по основной деятельности</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MAINACTCONTRNODEID - ИД договора по основной деятельности со всеми версиями</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"LASTVERNUMBER", "RVERSION"})
    public Map<String,Object> dsB2BMainActivityContractNodeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractNodeInsert", params);
        result.put("MAINACTCONTRNODEID", params.get("MAINACTCONTRNODEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания договора по основной деятельности</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор по основной деятельности</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора по основной деятельности со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>MAINACTCONTRID - ИД текущей версии договора по основной деятельности</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора по основной деятельности</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор по основной деятельности</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MAINACTCONTRNODEID - ИД договора по основной деятельности со всеми версиями</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MAINACTCONTRNODEID", "LASTVERNUMBER", "RVERSION"})
    public Map<String,Object> dsB2BMainActivityContractNodeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BMainActivityContractNodeInsert", params);
        result.put("MAINACTCONTRNODEID", params.get("MAINACTCONTRNODEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания договора по основной деятельности</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор по основной деятельности</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора по основной деятельности со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>MAINACTCONTRID - ИД текущей версии договора по основной деятельности</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора по основной деятельности</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор по основной деятельности</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MAINACTCONTRNODEID - ИД договора по основной деятельности со всеми версиями</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MAINACTCONTRNODEID"})
    public Map<String,Object> dsB2BMainActivityContractNodeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractNodeUpdate", params);
        result.put("MAINACTCONTRNODEID", params.get("MAINACTCONTRNODEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания договора по основной деятельности</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор по основной деятельности</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора по основной деятельности со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>MAINACTCONTRID - ИД текущей версии договора по основной деятельности</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора по основной деятельности</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор по основной деятельности</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>MAINACTCONTRNODEID - ИД договора по основной деятельности со всеми версиями</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"MAINACTCONTRNODEID"})
    public Map<String,Object> dsB2BMainActivityContractNodeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BMainActivityContractNodeUpdate", params);
        result.put("MAINACTCONTRNODEID", params.get("MAINACTCONTRNODEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>MAINACTCONTRNODEID - ИД договора по основной деятельности со всеми версиями</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"MAINACTCONTRNODEID"})
    public void dsB2BMainActivityContractNodeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BMainActivityContractNodeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания договора по основной деятельности</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор по основной деятельности</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора по основной деятельности со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>MAINACTCONTRID - ИД текущей версии договора по основной деятельности</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора по основной деятельности</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор по основной деятельности</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания договора по основной деятельности</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор по основной деятельности</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>MAINACTCONTRNODEID - ИД договора по основной деятельности со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>MAINACTCONTRID - ИД текущей версии договора по основной деятельности</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора по основной деятельности</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор по основной деятельности</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BMainActivityContractNodeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BMainActivityContractNodeBrowseListByParam", "dsB2BMainActivityContractNodeBrowseListByParamCount", params);
        return result;
    }





}
