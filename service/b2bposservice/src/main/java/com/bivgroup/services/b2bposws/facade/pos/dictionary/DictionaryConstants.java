package com.bivgroup.services.b2bposws.facade.pos.dictionary;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mmamaev
 */
public interface DictionaryConstants {

    // префиксы для имен сущностей (новый хибернейт)
    public static final String DCT_MODULE_PREFIX_CRM = "com.bivgroup.crm.";
    public static final String DCT_MODULE_PREFIX_TERMINATION = "com.bivgroup.termination.";
    public static final String DCT_MODULE_PREFIX_LOSS = "com.bivgroup.loss.";
    public static final String DCT_MODULE_PREFIX_MESSAGES = "com.bivgroup.messages.";
    public static final String DCT_MODULE_PREFIX_SYSTEM = "com.bivgroup.system.";
    public static final String DCT_MODULE_PREFIX_IMPORTS = "com.bivgroup.imports.";
    public static final String DCT_MODULE_PREFIX_UNDERWRITING = "com.bivgroup.underwriting.";

    /** Имя поля для прямого указания сущности, должно содержать наименование типа (без префикса с полным именем пакета) */
    public static final String ENTITY_TYPE_NAME_FIELD_NAME = "$type$";

    /** Имя сущности 'Адрес' (таблица SD_ADDRESS) */
    public static final String ADDRESS_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "Address";
    /** Имя сущности 'Банковские реквизиты' (таблица SD_BANKDETAILS) */
    public static final String BANK_DETAILS_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "BankDetails";
    /** Имя сущности 'Клиент' (таблица CDM_CLIENT) */
    public static final String CLIENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "Client";
    /** Имя сущности 'Адрес клиента' (таблица CDM_CLIENT_ADDRESS) */
    public static final String CLIENT_ADDRESS_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientAddress";
    /** Имя сущности 'Банковские реквизиты клиента' (таблица CDM_CLIENT_BANKDETAILS) */
    public static final String CLIENT_BANK_DETAILS_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientBankDetails";
    /** Имя сущности 'Контакт клиента' (таблица CDM_CLIENT_CONTACT) */
    public static final String CLIENT_CONTACT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientContact";
    /** Имя сущности 'Документ клиента' (таблица CDM_CLIENT_DOCUMENT) */
    public static final String CLIENT_DOCUMENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientDocument";
    /** Имя сущности 'Аккаунт клиента' (таблица SD_CLIENTPROFILE) */
    public static final String CLIENT_PROFILE_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientProfile";
    /**
     * @deprecated use CLIENT_AGREEMENT_ENTITY_NAME
     */
    @Deprecated
    public static final String CLIENT_PROFILE_AGREEMENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientProfileAgreement";
    /** Имя сущности 'Пользовательское соглашение' (таблица SD_CLIENTPROFILE_AGREEMENT) */
    public static final String CLIENT_AGREEMENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientProfileAgreement";
    /** Имя сущности 'Событие акаунта клиента' (таблица SD_CLIENTPROFILE_EVENT) */
    public static final String CLIENT_PROFILE_EVENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientProfileEvent";
    /** Имя сущности 'Токен акаунта клиента' (таблица SD_CLIENTPROFILE_TOKEN) */
    public static final String CLIENT_PROFILE_TOKEN_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientProfileToken";
    /** Имя сущности 'Признаки клиента' (таблица CDM_CLIENT_PROPERTY) */
    public static final String CLIENT_PROPERTY_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientProperty";
    /** Имя сущности 'Контакт' (таблица SD_CONTACT) */
    public static final String CONTACT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "Contact";
    /** Имя сущности 'Водитель по договору страхования ' (таблица PD_CONTRACT_DRIVER) */
    public static final String CONTRACT_DRIVER_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ContractDriver";
    /** Имя сущности 'Участник договора (физическое лицо)' (таблица PD_CONTRACT_PMEMBER) */
    public static final String CONTRACT_PMEMBER_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ContractPMember";
    /** Имя сущности 'Заявление по договору страхования' (таблица PD_DECLARATION) */
    public static final String DECLARATION_FOR_CONTRACT_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "DeclarationForContract";
    /** Имя сущности 'Заявление на расторжение договора страхования' (таблица PD_DECLARATIONOFAVOID) */
    public static final String DECLARATION_OF_AVOIDANCE_FOR_CONTRACT_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "DeclarationOfAvoidanceForContract";
    /** Имя сущности 'Заявление на изменение условий договора страхования' (таблица PD_DECLARATIONOFCHANGE) */
    public static final String DECLARATION_OF_CHANGE_FOR_CONTRACT_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "DeclarationOfChangeForContract";
    /** Имя сущности 'Документ' (таблица SD_DOCUMENT) */
    public static final String DOCUMENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "Document";
    /** Имя сущности 'Клиент(индивидуальный предприниматель)' (таблица CDM_ECLIENT) */
    public static final String ECLIENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "EClient";
    /** Имя сущности 'Клиент(индивидуальный предприниматель) - версия' (таблица CDM_ECLIENT_VER) */
    public static final String ECLIENT_VER_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "EClient_VER";
    /** Имя сущности 'Индивидуальный предприниматель' (таблица CDM_EPERSON) */
    public static final String EPERSON_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "EPerson";
    /** Имя сущности 'Клиент(юридическое лицо)' (таблица CDM_JCLIENT) */
    public static final String JCLIENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "JClient";
    /** Имя сущности 'Клиент(юридическое лицо) - версия' (таблица CDM_JCLIENT_VER) */
    public static final String JCLIENT_VER_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "JClient_VER";
    /** Имя сущности 'Юридическое лицо' (таблица CDM_JPERSON) */
    public static final String JPERSON_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "JPerson";
    /** Имя сущности 'Классификатор типов адресов' (таблица HB_KINDADDRESS) */
    public static final String KIND_ADDRESS_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindAddress";
    /** Имя сущности 'Классификатор типов пользовательских соглашений' (таблица HB_KINDAGREEMCLIENTPROFILE) */

