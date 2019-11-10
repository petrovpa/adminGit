package com.bivgroup.services.b2bposws.facade.pos.clientProfile;

import com.bivgroup.services.b2bposws.facade.pos.validation.DescriptionParameter;
import com.bivgroup.services.b2bposws.facade.pos.validation.ValidationParameterB2BCustom;
import org.apache.log4j.Logger;

import java.util.*;

import static com.bivgroup.services.b2bposws.facade.pos.validation.DescriptionParameter.*;

/**
 * @author Alex Ivashin
 */
public class B2BClientProfileParameterValidation extends ValidationParameterB2BCustom {

    private static final String ERROR_STRUCTURE_HEADER_CLIENT_PROFILE = ERROR_STRUCTURE_REQUIRED_HEADER + " профиля клиента:";
    private static final String REQIRIED_PARAMETERES_FOREIGN_DOC_NAME = "FOREIGN_CLIENT_PROFILE_DOC";
    private static final String REQIRIED_PARAMETERES_RUSSIAN_DOC_NAME = "RUSSIAN_CLIENT_PROFILE_DOC";
    private static final String REQIRIED_PARAMETERES_DRIVER_LICENSE_DOC_NAME = "DRIVER_LICENSE_CLIENT_PROFILE_DOC";
    private static final String TYPEID_SYSNAME = "typeId";

    private static final String VEHICLE_DOC_SERIES_REG_EXP = "^([0-9]{2})([0-9А-Я]{2})$";

    private static final int MIN_AGE_FOR_DRIVING = 18;
    private static final long RUSSIAN_CITIZEN_DOC_TYPE = 1001L;
    private static final long FOREIGN_CITIZEN_DOC_TYPE = 1004L;
    private static final long DRIVER_LICENSE_DOC_TYPE = 1016L;
    //private static final long RUSSIAN_CITIZENSHIP = 0L;
    private static final long RUSSIAN_CITIZENSHIP = 1L;
    private static final long REGISTRATION_ADDRESS_TYPE = 1003L;
    private static final long RESIDENTIAL_ADDRESS_TYPE = 1005L;
    private static final long MOBILE_PHONE_TYPE = 1005;
    private static final long EMAIL_TYPE = 1006;

    private Long citizenship;
    private Map<String, Object> clientMap;

    private final Map<String, List<DescriptionParameter>> DESCRIPTION_PARAMETERS_BEFORE_SAVING;

