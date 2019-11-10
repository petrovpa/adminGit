/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.bivsberposws.facade.pos;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.aspect.impl.version.NodeVersion;
import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.aspect.impl.ownerright.OwnerRightView;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности LossesRequest
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@State(idFieldName = "REQUESTID", startStateName = "LOSS_REQ_NEW", typeSysName = "LOSS_REQUEST")
@NodeVersion(nodeTableName="LOSS_REQUESTNODE",nodeTableIdFieldName="REQUESTNODEID",versionNumberParamName="VERNUMBER",nodeLastVersionNumberFieldName="LASTVERNUMBER",nodeRVersionFieldName="RVERSION")
@ProfileRights({
        @PRight(sysName="RPAccessPOS_Branch",
                name="Доступ по подразделению",
                joinStr="  inner join LOSS_REQUESTORGSTRUCT AOS on (t.REQUESTID = AOS.REQUESTID) inner join INS_DEPLVL DEPLVL on (AOS.ORGSTRUCTID = DEPLVL.OBJECTID) ",
                restrictionFieldName="DEPLVL.PARENTID",
                paramName="DEPARTMENTID")})
@OwnerRightView()
@IdGen(entityName="LOSS_REQUEST",idFieldName="REQUESTID")
@BOName("LossesRequest")
public class LossesRequestFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>BENEFICIARID - ИД выгодопреобретателя</LI>
     * <LI>BIC - БИК получателя</LI>
     * <LI>BKNUM - Номер банковской карты получателя</LI>
     * <LI>CALCAMOUNT - Рассчетная сумма выплаты</LI>
     * <LI>CANCELCOMMENT - Комментарий к отказу в возврате</LI>
     * <LI>CANCELREASON - Причина отказа возврата</LI>
     * <LI>CANCELREASONID - ИД причины отказа возврата</LI>
     * <LI>CAUSES - Обстоятельства и причины события</LI>
     * <LI>CONFIRMDOCS - Документы подтверждающие событие</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CREATEUSERROLE - роль пользователя создавшего заявку</LI>
     * <LI>CULPRIT - Виновник</LI>
     * <LI>CURACCOUNT - Расчетный счет получателя</LI>
     * <LI>DECISIONCSKO - Решение ЦСКО</LI>
     * <LI>DECISIONSK - Решение СК</LI>
     * <LI>DECLARERID - ИД заявителя</LI>
     * <LI>EVENTDATE - Дата события</LI>
     * <LI>FACEACCOUNT - Лицевой счет получателя</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>INN - ИНН получателя</LI>
     * <LI>INSOBJADDRESS - Адрес объекта страхования или события</LI>
     * <LI>INSOBJNAME - Наименование объекта страхования</LI>
     * <LI>INSOBJTYPE - Тип объекта страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>ISOTHERINS - Признак страхования в другой страховой</LI>
     * <LI>ISTHIRDPART - Признак предъявления претензий третьим лицам</LI>
     * <LI>KORACCOUNT - Корр. счет</LI>
     * <LI>KPP - КПП</LI>
     * <LI>LOSSES - Причиненные убытки</LI>
     * <LI>LOSSESAMOUNT - Предположительная сумма убытков</LI>
     * <LI>NAMEOTHERINS - Наименование другой страховой компании</LI>
     * <LI>REQUESTDATE - Дата заявки на возврат</LI>
     * <LI>REQUESTNODEID - ИД ноды</LI>
     * <LI>REQUESTNUM - Номер заявки</LI>
     * <LI>STATEID - Состояние заявки</LI>
     * <LI>TOTALAMOUNT - Итоговая сумма убытка</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>USERCOMMENT - Комментарии пользователя</LI>
     * <LI>USERCOMMENT2 - Комментарии сотрудников СГИ</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WHENWHOCLAIM - Кому и когда заявлено о событии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesRequestCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesRequestInsert", params);
        result.put("REQUESTID", params.get("REQUESTID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>BENEFICIARID - ИД выгодопреобретателя</LI>
     * <LI>BIC - БИК получателя</LI>
     * <LI>BKNUM - Номер банковской карты получателя</LI>
     * <LI>CALCAMOUNT - Рассчетная сумма выплаты</LI>
     * <LI>CANCELCOMMENT - Комментарий к отказу в возврате</LI>
     * <LI>CANCELREASON - Причина отказа возврата</LI>
     * <LI>CANCELREASONID - ИД причины отказа возврата</LI>
     * <LI>CAUSES - Обстоятельства и причины события</LI>
     * <LI>CONFIRMDOCS - Документы подтверждающие событие</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CREATEUSERROLE - роль пользователя создавшего заявку</LI>
     * <LI>CULPRIT - Виновник</LI>
     * <LI>CURACCOUNT - Расчетный счет получателя</LI>
     * <LI>DECISIONCSKO - Решение ЦСКО</LI>
     * <LI>DECISIONSK - Решение СК</LI>
     * <LI>DECLARERID - ИД заявителя</LI>
     * <LI>EVENTDATE - Дата события</LI>
     * <LI>FACEACCOUNT - Лицевой счет получателя</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>INN - ИНН получателя</LI>
     * <LI>INSOBJADDRESS - Адрес объекта страхования или события</LI>
     * <LI>INSOBJNAME - Наименование объекта страхования</LI>
     * <LI>INSOBJTYPE - Тип объекта страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>ISOTHERINS - Признак страхования в другой страховой</LI>
     * <LI>ISTHIRDPART - Признак предъявления претензий третьим лицам</LI>
     * <LI>KORACCOUNT - Корр. счет</LI>
     * <LI>KPP - КПП</LI>
     * <LI>LOSSES - Причиненные убытки</LI>
     * <LI>LOSSESAMOUNT - Предположительная сумма убытков</LI>
     * <LI>NAMEOTHERINS - Наименование другой страховой компании</LI>
     * <LI>REQUESTDATE - Дата заявки на возврат</LI>
     * <LI>REQUESTNODEID - ИД ноды</LI>
     * <LI>REQUESTNUM - Номер заявки</LI>
     * <LI>STATEID - Состояние заявки</LI>
     * <LI>TOTALAMOUNT - Итоговая сумма убытка</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>USERCOMMENT - Комментарии пользователя</LI>
     * <LI>USERCOMMENT2 - Комментарии сотрудников СГИ</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WHENWHOCLAIM - Кому и когда заявлено о событии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTID"})
    public Map<String,Object> dsLossesRequestInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsLossesRequestInsert", params);
        result.put("REQUESTID", params.get("REQUESTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BENEFICIARID - ИД выгодопреобретателя</LI>
     * <LI>BIC - БИК получателя</LI>
     * <LI>BKNUM - Номер банковской карты получателя</LI>
     * <LI>CALCAMOUNT - Рассчетная сумма выплаты</LI>
     * <LI>CANCELCOMMENT - Комментарий к отказу в возврате</LI>
     * <LI>CANCELREASON - Причина отказа возврата</LI>
     * <LI>CANCELREASONID - ИД причины отказа возврата</LI>
     * <LI>CAUSES - Обстоятельства и причины события</LI>
     * <LI>CONFIRMDOCS - Документы подтверждающие событие</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CREATEUSERROLE - роль пользователя создавшего заявку</LI>
     * <LI>CULPRIT - Виновник</LI>
     * <LI>CURACCOUNT - Расчетный счет получателя</LI>
     * <LI>DECISIONCSKO - Решение ЦСКО</LI>
     * <LI>DECISIONSK - Решение СК</LI>
     * <LI>DECLARERID - ИД заявителя</LI>
     * <LI>EVENTDATE - Дата события</LI>
     * <LI>FACEACCOUNT - Лицевой счет получателя</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>INN - ИНН получателя</LI>
     * <LI>INSOBJADDRESS - Адрес объекта страхования или события</LI>
     * <LI>INSOBJNAME - Наименование объекта страхования</LI>
     * <LI>INSOBJTYPE - Тип объекта страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>ISOTHERINS - Признак страхования в другой страховой</LI>
     * <LI>ISTHIRDPART - Признак предъявления претензий третьим лицам</LI>
     * <LI>KORACCOUNT - Корр. счет</LI>
     * <LI>KPP - КПП</LI>
     * <LI>LOSSES - Причиненные убытки</LI>
     * <LI>LOSSESAMOUNT - Предположительная сумма убытков</LI>
     * <LI>NAMEOTHERINS - Наименование другой страховой компании</LI>
     * <LI>REQUESTDATE - Дата заявки на возврат</LI>
     * <LI>REQUESTNODEID - ИД ноды</LI>
     * <LI>REQUESTNUM - Номер заявки</LI>
     * <LI>STATEID - Состояние заявки</LI>
     * <LI>TOTALAMOUNT - Итоговая сумма убытка</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>USERCOMMENT - Комментарии пользователя</LI>
     * <LI>USERCOMMENT2 - Комментарии сотрудников СГИ</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WHENWHOCLAIM - Кому и когда заявлено о событии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTID"})
    public Map<String,Object> dsLossesRequestUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesRequestUpdate", params);
        result.put("REQUESTID", params.get("REQUESTID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>BENEFICIARID - ИД выгодопреобретателя</LI>
     * <LI>BIC - БИК получателя</LI>
     * <LI>BKNUM - Номер банковской карты получателя</LI>
     * <LI>CALCAMOUNT - Рассчетная сумма выплаты</LI>
     * <LI>CANCELCOMMENT - Комментарий к отказу в возврате</LI>
     * <LI>CANCELREASON - Причина отказа возврата</LI>
     * <LI>CANCELREASONID - ИД причины отказа возврата</LI>
     * <LI>CAUSES - Обстоятельства и причины события</LI>
     * <LI>CONFIRMDOCS - Документы подтверждающие событие</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CREATEUSERROLE - роль пользователя создавшего заявку</LI>
     * <LI>CULPRIT - Виновник</LI>
     * <LI>CURACCOUNT - Расчетный счет получателя</LI>
     * <LI>DECISIONCSKO - Решение ЦСКО</LI>
     * <LI>DECISIONSK - Решение СК</LI>
     * <LI>DECLARERID - ИД заявителя</LI>
     * <LI>EVENTDATE - Дата события</LI>
     * <LI>FACEACCOUNT - Лицевой счет получателя</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>INN - ИНН получателя</LI>
     * <LI>INSOBJADDRESS - Адрес объекта страхования или события</LI>
     * <LI>INSOBJNAME - Наименование объекта страхования</LI>
     * <LI>INSOBJTYPE - Тип объекта страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>ISOTHERINS - Признак страхования в другой страховой</LI>
     * <LI>ISTHIRDPART - Признак предъявления претензий третьим лицам</LI>
     * <LI>KORACCOUNT - Корр. счет</LI>
     * <LI>KPP - КПП</LI>
     * <LI>LOSSES - Причиненные убытки</LI>
     * <LI>LOSSESAMOUNT - Предположительная сумма убытков</LI>
     * <LI>NAMEOTHERINS - Наименование другой страховой компании</LI>
     * <LI>REQUESTDATE - Дата заявки на возврат</LI>
     * <LI>REQUESTNODEID - ИД ноды</LI>
     * <LI>REQUESTNUM - Номер заявки</LI>
     * <LI>STATEID - Состояние заявки</LI>
     * <LI>TOTALAMOUNT - Итоговая сумма убытка</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>USERCOMMENT - Комментарии пользователя</LI>
     * <LI>USERCOMMENT2 - Комментарии сотрудников СГИ</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WHENWHOCLAIM - Кому и когда заявлено о событии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTID"})
    public Map<String,Object> dsLossesRequestModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsLossesRequestUpdate", params);
        result.put("REQUESTID", params.get("REQUESTID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"REQUESTID"})
    public void dsLossesRequestDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsLossesRequestDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>BENEFICIARID - ИД выгодопреобретателя</LI>
     * <LI>BIC - БИК получателя</LI>
     * <LI>BKNUM - Номер банковской карты получателя</LI>
     * <LI>CALCAMOUNT - Рассчетная сумма выплаты</LI>
     * <LI>CANCELCOMMENT - Комментарий к отказу в возврате</LI>
     * <LI>CANCELREASON - Причина отказа возврата</LI>
     * <LI>CANCELREASONID - ИД причины отказа возврата</LI>
     * <LI>CAUSES - Обстоятельства и причины события</LI>
     * <LI>CONFIRMDOCS - Документы подтверждающие событие</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CREATEUSERROLE - роль пользователя создавшего заявку</LI>
     * <LI>CULPRIT - Виновник</LI>
     * <LI>CURACCOUNT - Расчетный счет получателя</LI>
     * <LI>DECISIONCSKO - Решение ЦСКО</LI>
     * <LI>DECISIONSK - Решение СК</LI>
     * <LI>DECLARERID - ИД заявителя</LI>
     * <LI>EVENTDATE - Дата события</LI>
     * <LI>FACEACCOUNT - Лицевой счет получателя</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>INN - ИНН получателя</LI>
     * <LI>INSOBJADDRESS - Адрес объекта страхования или события</LI>
     * <LI>INSOBJNAME - Наименование объекта страхования</LI>
     * <LI>INSOBJTYPE - Тип объекта страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>ISOTHERINS - Признак страхования в другой страховой</LI>
     * <LI>ISTHIRDPART - Признак предъявления претензий третьим лицам</LI>
     * <LI>KORACCOUNT - Корр. счет</LI>
     * <LI>KPP - КПП</LI>
     * <LI>LOSSES - Причиненные убытки</LI>
     * <LI>LOSSESAMOUNT - Предположительная сумма убытков</LI>
     * <LI>NAMEOTHERINS - Наименование другой страховой компании</LI>
     * <LI>REQUESTDATE - Дата заявки на возврат</LI>
     * <LI>REQUESTNODEID - ИД ноды</LI>
     * <LI>REQUESTNUM - Номер заявки</LI>
     * <LI>STATEID - Состояние заявки</LI>
     * <LI>TOTALAMOUNT - Итоговая сумма убытка</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>USERCOMMENT - Комментарии пользователя</LI>
     * <LI>USERCOMMENT2 - Комментарии сотрудников СГИ</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WHENWHOCLAIM - Кому и когда заявлено о событии</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>BENEFICIARID - ИД выгодопреобретателя</LI>
     * <LI>BIC - БИК получателя</LI>
     * <LI>BKNUM - Номер банковской карты получателя</LI>
     * <LI>CALCAMOUNT - Рассчетная сумма выплаты</LI>
     * <LI>CANCELCOMMENT - Комментарий к отказу в возврате</LI>
     * <LI>CANCELREASON - Причина отказа возврата</LI>
     * <LI>CANCELREASONID - ИД причины отказа возврата</LI>
     * <LI>CAUSES - Обстоятельства и причины события</LI>
     * <LI>CONFIRMDOCS - Документы подтверждающие событие</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>CREATEDATE - Дата создания записи</LI>
     * <LI>CREATEUSERID - ИД пользователя создавшего запись</LI>
     * <LI>CREATEUSERROLE - роль пользователя создавшего заявку</LI>
     * <LI>CULPRIT - Виновник</LI>
     * <LI>CURACCOUNT - Расчетный счет получателя</LI>
     * <LI>DECISIONCSKO - Решение ЦСКО</LI>
     * <LI>DECISIONSK - Решение СК</LI>
     * <LI>DECLARERID - ИД заявителя</LI>
     * <LI>EVENTDATE - Дата события</LI>
     * <LI>FACEACCOUNT - Лицевой счет получателя</LI>
     * <LI>REQUESTID - ИД заявки на возврат</LI>
     * <LI>INN - ИНН получателя</LI>
     * <LI>INSOBJADDRESS - Адрес объекта страхования или события</LI>
     * <LI>INSOBJNAME - Наименование объекта страхования</LI>
     * <LI>INSOBJTYPE - Тип объекта страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>ISOTHERINS - Признак страхования в другой страховой</LI>
     * <LI>ISTHIRDPART - Признак предъявления претензий третьим лицам</LI>
     * <LI>KORACCOUNT - Корр. счет</LI>
     * <LI>KPP - КПП</LI>
     * <LI>LOSSES - Причиненные убытки</LI>
     * <LI>LOSSESAMOUNT - Предположительная сумма убытков</LI>
     * <LI>NAMEOTHERINS - Наименование другой страховой компании</LI>
     * <LI>REQUESTDATE - Дата заявки на возврат</LI>
     * <LI>REQUESTNODEID - ИД ноды</LI>
     * <LI>REQUESTNUM - Номер заявки</LI>
     * <LI>STATEID - Состояние заявки</LI>
     * <LI>TOTALAMOUNT - Итоговая сумма убытка</LI>
     * <LI>UPDATEDATE - Дата изменения записи</LI>
     * <LI>UPDATEUSERID - ИД пользователя изменившего запись</LI>
     * <LI>USERCOMMENT - Комментарии пользователя</LI>
     * <LI>USERCOMMENT2 - Комментарии сотрудников СГИ</LI>
     * <LI>VERNUMBER - Номер версии</LI>
     * <LI>WHENWHOCLAIM - Кому и когда заявлено о событии</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsLossesRequestBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsLossesRequestBrowseListByParam", "dsLossesRequestBrowseListByParamCount", params);
        return result;
    }





}
