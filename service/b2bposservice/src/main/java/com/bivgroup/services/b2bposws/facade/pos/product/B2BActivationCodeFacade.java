/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BActivationCode
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_ACTIVCODE",idFieldName="ACTIVCODEID")
@BOName("B2BActivationCode")
public class B2BActivationCodeFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTIVATIONDATE - Дата активации</LI>
     * <LI>CODE - Код активации</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>ENDDATE - Дата окончания действия кода активации</LI>
     * <LI>ACTIVCODEID - ИД кода активации</LI>
     * <LI>ISACTIVATED - Флаг код активирован</LI>
     * <LI>NUM - Номер договора</LI>
     * <LI>PRODACTIVID - Ссылка на активацию продукта</LI>
     * <LI>SERIES - Серия договора</LI>
     * <LI>STARTDATE - Дата начала действия кода</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ACTIVCODEID - ИД кода активации</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BActivationCodeCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BActivationCodeInsert", params);
        result.put("ACTIVCODEID", params.get("ACTIVCODEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTIVATIONDATE - Дата активации</LI>
     * <LI>CODE - Код активации</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>ENDDATE - Дата окончания действия кода активации</LI>
     * <LI>ACTIVCODEID - ИД кода активации</LI>
     * <LI>ISACTIVATED - Флаг код активирован</LI>
     * <LI>NUM - Номер договора</LI>
     * <LI>PRODACTIVID - Ссылка на активацию продукта</LI>
     * <LI>SERIES - Серия договора</LI>
     * <LI>STARTDATE - Дата начала действия кода</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ACTIVCODEID - ИД кода активации</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ACTIVCODEID"})
    public Map<String,Object> dsB2BActivationCodeInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BActivationCodeInsert", params);
        result.put("ACTIVCODEID", params.get("ACTIVCODEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTIVATIONDATE - Дата активации</LI>
     * <LI>CODE - Код активации</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>ENDDATE - Дата окончания действия кода активации</LI>
     * <LI>ACTIVCODEID - ИД кода активации</LI>
     * <LI>ISACTIVATED - Флаг код активирован</LI>
     * <LI>NUM - Номер договора</LI>
     * <LI>PRODACTIVID - Ссылка на активацию продукта</LI>
     * <LI>SERIES - Серия договора</LI>
     * <LI>STARTDATE - Дата начала действия кода</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ACTIVCODEID - ИД кода активации</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ACTIVCODEID"})
    public Map<String,Object> dsB2BActivationCodeUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BActivationCodeUpdate", params);
        result.put("ACTIVCODEID", params.get("ACTIVCODEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTIVATIONDATE - Дата активации</LI>
     * <LI>CODE - Код активации</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>ENDDATE - Дата окончания действия кода активации</LI>
     * <LI>ACTIVCODEID - ИД кода активации</LI>
     * <LI>ISACTIVATED - Флаг код активирован</LI>
     * <LI>NUM - Номер договора</LI>
     * <LI>PRODACTIVID - Ссылка на активацию продукта</LI>
     * <LI>SERIES - Серия договора</LI>
     * <LI>STARTDATE - Дата начала действия кода</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ACTIVCODEID - ИД кода активации</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ACTIVCODEID"})
    public Map<String,Object> dsB2BActivationCodeModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BActivationCodeUpdate", params);
        result.put("ACTIVCODEID", params.get("ACTIVCODEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTIVCODEID - ИД кода активации</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ACTIVCODEID"})
    public void dsB2BActivationCodeDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BActivationCodeDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ACTIVATIONDATE - Дата активации</LI>
     * <LI>CODE - Код активации</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>ENDDATE - Дата окончания действия кода активации</LI>
     * <LI>ACTIVCODEID - ИД кода активации</LI>
     * <LI>ISACTIVATED - Флаг код активирован</LI>
     * <LI>NUM - Номер договора</LI>
     * <LI>PRODACTIVID - Ссылка на активацию продукта</LI>
     * <LI>SERIES - Серия договора</LI>
     * <LI>STARTDATE - Дата начала действия кода</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ACTIVATIONDATE - Дата активации</LI>
     * <LI>CODE - Код активации</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>ENDDATE - Дата окончания действия кода активации</LI>
     * <LI>ACTIVCODEID - ИД кода активации</LI>
     * <LI>ISACTIVATED - Флаг код активирован</LI>
     * <LI>NUM - Номер договора</LI>
     * <LI>PRODACTIVID - Ссылка на активацию продукта</LI>
     * <LI>SERIES - Серия договора</LI>
     * <LI>STARTDATE - Дата начала действия кода</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя, изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BActivationCodeBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BActivationCodeBrowseListByParam", "dsB2BActivationCodeBrowseListByParamCount", params);
        return result;
    }





}
