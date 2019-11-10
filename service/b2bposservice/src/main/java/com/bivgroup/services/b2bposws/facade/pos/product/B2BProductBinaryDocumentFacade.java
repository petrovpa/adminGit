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
 * Фасад для сущности B2BProductBinaryDocument
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODBINDOC",idFieldName="PRODBINDOCID")
@BOName("B2BProductBinaryDocument")
public class B2BProductBinaryDocumentFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCTYPE - Тип документа</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>DOCLEVEL - Тип документа по виду страхования</LI>
     * <LI>DOCLEVELNOTE - Описание уровня документа</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>DOCSYSNAME - Системное имя для сортировки в таблицах</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINDOCTYPE", "NAME", "PRODCONFID", "REQUIRED"})
    public Map<String,Object> dsB2BProductBinaryDocumentCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductBinaryDocumentInsert", params);
        result.put("PRODBINDOCID", params.get("PRODBINDOCID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCTYPE - Тип документа</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>DOCLEVEL - Тип документа по виду страхования</LI>
     * <LI>DOCLEVELNOTE - Описание уровня документа</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>DOCSYSNAME - Системное имя для сортировки в таблицах</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINDOCTYPE", "PRODBINDOCID", "NAME", "PRODCONFID", "REQUIRED"})
    public Map<String,Object> dsB2BProductBinaryDocumentInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductBinaryDocumentInsert", params);
        result.put("PRODBINDOCID", params.get("PRODBINDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCTYPE - Тип документа</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>DOCLEVEL - Тип документа по виду страхования</LI>
     * <LI>DOCLEVELNOTE - Описание уровня документа</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>DOCSYSNAME - Системное имя для сортировки в таблицах</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODBINDOCID"})
    public Map<String,Object> dsB2BProductBinaryDocumentUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductBinaryDocumentUpdate", params);
        result.put("PRODBINDOCID", params.get("PRODBINDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCTYPE - Тип документа</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>DOCLEVEL - Тип документа по виду страхования</LI>
     * <LI>DOCLEVELNOTE - Описание уровня документа</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>DOCSYSNAME - Системное имя для сортировки в таблицах</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODBINDOCID"})
    public Map<String,Object> dsB2BProductBinaryDocumentModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductBinaryDocumentUpdate", params);
        result.put("PRODBINDOCID", params.get("PRODBINDOCID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODBINDOCID"})
    public void dsB2BProductBinaryDocumentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductBinaryDocumentDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCTYPE - Тип документа</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>DOCLEVEL - Тип документа по виду страхования</LI>
     * <LI>DOCLEVELNOTE - Описание уровня документа</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>DOCSYSNAME - Системное имя для сортировки в таблицах</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINDOCTYPE - Тип документа</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>DOCLEVEL - Тип документа по виду страхования</LI>
     * <LI>DOCLEVELNOTE - Описание уровня документа</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>DOCSYSNAME - Системное имя для сортировки в таблицах</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductBinaryDocumentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductBinaryDocumentBrowseListByParam", "dsB2BProductBinaryDocumentBrowseListByParamCount", params);
        return result;
    }





}
