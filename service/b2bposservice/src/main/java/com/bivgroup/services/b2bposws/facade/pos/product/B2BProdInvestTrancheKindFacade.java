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
 * Фасад для сущности B2BProdInvestTrancheKind
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODINVTRANCHEKIND",idFieldName="INVPRODTRANCHEKINDID")
@BOName("B2BProdInvestTrancheKind")
public class B2BProdInvestTrancheKindFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>INSTRANCHEKINDID - ИД вида транша</LI>
     * <LI>INVPRODTRANCHEKINDID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVPRODTRANCHEKINDID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProdInvestTrancheKindCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProdInvestTrancheKindInsert", params);
        result.put("INVPRODTRANCHEKINDID", params.get("INVPRODTRANCHEKINDID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>INSTRANCHEKINDID - ИД вида транша</LI>
     * <LI>INVPRODTRANCHEKINDID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVPRODTRANCHEKINDID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVPRODTRANCHEKINDID"})
    public Map<String,Object> dsB2BProdInvestTrancheKindInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProdInvestTrancheKindInsert", params);
        result.put("INVPRODTRANCHEKINDID", params.get("INVPRODTRANCHEKINDID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>INSTRANCHEKINDID - ИД вида транша</LI>
     * <LI>INVPRODTRANCHEKINDID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVPRODTRANCHEKINDID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVPRODTRANCHEKINDID"})
    public Map<String,Object> dsB2BProdInvestTrancheKindUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProdInvestTrancheKindUpdate", params);
        result.put("INVPRODTRANCHEKINDID", params.get("INVPRODTRANCHEKINDID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>INSTRANCHEKINDID - ИД вида транша</LI>
     * <LI>INVPRODTRANCHEKINDID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INVPRODTRANCHEKINDID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVPRODTRANCHEKINDID"})
    public Map<String,Object> dsB2BProdInvestTrancheKindModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProdInvestTrancheKindUpdate", params);
        result.put("INVPRODTRANCHEKINDID", params.get("INVPRODTRANCHEKINDID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>INVPRODTRANCHEKINDID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"INVPRODTRANCHEKINDID"})
    public void dsB2BProdInvestTrancheKindDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProdInvestTrancheKindDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>INSTRANCHEKINDID - ИД вида транша</LI>
     * <LI>INVPRODTRANCHEKINDID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>INSTRANCHEKINDID - ИД вида транша</LI>
     * <LI>INVPRODTRANCHEKINDID - ИД</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProdInvestTrancheKindBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProdInvestTrancheKindBrowseListByParam", "dsB2BProdInvestTrancheKindBrowseListByParamCount", params);
        return result;
    }





}
