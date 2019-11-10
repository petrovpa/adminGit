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
 * Фасад для сущности B2BProductInsuranceCover
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODINSCOVER",idFieldName="INSCOVERID")
@BOName("B2BProductInsuranceCover")
public class B2BProductInsuranceCoverFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>INSCOVERID - ИД страхового покрытия</LI>
     * <LI>NAME - Наименование страхового покрытия</LI>
     * <LI>SYSNAME - Системное наименование страхового покрытия</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSCOVERID - ИД страхового покрытия</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductInsuranceCoverCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductInsuranceCoverInsert", params);
        result.put("INSCOVERID", params.get("INSCOVERID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>INSCOVERID - ИД страхового покрытия</LI>
     * <LI>NAME - Наименование страхового покрытия</LI>
     * <LI>SYSNAME - Системное наименование страхового покрытия</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSCOVERID - ИД страхового покрытия</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSCOVERID"})
    public Map<String,Object> dsB2BProductInsuranceCoverInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductInsuranceCoverInsert", params);
        result.put("INSCOVERID", params.get("INSCOVERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>INSCOVERID - ИД страхового покрытия</LI>
     * <LI>NAME - Наименование страхового покрытия</LI>
     * <LI>SYSNAME - Системное наименование страхового покрытия</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSCOVERID - ИД страхового покрытия</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSCOVERID"})
    public Map<String,Object> dsB2BProductInsuranceCoverUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductInsuranceCoverUpdate", params);
        result.put("INSCOVERID", params.get("INSCOVERID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>INSCOVERID - ИД страхового покрытия</LI>
     * <LI>NAME - Наименование страхового покрытия</LI>
     * <LI>SYSNAME - Системное наименование страхового покрытия</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSCOVERID - ИД страхового покрытия</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSCOVERID"})
    public Map<String,Object> dsB2BProductInsuranceCoverModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductInsuranceCoverUpdate", params);
        result.put("INSCOVERID", params.get("INSCOVERID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INSCOVERID - ИД страхового покрытия</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INSCOVERID"})
    public void dsB2BProductInsuranceCoverDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductInsuranceCoverDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>INSCOVERID - ИД страхового покрытия</LI>
     * <LI>NAME - Наименование страхового покрытия</LI>
     * <LI>SYSNAME - Системное наименование страхового покрытия</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSCOVERID - ИД страхового покрытия</LI>
     * <LI>NAME - Наименование страхового покрытия</LI>
     * <LI>SYSNAME - Системное наименование страхового покрытия</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductInsuranceCoverBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductInsuranceCoverBrowseListByParam", "dsB2BProductInsuranceCoverBrowseListByParamCount", params);
        return result;
    }





}
