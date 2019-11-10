/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.rest.dictionaryHibernate.common;

/**
 *
 * @author mmamaev
 */
public interface DictionaryConstants {

    // ID устройства
    public static final String IDDEVICE_WEB = "web";

    // префиксы для имен сущностей (новый хибернейт)
    public static final String DCT_MODULE_PREFIX_CRM = "com.bivgroup.crm.";
    public static final String DCT_MODULE_PREFIX_TERMINATION = "com.bivgroup.termination.";
    public static final String DCT_MODULE_PREFIX_LOSS = "com.bivgroup.loss.";
    public static final String DCT_MODULE_PREFIX_MESSAGES = "com.bivgroup.messages.";
    public static final String DCT_MODULE_PREFIX_SYSTEM = "com.bivgroup.system.";

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
    /** Имя сущности 'Пользовательское соглашение' (таблица SD_CLIENTPROFILE_AGREEMENT) */
    public static final String CLIENT_PROFILE_AGREEMENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientProfileAgreement";
    /** Имя сущности 'Событие акаунта клиента' (таблица SD_CLIENTPROFILE_EVENT) */
    public static final String CLIENT_PROFILE_EVENT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "ClientProfileEvent";
    /** Имя сущности 'Уведомление для профиля' (таблица CDM_CLIENTPROFILE_NOTIFICATION_) */
    public static final String CLIENT_PROFILE_EVENT_NOTIFICATION = DCT_MODULE_PREFIX_CRM + "ClientProfileNotification";
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
    public static final String KIND_AGREEMENT_CLIENT_PROFILE_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindAgreementClientProfile";
    /** Имя сущности 'Классификатор причин изменения договора страхования' (таблица HB_KINDCHANGEREASON) */
    public static final String KIND_CHANGE_REASON_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "KindChangeReason";
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
    /** Имя сущности 'Причина изменения договора страхования (управление опцией)' (таблица PD_REASONCHANGE_O) */
    public static final String REASON_CHANGE_PPOONLINE_ACT_ENTITY_NAME = DCT_MODULE_PREFIX_TERMINATION + "ReasonChange_PPOOnlineActivation";
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
    /** Имя сущности 'Система переходов' (таблица CORE_SM_TRANSITION) */
    public static final String SM_TRANSITION_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "SMTransition";
    /** Имя сущности 'Аккаунт клиента' (таблица CORE_SM_TYPE) */
    public static final String SM_TYPE_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "SMType";
    /** Имя сущности 'Аккаунт клиента' (таблица CORE_SM_STATE) */
    public static final String SM_STATE_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "SMState";
    /** Имя сущности 'Аккаунт клиента' (таблица HB_KINDACCOUNT) */
    public static final String KIND_ACCOUNT_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindAccount";

    /* */
    public static final String KIND_COUNTRY_ENTITY_NAME = DCT_MODULE_PREFIX_CRM + "KindCountry";

    /** Имя сущности '...' (таблица ...) */
    // todo: указать имя сущности и таблицу
    public static final String LOSS_EVENT_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "LossEvent";
    /** Имя сущности '...' (таблица ...) */
    // todo: указать имя сущности и таблицу
    public static final String LOSS_DAMAGE_CATEGORY_EVENT_ENTITY_NAME = DCT_MODULE_PREFIX_LOSS + "LossDamageCategory";

    /** Имя сущности 'Чат' (таблица SD_CHAT) */
    public static final String MESSAGES_CHAT_ENTITY_NAME = DCT_MODULE_PREFIX_MESSAGES + "Chat";
    /** Имя сущности 'Сообщение' (таблица SD_MESSAGE) */
    public static final String MESSAGES_MESSAGE_ENTITY_NAME = DCT_MODULE_PREFIX_MESSAGES + "Message";
    /** Имя сущности 'Канал сообщения' (таблица HD_MESSAGECHANNEL) */
    public static final String MESSAGES_MESSAGE_CHANNEL_ENTITY_NAME = DCT_MODULE_PREFIX_MESSAGES + "MessageChannel";
    /** Имя сущности 'Корреспондент сообщения' (таблица SD_MESSAGE_CORRESPONDENT) */
    public static final String MESSAGES_MESSAGE_CORRESPONDENT_ENTITY_NAME = DCT_MODULE_PREFIX_MESSAGES + "MessageCorrespondent";
    /** Имя сущности 'Получатель сообщения' (таблица SD_MESSAGE_RECIPIENT) */
    public static final String MESSAGES_MESSAGE_RECIPIENT_EVENT_ENTITY_NAME = DCT_MODULE_PREFIX_MESSAGES + "MessageRecipient";
    /** Имя сущности 'Тип импорта' (таблица HB_KINDKMIMPORT)*/
    public static final String KIND_KM__IMPORT_ENTITY_NAME = DCT_MODULE_PREFIX_SYSTEM + "KindKMImport";

}
