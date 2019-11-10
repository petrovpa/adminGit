/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.product;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.discriminator.Discriminator;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BProductStructureMultiProduct
 *
 * @author reson
 */
@Discriminator(5)
@IdGen(entityName="B2B_PRODSTRUCT",idFieldName="PRODSTRUCTID")
@BOName("B2BProductStructureMultiProduct")
public class B2BProductStructureMultiProductFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductStructureMultiProductCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductStructureMultiProductInsert", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureMultiProductInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductStructureMultiProductInsert", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureMultiProductUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductStructureMultiProductUpdate", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureMultiProductModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductStructureMultiProductUpdate", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public void dsB2BProductStructureMultiProductDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductStructureMultiProductDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CHILDPRODVERID - ИД версии подчиненного продукта</LI>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SDCALCMETHOD - Правило определения даты начала - условие</LI>
     * <LI>SDLAG - Правило определения даты начала - лаг</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductStructureMultiProductBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductStructureMultiProductBrowseListByParam", "dsB2BProductStructureMultiProductBrowseListByParamCount", params);
        return result;
    }





}
