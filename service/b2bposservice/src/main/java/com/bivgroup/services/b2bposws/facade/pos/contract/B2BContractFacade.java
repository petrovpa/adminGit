/*
* Copyright (c) Diasoft 2004-2013
*/
package com.bivgroup.services.b2bposws.facade.pos.contract;


import java.util.HashMap;
import java.util.Map;

import ru.diasoft.services.inscore.aspect.impl.orgstruct.OrgStruct;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import ru.diasoft.services.inscore.aspect.impl.auth.Auth;
import ru.diasoft.services.inscore.aspect.impl.state.State;
import ru.diasoft.services.inscore.aspect.impl.binaryfile.BinaryFile;
import ru.diasoft.services.inscore.aspect.impl.version.NodeVersion;
import ru.diasoft.services.inscore.aspect.impl.profilerights.PRight;
import ru.diasoft.services.inscore.aspect.impl.profilerights.ProfileRights;
import ru.diasoft.services.inscore.aspect.impl.guididgen.GUIDIdGen;
import ru.diasoft.services.inscore.aspect.impl.idgen.IdGen;
import ru.diasoft.services.inscore.aspect.impl.ownerright.OwnerRightView;
import ru.diasoft.services.inscore.system.annotations.BOName;



/**
 * Фасад для сущности B2BContract
 *
 * @author reson
 */
@Auth(onlyCreatorAccess = false)
@State(idFieldName = "CONTRID", startStateName = "B2B_CONTRACT_DRAFT", typeSysName = "B2B_CONTRACT")
@BinaryFile(objTableName = "B2B_CONTR", objTablePKFieldName = "CONTRID")
@NodeVersion(nodeTableName="B2B_CONTRNODE",nodeTableIdFieldName="CONTRNODEID",versionNumberParamName="VERNUMBER",nodeLastVersionNumberFieldName="LASTVERNUMBER",nodeRVersionFieldName="RVERSION")
@ProfileRights({
        @PRight(sysName="RPAccessPOS_Branch",
                name="Доступ по подразделению",
                joinStr="  inner join B2B_CONTRORGSTRUCT COS on COS.OBJLEVEL is null and t.CONTRID = COS.CONTRID and (COS.ISBLOCKED != 1 or COS.ISBLOCKED is null) inner join INS_DEPLVL DEPLVL on (COS.ORGSTRUCTID = DEPLVL.OBJECTID) ",
                restrictionFieldName="DEPLVL.PARENTID",
                paramName="DEPARTMENTID")})
@OwnerRightView(accessByUserRole = true, 
                joinStr = " left join B2B_CONTRORGSTRUCT ORGSTR on (T.CONTRID = ORGSTR.CONTRID) ")

@GUIDIdGen(idFieldName="EXTERNALID")
@OrgStruct(tableName = "B2B_CONTRORGSTRUCT", orgStructPKFieldName = "CONTRORGSTRUCTID", objTablePKFieldName = "CONTRID")
@IdGen(entityName="B2B_CONTR",idFieldName="CONTRID")
@BOName("B2BContract")
public class B2BContractFacade extends BaseFacade {



