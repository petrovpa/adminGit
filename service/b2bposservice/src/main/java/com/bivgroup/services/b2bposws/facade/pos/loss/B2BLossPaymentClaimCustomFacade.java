package com.bivgroup.services.b2bposws.facade.pos.loss;

import com.bivgroup.services.b2bposws.facade.B2BDictionaryBaseFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import com.bivgroup.services.b2bposws.system.files.FileWriter;
import org.apache.log4j.Logger;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

import java.io.File;
import java.util.*;

import static com.bivgroup.services.b2bposws.facade.pos.loss.B2BLossNoticeCustomFacade.LOSS_NOTICE_ID_PARAMNAME;
import static com.bivgroup.services.b2bposws.system.files.BinFileType.LOSS_PAYMENT_CLAIM;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

@BOName("B2BLossPaymentClaimCustom")
public class B2BLossPaymentClaimCustomFacade extends B2BDictionaryBaseFacade implements FileWriter {

    protected static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    protected static final String THIS_SERVICE_NAME = B2BPOSWS_SERVICE_NAME;

    private final Logger logger = Logger.getLogger(this.getClass());

    private static final String EVENT_MAP_PARAMNAME = "EVENTMAP";
    private static final String APPLICANT_MAP_PARAMNAME = "APPLICANTMAP";
    private static final String BENEFICIARY_MAP_PARAMNAME = "BENEFMAP";
    private static final String DCT_MAP_PARAMNAME_PREFIX = "DCT_";
    private static final String DCT_APPLICANT_MAP_PARAMNAME = DCT_MAP_PARAMNAME_PREFIX + APPLICANT_MAP_PARAMNAME;
    private static final String DCT_BENEFICIARY_MAP_PARAMNAME = DCT_MAP_PARAMNAME_PREFIX + BENEFICIARY_MAP_PARAMNAME;
    private static final String IS_APPLICANT_BENEFICIARY_PARAMNAME = "isApplicantBeneficiary";

    private static final String LOSS_NOTICE_ID_PARAMNAME = "lossNoticeId";
    private static final String LOSS_NOTICE_ATTACHMENT_FACADE_NAME = "B2BLossNoticeAttachment";


    // Тип получателя - REPORTDATA.RECIPIENTTYPESTR
    private static final String RECIPIENT_TYPE_STR_PARAMNAME = "RECIPIENTTYPESTR";
    // Тип получателя: Выгодоприобретатель = BENEF
    private static final String RECIPIENT_TYPE_STR_BENEFICIARY = "BENEF";
    // Тип получателя: Представитель выг-ля = REPRESENT
    private static final String RECIPIENT_TYPE_STR_REPRESENTATIVE = "REPRESENT";

    // параметры и мапы, передаваемые напрямую с интерфейса
    private static final String[] DIRECT_DATA_KEYS = {
            // Застрахованный
            "INSUREDMAP",
            // Кредитный договор
            "CREDITCONTRNUM", "CREDITCONTRDATESTR",
            // Мед учреждение
            "MEDNAME", "MEDADDRESS",
            // Банковские реквизиты
            "BANKMAP",
            // Ребенок
            "CHILDMAP"
    };

    // тексты ошибок
    private static final String DEFAULT_DATA_PROVIDER_ERROR_MSG = "Не удалось подготовить данные для формирования заявления на выплату!";
    private static final String DEFAULT_CLAIM_PRINT_ERROR_MSG = "Не удалось сформировать заявление на выплату!";
    private static final String DEFAULT_CLAIM_NO_TEMPLATE_ERROR_MSG = "Не удалось выбрать шаблон для формирования заявление на выплату!";
    private static final String DEFAULT_CLAIM_BIN_FILE_ERROR_MSG = "Не удалось прикрепить сформированное заявление на выплату к уведомлению о страховом событии!";

    /**
     * Имя параметра в CORE_SETTINGS, хранящего относительный путь (включая имя файла) до шаблона заявления на выплату по страховому событию
     */
    private static final String LOSS_PAYMENT_CLAIM_REPORT_TEMPLATE_FILENAME_SETTING_NAME = "b2bLossPaymentClaimReportTemplateFilename";
    /**
     * Относительный путь (включая имя файла) до шаблона заявления на выплату по страховому событию по умолчанию
     */
    private static final String LOSS_PAYMENT_CLAIM_REPORT_TEMPLATE_FILENAME_DEFAULT = "LOSSES" + File.separator + "LOSSES02" + File.separator + "paymentClaim.odt";

    private static final String REG_EXP_FOREIGN_NAME = "[A-Za-zА-Яа-яЁё -]+"; // ФТ: "буквы латинского алфавита или кириллицы, пробел и тире"
    private static final String REG_EXP_FOREIGN_NAME_DESCRIPTION = "буквы латинского алфавита или кириллицы, пробел и тире";
    private static final String REG_EXP_RUSSIAN_NAME = "[А-Яа-яЁё -]+"; // ФТ: "буквы кириллицы, пробел и тире"
    private static final String REG_EXP_RUSSIAN_NAME_DESCRIPTION = "буквы кириллицы, пробел и тире";

