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
 * Фасад для сущности InsPromocodes
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="INS_PROMOCODES",idFieldName="PROMOCODEID")
@BOName("InsPromocodes")
public class InsPromocodesFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания промокода</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего промокод</LI>
     * <LI>PROMOCODEID - ИД промокода</LI>
     * <LI>PROMOCODE - промокод</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>UPDATEDATE - Дата изменения промокода</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего промокод</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PROMOCODEID - ИД промокода</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsInsPromocodesCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsPromocodesInsert", params);
        result.put("PROMOCODEID", params.get("PROMOCODEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания промокода</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего промокод</LI>
     * <LI>PROMOCODEID - ИД промокода</LI>
     * <LI>PROMOCODE - промокод</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>UPDATEDATE - Дата изменения промокода</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего промокод</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PROMOCODEID - ИД промокода</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PROMOCODEID"})
    public Map<String,Object> dsInsPromocodesInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsInsPromocodesInsert", params);
        result.put("PROMOCODEID", params.get("PROMOCODEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания промокода</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего промокод</LI>
     * <LI>PROMOCODEID - ИД промокода</LI>
     * <LI>PROMOCODE - промокод</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>UPDATEDATE - Дата изменения промокода</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего промокод</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PROMOCODEID - ИД промокода</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PROMOCODEID"})
    public Map<String,Object> dsInsPromocodesUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsPromocodesUpdate", params);
        result.put("PROMOCODEID", params.get("PROMOCODEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания промокода</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего промокод</LI>
     * <LI>PROMOCODEID - ИД промокода</LI>
     * <LI>PROMOCODE - промокод</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>UPDATEDATE - Дата изменения промокода</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего промокод</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PROMOCODEID - ИД промокода</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PROMOCODEID"})
    public Map<String,Object> dsInsPromocodesModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsInsPromocodesUpdate", params);
        result.put("PROMOCODEID", params.get("PROMOCODEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PROMOCODEID - ИД промокода</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PROMOCODEID"})
    public void dsInsPromocodesDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsInsPromocodesDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CREATEDATE - Дата создания промокода</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего промокод</LI>
     * <LI>PROMOCODEID - ИД промокода</LI>
     * <LI>PROMOCODE - промокод</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>UPDATEDATE - Дата изменения промокода</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего промокод</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CREATEDATE - Дата создания промокода</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего промокод</LI>
     * <LI>PROMOCODEID - ИД промокода</LI>
     * <LI>PROMOCODE - промокод</LI>
     * <LI>SHAREID - ИД акции</LI>
     * <LI>UPDATEDATE - Дата изменения промокода</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего промокод</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsInsPromocodesBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsInsPromocodesBrowseListByParam", "dsInsPromocodesBrowseListByParamCount", params);
        return result;
    }





}