    /**
     * @deprecated use KIND_AGREEMENT_CLIENT_ENTITY_NAME
     */
    @Deprecated
    public static final String KIND_AGREEMENT_CLIENT_PROFILE_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindAgreementClientProfile";
    public static final String KIND_AGREEMENT_CLIENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindAgreementClientProfile";

    /** Имя сущности 'Классификатор причин изменения договора страхования' (таблица HB_KINDCHANGEREASON) */
    public static final String KIND_CHANGE_REASON_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "KindChangeReason";
    /** Имя сущности 'Классификатор причин изменения договора страхования по продукту' (таблица HB_PRODKINDCHANGEREASON) */
    public static final String PROD_KIND_CHANGE_REASON_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "ProdKindChangeReason";
    /** Имя сущности 'Классификатор описаний для изменения договора страхования по продукту' (таблица HB_PRODKINDDECLARATION) */
    public static final String PROD_KIND_DECLARATION_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "ProdKindDeclaration";
    /** Имя сущности 'Классификатор типов признаков клиента' (таблица HB_KINDCLIENTPROPERTY) */
    public static final String KIND_CLIENT_PROPERTY_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindClientProperty";
    /** Имя сущности 'Классификатор типов контактов' (таблица HB_KINDCONTACT) */
    public static final String KIND_CONTACT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindContact";
    /** Имя сущности 'Классификатор типов участников по договору страхования' (таблица PD_KINDCONTRACTMEMBER) */
    public static final String KIND_CONTRACT_MEMBER_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindContractMember";
    /** Имя сущности 'Классификатор типов документов' (таблица HB_KINDDOCUMENT) */
    public static final String KIND_DOCUMENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindDocument";
    /** Имя сущности 'Классификатор типов события акаунта клиента' (таблица HB_KINDEVENTCLIENTPROFILE) */
    public static final String KIND_EVENT_CLIENT_PROFILE_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindEventClientProfile";
    /** Имя сущности 'Классификатор ролей наблюдателя' (таблица HD_KINDSHAREROLE) */
    public static final String KIND_SHARE_ROLE_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindShareRole";
    /** Имя сущности 'Классификатор статусов' (таблица HB_KINDSTATUS) */
    public static final String KIND_STATUS_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindStatus";
    /** Имя сущности 'Классификатор организационно-правовых форм' (таблица HB_LEGALFORMSOFBUSINESS) */
    public static final String LEGAL_FORMS_OF_BUSINESS_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "LegalFormsOfBusiness";
    /** Имя сущности 'Клиент(физическое лицо)' (таблица CDM_PCLIENT) */
    public static final String PCLIENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "PClient";
    /** Имя сущности 'Клиент(физическое лицо) - версия' (таблица CDM_PCLIENT_VER) */
    public static final String PCLIENT_VER_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "PClient_VER";
    /** Имя сущности 'Контрагент' (таблица CDM_PERSON) */
    public static final String PERSON_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "Person";
    /** Имя сущности 'Адрес контрагента' (таблица CDM_PERSON_ADDRESS) */
    public static final String PERSON_ADDRESS_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "PersonAddress";
    /** Имя сущности 'Контакт контрагента' (таблица CDM_PERSON_CONTACT) */
    public static final String PERSON_CONTACT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "PersonContact";
    /** Имя сущности 'Документ контрагента' (таблица CDM_PERSON_DOCUMENT) */
    public static final String PERSON_DOCUMENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "PersonDocument";
    /** Имя сущности 'Физическое лицо' (таблица CDM_PPERSON) */
    public static final String PPERSON_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "PPerson";
    /** Имя сущности 'Дети физического лица' (таблица CDM_PPERSON_CHILD) */
    public static final String PPERSON_CHILD_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "PPersonChild";
    /** Имя сущности 'Причина изменения договора страхования' (таблица PD_REASONCHANGE) */
    public static final String REASON_CHANGE_FOR_CONTRACT_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "ReasonChangeForContract";
    /** Имя сущности 'Причина изменения договора страхования (заморозка/разморозка)' (таблица PD_REASONCHANGE_F) */
    public static final String REASON_CHANGE_FOR_CONTRACT_FREEZE_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "ReasonChangeForContract_Freeze";
    /** Имя сущности 'Причина изменения договора страхования (управление опцией)' (таблица PD_REASONCHANGE_O) */
    public static final String REASON_CHANGE_FOR_CONTRACT_OPTION_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "ReasonChangeForContract_Option";
    /** Имя сущности 'Канал приема' (таблица HD_RECEIVINGCHANNEL) */
    public static final String RECEIVING_CHANNEL_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "ReceivingChannel";
    /** Имя сущности 'Кампания продаж' (таблица SD_SALESCAMPAIGN) */
    public static final String SALES_CAMPAIGN_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "SalesCampaign";
    /** Имя сущности 'Персональное предложение' (таблица SD_SALESCAMPAIGN_OFFER) */
    public static final String SALES_CAMPAIGN_OFFER_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "SalesCampaignOffer";
    /** Имя сущности 'Наблюдатель договора страхования' (таблица SD_SHARE_CONTRACT) */
    public static final String SHARE_CONTRACT_INS_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ShareContractIns";
    /** Имя сущности 'Наблюдатель договора страхования' (таблица SD_SHARE_CONTRACT) */
    public static final String SHARE_CONTRACT_ENTITY_NAME = SHARE_CONTRACT_INS_ENTITY_NAME;
    /** Имя сущности 'Классификатор оснований расторжения договора страхования' (таблица HD_TERMINATIONREASON) */
    public static final String TERMINATION_REASON_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "TerminationReason";
    /** Имя сущности 'Сообщение пользователя' (таблица SD_USERPOST) */
    public static final String USER_POST_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "UserPost";
    /** Имя сущности 'Значения причины изменения договора страхования' (таблица PD_REASONCHANGE_VALUE) */
    public static final String VALUE_REASON_CHANGE_FOR_CONTRACT_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "ValueReasonChangeForContract";
    /** Имя сущности 'Заявление по страховому событию' (таблица CH_NOTIFICATION) */
    public static final String CLAIMHANDLINGNOTIFICATION_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "ClaimHandlingNotification";
    /** Имя сущности 'Настройка категорий ущерба/вреда по страховому продукту' (таблица PF_PRODUCT_COD) */
    public static final String PRODUCTCOD_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "SettingCategoryOfDamageOnInsProduct";
    /** Имя сущности 'Страховое событие' (таблица CH_INSEVENT) */
    public static final String CLAIMHANDLINGINSEVENT_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "ClaimHandlingInsEvent";
    /** Имя сущности 'Страховое событие' (таблица CH_MESSAGEINSEVENT) */
    public static final String CLAIMHANDLINGMESSAGEINSEVENT_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "ClaimHandlingMessageInsEvent";

