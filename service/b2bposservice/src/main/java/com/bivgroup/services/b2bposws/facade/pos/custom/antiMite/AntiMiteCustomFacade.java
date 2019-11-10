/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bivgroup.services.b2bposws.facade.pos.custom.antiMite;

import static com.bivgroup.services.b2bposws.facade.pos.contract.custom.B2BContractCustomFacade.getLastElementByAtrrValue;
import com.bivgroup.services.b2bposws.facade.pos.custom.ProductContractCustomFacade;
import com.bivgroup.services.b2bposws.system.Constants;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;
import org.apache.log4j.Logger;
import static ru.diasoft.services.inscore.system.WsConstants.LOGIN;
import static ru.diasoft.services.inscore.system.WsConstants.PASSWORD;

/**
 *
 * @author averichevsm
 */
@BOName("AntiMite")
public class AntiMiteCustomFacade extends ProductContractCustomFacade {

    private final Logger logger = Logger.getLogger(this.getClass());

    private static final String B2BPOSWS_SERVICE_NAME = Constants.B2BPOSWS;
    private static final String THIS_SERVICE_NAME = B2BPOSWS_SERVICE_NAME;
    private static final String INSTARIFICATORWS_SERVICE_NAME = Constants.INSTARIFICATORWS;

    private static final String DEFAULT_AND_ONLY_PROGRAM_SYSTEM_NAME_ANTIMITE = "ANTIMITE_BASIC";

