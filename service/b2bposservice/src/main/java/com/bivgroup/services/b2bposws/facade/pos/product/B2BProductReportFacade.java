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
 * Фасад для сущности B2BProductReport
 *
 * @author reson
 */
@IdGen(entityName="B2B_PRODREP",idFieldName="PRODREPID")
@BOName("B2BProductReport")
public class B2BProductReportFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CATEGORYID - Категория документа по страховому продукту</LI>
     * <LI>DATAPROVID - ИД провайдера данных</LI>
     * <LI>EDOC - Признак принадлежности к группе документов для отправки на Email или сохранения на диск</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>ORIGPRINTING - Чистовая печать</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PREPRINTING - Предварительная печать</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPLEVEL - Уровень отчета (1 - отчет по договору (котировке) 2 - отчет по объекту в договоре (количество таких отчетов в рамках договора равно количеству объектов в договоре) 3 - отчет по риску в договоре  (количество таких отчетов в рамках договора равно общему количеству рисков в договоре))</LI>
     * <LI>REPTYPE - Тип отчета (1 - Страховая документация, 2 - Печатный документ)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODREPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODCONFID"})
    public Map<String,Object> dsB2BProductReportCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductReportInsert", params);
        result.put("PRODREPID", params.get("PRODREPID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CATEGORYID - Категория документа по страховому продукту</LI>
     * <LI>DATAPROVID - ИД провайдера данных</LI>
     * <LI>EDOC - Признак принадлежности к группе документов для отправки на Email или сохранения на диск</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>ORIGPRINTING - Чистовая печать</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PREPRINTING - Предварительная печать</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPLEVEL - Уровень отчета (1 - отчет по договору (котировке) 2 - отчет по объекту в договоре (количество таких отчетов в рамках договора равно количеству объектов в договоре) 3 - отчет по риску в договоре  (количество таких отчетов в рамках договора равно общему количеству рисков в договоре))</LI>
     * <LI>REPTYPE - Тип отчета (1 - Страховая документация, 2 - Печатный документ)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODREPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODREPID", "PRODCONFID"})
    public Map<String,Object> dsB2BProductReportInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BProductReportInsert", params);
        result.put("PRODREPID", params.get("PRODREPID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CATEGORYID - Категория документа по страховому продукту</LI>
     * <LI>DATAPROVID - ИД провайдера данных</LI>
     * <LI>EDOC - Признак принадлежности к группе документов для отправки на Email или сохранения на диск</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>ORIGPRINTING - Чистовая печать</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PREPRINTING - Предварительная печать</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPLEVEL - Уровень отчета (1 - отчет по договору (котировке) 2 - отчет по объекту в договоре (количество таких отчетов в рамках договора равно количеству объектов в договоре) 3 - отчет по риску в договоре  (количество таких отчетов в рамках договора равно общему количеству рисков в договоре))</LI>
     * <LI>REPTYPE - Тип отчета (1 - Страховая документация, 2 - Печатный документ)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODREPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODREPID"})
    public Map<String,Object> dsB2BProductReportUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductReportUpdate", params);
        result.put("PRODREPID", params.get("PRODREPID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CATEGORYID - Категория документа по страховому продукту</LI>
     * <LI>DATAPROVID - ИД провайдера данных</LI>
     * <LI>EDOC - Признак принадлежности к группе документов для отправки на Email или сохранения на диск</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>ORIGPRINTING - Чистовая печать</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PREPRINTING - Предварительная печать</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPLEVEL - Уровень отчета (1 - отчет по договору (котировке) 2 - отчет по объекту в договоре (количество таких отчетов в рамках договора равно количеству объектов в договоре) 3 - отчет по риску в договоре  (количество таких отчетов в рамках договора равно общему количеству рисков в договоре))</LI>
     * <LI>REPTYPE - Тип отчета (1 - Страховая документация, 2 - Печатный документ)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODREPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODREPID"})
    public Map<String,Object> dsB2BProductReportModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BProductReportUpdate", params);
        result.put("PRODREPID", params.get("PRODREPID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>PRODREPID - ИД отчета</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODREPID"})
    public void dsB2BProductReportDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BProductReportDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CATEGORYID - Категория документа по страховому продукту</LI>
     * <LI>DATAPROVID - ИД провайдера данных</LI>
     * <LI>EDOC - Признак принадлежности к группе документов для отправки на Email или сохранения на диск</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>ORIGPRINTING - Чистовая печать</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PREPRINTING - Предварительная печать</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPLEVEL - Уровень отчета (1 - отчет по договору (котировке) 2 - отчет по объекту в договоре (количество таких отчетов в рамках договора равно количеству объектов в договоре) 3 - отчет по риску в договоре  (количество таких отчетов в рамках договора равно общему количеству рисков в договоре))</LI>
     * <LI>REPTYPE - Тип отчета (1 - Страховая документация, 2 - Печатный документ)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CATEGORYID - Категория документа по страховому продукту</LI>
     * <LI>DATAPROVID - ИД провайдера данных</LI>
     * <LI>EDOC - Признак принадлежности к группе документов для отправки на Email или сохранения на диск</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>ORIGPRINTING - Чистовая печать</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PREPRINTING - Предварительная печать</LI>
     * <LI>PRODCONFID - ИД конфигурации продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPLEVEL - Уровень отчета (1 - отчет по договору (котировке) 2 - отчет по объекту в договоре (количество таких отчетов в рамках договора равно количеству объектов в договоре) 3 - отчет по риску в договоре  (количество таких отчетов в рамках договора равно общему количеству рисков в договоре))</LI>
     * <LI>REPTYPE - Тип отчета (1 - Страховая документация, 2 - Печатный документ)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BProductReportBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BProductReportBrowseListByParam", "dsB2BProductReportBrowseListByParamCount", params);
        return result;
    }





}