    /** Имя сущности 'Уведомление о СС' (таблица B2B_LOSSNOTICE) */
    public static final String LOSS_NOTICE_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "LossNotice";
    /** Имя сущности 'Страховое событие' (таблица B2B_INSEVENT) */
    public static final String LOSS_EVENT_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "LossEvent";
    /** Имя сущности 'Причина события' (таблица B2B_LOSS_DAMAGECAT) */
    public static final String LOSS_DAMAGE_CATEGORY_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "LossDamageCategory";
    /** Имя сущности 'Описание рисков для интеграции' (таблица HB_KINDINTEGRATIONRISK) */
    public static final String KIND_INTEGRATION_RISK_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "KindIntegrationRisk";

    /** Имя сущности уведомления */
    public static final String CLIENT_PROFILE_NOTIFICATION_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientProfileNotification";

    /** Имя сущности 'Классификатор стран' (таблица B2B_COUNTRY) */
    String KIND_COUNTRY_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindCountry";

    /** Имя сущности 'Чат' (таблица SD_CHAT) */
    public static final String MESSAGES_CHAT_ENTITY_NAME = DCT_MODULE_PREFIX_MESSAGES + "Chat";
    /** Имя сущности 'Сообщение' (таблица SD_MESSAGE) */
    public static final String MESSAGE_ENTITY_NAME = DCT_MODULE_PREFIX_MESSAGES + "Message";
    /** Имя сущности 'Кореспондент сообщения' (таблица SD_MESSAGE_CORRESPONDENT) */
    public static final String MESSAGE_CORRESPONDENT_ENTITY_NAME = DCT_MODULE_PREFIX_MESSAGES + "MessageCorrespondent";
    /** Имя сущности 'Получатель сообщения' (таблица SD_MESSAGE_RECIPIENT) */
    public static final String MESSAGE_RECIPIENT_ENTITY_NAME = DCT_MODULE_PREFIX_MESSAGES + "MessageRecipient";