    {
        DESCRIPTION_PARAMETERS_BEFORE_SAVING = new HashMap<String, List<DescriptionParameter>>();

        // "Профиль. Валидация даты рождения. Дата рождения должна быть 18+"
        DescriptionParameter clientDateOfBirth = new DescriptionParameter(
                "dateOfBirth$date", "Дата рождения клиента",
                verificationClientDateOfBirthEighteenPlus
        ).isNeedChangePersonalData();

        DESCRIPTION_PARAMETERS_BEFORE_SAVING.put("CLIENT_PROFILE_PERSONAL_INFORMATION",
                Arrays.asList(
                        (new DescriptionParameter("sex", "Пол")).isNeedChangePersonalData(),
                        new DescriptionParameter("isEmptyPatronymic", "Отчество отсутствует"),
                        (new DescriptionParameter(
                                "patronymic", "isEmptyPatronymic",
                                "Отчество", false
                        )).isNeedChangePersonalData(),
                        // "Профиль. Валидация даты рождения. Дата рождения должна быть 18+"
                        clientDateOfBirth,
                        (new DescriptionParameter("surname", "Фамилия")).isNeedChangePersonalData(),
                        (new DescriptionParameter("name", "Имя")).isNeedChangePersonalData(),
                        (new DescriptionParameter("countryId", "Гражданство")).isNeedChangePersonalData(),
                        new DescriptionParameter("isMarried", "Состоит в браке")
                )
        );

        DescriptionParameter isPrimary = new DescriptionParameter("isPrimary", "Основной");

        DescriptionParameter docType = new DescriptionParameter(TYPEID_SYSNAME, "Вид документа");
        DescriptionParameter dateOfIssue = (new DescriptionParameter(
                "dateOfIssue$date", "Дата выдачи",
                verificationDateOfIssuePassport
        )).isNeedChangePersonalData();
        DescriptionParameter authority = (new DescriptionParameter("authority", "Кем выдан")).isNeedChangePersonalData();
        DescriptionParameter issueCode = (new DescriptionParameter("issuerCode", "Код подразделения")).isNeedChangePersonalData();
        DESCRIPTION_PARAMETERS_BEFORE_SAVING.put(REQIRIED_PARAMETERES_RUSSIAN_DOC_NAME,
                Arrays.asList(
                        isPrimary, docType, series, no, dateOfIssue, authority, issueCode
                )
        );

        DESCRIPTION_PARAMETERS_BEFORE_SAVING.put(REQIRIED_PARAMETERES_FOREIGN_DOC_NAME,
                Arrays.asList(
                        isPrimary, docType, (new DescriptionParameter(foreignNo)).isNeedChangePersonalData(),
                        dateOfIssue, authority
                )
        );

        DESCRIPTION_PARAMETERS_BEFORE_SAVING.put(REQIRIED_PARAMETERES_DRIVER_LICENSE_DOC_NAME,
                Arrays.asList(
                        isPrimary, docType, no, authority,
                        new DescriptionParameter(dateOfIssue).setVerification(new UniqueVerification() {
                            @Override
                            public boolean check(Object parameter, String errorPrefix) {
                                boolean result = true;
                                if (isNullParameter(parameter)) {
                                    result = false;
                                }

                                if (result) {
                                    errorPrefix = isNullParameter(errorPrefix) ? "" : errorPrefix;
                                    Date currentDate = clearTimeToDate(new Date());
                                    Date minDate = changeDateToYears(currentDate, -10);
                                    Date checkDate = (Date) parseAnyDate(parameter, Date.class, "Дата выдачи");
                                    if (checkDate.before(minDate)) {
                                        setOtherError(errorPrefix + "Дата выдачи водительского удостоверения: срок действия указанного ВУ истек");
                                        result = false;
                                    }


                                    if (result && checkDate.after(currentDate)) {
                                        setOtherError(errorPrefix + "Неверное значение поля дата выдачи водительского удостоверения");
                                        result = false;
                                    }
                                }
                                return result;
                            }
                        }),
                        new DescriptionParameter("series", "Серия", new UniqueVerification() {
                            @Override
                            public boolean check(Object parameter, String errorPrefix) {
                                return isValidateDocumentNumber(parameter, errorPrefix, "В серии ", VEHICLE_DOC_SERIES_REG_EXP);
                            }
                        })
                )
        );

        DESCRIPTION_PARAMETERS_BEFORE_SAVING.put("OWNER_PAGE_ADDRESS",
                Arrays.asList(
                        new DescriptionParameter(TYPEID_SYSNAME, "Тип адреса"),
                        (new DescriptionParameter("cityCode", "Код города")).isNeedChangePersonalData(),
                        (new DescriptionParameter("city", "Город")).isNeedChangePersonalData(),
                        (new DescriptionParameter("regionCode", "Код региона")).isNeedChangePersonalData(),
                        (new DescriptionParameter("region", "Регион")).isNeedChangePersonalData(),
                        (new DescriptionParameter("house", "Дом")).isNeedChangePersonalData(),
                        new DescriptionParameter(
                                "street", "isEmptyStreet",
                                "Улица", false
                        ),
                        isPrimary
                )
        );

        DESCRIPTION_PARAMETERS_BEFORE_SAVING.put("CLIENT_PROFILE_CONTACT",
                Arrays.asList(
                        new DescriptionParameter(TYPEID_SYSNAME, "Тип контакта"),
                        new DescriptionParameter("value", "Значение"),
                        isPrimary
                )
        );
    }

    public B2BClientProfileParameterValidation(Map<String, Object> clientMap, Map<String, Object> additionalSettings) {
        super(additionalSettings);
        this.clientMap = clientMap;
        logger = Logger.getLogger(B2BClientProfileParameterValidation.class);
    }

    @Override
    public boolean validationParametersBeforeSaving() {
        setDescriptionParameters(DESCRIPTION_PARAMETERS_BEFORE_SAVING);

        if (!isNullCollection(clientMap)) {
            checkRequiredParameter("CLIENT_PROFILE_PERSONAL_INFORMATION", clientMap);
            checkDocuments();
            checkAddresses();
            checkContacts();
        }
        return isHaveErrors(clientMap);
    }

