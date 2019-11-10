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
 * Фасад для сущности B2BProductTerm
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODTERM",idFieldName="PRODTERMID")
@BOName("B2BProductTerm")
public class B2BProductTermFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODTERMID - ИД записи</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TERMID - ИД срока</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODTERMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductTermCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductTermInsert", params);
        result.put("PRODTERMID", params.get("PRODTERMID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODTERMID - ИД записи</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TERMID - ИД срока</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODTERMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODTERMID"})
    public Map<String,Object> dsB2BProductTermInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductTermInsert", params);
        result.put("PRODTERMID", params.get("PRODTERMID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODTERMID - ИД записи</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TERMID - ИД срока</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODTERMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODTERMID"})
    public Map<String,Object> dsB2BProductTermUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductTermUpdate", params);
        result.put("PRODTERMID", params.get("PRODTERMID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODTERMID - ИД записи</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TERMID - ИД срока</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODTERMID - ИД записи</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODTERMID"})
    public Map<String,Object> dsB2BProductTermModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductTermUpdate", params);
        result.put("PRODTERMID", params.get("PRODTERMID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODTERMID - ИД записи</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODTERMID"})
    public void dsB2BProductTermDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductTermDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODTERMID - ИД записи</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TERMID - ИД срока</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODTERMID - ИД записи</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>TERMID - ИД срока</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductTermBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductTermBrowseListByParam", "dsB2BProductTermBrowseListByParamCount", params);
        return result;
    }

    /**
     * Получение периода для версии продукта
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {"PRODVERID"})
    public Map<String,Object> dsB2BProductTermListByProdVerId(Map<String, Object> params) throws Exception {
        return this.selectQuery("dsB2BProductTermListByProdVerId", "dsB2BProductTermListByProdVerIdCount", params);
    }

}