    /**
     * Создать объект с генерацией id
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CONTRNUMBER - Полный номер версии договора</LI>
     * <LI>CONTRPOLNUM - Номер полиса (отдельно от серии)</LI>
     * <LI>CONTRPOLSER - Серия полиса</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>CURRENCYRATE - Курс приведения из валюты договора к валюте расчета</LI>
     * <LI>DOCUMENTDATE - Дата оформления</LI>
     * <LI>DURATION - Срок действия договора</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FILLPROGRESS - Прогресс заполнения договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>INSREGIONCODE - Регион страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>INSURERREPID - ИД представителя страхователя</LI>
     * <LI>LINK - Ссылка на страницу оформления полиса</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>NUMMETHODID - Метод нумерации</LI>
     * <LI>PAYVARID - Вариант выплат по договору</LI>
     * <LI>PREMCURRENCYID - Валюта ответственности</LI>
     * <LI>PREMDELTA - Изменение премии</LI>
     * <LI>PREMVALUE - Премия в валюте ответственности</LI>
     * <LI>PRINTDOCEMAILSEND - Документ выслан по EMail</LI>
     * <LI>PRINTDOCFORMED - Документ сформирован</LI>
     * <LI>PRODPROGID - ИД программы по продукту</LI>
     * <LI>PRODVERID - ИД версии продукта, по которой заключается договор</LI>
     * <LI>REFERRAL - Реферал</LI>
     * <LI>REFERRALBACK - Реферал, обратная ссылка</LI>
     * <LI>REQUESTQUEUEID - ИД очереди запросов</LI>
     * <LI>SALESOFFICE - Офис продаж</LI>
     * <LI>SELLERID - ИД продавца</LI>
     * <LI>SESSIONID - ИД сессии</LI>
     * <LI>SIGNDATE - Дата подписания</LI>
     * <LI>SMSCODE - Смс код</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>STATEID - ИД состояния договора</LI>
     * <LI>TERMID - ИД срока</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRNODEID"})
    public Map<String,Object> dsB2BContractCreate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractInsert", params);
        result.put("CONTRID", params.get("CONTRID"));
        return result;
    }





    /**
     * Создать объект без генерации id
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CONTRNUMBER - Полный номер версии договора</LI>
     * <LI>CONTRPOLNUM - Номер полиса (отдельно от серии)</LI>
     * <LI>CONTRPOLSER - Серия полиса</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>CURRENCYRATE - Курс приведения из валюты договора к валюте расчета</LI>
     * <LI>DOCUMENTDATE - Дата оформления</LI>
     * <LI>DURATION - Срок действия договора</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FILLPROGRESS - Прогресс заполнения договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>INSREGIONCODE - Регион страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>INSURERREPID - ИД представителя страхователя</LI>
     * <LI>LINK - Ссылка на страницу оформления полиса</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>NUMMETHODID - Метод нумерации</LI>
     * <LI>PAYVARID - Вариант выплат по договору</LI>
     * <LI>PREMCURRENCYID - Валюта ответственности</LI>
     * <LI>PREMDELTA - Изменение премии</LI>
     * <LI>PREMVALUE - Премия в валюте ответственности</LI>
     * <LI>PRINTDOCEMAILSEND - Документ выслан по EMail</LI>
     * <LI>PRINTDOCFORMED - Документ сформирован</LI>
     * <LI>PRODPROGID - ИД программы по продукту</LI>
     * <LI>PRODVERID - ИД версии продукта, по которой заключается договор</LI>
     * <LI>REFERRAL - Реферал</LI>
     * <LI>REFERRALBACK - Реферал, обратная ссылка</LI>
     * <LI>REQUESTQUEUEID - ИД очереди запросов</LI>
     * <LI>SALESOFFICE - Офис продаж</LI>
     * <LI>SELLERID - ИД продавца</LI>
     * <LI>SESSIONID - ИД сессии</LI>
     * <LI>SIGNDATE - Дата подписания</LI>
     * <LI>SMSCODE - Смс код</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>STATEID - ИД состояния договора</LI>
     * <LI>TERMID - ИД срока</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRNODEID", "CONTRID"})
    public Map<String,Object> dsB2BContractInsert(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.insertQuery("dsB2BContractInsert", params);
        result.put("CONTRID", params.get("CONTRID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CONTRNUMBER - Полный номер версии договора</LI>
     * <LI>CONTRPOLNUM - Номер полиса (отдельно от серии)</LI>
     * <LI>CONTRPOLSER - Серия полиса</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>CURRENCYRATE - Курс приведения из валюты договора к валюте расчета</LI>
     * <LI>DOCUMENTDATE - Дата оформления</LI>
     * <LI>DURATION - Срок действия договора</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FILLPROGRESS - Прогресс заполнения договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>INSREGIONCODE - Регион страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>INSURERREPID - ИД представителя страхователя</LI>
     * <LI>LINK - Ссылка на страницу оформления полиса</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>NUMMETHODID - Метод нумерации</LI>
     * <LI>PAYVARID - Вариант выплат по договору</LI>
     * <LI>PREMCURRENCYID - Валюта ответственности</LI>
     * <LI>PREMDELTA - Изменение премии</LI>
     * <LI>PREMVALUE - Премия в валюте ответственности</LI>
     * <LI>PRINTDOCEMAILSEND - Документ выслан по EMail</LI>
     * <LI>PRINTDOCFORMED - Документ сформирован</LI>
     * <LI>PRODPROGID - ИД программы по продукту</LI>
     * <LI>PRODVERID - ИД версии продукта, по которой заключается договор</LI>
     * <LI>REFERRAL - Реферал</LI>
     * <LI>REFERRALBACK - Реферал, обратная ссылка</LI>
     * <LI>REQUESTQUEUEID - ИД очереди запросов</LI>
     * <LI>SALESOFFICE - Офис продаж</LI>
     * <LI>SELLERID - ИД продавца</LI>
     * <LI>SESSIONID - ИД сессии</LI>
     * <LI>SIGNDATE - Дата подписания</LI>
     * <LI>SMSCODE - Смс код</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>STATEID - ИД состояния договора</LI>
     * <LI>TERMID - ИД срока</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String,Object> dsB2BContractUpdate(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        
        this.updateQuery("dsB2BContractUpdate", params);
        result.put("CONTRID", params.get("CONTRID"));
        return result;
    }





    /**
     * Изменить объект
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CONTRNUMBER - Полный номер версии договора</LI>
     * <LI>CONTRPOLNUM - Номер полиса (отдельно от серии)</LI>
     * <LI>CONTRPOLSER - Серия полиса</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>CURRENCYRATE - Курс приведения из валюты договора к валюте расчета</LI>
     * <LI>DOCUMENTDATE - Дата оформления</LI>
     * <LI>DURATION - Срок действия договора</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FILLPROGRESS - Прогресс заполнения договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>INSREGIONCODE - Регион страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>INSURERREPID - ИД представителя страхователя</LI>
     * <LI>LINK - Ссылка на страницу оформления полиса</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>NUMMETHODID - Метод нумерации</LI>
     * <LI>PAYVARID - Вариант выплат по договору</LI>
     * <LI>PREMCURRENCYID - Валюта ответственности</LI>
     * <LI>PREMDELTA - Изменение премии</LI>
     * <LI>PREMVALUE - Премия в валюте ответственности</LI>
     * <LI>PRINTDOCEMAILSEND - Документ выслан по EMail</LI>
     * <LI>PRINTDOCFORMED - Документ сформирован</LI>
     * <LI>PRODPROGID - ИД программы по продукту</LI>
     * <LI>PRODVERID - ИД версии продукта, по которой заключается договор</LI>
     * <LI>REFERRAL - Реферал</LI>
     * <LI>REFERRALBACK - Реферал, обратная ссылка</LI>
     * <LI>REQUESTQUEUEID - ИД очереди запросов</LI>
     * <LI>SALESOFFICE - Офис продаж</LI>
     * <LI>SELLERID - ИД продавца</LI>
     * <LI>SESSIONID - ИД сессии</LI>
     * <LI>SIGNDATE - Дата подписания</LI>
     * <LI>SMSCODE - Смс код</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>STATEID - ИД состояния договора</LI>
     * <LI>TERMID - ИД срока</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public Map<String,Object> dsB2BContractModify(Map<String, Object> params) throws Exception {
        Map<String,Object> result = new HashMap<String, Object>();
        this.updateQuery("dsB2BContractUpdate", params);
        result.put("CONTRID", params.get("CONTRID"));
        return result;
    }





    /**
     * Удалить объект по id
     * @author reson
     * @param params
     * <UL>
     * <LI>CONTRID - ИД договора</LI>
     * </UL>
     * @return
     * <UL>
     * </UL>
     */
    @WsMethod(requiredParams = {"CONTRID"})
    public void dsB2BContractDelete(Map<String, Object> params) throws Exception {
        this.deleteQuery("dsB2BContractDelete", params);
    }





