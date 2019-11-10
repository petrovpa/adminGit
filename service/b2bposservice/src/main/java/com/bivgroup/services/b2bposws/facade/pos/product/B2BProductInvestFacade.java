/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BProductInvest
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@Discriminator(2)
@IdGen(entityName="B2B_PRODINVEST",idFieldName="PRODINVESTID")
@BOName("B2BProductInvest")
public class B2BProductInvestFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FINISHDATE - Дата окончания действия</LI>
     * <LI>PRODINVESTID - ИД связи стратегии с продуктом</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINVESTID - ИД связи стратегии с продуктом</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductInvestCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductInvestInsert", params);
        result.put("PRODINVESTID", params.get("PRODINVESTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FINISHDATE - Дата окончания действия</LI>
     * <LI>PRODINVESTID - ИД связи стратегии с продуктом</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINVESTID - ИД связи стратегии с продуктом</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINVESTID"})
    public Map<String,Object> dsB2BProductInvestInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductInvestInsert", params);
        result.put("PRODINVESTID", params.get("PRODINVESTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FINISHDATE - Дата окончания действия</LI>
     * <LI>PRODINVESTID - ИД связи стратегии с продуктом</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINVESTID - ИД связи стратегии с продуктом</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINVESTID"})
    public Map<String,Object> dsB2BProductInvestUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductInvestUpdate", params);
        result.put("PRODINVESTID", params.get("PRODINVESTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FINISHDATE - Дата окончания действия</LI>
     * <LI>PRODINVESTID - ИД связи стратегии с продуктом</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODINVESTID - ИД связи стратегии с продуктом</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINVESTID"})
    public Map<String,Object> dsB2BProductInvestModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductInvestUpdate", params);
        result.put("PRODINVESTID", params.get("PRODINVESTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODINVESTID - ИД связи стратегии с продуктом</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODINVESTID"})
    public void dsB2BProductInvestDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductInvestDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FINISHDATE - Дата окончания действия</LI>
     * <LI>PRODINVESTID - ИД связи стратегии с продуктом</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>FINISHDATE - Дата окончания действия</LI>
     * <LI>PRODINVESTID - ИД связи стратегии с продуктом</LI>
     * <LI>INVESTSTRATEGYID - ИД стратегии</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>STARTDATE - Дата начала действия</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductInvestBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductInvestBrowseListByParam", "dsB2BProductInvestBrowseListByParamCount", params);
        return result;
    }





}
