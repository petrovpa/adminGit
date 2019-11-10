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
 * Фасад для сущности LossesProductReport
 *
 * @author reson
 */
@IdGen(entityName="LOSS_PRODREP",idFieldName="PRODREPID")
@BOName("LossesProductReport")
public class LossesProductReportFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>EARLYREPAYMENT - Досрочное погашение договора (флаг используется только отказами)</LI>
     * <LI>EXPIRIED - Истечение срока договора (флаг используется только отказами)</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPTYPE - Тип отчета (1 - заявление, 2 - дополнительное соглашение, 3 - отказ)</LI>
     * <LI>RETURNPARTID - Объем возврата</LI>
     * <LI>RETURNREASONID - Причина возврата</LI>
     * <LI>RETURNTYPEID - вид возврата (1 контрактный 2 неконтрактный)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODREPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODID"})
    public Map<String,Object> dsLossesProductReportCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesProductReportInsert", params);
        result.put("PRODREPID", params.get("PRODREPID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>EARLYREPAYMENT - Досрочное погашение договора (флаг используется только отказами)</LI>
     * <LI>EXPIRIED - Истечение срока договора (флаг используется только отказами)</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPTYPE - Тип отчета (1 - заявление, 2 - дополнительное соглашение, 3 - отказ)</LI>
     * <LI>RETURNPARTID - Объем возврата</LI>
     * <LI>RETURNREASONID - Причина возврата</LI>
     * <LI>RETURNTYPEID - вид возврата (1 контрактный 2 неконтрактный)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODREPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODREPID", "PRODID"})
    public Map<String,Object> dsLossesProductReportInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesProductReportInsert", params);
        result.put("PRODREPID", params.get("PRODREPID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>EARLYREPAYMENT - Досрочное погашение договора (флаг используется только отказами)</LI>
     * <LI>EXPIRIED - Истечение срока договора (флаг используется только отказами)</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPTYPE - Тип отчета (1 - заявление, 2 - дополнительное соглашение, 3 - отказ)</LI>
     * <LI>RETURNPARTID - Объем возврата</LI>
     * <LI>RETURNREASONID - Причина возврата</LI>
     * <LI>RETURNTYPEID - вид возврата (1 контрактный 2 неконтрактный)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODREPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODREPID"})
    public Map<String,Object> dsLossesProductReportUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesProductReportUpdate", params);
        result.put("PRODREPID", params.get("PRODREPID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>EARLYREPAYMENT - Досрочное погашение договора (флаг используется только отказами)</LI>
     * <LI>EXPIRIED - Истечение срока договора (флаг используется только отказами)</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPTYPE - Тип отчета (1 - заявление, 2 - дополнительное соглашение, 3 - отказ)</LI>
     * <LI>RETURNPARTID - Объем возврата</LI>
     * <LI>RETURNREASONID - Причина возврата</LI>
     * <LI>RETURNTYPEID - вид возврата (1 контрактный 2 неконтрактный)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>PRODREPID - ИД отчета</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"PRODREPID"})
    public Map<String,Object> dsLossesProductReportModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesProductReportUpdate", params);
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
    public void dsLossesProductReportDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesProductReportDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>EARLYREPAYMENT - Досрочное погашение договора (флаг используется только отказами)</LI>
     * <LI>EXPIRIED - Истечение срока договора (флаг используется только отказами)</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPTYPE - Тип отчета (1 - заявление, 2 - дополнительное соглашение, 3 - отказ)</LI>
     * <LI>RETURNPARTID - Объем возврата</LI>
     * <LI>RETURNREASONID - Причина возврата</LI>
     * <LI>RETURNTYPEID - вид возврата (1 контрактный 2 неконтрактный)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>EARLYREPAYMENT - Досрочное погашение договора (флаг используется только отказами)</LI>
     * <LI>EXPIRIED - Истечение срока договора (флаг используется только отказами)</LI>
     * <LI>PRODREPID - ИД отчета</LI>
     * <LI>NAME - Наименование</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>POSTN - Позиция</LI>
     * <LI>PRODID - ИД продукта</LI>
     * <LI>PRODKINDID - ИД типа продукта</LI>
     * <LI>CHECKID - Проверка перед выводом отчета</LI>
     * <LI>REPID - ИД отчета</LI>
     * <LI>REPTYPE - Тип отчета (1 - заявление, 2 - дополнительное соглашение, 3 - отказ)</LI>
     * <LI>RETURNPARTID - Объем возврата</LI>
     * <LI>RETURNREASONID - Причина возврата</LI>
     * <LI>RETURNTYPEID - вид возврата (1 контрактный 2 неконтрактный)</LI>
     * <LI>CPNUM - Количество копий</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesProductReportBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesProductReportBrowseListByParam", "dsLossesProductReportBrowseListByParamCount", params);
        return result;
    }





}