    /** Имя сущности 'Выкупная сумма по СП' (таблица B2B_PRODREDEMPTIONAMOUNT */
    public static final String REDEMPTION_AMOUNT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "RedemptionAmount";

    /** Имя сущности 'Тип импорта' (таблица HB_KINDKMIMPORT) */
    public static final String KIND_KM_IMPORT_ENTITY_NAME = DCT_MODULE_PREFIX_SYSTEM + "KindKMImport";

    /** Состояние (таблица CORE_SM_STATE) */
    public static final String SM_STATE_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "SMState";
    /** Состояние (таблица CORE_SM_TRANS) */
    public static final String SM_TRANSITION_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "SMTransition";

    /** Имя сущности "Сессия импорта 'Оргструктура'" (таблица B2B_IMPORTSESSION) */
    public static final String IMPORT_SESSION_DEPARTMENT_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionDepartment";
    /** Имя сущности "Сессия импорта 'КМ-ВСП'" (таблица B2B_IMPORTSESSION) */
    public static final String IMPORT_SESSION_MANAGER_DEPARTMENT_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionManagerDepartment";
    /** Имя сущности "Сессия импорта 'КМ-Договор'" (таблица B2B_IMPORTSESSION) */
    public static final String IMPORT_SESSION_MANAGER_CONTRACT_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionManagerContract";

    /** Имя сущности "Содержимое сессии импорта 'Оргструктура'" (таблица B2B_IS_CNT_DEPARTMENT) */
    public static final String IMPORT_SESSION_CONTENT_DEPARTMENT_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionContentDepartment";
    /** Имя сущности "Содержимое сессии импорта 'КМ-ВСП'" (таблица B2B_IS_CNT_MANAGER_DEPARTMENT) */
    public static final String IMPORT_SESSION_CONTENT_MANAGER_DEPARTMENT_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionContentManagerDepartment";
    /** Имя сущности "Содержимое сессии импорта 'КМ-Договор'" (таблица B2B_IS_CNT_MANAGER_CONTRACT) */
    public static final String IMPORT_SESSION_CONTENT_MANAGER_CONTRACT_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionContentManagerContract";

    /** Имя сущности "Классификатор типов подразделений" (таблица HB_IS_DEPTYPE) */
    public static final String KIND_IMPORT_SESSION_DEPARTMENT_TYPE_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "KindImportSessionDepartmentType";
    /** Имя сущности "Классификатор сегментов обслуживания подразделений" (таблица HB_IS_DEPTYPE) */
    public static final String KIND_IMPORT_SESSION_DEPARTMENT_SEGMENT_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "KindImportSessionDepartmentSegment";
    /** Имя сущности "Классификатор должностей клиентских менеджеров" (таблица HB_IS_MANAGERPOSITION) */
    public static final String KIND_IMPORT_SESSION_MANAGER_POSITION_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "KindImportSessionManagerPosition";
    /** Имя сущности "Классификатор ролей клиентских менеджеров в подразделениях" (таблица HB_IS_MANAGERROLE) */
    public static final String KIND_IMPORT_SESSION_MANAGER_ROLE_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "KindImportSessionManagerRole";
    /** Имя сущности "Классификатор типов событий протокола обработки содержимого сессии импорта" (таблица HB_IS_PROCESS_LOG_EVENT) */
    public static final String KIND_IMPORT_SESSION_CONTENT_PROCESS_LOG_EVENT_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "KindImportSessionContentProcessLogEvent";
    /** Имя сущности "Классификатор уровней подразделений" (таблица DEP_DEPTLEVEL) */
    public static final String KIND_IMPORT_SESSION_DEPARTMENT_LEVEL_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "KindImportSessionDepartmentLevel";
    /** Имя сущности "Классификатор исключений по кодам подразделений при обработке исходных файлов" (таблица HB_IS_FILEEXRULE) */
    public static final String KIND_IMPORT_SESSION_DEPARTMENT_FILE_EXCEPTION_RULE_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "KindImportSessionDepartmentFileExceptionRule";

