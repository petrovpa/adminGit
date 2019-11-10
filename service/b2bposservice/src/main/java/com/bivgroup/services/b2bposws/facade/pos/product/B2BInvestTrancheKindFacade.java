/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BInvestTrancheKind
 *
 * @author reson
 */
@IdGen(entityName="B2B_INVTRANCHEKIND",idFieldName="INVTRANCHEKINDID")
@BOName("B2BInvestTrancheKind")
public class B2BInvestTrancheKindFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>MAINACTCONTRID - ИД партнерского договора</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PARTNER - Партнер</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestTrancheKindCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestTrancheKindInsert", params);
        result.put("INVTRANCHEKINDID", params.get("INVTRANCHEKINDID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>MAINACTCONTRID - ИД партнерского договора</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PARTNER - Партнер</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTRANCHEKINDID"})
    public Map<String,Object> dsB2BInvestTrancheKindInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BInvestTrancheKindInsert", params);
        result.put("INVTRANCHEKINDID", params.get("INVTRANCHEKINDID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>MAINACTCONTRID - ИД партнерского договора</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PARTNER - Партнер</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTRANCHEKINDID"})
    public Map<String,Object> dsB2BInvestTrancheKindUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestTrancheKindUpdate", params);
        result.put("INVTRANCHEKINDID", params.get("INVTRANCHEKINDID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>MAINACTCONTRID - ИД партнерского договора</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PARTNER - Партнер</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTRANCHEKINDID"})
    public Map<String,Object> dsB2BInvestTrancheKindModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BInvestTrancheKindUpdate", params);
        result.put("INVTRANCHEKINDID", params.get("INVTRANCHEKINDID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVTRANCHEKINDID"})
    public void dsB2BInvestTrancheKindDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BInvestTrancheKindDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>MAINACTCONTRID - ИД партнерского договора</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PARTNER - Партнер</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVTRANCHEKINDID - ИД вида транша</LI>
     * <LI>MAINACTCONTRID - ИД партнерского договора</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PARTNER - Партнер</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BInvestTrancheKindBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BInvestTrancheKindBrowseListByParam", "dsB2BInvestTrancheKindBrowseListByParamCount", params);
        return result;
    }





}
