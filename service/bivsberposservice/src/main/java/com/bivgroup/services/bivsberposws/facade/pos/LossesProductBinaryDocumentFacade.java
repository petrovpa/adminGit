/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesProductBinaryDocument
 *
 * @author reson
 */
@IdGen(entityName="LOSS_PRODBINDOC",idFieldName="PRODBINDOCID")
@BOName("LossesProductBinaryDocument")
public class LossesProductBinaryDocumentFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>BINDOCTYPE - Тип документа по причине возврата</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>PRODID - ИД конфигурации продукта</LI>
     * <LI>PRODKINDID - ИД группы проэктов</LI>
     * <LI>REFUNDTYPEID - Тип возврата</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINDOCTYPE", "REQUIRED"})
    public Map<String,Object> dsLossesProductBinaryDocumentCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesProductBinaryDocumentInsert", params);
        result.put("PRODBINDOCID", params.get("PRODBINDOCID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>BINDOCTYPE - Тип документа по причине возврата</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>PRODID - ИД конфигурации продукта</LI>
     * <LI>PRODKINDID - ИД группы проэктов</LI>
     * <LI>REFUNDTYPEID - Тип возврата</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BINDOCTYPE", "PRODBINDOCID", "REQUIRED"})
    public Map<String,Object> dsLossesProductBinaryDocumentInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesProductBinaryDocumentInsert", params);
        result.put("PRODBINDOCID", params.get("PRODBINDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>BINDOCTYPE - Тип документа по причине возврата</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>PRODID - ИД конфигурации продукта</LI>
     * <LI>PRODKINDID - ИД группы проэктов</LI>
     * <LI>REFUNDTYPEID - Тип возврата</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODBINDOCID"})
    public Map<String,Object> dsLossesProductBinaryDocumentUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesProductBinaryDocumentUpdate", params);
        result.put("PRODBINDOCID", params.get("PRODBINDOCID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>BINDOCTYPE - Тип документа по причине возврата</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>PRODID - ИД конфигурации продукта</LI>
     * <LI>PRODKINDID - ИД группы проэктов</LI>
     * <LI>REFUNDTYPEID - Тип возврата</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODBINDOCID"})
    public Map<String,Object> dsLossesProductBinaryDocumentModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesProductBinaryDocumentUpdate", params);
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
    public void dsLossesProductBinaryDocumentDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesProductBinaryDocumentDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>BINDOCTYPE - Тип документа по причине возврата</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>PRODID - ИД конфигурации продукта</LI>
     * <LI>PRODKINDID - ИД группы проэктов</LI>
     * <LI>REFUNDTYPEID - Тип возврата</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BINDOCID - ИД документа</LI>
     * <LI>BINDOCTYPE - Тип документа по причине возврата</LI>
     * <LI>CHECKNAME - Ссылка на проверку через наименование</LI>
     * <LI>PRODBINDOCID - ИД бинарного документа</LI>
     * <LI>PRODID - ИД конфигурации продукта</LI>
     * <LI>PRODKINDID - ИД группы проэктов</LI>
     * <LI>REFUNDTYPEID - Тип возврата</LI>
     * <LI>REQUIRED - Обязательный</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesProductBinaryDocumentBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesProductBinaryDocumentBrowseListByParam", "dsLossesProductBinaryDocumentBrowseListByParamCount", params);
        return result;
    }





}
