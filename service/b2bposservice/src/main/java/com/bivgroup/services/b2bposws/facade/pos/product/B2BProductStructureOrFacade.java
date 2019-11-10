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
 * Фасад для сущности B2BProductStructureOr
 *
 * @author reson
 */
@Discriminator(1)
@IdGen(entityName="B2B_PRODSTRUCT",idFieldName="PRODSTRUCTID")
@BOName("B2BProductStructureOr")
public class B2BProductStructureOrFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductStructureOrCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductStructureOrInsert", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureOrInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductStructureOrInsert", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureOrUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductStructureOrUpdate", params);
        result.put("PRODSTRUCTID", params.get("PRODSTRUCTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODSTRUCTID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODSTRUCTID"})
    public Map<String,Object> dsB2BProductStructureOrModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductStructureOrUpdate", params);
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
    public void dsB2BProductStructureOrDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductStructureOrDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>DISCRIMINATOR - Тип сущности</LI>
     * <LI>PRODSTRUCTID - ИД</LI>
     * <LI>PARENTSTRUCTID - ИД родительской структуры</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductStructureOrBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductStructureOrBrowseListByParam", "dsB2BProductStructureOrBrowseListByParamCount", params);
        return result;
    }





}
