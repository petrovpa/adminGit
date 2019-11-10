/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.paws.facade.pa;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности PaContract
 *
 * @author reson
 */
@IdGen(entityName="PA_CONTRACT",idFieldName="PAOBJECTID")
@BOName("PaContract")
public class PaContractFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ид договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PAOBJECTID - Ид объекта</LI>
     * <LI>PAUSERID - Ид пользователя</LI>
     * <LI>RELIABILITYLEVEL - Уровень достоверности</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAOBJECTID - Ид объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsPaContractCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPaContractInsert", params);
        result.put("PAOBJECTID", params.get("PAOBJECTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ид договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PAOBJECTID - Ид объекта</LI>
     * <LI>PAUSERID - Ид пользователя</LI>
     * <LI>RELIABILITYLEVEL - Уровень достоверности</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAOBJECTID - Ид объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAOBJECTID"})
    public Map<String,Object> dsPaContractInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsPaContractInsert", params);
        result.put("PAOBJECTID", params.get("PAOBJECTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ид договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PAOBJECTID - Ид объекта</LI>
     * <LI>PAUSERID - Ид пользователя</LI>
     * <LI>RELIABILITYLEVEL - Уровень достоверности</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAOBJECTID - Ид объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAOBJECTID"})
    public Map<String,Object> dsPaContractUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPaContractUpdate", params);
        result.put("PAOBJECTID", params.get("PAOBJECTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ид договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PAOBJECTID - Ид объекта</LI>
     * <LI>PAUSERID - Ид пользователя</LI>
     * <LI>RELIABILITYLEVEL - Уровень достоверности</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PAOBJECTID - Ид объекта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAOBJECTID"})
    public Map<String,Object> dsPaContractModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsPaContractUpdate", params);
        result.put("PAOBJECTID", params.get("PAOBJECTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PAOBJECTID - Ид объекта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PAOBJECTID"})
    public void dsPaContractDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsPaContractDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - Ид договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PAOBJECTID - Ид объекта</LI>
     * <LI>PAUSERID - Ид пользователя</LI>
     * <LI>RELIABILITYLEVEL - Уровень достоверности</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - Ид договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший</LI>
     * <LI>NAME - Имя пользователя</LI>
     * <LI>PAOBJECTID - Ид объекта</LI>
     * <LI>PAUSERID - Ид пользователя</LI>
     * <LI>RELIABILITYLEVEL - Уровень достоверности</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsPaContractBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsPaContractBrowseListByParam", "dsPaContractBrowseListByParamCount", params);
        return result;
    }





}
