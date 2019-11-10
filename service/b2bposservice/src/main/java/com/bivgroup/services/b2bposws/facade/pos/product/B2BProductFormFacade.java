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
 * Фасад для сущности B2BProductForm
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODFORM",idFieldName="PRODFORMID")
@BOName("B2BProductForm")
public class B2BProductFormFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>FORMTYPEID - Вид формы</LI>
     * <LI>PRODFORMID - ИД</LI>
     * <LI>FORMINDEX - Порядковый номер</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PAGE - Страница</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODFORMID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductFormCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductFormInsert", params);
        result.put("PRODFORMID", params.get("PRODFORMID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>FORMTYPEID - Вид формы</LI>
     * <LI>PRODFORMID - ИД</LI>
     * <LI>FORMINDEX - Порядковый номер</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PAGE - Страница</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODFORMID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODFORMID"})
    public Map<String,Object> dsB2BProductFormInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductFormInsert", params);
        result.put("PRODFORMID", params.get("PRODFORMID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>FORMTYPEID - Вид формы</LI>
     * <LI>PRODFORMID - ИД</LI>
     * <LI>FORMINDEX - Порядковый номер</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PAGE - Страница</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODFORMID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODFORMID"})
    public Map<String,Object> dsB2BProductFormUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductFormUpdate", params);
        result.put("PRODFORMID", params.get("PRODFORMID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>FORMTYPEID - Вид формы</LI>
     * <LI>PRODFORMID - ИД</LI>
     * <LI>FORMINDEX - Порядковый номер</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PAGE - Страница</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODFORMID - ИД</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODFORMID"})
    public Map<String,Object> dsB2BProductFormModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductFormUpdate", params);
        result.put("PRODFORMID", params.get("PRODFORMID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODFORMID - ИД</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODFORMID"})
    public void dsB2BProductFormDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductFormDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>FORMTYPEID - Вид формы</LI>
     * <LI>PRODFORMID - ИД</LI>
     * <LI>FORMINDEX - Порядковый номер</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PAGE - Страница</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>FORMTYPEID - Вид формы</LI>
     * <LI>PRODFORMID - ИД</LI>
     * <LI>FORMINDEX - Порядковый номер</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>PAGE - Страница</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductFormBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductFormBrowseListByParam", "dsB2BProductFormBrowseListByParamCount", params);
        return result;
    }





}