    protected List<Map<String, Object>> resolveClientDocumentList(List<Map<String, Object>> documentList, String citizenship) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> item;
        // системное наименование типа требуемого документа
        String docTypeSysName = citizenship.equals("1") ? "ForeignPassport" : "PassportRF";
        // системное наименование типа противоположного документа
        String oppositeDocTypeSysName = citizenship.equals("1") ? "PassportRF" : "ForeignPassport";
        if (documentList != null && !documentList.isEmpty()) {
            Map<String, Map<String, Object>> clientItemByType = formationMapMapByList(documentList);
            String itemTypeSysName, itemTypeName;
            Map<String, Object> clientItem, itemType;
            boolean isDocTypeExist = false; // флаг, сообщающий о том найденны
            Map<String, Object> copyDoc = new HashMap<>(); // мапа для копирования противоположного документа
            for (Map.Entry<String, Map<String, Object>> clientItemEntry : clientItemByType.entrySet()) {
                itemTypeSysName = clientItemEntry.getKey();
                if (itemTypeSysName.equals(docTypeSysName)) {
                    isDocTypeExist = true;
                    }
                clientItem = clientItemEntry.getValue();
                item = new HashMap<>();
                item.putAll(clientItem);
                itemType = getMapParam(clientItem, "typeId_EN");
                itemTypeName = getStringParam(itemType, "name");
                // документ
                item.put("DOCTYPESYSNAME", itemTypeSysName);
                item.put("DOCTYPENAME", itemTypeName);
                item.put("DOCNUMBER", clientItem.get("no"));
                item.put("DOCSERIES", clientItem.get("series"));
                item.put("ISSUEDATE", getDateDctValue(clientItem, "dateOfIssue"));
                item.put("ISSUEDBY", clientItem.get("authority"));
                item.put("ISSUERCODE", clientItem.get("issuerCode"));
                // todo: знаки вопроса изменить на реализованные в интерфейсе наименования полей
                item.put("VALIDFROMDATE", clientItem.get("dateStayFrom")); // 'Срок пребывания с'
                item.put("VALIDTODATE", clientItem.get("dateStayUntil")); // 'Срок пребывания по'
                if (!isDocTypeExist && itemTypeSysName.equals(oppositeDocTypeSysName)) {
                    copyDoc.putAll(item);
                }
                resultList.add(item);
            }
            // если требуемый согласно гржданству документ отсутствует, тогда
            // копируем имеющийся и подменяем ему DOCTYPESYSNAME
            // для того чтобы вывелось в формуле
            if (!isDocTypeExist) {
                copyDoc.put("DOCTYPESYSNAME", docTypeSysName);
                resultList.add(copyDoc);
            }
        } else {
            // если список документов отсутствует то создадим один пустой документ по гражданству
            item = new HashMap<>();
            String docTypeName = citizenship.equals("1") ? "Паспорт иностранного гражданина"
                    : "Паспорт гражданина РФ";
            item.put("DOCTYPESYSNAME", docTypeSysName);
            item.put("DOCTYPENAME", docTypeName);
            resultList.add(item);
        }
        return resultList;
    }

    protected List<Map<String, Object>> resolveClientAddressList(List<Map<String, Object>> addressList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (addressList != null) {
            Map<String, Map<String, Object>> clientItemByType = formationMapMapByList(addressList);
            String itemTypeSysName, itemTypeName;
            Map<String, Object> clientItem, item, itemType;
            for (Map.Entry<String, Map<String, Object>> clientItemEntry : clientItemByType.entrySet()) {
                itemTypeSysName = clientItemEntry.getKey();
                clientItem = clientItemEntry.getValue();
                item = new HashMap<>();
                item.putAll(clientItem);
                itemType = getMapParam(clientItem, "typeId_EN");
                itemTypeName = getStringParam(itemType, "name");
                // адрес
                item.put("ADDRESSTYPESYSNAME", itemTypeSysName);
                item.put("ADDRESSTYPENAME", itemTypeName);
                item.put("CITY", clientItem.get("city"));
                item.put("REGION", clientItem.get("region"));
                item.put("STREET", clientItem.get("street"));
                item.put("HOUSE", clientItem.get("house"));
                item.put("FLAT", clientItem.get("flat"));
                item.put("ADDRESSTEXT1", clientItem.get("address"));
                item.put("ADDRESSTEXT2", clientItem.get("address"));
                item.put("ADDRESSTEXT3", clientItem.get("address2"));
                item.put("POSTALCODE", clientItem.get("postcode"));
                resultList.add(item);
            }
        }
        return resultList;
    }

    protected List<Map<String, Object>> resolveClientContactList(List<Map<String, Object>> contactList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (contactList != null) {
            Map<String, Map<String, Object>> clientItemByType = formationMapMapByList(contactList);
            String itemTypeSysName, itemTypeName;
            Map<String, Object> clientItem, item, itemType;
            for (Map.Entry<String, Map<String, Object>> clientItemEntry : clientItemByType.entrySet()) {
                itemTypeSysName = clientItemEntry.getKey();
                clientItem = clientItemEntry.getValue();
                item = new HashMap<>();
                item.putAll(clientItem);
                itemType = getMapParam(clientItem, "typeId_EN");
                itemTypeName = getStringParam(itemType, "name");
                // контакт
                item.put("CONTACTTYPESYSNAME", itemTypeSysName);
                item.put("CONTACTTYPENAME", itemTypeName);
                item.put("VALUE", clientItem.get("value"));
                resultList.add(item);
            }
        }
        return resultList;
    }

    private Map<String, Map<String, Object>> formationMapMapByList(List<Map<String, Object>> list) {
        Map<String, Map<String, Object>> clientItemByType = new HashMap<>();
        for (Map<String, Object> clientItem : list) {
            Map<String, Object> itemType = getMapParam(clientItem, "typeId_EN");
            String itemTypeSysName = getStringParam(itemType, "sysname");
            if (!itemTypeSysName.isEmpty()) {
                Long isPrimary = getLongParam(clientItem, "isPrimary");
                if ((BOOLEAN_FLAG_LONG_VALUE_TRUE.equals(isPrimary)) || (clientItemByType.get(itemTypeSysName) == null)) {
                    // если по данному типу не было других элементов или если проверямый элемент имеет признак isPrimary - выбор элемента как используемого для данного типа
                    clientItemByType.put(itemTypeSysName, clientItem);
                }
            }
        }
        return clientItemByType;
    }

    private Object getDateDctValue(Map<String, Object> sourceMap, String dateKeyName) {
        Object dateObj = null;
        if (sourceMap != null) {
            dateObj = sourceMap.get(dateKeyName);
            if (dateObj == null) {
                dateObj = sourceMap.get(dateKeyName + "$date");
            }
        }
        return dateObj;
    }

    private void updateReportPersonMapByClientMap(Map<String, Object> reportPerson, Map<String, Object> client,
                                                  String login, String password) throws Exception {
        if ((reportPerson == null) || (client == null)) {
            return;
        }
        // фамилия заявителя
        reportPerson.put("LASTNAME", client.get("surname"));
        // имя заявителя
        reportPerson.put("FIRSTNAME", client.get("name"));
        // отчество заявителя
        reportPerson.put("MIDDLENAME", client.get("patronymic"));
        // фамилия и. о. заявителя
        Map<String, Object> namesMap = getClientNamesMap(client);
        reportPerson.put("BRIEFNAME", namesMap.get("fullNameAbbr"));
        // дата рождения заявителя
        reportPerson.put("BIRTHDATE", getDateDctValue(client, "dateOfBirth"));
        // Контактные данные заявителя (мобильный, дополнительный, email); аналогично той структуре, которая лежит в PARTICIPANTMAP
        // Используемые системные наименования: Email = PersonalEmail, Мобильный = MobilePhone, Дополнительный = FactAddressPhone"
        List<Map<String, Object>> clientContactList = getOrCreateListParam(client, "contacts");
        List<Map<String, Object>> contactList = resolveClientContactList(clientContactList);
        reportPerson.put("contactList", contactList);
        // Адреса заявителя; аналогично той структуре, которая лежит в PARTICIPANTMAP
        List<Map<String, Object>> clientAddressList = getOrCreateListParam(client, "addresses");
        List<Map<String, Object>> addressList = resolveClientAddressList(clientAddressList);
        reportPerson.put("addressList", addressList);
        // гражданство заявителя
        Map<String, Object> country = getMapParam(client, "countryId_EN");
        if (country == null) {
            Long countryId = getLongParamLogged(client, "countryId");
            if (countryId == null) {
                countryId = 1L; // todo: в константы (а мб такая коснтанта уже есть, нужно найти)
            }
            try {
                country = dctFindById(KIND_COUNTRY_ENTITY_NAME, countryId);
            } catch (Exception ex) {
                logger.error(String.format(
                        "Unable to get country info by dctFindById from '%s' with id = %d - exception was thrown:",
                        KIND_COUNTRY_ENTITY_NAME, countryId
                ), ex);
            }
        }
        String citizenshipStr = getStringParamLogged(country, "countryName");
        reportPerson.put("CITIZENSHIPSTR", citizenshipStr);
        // по умолчанию будем считать гражданство РФ
        String citizenship = "0";
        String countryCode = "RUS";
        String alphaCode3 = getStringParamLogged(country, "alphaCode3"); // RUS - Российская Федерация и т.д.
        if (!alphaCode3.isEmpty()) {
            countryCode = alphaCode3;
        }
        // если после запроса по countryId alphaCode3 не пусто
        // и не равно русскому, тогда присваиваем 1
        if (!countryCode.isEmpty() && !"RUS".equalsIgnoreCase(countryCode)) {
            citizenship = "1";
        }
        reportPerson.put("CITIZENSHIP", citizenship);
        reportPerson.put("COUNTRYCODE", countryCode);

        // реквизиты документа заявителя (в т.ч. миграционная карта и разрешение на пребывание); аналогично той структуре, которая лежит в PARTICIPANTMAP
        List<Map<String, Object>> clientDocumentList = getOrCreateListParam(client, "documents");
        List<Map<String, Object>> documentList = resolveClientDocumentList(clientDocumentList, citizenship);
        reportPerson.put("documentList", documentList);

        Long kinshipId = getLongParam(client, "kinshipId");
        if (kinshipId != null) {
            reportPerson.put("RELATIONSHIP", getKinshipName(kinshipId, login, password));
        }
        // СНИЛС заявителя - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("SNILS", "");
        // Страна рождения заявителя - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("BIRTHCOUNTRY", "");
        // Место рождения заявителя - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("BIRTHPLACE", "");
        // ИНН заявителя - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("INN", client.get("inn"));
        // Вид на жительство заявителя Флаг - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("RESIDENCESTR", "");
        // Статус налогового резидента США заявителя - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("RESIDENTUSA", "");
        // Статус налогового резидента другой страны заявителя - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("RESIDENTOTHER", "");
        // ИНН США заявителя - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("INNUSA", "");
        // ИНН Другая страна заявителя - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("INNOTHER", "");
        // Реквизиты документа, подтверждающего полномочия (для Представителей) - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("REPRESENTDOCINFO", "");
        // Тип заявителя (лично, представитель, иное) - нет в профиле (согласно ФТ из #6599)
        //reportPerson.put("TYPESTR", "");
    }

    private String getKinshipName(Long kinshipId, String login, String password) throws Exception {
        Map<String, Object> kinshipQuerryParams = new HashMap<>();
        kinshipQuerryParams.put("HANDBOOKNAME", "B2B.Life.Kinship");
        kinshipQuerryParams.put(RETURN_LIST_ONLY, true);
        Map<String, Object> hbParams = new HashMap<String, Object>();
        hbParams.put("hid", kinshipId);
        kinshipQuerryParams.put("HANDBOOKDATAPARAMS", hbParams);
        List<Map<String, Object>> kinshipList = this.callServiceAndGetListFromResultMap(Constants.B2BPOSWS,
                "dsB2BHandbookDataBrowseByHBName", kinshipQuerryParams, login, password);
        String result = "";
        if (!kinshipList.isEmpty()) {
            result = getStringParam(kinshipList.get(0), "name");
        }
        return result;
    }

    @WsMethod(requiredParams = {LOSS_NOTICE_ID_PARAMNAME, IS_APPLICANT_BENEFICIARY_PARAMNAME})
    public Map<String, Object> dsB2BLossPaymentClaimDataProvider(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BLossPaymentClaimDataProvider begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        boolean isCallFromGate = isCallFromGate(params);
        String errorDefault = DEFAULT_DATA_PROVIDER_ERROR_MSG;
        String error = "";
        // мапа основного результата работы провайдера - REPORTDATA
        Map<String, Object> reportData = new HashMap<String, Object>();
        // Событие - REPORTDATA.EVENTMAP; часть данных может быть передана с интерфейса
        Map<String, Object> eventMap = getOrCreateMapParam(params, EVENT_MAP_PARAMNAME);
        reportData.put(EVENT_MAP_PARAMNAME, eventMap);
        // Заявитель - REPORTDATA.APPLICANTMAP; часть данных может быть передана с интерфейса
        Map<String, Object> applicantMap = getOrCreateMapParam(params, APPLICANT_MAP_PARAMNAME);
        reportData.put(APPLICANT_MAP_PARAMNAME, applicantMap);
        // Заявитель - часть данных, которая может быть передана с интерфейса в синтаксисе словарной системы
        Map<String, Object> applicantMapDct = getOrCreateMapParam(params, DCT_APPLICANT_MAP_PARAMNAME);
        // Выгодоприобретатель - REPORTDATA.BENEFMAP; часть данных может быть передана с интерфейса
        Map<String, Object> benefMap = getOrCreateMapParam(params, BENEFICIARY_MAP_PARAMNAME);
        reportData.put(BENEFICIARY_MAP_PARAMNAME, benefMap);
        // Выгодоприобретатель - часть данных, которая может быть передана с интерфейса в синтаксисе словарной системы
        Map<String, Object> benefMapDct = getOrCreateMapParam(params, DCT_BENEFICIARY_MAP_PARAMNAME);
        // параметры и мапы, передаваемые напрямую с интерфейса
        for (String dataKey : DIRECT_DATA_KEYS) {
            reportData.put(dataKey, params.get(dataKey));
        }
        //
        Long lossNoticeId = getLongParamLogged(params, LOSS_NOTICE_ID_PARAMNAME);
        //
        Map<String, Object> lossNoticeParams = new HashMap<String, Object>();
        lossNoticeParams.put(LOSS_NOTICE_ID_PARAMNAME, lossNoticeId);
        lossNoticeParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> lossNotice = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BLossNoticeLoad", lossNoticeParams, login, password);
        if (!isCallResultOKAndContainsLongValue(lossNotice, LOSS_NOTICE_ID_PARAMNAME, lossNoticeId)) {
            error = "Не удалось загрузить сведения уведомления о страховом событии!";
        } else {
            // Дата заявления
            reportData.put("CLAIMDATE", lossNotice.get("createDate"));
            Map<String, Object> insEvent = (Map<String, Object>) lossNotice.get("insEventId_EN");
            if (insEvent != null) {
                // Событие - Тип события
                String insEventSysName = getStringParamLogged(insEvent, "sysname");
                eventMap.put("EVENTTYPESYSNAME", insEventSysName);
                String insEventType = insEventSysName.replaceAll("^e", "").replaceAll("gr$", "").toUpperCase();
                eventMap.put("EVENTTYPESTR", insEventType);
            }
            // Событие - Дата события
            eventMap.put("EVENTDATE", lossNotice.get("eventDate"));
            // Событие - Описание события - нет в lossEvent
            //eventMap.put("DESCRIPTION", "");
        }
        Long contractId = null;
        if (error.isEmpty()) {
            contractId = getLongParamLogged(lossNotice, "contrId");
            if (contractId != null) {
                Map<String, Object> contractParams = new HashMap<String, Object>();
                contractParams.put("CONTRID", contractId);
                contractParams.put(RETURN_AS_HASH_MAP, true);
                Map<String, Object> contract = this.callServiceLogged(B2BPOSWS_SERVICE_NAME, "dsB2BContractBrowseListByParam", contractParams, login, password);
                if (!isCallResultOKAndContainsLongValue(contract, "CONTRID", contractId)) {
                    error = "Не удалось загрузить сведения договора страхования, указанного в уведомлении о страховом событии!";
                } else {
                    reportData.put("CONTRPOLSER", contract.get("CONTRPOLSER"));
                    reportData.put("CONTRPOLNUM", contract.get("CONTRPOLNUM"));
                    reportData.put("DOCUMENTDATE", contract.get("DOCUMENTDATE"));
                }
            }
        }
        if (error.isEmpty()) {
            // если не указан clientProfileId, то метод вызван из ЛК без аутентификации - следовательно заявление формирует представитель
            // если указан clientProfileId, то метод вызван из ЛК с аутентификацией - следовательно заявление формирует заявитель
            Long clientProfileId = getLongParamLogged(params, "clientProfileId");
            // Тип получателя - REPORTDATA.RECIPIENTTYPESTR
            String recipientTypeStr = (clientProfileId == null) ? RECIPIENT_TYPE_STR_REPRESENTATIVE : RECIPIENT_TYPE_STR_BENEFICIARY;
            reportData.put(RECIPIENT_TYPE_STR_PARAMNAME, recipientTypeStr);
            //
            if (clientProfileId != null) {
                // указан clientProfileId поскольку метод вызван из ЛК с аутентификацией - данные заявителя нужно получить из профиля
                Map<String, Object> clientProfile = dctFindById(CLIENT_PROFILE_ENTITY_NAME, clientProfileId);
                Map<String, Object> client = getOrCreateMapParam(clientProfile, "clientId_EN");
                loggerDebugPretty(logger, CLIENT_PROFILE_ENTITY_NAME + ".clientId_EN", client); // спам
                updateReportPersonMapByClientMap(applicantMap, client, login, password);
                applicantMap.put("TYPESTR", "PERSONAL");
            } else {
                updateReportPersonMapByClientMap(applicantMap, applicantMapDct, login, password);
                applicantMap.put("TYPESTR", RECIPIENT_TYPE_STR_REPRESENTATIVE);
            }
            boolean isApplicantBeneficiary = getBooleanParamLogged(params, IS_APPLICANT_BENEFICIARY_PARAMNAME, false);
            if (isApplicantBeneficiary) {
                // 'Страхователь является Выгодоприобретателем' или 'Заявитель является Выгодоприобретателем'
                benefMap.putAll(applicantMap);
                // также в мапу выгодоприобретателя следует добавить и сам признак, что он совпадает с заявителем/страхователем
                benefMap.put(IS_APPLICANT_BENEFICIARY_PARAMNAME, isApplicantBeneficiary);
            } else {
                updateReportPersonMapByClientMap(benefMap, benefMapDct,  login, password);
            }
        }
        // Убираем индекс из начала ADDRESSTEXT1, ADDRESSTEXT2, ADDRESSTEXT3
        applicantMap.put("addressList", checkPostalCodeInAddressText(getListParam(applicantMap, "addressList")));

        if (error.isEmpty()) {
            // генерация строковых представлений для всех дат
            genDateStrs(reportData);
        }
        // формирование результата
        Map<String, Object> result = new HashMap<String, Object>();
        if (error.isEmpty()) {
            // формирование результата
            result.put("REPORTDATA", reportData);
        } else {
            result.put(ERROR, error);
        }
        loggerDebugPretty(logger, "dsB2BLossPaymentClaimDataProvider result", result);
        logger.debug("dsB2BLossPaymentClaimDataProvider end");
        return result;
    }

    @WsMethod(requiredParams = {LOSS_NOTICE_ID_PARAMNAME, IS_APPLICANT_BENEFICIARY_PARAMNAME})
    public Map<String, Object> dsB2BLossPaymentClaimPrint(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BLossPaymentClaimPrint begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        //boolean isCallFromGate = isCallFromGate(params);
        String error = "";
        // промежуточный результат, в основном для протоколирования
        Map<String, Object> subResult = new HashMap<String, Object>();

        // данные подготовленные для отчета провайдером
        Map<String, Object> reportData = null;
        // вызов провайдера данных для заявления
        Map<String, Object> dataProviderParams = new HashMap<String, Object>();
        dataProviderParams.putAll(params);
        dataProviderParams.put(RETURN_AS_HASH_MAP, true);
        // todo: возможно, получать имя метода провайдера данных из БД
        // (однако, делать это следует по системному наименованию или самого провайдера или настройки из CORE_SETTINGS, а это получение одной строковой константы по другой строковой константе)
        String dataProviderMethodName = "dsB2BLossPaymentClaimDataProvider";
        Map<String, Object> dataProviderResult = this.callService(B2BPOSWS_SERVICE_NAME, dataProviderMethodName, dataProviderParams, login, password);
        error = getStringParamLogged(dataProviderResult, ERROR);
        if (isCallResultOKAndContains(dataProviderResult, "REPORTDATA") && (error.isEmpty())) {
            // данные подготовленные для отчета провайдером
            reportData = getMapParam(dataProviderResult, "REPORTDATA");
            subResult.put("REPORTDATA", reportData);
        }
        if ((reportData == null) && (error.isEmpty())) {
            error = DEFAULT_DATA_PROVIDER_ERROR_MSG;
        }

        String templateName = "";
        if (error.isEmpty()) {
            // получение из CORE_SETTINGS относительного пути (включая имя файла) до шаблона заявления на выплату по страховому событию
            templateName = getCoreSettingBySysName(LOSS_PAYMENT_CLAIM_REPORT_TEMPLATE_FILENAME_SETTING_NAME, login, password);
            subResult.put(LOSS_PAYMENT_CLAIM_REPORT_TEMPLATE_FILENAME_SETTING_NAME, templateName);
            if (templateName.isEmpty()) {
                error = DEFAULT_CLAIM_NO_TEMPLATE_ERROR_MSG;
            }
        }

        // валидация подготовленных для отчета данных
        if (error.isEmpty()) {
            error = validatePaymentClaimReportData(reportData, login, password);
        }

        String reportName = "";
        if (error.isEmpty()) {
            // по умолчанию считается, что произошла ошибка
            error = DEFAULT_CLAIM_PRINT_ERROR_MSG;
            Map<String, Object> reportParams = new HashMap<String, Object>();
            //reportParams.put("REPORTFORMATS", ".pdf"); // необязательный параметр для dsLibreOfficeReportCreate, pdf будет использован по умолчанию
            //reportParams.put("reportName", "reportName"); // необязательный параметр, в dsLibreOfficeReportCreate по умолчанию будет использовано UUID.randomUUID().toString()
            reportParams.put("templateName", templateName);
            reportParams.put("REPORTDATA", reportData);
            reportParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> reportPrintResult = this.callServiceLogged(LIBREOFFICEREPORTSWS_SERVICE_NAME, "dsLibreOfficeReportCreate", reportParams, login, password);
            subResult.put("PRINTRES", reportPrintResult);
            if (isCallResultOKAndContains(reportPrintResult, "REPORTDATA")) {
                Map<String, Object> printResultReportData = getMapParam(reportPrintResult, "REPORTDATA");
                reportName = getStringParam(printResultReportData, "reportName");
                if (!reportName.isEmpty()) {
                    // успех
                    error = "";
                }
            }
        }

        Long binFileId = null;
        if (error.isEmpty()) {
            // по умолчанию считается, что произошла ошибка
            error = DEFAULT_CLAIM_BIN_FILE_ERROR_MSG;
            // Сохранение уже существующего файла в SeaweedFS (если требуется); получение подробной информации о файле (всегда)
            Map<String, Object> binFileParams = trySaveReportToSeaweeds(reportName, ".pdf");
            loggerDebugPretty(logger, "binFileParams from trySaveReportToSeaweeds", binFileParams); // спам
            //
            binFileParams.putAll(LOSS_PAYMENT_CLAIM.getFileTypeMap());
            Long lossNoticeId = getLongParamLogged(params, LOSS_NOTICE_ID_PARAMNAME);
            binFileParams.put("OBJID", lossNoticeId);
            binFileParams.put("NOTE", "");
            subResult.put("BINFILEPARAM", binFileParams);
            binFileParams.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> binFileInfoCreateResult = this.callServiceLogged(THIS_SERVICE_NAME, "dsB2BLossNoticeAttachment_BinaryFile_createBinaryFileInfo", binFileParams, login, password);

            Map<String, Object> lossNoticeDoc = new HashMap<String, Object>();
            lossNoticeDoc.put("LOSSNOTICEID", lossNoticeId);
            // константа из sberlifelk/src/app/components/loss-upload-wraper/requaredParams.json
            // todo: переделать на справочник, выбирать ид по системному имени типа документа.
            lossNoticeDoc.put("BINDOCTYPE", "100200");
            if (binFileInfoCreateResult.get("BINFILEID") != null) {
                lossNoticeDoc.put("EXTERNALID", binFileInfoCreateResult.get("BINFILEID"));
            }

            lossNoticeDoc.put(RETURN_AS_HASH_MAP, true);
            Map<String, Object> lossNoticeDocRes = this.callServiceLogged(THIS_SERVICE_NAME, "dsB2BLossNoticeDocCreate", lossNoticeDoc, login, password);
            if (lossNoticeDocRes.get("LOSSNOTICEDOCID") != null) {
                binFileParams.put("OBJID", lossNoticeDocRes.get("LOSSNOTICEDOCID"));

                Map<String, Object> binFileInfoCreateDocResult = this.callServiceLogged(THIS_SERVICE_NAME, "dsB2BLossNoticeDoc_BinaryFile_createBinaryFileInfo", binFileParams, login, password);
            }


            subResult.put("BINFILERES", binFileInfoCreateResult);
            if (isCallResultOKAndContains(binFileInfoCreateResult, "BINFILEID")) {
                // успех
                binFileId = getLongParamLogged(binFileInfoCreateResult, "BINFILEID");
                if (binFileId != null) {
                    error = "";
                }
            }
        }
        // формирование результата
        loggerDebugPretty(logger, "dsB2BLossPaymentClaimPrint sub result", subResult);
        Map<String, Object> result = new HashMap<String, Object>();
        if (error.isEmpty()) {
            //result.put("subResult", subResult); // !только для отладки!
            // формирование результата
            // todo: изменить, когда требуемые параметры уточнит группа разработки интерфейсов (см. гуглодок)
            result.put("REPORTDATA", subResult.get("REPORTDATA"));
            result.put("BINFILEID", binFileId);
        } else {
            result.put(ERROR, error);
            result.put("REPORTDATA", subResult.get("REPORTDATA")); // доп. сведения о собранных для отчета данных, могут быть полезны для уточнения условий ошибка
        }
        loggerDebugPretty(logger, "dsB2BLossPaymentClaimPrint result", result);
        logger.debug("dsB2BLossPaymentClaimPrint end");
        return result;
    }

    /**
     * Метод для работы с LOSSNOTICEDOC
     *
     * @param params
     * @return
     * @throws Exception
     */
    @WsMethod(requiredParams = {LOSS_NOTICE_ID_PARAMNAME})
    public Map<String, Object> dsB2BLossNoticeDocBrowseListByParam(Map<String, Object> params) throws Exception {
        Map<String, Object> lossNoticeDocRes = this.selectQuery("dsB2BLossNoticeDocBrowseListByParamEx", "dsB2BLossNoticeDocBrowseListByParamExCount", params);
        return lossNoticeDocRes;
    }

    @WsMethod(requiredParams = {LOSS_NOTICE_ID_PARAMNAME})
    public Map<String, Object> dsB2BLossPaymentClaimBrowseListByLossNoticeId(Map<String, Object> params) throws Exception {
        logger.debug("dsB2BLossPaymentClaimBrowseListByLossNoticeId begin");
        String login = getStringParam(params, LOGIN);
        String password = getStringParam(params, PASSWORD);
        //boolean isCallFromGate = isCallFromGate(params);
        String error = "";
        // промежуточный результат, в основном для протоколирования
        Map<String, Object> subResult = new HashMap<String, Object>();
        // ИД LossNotice
        Long lossNoticeId = getLongParamLogged(params, LOSS_NOTICE_ID_PARAMNAME);
        //
        Map<String, Object> lossPaymentClaimParams = new HashMap<String, Object>();

        boolean isNeedProcessForUpload = true; // подготавливать пути для загрузки ?

        lossPaymentClaimParams.put("OBJID", lossNoticeId);

        String browseBinFileMethodName = "ds" + LOSS_NOTICE_ATTACHMENT_FACADE_NAME + "_BinaryFile_BinaryFileBrowseListByParam";
        List<Map<String, Object>> lossPaymentClaimList = this.callServiceAndGetListFromResultMapLogged(B2BPOSWS_SERVICE_NAME, browseBinFileMethodName, lossPaymentClaimParams, login, password);
        subResult.put("lossPaymentClaimList", lossPaymentClaimList);
        if (lossPaymentClaimList == null) {
            error = "Не удалось получить сведения о сформированных заявлениях!";
        }

        if (params.get("ISNEEDPROCESSFORUPLOAD") != null) {
            isNeedProcessForUpload = (Boolean) params.get("ISNEEDPROCESSFORUPLOAD");
        }

        if (isNeedProcessForUpload) {
            processDocListForUpload(lossPaymentClaimList, params, login, password);
        }

        // формирование результата
        loggerDebugPretty(logger, "dsB2BLossPaymentClaimPrint sub result", subResult);
        Map<String, Object> result = new HashMap<String, Object>();
        if (error.isEmpty()) {
            result.putAll(subResult); // !только для отладки!
            // формирование результата
            // todo: изменить, когда требуемые параметры уточнит группа разработки интерфейсов (см. гуглодок)
        } else {
            result.put(ERROR, error);
        }
        loggerDebugPretty(logger, "dsB2BLossPaymentClaimPrint result", result);
        logger.debug("dsB2BLossPaymentClaimBrowseListByLossNoticeId end");
        return result;
    }

    private String checkNotEmpty(List<String> errorList, Map<String, Object> sourceMap, String keyName, String error) {
        String valueStr = getStringParamLogged(sourceMap, keyName);
        if (valueStr.isEmpty()) {
            errorList.add(error);
        }
        return valueStr;
    }

    private void checkName(List<String> errorList, String valueStr, Boolean isRussian, String valueDescription, String valueOwnerNameGenitive) {
        if (isRussian != null) {
            String regExp = isRussian ? REG_EXP_RUSSIAN_NAME : REG_EXP_FOREIGN_NAME;
            String formatDescription = isRussian ? REG_EXP_RUSSIAN_NAME_DESCRIPTION : REG_EXP_FOREIGN_NAME_DESCRIPTION;
            checkCustomIfNotEmpty(errorList, valueStr, regExp, valueDescription, valueOwnerNameGenitive, formatDescription);
        }
    }

    private void checkNotEmptyAndName(List<String> errorList, Map<String, Object> sourceMap, String keyName, String emptyErrorPrefix, Boolean isRussian, String valueDescription, String valueOwnerNameGenitive) {
        String valueStr = getStringParamLogged(sourceMap, keyName);
        if (valueStr.isEmpty()) {
            errorList.add(String.format("%s %s", emptyErrorPrefix, valueOwnerNameGenitive));
        } else {
            checkName(errorList, valueStr, isRussian, valueDescription, valueOwnerNameGenitive);
        }
    }

    // Заявитель - валидация
    private void validateApplicant(List<String> errorList, Map<String, Object> reportData) {
        // Заявитель (REPORTDATA.APPLICANTMAP)
        Map<String, Object> applicant = getMapParam(reportData, "APPLICANTMAP");
        // personNameGenitiveSuffix
        String personNameGenitiveSuffix = "заявителя";
        //
        // Заявитель - гражданство (REPORTDATA.APPLICANTMAP.CITIZENSHIPSTR)
        checkNotEmpty(errorList, applicant, "CITIZENSHIPSTR", "Не указано гражданство", personNameGenitiveSuffix);
        // Заявитель - альфа-код страны гражданства (REPORTDATA.APPLICANTMAP.COUNTRYCODE)
        checkNotEmpty(errorList, applicant, "COUNTRYCODE", "Не указан альфа-код страны гражданства", personNameGenitiveSuffix);
        //
        // Заявитель - страна рождения (REPORTDATA.APPLICANTMAP.BIRTHCOUNTRY)
        checkNotEmpty(errorList, applicant, "BIRTHCOUNTRY", "Не указана страна рождения", personNameGenitiveSuffix);
        // Заявитель - место рождения (REPORTDATA.APPLICANTMAP.BIRTHPLACE)
        checkNotEmpty(errorList, applicant, "BIRTHPLACE", "Не указано место рождения", personNameGenitiveSuffix);
        //
        // Заявитель - ИНН (REPORTDATA.APPLICANTMAP.INN)
        checkNumberIfNotEmpty(errorList, applicant, "INN", "о ИНН", personNameGenitiveSuffix, "12");
        // Заявитель - СНИЛС (REPORTDATA.APPLICANTMAP.SNILS)
        /*
        checkCustomIfNotEmpty(errorList, applicant, "SNILS",
                "^[0-9-]{14}", "о СНИЛС", personNameGenitiveSuffix, "только цифры и тире, 14 символов");
        */
        // изменено согласно уточнению клиента относительно наличия пробелов и дефисов в кодах и номерах только на интерфейсе
        checkNumberIfNotEmpty(errorList, applicant, "SNILS", "о СНИЛС", personNameGenitiveSuffix, "11");
        //
        // Заявитель - статус налогового резидента иностранного государства
        validateForeignTaxResidentInfo(errorList, applicant, "заявителя");
    }

    // Выгодоприобретатель - валидация
    private void validateBeneficiary(List<String> errorList, Map<String, Object> reportData) {
        logger.debug("validateBeneficiary...");
        // Выгодоприобретатель (REPORTDATA.BENEFMAP)
        Map<String, Object> beneficiary = getMapParam(reportData, "BENEFMAP");
        // флаг 'Страхователь является Выгодоприобретателем' или 'Заявитель является Выгодоприобретателем'
        boolean isApplicantBeneficiary = getBooleanParamLogged(beneficiary, IS_APPLICANT_BENEFICIARY_PARAMNAME, false);
        // personNameGenitiveSuffix
        String personNameGenitiveSuffix = "выгодоприобретателя";
        //
        // Выгодоприобретатель - гражданство (REPORTDATA.BENEFMAP.CITIZENSHIPSTR)
        checkNotEmpty(errorList, beneficiary, "CITIZENSHIPSTR", "Не указано гражданство", personNameGenitiveSuffix);
        // Выгодоприобретатель - альфа-код страны гражданства (REPORTDATA.BENEFMAP.COUNTRYCODE)
        String countryCode = checkNotEmpty(errorList, beneficiary, "COUNTRYCODE", "Не указан альфа-код страны гражданства", personNameGenitiveSuffix);
        // true - российское гражданство; false - иностранный гражданин; null - гражданство не указано, не определить
        Boolean isRussian = isRussianByCountryCode(countryCode);
        //
        // Выгодоприобретатель - фамилия (REPORTDATA.BENEFMAP.LASTNAME)
        checkNotEmptyAndName(errorList, beneficiary, "LASTNAME",
                "Не указана фамилия",
                isRussian, "о фамилии",
                personNameGenitiveSuffix
        );
        // Выгодоприобретатель - имя (REPORTDATA.BENEFMAP.FIRSTNAME)
        checkNotEmptyAndName(errorList, beneficiary, "FIRSTNAME",
                "Не указано имя",
                isRussian, "о имени",
                personNameGenitiveSuffix
        );
        // Выгодоприобретатель - отчество (REPORTDATA.BENEFMAP.MIDDLENAME)
        checkNotEmptyAndName(errorList, beneficiary, "MIDDLENAME",
                "Не указано отчество",
                isRussian, "об отчестве",
                personNameGenitiveSuffix
        );
        // Выгодоприобретатель - дата рождения (REPORTDATA.BENEFMAP.BIRTHDATESTR)
        checkNotEmpty(errorList, beneficiary, "BIRTHDATESTR", "Не указана дата рождения", personNameGenitiveSuffix);
        //
        // Выгодоприобретатель - страна рождения (REPORTDATA.BENEFMAP.BIRTHCOUNTRY)
        checkNotEmpty(errorList, beneficiary, "BIRTHCOUNTRY", "Не указана страна рождения", personNameGenitiveSuffix);
        // Выгодоприобретатель - место рождения (REPORTDATA.BENEFMAP.BIRTHPLACE)
        checkNotEmpty(errorList, beneficiary, "BIRTHPLACE", "Не указано место рождения", personNameGenitiveSuffix);
        //
        // Выгодоприобретатель - родственная связь (REPORTDATA.BENEFMAP.RELATIONSHIP)
        if (!isApplicantBeneficiary) {
            // по требованию клиента следует пропускать проверку родственной связи если сведения выгодоприобретателя получены из профиля клиента заявителя
            checkNotEmpty(errorList, beneficiary, "RELATIONSHIP", "Не указана родственная связь", personNameGenitiveSuffix);
        }
        // Выгодоприобретатель - ИНН (REPORTDATA.BENEFMAP.INN)
        checkNumberIfNotEmpty(errorList, beneficiary, "INN", "о ИНН", personNameGenitiveSuffix, "12");
        // Выгодоприобретатель - СНИЛС (REPORTDATA.BENEFMAP.SNILS)
        /*
        checkCustomIfNotEmpty(errorList, beneficiary, "SNILS",
                "^[0-9-]{14}", "о СНИЛС", personNameGenitiveSuffix, "только цифры и тире, 14 символов");
        */
        // изменено согласно уточнению клиента относительно наличия пробелов и дефисов в кодах и номерах только на интерфейсе
        checkNumberIfNotEmpty(errorList, beneficiary, "SNILS", "о СНИЛС", personNameGenitiveSuffix, "11");
        //
        // Выгодоприобретатель - контакты
        validateAddresses(errorList, beneficiary, personNameGenitiveSuffix);
        //
        // Выгодоприобретатель - контакты
        validateContacts(errorList, beneficiary, personNameGenitiveSuffix);
        //
        // Выгодоприобретатель - документы (в зависимости от гражданства)
        validateDocuments(errorList, beneficiary, isRussian, personNameGenitiveSuffix);
        //
        // Выгодоприобретатель - статус налогового резидента иностранного государства
        validateForeignTaxResidentInfo(errorList, beneficiary, personNameGenitiveSuffix);
        logger.debug("validateBeneficiary finished.");
    }

    private void validateAddresses(List<String> errorList, Map<String, Object> person, String personNameGenitiveSuffix) {
        List<Map<String, Object>> addressList = getListParam(person, "addressList");
        Map<String, Map<String, Object>> addressMap = getMapByFieldStringValues(addressList, "ADDRESSTYPESYSNAME");
        // RegisterAddress
        Map<String, Object> registerAddress = addressMap.get("RegisterAddress");
        validateRegisterAddress(errorList, registerAddress, personNameGenitiveSuffix);
        // FactAddress
        Map<String, Object> factAddress = addressMap.get("FactAddress");
        validateFactAddress(errorList, factAddress, personNameGenitiveSuffix);
    }

    private void validateRegisterAddress(List<String> errorList, Map<String, Object> registerAddress, String personNameGenitiveSuffix) {
        String addressNameGenitiveSuffix = "адреса регистрации " + personNameGenitiveSuffix;
        validateAddressCommon(errorList, registerAddress, addressNameGenitiveSuffix);
    }

    private void validateFactAddress(List<String> errorList, Map<String, Object> factAddress, String personNameGenitiveSuffix) {
        String addressNameGenitiveSuffix = "фактического адреса " + personNameGenitiveSuffix;
        validateAddressCommon(errorList, factAddress, addressNameGenitiveSuffix);
    }

    private void validateAddressCommon(List<String> errorList, Map<String, Object> address, String addressNameGenitiveSuffix) {
        if (address == null) {
            errorList.add("Не указаны данные " + addressNameGenitiveSuffix);
        } else {
            // проверка отдельных полей согласно ФТ
            checkNotEmpty(errorList, address, "CITY", "Не указан город или населенный пункт", addressNameGenitiveSuffix);
            checkNotEmpty(errorList, address, "REGION", "Не указан регион", addressNameGenitiveSuffix);
            checkNotEmpty(errorList, address, "STREET", "Не указана улица", addressNameGenitiveSuffix);
            checkNotEmptyAndCustom(errorList, address, "HOUSE", addressNameGenitiveSuffix,
                    "Не указан дом",
                    "о доме", "^[0-9А-Яа-яЁё№:.,/ -]+", "цифры и буквы кириллицы, допустимы символы '№', '-', ':', '.', ',', '/', ' '");
            // (по требованию клиента в дополонение к описанным в ФТ допустимым символам поля 'Дом' добавлен пробел)
            checkCustomIfNotEmpty(errorList, address, "FLAT",
                    ".{1,4}", "о квартире", addressNameGenitiveSuffix, "не более 4 символов");
            // проверка наличия полных строковых представлений для адреса (требуются для отчета)
            checkNotEmpty(errorList, address, "ADDRESSTEXT1", "Не указаны полные сведения", addressNameGenitiveSuffix);
            checkNotEmpty(errorList, address, "ADDRESSTEXT2", "Не указаны полные сведения", addressNameGenitiveSuffix);
            checkNotEmpty(errorList, address, "ADDRESSTEXT3", "Не указаны полные сведения", addressNameGenitiveSuffix);
            // почтовый индекс
            checkNotEmptyAndNumber(errorList, address, "POSTALCODE",
                    addressNameGenitiveSuffix, "Не указан почтовый индекс",
                    "о почтовом индексе",
                    "6"
            );
        }
    }

    private void validateContacts(List<String> errorList, Map<String, Object> person, String personNameGenitiveSuffix) {
        List<Map<String, Object>> contactList = getListParam(person, "contactList");
        Map<String, Map<String, Object>> contactMap = getMapByFieldStringValues(contactList, "CONTACTTYPESYSNAME");
        Map<String, Object> mobilePhone = contactMap.get("MobilePhone");
        validateMobilePhone(errorList, mobilePhone, personNameGenitiveSuffix);
        Map<String, Object> factAddressPhone = contactMap.get("FactAddressPhone");
        validateFactAddressPhone(errorList, factAddressPhone, personNameGenitiveSuffix);
        Map<String, Object> eMail = contactMap.get("PersonalEmail");
        validateEMail(errorList, eMail, personNameGenitiveSuffix);

    }

    private void validateMobilePhone(List<String> errorList, Map<String, Object> mobilePhone, String personNameGenitiveSuffix) {
        String contactNameGenitiveSuffix = "мобильного телефона " + personNameGenitiveSuffix;
        if (mobilePhone == null) {
            errorList.add("Не указаны данные " + contactNameGenitiveSuffix);
        } else {
            checkNotEmptyAndNumber(errorList, mobilePhone, "VALUE",
                    contactNameGenitiveSuffix, "Не указан номер",
                    "о номере",
                    "10"
            );
        }
    }

    private void validateFactAddressPhone(List<String> errorList, Map<String, Object> factAddressPhone, String personNameGenitiveSuffix) {
        String contactNameGenitiveSuffix = "дополнительного телефона " + personNameGenitiveSuffix;
        if (factAddressPhone != null) {
            checkNumberIfNotEmpty(errorList, factAddressPhone, "VALUE",
                    "о номере",
                    contactNameGenitiveSuffix,
                    "10"
            );
        }
    }

    private void validateEMail(List<String> errorList, Map<String, Object> eMail, String personNameGenitiveSuffix) {
        String contactNameGenitiveSuffix = "электронной почты " + personNameGenitiveSuffix;
        if (eMail == null) {
            errorList.add("Не указаны данные " + contactNameGenitiveSuffix);
        } else {
            checkNotEmpty(errorList, eMail, "VALUE",
                    "Не указан адрес", contactNameGenitiveSuffix
            );
        }
    }

    private void validateDocuments(List<String> errorList, Map<String, Object> person, Boolean isRussian, String personNameGenitiveSuffix) {
        if (isRussian != null) {
            List<Map<String, Object>> documentList = getListParam(person, "documentList");
            Map<String, Map<String, Object>> documentMap = getMapByFieldStringValues(documentList, "DOCTYPESYSNAME");
            // проверяемые документы зависят от гражданства
            if (isRussian) {
                // проверяемый документ зависит от возраста лица
                Date birthDate = null;
                Object birthDateObj = person.get("BIRTHDATE");
                if (birthDateObj != null) {
                    birthDate = (Date) parseAnyDate(birthDateObj, Date.class, "BIRTHDATE", true);
                }
                // проверяемый документ можно определить, только если возраст лица известен
                if (birthDate != null) {
                    // 14 лет
                    GregorianCalendar age14DateGC = new GregorianCalendar();
                    age14DateGC.setTime(birthDate);
                    age14DateGC.add(Calendar.YEAR, 14);
                    // сравнение с текущей системной датой допустимо, поскольку валидация выполняется для сведений, которые не сохраняются в БД
                    // todo: использовать вместо текущей даты дату создания записи в БД, если валидируемые данные станут сохраняться в БД
                    Date nowDate = new Date();
                    if (nowDate.before(age14DateGC.getTime())) {
                        // < 14 лет
                        // свидетельство о рождении
                        Map<String, Object> bornCertificate = documentMap.get("BornCertificate");
                        validateBornCertificate(errorList, bornCertificate, personNameGenitiveSuffix);
                    } else {
                        // >= 14 лет
                        // паспорт
                        Map<String, Object> passportRF = documentMap.get("PassportRF");
                        validatePassportRF(errorList, passportRF, personNameGenitiveSuffix);
                    }
                }
            } else {
                Map<String, Object> passportForeign = documentMap.get("ForeignPassport");
                validatePassportForeign(errorList, passportForeign, personNameGenitiveSuffix);
                Map<String, Object> migrationCard = documentMap.get("MigrationCard");
                validateMigrationCard(errorList, migrationCard, personNameGenitiveSuffix);
                Map<String, Object> residencePermit = documentMap.get("ResidencePermit");
                validateResidencePermit(errorList, residencePermit, personNameGenitiveSuffix);
            }
        }
    }

    private void validateBornCertificate(List<String> errorList, Map<String, Object> bornCertificate, String personNameGenitiveSuffix) {
        String docNameGenitiveSuffix = "свидетельства о рождении " + personNameGenitiveSuffix;
        if (bornCertificate == null) {
            errorList.add("Не указаны данные " + docNameGenitiveSuffix);
        } else {
            checkNotEmptyAndCustom(errorList, bornCertificate, "DOCSERIES",
                    docNameGenitiveSuffix, "Не указана серия",
                    "о серии", "[А-Яа-яЁёA-Za-z-]+", "буквы кириллицы и латинские, символ '-'"
            );
            checkNotEmptyAndNumber(errorList, bornCertificate, "DOCNUMBER",
                    docNameGenitiveSuffix, "Не указан номер",
                    "о номере",
                    "6"
            );
            checkNotEmpty(errorList, bornCertificate, "ISSUEDBY",
                    "Не указано кем выдан документ для",
                    docNameGenitiveSuffix);
            String issueDate = checkNotEmpty(errorList, bornCertificate, "ISSUEDATE",
                    "Не указана дата выдачи", docNameGenitiveSuffix
            );
            checkDateNotFuture(issueDate, errorList, "Дата выдачи", docNameGenitiveSuffix);
        }
    }

    private void validatePassportRF(List<String> errorList, Map<String, Object> passportRF, String personNameGenitiveSuffix) {
        String docNameGenitiveSuffix = "паспорта РФ " + personNameGenitiveSuffix;
        if (passportRF == null) {
            errorList.add("Не указаны данные " + docNameGenitiveSuffix);
        } else {
            checkNotEmptyAndNumber(errorList, passportRF, "DOCSERIES",
                    docNameGenitiveSuffix, "Не указана серия",
                    "о серии",
                    "4"
            );
            checkNotEmptyAndNumber(errorList, passportRF, "DOCNUMBER",
                    docNameGenitiveSuffix, "Не указан номер",
                    "о номере",
                    "6"
            );
            checkNotEmptyAndNumber(errorList, passportRF, "ISSUERCODE",
                    docNameGenitiveSuffix, "Не указан код подразделения",
                    "о коде подразделения",
                    "6"
            );
            checkNotEmpty(errorList, passportRF, "ISSUEDBY",
                    "Не указано кем выдан документ для",
                    docNameGenitiveSuffix);
            String issueDate = checkNotEmpty(errorList, passportRF, "ISSUEDATE",
                    "Не указана дата выдачи", docNameGenitiveSuffix
            );
            checkDateNotFuture(issueDate, errorList, "Дата выдачи", docNameGenitiveSuffix);
        }
    }

    private void validatePassportForeign(List<String> errorList, Map<String, Object> passportForeign, String personNameGenitiveSuffix) {
        String docNameGenitiveSuffix = "паспорта иностранного гражданина " + personNameGenitiveSuffix;
        if (passportForeign == null) {
            errorList.add("Не указаны данные " + docNameGenitiveSuffix);
        } else {
            checkNotEmptyAndCustom(errorList, passportForeign, "DOCSERIES", docNameGenitiveSuffix,
                    "Не указана серия",
                    "о серии", "^[0-9A-Za-z]{1,10}", "цифры и латинские буквы, не более 10 символов"
            );
            checkNotEmptyAndCustom(errorList, passportForeign, "DOCNUMBER", docNameGenitiveSuffix,
                    "Не указан номер",
                    "о номере", "^[0-9A-Za-z]{1,20}", "цифры и латинские буквы, не более 20 символов"
            );
            checkNotEmpty(errorList, passportForeign, "ISSUEDBY",
                    "Не указано кем выдан документ для", docNameGenitiveSuffix
            );
            String issueDate = checkNotEmpty(errorList, passportForeign, "ISSUEDATE",
                    "Не указана дата выдачи", docNameGenitiveSuffix
            );
            checkDateNotFuture(issueDate, errorList, "Дата выдачи", docNameGenitiveSuffix);
        }
    }

    private void validateMigrationCard(List<String> errorList, Map<String, Object> migrationCard, String personNameGenitiveSuffix) {
        String docNameGenitiveSuffix = "миграционной карты " + personNameGenitiveSuffix;
        if (migrationCard == null) {
            errorList.add("Не указаны данные " + docNameGenitiveSuffix);
        } else {
            checkNotEmptyAndCustom(errorList, migrationCard, "DOCSERIES", docNameGenitiveSuffix,
                    "Не указана серия",
                    "о серии", "^[0-9А-Яа-яЁё]{1,2}", "цифры и буквы кириллицы, не более 2 символов"
            );
            checkNotEmptyAndNumber(errorList, migrationCard, "DOCNUMBER", docNameGenitiveSuffix,
                    "Не указан номер",
                    "о номере", "7"
            );
            checkValidFromAndToDates(errorList, migrationCard, docNameGenitiveSuffix);
        }
    }

    private void validateResidencePermit(List<String> errorList, Map<String, Object> residencePermit, String personNameGenitiveSuffix) {
        String docNameGenitiveSuffix = "документа на право проживания (пребывания) " + personNameGenitiveSuffix;
        if (residencePermit == null) {
            errorList.add("Не указаны данные " + docNameGenitiveSuffix);
        } else {
            checkNotEmptyAndNumber(errorList, residencePermit, "DOCSERIES", docNameGenitiveSuffix,
                    "Не указана серия",
                    "о серии", "4"
            );
            checkNotEmptyAndNumber(errorList, residencePermit, "DOCNUMBER", docNameGenitiveSuffix,
                    "Не указан номер",
                    "о номере", "6"
            );
            checkValidFromAndToDates(errorList, residencePermit, docNameGenitiveSuffix);
        }
    }

    private void checkValidFromAndToDates(List<String> errorList, Map<String, Object> document, String docNameGenitiveSuffix) {
        String fromDateStr = checkNotEmpty(errorList, document, "VALIDFROMDATE",
                "Не указано начало срока пребывания для", docNameGenitiveSuffix
        );
        checkDateNotFuture(fromDateStr, errorList, "Дата начала срока пребывания для", docNameGenitiveSuffix);
        String toDateStr = checkNotEmpty(errorList, document, "VALIDTODATE",
                "Не указано окончание срока пребывания для", docNameGenitiveSuffix
        );
        if (!toDateStr.isEmpty()) {
            // сравнение с текущей системной датой допустимо, поскольку валидация выполняется для сведений, которые не сохраняются в БД
            // todo: использовать вместо текущей даты дату создания записи в БД, если валидируемые данные станут сохраняться в БД
            Date nowDate = new Date();
            Date toDate = (Date) parseAnyDate(toDateStr, Date.class, "VALIDTODATE", true);
            if (toDate != null) {
                if (toDate.before(nowDate)) {
                    errorList.add(String.format(
                            "Дата окончания срока пребывания для %s не может быть из прошлого периода", // ФТ: "Дата не может быть из прошлого периода."
                            docNameGenitiveSuffix
                    ));
                }
            }
        }
    }

    private void checkDateNotFuture(String dateStr, List<String> errorList, String valueDescrption, String docNameGenitiveSuffix) {
        if (!dateStr.isEmpty()) {
            // сравнение с текущей системной датой допустимо, поскольку валидация выполняется для сведений, которые не сохраняются в БД
            // todo: использовать вместо текущей даты дату создания записи в БД, если валидируемые данные станут сохраняться в БД
            Date nowDate = new Date();
            Date fromDate = (Date) parseAnyDate(dateStr, Date.class, "*DATE", true);
            if (fromDate != null) {
                if (fromDate.after(nowDate)) {
                    errorList.add(String.format(
                            "%s %s не может быть из будущего периода", // ФТ: "Дата не может быть из будущего периода."
                            valueDescrption, docNameGenitiveSuffix
                    ));
                }
            }
        }
    }

    private void checkNotEmptyAndCustom(List<String> errorList, Map<String, Object> sourceMap, String keyName, String valueOwnerNameGenitive, String emptyErrorPrefix, String valueAbout, String regExp, String formatDescription) {
        String valueStr = getStringParamLogged(sourceMap, keyName);
        if (valueStr.isEmpty()) {
            errorList.add(String.format("%s %s", emptyErrorPrefix, valueOwnerNameGenitive));
        } else {
            checkCustom(errorList, valueStr, regExp, valueAbout, valueOwnerNameGenitive, formatDescription);
        }
    }

    private void checkCustomIfNotEmpty(List<String> errorList, String value, String regExp, String valueDescription, String valueOwnerNameGenitive, String formatDescription) {
        if ((value != null) && (!value.isEmpty())) {
            checkCustom(errorList, value, regExp, valueDescription, valueOwnerNameGenitive, formatDescription);
        }
    }

    private void checkCustomIfNotEmpty(List<String> errorList, Map<String, Object> sourceMap, String keyName, String regExp, String valueDescription, String valueOwnerNameGenitive, String formatDescription) {
        String value = getStringParamLogged(sourceMap, keyName);
        if (!value.isEmpty()) {
            checkCustom(errorList, value, regExp, valueDescription, valueOwnerNameGenitive, formatDescription);
        }
    }

    private void checkCustom(List<String> errorList, String value, String regExp, String valueDescription, String valueOwnerNameGenitive, String formatDescription) {
        boolean isValueInvalid = checkIsValueInvalidByRegExp(value, regExp, false);
        if (isValueInvalid) {
            errorList.add(String.format(
                    "Сведения %s %s указаны некорректно (ожидаемый формат - %s)",
                    valueDescription, valueOwnerNameGenitive, formatDescription
            ));
        }
    }

    /**
     * true - российское гражданство; false - иностранный гражданин; null - гражданство не указано, не определить
     */
    private Boolean isRussianByCountryCode(String countryCode) {
        Boolean isRussian = null;
        if ((countryCode != null) && (!countryCode.isEmpty())) {
            isRussian = "RUS".equals(countryCode);
        }
        return isRussian;
    }

    // Статус налогового резидента иностранного государства - валидация
    private void validateForeignTaxResidentInfo(List<String> errorList, Map<String, Object> person, String personNameGenitiveSuffix) {
        // Статус налогового резидента США (.RESIDENTUSA)
        String residentUsa = checkNotEmpty(errorList, person, "RESIDENTUSA", "Не указан признак налогового резидента США", personNameGenitiveSuffix);
        if ("TRUE".equals(residentUsa)) {
            // ИНН США (.INNUSA)
            checkNotEmpty(errorList, person, "INNUSA", "Не указан ИНН США", personNameGenitiveSuffix);
        }
        // Статус налогового резидента другой страны (.RESIDENTOTHER); если статус НЕТ, то значение FALSE, иначе наименование страны
        String residentOther = checkNotEmpty(errorList, person, "RESIDENTOTHER", "Не указан статус налогового резидента другой страны", personNameGenitiveSuffix);
        if (!"FALSE".equals(residentOther)) {
            // ИНН другой страны (.INNOTHER)
            checkNotEmpty(errorList, person, "INNOTHER", "Не указан ИНН другой страны", personNameGenitiveSuffix);
        }
    }

    //  Реквизиты для страховой выплаты - валидация
    private void validatePaymentBankDetailsInfo(List<String> errorList, Map<String, Object> reportData) {
        // Банковские реквизиты (REPORTDATA.BANKMAP)
        Map<String, Object> bankDetails = getMapParam(reportData, "BANKMAP");
        //
        String personNameGenitiveSuffix = "в реквизитах для страховой выплаты";
        //
        // Наименование банка и его отделения (REPORTDATA.BANKMAP.NAME)
        checkNotEmpty(errorList, bankDetails, "NAME", "Не указано наименование банка и его отделения", personNameGenitiveSuffix);
        // БИК банка (REPORTDATA.BANKMAP.BIK)
        checkNotEmptyAndNumber(errorList, bankDetails, "BIK",
                personNameGenitiveSuffix, "Не указан БИК банка",
                "о БИК банка", "9"
        );
        /*
        // ИНН банка (REPORTDATA.BANKMAP.INN)
        checkNotEmptyAndNumber(errorList, bankDetails, "INN",
                personNameGenitiveSuffix, "Не указан ИНН банка",
                "о ИНН банка", "10"
        );
        // Р/с банка (REPORTDATA.BANKMAP.CHECKACC)
        checkNotEmptyAndNumber(errorList, bankDetails, "CHECKACC",
                personNameGenitiveSuffix, "Не указан расчетный счет",
                "о расчетном счете", "20"
        );
        // Корр. счет банка (REPORTDATA.BANKMAP.KORRACC)
        checkNotEmptyAndNumber(errorList, bankDetails, "KORRACC",
                personNameGenitiveSuffix, "Не указан корреспондентский счет",
                "о корреспондентском счете", "20"
        );
        */
        if (getStringParamLogged(bankDetails, "CARDNUMBER").isEmpty()) {
        // ЛС получателя (REPORTDATA.BANKMAP.FACACC)
        checkNotEmptyAndNumber(errorList, bankDetails, "FACACC",
                personNameGenitiveSuffix, "Не указан лицевой (расчетный) счет",
                "о лицевом (расчетном) счете", "20"
        );
        }
        // № карты получателя (REPORTDATA.BANKMAP.CARDNUMBER)
        checkNumberIfNotEmpty(errorList, bankDetails, "CARDNUMBER",
                "о номере пластиковой карты в реквизитах для страховой выплаты", personNameGenitiveSuffix, "16"
        );
        // телефон банка (REPORTDATA.BANKMAP.PHONE)
        checkNumberIfNotEmpty(errorList, bankDetails, "PHONE",
                "о телефоне банка в реквизитах для страховой выплаты", personNameGenitiveSuffix, "10"
        );
    }

    private String validatePaymentClaimReportData(Map<String, Object> reportData, String login, String password) {
        List<String> errorList = new ArrayList<String>();
        // Заявитель
        validateApplicant(errorList, reportData);
        // Выгодоприобретатель
        validateBeneficiary(errorList, reportData);
        //  Реквизиты для страховой выплаты
        validatePaymentBankDetailsInfo(errorList, reportData);
        // формирование результата
        String error = getErrorStringFromList(errorList);
        return error;
    }

    /**
     * Проверка всех адресов на то, что в начале ADDRESSTEXT1, ADDRESSTEXT2, ADDRESSTEXT3 нет
     * индекса. Если индекс есть, удаляем его.
     * Если в адресе отсутствует POSTALCODE, но присутствует в ADDRESSTEXT1, ADDRESSTEXT2 или ADDRESSTEXT3,
     * то записываем значение в POSTALCODE адреса
     * @param addressList
     * @return Обновлённый addressList
     */
    private List<Map<String, Object>> checkPostalCodeInAddressText(List<Map<String, Object>> addressList){
        for (int i=0; i<addressList.size(); i++) {
            Map<String, Object> address = addressList.get(i);
            String addressText1 = getStringParamLogged(address,"ADDRESSTEXT1");
            String addressText2 = getStringParamLogged(address,"ADDRESSTEXT2");
            String addressText3 = getStringParamLogged(address,"ADDRESSTEXT3");
            String postalCode = "";
            String regex = "\\d{6}.*";
            String replaceRegex = "\\d{6}\\,{0,1}\\s{0,1}";
            if (addressText1.matches(regex)){
                postalCode = addressText1.substring(0, 6);
                addressText1 = addressText1.replaceFirst(replaceRegex, "");
                address.put("ADDRESSTEXT1", addressText1);
            }
            if (addressText2.matches(regex)){
                addressText2 = addressText2.replaceFirst(replaceRegex, "");
                address.put("ADDRESSTEXT2", addressText2);
            }
            if (addressText3.matches(regex)){
                addressText3 = addressText3.replaceFirst(replaceRegex, "");
                address.put("ADDRESSTEXT3", addressText3);
            }
            String postalCodeOld = getStringParamLogged(address, "POSTALCODE");
            if ((postalCodeOld.equals("-") || postalCodeOld.isEmpty()) && !postalCode.isEmpty()){
                address.put("POSTALCODE", postalCode);
            }
            addressList.set(i, address);
        }
        return addressList;

        // RegisterAddress
    }

}
