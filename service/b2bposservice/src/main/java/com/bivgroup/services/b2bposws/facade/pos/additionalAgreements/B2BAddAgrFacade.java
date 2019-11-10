/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.additionalAgreements;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BAddAgr
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_ADDAGR",idFieldName="ADDAGRID")
@State(idFieldName = "ADDAGRID", startStateName = "B2B_ADDAGR_DRAFT", typeSysName = "B2B_ADDAGR")
@BOName("B2BAddAgr")
public class B2BAddAgrFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * <LI>AGRNUMBER - Номер</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>INITIATOR - Ид инициатора</LI>
     * <LI>PRODCONFIGID - Ид продукта</LI>
     * <LI>SIGNDATE - Дата принятия</LI>
     * <LI>STARTDATE - Дата создания заявки</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TYPEID - Тип допса</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAddAgrCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BAddAgrInsert", params);
        result.put("ADDAGRID", params.get("ADDAGRID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * <LI>AGRNUMBER - Номер</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>INITIATOR - Ид инициатора</LI>
     * <LI>PRODCONFIGID - Ид продукта</LI>
     * <LI>SIGNDATE - Дата принятия</LI>
     * <LI>STARTDATE - Дата создания заявки</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TYPEID - Тип допса</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRID"})
    public Map<String,Object> dsB2BAddAgrInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BAddAgrInsert", params);
        result.put("ADDAGRID", params.get("ADDAGRID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * <LI>AGRNUMBER - Номер</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>INITIATOR - Ид инициатора</LI>
     * <LI>PRODCONFIGID - Ид продукта</LI>
     * <LI>SIGNDATE - Дата принятия</LI>
     * <LI>STARTDATE - Дата создания заявки</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TYPEID - Тип допса</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRID"})
    public Map<String,Object> dsB2BAddAgrUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BAddAgrUpdate", params);
        result.put("ADDAGRID", params.get("ADDAGRID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * <LI>AGRNUMBER - Номер</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>INITIATOR - Ид инициатора</LI>
     * <LI>PRODCONFIGID - Ид продукта</LI>
     * <LI>SIGNDATE - Дата принятия</LI>
     * <LI>STARTDATE - Дата создания заявки</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TYPEID - Тип допса</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRID"})
    public Map<String,Object> dsB2BAddAgrModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BAddAgrUpdate", params);
        result.put("ADDAGRID", params.get("ADDAGRID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRID"})
    public void dsB2BAddAgrDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BAddAgrDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * <LI>AGRNUMBER - Номер</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>INITIATOR - Ид инициатора</LI>
     * <LI>PRODCONFIGID - Ид продукта</LI>
     * <LI>SIGNDATE - Дата принятия</LI>
     * <LI>STARTDATE - Дата создания заявки</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TYPEID - Тип допса</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRID - ИД</LI>
     * <LI>AGRNUMBER - Номер</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания</LI>
     * <LI>CREATEUSERID - Пользователь создавший запись</LI>
     * <LI>INITIATOR - Ид инициатора</LI>
     * <LI>PRODCONFIGID - Ид продукта</LI>
     * <LI>SIGNDATE - Дата принятия</LI>
     * <LI>STARTDATE - Дата создания заявки</LI>
     * <LI>STATEID - Состояние</LI>
     * <LI>TYPEID - Тип допса</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - Пользователь изменивший запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAddAgrBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BAddAgrBrowseListByParam", "dsB2BAddAgrBrowseListByParamCount", params);
        return result;
    }





}