    private void checkDocuments() {
        Object citizenshipObj = clientMap.get("countryId");
        if (isNullParameter(citizenshipObj)) {
            return;
        }
        citizenship = Long.parseLong(citizenshipObj.toString());

        List<Map<String, Object>> documents = (List<Map<String, Object>>) clientMap.get("documents");
        if (isNullCollection(documents) || isAllElementListWithStatusDeleted(documents)) {
            setStructureError("Список документов");
            return;
        }

        if (!isPrimaryAtLeastOnePassport(documents)) {
            setOtherError("В списке документов отсутствует хотя бы один " + getPassportTypeString() + " помеченный признаком \"Основной\"");
        }

        String requiredParametersName;
        for (Map<String, Object> document : documents) {
            if (isNotNullAndDeletedRowStatus(document)) {
                continue;
            }

            requiredParametersName = getNameRequiredParametersByDocumentType(document);

            checkRequiredParameterAddPrefix(requiredParametersName, "Список документов: ", document);
            if (!checkDocumentDate(document)) {
                this.isNeedChangeError = true;
            }
        }
    }

    private boolean checkDocumentDate(Map<String, Object> document) {
        Object typeIdObj = document.get("typeId");
        Object series = document.get("series");
        Object no = document.get("no");
        boolean result = true;
        if (isNullParameter(typeIdObj) || isNullParameter(series) || isNullParameter(no)) {
            result = false;
        }

        StringBuilder errorText = new StringBuilder();
        String errorDate = "";
        Long typeId = Long.valueOf(typeIdObj.toString());
        if (typeId.equals(RUSSIAN_CITIZEN_DOC_TYPE) || typeId.equals(FOREIGN_CITIZEN_DOC_TYPE)) {
            errorText.append(" У паспорта c серией: ")
                    .append(series.toString())
                    .append(" и номером: ")
                    .append(no.toString());
            errorDate = (checkDateOfIssue(document.get("dateOfIssue$date"), clientMap.get("dateOfBirth$date")));
        }

        if (typeId.equals(DRIVER_LICENSE_DOC_TYPE)) {
            errorText.append(", у водительского удостоверения c серией: ")
                    .append(series.toString())
                    .append(" и номером: ")
                    .append(no.toString());
            errorDate = checkExpStartDate(document);
        }

        if (!isNullParameter(errorDate)) {
            errorText.insert(0, errorDate);
            errorText.append('.');
            setIncorrectError(errorText.toString());
            result = false;
        }
        return result;
    }

    private String checkExpStartDate(Map<String, Object> document) {
        String result = "";
        if ((document.get("dateOfIssue$date") != null) && (clientMap.get("dateOfBirth$date") != null)) {
            Date dateOfExp = (Date) parseAnyDate(document.get("dateOfIssue$date"), Date.class, "dateOfExp$date");
            Date dateOfBirth = (Date) parseAnyDate(clientMap.get("dateOfBirth$date"), Date.class, "dateOfBirth$date");
            Date settlementDateOfBirth = changeDateToYears(dateOfBirth, MIN_AGE_FOR_DRIVING);

            if (settlementDateOfBirth.after(dateOfExp)) {
                result = "Дата выдачи";
            }
        }
        return result;
    }

    private String getNameRequiredParametersByDocumentType(Map<String, Object> document) {
        String result = REQIRIED_PARAMETERES_RUSSIAN_DOC_NAME;

        Object typeIdObj = document.get("typeId");
        Long typeId = isNullParameter(typeIdObj) ? FOREIGN_CITIZEN_DOC_TYPE : Long.valueOf(typeIdObj.toString());
        if (typeId.equals(RUSSIAN_CITIZEN_DOC_TYPE)) {
            result = REQIRIED_PARAMETERES_RUSSIAN_DOC_NAME;
        }
        if (typeId.equals(FOREIGN_CITIZEN_DOC_TYPE)) {
            result = REQIRIED_PARAMETERES_FOREIGN_DOC_NAME;
        }
        if (typeId.equals(DRIVER_LICENSE_DOC_TYPE)) {
            result = REQIRIED_PARAMETERES_DRIVER_LICENSE_DOC_NAME;
        }
        return result;
    }

