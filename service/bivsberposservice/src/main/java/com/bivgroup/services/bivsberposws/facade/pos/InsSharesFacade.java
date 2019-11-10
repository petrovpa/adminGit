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
 * Фасад для сущности InsShares
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="INS_SHARES",idFieldName="SHAREID")
@BOName("InsShares")
public class InsSharesFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания акции</LI>
     * <LI>CREATEUSERID - Пользователь создавший акцию</LI>
     * <LI>FINISHDATE - Дата окончания акции</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>NAME - Наименование акции</LI>
     * <LI>PRODVERID - Связь с продуктом</LI>
     * <LI>STARTDATE - Дата начала действия акции</LI>
     * <LI>SYSNAME - Системное наименование акции</LI>
     * <LI>TYPESYSNAME - Вид акции</LI>
     * <LI>UPDATEDATE - Дата изменения акции</LI>
     * <LI>UPDATEUSERID - Ид пользователя изменившего акцию</LI>
     * <LI>VALUE - Значение коеффициента скидки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SHAREID - ИД акции</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsInsSharesCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsSharesInsert", params);
        result.put("SHAREID", params.get("SHAREID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания акции</LI>
     * <LI>CREATEUSERID - Пользователь создавший акцию</LI>
     * <LI>FINISHDATE - Дата окончания акции</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>NAME - Наименование акции</LI>
     * <LI>PRODVERID - Связь с продуктом</LI>
     * <LI>STARTDATE - Дата начала действия акции</LI>
     * <LI>SYSNAME - Системное наименование акции</LI>
     * <LI>TYPESYSNAME - Вид акции</LI>
     * <LI>UPDATEDATE - Дата изменения акции</LI>
     * <LI>UPDATEUSERID - Ид пользователя изменившего акцию</LI>
     * <LI>VALUE - Значение коеффициента скидки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SHAREID - ИД акции</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"SHAREID"})
    public Map<String,Object> dsInsSharesInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsSharesInsert", params);
        result.put("SHAREID", params.get("SHAREID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания акции</LI>
     * <LI>CREATEUSERID - Пользователь создавший акцию</LI>
     * <LI>FINISHDATE - Дата окончания акции</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>NAME - Наименование акции</LI>
     * <LI>PRODVERID - Связь с продуктом</LI>
     * <LI>STARTDATE - Дата начала действия акции</LI>
     * <LI>SYSNAME - Системное наименование акции</LI>
     * <LI>TYPESYSNAME - Вид акции</LI>
     * <LI>UPDATEDATE - Дата изменения акции</LI>
     * <LI>UPDATEUSERID - Ид пользователя изменившего акцию</LI>
     * <LI>VALUE - Значение коеффициента скидки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SHAREID - ИД акции</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"SHAREID"})
    public Map<String,Object> dsInsSharesUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsSharesUpdate", params);
        result.put("SHAREID", params.get("SHAREID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания акции</LI>
     * <LI>CREATEUSERID - Пользователь создавший акцию</LI>
     * <LI>FINISHDATE - Дата окончания акции</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>NAME - Наименование акции</LI>
     * <LI>PRODVERID - Связь с продуктом</LI>
     * <LI>STARTDATE - Дата начала действия акции</LI>
     * <LI>SYSNAME - Системное наименование акции</LI>
     * <LI>TYPESYSNAME - Вид акции</LI>
     * <LI>UPDATEDATE - Дата изменения акции</LI>
     * <LI>UPDATEUSERID - Ид пользователя изменившего акцию</LI>
     * <LI>VALUE - Значение коеффициента скидки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>SHAREID - ИД акции</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"SHAREID"})
    public Map<String,Object> dsInsSharesModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsSharesUpdate", params);
        result.put("SHAREID", params.get("SHAREID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>SHAREID - ИД акции</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"SHAREID"})
    public void dsInsSharesDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsInsSharesDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания акции</LI>
     * <LI>CREATEUSERID - Пользователь создавший акцию</LI>
     * <LI>FINISHDATE - Дата окончания акции</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>NAME - Наименование акции</LI>
     * <LI>PRODVERID - Связь с продуктом</LI>
     * <LI>STARTDATE - Дата начала действия акции</LI>
     * <LI>SYSNAME - Системное наименование акции</LI>
     * <LI>TYPESYSNAME - Вид акции</LI>
     * <LI>UPDATEDATE - Дата изменения акции</LI>
     * <LI>UPDATEUSERID - Ид пользователя изменившего акцию</LI>
     * <LI>VALUE - Значение коеффициента скидки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания акции</LI>
     * <LI>CREATEUSERID - Пользователь создавший акцию</LI>
     * <LI>FINISHDATE - Дата окончания акции</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>NAME - Наименование акции</LI>
     * <LI>PRODVERID - Связь с продуктом</LI>
     * <LI>STARTDATE - Дата начала действия акции</LI>
     * <LI>SYSNAME - Системное наименование акции</LI>
     * <LI>TYPESYSNAME - Вид акции</LI>
     * <LI>UPDATEDATE - Дата изменения акции</LI>
     * <LI>UPDATEUSERID - Ид пользователя изменившего акцию</LI>
     * <LI>VALUE - Значение коеффициента скидки</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsInsSharesBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsInsSharesBrowseListByParam", "dsInsSharesBrowseListByParamCount", params);
        return result;
    }





}
