/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.additionalAgreements;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BAddAgrCause
 *
 * @author reson
 */
@IdGen(entityName="B2B_ADDAGRCAUSE",idFieldName="ADDAGRCAUSEID")
@BOName("B2BAddAgrCause")
public class B2BAddAgrCauseFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид</LI>
     * <LI>CHECKID - Ид проверки</LI>
     * <LI>FIELDMAPPING - Маппинг полей</LI>
     * <LI>HBDATAVERID - Ид версии справочника</LI>
     * <LI>ISNEEDCALC - Требуется перерасчет</LI>
     * <LI>ISNEEDATTACH - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDINNERAGR - настройка процесса согласования с внутренними подразделениям компании (Юридический отдел, Безопасность и т.д.)</LI>
     * <LI>ISNEEDMANUALCHECK - Признак для ручного контроля прикрепленных документов</LI>
     * <LI>ISNEEDORIGINAL - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDPARTNER - Признак о необходимости выполнить запрос согласования внесения изменений с Партнером</LI>
     * <LI>ISNEEDUW - Признак вызова модуля «online Андеррайтинг»</LI>
     * <LI>NAME - Название причины</LI>
     * <LI>NOTE - Посказка</LI>
     * <LI>PAGECONTENT - Страница содержимого причины</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAddAgrCauseCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BAddAgrCauseInsert", params);
        result.put("ADDAGRCAUSEID", params.get("ADDAGRCAUSEID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид</LI>
     * <LI>CHECKID - Ид проверки</LI>
     * <LI>FIELDMAPPING - Маппинг полей</LI>
     * <LI>HBDATAVERID - Ид версии справочника</LI>
     * <LI>ISNEEDCALC - Требуется перерасчет</LI>
     * <LI>ISNEEDATTACH - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDINNERAGR - настройка процесса согласования с внутренними подразделениям компании (Юридический отдел, Безопасность и т.д.)</LI>
     * <LI>ISNEEDMANUALCHECK - Признак для ручного контроля прикрепленных документов</LI>
     * <LI>ISNEEDORIGINAL - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDPARTNER - Признак о необходимости выполнить запрос согласования внесения изменений с Партнером</LI>
     * <LI>ISNEEDUW - Признак вызова модуля «online Андеррайтинг»</LI>
     * <LI>NAME - Название причины</LI>
     * <LI>NOTE - Посказка</LI>
     * <LI>PAGECONTENT - Страница содержимого причины</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRCAUSEID"})
    public Map<String,Object> dsB2BAddAgrCauseInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BAddAgrCauseInsert", params);
        result.put("ADDAGRCAUSEID", params.get("ADDAGRCAUSEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид</LI>
     * <LI>CHECKID - Ид проверки</LI>
     * <LI>FIELDMAPPING - Маппинг полей</LI>
     * <LI>HBDATAVERID - Ид версии справочника</LI>
     * <LI>ISNEEDCALC - Требуется перерасчет</LI>
     * <LI>ISNEEDATTACH - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDINNERAGR - настройка процесса согласования с внутренними подразделениям компании (Юридический отдел, Безопасность и т.д.)</LI>
     * <LI>ISNEEDMANUALCHECK - Признак для ручного контроля прикрепленных документов</LI>
     * <LI>ISNEEDORIGINAL - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDPARTNER - Признак о необходимости выполнить запрос согласования внесения изменений с Партнером</LI>
     * <LI>ISNEEDUW - Признак вызова модуля «online Андеррайтинг»</LI>
     * <LI>NAME - Название причины</LI>
     * <LI>NOTE - Посказка</LI>
     * <LI>PAGECONTENT - Страница содержимого причины</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRCAUSEID"})
    public Map<String,Object> dsB2BAddAgrCauseUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BAddAgrCauseUpdate", params);
        result.put("ADDAGRCAUSEID", params.get("ADDAGRCAUSEID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид</LI>
     * <LI>CHECKID - Ид проверки</LI>
     * <LI>FIELDMAPPING - Маппинг полей</LI>
     * <LI>HBDATAVERID - Ид версии справочника</LI>
     * <LI>ISNEEDCALC - Требуется перерасчет</LI>
     * <LI>ISNEEDATTACH - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDINNERAGR - настройка процесса согласования с внутренними подразделениям компании (Юридический отдел, Безопасность и т.д.)</LI>
     * <LI>ISNEEDMANUALCHECK - Признак для ручного контроля прикрепленных документов</LI>
     * <LI>ISNEEDORIGINAL - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDPARTNER - Признак о необходимости выполнить запрос согласования внесения изменений с Партнером</LI>
     * <LI>ISNEEDUW - Признак вызова модуля «online Андеррайтинг»</LI>
     * <LI>NAME - Название причины</LI>
     * <LI>NOTE - Посказка</LI>
     * <LI>PAGECONTENT - Страница содержимого причины</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRCAUSEID"})
    public Map<String,Object> dsB2BAddAgrCauseModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BAddAgrCauseUpdate", params);
        result.put("ADDAGRCAUSEID", params.get("ADDAGRCAUSEID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"ADDAGRCAUSEID"})
    public void dsB2BAddAgrCauseDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BAddAgrCauseDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид</LI>
     * <LI>CHECKID - Ид проверки</LI>
     * <LI>FIELDMAPPING - Маппинг полей</LI>
     * <LI>HBDATAVERID - Ид версии справочника</LI>
     * <LI>ISNEEDCALC - Требуется перерасчет</LI>
     * <LI>ISNEEDATTACH - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDINNERAGR - настройка процесса согласования с внутренними подразделениям компании (Юридический отдел, Безопасность и т.д.)</LI>
     * <LI>ISNEEDMANUALCHECK - Признак для ручного контроля прикрепленных документов</LI>
     * <LI>ISNEEDORIGINAL - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDPARTNER - Признак о необходимости выполнить запрос согласования внесения изменений с Партнером</LI>
     * <LI>ISNEEDUW - Признак вызова модуля «online Андеррайтинг»</LI>
     * <LI>NAME - Название причины</LI>
     * <LI>NOTE - Посказка</LI>
     * <LI>PAGECONTENT - Страница содержимого причины</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>ADDAGRCAUSEID - Ид</LI>
     * <LI>CHECKID - Ид проверки</LI>
     * <LI>FIELDMAPPING - Маппинг полей</LI>
     * <LI>HBDATAVERID - Ид версии справочника</LI>
     * <LI>ISNEEDCALC - Требуется перерасчет</LI>
     * <LI>ISNEEDATTACH - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDINNERAGR - настройка процесса согласования с внутренними подразделениям компании (Юридический отдел, Безопасность и т.д.)</LI>
     * <LI>ISNEEDMANUALCHECK - Признак для ручного контроля прикрепленных документов</LI>
     * <LI>ISNEEDORIGINAL - Признак требуется оригинал заявления</LI>
     * <LI>ISNEEDPARTNER - Признак о необходимости выполнить запрос согласования внесения изменений с Партнером</LI>
     * <LI>ISNEEDUW - Признак вызова модуля «online Андеррайтинг»</LI>
     * <LI>NAME - Название причины</LI>
     * <LI>NOTE - Посказка</LI>
     * <LI>PAGECONTENT - Страница содержимого причины</LI>
     * <LI>PRODVERID - ИД версии продукта</LI>
     * <LI>SYSNAME - Системное наименование</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BAddAgrCauseBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BAddAgrCauseBrowseListByParam", "dsB2BAddAgrCauseBrowseListByParamCount", params);
        return result;
    }





}