    private boolean isPrimaryAtLeastOnePassport(List<Map<String, Object>> list) {
        Long findPassportType = getPassportTypeBy();
        Long itemPassportType;
        Object itemPassportTypeObj;
        for (Map<String, Object> item : list) {
            itemPassportTypeObj = item.get(TYPEID_SYSNAME);
            if (isNullParameter(itemPassportTypeObj) || isNotNullAndDeletedRowStatus(item)) {
                continue;
            }
            itemPassportType = Long.parseLong(itemPassportTypeObj.toString());

            if (findPassportType.equals(itemPassportType)) {
                if (getBooleanByIntegerValue(item.get("isPrimary"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private long getPassportTypeBy() {
        return citizenship.equals(RUSSIAN_CITIZENSHIP) ? RUSSIAN_CITIZEN_DOC_TYPE : FOREIGN_CITIZEN_DOC_TYPE;
    }

    private String getPassportTypeString() {
        return citizenship.equals(RUSSIAN_CITIZENSHIP) ? "Паспорт гражданина РФ" : "Паспорт иностранного гражданина";
    }

    private void checkAddresses() {
        List<Map<String, Object>> addresses = (List<Map<String, Object>>) clientMap.get("addresses");

        if (isNullCollection(addresses) || isAllElementListWithStatusDeleted(addresses)) {
            setStructureError("Список адресов");
            return;
        }

        isPrimaryAtLeastOneAddress(addresses);

        for (Map<String, Object> address : addresses) {
            if (isNotNullAndDeletedRowStatus(address)) {
                continue;
            }

            checkRequiredParameterAddPrefix("OWNER_PAGE_ADDRESS", "Список адресов: ", address);
        }
    }

    private boolean isPrimaryAtLeastOneAddress(List<Map<String, Object>> list) {
        boolean isPrimaryOne = checkIsPrimary(REGISTRATION_ADDRESS_TYPE, list);

        if (!isPrimaryOne) {
            setOtherError("В списке адресов отсутствует хотя бы один адрес регистрации помеченный признаком \"Основной\"");
        }

        // ЛК 2.1: "При сохранении профиля клиента убрать проверку обязательности заполнения для поля «Адрес проживания»"
        //boolean isPrimaryTwo = true;
       /* if (!isPrimaryTwo) {
            setOtherError("В списке адресов отсутствует хотя бы один адрес проживания помеченный признаком \"Основной\"");
        }*/

        return isPrimaryOne;
    }

    private void checkContacts() {
        List<Map<String, Object>> contacts = (List<Map<String, Object>>) clientMap.get("contacts");

        if (isNullCollection(contacts) || isAllElementListWithStatusDeleted(contacts)) {
            setStructureError("Список контактов");
            return;
        }

        isPrimaryAtLeastOneContact(contacts);

        Object itemTypeObj;
        long itemType;
        String value;
        for (Map<String, Object> contact : contacts) {
            if (isNotNullAndDeletedRowStatus(contact)) {
                continue;
            }

            itemTypeObj = contact.get(TYPEID_SYSNAME);
            if (!isNullParameter(itemTypeObj)) {
                itemType = Long.parseLong(itemTypeObj.toString());
                value = contact.get("value").toString();
                if ((itemType == MOBILE_PHONE_TYPE) && (value.length() != 10)) {
                    setIncorrectError("Некорректный формат мобильного телефона");
                }
                if ((itemType == EMAIL_TYPE) && (isValueInvalidByRegExp(value.toLowerCase(), EMAIL_REG_EXP))) {
                    setIncorrectError("Некорректный формат электронной почты");
                }
            }

            checkRequiredParameterAddPrefix("CLIENT_PROFILE_CONTACT", "Список контактов: ", contact);
        }
    }

    private boolean isPrimaryAtLeastOneContact(List<Map<String, Object>> list) {
        boolean isPrimaryOne = checkIsPrimary(MOBILE_PHONE_TYPE, list);
        boolean isPrimaryTwo = checkIsPrimary(EMAIL_TYPE, list);

        if (!isPrimaryOne) {
            setOtherError("В списке контактов отсутствует хотя бы один мобильный телефон помеченный признаком \"Основной\"");
        }
        if (!isPrimaryTwo) {
            setOtherError("В списке контактов отсутствует хотя бы один электронный адрес помеченный признаком \"Основной\"");
        }

        return isPrimaryOne && isPrimaryTwo;
    }

    private boolean checkIsPrimary(Long itemTypeValueFind, List<Map<String, Object>> list) {
        boolean result = false;
        Object itemTypeObj;
        Long itemTypeValue;
        for (Map<String, Object> item : list) {
            itemTypeObj = item.get(TYPEID_SYSNAME);
            if (isNullParameter(itemTypeObj) || isNotNullAndDeletedRowStatus(item)) {
                continue;
            }
            itemTypeValue = Long.parseLong(itemTypeObj.toString());
            if (itemTypeValue.equals(itemTypeValueFind) && getBooleanByIntegerValue(item.get("isPrimary"))) {
                result = true;
                break;
            }
        }
        return result;
    }

    protected void setStructureError(String structureName) {
        setError(ERROR_STRUCTURE_HEADER_CLIENT_PROFILE, structureName);
    }
}
