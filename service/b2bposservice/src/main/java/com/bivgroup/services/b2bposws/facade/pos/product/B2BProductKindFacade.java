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
 * Фасад для сущности B2BProductKind
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODKIND",idFieldName="PRODKINDID")
@BOName("B2BProductKind")
public class B2BProductKindFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>IMGPATH - Картинка</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODKINDID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"NAME"})
    public Map<String,Object> dsB2BProductKindCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductKindInsert", params);
        result.put("PRODKINDID", params.get("PRODKINDID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>IMGPATH - Картинка</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODKINDID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"NAME", "PRODKINDID"})
    public Map<String,Object> dsB2BProductKindInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductKindInsert", params);
        result.put("PRODKINDID", params.get("PRODKINDID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>IMGPATH - Картинка</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODKINDID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODKINDID"})
    public Map<String,Object> dsB2BProductKindUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductKindUpdate", params);
        result.put("PRODKINDID", params.get("PRODKINDID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>IMGPATH - Картинка</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODKINDID - ИД продукта</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODKINDID"})
    public Map<String,Object> dsB2BProductKindModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductKindUpdate", params);
        result.put("PRODKINDID", params.get("PRODKINDID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODKINDID - ИД продукта</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODKINDID"})
    public void dsB2BProductKindDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductKindDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>IMGPATH - Картинка</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ISHIDDEN - Продукт скрыт или нет</LI>
     * <LI>IMGPATH - Картинка</LI>
     * <LI>NAME - Наименование продукта</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>PRODKINDID - ИД продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductKindBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductKindBrowseListByParam", "dsB2BProductKindBrowseListByParamCount", params);
        return result;
    }





}
