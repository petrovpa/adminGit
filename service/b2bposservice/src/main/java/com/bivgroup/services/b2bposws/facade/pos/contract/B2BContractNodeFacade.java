/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BContractNode
 *
 * @author reson
 */
@IdGen(entityName="B2B_CONTRNODE",idFieldName="CONTRNODEID")
@BOName("B2BContractNode")
public class B2BContractNodeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД текущей версии договора</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"LASTVERNUMBER", "RVERSION"})
    public Map<String,Object> dsB2BContractNodeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractNodeInsert", params);
        result.put("CONTRNODEID", params.get("CONTRNODEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД текущей версии договора</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRNODEID", "LASTVERNUMBER", "RVERSION"})
    public Map<String,Object> dsB2BContractNodeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractNodeInsert", params);
        result.put("CONTRNODEID", params.get("CONTRNODEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД текущей версии договора</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRNODEID"})
    public Map<String,Object> dsB2BContractNodeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractNodeUpdate", params);
        result.put("CONTRNODEID", params.get("CONTRNODEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД текущей версии договора</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRNODEID"})
    public Map<String,Object> dsB2BContractNodeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractNodeUpdate", params);
        result.put("CONTRNODEID", params.get("CONTRNODEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRNODEID"})
    public void dsB2BContractNodeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractNodeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД текущей версии договора</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД текущей версии договора</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>LASTVERNUMBER - Номер последней версии</LI>
     * <LI>RVERSION - Номер версии (оптимистичная блокировка)</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractNodeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractNodeBrowseListByParam", "dsB2BContractNodeBrowseListByParamCount", params);
        return result;
    }





}
