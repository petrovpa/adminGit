/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BBankCashFlow
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@IdGen(entityName="B2B_BANKCASHFLOW",idFieldName="BANKCASHFLOWID")
@State(idFieldName = "BANKCASHFLOWID", startStateName = "B2B_BANKCASHFLOW_NEW", typeSysName = "B2B_BANKCASHFLOW")
@BOName("B2BBankCashFlow")
public class B2BBankCashFlowFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>AMVALUE - Сумма движения</LI>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки банковской выписки</LI>
     * <LI>BFCVALUE - Показатель КБК</LI>
     * <LI>CODE - Код</LI>
     * <LI>CONTRID - ИД договора, созданного по сведениям текущей записи о движении средств</LI>
     * <LI>CREATEDATE - Дата создания потока</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKCASHFLOWID - ИД движения средств по расчетному счету</LI>
     * <LI>INDEXBASE - Показатель основания</LI>
     * <LI>INDEXDATE - Показатель даты</LI>
     * <LI>INDEXNUMBER - Показатель номера</LI>
     * <LI>INDEXPERIOD - Показатель периода</LI>
     * <LI>INDEXTYPE - Показатель типа</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTNUMBER - Входящий номер</LI>
     * <LI>OKATO - ОКАТО</LI>
     * <LI>ORIGINATORSTATE - Статус составителя</LI>
     * <LI>PAYDATE - Дата списания со счета плательщика</LI>
     * <LI>PAYER - Наименование плательщика</LI>
     * <LI>PAYERACCOUNT - Счет плательщика</LI>
     * <LI>PAYERBANK - Наименование банка плательщика</LI>
     * <LI>PAYERBIK - БИК банка плательщика</LI>
     * <LI>PAYERCORACCOUNT - Корр счет банка плательщика</LI>
     * <LI>PAYERINN - ИНН плательщика</LI>
     * <LI>PAYERKPP - КПП плательщика</LI>
     * <LI>PAYERRSACCOUNT - Расчетный счет плательщика</LI>
     * <LI>PAYMENTMETHOD - Вид оплаты</LI>
     * <LI>PAYMENTTYPE - Вид платежа</LI>
     * <LI>PRIORITY - Очередность</LI>
     * <LI>PURPOSE - Назначение платежа</LI>
     * <LI>RECEIPTDATE - Дата поступления на счет получателя</LI>
     * <LI>RECIPIENT - Наименование получателя</LI>
     * <LI>RECIPIENTACCOUNT - Счет получателя</LI>
     * <LI>RECIPIENTBANK - Наименование банка получателя</LI>
     * <LI>RECIPIENTBIK - БИК банка получателя</LI>
     * <LI>RECIPIENTCORACCOUNT - Корр счет банка получателя</LI>
     * <LI>RECIPIENTINN - ИНН получателя</LI>
     * <LI>RECIPIENTKPP - КПП получателя</LI>
     * <LI>RECIPIENTRSACCOUNT - Расчетный счет получателя</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>TEMPLATE - Шаблон</LI>
     * <LI>TYPE - Тип движения поступление или списание</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKCASHFLOWID - ИД движения средств по расчетному счету</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankCashFlowCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankCashFlowInsert", params);
        result.put("BANKCASHFLOWID", params.get("BANKCASHFLOWID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>AMVALUE - Сумма движения</LI>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки банковской выписки</LI>
     * <LI>BFCVALUE - Показатель КБК</LI>
     * <LI>CODE - Код</LI>
     * <LI>CONTRID - ИД договора, созданного по сведениям текущей записи о движении средств</LI>
     * <LI>CREATEDATE - Дата создания потока</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKCASHFLOWID - ИД движения средств по расчетному счету</LI>
     * <LI>INDEXBASE - Показатель основания</LI>
     * <LI>INDEXDATE - Показатель даты</LI>
     * <LI>INDEXNUMBER - Показатель номера</LI>
     * <LI>INDEXPERIOD - Показатель периода</LI>
     * <LI>INDEXTYPE - Показатель типа</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTNUMBER - Входящий номер</LI>
     * <LI>OKATO - ОКАТО</LI>
     * <LI>ORIGINATORSTATE - Статус составителя</LI>
     * <LI>PAYDATE - Дата списания со счета плательщика</LI>
     * <LI>PAYER - Наименование плательщика</LI>
     * <LI>PAYERACCOUNT - Счет плательщика</LI>
     * <LI>PAYERBANK - Наименование банка плательщика</LI>
     * <LI>PAYERBIK - БИК банка плательщика</LI>
     * <LI>PAYERCORACCOUNT - Корр счет банка плательщика</LI>
     * <LI>PAYERINN - ИНН плательщика</LI>
     * <LI>PAYERKPP - КПП плательщика</LI>
     * <LI>PAYERRSACCOUNT - Расчетный счет плательщика</LI>
     * <LI>PAYMENTMETHOD - Вид оплаты</LI>
     * <LI>PAYMENTTYPE - Вид платежа</LI>
     * <LI>PRIORITY - Очередность</LI>
     * <LI>PURPOSE - Назначение платежа</LI>
     * <LI>RECEIPTDATE - Дата поступления на счет получателя</LI>
     * <LI>RECIPIENT - Наименование получателя</LI>
     * <LI>RECIPIENTACCOUNT - Счет получателя</LI>
     * <LI>RECIPIENTBANK - Наименование банка получателя</LI>
     * <LI>RECIPIENTBIK - БИК банка получателя</LI>
     * <LI>RECIPIENTCORACCOUNT - Корр счет банка получателя</LI>
     * <LI>RECIPIENTINN - ИНН получателя</LI>
     * <LI>RECIPIENTKPP - КПП получателя</LI>
     * <LI>RECIPIENTRSACCOUNT - Расчетный счет получателя</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>TEMPLATE - Шаблон</LI>
     * <LI>TYPE - Тип движения поступление или списание</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKCASHFLOWID - ИД движения средств по расчетному счету</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKCASHFLOWID"})
    public Map<String,Object> dsB2BBankCashFlowInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BBankCashFlowInsert", params);
        result.put("BANKCASHFLOWID", params.get("BANKCASHFLOWID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AMVALUE - Сумма движения</LI>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки банковской выписки</LI>
     * <LI>BFCVALUE - Показатель КБК</LI>
     * <LI>CODE - Код</LI>
     * <LI>CONTRID - ИД договора, созданного по сведениям текущей записи о движении средств</LI>
     * <LI>CREATEDATE - Дата создания потока</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKCASHFLOWID - ИД движения средств по расчетному счету</LI>
     * <LI>INDEXBASE - Показатель основания</LI>
     * <LI>INDEXDATE - Показатель даты</LI>
     * <LI>INDEXNUMBER - Показатель номера</LI>
     * <LI>INDEXPERIOD - Показатель периода</LI>
     * <LI>INDEXTYPE - Показатель типа</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTNUMBER - Входящий номер</LI>
     * <LI>OKATO - ОКАТО</LI>
     * <LI>ORIGINATORSTATE - Статус составителя</LI>
     * <LI>PAYDATE - Дата списания со счета плательщика</LI>
     * <LI>PAYER - Наименование плательщика</LI>
     * <LI>PAYERACCOUNT - Счет плательщика</LI>
     * <LI>PAYERBANK - Наименование банка плательщика</LI>
     * <LI>PAYERBIK - БИК банка плательщика</LI>
     * <LI>PAYERCORACCOUNT - Корр счет банка плательщика</LI>
     * <LI>PAYERINN - ИНН плательщика</LI>
     * <LI>PAYERKPP - КПП плательщика</LI>
     * <LI>PAYERRSACCOUNT - Расчетный счет плательщика</LI>
     * <LI>PAYMENTMETHOD - Вид оплаты</LI>
     * <LI>PAYMENTTYPE - Вид платежа</LI>
     * <LI>PRIORITY - Очередность</LI>
     * <LI>PURPOSE - Назначение платежа</LI>
     * <LI>RECEIPTDATE - Дата поступления на счет получателя</LI>
     * <LI>RECIPIENT - Наименование получателя</LI>
     * <LI>RECIPIENTACCOUNT - Счет получателя</LI>
     * <LI>RECIPIENTBANK - Наименование банка получателя</LI>
     * <LI>RECIPIENTBIK - БИК банка получателя</LI>
     * <LI>RECIPIENTCORACCOUNT - Корр счет банка получателя</LI>
     * <LI>RECIPIENTINN - ИНН получателя</LI>
     * <LI>RECIPIENTKPP - КПП получателя</LI>
     * <LI>RECIPIENTRSACCOUNT - Расчетный счет получателя</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>TEMPLATE - Шаблон</LI>
     * <LI>TYPE - Тип движения поступление или списание</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKCASHFLOWID - ИД движения средств по расчетному счету</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKCASHFLOWID"})
    public Map<String,Object> dsB2BBankCashFlowUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankCashFlowUpdate", params);
        result.put("BANKCASHFLOWID", params.get("BANKCASHFLOWID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>AMVALUE - Сумма движения</LI>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки банковской выписки</LI>
     * <LI>BFCVALUE - Показатель КБК</LI>
     * <LI>CODE - Код</LI>
     * <LI>CONTRID - ИД договора, созданного по сведениям текущей записи о движении средств</LI>
     * <LI>CREATEDATE - Дата создания потока</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKCASHFLOWID - ИД движения средств по расчетному счету</LI>
     * <LI>INDEXBASE - Показатель основания</LI>
     * <LI>INDEXDATE - Показатель даты</LI>
     * <LI>INDEXNUMBER - Показатель номера</LI>
     * <LI>INDEXPERIOD - Показатель периода</LI>
     * <LI>INDEXTYPE - Показатель типа</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTNUMBER - Входящий номер</LI>
     * <LI>OKATO - ОКАТО</LI>
     * <LI>ORIGINATORSTATE - Статус составителя</LI>
     * <LI>PAYDATE - Дата списания со счета плательщика</LI>
     * <LI>PAYER - Наименование плательщика</LI>
     * <LI>PAYERACCOUNT - Счет плательщика</LI>
     * <LI>PAYERBANK - Наименование банка плательщика</LI>
     * <LI>PAYERBIK - БИК банка плательщика</LI>
     * <LI>PAYERCORACCOUNT - Корр счет банка плательщика</LI>
     * <LI>PAYERINN - ИНН плательщика</LI>
     * <LI>PAYERKPP - КПП плательщика</LI>
     * <LI>PAYERRSACCOUNT - Расчетный счет плательщика</LI>
     * <LI>PAYMENTMETHOD - Вид оплаты</LI>
     * <LI>PAYMENTTYPE - Вид платежа</LI>
     * <LI>PRIORITY - Очередность</LI>
     * <LI>PURPOSE - Назначение платежа</LI>
     * <LI>RECEIPTDATE - Дата поступления на счет получателя</LI>
     * <LI>RECIPIENT - Наименование получателя</LI>
     * <LI>RECIPIENTACCOUNT - Счет получателя</LI>
     * <LI>RECIPIENTBANK - Наименование банка получателя</LI>
     * <LI>RECIPIENTBIK - БИК банка получателя</LI>
     * <LI>RECIPIENTCORACCOUNT - Корр счет банка получателя</LI>
     * <LI>RECIPIENTINN - ИНН получателя</LI>
     * <LI>RECIPIENTKPP - КПП получателя</LI>
     * <LI>RECIPIENTRSACCOUNT - Расчетный счет получателя</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>TEMPLATE - Шаблон</LI>
     * <LI>TYPE - Тип движения поступление или списание</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BANKCASHFLOWID - ИД движения средств по расчетному счету</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKCASHFLOWID"})
    public Map<String,Object> dsB2BBankCashFlowModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BBankCashFlowUpdate", params);
        result.put("BANKCASHFLOWID", params.get("BANKCASHFLOWID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKCASHFLOWID - ИД движения средств по расчетному счету</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKCASHFLOWID"})
    public void dsB2BBankCashFlowDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BBankCashFlowDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>AMVALUE - Сумма движения</LI>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки банковской выписки</LI>
     * <LI>BFCVALUE - Показатель КБК</LI>
     * <LI>CODE - Код</LI>
     * <LI>CONTRID - ИД договора, созданного по сведениям текущей записи о движении средств</LI>
     * <LI>CREATEDATE - Дата создания потока</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKCASHFLOWID - ИД движения средств по расчетному счету</LI>
     * <LI>INDEXBASE - Показатель основания</LI>
     * <LI>INDEXDATE - Показатель даты</LI>
     * <LI>INDEXNUMBER - Показатель номера</LI>
     * <LI>INDEXPERIOD - Показатель периода</LI>
     * <LI>INDEXTYPE - Показатель типа</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTNUMBER - Входящий номер</LI>
     * <LI>OKATO - ОКАТО</LI>
     * <LI>ORIGINATORSTATE - Статус составителя</LI>
     * <LI>PAYDATE - Дата списания со счета плательщика</LI>
     * <LI>PAYER - Наименование плательщика</LI>
     * <LI>PAYERACCOUNT - Счет плательщика</LI>
     * <LI>PAYERBANK - Наименование банка плательщика</LI>
     * <LI>PAYERBIK - БИК банка плательщика</LI>
     * <LI>PAYERCORACCOUNT - Корр счет банка плательщика</LI>
     * <LI>PAYERINN - ИНН плательщика</LI>
     * <LI>PAYERKPP - КПП плательщика</LI>
     * <LI>PAYERRSACCOUNT - Расчетный счет плательщика</LI>
     * <LI>PAYMENTMETHOD - Вид оплаты</LI>
     * <LI>PAYMENTTYPE - Вид платежа</LI>
     * <LI>PRIORITY - Очередность</LI>
     * <LI>PURPOSE - Назначение платежа</LI>
     * <LI>RECEIPTDATE - Дата поступления на счет получателя</LI>
     * <LI>RECIPIENT - Наименование получателя</LI>
     * <LI>RECIPIENTACCOUNT - Счет получателя</LI>
     * <LI>RECIPIENTBANK - Наименование банка получателя</LI>
     * <LI>RECIPIENTBIK - БИК банка получателя</LI>
     * <LI>RECIPIENTCORACCOUNT - Корр счет банка получателя</LI>
     * <LI>RECIPIENTINN - ИНН получателя</LI>
     * <LI>RECIPIENTKPP - КПП получателя</LI>
     * <LI>RECIPIENTRSACCOUNT - Расчетный счет получателя</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>TEMPLATE - Шаблон</LI>
     * <LI>TYPE - Тип движения поступление или списание</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>AMVALUE - Сумма движения</LI>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * <LI>BANKSTATETEMPLATEID - ИД шаблона обработки банковской выписки</LI>
     * <LI>BFCVALUE - Показатель КБК</LI>
     * <LI>CODE - Код</LI>
     * <LI>CONTRID - ИД договора, созданного по сведениям текущей записи о движении средств</LI>
     * <LI>CREATEDATE - Дата создания потока</LI>
     * <LI>CREATEUSERID - ИД создавшего пользователя</LI>
     * <LI>ERRORTEXT - Текст ошибки</LI>
     * <LI>BANKCASHFLOWID - ИД движения средств по расчетному счету</LI>
     * <LI>INDEXBASE - Показатель основания</LI>
     * <LI>INDEXDATE - Показатель даты</LI>
     * <LI>INDEXNUMBER - Показатель номера</LI>
     * <LI>INDEXPERIOD - Показатель периода</LI>
     * <LI>INDEXTYPE - Показатель типа</LI>
     * <LI>INPUTDATE - Входящая дата</LI>
     * <LI>INPUTNUMBER - Входящий номер</LI>
     * <LI>OKATO - ОКАТО</LI>
     * <LI>ORIGINATORSTATE - Статус составителя</LI>
     * <LI>PAYDATE - Дата списания со счета плательщика</LI>
     * <LI>PAYER - Наименование плательщика</LI>
     * <LI>PAYERACCOUNT - Счет плательщика</LI>
     * <LI>PAYERBANK - Наименование банка плательщика</LI>
     * <LI>PAYERBIK - БИК банка плательщика</LI>
     * <LI>PAYERCORACCOUNT - Корр счет банка плательщика</LI>
     * <LI>PAYERINN - ИНН плательщика</LI>
     * <LI>PAYERKPP - КПП плательщика</LI>
     * <LI>PAYERRSACCOUNT - Расчетный счет плательщика</LI>
     * <LI>PAYMENTMETHOD - Вид оплаты</LI>
     * <LI>PAYMENTTYPE - Вид платежа</LI>
     * <LI>PRIORITY - Очередность</LI>
     * <LI>PURPOSE - Назначение платежа</LI>
     * <LI>RECEIPTDATE - Дата поступления на счет получателя</LI>
     * <LI>RECIPIENT - Наименование получателя</LI>
     * <LI>RECIPIENTACCOUNT - Счет получателя</LI>
     * <LI>RECIPIENTBANK - Наименование банка получателя</LI>
     * <LI>RECIPIENTBIK - БИК банка получателя</LI>
     * <LI>RECIPIENTCORACCOUNT - Корр счет банка получателя</LI>
     * <LI>RECIPIENTINN - ИНН получателя</LI>
     * <LI>RECIPIENTKPP - КПП получателя</LI>
     * <LI>RECIPIENTRSACCOUNT - Расчетный счет получателя</LI>
     * <LI>STATEID - Статус</LI>
     * <LI>TEMPLATE - Шаблон</LI>
     * <LI>TYPE - Тип движения поступление или списание</LI>
     * <LI>UPDATEDATE - Дата изменения</LI>
     * <LI>UPDATEUSERID - ИД изменившего пользователя</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BBankCashFlowBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BBankCashFlowBrowseListByParam", "dsB2BBankCashFlowBrowseListByParamCount", params);
        return result;
    }

    /**
     * Получить количество строк по статусу
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>COUNTERRORSTRING - Количество строк по статусу</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"BANKSTATEMENTID", "STATEID"})
    public Map<String,Object> dsB2BBankCashFlowCountStateString(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BBankCashFlowCountStateString", "dsB2BBankCashFlowCountStateStringCount", params);
        return result;
    }

    /**
     * Получить количество по типу изменения
     * @author reson
     * @param params
     * <UL>
     * <LI>BANKSTATEMENTID - ИД банковской выписки</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>TYPEMODIFYSTRING - Количество строк с типом изменения</LI>
     * </UL>
     * 
     */
    @WsMethod(requiredParams = {"BANKSTATEMENTID", "TYPEMODIFYSTRING"})
    public Map<String,Object> dsB2BBankCashFlowCountTypeModifyString(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BBankCashFlowCountTypeModifyString", "dsB2BBankCashFlowCountTypeModifyStringCount", params);
        return result;
    }



}
