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
 * Фасад для сущности B2BProductActivation
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODACTIV",idFieldName="PRODACTIVID")
@BOName("B2BProductActivation")
public class B2BProductActivationFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ENDDATE - Дата окончания действия активации по продукту</LI>
     * <LI>PRODACTIVID - ИД активации продукта</LI>
     * <LI>PRODACTIVTYPEID - Ссылка на тип активации</LI>
     * <LI>PRODVERID - Ссылка на версию активируемого продукта</LI>
     * <LI>STARTDATE - Дата начала действия активации по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODACTIVID - ИД активации продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductActivationCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductActivationInsert", params);
        result.put("PRODACTIVID", params.get("PRODACTIVID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ENDDATE - Дата окончания действия активации по продукту</LI>
     * <LI>PRODACTIVID - ИД активации продукта</LI>
     * <LI>PRODACTIVTYPEID - Ссылка на тип активации</LI>
     * <LI>PRODVERID - Ссылка на версию активируемого продукта</LI>
     * <LI>STARTDATE - Дата начала действия активации по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODACTIVID - ИД активации продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODACTIVID"})
    public Map<String,Object> dsB2BProductActivationInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductActivationInsert", params);
        result.put("PRODACTIVID", params.get("PRODACTIVID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ENDDATE - Дата окончания действия активации по продукту</LI>
     * <LI>PRODACTIVID - ИД активации продукта</LI>
     * <LI>PRODACTIVTYPEID - Ссылка на тип активации</LI>
     * <LI>PRODVERID - Ссылка на версию активируемого продукта</LI>
     * <LI>STARTDATE - Дата начала действия активации по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODACTIVID - ИД активации продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODACTIVID"})
    public Map<String,Object> dsB2BProductActivationUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductActivationUpdate", params);
        result.put("PRODACTIVID", params.get("PRODACTIVID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ENDDATE - Дата окончания действия активации по продукту</LI>
     * <LI>PRODACTIVID - ИД активации продукта</LI>
     * <LI>PRODACTIVTYPEID - Ссылка на тип активации</LI>
     * <LI>PRODVERID - Ссылка на версию активируемого продукта</LI>
     * <LI>STARTDATE - Дата начала действия активации по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODACTIVID - ИД активации продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODACTIVID"})
    public Map<String,Object> dsB2BProductActivationModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductActivationUpdate", params);
        result.put("PRODACTIVID", params.get("PRODACTIVID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODACTIVID - ИД активации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODACTIVID"})
    public void dsB2BProductActivationDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductActivationDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ENDDATE - Дата окончания действия активации по продукту</LI>
     * <LI>PRODACTIVID - ИД активации продукта</LI>
     * <LI>PRODACTIVTYPEID - Ссылка на тип активации</LI>
     * <LI>PRODVERID - Ссылка на версию активируемого продукта</LI>
     * <LI>STARTDATE - Дата начала действия активации по продукту</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ENDDATE - Дата окончания действия активации по продукту</LI>
     * <LI>PRODACTIVID - ИД активации продукта</LI>
     * <LI>PRODACTIVTYPEID - Ссылка на тип активации</LI>
     * <LI>PRODVERID - Ссылка на версию активируемого продукта</LI>
     * <LI>STARTDATE - Дата начала действия активации по продукту</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductActivationBrowseListByParam(Map<String, Object> params) throws Exception {      
        Map<String,Object> result = this.selectQuery("dsB2BProductActivationBrowseListByParam", "dsB2BProductActivationBrowseListByParamCount", params);
        return result;
    }





}