    /** Имя сущности "Запись протокола обработки содержимого сессии импорта" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_CONTENT_PROCESS_LOG_ENTRY_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionContentProcessLogEntry";

    /** Имя сущности "Событие обработки - Обработка записи" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_TO_PROCESSING_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventToProcessing";
    /** Имя сущности "Событие обработки - Создано новое подразделение" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_DEPARTMENT_CREATED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventDepartmentCreated";
    /** Имя сущности "Событие обработки - Строка обработана, изменений не внесено" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_NO_CHANGES_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventNoChanges";
    /** Имя сущности "Событие обработки - Создана учетная запись КМ" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_MANAGER_CREATED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventManagerCreated";
    /** Имя сущности "Событие обработки - Создана связь КМ-ВСП" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_MANAGER_TO_DEPARTMENT_CREATED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventManagerToDepartmentCreated";
    /** Имя сущности "Событие обработки - Создана связь ВСП-договор" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_DEPARTMENT_TO_CONTRACT_CREATED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventDepartmentToContractCreated";
    /** Имя сущности "Событие обработки - Переопределена связь КМ-ВСП" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_MANAGER_TO_DEPARTMENT_CHANGED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventManagerToDepartmentChanged";
    /** Имя сущности "Событие обработки - Создана связь КМ-договор" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_MANAGER_TO_CONTRACT_CREATED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventManagerToContractCreated";
    /** Имя сущности "Событие обработки - Переопределена связь КМ-договор" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_MANAGER_TO_CONTRACT_CHANGED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventManagerToContractChanged";
    /** Имя сущности "Событие обработки - Учётная запись не найдена" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_MANAGER_NOT_FOUND_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventManagerNotFound";
    /** Имя сущности "Событие обработки - Договор не найден" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_CONTRACT_NOT_FOUND_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventContractNotFound";
    /** Имя сущности "Событие обработки - ВСП не найдено" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_DEPARTMENT_NOT_FOUND_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventDepartmentNotFound";
    /** Имя сущности "Событие обработки - УЗ заблокирована" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_MANAGER_BLOCKED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventManagerBlocked";
    /** Имя сущности "Событие обработки - ВСП заблокировано" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_DEPARTMENT_BLOCKED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventDepartmentBlocked";
    /** Имя сущности "Событие обработки - Ошибка" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_UNKNOWN_ERROR_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventUnknownError";
    /** Имя сущности "Событие обработки - Создание связи между подразделениями" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_DEPARTMENT_TO_DEPARTMENT_CREATED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventDepartmentToDepartmentCreated";
    /** Имя сущности "Событие обработки - Создана связь Группа-договор" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_GROUP_TO_CONTRACT_CREATED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventGroupToContractCreated";
    /** Имя сущности "Событие обработки - Переопределена связь ВСП-договор" (таблица B2B_IS_CNT_PROCESS_LOG) */
    public static final String IMPORT_SESSION_EVENT_DEPARTMENT_TO_CONTRACT_CHANGED_ENTITY_NAME = DCT_MODULE_PREFIX_IMPORTS + "ImportSessionEventDepartmentToContractChanged";

    public static final Map<String, String> DCT_MODULE_PREFIX_MAP = new HashMap<String, String>() {
        {
            put("KindContact", DCT_MODULE_PREFIX_CRM);
            put("KindDocument", DCT_MODULE_PREFIX_CRM);
            put("KindAddress", DCT_MODULE_PREFIX_CRM);
            put("KindClientProperty", DCT_MODULE_PREFIX_CRM);
            put("KindAgreementClientProfile", DCT_MODULE_PREFIX_CRM);
            put("KindContractMember", DCT_MODULE_PREFIX_CRM);
            put("KindEventClientProfile", DCT_MODULE_PREFIX_CRM);
            put("KindShareRole", DCT_MODULE_PREFIX_CRM);
            put("KindStatus", DCT_MODULE_PREFIX_CRM);
            put("KindChangeReason", DCT_MODULE_PREFIX_TERMINATION);
            put("KindKMImport", DCT_MODULE_PREFIX_SYSTEM);
        }
    };
}