    //private static final String REG_EXP_LATIN = "^[A-Za-z]+";
    /**
     * Метод для подготовки данных для отчета по продукту.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {"CONTRID", "PRODCONFID"})
    public Map<String, Object> dsB2BAntiMitePrintDocDataProvider(Map<String, Object> params) throws Exception {

        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();

        // загрузка данных договора базовой версией поставщика
        params.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BBasePrintDocDataProvider", params, login, password);

        // todo: генерация строковых представлений для сумм
        result.put(RETURN_AS_HASH_MAP, true);
        result = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContractTextSums", result, login, password);

        // todo: генерация дополнительных значений 
        //logger.debug("dsB2BAntiMitePrintDocDataProvider result:\n\n" + result + "\n");
        result.put("PRODCONFID", params.get("PRODCONFID"));
        return result;
    }

    private boolean validateSaveParams(Map<String, Object> contract, String login, String password) throws Exception {

        StringBuffer errorText = new StringBuffer();

        // для текущего продукта не требуется - все суммы всегда в рублях
        //validateCurrencyRate(contract, errorText, login, password);
        Map<String, Object> insurer = (Map<String, Object>) contract.get("INSURERMAP");
        if (insurer == null) {
            errorText.append("Не указаны сведения о страхователе (INSURERMAP). ");
        } else {
            validateInsurerInfo(insurer, errorText);
        }

        Map<String, Object> contractExtValues = (Map<String, Object>) contract.get("CONTREXTMAP");
        if (contractExtValues == null) {
            errorText.append("Не указаны расширенные атрибуты договора (CONTREXTMAP). ");
        } else {
            validateContractExtValuesAndMemberList(contract, contractExtValues, errorText);
        }

        boolean isDataValid = errorText.length() == 0;
        if (!isDataValid) {
            errorText.append("Сведения договора не сохранены.");
            contract.put("Status", "Error");
            contract.put("Error", errorText.toString());
        }
        return isDataValid;
    }

    // todo: возможно, перенести в ProductContractCustomFacade метод validateInsurerInfo и все вызываемые из него (используется в трех продуктах - vzr, sis, antimite)
    protected void validateInsurerInfo(Map<String, Object> insurer, StringBuffer errorText) {
        if (insurer.get("FIRSTNAME") == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) не указано имя страхователя (FIRSTNAME). ");
        }
        if (insurer.get("LASTNAME") == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) не указана фамилия страхователя (LASTNAME). ");
        }
        if (insurer.get("BIRTHDATE") == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) не указана дата рождения страхователя (BIRTHDATE). ");
        }
        if (insurer.get("GENDER") == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) не указан пол страхователя (GENDER). ");
        }
        Long citizenship = getLongParam(insurer.get("CITIZENSHIP"));
        if (citizenship == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) не указано гражданство страхователя (CITIZENSHIP). ");
        } else {
            ArrayList<Map<String, Object>> documentList = (ArrayList<Map<String, Object>>) insurer.get("documentList");
            if ((documentList == null) || (documentList.isEmpty())) {
                errorText.append("В сведениях о страхователе (INSURERMAP) список документов страхователя (documentList) отсутствует или пуст. ");
            } else {
                validateInsurerDocuments(documentList, citizenship, errorText);
            }

            ArrayList<Map<String, Object>> contactList = (ArrayList<Map<String, Object>>) insurer.get("contactList");
            if ((contactList == null) || (contactList.isEmpty())) {
                errorText.append("В сведениях о страхователе (INSURERMAP) список контактных данных страхователя (contactList) отсутствует или пуст. ");
            } else {
                validateInsurerContacts(contactList, errorText);
            }
        }
    }

    protected void validateInsurerDocuments(ArrayList<Map<String, Object>> documentList, Long citizenship, StringBuffer errorText) {

        String docType;
        String docSeriesRegExp;
        String docNumberRegExp;
        if (citizenship.intValue() == 0) {
            docType = "PassportRF";
            docSeriesRegExp = "\\d{4}";
            docNumberRegExp = "\\d{6}";
        } else {
            docType = "ForeignPassport";
            docSeriesRegExp = "^[0-9A-Za-z]{1,10}"; // аналогично angular-интерфейсу
            docNumberRegExp = "^[0-9A-Za-z]{1,20}"; // аналогично angular-интерфейсу
        }

        Map<String, Object> passport = null;
        for (Map<String, Object> document : documentList) {
            String docTypeSysName = getStringParam(document.get("DOCTYPESYSNAME"));
            if (docType.equals(docTypeSysName)) {
                passport = document;
                break;
            }
        }

        if (passport == null) {
            errorText.append("В сведениях о страхователе (INSURERMAP) список документов страхователя (documentList) не содержит записи о паспортных данных. ");
        } else {

            if (passport.get("ISSUEDBY") == null) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных не содержит данных о том, кем выдан паспорт (ISSUEDBY). ");
            }
            if (passport.get("ISSUEDATE") == null) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных не содержит даты выдачи паспорта (ISSUEDATE). ");
            }

            String docSeries = getStringParam(passport.get("DOCSERIES"));
            if (docSeries.isEmpty()) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных не содержит серию паспорта. ");
            } else if (checkIsValueInvalidByRegExp(docSeries, docSeriesRegExp, false)) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных содержит некорректное значение серии паспорта. ");
            }

            String docNumber = getStringParam(passport.get("DOCNUMBER"));
            if (docNumber.isEmpty()) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных не содержит номер паспорта. ");
            } else if (checkIsValueInvalidByRegExp(docNumber, docNumberRegExp, false)) {
                errorText.append("В сведениях о страхователе (INSURERMAP) в списке документов страхователя (documentList) запись о паспортных данных содержит некорректное значение номера паспорта. ");
            }
        }
    }

    protected void validateInsurerContacts(ArrayList<Map<String, Object>> contactList, StringBuffer errorText) {

        String personalEmail = "";
        String mobilePhone = "";

        for (Map<String, Object> contact : contactList) {
            String contactTypeSysName = getStringParam(contact.get("CONTACTTYPESYSNAME"));
            if ((personalEmail.isEmpty()) && ("PersonalEmail".equals(contactTypeSysName))) {
                personalEmail = getStringParam(contact.get("VALUE"));
            } else if ((mobilePhone.isEmpty()) && ("MobilePhone".equals(contactTypeSysName))) {
                mobilePhone = getStringParam(contact.get("VALUE"));
            }
        }

        if (personalEmail.isEmpty()) {
            errorText.append("В сведениях о страхователе (INSURERMAP) список контактных данных страхователя (contactList) не содержит записи c адресом электронной почты. ");
        }

        if (mobilePhone.isEmpty()) {
            errorText.append("В сведениях о страхователе (INSURERMAP) список контактных данных страхователя (contactList) не содержит записи с номером мобильного телефона. ");
        }

    }

    private void validateContractExtValuesAndMemberList(Map<String, Object> contract, Map<String, Object> contractExtValues, StringBuffer errorText) {
        Object isInsurerInsuredPersonObj = contractExtValues.get("isInsurerInsuredPerson");
        if (isInsurerInsuredPersonObj == null) {
            errorText.append("В расширенных атрибутах договора (CONTREXTMAP) не указано является ли страхователь застрахованным лицом (isInsurerInsuredPerson). ");
        } else {
            Long isInsurerInsuredPerson = getLongParam(isInsurerInsuredPersonObj);
            if (isInsurerInsuredPerson == 0L) {
                // проверка списка застрахованных
                List<Map<String, Object>> memberList = (List<Map<String, Object>>) contract.get("MEMBERLIST");
                if ((memberList == null) || (memberList.size() != 1)) {
                    errorText.append("Список застрахованных (MEMBERLIST) пуст или содержит более одного элемента. ");
                } else {
                    Map<String, Object> member = memberList.get(0);
                    // проверка застрахованного
                    if (member == null) {
                        errorText.append("Единственный элемент списка застрахованных (MEMBERLIST) пуст. ");
                    } else {
                        if (member.get("NAME") == null) {
                            errorText.append("Запись о застрахованном в списке застрахованных (MEMBERLIST) не содержит имени застрахованного (NAME). ");
                        }
                        if (member.get("SURNAME") == null) {
                            errorText.append("Запись о застрахованном в списке застрахованных (MEMBERLIST) не содержит фамилии застрахованного (SURNAME). ");
                        }
                        if (member.get("BIRTHDATE") == null) {
                            errorText.append("Запись о застрахованном в списке застрахованных (MEMBERLIST) не содержит даты рождения застрахованного (BIRTHDATE). ");
                        }
                        if (member.get("docSeries") == null) {
                            errorText.append("Запись о застрахованном в списке застрахованных (MEMBERLIST) не содержит серии документа застрахованного (docSeries). ");
                        }
                        if (member.get("docNumber") == null) {
                            errorText.append("Запись о застрахованном в списке застрахованных (MEMBERLIST) не содержит номера документа застрахованного (docNumber). ");
                        }
                    }
                }
            }
        }
    }

    protected Map<String, Object> genAdditionalSaveParams(Map<String, Object> contract, String login, String password) throws Exception {
        boolean isParamsChangingLogged = logger.isDebugEnabled();
        // идентификатор версии продукта всегда передается в явном виде из b2bContrSave
        Long prodVerID = getLongParam(contract.get("PRODVERID"));
        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            setGeneratedParam(contract, "PRODCONFID", prodConfID, isParamsChangingLogged);
        }

        // инициализация даты документа
        GregorianCalendar documentDateGC = getOrGenerateDocumentDate(contract);

        // дата начала действия полиса
        // todo: определение метода вычисления даты заменить на анализ SDCALCMETHOD из B2B_PRODDEFVAL
        GregorianCalendar startDateGC = new GregorianCalendar();
        startDateGC.setTime(documentDateGC.getTime());
        startDateGC.add(Calendar.DATE, 6); // todo: заменить на SDLAG из B2B_PRODDEFVAL
        setOverridedParam(contract, "STARTDATE", startDateGC.getTime(), isParamsChangingLogged);

        // определение срока действия в годах
        // todo: получение идентификатора срока действия из продукта (когда будут заполнены B2B_PRODTERM и B2B_TERM)
        Integer durationYears = getIntegerParam(contract.get("TERMID")); // todo: получение действительного срока действия по идентификатору срока (когда будут заполнена B2B_TERM)
        if (durationYears == 0) {
            durationYears = 1;
            contract.put("TERMID", durationYears);
        }

        // безусловное вычисление даты окончания действия
        GregorianCalendar finishDateGC = new GregorianCalendar();
        finishDateGC.setTime(startDateGC.getTime());
        finishDateGC.add(Calendar.YEAR, durationYears);
        finishDateGC.add(Calendar.DATE, -1);
        finishDateGC.set(Calendar.HOUR_OF_DAY, 23);
        finishDateGC.set(Calendar.MINUTE, 59);
        finishDateGC.set(Calendar.SECOND, 59);
        finishDateGC.set(Calendar.MILLISECOND, 0);
        setOverridedParam(contract, "FINISHDATE", finishDateGC.getTime(), isParamsChangingLogged);

        // безусловное перевычисление срока действия
        long startDateInMillis = startDateGC.getTimeInMillis();
        long finishDateInMillis = finishDateGC.getTimeInMillis();
        long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000)); // в сутках (24*60*60*1000) милисекунд
        duration += 1; // необходимо прибавить один день, т.к. дата окончания сдвинута на одну секунду в прошлое (и точная длительность получается Х дней без одной секунды)
        setOverridedParam(contract, "DURATION", duration, isParamsChangingLogged);

        // список типов объектов - выбор (если уже существует в договоре) или создание нового
        Object insObjGroupListFromContract = contract.get("INSOBJGROUPLIST");
        ArrayList<Map<String, Object>> insObjGroupList;
        if (insObjGroupListFromContract == null) {
            insObjGroupList = new ArrayList<Map<String, Object>>();
            contract.put("INSOBJGROUPLIST", insObjGroupList);
        } else {
            insObjGroupList = (ArrayList<Map<String, Object>>) insObjGroupListFromContract;
        }

        // получение сведений о продукте (по идентификатору конфигурации продукта)
        Map<String, Object> productParams = new HashMap<String, Object>();
        productParams.put("PRODCONFID", prodConfID);
        productParams.put("HIERARCHY", false);
        productParams.put(RETURN_AS_HASH_MAP, true);
        Map<String, Object> product = this.callService(B2BPOSWS_SERVICE_NAME, "dsProductBrowseByParams", productParams, login, password);

        // расширенные атрибуты договора - выбор (если уже существуют в договоре) или создание новых
        Object contractExt = contract.get("CONTREXTMAP");
        Map<String, Object> contractExtValues;
        if (contractExt != null) {
            contractExtValues = (Map<String, Object>) contractExt;
        } else {
            contractExtValues = new HashMap<String, Object>();
            contract.put("CONTREXTMAP", contractExtValues);
        }

        // определение идентификатора справочника расширенных атрибутов договора на основании сведений о продукте
        Object contrExtMapHBDataVerID = product.get("HBDATAVERID");
        contractExtValues.put("HBDATAVERID", contrExtMapHBDataVerID);

        // установка системного имени программы
        String programSysName = DEFAULT_AND_ONLY_PROGRAM_SYSTEM_NAME_ANTIMITE;
        setOverridedParam(contract, "PRODPROGSYSNAME", programSysName, isParamsChangingLogged);

        // определение идентификатора и кода программы по её системному имени на основании сведений о продукте
        Map<String, Object> prodVer = (Map<String, Object>) product.get("PRODVER");
        ArrayList<Map<String, Object>> prodProgs = (ArrayList<Map<String, Object>>) prodVer.get("PRODPROGS");
        Map<String, Object> program = (Map<String, Object>) getLastElementByAtrrValue(prodProgs, "SYSNAME", programSysName);
        Long programID = getLongParam(program.get("PRODPROGID"));
        setOverridedParam(contract, "PRODPROGID", programID, isParamsChangingLogged);
        String programCode = getStringParam(program.get("PROGCODE"));
        setOverridedParam(contract, "PRODPROGCODE", programCode, isParamsChangingLogged);

        // определение страховых суммы и премии по сведениями из программы страхования
        Double programInsAmValue = getDoubleParam(program.get("INSAMVALUE"));
        Double programPremValue = getDoubleParam(program.get("PREMVALUE"));
        setOverridedParam(contract, "INSAMVALUE", programInsAmValue, isParamsChangingLogged);
        setOverridedParam(contract, "PREMVALUE", programPremValue, isParamsChangingLogged);

        // безусловная установка валют договора (для текущего продукта - всегда рубли)
        setOverridedParam(contract, "INSAMCURRENCYID", 1L, isParamsChangingLogged);
        setOverridedParam(contract, "PREMCURRENCYID", 1L, isParamsChangingLogged);

        // формирование структуры тип/объект/риск по сведениям из продукта для включения в сохраняемый договор
        boolean isMissingStructsCreated = true;
        updateContractInsuranceProductStructure(contract, product, false, programCode, isMissingStructsCreated, login, password);

        // застрахованный
        List<Map<String, Object>> memberList = (List<Map<String, Object>>) contract.get("MEMBERLIST");
        if ((memberList != null) && (memberList.size() > 0)) {
            Map<String, Object> member = memberList.get(0);
            // дата рождения застрахованного
            Object insuredBirthdate = member.get("BIRTHDATE");
            Date insurerBirthdateDate = (Date) parseAnyDate(insuredBirthdate, Date.class, "BIRTHDATE");
            GregorianCalendar insurerBirthdateGC = new GregorianCalendar();
            insurerBirthdateGC.setTime(insurerBirthdateDate);

            // дата документа минус 14 лет
            GregorianCalendar documentDateMinus14YearsGC = new GregorianCalendar();
            documentDateMinus14YearsGC.setTime(documentDateGC.getTime());
            documentDateMinus14YearsGC.add(Calendar.YEAR, -14);

            // установка ссылки на тип документа из справочника 'B2B.AntiMite.InsuredDocumentTypes' в завсисмости от возраста
            if (insurerBirthdateGC.after(documentDateMinus14YearsGC)) {
                // B2B.AntiMite.InsuredDocumentTypes: hid = 2, sysName = 'BornCertificate', name = 'Свидетельство о рождении'
                //member.put("docTypeHID", 2L);
                setOverridedParam(member, "docTypeHID", 2L, isParamsChangingLogged);
            } else {
                // B2B.AntiMite.InsuredDocumentTypes: hid = 1, sysName = 'PassportRF', name = 'Паспорт гражданина РФ'
                //member.put("docTypeHID", 1L);
                setOverridedParam(member, "docTypeHID", 1L, isParamsChangingLogged);
            }
        }
        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        Object currentRowStatus = contract.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
            contract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
        }

        return contract;
    }

    private void genAdditionalSaveParamsFixContr(Map<String, Object> contract, String login, String password) throws Exception {
        boolean isParamsChangingLogged = logger.isDebugEnabled();
        // идентификатор версии продукта всегда передается в явном виде из b2bContrSave
        Long prodVerID = getLongParam(contract.get("PRODVERID"));
        // определение идентификатора продукта по идентификатору версии
        Long prodConfID = getLongParam(contract.get("PRODCONFID"));
        if ((prodConfID == null) || (prodConfID == 0L)) {
            Map<String, Object> configParams = new HashMap<String, Object>();
            configParams.put("PRODVERID", prodVerID);
            prodConfID = getLongParam(this.callServiceAndGetOneValue(B2BPOSWS_SERVICE_NAME, "dsB2BProductConfigBrowseListByParam", configParams, login, password, "PRODCONFID"));
            setGeneratedParam(contract, "PRODCONFID", prodConfID, isParamsChangingLogged);
        }
        GregorianCalendar documentDateGC = new GregorianCalendar();
        Object documentDate = contract.get("DOCUMENTDATE");
        documentDateGC.setTime((Date) parseAnyDate(documentDate, Date.class, "DOCUMENTDATE"));
        GregorianCalendar startDateGC = new GregorianCalendar();
        Object startDate = contract.get("STARTDATE");
        startDateGC.setTime((Date) parseAnyDate(startDate, Date.class, "STARTDATE"));
        GregorianCalendar finishDateGC = new GregorianCalendar();
        Object finishDate = contract.get("FINISHDATE");
        finishDateGC.setTime((Date) parseAnyDate(finishDate, Date.class, "FINISHDATE"));
        // безусловное перевычисление срока действия
        long startDateInMillis = startDateGC.getTimeInMillis();
        long finishDateInMillis = finishDateGC.getTimeInMillis();
        long duration = (long) ((finishDateInMillis - startDateInMillis) / (24 * 60 * 60 * 1000)); // в сутках (24*60*60*1000) милисекунд
        duration += 1; // необходимо прибавить один день, т.к. дата окончания сдвинута на одну секунду в прошлое (и точная длительность получается Х дней без одной секунды)
        setOverridedParam(contract, "DURATION", duration, isParamsChangingLogged);

        // застрахованный
        List<Map<String, Object>> memberList = (List<Map<String, Object>>) contract.get("MEMBERLIST");
        if ((memberList != null) && (memberList.size() > 0)) {
            Map<String, Object> member = memberList.get(0);
            // дата рождения застрахованного
            Object insuredBirthdate = member.get("BIRTHDATE");
            Date insurerBirthdateDate = (Date) parseAnyDate(insuredBirthdate, Date.class, "BIRTHDATE");
            GregorianCalendar insurerBirthdateGC = new GregorianCalendar();
            insurerBirthdateGC.setTime(insurerBirthdateDate);

            // дата документа минус 14 лет
            GregorianCalendar documentDateMinus14YearsGC = new GregorianCalendar();
            documentDateMinus14YearsGC.setTime(documentDateGC.getTime());
            documentDateMinus14YearsGC.add(Calendar.YEAR, -14);

            // установка ссылки на тип документа из справочника 'B2B.AntiMite.InsuredDocumentTypes' в завсисмости от возраста
            if (insurerBirthdateGC.after(documentDateMinus14YearsGC)) {
                // B2B.AntiMite.InsuredDocumentTypes: hid = 2, sysName = 'BornCertificate', name = 'Свидетельство о рождении'
                //member.put("docTypeHID", 2L);
                setOverridedParam(member, "docTypeHID", 2L, isParamsChangingLogged);
            } else {
                // B2B.AntiMite.InsuredDocumentTypes: hid = 1, sysName = 'PassportRF', name = 'Паспорт гражданина РФ'
                //member.put("docTypeHID", 1L);
                setOverridedParam(member, "docTypeHID", 1L, isParamsChangingLogged);
            }
        }
        // если договор уже был создан ранее и повторно передан для сохранения - необходимо пометить его как изменившийся (поскольку ряд атрибутов были пересчитаны безусловно)
        Object currentRowStatus = contract.get(ROWSTATUS_PARAM_NAME);
        if ((currentRowStatus != null) && (getIntegerParam(currentRowStatus) == UNMODIFIED_ID)) {
            contract.put(ROWSTATUS_PARAM_NAME, MODIFIED_ID);
        }
    }

    // инициализация даты документа
    private GregorianCalendar getOrGenerateDocumentDate(Map<String, Object> contract) {
        GregorianCalendar documentDateGC = new GregorianCalendar();
        Object docDate = contract.get("DOCUMENTDATE");
        if (docDate == null) {
            documentDateGC.setTime(new Date());
            documentDateGC.set(Calendar.HOUR_OF_DAY, 0);
            documentDateGC.set(Calendar.MINUTE, 0);
            documentDateGC.set(Calendar.SECOND, 0);
            documentDateGC.set(Calendar.MILLISECOND, 0);
            setGeneratedParam(contract, "DOCUMENTDATE", documentDateGC.getTime(), logger.isDebugEnabled());
        } else {
            //logger.debug("DOCDATE-" + docDate);
            documentDateGC.setTime((Date) parseAnyDate(docDate, Date.class, "DOCUMENTDATE"));
        }
        return documentDateGC;
    }

    /**
     * Метод для сохранения договора по продукту.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BAntiMiteContractPrepareToSave(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BAntiMiteContractPrepareToSave");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        boolean isDataValid; //= true; //!только для отладки!
        if (!((params.get("DISABLE_VALIDATION") != null) && (Long.valueOf(params.get("DISABLE_VALIDATION").toString()).longValue() == 1))) {
            isDataValid = validateSaveParams(contract, login, password);
        } else {
            isDataValid = true;
        }
        Map<String, Object> result;
        if (isDataValid) {
            genAdditionalSaveParams(contract, login, password);
            result = contract;
        } else {
            result = contract;
        }
        logger.debug("after dsB2BAntiMiteContractPrepareToSave\n");
        return result;
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BAntiMiteContractPrepareToSaveFixContr(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BAntiMiteContractPrepareToSaveFixContr");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> contract;
        if (params.get("CONTRMAP") != null) {
            contract = (Map<String, Object>) params.get("CONTRMAP");
        } else {
            contract = params;
        }
        boolean isDataValid = validateSaveParams(contract, login, password);
        Map<String, Object> result;
        if (isDataValid) {
            genAdditionalSaveParamsFixContr(contract, login, password);
            result = contract;
        } else {
            result = contract;
        }
        if ((null != params.get("is1CExported")) && ((Boolean) params.get("is1CExported"))) {
            if ((null != params.get("b2bCorrector1C")) && ((Boolean) params.get("b2bCorrector1C"))) {
                result = contract;
            } else if ((null != params.get("isCorrector")) && ((Boolean) params.get("isCorrector"))) {
                contract.remove("CRMDOCLIST");
                contract.remove("MEMBERLIST");
                contract.remove("INSURERID");
                contract.remove("INSURERMAP");
                if (null != contract.get("CONTREXTMAP")) {
                    Map<String, Object> cExtMap = (Map<String, Object>) contract.get("CONTREXTMAP");
                    cExtMap.remove("isInsurerInsuredPerson");
                }
            } else {
                // Если договор выгружен в 1С и у пользователя нет прав корректора запрещем что либо сохранять.
                result = new HashMap< String, Object>();
            }
        }

        logger.debug("after dsB2BAntiMiteContractPrepareToSaveFixContr");
        return result;
    }

    /**
     * Метод для загрузки договора по продукту.
     *
     * @param params
     *
     * @return
     *
     * @throws Exception
     */
    @WsMethod(requiredParams = {})
    public Map<String, Object> dsB2BAntiMiteContrLoad(Map<String, Object> params) throws Exception {
        logger.debug("before dsB2BAntiMiteContrLoad");
        String login = params.get(LOGIN).toString();
        String password = params.get(PASSWORD).toString();
        Map<String, Object> loadResult = this.callService(B2BPOSWS_SERVICE_NAME, "dsB2BContrLoad", params, login, password);
        logger.debug("after dsB2BAntiMiteContrLoad");
        return loadResult;
    }

}