    /**
     * Получить объекты в виде списка по ограничениям
     * @author reson
     * @param params
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CONTRNUMBER - Полный номер версии договора</LI>
     * <LI>CONTRPOLNUM - Номер полиса (отдельно от серии)</LI>
     * <LI>CONTRPOLSER - Серия полиса</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>CURRENCYRATE - Курс приведения из валюты договора к валюте расчета</LI>
     * <LI>DOCUMENTDATE - Дата оформления</LI>
     * <LI>DURATION - Срок действия договора</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FILLPROGRESS - Прогресс заполнения договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>INSREGIONCODE - Регион страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>INSURERREPID - ИД представителя страхователя</LI>
     * <LI>LINK - Ссылка на страницу оформления полиса</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>NUMMETHODID - Метод нумерации</LI>
     * <LI>PAYVARID - Вариант выплат по договору</LI>
     * <LI>PREMCURRENCYID - Валюта ответственности</LI>
     * <LI>PREMDELTA - Изменение премии</LI>
     * <LI>PREMVALUE - Премия в валюте ответственности</LI>
     * <LI>PRINTDOCEMAILSEND - Документ выслан по EMail</LI>
     * <LI>PRINTDOCFORMED - Документ сформирован</LI>
     * <LI>PRODPROGID - ИД программы по продукту</LI>
     * <LI>PRODVERID - ИД версии продукта, по которой заключается договор</LI>
     * <LI>REFERRAL - Реферал</LI>
     * <LI>REFERRALBACK - Реферал, обратная ссылка</LI>
     * <LI>REQUESTQUEUEID - ИД очереди запросов</LI>
     * <LI>SALESOFFICE - Офис продаж</LI>
     * <LI>SELLERID - ИД продавца</LI>
     * <LI>SESSIONID - ИД сессии</LI>
     * <LI>SIGNDATE - Дата подписания</LI>
     * <LI>SMSCODE - Смс код</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>STATEID - ИД состояния договора</LI>
     * <LI>TERMID - ИД срока</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     * @return
     * <UL>
     * <LI>CANCELDATE - Дата расторжения договора</LI>
     * <LI>CONTRNODEID - ИД договора со всеми версиями</LI>
     * <LI>CONTRNDNUMBER - Номер договора</LI>
     * <LI>CONTRNUMBER - Полный номер версии договора</LI>
     * <LI>CONTRPOLNUM - Номер полиса (отдельно от серии)</LI>
     * <LI>CONTRPOLSER - Серия полиса</LI>
     * <LI>CREATEDATE - Дата создания договора</LI>
     * <LI>CREATEUSERID - ИД пользователя, создавшего договор</LI>
     * <LI>CURRENCYRATE - Курс приведения из валюты договора к валюте расчета</LI>
     * <LI>DOCUMENTDATE - Дата оформления</LI>
     * <LI>DURATION - Срок действия договора</LI>
     * <LI>EXTERNALID - Внешний ИД</LI>
     * <LI>FILLPROGRESS - Прогресс заполнения договора</LI>
     * <LI>FINISHDATE - Дата окончания действия договора</LI>
     * <LI>CONTRID - ИД договора</LI>
     * <LI>INSAMCURRENCYID - Валюта страховой суммы</LI>
     * <LI>INSAMVALUE - Размер страховой суммы</LI>
     * <LI>INSREGIONCODE - Регион страхования</LI>
     * <LI>INSURERID - ИД страхователя</LI>
     * <LI>INSURERREPID - ИД представителя страхователя</LI>
     * <LI>LINK - Ссылка на страницу оформления полиса</LI>
     * <LI>NOTE - Примечание</LI>
     * <LI>NUMMETHODID - Метод нумерации</LI>
     * <LI>PAYVARID - Вариант выплат по договору</LI>
     * <LI>PREMCURRENCYID - Валюта ответственности</LI>
     * <LI>PREMDELTA - Изменение премии</LI>
     * <LI>PREMVALUE - Премия в валюте ответственности</LI>
     * <LI>PRINTDOCEMAILSEND - Документ выслан по EMail</LI>
     * <LI>PRINTDOCFORMED - Документ сформирован</LI>
     * <LI>PRODPROGID - ИД программы по продукту</LI>
     * <LI>PRODVERID - ИД версии продукта, по которой заключается договор</LI>
     * <LI>REFERRAL - Реферал</LI>
     * <LI>REFERRALBACK - Реферал, обратная ссылка</LI>
     * <LI>REQUESTQUEUEID - ИД очереди запросов</LI>
     * <LI>SALESOFFICE - Офис продаж</LI>
     * <LI>SELLERID - ИД продавца</LI>
     * <LI>SESSIONID - ИД сессии</LI>
     * <LI>SIGNDATE - Дата подписания</LI>
     * <LI>SMSCODE - Смс код</LI>
     * <LI>STARTDATE - Дата начала действия договора</LI>
     * <LI>STATEID - ИД состояния договора</LI>
     * <LI>TERMID - ИД срока</LI>
     * <LI>UPDATEDATE - Дата редактирования договора</LI>
     * <LI>UPDATEUSERID - ИД пользователя, редактировавшего договор</LI>
     * <LI>VERNUMBER - Номер версии договора</LI>
     * </UL>
     */
    @WsMethod(requiredParams = {})
    public Map<String,Object> dsB2BContractBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String,Object> result = this.selectQuery("dsB2BContractBrowseListByParam", "dsB2BContractBrowseListByParamCount", params);
        return result;
    }





}
