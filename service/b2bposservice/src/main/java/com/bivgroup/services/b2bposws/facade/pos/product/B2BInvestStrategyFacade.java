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
 * Фасад для сущности B2BInvestStrategy
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_INVESTSTRATEGY",idFieldName="INVESTSTRATEGYID")
@BOName("B2BInvestStrategy")
public class B2BInvestStrategyFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVESTSTRATEGYID - ИД инвестиционной стратегии</LI>
     * <LI>MAXAMVALUE - Максимальная страховая сумма</LI>
     * <LI>MINAMVALUE - Минимальная страховая сумма</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>RATE - Размер купона</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TERMID - ИД срока страхования</LI>
     * <LI>TERMYEARCOUNT - Количество лет срока страхования</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>ISFORPPO - Флаг купона для ППО</LI>
     * <LI>ISFORSALE - Флаг купона для продажи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVESTSTRATEGYID - ИД инвестиционной стратегии</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestStrategyCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestStrategyInsert", params);
        result.put("INVESTSTRATEGYID", params.get("INVESTSTRATEGYID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVESTSTRATEGYID - ИД инвестиционной стратегии</LI>
     * <LI>MAXAMVALUE - Максимальная страховая сумма</LI>
     * <LI>MINAMVALUE - Минимальная страховая сумма</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>RATE - Размер купона</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TERMID - ИД срока страхования</LI>
     * <LI>TERMYEARCOUNT - Количество лет срока страхования</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>ISFORPPO - Флаг купона для ППО</LI>
     * <LI>ISFORSALE - Флаг купона для продажи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVESTSTRATEGYID - ИД инвестиционной стратегии</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVESTSTRATEGYID"})
    public Map<String,Object> dsB2BInvestStrategyInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestStrategyInsert", params);
        result.put("INVESTSTRATEGYID", params.get("INVESTSTRATEGYID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVESTSTRATEGYID - ИД инвестиционной стратегии</LI>
     * <LI>MAXAMVALUE - Максимальная страховая сумма</LI>
     * <LI>MINAMVALUE - Минимальная страховая сумма</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>RATE - Размер купона</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TERMID - ИД срока страхования</LI>
     * <LI>TERMYEARCOUNT - Количество лет срока страхования</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>ISFORPPO - Флаг купона для ППО</LI>
     * <LI>ISFORSALE - Флаг купона для продажи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVESTSTRATEGYID - ИД инвестиционной стратегии</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVESTSTRATEGYID"})
    public Map<String,Object> dsB2BInvestStrategyUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestStrategyUpdate", params);
        result.put("INVESTSTRATEGYID", params.get("INVESTSTRATEGYID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVESTSTRATEGYID - ИД инвестиционной стратегии</LI>
     * <LI>MAXAMVALUE - Максимальная страховая сумма</LI>
     * <LI>MINAMVALUE - Минимальная страховая сумма</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>RATE - Размер купона</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TERMID - ИД срока страхования</LI>
     * <LI>TERMYEARCOUNT - Количество лет срока страхования</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>ISFORPPO - Флаг купона для ППО</LI>
     * <LI>ISFORSALE - Флаг купона для продажи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVESTSTRATEGYID - ИД инвестиционной стратегии</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVESTSTRATEGYID"})
    public Map<String,Object> dsB2BInvestStrategyModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestStrategyUpdate", params);
        result.put("INVESTSTRATEGYID", params.get("INVESTSTRATEGYID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVESTSTRATEGYID - ИД инвестиционной стратегии</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVESTSTRATEGYID"})
    public void dsB2BInvestStrategyDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BInvestStrategyDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVESTSTRATEGYID - ИД инвестиционной стратегии</LI>
     * <LI>MAXAMVALUE - Максимальная страховая сумма</LI>
     * <LI>MINAMVALUE - Минимальная страховая сумма</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>RATE - Размер купона</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TERMID - ИД срока страхования</LI>
     * <LI>TERMYEARCOUNT - Количество лет срока страхования</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>ISFORPPO - Флаг купона для ППО</LI>
     * <LI>ISFORSALE - Флаг купона для продажи</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CODE - Код</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего запись</LI>
     * <LI>CURRENCYID - ИД валюты</LI>
     * <LI>INVBASEACTIVEID - ИД базового актива</LI>
     * <LI>INVESTSTRATEGYID - ИД инвестиционной стратегии</LI>
     * <LI>MAXAMVALUE - Максимальная страховая сумма</LI>
     * <LI>MINAMVALUE - Минимальная страховая сумма</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>RATE - Размер купона</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * <LI>TERMID - ИД срока страхования</LI>
     * <LI>TERMYEARCOUNT - Количество лет срока страхования</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>ISFORPPO - Флаг купона для ППО</LI>
     * <LI>ISFORSALE - Флаг купона для продажи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestStrategyBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BInvestStrategyBrowseListByParam", "dsB2BInvestStrategyBrowseListByParamCount", params);
        return result;
    }





}
